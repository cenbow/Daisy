package com.yourong.core.repayment.manager.impl.HostingCollectTradeListen;

import com.google.common.eventbus.Subscribe;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.eventbus.EventListener;
import com.yourong.core.ic.manager.LeaseBonusManager;
import com.yourong.core.repayment.model.HostingCollectTradeListenerObject;
import com.yourong.core.tc.model.HostingCollectTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 租赁分红处理
 * Created by PY on 2015/7/23.
 */
@Component
public class LeaseBonusListen implements EventListener<HostingCollectTradeListenerObject>{
    @Autowired
    private LeaseBonusManager leaseBonusManager;
    private static Logger logger = LoggerFactory.getLogger(LeaseBonusListen.class);

    //@Subscribe
    @Override
    public void handle(HostingCollectTradeListenerObject hostingCollectTradeListenerObject ) {
        HostingCollectTrade hostingCollectTrade = hostingCollectTradeListenerObject.getHostingCollectTrade();
        String tradeNo = hostingCollectTradeListenerObject.getTradeNo();
        String tradeStatus = hostingCollectTradeListenerObject.getTradeStatus();
        if(TypeEnum.HOSTING_COLLECT_TRADE_LEASE_BONUS.getType()==hostingCollectTrade.getType()) {
            try {
                leaseBonusManager.afterLeaseBonusCollectNotifyProcess(tradeNo, tradeStatus);
            } catch (Exception e) {
                logger.error("租赁分红异常",e);
            }
        }
    }
}

