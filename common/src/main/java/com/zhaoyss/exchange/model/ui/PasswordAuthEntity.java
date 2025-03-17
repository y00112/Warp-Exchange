package com.zhaoyss.exchange.model.ui;

import com.zhaoyss.exchange.model.support.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author zhaoyss
 * @date 12/3/2025 下午 5:15
 * @description:
 */
@Data
@Entity
@Table(name = "password_auths")
public class PasswordAuthEntity implements EntitySupport {

    /**
     * 关联至用户ID：
     */
    @Id
    @Column(nullable = false, updatable = false)
    public Long userId;

    /**
     * 随机字符串用于创建Hmac-SHA256.
     */
    @Column(nullable = false, updatable = false, length = VAR_ENUM)
    public String random;

    /**
     * 存储HmacSha256哈希 password = HmacSHA256(原始口令，key = random)
     */
    @Column(nullable = false, length = VAR_CHAR_100)
    public String password;
}
