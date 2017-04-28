package com.yourong.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.common.enums.CouponTemplateRelationEnum;
import com.yourong.core.fin.model.biz.OverduePopularityBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.fin.model.biz.PopularityMemberBiz;
import com.yourong.core.fin.model.biz.QuadrupleGiftCount;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.query.CouponQuery;
import com.yourong.core.uc.model.biz.ReferralBiz;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.dto.NoviceTaskDto;
import com.yourong.web.service.CouponService;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.PopularityInOutLogService;
import com.yourong.web.utils.ServletUtil;
import com.yourong.web.utils.WebPropertiesUtil;

/**
 * 我的红包
 * 
 * @author fuyl
 *
 */
@Controller
@RequestMapping("coupon")
public class CouponController extends BaseController {

	@Autowired
	private CouponService couponService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private PopularityInOutLogService popularityInOutLogService;

	/**
	 * 现金券列表页面
	 *
	 * @return
	 */
	@RequestMapping(value = "couponList", method = RequestMethod.GET)
	public String showCouponPage(HttpServletRequest req, HttpServletResponse resps) {
		return "member/coupon/coupon";
	}

	/**
	 * 收益券列表页面
	 *
	 * @return
	 */
	@RequestMapping(value = "profitCouponList", method = RequestMethod.GET)
	public String showProfitCouponPage(HttpServletRequest req, HttpServletResponse resps) {
		return "member/coupon/profitCoupon";
	}

	/**
	 * 邀请好友
	 *
	 * @return
	 */
	@RequestMapping(value = "inviteFriend")
	public ModelAndView inviteFriend() {
		ModelAndView mView = new ModelAndView();
		mView.setViewName("/member/inviteFriend");
		MemberSessionDto user = getMember();
		mView.addObject("inviteCode", user.getShortUrl());
		return mView;
	}

	/**
	 * 列表数据
	 */
	@RequestMapping(value = "couponListData", method = RequestMethod.GET)
	public ModelAndView getCouponDataPage(@ModelAttribute("couponQuery") CouponQuery query, HttpServletRequest req,
			HttpServletResponse resps) {
		ModelAndView mView = new ModelAndView();
		query.setPageSize(6);
		query.setWay(1);
		MemberSessionDto user = ServletUtil.getUserDO();
		Page<Coupon> couponPage = new Page<Coupon>();
		if (user != null) {
			Long memberId = user.getId();
			query.setMemberId(memberId);
			couponPage = couponService.findCouponsByPage(query);
		}
		String uableTotalAmount = "";
		if (StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus() == query.getStatus().intValue()) {
			uableTotalAmount = FormulaUtil.formatCurrency(couponService.findTotalAmountByStatus(query));
		}
		String couponStatusID = req.getParameter("couponStatusID");
		mView.addObject("uableTotalAmount", uableTotalAmount);
		mView.addObject("query", query);
		mView.addObject("couponStatusID", couponStatusID);
		mView.addObject("couponPage", couponPage);
		mView.setViewName("member/coupon/couponList");
		return mView;
	}

