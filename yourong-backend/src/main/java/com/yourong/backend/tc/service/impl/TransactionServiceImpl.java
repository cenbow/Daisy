package com.yourong.backend.tc.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.backend.tc.service.RepaymentService;
import com.yourong.backend.tc.service.TransactionService;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.BalanceAction;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.pay.sina.SinaPayConfig;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.ErrorCode;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.RefundStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.ResponseCodeEnum;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateSinglePayTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryRefundResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.LeaseBonusManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.repayment.manager.AfterHostingPayHandleManager;
import com.yourong.core.repayment.manager.RepaymentManager;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.manager.SysOperateInfoManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.ContractManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.HostingRefundManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TradeProcManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.HostingRefund;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.TransactionForOrder;
import com.yourong.core.tc.model.biz.TransactionInterestForPay;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

@Service
public class TransactionServiceImpl implements TransactionService {
	
	@Resource
	private TransactionInterestManager transactionInterestManager;
	
	@Resource
	private HostingCollectTradeManager hostingCollectTradeManager;
	
	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;
	
	@Resource
	private DebtManager debtManager;
	
	@Resource
	private SinaPayClient sinaPayClient;
	
	@Autowired
	private TransactionManager myTransactionManager;
	
	@Autowired
	private ContractManager contractManager;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired
	private BscAttachmentManager bscAttachmentManager;
	
	@Autowired
	private AttachmentIndexManager attachmentIndexManager;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private OrderManager orderManager;
	
	@Autowired
	private CouponManager couponManager;
	
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	
	@Autowired
	private LeaseBonusManager leaseBonusManager;

	@Autowired
	private ActivityAfterTransactionManager activityAfterTransactionManager;

	@Autowired
	private HostingRefundManager hostingRefundManager;
	
	@Autowired
	private SysDictManager sysDictManager;

	@Autowired
	private RepaymentManager   repaymentManager;
	
	@Autowired
	private MemberManager memberManager;
	@Autowired
	private SysOperateInfoManager   sysOperateInfoManager;
	
	@Autowired
	private  AfterHostingPayHandleManager afterHostingPayHandleManager;
	
	
	@Autowired
	private RepaymentService repaymentService;
	
	@Autowired
	private TradeProcManager tradeProcManager;


	private static Logger logger = LoggerFactory.getLogger(TransactionService.class);

	/**
	 * 代收原始债权人和平台本息金额
	 */
	@Override
	public synchronized void createHostingCollectTradeForPayInterestAndPrincipal() {
		try {
			List<TransactionInterestForPay> transactionInterestForPays = transactionInterestManager.selectToBePaidTransactionInterests();
			//计算每个项目 债权人要还的本息 和 平台垫付的利息
			List<TransactionInterestForPay> transactionInterestForPayLists = buildTransactionInterestForPayLists(transactionInterestForPays);
			if (Collections3.isEmpty(transactionInterestForPayLists)) {
				return;
			}
			for (TransactionInterestForPay transactionInterestForPay : transactionInterestForPayLists) {
					//插入代收表， 债权人应付的本息
					ResultDto<CreateCollectTradeResult> result = insertCreditorHostingCollectTrade(transactionInterestForPay);
					if (result != null && !result.isSuccess()) {
						collectTradeError(result);
					}
					//插入代收表， 平台应付的利息
				     result= insertPlatformHostingCollectTrade(transactionInterestForPay);
					if (result != null && !result.isSuccess()) {
						collectTradeError(result);
					}

//					String tradeNo = result.getModule().getTradeNo();
//					String error = result.getErrorMsg();
//					if (result != null && result.getModule().getPayStatus().equals(PayStatus.FAILED.name())) {
////						//将本息记录重置为待支付
////						transactionInterestManager.updateStatusForPayInterest(
////								StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus(),
////								StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus(),
////								tradeNo
////						);
//						String remak = tradeNo + ":交易失败,失败原因:" + error;
//						//将代收记录置为失败
//						hostingCollectTradeManager.updateTradeStatus(
//								hostingCollectTradeManager.getByTradeNo(tradeNo),
//								SinaPayEnum.TRANSACTION_TRADE_FAILED.getCode(),
//								remak
//						);
//					}
				}
		} catch (Exception e) {
			logger.error("代收原始债权人和平台本息金额发生异常", e);
		}
	}

	/**
	 * 代收失败
	 * @param result
	 * @throws ManagerException
	 */
	private void collectTradeError(ResultDto<CreateCollectTradeResult> result) throws ManagerException {
		String tradeNo = result.getModule().getTradeNo();
		String error = result.getErrorMsg();
		if (result.getModule().getPayStatus().equals(PayStatus.FAILED.name())){
            String remak = tradeNo + ":交易失败,失败原因:" + error;
            //将代收记录置为失败
            hostingCollectTradeManager.updateTradeStatus(
                    hostingCollectTradeManager.getByTradeNo(tradeNo),
                    SinaPayEnum.TRANSACTION_TRADE_FAILED.getCode(),
                    remak
            );
        }
	}

	/**
	 *	计算每个项目 债权人要还的本息 和 平台垫付的利息
 	 * @param transactionInterestForPays
	 * @return
	 */
	private List<TransactionInterestForPay> buildTransactionInterestForPayLists(
			List<TransactionInterestForPay> transactionInterestForPays) {
		if(Collections3.isNotEmpty(transactionInterestForPays)) {
			Map<Long,List<TransactionInterestForPay>> TransactionInterestForPayMaps = Maps.newHashMap();
			//按照项目来分组
			for (TransactionInterestForPay transactionInterestForPay : transactionInterestForPays) {
				List<TransactionInterestForPay> transactionInterestForPayLists  = Lists.newArrayList();
				transactionInterestForPayLists.add(transactionInterestForPay);
				if(TransactionInterestForPayMaps.containsKey(transactionInterestForPay.getProjectId())) {
					TransactionInterestForPayMaps.get(transactionInterestForPay.getProjectId()).add(transactionInterestForPay);
				} else {
					TransactionInterestForPayMaps.put(transactionInterestForPay.getProjectId(), transactionInterestForPayLists);
				}
			}
			List<TransactionInterestForPay> transactionInterestForPayLists  = Lists.newArrayList();
			
			if(TransactionInterestForPayMaps!=null && TransactionInterestForPayMaps.size()>0) {
				for (Long projectId : TransactionInterestForPayMaps.keySet()) {
					List<TransactionInterestForPay> transactionInterestForPayList = TransactionInterestForPayMaps.get(projectId);
					if(Collections3.isNotEmpty(transactionInterestForPayList)) {
						//原始债权人还款总额
						 BigDecimal totalPayInterestAndPrincipalForLender = BigDecimal.ZERO;
						 //平台垫付收益券总额
						 BigDecimal totalPayInterestAndPrincipalForPlatform = BigDecimal.ZERO;
						 StringBuffer transactionInterestIds = new StringBuffer();
						for (TransactionInterestForPay transactionInterestForPay : transactionInterestForPayList) {
							// 应付利息 只是针对投资者，  原始债权人还款总额 应该要减去使用收益劵产生的利息
//							if(transactionInterestForPay.getExtraInterest()!=null) {
//								totalPayInterestAndPrincipalForPlatform = totalPayInterestAndPrincipalForPlatform.add(transactionInterestForPay.getExtraInterest());
//								if(transactionInterestForPay.getPayableInterest()!=null) {
//									totalPayInterestAndPrincipalForLender = totalPayInterestAndPrincipalForLender.add(transactionInterestForPay.getPayableInterest().subtract(transactionInterestForPay.getExtraInterest()));
//								}
//							} else {
//								if(transactionInterestForPay.getPayableInterest()!=null) {
//									totalPayInterestAndPrincipalForLender = totalPayInterestAndPrincipalForLender.add(transactionInterestForPay.getPayableInterest());
//								}
//							}
//							if(transactionInterestForPay.getPayablePrincipal()!=null) {
//								totalPayInterestAndPrincipalForLender = totalPayInterestAndPrincipalForLender.add(transactionInterestForPay.getPayablePrincipal());
//							}
							//平台垫付收益券总额
							totalPayInterestAndPrincipalForPlatform = totalPayInterestAndPrincipalForPlatform.add(transactionInterestForPay.getExtraInterest());
							//原始债权人要还利息    应该要减去使用收益劵产生的利息（收益劵是平台代付）
							BigDecimal bigDecimal = transactionInterestForPay.getPayableInterest().subtract(transactionInterestForPay.getExtraInterest());
							totalPayInterestAndPrincipalForLender = totalPayInterestAndPrincipalForLender.add(bigDecimal);
							//原始债权人要还本金
							totalPayInterestAndPrincipalForLender = totalPayInterestAndPrincipalForLender.add(transactionInterestForPay.getPayablePrincipal());

							transactionInterestIds.append(transactionInterestForPay.getTransactionInterestId().toString());
							transactionInterestIds.append(StringUtil.CARET);
						}
						TransactionInterestForPay interestForPay = new TransactionInterestForPay();
						interestForPay.setProjectId(projectId);
						interestForPay.setPayerId(transactionInterestForPayList.get(0).getPayerId());
						interestForPay.setTotalPayInterestAndPrincipalForLender(totalPayInterestAndPrincipalForLender.setScale(2, BigDecimal.ROUND_HALF_UP));
						interestForPay.setTotalPayInterestAndPrincipalForPlatform(totalPayInterestAndPrincipalForPlatform.setScale(2, BigDecimal.ROUND_HALF_UP));
						interestForPay.setTransactionInterestIds(transactionInterestIds.substring(0, transactionInterestIds.lastIndexOf(StringUtil.CARET)));
						transactionInterestForPayLists.add(interestForPay);
					}
				}
			}
			return transactionInterestForPayLists;
		}
		return null;
	}

