package com.yourong.backend.tc.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yourong.backend.tc.service.HostingCollectTradeService;
import com.yourong.backend.tc.service.TransactionService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.SummaryEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.RefundStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.ResponseCodeEnum;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.RefundTradeResult;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.dao.HostingRefundMapper;
import com.yourong.core.tc.manager.HostingCollectTradeAuthManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingRefundManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingRefund;
import com.yourong.core.tc.model.Order;

@Service
public class HostingCollectTradeServiceImpl implements HostingCollectTradeService {

	private static Logger logger = LoggerFactory.getLogger(HostingCollectTradeServiceImpl.class);
	
	@Autowired 
	private HostingCollectTradeManager hostingCollectTradeManager;
	
	@Autowired
    private SinaPayClient sinaPayClient;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private OrderManager orderManager;
	
	@Autowired
	private HostingRefundMapper hostingRefundMapper;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
	private HostingCollectTradeAuthManager hostingCollectTradeAuthManager;
	
	@Autowired
	private HostingRefundManager hostingRefundManager;
	
	@Autowired
	private ActivityAfterTransactionManager activityAfterTransactionManager;
	
	@Autowired
	private ProjectManager projectManager;

	@Override
	public Page<HostingCollectTrade> findByPage(
			Page<HostingCollectTrade> pageReques, Map<String, Object> map) {
		try {
			return hostingCollectTradeManager.findByPage(pageReques, map);
		}catch(ManagerException e) {
			logger.error("代收查询失败", e);
		}
		return null;
	}

	@Override
	public ResultDto<QueryTradeResult> queryHostingCollectTradeRecord(Long id) {
		ResultDto<QueryTradeResult> result=new ResultDto<QueryTradeResult>();
		try {
			HostingCollectTrade trade=hostingCollectTradeManager.getById(id);
			result = sinaPayClient.queryTrade(
				SerialNumberUtil.generateIdentityId(trade.getPayerId()), 
				IdType.UID, 
				trade.getTradeNo(), 
				0, 
				20, 
				null, 
				null
				);
		} catch (Exception e) {
			logger.error("代收查询失败", e);
		}
		return result;
	}

