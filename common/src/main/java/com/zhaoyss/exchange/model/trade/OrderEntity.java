package com.zhaoyss.exchange.model.trade;

import com.zhaoyss.exchange.enums.OrderStatus;

import java.math.BigDecimal;

public class OrderEntity {

    // 订单ID / 定序ID / 用户ID
    public Long id;
    public long sequenceId;
    public Long userId;

    // 价格 / 方向:买家或者卖家 / 状态
    public BigDecimal price;
    public Direction direction;
    public OrderStatus status;

    // 订单数量 / 未成交数量
    public BigDecimal quantity;
    public BigDecimal unfilledQuantity;

    // 创建时间和更新时间
    public long createdAt;
    public long updatedAt;

    private int version;

    // 订单交易对
    public Long symbolId;

    public void updateOrder(BigDecimal unfilledQuantity,OrderStatus status,long updateAt){
         this.version++;
         this.unfilledQuantity = unfilledQuantity;
         this.status = status;
         this.updatedAt = updateAt;
         this.version++;
    }
}
