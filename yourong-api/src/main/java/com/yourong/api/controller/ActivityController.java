package com.yourong.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.api.dto.ActivityForNewYearDto;
import com.yourong.api.service.TransactionService;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.*;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.biz.*;
import com.yourong.core.mc.model.biz.ActivityForFiveBillionRetrun;
import com.yourong.core.mc.model.biz.ActivityForNewYear;
import com.yourong.core.mc.model.biz.ActivityForNewYearGrab;
import com.yourong.core.mc.model.biz.ActivityGrabResultBiz;
import com.yourong.core.mc.model.biz.ActivityForWomensDay;
import com.yourong.core.mc.model.biz.ActivityForSubscription;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.PopularityInOutLogDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.ActivityLotteryService;
import com.yourong.api.service.MemberInfoService;
import com.yourong.api.service.MemberService;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.query.ActivityLotteryResultQuery;
import com.yourong.core.uc.model.Member;

/**
 * 活动
 * 
 * @author wangyanji
 *
 */
@Controller
@RequestMapping(value = "activity")
public class ActivityController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

	@Autowired
	private ActivityLotteryService activityLotteryService;

	@Autowired
	private MemberService memberService;
	
	 @Autowired
	 private MemberInfoService memberInfoService;

	@Autowired
	private TransactionService transactionService;
	
	/**
	 * 周年庆-瓜分红包
	 * 
	 * @param urlCode
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "anniversary/getRed", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO<Object> anniversaryGetRed(HttpServletRequest req, HttpServletResponse resp) {
		try {
			String code = req.getParameter("code");
			String mobile = req.getParameter("mobile");
			return activityLotteryService.anniversaryGetRed(code, mobile);
		} catch (Exception e) {
			ResultDTO<Object> rDO = new ResultDTO<Object>();
			rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return rDO;
		}
	}

	/**
	 * 周年庆-瓜分红包初始化
	 * 
	 * @param urlCode
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("anniversary/loadRed")
	public ModelAndView anniversaryLoadRed(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		try {
			String urlCode = req.getParameter("code");
			String activityIdStr = PropertiesUtil.getProperties("activity.anniversary.red.id");
			Long actId = Long.parseLong(activityIdStr);
			Activity act = activityLotteryService.getActivityById(actId);
			if (act.getActivityStatus().intValue() == 4) {
				if (StringUtil.isNotBlank(urlCode)) {
					AES aes = AES.getInstance();
					// urlCode = URLDecoder.decode(urlCode,
					// Constant.DEFAULT_CODE);
					List<String> strings = aes.tokenDecrypt(urlCode);
					if (Collections3.isNotEmpty(strings) && strings.size() == 4) {
						model.setViewName("/weixin/activity/anniversaryRed");
						return model;
					}
				}
			}
		} catch (Exception e) {
			logger.error("周年庆-瓜分红包初始化失败", e);
		}
		model.setViewName("/weixin/activity/anniversaryTimeOut");
		return model;
	}

	/**
	 * 
	 * @Description:百万现金券活动入口
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午9:24:39
	 */
	@RequestMapping("/millionFund/index")
	public ModelAndView millionFundIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.addObject("fund", activityLotteryService.getMillionCouponFund());
		model.setViewName("/activity/millionFund");
		return model;
	}

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
			DynamicParamBuilder paramBuilder = null;
			if ("break2BillionReceive".equals(method) || "saveEvaluation".equals(method)) {
				paramBuilder = buildArgs2(req);
			} else if("guessGold".equals(method)||"setOlympic".equals(method)){
				paramBuilder = buildArgs3(req);
			} else {
				paramBuilder = buildArgs(req);
			}

			logger.debug("移动端活动动态调用 method={}, Objects={}", method, paramBuilder);
			try {
				resultDTO = (ResultDTO<Object>) MethodUtils.invokeExactMethod(activityLotteryService, method, paramBuilder);
			} catch (Exception e) {
				logger.error("动态调用失败 class=activityLotteryService paramBuilder={}", method, paramBuilder, e);
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
	 * @Description:春节活动
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月6日 下午1:56:46
	 */
	@RequestMapping("springFestival/index")
	public ModelAndView springFestivalIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/springFestival/springFestival");
		return model;
	}

	/**
	 * 春节活动页面初始化
	 * 
	 * @param urlCode
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "springFestival/init", method = RequestMethod.POST)
	@ResponseBody
	public Object springFestivalInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.springFestivalInit(Optional.fromNullable(getMember()));
	}

	/**
	 * 红包分享页面
	 * 
	 * @param urlCode
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("redBag/share")
	public ModelAndView redBagShare(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		try {
			String exceptionUrl = prepareForRedBag(req, resp);
			if (StringUtil.isNotBlank(exceptionUrl)) {
				model.setViewName(exceptionUrl);
				return model;
			}
			model.setViewName("/activity/springFestival/springFestivalShare");
			return model;
		} catch (Exception e) {
			logger.error("跳转红包分享页面失败", e);
			model.setViewName("/activity/springFestival/scanError");
			return model;
		}
	}

	/**
	 * 领红包页面
	 * 
	 * @param urlCode
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("redBag/grab")
	public ModelAndView redBagIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		try {
			String exceptionUrl = prepareForRedBag(req, resp);
			if (StringUtil.isNotBlank(exceptionUrl)) {
				model.setViewName(exceptionUrl);
				return model;
			}
			model.setViewName("/activity/springFestival/springFestivalGrab");
			return model;
		} catch (Exception e) {
			logger.error("跳转领红包页面失败", e);
			model.setViewName("/activity/springFestival/scanError");
			return model;
		}
	}

	/**
	 * 
	 * @Description:访问红包路由预校验
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月20日 下午2:01:18
	 */
	private String prepareForRedBag(HttpServletRequest req, HttpServletResponse resp) {
		String exceptionUrl = null;
		String redBagCode = null;
		String decryptCode = null;
		try {
			redBagCode = req.getParameter("redBagCode");
			// 加密串长度正常情况下是固定的，非固定长度的加密串都需要拦截。
			if (StringUtil.isBlank(redBagCode) || redBagCode.length() != ActivityConstant.redBagCodeLength) {
				exceptionUrl = "/scanError";
				return exceptionUrl;
			}
			// 红包解密
			decryptCode = CryptHelper.decryptByase(redBagCode);
			String[] decryptArr = decryptCode.split(ActivityConstant.redBagCodeSplit);
			if (decryptArr == null || decryptArr.length == 0) {
				exceptionUrl = "/scanError";
				return exceptionUrl;
			}
			Long activityId = new Long(decryptArr[0]);
			Optional<Activity> opt = LotteryContainer.getInstance().getActivity(activityId);
			if (!opt.isPresent()) {
				exceptionUrl = "/scanError";
				return exceptionUrl;
			}
			if (opt.get().getActivityStatus() != StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				exceptionUrl = "/end";
				return exceptionUrl;
			}
		} catch (Exception e) {
			logger.error("访问红包路由预校验失败, redBagCode={}, decryptCode={}", redBagCode, decryptCode, e);
			exceptionUrl = "/scanError";
		}
		return exceptionUrl;
	}


	/**
	 * 人气值红包初始化页面
	 *
	 * @param urlCode
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("popRedPackage/grab")
	public ModelAndView popularityRedPackage(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		String path = "/activity/popularityRedPackage";
		try {
			String exceptionUrl = prepareForRedBag(req, resp);
			if (StringUtil.isNotBlank(exceptionUrl)) {
				model.setViewName(path + exceptionUrl);
				return model;
			}
			model.setViewName(path + "/index");
			return model;
		} catch (Exception e) {
			logger.error("跳转领红包页面失败", e);
			model.setViewName(path + "/scanError");
			return model;
		}
	}

	/**
	 * 
	 * @Description:领红包初始化
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "/popRedPackage/init", method = RequestMethod.POST)
	@ResponseBody
	public Object popularityRedPackageInit(HttpServletRequest req, HttpServletResponse resp) {
		String redBagCode = req.getParameter("redBagCode");
		return activityLotteryService.redBagInit(redBagCode);
	}

	/**
	 * 
	 * @Description:领红包
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "/popRedPackage/receive", method = RequestMethod.POST)
	@ResponseBody
	public Object redBagReceive(HttpServletRequest req, HttpServletResponse resp) {
		String redBagCode = req.getParameter("redBagCode");
		Long mobile = ServletRequestUtils.getLongParameter(req, "mobile", 0L);
		return activityLotteryService.redBagReceive(redBagCode, mobile);
	}

	/**
	 * 
	 * @Description:下载APP领红包页
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午9:24:39
	 */
	@RequestMapping("/popRedPackage/downloadApp")
	public ModelAndView downloadApp(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/popularityRedPackage/downloadApp");
		return model;
	}

	/**
	 * 
	 * @Description:五一四重礼
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午9:24:39
	 */
	@RequestMapping("/mayDay4Gifts/index")
	public ModelAndView mayDay4Gifts(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if(optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		model.addObject("data", activityLotteryService.mayDay4GiftsInit(builder));
		model.setViewName("/activity/mayDay4Gifts/index");
		return model;
	}

	/**
	 * 
	 * @Description:红包适配页
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午9:24:39
	 */
	@RequestMapping("/popRedPackage/mine")
	public ModelAndView popRedPackageMine(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.popRedPackageMineInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/share");
		return model;
	}
	
	/**
	 * 
	 * @Description:520活动
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午9:24:39
	 */
	@RequestMapping("/venus")
	public ModelAndView fellInLoveFor520(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.fellInLoveFor520Init(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/fellInLoveFor520");
		return model;
	}

	/**
	 *
	 * @Description:A轮专题页
	 * @param req
	 * @param resp
	 * @return
	 * @author:
	 * @time:2016年月22日 下午18:56:46
	 */
	@RequestMapping("Again")
	public ModelAndView Again(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/Again/Again");
		return model;
	}

	/**
	 * 
	 * @Description:破二十亿活动初始化
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午9:24:39
	 */
	@RequestMapping("/break2Billion")
	public ModelAndView break2BillionInit(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
			model.addObject("mKey", builder.getMemberId());
		}
		Object o = activityLotteryService.break2BillionInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/break2Billion/break2Billion");
		return model;
	}

	/**
	 * 
	 * @Description:APP跳转
	 * @param req
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月5日 上午9:55:15
	 */
	private Optional<Member> encryptionMemberFromApp(HttpServletRequest req, ModelAndView model) {
		try {
			String loginSource = req.getHeader("loginSource");
			if (StringUtil.isBlank(loginSource)) {
				loginSource = "3";
				model.addObject("loginSource", loginSource);
				MemberSessionDto memberDto = getMember();
				if (memberDto == null) {
					return Optional.fromNullable(null);
				}
				Long memberId = memberDto.getId();
				Member member = memberService.selectByPrimaryKey(memberId);
				return Optional.fromNullable(member);
			} else {
				model.addObject("loginSource", loginSource);
			}
			String isNeedYRWtoken = req.getParameter("isNeedYRWtoken");
			if (StringUtil.isBlank(isNeedYRWtoken)) {
				return Optional.fromNullable(null);
			}
			String encryptionId = req.getParameter("encryptionId");
			if (StringUtil.isBlank(encryptionId)) {
				return Optional.fromNullable(null);
			}
			List<String> encryptionCode = AES.getInstance().tokenDecrypt(encryptionId);
			if (Collections3.isEmpty(encryptionCode)) {
				return Optional.fromNullable(null);
			} else {
				Long memberId = Long.valueOf(encryptionCode.get(0));
				Member member = memberService.selectByPrimaryKey(memberId);
				if (member != null) {
					model.addObject("memberId", memberId);
				}
				return Optional.fromNullable(member);
			}
		} catch (Exception e) {
			return Optional.fromNullable(null);
		}
	}

	/**
	 * 
	 * @Description:里程拉新活动
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午9:24:39
	 */
	@RequestMapping("/marathon")
	public ModelAndView inviteFriend(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
			model.addObject("mKey", builder.getMemberId());
		}
		Object o = activityLotteryService.inviteFriendInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/marathon/marathon");
		return model;
	}
	/**
	 * 庆a轮活动初始化
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/celebrationA/index")
	public ModelAndView celebrationActivity(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.celebrationActivityInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/celebrationA");
		return model;
	}
	/*@RequestMapping("/celebrationA/index")
	public ModelAndView celebrationActivity(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("isReceived", false);
		model.addObject("isLogined", false);
		String activityId = PropertiesUtil.getProperties("activity.celebrationA.id");
		// 当前用户是否已登录
		if (getMember() != null) {
			model.addObject("isLogined", true);
			// 判断用户是否已经领取
			if (activityLotteryService.isReceived(getMember().getId(), Long.parseLong(activityId))) {
				model.addObject("isReceived", true);
			}
		}
		model.setViewName("/activity/celebrationA");
		return model;
	}
	*/
	/**
	 * 庆A轮活动领取红包
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/celebrationA/receive", method = RequestMethod.POST)
	@ResponseBody
	public Object celebrationAReceive(HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO<ActivityForAnniversary> rDO = new ResultDTO<ActivityForAnniversary>();
		if(getMember()==null){
			rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			return rDO;
		}
		//rDO= activityLotteryService.receiveCelebrationA(getMember().getId());
		return rDO;
	}
	
	/**
 	 * 
 	 * @Description:app 测评初始化
 	 * @param req
 	 * @param resp    http://192.168.0.37:8082/yourong-api/activity/evalua/getEvalua/mine
 	 * @return
 	 * @throws ManagerException
 	 * @author: chaisen
 	 * @time:2016年6月21日 下午2:26:07
 	 */
	@RequestMapping("/getEvalua/mine")
	public ModelAndView getEvaluaMine(HttpServletRequest req, HttpServletResponse resp) throws ManagerException {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromAppC(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
			model.addObject("mKey", builder.getMemberId());
		}
		Object o = memberInfoService.getEvaluaByMemberId(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/mobile/post/questionnaire");
		return model;
	}

	/**
	 *
	 * @Description:A轮回顾页面
	 * @param req
	 * @param resp
	 * @return
	 * @author:
	 * @time:2016年7月7日
	 */
	@RequestMapping("/againReview")
	public ModelAndView againReview(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/againReview");
		return model;
	}
	/**
	 * 
	 * @Description:玩转奥运
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年7月19日 下午1:55:52
	 */
	@RequestMapping("/olympic/index")
	public ModelAndView playOlympicActivity(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.olympicActivityInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/playOlympic");
		return model;
	}
	/**
	 * 新手六重礼
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/newSixGift/index")
	public ModelAndView newSixGift(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.newSixGiftInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/newSixGift");
		return model;
	}

	/**
	 * 
	 * @Description:九月战队初始化
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年9月9日 上午11:07:22
	 */
	@RequestMapping("/SeptemberTeam/init")
	public ModelAndView septemberTeamActivity(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.julyTeamInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/SeptemberTeam");
		return model;
	}
	/**
	 * 
	 * @Description:数据频道
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年10月11日 上午9:52:48
	 */
	@RequestMapping("/dataChannel/init")
	public ModelAndView dataChannelInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.dataChannelInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/mobile/post/dataCentre");
		return model;
	}
	
	@RequestMapping(value ="/dataChannel/realTimeData", method = RequestMethod.POST)
	@ResponseBody
	public Object realTimeData(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.realTimeData();
	}

	/**
	 *  周年庆
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/twoYearAnniversary/init")
	public ModelAndView anniversary(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.anniversaryInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("mobile/activity/twoYearAnniversary");
		return model;
	}
	/**
	 * 
	 * @Description:十月活动初始化
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年10月11日 上午9:53:56
	 */
	@RequestMapping("/october/init")
	public ModelAndView octoberInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.octoberInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/October");
		return model;
	}
	
	/**
	 * 
	 * @Description:周年庆专题页
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年10月28日 下午12:36:57
	 */
	@RequestMapping("/anniversarySpecial/init")
	public ModelAndView anniversarySpecial(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.anniversarySpecial(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/anniversarySpecial");
		return model;
	}
	
	/**
	 * 
	 * @Description:周年庆倒计时活动
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年10月12日 上午11:07:03
	 */
	@RequestMapping("/anniversaryLast/index")
	public ModelAndView anniversaryLastInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.anniversaryLastInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/anniversaryCountdown");
		return model;
	}
	/**
	 *
	 * @description 快投有奖
	 * @project APP
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/directReward/index")
	public ModelAndView DirectRewardPage(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/app/activity/directReward/index");
		return model;
	}
    /**
     *
     * @description 快投有奖-抽奖
     * @project APP
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/directReward/lottery")
    public ModelAndView DirectRewardLottery(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/app/activity/directReward/lottery");
        return model;
    }
	/**
	 *
	 * @description 快投有奖-专题页
	 * @project APP
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/directReward/topic")
	public ModelAndView DirectRewardTopic(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		
		Object data = activityLotteryService.getQuickRewardRule();
		model.addObject("xiaohei", JSON.toJSON(data));
		model.setViewName("/app/activity/directReward/topic");
		return model;
	}
	/**
	 *
	 * @description APP微信服务号介绍引导页
	 * @project APP
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/post/weixinService")
	public ModelAndView weixinService(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/app/activity/directReward/post/weixinService");
		return model;
	}

	/**
	 * 
	 * @Description:周年庆公益活动
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年11月15日 上午11:00:01
	 */
	@RequestMapping("/publicWelfare/init")
	public ModelAndView publicWelfareInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.publicWelfareInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("mobile/activity/publicWelfare");
		return model;
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @description 双旦活动
	 * @project APP
	 */
	@RequestMapping("/december/init")
	public ModelAndView decemberInit(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.doubleDanInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/december");
		return model;
	}

	/**
	 * 新年活动
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/newyear/index")
	public ModelAndView newYearIndex(HttpServletRequest req, HttpServletResponse resp){
		ModelAndView model=new ModelAndView();
		model.setViewName("/activity/newyear");
		ActivityForNewYearDto activityForNewYearDto =new ActivityForNewYearDto();
		Optional<Activity> newyear = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_NEWYEAR);
		if (!newyear.isPresent()) {
			model.addObject("data",JSON.toJSON(activityForNewYearDto));
			return model;
		}

		Activity newyearActivity = newyear.get();

		ActivityForNewYear activityForNewYear = LotteryContainer.getInstance().getObtainConditions(newyearActivity,
				ActivityForNewYear.class, ActivityConstant.ACTIVITY_NEWYEAR_KEY);
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		Long memberId=null;
		if (optOfMember.isPresent()){
			memberId=optOfMember.get().getId();
			String cycleConstraint = newyearActivity.getId() + "-" + memberId + ":Exchange";
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("activityId", newyearActivity.getId());
			paraMap.put("memberId", memberId);
			paraMap.put("cycleConstraint", cycleConstraint);
			//查询大集换大利的图标数量
			ActivityLottery activityLottery= activityLotteryService.selectByMemberActivity(paraMap);

			String cycleConstraint1 = newyearActivity.getId() + "-" + memberId + ":LuckyMoney";
			Map<String, Object> paraMap1 = new HashMap<String, Object>();
			paraMap1.put("activityId", newyearActivity.getId());
			paraMap1.put("memberId", memberId);
			paraMap1.put("cycleConstraint", cycleConstraint1);
			//查询领取红包的机会
			ActivityLottery activityLottery1= activityLotteryService.selectByMemberActivity(paraMap1);
			//邀请注册用户数
			int referralCount= memberService.queryMemberReferralCount(memberId,newyearActivity.getStartTime(),newyearActivity.getEndTime());
			//邀请注册且投资的用户数
			int referralTransactionCount = memberService.queryMemberReferralAndTransactionCount(memberId,newyearActivity.getStartTime(),newyearActivity.getEndTime());
			//用户10月之后投资数
			int transactionCount= transactionService.queryMemberTransactionCount(memberId, DateUtils.getDateFromString("2016-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"),newyearActivity.getEndTime());

			activityForNewYearDto.setReferralCount(referralCount);
			activityForNewYearDto.setReferralTransactionCount(referralTransactionCount);
			activityForNewYearDto.setTransactionCount(transactionCount);
			activityForNewYearDto.setStartTime(newyearActivity.getStartTime());
			activityForNewYearDto.setEndTime(newyearActivity.getEndTime());
			activityForNewYearDto.setStatus(newyearActivity.getActivityStatus());
			activityForNewYearDto.setRegisterTime(optOfMember.get().getRegisterTime());
			activityForNewYearDto.setLuckyMoneyTemplateIds(activityForNewYear.getLuckyMoneyTemplateIds());
            model.addObject("data",JSON.toJSON(activityForNewYearDto));
			return model;
		}
		activityForNewYearDto.setReferralCount(0);
		activityForNewYearDto.setReferralTransactionCount(0);
		activityForNewYearDto.setTransactionCount(0);
		activityForNewYearDto.setStartTime(newyearActivity.getStartTime());
		activityForNewYearDto.setEndTime(newyearActivity.getEndTime());
		activityForNewYearDto.setStatus(newyearActivity.getActivityStatus());
		activityForNewYearDto.setRegisterTime(null);
		activityForNewYearDto.setLuckyMoneyTemplateIds(activityForNewYear.getLuckyMoneyTemplateIds());
        model.addObject("data",JSON.toJSON(activityForNewYearDto));
		return model;
	}
	/**
	 *
	 * @Description:A轮回顾页面
	 * @param req
	 * @param resp
	 * @return
	 * @author:
	 * @time:2016年7月7日
	 */
	@RequestMapping("/joinPart")
	public ModelAndView joinPart(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/joinPart");
		return model;
	}

    /**
     *
     * @Description:2017除夕抢压岁包
     * @param req
     * @param resp
     * @return
     * @author:
     * @time:2017年01月22日
     */
	@RequestMapping(value = "/newYearEveRed/init")
	public ModelAndView newYearGrab(HttpServletRequest req, HttpServletResponse resp){
		ModelAndView model=new ModelAndView();
		model.setViewName("/activity/newYearEveRed");
		Optional<Activity> activityOptional = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_GRABBAG);
		if (!activityOptional.isPresent()) {
			return model;
		}

		Activity activity = activityOptional.get();

		ActivityForNewYearGrab grab = LotteryContainer.getInstance().getObtainConditions(activity,
				ActivityForNewYearGrab.class, ActivityConstant.ACTIVITY_GRABBAG_KEY);
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		if (activity.getStartTime().before(new Date())){
			List<ActivityGrabResultBiz> list= activityLotteryService.queryGrabResult(activity.getId());
			grab.setGrabResult(list);
		}
		//查询已抢压岁钱数目
		Integer count= activityLotteryService.queryCountByActivityId(activity.getId());
		grab.setGrabedCount(count);
		Long memberId=null;
		if (optOfMember.isPresent()){
			grab.setStartTime(activity.getStartTime());
			grab.setEndTime(activity.getEndTime());
			//查询是否已抢过
			boolean grabed= activityLotteryService.queryGrabStatus(memberId,activity.getId());
			grab.setGrabed(grabed);
			model.addObject("data",JSON.toJSON(grab));
			return model;
		}
		grab.setStartTime(activity.getStartTime());
		grab.setEndTime(activity.getEndTime());
		model.addObject("data",JSON.toJSON(grab));
		return model;
	}

	/**
	 * 元宵活动
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/lanternfestival/index")
	public String lanternFestival(Model model){
		ResultDO<Activity> resultDO= activityLotteryService.lanternFestival();
		model.addAttribute("activity",resultDO.getResult());
		return "/activity/lanternfestival";
	}

	/**
	 * 38女神活动
	 * @param req
	 * @param resp
	 * @return
     */
    @RequestMapping(value = "/womensDay/index")
    public ModelAndView womensDay(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model=new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		ResultDTO<ActivityForWomensDay> resultDTO=new ResultDTO<>();
		if (optOfMember.isPresent()){
			resultDTO = activityLotteryService.womensDayInit(optOfMember.get().getId());
		}else {
			resultDTO = activityLotteryService.womensDayInit(null);
		}
        model.addObject("data", JSON.toJSON(resultDTO.getResult()));
		model.setViewName("/activity/womensDay");
        return model;
    }

	@RequestMapping(value = "/subscription/init")
	public ModelAndView subscription(HttpServletRequest req){
		ModelAndView model=new ModelAndView();
		ResultDO<ActivityForSubscription> subscriptionResultDO=new ResultDO<>();
		model.setViewName("/activity/subscription");
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		if (optOfMember.isPresent()){
			subscriptionResultDO= activityLotteryService.subscriptionData(optOfMember.get().getId());
		}else {
			subscriptionResultDO= activityLotteryService.subscriptionData(null);
		}
		model.addObject("data",JSON.toJSON(subscriptionResultDO));
		return model;
	}
	
	/**
	 * 50亿初始化
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/fiveBillion/index")
	public ModelAndView fiveBillionIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.fiveBillionIndex(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/fiveBillion");
		return model;
	}
	
	/**
	 * 天降惊喜初始化
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/dayDropGold/index")
	public ModelAndView dayDropGoldInit(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.dayDropGoldInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/dayDropGold");
		return model;
	}

	/**
	 * 邀请好友活动
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/inviteFriend/index")
	public ModelAndView inviteFriend(HttpServletRequest req){
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		ResultDTO<ActivityForInviteFriend> resultDTO= activityLotteryService.inviteFriendData(builder);
		model.addObject("data", JSON.toJSON(resultDTO));
		model.setViewName("/activity/inviteFriend");
		return model;
	}
	/**
	 * 送花活动
	 * @param req
	 * @param resp
     * @return
     */
	@RequestMapping(value = "/flowers/index")
	public ModelAndView flowers(HttpServletRequest req, HttpServletResponse resp){
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		ResultDTO<ActivityForSubscription> subscriptionResultDO=new ResultDTO<>();
		if (optOfMember.isPresent()) {
			subscriptionResultDO = activityLotteryService.flowersData(optOfMember.get().getId());
		}else {
			subscriptionResultDO = activityLotteryService.flowersData(null);
		}
		model.addObject("data",JSON.toJSON(subscriptionResultDO));
        model.setViewName("/activity/flowers");
        return model;
	}

	/**
	 * 好春来
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/goodSpring/index")
	public ModelAndView goodSpring(HttpServletRequest req, HttpServletResponse resp){
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
		}
		Object o = activityLotteryService.springComingInit(builder);
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/activity/goodSpring");
		return model;
	}

	/**
	 * 活动数据
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/labor/index")
	public ModelAndView labor(HttpServletRequest req, HttpServletResponse resp){
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		ResultDTO<ActivityForLabor> laborResultDTO=new ResultDTO<>();
		if (optOfMember.isPresent()) {
			laborResultDTO = activityLotteryService.laborData(optOfMember.get().getId());
		}else {
			laborResultDTO = activityLotteryService.laborData(null);
		}
		model.addObject("data",JSON.toJSON(laborResultDTO));
		model.setViewName("/activity/labor");
		return model;
	}

}
