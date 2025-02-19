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

    // 冻结
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

    // 转账
    public void transfer(Transfer type, Long fromUser, Long toUser, AssetEnum assetId, BigDecimal amount) {
        if (!tryTransfer(type, fromUser, toUser, assetId, amount, true)) {
            throw new RuntimeException("Transfer failed");
        }
    }

    // 尝试转账
    public boolean tryTransfer(Transfer type, Long fromUser, Long toUser, AssetEnum assetId, BigDecimal amount, boolean checkBalance) {
        // 转账金额不能为负
        if (amount.signum() == 0) {
            throw new IllegalArgumentException("Negative amount");
        }
        // 获取源用户资产:
        Asset fromAsset = getAsset(fromUser, assetId);
        if (fromAsset == null) {
            // 资产不存在时初始化用户资产：
            fromAsset = initAssets(fromUser, assetId);
        }
        // 获取目标用户资产
        Asset toAsset = getAsset(toUser, assetId);
        if (toAsset == null) {
            // 资产不存在时初始化用户资产：
            toAsset = initAssets(toUser, assetId);
        }
        return switch (type) {
            case AVAILABLE_TO_AVAILABLE -> {
                // 需要检查余额且余额不足：
                if (checkBalance && fromAsset.available.compareTo(amount) < 0) {
                    // 转账失败：
                    yield false;
                }
                // 源用户的可用资产减少:
                fromAsset.available = fromAsset.available.subtract(amount);
                // 目标用户的可用资产增加:
                toAsset.available = toAsset.available.add(amount);
                // 返回成功
                yield true;

            }
            // 从可用转至冻结
            case AVAILABLE_TO_FROZEN -> {
                if (checkBalance && fromAsset.available.compareTo(amount) < 0) {
                    yield false;
                }
                fromAsset.available = fromAsset.available.subtract(amount);
                toAsset.frozen = toAsset.frozen.add(amount);
                yield true;
            }
            // 从冻结转至可用
            case FROZEN_TO_AVAILABLE -> {
                if (checkBalance && fromAsset.frozen.compareTo(amount) < 0) {
                    yield false;
                }
                fromAsset.frozen = fromAsset.frozen.subtract(amount);
                toAsset.available = toAsset.available.add(amount);
                yield true;
            }
            default -> {
                throw new IllegalArgumentException("invalid type:" + type);
            }
        };
    }

    private Asset initAssets(Long userId, AssetEnum assetId) {
        ConcurrentMap<AssetEnum, Asset> map = userAssets.get(userId);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            userAssets.put(userId, map);
        }
        Asset zeroAsset = new Asset();
        map.put(assetId, zeroAsset);
        return zeroAsset;
    }

}
