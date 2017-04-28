package com.yourong.common.thirdparty.sms.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.thirdparty.sms.SmsStatus;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.HttpUtil;
import com.yourong.common.util.Identities;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.common.util.XmlUtils;

@Service
public class SmsMobileSendImpl implements SmsMobileSend {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	private final static String VOICE_SEND_SUCCESS ="1";

	@Override
	public ResultDO<Integer> sendSMS(String url ,long mobile, String smsContent) {

		long[] mobils = { mobile };
		ResultDO<Integer> bachSendSms = bachSendSms(url,mobils, smsContent);
		return bachSendSms;
	}

	@Override
	public ResultDO<Integer> sendVoice(long mobile,String code) {
		ResultDO<Integer> object = new ResultDO<Integer>();
		try {
			String url = PropertiesUtil.getProperties("voice.href.code");
			String urlmobile = String.format(url, mobile,code);
			String doGet = HttpUtil.doGet(urlmobile, null);	
			if (StringUtil.isNotBlank(doGet) && doGet.indexOf("<code>1</code>") > -1) {
				object.setSuccess(true);
			} else {
				logger.error("获取语音验证码, 手机号={}, 验证码={}, 接口返回={}", mobile, code, doGet);
				object.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("发送语音失败 mobile " + mobile, e);
			object.setResultCode(ResultCode.MOBILE_SYSTEM_SEND_VOICE_ERROR);
		}
		return object;
	}

	@Override
	public ResultDO<Integer> checkVoiceMessage(long mobile, String message) {
		ResultDO<Integer> object = new ResultDO<Integer>();
		try {
			String url = PropertiesUtil.getProperties("voice.href.zjhm");
			String urlmobile = url + mobile + "&tmp=" + message;
			String doGet = HttpUtil.doGet(urlmobile, null);
			logger.debug("语音返回结果"+doGet);
			object.setResultCode(ResultCode.getResultCodeByMessage(doGet));
		} catch (Exception e) {
			logger.error(String.format("语音验证失败 ,mobile=%l,code=%s", mobile, message),	e);
			object.setResultCode(ResultCode.MOBILE_SYSTEM_SEND_VOICE_VALIDATOR_ERROR);
		}
		return object;
	}

	/**
	 * * 1、mobiles 手机数组长度不能超过1000 2、smsContent
	 * 最多500个汉字或1000个纯英文、请客户不要自行拆分短信内容以免造成混乱
	 * 、亿美短信平台会根据实际通道自动拆分、计费以实际拆分条数为准、亿美推荐短信长度70字以内 3、 smsPriority	 * 
	 */
	@Override
	public ResultDO<Integer> bachSendSms(String url ,long[] mobils, String smsContent) {
		ResultDO<Integer> result = new ResultDO<Integer>();
		try {
			if (mobils != null && mobils.length > 0) {
				StringBuffer mobiles = new StringBuffer();
				for (int i = 0; i < mobils.length; i++) {
					if (mobils[i] > 0) {
						mobiles.append(mobils[i]);
						mobiles.append(",");
					}
				}
				if (mobiles.length() > 0) {
					String mobis = mobiles.substring(0, mobiles.length() - 1);
					String string = StringUtil.encodeUTF(smsContent);
					String sendurl = String.format(url, mobis, string);
					if(logger.isInfoEnabled()){
						logger.info(sendurl);
					}
					String doGet = HttpUtil.doGet(sendurl, null);
					logger.debug(String.format("短信发送内容:%s.短信返回结果:%s", smsContent, doGet));
					Integer valueOf = getResult(doGet.trim());
					if(valueOf != 0){
						SmsStatus byCode = SmsStatus.getSmsStatusByCode(valueOf);
						logger.error(String.format("短信发送异常 状态码:%s,%s", valueOf,byCode.getMsg()));
					}
					result.setResult(valueOf);
				}
			}
		} catch (Exception e) {
			String mobile = StringUtil.join(mobils, StringUtil.VERTICAL_BAR);
			logger.error("发送短信失败 mobils=" + mobile, e);
		}
		return result;
	}
	
	private Integer getResult(String erro) throws DocumentException{
		Map<String, Object> xmlToMap = XmlUtils.xmlToMap(erro);
		Integer result  = Integer.valueOf((String)xmlToMap.get("error"));
		return result;
	}
	


	@Override
	public ResultDO<Integer> sendSMSVerificationCode(long mobile,
			String smsContent) {
		String smstemple = PropertiesUtil.getProperties("smscontext");
		String url = PropertiesUtil.getProperties("smsurl.system");
		String format = String.format(smstemple, smsContent);		
		return sendSMS(url,mobile, format);
	}

	@Override
	public ResultDO<Integer> sendTimeSMS(long mobile, String smsContent,
			Date sendTime) {
		String href = PropertiesUtil.getProperties("smsTimeSend") ;		
		Map<String,String> map = Maps.newHashMap();
		map.put("cdkey", PropertiesUtil.getProperties("sendmail.username"));
		map.put("password", PropertiesUtil.getProperties("sendmail.password"));
		map.put("phone", String.valueOf(mobile));
		map.put("message", smsContent);
		map.put("sendtime", DateUtils.formatDatetoString(sendTime, DateUtils.TIME_PATTERN_SESSION));
		map.put("smspriority", "1");
		map.put("seqid", String.valueOf(Identities.randomLong()));	
		logger.debug("开始发送定时短信:参数"+map.toString() );
		String resultHtml = HttpUtil.doPost(href, map);	
		logger.debug("结束 发送定时短信 result"+resultHtml );
		ResultDO<Integer> result = new ResultDO<Integer>();
		Integer valueOf = 0;
		try {
			valueOf = getResult(resultHtml.trim());
		} catch (DocumentException e) {
			logger.error("解析发送定时短信异常：",e);
			result.setSuccess(false);
			return result;
		}
		if(valueOf != 0){
			SmsStatus byCode = SmsStatus.getSmsStatusByCode(valueOf);
			logger.error(String.format("短信发送异常 状态码:%s,%s", valueOf,byCode.getMsg()));
		}
		result.setResult(valueOf);
		return result;
	}

	@Override
	public ResultDO<Integer> sendMarketingSMS(long mobile, String smsContent) {
		String url = PropertiesUtil.getProperties("smsurl.marketing");
		return sendSMS(url,mobile,smsContent);
	}
	/**
	 * 
	 * @desc 查询余额
	 * @param cdkey
	 * @return
	 * @author chaisen
	 * @time 2016年4月7日 上午11:40:47
	 *
	 */
	@Override
	public ResultDO<BigDecimal> queryBalanceSMS(String cdkey) {
		String url="";
		if(TypeEnum.SMS_REMIND_FUNCTION.getDesc().equals(cdkey)){
			url = PropertiesUtil.getProperties("smsurl.querybalanceFunc");
		}else if(TypeEnum.SMS_REMIND_SALE.getDesc().equals(cdkey)){
			url = PropertiesUtil.getProperties("smsurl.querybalanceSale");
		}else if(TypeEnum.SMS_REMIND_CODE.getDesc().equals(cdkey)){
			url = PropertiesUtil.getProperties("smsurl.querybalanceCode");
		}
		ResultDO<BigDecimal> result = new ResultDO<BigDecimal>();
		Map<String,String> map = Maps.newHashMap();
		String resultHtml = HttpUtil.doPost(url, map);
		BigDecimal balance=BigDecimal.ZERO;
		try {
			Integer valueOf = getResult(resultHtml.trim());
			if(valueOf==0){
				balance=getMessage(resultHtml.trim());
				result.setResult(balance);
			}
			if(valueOf != 0){
				SmsStatus byCode = SmsStatus.getSmsStatusByCode(valueOf);
				logger.error(String.format("查询短信余额 状态码:%s,%s", valueOf,byCode.getMsg()));
			}
		} catch (DocumentException e) {
			logger.error("查询短信余额异常：",e);
			result.setSuccess(false);
			return result;
		}
		
		
		return result;
	}
	private BigDecimal getMessage(String erro) throws DocumentException{
		Map<String, Object> xmlToMap = XmlUtils.xmlToMap(erro);
		BigDecimal balance=BigDecimal.ZERO;
		balance=new BigDecimal(xmlToMap.get("message").toString());  
		return balance;
	}
}
