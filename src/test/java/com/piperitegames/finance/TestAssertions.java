package com.piperitegames.finance;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestAssertions {

    private TestAssertions() {
    }

    /**
     * Сравнивает суммы по значению: 90.00 и 90.0000 считаются равными.
     */
    public static void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                () -> "Ожидалось " + expected + ", получено " + actual.toPlainString());
    }
}