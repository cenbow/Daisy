package com.yourong.core.repayment.manager.impl.HostingPayTradeFinishedListen;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.Subscribe;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.eventbus.AsyncEventListener;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.manager.OverdueRepayLogManager;
import com.yourong.core.fin.model.OverdueLog;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.repayment.model.HostingPayTradeFinishedListenerObject;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.model.TransactionInterest;

/**
 * @desc 代付成功后，更新项目本息和项目状态
 * @author fuyili 2016年5月27日下午5:35:44
 */
//@Component
public class HostingPaySuccessUpdateProjectInterestStatus implements
		AsyncEventListener<HostingPayTradeFinishedListenerObject> {
	private static Logger logger = LoggerFactory.getLogger(HostingPaySuccessUpdateProjectInterestStatus.class);
	@Autowired
	private TransactionInterestManager transactionInterestManager;
	@Autowired
	private DebtInterestManager debtInterestManager;
	@Autowired
	private ProjectManager projectManager;
	@Autowired
	private SinaPayClient sinaPayClient;
	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	@Autowired
	private SysDictManager sysDictManager;
	@Autowired
	private OverdueLogManager overdueLogManager;
	@Autowired
	private OverdueRepayLogManager overdueRepayLogManager;
	@Autowired
	private TransferProjectManager transferProjectManager;

	/**
	 * @desc 1.项目本息下所有的交易本息都为已还款，则更新项目本息状态为已还款 2.项目本息为已还款，查找是否有逾期记录，对应逾期记录更新为已还款
	 *       3.所有逾期结算记录下对应的逾期记录已还款，更新逾期结算记录为已还款 4.所有项目本息都为已还款，则更新项目状态为已还款
	 *       5.项目更新为已还款，归还保证金
	 * @param hostingPayTradeFinishedListenerObject
	 * @author fuyili
	 * @time 2016年6月6日 上午11:15:56
	 */
	@Override
	@Subscribe
	public void handle(HostingPayTradeFinishedListenerObject hostingPayTradeFinishedListenerObject) {
		logger.info("还本付息后续业务处理【开始】");
		TransactionInterest transactionInterest = hostingPayTradeFinishedListenerObject.getTransactionInterest();
		Project project = hostingPayTradeFinishedListenerObject.getProject();
		try {
			// 项目本息对应所有交易本息记录，是否全都已还款，是则更新项目本息状态为已还款
			int count = transactionInterestManager
					.getCountUnReturnTransationInterestByProjectInterestId(transactionInterest.getInterestId());
			logger.info("项目本息{}交易本息未还成功个数：{}",transactionInterest.getInterestId(),count);
			if (count <= 0) {
				if (transactionInterest.getPayType() == TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_OVERDUE.getType()) {// 逾期还款
																													// 逾期-->已还款
					debtInterestManager.updateStatusById(transactionInterest.getInterestId(),
							StatusEnum.DEBT_INTEREST_OVERDUE_PAY.getStatus(),
							StatusEnum.DEBT_INTEREST_ALL_PAYED.getStatus());
				} else {
					debtInterestManager.updateStatusById(transactionInterest.getInterestId(),
							StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus(),
							StatusEnum.DEBT_INTEREST_ALL_PAYED.getStatus());
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
