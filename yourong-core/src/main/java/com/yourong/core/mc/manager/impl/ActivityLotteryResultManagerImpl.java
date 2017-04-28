package com.yourong.core.mc.manager.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.biz.ActivityGrabResultBiz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.ic.model.biz.LotteryRewardBiz;
import com.yourong.core.ic.model.biz.ProjectForLevel;
import com.yourong.core.ic.model.biz.ProjectForRewardMember;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.mc.dao.ActivityDataMapper;
import com.yourong.core.mc.dao.ActivityLotteryMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultNewMapper;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.ActivityLotteryResultNew;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.biz.ActivityForFiveBillionRetrun;
import com.yourong.core.mc.model.biz.ActivityForJulyBet;
import com.yourong.core.mc.model.biz.ActivityForMemberInfo;
import com.yourong.core.mc.model.biz.ActivityForOlympic;
import com.yourong.core.mc.model.biz.ActivityForOlympicDate;
import com.yourong.core.mc.model.biz.ActivityForInviteFriendDetail;
import com.yourong.core.mc.model.biz.ActivityLotteryResultBiz;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.model.Member;

@Component
public class ActivityLotteryResultManagerImpl implements ActivityLotteryResultManager {

	private static final Logger logger = LoggerFactory.getLogger(ActivityLotteryResultManagerImpl.class);
	
	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;
	
	@Autowired
	private ActivityLotteryResultNewMapper activityLotteryResultNewMapper;
	
	@Autowired
	private ActivityLotteryMapper activityLotteryMapper;
	
	@Autowired
	private ActivityDataMapper activityDataMapper;
	
	@Autowired
	private DrawByPrizeDirectly drawByPrizeDirectly;
	
	@Autowired
	private CouponTemplateManager couponTemplateManager;
	
	@Autowired
	private MemberMapper memberMapper;

