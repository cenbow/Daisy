package com.yourong.backend.jobs;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Maps;
import com.yourong.backend.ic.service.DirectProjectLotteryRuleService;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.common.PushClient;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.ProjectNoticeManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.ProjectNotice;
import com.yourong.core.msg.model.CustomMessage;
import com.yourong.core.push.PushEnum;
import com.yourong.core.tc.dao.TransactionMapper;


/**
 * 项目相关定时任务
 * @author Administrator
 *
 */
public class ProjectAutoStartTask {
	
	@Resource
	private ProjectManager projectManager;

	@Resource
	private ProjectNoticeManager projectNoticeManager;
	
	@Resource
	private DirectProjectLotteryRuleService directProjectLotteryRuleService;
	
	@Resource
	private ProjectExtraManager projectExtraManager;
	
	@Resource
	private TransactionMapper transactionMapper;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired
	private TaskExecutor threadPool;
	
	/** 每次查询数量 **/
	static final int numConstant = 1000;

	static final int startConstant = 0;

	
	private static final Logger logger = LoggerFactory.getLogger(ProjectAutoStartTask.class);
	
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("项目task start");
					investingProjectTask();
					endSaleProjectTask();
					autoStartProjectNoticeTask();
					autoEndProjectNoticeTask();
					autoCancelRecommendProject();
					logger.info("项目task end");
				} catch (Exception e) {
					logger.error("项目相关定时任务执行异常：", e);
				}
			}
		});
	}
	
	/**
	 * 发布投资项目
	 * @throws ManagerException
	 */
	private void investingProjectTask() throws ManagerException{
 		List<Project> projectList = projectManager.findUpcomingProject();
		int releaseNum = projectManager.investingProjectTask();
		for(Project p : projectList){
			int days = DateUtils.getIntervalDays(p.getStartDate(), p.getEndDate())+1-p.getInterestFrom();
			//上线是否短信通知
			if(p.getOnlineNotice()==StatusEnum.PROJECT_ONLINE_NOTICE.getStatus()){
//				SendMsgClient.sendProjectOnlineMsg(p.getName(), days, p.getTotalAmount());
			}
			//直投满标悬赏活动预处理
			directProjectLotteryRuleService.catalyticActivity(p);
			
		}
		logger.info("定时把项目状态置为已投资数量："+releaseNum);
	}
	
	public void autoEndProjectNoticeTask() throws ManagerException{
		int num = projectNoticeManager.autoEndProjectNoticeTask();
		logger.info("定时结束项目预告："+num);
	}
	
	public void autoStartProjectNoticeTask() throws ManagerException{
		List<ProjectNotice> projectList = projectNoticeManager.findUpcomingProjectNotice();
		int num = projectNoticeManager.autoStartProjectNoticeTask();
		for(ProjectNotice pt : projectList){
			Project project = projectManager.selectByPrimaryKey(pt.getProjectId());
			//项目上线 推送取消  by chanpin yang
			/*PushClient.pushMsgToAllMember(
					getPrefixProjectName(pt.getProjectName()) + ",将在"
							+ DateUtils.formatDatetoString(project.getOnlineTime(), "yyyy-MM-dd HH:mm") + "上线", project.getId().toString(),
					PushEnum.PROJECT_NOTICE);*/
			//预告是否短信通知
			if(pt.getNoticeNotice()==StatusEnum.PROJECT_NOTICE_NOTICE.getStatus()){
//				SendMsgClient.sendProjectNoticeMsg(pt.getProjectName(), days, project.getTotalAmount(), pt.getOnlineTime());
				ProjectExtra projectExtra = projectExtraManager.getProjectQucikReward(pt.getProjectId());
				if(projectExtra != null&&projectExtra.getActivitySign()!=null){
					if (projectExtra.getActivitySign() == TypeEnum.PROJECT_ACTIVITY_CATALYZER
							.getType()) {
						
						Map<String, Object> map = Maps.newHashMap();
						int startNum = startConstant;
						int queryNum = numConstant;
						int size = numConstant;
						for (int i = 0; size > 0; i++) {
							map.put("startNum", startNum + i * queryNum);
							map.put("num", queryNum);
							List<Long> memberList = transactionMapper
									.selectMemberThreeMonth(map);
							size = memberList.size();
							threadPool.execute(new ShortMessageNoticeByMemberThread(memberList));
						}
						return;
					}
				}
				MessageClient.sendMsgForProjectNotice(project);
			}


		}
		logger.info("定时开始项目预告："+num);
	}
	
	public void autoCancelRecommendProject() throws ManagerException{
		int num = projectManager.cancelRecommendByNotInvestingState();
		logger.info("定时取消非投资状态的推荐项目："+num);
	}
	
	/**
	 * 已截止项目
	 * @throws ManagerException
	 */
	private void endSaleProjectTask() throws ManagerException{
		int num = projectManager.endSaleProjectTask();
		logger.info("定时把项目状态置为已截止数量："+num);
	}
	
	public String getPrefixProjectName(String name) {
		if (name.contains("期")) {
			return name.substring(0, name.indexOf("期") + 1);
		} else {
			return name;
		}
	}
	
	private class ShortMessageNoticeByMemberThread implements Runnable {


		private List<Long> memberList;

		public ShortMessageNoticeByMemberThread(List<Long> memberList) {
			this.memberList = memberList;
		}

		@Override
		public void run() {
			ShortMessageNoticeByMemberThread();
		}

		public void ShortMessageNoticeByMemberThread() {
			
			for(Long memberId : memberList){
				//快投项目上线，短信通知投资者
				MessageClient.sendMsgForCommon(memberId, Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.QUICK_REWARD_NOTICE.getCode() 
						);

			}
		}

	}
}
