package com.zhaoyss.exchange.assets;

import com.zhaoyss.exchange.enums.AssetEnum;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AssetServiceTest {

    static final Long DEBT = 1L;
    static final Long USER_A = 2000L;
    static final Long USER_B = 3000L;
    static final Long USER_C = 4000L;

    AssetService service;

    @BeforeEach
    public void setUp() {
        service = new AssetService();
        init();
    }

    @AfterEach
    public void tearDown() {
        verify();
    }

    @Test
    void tryTransfer() {
        // A -> B ok:
        service.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, USER_A, USER_B, AssetEnum.USD, new BigDecimal("12000"), true);
        assertBDEquals(300, service.getAsset(USER_A, AssetEnum.USD).available);
        assertBDEquals(12000 + 45600, service.getAsset(USER_B, AssetEnum.USD).available);

        // A -> B failed:
        assertFalse(service.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, USER_A, USER_B, AssetEnum.USD, new BigDecimal("301"), true));
        assertBDEquals(300, service.getAsset(USER_A, AssetEnum.USD).available);
        assertBDEquals(12000 + 45600, service.getAsset(USER_B, AssetEnum.USD).available);
    }

    @Test
    void tryFreeze() {
        // freeze 12000 ok:
        service.tryFreeze(USER_A, AssetEnum.USD, new BigDecimal(12000));
        assertBDEquals(300, service.getAsset(USER_A, AssetEnum.USD).available);
        assertBDEquals(12000, service.getAsset(USER_A, AssetEnum.USD).frozen);

        // freeze 301 failed:
        assertFalse(service.tryFreeze(USER_A, AssetEnum.USD, new BigDecimal(301)));

        assertBDEquals(300, service.getAsset(USER_A, AssetEnum.USD).available);
        assertBDEquals(12000, service.getAsset(USER_A, AssetEnum.USD).frozen);
    }

    @Test
    void unfreeze() {
        // freeze 12000 ok:
        service.tryFreeze(USER_A, AssetEnum.USD, new BigDecimal(12000));
        assertBDEquals(300, service.getAsset(USER_A, AssetEnum.USD).available);
        assertBDEquals(12000, service.getAsset(USER_A, AssetEnum.USD).frozen);

        // unfreeze 9000 ok:
        service.unfreeze(USER_A, AssetEnum.USD, new BigDecimal(9000));
        assertBDEquals(9300, service.getAsset(USER_A, AssetEnum.USD).available);
        assertBDEquals(3000, service.getAsset(USER_A, AssetEnum.USD).frozen);

        // unfreeze 3001 failed:
        assertThrows(RuntimeException.class, () -> service.unfreeze(USER_A, AssetEnum.USD, new BigDecimal(3001)));
    }

    /**
     * A: USD = 12300，BTC=12
     * <p>
     * B: USD = 45600
     * <p>
     * C: BTC = 34
     */
    void init() {
        // 存入A的USD，额度为：12300:
        service.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, DEBT, USER_A, AssetEnum.USD, BigDecimal.valueOf(12300), false);
        // 存入A的BTC，额度为：12:
        service.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, DEBT, USER_A, AssetEnum.BTC, BigDecimal.valueOf(12), false);
        // 存入B的USD，额度为：45600:
        service.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, DEBT, USER_B, AssetEnum.USD, BigDecimal.valueOf(45600), false);
        // 存入C的BTC，额度为：34
        service.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, DEBT, USER_C, AssetEnum.BTC, BigDecimal.valueOf(34), false);

        assertBDEquals(-57900, service.getAsset(DEBT, AssetEnum.USD).available);
        assertBDEquals(-46, service.getAsset(DEBT, AssetEnum.BTC).available);

    }

    void verify() {
        BigDecimal totalUSD = BigDecimal.ZERO;
        BigDecimal totalBTC = BigDecimal.ZERO;
        for (Long userId : service.userAssets.keySet()) {
            Asset assetUSD = service.getAsset(userId, AssetEnum.USD);
            if (assetUSD != null) {
                totalUSD = totalUSD.add(assetUSD.available).add(assetUSD.frozen);
            }
            var assetBTC = service.getAsset(userId, AssetEnum.BTC);
            if (assetBTC != null) {
                totalBTC = totalBTC.add(assetBTC.available).add(assetBTC.frozen);
            }
        }

        assertBDEquals(0, totalUSD);
        assertBDEquals(0, totalBTC);
    }

    void assertBDEquals(long value, BigDecimal bd) {
        assertBDEquals(String.valueOf(value), bd);
    }

    void assertBDEquals(String value, BigDecimal bd) {
        assertTrue(new BigDecimal(value).compareTo(bd) == 0,
                String.format("Expected %s but actual %s.", value, bd.toPlainString()));
    }
}
