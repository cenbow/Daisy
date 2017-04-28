package com.yourong.core.mc.manager.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

import javax.annotation.Resource;

import com.yourong.common.util.*;
import com.yourong.core.mc.model.biz.*;
import com.yourong.core.uc.dao.MemberMapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.bsc.manager.AuditManager;
import com.yourong.core.bsc.model.Audit;
import com.yourong.core.cache.RedisForProjectClient;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.ic.dao.ProjectExtraMapper;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.draw.DrawByPopularityRedBagCreate;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.lottery.draw.DrawByProbability;
import com.yourong.core.lottery.manager.RedPackageManager;
import com.yourong.core.lottery.model.PopularityRedBag;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RewardsBodyForProbility;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.mc.dao.ActivityLotteryMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultNewMapper;
import com.yourong.core.mc.dao.ActivityMapper;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.manager.CouponTemplateRelationManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.ActivityLotteryResultNew;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.tc.dao.HostingCollectTradeMapper;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.biz.TransactionForProject;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

/**
 * 
 * @desc 交易后活动相关业务处理
 * @author wangyanji 2015年12月22日下午5:03:12
 */
@Component
public class ActivityAfterTransactionManagerImpl implements ActivityAfterTransactionManager {

	private static Logger logger = LoggerFactory.getLogger(ActivityAfterTransactionManagerImpl.class);

	@Autowired
	private CouponManager couponManager;

	@Autowired
	private DrawByProbability drawByProbability;

	@Autowired
	private DrawByPrizeDirectly drawByPrizeDirectly;

	@Autowired
	private DrawByPopularityRedBagCreate drawByPopularityRedBagCreate;

	@Autowired
	private ProjectExtraManager projectExtraManager;

	@Autowired
	private OrderManager orderManager;

	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private RedPackageManager redPackageManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private ActivityLotteryManager activityLotteryManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;
	
	@Autowired
	private CouponTemplateManager couponTemplateManager;
	
	
	@Autowired
	private HostingCollectTradeMapper hostingCollectTradeMapper;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired
	private TransactionMapper transactionMapper;
	
	@Autowired
	private ActivityLotteryMapper activityLotteryMapper;
	
	@Autowired
	private ProjectExtraMapper projectExtraMapper;
	
	@Autowired
	private ActivityMapper activityMapper;
	
	@Autowired
	private CouponTemplateRelationManager couponTemplateRelationManager;
	
	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;
	
	@Autowired
	private ActivityLotteryResultNewMapper activityLotteryResultNewMapper;
	
	@Autowired
	private AuditManager auditManager;

	@Autowired
	private MemberMapper memberMapper;

	@Override
	public void afterTransactionEntry(Transaction transaction) {
		// 异步业务
		taskExecutor.execute(new ProcessForActivityAfterTransactionThread(transaction));
		// 同步业务
		// 一样领头
		firstInvestInProject(transaction, ProjectEnum.PROJECT_TYPE_DEBT.getType());
		//五重礼
		fiveRites(transaction, ProjectEnum.PROJECT_TYPE_DEBT.getType());
	}

	@Override
	public void afterDirectProjectAuditEntry(Transaction transaction) {
		// 同步业务
		processForActivityAfterDirectProjectAuditThread(transaction);
	}

	/**
	 * 交易完成异步处理活动业务
	 * 
	 * @author Administrator
	 *
	 */
	private class ProcessForActivityAfterTransactionThread implements Runnable {
		private Transaction transaction;

		public ProcessForActivityAfterTransactionThread(final Transaction transaction) {
			this.transaction = transaction;
		}

		@Override
		public void run() {
			// 破20亿
			//break2Billion(transaction);
			//七月战队
			//julyTeam(transaction);
			//庆祝奥运
			//celebrateOlympic(transaction);
			//celebrationA(transaction);
			String key = RedisConstant.TRANSACTION_INVERESTLIST_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + transaction.getProjectId();
			RedisManager.removeObject(key);
			//玩转奥运
			//playOlympic(transaction);
			//国庆活动
			//octoberNational(transaction);
			
			wechatInvest(transaction);

			//庆国资战略入股有融网
			sasac(transaction);

			thirtyGift(transaction);
			
			//直投抽奖数据记录
			directProjectLottery(transaction);
			//周年庆
			//anniversary(transaction);
			
			
			//十月活动
			october(transaction);
			
			doubleActivity(transaction);

			//喜领红包,重置领取红包资格
			newyearLuckyMoney(transaction);

			//福袋使用再补发
			newyearLuckyBag(transaction);

			//38节送券
			//womensDay(transaction);
			
			//50亿活动投资送抽奖券
			fiveBillionLuck(transaction);
			
			//天降金喜
			dayDropGold(transaction);

			//邀请好友
			inviteFriend(transaction);

			//好春来活动
			//springActivity(transaction);

			//51活动
			laborInvest(transaction);
		}


	}

	/**
	 * 直投审核通过异步处理活动业务
	 * 
	 * @author Administrator
	 *
	 */
	private void processForActivityAfterDirectProjectAuditThread(Transaction transaction) {
		// 一样领头
		firstInvestInProject(transaction, ProjectEnum.PROJECT_TYPE_DIRECT.getType());
		//五重礼
		logger.info("直投送五重礼开始!");
		fiveRites(transaction, ProjectEnum.PROJECT_TYPE_DIRECT.getType());
		logger.info("直投送五重礼结束!");
		logger.info("直投活动相关业务处理结束! projectId={}", transaction.getProjectId());
		directSendPopularValue(transaction);
		
		sumTotalFirstInvestAmount(transaction);

		//投资返人气值
		newyearSendReward(transaction);

		//邀请好友赠送人气值
		inviteFriendSendPopularity(transaction);
	}


	private void springActivity(Transaction transaction){

		//转让项目不参与
		if(transaction.getProjectCategory()==2){
			return;
		}

		logger.info("【好春来活动】 活动处理开始 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId());
		//用券投资返券
		this.springActivityForTransaction(transaction);
		//当日首投返券
		this.springActivityForFirstInvest(transaction);
		//累计投资返券
		this.springActivityForTotalInvest(transaction);
		logger.info("【好春来活动】 活动处理结束 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId());
	}


	/**
	 * 好春来活动-1 用券投资返券
	 * @param transaction
	 */
	private void springActivityForTransaction(Transaction transaction) {
		logger.info("【好春来活动-1 用券投资返券】 活动处理开始 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId());

		Optional<Activity> springActivity = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.SPRING_ACTIVITY_TRANSACTION);
		if (!springActivity.isPresent()) {
			return;
		}
		Activity activity = springActivity.get();
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
			return;
		}

		ActivityForSpringCome model = LotteryContainer.getInstance().getObtainConditions(activity,
				ActivityForSpringCome.class, ActivityConstant.SPRING_ACTIVITY_TRANSACTION);

		//投资完成→是否活动期间→
		//是否使用优惠券→是否指定模板→是否用券投资时间＝券领用时间→送同模板的券
		Long templateId=null;
		try {
			Order order= orderManager.selectByPrimaryKey(transaction.getOrderId());
			Coupon coupon= couponManager.getCouponByCouponNo(order.getCashCouponNo());
			if (coupon!=null){
				String[] strings= model.getInvestTemplateIds().split(",");
				for (String s:strings) {
					if (!StringUtils.isEmpty(s)){
						if (s.equals(coupon.getCouponTemplateId().toString())){
							templateId=coupon.getCouponTemplateId();
							break;
						}
					}
				}
				//是否用券投资时间＝券领用时间
				if(DateUtils.daysBetween(coupon.getReceiveTime(),DateUtils.getCurrentDate())!=0){
					templateId=null;
				}

				//当日领取优惠券
				String cycleConstraint=transaction.getMemberId()+DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+activity.getId();
				ActivityLottery querymodel = new ActivityLottery();
				querymodel.setActivityId(activity.getId());
				querymodel.setMemberId(transaction.getMemberId());
				querymodel.setCycleConstraint(cycleConstraint);
				ActivityLottery activityLottery = activityLotteryManager.checkExistLottery(querymodel);
				if(activityLottery==null ||templateId==null|| !activityLottery.getRemark().equals(templateId.toString())){
					templateId = null;
				}
			}
		} catch (Exception e) {
			logger.error("【好春来活动-1 用券投资返券】优惠券校验异常， memberId={}, activityId={}, templateId={}", transaction.getMemberId(),
					activity.getId(), templateId,e);
		}

		if (templateId==null){
			return;
		}
		//送同模板的券
		try {
			// 收益券or现金券
			Coupon c = couponManager.receiveCoupon(transaction.getMemberId(), activity.getId(), templateId, -1L);
			if (c == null) {
				// 优惠券赠送失败;
				logger.error("【好春来活动-1 用券投资返券】优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}",
						transaction.getMemberId(),activity.getId(), templateId);
			}
		} catch (ManagerException e) {
			logger.error("【好春来活动-1 用券投资返券】优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}",
					transaction.getMemberId(),activity.getId(), templateId);
		}
	}

	/**
	 * 好春来活动-2 当日首投返券
	 * @param transaction
	 */
	private void springActivityForFirstInvest(Transaction transaction) {
		logger.info("【好春来活动-2 当日首投返券】 活动处理开始 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId());
		Optional<Activity> springActivity = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.SPRING_ACTIVITY_FIRST_INVEST);
		if (!springActivity.isPresent()) {
			return;
		}
		Activity activity = springActivity.get();
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
			return;
		}

		Long templateId=null;
		try {
			//是否当日首次投资→随机收益券
			int num = transactionMapper.queryMemberNormalTransactionCount(transaction.getMemberId(),
					DateUtils.zerolizedTime(DateUtils.getCurrentDate()), DateUtils.getEndTime(DateUtils.getCurrentDate()));
			if(num==1){
				templateId = Long.valueOf(this.drawLotteryByProbabilityUtil(activity.getObtainConditionsJson()));
			}
		} catch (Exception e) {
			logger.error("【好春来活动-2 当日首投返券】优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}",
					transaction.getMemberId(),activity.getId());
		}
		if (templateId==null){
			return;
		}
		//送同模板的券
		try {
			// 收益券or现金券
			Coupon c = couponManager.receiveCoupon(transaction.getMemberId(), activity.getId(), templateId, -1L);
			if (c == null) {
				// 优惠券赠送失败;
				logger.error("【好春来活动-2 当日首投返券】优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}",
						transaction.getMemberId(),activity.getId(), templateId);
			}
		} catch (ManagerException e) {
			logger.error("【好春来活动-2 当日首投返券】优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}",
					transaction.getMemberId(),activity.getId(), templateId);
		}
	}

	/**
	 * 概率抽奖
	 *
	 * @param jsonStr
	 *            json串包含奖品code,和奖品概率：{"rewardCode":"PopularityFor25",
	 *            "probability":0.05}
	 * @return
	 */
	private String drawLotteryByProbabilityUtil(String jsonStr) throws Exception {
		double randomNumber = Math.random();
		List<ActivityLotteryBiz> list = JSON.parseArray(jsonStr, ActivityLotteryBiz.class);
		double[] rewards = createProbability(list);
		for (int i = 0; i < rewards.length; i++) {
			if (randomNumber <= rewards[i]) {
				return list.get(i).getRewardCode();
			}
		}
		return null;
	}

	/**
	 * 生成奖品概率
	 *
	 * @param list
	 * @return
	 */
	private double[] createProbability(List<ActivityLotteryBiz> list) throws Exception {
		double[] rewards = new double[list.size()];
		double start = 0d;
		for (int i = 0; i < list.size(); i++) {
			start = FormulaUtil.doubleAdd(start, list.get(i).getProbability().doubleValue());
			rewards[i] = start;
		}
		if (start > 1d) {
			throw new IllegalArgumentException("概率设定不合法 num={" + start + "}，总概率>100%");
		}
		return rewards;
	}

	/**
	 * 好春来活动-3 累计投资返券
	 * @param transaction
	 */
	private void springActivityForTotalInvest(Transaction transaction) {
		logger.info("【好春来活动-3 累计投资返券】 活动处理开始 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId());

		Optional<Activity> springActivity = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.SPRING_ACTIVITY_TOTAL_INVEST);
		if (!springActivity.isPresent()) {
			return;
		}
		Activity activity = springActivity.get();
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
			return;
		}

		BigDecimal invest= transactionMapper.getMemberTotalInvestNoTransfer(transaction.getMemberId(),
				activity.getStartTime(),activity.getEndTime());


		ActivityForSpringComeTotal model = LotteryContainer.getInstance().getObtainConditions(activity,
				ActivityForSpringComeTotal.class, ActivityConstant.SPRING_ACTIVITY_TOTAL_INVEST);

		String amount = model.getTotalAmount();

		BigDecimal num = invest.divide(BigDecimal.valueOf(Integer.valueOf(amount)),10,BigDecimal.ROUND_DOWN);

