package com.yourong.common.thirdparty.sinapay.pay.core.common;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.common.util.sign.RSA;

/**
 * <p>RSA加签工具扩展</p>
 * @author Wallis Wang
 * @version $Id: RSAExtend.java, v 0.1 2014年7月1日 下午4:07:14 wangqiang Exp $
 */
public class RSAExtend {

    /**
     * 对内容进行RSA加密
     * @param original
     * @param charsetType
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    public static String crytionByRSA(String original, CharsetType charsetType,
                                      String payfrontPublicKey) {
        if (StringUtils.isBlank(original))
            return StringUtils.EMPTY;
        try {
            byte[] decryptByPublicKey = RSA.encryptByPublicKey(
                original.getBytes(charsetType.charset()), payfrontPublicKey);
            return Base64.encodeBase64String(decryptByPublicKey);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }
}
