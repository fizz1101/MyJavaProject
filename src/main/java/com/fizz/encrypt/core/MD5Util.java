package com.fizz.encrypt.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 */
public class MD5Util {

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 获取16位MD5值(小写)
     * @param strMing   明文字符串
     * @return
     */
    public static String get16MD5(String strMing) {
        return formatMD5(strMing, 16, false);
    }

    /**
     * 获取16位MD5值(大写)
     * @param strMing   明文字符串
     * @return
     */
    public static String get16MD5Upper(String strMing) {
        return formatMD5(strMing, 16, true);
    }

    /**
     * 获取32位MD5值(小写)
     * @param strMing   明文字符串
     * @return
     */
    public static String get32MD5(String strMing) {
        return formatMD5(strMing, 32, false);
    }

    /**
     * 获取32位MD5值(大写)
     * @param strMing   明文字符串
     * @return
     */
    public static String get32MD5Upper(String strMing) {
        return formatMD5(strMing, 32, true);
    }

    /**
     * 获取不同格式MD5值
     * @param strMing   明文字符串
     * @param len   长度
     * @param upper 大小写
     * @return
     */
    public static String formatMD5(String strMing, int len, boolean upper) {
        String strMi = encrypt(strMing);
        if (len == 16) {
            strMi = strMi.substring(8, 24);
        }
        if (upper) {
            strMi.toUpperCase();
        }
        return strMi;
    }

    /**
     * 调用MessageDigest方法实现MD5加密
     * @param strMing   明文字符串
     * @return
     */
    private static String encrypt(String strMing) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(strMing.getBytes());
        byte[] byteMi = md5.digest();
        return byte2str(byteMi);
    }

    /**
     * 将字节数组转换成十六进制字符串
     * @param byteMi    密文数组
     * @return
     */
    private static String byte2str(byte[] byteMi) {
        StringBuffer buf = new StringBuffer();
        int len = byteMi.length;
        for (int offset=0; offset<len; offset++) {
            byte b = byteMi[offset];
            buf.append(HEX_CHAR[b >>> 4 & 0xf]);
            buf.append(HEX_CHAR[b & 0xf]);
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        System.out.println(get16MD5("fizz"));
    }

}
