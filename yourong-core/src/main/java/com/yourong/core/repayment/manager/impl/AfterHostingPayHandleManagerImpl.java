package com.yourong.core.repayment.manager.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.manager.OverdueRepayLogManager;
import com.yourong.core.fin.model.OverdueLog;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.repayment.manager.AfterHostingPayHandleManager;
import com.yourong.core.repayment.model.HostingPayTradeFinishedListenerObject;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;

/**
 * 代付之后的业务操作
 * Created by py on 2015/7/29.
 */
@Component
public class AfterHostingPayHandleManagerImpl implements AfterHostingPayHandleManager {

    @Autowired
    private HostingPayTradeManager hostingPayTradeManager;
    @Autowired
    private TransactionManager myTransactionManager;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private TransactionInterestManager transactionInterestManager;
    @Autowired
    private AsyncEventBus asyncEventBus;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private DebtInterestManager debtInterestManager;
    @Autowired
    private OverdueLogManager overdueLogManager;
    @Autowired
    private ProjectManager projectManager;
    @Autowired
    private OverdueRepayLogManager overdueRepayLogManager;
	@Autowired
	private TransferProjectManager transferProjectManager;

    private Logger logger = LoggerFactory.getLogger(AfterHostingPayHandleManagerImpl.class);

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ResultDO<TransactionInterest> afterPayInterestAndPrincipal(String tradeStatus, String tradeNo,String outTradeNo) throws Exception {
        ResultDO<TransactionInterest> result = new ResultDO<TransactionInterest>();
        try {
            logger.info("-------代付回调后 ， 代付号NO={},代付状态：{}",tradeNo,tradeStatus);
            HostingPayTrade payTrade = hostingPayTradeManager.getByTradeNo(tradeNo);
            if(payTrade!=null) {
            	HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByIdForLock(payTrade.getId());
                //如果是最终状态，则直接返回
                if(hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
                        ||hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
                        ||hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())) {
                    logger.info("同步代付交易后处理支付本息已经是最终状态，tradeNo="+tradeNo + "状态："+hostingPayTrade.getTradeStatus());
                    return result;
                }
                //将交易状态置为最终状态
                hostingPayTrade.setTradeStatus(tradeStatus);
                hostingPayTrade.setOutTradeNo(outTradeNo);
                hostingPayTradeManager.updateHostingPayTrade(hostingPayTrade);
                if(hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())) {
                    //将本息记录置为已支付状态
                	TransactionInterest transactionInterest = transactionInterestManager.getTransactionInterestById(hostingPayTrade.getSourceId());
                	int updateStatus = 0 ;
                	//正常还款和逾期还款的交易本息     需要更新实际已付
    				if (transactionInterest.getPayType() == TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_NORMAL.getType()
    						|| transactionInterest.getPayType() == TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_OVERDUE.getType()) {
    					updateStatus = transactionInterestManager.updateStatusAndRealPayForPaySuccess(
    							StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus(),
    							StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus(), hostingPayTrade.getSourceId());
    					//获取实际支付
    					transactionInterest = transactionInterestManager.getTransactionInterestById(hostingPayTrade.getSourceId());
    				} else {
    					updateStatus = transactionInterestManager.updateStatusForPaySuccess(
    							StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus(),
    							StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus(), hostingPayTrade.getSourceId());
    				}
    				if (updateStatus == 0) {
    					logger.info("本息记录已经更新过了，id={}", hostingPayTrade.getSourceId());
    					return result;
    				}
                    //更新交易表中已收本金和已收利息
                    if(transactionInterest!=null) {
                        Transaction transaction = myTransactionManager.selectTransactionByIdLock(transactionInterest.getTransactionId());
                        transaction.setReceivedInterest(transactionInterest.getRealPayInterest().add(transaction.getReceivedInterest()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        transaction.setReceivedPrincipal(transactionInterest.getRealPayPrincipal().add(transaction.getReceivedPrincipal()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        transaction.setReceivedExtraInterest(transactionInterest.getRealPayExtraInterest().add(transaction.getReceivedExtraInterest()));
                        transaction.setReceivedExtraProjectInterest(transactionInterest.getRealPayExtraProjectInterest().add(transaction.getReceivedExtraProjectInterest()));
                        transaction.setOverdueFine(transactionInterest.getOverdueFine().add(transaction.getOverdueFine()));//逾期
                        Project project = projectMapper.selectByPrimaryKey(transaction.getProjectId());
                        //判断对应的交易本息是否都为已还款，如果是已还款更新交易状态
                        if(transactionInterestManager.getCountUnReturnTransationInterestByTransationId(transaction.getId())<=0){
                        	transaction.setStatus(StatusEnum.TRANSACTION_COMPLETE.getStatus());
                        }
                        myTransactionManager.updateByPrimaryKeySelective(transaction);
                        result.setResult(transactionInterest);
                        //代付成功后，业务剩余处理,以后的业务扩展 ，在这个方法里
                        afterHostingPayTradeSucess(transactionInterest,project);
                    }
                }
            
            }
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    private void afterHostingPayTradeSucess(TransactionInterest transactionInterest,Project project) {
        HostingPayTradeFinishedListenerObject listenerObject = new HostingPayTradeFinishedListenerObject();
        listenerObject.setTransactionInterest(transactionInterest);
        listenerObject.setProject(project);
        //同步通知
        eventBus.post(listenerObject);
        //异步通知
        asyncEventBus.post(listenerObject);
    }

    
    @Override
	public void afterHostingPayTradeSucess(TransactionInterest transactionInterest) throws Exception {
		logger.info("还本付息后续业务处理【开始】");
		Project project = projectMapper.selectByPrimaryKey(transactionInterest.getProjectId());
		try {
			// 项目本息对应所有交易本息记录，是否全都已还款，是则更新项目本息状态为已还款
			int count = transactionInterestManager
					.getCountUnReturnTransationInterestByProjectInterestId(transactionInterest.getInterestId());
			logger.info("项目本息{}交易本息未还成功个数：{}",transactionInterest.getInterestId(),count);
			if (count <= 0) {
				// 逾期还款 逾期-->已还款
				int debInterUpdRes = 0;
				if (transactionInterest.getPayType() == TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_OVERDUE.getType()) {
					debInterUpdRes = debtInterestManager.updateStatusById(transactionInterest.getInterestId(),
							StatusEnum.DEBT_INTEREST_OVERDUE_PAY.getStatus(),
							StatusEnum.DEBT_INTEREST_ALL_PAYED.getStatus());
				} else {
					//其他类型还款
					debInterUpdRes = debtInterestManager.updateStatusById(transactionInterest.getInterestId(),
							StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus(),
							StatusEnum.DEBT_INTEREST_ALL_PAYED.getStatus());
				}
				if (debInterUpdRes > 0) {
					// 统计项目本息下所有交易本息的实付本金和实付利息
					TransactionInterest sumInterest = transactionInterestManager
							.getRealPayByInterestId(transactionInterest.getInterestId());
					debtInterestManager.updateRealPayForPrincipalAndInterestSuccess(
							transactionInterest.getInterestId(), sumInterest.getRealPayInterest(),
							sumInterest.getRealPayPrincipal());
				}
				// 查询是否存在逾期记录，逾期记录更新为已还款
				OverdueLog log = overdueLogManager.selectByInterestId(transactionInterest.getInterestId());
				if (log != null) {
					overdueLogManager.updateForOverdueRepaySuccess(StatusEnum.OVERDUE_LOG_PAYING.getStatus(),
							StatusEnum.OVERDUE_LOG_HAD_PAY.getStatus(), log.getId());
					// 逾期结算记录下所有的逾期记录都为已还款，则更新逾期结算记录为已还款
					if (overdueRepayLogManager.getUnreturnCountByOverdueRepayId(log.getOverdueRepayId()) <= 0) {
						overdueRepayLogManager.updateStatusById(StatusEnum.OVERDUE_REPAYSTATUS_PAYING.getStatus(),
								StatusEnum.OVERDUE_REPAYSTATUS_HADPAY.getStatus(), log.getOverdueRepayId());
					}
				}
			}
			int updProNum = updateProjectStatus(project);
			// 项目表更新为已还款，归还保证金
			if (updProNum > 0) {
				returnGuaranteeFee(project);
				//合同置为已过期
				myTransactionManager.expireContract(project.getId());
			}
			logger.info("还本付息后续业务处理【结束】");
		} catch (ManagerException e) {
			logger.error("还本付息代付成功之后，更新项目本息和项目状态失败", e);
		}
	}

    /**
	 * @Description:归还保证金
	 * @param project
	 * @author: fuyili
	 * @time:2016年6月14日 上午10:40:36
	 */
	private void returnGuaranteeFee(Project project) {
		// 判断项目是否需要归还保证金
		if (!project.isDirectProject() && project.getGuaranteeFeeRate().compareTo(BigDecimal.ZERO) > 0) {
			return;
		}
		try {
			projectManager.createCollectTradeForGuaranteeFee(project);
		} catch (Exception e) {
			logger.error("项目归还保证金发起代付失败，projectId={}", project.getId());
		}
	}

	/**
	 * @Description:更新项目状态为已还款
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年6月14日 上午10:40:51
	 */
	private int updateProjectStatus(Project project) throws ManagerException {
		// 判断所有项目本息是否已还款，如果是则更新项目表为已还款
		int updProNum = 0;
		int countUnReturnByProjectId = debtInterestManager.getCountUnReturnByProjectId(project.getId());
		if (countUnReturnByProjectId <= 0) {
			logger.info("项目{}交易本息未还成功个数：{}",project.getId(),countUnReturnByProjectId);
			if (project.isDirectProject()) {
				updProNum = projectManager.updateProjectStatus(StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus(),
						StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus(), project.getId());
			} else {
				updProNum = projectManager.updateProjectStatus(StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus(),
						StatusEnum.PROJECT_STATUS_FULL.getStatus(), project.getId());
				updProNum = projectManager.updateProjectStatus(StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus(),
						StatusEnum.PROJECT_STATUS_END.getStatus(), project.getId());
			}
			// 如果存在转让项目则更新转让项目的状态为已还款
			transferProjectManager.updateStatusByProjectId(project.getId(), StatusEnum.TRANSFER_PROJECT_STATUS_REPAYMENT.getStatus(),
					StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus());
		}
		return updProNum;
	}



}
