package com.yourong.core.repayment.manager.impl;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.repayment.manager.AfterHostingCollectTradeHandleManager;
import com.yourong.core.tc.dao.TransactionInterestMapper;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.TransactionInterest;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * 代收成功后的业务处理
 * Created by py on 2015/7/29.
 */
@Component
public class AfterHostingCollectTradeHandleManagerImpl  implements AfterHostingCollectTradeHandleManager{
    @Autowired
    private TransactionInterestMapper transactionInterestMapper;
    @Autowired
    private HostingCollectTradeManager hostingCollectTradeManager;
    @Autowired
    private BalanceManager balanceManager;
    @Autowired
    private CapitalInOutLogManager capitalInOutLogManager;
    @Autowired
    private ProjectMapper projectMapper;

    private Logger logger = LoggerFactory.getLogger(AfterHostingCollectTradeHandleManagerImpl.class);


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ResultDO<HostingCollectTrade> afterHostingCollectTradeForPayInterestAndPrincipal(String tradeNo,
                                                                                            String outTradeNo, String tradeStatus) throws Exception {
        ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
        try {
            HostingCollectTrade   temp = hostingCollectTradeManager.getByTradeNo(tradeNo);
            HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getByIdForLock(temp.getId());
            if(hostingCollectTrade!=null) {
                //如果是最终状态，则直接返回
                if(hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
                        ||hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
                        ||hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())) {
                	logger.info("[还本付息]-[代收回调]--项目id：{},代收号：{}原始债权人还款代收已经是最终状态",hostingCollectTrade.getProjectId(),tradeNo);
                    result.setSuccess(false);
                    return result;
                }

                //将交易状态置为最终状态
                hostingCollectTrade.setTradeStatus(tradeStatus);
                hostingCollectTrade.setOutTradeNo(outTradeNo);
                hostingCollectTradeManager.updateHostingCollectTrade(hostingCollectTrade);
                //如果交易为交易成功状态
                if(TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
                    //同步余额，并且记录资金流水
                    //写流水
                    //资金流水的备注
                    Project project = projectMapper.selectByPrimaryKey(hostingCollectTrade.getSourceId());
                    Object[] param = {"",""};
                    if(project!=null){
                        param[0] = StringUtil.getShortProjectName(project.getName());
                    }
                    String ids = hostingCollectTrade.getTransactionInterestIds();
                    if(ids!=null){
                        String[] idArray = ids.split("\\^");
                        boolean resul = NumberUtils.isNumber(idArray[0]);
                        if(resul&& StringUtil.isNotBlank(idArray[0])){
                            TransactionInterest transactionInterest = transactionInterestMapper.selectByPrimaryKey(Long.valueOf(idArray[0]));
                            if(transactionInterest!=null){
                                String period = transactionInterestMapper.queryRepayPeriods(transactionInterest.getTransactionId(), transactionInterest.getEndDate());
                                if(StringUtil.isNotBlank(period) ){
                                    if(transactionInterest.getPayablePrincipal().compareTo(BigDecimal.ZERO)>0){
                                        param[1] = "本金与（"+period+"）利息";
                                    }else{
                                        param[1] = "（"+period+"）利息";
                                    }
                                }
                            }
                        }

                    }
                    String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_RETURN.getRemarks(), param);
                    Balance balance = balanceManager.synchronizedBalance(hostingCollectTrade.getPayerId(), TypeEnum.BALANCE_TYPE_PIGGY);
                    //记录用户投资资金流水
                    capitalInOutLogManager.insert(
                            hostingCollectTrade.getPayerId(),
                            TypeEnum.FINCAPITALINOUT_TYPE_RETURN,
                            null,
                            hostingCollectTrade.getAmount(),
                            balance.getAvailableBalance(),
                            hostingCollectTrade.getId().toString(),
                            remark,
                            TypeEnum.BALANCE_TYPE_PIGGY
                    );
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("代收异常，tradeNo：" +tradeNo, e);
            result.setSuccess(false);
            throw e;
        }
    }

}
