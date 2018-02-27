package com.fizz.encrypt.core;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CipherUtil {

    private String strMing;
    private byte[] byteMi;
    private String key;
    private String algorithm;

    private Cipher cipher;
    private KeyGenerator keyGenerator;

    public CipherUtil() {
    }

    public CipherUtil(String strMing, byte[] byteMi, String key, String algorithm) {
        this.strMing = strMing;
        this.byteMi = byteMi;
        this.key = key;
        this.algorithm = algorithm;
    }

    /**
     * 加密
     * @return
     */
    public byte[] encrypt() {
        byte[] byteMi= null;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, createKey());
            byteMi = cipher.doFinal(strMing.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteMi;
    }

    /**
     * 解密
     * @return
     */
    public String decrypt() {
        String ming = null;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, createKey());
            byte[] byteMing = cipher.doFinal(byteMi);
            ming = new String(byteMing, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ming;
    }

    /**
     * 生成秘钥
     * @return
     */
    public Key createKey() {
        try {
            keyGenerator = KeyGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (key != null && !"".equals(key)) {
            keyGenerator.init(getKeyLen(), new SecureRandom(key.getBytes()));
        } else {
            keyGenerator.init(getKeyLen());
        }
        return keyGenerator.generateKey();
    }

    /**
     * 秘钥长度
     * @return
     */
    public Integer getKeyLen() {
        int len = 0;
        switch (algorithm) {
            case "AES":
                len = 128;
                break;
            case "AES/ECB/PKCS5Padding":
                len = 128;
                break;
            case "AES/ECB/PKCS7Padding":
                len = 256;
                break;
            case "DES":
                len = 56;
                break;
            case "DES/CBC/PKCS5Padding":
                len = 56;
                break;
        }
        return len;
    }

    public static void main(String[] args) throws Exception {
        CipherUtil cipherUtil = new CipherUtil();

    }

}
