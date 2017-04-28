package com.yourong.common.util;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.common.util.ResponseUtil;
import com.yourong.common.thirdparty.sinapay.common.util.sign.RSA;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.enums.SinaPayEnum;

public class SignUtil {
	//
	private static final Logger LOGGER = LoggerFactory.getLogger(SignUtil.class);
	public static String generateSign(Map<String, String> params, String md5Key) {
		if (params != null && params.size() > 0) {
			StringBuffer signStr = buildSign(params);
			LOGGER.debug("生成签名，signStr：" +signStr + ", 安全码：" + md5Key);
			return DigestUtils.md5Hex(getContentBytes(signStr.append(md5Key).toString(), "utf-8"));
		}
		return null;
	}
	public static boolean generateSignByRsa(Map<String, String> params,String sign,String rsapublic) throws Exception{
		if (params != null && params.size() > 0) {
			StringBuffer signStr = buildSign(params);
			LOGGER.debug("生成签名，signStr：{}" ,signStr );
			boolean verify = RSA.verify(signStr.toString(), sign, rsapublic, CharsetType.UTF8.name());
			LOGGER.debug("验证签名，verify：{}" ,verify );
			return verify;
		}
		return false;
	}

	private static StringBuffer buildSign(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuffer prestr = new StringBuffer();
		String key = "";
		String value = "";
		for (int i = 0; i < keys.size(); i++) {
			key = (String) keys.get(i);
			value = (String) params.get(key);
			if ("null".equals(value) || StringUtil.isBlank(value)
					||key.equalsIgnoreCase(SinaPayEnum.PARAM_BASE_SIGN.getCode())|| 
					key.equalsIgnoreCase(SinaPayEnum.PARAM_BASE_SIGN_TYPE.getCode())||key.equalsIgnoreCase(SinaPayEnum.PARAM_BASE_SIGN_VERSION.getCode())) {
				continue;
			}
			prestr.append(key).append("=").append(value).append("&");
		}
		return prestr.deleteCharAt(prestr.length() - 1); 
	}
	
	 /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
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

}
