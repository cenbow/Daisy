package com.yourong.common.util;

import com.yourong.common.thirdparty.sinapay.common.util.sign.RSA;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public class RSAUtils {
//    public static final String KEY_SHA = "SHA";
//    public static final String KEY_MD5 = "MD5";
//    public static final String KEY_ALGORITHM = "RSA";
//    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
//    public static final String keyFile = "D:/key/rsa_public.pem";

//    /**
//     * 用私钥对信息生成数字签名
//     *
//     * @param data 加密数据
//     * @param privateKey 私钥
//     * @return
//     * @throws Exception
//     */
//    public static String sign(byte[] data, PrivateKey privateKey) throws Exception {  
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//        signature.initSign(privateKey);
//        signature.update(data);
//        return encryptBASE64(signature.sign());
//    }
//
//    /**
//     * 校验数字签名
//     *
//     * @param data 加密数据
//     * @param publicKey 公钥
//     * @param sign 数字签名
//     * @return 校验成功返回true 失败返回false
//     * @throws Exception
//     */
//    public static boolean verify(byte[] data, PublicKey publicKey, String sign) throws Exception {
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//        signature.initVerify(publicKey);
//        signature.update(data);
//        return signature.verify(decryptBASE64(sign));
//    }
//
//    /**
//     * 私钥解密
//     *
//     * @param data 密文
//     * @param PrivateKey 私钥
//     * @return
//     * @throws Exception
//     */
//    public static byte[] decryptByPrivateKey(byte[] data, PrivateKey privateKey) throws Exception {
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.DECRYPT_MODE, privateKey);
//        return cipher.doFinal(data);
//    }

//    /**
//     * 用公钥解密
//     *
//     * @param data 密文
//     * @param publicKey 公钥
//     * @return
//     * @throws Exception
//     */
//    public static byte[] decryptByPublicKey(byte[] data, PublicKey publicKey) throws Exception {
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.DECRYPT_MODE, publicKey);
//        return cipher.doFinal(data);
//    }
//
//    /**
//     * 用公钥加密
//     *
//     * @param data 明文
//     * @param PublicKey 公钥
//     * @return
//     * @throws Exception
//     */
//    public static byte[] encryptByPublicKey(byte[] data, PublicKey publicKey) throws Exception {
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//        return cipher.doFinal(data);
//    }
//
//    /**
//     * 用私钥加密
//     *
//     * @param data 明文
//     * @param privateKey 私钥
//     * @return
//     * @throws Exception
//     */
//    public static byte[] encryptByPrivateKey(byte[] data, PrivateKey privateKey) throws Exception {
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//        return cipher.doFinal(data);
//    }

//    public static PrivateKey getPrivateKeyFromPem() throws Exception {
//        BufferedReader br = new BufferedReader(new FileReader("e:/pkcs8_privatekey.pem"));
//        String s = br.readLine();
//        String str = "";
//        s = br.readLine();
//        while (s.charAt(0) != '-') {
//            str += s + "\r";
//            s = br.readLine();
//        }
//        BASE64Decoder base64decoder = new BASE64Decoder();
//        byte[] b = base64decoder.decodeBuffer(str);
//
//        // 生成私匙  
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(b);
//        PrivateKey privateKey = kf.generatePrivate(keySpec);
//        return privateKey;
//    }
//
//    public static PublicKey getPublicKeyFromPem() throws Exception {
//        BufferedReader br = new BufferedReader(new FileReader("e:/publickey.pem"));
//        String s = br.readLine();
//        String str = "";
//        s = br.readLine();
//        while (s.charAt(0) != '-') {
//            str += s + "\r";
//            s = br.readLine();
//        }
//        BASE64Decoder base64decoder = new BASE64Decoder();
//        byte[] b = base64decoder.decodeBuffer(str);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(b);
//        PublicKey pubKey = kf.generatePublic(keySpec);
//        return pubKey;
//    }
//    
//    public static byte[] decryptBASE64(String key) throws Exception {   
//        return (new BASE64Decoder()).decodeBuffer(key);   
//    }   
//  
//    public static String encryptBASE64(byte[] key) throws Exception {   
//        return (new BASE64Encoder()).encodeBuffer(key);   
//    }   

