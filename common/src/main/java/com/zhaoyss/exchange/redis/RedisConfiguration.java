package com.zhaoyss.exchange.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaoyss
 * @date 5/3/2025 下午 3:54
 * @description:
 */
@Data
@Configuration
@ConfigurationProperties("spring.redis")
public class RedisConfiguration {

    private String host;

    private int port;

    private String password;

    private int database;

}
