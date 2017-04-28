package com.yourong.backend.fin.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.fin.service.LoanDetailService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.LoanDetail;
/**
 * 放款详情模块
 * @author fuyili
 * 2015年1月23日上午11:52:54
 */
@Controller
@RequestMapping("loanDetail")
public class LoanDetailController extends BaseController {
    @Autowired
    private LoanDetailService loanDetailService;

    @RequestMapping(value = "index")
    @RequiresPermissions("loanDetail:index")
    public String showLoanDetailIndex(HttpServletRequest req, HttpServletResponse resp) {
         return "/fin/loanDetail/index";
    }

    @SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
    @RequiresPermissions("loanDetail:ajax")
    @ResponseBody
    public Object showLoanDetailPages(@RequestParam("projectId") Long projectId,HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
        Page<LoanDetail> pageRequest = new Page<LoanDetail>();
		Map<String,Object> map = getPageInfoFromRequest(req, pageRequest );	 
		map.put("projectId", projectId);
		Page<LoanDetail> pager = loanDetailService.findByPage(pageRequest,map);		 
		return pager;
    }

    @RequestMapping(value = "save")
    @RequiresPermissions("loanDetail:save")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "放款详情模块",desc = "保存放款详情")
    public Object saveLoanDetail(@ModelAttribute LoanDetail loanDetail, HttpServletRequest req, HttpServletResponse resp) {
    	int insertSelective = loanDetailService.insertSelective(loanDetail);
    	return insertSelective;
    }	 
    

	@RequestMapping(value = "query")
    @RequiresPermissions("loanDetail:query")
    @ResponseBody
    public Object showLoanDetailListPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
        Page<LoanDetail> pageRequest = new Page<LoanDetail>();
		Map<String,Object> map = getPageInfoFromRequest(req, pageRequest );	 
		Page<LoanDetail> pager = loanDetailService.findByPage(pageRequest,map);		 
		return pager;
    }

}