package com.zhaoyss.exchange.trading;

import com.zhaoyss.exchange.assets.Asset;
import com.zhaoyss.exchange.assets.AssetService;
import com.zhaoyss.exchange.assets.Transfer;
import com.zhaoyss.exchange.bean.OrderBookBean;
import com.zhaoyss.exchange.clearing.ClearingService;
import com.zhaoyss.exchange.enums.AssetEnum;
import com.zhaoyss.exchange.enums.Direction;
import com.zhaoyss.exchange.enums.MatchType;
import com.zhaoyss.exchange.match.MatchDetailRecord;
import com.zhaoyss.exchange.match.MatchEngine;
import com.zhaoyss.exchange.match.MatchResult;
import com.zhaoyss.exchange.message.ApiResultMessage;
import com.zhaoyss.exchange.message.NotificationMessage;
import com.zhaoyss.exchange.message.TickMessage;
import com.zhaoyss.exchange.message.event.AbstractEvent;
import com.zhaoyss.exchange.message.event.OrderCancelEvent;
import com.zhaoyss.exchange.message.event.OrderRequestEvent;
import com.zhaoyss.exchange.message.event.TransferEvent;
import com.zhaoyss.exchange.messaging.MessageConsumer;
import com.zhaoyss.exchange.messaging.MessagingFactory;
import com.zhaoyss.exchange.model.quotation.TickEntity;
import com.zhaoyss.exchange.model.trade.MatchDetailEntity;
import com.zhaoyss.exchange.model.trade.OrderEntity;
import com.zhaoyss.exchange.order.OrderService;
import com.zhaoyss.exchange.redis.RedisService;
import com.zhaoyss.exchange.store.StoreService;
import com.zhaoyss.exchange.support.LoggerSupport;
import io.netty.util.concurrent.PromiseCombiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.util.Loggers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhaoyss
 * @date 28/2/2025 下午 3:13
 * @description: 交易引擎
 */
@Component
public class TradingEngineService extends LoggerSupport {

    @Value("#{exchangeConfiguration.orderBookDepth}")
    int orderBookDepth = 100;

    @Value("#{exchagneConfiguration.debugMode}")
    boolean debugMode = false;

    @Autowired
    AssetService assetService;

    @Autowired
    OrderService orderService;

    @Autowired
    MatchEngine matchEngine;

    @Autowired
    ClearingService clearingService;

    @Autowired
    StoreService storeService;

    @Autowired(required = false)
    ZoneId zoneId = ZoneId.systemDefault();

    @Autowired
    RedisService redisService;

    @Autowired
    MessagingFactory messagingFactory;

    boolean fatalError = false;

    boolean fatalWarning = false;

    private String shaUpdateOrderBookLua;

    private long lastSequenceId = 0;

    private MessageConsumer consumer;

    private Thread tickThread;
    private Thread notifyThread;
    private Thread apiResultThread;
    private Thread orderBookThread;
    private Thread dbThread;

    private Queue<List<OrderEntity>> orderQueue = new ConcurrentLinkedQueue<>();
    private Queue<ApiResultMessage> apiResultQueue = new ConcurrentLinkedQueue<>();
    private Queue<List<MatchDetailEntity>> matchQueue = new ConcurrentLinkedQueue<>();
    private Queue<TickMessage> tickQueue = new ConcurrentLinkedQueue<>();
    private Queue<NotificationMessage> notificationQueue = new ConcurrentLinkedQueue<>();

    private boolean orderBookChanged = false;

    private OrderBookBean latestOrderBook = null;

    // TODO:
    public void init() {
        this.shaUpdateOrderBookLua = this.redisService.loadScriptFromClassPath("/redis/update-orderbook.lua");
    }

    void processMessages(List<AbstractEvent> messages) {
        this.orderBookChanged = false;
        for (AbstractEvent message : messages) {
            processEvent(message);
        }
        if (this.orderBookChanged) {
            // 获取最新的OrderBook快照
            this.latestOrderBook = this.matchEngine.getOrderBook(this.orderBookDepth);
        }
    }

