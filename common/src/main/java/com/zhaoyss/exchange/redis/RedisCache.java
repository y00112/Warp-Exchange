package com.zhaoyss.exchange.redis;

import org.apache.kafka.common.protocol.types.Field;

/**
 * @author zhaoyss
 * @date 5/3/2025 下午 4:28
 * @description:
 */
public class RedisCache {

    public interface Topic {
        String TRADING_API_RESULT = "trading_api_result";
        String NOTIFICATION = "notification";
    }

    public interface Key {
        String ORDER_BOOK = "_order_book_";
        String RECENT_TICKS = "__ticks__";
        String DAY_BARS = "_day_bars_";
        String HOUR_BARS = "_hour_bars_";
        String MIN_BARS = "_min_bars_";
        String SEC_BARS = "_sec_bars_";

    }
}
