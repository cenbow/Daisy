package com.yourong.backend.tc.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.tc.service.OrderService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.OrderForMember;

@Controller
@RequestMapping("order")
/**
 * 后台订单controller
 * @author wangyanji
 *
 */
public class OrderController extends BaseController{

	@Autowired
	private OrderService orderService;
	
	@RequestMapping(value = "index")
	@RequiresPermissions("order:index")
	public String showOrderIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "tc/order/index";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
    @RequiresPermissions("order:ajax")
    @ResponseBody
    public Object showOrderPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<OrderForMember> pageRequest = new Page<OrderForMember>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
        	Page<OrderForMember> pager = orderService.findByPage(pageRequest,map);		 
         return pager;
    }
	
	/**
	 * 取消订单
	 * 
	 * @author wangyanji
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "cancelOrder")
	@RequiresPermissions("order:cancelOrder")
	@ResponseBody
	public Object cancelOrder(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		return orderService.cancelOrder(id);
	}
	
	/**
	 * 刷新订单
	 * 
	 * @author wangyanji
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "reflashOrder")
	@RequiresPermissions("order:reflashOrder")
	@ResponseBody
	public Object reflashOrder(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		return orderService.reflashOrder(id);
	}
	
	/**
	 * 投资人信息
	 * 
	 * @author wangyanji
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "transPersonInfo")
	@RequiresPermissions("order:transPersonInfo")
	@ResponseBody
	public Object transPersonInfo(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String id = ServletRequestUtils.getStringParameter(req, "id");
		return orderService.selectTransPersonByOrderNo(id);
	}
	
	/**
	 * 关闭订单待确认订单
	 * 
	 * @author zhanghao
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "closeDirectProjectOrder")
	@RequiresPermissions("order:closeDirectProjectOrder")
	@ResponseBody
	public Object closeDirectProjectOrder(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		try {
			return orderService.closeDirectProjectOrder(id);
		} catch (Exception e) {
			ResultDO<Order> result = new ResultDO<Order>();
			result.setResultCode(ResultCode.ERROR_SYSTEM);
			return result;
		}
	}

	/**
	 * 查询退款状态
	 * @param id
	 * @return
     */
	@ResponseBody
	@RequestMapping(value = "returnOrderStatus")
	@RequiresPermissions("order:returnOrderStatus")
	public Object returnOrderStatus(@RequestParam(value = "id",required = true,defaultValue = "0") String id){
		String status= orderService.queryStatusByOrderId(id);
		Map<String,String> result=new HashMap<String, String>();
		if (!StringUtils.isEmpty(status)){
			result.put("code","1");
			result.put("status",status);
			return result;
		}
		result.put("code","-1");
		result.put("status",status);
		return result;
	}
}
