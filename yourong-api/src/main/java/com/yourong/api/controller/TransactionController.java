package com.yourong.api.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.api.dto.ContractSignDto;
import com.yourong.api.dto.OrderDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.TransactionForMemberDto;
import com.yourong.api.service.ActivityLotteryService;
import com.yourong.api.service.MemberBankCardService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.OrderService;
import com.yourong.api.service.ProjectService;
import com.yourong.api.service.TransactionInterestService;
import com.yourong.api.service.TransactionService;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.enums.RechargeBankCode;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.query.TransferRecordQuery;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.ContractBiz;
import com.yourong.core.tc.model.biz.TradeBiz;
import com.yourong.core.tc.model.biz.TransactionInterestForMember;
import com.yourong.core.tc.model.biz.TransferContractBiz;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionQuery;

/**
 * 交易controller
 *
 * @author Administrator
 */
@Controller
@RequestMapping("security/transaction")
public class TransactionController extends BaseController {

	@Autowired
	private TransactionService transactionService;
	@Autowired
	private TransactionInterestService transactionInterestService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private SinaPayClient sinaPayClient;
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private MemberBankCardService memberBankCardService;
	@Autowired
	private ProjectManager projectManager;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private ActivityLotteryService activityLotteryService;
	@Autowired
	private TransferProjectManager transferProjectManager;

