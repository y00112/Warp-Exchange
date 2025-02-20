package com.zhaoyss.exchange.match;

import com.zhaoyss.exchange.model.trade.Direction;
import com.zhaoyss.exchange.model.trade.OrderEntity;
import jakarta.persistence.criteria.Order;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * 买卖盘
 */
public class OrderBook {

    public final Direction direction;  // 方向：买家或者卖家

    public final TreeMap<OrderKey, OrderEntity> book;  // 排序树

    public OrderBook(Direction direction) {
        this.direction = direction;
        this.book = new TreeMap<>(direction == Direction.BUY ? SORT_BUY : SORT_SELL);
    }

    private static final Comparator<OrderKey> SORT_SELL = new Comparator<OrderKey>() {
        @Override
        public int compare(OrderKey o1, OrderKey o2) {
            // 价格低在前：
            int cmp = o1.price().compareTo(o2.price());
            // 时间早在前：
            return cmp == 0 ? Long.compare(o1.sequenceId(), o2.sequenceId()) : cmp;
        }
    };

    private static final Comparator<OrderKey> SORT_BUY = new Comparator<OrderKey>() {

        @Override
        public int compare(OrderKey o1, OrderKey o2) {
            // 价格高在前：
            int cmp = o2.price().compareTo(o1.price());
            // 时间早在前：
            return cmp == 0 ? Long.compare(o1.sequenceId(), o2.sequenceId()) : cmp;
        }
    };

    public OrderEntity getFirst() {
        return this.book.isEmpty() ? null : this.book.firstEntry().getValue();
    }

    public boolean remove(OrderEntity order) {
        return this.book.remove(new OrderKey(order.sequenceId,order.price)) != null;
    }

    public boolean add(OrderEntity order) {
        return this.book.put(new OrderKey(order.sequenceId,order.price), order) != null;
    }
}
