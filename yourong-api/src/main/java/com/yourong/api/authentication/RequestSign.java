package com.yourong.api.authentication;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.thirdparty.sinapay.common.util.sign.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Administrator on 2015/3/22.
 */
public class RequestSign {
    /**
     * 日志对象
     */
    protected static Logger logger = LoggerFactory.getLogger(RequestSign.class);

    private static final String UTF_8 = "utf-8";

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray
     *            签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String[]> sArray) {
        Map<String, String> result = Maps.newHashMap();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = buildStringArray(sArray.get(key));
            
            
            if (value == null || ("").equals(value) || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type") || key.equalsIgnoreCase("avatar")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
    private static  String  buildStringArray(String[] array){
        StringBuffer  s = new StringBuffer();
        for (String temp :array){
            s.append(temp);
        }
        return s.toString();

    }



    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params
     *            需要排序并参与字符拼接的参数组
     * @param encode 是否需要urlEncode
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params, boolean encode) {
        List<String> keys = Lists.newArrayList(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (encode) {
                try {
                    value = URLEncoder.encode(value, UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    /**
     * 生成MD5签名结果
     *
     * @param sPara
     *            要签名的数组
     * @return 签名结果字符串
     */
    public static String buildRequestByMD5(Map<String, String> sPara, String key ) throws Exception {
        String prestr = createLinkString(sPara, false); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        logger.debug("要签名的字符串----"+prestr);
        String mysign = "";
        mysign = MD5.sign(prestr, key, UTF_8);
        return mysign;
    }



}
