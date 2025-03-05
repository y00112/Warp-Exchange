package com.zhaoyss.exchange;

import com.zhaoyss.exchange.enums.ApiError;

/**
 * @author zhaoyss
 * @date 4/3/2025 上午 9:11
 * @description: API 错误响应
 */
public record ApiErrorResponse(ApiError error, String data, String message) {
}
