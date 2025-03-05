package com.zhaoyss.exchange.message.event;

import com.zhaoyss.exchange.message.AbstractMessage;
import jakarta.annotation.Nullable;

/**
 * @author zhaoyss
 * @date 28/2/2025 下午 3:20
 * @description: 抽象事件
 */
public class AbstractEvent extends AbstractMessage {

    /**
     * Message id, set after sequenced
     */
    public long sequenceId;

    /**
     * Previous message sequence id.
     */
    public long previousId;

    /**
     * Unique Id or null if not set
     */
    @Nullable
    public String uniqueId;
}
