package com.yourong.core.sales.rule;

import java.util.HashMap;
import java.util.Map;

import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.mc.dao.ActivityLotteryMapper;
import com.yourong.core.mc.manager.ActivityHistoryManager;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.SPRuleMethod;
import com.yourong.core.sales.annotation.SPMethod;
import com.yourong.core.sales.manager.MemberRuleManager;

public class MemberSalesRule extends SPRuleBase {
	private static MemberRuleManager memberRuleManager = SpringContextHolder.getBean(MemberRuleManager.class);
	private static ActivityHistoryManager activityHistoryManager = SpringContextHolder.getBean(ActivityHistoryManager.class);
	private static ActivityLotteryMapper activityLotteryMapper = SpringContextHolder.getBean(ActivityLotteryMapper.class);
	
	@Override
	public boolean execute(SPParameter parameter) {
		return super.build(parameter);
	}
	
	
	/**
	 * 会员是否注册成功
	 * @param memberId 会员ID
	 * @return
	 */
	@SPMethod(name="zhuCeChengGong")
	public boolean registrationSuccessful(SPParameter parameter){
		return memberRuleManager.registrationSuccessful(parameter.getMemberId());
	}
	
	/**
	 * 是否开通新浪存钱罐
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="kaiTongCunQianGuan")
	public boolean activateWallet(SPParameter parameter){
		return memberRuleManager.activateWallet(parameter.getMemberId());
		
	}
	
	/**
	 * 是否绑定邮箱
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="youXiangBangDing")
	public boolean bindEmail(SPParameter parameter){
		return memberRuleManager.bindEmail(parameter.getMemberId());
	}
	
	/**
	 * 完善个人资料
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="wanShanXinXi")
	public boolean perfectInformation(SPParameter parameter){
		return memberRuleManager.perfectInformation(parameter.getMemberId());
	}
	
	/**
	 * 用户生日
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="yongHuShengRi")
	public boolean birthday(SPParameter parameter){
		return memberRuleManager.birthday(parameter.getMemberId());
	}
	
	/**
	 * 第一个注册
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="meiTianDiYiZC")
	public boolean firstRegistered(SPParameter parameter){
		return memberRuleManager.firstRegistered(parameter.getMemberId());
	}
	
	/**
	 * 性别
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="xingBie")
	public boolean memberSex(SPParameter parameter, SPRuleMethod method){
		int sex = memberRuleManager.memberSex(parameter.getMemberId());
		int xingBie = Integer.parseInt(method.getValue());
		if(xingBie == sex){
			return true;
		}
		return false;
	}

	/**
	 * 年龄
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="nianLing")
	public boolean memberAge(SPParameter parameter, SPRuleMethod method){
		int age = memberRuleManager.getMemberAge(parameter.getMemberId());
		int nianLing = Integer.parseInt(method.getValue());
		if(method.getRule().equals("1")){
			return age <= nianLing;
		}else{
			return age >= nianLing;
		}
	}
	
	/**
	 * 邀请好友数量
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="yaoQingHaoYouSL")
	public boolean countFriends(SPParameter parameter, SPRuleMethod method){
		int number = memberRuleManager.countFriends(parameter.getMemberId(), DateUtils.getDateFromString("2014-11-19 00:00:00"),DateUtils.getDateFromString("2088-11-19 00:00:00"));
		int shuLiang = Integer.parseInt(method.getValue());
		if(shuLiang >= number){
			return true;
		}
		return false;
	}
	
	/**
	 * 首次邀请好友注册
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="yaoQingHaoYouZhuCe")
	public boolean firstInviteFriendsRegistered(SPParameter parameter){
		return memberRuleManager.firstInviteFriendsRegistered(parameter.getMemberId(), parameter.getStartTime(), parameter.getEndTime());
	}
	
	/**
	 * 好友开通新浪存钱罐
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="haoYouShouCiKTCQG")
	public boolean friendActivateWallet(SPParameter parameter){
		return memberRuleManager.friendActivateWallet(parameter.getMemberId());
	}
	
	/**
	 * 好友绑定邮箱
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="haoYouShouCiBDYX")
	public boolean friendBindEmail(SPParameter parameter){
		return memberRuleManager.friendBindEmail(parameter.getMemberId());
	}
	
	/**
	 * 好友完善信息
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="haoYouShouCiWSXX")
	public boolean friendPerfectInformation(SPParameter parameter){
		return memberRuleManager.friendPerfectInformation(parameter.getMemberId());
	}
	
	/**
	 * 首次绑定微信
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="firstBindingWeiXin")
	public boolean isFirstBindingWeiXin(SPParameter parameter){
		return memberRuleManager.isFirstBindingWeiXin(parameter.getMemberId(), parameter.getStartTime());
	}
	
	/**
	 * 首次体验app
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="firstBindingApp")
	public boolean isFirstBindingApp(SPParameter parameter){
		return memberRuleManager.isFirstBindingApp(parameter.getMemberId(), parameter.getStartTime());
	}
	
	/**
	 * 判断是否已经参加过活动, 未参加过返回TRUE
	 * @param parameter
	 * @return
	 * @throws ManagerException 
	 */
	@SPMethod(name="isParticipateInActivity")
	public boolean isParticipateInActivity(SPParameter parameter) throws ManagerException{
		boolean flag = RedisActivityClient.isParticipateInActivity(parameter.getActivityId(), parameter.getMemberId());
		if(!flag){
			flag = activityHistoryManager.isParticipateInActivity(parameter.getMemberId(), parameter.getActivityId());
			if(flag){
				RedisActivityClient.setActivitiesMember(parameter.getActivityId(), parameter.getMemberId());
			}
		}
		if(!flag) {
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("memberId", parameter.getMemberId());
			paraMap.put("activityId", parameter.getActivityId());
			paraMap.put("cycleConstraint", parameter.getActivityId());
			paraMap.put("realCount", 1);
			ActivityLottery al = activityLotteryMapper.selectByMemberActivity(paraMap);
			if(al != null) {
				return true;
			} else {
				return false;
			}
		}
		return !flag;
	}
	
	/**
	 * 会员是否注册成功
	 * @param memberId 会员ID
	 * @return
	 */
	@SPMethod(name = "registerFromRedPackage")
	public boolean registerByRedPackageSuccessful(SPParameter parameter){
		return memberRuleManager.registerByRedPackageSuccessful(parameter.getMemberId());
	}
}
