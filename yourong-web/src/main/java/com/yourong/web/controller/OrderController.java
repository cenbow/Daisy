package com.yourong.web.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.pay.PayMentService;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.NewOrderBiz;
import com.yourong.core.tc.model.biz.OrderForMember;
import com.yourong.core.tc.model.query.OrderQuery;
import com.yourong.web.dto.OrderDto;
import com.yourong.web.dto.ProjectInfoDto;
import com.yourong.web.service.MemberBankCardService;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.OrderService;
import com.yourong.web.service.ProjectService;
import com.yourong.web.service.TransactionService;
import com.yourong.web.utils.ServletUtil;
import com.yourong.web.utils.SysServiceUtils;

@Controller
@RequestMapping("order")
/**
 * 前台订单controller
 * @author Administrator
 *
 */
public class OrderController extends BaseController{

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private PayMentService payMentService; 
	
	@Autowired
	private SinaPayClient sinaPayClient;

	@Autowired
	private MemberBankCardService memberBankCardService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private ProjectService projectService;
	
	/**
	 * 保存订单方法
	 * @param orderDto
	 * @param result
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "save",method = RequestMethod.POST)	
	@ResponseBody
	public ResultDO<Order> saveOrder(@Valid OrderDto orderDto, BindingResult result,
			HttpServletRequest req, HttpServletResponse resp) {	
		Order order = new Order();
		BeanCopyUtil.copy(orderDto, order);
		order.setMemberId(getMember().getId());
		boolean isMobile = ServletUtil.isMobile(req);
        if(isMobile){
        	order.setOrderSource(TypeEnum.ORDER_SOURCE_WAP.getType());
        }else{
        	order.setOrderSource(TypeEnum.ORDER_SOURCE_PC.getType());
        }
		ResultDO<Order> resultDO = new ResultDO<Order>();
		validateResult(resultDO, result);
		if(resultDO.isSuccess()) {
			//碰到是否会员是否实名认证和手机认证
			if(!memberService.isAuthIdentityAndPhone(order.getMemberId())) {
				resultDO.setResultCode(ResultCode.MEMBER_IS_NOT_AUTH_ID_AND_PHONE_NULL);
				return resultDO;
			}
			// 未设置支付密码
			if (StatusEnum.SET_PAY_SUCCESS_FLAG_Y.getStatus() != getMember().getPayPasswordFlag()) {
				resultDO.setResultCode(ResultCode.PAY_PASSWORD_NOT_SET_ERROR);
				return resultDO;
			}
			try {
				Long memberId = getMember().getId();
				//黑名单
				Set<Long> blackMemberId = SysServiceUtils.getBlackMemberId();
				if(blackMemberId.contains(memberId)){
					try {
						Thread.sleep(PropertiesUtil.theadSuspend());
					} catch (Exception e) {
						logger.error("线程中断",e);
					}
				}
				resultDO = orderService.saveOrder(order);
			} catch (Exception e) {
				resultDO.setResultCode(ResultCode.ORDER_FRONT_SAVE_FAIL_ERROR);
				return resultDO;
			}
		}
		return resultDO;
	}
	
	/**
	 * 跳转到创建订单页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "/new")
	public String toOrderForm(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {

		Long projectId = ServletRequestUtils.getLongParameter(req, "projectId");
		Double investAmount = ServletRequestUtils.getDoubleParameter(req, "investAmount");
		String url = "redirect:/products/list-all-all-all-1.html";
		if (projectId == null || investAmount == null) {
			return url;
		}
		Long memberId = getMember().getId();
		ProjectInfoDto project = projectService.getAllProjectInfo(projectId, memberId);
		// 判断是否可以投资
		if (!project.isActive()) {
			return "redirect:/products/detail-" + projectId + ".html";
		}
		if (project.isNoviceProject() && transactionService.hasTransactionByMember(memberId)) {
			return url;
		}
		NewOrderBiz newOrderBiz = new NewOrderBiz();
		req.setAttribute("newOrderBiz", newOrderBiz);
		req.setAttribute("invertingProject", project);
		return "/order/new";
	}

	/**
	 * 用户中心订单列表
	 * 
	 * @param req
	 * @param resp
	 * @param orderQuery
	 * @return
	 */
	@RequestMapping(value = "front/member/orders")
    @ResponseBody
    public ResultDO<Object> selectOrders(HttpServletRequest req, HttpServletResponse resp, OrderQuery orderQuery) {
		OrderQuery query = new OrderQuery();
		query.setMemberId(getMember().getId());
        Page<OrderForMember> pager = orderService.selectAllOrderForMember(query);		 
         return new ResultDO<Object>(pager);
    }
	
	/**
	 * 跳转到订单支付页面
	 * @param req
	 * @param resp
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value="/to/pay")
	public ModelAndView toPayOrder(HttpServletRequest req, HttpServletResponse resp) {
		String orderNo = ServletRequestUtils.getStringParameter(req, "orderNo", null);
		ResultDO<Object> result = orderService.getPayOrderDetail(orderNo, getMember().getId());
		ModelAndView model  = new ModelAndView();
		model.setViewName("/order/pay");
		model.addObject("result", result);
		return model;
	}

	/**
     * 取消订单
     * @param req
     * @param resp
     * @param orderQuery
     * @return
     */
	@RequestMapping(value = "/member/cancel/order",method=RequestMethod.GET)
    @ResponseBody
    public ResultDO<Order> cancelOrder(HttpServletRequest req, HttpServletResponse resp, @RequestParam("orderId")Long orderId) {
		 ResultDO<Order> result = orderService.cancelOrder(orderId,RemarksEnum.ORDER_CANCEL_ORDER.getRemarks(), getMember().getId());		 
         return result;
    }
	
	/**
	 * 获取订单详情
	 * @param transactionId
	 * @return
	 */
	@RequestMapping(value = "/detail")
    @ResponseBody
	public ResultDO<OrderForMember> getOrderDetails(@RequestParam("orderId") Long orderId) {
		return orderService.getOrderForMember(orderId, getMember().getId());
	}
	
	
	/**
     *  用户中心订单列表页面
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/order",method=RequestMethod.GET)
    public ModelAndView ordersPage(HttpServletRequest req,
                                 HttpServletResponse resp) {
    	ModelAndView orderMv = new ModelAndView();
    	Long memberId = getMember().getId();
        int count = projectService.collectingProject(memberId);
        orderMv.addObject("count",count);
        orderMv.setViewName("/member/invest/order");
        return  orderMv;
    }
    
    
    /**
     * 用户中心订单列表数据
     * @param query
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value="/orderList",method=RequestMethod.GET)
    public ModelAndView ordersPageData(@ModelAttribute("orderQuery")OrderQuery query,HttpServletRequest req,
            HttpServletResponse resp) {
	    ModelAndView model = new ModelAndView();
    	query.setMemberId(getMember().getId());
    	Page<OrderForMember> page = orderService.selectAllOrderForMember(query);
    	model.addObject("ordersForPage", page);
    	model.addObject("query",query);
    	model.setViewName("/member/invest/orderList");
    	return model;
	}
	
	
	
}
