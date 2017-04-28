package com.yourong.backend.uc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.uc.service.MemberBankCardService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.MemberBankCard;

/**
 * 银行卡管理
 * @author wangyanji
 *
 */
@Controller
@RequestMapping("memberBankCard")
public class MemberBankCardController extends BaseController {

    @Autowired
    private MemberBankCardService memberBankCardService;

    @RequestMapping(value = "index")
    @RequiresPermissions("memberBankCard:index")
    public String showSysDictIndex(HttpServletRequest req, HttpServletResponse resp) {
         return "/uc/memberBankCard/index";
    }
    
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
    @RequiresPermissions("memberBankCard:ajax")
    @ResponseBody
    public Object findByPage(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<MemberBankCard> pageRequest = new Page<MemberBankCard>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
        	Page<MemberBankCard> pager = memberBankCardService.queryMemberCard(pageRequest,map);		 
         return pager;
    }
    
	@RequestMapping(value = "deleteSafeCard")
    @RequiresPermissions("memberBankCard:deleteSafeCard")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "银行卡管理",desc = "删除安全卡")
    public Object deleteSafeCard(HttpServletRequest req, HttpServletResponse resp) {
		long memberId = ServletRequestUtils.getLongParameter(req, "memberId", 0); 
		return memberBankCardService.deleteBankCardByMemberId(memberId);
    }
}
