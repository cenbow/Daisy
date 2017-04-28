package com.yourong.core.tc.manager;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.TradeBiz;


public interface TradeManager {

	/**
	 * 
	 * @Description:使用存钱罐余额发起代收交易
	 * @return
	 * @author: wangyanji
	 * @throws Exception
	 * @time:2016年7月27日 上午10:12:09
	 */
	public ResultDO<TradeBiz> tradeUseCapital(Order order, int sourceType, String payerIp, String returnUrl);

	/**
	 * 
	 * @Description:非委托支付获取支付链接
	 * @param order
	 * @param sourceType
	 * @param payerIp
	 * @return
	 * @author: wangyanji
	 * @time:2016年8月8日 下午3:27:55
	 */
	public ResultDO<TradeBiz> isRepeatToCashDest(Order order, String payerIp);

}