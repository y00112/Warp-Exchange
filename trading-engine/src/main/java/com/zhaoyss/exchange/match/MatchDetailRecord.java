package com.zhaoyss.exchange.match;

import com.zhaoyss.exchange.model.trade.OrderEntity;

import java.math.BigDecimal;

public record MatchDetailRecord(BigDecimal price, BigDecimal quantity, OrderEntity takerOrder, OrderEntity makerOrder) {

}
