/**
 * 
 */
package com.yourong.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.yourong.api.dto.FindDto;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.BannerService;
import com.yourong.api.service.FindeService;
import com.yourong.api.service.MemberService;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.AES;
import com.yourong.common.util.Collections3;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;
import com.yourong.core.ic.model.query.BannerQuery;
import com.yourong.core.sh.model.Area;
import com.yourong.core.sh.model.OrderDelivery;
import com.yourong.core.uc.model.Member;

/**
 * @desc 发现页面
 * @author zhanghao 2016年5月16日上午9:22:40
 */
@Controller
@RequestMapping("security/find")
public class FindController extends BaseController {

	@Autowired
	private FindeService findeService;

	@Autowired
	private BannerService bannerService;

	@Autowired
	private MemberService memberService;

	/**
	 * 发现页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "init", method = RequestMethod.POST, headers = { "Accept-Version=1.4.0" })
	@ResponseBody
	public ResultDTO<FindDto> getFindPage(HttpServletRequest req,
			HttpServletResponse resp) {
		FindDto fingDtoQuery = new FindDto();
		Long memberId = getMemberID(req);
		fingDtoQuery.setMemberId(memberId);
		ResultDTO<FindDto> result = findeService.getFindPage(fingDtoQuery);
		return result;
	}
	
	/**
	 * 发现页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "initUnlogin", method = RequestMethod.POST, headers = { "Accept-Version=1.4.0" })
	@ResponseBody
	public ResultDTO<FindDto> getFindPageUnlogin(HttpServletRequest req,
			HttpServletResponse resp) {
		FindDto fingDtoQuery = new FindDto();
		ResultDTO<FindDto> result = findeService.getFindPageUnlogin(fingDtoQuery);
		return result;
	}

	/**
	 * 活动专题页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "activityRecord", method = RequestMethod.POST, headers = { "Accept-Version=1.6.0" })
	@ResponseBody
	public ResultDTO getActivityRecord(
			@ModelAttribute("bannerQuery") BannerQuery query,
			HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO resultDTO = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		query.setCurrentPage(pageNo);
		Page<BannerFroAreaBiz> bannerList = bannerService
				.findAppActivityBannerByPage(query);
		resultDTO.setResult(bannerList);
		return resultDTO;
	}

	/**
	 * 我的反馈
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	/*
	 * @RequestMapping(value = "feedbackInfo.html") public String
	 * feedbackInfo(HttpServletRequest req, HttpServletResponse resp){
	 * 
	 * return "/article/feedbackInfo"; }
	 */

	@RequestMapping("feedbackInfo.html")
	public ModelAndView feedbackInfo(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		DynamicParamBuilder builder = new DynamicParamBuilder();
		model.setViewName("/article/feedbackInfo");
		return model;
	}

	//方法 0-九宫格抽奖，1-九宫格记录  ,2-获取商品详细信息,3-兑换商品，生成订单,4-购买记录,5-人气值商城订单详情,6-人气值流水,7-会员成长记录,8-获取商品列表,9,10,11-获取快投有奖规则专题页信息,
	//12-直投快投有奖抽奖 ,13-发现页面快投有奖专题页,14-用户行为记录
	private  final String[] invokeMethods={"anniversaryNineGrid","anniversaryNineGridResult","getGoodsDetailInfromations","creatGoodsOrder",
				"purchaseHistory","getShopOrderDetail","queryMemberPopularityLogList","queryMemberVipList",
				"getGoodsList","","","getQuickReward","directLottery","quickRewardRefresh","memberBehavior","queryOrderDelivery"};
		
	/**
	 * 
	 * @Description:人气值页面动态调用
	 * @param req
	 * @param resp
	 * @return
	 * @author: zhanghao
	 * @time:2016年10月24日 上午9:01:44
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "dynamicInvoke", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO<Object> dynamicInvoke(HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDTO<Object> resultDTO = new ResultDTO<>();
		resultDTO.setIsSuccess();
		try {
			Integer invokeMethod = ServletRequestUtils.getIntParameter(req,
					"invokeMethod", 0);
			String method = invokeMethods[invokeMethod];

			if (StringUtil.isBlank(method)) {
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return resultDTO;
			}
			DynamicParamBuilder paramBuilder = null;
			paramBuilder = buildArgs(req);

			logger.debug("移动端发现页面动态调用 method={}, Objects={}", method,
					paramBuilder);
			try {
				resultDTO = (ResultDTO<Object>) MethodUtils.invokeExactMethod(
						findeService, method, paramBuilder);
			} catch (Exception e) {
				logger.error(
						"移动端发现页面动态调用失败 class=findeService paramBuilder={}",
						method, paramBuilder, e);
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			}
		} catch (Exception e) {
			logger.error("移动端发现页面动态调用异常", e);
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return resultDTO;
	}

	/**
	 * 
	 * @Description:APP跳转
	 * @param req
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月5日 上午9:55:15
	 */
	private Optional<Member> encryptionMemberFromApp(HttpServletRequest req,
			ModelAndView model) {
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
			List<String> encryptionCode = AES.getInstance().tokenDecrypt(
					encryptionId);
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
	 * @Description:投资有奖
	 * @param req
	 * @param resp
	 * @return
	 * @author: zhanghao
	 * @time:2016年11月3日 上午9:24:39
	 */
	@RequestMapping("/quickRewardInit")
	public ModelAndView quickRewardInit(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
			model.addObject("mKey", builder.getMemberId());
		}
		//builder.setMemberId(110800001560L);
		Object o = findeService.quickRewardInit(builder.getMemberId());
		model.addObject("data", JSON.toJSON(o));
		model.setViewName("/app/activity/directReward/lottery");
		return model;
	}
	
