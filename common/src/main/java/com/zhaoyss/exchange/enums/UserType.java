package com.zhaoyss.exchange.enums;

/**
 * @author zhaoyss
 * @date 7/3/2025 下午 1:58
 * @description:
 */
public enum UserType {

    DEBT(1),

    TRADER(0);

    private final long userId;

    public long getInternalUserId() {
        return this.userId;
    }

    UserType(long userId) {
        this.userId = userId;
    }
}
