package com.yourong.core.mc.manager.listen;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.eventbus.EventListener;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.mc.dao.ActivityLotteryMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.manager.ActivityHistoryManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityAfterEngineListenerObject;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.biz.ActivityForFriendShip;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

/**
 * 
 * @desc 活动引擎后续处理
 * @author wangyanji 2016年5月10日上午10:08:15
 */
@Component
public class ActivityAfterEngineListen implements EventListener<ActivityAfterEngineListenerObject> {

	private static Logger logger = LoggerFactory.getLogger(ActivityAfterEngineListen.class);

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private DrawByPrizeDirectly drawByPrizeDirectly;

	@Autowired
	private ActivityHistoryManager activityHistoryManager;

	@Autowired
	private ActivityLotteryMapper activityLotteryMapper;

	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;

	@Autowired
	private EventBus eventBus;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	@Subscribe
	public void handle(final ActivityAfterEngineListenerObject t) {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				friendShipYears(t);
			}
		});
	}

	/**
	 * 
	 * @Description:【友情岁月活动】
	 * @param t
	 * @author: wangyanji
	 * @time:2016年5月10日 上午11:41:59
	 */
	private void friendShipYears(ActivityAfterEngineListenerObject t) {
		try {
			logger.info("友情岁月活动处理开始 memberId={}, activityName={}", t.getMemberId(), t.getActivityName());
			Optional<Activity> optOfActivity = LotteryContainer.getInstance()
					.getActivityByName(ActivityConstant.ACTIVITY_FRIEND_SHIP_YEARS);
			if (!optOfActivity.isPresent()) {
				logger.error("【友情岁月活动】活动脚本缺失！");
				return;
			}
			Activity activity = optOfActivity.get();
			if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != activity.getActivityStatus()) {
				return;
			}
			// 查询推荐人
			Member referred = memberManager.selectByPrimaryKey(t.getMemberId());
			if (referred.getReferral() == null) {
				return;
			}
			if (!DateUtils.isDateBetween(referred.getRegisterTime(), activity.getStartTime(), activity.getEndTime())) {
				// 不是活动时间内注册的会员
				return;
			}
			Member refer = memberManager.selectByPrimaryKey(referred.getReferral());
			ActivityForFriendShip rule = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForFriendShip.class,
					ActivityConstant.ACTIVITY_FRIEND_SHIP_KEY);
			// 如果用户完成的是抽次投资
			if (t.getActivityName().indexOf("首次投资") > -1) {
				// 增加里程
				RewardsBase rBase = new RewardsBase();
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_OTHER.getType());
				rBase.setRewardName(rule.getFirstInvest() + "km");
				rBase.setRewardValue(rule.getFirstInvest());
				String cycleStr = referred.getId() + "-" + "firstInvest";
				String remarks = "firstInvest";
				boolean prizeFlag = receiveCouponCommon(refer.getId(), activity, rBase, cycleStr, remarks, referred.getId().toString());
				if (!prizeFlag) {
					return;
				}
				// 送券
				RewardsBase rBase2 = new RewardsBase();
				rBase2.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase2.setRewardName(rule.getFirstInvestCouponValue() + ActivityConstant.couponDesc);
				rBase2.setRewardValue(rule.getFirstInvestCouponValue());
				rBase2.setTemplateId(rule.getFirstInvestCoupon());
				String remarks2 = "firstInvestFor30Coupon";
				String cycleStr2 = referred.getId() + "-" + "firstInvestFor30Coupon";
				prizeFlag = receiveCouponCommon(refer.getId(), activity, rBase2, cycleStr2, remarks2, referred.getId().toString());
				if (prizeFlag)
					afterFirstInvest(t, activity, referred, refer, rule);
				return;
			}
			// 判断是否投资过
			String firstInvestIdStr = PropertiesUtil.getProperties("activity.fristInvest");
			boolean hasInvested = isParticipateInActivity(t.getMemberId(), Long.valueOf(firstInvestIdStr));
			if (!hasInvested) {
				return;
			}
			otherPrize(t, activity, referred, refer, rule);
		} catch (Exception e) {
			logger.error("友情岁月活动处理失败 memberId={}, activityName={}", t.getMemberId(), t.getActivityName(), e);
		}
	}

	private void otherPrize(ActivityAfterEngineListenerObject t, Activity activity, Member referred, Member refer,
			ActivityForFriendShip rule) throws Exception {
		Integer km = 0;
		String remarks = "";
		String cycleStr = referred.getId() + "-";
		if (t.getActivityName().indexOf("首次体验APP") > -1) {
			km = rule.getBindApp();
			remarks = "firstBindApp";
			cycleStr += "firstBindApp";
		} else if (t.getActivityName().indexOf("首次关注微信") > -1) {
			km = rule.getBindWeChat();
			remarks += "firstBindWeChat";
			cycleStr += "firstBindWeChat";
		} else if (t.getActivityName().indexOf("完善信息") > -1) {
			km = rule.getImproveMemberInfo();
			remarks += "firstImproveInfo";
			cycleStr += "firstImproveInfo";
		} else if (t.getActivityName().indexOf("绑定邮箱") > -1) {
			km = rule.getBindEmail();
			remarks += "firstBindEmail";
			cycleStr += "firstBindEmail";
		} else {
			return;
		}
		RewardsBase rBase = new RewardsBase();
		rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_OTHER.getType());
		rBase.setRewardName(km + "km");
		rBase.setRewardValue(km);
		receiveCouponCommon(refer.getId(), activity, rBase, cycleStr, remarks, referred.getId().toString());
	}

	private void afterFirstInvest(ActivityAfterEngineListenerObject t, Activity activity, Member referred, Member refer,
			ActivityForFriendShip rule) throws Exception {
		String fristBindingAppIdStr = PropertiesUtil.getProperties("activity.fristBindingApp");
		String fristBindingWeixinIdStr = PropertiesUtil.getProperties("activity.fristBindingWeixin");
		String bindEmailIdStr = PropertiesUtil.getProperties("activity.bindEmail");
		String completedMemberInfoIdStr = PropertiesUtil.getProperties("activity.completedMemberInfo");
		List<Long> idLists = Lists.newArrayList(Long.valueOf(fristBindingAppIdStr), Long.valueOf(fristBindingWeixinIdStr),
				Long.valueOf(bindEmailIdStr), Long.valueOf(completedMemberInfoIdStr));
		List<String> nameLists = Lists.newArrayList("首次体验APP", "首次关注微信", "首次绑定邮箱", "首次完善信息");
		for (int i = 0, size = idLists.size(); i < size; i++) {
			Long id = Long.valueOf(idLists.get(i));
			boolean hasInvested = isParticipateInActivity(t.getMemberId(), id);
			if (!hasInvested) {
				continue;
			}
			ActivityAfterEngineListenerObject model = new ActivityAfterEngineListenerObject(t.getMemberId(), id, nameLists.get(i));
			friendShipYears(model);
		}
	}


	/**
	 * 
	 * @Description:奖励通用方法
	 * @param memberId
	 * @param activity
	 * @param templateId
	 * @param rDO
	 * @author: wangyanji
	 * @throws Exception
	 * @time:2016年4月18日 下午6:12:24
	 */
	private boolean receiveCouponCommon(Long memberId, Activity activity, RewardsBase rewardsBase, String cycleStr, String remarks,
			String rewardId)
			throws Exception {
		try {
			// 领取
			RuleBody ruleBody = new RuleBody();
			ruleBody.setActivityId(activity.getId());
			ruleBody.setMemberId(memberId);
			ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
			ruleBody.setCycleStr(cycleStr);
			ruleBody.setActivityName(activity.getActivityName());
			ruleBody.setDeductRemark(remarks);
			ruleBody.setRewardId(rewardId);
			if (!drawByPrizeDirectly.validate(ruleBody, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode())) {
				logger.error("友情岁月活动重复领取活动奖励 memberId={} cycleStr={}", memberId, cycleStr);
				return false;
			}
			// 校验初始化数据
			ActivityLottery model = new ActivityLottery();
			model.setActivityId(activity.getId());
			model.setMemberId(memberId);
			model.setCycleConstraint(activity.getId().toString());
			ActivityLottery record = activityLotteryMapper.checkExistLottery(model);
			if (record == null) {
				// 第一次获得里程需要初始化数据
				model.setTotalCount(0);
				model.setRealCount(0);
				try {
					activityLotteryMapper.insertSelective(model);
					model.setCycleConstraint("packages");
					activityLotteryMapper.insertSelective(model);
					model.setCycleConstraint("boxes");
					activityLotteryMapper.insertSelective(model);
					model.setCycleConstraint(activity.getId().toString());
					record = activityLotteryMapper.checkExistLottery(model);
				} catch (DuplicateKeyException mysqlE) {
					record = activityLotteryMapper.checkExistLottery(model);
				}
			}
			if (record == null) {
				throw new Exception("获取不到用户里程数 memberId=" + memberId);
			}
			// 锁行
			ActivityLottery recordForLock = activityLotteryMapper.getRecordForLock(record.getId());
			drawByPrizeDirectly.drawLottery(ruleBody, rewardsBase, TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
			if (rewardsBase.getRewardType() != TypeEnum.ACTIVITY_LOTTERY_TYPE_OTHER.getType()) {
				return true;
			}
			// 更新总里程数
			ActivityLottery al = new ActivityLottery();
			al.setActivityId(activity.getId());
			al.setMemberId(memberId);
			al.setCycleConstraint(activity.getId().toString());
			al.setTotalCount(rewardsBase.getRewardValue());
			activityLotteryMapper.updateByActivityAndMember(al);
			// 更新背包数
			updateRewards(memberId, activity, rewardsBase, recordForLock);
			return true;
		} catch (Exception e) {
			String errorMsg = e.getMessage();
			if (errorMsg.startsWith("重复参加活动")) {
				logger.error("友情岁月活动处理失败 memberId={}, cycleStr={}, errorMsg={}", memberId, cycleStr, errorMsg);
			} else {
				logger.error("友情岁月活动处理失败 memberId={}, cycleStr={}", memberId, cycleStr, e);
			}
			return false;
		}
	}

	private void updateRewards(Long memberId, Activity activity, RewardsBase rewardsBase, ActivityLottery recordForLock) {
		int totalKm = recordForLock.getTotalCount() + rewardsBase.getRewardValue();
		// 更新背包
		ActivityLottery queryPackages = new ActivityLottery();
		queryPackages.setActivityId(activity.getId());
		queryPackages.setMemberId(memberId);
		queryPackages.setCycleConstraint("packages");
		ActivityLottery packages = activityLotteryMapper.checkExistLottery(queryPackages);
		ActivityLottery packagesForLock = activityLotteryMapper.getRecordForLock(packages.getId());
		int totalPackages = (totalKm / 100) * 3;
		int modPackages = totalKm % 100;
		if (modPackages >= 30 && modPackages < 50) {
			totalPackages += 1;
		} else if (modPackages >= 50 && modPackages < 80) {
			totalPackages += 2;
		} else if (modPackages >= 80) {
			totalPackages += 3;
		}
		int valuePackages = totalPackages - packagesForLock.getTotalCount();
		if (valuePackages > 0) {
			ActivityLottery updatePackages = new ActivityLottery();
			updatePackages.setActivityId(activity.getId());
			updatePackages.setMemberId(memberId);
			updatePackages.setCycleConstraint("packages");
			updatePackages.setTotalCount(valuePackages);
			updatePackages.setRealCount(valuePackages);
			activityLotteryMapper.updateByActivityAndMember(updatePackages);
		}
		// 更新宝箱
		ActivityLottery queryBoxes = new ActivityLottery();
		queryBoxes.setActivityId(activity.getId());
		queryBoxes.setMemberId(memberId);
		queryBoxes.setCycleConstraint("boxes");
		ActivityLottery boxes = activityLotteryMapper.checkExistLottery(queryBoxes);
		ActivityLottery boxesForLock = activityLotteryMapper.getRecordForLock(boxes.getId());
		int totalBoxes = totalKm / 100;
		int valueBoxes = totalBoxes - boxesForLock.getTotalCount();
		if (valueBoxes > 0) {
			ActivityLottery updateBoxes = new ActivityLottery();
			updateBoxes.setActivityId(activity.getId());
			updateBoxes.setMemberId(memberId);
			updateBoxes.setCycleConstraint("boxes");
			updateBoxes.setTotalCount(valueBoxes);
			updateBoxes.setRealCount(valueBoxes);
			activityLotteryMapper.updateByActivityAndMember(updateBoxes);
		}
	}

	/**
	 * 检查用户是否已经参与活动
	 * 
	 * @param memberId
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	private boolean isParticipateInActivity(Long memberId, Long activityId) throws ManagerException {
		boolean flag = RedisActivityClient.isParticipateInActivity(activityId, memberId);
		if (!flag) {
			flag = activityHistoryManager.isParticipateInActivity(memberId, activityId);
			if (flag) {
				RedisActivityClient.setActivitiesMember(activityId, memberId);
			}
		}
		return flag;
	}

}
