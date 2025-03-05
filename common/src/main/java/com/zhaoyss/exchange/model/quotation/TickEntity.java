package com.zhaoyss.exchange.model.quotation;

import com.zhaoyss.exchange.model.support.EntitySupport;

import java.math.BigDecimal;

/**
 * @author zhaoyss
 * @date 4/3/2025 上午 10:16
 * @description:
 */
public class TickEntity implements EntitySupport {

    public long id;

    public long sequenceId;

    public Long takerOrderId;

    public Long makerOrderId;

    public boolean takerDirection;

    public BigDecimal price;

    public BigDecimal quantity;

    public long createdAt;

    public String toJson() {
        return "[" + createdAt + "," + (takerDirection ? 1 : 0) + "," + price + "," + quantity + "]";
    }}
