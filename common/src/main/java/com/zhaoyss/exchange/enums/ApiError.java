package com.zhaoyss.exchange.enums;

/**
 * @author zhaoyss
 * @date 4/3/2025 上午 9:12
 * @description: API 错误枚举
 */
public enum ApiError {

    // 参数无效
    PARAMETER_INVALID,

    // 需要 AUTH 登录
    AUTH_SIGNIN_REQUIRED,

    // 身份验证登录失败
    AUTH_SIGNIN_FAILED,

    // 用户无法登录
    USER_CANNOT_SIGNIN,

    // 没有足够的资产
    NO_ENOUGH_ASSET,

    // 订单未找到
    ORDER_NOT_FOUND,

    // 操作超时
    OPERATION_TIMEOUT,

    // 内部服务器错误
    INTERNAL_SERVER_ERROR,

}
