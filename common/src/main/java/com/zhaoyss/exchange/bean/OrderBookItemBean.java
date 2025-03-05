package com.zhaoyss.exchange.bean;

import java.math.BigDecimal;

/**
 * @author zhaoyss
 * @date 5/3/2025 上午 10:52
 * @description:
 */
public class OrderBookItemBean {

    public BigDecimal price;

    public BigDecimal quantity;

    public OrderBookItemBean(BigDecimal price, BigDecimal quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public void addQuantity(BigDecimal quantity) {
        this.quantity = this.quantity.add(quantity);
    }
}
