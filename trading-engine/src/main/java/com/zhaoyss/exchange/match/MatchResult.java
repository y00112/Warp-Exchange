package com.zhaoyss.exchange.match;

import com.zhaoyss.exchange.model.trade.OrderEntity;

import java.util.ArrayList;
import java.util.List;

public class MatchResult {

    public final OrderEntity takerOrder;

    public final List<MatchDetailRecord> MatchDetails = new ArrayList<>();

    public MatchResult(final OrderEntity takerOrder) {
        this.takerOrder = takerOrder;
    }
}
