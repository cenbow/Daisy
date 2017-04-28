package com.yourong.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.bsc.manager.PaymentPlatformManager;
import com.yourong.core.bsc.model.PaymentPlatform;
import com.yourong.web.service.PaymentPlatformService;

@Service
public class PaymentPlatformServiceImpl implements PaymentPlatformService {
	
	private static Logger logger = LoggerFactory.getLogger(PaymentPlatformServiceImpl.class);
	
	@Autowired
	private PaymentPlatformManager paymentPlatformManager;

	@Override
	public ResultDO<PaymentPlatform> queryPlatformLimit(PaymentPlatform paymentPlatform) {
		try {
			return paymentPlatformManager.queryPlatformLimit(paymentPlatform);
		} catch(ManagerException e) {
			try {
				logger.error("查询限额及维护公告异常, bankCode={}, typeLimt={}, platformType", paymentPlatform.getCode(), paymentPlatform.getTypeLimit(), paymentPlatform.getPlatformType(), e);				
			} catch(Exception ex) {
				logger.error("查询限额及维护公告异常, 缺少接口入参", ex);
			}
		}
		return null;
	}

}
