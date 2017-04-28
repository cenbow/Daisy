package com.yourong.common.thirdparty.sinapay.common.util;

import java.util.Map;
import java.util.TreeMap;

import com.yourong.common.thirdparty.sinapay.common.util.sign.RSA;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.common.util.sign.MD5;
import com.yourong.common.thirdparty.sinapay.pay.exceptions.PayFrontException;

public class ResponseUtil {

    // 签名版本名称
    public static final String SIGN_VERSION_NAME = "sign_version";

    //签名类型名称
    public static final String SIGN_TYPE_NAME    = "sign_type";

    // 签名名称
    public static final String SIGN_NAME         = "sign";

    //字符集名称
    public static final String CHARSET_TYPE_NAME = "_input_charset";

    /**
     * Map中去除sign,sign_version,sign_type剩下的字符串
     * @param responseMap 
     * @return
     */
    private static String resloveContentFromJsonResponse(Map<String, String> responseMap) {
        TreeMap<String, String> resultMap = Maps.newTreeMap();

        if (responseMap != null && !responseMap.isEmpty()) {
            for (Map.Entry<String, String> entry : responseMap.entrySet()) {
                if (SIGN_NAME.equals(entry.getKey()) || SIGN_VERSION_NAME.equals(entry.getKey())
                    || SIGN_TYPE_NAME.equals(entry.getKey()))
                    continue;
                if (entry.getValue() == null || StringUtils.isBlank(entry.getValue()))
                    continue;

                resultMap.put(entry.getKey(), entry.getValue());
            }
            return RequestUtil.createLinkString(resultMap, false);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 验证返回的JSON结果中的签名
     * <p>
     * 1、验证签名出现异常抛出异常
     * 2、验证签名结果返回FALSE抛出异常
     * </p>
     * @param jsonResponse
     * @param charsetType
     * @throws PayFrontException
     */
    @SuppressWarnings("unchecked")
    public static void verifyResponseSign(String jsonResponse, CharsetType charsetType,
                                          String responseMd5Key) {
        try {
            Map<String, String> responseMapVal = (Map<String, String>) GsonUtil
                .fronJson2Map(jsonResponse);
            String contentFromJsonResponse = ResponseUtil
                .resloveContentFromJsonResponse(responseMapVal);
            boolean verifyResult = MD5.verify(contentFromJsonResponse,
                responseMapVal.get(SIGN_NAME), responseMd5Key,
                responseMapVal.get(CHARSET_TYPE_NAME));

            Preconditions.checkState(verifyResult, "签名验证失败,签名内容为:%s", contentFromJsonResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证返回的JSON结果中的签名 rsa 签名方式
     * <p>
     * 1、验证签名出现异常抛出异常
     * 2、验证签名结果返回FALSE抛出异常
     * </p>
     * @param jsonResponse
     * @param charsetType
     * @throws PayFrontException
     */
    @SuppressWarnings("unchecked")
    public static void verifyResponseSignbyRSA(String jsonResponse, CharsetType charsetType,
                                          String responseMd5Key) {
        try {
            Map<String, String> responseMapVal = (Map<String, String>) GsonUtil
                    .fronJson2Map(jsonResponse);
            String contentFromJsonResponse = ResponseUtil
                    .resloveContentFromJsonResponse(responseMapVal);
            boolean verifyResult = RSA.verify(contentFromJsonResponse, responseMapVal.get(SIGN_NAME),responseMd5Key, responseMapVal.get(CHARSET_TYPE_NAME));
            Preconditions.checkState(verifyResult, "签名验证失败,签名内容为:%s", contentFromJsonResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
