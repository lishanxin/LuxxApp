package com.telink.sig.mesh.util;
import java.util.HashMap;

// 代码从原先的36改至64位
public class X64Trans {
    // 定义36进制数字
    private static final String X64 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz/+";
    // 定义静态进制数
    private static int BASE = X64.length();
    // 拿到36进制转换10进制的值键对
    private static HashMap<Character, Integer> thirysixToTen = createMapThirtysixToTen();
    // 拿到10进制转换36进制的值键对
    private static HashMap<Integer, Character> tenToThirtysix = createMapTenToThirtysix();



    private static HashMap<Character, Integer> createMapThirtysixToTen() {
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        for (int i = 0; i < BASE; i++) {
            // 0--0,... ..., Z -- 35的对应存放进去
            map.put(X64.charAt(i), i);
        }
        return map;
    }

    private static HashMap<Integer, Character> createMapTenToThirtysix() {
        HashMap<Integer, Character> map = new HashMap<Integer, Character>();
        for (int i = 0; i < BASE; i++) {
            // 0--0,... ..., 35 -- Z的对应存放进去
            map.put(i, X64.charAt(i));
        }
        return map;
    }

    /**
     * 十进制数转为特定进制数
     * @param iSrc 十进制数
     * @param radix 设置要转为的进制 1-64
     * @return 结果
     */
    public static String DeciamlToThirtySix(long iSrc, int radix) {
        String result = "";
        long key;
        long value;

        key = iSrc / radix;
        value = iSrc - key * radix;
        if (key != 0) {
            result = result + DeciamlToThirtySix(key, radix);
        }

        result = result + tenToThirtysix.get((int)value).toString();

        return result;
    }

    /**
     * 特定进制的字符串转为十进制数
     * @param pStr 特定进制的字符串
     * @param radix 进制 1-64
     * @return 十进制数
     */
    public static long ThirtysixToDeciamlLong(String pStr, int radix) {
        if (pStr == "")
            return 0;
        // 目标十进制数初始化为0
        long deciaml = 0;
        // 记录次方,初始为36进制长度 -1
        long power = pStr.length() - 1;
        // 将36进制字符串转换成char[]
        char[] keys = pStr.toCharArray();
        for (int i = 0; i < pStr.length(); i++) {
            // 拿到36进制对应的10进制数
            long value = thirysixToTen.get(keys[i]);
            deciaml = (long) (deciaml + value * Math.pow(radix, power));
            // 执行完毕 次方自减
            power--;
        }
        return deciaml;
    }


    public static void main(String[] args) {
        long x = ThirtysixToDeciamlLong("5F9970C7", 16);
        System.out.println(x);
        long time = System.currentTimeMillis() / 1000;
        System.out.println("time:" + time);
        String s = DeciamlToThirtySix(time, 16);
        System.out.println(s);

        System.out.println(Integer.valueOf("12", 16));
    }
}