package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.tc.model.HostingCollectTrade;



public interface RepaymentService {
	/**
	 * 还本付息代收回调后业务处理逻辑
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 */
	public ResultDO<HostingCollectTrade> afterHostingCollectTradeForRepayment(
			String tradeNo, String outTradeNo, String tradeStatus) throws Exception;


}
