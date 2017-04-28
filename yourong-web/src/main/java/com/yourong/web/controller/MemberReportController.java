package com.yourong.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.core.uc.model.MemberReport;
import com.yourong.web.dto.MemberReportDto;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.service.MemberReportService;

/**
 * @author chaisen
 * @date 
 */
@Controller
@RequestMapping("memberReport")
public class MemberReportController extends BaseController {

	@Autowired
	private MemberReportService meberReportService;

	/**
	 * 保存报名信息
	 * 
	 * @param orderDto
	 * @param result
	 * @param req
	 * @param resp
	 * @return
	 */
	/*@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<MemberReport> saveMemberReport(
			@Valid @ModelAttribute("form") MemberReportDto memberReportDto, BindingResult bindingResult, HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<MemberReport> resultDO = new ResultDO<MemberReport>();
		MemberSessionDto member = getMember();
		//登录认证
		if (member == null) {
			resultDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			resultDO.setSuccess(false);
			return resultDO;
		}
		// 未实名认证
		if (member.getTrueName() == null) {
				resultDO.setResultCode(ResultCode.MEMBER_UN_TRUE_NAME);
				resultDO.setSuccess(false);
				return resultDO;
		}
		MemberReport memberReport = new MemberReport();
		BeanCopyUtil.copy(memberReportDto, memberReport);
		memberReport.setMobile(member.getMobile());
		memberReport.setTrueName(member.getTrueName());
		Long memberId = getMember().getId();
		memberReport.setMemberId(memberId);
		try {
			//resultDO = meberReportService.saveReport(memberReport);
		} catch (Exception e) {
			logger.error("系统异常",e);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return resultDO;
	}
*/
}
