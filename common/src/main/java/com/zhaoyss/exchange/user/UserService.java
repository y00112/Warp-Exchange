package com.zhaoyss.exchange.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhaoyss.exchange.ApiError;
import com.zhaoyss.exchange.ApiException;
import com.zhaoyss.exchange.enums.UserType;
import com.zhaoyss.exchange.mapper.GenericDbService;
import com.zhaoyss.exchange.model.ui.PasswordAuthEntity;
import com.zhaoyss.exchange.model.ui.UserEntity;
import com.zhaoyss.exchange.model.ui.UserProfileEntity;
import com.zhaoyss.exchange.util.HashUtil;
import com.zhaoyss.exchange.util.RandomUtil;
import jakarta.servlet.GenericServlet;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.PasswordAuthentication;

/**
 * @author zhaoyss
 * @date 12/3/2025 下午 5:04
 * @description:
 */
@Component
public class UserService {

    @Autowired
    GenericDbService db;

    public UserProfileEntity signup(String email, String name, String password) {
        final long ts = System.currentTimeMillis();
        // insert User:
        var user = new UserEntity();
        user.type = UserType.TRADER;
        user.createdAt = ts;
        db.getMapperRegistry().getMapper(UserEntity.class).insert(user);

        // insert User profile:
        var up = new UserProfileEntity();
        up.userId = user.id;
        up.email = email;
        up.name = name;
        up.createdAt = up.updatedAt = ts;
        db.getMapperRegistry().getMapper(UserProfileEntity.class).insert(up);

        // insert password auth:
        var pa = new PasswordAuthEntity();
        pa.userId = user.id;
        pa.random = RandomUtil.createRandomString(32);
        pa.password = HashUtil.hmacSha256(password, pa.random);
        db.getMapperRegistry().getMapper(PasswordAuthEntity.class).insert(pa);
        return up;
    }

    public UserProfileEntity signup(String email, String password) {
        UserProfileEntity userProfile = getUserProfileByEmail(email);
        // 通过 id 查找 PasswordAuthEntity by user id：
        PasswordAuthEntity pa = db.getMapperRegistry().getMapper(PasswordAuthEntity.class).selectOne(new LambdaQueryWrapper<PasswordAuthEntity>().eq(PasswordAuthEntity::getUserId, userProfile.getUserId()));
        if (pa == null) {
            throw new ApiException(ApiError.USER_CANNOT_SIGNIN);
        }
        // 检查密码 hash：
        String hash = HashUtil.hmacSha256(password, pa.password);
        if (!hash.equals(pa.password)) {
            throw new ApiException(ApiError.AUTH_SIGIN_FAILED);
        }
        return userProfile;
    }

    private UserProfileEntity getUserProfileByEmail(String email) {
        UserProfileEntity userProfile = db.getMapperRegistry().getMapper(UserProfileEntity.class).selectOne(new LambdaQueryWrapper<>(UserProfileEntity.class)
                .eq(UserProfileEntity::getEmail, email));
        if (userProfile == null) {
            throw new ApiException(ApiError.AUTH_SIGIN_FAILED);
        }
        return userProfile;
    }
}
