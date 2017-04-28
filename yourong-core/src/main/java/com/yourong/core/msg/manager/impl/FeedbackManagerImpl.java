package com.yourong.core.msg.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.core.msg.dao.FeedbackMapper;
import com.yourong.core.msg.manager.FeedbackManager;
import com.yourong.core.msg.model.Feedback;
import com.yourong.core.msg.model.biz.FeedbackForMember;

@Component
public class FeedbackManagerImpl implements FeedbackManager {
	
	@Autowired
	private FeedbackMapper feedbackMapper;

	@Override
	public int insert(Feedback feedback) throws ManagerException {
		return feedbackMapper.insert(feedback);
	}
	
	@Override
	public int updateById(Feedback feedback) throws ManagerException {
		return feedbackMapper.updateById(feedback);
	}

	@Override
	public Page<FeedbackForMember> queryFeedbackByPage(Page<FeedbackForMember> page, Map<String, Object> map) throws ManagerException {
		try{
			map.put("startRow", page.getiDisplayStart());
			map.put("pageSize", page.getiDisplayLength());
			List<FeedbackForMember> feedbackList = Lists.newArrayList();
			int count = feedbackMapper.selectForPaginTotalCount(map);
			if(count > 0){
				feedbackList = feedbackMapper.selectForPagin(map);
			}
			page.setData(feedbackList);
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return page;
	}
	
	@Override
	public Feedback selectByPrimaryKey(Long id) throws ManagerException {
		return feedbackMapper.selectByPrimaryKey(id);
	}


	
	
	
	

}
