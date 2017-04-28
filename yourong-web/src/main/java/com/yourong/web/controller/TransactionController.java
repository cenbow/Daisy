package com.yourong.web.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.core.ic.model.biz.TransactionProjectDetailBiz;
import com.yourong.core.ic.model.query.TransactionProjectDetailBizQuery;
import com.yourong.core.tc.model.biz.*;
import com.yourong.core.tc.model.query.TransactionProjectBizQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.query.TransferProjectQuery;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.query.CollectingProjectQuery;
import com.yourong.core.tc.model.query.DebtForLenderQuery;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.web.dto.OrderDto;
import com.yourong.web.dto.OrderPayDto;
import com.yourong.web.service.ActivityLotteryService;
import com.yourong.web.service.MemberBankCardService;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.OrderService;
import com.yourong.web.service.ProjectService;
import com.yourong.web.service.TransactionInterestService;
import com.yourong.web.service.TransactionService;
import com.yourong.web.utils.ServletUtil;


@Controller
@RequestMapping("transaction")
/**
 * 前台交易controller
 * @author Administrator
 *
 */
public class TransactionController extends BaseController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionInterestService transactionInterestService;
    @Autowired
    private OrderService orderService;
    @Resource
    private OrderManager orderManager;
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
    private ActivityLotteryService activityLotteryService;
    @Autowired
    private ProjectService projectService;
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
	@RequestMapping(value = "/pay/order/cashDesk", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<TradeBiz> payOrderCashDesk(HttpServletRequest req, HttpServletResponse resp, OrderDto orderDto) {
		ResultDO<TradeBiz> result = new ResultDO<TradeBiz>();
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
				ResultDO<TradeBiz> rDO = transactionService.isRepeatToCashDest(order, payerIp);
				if (rDO.isError() || rDO.getResult().isNotFirstRequest()) {
					return rDO;
				}
			}
			if (order.getStatus() != StatusEnum.ORDER_WAIT_PAY.getStatus()) {
				result.setResultCode(ResultCode.ORDER_FRONT_ORDER_NOT_WAIT_PAY);
				return result;
			}
			order.setUsedCapital(orderDto.getUsedCapital());
			// 设置订单现金券金额
			order.setUsedCouponAmount(orderDto.getUsedCouponAmount());
			order.setCashCouponNo(orderDto.getCashCouponNo());
			order.setPayAmount(BigDecimal.ZERO);
			// 校验支付金额是否匹配
//			BigDecimal usedCouponAmount = orderDto.getUsedCouponAmount() == null ? BigDecimal.ZERO : orderDto.getUsedCouponAmount();
//			BigDecimal usedCapital = orderDto.getUsedCapital() == null ? BigDecimal.ZERO : orderDto.getUsedCapital();
//			if (order.getInvestAmount().compareTo(usedCouponAmount.add(usedCapital)) != 0) {
//				result.setResultCode(ResultCode.TRANSACTION_USEDCAPITAL_USEDREWARD_NOT_EQUAL_INVESTAMOUNT);
//				return result;
//			}
			// carPayIn、carBusiness不允许使用优惠券
			if (orderManager.orderOfCarPayInOrCarBusinessIsUseCoupon(order.getProjectId(), order.getCashCouponNo())) {
				result.setResultCode(ResultCode.PROJECT_CARPAYIN_CANOT_USE_COUPON);
				return result;
			}
			if (order.getProjectCategory() != TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				ResultDO<Order> checkCouponDO = orderService.checkOrderCashCouponNo(order, false);
				if (checkCouponDO.isError()) {
					result.setResultCode(checkCouponDO.getResultCode());
					return result;
				}
			}
			// 发起新浪代收支付
			return transactionService.createTransactionHostingTrade(order, TypeEnum.TRADE_SOURCE_TYPE_TRANSACTION.getType(), payerIp);
		} catch (Exception e) {
			result.setResultCode(ResultCode.ORDER_FRONT_PAY_ERROR);
			return result;
		}
	}

	/**
	 * 需要 订单支付 绑卡支付
	 *
	 * @param req
	 * @param resp
	 * @param orderDto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/pay/order/recharge", method = RequestMethod.POST)
	@ResponseBody
	public Object onthirdPayCarID(HttpServletRequest req, HttpServletResponse resp, OrderDto orderDto) {
		ResultDO<Order> orderResultDO = checkPayOrder(orderDto);
		if (orderResultDO.isError()) {
			return orderResultDO;
		}
		Order order = orderResultDO.getResult();
		BigDecimal bigDecimal = order.getPayAmount();
		String orderNo = order.getOrderNo();
		String payerIp = getIp(req);
		// 交易来源
		boolean isMobile = ServletUtil.isMobile(req);
		ResultDO<Object> resultDO = memberService.createHostingDepositAndRechargeLog(getMember().getId(), payerIp, bigDecimal,
				TypeEnum.RECHARGELOG_TYPE_TRADING, orderNo, isMobile);
		return resultDO;
	}

	/**
	 * 效验订单
	 * 
	 * @param orderDto
	 * @param isPayOnBank
	 *            网银充值，还是绑卡支付
	 * @return
	 * @throws Exception
	 */
	private ResultDO<Order> checkPayOrder(OrderDto orderDto) {
        ResultDO<Order> result = new ResultDO<Order>();
		try {
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

			// carPayIn、carBusiness不允许使用优惠券
			if (orderManager.orderOfCarPayInOrCarBusinessIsUseCoupon(order.getProjectId(), orderDto.getCashCouponNo())) {
				result.setResultCode(ResultCode.PROJECT_CARPAYIN_CANOT_USE_COUPON);
				return result;
			}

			order.setUsedCapital(orderDto.getUsedCapital());
			order.setUsedCouponAmount(orderDto.getUsedCouponAmount());
			order.setCashCouponNo(orderDto.getCashCouponNo());
			order.setPayAmount(orderDto.getPayAmount());
			if (order.getPayAmount() == null || order.getPayAmount().doubleValue() <= 0) {
				result.setResultCode(ResultCode.TRANSACTION_AMOUNT_MUST_BE_ABOVE_ZERO);
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
				result.setResult(order);
				return result;
			}

			// 设置订单现金券金额
			// setOrderCashCouponAmount(order);

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
			// result = orderService.checkOrderCashCouponNo(order, true);
			// if(!result.isSuccess()){
			// return result;
			// }
			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			if (project == null || (project.getDelFlag() != null && project.getDelFlag().intValue() < 1)) {
				result.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
				logger.info("交易前置条件验证失败：" + ResultCode.PROJECT_NOT_EXIST_ERROR.getMsg() + ", orderNo=" + order.getOrderNo()
						+ ", projectId=" + order.getProjectId());
				return result;
			}
			if (project != null && project.isNoviceProject()) {
				boolean checkNoviceFlag = memberService.needCheckNoviceProject(order.getMemberId());
				if (checkNoviceFlag && transactionService.hasTransactionByMember(order.getMemberId())) {
					result.setResultCode(ResultCode.ORDER_ONLY_ALLOWING_NEW_USERS_ERROR);
					return result;
				}
			}
			result.setResult(order);
		} catch (Exception e) {
			logger.error("效验订单异常 orderNo={}", orderDto.getOrderNo(), e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
        }
        return result;
    }


    /**
     * 项目列表页最新投资记录
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/new/transactions")
    @ResponseBody
    public ResultDO<TransactionForProject> getNewTransactions(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam("pageSize") int pageSize
    ) {
        List<TransactionForProject> list = transactionService.selectNewTransactions(pageSize);
        ResultDO<TransactionForProject> result = new ResultDO<TransactionForProject>();
        result.setResultList(list);
        return result;
    }

    /**
     * 生成合同接口
     *
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "/generate/contract")
    @ResponseBody
    public ResultDO<Transaction> generateContract(@RequestParam("transactionId") Long transactionId) {
        ResultDO<Transaction> result = new ResultDO<Transaction>();
        if (transactionService.generateContract(transactionId)) {
            return result;
        }
        return null;
    }


    /**
     * 前台查询用户中心交易记录
     *
     * @param req
     * @param resp
     * @param transactionQuery
     * @return
     */
    @RequestMapping(value = "front/member/transactions")
    @ResponseBody
    public ResultDO<Object> selectTransactions(HttpServletRequest req, HttpServletResponse resp, TransactionQuery transactionQuery) {
        TransactionQuery query = new TransactionQuery();
        query.setMemberId(getMember().getId());
        Page<TransactionForMemberCenter> pager = transactionService.selectAllTransactionForMember(query);
        return new ResultDO<Object>(pager);
    }

    /**
     * 获取交易详情
     *
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public ResultDO<TransactionForMemberCenter> getTransactionDetails(@RequestParam("transactionId") Long transactionId) {
    	return transactionService.getTransactionForMemberCenter(transactionId,getMember().getId());
    }

    /**
     * 获取交易详情
     * @return
     */
	@RequestMapping(value = "/getByOrderId", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<OrderPayDto> getTransactionByOrderId(@RequestParam("orderId") Long orderId) {
    	ResultDO<OrderPayDto> byOrderId = transactionService.getTransactionByOrderId(orderId);
        return byOrderId;
    }

    /**
     * 获取收益详情
     *
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "/interests", method = RequestMethod.GET)
    @ResponseBody
    public ResultDO<TransactionForMemberCenter> getTransactionInterests(@RequestParam("transactionId") Long transactionId) {
        ResultDO<TransactionForMemberCenter> result = transactionService.getTransactionForMemberCenter(transactionId,getMember().getId());
        if (result != null && result.getResult() != null) {
        	  transactionInterestService.getTransactionInterestsByTransactionId(result,transactionId);
        }
        return result;
    }

    /**
     * 用户中心交易列表页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/investment", method = RequestMethod.GET)
    public ModelAndView transactionsPage(HttpServletRequest req,
                                   HttpServletResponse resp) {
        ModelAndView transactionMv = new ModelAndView();
        String date = (String) req.getParameter("searchDate");
        transactionMv.addObject("searDate",date);
        transactionMv.setViewName("/member/invest/investment");
        Long memberId = getMember().getId();
        int count = projectService.collectingProject(memberId);
        transactionMv.addObject("count",count);
        
		//周年庆投资分享红包
//		String activityId = PropertiesUtil.getProperties("activity.anniversary.id");
//		Optional<Activity> ac = activityLotteryService.getActivityByCache(Long.parseLong(activityId));
//		if(ac.isPresent()) {
//			if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), ac.get().getStartTime(), ac.get().getEndTime())) {
//				transactionMv.addObject("anniversaryFlag", true);
//			}
//		}
        return transactionMv;
    }

    /**
     * 用户中心交易列表数据
     *
     * @param query
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/investmentList", method = RequestMethod.GET)
    public ModelAndView transactionDataList(@ModelAttribute("transactionQuery") TransactionQuery query, HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView transactionMv = new ModelAndView();
        query.setMemberId(getMember().getId());
        Page<TransactionForMemberCenter> transactionPage = transactionService.selectAllTransactionForMember(query);
        transactionMv.setViewName("/member/invest/investmentList");
        transactionMv.addObject("transactionPage", transactionPage);
        transactionMv.addObject("query", query);
        return transactionMv;
    }

    /**
     * 合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/contract/preview", method = RequestMethod.GET)
    public ModelAndView previewContract(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam("investAmount") BigDecimal investAmount,
            @RequestParam("projectId") Long projectId,
            @RequestParam("annualizedRate") BigDecimal annualizedRate
    ) {
        Long memberId = getMember().getId();
        ResultDO<ContractBiz> result = transactionService.previewContract(memberId, projectId, investAmount, annualizedRate);
        ModelAndView model = new ModelAndView();
        model.setViewName("/contract/contract-preview");
        model.addObject("contract", result.getResult());
        model.addObject("preview", true);
        return model;
    }
    /**
     * 直投项目合同预览,需要登录
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/p2pContract/preview")
    public ModelAndView ztPreview(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.addObject("preview", true);
        model.setViewName("/contract/ztContract-preview");
        return model;
    }

    /**
     * 直投项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value ="/p2pContract/view" , method = RequestMethod.GET)
    public ModelAndView p2pViewContract(HttpServletRequest req,
            HttpServletResponse resp  ) {
    	ModelAndView model = new ModelAndView();
    	Long orderId = ServletRequestUtils.getLongParameter(req,"orderId",0L);
    	if (orderId == 0L){
            model.setViewName("redirect:/404");
            return model;
        }
    	ResultDO<ContractBiz> result = transactionService.p2pViewContract(orderId, getMember().getId());
    	 if (!result.isSuccess()) {
             model.setViewName("redirect:/404");
             return model;
         }
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
     * @return
     */
    @RequestMapping(value = "/contract/view", method = RequestMethod.GET)
    public ModelAndView viewContract(
            HttpServletRequest req,
            HttpServletResponse resp ) {
        Long orderId = ServletRequestUtils.getLongParameter(req,"orderId",0L);
        ModelAndView model = new ModelAndView();
        if (orderId == 0L){
            model.setViewName("redirect:/404");
            return model;
        }
        ResultDO<ContractBiz> result = transactionService.viewContract(orderId, getMember().getId());
        if (!result.isSuccess()) {
            model.setViewName("redirect:/404");
            return model;
        }
        model.setViewName("/contract/contract-preview");
        model.addObject("preview", false);
        model.addObject("contract", result.getResult());
        return model;
    }

    /**
     * 下载合同
     *
     * @param req
     * @param resp
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "/contract/download", method = RequestMethod.GET)
    @ResponseBody
    public ResultDO<String> downloadContract(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam("transactionId") Long transactionId
    ) {
        Long memberId = getMember().getId();
        ResultDO<String> downloadUrlResult = transactionService.downloadContract(memberId, transactionId);
        return downloadUrlResult;
    }

    /**
     * 会员中心收益日历本息接口
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/interest/calendar", method = RequestMethod.GET)
    @ResponseBody
    public ResultDO<TransactionInterestForCalendar> getInterestCalendar(
            HttpServletRequest req,
            HttpServletResponse resp) {
        Long memberId = getMember().getId();
        ResultDO<TransactionInterestForCalendar> result = transactionInterestService.getInterestCalendar(memberId);
        return result;
    }
    
    /**
     * 设置订单现金券金额
     * @param order
     * @throws ManagerException
     */
    private void setOrderCashCouponAmount(Order order) throws ManagerException{
    	if(StringUtil.isNotBlank(order.getCashCouponNo())){
        	Coupon coupon = couponManager.getCouponByCouponNo(order.getCashCouponNo());
        	if(coupon != null){
        		order.setUsedCouponAmount(coupon.getAmount());
        	}
        }
    }
    
    /**
     * 用户中心交易列表数据(web2)
     *
     * @param query
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/investmentListData")
    public Object transactionListData(@ModelAttribute("transactionQuery") TransactionQuery query, HttpServletRequest req, HttpServletResponse resp) {
    	ModelAndView transactionMv = new ModelAndView();
        query.setMemberId(getMember().getId());
        Page<TransactionForMemberCenter> transactionPage = transactionService.selectAllTransactionForMember(query);
        transactionMv.setViewName("/member/invest/investmentList");
        transactionMv.addObject("transactionPage", transactionPage);
        transactionMv.addObject("query",query);
        return transactionMv;
    }
    
    /**
     * 用户中心募集中的项目页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/collectingProject", method = RequestMethod.GET)
    public ModelAndView projectPage(HttpServletRequest req,
                                   HttpServletResponse resp) {
        ModelAndView transactionMv = new ModelAndView();
        transactionMv.setViewName("/member/invest/collectingProject");
        Long memberId = getMember().getId();
        int count = projectService.collectingProject(memberId);
        transactionMv.addObject("count",count);
        return transactionMv;
    }
    
    
    
    /**
     * 用户中心募集中的项目数据
     * @param query
     * @param req
     * @param resp  
     * @return
     */
    @RequestMapping(value="/collectProjectList")
    public Object memberPageData(@ModelAttribute("collectingProjectQuery") CollectingProjectQuery query,ProjectForMember projectForMember,HttpServletRequest req,
            HttpServletResponse resp) {
	    ModelAndView model = new ModelAndView();
	    query.setMemberId(getMember().getId());
    	Page<ProjectForMember> page = projectService.selectCollectProjectForMember(query);
    	model.addObject("collectProjectList", page);
    	model.addObject("query",query);
    	model.setViewName("/member/invest/collectingProjectList");
    	return model;
	}
    
    /**
     * 获取交易详情
     *
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "/collectProjectDetail", method = RequestMethod.GET)
    @ResponseBody
    public ResultDO<ProjectForMember> getCollectProjectDetails(@RequestParam("transactionId") Long transactionId) {
    	Long memberId =getMember().getId();
        return projectService.selectCollectProjectDetail(transactionId,memberId);
    }
    
    /**
     * 用户中心债权管理
     * @param req
     * @param resp  
     * @return
     */
    @RequestMapping(value = "/debt")
    public ModelAndView debtPage(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView transactionMv = new ModelAndView();
        /*    Long memberId = getMember().getId();
        query.setMemberId(memberId);*/
       /* Page<DebtForLenderMember> page = transactionService.getDebtInfoByLenderId(query);
        transactionMv.addObject("debtForLenderMember", page);*/
       /* DebtForLenderMember debtForLenderMember = transactionService.getStatisticalDataByLenderId(query);
        transactionMv.addObject("projectNum", debtForLenderMember.getProjectNum());
        transactionMv.addObject("totalInterest", debtForLenderMember.getTotalInterest());
        transactionMv.addObject("totalPrincipal", debtForLenderMember.getTotalPrincipal());
        transactionMv.addObject("sum", debtForLenderMember.getTotalInterest().add(debtForLenderMember.getTotalPrincipal()));*/
        transactionMv.setViewName("/member/debt/debt");
        return transactionMv;
    }
    
    /**
     * 用户中心债权管理列表数据
     * @param req
     * @param resp  
     * @return
     */
    @RequestMapping(value = "/debtList" , method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<DebtForLenderMember> debtPageList(@ModelAttribute("debtForLenderQuery") DebtForLenderQuery query,HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<DebtForLenderMember> result = new ResultDO<DebtForLenderMember>();
        Long memberId = getMember().getId();
        query.setMemberId(memberId);
        //项目列表数据
        Page<DebtForLenderMember> page = transactionService.getDebtInfoByLenderId(query);
        //项目个数，本息和等数据
        query.setThreeDayFlag(0);
        DebtForLenderMember debtForLenderMember = transactionService.getStatisticalDataByLenderId(query);
        result.setPage(page);
        result.setResult(debtForLenderMember);
        result.setResultList(null);
        return result;
    }
    
    /**
     * 用户中心债权管理列表数据,黄条默认显示三天数据
     * @param req
     * @param resp  
     * @return
     */
    @RequestMapping(value = "/debtListThreeDay" , method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<DebtForLenderMember> debtPageListThreeDay(@ModelAttribute("debtForLenderQuery") DebtForLenderQuery query,HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<DebtForLenderMember> result = new ResultDO<DebtForLenderMember>();
        Long memberId = getMember().getId();
        query.setMemberId(memberId);
        //项目个数，本息和等数据
        query.setThreeDayFlag(1);
        DebtForLenderMember debtForLenderMember = transactionService.getStatisticalDataByLenderId(query);
        result.setResult(debtForLenderMember);
        result.setResultList(null);
        return result;
    }
    
    /**
     * 获取交易详情
     *
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "/debtTransactionInterest", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<DebtForLenderMember> getTransactionInterestDetail(@RequestParam("projectId") Long projectId) {
    	return transactionService.getTransactionInterestDetail(projectId);
    }

    /**
     * 一键签署
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "/signAllContract")
    @ResponseBody
    public ResultDO<Object> signAllContract(@RequestParam("type") Integer type,HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMember().getId();
    	return transactionService.signAllContractByType(memberId,type);
    }
    
    /**
     * 手动签署
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "/signContract")
    @ResponseBody
    public ResultDO<Object> signContract(@RequestParam("transactionId") Long transactionId,HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMember().getId();
    	if(memberId>0){
    		return transactionService.signContract(transactionId);
    	}
    	ResultDO<Object> result = new ResultDO<Object>();
    	result.setSuccess(false);
    	return result;
    }
    
    /**
     * 自动签署单个合同
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "signContractAuto")
    @ResponseBody
    public ResultDO<Object> signContractAuto(@RequestParam("transactionId") Long transactionId,HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMember().getId();
    	return transactionService.signContractAuto(memberId,transactionId);
    }
    
    /**
     * 获取合同下载链接
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "getContractDownUrl")
    @ResponseBody
    public ResultDO<Object> getContractDownUrl(@RequestParam("transactionId") Long transactionId,HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMember().getId();
    	return transactionService.getContractDownUrl(memberId,transactionId);
    }
    
    
    
    /**
     * 发起转让项目
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "transferToProject", method = RequestMethod.POST)
	@ResponseBody
    public ResultDO<Object> transferToProject(@RequestParam("transactionId") Long transactionId,
    		@RequestParam("transferAmount") BigDecimal transferAmount,
    		HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMember().getId();
    	return transactionService.transferToProject(memberId, transactionId, transferAmount);
    }
    
    /**
     * 发起转让项目
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "ableToTransfer", method = RequestMethod.POST)
	@ResponseBody
    public ResultDO<Object> ableToTransfer(@RequestParam("transactionId") Long transactionId,
    		HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMember().getId();
    	return transactionService.ableToTransfer(memberId, transactionId);
    }

	/**
	 * 用户中心转让记录
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/transfer")
	public ModelAndView transferPage(HttpServletRequest req,
									HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Long memberId = getMember().getId();
		int count = projectService.collectingProject(memberId);
	    model.addObject("count",count);
        model.setViewName("/member/invest/transfer");
		return model;
	}
    /**
     * 用户中心转让记录列表
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/transferList")
    public ModelAndView transferList(HttpServletRequest req,
                                     HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/invest/transferList");
        return model;
    }
    /**
     * 转让项目详情
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "transferInformation")
	@ResponseBody
    public ResultDO<Object> transferInformation(@RequestParam("transactionId") Long transactionId,
    		HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMember().getId();
    	return transactionService.transferInformation(memberId, transactionId);
    }
    
    /**
     * 撤销转让项目
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "cancelTransferProject", method = RequestMethod.POST)
	@ResponseBody
    public ResultDO<Object> cancelTransferProject(@RequestParam("transactionId") Long transactionId,
    		HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMember().getId();
    	return transactionService.cancelTransferProject(memberId, transactionId);
    }
    
    /**
     * @Description:获取转让记录列表
     * @param query
     * @param req
     * @param resp
     * @return
     * @author: fuyili
     * @time:2016年9月24日 下午4:10:25
     */
    @RequestMapping(value="/transferProjectList")
    public Object transferProjectData(@ModelAttribute("transferProjectQuery") TransferProjectQuery query,HttpServletRequest req,
            HttpServletResponse resp) {
	    ModelAndView model = new ModelAndView();
	    query.setMemberId(getMember().getId());
    	Page<TransferProject> page = projectService.findTransferProjectForMemberCenter(query);
    	model.addObject("count",page.getiTotalRecords());
    	model.addObject("transferList", page);
    	model.addObject("query",query);
    	model.setViewName("/member/invest/transferList");
    	return model;
	}
    
    /**
     * 直投转让项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/ztTransferContract/preview")
    public ModelAndView ztTransferContractPreview(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.addObject("preview", true);
        model.setViewName("/contract/ztTransferContract-preview");
        return model;
    }

    /**
     * 直投项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value ="/ztTransferContract/view" , method = RequestMethod.GET)
    public ModelAndView ztTransferContract(HttpServletRequest req,
            HttpServletResponse resp  ) {
    	ModelAndView model = new ModelAndView();
    	Long orderId = ServletRequestUtils.getLongParameter(req,"orderId",0L);
    	if (orderId == 0L){
            model.setViewName("redirect:/404");
            return model;
        }
    	ResultDO<TransferContractBiz> result = transactionService.viewZtTransferContract(orderId, getMember().getId());
    	 if (!result.isSuccess()) {
             model.setViewName("redirect:/404");
             return model;
         }
    	model.addObject("preview", false);
        model.addObject("transferContractBiz", result.getResult());
        model.setViewName("/contract/ztTransferContract-preview");
        return model;
    }
    
    /**
     * 债权转让项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/transferContract/preview")
    public ModelAndView transferContractPreview(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.addObject("preview", true);
        model.setViewName("/contract/transferContract-preview");
        return model;
    }

    /**
     * 债权项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value ="/transferContract/view" , method = RequestMethod.GET)
    public ModelAndView transferContract(HttpServletRequest req,
            HttpServletResponse resp  ) {
    	ModelAndView model = new ModelAndView();
    	Long orderId = ServletRequestUtils.getLongParameter(req,"orderId",0L);
    	if (orderId == 0L){
            model.setViewName("redirect:/404");
            return model;
        }
    	ResultDO<TransferContractBiz> result = transactionService.transferContract(orderId, getMember().getId());
    	 if (!result.isSuccess()) {
             model.setViewName("redirect:/404");
             return model;
         }
    	model.addObject("preview", false);
        model.addObject("transferContractBiz", result.getResult());
        model.setViewName("/contract/transferContract-preview");
        return model;
    }

	/**
	 * 转让项目列表数据
	 * @param model
	 * @param query
     * @return
     */
    @RequestMapping(value = "/transferprojectDataList")
    public String transferProjectData(Model model , TransactionProjectBizQuery query){
		query.setMemberId(getMember().getId());
		if (query.getStatus()!=null){
			//已结束
			if (query.getStatus()==1){
				query.setStatusList(Arrays.asList(2,3));
			}
			//持有中
			if (query.getStatus()==2){
				query.setStatusList(Arrays.asList(0));
				query.setTransferStatusList(Arrays.asList(0,2));
			}
			//转让中
			if (query.getStatus()==3){
				query.setTransferStatusList(Arrays.asList(1));
			}
		}
		Page<TransactionProjectBiz> projectBizPage= null;
		try {
			projectBizPage = transactionService.queryPageTransactionProjectBiz(query);
		} catch (Exception e) {
			logger.error("转让项目列表数据异常"+e.toString());
		}
		model.addAttribute("projectBizPage",projectBizPage);
        model.addAttribute("query", query);
        return "/member/invest/transferProjectList";
    }

    /**
     * 账户中心-债权转让页面
     */
    @RequestMapping("/transferproject")
    public ModelAndView transferProject(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/invest/transferProject");
        return model;
    }

	/**
	 * 转让详情列表数据
	 * @param query
	 * @return
     */
	@ResponseBody
	@RequestMapping(value = "/transfedetaillist")
	public Object transferDetailList(TransactionProjectDetailBizQuery query){
		ResultDO<TransactionProjectDetailBiz> resultDO=new ResultDO<>();
		try {
			query.setMemberId(getMember().getId());
			query.setPageSize(1);
			Page<TransactionProjectDetailBiz> projectDetailBizPage = transactionService.queryPageTransactionProjectDetailBiz(query);
			TransactionProjectDetailBiz transactionProjectDetailBiz= transactionService.queryInvestmentedTransactionProjectDetailBiz(query.getTransactionId(),query.getMemberId());
			resultDO.setPage(projectDetailBizPage);
			resultDO.setResult(transactionProjectDetailBiz);
			resultDO.setSuccess(true);
		} catch (Exception e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("转让详情列表数据异常"+e.toString());
		}
		return resultDO;
	}
    
}
