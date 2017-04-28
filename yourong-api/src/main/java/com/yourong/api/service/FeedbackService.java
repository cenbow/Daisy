package com.yourong.api.service;

import com.yourong.api.dto.ResultDTO;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.Feedback;
import com.yourong.core.msg.model.biz.FeedbackForMember;
import com.yourong.core.msg.model.query.FeedBackQuery;

public interface FeedbackService {

	/**
	 * 用户反馈
	 * @param feedback
	 * @return
	 */
	public ResultDTO addFeedback(Feedback feedback);
	/**
	 * 
	 * @Description:查询意见反馈
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年9月23日 上午10:52:46
	 */
	public Page<Feedback> queryFeedbackList(FeedBackQuery query);
}
