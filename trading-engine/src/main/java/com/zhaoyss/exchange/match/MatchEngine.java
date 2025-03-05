package com.zhaoyss.exchange.match;

import com.zhaoyss.exchange.bean.OrderBookBean;
import com.zhaoyss.exchange.enums.OrderStatus;
import com.zhaoyss.exchange.enums.Direction;
import com.zhaoyss.exchange.model.trade.OrderEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 撮合引擎
 */
@Component
public class MatchEngine {

    // 买盘
    public final OrderBook buyBook = new OrderBook(Direction.BUY);

    // 卖盘
    public final OrderBook sellBook = new OrderBook(Direction.SELL);

    // 注意：在Java中比较两个BigDecimal的值只能使用compareTo()，不能使用equals()！
    // 最新成交价
    public BigDecimal marketPrice = BigDecimal.ZERO;

    public long sequenceId; // 上次处理的 Sequence ID

    public MatchResult processOrder(long sequenceId, OrderEntity order) {
        switch (order.direction) {
            case BUY:
                // 买单与sellBook匹配，最后放入bugBook:
                return processOrder(sequenceId, order, this.sellBook, this.buyBook);
            case SELL:
                // 卖单与buyBook匹配，最后放入sellBook:
                return processOrder(sequenceId, order, this.buyBook, this.sellBook);
            default:
                throw new IllegalArgumentException("Invalid direction.");
        }
    }

    /**
     * @param sequenceId
     * @param takerOrder  输入订单
     * @param makerBook   尝试匹配成交的 OrderBook
     * @param anotherBook 未能完全匹配成交后挂单的 OrderBook
     * @return
     */
    MatchResult processOrder(long sequenceId, OrderEntity takerOrder, OrderBook makerBook, OrderBook anotherBook) {
        this.sequenceId = sequenceId;
        long ts = takerOrder.createdAt;
        MatchResult matchResult = new MatchResult(takerOrder);
        BigDecimal takerUnfilledQuantity = takerOrder.quantity;
        for (; ; ) {
            OrderEntity makerOrder = makerBook.getFirst();
            if (makerOrder == null) {
                // 对手盘不存在
                break;
            }
            if (takerOrder.direction == Direction.BUY && takerOrder.price.compareTo(makerOrder.price) < 0) {
                // 买入订单价格比卖盘第一档价格低：
                break;
            } else if (takerOrder.direction == Direction.SELL && takerOrder.price.compareTo(makerOrder.price) > 0) {
                // 卖出订单价格比买盘第一档价格高：
                break;
            }
            // 成交价
            this.marketPrice = makerOrder.price;
            // 待成交数量：为两者较小值
            BigDecimal matchedQuantity = takerUnfilledQuantity.min(makerOrder.unfilledQuantity);
            // 成交记录:
            matchResult.add(makerOrder.price, matchedQuantity, makerOrder);
            // 更新成交后的订单数量：
            takerUnfilledQuantity = takerUnfilledQuantity.subtract(matchedQuantity);
            BigDecimal makerUnfilledQuantity = makerOrder.unfilledQuantity.subtract(matchedQuantity);
            // 对手盘完全成交后，从订单溥中删除
            if (makerUnfilledQuantity.signum() == 0) {
                makerOrder.updateOrder(makerUnfilledQuantity, OrderStatus.FULLY_FILLED, ts);
                makerBook.remove(makerOrder);
            } else {
                // 对手盘部分成交
                makerOrder.updateOrder(makerUnfilledQuantity, OrderStatus.PARTIAL_FILLED, ts);
            }
            /// Taker 订单完全成交后，退出循环
            if (takerUnfilledQuantity.signum() == 0) {
                takerOrder.updateOrder(takerUnfilledQuantity, OrderStatus.FULLY_FILLED, ts);
                break;
            }
        }
        // Taker 订单未完全成交时，放入订单谱
        if (takerUnfilledQuantity.signum() > 0) {
            takerOrder.updateOrder(takerUnfilledQuantity, takerUnfilledQuantity.compareTo(takerOrder.quantity) == 0 ? OrderStatus.PENDING : OrderStatus.PARTIAL_FILLED, ts);
            anotherBook.add(takerOrder);
        }
        return matchResult;
    }

    public OrderBookBean getOrderBook(int maxDepth) {
        return new OrderBookBean(this.sequenceId, this.marketPrice, this.buyBook.getOrderBook(maxDepth), this.sellBook.getOrderBook(maxDepth));
    }

    public void cancel(long ts, OrderEntity order) {
        OrderBook book = order.direction == Direction.BUY ? this.buyBook : this.sellBook;
        if (!book.remove(order)) {
            throw new IllegalArgumentException("Order not found in order book.");
        }
        OrderStatus status = order.unfilledQuantity.compareTo(order.quantity) == 0 ? OrderStatus.FULLY_CANCELLED : OrderStatus.PARTIAL_CANCELLED;
        order.updateOrder(order.unfilledQuantity, status, ts);
    }
}
