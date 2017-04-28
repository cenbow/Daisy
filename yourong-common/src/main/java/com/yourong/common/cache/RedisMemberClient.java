package com.yourong.common.cache;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.constant.RedisConstant;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

/**
 * 会员redis相关
 */
public class RedisMemberClient {

	private static final Logger log = LoggerFactory
			.getLogger(RedisMemberClient.class);

	/**
	 * 记录用户注册时 发送短信次数
	 * 
	 * @param mobile
	 * @return
	 */
	public static void setMemberMobileSMSCount(Long mobile) {
		String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR
				+ mobile;
		RedisManager.hincrBy(key, RedisConstant.REDIS_FIELD_USER_SEND_SMS_COUNT,1L);
		RedisManager.expire(key, DateUtils.calculateCurrentToEndTime());
	}

	/**
	 * 记录用户 验证次数
	 *
	 * @param mobile
	 * @return
	 */
	public static void setUserCheckCount(Long mobile) {
		String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR	+ mobile;
		RedisManager.hincrBy(key, RedisConstant.REDIS_FIELD_USER_CHECK_SMS_COUNT,1L);
		RedisManager.expire(key, DateUtils.calculateCurrentToEndTime());
	}

	/**
	 * 获取用户注册时 发送短信次数
	 *
	 * @param mobile
	 * @return
	 */
	public static Integer getUserCheckCount(Long mobile) {
		String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR
				+ mobile;
		String counts = RedisManager.hget(key,RedisConstant.REDIS_FIELD_USER_CHECK_SMS_COUNT);		
		if (StringUtil.isNumeric(counts)) {
			return Integer.valueOf(counts);
		}
		return 0;
	}





	/**
	 * 设置用户短信验证码，
	 * @param mobile
	 */
	public static void setUserMobileSMScode(Long mobile){

	}


	/**
	 * 获取用户注册时 发送短信次数
	 * 
	 * @param mobile
	 * @return
	 */
	public static Integer getMemberMobileSMSCount(Long mobile) {
		String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR
				+ mobile;
		String hget = RedisManager.hget(key,RedisConstant.REDIS_FIELD_USER_SEND_SMS_COUNT);
		if (log.isDebugEnabled()) {
			log.debug("key:" + mobile + " value:" + hget);
		}

		if (StringUtil.isNumeric(hget)) {
			return Integer.valueOf(hget);
		}
		return 0;
	}
	
	/**
	 * 登录成功后 清空发送短信次数
	 * 
	 * @param mobile
	 */
	public static void removeMemberMobileSMSCount(Long mobile) {
		String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR + mobile;
		RedisManager.hdel(key, RedisConstant.REDIS_FIELD_USER_SEND_SMS_COUNT);
	}

	/**
	 * 记录用户注册时 发送语音次数
	 * 
	 * @param mobile
	 * @return
	 */
	public static void setMemberMobileVoiceCount(Long mobile) {
		String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR
				+ mobile;
		RedisManager.hincrBy(key, RedisConstant.REDIS_FIELD_USER_SEND_VOICE_COUNT,
				1L);
		RedisManager.expire(key, DateUtils.calculateCurrentToEndTime());
	}

	/**
	 * 获取用户注册时 发送语音次数
	 * 
	 * @param mobile
	 * @return
	 */
	public static Integer getMemberMobileVoiceCount(Long mobile) {
		String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR
				+ mobile;
		String hget = RedisManager.hget(key,RedisConstant.REDIS_FIELD_USER_SEND_VOICE_COUNT);
		if (StringUtil.isNumeric(hget)) {
			return Integer.valueOf(hget);
		}
		return 0;
	}
	
	/**
	 * 登录成功后 清空发送语音次数
	 * 
	 * @param mobile
	 */
	public static void removeMemberMobileVoiceCount(Long mobile) {
		String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR + mobile;
		RedisManager.hdel(key, RedisConstant.REDIS_FIELD_USER_SEND_VOICE_COUNT);
	}

	/**
	 * 记录用户注登录次数
	 * 
	 * @param userName
	 * @return
	 */
	public static void setMemberLoginCount(String userName) {
		String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR
				+ userName;
		RedisManager.hincrBy(key, RedisConstant. REDIS_FIELD_USER_LOGIN_COUNT,1L);
		RedisManager.expire(key, DateUtils.calculateCurrentToEndTime());
	}

