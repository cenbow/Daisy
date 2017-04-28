package com.yourong.core.repayment.manager.impl;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.BalanceAction;
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
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.LeaseBonusManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.repayment.manager.SynchronizedHostingCollectTradeManager;
import com.yourong.core.repayment.model.HostingCollectTradeListenerObject;
import com.yourong.core.tc.manager.*;
import com.yourong.core.tc.model.HostingCollectTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by py on 2015/7/23.
 */
@Component
public class SynchronizedHostingCollectTradeManagerImpl implements SynchronizedHostingCollectTradeManager {
    @Resource
    private TransactionInterestManager transactionInterestManager;
    @Resource
    private HostingCollectTradeManager hostingCollectTradeManager;
    @Resource
    private SinaPayClient sinaPayClient;
    @Autowired
    private TransactionManager myTransactionManager;
    @Autowired
    private BalanceManager balanceManager;
    @Autowired
    private CapitalInOutLogManager capitalInOutLogManager;
    @Autowired
    private LeaseBonusManager leaseBonusManager;
    @Autowired
    private EventBus eventBus;


    private  Logger logger = LoggerFactory.getLogger(SynchronizedHostingCollectTradeManagerImpl.class);

    public  void synchronizedHostingCollectTrade() {
        // 查询需要同步的代收交易 (除用户投资外的)
        try {
            //暂定同步两次，考虑新浪服务器的异常，出现一 等待交易的 状态
            for (int i=0 ,size =2;i<size;i++){
            	logger.info("查询还本付息需要同步的出借人还款代收和平台垫付收益券代收记录开始");
                List<HostingCollectTrade> hostingCollectTrades = hostingCollectTradeManager.selectSynchronizedHostingCollectTradesForPayerAndReplayment();
                logger.info("查询还本付息需要同步的出借人还款代收和平台垫付收益券代收记录结束，代收列表hostingCollectTrades="+hostingCollectTrades);
                if (Collections3.isEmpty(hostingCollectTrades)) {
                    return;
                }
                for (HostingCollectTrade hostingCollectTrade : hostingCollectTrades) {
                    ResultDto<QueryTradeResult>   result = getQueryTradeResultResultDto(hostingCollectTrade);
                    if (result != null) {
                        //订单不存在
                        if (ErrorCode.ORDER_NOT_EXIST.code().equalsIgnoreCase(result.getErrorCode())){
                            logger.info("同步代收交易号：{},这笔代付不存在", hostingCollectTrade.getTradeNo() );
                            hostingCollectTradeManager.updateTradeStatus(hostingCollectTrade, ErrorCode.ORDER_NOT_EXIST.code(), "该笔代收新浪不存在");
                            continue;
                        }
                        QueryTradeResult queryTradeResult = result.getModule();
                        if (queryTradeResult != null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
                            TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
                            logger.info("还本付息同步代收交易号：" + tradeItem.getTradeNo() + "状态为：" + tradeItem.getProcessStatus());
                            HostingCollectTradeListenerObject listenerObject = new HostingCollectTradeListenerObject();
                            listenerObject.setHostingCollectTrade(hostingCollectTrade);
                            listenerObject.setTradeNo(tradeItem.getTradeNo());
                            listenerObject.setTradeStatus(tradeItem.getProcessStatus());
                            eventBus.post(listenerObject);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("同步代收交易发生异常", e);
        }
    }
    /**
     * 查询交易
     *
     * @param hostingCollectTrade
     * @return
     * @throws Exception
     */
    private ResultDto<QueryTradeResult> getQueryTradeResultResultDto(HostingCollectTrade hostingCollectTrade) throws Exception {
        ResultDto<QueryTradeResult> result = null;
        Set set = Sets.newHashSet();
        set.add(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_A.getType());
        set.add(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType());
        set.add(TypeEnum.HOSTING_COLLECT_TRADE_LEASE_BONUS.getType());
        if (set.contains(hostingCollectTrade.getType())) {
           return  sinaPayClient.queryTrade(
                    SerialNumberUtil.getBasicAccount(),
                    IdType.EMAIL,
                    hostingCollectTrade.getTradeNo(),
                    1,
                    20,
                    null,
                    null
            );
        }
        set = Sets.newHashSet();
        set.add(TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType());
        set.add(TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType());
        set.add(TypeEnum.HOSTING_COLLECT_DIRECT_PAY.getType());
        if (set.contains(hostingCollectTrade.getType())) {
            return  sinaPayClient.queryTrade(
                    SerialNumberUtil.generateIdentityId(hostingCollectTrade.getPayerId()),
                    IdType.UID,
                    hostingCollectTrade.getTradeNo(),
                    1,
                    20,
                    null,
                    null
            );
        }
        return result;
    }


}