	@Override
	public List<ActivityLotteryResult> getLotteryResultBySelective(
			ActivityLotteryResult model) throws ManagerException {
		try {
			return activityLotteryResultMapper.getLotteryResultBySelective(model);
		} catch (Exception e) {
			logger.error("自定义中奖结果查询失败", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityLotteryResult> topByRewardType(ActivityLotteryResult model) throws ManagerException {
		try {
			return activityLotteryResultMapper.topByRewardType(model);
		} catch (Exception e) {
			logger.error("根据奖励类型排行失败", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public int sumRewrdsByMemberActivity(ActivityLotteryResult model) throws ManagerException {
		try {
			return activityLotteryResultMapper.sumRewrdsByMemberActivity(model);
		} catch (Exception e) {
			logger.error("根据奖励类型排行失败", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityLotteryResult> inviteFriendList(Long activityId, Long memberId, int startNo, Date startTime, Date endTime)
			throws ManagerException {
		try {
			return activityLotteryResultMapper.inviteFriendList(activityId, memberId, startNo, startTime, endTime);
		} catch (Exception e) {
			logger.error("根据奖励类型排行失败", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer inviteFriendListCount(Long activityId, Long memberId, Date startTime, Date endTime) throws ManagerException {
		try {
			Integer num = activityLotteryResultMapper.inviteFriendListTotal(activityId, memberId, startTime, endTime);
			if (num != null) {
				return num;
			}
		} catch (Exception e) {
			logger.error("根据奖励类型排行失败", e);
			throw new ManagerException(e);
		}
		return 0;
	}

	@Override
	public int sumRewrdsByMemberActivityRewardId(ActivityLotteryResult activityResult) throws ManagerException {
		try {
			return activityLotteryResultMapper.sumRewrdsByMemberActivityRewardId(activityResult);
		} catch (Exception e) {
			logger.error("根据奖励类型统计人气值", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public int countRewrdsByMemberActivityRewardId(ActivityLotteryResult activityResult) throws ManagerException {
		try {
			return activityLotteryResultMapper.countRewrdsByMemberActivityRewardId(activityResult);
		} catch (Exception e) {
			logger.error("统计记录数失败", e);
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 获取奥运徽章
	 * @param activityId
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年7月14日 下午1:42:52
	 *
	 */
	@Override
	public String getBrightOlympicEveryDay(Long activityId, Long memberId) throws ManagerException {
		List tempList=Lists.newArrayList();
		List<ActivityLottery> lotteryList=activityLotteryMapper.selectActivityLotteryByActivityIdAndCycleConstraint(activityId,memberId);
		if(Collections3.isEmpty(lotteryList)){ 
			return null;
		}
		for(ActivityLottery lottery:lotteryList){
			tempList.add(DateUtils.getDate(lottery.getCreateTime()));
		}
		return JSON.toJSONString(tempList);
	}
	
	@Override
	public int countLotteryResultByMemberIdAndRemark(ActivityLotteryResult model) throws ManagerException {
		try {
			return activityLotteryResultMapper.countLotteryResultByMemberIdAndRemark(model);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/***
	 * 
	 * @Description:亮奥运奖励领取情况
	 * @param activity
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年7月14日 下午12:56:26
	 */
	@Override
	public ActivityForOlympic ifBrightOlympicReceived(Activity activity, Long memberId,ActivityForOlympic model) throws ManagerException{
		ActivityForOlympicDate olympicDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForOlympicDate.class,
				ActivityConstant.ACTIVITY_PLAY_OLYMPIC_KEY);
		if(olympicDate.getCouponAmount()==null){
			return null;
		}
		ActivityLotteryResult biz = new ActivityLotteryResult();
		biz.setActivityId(activity.getId());
		biz.setMemberId(memberId);
		biz.setRemark(ActivityConstant.ACTIVITY_PLAY_BRIGHT_OLYMPIC+activity.getId());
		biz.setRewardResult(olympicDate.getCouponAmount().get(1).toString());
		if(countLotteryResultByMemberIdAndRemark(biz)>0){
			model.setCouponAmount50(true);
		}
		biz.setRewardResult(olympicDate.getCouponAmount().get(2).toString());
		if(countLotteryResultByMemberIdAndRemark(biz)>0){
			model.setCouponAmount100(true);
		}
		biz.setRewardResult(olympicDate.getCouponAmount().get(3).toString());
		if(countLotteryResultByMemberIdAndRemark(biz)>0){
			model.setCouponAmount200(true);
		}
		return model;
	}
	/**
	 * 
	 * @desc 猜奥运逻辑处理
	 * @author chaisen
	 * @throws Exception 
	 * @time 2016年7月14日 下午2:09:10
	 *
	 */
	@Override
	public void guessOlympicSendCoupon() throws Exception {
		Optional<Activity> olympicActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_PLAY_OLYMPIC);
		if (!olympicActivity.isPresent()) {
			return;
		}
		BigDecimal totalMedals=BigDecimal.ZERO;
		Activity activity=olympicActivity.get();
		ActivityForOlympicDate olympicDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForOlympicDate.class,
				ActivityConstant.ACTIVITY_PLAY_OLYMPIC_KEY);
		try {
		//查询奖牌数量
		ActivityData activityData=activityDataMapper.selectActivityDateByActivityId(activity.getId(), DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
		RewardsBase rBase = new RewardsBase();
		RuleBody rb = new RuleBody();
		if(activityData!=null){
			totalMedals=new BigDecimal(activityData.getDataGole()).add(new BigDecimal(activityData.getDataSilver())).add(new BigDecimal(activityData.getDataCopper()));
			int medalType=0;
			//奖牌总量是偶数
			if(totalMedals.intValue()%2==0){
				medalType=2;
			//奖牌总量是奇数	
			}else{
				medalType=1;	
			}
			//查询昨天猜对的用户
			List<ActivityLotteryResult> lotteryList=activityLotteryResultMapper.getLotteryResultByRemark(activity.getId(),DateUtils.getDateStrFromDate(DateUtils.getYesterDay())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL+medalType);
			if(Collections3.isNotEmpty(lotteryList)){
				//送50元现金券
				CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(olympicDate.getTemplateId().get(1));
				if(couponTemplate==null){
					return;
				}
				rBase.setTemplateId(couponTemplate.getId());
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_COUPON.getType());
				rBase.setRewardName(couponTemplate.getAmount().intValue() + ActivityConstant.couponDesc);
				rBase.setRewardValue(couponTemplate.getAmount().intValue());
				rb.setActivityId(activity.getId());
				rb.setDeductRemark(ActivityConstant.ACTIVITY_OLYMPIC_GUESS_NAME);
				rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getYesterDay())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL);
				for(ActivityLotteryResult biz:lotteryList){
					rb.setMemberId(biz.getMemberId());
					try {
						//判断是否领取过
						int j=activityLotteryResultMapper.countLotteryResultByActivityIdAndRemark(activity.getId(),biz.getMemberId(),DateUtils.getDateStrFromDate(DateUtils.getYesterDay())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL+medalType);
						if(j>0){
							continue;
						}
						drawByPrizeDirectly.drawLottery(rb, rBase, null);
						ActivityLotteryResult result=new ActivityLotteryResult();
						result.setActivityId(activity.getId());
						result.setMemberId(biz.getMemberId());
						result.setRemark(DateUtils.getDateStrFromDate(DateUtils.getYesterDay())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL+medalType);
						result.setStatus(1);
						result.setRewardInfo(Integer.toString(medalType));
						activityLotteryResultMapper.updateBatchLotteryResultRewardResultByActivityId(result);
					} catch (Exception e) {
						logger.info("猜奖牌送现金券失败, memberId={}, activityId={}", biz.getMemberId(), activity.getId());
					}
					//站内信
					MessageClient.sendMsgForSPEngin(biz.getMemberId(), ActivityConstant.ACTIVITY_OLYMPIC_GUESS_NAME, rBase.getRewardName());
				}
				//未猜中的更新实际结果
				activityLotteryResultMapper.updateResultInfoByActivityId(activity.getId(),DateUtils.getDateStrFromDate(DateUtils.getYesterDay())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_MEDAL,Integer.toString(medalType));
			}
		}
		//判断是否是活动最后一天,最后一天对猜金牌的用户送人气值
		if(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()).equals(DateUtils.getDateStrFromDate(activity.getEndTime()))){
			//查询金牌数量
			int totalGold=activityDataMapper.sumTotalGold(activity.getId());
			ActivityLotteryResult model = new ActivityLotteryResult();
			model.setActivityId(olympicActivity.get().getId());
			model.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
			model.setRemark(ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
			model.setRewardResult(Integer.toString(totalGold));
			List<ActivityLotteryResult> guessList=activityLotteryResultMapper.getLotteryResultByGuessTime(model);
			if(Collections3.isEmpty(guessList)){
				return;
			}
			BigDecimal popularityValue=BigDecimal.ZERO;
			for(ActivityLotteryResult bean:guessList){
				//8.5到8.6竞猜的用户 送5倍
				if(DateUtils.isDateBetween(bean.getCreateTime(), DateUtils.getDateTimeFromString(olympicDate.getGuessGoldStartTime()), DateUtils.getDateTimeFromString(olympicDate.getSixEndTime()))){
					popularityValue=new BigDecimal(bean.getChip()).multiply(olympicDate.getMultiple().get(0));
				//8.7到8.8竞猜的用户 送3倍	
				}else if(DateUtils.isDateBetween(bean.getCreateTime(),DateUtils.getDateTimeFromString(olympicDate.getSevenStartTime()), DateUtils.getDateTimeFromString(olympicDate.getEightEndTime()))){
					popularityValue=new BigDecimal(bean.getChip()).multiply(olympicDate.getMultiple().get(1));
				//8.9到8.12竞猜的用户 送2倍		
				}else if(DateUtils.isDateBetween(bean.getCreateTime(),DateUtils.getDateTimeFromString(olympicDate.getNineStartTime()),  DateUtils.getDateTimeFromString(olympicDate.getGuessGoldEndTime()))){
					popularityValue=new BigDecimal(bean.getChip()).multiply(olympicDate.getMultiple().get(2));
				}
				//送人气值
				rb.setDeductRemark(DateUtils.getDateStrFromDate(bean.getCreateTime())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
				rb.setMemberId(bean.getMemberId());
				rb.setCycleStr(DateUtils.getDateStrFromDate(bean.getCreateTime())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
				rBase.setRewardName(popularityValue + ActivityConstant.popularityDesc);
				rBase.setRewardValue(popularityValue.intValue());
				rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
				rb.setCycleStr(DateUtils.getDateStrFromDate(bean.getCreateTime())+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
				rb.setActivityId(activity.getId());
				rb.setActivityName(ActivityConstant.ACTIVITY_OLYMPIC_GUESS_NAME);
				try {
					//判断是否领取过
					int k=activityLotteryResultMapper.countLotteryResultByActivityIdAndRewardId(activity.getId(),bean.getMemberId(),bean.getMemberId()+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
					if(k>0){
						continue;
					}
					drawByPrizeDirectly.drawLottery(rb, rBase, "");
					ActivityLotteryResult result=new ActivityLotteryResult();
					result.setActivityId(activity.getId());
					result.setMemberId(bean.getMemberId());
					result.setRewardId(bean.getMemberId()+ActivityConstant.ACTIVITY_OLYMPIC_GUESS_GOLE);
					result.setStatus(1);
					activityLotteryResultMapper.updateBatchLotteryResultRewardResultByActivityId(result);
				} catch (Exception e) {
					logger.info("猜金牌送人气值失败, memberId={}, activityId={}", bean.getMemberId(), activity.getId());
				}
				
				//站内信
				MessageClient.sendMsgForSPEngin(bean.getMemberId(), ActivityConstant.ACTIVITY_OLYMPIC_GUESS_NAME, rBase.getRewardName());
			}
			
		}
		} catch (ManagerException e) {
			logger.error("猜奥运猜中用户定时任务发送现金券失败", e);
		}
	}
	

	@Override
	public int insertSelective(ActivityLotteryResult model) throws ManagerException {
		try {
			return activityLotteryResultMapper.insertSelective(model);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	
	@Override
	public Integer countBetLotteryResult(Long activityId, Long memberId) throws ManagerException {
		try {
			return activityLotteryResultMapper.countBetLotteryResult(activityId, memberId);
		} catch (Exception e) {
			logger.error("统计押注记录失败", e);
			throw new ManagerException(e);
		}
	}


	@Override
	public Integer countLastBetTotal(Long activityId, String remark) throws ManagerException {
		try {
			return activityLotteryResultMapper.countLastBetTotal(activityId,remark);
		} catch (Exception e) {
			logger.error("统计押注记录失败", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityForJulyBet> getBetRecordList(
			ActivityLotteryResult activityResult) throws ManagerException {
		List<ActivityForJulyBet> betList=Lists.newArrayList();
		List<ActivityLotteryResult> resultList= activityLotteryResultMapper.getLotteryResultBySelective(activityResult);
		if(Collections3.isNotEmpty(resultList)){
			for(ActivityLotteryResult result:resultList){
				ActivityForJulyBet bet=new ActivityForJulyBet();
				bet.setBetTime(result.getCreateTime());
				bet.setPopularityValue(result.getChip());
				// 押注失败
				if(result.getRewardResult()!=null&&result.getRewardResult().equals("0")){
					bet.setRewardPopularityValue(0);
				}
				if(result.getRewardResult()!=null&&!result.getRewardResult().equals("0")){
					bet.setRewardPopularityValue(Integer.parseInt(result.getRewardResult()));
				}
				//待揭晓
				if(result.getStatus()==0){
					bet.setRewardPopularityValue(-1);
				}
				
				betList.add(bet);
			}
		}
		return betList;
	}

	@Override
	public List<ProjectForLevel> getRewardLevelByProjectId(ActivityLotteryResult modelResult) throws ManagerException {
		try {
			return activityLotteryResultMapper.getRewardLevelByProjectId(modelResult);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal sumRewardInfoByProjectId(ActivityLotteryResult modelResult,int rewardType) throws ManagerException {
		try {
			modelResult.setRewardType(rewardType);
			modelResult.setStatus(1);
			//modelResult.setRemark(ActivityConstant.DIRECT_QUICK_REWARD_SEND);
			return activityLotteryResultMapper.sumRewardInfoByProjectId(modelResult);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public List<ProjectForRewardMember> findProjectRewardByProjectId(ActivityLotteryResult modelResult) throws ManagerException {
		try {
			return activityLotteryResultMapper.findProjectRewardByProjectId(modelResult);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityLotteryResult> getActivityLotteryResultByProject(ActivityLotteryResult recrod) throws ManagerException {
		try {
			return activityLotteryResultMapper.getLotteryResultBySelectiveOrderBy(recrod);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	@Override
	public boolean isReceived(Long activityId, Long memberId,String remark) {
		ActivityLotteryResult model=new ActivityLotteryResult();
		model.setActivityId(activityId);
		model.setRemark(remark);
		model.setMemberId(memberId);
		int i=activityLotteryResultMapper.countNewLotteryResult(model);
		if(i>0){
			return true;
		}
		return false;
	}
	
	@Override
	public List<ActivityLotteryResult> sumRewardInfoByMemberId(ActivityLotteryResult lotteryResult) throws ManagerException{
		try {
			return activityLotteryResultMapper.sumRewardInfoByMemberId(lotteryResult);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ActivityLotteryResult getMaxRewardForQuickProject(String remark) throws ManagerException {
		try {
			ActivityLotteryResult model = new ActivityLotteryResult();
			List<ActivityLotteryResult> list = activityLotteryResultMapper.getMaxRewardForQuickProject(remark);
			BigDecimal maxAmount = BigDecimal.ZERO;
			Long memberId = 0l;
			for (ActivityLotteryResult result : list) {
				BigDecimal amount = new BigDecimal(result.getRewardInfo());
				if (amount.compareTo(maxAmount) == 1) {
					maxAmount = amount;
					memberId = result.getMemberId();
				}
			}
			model.setRewardInfo(maxAmount.toString());
			model.setMemberId(memberId);
			return model;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	
	@Override
	public List<LotteryRewardBiz> getProjectLotteryReward(Long projectId) throws ManagerException {
		try {
			return activityLotteryResultMapper.getProjectLotteryReward(projectId.toString());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<LotteryRewardBiz> getLotteryReward(int rowNum) throws ManagerException  {
		try {
			return activityLotteryResultMapper.getLotteryReward(rowNum);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	

	@Override
	public List<ActivityLotteryResult> getLotteryResultBySelectiveAndLotteryStatus(ActivityLotteryResult model)throws ManagerException {
		try {
			return activityLotteryResultMapper.getLotteryResultBySelectiveAndLotteryStatus(model);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityGrabResultBiz> queryActivityGrabResult(Long activityId) throws ManagerException{
		try {
			return activityLotteryResultMapper.queryActivityGrabResult(activityId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean queryLotteryResultByActivityAndMemberId(Long activityId, Long memberId) {
		if (activityLotteryResultMapper.queryLotteryResultByActivityAndMemberId(activityId,memberId)>0){
			return true;
		}
		return false;
	}
	
	
	
	@Override
	public int queryAvailableLotteryNum(Long memberId)throws ManagerException{
		try {
			return activityLotteryResultMapper.queryAvailableLotteryNum(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ActivityLotteryResult sumRewardInfoResultByProjectId(
			ActivityLotteryResult model) throws ManagerException {
		try {
			return activityLotteryResultMapper.sumRewardInfoResultByProjectId(model);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	
	@Override
	public ActivityLotteryResult queryLotteryResultByRemark(Long activityId,
			Long memberId, String remark) throws ManagerException {
		try {
			return activityLotteryResultMapper.queryLotteryResultByRemark(activityId,memberId,remark);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 获取中奖记录
	 * @param activityId
	 * @param param
	 * @return
	 * @throws ManagerException
	 */
	@Override
	public List<ActivityForMemberInfo> getLotteryRecord(Long activityId,String param) throws ManagerException{
		List<ActivityForMemberInfo> lotteryRecord=Lists.newArrayList();
		ActivityLotteryResultNew record=new ActivityLotteryResultNew();
		record.setActivityId(activityId);
		record.setRemark(param);
		record.setNumber(100);
		List<ActivityLotteryResultNew> listLuck=activityLotteryResultNewMapper.getLotteryResultBySelective(record);
		if(Collections3.isNotEmpty(listLuck)){
			for(ActivityLotteryResultNew bean :listLuck){
				ActivityForMemberInfo acInfo=new ActivityForMemberInfo();
				acInfo.setMemberId(bean.getMemberId());
				Member member=memberMapper.selectByPrimaryKey(bean.getMemberId());
				if(member!=null){
					acInfo.setMobile(member.getMobile());
					acInfo.setAvatars(member.getAvatars());
					acInfo.setUsername(member.getUsername());
				}
				acInfo.setHappenTime(bean.getCreateTime());
				acInfo.setRewardInfo(bean.getRewardInfo());
				lotteryRecord.add(acInfo);
			}
		}
		return lotteryRecord;
		
	}
	
	/**
	 * 我的中奖记录
	 * @param memberId
	 * @param activityId
	 * @param param
	 * @return
	 * @throws ManagerException
	 */
	@Override
	public List<ActivityForFiveBillionRetrun> getMyLotteryRecord(Long memberId,Long activityId,String param) throws ManagerException{
		String key = ActivityConstant.ACTIVITY_FIVEBILLION_MYLOTTERY_KEY+":"+param;
		boolean isExit = RedisManager.isExitByObjectKey(key);
		List<ActivityForFiveBillionRetrun> lotteryRecord=Lists.newArrayList();
		if (isExit) {
			lotteryRecord = (List<ActivityForFiveBillionRetrun>) RedisManager.getObject(key);
		} else {
			ActivityLotteryResultNew record=new ActivityLotteryResultNew();
			record.setActivityId(activityId);
			record.setMemberId(memberId);
			record.setRemark(param);
			record.setNumber(10000);
			List<ActivityLotteryResultNew> listLuck=activityLotteryResultNewMapper.getLotteryResultBySelective(record);
			if(Collections3.isNotEmpty(listLuck)){
				for(ActivityLotteryResultNew bean :listLuck){
					ActivityForFiveBillionRetrun acInfo=new ActivityForFiveBillionRetrun();
					acInfo.setRewardInfo(bean.getRewardInfo());
					acInfo.setHappenTime(bean.getCreateTime());
					lotteryRecord.add(acInfo);
				}
			}
		}
		return lotteryRecord;
		
	}

	@Override
	public List<ActivityForMemberInfo> getFirstTotalInvestAmount(
			ActivityLotteryResultNew totalNew) {
		List<ActivityForMemberInfo> lotteryRecord=Lists.newArrayList();
		List<ActivityLotteryResultNew> listLuck=activityLotteryResultNewMapper.sumInvestAmountByActivityId(totalNew);
		if(Collections3.isNotEmpty(listLuck)){
			for(ActivityLotteryResultNew bean :listLuck){
				ActivityForMemberInfo acInfo=new ActivityForMemberInfo();
				acInfo.setMemberId(bean.getMemberId());
				Member member=memberMapper.selectByPrimaryKey(bean.getMemberId());
				if(member!=null){
					acInfo.setMobile(member.getMobile());
					//acInfo.setAvatars(member.getAvatars());
					//acInfo.setUsername(member.getUsername());
					acInfo.setAvatars(member.getAvatars());
				}
				acInfo.setHappenTime(bean.getCreateTime());
				acInfo.setRewardInfo(bean.getRewardInfo());
				acInfo.setTotalInvestAmount(new BigDecimal(bean.getChip()));
				lotteryRecord.add(acInfo);
			}
		}
		return lotteryRecord;
		
	}

	@Override
	public Page<ActivityForInviteFriendDetail> queryInviteFriendDetail(Long memberId,Integer pageNo) {
		Page<ActivityForInviteFriendDetail> page=new Page<>();
		List<ActivityForInviteFriendDetail> list=new ArrayList<>();
		int count= activityLotteryResultNewMapper.queryInviteFriendDetailCount(memberId);
		int startRow=0;
		int pageSize=10;
		int pageCount=0;
		if (count>0){
			startRow=(pageNo-1)*pageSize;
			list=activityLotteryResultNewMapper.queryInviteFriendDetail(memberId,startRow,pageSize);
			pageCount=count%pageSize>0?count/pageSize+1:count/pageSize;
		}
		page.setData(list);
		page.setPageNo(pageNo);
		page.setTotalPageCount(pageCount);
		return page;
	}
}