	/**
	 * 获取用户登录次数
	 * 
	 * @param userName
	 * @return
	 */
	public static Integer getMemberLoginCount(String userName) {
		String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR + userName;
		String hget = RedisManager.hget(key, RedisConstant.REDIS_FIELD_USER_LOGIN_COUNT);
		if (StringUtil.isNumeric(hget)) {
			return Integer.valueOf(hget);
		}
		return 0;
	}

    public static void removeMemberLoginCount(String userName) {
        String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR + userName;
         RedisManager.hdel(key, RedisConstant.REDIS_FIELD_USER_LOGIN_COUNT);
    }


    public static Integer getCountWithDraw(Long memberID){
		return 0;
	}
	
	
	

	/**
	 * 设置用户存钱罐余额
	 * 
	 * @param memberId
	 * @param sinaPayBalance
	 * @return
	 */
	public static boolean setSinaPayBalance(Long memberId,
			BigDecimal sinaPayBalance) {
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_SINAPAY_BALANCE,
				sinaPayBalance.toPlainString());

	}

	public static BigDecimal getSinaPayBalance(Long memberId) {
		String sinaPayBalanceStr = RedisManager.hget(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId,
				RedisConstant.REDIS_FIELD_MEMBER_SINAPAY_BALANCE);
		return new BigDecimal(sinaPayBalanceStr).setScale(2,
				BigDecimal.ROUND_HALF_UP);

	}
	
	/**
	 * 设置会员头像
	 * @param memberId
	 * @param avatar
	 * @return
	 */
	public static boolean setMemberAvatar(Long memberId, String avatar){
		String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+ memberId;
		return RedisManager.hset(key, RedisConstant.REDIS_FIELD_MEMBER_AVATAR, avatar);
	}
	
	/**
	 * 获取会员头像
	 * @param memberId
	 * @return
	 */
	public static String getMemberAvatar(Long memberId){
		String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+ memberId;
		return RedisManager.hget(key, RedisConstant.REDIS_FIELD_MEMBER_AVATAR);
	}
	
	/**
	 * 设置用户详情信息完善状态
	 * @param memberId
	 * @param status
	 * @return
	 */
	public static boolean setMemberInfoCompleteStatus(Long memberId, Boolean status){
		String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+ memberId;
		return RedisManager.hset(key, RedisConstant.REDIS_FIELD_MEMBER_INFO_COMPLETE_STATUS, status.toString());
		
	}
	
	/**
	 * 获取用户详情信息完善状态
	 * @param memberId
	 * @return
	 */
	public static String getMemberInfoCompleteStatus(Long memberId){
		String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+ memberId;
		return RedisManager.hget(key, RedisConstant.REDIS_FIELD_MEMBER_INFO_COMPLETE_STATUS);
	}

	
	/**
	 * 设置充值成功笔数
	 * 
	 * @param memberId
	 * @param count
	 * @return
	 */
	public static boolean setRechargeSuccessCount(Long memberId,
			int count) {
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_RECHARGE_COUNT,
				count+"");

	}

	/**
	 * 获取充值成功笔数
	 * @param memberId
	 * @return
	 */
	public static int getRechargeSuccessCount(Long memberId) {
		String count = RedisManager.hget(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId,
				RedisConstant.REDIS_FIELD_MEMBER_RECHARGE_COUNT);
		if(StringUtil.isNotBlank(count)) {
			return Integer.parseInt(count);
		} else {
			return 0;
		}
	}
	
	/**
	 * 增加充值成功笔数
	 * @param memberId
	 * @param count
	 * @return
	 */
	public static boolean addRechargeSuccessCount(Long memberId,
			int count) {
		Integer totalCount = getRechargeSuccessCount(memberId)+count;
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_RECHARGE_COUNT,
				totalCount.toString());

	}
	
	/**
	 * 设置充值成功金额
	 * 
	 * @param memberId
	 * @param count
	 * @return
	 */
	public static boolean setRechargeSuccessAmount(Long memberId,
			BigDecimal Amount) {
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_RECHARGE_AMOUNT,
				Amount.toPlainString());

	}

	/**
	 * 获取充值成功金额
	 * @param memberId
	 * @return
	 */
	public static BigDecimal getRechargeSuccessAmount(Long memberId) {
		String Amount = RedisManager.hget(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId,
				RedisConstant.REDIS_FIELD_MEMBER_RECHARGE_AMOUNT);
		if(StringUtil.isNotBlank(Amount)) {
			return new BigDecimal(Amount).setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * 增加充值成功金额
	 * @param memberId
	 * @param count
	 * @return
	 */
	public static boolean addRechargeSuccessAmount(Long memberId,
			BigDecimal amount) {
		BigDecimal totalAmount = getRechargeSuccessAmount(memberId).add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_RECHARGE_AMOUNT,
				totalAmount.toPlainString());

	}
	
	/**
	 * 设置提现成功笔数
	 * 
	 * @param memberId
	 * @param count
	 * @return
	 */
	public static boolean setWithdrawSuccessCount(Long memberId,
			int count) {
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_WITHDRAW_COUNT,
				count+"");

	}

	/**
	 * 获取提现成功笔数
	 * @param memberId
	 * @return
	 */
	public static int getWithdrawSuccessCount(Long memberId) {
		String count = RedisManager.hget(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId,
				RedisConstant.REDIS_FIELD_MEMBER_WITHDRAW_COUNT);
		if(StringUtil.isNotBlank(count)) {
			return Integer.parseInt(count);
		} else {
			return 0;
		}
	}
	
	/**
	 * 增加提现成功笔数
	 * @param memberId
	 * @param count
	 * @return
	 */
	public static boolean addWithdrawSuccessCount(Long memberId,
			int count) {
		Integer totalCount = getWithdrawSuccessCount(memberId)+count;
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_WITHDRAW_COUNT,
				totalCount.toString());

	}
	
	
	/**
	 * 设置提现成功金额
	 * 
	 * @param memberId
	 * @param count
	 * @return
	 */
	public static boolean setWithdrawSuccessAmount(Long memberId,
			BigDecimal Amount) {
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_WITHDRAW_AMOUNT,
				Amount.toPlainString());

	}

	/**
	 * 获取提现成功金额
	 * @param memberId
	 * @return
	 */
	public static BigDecimal getWithdrawSuccessAmount(Long memberId) {
		String Amount = RedisManager.hget(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId,
				RedisConstant.REDIS_FIELD_MEMBER_WITHDRAW_AMOUNT);
		if(StringUtil.isNotBlank(Amount)) {
			return new BigDecimal(Amount).setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * 增加提现成功金额
	 * @param memberId
	 * @param count
	 * @return
	 */
	public static boolean addWithdrawSuccessAmount(Long memberId,
			BigDecimal amount) {
		BigDecimal totalAmount = getWithdrawSuccessAmount(memberId).add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_WITHDRAW_AMOUNT,
				totalAmount.toPlainString());

	}
	
	/**
	 * 设置用户累计投资额
	 * 
	 * @param memberId
	 * @param count
	 * @return
	 */
	public static boolean setTotalInvestAmount(Long memberId,
			BigDecimal Amount) {
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_TOTAL_INVEST_AMOUNT,
				Amount.toPlainString());

	}

	/**
	 * 获取用户累计投资额
	 * @param memberId
	 * @return
	 */
	public static BigDecimal getTotalInvestAmount(Long memberId) {
		String Amount = RedisManager.hget(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId,
				RedisConstant.REDIS_FIELD_MEMBER_TOTAL_INVEST_AMOUNT);
		if(StringUtil.isNotBlank(Amount)) {
			return new BigDecimal(Amount).setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * 增加用户累计投资额
	 * @param memberId
	 * @param count
	 * @return
	 */
	public static boolean addTotalInvestAmount(Long memberId,
			BigDecimal amount) {
		BigDecimal totalAmount = getTotalInvestAmount(memberId).add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+memberId ,
				RedisConstant.REDIS_FIELD_MEMBER_TOTAL_INVEST_AMOUNT,
				totalAmount.toPlainString());

	}
	
	/**
	 * 验证邮箱设置邮箱token值
	 * @param memberId
	 * @param email
	 * @param emailToken
	 * @return
	 */
	public static boolean setEmailToken(Long memberId, String email, String emailToken) {
		String value = memberId + RedisConstant.REDIS_SEPERATOR+email;
		boolean flag = RedisManager.set(emailToken , value);
		RedisManager.expire(emailToken, 3600*24);
		return flag;
	}
	
	/**
	 * 获取验证邮箱设置邮箱token值
	 * @param memberId
	 * @param email
	 * @return
	 */
	public static String getEmailToken(String emailToken) {
		return RedisManager.get(emailToken);
	}
	
	/**
	 * 获取验证邮箱设置邮箱token值
	 * @param memberId
	 * @param email
	 * @return
	 */
	public static void removeEmailToken(String emailToken) {
		 RedisManager.removeString(emailToken);
	}
	
	/**
	 * 设置邀请码放入redis
	 * @param inviteCode
	 * author: pengyong
	 * 上午11:39:00
	 */
	public static void  setMemberInviteCode(String inviteCode,Long memberID){		
		RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER_INVITECODE, inviteCode,memberID.toString());
	}
	/***
	 *  判断邀请码是否存在
	 * @param inviteCode
	 * @return
	 * author: pengyong
	 * 上午11:51:03
	 */
	public static boolean isExistMemberInviteCode(String inviteCode){
		String code  = RedisManager.hget(RedisConstant.REDIS_KEY_MEMBER_INVITECODE, inviteCode);
		if(StringUtil.isNotBlank(code)){
			return true;	
		}		
		return false; 
	}
	
	/**
	 * 设置会员昵称
	 * @param memberId
	 * @param userName
	 * @return
	 */
	public static boolean setMemberUserName(Long memberId, String userName){
		String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+ memberId;
		return RedisManager.hset(key, RedisConstant.REDIS_FIELD_MEMBER_USERNAME, userName);
	}
	
	/**
	 * 获取会员昵称
	 * @param memberId
	 * @return
	 */
	public static String getMemberUserName(Long memberId){
		String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR+ memberId;
		return RedisManager.hget(key, RedisConstant.REDIS_FIELD_MEMBER_USERNAME);
	}
	
	/**
	 * 设置用户邮箱退订Key
	 */
	public static boolean setEmailUnsubscribe(Long memberId, String key){
		return RedisManager.hset(RedisConstant.REDIS_KEY_MEMBER_EMAIL_UNSUBSCRIBE, key, memberId.toString());
	}
	
	/**
	 * 获得邮箱退订用户
	 * @param unsubscribe
	 * @return
	 */
	public static String getEmailUnsubscribe(String unsubscribe){
		String value = RedisManager.hget(RedisConstant.REDIS_KEY_MEMBER_EMAIL_UNSUBSCRIBE, unsubscribe);
		return value;
	}
	
	/**
	 * 设置微信解绑指令
	 * @param weixin
	 * @param memberId
	 */
	public static void setWeiXinUnbundlingDirective(String weixin, Long memberId){
		String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR
				+ memberId;
		RedisManager.hset(key, RedisConstant. REDIS_FIELD_WEIXIN_UNBUNDLING_DIRECTIVE,memberId.toString());
	}
	
	/**
	 * 获得微信解绑指令
	 * @param memberId
	 * @return
	 */
	public static Long getWeiXinUnbundlingDirective(Long memberId){
		String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR + memberId;
		String hget = RedisManager.hget(key, RedisConstant.REDIS_FIELD_WEIXIN_UNBUNDLING_DIRECTIVE);
		if (StringUtil.isNumeric(hget)) {
			return Long.valueOf(hget);
		}
		return 0L;
	}
	
	/**
	 * 删除微信解绑指令
	 * @param memberId
	 */
	public static  void  removeWeiXinUnbundlingDirective(Long memberId) {
        String key = RedisConstant.REDIS_KEY_MEMBER + RedisConstant.REDIS_SEPERATOR
                + memberId;
         RedisManager.hdel(key, RedisConstant.REDIS_FIELD_WEIXIN_UNBUNDLING_DIRECTIVE);
    }
	/**
	 * 设置注册时用过得IP
	 * @param ip
	 * @param seconds
	 * author: wanglei
	 * time 2017-01-12
	 */
	public static void  setUserRegisterIp(String ip,int seconds){	
		RedisManager.set(RedisConstant.REDIS_KEY_USER_RESTRICT_IP+ip, "1");
		RedisManager.expire(RedisConstant.REDIS_KEY_USER_RESTRICT_IP+ip, seconds);
	}
	/***
	 *  判断是否还在限制期的Ip
	 * @param ip 
	 * @return false：未失效，不可注册；true：失效，可注册。
	 * author: wanglei
	 * time 2017-01-12
	 */
	public static boolean isExitRegistedIp(String ip){
		return RedisManager.isExit(RedisConstant.REDIS_KEY_USER_RESTRICT_IP+ip);
	}
	
	

}
