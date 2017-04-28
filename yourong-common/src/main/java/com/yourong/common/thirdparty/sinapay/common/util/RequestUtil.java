package com.yourong.common.thirdparty.sinapay.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.thirdparty.sinapay.common.util.httpclient.HttpProtocolHandler;
import com.yourong.common.thirdparty.sinapay.common.util.httpclient.HttpRequest;
import com.yourong.common.thirdparty.sinapay.common.util.httpclient.HttpResponse;
import com.yourong.common.thirdparty.sinapay.common.util.httpclient.HttpResultType;
import com.yourong.common.thirdparty.sinapay.common.util.sign.MD5;
import com.yourong.common.thirdparty.sinapay.common.util.sign.RSA;

/* *
 * 工具类
 *
 */

public class RequestUtil {

	private static Logger logger = LoggerFactory.getLogger(RequestUtil.class);

    public static final int CONNECT_TIME_OUT = 30000;

    public static final int TIME_OUT         = 30000;

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray
     *            签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
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
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        String charset = params.get("_input_charset");
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (encode) {
                try {
                    value = URLEncoder.encode(value, charset);
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
    public static String buildRequestByMD5(Map<String, String> sPara, String key,
                                           String inputCharset) throws Exception {
        String prestr = createLinkString(sPara, false); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = MD5.sign(prestr, key, inputCharset);
        return mysign;
    }

    /**
     * 生成RSA签名结果
     *
     * @param sPara
     *            要签名的数组
     * @return 签名结果字符串
     */
    public static String buildRequestByRSA(Map<String, String> sPara, String privateKey,
                                           String inputCharset) throws Exception {
        String prestr = createLinkString(sPara, false); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String  mysign = RSA.sign(prestr, privateKey, inputCharset);
		logger.info("请求新浪接口生成签名参数:param={}, mysign={}", prestr, mysign);
        return mysign;
    }

    /**
     * 生成要请求给钱包的参数数组
     *
     * @param sParaTemp         请求前的参数数组
     * @return                  要请求的参数数组
     */
    public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp,
                                                       String signType, String key, String version,
                                                       String inputCharset) throws Exception {
        // 除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        // 生成签名结果
       // String mysign = "";
//        if ("MD5".equalsIgnoreCase(signType)) {
//            mysign = buildRequestByMD5(sPara, key, inputCharset);
//        } else if ("RSA".equalsIgnoreCase(signType)) {
          String   mysign = buildRequestByRSA(sPara, key, inputCharset);
//        }

        // 签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", "RSA");
        sPara.put("sign_version", version);

        return sPara;
    }

    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取钱包的处理结果
     * 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值 如：buildRequest("",
     * "",sParaTemp)
     *
     * @param strParaFileName
     *            文件类型的参数名
     * @param strFilePath
     *            文件路径
     * @param sParaTemp
     *            请求参数数组
     * @return 钱包处理结果
     * @throws Exception
     */
    public static String buildRequest(String url, Map<String, String> sParaTemp, String signType,
                                      String key, String version, String inputCharset)
                                                                                      throws Exception {
        // 待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp, signType, key, version,
            inputCharset);

        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();

        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        // 设置编码集
        request.setCharset(inputCharset);

        request.setMethod(HttpRequest.METHOD_POST);
        request.setConnectionTimeout(CONNECT_TIME_OUT);
        request.setTimeout(TIME_OUT);

        request.setParameters(generatNameValuePair(sPara, inputCharset));
        request.setUrl(url);

        HttpResponse response = httpProtocolHandler.execute(request, null, null);
        if (response == null) {
            return null;
        }
        return URLDecoder.decode(response.getStringResult(), inputCharset);
    }

    /**
     * MAP类型数组转换成NameValuePair类型
     *
     * @param properties
     *            MAP类型数组
     * @return NameValuePair类型数组
     */
    private static NameValuePair[] generatNameValuePair(Map<String, String> properties,
                                                        String charset) throws Exception {
		NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), URLEncoder.encode(
                entry.getValue(), charset));

        }

        return nameValuePair;
    }



    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取钱包的处理结果,异步处理
     * @param sParaTemp
     *            请求参数数组
     * @return 钱包处理结果
     * @throws Exception
     */
    public static boolean asynbuildRequest(String url, Map<String, String> sParaTemp, String signType,
                                      String key, String version, String inputCharset)
            throws Exception {
        // 待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp, signType, key, version,
                inputCharset);

        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();

        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        // 设置编码集
        request.setCharset(inputCharset);

        request.setMethod(HttpRequest.METHOD_POST);
        request.setConnectionTimeout(CONNECT_TIME_OUT);
        request.setTimeout(TIME_OUT);
        request.setParameters(generatNameValuePair(sPara, inputCharset));
        request.setUrl(url);
         httpProtocolHandler.asynExecute(request, null, null);
        boolean  result = true;
        return result;
    }

}
