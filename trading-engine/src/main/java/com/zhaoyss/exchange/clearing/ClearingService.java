package com.zhaoyss.exchange.clearing;

import com.zhaoyss.exchange.assets.AssetService;
import com.zhaoyss.exchange.assets.Transfer;
import com.zhaoyss.exchange.enums.AssetEnum;
import com.zhaoyss.exchange.enums.OrderStatus;
import com.zhaoyss.exchange.match.MatchDetailRecord;
import com.zhaoyss.exchange.match.MatchResult;
import com.zhaoyss.exchange.model.trade.OrderEntity;
import com.zhaoyss.exchange.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author zhaoyss
 * @date 28/2/2025 下午 2:01
 * @description: 清算服务
 */
@Component
public class ClearingService {

    @Value("${exchange.fee-rate:0.0005}")
    BigDecimal feeRate;

    final AssetService assetService;

    final OrderService orderService;

    public ClearingService(@Autowired AssetService assetService, @Autowired OrderService orderService) {
        this.assetService = assetService;
        this.orderService = orderService;
    }

    public void clearMatchResult(MatchResult result) {
        OrderEntity taker = result.takerOrder;
        switch (taker.direction) {
            case BUY -> {
                // TODO:
                // 买入时，按照Maker的价格成交
                for (MatchDetailRecord detail : result.matchDetails) {
                    OrderEntity maker = detail.makerOrder();
                    BigDecimal matched = detail.quantity();
                    if (taker.price.compareTo(maker.price) > 0) {
                        // 实例买入价比报价低，部分USD退回账户
                        BigDecimal unfreezeQuote = taker.price.subtract(maker.price).multiply(matched);
                        assetService.unfreeze(taker.userId, AssetEnum.USD, unfreezeQuote);
                    }
                    // 买方USD转入卖方账户：
                    assetService.transfer(Transfer.FROZEN_TO_AVAILABLE, taker.userId, maker.userId, AssetEnum.USD, maker.price.multiply(matched));
                    // 卖方的BTC转入卖方账户：
                    assetService.transfer(Transfer.FROZEN_TO_AVAILABLE, maker.userId, taker.userId, AssetEnum.BTC, matched);
                    // 删除完全成交的Maker:
                    if (maker.unfilledQuantity.signum() == 0) {
                        orderService.removeOrder(maker.id);
                    }
                    // 删除完全成交的Maker:
                    if (taker.unfilledQuantity.signum() == 0) {
                        orderService.removeOrder(taker.id);
                    }
                }
            }
            case SELL -> {
                // TODO:
                for (MatchDetailRecord detail : result.matchDetails) {
                    OrderEntity maker = detail.makerOrder();
                    BigDecimal matched = detail.quantity();
                    // 卖方BTC转入买方账户:
                    assetService.transfer(Transfer.FROZEN_TO_AVAILABLE, taker.userId, maker.userId, AssetEnum.BTC, matched);
                    // 买房USD转入卖方账户：
                    assetService.transfer(Transfer.FROZEN_TO_AVAILABLE, maker.userId, taker.userId, AssetEnum.USD, maker.price.multiply(matched));
                    // 删除完全成交的Maker：
                    if (maker.unfilledQuantity.signum() == 0) {
                        orderService.removeOrder(maker.id);
                    }
                }
                // 删除未完全成交的Taker：
                if (taker.unfilledQuantity.signum() == 0) {
                    orderService.removeOrder(taker.id);
                }
            }
            default -> throw new IllegalArgumentException("Invalid direction");
        }
    }

    public void clearCancelOrder(OrderEntity order) {
        switch (order.direction) {
            case BUY -> {
                // 解冻USD = 价格 * 未成交数量
                assetService.unfreeze(order.userId, AssetEnum.USD, order.price.multiply(order.unfilledQuantity));
            }
            case SELL -> {
                // 解冻BTC = 非成交的数据
                assetService.unfreeze(order.userId, AssetEnum.BTC, order.unfilledQuantity);
            }
            default -> throw new IllegalArgumentException("Invalid direction");
        }
        // 从OrderService中删除订单：
        orderService.removeOrder(order.id);
    }
}
