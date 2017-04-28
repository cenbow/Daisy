package com.yourong.core.tc.manager;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.TradeBiz;


public interface TradeProcManager {

	/**
	 * 
	 * @Description:根据本地代收记录创建代收
	 * @param order
	 * @param sourceType
	 * @param payerIp
	 * @param isWithholdAuthority
	 * @param returnUrl
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年8月10日 上午11:16:27
	 */
	public ResultDO<TradeBiz> createSinpayHostingCollectTradeAfterLocalSuccess(Order order, int sourceType, String payerIp,
			boolean isWithholdAuthority, String returnUrl) throws Exception;

	public ResultDO<TradeBiz> createTransactionHostingTradeLocal(Order order, int sourceType, String payerIp, boolean isWithholdAuthority)
			throws Exception;
	
	public ResultDO<?> createTransactionPreValidate(Order order, int sourceType, ResultDO<?> result, boolean isWithholdAuthority)
			throws Exception;

	public HostingCollectTrade insertHostingCollectTrade(Order order, int collectType, String payerIp, boolean isWithholdAuthority)
			throws Exception;
}