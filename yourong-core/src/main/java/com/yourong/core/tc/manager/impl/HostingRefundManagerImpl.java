package com.yourong.core.tc.manager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.RefundStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.RefundTradeResult;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.tc.dao.HostingRefundMapper;
import com.yourong.core.tc.manager.HostingRefundManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingRefund;

@Component
public class HostingRefundManagerImpl implements HostingRefundManager {

	@Autowired
	private SinaPayClient sinaPayClient;

	@Autowired
	private HostingRefundMapper hostingRefundMapper;
	
	private Logger logger = LoggerFactory.getLogger(HostingRefundManagerImpl.class);

	@Override
	public ResultDto<RefundTradeResult> refundByTradeNo(HostingCollectTrade collectTrade, String summary) throws ManagerException {
		ResultDto<RefundTradeResult> rDto = null;
		try {
			HostingRefund refund = new HostingRefund();
			String refundNo = SerialNumberUtil.generateRefundTradeaNo();
			refund.setTradeNo(refundNo);
			refund.setCollectTradeNo(collectTrade.getTradeNo());
			refund.setProjectId(collectTrade.getProjectId());
			refund.setAmount(collectTrade.getAmount());
			refund.setReceiverId(collectTrade.getPayerId());
			refund.setRefundStatus(RefundStatus.WAIT_REFUND.name());
			refund.setType(TypeEnum.REFUND_TYPE_TRANSACTION.getType());
			refund.setSummary(summary);
			refund.setUserIp(collectTrade.getPayerIp());
			hostingRefundMapper.insertSelective(refund);
			rDto = sinaPayClient.createHostingRefund(refundNo, collectTrade.getTradeNo(),
					collectTrade.getAmount(), refund.getSummary(),collectTrade.getPayerIp());
			if (rDto != null && rDto.isError()) {
				logger.error("退款失败，summary ={}, TradeNo={}, errorCode={}, errorMsg={}", refund.getSummary(), refund.getCollectTradeNo(),
						rDto.getErrorCode(), rDto.getErrorMsg());
			}
		} catch (Exception e) {
			logger.error("退款失败，summary ={}, TradeNo={}", summary, collectTrade.getTradeNo(), e);
		}
		return rDto;
	}

	@Override
	public HostingRefund selectByTradeNo(String tradeNo) throws ManagerException {
		try {
			return hostingRefundMapper.selectByTradeNo(tradeNo);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public String queryStatusByOrderId(String orderid) {
		return hostingRefundMapper.queryStatusByOrderId(orderid);
	}
}
