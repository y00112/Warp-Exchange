package com.zhaoyss.exchange.model.support;

/**
 * @author zhaoyss
 * @date 4/3/2025 上午 10:08
 * @description: 实体支持
 */
public interface EntitySupport {

    /**
     * 默认 big decimal storage type: DECIMAL(PRECISION, SCALE)
     * <p>
     * Range = +/-999999999999999999.999999999999999999
     */
    int PRECISION = 36;

    /**
     * Default big decimal storage scale. Minimum is 0.000000000000000001.
     */
    int SCALE = 18;

    int VAR_ENUM = 32;

    int VAR_CHAR_50 = 50;
    int VAR_CHAR_100 = 100;
    int VAR_CHAR_200 = 200;
    int VAR_CHAR_1000 = 1000;
    int VAR_CHAR_10000 = 10000;

}
