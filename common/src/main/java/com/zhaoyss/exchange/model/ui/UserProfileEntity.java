package com.zhaoyss.exchange.model.ui;

import com.zhaoyss.exchange.model.support.EntitySupport;
import jakarta.persistence.*;
import lombok.Data;

/**
 * @author zhaoyss
 * @date 12/3/2025 上午 11:21
 * @description:
 */
@Data
@Entity
@Table(name = "user_profiles", uniqueConstraints = {@UniqueConstraint(name = "UNI_EMAIL", columnNames = {"email"})})
public class UserProfileEntity implements EntitySupport {

    /**
     * 关联至用户ID
     */
    @Id
    @Column(nullable = false, updatable = false)
    public Long userId;

    /**
     * 登录Email
     */
    @Column(nullable = false,updatable = false,length = VAR_CHAR_50)
    public String email;

    @Column(nullable = false,length = VAR_CHAR_100)
    public String name;

    @Column(nullable = false, updatable = false)
    public long createdAt;

    @Column(nullable = false)
    public long updatedAt;

    @Override
    public String toString() {
        return "UserProfileEntity [userId=" + userId + ", email=" + email + ", name=" + name + ", createdAt="
                + createdAt + ", updatedAt=" + updatedAt + "]";
    }

}