	//人气值乐园页面
	@RequestMapping("popularity-park")
	@ResponseBody
	public ModelAndView popularityPark(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		DynamicParamBuilder builder = new DynamicParamBuilder();
		model.setViewName("/article/feedbackInfo");
		return model;
	}

	/**
	 * 
	 * @Description:人气值乐园数据
	 * @param req
	 * @param resp
	 * @return
	 * @author: zhanghao
	 * @time:2016年10月20日 上午9:24:39
	 */
	@RequestMapping("/getPopularityParkInfor")
	public ModelAndView inviteFriend(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
			model.addObject("mKey", builder.getMemberId());
		}
		Object o = findeService.getPopularityParkInfor(builder.getMemberId());
		model.addObject("data", JSON.toJSON(o));
		model.addObject("parameter", "popularityPark");
		model.setViewName("/popularity/popularityPark");
		return model;
	}
	
	
	//用户中心
	@RequestMapping("/getPopularityMemberCenter")
	@ResponseBody
	public ModelAndView memberCenter(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
			model.addObject("mKey", builder.getMemberId());
		}
		Object o = findeService.getPopularityParkInfor(builder.getMemberId());
		model.addObject("data", JSON.toJSON(o));
		model.addObject("parameter", "memberCenter");
		model.setViewName("/popularity/popularityPark");
		return model;
	}
	
	//兑换优惠券，人气值商城
	@RequestMapping("/getPopularityMall")
	@ResponseBody
	public ModelAndView popularityMall(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
			model.addObject("mKey", builder.getMemberId());
		}
		Object o = findeService.getPopularityParkInfor(builder.getMemberId());
		model.addObject("data", JSON.toJSON(o));
		model.addObject("parameter", "mall");
		model.setViewName("/popularity/popularityPark");
		return model;
	}
	
	//兑换记录
	@RequestMapping("/getPopularityPurchaseHistory")
	@ResponseBody
	public ModelAndView popularityEarn(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ModelAndView model = new ModelAndView();
		Optional<Member> optOfMember = encryptionMemberFromApp(req, model);
		DynamicParamBuilder builder = new DynamicParamBuilder();
		if (optOfMember.isPresent()) {
			builder.setMemberId(optOfMember.get().getId());
			model.addObject("mKey", builder.getMemberId());
		}
		Object o = findeService.getPopularityParkInfor(builder.getMemberId());
		model.addObject("data", JSON.toJSON(o));
		model.addObject("parameter", "purchaseHistory");
		model.setViewName("/popularity/popularityPark");
		return model;
	}
	
	//返回省市地区
		@RequestMapping("/getPopularityArea")
		@ResponseBody
		public ResultDTO<Object> area(HttpServletRequest req, HttpServletResponse resp) throws Exception {
			ResultDTO<Object> resultDTO = new ResultDTO<>();
			List<Map<String,Object>> areaList = new ArrayList<Map<String,Object>>();
			//省Map
			List<Area> provinces = new ArrayList<Area>();
			try{
				HashMap<String,Object> provsMap = new HashMap<String,Object>();
		    	provinces = findeService.queryAreasByParentCode(1L);
		    	provsMap.put("provs_data", provinces);
		    	areaList.add(provsMap);
	    	}catch(Exception e){
	    		logger.error("省信息查询失败", e);
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
	    	}
	    	
	    	//市 Map
	    	Map<String,Object> cityMap = new HashMap<String,Object>();
	    	LinkedHashMap<String, List<Area>> provsCityMap = new LinkedHashMap<String,List<Area>>();
	    	List<Area> allCitys = new ArrayList<Area>();
	    	try{
		    	for (Area province : provinces) {
		    		List<Area> citys = findeService.queryAreasByParentCode(Long.parseLong(province.getCode()));
		    		provsCityMap.put(province.getCode(), citys);
		    		allCitys.addAll(citys);
				}
		    	cityMap.put("citys_data", provsCityMap);
		    	areaList.add(cityMap);
	    	}catch(Exception e){
	    		logger.error("市信息查询失败", e);
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
	    	}
	    	
	    	//区 Map
	    	Map<String,Object> distMap = new HashMap<String,Object>();
	    	LinkedHashMap<String, List<Area>> cityDistMap = new LinkedHashMap<String,List<Area>>();
	    	try{
		    	for (Area city : allCitys) {
		    		List<Area> dists = findeService.queryAreasByParentCode(Long.parseLong(city.getCode()));
		    		cityDistMap.put(city.getCode(), dists);
				}
		    	distMap.put("dists_data", cityDistMap);
		    	areaList.add(distMap);
	    	}catch(Exception e){
	    		logger.error("区信息查询失败", e);
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
	    	}

	    	resultDTO.setResult(areaList);
	    	
	    	return resultDTO;
	    }

}
