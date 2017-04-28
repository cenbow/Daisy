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
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.fin.service.LoanService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.biz.LoanBiz;

@Controller
@RequestMapping("loan")
public class LoanController extends BaseController { 
	@Autowired
	private LoanService loanService;
	
	@Autowired
	private BalanceManager balanceManager;
	
	/**
	 * 放款列表页面
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("loan:index")
	public String showLoanIndex(HttpServletRequest req,
			HttpServletResponse resp){
		return "/fin/loan/index";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("loan:ajax")
	@ResponseBody
	public Object showLoanListData(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException{
		Page<LoanBiz> pageRequest = new Page<LoanBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		
		return loanService.findByPage(pageRequest, map);
	}
	
	@RequestMapping(value = "showLoan")
	@RequiresPermissions("loan:showLoan")
	@ResponseBody
	public Object showLoanBiz(@ModelAttribute("projectId") Long projectId,HttpServletRequest req,
			HttpServletResponse resp) throws NumberFormatException, ManagerException{
		Map<String, Object> map = Maps.newHashMap();
		//获取平台余额
		Balance balance = balanceManager.queryBalance(Long.valueOf(Config.internalMemberId), TypeEnum.BALANCE_TYPE_BASIC);
		//获取放款基本信息
		LoanBiz loanBiz = loanService.findLoanBiz(projectId);
		map.put("balance", balance.getBalance());
		map.put("loanBiz",loanBiz);
		return map;
	}
	
	@RequestMapping(value = "synchronizedBaseBalance")
	@RequiresPermissions("loan:synchronizedBaseBalance")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "放款模块",desc = "同步平台余额")
	public Object synchronizedBaseBalance(HttpServletRequest req,
			HttpServletResponse resp) throws NumberFormatException, ManagerException{	
		ResultDto<Balance> resultDto = new ResultDto<>();
		//获取平台余额
		Balance balance = null;
		try {
			balance = balanceManager.synchronizedBalance(Long.valueOf(Config.internalMemberId), TypeEnum.BALANCE_TYPE_BASIC);
			resultDto.setModule(balance);
			resultDto.setSuccess(true);
		} catch (Exception e2) {
			resultDto.setSuccess(false);
		    resultDto.setErrorMsg("第三方支付查询余额失败");	
		}			
		return balance;
	}
	
	
	@RequestMapping(value = "basebalance")
	@RequiresPermissions("loan:basebalance")
	public String basebalance(HttpServletRequest req,
			HttpServletResponse resp){
		return "/fin/capital/index";
	}

}
