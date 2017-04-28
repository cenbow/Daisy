package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.bsc.model.PaymentPlatform;


/**
 * 支付平台限额信息service
 * @author wangyanji
 *
 */
public interface PaymentPlatformService {
	/**
	 * 查询限额及维护公告
	 */
	public ResultDO<PaymentPlatform> queryPlatformLimit(PaymentPlatform paymentPlatform);
}
