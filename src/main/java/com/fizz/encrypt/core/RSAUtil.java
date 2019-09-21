package com.fizz.encrypt.core;

import com.fizz.file.core.FileUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RSAUtil {

    private static final String ALGORITHM = "RSA";
    private static final Integer LEN = 1024;

    /**
     * 生成RSA加密所需的公钥/私钥(存入文件中)
     * @throws Exception
     */
    public static Map<String, Object> generateKeyPair(Integer length) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Map<String, Object> map = new HashMap<>();
        if (length == null || length <= 0) {
            length = LEN;
        }

        /** RSA算法要求有一个可信任的随机数源 */
        SecureRandom sr = new SecureRandom();
        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        kpg.initialize(length, sr);
        /** 生成密匙对 */
        KeyPair kp = kpg.generateKeyPair();
        /** 获取公钥 */
        Key publicKey = kp.getPublic();
        /** 获取私钥 */
        Key privateKey = kp.getPrivate();
        map.put("publicKey", publicKey);
        map.put("privateKey", privateKey);

        /** base64编码 */
        String publicKey64 = Base64Util.encodeByJDK(publicKey.getEncoded());
        String privateKey64 = Base64Util.encodeByJDK(privateKey.getEncoded());
        map.put("publicKeyStr", publicKey64);
        map.put("privateKeyStr", privateKey64);

        /** 生成公钥/私钥组成参数 */
        BigInteger modulus = null;
        BigInteger exponent = null;
        KeyFactory keyFact = KeyFactory.getInstance(ALGORITHM);
        RSAPublicKeySpec keySpec = keyFact.getKeySpec(publicKey, RSAPublicKeySpec.class);
        modulus = keySpec.getModulus();
        exponent = keySpec.getPublicExponent();
        map.put("publicModulus", modulus);
        map.put("publicExponent", exponent);
        RSAPrivateCrtKeySpec privateKeySpec = keyFact.getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
        BigInteger privateModulus = privateKeySpec.getModulus();
        BigInteger privateExponent = privateKeySpec.getPrivateExponent();
        map.put("privateModulus", privateModulus);
        map.put("privateExponent", privateExponent);

        return map;
    }

    /**
     * 使用公钥加密
     * @param strMing 明文
     * @param publicKey 公钥字符串
     * @return
     * @throws Exception
     */
    public static String encryptByPub(String strMing, String publicKey) throws Exception {
        RSAPublicKey key = getPublicKey(publicKey);
        return encrypt(strMing, key);
    }

    /**
     * 使用私钥加密
     * @param strMi 密文
     * @param privateKey 私钥字符串
     * @return
     * @throws Exception
     */
    public static String encryptByPri(String strMi, String privateKey) throws Exception {
        RSAPrivateKey key = getPrivateKey(privateKey);
        return encrypt(strMi, key);
    }

    /**
     * 使用公钥解密
     * @param strMi 密文
     * @param publicKey 公钥字符串
     * @return
     * @throws Exception
     */
    public static String decryptByPub(String strMi, String publicKey) throws Exception {
        RSAPublicKey key = getPublicKey(publicKey);
        return decrypt(strMi, key);
    }

    /**
     * 使用私钥解密
     * @param strMi 密文
     * @param privateKey 私钥字符串
     * @return
     * @throws Exception
     */
    public static String decryptByPri(String strMi, String privateKey) throws Exception {
        RSAPrivateKey key = getPrivateKey(privateKey);
        return decrypt(strMi, key);
    }

    /**
     * 获取公钥实体
     * @param publicKey 公钥字符串(base64加密)
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        byte[] keyByte = Base64Util.decodeByJDK2(publicKey.getBytes());
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByte);
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 获取私钥实体
     * @param privateKey 私钥字符串(base64加密)
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        byte[] keyByte = Base64Util.decodeByJDK2(privateKey.getBytes());
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByte);
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公钥/私钥加密
     * @param strMing 明文
     * @param key 秘钥实体
     * @return 密文(base64)
     * @throws Exception
     */
    private static String encrypt(String strMing, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] str_mi = cipher.doFinal(strMing.getBytes());
        return new String(Base64Util.encode(str_mi));
    }

    /**
     * 公钥/秘钥解密
     * @param strMi 密文(base64)
     * @param key 秘钥实体
     * @return 明文
     * @throws Exception
     */
    public static String decrypt(String strMi, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] b = Base64Util.decode(strMi.toCharArray());
        byte[] str_ming = cipher.doFinal(b);
        return new String(str_ming);
    }

    public static void main (String[] args) {
        try {
            Map<String, Object> map_key = generateKeyPair(1024);
            String mi = encryptByPub("fizz", (String) map_key.get("publicKeyStr"));
            System.out.println("加密后：" + mi);
            String ming = decryptByPri(mi, (String) map_key.get("privateKeyStr"));
            System.out.println("解密后：" + ming);

            /*String mi = "Hj4XvdknJxBQo2Ua8WCWVnbKvMC1hHxJVRx2dH9UXxuz1NR21++MCIg00dNIVj/Nkd8Syepw0OjWbNe2TN6JxEO7XPpCYl6DMrfYouuxIkpdVhR6rOJM2mj5MtzzqKUabHo6xy908Qm5DCSNRVjLbzaHgSEJKoiUAYJfeMQW/yA=";
            String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMT12sgFvO4kE8SQXJSVmY8PdqAzxGENAjTH3lEgrY60pblfxcE7w8r9JYyYxEWXZOgWK4HyL5nqvCKxD6igJRy4wuOuUjY6LA8S/gYYUG9ffJ74Y+oOUNQojYHG5W/dR8U2GPb05Obpf3CEip9fQlkmLb0vabS7fGtXJ91O6Lx1AgMBAAECgYAYpAVt9sHPLTZH5iOe9yRR8IZPvoo39g+rmMGodqUf8Fu3WFL+PMLe91KmR0puxmT6YCOrSyhHYkWNNYeW+goKWFZo0MWkQHHbFjaHd/PlrFsRfgR0vbbThF2IMfGsmiPUmwSbp2gZAWqMZEBZrMUoclcOSd4RZE4cZBB7Ja8zAQJBAPm5zFX/5jJ6geue0KJe++uGQO5xOFIuQzsZUBbC8Ej4nPLcj7QXW0rCbBbbq8c2LLlReQSbQP0PNhCcQcxiskECQQDJ6K02FiUAakh91125R8uOqpfUnsYGVcvfBvUZlHpG9J4aq5GPblUmiuDvDLesMRqcxcQPcZX787pIDE2qOpU1AkBfI7H9cai5woKMycyij51adaN3NBtI8+cZNOOWSF5S7qScwfrdmahmKvwTGU2wb6tKTwfUAXIY9xqZ6W4H9/IBAkBnToGxdssf10GYAaWWs0FXkbl1tHclrqhNSMYHfIzD2U074CTt+knfGpfUN/GSTN2M8TNLo5ZV/3KEgqcKPkcxAkEAwOCj/1TExgPJ+lTl/yvtG3Tc/Udv0B96jXIky+XY0eLFea30N4SWhi4SO6dIkpAXix3zmhtFKcYrBhr2WoDyMw==";
            String ming = decrypt(mi, privateKey);
            System.out.println(ming);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