//    public static byte[] encryptMD5(byte[] data) throws Exception {
//
//        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
//        md5.update(data);
//
//        return md5.digest();
//
//    }
//
//    public static byte[] encryptSHA(byte[] data) throws Exception {
//
//        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
//        sha.update(data);
//
//        return sha.digest();
//
//    }
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;

    }

    public static void main(String[] args) throws Exception {
//        Map<String, Object> genKeyPair = genKeyPair();
//
//        String base64publicKey = getPublicKey(genKeyPair);
//        System.out.println("公钥 \n" + base64publicKey);
//        String base64privateKey = getPrivateKey(genKeyPair);
//        System.out.println("私钥\n" + base64privateKey);
//
//        String passwd = "cat123113";
//        String charsetName = "utf-8";
//

//
//        byte[] decryptByPrivateKey = decryptByPrivateKey(Base64.decodeBase64(encryptByPublicKey),
//            base64privateKey);
//        String string = new String(decryptByPrivateKey, "utf-8");
//        System.out.println("解密后\n" + string);
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMfWfZLv4b8GpnlAIjTne1s0fh78M2PY60uk+hoyAQPqyWmYMOkshQj49qRPBThemLPX2xXBzhkBFzHMUIDV0DjLn1RDqEV50p/t71GGf5/+x99ApPvpwtaUXKjkYGLWjhoX+ibd2S2auHuEbPkhb4lSKL5LTdIXeyiwEyBR5AatAgMBAAECgYEAkZbAkcZWDxfHPNrukOB8TGwn3mcbAYlNkyS0WVVIzBPHfTi1mYsnuOu4tOglA3ZXhd6i7Gzvu+PBhDQ4CkS7lvuNAflaqiFqIlSctVmo9J4/+XrfTw4RZr2eBK+DT+qPzqIGU1IA2ydPqcTFTIjRTDxrcTSvC52S7vl/X/4eBbkCQQDnHCaldi1so5KDT33Aa6xkxIEEcE2RqhOFmfNk9uNNFESSopp7KHFNWiqRwm6EjWAhpLGpi4z429ptpoVy1y/zAkEA3VwlxDesQmRWoces9re6jqzinzM5GQIp00uR+/mqsw1UA9+Dly4292e5vthNTnQMGTGyD67m48IvntC181w23wJANPS9YkKfC/q7Mr1/Oh0yBnEEgyjcjVkkWgO5wtPTp7DSOatqrHfK5oeXo4ii3FqswWjEkO36Inf2KBP1Fih6wwJBAI0DR5fA835z1vBGeDvO/QwovFE4W1ZAF47f5EaFXKWlhvDUsUpciW1/6UbXGiAxMwfVrZ3qCHHs4VMll9NI2jcCQF/cP6zsXE2A5X3bLCFzm6bqblDGGHWfjPJYHfItVrUDg4O7XZPUkawl4PDUHD8209VAyJ+BOocDiGLV6uSeF2U=";
        String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDH1n2S7+G/BqZ5QCI053tbNH4e/DNj2OtLpPoaMgED6slpmDDpLIUI+PakTwU4Xpiz19sVwc4ZARcxzFCA1dA4y59UQ6hFedKf7e9Rhn+f/sffQKT76cLWlFyo5GBi1o4aF/om3dktmrh7hGz5IW+JUii+S03SF3sosBMgUeQGrQIDAQAB";
      String s = "123456";
        String encryptByPublicKey = Base64.encodeBase64String((encryptByPrivateKey( s.getBytes(), privateKey)));
        System.out.println("加密\n" + encryptByPublicKey);
   //     a6TyrBD0JWWWpQ33mlkCVKABN5evmRotM6PLVayn9mkgjsasENnOHDMhf+3SoylvDsuHxGT786gWsIKiIRG0SmzGO+t/sTQXLVg3Q0s6UFJefRwY5DC64a0kkZF8Z1O/xlE0dpLntnHUmovF4ma/iQmvS6gmBz0a25fwA0MFCko=

//        String sign = RSA.sign("124234234", privateKey, "utf-8");
//        System.out.println(sign);
//        System.out.println(System.currentTimeMillis());
//        String sign1 = "Myva++yil1LVMyPFVKFgCz1tqsclN3g34K50EXxyn/ftBGXnNO6+FGHYBAFU2E/40Pful66C4DXzOTLYrtAA3jdU60Y8LzS5YU+quvvNxuUp/P+q5NqO330tc8Xnk5wPUWg7MyW0tJ9CQigWbxLjeAP1cGYe5wzvj4lgi6vOxoE=";
//        boolean verify = RSAUtils.verify("124234234", sign1, publickey, "utf-8");
//        System.out.println(System.currentTimeMillis());
//        System.out.println(verify);

    }

    /**
     * 签名字符串
     *
     * @param text       需要签名的字符串
     * @param privateKey 私钥(BASE64编码)
     * @param charset    编码格式
     * @return 签名结果(BASE64编码)
     */
    public static String sign(String text, String privateKey, String charset) throws Exception {

        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(getContentBytes(text, charset));
        byte[] result = signature.sign();
        return Base64.encodeBase64String(result);

    }

    public static String sign(String text, PrivateKey privateKey, String charset)
            throws SignatureException,
            InvalidKeyException {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(getContentBytes(text, charset));
            byte[] result = signature.sign();
            return Base64.encodeBase64String(result);
        } catch (NoSuchAlgorithmException e) {
            //不可能发生，
            return null;
        }
    }

    /**
     * 签名字符串
     *
     * @param text      需要签名的字符串
     * @param sign      客户签名结果
     * @param publicKey 公钥(BASE64编码)
     * @param charset   编码格式
     * @return 验签结果
     */
    public static boolean verify(String text, String sign, String publicKey, String charset)
            throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(getContentBytes(text, charset));
        return signature.verify(Base64.decodeBase64(sign));

    }

    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;

    }

    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;

    }

    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;

    }

    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data 源数据
     * @param cert 证书
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, java.security.cert.Certificate cert) throws Exception {

        // 对数据加密
        PublicKey uk = cert.getPublicKey();
        Cipher cipher = Cipher.getInstance(uk.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, uk);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;

    }

    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;

    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws java.io.UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }
}