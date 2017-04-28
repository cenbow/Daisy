package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.time.StopWatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rop.thirdparty.com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.yourong.api.dto.ContractSignDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.TransactionForMemberDto;
import com.yourong.api.dto.TransactionForProjectDto;
import com.yourong.api.dto.TransactionInterestDto;
import com.yourong.api.dto.UnSignContractDto;
import com.yourong.api.service.CapitalInOutLogService;
import com.yourong.api.service.TransactionService;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.api.utils.SysServiceUtils;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.SinaBankMobileEnum;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.OSSUtil;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.manager.OverdueRepayLogManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.DebtTransferManager;
import com.yourong.core.ic.manager.LeaseBonusDetailManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.LeaseBonusDetail;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.biz.TransactionTransferRecordBiz;
import com.yourong.core.ic.model.biz.TransferInformation;
import com.yourong.core.ic.model.biz.TransferRateList;
import com.yourong.core.ic.model.biz.TransferRecordBiz;
import com.yourong.core.ic.model.query.TransferRecordQuery;
import com.yourong.core.lottery.manager.RedPackageManager;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.mc.model.biz.ActivityForFirstInvest;
import com.yourong.core.mc.model.biz.ActivityForKing;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.tc.manager.ContractManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.ContractBiz;
import com.yourong.core.tc.model.biz.MemberTransactionCapital;
import com.yourong.core.tc.model.biz.TradeBiz;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.biz.TransactionForProject;
import com.yourong.core.tc.model.biz.TransferContractBiz;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;


