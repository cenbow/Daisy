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

public class HtmlUnLoginedHandleInterceptor extends HandlerInterceptorAdapter{

	protected static Logger logger = LoggerFactory.getLogger(LoginedHandleInterceptor.class);

    private static final String MEMBER_ID = "api_memberID";

    
    @Autowired
    private HtmlTokenService htmlTokenService;

    //非必须登录情况下，获取用户ID
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	 ResultDTO<Object> dto = new ResultDTO<Object>();
        String htmlToken = request.getParameter("htmlToken");
		if (StringUtil.isNotBlank(htmlToken)) {
			 List<String> tokenDecrypt = null;
		        try {
		            tokenDecrypt = AES.getInstance().tokenDecrypt(htmlToken);
		        }catch (Exception e){
		            logger.error("解密htmlToken失败:" +htmlToken);
		        }
			if (Collections3.isNotEmpty(tokenDecrypt)) {
				String memberID = tokenDecrypt.get(0);
				if (StringUtil.isNumeric(memberID)) {
					Long id = Long.parseLong(memberID);
					if(id > 0){
						  MemberHtmlToken memberHtmlToken = htmlTokenService.selectByMemberId(id);
					        if (memberHtmlToken != null) {
					        	//token时效性 24 小时
						        long intervalMilli = Long.valueOf(tokenDecrypt.get(2)) - DateUtils.getCurrentDate().getTime();
						        if((int) (Math.abs(intervalMilli) / (60 * 1000))>=60*24){
						        	dto.setResultCode(ResultCode.APP_HTML_MEMBER_TOKEN_OVERTIME);
						            unAuthorMember(response, dto);
						            return false;
						        }else{
						        	  if (StringUtil.equals(tokenDecrypt.get(1), memberHtmlToken.getToken(), true)) {
								            request.setAttribute(MEMBER_ID, id);
								            return true;
								        }
						        }
					        }
					         
					}
				}
			}
			
		}
		return super.preHandle(request, response, handler);
    }
    
    private void unAuthorMember(HttpServletResponse response, ResultDTO<Object> dto) throws IOException {
        
        String jsonString = JSON.toJSONString(dto);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(jsonString.getBytes(Constant.DEFAULT_CHARSET));
    }
}
