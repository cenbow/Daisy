package com.yourong.backend.fin.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.fin.service.RechargeLogService;
import com.yourong.backend.sys.service.impl.SysDictServiceImpl;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryDepositResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.fin.manager.RechargeLogManager;
import com.yourong.core.fin.model.RechargeLog;

@Service
public class RechargeLogServiceImpl implements RechargeLogService {

	private static Logger logger = LoggerFactory.getLogger(SysDictServiceImpl.class);
	
	@Autowired
	private RechargeLogManager rechargeLogManager;

	@Autowired
    private SinaPayClient sinaPayClient;
	
	@Override
	public Page<RechargeLog> findByPage(Page<RechargeLog> pageRequest,
			Map<String, Object> map) {
		try {
			return rechargeLogManager.queryLogByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询充值记录失败", e);
		}
		return null;
	}

	/**
	 * 同步充值
	 */
	@Override
	public boolean synchronizedRecharge(Integer status, String rechargeNo, String startTime, String endTime) {
		try {
			if (status == null) {
				status = StatusEnum.RECHARGE_STATUS_PROESS.getStatus();
			}
			List<RechargeLog> rechargeLogs = rechargeLogManager.selectSynchronizedRecharge(status, rechargeNo, startTime, endTime);			
			if(Collections3.isNotEmpty(rechargeLogs)) {
				for (RechargeLog rechargeLog : rechargeLogs) {
					try {ResultDto<QueryDepositResult> result = sinaPayClient.queryDeposit(
							SerialNumberUtil.generateIdentityId(rechargeLog.getMemberId()), 
							IdType.UID, 
							AccountType.SAVING_POT, 
							rechargeLog.getRechargeNo(), 
							1, 
							20, 
							null, 
							null
							);
					if(result!=null) {
						QueryDepositResult queryDepositResult = result.getModule();
						if(queryDepositResult!=null && Collections3.isNotEmpty(queryDepositResult.getTradeItemList())) {
							TradeItem tradeItem = queryDepositResult.getTradeItemList().get(0);
							//状态为成功
							if(PayStatus.SUCCESS.name().equals(tradeItem.getProcessStatus())) {
								rechargeLogManager.rechargeSuccess(rechargeLog.getRechargeNo(), rechargeLog.getOuterRechargeNo(), rechargeLog.getRemarks(),
										rechargeLog.getBankCardId(), rechargeLog.getBankCode(), rechargeLog.getPayMethod());
							}
							//状态为失败
							if(PayStatus.FAILED.name().equals(tradeItem.getProcessStatus())) {
								rechargeLogManager.rechargeFailed(rechargeLog.getRechargeNo(), rechargeLog.getOuterRechargeNo(), tradeItem.getRemark(),
										rechargeLog.getBankCardId(), rechargeLog.getBankCode(), rechargeLog.getPayMethod());
							}
						}
					}
					} catch (Exception e) {
						logger.error("同步充值状态失败", e);
						return false;
					}
				}
//				Thread.sleep(1000);
			}
			
			} catch (ManagerException e1) {
				logger.error("查询第三方 充值记录异常", e1);
			}
		return true;
	}
}
