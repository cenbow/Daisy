package com.yourong.core.tc.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.yourong.common.cache.*;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.*;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.*;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.TradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateSinglePayTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.RefundTradeResult;
import com.yourong.common.util.*;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.cache.RedisForProjectClient;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.dao.LoanDetailMapper;
import com.yourong.core.fin.manager.*;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.LoanDetail;
import com.yourong.core.fin.model.ProjectFee;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.ic.dao.*;
import com.yourong.core.ic.manager.*;
import com.yourong.core.ic.model.*;
import com.yourong.core.ic.model.biz.TransactionTransferRecordBiz;
import com.yourong.core.ic.model.biz.TransferRateList;
import com.yourong.core.ic.model.biz.TransferRecordBiz;
import com.yourong.core.ic.model.biz.TransferRecordListBiz;
import com.yourong.core.ic.model.query.TransferRecordQuery;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultNewMapper;
import com.yourong.core.mc.dao.ActivityRuleMapper;
import com.yourong.core.mc.manager.*;
import com.yourong.core.mc.model.*;
import com.yourong.core.mc.model.biz.*;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.dao.*;
import com.yourong.core.tc.manager.*;
import com.yourong.core.tc.model.*;
import com.yourong.core.tc.model.biz.*;
import com.yourong.core.tc.model.query.*;
import com.yourong.core.uc.manager.AutoInvestLogManager;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;
import org.apache.commons.lang.time.StopWatch;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rop.thirdparty.com.google.common.collect.Maps;
import rop.thirdparty.org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;


@Component
public class TransactionManagerImpl implements TransactionManager {
	@Autowired
	private TransactionMapper transactionMapper;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private DebtManager debtManager;

	@Autowired
	private CouponManager couponManager;

	@Autowired
	private DebtTransferManager debtTransferManager;

	@Autowired
	private OrderManager orderManager;

	@Autowired
	private TransactionInterestManager transactionInterestManager;

	@Autowired
	private TransactionInterestMapper transactionInterestMapper;

	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;

	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;

	@Autowired
	private SinaPayClient sinaPayClient;

	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private LoanDetailMapper loanDetailMapper;

	@Autowired
	private ActivityManager activityManager;

	@Autowired
	private ActivityRuleMapper activityRuleMapper;

	@Autowired
	private ContractManager contractManager;

	@Resource
	private DebtCollateralManager debtCollateralManager;

	@Autowired
	private PreservationManager preservationManager;

	@Resource
	private BscAttachmentManager bscAttachmentManager;

	@Resource
	private AttachmentIndexManager attachmentIndexManager;

	@Resource
	private LvGouManager lvGouManager;

	@Resource
	private MemberInfoManager memberInfoManager;

	@Autowired
    private EnterpriseManager enterpriseManager;

	
	@Autowired
	private SysDictManager sysDictManager;

	@Resource
	private ActivityLotteryManager activityLotteryManager;

	@Autowired
	private LeaseBonusDetailMapper leaseBonusDetailMapper;

	@Autowired
	private TransactionMapper myTransactionManager;

	@Autowired
	private ActivityAfterTransactionManager activityAfterTransactionManager;
	
	@Autowired
	private DebtInterestMapper debtInterestMapper;
	
	@Autowired
	private HostingRefundMapper hostingRefundMapper;
	
	@Autowired
	private DouwanManager douwanManager;
	
	@Autowired
	private ProjectFeeManager projectFeeManager;
	
	@Autowired
	private HostingRefundManager hostingRefundManager;

	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;

	@Autowired
	private ActivityLotteryResultNewMapper activityLotteryResultNewMapper;

	@Autowired
	private ContractCoreManager contractCoreManager;
	
	@Autowired
	private ContractSignManager contractSignManager;
	
	@Autowired
	private ProjectMapper projectMapper;
	
	@Autowired
	private HostingCollectTradeAuthManager hostingCollectTradeAuthManager;
	
	@Autowired
	private AutoInvestLogManager autoInvestLogManager;
	
	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Autowired
	private TransferProjectMapper transferProjectMapper;
	
	@Autowired
	private OverdueLogManager overdueLogManager;
	
	@Autowired 
	private ProjectExtraManager projectExtraManager;
	
	@Autowired
	private ProjectExtraMapper projectExtraMapper;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

	@Autowired
	private MemberRepaymentMapper memberRepaymentMapper;
	
	@Autowired
	private MemberHistoryRepaymentMapper memberHistoryRepaymentMapper;

	@Autowired
	private ContractSignMapper contractSignMapper;
	private Logger logger = LoggerFactory.getLogger(TransactionManagerImpl.class);

