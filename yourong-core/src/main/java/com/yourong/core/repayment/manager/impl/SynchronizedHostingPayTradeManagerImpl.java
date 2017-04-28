package com.yourong.core.repayment.manager.impl;

import com.google.common.eventbus.EventBus;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.ErrorCode;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.repayment.manager.AfterHostingPayHandleManager;
import com.yourong.core.repayment.manager.SynchronizedHostingPayTradeManager;
import com.yourong.core.repayment.model.HostingPayTradeListenerObject;
import com.yourong.core.tc.manager.*;
import com.yourong.core.tc.model.HostingPayTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 同步代付
 * Created by py on 2015/7/24.
 */
@Component
public class SynchronizedHostingPayTradeManagerImpl  implements SynchronizedHostingPayTradeManager {
    @Autowired
    private HostingPayTradeManager hostingPayTradeManager;
    @Resource
    private SinaPayClient sinaPayClient;
    @Autowired
    private EventBus eventBus;

    private  Logger logger = LoggerFactory.getLogger(SynchronizedHostingPayTradeManagerImpl.class);
    @Override
    public synchronized void synchronizedHostingPayTrade() {
        // 查询需要同步的代付交易
        try {
            List<HostingPayTrade> hostingPayTrades = hostingPayTradeManager.selectSynchronizedHostingPayTrades();
            if (Collections3.isEmpty(hostingPayTrades)) {
                logger.info("没有同步代付的交易数据");
                return;
            }
            for (HostingPayTrade hostingPayTrade : hostingPayTrades) {
                ResultDto<QueryTradeResult> result = sinaPayClient.queryTrade(
                        SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()),
                        IdType.UID,
                        hostingPayTrade.getTradeNo(),
                        1,
                        20,
                        null,
                        null
                );
                if (result == null) {
                    continue;
                }
                //订单不存在
                if (ErrorCode.ORDER_NOT_EXIST.code().equalsIgnoreCase(result.getErrorCode())){
                    logger.info("同步代付交易号：{},这笔代付不存在", hostingPayTrade.getTradeNo() );
                }
                QueryTradeResult queryTradeResult = result.getModule();
                if (queryTradeResult != null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
                    TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
                    logger.info("同步代付交易号：" + tradeItem.getTradeNo() + "状态为：" + tradeItem.getProcessStatus());
                    HostingPayTradeListenerObject listenerObject = new HostingPayTradeListenerObject();
                    listenerObject.setHostingPayTrade(hostingPayTrade);
                    listenerObject.setTradeItem(tradeItem);
                    eventBus.post(listenerObject);
                    // 交易关闭状态处理业务
//                    if (TradeStatus.TRADE_CLOSED.name().equals(tradeItem.getProcessStatus())) {
                        //放款业务后续处理流程
//                        if (TypeEnum.HOSTING_PAY_TRADE_LOAN.getType() == hostingPayTrade.getType()) {
//                            transactionManager.afterHostingPayTradeLoan(tradeItem.getProcessStatus(), tradeItem.getTradeNo());
//                        }
//                        //还本付息业务后续处理流程
//                        if (TypeEnum.HOSTING_PAY_TRADE_RETURN.getType() == hostingPayTrade.getType()) {
//                            transactionInterestManager.afterPayInterestAndPrincipal(tradeItem.getProcessStatus(), tradeItem.getTradeNo());
//                        }
//                    }
//                    if (TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())){
//                        //放款业务后续处理流程
//                        if (TypeEnum.HOSTING_PAY_TRADE_LOAN.getType() == hostingPayTrade.getType()) {
//                            transactionManager.afterHostingPayTradeLoan(tradeItem.getProcessStatus(), tradeItem.getTradeNo());
//                        }
//                        //还本付息业务后续处理流程
//                        if (TypeEnum.HOSTING_PAY_TRADE_RETURN.getType() == hostingPayTrade.getType()) {
//                            afterHostingPayHandleManager.afterPayInterestAndPrincipal(tradeItem.getProcessStatus(), tradeItem.getTradeNo());
//                        }
//                    }
                }
            }
        } catch (Exception e) {
            logger.error("同步代付交易发生异常", e);
        }
    }



}
