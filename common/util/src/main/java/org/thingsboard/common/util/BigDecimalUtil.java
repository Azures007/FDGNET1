package org.thingsboard.common.util;

import java.math.BigDecimal;

public class BigDecimalUtil {

    private BigDecimalUtil() {

    }

    public static float format(double v1, int scale) {
        BigDecimal b = new BigDecimal(Double.toString(v1));
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static float format(float v1, int scale) {
        BigDecimal b = new BigDecimal(Float.toString(v1));
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static BigDecimal add(double v1, double v2) {
        // v1 + v2
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    public static BigDecimal sub(double v1, double v2) {
        //v1-v2
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }

    public static BigDecimal mul(double v1, double v2) {
        //v1*v2
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    public static BigDecimal div(double v1, double v2) {
        //v1/v2
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        // 2 = 保留小数点后两位   ROUND_HALF_UP = 四舍五入
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP);// 应对除不尽的情况
    }

    public static BigDecimal add(float v1, float v2) {
        // v1 + v2
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.add(b2);
    }

    public static BigDecimal sub(float v1, float v2) {
        //v1-v2
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.subtract(b2);
    }

    public static BigDecimal mul(float v1, float v2) {
        //v1*v2
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.multiply(b2);
    }

    public static BigDecimal div(float v1, float v2) {
        //v1/v2
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        // 2 = 保留小数点后两位   ROUND_HALF_UP = 四舍五入
        return b1.divide(b2, 3, BigDecimal.ROUND_HALF_UP);// 应对除不尽的情况
    }

    public static BigDecimal div(float v1, float v2, int scale) {
        //v1/v2
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        // 2 = 保留小数点后两位   ROUND_HALF_UP = 四舍五入
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);// 应对除不尽的情况
    }

    /**
     * 安全的BigDecimal减法运算
     */
    public static BigDecimal safeSubtract(BigDecimal minuend, BigDecimal subtrahend) {
        BigDecimal safeMinuend = minuend != null ? minuend : BigDecimal.ZERO;
        BigDecimal safeSubtrahend = subtrahend != null ? subtrahend : BigDecimal.ZERO;
        return safeMinuend.subtract(safeSubtrahend);
    }

    /**
     * 安全的BigDecimal加法运算
     */
    public static BigDecimal safeAdd(BigDecimal addend1, BigDecimal addend2) {
        BigDecimal safeAddend1 = addend1 != null ? addend1 : BigDecimal.ZERO;
        BigDecimal safeAddend2 = addend2 != null ? addend2 : BigDecimal.ZERO;
        return safeAddend1.add(safeAddend2);
    }

    /**
     * 安全的BigDecimal乘法运算
     */
    public static BigDecimal safeMultiply(BigDecimal multiplicand, BigDecimal multiplier) {
        if (multiplicand == null || multiplier == null) {
            return BigDecimal.ZERO;
        }
        return multiplicand.multiply(multiplier);
    }

}
