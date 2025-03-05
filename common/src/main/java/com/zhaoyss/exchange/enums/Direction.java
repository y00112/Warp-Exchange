package com.zhaoyss.exchange.enums;

public enum Direction {

    BUY(1),

    SELL(0);

    /**
     * Direction的int值
     */
    public final int value;

    public Direction negate() {
        return this == BUY ? SELL : BUY;
    }

    Direction(int value) {
        this.value = value;
    }

    public static Direction of(int value) {
        if (value == 1) {
            return BUY;
        }
        if (value == 0) {
            return SELL;
        }
        throw new IllegalArgumentException("Invalid direction value.");
    }
}
