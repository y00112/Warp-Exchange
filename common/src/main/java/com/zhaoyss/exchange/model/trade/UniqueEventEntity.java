package com.zhaoyss.exchange.model.trade;

import com.zhaoyss.exchange.model.support.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author zhaoyss
 * @date 11/3/2025 下午 3:13
 * @description:
 */
@Entity
@Table(name = "unique_events")
public class UniqueEventEntity implements EntitySupport {

    @Id
    @Column(nullable = false, updatable = false, length = VAR_CHAR_50)
    public String uniqueId;

    /**
     * 关联事件
     */
    public long sequenceId;

    /**
     * 创建时间，sequenced（定序） 后设置
     */
    public long createdAt;

    @Override
    public String toString() {
        return "UniqueEventEntity [uniqueId=" + uniqueId + ", sequenceId=" + sequenceId + ", createdAt=" + createdAt
                + "]";
    }
}
