package com.yourong.core.lottery.manager.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.RandomUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.manager.RedPackageManager;
import com.yourong.core.lottery.model.EntityForRedPackage;
import com.yourong.core.lottery.model.RuleForRedPackage;
import com.yourong.core.mc.manager.ActivityLotteryPretreatManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLotteryPretreat;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.mc.model.biz.ActivityForFiveRites;
import com.yourong.core.mc.model.biz.ActivityForRedBag;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

@Component
public class RedPackageManagerImpl implements RedPackageManager {

	private static Logger logger = LoggerFactory.getLogger(RedPackageManagerImpl.class);

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private ProjectExtraManager projectExtraManager;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private ActivityLotteryPretreatManager activityLotteryPretreatManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;

	@Override
	public ResultDO<Object> popularityRedPackage(ActivityBiz activityBiz, Transaction transaction) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			if(transaction.getProjectCategory()==2){
				rDO.setResultCode(ResultCode.REDPACKAGE_CHECK_ALLOW_TRANSFER_ERROR);
				return rDO;
			}
			String info = RedisActivityClient.getRedBagInfo(transaction.getId());
			if (StringUtil.isNotBlank(info)) {
				rDO.setResultCode(ResultCode.REDPACKAGE_CHECK_RULE_PARAMETERS_ERROR);
				return rDO;
			}
			if (StringUtil.isBlank(activityBiz.getRuleParameterJson())) {
				rDO.setResultCode(ResultCode.REDPACKAGE_CHECK_RULE_PARAMETERS_ERROR);
				return rDO;
			}
			RuleForRedPackage rule = JSON.parseObject(activityBiz.getRuleParameterJson(), RuleForRedPackage.class);
			// 校验
			checkRedPackage(rule, transaction, rDO, TypeEnum.ACTIVITY_SOURCE_TYPE_CREATE.getType());
			if (rDO.isError()) {
				return rDO;
			}
			// 根据规则生成实体类
			EntityForRedPackage entityForRedPackage = pretreatePopularityRedPackage(rule, transaction, activityBiz.getId());
			rDO.setResult(entityForRedPackage);
			return rDO;
		} catch (Exception e) {
			logger.error("创建人气值红包失败, ruleJoson={}, transactionId={}", activityBiz.getRuleParameterJson(), transaction.getId(), e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		}
	}

	/**
	 * 
	 * @Description:校验红包规则
	 * @param rule
	 * @param transaction
	 * @param rDO
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年3月17日 下午2:59:51
	 */
	@Override
	public ResultDO<Object> checkRedPackage(RuleForRedPackage rule, Transaction transaction, ResultDO<Object> rDO, int fromType)
			throws Exception {
		// 是否允许新客项目
		Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
		if (!rule.isAllowNovise()) {
			if (project.isNoviceProject()) {
				rDO.setResultCode(ResultCode.REDPACKAGE_CHECK_ALLOW_NOVISE_ERROR);
				return rDO;
			}
		}
		// 判断投资类型
		if (Collections3.isNotEmpty(rule.getAllowInvestType())) {
			List<Integer> typeList = rule.getAllowInvestType();
			if (!typeList.contains(project.getInvestType())) {
				rDO.setResultCode(ResultCode.REDPACKAGE_CHECK_ALLOW_DIRECT_PROJECT_ERROR);
				return rDO;
			}
		}
		// 是否允许特殊活动项目
		if (Collections3.isNotEmpty(rule.getNotAllowActivitySign())) {
			int sign = projectExtraManager.getProjectActivitySign(transaction.getProjectId());
			if (rule.getNotAllowActivitySign().contains(sign)) {
				rDO.setResultCode(ResultCode.REDPACKAGE_CHECK_ALLOW_ACTIVITYSIGN_ERROR);
				return rDO;
			}
		}
		// 检查最小起投金额
		if (rule.getMinInvestAmount() != null && transaction.getInvestAmount().compareTo(rule.getMinInvestAmount()) < 0) {
			rDO.setResultCode(ResultCode.REDPACKAGE_CHECK_MIN_INVEST_AMOUNT_ERROR);
			return rDO;
		}
		// 后面的校验在其他触发点的情况需要校验
		if (TypeEnum.ACTIVITY_SOURCE_TYPE_CREATE.getType() == fromType) {
			return rDO;
		}
		// 校验是否过期
		if (rule.getTimeOutFromCreate() != null && rule.getTimeOutFromCreate().intValue() > 0) {
			if (DateUtils.getTimeIntervalSencond(transaction.getTransactionTime(), DateUtils.getCurrentDate()) > rule
					.getTimeOutFromCreate()) {
				rDO.setResultCode(ResultCode.REDPACKAGE_CHECK_TIME_OUT_ERROR);
				return rDO;
			}
		}
		return rDO;
	}

	/**
	 * 
	 * @Description:人气值红包预处理
	 * @param rule
	 * @param transaction
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月17日 下午2:59:56
	 */
	private EntityForRedPackage pretreatePopularityRedPackage(RuleForRedPackage rule, Transaction transaction, Long activityId)
			throws Exception {
		EntityForRedPackage entityForRedPackage = new EntityForRedPackage();
		entityForRedPackage.setActivityId(activityId);
		entityForRedPackage.setSourceId(transaction.getId());
		// 设置人气值红包总金额
		BigDecimal totalAmount = BigDecimal.ZERO;
		Optional<Activity> optAct = LotteryContainer.getInstance().getActivityByName(ActivityConstant.FIVE_RITES);
		System.out.println("调用红包新规则开始！！！！！"); 
		if(optAct.isPresent()&&DateUtils.isDateBetween(DateUtils.getCurrentDate(), optAct.get().getStartTime(), optAct.get().getEndTime())){
			System.out.println("调用红包新规则进入！！！！！");
			Activity activity=optAct.get();
			totalAmount=getTotalRedAmount(transaction.getInvestAmount(),transaction.getTotalDays(),activity);
		}else{
			if (rule.getTotalAmount().startsWith(":=")) {
				String amountStr = rule.getTotalAmount().split(":=")[1];
				totalAmount = new BigDecimal(amountStr);
			} else {
				totalAmount = FormulaUtil.arithmeticByString(transaction.getInvestAmount(), rule.getTotalAmount());
			}
		}
		entityForRedPackage.setTotalAmount(new BigDecimal(totalAmount.intValue()));
		// 设置红包个数
		int maxNum = rule.getMaxNumber();
		if (totalAmount.intValue() < maxNum) {
			maxNum = totalAmount.intValue();
		}
		int number = RandomUtils.getRandomNumberByRange(rule.getMinNumber(), maxNum);
		if (number < rule.getMinNumber()) {
			number = rule.getMinNumber();
		}
		if (number > maxNum) {
			number = maxNum;
		}
		entityForRedPackage.setNumber(number);
		// 设置最小点数
		entityForRedPackage.setMinPopularityUnit(rule.getMinPopularityUnit());
		// 设置红包过期时间
		if (rule.getTimeOutFromCreate() != null && rule.getTimeOutFromCreate().intValue() > 0) {
			entityForRedPackage.setTimeOutSeconds(rule.getTimeOutFromCreate());
		}
		// 根据红包算法继续处理
		if (rule.getArithmeticType().equals(TypeEnum.REDPACKAGE_POPULARITY_RANDOM_ONCE.getType())) {
			createRandomPopularityRadPackage(entityForRedPackage);
		}
		return entityForRedPackage;
	}
	/**
	 * 
	 * @Description:红包新规则
	 * @param investAmount
	 * @param totalDays
	 * @param activity
	 * @return
	 * @author: chaisen
	 * @time:2016年6月12日 下午3:28:02
	 */
	public BigDecimal getTotalRedAmount(BigDecimal investAmount,int totalDays,Activity activity){
			BigDecimal totalAmount=BigDecimal.ZERO;
			BigDecimal minAmount=new BigDecimal(3);
			BigDecimal bonus=BigDecimal.ZERO;
			ActivityForFiveRites rite = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForFiveRites.class,
					ActivityConstant.FIVE_RITES_KEY);
			if(rite==null){
				return totalAmount;
			}
			if(rite.getTotalAmount().length<4){
				return totalAmount;
			}
			if(totalDays<rite.getTotalDays()[0]){
				bonus=new BigDecimal(rite.getTotalAmount()[0]);
			}else if(totalDays>=rite.getTotalDays()[0]&&totalDays<rite.getTotalDays()[1]){
				bonus=new BigDecimal(rite.getTotalAmount()[1]);
			}else if(totalDays>=rite.getTotalDays()[1]&&totalDays<rite.getTotalDays()[2]){
				bonus=new BigDecimal(rite.getTotalAmount()[2]);
			}else{
				bonus=new BigDecimal(rite.getTotalAmount()[3]);
			}
			totalAmount=investAmount.multiply(bonus);
			if(totalAmount.compareTo(minAmount)<=0){
				totalAmount=minAmount;
			}
			System.out.println("调用红包新规则人气值："+totalAmount);
		return totalAmount;
	}
	/**
	 * 
	 * @Description:随机人气值红包放入redis
	 * @param entityForRedPackage
	 * @author: wangyanji
	 * @throws ManagerException
	 * @time:2016年3月18日 上午10:43:52
	 */
	private void createRandomPopularityRadPackage(EntityForRedPackage entityForRedPackage) throws ManagerException {
		BigDecimal amount = entityForRedPackage.getTotalAmount().subtract(new BigDecimal(entityForRedPackage.getMinPopularityUnit()));
		Integer minUnit = entityForRedPackage.getMinPopularityUnit();
		int num = entityForRedPackage.getNumber();
		List<Integer> cacheList = Lists.newArrayList();
		for (int i = 0; i < num; i++) {
			BigDecimal unitAmount = null;
			if (i < num - 1) {
				unitAmount = RandomUtils.getRandomInList(amount, num - i, minUnit);
				amount = amount.subtract(unitAmount);
			} else {
				unitAmount = amount.add(new BigDecimal(minUnit));
			}
			cacheList.add(unitAmount.intValue());
		}
		if (Collections3.isEmpty(cacheList)) {
			throw new ManagerException("随机人气值红包创建失败，红包队列为空");
		}
		// push一组红包到redis
		Long retNum = RedisActivityClient.rpushRedBagValue(entityForRedPackage.getSourceId(), cacheList,
				entityForRedPackage.getTimeOutSeconds());
		RedisActivityClient.incrPopularityRedBag(entityForRedPackage.getActivityId(), retNum);
		logger.info("交易号transactionId={}, 生成红包个数={}", entityForRedPackage.getSourceId(), retNum);
	}

	@Override
	public ActivityForRedBag receivePopularityRedPackage(Long mobile, Transaction transaction, ActivityBiz activityBiz,
			RuleForRedPackage rule) {
		ActivityForRedBag model = new ActivityForRedBag();
		try {
			// 查询是否有领取记录
			ActivityLotteryPretreat query = new ActivityLotteryPretreat();
			query.setMobile(mobile);
			query.setSourceId(transaction.getId());
			List<ActivityLotteryPretreat> receiveList = activityLotteryPretreatManager.getRecordByQuery(query);
			if (Collections3.isNotEmpty(receiveList)) {
				model.setHasReceive(true);
			}
			Member member = memberManager.selectByMobile(mobile);
			//设置是否是新老用户放到前面执行，防止异常不执行
			if (member != null) {
				model.setReceiveFlag(1);
			} else {
				model.setReceiveFlag(0);
			}
			// 记录redis标记，防止多次领取
			Long participate = RedisActivityClient.hincrRedBagMobile(transaction.getId(), mobile, 1L, rule.getTimeOutFromCreate());
			if (participate == 0L) {
				// redis取值异常
				model.setHasException(true);
			} else if (participate == 1L && !model.isHasReceive()) {
				// 根据redis发放奖励
				receiveRedPackageByRedis(member, mobile, transaction.getId(), transaction.getMemberId(), model, activityBiz);
				if (model.getRewardValue() == null) {
					// 没有获取到红包，返还redis领取标记
					RedisActivityClient.hincrRedBagMobile(transaction.getId(), mobile, -1L, rule.getTimeOutFromCreate());
				}
				List<String> cacheList = RedisActivityClient.getRedBag(transaction.getId());
				if (Collections3.isEmpty(cacheList)) {
					// 更新状态
					int s = DateUtils.getTimeIntervalSencond(transaction.getTransactionTime(), DateUtils.getCurrentDate());
					s = rule.getTimeOutFromCreate() - s;
					RedisActivityClient.updateRedPackageInfo(transaction.getId(), s);
					model.setHasEmpty(true);
					// 设置最佳人气
					activityLotteryPretreatManager.setRedPackageTop(activityBiz.getId(), transaction.getId());
				}
			} else {
				model.setHasReceive(true);
			}
			// 本人领取自己的显示红包余额
			if (member != null && member.getId().equals(transaction.getMemberId())) {
				model.setMobile(mobile);
				// 获取余额和余量
				List<String> list = RedisActivityClient.getRedBag(transaction.getId());
				int num = 0;
				for (String number : list) {
					num += Integer.valueOf(number);
				}
				model.setResidualAmount(num);
				model.setResidualNumber(list.size());
			}
		} catch (Exception e) {
			logger.error("领取人气值红包主程序失败, sourceId={} mobile={}", transaction.getId(), mobile, e);
			model.setHasException(true);
		}
		return model;
	}

	private ActivityForRedBag receiveRedPackageByRedis(Member member, Long mobile, Long sourceId, Long sourceHolder,
			ActivityForRedBag model, ActivityBiz activityBiz)
			throws ManagerException {
		// 红包总数-1,返回的index用于更新对应的表数据
		int redBagValue = RedisActivityClient.rpopRedBagValue(sourceId);
		if (redBagValue < 0) {
			// redis通信异常
			model.setHasException(true);
			return model;
		} else if (redBagValue < 1) {
			// redis通信异常
			model.setHasEmpty(true);
			return model;
		}
		ActivityLotteryPretreat query = new ActivityLotteryPretreat();
		query.setActivityId(activityBiz.getId());
		query.setSourceId(sourceId);
		query.setClaimFlag(1);
		if (member == null) {
			// 非注册会员
			query.setIsMember(0);
			query.setReceiveFlag(0);
		} else {
			// 会员直接发放奖励
			query.setIsMember(1);
			query.setReceiveFlag(1);
		}
		query.setMobile(mobile);
		query.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
		query.setRewardValue(redBagValue);
		String rewardInfo = redBagValue + ActivityConstant.popularityDesc;
		query.setRewardInfo(rewardInfo);
		query.setSourceHolder(sourceHolder);
		query.setRemark(activityBiz.getDescription());
		// 更新奖励预处理表
		activityLotteryPretreatManager.receivePrize(query);
		// 已领红包总数+1
		RedisActivityClient.decrPopularityRedBag(activityBiz.getId());
		if (member != null) {
			// 发放奖励
			BigDecimal prizeValue = new BigDecimal(redBagValue);
			// 人气值
			Balance balance = balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, prizeValue, member.getId());
			// 记录人气值资金流水
			popularityInOutLogManager.insert(member.getId(), TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, prizeValue, null,
					balance.getAvailableBalance(), activityBiz.getId(), activityBiz.getDescription()
							+ RemarksEnum.ACTIVITY_LOTTERY_FOR_POPULARITY.getRemarks());
			balanceManager.incrGivePlatformTotalPoint(prizeValue);
			// 发送站内信
			MessageClient.sendMsgForSPEngin(member.getId(), activityBiz.getDescription(), rewardInfo);
		}
		model.setRewardValue(new BigDecimal(redBagValue));
		return model;
	}
}
