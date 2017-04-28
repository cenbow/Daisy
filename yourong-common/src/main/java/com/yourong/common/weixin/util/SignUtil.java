package com.yourong.common.weixin.util;

import com.yourong.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 绑定服务器工具类
 *
 * @author peiyu
 */
public final class SignUtil {

    private static final Logger LOG   = LoggerFactory.getLogger(SignUtil.class);
    private static final char[] digit = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 此类不需要实例化
     */
    private SignUtil() {
    }

    /**
     * 认证微信，可以参见微信开发者文档
     *
     * @param token     我们自己设定的token值
     * @param signature 微信传来的变量
     * @param timestamp 微信传来的变量
     * @param nonce     微信传来的变量
     * @return 是否合法
     */
    public static boolean checkSignature(String token, String signature,
                                         String timestamp, String nonce) {
        if (hasBlank(token, signature, timestamp, nonce)) {
            return false;
        }
        String[] arr = new String[]{token, timestamp, nonce};
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (String anArr : arr) {
            content.append(anArr);
        }
        MessageDigest md;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.toString().getBytes("UTF-8"));
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("加密方式异常", e);
        } catch (UnsupportedEncodingException e) {
            LOG.error("编码格式不支持", e);
        }
        return tmpStr != null && tmpStr.equalsIgnoreCase(signature);
    }

    /**
     * 将byte数组变为16进制对应的字符串
     *
     * @param byteArray byte数组
     * @return 转换结果
     */
    private static String byteToStr(byte[] byteArray) {
        int len = byteArray.length;
        StringBuilder strDigest = new StringBuilder(len * 2);
        for (byte aByteArray : byteArray) {
            strDigest.append(byteToHexStr(aByteArray));
        }
        return strDigest.toString();
    }

    private static String byteToHexStr(byte mByte) {
        char[] tempArr = new char[2];
        tempArr[0] = digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = digit[mByte & 0X0F];
        return new String(tempArr);
    }
    /**
     * 判断一组字符串是否有空值
     *
     * @param strs 需要判断的一组字符串
     * @return 判断结果，只要其中一个字符串为null或者为空，就返回true
     */
    public static boolean hasBlank(String... strs) {
        if (null == strs || 0 == strs.length) {
            return true;
        } else {
            //这种代码如果用java8就会很优雅了
            for (String str : strs) {
                if (StringUtil.isBlank(str)) {
                    return true;
                }
            }
        }
        return false;
    }
}
