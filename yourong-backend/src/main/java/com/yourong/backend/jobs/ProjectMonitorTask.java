package com.yourong.backend.jobs;


import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.backend.tc.service.TransactionService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.core.common.MessageClient;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.model.HostingCollectTrade;

/**
 * 定时监控项目状态
 * @author zhanghao
 *
 */

public class ProjectMonitorTask {
	private static final Logger logger = LoggerFactory
			.getLogger(ProjectMonitorTask.class);

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private SmsMobileSend smsMobileSend;
	
	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	
	@Autowired
	private TransactionService transactionService;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static String existsFlag="1";//存在标示
	
	
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("监控项目状态定时器执行开始");
					ProjectMonitor();
					logger.info("监控项目状态定时器执行结束");
				} catch (Exception e) {
					logger.error("定时器执行出现异常", e);
				}
			}
		});
	}

	private void ProjectMonitor() throws Exception {
		//字典表获取监控结果发送手机号
		String mobiles = SysServiceUtils.getDictValue("projectmobile",  
				"projectinfor", "");
		
		mobiles = mobiles.replaceAll("；", ";");
		
		String[] projectMobileList = null;
		if (StringUtil.isNotEmpty(mobiles)) {
			projectMobileList = mobiles.split(";");
		}
		List<Long> projectIds = Lists.newArrayList();
		StringBuffer message = new StringBuffer();
		
		
		//发送手机号不为空的情况下,开始监控
		if(StringUtil.isEmpty(mobiles)){
			return;
		}
			
		List<Project>  projectList = projectManager.selectPaymentingProject();
			
			for( Project project :projectList){
				//自动根据查询到的项目，查询需要同步代收的交易订单
				List<HostingCollectTrade> hostingCollectTradeList  = hostingCollectTradeManager.selectSynchronizedHostingCollectTradesByProjectId(project.getId());
				for(HostingCollectTrade hostingCollectTrade:hostingCollectTradeList){
					//调用同步代收接口
					transactionService.synHostingCollectTrade(hostingCollectTrade.getTradeNo());
				}
				
				String key =  RedisConstant.REDIS_FIELD_PROJECT_PROJECT_PAYMENTING+ RedisConstant.REDIS_SEPERATOR
		    			+ project.getId();
				
				String flag = RedisManager.hget(key, RedisConstant.REDIS_FIELD_PROJECT_PROJECT_PAYMENTING);
				
				if(!existsFlag.equals(flag)){
					message.append(project.getName()+"("+project.getId()+")"+"当前状态：支付确认中；");
					projectIds.add(project.getId());
				}
			}
		
		if (StringUtil.isNotEmpty(mobiles)&&StringUtil.isNotEmpty(message.toString())) {
			logger.info(message.toString());
			message.append("请及时安排处理!回复td退订");
			for (int i = 0; i < projectMobileList.length; i++) {
				//发送到预设手机
				MessageClient.sendShortMessageByMobile( 		
						Long.parseLong(projectMobileList[i]), message.toString());
			}
		}
		//短信发送成功，存储到redis，并标记为1
		 for (int i = 0; i < projectIds.size(); i++) {
			 Long id =  projectIds.get(i);
			 String key =  RedisConstant.REDIS_FIELD_PROJECT_PROJECT_PAYMENTING+ RedisConstant.REDIS_SEPERATOR
		    			+ id.toString();
			 RedisManager.hset(key, RedisConstant.REDIS_FIELD_PROJECT_PROJECT_PAYMENTING, existsFlag);
			 RedisManager.expire(key, 7200);
		}
		
		
	}

}
