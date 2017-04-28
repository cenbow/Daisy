/**
 * 
 */
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
import com.yourong.backend.tc.service.ContractSignService;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.ContractSignDto;

/**
 * @desc 合同签署
 * @author zhanghao
 * 2016年7月22日下午3:58:47
 */
@Controller
@RequestMapping("contractSign")
public class ContractSignController extends BaseController {

	@Autowired
	private ContractSignService contractSignService;
	
	
	/**
	 * 用户签署合同信息
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("contractSign:index")
	public String showMemberIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/tc/contractSign/index";
	}
	

	/**
	 * 分页查询客户信息
	 *
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "ajax")
	@RequiresPermissions("contractSign:ajax")
	@ResponseBody
	public Object showMemberPages(HttpServletRequest req,
								  HttpServletResponse resp) throws ServletRequestBindingException {
		Page<ContractSignDto> pageRequest = new Page<ContractSignDto>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<ContractSignDto> pager = contractSignService.findByPage(pageRequest, map);
		return pager;
	}
	
	
	
}
