package com.zhaoyss.exchange.model.trade;

import com.zhaoyss.exchange.enums.OrderStatus;
import com.zhaoyss.exchange.enums.Direction;
import com.zhaoyss.exchange.model.support.EntitySupport;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Order Entity
 */
@Entity
@Table(name = "orders")
public class OrderEntity implements EntitySupport, Comparable<OrderEntity> {

    // 订单ID / 定序ID / 用户ID
    @Id
    @Column(nullable = false, updatable = false)
    public Long id;
    @Column(nullable = false, updatable = false)
    public long sequenceId;
    @Column(nullable = false, updatable = false)
    public Long userId;

    // 价格 / 方向:买家或者卖家 / 状态
    @Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
    public BigDecimal price;
    @Column(nullable = false, updatable = false, length = VAR_ENUM)
    public Direction direction;
    @Column(nullable = false, updatable = false, length = VAR_ENUM)
    public OrderStatus status;

    // 订单数量 / 未成交数量
    @Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
    public BigDecimal quantity;
    @Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
    public BigDecimal unfilledQuantity;

    // 创建时间和更新时间
    @Column(nullable = false, updatable = false)
    public long createdAt;
    @Column(nullable = false, updatable = false)
    public long updatedAt;

    private int version;

    // 订单交易对
    public Long symbolId;

    public void updateOrder(BigDecimal unfilledQuantity, OrderStatus status, long updateAt) {
        this.version++;
        this.unfilledQuantity = unfilledQuantity;
        this.status = status;
        this.updatedAt = updateAt;
        this.version++;
    }

    @Nullable
    public OrderEntity copy() {
        OrderEntity entity = new OrderEntity();
        int ver = this.version;
        entity.status = this.status;
        entity.unfilledQuantity = this.unfilledQuantity;
        entity.updatedAt = this.updatedAt;
        if (ver != this.version) {
            return null;
        }

        entity.createdAt = this.createdAt;
        entity.direction = this.direction;
        entity.price = this.price;
        entity.sequenceId = this.sequenceId;
        entity.id = this.id;
        entity.quantity = this.quantity;
        entity.userId = this.userId;
        return entity;
    }

    /**
     * 按照OrderId排序
     */
    @Override
    public int compareTo(@NotNull OrderEntity o) {
        return Long.compare(this.id.longValue(), o.id.longValue());
    }
}
