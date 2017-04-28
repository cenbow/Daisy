package com.yourong.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.MemberInfoService;
import com.yourong.api.service.MemberService;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.StringUtil;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;

/**
 * @author chaisen
 * @date 
 */
@Controller
@RequestMapping("security/evalua")
public class QuestionNaireController extends BaseController {

	 @Autowired
	 private MemberInfoService memberInfoService;
	 
	 @Autowired
	 private MemberService memberService;
	 
	 
	 /**
		 * 
		 * @Description:移动端活动动态调用
		 * @param req
		 * @param resp
		 * @return
		 * @author: wangyanji
		 * @time:2016年2月24日 下午5:01:44
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping(value = "dynamicInvoke", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
		@ResponseBody
		public ResultDTO<Object> dynamicInvoke(HttpServletRequest req, HttpServletResponse resp) {
			ResultDTO<Object> resultDTO = new ResultDTO<>();
			resultDTO.setIsSuccess();
			try {
				String method = ServletRequestUtils.getStringParameter(req, "invokeMethod");
				if (StringUtil.isBlank(method)) {
					resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
					return resultDTO;
				}
				DynamicParamBuilder paramBuilder = buildArgs(req);
				logger.debug("移动端活动动态调用 method={}, Objects={}", method, paramBuilder);
				try {
					resultDTO = (ResultDTO<Object>) MethodUtils.invokeExactMethod(memberInfoService, method, paramBuilder);
				} catch (Exception e) {
					logger.error("动态调用失败 class=memberInfoService paramBuilder={}", method, paramBuilder, e);
					resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
				}
			} catch (Exception e) {
				logger.error("移动端活动动态调用异常", e);
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			}
			return resultDTO;
		}
		
	/**
	 * 
	 * @Description 风险测评
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年6月15日 下午1:40:08
	 */
	 @RequestMapping(value = "saveEvaluation", method = RequestMethod.POST)
	    @ResponseBody
	    public ResultDO<MemberInfo>saveEvaluationMemberInfo(HttpServletRequest req, HttpServletResponse resp) {
	        ResultDO<MemberInfo> result = new ResultDO<MemberInfo>();
	        MemberSessionDto member = getMember();
	        int evaluationScore=ServletRequestUtils.getIntParameter(req, "evaluationScore", 0);
			//登录认证
			if (member == null) {
				result.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return result;
			}
			if(evaluationScore==0){
				result.setResultCode(ResultCode.EVALUATION_SCORE_RESULT);
				return result;
			}
	        result = memberInfoService.saveMemberInfoByMemberId(getMember(),evaluationScore);
	        return result;
	    }
	 	/**
	 	 * 
	 	 * @Description:风险测评
	 	 * @param req
	 	 * @param resp
	 	 * @return
	 	 * @throws ManagerException
	 	 * @author: chaisen
	 	 * @time:2016年6月15日 下午4:16:34
	 	 */
	 	@RequestMapping(value = "/getEvalua", method = RequestMethod.POST, headers = {"Accept-Version=1.5.0"})
	 	@ResponseBody
		public Object getEvalua(HttpServletRequest req, HttpServletResponse resp) throws ManagerException {
			ResultDTO result = new ResultDTO();
			Long memberId = getMemberID(req);
			result=memberInfoService.getEvaluationByMemberId(memberId);
			return result;
			
		}
	 	
	 	
	 	/**
	 	 * 
	 	 * @Description:添加获取用户签署信息接口
	 	 * @param req
	 	 * @param resp
	 	 * @return
	 	 * @throws ManagerException
	 	 * @author: chaisen
	 	 * @time:2016年6月15日 下午4:16:34
	 	 */
	 	@RequestMapping(value = "/getSignInfo", method = RequestMethod.POST, headers = {"Accept-Version=1.6.0"})
	 	@ResponseBody
		public Object getSignInfo(HttpServletRequest req, HttpServletResponse resp) throws ManagerException {
			ResultDTO result = new ResultDTO();
			Long memberId = getMemberID(req);
			result=memberInfoService.getSignInfoByMemberId(memberId);
			return result;
			
		}
		
		

}
