package com.zhaoyss.exchange.model.trade;

import com.zhaoyss.exchange.model.support.EntitySupport;
import jakarta.persistence.*;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;

import static com.zhaoyss.exchange.model.support.EntitySupport.VAR_CHAR_10000;

/**
 * @author zhaoyss
 * @date 5/3/2025 下午 1:54
 * @description:
 */
@Data
@Entity
@Table(name = "events", uniqueConstraints = @UniqueConstraint(name = "UNI_PREV_ID", columnNames = {"previousId"}))
public class EventEntity implements EntitySupport {

    /**
     * 主键:已分配
     */
    @Id
    @Column(nullable = false, updatable = false)
    public long sequenceId;


    /**
     * 保留以前的ID，第一个事件的上一个ID为0.
     */
    @Column(nullable = false, updatable = false)
    public long previousId;

    /**
     * JSON 编号的事件数据
     */
    @Column(nullable = false, updatable = false,length = VAR_CHAR_10000)
    public String data;

    @Column(nullable = false, updatable = false)
    public long createdAt;

    @Override
    public String toString() {
        return "EventEntity [sequenceId=" + sequenceId + ", previousId=" + previousId + ", data=" + data
                + ", createdAt=" + createdAt + "]";
    }


}
