package com.yourong.core.repayment.manager.impl.HostingPayTradeListen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.eventbus.EventListener;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.core.repayment.manager.AfterHostingPayHandleManager;
import com.yourong.core.repayment.model.HostingPayTradeListenerObject;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.TransactionInterest;

/**
 * 代付业务完成业务处理 Created by Administrator on 2015/7/28.
 */
@Component
public class HostingPayTradeFinishedListen implements EventListener<HostingPayTradeListenerObject> {
	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private AfterHostingPayHandleManager afterHostingPayHandleManager;

	@Autowired
	private AsyncEventBus asyncEventBus;
	@Autowired
	private EventBus eventBus;

	private static Logger logger = LoggerFactory.getLogger(HostingPayTradeFinishedListen.class);

	@Override
	@Subscribe
	public void handle(HostingPayTradeListenerObject hostingPayTradeListenerObject) {
		HostingPayTrade hostingPayTrade = hostingPayTradeListenerObject.getHostingPayTrade();
		TradeItem tradeItem = hostingPayTradeListenerObject.getTradeItem();
		try {
			if (TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())) {
				// //放款业务后续处理流程
				// if (TypeEnum.HOSTING_PAY_TRADE_LOAN.getType() ==
				// hostingPayTrade.getType()) {
				// transactionManager.afterHostingPayTradeLoan(tradeItem.getProcessStatus(),
				// tradeItem.getTradeNo());
				// }
				// 还本付息业务后续处理流程
				if (TypeEnum.HOSTING_PAY_TRADE_RETURN.getType() == hostingPayTrade.getType()) {
					// 定时任务查询出没有内部订单号
					logger.info("代付号NO={},代付状态：{}", tradeItem.getTradeNo(), tradeItem.getProcessStatus());
					ResultDO<TransactionInterest> result = afterHostingPayHandleManager.afterPayInterestAndPrincipal(
							tradeItem.getProcessStatus(), tradeItem.getTradeNo(), null);
					if (result != null && result.isSuccess()) {
						afterHostingPayHandleManager.afterHostingPayTradeSucess(result.getResult());
					}
				}
			}
		} catch (Exception e) {
			logger.error("代付完成异常 tradeNo={}", tradeItem.getTradeNo(), e);
		}
	}
	
}
