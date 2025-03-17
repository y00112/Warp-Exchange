package com.zhaoyss.exchange;

import org.apache.kafka.common.protocol.types.Field;

/**
 * @author zhaoyss
 * @date 12/3/2025 下午 4:50
 * @description: API 异常
 */
public class ApiException extends RuntimeException {

    public final ApiErrorResponse error;

    public ApiException(ApiError error) {
        super(error.toString());
        this.error = new ApiErrorResponse(error, null, "");
    }

    public ApiException(ApiError error, String data) {
        super(error.toString());
        this.error = new ApiErrorResponse(error, data, "");
    }

    public ApiException(ApiError error, String data, String message) {
        super(message);
        this.error = new ApiErrorResponse(error, data, message);
    }

}
