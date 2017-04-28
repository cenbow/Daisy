package com.yourong.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.MemberInfoService;
import com.yourong.common.exception.ManagerException;

/**
 * 
 * @desc 风险测评
 * @author chaisen
 * 2016年6月6日上午11:57:59
 */
@Controller
@RequestMapping(value = "security/evalua")
public class EvaluationController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(EvaluationController.class);

	 @Autowired
	 private MemberInfoService memberInfoService;

	/**
	 * 
	 * @Description:获取风险测评结果
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年6月6日 下午12:00:08
	 */
	@RequestMapping(value = "getEvalua", headers = { "Accept-Version=1.5.0" })
	@ResponseBody
	public ResultDTO getEvalua(HttpServletRequest req, HttpServletResponse resp) throws ManagerException {
		ResultDTO result = new ResultDTO();
		Long memberId = getMemberID(req);
		return memberInfoService.getEvaluationByMemberId(memberId); 
		
	}
}
