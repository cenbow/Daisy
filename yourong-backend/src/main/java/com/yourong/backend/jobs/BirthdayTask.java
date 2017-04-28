package com.yourong.backend.jobs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.common.PushClient;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.push.PushEnum;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

public class BirthdayTask {

	private static final Logger logger = LoggerFactory.getLogger(BirthdayTask.class);
	
	@Autowired
	private MemberManager memberManager;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Activity activity = memberManager.getBirthdayActivity();
					if (activity != null && activity.getActivityStatus().intValue() == 4) {
						if (DateUtils.getCurrentDate().after(activity.getStartTime())) {
							birthday();
						}
					}
				} catch (Exception e) {
					logger.error("查询今天生日的用户定时器执行出现异常", e);
				}
			}
		});
	}
	
	private void birthday() throws ManagerException{
		logger.info("start 查询今天生日的用户");
		List<Member> memberList = memberManager.selectTodayBirthdayMember();
		if(Collections3.isNotEmpty(memberList)){
			for(Member member : memberList){
				String sex = "";
				
				if(StringUtil.isNotBlank(member.getIdentityNumber())){
									String num = member.getIdentityNumber().substring(member.getIdentityNumber().length()-2, member.getIdentityNumber().length()-1);
									Integer n = Integer.valueOf(num);
									if(n%2==0){
										sex = "女士";
									}else{
										sex = "先生";
									}
				}else{
					sex = "先生（女士）";
				}
				
				MessageClient.sendMsgForCommon(member.getId(),Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.BIRTHDAY.getCode(),
						member.getTrueName(),sex);
				//生日推送取消，bychanpin  yang
				//PushClient.pushMsgToMember("祝您生日快乐！今天签到享有10倍人气值，更有超值礼包在呼唤哦！", member.getId(), "", PushEnum.BIRTHDAY);
				//MessageClient.sendMsgForBirthday(member.getId(), member.getTrueName());
			}
		}
		logger.info("end 查询今天生日的用户");
	}
}
