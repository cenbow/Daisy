/**
 * 
 */
package com.yourong.backend.jobs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.biz.CouponTemplateSMSBiz;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年11月28日下午3:35:37
 */
public class CouponExpirationReminderTask {
	@Autowired
    private CouponManager couponManager;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	
	private static final Logger logger = LoggerFactory
			.getLogger(CouponExpirationReminderTask.class);
	
    public void run(){
    	
    	taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("发送消息 task start");
					work();
					logger.info("发送消息  task end");
				} catch (Exception e) {
					logger.error("发送消息定时任务执行异常：", e);
				}
			}
		});
       
    }
    
    private void work(){
    		//优惠券到期提醒站内信，预设天数
    		String day =SysServiceUtils.getDictValue("couponExpirationReminder",  
				"couponExpirationReminder", ""); 
    		if (StringUtil.isBlank(day) ) {
        		logger.info("【优惠券到期提醒app站内信】未上线！");
	    		return;
        	}
             Date date=new Date();
             Calendar calendar = new GregorianCalendar();
             calendar.setTime(date);
             calendar.add(calendar.DATE,Integer.valueOf(day));//把日期往后增加
             date=calendar.getTime();   //这个时间就是日期往后推一天的结果
             SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
             simpleDateFormat.format(date);
             List<CouponTemplateSMSBiz> smsbiz=new ArrayList<CouponTemplateSMSBiz>();
             try {
                 smsbiz= couponManager.queryCouponExpirationReminderMobile(simpleDateFormat.parse(simpleDateFormat.format(date)));
             } catch (ParseException e) {
                 e.printStackTrace();
             }
             for (CouponTemplateSMSBiz biz:smsbiz) {
                 if (biz.getNum()>0){
                   //优惠券即将过期  app信息提醒
                	 if(biz.getCoupontType()==null){
                		 continue;
                	 }
                	 String name ="";
                	 if(biz.getCoupontType()==1){//现金券
                		 name = biz.getAmount()+"元现金券";
                	 }else{
                		 name = biz.getAmount()+"%收益券";
                	 }
                     MessageClient.sendMsgForCommon(biz.getMemberid(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_COUPON_TIME_OUT.getCode(), 
                     		biz.getNum().toString(),name);
                 }
             }
    }
}
