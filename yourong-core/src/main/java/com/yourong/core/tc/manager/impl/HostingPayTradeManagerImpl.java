package com.yourong.core.tc.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.pay.sina.SinaPayConfig;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.BatchTradeNotifyMode;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.TradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.dao.HostingPayTradeMapper;
import com.yourong.core.tc.dao.TransactionInterestMapper;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.model.HostingPayProjectMember;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.TransactionInterest;

@Component
public class HostingPayTradeManagerImpl implements HostingPayTradeManager {
	private Logger logger = LoggerFactory.getLogger(HostingPayTradeManagerImpl.class);
	@Autowired
	private HostingPayTradeMapper hostingPayTradeMapper;

	@Autowired
	private TransactionInterestMapper transactionInterestMapper;
	
	@Autowired
	private SinaPayClient sinaPayClient;
	
	@Autowired
	private SysDictManager sysDictManager;

	@Override
	public int insertSelective(HostingPayTrade record) throws ManagerException {
		try {
			return hostingPayTradeMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public HostingPayTrade getByTradeNo(String tradeNo) throws ManagerException {
		try {
			Map<String, Object> params = Maps.newHashMap();
			params.put("tradeNo", tradeNo);
			List<HostingPayTrade> payTradeList = hostingPayTradeMapper.selectHostingPayTradesByParams(params);
			if (Collections3.isNotEmpty(payTradeList)) {
				return payTradeList.get(0);
			}
			return null;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateHostingPayTrade(HostingPayTrade hostingPayTrade) throws ManagerException {
		try {
			return hostingPayTradeMapper.updateByPrimaryKeySelective(hostingPayTrade);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingPayTrade> getHostingPayTradeByParam(Map<String, Object> params) throws ManagerException {
		try {
			return hostingPayTradeMapper.selectHostingPayTradesByParams(params);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<HostingPayTrade> getTransferHostingPayTradeByTransferId(Long transferId) throws ManagerException {
		try {
			Map<String, Object> params = Maps.newHashMap();
			params.put("sourceId", transferId);
			params.put("type", TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType());
			return hostingPayTradeMapper.selectHostingPayTradesByParams(params);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingPayTrade> selectSynchronizedHostingPayTrades() throws ManagerException {
		try {
			return hostingPayTradeMapper.selectSynchronizedHostingPayTrades();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
//	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean haveHostPayTradeForPayInterestAndPrincipal(Long sourceId, int type) throws Exception {
		try {
			//对交易本息记录加事务控制并发问题
//        	TransactionInterest interest = transactionInterestMapper.selectByPrimaryKeyForLock(sourceId);
			HostingPayTrade hostingPayTrade = hostingPayTradeMapper.selectHostPayTradeForPayInterestAndPrincipal(sourceId, type);
			if (hostingPayTrade != null) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
	
	@Override
	public HostingPayTrade getHostingPayTradeBySourceIdType(Long sourceId, int type) throws ManagerException {
		try {
			HostingPayTrade hostingPayTrade = hostingPayTradeMapper.selectHostPayTradeForPayInterestAndPrincipal(sourceId, type);
			return hostingPayTrade;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean haveHostPayTradeForPayLeaseBonus(Long sourceId, int type, Long transactionId) throws ManagerException {
		try {
			HostingPayTrade hostingPayTrade = hostingPayTradeMapper.selectHostPayTradeForPayLeaseBonus(sourceId, type, transactionId);
			if (hostingPayTrade != null) {
				return true;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return false;
	}

	@Override
	public HostingPayTrade getByIdForLock(Long id) throws ManagerException {
		try {
			return hostingPayTradeMapper.getByIdForLock(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int countHostPayBySourceIdAndType(Long sourceId, int type, String tradeStatus) throws ManagerException {
		try {
			return hostingPayTradeMapper.countHostPayBySourceIdAndType(sourceId, type, tradeStatus);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<HostingPayTrade> findByPage(Page<HostingPayTrade> pageRequest, Map<String, Object> map) throws ManagerException {
			Page<HostingPayTrade> page = new Page<HostingPayTrade>();
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<HostingPayTrade> hostingPayTradeList=  hostingPayTradeMapper.selectHostingPayTradeList(map);
			int totalCount = hostingPayTradeMapper.selectHostingPayTradeListCount(map);
			page.setData(hostingPayTradeList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingPayProjectMember> selectProjectInverstAndHostingPayTrade(Long projectId) throws ManagerException {
		try {
			return hostingPayTradeMapper.selectProjectInverstAndHostingPayTrade(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int countHostPayByBatchNoAndStatus(String batchPayNo, String tradeStatus) throws ManagerException {
		try {
			return hostingPayTradeMapper.countHostPayByBatchNoAndStatus(batchPayNo, tradeStatus);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateHostingPayTradeStatus(String afterTradeStatus, String beforeTradeStatus, String outTradeNo, Long id)
			throws ManagerException {
		try {
			return hostingPayTradeMapper.updateHostingPayTradeStatus(afterTradeStatus, beforeTradeStatus,outTradeNo,id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingPayTrade> selectHostPayByBatchNoAndStatus(String batchPayNo, String tradeStatus)
			throws ManagerException {
		try {
			return hostingPayTradeMapper.selectHostPayByBatchNoAndStatus(batchPayNo, tradeStatus);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<String> createHostBatchHostingPayForRepayment(String tradeNo)  throws Exception{
		try {
			List<String> batchPayNos = Lists.newArrayList();
			List<TransactionInterest> transactionInterests = transactionInterestMapper.selectPayingTransactionInterestsByTradeNo(tradeNo, StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus());
	        if(Collections3.isNotEmpty(transactionInterests)) {
	            List<HostingPayTrade> hostingPayTrades = Lists.newArrayList();
	            String batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo(tradeNo);
	            batchPayNos.add(batchPayNo);
	            for (TransactionInterest transactionInterest : transactionInterests) {
	                //判断这笔本息有没有代付过
	            	
	                if (this.haveHostPayTradeForPayInterestAndPrincipal( transactionInterest.getId(), TypeEnum.HOSTING_PAY_TRADE_RETURN.getType())) {
	                	logger.info("[还本付息]，本息id={}已经代付过，无需重复代付",transactionInterest.getId());
	                    continue;
	                }
	                HostingPayTrade hostingPayTrade = new HostingPayTrade();
	                //当还款类型为逾期还款时，需要还款的金额应该设置为实付金额
	                BigDecimal totalAmount = BigDecimal.ZERO;
	                if(transactionInterest.getPayType() == TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_PREPAY.getType()){
	                	totalAmount = transactionInterest.getRealPayInterest().add(transactionInterest.getRealPayPrincipal()).setScale(2, BigDecimal.ROUND_HALF_UP);
	                }else{
	                	//加入添加滞纳金的代付
	                	totalAmount = transactionInterest.getRealPayInterest().add(transactionInterest.getRealPayPrincipal()).add(transactionInterest.getOverdueFine()).setScale(2, BigDecimal.ROUND_HALF_UP);
	                }
	                hostingPayTrade.setAmount(totalAmount);
	                hostingPayTrade.setBatchPayNo(batchPayNo);
	                hostingPayTrade.setPayeeId(transactionInterest.getMemberId());
//	                hostingPayTrade.setRemarks(RemarksEnum.HOSTING_PAY_TRADE_FOR_INTEREST_AND_PRINCIPAL.getRemarks()+"本息id："+transactionInterest.getId());
	                hostingPayTrade.setSourceId(transactionInterest.getId());
	                String summary = "[项目"+transactionInterest.getProjectId()+"]创建[交易本息"+transactionInterest.getId()+"]还本付息代付";
	        		hostingPayTrade.setSummary(summary);
	                hostingPayTrade.setTradeNo(SerialNumberUtil.generatePayTradeaNo(transactionInterest.getId()));
	                hostingPayTrade.setTradeStatus(TradeStatus.WAIT_PAY.name());
	                hostingPayTrade.setType(TypeEnum.HOSTING_PAY_TRADE_RETURN.getType());
	                hostingPayTrade.setProjectId(transactionInterest.getProjectId());
	                hostingPayTrades.add(hostingPayTrade);
	                //组建批量代付参数
	                if(hostingPayTrades.size()==SinaPayConfig.batchNum) {
	                    //当批量代付满300笔时提交代付请求
	                    hostingPayTradeMapper.batchInsertHostingPayTrade(hostingPayTrades);
	                    batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo(tradeNo);
	                    batchPayNos.add(batchPayNo);
	                    hostingPayTrades.clear();
	                }
	            }
	            if(Collections3.isNotEmpty(hostingPayTrades) && hostingPayTrades.size()<SinaPayConfig.batchNum) {
	            	hostingPayTradeMapper.batchInsertHostingPayTrade(hostingPayTrades);
	            }
	        }
			return batchPayNos;
		} catch (Exception e) {
			logger.error("[还本付息]-[代收回调]--创建本地批付发生异常,借款人代收号tradeNo={}",tradeNo,e);
			throw e;
		}
	}

	@Override
	public void createRemoteBatchHostingPayForRepayment(List<String> batchPayNos, Long projectId)
			throws ManagerException {
		try {
			if(Collections3.isNotEmpty(batchPayNos)) {
				for (String batchPayNo : batchPayNos) {
					// 查询该批次号在新浪是否已经创建，若创建过了，则不再创建
					if(isRemoteCreate(batchPayNo)) {
						logger.warn("批次号{}在新浪已经创建过批次了，无需再创建",batchPayNo);
						return;
					}
					List<HostingPayTrade> hostingPayTrades = hostingPayTradeMapper.selectHostPayByBatchNoAndStatus(batchPayNo, TradeStatus.WAIT_PAY.name());
					if(Collections3.isNotEmpty(hostingPayTrades)) {
						List<TradeArgs> tradeList = Lists.newArrayList();
						convertToTradeArgs(hostingPayTrades, tradeList);
						String summary = "[项目："+projectId+"]创建[批次号："+batchPayNo+"]还本付息批次代付";
						String ip = null;
						SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
						if (dict != null) {
							ip = dict.getValue();
						}
						sinaPayClient.createBatchPayTrade(batchPayNo, summary, ip, tradeList, TradeCode.PAY_TO_INVESTOR,
								BatchTradeNotifyMode.batch_notify);
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("[还本付息]-[代收回调]--创建远程批付发生异常,projectId={},batchPayNos={}",projectId,batchPayNos,e);
			throw new ManagerException(e);
		}
	}

	private boolean isRemoteCreate(String batchPayNo) {
		ResultDto<QueryTradeResult> result = sinaPayClient.queryHostingBatchTrade(batchPayNo);
		if(result!=null) {
			QueryTradeResult queryTradeResult = result.getModule();
			if(queryTradeResult!=null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
				return true;
			}
		}
		return false;
	}

	private void convertToTradeArgs(List<HostingPayTrade> hostingPayTrades,
			List<TradeArgs> tradeList) {
		//组建批量代付参数
		for (HostingPayTrade hostingPayTrade : hostingPayTrades) {
			TradeArgs tradeArgs = new TradeArgs();
			tradeArgs.setAccountType(AccountType.SAVING_POT);
			tradeArgs.setIdType(IdType.UID);
			tradeArgs.setMoney(new Money(hostingPayTrade.getAmount()));
			tradeArgs.setPayeeId(SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()));
			tradeArgs.setRemark(hostingPayTrade.getSummary());
			tradeArgs.setTradeNo(hostingPayTrade.getTradeNo());
			tradeList.add(tradeArgs);
		}
	}

	@Override
	public List<String> selectBatchPayNosByProject(Long projectId, String status)
			throws ManagerException {
		try {
			return hostingPayTradeMapper.selectBatchPayNosByProject(projectId,status, TypeEnum.HOSTING_PAY_TRADE_RETURN.getType());
		} catch (Exception e) {
			logger.error("通过项目id和状态查询批次号发生异常,projectId={},status={}",projectId,status,e);
			throw new ManagerException(e);
		}
	}

	@Override
	public List<String> selectSynchronizedBatchHostingPayTrades()
			throws ManagerException {
		try {
			return hostingPayTradeMapper.selectBatchPayNosByProject(null,TradeStatus.WAIT_PAY.name(), TypeEnum.HOSTING_PAY_TRADE_RETURN.getType());
		} catch (Exception e) {
			logger.error("查询需要同步的状态为wait_pay批次发生异常",e);
			throw new ManagerException(e);
		}
	}

	@Override
	public HostingPayTrade selectByPrimaryKey(Long sourceId) throws ManagerException {
		try {
			return hostingPayTradeMapper.selectByPrimaryKey(sourceId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Map getTotalTransferGetAmount(Long payeeId) throws ManagerException {
		try {
			return hostingPayTradeMapper.getTotalTransferGetAmount(payeeId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Map getTotalTransferFeeAmount(Long payeeId) throws ManagerException {
		try {
			return hostingPayTradeMapper.getTotalTransferFeeAmount(payeeId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingPayTrade> getTotalTransferGetList(Long payeeId) throws ManagerException {
		try {
			return hostingPayTradeMapper.getTotalTransferGetList(payeeId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingPayTrade> getTotalTransferFeeList(Long payeeId) throws ManagerException {
		try {
			return hostingPayTradeMapper.getTotalTransferFeeList(payeeId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ResultDO<Object> synTransferPay(String batchNo) {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			// 查询该批次号在新浪是否已经创建，若创建过了，则不再创建
			if (isRemoteCreate(batchNo)) {
				logger.warn("批次号{}在新浪已经创建过批次了，无需再创建", batchNo);
				return result;
			}
			List<HostingPayTrade> hostingPayTrades = hostingPayTradeMapper.selectHostPayByBatchNoAndStatus(batchNo,
					TradeStatus.WAIT_PAY.name());
			List<TradeArgs> tradeArgList = Lists.newArrayList();
			String summary = "转让项目代付";
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			for (HostingPayTrade hostingPayTrade : hostingPayTrades) {
				TradeArgs tradeArg = new TradeArgs();
				if (SerialNumberUtil.getInternalMemberId().equals(hostingPayTrade.getPayeeId())) {
					tradeArg.setAccountType(AccountType.BASIC);
					tradeArg.setIdType(IdType.EMAIL);
				} else {
					tradeArg.setAccountType(AccountType.SAVING_POT);
					tradeArg.setIdType(IdType.UID);
				}
				tradeArg.setMoney(new Money(hostingPayTrade.getAmount()));
				tradeArg.setPayeeId(SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()));
				tradeArg.setRemark(summary);
				tradeArg.setTradeNo(hostingPayTrade.getTradeNo());
				tradeArgList.add(tradeArg);
			}
			sinaPayClient.createBatchPayTrade(batchNo, summary, ip, tradeArgList, TradeCode.PAY_TO_TRANSFER_FOR_TRANSACTION_AMOUNT,
						BatchTradeNotifyMode.single_notify);
		} catch (Exception e) {
			logger.error("根据批付号重新发起转让代付失败, batchNo={}", batchNo, e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}
	
	@Override
	public BigDecimal totalMemberPayAmountByType(Long payeeId, int type) throws ManagerException {
		try {
			return hostingPayTradeMapper.totalMemberPayAmountByType(payeeId, type, TradeStatus.TRADE_FINISHED.name());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}