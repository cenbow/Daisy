package com.yourong.core.common;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.bsc.manager.BscBankManager;
import com.yourong.core.bsc.model.BscBank;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.msg.manager.MessageLogManager;
import com.yourong.core.msg.manager.MessageTemplateManager;
import com.yourong.core.msg.model.MessageTemplate;
import com.yourong.core.push.PushEnum;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.manager.MemberBankCardManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberNotifySettingsManager;
import com.yourong.core.uc.model.MemberBankCard;
import com.yourong.core.uc.model.biz.MemberNotifySettingsForSendMsg;

public class MessageClient {
	private static Logger logger = LoggerFactory.getLogger(MessageClient.class);
	private static MessageTemplateManager messageTemplateManager = SpringContextHolder.getBean(MessageTemplateManager.class);
	private static MessageLogManager messageLogManager = SpringContextHolder.getBean(MessageLogManager.class);
	private static ProjectManager projectManager = SpringContextHolder.getBean(ProjectManager.class);
    private static MemberBankCardManager memberBankCardManager = SpringContextHolder.getBean(MemberBankCardManager.class);
    private static BscBankManager bscBankManager = SpringContextHolder.getBean(BscBankManager.class);
    private static MemberNotifySettingsManager memberNotifySettingsManager = SpringContextHolder.getBean(MemberNotifySettingsManager.class);
    private static MemberManager memberManager = SpringContextHolder.getBean(MemberManager.class);
    private static TransactionManager transactionManager = SpringContextHolder.getBean(TransactionManager.class);
	/**
	 * 优惠券失效
	 * @param amount 金额
	 * @param expiredDate 失效日期
	 * @param couponType  优惠券类型: 1-现金券 2-收益券
	 * @param memberId 用户编号
	 */
	public static void sendMsgForCouponExpired(BigDecimal amount, Date expiredDate, Integer couponType,  Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.COUPON_EXPIRED.getCode());
		if(msgTemplateList != null){
			String couponTypeName = (couponType == 1) ? "现金券" : "收益券";
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = Maps.newHashMap();
					if(couponTypeName.equals("现金券")){
						model.put("amount", amount.intValue()+"元");
					}else{
						model.put("amount", amount+"%");
					}
					model.put("expiredDate", DateUtils.formatDatetoString(expiredDate,"MM月dd日"));
					model.put("couponTypeName", couponTypeName);
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_SYSTEM);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 修改密码
	 * @param currentDate
	 * @param memberId
	 */
	public static void sendMsgForUpdatePassword(Date currentDate, Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.UPDATE_PASSWORD.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(currentDate,"MM月dd日HH时mm分"));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_SYSTEM);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 绑定银行卡
	 * @param currentDate
	 * @param bankName
	 * @param memberId
	 */
	public static void sendMsgForBindBandCard(String bankCode, String cardNumber, Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.BIND_BAND_CARD.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日HH时mm分"));
					model.put("bankName", getBankNameByCode(bankCode));
					model.put("bankCardCode", getBankCardCode(cardNumber));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_SYSTEM);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 利息/本金到账
	 * @param currentDate
	 * @param transactionId 交易ID
	 * @param payableInterest 利息
	 * @param payablePrincipal 本金
	 * @param memberId
	 */
	public static void sendMsgForInterestOrCapital(Date currentDate, Long transactionId, BigDecimal payableInterest, BigDecimal payablePrincipal, Long memberId){
		List<MessageTemplate> msgTemplateList = null;
		boolean isPayablePrincipal = false;//本金
		try{
			if(payablePrincipal == null || payablePrincipal.compareTo(BigDecimal.ZERO) <= 0){
				msgTemplateList = getMessageTemplateList(MessageEnum.INTEREST.getCode());
			}else{
				msgTemplateList = getMessageTemplateList(MessageEnum.CAPITAL.getCode());
				isPayablePrincipal = true;
			}
			if(msgTemplateList != null){
				Transaction transaction = transactionManager.selectTransactionById(transactionId);
				if(transaction != null && transaction.getMemberId().toString().equals(memberId.toString())){
					MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_PAY_STATUS.getType(), memberId);
					Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
					for(MessageTemplate msgt : msgTemplateList){
						if(msgTemplateIsUse(msgt)){
							Map<String, Object> model = new HashMap<String, Object>();
							model.put("currentDate", DateUtils.formatDatetoString(currentDate,"M月dd日HH时mm分"));
							model.put("payableInterest", payableInterest);
							model.put("payablePrincipal", FormulaUtil.getFormatPrice(payablePrincipal));
							model.put("projectName", getProjectURL(project.getName(), project.getId(), msgt.getMsgType()));
							msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
							sendMessage2(msgt, memberNotifySettingsForSendMsg, model, memberId);
						}
					}
					String projectName = getProjectURL(project.getName(), project.getId(), Constant.MSG_TEMPLATE_TYPE_PHONE);
					String message = "";
					if(isPayablePrincipal){
						if(transaction.getProjectCategory()==2){
							message = "您投资的转让项目"+projectName+",本金到账"+FormulaUtil.getFormatPrice(payablePrincipal)+"元,利息到账"+payableInterest+"元";
						}else{
							message = "您投资的"+projectName+",本金到账"+FormulaUtil.getFormatPrice(payablePrincipal)+"元,利息到账"+payableInterest+"元";
						}
						//app信息提醒用户本息到账
						MessageClient.sendMsgForCommon(memberId, Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_PAY_PRINCIPAL.getCode(), 
									project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName()
											,payableInterest.toString(),FormulaUtil.getFormatPrice(payablePrincipal));
					}else{
						message = "您投资的"+projectName+",利息到账"+payableInterest+"元";
						MessageClient.sendMsgForCommon(memberId, Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_PAY_INTEREST.getCode(), 
								project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName()
										,payableInterest.toString());
					}
					PushClient.pushMsgToMember(message, memberId, transactionId.toString(), PushEnum.TRANSACTION_INTEREST);
				}
			}
		}catch(Exception ex){
			logger.error("利息/本金到账发送消息异常", ex);
		}
	}
	
	/**
	 * 新浪存钱罐收益
	 * @param currentDate
	 * @param bizType
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForSinaWalletIncome(Date currentDate, BigDecimal amount, Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.SINA_WALLET_INCOME.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(currentDate,"MM月dd日"));
					model.put("amount", amount);
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 签到
	 * @param currentDate
	 * @param popularityNumber
	 * @param memberId
	 */
	public static void sendMsgForSignIn(Date currentDate, int popularityNumber, Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.SIGN_IN.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(currentDate,"MM月dd日HH时mm分"));
					model.put("popularityNumber", popularityNumber);
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 开通新浪存钱罐
	 * @param currentDate
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForRegSina(Date currentDate, BigDecimal amount, Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.REG_SINA.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(currentDate,"MM月dd日HH时mm分"));
					model.put("amount", amount);
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 绑定邮箱
	 * @param currentDate
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForBindEmail(Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.BIND_EMAIL.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日HH时mm分"));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 完善资料
	 * @param currentDate
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForPerfectInformation(Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.PERFECT_INFORMATION.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日HH时mm分"));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 人气值兑换
	 * @param currentDate
	 * @param popularityNumber
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForExchangePopularity(Date currentDate, int popularityNumber, BigDecimal amount, Long memberId, int couponType){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.EXCHANGE_POPULARITY.getCode());
		if(msgTemplateList != null){
			String couponTypeName = (couponType == 1) ? "现金券" : "收益券";
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(currentDate,"MM月dd日HH时mm分"));
					if(couponTypeName.equals("现金券")){
						model.put("amount", amount.intValue()+"元");
					}else{
						model.put("amount", amount+"%");
					}
					model.put("popularityNumber", popularityNumber);
					model.put("couponTypeName", couponTypeName);
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 投资成功
	 * @param currentDate
	 * @param projectId
	 * @param memberId
	 */
	public static void sendMsgForInvestmentSuccess(Date currentDate, Long projectId, Long memberId, int totalDays, BigDecimal totalInterest, BigDecimal amount){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.INVESTMENT_SUCCESS.getCode());
		if(msgTemplateList != null){
			try {
				Project project = projectManager.selectByPrimaryKey(projectId);
				for(MessageTemplate msgt : msgTemplateList){
					if(msgTemplateIsUse(msgt)){
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("currentDate", DateUtils.formatDatetoString(currentDate,"MM月dd日HH时mm分"));
						model.put("projectName", getProjectURL(project.getName(), project.getId(), msgt.getMsgType()));
						model.put("totalDays", totalDays);
						model.put("amount", FormulaUtil.getFormatPrice(amount));
						model.put("totalInterest", totalInterest);
						msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_OPERATE);
						sendMessage(msgt, model, memberId);
					}
				}
			} catch (ManagerException e) {
				logger.error("投资成功发送消息异常", e);
			}
		}
	}
	
	/**
	 * 提现成功
	 * @param amount
	 * @param memberBankCardId
	 * @param currentDate
	 * @param memberId
	 */
	public static void sendMsgForWithdrawalsSuccess(BigDecimal amount, Long memberBankCardId, Date currentDate, Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.WITHDRAWALS_SUCCESS.getCode());
		if(msgTemplateList != null){
			try {
				MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_WITHDRAW_STATUS.getType(), memberId);
				for(MessageTemplate msgt : msgTemplateList){
					if(msgTemplateIsUse(msgt)){
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("currentDate", DateUtils.formatDatetoString(currentDate,"M月d日HH时mm分"));
						model.put("amount", amount);
						model.put("bankName", getBankNameByMemberBankCardId(memberBankCardId));
						msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_OPERATE);
						sendMessage2(msgt, memberNotifySettingsForSendMsg, model, memberId);
					}
				}
			} catch (Exception e) {
				logger.error("提现成功发送消息异常", e);
			}
		}
	}
	
	/**
	 * 提现失败
	 * @param amount
	 * @param memberBankCardId
	 * @param currentDate
	 * @param memberId
	 */
	public static void sendMsgForWithdrawalsError(BigDecimal amount, Long memberBankCardId, Date currentDate, Long memberId, String remarks){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.WITHDRAWALS_ERROR.getCode());
		if(msgTemplateList != null){
			try{
				MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_WITHDRAW_STATUS.getType(), memberId);
				for(MessageTemplate msgt : msgTemplateList){
					if(msgTemplateIsUse(msgt)){
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("currentDate", DateUtils.formatDatetoString(currentDate,"M月d日HH时mm分"));
						model.put("amount", amount);
						model.put("bankName", getBankNameByMemberBankCardId(memberBankCardId));
						model.put("remarks", remarks);
						msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_OPERATE);
						sendMessage2(msgt, memberNotifySettingsForSendMsg, model, memberId);
					}
				}
			}catch(Exception ex){
				logger.error("提现失败发送消息异常", ex);
			}
		}
	}
	
	/**
	 * 项目活动
	 * @param projectId
	 * @param activityName
	 * @param popularityNumber
	 * @param memberId
	 */
	public static void sendMsgForProjectActivities(Long projectId, String activityName, String popularityNumber, Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.PROJECT_ACTIVITIES.getCode());
		if(msgTemplateList != null){
			try{
				Project project = projectManager.selectByPrimaryKey(projectId);
				for(MessageTemplate msgt : msgTemplateList){
					if(msgTemplateIsUse(msgt)){
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("popularityNumber", popularityNumber);
						model.put("activityName", activityName);
						model.put("projectName", getProjectURL(project.getName(), project.getId(), msgt.getMsgType()));
						msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
						sendMessage(msgt, model, memberId);
					}
				}
			}catch(Exception ex){
				logger.error("项目活动发送消息异常", ex);
			}
		}
	}
	
	/**
	 * 投资失败
	 * @param projectId
	 * @param memberId
	 */
	public static void sendMsgForInvestmentFail(Long projectId, Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.INVESTMENT_FAIL.getCode());
		if(msgTemplateList != null){
			try{
				Project project = projectManager.selectByPrimaryKey(projectId);
				for(MessageTemplate msgt : msgTemplateList){
					if(msgTemplateIsUse(msgt)){
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日HH时mm分"));
						model.put("projectName", getProjectURL(project.getName(), project.getId(), msgt.getMsgType()));
						msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_OPERATE);
						sendMessage(msgt, model, memberId);
					}
				}
			}catch(Exception ex){
				logger.error("投资失败发送消息异常", ex);
			}
		}
	}
	
	/**
	 * 问题调查
	 * @param projectId
	 * @param memberId
	 */
	public static void sendMsgForQuestionnaire(Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.QUESTIONNAIRE.getCode());
		if(msgTemplateList != null){
			try{
				for(MessageTemplate msgt : msgTemplateList){
					if(msgTemplateIsUse(msgt)){
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日HH时mm分"));
						msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
						sendMessage(msgt, model, memberId);
					}
				}
			}catch(Exception ex){
				logger.error("问题调查发送消息异常", ex);
			}
		}
	}
	
	/**
	 * 项目预告发送
	 * @param project
	 * @param memberId
	 */
	public static void sendMsgForProjectNotice(Project project){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.PROJECT_NOTICE.getCode());
		if(msgTemplateList != null){
			try{
				long maxMember = memberManager.getMaxMemberId();
				int days = DateUtils.getIntervalDays(project.getOnlineTime(), project.getEndDate())+1-project.getInterestFrom();
				for (long memberId = Long.parseLong(Config.firstMemberId); memberId <= maxMember; memberId++) {
					MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_PROJECT_NOTICE.getType(), memberId);
					for(MessageTemplate msgt : msgTemplateList){
						if(msgTemplateIsUse(msgt)){
							Map<String, Object> model = new HashMap<String, Object>();
							model.put("onlineTime", DateUtils.formatDatetoString(project.getOnlineTime(),"M月d日HH时mm分"));
							model.put("projectName", getProjectURL(project.getName(), project.getId(), msgt.getMsgType()));
							model.put("totalAmount", FormulaUtil.getFormatPrice(project.getTotalAmount().divide(new BigDecimal(10000)))+"万");
							if(!project.getMinAnnualizedRate().toPlainString().equals(project.getMaxAnnualizedRate().toPlainString())){
								model.put("minRate", project.getMinAnnualizedRate().toPlainString()+"%-");//加一个横杆
								model.put("maxRate", project.getMaxAnnualizedRate().toPlainString()+"%");
							}else{
								model.put("maxRate", project.getMaxAnnualizedRate().toPlainString()+"%");
							}
							model.put("day", days);
							msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_SYSTEM);
							if(memberNotifySettingsForSendMsg != null){
								memberNotifySettingsForSendMsg.setMarketingSMS(true);
							}
							sendMessage2(msgt, memberNotifySettingsForSendMsg, model, memberId);
						}
					}
				}
			}catch(Exception ex){
				logger.error("项目预告发送消息异常", ex);
			}
		}
	}
	
	/**
	 * 提现被拒绝
	 * @param amount
	 * @param memberId
	 * @param remarks
	 */
	public static void sendMsgForWithdrawRefuse(BigDecimal amount, Long memberId, String remarks){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.WITHDRAWALS_REFUSE.getCode());
		if(msgTemplateList != null){
			try{
				MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = memberNotifySettingsManager.getNotifySendType(TypeEnum.SEND_MSG_NOTIFY_TYPE_WITHDRAW_STATUS.getType(), memberId);
				for(MessageTemplate msgt : msgTemplateList){
					if(msgTemplateIsUse(msgt)){
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"M月d日HH时mm分"));
						model.put("amount", amount);
						model.put("remarks", remarks);
						msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_OPERATE);
						sendMessage2(msgt, memberNotifySettingsForSendMsg, model, memberId);
					}
				}
			}catch(Exception ex){
				logger.error("提现被拒绝发送消息异常", ex);
			}
		}
	}
	
	/**
	 * 绑定微信
	 * @param memberId
	 */
	public static void sendMsgForBindWeixin(Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.BIND_WEIXIN.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"M月d日HH时mm分"));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 绑定微信 临时处理方案
	 * @param memberId
	 */
	public static void sendMsgForBindWeixinTemp(Long memberId){
		MessageTemplate msgt = new MessageTemplate();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"M月d日HH时mm分"));
		msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
		msgt.setMsgType(Constant.MSG_TEMPLATE_TYPE_STAND);
		msgt.setId(40L);
		msgt.setContent("<p>您于${currentDate}，成功参加“关注并绑定微信服务号”活动，获得5点人气值奖励。<a href=\"http://www.yrw.com/coupon/reputation\" target=\"_blank\">点此查看我的人气值</a></p>");
		sendMessage(msgt, model, memberId);
	}
	
	/**
	 * 非常规活动
	 * @param memberId
	 * @param projectId
	 * @param investent
	 * @param activityName
	 * @param value
	 */
	public static void sendMsgForApplicationActivity(Long memberId, Long projectId, BigDecimal investent, String activityName, BigDecimal value){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.APPLICATION.getCode());
		if(msgTemplateList != null){
			try{
				Project project = projectManager.selectByPrimaryKey(projectId);
				for(MessageTemplate msgt : msgTemplateList){
					if(msgTemplateIsUse(msgt)){
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"M月d日HH时"));
						model.put("amount", FormulaUtil.getFormatPrice(investent));
						model.put("projectName", getProjectURL(project.getName(), project.getId(), msgt.getMsgType()));
						model.put("activityName", activityName);
						model.put("value", value);
						msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
						sendMessage(msgt, model, memberId);
					}
				}
			}catch(Exception ex){
				logger.error("非常规活动发送消息异常", ex);
			}
		}
	}
	
	/**
	 * 人工派发
	 * @param memberId
	 * @param value
	 * @param remarks
	 */
	public static void sendMsgForGivePopularity(Long memberId, BigDecimal value, String remarks){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.GIVE_POPULARITY.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"M月d日HH时"));
					model.put("value", value);
					model.put("remarks", remarks);
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 开通快捷支付
	 * @param memberId
	 * @param cardNumber
	 * @param bankCode
	 */
	public static void sendMsgForOpenQuickPayment(Long memberId, String cardNumber, String bankCode){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.QUICK_PAYMENT.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"M月d日HH时"));
					model.put("bankName", getBankNameByCode(bankCode));
					model.put("bankCardCode", getBankCardCode(cardNumber));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_OPERATE);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	/**
	 * 升级为安全卡
	 * @param memberId
	 * @param bankCardId
	 */
	public static void sendMsgForSafeBankCard(Long memberId, Long bankCardId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.SAFE_BANK_CARD.getCode());
		if(msgTemplateList != null){
			MemberBankCard bankCard = getMemberBankCard(bankCardId);
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"M月d日HH时"));
					model.put("bankName", getBankNameByCode(bankCard.getBankCode()));
					model.put("bankCardCode", getBankCardCode(bankCard.getCardNumber()));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_OPERATE);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 删除银行卡
	 * @param memberId
	 * @param cardNumber
	 * @param bankCode
	 */
	public static void sendMsgForDeleteBankCard(Long memberId, String cardNumber, String bankCode){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.DELETE_BANK_CARD.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"M月d日HH时"));
					model.put("bankName", getBankNameByCode(bankCode));
					model.put("bankCardCode", getBankCardCode(cardNumber));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_OPERATE);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 租赁分红
	 * @param projectId
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForLeaseBonus(Long projectId, BigDecimal amount,Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.LEASE_BONUS.getCode());
		if(msgTemplateList != null){
			try{
				Project project = projectManager.selectByPrimaryKey(projectId);
				for(MessageTemplate msgt : msgTemplateList){
					if(msgTemplateIsUse(msgt)){
						Map<String, Object> model = new HashMap<String, Object>();
						model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"M月d日HH时"));
						model.put("projectName", getProjectURL(project.getName(), project.getId(), msgt.getMsgType()));
						model.put("amount", amount);
						msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_INCOME);
						sendMessage(msgt, model, memberId);
					}
				}
			}catch(Exception ex){
				logger.error("租赁分红发送消息异常", ex);
			}
		}
	}
	
	/**
	 * 充值成功
	 * @param memberId
	 * @param amount
	 */
	public static void sendMsgForRechargeSuccess(Long memberId, BigDecimal amount){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.RECHARGE_SUCCESS.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日"));
					model.put("amount", FormulaUtil.formatCurrencyNoUnit(amount));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_OPERATE);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	

	/**
	 * 红色星期五
	 * @param memberId
	 */
	public static void sendMsgForRedFriday(Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.RED_FRIDAY.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日HH时mm分"));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_OPERATE);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	

	/**
	 * 亿路上 有你抽奖
	 * @param memberId
	 * @param amount
	 */
	public static void sendMsgForYiRoad(Long memberId, String rewardName, String activityName){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.YIROAD_LOTTERY.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日"));
					model.put("rewardName", rewardName);
					model.put("activityName", activityName);
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_ACTIVITY);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 亿路上有你 首当直充
	 * @param memberId
	 * @param amount
	 */
	public static void sendMsgForAppFirstRecharge(Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.APP_FIRST_RECHARGE.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日"));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_ACTIVITY);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 生日消息推送
	 * @param memberId
	 * @param trueName
	 */
	public static void sendMsgForBirthday(Long memberId, String trueName){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.BIRTHDAY.getCode());
		String lastName = trueName.substring(0, 1);
		if(msgTemplateList != null){
			MemberNotifySettingsForSendMsg notify = new MemberNotifySettingsForSendMsg();
			notify.setNeedSendSMSNotice(true);
			notify.setNeedSendEmailNotice(true);
			notify.setMemberId(memberId);
			notify.setMarketingSMS(true);
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("lastName", lastName);
					model.put("name", trueName);
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_SYSTEM);
					sendMessage2(msgt, notify, model, memberId);
				}
			}
		}
		//生日推送，取消 bychanpin yang
		//PushClient.pushMsgToMember("生日快乐！快去生日专题页享用您的生日特权吧！", memberId, "", PushEnum.BIRTHDAY);
	}

	/**
	 * 中秋活动
	 * @param memberId
	 */
	public static void sendMsgForMidAutumn(Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.MID_AUTUMN.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_ACTIVITY);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 关注微信
	 * @param currentDate
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForFollowWechat(Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.FOLLOW_WECHAT.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日HH时mm分"));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_ACTIVITY);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 投资项目
	 * @param currentDate
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForInvestProject(Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.INVEST_PROJECT.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日HH时mm分"));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_ACTIVITY);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 体验APP
	 * @param currentDate
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForUserApp(Long memberId){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.USE_APP.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日HH时mm分"));
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_ACTIVITY);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	/**
	 * 根据手机号直接发送短息
	 * @param project
	 * @param memberId
	 */
	public static void sendShortMessageByMobile(Long mobile, String content) {
		try{
			MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = new MemberNotifySettingsForSendMsg();
			memberNotifySettingsForSendMsg.setNeedSendSMSNotice(true);
			memberNotifySettingsForSendMsg.setSendForward(mobile.toString());
			memberNotifySettingsForSendMsg.setMarketingSMS(true);
			memberNotifySettingsForSendMsg.setMsgType(Constant.MSG_TEMPLATE_TYPE_PHONE);
			memberNotifySettingsForSendMsg.setSendType(Constant.MSG_SEND_BY_FORWARDOBJECT);
			pushMessageToRedis(memberNotifySettingsForSendMsg, content);
		}catch(Exception ex){
			logger.error("全局用户发送短信异常", ex);
		}
	}
	
	/**
	 * 后台发送短信
	 * @param project
	 * @param memberId
	 */
	public static void sendShortMessageNotice(String content) {
		try{
			long maxMember = memberManager.getMaxMemberId();
 			for (long memberId = Long.parseLong(Config.firstMemberId); memberId <= maxMember; memberId++) {
				MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = new MemberNotifySettingsForSendMsg();
				memberNotifySettingsForSendMsg.setNeedSendSMSNotice(true);
				memberNotifySettingsForSendMsg.setMemberId(memberId);
				memberNotifySettingsForSendMsg.setMarketingSMS(true);
				memberNotifySettingsForSendMsg.setMsgType(Constant.MSG_TEMPLATE_TYPE_PHONE);
				pushMessageToRedis(memberNotifySettingsForSendMsg, content);
				
			}
		}catch(Exception ex){
			logger.error("全局用户发送短信异常", ex);
		}
	}
	
	/**
	 * 根据memberID后台发送短信
	 * 
	 * @param project
	 * @param memberId
	 */
	public static void sendShortMessageNoticeByMember(List<Long> memberList,
			String content) {
		try {
			for (long memberId : memberList) {
				MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg = new MemberNotifySettingsForSendMsg();
				memberNotifySettingsForSendMsg.setNeedSendSMSNotice(true);
				memberNotifySettingsForSendMsg.setMemberId(memberId);
				memberNotifySettingsForSendMsg.setMarketingSMS(true);
				memberNotifySettingsForSendMsg
						.setMsgType(Constant.MSG_TEMPLATE_TYPE_PHONE);
				pushMessageToRedis(memberNotifySettingsForSendMsg, content);

			}
		} catch (Exception ex) {
			logger.error("全局用户发送短信异常", ex);
		}
	}

	
	/**
	 * 活动引擎通用站内信
	 * @param currentDate
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForSPEngin(Long memberId, String activityName, String rewardName){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.SPEGINE_COMMON.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"MM月dd日HH时mm分"));
					model.put("activityName", activityName);
					model.put("rewardName", rewardName);
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_ACTIVITY);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}


	/**
	 * 
	 * @Description:发送消息通用接口
	 * @param memberId
	 * @param notifyType
	 *            通知类型：系统、操作、收益、活动 取常量如：Constant.MSG_NOTIFY_TYPE_SYSTEM
	 * @param messageEnumCode
	 *            取枚举类里对应模板的code 如MessageEnum.COUPON_EXPIRED.getCode()
	 * @param parameters
	 *            需要动态显示的参数，用户替换到模板的占位符里
	 * @author: wangyanji
	 * @time:2016年2月22日 下午4:24:48
	 */
	public static boolean sendMsgForCommon(Long memberId, int notifyType, String messageEnumCode, String... parameters) {
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(messageEnumCode);
		if (Collections3.isEmpty(msgTemplateList)) {
			return false;
		}
		// 根据模板消息类型发送消息
		MemberNotifySettingsForSendMsg settingsForSendMsg = new MemberNotifySettingsForSendMsg();
		for (MessageTemplate template : msgTemplateList) {
			if (template.getMsgType().equals(Constant.MSG_TEMPLATE_TYPE_STAND)) {
				settingsForSendMsg.setNeedSendSystemNotice(true);
			} else if (template.getMsgType().equals(Constant.MSG_TEMPLATE_TYPE_PHONE)) {
				settingsForSendMsg.setNeedSendSMSNotice(true);
			} else if (template.getMsgType().equals(Constant.MSG_TEMPLATE_TYPE_EMAIL)) {
				settingsForSendMsg.setNeedSendEmailNotice(true);
			} else if (template.getMsgType().equals(Constant.MSG_TEMPLATE_TYPE_BAIDU)) {
				settingsForSendMsg.setNeedBaiduPushNotice(true);
			}else if (template.getMsgType().equals(Constant.MSG_TEMPLATE_TYPE_APP)) {
				settingsForSendMsg.setNeedSendAPPNotice(true);
			}
			settingsForSendMsg.setMemberId(memberId);
			settingsForSendMsg.setNotifyType(notifyType);
			template.setNotifyType(notifyType);
			try {
				sendMessageForCommon(template, settingsForSendMsg, memberId, parameters);
			} catch (Exception e) {
				logger.error("发送站内信失败, messageEnumCode={}, msgType={}", messageEnumCode, template.getMsgType(), e);
			}
		}
		return true;
	}

	/**
	 * 活动引擎通用站内信(自定义时间)
	 * @param currentDate
	 * @param amount
	 * @param memberId
	 */
	public static void sendMsgForSPEnginByReceiveTime(Long memberId, String activityName, String rewardName, Date receiveTime){
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(MessageEnum.SPEGINE_COMMON.getCode());
		if(msgTemplateList != null){
			for(MessageTemplate msgt : msgTemplateList){
				if(msgTemplateIsUse(msgt)){
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("currentDate", DateUtils.formatDatetoString(receiveTime,"MM月dd日HH时mm分"));
					model.put("activityName", activityName);
					model.put("rewardName", rewardName);
					msgt.setNotifyType(Constant.MSG_NOTIFY_TYPE_ACTIVITY);
					sendMessage(msgt, model, memberId);
				}
			}
		}
	}
	
	
	private static void sendMessage(MessageTemplate template, Map<String, Object> model, Long memberId){
		if(template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_PHONE){
			//TODO
		}else if(template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_EMAIL){
			//TODO
		}else if(template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_STAND){
			String message = formatMessage(model, template.getContent());
			if(message != null){
				try {
					messageLogManager.insertFromMessageTemplate(message, memberId, template.getId(), template.getNotifyType(),template.getMsgType()
							,template.getServiceType());
				} catch (ManagerException e) {
					logger.error("站内发送消息异常", e);
				}
			}
		}
	}
	
	private static void sendMessage2(MessageTemplate template, MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg, Map<String, Object> model, Long memberId){
		String message = formatMessage(model, template.getContent());
		if(template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_PHONE){
			if(memberNotifySettingsForSendMsg != null){
				if(memberNotifySettingsForSendMsg.isNeedSendSMSNotice()){
					memberNotifySettingsForSendMsg.setMsgType(Constant.MSG_TEMPLATE_TYPE_PHONE);
					pushMessageToRedis(memberNotifySettingsForSendMsg, message);
				}
			}
		}else if(template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_EMAIL){
			if(memberNotifySettingsForSendMsg != null){
				if(memberNotifySettingsForSendMsg.isNeedSendEmailNotice()){
					memberNotifySettingsForSendMsg.setMsgType(Constant.MSG_TEMPLATE_TYPE_EMAIL);
					pushMessageToRedis(memberNotifySettingsForSendMsg, message);
				}
			}
		}else if(template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_STAND){
			if(message != null){
				try {
					messageLogManager.insertFromMessageTemplate(message, memberId, template.getId(), template.getNotifyType(),template.getMsgType()
							,template.getServiceType());
				} catch (ManagerException e) {
					logger.error("站内发送消息异常", e);
				}
			}
		}
	}
	
	private static void sendMessageForCommon(MessageTemplate template, MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg,
			Long memberId, String... parameters) throws Exception {
		String message = generateMessage(template.getContent(), parameters);
		if (template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_PHONE) {
			if (memberNotifySettingsForSendMsg.isNeedSendSMSNotice()) {
				memberNotifySettingsForSendMsg.setMsgType(Constant.MSG_TEMPLATE_TYPE_PHONE);
				pushMessageToRedis(memberNotifySettingsForSendMsg, message);
			}
		} else if (template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_EMAIL) {
			if (memberNotifySettingsForSendMsg.isNeedSendEmailNotice()) {
				memberNotifySettingsForSendMsg.setMsgType(Constant.MSG_TEMPLATE_TYPE_EMAIL);
				pushMessageToRedis(memberNotifySettingsForSendMsg, message);
			}
		} else if (template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_STAND) {
			messageLogManager.insertFromMessageTemplate(message, memberId, template.getId(), template.getNotifyType(),template.getMsgType()
					,template.getServiceType());
		} else if (template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_BAIDU) {
			PushEnum pushEnum = PushEnum.getEnumByIndex(Integer.valueOf(template.getTemplateCode()));
			PushClient.pushMsgToMember(message, memberId, "", pushEnum);
		}else if(template.getMsgType() == Constant.MSG_TEMPLATE_TYPE_APP){
			messageLogManager.insertFromMessageTemplate(message, memberId, template.getId(), template.getNotifyType(),template.getMsgType()
					,template.getServiceType());
		}
	}
	
	private static List<MessageTemplate> getMessageTemplateList(String templateCode){
		try {
			return messageTemplateManager.getMessageTemplateByCode(templateCode);
		} catch (ManagerException e) {
			logger.error("获得消息模板异常", e);
		}
		return null;
	}
	
	private static Boolean msgTemplateIsUse(MessageTemplate msgTemplate) {
		if (null == msgTemplate.getStatus()) {
			return false;
		}else if (msgTemplate.getStatus() == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获得银行卡
	 * @param memberBankCardId
	 * @return
	 */
	private static String getBankNameByMemberBankCardId(Long memberBankCardId){
		try{
			MemberBankCard bankCard = memberBankCardManager.selectByPrimaryKey(memberBankCardId);
			if(bankCard != null){
				BscBank bank = bscBankManager.getBscBankByCode(bankCard.getBankCode());
				if(bank != null){
					return bank.getFullName();
				}
			}
		}catch(Exception ex){
			logger.error("获得银行卡异常", ex);
		}
		return "";
	}
	
	/**
	 * 获得银行卡
	 * @param memberBankCardId
	 * @return
	 */
	private static MemberBankCard getMemberBankCard(Long memberBankCardId){
		try{
			MemberBankCard bankCard = memberBankCardManager.selectByPrimaryKey(memberBankCardId);
			return bankCard;
		}catch(Exception ex){
			logger.error("获得银行卡异常", ex);
		}
		return null;
	}
	
	/**
	 * 获得银行卡名称
	 * @param bankCode
	 * @return
	 */
	private static String getBankNameByCode(String bankCode){
		try{
			BscBank bank = bscBankManager.getBscBankByCode(bankCode);
			if(bank != null){
				return bank.getFullName();
			}
		}catch(Exception ex){
			logger.error("获得银行卡名称异常", ex);
		}
		return "";
	}
	
	public static String getSuffixProjectName(String name) {
		if (name.contains("期")) {
			return name.substring(0, name.indexOf("期") + 1);
		} else {
			return name;
		}
	}
	
	private static String getProjectURL(String name, Long projectId, int templateStatus){
		if(templateStatus == Constant.MSG_TEMPLATE_TYPE_PHONE)
			return getSuffixProjectName(name);
		return "<a href='"+PropertiesUtil.getWebRootUrl()+"/products/detail-"+projectId+".html' target='_blank'>"+getSuffixProjectName(name)+"</a>";
	}
	
	private static String getBankCardCode(String code){
		if(code != null){
			return code.substring(code.length()-4, code.length());
		}
		return "";
	}
	
	public static void pushMessageToRedis(MemberNotifySettingsForSendMsg memberNotifySettingsForSendMsg, String message){
		memberNotifySettingsForSendMsg.setMsgTemplate(true);//为了向前兼容，暂时通过此参数设置
		memberNotifySettingsForSendMsg.setMessage(message);
		RedisManager.rpush(RedisConstant.REDIS_KEY_SENDMESSAGE, JSONObject.toJSONString(memberNotifySettingsForSendMsg));
	}
	
	
	private static String formatMessage(Map<String,Object> model, String content){
		try{
			Velocity velocityEngine = new Velocity();
			StringWriter writer = new StringWriter();
			VelocityContext context = new VelocityContext(model);
			velocityEngine.evaluate(context, writer, "", content);
			String strNoEL = writer.toString().replaceAll("\\$\\{[a-zA-Z\\d\\.]*\\}", "");
			strNoEL = strNoEL.replaceAll("\\$[a-zA-Z\\d\\.]*\\(\\)", "");
			return strNoEL;
		}catch(Exception ex){
			logger.error("格式化消息异常", ex);
		}
		return null;
	}
	
	private static String generateMessage(String templateMessage, String... parameters) {
		return MessageFormat.format(templateMessage, parameters);
	}
	
	/**
	 * 临时封装，手机号发送短信
	 * @param 
	 * @return
	 */
	public static void sendMsgForCommonBymobile(List<Long> mobileList, int notifyType, String messageEnumCode, String... parameters) {
		List<MessageTemplate> msgTemplateList = getMessageTemplateList(messageEnumCode);
		if (Collections3.isEmpty(msgTemplateList)) {
			return ;
		}
		for (MessageTemplate template : msgTemplateList) {
			String message = generateMessage(template.getContent(), parameters);
			// 根据模板消息类型发送消息
			for(Long mobile :mobileList){
				sendShortMessageByMobile(mobile,message);
			}
		}
	}

}
