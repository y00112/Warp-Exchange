package com.zhaoyss.exchange.message;

import com.zhaoyss.exchange.model.quotation.TickEntity;

import java.util.List;

/**
 * @author zhaoyss
 * @date 4/3/2025 下午 5:46
 * @description:
 */
public class TickMessage extends AbstractMessage {

    public long sequenceId;

    public List<TickEntity> ticks;
}
