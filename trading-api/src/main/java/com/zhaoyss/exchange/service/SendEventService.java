package com.zhaoyss.exchange.service;

import com.zhaoyss.exchange.message.event.AbstractEvent;
import com.zhaoyss.exchange.messaging.MessageProducer;
import com.zhaoyss.exchange.messaging.Messaging;
import com.zhaoyss.exchange.messaging.MessagingFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyss
 * @date 18/3/2025 上午 10:19
 * @description: 发送事件服务类
 */
@Component
public class SendEventService {

    @Autowired
    private MessagingFactory messagingFactory;

    private MessageProducer<AbstractEvent> messageProducer;

    @PostConstruct
    public void init() {
        this.messageProducer = messagingFactory.createMessageProducer(Messaging.Topic.SEQUENCE, AbstractEvent.class);
    }

    public void sendMessage(AbstractEvent message) {
        this.messageProducer.sendMessage(message);
    }
}
