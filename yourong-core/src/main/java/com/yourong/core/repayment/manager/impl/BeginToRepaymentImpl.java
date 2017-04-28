package com.yourong.core.repayment.manager.impl;

import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.manager.UnderwriteLogManager;
import com.yourong.core.ic.dao.DebtInterestMapper;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.repayment.manager.BeginToRepayment;
import com.yourong.core.repayment.manager.CollectCreditorPrincipalAndInterestManager;
import com.yourong.core.repayment.manager.RepaymentManager;
import com.yourong.core.repayment.manager.SynchronizedHostingCollectTradeManager;
import com.yourong.core.repayment.manager.SynchronizedHostingPayTradeManager;
import com.yourong.core.tc.model.biz.TransactionInterestForPay;

/**
 * 还本付息主要逻辑
 * Created by py on 2015/7/23.
 */
@Component
public  class BeginToRepaymentImpl implements BeginToRepayment {
    @Autowired
    private CollectCreditorPrincipalAndInterestManager collectCreditorPrincipalAndInterestManager;

    @Autowired
    private RepaymentManager repaymentManager;
    @Autowired
    private SynchronizedHostingCollectTradeManager synchronizedHostingCollectTradeManager;
    @Autowired
    private SynchronizedHostingPayTradeManager  synchronizedHostingPayTradeManager;
    @Autowired
    private DebtInterestMapper debtInterestMapper;
    @Autowired
    private OverdueLogManager overdueLogManager;
    @Autowired
    private UnderwriteLogManager underwriteLogManager;
    private  Logger logger = LoggerFactory.getLogger(BeginToRepaymentImpl.class);

    @Override
    public   synchronized  void beginToRepayment() throws Exception{
        StopWatch  stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("[还本付息]-[创建代收]--开始分项目计算借款人还款代收金额和平台收益券垫付代收金额，同时创建本地代收和远程代收  , 时间:{}",stopWatch.getTime());
        //计算还本付息资金,并且保存本地和远程借款人（出借人）代收、平台收益券代收
        collectCreditorPrincipalAndInterestManager.createHostingCollectTradeForPayInterestAndPrincipal();
        logger.info("[还本付息]-[创建代收]--计算分项目计算借款人还款代收金额和平台收益券垫付代收金额，同时创建本地代收和远程代收结束, 时间:{}",stopWatch.getTime());
//        Thread.sleep(10000);
//        //同步代收
//        logger.info("[还本付息]3 --同步借款人还款代收和平台收益券垫付代收, 时间:{}",stopWatch.getTime());
//        synchronizedHostingCollectTradeManager.synchronizedHostingCollectTrade();
//        //发起代付给投资者
//        logger.info("[还本付息]4 --代付给投资者, 时间:{}",stopWatch.getTime());
        //TODO 要重新考虑在什么地方更新交易本息的状态
//        repaymentManager.updateInterestStatus(hostingCollectTradeForPayInterestAndPrincipal);
//        Thread.sleep(10000);
//        logger.info("[还本付息]5 --同步代付, 时间:{}",stopWatch.getTime());
        //synchronizedHostingPayTradeManager.synchronizedHostingPayTrade();
        List<DebtInterest> underWriteDebtInterest = debtInterestMapper.findUnderWriteDebtInterest();
        for (DebtInterest interest: underWriteDebtInterest){
            //插入逾期记录
//            overdueLogManager.insertOverdue(interest.getProjectId(),interest.getId(), DateUtils.getCurrentDate(),TypeEnum.OVERDUE_LOG_TYPE_UNDERWRITE.getType());
            // 修改垫资时间 和 垫资状态
            underwriteLogManager.updateUnderWriteLogByInterestId(interest.getId());
        }
        stopWatch.stop();
        logger.info("[还本付息]还本付息执行完成,时间:{}",stopWatch.getTime());
    }








}
