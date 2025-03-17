package com.zhaoyss.exchange.model.ui;

import com.zhaoyss.exchange.enums.UserType;
import com.zhaoyss.exchange.model.support.EntitySupport;
import jakarta.persistence.*;
import org.apache.kafka.common.protocol.types.Field;

/**
 * @author zhaoyss
 * @date 12/3/2025 上午 11:18
 * @description:
 */
@Entity
@Table(name = "users")
public class UserEntity implements EntitySupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, length = VAR_ENUM)
    public long id;

    @Column(nullable = false, updatable = false, length = VAR_ENUM)
    public UserType type;

    @Column(nullable = false, updatable = false)
    public long createdAt;

    @Override
    public String toString() {
        return "UserEntity [id=" + id + ", type=" + type + ", createdAt=" + createdAt + "]";
    }
}
