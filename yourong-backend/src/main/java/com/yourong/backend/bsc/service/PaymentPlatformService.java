package com.yourong.backend.bsc.service;

import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.PaymentPlatform;

public interface PaymentPlatformService {
    
	/**
	 * 后台支付平台列表查询
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	Page<PaymentPlatform> findByPage(Page<PaymentPlatform> pageRequest, Map<String, Object> map);
	
	/**
	 * 保存
	 * @param paymentPlatform
	 * @return
	 */
	ResultDO<PaymentPlatform> save(PaymentPlatform paymentPlatform);
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	ResultDO<PaymentPlatform> delPaymentPlatform(Long id);
	
	/**
	 * 保存公告
	 * @param id
	 * @return
	 */
	ResultDO<PaymentPlatform> saveMaintence(PaymentPlatform paymentPlatform);
	
	/**
	 * 结束维护
	 * @param id
	 * @return
	 */
	ResultDO<PaymentPlatform> delMaintence(Long id);
	
	/**
	 * 刷新支付平台状态定时任务
	 */
	void flushStatus();
}