package com.yourong.backend.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.common.PushClient;
import com.yourong.core.fin.dao.BalanceMapper;
import com.yourong.core.msg.dao.CustomMessageMapper;
import com.yourong.core.msg.manager.CustomMessageManager;
import com.yourong.core.msg.manager.MessageBodyManager;
import com.yourong.core.msg.manager.MessageLogManager;
import com.yourong.core.msg.model.CustomMessage;
import com.yourong.core.msg.model.MessageBody;
import com.yourong.core.msg.model.MessageLog;
import com.yourong.core.push.PushEnum;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

/**
 * 消息定时任务处理
 * 
 * @author Administrator
 *
 */
public class sendMessageTask {

	@Autowired
	private CustomMessageManager customMessageManager;

	private static final Logger logger = LoggerFactory.getLogger(sendMessageTask.class);
	
	// 当天新注册并未投资的用户发送短信标识
	private static final String current_day_send_msg_flag = "Y";

	@Autowired
	private TaskExecutor threadPool;

	@Autowired
	private TransactionMapper transactionMapper;
	
	@Autowired
	private BalanceMapper balanceMapper;
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
	private MemberManager memberManager;

	@Autowired
	private CustomMessageMapper customMessageMapper;
	
	@Autowired
	private MessageBodyManager messageBodyManager;
	
	@Autowired
	private MessageLogManager messageLogManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	/** 每次查询数量 **/
	static final int numConstant = 1000;

