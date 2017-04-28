package com.yourong.core.repayment.manager.impl.HostingCollectTradeListen;

import com.google.common.eventbus.Subscribe;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.BalanceAction;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.eventbus.EventListener;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.LeaseBonusManager;
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
 * 直接代收成功处理业务
 * Created by py on 2015/7/23.
 */
@Component
public class DirectCollectListen implements EventListener<HostingCollectTradeListenerObject> {

    private static Logger logger = LoggerFactory.getLogger(DirectCollectListen.class);

    @Resource
    private HostingCollectTradeManager hostingCollectTradeManager;
    @Autowired
    private BalanceManager balanceManager;
    @Autowired
    private CapitalInOutLogManager capitalInOutLogManager;


    @Override
    //@Subscribe
    public void handle(HostingCollectTradeListenerObject hostingCollectTradeListenerObject) {

    }
}

