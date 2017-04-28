package com.yourong.core.msg.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.Feedback;
import com.yourong.core.msg.model.biz.FeedbackForMember;
import com.yourong.core.msg.model.query.FeedBackQuery;

public interface FeedbackManager {
	
	/**
	 * 插入用户反馈信息
	 * @param feedback
	 * @return
	 * @throws ManagerException
	 */
	public int insert(Feedback feedback) throws ManagerException;
	
	public int updateById(Feedback feedback) throws ManagerException;
	
	/**
	 * 分页查询
	 * @param query
	 * @return
	 */
	public Page<FeedbackForMember> queryFeedbackByPage(Page<FeedbackForMember> page, Map<String, Object> map) throws ManagerException;


	public Feedback selectByPrimaryKey(Long id) throws ManagerException;

}
