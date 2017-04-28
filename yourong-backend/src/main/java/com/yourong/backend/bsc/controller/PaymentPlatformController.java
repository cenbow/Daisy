package com.yourong.backend.bsc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.bsc.service.PaymentPlatformService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.PaymentPlatform;

/**
 * 支付平台管理
 * @author wangyanji
 *
 */
@Controller
@RequestMapping("paymentPlatform")
public class PaymentPlatformController extends BaseController {
    
	@Autowired
    private PaymentPlatformService paymentPlatformService;

    @RequestMapping(value = "index")
    @RequiresPermissions("paymentPlatform:index")
    public String showPaymentPlatformIndex(HttpServletRequest req, HttpServletResponse resp) {
         return "/bsc/paymentPlatform/index";
    }

    /**
     * 查询
     * @param req
     * @param resp
     * @return
     * @throws ServletRequestBindingException
     */
    @RequestMapping(value = "ajax")
    @RequiresPermissions("paymentPlatform:ajax")
    @ResponseBody
    public Object showPaymentPlatformPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<PaymentPlatform> pageRequest = new Page<PaymentPlatform>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest); 
        	Page<PaymentPlatform> pager = paymentPlatformService.findByPage(pageRequest,map);		 
         return pager;
    }

    /**
     * 保存
     * @param paymentPlatform
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "save")
    @RequiresPermissions("paymentPlatform:save")
    @ResponseBody
    public Object savePaymentPlatform(@ModelAttribute PaymentPlatform paymentPlatform, HttpServletRequest req, HttpServletResponse resp) {
    	ResultDO<PaymentPlatform> returnDO = paymentPlatformService.save(paymentPlatform);
        return returnDO;		 
    }
    
    /**
     * 删除
     * @param paymentPlatform
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "del")
    @RequiresPermissions("paymentPlatform:del")
    @ResponseBody
    public Object delPaymentPlatform(@ModelAttribute PaymentPlatform paymentPlatform, HttpServletRequest req, HttpServletResponse resp) {
    	Long id =  ServletRequestUtils.getLongParameter(req, "id", 0); 
    	ResultDO<PaymentPlatform> returnDO = paymentPlatformService.delPaymentPlatform(id);
        return returnDO;		 
    }

    /**
     * 保存维护公告
     * @param paymentPlatform
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "saveMaint")
    @RequiresPermissions("paymentPlatform:saveMaint")
    @ResponseBody
    public Object saveMaintence(@ModelAttribute PaymentPlatform paymentPlatform, HttpServletRequest req, HttpServletResponse resp) {
    	ResultDO<PaymentPlatform> returnDO = paymentPlatformService.saveMaintence(paymentPlatform);
        return returnDO;		 
    }

    /**
     * 结束维护公告
     * @param paymentPlatform
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "delMaint")
    @RequiresPermissions("paymentPlatform:delMaint")
    @ResponseBody
    public Object delMaintence(@ModelAttribute PaymentPlatform paymentPlatform, HttpServletRequest req, HttpServletResponse resp) {
    	Long id =  ServletRequestUtils.getLongParameter(req, "id", 0); 
    	ResultDO<PaymentPlatform> returnDO = paymentPlatformService.delMaintence(id);
        return returnDO;		 
    }
}