	/**
	 * 插入原始债权人还款
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public synchronized ResultDto<CreateCollectTradeResult> insertCreditorHostingCollectTrade(TransactionInterestForPay transactionInterestForPay) throws Exception {
		BigDecimal totalPayInterestAndPrincipalForLender = transactionInterestForPay.getTotalPayInterestAndPrincipalForLender();
		Long projectId = transactionInterestForPay.getProjectId();
		Long payerId = transactionInterestForPay.getPayerId();
		BigDecimal totalPayInterestAndPrincipalForPlatform = transactionInterestForPay.getTotalPayInterestAndPrincipalForPlatform();
		ResultDto<CreateCollectTradeResult> result = new ResultDto<CreateCollectTradeResult>();
		try {
			//如果原始债权人总还款额大于0，则创建原始债权人还款代收交易
//			if(totalPayInterestAndPrincipalForLender!=null &&totalPayInterestAndPrincipalForLender.doubleValue()>0) {
				// 判断是否已经代收过原始债权人的还款
				if(hostingCollectTradeManager.haveHostingCollectTradeForLender(projectId, transactionInterestForPay.getTransactionInterestIds())) {
					logger.info("项目：projectId=" + projectId + "还本付息已经创建过原始债权人代收了");
					result.setSuccess(false);
					return result;
				}
				String ip = null;
				SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
				if (dict != null) {
					ip = dict.getValue();
				}
				HostingCollectTrade collectTrade = new HostingCollectTrade();
				buildBaseCollectTrade(collectTrade, projectId);
				collectTrade.setAmount(totalPayInterestAndPrincipalForLender);
				collectTrade.setPayerId(payerId);
				collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
				collectTrade.setType(TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType());
				collectTrade.setTransactionInterestIds(transactionInterestForPay.getTransactionInterestIds());
				collectTrade.setPlatformAmount(totalPayInterestAndPrincipalForPlatform);
				collectTrade.setRemarks(RemarksEnum.HOSTING_COLLECT_TRADE_FOR_LENDER.getRemarks());
				collectTrade.setPayerIp(ip);
				if(hostingCollectTradeManager.insertSelective(collectTrade)>0) { 
					//更新该项目下所有本息记录为支付中状态
//					transactionInterestManager.updateStatusToPayingForPayInterest(
//							StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus(),
//							StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus(),
//							projectId,
//							collectTrade.getTradeNo()
//							);
					return sinaPayClient.createHostingCollectTrade(
						collectTrade.getTradeNo(), 
						TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getDesc(), 
						SerialNumberUtil.generateIdentityId(payerId), 
						ip, 
						AccountType.SAVING_POT, 
						collectTrade.getAmount(), 
						IdType.UID, 
						TradeCode.COLLECT_FROM_BORROWER,
						TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode()
						);
				}
		//	}
			return null;
		} catch (Exception e) {
			logger.error("插入原始债权人还款和平台垫付代收交易出错：projectId=" + projectId + ", payerId=" + payerId, e);
			throw e;
		}
	}
	/**
	 * 插入平台原始应付的利息
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public synchronized ResultDto<CreateCollectTradeResult> insertPlatformHostingCollectTrade(TransactionInterestForPay transactionInterestForPay) throws Exception {
		ResultDto<CreateCollectTradeResult> result = new ResultDto<CreateCollectTradeResult>();
		BigDecimal totalPayInterestAndPrincipalForPlatform = transactionInterestForPay.getTotalPayInterestAndPrincipalForPlatform();
		if(totalPayInterestAndPrincipalForPlatform==null || BigDecimal.ZERO.compareTo(totalPayInterestAndPrincipalForPlatform)==0){
			logger.info("项目：projectId={}  不用平台收益券代收",transactionInterestForPay.getProjectId());
			result.setSuccess(true);
			return result;
		}
		if(hostingCollectTradeManager.haveHostingCollectTradeForPaltform(transactionInterestForPay.getProjectId(), transactionInterestForPay.getTransactionInterestIds())) {
			logger.info("项目：projectId={}  已经创建了平台收益券代收",transactionInterestForPay.getProjectId());
			result.setSuccess(false);
			return result;
		}
		String ip = null;
		SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
		if (dict != null) {
			ip = dict.getValue();
		}
		HostingCollectTrade collectTrade = new HostingCollectTrade();
		collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
		collectTrade.setSourceId(transactionInterestForPay.getProjectId());
		collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
		collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
		collectTrade.setTradeTime(DateUtils.getCurrentDate());
		collectTrade.setPayStatus(PayStatus.PROCESSING.name());
		collectTrade.setAmount(totalPayInterestAndPrincipalForPlatform);
		collectTrade.setPayerId(Long.parseLong(Config.internalMemberId));
		collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
		collectTrade.setType(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType());
		collectTrade.setRemarks(transactionInterestForPay.getProjectId() + "平台代付收益券");
		collectTrade.setTransactionInterestIds(transactionInterestForPay.getTransactionInterestIds());
		collectTrade.setPayerIp(ip);
		if(hostingCollectTradeManager.insertSelective(collectTrade)>0) {
				return	sinaPayClient.createHostingCollectTrade(
						collectTrade.getTradeNo(),
						TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getDesc(),
						SerialNumberUtil.getBasicAccount(),
						ip,
						AccountType.BASIC,
						collectTrade.getAmount(),
						IdType.EMAIL,
						TradeCode.COLLECT_FROM_PLATFORM_PROFITCOUPON,
						TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode()
				);
		}
		return  null;
	}
	private void buildBaseCollectTrade(HostingCollectTrade collectTrade, Long projectId) {
		collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
		collectTrade.setSourceId(projectId);
		collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
		collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
		collectTrade.setTradeTime(DateUtils.getCurrentDate());
		collectTrade.setPayStatus(PayStatus.PROCESSING.name());
	}

	@Override
	public synchronized void SynchronizedHostingCollectTrade() {
		// 查询需要同步的代收交易
		try {
			List<HostingCollectTrade> hostingCollectTrades = hostingCollectTradeManager.selectSynchronizedHostingCollectTrades();
			if(Collections3.isNotEmpty(hostingCollectTrades)) {
				for (HostingCollectTrade hostingCollectTrade : hostingCollectTrades) {
					ResultDto<QueryTradeResult> result = null;
					if(TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType()==hostingCollectTrade.getType()
							||TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType()==hostingCollectTrade.getType()
							||TypeEnum.HOSTING_COLLECT_DIRECT_PAY.getType()==hostingCollectTrade.getType()) {
						result = sinaPayClient.queryTrade(
								SerialNumberUtil.generateIdentityId(hostingCollectTrade.getPayerId()), 
								IdType.UID, 
								hostingCollectTrade.getTradeNo(), 
								1, 
								20, 
								null, 
								null
								);
					}
					if(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_A.getType()==hostingCollectTrade.getType()
							||TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()==hostingCollectTrade.getType()
							||TypeEnum.HOSTING_COLLECT_TRADE_LEASE_BONUS.getType()==hostingCollectTrade.getType()) {
						result = sinaPayClient.queryTrade(
								SerialNumberUtil.getBasicAccount(), 
								IdType.EMAIL, 
								hostingCollectTrade.getTradeNo(), 
								1, 
								20, 
								null, 
								null
								);
					}
					if(result!=null) {
						QueryTradeResult queryTradeResult = result.getModule();
						if(queryTradeResult!=null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
							TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
							logger.info("同步代收交易号："+tradeItem.getTradeNo()+"状态为："+tradeItem.getProcessStatus());
							
							if(TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
									||TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())
									||TradeStatus.TRADE_CLOSED.name().equals(tradeItem.getProcessStatus())) {
								//基本户垫付租赁分红代收同步处理
								if(TypeEnum.HOSTING_COLLECT_TRADE_LEASE_BONUS.getType()==hostingCollectTrade.getType()) {
									leaseBonusManager.afterLeaseBonusCollectNotifyProcess(tradeItem.getTradeNo(), tradeItem.getProcessStatus());
								}
								
								//TODO 用户投资代收同步处理
								if(TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType()==hostingCollectTrade.getType()) {
									
								}
								
								//TODO 借款人还款代收同步处理
								if(TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType()==hostingCollectTrade.getType()) {
									
								}
								
								//TODO 代收平台收益券同步处理
								if(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()==hostingCollectTrade.getType()) {
									
								}
								
								//借款人还款代收同步处理||代收平台收益券同步处理
								if(TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType()==hostingCollectTrade.getType()
										||TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()==hostingCollectTrade.getType()) {
									repaymentService.afterHostingCollectTradeForRepayment(tradeItem.getTradeNo(), null, tradeItem.getProcessStatus());
								}
								
								//基本户现金券投资垫付代收同步处理
								if(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_A.getType()==hostingCollectTrade.getType()) {
									myTransactionManager.afterPaltformCouponCollectNotify(tradeItem.getTradeNo(), null, tradeItem.getProcessStatus());
								}
									
								
								//直接代收成功同步处理
								if(TypeEnum.HOSTING_COLLECT_DIRECT_PAY.getType()==hostingCollectTrade.getType()) {
									hostingCollectTradeManager.updateTradeStatus(hostingCollectTrade, tradeItem.getProcessStatus(), tradeItem.getRemark());
									if (TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())) {

										TypeEnum typeEnum = null;
										if (hostingCollectTrade.getPayerId().equals( Long.parseLong(Config.internalMemberId))) {
											typeEnum = TypeEnum.BALANCE_TYPE_BASIC;
										} else {
											typeEnum = TypeEnum.BALANCE_TYPE_PIGGY;
										}
										// 写流水
										Balance balance = balanceManager.reduceBalance(typeEnum,
												hostingCollectTrade.getAmount(), hostingCollectTrade.getPayerId(),
												BalanceAction.balance_subtract_Available_subtract);
										// 记录用户投资资金流水
										capitalInOutLogManager.insert(hostingCollectTrade.getPayerId(),
												TypeEnum.FINCAPITALINOUT_TYPE_DIRECT_COLLECT, null,
												hostingCollectTrade.getAmount(), balance.getAvailableBalance(),
												hostingCollectTrade.getId().toString(), hostingCollectTrade.getSummary(), typeEnum);
									}
								}
							}
							
							
						}
					} else {
						logger.info("代收交易："+hostingCollectTrade.getTradeNo()+"在新浪不存在");
					}
				}
			}
		} catch (Exception e) {
			logger.error("同步代收交易发生异常", e);
		}
		
	}
	
	@Override
	public boolean generateContract(Long transactionId) {
		try {
			//生成合同
			if(transactionId!=null) {
				taskExecutor.execute(new GenerateContractThread(transactionId));
			}
		} catch (Exception e) {
			logger.error("生成合同发生异常，transactionId="+transactionId, e);
		}

		return true;
	}
	
	/**
	 * 生成合同线程
	 * @author Administrator
	 *
	 */
	private class GenerateContractThread implements Runnable {
		private Long transactionId;
		public GenerateContractThread(final Long transactionId) {
			this.transactionId = transactionId;
		}
		
