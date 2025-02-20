package com.zhaoyss.exchange.match;

import com.zhaoyss.exchange.model.trade.Direction;
import com.zhaoyss.exchange.model.trade.OrderEntity;

import java.math.BigDecimal;

/**
 * 撮合引擎
 */
public class MatchEngine {

    public final OrderBook buyBook = new OrderBook(Direction.BUY);

    public final OrderBook sellBook = new OrderBook(Direction.SELL);

    // 注意：在Java中比较两个BigDecimal的值只能使用compareTo()，不能使用equals()！
    public BigDecimal marketPrice = BigDecimal.ZERO; // 最新市场价

    public long sequenceId; // 上次处理的 Sequence ID

    /**
     * @param sequenceId
     * @param order
     * @return
     */
    public MatchResult processOrder(long sequenceId, OrderEntity order) {
        switch (order.direction) {
            case BUY:
                // 买单与sellBook匹配，最后放入bugBook:
                return processOrder(order, this.sellBook, this.buyBook);
            case SELL:
                // 卖单与buyBook匹配，最后放入sellBook:
                return processOrder(order, this.buyBook, this.sellBook);
            default:
                throw new IllegalArgumentException("Invalid direction.");
        }
    }

    // TODO: 待完成
    private MatchResult processOrder(OrderEntity order, OrderBook buyBook, OrderBook sellBook) {
        return null;
    }

    /**
     * TODO: 待完成
     * Maker（挂单）：已经挂在买卖盘的订单
     * Taker（吃单）：当前正在处理的订单
     */
    MatchResult processOrder(long sequenceId, OrderEntity takerOrder, OrderBook markerBook, OrderBook anotherBook) {
        this.sequenceId = sequenceId;
        long ts = takerOrder.createdAt;
        MatchResult matchResult = new MatchResult(takerOrder);

        return null;
    }
}
