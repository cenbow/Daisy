package com.yourong.backend.msc.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.msc.service.FeedbackService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.util.DateUtils;
import com.yourong.core.msg.manager.FeedbackManager;
import com.yourong.core.msg.model.Feedback;
import com.yourong.core.msg.model.biz.FeedbackForMember;

@Service
public class FeedbackServiceImpl implements FeedbackService {
	private static Logger logger = LoggerFactory
			.getLogger(FeedbackServiceImpl.class);
	@Autowired
	private FeedbackManager feedbackManager;

	@Override
	public Page<FeedbackForMember> queryFeedbackByPage(
			Page<FeedbackForMember> page, Map<String, Object> map) {
		try {
			return feedbackManager.queryFeedbackByPage(page, map);
		} catch (ManagerException e) {
			logger.error("分页查询意见反馈异常", e);
		}
		return page;
	}


	@Override
	public Feedback selectByPrimaryKey(Long id) {
		try {
			return feedbackManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("根据ID查询反馈信息，id={}",id, e);
		}
		return null;
	}
	
	@Override
	public ResultDto<Object> saveFeedback(Feedback feedback) {
		ResultDto<Object> result = new ResultDto<>();
		try {

			Feedback feed = feedbackManager.selectByPrimaryKey(feedback.getId());
			if(feed==null){
				result.setSuccess(false);
				return result;
			}
			Feedback feedbackFroUpdate = new Feedback();
			feedbackFroUpdate.setReply(feedback.getReply());
			feedbackFroUpdate.setId(feedback.getId());
			feedbackFroUpdate.setReplyStatus(1);
			feedbackFroUpdate.setReplyTime(DateUtils.getCurrentDate());
			
			feedbackManager.updateById(feedbackFroUpdate);
			
			result.setSuccess(true);
		} catch (ManagerException e) {
			logger.error("保存反馈信息，feedback={}",feedback, e);
			result.setSuccess(false);
		}
		return result;
	}
	

	
}
