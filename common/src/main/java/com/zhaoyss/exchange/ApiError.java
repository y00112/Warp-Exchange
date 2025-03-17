package com.zhaoyss.exchange;

/**
 * @author zhaoyss
 * @date 12/3/2025 下午 4:43
 * @description: API 错误常量
 */
public enum ApiError {
    // 参数无效
    PARAMETER_INVALID,

    AUTH_SIGNIN_REQUIRED,

    AUTH_SIGIN_FAILED,

    USER_CANNOT_SIGNIN,

    NO_ENOUGH_ASSET,

    ORDER_NOT_FOUND,

    OPERATION_TIMEOUT,

    INTERNAL_SERVER_ERROR;
}
