package com.yourong.web.controller;

import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.common.constant.ActivityConstant;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.biz.*;
import com.yourong.core.mc.model.query.ActivityLotteryResultQuery;
import com.yourong.web.dto.ActivityForNewYearDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.manager.MemberReferManager;
import com.yourong.core.uc.model.biz.MemberForLottery;
import com.yourong.web.cache.MyCacheManager;
import com.yourong.web.controller.BaseController;
import com.yourong.web.dto.ActivityForRedFridayDto;
import com.yourong.web.dto.FinEaringDto;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.service.ActivityLeadingSheepService;
import com.yourong.web.service.ActivityLotteryService;
import com.yourong.web.service.CouponService;
import com.yourong.web.service.LeaseBonusService;
import com.yourong.web.service.MemberReportService;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.ProjectService;
import com.yourong.web.service.TransactionService;
import com.yourong.web.utils.ServletUtil;
import com.yourong.web.utils.WebPropertiesUtil;

/**
 * 关于活动的controller
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("activity")
public class ActivityController extends BaseController {

	@Autowired
	private MemberReferManager memberReferManager;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;

	@Autowired
	private CouponService couponService;

	@Autowired
	private MyCacheManager myCacheManager;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private LeaseBonusService leaseBonusService;

	@Autowired
	private ActivityLotteryService activityLotteryService;

	@Autowired
	private ActivityLeadingSheepService activityLeadingSheepService;
	
	@Autowired
	private MemberReportService meberReportService;
	
	@Autowired
	private CouponManager couponManager;
	
	@Autowired
	private ActivityAfterTransactionManager activityAfterTransactionManager;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;
	
	@Autowired
	private TransactionManager myTransactionManager;
	
	@Autowired
	private TransactionManager transactionManager;
	
	

	/**
	 * 活动-新手注册认证活动-我的第一次
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/newMemberTheFirst")
	public ModelAndView newMemberTheFirst(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/newMemberTheFirst");
		return model;
	}

	/**
	 * 新浪支付专题
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/sinapay")
	public ModelAndView sinapayAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		List<FinEaringDto> finList = this.memberService.getfin();
		model.addObject("fin", finList);
		model.setViewName("/activity/sinapay");
		return model;
	}
	/**
	 * 活动初始化信息输出
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/init")
	public String activityInit(Model model){
		return "/common/activityInit";
	}

	/**
	 * 风险管控体系专题
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/ventureControl")
	public ModelAndView ventureControlAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/ventureControl");
		return model;
	}

	/**
	 * 我是王活动页
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/iamKing")
	public ModelAndView iamKingAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		try {
			List<ActivityForKing> refferalCountList = memberReferManager.getRefferalCountList();
			List<ActivityForKing> refferalInvestAmountList = transactionService.getRefferalInvestAmountList();
			model.addObject("refferalCountList", refferalCountList);
			model.addObject("refferalInvestAmountList", refferalInvestAmountList);
		} catch (Exception e) {
			logger.error("查询我是王活动排行榜出错", e);
		}

		model.setViewName("/activity/iamKing");
		return model;
	}

	/**
	 * 压岁钱活动页
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/giftmoney")
	public ModelAndView giftmoneyAct(HttpServletRequest req, HttpServletResponse resp) {
		StopWatch sw = new StopWatch();
		sw.start();
		ModelAndView model = new ModelAndView();
		List<MemberForLottery> members = new ArrayList<MemberForLottery>();
		String memberStr = RedisPlatformClient.getReceiveGiftMoneyMembers();
		buildMembers(members, memberStr);
		model.addObject("members", members);
		model.addObject("isReceived", false);
		model.addObject("isLogined", false);
		// 当前用户是否已领取
		if (getMember() != null) {
			model.addObject("isLogined", true);
			String memberId = getMember().getId().toString();
			if (StringUtil.isNotBlank(memberStr) && memberStr.contains(memberId)) {
				model.addObject("isReceived", true);
			}
		}

		model.setViewName("/activity/giftmoney");
		sw.stop();
		logger.info("打开压岁钱活动页耗时：" + sw.getTotalTimeMillis());
		return model;
	}

	private void buildMembers(List<MemberForLottery> members, String memberStr) {
		// StopWatch sw = new StopWatch();
		// sw.start();
		if (StringUtil.isNotBlank(memberStr)) {
			String[] memberArr = memberStr.split("\\" + StringUtil.VERTICAL_BAR);
			for (int i = 0; i < memberArr.length; i++) {
				String memberIdAndUsername = memberArr[i];
				String[] memberIdAndUsernameArr = memberIdAndUsername.split(",");
				Long memberId = Long.parseLong(memberIdAndUsernameArr[0]);
				String username = memberIdAndUsernameArr[1];
				String avatar = memberIdAndUsernameArr[2];
				MemberForLottery memberForLottery = new MemberForLottery();
				memberForLottery.setMemberId(memberId);
				memberForLottery.setMaskUsername(username);
				memberForLottery.setAvatars(avatar);
				members.add(memberForLottery);
			}
		}
		// sw.stop();
		// logger.info("转换领取用户列表耗时：" + sw.getTotalTimeMillis());

	}

	/**
	 * 领取压岁钱
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/receive/giftmoney")
	@ResponseBody
	public ResultDO<Coupon> receiveGiftmoney(HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<Coupon> result = new ResultDO<Coupon>();

		result = couponService.receiveGiftmoney(getMember().getId(), Long.parseLong(Config.activityIdForGiftMoney));
		return result;
	}

	/**
	 * 注册送彩票活动
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/lottery")
	public ModelAndView lotteryForRegister(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		try {
			List<MemberForLottery> memberForLotterys = memberService.getMembersForLottery();
			model.addObject("memberForLotterys", memberForLotterys);
		} catch (Exception e) {
			logger.error("注册送彩票活动出错", e);
		}

		model.setViewName("/activity/lottery");
		return model;
	}

	/**
	 * 一马当先、一鸣惊人、一锤定音、幸运女神活动
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	/*
	 * @RequestMapping("/firstInvest") public ModelAndView
	 * projectActivity(HttpServletRequest req, HttpServletResponse resp) {
	 * ModelAndView model = new ModelAndView(); try { PopularityInOutLogQuery
	 * query = new PopularityInOutLogQuery();
	 * query.setType(TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY.getType());
	 * query.setRemark
	 * (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST
	 * .getRemarks()); List<ActivityForFirstInvest> firstList =
	 * popularityInOutLogManager.selectActivityForFirstInvestList(query);
	 * query.setRemark
	 * (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST
	 * .getRemarks()); List<ActivityForFirstInvest> mostList =
	 * popularityInOutLogManager.selectActivityForFirstInvestList(query);
	 * query.setRemark
	 * (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST
	 * .getRemarks()); List<ActivityForFirstInvest> lastList =
	 * popularityInOutLogManager.selectActivityForFirstInvestList(query);
	 * query.setRemark
	 * (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST
	 * .getRemarks()); List<ActivityForFirstInvest> luckList =
	 * popularityInOutLogManager.selectActivityForFirstInvestList(query);
	 * model.addObject("firstList", firstList); model.addObject("mostList",
	 * mostList); model.addObject("lastList", lastList);
	 * model.addObject("luckList", luckList); List<TransactionForFirstInvestAct>
	 * transactionForFirstInvestActs = transactionService.selectTopTenInvest();
	 * model.addObject("transactionForFirstInvestActs",
	 * transactionForFirstInvestActs); } catch (Exception e) {
	 * logger.error("查询 一马当先、一鸣惊人、一锤定音、幸运女神活动出错", e); }
	 * 
	 * model.setViewName("/activity/firstInvest"); return model; }
	 */
	/**
	 * 土豪排行2
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/rankingList2nd")
	public ModelAndView rankingList2ndActivity(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		org.apache.commons.lang3.time.StopWatch watch = new org.apache.commons.lang3.time.StopWatch();
		watch.start();
		try {
			PopularityInOutLogQuery query = new PopularityInOutLogQuery();
			query.setType(TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY.getType());
			query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
			query.setStartTime(DateUtils.getDateFromString(Config.rankingStartTime, DateUtils.TIME_PATTERN));// 活动的时间范围
			query.setEndTime(DateUtils.getDateFromString(Config.rankingEndTime, DateUtils.TIME_PATTERN));
			List<ActivityForFirstInvest> firstList = popularityInOutLogManager.selectActivityForFirstInvestList(query);
			query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
			List<ActivityForFirstInvest> mostList = popularityInOutLogManager.selectActivityForFirstInvestList(query);
			query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
			List<ActivityForFirstInvest> lastList = popularityInOutLogManager.selectActivityForFirstInvestList(query);
			query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
			List<ActivityForFirstInvest> luckList = popularityInOutLogManager.selectActivityForFirstInvestList(query);
			watch.split();
			logger.info("土豪排行榜的获取一羊领头等列表总时间：" + watch.getSplitTime());
			// //判断活动是否结束
			boolean endFlag = DateUtils.getCurrentDate().after(DateUtils.getDateFromString(Config.rankingEndTime, DateUtils.TIME_PATTERN));
			model.addObject("firstList", firstList);
			model.addObject("mostList", mostList);
			model.addObject("lastList", lastList);
			model.addObject("luckList", luckList);
			model.addObject("endFlag", endFlag);
			watch.stop();
			logger.info("土豪排行榜第二季页面加载总时间：" + watch.getTime());
		} catch (Exception e) {
			logger.error("查询 一马当先、一鸣惊人、一锤定音、幸运女神活动出错", e);
		}

		model.setViewName("/activity/rankingList2nd");
		return model;
	}

	/**
	 * 获取投资累计总额某一范围内的用户
	 * 
	 * @param range
	 * @return
	 */
	@RequestMapping(value = "investRanking2nd")
	@ResponseBody
	public Object getMemberMeetInvestRange(@RequestParam("range") int range) {
		ActivityForRankingList2nd list2nd = new ActivityForRankingList2nd();
		switch (range) {
		case 12:
			list2nd = transactionService.getMemberMeetTotalInvestRange(null, BigDecimal.valueOf(Long.valueOf(Config.investAmount1)),
					Integer.valueOf(Config.rankingNum));
			break;
		case 11:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount1)),
					BigDecimal.valueOf(Long.valueOf(Config.investAmount2)), Integer.valueOf(Config.rankingNum));
			break;
		case 10:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount2)),
					BigDecimal.valueOf(Long.valueOf(Config.investAmount3)), Integer.valueOf(Config.rankingNum));
			break;
		case 9:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount3)),
					BigDecimal.valueOf(Long.valueOf(Config.investAmount4)), Integer.valueOf(Config.rankingNum));
			break;
		case 8:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount4)),
					BigDecimal.valueOf(Long.valueOf(Config.investAmount5)), Integer.valueOf(Config.rankingNum));
			break;
		case 7:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount5)),
					BigDecimal.valueOf(Long.valueOf(Config.investAmount6)), Integer.valueOf(Config.rankingNum));
			break;
		case 6:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount6)),
					BigDecimal.valueOf(Long.valueOf(Config.investAmount7)), Integer.valueOf(Config.rankingNum));
			break;
		case 5:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount7)),
					BigDecimal.valueOf(Long.valueOf(Config.investAmount8)), Integer.valueOf(Config.rankingNum));
			break;
		case 4:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount8)),
					BigDecimal.valueOf(Long.valueOf(Config.investAmount9)), Integer.valueOf(Config.rankingNum));
			break;
		case 3:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount9)),
					BigDecimal.valueOf(Long.valueOf(Config.investAmount10)), Integer.valueOf(Config.rankingNum));
			break;
		case 2:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount10)),
					BigDecimal.valueOf(Long.valueOf(Config.investAmount11)), Integer.valueOf(Config.rankingNum));
			break;
		case 1:
			list2nd = transactionService.getMemberMeetTotalInvestRange(BigDecimal.valueOf(Long.valueOf(Config.investAmount11)), null,
					Integer.valueOf(Config.rankingNum));
			break;
		default:
			break;
		}
		return list2nd;
	}

	/**
	 * 着落页
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/earningsLand")
	public ModelAndView earningsLand(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/earningsLand");
		return model;
	}

	/**
	 * 小明和小刚的故事活动
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/story")
	public ModelAndView story(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		// 判断是否登录
		model.addObject("logined", false);
		if (getMember() != null) {
			model.addObject("logined", true);
		}
		ProjectForFront noviceProject = projectService.getIndexNoviceProject();
		model.addObject("newProjectId", noviceProject.getId());

		List<Coupon> coupons = couponService.getCouponsByCouponTemplateId(Long.valueOf(Config.activityIdForXiaomingStory));
		model.setViewName("/activity/story");
		model.addObject("coupons", coupons);

		return model;
	}

	/**
	 * 我要领取（小明和小刚的故事）
	 * 
	 * @param req
	 * @param resps
	 * @return
	 */
	@RequestMapping(value = "receive")
	@ResponseBody
	public ResultDO<Coupon> receiveCoupon(HttpServletRequest req, HttpServletResponse resps) {
		ResultDO<Coupon> result = new ResultDO<Coupon>();
		result = couponService.receiveCoupon(getMember().getId(), Long.valueOf(Config.activityIdForXiaomingStory));
		return result;
	}

	/**
	 * 租赁分红
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */

	@RequestMapping("/leaseBonus")
	public ModelAndView leaseBonusAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/leaseBonus");
		return model;
	}

	/**
	 * 安全卡专题页
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/securityCard")
	public ModelAndView securityCardAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		List<FinEaringDto> finList = this.memberService.getfin();
		model.addObject("fin", finList);
		model.setViewName("/activity/securityCard");
		return model;
	}

	/**
	 * 人气值专题页
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/popularityValues")
	public ModelAndView popularityValuesAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		int topNum = 10;
		try {
			List<PopularityInOutLog> popularityLogs = popularityInOutLogManager.findTopPopularityMember(topNum);
			model.addObject("popularityTop10", popularityLogs);
			List<PopularityInOutLog> exchangeCoupon = popularityInOutLogManager.findLastExchangeCoupon();
			model.addObject("exchangeCoupon", exchangeCoupon);
		} catch (ManagerException e) {
			logger.error("获取人气值排行前10异常", e);
		}
		List<FinEaringDto> finList = this.memberService.getfin();
		model.addObject("fin", finList);
		model.setViewName("/activity/popularityValues");
		return model;
	}

	/**
	 * 土豪请出列
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/finalRanking")
	public ModelAndView finalRankingAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/finalRanking");
		return model;
	}

	/**
	 * 大额投资活动
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/single/invest")
	public ModelAndView heavyInvestmentAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		PopularityInOutLogQuery query = new PopularityInOutLogQuery();
		query.setType(TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY.getType());
		query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_SINGLE_INVEST.getRemarks());
		try {
			List<ActivityForSingleInvest> singleInvestList = popularityInOutLogManager.selectActivityForSingleInvest(query);
			model.addObject("singleInvestList", singleInvestList);
		} catch (Exception e) {
			logger.error("查询大额投资活动出错", e);
		}
		model.setViewName("/activity/heavyInvestment");
		return model;
	}

	/**
	 * 电子数据保全中心
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/eDataSecurity")
	public ModelAndView eDataSecurityAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/eDataSecurity");
		return model;
	}

	/**
	 * 手机客户端活动页
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/mobilePhoneApp")
	public ModelAndView mobilePhoneAppAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/mobilePhoneApp");
		return model;
	}

	/**
	 * 领取红色星期五现金券
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/receive/redFriday", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<Coupon> receiveRedFriday(HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<Coupon> result = new ResultDO<Coupon>();

		result = couponService.receiveRedFriday(getMember().getId(), Long.parseLong(Config.activityIdForRedFriday));
		return result;
	}

	/**
	 * 红色星期五活动页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/redFriday")
	public ModelAndView redFridayAct(HttpServletRequest req, HttpServletResponse resp) {
		// StopWatch sw = new StopWatch();
		// sw.start();
		Long activityId = Long.parseLong(Config.activityIdForRedFriday);
		ModelAndView model = new ModelAndView();
		model.addObject("receivedNum", RedisPlatformClient.getRedFridayCouponCount());
		// model.addObject("activityForRedFridays",
		// couponService.getReceivedCouponByActivityId(activityId));
		model.addObject("isLogined", false);
		// 当前用户是否已领取
		if (getMember() != null) {
			model.addObject("isLogined", true);
			model.addObject("shortUrl", getMember().getShortUrl());
			model.addObject("isReceived",
					couponService.redFridayIsReceived(getMember().getId(), Long.parseLong(Config.activityIdForRedFriday)));
		}

		model.setViewName("/activity/redFriday");
		// sw.stop();
		// logger.info("打开红色星期五活动页面耗时：" + sw.getTotalTimeMillis());
		return model;
	}

	@RequestMapping("/queryReceivedCouponMember")
	@ResponseBody
	public ResultDO getReceivedCouponByActivityId() {
		ResultDO result = new ResultDO();
		Long activityId = Long.parseLong(Config.activityIdForRedFriday);
		List<ActivityForRedFriday> list = couponService.getReceivedCouponByActivityId(activityId);
		if (!Collections3.isEmpty(list)) {
			List<ActivityForRedFridayDto> listDto = BeanCopyUtil.mapList(list, ActivityForRedFridayDto.class);
			result.setResult(listDto);
		}
		return result;
	}

	/**
	 * 亿路上有你抽奖页
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/yiRoad/index")
	public ModelAndView yiRoadLotteryAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		/*
		 * //亿路上有你抽奖最新结果 List<ActivityLotteryResultBiz> lotteryList =
		 * activityLotteryService.yiRoadNewLotteryResult(); //亿举夺魁排行榜
		 * List<ActivityLotteryBiz> topList =
		 * activityLotteryService.yiRoadRankList(); //分享排行榜
		 * List<ActivityLotteryResultBiz> shareList =
		 * activityLotteryService.yiRoadShareList(); //app下载次数 int downNum =
		 * activityLotteryService.getDownTotalCount();
		 * 
		 * model.addObject("lotteryList", lotteryList);
		 * model.addObject("topList", topList); model.addObject("shareList",
		 * shareList); model.addObject("downNum", downNum);
		 */
		model.setViewName("/activity/yiRoadLottery");
		return model;
	}

	/**
	 * 亿路上有你剩余抽奖次数
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/yiRoad/realCount", method = RequestMethod.POST)
	@ResponseBody
	public Object showYiRoadLotteryRealCount(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMember().getId();
		return activityLotteryService.yiRoadNewLotteryCount(memberId);
	}

	/**
	 * 亿路上有你当天是否分享
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/yiRoad/shareFlag", method = RequestMethod.POST)
	@ResponseBody
	public Object showYiRoadShareFlag(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMember().getId();
		return activityLotteryService.showYiRoadShareFlag(memberId);
	}

	/**
	 * 亿路上有你抽奖最新结果
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/yiRoad/showList", method = RequestMethod.POST)
	@ResponseBody
	public Object showYiRoadLotteryResult(HttpServletRequest req, HttpServletResponse resp) {
		List<ActivityLotteryResultBiz> lotteryList = activityLotteryService.yiRoadNewLotteryResult();
		return lotteryList;
	}

	/**
	 * 亿路上有你抽奖
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/yiRoad/drawLottery", method = RequestMethod.POST)
	@ResponseBody
	public Object yiRoadDrawLottery(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMember().getId();
		return activityLotteryService.yiRoadDrawLottery(memberId);
	}

	/**
	 * 亿路上有你亿举夺魁排行榜
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/yiRoad/rankList", method = RequestMethod.POST)
	@ResponseBody
	public Object yiRoadRankList(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.yiRoadRankList();
	}

	/**
	 * 获取平台下载app总数
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/yiRoad/appDownCount", method = RequestMethod.POST)
	@ResponseBody
	public Object appDownCount(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.getDownTotalCount();
	}

	/**
	 * 亿起分享
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/yiRoad/share", method = RequestMethod.POST)
	@ResponseBody
	public Object yiRoadShare(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMember().getId();
		return activityLotteryService.yiRoadShare(memberId);
	}

	/**
	 * 亿路上有你分享滚屏
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/yiRoad/shareList", method = RequestMethod.POST)
	@ResponseBody
	public Object yiRoadShareList(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.yiRoadShareList();
	}

	/**
	 * 土豪请再次出列
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/finalRanking2nd")
	public ModelAndView finalRanking2ndAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/finalRanking2nd");
		return model;
	}

	/**
	 * 四重礼
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/quadrupleGift")
	public ModelAndView quadrupleGiftAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/quadrupleGift");
		return model;
	}

	/**
	 * 一羊领头活动专题页——可一羊领头等的项目列表
	 * 
	 * @param req
	 * @param resp
	 * @param remark
	 * @return
	 */
	@RequestMapping(value = "/leadingSheep/projects", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Object getLeadingSheepProjects(HttpServletRequest req, HttpServletResponse resp) {
		return activityLeadingSheepService.getProjectForLeadingSheeps();
	}

	/**
	 * 一羊领头活专题页——一羊领头等获取记录列表
	 * 
	 * @param req
	 * @param resp
	 * @param popularityInOutLogQuery
	 * @return
	 */
	@RequestMapping(value = "/leadingSheep/gainList", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Object getLeadingSheepListData(HttpServletRequest req, HttpServletResponse resp,
			@ModelAttribute("PopularityInOutLogQuery") PopularityInOutLogQuery popularityInOutLogQuery) {
		popularityInOutLogQuery.setLimitSize(20);
		List<ActivityForFirstInvest> firstInvestList = activityLeadingSheepService.selectLeadingSheepList(popularityInOutLogQuery);
		acessControllerAllowOrigin(resp);

		return firstInvestList;
	}

	/**
	 * JS 跨域
	 * 
	 * @param resp
	 */
	private void acessControllerAllowOrigin(HttpServletResponse resp) {
		// 可以跨域
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
		resp.setHeader("Access-Control-Allow-Methods", "GET");
		resp.setHeader("Allow", "GET");
	}

	/**
	 * 获取一羊领头风云榜
	 */
	@RequestMapping(value = "/leadingSheep/rankList", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Object getLeadingSheepRankList(HttpServletRequest req, HttpServletResponse resp) {
		acessControllerAllowOrigin(resp);
		return activityLeadingSheepService.selectLeadingSheepRanksAndCount();
	}

	/**
	 * 月度排行
	 */
	@RequestMapping(value = "/monthlyRank/list", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Object getMonthlyRankData(HttpServletRequest req, HttpServletResponse resp) {
		List<ActivityForRankingList2nd> list = Lists.newArrayList();
		ActivityForRankingList2nd firstRank = transactionService.getMonthlyMemberMeetTotalInvestRange(
				BigDecimal.valueOf(Long.valueOf(WebPropertiesUtil.getProperties("activity.monthlyTotalAmount1"))), null, null);
		list.add(firstRank);
		ActivityForRankingList2nd secondRank = transactionService.getMonthlyMemberMeetTotalInvestRange(
				BigDecimal.valueOf(Long.valueOf(WebPropertiesUtil.getProperties("activity.monthlyTotalAmount2"))),
				BigDecimal.valueOf(Long.valueOf(WebPropertiesUtil.getProperties("activity.monthlyTotalAmount1"))), null);
		list.add(secondRank);
		ActivityForRankingList2nd thirdRank = transactionService.getMonthlyMemberMeetTotalInvestRange(
				BigDecimal.valueOf(Long.valueOf(WebPropertiesUtil.getProperties("activity.monthlyTotalAmount3"))),
				BigDecimal.valueOf(Long.valueOf(WebPropertiesUtil.getProperties("activity.monthlyTotalAmount2"))), null);
		list.add(thirdRank);
		ActivityForRankingList2nd otherRank = transactionService.getMonthlyMemberMeetTotalInvestRange(null,
				BigDecimal.valueOf(Long.valueOf(WebPropertiesUtil.getProperties("activity.monthlyTotalAmount3"))),
				Integer.valueOf(WebPropertiesUtil.getProperties("activity.monthlyRankNum")));
		list.add(otherRank);
		// 可以跨域
		acessControllerAllowOrigin(resp);
		return list;
	}

	/**
	 * 获取我的排名
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "/monthlyRank/mine", method = RequestMethod.POST)
	@ResponseBody
	public Object getMyRank(HttpServletRequest req, HttpServletResponse res) {
		Map<String, Object> rankMap = Maps.newHashMap();
		String startTimeStr = WebPropertiesUtil.getProperties("activity.monthlyStartTime");
		String endTimeStr = WebPropertiesUtil.getProperties("activity.monthlyEndTime");
		Date startDate = DateUtils.getDateFromString(startTimeStr, DateUtils.TIME_PATTERN);
		Date endDate = DateUtils.getDateFromString(endTimeStr, DateUtils.TIME_PATTERN);
		Date nowDate = DateUtils.getCurrentDate();
		if (DateUtils.getCurrentDate().getTime() < startDate.getTime()) {
			rankMap.put("result", 0);
			return rankMap;
		}
		if (nowDate.getTime() > endDate.getTime()) {
			rankMap.put("result", 2);
			return rankMap;
		}
		rankMap.put("result", 1);
		Map<String, Object> map = Maps.newHashMap();
		map.put("memberId", getMember().getId());
		rankMap.put("rank", transactionService.findMyRank(map));
		return rankMap;
	}

	/**
	 * 月度排行-奇偶排行
	 */
	@RequestMapping(value = "/monthlyRank/weeklyList", method = RequestMethod.POST)
	@ResponseBody
	public Object getWeeklyRankData(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.weeklyRank();
	}

	/**
	 * 八月豪情
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/monthlyRank")
	public ModelAndView monthlyRankAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/monthlyRank");
		return model;
	}

	/**
	 * 生日月
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/birthdayGift")
	public ModelAndView birthdayGift(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		MemberSessionDto userName = ServletUtil.getUserDO();
		if (userName != null) {
			int status = memberService.getBirthdayStatus(userName.getBirthday());
			model.addObject("isVerifyTrueName", ServletUtil.isVerifyTrueName());
			model.addObject("isBirthday", status == 1 ? true : false);
		}
		model.setViewName("/activity/birthdayGift");
		return model;
	}

	/**
	 * 投房有喜-查询投资列表
	 */
	@RequestMapping(value = "/investHouse/list", method = RequestMethod.POST)
	@ResponseBody
	public Object getInvestHouseList(HttpServletRequest req, HttpServletResponse resp) {
		return transactionService.getInvestHouseList();
	}

	/**
	 * 投房有喜-优惠券
	 */
	@RequestMapping(value = "/investHouse/coupon", method = RequestMethod.POST)
	@ResponseBody
	public Object getInvestHouseCoupon(HttpServletRequest req, HttpServletResponse resp) {
		return transactionService.getInvestHouseCoupon();
	}

	/**
	 * 投房有喜-查看我的排名
	 */
	@RequestMapping(value = "/investHouse/myRank", method = RequestMethod.POST)
	@ResponseBody
	public Object getMyInvestHouseRank(HttpServletRequest req, HttpServletResponse resp) {
		return transactionService.getMyInvestHouseRank(getMember().getId());
	}

	/**
	 * 投房有喜
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/investHouse")
	public ModelAndView investHouseAct(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/investHouseJoy");
		return model;
	}

	/**
	 * 领取中秋现金券
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/midAutumn/coupon", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<ActivityBiz> midAutumnCoupon(HttpServletRequest req, HttpServletResponse resp) {
		String activityId = WebPropertiesUtil.getProperties("activity.midAutumn.id");
		return couponService.midAutumnCoupon(getMember().getId(), Long.parseLong(activityId));
	}

	/**
	 * 中秋活动页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/midAutumn")
	public ModelAndView midAutumnIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		String activityId = WebPropertiesUtil.getProperties("activity.midAutumn.id");
		Activity ac = activityLotteryService.getActivityId(Long.parseLong(activityId));
		if (ac != null) {
			model.addObject("startTime", ac.getStartTime().getTime());
			model.addObject("endTime", ac.getEndTime().getTime());
		}
		model.setViewName("/activity/midAutumn");
		return model;
	}

	/**
	 * 领取中秋现金券
	 * 
	 * @author chaisen
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/memberReport/coupon", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<ActivityBiz> memberReport(HttpServletRequest req, HttpServletResponse resp) {
		String activityId = WebPropertiesUtil.getProperties("activity.midAutumn.id");
		return couponService.midAutumnCoupon(getMember().getId(), Long.parseLong(activityId));
	}

	/**
	 * 活动报名页面
	 * 
	 * @author chaisen
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/memberReport")
	public ModelAndView memberReportIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		String activityId = WebPropertiesUtil.getProperties("activity.midAutumn.id");
		Activity ac = activityLotteryService.getActivityId(Long.parseLong(activityId));
		if (ac != null) {
			model.addObject("startTime", ac.getStartTime().getTime());
			model.addObject("endTime", ac.getEndTime().getTime());
		}
		// 当前用户是否已报名
		if (getMember() != null) {
			model.addObject("isReceived", meberReportService.memberReportIsApply(getMember().getId()));
		}
		model.addObject("totalMemberReport", meberReportService.countHaveReported());
		model.setViewName("/activity/memberReport");
		// model.setViewName("/activity/reportIndex");
		return model;
	}

	/**
	 * 周年庆
	 * 
	 * @author wangyanji
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/anniversary/index")
	public ModelAndView anniversary(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		String activityId = PropertiesUtil.getProperties("activity.anniversary.id");
		Optional<Activity> ac = activityLotteryService.getActivityByCache(Long.parseLong(activityId));
		if (ac.isPresent()) {
			model.addObject("anniversaryStartTime", ac.get().getStartTime());
			model.addObject("anniversaryEndTime", ac.get().getEndTime());
		}
		model.setViewName("/activity/anniversary");
		return model;
	}

	/**
	 * 周年庆预热
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/meetingPreHeat")
	public ModelAndView meetingPreHeat(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/meetingPreHeat");
		return model;
	}

	/**
	 * 周年庆-奖励大放送初始化
	 * 
	 * @author wangyanji
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/anniversary/initPrize", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<ActivityForAnniversary> anniversaryPrizeInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.anniversaryPrizeInit(getMember() != null ? getMember().getId() : null);
	}

	/**
	 * 周年庆-奖励大放送初始化-领取
	 * 
	 * @author wangyanji
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/anniversary/receivePrize", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<ActivityForAnniversary> anniversaryReceivePrize(HttpServletRequest req, HttpServletResponse resp) {
		Long activityId = ServletRequestUtils.getLongParameter(req, "activityId", 0L);
		return activityLotteryService.anniversaryReceivePrize(getMember().getId(), activityId);
	}

	/**
	 * 周年庆-巅峰1小时
	 */
	@RequestMapping(value = "/anniversary/oneHour", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<ActivityForRankListBiz> anniversaryOneHour(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.anniversaryOneHourList();
	}

	/**
	 * 周年庆-幸运25宫格
	 */
	@RequestMapping(value = "/anniversary/twentyFive", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<?> anniversaryTwentyFiveGrid(HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		Integer chip = ServletRequestUtils.getIntParameter(req, "chip", 0);
		if (chip.equals(0)) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return rDO;
		}
		return activityLotteryService.anniversaryTwentyFiveGrid(getMember().getId(), chip);
	}

	/**
	 * 周年庆-幸运25宫格抽奖最新结果
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/anniversary/twentyFive/list", method = RequestMethod.POST)
	@ResponseBody
	public Object anniversaryTwentyFiveGridResult(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.anniversaryTwentyFiveGridResult();
	}

	/**
	 * 周年庆-生成分享链接
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/anniversary/shareUrl", method = RequestMethod.POST)
	@ResponseBody
	public Object anniversaryShareUrl(HttpServletRequest req, HttpServletResponse resp) {
		Long transactionId = ServletRequestUtils.getLongParameter(req, "transactionId", 0L);
		return activityLotteryService.anniversaryShareUrl(transactionId, getMember().getId());
	}

	/**
	 * 周年庆后续
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/anniversaryReview")
	public ModelAndView anniversaryReview(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/anniversaryReview");
		return model;
	}

	/**
	 * 
	 * @Description:双旦活动入口
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午9:24:39
	 */
	@RequestMapping("/decGift/index")
	public ModelAndView doubleDanIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/doubleDan");
		return model;
	}

	/**
	 * 
	 * @Description:圣诞节&元旦初始化
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午11:08:21
	 */
	@RequestMapping(value = "/doubleDan/init", method = RequestMethod.POST)
	@ResponseBody
	public Object doubleDanInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.doubleDanInit(Optional.fromNullable(getMember()));
	}

	/**
	 * 
	 * @Description:圣诞节领券
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午11:08:21
	 */
	@RequestMapping(value = "/doubleDan/receive/christmas", method = RequestMethod.POST)
	@ResponseBody
	public Object doubleDanReceiveChristmas(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.doubleDanReceiveChristmas(getMember().getId());
	}

	/**
	 * 
	 * @Description:元旦领券
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午11:08:21
	 */
	@RequestMapping(value = "/doubleDan/receive/newYear", method = RequestMethod.POST)
	@ResponseBody
	public Object doubleDanReceiveNewYear(HttpServletRequest req, HttpServletResponse resp) {
		int giftIndex = ServletRequestUtils.getIntParameter(req, "giftIndex", -1);
		return activityLotteryService.doubleDanReceiveNewYear(getMember().getId(), giftIndex);
	}

	/**
	 * 
	 * @Description:神秘新年礼
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午11:08:21
	 */
	@RequestMapping(value = "/doubleDan/receive/scretGift", method = RequestMethod.POST)
	@ResponseBody
	public Object doubleDanReceiveScretGift(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.doubleDanReceiveScretGift(getMember().getId());
	}

//	/**
//	 *
//	 * @Description:融资发布会后续页面
//	 * @param req
//	 * @param resp
//	 * @return
//	 * @author: liuwebtao
//	 * @time:2015年11月20日 上午9:24:39
//	 */
//	@RequestMapping("/PreA/index")
//	public ModelAndView PreAIndex(HttpServletRequest req, HttpServletResponse resp) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("/activity/PreA");
//		return model;
//	}

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
		model.setViewName("/activity/millionFund");
		return model;
	}

	/**
	 * 
	 * @Description:百万现金券活动初始化
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午11:08:21
	 */
	@RequestMapping(value = "/millionFund/init", method = RequestMethod.POST)
	@ResponseBody
	public Object millionFundInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.millionFundInit();
	}

	/**
	 * 
	 * @Description:春节活动
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping("/springFestival/index")
	public ModelAndView springFestival(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		//兑换现金券
		Long[] templateIds = { Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate50")),
				Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate100")),
				Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate500")),
				Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate1000")) };
		model.addObject("cashTemplates", couponService.findExchangeCouponsByIds(templateIds));
		model.setViewName("/activity/springFestival");
		return model;
	}

	/**
	 * 
	 * @Description:春节活动初始化
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "/springFestival/init", method = RequestMethod.POST)
	@ResponseBody
	public Object springFestivalInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.springFestivalInit(Optional.fromNullable(getMember()));
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
		return activityLotteryService.springFestivalReceiveCoupon(getMember().getId());
	}

	/**
	 * 
	 * @Description:获取红包分享链接
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "/redBag/shareUrl", method = RequestMethod.POST)
	@ResponseBody
	public Object redBagShareUrl(HttpServletRequest req, HttpServletResponse resp) {
		Long transactionId = ServletRequestUtils.getLongParameter(req, "transactionId", 0L);
		return activityLotteryService.getTransactionRedBagUrl(transactionId, getMember().getId());
	}

	/**
	 * 
	 * @Description:获取红包规则
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@RequestMapping(value = "/redBag/getRule", method = RequestMethod.POST)
	@ResponseBody
	public Object getRedBagRule(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.getRedBagRule();
	}

	/**
	 *50元现金券
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/50cashCoupon")
	public ModelAndView cashCoupon(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/50cashCoupon");
		return model;
	}

	/**
	 * 
	 * @Description:破十亿活动入口
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午9:24:39
	 */
	@RequestMapping("/breakBillion/index")
	public ModelAndView breakBillionIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/breakBillion");
		return model;
	}

	/**
	 * 
	 * @Description:破十亿活动初始化
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午11:08:21
	 */
	@RequestMapping(value = "/breakBillion/init", method = RequestMethod.POST)
	@ResponseBody
	public Object breakBillionInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.breakBillionInit();
	}

	/**
	 * 
	 * @Description:获取新手任务动态
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午11:08:21
	 */
	@RequestMapping(value = "/newerMission/init", method = RequestMethod.POST)
	@ResponseBody
	public Object newerMissionInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.getNewerMissionList();
	}

	/**
	 *等本等息
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/financialPlan")
	public ModelAndView financialPlan(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/financialPlan");
		return model;
	}

	/**
	 * 
	 * @Description:投房有礼初始化
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping("/house2Gifts/index")
	public ModelAndView house2Gifts(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/houseTwoGifts");
		return model;
	}

	/**
	 * 
	 * @Description:投房有礼
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping(value = "/house2Gifts/init", method = RequestMethod.POST)
	@ResponseBody
	public Object house2GiftsInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.house2GiftsInit();
	}

	/**
	 * 
	 * @Description:五一四重礼
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping("/mayDay4Gifts/index")
	public ModelAndView mayDay4Gifts(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/mayDay4Gifts");
		return model;
	}

	/**
	 * 
	 * @Description:五一四重礼初始化接口
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping(value = "/mayDay4Gifts/init", method = RequestMethod.POST)
	@ResponseBody
	public Object mayDay4GiftsInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.mayDay4GiftsInit(Optional.fromNullable(getMember()));
	}

	/**
	 * 
	 * @Description:五一四重礼领券接口
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping(value = "/mayDay4Gifts/receive", method = RequestMethod.POST)
	@ResponseBody
	public Object mayDay4GiftsReceive(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMember().getId();
		int couponAmount = ServletRequestUtils.getIntParameter(req, "couponAmount", 0);
		return activityLotteryService.mayDay4GiftsReceive(memberId, couponAmount);
	}

	/**
	 * 
	 * @Description:520活动
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping("/venus")
	public ModelAndView fellInLoveFor520(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/fellInLove");
		return model;
	}

	/**
	 * 
	 * @Description:520活动
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping(value = "/venus/init", method = RequestMethod.POST)
	@ResponseBody
	public Object fellInLoveFor520Init(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.fellInLoveFor520Init(Optional.fromNullable(getMember()));
	}

	/**
	 * 
	 * @Description:破二十亿活动入口
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午9:24:39
	 */
	@RequestMapping("/break2Billion/index")
	public ModelAndView break2BillionIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/break2Billion");
		return model;
	}

	/**
	 * 
	 * @Description:破二十亿活动初始化
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午11:08:21
	 */
	@RequestMapping(value = "/break2Billion/init", method = RequestMethod.POST)
	@ResponseBody
	public Object break2BillionInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.break2BillionInit(Optional.fromNullable(getMember()));
	}

	/**
	 * 
	 * @Description:破二十亿活动互动
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 上午11:08:21
	 */
	@RequestMapping(value = "/break2Billion/receive", method = RequestMethod.POST)
	@ResponseBody
	public Object break2BillionReceive(HttpServletRequest req, HttpServletResponse resp) {
		int activityPart = ServletRequestUtils.getIntParameter(req, "activityPart", 0);
		return activityLotteryService.break2BillionReceive(getMember().getId(), activityPart);
	}

	/**
	 * 
	 * @Description:里程拉新
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping("/marathon")
	public ModelAndView inviteFriend(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/marathon");
		return model;
	}

	/**
	 * 
	 * @Description:里程拉新初始化
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping(value = "/marathon/init", method = RequestMethod.POST)
	@ResponseBody
	public Object inviteFriendInit(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.inviteFriendInit(Optional.fromNullable(getMember()));
	}

	/**
	 * 
	 * @Description:里程拉新领取
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping(value = "/marathon/receive", method = RequestMethod.POST)
	@ResponseBody
	public Object inviteFriendReceive(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMember().getId();
		int rewardType = ServletRequestUtils.getIntParameter(req, "rewardType", 0);
		return activityLotteryService.inviteFriendReceive(memberId, rewardType);
	}

	/**
	 * 
	 * @Description:里程拉新助力榜
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月1日 上午11:24:03
	 */
	@RequestMapping(value = "/marathon/list", method = RequestMethod.POST)
	@ResponseBody
	public Object inviteFriendList(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMember().getId();
		int startNo = ServletRequestUtils.getIntParameter(req, "startNo", 0);
		return activityLotteryService.inviteFriendList(memberId, startNo);
	}
	/**
	 * "融"光焕发活动
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @throws Exception
	 * @time:2016年6月16日 下午17:24:03
	 */
	@RequestMapping("/celebrationA/index")
	public ModelAndView celebrateInit(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/celebrationA");
		return model;
	}
	/**
	 * "融"光焕发活动
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年6月16日 下午17:24:03
	 */
	@RequestMapping(value ="/celebrationA/init", method = RequestMethod.POST)
	@ResponseBody
	public Object celebrationActivity(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.initCelebration(Optional.fromNullable(getMember()));
	}
	/**
	 * "融"光焕发活动领取红包
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/celebrationA/receive", method = RequestMethod.POST)
	@ResponseBody
	public Object celebrationAReceive(HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<ActivityForAnniversary> rDO = new ResultDO<ActivityForAnniversary>();
		if(getMember()==null){
			rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			return rDO;
		}
		rDO= activityLotteryService.receiveCelebrationA(getMember().getId());
		return rDO;
	}


	/**
	 * A轮融资专题页
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/Again")
	public ModelAndView Again(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/Again");
		return model;
	}

	/**
	 * A轮融资专题回顾
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/againReview")
	public ModelAndView againReview(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("activity/againReview");
		return model;
	}
	/**
	 * 
	 * @Description:玩转奥运
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 上午11:13:04
	 */
	@RequestMapping("/olympic/index")
	public ModelAndView playOlympicIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/playOlympic");
		return model;
	}
	/**
	 * 
	 * @Description:玩转奥运初始化
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年7月11日 下午2:23:00
	 */
	@RequestMapping(value ="/olympic/init", method = RequestMethod.POST)
	@ResponseBody
	public Object playOlympicInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.olympicInit(Optional.fromNullable(getMember()));
	}
	/**
	 * 
	 * @Description:猜奖牌
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 上午9:10:48
	 */
	@RequestMapping(value = "/olympic/guessMedal", method = RequestMethod.POST)
	@ResponseBody
	public Object olympicGuessMedal(HttpServletRequest req, HttpServletResponse resp) {
		int medalType = ServletRequestUtils.getIntParameter(req, "medalType",-1);
		return activityLotteryService.guessMedal(getMember().getId(),medalType);
	}
	/**
	 * 
	 * @Description:猜金牌
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 上午9:11:01
	 */
	@RequestMapping(value = "/olympic/guessGold", method = RequestMethod.POST)
	@ResponseBody
	public Object olympicGuessGold(HttpServletRequest req, HttpServletResponse resp) {
		int popularityValue = ServletRequestUtils.getIntParameter(req, "popularityValue", -1);
		int goldNumber  = ServletRequestUtils.getIntParameter(req, "goldNumber", -1);
		return activityLotteryService.guessGold(getMember().getId(),popularityValue,goldNumber );
	}
	/**
	 * 
	 * @Description:集奥运
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @throws ServletRequestBindingException 
	 * @time:2016年7月12日 下午3:02:30
	 */
	@RequestMapping(value = "/olympic/setOlympic", method = RequestMethod.POST)
	@ResponseBody
	public Object olympicSetOlympic(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		String puzzle= ServletRequestUtils.getStringParameter(req, "puzzle");
		return activityLotteryService.setOlympic(getMember().getId(),puzzle);
	}
	
	/**
	 * 
	 * @Description:九月战队
	 * @param req
	 * @param resp
	 * @return
	 * @author:
	 * @time:2016年9月9日
	 */
	@RequestMapping("/SeptemberTeam/index")
	public ModelAndView septemberTeamIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/SeptemberTeam");
		return model;
	}
	/**
	 * 
	 * @Description:七月战队初始化
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年6月29日 下午5:07:21
	 */
	@RequestMapping(value ="/julyTeam/init", method = RequestMethod.POST)
	@ResponseBody
	public Object julyTeamInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.julyTeamInit(Optional.fromNullable(getMember()));
	}
	/**
	 * 
	 * @Description:加入战队
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年6月30日 下午4:16:20
	 */
	@RequestMapping(value = "/julyTeam/joinTeam", method = RequestMethod.POST)
	@ResponseBody
	public Object joinTeam(HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<ActivityForJulyRetrun> rDO = new ResultDO<ActivityForJulyRetrun>();
		if(getMember()==null){
			rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			return rDO;
		}
		rDO= activityLotteryService.julyTeamJoin(getMember().getId());
		return rDO;
	}
	/**
	 * 
	 * @Description:七月战队-领取现金券
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年7月1日 下午2:23:18
	 */
	@RequestMapping(value = "/julyTeam/receiveCoupon", method = RequestMethod.POST)
	@ResponseBody
	public Object receiveJulyTeamCoupon(HttpServletRequest req, HttpServletResponse resp) {
		int couponAmount = ServletRequestUtils.getIntParameter(req, "couponAmount", 0);
		ResultDO<ActivityForJulyRetrun> rDO = new ResultDO<ActivityForJulyRetrun>();
		if(getMember()==null){
			rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			return rDO;
		}
		rDO= activityLotteryService.receiveJulyTeamCoupon(getMember().getId(),couponAmount);
		return rDO;
	}
	/**
	 * 
	 * @Description:押注
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年6月30日 下午6:03:44
	 */
	@RequestMapping(value = "/julyTeam/betTeam", method = RequestMethod.POST)
	@ResponseBody
	public Object betJulyTeam(HttpServletRequest req, HttpServletResponse resp) {
		int popularityValue = ServletRequestUtils.getIntParameter(req, "popularityValue", 0);
		int groupType = ServletRequestUtils.getIntParameter(req, "groupType", 0);
		ResultDO<Object> rDO = new ResultDO<Object>();
		if(getMember()==null){
			rDO.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			return rDO;
		}
		return activityLotteryService.betJulyTeam(getMember().getId(),popularityValue,groupType);
	}

	/**
	 * 
	 * @Description:平台数据频道
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年8月26日 下午4:42:47
	 */
	@RequestMapping(value ="/dataChannel/init", method = RequestMethod.POST)
	@ResponseBody
	public Object dataChannelInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.dataChannelInit();
	}
	/**
	 * 
	 * @Description:获取实时数据
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年9月22日 下午1:55:21
	 */
	@RequestMapping(value ="/dataChannel/realTimeData", method = RequestMethod.POST)
	@ResponseBody
	public Object realTimeData(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.realTimeData();
	}

	
	
	@RequestMapping(value ="/celebrate/realTimeData1", method = RequestMethod.POST)
	@ResponseBody
	public Object celebrate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Transaction  transaction=new Transaction();
		transaction=myTransactionManager.selectTransactionById(888800000014L);
		activityAfterTransactionManager.thirtyGift(transaction);
		return null;
	}
	
	/**
	 * 
	 * @Description:直投抽奖
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年10月31日 下午1:47:24
	 */
	 @RequestMapping(value = "/lottery", method = RequestMethod.POST)
	 @ResponseBody
	 public ResultDO<Object> directProjectLottery(HttpServletRequest req, HttpServletResponse resp) throws ManagerException {
		 //activityAfterTransactionManager.directProjectLottery(transaction);
	    Long memberId = getMember().getId();
	    Long projectId = ServletRequestUtils.getLongParameter(req, "projectId",0L);
	    Integer type = ServletRequestUtils.getIntParameter(req, "type",0);
	   return projectService.directProjectLottery(projectId,memberId,type);
	 }

	/**
	 *
	 * @Description:周年庆
	 * @param req
	 * @param resp
	 * @return
	 * @author:
	 * @time:2016年10月11日
	 */
	@RequestMapping("/twoYearAnniversary/index")
	public ModelAndView twoYearAnniversaryIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/twoYearAnniversary");
		return model;
	}
	
	/**
	 * 
	 * @Description:周年庆活动
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年10月17日 下午1:27:33
	 */
	@RequestMapping(value ="/anniversary/init", method = RequestMethod.POST)
	@ResponseBody
	public Object anniversaryInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.anniversaryInit(Optional.fromNullable(getMember()));
	}
	/**
	 * 
	 * @Description:领取红包
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年10月17日 下午2:09:13
	 */
	@RequestMapping(value = "/anniversary/receive", method = RequestMethod.POST)
	@ResponseBody
	public Object receiveCouponAnniversary(HttpServletRequest req, HttpServletResponse resp) {
		int type = ServletRequestUtils.getIntParameter(req, "type", 0);
		int couponAmount  = ServletRequestUtils.getIntParameter(req, "couponAmount",-1);
		return activityLotteryService.receiveCouponAnniversary(getMember().getId(),type,couponAmount );
	}
	/**
	 * 
	 * @Description:消耗人气值领取奖品
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年10月19日 下午9:17:16
	 */
	@RequestMapping(value = "/anniversary/receiveReward", method = RequestMethod.POST)
	@ResponseBody
	public Object receiveRewardAnniversary(HttpServletRequest req, HttpServletResponse resp) {
		int chip = ServletRequestUtils.getIntParameter(req, "chip", 0);
		int popularValue  = ServletRequestUtils.getIntParameter(req, "popularValue",-1);
		return activityLotteryService.receiveRewardAnniversary(getMember().getId(),chip,popularValue);
	}
	/**
	 * 
	 * @Description:十月活动初始化
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年10月9日 下午1:10:22
	 */
	@RequestMapping(value ="/october/init", method = RequestMethod.POST)
	@ResponseBody
	public Object octoberInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.octoberInit(Optional.fromNullable(getMember()));
	}
	/**
	 *
	 * @Description:十月活动
	 * @param req
	 * @param resp
	 * @return
	 * @author:
	 * @time:2016年10月8日
	 */
	@RequestMapping("/October/index")
	public ModelAndView OctoberIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/October");
		return model;
	}
	/**
	 * 
	 * @Description:双旦活动
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年12月6日 上午10:17:32
	 */
	@RequestMapping(value ="/double/init", method = RequestMethod.POST)
	@ResponseBody
	public Object doubleInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.doubleInit(Optional.fromNullable(getMember()));
	}
	/**
	 * 
	 * @Description:双旦抢红包
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年12月6日 上午10:35:18
	 */
	@RequestMapping(value = "/double/receive", method = RequestMethod.POST)
	@ResponseBody
	public Object doubleReceiveCoupon(HttpServletRequest req, HttpServletResponse resp) {
		return activityLotteryService.doubleReceiveCoupon(getMember().getId());
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @Description:双旦活动
	 * @author:
	 * @time:2016年10月8日
	 */
	@RequestMapping("/December/index")
	public ModelAndView DecemberIndex(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/activity/December");
		return model;
	}
	/**
	 * 国资战略入股专题回顾
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/joinPart")
	public ModelAndView joinPart(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("activity/joinPart");
		return model;
	}

	/**
	 * 新年活动
	 * @param model
	 * @return
     */
	@RequestMapping(value = "/newyear/index")
	public String newYearIndex(Model model){
		return "/activity/newYear";
	}

	/**
	 * 新年活动数据
	 * @param req
	 * @param resp
     * @return
     */
	@ResponseBody
	@RequestMapping(value = "/newyear/init")
	public Object newYearInit(Model model, HttpServletRequest req, HttpServletResponse resp){
		ActivityForNewYearDto activityForNewYearDto =new ActivityForNewYearDto();
		Optional<Activity> newyear = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_NEWYEAR);
		if (!newyear.isPresent()) {
			return activityForNewYearDto;
		}

		Activity newyearActivity = newyear.get();

		ActivityForNewYear activityForNewYear = LotteryContainer.getInstance().getObtainConditions(newyearActivity,
				ActivityForNewYear.class, ActivityConstant.ACTIVITY_NEWYEAR_KEY);
		MemberSessionDto member= getMember();
		Long memberId=null;
		if (member!=null){
			memberId=member.getId();
		}
		if (memberId!=null){
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
			int transactionCount= transactionService.queryMemberTransactionCount(member.getId(),DateUtils.getDateFromString("2016-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"),newyearActivity.getEndTime());

			activityForNewYearDto.setReferralCount(referralCount);
			activityForNewYearDto.setReferralTransactionCount(referralTransactionCount);
			activityForNewYearDto.setTransactionCount(transactionCount);
			activityForNewYearDto.setStartTime(newyearActivity.getStartTime());
			activityForNewYearDto.setEndTime(newyearActivity.getEndTime());
			activityForNewYearDto.setStatus(newyearActivity.getActivityStatus());
			activityForNewYearDto.setRegisterTime(member.getRegisterTime());
			activityForNewYearDto.setLuckyMoneyTemplateIds(activityForNewYear.getLuckyMoneyTemplateIds());
			return activityForNewYearDto;
		}
		activityForNewYearDto.setStartTime(newyearActivity.getStartTime());
		activityForNewYearDto.setEndTime(newyearActivity.getEndTime());
		activityForNewYearDto.setStatus(newyearActivity.getActivityStatus());
		activityForNewYearDto.setRegisterTime(null);
		activityForNewYearDto.setLuckyMoneyTemplateIds(activityForNewYear.getLuckyMoneyTemplateIds());
		return activityForNewYearDto;
	}

	/**
	 * 喜领压岁钱
	 * @return
     */
	@ResponseBody
	@RequestMapping(value = "/newyear/luckymoney")
	public Object luckyMoney(@RequestParam("templateid")Long templateId){
		MemberSessionDto member= getMember();
		if (member!=null){
			return activityLotteryService.newYearLuckyMoney(getMember().getId(),templateId);
		}
		return activityLotteryService.newYearLuckyMoney(null,templateId);
	}

	/**
	 * 元宵活动
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/lanternFestival/index")
	public String lanternFestival(Model model){
		ResultDO<Activity> resultDO= activityLotteryService.lanternFestival();
		model.addAttribute("activity",resultDO.getResult());
		return "/activity/lanternFestival";
	}

	/**
	 * 元宵活动
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/womensDay/index")
	public String womensDay(Model model){
		return "/activity/womensDay";
	}

	@ResponseBody
	@RequestMapping(value = "/womensDay/init")
	public ResultDO<ActivityForWomensDay> womensDayInit(){
		MemberSessionDto member= getMember();
		if (member!=null){
			return activityLotteryService.womensDayData(getMember().getId());
		}
		return activityLotteryService.womensDayData(null);
	}

	/**
	 * 38节领取礼包
	 * @return
     */
	@ResponseBody
	@RequestMapping(value = "/womensDay/bag")
	public ResultDO<ActivityForWomensDay> womensDayBag(){
		MemberSessionDto member= getMember();
		if (member!=null){
			return activityLotteryService.womensDayBag(getMember().getId());
		}
		return activityLotteryService.womensDayBag(null);
	}

	
	/**
	 * 
	 * @Description:领取福袋
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2017年2月6日 下午2:09:13
	 */
	
	@RequestMapping(value = "/fiveBillion/receive", method = RequestMethod.POST)
	@ResponseBody
	public Object receiveLuckBag(HttpServletRequest req, HttpServletResponse resp) {
		int couponAmount  = ServletRequestUtils.getIntParameter(req, "couponAmount",-1);
		return activityLotteryService.receiveLuckBag(getMember().getId(),couponAmount);
	}
	
	/**
	 * 50亿活动
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/fiveBillion/index")
	public String fiveBillionIndex(Model model){
		return "/activity/fiveBillion";
	}
	
	/**
	 * 50亿活动初始化
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/fiveBillion/init", method = RequestMethod.POST)
	@ResponseBody
	public Object fiveBillionInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.fiveBillionInit(Optional.fromNullable(getMember()));
	}
	
	/**
	 * 50亿  抽奖
	 * @param req type 1,福禄双全  2，福禄天齐
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/fiveBillion/lotteryLuck", method = RequestMethod.POST)
	@ResponseBody
	public Object lotteryLuckBoth(HttpServletRequest req, HttpServletResponse resp) {
		int type  = ServletRequestUtils.getIntParameter(req, "type",-1);
		return activityLotteryService.lotteryLuckBoth(getMember().getId(),type);
	}
	
	/**
	 * 我的中奖记录
	 * @param req
	 * @param resp
	 * @param transactionQuery
	 * @return
	 */
	 @RequestMapping(value = "fiveBillion/myLotteryRecord", method = RequestMethod.POST)
	 @ResponseBody
	 public ResultDO<Object> myLotteryRecord(HttpServletRequest req, HttpServletResponse resp, ActivityLotteryResultQuery query) {
		 ResultDO<Object> result=new ResultDO<Object>(); 
	     query.setMemberId(getMember().getId());
	     int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
	     query.setCurrentPage(pageNo);
	     if(query.getType()==1){
	    	 query.setRemark("luckBoth");
	     }else if(query.getType()==2){
	    	 query.setRemark("luckMonkey");
	     }
	     Page<ActivityForFiveBillionRetrun> pager = activityLotteryService.myLotteryRecord(query);
	     result.setResult(pager);
	     return result;
	 }

    /**
     * 50亿活动
     * @param model
     * @return
     */
    @RequestMapping(value = "/dayDropGold/index")
    public String dayDropGold(Model model){
        return "/activity/dayDropGold";
    }

	 /**
	  * 天降金喜初始化
	  * @param req
	  * @param resp
	  * @return
	  * @throws Exception
	  */
	 @RequestMapping(value ="/dayDropGold/init", method = RequestMethod.POST)
	 @ResponseBody
	 public Object dayDropInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.dayDropInit(Optional.fromNullable(getMember()));
	 }

	 /**
	  * 领取现金券
	  * @param req
	  * @param resp
	  * @return
	  */
	 @RequestMapping(value = "/dayDropGold/receive", method = RequestMethod.POST)
	 @ResponseBody
	 public Object receiveCouponGold(HttpServletRequest req, HttpServletResponse resp) {
		int couponAmount  = ServletRequestUtils.getIntParameter(req, "couponAmount",-1);
		return activityLotteryService.receiveCouponGold(getMember().getId(),couponAmount);
	 }


	/**
	 * 邀请活动
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/inviteFriend/index")
	public String inviteFriend(Model model){
		return "/activity/inviteFriend";
	}

	@ResponseBody
	@RequestMapping(value = "/inviteFriend/init")
	public ResultDO<ActivityForInviteFriend> inviteFriendInit(){
		MemberSessionDto member= getMember();
		if (member!=null){
			return activityLotteryService.inviteFriendData(getMember().getId());
		}else {
			return activityLotteryService.inviteFriendData(null);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/inviteFriend/detail")
	public ResultDO<Page<ActivityForInviteFriendDetail>> inviteFriendDetail(HttpServletRequest req){
		MemberSessionDto member= getMember();
		Integer pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		ResultDO<Page<ActivityForInviteFriendDetail>> resultDO=new ResultDO<>();
		Page<ActivityForInviteFriendDetail> page=new Page<>();
		if (member!=null){
			page = activityLotteryService.inviteFriendDetail(getMember().getId(),pageNo);
		}else {
			page = activityLotteryService.inviteFriendDetail(null,pageNo);
		}
		resultDO.setResult(page);
		return resultDO;
	}
	/**
	 * 好春来活动
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/goodSpring/index")
	public String goodSpring(Model model){
		return "/activity/goodSpring";
	}
	
	 /**
	  * 好春来活动初始化
	  * @param req
	  * @param resp
	  * @return
	  * @throws Exception
	  */
	 @RequestMapping(value ="/springComing/init", method = RequestMethod.POST)
	 @ResponseBody
	 public ResultDO<Object> springComingInit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return activityLotteryService.springComingInit(Optional.fromNullable(getMember()));
	 }
	
	 /**
	  * 好春来活动领券
	  * @param req
	  * @param resp
	  * @return
	  */
	 @RequestMapping(value = "/springComing/receive", method = RequestMethod.POST)
	 @ResponseBody
	 public Object springComingActivity(HttpServletRequest req, HttpServletResponse resp) {
		Long templateId  = ServletRequestUtils.getLongParameter(req, "templateId",-1);
		Long memberId = getMember().getId();
		return activityLotteryService.springComingActivity(memberId,templateId);
	 }

	/**
	 * 51活动
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/labor/index")
	public String labor(Model model){
		return "/activity/labor";
	}

	/**
	 * 51活动数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/labor/init")
	public ResultDO<ActivityForLabor> laborInit(){
		MemberSessionDto member= getMember();
		if (member!=null){
			return activityLotteryService.laborInit(getMember().getId());
		}else {
			return activityLotteryService.laborInit(null);
		}
	}

	/**
	 * 51活动领取礼包
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/labor/receiveLabor")
	public ResultDO<Activity> receiveLabor(){
		MemberSessionDto member= getMember();
		if (member!=null){
			return activityLotteryService.receiveLabor(getMember().getId());
		}else {
			return activityLotteryService.receiveLabor(null);
		}
	}

}