		for(int i=1 ;i<=num.doubleValue();i++){

			String cycleConstraint=transaction.getMemberId()+"第"+i+"次"+activity.getId();
			ActivityLottery insertmodel = new ActivityLottery();
			insertmodel.setActivityId(activity.getId());
			insertmodel.setMemberId(transaction.getMemberId());
			insertmodel.setTotalCount(1);
			insertmodel.setRealCount(0);
			insertmodel.setCycleConstraint(cycleConstraint);
			Boolean flag = false;
			try {
				if(activityLotteryManager.checkExistLottery(insertmodel) == null) {
					activityLotteryManager.insertActivityLottery(insertmodel);
					flag = true;
				}
			} catch (Exception e) {
				logger.error("好春来活动-3 累计投资返券重复领取, memberId={}, cycleConstraint={}", transaction.getMemberId(), cycleConstraint);
				return;
			}
			if(flag){
				//发奖
				String[] strings= model.getRewardTemplateIds().split(",");
				for (String s:strings) {
					try {
						// 收益券or现金券
						Coupon c = couponManager.receiveCoupon(transaction.getMemberId(), activity.getId(), Long.valueOf(s), -1L);
						if (c == null) {
							// 优惠券赠送失败;
							logger.error("【好春来活动-3 累计投资返券】优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}",
									transaction.getMemberId(),activity.getId(), s);
						}
					} catch (ManagerException e) {
						logger.error("【好春来活动-3 累计投资返券】优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}",
								transaction.getMemberId(),activity.getId(), s);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @Description:直投抽奖奖励发放
	 * @param transaction
	 * @author: chaisen
	 * @time:2016年10月31日 下午5:23:57
	 */
	@Override
	public void directSendReward(Transaction transaction) {
		try {
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if(project==null){
				return;
			}
			if (project.getInvestType() == ProjectEnum.PROJECT_TYPE_DEBT.getType()) {
				return;
			}
			//校验奖励是否发放过
			if(!RedisActivityClient.directLotteryIsSend(transaction.getProjectId())){
				return;
			}
			ProjectExtra pe = projectExtraMapper.getProjectActivitySign(transaction.getProjectId());
			if(pe==null){
				return;
			}
			Activity activity=activityMapper.selectByPrimaryKey(pe.getActivityId());
			if(activity==null){
				return;
			}
			
			//募集时间超过奖励期限
			int totalDays=DateUtils.getIntervalDays(project.getOnlineTime(),project.getSaleComplatedTime())+1;
			List<PrizePool> prizePoolList =projectExtraManager.getPrizePoolByProjectId(transaction.getProjectId());
			Integer maxDay = 0;
			if(Collections3.isNotEmpty(prizePoolList)){
				for (PrizePool pri : prizePoolList) {
					if (Float.parseFloat(pri.getRatio())<= 0) {
						continue;
					}
					if (maxDay < pri.getDay()) {
						maxDay = pri.getDay();
					}
				}
				
				if(totalDays>maxDay){
					ActivityLotteryResult modelResult=new ActivityLotteryResult(); 
					modelResult.setActivityId(pe.getActivityId());
					modelResult.setRemark(project.getId().toString());
					modelResult.setRewardType(5);
					modelResult.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);
					List<ActivityLotteryResult> rewardInfoList=activityLotteryResultMapper.sumRewardInfoByMemberId(modelResult);
					if(Collections3.isEmpty(rewardInfoList)){
						return;
					}
					for(ActivityLotteryResult sendResult:rewardInfoList){
						//快投有奖 超过奖励期限 app站内信
						MessageClient.sendMsgForCommon(sendResult.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_QUICK_TIME_OUT.getCode(), 
								project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName());
					}
					return;
				}
			}
			
			//获取奖励系数
			String ratio="";
			Map map = JSON.parseObject(activity.getObtainConditionsJson()); 
			String prizePoolJson = map.get("prizePool").toString();
			List<PrizePool> prizePool = JSON.parseArray(prizePoolJson,PrizePool.class); 
			
			if(Collections3.isNotEmpty(prizePool)){
				for(PrizePool pool:prizePool){
					if(pool.getDay()==totalDays){
						ratio=pool.getRatio();
						break;
					}
				}
			}
			
			String prizeInPoolJson = map.get("prizeInPool").toString();
			List<PrizeInPool> prizeInPool =JSON.parseArray(prizeInPoolJson,PrizeInPool.class); 
			
			//各等级占比例
			Map<String, Object> paraMap = new HashMap<String, Object>();
			for(PrizeInPool pool:prizeInPool){
				paraMap.put(pool.getLevel()+"level", pool.getProportion().toString());
			}
			
			//各等级个数
			Map<String, Object> paraMapNumber = new HashMap<String, Object>();
			for(PrizeInPool pool:prizeInPool){
				paraMapNumber.put(pool.getLevel()+"level", pool.getNum());
			}
			//奖池金额
			BigDecimal ratioAmount=pe.getExtraAmount().multiply(new BigDecimal(ratio));
			
			ActivityLotteryResult modelResult=new ActivityLotteryResult(); 
			modelResult.setActivityId(pe.getActivityId());
			modelResult.setRemark(project.getId().toString());
			modelResult.setRewardType(5);
			modelResult.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);
			modelResult.setStatus(0);
			List<ActivityLotteryResult> listModelResult=activityLotteryResultMapper.getLotteryResultBySelective(modelResult);
			if(Collections3.isEmpty(listModelResult)){
				return;
			}
			//更新奖项对应的金额
			for(ActivityLotteryResult sendResult:listModelResult){
				sendResult.setRewardInfo(ratioAmount.multiply(new BigDecimal(paraMap.get(sendResult.getChip()+"level").toString())).intValue()+"");
				activityLotteryResultMapper.updateByPrimaryKeySelective(sendResult);
			}
			List<CouponTemplate> listTemplate=couponTemplateRelationManager.getDirectReward();
			if(Collections3.isEmpty(listTemplate)){
				logger.info(" 现金券模板id不存在, activityId={}, projectId={}, ", pe.getActivityId(),transaction.getProjectId());
				return;
			}
			List<ActivityLotteryResult> rewardInfoList=activityLotteryResultMapper.sumRewardInfoByMemberId(modelResult);
			if(Collections3.isEmpty(rewardInfoList)){
				return;
			}
			for(ActivityLotteryResult sendResult:rewardInfoList){
				totalCoupon(Integer.parseInt(sendResult.getRewardInfo()),sendResult,listTemplate);
				//发送短信
				String message = MessageFormat.format(ActivityConstant.DIRECT_QUICK_LOTTERY_MSG, project.getName());
				Member member = memberManager.selectByPrimaryKey(sendResult.getMemberId());
				if(member!=null){
					MessageClient.sendShortMessageByMobile(member.getMobile(), message);
				}
				
				//快投有奖 开奖  app站内信
				MessageClient.sendMsgForCommon(sendResult.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_QUICK_SUCCESS.getCode(), 
						project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName(),sendResult.getRewardInfo());
				
			}
			
		} catch (ManagerException e) {
			logger.error("直投抽奖,奖励发放失败, projectId={}, memberId={}", transaction.getProjectId(),
					transaction.getMemberId(), e);
		}
		
	}

	/**
	 * 
	 * @Description:根据奖金折算成现金券和人气值
	 * @param allReward
	 * @param couponAmount
	 * @param activityLotteryResult
	 * @param templateIds
	 * @author: chaisen
	 * @time:2016年11月2日 下午3:06:24
	 */
	private void totalCoupon(int allReward,ActivityLotteryResult activityLotteryResult,List<CouponTemplate> listTemplate) {
		for(CouponTemplate biz:listTemplate){

			if(allReward>=biz.getAmount().intValue()){
				int j=allReward/biz.getAmount().intValue();
				int jj=allReward%biz.getAmount().intValue();
				if(j>0){
					for(int n=0;n<j;n++){
						allReward=allReward-biz.getAmount().intValue();
						//发送现金券
						sendCoupon(activityLotteryResult,biz.getAmount().intValue(),listTemplate);
					}
				}
				if(jj<=0){
					break;
				}
			}
		
		}
		//发送人气值
		if(allReward>0){
			RuleBody rb = new RuleBody();
			rb.setActivityId(activityLotteryResult.getActivityId());
			rb.setMemberId(activityLotteryResult.getMemberId());
			rb.setDeductValue(allReward);
			rb.setRewardId(activityLotteryResult.getRemark());
			rb.setCycleStr(activityLotteryResult.getRemark());
			rb.setDeductRemark(ActivityConstant.DIRECT_QUICK_REWARD_SEND);
			rb.setActivityName("快投有奖");
			RewardsBase rBase = new RewardsBase();
			rBase.setRewardName(allReward + ActivityConstant.popularityDesc);
			rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
			rBase.setRewardValue(allReward);
			
			
			// 发放奖励
			try {
				drawByPrizeDirectly.drawLottery(rb, rBase, null);
				ActivityLotteryResult  upResult=new ActivityLotteryResult();
				upResult.setStatus(1);
				upResult.setMemberId(activityLotteryResult.getMemberId());
				upResult.setActivityId(activityLotteryResult.getActivityId());
				upResult.setRemark(activityLotteryResult.getRemark());
				upResult.setRewardResult(activityLotteryResult.getRewardResult());
				activityLotteryResultMapper.updateBatchLotteryResultRewardResultByActivityId(upResult);
			} catch (Exception e) {
				logger.error("直投抽奖，发送人气值失败, activityId={}, projectId={}, memberId={}, transactionId={}, popularity={}",  activityLotteryResult.getActivityId(), activityLotteryResult.getRemark(),
						activityLotteryResult.getMemberId(), activityLotteryResult.getRewardId(),allReward, e);
			}
		}
	}
	/**
	 * 
	 * @Description:发送现金券
	 * @param activityLotteryResult
	 * @param couponAmount
	 * @param templateIds
	 * @author: chaisen
	 * @time:2016年11月2日 下午3:02:58
	 */
	private void sendCoupon(ActivityLotteryResult activityLotteryResult,int couponAmount,List<CouponTemplate> listTemplate){
		Long templateId=0L;
		for(int i=0;i<listTemplate.size();i++){
			if(couponAmount==listTemplate.get(i).getAmount().intValue()){
				templateId=listTemplate.get(i).getId();
				break;
			}
		}
		RuleBody rb = new RuleBody();
		
		rb.setDeductValue(couponAmount);
		rb.setActivityId(activityLotteryResult.getActivityId());
		rb.setMemberId(activityLotteryResult.getMemberId());
		rb.setCycleStr(activityLotteryResult.getRemark());
		rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
		rb.setRewardId(activityLotteryResult.getRemark());
		rb.setDeductRemark(ActivityConstant.DIRECT_QUICK_REWARD_SEND);
		RewardsBase rBase = new RewardsBase();
		rBase.setTemplateId(templateId);
		rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
		try {
			CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
			if(couponTemplate==null){
				logger.info(" 现金券模板id不存在, activityId={}, memberId={}, templateId={}, ", activityLotteryResult.getActivityId(), activityLotteryResult.getMemberId(),templateId);
				return;
			}
			rBase.setRewardName(couponTemplate.getAmount().intValue() +ActivityConstant.couponDesc);
			rBase.setRewardValue(couponTemplate.getAmount().intValue());
			drawByPrizeDirectly.drawLottery(rb, rBase, null);
			ActivityLotteryResult  upResult=new ActivityLotteryResult();
			upResult.setStatus(1);
			upResult.setMemberId(activityLotteryResult.getMemberId());
			upResult.setActivityId(activityLotteryResult.getActivityId());
			upResult.setRemark(activityLotteryResult.getRemark());
			//upResult.setRewardId(activityLotteryResult.getRewardId());
			upResult.setRewardResult(activityLotteryResult.getRewardResult());
			activityLotteryResultMapper.updateBatchLotteryResultRewardResultByActivityId(upResult);
		} catch (Exception e) {
			logger.error("直投抽奖，发送现金券失败, activityId={}, projectId={}, memberId={}, transactionId={}",  activityLotteryResult.getActivityId(), activityLotteryResult.getRemark(),
					activityLotteryResult.getMemberId(), activityLotteryResult.getRewardId(), e);
		}
	}
	/**
	 * 
	 * @Description:春节活动入口
	 * @param member
	 * @param project
	 * @param transaction
	 * @param activity
	 * @author: wangyanji
	 * @time:2016年1月27日 下午2:31:54
	 */
	private void springEntry(Transaction transaction, Activity activity) {
		// 辞旧迎新兑现返人气
		springRechargePopularity(transaction, transaction.getMemberId(), activity);
		// 如意项目返利
		springWishesProjectPrize(activity, transaction.getProjectId(), transaction);
		// 春节人气值红包预处理
		springRedBag(activity, transaction.getProjectId(), transaction);
	}

	/**
	 * 
	 * @Description:辞旧迎新兑现返人气
	 * @param transacvitonId
	 * @author: wangyanji
	 * @time:2015年12月28日 下午1:19:36
	 */
	private void springRechargePopularity(Transaction transaction, Long memberId, Activity act) {
		try {
			Long[] templateIds = new Long[] { Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate50")),
					Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate100")),
					Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate500")),
					Long.parseLong(PropertiesUtil.getProperties("business.exchangeRevisionCouponTemplate1000")) };
			Coupon coupon = couponManager.checkTransactionCouponFromPopularity(transaction.getOrderId(), templateIds, act.getStartTime(),
					act.getEndTime());
			if (coupon != null) {
				// 奖励10%的辞旧迎新返利
				BigDecimal precent = new BigDecimal(0.1);
				int prizeValue = coupon.getAmount().multiply(precent).intValue();
				RuleBody rb = new RuleBody();
				rb.setActivityId(act.getId());
				rb.setMemberId(memberId);
				rb.setCycleStr(transaction.getId().toString());
				rb.setDeductValue(coupon.getAmount().intValue());
				rb.setActivityName(Constant.SPRING_REBATE);
				rb.setDeductRemark(Constant.SPRING_REBATE);
				RewardsBase rBase = new RewardsBase();
				rBase.setRewardName(prizeValue + ActivityConstant.popularityDesc);
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
				rBase.setRewardValue(prizeValue);
				// 发放奖励
				drawByPrizeDirectly.drawLottery(rb, rBase, null);
				MessageClient.sendMsgForSPEngin(memberId, Constant.SPRING_REBATE, prizeValue + ActivityConstant.popularityDesc);
				// 清楚redis缓存
				String key = RedisConstant.REDIS_KEY_ACTIVITY_SPRING_REBATELIST;
				RedisManager.removeObject(key);
				if (coupon.getStatus() != StatusEnum.COUPON_STATUS_USED.getStatus()) {
					logger.info("辞旧迎新兑现返人气, 现金券状态transactionId={}, couponNo={}, couponStatus={}", transaction.getId(),
							coupon.getCouponCode(), coupon.getStatus());
				}
			}
		} catch (Exception e) {
			logger.error("辞旧迎新兑现返人气失败, transacvitonId={}", transaction.getId(), e);
		}
	}

	/**
	 * 
	 * @Description:春节如意项目返利
	 * @param projectId
	 * @param memberId
	 * @param investAmount
	 * @author: wangyanji
	 * @time:2016年1月1日 下午4:13:09
	 */
	private void springWishesProjectPrize(Activity activity, Long projectId, Transaction transaction) {
		try {
			Integer projectSign = projectExtraManager.getProjectActivitySign(projectId);
			if (projectSign == TypeEnum.PROJECT_ACTIVITY_SIGN.getType()) {
				// 获取活动规则
				ActivityForSpringFestival spring = LotteryContainer.getInstance().getObtainConditions(activity,
						ActivityForSpringFestival.class, RedisConstant.REDIS_KEY_ACTIVITY_REBATE);
				BigDecimal wishesProInvest = new BigDecimal(spring.getWishesProInvest());
				Integer prizeValue = null;
				if (transaction.getInvestAmount().compareTo(wishesProInvest) == -1) {
					// 单笔金额＜10000元的投资，可获得投资额1‰的人气值奖励
					prizeValue = transaction.getInvestAmount().multiply(spring.getWishesProLevel1()).intValue();
				} else {
					// 单笔金额≥10000元的投资，可获得投资额2‰的人气值奖励
					prizeValue = transaction.getInvestAmount().multiply(spring.getWishesProLevel2()).intValue();
				}
				if (prizeValue > 0) {
					RuleBody rb = new RuleBody();
					rb.setActivityId(activity.getId());
					rb.setMemberId(transaction.getMemberId());
					rb.setCycleStr(transaction.getId().toString());
					rb.setDeductValue(transaction.getInvestAmount().intValue());
					rb.setActivityName(Constant.SPRING_WISHES);
					rb.setDeductRemark(Constant.SPRING_WISHES);
					rb.setRewardId(projectId.toString());
					RewardsBase rBase = new RewardsBase();
					rBase.setRewardName(prizeValue + ActivityConstant.popularityDesc);
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					rBase.setRewardValue(prizeValue);
					// 发放奖励
					drawByPrizeDirectly.drawLottery(rb, rBase, null);
					MessageClient.sendMsgForSPEngin(transaction.getMemberId(), Constant.SPRING_WISHES, prizeValue
							+ ActivityConstant.popularityDesc);
					// 清楚redis缓存
					String key = RedisConstant.REDIS_KEY_ACTIVITY_SPRING_WISHESPROLIST;
					RedisManager.removeObject(key);
				}
			}
		} catch (Exception e) {
			logger.error("春节如意项目返利失败, activityId={}, projectId={}, memberId={}, transactionId={}", activity.getId(), projectId,
					transaction.getMemberId(), transaction.getId(), e);
		}
	}

	/**
	 * 
	 * @Description:春节人气值红包预处理
	 * @param member
	 * @param project
	 * @param transaction
	 * @author: wangyanji
	 * @time:2016年1月7日 下午7:50:50
	 */
	private void springRedBag(Activity activity, Long projectId, Transaction transaction) {
		try {
			// 获取活动规则
			PopularityRedBag redBag = LotteryContainer.getInstance().getObtainConditions(activity, PopularityRedBag.class,
					RedisConstant.REDIS_KEY_ACTIVITY_REDBAG);
			redBag.setProjectId(projectId);
			redBag.setSourceId(transaction.getId());
			redBag.setRedBagAmount(transaction.getInvestAmount());
			RuleBody ruleBody = new RuleBody();
			ruleBody.setActivityId(activity.getId());
			ruleBody.setVerificationObj(redBag);
			// 人气值红包校验
			if (drawByPopularityRedBagCreate.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITYREDBAG.getCode())) {
				// 红包预处理
				drawByPopularityRedBagCreate.drawLottery(ruleBody, transaction.getInvestAmount(),
						TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITYREDBAG.getCode());
			}
		} catch (Exception e) {
			logger.error("春节人气值红包预处理失败, activityId={}, projectId={}, memberId={}, transactionId={}", activity.getId(), projectId,
					transaction.getInvestAmount(), e);
		}
	}

	/**
	 * 
	 * @Description:移动端投资送券活动入口
	 * @param member
	 * @param project
	 * @param transaction
	 * @param activity
	 * @author: wangyanji
	 * @time:2016年1月27日 下午2:31:54
	 */
	private void mobileTransSendCoupon(Transaction transaction, Activity activity) {
		try {
			Order order = orderManager.selectByPrimaryKey(transaction.getOrderId());
			int source = order.getOrderSource() != null ? order.getOrderSource() : 0;
			if (source > TypeEnum.ORDER_SOURCE_PC.getType()
					&& transaction.getInvestAmount().compareTo(ActivityConstant.ACTIVITY_MOBILE_INVEST_LIMIT) > -1) {
				// 送券
				Long templateId = 125l;
				String result = "50";
				String rewardInfo = result + ActivityConstant.couponDesc;
				// 送券
				RuleBody ruleBody = new RuleBody();
				ruleBody.setActivityId(activity.getId());
				ruleBody.setMemberId(transaction.getMemberId());
				ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				ruleBody.setCycleStr(activity.getId().toString());
				ruleBody.setDeductRemark(ActivityConstant.ACTIVITY_MOBILE_INVEST_NAME);
				ruleBody.setRewardId(transaction.getId().toString());
				ruleBody.setDeductValue(transaction.getInvestAmount().intValue());
				RewardsBase rBase = new RewardsBase();
				rBase.setRewardCode("xianJinQuan50");
				rBase.setRewardName(rewardInfo);
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setTemplateId(templateId);
				rBase.setRewardValue(Integer.valueOf(result));
				if (drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					drawByPrizeDirectly.drawLottery(ruleBody, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
					// 发送站内信
					MessageClient.sendMsgForSPEngin(transaction.getMemberId(), ActivityConstant.ACTIVITY_MOBILE_INVEST_NAME, rewardInfo);
				}
			}
		} catch (Exception e) {
			boolean hasReceive = false;
			if (e.getClass().equals(ManagerException.class)) {
				String errorMsg = e.getMessage();
				if (errorMsg.startsWith("重复参加活动")) {
					hasReceive = true;
				}
			}
			if (hasReceive) {
				logger.error("移动端投资送券活动处理失败, transactionId={}, activityId={}", transaction.getId(), activity.getId());
			} else {
				logger.error("移动端投资送券活动处理失败, transactionId={}, activityId={}", transaction.getId(), activity.getId(), e);
			}

		}
	}

	/**
	 * 
	 * @Description:破十亿庆祝活动
	 * @param memberId
	 * @param transactionId
	 * @param investAmount
	 * @author: wangyanji
	 * @time:2016年2月19日 上午9:26:57
	 */
	private void breakBillion(Activity activity, Transaction transaction) {
		try {
			// 余额是否大于0
			Long value = RedisActivityClient.getActivityCouponAmountLimit(activity.getId(),
					ActivityConstant.ACTIVITY_BREAK_BILLION_COUPONAMOUNT_LIMIT);
			if (value.longValue() > 0l) {
				// 领券
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(transaction.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setRewardId(transaction.getId().toString());
				rb.setDeductValue(transaction.getInvestAmount().intValue());
				rb.setActivityName(ActivityConstant.ACTIVITY_BREAK_BILLION_NAME);
				rb.setExceptDrawedRewards(false);
				rb.setProbabilityByAverage(false);
				rb.setRewardsPoolMaxNum(3);
				rb.setRewardsAvailableNum(3);
				// 抽奖
				RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null, null);
				if (!"noReward".equals(rfp.getRewardCode())) {
					// 扣除现金券余额
					BigDecimal decrValue = new BigDecimal(rfp.getRewardValue());
					RedisActivityClient.decrActivityCouponAmountLimit(activity.getId(), decrValue);
					// 发送站内信
					MessageClient.sendMsgForSPEngin(transaction.getMemberId(), rb.getActivityName(), rfp.getRewardName());
				}
			}
		} catch (Exception e) {
			logger.error("破十亿庆祝活动送券失败, memberId={}, transactionId={}", transaction.getMemberId(), transaction.getId(), e);
		}
	}

	/**
	 * 
	 * @Description:投房有礼入口
	 * @param transaction
	 * @author: wangyanji
	 * @time:2016年4月5日 上午10:12:47
	 */
	private void house2Gifts(Transaction transaction) {
		try {
			Optional<Activity> optOfGift1 = LotteryContainer.getInstance().getActivityByName(ActivityConstant.ACTIVITY_HOUSE1GIFT_NAME);
			Optional<Activity> optOfGift2 = LotteryContainer.getInstance().getActivityByName(ActivityConstant.ACTIVITY_HOUSE2GIFT_NAME);
			if (!optOfGift1.isPresent() || !optOfGift2.isPresent()) {
				logger.error("投房有礼未找到活动数据！");
				return;
			}
			Activity gift1 = optOfGift1.get();
			Activity gift2 = optOfGift2.get();
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != gift1.getActivityStatus()
					|| StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != gift2.getActivityStatus()) {
				// 活动外开始或者已结束
				return;
			}
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if (!DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSE.getCode().equals(project.getProjectType())
					&& !DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode().equals(project.getProjectType())) {
				// 不是房有融
				return;
			}
			Long gift1Value = RedisActivityClient.getActivityCouponAmountLimit(gift1.getId(), gift1.getObtainConditionsJson());
			if (gift1Value > 0L) {
				// 一重来送券
				house2GiftsMain(transaction, gift1);
				return;
			}
			Long gift2Value = RedisActivityClient.getActivityCouponAmountLimit(gift2.getId(), gift2.getObtainConditionsJson());
			if (gift2Value > 0L) {
				// 二重来送券
				house2GiftsMain(transaction, gift2);
				return;
			}
		} catch (Exception e) {
			logger.error("投房有礼活动送券失败, memberId={}, transactionId={}", transaction.getMemberId(), transaction.getId(), e);
		}
	}

	/**
	 * 
	 * @Description:投房有礼
	 * @param transaction
	 * @param optOfGift1
	 * @param optOfGift2
	 * @author: wangyanji
	 * @throws Exception
	 * @time:2016年4月5日 上午10:26:01
	 */
	private void house2GiftsMain(Transaction transaction, Activity gift) throws Exception {
		// 领券
		RuleBody rb = new RuleBody();
		rb.setActivityId(gift.getId());
		rb.setMemberId(transaction.getMemberId());
		rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
		rb.setRewardId(transaction.getId().toString());
		rb.setDeductValue(transaction.getInvestAmount().intValue());
		rb.setActivityName(gift.getActivityName());
		rb.setExceptDrawedRewards(false);
		rb.setProbabilityByAverage(false);
		rb.setRewardsPoolMaxNum(3);
		rb.setRewardsAvailableNum(3);
		// 抽奖
		RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null, null);
		if (!"noReward".equals(rfp.getRewardCode())) {
			// 扣除现金券余额
			BigDecimal decrValue = new BigDecimal(rfp.getRewardValue());
			RedisActivityClient.decrActivityCouponAmountLimit(gift.getId(), decrValue);
			// 发送站内信
			MessageClient.sendMsgForSPEngin(transaction.getMemberId(), rb.getActivityName(), rfp.getRewardName());
		}
	}

