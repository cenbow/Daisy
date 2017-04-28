package com.yourong.core.msg.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.msg.model.Feedback;
import com.yourong.core.msg.model.biz.FeedbackForMember;
import com.yourong.core.msg.model.query.FeedBackQuery;

public interface FeedbackMapper {
	
	Feedback selectByPrimaryKey(Long id);

	/**
	 * 插入用户反馈
	 * @param record
	 * @return
	 */
	int insert(Feedback record);
	
	int updateById(Feedback record);
	
	
	/**
	 * 分页查询意见反馈
	 * @param query
	 * @return
	 */
	List<FeedbackForMember> selectForPagin(@Param("map")Map<String, Object> map);
	
	/**
	 * 统计意见反馈数量
	 * @param query
	 * @return
	 */
	int selectForPaginTotalCount(@Param("map")Map<String, Object> map);

	List<Feedback> queryFeedbackList(FeedBackQuery query);

	int queryFeedbackListCount(FeedBackQuery query);
}
