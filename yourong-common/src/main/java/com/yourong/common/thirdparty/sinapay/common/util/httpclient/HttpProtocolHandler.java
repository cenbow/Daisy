package com.yourong.common.thirdparty.sinapay.common.util.httpclient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.common.collect.Lists;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.http.*;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import org.apache.http.util.EntityUtils;

/* *
 *功能：HttpClient方式访问
 *详细：获取远程HTTP数据
 */

public class HttpProtocolHandler {

    private static String              DEFAULT_CHARSET                     = "UTF-8";

    /** 连接超时时间，由bean factory设置，缺省为8秒钟 */
    private int                        defaultConnectionTimeout            = 300000;

    /** 回应超时时间, 由bean factory设置，缺省为30秒钟 */
    private int                        defaultSoTimeout                    = 300000;

    /** 闲置连接超时时间, 由bean factory设置，缺省为60秒钟 */
    private int                        defaultIdleConnTimeout              = 60000;

    private int                        defaultMaxConnPerHost               = 30;

    private int                        defaultMaxTotalConn                 = 80;

    /** 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒*/
    private static final long          defaultHttpConnectionManagerTimeout = 3 * 1000;

    /**
     * HTTP连接管理器，该连接管理器必须是线程安全的.
     */
    private HttpConnectionManager      connectionManager;

    private static HttpProtocolHandler httpProtocolHandler                 = new HttpProtocolHandler();

    private static Logger logger       = LoggerFactory.getLogger(HttpProtocolHandler.class);
    /**
     * 工厂方法
     *
     * @return
     */
    public static HttpProtocolHandler getInstance() {
        return httpProtocolHandler;
    }

    /**
     * 私有的构造方法
     */
    private HttpProtocolHandler() {
        // 创建一个线程安全的HTTP连接池
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
        connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);

        IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        ict.addConnectionManager(connectionManager);
        ict.setConnectionTimeout(defaultIdleConnTimeout);

        ict.start();
    }
    /**
     * 执行Http请求
     *
     * @param request 请求数据
     * @return
     * @throws HttpException, IOException
     */
    public HttpResponse execute(HttpRequest request) throws HttpException, IOException {
        return execute(request, null , null);
    }
    /**
     * 执行Http请求
     *
     * @param request 请求数据
     * @param strParaFileName 文件类型的参数名
     * @param strFilePath 文件路径
     * @return
     * @throws IllegalArgumentException if request is null
     * @throws HttpException, IOException
     */
    public HttpResponse execute(HttpRequest request, String strParaFileName, String strFilePath)
                                                                                                throws IllegalArgumentException, HttpException,
                                                                                                IOException {
    	
    	if (logger.isDebugEnabled()) {
             logger.info("请求第三方支付接口,请求参数:",request);
        }
        HttpClient httpclient = new HttpClient(connectionManager);

        // 设置连接超时
        int connectionTimeout = defaultConnectionTimeout;
//        if (request.getConnectionTimeout() > 0) {
//            connectionTimeout = request.getConnectionTimeout();
//        }
        httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);

        // 设置回应超时
        int soTimeout = defaultSoTimeout;
