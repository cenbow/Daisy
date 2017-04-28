package com.yourong.common.thirdparty.sinapay.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;

import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.common.util.httpclient.HttpProtocolHandler;
import com.yourong.common.thirdparty.sinapay.common.util.httpclient.HttpRequest;
import com.yourong.common.thirdparty.sinapay.common.util.httpclient.HttpResponse;
import com.yourong.common.thirdparty.sinapay.common.util.httpclient.HttpResultType;

/**
 * <p>Http请求工具包</p>
 * @author Wallis Wang
 * @version $Id: HttpRequestUtil.java, v 0.1 2014年5月16日 下午2:25:02 wangqiang Exp $
 */
public class RequestWrapperUtil {

    public static final int CONNECT_TIME_OUT = 10000;

    public static final int TIME_OUT         = 10000;

    /**
     * 发送POST请求
     * @param url 请求的URL
     * @param requestMap 请求的数据
     * @param charsetType 字符集类型
     * @return
     * @throws IOException 
     * @throws HttpException 
     * @throws IllegalArgumentException 
     * @throws UnsupportedEncodingException 
     * @throws Exception
     */
    public static String sendRequest(String url, CharsetType charsetType,
                                     Map<String, String> requestMap)
                                                                    throws UnsupportedEncodingException,
                                                                    IllegalArgumentException,
                                                                    HttpException, IOException {
        HttpRequest httpRequest = new HttpRequest(HttpResultType.BYTES);
        httpRequest.setCharset(charsetType.charset());
        httpRequest.setMethod(HttpRequest.METHOD_POST);
        httpRequest.setConnectionTimeout(CONNECT_TIME_OUT);
        httpRequest.setTimeout(TIME_OUT);
        httpRequest.setUrl(url);

        httpRequest.setParameters(map2NameValuePair(requestMap, charsetType));

        HttpProtocolHandler protocolHandler = HttpProtocolHandler.getInstance();
        HttpResponse response = protocolHandler.execute(httpRequest, null, null);
        if (response == null)
            return null;

        return URLDecoder.decode(response.getStringResult(), charsetType.charset());
    }

    /**
     * MAP类型数组转换成NameValuePair类型
     *
     * @param properties
     *            MAP类型数组
     * @return NameValuePair类型数组
     * @throws UnsupportedEncodingException 
     */
    private static NameValuePair[] map2NameValuePair(Map<String, String> properties,
                                                     CharsetType charset)
                                                                         throws UnsupportedEncodingException {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), URLEncoder.encode(
                entry.getValue(), charset.charset()));
        }

        return nameValuePair;
    }
}
