package com.yourong.api.authentication;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;

/**
 * 签名验证
 * Created by py on 2015/3/19.
 */
public class SignHandleInterceptor extends HandlerInterceptorAdapter {

    private static final String YOURONG_API_MD5_SIGN = "yourong.api.md5Sign";
    private static final String ACCEPT_VERSION = "Accept-Version";
    private static final String ENCRYPT_VERSION = "Encrypt-Version";
    /**
     * 日志对象
     */
    private final static Logger logger = LoggerFactory.getLogger(SignHandleInterceptor.class);

    //是否要拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestSign = request.getParameter("sign");
        logger.debug("APP签名：" + requestSign);
        if (StringUtil.isBlank(requestSign)) {
            logger.debug("没有签名");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing sign value");
            return false;
        }
        String requestTime = request.getParameter("requestTime");
        if (StringUtil.isBlank(requestTime)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing requestTime value");
            return false;
        }

        String device = request.getParameter("device");
        if (StringUtil.isBlank(device)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing device value");
            return false;
        }
        //TODO  判断请求时间
        Map<String, String[]> parameterMap = request.getParameterMap();
        String header = request.getHeader(ACCEPT_VERSION);
        if (StringUtil.isBlank(header)) {
            header = request.getParameter(ACCEPT_VERSION);
        }
        String entry = request.getHeader(ENCRYPT_VERSION);
        Map<String, String> paraFilter = RequestSign.paraFilter(parameterMap);
        if (StringUtil.isNotBlank(entry)) {
            logger.debug("加密版本号",entry);
            paraFilter.put(ENCRYPT_VERSION, entry);
        }
        String sign = RequestSign.buildRequestByMD5(paraFilter, PropertiesUtil.getProperties(YOURONG_API_MD5_SIGN));
        logger.debug("服务器签名：" + sign);
        if (PropertiesUtil.isAppNoEncrption()) {
            //开发环境，暂时不验证签名
            return true;
        }
        if (StringUtil.equals(requestSign, sign)) {
            return true;
        } else {
            logger.debug("签名不匹配");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing sign value");
            return false;
        }
    }


}
