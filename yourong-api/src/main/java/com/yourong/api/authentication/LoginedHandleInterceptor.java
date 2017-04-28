package com.yourong.api.authentication;

import com.alibaba.fastjson.JSON;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.cache.RedisAPIMemberClient;
import com.yourong.api.service.TokenService;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.util.AES;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.StringUtil;
import com.yourong.core.uc.model.MemberToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 是否登陆
 * Created by py on 2015/3/19.
 */
public class LoginedHandleInterceptor extends HandlerInterceptorAdapter {
    protected static Logger logger = LoggerFactory.getLogger(LoginedHandleInterceptor.class);

    private static final String MEMBER_ID = "api_memberID";

    private static final String REQUEST_SOURCE = "request_source";

    @Autowired
    private TokenService tokenService;

    //是否要拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("token");
        ResultDTO<Object> dto = new ResultDTO<Object>();
        if (StringUtil.isBlank(token)) {
            if (logger.isDebugEnabled()) {
                logger.debug("token为空");
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing token value");
            return false;
        }
        String device = request.getParameter("device");
        if (StringUtil.isBlank(device)) {
            if (logger.isDebugEnabled()) {
                logger.debug("device为空");
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing device value");
            return false;
        }
        List<String> tokenDecrypt = null;
        try {
            tokenDecrypt = AES.getInstance().tokenDecrypt(token);
        }catch (Exception e){
            logger.error("解密token失败:" +token);
        }
        if (Collections3.isEmpty(tokenDecrypt)) {
            if (logger.isDebugEnabled()) {
                logger.debug("解密token失败:"+token);
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing token value");
            return false;
        }
        String memberID = tokenDecrypt.get(0);
        if (!StringUtil.isNumeric(memberID)) {
            if (logger.isDebugEnabled()) {
                logger.debug("解密token失败 memberID"+memberID);
            }
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing token value");
            return false;
        }
        Long id = Long.valueOf(memberID);
        if (id == 0) {
            if (logger.isDebugEnabled())
                logger.debug("解密token失败 memberID:"+id);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing token value");
            return false;
        }
        MemberToken memberToken = tokenService.selectByDeviceAndMemberID(device, id);
        if (memberToken == null) {
            unAuthorMember(response, dto);
            return false;
        }
        if (StringUtil.equals(tokenDecrypt.get(1), memberToken.getToken(), true)) {
            request.setAttribute(MEMBER_ID, id);
            request.setAttribute(REQUEST_SOURCE,memberToken.getTokenType());
            return true;
        } else {
            unAuthorMember(response, dto);
            return false;
        }
    }
    private void unAuthorMember(HttpServletResponse response, ResultDTO<Object> dto) throws IOException {
        dto.setResultCode(ResultCode.UNAUTHOR_MEMBER_ERROR);
        String jsonString = JSON.toJSONString(dto);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(jsonString.getBytes(Constant.DEFAULT_CHARSET));
    }


}
