/**
 * 
 */
package com.yourong.backend.jobs;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import rop.thirdparty.com.google.common.collect.Maps;

import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.tc.manager.TransactionManager;

/**
 * @desc 转让项目流标
 * @author zhanghao
 * 2016年9月24日下午4:51:09
 */
public class TransferProjectFailTask {

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Autowired
	private TransactionManager transactionManager;

	private static final Logger logger = LoggerFactory.getLogger(TransferProjectFailTask.class);
	
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
					logger.info("转让项目失败 task start");
					loseTransferProject();
					logger.info("转让项目失败  task end");
			}
		});
	}
	
	private void loseTransferProject() {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("status", StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus());
			map.put("fail", 1);
			List<TransferProject> transferProjectList = transferProjectManager.queryTransferProjectListByMap(map);
			if (transferProjectList.size() <= 0) {
				logger.info("没有转让截止时间到期的转让项目");
				return ;
			}
			//调用流标处理
			for(TransferProject transferProject:transferProjectList){
				
				TransferProject tranForLock = transferProjectManager.selectByPrimaryKeyForLock(transferProject.getId());
				if(tranForLock.getStatus()!= StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()){
					continue ;
				}
				taskExecutor.execute(new TransferProjectFailThread(transferProject.getId(),2));
			}
		} catch (ManagerException e) {
			logger.error("流标转让项目异常",e);
		}
	}
	
	
	private class TransferProjectFailThread implements Runnable {
		private Long id;
		private Integer flag;
		public TransferProjectFailThread(final Long id,final Integer flag) {
			this.id = id;
			this.flag =flag;//1提前还款，2转让截止时间到期
		}
		public void run() {
			try {
				transferProjectManager.loseTransferProject(id,flag);
			} catch (ManagerException e) {
				logger.error("【转让失败】发生异常{}",id,e);
			}
		}
	}
	
}
