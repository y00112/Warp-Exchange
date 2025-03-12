package com.zhaoyss.exchange.sequencer;

import com.zhaoyss.exchange.message.event.AbstractEvent;
import com.zhaoyss.exchange.messaging.*;
import com.zhaoyss.exchange.support.LoggerSupport;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhaoyss
 * @date 11/3/2025 下午 3:00
 * @description:
 */
@Component
public class SequenceService extends LoggerSupport implements CommonErrorHandler {

    private static final String GROUP_ID = "SequencerGroup";

    @Autowired
    private SequenceHandler sequenceHandler;

    @Autowired
    private MessagingFactory messagingFactory;

    @Autowired
    private MessageTypes messageTypes;

    // 全局唯一递增ID：
    private AtomicLong sequence;

    private MessageProducer<AbstractEvent> messageProducer;

    private Thread jobThread;


    @PostConstruct
    public void init() {
        Thread thread = new Thread(() -> {
            logger.info("Start sequence job...");
            // TODO: try get global DB lock:
//            while (!hasLock()){Thread.sleep(1000);}
            this.messageProducer = this.messagingFactory.createMessageProducer(Messaging.Topic.TRADE, AbstractEvent.class);

            // 找出最大的序列ID:
            this.sequence = new AtomicLong(this.sequenceHandler.getMaxSequenceId());

            // 初始化 consumer
            logger.info("create message consumer for {}...", getClass().getName());

            // 共享同一组ID：
            MessageConsumer consumer = this.messagingFactory.createBatchMessageListener(Messaging.Topic.SEQUENCE,
                    GROUP_ID, this::processMessages, this);

            // start running:
            this.running = true;
            while (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }

            // close message consumer:
            logger.info("close message consumer for {}...", getClass().getName());
            consumer.stop();
            System.exit(1);
        });
        this.jobThread = thread;
        this.jobThread.start();
    }

    private boolean running;

    private boolean crash = false;

    private void sendMessages(List<AbstractEvent> messages) {
        this.messageProducer.sendMessage(messages);
    }

    // 接收消息并定序在发送：
    private synchronized void processMessages(List<AbstractEvent> messages) {
        if (!running || crash) {
            panic();
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.info("do sequence for {} messages...", messages.size());
        }
        long start = System.currentTimeMillis();
        // 定序后的事件消息：
        List<AbstractEvent> sequenced = null;
        try {
            // 定序：
            sequenced = this.sequenceHandler.sequenceMessages(this.messageTypes, this.sequence, messages);
        } catch (Throwable e) {
            // 定序出错时进程退出：
            logger.error("exception when do sequence", e);
            System.exit(1);
            throw new Error(e);
        }
        if (logger.isInfoEnabled()) {
            long end = System.currentTimeMillis();
            logger.info("sequenced {} messages in {} ms. current sequence id: {}", messages.size(), (end - start), this.sequence.get());
        }
        // 发送定序后的消息：
        sendMessages(sequenced);
    }

    private void panic() {
        this.crash = true;
        this.running = false;
        System.exit(1);
    }
}
