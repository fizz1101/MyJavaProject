package com.fizz.encrypt.core;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

/**
 * AES加密工具类
 */
public class AESUtil {

    private static boolean initialized = false;
    private static String ALGORITHM = "AES";
    private static String ALGORITHM_MODE_PKCS5  = "AES/ECB/PKCS5Padding";
    private static String ALGORITHM_MODE_PKCS7  = "AES/ECB/PKCS7Padding";
    private static String BOUNCYCASTLE = "BC";  //PKCS7Padding算法库


    /**
     * AES加密
     * @param strMing  要被加密的字符串
     * @param key  加/解密要用的长度为16的字节数组（256位）密钥
     * @return  加密后的字节数组
     */
    public static String AESEncrypt(String strMing, String key){
        String result = null;
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PKCS5);
            byte[] byteMing = strMing.getBytes();
            byte[] byteKey = getKeyByte(key, 16);
            result = BaseEncrypt(cipher, byteMing, byteKey);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * AES解密
     * @param strMi  要被解密的字符串
     * @param key    加/解密要用的长度为16的字节数组（256位）密钥
     * @return  解密后的字符串
     */
    public static String AESDecrypt(String strMi, String key){
        String result = null;
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PKCS5);
            byte[] byteMi = strMi.getBytes();
            byte[] byteKey = getKeyByte(key, 16);
            result = BaseDecrypt(cipher, byteMi, byteKey);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }


    /**
     * AES256-ECB-PKCS7Padding加密
     * 使用PKCS7Padding算法须引入库：BOUNCYCASTLE
     * 使用256位长度秘钥须替换jdk下原jar包
     * 路径：%JAVA_HOME%/jre/lib/security/
     * 文件：local_policy.jar、US_export_policy.jar
     * 来源：http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     */

    /**
     * AES256加密
     * @param str  要被加密的字符串
     * @param key  加/解密要用的长度为32的字节数组（256位）密钥
     * @return  加密后的字符串
     */
    public static String AES256Encrypt(String str, String key){
        initialize();
        String result = null;
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PKCS7, BOUNCYCASTLE);
            byte[] byteSrc = str.getBytes();
            byte[] byteKey = getKeyByte(key, 32);
            result = BaseEncrypt(cipher, byteSrc, byteKey);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * AES256解密
     * @param strMi  要被解密的字符串
     * @param key    加/解密要用的长度为32的字节数组（256位）密钥
     * @return  解密后的字符串
     */
    public static String AES256Decrypt(String strMi, String key){
        initialize();
        String result = null;
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PKCS7, BOUNCYCASTLE);
            byte[] byteMi = strMi.getBytes();
            byte[] byteKey = getKeyByte(key, 32);
            result = BaseDecrypt(cipher, byteMi, byteKey);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 初始化：加载BouncyCastle库(包含AES/ECB/PKCS7Padding算法)
     */
    private static void initialize() {
        if (!initialized) {
            Security.addProvider(new BouncyCastleProvider());
            initialized = true;
        }
    }

    /**
     * 加密算法
     * @param cipher
     * @param src
     * @param key
     * @return
     */
    private static String BaseEncrypt(Cipher cipher, byte[] src, byte[] key) {
        String result = null;
        try{
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM); //生成加密解密需要的Key
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encode = cipher.doFinal(src);
            result = parseByte2HexStr(encode);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解密算法
     * @param cipher
     * @param bytes
     * @param key
     * @return
     */
    public static String BaseDecrypt(Cipher cipher, byte[] bytes, byte[] key){
        initialize();
        String result = null;
        try{
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM); //生成加密解密需要的Key
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = cipher.doFinal(bytes);
            result = new String(decoded, "UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成key
     * @param key
     * @return
     * @throws Exception
     */
    private static byte[] getKeyByte(String key, int len) throws Exception {
        byte[] seed = new byte[len];
        if(key!=null && !"".equals(key)) {
            seed = key.getBytes();
        }
        return seed;
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    @Deprecated
    private static SecretKeySpec getSecretKey(final String password) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(ALGORITHM);
            //AES 要求密钥长度为 128
            kg.init(128, new SecureRandom(password.getBytes()));
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);// 转换为AES专用密钥
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * byte[]转化为16进制String
     * @param buf
     * @return
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     * @param hexStr
     * @return
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) {
        String strMing = "fizz";
        String key = "2IBtBXdrqC3kCBs4gaceL7nl2nnFadQv";
        System.out.println(AESEncrypt(strMing, key));
    }

}
