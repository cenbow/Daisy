package com.yourong.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yourong.common.baidu.push.auth.PushKeyPair;
import com.yourong.common.baidu.push.client.BaiduPushClient;
import com.yourong.common.baidu.push.exception.PushClientException;
import com.yourong.common.baidu.push.exception.PushServerException;
import com.yourong.common.baidu.push.model.PushMsgToAllRequest;
import com.yourong.common.baidu.push.model.PushMsgToAllResponse;
import com.yourong.common.baidu.push.model.PushMsgToSingleDeviceRequest;
import com.yourong.common.baidu.push.model.PushMsgToSingleDeviceResponse;
import com.yourong.common.baidu.yun.log.YunLogEvent;
import com.yourong.common.baidu.yun.log.YunLogHandler;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.common.util.StringUtil;
import com.yourong.core.push.PushEnum;
import com.yourong.core.push.PushMsgParam;
import com.yourong.core.push.PushMsgResponse;
import com.yourong.core.uc.manager.MemberTokenManager;
import com.yourong.core.uc.model.MemberToken;

/**
 * 推送
 * @author Administrator
 *
 */
public class PushClient {
	//android、ios key
	private static String androidApiKey = null;
	private static String androidSecretKey = null;
	private static String iosApiKey = null;
	private static String iosSecretKey = null;
	private static PushKeyPair androidPushKeyPair = null;
	private static PushKeyPair iosPushKeyPair = null;
	//设备类型
	private static final int ANDROID_DEVICE = 3;
	private static final int IOS_DEVICE = 4;
	//消息类型，IOS只有通知
	private static final int MESSAGE_TYPE_ADVICE = 1; //通知
	private static final int MESSAGE_TYPE_MSG = 0;//透传消息
	private static final int DEFAULT_MSG_EXPIRES = 3600; //默认消息过期时间(一个小时)
	
	private static MemberTokenManager memberTokenManager = SpringContextHolder.getBean(MemberTokenManager.class);
	