		@Override
		@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
		public void run() {
			logger.debug("生成合同线程开始执行，transactionId：" +transactionId);
			try {
				BscAttachment attachment = contractManager.saveContract(transactionId, "backend");
				attachment.setListOrder(0);
				attachment.setUploadTime(DateUtils.getCurrentDate());
				attachment.setModule(Constant.ATTACHMENT_CONTRACT_IDENTITY);
				attachment.setDelFlag(Constant.ENABLE);
				attachment.setStorageWay(TypeEnum.ATTACHMENT_STORAGE_WAY_OSS.getCode());
				int rows = bscAttachmentManager.insertSelective(attachment);
				if(rows>0) {
					//如果有索引，则先把他删除
					attachmentIndexManager.deleteAttachmentIndexByKeyId(transactionId.toString());
					attachmentIndexManager.insertAttachmentIndex(attachment.getId(), transactionId.toString());
				}
			} catch (Exception e) {
				logger.error("生成合同发生异常，transactionId：" +transactionId, e);
			}
		}
	}

	@Override
	public void SynchronizedHostingPayTrade() {
		// 查询需要同步的代付交易
		try {
			List<HostingPayTrade> hostingPayTrades = hostingPayTradeManager.selectSynchronizedHostingPayTrades();
			if (Collections3.isEmpty(hostingPayTrades)) {
				logger.info("[定时任务同步代付]没有同步代付的交易数据");
				return;
			}
			for (HostingPayTrade hostingPayTrade : hostingPayTrades) {
				ResultDto<QueryTradeResult> result = sinaPayClient.queryTrade(
						SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()),
						IdType.UID,
						hostingPayTrade.getTradeNo(),
						1,
						20,
						null,
						null
				);
				if (result == null) {
					break;
				}
				QueryTradeResult queryTradeResult = result.getModule();
				if (queryTradeResult != null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
					TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
					logger.info("[定时任务同步代付]代付交易号：" + tradeItem.getTradeNo() + "状态为：" + tradeItem.getProcessStatus());
					// 交易成功或者失败状态处理业务或交易关闭状态处理业务
					if (TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
							||TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())
							||TradeStatus.TRADE_CLOSED.name().equals(tradeItem.getProcessStatus())) {
						//放款业务后续处理流程
						if (TypeEnum.HOSTING_PAY_TRADE_LOAN.getType() == hostingPayTrade.getType()) {
							myTransactionManager.afterHostingPayTradeLoan(tradeItem.getProcessStatus(), tradeItem.getTradeNo());
						}
						
						//还本付息业务后续处理流程
						if(TypeEnum.HOSTING_PAY_TRADE_RETURN.getType()==hostingPayTrade.getType()) {
				            ResultDO<TransactionInterest> resultData = afterHostingPayHandleManager.afterPayInterestAndPrincipal(tradeItem.getProcessStatus(), tradeItem.getTradeNo(),null);
				            if(resultData.isSuccess()&&resultData.getResult()!=null) {
				            	if(resultData.getResult()!=null){
				            		afterHostingPayHandleManager.afterHostingPayTradeSucess(resultData.getResult());
				            	}
				            }
				        }
					}
				} else {
					logger.info("[定时任务同步代付]代付交易号：" + hostingPayTrade.getTradeNo() + "在新浪不存在");
				}
			}
		} catch (Exception e) {
			logger.error("[定时任务同步代付]同步代付交易发生异常", e);
		}

	}
	
	@Override
	public void SynchronizedBatchHostingPayTrade() {
		// 查询需要同步的批次
		try {
			List<String> batchNos = hostingPayTradeManager.selectSynchronizedBatchHostingPayTrades();
			if (Collections3.isEmpty(batchNos)) {
				logger.info("[定时任务同步代付]没有同步批次代付的交易数据");
				return;
			}
			for (String batchNo : batchNos) {
				ResultDto<QueryTradeResult> result = sinaPayClient.queryHostingBatchTrade(batchNo);
				if(result!=null) {
					QueryTradeResult queryTradeResult = result.getModule();
					if (queryTradeResult != null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
						List<TradeItem> payItemList = queryTradeResult.getPayItemList();
						// 交易成功或者失败状态处理业务或交易关闭状态处理业务
							//还本付息业务后续处理流程
							if(Collections3.isNotEmpty(payItemList)) {
								for (TradeItem tradeItem : payItemList) {
									if(TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
											||TradeStatus.TRADE_CLOSED.name().equals(tradeItem.getProcessStatus())
											||TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())) {
										 ResultDO<TransactionInterest> resultDO = afterHostingPayHandleManager.afterPayInterestAndPrincipal(tradeItem.getProcessStatus(), tradeItem.getTradeNo(),null);
								            if(resultDO.isSuccess()&&resultDO.getResult()!=null) {
								            	if(resultDO.getResult()!=null){
								            		afterHostingPayHandleManager.afterHostingPayTradeSucess(resultDO.getResult());
								            	}
								            }
									}
								}
							}
						}
					} else {
						logger.info("[定时任务同步代付]批次代付交易号：" + batchNo + "在新浪不存在");
					}
				}
		} catch (Exception e) {
			logger.error("[定时任务同步代付]同步批次代付交易发生异常", e);
		}

	}

	/**
	 * 业务逻辑：
	 * 
	 * 补充：直投项目校验项目放款状态
	 * 1、通过项目id查询出该项目未放款的投资记录
	 * 2、判断平台余额是否足够垫付现金券
	 * 3、代收平台垫付现金券，锁定交易状态为放款中
	 * 4、平台垫付现金券代收成功后执行代付
	 * 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public synchronized ResultDO<?> beforeLoanToOriginalCreditor(Long projectId,Long loginId) throws Exception{
		ResultDO<?> result = new ResultDO<>();
		try {
			//直投项目需要判断项目状态是否为51，待放款状态
			checkProjectStatusForLoan(projectId, result);
			if(result.isError()){
				return result;
			}
			//通过项目id查询出该项目未放款的投资记录
			TransactionQuery transactionQuery = new TransactionQuery();
			transactionQuery.setProjectId(projectId);
			transactionQuery.setLoanStatus(StatusEnum.TRANSACTION_LOAN_STATUS_WAIT_LOAN.getStatus());
			List<Transaction> transactions = myTransactionManager.selectTransactionsByQueryParams(transactionQuery);
			if(Collections3.isNotEmpty(transactions)) {
				BigDecimal totalCouponAmount = BigDecimal.ZERO;
				BigDecimal totalUsedCapitalAmount = BigDecimal.ZERO;
				for (Transaction transaction : transactions) {
					if(transaction.getUsedCouponAmount()!=null) {
						totalCouponAmount = totalCouponAmount.add(transaction.getUsedCouponAmount());
					}
					if(transaction.getUsedCapital()!=null) {
						totalUsedCapitalAmount = totalUsedCapitalAmount.add(transaction.getUsedCapital());
					}
				}
				
				if(totalCouponAmount!=null && totalCouponAmount.doubleValue()>0) {
					//判断平台余额是否足够
					Balance paltformBalance = balanceManager.queryBalance(Long.parseLong(Config.internalMemberId), TypeEnum.BALANCE_TYPE_BASIC);
					if(totalCouponAmount.doubleValue()>paltformBalance.getBalance().doubleValue()) {
						result.setResultCode(ResultCode.BALANCE_PALTFORM_BALANCE_LACKING);
						return result;
					}
					String ip = null;
					SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
					if (dict != null) {
						ip = dict.getValue();
					}
					//创建平台优惠券垫付代收交易
					HostingCollectTrade couponHostingCollectTrade = insertHostingCollectTradeForPaltformCoupon(totalCouponAmount,
							projectId, ip);
					//代付后锁定交易放款状态，同时更新tradeNo到交易表
					updateTransactionByLoan(couponHostingCollectTrade, transactions,projectId);
					if(couponHostingCollectTrade!=null) {
						try {
							//调用第三方接口完成托管代收交易
							ResultDto<CreateCollectTradeResult> collectTradeResult = sinaPayClient.createHostingCollectTrade(
									couponHostingCollectTrade.getTradeNo(), 
									TypeEnum.HOSTING_COLLECT_TRADE_COUPON_A.getDesc(), // 后期修改
									SinaPayConfig.indentityEmail, 
									ip,
									AccountType.BASIC,
									couponHostingCollectTrade.getAmount(),
									IdType.EMAIL,
									TradeCode.COLLECT_FROM_PLATFORM_CASHCOUPON,
									TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode()
									);
						} catch (Exception e) {
							logger.error("【放款代收】远程调用接口失败",e);
						}
				}
				} else {
					//直接调用放款接口
					myTransactionManager.createHostPayForLoan(projectId,totalUsedCapitalAmount,transactions,null);
				}
				//记录放款操作人
				sysOperateInfoManager.saveOperateInfo(projectId, TypeEnum.OPERATE_TYPE_PROJECT.getType(), loginId, TypeEnum.OPERATE_DIRECT_LOAN.getDesc(), TypeEnum.OPERATE_DIRECT_LOAN.getCode(),"");
				
			} else {
				result.setResultCode(ResultCode.TRANSACTION_LOAN_NO_INVEST_ERROR);
				return result;
			}
			
		} catch (Exception e) {
			logger.error("【放款给原始债权人前的业务（代收平台优惠券垫付金额）】发生异常，projectId：" +projectId, e);
			result.setSuccess(false);
			result.setResultCode(ResultCode.SINA_PAY_ERROR);
			throw e;
		}
		return result;
	}
	
	private void updateTransactionByLoan(
			HostingCollectTrade couponHostingCollectTrade,
			List<Transaction> transactions,Long projectId) throws ManagerException {
		Project project = projectManager.selectByPrimaryKey(projectId);
		int investType = project.getInvestType();
		if(ProjectEnum.PROJECT_TYPE_DIRECT.getType() == investType){//直投项目不设置代收交易号
			for (Transaction transaction : transactions) {
				transaction.setLoanStatus(StatusEnum.TRANSACTION_LOAN_STATUS_LOANING.getStatus());
				myTransactionManager.updateByPrimaryKeySelective(transaction);
			}
		}else{
			for (Transaction transaction : transactions) {
				transaction.setLoanStatus(StatusEnum.TRANSACTION_LOAN_STATUS_LOANING.getStatus());
				transaction.setCollectTradeNo(couponHostingCollectTrade.getTradeNo());
				myTransactionManager.updateByPrimaryKeySelective(transaction);
			}
		}
	}

	/**
	 * 插入平台优惠券垫付代收交易
	 * @param totalCouponAmount
	 * @param projectId
	 * @throws ManagerException 
	 */
	private HostingCollectTrade insertHostingCollectTradeForPaltformCoupon(BigDecimal totalCouponAmount, Long projectId, String payerIp)
			throws ManagerException {
		try {
			//本地保存HostingCollectTrade信息
			HostingCollectTrade collectTrade = new HostingCollectTrade();
			collectTrade.setAmount(totalCouponAmount);
			collectTrade.setPayerId(Long.parseLong(Config.internalMemberId));
			collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
			collectTrade.setRemarks(RemarksEnum.PALTFORM_COUPON_FOR_LOAN.getRemarks()+":projectId="+projectId);
			collectTrade.setSourceId(projectId);
			collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
			collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
			collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
			collectTrade.setPayStatus((SinaPayEnum.PAY_PROCESSING.getCode()));
			collectTrade.setTradeTime(DateUtils.getCurrentDate());
			collectTrade.setType(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_A.getType());
			collectTrade.setPayerIp(payerIp);
			if(hostingCollectTradeManager.insertSelective(collectTrade)>0) {
				return collectTrade;
			}
			
		} catch (ManagerException e) {
			logger.error("[插入平台优惠券垫付代收交易信息出错]：projectId=" + projectId, e);
			throw e;
		}
		return null;
	}

	@Override
	public ResultDO<?> directHostingPayTrade(Long memberId, BigDecimal amount, int type,String remark)
			throws Exception {
		ResultDO<?> result = new ResultDO();
		//判断member是否存在
		Member member = memberManager.selectByPrimaryKey(memberId);
		if(member==null&&memberId.longValue()!=Long.parseLong(Config.internalMemberId)){
			result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return result;
		}
		String ip = null;
		SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
		if (dict != null) {
			ip = dict.getValue();
		}
		HostingPayTrade hostingPayTrade = new HostingPayTrade();
		hostingPayTrade.setAmount(amount);
		hostingPayTrade.setPayeeId(memberId);
		hostingPayTrade.setRemarks(remark);
		hostingPayTrade.setSourceId(-1L);
		hostingPayTrade.setTradeNo(SerialNumberUtil.generatePayTradeaNo(memberId));
		hostingPayTrade.setTradeStatus(TradeStatus.WAIT_PAY.name());
		hostingPayTrade.setType(TypeEnum.HOSTING_PAY_DIRECT_PAY.getType());
		hostingPayTrade.setUserIp(ip);
		if(hostingPayTradeManager.insertSelective(hostingPayTrade)>0) {
			TradeCode tradeCode = null;
			//type=1投资专用付款，type=2还款专用付款
			if(type==1) {
				tradeCode = TradeCode.PAY_FOR_DIRECT_FROM_INVEST_ACCOUNT;
			}
			if(type==2) {
				tradeCode = TradeCode.PAY_FOR_DIRECT_FROM_REPAYMENT_ACCOUNT;
			}
			if(memberId.longValue()==Long.parseLong(Config.internalMemberId)) {
				ResultDto<CreateSinglePayTradeResult> payResult = sinaPayClient.createSinglePayTrade(
						AccountType.BASIC, 
						amount, 
						hostingPayTrade.getTradeNo(), 
						IdType.EMAIL, 
						SinaPayConfig.indentityEmail, 
						hostingPayTrade.getRemarks(), 
						ip,
						tradeCode
						);
			} else {
				ResultDto<CreateSinglePayTradeResult> payResult = sinaPayClient.createSinglePayTrade(
						AccountType.SAVING_POT, 
						amount, 
						hostingPayTrade.getTradeNo(), 
						IdType.UID, 
						SerialNumberUtil.generateIdentityId(memberId), 
						hostingPayTrade.getRemarks(), 
						ip,
						tradeCode
						);
			}
			
//			//TODO 后期需要对同步返回的状态进行一次update，同时异步也需要去处理
//			if(payResult!=null && !payResult.isSuccess()) {
//				throw new ManagerException(payResult.getErrorMsg());
//			}
		}
		return result;
	}
	
	
	@Override
	public ResultDO<?> directHostingCollectTrade(Long memberId, BigDecimal amount, int type, String remark)
			throws Exception {
		ResultDO<?> result = new ResultDO();
		String ip = null;
		SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
		if (dict != null) {
			ip = dict.getValue();
		}
		if(memberId.longValue()==Long.parseLong(Config.internalMemberId)) {
			//本地保存HostingCollectTrade信息
			HostingCollectTrade collectTrade = new HostingCollectTrade();
			collectTrade.setAmount(amount);
			collectTrade.setPayerId(Long.parseLong(Config.internalMemberId));
			collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
			collectTrade.setSummary(remark);
			collectTrade.setSourceId(Long.parseLong(Config.internalMemberId));
			collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
			collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
			collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
			collectTrade.setPayStatus((SinaPayEnum.PAY_PROCESSING.getCode()));
			collectTrade.setTradeTime(DateUtils.getCurrentDate());
			collectTrade.setType(TypeEnum.HOSTING_COLLECT_DIRECT_PAY.getType());
			collectTrade.setPayerIp(ip);
			if(hostingCollectTradeManager.insertSelective(collectTrade)>0) {
				//代收到投资专用账户
				TradeCode tradeCode = null;
				if(type==1) {
					tradeCode = TradeCode.COLLECT_FROM_INVESTOR;
				}
				//代收到还款专用账户
				if(type==2) {
					tradeCode = TradeCode.COLLECT_FROM_BORROWER;
				}
				//调用第三方接口完成托管代收交易
				ResultDto<CreateCollectTradeResult> collectTradeResult = sinaPayClient.createHostingCollectTrade(
						collectTrade.getTradeNo(), 
						TypeEnum.HOSTING_COLLECT_DIRECT_PAY.getDesc(), // 后期修改
						SinaPayConfig.indentityEmail, 
						ip,
						AccountType.BASIC,
						collectTrade.getAmount(),
						IdType.EMAIL,
						tradeCode,
						TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode()
						);
				// 后期需要对同步返回的状态进行一次update，同时异步也需要去处理
				if(collectTradeResult!=null && !collectTradeResult.isSuccess()) {
					throw new ManagerException(collectTradeResult.getErrorMsg());
				}
			}
		} else {
			//本地保存HostingCollectTrade信息
			HostingCollectTrade collectTrade = new HostingCollectTrade();
			collectTrade.setAmount(amount);
			collectTrade.setPayerId(memberId);
			collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
			collectTrade.setSummary(remark);
			collectTrade.setSourceId(memberId);
			collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
			collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
			collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
			collectTrade.setPayStatus((SinaPayEnum.PAY_PROCESSING.getCode()));
			collectTrade.setTradeTime(DateUtils.getCurrentDate());
			collectTrade.setType(TypeEnum.HOSTING_COLLECT_DIRECT_PAY.getType());
			collectTrade.setPayerIp(ip);
			if(hostingCollectTradeManager.insertSelective(collectTrade)>0) {
				//代收到投资专用账户
				TradeCode tradeCode = null;
				if(type==1) {
					tradeCode = TradeCode.COLLECT_FROM_INVESTOR;
				}
				//代收到还款专用账户
				if(type==2) {
					tradeCode = TradeCode.COLLECT_FROM_BORROWER;
				}
				//调用第三方接口完成托管代收交易
				ResultDto<CreateCollectTradeResult> collectTradeResult = sinaPayClient.createHostingCollectTrade(
						collectTrade.getTradeNo(), 
						TypeEnum.HOSTING_COLLECT_DIRECT_PAY.getDesc(), // 后期修改
						SerialNumberUtil.generateIdentityId(memberId), 
						ip,
						AccountType.SAVING_POT,
						collectTrade.getAmount(),
						IdType.UID,
						tradeCode,
						TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode()
						);
				// 后期需要对同步返回的状态进行一次update，同时异步也需要去处理
				if(collectTradeResult!=null && !collectTradeResult.isSuccess()) {
					throw new ManagerException(collectTradeResult.getErrorMsg());
				}
			}
		}
		
		return result;
	}

	@Override
	public ResultDO<?> synHostingCollectTrade(String tradeNo) throws Exception {
		HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
		if(hostingCollectTrade!=null) {
			if(TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType()==hostingCollectTrade.getType()) {
				ResultDto<QueryTradeResult> result = sinaPayClient.queryTrade(
						SerialNumberUtil.generateIdentityId(hostingCollectTrade.getPayerId()), 
						IdType.UID, 
						hostingCollectTrade.getTradeNo(), 
						1, 
						20, 
						null, 
						null
						);
				
				if(result!=null) {
					QueryTradeResult queryTradeResult = result.getModule();
					if(queryTradeResult!=null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
						TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
						logger.info("同步代收交易号："+tradeItem.getTradeNo()+"状态为："+tradeItem.getProcessStatus());
						//交易成功或者失败状态处理业务
						if (TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
								|| TradeStatus.PRE_AUTH_APPLY_SUCCESS.name().equals(tradeItem.getProcessStatus())
								|| TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())) {
							ResultDO<Transaction> transactionResult = myTransactionManager.afterTransactionCollectNotifyProcess(tradeNo, null, tradeItem.getProcessStatus());
							if(transactionResult!=null && transactionResult.isSuccess()) {
								if(transactionResult.getResult()!=null) {
									Transaction transaction = transactionResult.getResult();
									if(transaction!=null) {
										//发送投资成功通知
//										SendMsgClient.sendTransactionSuccessMsg(transaction.getMemberId(), transaction.getTransactionTime(), transaction.getInvestAmount());
										try{
											Project  dto =  projectManager.selectByPrimaryKey(transaction.getProjectId());
											if (dto.isDirectProject()){
												//同步代收，发送站内信和短信通知投资者        
												MessageClient.sendMsgForCommon(transaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM,
														MessageEnum.P2P_UNRAISE.getCode(), DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_7),
														(transaction.getProjectName().contains("期")?transaction.getProjectName().substring(0, transaction.getProjectName().indexOf("期") + 1):transaction.getProjectName()),
														transaction.getInvestAmount().toString(),transaction.getProjectId().toString());
												SPParameter parameter = new SPParameter();
												parameter.setMemberId(transaction.getMemberId());
												SPEngine.getSPEngineInstance().run(parameter);
											}else {
												SPParameter parameter = new SPParameter();
												parameter.setMemberId(transaction.getMemberId());
												parameter.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
												parameter.setBiz(transaction);
												SPEngine.getSPEngineInstance().run(parameter);
												MessageClient.sendMsgForInvestmentSuccess(DateUtils.getCurrentDate(), transaction.getProjectId(), transaction.getMemberId(), transaction.getTotalDays(), transaction.getTotalInterest(), transaction.getInvestAmount());
											}
										}catch(Exception e){
											logger.error("交易成功发送短信失败，projectId={},memberId={}",transaction.getProjectId(),transaction.getMemberId(),e);
										}
										//清理项目交易明细缓存
//										RedisProjectClient.clearTransactionDetailForProject(transaction.getProjectId());
										myTransactionManager.afterTransactionContract(transaction);
										activityAfterTransactionManager.afterTransactionEntry(transaction);
									}
								}
							} 
							
						}
					}
				}
			}
			//TODO 同步平台垫付现金券代收
			if(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_A.getType()==hostingCollectTrade.getType()) {
				ResultDto<QueryTradeResult> result = sinaPayClient.queryTrade(
						SerialNumberUtil.getBasicAccount(), 
						IdType.EMAIL, 
						hostingCollectTrade.getTradeNo(), 
						1, 
						20, 
						null, 
						null
						);
				if(result!=null) {
					QueryTradeResult queryTradeResult = result.getModule();
					if(queryTradeResult!=null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
						TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
						logger.info("同步平台垫付现金券代收交易号："+tradeItem.getTradeNo()+"状态为："+tradeItem.getProcessStatus());
						//交易成功或者失败状态处理业务
						if(TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
								||TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())) {
						 myTransactionManager.afterPaltformCouponCollectNotify(tradeNo, tradeItem.getTradeNo(), tradeItem.getProcessStatus());
							
						}
					}
				}
			}
			
			//垫资还款代收
			if(TypeEnum.HOSTING_COLLECT_TRADE_OVERDUE_REPAY.getType()==hostingCollectTrade.getType()) {
				ResultDto<QueryTradeResult> result = sinaPayClient.queryTrade(
						SerialNumberUtil.generateIdentityId(hostingCollectTrade.getPayerId()), 
						IdType.UID,
						hostingCollectTrade.getTradeNo(), 
						1, 
						20, 
						null, 
						null
						);
				if(result!=null) {
					QueryTradeResult queryTradeResult = result.getModule();
					if(queryTradeResult!=null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
						TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
						logger.info("同步垫资还款代收交易号："+tradeItem.getTradeNo()+"状态为："+tradeItem.getProcessStatus());
						if(TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
								||TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())) {
							ResultDO<List<HostingPayTrade>> localPayResult = projectManager.afterOverdueRepayCollectNotify(tradeNo, tradeItem.getTradeNo(), tradeItem.getProcessStatus());
							if(localPayResult!=null && localPayResult.isSuccess()) {
								projectManager.createSinpayHostingPayTradeForRepay(localPayResult.getResult());
							}
						}
					}
				}
			}
			
			if(TypeEnum.HOSTING_COLLECT_TRADE_PROJECT_GUARNANTEEFEE.getType()==hostingCollectTrade.getType()) {
				ResultDto<QueryTradeResult> result = sinaPayClient.queryTrade(
						SerialNumberUtil.generateIdentityId(hostingCollectTrade.getPayerId()), 
						IdType.UID,
						hostingCollectTrade.getTradeNo(), 
						1, 
						20, 
						null, 
						null
						);
				if(result!=null) {
					QueryTradeResult queryTradeResult = result.getModule();
					if(queryTradeResult!=null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
						TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
						logger.info("同步保证金归还代收交易号："+tradeItem.getTradeNo()+"状态为："+tradeItem.getProcessStatus());
						if(TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
								||TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())) {
							ResultDO<HostingPayTrade> localPayResult = projectManager.afterGuaranteeFeeCollectNotify(tradeNo, tradeItem.getTradeNo(), tradeItem.getProcessStatus());
							if(localPayResult!=null && localPayResult.isSuccess()) {
								projectManager.createSinpayHostingPayTradeForGuaranteeFee(localPayResult.getResult());
							}
						}
					}
				}
			}
			
		}
		return null;
	}

	@Override
	public ResultDO<HostingCollectTrade> processPayInterestAndPrincipal(String tradeNo)
			throws Exception {
		//如果tradeNo为null，则表示处理所有还未处理的代收
		ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
		if(StringUtil.isBlank(tradeNo)) {
			//查询本息状态为4的代收
			List<String> collectTradeNos = transactionInterestManager.selectCurrentDateCollectTradeNos(StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus());
			if(Collections3.isNotEmpty(collectTradeNos)) {
				for (String collectTradeNo : collectTradeNos) {
					repaymentManager.processPayInterestAndPrincipal(collectTradeNo);
				}
			}
			
		} else {
			return repaymentManager.processPayInterestAndPrincipal(tradeNo);
		}
		return result;
	}

	@Override
	public Page<Transaction> findByPage(Page<Transaction> pageRequest,
			Map<String, Object> map) throws Exception {
		try {
			return myTransactionManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<TransactionForOrder> queryTansactionForList(
			Page<TransactionForOrder> pageRequest, Map<String, Object> map) {
		try {
			if(map.get("memberId") != null || map.get("mobile") != null) {
				map.put("memberSel", "INNER JOIN");
			}
			if(map.get("checkAll") == null ) {
				//支付方式：组合查询
				String SQLStr = null;
				if (map.containsKey("checkCapital")
						&& !map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//仅存钱罐
					SQLStr = " IFNULL(o.pay_amount, 0) = 0 and IFNULL(o.cash_coupon_no,'') = '' and o.used_capital > 0  ";
				}
				if (!map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//仅现金券
					SQLStr = " IFNULL(o.used_capital, 0) = 0 and IFNULL(o.pay_amount, 0) = 0 and IFNULL(o.cash_coupon_no,'') != ''  ";
				}
				if (!map.containsKey("checkCapital")
						&& !map.containsKey("checkCoupon")
						&& map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//仅网银
					SQLStr = " IFNULL(o.used_capital, 0) = 0 and IFNULL(o.cash_coupon_no,'') = '' and o.pay_amount > 0 and o.pay_method = 1  ";
				}
				if (!map.containsKey("checkCapital")
						&& !map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& map.containsKey("checkFastPayment")) {
					//仅快捷支付
					SQLStr = " IFNULL(o.used_capital, 0) = 0 and IFNULL(o.cash_coupon_no,'') = '' and o.pay_amount > 0 and o.pay_method = 2  ";
				}
				if (map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//存钱罐+现金券
					SQLStr = " IFNULL(o.pay_amount, 0) = 0 and IFNULL(o.cash_coupon_no,'') != '' and o.used_capital > 0  ";
				}
				if (map.containsKey("checkCapital")
						&& !map.containsKey("checkCoupon")
						&& map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//存钱罐+网银
					SQLStr = " o.pay_amount > 0 and o.pay_method = 1 and o.used_capital > 0 and IFNULL(o.cash_coupon_no,'') = '' ";
				}
				if (map.containsKey("checkCapital")
						&& !map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& map.containsKey("checkFastPayment")) {
					//存钱罐+快捷
					SQLStr = " o.pay_amount > 0 and o.pay_method = 2 and o.used_capital > 0 and IFNULL(o.cash_coupon_no,'') = '' ";
				}
				if (!map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//现金券+网银
					SQLStr = " IFNULL(o.cash_coupon_no,'') != '' and o.pay_amount > 0 and o.pay_method = 1 and IFNULL(o.used_capital, 0) = 0 ";
				}
				if (!map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& map.containsKey("checkFastPayment")) {
					//现金券+快捷
					SQLStr = " IFNULL(o.cash_coupon_no,'') != '' and o.pay_amount > 0 and o.pay_method = 2 and IFNULL(o.used_capital, 0) = 0 ";
				}
				if (map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//存钱罐余额+现金券+网银
					SQLStr = " IFNULL(o.cash_coupon_no,'') != '' and o.pay_amount > 0 and o.pay_method = 1 and o.used_capital > 0 ";
				}
				if (map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& map.containsKey("checkFastPayment")) {
					//存钱罐余额+现金券+快捷
					SQLStr = " IFNULL(o.cash_coupon_no,'') != '' and o.pay_amount > 0 and o.pay_method = 2 and o.used_capital > 0 ";
				}
				map.put("payMethod", SQLStr);
			}
			return myTransactionManager.queryTansactionForList(pageRequest, map);
		} catch (Exception e) {
			logger.error("交易管理分页查询发生异常", e);
		}
		return null;
	}

	@Override
	public Page<TransactionInterest> showInterestRecord(
			Page<TransactionInterest> pageRequest, Map<String, Object> map) {
		TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
		transactionInterestQuery.setTransactionId((Long)map.get("id"));
		try {
			List<TransactionInterest> transactionInterests = transactionInterestManager
					.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			pageRequest.setData(transactionInterests);
			return pageRequest;
		} catch (ManagerException e) {
			logger.error("根据交易ID查询付息记录, tansaction_id=" + map.get("id"), e);
		}
		return null;
	}

	@Override
	public ResultDO<?> addHostingPayTradeToSina(String tradeNo) {
		ResultDO<?> result = new ResultDO();
		try {
			HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByTradeNo(tradeNo);
			if(hostingPayTrade!=null && hostingPayTrade.getTradeStatus().equals(TradeStatus.WAIT_PAY.name())) {
				ResultDto<CreateSinglePayTradeResult> sinpayResult = sinaPayClient.createSinglePayTrade(
						AccountType.SAVING_POT,
						// project.getTotalAmount(),
						hostingPayTrade.getAmount(), hostingPayTrade.getTradeNo(), IdType.UID,
						SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()),
						hostingPayTrade.getUserIp(),
						hostingPayTrade.getRemarks(), TradeCode.PAY_TO_BORROWER);
				// TODO 后期需要对同步返回的状态进行一次update，同时异步也需要去处理
				if (sinpayResult != null && !sinpayResult.isSuccess()) {
					throw new ManagerException(sinpayResult.getErrorMsg());
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("保存代付到新浪（处理代付本地保存了，新浪没有保存的业务）,tradeNo=" + tradeNo, e);
		}
		return null;
	}

	@Override
	public ResultDO<?> synProjectHostCollectToSina(Long projectId) {
		ResultDO<?> result = new ResultDO();
		try {
			List<HostingCollectTrade> collectTrades = hostingCollectTradeManager.getWaitPayTradeNoByProjectId(projectId);
			for (HostingCollectTrade hostingCollectTrade : collectTrades) {
				result = this.synHostingCollectTrade(hostingCollectTrade.getTradeNo());
			}
			return result;
		} catch (Exception e) {
			logger.error("同步项目待支付的交易失败,projectId=" + projectId, e);
		}
		return null;
	}
	
	private void checkProjectStatusForLoan(Long projectId,ResultDO<?> resultDO) throws ManagerException{
		Project project = projectManager.selectByPrimaryKey(projectId);
		if (project != null && ProjectEnum.PROJECT_TYPE_DIRECT.getType() == project.getInvestType()
				&& StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus() != project.getStatus()) {
			resultDO.setResultCode(ResultCode.PROJECT_NOT_FULL_STATUS_ERROR);
		}
	}

	@Override
	public ResultDO<?> synHostingPayTrade(String tradeNo) throws Exception {
		ResultDO<?> resultDO = new ResultDO();
		HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByTradeNo(tradeNo);
		if (hostingPayTrade == null) {
			resultDO.setSuccess(false);
			return resultDO;
		}
		ResultDto<QueryTradeResult> result = sinaPayClient.queryTrade(
				SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()), IdType.UID, hostingPayTrade.getTradeNo(), 1, 20,
				null, null);
		if (result == null) {
			resultDO.setSuccess(false);
			return resultDO;
		}
		QueryTradeResult queryTradeResult = result.getModule();
		if (queryTradeResult != null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
			TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
			logger.info("同步代付交易号：" + tradeItem.getTradeNo() + "状态为：" + tradeItem.getProcessStatus());
			if (TypeEnum.HOSTING_COLLECT_TRADE_OVERDUE_REPAY.getType() == hostingPayTrade.getType()) {
				// 垫资还款代付回调
				if (TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
						|| TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())) {
					projectManager.afterOverdueRepayHostingPay(tradeItem.getProcessStatus(), tradeNo,
							tradeItem.getTradeNo());
				}
			}
			if(TypeEnum.HOSTING_PAY_TRADE_LOAN.getType()==hostingPayTrade.getType() 
					|| TypeEnum.HOSTING_PAY_TRADE_LOAN_BORROWER.getType()==hostingPayTrade.getType()
					|| TypeEnum.HOSTING_PAY_TRADE_MANAGER_FEE.getType() == hostingPayTrade.getType()
					|| TypeEnum.HOSTING_PAY_TRADE_GUARANTEEFEE.getType() == hostingPayTrade.getType()
					|| TypeEnum.HOSTING_PAY_TRADE_RISK_FEE.getType() == hostingPayTrade.getType()
					|| TypeEnum.HOSTING_PAY_TRADE_INTRODUCE_FEE.getType() == hostingPayTrade.getType()) {
				// 放款代付成功后处理
				if (TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
						|| TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())) {
					return myTransactionManager.afterHostingPayTradeLoan(tradeItem.getProcessStatus(), tradeNo);
				}
			}
			if(TypeEnum.HOSTING_PAY_TRADE_GUARANTEEFEE_RETURN.getType()==hostingPayTrade.getType()) {
				// 项目保证金代付成功后处理
				if (TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
						|| TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())) {
					return projectManager.afterGuaranteeFeeHostingPay(tradeItem.getProcessStatus(), tradeNo,
							tradeItem.getTradeNo());
				}
			}
		}
		return resultDO;
	}
	
	
	@Override
	public ResultDO<?> synHostingRefundTrade(String tradeNo) throws Exception {
		HostingRefund hostingRefund = hostingRefundManager.selectByTradeNo(tradeNo);
		if (hostingRefund != null) {
			if (TypeEnum.REFUND_TYPE_TRANSACTION.getType() == hostingRefund.getType()) {
				ResultDto<QueryRefundResult> result = sinaPayClient.queryRefund(
						SerialNumberUtil.generateIdentityId(hostingRefund.getReceiverId()), IdType.UID, hostingRefund.getTradeNo(), 1, 20,
						null, null);
				if (result != null) {
					QueryRefundResult queryRefundResult = result.getModule();
					if (queryRefundResult != null && Collections3.isNotEmpty(queryRefundResult.getTradeList())) {
						TradeItem tradeItem = queryRefundResult.getTradeList().get(0);
						logger.info("同步退款交易号：" + tradeItem.getTradeNo() + "状态为：" + tradeItem.getProcessStatus());
						// 交易退款最终状态处理
						if (RefundStatus.SUCCESS.name().equals(tradeItem.getProcessStatus()) || RefundStatus.FAILED.name().equals(tradeItem.getProcessStatus())) {
							return myTransactionManager.afterHostingRefund(tradeItem.getProcessStatus(), tradeNo, tradeItem.getTradeNo());
						}
					}
					// 如果该笔退款交易订单在新浪不存在
					if (ResponseCodeEnum.ILLEGAL_OUTER_TRADE_NO.name().equals(result.getErrorCode())) {
						ResultDO<Object> resultDO = new ResultDO<Object>();
						resultDO.setResult(ResponseCodeEnum.ILLEGAL_OUTER_TRADE_NO.name());
						return resultDO;
					}
				}
			}
		}
		return null;
	}

	@Override
	public ResultDO<?> createHostPayForLoan(String collectTradeNo) {
		ResultDO<?> resultDO = new ResultDO();
		try {
			HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNo(collectTradeNo);
			if (collectTrade == null) {
				resultDO.setSuccess(false);
				return resultDO;
			}
			myTransactionManager.createHostPayForLoan(null, null, null, collectTrade);
		} catch (Exception e) {
			logger.error("放款批付异常处理接口调用失败，collectTradeNo={}", e);
		}
		return resultDO;
	}

	public Object rebuildHostingCollectTrade(String collectTradeNo) {
		ResultDO<Object> resultDO = new ResultDO();
		try {
			HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNo(collectTradeNo);
			if (collectTrade == null) {
				ResultCode.SINA_PAY_ERROR.setMsg("代收为null");
				resultDO.setResultCode(ResultCode.SINA_PAY_ERROR);
				return resultDO;
			}
			ResultDto<QueryTradeResult> queryTradeResultResultDto = sinaPayClient.queryTrade(
					SerialNumberUtil.generateIdentityId(collectTrade.getPayerId()), IdType.UID, collectTrade.getTradeNo(), 0, 20, null,
					null);
			// 只有新浪 这笔代收不存在 才可以重复创建
			if (!ErrorCode.ORDER_NOT_EXIST.code().equalsIgnoreCase(queryTradeResultResultDto.getErrorCode())) {
				ResultCode.SINA_PAY_ERROR.setMsg("新浪代收已经存在");
				resultDO.setResultCode(ResultCode.SINA_PAY_ERROR);
				return resultDO;
			}
			HostingCollectTrade collectTradeForLock = hostingCollectTradeManager.getByIdForLock(collectTrade.getId());
			// String remarks ="新浪代收不存在";
			// 重新代收需要交易状态为待支付
			if (!TradeStatus.WAIT_PAY.name().equals(collectTradeForLock.getTradeStatus())) {
				hostingCollectTradeManager.updateTradeStatus(collectTradeForLock, TradeStatus.WAIT_PAY.name(), "");
			}
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			// 用户投资单独处理
			if (collectTradeForLock.getType() == TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType()) {
				Order order = orderManager.selectByPrimaryKey(collectTrade.getSourceId());
				boolean isWithholdAuthority = true;
				if (collectTrade.getIsWithholdAuthority() != null
						&& collectTrade.getIsWithholdAuthority().intValue() == StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus()) {
					isWithholdAuthority = false;
				}
				return tradeProcManager.createSinpayHostingCollectTradeAfterLocalSuccess(order,
						TypeEnum.TRADE_SOURCE_TYPE_TRANSACTION.getType(), ip, isWithholdAuthority, null);
			} else if (collectTradeForLock.getType() == TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType()) {
				return sinaPayClient
						.createHostingCollectTrade(collectTrade.getTradeNo(), TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getDesc(),
								SerialNumberUtil.generateIdentityId(collectTrade.getPayerId()), ip, AccountType.SAVING_POT,
								collectTrade.getAmount(), IdType.UID, TradeCode.COLLECT_FROM_BORROWER,
								TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode());

			} else if (collectTradeForLock.getType() == TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()) {
				return sinaPayClient.createHostingCollectTrade(collectTrade.getTradeNo(),
						TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getDesc(), SerialNumberUtil.getBasicAccount(), ip, AccountType.BASIC,
						collectTrade.getAmount(), IdType.EMAIL, TradeCode.COLLECT_FROM_PLATFORM_PROFITCOUPON,
						TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode());
			} else {
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
				return resultDO;
			}
		} catch (Exception e) {
			logger.error("根据代收交易号查询代收交易记录发生异常，tradeNo={}", collectTradeNo, e);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return resultDO;
		}
	}

	@Override
	public ResultDO<?> synBatchHostingPayTrade(String batchPayNo) throws Exception {
		ResultDO<?> resultDO = new ResultDO();
		List<HostingPayTrade> hostingPayTrades = hostingPayTradeManager.selectHostPayByBatchNoAndStatus(batchPayNo,
				TradeStatus.WAIT_PAY.name());
		if (Collections3.isEmpty(hostingPayTrades)) {
			resultDO.setSuccess(false);
			return resultDO;
		}
		for (HostingPayTrade hostingPayTrade : hostingPayTrades) {
			ResultDto<QueryTradeResult> result = sinaPayClient.queryTrade(
					SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()), IdType.UID,
					hostingPayTrade.getTradeNo(), 1, 20, null, null);
			if (result == null) {
				continue;
			}
			QueryTradeResult queryTradeResult = result.getModule();
			TradeItem tradeItem = queryTradeResult.getPayItemList().get(0);
			logger.info("同步代付交易号：" + tradeItem.getTradeNo() + "状态为：" + tradeItem.getProcessStatus());
			if (queryTradeResult != null && Collections3.isNotEmpty(queryTradeResult.getPayItemList())) {
				if (TypeEnum.HOSTING_PAY_TRADE_RETURN.getType() == hostingPayTrade.getType()) {
					if (TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
							|| TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())) {
						afterHostingPayHandleManager.afterPayInterestAndPrincipal(tradeItem.getProcessStatus(),
								hostingPayTrade.getTradeNo(), tradeItem.getTradeNo());
					}
				}
			}
		}
		return resultDO;
	}

	@Override
	public ResultDO<?> createRemoteBatchPay(Long projectId) throws Exception {
		ResultDO<?> result = new ResultDO();
		hostingPayTradeManager.createRemoteBatchHostingPayForRepayment(
				hostingPayTradeManager.selectBatchPayNosByProject(projectId, TradeStatus.WAIT_PAY.name()),
				projectId
				);
		return result;
	}
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<?> createHostBatchPay(String tradeNo) throws Exception {
		ResultDO<?> result = new ResultDO();
		try {
			if(hostingCollectTradeManager.isAllCollectFinishedForRepayment(tradeNo)) {
				HostingCollectTrade hostingCollectTradeUnlock = hostingCollectTradeManager.getByTradeNo(tradeNo);
				if(hostingCollectTradeUnlock!=null) {
					HostingCollectTrade hostingCollectTradeForlock = hostingCollectTradeManager.getByIdForLock(hostingCollectTradeUnlock.getId());
					List<String> batchPayNos = hostingPayTradeManager.createHostBatchHostingPayForRepayment(tradeNo);
					hostingPayTradeManager.createRemoteBatchHostingPayForRepayment(batchPayNos,hostingCollectTradeForlock.getProjectId());
				}
			}
			return result;
		} catch (Exception e) {
			throw new Exception(e);
		}
		
	}
	
	@Override
	public ResultDO<Object> getContractDownUrl(Long transactionId) {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(transaction==null){
				result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				result.setSuccess(false);
				return result;
			}
			
			String downUrl = myTransactionManager.getContractDownUrl(transactionId);
			if(StringUtil.isBlank(downUrl)){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setSuccess(false);
				return result;
			}
			result.setResult(downUrl);
		} catch (Exception e) {
			logger.error("合同下载异常,transactionId =" + transactionId, e);
		}
		return result;
	}
}
