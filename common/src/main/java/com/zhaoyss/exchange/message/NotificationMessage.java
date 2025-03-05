package com.zhaoyss.exchange.message;

/**
 * @author zhaoyss
 * @date 4/3/2025 上午 9:48
 * @description: 通知消息
 */
public class NotificationMessage extends AbstractMessage {

    public String type;

    public Long userId;

    public Object data;
}
