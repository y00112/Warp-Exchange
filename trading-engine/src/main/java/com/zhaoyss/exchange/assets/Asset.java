package com.zhaoyss.exchange.assets;

import java.math.BigDecimal;

public class Asset {

    // 可用
    BigDecimal available;

    // 冻结
    BigDecimal frozen;

    public Asset() {
        this(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public Asset(BigDecimal available, BigDecimal frozen) {
        this.available = available;
        this.frozen = frozen;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public BigDecimal getFrozen() {
        return frozen;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public void setFrozen(BigDecimal frozen) {
        this.frozen = frozen;
    }

    @Override
    public String toString() {
        return String.format("[available=%04.2f, frozen=%04.2f]", available, frozen);
    }
}
