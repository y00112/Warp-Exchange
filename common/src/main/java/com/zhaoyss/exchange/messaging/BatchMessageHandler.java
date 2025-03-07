package com.zhaoyss.exchange.messaging;

import com.zhaoyss.exchange.message.AbstractMessage;

import java.util.List;

/**
 * @author zhaoyss
 * @date 6/3/2025 上午 9:27
 * @description:
 */
@FunctionalInterface
public interface BatchMessageHandler<T extends AbstractMessage> {

    void processMessage(List<T> messages);

}
