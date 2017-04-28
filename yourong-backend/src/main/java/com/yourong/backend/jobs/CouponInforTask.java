package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.base.Optional;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.core.common.MessageClient;
import com.yourong.core.mc.manager.CouponTemplatePrintManager;
import com.yourong.core.mc.model.CouponTemplatePrint;

/**
 * 定时监控优惠券数量
 * @author zhanghao
 *
 */

public class CouponInforTask {
	private static final Logger logger = LoggerFactory
			.getLogger(CouponInforTask.class);

	@Autowired
	private CouponTemplatePrintManager CouponTemplatePrintManager;

	@Autowired
	private SmsMobileSend smsMobileSend;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("监控优惠券定时器执行开始");
					couponInfor();
					logger.info("监控优惠券定时器执行结束");
				} catch (Exception e) {
					logger.error("定时器执行出现异常", e);
				}
			}
		});
	}

	private void couponInfor() throws ManagerException {
		//字典表获取监控结果发送手机号
		String mobiles = SysServiceUtils.getDictValue("couponmobile",  
				"couponinfor", "");
		
		mobiles = mobiles.replaceAll("；", ";");
		
		String[] CouponMobileList = null;
		if (StringUtil.isNotEmpty(mobiles)) {
			CouponMobileList = mobiles.split(";");
		}
		//字典表获取监控条件，格式为 模板ID-监控数量
		String templateids = SysServiceUtils.getDictValue("coupontemplateid",
				"couponinfor", "");
		
		templateids = templateids.replaceAll("；", ";");
		
		String[] CouponTemplateIdList = null;
		if (StringUtil.isNotEmpty(templateids)) {
			CouponTemplateIdList = templateids.split(";");
		}
		StringBuffer templateIds = new StringBuffer();
		StringBuffer templateIdsNull = new StringBuffer();
		//监控模板和发送手机号不为空的情况下，开始遍历
		if(StringUtil.isNotEmpty(templateids)&&StringUtil.isNotEmpty(mobiles) ){
		
			for (int i = 0; i < CouponTemplateIdList.length; i++) {

				String index= CouponTemplateIdList[i];
				//获取模板ID
				String TemplateID = index.substring(0,index.indexOf("-")); 
				//获取监控数量
				String Num = index.substring(index.indexOf("-") + 1); 
				//查询对应模板的优惠券剩余数量
				CouponTemplatePrint couponTemplatePrint = CouponTemplatePrintManager 
						.selectInforByTemplateId(Long.parseLong(TemplateID));
				//getPrintNum对应剩余数量详见SQL
				
				if(Optional.fromNullable(couponTemplatePrint).isPresent()){
					if (couponTemplatePrint.getPrintNum() < Integer.parseInt(Num)) {
						//拼接低于警戒数量的优惠券模板ID
						templateIds.append(TemplateID +","); 
					}
				}else{
					//若模板ID不存在
					templateIdsNull.append(TemplateID +","); 
				}
			}
			
		}
		
		if (StringUtil.isNotEmpty(mobiles)&&StringUtil.isNotEmpty(templateIds.toString())) {
			logger.info("监控优惠券数量定时任务，优惠券模板ID"+templateIds.toString() +"不存在的模板ID"+templateIdsNull.toString()+ "发送短信开始");
			String message =" 优惠券模板ID: "+ templateIds.toString() + "低于预设警戒数量;优惠券模板ID: "+ templateIdsNull.toString() + "不存在，请相关人员注意!，回复TD退订。";
			for (int i = 0; i < CouponMobileList.length; i++) {
				//发送到预设手机
				MessageClient.sendShortMessageByMobile( 		
						Long.parseLong(CouponMobileList[i]), message);
			}
		}
	}

}
