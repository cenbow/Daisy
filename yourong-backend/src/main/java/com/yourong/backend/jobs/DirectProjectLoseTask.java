package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.manager.ProjectManager;

/**
 * @desc 直投项目流标创建退款定时任务
 * @author fuyili 2016年2月18日上午9:33:02
 */
public class DirectProjectLoseTask {

	private static final Logger logger = LoggerFactory.getLogger(DirectProjectLoseTask.class);

	@Autowired	
	private ProjectManager projectManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("直投项目流标创建退款定时任务 start");
					// directProjectLoseTask();
					directProjectAutoLoseTask();
					logger.info("直投项目流标创建退款定时任务 end");
				} catch (Exception e) {
					logger.error("流标定时任务异常：", e);
				}
			}
		});

	}

	/**
	 * @Description:直投项目流标创建退款定时任务
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月18日 上午10:58:44
	 */
	/*private void directProjectLoseTask() throws ManagerException {
		projectManager.loseProject(null);
	}
*/
	/**
	 * @Description:定时任务更新项目为流标状态
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月18日 上午10:58:55
	 */
	private void directProjectAutoLoseTask() throws ManagerException {
		int num = projectManager.autoLoseProject();
		logger.info("更新项目为流标状态，num={}", num);
	}

}
