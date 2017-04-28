package com.yourong.backend.bsc.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.bsc.service.PaymentPlatformService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.manager.PaymentPlatformManager;
import com.yourong.core.bsc.model.PaymentPlatform;

@Service
public class PaymentPlatformServiceImpl implements PaymentPlatformService {

	private static Logger logger = LoggerFactory.getLogger(PaymentPlatformServiceImpl.class);
	
	@Autowired
	private PaymentPlatformManager paymentPlatformManager;
	
	@Override
	public Page<PaymentPlatform> findByPage(Page<PaymentPlatform> pageRequest, Map<String, Object> map) {
		try {
			return paymentPlatformManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("后台支付平台列表查询异常", e);
		}
		return null;
	}

	@Override
	public ResultDO<PaymentPlatform> save(PaymentPlatform paymentPlatform) {
		ResultDO<PaymentPlatform> resultDO = new ResultDO<PaymentPlatform>();
		resultDO.setSuccess(false);
		try {
			return paymentPlatformManager.save(paymentPlatform);
		} catch (ManagerException e) {
			logger.error("后台支付平台保存异常", e);
		}
		return resultDO;
	}

	@Override
	public ResultDO<PaymentPlatform> delPaymentPlatform(Long id) {
		ResultDO<PaymentPlatform> resultDO = new ResultDO<PaymentPlatform>();
		resultDO.setSuccess(false);
		try {
			return paymentPlatformManager.delPaymentPlatform(id);
		} catch (ManagerException e) {
			logger.error("后台支付平台删除异常", e);
		}
		return resultDO;
	}

	@Override
	public ResultDO<PaymentPlatform> saveMaintence(PaymentPlatform paymentPlatform) {
		ResultDO<PaymentPlatform> resultDO = new ResultDO<PaymentPlatform>();
		resultDO.setSuccess(false);
		try {
			return paymentPlatformManager.saveMaintence(paymentPlatform);
		} catch (ManagerException e) {
			logger.error("后台保存维护公告异常", e);
		}
		return resultDO;
	}

	@Override
	public ResultDO<PaymentPlatform> delMaintence(Long id) {
		ResultDO<PaymentPlatform> resultDO = new ResultDO<PaymentPlatform>();
		resultDO.setSuccess(false);
		try {
			return paymentPlatformManager.delMaintence(id);
		} catch (ManagerException e) {
			logger.error("后台结束维护异常", e);
		}
		return resultDO;
	}

	@Override
	public void flushStatus() {
		try {
			paymentPlatformManager.flushStatus();
		} catch (ManagerException e) {
			logger.error("刷新支付平台状态定时任务异常", e);
		}
	}
    
}