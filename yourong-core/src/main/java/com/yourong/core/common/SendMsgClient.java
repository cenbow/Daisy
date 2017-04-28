package com.yourong.core.common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberNotifySettingsManager;
import com.yourong.core.uc.model.biz.MemberNotifySettingsForSendMsg;

/**
 * 发送消息redis客户端
 */
public class SendMsgClient {

	private static final Logger logger = LoggerFactory
			.getLogger(SendMsgClient.class);
	private static MemberNotifySettingsManager memberNotifySettingsManager = SpringContextHolder.getBean(MemberNotifySettingsManager.class);
	private static MemberManager memberManager = SpringContextHolder.getBean(MemberManager.class);
	private static SysDictManager sysDictManager = SpringContextHolder.getBean(SysDictManager.class);
	/**
	 * 发送消息 ，使用redis队列
	 * @param memberId 会员ID
	 * @param notiyType 通知类型
	 * @param args 将指定字符串中的每个格式项替换为相应对象的值的文本等效项
	 */
	public static void pushMessageToRedis(MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg, String... args){
		
		try {
			List<SysDict> sysDicts = sysDictManager.findByGroupName("notify_type");
			Map<String, SysDict> map = Collections3.extractToMap(sysDicts,"value");
			SysDict dict = map.get(String.valueOf(memberNotifySettingsForSendMsg.getNotiyType()));
			// 发送内容
			if(StringUtil.isBlank(dict.getDescription())){
				return;
			}		
			String message = String.format(dict.getDescription(), args);
			memberNotifySettingsForSendMsg.setMessage(message);
			
			RedisManager.rpush(RedisConstant.REDIS_KEY_SENDMESSAGE, JSONObject.toJSONString(memberNotifySettingsForSendMsg));
		} catch (ManagerException e) {
			logger.error("发送消息 发生异常", e);
		}
		
		
	}
	/**
	 * 从队列里获取消息
	 * @return
	 */
	public static String popSendMsgFromRedis(){				
		return RedisManager.rpop(RedisConstant.REDIS_KEY_SENDMESSAGE);
	}
	
	/**
	 * 充值成功发送通知
	 * @param memberId	用户id
	 * @param rechargeTime	充值时间
	 * @param amount	充值金额
	 * @return
	 */
	public static boolean sendRechargeMsg(Long memberId, Date rechargeTime, BigDecimal amount) {
		MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = null;
		try {
			//获得该用户消息通知设置信息
			memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_RECHARGE_SUCCESS.getType(), memberId);
			pushMessageToRedis(
					memberNotifySettingsForSendMsg,
					DateUtils.getStrFromDate(rechargeTime, DateUtils.DATE_FMT_7),
					amount.toPlainString()
					);
		} catch (Exception e) {
			logger.error("[充值成功发送通知]发生异常,memberNotifySettingsForSendMsg="+memberNotifySettingsForSendMsg);
		}
		
