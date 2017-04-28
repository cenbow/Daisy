package com.yourong.api.controller;

import java.math.BigDecimal;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.api.dto.OrderDto;
import com.yourong.api.dto.OrderForAppDto;
import com.yourong.api.dto.OrderForMemberDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.OrderService;
import com.yourong.api.service.ProjectService;
import com.yourong.api.utils.SysServiceUtils;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.query.OrderQuery;
import com.yourong.core.uc.model.Member;

@Controller
@RequestMapping("security/order")
public class OrderController extends BaseController {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ProjectService projectService;
	
	/**
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "investment", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO investment(HttpServletRequest req, HttpServletResponse resp){
		Long projectId = ServletRequestUtils.getLongParameter(req, "pid",0L);
		Double investAmount = ServletRequestUtils.getDoubleParameter(req, "investAmount",-1D);
		Long memberId = getMemberID(req);
		ResultDTO resultDTO = orderService.investment(projectId, new BigDecimal(investAmount), memberId);
		return resultDTO;
	}
	
	/**
	 * 创建订单
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "createOrder", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO createOrder(@Valid OrderDto orderDto, BindingResult result, HttpServletRequest req, HttpServletResponse resp){
		Order order = new Order();
		BeanCopyUtil.copy(orderDto, order);
		Long memberID = getMemberID(req);
		//黑名单
		Set<Long> blackMemberId = SysServiceUtils.getBlackMemberId();
		if(blackMemberId.contains(memberID)){
			try {
				Thread.sleep(PropertiesUtil.theadSuspend());
			} catch (Exception e) {
				logger.error("线程中断",e);
			}
		}
		order.setMemberId(getMemberID(req));
		order.setOrderSource(getRequestSource(req));
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		validateResult(resultDTO, result);
		if(resultDTO.isSuccess()) {
			try {
				resultDTO = orderService.saveOrder(order);
			} catch (Exception e) {
				resultDTO.setResultCode(ResultCode.ORDER_FRONT_SAVE_FAIL_ERROR);
				return resultDTO;
			}
		}
		return resultDTO;
	}
	
	/**
	 * 创建订单
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "createOrder", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	@ResponseBody
	public ResultDTO createOrderSinaBank(@Valid OrderDto orderDto, BindingResult result, HttpServletRequest req, HttpServletResponse resp){
		Order order = new Order();
		BeanCopyUtil.copy(orderDto, order);
		Long memberID = getMemberID(req);
		//黑名单
		Set<Long> blackMemberId = SysServiceUtils.getBlackMemberId();
		if(blackMemberId.contains(memberID)){
			try {
				Thread.sleep(PropertiesUtil.theadSuspend());
			} catch (Exception e) {
				logger.error("线程中断",e);
			}
		}
		order.setMemberId(getMemberID(req));
		order.setOrderSource(getRequestSource(req));
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		validateResult(resultDTO, result);
		if(resultDTO.isSuccess()) {
			Member member = memberService.selectByPrimaryKey(memberID);
			// 未设置支付密码
			if (StatusEnum.SET_PAY_SUCCESS_FLAG_Y.getStatus() !=member.getPayPasswordFlag() ) {
				resultDTO.setResultCode(ResultCode.PAY_PASSWORD_APP_NOT_SET_ERROR);
				return resultDTO;
			}
			
			try {
				resultDTO = orderService.saveOrderSina(order);
			} catch (Exception e) {
				resultDTO.setResultCode(ResultCode.ORDER_FRONT_SAVE_FAIL_ERROR);
				return resultDTO;
			}
		}
		return resultDTO;
	}
	
	/**
	 * 根据用户编号获得用户订单
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryOrderForMember", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO p2p2QueryOrderListByMemberId(@ModelAttribute("orderQuery") OrderQuery orderQuery, HttpServletRequest req, HttpServletResponse resp){
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		ResultDTO resultDto = new ResultDTO();
		Long memberId = getMemberID(req);
		orderQuery.setMemberId(memberId);
		orderQuery.setPageSize(20);
		orderQuery.setCurrentPage(pageNo);
		orderQuery.setInvestType(ProjectEnum.PROJECT_TYPE_DEBT.getType());
		Page<OrderForMemberDto> orderList = orderService.queryOrderyForMemberP2p(orderQuery);
		resultDto.setResult(orderList);
		return resultDto;
	}
	
	/**
	 * 根据用户编号获得用户订单
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryOrderForMember", method = RequestMethod.POST, headers = {"Accept-Version=1.3.0"})
	@ResponseBody
	public ResultDTO queryOrderListByMemberId(@ModelAttribute("orderQuery") OrderQuery orderQuery, HttpServletRequest req, HttpServletResponse resp){
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		ResultDTO resultDto = new ResultDTO();
		Long memberId = getMemberID(req);
		orderQuery.setMemberId(memberId);
		orderQuery.setPageSize(20);
		orderQuery.setCurrentPage(pageNo);
		Page<OrderForMemberDto> orderList = orderService.queryOrderyForMember(orderQuery);
		resultDto.setResult(orderList);
		return resultDto;
	}
	
	/**
	 * 取消订单
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "cancelOrder", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO cancelOrder(HttpServletRequest req, HttpServletResponse resp){
		Long orderId = ServletRequestUtils.getLongParameter(req, "orderId",0L);
		Long memberId = getMemberID(req);
		ResultDTO resultDto = orderService.cancelOrder(orderId, RemarksEnum.ORDER_CANCEL_ORDER.getRemarks(), memberId);
		return resultDto;
	}
	
	/**
	 * 订单支付信息
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryPayOrderInfo", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO queryPayOrderInfo(HttpServletRequest req, HttpServletResponse resp){
		Long orderId = ServletRequestUtils.getLongParameter(req, "orderId",0L);
		String cashCouponNo = ServletRequestUtils.getStringParameter(req, "cashCouponNo","");
		Long memberId = getMemberID(req);
		return orderService.queryPayOrderInfo(orderId, cashCouponNo, memberId);
	}
	
	/**
	 * 查询订单支付信息
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryPayOrderInfo", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	@ResponseBody
	public ResultDTO queryPayOrderInfoSinaBank(HttpServletRequest req, HttpServletResponse resp){
		Long orderId = ServletRequestUtils.getLongParameter(req, "orderId",0L);
		Long memberId = getMemberID(req);
		return orderService.queryPayOrderInfoSinaBank(orderId,memberId);
	}
	
	/**
	 * 订单支付后的临时页
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "afterInvest", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public Object tempPageAfterInvest(HttpServletRequest req, HttpServletResponse resp) {
		Long orderId = ServletRequestUtils.getLongParameter(req, "orderId", 0L);
		Long memberId = getMemberID(req);
		return orderService.tempPageAfterInvest(orderId, memberId);
	}
	
	/**
	 * 订单支付后的 抽奖接口
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "directLottery", method = RequestMethod.POST, headers = { "Accept-Version=1.9.1" })
	@ResponseBody
	public Object directLottery(HttpServletRequest req, HttpServletResponse resp) {
		Long transactionId = ServletRequestUtils.getLongParameter(req, "transactionId", 0L);
		int type = ServletRequestUtils.getIntParameter(req, "type", 1);//1 抽一次， 2全抽
		Long memberId = getMemberID(req);
		return orderService.directLottery(memberId, transactionId,type);
	}
	
	
}
