package com.zhaoyss.exchange.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhaoyss.exchange.util.JsonUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhaoyss
 * @date 4/3/2025 下午 5:59
 * @description:
 */
public class OrderBookBean {

    public static final String EMPTY = JsonUtil.writeJson(new OrderBookBean(0, BigDecimal.ZERO, List.of(), List.of()));

    @JsonIgnore
    public long sequenceId;

    public BigDecimal price;

    public List<OrderBookItemBean> bug;

    public List<OrderBookItemBean> sell;

    public OrderBookBean(long sequenceId, BigDecimal price, List<OrderBookItemBean> buy, List<OrderBookItemBean> sell) {
        this.sequenceId = sequenceId;
        this.price = price;
        this.bug = buy;
        this.sell = sell;
    }
}
