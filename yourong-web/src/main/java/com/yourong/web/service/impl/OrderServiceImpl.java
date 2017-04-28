package com.yourong.web.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.CouponEnum;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.biz.OrderForMember;
import com.yourong.core.tc.model.query.OrderQuery;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.web.service.BaseService;
import com.yourong.web.service.OrderService;

@Service
public class OrderServiceImpl extends BaseService implements OrderService {
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private ProjectManager projectManager;
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private MemberManager memberManager;
	@Autowired
	private DebtManager debtManager;
	@Autowired
	private BalanceManager balanceManager;
	@Autowired
	private TransactionInterestManager transactionInterestManager;
	@Autowired
	private TransactionManager transactionManager;
	@Autowired
	private TransferProjectManager transferProjectManager;
	@Autowired
	private ProjectExtraManager projectExtraManager;
	

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Order> saveOrder(Order order) throws Exception{
		ResultDO<Order> result = new ResultDO<Order>();
		
		try {
			//创建订单前的校验
			result = orderManager.preCreateOrderValidate(order);
			if(!result.isSuccess()) {
				return result;
			}
			// 项目名称
			String projectName = "";
			// 订单年化
			BigDecimal annualizedRate = null;
			Project sourceProject = projectManager.selectByPrimaryKey(order.getProjectId());
			if (order.getProjectCategory() != null && order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				TransferProject project = transferProjectManager.selectByPrimaryKey(order.getTransferId());
				projectName = project.getTransferName();
				annualizedRate = transferProjectManager.getTransferProjectAnnualized(order.getTransferId());
			} else {
				projectName = sourceProject.getName();
				annualizedRate = projectManager.getAnnualizedRateByProjectIdAndInvestAmount(order.getInvestAmount(), order.getProjectId());
				
				//是否为加息项目
				ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(order.getProjectId());
				if(projectExtraAddRate!=null){
					order.setExtraProjectAnnualizedRate(projectExtraAddRate.getExtraAmount());
				}
			}
			order.setProjectName(projectName);
			// 生成trade_no
			order.setOrderNo(SerialNumberUtil.generateOrderNo(order
					.getMemberId()));
			// 初始订单状态为待支付状态
			order.setStatus(StatusEnum.ORDER_WAIT_PAY.getStatus());
			// 订单年化收益
			order.setAnnualizedRate(annualizedRate);
			// 如果有收益券，则验证收益券是否有效并且查询出收益券收益额
			if(StringUtil.isNotBlank(order.getProfitCouponNo())) {
				result = checkOrderProfitCouponNo(order, false);
				if(result.isSuccess()) {
					Coupon coupon = couponManager.getCouponByCouponNo(order.getProfitCouponNo());
					//使用高额收益券
					if(coupon.getExtraInterestType()==1&&coupon.getExtraInterestDay()>0){
						order.setExtraInterestDay(coupon.getExtraInterestDay());
					}
					order.setExtraAnnualizedRate(coupon.getAmount());
					//将优惠券置为使用中，锁定优惠券
					couponManager.lockCoupon(order.getProfitCouponNo());
				}else{
					return result;
				}
			}
			if (orderManager.insert(order) > 0) {
				Order returnOrder = new Order();
				returnOrder.setOrderNo(order.getOrderNo());
				result.setResult(returnOrder);
			}
		} catch (Exception e) {
			logger.error("前台保存订单失败，order=" + order, e);
			result.setResultCode(ResultCode.ORDER_FRONT_SAVE_FAIL_ERROR);
			throw e;
		}
		return result;
	}

	@Override
	public Order getOrderByOrderNo(String orderNo) {
		try {
			return orderManager.getOrderByOrderNo(orderNo);
		} catch (Exception e) {
			logger.error("通过订单号获取订单失败，orderNo=" + orderNo, e);
		}
		return null;
	}

	@Override
	public Page<OrderForMember> selectAllOrderForMember(OrderQuery orderQuery) {
		try {
			Page<OrderForMember> page = orderManager.selectAllOrderForMember(orderQuery);
			List<OrderForMember> orderForMembers = page.getData();
			//TOTO设置预期收益和收益总天数
			if(Collections3.isEmpty(orderForMembers)) {
				return page;
			}
			for (OrderForMember orderForMember : orderForMembers) {
					Project project = projectManager.selectByPrimaryKey(orderForMember.getProjectId());
					orderForMember.setInvestType(project.getInvestType());
					if(!project.isDirectProject()){
						orderForMember.setExpectAmount(transactionInterestManager.getExpectAmount(
								project, 
								orderForMember.getInvestAmount(), 
								orderForMember.getAnnualizedRate(), 
								orderForMember.getExtraAnnualizedRate()));
					}
					orderForMember.setProfitDays(DateUtils.getIntervalDays(DateUtils.addDate(orderForMember.getOrderTime(), project.getInterestFrom()), project.getEndDate())+1);
			}
			
			return page;
		} catch (Exception e) {
			logger.error("用户中心查询订单分页列表失败，orderQuery=" + orderQuery, e);
		}
		return null;
	}

	
	@Override
	public int getNoPayOrdeCount(Long memberId) {
		try {
			return orderManager.getNoPayOrdeCount(memberId);
		} catch (ManagerException e) {
			logger.error("获取当前用户 还未支付的订单出错，memberId=" + memberId , e);
		}
		return 0;
	}

