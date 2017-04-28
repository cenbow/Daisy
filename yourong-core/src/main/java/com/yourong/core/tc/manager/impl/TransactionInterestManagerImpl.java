package com.yourong.core.tc.manager.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang3.math.NumberUtils;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.BalanceAction;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.BatchTradeNotifyMode;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.TradeArgs;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.dao.OverdueRepayLogMapper;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.Project;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.dao.TransactionInterestMapper;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.BorrowInterestDetailBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestCollect;
import com.yourong.core.tc.model.biz.CalenderTransactionInterestDetail;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.biz.TransactionInterestDetailForCalendar;
import com.yourong.core.tc.model.biz.TransactionInterestForDate;
import com.yourong.core.tc.model.biz.TransactionInterestForMember;
import com.yourong.core.tc.model.biz.TransactionInterestForMonth;
import com.yourong.core.tc.model.biz.TransactionInterestForPay;
import com.yourong.core.tc.model.query.BorrowRepayQuery;
import com.yourong.core.tc.model.query.BorrowTransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.uc.manager.MemberOpenManager;
import com.yourong.core.uc.model.MemberOpen;

@Component
public class TransactionInterestManagerImpl implements TransactionInterestManager {
	@Autowired
	private TransactionInterestMapper transactionInterestMapper;
	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	@Autowired
	private SinaPayClient sinaPayClient;
	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;
	@Autowired
	private TransactionManager myTransactionManager;
	@Autowired
	private DebtManager debtManager;
	@Autowired
	private BalanceManager balanceManager;
	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	@Autowired
	private ProjectMapper projectMapper;
	
