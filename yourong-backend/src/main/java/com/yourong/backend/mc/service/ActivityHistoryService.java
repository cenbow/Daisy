package com.yourong.backend.mc.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.biz.ActivityHistoryBiz;
import com.yourong.core.mc.model.biz.ActivityHistoryDetail;

public interface ActivityHistoryService {
	public Page<ActivityHistoryBiz> findByPage(Page<ActivityHistoryBiz> pageRequest, Map<String, Object> map);
	
	public Page<ActivityHistoryDetail> findActivityHistoryDetailPage(Page<ActivityHistoryDetail> pageRequest, Map<String, Object> map);
}
