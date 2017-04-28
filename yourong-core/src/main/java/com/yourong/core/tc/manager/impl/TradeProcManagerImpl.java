package com.yourong.core.tc.manager.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.SinaCashDeskClient;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.PayPasswordDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TradeProcManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.TradeBiz;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.uc.manager.MemberManager;

@Service
public class TradeProcManagerImpl implements TradeProcManager {

	@Autowired
	private OrderManager orderManager;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private CouponManager couponManager;

	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;

	@Autowired
	private ActivityAfterTransactionManager activityAfterTransactionManager;

	@Autowired
	private SinaPayClient sinaPayClient;

	@Autowired
	private SinaCashDeskClient sinaCashDeskClient;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Autowired
	private TransactionInterestManager transactionInterestManager;

	private Logger logger = LoggerFactory.getLogger(TradeProcManagerImpl.class);
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<TradeBiz> createTransactionHostingTradeLocal(Order order, int sourceType, String payerIp, boolean isWithholdAuthority)
			throws Exception {
		ResultDO<TradeBiz> result = new ResultDO<TradeBiz>();
		TradeBiz biz = new TradeBiz();
		biz.setIsWithholdAuthority(isWithholdAuthority);
		result.setResult(biz);
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			//首先判断订单如果已经是支付中了，就直接返回，无需再创建代收交易
			Order orderForLock = orderManager.getOrderByIdForLock(order.getId());
			if (orderForLock.getStatus() != StatusEnum.ORDER_WAIT_PAY.getStatus()) {
				result.setResultCode(ResultCode.ORDER_FRONT_ORDER_YET_PAYING);
				logger.info("交易前置条件验证失败：" + ResultCode.ORDER_FRONT_ORDER_YET_PAYING.getMsg() + ", orderNo="
						+ order.getOrderNo());
				return result;

			}
			// 创建交易前的校验
			createTransactionPreValidate(order, sourceType, result, isWithholdAuthority);
			if (TypeEnum.TRADE_SOURCE_TYPE_RECHARGE_NOTIFY.getType() == sourceType) {
				logger.info("[充值notify]创建代收交易开始，orderNo=" + order.getOrderNo());
				// 如果前置验证失败，则叫订单置为已充值，交易失败
				if (result.isError()) {
					orderManager.updateStatus(order, StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus(), result.getResultCode().getMsg());
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
					insertHostingCollectTrade(order, TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType(), payerIp, isWithholdAuthority);
				}
				if (isWithholdAuthority) {
					BigDecimal balanceAmount = null;
					Long balanceSourceId = null;
					TypeEnum typeEnum = null;
					if (order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
						TransferProject transferPro = transferProjectManager.selectByPrimaryKey(order.getTransferId());
						balanceAmount = order.getTransferPrincipal();
						balanceSourceId = transferPro.getTransactionId();
						typeEnum = TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT;
					} else {
						balanceAmount = order.getInvestAmount();
						balanceSourceId = order.getProjectId();
						typeEnum = TypeEnum.BALANCE_TYPE_PROJECT;
					}
					// 冻结项目余额
					balanceManager.frozenBalance(typeEnum, balanceAmount, balanceSourceId);
				}
			} else if (TypeEnum.TRADE_SOURCE_TYPE_TRANSACTION.getType() == sourceType) {
				logger.info("[交易时调用（无需充值）]创建代收交易开始，orderNo=" + order.getOrderNo());
				// 如果前置验证失败，则叫订单置为交易失败
				if (!result.isSuccess()) {
					orderManager.updateStatus(order, StatusEnum.ORDER_PAYED_FAILED.getStatus(), result.getResultCode().getMsg());
					// 如果有使用收益券，解锁收益券
					if (StringUtil.isNotBlank(order.getProfitCouponNo())) {
						couponManager.unLockCoupon(order.getProfitCouponNo());
					}
					MessageClient.sendMsgForInvestmentFail(order.getProjectId(), order.getMemberId());
					return result;
				}
				// 更新订单信息
				if (isWithholdAuthority) {
					orderManager.updateStatus(order, StatusEnum.ORDER_WAIT_PROCESS.getStatus(), null);
				} else {
					orderManager.updateStatus(order, StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus(), null);
				}
				// 创建代收交易
				double usedCapitalAmount = order.getUsedCapital().add(order.getPayAmount()).doubleValue();
				if (usedCapitalAmount > 0) {
					insertHostingCollectTrade(order, TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType(), payerIp, isWithholdAuthority);
					// 冻结项目余额
					// 锁定现金券
					if (StringUtil.isNotBlank(order.getCashCouponNo())) {
						couponManager.lockCoupon(order.getCashCouponNo());
					}
					// 开通委托支付的交易冻结项目余额
					if (isWithholdAuthority) {
						BigDecimal balanceAmount = null;
						Long balanceSourceId = null;
						TypeEnum typeEnum = null;
						if (order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
							TransferProject transferPro = transferProjectManager.selectByPrimaryKey(order.getTransferId());
							balanceAmount = order.getTransferPrincipal();
							balanceSourceId = transferPro.getTransactionId();
							typeEnum = TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT;
						} else {
							balanceAmount = order.getInvestAmount();
							balanceSourceId = order.getProjectId();
							typeEnum = TypeEnum.BALANCE_TYPE_PROJECT;
						}
						balanceManager.frozenBalance(typeEnum, balanceAmount, balanceSourceId);
					}
				}
				// 如果代收等于0，直接执行交易
				if (usedCapitalAmount == 0) {
					logger.info("全部使用优惠券交易，无需创建代收交易，orderNo=" + order.getOrderNo());
					result.getResult().setAllCashCouponPay(true);
					ResultDO<Transaction> transactionResult = transactionManager.doTransactionWithoutCreateHostingCollect(order);
					if (transactionResult.isError()) {
						// 如果交易失败，则将解锁现金券和优惠券，同时将订单置为支付失败
						// 如果使用了优惠券，解锁优惠券
						if (StringUtil.isNotBlank(order.getProfitCouponNo())) {
							couponManager.unLockCoupon(order.getProfitCouponNo());
						}
						orderManager.updateStatus(order, StatusEnum.ORDER_PAYED_FAILED.getStatus(), transactionResult.getResultCode()
								.getMsg());
						MessageClient.sendMsgForInvestmentFail(order.getProjectId(), order.getMemberId());
						return result;
					}
					if (transactionResult.getResult() != null) {
						Transaction transaction = transactionResult.getResult();
						if (transaction != null) {
							// 活动引擎
							Project dto = projectManager.selectByPrimaryKey(transaction.getProjectId());
							if (dto.isDirectProject()) {
								// 投资成功，发送站内信和短信通知投资者
								MessageClient.sendMsgForCommon(transaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM,
										MessageEnum.P2P_UNRAISE.getCode(), DateUtils.getStrFromDate(DateUtils.getCurrentDate(),
												DateUtils.DATE_FMT_7), 
												(transaction.getProjectName().contains("期")?transaction.getProjectName().substring(0, transaction.getProjectName().indexOf("期") + 1):transaction.getProjectName()),
												transaction.getInvestAmount().toString(), transaction.getProjectId().toString());
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
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<TradeBiz> createSinpayHostingCollectTradeAfterLocalSuccess(Order order, int sourceType, String payerIp,
			boolean isWithholdAuthority, String returnUrl) throws Exception {
		ResultDO<TradeBiz> result = new ResultDO<TradeBiz>();
		HostingCollectTrade memberHostingCollectTradeUnLock = hostingCollectTradeManager.getWaitPayHostingCollectTrade(order.getId());
		logger.info("本地代收：" + memberHostingCollectTradeUnLock.getTradeNo() + "开始创建新浪代收");
		try {
			HostingCollectTrade collectTradeForLock = hostingCollectTradeManager.getByIdForLock(memberHostingCollectTradeUnLock.getId());
			// 如果已经是最终状态，不处理，直接返回
			if (collectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
					|| collectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())
					|| collectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())) {
				logger.info("托管代收交易tradeNo=" + collectTradeForLock.getTradeNo() + "状态已经是最终状态：" + collectTradeForLock.getTradeStatus());
				result.setResultCode(ResultCode.COLLECT_TRADE_AUTH_IS_LAST_STATUS_ERROR);
				return result;
			} 
			TradeBiz biz = new TradeBiz();
			result.setResult(biz);
			String collectTradeType = null;
			if (TypeEnum.COLLECT_TRADE_TYPE_FREEZE.getType() == collectTradeForLock.getIsPreAuth()) {
				collectTradeType = TypeEnum.COLLECT_TRADE_TYPE_FREEZE.getCode();
			}
			if (isWithholdAuthority) {
				biz.setIsWithholdAuthority(StatusEnum.WITHHOLD_AUTHORITY_FLAG_Y.getStatus());
				ResultDto<CreateCollectTradeResult> resultSina = sinaPayClient.createHostingCollectTrade(collectTradeForLock.getTradeNo(),
						order.getProjectName() + Constant.TRADE_GOODS_SUMMARY,
						SerialNumberUtil.generateIdentityId(collectTradeForLock.getPayerId()), payerIp, AccountType.SAVING_POT,
						collectTradeForLock.getAmount(), IdType.UID, TradeCode.COLLECT_FROM_INVESTOR, collectTradeType);
				// 委托支付授权未开通，发起收银台代收
				if (resultSina.isError() && resultSina.getErrorCode().equals(SinaPayEnum.RESPONSE_AUTH_EMPTY.getCode())) {
					logger.info("发起新浪委托代收失败, tradeNo={}, reason={},发起新浪收银台代收", collectTradeForLock.getTradeNo(),
							SinaPayEnum.RESPONSE_AUTH_EMPTY.getDesc());
					biz.setIsWithholdAuthority(StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus());
					// 调用跳转收银台接口
					String returnForm = sinaCashDeskClient.createHostingCollectTrade(
							collectTradeForLock.getTradeNo(),
							order.getProjectName() + Constant.TRADE_GOODS_SUMMARY,
							SerialNumberUtil.generateIdentityId(collectTradeForLock.getPayerId()), payerIp,
							collectTradeForLock.getAmount(), IdType.UID, TradeCode.COLLECT_FROM_INVESTOR, collectTradeType, returnUrl);
					biz.setRedirectUrl(returnForm);
					// 更新委托支付状态
					collectTradeForLock.setIsWithholdAuthority(StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus());
					hostingCollectTradeManager.updateHostingCollectTradeWithholdAuthority(collectTradeForLock);
					return result;
				}
				// 开通委托的代收返回不需要处理其他业务
				return result;
			} else {
				biz.setIsWithholdAuthority(StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus());
				// 调用跳转收银台接口
				String returnForm = sinaCashDeskClient.createHostingCollectTrade(collectTradeForLock.getTradeNo(), order.getProjectName()
						+ Constant.TRADE_GOODS_SUMMARY, SerialNumberUtil.generateIdentityId(collectTradeForLock.getPayerId()), payerIp,
						collectTradeForLock.getAmount(), IdType.UID, TradeCode.COLLECT_FROM_INVESTOR, collectTradeType, returnUrl);
				biz.setRedirectUrl(returnForm);
				return result;
			}
		} catch (Exception e) {
			logger.error("交易时创建新浪代收交易发生异常：" + memberHostingCollectTradeUnLock.getTradeNo(), e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

	@Override
	public ResultDO<?> createTransactionPreValidate(Order order, int sourceType, ResultDO<?> result, boolean isWithholdAuthority) {
		if (order.getProjectCategory() != null && order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
			return createTransactionPreValidateForTransfer(order, sourceType, result, isWithholdAuthority);
		} else {
			return createTransactionPreValidateForNormal(order, sourceType, result, isWithholdAuthority);
		}
	}

	private ResultDO<?> createTransactionPreValidateForNormal(Order order, int sourceType, ResultDO<?> result, boolean isWithholdAuthority) {
		BigDecimal usedCapital = new BigDecimal(0);
		BigDecimal usedCouponAmount = new BigDecimal(0);
		BigDecimal investAmount = new BigDecimal(0);
		BigDecimal payAmount = new BigDecimal(0);
		try {
			if (order.getUsedCapital() != null) {
				usedCapital = order.getUsedCapital();
			} else {
				order.setUsedCapital(usedCapital);
			}
			if (order.getUsedCouponAmount() != null) {
				usedCouponAmount = order.getUsedCouponAmount();
			} else {
				order.setUsedCouponAmount(usedCouponAmount);
			}
			if (order.getInvestAmount() != null) {
				investAmount = order.getInvestAmount();
			} else {
				order.setInvestAmount(investAmount);
			}
			if (order.getPayAmount() != null) {
				payAmount = order.getPayAmount();
			} else {
				order.setPayAmount(payAmount);
			}
			// 如果是充值notify，需要再一次验证订单状态
			if (TypeEnum.TRADE_SOURCE_TYPE_RECHARGE_NOTIFY.getType() == sourceType) {
				if (order.getStatus() != StatusEnum.ORDER_WAIT_PAY.getStatus()) {
					result.setResultCode(ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY);
					logger.info("交易前置条件验证失败：" + ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY.getMsg() + ", orderNo=" + order.getOrderNo());
					return result;
				}
				// 充值交易，非委托支付无法
				if (!isWithholdAuthority) {
					logger.info("交易前置条件验证失败：充值交易因为未开通非委托支付授权 orderNo={}", order.getOrderNo());
					result.setResultCode(ResultCode.AUTO_INVEST_AUTHORITY_ERROR);
					return result;
				}
			}
			/**
			 * 基础判断
			 */
			// 校验该用户是否有用券显示
			if (StringUtil.isNotBlank(order.getProfitCouponNo()) || StringUtil.isNotBlank(order.getCashCouponNo())) {
				if (!couponManager.useCouponSpecialLimit(order.getMemberId())) {
					logger.info("前台保存订单前置条件验证失败：" + ResultCode.MEMBER_USE_COUPON_LIMIT_ERROR.getMsg() + ", order=" + order);
					result.setResultCode(ResultCode.MEMBER_USE_COUPON_LIMIT_ERROR);
					return result;
				}
			}
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
				logger.info("交易前置条件验证失败：{}, orderNo={}, usedCapital={}, usedCouponAmount={}, payAmount={}, investAmount={}",
						ResultCode.TRANSACTION_USEDCAPITAL_USEDREWARD_NOT_EQUAL_INVESTAMOUNT.getMsg(), order.getOrderNo(), usedCapital,
						usedCouponAmount, payAmount, investAmount);
				return result;
			}
			/**
			 * 项目相关判断
			 */
			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			// 项目不存在返回
			if (project == null || (project.getDelFlag() != null && project.getDelFlag().intValue() < 1)) {
				result.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
				logger.info("交易前置条件验证失败：" + ResultCode.PROJECT_NOT_EXIST_ERROR.getMsg() + ", orderNo=" + order.getOrderNo()
						+ ", projectId=" + order.getProjectId());
				return result;
			}
			// 如果使用账户资金和使用现金券金额相加小于项目最小投资额时，返回
			BigDecimal minInvestAmount = project.getMinInvestAmount();
			if (minInvestAmount.compareTo(usedCapital.add(usedCouponAmount).add(payAmount)) > 0) {
				result.setResultCode(ResultCode.TRANSACTION_PROJECT_LESS_MINIVESTMOUNT);
				logger.info("交易前置条件验证失败：" + ResultCode.TRANSACTION_PROJECT_LESS_MINIVESTMOUNT.getMsg() + ", orderNo=" + order.getOrderNo()
						+ ", order=" + order);
				return result;
			}
			// 判断当前项目是否是可销售状态
			if (project.getStatus().intValue() != StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
				result.setResultCode(ResultCode.PROJECT_NOT_INVESTING_STATUS_ERROR);
				logger.info("交易前置条件验证失败：" + ResultCode.PROJECT_NOT_INVESTING_STATUS_ERROR.getMsg() + ", orderNo=" + order.getOrderNo()
						+ ", projectId=" + order.getProjectId());
				return result;
			}
			// 新客校验
			if (project.isNoviceProject()) {
				boolean checkNoviceFlag = memberManager.needCheckNoviceProject(order.getMemberId());
				if (checkNoviceFlag && transactionManager.getTransactionCountByMember(order.getMemberId()) > 0) {
					result.setResultCode(ResultCode.ORDER_ONLY_ALLOWING_NEW_USERS_ERROR);
					logger.info("前台交易订单前置条件验证失败：" + ResultCode.ORDER_ONLY_ALLOWING_NEW_USERS_ERROR.getMsg() + ", order="
							+ order.getOrderNo());
					return result;
				}
			}
			// 如果(用户投资总额-起投金额)/递增金额 余数等于0
			BigDecimal incrementAmount = project.getIncrementAmount();
			if (investAmount.subtract(minInvestAmount).remainder(incrementAmount).compareTo(BigDecimal.ZERO) != 0) {
				result.setResultCode(ResultCode.TRANSACTION_INCREMENT_AMOUNT_ERROR);
				logger.info("交易前置条件验证失败：" + ResultCode.TRANSACTION_INCREMENT_AMOUNT_ERROR.getMsg() + ", orderNo=" + order.getOrderNo()
						+ ", order=" + order);
				return result;
			}
			/**
			 * 用户存钱罐相关判断
			 */
			Long memberId = order.getMemberId();
			// 同步下是否开通支付密码(防止本地数据篡改,其实新浪那边没有开通支付密码)
			ResultDto<PayPasswordDto> reusltSinaDto = sinaCashDeskClient.handlePayPassword(memberId,
					TypeEnum.QUERY_IS_SET_PAY_PASSWORD.getType(), null);
			if (reusltSinaDto.isError()) {
				result.setResultCode(ResultCode.ERROR_SYSTEM);
				logger.error("新浪查询是否设置支付密码失败, memberId={}, errorCode={}, errorMsg={}", memberId, reusltSinaDto.getErrorCode(),
						reusltSinaDto.getErrorMsg());
				String errorMsg = String.format("同步是否开通支付密码异常");
				result.getResultCode().setMsg(errorMsg);
				logger.error(errorMsg);
				return result;
			}
			if (!"Y".equals(reusltSinaDto.getModule().getIsSetPaypass())) {
				result.setResultCode(ResultCode.PAY_PASSWORD_NOT_SET_ERROR);
				return result;
			}
			// 判断用户资金余额
			BigDecimal investTotalAmount = usedCapital.add(payAmount);
			if (investTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
				Balance piggyBalance = balanceManager.queryFromThirdPay(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
				BigDecimal capitalAccountBalance = piggyBalance.getAvailableBalance();
				if (investTotalAmount.compareTo(capitalAccountBalance) > 0) {
					result.setResultCode(ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING);
					logger.info("交易前置条件验证失败：" + ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING.getMsg() + ", orderNo="
							+ order.getOrderNo() + ", order=" + order);
					return result;
				}
			}
			// 项目余额为0 修改为加锁查询
			Balance projectBalance = balanceManager.queryBalanceLocked(order.getProjectId(), TypeEnum.BALANCE_TYPE_PROJECT);
			if (projectBalance != null) {
				if (projectBalance.getAvailableBalance() != null && projectBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 1) {
					result.setResultCode(ResultCode.PROJECT_BALANCE_ZERO_ERROR);
					logger.debug("交易前置条件验证失败：" + ResultCode.PROJECT_BALANCE_ZERO_ERROR.getMsg() + ", orderNo=" + order.getOrderNo()
							+ ", projectId=" + order.getProjectId());
					return result;
				}
			}
			// 判断项目余额是否足够
			if (projectBalance.getAvailableBalance() != null && projectBalance.getAvailableBalance().compareTo(investAmount) < 0) {
				result.setResultCode(ResultCode.PROJECT_BALANCE_LACEING_ERROR);
				logger.debug("交易前置条件验证失败：" + ResultCode.PROJECT_BALANCE_LACEING_ERROR.getMsg() + ", projectBalance="
						+ projectBalance.getAvailableBalance().toPlainString() + ", order=" + order);
				return result;
			}
			result.setSuccess(true);
		} catch (Exception e) {
			result.setResultCode(ResultCode.TRANSACTION_PRE_VALIDATE_EXCEPTION_ERROR);
			logger.error("交易前判断发生异常, orderNo=" + order.getOrderNo(), e);
			// throw new ManagerException("交易前判断发生异常", e);
		}
		return result;
	}

	private ResultDO<?> createTransactionPreValidateForTransfer(Order order, int sourceType, ResultDO<?> result, boolean isWithholdAuthority) {
		BigDecimal usedCapital = new BigDecimal(0);
		BigDecimal investAmount = new BigDecimal(0);
		BigDecimal payAmount = new BigDecimal(0);
		try {
			if (order.getUsedCapital() != null) {
				usedCapital = order.getUsedCapital();
			} else {
				order.setUsedCapital(usedCapital);
			}
			if (order.getInvestAmount() != null) {
				investAmount = order.getInvestAmount();
			} else {
				order.setInvestAmount(investAmount);
			}
			if (order.getPayAmount() != null) {
				payAmount = order.getPayAmount();
			} else {
				order.setPayAmount(payAmount);
			}
			// 如果是充值notify，需要再一次验证订单状态
			if (TypeEnum.TRADE_SOURCE_TYPE_RECHARGE_NOTIFY.getType() == sourceType) {
				if (order.getStatus() != StatusEnum.ORDER_WAIT_PAY.getStatus()) {
					result.setResultCode(ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY);
					logger.info("交易前置条件验证失败：" + ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY.getMsg() + ", orderNo=" + order.getOrderNo());
					return result;
				}
				// 充值交易，非委托支付无法
				if (!isWithholdAuthority) {
					logger.info("交易前置条件验证失败：充值交易因为未开通非委托支付授权 orderNo={}", order.getOrderNo());
					result.setResultCode(ResultCode.AUTO_INVEST_AUTHORITY_ERROR);
					return result;
				}
			}
			/**
			 * 基础判断
			 */
			// 转让项目不允许使用优惠券
			if (StringUtil.isNotBlank(order.getProfitCouponNo()) || StringUtil.isNotBlank(order.getCashCouponNo())) {
				result.setResultCode(ResultCode.TRANSFER_PROJECT_EXISTS_COUPON_ERROR);
				logger.info("交易前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_EXISTS_COUPON_ERROR.getMsg() + ", orderNo=" + order.getOrderNo());
				return result;
			}
			if ((order.getExtraAnnualizedRate() != null && order.getExtraAnnualizedRate().compareTo(BigDecimal.ZERO) != 0)
					|| (order.getUsedCouponAmount() != null && order.getUsedCouponAmount().compareTo(BigDecimal.ZERO) != 0)) {
				result.setResultCode(ResultCode.TRANSFER_PROJECT_EXISTS_COUPON_ERROR);
				logger.info("交易前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_EXISTS_COUPON_ERROR.getMsg() + ", orderNo=" + order.getOrderNo());
				return result;
			}
			// 如果使用账户资金和使用奖励账户资金以及支付金额都为0时，返回
			if (usedCapital.compareTo(BigDecimal.ZERO) < 1 && payAmount.compareTo(BigDecimal.ZERO) < 1) {
				result.setResultCode(ResultCode.TRANSACTION_CAPITAL_REWARD_ZERO);
				logger.info("交易前置条件验证失败：" + ResultCode.TRANSACTION_CAPITAL_REWARD_ZERO.getMsg() + ", orderNo=" + order.getOrderNo());
				return result;
			}
			// 判断使用资金账户金额与支付金额是否等于投资金额
			if (usedCapital.add(payAmount).compareTo(investAmount) != 0) {
				result.setResultCode(ResultCode.TRANSACTION_USEDCAPITAL_USEPAYAMOUNT_NOT_EQUAL_INVESTAMOUNT);
				logger.info("交易前置条件验证失败：{}, orderNo={}, usedCapital={}, payAmount={}, investAmount={}",
						ResultCode.TRANSACTION_USEDCAPITAL_USEPAYAMOUNT_NOT_EQUAL_INVESTAMOUNT.getMsg(), order.getOrderNo(), usedCapital,
						payAmount, investAmount);
				return result;
			}
			/**
			 * 转让项目相关判断
			 */

			TransferProject transferPro = transferProjectManager.selectByPrimaryKey(order.getTransferId());
			// 项目不存在返回
			if (transferPro == null || (transferPro.getDelFlag() != null && transferPro.getDelFlag().intValue() < 1)) {
				result.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
				logger.info("交易前置条件验证失败：" + ResultCode.PROJECT_NOT_EXIST_ERROR.getMsg() + ", orderNo=" + order.getOrderNo()
						+ ", transferId=" + order.getTransferId());
				return result;
			}
			// 校验是否流标
			if ( transferPro.getStatus() == StatusEnum.TRANSFER_PROJECT_STATUS_LOSE.getStatus()
					|| DateUtils.getCurrentDate().after(transferPro.getTransferEndDate())) {
				logger.info("交易前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_HAS_LOST.getMsg() + ", order=" + order);
				result.setResultCode(ResultCode.TRANSFER_PROJECT_HAS_LOST);
				return result;
			}
			// 校验项目状态
			if (transferPro.getStatus() != StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()) {
				logger.info("交易前置条件验证失败：" + ResultCode.PROJECT_NOT_INVESTING_STATUS_ERROR.getMsg() + ", order=" + order);
				result.setResultCode(ResultCode.PROJECT_NOT_INVESTING_STATUS_ERROR);
				return result;
			}
			// 判断当天是否有还款（普通还款只需要根据转让项目结束时间判断，这里判断提前还款）
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transferPro.getTransactionId());
			transactionInterestQuery.setCurdate(true);
			List<TransactionInterest> earlyPayList = transactionInterestManager.queryEarlyInterest(transactionInterestQuery);
			if (Collections3.isNotEmpty(earlyPayList)) {
				logger.info("交易前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_EXISTS_EARLY_PAYMENT_ERROR.getMsg() + ", order=" + order);
				result.setResultCode(ResultCode.TRANSFER_PROJECT_EXISTS_EARLY_PAYMENT_ERROR);
				return result;
			}
			// 认购本金小于最小认购金额，则返回
			if (transferPro.getUnitSubscriptionAmount().compareTo(order.getTransferPrincipal()) == 1) {
				result.setResultCode(ResultCode.ORDER_FRONT_TRANSFER_PRINCIPAL_LESS_MINPRINCIPAL);
				logger.info("交易前置条件验证失败：" + ResultCode.ORDER_FRONT_TRANSFER_PRINCIPAL_LESS_MINPRINCIPAL.getMsg() + ", orderNo="
						+ order.getOrderNo() + ", order=" + order);
				return result;
			}
			// 如果用户投资总额/递增金额 余数等于0
			if (order.getTransferPrincipal().doubleValue() % transferPro.getUnitSubscriptionAmount().doubleValue() != 0) {
				result.setResultCode(ResultCode.TRANSFER_TRANSACTION_INCREMENT_AMOUNT_ERROR);
				logger.info("前台保存订单前置条件验证失败：" + ResultCode.TRANSFER_TRANSACTION_INCREMENT_AMOUNT_ERROR.getMsg() + ", order=" + order);
				return result;
			}
			/**
			 * 用户存钱罐相关判断
			 */
			Long memberId = order.getMemberId();
			// 同步下是否开通支付密码(防止本地数据篡改,其实新浪那边没有开通支付密码)
			ResultDto<PayPasswordDto> reusltSinaDto = sinaCashDeskClient.handlePayPassword(memberId,
					TypeEnum.QUERY_IS_SET_PAY_PASSWORD.getType(), null);
			if (reusltSinaDto.isError()) {
				result.setResultCode(ResultCode.ERROR_SYSTEM);
				logger.error("新浪查询是否设置支付密码失败, memberId={}, errorCode={}, errorMsg={}", memberId, reusltSinaDto.getErrorCode(),
						reusltSinaDto.getErrorMsg());
				String errorMsg = String.format("同步是否开通支付密码异常");
				result.getResultCode().setMsg(errorMsg);
				logger.error(errorMsg);
				return result;
			}
			if (!"Y".equals(reusltSinaDto.getModule().getIsSetPaypass())) {
				result.setResultCode(ResultCode.PAY_PASSWORD_NOT_SET_ERROR);
				return result;
			}
			// 判断用户资金余额
			BigDecimal investTotalAmount = usedCapital.add(payAmount);
			if (investTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
				Balance piggyBalance = balanceManager.queryFromThirdPay(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
				BigDecimal capitalAccountBalance = piggyBalance.getAvailableBalance();
				if (investTotalAmount.compareTo(capitalAccountBalance) > 0) {
					result.setResultCode(ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING);
					logger.info("交易前置条件验证失败：" + ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING.getMsg() + ", orderNo="
							+ order.getOrderNo() + ", order=" + order);
					return result;
				}
			}
			// 判断可认购本金是否为零
			Balance transactionBalance = balanceManager.queryBalanceLocked(transferPro.getTransactionId(),
					TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT);
			if (transactionBalance.getAvailableBalance().compareTo(BigDecimal.ZERO) < 1) {
				result.setResultCode(ResultCode.PROJECT_BALANCE_ZERO_ERROR);
				logger.debug("交易前置条件验证失败：" + ResultCode.PROJECT_BALANCE_ZERO_ERROR.getMsg() + ", orderNo=" + order.getOrderNo()
						+ ", projectId=" + order.getProjectId());
				return result;
			}
			if (transactionBalance.getAvailableBalance().compareTo(order.getTransferPrincipal()) < 0) {
				result.setResultCode(ResultCode.TRANSFER_PROJECT_BALANCE_LACEING_ERROR);
				logger.debug("交易前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_BALANCE_LACEING_ERROR.getMsg() + ", projectBalance="
						+ transactionBalance.getAvailableBalance().toPlainString() + ", order=" + order);
				return result;
			}
			// 计算投资本金
			BigDecimal investAmountNew = order.getTransferPrincipal().multiply(transferPro.getTransferAmount())
					.divide(transferPro.getSubscriptionPrincipal(), 2, BigDecimal.ROUND_HALF_UP);
			if (investAmountNew.compareTo(investAmount) != 0) {
				result.setResultCode(ResultCode.TRANSFER_TRANSACTION_INVEST_AMOUNT_ERROR);
				logger.debug("交易前置条件验证失败：" + ResultCode.TRANSFER_TRANSACTION_INVEST_AMOUNT_ERROR.getMsg() + ", order=" + order);
				return result;
			}
			result.setSuccess(true);
		} catch (Exception e) {
			result.setResultCode(ResultCode.TRANSACTION_PRE_VALIDATE_EXCEPTION_ERROR);
			logger.error("交易前判断发生异常, orderNo=" + order.getOrderNo(), e);
		}
		return result;
	}

	public HostingCollectTrade insertHostingCollectTrade(Order order, int collectType, String payerIp, boolean isWithholdAuthority)
			throws Exception {
		try {
			HostingCollectTrade trade = hostingCollectTradeManager.selectWaitPayOrFinishedHostingCollectTrade(order.getId(),
					TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType());
			if (trade != null) {
				throw new ManagerException("订单" + order.getId() + ",已经存在待收，创建待收失败");
			}
			// 本地保存HostingCollectTrade信息
			BigDecimal amount = BigDecimal.ZERO;
			Long payerId = 0l;
			HostingCollectTrade collectTrade = new HostingCollectTrade();
			amount = order.getUsedCapital().add(order.getPayAmount());
			payerId = order.getMemberId();
			collectTrade.setAmount(amount);
			collectTrade.setPayerId(payerId);
			collectTrade.setBankCode(order.getBankCode());
			collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
			collectTrade.setRemarks(order.getRemarks());
			collectTrade.setSourceId(order.getId());
			collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
			String orderIndex = order.getId().toString().length() > 4 ? order.getId().toString()
					.substring(order.getId().toString().length() - 4, order.getId().toString().length()) : order.getId().toString();
			collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo() + orderIndex);
			collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
			collectTrade.setPayStatus((SinaPayEnum.PAY_PROCESSING.getCode()));
			collectTrade.setTradeTime(order.getOrderTime());
			collectTrade.setType(collectType);
			collectTrade.setPayerIp(payerIp);
			collectTrade.setIsWithholdAuthority(isWithholdAuthority);
			collectTrade.setProjectCategory(order.getProjectCategory());
			// 代收类型如果是代收冻结，则插入冻结类型
			hostingCollectTradeManager.loadCollectTradeByOrder(order, collectTrade);
			hostingCollectTradeManager.insertSelective(collectTrade);
			return collectTrade;
		} catch (ManagerException e) {
			logger.error("交易出错[保存HostingCollectTrade信息出错]：order=" + order, e);
			throw e;
		}
	}

}