/**
 * 交易核心业务类
 * @author Leon Ray
 * 2014年9月20日-下午6:38:08
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TransactionManager myTransactionManager;
    
    @Autowired
    private ContractManager contractManager;
    
    @Autowired
    private ProjectManager projectManager;
    
    @Autowired
    private BalanceManager balanceManager;
    
    @Autowired
    private DebtManager debtManager;
    
    @Autowired
    private MemberManager memberManager;
    
    @Autowired
    private CouponManager couponManager;
    
    @Autowired
    private DebtTransferManager debtTransferManager;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private TransactionInterestManager transactionInterestManager;
    
    @Autowired
    private HostingCollectTradeManager hostingCollectTradeManager;
    
    @Autowired
    private HostingPayTradeManager hostingPayTradeManager;
    
    @Autowired
    private SinaPayClient sinaPayClient;
    
    
    @Resource
	private ThreadPoolTaskExecutor taskExecutor;
    
    @Resource
    private BscAttachmentManager bscAttachmentManager;
    
    @Resource
    private AttachmentIndexManager attachmentIndexManager;
    
    @Autowired
    private PopularityInOutLogManager popularityInOutLogManager;
    
    @Autowired
    private LeaseBonusDetailManager leaseBonusDetailManager;

    @Autowired
	private ActivityManager activityManager;
	
	@Autowired
	private RedPackageManager redPackageManager;
	
	@Autowired 
	private OverdueRepayLogManager overdueRepayLogManager;

	@Autowired
	private ActivityAfterTransactionManager activityAfterTransactionManager;
	
	@Autowired 
	private TradeManager tradeManager;
	
	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Autowired
	private OverdueLogManager overdueLogManager;
	
	@Autowired
	private CapitalInOutLogService capitalInOutLogService;
	
	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	
	@Autowired
	private ProjectExtraManager projectExtraManager;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Order> createTransactionHostingTradeOld(Order order, int sourceType, String payerIp) throws Exception {
		ResultDO<Order> result = new ResultDO<Order>();
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			//首先判断订单如果已经是支付中了，就直接返回，无需再创建代收交易
			Order orderForLock = orderManager.getOrderByIdForLock(order.getId());
			if (orderForLock.getStatus() == StatusEnum.ORDER_WAIT_PROCESS.getStatus()) {
				result.setResultCode(ResultCode.ORDER_FRONT_ORDER_YET_PAYING);
				logger.info("交易前置条件验证失败：" + ResultCode.ORDER_FRONT_ORDER_YET_PAYING.getMsg() + ", orderNo="
						+ order.getOrderNo());
				return result;

			}
			result = preValidate(order, sourceType);

			// 来源为充值notify
			if (TypeEnum.TRADE_SOURCE_TYPE_RECHARGE_NOTIFY.getType() == sourceType) {
				logger.info("[充值notify]创建代收交易开始，orderNo=" + order.getOrderNo());
				// 如果前置验证失败，则叫订单置为已充值，交易失败
				if (!result.isSuccess()) {
					orderManager.updateStatus(order, StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus(), result
							.getResultCode().getMsg());
					// 如果有使用收益券，解锁收益券
					if (StringUtil.isNotBlank(order.getProfitCouponNo())) {
						couponManager.unLockCoupon(order.getProfitCouponNo());
					}
					// 如果有使用现金券，解锁现金券
					if (StringUtil.isNotBlank(order.getCashCouponNo())) {
						couponManager.unLockCoupon(order.getCashCouponNo());
					}
					MessageClient.sendMsgForInvestmentFail(order.getProjectId(), order.getMemberId());
					return result;
				}
				// 更新订单信息
				orderManager.updateStatus(order, StatusEnum.ORDER_WAIT_PROCESS.getStatus(), null);

				// 如果如果用户使用用户资金大于0，创建会员代收交易，同时调用第三方接口进行代收交易，记录用户资金流水并且更新余额
				double usedCapitalAmount = order.getUsedCapital().add(order.getPayAmount()).doubleValue();
				if (usedCapitalAmount > 0) {
					this.insertHostingCollectTrade(order, TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType(), payerIp);
				}
				// 冻结项目余额
				balanceManager.frozenBalance(TypeEnum.BALANCE_TYPE_PROJECT, order.getInvestAmount(), order.getProjectId());
			}

			// 来源为直接交易
			if (TypeEnum.TRADE_SOURCE_TYPE_TRANSACTION.getType() == sourceType) {
				logger.info("[交易时调用（无需充值）]创建代收交易开始，orderNo=" + order.getOrderNo());
				// 如果前置验证失败，则叫订单置为已充值，交易失败
				if (!result.isSuccess()) {
					orderManager.updateStatus(order, StatusEnum.ORDER_PAYED_FAILED.getStatus(), result
							.getResultCode().getMsg());
					// 如果有使用收益券，解锁收益券
					if (StringUtil.isNotBlank(order.getProfitCouponNo())) {
						couponManager.unLockCoupon(order.getProfitCouponNo());
					}
					// 如果有使用现金券，解锁现金券
//					if (StringUtil.isNotBlank(order.getCashCouponNo())) {
//						couponManager.unLockCoupon(order.getCashCouponNo());
//					}
					MessageClient.sendMsgForInvestmentFail(order.getProjectId(), order.getMemberId());
					return result;
				}
				// 更新订单信息
				orderManager.updateStatus(order, StatusEnum.ORDER_WAIT_PROCESS.getStatus(), null);
				// 创建代收交易
				double usedCapitalAmount = order.getUsedCapital().add(order.getPayAmount()).doubleValue();
				if (usedCapitalAmount > 0) {
					this.insertHostingCollectTrade(order, TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType(), payerIp);
					// 冻结项目余额
					//锁定现金券
					if (StringUtil.isNotBlank(order.getCashCouponNo())) {
						couponManager.lockCoupon(order.getCashCouponNo());
					}
					balanceManager.frozenBalance(TypeEnum.BALANCE_TYPE_PROJECT, order.getInvestAmount(), order.getProjectId());
				}
				// 如果代收等于0，直接执行交易
				if (usedCapitalAmount == 0) {
					logger.info("全部使用优惠券交易，无需创建代收交易，orderNo=" + order.getOrderNo());
					ResultDO<Transaction> transactionResult = myTransactionManager
							.doTransactionWithoutCreateHostingCollect(order);
					if (transactionResult.isSuccess()) {
						if (transactionResult.getResult() != null) {
							Transaction transaction = transactionResult.getResult();
							if (transaction != null) {
								// 生成合同
								// this.generateContract(transaction.getId());
								// 发送投资成功通知
//								SendMsgClient.sendTransactionSuccessMsg(transaction.getMemberId(),
//										transaction.getTransactionTime(), transaction.getInvestAmount());

								// 活动引擎
								SPParameter parameter = new SPParameter();
								parameter.setMemberId(transaction.getMemberId());
								SPEngine.getSPEngineInstance().run(parameter);

								// 清理项目交易明细缓存
//								RedisProjectClient.clearTransactionDetailForProject(transaction.getProjectId());
								Project  dto =  projectManager.selectByPrimaryKey(transaction.getProjectId());
								if (dto.isDirectProject()){
									//TODO  发送 短信通知投资者        
									MessageClient.sendMsgForCommon(transaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.P2P_UNRAISE.getCode(), 
											DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_7),
											(transaction.getProjectName().contains("期")?transaction.getProjectName().substring(0, transaction.getProjectName().indexOf("期") + 1):transaction.getProjectName()),
											transaction.getInvestAmount().toString(),transaction.getProjectId().toString());
								}else {
									MessageClient.sendMsgForInvestmentSuccess(DateUtils.getCurrentDate(), transaction.getProjectId(), transaction.getMemberId(), transaction.getTotalDays(), transaction.getTotalInterest(), transaction.getInvestAmount());
									
								}
								//交易后的合同初始化业务
								myTransactionManager.afterTransactionContract(transaction);
							}
						}
					} else {
						// 如果交易失败，则将解锁现金券和优惠券，同时将订单置为支付失败
						// 如果使用了优惠券，解锁优惠券
						if (StringUtil.isNotBlank(order.getProfitCouponNo())) {
							couponManager.unLockCoupon(order.getProfitCouponNo());
						}
						// 如果使用了优惠券，解锁优惠券
//						if (StringUtil.isNotBlank(order.getCashCouponNo())) {
//							couponManager.unLockCoupon(order.getCashCouponNo());
//						}
						orderManager.updateStatus(order, StatusEnum.ORDER_PAYED_FAILED.getStatus(), transactionResult
								.getResultCode().getMsg());
						MessageClient.sendMsgForInvestmentFail(order.getProjectId(), order.getMemberId());
						return result;
					}

				}
			}


			sw.stop();
			logger.info("创建代收交易结束，交易耗时：" + sw.getTime() + ", orderNo=" + order.getOrderNo());
			return result;
		} catch (Exception e) {
			logger.error("创建代收交易出错，order:" + order, e);
			throw new Exception(e);
		}
	}
	
	
	@Override
	public ResultDTO<TradeBiz> createTransactionHostingTrade(Order order, int sourceType, String payerIp,Integer source) {
		ResultDTO<TradeBiz> resultDTO = new ResultDTO<TradeBiz>();
		String returnUrl = APIPropertiesUtil.getProperties("sinaBankReturnUrl")+"?source="+source+"&type="+SinaBankMobileEnum.INVESTMENT_M.getStatus()+"&orderId="+order.getId();
		ResultDO<TradeBiz> resultDO = tradeManager.tradeUseCapital(order, sourceType, payerIp,returnUrl);
		resultDTO.setResultCode(resultDO.getResultCode());
		
		TradeBiz tra = new TradeBiz();
		tra = resultDO.getResult();
		String  redirectUrl =this.getReturnUrlByForm(tra.getRedirectUrl());
		logger.info("未开通委托且余额足够：返回支付重定向链接："+redirectUrl+",orderNo="+order.getOrderNo());
		tra.setRedirectUrl(redirectUrl);
		resultDTO.setResult(tra);
		if(resultDO.isSuccess()){
			resultDTO.setIsSuccess();
		}else{
			resultDTO.setIsError();
		}
		return resultDTO;
	}
	
	private String getReturnUrlByForm(String form){
		
		if(StringUtil.isBlank(form)){
			return "";
		}
		String value="";
		String returnUrl="";
		String errorDesc="";
		Document doc = Jsoup.parse(form);
		Element content = doc.getElementById("form1");
		Elements q=doc.getElementsByTag("input");
		for(Element link:q){
			if(link.attr("name").equals("ft")){
				value = link.attr("value");
			}else if(link.attr("name").equals("errorDesc")){
				errorDesc = link.attr("value");
			}else if(link.attr("name").equals("errorCode")){
				
			}
		}
		if(content!=null){
			returnUrl = content.attr("action");
		}
		if(StringUtil.isNotBlank(value)){
			returnUrl = returnUrl+"?ft="+value;
		}else if(StringUtil.isNotBlank(errorDesc)){
			returnUrl = returnUrl+"?errorDesc="+errorDesc;
		}
		return returnUrl;
	}
	
    /*@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDTO<Order> createTransactionHostingTrade(Order order, int sourceType, String payerIp) throws Exception {
    	ResultDTO<Order> result = new ResultDTO<Order>();
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			//首先判断订单如果已经是支付中了，就直接返回，无需再创建代收交易
			Order orderForLock = orderManager.getOrderByIdForLock(order.getId());
			if (orderForLock.getStatus() == StatusEnum.ORDER_WAIT_PROCESS.getStatus()) {
				result.setResultCode(ResultCode.ORDER_FRONT_ORDER_YET_PAYING);
				logger.info("交易前置条件验证失败：" + ResultCode.ORDER_FRONT_ORDER_YET_PAYING.getMsg() + ", orderNo="
						+ order.getOrderNo());
				return result;

			}
			result = preValidate(order, sourceType);

			// 来源为充值notify
			if (TypeEnum.TRADE_SOURCE_TYPE_RECHARGE_NOTIFY.getType() == sourceType) {
				logger.info("[充值notify]创建代收交易开始，orderNo=" + order.getOrderNo());
				// 如果前置验证失败，则叫订单置为已充值，交易失败
				if (!result.isSuccess()) {
					orderManager.updateStatus(order, StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus(), result
							.getResultCode().getMsg());
					// 如果有使用收益券，解锁收益券
					if (StringUtil.isNotBlank(order.getProfitCouponNo())) {
						couponManager.unLockCoupon(order.getProfitCouponNo());
					}
					// 如果有使用现金券，解锁现金券
					if (StringUtil.isNotBlank(order.getCashCouponNo())) {
						couponManager.unLockCoupon(order.getCashCouponNo());
					}
					MessageClient.sendMsgForInvestmentFail(order.getProjectId(), order.getMemberId());
					return result;
				}
				// 更新订单信息
				orderManager.updateStatus(order, StatusEnum.ORDER_WAIT_PROCESS.getStatus(), null);

				// 如果如果用户使用用户资金大于0，创建会员代收交易，同时调用第三方接口进行代收交易，记录用户资金流水并且更新余额
				double usedCapitalAmount = order.getUsedCapital().add(order.getPayAmount()).doubleValue();
				if (usedCapitalAmount > 0) {
					this.insertHostingCollectTrade(order, TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType(), payerIp);
				}
				// 冻结项目余额
				balanceManager.frozenBalance(TypeEnum.BALANCE_TYPE_PROJECT, order.getInvestAmount(), order.getProjectId());
			}

			// 来源为直接交易
			if (TypeEnum.TRADE_SOURCE_TYPE_TRANSACTION.getType() == sourceType) {
				logger.info("[交易时调用（无需充值）]创建代收交易开始，orderNo=" + order.getOrderNo());
				// 如果前置验证失败，则叫订单置为已充值，交易失败
				if (!result.isSuccess()) {
					orderManager.updateStatus(order, StatusEnum.ORDER_PAYED_FAILED.getStatus(), result
							.getResultCode().getMsg());
					// 如果有使用收益券，解锁收益券
					if (StringUtil.isNotBlank(order.getProfitCouponNo())) {
						couponManager.unLockCoupon(order.getProfitCouponNo());
					}
					// 如果有使用现金券，解锁现金券
//					if (StringUtil.isNotBlank(order.getCashCouponNo())) {
//						couponManager.unLockCoupon(order.getCashCouponNo());
//					}
					MessageClient.sendMsgForInvestmentFail(order.getProjectId(), order.getMemberId());
					return result;
				}
				// 更新订单信息
				orderManager.updateStatus(order, StatusEnum.ORDER_WAIT_PROCESS.getStatus(), null);
				// 创建代收交易
				double usedCapitalAmount = order.getUsedCapital().add(order.getPayAmount()).doubleValue();
				if (usedCapitalAmount > 0) {
					this.insertHostingCollectTrade(order, TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType(), payerIp);
					// 冻结项目余额
					//锁定现金券
					if (StringUtil.isNotBlank(order.getCashCouponNo())) {
						couponManager.lockCoupon(order.getCashCouponNo());
					}
					balanceManager.frozenBalance(TypeEnum.BALANCE_TYPE_PROJECT, order.getInvestAmount(), order.getProjectId());
				}
				// 如果代收等于0，直接执行交易
				if (usedCapitalAmount == 0) {
					logger.info("全部使用优惠券交易，无需创建代收交易，orderNo=" + order.getOrderNo());
					ResultDO<Transaction> transactionResult = myTransactionManager
							.doTransactionWithoutCreateHostingCollect(order);
					if (transactionResult.isSuccess()) {
						if (transactionResult.getResult() != null) {
							Transaction transaction = transactionResult.getResult();
							if (transaction != null) {
								// 活动引擎
								Project dto = projectManager.selectByPrimaryKey(transaction.getProjectId());
								if (dto.isDirectProject()) {
									// 投资成功，发送站内信和短信通知投资者
									MessageClient.sendMsgForCommon(transaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM,
											MessageEnum.P2P_UNRAISE.getCode(), DateUtils.getStrFromDate(DateUtils.getCurrentDate(),
													DateUtils.DATE_FMT_7), transaction.getProjectName(), transaction.getInvestAmount()
													.toString(), transaction.getProjectId().toString());
									SPParameter parameter = new SPParameter();
									parameter.setMemberId(transaction.getMemberId());
									SPEngine.getSPEngineInstance().run(parameter);
								} else {
									SPParameter parameter = new SPParameter();
									parameter.setMemberId(transaction.getMemberId());
									parameter.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
									parameter.setBiz(transaction);
									SPEngine.getSPEngineInstance().run(parameter);
									MessageClient.sendMsgForInvestmentSuccess(DateUtils.getCurrentDate(), transaction.getProjectId(),
											transaction.getMemberId(), transaction.getTotalDays(), transaction.getTotalInterest(),
											transaction.getInvestAmount());
								}
								activityAfterTransactionManager.afterTransactionEntry(transaction);
							}
						}
					} else {
						// 如果交易失败，则将解锁现金券和优惠券，同时将订单置为支付失败
						// 如果使用了优惠券，解锁优惠券
						if (StringUtil.isNotBlank(order.getProfitCouponNo())) {
							couponManager.unLockCoupon(order.getProfitCouponNo());
						}
						// 如果使用了优惠券，解锁优惠券
//						if (StringUtil.isNotBlank(order.getCashCouponNo())) {
//							couponManager.unLockCoupon(order.getCashCouponNo());
//						}
						orderManager.updateStatus(order, StatusEnum.ORDER_PAYED_FAILED.getStatus(), transactionResult
								.getResultCode().getMsg());
						MessageClient.sendMsgForInvestmentFail(order.getProjectId(), order.getMemberId());
						return result;
					}

				}
			}


			sw.stop();
			logger.info("创建代收交易结束，交易耗时：" + sw.getTime() + ", orderNo=" + order.getOrderNo());
			return result;
		} catch (Exception e) {
			logger.error("创建代收交易出错，order:" + order, e);
			throw new Exception(e);
		}
	}*/
    
	private ResultDto<CreateCollectTradeResult> createSinpayHostingCollectTrade(
			HostingCollectTrade memberHostingCollectTrade, String payerIp) throws ManagerException {
		try {
			if(memberHostingCollectTrade!=null) {
				String collectTradeType = null;
				if (TypeEnum.COLLECT_TRADE_TYPE_FREEZE.getType() == memberHostingCollectTrade.getIsPreAuth()) {
					collectTradeType = TypeEnum.COLLECT_TRADE_TYPE_FREEZE.getCode();
				}
				//调用第三方接口完成托管代收交易
				ResultDto<CreateCollectTradeResult> result = sinaPayClient.createHostingCollectTrade(
						memberHostingCollectTrade.getTradeNo(), 
						TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getDesc(), // 后期修改
						SerialNumberUtil.generateIdentityId(memberHostingCollectTrade.getPayerId()), 
						payerIp,
						AccountType.SAVING_POT,
						memberHostingCollectTrade.getAmount(),
						IdType.UID,
						TradeCode.COLLECT_FROM_INVESTOR,
						collectTradeType
						);
//				// 后期需要对同步返回的状态进行一次update，同时异步也需要去处理
//				if(result!=null && !result.isSuccess()) {
//					throw new ManagerException(result.getErrorMsg());
//				}
//				if(result.getModule().getPayStatus().name().equals(PayStatus.FAILED.name())) {
//					//如果支付失败,返回
//					throw new ManagerException(memberHostingCollectTrade.getTradeNo()+"支付失败");
//				}
				return result;
			}
		} catch (Exception e) {
			throw e;
		}
		
		return null;
		
	}



	/**
	 * 交易前置条件验证，只有所有验证通过才会执行交易核心业务
	 * @param
	 * @param order
	 * @param sourceType 
	 * @param
	 * @return
	 * @throws ManagerException 
	 */
	private ResultDO<Order> preValidate(Order order, int sourceType) throws ManagerException {
		ResultDO<Order> result = new ResultDO<Order>(order);
		BigDecimal usedCapital = new BigDecimal(0);
		BigDecimal usedCouponAmount = new BigDecimal(0);
		BigDecimal investAmount = new BigDecimal(0);
		BigDecimal payAmount = new BigDecimal(0);
		try{
		if(order.getUsedCapital()!=null) {
				usedCapital = order.getUsedCapital();
		}
		if(order.getUsedCouponAmount()!=null) {
				usedCouponAmount = order.getUsedCouponAmount();
		}
		if(order.getInvestAmount()!=null) {
				investAmount = order.getInvestAmount();
		}
		if(order.getPayAmount()!=null) {
				payAmount = order.getPayAmount();
		}
		// 如果是充值notify，需要再一次验证订单状态
		if (TypeEnum.TRADE_SOURCE_TYPE_RECHARGE_NOTIFY.getType() == sourceType) {
			Order orderForLock = orderManager.selectByPrimaryKey(order.getId());
			
			if (orderForLock.getStatus() != StatusEnum.ORDER_WAIT_PAY.getStatus()) {
				result.setResultCode(ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY);
				logger.info("交易前置条件验证失败：" + ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY.getMsg() + ", orderNo="
						+ order.getOrderNo());
				return result;

			}
		}
		/**
		 * 基础判断
		 */
		// 如果使用账户资金和使用奖励账户资金以及支付金额都为0时，返回
		if (usedCapital.compareTo(BigDecimal.ZERO) < 1 && usedCouponAmount.compareTo(BigDecimal.ZERO) < 1
					&& payAmount.compareTo(BigDecimal.ZERO) < 1) {
			result.setResultCode(ResultCode.TRANSACTION_CAPITAL_REWARD_ZERO);
			logger.info("交易前置条件验证失败：" + ResultCode.TRANSACTION_CAPITAL_REWARD_ZERO.getMsg() + ", orderNo=" + order.getOrderNo());
			return result;
		}
		
		// 判断使用资金账户金额与使用现金券金额、支付金额是否等于投资金额
		if (usedCapital.add(usedCouponAmount).add(payAmount).compareTo(investAmount) != 0) {
			result.setResultCode(ResultCode.TRANSACTION_USEDCAPITAL_USEDREWARD_NOT_EQUAL_INVESTAMOUNT);
			logger.info("交易前置条件验证失败：" + ResultCode.TRANSACTION_USEDCAPITAL_USEDREWARD_NOT_EQUAL_INVESTAMOUNT.getMsg() + ", orderNo=" + order.getOrderNo());
			return result;
		}
		/**
		 * 项目相关判断
		 */
		Project project = projectManager.selectByPrimaryKey(order.getProjectId());
		
		
		//项目不存在返回
		if(project==null) {
			result.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
			logger.info("交易前置条件验证失败：" + ResultCode.PROJECT_NOT_EXIST_ERROR.getMsg() + ", orderNo=" + order.getOrderNo() + ", projectId=" + order.getProjectId());
			return result;
		}
		
		// 如果使用账户资金和使用现金券金额相加小于项目最小投资额时，返回
		BigDecimal minInvestAmount = project.getMinInvestAmount();
		if (minInvestAmount.compareTo(usedCapital.add(usedCouponAmount).add(payAmount)) > 0) {
			result.setResultCode(ResultCode.TRANSACTION_PROJECT_LESS_MINIVESTMOUNT);
			logger.info("交易前置条件验证失败：" + ResultCode.TRANSACTION_PROJECT_LESS_MINIVESTMOUNT.getMsg() + ", orderNo=" + order.getOrderNo() + ", order=" + order);
			return result;
		}
		
		
		// 判断当前项目是否是可销售状态
		if (project.getStatus().intValue()!=StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
			result.setResultCode(ResultCode.PROJECT_NOT_INVESTING_STATUS_ERROR);
			logger.info("交易前置条件验证失败：" + ResultCode.PROJECT_NOT_INVESTING_STATUS_ERROR.getMsg() + ", orderNo=" + order.getOrderNo() + ", projectId=" + order.getProjectId());
			return result;
		}
		
		if(project.isNoviceProject()) {
//			优惠券限制解除
//			if(StringUtil.isNotBlank(order.getCashCouponNo()) && order.getInvestAmount().compareTo(new BigDecimal(Constant.NOVICE_INVEST_AMOUNT)) < 0) {
//				result.setResultCode(ResultCode.ORDER_NEW_MEMBER_FIRST_NOT_ALLOW_USED_COUPON_ERROR);
//				logger.info("前台交易订单前置条件验证失败：" + ResultCode.ORDER_NEW_MEMBER_FIRST_NOT_ALLOW_USED_COUPON_ERROR.getMsg() + ", order=" + order.getOrderNo());
//				return result;
//			}
			if(myTransactionManager.getTransactionCountByMember(order.getMemberId()) > 0){
				result.setResultCode(ResultCode.ORDER_ONLY_ALLOWING_NEW_USERS_ERROR);
				logger.info("前台交易订单前置条件验证失败：" + ResultCode.ORDER_ONLY_ALLOWING_NEW_USERS_ERROR.getMsg() + ", order=" + order.getOrderNo());
				return result;
			}
		}
		// 如果(用户投资总额-起投金额)/递增金额 余数等于0
		BigDecimal incrementAmount = project.getIncrementAmount();
		if (investAmount.subtract(minInvestAmount).remainder(incrementAmount).compareTo(BigDecimal.ZERO) != 0) {
			result.setResultCode(ResultCode.TRANSACTION_INCREMENT_AMOUNT_ERROR);
			logger.info("交易前置条件验证失败：" + ResultCode.TRANSACTION_INCREMENT_AMOUNT_ERROR.getMsg() + ", orderNo=" + order.getOrderNo() + ", order=" + order);
			return result;
		}
		
		
		/**
		 * 用户存钱罐相关判断
		 */
		// 判断用户存钱罐余额是否足够以及现金券余额是否与现金券匹配
		Long memberId = order.getMemberId();
		BigDecimal investTotalAmount = usedCapital.add(payAmount);
		// 判断用户资金余额
		if (investTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
			Balance piggyBalance = balanceManager.queryFromThirdPay(memberId,TypeEnum.BALANCE_TYPE_PIGGY);
//			Balance piggyBalance = balanceManager.queryBalance(memberId,TypeEnum.BALANCE_TYPE_PIGGY);
			BigDecimal capitalAccountBalance = piggyBalance.getAvailableBalance();
			if (investTotalAmount.compareTo(capitalAccountBalance) > 0) {
				result.setResultCode(ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING);
				logger.info("交易前置条件验证失败：" + ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING.getMsg() + ", orderNo=" + order.getOrderNo() + ", order=" + order);
				return result;
			}
		}
		if(TypeEnum.TRADE_SOURCE_TYPE_RECHARGE_NOTIFY.getType()!=sourceType) {
			//判断现金券余额是否与现金券匹配
			if(StringUtil.isNotBlank(order.getCashCouponNo())) {
				//优惠券限制解除
//				if(project.isNoviceProject() && order.getInvestAmount().compareTo(new BigDecimal(Constant.NOVICE_INVEST_AMOUNT)) < 0){
//					result.setResultCode(ResultCode.COUPON_CASH_DISABLE_BY_NOVICE_PROJECT_ERROR);
//					logger.info("交易前置条件验证失败：" + ResultCode.COUPON_CASH_DISABLE_BY_NOVICE_PROJECT_ERROR.getMsg() + ", orderNo=" + order.getOrderNo() + ", CashCouponNo=" + order.getCashCouponNo());
//					return result;
//				}
//				2015.06.10 尹龙确认创建代收的时候无需再校验优惠券，已在前置业务校验
				//Coupon coupon = couponManager.getCouponByCouponNo(order.getCashCouponNo());
				
//				if(usedCouponAmount!=coupon.getAmount().doubleValue()) {
//					result.setResultCode(ResultCode.TRANSACTION_COUPON_AMOUNT_NOT_MATCH_ERROR);
//					logger.info("交易前置条件验证失败：" + ResultCode.TRANSACTION_COUPON_AMOUNT_NOT_MATCH_ERROR.getMsg() + ", orderNo=" + order.getOrderNo() + ", CashCouponNo=" + order.getCashCouponNo());
//					return result;
//				}
			}
		}
//		if (TypeEnum.TRADE_SOURCE_TYPE_TRANSACTION.getType()==sourceType ){
//			if(StringUtil.isNotBlank(order.getCashCouponNo())) {
//				Coupon coupon = couponManager.getCouponByCouponNo(order.getCashCouponNo());
//				
//				if (coupon ==null ){
//					result.setResultCode(ResultCode.COUPON_NOT_EXIST_ERROR);
//					logger.info("交易前置条件验证失败：" + ResultCode.COUPON_NOT_EXIST_ERROR.getMsg() + ", orderNo="
//							+ order.getOrderNo() + ", CashCouponNo=" + order.getCashCouponNo());
//					return result;
//				}
//				Coupon couponForLock = couponManager.selectCouponforUpdate(coupon.getId());
//				if (StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus() != couponForLock.getStatus()) {
//					result.setResultCode(ResultCode.COUPON_STATUS_NOT_VALID_ERROR);
//					logger.info("交易前置条件验证失败：" + ResultCode.COUPON_STATUS_NOT_VALID_ERROR.getMsg() + ", orderNo="
//							+ order.getOrderNo() + ", CashCouponNo=" + order.getCashCouponNo());
//					return result;
//				}
//				if(usedCouponAmount!=couponForLock.getAmount().doubleValue()) {
//					result.setResultCode(ResultCode.TRANSACTION_COUPON_AMOUNT_NOT_MATCH_ERROR);
//					logger.info("交易前置条件验证失败：" + ResultCode.TRANSACTION_COUPON_AMOUNT_NOT_MATCH_ERROR.getMsg() + ", orderNo=" + order.getOrderNo() + ", CashCouponNo=" + order.getCashCouponNo());
//					return result;
//				}
//			}
//
//		}
		//项目余额为0 修改为加锁查询
		Balance projectBalance = balanceManager.queryBalanceLocked(order.getProjectId(),TypeEnum.BALANCE_TYPE_PROJECT);
		if(projectBalance!=null) {
			if(projectBalance.getAvailableBalance()!=null && projectBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) == 0) {
				result.setResultCode(ResultCode.PROJECT_BALANCE_ZERO_ERROR);
				logger.debug("交易前置条件验证失败：" + ResultCode.PROJECT_BALANCE_ZERO_ERROR.getMsg() + ", orderNo=" + order.getOrderNo() + ", projectId=" + order.getProjectId());
				return result;
			}
		}
		
		// 判断项目余额是否足够
		if (projectBalance.getAvailableBalance()!=null && projectBalance.getAvailableBalance().compareTo(investAmount) < 0) {
			result.setResultCode(ResultCode.PROJECT_BALANCE_LACEING_ERROR);
			logger.debug("交易前置条件验证失败：" + ResultCode.PROJECT_BALANCE_LACEING_ERROR.getMsg() + ", projectBalance=" + projectBalance.getAvailableBalance().toPlainString() + ", order=" + order);
			return result;
		}

		result.setSuccess(true);
		} catch (Exception e) {
			result.setResultCode(ResultCode.TRANSACTION_PRE_VALIDATE_EXCEPTION_ERROR);
			logger.error("交易前判断发生异常, orderNo=" + order.getOrderNo(), e);
			throw new ManagerException("交易前判断发生异常", e);
		}
		return result;
	}

	

	/**
	 * 插入代收交易，并且调用第三方支付接口进行代收交易
	 * @param order
	 * @param collectType
	 * @throws ManagerException 
	 */
	private HostingCollectTrade insertHostingCollectTrade(Order order, int collectType, String payerIp) throws ManagerException {
		try {
			HostingCollectTrade trade = hostingCollectTradeManager.selectWaitPayOrFinishedHostingCollectTrade(order.getId(), TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType());
			if(trade != null) {
				throw new ManagerException("订单"+order.getId()+",已经存在待收，创建待收失败");
			}
			// 本地保存HostingCollectTrade信息
			BigDecimal amount = BigDecimal.ZERO;
			Long payerId = 0l;
			HostingCollectTrade collectTrade = new HostingCollectTrade();
			if (TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType() == collectType) {
				amount = order.getUsedCapital().add(order.getPayAmount());
				payerId = order.getMemberId();
			}
			if (TypeEnum.HOSTING_COLLECT_TRADE_COUPON_A.getType() == collectType) {
				amount = order.getUsedCouponAmount();
				payerId = Long.parseLong(Config.internalMemberId);
			}
			collectTrade.setAmount(amount);
			collectTrade.setPayerId(payerId);
			collectTrade.setBankCode(order.getBankCode());
			collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
			collectTrade.setRemarks(order.getRemarks());
			collectTrade.setSourceId(order.getId());
			collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
			collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
			collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
			collectTrade.setPayStatus((SinaPayEnum.PAY_PROCESSING.getCode()));
			collectTrade.setTradeTime(order.getOrderTime());
			collectTrade.setType(collectType);
			collectTrade.setPayerIp(payerIp);
			// 代收类型如果是代收冻结，则插入冻结类型
			hostingCollectTradeManager.loadCollectTradeByOrder(order, collectTrade);
			if(hostingCollectTradeManager.insertSelective(collectTrade)>0) {
				return collectTrade;
			}
			
		} catch (ManagerException e) {
			logger.error("交易出错[保存HostingCollectTrade信息出错]：order=" + order, e);
			throw e;
		}
		return null;
	}


	@Override
	public boolean generateContract(Long transactionId) {
		try {
			//生成合同
			if(transactionId!=null) {
				taskExecutor.execute(new GenerateContractThread(transactionId));
			}
		} catch (Exception e) {
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
		public void run() {
			logger.debug("生成合同线程开始执行，transactionId：" +transactionId);
			try {
				BscAttachment attachment = contractManager.saveContract(transactionId,"api");
				attachment.setListOrder(0);
				attachment.setUploadTime(DateUtils.getCurrentDate());
				attachment.setModule(Constant.ATTACHMENT_CONTRACT_IDENTITY);
				attachment.setDelFlag(Constant.ENABLE);
				attachment.setStorageWay(TypeEnum.ATTACHMENT_STORAGE_WAY_OSS.getCode());
				int rows = bscAttachmentManager.insertSelective(attachment);
				if(rows>0) {
					//如果有索引，则先把他删除
					if(attachmentIndexManager.deleteAttachmentIndexByKeyId(transactionId.toString())>0) {
						attachmentIndexManager.insertAttachmentIndex(attachment.getId(), transactionId.toString());
					}
				}
			} catch (Exception e) {
				logger.error("生成合同发生异常，transactionId：" +transactionId, e);
			}
		}
	}
	

	@Override
	public Page<TransactionForProjectDto> selectTransactionForProjectsForPage(TransactionQuery transactionQuery) {
		try {
			Long projectId = transactionQuery.getProjectId();
			//普通项目交易类型
			transactionQuery.setProjectCategory(TypeEnum.PROJECT_CATEGORY_NORMAL.getType());
			Page<TransactionForProject>	transactionsPage = myTransactionManager.selectTransactionForProjectsForPage(transactionQuery);
			List<TransactionForProject> transactionForProjects = transactionsPage.getData();
			if (Collections3.isNotEmpty(transactionForProjects)) {
				PopularityInOutLogQuery query = new PopularityInOutLogQuery();
				query.setSourceId(transactionQuery.getProjectId());
				query.setType(TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY.getType());

				if(transactionQuery.getCurrentPage() == 1){
					// 设置一锤定音
					query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
					List<ActivityForFirstInvest> lastList = popularityInOutLogManager
							.selectActivityForFirstInvestList(query);
					if (Collections3.isNotEmpty(lastList)) {
						transactionForProjects.get(0).setLastInvest(true);
					}
				}

				if(transactionQuery.getCurrentPage() == transactionsPage.getTotalPageCount()){
					// 设置一羊领头
					query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
					List<ActivityForFirstInvest> firstList = popularityInOutLogManager
							.selectActivityForFirstInvestList(query);
					if (Collections3.isNotEmpty(firstList)) {
						transactionForProjects.get(transactionForProjects.size() - 1).setFirstInvest(true);
					}
				}
				//幸运女神
				query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
				List<ActivityForFirstInvest> luckList = popularityInOutLogManager
						.selectActivityForFirstInvestList(query);
				Long luckTransactionId = 0L;
				if (Collections3.isNotEmpty(luckList)) {
					ActivityForFirstInvest activityForFirstInvest = luckList.get(0);
					TransactionQuery luckQuery = new TransactionQuery();
					luckQuery.setProjectId(projectId);
					luckQuery.setMemberId(activityForFirstInvest.getMemberId());
					List<Transaction> luckTransactions = myTransactionManager
							.selectTransactionsByQueryParams(luckQuery);
					if (Collections3.isNotEmpty(luckTransactions)) {
						luckTransactionId = luckTransactions.get(0).getId();
					}
				}
				//一鸣惊人
				query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
				List<ActivityForFirstInvest> mostList = popularityInOutLogManager
						.selectActivityForFirstInvestList(query);
				Long mostTransactionId = 0L;
				if (Collections3.isNotEmpty(mostList)) {
					mostTransactionId = myTransactionManager.selectMostTransactionByProject(projectId).getId();
				}
				if(transactionQuery.getCurrentPage() == 1){
					// 一掷千金
					query.setRemark(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks());
					List<ActivityForFirstInvest> mostAndLastList = popularityInOutLogManager
							.selectActivityForFirstInvestList(query);					
					if (Collections3.isNotEmpty(mostAndLastList)) {
						transactionForProjects.get(0).setMostAndLastInvest(true);
					}
				}
				for (TransactionForProject transactionForProject : transactionForProjects) {
					// 设置一鸣惊人
					if (transactionForProject.getId().longValue() == mostTransactionId.longValue()) {
						transactionForProject.setMostInvest(true);
					}
					// 设置幸运女神
					if (transactionForProject.getId().longValue() == luckTransactionId.longValue()) {
						transactionForProject.setLuckInvest(true);
					}
				}
				//设置租赁分红收益
				for(TransactionForProject transactionForProject:transactionForProjects){
					//租赁分红明细
					List<LeaseBonusDetail> details = leaseBonusDetailManager.findListByTransactionId(transactionForProject.getId());
					if(Collections3.isNotEmpty(details)){
						transactionForProject.setLeaseBonusDetails(details);
					}
					//租赁总额
					LeaseBonusDetail bonusDetail = leaseBonusDetailManager.getLeaseTotalIncomeByTransactionId(transactionForProject.getId()); 
					if(bonusDetail!=null){
						transactionForProject.setLeaseBonusAmounts(bonusDetail.getBonusAmount());
						transactionForProject.setBonusAnnualizedRate(bonusDetail.getBonusRate());
					}else{
						transactionForProject.setLeaseBonusAmounts(BigDecimal.ZERO);
						transactionForProject.setBonusAnnualizedRate(BigDecimal.ZERO);
					}
				}
			}
			
			Page<TransactionForProjectDto> pageList = new Page<TransactionForProjectDto>();
			List<TransactionForProjectDto> list = Lists.newArrayList();
			if(Collections3.isNotEmpty(transactionsPage.getData())){
				list = BeanCopyUtil.mapList(transactionsPage.getData(), TransactionForProjectDto.class);
			}
			pageList.setData(list);
			pageList.setiDisplayLength(transactionsPage.getiDisplayLength());
			pageList.setiDisplayStart(transactionsPage.getiDisplayStart());
			pageList.setiTotalRecords(transactionsPage.getiTotalRecords());
			pageList.setPageNo(transactionsPage.getPageNo());
			return pageList;
		} catch (Exception e) {
			logger.error("通过项目id分页查询交易记录，transactionQuery=" + transactionQuery, e);
		}
		return null;
	}

	@Override
	public MemberTransactionCapital getMemberTransactionCapital(Long memberID) {
		try {
			MemberTransactionCapital memberTransactionCapital = new MemberTransactionCapital();
			TransactionQuery transactionQuery = new TransactionQuery();
			transactionQuery.setMemberId(memberID);
			//原有的方法注释掉
			List<Transaction> transactions = myTransactionManager.selectTransactionsByQueryParams(transactionQuery);
			
			//过滤p2p项目的新方法
			//List<Transaction> transactions = myTransactionManager.selectTransactionsByQueryParamsFilterP2p(transactionQuery);
			
			memberTransactionCapital.setMemberId(memberID);
			if(Collections3.isNotEmpty(transactions)) {
//				int finishedInvestNum = 0;
//				int subsistingInvestNum = 0;
				BigDecimal finishedInvestTotal = BigDecimal.ZERO;
				BigDecimal subsistingInvestTotal = BigDecimal.ZERO;
				for (Transaction transaction : transactions) {
					//已完结投资
					if(StatusEnum.TRANSACTION_COMPLETE.getStatus()==transaction.getStatus()) {
//						finishedInvestNum++;
						finishedInvestTotal = finishedInvestTotal.add(transaction.getInvestAmount());
					}
					//存续期投资
					if(StatusEnum.TRANSACTION_REPAYMENT.getStatus()==transaction.getStatus()) {
//						subsistingInvestNum++;
						subsistingInvestTotal = subsistingInvestTotal.add(transaction.getInvestAmount());
					}
				}
//				memberTransactionCapital.setFinishedInvestNum(finishedInvestNum);
//				memberTransactionCapital.setSubsistingInvestNum(subsistingInvestNum);
				memberTransactionCapital.setFinishedInvestTotal(finishedInvestTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setSubsistingInvestTotal(subsistingInvestTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setMemberId(memberID);
			//原有的方法注释掉
			//List<TransactionInterest> transactionInterests = transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			//过滤p2p项目的新方法
			List<TransactionInterest> transactionInterests = transactionInterestManager.selectTransactionInterestsByQueryParamsFilterP2p(transactionInterestQuery);
			if(Collections3.isNotEmpty(transactionInterests)) {
				BigDecimal receivableInterest = BigDecimal.ZERO;
				BigDecimal receivablePrincipal = BigDecimal.ZERO;
				BigDecimal receivedInterest = BigDecimal.ZERO;
				BigDecimal receivedPrincipal = BigDecimal.ZERO;
				 /**待收利息笔数**/
			    int receivableInterestNum = 0;
			    /**已收利息笔数**/
			    int receivedInterestNum = 0;
			    /** 已收本金笔数 **/
				int finishedInvestNum = 0;
				/** 代收本金笔数 **/
				int subsistingInvestNum = 0;
				for (TransactionInterest transactionInterest : transactionInterests) {
					//未付利息和本金
					if(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus()==transactionInterest.getStatus()
							||StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus()==transactionInterest.getStatus()) {
						receivableInterest = receivableInterest.add(transactionInterest.getPayableInterest());
						receivablePrincipal = receivablePrincipal.add(transactionInterest.getPayablePrincipal());
						if(transactionInterest.getPayablePrincipal().compareTo(BigDecimal.ZERO)>0){
							subsistingInvestNum += 1;
						}
						receivableInterestNum +=1;
					}
					//已付利息和本金
					if(StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus()==transactionInterest.getStatus()
							|| StatusEnum.TRANSACTION_INTEREST_PART_PAYED.getStatus()==transactionInterest.getStatus()) {
						receivedInterest = receivedInterest.add(transactionInterest.getRealPayInterest());
						receivedPrincipal = receivedPrincipal.add(transactionInterest.getRealPayPrincipal());
						if(transactionInterest.getRealPayPrincipal().compareTo(BigDecimal.ZERO)>0){
							finishedInvestNum += 1;
						}
						receivedInterestNum +=1;
					}
				}
				memberTransactionCapital.setFinishedInvestNum(finishedInvestNum);
				memberTransactionCapital.setSubsistingInvestNum(subsistingInvestNum);
				memberTransactionCapital.setReceivableInterest(receivableInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivablePrincipal(receivablePrincipal.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivedInterest(receivedInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivedPrincipal(receivedPrincipal.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivableInterestNum(receivableInterestNum);
				memberTransactionCapital.setReceivedInterestNum(receivedInterestNum);
			}
			return memberTransactionCapital;
		} catch (Exception e) {
			logger.error("查询会员投资资金信息出错，memberID=" + memberID, e);
		}
		return null;
	}

	
	@Override
	public List<TransactionForProject> selectNewTransactions(int pageSize) {
		try {
			return myTransactionManager.selectNewTransactions(pageSize);
		} catch (Exception e) {
			logger.error("查询最新投资记录出异常", e);
		}
		return null;
	}

	@Override
	public ResultDO<Transaction> afterTransactionCollectNotifyProcess(String tradeNo,
			String outTradeNo, String tradeStatus) {
		 ResultDO<Transaction> result = new ResultDO<Transaction>();
		try {
			result = myTransactionManager.afterTransactionCollectNotifyProcess(tradeNo, outTradeNo, tradeStatus);
//			if(!result.isSuccess()) {
//				afterTransactionCollectNotifyProcessFailed(tradeNo);
//			}
			return result;
		} catch (Exception e) {
			logger.error("执行交易发生异常，tradeNo：" +tradeNo, e);
			result.setSuccess(false);
//			afterTransactionCollectNotifyProcessFailed(tradeNo);
			return result;
		}
	}
	
	/**
	 * 交易代收回调后处理方法失败后的业务
	 * @param tradeNo
	 */
	private void afterTransactionCollectNotifyProcessFailed(String tradeNo) {
		try {
		//将订单置为交易失败状态
			Order order = orderManager.getOrderByTradeNo(tradeNo);
			orderManager.updateStatus(order, StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus(), TradeStatus.TRADE_FAILED.name());
			balanceManager.unfrozenBalance(TypeEnum.BALANCE_TYPE_PROJECT, order.getInvestAmount(), order.getProjectId());
			//如果使用了优惠券，解锁优惠券
			if(StringUtil.isNotBlank(order.getProfitCouponNo())) {
				couponManager.unLockCoupon(order.getProfitCouponNo());
			}
			//如果使用了优惠券，解锁优惠券
			if(StringUtil.isNotBlank(order.getCashCouponNo())) {
				couponManager.unLockCoupon(order.getCashCouponNo());
			}
		} catch (Exception e) {
			logger.error("交易代收回调后处理方法失败后的业务发生异常，tradeNo：" +tradeNo, e);
		}
	}

	
	@Override
	public void afterloanToOriginalCreditor(String tradeStatus,
			String outTradeNo) {
		//将交易状态置为最终状态
		HostingPayTrade hostingPayTrade;
		try {
			hostingPayTrade = hostingPayTradeManager.getByTradeNo(outTradeNo);
			hostingPayTrade.setTradeStatus(tradeStatus);
			hostingPayTrade.setOutTradeNo(outTradeNo);
			hostingPayTradeManager.updateHostingPayTrade(hostingPayTrade);
		} catch (ManagerException e) {
			logger.error("处理回调后放款给原始债权人业务发生异常，tradeNo：" +outTradeNo, e);
		}
		
	}

	@Override
	public Page<TransactionForMemberCenter> selectAllTransactionForMember(
			TransactionQuery query) {
		
		try {
			Page<TransactionForMemberCenter> page = myTransactionManager.selectAllTransactionForMember(query);
			return page;
		} catch (Exception e) {
			logger.error("查询用户中心交易记录，TransactionQuery：" +query, e);
		}
		return null;
	}

	/*@Override
	public ResultDO<TransactionForMemberCenter> getTransactionForMemberCenter(
			Long transactionId) {
		ResultDO<TransactionForMemberCenter> result = new ResultDO<TransactionForMemberCenter>();
		try {
			TransactionForMemberCenter transactionForMemberCenter = myTransactionManager.getTransactionForMember(transactionId);
			result.setResult(transactionForMemberCenter);
			return result;
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("获取交易详情发生异常，transactionId：" +transactionId, e);
		}
		return null;
	}*/

	@Override
	public ResultDO<Transaction> getTransactionByOrderId(Long orderId) {
		ResultDO<Transaction> result = new ResultDO<Transaction>();
		try {
			Transaction transaction = myTransactionManager.getTransactionByOrderId(orderId);
			result.setResult(transaction);
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("通过订单id获取交易信息发生异常，orderId：" +orderId, e);
		}
		return result;
	}

	@Override
	public ResultDTO<ContractBiz> previewContract(Long memberId, Long projectId,
			BigDecimal investAmount, BigDecimal annualizedRate) {
		ResultDTO<ContractBiz> result = new ResultDTO<ContractBiz>();
		try {
			ContractBiz contractBiz = contractManager.getPreviewContract(memberId, projectId, investAmount, annualizedRate, DateUtils.getCurrentDate());
			result.setResult(contractBiz);
			return result;
		} catch (Exception e) {
			result.setResultCode(ResultCode.ERROR);
			logger.error("合同预览发生异常，projectId：{},memberId:{},annualizedRate:{}", projectId, memberId,annualizedRate, e);
		}
		return result;
	}

	@Override
	public ResultDO<String> downloadContract(Long memberId, Long transactionId) {
		ResultDO<String> result = new ResultDO<String>();
		try {
			//判断该交易是否是属于该用户
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(transaction==null) {
				result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return result;
			}
			if(!transaction.getMemberId().equals(memberId)) {
				result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
				return result;
			}
			//判断密码是否正确
//			Member member = memberManager.selectByPrimaryKey(memberId);
//			String mobile = member.getMobile().toString();
//			String localPassword = mobile.substring(mobile.length()-6, mobile.length());
//			if(!localPassword.equals(password)) {
//				result.setResultCode(ResultCode.MEMBER_DOWNLOAD_CONTRACT_PASSWORD_NOT_EQUAL);
//				return result;
//			}
			BscAttachment attachment = bscAttachmentManager.getBscAttachmentByKeyIdAndModule(transactionId.toString(), DebtEnum.ATTACHMENT_MODULE_CONTRACT.getCode());
			String downloadUrl = OSSUtil.getContractDownloadUrl(attachment.getFileUrl()+attachment.getFileName(), null);
			result.setResult(downloadUrl);
			return result;
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("下载合同发生异常，memberId：" +memberId, e);
		}
		return result;
	}

	@Override
	public int getTransactionCount(Long memberId, int type) {
		try {
			return myTransactionManager.getTransactionCount(memberId,type);
		} catch (Exception e) {
			logger.error("查询交易记录总数发生异常，memberId：" +memberId, e);
		}
		return 0;
	}

	@Override
	public ResultDO<?> afterPaltformCouponCollectNotify(String tradeNo,
			String outTradeNo, String tradeStatus) {
		try {
			return myTransactionManager.afterPaltformCouponCollectNotify(tradeNo, outTradeNo, tradeStatus);
		} catch (Exception e) {
			logger.error("代收平台垫付优惠券回调处理方法发生异常，tradeNo：" +tradeNo, e);
		}
		return null;
	}

	

	@Override
	public ResultDTO<ContractBiz> viewContract(Long orderId,Long memberId) {
		ResultDTO<ContractBiz> result = new ResultDTO<ContractBiz>();
		try {
			//TODO 前置校验
			Order order = orderManager.selectByPrimaryKey(orderId);
			if(order.getStatus()==StatusEnum.ORDER_PAYED_INVESTED.getStatus()) {
				Transaction transaction = myTransactionManager.getTransactionByOrderId(orderId);
				if(transaction==null) {
					result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
					return result;
				}
				if(!transaction.getMemberId().equals(memberId)) {
					result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
					return result;
				}
				ContractBiz contractBiz = contractManager.getViewContract(transaction.getId());
				result.setResult(contractBiz);
			} else {
				BigDecimal annualizedRate = order.getAnnualizedRate(); 
				if(order.getExtraAnnualizedRate()!=null) {
					annualizedRate = annualizedRate.add(order.getExtraAnnualizedRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				ContractBiz contractBiz = contractManager.getPreviewContract(memberId, order.getProjectId(), order.getInvestAmount(), annualizedRate, order.getOrderTime());
				result.setResult(contractBiz);
			}
			
			return result;
		} catch (Exception e) {
			result.setResultCode(ResultCode.ERROR);
			logger.error("查看合同发生异常，orderId：" + orderId, e);
		}
		return result;
	}
	
	@Override
	public ResultDTO<ContractBiz> p2pViewContract(Long orderId, Long memberId) {
		ResultDTO<ContractBiz> result = new ResultDTO<ContractBiz>();
		try {
			ResultDO<ContractBiz> resultDO = myTransactionManager.p2pViewContract(orderId, memberId);
			result.setResult(resultDO.getResult());
			return result;
		} catch (Exception e) {
			result.setIsError();
			logger.error("查看P2P合同发生异常，orderId：" + orderId, e);
		}
		return result;
	}


	@Override
	public int getTransactionCountByProject(Long projectId) {
		try {
			return myTransactionManager.getTransactionCountByProject(projectId);
		} catch (Exception e) {
			logger.error("通过项目id查询投资笔数发生异常,projectId= "+projectId, e);
		}
		return 0;
	}
	@Override
	public int getTransactionMemberCountByProject(Long projectId) {
		try {
			return myTransactionManager.getTransactionMemberCountByProject(projectId);
		} catch (Exception e) {
			logger.error("通过项目id查询投资会员数发生异常,projectId= "+projectId, e);
		}
		return 0;
	}

	@Override
	public BigDecimal getTotalTransactionInterestByProject(Long projectId) {
		try {
			return myTransactionManager.getTotalTransactionInterestByProject(projectId);
		} catch (Exception e) {
			logger.error("通过项目id查询该项目总收益发生异常,projectId= "+projectId, e);
		}
		return BigDecimal.ZERO;
	}

	@Override
	public List<ActivityForKing> getRefferalInvestAmountList() {
		try {
			List<ActivityForKing> activityForKings = myTransactionManager.getRefferalInvestAmountList();
			if(Collections3.isNotEmpty(activityForKings)) {
				for (ActivityForKing activityForKing : activityForKings) {
					Member member = memberManager.selectByPrimaryKey(activityForKing.getMemberId());
					activityForKing.setUsername(StringUtil.maskUserNameOrMobile(member.getUsername(), member.getMobile()));
				}
			}
			return activityForKings;
		} catch (Exception e) {
			logger.error("我是王推荐好友投资额排行榜发生异常", e);
		}
		return null;
	}

	@Override
	public List<TransactionForFirstInvestAct> selectTopTenInvest() {
		try {
			return myTransactionManager.selectTopTenInvest();
		} catch (Exception e) {
			logger.error("查询投资排行前10异常", e);
		}
		return null;
	}

	@Override
	public boolean hasTransactionByMember(Long memberId) {
		try {
			int count = myTransactionManager.getTransactionCountByMember(memberId);
			if(count > 0){
				return true;
			}
		} catch (Exception e) {
			logger.error("查询用户是否投资异常", e);
		}
		return false;
	}
	
	@Override
	public boolean isMemberDirectInvest(Long memberId) {
		try {
			return myTransactionManager.isMemberDirectInvest(memberId);
		} catch (Exception e) {
			logger.error("查询用户是否投资过直投项目异常", e);
		}
		return false;
	}

	@Override
	public Page<TransactionForMemberDto> queryTransactionList(TransactionQuery query) {
		Page<TransactionForMemberDto>  dto = new Page<TransactionForMemberDto>();
		try {
			Page<TransactionForMemberCenter> page = myTransactionManager.selectAllTransactionForMember(query);
			dto.setData(BeanCopyUtil.mapList(page.getData(), TransactionForMemberDto.class));
			dto.setiDisplayLength(page.getiDisplayLength());
			dto.setiDisplayStart(page.getiDisplayStart());
			dto.setiTotalRecords(page.getiTotalRecords());
			dto.setPageNo(page.getPageNo());
			
			for(TransactionForMemberDto tran : dto.getData()){
				TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
				transactionInterestQuery.setTransactionId(tran.getTransactionId());
				List<TransactionInterest> transactionInterests= transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
				List<TransactionInterestDto> list = BeanCopyUtil.mapList(transactionInterests, TransactionInterestDto.class);
				tran.setTransactionInterest(list);
			}
		} catch (Exception e) {
			logger.error("查询用户中心交易记录，TransactionQuery：" +query, e);
		}
		return dto;
	}
	
	private Page<TransactionForMemberDto> queryTransactionListP2p(TransactionQuery query) {
		Page<TransactionForMemberDto>  dto = new Page<TransactionForMemberDto>();
		try {
			Page<TransactionForMemberCenter> page = myTransactionManager.p2pSelectAllTransactionForMember(query);
			dto.setData(BeanCopyUtil.mapList(page.getData(), TransactionForMemberDto.class));
			dto.setiDisplayLength(page.getiDisplayLength());
			dto.setiDisplayStart(page.getiDisplayStart());
			dto.setiTotalRecords(page.getiTotalRecords());
			dto.setPageNo(page.getPageNo());
			
			for(TransactionForMemberDto tran : dto.getData()){
				TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
				transactionInterestQuery.setTransactionId(tran.getTransactionId());
				List<TransactionInterest> transactionInterests= transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
				List<TransactionInterestDto> list = BeanCopyUtil.mapList(transactionInterests, TransactionInterestDto.class);
				tran.setTransactionInterest(list);
			}
		} catch (Exception e) {
			logger.error("查询用户中心交易记录，TransactionQuery：" +query, e);
		}
		return dto;
	}

	@Override
	public ResultDTO<TransactionForMemberDto> getTransactionDetailForMember(
			Long transactionId, Long memberId) {
		ResultDTO<TransactionForMemberDto> resultDto = new ResultDTO<TransactionForMemberDto>();
		try {
			TransactionForMemberDto detail = new TransactionForMemberDto();
			TransactionForMemberCenter transaction = myTransactionManager.getTransactionDetailForMember(transactionId, memberId);
			if (transaction == null) {
				resultDto.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return resultDto;
			}
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			transaction.setInvestType(project.getInvestType());
			BeanCopyUtil.copy(transaction, detail);
			this.p2pProjectCheck(detail, project);
		
			detail.setAnnualizedRate(detail.getAnnualizedRate().add(detail.getExtraProjectAnnualizedRate()));
			// 加载交易本息
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transactionId);
			List<TransactionInterest> transactionInterests = transactionInterestManager
					.selectTransactionInterestsByQueryParamsAndOverdue(transactionInterestQuery);
			
			this.transferProjectCheckNew(detail, project, transactionInterests,transaction);
			//直投抽奖
			detail.setQuickRewardFlag(0);
			if(transaction.getExtraInterestDay()>0){
				detail.setExtraName("起息日起，加息"+transaction.getExtraInterestDay()+"天");
				BigDecimal extraInterest = FormulaUtil.calculateInterest(transaction.getInvestAmount(),transaction.getExtraAnnualizedRate(),transaction.getExtraInterestDay());
				if(extraInterest==null){
					extraInterest=BigDecimal.ZERO;
				}
				detail.setTotalInterest(transaction.getTotalInterest());
			}
			
			ProjectExtra pro = projectExtraManager.getProjectQucikReward(project.getId());
			if(pro.getActivitySign()!=null&&pro.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
				//查询是否有未抽奖的记录
				ActivityLotteryResult model=new ActivityLotteryResult();
				model.setActivityId(pro.getActivityId());
				model.setMemberId(memberId);
				model.setRemark(project.getId().toString());
				model.setRewardId(transactionId.toString());
				model.setLotteryStatus(StatusEnum.LOTTERY_STATUS_NO.getStatus());
				List<ActivityLotteryResult> listActivityResult=activityLotteryResultManager.getLotteryResultBySelectiveAndLotteryStatus(model);
				if(Collections3.isNotEmpty(listActivityResult)){
					detail.setQuickRewardFlag(1);
				}
			}
			/*ProjectExtra pro = projectExtraManager.getProjectQucikReward(project.getId());
			if(pro.getActivitySign()!=null&&pro.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
				if(transaction.getStatus()==StatusEnum.TRANSACTION_INVESTMENTING.getStatus()||
						transaction.getStatus()==StatusEnum.TRANSACTION_REPAYMENT.getStatus()
						){
					detail.setQuickRewardFlag(1);
				}
			}*/
			
			if (Collections3.isNotEmpty(transactionInterests)) {
				List<TransactionInterestDto> list = BeanCopyUtil.mapList(transactionInterests, TransactionInterestDto.class);
				for(TransactionInterestDto transactionInterestDto : list ){
					if(transactionInterestDto.getStatus()==1){
						if(TypeEnum.REPAYMENT_TYPE_NORMAL.getType()==transactionInterestDto.getPayType()
								||TypeEnum.REPAYMENT_TYPE_LOANREPAYMENT.getType()==transactionInterestDto.getPayType()){
							transactionInterestDto.setEndDate(transactionInterestDto.getPayTime()!=null?transactionInterestDto.getPayTime():transactionInterestDto.getEndDate());
						}
						if(TypeEnum.REPAYMENT_TYPE_PREREPAYMENT.getType()==transactionInterestDto.getPayType()){
							transactionInterestDto.setPayTime(transactionInterestDto.getTopayDate()!=null?transactionInterestDto.getTopayDate():transactionInterestDto.getEndDate());
						}
						
					}else{
						if(TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_OVERDUE.getType()==transactionInterestDto.getPayType()){
							Integer days = DateUtils.daysBetween(transactionInterestDto.getEndDate(), DateUtils.getCurrentDate());
							transactionInterestDto.setOverDays(days);
						}
					}
				}
				
				detail.setTransactionInterest(list);
			}
			getRedPackageStatus(detail);
			// 判断活动
			resultDto.setResult(detail);
		} catch (Exception e) {
			logger.error("查询用户交易详情异常：" +transactionId, e);
			resultDto.setResultCode(ResultCode.ERROR);
		}
		return resultDto;
	}
	
	private void transferProjectCheckNew(TransactionForMemberDto transactionForMemberCenter,Project project,List<TransactionInterest> traList,TransactionForMemberCenter transaction) {
		try {
			transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_NORMAL.getType());
			transactionForMemberCenter.setTransferFlag(0);
			
			if(StatusEnum.TRANSACTION_INVESTMENTING.getStatus()==transactionForMemberCenter.getStatus()){
				//募集中不计算额外信息
				return;
			}
			
			if(transactionForMemberCenter.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_NORMAL.getType()&&project.getTransferFlag()==1){
				Balance balance = balanceManager.queryBalance(transactionForMemberCenter.getTransactionId(), TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT);
				if(balance!=null&&balance.getAvailableBalance().compareTo(transactionForMemberCenter.getInvestAmount())!=0){
					transactionForMemberCenter.setTransferFlag(1);
				}
				//可转让交易都显示债权转让
				transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_TRANSFER.getType());
				if(StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus()==transactionForMemberCenter.getTransferStatus()){
					transactionForMemberCenter.setOperaPrompt("转让中");
				}
				if(StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus()==transactionForMemberCenter.getTransferStatus()||
						StatusEnum.TRANSACTION_TRANSFER_STATUS_ALL_TRANSFERED.getStatus()==transactionForMemberCenter.getTransferStatus()){
					//部分转让，显示已转让金额 //全部转让，显示已转让金额
					if(balance!=null&&balance.getAvailableBalance().compareTo(transactionForMemberCenter.getInvestAmount())!=0){
						BigDecimal amount = transactionForMemberCenter.getInvestAmount().subtract(balance.getAvailableBalance());
						String operaPrompt = "已转让￥"+FormulaUtil.getFormatPrice(amount);
						transactionForMemberCenter.setOperaPrompt(operaPrompt);
					}
				}
				
			}
			if(transactionForMemberCenter.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){//投资转让项目
				//剩余期限 通过交易时间计算
				Integer residualDays = 0 ;
				Date lastEndDate = DateUtils.getCurrentDate();
				for(TransactionInterest tra :traList){
					if(lastEndDate.getTime()<tra.getEndDate().getTime()){//遍历最大的还款时间，即为最后一期
						lastEndDate = tra.getEndDate();
					}
					if(tra.getEndDate().getTime()< DateUtils.getCurrentDate().getTime() ){
						continue;
					}
					if(tra.getEndDate().getTime() == DateUtils.getCurrentDate().getTime()){
						continue;
					}
					if(DateUtils.getCurrentDate().getTime() < tra.getEndDate().getTime() ){
					}
				}
				residualDays = DateUtils.daysOfTwo(DateUtils.formatDate(transactionForMemberCenter.getTransactionTime(), DateUtils.DATE_FMT_3), lastEndDate) + 1 ;
				transactionForMemberCenter.setProfitPeriod(residualDays+"天");
			}
		} catch (ManagerException e) {
			logger.error("交易详情，转让相关信息获取异常：project={},transactionId={}" ,project.getId(),transactionForMemberCenter.getTransactionId(), e);
		}
	}
	
	private void transferProjectCheck(TransactionForMemberDto transactionForMemberCenter, Project project,List<TransactionInterest> traList,TransactionForMemberCenter transaction) {
		
		try {
			if(transactionForMemberCenter.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
				
				transactionForMemberCenter.setTotalInterest(transactionForMemberCenter.getTotalPrincipal().add(transactionForMemberCenter.getTotalInterest())
						.subtract(transactionForMemberCenter.getInvestAmount()));
				TransferProject transferProject = transferProjectManager.selectByPrimaryKey(transactionForMemberCenter.getTransferId());
				
				transactionForMemberCenter.setSubscriptionAmount(transactionForMemberCenter.getTransferPrincipal());
				
				transactionForMemberCenter.setProfitPeriod(transferProjectManager.getReturnDay(transferProject.getTransactionId()).toString());
				transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_NORMAL.getType());
				transactionForMemberCenter.setProjectStatus(transferProject.getStatus());
				return ;
			}
			
			// 项目是否可以转让
			if (project.getTransferFlag()==1&&transactionForMemberCenter.getProjectCategory()!=TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()
					&&transactionForMemberCenter.getStatus()!=StatusEnum.TRANSACTION_INVESTMENTING.getStatus()
					&&transactionForMemberCenter.getStatus()!=StatusEnum.TRANSACTION_LOSE.getStatus()) {
				
				transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_TRANSFER.getType());
				String operaPrompt = "";
				TransferProject transferProject = transferProjectManager.selectByTransactionId(transactionForMemberCenter.getTransactionId());
				if(transferProject!=null){
					if(transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()
							){
						operaPrompt = "转让中";
						transactionForMemberCenter.setTransferId(transferProject.getId());
						transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_TRANSFER.getType());
						transactionForMemberCenter.setOperaPrompt(operaPrompt);
						return ;
					}
					if(transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus()||
							transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_REPAYMENT.getStatus()
							){
						operaPrompt = "已转让";
						transactionForMemberCenter.setTransferId(transferProject.getId());
						transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_TRANSFER.getType());
						transactionForMemberCenter.setOperaPrompt(operaPrompt);
						return ;
					}
					if(transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_LOSE.getStatus()
							){
						operaPrompt = "流标";
						transactionForMemberCenter.setTransferId(transferProject.getId());
						transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_TRANSFER.getType());
						transactionForMemberCenter.setOperaPrompt(operaPrompt);
						return ;
					}
				}
				
				if(	project.getPrepayment()==1){//提前还款
					operaPrompt = "当前不可转让";
					transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_CANNOT_TRANSFER.getType());
					transactionForMemberCenter.setOperaPrompt(operaPrompt);
					return ;
				}
				
				if(overdueLogManager.isOverDueUnrepayment(project.getId())){//是否逾期
					operaPrompt = "当前不可转让";
					transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_CANNOT_TRANSFER.getType());
					transactionForMemberCenter.setOperaPrompt(operaPrompt);
					return ;
				}
				Date lastEndDate = traList.get(0).getEndDate();
				Date firstStartDate = traList.get(0).getStartDate();
				Integer days = 0;
				for(TransactionInterest tra :traList){
					
					if(lastEndDate.getTime()<tra.getEndDate().getTime()){//遍历最大的还款时间，即为最后一期
						lastEndDate = tra.getEndDate();
					}
					if(firstStartDate.getTime()>tra.getStartDate().getTime()){//遍历最大的还款时间，即为最后一期
						firstStartDate = tra.getStartDate();
					}
					//当期期
					if(tra.getStartDate().getTime() <= DateUtils.getCurrentDate().getTime() 
							&& DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime() <= tra.getEndDate().getTime() ){
						days = DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), tra.getEndDate()) + 1  ;
					}
				}
				/*//距离最近一期还款日X天
				if(!(project.getTransferRecentRepayment() < days)&&days>0){
					operaPrompt = (project.getTransferRecentRepayment()-days + 1) + "天后可转让";
					transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_CANNOT_TRANSFER.getType());
					transactionForMemberCenter.setOperaPrompt(operaPrompt);
					return ;
				}*/
				//起息日后X个自然日可以转让
				int transferAfterInterest = DateUtils.daysOfTwo( firstStartDate , DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))+1;
				if(!(project.getTransferAfterInterest() < transferAfterInterest)){
					operaPrompt = (project.getTransferAfterInterest() - transferAfterInterest + 1) + "天后可转让";
					transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_CANNOT_TRANSFER.getType());
					transactionForMemberCenter.setOperaPrompt(operaPrompt);
					return ;
				}
				/*//距离最后一期还款日X天
				if(!(project.getTransferLastRepayment() < 
						(DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), lastEndDate)+1))){
					operaPrompt = "当前不可转让";
					transactionForMemberCenter.setOperaPrompt(operaPrompt);
					transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_CANNOT_TRANSFER.getType());
					return ;
				}*/
				transactionForMemberCenter.setOperaPrompt(operaPrompt);
			}else{
				transactionForMemberCenter.setOperaType(TypeEnum.TRANSACTION_OPERA_TYPE_NORMAL.getType());
				transactionForMemberCenter.setOperaPrompt("不可转让");
			}
		} catch (ManagerException e) {
			logger.error("交易详情，转让时间限制获取异常：" +project.getId(), e);
		}
	}
	
	private void p2pProjectCheck(TransactionForMemberDto transactionForMemberCenter, Project project) {
		// 项目是否已满额
		transactionForMemberCenter.setProjectStatus(project.getStatus());
		if (project.getSaleComplatedTime() != null) {
			transactionForMemberCenter.setSaleComplatedFlag(true);
		} else {
			transactionForMemberCenter.setSaleComplatedFlag(false);
		}
		// 是否为P2P项目
		if (project.isDirectProject()) {
			transactionForMemberCenter.setProfitPeriod(project.getProfitPeriod());
			transactionForMemberCenter.setInterestFrom(project.getInterestFrom());
			
		}
	}

	@Override
	public int getTransactionsTotalCount(Long memberId, int status) {
		try {
			return myTransactionManager.getTransactionsTotalCount(memberId, status);
		} catch (ManagerException e) {
			logger.error("查询用户的交易总笔数异常：" +memberId, e);
		}
		return 0;
	}

	@Override
	public Page<TransactionForMemberDto> queryTransactionListWithActivity(TransactionQuery query) {
		Page<TransactionForMemberDto> dto = queryTransactionListP2p(query);
		try {
			if (Collections3.isEmpty(dto.getData())) {
				return dto;
			}
			ActivityBiz activityBiz = new ActivityBiz();
			activityBiz.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
			activityBiz.setRuleType(ActivityConstant.ACTIVITY_RULE_TYPE_POPULARITY_RED_PACKAGE);
			List<ActivityBiz> actList = activityManager.findInProgressActivityByBizType(activityBiz);
			if (Collections3.isEmpty(actList)) {
				return dto;
			}
			// 同一时间只支持存在一种红包
			ResultDO<Object> rDO = new ResultDO<Object>();
			for (TransactionForMemberDto tran : dto.getData()) {
				getRedPackageStatus(tran);
			}
		} catch (Exception e) {
			logger.error("查询用户中心交易记录，TransactionQuery：" + query, e);
		}
		return dto;
	}

	/**
	 * 
	 * @Description:根据活动规则刷新红包状态
	 * @param rule
	 * @param tran
	 * @param rDO
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年3月18日 下午4:44:33
	 */
	private TransactionForMemberDto getRedPackageStatus(TransactionForMemberDto tran)
			throws Exception {
		// 找对应的redis中的红包信息缓存,匹配状态
		String info = RedisActivityClient.getRedBagInfo(tran.getTransactionId());
		if (StringUtil.isBlank(info)) {
			tran.setRedPackageStatus(StatusEnum.REDPACKAGE_STATUS_NULL.getStatus());
			return tran;
		} else if (info.endsWith(RedisConstant.REDIS_VALUE_ACTIVITY_REDBAG_WAITING)) {
			tran.setRedPackageStatus(StatusEnum.REDPACKAGE_STATUS_SEND.getStatus());
			return tran;
		} else if (info.endsWith(RedisConstant.REDIS_VALUE_ACTIVITY_REDBAG_EMPTY)) {
			tran.setRedPackageStatus(StatusEnum.REDPACKAGE_STATUS_EMPTY.getStatus());
			return tran;
		} else {
			tran.setRedPackageStatus(StatusEnum.REDPACKAGE_STATUS_NULL.getStatus());
			return tran;
		}
	}

	@Override
	public BigDecimal getMemberTotalInvestAmountDirectProject(Long memberId) {
		try {
			return myTransactionManager.getMemberTotalInvestAmountDirectProject(memberId);
		} catch (Exception e) {
			logger.error("通过用户ID查询M站累计投资本金过滤P2P项目,memberId= "+memberId, e);
		}
		return BigDecimal.ZERO;
	}
	/**
	 * 临时方法
	 */
	@Override
	public MemberTransactionCapital getMemberTransactionCapitalTemp(
			Long memberID) {
		try {
			MemberTransactionCapital memberTransactionCapital = new MemberTransactionCapital();
			TransactionQuery transactionQuery = new TransactionQuery();
			transactionQuery.setMemberId(memberID);
			//原有的方法
			List<Transaction> transactions = myTransactionManager.selectTransactionsByQueryParams(transactionQuery);
			
			//过滤p2p项目的新方法
			//List<Transaction> transactions = myTransactionManager.selectTransactionsByQueryParamsFilterP2p(transactionQuery);
			
			memberTransactionCapital.setMemberId(memberID);
			if(Collections3.isNotEmpty(transactions)) {
//				int finishedInvestNum = 0;
//				int subsistingInvestNum = 0;
				BigDecimal finishedInvestTotal = BigDecimal.ZERO;
				BigDecimal subsistingInvestTotal = BigDecimal.ZERO;
				for (Transaction transaction : transactions) {
					// 已完结投资
					if (StatusEnum.TRANSACTION_COMPLETE.getStatus() == transaction.getStatus()) {
//						finishedInvestNum++;
						finishedInvestTotal = finishedInvestTotal.add(transaction.getInvestAmount());
					}
					// 存续期投资
					if (StatusEnum.TRANSACTION_REPAYMENT.getStatus() == transaction.getStatus()) {
//						subsistingInvestNum++;
						subsistingInvestTotal = subsistingInvestTotal.add(transaction.getInvestAmount());
					}
				}
//				memberTransactionCapital.setFinishedInvestNum(finishedInvestNum);
//				memberTransactionCapital.setSubsistingInvestNum(subsistingInvestNum);
				memberTransactionCapital.setFinishedInvestTotal(finishedInvestTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setSubsistingInvestTotal(subsistingInvestTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setMemberId(memberID);
			//原有的方法注
			List<TransactionInterest> transactionInterests = transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			//过滤p2p项目的新方法
			//List<TransactionInterest> transactionInterests = transactionInterestManager.selectTransactionInterestsByQueryParamsFilterP2p(transactionInterestQuery);
			if(Collections3.isNotEmpty(transactionInterests)) {
				BigDecimal receivableInterest = BigDecimal.ZERO;
				BigDecimal receivablePrincipal = BigDecimal.ZERO;
				BigDecimal receivedInterest = BigDecimal.ZERO;
				BigDecimal receivedPrincipal = BigDecimal.ZERO;
				 /**待收利息笔数**/
			    int receivableInterestNum = 0;
			    /**已收利息笔数**/
			    int receivedInterestNum = 0;
			    /** 已收本金笔数 **/
				int finishedInvestNum = 0;
				/** 代收本金笔数 **/
				int subsistingInvestNum = 0;
				for (TransactionInterest transactionInterest : transactionInterests) {
					//未付利息和本金
					if(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus()==transactionInterest.getStatus()
							||StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus()==transactionInterest.getStatus()) {
						receivableInterest = receivableInterest.add(transactionInterest.getRealPayInterest());
						receivablePrincipal = receivablePrincipal.add(transactionInterest.getRealPayPrincipal());
						if (transactionInterest.getRealPayPrincipal().compareTo(BigDecimal.ZERO) > 0) {
							subsistingInvestNum += 1;
						}
						receivableInterestNum +=1;
					}
					//已付利息和本金
					if(StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus()==transactionInterest.getStatus()
							|| StatusEnum.TRANSACTION_INTEREST_PART_PAYED.getStatus()==transactionInterest.getStatus()) {
						receivedInterest = receivedInterest.add(transactionInterest.getRealPayInterest());
						receivedPrincipal = receivedPrincipal.add(transactionInterest.getRealPayPrincipal());
						if(transactionInterest.getRealPayPrincipal().compareTo(BigDecimal.ZERO)>0){
							finishedInvestNum += 1;
						}
						receivedInterestNum +=1;
					}
				}
				memberTransactionCapital.setFinishedInvestNum(finishedInvestNum);
				memberTransactionCapital.setSubsistingInvestNum(subsistingInvestNum);
				memberTransactionCapital.setReceivableInterest(receivableInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivablePrincipal(receivablePrincipal.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivedInterest(receivedInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivedPrincipal(receivedPrincipal.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivableInterestNum(receivableInterestNum);
				memberTransactionCapital.setReceivedInterestNum(receivedInterestNum);
			}
			return memberTransactionCapital;
		} catch (Exception e) {
			logger.error("查询会员投资资金信息出错，memberID=" + memberID, e);
		}
		return null;
	}

	@Override
	public Page<TransactionForMemberDto> queryTransactionListWithP2P(TransactionQuery query) {
		Page<TransactionForMemberDto> dto = new Page<TransactionForMemberDto>();
		try {
			Page<TransactionForMemberCenter> page = myTransactionManager.selectAllTransactionWithP2P(query);
			List<TransactionForMemberDto> dataList = BeanCopyUtil.mapList(page.getData(), TransactionForMemberDto.class);
			int unSignContracts = myTransactionManager.getUnsignContractNum(query.getMemberId());
			for (TransactionForMemberDto transactionForMemberDto : dataList) {
				Project project = projectManager.selectByPrimaryKey(transactionForMemberDto.getProjectId());
				transactionForMemberDto.setInvestType(project.getInvestType());
				transactionForMemberDto.setThumbnail(project.getThumbnail());
				this.p2pProjectCheck(transactionForMemberDto, project);
				this.addDiffInformation(transactionForMemberDto, project);
				transactionForMemberDto.setUnSignContracts(unSignContracts);
			}
			dto.setData(dataList);
			dto.setiDisplayLength(page.getiDisplayLength());
			dto.setiDisplayStart(page.getiDisplayStart());
			dto.setiTotalRecords(page.getiTotalRecords());
			dto.setPageNo(page.getPageNo());
		} catch (Exception e) {
			logger.error("查询我的交易和我的募集中列表失败，queryStatus={}, TransactionQuery={}", query.getQueryStatus(), query, e);
		}
		return dto;
	}
	
	private void addDiffInformation(TransactionForMemberDto transactionForMemberDto,Project project) throws ManagerException{
		transactionForMemberDto.setFlag(0);
		
		if(transactionForMemberDto.getProjectCategory()==2){
			TransferProject transferProject = transferProjectManager.selectByPrimaryKey(transactionForMemberDto.getTransferId());
			String remainingTime = DateUtils.betweenTwoDays(DateUtils.getCurrentDate(), transferProject.getTransferEndDate());
			transactionForMemberDto.setRemainingTime(remainingTime);
			
			BigDecimal balance = transferProjectManager.getTransferProjectBalanceById(transferProject.getId());
			String progress = getProjectNumberProgress(transferProject.getTransactionAmount(), balance);
			transactionForMemberDto.setProgress(progress);
			transactionForMemberDto.setProjectStatus(transferProject.getStatus());
			
			if(1==transactionForMemberDto.getStatus()||4==transactionForMemberDto.getStatus()){
				//TODO   履约中   下一期还款金额   还款时间      如果逾期  逾期期数 ，滞纳金
				
				OverdueRepayLog overdueRepayLog = overdueRepayLogManager.getOverdueRepayByStatus(transactionForMemberDto.getProjectId());
				if(overdueRepayLog==null){// 未逾期正常显示
					TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
					transactionInterestQuery.setMemberId(transactionForMemberDto.getMemberId());
					transactionInterestQuery.setTransactionId(transactionForMemberDto.getTransactionId());
					transactionInterestQuery.setNoStatus(1);
					List<TransactionInterest>  transactionList= transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
					if(Collections3.isNotEmpty(transactionList)){
						transactionForMemberDto.setNextEndDate(transactionList.get(0).getEndDate());
						transactionForMemberDto.setRepayPrincipal(transactionList.get(0).getRealPayInterest().add(transactionList.get(0).getRealPayPrincipal()));
					}
				}else{//逾期显示 逾期期数和滞纳金
					String interestPeriods = overdueRepayLog.getInterestPeriods();
					String [] periods = interestPeriods.split(",");
					String overPeriod = "";
					for(int i = 0; i < periods.length; i++)
					{
						String period = periods[i];
						if(i==0){
							overPeriod = period.split("/")[0];
						}else{
							overPeriod = overPeriod + ","  + period.split("/")[0];
						}
						
					}
					transactionForMemberDto.setOverPeriod(overPeriod);
					BigDecimal overdueFine = BigDecimal.ZERO;
					TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
					transactionInterestQuery.setTransactionId(transactionForMemberDto.getTransactionId());
					List<TransactionInterest> transactionInterList = transactionInterestManager.selectTransactionInterestsByQueryParamsAndOverdue(transactionInterestQuery);
					for(TransactionInterest tran:transactionInterList){
						if(StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus()!=tran.getStatus()){
							overdueFine = overdueFine.add(tran.getOverdueFine());
						}
					}
					overdueFine = overdueFine.setScale(2, BigDecimal.ROUND_HALF_UP);
					transactionForMemberDto.setOverdueFine(overdueFine);
					transactionForMemberDto.setFlag(2);
				}
			}
			if(2==transactionForMemberDto.getStatus()){
				Map<String,Object> map = Maps.newHashMap();
				map.put("projectId", transactionForMemberDto.getProjectId());
				map.put("transactionId", transactionForMemberDto.getTransactionId());
				map.put("memberId", transactionForMemberDto.getMemberId());
				TransactionInterest transactionInterest =transactionInterestManager.getPreTransactionInterest(map);
				transactionForMemberDto.setPayDate(transactionInterest.getPayTime());
				transactionForMemberDto.setTotalInterest(transactionForMemberDto.getReceivedInterest());
				//   已还款   还款日期          考虑提前还款
				if(1==project.getPrepayment()){
					transactionForMemberDto.setFlag(1);
					transactionForMemberDto.setPayDate(transactionInterest.getTopayDate());
				}
			}
			
		}else{
			transactionForMemberDto.setTransferFlag(0);//0：没有转让，1：部分转让，2：全部转让
			
			//债权
			if(!project.isDirectProject()){
				
				if(1==transactionForMemberDto.getStatus()||4==transactionForMemberDto.getStatus()){
					TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
					transactionInterestQuery.setMemberId(transactionForMemberDto.getMemberId());
					transactionInterestQuery.setTransactionId(transactionForMemberDto.getTransactionId());
					transactionInterestQuery.setNoStatus(1);
					List<TransactionInterest>  transactionList= transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
					if(Collections3.isNotEmpty(transactionList)){
						transactionForMemberDto.setNextEndDate(transactionList.get(0).getEndDate());
						transactionForMemberDto.setRepayPrincipal(transactionList.get(0).getRealPayInterest().add(transactionList.get(0).getRealPayPrincipal()));
					}
				}
				if(2==transactionForMemberDto.getStatus()){
					//   已还款   还款日期          考虑提前还款
					Map<String,Object> map = Maps.newHashMap();
					map.put("projectId", transactionForMemberDto.getProjectId());
					map.put("transactionId", transactionForMemberDto.getTransactionId());
					map.put("memberId", transactionForMemberDto.getMemberId());
					TransactionInterest transactionInterest =transactionInterestManager.getPreTransactionInterest(map);
					transactionForMemberDto.setPayDate(transactionInterest.getPayTime());
					transactionForMemberDto.setTotalInterest(transactionForMemberDto.getReceivedInterest());
				}
				/*if(3==transactionForMemberDto.getStatus()){
					//TODO   流标
				}*/
				return ;
			}
			if(StatusEnum.TRANSACTION_TRANSFER_STATUS_ALL_TRANSFERED.getStatus()==transactionForMemberDto.getTransferStatus()){
				TransferProject transferProject = transferProjectManager.selectByTransactionId(transactionForMemberDto.getTransactionId());
				transactionForMemberDto.setPayDate(transferProject.getTransferSaleComplatedTime());
				transactionForMemberDto.setTransferFlag(2);//0：没有转让，1：部分转让，2：全部转让
			}
			
			if(StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus()==transactionForMemberDto.getTransferStatus()){
				transactionForMemberDto.setTransferFlag(1);//0：没有转让，1：部分转让，2：全部转让
			}
			
			if(StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus()==transactionForMemberDto.getTransferStatus()
					){//转让中，通过是否有转让出去金额判断状态   
				Balance balance = balanceManager.queryBalance(transactionForMemberDto.getTransactionId(), TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT);
				if(balance!=null&&balance.getAvailableBalance().compareTo(transactionForMemberDto.getInvestAmount())!=0){
					transactionForMemberDto.setTransferFlag(1);//0：没有转让，1：部分转让，2：全部转让
				}
			}
			
			
			if("募集中".equals(transactionForMemberDto.getDirectProjectStatus())||"募集暂停".equals(transactionForMemberDto.getDirectProjectStatus())){
				//TODO   剩余时间  募集进度
				String remainingTime = DateUtils.betweenTwoDays(DateUtils.getCurrentDate(), project.getSaleEndTime());
				transactionForMemberDto.setRemainingTime(remainingTime);
				BigDecimal balance = getProjectBalanceById(project.getId());
				String progress = getProjectNumberProgress(project.getTotalAmount(), balance);
				transactionForMemberDto.setProgress(progress);
			}
			if("已投满".equals(transactionForMemberDto.getDirectProjectStatus())){
				//   募集用时
				String remainingTime = DateUtils.betweenTwoDays(project.getOnlineTime(), project.getSaleComplatedTime());
				transactionForMemberDto.setRemainingTime(remainingTime);
			}
			/*if("募集暂停".equals(transactionForMemberDto.getDirectProjectStatus())){
				//TODO  同 募集中
			}*/
			/*if("已截止".equals(transactionForMemberDto.getDirectProjectStatus())){
				//TODO 
			}*/
			if(1==transactionForMemberDto.getStatus()||4==transactionForMemberDto.getStatus()){
				//TODO   履约中   下一期还款金额   还款时间      如果逾期  逾期期数 ，滞纳金
				
				OverdueRepayLog overdueRepayLog = overdueRepayLogManager.getOverdueRepayByStatus(transactionForMemberDto.getProjectId());
				if(overdueRepayLog==null){// 未逾期正常显示
					TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
					transactionInterestQuery.setMemberId(transactionForMemberDto.getMemberId());
					transactionInterestQuery.setTransactionId(transactionForMemberDto.getTransactionId());
					transactionInterestQuery.setNoStatus(1);
					List<TransactionInterest>  transactionList= transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
					if(Collections3.isNotEmpty(transactionList)){
						transactionForMemberDto.setNextEndDate(transactionList.get(0).getEndDate());
						transactionForMemberDto.setRepayPrincipal(transactionList.get(0).getRealPayInterest().add(transactionList.get(0).getRealPayPrincipal()));
					}
				}else{//逾期显示 逾期期数和滞纳金
					String interestPeriods = overdueRepayLog.getInterestPeriods();
					String [] periods = interestPeriods.split(",");
					String overPeriod = "";
					for(int i = 0; i < periods.length; i++)
					{
						String period = periods[i];
						if(i==0){
							overPeriod = period.split("/")[0];
						}else{
							overPeriod = overPeriod + ","  + period.split("/")[0];
						}
						
					}
					transactionForMemberDto.setOverPeriod(overPeriod);
					BigDecimal overdueFine = BigDecimal.ZERO;
					TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
					transactionInterestQuery.setTransactionId(transactionForMemberDto.getTransactionId());
					List<TransactionInterest> transactionInterList = transactionInterestManager.selectTransactionInterestsByQueryParamsAndOverdue(transactionInterestQuery);
					for(TransactionInterest tran:transactionInterList){
						if(StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus()!=tran.getStatus()){
							overdueFine = overdueFine.add(tran.getOverdueFine());
						}
					}
					overdueFine = overdueFine.setScale(2, BigDecimal.ROUND_HALF_UP);
					transactionForMemberDto.setOverdueFine(overdueFine);
					transactionForMemberDto.setFlag(2);
				}
			}
			if(2==transactionForMemberDto.getStatus()){
				//   已还款   还款日期          考虑提前还款
				Map<String,Object> map = Maps.newHashMap();
				map.put("projectId", transactionForMemberDto.getProjectId());
				map.put("transactionId", transactionForMemberDto.getTransactionId());
				map.put("memberId", transactionForMemberDto.getMemberId());
				TransactionInterest transactionInterest =transactionInterestManager.getPreTransactionInterest(map);
				transactionForMemberDto.setPayDate(transactionInterest.getPayTime());
				transactionForMemberDto.setTotalInterest(transactionForMemberDto.getReceivedInterest());
				//   已还款   还款日期          考虑提前还款
				if(1==project.getPrepayment()){
					transactionForMemberDto.setFlag(1);
					transactionForMemberDto.setPayDate(transactionInterest.getTopayDate());
				}
			}
		}
		
		/*if(3==transactionForMemberDto.getStatus()){
			//TODO   流标
		}*/
		
	}
	
	/**
	 * 项目进度
	 * @param totalAmount
	 * @param availableBalance
	 * @return
	 */
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
	
	@Override
	public ResultDTO<ContractSignDto> queryUnsignList(TransactionQuery query) {
		ResultDTO<ContractSignDto> dto = new ResultDTO<ContractSignDto>();
		try {
			ContractSignDto result = new ContractSignDto();
			Page<UnSignContractDto> unSingList = new Page<UnSignContractDto>();
			Page<Transaction> page = myTransactionManager.queryUnsignList(query);
			List<UnSignContractDto> dataList = BeanCopyUtil.mapList(page.getData(), UnSignContractDto.class);
			for(UnSignContractDto unSign : dataList){
				Project project = projectManager.selectByPrimaryKey(unSign.getProjectId());
				unSign.setProjectName(project.getName());
				unSign.setThumbnail(project.getThumbnail());
			}
			unSingList.setData(dataList);
			unSingList.setiDisplayLength(page.getiDisplayLength());
			unSingList.setiDisplayStart(page.getiDisplayStart());
			unSingList.setiTotalRecords(page.getiTotalRecords());
			unSingList.setPageNo(page.getPageNo());
			
			Member member = memberManager.selectByPrimaryKey(query.getMemberId());
			result.setSignWay(member.getSignWay());
			result.setUnSingList(unSingList);
			dto.setResult(result);
		} catch (Exception e) {
			logger.error("查询未签署合同列表失败，TransactionQuery={}",  query, e);
		}
		return dto;
	}
	
	@Override
	public ResultDTO<Object> signAllContract(Long memberId) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			int num = myTransactionManager.signAllContract(memberId);
			if(num<1){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setIsError();
				return result;
			}
			result.setResult(num);
			result.setIsSuccess();
		} catch (Exception e) {
			logger.error("一键签署异常,memberId= " + memberId, e);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}
	
	@Override
	public ResultDTO<Object> signContract(Long transactionId,Integer source) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			ResultDO<Object> queryContractInfoResult = myTransactionManager.queryContractInfo(transactionId);
			if(queryContractInfoResult.isSuccess()&&Integer.valueOf(queryContractInfoResult.getResult().toString())>0){
				result.setResultCode(ResultCode.CONTRACT_SIGN_ALREADY_SIGN);
				result.setIsError();
				return result;
			}
			
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(transaction.getSignStatus()==StatusEnum.CONTRACT_STATUS_INIT.getStatus()){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setIsError();
				return result;
			}
			
			if(transaction.getSignStatus()==StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus()){
				result.setResultCode(ResultCode.CONTRACT_SIGN_ALREADY_SIGN);
				result.setIsError();
				return result;
			} 
			
			if(transaction.getSignStatus()==StatusEnum.CONTRACT_STATUS_EXPIRED.getStatus()){
				result.setResultCode(ResultCode.CONTRACT_SIGN_EXPIRED);
				result.setIsError();
				return result;
			} 
			
			String signUrl = myTransactionManager.signContract(transactionId,StatusEnum.CONTRACT_SIGN_WAY_MOBILE.getStatus(),source);
			if(StringUtil.isBlank(signUrl)){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setIsError();
				return result;
			}
			result.setResult(signUrl);
		} catch (Exception e) {
			logger.error("手动签署异常,transactionId =" + transactionId, e);
		}
		return result;
	}
	
	@Override
	public ResultDTO<Object> signContractAuto(Long memberId,
			Long transactionId) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(!transaction.getMemberId().equals(memberId)){
				result.setResultCode(ResultCode.CONTRACT_SIGN_MEMBER_NOT_MATCH);
				result.setIsError();
				return result;
			}
			
			int num =  myTransactionManager.signContractAuto(transactionId);
			if(num<1){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setIsError();
				return result;
			}
			
			//乙方签署成功，更改交易状态
			Transaction tra = new Transaction();
			tra.setId(transactionId);
			tra.setSignStatus(StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus());
			try {
				myTransactionManager.updateByPrimaryKeySelective(tra);
			} catch (ManagerException e) {
				logger.error("自动签名：更新交易表合同签署状态异常,transactionId={}" , transactionId,e);
			}
			
			result.setResult(num);
			result.setIsSuccess();
		} catch (Exception e) {
			logger.error("自动签署异常,transactionId =" + transactionId, e);
		}
		return result;
	}
	
	
	public void afterTransactionContractIsFullCoupon(Order order){
		myTransactionManager.afterTransactionContractIsFullCoupon(order);
	}
	
	
	@Override
	public ResultDTO<TradeBiz> isRepeatToCashDest(Order order, String payerIp) {
		ResultDTO<TradeBiz> resultDTO = new ResultDTO<TradeBiz>();
		try {
			ResultDO<TradeBiz> resultDO = tradeManager.isRepeatToCashDest(order, payerIp);
			
			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			if(project.isDirectProject()){
				resultDTO.setResultCode(ResultCode.ORDER_SINA_UNWITHHOLD_NOT_PAY_ERROR);
				return resultDTO;
			}
			
			TradeBiz tra = new TradeBiz();
			tra = resultDO.getResult();
			String  redirectUrl =this.getReturnUrlByForm(tra.getRedirectUrl());
			logger.info("未开通委托且余额足够，第二次获取：返回支付重定向链接："+redirectUrl+",orderNo="+order.getOrderNo());
			tra.setRedirectUrl(redirectUrl);
			resultDTO.setResult(tra);
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
				resultDTO.setResultCode(resultDO.getResultCode());
			}
			return resultDTO;
		} catch (Exception e) {
			logger.error("非委托支付获取支付链接, orderNo={}", order.getOrderNo(), e);
			resultDTO = new ResultDTO<TradeBiz>();
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			return resultDTO;
		}
	}
	
	@Override
	public Page<TransactionForProjectDto> selectTransferTransactionForProjectsForPage(TransactionQuery transactionQuery) {
		try {
			Long projectId = transactionQuery.getProjectId();
			TransferProject transferProject = transferProjectManager.selectByPrimaryKey(projectId);
			Project project = projectManager.selectByPrimaryKey(transferProject.getProjectId());
			transactionQuery.setProjectId(project.getId());
			//转让项目交易类型
			transactionQuery.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
			transactionQuery.setTransferId(transferProject.getId());
			Page<TransactionForProject>	transactionsPage = myTransactionManager.selectTransactionForProjectsForPage(transactionQuery);
			
			Page<TransactionForProjectDto> pageList = new Page<TransactionForProjectDto>();
			List<TransactionForProjectDto> list = Lists.newArrayList();
			if(Collections3.isNotEmpty(transactionsPage.getData())){
				list = BeanCopyUtil.mapList(transactionsPage.getData(), TransactionForProjectDto.class);
			}
			pageList.setData(list);
			pageList.setiDisplayLength(transactionsPage.getiDisplayLength());
			pageList.setiDisplayStart(transactionsPage.getiDisplayStart());
			pageList.setiTotalRecords(transactionsPage.getiTotalRecords());
			pageList.setPageNo(transactionsPage.getPageNo());
			return pageList;
		} catch (Exception e) {
			logger.error("通过项目id分页查询交易记录，transactionQuery=" + transactionQuery, e);
		}
		return null;
	}
	
	
	public ResultDTO<Object> transferList(TransferRecordQuery query){
		ResultDTO<Object> result = new ResultDTO<Object>();
		Page<TransferRecordBiz> transferList = myTransactionManager.transferList(query);
		result.setResult(transferList);
		return result;
	}
	
	public ResultDTO<Object> transactionTransferList(TransferRecordQuery query){
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			TransactionTransferRecordBiz transactionTransferRecordBiz = myTransactionManager.transactionTransferList(query);
			result.setResult(transactionTransferRecordBiz);
		} catch (ManagerException e) {
			logger.error("获取交易相关转让记录异常, memberId={},transactionId={}", query.getMemberId(),query.getTransactionId(), e);
		}
		return result;
	}
	
	
	
	@Override
	public ResultDTO<Object> transferToProject(Long memberId,Long transactionId,BigDecimal transferAmount) {
		ResultDTO<Object> rDTO = null;
		rDTO = this.checktransferToProject(memberId, transactionId, transferAmount);
		if (!rDTO.isSuccess()) {
			return rDTO;
		}
		try {
			ResultDO<Object> rDO = transferProjectManager.transferToProject(memberId, transactionId,  transferAmount);
			rDTO.setResult(rDO.getResult());
			if(rDO.isSuccess()){
				rDTO.setIsSuccess();
			}else if(rDO.isError()){
				rDTO.setIsError();
			}
			return rDTO;
		} catch (Exception e) {
			logger.error("发起转让异常, memberId={},transactionId={},discount={},transferAmount={}", memberId,
					transactionId,transferAmount, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDTO;
		}
		
	}
	
	
	private ResultDTO<Object> checktransferToProject(Long memberId,Long transactionId,BigDecimal transferAmount) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			rDTO = this.ableToTransfer(memberId, transactionId);//交易是否可转让
			if(!rDTO.isSuccess()){
				return rDTO;
			}
			//转让价格小于等于剩余未还本金
			BigDecimal residualPrincipal = transferProjectManager.getResidualPrincipal(transactionId);
			if(residualPrincipal.compareTo(transferAmount)==-1){
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_TRANSFERAMOUNT_INVALID);
				return rDTO;
			}
			
			/*if(residualPrincipal.compareTo((transferAmount.add(discount)))!=0){
				//外部传来的转让价格不等于剩余未还本金-折价
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_TRANSFERAMOUNT_ERROR);
				return rDTO;
			}*/
			
			rDTO.setIsSuccess();
		} catch (Exception e) {
			logger.error("发起转让校验异常, memberId={},transactionId={},transferAmount={}", memberId,
					transactionId,transferAmount, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDTO;
		}
		return rDTO;
	}
	
	@Override
	public ResultDTO<Object> transferInformation(Long memberId,Long transactionId) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		TransferInformation tran = new TransferInformation();
		BigDecimal residualPrincipal = BigDecimal.ZERO;//剩余本金
		BigDecimal residualInterest = BigDecimal.ZERO;//剩余收益
		try {
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(transaction==null){
				result.setResultCode(ResultCode.ERROR_SYSTEM);
				return result;
			}
			if (memberId.compareTo(transaction.getMemberId()) != 0) {
				result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
				return result;
			}

			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			tran.setInvestType(project.getInvestType());
			tran.setTransactionId(transactionId);
			
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transactionId);
			List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			for(TransactionInterest tra :traList){
				if(tra.getEndDate().getTime()< DateUtils.getCurrentDate().getTime() ){
					continue;
				}
				if(tra.getEndDate().getTime() == DateUtils.getCurrentDate().getTime()){
					continue;
				}
				if(DateUtils.getCurrentDate().getTime() < tra.getEndDate().getTime() ){
					residualPrincipal = residualPrincipal.add(tra.getRealPayPrincipal());
					residualInterest = residualInterest.add(tra.getRealPayInterest());//接盘人，享受转让人的收益券收益
				}
				
			}
			tran.setHoldDays(DateUtils.daysBetween(traList.get(0).getStartDate(), DateUtils.getCurrentDate())+1);
			tran.setRemainingTime(DateUtils.daysBetween(DateUtils.getCurrentDate(),traList.get(traList.size()-1).getEndDate())+1);//今天算一天
			tran.setResidualPrincipal(residualPrincipal);
			tran.setResidualInterest(residualInterest);
			
			tran.setTransferTime(Integer.valueOf(SysServiceUtils.getDictValue("transfer_group", "raise_time","72")));
			tran.setTransferRate(myTransactionManager.getTransferRate(transactionId)!=null?myTransactionManager.getTransferRate(transactionId):new BigDecimal(2));
			tran.setTransferRateList(this.getTransferRate());
			result.setResult(tran);
			
		} catch (ManagerException e) {
			logger.error("获取转让信息异常, memberId={},transactionId={}", memberId,transactionId,e);
		}
		
		return result;
	}
	
	private List<TransferRateList> getTransferRate(){

		List<TransferRateList> transferRateList  = Lists.newArrayList();
		try {
			transferRateList= JSON.parseObject(SysServiceUtils.getDictValue("Rate", "transferRate_group",""),new TypeReference<ArrayList<TransferRateList>>(){}) ;
		} catch (Exception e) {
			return transferRateList;
		}
		return transferRateList;
		
	}
	
	@Override
	public ResultDTO<Object> cancelTransferProject(Long memberId,Long transactionId) {
		ResultDTO<Object> rDTO = null;
		rDTO = this.checkCancleTransferToProject(memberId, transactionId);
		if (!rDTO.isSuccess()) {
			return rDTO;
		}
		try {
			ResultDO<Object> rDO = transferProjectManager.cancelTransferProject(transactionId);
			rDTO.setResult(rDO.getResult());
			rDTO.setResultCode(rDO.getResultCode());
			if(rDO.isSuccess()){
				rDTO.setIsSuccess();
			}else if(rDO.isError()){
				rDTO.setIsError();
			}
			return rDTO;
		} catch (Exception e) {
			logger.error("撤销转让异常, memberId={},transactionId={}", memberId,
					transactionId, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDTO;
		}
		
	}
	
	private ResultDTO<Object> checkCancleTransferToProject(Long memberId,Long transactionId) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(transaction==null){
				rDTO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return rDTO;
			}
			if(memberId.compareTo(transaction.getMemberId()) != 0){
				rDTO.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
				return rDTO;
			}
			rDTO.setIsSuccess();
		} catch (Exception e) {
			logger.error("撤销转让校验异常, memberId={},transactionId={}", memberId,
					transactionId, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDTO;
		}
		return rDTO;
	}
	
	
	@Override
	public ResultDTO<TransferContractBiz> viewZtTransferContract(Long orderId,Long memberId) {
			ResultDTO<TransferContractBiz> result = new ResultDTO<TransferContractBiz>();
			try {
				ResultDO<TransferContractBiz> resultDO = myTransactionManager.viewZtTransferContract(orderId, memberId);
				result.setResult(resultDO.getResult());
				return result;
			} catch (Exception e) {
				result.setIsError();
				logger.error("查看转让直投合同发生异常，orderId：" + orderId, e);
			}
			return result;
		}
	
	@Override
	public ResultDTO<TransferContractBiz> transferContract(Long orderId,Long memberId) {
			ResultDTO<TransferContractBiz> result = new ResultDTO<TransferContractBiz>();
			try {
				ResultDO<TransferContractBiz> resultDO = myTransactionManager.transferContract(orderId, memberId);
				result.setResult(resultDO.getResult());
				return result;
			} catch (Exception e) {
				result.setIsError();
				logger.error("查看转让债权合同发生异常，orderId：" + orderId, e);
			}
			return result;
		}


	public ResultDTO<Object> ableToTransfer(Long memberId,Long transactionId) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(transaction==null){
				rDTO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return rDTO;
			}
			if(memberId.compareTo(transaction.getMemberId()) != 0){
				rDTO.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
				return rDTO;
			}
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if(project==null){
				rDTO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
				return rDTO;
			}
			if(project.getTransferFlag() == null ||  project.getTransferFlag() != 1){//TODO 转让标记 枚举
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER);
				return rDTO;
			}
			if(project.getPrepayment()==1){//标记提前还款的项目不能进行转让
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER_EARLY_REPAYMENT);
				return rDTO;
			}
			if( (project.isDirectProject() && project.getStatus()!=StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()
					&&project.getStatus()!=StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus())
					){//非履约中项目，不能转让|| ( project.getStatus()!=StatusEnum.PROJECT_STATUS_FULL.getStatus() && !project.isDirectProject())
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER);
				return rDTO;
			}
			if(overdueLogManager.isOverDueUnrepayment(project.getId())){//普通逾期未还，即不可转让
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER_OVER);
				return rDTO;
			}
			if(transaction.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){//二次转让
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_TRANSACTION_REPAYMENTING);
				return rDTO;
			}
			if(transaction.getStatus()!=StatusEnum.TRANSACTION_REPAYMENT.getStatus()){//不是回款中，不可以进行转让
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_TRANSACTION_REPAYMENTING);
				return rDTO;
			}
			if(transaction.getTransferStatus()!=StatusEnum.TRANSACTION_TRANSFER_STATUS_NO.getStatus()&&
					transaction.getTransferStatus()!=StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus()){//不是未转让且部分转让，不可以进行转让
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_TRANSACTION_REPAYMENTING);
				return rDTO;
			}
			if(transaction.getCanTransferDate()==null||transaction.getExtraInterestDay()>0){//高额加息券该部分值不写入，高额加息券不可转让
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER_HIGH_EXTRA);
				return rDTO;
			}
			//交易是否处于可转让期（项目可转让期）
			if(transaction.getCanTransferDate()!=null){
				if(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime()<transaction.getCanTransferDate().getTime()){
					String message =  "距离可转让还有" + (DateUtils.daysBetween( DateUtils.getCurrentDate() , DateUtils.formatDate(transaction.getCanTransferDate(), DateUtils.DATE_FMT_3)))
							+ "天";
					ResultCode.CUSTOM.setMsg(message);
					rDTO.setResultCode(ResultCode.CUSTOM);
					return rDTO;
				}
			}
			
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transactionId);
			List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			Date lastEndDate = traList.get(0).getEndDate();
			Date firstStartDate = traList.get(0).getStartDate();
			Integer days = 0;
			for(TransactionInterest tra :traList){
				if(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime() ==tra.getEndDate().getTime()
						){
					rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER_REPAY);
					return rDTO;
				}
				
				/*if(lastEndDate.getTime()<tra.getEndDate().getTime()){//遍历最大的还款时间，即为最后一期
					lastEndDate = tra.getEndDate();
				}
				if(firstStartDate.getTime()>tra.getStartDate().getTime()){//遍历最小的起始时间，即为第一期
					firstStartDate = tra.getStartDate();
				}
				//当期期
				if(tra.getStartDate().getTime() < DateUtils.getCurrentDate().getTime() 
						&& DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime() <= tra.getEndDate().getTime() ){
					days = DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), tra.getEndDate()) + 1  ;
				}*/
			}
		
			/*//距离最近一期还款日X天
			if(!(project.getTransferRecentRepayment() < days) && days>0){
				String message =  "该项目" + (project.getTransferRecentRepayment()-days + 1) + "天后可转让";
				ResultCode.CUSTOM.setMsg(message);
				rDTO.setResultCode(ResultCode.CUSTOM);
				return rDTO;
			}*/
			/*//距离最后一期还款日X天
			if(!(project.getTransferLastRepayment() < 
					(DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), lastEndDate)+1))){
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER);
				return rDTO;
			}*/
			//起息日后X个自然日可以转让
			/*int transferAfterInterest = DateUtils.daysOfTwo( firstStartDate , DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))+1;
			if(!(project.getTransferAfterInterest() < 
					(DateUtils.daysOfTwo( firstStartDate , DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))+1))){
				String message =  "距离可转让还有" + (project.getTransferAfterInterest() - transferAfterInterest+ 1) + "天";
				ResultCode.CUSTOM.setMsg(message);
				rDTO.setResultCode(ResultCode.CUSTOM);
				return rDTO;
			}*/
			
			//今天是否二次转让
			TransferProject isAlreadytransferProject=  transferProjectManager.selectByTransactionIdToday(transactionId);
			
			if(isAlreadytransferProject!=null){
				rDTO.setResultCode(ResultCode.TRANSFER_PROJECT_ALREADY_TRANSFER);
				return 	rDTO;
			}
			
			rDTO.setIsSuccess();
		} catch (Exception e) {
			logger.error("是否可以发起转让校验异常, memberId={},transactionId={},discount={},transferAmount={}", memberId,
					transactionId, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDTO;
		}
		return rDTO;
	}

	@Override
	public int queryMemberTransactionCount(Long memberid, Date starttime, Date endtime) {
		return myTransactionManager.queryMemberTransactionCount(memberid,starttime,endtime);
	}
}