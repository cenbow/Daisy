package com.yourong.core.repayment.manager.impl.HostingCollectTradeListen;

import com.google.common.eventbus.Subscribe;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.eventbus.EventListener;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.core.repayment.manager.AfterHostingCollectTradeHandleManager;
import com.yourong.core.repayment.model.HostingCollectTradeListenerObject;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by py on 2015/7/23.
 */
@Component
public class CollectOldPayerListen implements EventListener<HostingCollectTradeListenerObject> {
    private static Logger logger = LoggerFactory.getLogger(CollectOldPayerListen.class);
    @Autowired
    private HostingCollectTradeManager hostingCollectTradeManager;
    @Autowired
   private  AfterHostingCollectTradeHandleManager afterHostingCollectTradeHandleManager;

    @Override
    @Subscribe
    public void handle(HostingCollectTradeListenerObject hostingCollectTradeListenerObject) {
        HostingCollectTrade hostingCollectTrade = hostingCollectTradeListenerObject.getHostingCollectTrade();
        String tradeNo = hostingCollectTradeListenerObject.getTradeNo();
        String tradeStatus = hostingCollectTradeListenerObject.getTradeStatus();
        String remark = hostingCollectTradeListenerObject.getRemark();
        // 交易是trade_finished或者trade_failed处理逻辑
        if (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)
                || TradeStatus.TRADE_FAILED.name().equals(tradeStatus)  ) {
            //代收原始债权人
            if (TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType() == hostingCollectTrade.getType()) {
                try {
                    ResultDO<HostingCollectTrade> collectResult = afterHostingCollectTradeHandleManager.afterHostingCollectTradeForPayInterestAndPrincipal(tradeNo, null, tradeStatus);
                } catch (Exception e) {
                    logger.error("代收原始债权人异常 tradeNo={}",tradeNo, e);
                }
            }
        }
        // 交易是trade_closed处理逻辑
        if (TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)){
            if (TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType() == hostingCollectTrade.getType()) {
                try {
                    hostingCollectTradeManager.updateTradeStatus(hostingCollectTrade, tradeStatus, remark);
                } catch (ManagerException e) {
                    logger.error("更新待收表异常 tradeNo={}",tradeNo, e);
                }
            }
        }
    }
}

