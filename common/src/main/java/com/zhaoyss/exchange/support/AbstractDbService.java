package com.zhaoyss.exchange.support;

import com.zhaoyss.exchange.mapper.GenericDbService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhaoyss
 * @date 11/3/2025 下午 3:23
 * @description:
 */
public abstract class AbstractDbService extends LoggerSupport {

    @Autowired
    protected GenericDbService genericDbService;

}
