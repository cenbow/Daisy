package com.yourong.core.sales.manager;

import java.util.Date;

public interface MemberRuleManager {

	/**
	 * 会员是否注册成功
	 * @param memberId 会员ID
	 * @return
	 */
	public boolean registrationSuccessful(Long memberId);
	
	/**
	 * 是否开通新浪存钱罐
	 * @param memberId
	 * @return
	 */
	public boolean activateWallet(Long memberId);
	
	/**
	 * 是否绑定邮箱
	 * @param memberId
	 * @return
	 */
	public boolean bindEmail(Long memberId);
	
	/**
	 * 完善个人资料
	 * @param memberId
	 * @return
	 */
	public boolean perfectInformation(Long memberId);
	
	/**
	 * 用户生日
	 * @param memberId
	 * @return
	 */
	public boolean birthday(Long memberId);
	
	/**
	 * 第一个注册
	 * @param memberId
	 * @return
	 */
	public boolean firstRegistered(Long memberId);
	
	/**
	 * 会员性别
	 * @param member
	 * @return
	 */
	public int memberSex(Long member); 
	
	/**
	 * 获得用户年龄
	 * @param memberId
	 * @param age
	 * @param type
	 * @return
	 */
	public int getMemberAge(Long memberId);
	
	/**
	 * 好友数量
	 * @param memberId
	 * @return
	 */
	public int countFriends(Long memberId, Date registerStartTime, Date registerEndTime);
	
	/**
	 * 首次邀请好友注册
	 * @param memberId
	 * @return
	 */
	public boolean firstInviteFriendsRegistered(Long memberId, Date registerStartTime, Date registerEndTime);
	
	/**
	 * 好友开通新浪存钱罐
	 * @param memberId
	 * @return
	 */
	public boolean friendActivateWallet(Long memberId);
	
	/**
	 * 好友绑定邮箱
	 * @param memberId
	 * @param registerStartTime
	 * @param registerEndTime
	 * @return
	 */
	public boolean friendBindEmail(Long memberId);
	
	/**
	 * 好友完善信息
	 * @param memberId
	 * @return
	 */
	public boolean friendPerfectInformation(Long memberId);
	
	/**
	 * 是否首次绑定威信
	 * @param memberId
	 * @param startTime
	 * @return
	 */
	public boolean isFirstBindingWeiXin(Long memberId, Date startTime);
	
	/**
	 * 是否首次绑定APP
	 * @param memberId
	 * @param startTime
	 * @return
	 */
	public boolean isFirstBindingApp(Long memberId, Date startTime);

	/**
	 * 参加过红包的会员注册
	 * 
	 * @param memberId
	 * @param startTime
	 * @return
	 */
	public boolean registerByRedPackageSuccessful(Long memberId);
}
