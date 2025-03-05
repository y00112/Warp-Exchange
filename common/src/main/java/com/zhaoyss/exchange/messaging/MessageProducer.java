package com.zhaoyss.exchange.messaging;

import com.zhaoyss.exchange.message.AbstractMessage;

import java.util.List;

/**
 * @author zhaoyss
 * @date 5/3/2025 下午 4:58
 * @description:
 */
@FunctionalInterface
public interface MessageProducer<T extends AbstractMessage> {

    void sendMessage(T message);

    default void sendMessage(List<T> messages) {
        for (T message : messages) {
            sendMessage(message);
        }
    }
}
