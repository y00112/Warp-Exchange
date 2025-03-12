package com.zhaoyss.exchange.sequencer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhaoyss.exchange.mapper.MapperRegistry;
import com.zhaoyss.exchange.message.event.AbstractEvent;
import com.zhaoyss.exchange.messaging.MessageTypes;
import com.zhaoyss.exchange.model.trade.EventEntity;
import com.zhaoyss.exchange.model.trade.UniqueEventEntity;
import com.zhaoyss.exchange.support.AbstractDbService;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhaoyss
 * @date 11/3/2025 下午 3:02
 * @description: 写入SequenceID并落库
 */
@Component
@Transactional(rollbackFor = Throwable.class)
public class SequenceHandler extends AbstractDbService {

    private long lastTimestamp = 0;

    final MapperRegistry db = genericDbService.getMapperRegistry();

    public List<AbstractEvent> sequenceMessages(MessageTypes messageTypes, AtomicLong sequence, List<AbstractEvent> messages) {

        final long t = System.currentTimeMillis();
        if (t < this.lastTimestamp) {
            logger.warn("[Sequence] current time {} is turned back from {}!", t, this.lastTimestamp);
        } else {
            this.lastTimestamp = t;
        }

        List<UniqueEventEntity> uniques = null;
        Set<String> uniqueKeys = null;
        List<AbstractEvent> sequencedMessages = new ArrayList<>(messages.size());
        List<EventEntity> events = new ArrayList<>(messages.size());
        for (AbstractEvent message : messages) {
            UniqueEventEntity unique = null;
            final String uniqueId = message.uniqueId;
            // check uniqueId:
            if (uniqueId != null) {
                if ((uniqueKeys != null && uniqueKeys.contains(uniqueId))
                        || db.getMapper(UniqueEventEntity.class).selectById(uniqueId) != null) {
                    logger.warn("ignore processed unique Message: {}", message);
                    continue;
                }
                unique = new UniqueEventEntity();
                unique.uniqueId = uniqueId;
                unique.createdAt = message.createdAt;
                if (uniques == null) {
                    uniques = new ArrayList<>();
                }
                uniques.add(unique);
                if (uniqueKeys == null) {
                    uniqueKeys = new HashSet<>();
                }
                uniqueKeys.add(uniqueId);
                logger.info("unique event {} sequenced.", uniqueId);
            }

            final long previousId = sequence.get();
            final long currentId = sequence.incrementAndGet();

            // 先设置message的sequenceId / previousId / createdAt,在序列化并落库
            message.sequenceId = currentId;
            message.previousId = previousId;
            message.createdAt = this.lastTimestamp;

            // 如果此消息关联了UniqueEvent,给UniqueEvent加上相同的SequenceId:
            if (unique != null) {
                unique.sequenceId = message.sequenceId;
            }

            // create AbstractEvent and save to db later:
            EventEntity event = new EventEntity();
            event.previousId = previousId;
            event.sequenceId = currentId;

            event.data = messageTypes.serialize(message);
            event.createdAt = this.lastTimestamp;
            events.add(event);

            // 添加到结果集
            sequencedMessages.add(message);
        }

        if (uniques != null) {
            db.getMapper(UniqueEventEntity.class).insert(uniques);
        }
        db.getMapper(EventEntity.class).insert(events);
        // 返回定序后的消息
        return sequencedMessages;
    }

    public long getMaxSequenceId() {
        EventEntity last = db.getMapper(EventEntity.class).selectList(new LambdaQueryWrapper<EventEntity>().orderByDesc(EventEntity::getSequenceId)).getFirst();
        if (last == null) {
            logger.info("no max sequenceId found. set max sequenceId = 0.");
            return 0;
        }
        this.lastTimestamp = last.createdAt;
        logger.info("find max sequenceId = {}, last timestamp = {}", last.sequenceId, this.lastTimestamp);
        return last.sequenceId;
    }
}