	@Override
	public ResultDO<Order> cancelOrder(Long orderId, String remarks, Long memberId) {
		ResultDO<Order> result = new ResultDO<Order>();
		try {
			Order order = orderManager.selectByPrimaryKey(orderId);
			// 判断该订单是否属于该会员
			if(order.getMemberId().longValue()!=memberId.longValue()) {
				result.setSuccess(false);
				result.setResultCode(ResultCode.ORDER_NOT_BELONG_TO_MEMBER_ERROR);
				return result;
			}
			if(orderManager.cancelOrder(orderId, remarks)>0) {
				//解锁收益券
				if(StringUtil.isNotBlank(order.getProfitCouponNo())) {
					couponManager.unLockCoupon(order.getProfitCouponNo());
				}
				//解锁现金券
				if(StringUtil.isNotBlank(order.getCashCouponNo())) {
					couponManager.unLockCoupon(order.getCashCouponNo());
				}
				return result;
			}
		} catch (ManagerException e) {
			logger.error("取消订单出错，orderId=" + orderId , e);
			result.setSuccess(false);
			return result;
		}
		return result;
	}

	@Override
	public ResultDO<OrderForMember> getOrderForMember(Long orderId, Long memberId) {
		ResultDO<OrderForMember> result = new ResultDO<OrderForMember>();
		try {
			OrderForMember orderForMember = orderManager.getOrderForMemberByOrderId(orderId, memberId);
			if(orderForMember!=null) {
				//设置预期年化收益，收益天数，预期收益
				BigDecimal expectAmount = BigDecimal.ZERO;
				int profitDays = 0;
				Project project = projectManager.selectByPrimaryKey(orderForMember.getProjectId());
				orderForMember.setInvestType(project.getInvestType());
				
				//项目加息默认处理为0.00
				orderForMember.setAnnualizedRate(orderForMember.getAnnualizedRate().add(orderForMember.getExtraProjectAnnualizedRate()));
				
				if(orderForMember.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
					TransferProject transferProject =transferProjectManager.selectByPrimaryKey(orderForMember.getTransferId());
					orderForMember.setProfitDays(transferProjectManager.getReturnDay(transferProject.getTransactionId()));
					//预期赚取
					BigDecimal totalInterest = BigDecimal.ZERO;
					if(orderForMember.getStatus()==StatusEnum.ORDER_PAYED_INVESTED.getStatus()){
						Transaction tra = transactionManager.getTransactionByOrderId(orderForMember.getOrderId());
						totalInterest = tra.getTotalInterest().add(tra.getTotalPrincipal()).subtract(tra.getInvestAmount());
						orderForMember.setTotalInterest(totalInterest);
					}else{
						totalInterest = FormulaUtil.calculateInterest(orderForMember.getInvestAmount(), orderForMember.getAnnualizedRate(), transferProjectManager.getReturnDay(transferProject.getTransactionId()));
						orderForMember.setTotalInterest(totalInterest);
					}
				}else{
					if(project.isDirectProject()){		
						//是否为P2P项目
						this.p2pProjectCheck(orderForMember, project,orderId);
					}else{
						this.debtProjectCheck(orderForMember, expectAmount, profitDays, project);
					}
				}
				result.setResult(orderForMember);
			}
		} catch (ManagerException e) {
			logger.error("获取订单详情出错，orderId=" + orderId , e);
			result.setSuccess(false);
			return result;
		}
		return result;
	}
	
	private void debtProjectCheck(OrderForMember orderForMember,BigDecimal expectAmount,int profitDays ,Project project) throws ManagerException{
		
		List<DebtInterest> debtInterests = debtManager.getFullDebtInfoById(project.getDebtId()).getDebtInterests();
		BigDecimal extraAnnualizedRate = BigDecimal.ZERO;
		if(orderForMember.getExtraAnnualizedRate()!=null) {
			extraAnnualizedRate = orderForMember.getExtraAnnualizedRate();
		}
		int period = 0;
		Date startInterestDate = DateUtils.addDate(DateUtils.formatDate(orderForMember.getOrderTime(),  DateUtils.DATE_FMT_3), project.getInterestFrom());
		if(Collections3.isNotEmpty(debtInterests)) {
			// 如果某一期的结束时间早于开始计息时间，则不记录收益和收益天数
			for (DebtInterest debtInterest : debtInterests) {
				if(startInterestDate.after(debtInterest.getEndDate())){
					continue;
				}
				int days = this.getDays(startInterestDate, debtInterest);
				// 后期需要根据收益类型不同而采用不同的计算方式，目前只有按日计息，按月付息
				expectAmount = this.getExpectAmount(orderForMember, project, expectAmount, extraAnnualizedRate, days, debtInterest, period, startInterestDate);
				profitDays += days;
				period = period + 1;
			}
		}
		orderForMember.setExpectAmount(expectAmount);
		orderForMember.setProfitDays(profitDays);
	}
	
