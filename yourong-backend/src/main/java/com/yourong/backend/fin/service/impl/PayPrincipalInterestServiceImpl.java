package com.yourong.backend.fin.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.fin.service.PayPrincipalInterestService;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.manager.PayPrincipalInterestManager;
import com.yourong.core.fin.model.biz.PayPrincipalInterestBiz;

@Service
public class PayPrincipalInterestServiceImpl implements
		PayPrincipalInterestService {
	private static final Logger logger = LoggerFactory.getLogger(PayPrincipalInterestServiceImpl.class);
	@Autowired
	private PayPrincipalInterestManager payPrincipalInterestManager;
	
	@Override
	public Page<PayPrincipalInterestBiz> findByPage(
			Page<PayPrincipalInterestBiz> pageRequest, Map<String, Object> map) {
		try {
			return payPrincipalInterestManager.findByPage(pageRequest, map); 
		} catch (Exception e) {
			logger.error("获取还本付息列表失败",e);
		}
		return null;
	}

	
	@Override
	public PayPrincipalInterestBiz findTotalPrincipalAndInterestByStatus(
			Map<String, Object> map) {
		try {
			return payPrincipalInterestManager.findTotalPrincipalAndInterestByStatus(map); 
		} catch (Exception e) {
			logger.error("还款本息数据统计失败，map"+map,e);
		}
		return null;
	}

}
