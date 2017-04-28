package com.yourong.core.repayment.manager.impl;

import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.BalanceAction;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.repayment.manager.AfterHostingCollectTradeCouponHandleManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by py on 2015/8/5.
 */
@Component
public class AfterHostingCollectTradeCouponHandleManagerImpl implements AfterHostingCollectTradeCouponHandleManager {
    @Autowired
    private HostingCollectTradeManager hostingCollectTradeManager;
    @Autowired
    private BalanceManager balanceManager;
    @Autowired
    private CapitalInOutLogManager capitalInOutLogManager;

    private Logger logger = LoggerFactory.getLogger(AfterHostingCollectTradeCouponHandleManagerImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ResultDO<HostingCollectTrade> afterHostingCollectTradeCouponBForPayInterestAndPrincipal(
            String tradeNo, String outTradeNo, String tradeStatus) {
    	
        ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
        try {
            HostingCollectTrade hostingCollectTradeUnLock = hostingCollectTradeManager.getByTradeNo(tradeNo);
            if (hostingCollectTradeUnLock != null) {
                HostingCollectTrade hostingCollectTradeForLock = hostingCollectTradeManager.getByIdForLock(hostingCollectTradeUnLock.getId());
                if (hostingCollectTradeForLock != null) {
                    //如果是最终状态，则直接返回
                    if (hostingCollectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
                            || hostingCollectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
                            || hostingCollectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())) {
                        logger.info("平台收益权垫付代收已经是最终状态，tradeNo=" + tradeNo);
                        result.setSuccess(false);
                        return result;
                    }
                    //将交易状态置为最终状态
                    hostingCollectTradeForLock.setTradeStatus(tradeStatus);
                    hostingCollectTradeForLock.setOutTradeNo(outTradeNo);
                    hostingCollectTradeManager.updateHostingCollectTrade(hostingCollectTradeForLock);
                    //如果交易为交易成功状态
                    if (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
                        //写流水
                        Balance balance = balanceManager.reduceBalance(TypeEnum.BALANCE_TYPE_BASIC, hostingCollectTradeForLock.getAmount(), Long.parseLong(Config.internalMemberId), BalanceAction.balance_subtract_Available_subtract);
                        //记录用户投资资金流水
                        capitalInOutLogManager.insert(
                                Long.parseLong(Config.internalMemberId),
                                TypeEnum.FINCAPITALINOUT_TYPE_EARNINGS,
                                null,
                                hostingCollectTradeForLock.getAmount(),
                                balance.getAvailableBalance(),
                                hostingCollectTradeForLock.getId().toString(),
                                null,
                                TypeEnum.BALANCE_TYPE_BASIC
                        );
                    }
                    //失败或者关闭，需要继续创建代收
                    if (TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)
                            || TradeStatus.TRADE_FAILED.name().equals(tradeStatus)) {
                        logger.info("平台收益权垫付代收已经是最终状态，tradeNo=" + tradeNo+"，tradeStatus={}"+tradeStatus);
                    }
                }
            }

            return result;
        } catch (Exception e) {
            logger.error("平台收益权垫付代收回调发生异常，tradeNo：" + tradeNo, e);
            result.setSuccess(false);
            return result;
        }
    }
}
