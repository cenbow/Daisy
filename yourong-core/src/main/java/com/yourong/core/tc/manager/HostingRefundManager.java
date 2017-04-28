package com.yourong.core.tc.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.RefundTradeResult;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingRefund;

public interface HostingRefundManager {
	
	public HostingRefund selectByTradeNo(String tradeNo) throws ManagerException;

	/**
	 * 
	 * @Description:根据代收发起退款
	 * @param collectTrade
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年8月1日 下午2:21:55
	 */
	public ResultDto<RefundTradeResult> refundByTradeNo(HostingCollectTrade collectTrade, String summary) throws ManagerException;

	/**
	 * 根据订单id查询退款状态
	 * @param orderid
	 * @return
     */
	public String queryStatusByOrderId(String orderid);
	
}
