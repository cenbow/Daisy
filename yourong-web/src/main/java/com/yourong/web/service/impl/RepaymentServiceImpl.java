package com.yourong.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.core.repayment.manager.AfterHostingCollectTradeCouponHandleManager;
import com.yourong.core.repayment.manager.AfterHostingCollectTradeHandleManager;
import com.yourong.core.repayment.manager.RepaymentManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.web.service.BaseService;
import com.yourong.web.service.RepaymentService;


/**
 * 还本付息web处理service类
 * @author Leon Ray
 * 2016年7月18日-下午4:27:40
 */
@Service
public class RepaymentServiceImpl extends BaseService implements RepaymentService {
    @Autowired
    private RepaymentManager repaymentManager;
    @Autowired
    private AfterHostingCollectTradeHandleManager afterHostingCollectTradeHandleManager;
    @Autowired
    private AfterHostingCollectTradeCouponHandleManager afterHostingCollectTradeCouponHandleManager;
    @Autowired
    private HostingCollectTradeManager hostingCollectTradeManager;
    @Autowired
    private HostingPayTradeManager hostingPayTradeManager;
    @Autowired
    private TransactionInterestManager transactionInterestManager;
    
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<HostingCollectTrade> afterHostingCollectTradeForRepayment(
			String tradeNo, String outTradeNo, String tradeStatus) throws Exception {
		ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
		HostingCollectTrade hostingCollectTradeForlock = null;
		try {
			HostingCollectTrade hostingCollectTradeUnlock = hostingCollectTradeManager.getByTradeNo(tradeNo);
			logger.info("[还本付息]-[代收回调]--项目id：{},代收号：{}回调开始处理", hostingCollectTradeUnlock.getProjectId(), tradeNo);
			if(hostingCollectTradeUnlock!=null) {
				hostingCollectTradeForlock = hostingCollectTradeManager.getByIdForLock(hostingCollectTradeUnlock.getId());
				boolean isAllFinished = false;
				if(TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType()==hostingCollectTradeForlock.getType()) {
					ResultDO<HostingCollectTrade> resultDO = afterHostingCollectTradeHandleManager.afterHostingCollectTradeForPayInterestAndPrincipal(tradeNo, outTradeNo, tradeStatus);
					if(resultDO.isSuccess()) {
						isAllFinished = hostingCollectTradeManager.isAllCollectFinishedForRepayment(tradeNo);
					}
				}
				if(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()==hostingCollectTradeForlock.getType()) {
					ResultDO<HostingCollectTrade> resultDO = afterHostingCollectTradeCouponHandleManager.afterHostingCollectTradeCouponBForPayInterestAndPrincipal(tradeNo, outTradeNo, tradeStatus);
					if(resultDO.isSuccess()) {
						isAllFinished = hostingCollectTradeManager.isAllCollectFinishedForRepayment(tradeNo);
					}
				}
				//通过代收号创建本地批付
				if(isAllFinished) {
					 
					if(TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
						String borrowTradeNo = null;
						if(TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType()==hostingCollectTradeForlock.getType()) {
							borrowTradeNo = tradeNo;
						}
						if(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()==hostingCollectTradeForlock.getType()) {
							borrowTradeNo = hostingCollectTradeManager.getBorrowTradeByPlatformTradeNo(tradeNo).getTradeNo();
						}
						//更新关联的交易本息状态为支付中
						transactionInterestManager.updateStatusToPayingForPayInterest(
								StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus(),
								StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus(),	
								hostingCollectTradeUnlock.getProjectId(),
								borrowTradeNo
								);
						List<String> batchPayNos = hostingPayTradeManager.createHostBatchHostingPayForRepayment(borrowTradeNo);
						hostingPayTradeManager.createRemoteBatchHostingPayForRepayment(batchPayNos,hostingCollectTradeUnlock.getProjectId());
					}
				}
			}
			
			return result;
		} catch (Exception e) {
			logger.error("[还本付息]-[代收回调]--项目id：{},代收号：{}回调发生异常",hostingCollectTradeForlock.getProjectId(),tradeNo, e);
			throw new Exception(e);
		}
	}

}