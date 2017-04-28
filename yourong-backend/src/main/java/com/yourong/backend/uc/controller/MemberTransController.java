package com.yourong.backend.uc.controller;

import java.util.HashMap;
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
import com.yourong.backend.tc.service.TransactionService;
import com.yourong.backend.uc.service.MemberTransService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.model.Member;

@Controller
@RequestMapping("memberTrans")
public class MemberTransController extends BaseController {

	@Autowired
	private MemberTransService memberTransService;

	@Autowired
	private TransactionService transactionService;

	/**
	 * 用户投资信息首页
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("memberTrans:index")
	public String showMemberIndex(HttpServletRequest req, HttpServletResponse 用户投资信息) {
		return "/uc/memberTrans/index";
	}

	/**
	 * 客户投资汇总
	 *
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "memberInfoAjax")
	@RequiresPermissions("memberTrans:memberInfoAjax")
	@ResponseBody
	public Object memberInfoAjax(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		Long mobile = ServletRequestUtils.getLongParameter(req, "mobile", 0L);
		Map<String, Object> map = new HashMap<String, Object>();
		if(id != null && id > 0L) {
			map.put("id", id);
		}
		if(mobile != null && mobile > 0L) {
			map.put("mobile", mobile);
		}
		return memberTransService.getMemberTransInfo(map);
	}
	
	/**
	 * 用户投资记录
	 *
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "transRecordAjax")
	@RequiresPermissions("memberTrans:transRecordAjax")
	@ResponseBody
	public Object transRecordAjax(@ModelAttribute("memberId")  String memberId, HttpServletRequest req,
								  HttpServletResponse resp) throws Exception {
		Page<Transaction> pageRequest = new Page<Transaction>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		map.put("memberId", memberId);
		Page<Transaction> pager = transactionService.findByPage(pageRequest, map);
		return pager;
	}
	
}
