package com.zhaoyss.exchange.assets;

import com.zhaoyss.exchange.enums.AssetEnum;
import com.zhaoyss.exchange.support.LoggerSupport;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class AssetService extends LoggerSupport {

    // userId -> Map(AssetEnum -> Assets[available/frozen])
    final ConcurrentMap<Long, ConcurrentMap<AssetEnum, Asset>> userAssets = new ConcurrentHashMap<Long, ConcurrentMap<AssetEnum, Asset>>();

    public Asset getAsset(Long userId, AssetEnum assetId) {
        ConcurrentMap<AssetEnum, Asset> assets = userAssets.get(userId);
        if (assets == null) {
            return null;
        }
        return assets.get(assetId);
    }

    public Map<AssetEnum, Asset> getAssets(Long userId) {
        Map<AssetEnum, Asset> assets = userAssets.get(userId);
        if (assets == null) {
            return Map.of();
        }
        return assets;
    }

    public ConcurrentMap<Long, ConcurrentMap<AssetEnum, Asset>> getUserAssets() {
        return this.userAssets;
    }

    // 尝试冻结
    public boolean tryFreeze(Long userId, AssetEnum assetId, BigDecimal amount) {
        boolean ok = tryTransfer(Transfer.AVAILABLE_TO_FROZEN, userId, userId, assetId, amount, true);
        if (ok && logger.isDebugEnabled()) {
            logger.debug("freezed user {}, asset {}, amount {}", userId, assetId, amount);
        }
        return ok;
    }

    // 解冻
    public void unfreeze(Long userId, AssetEnum assetId, BigDecimal amount) {
        if (!tryTransfer(Transfer.FROZEN_TO_AVAILABLE, userId, userId, assetId, amount, true)) {
            throw new RuntimeException("Unfreeze failed for user " + userId + ", asset = " + assetId + ", amount = " + amount);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("unfreezed user {}, asset {}, amount {}", userId, assetId, amount);
        }
    }

    // 转移
    public void transfer(Transfer type, Long fromUser, Long toUser, AssetEnum assetId, BigDecimal amount) {
        if (!tryTransfer(type, fromUser, toUser, assetId, amount, true)) {
            throw new RuntimeException("Transfer failed for " + type + ", from user " + fromUser + " to user " + toUser + ", asset = " + assetId + ", amount = " + amount);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("transfer asset {}, from {} => {}, amount {}", assetId, fromUser, toUser, amount);
        }
    }

    // 尝试转移
    public boolean tryTransfer(Transfer type, Long fromUser, Long toUser, AssetEnum assetId, BigDecimal amount, boolean checkBalance) {
        if (amount.signum() == 0){

        }
    }

}
