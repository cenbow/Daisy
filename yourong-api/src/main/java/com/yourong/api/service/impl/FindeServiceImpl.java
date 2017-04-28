/**
 * 
 */
package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yourong.core.fin.model.biz.OverduePopularityBiz;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rop.thirdparty.com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.yourong.api.dto.FindDto;
import com.yourong.api.dto.GoodsForAppDto;
import com.yourong.api.dto.Icon;
import com.yourong.api.dto.PopularityInOutLogDto;
import com.yourong.api.dto.PopularityNineDto;
import com.yourong.api.dto.PopularityParkDto;
import com.yourong.api.dto.QuickRewardDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.ShopOrderDetilDto;
import com.yourong.api.service.BalanceService;
import com.yourong.api.service.BannerService;
import com.yourong.api.service.FindeService;
import com.yourong.api.service.MemberBehaviorLogService;
import com.yourong.api.service.MemberService;
import com.yourong.api.utils.ServletUtil;
import com.yourong.api.utils.SysServiceUtils;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.AttachmentEnum;
import com.yourong.common.enums.MemberLogEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.cms.manager.BannerManager;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.DirectLotteryResultList;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.biz.ProjectForLevel;
import com.yourong.core.ic.model.biz.ProjectForLottery;
import com.yourong.core.ic.model.biz.ProjectForLotteryReturn;
import com.yourong.core.ic.model.biz.ProjectForReward;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.draw.DrawByProbability;
import com.yourong.core.lottery.model.RewardsBodyForProbility;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.manager.CouponTemplateRelationManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.biz.ActivityForAnniversary;
import com.yourong.core.mc.model.biz.ActivityLotteryResultBiz;
import com.yourong.core.mc.model.biz.ActivityProject;
import com.yourong.core.msg.manager.MessageLogManager;
import com.yourong.core.sh.manager.GoodsAddrManager;
import com.yourong.core.sh.manager.GoodsManager;
import com.yourong.core.sh.manager.ShopOrderManager;
import com.yourong.core.sh.model.Area;
import com.yourong.core.sh.model.Goods;
import com.yourong.core.sh.model.OrderDelivery;
import com.yourong.core.sh.model.OrderForCreat;
import com.yourong.core.sh.model.OrderMain;
import com.yourong.core.sh.model.OrderSub;
import com.yourong.core.sh.model.query.GoodsQuery;
import com.yourong.core.sh.model.query.ShopOrderForAppQuery;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.uc.manager.MemberCheckManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberVipManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberVip;
/**
 * @desc TODO
 * @author zhanghao 2016年5月16日上午9:45:44
 */