	/**
	 * 收银台交易接口： 1. 开通委托且余额足够：自动发起代收 2. 未开通委托且余额足够：返回支付重定向链接
	 * 
	 * @param req
	 * @param resp
	 * @param orderDto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/pay/order/cashDesk", method = RequestMethod.POST, headers = { "Accept-Version=1.7.0" })
	@ResponseBody
	public ResultDTO<TradeBiz> payOrderCashDesk(HttpServletRequest req, HttpServletResponse resp, OrderDto orderDto) {
		ResultDTO<TradeBiz> result = new ResultDTO<TradeBiz>();
		try {
			if ((orderDto.getUsedCapital() != null && orderDto.getUsedCapital().doubleValue() < 0)
					|| (orderDto.getUsedCouponAmount() != null && orderDto.getUsedCouponAmount().doubleValue() < 0)) {
				result.setResultCode(ResultCode.TRANSACTION_AMOUNT_MUST_BE_ABOVE_ZERO);
				return result;
			}
			Order order = orderService.getOrderByOrderNo(orderDto.getOrderNo());
			if (order == null) {
				result.setResultCode(ResultCode.ORDER_NOT_EXISTS_ERROR);
				return result;
			}
			if (order.getStatus() == StatusEnum.ORDER_CLOSED.getStatus()) {
				result.setResultCode(ResultCode.ORDER_FRONT_ORDER_CLOSE);
				return result;
			}
			String payerIp = getIp(req);
			// 非委托支付，订单正在新浪确认中则直接返回交易链接
			if (order.getStatus() == StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus()) {
				ResultDTO<TradeBiz> rDTO = transactionService.isRepeatToCashDest(order, payerIp);
				if (!rDTO.isSuccess() || rDTO.getResult().isNotFirstRequest()) {
					return rDTO;
				}
			}
			if (order.getStatus() != StatusEnum.ORDER_WAIT_PAY.getStatus()
					&&order.getStatus() != StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus()) {
				result.setResultCode(ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY);
				return result;
			}
			order.setUsedCapital(orderDto.getUsedCapital());
			// 设置订单现金券金额
			order.setUsedCouponAmount(orderDto.getUsedCouponAmount());
			order.setCashCouponNo(orderDto.getCashCouponNo());
			order.setPayAmount(BigDecimal.ZERO);
			// carPayIn、carBusiness不允许使用优惠券
			if (orderManager.orderOfCarPayInOrCarBusinessIsUseCoupon(order.getProjectId(), order.getCashCouponNo())) {
				result.setResultCode(ResultCode.PROJECT_CARPAYIN_CANOT_USE_COUPON);
				return result;
			}
			ResultDO<Order> checkCouponDO = orderService.checkOrderCashCouponNo(order, false);
			if (checkCouponDO.isError()) {
				result.setResultCode(checkCouponDO.getResultCode());
				return result;
			}
			Integer source = getRequestSource(req);
			// 发起新浪代收支付
			return transactionService.createTransactionHostingTrade(order, TypeEnum.TRADE_SOURCE_TYPE_TRANSACTION.getType(), payerIp,source);
		} catch (Exception e) {
			result.setResultCode(ResultCode.ORDER_FRONT_PAY_ERROR);
			return result;
		}
	}

	/**
	 * 充值交易
	 *
	 * @param req
	 * @param resp
	 * @param orderDto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/pay/order/recharge", method = RequestMethod.POST, headers = { "Accept-Version=1.7.0" })
	@ResponseBody
	public Object onthirdPayCarID(HttpServletRequest req, HttpServletResponse resp, OrderDto orderDto) {
		ResultDTO<Order> orderResultDO = checkPayOrder(orderDto, req);
		//TODO 日志打印
		if (!orderResultDO.isSuccess()) {
			return orderResultDO;
		}
		Integer source = getRequestSource(req);
		Order order = orderResultDO.getResult();
		BigDecimal bigDecimal = order.getPayAmount();
		String orderNo = order.getOrderNo();
		Long orderId = order.getId();
		String payerIp = getIp(req);
		ResultDTO<Object> resultDO = memberService.createHostingDepositAndRechargeLogInvestment(getMemberID(req), payerIp, bigDecimal,
				TypeEnum.RECHARGELOG_TYPE_TRADING, orderNo,source,orderId);
		return resultDO;
	} 

	
	/**
	 * 效验订单
	 *
	 * @param orderDto
	 *            是绑卡支付
	 * @return
	 * @throws Exception
	 */
	private ResultDO<Order> checkPayOrderOld(OrderDto orderDto, HttpServletRequest req) throws Exception {
		ResultDO<Order> result = new ResultDO<Order>();
		Long memberId = getMemberID(req);
		if (orderDto.getIsFirstCreaterOrder() == null) {
			result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return result;
		}
		if (orderDto.getBankCode() == null) {
			result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return result;
		}
		if ((orderDto.getPayAmount() != null && orderDto.getPayAmount().doubleValue() < 0)
				|| (orderDto.getUsedCapital() != null && orderDto.getUsedCapital().doubleValue() < 0)
				|| (orderDto.getUsedCouponAmount() != null && orderDto.getUsedCouponAmount().doubleValue() < 0)) {
			logger.info("支付订单：" + ResultCode.TRANSACTION_AMOUNT_MUST_BE_ABOVE_ZERO.getMsg());
			result.setResultCode(ResultCode.TRANSACTION_AMOUNT_MUST_BE_ABOVE_ZERO);
			return result;
		}
		Order order = orderService.getOrderByOrderNo(orderDto.getOrderNo());
		if (order == null) {
			result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
			return result;
		}
		if (order.getStatus() == StatusEnum.ORDER_CLOSED.getStatus()) {
			result.setResultCode(ResultCode.ORDER_FRONT_ORDER_CLOSE);
			return result;
		}
		if (order.getStatus() != StatusEnum.ORDER_WAIT_PAY.getStatus()) {
			result.setResultCode(ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY);
			return result;
		}
		if (memberId.compareTo(order.getMemberId()) != 0) {
			result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
			return result;
		}

		// carPayIn、carBusiness不允许使用优惠券
		if (orderManager.orderOfCarPayInOrCarBusinessIsUseCoupon(order.getProjectId(), orderDto.getCashCouponNo())) {
			result.setResultCode(ResultCode.PROJECT_CARPAYIN_CANOT_USE_COUPON);
			return result;
		}

		order.setUsedCapital(orderDto.getUsedCapital());
		order.setUsedCouponAmount(orderDto.getUsedCouponAmount());
		order.setCashCouponNo(orderDto.getCashCouponNo());
		order.setBankCode(orderDto.getBankCode());
		order.setPayAmount(orderDto.getPayAmount());
		order.setPayMethod(2);

		// 设置订单现金券金额
		// setOrderCashCouponAmount(order);

		if (order.getPayAmount() == null || order.getPayAmount().doubleValue() <= 0) {
			result.setResultCode(ResultCode.TRANSACTION_AMOUNT_MUST_BE_ABOVE_ZERO);
			return result;
		}
		BigDecimal usedCapital = orderDto.getUsedCapital() == null ? BigDecimal.ZERO : orderDto.getUsedCapital();
		BigDecimal investAmount = order.getInvestAmount() == null ? BigDecimal.ZERO : order.getInvestAmount();
		BigDecimal usedCouponAmount = order.getUsedCouponAmount() == null ? BigDecimal.ZERO : order.getUsedCouponAmount();
		BigDecimal payAmount = orderDto.getPayAmount() == null ? BigDecimal.ZERO : orderDto.getPayAmount();
		if (investAmount.compareTo(usedCapital.add(usedCouponAmount).add(payAmount)) != 0) {
			result.setResultCode(ResultCode.TRANSACTION_USEDCAPITAL_USEDREWARD_NOT_EQUAL_INVESTAMOUNT);
			return result;
		}
		if (orderDto.getIsFirstCreaterOrder()) {
			ResultDO<Order> resultOrder = orderService.updateOrderForRecharge(order);
			if (!resultOrder.isSuccess()) {
				result.setResultCode(resultOrder.getResultCode());
				return result;
			}
		} else {
			// result = orderService.checkOrderCashCouponNo(order, true);
			// if (!result.isSuccess()) {
			// return result;
			// }
		}

		// result = orderService.checkOrderProfitCouponNo(order, true);
		// if(!result.isSuccess()){
		// return result;
		// }

		BigDecimal bigDecimal = order.getPayAmount();
		// 充值金额必须大于0.01 必须要选择银行卡号
		if (new BigDecimal(Config.minrechargeAmount).compareTo(bigDecimal) == 1) {
			result.setResultCode(ResultCode.MEMBER_RECHARGE_MUST_MORE_ERROR);
			return result;
		}
		String blankCode = order.getBankCode();
		if (StringUtil.isBlank(blankCode)) {
			result.setResultCode(ResultCode.MEMBER_BANK_NOT_EXIST_ERROR);
			return result;
		}
		RechargeBankCode bankCodeemun = RechargeBankCode.getRechargeBankCode(blankCode);
		if (bankCodeemun == null) {
			result.setResultCode(ResultCode.MEMBER_BANK_NOT_EXIST_ERROR);
			return result;
		}
		Long cardID = orderDto.getCardId();
		if (cardID == null) {
			result.setResultCode(ResultCode.MEMBER_USERNAME_WITHDRAW_BANKID);
			return result;
		}
		boolean exist = memberBankCardService.isExist(cardID, memberId);
		if (!exist) {
			result.setResultCode(ResultCode.MEMBER_USERNAME_WRONG_BANKID);
			return result;
		}

		Project project = projectManager.selectByPrimaryKey(order.getProjectId());
		if (project.isNoviceProject()) {
			if (transactionService.hasTransactionByMember(order.getMemberId())) {
				result.setResultCode(ResultCode.ORDER_ONLY_ALLOWING_NEW_USERS_ERROR);
				return result;
			}
		}
		result.setSuccess(true);
		result.setResult(order);
		return result;
	}

