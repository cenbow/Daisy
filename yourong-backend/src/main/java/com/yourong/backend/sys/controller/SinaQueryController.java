package com.yourong.backend.sys.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.backend.BaseController;
import com.yourong.backend.uc.service.MemberService;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.member.domain.result.AccountDetail;
import com.yourong.common.thirdparty.sinapay.member.exception.MemberGatewayInvokeFailureException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.uc.model.Member;

/**
 * 后台新浪接口查询
 * @author wangyanji
 *
 */
@Controller
@RequestMapping("sinaQuery")
public class SinaQueryController extends BaseController {
    
	@Autowired
    private SinaPayClient sinaPayClient;
	
	@Autowired
    private MemberService memberService;

    @RequestMapping(value = "/queryAccountDetails")
    @RequiresPermissions("sinaQuery:queryAccountDetails")
    public ModelAndView queryAccountDetails(HttpServletRequest req, HttpServletResponse resp) {
    	ModelAndView model = new ModelAndView("/sys/sinaQuery/queryAccountDetails"); 
    	String end = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN);
    	String start = DateUtils.addDays(end, -7, DateUtils.TIME_PATTERN);
    	model.addObject("start", start);
    	model.addObject("end", end);
    	return model;
    }

    /**
     * 查询会员收支明细
     * @param req
     * @param resp
     * @return
     * @throws ServletRequestBindingException
     */
    @RequestMapping(value = "/queryAccountDetailsAjax")
    @RequiresPermissions("sinaQuery:queryAccountDetailsAjax")
    @ResponseBody
    public Object queryAccountDetailsAjax(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		ResultDto<AccountDetail> rDto = new ResultDto<AccountDetail>();
		try {
			long memberId = ServletRequestUtils.getLongParameter(req, "memberId", 0);
			if(memberId == 0l) {
				long mobile = ServletRequestUtils.getLongParameter(req, "mobile", 0);
				Map<String, Object> selectPara = new HashMap<String, Object>();
				selectPara.put("mobile", mobile);
				 List<Member> list = memberService.selectMember(selectPara);
				if(Collections3.isNotEmpty(list)) {
					memberId = list.get(0).getId();
				} else {
					rDto.setSuccess(false);
					rDto.setErrorMsg(ResultCode.MEMBER_NOT_EXIST_ERROR.getMsg());
					return rDto;
				}
			}
			String svpTradeType = ServletRequestUtils.getStringParameter(req, "svpTradeType", null);
			String startDateStr = ServletRequestUtils.getStringParameter(req, "startDateStr", null);
			String endDateStr = ServletRequestUtils.getStringParameter(req, "endDateStr", null);
			String pageNo = ServletRequestUtils.getStringParameter(req, "pageNo", "1");
			String pageSize = ServletRequestUtils.getStringParameter(req, "pageSize", "10");
			rDto = sinaPayClient.queryAccountDetailsAll(
					memberId,
					svpTradeType,
					DateUtils.getDateFromString(startDateStr, DateUtils.TIME_PATTERN),
					DateUtils.getDateFromString(endDateStr, DateUtils.TIME_PATTERN),
					pageNo,
					pageSize
					);
		} catch (MemberGatewayInvokeFailureException e) {
			logger.error("查询会员收支明细失败", e);
			rDto.setSuccess(false);
			rDto.setErrorMsg(ResultCode.ERROR_SYSTEM.getMsg());
		}		 
        return rDto;
    }
}