package com.yourong.web.service;

import com.yourong.core.tc.model.HostingCollectTrade;

/**
 * 
 * @desc 代收交易service
 * @author wangyanji 2016年5月25日下午10:24:35
 */
public interface HostingCollectTradeService {

	/**
	 * 
	 * @Description:代收完成/撤销成功的业务处理
	 * @param tradeStatus
	 * @param collectTrade
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月25日 下午10:23:28
	 */
	public String handlePreAuthTrade(String notifyTradeStatus, HostingCollectTrade collectTrade);
}
