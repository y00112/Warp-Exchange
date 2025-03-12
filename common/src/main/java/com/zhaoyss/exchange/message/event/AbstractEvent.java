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
     * 定序后的Sequence ID:
     */
    public long sequenceId;

    /**
     * 定序后的Previous Sequence ID:
     */
    public long previousId;

    /**
     * 可选的全局唯一标识
     */
    @Nullable
    public String uniqueId;
}
