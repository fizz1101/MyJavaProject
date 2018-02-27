package com.fizz.encrypt.core;

import java.io.UnsupportedEncodingException;

/**
 * DES加密工具类
 */
public class DESUtil {

    private static String ALGORITHM = "DES";

    /**
     * 加密字符串
     * @param strMing 需加密的字符串
     * @param key 秘钥
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String encrypt(String strMing, String key) {
        String result = null;
        try {
            CipherUtil cipherUtil = new CipherUtil(strMing, null, key, ALGORITHM);
            byte[] mi = cipherUtil.encrypt();
            result = new String(Base64Util.encodeByJDK(mi));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解密字节串
     * @param strMi 需解密的字符串
     * @param key 秘钥
     * @return 解密后的字节数组
     * @throws Exception
     */
    public static String decrypt(String strMi, String key) {
        String result = null;
        try {
            byte[] byteMi = Base64Util.decodeByJDK2(strMi.getBytes());
            CipherUtil cipherUtil = new CipherUtil(null, byteMi, key, ALGORITHM);
            result = cipherUtil.decrypt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "fizz";
        String key = "921101";

        System.out.println(encrypt(str, key));
       /* String mi = "eb64a3a8d528cc78";
        System.out.println(decrypt(mi, key));*/
    }

}
