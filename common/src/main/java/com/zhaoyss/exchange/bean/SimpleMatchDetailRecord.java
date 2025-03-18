package com.zhaoyss.exchange.bean;

import com.zhaoyss.exchange.enums.MatchType;

import java.math.BigDecimal;

/**
 * @author zhaoyss
 * @date 18/3/2025 上午 11:18
 * @description:
 */
public record SimpleMatchDetailRecord(BigDecimal price, BigDecimal quantity, MatchType type) {
}
