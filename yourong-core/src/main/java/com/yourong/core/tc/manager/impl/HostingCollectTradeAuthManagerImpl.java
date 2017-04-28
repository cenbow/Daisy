package com.yourong.core.tc.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.CancelAuthTradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.FinishAuthTradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.dao.HostingCollectTradeAuthMapper;
import com.yourong.core.tc.manager.HostingCollectTradeAuthManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingCollectTradeAuth;

@Component
public class HostingCollectTradeAuthManagerImpl implements HostingCollectTradeAuthManager {
	
	@Autowired
	private SinaPayClient sinaPayClient;

	@Autowired
	private HostingCollectTradeAuthMapper hostingCollectTradeAuthMapper;

	@Autowired
	private SysDictManager sysDictManager;

	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;

	private Logger logger = LoggerFactory.getLogger(ContractManagerImpl.class);

	@Override
	public int batchInsert(List<HostingCollectTradeAuth> records) throws ManagerException {
		try {
			return hostingCollectTradeAuthMapper.batchInsert(records);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ResultDto<?> cancelPreAuthTradeByCollectTrade(HostingCollectTrade collectTrade, String summary) {
		ResultDto<?> sinaRdo = null;
		try {
			String outRequestNo = SerialNumberUtil.generateCancelCollecTradeBatchNo(0);
			List<CancelAuthTradeArgs> tradeList = Lists.newArrayList();
			List<HostingCollectTradeAuth> records = Lists.newArrayList();
			// 封装交易参数
			CancelAuthTradeArgs args = new CancelAuthTradeArgs();
			args.setRequestNo(SerialNumberUtil.generateCancelCollecTradeRequestNo(collectTrade.getSourceId()));
			args.setTradeNo(collectTrade.getTradeNo());
			args.setSummary(summary);
			tradeList.add(args);
			// 封装本地记录参数
			HostingCollectTradeAuth collectAuth = new HostingCollectTradeAuth();
			collectAuth.setTradeNo(args.getTradeNo());
			collectAuth.setTradeRequestNo(args.getRequestNo());
			collectAuth.setBatchRequestNo(outRequestNo);
			collectAuth.setAuthType(TypeEnum.COLLECT_TRADE_CANCEL_TYPE.getType());
			collectAuth.setProjectId(collectTrade.getProjectId());
			collectAuth.setRemark(summary);
			records.add(collectAuth);
			batchInsert(records);
			sinaRdo = sinaPayClient.cancelPreAuthTrade(outRequestNo, tradeList);
			if (sinaRdo.isError()) {
				logger.error("代收撤销失败 tradeNo={}, errorCode={}, errorMsg={}", collectTrade.getTradeNo(), sinaRdo.getErrorCode(),
						sinaRdo.getErrorMsg());
			}
			return sinaRdo;
		} catch (Exception e) {
			logger.error("代收撤销失败 tradeNo={}", collectTrade.getTradeNo(), e);
		}
		return sinaRdo;
	}

	@Override
	public ResultDO<Object> finishPreAuthTrade(List<HostingCollectTrade> freezeList) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		Long sourceId = null;
		int paramLength = 0;
		if (Collections3.isNotEmpty(freezeList)) {
			HostingCollectTrade trade = freezeList.get(0);
			if (trade.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				sourceId = trade.getTransferId();
			} else {
				sourceId = trade.getProjectId();
			}
			paramLength = freezeList.size();
		}
		try {
			// 单次请求100条代收
			List<List<HostingCollectTrade>> subList = Lists.partition(freezeList, 10);
			boolean isError = false;
			StringBuffer errorMsg = new StringBuffer("代收完成失败：");
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			for (int i = 0, size = subList.size(); i < size; i++) {
				String outRequestNo = SerialNumberUtil.generateFinishCollecTradeBatchNo(i);
				List<FinishAuthTradeArgs> tradeList = Lists.newArrayList();
				List<HostingCollectTradeAuth> records = Lists.newArrayList();
				for (HostingCollectTrade collectTrade : subList.get(i)) {
					// 封装交易参数
					FinishAuthTradeArgs args = new FinishAuthTradeArgs();
					args.setRequestNo(SerialNumberUtil.generateFinishCollecTradeRequestNo(collectTrade.getSourceId()));
					args.setTradeNo(collectTrade.getTradeNo());
					args.setAmount(new Money(collectTrade.getAmount()));
					args.setSummary(RemarksEnum.PROJECT_AUTH_PASS.getRemarks());
					tradeList.add(args);
					// 封装本地记录参数
					HostingCollectTradeAuth collectAuth = new HostingCollectTradeAuth();
					collectAuth.setTradeNo(args.getTradeNo());
					collectAuth.setTradeRequestNo(args.getRequestNo());
					collectAuth.setBatchRequestNo(outRequestNo);
					collectAuth.setAuthType(TypeEnum.COLLECT_TRADE_FINISH_TYPE.getType());
					collectAuth.setProjectId(collectTrade.getProjectId());
					collectAuth.setUserIp(collectTrade.getPayerIp());
					records.add(collectAuth);
				}
				batchInsert(records);
				ResultDto<?> sinaRdo = sinaPayClient.finishPreAuthTrade(outRequestNo, tradeList, ip);
				if (!sinaRdo.isSuccess()) {
					isError = true;
					errorMsg.append("【批次号:" + outRequestNo + "】" + sinaRdo.getErrorMsg());
				}
			}
			if (isError) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM);
				rDO.getResultCode().setMsg(errorMsg.toString());
			}
		} catch (Exception e) {
			logger.error("代收完成失败 sourceId={}, paramLength={}", sourceId, paramLength, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public ResultDO<Object> cancelPreAuthTrade(List<HostingCollectTrade> freezeList) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		Long sourceId = null;
		int paramLength = 0;
		if (Collections3.isNotEmpty(freezeList)) {
			HostingCollectTrade trade = freezeList.get(0);
			if (trade.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				sourceId = trade.getTransferId();
			} else {
				sourceId = trade.getProjectId();
			}
			paramLength = freezeList.size();
		}
		try {
			// 单次请求100条代收
			List<List<HostingCollectTrade>> subList = Lists.partition(freezeList, 10);
			boolean isError = false;
			StringBuffer errorMsg = new StringBuffer("代收撤销失败：");
			for (int i = 0, size = subList.size(); i < size; i++) {
				String outRequestNo = SerialNumberUtil.generateCancelCollecTradeBatchNo(i);
				List<CancelAuthTradeArgs> tradeList = Lists.newArrayList();
				List<HostingCollectTradeAuth> records = Lists.newArrayList();
				for (HostingCollectTrade collectTrade : subList.get(i)) {
					// 封装交易参数
					CancelAuthTradeArgs args = new CancelAuthTradeArgs();
					args.setRequestNo(SerialNumberUtil.generateCancelCollecTradeRequestNo(collectTrade.getSourceId()));
					args.setTradeNo(collectTrade.getTradeNo());
					args.setSummary(RemarksEnum.PROJECT_LOSE.getRemarks());
					tradeList.add(args);
					// 封装本地记录参数
					HostingCollectTradeAuth collectAuth = new HostingCollectTradeAuth();
					collectAuth.setTradeNo(args.getTradeNo());
					collectAuth.setTradeRequestNo(args.getRequestNo());
					collectAuth.setBatchRequestNo(outRequestNo);
					collectAuth.setAuthType(TypeEnum.COLLECT_TRADE_CANCEL_TYPE.getType());
					collectAuth.setProjectId(collectTrade.getProjectId());
					records.add(collectAuth);
				}
				batchInsert(records);
				ResultDto<?> sinaRdo = sinaPayClient.cancelPreAuthTrade(outRequestNo, tradeList);
				if (!sinaRdo.isSuccess()) {
					isError = true;
					errorMsg.append("【批次号:" + outRequestNo + "】" + sinaRdo.getErrorMsg());
				}
			}
			if (isError) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM);
				rDO.getResultCode().setMsg(errorMsg.toString());
			}
		} catch (Exception e) {
			logger.error("代收撤销失败 sourceId={}, paramLength={}", sourceId, paramLength, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public List<HostingCollectTrade> synHostingCollectTrade(List<HostingCollectTrade> freezeList) throws Exception {
		List<HostingCollectTrade> resultList = Lists.newArrayList();
		for (HostingCollectTrade collectTrade : freezeList) {
			ResultDto<QueryTradeResult> result = null;
			try {
				result = sinaPayClient.queryTrade(SerialNumberUtil.generateIdentityId(collectTrade.getPayerId()), IdType.UID,
						collectTrade.getTradeNo(), 1, 20, null, null);
			} catch (Exception e) {
				result = null;
			}
			if (result == null || result.getModule() == null || Collections3.isEmpty(result.getModule().getPayItemList())) {
				logger.info("同步代收交易号{}失败，返回结果为空", collectTrade.getTradeNo());
				resultList.add(collectTrade);
				continue;
			}
			int updateNum = 0;
			QueryTradeResult queryTradeResult = result.getModule();
			TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
			logger.info("同步代收交易号：" + tradeItem.getTradeNo() + "状态为：" + tradeItem.getProcessStatus());
			// TRADE_FINISHED或者PRE_AUTH_CANCELED更新记录并从list中去除
			if (TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
					|| TradeStatus.PRE_AUTH_CANCELED.name().equals(tradeItem.getProcessStatus())) {
				HostingCollectTrade updateModel = new HostingCollectTrade();
				updateModel.setId(collectTrade.getId());
				updateModel.setTradeStatus(tradeItem.getProcessStatus());
				// 将交易状态置为最终状态
				updateNum = hostingCollectTradeManager.updateHostingCollectTradeByIdAndTradeStatus(updateModel);
			}
			if (updateNum == 0) {
				resultList.add(collectTrade);
				continue;
			}
		}
		return resultList;
	}
}