	private void p2pProjectCheck(OrderForMember orderForMember,Project project,Long orderId) throws ManagerException{
		orderForMember.setProfitPeriod(project.getProfitPeriod());
		orderForMember.setInterestFrom(project.getInterestFrom());
		//预期收益具体取值，从订单待支付开始，即显示此信息，手工计算
		Order order = orderManager.selectByPrimaryKey(orderId);
		BigDecimal totalInterest =projectManager.invertExpectEarnings(project, order.getInvestAmount(), order.getOrderTime(), order.getExtraAnnualizedRate());
		orderForMember.setExpectAmount(totalInterest);
		if(orderForMember.getExtraInterestDay()!=null&&orderForMember.getExtraInterestDay()>0){
			totalInterest =projectManager.invertExpectEarnings(project, order.getInvestAmount(), order.getOrderTime(), BigDecimal.ZERO);
			BigDecimal extraAnnu=FormulaUtil.calculateInterest(order.getInvestAmount(),order.getExtraAnnualizedRate(),orderForMember.getExtraInterestDay());
			if(extraAnnu==null){
				extraAnnu=BigDecimal.ZERO;
			}
			orderForMember.setExpectAmount(totalInterest.add(extraAnnu));
		}
	}
	
	private int getDays(Date startInterestDate,DebtInterest debtInterest){
		int days = 0;
		if(DateUtils.isDateBetween(startInterestDate, debtInterest.getStartDate(), debtInterest.getEndDate())) {
			days = DateUtils.daysOfTwo(startInterestDate, debtInterest.getEndDate()) + 1;
		} 
		if(startInterestDate.after(debtInterest.getEndDate())) {
			days = 0;
		}
		if(startInterestDate.before(debtInterest.getStartDate())) {
			days = DateUtils.daysOfTwo(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
		}	
		return days;
	}
	
	private BigDecimal getExpectAmount(OrderForMember orderForMember,Project project,BigDecimal expectAmount,	
			BigDecimal extraAnnualizedRate,int days ,DebtInterest debtInterest,int period,Date startInterestDate ){
		// 后期需要根据收益类型不同而采用不同的计算方式，目前只有按日计息，按月付息
		if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())||DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
			// 单位利息
			BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(),
					orderForMember.getInvestAmount(), orderForMember.getAnnualizedRate().add(extraAnnualizedRate).setScale(2, BigDecimal.ROUND_HALF_UP));
			BigDecimal value = (unitInterest.multiply(new BigDecimal(days))).setScale(2, BigDecimal.ROUND_HALF_UP);
			expectAmount = expectAmount.add(value);
		}else if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode()// 还款方式：等本等息 
				.equals(project.getProfitType())) {
			BigDecimal interest = BigDecimal.ZERO;
			interest = FormulaUtil.getTransactionInterest(project.getProfitType(),
					orderForMember.getInvestAmount(),
					orderForMember.getAnnualizedRate().add(extraAnnualizedRate).setScale(2, BigDecimal.ROUND_HALF_UP),
					period,
					debtInterest.getStartDate(),
					startInterestDate,
					debtInterest.getEndDate());//应付利息
			expectAmount = expectAmount.add(interest);
		}
		return expectAmount;
		
	}
	

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Order> updateOrderForRecharge(Order order) throws Exception{
		ResultDO<Order> result = new ResultDO<Order>(order);
		try {
		//判断现金券余额是否与现金券匹配
		if(StringUtil.isNotBlank(order.getCashCouponNo())) {
//			if(coupon==null) {
//				result.setResultCode(ResultCode.COUPON_NOT_EXIST_ERROR);
//				logger.info("[updateOrderForRecharge]前置条件验证失败：" + ResultCode.COUPON_NOT_EXIST_ERROR.getMsg() + ", orderNo=" + order.getOrderNo() + ", CashCouponNo=" + order.getCashCouponNo());
//				return result;
//			}
//			if(StatusEnum.COUPON_STATUS_RECEIVED_NOT_USED.getStatus()!=coupon.getStatus()) {
//				result.setResultCode(ResultCode.COUPON_STATUS_NOT_VALID_ERROR);
//				logger.info("[updateOrderForRecharge]前置条件验证失败：" + ResultCode.COUPON_STATUS_NOT_VALID_ERROR.getMsg() + ", orderNo=" + order.getOrderNo() + ", CashCouponNo=" + order.getCashCouponNo());
//				return result;
//			}
//			if(order.getMemberId().longValue()!=coupon.getHolderId().longValue()) {
//				result.setResultCode(ResultCode.COUPON_HOLDER_NOT_VALID_ERROR);
//				logger.info("[updateOrderForRecharge]前置条件验证失败：" + ResultCode.COUPON_HOLDER_NOT_VALID_ERROR.getMsg() + ", orderNo=" + order.getOrderNo() + ", CashCouponNo=" + order.getCashCouponNo());
//				return result;
//			}
			if(order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				result.setResultCode(ResultCode.TRANSFER_PROJECT_EXISTS_COUPON_ERROR);
				return result;
			}
			result = checkOrderCashCouponNo(order, false);
			if(!result.isSuccess()){
				return result;
			}
			//将优惠券置为使用中，锁定优惠券
			couponManager.lockCoupon(order.getCashCouponNo());
		}
//		order.setStatus(StatusEnum.ORDER_WAIT_PROCESS.getStatus());
		orderManager.updateByPrimaryKeySelective(order);
		} catch (Exception e) {
			logger.error("支付交易时如果需要充值，则先调用次接口更新订单信息出错"  , e);
			result.setSuccess(false);
			throw e;
		}
		return result;
	}
	
	@Override
	public ResultDO<Order> checkOrderCashCouponNo(Order order, boolean checkStatus) {
		ResultDO<Order> rDO = new ResultDO<Order>();
		if (order.getUsedCouponAmount() != null && StringUtil.isBlank(order.getCashCouponNo())) {
			rDO.setResultCode(ResultCode.ORDER_USED_COUPON_AMOUNT_EMPTY_ERROR);
			return rDO;
		}
		if(StringUtil.isNotBlank(order.getCashCouponNo())){
			ResultDO<Coupon> result = checkOrderCoupon(order, order.getCashCouponNo());
			return checkOrderCoupon(result, checkStatus);
		}
		return new ResultDO<Order>();
	}
	
	@Override
	public ResultDO<Order> checkOrderProfitCouponNo(Order order, boolean checkStatus){
		if(StringUtil.isNotBlank(order.getProfitCouponNo())){
			ResultDO<Coupon> result = checkOrderCoupon(order, order.getProfitCouponNo());
			return checkOrderCoupon(result, checkStatus);
		}
		return new ResultDO<Order>();
	}
	
	private ResultDO<Order> checkOrderCoupon(ResultDO<Coupon> result, boolean checkStatus){
		ResultDO<Order> resultOrder = new ResultDO<Order>();
		if(checkStatus && !result.isSuccess()){
			Coupon coupon = result.getResult();
			if(coupon != null){
				int status = coupon.getStatus();
				if(status != StatusEnum.COUPON_STATUS_USING.getStatus()){
					return resultOrder;
				}
			}
		}
		resultOrder.setSuccess(result.isSuccess());
		if(!result.isSuccess()){
			resultOrder.setResultCode(result.getResultCode());
		}
		return resultOrder;
	}
	
	/**
	 * 检查优惠券是否可用
	 * @param order
	 * @param couponNo
	 * @return
	 */
	private ResultDO<Coupon> checkOrderCoupon(Order order, String couponNo){
		ResultDO<Coupon> result = new ResultDO<Coupon>();
		try{
			Coupon coupon = couponManager.getCouponByCouponNo(couponNo);
			if(coupon != null){
				Coupon couponForLock = couponManager.selectCouponforUpdate(coupon.getId());
				Project project = projectManager.selectByPrimaryKey(order.getProjectId());
				int days = 0;
				if (project.isDirectProject()){
					days = project.countProjectDays();
				}else {
					days = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), project.getEndDate())+1 - project.getInterestFrom();
				}
				 couponManager.couponForCurrentInvestIsUsable(result, couponForLock, CouponEnum.COUPON_CLIENT_WEB.getCode(), days, order);				
				result.setResult(couponForLock);
			} else {
				result.setResultCode(ResultCode.ORDER_USED_COUPON_NOT_EXISTS_ERROR);
			}
		} catch (Exception e) {
			result.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("优惠券是否可使用检查异常" , e);
		}
		return result;
	}

	@Override
	public ResultDO<Object> getPayOrderDetail(String orderNo, Long memberId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try{
			rDO = orderManager.getPayOrderDetail(orderNo, memberId);
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("支付订单详情页异常, orderNo={}, memberId={}", orderNo, memberId, e);
		}
		return rDO;
	}

}