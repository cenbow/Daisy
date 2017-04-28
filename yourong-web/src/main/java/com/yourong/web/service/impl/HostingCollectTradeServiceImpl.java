package com.yourong.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.SummaryEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.web.service.HostingCollectTradeService;

@Service
public class HostingCollectTradeServiceImpl implements HostingCollectTradeService {

	private Logger logger = LoggerFactory.getLogger(HostingCollectTradeServiceImpl.class);

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private OrderManager orderManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Autowired
	private ProjectExtraManager projectExtraManager;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;
	
	@Autowired
	private TransactionManager transactionManager;

	@Override
	public String handlePreAuthTrade(String notifyTradeStatus, HostingCollectTrade collectTrade) {
		try {
			logger.info("代收完成/撤销主方法入口, tradeNo={}, notifyTradeStatus={}", collectTrade.getTradeNo(), notifyTradeStatus);
			// 订单失败发起的代收撤销
			if (TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType() == collectTrade.getType()
					&& TradeStatus.PRE_AUTH_CANCELED.name().equals(notifyTradeStatus)) {
				if (TradeStatus.PRE_AUTH_CANCELED.name().equals(collectTrade.getTradeStatus())) {
					return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
				}
				Order order = orderManager.getOrderByIdForLock(collectTrade.getSourceId());
				if (StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus() == order.getStatus()
						&& (SummaryEnum.REFUND_PROJECT_BALANCE_NOT_ENOUGN.getDesc().equals(order.getRemarks()) || (SummaryEnum.TRANSFER_PROJECT_STSTUS_ERROR
								.getDesc().equals(order.getRemarks())))) {
					logger.info("非委托支付交易发起代收撤销,tradeNo={},reason={}", collectTrade.getTradeNo(), order.getRemarks());
					HostingCollectTrade updateModel = new HostingCollectTrade();
					updateModel.setId(collectTrade.getId());
					updateModel.setTradeStatus(notifyTradeStatus);
					updateModel.setRemarks(order.getRemarks());
					// 将交易状态置为最终状态
					int i = hostingCollectTradeManager.updateHostingCollectTradeByIdAndTradeStatus(updateModel);
					if (i > 0) {
						try {
							balanceManager.synchronizedBalance(collectTrade.getPayerId(), TypeEnum.BALANCE_TYPE_PIGGY);
						} catch (Exception e) {
							logger.error("代收撤销同步存钱罐余额失败,memberId={}", collectTrade.getPayerId());
						}
					}
					return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
				}
			}
			// 直投项目流标或者审核通过逻辑
			ResultDO<Object> notifyReturnResult = hostingCollectTradeManager.handlePreAuthTrade(notifyTradeStatus, collectTrade);
			if (notifyReturnResult.isError()) {
				logger.error("代收交易完成/撤销返回fail errorCode={} errorMsg={}", notifyReturnResult.getResultCode().getCode(), notifyReturnResult
						.getResultCode().getCodeStr());
				return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
			}
			// 转让项目后续业务处理
			if (collectTrade.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				
				List<HostingCollectTrade> freezeList = hostingCollectTradeManager.selectPreAuthApplySuccessByTransferId(collectTrade
						.getTransferId());
				if (Collections3.isEmpty(freezeList)) {
					// 成功处理后续业务
					if (notifyReturnResult.isSuccess()) {
						taskExecutor.execute(new AfterHandlePreAuthTrade(collectTrade));
					}
				}
				return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
			}
			if (notifyTradeStatus.equals(TradeStatus.PRE_AUTH_CANCELED.name())) {
				// 通知投资人流标
				Order order = orderManager.selectByPrimaryKey(collectTrade.getSourceId());
				MessageClient.sendMsgForCommon(order.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.P2P_RAISE_FAIL.getCode(),
						DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_7), 
						(order.getProjectName().contains("期")?order.getProjectName().substring(0, order.getProjectName().indexOf("期") + 1):order.getProjectName()),
						order.getInvestAmount().toString());
				Transaction tra = transactionManager.getTransactionByOrderId(order.getId());
				MessageClient.sendMsgForCommon(order.getMemberId(), Constant.MSG_TEMPLATE_TYPE_APP, MessageEnum.APP_DIRECT_PROJECT_FAIL.getCode(), 
						order.getProjectName().contains("期")?order.getProjectName().substring(0, order.getProjectName().indexOf("期") + 1):order.getProjectName(),tra.getId().toString());
				if(projectExtraManager.isQuickProject(order.getProjectId())){// 快投项目额外通知中奖人，奖励失效
					//校验流标通知是否发放过
					if(RedisActivityClient.directLotteryFailIsSend(order.getProjectId())){
						ProjectExtra pe = projectExtraManager.getProjectQucikReward(order.getProjectId());
						ActivityLotteryResult modelResult=new ActivityLotteryResult(); 
						modelResult.setActivityId(pe.getActivityId());
						modelResult.setRemark(order.getProjectId().toString());
						modelResult.setRewardType(5);
						modelResult.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);
						List<ActivityLotteryResult> rewardInfoList=activityLotteryResultManager.sumRewardInfoByMemberId(modelResult);
						if(Collections3.isNotEmpty(rewardInfoList)){
							for(ActivityLotteryResult sendResult:rewardInfoList){
								MessageClient.sendMsgForCommon(sendResult.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_QUICK_FAIL.getCode(), 
										order.getProjectName().contains("期")?order.getProjectName().substring(0, order.getProjectName().indexOf("期") + 1):order.getProjectName());
							}
						}
					}
					
				}
			}
			// 查询是否还有存在代收冻结
			List<HostingCollectTrade> freezeList = hostingCollectTradeManager.selectPreAuthApplySuccessByProjectId(collectTrade
					.getProjectId());
			if (Collections3.isEmpty(freezeList)) {
				logger.info("直投项目={}, 剩余代收冻结笔数为零", collectTrade.getProjectId());
				// 成功处理后续业务
				if (notifyReturnResult.isSuccess()) {
					taskExecutor.execute(new AfterHandlePreAuthTrade(collectTrade));
				}
			}
			return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
		} catch (Exception e) {
			logger.error("代收完成/撤销成功的业务处理失败, notifyTradeStatus={} tradeNo={}", notifyTradeStatus, collectTrade.getTradeNo(), e);
			return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
		}
	}

	/**
	 * 
	 * @desc 代收完成/撤销后续业务处理
	 * @author wangyanji 2016年5月26日下午2:19:38
	 */
	private class AfterHandlePreAuthTrade implements Runnable {

		private HostingCollectTrade collectTrade;

		public AfterHandlePreAuthTrade(final HostingCollectTrade collectTrade) {
			this.collectTrade = collectTrade;
		}

		@Override
		public void run() {
			try {
				if (collectTrade.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
					
					TransferProject transferProject =transferProjectManager.selectByPrimaryKey(collectTrade.getTransferId());

					if(transferProject==null){
						logger.info("代收完成/撤销后续业务处理,转让项目不存在，transfe{}",collectTrade.getTransferId());
						return;
					}
					
					
				} else {
					projectManager.afterHandlePreAuthTrade(collectTrade.getProjectId(), false);
				}
			} catch (Exception e) {
				logger.error("代收完成/撤销后续业务处理失败, projectId={}",collectTrade.getProjectId(),e);
			}
		}
	}

}
