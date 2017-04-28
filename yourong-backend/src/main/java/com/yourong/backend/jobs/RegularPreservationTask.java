package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.backend.ic.service.LvgouService;
import com.yourong.backend.tc.service.PreservationService;
import com.yourong.common.exception.ManagerException;

/**
 * 定时创建保全
 * 
 * @author wangyanji
 */
public class RegularPreservationTask {

	@Resource
	private PreservationService preservationService;

	@Resource
	private LvgouService lvgouService;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory
			.getLogger(RegularPreservationTask.class);

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("定时创建保全 start");
					createPreservationTask();
					logger.info("定时创建保全 end");
				} catch (Exception e) {
					logger.error("定时创建保全异常：", e);
				}
			}
		});
	}

	/**
	 * 定时创建保全
	 * 
	 * @author wangyanji
	 * @throws ManagerException
	 */
	private void createPreservationTask() throws Exception {
		int createNum = preservationService.createPreservationTask();
		logger.info("定时创建保全数量{}", createNum);
		int lvgouNum = lvgouService.createLvgouContractTask();
		logger.info("定时创建绿狗项目合同数量{}", lvgouNum);
	}

}