	/**
	 * 
	 * @Description:微信周末活动
	 * @param transaction
	 * @author: wangyanji
	 * @time:2016年4月10日 下午2:50:07
	 */
	private void wechatInvest(Transaction transaction) {
		try {
			Optional<Activity> optOfWechatInvest = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.ACTIVITY_WECHAT_INVEST_NAME);
			if (!optOfWechatInvest.isPresent()) {
				logger.error("微信周末活动不符合",transaction.getId());
				return;
			}
			Activity wechatInvest = optOfWechatInvest.get();
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != wechatInvest.getActivityStatus()) {
				logger.error("微信周末活动不符合",transaction.getId());
				return;
			}
			// 周末才能参加
			if (!DateUtils.isWeekend(DateUtils.getCurrentDate())) {
				logger.error("微信周末活动不符合",transaction.getId());
				return;
			}
			//转让项目不参与五重礼
			if(transaction.getProjectCategory()==2){
				logger.error("微信周末活动转让项目不参与五重礼",transaction.getId());
				return;
			}
			// 订单来源于M站
			Order order = orderManager.selectByPrimaryKey(transaction.getOrderId());
			if (TypeEnum.ORDER_SOURCE_WAP.getType() != order.getOrderSource()) {
				logger.error("微信周末活动转让项目不参与五重礼",transaction.getId());
				return;
			}
			Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());
			ActivityForMillionCoupon model = LotteryContainer.getInstance().getObtainConditions(wechatInvest,
					ActivityForMillionCoupon.class, ActivityConstant.ACTIVITY_WECHAT_INVEST);
			BigDecimal investLevel = model.getMaxInvestAmount();
			// 判断是否是用户第一次投资
			if (checkNewMember(member, transaction, wechatInvest)) {
				investLevel = model.getMinInvestAmount();
			}
			// 判断投资金额
			if (transaction.getInvestAmount().compareTo(investLevel) == -1) {
				logger.error("微信周末活动投资金额不符合",transaction.getId());
				return;
			}
			String dateStr = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3);
			// 领券
			RuleBody rb = new RuleBody();
			rb.setActivityId(wechatInvest.getId());
			rb.setCycleStr(dateStr);
			rb.setMemberId(transaction.getMemberId());
			rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			rb.setRewardId(transaction.getId().toString());
			rb.setDeductValue(transaction.getInvestAmount().intValue());
			rb.setActivityName(wechatInvest.getActivityName());
			rb.setExceptDrawedRewards(false);
			rb.setProbabilityByAverage(false);
			rb.setRewardsPoolMaxNum(2);
			rb.setRewardsAvailableNum(2);
			// 抽奖
			if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null,
						TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
				if (!"noReward".equals(rfp.getRewardCode())) {
					//领收益券
					RuleBody rb1 = new RuleBody();
					RewardsBase rBase = new RewardsBase();
					rb1.setMemberId(transaction.getMemberId());
					rb1.setActivityId(wechatInvest.getId());
					rb1.setActivityName(wechatInvest.getActivityDesc());
					rb1.setRewardId(model.getTemplateId88().toString());
					CouponTemplate c = couponTemplateManager.selectByPrimaryKey(model.getTemplateId88());
					if(c!=null){
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
						rBase.setRewardName(c.getAmount().intValue() +ActivityConstant.annualizedUnit+ActivityConstant.annualizedDesc);
						rBase.setRewardValue(c.getAmount().intValue());
						rBase.setTemplateId(model.getTemplateId88());
						drawByPrizeDirectly.drawLottery(rb1, rBase, "");
					}
					// 发送短信
					String message = MessageFormat.format(ActivityConstant.ACTIVITY_WECHAT_INVEST_MSG, rfp.getRewardName(),rBase.getRewardName());
					MessageClient.sendShortMessageByMobile(member.getMobile(), message);
					// 发送站内信
					MessageClient.sendMsgForSPEngin(transaction.getMemberId(), rb.getActivityName(), rfp.getRewardName()+","+rBase.getRewardName());
				}
			}
		} catch (Exception e) {
			logger.error("微信周末活动活动失败 transactionId={}", transaction.getId(), e);
		}
	}

	private boolean checkNewMember(Member member, Transaction transaction, Activity wechatInvest) throws ManagerException {
		// 判断注册时间在活动期间且是周末
		if (!DateUtils.isDateBetween(member.getRegisterTime(), wechatInvest.getStartTime(), wechatInvest.getEndTime())
				|| !DateUtils.isWeekend(member.getRegisterTime())) {
			return false;
		}
		// 判断是否是用户第一次投资
		Transaction firstTransaction = transactionManager.getFirstTransaction(transaction.getMemberId());
		if (firstTransaction == null || !transaction.getId().equals(firstTransaction.getId())) {
			return false;
		}
		return true;
	}

	/**
	 * 庆国资战略入股有融网活动
	 * @param transaction
	 */
	private void sasac(Transaction transaction){
		try {
			Optional<Activity> optOfWechatInvest = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.ACTIVITY_SASAC_INVEST_NAME);
			if (!optOfWechatInvest.isPresent()) {
				logger.error("庆国资战略入股有融网",transaction.getId());
				return;
			}
			Activity wechatInvest = optOfWechatInvest.get();
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != wechatInvest.getActivityStatus()) {
				logger.error("庆国资战略入股有融网活动不符合",transaction.getId());
				return;
			}
			//转让项目不参与五重礼
			if(transaction.getProjectCategory()==2){
				logger.error("庆国资战略入股有融网活动转让项目不参与五重礼",transaction.getId());
				return;
			}
			Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());
			ActivityForMillionCoupon model = LotteryContainer.getInstance().getObtainConditions(wechatInvest,
					ActivityForMillionCoupon.class, ActivityConstant.ACTIVITY_WECHAT_INVEST);

			// 判断是否是新用户第一次投资
			if (checkNewMemberFirst(member, transaction, wechatInvest)) {
				if (transaction.getInvestAmount().compareTo(model.getMinInvestAmount()) == -1) {
					logger.error("庆国资战略入股有融网活动投资金额不符合",transaction.getId());
					return;
				}
			}else {
				// 非新用户第一次投资判断投资金额
				if (transaction.getInvestAmount().compareTo(model.getMaxInvestAmount()) == -1) {
					logger.error("庆国资战略入股有融网活动投资金额不符合",transaction.getId());
					return;
				}
			}

			//领收益券
			RuleBody rb = new RuleBody();
			RewardsBase rBase = new RewardsBase();
			rb.setMemberId(transaction.getMemberId());
			rb.setActivityId(wechatInvest.getId());
			rb.setActivityName(wechatInvest.getActivityDesc());
			rb.setRewardId(model.getTemplateId88().toString());
			CouponTemplate c = couponTemplateManager.selectByPrimaryKey(model.getTemplateId88());
			if(c!=null){
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
				rBase.setRewardName(c.getAmount().intValue() +ActivityConstant.couponDesc);
				rBase.setRewardValue(c.getAmount().intValue());
				rBase.setTemplateId(model.getTemplateId88());

			}
			drawByPrizeDirectly.drawLottery(rb, rBase, "");
		} catch (Exception e) {
			logger.error("庆国资战略入股有融网活动活动失败 transactionId={}", transaction.getId(), e);
		}
	}

	/**
	 * 是否是新注册用户投资
	 * @param member
	 * @param transaction
	 * @param wechatInvest
	 * @return
	 * @throws ManagerException
     */
	private boolean checkNewMemberFirst(Member member, Transaction transaction, Activity wechatInvest) throws ManagerException {
		// 判断注册时间在活动期间且是周末
		if (DateUtils.isDateBetween(member.getRegisterTime(), wechatInvest.getStartTime(), wechatInvest.getEndTime())) {
			// 判断是否是用户第一次投资
			Transaction firstTransaction = transactionManager.getFirstTransaction(transaction.getMemberId());
			if (firstTransaction != null && transaction.getId().equals(firstTransaction.getId())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @Description:520活动
	 * @param transaction
	 * @author: wangyanji
	 * @time:2016年5月4日 下午2:40:17
	 */
	private void fellInLove(Transaction transaction) {
		try {
			Optional<Activity> optOfActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FELL_IN_LOVE_NAME);
			if (!optOfActivity.isPresent()) {
				return;
			}
			Activity activity = optOfActivity.get();
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
				return;
			}
			ActivityFor520 rule = LotteryContainer.getInstance().getObtainConditions(activity, ActivityFor520.class,
					ActivityConstant.ACTIVITY_FELL_IN_LOVE_NAME);
			// 活动一
			fellInLovePart1(activity, transaction, rule);
			// 活动二
			fellInLovePart2(activity, transaction, rule);
		} catch (Exception e) {
			logger.error("520活动处理失败, transacvitonId={}", transaction.getId(), e);
		}
	}

	/**
	 * 
	 * @Description:520活动1
	 * @param activity
	 * @param transaction
	 * @param rule
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年5月4日 下午3:14:02
	 */
	private void fellInLovePart1(Activity activity, Transaction transaction, ActivityFor520 rule) throws Exception {
		if (rule.getMinInvestAmount().compareTo(transaction.getInvestAmount()) == 1) {
			return;
		}
		Date nowDate = DateUtils.getCurrentDate();
		String cycleStr = DateUtils.getDateStrFromDate(nowDate);
		String startTimeStr = cycleStr + " " + rule.getReceiveStartTimeStr();
		Date receiveStartTime = DateUtils.getDateFromString(startTimeStr, DateUtils.TIME_PATTERN);
		if (nowDate.before(receiveStartTime)) {
			return;
		}
		String key = activity.getId().toString() + DateUtils.getYear(nowDate) + DateUtils.getMonth(nowDate) + DateUtils.getDate(nowDate);
		// 余额是否大于0
		Long value = RedisActivityClient.getActivityCouponAmountLimit(Long.valueOf(key), rule.getCouponNumber().toString());
		if (value.longValue() > 0l) {
			// 领券
			RuleBody rb = new RuleBody();
			rb.setActivityId(activity.getId());
			rb.setMemberId(transaction.getMemberId());
			rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			rb.setRewardId(transaction.getId().toString());
			rb.setDeductValue(transaction.getInvestAmount().intValue());
			rb.setCycleStr(transaction.getId().toString());
			rb.setActivityName(activity.getActivityName());
			rb.setExceptDrawedRewards(false);
			rb.setProbabilityByAverage(false);
			rb.setRewardsPoolMaxNum(2);
			rb.setRewardsAvailableNum(2);
			// 抽奖
			if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null,
						TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
				if (!"noReward".equals(rfp.getRewardCode())) {
					// 扣除现金券余量
					RedisActivityClient.decrActivityCouponAmountLimit(Long.valueOf(key), new BigDecimal(1));
					// 发送站内信
					MessageClient.sendMsgForSPEngin(transaction.getMemberId(), rb.getActivityName(), rfp.getRewardName());
				}
			}
		}
	}

	/**
	 * 
	 * @Description:520活动2
	 * @param activity
	 * @param transaction
	 * @param rule
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年5月4日 下午3:14:12
	 */
	public void fellInLovePart2(Activity activity, Transaction transaction, ActivityFor520 rule) throws Exception {
		BigDecimal giftAmount = rule.getReceiveGiftPacksAmount();
		// 用户总投资
		BigDecimal totalAmount = getTotalAmountByMemberId(transaction.getMemberId(), activity.getStartTime(), activity.getEndTime());
		if (giftAmount.compareTo(totalAmount) == 1) {
			return;
		}
		BigDecimal num = totalAmount.divide(giftAmount, 0, BigDecimal.ROUND_DOWN);
		if (num.intValue() < 1) {
			return;
		}
		int sendNum = 0;
		for (int i = num.intValue(); i > 0; i--) {
			boolean returnFlag = sendGiftPacks(activity, transaction, rule, giftAmount, new BigDecimal(i));
			if (!returnFlag) {
				break;
			} else {
				sendNum++;
			}
		}
		if (sendNum > 0) {
			// 发送站内信
			MessageClient.sendMsgForSPEngin(transaction.getMemberId(), activity.getActivityName(), rule.getWebMsg());
			// 发送短信
			Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());
			MessageClient.sendShortMessageByMobile(member.getMobile(), rule.getShortMsg());
		}
	}

	private boolean sendGiftPacks(Activity activity, Transaction transaction, ActivityFor520 rule, BigDecimal giftAmount, BigDecimal num)
			throws ManagerException {
		String cycleConstraint = activity.getId() + "-" + giftAmount.multiply(num).intValue();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("activityId", activity.getId());
		paraMap.put("memberId", transaction.getMemberId());
		paraMap.put("cycleConstraint", cycleConstraint);
		ActivityLottery al = activityLotteryManager.selectByMemberActivity(paraMap);
		if (al != null) {
			return false;
		}
		// 送礼包
		ActivityLottery model = new ActivityLottery();
		model.setActivityId(activity.getId());
		model.setMemberId(transaction.getMemberId());
		model.setCycleConstraint(cycleConstraint);
		model.setTotalCount(1);
		model.setRealCount(0);
		paraMap.put("cycleConstraint", cycleConstraint);
		int i = 0;
		try {
			i = activityLotteryManager.insertActivityLottery(model);
		} catch (Exception e) {
			logger.error("520活动真爱大礼包重复领取, memberId={}, cycleKey={}", transaction.getMemberId(), cycleConstraint);
			return false;
		}
		if (i < 1) {
			return false;
		}
		// 赠送礼包中的人气值
		Integer popularityValue = rule.getGiftPacksForPopularity();
		if (popularityValue != null && popularityValue.intValue() > 0) {
			Balance balance = balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, new BigDecimal(popularityValue),
					transaction.getMemberId());
			popularityInOutLogManager.insert(transaction.getMemberId(), TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, new BigDecimal(popularityValue), null,
					balance.getAvailableBalance(), activity.getId(), activity.getActivityName()
							+ RemarksEnum.ACTIVITY_LOTTERY_FOR_POPULARITY.getRemarks());
			balanceManager.incrGivePlatformTotalPoint(new BigDecimal(popularityValue));
		}
		// 赠送礼包中的优惠券
		List<Long> couponList = rule.getGiftPacksForCoupons();
		if (Collections3.isEmpty(couponList)) {
			logger.error("520活动真爱大礼包优惠券为空");
			return false;
		}
		for (Long templateId : couponList) {
			// 收益券or现金券
			Coupon c = couponManager.receiveCoupon(transaction.getMemberId(), activity.getId(), templateId, -1L);
			if (c == null) {
				// 优惠券赠送失败;
				logger.error("优惠券赠送失败，接口返回Coupon=null!, memberId={}, activityId={}, templateId={}", transaction.getMemberId(),
						activity.getId(), templateId);
			}
		}
		return true;
	}

	/**
	 * 
	 * @Description:获取时间范围内的用户总投资额
	 * @param memberId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author: wangyanji
	 * @throws ManagerException
	 * @time:2016年4月18日 下午3:24:16
	 */
	private BigDecimal getTotalAmountByMemberId(Long memberId, Date startTime, Date endTime) throws ManagerException {
		TransactionQuery query = new TransactionQuery();
		query.setMemberId(memberId);
		query.setTransactionStartTime(startTime);
		query.setTransactionEndTime(endTime);
		return transactionManager.getMemberTotalInvest(query);
	}

	@Override
	public ResultDO<Object> popularityRedPackage(ActivityBiz activityBiz, Transaction transaction) {
		return redPackageManager.popularityRedPackage(activityBiz, transaction);
	}
	

	/**
	 * 
	 * @Description:破20亿活动
	 * @param transaction
	 * @author: wangyanji
	 * @time:2016年5月4日 下午2:40:17
	 */
	private void break2Billion(Transaction transaction) {
		try {
			Optional<Activity> optOfActivity = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.ACTIVITY_2_BREAK_BILLION_NAME);
			if (!optOfActivity.isPresent()) {
				return;
			}
			Activity activity = optOfActivity.get();
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
				return;
			}
			ActivityForBreak2Billion rule = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForBreak2Billion.class,
					ActivityConstant.ACTIVITY_2_BREAK_BILLION);
			// 判断20亿有没有到
			if (StringUtil.isBlank(rule.getAmountUnit())) {
				logger.error("破二十亿活动数据缺失");
				return;
			}
			// 是否已经破20亿
			String key = RedisConstant.REDIS_PLATFORM_TOTAL_INVEST_TIME;
			String break2BillionTimeStr = RedisManager.hget(key, rule.getAmountUnit());
			if (StringUtil.isNotBlank(break2BillionTimeStr)) {
				return;
			} 
			// 在活动期间，投资送券
			Long fund = RedisActivityClient.getActivityCouponAmountLimit(activity.getId(), rule.getFund().toString());
			if(fund <= 0l) {
				return;
			}
			if(transaction.getInvestAmount().intValue() < rule.getFundLevel().get(0)) {
				return;
			}
			Long prizeTemplateId = 0l;
			int index = 0;
			for (int size = rule.getFundLevel().size(); index < size; index++) {
				if (index == size - 1) {
					break;
				}
				if (transaction.getInvestAmount().intValue() < rule.getFundLevel().get(index + 1)) {
					break;
				}
			}
			// 发放现金券
			prizeTemplateId = rule.getFundPrizeLevel().get(index);
			RuleBody rb = new RuleBody();
			rb.setActivityId(activity.getId());
			rb.setMemberId(transaction.getMemberId());
			rb.setCycleStr(transaction.getId().toString());
			rb.setDeductValue(transaction.getInvestAmount().intValue());
			rb.setActivityName(activity.getActivityDesc());
			rb.setDeductRemark(ActivityConstant.ACTIVITY_2_BREAK_BILLION_PART1);
			CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(prizeTemplateId);
			RewardsBase rBase = new RewardsBase();
			rBase.setTemplateId(prizeTemplateId);
			if (couponTemplate.getCouponType() == TypeEnum.COUPON_TYPE_CASH.getType()) {
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(couponTemplate.getAmount().intValue() + ActivityConstant.couponDesc);
			} else {
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
				rBase.setRewardName(couponTemplate.getAmount().intValue() + ActivityConstant.annualizedDesc);
			}
			rBase.setRewardValue(couponTemplate.getAmount().intValue());
			// 发放奖励
			drawByPrizeDirectly.drawLottery(rb, rBase, null);
			MessageClient.sendMsgForSPEngin(transaction.getMemberId(), activity.getActivityDesc(), rBase.getRewardName());
			RedisActivityClient.decrActivityCouponAmountLimit(activity.getId(), couponTemplate.getAmount());
		} catch (Exception e) {
			logger.error("破二十亿活动投资送券失败");
		}
	}
	/**
	 * "融"光焕发  感恩齐狂欢 感恩高收益 
	 * @param transaction
	 */
	private void celebrationA(Transaction transaction) {
		try {
			String activityId = PropertiesUtil.getProperties("activity.celebrationA.id");
			Optional<Activity> optOfActivity = LotteryContainer.getInstance().getActivity(Long.parseLong(activityId));
			if (!optOfActivity.isPresent()) {
				return;
			}
			Activity activity = optOfActivity.get();
			//是否在活动时间内
			if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(),activity.getStartTime(), activity.getEndTime())){
				return;
			}
			ActivityForA rule = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForA.class,
					ActivityConstant.ACTIVITY_CELEBRATE_A);
			// 发放现金券
			Long prizeTemplateId = 0l;
			if(rule.getTotalAmount().size()<4){
				return;
			}
			if(rule.getTemplateId().size()<5){
				return;
			}
			if (transaction.getInvestAmount().intValue() < rule.getTotalAmount().get(0)) {
					return;
			}else if(transaction.getInvestAmount().intValue() >= rule.getTotalAmount().get(0)&&transaction.getInvestAmount().intValue() < rule.getTotalAmount().get(1)){
				prizeTemplateId=rule.getTemplateId().get(0);
			}else if(transaction.getInvestAmount().intValue() >= rule.getTotalAmount().get(1)&&transaction.getInvestAmount().intValue() < rule.getTotalAmount().get(2)){
				prizeTemplateId=rule.getTemplateId().get(1);
			}else if(transaction.getInvestAmount().intValue() >= rule.getTotalAmount().get(2)){
				prizeTemplateId=rule.getTemplateId().get(2);
			}
			RuleBody rb = new RuleBody();
			rb.setActivityId(activity.getId());
			rb.setMemberId(transaction.getMemberId());
			rb.setCycleStr(transaction.getId().toString());
			rb.setDeductValue(transaction.getInvestAmount().intValue());
			rb.setActivityName(ActivityConstant.ACTIVITY_CELEBRATE_A_NAME);
			rb.setDeductRemark(ActivityConstant.ACTIVITY_CELEBRATE_A);
			CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(prizeTemplateId);
			RewardsBase rBase = new RewardsBase();
			rBase.setTemplateId(prizeTemplateId);
			rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
			rBase.setRewardName(couponTemplate.getAmount().intValue() + ActivityConstant.couponDesc);
			rBase.setRewardValue(couponTemplate.getAmount().intValue());
			// 发放奖励
			drawByPrizeDirectly.drawLottery(rb, rBase, null);
			MessageClient.sendMsgForSPEngin(transaction.getMemberId(), rb.getActivityName(), rBase.getRewardName());
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if (!DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSE.getCode().equals(project.getProjectType())
					&& !DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode().equals(project.getProjectType())) {
				// 不是房有融
				return;
			}
			if(transaction.getInvestAmount().intValue() >= rule.getTotalAmount().get(3)){
				if(transaction.getTotalDays()<rule.getDay()){
					prizeTemplateId=rule.getTemplateId().get(3);
				}else{
					prizeTemplateId=rule.getTemplateId().get(4);
				}
				rBase.setTemplateId(prizeTemplateId);
				couponTemplate = couponTemplateManager.selectByPrimaryKey(prizeTemplateId);
				rBase.setTemplateId(prizeTemplateId);
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
				rBase.setRewardName(couponTemplate.getAmount() + ActivityConstant.annualizedUnit+ ActivityConstant.annualizedDesc);
				drawByPrizeDirectly.drawLottery(rb, rBase, null);
				MessageClient.sendMsgForSPEngin(transaction.getMemberId(), rb.getActivityName(), rBase.getRewardName());
			}
		} catch (Exception e) {
			logger.error("融光焕发投资送券失败");
		}
	}

	/**
	 * 
	 * @Description:一羊领头
	 * @param transaction
	 * @author: wangyanji
	 * @time:2016年6月16日 上午11:34:16
	 */
	private void firstInvestInProject(Transaction transaction, int investType) {
		try {
			boolean sendFlag = false;
			Date transDate = null;
			long firstTransactionMemberId = 0l;
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_LEADING_SHEEP
					+ RedisConstant.REDIS_SEPERATOR + transaction.getProjectId();
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if (project.getInvestType() != investType) {
				return;
			}
			//转让项目不参与五重礼
			if(transaction.getProjectCategory()==2){
				return;
			}
			// 查询一样领头是否已经存在
			PopularityInOutLogQuery query = new PopularityInOutLogQuery();
			query.setSourceId(transaction.getProjectId());
			query.setType(TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY.getType());
			query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
			List<ActivityForFirstInvest> firstList = popularityInOutLogManager.selectActivityForFirstInvestList(query);
			if (Collections3.isNotEmpty(firstList)) {
				return;
			}
			int totalDays=0;
			if (!project.isDirectProject()) {
				// 债权项目
				String lockValue = RedisManager.get(key);
				if (StringUtil.isBlank(lockValue)) {
					return;
				}
				String[] valueArr = lockValue.split(RedisConstant.REDIS_SEPERATOR);
				long orderId = Long.valueOf(valueArr[0]);
				if (!transaction.getOrderId().equals(orderId)) {
					return;
				}
				// 赠送
				firstTransactionMemberId = transaction.getMemberId();
				totalDays=transaction.getTotalDays();
				sendFlag = true;
			} else {
				// 直投项目
				if (RedisManager.isExit(key)) {
					return;
				}
				TransactionQuery transactionQuery = new TransactionQuery();
				transactionQuery.setProjectId(transaction.getProjectId());
				List<TransactionForProject> transactionList = transactionManager.selectTransactionDetailByQueryParams(transactionQuery);
				TransactionForProject firstTransaction = transactionList.get(transactionList.size() - 1);
				if (!RedisActivityClient.setFirstInvestInProject(firstTransaction.getProjectId(), firstTransaction.getOrderId())) {
					return;
				}
				// 赠送
				firstTransactionMemberId = firstTransaction.getMemberId();
				transDate = firstTransaction.getTransactionTime();
				totalDays=firstTransaction.getTotalDays();
				sendFlag = true;
			}
			if (!sendFlag || firstTransactionMemberId == 0l) {
				return;
			}
			// 满足条件，赠送人气值
			String yyltValue=transactionManager.getPopularityValue(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getCode(),totalDays,Config.firstInvestPopularity);
			
			/** 庆60亿，抢标奖励翻6倍 **/
			BigDecimal value=this.sixBillionActivity(transDate, yyltValue);			//不满足活动条件
			if(value == null || value.intValue() < 1){
				value = new BigDecimal(yyltValue);
			}
			/** end **/
			
			if(value.intValue() < 1) {
				return;
			}
			// 调用赠送人气值接口
			Balance balance = balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, value, firstTransactionMemberId);
			/* 赠送人气值流水 */
			int flag = popularityInOutLogManager.insert(firstTransactionMemberId, TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, value, null,
					balance.getBalance(), transaction.getProjectId(), // 人气值的来源：用户id
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
			if (flag > 0) {
				balanceManager.incrGivePlatformTotalPoint(value);// 平台累计送出人气值
				MessageClient.sendMsgForProjectActivities(transaction.getProjectId(), "一羊领头",value.toString(),
						firstTransactionMemberId);
				logger.info("项目：" + transaction.getProjectId() +"一羊领头获得者：" + firstTransactionMemberId);
				// 清空缓存
				RedisForProjectClient.clearTransactionDetail(transaction.getProjectId());
				//一羊领头 赠送人气值
				MessageClient.sendMsgForCommon(firstTransactionMemberId, Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_FIVE_REWARD.getCode(), 
						project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName(),
								"一羊领头",value.toString());
			}
		} catch (Exception e) {
			logger.error("一羊领头赠送奖励失败");
		}
	}
	
	/**
	 * 
	 * @Description:玩转奥运
	 * @param transaction
	 * @author: chaisen
	 * @time:2016年7月12日 下午7:03:36
	 */
	private void playOlympic(Transaction transaction) {
		Optional<Activity> olympicActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
		if(!olympicActivity.isPresent()){
			return;
		}
		Activity activity = olympicActivity.get();
		logger.info("玩转奥运-集奥运开始, transactionId={}, memberId={}, activityId={}", transaction.getId(),
				transaction.getMemberId(), activity.getId());
		//是否在活动时间内
		if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(),activity.getStartTime(), activity.getEndTime())){
			return;
		}
		//记录当天的投资笔数
		RedisActivityClient.incrTransactionCount(1L);
		ActivityForOlympicDate olympicDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForOlympicDate.class,
				ActivityConstant.ACTIVITY_PLAY_OLYMPIC_KEY);
		//亮奥运送奖品
		brightOlympic(transaction,activity,olympicDate);
		//集奥运
		setOlympic(transaction,activity,olympicDate);
	}
	/**
	 * 
	 * @Description:投资送奥运吉祥物
	 * @param transaction
	 * @param activity
	 * @param olympicDate
	 * @author: chaisen
	 * @time:2016年7月8日 下午2:26:56
	 */
	private void setOlympic(Transaction transaction, Activity activity, ActivityForOlympicDate olympicDate) {
		try {
			RuleBody rb = new RuleBody();
			rb.setActivityId(activity.getId());
			rb.setMemberId(transaction.getMemberId());
			rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			List  tempList=Lists.newArrayList();
			//校验第四张拼图主键
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.ACTIVITY_PLAY_OLYMPIC_KEY
					+ RedisConstant.REDIS_SEPERATOR +ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ RedisConstant.REDIS_SEPERATOR + DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
			//boolean isExit = RedisManager.isExit(key);
			//已经集齐其他三张
			if(isExitsOtherThree(activity.getId(),transaction.getMemberId(),olympicDate)){
				//校验发放第四张
				if(sendFourOlympic(olympicDate, transaction,  rb,activity.getId(),key)>0){
					if(transaction.getInvestAmount().compareTo(olympicDate.getTotalAmount().get(2))>=0){
						tempList=RandomUtils.getRendomNumber(olympicDate.getPuzzleNumber().get(2)-1,olympicDate.getProbability()); 
					//获取3个吉祥物
					}else if(transaction.getInvestAmount().compareTo(olympicDate.getTotalAmount().get(1))>=0){
						tempList=RandomUtils.getRendomNumber(olympicDate.getPuzzleNumber().get(1)-1,olympicDate.getProbability()); 
					}
						sendOtherOlympic(transaction,rb,olympicDate,tempList,activity.getId());
					}else{
						sendOtherTreeOlympic(transaction,rb,olympicDate,tempList,activity.getId());
					}		
			}else{
				sendOtherTreeOlympic(transaction,rb,olympicDate,tempList,activity.getId());
			}
		} catch (Exception e) {
			logger.error("获取奥运吉祥物失败");
		}
	}
	/**
	 * 
	 * @Description:其他三张是否集齐
	 * @param activityId
	 * @param memberId
	 * @param olympicDate
	 * @return
	 * @author: chaisen
	 * @time:2016年7月21日 下午7:10:02
	 */
	private boolean isExitsOtherThree(Long activityId,Long memberId,ActivityForOlympicDate olympicDate){
		//是否集齐其他三张拼图
		ActivityLottery model = new ActivityLottery();
		model.setActivityId(activityId);
		model.setMemberId(memberId);
		for(int i=0;i<olympicDate.getPuzzle().size()-1;i++){
			model.setCycleConstraint(memberId+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+olympicDate.getPuzzle().get(i));
			ActivityLottery biz=activityLotteryMapper.checkExistLottery(model);
			if(biz==null){
				return false;
			}
			if(biz.getRealCount()<1){
				return false;
			}
		}
		return true;
	}
	/**
	 * 
	 * @Description:发放其他三张拼图
	 * @param transaction
	 * @param rb
	 * @param olympicDate
	 * @param tempList
	 * @param activityId
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月21日 下午7:18:39
	 */
	private void sendOtherTreeOlympic(Transaction transaction, RuleBody rb, ActivityForOlympicDate olympicDate,List  tempList,Long activityId) throws ManagerException{
		//获取5个吉祥物
		if(transaction.getInvestAmount().compareTo(olympicDate.getTotalAmount().get(2))>=0){
			tempList=RandomUtils.getRendomNumber(olympicDate.getPuzzleNumber().get(2),olympicDate.getProbability()); 
		//获取3个吉祥物
		}else if(transaction.getInvestAmount().compareTo(olympicDate.getTotalAmount().get(1))>=0){
			tempList=RandomUtils.getRendomNumber(olympicDate.getPuzzleNumber().get(1),olympicDate.getProbability()); 
		//获取1个吉祥物
		}else if(transaction.getInvestAmount().compareTo(olympicDate.getTotalAmount().get(0))>=0){
			tempList=RandomUtils.getRendomNumber(olympicDate.getPuzzleNumber().get(0),olympicDate.getProbability()); 
		}else{
			return;
		}
		try {
			if(Collections3.isNotEmpty(tempList)){
				for(int i=0;i<tempList.size();i++){
					rb.setCycleStr(transaction.getMemberId()+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+tempList.get(i));
					int j=addLotteryRecord(activityId,transaction.getMemberId(),1,1,rb.getCycleStr());
					if(i>0){
						logger.info("集奥运拼图发放成功, memberId={}, activityId={}", transaction.getMemberId(), activityId,"用户获得拼图"+tempList.get(i));
					}
				}
			}
		} catch (ManagerException e) {
			throw e;
		}
	}
	
	private void sendOtherOlympic(Transaction transaction, RuleBody rb, ActivityForOlympicDate olympicDate,List  tempList,Long activityId) throws ManagerException{
		try {
			if(Collections3.isNotEmpty(tempList)){
				for(int i=0;i<tempList.size();i++){
					rb.setCycleStr(transaction.getMemberId()+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+tempList.get(i));
					addLotteryRecord(activityId,transaction.getMemberId(),1,1,rb.getCycleStr());
				}
			}
		} catch (ManagerException e) {
			throw e;
		}
	}
	/**
	 * 
	 * @Description:发放第四张拼图
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月21日 下午7:35:00
	 */
	private int sendFourOlympic(ActivityForOlympicDate olympicDate,Transaction transaction, RuleBody rb,Long activityId,String key) throws ManagerException{
		int i=0;
		int totalInvest=0;
		int four=0;
		//第四张拼图 8.7-到 8.21 送
		if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(),DateUtils.getDateTimeFromString(olympicDate.getSetFourStartTime()), DateUtils.getDateTimeFromString(olympicDate.getSetFourEndTime()))){
			return i;
		}
		if(transaction.getInvestAmount().compareTo(olympicDate.getTotalAmount().get(0))<0){
			return i;
		}
		//查询当前投资笔数
		String totalKey = RedisConstant.ACTIVITY_PLAY_OLYMPIC_CURRENT_TRANSACTION_TOTALINVEST + RedisConstant.REDIS_SEPERATOR + DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
		if(RedisManager.isExit(totalKey)){
			String total=RedisManager.get(totalKey);
			if(total!=null){
				totalInvest=Integer.parseInt(total);
			}
		}else{
			totalInvest=transactionMapper.countInvestNumbers(DateUtils.zerolizedTime(DateUtils.getCurrentDate()),DateUtils.getEndTime(DateUtils.getCurrentDate()));
		}
		
		if(totalInvest<olympicDate.getFiveHundred()){
			return i;
		}
		//第四张数量控制在15张
		String fourkey = RedisConstant.REDIS_KEY_ACTIVITY+ RedisConstant.REDIS_SEPERATOR+RedisConstant.ACTIVITY_PLAY_OLYMPIC_CURRENT_FOURPUZZLE;
		if(RedisManager.isExit(fourkey)){
			String totalFour=RedisManager.get(fourkey);
			if(totalFour!=null){
				four=Integer.parseInt(totalFour);
			}
		}else{
			four=activityLotteryMapper.countActivityLotteryFourByActivityId(activityId);
		}
		if(four>15){
			return i;
		}
		rb.setCycleStr(transaction.getMemberId()+ActivityConstant.redBagCodeSplit+ActivityConstant.ACTIVITY_OLYMPIC_SET_OLYMPIC+ActivityConstant.redBagCodeSplit+olympicDate.getPuzzle().get(3));
		try {
				if(!RedisActivityClient.setFourOlympicPuzzle(transaction.getId())){
					return i;
				}
				i= addLotteryRecord(activityId,transaction.getMemberId(),1,1,rb.getCycleStr());
				if(i>0){
					//当前领取过记录到redis
					//RedisManager.putString(key,transaction.getId().toString());
					//记录第四张拼图领取数量
					logger.info("集奥运拼图4发放成功, memberId={}, activityId={}", transaction.getMemberId(), activityId);
					RedisActivityClient.incrFourPuzzle(1l);
				}
			return i;
		} catch (ManagerException e) {
			throw e;
		}
	}
	/**
	 * 
	 * @Description:亮奥运
	 * @param transaction
	 * @param activity
	 * @param olympicDate
	 * @author: chaisen
	 * @time:2016年7月8日 下午2:16:26
	 */
	public void brightOlympic(Transaction transaction,Activity activity,ActivityForOlympicDate olympicDate) {
		
		try {
			//亮奥运逻辑处理
			BigDecimal totalInvestAmount =transactionManager.getMyTotalInvestByMemberIdAndTime(transaction.getMemberId(),DateUtils.zerolizedTime(DateUtils.getCurrentDate()),DateUtils.getEndTime(DateUtils.getCurrentDate()));
			//当天累计投资额大于2000
			if(totalInvestAmount.compareTo(olympicDate.getInvestAmount())>=0){
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(transaction.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_PLAY_BRIGHT_OLYMPIC);
				rb.setDeductRemark(ActivityConstant.ACTIVITY_PLAY_BRIGHT_OLYMPIC+activity.getId());
				rb.setDeductValue(olympicDate.getDeductPopularvalue());
				rb.setActivityName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
				// 校验
				if(drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					addLotteryRecord(activity.getId(),transaction.getMemberId(),1,0,DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_PLAY_BRIGHT_OLYMPIC);
				}
				//统计徽章数量
				Long templateId=1L;
				int popularityValue=0;
				int couponAmoun=0;
				int count=activityLotteryMapper.countActivityLotteryByActivityIdAndCycleConstraint(activity.getId(),transaction.getMemberId(),ActivityConstant.ACTIVITY_PLAY_BRIGHT_OLYMPIC);
				//累计点亮15个
				if(count==olympicDate.getBadgeNumber().get(0)){
					templateId=olympicDate.getTemplateId().get(3);
					popularityValue=olympicDate.getPopularityValue().get(2);
					couponAmoun=olympicDate.getCouponAmount().get(3);
				//累计点亮10个
				}else if(count==olympicDate.getBadgeNumber().get(1)){
					templateId=olympicDate.getTemplateId().get(2);
					popularityValue=olympicDate.getPopularityValue().get(1);
					couponAmoun=olympicDate.getCouponAmount().get(2);
				//累计点亮5个
				}else if(count==olympicDate.getBadgeNumber().get(2)){
					templateId=olympicDate.getTemplateId().get(1);
					popularityValue=olympicDate.getPopularityValue().get(0);
					couponAmoun=olympicDate.getCouponAmount().get(1);
				}else{
					return;
				}
				//校验是否领取过
				ActivityLotteryResult model=new ActivityLotteryResult();
				model.setActivityId(activity.getId());
				model.setMemberId(transaction.getMemberId());
				model.setRewardResult(Integer.toString(couponAmoun));
				model.setRemark(ActivityConstant.ACTIVITY_PLAY_BRIGHT_OLYMPIC+activity.getId());
				List<ActivityLotteryResult> list=activityLotteryResultManager.getLotteryResultBySelective(model);
				if(Collections3.isNotEmpty(list)){
					return;
				}
				//发放现金券
				CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
				RewardsBase rBase = new RewardsBase();
				rBase.setTemplateId(templateId);
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(couponTemplate.getAmount().intValue() + ActivityConstant.couponDesc);
				rBase.setRewardValue(couponTemplate.getAmount().intValue());
				drawByPrizeDirectly.drawLottery(rb, rBase, null);
				//送人气值
				rBase.setRewardName(popularityValue + ActivityConstant.popularityDesc);
				rBase.setRewardValue(popularityValue);
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
				rb.setDeductRemark(ActivityConstant.ACTIVITY_OLYMPIC_NAME+RemarksEnum.ACTIVITY_LOTTERY_FOR_POPULARITY);
				drawByPrizeDirectly.drawLottery(rb, rBase, "");
				//发送短信
				MessageClient.sendMsgForCommon(transaction.getMemberId(),Constant.MSG_TEMPLATE_TYPE_PHONE, MessageEnum.BRIGHT_OLYMPIC.getCode(),
						ActivityConstant.ACTIVITY_PLAY_OLYMPIC,Integer.toString(count),couponTemplate.getAmount().toString(),Integer.toString(popularityValue));
				//站内信
				MessageClient.sendMsgForCommon(transaction.getMemberId(),Constant.MSG_TEMPLATE_TYPE_STAND, MessageEnum.BRIGHT_OLYMPIC_MAIL.getCode(),
						DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_13),ActivityConstant.ACTIVITY_OLYMPIC_BRIGHT_NAME,couponTemplate.getAmount().toString(),Integer.toString(popularityValue));
				}
		} catch (Exception e) {
			logger.error("亮奥运赠送奖励失败");
		}
	}
	private int addLotteryRecord(Long activityId, Long memberId, int totalNum, int realNum, String jsonCondition) throws ManagerException {
		try {
			int i=0;
			ActivityLottery model = new ActivityLottery();
			model.setActivityId(activityId);
			model.setMemberId(memberId);
			model.setTotalCount(totalNum);
			model.setRealCount(realNum);
			model.setCycleConstraint(jsonCondition);
			model.setRemark(ActivityConstant.DIRECT_COUNT_LOTTERY_KEY);
			if(activityLotteryMapper.checkExistLottery(model) == null) {
				//第一次获得抽奖机会
				i=activityLotteryMapper.insertSelective(model);
			} else {
				//累积抽奖机会
				i=activityLotteryMapper.updateByActivityAndMember(model);
			}
			return i;
		}catch (DuplicateKeyException mysqlE) {
			throw new ManagerException("重复插入数据");
		} catch (Exception e) {
			throw e;
		}
	}
	@Override
	public void olympicTest(Transaction transaction) {
		playOlympic(transaction);
	}
	private int getDays(Project project ) throws ManagerException{
		
		BigDecimal days=BigDecimal.ZERO;
		//日
		if (project.getBorrowPeriodType() == 1) {
			days = new BigDecimal(project.getBorrowPeriod()).divide(new BigDecimal(30), 10, BigDecimal.ROUND_HALF_DOWN);
		}
		//月
		if (project.getBorrowPeriodType() == 2) {
			days=new BigDecimal(project.getBorrowPeriod());
		}
		//年
		if (project.getBorrowPeriodType() == 3) {
			days=new BigDecimal(project.getBorrowPeriod()).multiply(new BigDecimal(12));
		}
		return days.intValue();
	}
	/**
	 * 一锤定音送人气值
	 * @param transaction
	 * @param investType
	 */
	private void lastInvestInProject(Transaction transaction, int investType) {
		Long memberId=0L;
		try {
			/*String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_LAST_INVEST
					+ RedisConstant.REDIS_SEPERATOR + transaction.getProjectId();
			if (RedisManager.isExit(key)) {
				return;
			}*/
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			// 一锤定音活动
			if(!RedisActivityClient.fiveRitesInvestInProject(transaction.getProjectId(), RedisConstant.REDIS_KEY_LAST_INVEST, RedisConstant.REDIS_KEY_LAST_INVEST_NAME)){
				return;
			}
			//最后一笔交易
			Transaction lastTransaction=transactionManager.selectLastTransactionByProjectOrderById(transaction.getProjectId());
			
			int days=0;
			if(lastTransaction==null){
				return;
			}
			if(project.isDirectProject()){
				days= project.countProjectDays();
			}else{
				days=lastTransaction.getTotalDays();
			}
			
			memberId=lastTransaction.getMemberId();
			if(memberId == 0L){
				return;
			}
			//获取人气值
			String lastInvestPopularity=transactionManager.getPopularityValue(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getCode(),days,Config.lastInvestPopularity);
			
			/** 庆60亿，抢标奖励翻6倍 **/
			BigDecimal value=this.sixBillionActivity(lastTransaction.getTransactionTime(), lastInvestPopularity);
			//不满足活动条件
			if(value == null || value.intValue() < 1){
				value = new BigDecimal(lastInvestPopularity);
			}
			/** end **/
			
			if(value.intValue() < 1) {
				return;
			}
			// 调用赠送人气值接口
			boolean flag=transactionManager.givePopularity(transaction.getProjectId(), memberId, TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, value,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
			if (flag) {
				MessageClient.sendMsgForProjectActivities(transaction.getProjectId(), "一锤定音", value.toString(),memberId);
				logger.info("项目：" + transaction.getProjectId() +"一锤定音获得者：" + memberId);
				// 清空缓存
				RedisForProjectClient.clearTransactionDetail(transaction.getProjectId());
				String key = RedisConstant.TRANSACTION_INVERESTLIST_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + transaction.getProjectId();
				RedisManager.removeObject(key);
				//一锤定音 赠送人气值
				MessageClient.sendMsgForCommon(lastTransaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_FIVE_REWARD.getCode(), 
						project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName(),
								"一锤定音",value.toString());
			}
		} catch (Exception e) {
			logger.error("一锤定音赠送奖励失败");
		}
	}
	
	//五重礼赠送人气值
	private void fiveRites(Transaction transaction, int investType) {
		try {
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if (project.getInvestType() != investType) {
				return;
			}
			//转让项目不参与五重礼
			if(transaction.getProjectCategory()==2){
				return;
			}
			if (investType==ProjectEnum.PROJECT_TYPE_DEBT.getType()) {
				//判断项目余额
				Balance projectBalance = balanceManager.queryBalanceLocked(transaction.getProjectId(),TypeEnum.BALANCE_TYPE_PROJECT);
				if(projectBalance!=null&&projectBalance.getAvailableBalance().compareTo(BigDecimal.ZERO)!=0){
					logger.info("五重礼发放：" + transaction.getProjectId() + "余额：" + projectBalance.getAvailableBalance() + " 交易id:"+ transaction.getId());
					return;
				}
				//待支付订单
				int count=hostingCollectTradeMapper.countWaitOrderCollectTrade(transaction.getProjectId());
				if(count>0){
					logger.info("五重礼发放失败：" + transaction.getMemberId()+ ", 交易id:"+ transaction.getId()+ ",待支付订单:"+ count);
					return;
				}
			}
			//人气值流水判断
			//一锤定音人气值流水
			int i=popularityInOutLogManager.countInvestBySourceId(transaction.getProjectId());
			if(i>0){
				return;
			}
			logger.info("五重礼发放开始：" + transaction.getProjectId());
			//一锤定音
			lastInvestInProject(transaction,investType);
			//一鸣惊人
			mostInvestInProject(transaction,investType);
			//一掷千金
			mostAndLastInvestInProject(transaction,investType);
			//幸运女神
			luckInvestInProject(transaction,investType);
		} catch (Exception e) {
			logger.error("五重礼赠送奖励失败");
		}
	}
	/**
	 * 一鸣惊人
	 * @param transaction
	 * @param investType
	 */
	private void mostInvestInProject(Transaction transaction, int investType) {
		Long memberId=0L;
		try {
			if(!RedisActivityClient.fiveRitesInvestInProject(transaction.getProjectId(), RedisConstant.REDIS_KEY_MOST_INVEST, RedisConstant.REDIS_KEY_MOST_INVEST_NAME)){
				return;
			}
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			// 一鸣惊人活动
			Transaction mostTransaction = transactionManager.selectMostTransactionByProject(transaction.getProjectId());
			if(mostTransaction==null){
				return;
			}
			int days=0;
			if(project.isDirectProject()){
				days= project.countProjectDays();
			}else{
				days=mostTransaction.getTotalDays();
			}
				
			String mostInvestPopularity=transactionManager.getPopularityValue(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getCode(),days,Config.mostInvestPopularity);
			/** 庆60亿，抢标奖励翻6倍 **/
			BigDecimal value=this.sixBillionActivity(mostTransaction.getTransactionTime(), mostInvestPopularity);
			//不满足活动条件
			if(value == null || value.intValue() < 1){
				value = new BigDecimal(mostInvestPopularity);
			}
			/** end **/
			
			if(value.intValue() < 1) {
				return;
			}
			// 调用赠送人气值接口
			boolean flag=transactionManager.givePopularity(mostTransaction.getProjectId(), mostTransaction.getMemberId(), TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY,value,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
			logger.info("项目：" + mostTransaction.getProjectId() + "一鸣惊人获得者：" + mostTransaction.getMemberId());
			if (flag) {
				MessageClient.sendMsgForProjectActivities(transaction.getProjectId(), "一鸣惊人", value.toString(),mostTransaction.getMemberId());
				// 清空缓存
				RedisForProjectClient.clearTransactionDetail(transaction.getProjectId());
				String key = RedisConstant.TRANSACTION_INVERESTLIST_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + transaction.getProjectId();
				RedisManager.removeObject(key);
				//一鸣惊人 赠送人气值
				MessageClient.sendMsgForCommon(mostTransaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_FIVE_REWARD.getCode(), 
						project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName(),
								"一鸣惊人",value.toString());
			}
		} catch (Exception e) {
			logger.error("一鸣惊人赠送奖励失败");
		}
	}
	/**
	 * 一掷千金
	 * @param transaction
	 * @param investType
	 */
	private void mostAndLastInvestInProject(Transaction transaction, int investType) {
		Long memberId=0L;
		boolean flag= false;
		try {
			if(!RedisActivityClient.fiveRitesInvestInProject(transaction.getProjectId(), RedisConstant.REDIS_KEY_MOST_LAST_INVEST, RedisConstant.REDIS_KEY_MOST_LAST_INVEST_NAME)){
				return;
			}
			Transaction mostTransaction = transactionManager.selectMostTransactionByProject(transaction.getProjectId());
			Transaction lastTransaction=transactionManager.selectLastTransactionByProjectOrderById(transaction.getProjectId());
			if(mostTransaction==null||lastTransaction==null){
				return;
			} 
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			int days=0;
			if(project.isDirectProject()){
				days= project.countProjectDays();
			}else{
				days=mostTransaction.getTotalDays();
			}
				
			// 一掷千金
			if (!lastTransaction.getMemberId().equals(mostTransaction.getMemberId())) {
				return;
			}
			// 一鸣惊人得主和一锤定音得主为同一个人
			String mostAndLastInvestPopularity=transactionManager.getPopularityValue(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getCode(),days,Config.mostAndLastInvestPopularity);
			
			/** 庆60亿，抢标奖励翻6倍 **/
			BigDecimal value=this.sixBillionActivity(DateUtils.getCurrentDate(), mostAndLastInvestPopularity);
			//不满足活动条件
			if(value == null || value.intValue() < 1){
				value = new BigDecimal(mostAndLastInvestPopularity);
			}
			/** end **/
			
			if(value.intValue() < 1) {
				return;
			}
			// 调用赠送人气值接口
			flag=transactionManager.givePopularity(mostTransaction.getProjectId(), mostTransaction.getMemberId(), TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, value,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks());
			if (flag) {
				MessageClient.sendMsgForProjectActivities(mostTransaction.getProjectId(),"一掷千金", value.toString(),mostTransaction.getMemberId());
				logger.info("项目：" + mostTransaction.getProjectId() + "一掷千金获得者：" + mostTransaction.getMemberId());
				// 清空缓存
				RedisForProjectClient.clearTransactionDetail(mostTransaction.getProjectId());
				String key = RedisConstant.TRANSACTION_INVERESTLIST_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + transaction.getProjectId();
				RedisManager.removeObject(key);
				//一掷千金赠送人气值
				MessageClient.sendMsgForCommon(mostTransaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_FIVE_REWARD.getCode(), 
						project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName(),
								"一掷千金",value.toString());
			}
		} catch (Exception e) {
			logger.error("一掷千金赠送奖励失败");
		}
	}
	/**
	 * 幸运女神
	 * @param transaction
	 * @param investType
	 */
	private void luckInvestInProject(Transaction transaction, int investType) {
		Long memberId=0L;
		try {
			if(!RedisActivityClient.fiveRitesInvestInProject(transaction.getProjectId(), RedisConstant.REDIS_KEY_LUCK_INVEST, RedisConstant.REDIS_KEY_LUCK_INVEST_NAME)){
				return;
			}
			// 幸运女神活动
			TransactionQuery transactionQuery = new TransactionQuery();
			transactionQuery.setProjectId(transaction.getProjectId());
			List<Transaction> transactions = transactionManager.selectTransactionsByQueryParams(transactionQuery);
			if (Collections3.isEmpty(transactions)) {
				return;
			}
			
				// 获取随机种子
				int randomNum = (int) (Math.random() * transactions.size());
				Transaction luckTransaction = transactions.get(randomNum);
				int days=0;
				Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
				if(project.isDirectProject()){
					days= project.countProjectDays();
				}else{
					days=luckTransaction.getTotalDays();
				}
					
				String xynsValue=transactionManager.getPopularityValue(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getCode(),days,Config.luckInvestPopularity);
				
				/** 庆60亿，抢标奖励翻6倍 **/
				BigDecimal value=this.sixBillionActivity(luckTransaction.getTransactionTime(), xynsValue);
				//不满足活动条件
				if(value == null || value.intValue() < 1){
					value = new BigDecimal(xynsValue);
				}
				/** end **/
				
				if(value.intValue() < 1) {
					return;
				}
				// 调用赠送人气值接口
				boolean flag=transactionManager.givePopularity(luckTransaction.getProjectId(), luckTransaction.getMemberId(),TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, value,
						RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
				Member memberLuck = memberManager.selectByPrimaryKey(luckTransaction.getMemberId());
				boolean isLast = false;
				boolean isFirstInvest = false;
				// 财富心跳插入redis 数据
				transactionManager.addTransactionDetailForIndexQuadruplegifts(luckTransaction, isFirstInvest, isLast, false, true, project, memberLuck);

			if (flag) {
				balanceManager.incrGivePlatformTotalPoint(value);// 平台累计送出人气值
				MessageClient.sendMsgForProjectActivities(transaction.getProjectId(),"幸运女神", value.toString(),luckTransaction.getMemberId());
				logger.info("项目：" + luckTransaction.getProjectId() + "幸运女神获得者：" + luckTransaction.getMemberId());
				// 清空缓存
				RedisForProjectClient.clearTransactionDetail(luckTransaction.getProjectId());
				String key = RedisConstant.TRANSACTION_INVERESTLIST_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + transaction.getProjectId();
				RedisManager.removeObject(key);
				//幸运女神赠送人气值
				MessageClient.sendMsgForCommon(luckTransaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_FIVE_REWARD.getCode(), 
						project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName(),"幸运女神",value.toString());
			}
		} catch (Exception e) {
			logger.error("幸运女神赠送奖励失败");
		}
	}

	@Override
	public void fiveRitesTest(Transaction transaction, int investType) {
		try {
			fiveRites(transaction,investType);
		} catch (Exception e) {
			logger.error("五重礼赠送奖励失败");
		}
	}
	
	/**
	 * 庆祝奥运活动
	 * @param transaction
	 */
	private void celebrateOlympic(Transaction transaction) {
		try {
			Optional<Activity> celebrolympic = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.ACTIVITY_CELEBRATE_OLYMPIC_NAME);
			if (!celebrolympic.isPresent()) {
				return;
			}
			Activity celebrolympicInvest = celebrolympic.get();
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != celebrolympicInvest.getActivityStatus()) {
				return;
			}
			logger.info("庆祝奥运活动发放奖励, activityId={}, memberId={}, ", celebrolympicInvest.getId(), transaction.getMemberId());
			// 活动仅限于移动端
			Order order = orderManager.selectByPrimaryKey(transaction.getOrderId());
			if (TypeEnum.ORDER_SOURCE_PC.getType() == order.getOrderSource()) {
				return;
			}
			Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());
			ActivityForMillionCoupon model = LotteryContainer.getInstance().getObtainConditions(celebrolympicInvest,
					ActivityForMillionCoupon.class, ActivityConstant.ACTIVITY_CELEBRATE_OLYMPIC_KEY);
			BigDecimal investLevel = model.getMaxInvestAmount();
			//是否活动期间注册和首次投资
			if (checkNewMemberAndFirst(member, transaction, celebrolympicInvest)) {
				investLevel = model.getMinInvestAmount();
			}
			// 判断投资金额
			if (transaction.getInvestAmount().compareTo(investLevel) == -1) {
				return;
			}
			String dateStr = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3);
			// 领券
			RuleBody rb = new RuleBody();
			rb.setActivityId(celebrolympicInvest.getId());
			rb.setCycleStr(dateStr);
			rb.setMemberId(transaction.getMemberId());
			rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			rb.setRewardId(transaction.getId().toString());
			rb.setDeductValue(transaction.getInvestAmount().intValue());
			rb.setActivityName(celebrolympicInvest.getActivityName());
			rb.setExceptDrawedRewards(false);
			rb.setProbabilityByAverage(false);
			rb.setRewardsPoolMaxNum(2);
			rb.setRewardsAvailableNum(2);
			// 抽奖
			if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null,
						TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
				if (!"noReward".equals(rfp.getRewardCode())) {
					// 发送站内信
					MessageClient.sendMsgForSPEngin(transaction.getMemberId(), celebrolympicInvest.getActivityDesc(), rfp.getRewardName());
				}
			}
		} catch (Exception e) {
			logger.error("【喝彩中国喜迎G20】活动失败 transactionId={}", transaction.getId(), e);
		}
	}
	
	private boolean checkNewMemberAndFirst(Member member, Transaction transaction, Activity wechatInvest) throws ManagerException {
		// 判断注册时间在活动期间
		if (!DateUtils.isDateBetween(member.getRegisterTime(), wechatInvest.getStartTime(), wechatInvest.getEndTime())) {
			return false;
		}
		// 判断是否是用户第一次投资
		Transaction firstTransaction = transactionManager.getFirstTransaction(transaction.getMemberId());
		if (firstTransaction == null || !transaction.getId().equals(firstTransaction.getId())) {
			return false;
		}
		return true;
	}
	/**
	 * 补发五重礼
	 * @param projectId
	 * @return
	 */
	@Override
	public ResultDO<Object> sendFiveRites(Long projectId) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			if(project==null||project.getStatus()<50){
				resultDO.setResultCode(ResultCode.PROJECT_LOSE_NOT_PROJECT_STATUS_FULL);
				return resultDO;
			}
			int i=popularityInOutLogManager.countInvestBySourceId(projectId);
			if(i>0){
				resultDO.setResultCode(ResultCode.POPULARITY_FIV_RITES_HAD_SEND_ERROR);
				return resultDO;
			}
			Transaction transaction=new Transaction();
			transaction.setProjectCategory(1);
			transaction.setProjectId(projectId);
			transaction.setProjectCategory(1);
			fiveRites(transaction,project.getInvestType());
			firstInvestInProject(transaction,project.getInvestType());
			String key = RedisConstant.TRANSACTION_INVERESTLIST_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + transaction.getProjectId();
			RedisManager.removeObject(key);
		} catch (Exception e) {
			resultDO.setSuccess(false);
			logger.error("根据项目id补发五重礼失败 projectId={}", projectId, e);
		}
		return resultDO;
	}
	
	/**
	 * 
	 * @Description:七月战队
	 * @param transaction
	 * @author: chaisen
	 * @throws ManagerException 
	 * @throws Exception 
	 * @time:2016年7月1日 下午3:16:16
	 */
	private void julyTeam(Transaction transaction)  {
		Optional<Activity> teamActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_JULY_TEAM);
		if(!teamActivity.isPresent()){
			return;
		}
		Activity activity=teamActivity.get();
		if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), activity.getStartTime(), activity.getEndTime())) {
			return;
		}
		ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
				ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
		if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getReceiveStartTime()), DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getReceiveEndTime()))){
			return;
		}
		if(transaction.getInvestAmount().compareTo(julyDate.getInvestAmount())>=0){
			try {
				addLotteryRecordSetp(activity.getId(),transaction.getMemberId(),1,1,DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
			} catch (ManagerException e) {
				logger.info("九月战队发放奖励, activityId={}, memberId={}, ", activity.getId(), transaction.getMemberId());
			}
		}
		
	}
	
	private void addLotteryRecordSetp(Long activityId, Long memberId, int totalNum, int realNum, String jsonCondition) throws ManagerException {
		try {
			int i=0;
			ActivityLottery model = new ActivityLottery();
			model.setActivityId(activityId);
			model.setMemberId(memberId);
			model.setTotalCount(totalNum);
			model.setRealCount(realNum);
			model.setCycleConstraint(jsonCondition);
			if(activityLotteryMapper.checkExistLottery(model) == null) {
				//第一次获得抽奖机会
				i=activityLotteryMapper.insertSelective(model);
			} else {
				//累积抽奖机会
				i=activityLotteryMapper.updateByActivityAndMember(model);
			}
		}catch (DuplicateKeyException mysqlE) {
			throw new ManagerException("重复插入数据");
		} catch (Exception e) {
			throw e;
		}
}
	/**
	 *  国庆活动
	 * @param transaction
	 */
	@Override
	public void octoberNational(Transaction transaction) {
		try {
			Optional<Activity> october = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.ACTIVITY_NATIONAL_OCTOBER_NAME);
			if (!october.isPresent()) {
				return;
			}
			Activity octoberActivity = october.get();
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != octoberActivity.getActivityStatus()) {
				return;
			}
			ActivityForOctober model = LotteryContainer.getInstance().getObtainConditions(octoberActivity,
					ActivityForOctober.class, ActivityConstant.ACTIVITY_NATIONAL_OCTOBER_KEY);
			/**
			 * 10月1日00：00~10月7日23:59
			 */
			if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), model.getStartTime(), octoberActivity.getEndTime())) {
				octoberNationalSend( transaction, octoberActivity, model);
			}else{
				/**
				 * 9月25日00：00~9月30日23:59，成功投资一笔收益天数≥60天的项目，即可随机获得30/50元现金券一张，（现金券每人每天限领一张）。
				 * 
				 */
				Project project=projectManager.selectByPrimaryKey(transaction.getProjectId());
				if(project==null){
					return;
				}
				int days=0;
				if(project.isDirectProject()){
					days= project.countProjectDays();
				}else{
					days=transaction.getTotalDays();
				}
				if(days<model.getProjectDays()){
					return;
				}
				String dateStr = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3);
				// 领券
				RuleBody rb = new RuleBody();
				rb.setActivityId(octoberActivity.getId());
				rb.setCycleStr(transaction.getId().toString());
				rb.setMemberId(transaction.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setRewardId(transaction.getId().toString());
				rb.setDeductValue(transaction.getInvestAmount().intValue());
				rb.setActivityName(octoberActivity.getActivityName());
				rb.setExceptDrawedRewards(false);
				rb.setProbabilityByAverage(false);
				rb.setRewardsPoolMaxNum(2);
				rb.setRewardsAvailableNum(2);
				// 抽奖
				//if (drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
					RewardsBodyForProbility rfp = (RewardsBodyForProbility) drawByProbability.drawLottery(rb, null,
							TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
					if (!"noReward".equals(rfp.getRewardCode())) {
						// 发送站内信
						MessageClient.sendMsgForSPEngin(transaction.getMemberId(), octoberActivity.getActivityDesc(), rfp.getRewardName());
					}
				//}
			}
		} catch (Exception e) {
			logger.error("喜破三十亿，携手庆十一赠送现金券失败 transactionId={}", transaction.getId(), e);
		}
	}
	
	/**
	 * 10月1日00：00~10月7日23:59，用户每单笔投资满2000元，即可获得一张0.5%收益券，多投多得
	 * @param transaction
	 * @param activity
	 * @param olympicDate
	 */
	public void octoberNationalSend(Transaction transaction,Activity activity,ActivityForOctober octoberDate) {
		try {
			//单笔投资额满2000
			if(transaction.getInvestAmount().compareTo(octoberDate.getInvestAmount())>=0){
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(transaction.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setActivityName(ActivityConstant.ACTIVITY_NATIONAL_OCTOBER_NAME);
				rb.setCycleStr(transaction.getId().toString());
				//发放收益券
				CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(octoberDate.getTemplateId());
				RewardsBase rBase = new RewardsBase();
				rBase.setTemplateId(octoberDate.getTemplateId());
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(couponTemplate.getAmount().intValue() + ActivityConstant.annualizedUnit+ActivityConstant.annualizedDesc);
				rBase.setRewardValue(couponTemplate.getAmount().intValue());
				drawByPrizeDirectly.drawLottery(rb, rBase, null);
				//站内信
				MessageClient.sendMsgForSPEngin(transaction.getMemberId(), activity.getActivityDesc(), rBase.getRewardName());
				}
		} catch (Exception e) {
			logger.error("喜破三十亿，携手庆十一发送收益券失败 transactionId={}", transaction.getId(), e);
		}
	}
	
	@Override
	public void thirtyGift(Transaction transaction) {
		try {
			Optional<Activity> optOfWechatInvest = LotteryContainer.getInstance().getActivityByName(
					ActivityConstant.ACTIVITY_CELEBRATEGIFT_NAME);
			if (!optOfWechatInvest.isPresent()) {
				return;
			}
			Activity activity = optOfWechatInvest.get();
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
				return;
			}
			//转让项目不参与
			if(transaction.getProjectCategory()==2){
				return;
			}
			String date=DateUtils.getDateStrFromDate(DateUtils.getCurrentDate());
			ActivityForMillionCoupon model = LotteryContainer.getInstance().getObtainConditions(activity,
					ActivityForMillionCoupon.class, ActivityConstant.ACTIVITY_CELEBRATEGIFT_KEY);
			//是否在当天的活动时间内
			/*if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), DateUtils.getDateTimeFromString(date+" "+model.getReceiveStartTime()), DateUtils.getDateTimeFromString(date+" "+model.getReceiveEndTime()))){
				return;
			}*/
			Long templateId=0L;
			// 判断投资金额
			if (transaction.getInvestAmount().compareTo(model.getTotalAmount4000())>=0) {
				templateId=model.getTemplateId188();
			}else if(transaction.getInvestAmount().compareTo(model.getTotalAmount2000())>=0){
				templateId=model.getTemplateId88();
			}else{
				return;
			}
			String dateStr = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3);
			// 领券
			RuleBody rb = new RuleBody();
			rb.setActivityId(activity.getId());
			rb.setCycleStr(dateStr);
			rb.setMemberId(transaction.getMemberId());
			rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			rb.setRewardId(transaction.getId().toString());
			rb.setDeductValue(transaction.getInvestAmount().intValue());
			rb.setActivityName(activity.getActivityName());
			//发放现金券
			CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
			if(couponTemplate==null){
				logger.info(" 现金券模板id不存在, activityId={}, memberId={}, templateId={}, ", activity.getId(), transaction.getMemberId(),templateId);
			}
			RewardsBase rBase = new RewardsBase();
			rBase.setTemplateId(templateId);
			rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
			rBase.setRewardName(couponTemplate.getAmount().intValue() + ActivityConstant.couponDesc);
			rBase.setRewardValue(couponTemplate.getAmount().intValue());
			drawByPrizeDirectly.drawLottery(rb, rBase, null);
			//RedisActivityClient.incrSendCoupon(dateStr);
			// 发送站内信
			MessageClient.sendMsgForSPEngin(transaction.getMemberId(), activity.getActivityDesc(), rBase.getRewardName());
		} catch (Exception e) {
			logger.error("【周年庆豪礼相送】 活动失败 transactionId={}", transaction.getId(), e);
		}
	}
	
	/**
	 * 
	 * @Description:记录用户抽奖记录
	 * @param transaction
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年10月31日 下午3:43:12
	 */
	@Override
	public void directProjectLottery(Transaction transaction) {
		//directSendReward(transaction);
		try {
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if(project==null){
				return;
			}
			if(!project.isDirectProject()){
				return;
			}
			ProjectExtra pe = projectExtraMapper.getProjectActivitySign(transaction.getProjectId());
			if(pe==null){
				return;
			}
			Activity activity=activityMapper.selectByPrimaryKey(pe.getActivityId());
			if(activity==null){
				return;
			}
			activity.setProjectId(transaction.getProjectId());
			Optional<Activity> directActivity = LotteryContainer.getInstance().getActivity(activity.getId());
			if(!directActivity.isPresent()){
				return;
			}
			//募集时间超过奖励期限,不生成次数
			int totalHour=DateUtils.getTimeIntervalHours(project.getOnlineTime(),DateUtils.getCurrentDate());
			String rewardHour =projectExtraManager.getRewardHourByProjectId(transaction.getProjectId());
			logger.info("直投抽奖次数印刷, 交易ID={}, memberId={}", transaction.getId(), transaction.getMemberId());
			if(!(totalHour<Float.valueOf(rewardHour))){
				logger.info("直投抽奖超过奖励时间，不生成次数, 交易ID={}, memberId={}", transaction.getId(), transaction.getMemberId());
				return;
			}
			/*List<PrizePool> prizePoolList =projectExtraManager.getPrizePoolByProjectId(transaction.getProjectId());
			Integer maxDay = 0;
			if(Collections3.isNotEmpty(prizePoolList)){
				for (PrizePool pri : prizePoolList) {
					if (Float.parseFloat(pri.getRatio())<= 0) {
						continue;
					}
					if (maxDay < pri.getDay()) {
						maxDay = pri.getDay();
					}
				}
				if(totalDays>maxDay){
					return;
				}
			}*/
			
			//抽奖次数
			int number=transaction.getInvestAmount().divide(project.getMinInvestAmount()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			if(number>0){
				addLotteryResult(activity.getId(),transaction.getMemberId(),number,number,1,project.getId().toString(),transaction.getId());
			}
			//额外抽奖次数
			int extraNumber=0;
			Map map = JSON.parseObject(activity.getObtainConditionsJson()); 
			String lotteryJson = map.get("lottery").toString();
			List<LotteryRuleAmountNumber> instance =JSON.parseArray(lotteryJson,LotteryRuleAmountNumber.class); 
			if(Collections3.isNotEmpty(instance)){
				for(LotteryRuleAmountNumber bean :instance){
					if(transaction.getInvestAmount().intValue()>=bean.getStartAmount()&&transaction.getInvestAmount().intValue()<=bean.getEndAmount()){
						extraNumber=bean.getNumber();
						break;
					}
				}
				if(transaction.getInvestAmount().intValue()>=instance.get(instance.size()-1).getStartAmount().intValue()){
					extraNumber=instance.get(instance.size()-1).getNumber();
				}
			}
			if(extraNumber>0){
				addLotteryResult(activity.getId(),transaction.getMemberId(),extraNumber,extraNumber,2,project.getId().toString(),transaction.getId());
			}
			
		} catch (Exception e) {
			logger.error("交易完成之后记录用户抽奖次数和记录失败, transactionId={}", transaction.getId(), e);
		}
		
	}
	
	private int addLotteryResult(Long activityId,Long memberId,int totalNum,int realNum,int type,String json,Long transactionId){
		int i=0;
		try {
			i = addLotteryRecord(activityId,memberId,totalNum,realNum,json);
			if(i>0){
				for(int j=0;j<totalNum;j++){
					ActivityLotteryResult record=new ActivityLotteryResult();
					record.setActivityId(activityId);
					record.setMemberId(memberId);
					record.setStatus(0);
					if(type==1){
						record.setLotteryStatus(0);
						record.setRewardType(5);
					}else{
						record.setLotteryStatus(0);
						record.setRewardType(6);
						//record.setRewardInfo("额外次数");
					}
					record.setRemark(json);
					record.setCycleStr(json);
					record.setRewardId(transactionId.toString());
					activityLotteryResultMapper.insertSelective(record);
				}
			}
		} catch (ManagerException e) {
			logger.error("生成抽奖记录失败, transactionId={},memberId={},activityId={}", transactionId,memberId,activityId, e);
		}
		return i;
	}
	
	/**
	 * 
	 * @Description:周年庆记录领取次数
	 * @param transaction
	 * @author: chaisen
	 * @time:2016年10月17日 下午5:55:36
	 */
	@Override
	public void anniversary(Transaction transaction) {
		
		Optional<Activity> annivActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_NAME);
		if(!annivActivity.isPresent()){
			return;
		}
		Activity activity = annivActivity.get();
		ActivityForAnniversaryCelebrate model = LotteryContainer.getInstance().getObtainConditions(activity,
				ActivityForAnniversaryCelebrate.class, ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_KEY);
		try {
			//单笔投资额满2000
			if(transaction.getInvestAmount().compareTo(model.getInvestAmount())>=0){
				String cycleStr=activity.getId()+":"+transaction.getMemberId()+":coupon";
				addLotteryRecord(activity.getId(),transaction.getMemberId(),1,1,cycleStr);
			}
			//新注册用户首次投资满即送
			Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());
			Long templateId=0L;
			// 判断是否是用户第一次投资
			if (checkNewRegisterMember(member, transaction, activity)) {
				for(int i=0;i<model.getFirstInvestAmount().size();i++){
					if(transaction.getInvestAmount().intValue()>=model.getFirstInvestAmount().get(i).intValue()){
						templateId=model.getFirstTemplateId().get(i);
						break;
					}
				}
				//发放现金券
				String cycleStr=activity.getId()+":"+transaction.getMemberId()+":first";
				RuleBody rb = new RuleBody();
				rb.setActivityId(activity.getId());
				rb.setMemberId(transaction.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
				rb.setCycleStr(cycleStr);
				rb.setDeductRemark(cycleStr);
				if(couponTemplate==null){
					logger.info("四季变换，有你相伴  现金券模板id不存在, activityId={}, memberId={}, templateId={}, ", activity.getId(), transaction.getMemberId(),templateId);
				}
				RewardsBase rBase = new RewardsBase();
				rBase.setTemplateId(templateId);
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(couponTemplate.getAmount().intValue() + ActivityConstant.couponDesc);
				rBase.setRewardValue(couponTemplate.getAmount().intValue());
				drawByPrizeDirectly.drawLottery(rb, rBase, null);
				//发送短信
				String message = MessageFormat.format(ActivityConstant.ACTIVITY_ANNIVERSARY_CELEBRATE_SEND,couponTemplate.getAmount().intValue());
				MessageClient.sendShortMessageByMobile(member.getMobile(), message);
			}
		
		} catch (Exception e) {
			logger.error("【四季变换，有你相伴】 获取领取机会一次失败 transactionId={}", transaction.getId(), e);
		}
	}
	
	private boolean checkNewRegisterMember(Member member, Transaction transaction, Activity wechatInvest) throws ManagerException {
		// 判断注册时间在活动期间
		if (!DateUtils.isDateBetween(member.getRegisterTime(), wechatInvest.getStartTime(), wechatInvest.getEndTime())) {
			return false;
		}
		// 判断是否是用户第一次投资
		Transaction firstTransaction = transactionManager.getFirstTransaction(transaction.getMemberId());
		if (firstTransaction == null || !transaction.getId().equals(firstTransaction.getId())) {
			return false;
		}
		return true;
	}
	
	@Override
	public void october(Transaction transaction) {
		//directSendPopularValue(transaction);
		Optional<Activity> october = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_OCTOBER_NAME);
		if (!october.isPresent()) {
			return;
		}
		Activity octoberActivity = october.get();
		 
		ActivityForOctober model = LotteryContainer.getInstance().getObtainConditions(octoberActivity,
				ActivityForOctober.class, ActivityConstant.ACTIVITY_OCTOBER_KEY);
		
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != octoberActivity.getActivityStatus()) {
			return;
		}
		//转让项目不参与五重礼
		if(transaction.getProjectCategory()==2){
			return;
		}
		try {
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if(project==null){
				return;
			}
			int days=0;
			if(project.isDirectProject()){
				days= project.countProjectDays();
			}else{
				days=transaction.getTotalDays();
			}
			//单笔投资大于2000送收益券一张
			Long templateId=0L;
			RuleBody rb = new RuleBody();
			if(transaction.getInvestAmount().compareTo(model.getInvestAmount())>=0){
				if(days>=model.getProjectDay().get(0)&&days<model.getProjectDay().get(1)){
					templateId=model.getTemplateIds().get(0);
				}else if(days>=model.getProjectDay().get(1)&&days<model.getProjectDay().get(2)){
					templateId=model.getTemplateIds().get(1);
				}else if(days>=model.getProjectDay().get(2)&&days<model.getProjectDay().get(3)){
					templateId=model.getTemplateIds().get(2);
				}else if(days>=model.getProjectDay().get(3)){
					templateId=model.getTemplateIds().get(3);
				}
				rb.setActivityId(octoberActivity.getId());
				rb.setMemberId(transaction.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setActivityName(ActivityConstant.ACTIVITY_OCTOBER_NAME);
				rb.setCycleStr(transaction.getId().toString());
				//发放收益券
				CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
				if(couponTemplate==null){
					logger.info("金秋狂欢季，领走iPhone7  收益券模板id不存在, activityId={}, memberId={}, templateId={}, ", octoberActivity.getId(), transaction.getMemberId(),templateId);
				}else{
					RewardsBase rBase = new RewardsBase();
					rBase.setTemplateId(templateId);
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
					rBase.setRewardName(couponTemplate.getAmount() + ActivityConstant.annualizedUnit+ActivityConstant.annualizedDesc);
					try {
						drawByPrizeDirectly.drawLottery(rb, rBase, null);
					} catch (Exception e) {
						logger.error("【金秋狂欢季，领走iPhone7】 发送收益券失败 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
					}
					//站内信
					MessageClient.sendMsgForSPEngin(transaction.getMemberId(), octoberActivity.getActivityDesc(), rBase.getRewardName());
				}
			}
			//直投单笔满5000 送现金券一张
			if(project.isDirectProject()){
				Long templateIdCoupon=0L;
				if(transaction.getInvestAmount().compareTo(model.getFourInvestAmount())>=0){
					templateIdCoupon=model.getTemplateIds().get(5);
				}else if(transaction.getInvestAmount().compareTo(model.getDirectInvestAmount())>=0){
					templateIdCoupon=model.getTemplateIds().get(4);
				}
					//发放现金券
					CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateIdCoupon);
					if(couponTemplate!=null){
						RewardsBase rBase = new RewardsBase();
						rBase.setTemplateId(templateIdCoupon);
						rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
						rBase.setRewardName(couponTemplate.getAmount().intValue() + ActivityConstant.couponDesc);
						rBase.setRewardValue(couponTemplate.getAmount().intValue());
						try {
							drawByPrizeDirectly.drawLottery(rb, rBase, null);
							//站内信
							MessageClient.sendMsgForSPEngin(transaction.getMemberId(), octoberActivity.getActivityDesc(), rBase.getRewardName());
						} catch (Exception e) {
							logger.error("【金秋狂欢季，领走iPhone7】 发送现金券失败 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
						}
					}
				}
			if(days<model.getTotalDays()){
				return;
			}
			ActivityLotteryResult alr=new ActivityLotteryResult();
			alr.setActivityId(octoberActivity.getId());
			alr.setMemberId(transaction.getMemberId());
			alr.setRewardType(5);
			alr.setRemark(transaction.getId().toString());
			List<ActivityLotteryResult>  list=activityLotteryResultMapper.getLotteryResultBySelective(alr);
			if(Collections3.isNotEmpty(list)){
				return;
			}
			activityLotteryResultMapper.insertSelectiveS(alr);
		} catch (ManagerException e) {
			logger.error("【金秋狂欢季，领走iPhone7】  transactionId={}", transaction.getId(), e);
		}
	}
	
	private void directSendPopularValue(Transaction transaction) {
		Optional<Activity> october = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_OCTOBER_NAME);
		if (!october.isPresent()) {
			return;
		}
		Activity octoberActivity = october.get();
		
		ActivityForOctober model = LotteryContainer.getInstance().getObtainConditions(octoberActivity,
				ActivityForOctober.class, ActivityConstant.ACTIVITY_OCTOBER_KEY);
		
		/*if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != octoberActivity.getActivityStatus()) {
			return;
		}*/
		if(!DateUtils.isDateBetween(transaction.getTransactionTime(), octoberActivity.getStartTime(), octoberActivity.getEndTime())){
			return;
		}
		//转让项目不参与
		if(transaction.getProjectCategory()==2){
			return;
		}
		/*if(transaction.getInvestAmount().compareTo(model.getDirectInvestAmount())<0){
			return;
		}*/
		try {
		Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
		if(project==null){
			return;
		}
		//int days=DateUtils.getIntervalDays(transaction.getTransactionTime(), DateUtils.getCurrentDate());
		
		Audit audit = auditManager.selectByProcessId(transaction.getProjectId());
		int days = 0 ;
		if(audit==null||audit.getAuditTime()==null){
			 days=DateUtils.getIntervalDays(DateUtils.formatDate(transaction.getTransactionTime(), DateUtils.DATE_FMT_3),
						DateUtils.formatDate(project.getSaleComplatedTime(), DateUtils.DATE_FMT_3))+1;
		}else{
			 days=DateUtils.getIntervalDays(DateUtils.formatDate(transaction.getTransactionTime(), DateUtils.DATE_FMT_3),
						DateUtils.formatDate(audit.getAuditTime(), DateUtils.DATE_FMT_3))+1;
		}
		
		if(days==0){
			return;
		}
		int popularityValue=0;
		if(days==model.getDays().get(0)){
			popularityValue= model.getPopularityValue().get(0).multiply(transaction.getInvestAmount()).intValue();
		}else if(days==model.getDays().get(1)){
			popularityValue= model.getPopularityValue().get(1).multiply(transaction.getInvestAmount()).intValue();
		}else if(days==model.getDays().get(2)){
			popularityValue= model.getPopularityValue().get(2).multiply(transaction.getInvestAmount()).intValue();
		}else if(days==model.getDays().get(3)){
			popularityValue= model.getPopularityValue().get(3).multiply(transaction.getInvestAmount()).intValue();
		}else if(days==model.getDays().get(4)){
			popularityValue= model.getPopularityValue().get(4).multiply(transaction.getInvestAmount()).intValue();
		}else if(days==model.getDays().get(5)){
			popularityValue= model.getPopularityValue().get(5).multiply(transaction.getInvestAmount()).intValue();
		}
		if(popularityValue<=0){
			return;
		}
		//送人气值
		RewardsBase rBase = new RewardsBase();
		RuleBody rb = new RuleBody();
		rBase.setRewardName(popularityValue + ActivityConstant.popularityDesc);
		rBase.setRewardValue(popularityValue);
		rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
		rb.setDeductRemark(octoberActivity.getActivityDesc());
		rb.setMemberId(transaction.getMemberId());
		rb.setActivityId(octoberActivity.getId());
		rb.setActivityName(octoberActivity.getActivityDesc());
			drawByPrizeDirectly.drawLottery(rb, rBase, "");
			//站内信
			MessageClient.sendMsgForSPEngin(transaction.getMemberId(), octoberActivity.getActivityDesc(), rBase.getRewardName());
		} catch (Exception e) {
			logger.error("【金秋狂欢季】 发送人气值失败 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
		}
	}
	
	/**
	 * 
	 * @Description:双旦活动
	 * @param transaction
	 * @author: chaisen
	 * @time:2016年12月6日 下午5:27:26
	 */
	public void doubleActivity(Transaction transaction) {
		Optional<Activity> doubleDan = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_DOUBLE_NAME);
		if (!doubleDan.isPresent()) {
			return;
		}
		Activity doubleActivity = doubleDan.get();
		 
		ActivityForDouble model = LotteryContainer.getInstance().getObtainConditions(doubleActivity,
				ActivityForDouble.class, ActivityConstant.ACTIVITY_DOUBLE_KEY);
		
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != doubleActivity.getActivityStatus()) {
			return;
		}
		if(transaction.getProjectCategory()==2){
			return;
		}
		try {
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if(project==null){
				return;
			}
			//单笔投资大于2000送现金券一张
			Long templateId=0L;
			RuleBody rb = new RuleBody();
			if(transaction.getInvestAmount().intValue()>=model.getInvestAmounts().get(0).intValue()&&transaction.getInvestAmount().intValue()<model.getInvestAmounts().get(1).intValue()){
				templateId=model.getTemplates().get(0);
			}else if(transaction.getInvestAmount().intValue()>=model.getInvestAmounts().get(1).intValue()&&transaction.getInvestAmount().intValue()<model.getInvestAmounts().get(2).intValue()){
				templateId=model.getTemplates().get(1);
			}else if(transaction.getInvestAmount().intValue()>=model.getInvestAmounts().get(2).intValue()&&transaction.getInvestAmount().intValue()<model.getInvestAmounts().get(3).intValue()){
				templateId=model.getTemplates().get(2);
			}else if(transaction.getInvestAmount().intValue()>=model.getInvestAmounts().get(3).intValue()){
				templateId=model.getTemplates().get(3);
			}else{
				return;
			}
				rb.setActivityId(doubleActivity.getId());
				rb.setMemberId(transaction.getMemberId());
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
				rb.setActivityName(ActivityConstant.ACTIVITY_DOUBLE_NAME);
				rb.setCycleStr(transaction.getId().toString());
				//发放现金券
				CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(templateId);
				if(couponTemplate==null){
					logger.info("【双旦狂欢惠】  优惠券模板id不存在, activityId={}, memberId={}, templateId={}, ", doubleActivity.getId(), transaction.getMemberId(),templateId);
				}else{
					RewardsBase rBase = new RewardsBase();
					rBase.setTemplateId(templateId);
					rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
					rBase.setRewardName(couponTemplate.getAmount().intValue() + ActivityConstant.couponDesc);
					rBase.setRewardValue(couponTemplate.getAmount().intValue());
					try {
						drawByPrizeDirectly.drawLottery(rb, rBase, null);
					} catch (Exception e) {
						logger.error("【双旦狂欢惠】 发送现金券失败 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
					}
					//站内信
					MessageClient.sendMsgForSPEngin(transaction.getMemberId(), doubleActivity.getActivityDesc(), rBase.getRewardName());
				}
		} catch (ManagerException e) {
			logger.error("【双旦狂欢惠】  transactionId={}", transaction.getId(), e);
		}
	}
	
	@Override
	public void sumTotalFirstInvestAmount(Transaction transaction) {
		
		Optional<Activity> doubleDan = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_DOUBLE_NAME);
		if (!doubleDan.isPresent()) {
			return;
		}
		Activity doubleActivity = doubleDan.get();
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != doubleActivity.getActivityStatus()) {
			return;
		}
		//转让项目不参与
		if(transaction.getProjectCategory()==2){
			return;
		}
		String dateStr = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3);
		String key=RedisConstant.ACTIVITY_DOUBLE_DAN_INVESTAMOUNT + RedisConstant.REDIS_SEPERATOR + doubleActivity.getId() + RedisConstant.REDIS_SEPERATOR + dateStr;
		RedisManager.removeString(key);
		List<ActivityFirstInvest> listFirstInvestAmount=transactionMapper.selectTopInvestAmountByActivityIdAndTime(DateUtils.zerolizedTime(DateUtils.getCurrentDate()),DateUtils.getEndTime(DateUtils.getCurrentDate()),doubleActivity.getStartTime());
		RedisManager.setnx(key, JSON.toJSONString(listFirstInvestAmount));
		//String list=RedisManager.get(key);
		
	}

	/**
	 * 福袋优惠券使用后补发一张
	 * @param transaction
     */
	public void newyearLuckyBag(Transaction transaction){
		Optional<Activity> newyear = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_NEWYEAR);
		if (!newyear.isPresent()) {
			return;
		}

		Activity newyearActivity = newyear.get();

		ActivityForNewYear model = LotteryContainer.getInstance().getObtainConditions(newyearActivity,
				ActivityForNewYear.class, ActivityConstant.ACTIVITY_NEWYEAR_KEY);

		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != newyearActivity.getActivityStatus()) {
			return;
		}

		Long templateId=null;
		try {
			Order order= orderManager.selectByPrimaryKey(transaction.getOrderId());
			Coupon coupon= couponManager.getCouponByCouponNo(order.getProfitCouponNo());
			if (coupon!=null){
				model.getLuckyMoneyTemplateIds();
				String[] strings= model.getLuckyBagTemplateIds().split(",");
				for (String s:strings) {
					if (!StringUtils.isEmpty(s)){
						if (s.equals(coupon.getCouponTemplateId().toString())){
							templateId=coupon.getCouponTemplateId();
							break;
						}

					}
				}
			}
		} catch (ManagerException e) {
			e.printStackTrace();
		}

		if (templateId==null){
			return;
		}

		// 发放奖励
		RuleBody rb = new RuleBody();
		rb.setActivityId(newyearActivity.getId());
		rb.setMemberId(transaction.getMemberId());
		rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
		rb.setCycleStr("");
		RewardsBase rBase = new RewardsBase();
		CouponTemplate c = null;
		try {
			c = couponTemplateManager.selectByPrimaryKey(templateId);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		if(c==null){
			return;
		}
		rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
		rBase.setRewardName(c.getAmount() + ActivityConstant.annualizedUnit + ActivityConstant.annualizedDesc);
		rBase.setTemplateId(templateId);
		rb.setRewardId(templateId.toString());

		try {
			drawByPrizeDirectly.drawLottery(rb, rBase,"");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 鸡年大吉，喜领压岁钱,使用优惠券后可重新领取
	 */
	public void newyearLuckyMoney(Transaction transaction){
		Optional<Activity> newyear = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_NEWYEAR);
		if (!newyear.isPresent()) {
			return;
		}

		Activity newyearActivity = newyear.get();

		ActivityForNewYear model = LotteryContainer.getInstance().getObtainConditions(newyearActivity,
				ActivityForNewYear.class, ActivityConstant.ACTIVITY_NEWYEAR_KEY);

		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != newyearActivity.getActivityStatus()) {
			return;
		}

		Long truetemplateId=null;
		try {
			Order order= orderManager.selectByPrimaryKey(transaction.getOrderId());
			Coupon coupon= couponManager.getCouponByCouponNo(order.getCashCouponNo());
			if (coupon!=null){
				model.getLuckyMoneyTemplateIds();
				String[] strings= model.getLuckyMoneyTemplateIds().split(",");
				for (String s:strings) {
					if (!StringUtils.isEmpty(s)){
						if (s.equals(coupon.getCouponTemplateId().toString())){
							truetemplateId=coupon.getCouponTemplateId();
							break;
						}

					}
				}
			}
		} catch (ManagerException e) {
			e.printStackTrace();
		}

		String cycleConstraint = newyearActivity.getId() + "-" + transaction.getMemberId() + "-" + truetemplateId+":LuckyMoney";
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("activityId", newyearActivity.getId());
		paraMap.put("memberId", transaction.getMemberId());
		paraMap.put("cycleConstraint", cycleConstraint);
		if (!DateUtils.isDateBetween(transaction.getTransactionTime(),newyearActivity.getStartTime(),newyearActivity.getEndTime())){
			return;
		}

		ActivityLottery al = null;
		try {
			al = activityLotteryManager.selectByMemberActivity(paraMap);
		} catch (ManagerException e) {
			logger.error("【鸡年新年大吉】  transactionId={}", transaction.getId(), e);
			return;
		}
		if (al != null) {
			try {
				al.setTotalCount(1);
				al.setRealCount(1);
				//优惠券使用完增加一次领取机会
				activityLotteryMapper.updateByActivityAndMember(al);
			} catch (Exception e) {
				logger.error("【鸡年新年大吉】  transactionId={}", transaction.getId(), e);
			}
		}
	}

	/**
	 * 投资返人气
	 * @param transaction
     */
	public void newyearSendReward(Transaction transaction){
		Optional<Activity> newyear = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_NEWYEAR);
		if (!newyear.isPresent()) {
			logger.error("【鸡年新年大吉】 投资返人气异常 transactionId={}", transaction.getId());
			return;
		}

		Activity newyearActivity = newyear.get();

		ActivityForNewYear activityForNewYear = LotteryContainer.getInstance().getObtainConditions(newyearActivity,
				ActivityForNewYear.class, ActivityConstant.ACTIVITY_NEWYEAR_KEY);

		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != newyearActivity.getActivityStatus()) {
			return;
		}
		if(transaction.getProjectCategory()==2){
			return;
		}


		if (newyearActivity.getStartTime().after(new Date())){
			logger.error("【鸡年新年大吉】 投资返人气异常时间不对 transactionId={}", transaction.getId());
			return;
		}
		if (newyearActivity.getEndTime().before(new Date())){
			logger.error("【鸡年新年大吉】 投资返人气异常时间不对 transactionId={}", transaction.getId());
			return;
		}

		Member member=new Member();
		try {
			member=memberManager.selectByPrimaryKey(transaction.getMemberId());
		} catch (ManagerException e) {
			logger.error("查询用户异常"+e.toString());
		}
		//活动期间注册用户
		if (member.getRegisterTime().after(newyearActivity.getStartTime())){
			restorePopularity(transaction,newyearActivity);
			logger.error("【鸡年新年大吉】 投资返人气活动期间注册用户 transactionId={}", transaction.getId());
			return;
		}
		int transcount= transactionMapper.queryMemberTransactionCount(transaction.getMemberId(),DateUtils.getDateFromString("2016-10-01 00:00:00","yyyy-MM-dd HH:mm:ss"),
				newyearActivity.getStartTime());
		//2016年10月份之后未投资老用户
		if (transcount<1){
			restorePopularity(transaction,newyearActivity);
			logger.error("【鸡年新年大吉】 投资返人气2016年10月份之后未投资老用户 transactionId={}", transaction.getId());
			return;
		}
		//活动期间邀请用户注册并完成投资用户数
		int referralCount= memberMapper.queryMemberReferralAndTransactionCount(transaction.getMemberId(),newyearActivity.getStartTime(),newyearActivity.getEndTime());
		//活动期间成功邀请5位及以上好友完成首投
		if (referralCount>4){
			restorePopularity(transaction,newyearActivity);
			logger.error("【鸡年新年大吉】 投资返人气活动期间成功邀请5位及以上好友完成首投 transactionId={}", transaction.getId());
			return;
		}
	}

	/**
	 * 鸡年新年活动返还人气值
	 * @param transaction
	 * @param newyearActivity
     */
	private void restorePopularity(Transaction transaction,Activity newyearActivity){
		int popularityValue= 0;
		try {
			popularityValue = transaction.getInvestAmount().multiply(new BigDecimal("0.001")).multiply(new BigDecimal(transaction.getTotalDays())).divide(new BigDecimal(30),2).intValue();
		} catch (Exception e) {
			logger.error("【鸡年新年活动】 发送人气值计算失败 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
		}
		//送人气值
		if (popularityValue>0){
			try {
				RewardsBase rBase = new RewardsBase();
				RuleBody rb = new RuleBody();
				rBase.setRewardName(popularityValue + ActivityConstant.popularityDesc);
				rBase.setRewardValue(popularityValue);
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
				rb.setDeductRemark(newyearActivity.getActivityDesc());
				rb.setMemberId(transaction.getMemberId());
				rb.setActivityId(newyearActivity.getId());
				rb.setActivityName(newyearActivity.getActivityDesc());
				rb.setCycleStr(newyearActivity.getId()+"-"+transaction.getMemberId()+":Restore");
				drawByPrizeDirectly.drawLottery(rb, rBase, "");
				//站内信
				//MessageClient.sendMsgForSPEngin(transaction.getMemberId(), newyearActivity.getActivityDesc(), rBase.getRewardName());
			} catch (Exception e) {
				logger.error("【鸡年新年活动】 发送人气值失败 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
			}
		}
	}
	
	/**
	 * 50亿活动投资送抽奖券
	 * @param transaction
	 */
	@Override
	public void fiveBillionLuck(Transaction transaction) {
		Optional<Activity> fiveBillionActivity = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_FIVEBILLION_NAME);
		if (!fiveBillionActivity.isPresent()) {
			return;
		}
		Activity fiveActivity = fiveBillionActivity.get();
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != fiveActivity.getActivityStatus()) {
			return;
		}
		ActivityForFiveBillion fiveBillion = LotteryContainer.getInstance().getObtainConditions(fiveActivity, ActivityForFiveBillion.class,
				ActivityConstant.ACTIVITY_FIVEBILLION_KEY);
		if(fiveBillion==null){
			return;
		}
		try {
			Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());
			if(member==null){
				return;
			}
			//转让项目不参与
			if(transaction.getProjectCategory()==2){
				return;
			}
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if(project==null){
				return;
			}
			int days=0;
			if(project.isDirectProject()){
				days= project.countProjectDays();
			}else{
				days=transaction.getTotalDays();
			}
			if(days<fiveBillion.getDays()){
				return;
			}
			BigDecimal investAmount=BigDecimal.ZERO;
			//活动期间注册的用户
			if(DateUtils.isDateBetween(member.getRegisterTime(), fiveActivity.getStartTime(), fiveActivity.getEndTime())) {
				if(transaction.getInvestAmount().compareTo(fiveBillion.getFirstRegisterAmount())<0){
					return;
				}
				investAmount=fiveBillion.getFirstRegisterAmount();
			}else{
				if(transaction.getInvestAmount().compareTo(fiveBillion.getInvestAmount())<0){
					return;
				}
				investAmount=fiveBillion.getInvestAmount(); 
			}
			//抽奖次数
			int number=transaction.getInvestAmount().divide(investAmount).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			//发送抽奖券
			ActivityLotteryResultNew alr=new ActivityLotteryResultNew();
			alr.setActivityId(fiveActivity.getId());
			alr.setMemberId(transaction.getMemberId());
			alr.setRewardInfo("一张抽奖券");
			alr.setRewardType(5);
			alr.setRewardId(transaction.getId().toString());
			alr.setStatus(1);
			alr.setRemark("luckLotteryBag");
			alr.setCycleStr("luckLotteryBag");
			//int i=activityLotteryResultMapper.countNewLotteryResult(alr);
			//if(i>0){
			//	return;
			//}
			alr.setLotteryStatus(0);
			if(number<1){
				return;
			}
			for(int i=0;i<number;i++){
				activityLotteryResultNewMapper.insertSelective(alr);
			}
		} catch (ManagerException e) {
			logger.error("【福临50亿】 送抽奖券失败 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
		}
		
		
		
	}

	/**
	 * 38节投资累计送券
	 * @param transaction
     */
	public void womensDay(Transaction transaction){
		Optional<Activity> newyear = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_WOMENSDAY);
		if (!newyear.isPresent()) {
			return;
		}

		Activity activity = newyear.get();

		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
			return;
		}

		//转让项目不参与
		if(transaction.getProjectCategory()==2){
			return;
		}

		ActivityForWomensDay activityForWomensDay = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForWomensDay.class,
				ActivityConstant.ACTIVITY_WOMENSDAY_KEY);

		BigDecimal invest= transactionMapper.getMemberTotalInvestNoTransfer(transaction.getMemberId(),DateUtils.getDateFromString(DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd HH:mm:ss")),
				DateUtils.getDateFromString(DateUtils.getStrFromDate(DateUtils.addDate(new Date(),1),"yyyy-MM-dd HH:mm:ss")));
		String str= activityForWomensDay.getTransactionTemplateIds();
		String[] transactionTemplateIds= str.split(",");
		for (String s:transactionTemplateIds) {
			if (!StringUtil.isBlank(s)){
				String[] tid= s.split("_");
				if (invest.compareTo(new BigDecimal(tid[0]))>=0){
					womensDaySendCoupon(activity.getId(),activity.getActivityDesc(),transaction.getMemberId(),tid[1]);
				}
			}

		}
	}

	private void womensDaySendCoupon(Long activityId,String activityDes,Long memberId,String templateId){
		try {
			String cycleConstraint = activityId + "-" + templateId + "-"+DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd")+":WomensDayTransaction";
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("activityId", activityId);
			paraMap.put("memberId", memberId);
			paraMap.put("cycleConstraint", cycleConstraint);
			ActivityLottery al = activityLotteryManager.selectByMemberActivity(paraMap);
			if (al==null){
				// 发放奖励
				RuleBody rb = new RuleBody();
				rb.setActivityId(activityId);
				rb.setMemberId(memberId);
				rb.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_COIN.getStatus());
				rb.setCycleStr("");
				RewardsBase rBase = new RewardsBase();
				CouponTemplate c = null;
				try {
					c = couponTemplateManager.selectByPrimaryKey(Long.parseLong(templateId));
				} catch (ManagerException e) {
					e.printStackTrace();
				}
				if(c==null){
					return;
				}
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(c.getAmount() + ActivityConstant.couponDesc);
				rBase.setTemplateId(Long.parseLong(templateId));
				rb.setRewardId(templateId);
				try {
					drawByPrizeDirectly.drawLottery(rb, rBase,"");
					//站内信
					MessageClient.sendMsgForSPEngin(memberId, activityDes, rBase.getRewardName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				//记录已领取
				ActivityLottery model = new ActivityLottery();
				model.setActivityId(activityId);
				model.setMemberId(memberId);
				model.setTotalCount(1);
				model.setRealCount(1);
				model.setCycleConstraint(cycleConstraint);
				activityLotteryMapper.insertSelective(model);
			}
		} catch (ManagerException e) {
			logger.error("【女神节，绽放你的美】投资累计满额送现金券异常  memberId={},templateId={}", memberId,templateId, e);
		}
	}
	
	/**
	 * 天降金喜
	 * @param transaction
	 */
	@Override
	public void dayDropGold(Transaction transaction) {
		logger.info("【天降金喜】 活动投资回调开始 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId());
		Optional<Activity> dayActivity = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_DAY_DROP_GOLD_NAME);
		if (!dayActivity.isPresent()) {
			return;
		}
		Activity activity = dayActivity.get();
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
			return;
		}
		ActivityForDayDrop dayDrop = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForDayDrop.class,
				ActivityConstant.ACTIVITY_DAY_DROP_GOLD_KEY);
		if(dayDrop==null){
			return;
		}
		try {
			//转让项目不参与
			if(transaction.getProjectCategory()==2){
				return;
			}
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if(project==null){
				return;
			}
			//记录用户投资大于3个月的项目
			int days=0;
			if(project.isDirectProject()){
				days= project.countProjectDays();
			}else{
				days=transaction.getTotalDays();
			}
			String dateStr = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3);
			if(days>=dayDrop.getTotalDays()){
				ActivityLotteryResultNew alr=new ActivityLotteryResultNew();
				alr.setActivityId(activity.getId());
				alr.setMemberId(transaction.getMemberId());
				alr.setRewardType(5);
				alr.setRewardId(transaction.getId().toString());
				alr.setStatus(1);
				alr.setChip(transaction.getInvestAmount().intValue());
				alr.setRewardInfo("investAmount");
				alr.setRemark(activity.getId()+":"+dateStr);
				alr.setCycleStr("dayDropGold");
				int i=activityLotteryResultNewMapper.insertSelective(alr);
				if(i>0){
					//是否领取过
					RuleBody rb = new RuleBody();
					rb.setActivityId(activity.getId());
					rb.setMemberId(transaction.getMemberId());
					rb.setCycleStr(transaction.getMemberId()+"-totalAmount:"+dateStr);
					if(drawByProbability.validate(rb, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
						Long templateId=dayDrop.getTemplateId();
						//发送收益券
						ActivityLotteryResultNew newA=new ActivityLotteryResultNew();
						newA.setRemark(activity.getId()+":"+dateStr);
						newA.setActivityId(activity.getId());
						newA.setMemberId(transaction.getMemberId());
						BigDecimal totalAmount=activityLotteryResultNewMapper.sumRewardInfoByProjectId(newA);
						if(totalAmount!=null&&totalAmount.compareTo(dayDrop.getTotalInvestAmount())>=0){
							// 发放奖励
							RewardsBase rBase = new RewardsBase();
							CouponTemplate c = couponTemplateManager.selectByPrimaryKey(templateId);
							if (c!=null){
								rBase.setRewardName(c.getAmount() + ActivityConstant.annualizedUnit + ActivityConstant.annualizedDesc);
								rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_ANNUALIZED.getType());
								rBase.setTemplateId(c.getId());
								rb.setRewardId("收益券id:"+templateId);
								try {
									drawByPrizeDirectly.drawLotteryNew(rb, rBase,TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
								} catch (Exception e) {
									logger.error("【天降金喜】 发送收益券异常 memberId={}", transaction.getMemberId());
								}
							}else {
								logger.error("【天降金喜】 收益券模板不存在 templateId={}", templateId);
							}
						}
					}
				}
			}
			if(transaction.getInvestAmount().intValue()<dayDrop.getSingleInvestAmount().intValue()){
				return;
			}
			//记录次数
			addLotteryRecordSetp(activity.getId(),transaction.getMemberId(),1,1,"receiveNum");
		} catch (Exception e) {
			logger.error("【天降金喜】 送抽奖券失败 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
		}
	}
	/**
	 * 邀请好友
	 * @param transaction
	 */
	public void inviteFriend(Transaction transaction){
		Optional<Activity> inviteFriend = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_INVITEFRIENDS_NAME);
		if (!inviteFriend.isPresent()) {
			return;
		}

		Activity activity = inviteFriend.get();

		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
			return;
		}

		//转让项目不参与
		if(transaction.getProjectCategory()==2){
			return;
		}
		try {
			ActivityForInviteFriend activityForInviteFriend = null;
			Long referral = null; //推荐人id
			activityForInviteFriend = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForInviteFriend.class,
					ActivityConstant.ACTIVITY_INVITEFRIENDS_KEY);

			//查询用户活动期间累计投资额
			BigDecimal invest= transactionMapper.getMemberTotalInvestNoTransfer(transaction.getMemberId(),activityForInviteFriend.getStartDate(),
					activityForInviteFriend.getEndDate());

			if (invest.compareTo(activityForInviteFriend.getInvest())<0){
				return;
			}

			Member member= memberMapper.selectByPrimaryKey(transaction.getMemberId());
			if (activity.getStartTime().after(member.getRegisterTime()) || activity.getEndTime().before(member.getRegisterTime())){
				return;
			}

			referral = member.getReferral();
			Integer transactionCount= transactionMapper.queryMemberTransactionCountEndTime(referral,activityForInviteFriend.getTransactionDate());
			//投资时间之前没有投资的不参与
			if (transactionCount==null||transactionCount<1){
				return;
			}
			ActivityLotteryResultNew activityLotteryResultNew= activityLotteryResultNewMapper.queryLotteryResultByInviteFriend(transaction.getMemberId().toString(),
					"InviteFriendResult",referral,activity.getId());
			if (activityLotteryResultNew==null){
				activityLotteryResultNew=new ActivityLotteryResultNew();
				activityLotteryResultNew.setActivityId(activity.getId());
				activityLotteryResultNew.setMemberId(referral);
				activityLotteryResultNew.setRewardResult(invest.toString());
				activityLotteryResultNew.setRewardType(5);
				activityLotteryResultNew.setRewardId(transaction.getMemberId().toString());
				activityLotteryResultNew.setRemark("InviteFriendResult");
				activityLotteryResultNew.setStatus(1);
				activityLotteryResultNewMapper.insertSelectiveS(activityLotteryResultNew);
			}else {
				activityLotteryResultNewMapper.updateRewardResultById(invest.toString(),activityLotteryResultNew.getId());
			}
			//查询当前推荐人领取的现金券是否超过设定值
			int receiveCount= activityLotteryMapper.queryCountMemberReceiveLikeCycle(activity.getId(),referral,"InviteFriend");
			if (receiveCount >= activityForInviteFriend.getTotalReceive()){
				return;
			}
			try {
				//记录当前用户开始发放
				ActivityLottery model = new ActivityLottery();
				model.setActivityId(activity.getId());
				model.setMemberId(referral);
				model.setTotalCount(1);
				model.setRealCount(1);
				String cycleConstraint = transaction.getMemberId()+":InviteFriend";
				model.setCycleConstraint(cycleConstraint);
				activityLotteryMapper.insertSelective(model);
			} catch (Exception e) {
				logger.error("【邀请好友】活动重复发放 templateId={}", transaction.getId(), e);
				return;
			}
			RuleBody rb = null;
			RewardsBase rBase = null;
			CouponTemplate c = null;
			//给推荐人发放现金券
			rb = new RuleBody();
			rb.setActivityId(activity.getId());
			rb.setMemberId(referral);
			rb.setRewardId(transaction.getMemberId().toString());
			rb.setDeductRemark("COUPON");
			rb.setCycleStr(activity.getId() + "-" + referral + "-" + transaction.getMemberId() + ":COUPON");
			rBase = new RewardsBase();
			c = null;
			try {
				c = couponTemplateManager.selectByPrimaryKey(activityForInviteFriend.getReferralTemplateId());
			} catch (ManagerException e) {
				e.printStackTrace();
			}
			if(c==null){
				return;
			}
			rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
			rBase.setRewardName(c.getAmount() + ActivityConstant.couponDesc);
			rBase.setTemplateId(activityForInviteFriend.getReferralTemplateId());
			rBase.setRewardValue(c.getAmount().intValue());
			drawByPrizeDirectly.drawLotteryNew(rb, rBase,TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
		} catch (Exception e) {
			logger.error("【邀请好友】 活动异常 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
		}
	}

	/**
	 * 推荐人发放人气值
	 * @param transaction
     */
	public void inviteFriendSendPopularity(Transaction transaction){
		Optional<Activity> inviteFriend = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_INVITEFRIENDS_NAME);
		if (!inviteFriend.isPresent()) {
			return;
		}

		Activity activity = inviteFriend.get();

