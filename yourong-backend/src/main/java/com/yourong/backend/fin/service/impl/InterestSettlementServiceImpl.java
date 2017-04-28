package com.yourong.backend.fin.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.fin.service.InterestSettlementService;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.manager.InterestSettlementManager;
import com.yourong.core.fin.model.biz.InterestSettlementBiz;

@Service
public class InterestSettlementServiceImpl implements InterestSettlementService {

	private static final Logger logger = LoggerFactory.getLogger(InterestSettlementServiceImpl.class);
	
	@Autowired
	private InterestSettlementManager interestSettlementManager;
	@Override
	public Page<InterestSettlementBiz> findByPage(
			Page<InterestSettlementBiz> pageRequest, Map<String, Object> map) {
		try {
			return interestSettlementManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("获取线上线下利息结算失败",e);
		}
		return pageRequest;
	}

}
