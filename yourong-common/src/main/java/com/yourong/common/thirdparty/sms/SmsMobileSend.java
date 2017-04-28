package com.yourong.common.thirdparty.sms;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.ResultDO;

/**
 * 手机发送短信，语音接口 
 * @author pengyong
 *
 */
public interface SmsMobileSend {
   /**
    * 发送短信  
    * @param mobile   手机号码
    * @param smsContent 短信内容
    * @param
    * @return
    */
   public  ResultDO<Integer>  sendSMS(String url ,long mobile ,String smsContent);

   /**
    * 发送营销短信接口
    * @param mobile
    * @param smsContent
    * @return
    */
   public  ResultDO<Integer>  sendMarketingSMS(long mobile ,String smsContent);
   /**
    * 定时发送短信
    * @param mobile
    * @param smsContent
    * @return
    * author: pengyong
    * 上午11:37:45
    */
   public  ResultDO<Integer>  sendTimeSMS(long mobile ,String smsContent,Date sendTime); 

   
   /**
    * 发送短信验证码
    * @param mobile   手机号码
    * @param smsContent 随机的验证码
    *  优先级 默认 5
    * @return
    */
   public  ResultDO<Integer>  sendSMSVerificationCode(long mobile ,String smsContent);  
   
    /**
     * 发送语音信息
     * @param mobile
     * @return
     */
   public ResultDO<Integer>  sendVoice(long mobile,String code);
   
   /***
    * 验证语音短信
    * @param message
    * @return
    */
   @Deprecated
   public ResultDO<Integer> checkVoiceMessage(long mobile,String message);
   
   /** **
    *  批量发送短信
    * @param mobils
    * @return
    */
   public  ResultDO<Integer>  bachSendSms(String url,long[] mobils,String smsContent);

   public ResultDO<BigDecimal> queryBalanceSMS(String cdkey);
   
   
}