	public Transaction selectTransactionById(Long id) throws ManagerException {
		try {
			return transactionMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKeySelective(Transaction transaction) throws ManagerException {
		try {

			return transactionMapper.updateByPrimaryKeySelective(transaction);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<TransactionForProject> selectTransactionForProjectsForPage(TransactionQuery transactionQuery) throws ManagerException {
		try {
			int totalCount = transactionMapper.selectTransactionForProjectsByQueryParamsTotalCount(transactionQuery);
			Page<TransactionForProject> page = new Page<TransactionForProject>();
			if (totalCount > 0) {
				// 不分页
				if (transactionQuery.getPageSize() <= 0) {
					transactionQuery.setPageSize(totalCount);
				}
				page.setData(transactionMapper.selectTransactionForProjectsByQueryParams(transactionQuery));
			} else {
				List<TransactionForProject> list = Lists.newArrayList();
				page.setData(list);
			}
			for (TransactionForProject transaction : page.getData()) {
				Order order = orderManager.selectByPrimaryKey(transaction.getOrderId());
				if (order != null && order.getOrderSource() != null) {
					transaction.setOrderSource(order.getOrderSource());
				}
			}
			page.setPageNo(transactionQuery.getCurrentPage());
			page.setiDisplayLength(transactionQuery.getPageSize());
			page.setiTotalRecords(totalCount);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Transaction> selectTransactionsByQueryParams(TransactionQuery transactionQuery) throws ManagerException {
		try {
			return transactionMapper.selectTransactionsByQueryParams(transactionQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer insert(Transaction transaction) throws ManagerException {
		try {
			return transactionMapper.insertSelective(transaction);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionForProject> selectNewTransactions(int pageSize) throws ManagerException {
		try {
			return transactionMapper.selectNewTransactions(pageSize);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer updateReceivedInterestAndPrincipal(TransactionQuery transactionQuery) throws ManagerException {
		try {
			return transactionMapper.updateReceivedInterestAndPrincipal(transactionQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Transaction> afterTransactionCollectNotifyProcess(String tradeNo, String outTradeNo, String tradeStatus)
			throws Exception {
		ResultDO<Transaction> result = new ResultDO<Transaction>();
		try {
			Order orderUnLock = orderManager.getOrderByTradeNo(tradeNo);
			if (orderUnLock == null) {
				return result;
			}
			// 债权项目记录一羊领头数据(转让项目不参加)
			Project project = projectManager.selectByPrimaryKey(orderUnLock.getProjectId());
			if (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)
					&& orderUnLock.getProjectCategory() != TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				if (!project.isDirectProject()) {
					RedisActivityClient.setFirstInvestInProject(orderUnLock.getProjectId(), orderUnLock.getId());
				}
			}
			Order orderForLock = orderManager.getOrderByIdForLock(orderUnLock.getId());
			if (orderForLock.getStatus().intValue() != StatusEnum.ORDER_WAIT_PROCESS.getStatus()
					&& orderForLock.getStatus().intValue() != StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus()) {
				return result;
			}
			StopWatch sw = new StopWatch();
			sw.start();
			logger.info("【 交易代收回调后处理方法】开始，tradeNo=" + tradeNo);
			HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
			HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getByIdForLock(collectTrade.getId());
			if (hostingCollectTrade != null) {
				// 如果已经是最终状态，不处理，直接返回
				if (hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
						|| hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())
						|| hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
						|| hostingCollectTrade.getTradeStatus().equals(TradeStatus.PRE_AUTH_APPLY_SUCCESS.name())
						|| hostingCollectTrade.getTradeStatus().equals(TradeStatus.PRE_AUTH_CANCELED.name())) {
					logger.info("托管代收交易tradeNo=" + tradeNo + "状态已经是最终状态：" + hostingCollectTrade.getTradeStatus());
					return result;
				}
				// 将交易状态置为最终状态
				hostingCollectTrade.setTradeStatus(tradeStatus);
				hostingCollectTrade.setOutTradeNo(outTradeNo);
				hostingCollectTrade.setProjectId(orderForLock.getProjectId());
				hostingCollectTrade.setProjectCategory(orderForLock.getProjectCategory());
				// 更新转让项目ID
				if (orderForLock.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
					hostingCollectTrade.setTransferId(orderForLock.getTransferId());
				}
				hostingCollectTradeManager.updateHostingCollectTrade(hostingCollectTrade);
				// 如果交易为交易成功状态
				if (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus) || TradeStatus.PRE_AUTH_APPLY_SUCCESS.name().equals(tradeStatus)) {
					// 判断转让项目目前是否可投(加锁)
					if (orderForLock.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
						TransferProject transferProForLock = transferProjectManager
								.selectByPrimaryKeyForLock(orderForLock.getTransferId());
						if (transferProForLock.getStatus() != StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()
								|| DateUtils.getCurrentDate().after(transferProForLock.getTransferEndDate())) {
							logger.info("转让项目已结束投资失败 tradeNo={}", hostingCollectTrade.getTradeNo());
							// 将订单置为交易失败状态
							isWithholdAuthorityTradeFailedAndClosed(tradeStatus, orderForLock, hostingCollectTrade,
									SummaryEnum.TRANSFER_PROJECT_STSTUS_ERROR);
							return result;
						}
						// 判断当天是否有还款（普通还款只需要根据转让项目结束时间判断，这里判断提前还款）
						TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
						transactionInterestQuery.setTransactionId(transferProForLock.getTransactionId());
						transactionInterestQuery.setCurdate(true);
						List<TransactionInterest> earlyPayList = transactionInterestManager
								.queryEarlyInterest(transactionInterestQuery);
						if (Collections3.isNotEmpty(earlyPayList)) {
							logger.info("转让项目已结束投资失败, 存在提前还款 tradeNo={}", hostingCollectTrade.getTradeNo());
							// 将订单置为交易失败状态
							isWithholdAuthorityTradeFailedAndClosed(tradeStatus, orderForLock, hostingCollectTrade,
									SummaryEnum.TRANSFER_PROJECT_STSTUS_ERROR);
							return result;
						}
					}
					boolean isFrozenProjectBalance = true;
					// 非委托支付的需要判断当前项目余额是否足够
					if (hostingCollectTrade.getIsWithholdAuthority() != null
							&& StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus() == hostingCollectTrade.getIsWithholdAuthority()) {
						isFrozenProjectBalance = false;
						// 加锁查询,判断项目余额不足
						BigDecimal balanceAmount = null;
						Long sourceId = null;
						TypeEnum typeEnum = null;
						if (orderForLock.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
							TransferProject transferPro = transferProjectManager.selectByPrimaryKey(orderForLock.getTransferId());
							sourceId = transferPro.getTransactionId();
							balanceAmount = orderForLock.getTransferPrincipal();
							typeEnum = TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT;
						} else {
							sourceId = orderForLock.getProjectId();
							balanceAmount = orderForLock.getInvestAmount();
							typeEnum = TypeEnum.BALANCE_TYPE_PROJECT;
						}
						Balance projectBalance = balanceManager.queryBalanceLocked(sourceId, typeEnum);
						if (projectBalance != null && projectBalance.getAvailableBalance() != null
								&& projectBalance.getAvailableBalance().compareTo(balanceAmount) < 0) {
							// 跳转收银台发起的代收交易因项目余额不足发起的后续业务
							isWithholdAuthorityTradeFailedAndClosed(tradeStatus, orderForLock, hostingCollectTrade,
									SummaryEnum.REFUND_PROJECT_BALANCE_NOT_ENOUGN);
							logger.info("退款事务提交时间，代收交易号{},当前时间{}",hostingCollectTrade.getTradeNo(),DateUtils.getCurrentDate());
							return result;
						}
					}
					// 执行交易
					result = processTransaction(orderForLock);
					//执行用户代收计算
					taskExecutor.execute(new MemberWaiteAmountThread(result.getResult(),project));
					// 将现金券或者收益券置为已使用状态
					processCoupon(orderForLock, result.getResult().getId());
					// 交易后异步处理的业务
					processAfterTransaction(result.getResult(), isFrozenProjectBalance);
				}
				tradeFailedAndClosed(tradeStatus, orderForLock, hostingCollectTrade);
				// 更新自动投标失败原因和状态
				//autoInvestLogManager.updateAutoInvestLogRemars(tradeStatus,orderForLock.getId(),hostingCollectTrade);
				logger.info("【 交易代收回调后处理方法】结束，总共耗时：" + sw.getTime() + ",tradeNo=" + tradeNo);
			} 
			return result;
		} catch (Exception e) {
			logger.error("【 交易代收回调后处理方法】发生异常，tradeNo：" + tradeNo, e);
			result.setSuccess(false);
			throw e;
		}
	}

	private class MemberWaiteAmountThread implements Runnable{
		private Transaction transaction;
		private Project project;
		public MemberWaiteAmountThread(Transaction transaction,Project project){
			this.transaction = transaction;
			this.project = project;
		}
		@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
		public void run(){
			try{
				//获取个人最大金额
				MemberRepayment memberRepayment =memberRepaymentMapper.selectByMemberId(transaction.getMemberId());
				BigDecimal max_repayment_amount =  BigDecimal.ZERO;
				BigDecimal histroy_max_repayment_amount =  BigDecimal.ZERO;
				BigDecimal max_repeat_amount = BigDecimal.ZERO;
				BigDecimal repeat_amount = BigDecimal.ZERO;
				BigDecimal current_investAmount = transaction.getInvestAmount();
				if(transaction.getTransferId()!=null){
					current_investAmount = transaction.getTransferPrincipal();
					current_investAmount = current_investAmount==null?BigDecimal.ZERO:current_investAmount;
				}
				if(memberRepayment != null){
					max_repayment_amount =histroy_max_repayment_amount = memberRepayment.getMaxRepaymentAmount();
					max_repeat_amount = memberRepayment.getMaxRepeatAmount();
				}else{
					 memberRepayment =new MemberRepayment();
				}
				MemberHistoryRepayment record =new MemberHistoryRepayment();
				SysDict sysDict = sysDictManager.findByGroupNameAndKey("user_repayment","time_limit");
				//满标交易本息表待还金额总额(去掉利息)
				BigDecimal full_scale_repayment_amount = transactionInterestMapper.totalMemberTransferAmount(transaction.getMemberId(),
						 StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus(),sysDict.getValue(),transaction.getId());
				full_scale_repayment_amount = full_scale_repayment_amount==null?BigDecimal.ZERO:full_scale_repayment_amount;
				//发生交易但未生成交易本息的未还总金额
				BigDecimal no_scale_repayment_amount =transactionMapper.getTransactionNoPayAmount(transaction.getMemberId()
						,sysDict.getValue(),transaction.getId());
				no_scale_repayment_amount = no_scale_repayment_amount==null?BigDecimal.ZERO:no_scale_repayment_amount;
				//未还总金额
				BigDecimal repayment_amount = full_scale_repayment_amount.add(no_scale_repayment_amount);
				//当前待还金额
				BigDecimal curr_repayment_amount = repayment_amount.add(current_investAmount);
				
				//历史最大待还金额 和 (待还金额+当前交易金额) 比较
				if(max_repayment_amount.compareTo(curr_repayment_amount) < 0){
					max_repayment_amount =  curr_repayment_amount;
				}
				if(histroy_max_repayment_amount.compareTo(BigDecimal.ZERO)==0){
					histroy_max_repayment_amount = repayment_amount;
				}
				//增量投资金额 = 当前待还金额+当前投资金额-历史最大金额
				BigDecimal addAmount = curr_repayment_amount.subtract(histroy_max_repayment_amount);
                if(addAmount.compareTo(BigDecimal.ZERO)<0){
                	addAmount = BigDecimal.ZERO;
				}
                //新增金额最大不能大于当前金额
                if(addAmount.compareTo(current_investAmount) > 0){
                	addAmount = current_investAmount;
                }
                //复投金额
           		repeat_amount  = current_investAmount.subtract(addAmount);
           		if(repeat_amount.compareTo(BigDecimal.ZERO) <0){//控制复投金额不能小于0
           			repeat_amount = BigDecimal.ZERO;
           		}
                //复投金额最大值限制大于当前金额
                if(repeat_amount.compareTo(current_investAmount) > 0){
                	repeat_amount = current_investAmount;
                }
            	max_repeat_amount = max_repeat_amount.add(repeat_amount);
				//生成用户投资记录数据
				record.setAddAmount(addAmount);
				record.setCreateTime(new Date());
				record.setMemberId(transaction.getMemberId());
				record.setRepeatAmount(repeat_amount);
				record.setStatus(1);
				record.setTransactionId(transaction.getId());
				record.setUpdateTime(new Date());
			    memberHistoryRepaymentMapper.insertSelective(record);
				//处理用户最大历史待还金额与复投金额
			    memberRepayment.setMaxRepaymentAmount(max_repayment_amount);
		    	memberRepayment.setMaxRepeatAmount(max_repeat_amount);
		    	memberRepayment.setTransactionId(transaction.getId());
			    if(memberRepayment.getId() != null){
			    	if(max_repayment_amount.compareTo(histroy_max_repayment_amount) < 0){
			    		memberRepayment.setMaxRepaymentAmount(histroy_max_repayment_amount);
			    	} 
			    	memberRepayment.setMaxRepeatAmount(max_repeat_amount);
			    	memberRepayment.setUpdateTime(new Date());
			        memberRepaymentMapper.updateByPrimaryKeySelective(memberRepayment);
			    }else{
			    	memberRepayment.setCreateTime(new Date());
			    	memberRepayment.setUpdateTime(new Date());
			    	memberRepayment.setMemberId(transaction.getMemberId());
			    	memberRepaymentMapper.insertSelective(memberRepayment);
			    }
			}catch(Exception e){
				logger.error("交易完成后记录用还款信息出现异常：" + transaction.toString(), e);
			}
		}
	}
	private void tradeFailedAndClosed(String tradeStatus, Order orderForLock, HostingCollectTrade hostingCollectTrade)
			throws ManagerException {
		if (TradeStatus.TRADE_FAILED.name().equals(tradeStatus) || TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)) {
			int status = StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus();
			if (TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)) {
				status = StatusEnum.ORDER_PAYED_FAILED.getStatus();
			}
			// 将订单置为交易失败状态
			orderManager.updateStatus(orderForLock, status, RemarksEnum.ORDER_NOT_PAY_ORDER.getRemarks());
			if (hostingCollectTrade.getIsWithholdAuthority() == null
					|| StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus() != hostingCollectTrade.getIsWithholdAuthority()) {
				balanceManager.unfrozenBalance(TypeEnum.BALANCE_TYPE_PROJECT, orderForLock.getInvestAmount(), orderForLock.getProjectId());
			}
			// 如果使用了优惠券，解锁优惠券
			if (StringUtil.isNotBlank(orderForLock.getProfitCouponNo())) {
				couponManager.unLockCoupon(orderForLock.getProfitCouponNo());
			}
			// 如果使用了优惠券，解锁优惠券
			if (StringUtil.isNotBlank(orderForLock.getCashCouponNo())) {
				couponManager.unLockCoupon(orderForLock.getCashCouponNo());
			}
			MessageClient.sendMsgForInvestmentFail(orderForLock.getProjectId(), orderForLock.getMemberId());
		}
	}
	
	@Override
	public ResultDO<Object> isWithholdAuthorityTradeFailedAndClosed(String tradeStatus, Order orderForLock,
			HostingCollectTrade hostingCollectTrade, SummaryEnum summary)
			throws ManagerException {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		if (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus) || TradeStatus.PRE_AUTH_APPLY_SUCCESS.name().equals(tradeStatus)) {
			// 将订单置为交易失败状态
			orderManager
					.updateStatus(orderForLock, StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus(), summary.getDesc());
			// 如果使用了优惠券，解锁优惠券
			if (StringUtil.isNotBlank(orderForLock.getProfitCouponNo())) {
				couponManager.unLockCoupon(orderForLock.getProfitCouponNo());
			}
			// 如果使用了优惠券，解锁优惠券
			if (StringUtil.isNotBlank(orderForLock.getCashCouponNo())) {
				couponManager.unLockCoupon(orderForLock.getCashCouponNo());
			}
			MessageClient.sendMsgForInvestmentFail(orderForLock.getProjectId(), orderForLock.getMemberId());
		}
		if (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
			// 如果如果用户使用用户资金大于0，创建会员代收交易，同时调用第三方接口进行代收交易，记录用户资金流水并且更新余额
			Balance balance = new Balance();
			try {
				balance = balanceManager.synchronizedBalance(orderForLock.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
			} catch (Exception e) {
				logger.error("代收发起退款同步用户存钱罐失败, memberId={}", orderForLock.getMemberId(), e);
				balance = balanceManager.queryBalance(orderForLock.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
			}
			String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_INVEST.getRemarks(),
					StringUtil.getShortProjectName(orderForLock.getProjectName()));
			// 记录用户投资资金流水
			capitalInOutLogManager.insert(orderForLock.getMemberId(), TypeEnum.FINCAPITALINOUT_TYPE_INVEST, null,
					hostingCollectTrade.getAmount(), balance.getAvailableBalance(), orderForLock.getId().toString(), remark,
					TypeEnum.BALANCE_TYPE_PIGGY);
			// 发起退款
			logger.info("跳转收银台发起的代收交易因{}, 发起退款 tradeNo={}", summary.getDesc(), hostingCollectTrade.getTradeNo());
			ResultDto<RefundTradeResult> resultDto = hostingRefundManager.refundByTradeNo(hostingCollectTrade,
					summary.getDesc());
			if (resultDto == null || !resultDto.isSuccess()) {
				resultDO.setSuccess(false);
				resultDO.setResult(resultDto.getErrorMsg());
			}
		} else if (TradeStatus.PRE_AUTH_APPLY_SUCCESS.name().equals(tradeStatus)) {
			// 发起代收撤销
			logger.info("跳转收银台发起的代收交易因{}, 发起代收撤销 tradeNo={}", summary.getDesc(), hostingCollectTrade.getTradeNo());
			ResultDto<?> resultDto = hostingCollectTradeAuthManager.cancelPreAuthTradeByCollectTrade(
					hostingCollectTrade, summary.getDesc());
			if (resultDto == null || !resultDto.isSuccess()) {
				resultDO.setSuccess(false);
			}
		}
		return resultDO;
	}

	private void processAfterTransaction(Transaction transaction, boolean isFrozenProjectBalance) throws Exception {
		try {
			// 减少项目余额
			Balance projectBalance = null;
			// 项目是否满额
			boolean isFull = false;
			BigDecimal balanceAmount = null;
			Long sourceId = null;
			TypeEnum typeEnum = null;
			if (transaction.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				TransferProject transferPro = transferProjectManager.selectByPrimaryKey(transaction.getTransferId());
				balanceAmount = transaction.getTransferPrincipal();
				sourceId = transferPro.getTransactionId();
				typeEnum = TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT;
			} else {
				balanceAmount = transaction.getInvestAmount();
				sourceId = transaction.getProjectId();
				typeEnum = TypeEnum.BALANCE_TYPE_PROJECT;
			}
			// 没有冻结可用余额的需要余额减少，可用余额也减少
			if (!isFrozenProjectBalance) {
				projectBalance = balanceManager.reduceBalance(typeEnum, balanceAmount, sourceId,
						BalanceAction.balance_subtract_Available_subtract);
			} else {
				projectBalance = balanceManager.reduceBalance(typeEnum, balanceAmount, sourceId,
						BalanceAction.balance_subtract);
			}
			// 更新项目redis余额
			RedisProjectClient.setProjectBalance(sourceId, projectBalance.getBalance());
			// 项目满额后，更新项目为满额状态
			if (projectBalance.getBalance() != null && projectBalance.getBalance().doubleValue() == 0) {
				isFull = true;
				if (transaction.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
					int num = transferProjectManager.updateProjectStatus(
							StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus(),
							StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus(), transaction.getTransferId());
					if (num == 1) {
						//更新转让项目的销售完成时间
						TransferProject p = new TransferProject();
						p.setId(transaction.getTransferId());
						p.setTransferSaleComplatedTime(DateUtils.getCurrentDate());
						p.setRemarks("本金已全部转让，转让结束");
						transferProjectManager.updateByPrimaryKeySelective(p);
					}
				} else {
					projectManager.updateProjectStatus(StatusEnum.PROJECT_STATUS_FULL.getStatus(),
							StatusEnum.PROJECT_STATUS_INVESTING.getStatus(), transaction.getProjectId());
					Project p = new Project();
					p.setId(transaction.getProjectId());
					p.setSaleComplatedTime(DateUtils.getCurrentDate());
					projectManager.updateByPrimaryKeySelective(p);
				}
			}
			// 转让项目生成交易本息
			if (transaction.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				// 生成交易本息、统计转让总本金、总利息、代付转让款、生产转让合同、更新转让原交易剩余认购本金、剩余总本金、剩余总利息
				transferProjectManager.handleTransaction(transaction, isFull, projectBalance);
				return;
			}
			boolean isFirstInvest = false;
			// 交易后续异步处理
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if (project.isDirectProject()){
				// 清空缓存
				RedisForProjectClient.clearTransactionDetail(transaction.getProjectId());
				String key = RedisConstant.TRANSACTION_INVERESTLIST_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + transaction.getProjectId();
				RedisManager.removeObject(key);
				if(isFull){
					//通知风控审核
					MessageClient.sendMsgForCommonBymobile(getMobileList("audit_mobile"), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.NOTICE_AUDIT.getCode(), 
							project.getName(),DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.TIME_PATTERN_SHORT_2));
				}
				//计算项目包进度
				taskExecutor.execute(new ProjectPackageProcessThread(transaction.getProjectId()));
			}else {
				taskExecutor.execute(new ProcessAfterTransactionThread(transaction, isFull, isFirstInvest));
			}
		} catch (ManagerException e) {
			throw e;
		}

	}

	/**
	 * 将现金券或者收益券置为已使用状态
	 * 
	 * @param order
	 * @param transactionId
	 */
	private void processCoupon(Order order, Long transactionId) throws Exception {
		try {
			// 现金券
			if (StringUtil.isNotBlank(order.getCashCouponNo())) {
				couponManager.useCoupon(order.getCashCouponNo(), order.getProjectId(), transactionId);
			}
			// 收益券
			if (StringUtil.isNotBlank(order.getProfitCouponNo())) {
				couponManager.useCoupon(order.getProfitCouponNo(), order.getProjectId(), transactionId);
			}
		} catch (ManagerException e) {
			throw e;
		}
	}

	/**
	 * 执行交易
	 * 
	 * @return
	 * @throws ManagerException
	 */

	public ResultDO<Transaction> processTransaction(Order order) throws ManagerException {
		ResultDO<Transaction> result = new ResultDO<Transaction>();
		try {
			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			Transaction transaction = new Transaction();
			buildTransaction(transaction, order,project);
			// 将order状态置为交易成功状态
			order.setStatus(StatusEnum.ORDER_PAYED_INVESTED.getStatus());
			updateOrderStatus(order);
			// 插入交易记录transaction
			result = saveTransaction(transaction);
			if (result.isSuccess() && order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				return result;
			}
			// 同步存钱罐
			Balance balance = new Balance();
			try {
				balance = balanceManager.synchronizedBalance(transaction.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
			} catch (Exception e) {
				logger.error("用户投资同步存钱罐账户失败", e);
				balance = balanceManager.queryBalance(transaction.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
			}
			// 直投项目后续业务在此不需要处理
			if (!result.isSuccess() || project.isDirectProject()) {
				return result;
			}
			DebtBiz debtBiz = debtManager.getFullDebtInfoById(project.getDebtId());
			// 插入交易本息记录transactionInterest
			saveTransactionInterest(transaction, debtBiz.getDebtInterests(), project, order);
			// 新增债权出让记录
			saveDebtTransfer(transaction, debtBiz, order);
			// 如果如果用户使用用户资金大于0，创建会员代收交易，同时调用第三方接口进行代收交易，记录用户资金流水并且更新余额
			if (transaction.getUsedCapital() != null && transaction.getUsedCapital().doubleValue() > 0) {
				String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_INVEST.getRemarks(),
						StringUtil.getShortProjectName(project.getName()));
				// 记录用户投资资金流水
				capitalInOutLogManager.insert(transaction.getMemberId(), TypeEnum.FINCAPITALINOUT_TYPE_INVEST, null,
						transaction.getUsedCapital(), balance.getAvailableBalance(), transaction.getId().toString(), remark,
						TypeEnum.BALANCE_TYPE_PIGGY);
			}
			// 如果会员使用了现金券，需要为基本户创建一条代收交易，同时调用第三方接口进行代收交易，记录基本户资金流水并且更新余额
			// if(transaction.getUsedCouponAmount()!=null &&
			// transaction.getUsedCouponAmount().doubleValue()>0) {
			// Balance balance =
			// balanceManager.queryBalance(Long.parseLong(Config.internalMemberId),
			// TypeEnum.BALANCE_TYPE_BASIC);
			// //记录平台垫付现金券资金流水
			// capitalInOutLogManager.insert(
			// Long.parseLong(Config.internalMemberId),
			// TypeEnum.FINCAPITALINOUT_TYPE_CASH,
			// null,
			// transaction.getUsedCouponAmount(),
			// balance.getAvailableBalance(),
			// transaction.getId().toString(),
			// order.getRemarks(),
			// TypeEnum.BALANCE_TYPE_BASIC
			// );
			// }

		} catch (Exception e) {
			logger.error("交易出错：order=" + order, e);
			throw e;
		}
		return result;
	}

  
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void createHostingPayForDirectLoan(Long projectId, BigDecimal totalUsedCapitalAmount, List<Transaction> transactions,
			String collectTradeNo) {
		logger.debug("放款给原始债权人线程开始执行，projectId：" + projectId);
		try {
			if (projectId == null) {
				return;
			}
			Project project = projectManager.selectByPrimaryKey(projectId);
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			if (project != null) {
				// 获取债权信息，包括债权本息列表
				DebtBiz debtBiz = debtManager.getFullDebtInfoById(project.getDebtId());
				Member originalCreditorPersonal = debtBiz.getLenderMember();
				HostingPayTrade hostingPayTrade = new HostingPayTrade();
				hostingPayTrade.setAmount(totalUsedCapitalAmount);
				hostingPayTrade.setPayeeId(originalCreditorPersonal.getId());
				hostingPayTrade.setRemarks("项目：" + project.getName() + "放款给" + originalCreditorPersonal.getId());
				hostingPayTrade.setSourceId(project.getId());
				hostingPayTrade.setTradeNo(SerialNumberUtil.generatePayTradeaNo(originalCreditorPersonal.getId()));
				hostingPayTrade.setTradeStatus(TradeStatus.WAIT_PAY.name());
				hostingPayTrade.setType(TypeEnum.HOSTING_PAY_TRADE_LOAN.getType());
				hostingPayTrade.setUserIp(ip);
				if (hostingPayTradeManager.insertSelective(hostingPayTrade) > 0) {
					if (Collections3.isNotEmpty(transactions)) {
						updateTransactionByLoan(hostingPayTrade.getTradeNo(), transactions,project);
					} else {
						// 更新代付交易号
						transactionMapper.updatePayTradeNoByCollectTradeNo(collectTradeNo, hostingPayTrade.getTradeNo());

					}
					try {
						ResultDto<CreateSinglePayTradeResult> result = sinaPayClient.createSinglePayTrade(
								AccountType.SAVING_POT,
								// project.getTotalAmount(),
								totalUsedCapitalAmount, hostingPayTrade.getTradeNo(), IdType.UID,
								SerialNumberUtil.generateIdentityId(originalCreditorPersonal.getId()), hostingPayTrade.getRemarks(),ip,
								TradeCode.PAY_TO_BORROWER);
					} catch (Exception e) {
						logger.error("【放款给原始债权人代付败】，collectTradeNo={}",collectTradeNo,e);
					}
				}

			}
		} catch (Exception e) {
			logger.error("放款给原始债权人发生异常,projectId=" + projectId, e);
		}
	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void createHostingPayForDirectProjectLoan(Long projectId, BigDecimal totalBorrowAmount, BigDecimal managerFee,
			BigDecimal guaranteeFee, BigDecimal riskFee, BigDecimal introducerFee,List<QuickRewardForPay> quickReForPayList) {
		logger.debug("放款给借款人开始执行，projectId：" + projectId);
		try {
			if (projectId == null) {
				return;
			}
			Project project = projectManager.selectByPrimaryKey(projectId);
			if (project == null) {
				return;
			}
			// 借款人Id
			Long borrowerId = project.getBorrowerId();
			if (StringUtil.isNotBlank(project.getOpenPlatformKey())) {
				// 从字典表找对应渠道合作商资金账户
				SysDict dict = sysDictManager.findByGroupNameAndKey("channel_business", project.getOpenPlatformKey());
				if (dict != null && StringUtil.isNotBlank(dict.getValue()) && StringUtil.isNumeric(dict.getValue())) {
					borrowerId = Long.valueOf(dict.getValue());
				}
			}
			List<TradeArgs> tradeList = Lists.newArrayList();
			String batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo();
			String summary = projectId + "直投项目放款";
			// 本地代付记录-放款给借款人
			buildDirectProjectLoanHostingPayTrade(totalBorrowAmount, borrowerId, batchPayNo,
					"项目：" + project.getName() + "放款给" + borrowerId, TypeEnum.HOSTING_PAY_TRADE_LOAN_BORROWER.getType(), projectId,
					AccountType.SAVING_POT, IdType.UID, tradeList);
			// 本地代付记录-放款给平台管理费
			if (managerFee.compareTo(BigDecimal.ZERO) > 0) {
				buildDirectProjectLoanHostingPayTrade(managerFee, Long.parseLong(Config.internalMemberId), batchPayNo,
						"项目：" + project.getName() + "的管理费放款给" + Long.parseLong(Config.internalMemberId),
						TypeEnum.HOSTING_PAY_TRADE_MANAGER_FEE.getType(), projectId, AccountType.BASIC, IdType.EMAIL, tradeList);
				projectFeeManager.updateProjectFeeByProjectId(projectId, TypeEnum.FEE_TYPE_MANAGE.getType(),
						StatusEnum.MANAGEMENT_FEE_GATHER_STATE_GATHERING.getStatus());
			}
			// 本地代付记录-放款给平台保证金
			if (guaranteeFee.compareTo(BigDecimal.ZERO) > 0) {
				buildDirectProjectLoanHostingPayTrade(guaranteeFee, Long.parseLong(Config.internalMemberId), batchPayNo,
						"项目：" + project.getName() + "的保证金放款给" + Long.parseLong(Config.internalMemberId),
						TypeEnum.HOSTING_PAY_TRADE_GUARANTEEFEE.getType(), projectId, AccountType.BASIC, IdType.EMAIL, tradeList);
				projectFeeManager.updateProjectFeeByProjectId(projectId, TypeEnum.FEE_TYPE_GUARANTEE.getType(),
						StatusEnum.MANAGEMENT_FEE_GATHER_STATE_GATHERING.getStatus());
			}
			// 本地代付记录-放款给平台风险金
			if (riskFee.compareTo(BigDecimal.ZERO) > 0) {
				buildDirectProjectLoanHostingPayTrade(riskFee, Long.parseLong(Config.internalMemberId), batchPayNo,
						"项目：" + project.getName() + "的风险金放款给" + Long.parseLong(Config.internalMemberId),
						TypeEnum.HOSTING_PAY_TRADE_RISK_FEE.getType(), projectId, AccountType.BASIC, IdType.EMAIL, tradeList);
				projectFeeManager.updateProjectFeeByProjectId(projectId, TypeEnum.FEE_TYPE_RISK.getType(),
						StatusEnum.MANAGEMENT_FEE_GATHER_STATE_GATHERING.getStatus());
			}
			// 本地代付记录-放款给介绍人介绍费
			if (introducerFee.compareTo(BigDecimal.ZERO) > 0) {
				buildDirectProjectLoanHostingPayTrade(introducerFee, project.getIntroducerId(), batchPayNo, "项目：" + project.getName()
						+ "的介绍费放款给"
						+ project.getIntroducerId(), TypeEnum.HOSTING_PAY_TRADE_INTRODUCE_FEE.getType(), projectId, AccountType.SAVING_POT,
						IdType.UID, tradeList);
				projectFeeManager.updateProjectFeeByProjectId(projectId, TypeEnum.FEE_TYPE_INTRODUCER.getType(),
						StatusEnum.MANAGEMENT_FEE_GATHER_STATE_GATHERING.getStatus());
			}
			
			// 本地代付记录-快投放款给中奖人
			if(Collections3.isNotEmpty(quickReForPayList)){
				for(QuickRewardForPay qRP :quickReForPayList){
					buildDirectProjectLoanHostingPayTrade(qRP.getAmount(), qRP.getMemberId(), batchPayNo, qRP.getRemarks(),
							TypeEnum.HOSTING_PAY_TRADE_QUICK_REWARD.getType(), projectId, AccountType.SAVING_POT,
							IdType.UID, tradeList);
				}
			}
			// 创建批量代付
			try {
				String ip = null;
				SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
				if (dict != null) {
					ip = dict.getValue();
				}
				sinaPayClient.createBatchPayTrade(batchPayNo, summary, ip, tradeList, TradeCode.PAY_TO_BORROWER, BatchTradeNotifyMode.single_notify);
			} catch (Exception e) {
				logger.error("【直投项目放款】-批量代付失败", e);
			}
		} catch (Exception e) {
			logger.error("放款给借款人发生异常,projectId=" + projectId, e);
		}
	}

	private void updateTransactionByLoan(String tradeNo, List<Transaction> transactions,Project project) throws ManagerException {
		int investType = project.getInvestType();
		if(ProjectEnum.PROJECT_TYPE_DIRECT.getType() == investType){//直投项目不设置代付交易号
			if(Collections3.isNotEmpty(transactions)){
				for (Transaction transaction : transactions) {
					transaction.setLoanStatus(StatusEnum.TRANSACTION_LOAN_STATUS_LOANING.getStatus());
					this.updateByPrimaryKeySelective(transaction);
				}
			}
		}else{
			for (Transaction transaction : transactions) {
				transaction.setLoanStatus(StatusEnum.TRANSACTION_LOAN_STATUS_LOANING.getStatus());
				transaction.setPayTradeNo(tradeNo);
				this.updateByPrimaryKeySelective(transaction);
			}
		}
	}

	private void updateOrderStatus(Order order) throws ManagerException {
		try {
			orderManager.updateByPrimaryKeySelective(order);
		} catch (ManagerException e) {
			logger.error("交易出错[更新订单为投资成功出错]：orderId=" + order.getId(), e);
			throw e;
		}
	}

	/**
	 * 通过order组装transaction
	 * 
	 * @param transaction
	 * @param order
	 * @throws ManagerException
	 */
	private void buildTransaction(Transaction transaction, Order order, Project project) throws ManagerException {
		Date firstOrderTransactionTime = null;
		Long firstOrderId = 0l;
		// 一羊领头时间校正开始
		if (!project.isDirectProject() && order.getProjectCategory() != TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_ACTIVITY_LEADING_SHEEP
					+ RedisConstant.REDIS_SEPERATOR + order.getProjectId();
			// 债权项目
			String lockValue = RedisManager.get(key);
			if (StringUtil.isNotBlank(lockValue)) {
				String[] valueArr = lockValue.split(RedisConstant.REDIS_SEPERATOR);
				if (valueArr != null && valueArr.length == 2) {
					firstOrderId = Long.valueOf(valueArr[0]);
					firstOrderTransactionTime = new Date(Long.valueOf(valueArr[1]));
				}
			}
		}
		Date transactionTime = DateUtils.getCurrentDate();
		if (order.getId().equals(firstOrderId) && firstOrderTransactionTime != null) {
			transactionTime = firstOrderTransactionTime;
		} else if (!order.getId().equals(firstOrderId) && firstOrderTransactionTime != null
				&& DateUtils.getTimeIntervalSencond(firstOrderTransactionTime, transactionTime) < 1) {
			transactionTime = DateUtils.addSecond(transactionTime, 1);
		}
		// 一羊领头时间校正结束
		transaction.setAnnualizedRate(order.getAnnualizedRate());
		transaction.setInvestAmount(order.getInvestAmount());
		transaction.setOrderId(order.getId());
		transaction.setProjectId(order.getProjectId());
		transaction.setProjectName(order.getProjectName());
		transaction.setProjectCategory(order.getProjectCategory());
		if (order.getPayAmount() != null) {
			transaction.setUsedCapital(order.getUsedCapital().add(order.getPayAmount()));
		} else {
			transaction.setUsedCapital(order.getUsedCapital());
		}
		transaction.setUsedCouponAmount(order.getUsedCouponAmount());
		transaction.setMemberId(order.getMemberId());
		transaction.setTransactionTime(transactionTime);
		transaction.setExtraAnnualizedRate(order.getExtraAnnualizedRate());
		transaction.setExtraProjectAnnualizedRate(order.getExtraProjectAnnualizedRate());
		transaction.setReceivedInterest(BigDecimal.ZERO);
		transaction.setReceivedPrincipal(BigDecimal.ZERO);
		if (order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
			// 计算预期收益
			// TransferProject tPro =
			// transferProjectManager.selectByPrimaryKey(order.getTransferId());
			// 计算转让项目的总本金和总利息 - 转移到计算交易本息的地方计算
			// BigDecimal totalPrincipal =
			// order.getTransferPrincipal().multiply(tPro.getResidualPrincipal())
			// .divide(tPro.getTransactionAmount(), 2,
			// BigDecimal.ROUND_HALF_UP);
			// transaction.setTotalPrincipal(totalPrincipal);
			// BigDecimal totalInterest =
			// order.getTransferPrincipal().multiply(tPro.getResidualInterest())
			// .divide(tPro.getTransactionAmount(), 2,
			// BigDecimal.ROUND_HALF_UP);
			// transaction.setTotalInterest(totalInterest);
			transaction.setStatus(StatusEnum.TRANSACTION_REPAYMENT.getStatus());
			transaction.setTransferId(order.getTransferId());
			transaction.setTransferPrincipal(order.getTransferPrincipal());
			return;
		}
		transaction.setTotalPrincipal(order.getInvestAmount());
		if (project.isDirectProject()){
			BigDecimal totalInterest =  projectManager.invertExpectEarnings(project, order.getInvestAmount(), order.getOrderTime(), order.getExtraAnnualizedRate());
			if(order.getExtraInterestDay()>0){
				totalInterest =  projectManager.invertExpectEarnings(project, order.getInvestAmount(), order.getOrderTime(), BigDecimal.ZERO);
				BigDecimal extraInterestH=FormulaUtil.calculateInterest(order.getInvestAmount(),order.getExtraAnnualizedRate(),order.getExtraInterestDay());
				if(extraInterestH==null){
					extraInterestH=BigDecimal.ZERO;
				}
				transaction.setExtraInterestDay(order.getExtraInterestDay());
				totalInterest=totalInterest.add(extraInterestH);
			}
			transaction.setTotalInterest(totalInterest);
			transaction.setStatus(StatusEnum.TRANSACTION_INVESTMENTING.getStatus());
		}else {
			transaction.setStatus(StatusEnum.TRANSACTION_REPAYMENT.getStatus());
		}
	}

	private List<TransactionInterest> saveTransactionInterest(Transaction transaction, List<DebtInterest> debtInterestList,
			Project project, Order order) throws ManagerException {
		// /获取债权本息信息
		List<TransactionInterest> transactionInterests = new ArrayList<TransactionInterest>();
		ResultDO<TransactionInterest> result = new ResultDO<TransactionInterest>();
		try {
			if (Collections3.isNotEmpty(debtInterestList)) {
				BigDecimal unitPrincipal = BigDecimal.ZERO;
				for (DebtInterest debtInterest : debtInterestList) {
					// 如果某一期的结束时间早于开始计息时间，则不记录本息记录
					Date startInterestDate = DateUtils.formatDate(DateUtils.addDate(DateUtils.getCurrentDate(), project.getInterestFrom()),
							DateUtils.DATE_FMT_3);
					if (!startInterestDate.after(debtInterest.getEndDate())) {
						TransactionInterest transactionInterest = new TransactionInterest();
						transactionInterest.setEndDate(debtInterest.getEndDate());
						//债权项目交易完成：交易本息添加interest_id、project_id 、periods
						transactionInterest.setInterestId(debtInterest.getId());
						transactionInterest.setProjectId(transaction.getProjectId());
						transactionInterest.setPeriods(debtInterest.getPeriods());
						transactionInterest.setMemberId(transaction.getMemberId());
						transactionInterest.setStartDate(debtInterest.getStartDate());
						transactionInterest.setStatus(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus());
						transactionInterest.setTransactionId(transaction.getId());
						// 后期需要根据收益类型不同而采用不同的计算方式，目前只有按日计息，按月付息
						if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())
								|| DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
							// 单位利息
							BigDecimal annualizedRate = transaction.getAnnualizedRate();
							if (transaction.getExtraAnnualizedRate() != null) {
								annualizedRate = annualizedRate.add(transaction.getExtraAnnualizedRate()).setScale(2,
										BigDecimal.ROUND_HALF_UP);

							}
							BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(), transaction.getInvestAmount(),
									annualizedRate);
							transactionInterest.setUnitInterest(unitInterest);
							transactionInterest.setUnitPrincipal(unitPrincipal);
						}
						transactionInterests.add(transactionInterest);
					}

				}
				int totalTransactionPeriod = transactionInterests.size();// 用户投资总期数
				if (totalTransactionPeriod > 0) {
					int totalDays = 0;
					BigDecimal totalInterest = BigDecimal.ZERO;
					BigDecimal totalExtraInterest = BigDecimal.ZERO;
					// 第一条交易本息对应的债券本息的开始时间
					Date firstTransInterestDebtSDate = transactionInterests.get(0).getStartDate();
					// 设置第一条记录的起息日
					TransactionInterest firstTransactionInterest = transactionInterests.get(0);
					firstTransactionInterest.setStartDate(DateUtils.addDate(
							DateUtils.formatDate(order.getOrderTime(), DateUtils.DATE_FMT_3), project.getInterestFrom()));
					// 一次性还本付息和按日计息，到期还本付息的还款方式的计算
					if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())
							|| DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
						// 最后一条记录的单位本金
						TransactionInterest lastTransactionInterest = transactionInterests.get(transactionInterests.size() - 1);
						lastTransactionInterest.setUnitPrincipal(transaction.getInvestAmount());
						result.setResultList(transactionInterests);

						for (TransactionInterest transactionInterest : transactionInterests) {
							// 计算应付利息和本金，后期如果有其他收益方式需要根据收益方式来计算
							transactionInterest.setPayablePrincipal(transactionInterest.getUnitPrincipal());
							int days = DateUtils.daysOfTwo(transactionInterest.getStartDate(), transactionInterest.getEndDate()) + 1;
							totalDays += days;
							BigDecimal value = (transactionInterest.getUnitInterest().multiply(new BigDecimal(days))).setScale(2,
									BigDecimal.ROUND_HALF_UP);
							transactionInterest.setPayableInterest(value);
							totalInterest = totalInterest.add(value);
							// 计算使用收益券获得的收益
							if (transaction.getExtraAnnualizedRate() != null) {
								BigDecimal extraUnitInterest = FormulaUtil.getUnitInterest(project.getProfitType(),
										transaction.getInvestAmount(), transaction.getExtraAnnualizedRate());
								BigDecimal extraInterest = extraUnitInterest.multiply(new BigDecimal(days));
								transactionInterest.setExtraInterest(extraInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
								totalExtraInterest = totalExtraInterest.add(extraInterest);
							}
							transactionInterestManager.insert(transactionInterest);
						}
					} else if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())) {// 等本等息
						int period = 0;
						for (TransactionInterest transactionInterest : transactionInterests) {
							// 等本等息计算方式 单位本金 = 应付本金； 应付利息 = 单位利息 * 收益天数
							// 单位本金
							unitPrincipal = FormulaUtil.getPrincipal(project.getProfitType(), transaction.getInvestAmount(),
									totalTransactionPeriod, period);
							// 总的年化利率
							BigDecimal annualizedRate = transaction.getAnnualizedRate();
							if (transaction.getExtraAnnualizedRate() != null) {
								annualizedRate = annualizedRate.add(transaction.getExtraAnnualizedRate()).setScale(2,
										BigDecimal.ROUND_HALF_UP);

							}
							int days = DateUtils.daysOfTwo(transactionInterest.getStartDate(), transactionInterest.getEndDate()) + 1;
							// 总收益天数
							totalDays += days;
							// 应付利息
							BigDecimal interest = BigDecimal.ZERO;
							interest = FormulaUtil.getTransactionInterest(project.getProfitType(), transaction.getInvestAmount(),
									annualizedRate, period, firstTransInterestDebtSDate, transactionInterest.getStartDate(),
									transactionInterest.getEndDate());
							// 单位利息
							BigDecimal unitInterest = BigDecimal.ZERO;
							unitInterest = interest.divide(new BigDecimal(days), 2, BigDecimal.ROUND_HALF_UP);

							transactionInterest.setPayablePrincipal(unitPrincipal);// 单位本金
																					// =
																					// 应付本金
							transactionInterest.setUnitPrincipal(unitPrincipal);// 单位本金
																				// =
																				// 应付本金
							transactionInterest.setUnitInterest(unitInterest);
							transactionInterest.setPayableInterest(interest);
							// 总利息
							totalInterest = totalInterest.add(interest);
							// 计算使用收益券获得的收益 总收益-项目收益率获取的收益 = 使用收益券获得的收益
							// 改为 收益券获得的收益  投资额 * 收益券利率 = 使用收益券获得的收益
							if (transaction.getExtraAnnualizedRate() != null) {
							/*	// 项目收益率获取的收益
								BigDecimal projectInterest = FormulaUtil.getTransactionInterest(project.getProfitType(),
										transaction.getInvestAmount(), transaction.getAnnualizedRate(), period,
										firstTransInterestDebtSDate, transactionInterest.getStartDate(), transactionInterest.getEndDate());
								// 使用收益券获得的收益
								BigDecimal extraInterest = interest.subtract(projectInterest);*/
								
								BigDecimal extraInterest = FormulaUtil.getTransactionInterest(project.getProfitType(),
										transaction.getInvestAmount(), transaction.getExtraAnnualizedRate(), period,
										firstTransInterestDebtSDate, transactionInterest.getStartDate(), transactionInterest.getEndDate());
								
								transactionInterest.setExtraInterest(extraInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
								totalExtraInterest = totalExtraInterest.add(extraInterest);
							}
							transactionInterestManager.insert(transactionInterest);
							period = period + 1;
						}
					}

					// 更新交易信息，包含收益总天数，总收益，总利息等
					transaction.setInstallmentNum(transactionInterests.size());
					transaction.setTotalDays(totalDays);
					transaction.setTotalInterest(totalInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
					//债权项目交易成功：total_extra_interest
					transaction.setTotalExtraInterest(totalExtraInterest);//总额外利息
					transaction.setReceivedInterest(BigDecimal.ZERO);
					transaction.setReceivedPrincipal(BigDecimal.ZERO);
					transaction.setOverdueFine(BigDecimal.ZERO);//滞纳金
					this.updateByPrimaryKeySelective(transaction);
					// 更新平台赚取的利息
//					balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_INTEREST,
//							totalInterest.setScale(2, BigDecimal.ROUND_HALF_UP), -1L);
				}

			}
		} catch (Exception e) {
			logger.error("交易出错[插入交易本息表出错]：transaction=" + transaction, e);
			throw e;
		}
		return transactionInterests;
	}

	private ResultDO<Transaction> saveTransaction(Transaction transaction) throws ManagerException {
		ResultDO<Transaction> result = null;
		try {
			if (this.insert(transaction) > 0) {
				result = new ResultDO<Transaction>(transaction);
			}
		} catch (ManagerException e) {
			result = new ResultDO<Transaction>();
			result.setResultCode(ResultCode.TRANSACTION_CAPITAL_REWARD_ZERO);
			logger.error("交易出错[保存交易基础信息出错]：transaction=" + transaction, e);
			throw e;
		}
		return result;
	}

	private void saveDebtTransfer(Transaction transaction, DebtBiz debtBiz, Order order) throws ManagerException {
		DebtTransfer debtTransfer = new DebtTransfer();

		debtTransfer.setAmount(transaction.getInvestAmount());
		BigDecimal annualizedRate = transaction.getAnnualizedRate();
		if (transaction.getExtraAnnualizedRate() != null) {
			annualizedRate = annualizedRate.add(transaction.getExtraAnnualizedRate());
		}
		debtTransfer.setAnnualizedRate(annualizedRate);
		debtTransfer.setDebtId(debtBiz.getId());
		debtTransfer.setDelFlag(Constant.ENABLE);
		debtTransfer.setEndDate(debtBiz.getEndDate());
		debtTransfer.setOwnerId(transaction.getMemberId());
		debtTransfer.setTransferTime(order.getOrderTime());
		debtTransfer.setStartDate(DateUtils.addDate(order.getOrderTime(), debtBiz.getInterestFrom()));
		try {
			debtTransferManager.insertSelective(debtTransfer);
		} catch (ManagerException e) {
			logger.error("交易出错[保存债权转让信息出错]：transaction=" + transaction + ",debtTransfer=" + debtTransfer, e);
			throw e;
		}
	}

	@Override
	public Transaction selectTransactionByIdLock(Long transactionId) throws ManagerException {
		try {
			return transactionMapper.selectByPrimaryKeyLock(transactionId);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<TransactionForMemberCenter> selectAllTransactionForMember(TransactionQuery query) throws ManagerException {
		try {

			// 查询用户交易记录
			List<TransactionForMemberCenter> orderForMembers = transactionMapper.selectAllTransactionForMember(query);
			if(orderForMembers!=null){
				Map<String, Object> map = Maps.newHashMap();
				//Project和ProjectExtra一对多以后
				for(TransactionForMemberCenter eachTransactionForMember :orderForMembers){
					map.put("projectId", eachTransactionForMember.getProjectId());
					List<ProjectExtra> projectExtraList = projectExtraMapper.getProjectExtraListByMap(map);
					eachTransactionForMember.setProjectExtraList(projectExtraList);
					eachTransactionForMember.setStatusCNName(eachTransactionForMember.getStatusName());
					if(eachTransactionForMember.getIsDirectProject()){
						eachTransactionForMember.setProjectType(1);
					}
				}
			}
			
			long count = transactionMapper.selectAllTransactionForMemberCount(query);
			
			Page<TransactionForMemberCenter> page = new Page<TransactionForMemberCenter>();
			page.setData(orderForMembers);
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<TransactionForMemberCenter> p2pSelectAllTransactionForMember(TransactionQuery query) throws ManagerException {
		try {

			List<TransactionForMemberCenter> orderForMembers = transactionMapper.p2pSelectAllTransactionForMember(query);
			// 通过项目id和投资金额获取年化收益计算预期收益
			long count = transactionMapper.p2pSelectAllTransactionForMemberCount(query);
			Page<TransactionForMemberCenter> page = new Page<TransactionForMemberCenter>();
			page.setData(orderForMembers);
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public TransactionForMemberCenter getTransactionForMember(Long transactionId,Long memberId) throws ManagerException {
		try {
			return transactionMapper.getTransactionForMember(transactionId,memberId);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Transaction getTransactionByOrderId(Long orderId) throws ManagerException {
		try {
			return transactionMapper.getTransactionByOrderId(orderId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getTransactionCount(Long memberId, int type) throws ManagerException {
		try {
			return transactionMapper.getTransactionCount(memberId, type);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Transaction> doTransactionWithoutCreateHostingCollect(Order order) throws Exception {
		ResultDO<Transaction> result = new ResultDO<Transaction>();
		try {
			if (order == null) {
				return result;
			}
			Order orderForLock = orderManager.getOrderByIdForLock(order.getId());
			if (orderForLock.getStatus().intValue() != StatusEnum.ORDER_WAIT_PROCESS.getStatus()
					&& orderForLock.getStatus().intValue() != StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus()) {
				return result;
			}
			StopWatch sw = new StopWatch();
			sw.start();
			logger.info("【不需要创建代收交易直接执行交易方法】开始，orderForLock=" + orderForLock);
			// 执行交易
			result = processTransaction(orderForLock);
			// 将现金券或者收益券置为已使用状态
			processCoupon(orderForLock, result.getResult().getId());
			// 交易后异步处理的业务
			processAfterTransaction(result.getResult(), false);
			logger.info("【不需要创建代收交易直接执行交易方法】结束，总共耗时：" + sw.getTime() + ",orderForLock=" + orderForLock + ",transactionId="
					+ result.getResult().getId());
			return result;
		} catch (Exception e) {
			logger.error("【不需要创建代收交易直接执行交易方法】发生异常，order：" + order, e);
			result.setSuccess(false);
			throw e;
		}
	}

	@Override
	public BigDecimal getCouponAmountForPaltform(Long projectId) throws ManagerException {
		try {
			return transactionMapper.getCouponAmountForPaltform(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<?> afterPaltformCouponCollectNotify(String tradeNo, String outTradeNo, String tradeStatus) throws Exception {
		ResultDO<?> result = new ResultDO();
		try {
			HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
			HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getByIdForLock(collectTrade.getId());
			if (hostingCollectTrade != null) {
				// 如果是最终状态，则直接返回
				if (hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
						|| hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
						|| hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())) {
					logger.info("代收平台垫付优惠券代收已经是最终状态，tradeNo=" + tradeNo);
					return result;
				}
				// 将交易状态置为最终状态
				hostingCollectTrade.setTradeStatus(tradeStatus);
				hostingCollectTrade.setOutTradeNo(outTradeNo);
				hostingCollectTradeManager.updateHostingCollectTrade(hostingCollectTrade);
				// 如果交易为交易成功状态
				if (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
					// 写流水
					Balance balance = balanceManager.reduceBalance(TypeEnum.BALANCE_TYPE_BASIC, hostingCollectTrade.getAmount(),
							Long.parseLong(Config.internalMemberId), BalanceAction.balance_subtract_Available_subtract);
					// 记录用户投资资金流水
					capitalInOutLogManager.insert(Long.parseLong(Config.internalMemberId), TypeEnum.FINCAPITALINOUT_TYPE_CASH, null,
							hostingCollectTrade.getAmount(), balance.getAvailableBalance(), hostingCollectTrade.getId().toString(), null,
							TypeEnum.BALANCE_TYPE_BASIC);
					createHostPayForLoan(null,null, null,collectTrade);
					
				}
				if (TradeStatus.TRADE_FAILED.name().equals(tradeStatus) || TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)) {
					// TODO 失败处理
				}

			}
		} catch (Exception e) {
			throw e;
		}
		return result;

	}

	public void createHostPayForLoan(Long projectId,BigDecimal totalUsedCapitalAmount,List<Transaction> transactions,HostingCollectTrade hostingCollectTrade) throws ManagerException {
		if(hostingCollectTrade!=null){
			projectId = hostingCollectTrade.getSourceId();
		}
		Project project = projectManager.selectByPrimaryKey(projectId);
		if(project.getInvestType().intValue() == ProjectEnum.PROJECT_TYPE_DEBT.getType()){
			if(hostingCollectTrade!=null){//代收现金券的放款,通过代收交易号查询代收的交易总额
				//债权项目代收成功处理
				TransactionQuery transactionQuery = new TransactionQuery();
				transactionQuery.setCollectTradeNo(hostingCollectTrade.getTradeNo());
				totalUsedCapitalAmount = transactionMapper.getTotalAmountByQuery(transactionQuery);
				this.createHostingPayForDirectLoan(projectId, totalUsedCapitalAmount, transactions,
						hostingCollectTrade.getTradeNo());
			}else{
				this.createHostingPayForDirectLoan(projectId, totalUsedCapitalAmount, transactions,
						null);
			}
			// 创建放款代付
		}else if(project.getInvestType().intValue() == ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
			//直投项目代收成功处理
			createHostingPayForDirectProject(project,transactions);
		}
	}
	
	/**
	 * @Description:直投项目，平台现金券代收成功后处理
	 * @author: fuyili
	 * @throws ManagerException 
	 * @time:2016年1月28日 下午7:30:43
	 * 
	 */
	private void createHostingPayForDirectProject(Project project,List<Transaction> transaction) throws ManagerException{
		/**
		 * 1.代付管理费到平台 2.代付（项目总额-管理费）给借款人
		 */
		// 项目总额
		BigDecimal totalAmount = project.getTotalAmount();
		// 管理费
		ProjectFee managerFeeRes = projectFeeManager.getProjectFeeByProjectIdType(project.getId(), TypeEnum.FEE_TYPE_MANAGE.getType());
		BigDecimal managerFee = BigDecimal.ZERO;
		if (managerFeeRes != null) {
			managerFee = managerFeeRes.getAmount() == null ? BigDecimal.ZERO : managerFeeRes.getAmount();
		}
		// 保证金
		ProjectFee guaranteeFeeRes = projectFeeManager.getProjectFeeByProjectIdType(project.getId(), TypeEnum.FEE_TYPE_GUARANTEE.getType());
		BigDecimal guaranteeFee = BigDecimal.ZERO;
		if (guaranteeFeeRes != null) {
			guaranteeFee = guaranteeFeeRes.getAmount() == null ? BigDecimal.ZERO : guaranteeFeeRes.getAmount();
		}
		// 风险金
		ProjectFee riskFeeRes = projectFeeManager.getProjectFeeByProjectIdType(project.getId(), TypeEnum.FEE_TYPE_RISK.getType());
		BigDecimal riskFee = BigDecimal.ZERO;
		if (riskFeeRes != null) {
			riskFee = riskFeeRes.getAmount() == null ? BigDecimal.ZERO : riskFeeRes.getAmount();
		}
		// 介绍费
		ProjectFee introduceFeeRes = projectFeeManager
				.getProjectFeeByProjectIdType(project.getId(), TypeEnum.FEE_TYPE_INTRODUCER.getType());
		BigDecimal introduceFee = BigDecimal.ZERO;
		if (introduceFeeRes != null) {
			introduceFee = introduceFeeRes.getAmount() == null ? BigDecimal.ZERO : introduceFeeRes.getAmount();
		}
		
		
		//快投奖金
		BigDecimal quickReward = BigDecimal.ZERO;
		List<QuickRewardForPay> quickReForPayList = Lists.newArrayList(); 
		ProjectExtra pro = projectExtraManager.getProjectQucikReward(project.getId());
		if(pro.getActivitySign()!=null&&pro.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
			
			ActivityLotteryResult model=new ActivityLotteryResult();
			model.setActivityId(pro.getActivityId());
			model.setRemark(project.getId().toString());
			model.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);//中奖标识
			List<ActivityLotteryResult> listActivityResult=activityLotteryResultManager.getLotteryResultBySelectiveAndLotteryStatus(model);
			Map<String, Object> map = Maps.newHashMap();
			Map<String, Object> mapB = Maps.newHashMap();
			for(ActivityLotteryResult actResult:listActivityResult){
				if (map.containsKey(actResult.getMemberId()+"")) {
					map.put(actResult.getMemberId()+"", Integer.valueOf(map.get(
							actResult.getMemberId()+"").toString()) + Integer.valueOf(actResult.getRewardInfo()));
				} else {
					map.put(actResult.getMemberId()+"", actResult.getRewardInfo());
				}
				if (mapB.containsKey(actResult.getMemberId()+"")) {
					mapB.put(actResult.getMemberId()+"", mapB.get(
							actResult.getMemberId()+"").toString() + actResult.getChip()+"等奖"+actResult.getRewardInfo()+"元");
				} else { 
					mapB.put(actResult.getMemberId()+"", "项目："+project.getName()+"用户："+actResult.getMemberId()+"获得"
										+actResult.getChip()+"等奖"+actResult.getRewardInfo()+"元");
				}
			}
			for (Entry<String, Object> entry : map.entrySet()) {
				QuickRewardForPay qRP = new QuickRewardForPay();
				qRP.setAmount(new BigDecimal(entry.getValue().toString()));
				qRP.setMemberId(Long.valueOf(entry.getKey()));
				qRP.setRemarks(mapB.get(entry.getKey()).toString());
				quickReForPayList.add(qRP);
				quickReward = quickReward.add(qRP.getAmount()!=null?qRP.getAmount():BigDecimal.ZERO);
			}
		}
		// 创建批量代付
		BigDecimal loanAmount = totalAmount.subtract(managerFee).subtract(guaranteeFee).subtract(riskFee).subtract(introduceFee).subtract(quickReward);
		updateTransactionByLoan(null, transaction, project);// 直投项目代付交易表不需要设置代付交易号
		createHostingPayForDirectProjectLoan(project.getId(), loanAmount, managerFee, guaranteeFee, riskFee, introduceFee,quickReForPayList);
		
		
	}
	
	

	@Override
	public ResultDO<?> afterHostingPayTradeLoan(String processStatus, String tradeNo) throws Exception {
		ResultDO<?> result = new ResultDO();
		try {
			HostingPayTrade payTrade = hostingPayTradeManager.getByTradeNo(tradeNo);
			if(payTrade == null) {
				logger.info("放款代付记录不存在，tradeNo={}",tradeNo);
				return result;
			}
			HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByIdForLock(payTrade.getId());
			// 如果是最终状态，则直接返回
			if (hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
					|| hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
					|| hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())) {
				logger.info("放款代付已经是最终状态，tradeNo=" + tradeNo + "状态：" + hostingPayTrade.getTradeStatus());
				return result;
			}
			int updateNum = hostingPayTradeManager.updateHostingPayTradeStatus(processStatus, TradeStatus.WAIT_PAY.name(), tradeNo,
					hostingPayTrade.getId());
			if (updateNum <= 0) {
				logger.info("放款代付记录不是WAIT_PAY状态，tradeNo=" + tradeNo);
				return result;
			}
			// 如果成功，则同步原始债权人余额以及插入资金流水
			if (processStatus.equals(TradeStatus.TRADE_FINISHED.name())) {
				// 判断项目类型
				Project p = projectManager.selectByPrimaryKey(hostingPayTrade.getSourceId());
				if (ProjectEnum.PROJECT_TYPE_DEBT.getType() == p.getInvestType().intValue()) {
					// 债权项目放款代付成功处理逻辑
					// 写流水
					Balance balance = null;
					try {
						balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
					} catch (Exception e) {
						logger.error("放款成功后同步余额失败,PayeeId={}", hostingPayTrade.getPayeeId());
						balance = balanceManager.queryBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
					}
					// 资金流水的备注
					String remark = "";
					if (hostingPayTrade.getSourceId() != null) {
						Project project = projectManager.selectByPrimaryKey(hostingPayTrade.getSourceId());
						if (project != null) {
							remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_LOAN.getRemarks(),
									StringUtil.getShortProjectName(project.getName()));
						}
					}
					// 记录用户投资资金流水
					capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(), TypeEnum.FINCAPITALINOUT_TYPE_LOAN,
							hostingPayTrade.getAmount(), null, balance.getAvailableBalance(), hostingPayTrade
									.getId().toString(), remark, TypeEnum.BALANCE_TYPE_PIGGY);
					// 更新交易表状态
					transactionMapper.updateLoanStatusByPayTradeNo(hostingPayTrade.getTradeNo(),
							StatusEnum.TRANSACTION_LOAN_STATUS_LOANED.getStatus());
					// 往放款明显写记录
					LoanDetail loanDetail = new LoanDetail();
					TransactionQuery transactionQuery = new TransactionQuery();
					transactionQuery.setPayTradeNo(hostingPayTrade.getTradeNo());
					BigDecimal platformPay = transactionMapper.getTotalCouponAmountByQuery(transactionQuery);
					if (platformPay == null) {
						platformPay = BigDecimal.ZERO;
					}
					loanDetail.setLoanAmount(hostingPayTrade.getAmount());
					loanDetail.setPlatformPay(platformPay);
					loanDetail.setProjectId(hostingPayTrade.getSourceId());
					loanDetail.setUserPay(hostingPayTrade.getAmount().subtract(platformPay)
							.setScale(2, BigDecimal.ROUND_HALF_UP));
					loanDetail.setLoanStatus(1);// 放款类型1-放款给出借人 2-放款给借款人
					loanDetail.setLoanType(1);// 放款状态(1-已放款)
					loanDetailMapper.insertSelective(loanDetail);
				} else if (ProjectEnum.PROJECT_TYPE_DIRECT.getType() == p.getInvestType().intValue()) {
					// 直投项目放款代付成功处理
					afterHostingPayDirectProjectLoan(p, hostingPayTrade);
				}

			}
			
			
			return result;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<?> afterDirectHostingPay(String tradeStatus, String tradeNo) throws Exception {
		ResultDO<?> result = new ResultDO();
		try {
			HostingPayTrade payTrade = hostingPayTradeManager.getByTradeNo(tradeNo);
			HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByIdForLock(payTrade.getId());
			if (hostingPayTrade != null) {
				// 如果是最终状态，则直接返回
				if (hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
						|| hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())) {
					logger.info("直接代付已经是最终状态，tradeNo=" + tradeNo);
					return result;
				}
				hostingPayTrade.setTradeStatus(tradeStatus);
				hostingPayTradeManager.updateHostingPayTrade(hostingPayTrade);
				String remarks = hostingPayTrade.getRemarks();
				// 如果成功，则同步原始债权人余额以及插入资金流水
				if (tradeStatus.equals(TradeStatus.TRADE_FINISHED.name())) {
					// 写流水
					Balance balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
					// 记录用户投资资金流水
					capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(), TypeEnum.FINCAPITALINOUT_TYPE_DIRECT_PAY,
							hostingPayTrade.getAmount(), null, balance.getAvailableBalance(), hostingPayTrade.getId().toString(), remarks,
							TypeEnum.BALANCE_TYPE_PIGGY);
				}
			}
			return result;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public int getTransactionCountByProject(Long projectId) throws Exception {
		try {
			return transactionMapper.getTransactionCountByProject(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int getTransactionCountByTransferId(Long transferId) throws Exception {
		try {
			return transactionMapper.getTransactionCountByTransferId(transferId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	
	@Override
	public int getTransactionCountByTransferProject(Long projectId) throws Exception {
		try {
			return transactionMapper.getTransactionCountByTransferProject(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getTransactionMemberCountByProject(Long projectId) throws Exception {
		try {
			return transactionMapper.getTransactionMemberCountByProject(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int getTransactionMemberCountByTransferId(Long transferId) throws Exception {
		try {
			return transactionMapper.getTransactionMemberCountByTransferId(transferId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	@Override
	public BigDecimal getTotalTransactionInterestByProject(Long projectId) throws Exception {
		try {
			return transactionMapper.getTotalTransactionInterestByProject(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public BigDecimal getTotalTransactionInterestByTransferId(Long transferId) throws Exception {
		try {
			return transactionMapper.getTotalTransactionInterestByTransferId(transferId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	@Override
	public List<ActivityForKing> getRefferalInvestAmountList() throws Exception {
		try {
			return transactionMapper.getRefferalInvestAmountList();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据项目id获取最后一笔交易
	 */
	@Override
	public Transaction selectLastTransactionByProject(Long projectId) throws Exception {
		try {
			return transactionMapper.selectLastTransactionByProject(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 通过项目查询投资金额最高的一笔（额度相同取投资时间最早的）
	 */
	@Override
	public Transaction selectMostTransactionByProject(Long projectId) throws Exception {
		try {
			return transactionMapper.selectMostTransactionByProject(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Transaction selectAmountByProject(Long projectId, Integer loanStatus) throws Exception {
		try {
			return transactionMapper.selectAmountByProject(projectId, loanStatus);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @Description:五重礼获取人气值
	 * @param activityId
	 * @param mostTransaction
	 * @param popularityValue
	 * @return
	 * @throws NumberFormatException
	 * @throws ManagerException
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年6月7日 下午3:16:58
	 */
	@Override
	public String getPopularityValue(String activityId,int days,String popularityValue) throws NumberFormatException, Exception {
		Optional<Activity> optAct = LotteryContainer.getInstance().getActivityByName(ActivityConstant.FIVE_RITES);
		if (!optAct.isPresent()) {
			return popularityValue;
		}
		logger.info("直投获取人气值 ,days={}",days);
		Activity activity=optAct.get();
		int value=0;
		if (DateUtils.isDateBetween(DateUtils.getCurrentDate(), activity.getStartTime(), activity.getEndTime())) {
			ActivityForFiveRites rite = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForFiveRites.class,
					ActivityConstant.FIVE_RITES_KEY);
			value = getValue(rite, days, activityId, popularityValue);
			popularityValue = String.valueOf(value);
		}
		logger.info("直投获取人气值返回 ,popularityValue={}",popularityValue);
		return popularityValue;
	}
	/**
	 * 
	 * @Description:计算人气值
	 * @param five
	 * @param days
	 * @param activityId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月12日 下午1:22:33
	 */
	public int getValue(ActivityForFiveRites five,int days,String activityId,String popularityValue){
		int value=0;
		if(five.getDays().length<4){
			return value;
		}
			if(days>=five.getDays()[0]&&days<five.getDays()[1]){
				if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getCode().equals(activityId)){
					value=five.getYcdyPopularityValue()[0];
				}else if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getCode().equals(activityId)){
					value=five.getYmjrPopularityValue()[0];
				}else if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getCode().equals(activityId)){
					value=five.getYzqjPopularityValue()[0];
				}else{
					value=Integer.parseInt(popularityValue);
				}
				
			}else if(days>=five.getDays()[1]&&days<five.getDays()[2]){
				if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getCode().equals(activityId)){
					value=five.getYcdyPopularityValue()[1];
				}else if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getCode().equals(activityId)){
					value=five.getYmjrPopularityValue()[1];
				}else if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getCode().equals(activityId)){
					value=five.getYzqjPopularityValue()[1];
				}else{
					value=Integer.parseInt(popularityValue);
				}
			}else if(days>=five.getDays()[2]&&days<five.getDays()[3]){
				if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getCode().equals(activityId)){
					value=five.getYcdyPopularityValue()[2];
				}else if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getCode().equals(activityId)){
					value=five.getYmjrPopularityValue()[2];
				}else if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getCode().equals(activityId)){
					value=five.getYzqjPopularityValue()[2];
				}else{
					value=Integer.parseInt(popularityValue);
				}
			}else if(days>=five.getDays()[3]){
				if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getCode().equals(activityId)){
					value=five.getYcdyPopularityValue()[3];
				}else if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getCode().equals(activityId)){
					value=five.getYmjrPopularityValue()[3];
				}else if(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getCode().equals(activityId)){
					value=five.getYzqjPopularityValue()[3];
				}else{
					value=Integer.parseInt(popularityValue);
				}
			}else if(days<five.getDays()[0]){
				value =0;
			}
		return value;
	}
	/**
	 * 线程计算项目进度信息
	 * @author XR
	 *
	 */
	private class ProjectPackageProcessThread implements Runnable{
		private Long projectId;

		public ProjectPackageProcessThread(Long projectId){
			this.projectId = projectId;
		}
		@Override
		public void run() {
			projectManager.computerProjectPackageProgress(projectId);
		}
		
	}
	/**
	 * 交易完成后处理线程
	 * 
	 * @author Administrator
	 *
	 */
	private class ProcessAfterTransactionThread implements Runnable {
		private Transaction transaction;
		private boolean isFull;
		private boolean isFirstInvest;

		public ProcessAfterTransactionThread(final Transaction transaction, final boolean isFull, final boolean isFirstInvest) {
			this.transaction = transaction;
			this.isFull = isFull;
			this.isFirstInvest = isFirstInvest;
		}

		@Override
		@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
		public void run() {
			logger.info("交易完成后处理线程开始执行，transactionId：" + transaction.getId());
			try {
				/*
				 * 每一笔交易形成的时候添加到缓存redis
				 */
				// addTransactionDetailForProject(transaction,isFirstInvest);
				Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
				Long memberId = transaction.getMemberId();
				//TODO 【直投项目】不需要在这里新增用户投资总额，移动到风控审核通过
				// 更新用户投资总额
				if (!project.isDirectProject()) {
					RedisMemberClient.addTotalInvestAmount(memberId, transaction.getInvestAmount());
					// 增加平台投资总额
					balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_INVEST, transaction.getInvestAmount(), -1L);
					// 增加平台累计收益
					balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_INTEREST, transaction.getTotalInterest(), -1L);
					// 平台投资总额累计破亿
					Balance balance = balanceManager.queryBalance(-1L, TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_INVEST);
					if (balance != null) {
						RedisPlatformClient.breakHundredMillion(balance.getAvailableBalance());
					}
				}
				// 新手投资在活动期间送收益券
				Member member = memberManager.selectByPrimaryKey(memberId);
				// 给推荐人增加人气值
				Optional<Activity> optAct = LotteryContainer.getInstance().getActivityByName(ActivityConstant.INVITATION_FRIENDS);
				if(optAct.isPresent()){
					Activity activit = optAct.get();
					if(DateUtils.isDateBetween(DateUtils.getCurrentDate(),activit.getStartTime(), activit.getEndTime())){
						incrNewReferralPoints(member, project, activit, transaction);
					}else{
						incrReferralPoints(member);
					}
				}else{
					incrReferralPoints(member);
				}
				boolean isMost = false;
				boolean isLuck = false;
				if (isFull) {
					//logger.info("项目：" + transaction.getProjectId() + "一锤定音获得者：" + memberId);
					// 一锤定音活动
					Transaction lastTransaction=transactionMapper.selectLastTransactionByProject(transaction.getProjectId());
					/*int days=0;
					if(lastTransaction!=null){
						days=lastTransaction.getTotalDays();
					}
					String lastInvestPopularity=getPopularityValue(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getCode(),days,Config.lastInvestPopularity);
					BigDecimal value = new BigDecimal(lastInvestPopularity);
					if(value.intValue()>0){
						MessageClient.sendMsgForProjectActivities(transaction.getProjectId(), "一锤定音", lastInvestPopularity, memberId);
						MessageClient.sendMsgForCommon(memberId, Constant.MSG_TEMPLATE_TYPE_BAIDU, MessageEnum.FIVE_PRIZE.getCode(),
								transaction.getMarkProjectName(), "一锤定音");
					}
					// 调用赠送人气值接口
					givePopularity(transaction.getProjectId(), memberId, TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, value,
							RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());*/
					// 一鸣惊人活动
					Transaction mostTransaction = transactionMapper.selectMostTransactionByProject(transaction.getProjectId());
					/*String mostInvestPopularity=getPopularityValue(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getCode(),mostTransaction.getTotalDays(),Config.mostInvestPopularity);
					BigDecimal mostInvestValue = new BigDecimal(mostInvestPopularity);
					if(mostInvestValue.intValue()>0){
						MessageClient.sendMsgForProjectActivities(mostTransaction.getProjectId(), "一鸣惊人", mostInvestPopularity,
								mostTransaction.getMemberId());
						MessageClient.sendMsgForCommon(mostTransaction.getMemberId(), Constant.MSG_TEMPLATE_TYPE_BAIDU,
								MessageEnum.FIVE_PRIZE.getCode(), transaction.getMarkProjectName(), "一鸣惊人");
					}*/
					// 调用赠送人气值接口
					/*givePopularity(mostTransaction.getProjectId(), mostTransaction.getMemberId(), TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY,
							new BigDecimal(mostInvestPopularity),
							RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
					logger.info("项目：" + transaction.getProjectId() + "一鸣惊人获得者：" + mostTransaction.getMemberId());*/

					// 一掷千金
					if (transaction.getMemberId().equals(mostTransaction.getMemberId())) {
						/*// 一鸣惊人得主和一锤定音得主为同一个人
						/*String mostAndLastInvestPopularity=getPopularityValue(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getCode(),mostTransaction.getTotalDays(),Config.mostAndLastInvestPopularity);
						BigDecimal mostAndLastValue = new BigDecimal(mostAndLastInvestPopularity);
						if(mostAndLastValue.intValue()>0){
							// 发送站内信
							MessageClient.sendMsgForProjectActivities(mostTransaction.getProjectId(), "一掷千金",
									mostAndLastInvestPopularity, transaction.getMemberId());
							MessageClient.sendMsgForCommon(transaction.getMemberId(), Constant.MSG_TEMPLATE_TYPE_BAIDU,
									MessageEnum.FIVE_PRIZE.getCode(), transaction.getMarkProjectName(), "一掷千金");
						}
						// 调用赠送人气值接口
						givePopularity(transaction.getProjectId(), memberId, TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, mostAndLastValue,
								RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks());
						logger.info("项目：" + transaction.getProjectId() + "一掷千金获得者：" + transaction.getMemberId());*/
					}

					// 财富心跳插入redis 数据
					if (mostTransaction.getId() == transaction.getId()) {
						isMost = true;
					} else {
						Member memberMost = memberManager.selectByPrimaryKey(mostTransaction.getMemberId());
						addTransactionDetailForIndexQuadruplegift(mostTransaction, isFirstInvest, false, true, false, project, memberMost);
					}

					// 幸运女神活动
					TransactionQuery transactionQuery = new TransactionQuery();
					transactionQuery.setProjectId(transaction.getProjectId());
					List<Transaction> transactions = transactionMapper.selectTransactionsByQueryParams(transactionQuery);
					if (Collections3.isNotEmpty(transactions)) {/*
						// 获取随机种子
						int randomNum = (int) (Math.random() * transactions.size());
						Transaction luckTransaction = transactions.get(randomNum);
						/*String xynsValue=getPopularityValue(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getCode(),luckTransaction.getTotalDays(),Config.luckInvestPopularity);
						BigDecimal xyvalue = new BigDecimal(xynsValue);
						if(xyvalue.intValue()>0){

							MessageClient.sendMsgForProjectActivities(luckTransaction.getProjectId(), "幸运女神", xynsValue,
									luckTransaction.getMemberId());
							MessageClient.sendMsgForCommon(luckTransaction.getMemberId(), Constant.MSG_TEMPLATE_TYPE_BAIDU,
									MessageEnum.FIVE_PRIZE.getCode(), luckTransaction.getMarkProjectName(), "幸运女神");
						}
						// 调用赠送人气值接口
						givePopularity(luckTransaction.getProjectId(), luckTransaction.getMemberId(),
								TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, new BigDecimal(xynsValue),
								RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
						logger.info("项目：" + luckTransaction.getProjectId() + "幸运女神获得者：" + luckTransaction.getMemberId());*/
						
						 
						// editTransactionDetailForProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks(),luckTransaction);
						// addTransactionDetailForIndex(transaction,isFirstInvest,isFull,
						// project,member );
						//Member memberLuck = memberManager.selectByPrimaryKey(luckTransaction.getMemberId());
						//boolean isLast = false;
						// 财富心跳插入redis 数据
						//addTransactionDetailForIndexQuadruplegift(luckTransaction, isFirstInvest, isLast, false, true, project, memberLuck);

					}
					SysDict dict = sysDictManager.findByGroupNameAndKey("lvgou_dict", "send_lvgou");
					// 把标识设为N，避免定时任务的唯一性
					if ("Y".equals(dict.getValue())) {
						// 推送绿狗合同
						lvGouManager.createLvGouContract(transaction.getProjectId());
					}
				}

				if (isFirstInvest) {
					// 判断是否是该项目第一笔投资
					// 满足条件，赠送人气值
					String yyltValue=getPopularityValue(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getCode(),transaction.getTotalDays(),Config.firstInvestPopularity);
					// 调用赠送人气值接口
					givePopularity(transaction.getProjectId(), memberId, TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, new BigDecimal(yyltValue),
							RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());

					MessageClient.sendMsgForProjectActivities(transaction.getProjectId(), "一羊领头",yyltValue, memberId);
					/*MessageClient.sendMsgForCommon(memberId, Constant.MSG_TEMPLATE_TYPE_BAIDU, MessageEnum.FIVE_PRIZE.getCode(),
							transaction.getMarkProjectName(), "一羊领头");*/
					logger.info("项目：" + transaction.getProjectId() + "一羊领头获得者：" + memberId);
				}

				// }

				if (project != null) {
					if (project.isNoviceProject()) {
						Long activityId = Long.parseLong(Config.activityIdForXiaomingStory);
						// 根据活动id查找活动
						Activity activity = activityManager.selectByPrimaryKey(activityId);
						if (activity != null) {
							// 判断活动是否已经开始
							if (DateUtils.isDateBetween(DateUtils.getCurrentDate(), activity.getStartTime(), activity.getEndTime())) {
								// 判断用户是否已经领取
								List<Coupon> coupons = couponManager.getCouponByMemberIdAndActivity(memberId, activityId);
								if (Collections3.isEmpty(coupons)) {
									couponManager.receiveCoupon(memberId, activityId, null, -1L);
								}
							}
						}
					}
				}

				// 生成合同
				//generateContractfinal(transaction.getId(), "web");
				// 添加redis
				addTransactionDetailForIndex(transaction, isFirstInvest, isFull, isMost, isLuck, project, member);
				// 清空缓存
				RedisForProjectClient.clearTransactionDetail(transaction.getProjectId());
				String key = RedisConstant.TRANSACTION_INVERESTLIST_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + transaction.getProjectId();
				RedisManager.removeObject(key);
				logger.info("交易完成后处理线程结束执行，transactionId：" + transaction.getId());
			} catch (Exception e) {
				logger.error("交易完成后处理线程结束发生异常，transactionId：" + transaction.getId(), e);
			}
		}

		/**
		 * 增加推荐人的人气值
		 * 
		 * @param member
		 * @throws ManagerException
		 */
		private void incrReferralPoints(Member member) throws ManagerException {
			if (member.getReferral() != null) {
				// 增加相应的人气值
				Long referralId = member.getReferral();
				BigDecimal value = transaction.getInvestAmount().multiply(new BigDecimal(Config.investRecommendScale))
						.setScale(0, BigDecimal.ROUND_DOWN);
				if (value.doubleValue() > 0) {
					givePopularity(transaction.getId(), referralId, TypeEnum.FIN_POPULARITY_TYPE_RECOMMEND, value,
							RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE.getRemarks());
				}
			}
		}
		/**
		 * 
		 * @Description:邀请好友增加人气值 （7月1后执行新规则）
		 * @param member
		 * @throws ManagerException
		 * @author: chaisen
		 * @time:2016年6月8日 上午9:35:41
		 */
		private void incrNewReferralPoints(Member member,Project project,Activity activit,Transaction transaction) throws ManagerException {
			if (member.getReferral() == null) {
				return;
			}
			ActivityForFiveRites rule = LotteryContainer.getInstance().getObtainConditions(activit, ActivityForFiveRites.class,
					ActivityConstant.INVITATION_FRIENDS_KEY);
			int value = 0;
			if (rule != null) {
				//新用户后360天内的投资有效
				if(member.getRegisterTime().compareTo(activit.getStartTime())>0){
					if(DateUtils.getIntervalDays(member.getRegisterTime(), transaction.getTransactionTime())+1<rule.getDay()){
						value = getPoints(transaction, project, rule);
					}
				//老用户
				}else{
					if(DateUtils.getIntervalDays(activit.getStartTime(), transaction.getTransactionTime())+1<rule.getDay()){
						value = getPoints(transaction, project, rule);
					} 
				}
			}
			// 增加相应的人气值
			Long referralId = member.getReferral();
			logger.info("交易id:" +transaction.getId()+ "用户：" + transaction.getMemberId() + "投资金额：" + transaction.getInvestAmount() +"收益天数："+transaction.getTotalDays() +"好友："+referralId +"获得人气值："+value);
			if (value > 0) {
				boolean flag = givePopularity(transaction.getId(), referralId, TypeEnum.FIN_POPULARITY_TYPE_RECOMMEND,
						new BigDecimal(value), RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE.getRemarks());
				if (flag) {
					ActivityLotteryResult result = new ActivityLotteryResult();
					result.setActivityId(activit.getId());
					result.setMemberId(member.getId());
					result.setRewardId(referralId.toString());
					result.setRemark(transaction.getId().toString());
					result.setRewardResult(String.valueOf(value));
					result.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
					activityLotteryResultMapper.insertSelective(result);

					//有钱一起赚活动在的话进行维护有钱一起赚活动的奖励数据
					Optional<Activity> inviteFriend = LotteryContainer.getInstance().getActivityByName(
							ActivityConstant.ACTIVITY_INVITEFRIENDS_NAME);
					if (inviteFriend.isPresent()) {
						Activity activity = inviteFriend.get();
						if (transaction.getTransactionTime().after(activity.getStartTime())&&
								transaction.getTransactionTime().before(activity.getEndTime())){
							ActivityLotteryResultNew resultNew = new ActivityLotteryResultNew();
							resultNew.setActivityId(activit.getId());
							resultNew.setMemberId(referralId);
							resultNew.setRewardId(member.getId().toString());
							resultNew.setRemark("POPULARITY");
							resultNew.setRewardResult(String.valueOf(value));
							resultNew.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
							resultNew.setStatus(1);
							activityLotteryResultNewMapper.insertSelectiveS(resultNew);
						}
					}
				}
			}
		}
	}

	//邀请好友人气值规则
	public Integer getPoints(Transaction transaction, Project project, ActivityForFiveRites rule) {
		int value = 0;
		BigDecimal days = BigDecimal.ZERO;
		int day=0;
		//债权项目 投资额*（收益天数/30）*0.3‰
		if(!project.isDirectProject()){
			if(transaction.getTotalDays()==null){
				day=0;
			}else{
				day=transaction.getTotalDays();
			}
			days = new BigDecimal(day).divide(new BigDecimal(30), 10, BigDecimal.ROUND_HALF_DOWN);
			value = transaction.getInvestAmount().multiply(rule.getInvestRecommendScale()).multiply(days).intValue();
		//直投项目
		}else{
			//日
			if (project.getBorrowPeriodType() == 1) {
				days = new BigDecimal(project.getBorrowPeriod()).divide(new BigDecimal(30), 10, BigDecimal.ROUND_HALF_DOWN);
			}
			//月
			if (project.getBorrowPeriodType() == 2) {
				days=new BigDecimal(project.getBorrowPeriod());
			}
			//年
			if (project.getBorrowPeriodType() == 3) {
				days=new BigDecimal(project.getBorrowPeriod()).multiply(new BigDecimal(12));
			}
			value = transaction.getInvestAmount().multiply(rule.getInvestRecommendScale()).multiply(days).intValue();
		}
		return value;
	}

	@Override
	public List<TransactionForFirstInvestAct> selectTopTenInvest() throws Exception {
		try {
			return transactionMapper.selectTopTenInvest();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 赠送人气值方法
	 * 
	 * @param senderId
	 * @param memberId
	 * @param income
	 * @param remarks
	 */
	public boolean givePopularity(Long senderId, Long memberId, TypeEnum type, BigDecimal income, String remarks) throws ManagerException {
		boolean result = false;
		try {
			if(income.intValue() < 1) {
				return false;
			}
			Balance balance = balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, income, memberId);
			/* 赠送人气值 */
			int flag = popularityInOutLogManager.insert(memberId, type, income, null, balance.getBalance(), senderId, // 人气值的来源：用户id
					remarks);
			if (flag > 0) {
				balanceManager.incrGivePlatformTotalPoint(income);// 平台累计送出人气值
																	// source_id：-1L
																	// 表示所有的人气值赠送都是平台
				result = true;
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return result;
	}

	@Override
	public int getTransactionCountByMember(Long memberId) throws Exception {
		try {
			return transactionMapper.getTransactionCountByMember(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getTotalInvestAmountByMemberId(Long memberId) throws ManagerException {
		try {
			return transactionMapper.getTotalInvestAmountByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Transaction> getTransactionByMap(@Param("map") Map map) throws ManagerException {
		try {
			return transactionMapper.getTransactionByMap(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal firstInvestmentAmount(Long memberId, Date startTime, Date endTime) throws Exception {
		try {
			return transactionMapper.firstInvestmentAmount(memberId, startTime, endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal countInvestmentAmount(Long memberId) throws ManagerException {
		try {
			return transactionMapper.countInvestmentAmount(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal largestInvestmentAmount(Long memberId, Date startTime, Date endTime) throws ManagerException {
		try {
			return transactionMapper.largestInvestmentAmount(memberId, startTime, endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int countInvestmentProject(Long memberId) throws ManagerException {
		try {
			return transactionMapper.countInvestmentProject(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Long getTodayFirstInvestmentMemberId() throws ManagerException {
		try {
			return transactionMapper.getTodayFirstInvestmentMemberId();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int countFriendsInvestmentNum(Long memberId, Date startTime, Date endTime) throws ManagerException {
		try {
			return transactionMapper.countFriendsInvestmentNum(memberId, startTime, endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal friendsFirstInvestmentAmount(Long memberId, Date startTime, Date endTime) throws ManagerException {
		try {
			return transactionMapper.friendsFirstInvestmentAmount(memberId, startTime, endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal friendsCountInvestmentAmount(Long memberId) throws ManagerException {
		try {
			return transactionMapper.friendsCountInvestmentAmount(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal friendsInvestmentMaxAmount(Long memberId, Date startTime, Date endTime) throws ManagerException {
		try {
			return transactionMapper.friendsInvestmentMaxAmount(memberId, startTime, endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int friendsCountInvestmentProjectNum(Long memberId) throws ManagerException {
		try {
			return transactionMapper.friendsCountInvestmentProjectNum(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Long firstInvestmentProjectMember(Long projectId, Date startTime, Date endTime) throws ManagerException {
		try {
			return transactionMapper.firstInvestmentProjectMember(projectId, startTime, endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Long lastInvestmentProjectMember(Long projectId, Date startTime, Date endTime) throws ManagerException {
		try {
			return transactionMapper.lastInvestmentProjectMember(projectId, startTime, endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal investmentMaxAmountProject(Long projectId, Long memberId) throws ManagerException {
		try {
			return transactionMapper.investmentMaxAmountProject(projectId, memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal friendsInvestmentMaxAmountProject(Long projectId, Long memberId) throws ManagerException {
		try {
			return transactionMapper.friendsInvestmentMaxAmountProject(projectId, memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal integralPointInvestmentProject(Long projectId, Long memberId, String punctuality, Date startTime, Date endTime)
			throws ManagerException {
		try {
			return transactionMapper.integralPointInvestmentProject(projectId, memberId, punctuality, startTime, endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal integralPointInvestment(Long memberId, String punctuality, Date startTime, Date endTime) throws ManagerException {
		try {
			return transactionMapper.integralPointInvestment(memberId, punctuality, startTime, endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取满足投资总额在某一范围内的用户
	 */
	@Override
	public List<TransactionForFirstInvestAct> getMemberMeetTotalInvestRange(Map<String, Object> map) throws ManagerException {
		try {
			return transactionMapper.getMemberMeetTotalInvestRange(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getCountMemberMeetTotalInvestRange(Map<String, Object> map) throws ManagerException {
		try {
			return transactionMapper.getCountMemberMeetTotalInvestRange(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public TransactionForMemberCenter getTransactionDetailForMember(Long transactionId, Long memberId) throws ManagerException {
		try {
			return transactionMapper.getTransactionDetailForMember(transactionId, memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getTransactionsTotalCount(Long memberId, int status) throws ManagerException {
		try {
			TransactionQuery query = new TransactionQuery();
			query.setMemberId(memberId);
			query.setStatus(status);
			Integer count = transactionMapper.selectTransactionsByQueryParamsTotalCount(query);
			if (count != null) {
				return count;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return 0;
	}

	@Override
	public Page<Transaction> findByPage(Page<Transaction> pageRequest, Map<String, Object> map) throws ManagerException {
			Page<Transaction> page = new Page<Transaction>();
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<Transaction> transactionList=  transactionMapper.findByPage(map);
			int totalCount = transactionMapper.findByPageCount(map);
			page.setData(transactionList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public void generateContractfinal(Long transactionId, String fromSys) {
		try {
			BscAttachment attachment = contractManager.saveContract(transactionId, fromSys);
			attachment.setListOrder(0);
			attachment.setUploadTime(DateUtils.getCurrentDate());
			attachment.setModule(Constant.ATTACHMENT_CONTRACT_IDENTITY);
			attachment.setDelFlag(Constant.ENABLE);
			attachment.setStorageWay(TypeEnum.ATTACHMENT_STORAGE_WAY_OSS.getCode());
			int rows = bscAttachmentManager.insertSelective(attachment);
			if (rows > 0) {
				// 如果有索引，则先把他删除
				attachmentIndexManager.deleteAttachmentIndexByKeyId(transactionId.toString());
				attachmentIndexManager.insertAttachmentIndex(attachment.getId(), transactionId.toString());

			}
			// 更新保全表的附件ID
			if (attachment.getPreservationId() != null && attachment.getPreservationId() > 0) {
				Preservation preservation = new Preservation();
				preservation.setPreservationId(attachment.getPreservationId());
				preservation.setAttachmentId(attachment.getId());
				preservationManager.updateByPreservationIdSelective(preservation);
			}
		} catch (Exception e) {
			logger.error("生成合同发生异常，transactionId：" + transactionId, e);
		}
	}

	@Override
	public List<Transaction> queryNotPreservTransForList(Map<String, Object> map) throws ManagerException {
		try {
			return transactionMapper.queryNotPreservTransForList(map);
		} catch (Exception e) {
			logger.error("查询没有创建保全的交易订单失败", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<TransactionForOrder> queryTansactionForList(Page<TransactionForOrder> pageRequest, Map<String, Object> map)
			throws ManagerException {
			Page<TransactionForOrder> page = new Page<TransactionForOrder>();
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<TransactionForOrder> transactionForOrderList=  transactionMapper.queryTansactionForList(map);
			int totalCount = transactionMapper.queryTansactionForListCount(map);
			page.setData(transactionForOrderList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		} catch (Exception e) {
			logger.error("查询没有创建保全的交易订单失败", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionForMemberCenter> selectTransactionMemberByPorjectId(Long projectId) throws ManagerException {
		try {
			return transactionMapper.selectTransactionMemberByPorjectId(projectId);
		} catch (Exception e) {
			logger.error("根据项目ID获取投资人信息失败", e);
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean isMemberInvested(Long memberId) throws ManagerException {
		boolean result = false;
		try {
			Integer invest = transactionMapper.isMemberInvest(memberId);
			if (invest == null) {
				result = false;
			} else {
				result = true;
			}
			return result;
		} catch (Exception e) {
			logger.error("查询会员是否投资过异常 memberId={}", memberId, e);
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean isMemberDirectInvest(Long memberId) throws ManagerException {
		boolean result = false;
		try {
			Integer invest = transactionMapper.isMemberDirectInvest(memberId);
			if (invest == 0) {
				result = false;
			} else {
				result = true;
			}
			return result;
		} catch (Exception e) {
			logger.error("查询会员是否投资过直投项目 异常 memberId={}", memberId, e);
			throw new ManagerException(e);
		}
	}
	
	@Override
	public BigDecimal totalMemberLeaseBonusAmounts(Long memberId) throws ManagerException {
		try {
			BigDecimal totalMemberLeaseBonusAmounts = transactionMapper.totalMemberLeaseBonusAmounts(memberId);
			if (totalMemberLeaseBonusAmounts == null) {
				return BigDecimal.ZERO;
			}
			return totalMemberLeaseBonusAmounts;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal totalMemberUsedCouponAmount(Long memberId) throws ManagerException {
		try {
			BigDecimal totalMemberUsedCouponAmount = transactionMapper.totalMemberUsedCouponAmount(memberId);
			if (totalMemberUsedCouponAmount == null) {
				return BigDecimal.ZERO;
			}
			return totalMemberUsedCouponAmount;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int totalMemberUsedCouponNum(Long memberId) throws ManagerException {
		try {
			Integer totalMemberUsedCouponNum = transactionMapper.totalMemberUsedCouponNum(memberId);
			if (totalMemberUsedCouponNum == null) {
				return 0;
			}
			return totalMemberUsedCouponNum;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Transaction getFirstTransaction(Long memberId) throws ManagerException {
		try {
			return transactionMapper.getFirstTransaction(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ActivityMonthlyMyRank findMyRank(Map<String, Object> map) throws ManagerException {
		try {
			return transactionMapper.findMyRank(map);
		} catch (Exception e) {
			logger.error("查询会员的排名失败map={}", map, e);
			throw new ManagerException(e);
		}
	}

	/**
	 * 交易成功之后，添加交易详情到缓存，首页 展示
	 */
	private void addTransactionDetailForIndex(Transaction transaction, boolean isFirstInvest, boolean isLastInvest, boolean isMostInvest,
			boolean isLuckInvest, Project project, Member member) {
		try {
			Long projectId = transaction.getProjectId();
			// 如果交易详情还未生成，则通过传入的交易数据转化
			TransactionForProject transactionForProject = transactionMapper.selectTransactionDetailById(transaction.getId());
			transactionForProject = BeanCopyUtil.map(transaction, TransactionForProject.class);
			if (transactionForProject != null) {
				transactionForProject = BeanCopyUtil.map(transaction, TransactionForProject.class);
			}
			transactionForProject.setFirstInvest(isFirstInvest);
			transactionForProject.setLastInvest(isLastInvest);
			transactionForProject.setAvatars(member.getAvatars());
			String maskUserName = member.getUsername();
			transactionForProject.setMobile(member.getMobile());
			transactionForProject.setUsername(maskUserName);
			maskUserName = transactionForProject.getUsername();
			transactionForProject.setMaskUserName(maskUserName);
			maskUserName = transactionForProject.getMaskUserName();
			transactionForProject.setMaskUserName(maskUserName);
			transactionForProject.setMobile(0L);
			String shortProjectName = StringUtil.getShortProjectName(project.getName());
			transactionForProject.setProjectName(shortProjectName);
			transactionForProject.setMostInvest(isMostInvest);// 一鸣惊人
			transactionForProject.setLuckInvest(isLuckInvest);// 幸运女神
			RedisForProjectClient.addTransactionDetailForIndex(transactionForProject);
			if (isFirstInvest || isLastInvest) {

				RedisForProjectClient.addTransactionDetailForQuadruplegift(transactionForProject);
			}
		} catch (Exception e) {
			logger.error("交易成功线程把交易详情添加到缓存失败,transactionId={}", transaction.getId(), e);
		}
	}

	/**
	 * 交易成功之后，添加交易详情到四崇礼缓存，首页 展示
	 */
	private void addTransactionDetailForIndexQuadruplegift(Transaction transaction, boolean isFirstInvest, boolean isLastInvest,
			boolean isMostInvest, boolean isLuckInvest, Project project, Member member) {
		try {
			Long projectId = transaction.getProjectId();
			// 如果交易详情还未生成，则通过传入的交易数据转化
			TransactionForProject transactionForProject = transactionMapper.selectTransactionDetailById(transaction.getId());
			transactionForProject = BeanCopyUtil.map(transaction, TransactionForProject.class);
			if (transactionForProject != null) {
				transactionForProject = BeanCopyUtil.map(transaction, TransactionForProject.class);
			}
			transactionForProject.setFirstInvest(isFirstInvest);
			transactionForProject.setLastInvest(isLastInvest);
			transactionForProject.setAvatars(member.getAvatars());
			String maskUserName = member.getUsername();
			transactionForProject.setMobile(member.getMobile());
			transactionForProject.setUsername(maskUserName);
			maskUserName = transactionForProject.getUsername();
			transactionForProject.setMaskUserName(maskUserName);
			maskUserName = transactionForProject.getMaskUserName();
			transactionForProject.setMaskUserName(maskUserName);
			transactionForProject.setMobile(0L);
			String shortProjectName = StringUtil.getShortProjectName(project.getName());
			transactionForProject.setProjectName(shortProjectName);
			transactionForProject.setMostInvest(isMostInvest);// 一鸣惊人
			transactionForProject.setLuckInvest(isLuckInvest);// 幸运女神
			if (isFirstInvest || isLastInvest || isMostInvest || isLuckInvest) {
				RedisForProjectClient.addTransactionDetailForQuadruplegift(transactionForProject);
			}
		} catch (Exception e) {
			logger.error("交易成功线程把交易详情添加到缓存失败,transactionId={}", transaction.getId(), e);
		}
	}

	/**
	 * 交易成功之后，添加交易详情到缓存
	 */
	private void addTransactionDetailForProject(Transaction transaction, boolean isFirstInvest) {
		try {
			Long projectId = transaction.getProjectId();
			if (projectId != null) {
				String key = RedisConstant.TRANSACTION_DETAIL_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + projectId;
				if (RedisManager.isExit(key) || isFirstInvest) {
					// 根据交易id查询交易详情，用于显示到项目详情页
					// 如果交易详情还未生成，则通过传入的交易数据转化
					TransactionForProject transactionForProject = transactionMapper.selectTransactionDetailById(transaction.getId());
					if (transactionForProject == null) {
						transactionForProject = BeanCopyUtil.map(transaction, TransactionForProject.class);
						if (transaction.getOrderId() != null) {
							Order order = orderManager.selectByPrimaryKey(transaction.getOrderId());
							if (order != null && order.getOrderSource() != null) {
								transactionForProject.setOrderSource(order.getOrderSource());
							}
						}
					}
					logger.info("项目transactionForProject={}", transactionForProject);
					RedisForProjectClient.addTransactionDetail(transactionForProject);
				} else {
					TransactionQuery transactionQuery = new TransactionQuery();
					transactionQuery.setProjectId(projectId);
					List<TransactionForProject> transactionForProjects = transactionMapper
							.selectTransactionDetailByQueryParams(transactionQuery);
					if (Collections3.isNotEmpty(transactionForProjects)) {
						RedisForProjectClient.addBatchTransactionDetail(projectId, transactionForProjects);
					}
				}
			}
		} catch (Exception e) {
			logger.error("交易成功线程把交易详情添加到缓存失败,transactionId={}", transaction.getId(), e);
		}
	}

	/**
	 * 设置获得一羊领头，一鸣惊人，一锤定音，幸运女神
	 */
	private void editTransactionDetailForProject(String remark, Transaction transaction) {
		try {
			Long projectId = transaction.getProjectId();
			String key = RedisConstant.TRANSACTION_DETAIL_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + projectId;
			TransactionForProject transactionForProject = new TransactionForProject();
			if (RedisManager.isExit(key)) {
				String tpStr = RedisManager.hget(key, String.valueOf(transaction.getId()));
				if (StringUtil.isNotBlank(tpStr)) {
					transactionForProject = JSON.parseObject(tpStr, TransactionForProject.class);// 缓存中存在，从缓存中取
				} else {
					transactionForProject = transactionMapper.selectTransactionDetailById(transaction.getId());// 缓存中不存在，根据交易id查询交易详情，用于显示到项目详情页
				}
				if (remark.equals(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks())) {
					transactionForProject.setMostInvest(true);
				}
				if (remark.equals(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks())) {
					transactionForProject.setLastInvest(true);
				}
				if (remark.equals(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks())) {
					transactionForProject.setLuckInvest(true);
				}
				if (remark.equals(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks())) {
					transactionForProject.setFirstInvest(true);
				}
				RedisForProjectClient.addTransactionDetail(transactionForProject);
			}
		} catch (Exception e) {
			logger.error("交易成功线程把修改交易详情缓存失败,transactionId={}", transaction.getId(), e);
		}

	}

	/**
	 * 项目详情页交易记录明细
	 * 
	 * @param transactionQuery
	 * @return
	 * @throws ManagerException
	 */
	@Override
	public List<TransactionForProject> selectTransactionDetailByQueryParams(TransactionQuery transactionQuery) throws ManagerException {
		Long projectId = transactionQuery.getProjectId();
		List<TransactionForProject> transactionForProjects = myTransactionManager.selectTransactionDetailByQueryParams(transactionQuery);
		
		
		
		
		if (Collections3.isNotEmpty(transactionForProjects)) {
			PopularityInOutLogQuery query = new PopularityInOutLogQuery();
			query.setSourceId(transactionQuery.getProjectId());
			query.setType(TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY.getType());

			// 设置一锤定音
			query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
			List<ActivityForFirstInvest> lastList = popularityInOutLogManager.selectActivityForFirstInvestList(query);
			if (Collections3.isNotEmpty(lastList)) {
				transactionForProjects.get(0).setLastInvest(true);
			}

			// 设置一羊领头
			query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
			List<ActivityForFirstInvest> firstList = popularityInOutLogManager.selectActivityForFirstInvestList(query);
			if (Collections3.isNotEmpty(firstList)) {
				transactionForProjects.get(transactionForProjects.size() - 1).setFirstInvest(true);
			}

			// 幸运女神
			query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
			List<ActivityForFirstInvest> luckList = popularityInOutLogManager.selectActivityForFirstInvestList(query);
			Long luckTransactionId = 0L;
			if (Collections3.isNotEmpty(luckList)) {
				ActivityForFirstInvest activityForFirstInvest = luckList.get(0);
				TransactionQuery luckQuery = new TransactionQuery();
				luckQuery.setProjectId(projectId);
				luckQuery.setMemberId(activityForFirstInvest.getMemberId());
				List<Transaction> luckTransactions = myTransactionManager.selectTransactionsByQueryParams(luckQuery);
				if (Collections3.isNotEmpty(luckTransactions)) {
					luckTransactionId = luckTransactions.get(0).getId();
				}
			}

			// 一鸣惊人
			query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
			List<ActivityForFirstInvest> mostList = popularityInOutLogManager.selectActivityForFirstInvestList(query);
			Long mostTransactionId = 0L;
			if (Collections3.isNotEmpty(mostList)) {
				mostTransactionId = myTransactionManager.selectMostTransactionByProject(projectId).getId();
			}

			// 一掷千金
			query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks());
			List<ActivityForFirstInvest> mostAndLastList = popularityInOutLogManager.selectActivityForFirstInvestList(query);
			if (Collections3.isNotEmpty(mostAndLastList)) {
				transactionForProjects.get(0).setMostAndLastInvest(true);
			}
			for (TransactionForProject transactionForProject : transactionForProjects) {
				// 设置一鸣惊人
				if (transactionForProject.getId().longValue() == mostTransactionId.longValue()) {
					transactionForProject.setMostInvest(true);
					;
				}
				// 设置幸运女神
				if (transactionForProject.getId().longValue() == luckTransactionId.longValue()) {
					transactionForProject.setLuckInvest(true);
				}
			}
			// 设置租赁分红收益
			for (TransactionForProject transactionForProject : transactionForProjects) {
				// 租赁分红明细
				List<LeaseBonusDetail> details = leaseBonusDetailMapper.findListByTransactionId(transactionForProject.getId());
				if (Collections3.isNotEmpty(details)) {
					transactionForProject.setLeaseBonusDetails(details);
				}
				// 租赁总额
				LeaseBonusDetail bonusDetail = leaseBonusDetailMapper.getLeaseTotalIncomeByTransactionId(transactionForProject.getId());
				if (bonusDetail != null) {
					transactionForProject.setLeaseBonusAmounts(bonusDetail.getBonusAmount());
					transactionForProject.setBonusAnnualizedRate(bonusDetail.getBonusRate());
				} else {
					transactionForProject.setLeaseBonusAmounts(BigDecimal.ZERO);
					transactionForProject.setBonusAnnualizedRate(BigDecimal.ZERO);
				}
			}
			for (TransactionForProject transaction : transactionForProjects) {
				Order order = orderManager.selectByPrimaryKey(transaction.getOrderId());
				Project project=projectManager.selectByPrimaryKey(transaction.getProjectId());
				BigDecimal extraInterest=BigDecimal.ZERO;
				if (order != null && order.getOrderSource() != null) {
					transaction.setOrderSource(order.getOrderSource());
				}
				if(transaction.getExtraProjectAnnualizedRate()!=null){
					transaction.setAnnualizedRate(transaction.getAnnualizedRate().add(transaction.getExtraProjectAnnualizedRate()));
				}
				
				if(transaction.getExtraInterestDay()!=null&&transaction.getExtraInterestDay()>0){
					extraInterest=FormulaUtil.calculateInterest(transaction.getInvestAmount(),transaction.getExtraAnnualizedRate(),transaction.getExtraInterestDay());
					if(extraInterest==null){
						extraInterest=BigDecimal.ZERO;
					}
				}else if(transaction.getExtraAnnualizedRate()!=null){
					if(project.isDirectProject()){
						extraInterest=projectManager.invertExtraInterest(project, order.getInvestAmount(), order.getOrderTime(), order.getExtraAnnualizedRate());
					}else{
						extraInterest=transaction.getTotalExtraInterest();
					}
					if(extraInterest==null){
						extraInterest=BigDecimal.ZERO;
					}
				}
				transaction.setTotalExtraInterest(extraInterest);
				transaction.setAllInterest(transaction.getTotalInterest());
				transaction.setTotalInterest(transaction.getTotalInterest().subtract(extraInterest));
			}
		}
		return transactionForProjects;
	}

	@Override
	public List<Transaction> getInvestHouseList(ActivityForInvest param) throws ManagerException {
		try {
			return transactionMapper.getInvestHouseList(param);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<Transaction> selectByTransferId(Long transferId) throws ManagerException {
		try {
			return transactionMapper.selectByTransferId(transferId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	@Override
	public ActivityForInvest getMyInvestHouseRank(ActivityForInvest param) throws ManagerException {
		try {
			return transactionMapper.getMyInvestHouseRank(param);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterestForMember> getTransactionsDetailForMember(TransactionInterestQuery query) throws ManagerException {
		try {
			return transactionMapper.getTransactionsDetailForMember(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public TransactionForActivity selectForProjectExtra(TransactionForActivity query) throws ManagerException {
		try {
			return transactionMapper.selectForProjectExtra(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public BigDecimal getTotalInvestAmountByProjectId(Long projectId) throws ManagerException {
		try {
			return transactionMapper.getTotalInvestAmountByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 根据项目的还款方式和周期,计算投资者的本息记录
	 * @param debtInterests
	 * @param project
	 * @return
	 */
	@Override
	public List<TransactionInterest> computeInvesterPrincipalAndInterest(Transaction transaction,List<DebtInterest> debtInterests, Project project) throws ManagerException {
		List<TransactionInterest> transactionInterests = Lists.newArrayList();
		//要还的期数
		int installmentNum = debtInterests.size();
		transaction.setInstallmentNum(installmentNum);
		//等本等息，每月应该还的本金， 最后一期 = 总投资额 - 前几期累积的本金
		BigDecimal avgInvert = transaction.getInvestAmount().divide(new BigDecimal(installmentNum), 10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal tempTotalInvert = BigDecimal.ZERO;
		if(transaction.getExtraInterestDay()!=null && transaction.getExtraInterestDay()>0){
			transaction.setExtraEndDay(DateUtils.addDate(debtInterests.get(0).getStartDate(), transaction.getExtraInterestDay()));
		}
		for (int i = 0;i<installmentNum;i++ ) {
			DebtInterest debtInterest  = debtInterests.get(i);
			TransactionInterest transactionInterest = new TransactionInterest();
			buildTransactionInterest(transaction, debtInterest, transactionInterest);
			// 后期需要根据收益类型不同而采用不同的计算方式，目前只有按日计息，按月付息
			String profitType = project.getProfitType();
			if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(profitType) ||  DebtEnum.RETURN_TYPE_ONCE.getCode().equals(profitType)) {
				//最后一期还本金
				BigDecimal principal = BigDecimal.ZERO;
				if (i == installmentNum - 1 ){
					principal = transaction.getInvestAmount();
				}else {
					principal = BigDecimal.ZERO;
				}
				buildTransactionIntestByDay(transaction, project, principal, debtInterest, transactionInterest);
			}
			//等本等息，（按月计息）
			if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(profitType) ) {
				// 最后一期 = 总投资额 - 前几期累积的本金
				if (i == installmentNum -1){
					avgInvert = transaction.getInvestAmount().subtract(tempTotalInvert);
					buildTransactionIntestByAvg(transaction, project,  avgInvert, debtInterest, transactionInterest);
				}else {
					tempTotalInvert = tempTotalInvert.add(avgInvert);
					buildTransactionIntestByAvg(transaction, project,  avgInvert, debtInterest, transactionInterest);
				}
			}
			
			// 等本等息按周还款
			if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(profitType) ) {
				// 最后一期本金 = 交易总投资额 - 前几期累积的本金
				if (i == installmentNum -1){
					avgInvert = transaction.getInvestAmount().subtract(tempTotalInvert);
					buildTransactionIntestByAvgAndWeek(transaction, project, avgInvert, transactionInterest);
				} else {
					tempTotalInvert = tempTotalInvert.add(avgInvert);
					buildTransactionIntestByAvgAndWeek(transaction, project, avgInvert, transactionInterest);
				}
			}
			// 按日计息，按季付息，到期还本
			if (DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(profitType)) {
				//最后一期应还本金
				BigDecimal payablePrincipal = BigDecimal.ZERO;
				if (i == installmentNum - 1 ){
					payablePrincipal = transaction.getInvestAmount();
				}else {
					payablePrincipal = BigDecimal.ZERO;
				}
				buildTransactionIntestByDaySeason(transaction, payablePrincipal, debtInterest, transactionInterest);
			}
			
			transactionInterests.add(transactionInterest);
		}
		return transactionInterests;
	}

	/**
	 * 
	 * @param transaction
	 * @param debtInterest
	 * @param transactionInterest
	 */
	private void buildTransactionInterest(Transaction transaction, DebtInterest debtInterest, TransactionInterest transactionInterest) {
		//直投项目风控审核：交易本息添加interest_id、project_id 、periods
		transactionInterest.setInterestId(debtInterest.getId());
		transactionInterest.setProjectId(debtInterest.getProjectId());
		transactionInterest.setPeriods(debtInterest.getPeriods());
		transactionInterest.setEndDate(debtInterest.getEndDate());
		transactionInterest.setMemberId(transaction.getMemberId());
		transactionInterest.setStartDate(debtInterest.getStartDate());
		transactionInterest.setStatus(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus());
		transactionInterest.setTransactionId(transaction.getId());
	}
	
	/**
	 * 
	 * @Description 等本等息按周还款
	 * @param transaction
	 * @param project
	 * @param payablePrincipal
	 * @param transactionInterest
	 * @author luwenshan
	 * @time 2016年10月27日 上午10:27:30
	 */
	private void buildTransactionIntestByAvgAndWeek(Transaction transaction, Project project, BigDecimal payablePrincipal, TransactionInterest transactionInterest) {
		// 年化利率
		BigDecimal annualizedRate = transaction.getAnnualizedRate();
		BigDecimal extraAnnualizedRate = transaction.getExtraAnnualizedRate();
		if (extraAnnualizedRate == null) {
			extraAnnualizedRate = BigDecimal.ZERO;
		}
		//项目加息收益率
		BigDecimal addProjectRate = transaction.getExtraProjectAnnualizedRate();
		if (addProjectRate == null) {
			addProjectRate = BigDecimal.ZERO;
		}
		//项目收益+项目加息收益+额外收益券收益
		BigDecimal annualizedRateAndExt = annualizedRate.add(addProjectRate).add(extraAnnualizedRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		if(transaction.getExtraInterestDay()!=null&&transaction.getExtraInterestDay()>0){
			annualizedRateAndExt = annualizedRate.add(addProjectRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		}
		// 年化利率+收益劵  含券收益
		BigDecimal payableInterest = FormulaUtil.calculateInterestByAvgWeek(transaction.getInvestAmount(), annualizedRateAndExt, 7);
		BigDecimal extraInterest=BigDecimal.ZERO;
		int extraDays=0;
		if(transaction.getExtraInterestDay()!=null&&transaction.getExtraInterestDay()>0){
			if(DateUtils.isDateBetween(transaction.getExtraEndDay(), transactionInterest.getStartDate(), transactionInterest.getEndDate())){
				extraDays=transaction.getExtraInterestDay();
				transactionInterest.setExtraEndDay(transaction.getExtraEndDay());
			}else if(transaction.getExtraEndDay().after(transactionInterest.getEndDate())){
				extraDays = DateUtils.getIntervalDays(transactionInterest.getStartDate(), transactionInterest.getEndDate()) + 1;
				transactionInterest.setExtraEndDay(transactionInterest.getEndDate());
			}
			extraInterest= FormulaUtil.calculateInterestByAvgSeason(transaction.getInvestAmount(),extraAnnualizedRate, extraDays);
			payableInterest=payableInterest.add(extraInterest);
		}else{
			// 额外利息    直接乘以  收益券利率
			extraInterest = FormulaUtil.calculateInterestByAvgWeek(transaction.getInvestAmount(), extraAnnualizedRate, 7);
		}
		//项目加息利息  直接乘以  项目加息利率
		BigDecimal addProjectInterest = FormulaUtil.calculateInterestByAvgWeek(transaction.getInvestAmount(),addProjectRate,7);
		// 应付利息
		transactionInterest.setPayableInterest(payableInterest);
		// 用收益劵， 额外付的利息
		transactionInterest.setExtraInterest(extraInterest);
		// 应还本金
		transactionInterest.setPayablePrincipal(payablePrincipal);
		//项目加息，额外的利息
		transactionInterest.setExtraProjectInterest(addProjectInterest);
		transactionInterest.setUnitInterest(payableInterest);
		transactionInterest.setUnitPrincipal(payablePrincipal);
	}
	
	/**
	 * 
	 * @Description 按日计息，按季付息，到期还本
	 * @param transaction
	 * @param payablePrincipal
	 * @param debtInterest
	 * @param transactionInterest
	 * @author luwenshan
	 * @time 2016年10月27日 上午10:50:15
	 */
	private void buildTransactionIntestByDaySeason(Transaction transaction, BigDecimal payablePrincipal, DebtInterest debtInterest, TransactionInterest transactionInterest) {
        // 年化利率
		BigDecimal annualizedRate = transaction.getAnnualizedRate();
		BigDecimal extraAnnualizedRate = transaction.getExtraAnnualizedRate();
		if (extraAnnualizedRate == null) {
			extraAnnualizedRate = BigDecimal.ZERO;
		}
		//项目加息收益率
		BigDecimal addProjectRate = transaction.getExtraProjectAnnualizedRate();
		if (addProjectRate == null) {
			addProjectRate = BigDecimal.ZERO;
		}
		//项目收益+项目加息收益+额外收益券收益
		BigDecimal annualizedRateAndExt = annualizedRate.add(addProjectRate).add(extraAnnualizedRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		if(transaction.getExtraInterestDay()!=null&&transaction.getExtraInterestDay()>0){
		//高额收益券
			annualizedRateAndExt = annualizedRate.add(addProjectRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		}
		int days = DateUtils.getIntervalDays(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
		// 年化利率+收益劵  
		BigDecimal payableInterest = FormulaUtil.calculateInterestByAvgSeason(transaction.getInvestAmount(),annualizedRateAndExt, days);
		BigDecimal extraInterest=BigDecimal.ZERO;
		int extraDays=0;
		if(transaction.getExtraInterestDay()!=null&&transaction.getExtraInterestDay()>0){
			if(DateUtils.isDateBetween(transaction.getExtraEndDay(), transactionInterest.getStartDate(), transactionInterest.getEndDate())){
				extraDays=transaction.getExtraInterestDay();
				transactionInterest.setExtraEndDay(transaction.getExtraEndDay());
			}else if(transaction.getExtraEndDay().after(transactionInterest.getEndDate())){
				extraDays = DateUtils.getIntervalDays(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
				transactionInterest.setExtraEndDay(transactionInterest.getEndDate());
			}
			extraInterest= FormulaUtil.calculateInterestByAvgSeason(transaction.getInvestAmount(),extraAnnualizedRate, extraDays);
			payableInterest=payableInterest.add(extraInterest);
		}else{
			//额外利息  
			extraInterest = FormulaUtil.calculateInterestByAvgSeason(transaction.getInvestAmount(),extraAnnualizedRate, days);
		}
		
		//项目加息利息  直接乘以  项目加息利率
		BigDecimal addProjectInterest = FormulaUtil.calculateInterestByAvgSeason(transaction.getInvestAmount(),addProjectRate,days);
		// 应付利息
		transactionInterest.setPayableInterest(payableInterest);
		//用收益劵， 额外付的利息
		transactionInterest.setExtraInterest(extraInterest);
		transactionInterest.setPayablePrincipal(payablePrincipal);
		//项目加息，额外的利息
		transactionInterest.setExtraProjectInterest(addProjectInterest);
		transactionInterest.setUnitInterest(payableInterest);
		transactionInterest.setUnitPrincipal(debtInterest.getUnitPrincipal());
	}

	/**
	 * 等本等息 构造投资者本息记录
	 * @param transaction
	 * @param project
	 * @param avgInvert
	 * @param debtInterest
	 * @param transactionInterest
	 */
	private void buildTransactionIntestByAvg(Transaction transaction, Project project, BigDecimal avgInvert, DebtInterest debtInterest, TransactionInterest transactionInterest) {
		// 年化利率
		BigDecimal annualizedRate = transaction.getAnnualizedRate();
		BigDecimal extraAnnualizedRate = transaction.getExtraAnnualizedRate();
		if (extraAnnualizedRate == null) {
			extraAnnualizedRate = BigDecimal.ZERO;
		}
		//项目加息收益率
		BigDecimal addProjectRate = transaction.getExtraProjectAnnualizedRate();
		if (addProjectRate == null) {
			addProjectRate = BigDecimal.ZERO;
		}
		//项目收益+项目加息收益+额外收益券收益
		BigDecimal annualizedRateAndExt = annualizedRate.add(addProjectRate).add(extraAnnualizedRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		if(transaction.getExtraInterestDay()!=null&&transaction.getExtraInterestDay()>0){
			annualizedRateAndExt= annualizedRate.add(addProjectRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		}
		BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(), transaction.getInvestAmount(), annualizedRateAndExt);
		transactionInterest.setUnitInterest(unitInterest);
		transactionInterest.setUnitPrincipal(debtInterest.getUnitPrincipal());
		//年化利率+收益劵  含券收益 直接乘以（项目利率+收益券利率）     
		BigDecimal payableInterest = FormulaUtil.calculateInterestByAvg(transaction.getInvestAmount(), annualizedRateAndExt);
		//年化利率算出的
		//BigDecimal annuoleInterest = FormulaUtil.calculateInterestByAvg(transaction.getInvestAmount(), annualizedRate);
		//额外利息    直接乘以  收益券利率
		//BigDecimal extraInterest = payableInterest.subtract(annuoleInterest);
		BigDecimal extraInterest=BigDecimal.ZERO;
		int extraDays=0;
		if(transaction.getExtraInterestDay()!=null&&transaction.getExtraInterestDay()>0){
		//高额收益券：
			if(DateUtils.isDateBetween(transaction.getExtraEndDay(), transactionInterest.getStartDate(), transactionInterest.getEndDate())){
				extraDays=transaction.getExtraInterestDay();
				transactionInterest.setExtraEndDay(transaction.getExtraEndDay());
			}else  if(transaction.getExtraEndDay().after(transactionInterest.getEndDate())){
				extraDays = DateUtils.getIntervalDays(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
				transactionInterest.setExtraEndDay(transactionInterest.getEndDate());
			}
			extraInterest= FormulaUtil.calculateInterestByAvgSeason(transaction.getInvestAmount(),extraAnnualizedRate, extraDays);
			payableInterest=payableInterest.add(extraInterest);
		}else{
			extraInterest = FormulaUtil.calculateInterestByAvg(transaction.getInvestAmount(),extraAnnualizedRate);
		}
		//项目加息利息  直接乘以  项目加息利率
		BigDecimal addProjectInterest = FormulaUtil.calculateInterestByAvg(transaction.getInvestAmount(),addProjectRate);
		//应付利息
		transactionInterest.setPayableInterest(payableInterest);
		//用收益劵， 额外付的利息
		transactionInterest.setExtraInterest(extraInterest);
		//项目加息，额外的利息
		transactionInterest.setExtraProjectInterest(addProjectInterest);
		//还本金
		transactionInterest.setPayablePrincipal(avgInvert);

	}

	/**
	 * 计算每期收益
	 * 按日计息，按月付息，到期还本  & 一次性还本付息
	 * @param transaction
	 * @param project
	 * @param payablePrincipal
	 * @param debtInterest
	 * @param transactionInterest
	 */
	private void buildTransactionIntestByDay(Transaction transaction, Project project,
											 BigDecimal payablePrincipal,DebtInterest debtInterest, TransactionInterest transactionInterest) {
		// 年化利率
		BigDecimal annualizedRate = transaction.getAnnualizedRate();
		//额外年化收益率（收益券）
		BigDecimal extraAnnualizedRate = transaction.getExtraAnnualizedRate();
		if (extraAnnualizedRate == null) {
			extraAnnualizedRate = BigDecimal.ZERO;
		}
		//项目加息收益率
		BigDecimal addProjectRate = transaction.getExtraProjectAnnualizedRate();
		if (addProjectRate == null) {
			addProjectRate = BigDecimal.ZERO;
		}
		//项目总收益率=项目收益+项目加息收益+普通收益券收益
		BigDecimal annualizedRateAndExt = annualizedRate.add(addProjectRate).add(extraAnnualizedRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		if(transaction.getExtraInterestDay()!=null&&transaction.getExtraInterestDay()>0){
			//有高额收益券时：项目总收益率=项目收益+项目加息收益（高额收益券另外计算）
			annualizedRateAndExt= annualizedRate.add(addProjectRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		}
		
		//单位利息
		BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(), transaction.getInvestAmount(), annualizedRateAndExt);
		//本期天数
		int dates = DateUtils.getIntervalDays(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
		transactionInterest.setUnitInterest(unitInterest);
		transactionInterest.setUnitPrincipal(debtInterest.getUnitPrincipal());
		//年化利率+收益劵   含券收益 直接乘以（项目利率+收益券利率）     
		//本期天数所有利息收入额
		BigDecimal payableInterest = FormulaUtil.calculateInterest(transaction.getInvestAmount(),annualizedRateAndExt,dates);
		//收益券收益额
		BigDecimal extraInterest=BigDecimal.ZERO;
		
		//年化利率算出的
		//BigDecimal annuoleInterest = FormulaUtil.calculateInterest(transaction.getInvestAmount(),annualizedRate,dates);
		//额外利息  直接乘以  收益券利率
		//BigDecimal extraInterest = payableInterest.subtract(annuoleInterest);
		
		//收益券可用天数
		int extraDays=0;
		//收益券收益额
		if(transaction.getExtraInterestDay()!=null&&transaction.getExtraInterestDay()>0){
			//高额收益券时
			if(DateUtils.isDateBetween(transaction.getExtraEndDay(), transactionInterest.getStartDate(), transactionInterest.getEndDate())){
				extraDays=transaction.getExtraInterestDay();
				transactionInterest.setExtraEndDay(transaction.getExtraEndDay());
			}else if(transaction.getExtraEndDay().after(transactionInterest.getEndDate())){
				extraDays=dates;
				transactionInterest.setExtraEndDay(transactionInterest.getEndDate());
			}
			extraInterest = FormulaUtil.calculateInterest(transaction.getInvestAmount(),extraAnnualizedRate,extraDays);
			payableInterest=payableInterest.add(extraInterest);
		}else{
			//
			extraDays=dates;
			extraInterest = FormulaUtil.calculateInterest(transaction.getInvestAmount(),extraAnnualizedRate,extraDays);
		}
		//项目加息利息  直接乘以  项目加息利率
		BigDecimal addProjectInterest = FormulaUtil.calculateInterest(transaction.getInvestAmount(),addProjectRate,dates);
		//应付利息
		transactionInterest.setPayableInterest(payableInterest);
		//用收益劵， 额外付的利息
		transactionInterest.setExtraInterest(extraInterest);
		//项目加息，额外的利息
		transactionInterest.setExtraProjectInterest(addProjectInterest);
		transactionInterest.setPayablePrincipal(payablePrincipal);
	}

	private void buildDirectProjectLoanHostingPayTrade(BigDecimal amount,Long payeeId,String batchPayNo,String remarks,Integer type,Long sourceId,AccountType accountType,IdType idType,List<TradeArgs> tradeArgList) throws ManagerException{
		HostingPayTrade hostingPayTrade = new HostingPayTrade();
		hostingPayTrade.setAmount(amount);
		hostingPayTrade.setPayeeId(payeeId);
		hostingPayTrade.setBatchPayNo(batchPayNo);
		hostingPayTrade.setRemarks(remarks);
		hostingPayTrade.setSourceId(sourceId);//放款的sourceId是项目Id
		hostingPayTrade.setTradeNo(SerialNumberUtil.generatePayTradeaNo(payeeId));//交易号通过收款人用户id生成
		hostingPayTrade.setTradeStatus(TradeStatus.WAIT_PAY.name());
		hostingPayTrade.setType(type);
		String ip = null;
		SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
		if (dict != null) {
			ip = dict.getValue();
		}
		hostingPayTrade.setUserIp(ip);
		hostingPayTradeManager.insertSelective(hostingPayTrade);
		TradeArgs tradeArg = new TradeArgs();
		tradeArg.setMoney(new Money(hostingPayTrade.getAmount()));
		if(SerialNumberUtil.getInternalMemberId().equals(payeeId)){
			tradeArg.setAccountType(AccountType.BASIC);
			tradeArg.setIdType(IdType.EMAIL);
		}else{
			tradeArg.setAccountType(AccountType.SAVING_POT);
			tradeArg.setIdType(IdType.UID);
		}
		tradeArg.setPayeeId(SerialNumberUtil.generateIdentityId(payeeId));
        tradeArg.setRemark(hostingPayTrade.getSummary());
        tradeArg.setTradeNo(hostingPayTrade.getTradeNo());
        tradeArgList.add(tradeArg);
	}
	
	
	private void afterHostingPayDirectProjectLoan(Project project,HostingPayTrade hostingPayTrade) throws Exception{
		/**
		 * 1.插入资金流水资金流水(类型为：11-满标放款,类型为：20-直投项目管理费）
		 * 2.如果该批付所有状态都为success，则调用放款成功处理接口
		 */
		// 资金流水的备注
		String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_LOAN.getRemarks(),
				StringUtil.getShortProjectName(project.getName()));
		if (TypeEnum.HOSTING_PAY_TRADE_MANAGER_FEE.getType() == hostingPayTrade.getType()
				|| TypeEnum.HOSTING_PAY_TRADE_GUARANTEEFEE.getType() == hostingPayTrade.getType()
				|| TypeEnum.HOSTING_PAY_TRADE_RISK_FEE.getType() == hostingPayTrade.getType()) {// 项目费率
			//记录的资金流水   类型为管理费，账户类型为：基本户
			Balance balance=new Balance();
			try{
				balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_BASIC);
			}catch(Exception e){
				logger.error("【放款】代付成功之后同步基本户失败",e);
				balance = balanceManager.queryBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_BASIC);
			}
			// 记录用户投资资金流水
			TypeEnum typeEnum = null;
			int feeType = 0;
			if (TypeEnum.HOSTING_PAY_TRADE_MANAGER_FEE.getType() == hostingPayTrade.getType()) {
				typeEnum = TypeEnum.FINCAPITALINOUT_TYPE_PAY_MANAGER_FEE;
				feeType = TypeEnum.FEE_TYPE_MANAGE.getType();
			} else if (TypeEnum.HOSTING_PAY_TRADE_GUARANTEEFEE.getType() == hostingPayTrade.getType()) {
				typeEnum = TypeEnum.FINCAPITALINOUT_TYPE_PAY_GUARANTEE_FEE;
				feeType = TypeEnum.FEE_TYPE_GUARANTEE.getType();
			} else if (TypeEnum.HOSTING_PAY_TRADE_RISK_FEE.getType() == hostingPayTrade.getType()) {
				typeEnum = TypeEnum.FINCAPITALINOUT_TYPE_PAY_RISK_FEE;
				feeType = TypeEnum.FEE_TYPE_RISK.getType();
			}
			capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(), typeEnum,
					hostingPayTrade.getAmount(), null, balance.getAvailableBalance(), hostingPayTrade.getId().toString(), remark,
					TypeEnum.BALANCE_TYPE_BASIC);
			//更新管理费数据（状态3-已收取、收取时间、实际收取值）
			int result = projectFeeManager.updateProjectFeeByProjectId(project.getId(), feeType,
					StatusEnum.MANAGEMENT_FEE_GATHER_STATE_GATHERED.getStatus());
		} else if (TypeEnum.HOSTING_PAY_TRADE_INTRODUCE_FEE.getType() == hostingPayTrade.getType()) { // 介绍费
			// 记录的资金流水 类型为管理费，账户类型为：基本户
			Balance balance = new Balance();
			try {
				balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
			} catch (Exception e) {
				logger.error("【放款】代付成功之后同步介绍费收取户余额失败", e);
				balance = balanceManager.queryBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
			}
			// 记录用户投资资金流水
			TypeEnum typeEnum = TypeEnum.FINCAPITALINOUT_TYPE_PAY_INTRODUCE_FEE;
			int feeType = TypeEnum.FEE_TYPE_INTRODUCER.getType();
			capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(), typeEnum, hostingPayTrade.getAmount(), null,
					balance.getAvailableBalance(), hostingPayTrade.getId().toString(), remark, TypeEnum.BALANCE_TYPE_BASIC);
			// 更新管理费数据（状态3-已收取、收取时间、实际收取值）
			projectFeeManager.updateProjectFeeByProjectId(project.getId(), feeType,
					StatusEnum.MANAGEMENT_FEE_GATHER_STATE_GATHERED.getStatus());
		} else if (TypeEnum.HOSTING_PAY_TRADE_QUICK_REWARD.getType() == hostingPayTrade.getType()){//快投奖励发放
			Balance balance = new Balance();
			try {
				balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
			} catch (Exception e) {
				logger.error("【放款】代付成功之后同步快投中奖用户余额失败", e);
				balance = balanceManager.queryBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
			}
			// 记录用户中奖资金流水
			capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(), TypeEnum.FINCAPITALINOUT_TYPE_PAY_QUICK_REWARD,
					hostingPayTrade.getAmount(), null, balance.getAvailableBalance(), hostingPayTrade.getId().toString(),RemarksEnum.QUICK_REWARD.getRemarks(),
					TypeEnum.BALANCE_TYPE_PIGGY);
			logger.info("【放款-快投发放奖励】用户={}资金流水，余额={}",hostingPayTrade.getPayeeId(),balance.getAvailableBalance());
			
			ProjectExtra projectExtra = projectExtraManager.getProjectQucikReward(hostingPayTrade.getSourceId());//sourceId是项目id	
			if(projectExtra.getActivityId()!=null){
				ActivityLotteryResult  upResult=new ActivityLotteryResult();
				upResult.setStatus(1);//发放奖励
				upResult.setMemberId(hostingPayTrade.getPayeeId());
				upResult.setRewardType(7);
				upResult.setRewardId(hostingPayTrade.getSourceId().toString());
				upResult.setActivityId(projectExtra.getActivityId());
				activityLotteryResultMapper.updateBatchLotteryResultRewardResultByActivityId(upResult);
			}
			
			
			
		}else {// 借款人
			//记录的资金流水   类型为满标放款，账户类型为：存钱罐
			Balance balance=new Balance();
			try{
				balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
			}catch(Exception e){	
				logger.error("【放款】代付成功之后同步基本户失败",e);
				balance = balanceManager.queryBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
			}
			// 记录用户投资资金流水
			capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(), TypeEnum.FINCAPITALINOUT_TYPE_LOAN,
					hostingPayTrade.getAmount(), null, balance.getAvailableBalance(), hostingPayTrade.getId().toString(), remark,
					TypeEnum.BALANCE_TYPE_PIGGY);
			logger.info("【放款】用户={}资金流水，余额={}",hostingPayTrade.getPayeeId(),balance.getAvailableBalance());
		}
		//所有放款代付都完成后续业务处理
		afterDirectProjectLoanProcess(project, hostingPayTrade);
	}

	private void afterDirectProjectLoanProcess(Project project, HostingPayTrade hostingPayTrade) throws Exception {
		//该批付号下的所有代付都是完成，则更新
		if(hostingPayTradeManager.countHostPayByBatchNoAndStatus(hostingPayTrade.getBatchPayNo(),TradeStatus.WAIT_PAY.name())<=0){
			//更新项目状态=52-已放款
			int result = projectManager.updateProjectStatus(StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus(),
					StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus(), hostingPayTrade.getSourceId());
			if(result>0){
				// 更新交易表的放款状态=2-已放款
				Transaction updateModel = new Transaction();
				updateModel.setLoanStatus(StatusEnum.TRANSACTION_LOAN_STATUS_LOANED.getStatus());
				updateModel.setProjectId(hostingPayTrade.getSourceId());
				transactionMapper.updateTransactionByProjectId(updateModel);
				//插入放款记录为已放款
				insertLoanDetail(project, hostingPayTrade);
			}
			
			//生成项目本息记录
//			List<DebtInterest> interests = projectManager.calculateInterest(project,project.getSaleComplatedTime());
//			logger.info("【直投项目放款】销售完成时间={}",project.getSaleComplatedTime());
//			int totalDays = 0;
//			if(Collections3.isNotEmpty(interests)){
//				for (DebtInterest debtInterest : interests) {
//					debtInterest.setDebtId(0L);
//					debtInterest.setProjectId(project.getId());
//					debtInterestMapper.insert(debtInterest);//插入本息记录
//				}
//				totalDays = DateUtils.getIntervalDays(interests.get(0).getStartDate(),interests.get(interests.size()-1).getEndDate())+1;
//				//更新项目开始时间和结束时间
//				Project p = new Project();
//				p.setStartDate(interests.get(0).getStartDate());
//				p.setEndDate(interests.get(interests.size()-1).getEndDate());
//				p.setId(project.getId());
//				projectManager.updateByPrimaryKeySelective(p);//TODO 重写一个更新开始时间和结束时间的方法
//			}
			//交易后续操作
			//afterDirectProjectLoanTransactionProcess(project, interests, totalDays);
		}
	}

	@Override
	public void afterDirectProjectFinishCollectTradeProcess(Project project, List<DebtInterest> interests, int totalDays) throws Exception {
		TransactionQuery transactionQuery = new TransactionQuery();
		transactionQuery.setProjectId(project.getId());
		List<Transaction> transactions = myTransactionManager.selectTransactionsByQueryParams(transactionQuery);//指定项目所有交易
		if (Collections3.isEmpty(transactions)) {
			return;
		}
		Transaction transaction = new Transaction();
		boolean isFirstInvest;
		boolean isFull;
		List<TransactionInterest> transactionInterests = Lists.newArrayList();
		for (int i = 0, size = transactions.size(); i < size; i++) {
			isFirstInvest = false;
			isFull = false;
			if (i == transactions.size() - 1) {
				isFull = true;
			}
			transaction = transactions.get(i);
			// 插入交易本息
			// 查询当前交易的交易本息，不存在插入
			int transationInterestCount = transactionInterestManager.selectTransactionInterestsCountByTransactionId(transaction.getId());//某笔交易的还款计划总数
			BigDecimal totalInterest = BigDecimal.ZERO;//总利息收益
			BigDecimal totalExtraInterest = BigDecimal.ZERO;//收益券增加的收益
			BigDecimal totalExtraProjectInterest = BigDecimal.ZERO;//项目加息增加的收益
			if (transationInterestCount <= 0) {
				transactionInterests = computeInvesterPrincipalAndInterest(transaction, interests, project);
				for (TransactionInterest transactionInterest : transactionInterests) {
					transactionInterest.setUnitPrincipal(transactionInterest.getPayablePrincipal());
					transactionInterest.setUnitInterest(transactionInterest.getPayableInterest().divide(
							new BigDecimal(
									DateUtils.getIntervalDays(transactionInterest.getStartDate(), transactionInterest.getEndDate()) + 1),
							10, BigDecimal.ROUND_HALF_UP));
					// 更新实付
					transactionInterest.setRealPayPrincipal(transactionInterest.getPayablePrincipal());
					transactionInterest.setRealPayInterest(transactionInterest.getPayableInterest());
					transactionInterest.setRealPayExtraInterest(transactionInterest.getExtraInterest());
					transactionInterest.setRealPayExtraProjectInterest(transactionInterest.getExtraProjectInterest());
					transactionInterest.setProjectId(transaction.getProjectId());
					transactionInterestManager.insert(transactionInterest);
					totalInterest = totalInterest.add(transactionInterest.getPayableInterest());
					totalExtraInterest = totalExtraInterest.add(transactionInterest.getExtraInterest());
					totalExtraProjectInterest = totalExtraProjectInterest.add(transactionInterest.getExtraProjectInterest());
				}
				// 更新交易的收益
				logger.info("交易id={} 更新项目状态为回款中", transaction.getId());
				transaction.setStatus(StatusEnum.TRANSACTION_REPAYMENT.getStatus());
				transaction.setTotalInterest(totalInterest);
				transaction.setTotalDays(totalDays);
				//直投项目风控审核：total_extra_interest
				transaction.setTotalExtraInterest(totalExtraInterest);
				transaction.setTotalExtraProjectInterest(totalExtraProjectInterest);
				if (project.getTransferFlag() != null && project.getTransferAfterInterest() != null && project.getTransferFlag() == 1
						&& transaction.getExtraInterestDay() <= 0) {
					transaction.setCanTransferDate(DateUtils.addDaysByDate(interests.get(0).getStartDate(), project.getTransferAfterInterest()));
				}
				transactionMapper.updateByPrimaryKeySelective(transaction);
			}
			// 增加用户累计投资额
			RedisMemberClient.addTotalInvestAmount(transaction.getMemberId(), transaction.getInvestAmount());
			// 活动引擎
			SPParameter parameter = new SPParameter();
			parameter.setMemberId(transaction.getMemberId());
			parameter.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
			parameter.setBiz(transaction);
			SPEngine.getSPEngineInstance().run(parameter);
			// 直投后续活动业务
			activityAfterTransactionManager.afterDirectProjectAuditEntry(transaction);
			//都玩带过来的用户，第一次交易回调都玩接口
			douwanManager.douwanFirstTransaction(transaction.getMemberId(),totalDays);
			// 募集完成，发送站内信和短信通知投资者 ，募集完成，开始计息
			logger.info("募集完成，发送站内信和短信通知投资者 ，募集完成，开始计息 transactionId={}", transaction.getId());
			MessageClient.sendMsgForCommon(transaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.P2P_RAISED.getCode(),
					DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_7),
					(transaction.getProjectName().contains("期")?transaction.getProjectName().substring(0, transaction.getProjectName().indexOf("期") + 1):transaction.getProjectName()),
					transaction.getInvestAmount().toString());
			//app消息通知用户
			MessageClient.sendMsgForCommon(transaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_DIRECT_PROJECT_SUCCESS.getCode(),
					project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName(),transaction.getId().toString());

			taskExecutor.execute(new ProcessAfterTransactionThread(transaction, isFull, isFirstInvest));
		}
	}

	private void insertLoanDetail(Project project, HostingPayTrade hostingPayTrade) throws Exception{
		LoanDetail loanDetail = new LoanDetail();
		BigDecimal platformPay = transactionMapper.getCouponAmountForPaltform(project.getId());
		if (platformPay == null) {
			platformPay = BigDecimal.ZERO;
		}
		loanDetail.setLoanAmount(project.getTotalAmount());
		loanDetail.setPlatformPay(platformPay);
		loanDetail.setProjectId(hostingPayTrade.getSourceId());
		loanDetail.setLoanType(2);//TODO 设置枚举2-放款给借款人
		loanDetail.setLoanStatus(1);//TODO 设置枚举1-放款状态
		loanDetail.setUserPay(project.getTotalAmount().subtract(platformPay).setScale(2, BigDecimal.ROUND_HALF_UP));
		loanDetailMapper.insertSelective(loanDetail);
	}


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<?> afterHostingRefund(String processStatus, String tradeNo, String outTradeNo) throws Exception {
		ResultDO<?> result = new ResultDO<>();
		HostingRefund refund = hostingRefundMapper.selectByTradeNo(tradeNo);
		HostingRefund hostingRefundForLock = hostingRefundMapper.getByIdForLock(refund.getId());
		if (hostingRefundForLock == null) {
			return result;
		}
		// 如果已经是最终状态，不处理，直接返回
		if (hostingRefundForLock.getRefundStatus().equals(RefundStatus.SUCCESS.name())
				|| hostingRefundForLock.getRefundStatus().equals(RefundStatus.FAILED.name())) {
			logger.info("托管退款tradeNo=" + tradeNo + "状态已经是最终状态：" + hostingRefundForLock.getRefundStatus());
			return result;
		}
		// 将交易状态置为最终状态
		hostingRefundForLock.setRefundStatus(processStatus);
		hostingRefundForLock.setOutTradeNo(outTradeNo);
		hostingRefundMapper.updateByPrimaryKeySelective(hostingRefundForLock);
		// 如果退款为成功状态
		Balance balance = new Balance();
		try {
			balance = balanceManager.synchronizedBalance(hostingRefundForLock.getReceiverId(), TypeEnum.BALANCE_TYPE_PIGGY);
		} catch (Exception e) {
			logger.error("退款查询同步存钱罐余额失败,memberId={}", hostingRefundForLock.getReceiverId());
			balance = balanceManager.queryBalance(hostingRefundForLock.getReceiverId(), TypeEnum.BALANCE_TYPE_PIGGY);
		}
		Project project = projectManager.selectByPrimaryKey(hostingRefundForLock.getProjectId());
		String remarks = StringUtil.getShortProjectName(project.getName());
		if(StringUtil.isNotBlank(hostingRefundForLock.getSummary())) {
			remarks += hostingRefundForLock.getSummary();
		}
		// 记录用户投资资金流水
		capitalInOutLogManager.insert(hostingRefundForLock.getReceiverId(), TypeEnum.FINCAPITALINOUT_TYPE_CAPITAL_FALLBACK,
				hostingRefundForLock.getAmount(), null, balance.getAvailableBalance(), hostingRefundForLock.getId().toString(),
				remarks, TypeEnum.BALANCE_TYPE_PIGGY);
		return result;
	}

	@Override
	public List<Transaction> selectTransactionsByQueryParamsFilterP2p(TransactionQuery transactionQuery) throws ManagerException {
		try {
			return transactionMapper.selectTransactionsByQueryParamsFilterP2p(transactionQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public ResultDO<ContractBiz> p2pViewContract(Long orderId, Long memberId)throws ManagerException{
		ResultDO<ContractBiz> result = new ResultDO<ContractBiz>();
		try {
			// TODO 前置校验
			Order order = orderManager.selectByPrimaryKey(orderId);
			if(order==null){
				result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return result;
			}
			if(!order.getMemberId().equals(memberId)){
				result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);//交易不属于该会员，则返回404
				return result;
			}
			if (order.getStatus() == StatusEnum.ORDER_PAYED_INVESTED.getStatus()) {
				Transaction transaction = myTransactionManager.getTransactionByOrderId(orderId);
				if (transaction == null) {
					result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
					return result;
				}
//				if (!transaction.getMemberId().equals(memberId)) {
//					result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
//					return result;
//				}
				ContractBiz contractBiz = new ContractBiz();
				//ContractBiz contractBiz = contractManager.getViewContract(transaction.getId());
				//投资完成展示保全链接
				contractBiz.setTransactionId(transaction.getId());
			
				contractBiz.setInvestDate(DateUtils.getStrFromDate(transaction.getTransactionTime(),
						DateUtils.DATE_FMT_4));
//2017-04-11换成上上签				
				Preservation preservation =  preservationManager.selectByTransactionId(transaction.getId());
				if(preservation != null && preservation.getPreservationId() != null && preservation.getPreservationId() > 0) {
//					Preservation retPreservation = preservationManager.getPreservationLink(preservation.getPreservationId());
//					if(retPreservation != null && StringUtil.isNotBlank(retPreservation.getPreservationLink())) {
//						contractBiz.setPreservationLink(retPreservation.getPreservationLink());
//					}
					Map<String,Object> mapPara =new HashMap<String,Object>();
					mapPara.put("transactionId", transaction.getId());
					mapPara.put("sourceId", memberId);
					ContractSign contractSign = contractSignMapper.getByMap(mapPara);
					if(contractSign!=null){
						String url = BestSignUtil.getContractArbitrationURL(contractSign.getMobile().toString(),contractSign.getSignId());
						contractBiz.setPreservationLink(url);
					}
				}


//				}


				Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());

				Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());
				MemberInfo memberInfo = memberInfoManager.getMemberInfoByMemberId(transaction.getMemberId());
				//乙方显示
				contractBiz.setIdentity(member.getIdentityNumber());
				contractBiz.setMobile(member.getMobile().toString());
				contractBiz.setName(member.getTrueName());
				contractBiz.setEmailBorrower(member.getEmail()!=null?member.getEmail():"");

				contractBiz.setAddress("");
				if(memberInfo!=null){
					contractBiz.setAddress(memberInfo.getCensusRegisterName()!=null?memberInfo.getCensusRegisterName():"");
				}

				//甲方显示
				Member borrowerMember = memberManager.selectByPrimaryKey(project.getBorrowerId());
				//Enterprise enterprise =enterpriseManager.selectByMemberID(project.getBorrowerId());
				Enterprise enterprise =enterpriseManager.selectByKey(project.getEnterpriseId());
				if(project.getBorrowerType()==2 && enterprise!=null){//企业
					contractBiz.setBorrowerType(ProjectEnum.PROJECT_BORROWER_ENTERPRISE_TYPE_DIRECT.getType());

					contractBiz.setOriginalCreditorName(enterprise.getFullName());
					//以下三项，没有就不显示
					contractBiz.setOriginalCreditorIdentityNumber(enterprise.getOrganizationCode());
					contractBiz.setOriginalCreditorPhone(enterprise.getTelephone()!=null?enterprise.getTelephone():"");
					contractBiz.setOriginalCreditorAddress(enterprise.getAddress()!=null?enterprise.getAddress():"");
					//企业时，邮箱字段为空
					contractBiz.setEmailLender("");

				}else{//个人
					contractBiz.setBorrowerType(ProjectEnum.PROJECT_BORROWER_PERSON_TYPE_DIRECT.getType());
					MemberInfo borrowerMemberMemberInfo = memberInfoManager.getMemberInfoByMemberId(project.getBorrowerId());
					contractBiz.setOriginalCreditorName(borrowerMember.getTrueName());
					contractBiz.setOriginalCreditorIdentityNumber(borrowerMember.getIdentityNumber());
					contractBiz.setOriginalCreditorPhone(borrowerMember.getMobile()+"");
					contractBiz.setOriginalCreditorAddress("");
					if(borrowerMemberMemberInfo!=null){
						contractBiz.setOriginalCreditorAddress(borrowerMemberMemberInfo.getCensusRegisterName()!=null?borrowerMemberMemberInfo.getCensusRegisterName():"");
					}
					contractBiz.setEmailLender(borrowerMember.getEmail()!=null?borrowerMember.getEmail():"");
				}

				DebtCollateral debtCollateral = debtCollateralManager.findCollateralByProjectId(transaction.getProjectId());
				String collateralDetails = "";
				Map<String, Object> ruleMap = JSON.parseObject(
						debtCollateral.getCollateralDetails(),
						new TypeReference<Map<String, Object>>() {
						});

				//担保
				if(ProjectEnum.PROJECT_DIRECT_GUARANTEE_TYPE_GUARANTEE.getCode().equals(debtCollateral.getDebtType()) ){
					collateralDetails = ruleMap.get("基本信息").toString();
				}

				//信用
				if(ProjectEnum.PROJECT_DIRECT_GUARANTEE_TYPE_CREDIT.getCode().equals(debtCollateral.getDebtType())){
					collateralDetails="无";
				}


				contractBiz.setCollateralDetails(collateralDetails);



				contractBiz.setOverdueFeeRate(project.getOverdueFeeRate());
				contractBiz.setTotalAmount(transaction.getInvestAmount());

				BigDecimal projectAnnualizedRate = project.getAnnualizedRate();
				if(transaction.getExtraProjectAnnualizedRate()!=null){
					projectAnnualizedRate = projectAnnualizedRate.add(transaction.getExtraProjectAnnualizedRate());
				}

				if(transaction.getExtraAnnualizedRate()!=null){
				contractBiz.setP2pExtraAnnualizedRate(transaction.getExtraAnnualizedRate());;
				contractBiz.setP2pAnnualizedRate(projectAnnualizedRate);
				contractBiz.setP2pTotalAnnualizedRate(projectAnnualizedRate.add(transaction.getExtraAnnualizedRate()));
				}else{
					contractBiz.setP2pTotalAnnualizedRate(projectAnnualizedRate);
				}

				contractBiz.setProfitPeriod(project.getProfitPeriod());
				contractBiz.setProfitType(DebtEnum.getEnumByCode(project.getProfitType()).getDesc());

				contractBiz.setUsageOfLoan(project.getBorrowDetail());
				contractBiz.setManageFeeRate(project.getManageFeeRate());
				contractBiz.setLateFeeRate(project.getLateFeeRate());
				contractBiz.setGuaranteeFeeRate(project.getGuaranteeFeeRate());



				Map<String,Object> map = Maps.newHashMap();
				map.put("transactionId", transaction.getId());
				 List<ContractSign>  contractSignList = contractSignManager.selectListByPrimaryKey(map);
				 Integer firstIsSign = 0;
				 Integer secondIsSign = 0;
				 Integer thirdIsSign = 0;
				 for(ContractSign con :contractSignList){
					 if(con.getType()==StatusEnum.CONTRACT_PARTY_FIRST.getStatus()){
						 if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							 firstIsSign=1;
						 }
					 }
					if(con.getType()==StatusEnum.CONTRACT_PARTY_SECOND.getStatus()){
						if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							secondIsSign=1;
						 }
					 }
					if(con.getType()==StatusEnum.CONTRACT_PARTY_THIRD.getStatus()){
						if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							thirdIsSign=1;
						 }
					}
				 }
				 contractBiz.setFirstIsSign(firstIsSign);
				 contractBiz.setSecondIsSign(secondIsSign);
				 contractBiz.setThirdIsSign(thirdIsSign);
				 contractBiz.setSignStatus(transaction.getSignStatus());
				/* 
				 //直投合同生成时，尚未确定起息日和还款日
				 contractBiz.setContractInterestDay("T（募集完成日）+"+project.getInterestFrom());
				 String contractRepaymentDay = "自起息日起";
				 if(project.getBorrowPeriodType()==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()){
					 contractRepaymentDay = contractRepaymentDay + "第"+ project.getBorrowPeriod() +"天";
				 }else if(project.getBorrowPeriodType()==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()){
					 contractRepaymentDay = contractRepaymentDay +  project.getBorrowPeriod() +"个月后";
				 }else if(project.getBorrowPeriodType()==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()){
					 contractRepaymentDay = contractRepaymentDay + project.getBorrowPeriod() +"年后";
				 }
				 contractBiz.setContractRepaymentDay(contractRepaymentDay);
				 
				if(project.getEndDate()!=null){
					contractBiz.setEndDate(DateUtils.getStrFromDate(project.getEndDate(), DateUtils.DATE_FMT_4));
				}else{
					contractBiz.setEndDate(contractRepaymentDay);
				}
				contractBiz.setStartTime((project.getStartDate()!=null?DateUtils.getStrFromDate(project.getStartDate(), DateUtils.DATE_FMT_4):contractBiz.getContractInterestDay()));*/
				 contractBiz.setStartTime("以借款人成功借款日为准");
				 contractBiz.setEndDate("以借款成功后项目界面显示的还款计划为准");

				 result.setResult(contractBiz);
//				transaction.getId()

			} /*else {
				BigDecimal annualizedRate = order.getAnnualizedRate();
				if (order.getExtraAnnualizedRate() != null) {
					annualizedRate = annualizedRate.add(order.getExtraAnnualizedRate()).setScale(2,
							BigDecimal.ROUND_HALF_UP);
				}
				ContractBiz contractBiz = contractManager.getPreviewContract(memberId, order.getProjectId(),
						order.getInvestAmount(), annualizedRate, order.getOrderTime());
				result.setResult(contractBiz);
			}*/
		return result;
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getMemberTotalInvestAmountDirectProject(Long memberId) throws ManagerException {
		try {
			return transactionMapper.getMemberTotalInvestAmountDirectProject(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getMemberTotalInvest(TransactionQuery transactionQuery) throws ManagerException {
		try {
			return transactionMapper.getMemberTotalInvest(transactionQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<TransactionForMemberCenter> selectAllTransactionWithP2P(TransactionQuery query) throws ManagerException {
		try {
			List<TransactionForMemberCenter> orderForMembers = transactionMapper.selectAllTransactionWithP2P(query);
			// 通过项目id和投资金额获取年化收益计算预期收益
			long count = transactionMapper.selectAllTransactionWithP2PCount(query);
			Page<TransactionForMemberCenter> page = new Page<TransactionForMemberCenter>();
			page.setData(orderForMembers);
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Page<DebtForLenderMember> getDebtInfoByLenderId(DebtForLenderQuery debtForLenderQuery) throws ManagerException {
		try {
			List<DebtForLenderMember> debtForLenderMember = transactionMapper.getDebtInfoByLenderId(debtForLenderQuery);
			long count =transactionMapper.getDebtInfoByLenderIdCount(debtForLenderQuery);
			Page<DebtForLenderMember> page = new Page<DebtForLenderMember>();
			page.setData(debtForLenderMember);
			page.setiDisplayLength(debtForLenderQuery.getPageSize());
			page.setiTotalRecords(count);
			page.setiTotalDisplayRecords(count);
			page.setPageNo(debtForLenderQuery.getCurrentPage());
			return page;

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtForLenderMember getStatisticalDataByLenderId(DebtForLenderQuery debtForLenderQuery) throws ManagerException {
		try {
			return transactionMapper.getStatisticalDataByLenderId(debtForLenderQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	private List<Long> getMobileList(String groupName)  {
		List<Long> mobileList = Lists.newArrayList();
		List<SysDict> sysDictList;
		try {
			sysDictList = sysDictManager.findByGroupName(groupName);
			for(SysDict sysDict : sysDictList){
				mobileList.add(Long.parseLong(sysDict.getValue()));
			}
		} catch (ManagerException e) {
			logger.info("项目满额获取通知手机号异常" + groupName);
		}

		return mobileList;
	}

	@Override
	public boolean isMemberInvestedProject(Long projectId, Long memberId) throws ManagerException {
		boolean result = false;
		try {
			Integer invest = transactionMapper.isMemberInvestedProject(projectId, memberId);
			if (invest == null) {
				result = false;
			} else {
				result = true;
			}
			return result;
		} catch (Exception e) {
			logger.error("查询会员是否投资过某项目异常 memberId={},projectId={}", memberId, projectId, e);
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getTotalTransactionReceivedInterestByProject(Long projectId) throws ManagerException {
		try {
			return transactionMapper.getTotalTransactionReceivedInterestByProject(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public BigDecimal getTotalTransactionReceivedInterestByTransferId(Long transferId) throws ManagerException {
		try {
			return transactionMapper.getTotalTransactionReceivedInterestByTransferId(transferId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getTotalAmountByMemberId(Long memberId, Date transactionTime) throws ManagerException {
		try {
			return transactionMapper.getTotalAmountByMemberId(memberId,transactionTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateTransactionTimeByPrimaryKey(Transaction record) throws ManagerException {
		try {
			return transactionMapper.updateTransactionTimeByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public void addTransactionDetailForIndexQuadruplegifts(Transaction transaction, boolean isFirstInvest, boolean isLastInvest,
			boolean isMostInvest, boolean isLuckInvest, Project project, Member member) {
		try {
			Long projectId = transaction.getProjectId();
			// 如果交易详情还未生成，则通过传入的交易数据转化
			TransactionForProject transactionForProject = transactionMapper.selectTransactionDetailById(transaction.getId());
			transactionForProject = BeanCopyUtil.map(transaction, TransactionForProject.class);
			if (transactionForProject != null) {
				transactionForProject = BeanCopyUtil.map(transaction, TransactionForProject.class);
			}
			transactionForProject.setFirstInvest(isFirstInvest);
			transactionForProject.setLastInvest(isLastInvest);
			transactionForProject.setAvatars(member.getAvatars());
			String maskUserName = member.getUsername();
			transactionForProject.setMobile(member.getMobile());
			transactionForProject.setUsername(maskUserName);
			maskUserName = transactionForProject.getUsername();
			transactionForProject.setMaskUserName(maskUserName);
			maskUserName = transactionForProject.getMaskUserName();
			transactionForProject.setMaskUserName(maskUserName);
			transactionForProject.setMobile(0L);
			String shortProjectName = StringUtil.getShortProjectName(project.getName());
			transactionForProject.setProjectName(shortProjectName);
			transactionForProject.setMostInvest(isMostInvest);// 一鸣惊人
			transactionForProject.setLuckInvest(isLuckInvest);// 幸运女神
			if (isFirstInvest || isLastInvest || isMostInvest || isLuckInvest) {
				RedisForProjectClient.addTransactionDetailForQuadruplegift(transactionForProject);
			}
		} catch (Exception e) {
			logger.error("交易成功线程把交易详情添加到缓存失败,transactionId={}", transaction.getId(), e);
		}
	}

	@Override
	public Transaction selectLastTransactionByProjectOrderById(Long projectId)
			throws ManagerException {
		try {
			return transactionMapper.selectLastTransactionByProjectOrderById(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionForFirstInvestAct> selectTopEightInvestAmount(Date startTime, Date endTime) throws ManagerException {
		try {
			return transactionMapper.selectTopEightInvestAmount(startTime,endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionForFirstInvestAct> selectTopTenInvestAmount(Date startTime, Date endTime) throws ManagerException {
		try {
			return transactionMapper.selectTopTenInvestAmount(startTime,endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getMyTotalInvestByMemberIdAndTime(Long memberId, Date startTime, Date endTime) throws ManagerException {
		try {
			return transactionMapper.getMyTotalInvestByMemberIdAndTime(memberId,startTime,endTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取用户未签署合同数量
	 */
	@Override
	public int getUnsignContractNum(Long memberId) throws ManagerException {
		try {
			List<Transaction> tranList = transactionMapper.getUnsignContractNum(memberId);
			return tranList.size();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * @desc 自动签署
	 * @author zhanghao
	 * 2016年7月11日下午2:11:07
	 */
	private class ContractAutoSignThread implements Runnable {

		private List<Transaction> tranList ;

		private Long memberId;

		private Integer type;//0全部，1募集中，2我的交易

		public ContractAutoSignThread(final List<Transaction> tranList,final  Long memberId,final Integer type) {
			this.tranList = tranList;
			this.memberId = memberId;
			this.type = type;
		}

		@Override
		@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
		public void run() {
			logger.info("自动签署合同线程开始执行，memberId：" + memberId +"type:"+type);
			try {
				for(Transaction tran:tranList){
					ResultDO<Object> resObj = contractCoreManager.autoSignSecond(tran.getId());
				}
				logger.info("自动签署合同线程开始执行，memberId：" + memberId +"type:"+type);
			} catch (Exception e) {
				logger.error("自动签署合同线程开始执行，memberId：" + memberId +"type:"+type, e);
			}
		}
	}


	@Override
	public List<Long> selectUnSignMember() throws ManagerException {
		try {
			return transactionMapper.selectUnSignMember();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 获取用户募集中未签署合同数量
	 */
	@Override
	public int getCollectSignableContractNum(Long memberId) throws ManagerException {
		try {
			List<Transaction> tranList = transactionMapper.getCollectSignableContractNum(memberId);
			return tranList.size();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取用户我的交易未签署合同数量
	 */
	@Override
	public int getTransactionSignableContractNum(Long memberId) throws ManagerException {
		try {
			List<Transaction> tranList = transactionMapper.getTransactionSignableContractNum(memberId);
			return tranList.size();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 一键签署
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int signAllContract(Long memberId) throws ManagerException {
		try {
			List<Transaction> tranList = transactionMapper.getSignableContractNum(memberId);
			int updateNum = transactionMapper.updateSignStatus(memberId);

			//异步开始合同自动签署
			taskExecutor.execute(new ContractAutoSignThread(tranList,memberId,0));

			return updateNum;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 一键签署
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int signAllContractByType(Long memberId,Integer type) throws ManagerException {
		try {
			List<Transaction> tranList = Lists.newArrayList();
			int updateNum = 0;
			if(type==1){
				tranList = transactionMapper.getCollectSignableContractNum(memberId);
				updateNum = transactionMapper.updateCollectSignableContractStatus(memberId);
			}else{
				tranList = transactionMapper.getTransactionSignableContractNum(memberId);
				updateNum = transactionMapper.updateTransactionSignableContractStatus(memberId);
			}

			//异步开始合同自动签署
			taskExecutor.execute(new ContractAutoSignThread(tranList,memberId,type));

			return updateNum;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 自动签署
	 */
	@Override
	public int signContractAuto(Long transactionId) throws ManagerException {
		try {
			ResultDO<Object> resObj = contractCoreManager.autoSignSecond(transactionId);
			if(resObj.isError()){
				return 0;
			}
			return 1;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取合同下载链接
	 */
	@Override
	public String getContractDownUrl(Long transactionId) throws ManagerException {
		String signUrl="";
		try {
			ResultDO<Object> signUrlResult = contractCoreManager.getDownUrl(transactionId);
			if(signUrlResult.isSuccess()){
				signUrl = (String) signUrlResult.getResult();
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return signUrl;
	}



	/**
	 * 手动签署
	 */
	@Override
	public String signContract(Long transactionId,int typeDevice,Integer source) throws ManagerException {
		String signUrl="";
		try {
			ResultDO<Object> signUrlResult = contractCoreManager.getSignUrl(transactionId,typeDevice,source);
			if(signUrlResult.isSuccess()){
				signUrl = (String) signUrlResult.getResult();
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return signUrl;
	}

	public void afterTransactionContract(Transaction transaction){
		//异步开始合同签署初始化
		taskExecutor.execute(new ContractInitAfterTransactionThread(transaction));
	}

	public void afterTransactionContractIsFullCoupon(Order order){
				try {
					double usedCapitalAmount = order.getUsedCapital().add(order.getPayAmount()).doubleValue();
					if (usedCapitalAmount == 0) {
						Transaction transaction = this.getTransactionByOrderId(order.getId());
						//异步开始合同签署初始化
						taskExecutor.execute(new ContractInitAfterTransactionThread(transaction));
					}
				} catch (ManagerException e) {
					logger.error("使用现金券全额投资发生异常，orderId：" + order.getId(), e);
				}
	}

	/**
	 * @desc 交易完成后初始化合同信息线程
	 * @author zhanghao
	 * 2016年7月11日下午2:11:07
	 */
	private class ContractInitAfterTransactionThread implements Runnable {

		private Transaction transaction;

		public ContractInitAfterTransactionThread(final Transaction transaction) {
			this.transaction = transaction;
		}

		@Override
		@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
		public void run() {
			logger.info("交易完成后合同初始化线程开始执行，transactionId：" + transaction.getId());
			try {
				Long transactionId=transaction.getId();
				//生成合同，发送第三方，初始化信息
				ResultDO<Object> preRDO = contractCoreManager.preSign(transactionId,"web");
				if(!preRDO.isSuccess()){
					
				}
				//甲方自动签名
				ResultDO<Object> firstRDO =contractCoreManager.autoSignFirst(transactionId);
				if(!firstRDO.isSuccess()){
					
				}
				//第三方自动签名
				ResultDO<Object> thirdRDO =contractCoreManager.autoSignThird(transactionId);
				if(!thirdRDO.isSuccess()){
					
				}
				//初始化成功，修改交易状态
				Transaction tra = new Transaction();
				tra.setId(transactionId);
				tra.setSignStatus(StatusEnum.CONTRACT_STATUS_UNSIGN.getStatus());
				updateByPrimaryKeySelective(tra);
				
				//用户选择自动签名时，乙方自动签名
				Member  member = memberManager.selectByPrimaryKey(transaction.getMemberId());
				if(member.getSignWay()==1){
					//自动签署，直接改为已签署
					tra.setId(transactionId);
					tra.setSignStatus(StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus());
					updateByPrimaryKeySelective(tra);
					//调用自动签署
					contractCoreManager.autoSignSecond(transactionId);
				}
				logger.info("交易完成后合同初始化线程结束执行，transactionId：" + transaction.getId());
			} catch (Exception e) {
				logger.error("交易完成后合同初始化线程结束发生异常，transactionId：" + transaction.getId(), e);
			}
		}
	}


	@Override
	public Page<Transaction> queryUnsignList(TransactionQuery query) throws ManagerException {
		try {
			List<Transaction> unSigns = transactionMapper.getUnsignContractNumPage(query);

			List<Transaction> tranList = transactionMapper.getUnsignContractNum(query.getMemberId());
			long count = tranList.size();
			Page<Transaction> page = new Page<Transaction>();
			page.setData(unSigns);
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	
	}
	
	@Override
	public int expireContract(Long projectId) throws ManagerException {
		try {
			int num = transactionMapper.expireContract(projectId);
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int expireContractForTransfer(Long transferId) throws ManagerException {
		try {
			int num = transactionMapper.expireContractForTransfer(transferId);
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public int expireHistoryContract() throws ManagerException {
		try {
			int num = transactionMapper.expireHistoryContract();
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	@Override
	public List<Transaction> getUnendTransaction() throws ManagerException {
		try {
			return transactionMapper.getUnendTransaction();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 同步签署信息
	 */
	@Override
	public ResultDO<Object> queryContractInfo(Long transactionId)  {
			ResultDO<Object> queryContractInfoResult = contractCoreManager.queryContractInfo(transactionId);
			return queryContractInfoResult;
	}

	@Override
	public int getTotalTransactionCount() throws ManagerException {
		try {
			return transactionMapper.getTotalTransactionCount();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getCountInterestByMemberId(Long memberId, int status,Date date) throws ManagerException {
		try {
			return transactionInterestMapper.getCountInterestByMemberId(memberId,status, date);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<TraceSourceCollectBiz> queryByPageCollectByTraceSource(TraceSourceCollectQuery query) {
		Page<TraceSourceCollectBiz> page=new Page<TraceSourceCollectBiz>();
		query.setStartRow((query.getCurrentPage()-1)*query.getPageSize());
		List<TraceSourceCollectBiz> traceSourceCollectBizs= transactionMapper.queryByPageCollectByTraceSource(query);
		for (TraceSourceCollectBiz biz:traceSourceCollectBizs) {
			Date date= transactionMapper.queryLastTransactionByMemberId(biz.getMemberid());
			String newmobile= StringUtil.maskString(biz.getMobile(), StringUtil.ASTERISK, 3, 2, 4);
			biz.setMobile(newmobile);
			biz.setLastinvest(date);
		}
		int totalCount = transactionMapper.queryCountCollectByTraceSource(query);
		page.setData(traceSourceCollectBizs);
		page.setiDisplayLength(query.getPageSize());
		page.setiTotalDisplayRecords(totalCount);
		page.setiTotalRecords(totalCount);
		page.setPageNo(query.getCurrentPage());
		return page;
	}

	@Override
	public TransactionInterest selectWaitPayByEndDate(int status, Date date) throws ManagerException {
		try {
			return transactionInterestMapper.selectWaitPayByEndDate(status, date);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public TransactionInterest selectRealPayByEndDate(Date date)
			throws ManagerException {
		try {
			return transactionInterestMapper.selectRealPayByEndDate(date);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public BigDecimal getMemberTotalInvestByStatus(TransactionQuery transactionQuery) throws ManagerException {
		try {
			return transactionMapper.getMemberTotalInvestByStatus(transactionQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	public Page<TransferRecordBiz> transferList(TransferRecordQuery query){
		List<TransferRecordBiz> traRecord = Lists.newArrayList();
		Page<TransferRecordBiz> page = new Page<TransferRecordBiz>();
		try {
			int count = transactionMapper.selectCountToTransferList(query);
			if(count >0){
				List<Transaction>  transactionList =  transactionMapper.selectTransctionToTransferList(query);
				for(Transaction tran :transactionList){
					TransferRecordBiz transRecord = new TransferRecordBiz();
					BeanCopyUtil.copy(tran,transRecord);
					//项目缩略图
					Project pro = projectManager.selectByPrimaryKey(tran.getProjectId());
					transRecord.setThumbnail(Config.ossPicUrl+pro.getThumbnail());
					//已转本金  
					Balance balance = balanceManager.queryBalance(tran.getId(), TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT);
					BigDecimal transferPrincipal = BigDecimal.ZERO;
					transferPrincipal = tran.getInvestAmount().subtract(balance!=null?balance.getAvailableBalance():tran.getInvestAmount());
					transRecord.setTransferPrincipal(transferPrincipal);
					//累计获得 交易遍历转让项目， 转让项目查询代付，求和取累计获得
					BigDecimal transferIncome = BigDecimal.ZERO;
					List<TransferProject> transferPro = transferProjectManager.queryTransferProjectListByTransactionId(tran.getId());
					for (TransferProject transPro : transferPro){
						List<HostingPayTrade> transferHostPay = hostingPayTradeManager.getTransferHostingPayTradeByTransferId(transPro.getId());
						for(HostingPayTrade hostPayTrad : transferHostPay){
							transferIncome = transferIncome.add(hostPayTrad.getAmount());
						}
					}
					transRecord.setTransferIncome(transferIncome);
					traRecord.add(transRecord);
				}
			}
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setData(traRecord);
		} catch (ManagerException e) {
			logger.error("获取转让记录列表异常，memberId：", query.getMemberId(), e);
		}
		return page;
	}


	public TransactionTransferRecordBiz transactionTransferList(TransferRecordQuery query) throws ManagerException {
		TransactionTransferRecordBiz transactionTransferRecordBiz = new TransactionTransferRecordBiz();
		List<TransferProject> transferList =  transferProjectManager.queryTransferProjectListByTransactionId(query.getTransactionId());
		List<TransferRecordListBiz> transferRecordListBiz = BeanCopyUtil.mapList(transferList, TransferRecordListBiz.class);
		for(TransferRecordListBiz transfer:transferRecordListBiz){
			if(transfer.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()){//投资中，显示剩余时间
				String remainingTime = DateUtils.betweenTwoDays(DateUtils.getCurrentDate(), transfer.getTransferEndDate());
				transfer.setRemainingTime(remainingTime);
			}else{//转让项目 结束时间  如果成功，取转让成功时间    如果失败,取流标时间
				if(transfer.getFailFlag()==0){
					transfer.setTransferEndDate(transfer.getTransferSaleComplatedTime());
				}else{
					transfer.setTransferEndDate(transfer.getFailTime());
				}
			}
			//已转本金  取交易表求和
			BigDecimal transferPrincipal = BigDecimal.ZERO;
			//转让价格  取交易表求和
			BigDecimal transferAmount = BigDecimal.ZERO;
			List<Transaction> transactionList= this.selectByTransferId(transfer.getId());
			for(Transaction tra : transactionList){
				transferPrincipal = transferPrincipal.add(tra.getTransferPrincipal());
				transferAmount = transferAmount.add(tra.getInvestAmount());
			}
			transfer.setTransferPrincipal(transferPrincipal);
			transfer.setTransferAmount(transferAmount);
			//手续费 根据费率 转让价格计算
			transfer.setTransferFee(transferAmount.multiply(transfer.getTransferRate()!=null?transfer.getTransferRate():BigDecimal.ZERO)
					.divide(new BigDecimal(100),10,BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP));
			
			//累计获得   根据项目id查询代付表
			BigDecimal transferIncome = BigDecimal.ZERO;
			List<HostingPayTrade> transferHostPay = hostingPayTradeManager.getTransferHostingPayTradeByTransferId(transfer.getId());
			for(HostingPayTrade hostPayTrad : transferHostPay){
				transferIncome = transferIncome.add(hostPayTrad.getAmount());
			}
			transfer.setTransferIncome(transferIncome);
		}
		transactionTransferRecordBiz.setTransferRecordListBiz(transferRecordListBiz);
		this.getTransferStatus(query.getTransactionId(),transactionTransferRecordBiz);
		
		return transactionTransferRecordBiz;
	}
	
	private void getTransferStatus(Long transactionId,TransactionTransferRecordBiz transactionTransferRecordBiz) throws ManagerException{
		
		//0：其他  1:发起转让  2：继续转让 3：终止转让  4：全部转让 5：已还款
		Transaction tran =  this.selectTransactionById(transactionId);
		Project project = projectManager.selectByPrimaryKey(tran.getProjectId());
		transactionTransferRecordBiz.setRemarks("");
		transactionTransferRecordBiz.setFlag(1);

		if(tran.getStatus()==StatusEnum.TRANSACTION_COMPLETE.getStatus()){
			transactionTransferRecordBiz.setFlag(5);
			return;
		}
		
		if(tran.getTransferStatus()==StatusEnum.TRANSACTION_TRANSFER_STATUS_ALL_TRANSFERED.getStatus()){
			transactionTransferRecordBiz.setFlag(4);
			return;
		}
		
		if(tran.getTransferStatus()==StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus()){
			transactionTransferRecordBiz.setFlag(3);
			return;
		}
		
		if(tran.getTransferStatus()==StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus()){
			transactionTransferRecordBiz.setFlag(2);
		}
		
		if(project.getPrepayment()==1){//标记提前还款的项目不能进行转让
			transactionTransferRecordBiz.setRemarks("即将提前还款，不可转让");
		}
		
		if(overdueLogManager.isOverDueUnrepayment(project.getId())){//逾期未还，即不可转让，不管是否垫资逾期
			transactionTransferRecordBiz.setRemarks("项目逾期，不可转让");
		}
		
		if(tran.getCanTransferDate()==null||tran.getExtraInterestDay()>0){//高额加息券可转时间不写入，高额加息券不可转让
			transactionTransferRecordBiz.setRemarks("投资使用高额收益券项目，暂不支持转让");
		}
		
		//交易是否处于可转让期（项目可转让期）
		TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
		transactionInterestQuery.setTransactionId(transactionId);
		List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);

		Date lastEndDate = traList.get(0).getEndDate();
		Date firstStartDate = traList.get(0).getStartDate();
		Date currentEndDate = traList.get(0).getEndDate();
		Date currentStartDate = traList.get(0).getStartDate();
		Integer days = 0;
		for(TransactionInterest tra :traList){
			if(lastEndDate.getTime()<tra.getEndDate().getTime()){//遍历最大的还款时间，即为最后一期
				lastEndDate = tra.getEndDate();
			}
			if(firstStartDate.getTime()>tra.getStartDate().getTime()){//遍历最小的起始时间，即为第一期
				firstStartDate = tra.getStartDate();
			}
			if(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime() ==tra.getEndDate().getTime()
					){
				transactionTransferRecordBiz.setRemarks("项目还款当天，不能发起转让");
			}
			//当期期
			if(tra.getStartDate().getTime() < DateUtils.getCurrentDate().getTime() 
					&& DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime() <= tra.getEndDate().getTime() ){
				currentEndDate = tra.getEndDate();
				currentStartDate =  tra.getStartDate();
			}
		}
		//最后一期还款日当天
		if(lastEndDate==DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3)){
			transactionTransferRecordBiz.setRemarks("即将到期，不可转让");
		}
		
		//距离最近一期还款日当天
		if(currentEndDate==DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3)){
			transactionTransferRecordBiz.setRemarks("1天后可转让");
		}
		
		//起息日后X个自然日可以转让
		int transferAfterInterest = DateUtils.daysOfTwo( firstStartDate , DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))+1;
		if(!(project.getTransferAfterInterest() < 
				(DateUtils.daysOfTwo( firstStartDate , DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))+1))){
			transactionTransferRecordBiz.setRemarks((project.getTransferAfterInterest() - transferAfterInterest+ 1) + "天后可转让");
		}
		//今天是否二次转让
		TransferProject isAlreadytransferProject=  transferProjectManager.selectByTransactionIdToday(transactionId);
		
		if(isAlreadytransferProject!=null){
			transactionTransferRecordBiz.setRemarks("每个项目每天只能提交一次转让申请");
		}
	}
	
	
	
	private BigDecimal getProjectBalanceById(Long id) {
		//可用余额
		BigDecimal availableBalance = null;
		try {
			//从缓存中找可用余额
			//availableBalance = RedisProjectClient.getProjectBalance(id);
			if(availableBalance == null){
				//logger.info("项目"+id+"，可用余额在redis未找到。");
				//如果为Null，到余额表找
				Balance _balance = balanceManager.queryBalance(id, TypeEnum.BALANCE_TYPE_PROJECT);
				if(_balance != null){
					availableBalance = _balance.getAvailableBalance();
				}else{
					logger.debug("项目"+id+"，可用余额在余额表未找到。");
				}
			}
			if(availableBalance == null){
				//再没有，那只能从项目中去找了
				Project project = projectManager.selectByPrimaryKey(id);
				availableBalance = project.getTotalAmount();
			}
			logger.debug("项目"+id+"，可用余额"+availableBalance);
		} catch (ManagerException e) {
			logger.error("项目"+id+"查找",e);
		}
		return availableBalance;
	}
	
	private String getProjectNumberProgress(BigDecimal totalAmount, BigDecimal availableBalance){
		String  progress = "0";
		if(availableBalance != null){
			if(availableBalance.compareTo(BigDecimal.ZERO) <= 0){
				progress = "100";
			}else if(availableBalance.compareTo(totalAmount) == 0){
				progress = "0";
			}else{
				progress = new DecimalFormat("###.##").format((totalAmount.subtract(availableBalance)).divide(totalAmount,4,RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
			}
		}
		return progress;
	}

	@Override
	public Integer updateByTransferId(Transaction transaction) throws ManagerException {
		try {
			return transactionMapper.updateByTransferId(transaction);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public ResultDO<TransferContractBiz> viewZtTransferContract(Long orderId, Long memberId)throws ManagerException{
		ResultDO<TransferContractBiz> result = new ResultDO<TransferContractBiz>();
		try {
			// TODO 前置校验
			Order order = orderManager.selectByPrimaryKey(orderId);
			if(order==null){
				result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return result;
			}
			if(!order.getMemberId().equals(memberId)){
				result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);//交易不属于该会员，则返回404
				return result;
			}
			if (order.getStatus() == StatusEnum.ORDER_PAYED_INVESTED.getStatus()) {
				Transaction transaction = myTransactionManager.getTransactionByOrderId(orderId);
				if (transaction == null) {
					result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
					return result;
				}
				if(transaction.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_NORMAL.getType()){
					result.setResultCode(ResultCode.TRANSFER_NOT_BELONG_TRANSFER);
					return result;
				}
				
				TransferContractBiz tranContract = new TransferContractBiz();
				
//				Preservation preservation =  preservationManager.selectByTransactionId(transaction.getId());
//				if(preservation != null && preservation.getPreservationId() != null && preservation.getPreservationId() > 0) {
//					Preservation retPreservation = preservationManager.getPreservationLink(preservation.getPreservationId());
//					if(retPreservation != null && StringUtil.isNotBlank(retPreservation.getPreservationLink())) {
//						tranContract.setPreservationLink(retPreservation.getPreservationLink());
//					}

//				}
				Map<String,Object> mapPara =new HashMap<String,Object>();
				mapPara.put("transactionId", transaction.getId());
				mapPara.put("sourceId", memberId);
				ContractSign contractSign = contractSignMapper.getByMap(mapPara);
				if(contractSign!=null){
					String url = BestSignUtil.getContractArbitrationURL(contractSign.getMobile().toString(),contractSign.getSignId());
					tranContract.setPreservationLink(url);
				}
				tranContract.setContractId(transaction.getId().toString());
				tranContract.setTransactionId(transaction.getId());
				tranContract.setTransactionTime(DateUtils.getStrFromDate(transaction.getTransactionTime(),
						DateUtils.DATE_FMT_4));
				tranContract.setTransferPrincipal(transaction.getTransferPrincipal().toString());
				tranContract.setInvestAmount(transaction.getInvestAmount().toString());


				TransferProject transferProject = transferProjectManager.selectByPrimaryKey(transaction.getTransferId());
				 
				//甲方
				Member firstMan = memberManager.selectByPrimaryKey(transferProject.getMemberId());
				MemberInfo firstMemberInfo = memberInfoManager.getMemberInfoByMemberId(firstMan.getId());
				
				tranContract.setFirstName(firstMan.getTrueName());
				tranContract.setFirstMobile(firstMan.getMobile().toString());
				tranContract.setFirstIdentity(firstMan.getIdentityNumber());
				tranContract.setFirstEmail(firstMan.getEmail());
				tranContract.setFirstAddress("");
				if(firstMemberInfo!=null){
					tranContract.setFirstAddress(firstMemberInfo.getCensusRegisterName()!=null?firstMemberInfo.getCensusRegisterName():"");
				}
				 
				//乙方
				Member secondMan = memberManager.selectByPrimaryKey(transaction.getMemberId());
				MemberInfo secondInfo = memberInfoManager.getMemberInfoByMemberId(secondMan.getId());
				
				tranContract.setSecondName(secondMan.getTrueName());
				tranContract.setSecondMobile(secondMan.getMobile().toString());
				tranContract.setSecondIdentity(secondMan.getIdentityNumber());
				tranContract.setSecondEmail(secondMan.getEmail());
				tranContract.setSecondAddress("");
				if(secondInfo!=null){
					tranContract.setSecondAddress(secondInfo.getCensusRegisterName()!=null?secondInfo.getCensusRegisterName():"");
				}
				
				Transaction originalTransaction = myTransactionManager.selectByPrimaryKey(transferProject.getTransactionId());
				Member  originalMan = memberManager.selectByPrimaryKey(originalTransaction.getMemberId());
				Project originalPro = projectManager.selectByPrimaryKey(originalTransaction.getProjectId());
				tranContract.setOriginalBorrower(originalMan.getTrueName());
				tranContract.setOriginalTransactionId(originalTransaction.getId().toString());
				tranContract.setOriginalTransactionAmount(originalTransaction.getInvestAmountStr());
				tranContract.setOriginalTransactionTime(DateUtils.getStrFromDate(originalTransaction.getTransactionTime(),
						DateUtils.DATE_FMT_4));
				
				tranContract.setOriginalAnnualizedRate(originalTransaction.getAnnualizedRate().toString());;
				tranContract.setOriginalEndDate(DateUtils.getStrFromDate(originalPro.getEndDate(),
						DateUtils.DATE_FMT_4));
				tranContract.setTransferAfterInterest(originalPro.getTransferAfterInterest());
				SysDict returnTypeDict = sysDictManager.findByGroupNameAndKey("return_type", originalPro.getProfitType());
				
				tranContract.setOriginalProfitType(returnTypeDict.getLabel());
				
				tranContract.setResidualDays(transaction.getTotalDays().toString());
				
				
				tranContract.setTransferAmount(transferProject.getTransactionAmount().toString());
				
			
				
				SysDict dict = sysDictManager.findByGroupNameAndKey("transferRate", "transferRate");
				tranContract.setTransferRate(dict.getValue());

				
				BigDecimal currentInterest = BigDecimal.ZERO;//当期利息
				BigDecimal residualPrincipal = BigDecimal.ZERO;//剩余本金
				int days = 0 ; //当期天数
				int currentDays = 0 ; //当期总天数
				TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
				transactionInterestQuery.setTransactionId(originalTransaction.getId());
				List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
				for(TransactionInterest tra :traList){
					if(tra.getEndDate().getTime()< transferProject.getTransferStartDate().getTime() ){
						continue;
					}
					if(tra.getEndDate().getTime() == transferProject.getTransferStartDate().getTime()){
						continue;
					}
					if(transferProject.getTransferStartDate().getTime() < tra.getEndDate().getTime() ){
						residualPrincipal = residualPrincipal.add(tra.getPayablePrincipal());
					}
					//当期利息
					if(tra.getStartDate().getTime() < transferProject.getTransferStartDate().getTime() 
							&& transferProject.getTransferStartDate().getTime() < tra.getEndDate().getTime() ){
						currentInterest = tra.getPayableInterest();
						days = DateUtils.daysOfTwo( tra.getStartDate(),DateUtils.formatDate(transferProject.getTransferStartDate(), DateUtils.DATE_FMT_3));;
						currentDays = DateUtils.daysOfTwo(tra.getStartDate(), tra.getEndDate()) + 1;
					}
				}
				
				currentInterest = currentInterest.multiply(new BigDecimal(days).divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP))
						.setScale(2, BigDecimal.ROUND_HALF_UP);
				
				residualPrincipal =residualPrincipal.multiply(transaction.getTransferPrincipal())
						.divide(transferProject.getTransactionAmount(),2,BigDecimal.ROUND_HALF_UP);
				tranContract.setResidualPrincipal(residualPrincipal.toString());
				
				tranContract.setCurrentInterest(currentInterest.toString());
				BigDecimal transferCurrentInterest =currentInterest.multiply(transaction.getTransferPrincipal())
						.divide(transferProject.getTransactionAmount(),2,BigDecimal.ROUND_HALF_UP);
				tranContract.setTransferCurrentInterest(transferCurrentInterest.toString());
				
				
				DebtCollateral debtCollateral = debtCollateralManager.findCollateralByProjectId(transaction.getProjectId());
				String collateralDetails = "";
				
				if(debtCollateral!=null){
					Map<String, Object> ruleMap = JSON.parseObject(
							debtCollateral.getCollateralDetails(),
							new TypeReference<Map<String, Object>>() {
							});
					
					//担保
					if(ProjectEnum.PROJECT_DIRECT_GUARANTEE_TYPE_GUARANTEE.getCode().equals(debtCollateral.getDebtType()) ){
						collateralDetails = ruleMap.get("基本信息").toString();
					}
					
					//信用				
					if(ProjectEnum.PROJECT_DIRECT_GUARANTEE_TYPE_CREDIT.getCode().equals(debtCollateral.getDebtType())){
						collateralDetails="无";
					}
				}
				tranContract.setCollateralDetails(collateralDetails);
				
				
				Map<String,Object> map = Maps.newHashMap();
				map.put("transactionId", transaction.getId());
				 List<ContractSign>  contractSignList = contractSignManager.selectListByPrimaryKey(map);
				 Integer firstIsSign = 0;
				 Integer secondIsSign = 0;
				 Integer thirdIsSign = 0;
				 for(ContractSign con :contractSignList){
					 if(con.getType()==StatusEnum.CONTRACT_PARTY_FIRST.getStatus()){
						 if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							 firstIsSign=1;
						 }
					 }
					if(con.getType()==StatusEnum.CONTRACT_PARTY_SECOND.getStatus()){
						if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							secondIsSign=1;
						 }
					 }
					if(con.getType()==StatusEnum.CONTRACT_PARTY_THIRD.getStatus()){
						if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							thirdIsSign=1;
						 }
					}
				 }
				 tranContract.setFirstIsSign(firstIsSign);
				 tranContract.setSecondIsSign(secondIsSign);
				 tranContract.setThirdIsSign(thirdIsSign);
				 tranContract.setSignStatus(transaction.getSignStatus());
				 result.setResult(tranContract);
			} 
		return result;
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public ResultDO<TransferContractBiz> transferContract(Long orderId, Long memberId)throws ManagerException{
		ResultDO<TransferContractBiz> result = new ResultDO<TransferContractBiz>();
		try {
			// TODO 前置校验
			Order order = orderManager.selectByPrimaryKey(orderId);
			if(order==null){
				result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return result;
			}
			if(!order.getMemberId().equals(memberId)){
				result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);//交易不属于该会员，则返回404
				return result;
			}
			if (order.getStatus() == StatusEnum.ORDER_PAYED_INVESTED.getStatus()) {
				Transaction transaction = myTransactionManager.getTransactionByOrderId(orderId);
				if (transaction == null) {
					result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
					return result;
				}
				if(transaction.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_NORMAL.getType()){
					result.setResultCode(ResultCode.TRANSFER_NOT_BELONG_TRANSFER);
					return result;
				}
				
				TransferContractBiz tranContract = new TransferContractBiz();

//				Preservation preservation =  preservationManager.selectByTransactionId(transaction.getId());
//				if(preservation != null && preservation.getPreservationId() != null && preservation.getPreservationId() > 0) {
//					Preservation retPreservation = preservationManager.getPreservationLink(preservation.getPreservationId());
//					if(retPreservation != null && StringUtil.isNotBlank(retPreservation.getPreservationLink())) {
//						tranContract.setPreservationLink(retPreservation.getPreservationLink());
//					}

//				}
				Map<String,Object> mapPara =new HashMap<String,Object>();
				mapPara.put("transactionId", transaction.getId());
				mapPara.put("sourceId", memberId);
				ContractSign contractSign = contractSignMapper.getByMap(mapPara);
				if(contractSign!=null){
					String url = BestSignUtil.getContractArbitrationURL(contractSign.getMobile().toString(),contractSign.getSignId());
					tranContract.setPreservationLink(url);
				}
				tranContract.setContractId(transaction.getId().toString());
				tranContract.setTransactionId(transaction.getId());
				tranContract.setTransactionTime(DateUtils.getStrFromDate(transaction.getTransactionTime(),
						DateUtils.DATE_FMT_4));
				tranContract.setTransferPrincipal(transaction.getTransferPrincipal().toString());
				tranContract.setInvestAmount(transaction.getInvestAmount().toString());
				
				TransferProject transferProject = transferProjectManager.selectByPrimaryKey(transaction.getTransferId());
				 
				//甲方
				Member firstMan = memberManager.selectByPrimaryKey(transferProject.getMemberId());
				MemberInfo firstMemberInfo = memberInfoManager.getMemberInfoByMemberId(firstMan.getId());
				
				tranContract.setFirstName(firstMan.getTrueName());
				tranContract.setFirstMobile(firstMan.getMobile().toString());
				tranContract.setFirstIdentity(firstMan.getIdentityNumber());
				tranContract.setFirstEmail(firstMan.getEmail());
				tranContract.setFirstAddress("");
				if(firstMemberInfo!=null){
					tranContract.setFirstAddress(firstMemberInfo.getCensusRegisterName()!=null?firstMemberInfo.getCensusRegisterName():"");
				}
				 
				//乙方
				Member secondMan = memberManager.selectByPrimaryKey(transaction.getMemberId());
				MemberInfo secondInfo = memberInfoManager.getMemberInfoByMemberId(secondMan.getId());
				
				tranContract.setSecondName(secondMan.getTrueName());
				tranContract.setSecondMobile(secondMan.getMobile().toString());
				tranContract.setSecondIdentity(secondMan.getIdentityNumber());
				tranContract.setSecondEmail(secondMan.getEmail());
				tranContract.setSecondAddress("");
				if(secondInfo!=null){
					tranContract.setSecondAddress(secondInfo.getCensusRegisterName()!=null?secondInfo.getCensusRegisterName():"");
				}
				
				Transaction originalTransaction = myTransactionManager.selectByPrimaryKey(transferProject.getTransactionId());
				//Member  originalMan = memberManager.selectByPrimaryKey(originalTransaction.getMemberId());
				Project originalPro = projectManager.selectByPrimaryKey(originalTransaction.getProjectId());
				//tranContract.setOriginalBorrower(originalMan.getTrueName());
				tranContract.setOriginalTransactionId(originalTransaction.getId().toString());
				tranContract.setOriginalTransactionAmount(originalTransaction.getInvestAmountStr());
				tranContract.setOriginalTransactionTime(DateUtils.getStrFromDate(originalTransaction.getTransactionTime(),
						DateUtils.DATE_FMT_4));
				
				tranContract.setOriginalResidualDays(originalTransaction.getTotalDays().toString());
				tranContract.setOriginalAnnualizedRate(originalTransaction.getAnnualizedRate().toString());;
				tranContract.setOriginalEndDate(DateUtils.getStrFromDate(originalPro.getEndDate(),
						DateUtils.DATE_FMT_4));
				SysDict returnTypeDict = sysDictManager.findByGroupNameAndKey("return_type", originalPro.getProfitType());
				
				tranContract.setOriginalProfitType(returnTypeDict.getLabel());
			
				
				tranContract.setResidualDays(transaction.getTotalDays().toString());
				tranContract.setTransferAmount(transferProject.getTransactionAmount().toString());
				tranContract.setAnnualizedRate(transferProject.getTransferAnnualizedRate().toString());
				
				SysDict dict = sysDictManager.findByGroupNameAndKey("transferRate", "transferRate");
				tranContract.setTransferRate(dict.getValue());
				BigDecimal transferRateFee = BigDecimal.ZERO;
				transferRateFee = new BigDecimal(dict.getValue()).multiply(transferProject.getTransferAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
				tranContract.setTransferRateFee(transferRateFee.toString());
				
				
				
				//预期赚取，此时交易本息还未生成，只能通过原项目的交易本息，计算收益，根据份数分割
				BigDecimal currentInterest = BigDecimal.ZERO;//当期利息
				//BigDecimal residualPrincipal = BigDecimal.ZERO;//剩余本金
				BigDecimal residualInterest = BigDecimal.ZERO;//剩余利息
				int days = 0 ; //当期天数
				int currentDays = 0 ; //当期总天数
				TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
				transactionInterestQuery.setTransactionId(originalTransaction.getId());
				List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
				for(TransactionInterest tra :traList){
					if(tra.getEndDate().getTime()< transferProject.getTransferStartDate().getTime() ){
						continue;
					}
					if(tra.getEndDate().getTime() == transferProject.getTransferStartDate().getTime()){
						continue;
					}
					if(transferProject.getTransferStartDate().getTime() < tra.getEndDate().getTime() ){
						//residualPrincipal = residualPrincipal.add(tra.getPayablePrincipal());
						residualInterest = residualInterest.add(tra.getPayableInterest());
					}
					//当期利息
					if(tra.getStartDate().getTime() < transferProject.getTransferStartDate().getTime() 
							&& transferProject.getTransferStartDate().getTime() < tra.getEndDate().getTime() ){
						currentInterest = tra.getPayableInterest();
						days = DateUtils.daysOfTwo( tra.getStartDate(),DateUtils.formatDate(transferProject.getTransferStartDate(), DateUtils.DATE_FMT_3));;
						currentDays = DateUtils.daysOfTwo(tra.getStartDate(), tra.getEndDate()) + 1;
					}
				}
				/*currentInterest = currentInterest.multiply(new BigDecimal(days).divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP))
						.setScale(2, BigDecimal.ROUND_HALF_UP);*/
				//tranContract.setResidualPrincipal(residualPrincipal.toString());
				//tranContract.setCurrentInterest(currentInterest.toString());
				// 预期赚取  = 剩余 利息 +折价    
				//residualInterest = residualInterest.subtract(currentInterest);
				BigDecimal expectedEarning = residualInterest;
				expectedEarning = transaction.getTransferPrincipal().multiply(expectedEarning)
						.divide(transferProject.getTransactionAmount(),2,BigDecimal.ROUND_HALF_UP);
				//取收益
				tranContract.setExpectedEarning(expectedEarning.toString());

				
				//抵押物
				DebtBiz debtBiz = debtManager.getFullDebtInfoById(originalPro.getDebtId());
				if(debtBiz.getDebtCollateral()!=null) {
					DebtCollateral debtCollateral = debtBiz.getDebtCollateral();
					Map<String, Object> ruleMap = JSON.parseObject(
							debtCollateral.getCollateralDetails(),
							new TypeReference<Map<String, Object>>() {
							});
					if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CAR.getCode())) {
						if(debtBiz.getInstalment()==1){
							tranContract.setCollateralDetails(ruleMap.get("base_info").toString());
						}else{
							tranContract.setCollateralDetails(ruleMap.get("car_cx").toString());
						}
					}
					if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSE.getCode())) {
						tranContract.setCollateralDetails(ruleMap.get("house_fwzl").toString()+ruleMap.get("house_fwlx").toString()+ruleMap.get("house_jzmj").toString());
					}
					if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode())) {
						tranContract.setCollateralDetails(ruleMap.get("houseRecord_info").toString());
					}
					if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CARPAYIN.getCode())) {
						tranContract.setCollateralDetails(ruleMap.get("carPayIn_info").toString());
					}
					if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CARBUSINESS.getCode())) {
						tranContract.setCollateralDetails(ruleMap.get("base_info").toString());
					}
				}
				if(debtBiz.getDebtPledge()!=null) {
					DebtPledge debtPledge = debtBiz.getDebtPledge();
					Map<String, Object> ruleMap = JSON.parseObject(
							debtPledge.getPledgeDetails(),
							new TypeReference<Map<String, Object>>() {
							});
					String pledgeType = debtPledge.getPledgeType();
					if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CAR.getCode())) {
						if(debtBiz.getInstalment()==1){
							tranContract.setCollateralDetails(ruleMap.get("base_info").toString());
						}else{
							tranContract.setCollateralDetails(ruleMap.get("car_cx").toString());
						}
					}
					if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_NEWCAR.getCode()) || pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_EQUITY.getCode())) {
						tranContract.setCollateralDetails(ruleMap.get("car_ms").toString());
					}
					if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode())) {
						tranContract.setCollateralDetails(ruleMap.get("houseRecord_info").toString());
					}
				}
				
				
				Map<String,Object> map = Maps.newHashMap();
				map.put("transactionId", transaction.getId());
				 List<ContractSign>  contractSignList = contractSignManager.selectListByPrimaryKey(map);
				 Integer firstIsSign = 0;
				 Integer secondIsSign = 0;
				 Integer thirdIsSign = 0;
				 for(ContractSign con :contractSignList){
					 if(con.getType()==StatusEnum.CONTRACT_PARTY_FIRST.getStatus()){
						 if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							 firstIsSign=1;
						 }
					 }
					if(con.getType()==StatusEnum.CONTRACT_PARTY_SECOND.getStatus()){
						if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							secondIsSign=1;
						 }
					 }
					if(con.getType()==StatusEnum.CONTRACT_PARTY_THIRD.getStatus()){
						if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							thirdIsSign=1;
						 }
					}
				 }
				 tranContract.setFirstIsSign(firstIsSign);
				 tranContract.setSecondIsSign(secondIsSign);
				 tranContract.setThirdIsSign(thirdIsSign);
				 tranContract.setSignStatus(transaction.getSignStatus());
				 result.setResult(tranContract);
			} 
		return result;
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	/**
	 * 查询用户是否有投资募集中的项目
	 * 
	 * @param
	 * @return
	 */
	@Override
	public int selectCollectForMemberCounting(CollectingProjectQuery query)throws ManagerException {
		try {
			int num  =transactionMapper.selectCollectForMemberCounting(query);
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionInterestForMember> getTransactionInterestDetailForMemberTransfer(
			TransactionInterestQuery query) throws ManagerException {
		try {
			return transactionInterestMapper.getTransactionInterestDetailForMemberTransfer(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransferProject> getTransferDetailForMember(
			TransactionInterestQuery query) throws ManagerException {
		try {
			return transferProjectMapper.getTransferDetailForMember(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Transaction firstInvestmentByMemberId(Long memberId) throws ManagerException {
		try {
			return transactionMapper.firstInvestmentByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Transaction totalInvestInterest(Long memberId) throws ManagerException {
		try {
			return transactionMapper.getTotalTransactionReceivedInterestByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getMemberTotalInvestByMemberId(Long memberId, Date startTime, Date endTime, Integer totalDays) throws ManagerException {
		try {
			return transactionMapper.getMemberTotalInvestByMemberId(memberId,startTime,endTime,totalDays);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public BigDecimal getTotalInvestAmount(Date date) throws ManagerException {
		try {
			return transactionMapper.getTotalInvestAmount(DateUtils.zerolizedTime(date),DateUtils.getEndTime(date));
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Transaction> getTotalInvestAmountList(int pageSize) throws ManagerException {
		try {
			return transactionMapper.getTotalInvestAmountList(pageSize);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getMemberTotalInvestByMemberIdAndTotalDays(Map<String, Object> map) throws ManagerException {
		try {
			return transactionMapper.getMemberTotalInvestByMemberIdAndTotalDays(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public List<TransactionForFirstInvestAct> selectTopTenInvestAmountByActivityId(Date startTime, Date endTime, Long activityId) throws ManagerException {
		try {
			return transactionMapper.selectTopTenInvestAmountByActivityId(startTime,endTime,activityId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getInvestAmountByMemberId(Long memberId, Long activityId ,Date endTime,Date startTime) throws ManagerException {
		try {
			return transactionMapper.getInvestAmountByMemberId(memberId,activityId,endTime,startTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public BigDecimal getInvestAmountByMemberIdAndTime(Long memberId ,Date startTime,Date endTime ,Date activityStartTime) throws ManagerException {
		try {
			return transactionMapper.getInvestAmountByMemberIdAndTime(memberId,startTime,endTime,activityStartTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransactionForFirstInvestAct> selectTopInvestAmountByActivityIdAndTime(Date startTime, Date endTime, Date activityStartTime) throws ManagerException {
		 List<TransactionForFirstInvestAct> listTransaction=Lists.newArrayList();
		List<ActivityFirstInvest> listFirstInvest=transactionMapper.selectTopInvestAmountByActivityIdAndTime(startTime,endTime,activityStartTime);
		if(Collections3.isNotEmpty(listFirstInvest)){
			for(ActivityFirstInvest invest:listFirstInvest){
				TransactionForFirstInvestAct act=new TransactionForFirstInvestAct();
				act.setTotalInvest(invest.getTotalInvest());
				try {
					Member member=memberManager.selectByPrimaryKey(invest.getMemberId());
					if(member!=null){
						act.setAvatars(member.getAvatars());	
						act.setMobile(member.getMobile());
						act.setUsername(member.getUsername());
					}
				} catch (ManagerException e) {
					throw new ManagerException(e);
				}
				listTransaction.add(act);
			}
		}
		return listTransaction;
	}
	/**
	 * 
	 * @desc 每日冠军榜
	 * @param activityId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author chaisen
	 * @time 2016年12月7日 下午4:12:36
	 *
	 */
	@Override
	public List<TransactionForFirstInvestAct> getEverydayFirstAmount(Long activityId,Date startTime, Date endTime){
		int totalDays=DateUtils.getIntervalDays(DateUtils.formatDate(startTime, DateUtils.DATE_FMT_3),
				DateUtils.formatDate(endTime, DateUtils.DATE_FMT_3))+1;
		List<TransactionForFirstInvestAct> listTransaction=Lists.newArrayList();
		for(int i=0;i<totalDays;i++){
			Date day=DateUtils.addDate(startTime, i);
			if(DateUtils.getStrFromDate(day, DateUtils.DATE_FMT_3).equals(DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))){
				break;
			}
			TransactionForFirstInvestAct act=getFirstOneInvestAmount(activityId,day);
			if(act!=null){
				listTransaction.add(act);
			}
			
		}
		return listTransaction;
	}

	@Override
	public BigDecimal getTransferRate(Long transactionId) {
		try {
			TransactionInterest transactionInterest= transactionInterestMapper.queryFirstInterest(transactionId);
			if (transactionInterest==null){
				return null;
			}
			if (transactionInterest.getStartDate()==null){
				return null;
			}
			int days= DateUtils.daysBetween(transactionInterest.getStartDate(),new Date())+1;//今天也算持有天数
			SysDict dict= sysDictManager.findByGroupNameAndKey("transferRate_group","Rate");
			if (dict==null){
				return null;
			}
			List<TransferRateList> rateBizs=JSON.parseObject(dict.getValue(),new TypeReference<ArrayList<TransferRateList>>(){}) ;
			if (Collections3.isNotEmpty(rateBizs)){
				for (int i=0;i<rateBizs.size();i++){
					if (days>=rateBizs.get(i).getStartDay()&&days<=rateBizs.get(i).getEndDay()){
						return new BigDecimal(rateBizs.get(i).getRate());
					}
				}
			}
		} catch (Exception e) {
			logger.error("获取阶梯费率异常:"+e.toString());
			return null;
		}
		return null;
	}

	@Override
	public Page<TransactionProjectBiz> queryPageTransactionProjectBiz(TransactionProjectBizQuery query) {
		List<TransactionProjectBiz> list = Lists.newArrayList();
		int count = transactionMapper.queryPageCountTransactionProjectBiz(query);
		list = transactionMapper.queryPageTransactionProjectBiz(query);
		if (Collections3.isNotEmpty(list)){
			for (TransactionProjectBiz biz:list) {
				biz.setTotalIncome(transactionMapper.queryTransactionTotalIncome(biz.getTransferId()));
			}
		}
		Page<TransactionProjectBiz> page = new Page<TransactionProjectBiz>();
		page.setData(list);
		// 每页总数
		page.setiDisplayLength(query.getPageSize());
		// 当前页
		page.setPageNo(query.getCurrentPage());
		// 总数
		page.setiTotalDisplayRecords(count);
		page.setiTotalRecords(count);
		return page;
	}

	@Override
	public BigDecimal queryTransactionTotalIncome(Long transactionid) {
		return transactionMapper.queryTransactionTotalIncome(transactionid);
	}

	@Override
	public int queryTransactionTransferCount(Long transactionid) {
		return transactionMapper.queryTransactionTransferCount(transactionid);
	}
	
	public TransactionForFirstInvestAct getFirstOneInvestAmount(Long activityId,Date startTime){
		
		String dateStr = DateUtils.getStrFromDate(startTime, DateUtils.DATE_FMT_3);
		String key=RedisConstant.ACTIVITY_DOUBLE_DAN_INVESTAMOUNT + RedisConstant.REDIS_SEPERATOR + activityId + RedisConstant.REDIS_SEPERATOR + dateStr;
		String json=RedisManager.get(key);
		TransactionForFirstInvestAct act=new TransactionForFirstInvestAct();
		act.setDayTime(startTime);
		if(StringUtils.isBlank(json)){
			return act;
		}
		List<ActivityFirstInvest> listFirst = JSON.parseArray(json,ActivityFirstInvest.class);
		if(Collections3.isEmpty(listFirst)){
			return null;
		}
		try {
			Member member=memberManager.selectByPrimaryKey(listFirst.get(0).getMemberId());
			if(member!=null){
				act.setAvatars(member.getAvatars());	
				act.setMobile(member.getMobile());
				act.setUsername(member.getUsername());
				act.setTotalInvest(listFirst.get(0).getTotalInvest());
			}
		} catch (ManagerException e) {
			logger.error("【 统计冠军】发生异常" , e);
		}
		return act;
		
	}
	/**
	 * 
	 * @desc 上榜记录
	 * @param activityId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author chaisen
	 * @time 2016年12月8日 下午4:59:32
	 *
	 */
	@Override
	public List<TransactionForFirstInvestAct> getCountEverydayFirstAmount(Long activityId,Date startTime, Date endTime){
		int totalDays=DateUtils.getIntervalDays(DateUtils.formatDate(startTime, DateUtils.DATE_FMT_3),
				DateUtils.formatDate(endTime, DateUtils.DATE_FMT_3))+1;
		List<ActivityFirstInvest> listTransactionInvestAll=Lists.newArrayList();
		List<TransactionForFirstInvestAct> listTransactionInvest=Lists.newArrayList();
		for(int i=0;i<totalDays;i++){
			Date day=DateUtils.addDate(startTime, i);
			if(DateUtils.getStrFromDate(day, DateUtils.DATE_FMT_3).equals(DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))){
				break;
			}
			String dateStr = DateUtils.getStrFromDate(day, DateUtils.DATE_FMT_3);
			String key=RedisConstant.ACTIVITY_DOUBLE_DAN_INVESTAMOUNT + RedisConstant.REDIS_SEPERATOR + activityId + RedisConstant.REDIS_SEPERATOR + dateStr;
			String json=RedisManager.get(key);
			if(StringUtils.isBlank(json)){
				continue;
			}
			List<ActivityFirstInvest> listFirst = JSON.parseArray(json,ActivityFirstInvest.class);
			listTransactionInvestAll.addAll(listFirst);
		}
			Map<Long, Integer> params = Maps.newHashMap();
	        for (ActivityFirstInvest temp : listTransactionInvestAll) {
	            Integer count = (Integer) params.get(temp.getMemberId());
	            params.put(temp.getMemberId(), (count == null) ? 1 : count + 1);
	        }
	        for (Entry<Long,Integer> entry : params.entrySet()) {
				 TransactionForFirstInvestAct act=new TransactionForFirstInvestAct();
				try {
					Member member = memberManager.selectByPrimaryKey(entry.getKey());
					if(member!=null){
						act.setAvatars(member.getAvatars());
						act.setMobile(member.getMobile());
						act.setUsername(member.getUsername());
						act.setNumber(entry.getValue());
					}
				} catch (ManagerException e) {
					logger.error("【 统计上榜记录】发生异常" , e);
				}

				 listTransactionInvest.add(act);
				}
	        Collections.sort(listTransactionInvest, new Comparator<TransactionForFirstInvestAct>(){
				@Override
				public int compare(TransactionForFirstInvestAct arg0, TransactionForFirstInvestAct arg1) {
	                if(arg0.getNumber() < arg1.getNumber()){
	                    return 1;
	                }
	                if(arg0.getNumber() == arg1.getNumber()){
	                    return 0;
	                }
	                return -1;
				}
	        });
		return listTransactionInvest;
	}

	@Override
	public List<TransactionForFirstInvestAct> getFirstEightInvestAmount(Long activityId,Date endTime){
		List<TransactionForFirstInvestAct> firstList=Lists.newArrayList();
		String dateStr = DateUtils.getStrFromDate(endTime, DateUtils.DATE_FMT_3);
		String key=RedisConstant.ACTIVITY_DOUBLE_DAN_INVESTAMOUNT + RedisConstant.REDIS_SEPERATOR + activityId + RedisConstant.REDIS_SEPERATOR + dateStr;
		String json=RedisManager.get(key);
		if(StringUtils.isBlank(json)){
			return null;
		}
		List<ActivityFirstInvest> listFirst = JSON.parseArray(json,ActivityFirstInvest.class);
		if(Collections3.isEmpty(listFirst)){
			return null;
		}
		try {
			for(ActivityFirstInvest invest:listFirst){
				TransactionForFirstInvestAct act=new TransactionForFirstInvestAct();
				act.setDayTime(endTime);
				if(invest.getMemberId()!=null){
					Member member=memberManager.selectByPrimaryKey(invest.getMemberId());
					if(member!=null){
						act.setAvatars(member.getAvatars());
						act.setMobile(member.getMobile());
						act.setUsername(member.getUsername());
						act.setTotalInvest(invest.getTotalInvest());
					}
				}
				firstList.add(act);
			}
			
		} catch (ManagerException e) {
			logger.error("【 统计前八名】发生异常" , e);
		}
		return firstList;
		
	}
	
	

	@Override
	public int queryMemberTransactionCount(Long memberid, Date starttime, Date endtime) {
		return transactionMapper.queryMemberTransactionCount(memberid,starttime,endtime);
	}
	
	 /**
     * 记录用户历史待还金额
     * @param memberRepay
     */
	public  void incrMemberRepayment(Long projectId){
//		try{
//			List<Transaction> tranList = transactionMapper.getTransactionByProjectId(projectId);
//			//计算项目用户代付金额
//			for(Transaction transaction:tranList){
//				MemberHistoryRepayment memberRepay =memberHistoryRepaymentMapper.selectByMemberID(transaction.getMemberId());
//				if(memberRepay == null){
//					memberRepay =new MemberHistoryRepayment();
//					memberRepay.setCreateTime(new Date());
//	 			}
//				BigDecimal hMaxBalance = memberRepay.gethMaxBalance()==null?BigDecimal.ZERO:memberRepay.gethMaxBalance();
//				//交易本息的未还金额
//				BigDecimal ti_noRepay_ammount =transactionInterestMapper.totalMemberTransferAmount(transaction.getMemberId(),StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus());
//				ti_noRepay_ammount = ti_noRepay_ammount ==null?BigDecimal.ZERO:ti_noRepay_ammount;
//				//交易表未生成交易本息的订单金额
//	//			BigDecimal or_noRepay_ammount = transactionMapper.getOrderNoRepayAmount(transaction.getMemberId());		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
//	//			or_noRepay_ammount = or_noRepay_ammount ==null?BigDecimal.ZERO:or_noRepay_ammount;
//				memberRepay.setMemberId(transaction.getMemberId());
//				//当前发生金额
//				BigDecimal investAmount = transaction.getInvestAmount();
//				if(investAmount == null){
//					investAmount = BigDecimal.ZERO;
//				}
//				BigDecimal newMaxBalace = investAmount.add(ti_noRepay_ammount.subtract(transaction.getInvestAmount()));
//				if(newMaxBalace.compareTo(hMaxBalance) > 0){
//					memberRepay.sethMaxBalance(newMaxBalace);
//					memberRepay.setTransactionId(transaction.getId());
//					memberRepay.setUpdateTime(new Date());
//					memberRepay.setcBalance(investAmount);
//					memberRepay.setcCountBalance(ti_noRepay_ammount.subtract(transaction.getInvestAmount()));
//				}
//				if(memberRepay.getId() != null){
//					memberHistoryRepaymentMapper.updateByPrimaryKey(memberRepay);
//				}else{
//					memberHistoryRepaymentMapper.insert(memberRepay);
//				}
//			}
//		}catch(Exception e){
//			logger.error("计算项目用户待还金额错误：projectId=" + projectId, e);
//		}
	}
	@Override
	public BigDecimal findTotalInvestAmount(Long memberId,Date transactionStartTime,Date transactionEndTime) throws ManagerException {
		try {
			return transactionMapper.findTotalInvestAmount(memberId,transactionStartTime,transactionEndTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	
}