	static final int startConstant = 0;

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("发送消息 task start");
					updateMessageStatusTo4();
					logger.info("发送消息  task end");
				} catch (Exception e) {
					logger.error("发送消息定时任务执行异常：", e);
				}
			}
		});
	}

	/**
	 * 把消息置为发布状态
	 * 
	 * @throws ManagerException
	 */
	private void updateMessageStatusTo4() throws ManagerException {

		List<CustomMessage> customMessageList = customMessageMapper
				.findUnsendMessage();
		int count = 0;
		for (CustomMessage customMessage : customMessageList) {
			int flag = customMessageMapper
					.updateShortMessageStatusTo4(customMessage.getId());
			if (flag < 1) {
				continue;
			}
			count++;
			
			if(1==customMessage.getMsgType()){
				msgType1(customMessage);
			}
			if(4==customMessage.getMsgType()){
				msgType4(customMessage);
			}
			if(5==customMessage.getMsgType()){
				msgType5(customMessage);
			}

		}
		logger.info(count + "条短信置为发布状态：");
	}
	
	
	private void msgType1(CustomMessage customMessage){
		
		
		
		// ** 目标用户：1-所有用户、2-投资过的用户、3-项目用户、4-自定义用户 、5-大于等于预设人气值的用户**/
		if (customMessage.getUserType() == 1) {
			Long start = (long) 1;
			String memberMobile = "";
			//速盾
			if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
				memberMobile = memberMapper.getMemberMobile(start);
				while(memberMobile != null && memberMobile != ""){
					customMessageManager.sendMessage(memberMobile,customMessage.getContent());
					start = start + 80;
					memberMobile = memberMapper.getMemberMobile(start);
				}
			}else {
			//亿美
				threadPool.execute(new ShortMessageNoticeThread(customMessage.getContent()));
			}
			
		}
		if (customMessage.getUserType() == 2) {
			//customMessageManager.sendMessage("13269756545,18658177193","今天的你，一定是美丽的你，辛苦啦女王！平台为您赠送38元节日红包，请及时登录领取，详情请咨询666666666，退订回复T");
			Map<String, Object> map = Maps.newHashMap();
			int startNum = startConstant;
			int num = numConstant;
			int size = numConstant;
			//速盾
			if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
				StringBuilder memberMobile = new StringBuilder("");
				for (int i = 0; size > 0; i++) {
					map.put("startNum", startNum + i * num);
					map.put("num", num);
					List<Long> memberList = transactionMapper.selectMemberSucceed(map);
					size = memberList.size();
					if(size == 0){
						break;
					}
					for (Long memberId : memberList) {
						String memberIdStr = "";
						Long mobile = transactionMapper.selectMemberMobileSucceed(memberId);
						if(mobile != null){
							memberIdStr = mobile.toString();
							memberMobile.append(memberIdStr);
							memberMobile.append(",");
						}
					}
					String memberMobileStr = memberMobile.toString();
					memberMobileStr = memberMobileStr.substring(0,memberMobile.length()-1);
					customMessageManager.sendMessage(memberMobileStr,customMessage.getContent());
				}
			}else {
				//亿美
				for (int i = 0; size > 0; i++) {
					map.put("startNum", startNum + i * num);
					map.put("num", num);
					List<Long> memberList = transactionMapper.selectMemberSucceed(map);
					size = memberList.size();
					threadPool.execute(new ShortMessageNoticeByMemberThread(memberList, customMessage.getContent()));
				}
			}
	
		}
		
		if (customMessage.getUserType() == 3) {
			Map<String, Object> map = Maps.newHashMap();
			int startNum = startConstant;
			int num = numConstant;
			int size = numConstant;
			//速盾
			if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
				StringBuilder memberMobile = new StringBuilder("");
				map.put("projectId", customMessage.getCustomAttr());
				for (int i = 0; size > 0; i++) {
					map.put("startNum", startNum + i * num);
					map.put("num", num);
					List<Long> memberList = transactionMapper.selectMemberByProject(map);
					size = memberList.size();
					if(size == 0){
						break;
					}
					for (Long memberId : memberList) {
						String memberIdStr = "";
						Long mobile = transactionMapper.selectMemberMobileSucceed(memberId);
						if(mobile != null){
							memberIdStr = mobile.toString();
							memberMobile.append(memberIdStr);
							memberMobile.append(",");
						}
					}
					String memberMobileStr = memberMobile.toString();
					memberMobileStr = memberMobileStr.substring(0,memberMobile.length()-1);
					customMessageManager.sendMessage(memberMobileStr,customMessage.getContent());
				}
			}else {
				//亿美
				map.put("projectId", customMessage.getCustomAttr());
				for (int i = 0; size > 0; i++) {
					map.put("startNum", startNum + i * num);
					map.put("num", num);
					List<Long> memberList = transactionMapper.selectMemberByProject(map);
					size = memberList.size();
					threadPool.execute(new ShortMessageNoticeByMemberThread(memberList, customMessage.getContent()));
				}
			}
		}
		
		
		if (customMessage.getUserType() == 4) {
			StringBuilder memberMobile = new StringBuilder("");
			String customAttr = customMessage.getCustomAttr();
			customAttr = customAttr.replaceAll("；", ";");
			customAttr = customAttr.replaceAll(" ", "");
			String[] customAttrs = customAttr.split(";");
			List<Long> memberList = Lists.newArrayList();
			for (int i = 0; i < customAttrs.length; i++) {
				memberList.add(Long.parseLong(customAttrs[i]));
			}
			if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
				for (Long memberId : memberList) {
					String memberIdStr = "";
					Long mobile = transactionMapper.selectMemberMobileSucceed(memberId);
					if(mobile != null){
						memberIdStr = mobile.toString();
						memberMobile.append(memberIdStr);
						memberMobile.append(",");
					}
				}
				String memberMobileStr = memberMobile.toString();
				memberMobileStr = memberMobileStr.substring(0,memberMobile.length()-1);
				customMessageManager.sendMessage(memberMobileStr,customMessage.getContent());
			}else{
				threadPool.execute(new ShortMessageNoticeByMemberThread(memberList, customMessage.getContent()));
			}
		}
		
		
		if (customMessage.getUserType() == 5) {
			StringBuilder memberMobile = new StringBuilder("");
			Map<String, Object> map = Maps.newHashMap();
			int startNum = startConstant;
			int num = numConstant;
			int size = numConstant;
			//短信预设人气值
			String popularity =SysServiceUtils.getDictValue("message_member_popularity","message_member_popularity", ""); 
			map.put("popularity", popularity);
			if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
				for (int i = 0; size > 0; i++) {
					map.put("startNum", startNum + i * num);
					map.put("num", num);
					List<Long> memberList = balanceMapper.selectMemberPopularity(map);
					size = memberList.size();
					if(size == 0){
						break;
					}
					for (Long memberId : memberList) {
						String memberIdStr = "";
						Long mobile = transactionMapper.selectMemberMobileSucceed(memberId);
						if(mobile != null){
							memberIdStr = mobile.toString();
							memberMobile.append(memberIdStr);
							memberMobile.append(",");
						}
					}
					String memberMobileStr = memberMobile.toString();
					memberMobileStr = memberMobileStr.substring(0,memberMobile.length()-1);
					customMessageManager.sendMessage(memberMobileStr,customMessage.getContent());
				}
			}else{
				for (int i = 0; size > 0; i++) {
					map.put("startNum", startNum + i * num);
					map.put("num", num);
					//查询人气值总余额小于预设值的用户
					List<Long> memberList = balanceMapper.selectMemberPopularity(map);
					size = memberList.size();
					threadPool.execute(new ShortMessageNoticeByMemberThread(memberList, customMessage.getContent()));
				}
			}			
		}
		
		
		if (customMessage.getUserType() == 6) {
			StringBuilder memberMobile = new StringBuilder("");
			Map<String, Object> map = Maps.newHashMap();
			int startNum = startConstant;
			int num = numConstant;
			int size = numConstant;
			if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
				for (int i = 0; size > 0; i++) {
					map.put("startNum", startNum + i * num);
					map.put("num", num);
					List<Long> memberList = memberMapper.countRealNameMember(map);
					size = memberList.size();
					if(size == 0){
						break;
					}
					for (Long memberId : memberList) {
						String memberIdStr = "";
						Long mobile = transactionMapper.selectMemberMobileSucceed(memberId);
						if(mobile != null){
							memberIdStr = mobile.toString();
							memberMobile.append(memberIdStr);
							memberMobile.append(",");
						}
					}
					String memberMobileStr = memberMobile.toString();
					memberMobileStr = memberMobileStr.substring(0,memberMobile.length()-1);
					customMessageManager.sendMessage(memberMobileStr,customMessage.getContent());
				}
			}else{
				for (int i = 0; size > 0; i++) {
					map.put("startNum", startNum + i * num);
					map.put("num", num);
					List<Long> memberList = memberMapper.countRealNameMember(map);
					size = memberList.size();
					threadPool.execute(new ShortMessageNoticeByMemberThread(memberList, customMessage.getContent()));
				}
			}	
		}
	
		
		// 新注册用户
		if (customMessage.getUserType() == 7) {
			// 注册天数
			Integer registerDays = customMessage.getRegisterDays();
			if (registerDays != null) {
				// 判断结束时间已过期
				if(new Date().compareTo(customMessage.getRegisterEndTime()) > 0) {//大于结束时间 1
					return;
				}
				// 判断当天是否已经发送过
				String key = RedisConstant.NEW_REGISTER_MEMBER_SEND_MSG_FLAG ;
				if(RedisManager.isExit(key)) {
					logger.info("新注册用户缓存已记录今天已发送过短信，日期={}", customMessage.getSendDate());
					// 短信发送后，更新当前短信内容的状态为待发布
					customMessageMapper.updateWaitStatus(customMessage.getId());
					return;
				}
				// 查询要发送的新注册并未投资的用户
				Date registerStartTime = DateUtils.getDateTimeFromString(DateUtils.addDays(new Date(), -registerDays));
				Date registerEndTime = DateUtils.getDateTimeFromString(DateUtils.addDays(registerStartTime, 1));
				// 查询用户
				List<Member> memberList = memberMapper.selectRegisterNumberByDate(registerStartTime, registerEndTime);
				// 过滤未投资的注册用户
				List<Long> toSendMemberList = Lists.newArrayList();

				for (Member member : memberList) {
					// 查询交易信息
					TransactionQuery transactionQuery = new TransactionQuery();
					transactionQuery.setMemberId(member.getId());
					//transactionQuery.setStatus(StatusEnum.TRANSACTION_REPAYMENT.getStatus()); //不成功交易也算
					int isExists = transactionMapper.selectTransactionsByQueryParamsTotalCount(transactionQuery);
					if (isExists <= 0) {
						toSendMemberList.add(member.getId());
					}
				}
				// 发短信
				if (Collections3.isNotEmpty(toSendMemberList)) {
					if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
						StringBuilder memberMobile = new StringBuilder("");
						for (Long memberId : toSendMemberList) {
							String memberIdStr = "";
							Long mobile = transactionMapper.selectMemberMobileSucceed(memberId);
							if(mobile != null){
								memberIdStr = mobile.toString();
								memberMobile.append(memberIdStr);
								memberMobile.append(",");
							}
						}
						String memberMobileStr = memberMobile.toString();
						memberMobileStr = memberMobileStr.substring(0,memberMobile.length()-1);
						if(memberMobileStr != null && memberMobileStr != ""){
							customMessageManager.sendMessage(memberMobileStr,customMessage.getContent());
						}
						memberMobileStr = "";
					}else{
						threadPool.execute(new ShortMessageNoticeByMemberThread(toSendMemberList, customMessage.getContent()));
					}
					
					// 记录到当天已经发送过
					RedisManager.set(key, current_day_send_msg_flag);
					RedisManager.expire(key, 86400);//一天一次86400
				}
				// 短信发送后，更新当前短信内容的状态为待发布
				customMessageMapper.updateWaitStatus(customMessage.getId());
				
			}
		}

		/*//投资模板
		if (customMessage.getUserType().equals(8)){
			customMessage.getTypeRule();
			JSONObject jsonObject = JSONObject.parseObject(customMessage.getTypeRule());
			jsonObject.get("");
		}
		//活动模板
		if (customMessage.getUserType().equals(9)){
			//速盾
			if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
				StringBuilder stringBuilder=new StringBuilder();
				customMessageManager.sendMessage(stringBuilder.toString(),customMessage.getContent());
			}else {

			}
		}
		//唤醒模板
		if (customMessage.getUserType().equals(10)){
			//速盾
			if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
				StringBuilder stringBuilder=new StringBuilder();
				customMessageManager.sendMessage(stringBuilder.toString(),customMessage.getContent());
			}else {

			}
		}
		//拉新模板
		if (customMessage.getUserType().equals(11)){
			//速盾
			if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
				StringBuilder stringBuilder=new StringBuilder();
				customMessageManager.sendMessage(stringBuilder.toString(),customMessage.getContent());
			}else {

			}
		}*/
		//自定义用户
		if (customMessage.getUserType().equals(12)){
			StringBuilder stringBuilder=new StringBuilder();
			List<Long> toSendMemberList = Lists.newArrayList();
			File file=new File(customMessage.getCustomFileUrl());
			XSSFWorkbook hssfWorkbook= null;
			try {
				hssfWorkbook = new XSSFWorkbook(new FileInputStream(file));
			} catch (IOException e) {
				logger.error("解析Excel异常",e);
			}
			XSSFSheet sheet = hssfWorkbook.getSheetAt(0);
			DecimalFormat decimalFormat=new DecimalFormat("0");
			XSSFRow row=null;
			XSSFCell cell=null;
			for(int j=0;j<sheet.getLastRowNum()+1;j++){
				row = sheet.getRow(j);
				cell = row.getCell(0);
				cell.setCellType(CellType.NUMERIC);
				Long memberId=null;
				try {
					memberId =Long.parseLong(decimalFormat.format(cell.getNumericCellValue()));
				} catch (NumberFormatException e) {
					logger.error("memberid转换异常{}",cell.getNumericCellValue(),e);
					continue;
				}
				if (memberId==null){
					logger.error("memberid不存在{}",cell.getNumericCellValue());
					continue;
				}
				if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(2)){
					toSendMemberList.add(memberId);
				}else {
					Member member= memberMapper.selectByPrimaryKey(memberId);
					if (member!=null){
						if (stringBuilder!=null&&stringBuilder.length()>0){
							stringBuilder.append(",");
						}
						stringBuilder.append(member.getMobile());
					}
				}
			}
			//速盾
			if (customMessage.getSmsType()!=null&&customMessage.getSmsType().equals(1)){
				threadPool.execute(new ShortMessageNoticeByMemberThread(toSendMemberList, customMessage.getContent()));
			}else {
				customMessageManager.sendMessage(stringBuilder.toString(),customMessage.getContent());
			}
		}
	}
	private void msgType5(CustomMessage customMessage){
		// ** 目标用户：1-所有用户、2-投资过的用户、3-项目用户、4-自定义用户 、5-大于等于预设人气值的用户**/
					if (customMessage.getUserType() == 1) {  //所有用户设定为，用户读取时写入数据
						return;
					}
					
					if (customMessage.getUserType() == 2) {
						Map<String, Object> map = Maps.newHashMap();
						int startNum = startConstant;
						int num = numConstant;
						int size = numConstant;
						for (int i = 0; size > 0; i++) {
							map.put("startNum", startNum + i * num);
							map.put("num", num);
							List<Long> memberList = transactionMapper
									.selectMemberSucceed(map);
							size = memberList.size();
							threadPool.execute(new appMessageNoticeByMemberThread(
									memberList, customMessage));
						}

					}
					if (customMessage.getUserType() == 3) {
						Map<String, Object> map = Maps.newHashMap();
						int startNum = startConstant;
						int num = numConstant;
						int size = numConstant;
						map.put("projectId", customMessage.getCustomAttr());
						for (int i = 0; size > 0; i++) {
							map.put("startNum", startNum + i * num);
							map.put("num", num);
							List<Long> memberList = transactionMapper
									.selectMemberByProject(map);
							size = memberList.size();
							threadPool.execute(new appMessageNoticeByMemberThread(
									memberList, customMessage));
						}

					}
					if (customMessage.getUserType() == 4) {
						String customAttr = customMessage.getCustomAttr();
						customAttr = customAttr.replaceAll("；", ";");
						customAttr = customAttr.replaceAll(" ", "");
						String[] customAttrs = customAttr.split(";");
						List<Long> memberList = Lists.newArrayList();
						for (int i = 0; i < customAttrs.length; i++) {
							memberList.add(Long.parseLong(customAttrs[i]));
						}
						threadPool.execute(new appMessageNoticeByMemberThread(
								memberList, customMessage));
					}
					
					
	}
	
	private void msgType4(CustomMessage customMessage){
		// ** 目标用户：1-所有用户、2-投资过的用户、3-项目用户、4-自定义用户 、5-大于等于预设人气值的用户**/
					if (customMessage.getUserType() == 1) {  
						try {
							long maxMember = memberManager.getMaxMemberId();
							for (long memberId = Long.parseLong(Config.firstMemberId); memberId <= maxMember; memberId++) {
								PushClient.pushDIYMsgToMember(customMessage.getContent(), memberId, "", PushEnum.DIY,customMessage.getRemark(),customMessage.getMsgName());
				 			}
						} catch (ManagerException e) {
							logger.error("获取最大用户ID异常");
						}
			 		}
					if (customMessage.getUserType() == 2) {
						Map<String, Object> map = Maps.newHashMap();
						int startNum = startConstant;
						int num = numConstant;
						int size = numConstant;
						for (int i = 0; size > 0; i++) {
							map.put("startNum", startNum + i * num);
							map.put("num", num);
							List<Long> memberList = transactionMapper
									.selectMemberSucceed(map);
							size = memberList.size();
							threadPool.execute(new appPushNoticeByMemberThread(
									memberList, customMessage));
						}

					}
					if (customMessage.getUserType() == 3) {
						Map<String, Object> map = Maps.newHashMap();
						int startNum = startConstant;
						int num = numConstant;
						int size = numConstant;
						map.put("projectId", customMessage.getCustomAttr());
						for (int i = 0; size > 0; i++) {
							map.put("startNum", startNum + i * num);
							map.put("num", num);
							List<Long> memberList = transactionMapper
									.selectMemberByProject(map);
							size = memberList.size();
							threadPool.execute(new appPushNoticeByMemberThread(
									memberList, customMessage));
						}

					}
					if (customMessage.getUserType() == 4) {
						String customAttr = customMessage.getCustomAttr();
						customAttr = customAttr.replaceAll("；", ";");
						customAttr = customAttr.replaceAll(" ", "");
						String[] customAttrs = customAttr.split(";");
						List<Long> memberList = Lists.newArrayList();
						for (int i = 0; i < customAttrs.length; i++) {
							memberList.add(Long.parseLong(customAttrs[i]));
						}
						threadPool.execute(new appPushNoticeByMemberThread(
								memberList, customMessage));
					}
					
					
	}
	
	

	private class ShortMessageNoticeThread implements Runnable {

		private String content;

		public ShortMessageNoticeThread(String content) {
			this.content = content;
		}

		@Override
		public void run() {
			sendShortMessageNotice();
		}

		public void sendShortMessageNotice() {
			MessageClient.sendShortMessageNotice(content);
		}

	}

	private class ShortMessageNoticeByMemberThread implements Runnable {

		private String content;

		private List<Long> memberList;

		public ShortMessageNoticeByMemberThread(List<Long> memberList,
				String content) {
			this.content = content;
			this.memberList = memberList;
		}

		@Override
		public void run() {
			sendShortMessageNoticeByMember();
		}

		public void sendShortMessageNoticeByMember() {
			MessageClient.sendShortMessageNoticeByMember(memberList, content);
		}

	}

	private class appMessageNoticeByMemberThread implements Runnable {

		private CustomMessage customMessage;

		private List<Long> memberList;

		public appMessageNoticeByMemberThread(List<Long> memberList,
				CustomMessage customMessage) {
			this.customMessage = customMessage;
			this.memberList = memberList;
		}

		@Override
		public void run() {
			sendAppMessageNoticeByMemberThread();
		}

		public void sendAppMessageNoticeByMemberThread() {
			
			for(Long memberId : memberList){
				MessageLog log = new MessageLog();
				MessageBody mb = messageBodyManager.getMessageBody(customMessage.getId(), 1);
				try {
					if (mb == null) {
						mb = new MessageBody( );
						
						String content = "{\"part1\":\""+customMessage.getContent()+"\",\"part4\":\""+customMessage.getRemark()+"\""
								+ ",\"part5\":\""+customMessage.getMsgName()+"\"}";
						mb.setContent(content);
						mb.setMsgId(customMessage.getId());
						mb.setMsgSource(1);
						messageBodyManager.insert(mb);
					}
					log.setMemberId(memberId);
					log.setMsgBodyId(mb.getId());
					log.setMsgSource(1);
					log.setMsgType(customMessage.getMsgType());
					log.setNotifyType(customMessage.getNotifyType());
					log.setReceiveDate(DateUtils.getCurrentDate());
					log.setServiceType(16);//自定义类型16
					log.setStatus(0);
					log.setDelFlag(1);
					log.setMsgSourceId(customMessage.getId());
					messageLogManager.insert(log);
				} catch (ManagerException e) {
					logger.error("写入app消息异常,log={},mb={}",log,mb,e);
				}
			}
		}

	}
	
	private class appPushNoticeByMemberThread implements Runnable {

		private CustomMessage customMessage;

		private List<Long> memberList;

		public appPushNoticeByMemberThread(List<Long> memberList,
				CustomMessage customMessage) {
			this.customMessage = customMessage;
			this.memberList = memberList;
		}

		@Override
		public void run() {
			sendAppPushNoticeByMemberThread();
		}

		public void sendAppPushNoticeByMemberThread() {
			
			for(Long memberId : memberList){
				PushClient.pushDIYMsgToMember(customMessage.getContent(), memberId, "", PushEnum.DIY,customMessage.getRemark(),customMessage.getMsgName());
			}
		}

	}
}
