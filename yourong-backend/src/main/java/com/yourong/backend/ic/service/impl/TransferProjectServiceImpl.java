package com.yourong.backend.ic.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.ic.service.TransferProjectService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.tc.manager.HostingCollectTradeAuthManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;

@Service
public class TransferProjectServiceImpl implements TransferProjectService {

	private static Logger logger = LoggerFactory.getLogger(TransferProjectServiceImpl.class);
	
	@Autowired
	private TransferProjectManager transferProjectManager;

	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;

	@Autowired
	private HostingCollectTradeAuthManager hostingCollectTradeAuthManager;

	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;

	@Autowired
	private SinaPayClient sinaPayClient;

	@Override
	public ResultDO<Object> afterHandlePreAuthTrade(Long transferId, boolean reAuthFlag) throws Exception {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			TransferProject transferProject = transferProjectManager.selectByPrimaryKey(transferId);
			if (transferProject == null) {
				logger.info("转让项目不存在，transferId={}", transferId);
				result.setResultCode(ResultCode.TRANSFER_PROJECT_NOT_EXIST);
				return result;
			}
			if (StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus() == transferProject.getStatus()
					&& DateUtils.getCurrentDate().before(transferProject.getTransferEndDate())) {
				// 发起撤销转让
				return transferProjectManager.cancelTransferProject(transferProject.getTransactionId());
			}
			// 发起代收完成
			List<HostingCollectTrade> resultList = null;
			if (reAuthFlag) {
				// 同步下代收记录
				List<HostingCollectTrade> freezeList = hostingCollectTradeManager.selectPreAuthApplySuccessByTransferId(transferId);
				resultList = hostingCollectTradeAuthManager.synHostingCollectTrade(freezeList);
			}
			 if (StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus() == transferProject.getStatus()) {
				// 重新发起代付
				List<HostingPayTrade> localHostPayTrades = transferProjectManager.insertHostPayForTransferSuccess(transferProject, false);
				transferProjectManager.createSinaHostPayForTransferSuccess(localHostPayTrades);
			} /*else if (StatusEnum.TRANSFER_PROJECT_STATUS_LOSING.getStatus() == transferProject.getStatus()) {
				if (Collections3.isNotEmpty(resultList)) {
					hostingCollectTradeAuthManager.cancelPreAuthTrade(resultList);
					return result;
				}
				// 所有代收撤销以后的后续业务处理
				transferProjectManager.afterLoseTransferProject(transferProject.getId());
			}*/ else {
				result.setResultCode(ResultCode.ERROR_SYSTEM);
			}
		} catch (Exception e) {
			logger.error("转让项目发起代收完成/撤销失败, transferId={}", transferId, e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

	@Override
	public ResultDO<Object> synTransferPay(String batchNo) {
		return hostingPayTradeManager.synTransferPay(batchNo);
	}
}
