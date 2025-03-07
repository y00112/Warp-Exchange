package com.zhaoyss.exchange.model.quotation;

import com.zhaoyss.exchange.model.support.EntitySupport;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * @author zhaoyss
 * @date 4/3/2025 上午 10:16
 * @description:
 */
@Entity
@Table(name = "tocks", uniqueConstraints = @UniqueConstraint(name = "UNI_T_M", columnNames = {"takerOrderId", "makerOrderId"}),
        indexes = @Index(name = "IDX_CAT", columnList = "createdAt"))
public class TickEntity implements EntitySupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    public long id;

    @Column(nullable = false, updatable = false)
    public long sequenceId;
    @Column(nullable = false, updatable = false)
    public Long takerOrderId;
    @Column(nullable = false, updatable = false)
    public Long makerOrderId;

    /**
     * 1 = LONG 0 = SHORT
     */
    @Column(nullable = false, updatable = false)
    public boolean takerDirection;

    @Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
    public BigDecimal price;
    @Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
    public BigDecimal quantity;
    @Column(nullable = false, updatable = false)
    public long createdAt;

    public String toJson() {
        return "[" + createdAt + "," + (takerDirection ? 1 : 0) + "," + price + "," + quantity + "]";
    }
}
