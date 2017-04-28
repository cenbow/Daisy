/**
 * 
 */
package com.yourong.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.common.enums.SinaBankMobileEnum;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年8月9日下午8:33:14
 */
@Controller
@RequestMapping("sinaBank")
public class SinaBankReturnController {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value = "/returnRedict")
    @ResponseBody
    public ModelAndView returnRedict(HttpServletRequest req,   HttpServletResponse resp) throws Exception {
		logger.info("进入新浪收银台回调");
		ModelAndView model = new ModelAndView();
	 	String type = ServletRequestUtils.getStringParameter(req, "type", String.valueOf(SinaBankMobileEnum.COMMON_APP.getStatus()));
	 	String source = ServletRequestUtils.getStringParameter(req, "source", "0");
	 	logger.info("新浪收银台回调：type="+type+"source="+source);
	 	String withdrawNo = ServletRequestUtils.getStringParameter(req, "withdrawNo", "0");
	 	String tradeNo = ServletRequestUtils.getStringParameter(req, "tradeNo", "0");
	 	String orderId = ServletRequestUtils.getStringParameter(req, "orderId", "0");
		logger.info("新浪收银台回调：withdrawNo="+withdrawNo+"tradeNo="+tradeNo+"orderId="+orderId);
	 	
		model.setViewName("/transaction/transferCashierDesk");
		model.addObject("loginSource", source);
		model.addObject("type", type);
		model.addObject("withdrawNo", withdrawNo);
		model.addObject("tradeNo", tradeNo);
		model.addObject("orderId", orderId);
		return model;
    }
	
}

