package com.yourong.core.sales.manager.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.core.sales.manager.MemberRuleManager;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberTokenManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;
import com.yourong.core.uc.model.MemberToken;

@Service
public class MemberRuleManagerImpl implements MemberRuleManager {
	private static final Logger logger = LoggerFactory.getLogger(MemberRuleManagerImpl.class);
	@Autowired
	MemberManager memberManager;
	@Autowired
	MemberInfoManager memberInfoManager;
	@Autowired
	MemberTokenManager memberTokenManager;

	@Override
	public boolean registrationSuccessful(Long memberId) {
		return true;
	}

	@Override
	public boolean activateWallet(Long memberId) {
		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			//通过身份证和真实姓名判断是否开通
			if(member != null && StringUtil.isNotEmpty(member.getIdentityNumber()) && StringUtil.isNotEmpty(member.getTrueName())){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("判断会员是否开通新浪存钱罐出现异常",e);
		}
		return false;
	}

	@Override
	public boolean bindEmail(Long memberId) {
		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			//通过身份证和真实姓名判断是否开通
			if(member != null && StringUtil.isNotEmpty(member.getEmail())){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("判断会员是否绑定邮箱出现异常",e);
		}
		return false;
	}

	@Override
	public boolean perfectInformation(Long memberId) {
		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			if(member != null){
				if(StringUtil.isNotEmpty(member.getUsername())){
					MemberInfo memberInfo = memberInfoManager.getMemberInfoByMemberId(memberId);
					logger.info("判断会员是否完善信息"+memberId);
					if(memberInfo != null){
						return true;
					}
				}
			}else{
				logger.error("未找到member用户信息,memberId="+memberId);
			}
		} catch (ManagerException e) {
			logger.error("判断会员是否完善信息出现异常",e);
		}
		return false;
	}

	@Override
	public boolean birthday(Long memberId) {
		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			if(member.getBirthday() != null){
				String birthday = DateUtils.formatDatetoString(member.getBirthday(),"MM-dd");
				String today = DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM-dd");
				if(birthday.equals(today)){
					return true;
				}
			}
		} catch (ManagerException e) {
			logger.error("判断会员今天是否生日出现异常",e);
		}		
		return false;
	}

	@Override
	public boolean firstRegistered(Long memberId) {
		try {
			Member member = memberManager.todayFirstRegistered();
			if(member != null && member.getId().equals(memberId)){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("判断会员是否第一个注册出现异常",e);
		}
		return false;
	}

	@Override
	public int memberSex(Long memberId) {
		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			if(member != null && member.getSex() != null){
				return member.getSex();
			}
		} catch (ManagerException e) {
			logger.error("查询会员性别出现异常",e);
		}
		return -1;
	}

	@Override
	public int getMemberAge(Long memberId) {
		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			if(member != null && member.getBirthday()!=null){
				int by = DateUtils.getYear(member.getBirthday().getTime());
				int ty = DateUtils.getYear(DateUtils.getCurrentDate().getTime());
				int _age = ty - by;
				return _age;
			}
		} catch (ManagerException e) {
			logger.error("查询会员年龄出现异常",e);
		}
		return -1;
	}

	@Override
	public int countFriends(Long memberId, Date registerStartTime, Date registerEndTime) {
		try {
			return memberManager.countFriends(memberId, registerStartTime, registerEndTime);
		} catch (ManagerException e) {
			logger.error("统计会员好友数量出现异常",e);
		}
		return -1;
	}

	@Override
	public boolean firstInviteFriendsRegistered(Long memberId, Date registerStartTime, Date registerEndTime) {
		try {
			int number = memberManager.countFriends(memberId, DateUtils.getDateFromString("2014-11-19 00:00:00"),DateUtils.getDateFromString("2088-11-19 00:00:00"));
			if(number == 1){
				if(memberManager.countFriends(memberId, registerStartTime, registerEndTime)==1){
					return true;
				}
			}
		} catch (ManagerException e) {
			logger.error("查询会员是否首次邀请好友注册出现异常",e);
		}
		return false;
	}

	@Override
	public boolean friendActivateWallet(Long memberId) {
		try {
			int number = memberManager.countFriendsActivateWallet(memberId);
			if(number >= 1){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("查询好友是否开通新浪存钱罐出现异常",e);
		}
		return false;
	}

	@Override
	public boolean friendBindEmail(Long memberId) {
		try {
			int number = memberManager.countFriendsBindEmail(memberId);
			if(number >= 1){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("查询好友是否绑定邮箱出现异常",e);
		}
		return false;
	}

	@Override
	public boolean friendPerfectInformation(Long memberId) {
		try {
			int number = memberManager.countFrinedsPerfectInformation(memberId);
			if(number >= 1){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("查询好友是否首次完善信息出现异常",e);
		}
		return false;
	}

	@Override
	public boolean isFirstBindingWeiXin(Long memberId, Date startTime) {
		try {
			MemberToken token = memberTokenManager.selectFirstLoginWeiXin(memberId);
			if(token != null && token.getCreateTime().after(startTime)){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("会员是否首次绑定微信",e);
		}
		return false;
	}

	@Override
	public boolean isFirstBindingApp(Long memberId, Date startTime) {
		try {
			MemberToken token = memberTokenManager.selectFirstLoginApp(memberId);
			if(token != null && token.getCreateTime().after(startTime)){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("会员是否首次绑定APP异常",e);
		}
		return false;
	}

	@Override
	public boolean registerByRedPackageSuccessful(Long memberId) {
		try {
			Member member = memberManager.registerByRedPackageSuccessful(memberId);
			if (member != null) {
				return true;
			}
		} catch (Exception e) {
			logger.error("参加过红包的会员注册异常", e);
		}
		return false;
	}

}
