package com.zhaoyss.exchange.ctx;

import com.zhaoyss.exchange.ApiError;
import com.zhaoyss.exchange.ApiException;

/**
 * @author zhaoyss
 * @date 14/3/2025 下午 1:06
 * @description: 将用户上下午保存在线程本地中。
 */
public class UserContext implements AutoCloseable {

    static final ThreadLocal<Long> THREAD_LOCAL_CTX = new ThreadLocal<>();

    /**
     * 获取当前用户ID，如果没有用户，则抛出异常。
     *
     * @return
     */
    public static Long getRequiredUserId() {
        Long userId = getUserId();
        if (userId == null) {
            throw new ApiException(ApiError.AUTH_SIGNIN_REQUIRED, null, "Need signin first.");
        }
        return userId;
    }

    public UserContext(Long userId) {
        THREAD_LOCAL_CTX.set(userId);
    }

    @Override
    public void close() throws Exception {
        THREAD_LOCAL_CTX.remove();
    }

    public static Long getUserId() {
        return THREAD_LOCAL_CTX.get();
    }

}
