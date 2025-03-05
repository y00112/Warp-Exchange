package com.zhaoyss.exchange.message.event;

import com.zhaoyss.exchange.enums.Direction;

import java.math.BigDecimal;

/**
 * @author zhaoyss
 * @date 28/2/2025 下午 3:37
 * @description: 订单请求事件
 */
public class OrderRequestEvent extends AbstractEvent{

    public Long userId;

    public Direction direction;

    public BigDecimal price;

    public BigDecimal quantity;

    @Override
    public String toString() {
        return "OrderRequestEvent [sequenceId=" + sequenceId + ", previousId=" + previousId + ", uniqueId=" + uniqueId
                + ", refId=" + refId + ", createdAt=" + createdAt + ", userId=" + userId + ", direction=" + direction
                + ", price=" + price + ", quantity=" + quantity + "]";
    }
}
