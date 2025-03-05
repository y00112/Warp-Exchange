package com.zhaoyss.exchange.store;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.zhaoyss.exchange.mapper.EventMapper;
import com.zhaoyss.exchange.message.event.AbstractEvent;
import com.zhaoyss.exchange.messaging.MessageTypes;
import com.zhaoyss.exchange.model.trade.EventEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaoyss
 * @date 5/3/2025 上午 11:43
 * @description:
 */
@Component
@Transactional
public class StoreService {

    @Autowired
    MessageTypes messageTypes;

    @Autowired
    EventMapper eventMapper;

    public List<AbstractEvent> loadEventsFromDb(long lastEventId) {
        List<EventEntity> events = this.eventMapper.selectList(new LambdaQueryWrapper<EventEntity>().
                eq(EventEntity::getSequenceId, lastEventId)
                .orderByDesc(EventEntity::getSequenceId));
        return events.stream().map(event -> (AbstractEvent) messageTypes.deserialize(event.data)).collect(Collectors.toList());
    }
}