	//初始化
	static{
		androidApiKey = PropertiesUtil.getBaiduPushAndroidApiKey();
		androidSecretKey = PropertiesUtil.getBaiduPushAndroidSecretKey();
		iosApiKey = PropertiesUtil.getBaiduPushIOSApiKey();
		iosSecretKey = PropertiesUtil.getBaiduPushIOSSecretKey();
		androidPushKeyPair = new PushKeyPair(androidApiKey, androidSecretKey);
		iosPushKeyPair = new PushKeyPair(iosApiKey, iosSecretKey);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(PushClient.class);
	
	

	/**
	 * 推送消息给会员
	 * @param message 消息内容
	 * @param memberId 用户ID
	 * @param bizId  业务编号  
	 * @param pushEnum 业务类型
	 */
	public static void pushMsgToMember(String message, Long memberId, String bizId, PushEnum pushEnum){
		MemberToken memberToken = getPushDevice(memberId);
		if(memberToken != null){
			PushMsgResponse response = null;
			String channelId = memberToken.getChannelId();
			if(StringUtil.isNotBlank(channelId) && !channelId.equals("0")){
				if(memberToken.getTokenType().intValue() == IOS_DEVICE){
					response = pushMsgToSingleIOSDevice(message, memberToken.getChannelId(), bizId, pushEnum,"","");
				}else if(memberToken.getTokenType().intValue() == ANDROID_DEVICE){
					response = pushMsgToSingleAndroidDevice(message, memberToken.getChannelId(), bizId, pushEnum, memberId,"","");
				}
				writePushLog(response);
			}else{
				if(logger.isDebugEnabled()){
					logger.debug("用户拒绝接收推送消息："+memberToken.getMemberId()+",channelId"+channelId);
				}
			}
		}
	}
	
	/**
	 * 推送自定义消息给会员
	 * @param message 消息内容
	 * @param memberId 用户ID
	 * @param bizId  业务编号  
	 * @param pushEnum 业务类型
	 */
	public static void pushDIYMsgToMember(String message, Long memberId, String bizId, PushEnum pushEnum,String url,String title){
		MemberToken memberToken = getPushDevice(memberId);
		if(memberToken != null){
			PushMsgResponse response = null;
			String channelId = memberToken.getChannelId();
			if(StringUtil.isNotBlank(channelId) && !channelId.equals("0")){
				if(memberToken.getTokenType().intValue() == IOS_DEVICE){
					response = pushMsgToSingleIOSDevice(message, memberToken.getChannelId(), bizId, pushEnum,url,title);
				}else if(memberToken.getTokenType().intValue() == ANDROID_DEVICE){
					response = pushMsgToSingleAndroidDevice(message, memberToken.getChannelId(), bizId, pushEnum, memberId,url,title);
				}
				writePushLog(response);
			}else{
				if(logger.isDebugEnabled()){
					logger.debug("用户拒绝接收推送消息："+memberToken.getMemberId()+",channelId"+channelId);
				}
			}
		}
	}
	

	/**
	 * 推送消息给所有会员
	 * @param message 消息内容
	 * @param bizId 业务编号
	 * @param pushEnum 业务类型
	 */
	public static void pushMsgToAllMember(String message, String bizId, PushEnum pushEnum){
		PushMsgResponse androidResponse = pushMsgToAllAndroidDevice(message, bizId, pushEnum);
		writePushLog(androidResponse);
		PushMsgResponse iosResponse = pushMsgToAllIOSDevice(message, bizId, pushEnum);
		writePushLog(iosResponse);
	}
	


	/**
	 * 推送消息到具体某个Android设备
	 * @param message 消息内容
	 * @param channelId 设备ID
	 * @param bizId 业务ID
	 * @param pushEnum 业务类型
	 * @param uid 用户编号，防止收到他人账户的推送
	 * @return
	 */
	private static PushMsgResponse pushMsgToSingleAndroidDevice(String message, String channelId, String bizId, PushEnum pushEnum, Long uid,String url,String title){
		PushMsgParam param = new PushMsgParam();
		param.setDeviceType(ANDROID_DEVICE);
		param.setMessageType(MESSAGE_TYPE_MSG);
		param.setMsgExpires(getMsgExpires(pushEnum));
		JSONObject notification = new JSONObject();
		notification.put("msg", message);
		notification.put("bizId", bizId);
		notification.put("bizType", pushEnum.getIndex());
		notification.put("uid", uid);
		notification.put("url", url);
		notification.put("title", title);
		param.setMessage(notification.toJSONString());
		param.setChannelId(channelId);
		return pushMsgToSingleDevice(param);
	}
	
	/**
	 * 推送消息到具体某个IOS设备
	 * @param message 消息内容
	 * @param channelId 设备ID
	 * @param bizId 业务ID
	 * @param pushEnum 业务类型
	 * @return
	 */
	private static PushMsgResponse pushMsgToSingleIOSDevice(String message, String channelId, String bizId, PushEnum pushEnum,String url,String title){
		PushMsgParam param = new PushMsgParam();
		param.setDeviceType(IOS_DEVICE);
		param.setMessageType(MESSAGE_TYPE_ADVICE);
		param.setMsgExpires(getMsgExpires(pushEnum));
		param.setChannelId(channelId);
		JSONObject notification = new JSONObject();
		JSONObject jsonAPS = new JSONObject();
		jsonAPS.put("alert", message);
		jsonAPS.put("sound", PropertiesUtil.getBaiduPushMsgSoundName());
		notification.put("aps", jsonAPS);
		notification.put("bizId", bizId);
		notification.put("bizType", pushEnum.getIndex());
		notification.put("url", url);
		notification.put("title", title);
		param.setMessage(notification.toJSONString());
		return pushMsgToSingleDevice(param);
	}
	
	
	/**
	 * 推送消息到所有Android设备
	 * @param message 消息内容
	 * @param bizId 业务ID
	 * @param pushEnum 业务类型
	 * @return
	 */
	private static PushMsgResponse pushMsgToAllAndroidDevice(String message, String bizId, PushEnum pushEnum){
		PushMsgParam param = new PushMsgParam();
		param.setDeviceType(ANDROID_DEVICE);
		param.setMessageType(MESSAGE_TYPE_MSG);
		param.setMsgExpires(getMsgExpires(pushEnum));
		JSONObject notification = new JSONObject();
		notification.put("msg", message);
		notification.put("bizId", bizId);
		notification.put("bizType", pushEnum.getIndex());
		notification.put("uid", "0");
		param.setMessage(notification.toJSONString());
		return pushMsgToAllDevice(param);
	}
	
	/**
	 * 推送消息到所有IOS设备
	 * @param message 消息内容
	 * @param bizId 业务ID
	 * @param pushEnum 业务类型
	 * @return
	 */
	private static PushMsgResponse pushMsgToAllIOSDevice(String message, String bizId, PushEnum pushEnum){
		PushMsgParam param = new PushMsgParam();
		param.setDeviceType(IOS_DEVICE);
		param.setMessageType(MESSAGE_TYPE_ADVICE);
		param.setMsgExpires(getMsgExpires(pushEnum));
		param.setMessage(message);
		JSONObject notification = new JSONObject();
		JSONObject jsonAPS = new JSONObject();
		jsonAPS.put("alert", message);
		jsonAPS.put("sound", PropertiesUtil.getBaiduPushMsgSoundName());
		notification.put("aps", jsonAPS);
		notification.put("bizId", bizId);
		notification.put("bizType", pushEnum.getIndex());
		param.setMessage(notification.toJSONString());
		return pushMsgToAllDevice(param);
	}
	
	
	/**
	 * 推送消息到所有设备
	 * @param param
	 * @return
	 */
	private static PushMsgResponse pushMsgToAllDevice(PushMsgParam param){
		BaiduPushClient pushClient = getPushClientByDeviceType(param.getDeviceType());
		pushClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				logger.info(event.getMessage());
			}
		});
		PushMsgToAllRequest request = new PushMsgToAllRequest()
		.addMsgExpires(param.getMsgExpires())
		.addMessageType(param.getMessageType())
		.addMessage(param.getMessage())
		.addDeviceType(param.getDeviceType());
		if(param.getDeviceType() == IOS_DEVICE){
			request.addDepolyStatus(PropertiesUtil.getBaiduPushDeployStatus());
		}
		try {
			PushMsgToAllResponse response = pushClient.pushMsgToAll(request);
			PushMsgResponse resp = new PushMsgResponse();
			resp.setMsgId(response.getMsgId());
			resp.setSendTime(response.getSendTime());
			resp.setDeviceType(param.getDeviceType());
			resp.setMessage(param.getMessage());
			return resp;
		} catch (PushClientException e) {
			logger.error("Baidu Push Client Error", e);
		} catch (PushServerException e) {
			logger.error(String.format("Baidu Push Server : requestId: %d, errorCode: %d, errorMessage: %s",e.getRequestId(), e.getErrorCode(), e.getErrorMsg()), e);
		}
		return null;
	}
	
	/**
	 * 推送消息到单个设备
	 * @param param
	 * @return
	 */
	private static PushMsgResponse pushMsgToSingleDevice(PushMsgParam param){
		BaiduPushClient pushClient = getPushClientByDeviceType(param.getDeviceType());
		pushClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				logger.info(event.getMessage());
			}
		});
		PushMsgToSingleDeviceRequest request = new PushMsgToSingleDeviceRequest()
		.addChannelId(param.getChannelId())
		.addMsgExpires(param.getMsgExpires())
		.addMessageType(param.getMessageType())
		.addMessage(param.getMessage())
		.addDeviceType(param.getDeviceType());
		if(param.getDeviceType() == IOS_DEVICE){
			request.addDeployStatus(PropertiesUtil.getBaiduPushDeployStatus());
		}
		try {
			PushMsgToSingleDeviceResponse response = pushClient.pushMsgToSingleDevice(request);
			PushMsgResponse resp = new PushMsgResponse();
			resp.setMsgId(response.getMsgId());
			resp.setSendTime(response.getSendTime());
			resp.setDeviceType(param.getDeviceType());
			resp.setChannelId(param.getChannelId());
			resp.setMessage(param.getMessage());
			return resp;
		} catch (PushClientException e) {
			logger.error("Baidu Push Client Error", e);
		} catch (PushServerException e) {
			int errorCode = e.getErrorCode();
			if (errorCode == 30608 || errorCode == 30600 || errorCode == 40004 || errorCode == 40013) {
				logger.error(String.format("Baidu Push Server : requestId: %d, errorCode: %d, errorMessage: %s",e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
			} else {				
				logger.error(String.format("Baidu Push Server : requestId: %d, errorCode: %d, errorMessage: %s",e.getRequestId(), e.getErrorCode(), e.getErrorMsg()), e);
			}
		}
		return null;
	}
	
	/**
	 * 获得IOS推送客户端连接
	 * @return
	 */
	private static BaiduPushClient getIOSPushClient(){
		return new BaiduPushClient(iosPushKeyPair);
	}
	
	/**
	 * 获得Android推送客户端连接
	 * @return
	 */
	private static BaiduPushClient getAndroidPushClient(){
		return new BaiduPushClient(androidPushKeyPair);
	}
	
	/**
	 * 根据驱动类型获得客户端连接
	 * @param deviceType
	 * @return
	 */
	private static BaiduPushClient getPushClientByDeviceType(int deviceType){
		if(deviceType == IOS_DEVICE){
			return getIOSPushClient();
		}
		return getAndroidPushClient();
	}
	
	/**
	 * 获得登录Token
	 * @param memberId
	 * @return
	 */
	private static MemberToken getPushDevice(Long memberId){
		try {
			MemberToken memberToken = memberTokenManager.queryLastLoginDeviceByMemberId(memberId);
			int deviceType = -1;
			if(memberToken != null){
				if(memberToken.getTokenType().intValue() == 1){
					deviceType = ANDROID_DEVICE;
				}else if(memberToken.getTokenType().intValue() == 2){
					deviceType = IOS_DEVICE;
				}
				if(deviceType != -1){
					memberToken.setTokenType(deviceType);
					return memberToken;
				}
			}
		} catch (ManagerException e) {
			logger.error("通过会员ID查询登录设备异常", e);
		}
		return null;
	}
	
	/**
	 * 记录推送日志
	 * 目前只是记录到文件，后面应该考虑记录到DB。
	 * @param response
	 */
	private static void writePushLog(PushMsgResponse response){
		if(response != null){
			logger.info("推送消息："+response.getMessage()+",消息ID:"+response.getMsgId()+","
					+ "消息发送时间："+response.getSendTime()+",目标设备："+response.getDeviceType()+","
							+ "目标用户："+response.getChannelId());
		}
	}
	
	/**
	 * 消息过期时间
	 * @param pushEnum
	 * @return
	 */
	private static int getMsgExpires(PushEnum pushEnum){
		if(pushEnum.getIndex() == 10){//还本付息
			return DEFAULT_MSG_EXPIRES * 4; //两小时
		}else if(pushEnum.getIndex() == 1){//项目预告
			return DEFAULT_MSG_EXPIRES / 2;//半小时
		}else if(pushEnum.getIndex() == 11){//生日
			return DEFAULT_MSG_EXPIRES * 6;//半小时
		}
		return DEFAULT_MSG_EXPIRES;
	}
	
	public static void main(String[] args) {
		//pushMsgToSingleAndroidDevice("hello", "3477237659917596334", "123", PushEnum.CANCEL_ORDER, 1L);
	}
}
