package com.yourong.core.repayment.manager.impl.HostingPayTradeFinishedListen;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.eventbus.EventListener;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.model.Project;
import com.yourong.core.repayment.model.HostingPayTradeFinishedListenerObject;
import com.yourong.core.tc.dao.TransactionInterestMapper;
import com.yourong.core.tc.model.TransactionInterest;

/**
 * 代付成功后，记录资金流水日志
 * Created by Administrator on 2015/8/4.
 */
@Component
public class HostingPaySucessInsertPoLog implements EventListener<HostingPayTradeFinishedListenerObject> {
    private static Logger logger = LoggerFactory.getLogger(HostingPaySucessInsertPoLog.class);
    @Autowired
    private TransactionInterestMapper transactionInterestMapper;
    @Autowired
    private BalanceManager balanceManager;
    @Autowired
    private CapitalInOutLogManager capitalInOutLogManager;

    @Override
    @Subscribe
    public void handle(HostingPayTradeFinishedListenerObject hostingPayTradeFinishedListenerObject) {
        TransactionInterest transactionInterest = hostingPayTradeFinishedListenerObject.getTransactionInterest();
        Project project = hostingPayTradeFinishedListenerObject.getProject();
        //写流水
        Balance balance = null;
        try {
            balance = balanceManager.synchronizedBalance(transactionInterest.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
            //记录利息流水
            payInterestInsertLog(transactionInterest, project, balance);
            //记录本金流水
            payPricipalInsertLog(transactionInterest, project, balance);
            //记录滞纳金流水
            payOverdueFineInsertLog(transactionInterest, project, balance);
        } catch (Exception e) {
            logger.error("代付完成后，写入资金流水异常",e);
        }

    }
    private void payPricipalInsertLog(TransactionInterest transactionInterest, Project project, Balance balance) {
        if(transactionInterest.getRealPayPrincipal()!=null && transactionInterest.getRealPayPrincipal().doubleValue()>0) {
            //资金流水备注
            String remark = "";
            if(project!=null){
                remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_PRINCIPAL.getRemarks(), StringUtil.getShortProjectName(project.getName()));
            }
            //记录用户投资资金流水
            capitalInOutLogManager.insert(
                    transactionInterest.getMemberId(),
                    TypeEnum.FINCAPITALINOUT_TYPE_PRINCIPAL,
                    transactionInterest.getRealPayPrincipal(),
                    null,
                    balance.getAvailableBalance(),
                    transactionInterest.getId().toString(),
                    remark,
                    TypeEnum.BALANCE_TYPE_PIGGY
            );
        }
    }

    private void payInterestInsertLog(TransactionInterest transactionInterest, Project project, Balance balance) {
        if(transactionInterest.getRealPayInterest()!=null && transactionInterest.getRealPayInterest().doubleValue()>0) {
            //资金流水备注
            Object[] param = {"",""};
            if(project!=null){
                param[0] = StringUtil.getShortProjectName(project.getName());
            }
            String repayPeriods = transactionInterestMapper.queryRepayPeriods(transactionInterest.getTransactionId(), transactionInterest.getEndDate());
            if(StringUtil.isNotBlank(repayPeriods)){
                param[1] = repayPeriods;
            }
            String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_INTEREST.getRemarks(), param);
            //记录用户投资资金流水
            capitalInOutLogManager.insert(
                    transactionInterest.getMemberId(),
                    TypeEnum.FINCAPITALINOUT_TYPE_INTEREST,
                    transactionInterest.getRealPayInterest(),
                    null,
                    balance.getAvailableBalance(),
                    transactionInterest.getId().toString(),
                    remark,
                    TypeEnum.BALANCE_TYPE_PIGGY
            );
        }
    }
    
    private void payOverdueFineInsertLog(TransactionInterest transactionInterest, Project project, Balance balance) {
        if(transactionInterest.getOverdueFine()!=null && transactionInterest.getOverdueFine().doubleValue()>0) {
            //资金流水备注
        	String remark = "";
            if(project!=null){
                remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_OVERFINE.getRemarks(), StringUtil.getShortProjectName(project.getName()));
            }
            //记录用户投资资金流水
            capitalInOutLogManager.insert(
                    transactionInterest.getMemberId(),
                    TypeEnum.FINCAPITALINOUT_TYPE_PAY_OVERDUE,
                    transactionInterest.getOverdueFine(),
                    null,
                    balance.getAvailableBalance(),
                    transactionInterest.getId().toString(),
                    remark,
                    TypeEnum.BALANCE_TYPE_PIGGY
            );
        }
    }
}
