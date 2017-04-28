package com.yourong.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.api.dto.MemberCheckInfoDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.MemberBehaviorLogService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.OrderDeliveryService;
import com.yourong.api.service.PopularityParkService;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.enums.MemberLogEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.core.sh.model.OrderDelivery;

@Controller
@RequestMapping("yrwHtml")
public class HtmlController extends BaseController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberBehaviorLogService memberBehaviorLogService;
	
	@Autowired
	private PopularityParkService popularityParkService;
	
	@Autowired
	private OrderDeliveryService orderDeliveryService;
	
	/**
	 * 用户行为记录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "memberBehavior", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public void memberBehavior(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		if(memberId==null){
			return;
		}
		String sourceId = ServletRequestUtils.getStringParameter(req,
				"sourceId", "");
		String device = ServletRequestUtils.getStringParameter(req, "device",
				"");
		String deviceParam = ServletRequestUtils.getStringParameter(req,
				"deviceParam", "");
		String anchor = ServletRequestUtils.getStringParameter(req, "anchor",
				"");
		String remarks = ServletRequestUtils.getStringParameter(req, "remarks",
				"");
		memberBehaviorLogService.memberBehaviorLogInsert(memberId, sourceId,
				MemberLogEnum.MEMBER_BEHAVIOR_TYPE_APPH5_OPERATION.getType(),
				MemberLogEnum.MEMBER_BEHAVIOR_APPH5.getType(), device,
				deviceParam, anchor, remarks);
	}

	/**
	 * 获得用户签到信息
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberSignInInfo", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO<MemberCheckInfoDto> queryMemberSignInInfo(
			HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return memberService.queryMemberSignInInfo(memberId);
	}
	
	
	//会员中心
	@RequestMapping("/memberCenter")
	@ResponseBody
	public ModelAndView memberCenter(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		DynamicParamBuilder builder = new DynamicParamBuilder();
		model.setViewName("/popularity/memberCenter");
		return model;
	}
	
	//会员中心的成长值
	@RequestMapping("/growthSystem")
	@ResponseBody
	public ModelAndView growthSystem(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		DynamicParamBuilder builder = new DynamicParamBuilder();
		model.setViewName("/popularity/growthSystem");
		return model;
	}
	
	//投资专享的全部商品
	@RequestMapping("/investProductList")
	@ResponseBody
	public ModelAndView investProductList(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		DynamicParamBuilder builder = new DynamicParamBuilder();
		model.setViewName("/popularity/investProductList");
		return model;
	}
	
	//实物的全部商品
	@RequestMapping("/entityProductList")
	@ResponseBody
	public ModelAndView entityProductList(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		DynamicParamBuilder builder = new DynamicParamBuilder();
		model.setViewName("/popularity/entityProductList");
		return model;
	}
	
	//虚拟卡券的全部商品
	@RequestMapping("/fictitiousProductList")
	@ResponseBody
	public ModelAndView fictitiousProductList(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		DynamicParamBuilder builder = new DynamicParamBuilder();
		model.setViewName("/popularity/fictitiousProductList");
		return model;
	}
	
	//商品详情
	@RequestMapping("/productDetail")
	@ResponseBody
	public ModelAndView productDetail(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		DynamicParamBuilder builder = new DynamicParamBuilder();
		model.setViewName("/popularity/productDetail");
		return model;
	}

	//兑换记录
	@RequestMapping("/exchangeRecord")
	@ResponseBody
	public ModelAndView exchangeRecord(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		DynamicParamBuilder builder = new DynamicParamBuilder();
		model.setViewName("/popularity/exchangeRecord");
		return model;
	}
	
	//订单详情记录
	@RequestMapping("/exchangeOrderInfo")
	@ResponseBody
	public ModelAndView exchangeOrderInfo(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		DynamicParamBuilder builder = new DynamicParamBuilder();
		model.setViewName("/popularity/exchangeOrderInfo");
		return model;
	}
	
	
	/**
	 * 会员中心
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getMemberCenter", method = RequestMethod.POST, headers = { "Accept-Version=2.0.0" })
	@ResponseBody
	public ResultDTO<Object> getMemberCenter(
			HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return popularityParkService.getMemberCenter(memberId);
	}
	
	/**
	 * 会员成长记录
	 *  
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getGrowthSystem", method = RequestMethod.POST, headers = { "Accept-Version=2.0.0" })
	@ResponseBody
	public ResultDTO<Object> getGrowthSystem(
			HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		
		Integer pageNo = ServletRequestUtils.getIntParameter(req,"pageNo", 1);
		return popularityParkService.getGrowthSystem(memberId,pageNo);
	}
	
	/**
	 * 投资专享的全部商品
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getInvestProductList", method = RequestMethod.POST, headers = { "Accept-Version=2.0.0" })
	@ResponseBody
	public ResultDTO<Object> getInvestProductList(
			HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return popularityParkService.getInvestProductList(memberId);
	}
	
	/**
	 * 根据类型获取全部商品
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getAllProductList", method = RequestMethod.POST, headers = { "Accept-Version=2.0.0" })
	@ResponseBody
	public ResultDTO<Object> getAllProductList(
			HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		Integer goodsType = ServletRequestUtils.getIntParameter(req,"goodsType", 1);
		return popularityParkService.getAllProductList(memberId,goodsType);
	}
	
	
	/**
	 * 商品详情
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getProductDetail", method = RequestMethod.POST, headers = { "Accept-Version=2.0.0" })
	@ResponseBody
	public ResultDTO<Object> getProductDetail(
			HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		Long goodId = ServletRequestUtils.getLongParameter(req,"goodId", 0);
		return popularityParkService.getProductDetail(goodId, memberId);
	}
	
	/**
	 * 兑换记录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getExchangeRecord", method = RequestMethod.POST, headers = { "Accept-Version=2.0.0" })
	@ResponseBody
	public ResultDTO<Object> getExchangeRecord(
			HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		Integer currentPage = ServletRequestUtils.getIntParameter(req,"currentPage", 1);
		return popularityParkService.getExchangeRecord(memberId,currentPage);
	}
	
	/**
	 * 订单详情
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getExchangeOrderInfo", method = RequestMethod.POST, headers = { "Accept-Version=2.0.0" })
	@ResponseBody
	public ResultDTO<Object> getExchangeOrderInfo(
			HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		Long orderMainId = ServletRequestUtils.getLongParameter(req,"orderMainId", 0);
		return popularityParkService.getExchangeOrderInfo(orderMainId, memberId);
	}	

	/**
	 * 获得订单地址回显
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryOrderDelivery", method = RequestMethod.POST,headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO<Object> queryOrderDelivery(HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		try{
			Long memberId = getMemberID(req);
			OrderDelivery orderDeliveryDetail = orderDeliveryService.queryOrderDelivery(memberId);
			orderDeliveryDetail.setMemberId(null);
			resultDTO.setResult(orderDeliveryDetail);
			resultDTO.setIsSuccess();
		}catch(Exception e){
			logger.error("移动端发现页面调用订单信息异常", e);
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return resultDTO;
	}
	/**
	 * 帮助中心用户行为记录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "helpCenterBehavior", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public void helpCenterBehavior(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		String sourceId = ServletRequestUtils.getStringParameter(req,
				"sourceId", "");
	
		String device = ServletRequestUtils.getStringParameter(req, "device",
				"");
		String deviceParam = ServletRequestUtils.getStringParameter(req,
				"deviceParam", "");
		String anchor = ServletRequestUtils.getStringParameter(req, "anchor",
				"");
		String remarks = ServletRequestUtils.getStringParameter(req, "remarks",
				"");
		memberBehaviorLogService.memberBehaviorLogInsert((long)2, sourceId,
				MemberLogEnum.MEMBER_BEHAVIOR_TYPE_APPH5_OPERATION.getType(),
				MemberLogEnum.MEMBER_BEHAVIOR_APPH5.getType(), device,
				deviceParam, anchor, remarks);
	}

}
