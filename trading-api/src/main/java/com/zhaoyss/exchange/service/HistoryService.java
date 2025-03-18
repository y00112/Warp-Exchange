package com.zhaoyss.exchange.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhaoyss.exchange.bean.SimpleMatchDetailRecord;
import com.zhaoyss.exchange.model.trade.MatchDetailEntity;
import com.zhaoyss.exchange.model.trade.OrderEntity;
import com.zhaoyss.exchange.support.AbstractDbService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaoyss
 * @date 18/3/2025 上午 10:38
 * @description: 历史服务
 */
@Component
public class HistoryService extends AbstractDbService {

    public List<OrderEntity> getHistoryOrders(Long userId, int maxResults) {
        return genericDbService.getMapperRegistry().getMapper(OrderEntity.class).selectList(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getUserId, userId)
                .orderByDesc(OrderEntity::getId));
    }

    public OrderEntity getHistoryOrder(Long userId, Long orderId) {
        OrderEntity entity = genericDbService.getMapperRegistry().getMapper(OrderEntity.class).selectOne(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getId, orderId));

        if (entity == null || entity.userId.longValue() != userId.longValue()) {
            return null;
        }
        return entity;
    }

    public List<SimpleMatchDetailRecord> getHistoryMatchDetails(Long orderId) {
        List<MatchDetailEntity> details = genericDbService.getMapperRegistry().getMapper(MatchDetailEntity.class).selectList(new LambdaQueryWrapper<MatchDetailEntity>()
                .select(MatchDetailEntity::getPrice, MatchDetailEntity::getQuantity, MatchDetailEntity::getType)
                .eq(MatchDetailEntity::getOrderId, orderId)
                .orderByDesc(MatchDetailEntity::getId));

        return details.stream().map(e -> new SimpleMatchDetailRecord(e.price, e.quantity, e.type))
                .collect(Collectors.toList());
    }
}
