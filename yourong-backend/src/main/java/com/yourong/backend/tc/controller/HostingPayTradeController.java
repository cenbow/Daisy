package com.yourong.backend.tc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.tc.service.HostingPayTradeService;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.HostingPayTrade;

/**
 * 代付controller
 * @author wangyanji
 *
 */
@Controller
@RequestMapping("hostingPayTrade")
public class HostingPayTradeController extends BaseController{

	@Autowired
	private HostingPayTradeService hostingPayTradeService;
	
	@RequestMapping(value = "index")
	@RequiresPermissions("hostingPayTrade:index")
	public String showOrderIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "tc/hostingPayTrade/index";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
    @RequiresPermissions("hostingPayTrade:ajax")
    @ResponseBody
    public Object findByPage(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<HostingPayTrade> pageRequest = new Page<HostingPayTrade>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
        Page<HostingPayTrade> pager = hostingPayTradeService.findByPage(pageRequest,map);		 
        return pager;
    }
}
