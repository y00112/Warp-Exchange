package com.zhaoyss.exchange.message;

import java.io.Serializable;

/**
 * @author zhaoyss
 * @date 28/2/2025 下午 3:22
 * @description: extends 基本消息
 */
public class AbstractMessage implements Serializable {

    /**
     * 引用id，默认：null
     */
    public String refId = null;

    /**
     * 消息创建于
     */
    public long createdAt;
}
