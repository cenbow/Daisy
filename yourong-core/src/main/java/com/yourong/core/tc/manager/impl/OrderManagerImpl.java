package com.yourong.core.tc.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yourong.common.constant.ActivityConstant;
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
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.dao.DebtMapper;
import com.yourong.core.ic.dao.ProjectExtraMapper;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.tc.dao.OrderMapper;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.OrderForMember;
import com.yourong.core.tc.model.biz.PayOrderBiz;
import com.yourong.core.tc.model.query.OrderQuery;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.uc.manager.MemberManager;

@Component
public class OrderManagerImpl implements OrderManager {

	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private DebtMapper debtMapper;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private CouponManager couponManager;

	@Autowired
	private DebtManager debtManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Autowired
	private ProjectExtraMapper projectExtraMapper;
	
	@Autowired
	private ActivityManager activityManager;
	
	@Autowired
	private ProjectExtraManager projectExtraManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private TransactionInterestManager transactionInterestManager;

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public Integer insert(Order Order) throws ManagerException {
		try {
			int result = orderMapper.insert(Order);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Order selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return orderMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKeySelective(Order Order)
			throws ManagerException {
		try {
			return orderMapper.updateByPrimaryKeySelective(Order);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getOrderCountCurrentDay(Map<String, Object> param)
			throws ManagerException {
		try {
			return orderMapper.getOrderCountCurrentDay(param);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Order getOrderByTradeNo(String tradeNo) throws ManagerException {
		try {
			return orderMapper.getOrderByTradeNo(tradeNo);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Order getOrderByOrderNo(String orderNo) throws ManagerException {
		try {
			return orderMapper.getOrderByOrderNo(orderNo);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int schedueCloseOrder(Long orderId, int beforeStatus, int afterStatus) throws ManagerException {
		try {
			return orderMapper.schedueCloseOrder(orderId, beforeStatus, afterStatus);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<OrderForMember> selectAllOrderForMember(OrderQuery orderQuery)  throws ManagerException {
		try {
			
			List<OrderForMember> orderForMembers = orderMapper.selectAllOrderForMember(orderQuery);
			//通过项目id和投资额获取年化收益计算预期收益
			long count = orderMapper.selectAllOrderForMemberCount(orderQuery);
			Page<OrderForMember> page = new Page<OrderForMember>();
			page.setData(orderForMembers);
			page.setiDisplayLength(orderQuery.getPageSize());
			page.setPageNo(orderQuery.getCurrentPage());
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Page<OrderForMember> p2pSelectAllOrderForMember(OrderQuery orderQuery)  throws ManagerException {
		try {
			
			List<OrderForMember> orderForMembers = orderMapper.p2pSelectAllOrderForMember(orderQuery);
			//通过项目id和投资额获取年化收益计算预期收益
			long count = orderMapper.p2pSelectAllOrderForMemberCount(orderQuery);
			Page<OrderForMember> page = new Page<OrderForMember>();
			page.setData(orderForMembers);
			page.setiDisplayLength(orderQuery.getPageSize());
			page.setPageNo(orderQuery.getCurrentPage());
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getNoPayOrdeCount(Long memberId) throws ManagerException {
		try {
			return orderMapper.getNoPayOrdeCount(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int cancelOrder(Long orderId, String remarks) throws ManagerException {
		try {
			return orderMapper.cancelOrder(orderId, StatusEnum.ORDER_WAIT_PAY.getStatus(), StatusEnum.ORDER_CLOSED.getStatus(),remarks);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public OrderForMember getOrderForMemberByOrderId(Long orderId, Long memberId)
			throws ManagerException {
		try {
			return orderMapper.getOrderForMemberByOrderId(orderId, memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateStatus(Order order, int status, String remarks)
			throws ManagerException {
		
		try {
			order.setStatus(status);
			order.setRemarks(remarks);
			return this.updateByPrimaryKeySelective(order);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Order> selectSchedueCloseOrder() throws ManagerException {
		try {
			return orderMapper.selectSchedueCloseOrder(StatusEnum.ORDER_WAIT_PAY.getStatus());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Order getOrderByIdForLock(Long id) throws ManagerException {
		try {
			return orderMapper.getOrderByIdForLock(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<OrderForMember> findByPage(Page<OrderForMember> pageRequest,
			Map<String, Object> map) throws ManagerException {
			Page<OrderForMember> page = new Page<OrderForMember>();
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<OrderForMember> orderForMemberList=  orderMapper.findByPage(map);
			int totalCount = orderMapper.findByPageCount(map);
			page.setData(orderForMemberList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Map selectTransPersonByOrderNo(String orderNo)
			throws ManagerException {
		try {
			return orderMapper.selectTransPersonByOrderNo(orderNo);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<Order> selectOrderByCoupon(String couponCode)
			throws ManagerException {
		try {
			return orderMapper.selectOrderByCoupon(couponCode);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	

	@Override
	public boolean orderOfCarPayInOrCarBusinessIsUseCoupon(Long projectId, String couponNo) throws ManagerException {
		boolean flag = false;
		try {
			if(projectId!=null){
				Project project = projectManager.selectByPrimaryKey(projectId);
				if(project!=null && StringUtil.isNotBlank(project.getProjectType())){
					boolean proflag = projectManager.isProjectOfCannotUseCoupon(project.getProjectType());
					//是购车垫资项目，并且使用了优惠券
					if(proflag && StringUtil.isNotBlank(couponNo)){
						flag = true;
					}
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return flag;
	}
	
	@Override
	public boolean orderOfBuyCarIsUseCoupon(Long projectId, String couponNo) throws ManagerException {
		boolean flag = false;
		try {
			if(projectId==null){
				return flag;
			}
			Project project = projectManager.selectByPrimaryKey(projectId);
			if(project==null){
				return flag;
			}
			Debt debt = debtMapper.selectByPrimaryKey(project.getDebtId());
			if(debt!=null && StringUtil.isNotBlank(debt.getGuarantyType())){
				boolean proflag = projectManager.projectOfCanotUseProfitCoupon(debt.getGuarantyType(),debt.getInstalment());//判断是否为购车分期项目
				//是购车分期项目，并且使用了优惠券
				if(proflag && StringUtil.isNotBlank(couponNo)){
					flag = true;
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return flag;
	}

	@Override
	public int getNoPayOrdeCountFilterP2p(Long memberId) throws ManagerException {
		try {
			return orderMapper.getNoPayOrdeCountFilterP2p(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	public int getOrderCountByProject(Long id) throws ManagerException {
		try {
			return orderMapper.getOrderCountByProject(id, StatusEnum.ORDER_WAIT_PROCESS.getStatus());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	public int getTransferOrderCountByProject(Long transferId) throws ManagerException {
		try {
			return orderMapper.getTransferOrderCountByProject(transferId, StatusEnum.ORDER_WAIT_PROCESS.getStatus());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
    public BigDecimal getPayingAmountByProject(Long projectId) throws ManagerException {
    	BigDecimal payingAmount = orderMapper.getOrderSumAmountByProject(projectId, StatusEnum.ORDER_WAIT_PROCESS.getStatus());
    	if(payingAmount==null) {
    		return BigDecimal.ZERO.setScale(2);
    	} else {
    		return payingAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    	}
    }
	
	@Override
    public BigDecimal getTransferPayingAmountByProject(Long transferId) throws ManagerException {
    	BigDecimal payingAmount = orderMapper.getTransferPayingAmountByProject(transferId, StatusEnum.ORDER_WAIT_PROCESS.getStatus());
    	if(payingAmount==null) {
    		return BigDecimal.ZERO.setScale(2);
    	} else {
    		return payingAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    	}
    }
	
	

	@Override
	public ResultDO<Object> getPayOrderDetail(String orderNo, Long memberId) throws ManagerException {
		ResultDO<Object> rDO = new ResultDO<Object>();
		PayOrderBiz payOrderBiz = new PayOrderBiz();
		rDO.setResult(payOrderBiz);
		try {
			if (StringUtil.isBlank(orderNo) && memberId == null) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			Order order = getOrderByOrderNo(orderNo);
			if (order == null) {
				rDO.setResultCode(ResultCode.ORDER_NOT_EXISTS_ERROR);
				return rDO;
			}
			payOrderBiz.setOrderStatus(order.getStatus());
			if (!order.getMemberId().equals(memberId)) {
				rDO.setResultCode(ResultCode.ORDER_NOT_BELONG_TO_MEMBER_ERROR);
				return rDO;
			}
			if (order.getStatus() == StatusEnum.ORDER_CLOSED.getStatus()) {
				rDO.setResultCode(ResultCode.ORDER_FRONT_ORDER_CLOSE);
				return rDO;
			}
			// 订单非待支付状态也不是支付中状态
			if (order.getStatus() != StatusEnum.ORDER_WAIT_PAY.getStatus()
					&& order.getStatus() != StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus()) {
				rDO.setResultCode(ResultCode.ORDER_PAY_STATUS_ERROR);
				return rDO;
			}
			payOrderBiz.setSavingPotBalance(balanceManager.queryFromThirdPay(order.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY)
					.getAvailableBalance());
			payOrderBiz.setInvestAmount(order.getInvestAmount());
			payOrderBiz.setMemberId(order.getMemberId());
			payOrderBiz.setOrderId(order.getId());
			payOrderBiz.setOrderNo(order.getOrderNo());
			payOrderBiz.setProjectId(order.getProjectId());
			payOrderBiz.setOrderPayAmount(order.getPayAmount());
			payOrderBiz.setOrderSavingPotAmount(order.getUsedCapital());
			payOrderBiz.setOrderTime(DateUtils.getStrFromDate(order.getOrderTime(), DateUtils.TIME_PATTERN));
			payOrderBiz.setProjectCategory(order.getProjectCategory());
			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			payOrderBiz.setProjectName(project.getName());
			payOrderBiz.setIsDirectProject(project.isDirectProject());
			payOrderBiz.setTotalNumber(getTotalLotteryNumber(order,project));
			payOrderBiz.setLotteryEndCountDown(getLotteryEndCountDown(order, project));
			payOrderBiz.setExtraInterestDay(order.getExtraInterestDay());
			// 转让项目订单
			if (order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				TransferProject tPro = transferProjectManager.selectByPrimaryKey(order.getTransferId());
				payOrderBiz.setProfitDays(transferProjectManager.getReturnDay(tPro.getTransactionId()));
				payOrderBiz.setInterestFrom(tPro.getInterestFrom());
				payOrderBiz.setAnnualizedRate(order.getAnnualizedRate());
				// 计算预期收益
				payOrderBiz.setExpectAmount(FormulaUtil.calculateInterest(order.getInvestAmount(), order.getAnnualizedRate(),
						transferProjectManager.getReturnDay(tPro.getTransactionId())));
				// 产品价值
//				BigDecimal projectValue = order.getTransferPrincipal().multiply(tPro.getProjectValue())
//						.divide(tPro.getTransactionAmount(), 2, BigDecimal.ROUND_HALF_UP);
//				payOrderBiz.setProjectValue(projectValue);
				payOrderBiz.setTransferPrincipal(order.getTransferPrincipal());
				payOrderBiz.setTransferId(order.getTransferId());
				payOrderBiz.setOriginalProEndDate(DateUtils.getStrFromDate(project.getEndDate(), DateUtils.DATE_FMT_3));
				return rDO;
			}
			payOrderBiz.setProfitPeriod(project.getProfitPeriod());
			payOrderBiz.setInterestFrom(project.getInterestFrom());
			// 订单是否已经锁定了现金券
			if (StringUtil.isNotBlank(order.getCashCouponNo())) {
				Coupon coupon = couponManager.getCouponByCouponNo(order.getCashCouponNo());
				if (coupon == null) {
					rDO.setResultCode(ResultCode.COUPON_NOT_EXIST_ERROR);
					return rDO;
				}
				payOrderBiz.setOrderCashEndDate(DateUtils.getStrFromDate(coupon.getEndDate(), DateUtils.DATE_FMT_3));
				payOrderBiz.setOrderUsedCashAmount(coupon.getAmount());
				payOrderBiz.setOrderUsedCashNo(order.getCashCouponNo());
			} else {
				// 加载可用优惠券列表
				int days = 0;
				if (project.isDirectProject()) {
					days = project.countProjectDays();
				} else {
					days = project.getEarningsDaysByStatus();
				}
				// 非受限使用优惠券用户
				if (couponManager.useCouponSpecialLimit(memberId)) {
					List<Coupon> coupons = couponManager.getUsableAndLimitedCoupons(order.getMemberId(),
							TypeEnum.COUPON_TYPE_CASH.getType(), CouponEnum.COUPON_CLIENT_WEB.getCode(), order.getInvestAmount(), days);
					payOrderBiz.setCoupons(coupons);
				}
			}
			// 加载预期收益
			if (project.isDirectProject()) {
				p2pProjectCheck(project, order, payOrderBiz);
			} else {
				debtExpectAmount(project, order, payOrderBiz);
			}
			return rDO;
		} catch (Exception e) {
			throw new ManagerException(e);
		} 
	}
	
	/**
	 * 获取抽奖次数
	 * @param order
	 * @param project
	 * @return
	 */
	public Integer getTotalLotteryNumber(Order order,Project project){
		ProjectExtra pe = projectExtraMapper.getProjectActivitySign(order.getProjectId());
		if(pe==null){
			return 0 ;
		}
		int totalNumber=0;
		//募集时间超过奖励期限
		int totalDays=DateUtils.getIntervalDays(DateUtils.formatDate(project.getOnlineTime(), DateUtils.DATE_FMT_3),
				DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))+1;
		List<PrizePool> prizePoolList =projectExtraManager.getPrizePoolByProjectId(order.getProjectId());
		Integer maxDay = 0;
		if(Collections3.isNotEmpty(prizePoolList)){
			for (PrizePool pri : prizePoolList) {
				if (Float.parseFloat(pri.getRatio())<= 0) {
					continue;
				}
				if (maxDay < pri.getDay()) {
					maxDay = pri.getDay();
				}
			}
			if(totalDays>maxDay){
				return totalNumber;
			}
		}
		
		try {
			Activity activity = activityManager.selectByPrimaryKey(pe.getActivityId());
			if(activity==null){
				return 0 ;
			}
			//抽奖次数
			int number=order.getInvestAmount().divide(project.getMinInvestAmount()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			//额外抽奖次数
			int extraNumber=0;
			Map map = JSON.parseObject(activity.getObtainConditionsJson()); 
			String lotteryJson = map.get("lottery").toString();
			List<LotteryRuleAmountNumber> instance =JSON.parseArray(lotteryJson,LotteryRuleAmountNumber.class); 
			if(Collections3.isNotEmpty(instance)){
				for(LotteryRuleAmountNumber bean :instance){
					if(order.getInvestAmount().intValue()>=bean.getStartAmount()&&order.getInvestAmount().intValue()<=bean.getEndAmount()){
						extraNumber=bean.getNumber();
						break;
						
					}
				}
				if(order.getInvestAmount().intValue()>=instance.get(instance.size()-1).getStartAmount().intValue()){
					extraNumber=instance.get(instance.size()-1).getNumber();
				}
			}
			totalNumber= number+extraNumber;
		} catch (Exception e) {
			logger.error("获取抽奖次数出错" , e);
		}
		return totalNumber;
	}
	
	
	
	/**
	 * 抽奖倒计时
	 * @param order
	 * @param project
	 * @return
	 */
	public Integer getLotteryEndCountDown(Order order,Project project){
		Integer lotteryEndCountDown = 0;
		try {
			ProjectExtra pe = projectExtraMapper.getProjectActivitySign(order.getProjectId());
			if(pe==null){
				return lotteryEndCountDown ;
			}
			
			//抽奖倒计时
			String rewardHour = projectExtraManager.getRewardHourByProjectId(order.getProjectId()); 
			Date lotteryEndTime = DateUtils.addHour(project.getOnlineTime(),Integer.valueOf(rewardHour));
			lotteryEndCountDown = DateUtils.getTimeIntervalSencond(new Date(),lotteryEndTime);
			
		} catch (Exception e) {
			logger.error("获取抽奖倒计时错误" , e);
		}
		return lotteryEndCountDown;
	}
	
	public void p2pProjectCheck(Project project, Order order, PayOrderBiz payOrderBiz) throws ManagerException {
		BigDecimal extraAnnualizedRate = BigDecimal.ZERO;
		boolean hasExtraAnnualizedRate = false;
		if (order.getExtraAnnualizedRate() != null) {
			hasExtraAnnualizedRate = true;
			extraAnnualizedRate = order.getExtraAnnualizedRate();
		}
		BigDecimal expectAmount = projectManager.invertExpectEarnings(project, order.getInvestAmount(), order.getOrderTime(), extraAnnualizedRate);
		payOrderBiz.setExpectAmount(expectAmount);
		//项目加息默认处理为0.00
		payOrderBiz.setAnnualizedRate(order.getAnnualizedRate().add(order.getExtraProjectAnnualizedRate()));
		payOrderBiz.setProfitDays(project.countProjectDays());
		if (hasExtraAnnualizedRate) {
			BigDecimal extraExpectAmount = projectManager.invertExpectEarningsByRate(project, order.getInvestAmount(), order.getOrderTime(),
					extraAnnualizedRate);
			payOrderBiz.setExtraAnnualizedRate(extraAnnualizedRate);
			if (extraExpectAmount.compareTo(BigDecimal.ZERO) == -1) {
				extraExpectAmount = BigDecimal.ZERO;
			}
			payOrderBiz.setExtraExpectAmount(extraExpectAmount);
		}
		//高额收益券收益
		if(order.getExtraInterestDay()!=null&&order.getExtraInterestDay()>0){
			payOrderBiz.setExtraExpectAmount(FormulaUtil.calculateInterest(order.getInvestAmount(),order.getExtraAnnualizedRate(),order.getExtraInterestDay()));
			expectAmount = projectManager.invertExpectEarnings(project, order.getInvestAmount(), order.getOrderTime(), BigDecimal.ZERO);
			if(payOrderBiz.getExtraExpectAmount()==null){
				payOrderBiz.setExpectAmount( BigDecimal.ZERO);
			}
			payOrderBiz.setExpectAmount(expectAmount.add(payOrderBiz.getExtraExpectAmount()));
		}
		
	}

	public void debtExpectAmount(Project project, Order order, PayOrderBiz payOrderBiz) throws ManagerException {
		int profitDays = 0;
		BigDecimal expectAmount = BigDecimal.ZERO;
		BigDecimal extraExpectAmount = BigDecimal.ZERO;
		List<DebtInterest> debtInterests = debtManager.getFullDebtInfoById(project.getDebtId()).getDebtInterests();
		BigDecimal extraAnnualizedRate = BigDecimal.ZERO;
		BigDecimal annualizedRate = BigDecimal.ZERO;
		annualizedRate = order.getAnnualizedRate();
		boolean hasExtraAnnualizedRate = false;
		if (order.getExtraAnnualizedRate() != null) {
			hasExtraAnnualizedRate = true;
			extraAnnualizedRate = order.getExtraAnnualizedRate();
			annualizedRate =  annualizedRate.add(extraAnnualizedRate);
		}
		int period = 0;
		Date startInterestDate = DateUtils.addDate(DateUtils.formatDate(order.getOrderTime(), DateUtils.DATE_FMT_3),
				project.getInterestFrom());
		if (Collections3.isNotEmpty(debtInterests)) {
			// 如果某一期的结束时间早于开始计息时间，则不记录收益和收益天数
			for (DebtInterest debtInterest : debtInterests) {
				if (startInterestDate.after(debtInterest.getEndDate())) {
					continue;
				}
				int days = this.getDays(startInterestDate, debtInterest);
				// 后期需要根据收益类型不同而采用不同的计算方式，目前只有按日计息，按月付息
				expectAmount = this.getExpectAmountFromdebtInterest(order, project, expectAmount, days, debtInterest,
						period, startInterestDate, annualizedRate);
				if (hasExtraAnnualizedRate) {
					extraExpectAmount = this.getExpectAmountFromdebtInterest(order, project, extraExpectAmount, days,
							debtInterest, period, startInterestDate, extraAnnualizedRate);
				}
				profitDays += days;
				period = period + 1;
			}
		}
		payOrderBiz.setExpectAmount(expectAmount);
		payOrderBiz.setAnnualizedRate(order.getAnnualizedRate());
		payOrderBiz.setProfitDays(profitDays);
		if (hasExtraAnnualizedRate) {
			payOrderBiz.setExtraAnnualizedRate(extraAnnualizedRate);
			payOrderBiz.setExtraExpectAmount(extraExpectAmount);
		}
	}

	private BigDecimal getExpectAmountFromdebtInterest(Order order, Project project, BigDecimal expectAmount, int days,
			DebtInterest debtInterest, int period, Date startInterestDate,
			BigDecimal annualizedRate) {
		// 后期需要根据收益类型不同而采用不同的计算方式，目前只有按日计息，按月付息
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())
				|| DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
			// 单位利息
			BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(), order.getInvestAmount(),
					annualizedRate.setScale(2, BigDecimal.ROUND_HALF_UP));
			BigDecimal value = (unitInterest.multiply(new BigDecimal(days))).setScale(2, BigDecimal.ROUND_HALF_UP);
			expectAmount = expectAmount.add(value);
		} else if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode()// 还款方式：等本等息
				.equals(project.getProfitType())) {
			BigDecimal interest = BigDecimal.ZERO;
			interest = FormulaUtil.getTransactionInterest(project.getProfitType(), order.getInvestAmount(),
					annualizedRate.setScale(2, BigDecimal.ROUND_HALF_UP), period, debtInterest.getStartDate(), startInterestDate,
					debtInterest.getEndDate());// 应付利息
			expectAmount = expectAmount.add(interest);
		}
		return expectAmount;
	}

	private int getDays(Date startInterestDate, DebtInterest debtInterest) {
		int days = 0;
		if (DateUtils.isDateBetween(startInterestDate, debtInterest.getStartDate(), debtInterest.getEndDate())) {
			days = DateUtils.daysOfTwo(startInterestDate, debtInterest.getEndDate()) + 1;
		}
		if (startInterestDate.after(debtInterest.getEndDate())) {
			days = 0;
		}
		if (startInterestDate.before(debtInterest.getStartDate())) {
			days = DateUtils.daysOfTwo(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
		}
		return days;
	}

	@Override
	public int getHandleingOrderCount(Long memberId, Integer investFlag) throws ManagerException {
		try {
			return orderMapper.getHandleingOrderCount(memberId, investFlag);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int closeDirectProjectOrder(Long orderId, String remarks) throws ManagerException {
		try {
			return orderMapper.cancelOrder(orderId, StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus(), StatusEnum.ORDER_CLOSED.getStatus(),remarks);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ResultDO<Order> preCreateOrderValidate(Order order) {
		try {
			// 根据项目大类校验
			if (order.getProjectCategory() != null && order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				return preCreateOrderValidateForTransferProject(order);
			} else {
				return preCreateOrderValidateForProject(order);
			}
		} catch (Exception e) {
			ResultDO<Order> rDO = new ResultDO<Order>();
			logger.error("前台保存订单前置条件验证失败，Order={}", order, e);
			rDO.setResultCode(ResultCode.ORDER_FRONT_SAVE_FAIL_ERROR);
			return rDO;
		}
	}
	
	private ResultDO<Order> preCreateOrderValidateForProject(Order order) throws Exception {
		ResultDO<Order> result = new ResultDO<Order>();
		// 判断项目是否存在，项目是否为可销售状态
		Project project = projectManager.selectByPrimaryKey(order.getProjectId());
		if (project == null || (project.getDelFlag() != null && project.getDelFlag().intValue() < 1)) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.PROJECT_NOT_EXIST_ERROR.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
			return result;
		}
		// carPayIn、carBusiness不允许使用优惠券
		boolean orderOfCarPayInIsUseCoupon = orderOfCarPayInOrCarBusinessIsUseCoupon(order.getProjectId(), order.getProfitCouponNo());
		if (orderOfCarPayInIsUseCoupon) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.PROJECT_CARPAYIN_CANOT_USE_COUPON.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.PROJECT_CARPAYIN_CANOT_USE_COUPON);
			return result;
		}
		// buyCar不支持使用收益券
		if (orderOfBuyCarIsUseCoupon(order.getProjectId(), order.getProfitCouponNo())) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.PROJECT_BUYCAR_CANOT_USE_COUPON.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.PROJECT_BUYCAR_CANOT_USE_COUPON);
			return result;
		}
		if (project.getStatus() != StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.PROJECT_NOT_INVESTING_STATUS_ERROR.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.PROJECT_NOT_INVESTING_STATUS_ERROR);
			return result;
		}
		BigDecimal investAmount = order.getInvestAmount();
		// 判断投资金额是否大于0
		if (investAmount.doubleValue() <= 0) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.ORDER_FRONT_INVESTAMOUNT_MUST_GREATER_THAN_ZERO.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.ORDER_FRONT_INVESTAMOUNT_MUST_GREATER_THAN_ZERO);
			return result;
		}
		Long memberId = order.getMemberId();
		// 判断该用户在今天时候已经保存过10个订单，如果超过10个，则返回，您的投资操作过于频繁，为了您的资金安全，请您明日再试
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("memberId", memberId);
		param.put("projectId", order.getProjectId());
		if (getOrderCountCurrentDay(param) >= 10) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.ORDER_FRONT_ORDER_THAN_TEN.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.ORDER_FRONT_ORDER_THAN_TEN);
			return result;
		}
		// 投资金额小于起投金额，则返回
		double minInvestAmount = project.getMinInvestAmount().doubleValue();
		if (minInvestAmount > investAmount.doubleValue()) {
			result.setResultCode(ResultCode.ORDER_FRONT_INVESTAMOUNT_LESS_MINIVESTMOUNT);
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.ORDER_FRONT_INVESTAMOUNT_LESS_MINIVESTMOUNT.getMsg() + ", order=" + order);
			return result;
		}
		// 如果(用户投资总额-起投金额)/递增金额 余数等于0
		double incrementAmount = project.getIncrementAmount().doubleValue();
		if ((investAmount.doubleValue() - minInvestAmount) % incrementAmount != 0) {
			result.setResultCode(ResultCode.TRANSACTION_INCREMENT_AMOUNT_ERROR);
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.TRANSACTION_INCREMENT_AMOUNT_ERROR.getMsg() + ", order=" + order);
			return result;
		}
		// 新客校验
		if (project.isNoviceProject()) {
			boolean checkNoviceFlag = memberManager.needCheckNoviceProject(memberId);
			if (checkNoviceFlag && transactionManager.getTransactionCountByMember(memberId) > 0) {
				result.setResultCode(ResultCode.ORDER_ONLY_ALLOWING_NEW_USERS_ERROR);
				logger.info("前台保存订单前置条件验证失败：" + ResultCode.ORDER_ONLY_ALLOWING_NEW_USERS_ERROR.getMsg() + ", order=" + order);
				return result;
			}
		}
		// 项目余额为0
		Balance projectBalance = balanceManager.queryBalanceLocked(order.getProjectId(), TypeEnum.BALANCE_TYPE_PROJECT);
		if (projectBalance != null) {
			if (projectBalance.getAvailableBalance() != null && projectBalance.getAvailableBalance().doubleValue() == 0) {
				logger.info("前台保存订单前置条件验证失败：" + ResultCode.PROJECT_BALANCE_ZERO_ERROR.getMsg() + ", order=" + order);
				result.setResultCode(ResultCode.PROJECT_BALANCE_ZERO_ERROR);
				return result;
			}
		}
		// 判断项目余额是否足够
		if (projectBalance.getAvailableBalance() != null
				&& projectBalance.getAvailableBalance().doubleValue() < order.getInvestAmount().doubleValue()) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.PROJECT_BALANCE_LACEING_ERROR.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.PROJECT_BALANCE_LACEING_ERROR);
			return result;
		}
		return result;
	}

	private ResultDO<Order> preCreateOrderValidateForTransferProject(Order order) throws Exception {
		ResultDO<Order> result = new ResultDO<Order>();
		// 判断项目是否存在，项目是否为可销售状态
		if (order.getTransferId() == null) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_NOT_EXISTS_ERROR.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.TRANSFER_PROJECT_NOT_EXISTS_ERROR);
			return result;
		}
		TransferProject project = transferProjectManager.selectByPrimaryKey(order.getTransferId());
		if (project == null || (project.getDelFlag() != null && project.getDelFlag().intValue() < 1)) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.PROJECT_NOT_EXIST_ERROR.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
			return result;
		}
		// 校验是否流标
		if (project.getStatus() == StatusEnum.TRANSFER_PROJECT_STATUS_LOSE.getStatus()
				|| DateUtils.getCurrentDate().after(project.getTransferEndDate())) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_HAS_LOST.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.TRANSFER_PROJECT_HAS_LOST);
			return result;
		}
		// 校验项目状态
		if (project.getStatus() != StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.PROJECT_NOT_INVESTING_STATUS_ERROR.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.PROJECT_NOT_INVESTING_STATUS_ERROR);
			return result;
		}
		// 不允许使用优惠券
		if (StringUtil.isNotBlank(order.getCashCouponNo()) || StringUtil.isNotBlank(order.getProfitCouponNo())) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_EXISTS_COUPON_ERROR.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.TRANSFER_PROJECT_EXISTS_COUPON_ERROR);
			return result;
		}
		// 判断当天是否有还款（普通还款只需要根据转让项目结束时间判断，这里判断提前还款）
		TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
		transactionInterestQuery.setTransactionId(project.getTransactionId());
		transactionInterestQuery.setCurdate(true);
		List<TransactionInterest> earlyPayList = transactionInterestManager.queryEarlyInterest(transactionInterestQuery);
		if (Collections3.isNotEmpty(earlyPayList)) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_EXISTS_EARLY_PAYMENT_ERROR.getMsg() + ", order="
					+ order);
			result.setResultCode(ResultCode.TRANSFER_PROJECT_EXISTS_EARLY_PAYMENT_ERROR);
			return result;
		}
		BigDecimal transferPrincipal = order.getTransferPrincipal();
		// 判断投资金额是否大于0
		if (transferPrincipal == null || transferPrincipal.doubleValue() <= 0) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.ORDER_FRONT_TRANSFER_PRINCIPAL_MUST_GREATER_THAN_ZERO.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.ORDER_FRONT_TRANSFER_PRINCIPAL_MUST_GREATER_THAN_ZERO);
			return result;
		}
		Long memberId = order.getMemberId();
		if (memberId.equals(project.getMemberId())) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.TRANSFER_PROJECT_OWNER_INVEST_ERROR.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.TRANSFER_PROJECT_OWNER_INVEST_ERROR);
			return result;
		}
		// 判断该用户在今天时候已经保存过10个订单，如果超过10个，则返回，您的投资操作过于频繁，为了您的资金安全，请您明日再试
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("memberId", memberId);
		param.put("projectId", order.getProjectId());
		if (getOrderCountCurrentDay(param) >= 10) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.ORDER_FRONT_ORDER_THAN_TEN.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.ORDER_FRONT_ORDER_THAN_TEN);
			return result;
		}
		// 判断可认购本金是否为零
		Balance transactionBalance = balanceManager.queryBalanceLocked(project.getTransactionId(),
				TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT);
		if (transactionBalance == null || transactionBalance.getBalance().compareTo(BigDecimal.ZERO) < 1) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.PROJECT_BALANCE_ZERO_ERROR.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.PROJECT_BALANCE_ZERO_ERROR);
			return result;
		}
		// 认购本金小于最小认购金额，则返回
		double unitSubscriptionAmount = project.getUnitSubscriptionAmount().doubleValue();
		if (unitSubscriptionAmount > transferPrincipal.doubleValue()) {
			result.setResultCode(ResultCode.ORDER_FRONT_TRANSFER_PRINCIPAL_LESS_MINPRINCIPAL);
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.ORDER_FRONT_TRANSFER_PRINCIPAL_LESS_MINPRINCIPAL.getMsg() + ", order=" + order);
			return result;
		}
		// 如果用户投资总额/递增金额 余数等于0
		if (transferPrincipal.doubleValue() % unitSubscriptionAmount != 0) {
			result.setResultCode(ResultCode.TRANSFER_TRANSACTION_INCREMENT_AMOUNT_ERROR);
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.TRANSFER_TRANSACTION_INCREMENT_AMOUNT_ERROR.getMsg() + ", order=" + order);
			return result;
		}
		// 判断项目余额是否足够
		if (transactionBalance.getAvailableBalance().doubleValue() < transferPrincipal.doubleValue()) {
			logger.info("前台保存订单前置条件验证失败：" + ResultCode.PROJECT_BALANCE_LACEING_ERROR.getMsg() + ", order=" + order);
			result.setResultCode(ResultCode.PROJECT_BALANCE_LACEING_ERROR);
			return result;
		}
		// 计算投资本金
		BigDecimal investAmount = order.getTransferPrincipal().multiply(project.getTransferAmount())
				.divide(project.getSubscriptionPrincipal(), 2, BigDecimal.ROUND_HALF_UP);
		order.setInvestAmount(investAmount);
		return result;
	}
}