package com.yourong.backend.mc.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.mc.service.ActivityHistoryService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.manager.ActivityHistoryManager;
import com.yourong.core.mc.model.biz.ActivityHistoryBiz;
import com.yourong.core.mc.model.biz.ActivityHistoryDetail;

@Service
public class ActivityHistoryServiceImpl implements ActivityHistoryService {
	@Autowired
	ActivityHistoryManager activityHistoryManager;
	
	@Override
	public Page<ActivityHistoryBiz> findByPage(Page<ActivityHistoryBiz> pageRequest, Map<String, Object> map) {
		try {
			 activityHistoryManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return pageRequest;
	}

	@Override
	public Page<ActivityHistoryDetail> findActivityHistoryDetailPage(
			Page<ActivityHistoryDetail> pageRequest, Map<String, Object> map) {
		try {
			 activityHistoryManager.findActivityHistoryDetailPage(pageRequest, map);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return pageRequest;
	}

}
