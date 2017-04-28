package com.yourong.core.mc.manager;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.mc.model.ActivityMessage;

public interface ActivityMessageManager {

	/**
	 * 
	 * @Description:插入活动留言
	 * @param activityMessage
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月30日 下午1:30:11
	 */
	public Integer insert(String actvityName, Long memberId, Long messageTemplateId) throws ManagerException;

	/**
	 * 
	 * @Description:根据活动取最新的留言记录
	 * @param activityId
	 * @param rowNum
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月30日 下午1:31:50
	 */
	public List<ActivityMessage> selectRankByActivityId(Long activityId, int rowNum) throws ManagerException;

	/**
	 * 
	 * @Description:判断用户是否留言
	 * @param activityId
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月30日 下午1:37:45
	 */
	public boolean checkMessageByActivityIdAndMemberId(Long activityId, Long memberId) throws ManagerException;

}