//		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
//			return;
//		}
		if (transaction.getTransactionTime().before(activity.getStartTime())||transaction.getTransactionTime().after(activity.getEndTime())){
			return;
		}

		//转让项目不参与
		if(transaction.getProjectCategory()==2){
			return;
		}

		ActivityForInviteFriend activityForInviteFriend = null;
		Long referral = null; //推荐人id
		activityForInviteFriend = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForInviteFriend.class,
				ActivityConstant.ACTIVITY_INVITEFRIENDS_KEY);

		Member member= memberMapper.selectByPrimaryKey(transaction.getMemberId());
		if (activity.getStartTime().after(member.getRegisterTime()) || activity.getEndTime().before(member.getRegisterTime())){
			return;
		}
		referral = member.getReferral();
		Integer transactionCount= transactionMapper.queryMemberTransactionCountEndTime(referral,activityForInviteFriend.getTransactionDate());
		//投资时间之前没有投资的不参与
		if (transactionCount==null||transactionCount<1){
			return;
		}
		RuleBody rb = new RuleBody();
		RewardsBase rBase = new RewardsBase();
		try {
			int popularityValue= 0;
			try {
				popularityValue = transaction.getInvestAmount().multiply(new BigDecimal("0.0001")).multiply(new BigDecimal(activityForInviteFriend.getRatio())).multiply(new BigDecimal(transaction.getTotalDays())).divide(new BigDecimal(30),2).intValue();
			} catch (Exception e) {
				logger.error("【邀请好友】 发送人气值计算异常 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
			}
			rb.setActivityId(activity.getId());
			rb.setMemberId(referral);
			rb.setRewardId(transaction.getMemberId().toString());
			rb.setDeductRemark("POPULARITY");
			rb.setActivityName(activity.getActivityDesc());
			rb.setCycleStr(activity.getId() + "-" + referral + "-" + transaction.getId() + ":POPULARITY");

			rBase.setRewardName(popularityValue + ActivityConstant.popularityDesc);
			rBase.setRewardValue(popularityValue);
			rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
			drawByPrizeDirectly.drawLotteryNew(rb, rBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
			//站内信
			//MessageClient.sendMsgForSPEngin(transaction.getMemberId(), newyearActivity.getActivityDesc(), rBase.getRewardName());
		} catch (Exception e) {
			logger.error("【邀请好友】 发送人气值失败 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
		}
	}
	
	/**
	 * 60亿活动，抢标五重礼的奖励翻6倍
	 * @return
	 * @throws Exception
	 */
	public BigDecimal sixBillionActivity(Date transDate,String popularValue) throws Exception{
		BigDecimal balanceValue = new BigDecimal(popularValue);
		Optional<Activity> activity = LotteryContainer.getInstance().getActivityByName(ActivityConstant.ACTIVITY_SIXBILLION_NAME);
		if (!activity.isPresent() || transDate == null) {
			return null;
		}
		Activity sixActivity = activity.get();

		//获取活动规则
		ActivityForSixBillion sixBillionActivity = LotteryContainer.getInstance().getObtainConditions(sixActivity, ActivityForSixBillion.class,ActivityConstant.ACTIVITY_SIXBILLION_KEY);
		if(sixBillionActivity==null){
			return null;
		}

		//是否在活动时间内投资的
		if(!DateUtils.isDateBetween(transDate,sixActivity.getStartTime(), sixActivity.getEndTime())){
			return null;
		}
		try {
			balanceValue = balanceValue.multiply(sixBillionActivity.getTurnTimes());
		} catch (Exception e) {
			logger.error("【庆60亿，抢标奖励翻"+sixBillionActivity.getTurnTimes().intValue()+"倍！】 发送人气值计算异常 ", e);
		}
		return balanceValue;
	}

	/**
	 * 51活动投资
	 * @param transaction
	 */
	public void laborInvest(Transaction transaction){
		Optional<Activity> inviteFriend = LotteryContainer.getInstance().getActivityByName(
				ActivityConstant.ACTIVITY_LABOR_NAME);
		if (!inviteFriend.isPresent()) {
			return;
		}

		Activity activity = inviteFriend.get();

		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
			return;
		}

		//转让项目不参与
		if(transaction.getProjectCategory()==2){
			return;
		}
		try {
			Map map = LotteryContainer.getInstance().getObtainConditions(activity, Map.class,
					ActivityConstant.ACTIVITY_LABOR_KEY);
			String startdate = DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + " 00:00:00";
			String enddate = DateUtils.addDays(new Date(),1,"yyyy-MM-dd") + " 00:00:00";
			BigDecimal invest = transactionMapper.getMemberTotalInvestNoTransfer(transaction.getMemberId(),DateUtils.getDateFromString(startdate),
					DateUtils.getDateFromString(enddate));
			//每日投资额大于workInvest
			BigDecimal workInvest = new BigDecimal(map.get("workInvest").toString());
			if (invest != null && invest.compareTo(workInvest)>=0){
				//记录可以领取510现金大礼包资格
				try {
					String cycleConstraint = activity.getId() + "-" + transaction.getMemberId() + "-" + DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd") + ":gifts";
					ActivityLottery model = new ActivityLottery();
					model.setActivityId(activity.getId());
					model.setMemberId(transaction.getMemberId());
					model.setTotalCount(1);
					model.setRealCount(1);
					model.setCycleConstraint(cycleConstraint);
					activityLotteryMapper.insertSelective(model);
				} catch (Exception e) {
					logger.error("【劳动最光荣】记录可以领取510现金大礼包资格异常, memberId={}", transaction.getMemberId(), e);
				}
			}
			if (DateUtils.getStrFromDate(new Date(),"yyyy-MM-dd").equals(map.get("laborDate").toString())){
				BigDecimal bigDecimal = transactionMapper.queryMemberInvestSixProjectPeriod(transaction.getMemberId(),DateUtils.getDateFromString(startdate),DateUtils.getDateFromString(enddate));
				BigDecimal honourInvest = new BigDecimal(map.get("honourInvest").toString());
				if (bigDecimal!=null && bigDecimal.compareTo(honourInvest)>=0){
					//发放51敬业奖章
					try {
						String medalConstraint = activity.getId() + "-" + transaction.getMemberId() + ":DedicatedMedal";
						ActivityLottery model = new ActivityLottery();
						model.setActivityId(activity.getId());
						model.setMemberId(transaction.getMemberId());
						model.setTotalCount(1);
						model.setRealCount(1);
						model.setCycleConstraint(medalConstraint);
						activityLotteryMapper.insertSelective(model);
					} catch (Exception e) {
						logger.error("【劳动最光荣】敬业奖章重复发放, memberId={}", transaction.getMemberId(), e);
					}
				}
			}

		} catch (Exception e) {
			logger.error("【劳动最光荣】 活动异常 transactionId={},memberId={}", transaction.getId(),transaction.getMemberId(), e);
		}
	}

}