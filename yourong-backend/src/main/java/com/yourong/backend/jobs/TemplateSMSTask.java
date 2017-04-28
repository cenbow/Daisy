package com.yourong.backend.jobs;

import com.yourong.common.constant.Constant;
import com.yourong.common.enums.MessageEnum;
import com.yourong.core.common.MessageClient;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplateSMSManager;
import com.yourong.core.mc.model.CouponTemplateSMS;
import com.yourong.core.mc.model.biz.CouponTemplateSMSBiz;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by XR on 2016/9/22.
 */
public class TemplateSMSTask {
    @Autowired
    private CouponManager couponManager;
    @Autowired
    private CouponTemplateSMSManager couponTemplateSMSManager;
    public void run(){
        List<CouponTemplateSMS> list= couponTemplateSMSManager.queryAllValid();
        for (CouponTemplateSMS sms:list) {
            Date date=new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE,sms.getValidDays());//把日期往后增加
            date=calendar.getTime();   //这个时间就是日期往后推一天的结果
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            simpleDateFormat.format(date);
            List<CouponTemplateSMSBiz> smsbiz=new ArrayList<CouponTemplateSMSBiz>();
            try {
                smsbiz= couponManager.queryCouponMemberMobileByTemplate(sms.getTemplateId(),simpleDateFormat.parse(simpleDateFormat.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (CouponTemplateSMSBiz biz:smsbiz) {
                if (biz.getNum()>0){
                    MessageClient.sendMsgForCommon(biz.getMemberid(), Constant.MSG_TEMPLATE_TYPE_PHONE, MessageEnum.CONTRACT_COUPON_EXPIRRE.getCode(),biz.getNum().toString(),biz.getTopic(),sms.getValidDays().toString());
                    
                  //优惠券即将过期  app信息提醒
                    MessageClient.sendMsgForCommon(biz.getMemberid(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_COUPON_TIME_OUT.getCode(), 
                    		biz.getNum().toString(),biz.getTopic(),sms.getValidDays().toString());
                }
            }
        }

    }
}
