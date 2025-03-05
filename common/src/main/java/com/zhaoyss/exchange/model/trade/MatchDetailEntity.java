package com.zhaoyss.exchange.model.trade;

import com.zhaoyss.exchange.enums.Direction;
import com.zhaoyss.exchange.enums.MatchType;
import com.zhaoyss.exchange.model.support.EntitySupport;

import java.math.BigDecimal;

/**
 * @author zhaoyss
 * @date 4/3/2025 上午 9:54
 * @description: 存储每个订单的只读匹配详细信息
 */
public class MatchDetailEntity implements EntitySupport, Comparable<MatchDetailEntity> {

    public long id;

    public long sequenceId;

    public Long orderId;

    public Long counterOrderId;

    public Long userId;

    public Long counterUserId;

    public MatchType type;

    public Direction direction;

    public BigDecimal price;

    public BigDecimal quantity;

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
