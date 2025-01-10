package com.zhaoyss.exchange.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerSupport {

    /**
     * 子类可以直接使用 logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());
}