	@Override
	public ResultDO<Object> hostingRefundUnfrozen(String tradeNo) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			Order orderUnLock = orderManager.getOrderByTradeNo(tradeNo);
			if (orderUnLock == null) {
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
				return resultDO;
			}
			Order orderForLock = orderManager.getOrderByIdForLock(orderUnLock.getId());
			// 判断是否是已支付，投资失败
			if(StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus() == orderForLock.getStatus()) {
				// 查询代收记录
				HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
				// 如果交易状态是已完成或代冻结（直投项目为代冻结）
				return handleRefundUnfrozen(orderForLock, tradeNo, collectTrade);
			}
		} catch (Exception e) {
			resultDO.setSuccess(false);
			logger.error("根据代收交易单号退款/解冻失败", e);
		}
		return resultDO;
	}
	// 退款/撤销冻结处理
	private ResultDO<Object> handleRefundUnfrozen(Order order, String tradeNo, HostingCollectTrade collectTrade) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		// 如果是债权项目
		if (TradeStatus.TRADE_FINISHED.name().equals(collectTrade.getTradeStatus())) {
			// 查询退款记录
			List<HostingRefund> hostingRefunds = hostingRefundMapper.findRefundByCollectNoForProjectLose(tradeNo);
			if(!Collections3.isEmpty(hostingRefunds)){
				HostingRefund hostingRefund = hostingRefunds.get(0);
				// 退款记录是等待处理
				if (RefundStatus.WAIT_REFUND.name().equals(hostingRefund.getRefundStatus())) {
					// 查询并同步最终状态
					ResultDO<?> sysResultDO = transactionService.synHostingRefundTrade(hostingRefund.getTradeNo());
					if (sysResultDO != null) {
						// 如果退款订单在新浪不存在
						if (ResponseCodeEnum.ILLEGAL_OUTER_TRADE_NO.name().equals(sysResultDO.getResult().toString())) {
							// 退款状态不是最终状态，需要重新发起退款，发起请求
							SysDict sysDict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
							String clientIp = "";
							if(sysDict != null) {
								clientIp = sysDict.getValue();
							}
							ResultDto<RefundTradeResult> resultDto = sinaPayClient.createHostingRefund(hostingRefund.getTradeNo(), collectTrade.getTradeNo(),
									collectTrade.getAmount(), SummaryEnum.REFUND_PROJECT_BALANCE_NOT_ENOUGN.getDesc(), clientIp);
							if (resultDto == null || !resultDto.isSuccess()) {
								resultDO.setResultCode(ResultCode.ERROR);
								logger.error("退款失败，summary ={}, TradeNo={}, errorCode={}, errorMsg={}", SummaryEnum.REFUND_PROJECT_BALANCE_NOT_ENOUGN.getDesc(),
										hostingRefund.getCollectTradeNo(), resultDto.getErrorCode(), resultDto.getErrorMsg());
							}
						} 
					}
				} else {
					resultDO.setResultCode(ResultCode.ORDER_COLLECT_TRADE_WAIT_PAY_ERROR);
					logger.error("退款失败，summary ={}, TradeNo={}, errorCode={}, errorMsg={}", SummaryEnum.REFUND_PROJECT_BALANCE_NOT_ENOUGN.getDesc(),
							hostingRefund.getCollectTradeNo(), resultDO.getResultCode(), resultDO.getResultCode().getMsg());
				}
			}
		}
		
		// 如果是直投项目
		if (TradeStatus.PRE_AUTH_APPLY_SUCCESS.name().equals(collectTrade.getTradeStatus())) {
			// 发起代收撤销
			ResultDto<?> resultDto = hostingCollectTradeAuthManager.cancelPreAuthTradeByCollectTrade(collectTrade, SummaryEnum.REFUND_PROJECT_BALANCE_NOT_ENOUGN.getDesc());
			if (resultDto == null || !resultDto.isSuccess()) {
				resultDO.setResultCode(ResultCode.ERROR);
				logger.error("代收撤销失败 tradeNo={}, errorCode={}, errorMsg={}", collectTrade.getTradeNo(), resultDto.getErrorCode(),
						resultDto.getErrorMsg());
			}
		}
		
		return resultDO;
	}

	@Override
	public Object refundTradeCollection(String tradeNo, String remark) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
			if(collectTrade==null){
				resultDO.setResultCode(ResultCode.COLLECT_TRADE_NOT_EXIST_ERROR);
				return resultDO;
			}
			// 判断是否是交易结束状态
			if(SinaPayEnum.TRANSACTION_TRADE_FINISHED.getClass().equals(collectTrade.getTradeStatus())) {
				// 如果交易状态是已完成或代冻结（直投项目为代冻结）
				 return hostingRefundManager.refundByTradeNo(collectTrade, remark);
			}else{
				resultDO.setResultCode(ResultCode.COLLECT_TRADE_NOT_FINISH_STATUS_ERROR);
				return resultDO;
			}
		} catch (Exception e) {
			resultDO.setSuccess(false);
			logger.error("根据代收交易单号发起退款失败", e);
		}
		return resultDO;
	}

	@Override
	public Object sendFiveRites(Long projectId) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			return activityAfterTransactionManager.sendFiveRites(projectId);
		} catch (Exception e) {
			resultDO.setSuccess(false);
			logger.error("补发五重礼人气值失败", e);
		}
		return resultDO;
	}
	
	@Override
	public Object handlePreAuthTrade(String tradeNo, int handleType) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
			if (collectTrade == null) {
				resultDO.setResultCode(ResultCode.COLLECT_TRADE_NOT_EXIST_ERROR);
				return resultDO;
			}
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			ResultDO<Project> rDO = new ResultDO<Project>();
			Project project = projectManager.selectByPrimaryKey(collectTrade.getProjectId());
			if (project == null) {
				resultDO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
				return resultDO;
			}
			rDO.setResult(project);
			List<HostingCollectTrade> freezeList = Lists.newArrayList(collectTrade);
			if (handleType == TypeEnum.COLLECT_TRADE_FINISH_TYPE.getType()) {
				projectManager.finishPreAuthTrade(rDO, freezeList);
				return rDO;
			} else if (handleType == TypeEnum.COLLECT_TRADE_CANCEL_TYPE.getType()) {
				projectManager.cancelPreAuthTrade(rDO, freezeList);
				return rDO;
			} else {
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM);
				return resultDO;
			}
		} catch (Exception e) {
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("根据代收号发起代收完成/撤销失败", e);
		}
		return resultDO;
	}

}
