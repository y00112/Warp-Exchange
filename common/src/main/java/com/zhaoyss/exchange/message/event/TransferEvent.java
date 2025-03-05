package com.zhaoyss.exchange.message.event;

import com.zhaoyss.exchange.enums.AssetEnum;

import java.math.BigDecimal;

/**
 * @author zhaoyss
 * @date 3/3/2025 下午 5:45
 * @description: 交易事件 Transfer between users.
 */
public class TransferEvent extends AbstractEvent {

    public Long fromUserId;
    public Long toUserId;
    public AssetEnum asset;
    public BigDecimal amount;
    public boolean sufficient;

    @Override
    public String toString() {
        return "TransferEvent [sequenceId=" + sequenceId + ", previousId=" + previousId + ", uniqueId=" + uniqueId
                + ", refId=" + refId + ", createdAt=" + createdAt + ", fromUserId=" + fromUserId + ", toUserId="
                + toUserId + ", asset=" + asset + ", amount=" + amount + ", sufficient=" + sufficient + "]";
    }
}
