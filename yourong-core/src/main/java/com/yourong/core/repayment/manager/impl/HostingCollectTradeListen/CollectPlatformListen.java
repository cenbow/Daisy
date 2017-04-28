package com.yourong.core.repayment.manager.impl.HostingCollectTradeListen;

import com.google.common.eventbus.Subscribe;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.eventbus.EventListener;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.core.repayment.manager.AfterHostingCollectTradeCouponHandleManager;
import com.yourong.core.repayment.model.HostingCollectTradeListenerObject;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 代收平台的收益劵
 * Created by py on 2015/7/23.
 */
@Component
public class CollectPlatformListen implements EventListener<HostingCollectTradeListenerObject> {

    @Autowired
    private AfterHostingCollectTradeCouponHandleManager afterHostingCollectTradeCouponHandleManager;
    private static Logger logger = LoggerFactory.getLogger(CollectPlatformListen.class);
    @Override
    @Subscribe
    public void handle(HostingCollectTradeListenerObject hostingCollectTradeListenerObject) {
        HostingCollectTrade hostingCollectTrade = hostingCollectTradeListenerObject.getHostingCollectTrade();
        String tradeNo = hostingCollectTradeListenerObject.getTradeNo();
        String tradeStatus = hostingCollectTradeListenerObject.getTradeStatus();
         //代收平台收益券
        if (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)
                || TradeStatus.TRADE_FAILED.name().equals(tradeStatus) ||
                TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)  ){
            if(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()==hostingCollectTrade.getType()) {
                ResultDO<HostingCollectTrade> collectResultForCouponB = afterHostingCollectTradeCouponHandleManager.afterHostingCollectTradeCouponBForPayInterestAndPrincipal(tradeNo, null, tradeStatus);
                logger.info("代收平台收益劵 tradeNo ={},tradeStatus={}",tradeNo,tradeStatus);
            }

        }

    }
}
