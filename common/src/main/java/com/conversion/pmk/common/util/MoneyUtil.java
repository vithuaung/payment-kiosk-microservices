package com.conversion.pmk.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Utility methods for monetary calculations
public final class MoneyUtil {

    private MoneyUtil() {
        // Utility class
    }

    // Round to 2 decimal places using HALF_UP
    public static BigDecimal round(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    // Convert double to rounded BigDecimal
    public static BigDecimal of(double value) {
        return round(BigDecimal.valueOf(value));
    }

    // Null-safe positive check
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    // Null-safe addition with rounding
    public static BigDecimal safeAdd(BigDecimal a, BigDecimal b) {
        BigDecimal left = a != null ? a : BigDecimal.ZERO;
        BigDecimal right = b != null ? b : BigDecimal.ZERO;
        return round(left.add(right));
    }

    // Null-safe subtraction with rounding
    public static BigDecimal safeSubtract(BigDecimal a, BigDecimal b) {
        BigDecimal left = a != null ? a : BigDecimal.ZERO;
        BigDecimal right = b != null ? b : BigDecimal.ZERO;
        return round(left.subtract(right));
    }
}
