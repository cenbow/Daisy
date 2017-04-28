package com.yourong.backend.fin.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.biz.InterestSettlementBiz;

public interface InterestSettlementService {
	
	Page<InterestSettlementBiz> findByPage(Page<InterestSettlementBiz> pageRequest, Map<String, Object> map);

}
