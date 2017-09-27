package com.born.frame.utils;

import android.text.TextUtils;

/**
 * Created by min on 2016/12/9.
 */

public class DigitUtil {


    /**
     * 两个长的数字字符串做和
     *
     * @param first
     * @param second
     * @return
     */
    public static String addTwoNumberOfString(String first, String second) {
        if (!FormatUtil.isNumeric(first) || !FormatUtil.isNumeric(second)) {
            return "0";
        }

        String integerF = "";
        String decimalF = "";
        String integerS = "";
        String decimalS = "";
        String sumInteger = "";
        String sumDecimal = "";

        int firstIndex = first.indexOf(".");
        if (firstIndex == -1) {
            integerF = first;
        } else {
            integerF = first.substring(0, firstIndex);
            decimalF = first.substring(firstIndex + 1);
        }

        int secondIndex = second.indexOf(".");
        if (secondIndex == -1) {
            integerS = second;
        } else {
            integerS = second.substring(0, secondIndex);
            decimalS = second.substring(secondIndex + 1);
        }

        // 字符串反转
        integerF = new StringBuffer(integerF).reverse().toString();
        integerS = new StringBuffer(integerS).reverse().toString();
        decimalF = new StringBuffer(decimalF).reverse().toString();
        decimalS = new StringBuffer(decimalS).reverse().toString();

        int sumc = 0;
        String cf = "";
        String cs = "";

        // 裁剪为等长
        if (decimalF.length() > decimalS.length()) {
            int offsetLen = decimalF.length() - decimalS.length();
            sumDecimal = decimalF.substring(0, offsetLen);
            decimalF = decimalF.substring(offsetLen);
        } else {
            int offsetLen = decimalS.length() - decimalF.length();
            sumDecimal = decimalS.substring(0, offsetLen);
            decimalS = decimalS.substring(offsetLen);
        }
        // 计算小数部分
        for (int i = 0; i < decimalF.length(); i++) {
            cf = decimalF.substring(i, i + 1);
            cs = decimalS.substring(i, i + 1);
            sumc += Integer.parseInt(cf) + Integer.parseInt(cs);
            sumDecimal += sumc % 10;
            sumc = sumc / 10;
        }
        sumDecimal = new StringBuffer(sumDecimal).reverse().toString();

        // 计算整数部分
        int integerMaxLen = integerF.length() > integerS.length() ? integerF.length() : integerS.length();
        for (int i = 0; i < integerMaxLen; i++) {
            cf = i < integerF.length() ? integerF.substring(i, i + 1) : "0";
            cs = i < integerS.length() ? integerS.substring(i, i + 1) : "0";
            sumc += Integer.parseInt(cf) + Integer.parseInt(cs);
            sumInteger += sumc % 10;
            sumc = sumc / 10;
        }
        if (sumc > 0) {
            sumInteger += sumc;
        }
        sumInteger = new StringBuffer(sumInteger).reverse().toString();

        if (TextUtils.isEmpty(sumDecimal)) {
            return sumInteger;
        } else {
            return sumInteger + "." + sumDecimal;
        }
    }

}
