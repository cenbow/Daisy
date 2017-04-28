package com.yourong.core.mc.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.ActivityHistory;
import com.yourong.core.mc.model.biz.ActivityHistoryBiz;
import com.yourong.core.mc.model.biz.ActivityHistoryDetail;

public interface ActivityHistoryManager {
	
	public void insertActivityHistory(ActivityHistory activityHistory) throws ManagerException;
	
	public boolean isParticipateInActivity(Long memberId, Long activityId) throws ManagerException;
	
	public Page<ActivityHistoryBiz> findByPage(Page<ActivityHistoryBiz> pageRequest, Map<String, Object> map) throws ManagerException;
	
	public Long totalMemberByActivityId(Long activityId) throws ManagerException;
	
	public Page<ActivityHistoryDetail> findActivityHistoryDetailPage(Page<ActivityHistoryDetail> pageRequest, Map<String, Object> map) throws ManagerException;

	public ActivityHistory getActivityHistory(Long memberId, Long activityId) throws ManagerException;

	/**
	 * 判断用户是否参加了完善信息活动
	 * @param memberId
	 * @return
	 */
	public boolean isCompletedMemberInfo(Long memberId);
	
	/**
	 * 判断用户是否参加了绑定邮箱活动
	 * @param memberId
	 * @return
	 */
	public boolean isBindEmail(Long memberId);

	/**
	 * 
	 * @Description:新手任务最新动态
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年2月26日 下午2:46:21
	 */
	public List<ActivityHistoryBiz> getNewerPrizeList(int num) throws ManagerException;
}