@Service
public class FindeServiceImpl implements FindeService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BalanceService balanceService;

	@Autowired
	private BannerService bannerService;

	@Autowired
	private BannerManager bannerManager;

	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberCheckManager memberCheckManager;

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;

	@Autowired
	private MemberVipManager memberVipManager;

	@Autowired
	private GoodsManager goodsManager;

	@Autowired
	private BscAttachmentManager bscAttachmentManager;

	@Autowired
	private DrawByProbability drawByProbability;

	@Autowired
	private ActivityLotteryManager activityLotteryManager;

	@Autowired
	private ShopOrderManager shopOrderManager;

	@Autowired
	private ProjectExtraManager projectExtraManager;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

	@Autowired
	private ActivityManager activityManager;
	
	@Autowired
	private CouponTemplateRelationManager couponTemplateRelationManager;
	
	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private CouponTemplateManager couponTemplateManager;
	
	@Autowired
	private MessageLogManager messageLogManager;
	
	@Autowired
	private MemberBehaviorLogService memberBehaviorLogService;
	
	@Autowired
	private GoodsAddrManager goodsAddrManager;

	@Override
	public ResultDTO<FindDto> getFindPage(FindDto fingDto) {

		ResultDTO<FindDto> result = new ResultDTO<FindDto>();
		Long memberId = fingDto.getMemberId();
		// 人气值总额
		Balance balance = balanceService.queryBalance(memberId,
				TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		if (balance != null) {
			fingDto.setPopularity(balance.getBalance().intValue());
		} else {
			fingDto.setPopularity(0);
			result.setIsError();
		}
		// 今日人气值总额
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("memberId", memberId);
			Integer num = popularityInOutLogManager
					.getMemberPopCountByDate(map);
			fingDto.setPopularityForCheck(num != null ? num : 0);
		} catch (ManagerException e) {
			logger.error("发现页面获取用户今日人气值总额异常" + memberId, e);
			fingDto.setPopularityForCheck(0);
			result.setIsError();

		}
		// 已邀请好友
		try {
			long recommendNum = memberManager
					.getReferralMemberByIdCount(memberId);
			fingDto.setRecommendNum((int) recommendNum);
		} catch (ManagerException e) {
			logger.error("发现页面获取用户邀请好友数量异常" + memberId, e);
			fingDto.setRecommendNum(0);
			result.setIsError();
		}
		// 是否登录
		//try {
			// 发现-活动banner
			List<BannerFroAreaBiz> activityBannerList = Lists.newArrayList();
			/*BannerQuery bannerQuery = new BannerQuery();
			bannerQuery.setBannerType(TypeEnum.BANNER_CHANNEL_TYPE_APP_FIND
					.getType());
			bannerQuery.setPageSize(4);
			bannerQuery.setStartRow(0);
			Page<BannerFroAreaBiz> bannerPage = bannerManager
					.findAppActivityBannerByPage(bannerQuery);
			activityBannerList = bannerPage.getData();*/
			
			List<Banner> bannerList = bannerService.findAppFindBanner();
			activityBannerList = BeanCopyUtil.mapList(
					bannerList, BannerFroAreaBiz.class);
			
			for (BannerFroAreaBiz ban : activityBannerList) {
				ban.setImage(Config.ossPicUrl + ban.getImage());
			}
			fingDto.setActivityBannerList(activityBannerList);
			
			
	/*	} catch (ManagerException activityE) {
			logger.error("发现页面获取活动banner异常", activityE);
			result.setIsError();
		}*/
			
			try {
				Member member = memberManager.selectByPrimaryKey(memberId);
				int status = this.getBirthdayStatus(member.getBirthday());
				fingDto.setBirth(status == 1 ? true : false);
				fingDto.setRealName(StringUtil.isNotBlank(member.getTrueName())?member.getTrueName():StringUtil.maskMobileCanNull(member.getMobile()));

			} catch (ManagerException birthE) {
				logger.error("发现页面获取用户是否生日异常" + memberId, birthE);
				fingDto.setBirth(false);
				result.setIsError();
			}
			
		MemberVip memberVip = new MemberVip();
		try {
			memberVip = memberVipManager
					.selectRecentMemberVipByMemberId(memberId);
		} catch (ManagerException birthE) {
			memberVip.setVipLevel(0);
		}
		// 人气值商城，图标banner
		List<Icon> iconList = Lists.newArrayList();
		
		String[] iconEles = { "first", "second", "third", "fourth" };
		Integer[] jumpFlags = { 0,0, 0, 0 };
		String[] urlEles = {
				"/security/find/getPopularityParkInfor?isNeedYRWtoken=Y",
				"/yrwHtml/memberCenter?isAuthorization=Y",
				"/yrwHtml/investProductList?isAuthorization=Y",
				"/mstation/post/gainPopularity?isAuthorization=Y" };// 接口路由地址
		String[] remarks = {fingDto.getPopularity()+"点人气值","当前等级：V"+memberVip.getVipLevel(),"兑券赚收益","玩转乐园嗨不停"};
		String aLiYun = PropertiesUtil.getAliyunUrl();
		for (int i = 0; i < 4; i++) {
			String e = iconEles[i];
			String url = urlEles[i];
			Integer jumpFlag = jumpFlags[i];
			String remark = remarks[i];
			Icon icon = new Icon();
			icon.setName(SysServiceUtils.getDictValue(e + "_name", "app_find", ""));
			icon.setImage(aLiYun
					+ SysServiceUtils.getDictValue(e + "_icon", "app_find", ""));
			icon.setHref(url);
			icon.setJumpFlag(jumpFlag);
			icon.setRemark(remark);
			iconList.add(icon);
		}
		fingDto.setIconList(iconList);

		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			int status = this.getBirthdayStatus(member.getBirthday());
			fingDto.setBirth(status == 1 ? true : false);
			fingDto.setRealName(StringUtil.isNotBlank(member.getTrueName())?member.getTrueName():StringUtil.maskMobileCanNull(member.getMobile()));

		} catch (ManagerException birthE) {
			logger.error("发现页面获取用户是否生日异常" + memberId, birthE);
			fingDto.setBirth(false);
			result.setIsError();
		}

		// 是否签到
		try {
			fingDto.setChecked(memberCheckManager.isChecked(memberId));
		} catch (ManagerException e) {
			logger.error("发现页面获取用户是否签到异常" + memberId, e);
			fingDto.setChecked(false);
			result.setIsError();
		}

		Integer qucikLotteryNum = activityLotteryManager
				.getQuickLotteryNumber(memberId);
		fingDto.setQuickLotteryNum(qucikLotteryNum);

		
		result.setResult(fingDto);
		return result;
	}
	
	
	@Override
	public ResultDTO<FindDto> getFindPageUnlogin(FindDto fingDto) {

		ResultDTO<FindDto> result = new ResultDTO<FindDto>();

		fingDto.setPopularity(0);
		fingDto.setPopularityForCheck(0);
		fingDto.setRecommendNum(0);
		
		List<BannerFroAreaBiz> activityBannerList = Lists.newArrayList();
			
		List<Banner> bannerList = bannerService.findAppFindBanner();
		activityBannerList = BeanCopyUtil.mapList(
					bannerList, BannerFroAreaBiz.class);
			
		for (BannerFroAreaBiz ban : activityBannerList) {
			ban.setImage(Config.ossPicUrl + ban.getImage());
		}
		fingDto.setActivityBannerList(activityBannerList);
			
		fingDto.setBirth(false);
			
			
		// 人气值商城，图标banner
		List<Icon> iconList = Lists.newArrayList();
		
		String[] iconEles = { "first", "second", "third", "fourth" };
		Integer[] jumpFlags = { 0,0, 0, 0 };
		String[] urlEles = {
				"/security/find/getPopularityParkInfor?isNeedYRWtoken=Y",
				"/yrwHtml/memberCenter?isAuthorization=Y",
				"/yrwHtml/investProductList?isAuthorization=Y",
				"/mstation/post/gainPopularity?isAuthorization=Y" };// 接口路由地址
		String[] remarks = {"精彩等你发现","尊享等级特权","兑券赚收益","玩转乐园嗨不停"};
		String aLiYun = PropertiesUtil.getAliyunUrl();
		for (int i = 0; i < 4; i++) {
			String e = iconEles[i];
			String url = urlEles[i];
			Integer jumpFlag = jumpFlags[i];
			String remark = remarks[i];
			Icon icon = new Icon();
			icon.setName(SysServiceUtils.getDictValue(e + "_name", "app_find", ""));
			icon.setImage(aLiYun
					+ SysServiceUtils.getDictValue(e + "_icon", "app_find", ""));
			icon.setHref(url);
			icon.setJumpFlag(jumpFlag);
			icon.setRemark(remark);
			iconList.add(icon);
		}
		fingDto.setIconList(iconList);

		fingDto.setBirth(false);
		fingDto.setChecked(false);

		fingDto.setQuickLotteryNum(0);
		
		result.setResult(fingDto);
		return result;
	}
	
	
	public int getBirthdayStatus(Date birthday) {
		if (birthday == null) {
			return 0;
		}
		Date date = DateUtils.getCurrentDate();
		int c_month = DateUtils.getMonth(date);
		int c_day = DateUtils.getDate(date);
		int b_month = DateUtils.getMonth(birthday);
		int b_day = DateUtils.getDate(birthday);
		if (c_month < b_month) {
			return 0;// 生日还未到
		} else if (c_month == b_month && c_day == b_day) {
			return 1;// 今天生日
		} else {
			return -1;// 生日已经过了
		}
	}

	/**
	 * 人气值乐园数据
	 */
	@Override
	public ResultDTO<Object> getPopularityParkInfor(Long memberId) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			long a=System.currentTimeMillis();
			
			PopularityParkDto popDto = new PopularityParkDto();
			Boolean levelUpFlag =false;
			Member member = memberManager.selectByPrimaryKey(memberId);
			if (member != null) {
				MemberVip memberVip = memberVipManager
						.selectRecentMemberVipByMemberId(memberId);
				if (memberVip == null) {
					ResultCode.CUSTOM.setMsg("data is shit");
					result.setResultCode(ResultCode.CUSTOM);
					return result;
				}
				popDto.setMemberId(memberId);
				popDto.setUsername(StringUtil.isNotBlank(member.getUsername())?member.getUsername():StringUtil.maskMobileCanNull(member.getMobile()));
				popDto.setAvatars(Config.ossPicUrl+member.getAvatars());
				popDto.setVipLevel(memberVip.getVipLevel());
				popDto.setScore(memberVip.getScore().intValue());
				popDto.setNeedSncreaseScore(memberVip.getNeedSncreaseScore());
				levelUpFlag = memberVipManager.isLevelUp(memberId);
				
				// 人气值总额
				Balance balance = balanceService.queryBalance(memberId,
						TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
				if (balance != null) {
					popDto.setPopularity(balance.getBalance().setScale(0, BigDecimal.ROUND_DOWN));
				}
			}
			popDto.setLevelUpFlag(levelUpFlag);
			logger.info("【人气值商城】获得人气值首页数据-用户个人信息执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
			

			GoodsQuery goodsQuery = new GoodsQuery();
			goodsQuery.setLimitSize(2);// 首页显示两个商品
			// 投资专项
			goodsQuery.setGoodsType(TypeEnum.GOODS_TYPE_FOR_INVEST.getType());
			List<Goods> investList = goodsManager.queryGoodsList(goodsQuery);
			List<GoodsForAppDto> investListDto = BeanCopyUtil.mapList(
					investList, GoodsForAppDto.class);
			this.getMoreInforForGood(investListDto,memberId);
			// 虚拟卡券
			goodsQuery.setGoodsType(TypeEnum.GOODS_TYPE_VIRTUAL_CARD.getType());
			List<Goods> virtualList = goodsManager.queryGoodsList(goodsQuery);
			List<GoodsForAppDto> virtualListDto = BeanCopyUtil.mapList(
					virtualList, GoodsForAppDto.class);
			this.getMoreInforForGood(virtualListDto,memberId);
			// 实物
			goodsQuery.setGoodsType(TypeEnum.GOODS_TYPE_PHYSICAL.getType());
			List<Goods> physicalList = goodsManager.queryGoodsList(goodsQuery);
			List<GoodsForAppDto> physicalListDto = BeanCopyUtil.mapList(
					physicalList, GoodsForAppDto.class);
			this.getMoreInforForGood(physicalListDto,memberId);
			//双节特惠
			goodsQuery.setGoodsType(TypeEnum.GOODS_TYPE_DOUBLE.getType());
			List<Goods> doubleList = goodsManager.queryGoodsList(goodsQuery);
			List<GoodsForAppDto> doubleListDto = BeanCopyUtil.mapList(
					doubleList, GoodsForAppDto.class);
			this.getMoreInforForGood(doubleListDto,memberId);

			popDto.setInvestmentList(investListDto);
			popDto.setVirtualCardList(virtualListDto);
			popDto.setPhysicalList(physicalListDto);
			popDto.setDoubleList(doubleListDto);

			logger.info("【人气值商城】获得人气值首页数据-部分商品执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");

			//全部
			//GoodsQuery goodsQueryAll = new GoodsQuery();
			// 投资专项
			
			/*String keyInvest = RedisConstant.POPULAR_PARK_GOOD_INVEST_ALL;
			List<GoodsForAppDto> investListAllDto = Lists.newArrayList();
			if (RedisManager.isExitByObjectKey(keyInvest)) {
				//缓存存在
				investListAllDto= (List<GoodsForAppDto>) RedisManager.getObject(keyInvest);
			}
			//缓存不存在
			if (Collections3.isEmpty(investListAllDto)) {
				goodsQueryAll.setGoodsType(TypeEnum.GOODS_TYPE_FOR_INVEST.getType());
				List<Goods> investListAll = goodsManager.queryGoodsList(goodsQueryAll);
				 investListAllDto = BeanCopyUtil.mapList(
						investListAll, GoodsForAppDto.class);
				if(Collections3.isNotEmpty(investListAllDto)){
					//RedisForProjectClient.addBatchTransactionDetail(projectId,transactionForProjects);
					RedisManager.putObject(keyInvest, investListAllDto);
					RedisManager.expire(keyInvest, 60);
				}
			}
			this.getMoreInforForGood(investListAllDto,memberId);//价格根据用户变动
			popDto.setInvestmentListAll(investListAllDto);*/
			
			/*// 双节特惠
			String keyDouble = RedisConstant.POPULAR_PARK_GOOD_Double_ALL;
			List<GoodsForAppDto> doubleListAllDto = Lists.newArrayList();
			if (RedisManager.isExitByObjectKey(keyDouble)) {
				//缓存存在
				doubleListAllDto= (List<GoodsForAppDto>) RedisManager.getObject(keyDouble);
			}
			//缓存不存在
			if (Collections3.isEmpty(doubleListAllDto)) {
				goodsQueryAll.setGoodsType(TypeEnum.GOODS_TYPE_DOUBLE.getType());
				List<Goods> doubleListAll = goodsManager.queryGoodsList(goodsQueryAll);
				doubleListAllDto = BeanCopyUtil.mapList(
						doubleListAll, GoodsForAppDto.class);
				if(Collections3.isNotEmpty(doubleListAllDto)){
					//RedisForProjectClient.addBatchTransactionDetail(projectId,transactionForProjects);
					RedisManager.putObject(keyDouble, doubleListAllDto);
					RedisManager.expire(keyDouble, 60);
				}
			}
			this.getMoreInforForGood(doubleListAllDto,memberId);//价格根据用户变动
			popDto.setDoubleListAll(doubleListAllDto);

			logger.info("【人气值商城】获得人气值首页数据-全部商品执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");*/

			/*popDto.setVirtualCardListAll(virtualListAllDto);
			popDto.setPhysicalListAll(physicalListAllDto);*/
			

			/*DynamicParamBuilder paramBuilder = new DynamicParamBuilder();
			paramBuilder.setMemberId(memberId);
			Map<String, Object> paramMap = Maps.newHashMap();
			paramBuilder.setParamMap(paramMap);
			ResultDTO<Object> rDTO = (ResultDTO<Object>) this.purchaseHistory(paramBuilder);
			popDto.setShopPage((Page<ShopOrderDetilDto>)rDTO.getResult());
			
			logger.info("【人气值商城】获得人气值首页数据-购买历史执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");*/
			
			//人气值过期需清零点数
			OverduePopularityBiz overduePopularityBiz= popularityInOutLogManager.queryOverduePopularity(memberId);
			popDto.setOverduePopularity(overduePopularityBiz);
			result.setResult(popDto);
			logger.info("【人气值商城】获得人气值首页数据执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
		} catch (ManagerException e) {
			logger.error("【人气值商城】获得人气值首页数据异常：memberId={}", memberId, e);
		}
		return result;
	}

	private void getMoreInforForGood(List<GoodsForAppDto> goodsList,Long memberId)
			throws ManagerException {

		for (GoodsForAppDto goods : goodsList) {
			BigDecimal discountPrice = memberVipManager.getGoodValueByMemberVip(memberId, goods.getId());
			goods.setDiscountPrice(discountPrice);
			
			BscAttachment bsc = bscAttachmentManager
					.getBscAttachmentByKeyIdAndModule(goods.getId().toString(),
							AttachmentEnum.ATTACHMENT_MODULE_SHOP_GOODS.getCode());
			if (bsc == null) {
				continue;
			}
			goods.setImage(bsc.getFileUrl());
		}
	}

	// 人气值乐园九宫格
	@Override
	public Object anniversaryNineGrid(DynamicParamBuilder paramBuilder) {
		ResultDTO<ActivityForAnniversary> rDTO = new ResultDTO<ActivityForAnniversary>();
		// 登录认证
		if (!paramBuilder.isMemberFlag()) {
			rDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return rDTO;
		}
		Long memberId = paramBuilder.getMemberId();
		Integer chip = (Integer) paramBuilder.getParamMap().get("chip");
		try {
			Optional<Activity> olympicActivity = LotteryContainer
					.getInstance()
					.getActivityByName(
							ActivityConstant.ACTIVITY_PLAY_POPULARITY_PARK_NINE);
			if (!olympicActivity.isPresent()) {
				rDTO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return rDTO;
			}
			Activity activity = olympicActivity.get();
			Long actId = activity.getId();
			// 判断是否在活动期间内
			if (drawByProbability.isInActivityTime(actId)) {
				RuleBody rb = new RuleBody();
				rb.setActivityId(actId);
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_POPULAR
						.getStatus());
				rb.setCycleStr(actId.toString());
				rb.setDeductValue(chip);
				rb.setDeductRemark("【人气值乐园九宫格活动】幸运九宫格人气值下注");
				rb.setRewardsAvailableNum(3);
				rb.setRewardsPoolMaxNum(9);
				// 校验
				if (drawByProbability
						.validate(rb,
								TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY
										.getCode())) {
					// 抽奖
					RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability
							.drawLottery(
									rb,
									chip,
									TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITY
											.getCode());
					ActivityForAnniversary afa = new ActivityForAnniversary();
					// 刷新人气值
					Balance balance = balanceService.queryBalance(memberId,
							TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
					if (balance != null) {
						afa.setPopularityVaule(balance.getAvailableBalance()
								.intValue());
					}
					afa.setRewardCode(rfp.getRewardCode());
					rDTO.setResult(afa);
					if (!"noReward".equals(rfp.getRewardCode())) {
						// 清空缓存
						String key = RedisConstant.REDIS_KEY_ACTIVITY
								+ RedisConstant.REDIS_SEPERATOR
								+ RedisConstant.REDIS_KEY_ACTIVITY_POPULARITY_PARK_NINE
								+ RedisConstant.REDIS_SEPERATOR
								+ RedisConstant.REDIS_KEY_ACTIVITY_POPULARITY_PARK_NINE_LIST;
						RedisManager.removeObject(key);
						/*
						 * // 发送站内信 String value = null; if
						 * ("PopularityFor10times".equals(rfp.getRewardCode()))
						 * { value = new BigDecimal(chip).multiply(new
						 * BigDecimal(10)).toString(); } else if
						 * ("PopularityFor5times".equals(rfp.getRewardCode())) {
						 * value = new BigDecimal(chip).multiply(new
						 * BigDecimal(5)).toString(); } else if
						 * ("PopularityFor2times".equals(rfp.getRewardCode())) {
						 * value = new BigDecimal(chip).multiply(new
						 * BigDecimal(2)).toString(); }
						 * MessageClient.sendMsgForSPEngin(memberId,
						 * "【干杯！我们的纪念日】幸运25宫格", value + "点人气值");
						 */
					}
					return rDTO;
				} else {
					rDTO.setResultCode(ResultCode.ACTIVITY_POPULARITY__ERROR);
					return rDTO;
				}

			} else {
				rDTO.setResultCode(ResultCode.ACTIVITY_NOT_START_OR_YET_END_ERROR);
				return rDTO;
			}
		} catch (Exception e) {
			logger.error("【人气值商城】人气值乐园九宫格错误, memberId={}, chip={}", memberId, chip, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			/*
			 * if (e.getClass().equals(ManagerException.class)) { String
			 * errorMsg = e.getMessage(); if (errorMsg.startsWith("可用余额不够")) {
			 * 
			 * rDTO.getResultCodes().get(0).setMsg(errorMsg); } }
			 */
		}
		return rDTO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object anniversaryNineGridResult(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		PopularityNineDto popResult = new PopularityNineDto();
		Long activityId = null;
		try {
			Long memberId = paramBuilder.getMemberId();
			String key = RedisConstant.REDIS_KEY_ACTIVITY
					+ RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_POPULARITY_PARK_NINE
					+ RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_POPULARITY_PARK_NINE_LIST;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			List<ActivityLotteryResultBiz> list = null;
			if (isExit) {
				list = (List<ActivityLotteryResultBiz>) RedisManager
						.getObject(key);
			} else {
				Optional<Activity> olympicActivity = LotteryContainer
						.getInstance()
						.getActivityByName(
								ActivityConstant.ACTIVITY_PLAY_POPULARITY_PARK_NINE);
				if (!olympicActivity.isPresent()) {
					rDTO.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
					return rDTO;
				}

				Activity activity = olympicActivity.get();
				activityId = activity.getId();
				List<ActivityLotteryResult> modelList = activityLotteryManager
						.queryNewLotteryResult(activityId, null, 30);
				list = BeanCopyUtil.mapList(modelList,
						ActivityLotteryResultBiz.class);
				if (CollectionUtils.isNotEmpty(list)) {
					for (ActivityLotteryResultBiz model : list) {
						model.setMemberName(ServletUtil.getMemberUserName(model
								.getMemberId()));
						model.setAvatar(ServletUtil.getMemberAvatarById(model
								.getMemberId()));
						model.setMemberId(null);
					}
					RedisManager.putObject(key, list);
					RedisManager.expireObject(key, 1);
				}
			}
			Balance balance = balanceService.queryBalance(memberId,
					TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
			if (balance != null) {
				popResult.setPopularity(balance.getBalance().intValue());
			}
			popResult.setList(list);
			rDTO.setResult(popResult);
		} catch (Exception e) {
			logger.error("【人气值商城】人气值乐园九宫格中奖结果查询失败, activityId={}", activityId, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}
	

	public Object getQuickReward(DynamicParamBuilder paramBuilder) {

		ResultDTO<QuickRewardDto> result = new ResultDTO<QuickRewardDto>();
		QuickRewardDto quickReward = new QuickRewardDto();
		/*
		 * if (!paramBuilder.getParamMap().containsKey("projectId")) {
		 * result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
		 * return result; } Long projectId = (Long)
		 * paramBuilder.getParamMap().get("projectId");
		 * 
		 * List<LotteryRuleAmountNumber> lottery = projectExtraManager
		 * .getLotteryByProjectId(projectId);
		 * 
		 * List<PrizeInPool> prizeInPoolList = projectExtraManager
		 * .getPrizeInPoolByProjectId(projectId);
		 * 
		 * List<PrizePool> prizePoolList = projectExtraManager
		 * .getPrizePoolByProjectId(projectId);
		 */
		try {
			Activity activity = new Activity();
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
					.getActivityBySelective(activity);
			if (activityList != null) {
				Activity act = activityList.get(activityList.size() - 1);

				String mapJson = act.getObtainConditionsJson();

				Map map = JSON.parseObject(mapJson);

				String prizePoolJson = map.get("prizePool").toString();
				List<PrizePool> prizePool = JSON.parseArray(prizePoolJson,
						PrizePool.class);
				String prizeInPoolJson = map.get("prizeInPool").toString();
				List<PrizeInPool> prizeInPool = JSON.parseArray(
						prizeInPoolJson, PrizeInPool.class);
				String lotteryJson = map.get("lottery").toString();
				List<LotteryRuleAmountNumber> lottery = JSON.parseArray(
						lotteryJson, LotteryRuleAmountNumber.class);

				List<CouponTemplate> couponList= couponTemplateRelationManager.getDirectReward();
				Collections.reverse(couponList);
				
				quickReward.setLottery(lottery);
				quickReward.setPrizePool(prizePool);
				quickReward.setPrizeInPool(prizeInPool);
				quickReward.setCouponList(couponList);
			}

			result.setResult(quickReward);
		} catch (ManagerException e) {
			logger.error("发现,获取直投有奖规则专题页异常", e);
		}

		return result;

	}

	/**
	 * 项目进度
	 * 
	 * @param totalAmount
	 * @param availableBalance
	 * @return
	 */
	private String getProjectNumberProgress(BigDecimal totalAmount, Long id)
			throws ManagerException {
		String progress = "0";
		BigDecimal availableBalance = getProjectBalanceById(id);
		if (availableBalance != null) {
			if (availableBalance.compareTo(BigDecimal.ZERO) <= 0) {
				progress = "100";
			} else if (availableBalance.compareTo(totalAmount) == 0) {
				progress = "0";
			} else {
				progress = new DecimalFormat("###.##").format((totalAmount
						.subtract(availableBalance)).divide(totalAmount, 4,
						RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
			}
		}
		return progress;
	}

	private BigDecimal getProjectBalanceById(Long id) {
		// 可用余额
		BigDecimal availableBalance = null;
		try {
			// 从缓存中找可用余额
			// availableBalance = RedisProjectClient.getProjectBalance(id);
			if (availableBalance == null) {
				// logger.info("项目"+id+"，可用余额在redis未找到。");
				// 如果为Null，到余额表找
				Balance _balance = balanceManager.queryBalance(id,
						TypeEnum.BALANCE_TYPE_PROJECT);
				if (_balance != null) {
					availableBalance = _balance.getAvailableBalance();
				} else {
					logger.debug("项目" + id + "，可用余额在余额表未找到。");
				}
			}
			if (availableBalance == null) {
				// 再没有，那只能从项目中去找了
				Project project = projectManager.selectByPrimaryKey(id);
				availableBalance = project.getTotalAmount();
			}
			logger.debug("项目" + id + "，可用余额" + availableBalance);
		} catch (ManagerException e) {
			logger.error("项目" + id + "查找", e);
		}
		return availableBalance;
	}

	@Override
	public Object directLottery(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			// 前置校验
			if (!paramBuilder.isMemberFlag()) {
				rDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return rDTO;
			}
			if (!paramBuilder.getParamMap().containsKey("projectId")
					|| !paramBuilder.getParamMap().containsKey("type")) {
				rDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDTO;
			}
			Long projectId = (Long) paramBuilder.getParamMap().get("projectId");
			Integer type = (Integer) paramBuilder.getParamMap().get("type");
			Long memberId = paramBuilder.getMemberId();

			ResultDO<Object> rDO = projectManager.directProjectLottery(
					projectId, memberId, type);
			List<PrizeInPool> prizList = Lists.newArrayList();
			Map  maps = Maps.newHashMap();
			Integer rewardAmount = 0;
			Integer popularity = 0;
			if (rDO.isSuccess()) {
				ProjectForLotteryReturn reward = (ProjectForLotteryReturn) rDO
						.getResult();
				popularity = Integer.valueOf(reward.getPopularity());
				List<DirectLotteryResultList> directLotteryResultList  = reward.getDirectLotteryResultList();
				Map<String, Object> map = Maps.newHashMap();
				if(Collections3.isNotEmpty(directLotteryResultList)){
						for (DirectLotteryResultList dir:directLotteryResultList) {
							if (map.containsKey(dir.getPrize())) {
								map.put(dir.getPrize(), Integer.valueOf(map.get(
										dir.getPrize()).toString()) + 1);
							} else {
								map.put(dir.getPrize(), 1);
							}
							
							rewardAmount +=Integer.valueOf(dir.getRewardAmount());
						}
					
				}
				for (Entry<String, Object> entry : map.entrySet()) {
					PrizeInPool pri = new PrizeInPool();
					pri.setLevel(Integer.valueOf(entry.getKey()));
					pri.setNum(Integer.valueOf(entry.getValue().toString()));
					prizList.add(pri);
				}
			}
			maps.put("prizList", prizList);
			maps.put("rewardAmount", rewardAmount);
			maps.put("popularity",popularity);
			
			rDTO.setResultCode(rDO.getResultCode());
			rDTO.setResult(maps);
			if (rDO.isSuccess()) {
				rDTO.setIsSuccess();
			} else if (rDO.isError()) {
				rDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("发现,快投有奖,抽奖异常", e);
		}
		return rDTO;
	}

	public ResultDTO<Object> quickRewardInit(Long memberId) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		ProjectForLottery model = new ProjectForLottery();
		try {
			if(memberId==null){
				result.setResultCode(ResultCode.MEMBER_ID_IS_NOT_NULL);
				return result;
			}
			// 抽奖次数
			List<ActivityLottery> listLottery = activityLotteryManager
					.selectActivityLotteryByMemberId(memberId,
							ActivityConstant.DIRECT_COUNT_LOTTERY_KEY);
			List<ActivityProject> listProjectLottery = Lists.newArrayList();
			if (Collections3.isNotEmpty(listLottery)) {
				for (ActivityLottery lottery : listLottery) {
					ActivityProject activityp = new ActivityProject();
					activityp.setNummber(lottery.getRealCount());
					activityp.setProjectId(Long.parseLong(lottery
							.getCycleConstraint()));
					Project project = projectManager.selectByPrimaryKey(Long
							.parseLong(lottery.getCycleConstraint()));
					if (project != null) {
						activityp.setProjectName(project.getName());
						activityp.setThumbnail(project.getThumbnail());
					}
					listProjectLottery.add(activityp);
				}
			}else{
				//抽奖次数为0
				if (projectExtraManager.isInvestingQuickProject()) {// 有可投的快投项目
					
				}else{
					Long id = projectExtraManager.getQuickProjectLately();
					//String prizePool =  projectExtraManager.getPrizePoolAmountByProjectId(id);
					// 查询上一期奖励最高的金额，块投二期上线第一期没有数据先写死
					String prizePool = "375";
					if (id != null && id > 0l) {
						ActivityLotteryResult data = activityLotteryResultManager.getMaxRewardForQuickProject(id
								.toString());
						prizePool = data.getRewardInfo();
					}
					model.setProjectId(id);
					model.setPrizePool(prizePool);
				}
			}
			model.setListProjectLottery(listProjectLottery);

			// 中奖信息
			List<ProjectForReward> projectForReward = Lists.newArrayList();
			ActivityLotteryResult modelResult = new ActivityLotteryResult();
			modelResult.setRewardType(5);
			modelResult.setMemberId(memberId);
			modelResult
					.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);
			List<ProjectForFront> listRewardProject = projectManager
					.findQuickInvestLotteryProject(modelResult);
			if (Collections3.isNotEmpty(listRewardProject)) {
				for (ProjectForFront bean : listRewardProject) {
					ProjectForReward reward = new ProjectForReward();
					reward.setId(bean.getId());
					reward.setName(bean.getName());
					if (StatusEnum.PROJECT_STATUS_LOSE.getStatus() == bean
							.getStatus()) {
						reward.setProgress("募集失败");
						reward.setRewardInfo("奖励已失效");
						reward.setStatus(1);
					} else if (StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus() == bean
									.getStatus()
							|| StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus() == bean
									.getStatus()
							|| StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus() == bean
									.getStatus()) {
						String desc = "已发放";
						reward.setProgress("募集成功");
						reward.setStatus(2);
						// 募集满额经历天数
						int totalDays = DateUtils.getIntervalDays(bean.getOnlineTime(), bean.getSaleComplatedTime()) + 1;
						List<PrizePool> prizePoolList = projectExtraManager
								.getPrizePoolByProjectId(bean.getId());
						int maxDay = 0;
						if (Collections3.isNotEmpty(prizePoolList)) {
							for (PrizePool pri : prizePoolList) {
								if (Float.parseFloat(pri.getRatio())<= 0) {
									continue;
								}
								if (maxDay < pri.getDay()) {
									maxDay = pri.getDay();
								}
							}
						}

						if (totalDays > maxDay &&Collections3.isNotEmpty(prizePoolList)) {
							reward.setStatus(4);
							reward.setRewardInfo("募集时间超过奖励期限，无奖励");
						} else {
							ActivityLotteryResult modelResultAmount=new ActivityLotteryResult(); 
							modelResultAmount.setMemberId(memberId);
							modelResultAmount.setRewardId(bean.getId().toString());
							
							reward.setTotalRewardAmount(activityLotteryResultManager
									.sumRewardInfoByProjectId(modelResultAmount, 3));
							reward.setPopularity(activityLotteryResultManager
									.sumRewardInfoByProjectId(modelResultAmount, 1));
							reward.setTotalCash(activityLotteryResultManager
									.sumRewardInfoByProjectId(modelResultAmount, 7));
							String rewardInfo = "";
							if (reward.getTotalCash() != null && reward.getTotalCash().intValue() > 0) {
								rewardInfo = reward.getTotalCash() + "元现金";
							}
							if(reward.getTotalRewardAmount()!=null&&reward.getTotalRewardAmount().intValue()>0){
								if (StringUtil.isNotBlank(rewardInfo)) {
									rewardInfo += "，" + reward.getTotalRewardAmount() + "元现金券";
								} else {
									rewardInfo = reward.getTotalRewardAmount() + "元现金券";
								}
							}
							rewardInfo += desc;
							if(reward.getPopularity()!=null&&reward.getPopularity().intValue()>0){
								if (StringUtil.isNotBlank(rewardInfo)) {
									rewardInfo += "，" + reward.getPopularity() + "点人气值";
								} else {
									rewardInfo = reward.getPopularity() + "点人气值";
								}
							}
							reward.setRewardInfo(rewardInfo);
						}
					} else {
						String desc = "履约当日发放";
						ActivityLotteryResult modelResultAmount = new ActivityLotteryResult();
						modelResultAmount.setMemberId(memberId);
						modelResultAmount.setRewardId(bean.getId().toString());

						reward.setTotalRewardAmount(activityLotteryResultManager
								.sumRewardInfoByProjectId(modelResultAmount, 3));
						reward.setPopularity(activityLotteryResultManager
								.sumRewardInfoByProjectId(modelResultAmount, 1));
						reward.setTotalCash(activityLotteryResultManager
								.sumRewardInfoByProjectId(modelResultAmount, 7));
						String rewardInfo = "";
						if (reward.getTotalCash() != null && reward.getTotalCash().intValue() > 0) {
							rewardInfo = reward.getTotalCash() + "元现金";
						}
						if (reward.getTotalRewardAmount() != null && reward.getTotalRewardAmount().intValue() > 0) {
							if (StringUtil.isNotBlank(rewardInfo)) {
								rewardInfo += "，" + reward.getTotalRewardAmount() + "元现金券";
							} else {
								rewardInfo = reward.getTotalRewardAmount() + "元现金券";
							}
						}
						rewardInfo += desc;
						if (reward.getPopularity() != null && reward.getPopularity().intValue() > 0) {
							if (StringUtil.isNotBlank(rewardInfo)) {
								rewardInfo += "，" + reward.getPopularity() + "点人气值";
							} else {
								rewardInfo = reward.getPopularity() + "点人气值";
							}
						}
						reward.setRewardInfo(rewardInfo);
						reward.setProgress("进度"
								+ getProjectNumberProgress(
										bean.getTotalAmount(), bean.getId())
								+ "%");
						reward.setStatus(3);
					}
					// 根据项目ID查询 用户中奖个数
					modelResult.setRemark(bean.getId().toString());
					modelResult.setRewardType(5);
					List<ProjectForLevel> projectForLevel = activityLotteryResultManager
							.getRewardLevelByProjectId(modelResult);
					reward.setProjectForLevel(projectForLevel);

					projectForReward.add(reward);
				}
			}
			model.setProjectForReward(projectForReward);

			result.setResult(model);
		} catch (ManagerException e) {
			logger.error("发现，直投抽奖抽奖页面，初始化失败", memberId, e);
		}
		return result;
	}
	
	public Object quickRewardRefresh(DynamicParamBuilder paramBuilder) {

		ResultDTO<Object> result = new ResultDTO<Object>();
		ProjectForLottery model = new ProjectForLottery();
		// 前置校验
		if (!paramBuilder.isMemberFlag()) {
			result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return result;
		}
		Long memberId = paramBuilder.getMemberId();
		try {
			// 抽奖次数
			List<ActivityLottery> listLottery = activityLotteryManager
					.selectActivityLotteryByMemberId(memberId,
							ActivityConstant.DIRECT_COUNT_LOTTERY_KEY);
			List<ActivityProject> listProjectLottery = Lists.newArrayList();
			if (Collections3.isNotEmpty(listLottery)) {
				for (ActivityLottery lottery : listLottery) {
					if(lottery.getRealCount()<=0){
						continue;
					}
					ActivityProject activityp = new ActivityProject();
					activityp.setNummber(lottery.getRealCount());
					activityp.setProjectId(Long.parseLong(lottery
							.getCycleConstraint()));
					Project project = projectManager.selectByPrimaryKey(Long
							.parseLong(lottery.getCycleConstraint()));
					if (project != null) {
						activityp.setProjectName(project.getName());
						activityp.setThumbnail(project.getThumbnail());
					}
					listProjectLottery.add(activityp);
				}
			}else{
				//抽奖次数为0
				if(projectExtraManager.isInvestingQuickProject()){//有可投的快投项目
					
				}else{
					Long id = projectExtraManager.getQuickProjectLately();
					// 查询上一期奖励最高的金额，块投二期上线第一期没有数据先写死
					String prizePool = "375";
					if (id != null && id > 0l) {
						ActivityLotteryResult data = activityLotteryResultManager.getMaxRewardForQuickProject(id
								.toString());
						prizePool = data.getRewardInfo();
					}
					model.setProjectId(id);
					model.setPrizePool(prizePool);
				}
			}
			model.setListProjectLottery(listProjectLottery);

			// 中奖信息
			List<ProjectForReward> projectForReward = Lists.newArrayList();
			ActivityLotteryResult modelResult = new ActivityLotteryResult();
			modelResult.setRewardType(5);
			modelResult.setMemberId(memberId);
			modelResult
					.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);
			List<ProjectForFront> listRewardProject = projectManager
					.findQuickInvestLotteryProject(modelResult);
			if (Collections3.isNotEmpty(listRewardProject)) {
				for (ProjectForFront bean : listRewardProject) {
					ProjectForReward reward = new ProjectForReward();
					reward.setId(bean.getId());
					reward.setName(bean.getName());
					if (StatusEnum.PROJECT_STATUS_LOSE.getStatus() == bean
							.getStatus()) {
						reward.setProgress("募集失败");
						reward.setRewardInfo("奖励已失效");
						reward.setStatus(1);
					} else if (StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus() == bean
									.getStatus()
								|| StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus() == bean
									.getStatus()
								|| StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus() == bean
								.getStatus()) {
						String desc = "已发放";
						reward.setProgress("募集成功");
						reward.setStatus(2);
						// 募集满额经历天数
						int totalDays = DateUtils.getIntervalDays(bean.getOnlineTime(), bean.getSaleComplatedTime()) + 1;
						List<PrizePool> prizePoolList = projectExtraManager
								.getPrizePoolByProjectId(bean.getId());
						int maxDay = 0;
						if (Collections3.isNotEmpty(prizePoolList)) {
							for (PrizePool pri : prizePoolList) {
								if (Float.parseFloat(pri.getRatio())<= 0) {
									continue;
								}
								if (maxDay < pri.getDay()) {
									maxDay = pri.getDay();
								}
							}
						}

						if (totalDays > maxDay &&Collections3.isNotEmpty(prizePoolList)) {
							reward.setStatus(4);
							reward.setRewardInfo("募集时间超过奖励期限，无奖励");
						} else {
							ActivityLotteryResult modelResultAmount=new ActivityLotteryResult(); 
							modelResultAmount.setMemberId(memberId);
							modelResultAmount.setRewardId(bean.getId().toString());
							
							reward.setTotalRewardAmount(activityLotteryResultManager
									.sumRewardInfoByProjectId(modelResultAmount, 3));
							reward.setPopularity(activityLotteryResultManager
									.sumRewardInfoByProjectId(modelResultAmount, 1));
							reward.setTotalCash(activityLotteryResultManager
									.sumRewardInfoByProjectId(modelResultAmount, 7));
							String rewardInfo = "";
							if (reward.getTotalCash() != null && reward.getTotalCash().intValue() > 0) {
								rewardInfo = reward.getTotalCash() + "元现金";
							}
							if(reward.getTotalRewardAmount()!=null&&reward.getTotalRewardAmount().intValue()>0){
								if (StringUtil.isNotBlank(rewardInfo)) {
									rewardInfo += "，" + reward.getTotalRewardAmount() + "元现金券";
								} else {
									rewardInfo = reward.getTotalRewardAmount() + "元现金券";
								}
							}
							rewardInfo += desc;
							if(reward.getPopularity()!=null&&reward.getPopularity().intValue()>0){
								if (StringUtil.isNotBlank(rewardInfo)) {
									rewardInfo += "，" + reward.getPopularity() + "点人气值";
								} else {
									rewardInfo = reward.getPopularity() + "点人气值";
								}
							}
							reward.setRewardInfo(rewardInfo);
						}
					} else {
						String desc = "履约当日发放";
						ActivityLotteryResult modelResultAmount = new ActivityLotteryResult();
						modelResultAmount.setMemberId(memberId);
						modelResultAmount.setRewardId(bean.getId().toString());

						reward.setTotalRewardAmount(activityLotteryResultManager
								.sumRewardInfoByProjectId(modelResultAmount, 3));
						reward.setPopularity(activityLotteryResultManager
								.sumRewardInfoByProjectId(modelResultAmount, 1));
						reward.setTotalCash(activityLotteryResultManager
								.sumRewardInfoByProjectId(modelResultAmount, 7));
						String rewardInfo = "";
						if (reward.getTotalCash() != null && reward.getTotalCash().intValue() > 0) {
							rewardInfo = reward.getTotalCash() + "元现金";
						}
						if (reward.getTotalRewardAmount() != null && reward.getTotalRewardAmount().intValue() > 0) {
							if (StringUtil.isNotBlank(rewardInfo)) {
								rewardInfo += "，" + reward.getTotalRewardAmount() + "元现金券";
							} else {
								rewardInfo = reward.getTotalRewardAmount() + "元现金券";
							}
						}
						rewardInfo += desc;
						if (reward.getPopularity() != null && reward.getPopularity().intValue() > 0) {
							if (StringUtil.isNotBlank(rewardInfo)) {
								rewardInfo += "，" + reward.getPopularity() + "点人气值";
							} else {
								rewardInfo = reward.getPopularity() + "点人气值";
							}
						}
						reward.setRewardInfo(rewardInfo);
						reward.setProgress("进度"
								+ getProjectNumberProgress(
										bean.getTotalAmount(), bean.getId())
								+ "%");
						reward.setStatus(3);
					}
					// 根据项目ID查询 用户中奖个数
					modelResult.setRemark(bean.getId().toString());
					List<ProjectForLevel> projectForLevel = activityLotteryResultManager
							.getRewardLevelByProjectId(modelResult);
					reward.setProjectForLevel(projectForLevel);

					projectForReward.add(reward);
				}
			}
			model.setProjectForReward(projectForReward);

			result.setResult(model);
		} catch (ManagerException e) {
			logger.error("发现,获取直投有奖专题页异常", e);
		}

		return result;

	}
	@Override
	public Object getGoodsDetailInfromations(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try{
			Long memberId = paramBuilder.getMemberId();
			Long goodId = (Long) paramBuilder.getParamMap().get("goodId");
			Goods goods =  goodsManager.queryGoodsById(goodId);
			GoodsForAppDto goodsForApp = BeanCopyUtil.map(goods, GoodsForAppDto.class);
			List<BscAttachment> imageList = bscAttachmentManager
					.findAttachmentsByKeyIdAndModule(goods.getId().toString(),
							AttachmentEnum.ATTACHMENT_MODULE_SHOP_GOODS.getCode(),99);
			goodsForApp.setImageList(imageList);
			goodsForApp.setInvestFlag(transactionManager.isMemberInvested(memberId));
			Balance balance = balanceService.queryBalance(memberId,
					TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
			if (balance != null) {
				goodsForApp.setPopularity(balance.getBalance().intValue());
			} 
			MemberVip memberVip =memberVipManager.selectRecentMemberVipByMemberId(memberId);
			goodsForApp.setMemberVip(memberVip.getVipLevel());
			
			BigDecimal discountPrice = memberVipManager.getGoodValueByMemberVip(memberId, goods.getId());
			goodsForApp.setDiscountPrice(discountPrice);
			
			String des = goodsForApp.getGoodsDes();//\r 空格 \n换行
			des = des.replaceAll("\\r", "#");
			des = des.replaceAll("\\n", "&");
			goodsForApp.setGoodsDes(des);
			
			
			rDTO.setResult(goodsForApp);
		} catch (Exception e) {
			logger.error("【人气值商城】获取商品详细信息查询失败, paramBuilder={}", paramBuilder, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}
	
	@Override
	public Object creatGoodsOrder(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try{
			//前置校验
			if(!paramBuilder.getParamMap().containsKey("goodId")
					||!paramBuilder.getParamMap().containsKey("amount")){
				rDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDTO;
			}
			Long goodId = (Long) paramBuilder.getParamMap().get("goodId");
			Goods goods =  goodsManager.queryGoodsById(goodId);
			if(goods==null){
				rDTO.setResultCode(ResultCode.SHOP_GOOD_NOT_EXIST_ERROR);
				return rDTO;
			}
			//双节特惠商品进行活动判断
			if (goods.getGoodsType()==TypeEnum.GOODS_TYPE_DOUBLE.getType()){
				ResultDO<Activity> resultDO= activityLotteryManager.newPreferential();
				if (!resultDO.isSuccess()){
					rDTO.setResultCode(resultDO.getResultCode());
					return rDTO;
				}
			}
			
			//封装商品订单信息
			OrderForCreat orderForCreat = new OrderForCreat();
			OrderDelivery orderDelivery = new OrderDelivery();
			
			Long memberId = paramBuilder.getMemberId();
			Member member = memberManager.selectByPrimaryKey(memberId);
			orderForCreat.setGoodId(goodId);
			Integer amount =  (Integer) paramBuilder.getParamMap().get("amount");
			orderForCreat.setAmount(new BigDecimal(amount));
			orderForCreat.setMemberId(memberId);
			
			//创建订单前的校验
			ResultDO<Order> rDO = this.preCreateOrderValidate(orderForCreat);
			if (!rDO.isSuccess()) {
				rDTO.setResultCode(rDO.getResultCode());
				return rDTO;
			}
			
			if(member.getTrueName()!=null){
				orderForCreat.setReceiver(member.getTrueName());
			}else{
				orderForCreat.setReceiver("未实名");
			}
			orderForCreat.setMemberId(memberId);
			if(goods.getGoodsType()==TypeEnum.GOODS_TYPE_FOR_INVEST.getType()){
				//无特殊字段要求
			}
			if(goods.getGoodsType()==TypeEnum.GOODS_TYPE_VIRTUAL_CARD.getType()){
				//判断商品是否需要手机号 TODO
				if(goods.getRechargeType()==TypeEnum.GOODS_RECHARGE_TYPE_RECHARGE.getType()){
					if(!paramBuilder.getParamMap().containsKey("rechargeCard")){
						rDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
						return rDTO;
					}
					String rechargeCard = (String) paramBuilder.getParamMap().get("rechargeCard");
					orderForCreat.setRechargeCard(rechargeCard);
				}
			}
			if(goods.getGoodsType()==TypeEnum.GOODS_TYPE_PHYSICAL.getType()){
	
				if(!paramBuilder.getParamMap().containsKey("address")||
						//!paramBuilder.getParamMap().containsKey("areaFullName")||
						!paramBuilder.getParamMap().containsKey("receiver")||
						!paramBuilder.getParamMap().containsKey("mobile")){
					rDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
					return rDTO;
				}
				String provinceCode = (String) paramBuilder.getParamMap().get("province");
				String cityCode = (String) paramBuilder.getParamMap().get("city");
				String districtCode = (String) paramBuilder.getParamMap().get("district");
				String addressCode = (String) paramBuilder.getParamMap().get("address");
				
				String province = URLDecoder.decode(provinceCode, Constant.DEFAULT_CODE);
				String city = URLDecoder.decode(cityCode, Constant.DEFAULT_CODE);
				String district = URLDecoder.decode(districtCode, Constant.DEFAULT_CODE);
				String address = URLDecoder.decode(addressCode, Constant.DEFAULT_CODE);
				
				String receiverCode = (String) paramBuilder.getParamMap().get("receiver");
				//String areaFullName = (String) paramBuilder.getParamMap().get("areaFullName");
				String mobileCode = (String) paramBuilder.getParamMap().get("mobile");
				String receiver = URLDecoder.decode(receiverCode, Constant.DEFAULT_CODE);
				String mobile = URLDecoder.decode(mobileCode, Constant.DEFAULT_CODE);
				
				orderForCreat.setMobile(mobile);
				orderForCreat.setAddress(address);
				orderForCreat.setReceiver(receiver);
				orderForCreat.setAreaFullName(province + " " + city + " " + district);

				
				//orderForCreat.setAreaFullName(areaFullName);
				
				//收货地址表填充信息
				orderDelivery.setMemberId(memberId);
				orderDelivery.setReceiver(receiver);
				orderDelivery.setMobile(mobile);
				orderDelivery.setProvince(province);
				orderDelivery.setCity(city);
				orderDelivery.setDistrict(district);
				orderDelivery.setAddress(address);
			}
		
		
			orderForCreat.setGoodsCount(1);
			
			//生成地址信息
			try{
				OrderDelivery queryOrderDelivery = shopOrderManager.queryOrderDelivery(memberId);
				logger.debug("queryOrderDelivery",queryOrderDelivery);
				if(queryOrderDelivery != null){
				
					shopOrderManager.updateOrderDelivery(orderDelivery);
				}else{
					shopOrderManager.creatOrderDelivery(orderDelivery);
				}
			}catch(Exception e){
				logger.error("生成订单地址异常",e);
			}
			
			//生成订单
			ResultDO<Object> result = (ResultDO<Object>) shopOrderManager.creatGoodsOrder(orderForCreat);
			
			if(result.isSuccess()){
				rDTO.setIsSuccess();
			}else{
				rDTO.setResultCode(result.getResultCode());
				rDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("【人气值商城】生成商品订单失败, paramBuilder={}", paramBuilder, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}
	
	private ResultDO<Order> preCreateOrderValidate(OrderForCreat orderForCreat) throws ManagerException{
		ResultDO<Order> result = new ResultDO<Order>();

		Long memberId = orderForCreat.getMemberId();
		
		BigDecimal amount = orderForCreat.getAmount();
		BigDecimal discountPrice = memberVipManager.getGoodValueByMemberVip(memberId, orderForCreat.getGoodId());
		if(amount.intValue()!=discountPrice.intValue()){
			result.setResultCode(ResultCode.SHOP_GOOD_AMOUNT_ERROR);
    		return result;
		}
		Integer memberVip = memberVipManager.getMemberVip(memberId);
		Balance balance = balanceService.queryBalance(memberId,
				TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		if(balance.getBalance().intValue()<amount.intValue()){
			result.setResultCode(ResultCode.SHOP_GOOD_POPULARITY_NOT_ENOUGH_ERROR);
    		return result;
		}
		
		Goods goodsForLock = goodsManager.selectByPrimaryKeyLock(orderForCreat.getGoodId());
		
		if(goodsForLock.getSurplusInventor()<1){
    		result.setResultCode(ResultCode.SHOP_GOOD_NUM_NOT_ENOUGH_ERROR);
    		return result;
    	}
		
		if(goodsForLock.getStatus()!=2){
			result.setResultCode(ResultCode.SHOP_GOOD_NUM_NOT_SALE_ERROR);
    		return result;
		}
		
		if(!transactionManager.isMemberInvested(memberId)&&goodsForLock.getGoodsType()!=TypeEnum.GOODS_TYPE_FOR_INVEST.getType()){
			result.setResultCode(ResultCode.SHOP_GOOD_NOT_FOR_UNINVEST_MEMBER_ERROR);
    		return result;
		}
		if(goodsForLock.getLevelNeed()!=null){
			if(memberVip<goodsForLock.getLevelNeed()){
				result.setResultCode(ResultCode.SHOP_GOOD_AMOUNT_ERROR);
	    		return result;
			}
		}
		
		if(goodsForLock.getGoodsType()!=TypeEnum.GOODS_TYPE_FOR_INVEST.getType()){
			if(!transactionManager.isMemberInvested(memberId)){
				result.setResultCode(ResultCode.SHOP_GOOD_NOT_INVEST_ERROR);
	    		return result;
			}
		}
		
		
		return result;
		
	}
	
	
	
	@Override
	public Object purchaseHistory(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			Page<ShopOrderDetilDto> shopPage = new Page<ShopOrderDetilDto>();
			List<ShopOrderDetilDto> shopList = Lists.newArrayList();
			Long memberId = paramBuilder.getMemberId();
			ShopOrderForAppQuery query = new ShopOrderForAppQuery();
			query.setMemberId(memberId);
			
			Integer currentPage = 1 ;
			if(paramBuilder.getParamMap().containsKey("currentPage")){
				currentPage = (Integer) paramBuilder.getParamMap().get("currentPage");
			}
			query.setCurrentPage(currentPage);
			query.setPageSize(10);
			
			Page<OrderMain> orderMainPage = shopOrderManager.getOrderMainPage(query);
			List<OrderMain> orderMainList = orderMainPage.getData();
			for(OrderMain orM :orderMainList){
				ShopOrderDetilDto shOrD = new ShopOrderDetilDto();
				List<OrderSub> orderSubList = shopOrderManager.queryOrderSubListByOrderMainId(orM.getId());
				OrderSub orderSub = orderSubList.get(0);
				Goods goods = goodsManager.queryGoodsById(orderSub.getGoodsId());
				
				BscAttachment bsc = bscAttachmentManager
						.getBscAttachmentByKeyIdAndModule(goods.getId().toString(),
								AttachmentEnum.ATTACHMENT_MODULE_SHOP_GOODS.getCode());
				if(bsc!=null){
					shOrD.setImage(bsc.getFileUrl());
				}
				shOrD.setStatus(orM.getStatus());
				shOrD.setCreateTime(orM.getCreateTime());
				shOrD.setGoodsName(goods.getGoodsName());
				shOrD.setOrderId(orM.getId());
				
				shopList.add(shOrD);
			}
			shopPage.setData(shopList);
			shopPage.setPageNo(query.getCurrentPage());
			shopPage.setiDisplayLength(query.getPageSize());
			shopPage.setiTotalDisplayRecords(orderMainPage.getiTotalDisplayRecords());
			shopPage.setiTotalRecords(orderMainPage.getiTotalRecords());
			
			rDTO.setResult(shopPage);
		} catch (ManagerException e) {
			logger.error("【人气值商城】获取购买历史记录异常, paramBuilder={}", paramBuilder, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}


	@Override
	public Object getShopOrderDetail(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			ShopOrderDetilDto shopOrderDetail = new ShopOrderDetilDto();
			
			if(!paramBuilder.getParamMap().containsKey("orderMainId")){
				rDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDTO;
			}
			Long orderMainId = (Long) paramBuilder.getParamMap().get("orderMainId");
			OrderMain orM = shopOrderManager.selectByPrimaryKey(orderMainId);
			Long memberId = paramBuilder.getMemberId();
			if(orM.getMemberId().longValue()!=memberId.longValue()) {
				rDTO.setResultCode(ResultCode.ORDER_NOT_BELONG_TO_MEMBER_ERROR);
				return rDTO;
			}
			List<OrderSub> orderSubList = shopOrderManager.queryOrderSubListByOrderMainId(orM.getId());
			OrderSub orderSub = orderSubList.get(0);
			Goods goods = goodsManager.queryGoodsById(orderSub.getGoodsId());
			
			shopOrderDetail.setOrderId(orM.getId());
			shopOrderDetail.setAmount(orM.getTotalAmount().toString());
			shopOrderDetail.setStatus(orM.getStatus());
			
			String address = orM.getAddress();//\r 空格 \n换行
			if(StringUtil.isNotBlank(address)){
				address = address.replaceAll("\\r", "#");
				address = address.replaceAll("\\s*", "#"); 
				address = address.replaceAll(" +", "#");//中文空格
				address = address.replaceAll(" +", "#");//英文空格
				address = address.replaceAll(" +", "#");
				address = address.replaceAll("\\n", "&");
				address = address.replaceAll("\'", "^");
				address = address.replaceAll("\"", "\\$");
				address = address.replaceAll(":", "冒号");
			}
			shopOrderDetail.setAddress(address);
			
			shopOrderDetail.setReceiver(orM.getReceiver());
			shopOrderDetail.setMobile(orM.getMobile());

			String sendRemark = orM.getSendRemark();//\r 空格 \n换行
			if(goods.getGoodsType()==TypeEnum.GOODS_TYPE_VIRTUAL_CARD.getType()
					&&goods.getRechargeType()==TypeEnum.GOODS_RECHARGE_TYPE_RECHARGE.getType()){
				sendRemark = orM.getRemark();//充话费的备注如此处理显示
			}
			
			if(StringUtil.isNotBlank(sendRemark)){
				sendRemark = sendRemark.replaceAll("\\r", "#");
				sendRemark = sendRemark.replaceAll("\\n", "&");
			}
			shopOrderDetail.setSendRemark(sendRemark);
			
			shopOrderDetail.setGoodsName(goods.getGoodsName());
			shopOrderDetail.setOrderType(orderSub.getOrderType());
			shopOrderDetail.setCreateTime(orderSub.getCreateTime());
			shopOrderDetail.setRechargeType(orderSub.getRechargeType());
			shopOrderDetail.setRechargeCard(orderSub.getRechargeCard());
			
			List<BscAttachment> imageList = bscAttachmentManager
					.findAttachmentsByKeyIdAndModule(goods.getId().toString(),
							AttachmentEnum.ATTACHMENT_MODULE_SHOP_GOODS.getCode(),99);
			shopOrderDetail.setImageList(imageList);
			
			rDTO.setResult(shopOrderDetail);
		} catch (ManagerException e) {
			logger.error("【人气值商城】获取商城订单详情异常, paramBuilder={}", paramBuilder, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}
	
	
	@Override
	public Object queryMemberPopularityLogList(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			if(!paramBuilder.getParamMap().containsKey("pageNo")){
				rDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDTO;
			}
			Integer pageNo = 1 ;
			if(paramBuilder.getParamMap().containsKey("pageNo")){
				pageNo = (Integer) paramBuilder.getParamMap().get("pageNo");
			}
			Long memberId = paramBuilder.getMemberId();
			BaseQueryParam query = new BaseQueryParam();
			query.setMemberId(memberId);
			query.setPageSize(20);
			query.setCurrentPage(pageNo);
			Page<PopularityInOutLogDto> log = memberService.getPopularityInOutLog(query);
			
			rDTO.setResult(log);
		} catch (Exception e) {
			logger.error("【人气值商城】获取人气值流水异常, paramBuilder={}", paramBuilder, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}
	
	@Override
	public Object queryMemberVipList(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			if(!paramBuilder.getParamMap().containsKey("pageNo")){
				rDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDTO;
			}
			Integer pageNo = 1 ;
			if(paramBuilder.getParamMap().containsKey("pageNo")){
				pageNo = (Integer) paramBuilder.getParamMap().get("pageNo");
			}
			Long memberId = paramBuilder.getMemberId();
			BaseQueryParam query = new BaseQueryParam();
			query.setMemberId(memberId);
			query.setPageSize(20);
			query.setCurrentPage(pageNo);
			
			Page<MemberVip> memberVipPage = memberVipManager.queryMemberVipList(query);
			
			rDTO.setResult(memberVipPage);
		} catch (Exception e) {
			logger.error("【人气值商城】获取会员成长记录异常, paramBuilder={}", paramBuilder, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}
	
	@Override
	public Object memberBehavior(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		Long memberId = paramBuilder.getMemberId();
		
		String sourceId="";
		if(paramBuilder.getParamMap().containsKey("sourceId")){
			sourceId = (String) paramBuilder.getParamMap().get("sourceId");
		}
		String device="";
		if(paramBuilder.getParamMap().containsKey("device")){
			device = (String) paramBuilder.getParamMap().get("device");
		}
		String deviceParam="";
		if(paramBuilder.getParamMap().containsKey("deviceParam")){
			deviceParam = (String) paramBuilder.getParamMap().get("deviceParam");
		}
		String anchor="";
		if(paramBuilder.getParamMap().containsKey("anchor")){
			anchor = (String) paramBuilder.getParamMap().get("anchor");
		}
		String remarks="";
		if(paramBuilder.getParamMap().containsKey("remarks")){
			remarks = (String) paramBuilder.getParamMap().get("remarks");
		}
		
		memberBehaviorLogService.memberBehaviorLogInsert(memberId, sourceId,
				MemberLogEnum.MEMBER_BEHAVIOR_TYPE_APPH5_OPERATION.getType(),
				MemberLogEnum.MEMBER_BEHAVIOR_APPH5.getType(), device,
				deviceParam, anchor, remarks);
		rDTO.setResult(true);
		return rDTO;
	}
	
	/**
	 * 商品列表数据
	 */
	@Override
	public Object getGoodsList(DynamicParamBuilder paramBuilder) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			Long memberId = paramBuilder.getMemberId();
			
			Integer goodsType=TypeEnum.GOODS_TYPE_FOR_INVEST.getType();//默认
			if(paramBuilder.getParamMap().containsKey("goodsType")){
				goodsType = (Integer) paramBuilder.getParamMap().get("goodsType");
			}
			
			//全部
			GoodsQuery goodsQueryAll = new GoodsQuery();
			goodsQueryAll.setGoodsType(goodsType);

			List<Goods> goodListAll = goodsManager.queryGoodsList(goodsQueryAll);
			List<GoodsForAppDto> goodListAllDto = BeanCopyUtil.mapList(
					goodListAll, GoodsForAppDto.class);
			this.getMoreInforForGood(goodListAllDto,memberId);
			for(GoodsForAppDto gLA :goodListAllDto){//商品展示页，不需要描述信息，描述信息中的特殊符号会导致前段js解析异常
				gLA.setGoodsDes("");
			}

			result.setResult(goodListAllDto);
		} catch (ManagerException e) {
			logger.error("【人气值商城】获取商品异常, paramBuilder={}", paramBuilder, e);
		}
		return result;
	}
	
	@Override
	public List<Area> queryAreasByParentCode(Long id) {
		List<Area> areas = new ArrayList<Area>();
		try {
			areas = goodsAddrManager.queryAreasByParentCode(id);
		} catch (ManagerException e) {
			logger.error("【人气值商城】获取发货地址异常",  e);
		}
		return areas;
	}
	
	@Override
	public OrderDelivery queryOrderDelivery(Long memberId) {
		return shopOrderManager.queryOrderDelivery(memberId);
		
	}
}
