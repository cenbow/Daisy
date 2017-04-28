package com.yourong.backend.jobs;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.backend.tc.service.TransactionInterestService;
import com.yourong.backend.tc.service.TransactionService;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.repayment.manager.BeginToRepayment;


/**
 * 还本付息定时创建原始债权人还款代收和平台垫付代收
 *
 * @author Administrator
 */
public class CreateHostingCollectTradeForPayInterestAndPrincipalTask {

    @Autowired
    private DebtInterestManager debtInterestManager;

    @Autowired
    private BeginToRepayment beginToRepayment;

    @Resource
    private TransactionInterestService transactionInterestService;

    @Resource
    private TransactionService transactionService;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

    private static final Logger logger = LoggerFactory.getLogger(CreateHostingCollectTradeForPayInterestAndPrincipalTask.class);

    public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("[还本付息]-[创建代收]定时创建原始债权人还款代收和平台垫付代收task start");
			        beginToRepayment.beginToRepayment();
			        //transactionService.createHostingCollectTradeForPayInterestAndPrincipal();
			        logger.info("[还本付息]-[创建代收]还本付息定时创建原始债权人还款代收和平台垫付代收task end");

					/*// 还本付息完成后执行一次同步代付操作，以保证所有还本付息代付操作能处理完成
					logger.info("还本付息完成后执行一次同步代付操作task start");
					transactionService.SynchronizedHostingPayTrade();
					logger.info("还本付息完成后执行一次同步代付操作task end");*/
				} catch (Exception e) {
					logger.error("[还本付息]-[创建代收]还本付息定时创建原始债权人还款代收和平台垫付代收发生异常", e);
				}
			}
		});
//        logger.info("债权本息还本付息task start");
//        try {
//            debtInterestManager.updateStatusForPayInterestAndPrincipal();
//        } catch (ManagerException e) {
//            logger.error("债权本息还本付息发生异常", e);
//        }
//        logger.info("债权本息还本付息task end");
    }
}
