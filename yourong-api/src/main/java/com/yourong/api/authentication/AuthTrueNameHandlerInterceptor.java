package com.yourong.api.authentication;

import com.alibaba.fastjson.JSON;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.MemberService;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.ResultCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.nio.charset.Charset;

/**
 * 该用户是否实名认证
 * Created by py on 2015/3/25.
 */
public class AuthTrueNameHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final String MEMBER_ID = "api_memberID";
    private static Logger logger = LoggerFactory.getLogger(AuthTrueNameHandlerInterceptor.class);
    @Autowired
    private MemberService memberService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long memberID = (Long) request.getAttribute(MEMBER_ID);
        if(memberService.isAuthIdentityAndPhone(memberID)){
            return true;
        }else {
            ResultDTO<Object> dto = new ResultDTO<Object>();
            dto.setResultCode(ResultCode.MEMBER_UN_TRUE_NAME);
            String jsonString = JSON.toJSONString(dto);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(jsonString.getBytes(Constant.DEFAULT_CHARSET));
            return false;
        }

    }
}
