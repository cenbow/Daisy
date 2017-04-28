package com.yourong.api.authentication;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.HtmlTokenService;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.util.AES;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.uc.model.MemberHtmlToken;

public class HtmlLoginedHandleInterceptor extends HandlerInterceptorAdapter{
	
	protected static Logger logger = LoggerFactory.getLogger(LoginedHandleInterceptor.class);

    private static final String MEMBER_ID = "api_memberID";

    private static final String REQUEST_SOURCE = "request_source";
    
    @Autowired
    private HtmlTokenService htmlTokenService;

    //是否要拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String htmlToken = request.getParameter("htmlToken");
        ResultDTO<Object> dto = new ResultDTO<Object>();
        if (StringUtil.isBlank(htmlToken)) {
            if (logger.isDebugEnabled()) {
                logger.debug("htmlToken为空");
            }
            dto.setResultCode(ResultCode.APP_HTML_MEMBER_UNLOGIN);
            unAuthorMember(response, dto);
            return false;
        }
        List<String> tokenDecrypt = null;
        try {
            tokenDecrypt = AES.getInstance().tokenDecrypt(htmlToken);
        }catch (Exception e){
            logger.error("解密htmlToken失败:" +htmlToken);
        }
        if (Collections3.isEmpty(tokenDecrypt)) {
            if (logger.isDebugEnabled()) {
                logger.debug("解密htmlToken失败:"+htmlToken);
            }
            dto.setResultCode(ResultCode.APP_HTML_MEMBER_UNLOGIN);
            unAuthorMember(response, dto);
            return false;
        }
        String tokenMemberID = tokenDecrypt.get(0);
        if (!StringUtil.isNumeric(tokenMemberID)) {
            if (logger.isDebugEnabled()) {
                logger.debug("解密token失败 memberID"+tokenMemberID);
            }
            dto.setResultCode(ResultCode.APP_HTML_MEMBER_UNLOGIN);
            unAuthorMember(response, dto);
            return false;
        }
        Long memberID = Long.valueOf(tokenMemberID);
        if (memberID == 0) {
            if (logger.isDebugEnabled())
                logger.debug("解密token失败 memberID:"+memberID);
            dto.setResultCode(ResultCode.APP_HTML_MEMBER_UNLOGIN);
            unAuthorMember(response, dto);
            return false;
        }
        
        MemberHtmlToken memberHtmlToken = htmlTokenService.selectByMemberId(memberID);
        if (memberHtmlToken == null) {
        	dto.setResultCode(ResultCode.APP_HTML_MEMBER_TOKEN_OVERTIME);
            unAuthorMember(response, dto);
            return false;
        }
         //token时效性24 小时
        long intervalMilli = Long.valueOf(tokenDecrypt.get(2)) - DateUtils.getCurrentDate().getTime();
        if((int) (Math.abs(intervalMilli) / (60 * 1000))>=60*24){
        	dto.setResultCode(ResultCode.APP_HTML_MEMBER_TOKEN_OVERTIME);
            unAuthorMember(response, dto);
            return false;
        }
        
        if (StringUtil.equals(tokenDecrypt.get(1), memberHtmlToken.getToken(), true)) {
            request.setAttribute(MEMBER_ID, memberID);
            return true;
        } else {
        	dto.setResultCode(ResultCode.APP_HTML_MEMBER_UNLOGIN);
            unAuthorMember(response, dto);
            return false;
        }
    }
    private void unAuthorMember(HttpServletResponse response, ResultDTO<Object> dto) throws IOException {
        
        String jsonString = JSON.toJSONString(dto);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(jsonString.getBytes(Constant.DEFAULT_CHARSET));
    }

}
