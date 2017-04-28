package com.yourong.api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rop.thirdparty.com.google.common.collect.Lists;

import com.alibaba.fastjson.JSONObject;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.TransactionForMemberDto;
import com.yourong.api.service.FeedbackService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.model.Project;
import com.yourong.core.msg.dao.FeedbackMapper;
import com.yourong.core.msg.manager.FeedbackManager;
import com.yourong.core.msg.model.Feedback;
import com.yourong.core.msg.model.biz.FeedbackForMember;
import com.yourong.core.msg.model.query.FeedBackQuery;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;

@Service
public class FeedbackServiceImpl implements FeedbackService {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FeedbackManager feedbackManager;
	
	@Autowired
	private FeedbackMapper feedbackMapper;
	
	@Override
	public ResultDTO addFeedback(Feedback feedback) {
		ResultDTO result = new ResultDTO();
		try {
			String content = feedback.getContent();
			if(StringUtil.isBlank(content)){
				result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return result;
			}else if(content.length() < 10){
				result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_TOO_SHORT);
				return result;
			}else if(content.length() > 200){
				result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_TOO_LONG);
				return result;
			}
			feedback.setContent(StringUtil.filterHtml(content));
			feedbackManager.insert(feedback);
			result.setIsSuccess();
		} catch (ManagerException e) {
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}

	@Override
	public Page<Feedback> queryFeedbackList(FeedBackQuery query) {
		Page<Feedback> dto = new Page<Feedback>();
		try {
			List<Feedback> feedbackForMember = feedbackMapper.queryFeedbackList(query);
			int count = feedbackMapper.queryFeedbackListCount(query);
			dto.setData(feedbackForMember);
			dto.setiDisplayLength(query.getPageSize());
			dto.setiTotalRecords(count);
			dto.setiTotalDisplayRecords(count);
			dto.setPageNo(query.getCurrentPage());
		} catch (Exception e) {
			logger.error("查询我的反馈列表失败，memberId={}", query.getMemberId(), query, e);
		}
		return dto;
	}

	

}
