package com.yourong.core.repayment.manager.impl.HostingCollectTradeListen;

import com.google.common.eventbus.Subscribe;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.eventbus.EventListener;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.core.repayment.model.HostingCollectTradeListenerObject;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by py on 2015/7/23.
 */
@Component
public class TransactionCosedListen implements EventListener<HostingCollectTradeListenerObject> {

    @Autowired
    private TransactionManager myTransactionManager;
    @Autowired
    private TransactionInterestManager transactionInterestManager;
    @Autowired
    private HostingCollectTradeManager hostingCollectTradeManager;

    private static Logger logger = LoggerFactory.getLogger(TransactionCosedListen.class);

    @Override
    @Subscribe
    public void handle(HostingCollectTradeListenerObject hostingCollectTradeListenerObject) {
        HostingCollectTrade hostingCollectTrade = hostingCollectTradeListenerObject.getHostingCollectTrade();
        String tradeNo = hostingCollectTradeListenerObject.getTradeNo();
        String tradeStatus = hostingCollectTradeListenerObject.getTradeStatus();
        String remak = hostingCollectTradeListenerObject.getRemark();
        //交易关闭逻辑，不做处理， 原因： 并发的情况，新增的代收业务，有可能在新浪那边没有创建
        try {
            // 交易关闭状态处理业务
//            if (TradeStatus.TRADE_CLOSED.name().equals(processStatus)) {
//                if (TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType() == hostingCollectTrade.getType()) {
//                    myTransactionManager.afterTransactionCollectNotifyProcess(tradeNo, null, processStatus);
//                }
//                if (TypeEnum.HOSTING_COLLECT_TRADE_COUPON_A.getType() == hostingCollectTrade.getType()) {
//                    myTransactionManager.afterPaltformCouponCollectNotify(tradeNo, null, processStatus);
//                }
//                if (TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType() == hostingCollectTrade.getType()) {
//                    transactionInterestManager.afterHostingCollectTradeForPayInterestAndPrincipal(tradeNo, null, processStatus);
//                }
//                if (TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType() == hostingCollectTrade.getType()) {
//                    transactionInterestManager.afterHostingCollectTradeCouponBForPayInterestAndPrincipal(tradeNo, null, processStatus);
//                }
//                if (TypeEnum.HOSTING_COLLECT_DIRECT_PAY.getType() == hostingCollectTrade.getType()) {
//                    hostingCollectTradeManager.updateTradeStatus(hostingCollectTrade, processStatus, remak);
//                }
//            }
        } catch (Exception e) {
            logger.error("交易关闭异常", e);
        }

    }
}
