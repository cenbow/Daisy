package com.yourong.api.service;

import com.yourong.api.dto.ResultDTO;
import com.yourong.core.bsc.model.PaymentPlatform;

public interface PaymentPlatformService {

	/**
	 * 查询所有银行限额信息
	 * @return
	 */
	public ResultDTO selectAllPaymentPlatform();
}
