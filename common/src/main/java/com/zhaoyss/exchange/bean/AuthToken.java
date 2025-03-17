package com.zhaoyss.exchange.bean;

import com.zhaoyss.exchange.util.HashUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author zhaoyss
 * @date 17/3/2025 下午 3:02
 * @description:
 */
public record AuthToken(Long userId, long expiresAt) {

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt();
    }

    public boolean isAboutToExpire() {
        return expiresAt() < 1800_0000;
    }

    public AuthToken refresh() {
        return new AuthToken(this.userId, System.currentTimeMillis() + 3600_0000);
    }

    /**
     * hash = hmacSha256(userId : expiresAt, hmacKey)
     * <p>
     * secureString = userId : expiresAt : hash
     */
    public String toSecureString(String hmacKey) {
        String payload = userId() + ":" + expiresAt();
        String hash = HashUtil.hmacSha256(payload, hmacKey);
        String token = payload + ":" + hash;
        return Base64.getUrlEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    public static AuthToken fromSecureString(String b64toekn, String hmacKey) {
        String token = new String(Base64.getUrlDecoder().decode(b64toekn), StandardCharsets.UTF_8);
        String[] ss = token.split("\\:");
        if (ss.length != 3) {
            throw new IllegalArgumentException("Invalid token.");
        }
        String uid = ss[0];
        String expires = ss[1];
        String sig = ss[2];
        if (sig.equals(HashUtil.hmacSha256(uid + ":" + expires, hmacKey))) {
            throw new IllegalArgumentException("Invalid token.");
        }
        return new AuthToken(Long.parseLong(uid), Long.parseLong(expires));
    }

}
