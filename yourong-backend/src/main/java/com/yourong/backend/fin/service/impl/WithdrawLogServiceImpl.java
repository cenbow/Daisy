package com.yourong.backend.fin.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.fin.service.WithdrawLogService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryWithDrawResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.fin.model.WithdrawLog;
import com.yourong.core.uc.dao.MemberBankCardMapper;

@Service
public class WithdrawLogServiceImpl implements WithdrawLogService {
	@Autowired
	private WithdrawLogManager withdrawLogManager;
	
    @Autowired
    private BalanceManager balanceManager;

	@Autowired
    private SinaPayClient sinaPayClient;
	
	@Autowired
	private MemberBankCardMapper memberBankCardMapper;
	
	/**
	 * 日志对象
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	public Page<WithdrawLog> findByPage(Page<WithdrawLog> pageRequest, Map<String, Object> map) {
		try {
			return withdrawLogManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询失败 map =" + map, e);
		}
		return pageRequest;
	}

	@Override
	public boolean synchronizedWithdraw() {		
		try {			
			List<WithdrawLog> 	WithdrawLogs = withdrawLogManager.selectSynchronizedWithdraws();			
			if(Collections3.isNotEmpty(WithdrawLogs)) {
				for (WithdrawLog withdrawLog : WithdrawLogs) {
					try {ResultDto<QueryWithDrawResult> result = sinaPayClient.queryWithDraw(
							SerialNumberUtil.generateIdentityId(withdrawLog.getMemberId()), 
							IdType.UID, 
							AccountType.SAVING_POT, 
							withdrawLog.getWithdrawNo(), 
							0, 
							20, 
							null, 
							null
							);
					if(result!=null) {
						QueryWithDrawResult queryWithDrawResult = result.getModule();
						if(queryWithDrawResult!=null && Collections3.isNotEmpty(queryWithDrawResult.getWithDrawList())) {
							TradeItem tradeItem = queryWithDrawResult.getWithDrawList().get(0);
							//状态为成功
							if(PayStatus.SUCCESS.name().equals(tradeItem.getProcessStatus())) {
//								withdrawLogManager.withdrawSuccess(
//										withdrawLog.getWithdrawNo(),
//										tradeItem.getRemark()
//										);
							}
							//状态为失败
							if(PayStatus.FAILED.name().equals(tradeItem.getProcessStatus())) {
//								withdrawLogManager.withdrawFailed(
//										withdrawLog.getWithdrawNo(),
//										tradeItem.getRemark()
//										);
							}
						}
					}
					} catch (Exception e) {
						logger.error("同步提现状态失败", e);
						return false;
					}
				}
			}
			
			} catch (ManagerException e1) {
				logger.error("查询第三方 提现记录异常", e1);
			}		
		return true;
	}

}