	/**
	 * 兑换优惠券
	 * 
	 * @param req
	 * @param resps
	 * @return
	 */
	@RequestMapping(value = "exchange", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<CouponTemplate> exchangeCoupon(HttpServletRequest req, HttpServletResponse resps,
			@ModelAttribute("couponTemplateId") Long couponTemplateId) {
		int num = 1;
		Long memberId = getMember().getId();

		List<Long> templateIds=new ArrayList<>();
		templateIds=couponService.getTemplateids(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_CASH.getCode());
		templateIds.addAll(couponService.getTemplateids(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_BENEFIT.getCode()));
		ResultDO<CouponTemplate> result = new ResultDO<CouponTemplate>();
		if (!templateIds.contains(couponTemplateId)) {
			result.setResultCode(ResultCode.COUPON_NOT_USE_FOR_EXCHANGE_POPULARITY_ERROR);
			return result;
		}
		try {
			result = couponService.exchangeCoupon(memberId, couponTemplateId, num);
		} catch (Exception e) {
			logger.error("兑换优惠券发生异常,couponTemplateId={}", couponTemplateId, e);
		}
		return result;
	}

	/**
	 * 获取用于兑换的优惠券列表
	 * 
	 * @param req
	 * @param resps
	 * @return
	 */
	@RequestMapping(value = "exchangeList")
	@ResponseBody
	public Object getExchangeCouponTemplates(HttpServletRequest req, HttpServletResponse resps) {
		Long[] templateIds=couponService.getTemplateidsArray(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_CASH.getCode());
		List<CouponTemplate> couponTemplates = couponService.findExchangeCouponsByIds(templateIds);
		return couponTemplates;
	}

	/**
	 * 人气值页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "reputation")
	public ModelAndView reputation(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		MemberSessionDto user = getMember();
		model.addObject("inviteCode", user.getShortUrl());
		List<CouponTemplate> cashTemplates=new ArrayList<CouponTemplate>();
		List<CouponTemplate> profitTemplates=new ArrayList<CouponTemplate>();
		Long[] cashtemplateIds=couponService.getTemplateidsArray(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_CASH.getCode());
		cashTemplates = couponService.findExchangeCouponsByIds(cashtemplateIds);

		Long[] profittemplateIds=couponService.getTemplateidsArray(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_BENEFIT.getCode());
		profitTemplates = couponService.findExchangeCouponsByIds(profittemplateIds);

		for (CouponTemplate couponTemplate : profitTemplates) {
			if (couponTemplate.getAmount().compareTo(new BigDecimal(0.5)) == 0) {
				couponTemplate.setExchangeAmount(new BigDecimal(WebPropertiesUtil.getProperties("business.exchangeProfitCouponAmount0.5")));
			}
			if (couponTemplate.getAmount().compareTo(new BigDecimal(1)) == 0) {
				couponTemplate.setExchangeAmount(new BigDecimal(WebPropertiesUtil.getProperties("business.exchangeProfitCouponAmount1")));
			}
			if (couponTemplate.getAmount().compareTo(new BigDecimal(1.5)) == 0) {
				couponTemplate.setExchangeAmount(new BigDecimal(WebPropertiesUtil.getProperties("business.exchangeProfitCouponAmount1.5")));
			}
			if (couponTemplate.getAmount().compareTo(new BigDecimal(2)) == 0) {
				couponTemplate.setExchangeAmount(new BigDecimal(WebPropertiesUtil.getProperties("business.exchangeProfitCouponAmount2")));
			}
		}

		OverduePopularityBiz overduepopularity= popularityInOutLogService.queryOverduePopularity(user.getId());
		model.addObject("overduepopularity",overduepopularity);
		model.addObject("cashTemplates", cashTemplates);
		model.addObject("profitTemplates", profitTemplates);
		model.setViewName("member/coupon/reputation");
		List<NoviceTaskDto> noviceTaskList = memberService.getNoviceTaskList(user.getId());
		boolean isShowNoviceTask = false;
		for (NoviceTaskDto ntd : noviceTaskList) {
			if (!ntd.isJoin()) {
				isShowNoviceTask = true;
				break;
			}
		}
		model.addObject("isShowNoviceTask", isShowNoviceTask);
		model.addObject("noviceTaskList", noviceTaskList);
		return model;
	}

	/**
	 * 获取人气值记录列表
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "popularity/gain/list", method = RequestMethod.GET)
	public ModelAndView getMemberPopularityGainList(HttpServletRequest req, HttpServletResponse resp, BaseQueryParam query) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("member/coupon/reputationGainList");
		ResultDO<PopularityInOutLog> result = new ResultDO<PopularityInOutLog>();
		try {
			query.setMemberId(getMember().getId());
			result.setPage(popularityInOutLogService.getPopularityInOutLog(query));
			mv.addObject("result", result.getPage());
		} catch (Exception e) {
			logger.error("获取用户人气值列表异常，memberId={}", getMember().getId(), e);
		}
		return mv;
	}

	/**
	 * 兑换人气值记录列表
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "popularity/exchange/list", method = RequestMethod.GET)
	public ModelAndView getMemberPopularityExchangeList(HttpServletRequest req, HttpServletResponse resp, BaseQueryParam query) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("member/coupon/reputationExchangeList");
		ResultDO<PopularityInOutLog> result = new ResultDO<PopularityInOutLog>();
		try {
			query.setMemberId(getMember().getId());
			result.setPage(memberService.getMemberPopularityInOutLogList(query));
			mv.addObject("result", result.getPage());
		} catch (Exception e) {
			logger.error("获取用户人气值列表异常，memberId={}", getMember().getId(), e);
		}
		return mv;
	}

	/**
	 * 获取被推荐用户列表
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "referral/list", method = RequestMethod.GET)
	public ModelAndView getReferralBiz(HttpServletRequest req, HttpServletResponse resp, BaseQueryParam query) {
		ResultDO<ReferralBiz> result = new ResultDO<ReferralBiz>();
		ModelAndView mv = new ModelAndView();
		mv.setViewName("member/coupon/reputationInviteList");
		try {
			query.setMemberId(getMember().getId());
			Page<ReferralBiz> referralBizs = memberService.getReferralBiz(query);
			result.setPage(referralBizs);
			mv.addObject("result", result.getPage());
			return mv;
		} catch (Exception e) {
			logger.error("获取推荐用户列表发生异常，memberId={}", getMember().getId(), e);
			result.setResultCode(ResultCode.MEMBER_GET_REFERRAL_ERROR);
			return mv;
		}
	}

	/**
	 * 领取的优惠券信息（小明和小刚的故事）
	 * 
	 * @param req
	 * @param resps
	 * @return
	 */
	@RequestMapping(value = "story/coupons")
	@ResponseBody
	public Object getActivityCoupons(HttpServletRequest req, HttpServletResponse resps) {
		List<Coupon> coupons = couponService.getCouponsByCouponTemplateId(7L);
		return coupons;
	}

	@RequestMapping(value = "popularity/total", method = RequestMethod.POST)
	@ResponseBody
	public Object getPopularityMemberBiz() {
		PopularityMemberBiz biz = popularityInOutLogService.findTotalPopByMemberId(getMember().getId());
		return biz;
	}

	/**
	 * 获取人气值流水
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "popularity/list", method = RequestMethod.POST)
	@ResponseBody
	public Object getMemberPopularityList(HttpServletRequest req, HttpServletResponse resp, BaseQueryParam query) {
		query.setMemberId(getMember().getId());
		Page<PopularityInOutLog> popPage = popularityInOutLogService.getPopularityInOutLog(query);
		return popPage;
	}

	/**
	 * 获取用户获取五重礼的次数
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/quadrupleGift/count", method = RequestMethod.POST)
	@ResponseBody
	public Object getQuadrupleGiftCount() {
		QuadrupleGiftCount quadrupleGiftCount = popularityInOutLogService.getQuadrupleGiftCountByMemberId(getMember().getId());
		return quadrupleGiftCount;
	}
}
