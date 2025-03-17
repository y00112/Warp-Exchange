package com.zhaoyss.exchange.support;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;

/**
 * @author zhaoyss
 * @date 12/3/2025 下午 4:24
 * @description:
 */
public abstract class AbstractFilter extends LoggerSupport implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("init filter: {}...", getClass().getName());
    }

    @Override
    public void destroy() {
        logger.info("destroy filter: {}...", getClass().getName());
    }
}
