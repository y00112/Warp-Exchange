package com.zhaoyss.exchange.match;

import com.zhaoyss.exchange.model.trade.OrderEntity;

import java.util.HashMap;
import java.util.Map;

public class MatchEngineGroup {
    Map<Long, MatchEngine> engines = new HashMap<>();

    public MatchResult processOrder(long sequenceId, OrderEntity order) {
        // 获得订单的交易的ID：
        Long symbolId = order.symbolId;
        // 查找交易对所对应的引擎示例：
        MatchEngine engine = engines.get(symbolId);
        if (engine == null) {
            // 该交易的第一个订单：
            engine = new MatchEngine();
            engines.put(symbolId, engine);
        }
        // 由实例处理订单：
        return engine.processOrder(sequenceId, order);
    }
}
