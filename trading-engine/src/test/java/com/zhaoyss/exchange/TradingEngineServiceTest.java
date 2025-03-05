package com.zhaoyss.exchange;

import com.zhaoyss.exchange.assets.AssetService;
import com.zhaoyss.exchange.clearing.ClearingService;
import com.zhaoyss.exchange.match.MatchEngine;
import com.zhaoyss.exchange.order.OrderService;
import com.zhaoyss.exchange.trading.TradingEngineService;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

/**
 * @author zhaoyss
 * @date 5/3/2025 下午 3:34
 * @description:
 */
public class TradingEngineServiceTest {

    static final Long USER_A = 11111L;
    static final Long USER_B = 22222L;
    static final Long USER_C = 33333L;
    static final Long USER_D = 44444L;
    static final Long USER_E = 55555L;

    static final Long[] USERS = {USER_A, USER_B, USER_C, USER_D, USER_E};

    @Test
    public void testTradingEngine(){

        var engine = createTradingEngine();
    }

    TradingEngineService createTradingEngine() {
        var matchEngine = new MatchEngine();
        var assetService = new AssetService();
        var orderService = new OrderService(assetService);
        var clearingService = new ClearingService(assetService,orderService);
        var engine = new TradingEngineService();
        engine.assetService = assetService;
    }
}