//        if (request.getTimeout() > 0) {
//            soTimeout = request.getTimeout();
//        }
        httpclient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);

        // 设置等待ConnectionManager释放connection的时间
        httpclient.getParams().setConnectionManagerTimeout(defaultHttpConnectionManagerTimeout);

        String charset = request.getCharset();
        charset = charset == null ? DEFAULT_CHARSET : charset;
        HttpMethod method = null;

        //get模式且不带上传文件
        if (HttpRequest.METHOD_GET.equals(request.getMethod())) {
            method = new GetMethod(request.getUrl());
            method.getParams().setCredentialCharset(charset);

            // parseNotifyConfig会保证使用GET方法时，request一定使用QueryString
            method.setQueryString(request.getQueryString());
        } else if (StringUtil.isEmpty(strParaFileName) && StringUtil.isEmpty(strFilePath)) {
            //post模式且不带上传文件
            method = new PostMethod(request.getUrl());
            ((PostMethod) method).addParameters(request.getParameters());
            method.addRequestHeader("Content-Type",
                "application/x-www-form-urlencoded; text/html; charset=" + charset);
        } else {
            //post模式且带上传文件
            method = new PostMethod(request.getUrl());
            List<Part> parts = new ArrayList<Part>();
            for (int i = 0; i < request.getParameters().length; i++) {
                parts.add(new StringPart(request.getParameters()[i].getName(), request
                    .getParameters()[i].getValue(), charset));
            }
            //增加文件参数，strParaFileName是参数名，使用本地文件
            parts.add(new FilePart(strParaFileName, new FilePartSource(new File(strFilePath))));

            // 设置请求体
            ((PostMethod) method).setRequestEntity(new MultipartRequestEntity(parts
                .toArray(new Part[0]), new HttpMethodParams()));
        }

        // 设置Http Header中的User-Agent属性
        method.addRequestHeader("User-Agent", "Mozilla/4.0");

        try {
            HttpResponse response = new HttpResponse();
            httpclient.executeMethod(method);

            if (request.getResultType().equals(HttpResultType.STRING)) {
                response.setStringResult(method.getResponseBodyAsString());
                if (logger.isDebugEnabled()) {
                    logger.info("请求第三方支付接口,响应结果:",method.getResponseBodyAsString());
                }                
            } else if (request.getResultType().equals(HttpResultType.BYTES)) {
                response.setByteResult(method.getResponseBody());  
                if (logger.isDebugEnabled()) {
                    logger.info("请求第三方支付接口,响应结果:",method.getResponseBody());
                }
            }
            response.setResponseHeaders(method.getResponseHeaders());            
            return response;
        } finally {
            method.releaseConnection();
        }
    }


    /**
     * 执行Http请求
     *
     * @param request 请求数据
     * @param strParaFileName 文件类型的参数名
     * @param strFilePath 文件路径
     * @return
     * @throws IllegalArgumentException if request is null
     * @throws HttpException, IOException
     */
    public void  asynExecute(HttpRequest request, String strParaFileName, String strFilePath)
            throws IllegalArgumentException, HttpException,
            IOException {
        logger.info("请求第三方支付接口,请求参数:",request);
        final CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();
        HttpPost httpPost = new HttpPost(request.getUrl());
        NameValuePair[] nameValuePairs = request.getParameters();
        List<BasicNameValuePair> params = Lists.newArrayList();
        for (NameValuePair v:nameValuePairs){
            params.add(new BasicNameValuePair(v.getName(), v.getValue()));
        }
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params);
        httpPost.setEntity(urlEncodedFormEntity);
        Future<org.apache.http.HttpResponse> execute = httpclient.execute(httpPost, null);
        try {
            org.apache.http.HttpResponse httpResponse = execute.get();
            if (httpResponse !=null && httpResponse.getEntity()!=null){
                String toString = EntityUtils.toString(httpResponse.getEntity());               ;
                logger.info("http异步响应结果 {}", toString );
            }else{
                logger.info("http异步响应结果为空，请开发排查" );
            }
        } catch (InterruptedException e) {
           logger.error("http异步执行异常中断",e);
        } catch (ExecutionException e) {
            logger.error("http异步执行接口异常", e);
        }catch (Exception e){
            logger.error("http异步执行接口异常", e);
        }finally {
            httpclient.close();
        }

    }

    /**
     * 将NameValuePairs数组转变为字符串
     *
     * @param nameValues
     * @return
     */
    protected String toString(NameValuePair[] nameValues) {
        if (nameValues == null || nameValues.length == 0) {
            return "null";
        }

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < nameValues.length; i++) {
            NameValuePair nameValue = nameValues[i];

            if (i == 0) {
                buffer.append(nameValue.getName() + "=" + nameValue.getValue());
            } else {
                buffer.append("&" + nameValue.getName() + "=" + nameValue.getValue());
            }
        }

        return buffer.toString();
    }
}
