package com.yourong.backend.bsc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.backend.BaseController;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyType;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryVerifyDto;
import com.yourong.common.thirdparty.sinapay.member.exception.MemberGatewayInvokeFailureException;

/**
 * 人员认证
 * @author 
 *
 */
@Controller
@RequestMapping("verify")
public class VerifyController  extends BaseController {
	
	
	 @Autowired
	 private SinaPayClient sinaPayClient;
	
	@RequestMapping(value = "index")
    @RequiresPermissions("verify:index")
    public String showVerifyIndex(HttpServletRequest req, HttpServletResponse resp) {
         return "/bsc/verify/index";

	}
	
	@RequestMapping(value = "verify")
    @RequiresPermissions("verify:verify")
	@ResponseBody
    public ResultDto<?> verifyMember(@RequestParam("memberID") String memberID,@RequestParam("verifytype") String verifytype,
    		HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		//Page<QueryVerifyDto> pageRequest = new Page<QueryVerifyDto>();
		//Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		ResultDto<?> verify = null;
		try {
			verify = sinaPayClient.queryVerify(Long.parseLong(memberID),VerifyType.valueOf(verifytype));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemberGatewayInvokeFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return verify;
	}
	
	
	/**
	 * 新浪存钱罐查询验证
	 */
	
	 @RequestMapping(value = "sinaverify")
	 @ResponseBody
	 public String sinaVerify(@RequestParam("memberID") String memberID,HttpServletRequest req, HttpServletResponse resp){
		 ModelAndView mv = new ModelAndView();
		 mv.setViewName("bsc/verify/sinaverify");
		 String auditHtml=null ;
		 try {
			 auditHtml = sinaPayClient.auditMemberInfos(Long.parseLong(memberID));
			mv.addObject("auditHtml", auditHtml);
		} catch (Exception e) {
			logger.error("新浪存钱罐查询验证生成签名异常",e);
			mv.setViewName("/error");  
		}
		 return auditHtml;
	 }
	

	
}
