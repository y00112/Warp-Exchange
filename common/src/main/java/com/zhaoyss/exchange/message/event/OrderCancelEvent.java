package com.zhaoyss.exchange.message.event;

/**
 * @author zhaoyss
 * @date 3/3/2025 下午 5:42
 * @description: 订单取消事件
 */
public class OrderCancelEvent extends AbstractEvent {

    public Long userId;

    public Long refOrderId;

    @Override
    public String toString() {
        return "OrderCancelEvent [sequenceId =" + sequenceId + ", previousId=" + previousId + ", uniqueId=" + uniqueId
                + ", refId=" + refId + ", createdAt=" + createdAt + ", userId=" + userId + ", refOrderId=" + refOrderId + "]";
    }
}
