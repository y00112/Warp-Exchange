package com.zhaoyss.exchange.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhaoyss.exchange.bean.OrderBookBean;
import com.zhaoyss.exchange.redis.RedisCache;
import com.zhaoyss.exchange.redis.RedisService;
import com.zhaoyss.exchange.service.HistoryService;
import com.zhaoyss.exchange.service.SendEventService;
import com.zhaoyss.exchange.support.AbstractApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.runtime.ObjectMethods;

/**
 * @author zhaoyss
 * @date 17/3/2025 下午 4:19
 * @description:
 */
@RestController
@RequestMapping("/api")
public class TradingApiController extends AbstractApiController {

    @Autowired
    private SendEventService sendEventService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ObjectMapper objectMapper;

    public TradingApiController(RedisService redisService) {
        super();
        this.redisService = redisService;
    }

    @ResponseBody
    @GetMapping(value = "/orderBook", produces = "application/json")
    public String getOrderBook() {
        String data = redisService.get(RedisCache.Key.ORDER_BOOK);
        return data == null ? OrderBookBean.EMPTY : data;
    }

    @ResponseBody
    @GetMapping(value = "/assets", produces = "application/json")
    public String getAssets() {
        return null;
    }


}
