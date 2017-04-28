package com.yourong.backend.jobs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;


/**
 * 定时还款项目
 * @author Administrator
 *
 */
public class ProjectAutoRepaymentTask {
	
	@Resource
	private ProjectManager projectManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory.getLogger(ProjectAutoRepaymentTask.class);
	
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("定时还款项目 start");
					repaymentProjectAddAvailableCredit();
					// repaymentProjectTask();
					logger.info("定时还款项目 end");
				} catch (Exception e) {
					logger.error("定时把项目状态置为已还款异常", e);
				}
			}
		});
	}
	
	/**
	 * 已还款项目
	 * @throws ManagerException
	 */
	/*private void repaymentProjectTask() throws ManagerException{
		int num = projectManager.repaymentProjectTask();
		logger.info("定时把债权项目状态置为已还款数量："+num);
		 num = projectManager.repaymentDirectProjectTask();
		logger.info("定时把直投项目状态置为已还款数量："+num);


	}*/
	
	/**
	 * 已还款项目
	 * @throws ManagerException
	 */
	private void repaymentProjectAddAvailableCredit() throws ManagerException{
		List<Project> projects = projectManager.repaymentProjectAddAvailableCredit();
		if (Collections3.isEmpty(projects)) {
			logger.info("还款之后，把企业的可用余额加上，projectIds=空");
			return;
		}
		StringBuffer proIds = new StringBuffer();
		for (Project pro : projects) {
			proIds.append(pro.getId()).append(",");
		}
		String ids = proIds.toString();
		if (StringUtil.isNotBlank(ids) && ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		logger.info("还款之后，把企业的可用余额加上，projectIds={}", ids);
	}
}
