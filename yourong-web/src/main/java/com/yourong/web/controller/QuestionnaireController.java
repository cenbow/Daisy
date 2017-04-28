package com.yourong.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.util.CryptCode;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.GenerateQuestionMd5;
import com.yourong.common.util.HttpUtil;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.mc.model.biz.ActivityQuestion;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.service.CouponService;
import com.yourong.web.service.PopularityInOutLogService;
import com.yourong.web.utils.ServletUtil;

/***
 * 问卷网对接
 * 
 * @author fuyili
 *
 *         创建时间:2015年5月4日下午4:26:15
 */
@Controller
@RequestMapping("questionnaire")
public class QuestionnaireController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(QuestionnaireController.class);

	@Autowired
	private PopularityInOutLogService popularityInOutLogService;

	@Autowired
	private CouponService couponService;
	
	/**
	 * 登陆问卷调查网站
	 */
	@RequestMapping("login")
	public String loginQuestion(RedirectAttributes attr) {
		String url = Config.getQuestionAddr() + "/openapi/login/";
		Map<String, String> params = Maps.newHashMap();
		params.put("site", Config.getQuestionSite());
		params.put("user", "fuyili");
		params.put("ctime", DateUtils.formatDatetoString(DateUtils.getCurrentDate(), "yyyy-MM-dd HH:mm"));
		logger.debug("问卷登陆当前时间:", params.get("ctime"));
		params.put("email", "fu.yili@yourongwang.com");
		params.put("mobile", "13738118891");
		List<String> keyList = new ArrayList<String>();
		keyList.add("site");
		keyList.add("user");
		keyList.add("ctime");
		keyList.add("email");
		keyList.add("mobile");
		String md5Param = GenerateQuestionMd5.sortParam(keyList, params);
		String md5 = CryptCode.encryptToMD5(md5Param + Config.getQuestionSecretKey());
		params.put("md5", md5);
		logger.debug("md5:", md5);
		String respons = HttpUtil.doGet(url, params);
		logger.debug("问卷调查返回内容", respons);
		attr.addAllAttributes(params);
		return "redirect:" + url;
	}

	// private String viewProjStatus() {
	// String url = Config.getQuestionAddr()+"/openapi/proj_status/?";
	// Map<String, String> params = Maps.newHashMap();
	// params.put("site", Config.getQuestionSite());
	// params.put("proj_id", "qqmmai");
	// List<String> keyList = new ArrayList<String>();
	// keyList.add("site");
	// keyList.add("proj_id");
	// String md5Param = GenerateQuestionMd5.sortParam(keyList, params);
	// String md5 = CryptCode.encryptToMD5(md5Param +
	// Config.getQuestionSecretKey());
	// params.put("md5", md5);
	// System.out.println("md5:" + md5);
	// String respons = HttpUtil.doGet(url, params, "UTF-8");
	// System.out.println(respons);
	// return "";
	// }

	@RequestMapping("answer")
	public String showProject(RedirectAttributes attr) {
		// /s/proj_id/?site=***&user=***&callback=***&md5=***&proj_id=***
		String url = Config.getQuestionAddr() + "/s/"+Config.getQuestionProjId()+"/";
		Map<String, String> params = Maps.newHashMap();
		params.put("site", Config.getQuestionSite());
		MemberSessionDto user = ServletUtil.getUserDO();
		if (user != null) {
			params.put("user", String.valueOf(user.getId()));
			params.put("callback", Config.getQuestionNotify());
			params.put("proj_id", Config.getQuestionProjId());
			List<String> keyList = new ArrayList<String>();
			keyList.add("site");
			keyList.add("user");
			keyList.add("callback");
			keyList.add("proj_id");
			String md5Param = GenerateQuestionMd5.sortParam(keyList, params);
			String md5 = CryptCode.encryptToMD5(md5Param + Config.getQuestionSecretKey());
			params.put("md5", md5);
			attr.addAllAttributes(params);
			return "redirect:" + url;
		} else{
			return "member/login/login";
		}
	}
	

	@RequestMapping("notify")
	@ResponseBody
	public boolean quesNotify(@ModelAttribute("activityQuestion") ActivityQuestion question) {
		Map<String, String> params = Maps.newHashMap();
		params.put("user", String.valueOf(question.getUser()));
		params.put("proj_id", question.getProj_id());
		params.put("status", String.valueOf(question.getStatus()));
		params.put("time", question.getTime());
		params.put("site", Config.getQuestionSite());
		params.put("repeat", "");
		params.put("sunion_status", String.valueOf(1));
		List<String> keyList = new ArrayList<String>();
		keyList.add("user");
		keyList.add("proj_id");
		keyList.add("status");
		keyList.add("time");
		keyList.add("site");
		keyList.add("repeat");
		keyList.add("sunion_status");
		String md5Param = GenerateQuestionMd5.sortParam(keyList, params);
		String md5Check = CryptCode.encryptToMD5(md5Param + Config.getQuestionSecretKey());
		boolean result = false;
		Date currentDate = DateUtils.getCurrentDate();
		Date endDate = DateUtils.getDateFromString(Config.getQuestionEndTime(), DateUtils.TIME_PATTERN);
		String requestUrl = Config.getQuestionNotify() + "?" + getUrlParamsByMap(params);
		logger.info("md5Param:" + md5Param);
		logger.info("notifyUrl:" + requestUrl);
		logger.info("md5:" + question.getMd5());
		logger.info("md5Check:" + md5Check);
		if (DateUtils.compareDate(currentDate, endDate) == DateUtils.BEFORE) {
			logger.info("问卷活动已结束！");
			return result;
		}
		if (!md5Check.equalsIgnoreCase(question.getMd5())) {
			logger.info("问卷调查callback的md5不正确");
			return result;
		}
		if (question.getStatus().equals(1)) {
			result = couponService.giveCouponForQuestion(Long.valueOf(question.getUser()),281L,-1L);
		} else {
			logger.debug("无效问卷");
			result = false;
		}
		return result;
	}

	/**
	 * map转化为url参数
	 * 
	 * @param map
	 * @return
	 */
	public static String getUrlParamsByMap(Map<String, String> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String s = sb.toString();
		if (s.endsWith("&")) {
			s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
		}
		return s;
	}

}
