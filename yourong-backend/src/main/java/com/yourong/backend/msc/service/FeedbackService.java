package com.yourong.backend.msc.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.core.msg.model.Feedback;
import com.yourong.core.msg.model.biz.FeedbackForMember;

public interface FeedbackService {
	public Page<FeedbackForMember> queryFeedbackByPage(Page<FeedbackForMember> page, Map<String, Object> map);
	
	public Feedback selectByPrimaryKey(Long id);
	
	public ResultDto<Object> saveFeedback(Feedback feedback);
	
	
}
