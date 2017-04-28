package com.yourong.common.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * 加密、签名
 * 
 * @author j.w
 */
public class CryptUtil {

	private static final Logger logger = LogManager.getLogger(CryptUtil.class);

	private final static String charset = "utf-8";

	private final static String signType = "MD5";

	private final static String encryptType = "AES";

	private final static int keySize = 128;

	/**
	 * MD5签名
	 */
	public static String getMD5Sign(String content) {
		try {
			byte[] bytes = content.getBytes(charset);
			byte[] output = getDigest(signType).digest(bytes);
			return bytes2HexStr(output);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * AES加密
	 * 
	 * @param content
	 *            内容
	 * @param password
	 *            私钥
	 * @return String
	 */
	public static String getAesEncrypt(String content, String password) {
		logger.info(content);
		return bytes2HexStr(encrypt(content, password));
	}

	private static byte[] encrypt(String content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(encryptType);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(password.getBytes());
			kgen.init(keySize, random);
			// kgen.init(keySize, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, encryptType);
			Cipher cipher = Cipher.getInstance(encryptType);
			byte[] byteContent = content.getBytes(charset);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(byteContent);
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 根据加密方法名称获取加密摘要对象 */
	public final static MessageDigest getDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private final static String bytes2HexStr(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (byte b : bytes)
			sb.append(byte2Hex(b, false));

		return sb.toString();
	}

	private final static char[] byte2Hex(byte b, boolean capital) {
		byte bh = (byte) (b >>> 4 & 0xF);
		byte bl = (byte) (b & 0xF);

		return new char[] { halfByte2Hex(bh, capital),
				halfByte2Hex(bl, capital) };
	}

	private final static char halfByte2Hex(byte b, boolean capital) {
		return (char) (b <= 9 ? b + '0' : (capital ? b + 'A' - 0xA
				: b + 'a' - 0xA));
	}

}
