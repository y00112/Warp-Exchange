package com.zhaoyss.exchange.model.trade;

import com.zhaoyss.exchange.enums.Direction;
import com.zhaoyss.exchange.enums.MatchType;
import com.zhaoyss.exchange.model.support.EntitySupport;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhaoyss
 * @date 4/3/2025 上午 9:54
 * @description: 存储每个订单的只读匹配详细信息
 */
@Data
@Entity
@Table(name = "match_details", uniqueConstraints = @UniqueConstraint(name = "UNI_OID_COID", columnNames = {"orderId", "counterOrderId"})
        , indexes = @Index(name = "IDX_OID_CT", columnList = "orderId,createAt"))
public class MatchDetailEntity implements EntitySupport, Comparable<MatchDetailEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    public long id;

    @Column(nullable = false, updatable = false)
    public long sequenceId;

    @Column(nullable = false, updatable = false)
    public Long orderId;

    @Column(nullable = false, updatable = false)
    public Long counterOrderId;

    @Column(nullable = false, updatable = false)
    public Long userId;

    @Column(nullable = false, updatable = false)
    public Long counterUserId;

    @Column(nullable = false, updatable = false, length = VAR_ENUM)
    public MatchType type;

    @Column(nullable = false, updatable = false, length = VAR_ENUM)
    public Direction direction;

    @Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
    public BigDecimal price;

    @Column(nullable = false, updatable = false, precision = PRECISION, scale = SCALE)
    public BigDecimal quantity;

    @Column(nullable = false, updatable = false)
    public long createdAt;

    /**
     * 按照 orderId,CounterOrderId 排序
     */
    @Override
    public int compareTo(final MatchDetailEntity o) {
        int cmp = Long.compare(this.orderId.longValue(), o.orderId.longValue());
        if (cmp == 0) {
            cmp = Long.compare(this.counterOrderId.longValue(), o.counterOrderId.longValue());
        }
        return cmp;
    }

}