		return true;
	}
	
	/**
	 * 提现成功发送通知
	 * @param memberId	用户id
	 * @param amount	提现金额
	 * @return
	 */
	public static boolean sendWithdrawSuccessMsg(Long memberId, BigDecimal amount) {
		MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = null;
		try {
			memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_WITHDRAW_SUCCESS.getType(), memberId);
			pushMessageToRedis(
					memberNotifySettingsForSendMsg, 
					amount.toPlainString()
					);
		} catch (Exception e) {
			logger.error("[ 提现成功发送通知]发生异常,memberNotifySettingsForSendMsg="+memberNotifySettingsForSendMsg);
		}
		return true;
	}
	
	/**
	 * 提现拒绝发送通知
	 * @param memberId	用户id
	 * @param withdrawTime	提现时间
	 * @return
	 */
	public static boolean sendWithdrawRefuseMsg(Long memberId, Date withdrawTime) {
		MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = null;
		try {
			memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_WITHDRAW_REFUSE.getType(), memberId);
			pushMessageToRedis(
					memberNotifySettingsForSendMsg, 
					DateUtils.getStrFromDate(withdrawTime, DateUtils.DATE_FMT_7)
					);
		} catch (Exception e) {
			logger.error("[提现拒绝发送通知]发生异常,memberNotifySettingsForSendMsg="+memberNotifySettingsForSendMsg);
		}
		return true;
	}
	
	/**
	 * 提现失败发送通知
	 * @param memberId	用户id
	 * @param withdrawTime	提现时间
	 * @return
	 */
	public static boolean sendWithdrawFailedMsg(Long memberId, Date withdrawTime) {
		MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = null;
		try {
			memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_WITHDRAW_FAILED.getType(), memberId);
			pushMessageToRedis(
					memberNotifySettingsForSendMsg, 
					DateUtils.getStrFromDate(withdrawTime, DateUtils.DATE_FMT_7)
					);
		} catch (Exception e) {
			logger.error("[提现失败发送通知]发生异常,memberNotifySettingsForSendMsg="+memberNotifySettingsForSendMsg);
		}
		return true;
	}
	
	/**
	 * 项目预告发送通知
	 * @param projectName	项目名称
	 * @param bigDecimal 
	 * @param days 
	 * @param onlineTime	上线时间
	 * @return
	 */
	public static boolean sendProjectNoticeMsg(String projectName, int days, BigDecimal totalAmount, Date onlineTime) {
		MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = null;
		try {
			long maxMember = memberManager.getMaxMemberId();
			for (long i = Long.parseLong(Config.firstMemberId); i <= maxMember; i++) {
				memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_PROJECT_NOTICE.getType(), i);
				if(memberNotifySettingsForSendMsg!=null) {
					pushMessageToRedis(
							memberNotifySettingsForSendMsg, 
							projectName,
							days+"",
							totalAmount.doubleValue()/10000+"",
							DateUtils.getStrFromDate(onlineTime, DateUtils.DATE_FMT_7)
							);
				}
				
			}
			
		} catch (Exception e) {
			logger.error("[项目预告发送通知]发生异常,memberNotifySettingsForSendMsg="+memberNotifySettingsForSendMsg, e);
		}
		return true;
	}
	
	/**
	 * 项目上线发送通知
	 * @param projectName
	 * @return
	 */
	public static boolean sendProjectOnlineMsg(String projectName, int days, BigDecimal totalAmount) {
		MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = null;
		try {
			long maxMember = memberManager.getMaxMemberId();
			for (long i = Long.parseLong(Config.firstMemberId); i <= maxMember; i++) {
				memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_PROJECT_ONLINE.getType(), i);
				if(memberNotifySettingsForSendMsg!=null) {
					pushMessageToRedis(
							memberNotifySettingsForSendMsg, 
							projectName,
							days+"",
							totalAmount.doubleValue()/10000+""
							);
				}
				
			}
			
		} catch (Exception e) {
			logger.error("[项目上线发送通知]发生异常,memberNotifySettingsForSendMsg="+memberNotifySettingsForSendMsg);
		}
		return true;
	}
	
	/**
	 * 本金归还提醒
	 * @param memberId	会员id
	 * @param payTime	支付时间
	 * @param amount	支付金额
	 * @return	
	 */
	public static boolean sendPayPrincipalMsg(Long memberId, Date payTime, BigDecimal amount) {
		MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = null;
		try {
			memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_PAY_PRINCIPAL.getType(), memberId);
			pushMessageToRedis(
					memberNotifySettingsForSendMsg, 
					DateUtils.getStrFromDate(payTime, DateUtils.DATE_FMT_7),
					amount.toPlainString()
					);
		} catch (Exception e) {
			logger.error("[本金归还提醒]发生异常,memberNotifySettingsForSendMsg="+memberNotifySettingsForSendMsg);
		}
		return true;
	}
	
	/**
	 * 利息支付提醒
	 * @param memberId	会员id
	 * @param payTime	支付时间
	 * @param amount	支付金额
	 * @return	
	 */
	public static boolean sendPayInterestMsg(Long memberId, Date payTime, BigDecimal amount) {
		MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = null;
		try {
			memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_PAY_INTEREST.getType(), memberId);
			pushMessageToRedis(
					memberNotifySettingsForSendMsg, 
					DateUtils.getStrFromDate(payTime, DateUtils.DATE_FMT_7),
					amount.toPlainString()
					);
		} catch (Exception e) {
			logger.error("[利息支付提醒]发生异常,memberNotifySettingsForSendMsg="+memberNotifySettingsForSendMsg);
		}
		return true;
	}
	
	/**
	 * 成功投资提醒
	 * @param memberId
	 * @param transactionTime
	 * @param projectName
	 * @return
	 */
	public static boolean sendTransactionSuccessMsg(Long memberId, Date transactionTime, BigDecimal amount) {
		MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = null;
		try {
			memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_TRANSACTION_SUCCESS.getType(), memberId);
			pushMessageToRedis(
					memberNotifySettingsForSendMsg, 
					DateUtils.getStrFromDate(transactionTime, DateUtils.DATE_FMT_7),
					amount.toPlainString()
					);
		} catch (Exception e) {
			logger.error("[成功投资提醒]发生异常,memberNotifySettingsForSendMsg="+memberNotifySettingsForSendMsg);
		}
		return true;
	}
	
	/**
	 * 订单即将失效提醒
	 * @param memberId
	 * @return
	 */
	public static boolean sendOrderInvalidMsg(Long memberId) {
		MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = null;
		try {
			memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_ORDER_INVALID.getType(), memberId);
			pushMessageToRedis(
					memberNotifySettingsForSendMsg);
		} catch (Exception e) {
			logger.error("[订单即将失效提醒]发生异常,memberNotifySettingsForSendMsg="+memberNotifySettingsForSendMsg);
		}
		return true;
	}
	

}