	/**
	 * 效验订单
	 *
	 * @param orderDto
	 *            是绑卡支付
	 * @return
	 * @throws Exception
	 */
	private ResultDTO<Order> checkPayOrder(OrderDto orderDto, HttpServletRequest req)  {
		ResultDTO<Order> result = new ResultDTO<Order>();
		try {
			Long memberId = getMemberID(req);
			if (orderDto.getIsFirstCreaterOrder() == null) {
				result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return result;
			}
			
			if ((orderDto.getPayAmount() != null && orderDto.getPayAmount().doubleValue() < 0)
					|| (orderDto.getUsedCapital() != null && orderDto.getUsedCapital().doubleValue() < 0)
					|| (orderDto.getUsedCouponAmount() != null && orderDto.getUsedCouponAmount().doubleValue() < 0)) {
				logger.info("支付订单：" + ResultCode.TRANSACTION_AMOUNT_MUST_BE_ABOVE_ZERO.getMsg());
				result.setResultCode(ResultCode.TRANSACTION_AMOUNT_MUST_BE_ABOVE_ZERO);
				return result;
			}
			Order order = orderService.getOrderByOrderNo(orderDto.getOrderNo());
			if (order == null) {
				result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
                return result;
            }
			if (order.getStatus() == StatusEnum.ORDER_CLOSED.getStatus()) {
				result.setResultCode(ResultCode.ORDER_FRONT_ORDER_CLOSE);
                return result;
            }
			if (order.getStatus() != StatusEnum.ORDER_WAIT_PAY.getStatus()
					&&order.getStatus() != StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus()) {
				result.setResultCode(ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY);
                return result;
            }
			if (memberId.compareTo(order.getMemberId()) != 0) {
				result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
				return result;
			}

		// carPayIn、carBusiness不允许使用优惠券
	
			if (orderManager.orderOfCarPayInOrCarBusinessIsUseCoupon(order.getProjectId(), orderDto.getCashCouponNo())) {
				result.setResultCode(ResultCode.PROJECT_CARPAYIN_CANOT_USE_COUPON);
				return result;
			}

			order.setUsedCapital(orderDto.getUsedCapital());
			order.setUsedCouponAmount(orderDto.getUsedCouponAmount());
			order.setCashCouponNo(orderDto.getCashCouponNo());
			order.setBankCode(orderDto.getBankCode());
			order.setPayAmount(orderDto.getPayAmount());
			order.setPayMethod(2);

			// 设置订单现金券金额
			// setOrderCashCouponAmount(order);

			if (order.getPayAmount() == null || order.getPayAmount().doubleValue() <= 0) {
				result.setResultCode(ResultCode.TRANSACTION_AMOUNT_MUST_BE_ABOVE_ZERO);
				return result;
			}
			BigDecimal usedCapital = orderDto.getUsedCapital() == null ? BigDecimal.ZERO : orderDto.getUsedCapital();
			BigDecimal investAmount = order.getInvestAmount() == null ? BigDecimal.ZERO : order.getInvestAmount();
			BigDecimal usedCouponAmount = order.getUsedCouponAmount() == null ? BigDecimal.ZERO : order.getUsedCouponAmount();
			BigDecimal payAmount = orderDto.getPayAmount() == null ? BigDecimal.ZERO : orderDto.getPayAmount();
			if (investAmount.compareTo(usedCapital.add(usedCouponAmount).add(payAmount)) != 0) {
				result.setResultCode(ResultCode.TRANSACTION_USEDCAPITAL_USEDREWARD_NOT_EQUAL_INVESTAMOUNT);
				return result;
			}
			BigDecimal bigDecimal = order.getPayAmount();
			// 充值金额必须大于0.01 必须要选择银行卡号
			if (new BigDecimal(Config.minrechargeAmount).compareTo(bigDecimal) == 1) {
				result.setResultCode(ResultCode.MEMBER_RECHARGE_MUST_MORE_ERROR);
				return result;
			}
			if (order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				TransferProject tPro = transferProjectManager.selectByPrimaryKey(order.getTransferId());
				if (tPro == null || (tPro.getDelFlag() != null && tPro.getDelFlag().intValue() < 1)) {
					result.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
					logger.info("交易前置条件验证失败：" + ResultCode.PROJECT_NOT_EXIST_ERROR.getMsg() + ", orderNo=" + order.getOrderNo()
							+ ", projectId=" + order.getProjectId());
					return result;
				}
				// 不允许使用优惠券
				if (StringUtil.isNotBlank(order.getCashCouponNo()) || StringUtil.isNotBlank(order.getProfitCouponNo())) {
					logger.info("交易前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_EXISTS_COUPON_ERROR.getMsg() + ", order=" + order);
					result.setResultCode(ResultCode.TRANSFER_PROJECT_EXISTS_COUPON_ERROR);
					return result;
				}
				if (orderDto.getIsFirstCreaterOrder()) {
					order.setUsedCouponAmount(BigDecimal.ZERO);
					orderManager.updateByPrimaryKeySelective(order);
				}
				result.setIsSuccess();
				result.setResult(order);
				return result;
			}
			if (orderDto.getIsFirstCreaterOrder()) {
				ResultDO<Order> resultOrder = orderService.updateOrderForRecharge(order);
				if (!resultOrder.isSuccess()) {
					result.setResultCode(resultOrder.getResultCode());
					return result;
				}
			} else {
					// result = orderService.checkOrderCashCouponNo(order, true);
					// if (!result.isSuccess()) {
					// return result;
					// }
			}

			// result = orderService.checkOrderProfitCouponNo(order, true);
			// if(!result.isSuccess()){
			// return result;
			// }
			/*String blankCode = order.getBankCode();
			if (StringUtil.isBlank(blankCode)) {
				result.setResultCode(ResultCode.MEMBER_BANK_NOT_EXIST_ERROR);
				return result;
			}
			RechargeBankCode bankCodeemun = RechargeBankCode.getRechargeBankCode(blankCode);
			if (bankCodeemun == null) {
				result.setResultCode(ResultCode.MEMBER_BANK_NOT_EXIST_ERROR);
				return result;
			}
			Long cardID = orderDto.getCardId();
			if (cardID == null) {
				result.setResultCode(ResultCode.MEMBER_USERNAME_WITHDRAW_BANKID);
				return result;
			}
			boolean exist = memberBankCardService.isExist(cardID, memberId);
			if (!exist) {
				result.setResultCode(ResultCode.MEMBER_USERNAME_WRONG_BANKID);
				return result;
			}*/

			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			if (project.isNoviceProject()) {
				boolean checkNoviceFlag = memberService.needCheckNoviceProject(order.getMemberId());
				if (checkNoviceFlag && transactionService.hasTransactionByMember(order.getMemberId())) {
					result.setResultCode(ResultCode.ORDER_ONLY_ALLOWING_NEW_USERS_ERROR);
					return result;
				}
			}
			result.setIsSuccess();;
			result.setResult(order);
			return result;
		} catch (Exception e) {
			logger.error("效验订单异常 orderNo={}", orderDto.getOrderNo(), e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

	/**
	 * 用户交易列表
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryTransactionList", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO queryTransactionList(HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		ResultDTO resultDto = new ResultDTO();
		TransactionQuery query = new TransactionQuery();
		query.setMemberId(getMemberID(req));
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		Page<TransactionForMemberDto> pager = transactionService.queryTransactionList(query);
		resultDto.setResult(pager);
		return resultDto;
	}

	/**
	 * 用户交易列表
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryTransactionList", method = RequestMethod.POST, headers = { "Accept-Version=1.0.1" })
	@ResponseBody
	public ResultDTO queryTransactionListWithActivity(HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		ResultDTO resultDto = new ResultDTO();
		TransactionQuery query = new TransactionQuery();
		query.setMemberId(getMemberID(req));
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		query.setInvestType(ProjectEnum.PROJECT_TYPE_DEBT.getType());
		Page<TransactionForMemberDto> pager = transactionService.queryTransactionListWithActivity(query);
		resultDto.setResult(pager);
		return resultDto;
	}

	/**
	 * 我的交易列表
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMyTransactionList", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO queryMyTransactionList(HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		ResultDTO resultDto = new ResultDTO();
		TransactionQuery query = new TransactionQuery();
		//query.setQueryStatus(TypeEnum.MY_TRANSACTION_UNRASING.getType());我的交易和募集中不区分
		query.setMemberId(getMemberID(req));
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		Page<TransactionForMemberDto> pager = transactionService.queryTransactionListWithP2P(query);
		resultDto.setResult(pager);
		return resultDto;
	}

	/**
	 * 我的募集中列表
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMyRasingList", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO queryMyRasingList(HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		ResultDTO resultDto = new ResultDTO();
		TransactionQuery query = new TransactionQuery();
		//query.setQueryStatus(TypeEnum.MY_TRANSACTION_RASING.getType());我的交易和募集中不区分
		query.setMemberId(getMemberID(req));
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		Page<TransactionForMemberDto> pager = transactionService.queryTransactionListWithP2P(query);
		resultDto.setResult(pager);
		return resultDto;
	}

	/**
	 * 交易详情信息
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryTransactionDetail", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO<TransactionForMemberDto> queryTransactionDetail(HttpServletRequest req, HttpServletResponse resp) {
		Long transactionId = ServletRequestUtils.getLongParameter(req, "transactionId", 0L);
		Long memberId = getMemberID(req);
		ResultDTO<TransactionForMemberDto> result = transactionService.getTransactionDetailForMember(transactionId, memberId);
		return result;
	}

	/**
	 * 收益详情信息
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryTransactionInterests", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	@Deprecated
	public ResultDTO queryTransactionInterests(HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO resultDto = new ResultDTO();
		Long transactionId = ServletRequestUtils.getLongParameter(req, "transactionId", 0L);
		Long memberId = getMemberID(req);
		ResultDTO<TransactionForMemberDto> result = transactionService.getTransactionDetailForMember(transactionId, memberId);
		if (result != null && result.getResult() != null) {
			return transactionInterestService.getTransactionInterestsByTransactionId(transactionId);
		}
		resultDto.setResultCode(ResultCode.SUCCESS);
		return resultDto;
	}

	/**
	 * 合同预览
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/contract/preview", method = RequestMethod.POST)
	public ModelAndView previewContract(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("investAmount") BigDecimal investAmount, @RequestParam("projectId") Long projectId,
			@RequestParam("annualizedRate") BigDecimal annualizedRate) {
		Long memberId = getMemberID(req);
		ResultDTO<ContractBiz> result = transactionService.previewContract(memberId, projectId, investAmount, annualizedRate);
		ModelAndView model = new ModelAndView();
		Integer source = getRequestSource(req);
    	model.addObject("source", source);
		model.setViewName("/contract/contract-preview");
		model.addObject("contract", result.getResult());
	    model.addObject("preview", true);
		return model;
	}

	/**
     * 直投项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/p2pContract/preview")
    public ModelAndView ztPreview(HttpServletRequest req, HttpServletResponse resp) {
    	Long projectId = ServletRequestUtils.getLongParameter(req, "projectId",0L);
    	
    	boolean flag = projectService.isBorrowerTypeEnterprise(projectId);
    	ModelAndView model = new ModelAndView();
    	Integer source = getRequestSource(req);
    	model.addObject("source", source);
        model.addObject("preview", true);
        model.addObject("isEnterprise",flag);
        model.setViewName("/contract/ztContract-preview");
        return model;
    }
    
    /**
     * 直投项目合同查看
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/p2pContract/view160")
    public ModelAndView ztView160(HttpServletRequest req, HttpServletResponse resp , @RequestParam("orderId") Long orderId) {
    	Long memberId = getMemberID(req);
    	ResultDTO<ContractBiz> result = transactionService.p2pViewContract(orderId, memberId);
    	ModelAndView model = new ModelAndView();
    	
    	Integer source = getRequestSource(req);
    	model.addObject("source", source);
        model.addObject("preview", false);
        model.addObject("contract", result.getResult());
        model.setViewName("/contract/ztContract-preview");
        return model;
    }
    
    /**
     * 直投项目合同查看
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/p2pContract/view")
    public ModelAndView ztView(HttpServletRequest req, HttpServletResponse resp , @RequestParam("orderId") Long orderId) {
    	Long memberId = getMemberID(req);
    	ResultDTO<ContractBiz> result = transactionService.p2pViewContract(orderId, memberId);
    	ModelAndView model = new ModelAndView();
    	
    	Integer source = getRequestSource(req);
    	model.addObject("source", source);
        model.addObject("preview", false);
        model.addObject("contract", result.getResult());
        model.setViewName("/contract/ztContract-preview");
        return model;
    }
	
	/**
	 * 查看合同
	 *
	 * @param req
	 * @param resp
	 * @param password
	 * @param transactionId
	 * @return
	 */
	@RequestMapping(value = "/contract/view160", method = RequestMethod.POST)
	public ModelAndView viewContract160(HttpServletRequest req, HttpServletResponse resp, @RequestParam("orderId") Long orderId) {
		Long memberId = getMemberID(req);
		ResultDTO<ContractBiz> result = transactionService.viewContract(orderId, memberId);
		ModelAndView model = new ModelAndView();
		if (!result.isSuccess()) {
			model.setViewName("redirect:/404");
			return model;
		}
		Integer source = getRequestSource(req);
    	model.addObject("source", source);
		model.setViewName("/contract/contract-preview");
		model.addObject("preview", false);
		model.addObject("contract", result.getResult());
		return model;
	}
	
	/**
	 * 查看合同
	 *
	 * @param req
	 * @param resp
	 * @param password
	 * @param transactionId
	 * @return
	 */
	@RequestMapping(value = "/contract/view", method = RequestMethod.POST)
	public ModelAndView viewContract(HttpServletRequest req, HttpServletResponse resp, @RequestParam("orderId") Long orderId) {
		Long memberId = getMemberID(req);
		ResultDTO<ContractBiz> result = transactionService.viewContract(orderId, memberId);
		ModelAndView model = new ModelAndView();
		if (!result.isSuccess()) {
			model.setViewName("redirect:/404");
			return model;
		}
		Integer source = getRequestSource(req);
    	model.addObject("source", source);
		model.setViewName("/contract/contract-preview");
		model.addObject("preview", false);
		model.addObject("contract", result.getResult());
		return model;
	}


	/**
	 * 设置订单现金券金额
	 * 
	 * @param order
	 * @throws ManagerException
	 */
	private void setOrderCashCouponAmount(Order order) throws ManagerException {
		if (StringUtil.isNotBlank(order.getCashCouponNo())) {
			Coupon coupon = couponManager.getCouponByCouponNo(order.getCashCouponNo());
			if (coupon != null) {
				order.setUsedCouponAmount(coupon.getAmount());
			}
		}
	}

	/**
	 * 查询会员待还本利息数据
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryPendingTransactionInterest", method = RequestMethod.POST, headers = { "Accept-Version=1.0.2" })
	@ResponseBody
	public ResultDTO p2pQueryPendingTransactionInterest(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		ResultDTO result = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		int type = ServletRequestUtils.getIntParameter(req, "type", 1);
		TransactionInterestQuery query = new TransactionInterestQuery();
		query.setMemberId(memberId);
		query.setStatus(0);
		query.setPageSize(20);
		if (type == 1) {// 本金查询
			query.setPrincipal(true);
		}
		query.setCurrentPage(pageNo);
		query.setInvestType(ProjectEnum.PROJECT_TYPE_DEBT.getType());
		Page<TransactionInterestForMember> page = new Page<TransactionInterestForMember>();
		if (type == 2 || type == 1) {
			page = transactionInterestService.queryTransactionInterestForMember(query);
		}
		result.setResult(page);
		return result;
	}
	
	/**
	 * 查询会员待还本利息数据
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryPendingTransactionInterest", method = RequestMethod.POST, headers = { "Accept-Version=1.3.0" })
	@ResponseBody
	public ResultDTO queryPendingTransactionInterest(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		ResultDTO result = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		int type = ServletRequestUtils.getIntParameter(req, "type", 1);
		TransactionInterestQuery query = new TransactionInterestQuery();
		query.setMemberId(memberId);
		query.setStatus(0);
		query.setPageSize(20);
		if (type == 1) {// 本金查询
			query.setPrincipal(true);
		}
		query.setCurrentPage(pageNo);
		Page<TransactionInterestForMember> page = new Page<TransactionInterestForMember>();
		if (type == 2 || type == 1) {
			page = transactionInterestService.queryTransactionInterestForMember(query);
		}
		result.setResult(page);
		return result;
	}
	
	/**
	 * 查询会员未签署合同
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryUnsignList", method = RequestMethod.POST, headers = { "Accept-Version=1.6.0" })
	@ResponseBody
	public ResultDTO queryUnsignList(HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		ResultDTO<ContractSignDto> resultDto = new ResultDTO();
		TransactionQuery query = new TransactionQuery();
		query.setQueryStatus(TypeEnum.MY_TRANSACTION_RASING.getType());
		query.setMemberId(getMemberID(req));
		query.setPageSize(20);
		query.setCurrentPage(pageNo);
		resultDto = transactionService.queryUnsignList(query);
		return resultDto;
	}
	
	/**
     * 一键签署
     * @param transactionId
     * @return
     */
	@RequestMapping(value = "signAllContract", method = RequestMethod.POST, headers = { "Accept-Version=1.6.0" })
	@ResponseBody
    public ResultDTO<Object> signAllContract(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
    	return transactionService.signAllContract(memberId);
    }
    
    /**
     * 手动签署
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "signContract", method = RequestMethod.POST, headers = { "Accept-Version=1.6.0" })
	@ResponseBody
    public ResultDTO<Object> signContract(@RequestParam("transactionId") Long transactionId,HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMemberID(req);
    	Integer source = getRequestSource(req);
    	//针对ios（2）和安卓（1）
    	if(memberId>0){
    		return transactionService.signContract(transactionId,source);
    	}
    	ResultDTO<Object> result = new ResultDTO<Object>();
    	result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
    	result.setIsError();
    	return result;
    }
    
    /**
     * 自动签署单个合同
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "signContractAuto", method = RequestMethod.POST, headers = { "Accept-Version=1.6.0" })
	@ResponseBody
    public ResultDTO<Object> signContractAuto(@RequestParam("transactionId") Long transactionId,HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMemberID(req);
    	return transactionService.signContractAuto(memberId,transactionId);
    }
	
    
    /**
     * 转让交易列表
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "transferList", method = RequestMethod.POST, headers = { "Accept-Version=1.8.0" })
	@ResponseBody
    public ResultDTO<Object> transferList(@ModelAttribute("transferRecordQuery") TransferRecordQuery query ,HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMemberID(req);
    	query.setMemberId(memberId);
    	int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		query.setCurrentPage(pageNo);
    	query.setPageSize(5);
    	return transactionService.transferList(query);
    }
    
    /**
     * 交易相关转让记录
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "transactionTransferList", method = RequestMethod.POST, headers = { "Accept-Version=1.9.0" })
	@ResponseBody
    public ResultDTO<Object> transactionTransferList(@ModelAttribute("transactionId") Long transactionId ,HttpServletRequest req, HttpServletResponse resp) {
    	 ResultDTO<Object> result = new ResultDTO<Object>();
    	
    	TransferRecordQuery query = new TransferRecordQuery();
    	Long memberId = getMemberID(req);
    	query.setMemberId(memberId);
    	query.setTransactionId(transactionId);
    	result =  transactionService.transactionTransferList(query);
    	return result;
    }
    
    
    
    /**
     * 发起转让项目
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "transferToProject", method = RequestMethod.POST, headers = { "Accept-Version=1.8.0"})
	@ResponseBody
    public ResultDTO<Object> transferToProject(@RequestParam("transactionId") Long transactionId,
    		@RequestParam("transferAmount") BigDecimal transferAmount,
    		HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMemberID(req);
    	return transactionService.transferToProject(memberId, transactionId,  transferAmount);
    }
    
    /**
     * 交易是否可以转让
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "ableToTransfer", method = RequestMethod.POST, headers = { "Accept-Version=1.8.0"})
	@ResponseBody
    public ResultDTO<Object> ableToTransfer(@RequestParam("transactionId") Long transactionId,
    		HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMemberID(req);
    	return transactionService.ableToTransfer(memberId, transactionId);
    }
    
    /**
     * 转让项目详情
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "transferInformation", method = RequestMethod.POST, headers = { "Accept-Version=1.8.0"})
	@ResponseBody
    public ResultDTO<Object> transferInformation(@RequestParam("transactionId") Long transactionId,
    		HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMemberID(req);
    	return transactionService.transferInformation(memberId, transactionId);
    }
    
    
  /**
     * 撤销转让项目
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "cancelTransferProject", method = RequestMethod.POST, headers = { "Accept-Version=1.8.0"})
	@ResponseBody
    public ResultDTO<Object> cancelTransferProject(@RequestParam("transactionId") Long transactionId,
    		HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMemberID(req);
    	return transactionService.cancelTransferProject(memberId, transactionId);
    }
    
    
    /**
	 * 查看转让直投合同
	 *
	 * @param req
	 * @param resp
	 * @param password
	 * @param transactionId
	 * @return
	 */
	@RequestMapping(value = "/ztTransferContract/view")
	public ModelAndView ztTransferContract(HttpServletRequest req, HttpServletResponse resp, @RequestParam("orderId") Long orderId) {
		Long memberId = getMemberID(req);
		ResultDTO<TransferContractBiz> result = transactionService.viewZtTransferContract(orderId, memberId);
		ModelAndView model = new ModelAndView();
		if (!result.isSuccess()) {
			model.setViewName("redirect:/404");
			return model;
		}
		Integer source = getRequestSource(req);
    	model.addObject("source", source);
		model.setViewName("/contract/ztTransferContract-preview");
		model.addObject("preview", false);
		model.addObject("transferContractBiz", result.getResult());
		return model;
	}
	
	/**
     * 转让直投项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/ztTransferContract/preview")
    public ModelAndView ztTransferContractPreview(HttpServletRequest req, HttpServletResponse resp) {
    	ModelAndView model = new ModelAndView();
    	Integer source = getRequestSource(req);
    	model.addObject("source", source);
        model.addObject("preview", true);
        model.setViewName("/contract/ztTransferContract-preview");
        return model;
    }
    
    /**
   	 * 查看转让债权合同
   	 *
   	 * @param req
   	 * @param resp
   	 * @param password
   	 * @param transactionId
   	 * @return
   	 */
   	@RequestMapping(value = "/transferContract/view")
   	public ModelAndView transferContract(HttpServletRequest req, HttpServletResponse resp, @RequestParam("orderId") Long orderId) {
   		Long memberId = getMemberID(req);
   		ResultDTO<TransferContractBiz> result = transactionService.transferContract(orderId, memberId);
   		ModelAndView model = new ModelAndView();
   		if (!result.isSuccess()) {
   			model.setViewName("redirect:/404");
   			return model;
   		}
   		Integer source = getRequestSource(req);
       	model.addObject("source", source);
   		model.setViewName("/contract/transferContract-preview");
   		model.addObject("preview", false);
   		model.addObject("transferContractBiz", result.getResult());
   		return model;
   	}
   	
   	/**
     * 转让债权项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/transferContract/preview")
    public ModelAndView transferContract(HttpServletRequest req, HttpServletResponse resp) {
    	ModelAndView model = new ModelAndView();
    	Integer source = getRequestSource(req);
    	model.addObject("source", source);
        model.addObject("preview", true);
        model.setViewName("/contract/transferContract-preview");
        return model;
    }
	
	
}
