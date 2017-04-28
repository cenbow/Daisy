package com.yourong.core.uc.manager.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.mail.SendMailService;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.dao.MemberNotifySettingsMapper;
import com.yourong.core.uc.manager.MemberNotifySettingsManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberNotifySettings;
import com.yourong.core.uc.model.biz.MemberNotifySettingsForSendMsg;

@Component
public class MemberNotifySettingsManagerImpl implements
		MemberNotifySettingsManager {
    private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private MemberNotifySettingsMapper memberNotifySettingsMapper;
	
	@Autowired
	private SysDictManager sysDictManager;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private SmsMobileSend smsMobileSend;

    @Autowired
    private MemberMapper  memberMapper;


	@Override
	public int updateNotifySettingsStatus(MemberNotifySettings record) throws ManagerException {
		try{
			return memberNotifySettingsMapper.updateNotifySettingsStatus(record);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public int initMemberNotifySettings(Long memberId) throws ManagerException {
		List<SysDict> notifyTypeDictList = sysDictManager.findByGroupName("notify_type");
		List<SysDict> notifyWayDictList = sysDictManager.findByGroupName("notify_way");
		List<MemberNotifySettings> notifySettingsList = Lists.newArrayList();
		Timestamp timestamp = DateUtils.getCurrentDateTime();
		for(SysDict notifyType : notifyTypeDictList){
			Integer notifyTypeValue = Integer.parseInt(notifyType.getValue());
			for(SysDict notifyWay : notifyWayDictList) {
				MemberNotifySettings mns = new MemberNotifySettings();
				Integer notifyWayValue = Integer.parseInt(notifyWay.getValue());
				mns.setMemberId(memberId);
				mns.setNotifyType(notifyTypeValue);
				mns.setNotifyWay(notifyWayValue);
				mns.setStatus(getStatus(notifyTypeValue,notifyWayValue));
				mns.setCreateTime(timestamp);
				mns.setUpdateTime(timestamp);
				notifySettingsList.add(mns);
			}
		}
		if(notifySettingsList.size() < 1){
			return 0;
		}
		return memberNotifySettingsMapper.batchInsertNotifySettings(notifySettingsList);
	}

	@Override
	public int batchUpdateNotifySettingsStatus(List<MemberNotifySettings> list) throws ManagerException {
		try{
			return memberNotifySettingsMapper.batchInsertNotifySettings(list);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public int deleteMemberNotifySettingsByMemberId(Long memberId) throws ManagerException {
		try{
			return memberNotifySettingsMapper.deleteMemberNotifySettingsByMemberId(memberId);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}


    public void sendMessageByNotifyType(MemberNotifySettingsForSendMsg sendType)throws Exception {
		
		Member member = this.memberMapper.selectByPrimaryKey(sendType.getMemberId());
		if (member == null) {
			logger.error("通知会员接口异常 memberID =" + sendType.getMemberId());
			return;
		}
		//该账号已经冻结，不发送任何消息
		if(member.getStatus() == 0){
			return;
		}
		//账号未激活，不发送任何消息
		if(member.getStatus() == 1){
			return;
		}
		Long mobile = member.getMobile();
		String email = member.getEmail();
		
		if(sendType.getMsgType() == Constant.MSG_TEMPLATE_TYPE_PHONE){
			if(PropertiesUtil.isDev()){
				if(sendType.isMarketingSMS()){
					logger.debug("营销类发送短信提醒  mobile:"+mobile+" message:"+sendType.getMessage());
				}else{
					logger.debug("发送短信提醒  mobile:"+mobile+" message:"+sendType.getMessage());
				}
			}else{					
				if(sendType.isMarketingSMS()){
					smsMobileSend.sendMarketingSMS(mobile,sendType.getMessage());
				}else{
					String smsUrl = PropertiesUtil.getProperties("smsurl.common");
					smsMobileSend.sendSMS(smsUrl, mobile,sendType.getMessage());
				}
			}
		}else if(sendType.getMsgType() == Constant.MSG_TEMPLATE_TYPE_EMAIL){
			if (StringUtil.isNotBlank(email) && isSendMailBySysDict()) {
				String subject = "有融网邮件通知";
				String vmFile = "mailHtml";
				String key = CryptHelper.encryptByase(member.getShortUrl());
				String emailUnsubscribeCode = RedisMemberClient.getEmailUnsubscribe(key);
				if(StringUtil.isBlank(emailUnsubscribeCode)){
					RedisMemberClient.setEmailUnsubscribe(member.getId(), key);
				}
				Map<String, Object> context = Maps.newHashMap();
				context.put("context", sendType.getMessage());
				context.put("emailUnsubscribeCode", key);
				sendMailService.sendMailNow(email, subject, vmFile, context);
			}
		}else if(sendType.getMsgType() == Constant.MSG_TEMPLATE_TYPE_STAND){
			
		}
//		// 系统通知
//		if (sendType.isNeedSendSystemNotice()) {
//
//		}
//		// 短信通知
//		if (sendType.isNeedSendSMSNotice()) {			
////			Date date = DateUtils.getCurrentDate();
////			//TODO, 以后优化
////			if(date.getHours() >= Constant.NIGHT_TIME || date.getHours() < Constant.MORNING_TIME){
////				Date sendTime = new Date();
////				if(date.getHours() >= Constant.NIGHT_TIME){
////					sendTime = DateUtils.addDate(sendTime, 1);
////					sendTime.setHours(Constant.SEND_SMS_TIME);
////				}else if(date.getHours()< Constant.MORNING_TIME){
////					sendTime.setHours(Constant.SEND_SMS_TIME);
////				}
////				if(PropertiesUtil.isDev()){
////					String message = String.format("发送定时短信提醒  mobile=%s,message=%s,time=%s", mobile,sendType.getMessage()
////							,DateUtils.formatDate(sendTime, DateUtils.TIME_PATTERN_SHORT_2));
////					logger.debug(message);
////				}else{					
////					smsMobileSend.sendTimeSMS(mobile, sendType.getMessage(), sendTime);
////				}
////				
////							
////			}else{
//				if(PropertiesUtil.isDev()){
//					logger.debug("发送短信提醒  mobile:"+mobile+" message:"+sendType.getMessage());
//				}else{					
//				  smsMobileSend.sendMarketingSMS(mobile,sendType.getMessage());
//				}
//				
//			//}
//			
//					
//			
//		}
//		// 邮件通知
//		if (sendType.isNeedSendEmailNotice() && StringUtil.isNotBlank(email) && isSendMailBySysDict()) {
//			String subject = "有融网邮件通知";
//			String vmFile = "mailHtml";
//			String key = CryptHelper.encryptByase(member.getShortUrl());
//			String emailUnsubscribeCode = RedisMemberClient.getEmailUnsubscribe(key);
//			if(StringUtil.isBlank(emailUnsubscribeCode)){
//				RedisMemberClient.setEmailUnsubscribe(member.getId(), key);
//			}
//			Map<String, Object> context = Maps.newHashMap();
//			context.put("context", sendType.getMessage());
//			context.put("emailUnsubscribeCode", key);
//			sendMailService.sendMailNow(email, subject, vmFile, context);
//		}
    }

	/**
	 * 是否发送邮件， 根据数据字典来配置
	 * @return
	 */
	private boolean  isSendMailBySysDict(){
		SysDict sysDict = null;
		boolean result = false;
		try {
			sysDict = this.sysDictManager.findByGroupNameAndKey("send_mail_notice", "is_send");
			if (sysDict!=null ){
				result = StringUtil.equalsIgnoreCases(sysDict.getValue(),"Y",true);
			}
		} catch (ManagerException e) {
			logger.debug("获取数据字典异常",e);
		}
		return  result;
	}



	@Override
	public List<MemberNotifySettings> getUncheckedNotifySettings(Long memberId)
			throws Exception {
		try{
			return memberNotifySettingsMapper.getUncheckedNotifySettings(memberId);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public MemberNotifySettingsForSendMsg getNotifySendType(int notifyType,
			Long memberId) throws Exception {
		try{
			
			List<MemberNotifySettings> notifySettings = memberNotifySettingsMapper.selectByMemberIDyAndNotifyType(memberId, notifyType);
			if(Collections3.isNotEmpty(notifySettings)) {
				MemberNotifySettingsForSendMsg settingsForSendMsg = new MemberNotifySettingsForSendMsg();
				for (MemberNotifySettings memberNotifySettings : notifySettings) {
					if(memberNotifySettings.getNotifyWay()==1) {
						settingsForSendMsg.setNeedSendSystemNotice(true);
					}
					if(memberNotifySettings.getNotifyWay()==2) {
						settingsForSendMsg.setNeedSendSMSNotice(true);
					}
					if(memberNotifySettings.getNotifyWay()==3) {
						settingsForSendMsg.setNeedSendEmailNotice(true);
					}
				}
				settingsForSendMsg.setMemberId(memberId);
				settingsForSendMsg.setNotifyType(notifyType);
				return settingsForSendMsg;
			}
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
		return null;
	}

	/**
	 * 获得销售订单配置状态
	 * @param notifyType
	 * @param notifyWay
	 * @return
	 */
	private Integer getStatus(int notifyType, int notifyWay){
		if(notifyWay == 2){//短信通知
			//利息支付、本金归还、奖励活动 
			if(notifyType == 5 || notifyType == 3 || notifyType == 6 || notifyType == 8){
				return Constant.ENABLE;
			}
			return Constant.DISABLE;
		}else{
			return Constant.ENABLE;
		}
	}

	@Override
	public List<MemberNotifySettings> getCheckedNotifySettings(Long memberId)
			throws Exception {
		try{
			return memberNotifySettingsMapper.getCheckedNotifySettings(memberId);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public int unsubscribe(Long memberId) throws ManagerException {
		try{
			return memberNotifySettingsMapper.unsubscribe(memberId);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public void sendMessageDirectly(MemberNotifySettingsForSendMsg sendType)
			throws Exception {
		if(sendType.getMsgType() == Constant.MSG_TEMPLATE_TYPE_PHONE){
			if(PropertiesUtil.isDev()){
				if(sendType.isMarketingSMS()){
					logger.debug("营销类发送短信提醒  mobile:"+sendType.getSendForward()+" message:"+sendType.getMessage());
				}else{
					logger.debug("发送短信提醒  mobile:"+sendType.getSendForward()+" message:"+sendType.getMessage());
				}
			}else{					
				if(sendType.isMarketingSMS()){
					smsMobileSend.sendMarketingSMS(Long.parseLong(sendType.getSendForward()),sendType.getMessage());
				}else{
					String smsUrl = PropertiesUtil.getProperties("smsurl.common");
					smsMobileSend.sendSMS(smsUrl, Long.parseLong(sendType.getSendForward()),sendType.getMessage());
				}
			}
		}else if(sendType.getMsgType() == Constant.MSG_TEMPLATE_TYPE_EMAIL){
			
		}else if(sendType.getMsgType() == Constant.MSG_TEMPLATE_TYPE_STAND){
			
		}
	}

}
