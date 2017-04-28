package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rop.thirdparty.com.google.common.collect.Lists;

import com.google.common.collect.Maps;
import com.yourong.api.dto.OrderCouponDto;
import com.yourong.api.dto.OrderDetailForMember;
import com.yourong.api.dto.OrderForAfterInvestDto;
import com.yourong.api.dto.OrderForAppDto;
import com.yourong.api.dto.OrderForMemberDto;
import com.yourong.api.dto.PayOrderDto;
import com.yourong.api.dto.PreliminaryOrderDto;
import com.yourong.api.dto.RecommendProjectDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.ActivityLotteryService;
import com.yourong.api.service.MemberBankCardService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.OrderService;
import com.yourong.api.service.ProjectService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.CouponEnum;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.DirectLotteryResultList;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.biz.ProjectForLotteryReturn;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.biz.OrderForMember;
import com.yourong.core.tc.model.biz.PayOrderBiz;
import com.yourong.core.tc.model.query.OrderQuery;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

@Service
public class OrderServiceImpl implements OrderService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderManager orderManager;
	@Autowired
	private ProjectManager projectManager;
	@Autowired
	private ProjectService projectService;
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
	TransactionManager transactionManager;
	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberBankCardService memberBankCardService;
	@Autowired
	private ActivityLotteryService activityLotteryService;
	@Autowired
	private DebtInterestManager debtInterestManager;
	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Autowired
	private ProjectExtraManager projectExtraManager;
	
	@Override
	public ResultDTO investment(Long projectId, BigDecimal investAmount, Long memberId) {
		ResultDTO resultDTO = new ResultDTO();
		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			if(member== null || member.getDelFlag() <1){
				resultDTO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR); //用户不存在
				return resultDTO;
			}
			Project project = projectManager.selectByPrimaryKey(projectId);
			if(project == null || project.getDelFlag() < 1){
				resultDTO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR); //项目不存在
				return resultDTO;
			}
			if(project.getStatus() != StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
				resultDTO.setResultCode(ResultCode.PROJECT_NOT_INVESTING_STATUS_APP_ERROR); //亲，该项目不可投，换个项目试试吧
				return resultDTO;
			}
			
			BigDecimal annualizedRate = projectManager.getAnnualizedRateByProjectIdAndInvestAmount(investAmount, projectId);
			if(annualizedRate == null){
				resultDTO.setResultCode(ResultCode.PROJECT_INVEST_AMOUNT_ERROR); //投资金额有误
				return resultDTO;
			}
			
			Balance _balance = balanceManager.queryBalance(project.getId(), TypeEnum.BALANCE_TYPE_PROJECT);
			if(_balance != null) {
				if(_balance.getAvailableBalance().compareTo(BigDecimal.ZERO) == 0){
					resultDTO.setResultCode(ResultCode.PROJECT_INVEST_END_ERROR); //项目已被一锤定音
					return resultDTO;
				}
				if(_balance.getAvailableBalance().compareTo(investAmount) < 0){
					resultDTO.setResultCode(ResultCode.PROJECT_BALANCE_LACEING_ERROR); //投资金额有误
					return resultDTO;
				}
			}else{
				resultDTO.setResultCode(ResultCode.ERROR_SYSTEM); //数据异常
				return resultDTO;
			}
			PreliminaryOrderDto preliminaryOrderDto = new PreliminaryOrderDto();
			preliminaryOrderDto.setAnnualizedRate(annualizedRate.setScale(2, BigDecimal.ROUND_HALF_UP));
			preliminaryOrderDto.setInvestAmount(investAmount);
			preliminaryOrderDto.setProjectId(projectId);
			preliminaryOrderDto.setInvestType(project.getInvestType());
			String projectName = project.getName();
			preliminaryOrderDto.setProjectName(projectName.contains("期")?projectName.substring(0, projectName.indexOf("期") + 1):projectName);
			preliminaryOrderDto.setInterestFrom(project.getInterestFrom());
			preliminaryOrderDto.setProfitType(project.getProfitType());
			BigDecimal expectAmount = BigDecimal.ZERO;
			int days = 0;
			if(project.isDirectProject()){
				this.p2pProject(preliminaryOrderDto, project, investAmount, expectAmount, memberId);
				/*int period = this.getPeriod(project);
				expectAmount = projectManager.invertExpectEarnings(project, investAmount, DateUtils.getCurrentDate(), BigDecimal.ZERO);
				preliminaryOrderDto.setEarningPeriod(period);
				preliminaryOrderDto.setExpectAmount(expectAmount);
				List<Coupon> couponList = couponManager.getUsableAndLimitedCoupons(memberId, TypeEnum.COUPON_TYPE_INCOME.getType(),
						CouponEnum.COUPON_CLIENT_APP.getCode(), preliminaryOrderDto.getInvestAmount(), project.countProjectDays());
				preliminaryOrderDto.setCoupons(BeanCopyUtil.mapList(couponList, OrderCouponDto.class));*/
				resultDTO.setResult(preliminaryOrderDto);
				return resultDTO;
			}else{
				days = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), project.getEndDate())+1 - project.getInterestFrom();
				if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())||DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
					// 单位利息
					BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(), investAmount, annualizedRate.setScale(2, BigDecimal.ROUND_HALF_UP));
					expectAmount = (unitInterest.multiply(new BigDecimal(days))).setScale(2, BigDecimal.ROUND_HALF_UP);
					
				}else if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode()// 还款方式：等本等息 
					.equals(project.getProfitType())) {
					List<DebtInterest> debtInterests = debtManager.getFullDebtInfoById(project.getDebtId()).getDebtInterests();
					preliminaryOrderDto.setDebtInterests(debtInterests);
					int period = 0;
					Date startInterestDate = DateUtils.addDate(DateUtils.formatDate(DateUtils.getCurrentDate(),  DateUtils.DATE_FMT_3), project.getInterestFrom());
					if(Collections3.isNotEmpty(debtInterests)) {
						// 如果某一期的结束时间早于开始计息时间，则不记录收益和收益天数
						for (DebtInterest debtInterest : debtInterests){
							if(!startInterestDate.after(debtInterest.getEndDate())){
								BigDecimal interest = BigDecimal.ZERO;
								interest = FormulaUtil.getTransactionInterest(project.getProfitType(),
										investAmount,
										annualizedRate.setScale(2, BigDecimal.ROUND_HALF_UP),
										period,
										debtInterest.getStartDate(),
										startInterestDate,
										debtInterest.getEndDate());//应付利息
								expectAmount = expectAmount.add(interest);
								period = period + 1;
							}
						}
						
					}
				}
			}
			preliminaryOrderDto.setExpectAmount(expectAmount);
			preliminaryOrderDto.setEarningsDays(days);
			//优惠券限制解除
			//if((!project.isNoviceProject() || investAmount.compareTo(new BigDecimal(Constant.NOVICE_INVEST_AMOUNT)) >= 0 )&& !projectManager.isProjectOfCannotUseProfitCoupon(projectId)){
