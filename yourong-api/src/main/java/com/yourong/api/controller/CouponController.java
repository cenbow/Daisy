package com.yourong.api.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.yourong.common.enums.CouponTemplateRelationEnum;
import com.yourong.core.fin.model.biz.OverduePopularityBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yourong.api.dto.CouponDto;
import com.yourong.api.dto.MemberReferralDto;
import com.yourong.api.dto.PopularityInOutLogDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.BalanceService;
import com.yourong.api.service.CouponService;
import com.yourong.api.service.MemberService;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.mc.model.query.CouponQuery;

@Controller
@RequestMapping("security/coupon")
public class CouponController extends  BaseController{
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private BalanceService balanceService;

	/**
	 * 查询收益/优惠券列表
	 * @param query
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryCouponList", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO queryCouponList(@Valid CouponQuery query, BindingResult result, HttpServletRequest req, HttpServletResponse resp){
		Page<CouponDto> page = new Page<CouponDto>();
		ResultDTO resultDTO = new ResultDTO();
		validateResult(resultDTO, result);
		if(!resultDTO.isSuccess()){
			resultDTO.setResultCode(ResultCode.ERROR);
			return resultDTO;
		}
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		int status = ServletRequestUtils.getIntParameter(req, "status",1);
		query.setCurrentPage(pageNo);
		query.setMemberId(getMemberID(req));
		query.setPageSize(0);
		query.setStatus(status);
		page = couponService.queryCoupons(query);
		resultDTO.setResult(page);
		return resultDTO;
	}
	
	/**
	 * 查询收益/优惠券列表
	 * @param query
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryCouponList", method = RequestMethod.POST, headers = {"Accept-Version=2.0.0"})
	@ResponseBody
	public ResultDTO queryCouponListAndHistory(@Valid CouponQuery query, BindingResult result, HttpServletRequest req, HttpServletResponse resp){
		ResultDTO resultDTO = new ResultDTO();
		validateResult(resultDTO, result);
		if(!resultDTO.isSuccess()){
			resultDTO.setResultCode(ResultCode.ERROR);
			return resultDTO;
		}
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		int status = ServletRequestUtils.getIntParameter(req, "status",1);
		query.setCurrentPage(pageNo);
		query.setMemberId(getMemberID(req));
		query.setPageSize(0);
		if(status==2){                                                                                                                                                                                                                                                                                                                                                                                                                            
			query.setPageSize(10);//历史优惠券，分页
		}
		query.setWay(2);
		query.setStatus(status);
		Map<String,Object> map = couponService.queryCouponsHistory(query);
		resultDTO.setResult(map);
		return resultDTO;
	}
	
	
	/**
	 * 获得用户推荐列表
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberReferraList", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO queryMemberReferraList(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO result = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		BaseQueryParam query = new BaseQueryParam();
		query.setMemberId(getMemberID(req));
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		Page<MemberReferralDto> page = memberService.queryMemberReferralList(query);
		result.setResult(page);
		return result;
	}
	
	/**
	 * 获得用户推荐列表
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberReferralList", method = RequestMethod.POST, headers = {"Accept-Version=1.0.2"})
	@ResponseBody
	public ResultDTO queryMemberReferralList(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO result = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		BaseQueryParam query = new BaseQueryParam();
		query.setMemberId(getMemberID(req));
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		Page<MemberReferralDto> page = memberService.queryMemberReferralList(query);
		result.setResult(page);
		return result;
	}
	
	
	/**
	 * 获取用户人气值列表
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberPopularityLogList", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO queryMemberPopularityLogList(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO result = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		int type = ServletRequestUtils.getIntParameter(req, "type",0);
		BaseQueryParam query = new BaseQueryParam();
		query.setMemberId(getMemberID(req));
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		query.setType(type);
		Page<PopularityInOutLogDto> log = memberService.queryMemberPopularityLogList(query);
		result.setResult(log);
		return result;
	}
	
	/**
	 * 获取用户人气值列表
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberPopularityLogList", method = RequestMethod.POST, headers = {"Accept-Version=1.0.2"})
	@ResponseBody
	public ResultDTO queryMemberPopularityLogList2(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO result = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		int type = ServletRequestUtils.getIntParameter(req, "type",0);
		BaseQueryParam query = new BaseQueryParam();
		query.setMemberId(getMemberID(req));
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		query.setType(type);
		Page<PopularityInOutLogDto> log = memberService.queryMemberPopularityLogList(query);
		result.setResult(log);
		return result;
	}
	
	/**
	 * 获取用户人气值
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberPopularity", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO queryMemberPopularity(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO<BigDecimal> popularity = new ResultDTO<BigDecimal>();
		Long memberId = getMemberID(req);
		Balance balance = balanceService.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		if(balance != null){
			popularity.setResult(balance.getAvailableBalance());
		}else{
			popularity.setResult(BigDecimal.ZERO);
		}
		return popularity;
	}

	/**
	 * 查询用户过期人气值
	 * @param req
	 * @param resp
     * @return
     */
	@RequestMapping(value = "queryMemberOverduePopularity", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO queryMemberOverduePopularity(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO<OverduePopularityBiz> popularity = new ResultDTO<OverduePopularityBiz>();
		Long memberId = getMemberID(req);
		OverduePopularityBiz overduePopularity = balanceService.queryOverduePopularity(memberId);
		popularity.setResult(overduePopularity);
		return popularity;
	}
	
	
	/**
	 * 兑换优惠券
	 * 
	 * @param req
	 * @param resps
	 * @return
	 */
	@RequestMapping(value = "exchange", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO exchangeCoupon(HttpServletRequest req,
			HttpServletResponse resps) {
		Long memberId = getMemberID(req);
		int num = ServletRequestUtils.getIntParameter(req, "num",1);
		Integer source = getRequestSource(req);
		int value = ServletRequestUtils.getIntParameter(req, "value",0);
		Long couponTemplateId = 0L;
		switch (value) {
			case 10:
				couponTemplateId = Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate10"));
				break;
			case 50:
				couponTemplateId = Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate50"));
				break;
			case 100:
				couponTemplateId = Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate100"));
				break;
			case 200:
				couponTemplateId = Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate200"));
				break;
			case 500:
				couponTemplateId = Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate500"));
				break;
			case 1000:
				couponTemplateId = Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate1000"));
				break;
			default:
				break;
		}
		ResultDTO result = new ResultDTO();
		if(couponTemplateId > 0){
			try {
				result = couponService.exchangeCoupon(memberId, couponTemplateId, num,source);
			} catch (Exception e) {
				logger.error("兑换优惠券发生异常", e);
				result.setResultCode(ResultCode.ERROR);
			}
		}else{
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}
	
	/**
	 * 兑换优惠券
	 * 
	 * @param req
	 * @param resps
	 * @return
	 */
	@RequestMapping(value = "exchange", method = RequestMethod.POST, headers = {"Accept-Version=1.0.1"})
	@ResponseBody
	public ResultDTO exchangeCouponV1(HttpServletRequest req,
			HttpServletResponse resps) {
		Long memberId = getMemberID(req);
		Integer source = getRequestSource(req);
		if(source==1){
			source=2;//针对ios（2）和安卓（1），统一为2app兑换
		}
		
		int num = ServletRequestUtils.getIntParameter(req, "num",1);
		Long couponTemplateId = ServletRequestUtils.getLongParameter(req, "value",0);
		Long[] templateIds = null;
		templateIds=couponService.getTemplateidsArray(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_CASH.getCode());
		ResultDTO result = new ResultDTO();
		if(!Arrays.asList(templateIds).contains(couponTemplateId)){
			result.setResultCode(ResultCode.COUPON_NOT_USE_FOR_EXCHANGE_POPULARITY_ERROR);
			return result;
		}
		if(couponTemplateId > 0){
			try {
				result = couponService.exchangeCoupon(memberId, couponTemplateId, num,source);
			} catch (Exception e) {
				logger.error("兑换优惠券发生异常", e);
				result.setResultCode(ResultCode.ERROR);
			}
		}else{
			result.setResultCode(ResultCode.ERROR);
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
	@RequestMapping(value = "exchangeList", method = RequestMethod.POST, headers = {"Accept-Version=1.0.1"})
	@ResponseBody
	public ResultDTO getExchangeCouponTemplates(HttpServletRequest req, HttpServletResponse resps) {
		Long[] templateIds = null;
		templateIds=couponService.getTemplateidsArray(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_CASH.getCode());
		ResultDTO result = new ResultDTO();
		result.setResult(couponService.findExchangeCouponsByIds(templateIds));
		return result;
	}
	
	
	/**
	 * 获取用于兑换的优惠券列表
	 * 
	 * @param req
	 * @param resps
	 * @return
	 */
	@RequestMapping(value = "exchangeList", method = RequestMethod.POST, headers = {"Accept-Version=1.0.5"})
	@ResponseBody
	public ResultDTO getExchangeCouponTemplates5(HttpServletRequest req, HttpServletResponse resps) {
		Long[] templateIds = null;
		int type = ServletRequestUtils.getIntParameter(req, "type",1);
		if(type == 1){
			templateIds=couponService.getTemplateidsArray(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_CASH.getCode());
		}else{
			templateIds=couponService.getTemplateidsArray(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_BENEFIT.getCode());
		}
		ResultDTO result = new ResultDTO();
		result.setResult(couponService.findExchangeCouponsByIds(templateIds));
		return result;
	}
	
	@RequestMapping(value = "queryMemberPopularity", method = RequestMethod.POST, headers = {"Accept-Version=1.0.5"})
	@ResponseBody
	public ResultDTO queryMemberPopularity5(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO popularity = new ResultDTO();
		Long memberId = getMemberID(req);
		Balance balance = balanceService.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		JSONObject jo = new JSONObject();
		if(balance != null){
			jo.put("availableBalance", balance.getBalance());
			jo.put("freezeBalance", balance.getBalance().subtract(balance.getAvailableBalance()));
			popularity.setResult(jo);
		}else{
			jo.put("availableBalance", BigDecimal.ZERO);
			jo.put("freezeBalance", BigDecimal.ZERO);
			popularity.setResult(jo);
		}
		return popularity;
	}
	
	/**
	 * 兑换现金券
	 * 
	 * @param req
	 * @param resps
	 * @return
	 */
	@RequestMapping(value = "exchangeCoupons", method = RequestMethod.POST, headers = {"Accept-Version=1.0.5"})
	@ResponseBody
	public ResultDTO exchangeCouponV5(HttpServletRequest req,
			HttpServletResponse resps) {
		Long memberId = getMemberID(req);
		Integer source = getRequestSource(req);
		if(source==1){
			source=2;//针对ios（2）和安卓（1），统一为2app兑换
		}
		Long couponTemplateId = ServletRequestUtils.getLongParameter(req, "value",0);
		Long[] templateIds = null;
		templateIds=couponService.getTemplateidsArray(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_BENEFIT.getCode());
		ResultDTO result = new ResultDTO();
		if(!Arrays.asList(templateIds).contains(couponTemplateId)){
			result.setResultCode(ResultCode.COUPON_NOT_USE_FOR_EXCHANGE_POPULARITY_ERROR);
			return result;
		}
		if(couponTemplateId > 0){
			try {
				result = couponService.exchangeCoupon(memberId, couponTemplateId, 1,source);
			} catch (Exception e) {
				logger.error("兑换优惠券发生异常", e);
				result.setResultCode(ResultCode.ERROR);
			}
		}else{
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}
	
	/**
	 * 获取用户人气值列表
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberPopularityLogList", method = RequestMethod.POST, headers = {"Accept-Version=1.0.5"})
	@ResponseBody
	public ResultDTO queryMemberPopularityLogList5(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO result = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		BaseQueryParam query = new BaseQueryParam();
		query.setMemberId(getMemberID(req));
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		Page<PopularityInOutLogDto> log = memberService.getPopularityInOutLog(query);
		result.setResult(log);
		return result;
	}
}
