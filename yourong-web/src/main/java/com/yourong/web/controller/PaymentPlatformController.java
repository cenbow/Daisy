package com.yourong.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.bsc.model.PaymentPlatform;
import com.yourong.web.service.PaymentPlatformService;

/**
 * 关于活动的controller
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("payment")
public class PaymentPlatformController extends BaseController {

	@Autowired
	private PaymentPlatformService paymentPlatformService;

	/**
	 * 查询限额及维护公告
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/platformLimit", method = RequestMethod.POST)
	@ResponseBody
	public Object queryPlatformLimit(@ModelAttribute PaymentPlatform paymentPlatform, HttpServletRequest req, HttpServletResponse resp) {
		return paymentPlatformService.queryPlatformLimit(paymentPlatform);
	}

}
