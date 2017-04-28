package com.yourong.core.mc.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.ActivityEnum;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.mc.dao.ActivityDataMapper;
import com.yourong.core.mc.dao.ActivityGroupMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.dao.ActivityMessageMapper;
import com.yourong.core.mc.manager.ActivityGroupManager;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.ActivityMessageManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.ActivityGroup;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.ActivityMessage;
import com.yourong.core.mc.model.biz.ActivityForJulyBet;
import com.yourong.core.mc.model.biz.ActivityForJulyDate;
import com.yourong.core.mc.model.biz.ActivityForJulyHistory;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.model.Member;

/**
 * 
 * @desc 活动分组
 * @author chaisen
 * 2016年6月30日上午11:28:03
 */
@Component
public class ActivityGroupManagerImpl implements ActivityGroupManager {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivityGroupManagerImpl.class);

	@Autowired
	private ActivityGroupMapper activityGroupMapper;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private TransactionMapper transactionMapper;
	
	@Autowired
	private ActivityLotteryManager activityLotteryManager;
	
	@Autowired
	private DrawByPrizeDirectly drawByPrizeDirectly;
	
	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
	private ActivityMessageMapper activityMessageMapper;
	
	@Autowired
	private ActivityDataMapper activityDataMapper;

	@Override
	public Integer insert(String actvityName, Long memberId, Long messageTemplateId) throws ManagerException {
		try {
			// 获取活动
			Optional<Activity> optOfSpring = LotteryContainer.getInstance().getActivityByName(actvityName);
			if (!optOfSpring.isPresent()) {
				return 0;
			}
			Activity activity = optOfSpring.get();
			if (activity.getActivityStatus() != StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				return 0;
			}
			ActivityMessage activityMessage = new ActivityMessage();
			activityMessage.setActivityId(activity.getId());
			activityMessage.setMemberId(memberId);
			activityMessage.setTemplateId(messageTemplateId);
			return activityGroupMapper.insert(activityMessage);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean checkGroupByActivityIdAndMemberId(Long activityId, Long memberId) throws ManagerException {
		try {
			int i = activityGroupMapper.countGroupByMemberIdAndActivityId(activityId, memberId);
			if(i>0){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityMessage> selectRankByActivityId(Long activityId, int rowNum) throws ManagerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActivityGroup getCurrentMemberGroupBy(Long activityId, Long memberId) throws ManagerException {
		try {
			return activityGroupMapper.getCurrentMemberGroupBy(activityId, memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getGroupTypeByMemberId(Long activityId, Long memberId) throws ManagerException {
		ActivityGroup activityGroup=activityGroupMapper.getCurrentMemberGroupBy(activityId, memberId);
		if(activityGroup!=null){
			return activityGroup.getGroupType();
		}
		return 0;
	}

	@Override
	public BigDecimal getTotalAmountByMemberIdAndActivityGroupType(Activity activity, int groupType) throws ManagerException {
		ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
				ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
		TransactionQuery query = new TransactionQuery();
		query.setGroupType(groupType);
		query.setTransactionStartTime(DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()));
		query.setActivityId(activity.getId());
		query.setTransactionEndTime(DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
		return transactionMapper.getTotalInvestByGroupType(query);
	}
	/**
	 * 
	 * @desc 投资排行前十
	 * @param groupType
	 * @param activityId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author chaisen
	 * @time 2016年7月2日 上午10:51:31
	 *
	 */
	@Override
	public List<TransactionForFirstInvestAct> selectTopTenInvestByGroupType(int groupType, Long activityId, Date startTime, Date endTime) {
		TransactionQuery query = new TransactionQuery();
		query.setGroupType(groupType);
		query.setTransactionStartTime(startTime);
		query.setActivityId(activityId);
		query.setTransactionEndTime(endTime);
		return transactionMapper.selectTopTenInvestByGroupType(query);
	}
	/**
	 * 
	 * @desc 所在战队红包剩余数量
	 * @param activity
	 * @param groupType
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年7月2日 上午11:33:32
	 *
	 */
	@Override
	public ActivityForJulyBet getRemindCoupon(Activity activity, int groupType) throws ManagerException {
		ActivityForJulyDate julyJson = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
				ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
		ActivityLottery rb=new ActivityLottery();
		rb.setActivityId(activity.getId());
		rb.setCycleConstraint(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
		ActivityForJulyBet biz=new ActivityForJulyBet();
		int coupon30=julyJson.getTotalCoupon().get(0)-activityLotteryManager.getRemindCouponNumber(rb, julyJson.getTemplateId().get(0),groupType);
		int coupon50=julyJson.getTotalCoupon().get(1)-activityLotteryManager.getRemindCouponNumber(rb, julyJson.getTemplateId().get(1),groupType);
		int coupon100=julyJson.getTotalCoupon().get(2)-activityLotteryManager.getRemindCouponNumber(rb, julyJson.getTemplateId().get(2),groupType);
		int coupon200=julyJson.getTotalCoupon().get(3)-activityLotteryManager.getRemindCouponNumber(rb, julyJson.getTemplateId().get(3),groupType);
		if(coupon30>=0){
			biz.setCoupon30(coupon30);
		}
		if(coupon50>=0){
			biz.setCoupon50(coupon50);
		}
		if(coupon100>=0){
			biz.setCoupon100(coupon100);
		}
		if(coupon200>=0){
			biz.setCoupon200(coupon200);
		}
		return biz;
	}
	/**
	 * 
	 * @desc 统计战队投资总额奖励人气值
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年7月4日 上午10:35:29
	 *
	 */
	@Override
	public void totalTeamInvestAmount() throws Exception {
		Optional<Activity> teamActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.ACTIVITY_JULY_TEAM);
		if (!teamActivity.isPresent()) {
			return;
		}
		Activity activity = teamActivity.get();
		ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
				ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
		//骄阳似火的当天投资总额
		BigDecimal totalAmountA = getTotalAmountByMemberIdAndActivityGroupType(activity,ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode());
		putAmountRedis(ActivityConstant.ACTIVITY_JULY_TEAM_JYSH_KEY,totalAmountA);
		//清凉一夏的当天投资总额
		BigDecimal totalAmountB = getTotalAmountByMemberIdAndActivityGroupType(activity,ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode());
		putAmountRedis(ActivityConstant.ACTIVITY_JULY_TEAM_QLYX_KEY,totalAmountB);
		//投资总额保存到redis
		ActivityForJulyHistory history=new ActivityForJulyHistory();
		history.setPkTime(DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_12));
		history.setTodayInvestAmountA(totalAmountA);
		history.setTodayInvestAmountB(totalAmountB);
		history.setHappenTime(DateUtils.getCurrentDate());
		ActivityMessage messageHistroy=new ActivityMessage();
		messageHistroy.setActivityId(activity.getId());
		messageHistroy.setTemplateId(1003L);
		messageHistroy.setRemark(JSON.toJSONString(history));
		messageHistroy.setCreateTime(new Date());
		messageHistroy.setDelFlag(1);
		int groupType=0;
		String messageName="";
		if(totalAmountA.compareTo(totalAmountB)>0){
			groupType=ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode();
			messageName=ActivityConstant.ACTIVITY_JULY_TEAM_JYSH_NAME;
		}else if(totalAmountA.compareTo(totalAmountB)<0){
			groupType=ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode();
			messageName=ActivityConstant.ACTIVITY_JULY_TEAM_QLYX_NAME;
		//投资总额相等
		}else{
			groupType=getMaxTransactionTime(activity);
			if(groupType==ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode()){
				messageName=ActivityConstant.ACTIVITY_JULY_TEAM_JYSH_NAME;
			}else{
				messageName=ActivityConstant.ACTIVITY_JULY_TEAM_QLYX_NAME;
			}
		} 
		//获胜的战队
		ActivityGroup record=new ActivityGroup();
		record.setActivityId(activity.getId());
		record.setGroupType(groupType);
		putSuccessTeamRedis(groupType);
		List<ActivityGroup> listGroup=activityGroupMapper.getActivityGroupBySelective(record);
		RuleBody rb = new RuleBody();
		RewardsBase rBase = new RewardsBase();
		rb.setCycleStr(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+ActivityConstant.ACTIVITY_JULY_TEAM_SUCCESS_TEAM);
		rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
		rb.setActivityId(activity.getId());
		BigDecimal totalAmount=BigDecimal.ZERO;
		activityGroupMapper.updateGroupTypeByActivityId(activity.getId());
		int prizeValue=0;
		if(Collections3.isNotEmpty(listGroup)){
			for(ActivityGroup group:listGroup){
				totalAmount = getTotalAmountByMemberId(group.getMemberId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
				rb.setMemberId(group.getMemberId());
				rb.setDeductRemark("successTeam");
				if(totalAmount!=null){
					prizeValue = totalAmount.multiply(julyDate.getThousandCase()).intValue();
				}
				rBase.setRewardName(prizeValue + ActivityConstant.popularityDesc);
				rBase.setRewardValue(prizeValue);
				record.setGroupType(group.getGroupType());
				record.setId(group.getId());
				record.setGroupInfo(totalAmount.toString());
				//activityGroupMapper.updateByPrimaryKeySelective(record);
				//获胜战队获人气值
				if(prizeValue>0){
					try {
						drawByPrizeDirectly.drawLottery(rb, rBase, "");
					} catch (Exception e) {
						logger.info("获胜战队送人气值失败, memberId={}, activityId={}", group.getMemberId(), activity.getId());
					}
					MessageClient.sendMsgForCommon(group.getMemberId(),  Constant.MSG_TEMPLATE_TYPE_STAND, MessageEnum.JULY_TEAM.getCode(),
							DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_4),ActivityConstant.ACTIVITY_JULY_TEAM_ZTDZZ_NAME,messageName,totalAmount.toString());
				}
			}
		}
		//更新用户投资总额
		ActivityGroup bizA=new ActivityGroup();
		bizA.setActivityId(activity.getId());
		List<ActivityGroup> allUserList=activityGroupMapper.getActivityGroupBySelective(bizA);
		if(Collections3.isNotEmpty(allUserList)){
			for(ActivityGroup bizGroup:allUserList){
				totalAmount = getTotalAmountByMemberId(bizGroup.getMemberId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
				bizA.setId(bizGroup.getId());
				bizA.setGroupInfo(totalAmount.toString());
				//if(totalAmount.compareTo(BigDecimal.ZERO)>0){
					activityGroupMapper.updateByPrimaryKeySelective(bizA);
				//}
				
			}
		}
		//押对的用户人气值翻倍
		Map<String, Object> map = Maps.newHashMap();
		map.put("activityId", activity.getId());
		map.put("rewardType", TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
		//map.put("remark", groupType);
		map.put("createdStartTime", DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getBetStartTime()));
		map.put("createdEndTime", DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getBetEndTime()));
		List<ActivityLotteryResult> listBet=activityLotteryResultMapper.queryBetBingoMember(map);
		BigDecimal num=new BigDecimal(2);
		if(Collections3.isNotEmpty(listBet)){
			for(ActivityLotteryResult lotterResult:listBet){
				rb.setMemberId(lotterResult.getMemberId());
				rb.setDeductRemark(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate())+"-betjulyteam");
				rBase.setRewardName(num.multiply(new BigDecimal(lotterResult.getChip())) + ActivityConstant.popularityDesc);
				rBase.setRewardValue(num.multiply(new BigDecimal(lotterResult.getChip())).intValue());
				if(lotterResult.getChip().intValue()>0){
					ActivityLotteryResult result=new ActivityLotteryResult();
					result.setId(lotterResult.getId());
					result.setStatus(2);
					if(Integer.parseInt(lotterResult.getRemark())==groupType){
						try {
							drawByPrizeDirectly.drawLottery(rb, rBase, "");
						} catch (Exception e) {
							logger.info("押对用户人气值翻倍失败, memberId={}, activityId={}", lotterResult.getMemberId(), activity.getId());
						}
						result.setRewardResult(num.multiply(new BigDecimal(lotterResult.getChip())).toString());
					}else{
						result.setRewardResult("0");
					}
					activityLotteryResultMapper.updateByPrimaryKeySelective(result);
				}
			}
		}
		Long firstMemberId=1L;
		List<TransactionForFirstInvestAct> currentGroupFirstTen=Lists.newArrayList();
		List<TransactionForFirstInvestAct> jyshFirstTen=selectTopTenInvestByGroupType(ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode(),activity.getId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
		//战队排行前十保存到redis
		List<TransactionForFirstInvestAct> qlyxFirstTen=selectTopTenInvestByGroupType(ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode(),activity.getId(),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()),DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
		if(groupType==ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode()){
			currentGroupFirstTen=jyshFirstTen;
		}else{
			currentGroupFirstTen=qlyxFirstTen;
		}
		ActivityMessage firstTen=new ActivityMessage();
		firstTen.setActivityId(activity.getId());
		firstTen.setTemplateId(1001L);
		firstTen.setRemark(JSON.toJSONString(jyshFirstTen));
		firstTen.setCreateTime(new Date());
		firstTen.setDelFlag(1);
		if(Collections3.isNotEmpty(currentGroupFirstTen)){
			firstMemberId=currentGroupFirstTen.get(0).getMemberId();
			//获胜战队第一名
			messageHistroy.setMemberId(firstMemberId);
			firstTen.setMemberId(firstMemberId);
			Member member=memberMapper.selectByPrimaryKey(firstMemberId);
			if(member==null){
				return;
			}
			//发送短信
			MessageClient.sendShortMessageByMobile(member.getMobile(), ActivityConstant.ACTIVITY_JULY_TEAM_NAME_MSG);
		}else{
			messageHistroy.setMemberId(1L);
			firstTen.setMemberId(1l);
		}
		activityMessageMapper.insertSelective(messageHistroy);
		activityMessageMapper.insertSelective(firstTen);
		firstTen.setTemplateId(1002L);
		firstTen.setRemark(JSON.toJSONString(qlyxFirstTen));
		activityMessageMapper.insertSelective(firstTen);
		
	}
	/**
	 * 
	 * @Description:获胜战队标识保存redis
	 * @param groupType
	 * @author: chaisen
	 * @time:2016年7月7日 上午9:33:12
	 */
	private void putSuccessTeamRedis(int groupType) {
		String key=ActivityConstant.ACTIVITY_JULY_TEAM_SUCCESS_TEAM+ RedisConstant.REDIS_SEPERATOR+ActivityConstant.ACTIVITY_JULY_TEAM_KEY;
		boolean isExit = RedisManager.isExit(key);
		if(isExit){
			RedisManager.removeString(key);
		}
		 RedisManager.putString(key,Integer.toString(groupType));
		
	}

	private int getMaxTransactionTime(Activity activity) {
		int groupType_jysh=ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode();
		int groupType_qlyx=ActivityEnum.JULYTEAM_GROUPTYPE_QLYX.getCode();
		Date jysh=getMaxDate(activity,groupType_jysh);
		Date qlyx=getMaxDate(activity,groupType_qlyx);
		if(jysh.before(qlyx)){
			return groupType_jysh;
		}else{
			return groupType_qlyx;
		}
	}
	private Date getMaxDate(Activity activity,int groupType){
		Date date=DateUtils.getCurrentDate();
		TransactionQuery query = new TransactionQuery();
		ActivityForJulyDate julyDate = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
				ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
		query.setGroupType(groupType);
		query.setTransactionStartTime(DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountStartTime()));
		query.setActivityId(activity.getId());
		query.setTransactionEndTime(DateUtils.getSpecialTime(DateUtils.getCurrentDate(),julyDate.getCountEndTime()));
		Transaction transaction=transactionMapper.selectMaxTransactionTime(query);
		if(transaction!=null){
			return transaction.getTransactionTime();
		}
		return date;
	}
	

	/**
	 * 
	 * @Description:投资总额保存到redis
	 * @param activityJulyTeamJyshKey
	 * @param totalAmountA
	 * @author: chaisen
	 * @time:2016年7月4日 下午7:29:38
	 */
	private void putAmountRedis(String activityJulyTeamJyshKey, BigDecimal totalAmountA) {
		String key=ActivityConstant.ACTIVITY_JULY_TEAM_KEY+ RedisConstant.REDIS_SEPERATOR+activityJulyTeamJyshKey;
		boolean isExit = RedisManager.isExit(key);
		if(isExit){
			RedisManager.removeString(key);
		}
		 RedisManager.putString(key,totalAmountA.toString());
	}
	/**
	 * 
	 * @Description:战队前10名放到redis
	 * @param grouyType
	 * @param firstList
	 * @param field
	 * @author: chaisen
	 * @time:2016年7月4日 下午7:58:13
	 */
	private void putFirstTenAmountRedis(int grouyType,List<TransactionForFirstInvestAct> firstList,String field) {
		String key=ActivityConstant.ACTIVITY_JULY_TEAM_KEY+ RedisConstant.REDIS_SEPERATOR+field+ RedisConstant.REDIS_SEPERATOR+grouyType;
		boolean isExit = RedisManager.isExitByObjectKey(key);
		if(isExit){
			RedisManager.removeObject(key);
		}
		RedisManager.putObject(key, firstList);
		
	}
	private BigDecimal getTotalAmountByMemberId(Long memberId, Date startTime, Date endTime) throws ManagerException {
		TransactionQuery query = new TransactionQuery();
		query.setMemberId(memberId);
		query.setTransactionStartTime(startTime);
		query.setTransactionEndTime(endTime);
		return transactionManager.getMemberTotalInvest(query);
	}
	/**
	 * 
	 * @desc 获取pk历史记录
	 * @return
	 * @author chaisen
	 * @time 2016年7月4日 下午6:46:51
	 *
	 */
	@Override
	public List<ActivityForJulyHistory> getPKforJulyHistory(Long activityId) {
		List<ActivityMessage> pkList=activityMessageMapper.selectPkHistroyByActivityId(activityId);
		List<ActivityForJulyHistory> historyList = Lists.newArrayList();
		if(Collections3.isNotEmpty(pkList)){
			for(ActivityMessage biz:pkList){
				ActivityForJulyHistory history = JSONObject.parseObject(biz.getRemark(), ActivityForJulyHistory.class);
				historyList.add(history);
			}
		}
		return historyList;
	}
	/**
	 * 
	 * @desc 获取红包初始化数量
	 * @param activity
	 * @return
	 * @author chaisen
	 * @time 2016年7月5日 上午11:44:36
	 *
	 */
	@Override
	public ActivityForJulyBet initRemindCoupon(Activity activity) {
		ActivityForJulyDate julyJson = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForJulyDate.class,
				ActivityConstant.ACTIVITY_JULY_TEAM_KEY);
		ActivityForJulyBet biz=new ActivityForJulyBet();
		biz.setCoupon30(julyJson.getTotalCoupon().get(0));
		biz.setCoupon50(julyJson.getTotalCoupon().get(1));
		biz.setCoupon100(julyJson.getTotalCoupon().get(2));
		biz.setCoupon200(julyJson.getTotalCoupon().get(3));
		return biz;
	}
	/**
	 * 
	 * @desc 获取战队投资总额
	 * @param activityId
	 * @param groupType
	 * @return
	 * @author chaisen
	 * @time 2016年7月8日 下午5:18:24
	 *
	 */
	@Override
	public BigDecimal getTotalAmountByActivityId(Long activityId, int groupType) {
		BigDecimal totalAmount=BigDecimal.ZERO;
		String key=ActivityConstant.ACTIVITY_JULY_TEAM_KEY+ RedisConstant.REDIS_SEPERATOR+groupType;
		if(RedisManager.isExit(key)){
			totalAmount=new BigDecimal(RedisManager.getValueByString(key));
		}else{
			ActivityMessage activityMessage=activityMessageMapper.selectTotalAmountByActivityId(activityId);
			if(activityMessage==null){
				return totalAmount;
			}
			if(activityMessage.getRemark()==null){
				return totalAmount;
			}
			ActivityForJulyHistory history = JSONObject.parseObject(activityMessage.getRemark(), ActivityForJulyHistory.class);
			if(history==null){
				return totalAmount;
			}
			if(ActivityEnum.JULYTEAM_GROUPTYPE_JYSH.getCode()==groupType){
				totalAmount=history.getTodayInvestAmountA();
			}else{
				totalAmount=history.getTodayInvestAmountB();
			}
		}
		return totalAmount;
	}
	/**
	 * 
	 * @desc 获取胜利战队
	 * @param activityId
	 * @return
	 * @author chaisen
	 * @time 2016年7月8日 下午5:39:31
	 *
	 */
	@Override
	public int getSuccessTeam(Long activityId) {
		int successFlag=0;
		String successKey=ActivityConstant.ACTIVITY_JULY_TEAM_SUCCESS_TEAM+ RedisConstant.REDIS_SEPERATOR+ActivityConstant.ACTIVITY_JULY_TEAM_KEY;
		if(RedisManager.isExit(successKey)){
			if(RedisManager.getValueByString(successKey)!=null){
				return Integer.parseInt(RedisManager.getValueByString(successKey));
			}
		}else{
			ActivityMessage activityMessage=activityMessageMapper.selectTotalAmountByActivityId(activityId);
			if(activityMessage==null){
				return successFlag;
			}
			ActivityGroup activityGroup=activityGroupMapper.getCurrentMemberGroupBy(activityId, activityMessage.getMemberId());
			if(activityGroup==null){
				return successFlag;
			}
			successFlag=activityGroup.getGroupType();
		}
		return successFlag;
	}

	@Override
	public ActivityData selectActivityDateByActivityId(Long activityId, String dateStrFromDate) throws ManagerException {
		try {
			return activityDataMapper.selectActivityDateByActivityId(activityId, dateStrFromDate);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}