//				List<Coupon> couponList = couponManager.findUsableCouponsByMemberId(memberId, TypeEnum.COUPON_TYPE_INCOME.getType());
				List<Coupon> couponList = couponManager.getUsableAndLimitedCoupons(memberId, TypeEnum.COUPON_TYPE_INCOME.getType(), CouponEnum.COUPON_CLIENT_APP.getCode(), preliminaryOrderDto.getInvestAmount(), preliminaryOrderDto.getEarningsDays());
				//等本等息不允许使用现金券
				/*if(project.getProfitType().equals(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode())) {
					for(Coupon c : couponList) {
						c.setLimited(StatusEnum.COUPON_USE_NO_LIMITED.getStatus());
					}					
				}*/
				preliminaryOrderDto.setCoupons(BeanCopyUtil.mapList(couponList, OrderCouponDto.class));
			//}else{

			//}
			resultDTO.setResult(preliminaryOrderDto);
		} catch (ManagerException e) {
			logger.error("获取创建订单页面信息出错，projectId=" + projectId + ",investAmount=" + investAmount, e);
		}
		return resultDTO;
	}
	

	
	private PreliminaryOrderDto p2pProject(PreliminaryOrderDto preliminaryOrderDto,Project project,BigDecimal investAmount,
			BigDecimal expectAmount,Long memberId) throws ManagerException{
		int period = this.getPeriod(project);
		expectAmount = projectManager.invertExpectEarnings(project, investAmount, DateUtils.getCurrentDate(), BigDecimal.ZERO);
		preliminaryOrderDto.setEarningPeriod(period);
		preliminaryOrderDto.setExpectAmount(expectAmount);
		List<Coupon> couponList = couponManager.getUsableAndLimitedCoupons(memberId, TypeEnum.COUPON_TYPE_INCOME.getType(),
				CouponEnum.COUPON_CLIENT_APP.getCode(), preliminaryOrderDto.getInvestAmount(), project.countProjectDays());
		preliminaryOrderDto.setCoupons(BeanCopyUtil.mapList(couponList, OrderCouponDto.class));
		return preliminaryOrderDto;
	}
	
	private Integer getPeriod(Project project)throws ManagerException{
		Integer interestFrom = project.getInterestFrom();
		//借款周期
		Integer borrowPeriod = project.getBorrowPeriod();
		//借款周期类型（1-日；2-月；3-年）
		Integer borrowPeriodType = project.getBorrowPeriodType();	
		//开始计息日
		Date beginInterestDate = DateUtils.addDate(DateUtils.getCurrentDate(), interestFrom);
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType()) ||
				DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
			//借款周期类型天
			int borrowDays = 0;
			if (borrowPeriodType == 1) {
				borrowDays = borrowPeriod;
			}
			//借款周期类型月
			if (borrowPeriodType == 2) {
				Date endDate = DateUtils.addMonth(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				Date endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			return borrowDays;
		}
		//等本等息 (按月)   不支持 借款周期为天的
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())) {
			int months = 0;
			//借款周期类型月
			if (borrowPeriodType == 2) {
				months = borrowPeriod;
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				months = borrowPeriod * 12;
			}
			return months;
		}
		return 0;
	} 
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDTO<Object> saveOrder(Order order) throws Exception{
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			//创建订单前的校验
			ResultDO<Order> rDO = orderManager.preCreateOrderValidate(order);
			if (!rDO.isSuccess()) {
				result.setResultCode(rDO.getResultCode());
				return result;
			}
			//project
			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			order.setProjectName(project.getName());
			// 生成trade_no
			order.setOrderNo(SerialNumberUtil.generateOrderNo(order.getMemberId()));
			// 初始订单状态为待支付状态
			order.setStatus(StatusEnum.ORDER_WAIT_PAY.getStatus());
			// 根据投资额获取年化收益
			order.setAnnualizedRate(projectManager.getAnnualizedRateByProjectIdAndInvestAmount(order.getInvestAmount(), order.getProjectId()));
			// 如果有收益券，则验证收益券是否有效并且查询出收益券收益额
			if(StringUtil.isNotBlank(order.getProfitCouponNo())) {
//				优惠券限制解除
//				if(project.isNoviceProject() && order.getInvestAmount().compareTo(new BigDecimal(Constant.NOVICE_INVEST_AMOUNT)) < 0){
//					result.setResultCode(ResultCode.COUPON_INCOME_DISABLE_BY_NOVICE_PROJECT_ERROR);
//					return result;
//				}
				//等本等息不能使用优惠券
				/*if(project.getProfitType().equals(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode())) {
					result.setResultCode(ResultCode.COUPON_NOT_VALID_AVG_PRINCIPAL_INTEREST_ERROR);
					return result;
				}*/
				ResultDO<Order> resultDO = checkOrderProfitCouponNo(order, false);
				//判断使用的收益权是否有效
				if(resultDO.isSuccess()) {
					Coupon coupon = couponManager.getCouponByCouponNo(order.getProfitCouponNo());
					order.setExtraAnnualizedRate(coupon.getAmount());
					//将优惠券置为使用中，锁定优惠券
					couponManager.lockCoupon(order.getProfitCouponNo());
				}else{
					result.setResultCode(resultDO.getResultCode());
					return result;
				}
			}
			if (orderManager.insert(order) > 0) {
				result.setResult(order);
//				PushClient.pushMsgToMember("您有新的一笔订单，订单号："+order.getOrderNo()+",投资金额："+order.getInvestAmount(), order.getMemberId(), order.getOrderNo(), PushEnum.CREATE_ORDER);
			}
		} catch (Exception e) {
			logger.error("前台保存订单失败，order=" + order, e);
			result.setResultCode(ResultCode.ORDER_FRONT_SAVE_FAIL_ERROR);
			throw e;
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDTO<Object> saveOrderSina(Order order) throws Exception{
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			//创建订单前的校验
			ResultDO<Order> rDO = orderManager.preCreateOrderValidate(order);
			if (!rDO.isSuccess()) {
				result.setResultCode(rDO.getResultCode());
				return result;
			}
			// 项目名称
			String projectName = "";
			// 订单年化
			BigDecimal annualizedRate = null;
			if (order.getProjectCategory() != null && order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				TransferProject project = transferProjectManager.selectByPrimaryKey(order.getTransferId());
				projectName = project.getTransferName();
				annualizedRate = transferProjectManager.getTransferProjectAnnualized(order.getTransferId());
			} else {
				Project sourceProject = projectManager.selectByPrimaryKey(order.getProjectId());
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
			order.setOrderNo(SerialNumberUtil.generateOrderNo(order.getMemberId()));
			// 初始订单状态为待支付状态
			order.setStatus(StatusEnum.ORDER_WAIT_PAY.getStatus());
			// 订单年化收益
			order.setAnnualizedRate(annualizedRate);
			// 如果有收益券，则验证收益券是否有效并且查询出收益券收益额
			if(StringUtil.isNotBlank(order.getProfitCouponNo())) {
				ResultDO<Order> resultDO = checkOrderProfitCouponNo(order, false);
				//判断使用的收益权是否有效
				if(resultDO.isSuccess()) {
					Coupon coupon = couponManager.getCouponByCouponNo(order.getProfitCouponNo());
					order.setExtraAnnualizedRate(coupon.getAmount());
					//使用高额收益券
					if(coupon.getExtraInterestType()==1&&coupon.getExtraInterestDay()>0){
						order.setExtraInterestDay(coupon.getExtraInterestDay());
					}
					//将优惠券置为使用中，锁定优惠券
					couponManager.lockCoupon(order.getProfitCouponNo());
				}else{
					result.setResultCode(resultDO.getResultCode());
					return result;
				}
			}
			if (orderManager.insert(order) > 0) {
				result.setResult(order);
//				PushClient.pushMsgToMember("您有新的一笔订单，订单号："+order.getOrderNo()+",投资金额："+order.getInvestAmount(), order.getMemberId(), order.getOrderNo(), PushEnum.CREATE_ORDER);
			}
		} catch (Exception e) {
			logger.error("前台保存订单失败，order=" + order, e);
			result.setResultCode(ResultCode.ORDER_FRONT_SAVE_FAIL_ERROR);
			throw e;
		}
		return result;
	}

	@Override
	public ResultDTO<Order> cancelOrder(Long orderId, String remarks, Long memberId) {
		ResultDTO<Order> result = new ResultDTO<Order>();
		try {
			Order order = orderManager.selectByPrimaryKey(orderId);
			if(order != null){
				// 判断该订单是否属于该会员
				if(order.getMemberId().longValue()!=memberId.longValue()) {
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
					result.setIsSuccess();
					return result;
				}else{
					result.setResultCode(ResultCode.ORDER_CANCEL_ERROR);
				}
			}else{
				result.setResultCode(ResultCode.ERROR);
			}
		} catch (ManagerException e) {
			logger.error("取消订单出错，orderId=" + orderId , e);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}

	@Override
	public ResultDTO<OrderDetailForMember> getOrderDetailForMember(Long orderId, Long memberId) {
		ResultDTO<OrderDetailForMember> result = new ResultDTO<OrderDetailForMember>();
		try {
			OrderDetailForMember orderDetail = new OrderDetailForMember();
			OrderForMember orderForMember = orderManager.getOrderForMemberByOrderId(orderId, memberId);
			if(orderForMember!=null) {
				//设置预期年化收益，收益天数，预期收益
				BigDecimal expectAmount = BigDecimal.ZERO;
				int profitDays = 0;
				Project project = projectManager.selectByPrimaryKey(orderForMember.getProjectId());
				List<DebtInterest> debtInterests = debtManager.getFullDebtInfoById(project.getDebtId()).getDebtInterests();
				BigDecimal extraAnnualizedRate = BigDecimal.ZERO;
				if(orderForMember.getExtraAnnualizedRate()!=null) {
					extraAnnualizedRate = orderForMember.getExtraAnnualizedRate();
				}
				if(Collections3.isNotEmpty(debtInterests)) {
					for (DebtInterest debtInterest : debtInterests) {
						int days = 0;
						Date startInterestDate = DateUtils.addDate(DateUtils.formatDate(orderForMember.getOrderTime(),  DateUtils.DATE_FMT_3), project.getInterestFrom());
						if(DateUtils.isDateBetween(startInterestDate, debtInterest.getStartDate(), debtInterest.getEndDate())) {
							days = DateUtils.daysOfTwo(startInterestDate, debtInterest.getEndDate()) + 1;
						} 
						if(startInterestDate.after(debtInterest.getEndDate())) {
							days = 0;
						}
						if(startInterestDate.before(debtInterest.getStartDate())) {
							days = DateUtils.daysOfTwo(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
						}
						
						// 后期需要根据收益类型不同而采用不同的计算方式，目前只有按日计息，按月付息
						if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())||DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
							// 单位利息
							BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(),
									orderForMember.getInvestAmount(), orderForMember.getAnnualizedRate().add(extraAnnualizedRate).setScale(2, BigDecimal.ROUND_HALF_UP));
							BigDecimal value = (unitInterest.multiply(new BigDecimal(days))).setScale(2, BigDecimal.ROUND_HALF_UP);
							expectAmount = expectAmount.add(value);
						}
						profitDays += days;
					}
				}
				orderForMember.setExpectAmount(expectAmount);
				orderForMember.setProfitDays(profitDays);
				BeanCopyUtil.copy(orderForMember, orderDetail);
				result.setResult(orderDetail);
			}
		} catch (ManagerException e) {
			logger.error("获取订单详情出错，orderId=" + orderId , e);
			result.setResultCode(ResultCode.ERROR);
			return result;
		}
		return result;
	}

	@Override
	public Page<OrderForMemberDto> queryOrderyForMember(OrderQuery orderQuery) {
		Page<OrderForMemberDto> dto = null;
		try {
			dto = new Page<OrderForMemberDto>();
//			orderQuery.setStatus(99);//不含取消的订单
			Page<OrderForMember> page = orderManager.selectAllOrderForMember(orderQuery);
			dto.setData(BeanCopyUtil.mapList(page.getData(), OrderForMemberDto.class));
			dto.setiDisplayLength(page.getiDisplayLength());
			dto.setiDisplayStart(page.getiDisplayStart());
			dto.setiTotalRecords(page.getiTotalRecords());
			dto.setPageNo(page.getPageNo());
		} catch (ManagerException e) {
			logger.error("获取用户订单异常：", e);
		}
		return dto;
	}
	
	@Override
	public Page<OrderForMemberDto> queryOrderyForMemberP2p(OrderQuery orderQuery) {
		Page<OrderForMemberDto> dto = null;
		try {
			dto = new Page<OrderForMemberDto>();
//			orderQuery.setStatus(99);//不含取消的订单
			Page<OrderForMember> page = orderManager.p2pSelectAllOrderForMember(orderQuery);
			dto.setData(BeanCopyUtil.mapList(page.getData(), OrderForMemberDto.class));
			dto.setiDisplayLength(page.getiDisplayLength());
			dto.setiDisplayStart(page.getiDisplayStart());
			dto.setiTotalRecords(page.getiTotalRecords());
			dto.setPageNo(page.getPageNo());
		} catch (ManagerException e) {
			logger.error("获取用户订单异常：", e);
		}
		return dto;
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
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Order> updateOrderForRecharge(Order order) throws Exception{
		ResultDO<Order> result = new ResultDO<Order>(order);
		try {
		//判断现金券余额是否与现金券匹配
		if(StringUtil.isNotBlank(order.getCashCouponNo())) {
			
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
	public int getNoPayOrdeCount(Long memberId) {
		try {
			return orderManager.getNoPayOrdeCount(memberId);
		} catch (ManagerException e) {
			logger.error("获取当前用户 还未支付的订单出错，memberId=" + memberId , e);
		}
		return 0;
	}

	@Override
	public ResultDTO<PayOrderDto> queryPayOrderInfo(Long orderId, String cashCouponNo, Long memberId) {
		ResultDTO<PayOrderDto> result = new ResultDTO<PayOrderDto>();
		try {
			PayOrderDto payOrderBiz = new PayOrderDto();
			Order order = orderManager.selectByPrimaryKey(orderId);
			if(order!=null) {
				if(order.getMemberId().longValue()!=memberId.longValue()) {
					result.setResultCode(ResultCode.ORDER_NOT_BELONG_TO_MEMBER_ERROR);
					return result;
				}
				if(order.getStatus()==StatusEnum.ORDER_CLOSED.getStatus()) {
					result.setResultCode(ResultCode.ORDER_FRONT_ORDER_CLOSE);
					return result;
				}
				if(order.getStatus()!=StatusEnum.ORDER_WAIT_PAY.getStatus()) {
					result.setResultCode(ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY);
					return result;
				}
				Project project = projectManager.selectByPrimaryKey(order.getProjectId());
				payOrderBiz.setInvestAmount(order.getInvestAmount());
				payOrderBiz.setMemberId(order.getMemberId());
				payOrderBiz.setOrderId(orderId);
				payOrderBiz.setOrderNo(order.getOrderNo());
				payOrderBiz.setProjectId(order.getProjectId());
				payOrderBiz.setProjectName(project.getName());
				payOrderBiz.setSavingPotBalance(balanceManager.queryFromThirdPay(order.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY).getAvailableBalance());
				//优惠券限制解除
				//if(project.getIsNovice().intValue() != StatusEnum.PROJECT_IS_NOVICE.getStatus() || payOrderBiz.getInvestAmount().compareTo(new BigDecimal(Constant.NOVICE_INVEST_AMOUNT)) >= 0){
//					List<Coupon> couponList = couponManager.findUsableCouponsByMemberId(order.getMemberId(), TypeEnum.COUPON_TYPE_CASH.getType());
					int days = 0; 
					if(project.isDirectProject()){
						days = project.countProjectDays();
					}else{
						days= DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), project.getEndDate())+1 - project.getInterestFrom();
					}
					List<Coupon> couponList = couponManager.getUsableAndLimitedCoupons(order.getMemberId(), TypeEnum.COUPON_TYPE_CASH.getType(), CouponEnum.COUPON_CLIENT_APP.getCode(), order.getInvestAmount(), days);
					payOrderBiz.setCoupons(BeanCopyUtil.mapList(couponList, OrderCouponDto.class));
				//}
				payOrderBiz.setOrderPayAmount(order.getPayAmount());
				payOrderBiz.setOrderSavingPotAmount(order.getUsedCapital());
				if(StringUtil.isNotBlank(cashCouponNo)) {
					Coupon coupon = couponManager.getCouponByCouponNo(cashCouponNo);
					if(coupon!=null) {
						payOrderBiz.setUsedCouponAmount(coupon.getAmount());
						payOrderBiz.setCashCouponNo(coupon.getCouponCode());
					}
				}
				if(StringUtil.isNotBlank(order.getCashCouponNo())) {
					Coupon coupon = couponManager.getCouponByCouponNo(order.getCashCouponNo());
					if(coupon != null){
						payOrderBiz.setOrderCashEndDate(DateUtils.getStrFromDate(coupon.getEndDate(), DateUtils.DATE_FMT_3));
						payOrderBiz.setOrderUsedCashAmount(coupon.getAmount());
						payOrderBiz.setOrderUsedCashNo(order.getCashCouponNo());
					}
				}
				result.setResult(payOrderBiz);
			}else{
				result.setResultCode(ResultCode.ERROR);
			}
		} catch (Exception e) {
			result.setResultCode(ResultCode.ORDER_FRONT_PAY_ERROR);
			logger.error("获取支付订单页面信息出错，orderId=" + orderId , e);
		}
		return result;
	}
	
	@Override
	public ResultDTO<OrderForAppDto> queryPayOrderInfoSinaBank(Long orderId, Long memberId) {
		ResultDTO<OrderForAppDto> result = new ResultDTO<OrderForAppDto>();
		try {
			Order order = orderManager.selectByPrimaryKey(orderId);
			if(order!=null) {
				if(order.getMemberId().longValue()!=memberId.longValue()) {
					result.setResultCode(ResultCode.ORDER_NOT_BELONG_TO_MEMBER_ERROR);
					return result;
				}
			/*	if(order.getStatus()==StatusEnum.ORDER_CLOSED.getStatus()) {
					result.setResultCode(ResultCode.ORDER_FRONT_ORDER_CLOSE);
					return result;
				}*/
			/*	if(order.getStatus()!=StatusEnum.ORDER_WAIT_PAY.getStatus()&&order.getStatus()!=StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus()) {
					result.setResultCode(ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY);
					return result;
				}*/
				Project project = projectManager.selectByPrimaryKey(order.getProjectId());
				OrderForAppDto orderForApp = new OrderForAppDto();
			    BeanCopyUtil.copy(order, orderForApp);
			    orderForApp.setSavingPotBalance(balanceManager.queryFromThirdPay(order.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY).getAvailableBalance());
				String prefixProjectName = project.getName();
				if (project.getName().contains("期")) {
					prefixProjectName = project.getName().substring(0, project.getName().indexOf("期") + 1);
				}
				orderForApp.setPrefixProjectName(prefixProjectName);
				ResultDO<Boolean> isAuthority = (ResultDO<Boolean>) memberManager.synWithholdAuthority(memberId);
				orderForApp.setWithholdAuthority(isAuthority.getResult());
				orderForApp.setOrderPayAmount(order.getPayAmount());
				orderForApp.setOrderSavingPotAmount(order.getUsedCapital());
				// 转让项目订单
				if (orderForApp.getProjectCategory() != null && orderForApp.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
					TransferProject tPro = transferProjectManager.selectByPrimaryKey(orderForApp.getTransferId());
					orderForApp.setProfitPeriod(transferProjectManager.getReturnDay(tPro.getTransactionId()) + "天");
					orderForApp.setAnnualizedRate(order.getAnnualizedRate());
					// 计算预期收益
					orderForApp.setExpectAmount(FormulaUtil.calculateInterest(order.getInvestAmount(), order.getAnnualizedRate(),
							transferProjectManager.getReturnDay(tPro.getTransactionId())));
					// 产品价值
//					BigDecimal projectValue = order.getTransferPrincipal().multiply(tPro.getProjectValue())
//							.divide(tPro.getTransactionAmount(), 2, BigDecimal.ROUND_HALF_UP);
//					orderForApp.setProjectValue(projectValue);
					orderForApp.setInvestType(project.getInvestType());
					result.setResult(orderForApp);
					return result;
				}
				int days = 0;
				if(project.isDirectProject()){
					days = project.countProjectDays();
				}else{
					days= DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), project.getEndDate())+1 - project.getInterestFrom();
				}
				// 非限制使用优惠券用户
				if (couponManager.useCouponSpecialLimit(memberId)) {
					List<Coupon> couponList = couponManager.getUsableAndLimitedCoupons(order.getMemberId(),
							TypeEnum.COUPON_TYPE_CASH.getType(), CouponEnum.COUPON_CLIENT_APP.getCode(), order.getInvestAmount(), days);
					orderForApp.setCoupons(BeanCopyUtil.mapList(couponList, OrderCouponDto.class));
				}
				orderForApp.setInvestType(project.getInvestType());
				
				//项目加息默认处理为0.00
				orderForApp.setAnnualizedRate(orderForApp.getAnnualizedRate().add(orderForApp.getExtraProjectAnnualizedRate()));
				
				String profitPeriod ="";
				if(project.isDirectProject()){
					profitPeriod=project.getProfitPeriod();					
				}else{
					profitPeriod =project.getEarningsDaysByStatus()+"天";
				}
				orderForApp.setProfitPeriod(profitPeriod);
				BigDecimal expectAmount = BigDecimal.ZERO;//项目收益
				BigDecimal totalExpectAmount = BigDecimal.ZERO;//含券总收益
				BigDecimal extraExpectAmount = BigDecimal.ZERO;//券收益
				
				PayOrderBiz payOrderBiz = new PayOrderBiz();
				if (project.isDirectProject()) {
					orderManager.p2pProjectCheck(project, order, payOrderBiz);
				} else {
					orderManager.debtExpectAmount(project, order, payOrderBiz);
				}
				totalExpectAmount = payOrderBiz.getExpectAmount();
				extraExpectAmount = payOrderBiz.getExtraExpectAmount();
				if(extraExpectAmount == null||extraExpectAmount.compareTo(BigDecimal.ZERO)==0){
					expectAmount = totalExpectAmount;
				}else{
					expectAmount =totalExpectAmount.subtract(extraExpectAmount);
				}
				orderForApp.setExpectAmount(expectAmount);
				orderForApp.setTotalExpectAmount(totalExpectAmount);
				orderForApp.setExtraExpectAmount(extraExpectAmount);
				if(order.getExtraInterestDay()>0){
					orderForApp.setExtraName("起息日起，加息"+order.getExtraInterestDay()+"天");
					orderForApp.setExtraExpectAmount(FormulaUtil.calculateInterest(order.getInvestAmount(),order.getExtraAnnualizedRate(),order.getExtraInterestDay()));
				}
				// orderForApp.setBankCardList(memberBankCardService.selectAllQuickPayBankCard(memberId));
				if(StringUtil.isNotBlank(order.getCashCouponNo())) {
					Coupon coupon = couponManager.getCouponByCouponNo(order.getCashCouponNo());
					if(coupon != null){
						orderForApp.setOrderCashEndDate(DateUtils.getStrFromDate(coupon.getEndDate(), DateUtils.DATE_FMT_3));
						orderForApp.setOrderUsedCashAmount(coupon.getAmount());
						orderForApp.setOrderUsedCashNo(order.getCashCouponNo());
					}
				}
				result.setResult(orderForApp);
			}else{
				result.setResultCode(ResultCode.ERROR);
			}
		} catch (Exception e) {
			result.setResultCode(ResultCode.ORDER_FRONT_PAY_ERROR);
			logger.error("获取支付订单页面信息出错，orderId=" + orderId , e);
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
				if(status == 5){
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
				couponManager.couponForCurrentInvestIsUsable(result, couponForLock, CouponEnum.COUPON_CLIENT_APP.getCode(), days, order);
				result.setResult(couponForLock);
			}else {
				result.setResultCode(ResultCode.ORDER_USED_COUPON_NOT_EXISTS_ERROR);
			}
		} catch (Exception e) {
			result.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("优惠券是否可使用检查异常" , e);
		}
		return result;
	}

	@Override
	public int getNoPayOrdeCountFilterP2p(Long memberId) {
		try {
			return orderManager.getNoPayOrdeCountFilterP2p(memberId);
		} catch (ManagerException e) {
			logger.error("获取当前用户 还未支付的订单出错，memberId=" + memberId , e);
		}
		return 0;
	}

	@Override
	public ResultDTO<OrderForAfterInvestDto> tempPageAfterInvest(Long orderId, Long memberId) {
		ResultDTO<OrderForAfterInvestDto> rDO = new ResultDTO<OrderForAfterInvestDto>();
		try {
			// 封装订单信息
			Order order = orderManager.selectByPrimaryKey(orderId);
			if (order == null) {
				rDO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return rDO;
			}
			fillNull(order);
			if (!memberId.equals(order.getMemberId())) {
				rDO.setResultCode(ResultCode.ORDER_NOT_BELONG_TO_MEMBER_ERROR);
				return rDO;
			}
			OrderForAfterInvestDto dto = new OrderForAfterInvestDto();
			order.setAnnualizedRate(order.getAnnualizedRate().add(order.getExtraProjectAnnualizedRate()));
			dto.setOrder(order);
			if (StatusEnum.ORDER_PAYED_INVESTED.getStatus() == order.getStatus()) {
				// 查询交易ID
				Transaction transaction = transactionManager.getTransactionByOrderId(order.getId());
				Long transactionId = transaction != null ? transaction.getId() : null;
				dto.setTransactionId(transactionId);
			}
			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			String prefixProjectName = project.getName();
			if (project.getName().contains("期")) {
				prefixProjectName = project.getName().substring(0, project.getName().indexOf("期") + 1);
			}
			// 还款方式
			dto.setProfitType(project.getProfitType());
						
			
			dto.setPrefixProjectName(prefixProjectName);
			dto.setProjectCategory(order.getProjectCategory());
			dto.setInvestType(project.getInvestType());
			if(order.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
				TransferProject transferProject = transferProjectManager.selectByPrimaryKey(order.getTransferId());
				dto.setResidualDays(transferProjectManager.getReturnDay(transferProject.getTransactionId()));
				dto.setTransferPrincipal(order.getTransferPrincipal());
				// 计算预计收益  
				BigDecimal expectAmount = BigDecimal.ZERO;
				expectAmount = FormulaUtil.calculateInterest(order.getInvestAmount(), order.getAnnualizedRate(),
						transferProjectManager.getReturnDay(transferProject.getTransactionId()));
				dto.setExpectAmount(expectAmount);
				
				BigDecimal balance = transferProjectManager.getTransferProjectBalanceById(transferProject.getId());
				String progress = getProjectNumberProgress(transferProject.getTransactionAmount(), balance);
				dto.setProgress(progress);
				
			}else{
				if (project.isDirectProject()) {
					// P2P设置收益周期
					dto.setProfitPeriod(project.getProfitPeriod());
				} else {
					// 债权设置收益天数
					dto.setProfitDays(getProfitDays(order, project));
				}
				//项目余额
				BigDecimal   availableBalance =  projectService.getProjectBalanceById(order.getProjectId());
				dto.setAvailableBalance(availableBalance);
				if(((BigDecimal.ZERO.compareTo(availableBalance)==0||availableBalance==null)
						&&(order.getStatus()==StatusEnum.ORDER_PAYED_FAILED.getStatus()||order.getStatus()==StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus()
						))				
						||order.getStatus()==StatusEnum.ORDER_PAYED_INVESTED.getStatus()){
					//投资完成推荐项目
					List<RecommendProjectDto> recommendProjectList = projectService.getRecommendProject(2);
					dto.setRecommendProjectList(recommendProjectList);
				}
				//处理返回信息
				this.addDiffInformation(dto);
			}
			//直投抽奖
			dto.setProjectId(order.getProjectId());
			dto.setLotteryNumber(0);
			dto.setQuickRewardFlag(0);
			if(order.getExtraInterestDay()>0){
				dto.setExtraName("起息日起，加息"+order.getExtraInterestDay()+"天");
			}
			if(order.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_NORMAL.getType()){//普通项目计算快投有奖
				ProjectExtra pro = projectExtraManager.getProjectQucikReward(order.getProjectId());
				if(pro.getActivitySign()!=null&&pro.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
					if(order.getStatus()==StatusEnum.ORDER_PAYED_INVESTED.getStatus()){
						Integer num =projectExtraManager.getLotteryNum(dto.getTransactionId());
						if(num!=null&&num>0){
							dto.setQuickRewardFlag(1);
							dto.setLotteryNumber(num);
						}
					}
				}
			}
			
			
			// 判断红包
			boolean activityFlag = activityLotteryService.hasActivityFlag(order, TypeEnum.ACTIVITY_SOURCE_TYPE_CREATE.getType());
			dto.setHasRedPackage(activityFlag);
			
			
			//用户签署方式
			Member member = memberService.selectByPrimaryKey(memberId);
			dto.setSignWay(member.getSignWay());
			
			rDO.setResult(dto);
		} catch (ManagerException e) {
			logger.error("获取当前用户 还未支付的订单出错，memberId=" + memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	private void addDiffInformation(OrderForAfterInvestDto dto) throws ManagerException{
		Order order = dto.getOrder();
		//投资成功
		if(StatusEnum.ORDER_PAYED_INVESTED.getStatus() == order.getStatus()){
			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			BigDecimal expectAmount = BigDecimal.ZERO;
			if(project.isDirectProject()){
				// 计算预计收益  
				expectAmount = projectManager.invertExpectEarnings(project, order.getInvestAmount(), DateUtils.getCurrentDate(),order.getExtraAnnualizedRate());
				dto.setExpectAmount(expectAmount);
				if(order.getExtraInterestDay()!=null&&order.getExtraInterestDay()>0){
					expectAmount = projectManager.invertExpectEarnings(project, order.getInvestAmount(), DateUtils.getCurrentDate(),BigDecimal.ZERO);
					BigDecimal extraInterest = FormulaUtil.calculateInterest(order.getInvestAmount(),order.getExtraAnnualizedRate(),order.getExtraInterestDay());
					if(extraInterest==null){
						extraInterest=BigDecimal.ZERO;
					}
					dto.setExpectAmount(expectAmount.add(extraInterest));
				}
				//TODO 当前募集进度
				BigDecimal balance = getProjectBalanceById(project.getId());
				String progress = getProjectNumberProgress(project.getTotalAmount(), balance);
				dto.setProgress(progress);
			}else{
				// 计算预计收益
				expectAmount = projectManager.expectAmount(project, order.getInvestAmount(), order.getExtraAnnualizedRate());
				dto.setExpectAmount(expectAmount);
				//TODO 首次还款日期
				List<DebtInterest> debtInterestList = debtInterestManager.findInterestsByDebtId(project.getDebtId());
				dto.setEndDate(debtInterestList.get(0).getEndDate());
			}
		}
		/*//投资失败
		if(StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus() == order.getStatus()||StatusEnum.ORDER_PAYED_FAILED.getStatus() == order.getStatus()){
			// 订单编号
		}
		//投资处理中
		if(StatusEnum.ORDER_WAIT_PROCESS.getStatus() == order.getStatus()||StatusEnum.ORDER_WAIT_PAY.getStatus() == order.getStatus()){
			// 订单编号  
		}*/
	}
	
	/**
	 * 项目进度
	 * @param totalAmount
	 * @param availableBalance
	 * @return
	 */
	public String getProjectNumberProgress(BigDecimal totalAmount, BigDecimal availableBalance){
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
	
	/**
	 * 
	 * @Description:关键变量初始化
	 * @param order
	 * @author: wangyanji
	 * @time:2016年4月11日 下午3:35:18
	 */
	private void fillNull(Order order) {
		if (order.getUsedCouponAmount() == null) {
			order.setUsedCouponAmount(BigDecimal.ZERO);
		}
		if (order.getAnnualizedRate() == null) {
			order.setAnnualizedRate(BigDecimal.ZERO);
		}
		if (order.getExtraAnnualizedRate() == null) {
			order.setExtraAnnualizedRate(BigDecimal.ZERO);
		}
	}

	/**
	 * 
	 * @Description:获取收益天数
	 * @param order
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年4月11日 下午3:35:47
	 */
	private int getProfitDays(Order order, Project project) throws ManagerException {
		List<DebtInterest> debtInterests = debtManager.getFullDebtInfoById(project.getDebtId()).getDebtInterests();
		Date startInterestDate = DateUtils.addDate(DateUtils.formatDate(order.getOrderTime(), DateUtils.DATE_FMT_3),
				project.getInterestFrom());
		if (Collections3.isEmpty(debtInterests)) {
			return 0;
		}
		int profitDays = 0;
		// 如果某一期的结束时间早于开始计息时间，则不记录收益和收益天数
		for (DebtInterest debtInterest : debtInterests) {
			if (startInterestDate.after(debtInterest.getEndDate())) {
				continue;
			}
			int days = 0;
			if (DateUtils.isDateBetween(startInterestDate, debtInterest.getStartDate(), debtInterest.getEndDate())) {
				days = DateUtils.daysOfTwo(startInterestDate, debtInterest.getEndDate()) + 1;
			} else if (startInterestDate.after(debtInterest.getEndDate())) {
				days = 0;
			} else if (startInterestDate.before(debtInterest.getStartDate())) {
				days = DateUtils.daysOfTwo(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
			}
			profitDays += days;
		}
		return profitDays;
	}
	
	private final String hz[]={"零","一","二","三","四","五","六","七","八","九","十"};
	
	@Override
	public Object directLottery(Long memberId,Long transactionId,int type) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try{
			
			ResultDO<Object> rDO =  projectManager.directProjectLotteryByTransactionIdType(transactionId,memberId,type);
			rDTO.setResultCode(rDO.getResultCode());
			
			List<PrizeInPool> prizList = Lists.newArrayList();
			Map  maps = Maps.newHashMap();
			Integer rewardAmount = 0;
			Integer popularity = 0;
			if (rDO.isSuccess()) {
				ProjectForLotteryReturn reward = (ProjectForLotteryReturn) rDO
						.getResult();
				popularity = Integer.valueOf(reward.getPopularity());
				List<DirectLotteryResultList> directLotteryResultList  = reward.getDirectLotteryResultList();
				Map<String, Object> map = Maps.newHashMap();
				if(Collections3.isNotEmpty(directLotteryResultList)){
						for (DirectLotteryResultList dir:directLotteryResultList) {
							if (map.containsKey(dir.getPrize())) {
								map.put(dir.getPrize(), Integer.valueOf(map.get(
										dir.getPrize()).toString()) + 1);
							} else {
								map.put(dir.getPrize(), 1);
							}
							
							rewardAmount +=Integer.valueOf(dir.getRewardAmount());
						}
					
				}
				for (Entry<String, Object> entry : map.entrySet()) {
					PrizeInPool pri = new PrizeInPool();
					pri.setLevel(Integer.valueOf(entry.getKey()));
					pri.setNum(Integer.valueOf(entry.getValue().toString()));
					prizList.add(pri);
				}
			}
			maps.put("prizList", prizList);
			maps.put("rewardAmount", rewardAmount);
			maps.put("popularity",popularity);
			
			rDTO.setResult(maps);
			if(rDO.isSuccess()){
				rDTO.setIsSuccess();
			}else if(rDO.isError()){
				rDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("投资完成页面,快投有奖,抽奖异常",e);
		}
		return rDTO;
	}

}