    void processEvent(AbstractEvent event) {
        if (this.fatalError) {
            return;
        }
        if (event.sequenceId <= this.lastSequenceId) {
            logger.warn("skip duplicate event: {}", event);
            return;
        }
        // 判断消息是否丢失：
        if (event.previousId > this.lastSequenceId) {
            logger.warn("event lost: excepted previous id {} but actual {} for event {}", this.lastSequenceId, event.previousId, event);
            List<AbstractEvent> events = this.storeService.loadEventsFromDb(this.lastSequenceId);
            if (events.isEmpty()) {
                logger.error("cannot load events from db.");
                panic();
                return;
            }
            // 处理丢失的消息：
            for (AbstractEvent e : events) {
                this.processEvent(e);
            }
            return;
        }
        // 判断当前消息是否指向上一条消息：
        if (event.previousId != lastSequenceId) {
            logger.error("bad event: expected previous id {} but actual {} for event: {}", this.lastSequenceId, event.previousId, event);
            panic();
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("process event {} -> {}: {}...", this.lastSequenceId, event.previousId, event);
        }
        try {
            if (event instanceof OrderRequestEvent) {
                createOrder((OrderRequestEvent) event);
            } else if (event instanceof OrderCancelEvent) {
                cancelOrder((OrderCancelEvent) event);
            } else if (event instanceof TransferEvent) {
                transfer((TransferEvent) event);
            } else {
                logger.error("unable to process event type: {}", event.getClass().getName());
                panic();
                return;
            }
            if (event.sequenceId <= this.lastSequenceId) {
                logger.warn("skip duplicate event: {}", event);
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.lastSequenceId = event.sequenceId;
        if (logger.isDebugEnabled()) {
            logger.debug("set last processed sequence id: {}...", this.lastSequenceId);
        }
        if (debugMode) {
            this.validate();
            this.debug();
        }

    }

    // TODO：
    public void debug() {
        System.out.println("========= trading engine =========");

        System.out.println("========= // trading engine =========");

    }

    // TODO:
    void validate() {
        logger.debug("start validate...");
        validateAssets();
//        validateOrders();
//        validateMatchEngine();
        logger.debug("end validate...");
    }

    // TODO:
    private void validateAssets() {
        // 验证系统资产完整性：
        BigDecimal totalUSD = BigDecimal.ZERO;
        BigDecimal totalBTC = BigDecimal.ZERO;
        this.assetService.getUserAssets().entrySet();
        for (Map.Entry<Long, ConcurrentMap<AssetEnum, Asset>> userEntry : this.assetService.getUserAssets().entrySet()) {
            return;
        }
    }

    boolean transfer(TransferEvent event) {
        boolean ok = this.assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, event.fromUserId, event.toUserId,
                event.asset, event.amount, event.sufficient);
        return ok;
    }

    private void cancelOrder(OrderCancelEvent event) {
        OrderEntity order = this.orderService.getOrder(event.refOrderId);
        // 未找到活动订单或者订单不属于该用户：
        if (order == null || order.userId.longValue() != event.userId.longValue()) {
            // 发送失败消息：
            this.apiResultQueue.add(ApiResultMessage.cancelOrderFailed(event.refId, event.createdAt));
            return;
        }
        this.matchEngine.cancel(event.createdAt, order);
        this.clearingService.clearCancelOrder(order);
        this.orderBookChanged = true;
        // 发送消息成功
        this.apiResultQueue.add(ApiResultMessage.orderSuccess(event.refId, order, event.createdAt));
        this.notificationQueue.add(createNotification(event.createdAt, "order_canceled", order.userId, order));
    }

    private void panic() {
        logger.error("application panic. exit now...");
        this.fatalError = true;
        System.exit(1);
    }

    void createOrder(OrderRequestEvent event) {
        // 生成Order ID:
        ZonedDateTime zdt = Instant.ofEpochMilli(event.createdAt).atZone(zoneId);
        int year = zdt.getYear();
        int month = zdt.getMonth().getValue();
        long orderId = event.sequenceId * 10000 + (year * 100 + month);
        // 创建Order:
        OrderEntity order = orderService.createOrder(event.sequenceId, event.createdAt, orderId, event.userId, event.direction, event.price, event.quantity);
        if (order == null) {
            logger.warn("create order failed.");
            // 推送失败结果
            this.apiResultQueue.add(ApiResultMessage.createOrderFailed(event.refId, event.createdAt));
            return;
        }
        // 撮合：
        MatchResult result = matchEngine.processOrder(event.sequenceId, order);
        // 清算：
        clearingService.clearMatchResult(result);
        // 推送成功结果,注意必须复制一份OrderEntity，因为将异步序列化：
        this.apiResultQueue.add(ApiResultMessage.orderSuccess(event.refId, order.copy(), event.createdAt));
        this.orderBookChanged = true;
        // 收集 Notification：
        List<NotificationMessage> notifications = new ArrayList<>();
        notifications.add(createNotification(event.createdAt, "order_matched", order.userId, order.copy()));
        // 清算完成后， 收集已完成的OrderEntity并生成MatchDetailEntity, TickEntity:：
        if (!result.matchDetails.isEmpty()) {
            List<OrderEntity> closedOrders = new ArrayList<>();
            List<MatchDetailEntity> matchDetails = new ArrayList<>();
            List<TickEntity> ticks = new ArrayList<>();
            if (result.takerOrder.status.isFinalStatus) {
                closedOrders.add(result.takerOrder);
            }
            for (MatchDetailRecord detail : result.matchDetails) {
                OrderEntity maker = detail.makerOrder();
                notifications.add(createNotification(event.createdAt, "order_matched", maker.userId, maker.copy()));
                if (maker.status.isFinalStatus) {
                    closedOrders.add(maker);
                }
                MatchDetailEntity takerDetail = generateMatchDetailEntity(event.sequenceId, event.createdAt, detail, true);
                MatchDetailEntity makerDetail = generateMatchDetailEntity(event.sequenceId, event.createdAt, detail, false);
                matchDetails.add(takerDetail);
                matchDetails.add(makerDetail);
                TickEntity tick = new TickEntity();
                tick.sequenceId = event.sequenceId;
                tick.takerOrderId = detail.takerOrder().id;
                tick.makerOrderId = detail.makerOrder().id;
                tick.price = detail.price();
                tick.quantity = detail.quantity();
                tick.takerDirection = detail.takerOrder().direction == Direction.BUY;
                tick.createdAt = event.createdAt;
                ticks.add(tick);
            }
            // 异步写入数据库
            this.orderQueue.add(closedOrders);
            this.matchQueue.add(matchDetails);
            // 异步发送Tick消息
            TickMessage msg = new TickMessage();
            msg.sequenceId = event.sequenceId;
            msg.createdAt = event.createdAt;
            msg.ticks = ticks;
            this.tickQueue.add(msg);
            // 异步通知OrderMatch:
            this.notificationQueue.addAll(notifications);
        }
    }

    private MatchDetailEntity generateMatchDetailEntity(long sequenceId, long timestamp, MatchDetailRecord detail, boolean forTaker) {
        MatchDetailEntity d = new MatchDetailEntity();
        d.sequenceId = sequenceId;
        d.orderId = forTaker ? detail.takerOrder().id : detail.makerOrder().id;
        d.counterOrderId = forTaker ? detail.makerOrder().id : detail.takerOrder().id;
        d.direction = forTaker ? detail.takerOrder().direction : detail.makerOrder().direction;
        d.price = detail.price();
        d.quantity = detail.quantity();
        d.type = forTaker ? MatchType.TAKER : MatchType.MAKER;
        d.userId = forTaker ? detail.takerOrder().userId : detail.makerOrder().userId;
        d.counterUserId = forTaker ? detail.makerOrder().userId : detail.takerOrder().userId;
        d.createdAt = timestamp;
        return d;
    }

    private NotificationMessage createNotification(long ts, String type, Long userId, OrderEntity data) {
        NotificationMessage msg = new NotificationMessage();
        msg.type = type;
        msg.userId = userId;
        msg.createdAt = ts;
        msg.data = data;
        return msg;
    }

    // 启动一个线程将 orderQueue 的 Order 异步写入数据库：
    void saveOrders() {

    }
}
