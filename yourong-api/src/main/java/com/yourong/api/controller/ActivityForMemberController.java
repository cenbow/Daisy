package com.yourong.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.ActivityLotteryService;
import com.yourong.common.enums.ResultCode;

/**
 * 会员活动控制类
 * 
 * @author wangyanji
 *
 */
@Controller
@RequestMapping(value = "security/activity")
public class ActivityForMemberController extends BaseController {

	@Autowired
	private ActivityLotteryService activityLotteryService;

	/**
	 * 
	 * @Description:根据订单号获取红包链接
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "/redBag/shareUrl", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public Object getTransactionRedBagUrl(HttpServletRequest req, HttpServletResponse resp) {
		Long orderId = ServletRequestUtils.getLongParameter(req, "orderId", 0L);
		Long memberId = getMemberID(req);
		return activityLotteryService.getTransactionRedBagUrl(orderId, memberId);
	}

	/**
	 * 
	 * @Description:春节许愿
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "/springFestival/makeWish", method = RequestMethod.POST)
	@ResponseBody
	public Object springFestivalMakeWish(HttpServletRequest req, HttpServletResponse resp) {
		Long messageTemplateId = ServletRequestUtils.getLongParameter(req, "msgId", 0L);
		ResultDTO<Object> rDO = new ResultDTO<Object>();
		if (messageTemplateId == 0L) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return rDO;
		} else if (getMember() == null) {
			rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			return rDO;
		}
		return activityLotteryService.springFestivalMakeWish(getMember().getId(), messageTemplateId);
	}

	/**
	 * 
	 * @Description:春节领券
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "/springFestival/receive", method = RequestMethod.POST)
	@ResponseBody
	public Object springFestivalReceiveCoupon(HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO<Object> rDO = new ResultDTO<Object>();
		if (getMember() == null) {
			rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			return rDO;
		}
		return activityLotteryService.springFestivalReceiveCoupon(getMember().getId());
	}
}