	@Autowired
	private OverdueRepayLogMapper overdueRepayLogMapper;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
	private MemberOpenManager memberOpenManager;
	
	
	private Logger logger = LoggerFactory.getLogger(TransactionInterestManagerImpl.class);

	
	
	
	@Override
	public Page<TransactionInterest> findByPage(Page<TransactionInterest> pageRequest,
			Map<String, Object> map) throws ManagerException {
			Page<TransactionInterest> page = new Page<TransactionInterest>();
		try {
			//针对前台传入时间，添加时分秒字段  
			if(map.containsKey("payTimeStart")){
				map.put("payTimeStart", (map.get("payTimeStart")+" 00:00:00"));
			}
			if(map.containsKey("payTimeEnd")){
				map.put("payTimeEnd", (map.get("payTimeEnd")+" 23:59:59"));
			}
			
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<TransactionInterest> transactionInterestList=  transactionInterestMapper.findByPage(map);
			int totalCount = transactionInterestMapper.findByPageCount(map);
			page.setData(transactionInterestList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	
	
	public Integer insert(TransactionInterest transactionInterest) throws ManagerException {
		try {
			int result = transactionInterestMapper.insertSelective(transactionInterest);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public TransactionInterest getTransactionInterestById(Long id) throws ManagerException {
		try {
			return transactionInterestMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	public Integer updateTransactionInterest(TransactionInterest transactionInterest)
			throws ManagerException {
		try {

			return transactionInterestMapper.updateByPrimaryKeySelective(transactionInterest);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterest> selectTransactionInterestsByQueryParams(
			TransactionInterestQuery transactionQuery) throws ManagerException {
		try {
			return transactionInterestMapper.selectTransactionInterestsByQueryParams(transactionQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<TransactionInterest> selectTransactionInterestsByQueryParamsAndOverdue(
			TransactionInterestQuery transactionQuery) throws ManagerException {
		try {
			
			List<TransactionInterest> transactionInterestList = transactionInterestMapper.selectTransactionInterestsByQueryParams(transactionQuery);
			this.getTransactionInterestOverdueInformation(transactionInterestList);
			return transactionInterestList;
			
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	private void getTransactionInterestOverdueInformation(List<TransactionInterest> transactionInterestList) throws ManagerException{
		
		for(TransactionInterest transactionInterest : transactionInterestList){
			
			BigDecimal overdueFine = BigDecimal.ZERO;
			
			//逾期未还的情况下需要计算滞纳金
			if(StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus()!=transactionInterest.getStatus()
					&&TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_OVERDUE.getType()==transactionInterest.getPayType()
					//&&transactionInterest.getOverdueFine()==null
					){
				
				String key = RedisConstant.REDIS_KEY_TRANSACTION_LATEFEE + RedisConstant.REDIS_SEPERATOR
						+ transactionInterest.getId();
				boolean isExit = RedisManager.isExitByObjectKey(key);
				//redis存在，从redis取
				if (isExit) {
					overdueFine = (BigDecimal) RedisManager.getObject(key);
					transactionInterest.setOverdueFine(overdueFine);
					continue;
				}
				
				BigDecimal unReturnPrincipalAndInterest = BigDecimal.ZERO;
				BigDecimal lateFeeRate = BigDecimal.ZERO;
				int days = 0;
				int periods= 0;
				int payingPeriods= 0;
				
				Project project = projectMapper.selectByPrimaryKey(transactionInterest.getProjectId());
				lateFeeRate = project.getLateFeeRate();
				
				//根据交易ID查询对应的未还本金和利息
				//计算对应交易本息的未还本金和利息
				TransactionInterest unreturnPrincipalAndInterest = transactionInterestMapper.selectUnreturnPrincipalAndInterestByTransactionId(transactionInterest.getTransactionId());
				//未还本息总和
				unReturnPrincipalAndInterest = unreturnPrincipalAndInterest.getPayablePrincipal().add(unreturnPrincipalAndInterest.getPayableInterest());
				
				//逾期记录表获取未还款的逾期记录信息，计算逾期天数
				List<OverdueRepayLog> overdueRepayLogList =  overdueRepayLogMapper.getOverdueRepayLogRecordByProjectIdAndType(transactionInterest.getProjectId(),
						TypeEnum.OVERDUE_SETTLEMENT_TYPE_COMMON.getType());
				for(OverdueRepayLog overdueRepayLog: overdueRepayLogList){
					if(overdueRepayLog.getRepayStatus()!=3){
						days = DateUtils.daysBetween(overdueRepayLog.getOverdueStartDate(), DateUtils.getCurrentDate())+1;
					}
				}
				//待支付的 逾期期数
				TransactionInterestQuery transactionQuery =new TransactionInterestQuery();
				transactionQuery.setTransactionId(transactionInterest.getTransactionId());
				transactionQuery.setStatus(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus());
				transactionQuery.setPayType(TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_OVERDUE.getType());
				periods = transactionInterestMapper.selectTransactionInterestsByQueryParamsTotalCount(transactionQuery);
				
				transactionQuery.setStatus(StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus());
				payingPeriods = transactionInterestMapper.selectTransactionInterestsByQueryParamsTotalCount(transactionQuery);
				periods += payingPeriods;
				
				overdueFine = FormulaUtil.getTransactionInterestLateFeeAmount(unReturnPrincipalAndInterest, lateFeeRate, days, periods);
				RedisManager.putObject(key, overdueFine);
				RedisManager.expire(key, DateUtils.calculateCurrentToEndTime());
				transactionInterest.setOverdueFine(overdueFine);
			}
		}
		
	 }

	@Override
	public List<TransactionInterest> getTransactionInterestByProjectAndDebtInterest(
			List<DebtInterest> debtInterestList,
			Project project, 
			BigDecimal investAmount,
			BigDecimal annualizedRate
			) throws ManagerException {
		///获取债权本息信息
				List<TransactionInterest> transactionInterests = new ArrayList<TransactionInterest>();
				ResultDO<TransactionInterest> result = new ResultDO<TransactionInterest>();
				try {
					if(Collections3.isNotEmpty(debtInterestList)) {
						
						BigDecimal unitPrincipal = BigDecimal.ZERO;
						for (DebtInterest debtInterest : debtInterestList) {
							//如果某一期的结束时间早于开始计息时间，则不记录本息记录
							Date startInterestDate = DateUtils.addDate(DateUtils.getCurrentDate(), project.getInterestFrom());
							if(!startInterestDate.after(debtInterest.getEndDate())) {
								TransactionInterest transactionInterest = new TransactionInterest();
								transactionInterest.setEndDate(debtInterest.getEndDate());
								transactionInterest.setStartDate(debtInterest.getStartDate());
								transactionInterest.setStatus(StatusEnum.TRANSACTION_INTEREST_NOT_PAY.getStatus());
								// 后期需要根据收益类型不同而采用不同的计算方式，目前只有按日计息，按月付息
								if(DebtEnum.RETURN_TYPE_DAY.equals(project.getProfitType())||DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
									// 单位利息
									BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(),
											investAmount, annualizedRate);
									transactionInterest.setUnitInterest(unitInterest);
									transactionInterest.setUnitPrincipal(unitPrincipal);
								}
								transactionInterests.add(transactionInterest);
							}
							
						}
						if(transactionInterests.size()>0) {
							//设置第一条记录的起息日和最后一条记录的单位本金
							TransactionInterest firstTransactionInterest = transactionInterests.get(0);
							TransactionInterest lastTransactionInterest = transactionInterests.get(transactionInterests.size()-1);
							firstTransactionInterest.setStartDate(DateUtils.addDate(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), project.getInterestFrom()));
							lastTransactionInterest.setUnitPrincipal(investAmount);
							result.setResultList(transactionInterests);
							for (TransactionInterest transactionInterest : transactionInterests) {
								//计算应付利息和本金，后期如果有其他收益方式需要根据收益方式来计算
								transactionInterest.setPayablePrincipal(transactionInterest.getUnitPrincipal());
								int days = DateUtils.daysOfTwo(transactionInterest.getStartDate(), transactionInterest.getEndDate()) + 1;
								BigDecimal value = (transactionInterest.getUnitInterest().multiply(new BigDecimal(days))).setScale(2, BigDecimal.ROUND_HALF_UP);
								transactionInterest.setPayableInterest(value);
							}
						}
					}
					return transactionInterests;
				} catch (Exception e) {
					throw new ManagerException(e);
				}
	}

	@Override
	public List<TransactionInterestForPay> selectToBePaidTransactionInterests()
			throws ManagerException {
		try {
			return transactionInterestMapper.selectToBePaidTransactionInterests();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateStatusAndRealPayForPaySuccess(int beforeStatus, int afterStatus, Long id)
			throws ManagerException {
		try {
			Map<String, Object> params = Maps.newHashMap();
			params.put("beforeStatus", beforeStatus);
			params.put("afterStatus", afterStatus);
			params.put("id", id);
			return transactionInterestMapper.updateStatusAndRealPayForPaySuccess(params);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateStatusToPayingForPayInterest(int beforeStatus, int afterStatus, Long projectId, String tradeNo) throws ManagerException  {
		try {
			return transactionInterestMapper.updateStatusToPayingForPayInterest(beforeStatus,afterStatus,projectId, tradeNo);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<HostingCollectTrade> afterHostingCollectTradeForPayInterestAndPrincipal(String tradeNo,
			String outTradeNo, String tradeStatus) throws Exception {
		ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
		try {
//			StopWatch sw = new StopWatch();
//			sw.start();
    // 	logger.info("代收完成后执行还本付息开始，tradeNo="+tradeNo);
			HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
			if(hostingCollectTrade!=null) {
				//如果是最终状态，则直接返回
				if(hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
						||hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
						||hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())) {
					logger.info("原始债权人还款代收已经是最终状态，tradeNo="+tradeNo);
					result.setSuccess(false);
					return result;
				}
				//将交易状态置为最终状态
				hostingCollectTrade.setTradeStatus(tradeStatus);
				hostingCollectTrade.setOutTradeNo(outTradeNo);
				hostingCollectTradeManager.updateHostingCollectTrade(hostingCollectTrade);
				//如果交易为交易成功状态
				if(TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
					//同步余额，并且记录资金流水
					//写流水
					//资金流水的备注
					//TODO 资金流水备注写入方式需要调整
					Project project = projectMapper.selectByPrimaryKey(hostingCollectTrade.getSourceId());
					Object[] param = {"",""};
					if(project!=null){
						param[0] = StringUtil.getShortProjectName(project.getName());
					}
					String ids = hostingCollectTrade.getTransactionInterestIds();
					if(ids!=null){
						String[] idArray = ids.split("\\^");						
						boolean resul = NumberUtils.isNumber(idArray[0]);
						if(resul&& StringUtil.isNotBlank(idArray[0])){
							TransactionInterest transactionInterest = transactionInterestMapper.selectByPrimaryKey(Long.valueOf(idArray[0]));
							if(transactionInterest!=null){
								String period = transactionInterestMapper.queryRepayPeriods(transactionInterest.getTransactionId(), transactionInterest.getEndDate());
								if(StringUtil.isNotBlank(period) ){
									if(transactionInterest.getPayablePrincipal().compareTo(BigDecimal.ZERO)>0){
										param[1] = "本金与（"+period+"）利息";
									}else{
										param[1] = "（"+period+"）利息";
									}
								}
							}
						}
						
					}
					String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_RETURN.getRemarks(), param);
					Balance balance = balanceManager.synchronizedBalance(hostingCollectTrade.getPayerId(), TypeEnum.BALANCE_TYPE_PIGGY);
					//记录用户投资资金流水
					capitalInOutLogManager.insert(
							hostingCollectTrade.getPayerId(), 
							TypeEnum.FINCAPITALINOUT_TYPE_RETURN, 
							null, 
							hostingCollectTrade.getAmount(), 
							balance.getAvailableBalance(), 
							hostingCollectTrade.getId().toString(), 
							remark,
							TypeEnum.BALANCE_TYPE_PIGGY
							);
				}
				// 如果是原始债权人代收失败，则将本息状态回滚
//				if(TradeStatus.TRADE_FAILED.name().equals(tradeStatus)
//						||TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)) {
//					this.updateStatusForPayInterest(
//							StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus(),
//							StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus(),
//							hostingCollectTrade.getTradeNo()
//							);
//				}
	//			logger.info("代收完成后执行还本付息结束，总共耗时：" + sw.getTime()+",tradeNo=" + tradeNo);
			}
			return result;
		} catch (Exception e) {
			logger.error("代收完成后执行还本付息发生异常，tradeNo：" +tradeNo, e);
			result.setSuccess(false);
			throw e;
		}
	}
	
	@Override
	public void processPayInterestAndPrincipal(String tradeNo) throws Exception  {
		//TODO 锁表操作， 通过项目id查询出代付的本息记录
		List<TransactionInterest> transactionInterests = transactionInterestMapper.selectPayingTransactionInterestsByTradeNo(tradeNo,StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus());
		String ip = null;
		SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
		if (dict != null) {
			ip = dict.getValue();
		}
		if(Collections3.isNotEmpty(transactionInterests)) {
			List<TradeArgs> tradeList = Lists.newArrayList();
			String batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo(tradeNo);
			String summary = "代收交易:"+tradeNo+"还本付息";
			for (TransactionInterest transactionInterest : transactionInterests) {
				// 本地创建代付交易hostingPayTrade
				//判断这笔本息有没有代付过
				if(hostingPayTradeManager.haveHostPayTradeForPayInterestAndPrincipal(
						transactionInterest.getId(), 
						TypeEnum.HOSTING_PAY_TRADE_RETURN.getType())) {
					break ;
				}
				HostingPayTrade hostingPayTrade = new HostingPayTrade();
				hostingPayTrade.setUserIp(ip);
				createHostingPayTrade(transactionInterest, batchPayNo, hostingPayTrade);
				//组建批量代付参数
				
				TradeArgs tradeArgs = new TradeArgs();
				tradeArgs.setAccountType(AccountType.SAVING_POT);
				tradeArgs.setIdType(IdType.UID);
				tradeArgs.setMoney(new Money(hostingPayTrade.getAmount()));
				tradeArgs.setPayeeId(SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()));
				tradeArgs.setRemark(hostingPayTrade.getSummary());
				tradeArgs.setTradeNo(hostingPayTrade.getTradeNo());
				tradeList.add(tradeArgs);
				if(tradeList.size()==100) {
					//当批量代付满100笔时提交代付请求
					try {
						sinaPayClient.createBatchPayTrade(batchPayNo, summary,ip, tradeList, TradeCode.PAY_TO_INVESTOR, BatchTradeNotifyMode.single_notify);
					} catch (Exception e) {
						logger.error("代收成功处理还本付息代付发生异常",e);
					}
					batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo(tradeNo);
					tradeList.clear();
				}
			}
			if(Collections3.isNotEmpty(tradeList) && tradeList.size()<100) {
				try {
					sinaPayClient.createBatchPayTrade(batchPayNo, summary,ip, tradeList, TradeCode.PAY_TO_INVESTOR, BatchTradeNotifyMode.single_notify);
				} catch (Exception e) {
					logger.error("代收成功处理还本付息代付发生异常",e);
				}
			}
		}
		
	}

	private Integer createHostingPayTrade(TransactionInterest transactionInterest, String batchPayNo, HostingPayTrade hostingPayTrade) throws ManagerException {
		BigDecimal totalAmount = transactionInterest.getPayableInterest().add(transactionInterest.getPayablePrincipal()).setScale(2, BigDecimal.ROUND_HALF_UP);
		hostingPayTrade.setAmount(totalAmount);
		hostingPayTrade.setBatchPayNo(batchPayNo);
		hostingPayTrade.setPayeeId(transactionInterest.getMemberId());
		hostingPayTrade.setRemarks(RemarksEnum.HOSTING_PAY_TRADE_FOR_INTEREST_AND_PRINCIPAL.getRemarks()+"本息id："+transactionInterest.getId());
		hostingPayTrade.setSourceId(transactionInterest.getId());
//		hostingPayTrade.setSummary(summary);
		hostingPayTrade.setTradeNo(SerialNumberUtil.generatePayTradeaNo(transactionInterest.getId()));
		hostingPayTrade.setTradeStatus(TradeStatus.WAIT_PAY.name());
		hostingPayTrade.setType(TypeEnum.HOSTING_PAY_TRADE_RETURN.getType());
		return hostingPayTradeManager.insertSelective(hostingPayTrade);
	}
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	@Override
	public ResultDO<TransactionInterest> afterPayInterestAndPrincipal(String tradeStatus,
			String outTradeNo) throws Exception {
		ResultDO<TransactionInterest> result = new ResultDO<TransactionInterest>();
		try {
			HostingPayTrade payTrade = hostingPayTradeManager.getByTradeNo(outTradeNo);
			HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByIdForLock(payTrade.getId());
			//如果是最终状态，则直接返回
			if(hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
					||hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())) {
				logger.info("同步代付交易后处理支付本息已经是最终状态，tradeNo="+outTradeNo);
				return result;
			}
			//将交易状态置为最终状态
			hostingPayTrade.setTradeStatus(tradeStatus);
			hostingPayTrade.setOutTradeNo(outTradeNo);
			hostingPayTradeManager.updateHostingPayTrade(hostingPayTrade);
			if(hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())) {
				//将本息记录置为已支付状态
				int updateStatus = this.updateStatusAndRealPayForPaySuccess(StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus(), StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus(), hostingPayTrade.getSourceId());
				if (updateStatus==0){
					logger.info("本息记录已经更新过了，id={}",hostingPayTrade.getSourceId());
					return result;
				}
				TransactionInterest transactionInterest = this.getTransactionInterestById(hostingPayTrade.getSourceId());
				//更新交易表中已收本金和已收利息
				if(transactionInterest!=null) {
					Transaction transaction = myTransactionManager.selectTransactionByIdLock(transactionInterest.getTransactionId());
					transaction.setReceivedInterest(transactionInterest.getRealPayInterest().add(transaction.getReceivedInterest()).setScale(2, BigDecimal.ROUND_HALF_UP));
					transaction.setReceivedPrincipal(transactionInterest.getRealPayPrincipal().add(transaction.getReceivedPrincipal()).setScale(2, BigDecimal.ROUND_HALF_UP));
					//判断项目是否是已还款，如果是已还款更新交易状态
					Project project = projectMapper.selectByPrimaryKey(transaction.getProjectId());
					if(project!=null) {
						if(project.getStatus()==StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()) {
							transaction.setStatus(StatusEnum.TRANSACTION_COMPLETE.getStatus());
						}
					}
					myTransactionManager.updateByPrimaryKeySelective(transaction);
					
					//发送利息支付通知
					if(transactionInterest.getPayableInterest()!=null && transactionInterest.getPayableInterest().doubleValue()>0) {
						//资金流水备注
						Object[] param = {"",""};
						if(project!=null){
							param[0] =StringUtil.getShortProjectName(project.getName()); 
						}
						String repayPeriods = transactionInterestMapper.queryRepayPeriods(transactionInterest.getTransactionId(), transactionInterest.getEndDate());
						if(StringUtil.isNotBlank(repayPeriods)){
							param[1] = repayPeriods;
						}
						String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_INTEREST.getRemarks(), param);
						//写流水
						Balance balance = balanceManager.synchronizedBalance(transactionInterest.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
						//记录用户投资资金流水
						capitalInOutLogManager.insert(
								transactionInterest.getMemberId(), 
								TypeEnum.FINCAPITALINOUT_TYPE_INTEREST, 
								transactionInterest.getPayableInterest(), 
								null, 
								balance.getAvailableBalance(), 
								transactionInterest.getId().toString(), 
								remark,
								TypeEnum.BALANCE_TYPE_PIGGY
								);
					}
					//发送本金支付通知
					if(transactionInterest.getPayablePrincipal()!=null && transactionInterest.getPayablePrincipal().doubleValue()>0) {
						//资金流水备注
						String remark = "";
						if(project!=null){
							remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_PRINCIPAL.getRemarks(), StringUtil.getShortProjectName(project.getName()));
						}
						//写流水
						Balance balance = balanceManager.synchronizedBalance(transactionInterest.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
						//记录用户投资资金流水
						capitalInOutLogManager.insert(
								transactionInterest.getMemberId(), 
								TypeEnum.FINCAPITALINOUT_TYPE_PRINCIPAL, 
								transactionInterest.getPayablePrincipal(), 
								null, 
								balance.getAvailableBalance(), 
								transactionInterest.getId().toString(), 
								remark,
								TypeEnum.BALANCE_TYPE_PIGGY
								);
					}
					result.setResult(transactionInterest);
					MessageClient.sendMsgForInterestOrCapital(DateUtils.getCurrentDate(), transactionInterest.getTransactionId(), transactionInterest.getPayableInterest(), transactionInterest.getPayablePrincipal(), transactionInterest.getMemberId());
					
			}
			}
			return result;
		} catch (Exception e) {
			throw e;
		}
		
	}

	@Override
	public BigDecimal getExpectAmount(Project project, BigDecimal  investAmount, BigDecimal annualizedRate , BigDecimal extraAnnualizedRate) throws Exception {
		List<DebtInterest> debtInterests = debtManager.getFullDebtInfoById(project.getDebtId()).getDebtInterests();
		BigDecimal expectAmount = BigDecimal.ZERO;
		if(Collections3.isNotEmpty(debtInterests)) {
			int totalPeriod = debtInterests.size();
			int period = 0;
			Date startInterestDate = DateUtils.addDate(DateUtils.formatDate(DateUtils.getCurrentDate(),  DateUtils.DATE_FMT_3), project.getInterestFrom());
			for (DebtInterest debtInterest : debtInterests) {
				//计算总的交易本息期数
				if(startInterestDate.after(debtInterest.getEndDate())){
					totalPeriod = totalPeriod - 1;
				}
				// 如果某一期的结束时间早于开始计息时间，则不记录本息记录
				if(!startInterestDate.after(debtInterest.getEndDate())){
					int days = 0;
					if(DateUtils.isDateBetween(startInterestDate, debtInterest.getStartDate(), debtInterest.getEndDate())) {
						days = DateUtils.daysOfTwo(startInterestDate, debtInterest.getEndDate()) + 1;
					} else {
						days = DateUtils.daysOfTwo(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
					}
					// 后期需要根据收益类型不同而采用不同的计算方式，目前只有按日计息，按月付息
					if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())||DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
						// 单位利息
						if(extraAnnualizedRate!=null) {
							annualizedRate = annualizedRate.add(extraAnnualizedRate).setScale(2, BigDecimal.ROUND_HALF_UP);
						}
						BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(),
										investAmount, annualizedRate);
						BigDecimal value = (unitInterest.multiply(new BigDecimal(days))).setScale(2, BigDecimal.ROUND_HALF_UP);
						expectAmount = expectAmount.add(value);
					}else if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode()// 还款方式：等本等息 
							.equals(project.getProfitType())) {
						if(extraAnnualizedRate!=null) {
							annualizedRate = annualizedRate.add(extraAnnualizedRate).setScale(2, BigDecimal.ROUND_HALF_UP);
						}
						BigDecimal interest = BigDecimal.ZERO;
						interest = FormulaUtil.getTransactionInterest(project.getProfitType(),
								investAmount,
								annualizedRate,
								period,
								debtInterest.getStartDate(),
								startInterestDate,
								debtInterest.getEndDate());//应付利息
						expectAmount = expectAmount.add(interest);
					}
					period = period + 1;
				}
			}
		}
		return expectAmount;
	}

	@Override
	public int updateStatusForPayInterest(int beforeStatus, int afterStatus,
			String tradeNo) throws ManagerException {
		try {
			return transactionInterestMapper.updateStatusForPayInterest(beforeStatus,afterStatus,tradeNo);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public synchronized ResultDO<HostingCollectTrade> afterHostingCollectTradeCouponBForPayInterestAndPrincipal(
			String tradeNo, String outTradeNo, String tradeStatus) {
		ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			logger.info("平台收益权垫付代收回调开始，tradeNo="+tradeNo);
			HostingCollectTrade hostingCollectTradeUnLock = hostingCollectTradeManager.getByTradeNo(tradeNo);
			if(hostingCollectTradeUnLock!=null) {
				HostingCollectTrade hostingCollectTradeForLock = hostingCollectTradeManager.getByIdForLock(hostingCollectTradeUnLock.getId());
				if(hostingCollectTradeForLock!=null) {
					//如果是最终状态，则直接返回
					if(hostingCollectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
							||hostingCollectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
							||hostingCollectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())) {
						logger.info("平台收益权垫付代收已经是最终状态，tradeNo="+tradeNo);
						result.setSuccess(false);
						return result;
					}
					//将交易状态置为最终状态
					hostingCollectTradeForLock.setTradeStatus(tradeStatus);
					hostingCollectTradeForLock.setOutTradeNo(outTradeNo);
					hostingCollectTradeManager.updateHostingCollectTrade(hostingCollectTradeForLock);
					//如果交易为交易成功状态
					if(TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
						//写流水
						Balance balance = balanceManager.reduceBalance(TypeEnum.BALANCE_TYPE_BASIC, hostingCollectTradeForLock.getAmount(), Long.parseLong(Config.internalMemberId), BalanceAction.balance_subtract_Available_subtract);
						//记录用户投资资金流水
						capitalInOutLogManager.insert(
								Long.parseLong(Config.internalMemberId), 
								TypeEnum.FINCAPITALINOUT_TYPE_EARNINGS, 
								null, 
								hostingCollectTradeForLock.getAmount(), 
								balance.getAvailableBalance(), 
								hostingCollectTradeForLock.getId().toString(), 
								null,
								TypeEnum.BALANCE_TYPE_BASIC
								);
					}
					//失败或者关闭，需要继续创建代收
					if(TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)
							||TradeStatus.TRADE_FAILED.name().equals(tradeStatus)) {
						
					}
					logger.info("平台收益权垫付代收回调结束，总共耗时：" + sw.getTime()+",tradeNo=" + tradeNo);
				}
			}
			
			return result;
		} catch (Exception e) {
			logger.error("平台收益权垫付代收回调发生异常，tradeNo：" +tradeNo, e);
			result.setSuccess(false);
			return result;
		}
	}

	@Override
	public TransactionInterest queryLastPayTransactionInterest(Long memberId)
			throws Exception {
		try {
			return transactionInterestMapper.queryLastPayTransactionInterest(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterest> queryInterestByProjectId(Long projectId)
			throws ManagerException {
		try {
			return transactionInterestMapper.queryInterestByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 查询待支付 项目id 集合
	 * @return
	 */
	@Override
	public List<Long> queryToBePaidTransactionInterests()
			throws ManagerException {
		try {
			return transactionInterestMapper.queryToBePaidTransactionInterests();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<TransactionInterestForMember> queryTransactionInterestForMember(
			TransactionInterestQuery query) throws ManagerException {
		List<TransactionInterestForMember> list = Lists.newArrayList();
		int count = transactionInterestMapper.queryTransactionInterestForMemberTotalCount(query);
		if(count > 0){
			list = transactionInterestMapper.queryTransactionInterestForMember(query);
		}
		Page<TransactionInterestForMember> page = new Page<TransactionInterestForMember>();
		page.setiTotalDisplayRecords(count);
		page.setPageNo(query.getCurrentPage());
		page.setiDisplayLength(query.getPageSize());
		page.setiTotalRecords(count);
		page.setData(list);
		return page;
	}
	
	@Override
	public Page<TransactionInterestForMember> p2pQueryTransactionInterestForMember(
			TransactionInterestQuery query) throws ManagerException {
		List<TransactionInterestForMember> list = Lists.newArrayList();
		int count = transactionInterestMapper.p2pQueryTransactionInterestForMemberTotalCount(query);
		if(count > 0){
			list = transactionInterestMapper.p2pQueryTransactionInterestForMember(query);
		}
		Page<TransactionInterestForMember> page = new Page<TransactionInterestForMember>();
		page.setiTotalDisplayRecords(count);
		page.setPageNo(query.getCurrentPage());
		page.setiDisplayLength(query.getPageSize());
		page.setiTotalRecords(count);
		page.setData(list);
		return page;
	}
	

	@Override
	public BigDecimal totalMemberExtraInterest(Long memberId)
			throws ManagerException {
		try {
			BigDecimal totalExtraInterest = transactionInterestMapper.totalMemberExtraInterest(memberId);
			if(totalExtraInterest == null){
				return BigDecimal.ZERO;
			}
			return totalExtraInterest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterestDetailForCalendar> getMemberInterestDetailByDate(
			Map<String,Object> map) throws ManagerException {
		try {
			return transactionInterestMapper.getMemberInterestDetailByDate(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<CalenderTransactionInterestDetail> getMemberInterestDetailByDateNew(
			Map<String,Object> map) throws ManagerException {
		try {
			return transactionInterestMapper.getMemberInterestDetailByDateNew(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer getCurrentInterestNum(Long memberId, Long transactionId,
			String endDate) throws ManagerException {
		try {
			Integer num = transactionInterestMapper.getCurrentInterestNum(memberId, transactionId, endDate);
			if(num == null){
				return 0;
			}
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer getTotalInterestNum(Long memberId, Long transactionId)
			throws ManagerException {
		try {
			Integer num = transactionInterestMapper.getTotalInterestNum(memberId, transactionId);
			if(num == null){
				return 0;
			}
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterestForMember> getTransactionInterestDetailForMember(
			TransactionInterestQuery query) throws ManagerException{
		try {
			System.out.println("query.getStatus()"+query.getStatus());
			return transactionInterestMapper.getTransactionInterestDetailForMember(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterestForPay> selectAllProjectToBePaidTransactionInterests() throws ManagerException {
		try {
			return transactionInterestMapper.selectAllProjectToBePaidTransactionInterests();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int selectTransactionInterestsCountByTransactionId(Long transactionId) throws ManagerException {
		try {
			TransactionInterestQuery query = new TransactionInterestQuery();
			query.setTransactionId(transactionId);
			return transactionInterestMapper.selectTransactionInterestsByQueryParamsTotalCount(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterest> selectTransactionInterestsByQueryParamsFilterP2p(TransactionInterestQuery transactionQuery)
			throws ManagerException {
		try {
			return transactionInterestMapper.selectTransactionInterestsByQueryParamsFilterP2p(transactionQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}




	@Override
	public List<TransactionInterestForMember> getTransactionInterestDetailForMemberFilterP2P(TransactionInterestQuery query)
			throws ManagerException {
		try {
			System.out.println("query.getStatus()"+query.getStatus());
			return transactionInterestMapper.getTransactionInterestDetailForMemberFilterP2P(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<TransactionInterest> queryInterestAndPrincipalFlagByProjectId(Long projectId)
			throws ManagerException {
		try {
			return transactionInterestMapper.queryInterestAndPrincipalFlagByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterestForMonth> selectTransactionInterestsByMonth(Long memberId, String startYM, String endYM)
			throws ManagerException {
		try {
			TransactionInterestQuery query = new TransactionInterestQuery();
			query.setMemberId(memberId);
			query.setStartYM(startYM);
			query.setEndYM(endYM);
			return transactionInterestMapper.selectTransactionInterestsByMonth(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterestForDate> selectTransactionInterestsByEndDate(Long memberId, String startYM, String endYM)
			throws ManagerException {
		try {
			TransactionInterestQuery query = new TransactionInterestQuery();
			query.setMemberId(memberId);
			query.setStartYM(startYM);
			query.setEndYM(endYM);
			return transactionInterestMapper.selectTransactionInterestsByEndDate(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Integer ifPayByEndDateAndMemberId(Map<String, Object> map)
			throws ManagerException {
		try {
			Integer num = transactionInterestMapper.ifPayByEndDateAndMemberId(map);
			if(num == null){
				return 0;
			}
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<String> selectCurrentDateCollectTradeNos(Integer status)
			throws ManagerException {
		try {
			return transactionInterestMapper.selectCurrentDateCollectTradeNos(status);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterestForPay> selectAllProjectToBePrePaidTransactionInterests() throws ManagerException {
		try {
			return transactionInterestMapper.selectAllProjectToBePrePaidTransactionInterests();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * @Description:交易对应未还款的本息个数
	 * @return
	 * @author: fuyili
	 * @time:2016年5月23日 上午10:27:32
	 */
	@Override
	public int getCountUnReturnTransationInterestByTransationId(Long transactionId)throws ManagerException {
		try {
			return transactionInterestMapper.getCountTransationInterestByTransationIdAndStatus(transactionId,StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	/**
	 * @desc 项目本息对应未还款的本息个数
	 * @return
	 * @throws ManagerException
	 * @author fuyili
	 * @time 2016年5月24日 下午1:39:01
	 *
	 */
	@Override
	public int getCountUnReturnTransationInterestByProjectInterestId(Long interestId)
			throws ManagerException {
		try {
			return transactionInterestMapper.getCountByProjectInterestNotStatus(interestId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * @Description:获取还本付息最后一期
	 * @return
	 * @author: zhanghao
	 * @time:2016年6月1日 下午14:00:45
	 */
	@Override
	public TransactionInterest getPreTransactionInterest(Map<String,Object> map)
			throws ManagerException {
		try {
			return transactionInterestMapper.getPreTransactionInterest(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterestForPay> selectAllProjectToBeOverPaidTransactionInterests() throws ManagerException {
		try {
			return transactionInterestMapper.selectAllProjectToBeOverPaidTransactionInterests();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int updateStatusForPaySuccess(int beforeStatus, int afterStatus, Long id)
			throws ManagerException {
		try {
			return transactionInterestMapper.updateStatusForPaySuccess(beforeStatus,afterStatus,id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public TransactionInterest getRealPayByInterestId(Long interestId) throws ManagerException {
		try {
			return transactionInterestMapper.getRealPayByInterestId(interestId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<BorrowRepayInterestBiz> findPageBorrowRepayInterest(BorrowRepayQuery query) {
		Page<BorrowRepayInterestBiz> page = new Page<BorrowRepayInterestBiz>();
		Map<String,Object> map=new HashMap<>();
		List<BorrowRepayInterestBiz> borrowRepayInterestBizs=  transactionInterestMapper.findByPageBorrowRepayInterest(query);
		for (BorrowRepayInterestBiz borrowbiz:borrowRepayInterestBizs) {
			List<Long> pids=new ArrayList<>();
			Set<String> openPlatformKey = Sets.newHashSet();
			try {
				String[] strings=borrowbiz.getProjectIds().split(",");
				for (String s:strings) {
                    if (!StringUtils.isEmpty(s)){
                        pids.add(Long.parseLong(s));
                        
                        Project project = projectMapper.selectByPrimaryKey(Long.parseLong(s));
        				
                        if(StringUtils.isNotEmpty(project.getOpenPlatformKey())){
                        	try {
                				SysDict openDict = sysDictManager.findByGroupNameAndKey("channel_key",  project.getOpenPlatformKey());
                				openPlatformKey.add(openDict.getLabel());
            				} catch (ManagerException e) {
            					logger.error("借款人还本付息列表，查询借款人渠道异常，memberId：" +borrowbiz.getBorrowerId(), e);
            				}
                        }
                    }
                }
			} catch (NumberFormatException e) {
				pids.clear();
			}
			try {
				int waitnum = transactionInterestMapper.selectWaitRepayNum(query.getEndDate(),pids);
				borrowbiz.setWaitRepayNum(waitnum);
			} catch (NumberFormatException e) {
				borrowbiz.setWaitRepayNum(0);
			}
			try {
				int endnum=transactionInterestMapper.selectEndRepayNum(query.getEndDate(),pids);
				borrowbiz.setEndRepayNum(endnum);
			} catch (NumberFormatException e) {
				borrowbiz.setEndRepayNum(0);
			}
			
			for (String str : openPlatformKey) {  
				if(StringUtils.isNotEmpty(str)){
					borrowbiz.setBorrowerPlatformKey((borrowbiz.getBorrowerPlatformKey()!=null?borrowbiz.getBorrowerPlatformKey():"")+str);
				}
				
			}  
			
		}
		int totalCount = transactionInterestMapper.findByPageCountBorrowRepayInterest(query);
		page.setData(borrowRepayInterestBizs);
		page.setiTotalDisplayRecords(totalCount);
		page.setiTotalRecords(totalCount);
		return page;
	}

	@Override
	public BorrowRepayInterestCollect findBorrowRepayInterestCollect(Date date) {
		BorrowRepayInterestCollect borrowRepayInterestCollect=new BorrowRepayInterestCollect();
		borrowRepayInterestCollect=transactionInterestMapper.findByDayCollectBorrowRepayInterest(date);
		BorrowRepayQuery query=new BorrowRepayQuery();
		query.setEndDate(date);
		List<BorrowRepayInterestBiz> borrowRepayInterestBizs=  transactionInterestMapper.findByPageBorrowRepayInterest(query);
		int waitnum= transactionInterestMapper.selectDayCollectWaitRepayNum(date);
		if (borrowRepayInterestCollect!=null){
			borrowRepayInterestCollect.setWaitRepayNum(waitnum);
		}
		return borrowRepayInterestCollect;
	}

	@Override
	public Page<BorrowInterestDetailBiz> findDayBorrowRepayInterestByBorrowId(BorrowTransactionInterestQuery query,String projectids) {
		Page<BorrowInterestDetailBiz> page = new Page<BorrowInterestDetailBiz>();
		if (!StringUtils.isEmpty(projectids)){
			List<Long> pids=new ArrayList<>();
			String[] strings=projectids.split(",");
			for (String s:strings) {
				if (!StringUtils.isEmpty(s)){
					pids.add(Long.parseLong(s));
				}
			}
			List<BorrowInterestDetailBiz> borrowInterestDetailBizs= transactionInterestMapper.selectDayInterestByBorrerId(query,pids);
			int totalCount = transactionInterestMapper.selectCountDayInterestByBorrerId(query,pids);
			page.setData(borrowInterestDetailBizs);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
		}
		return page;
	}

	@Override
	public Integer updateByTransactionId(TransactionInterest transactionInterest) throws ManagerException {
		try {
			return transactionInterestMapper.updateByTransactionId(transactionInterest);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterest> selectToTransferInterestByTransactionId(Long transactionId)
			throws ManagerException {
		try {
			return transactionInterestMapper.selectToTransferInterestByTransactionId(
					StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus(), transactionId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public BigDecimal totalTransferAmount(Long memberId) throws ManagerException {
		try {
			return transactionInterestMapper.totalTransferAmount(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public BigDecimal totalMemberExtraInterestT(Long memberId)
			throws ManagerException {
		try {
			BigDecimal totalExtraInterest = transactionInterestMapper.totalMemberExtraInterestT(memberId);
			if(totalExtraInterest == null){
				return BigDecimal.ZERO;
			}
			return totalExtraInterest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}




	@Override
	public Integer totalTransferAmountNum(Long memberId) throws ManagerException {
		try {
			return transactionInterestMapper.totalTransferAmountNum(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterest> queryEarlyInterest(TransactionInterestQuery transactionQuery)
			throws ManagerException {
		try {
			return transactionInterestMapper.queryEarlyInterest(transactionQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public TransactionInterest selectByPrimaryKeyForLock(Long id) throws ManagerException {
		try {
			return transactionInterestMapper.selectByPrimaryKeyForLock(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public TransactionForMemberCenter queryTotalInterestByTransactionId(Long transactionId) {
		return transactionInterestMapper.queryTotalInterestByTransactionId(transactionId);
	}
}