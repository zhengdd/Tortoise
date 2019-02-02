package com.dongdong.animal.tortoise.confusion;

/**
 * Created by dongdongzheng on 2019/1/23.
 */

public class ConfusionUtil {

    /**
     * 加密混淆
     * @param in 原文
     * @return 混淆生成对象
     */
    public static ConfusionOut encode(String in) {
        int len = in.length();
        char[] inArray = in.toCharArray();
        int[] outArray = new int[len];
        int vector = (int) (Math.random() * (8) + 1);
        for (int i = 0; i < len; i++) {
            int v = (((int) inArray[i]) << vector);
            outArray[i] = v ^ vector;
        }
        ConfusionOut out = new ConfusionOut();
        out.setLength(len);
        out.setVector(vector);
        out.setArray(outArray);

        return out;

    }


    /**
     * 解密
     *把对象解析成字符串
     * @param in 混淆后的对象
     * @return 原文
     */
    public static String decode(ConfusionOut in) {
        int len = in.getLength();
        int vector = in.getVector();
        char[] outArray = new char[len];
        int[] inArray = in.getArray();
        for (int i = 0; i < in.getLength(); i++) {
            int v = inArray[i] ^ vector;
            outArray[i] = (char) (v >> vector);
        }
        String outStr = String.valueOf(outArray);
        return outStr;
    }


}
