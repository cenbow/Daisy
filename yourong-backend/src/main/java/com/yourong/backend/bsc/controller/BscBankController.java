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
import com.yourong.backend.bsc.service.BscBankService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.BscBank;

/**
 * 银行管理
 * @author wangyanji
 *
 */
@Controller
@RequestMapping("bscBank")
public class BscBankController extends BaseController {
    @Autowired
    private BscBankService bscBankService;

    @RequestMapping(value = "index")
    @RequiresPermissions("bscBank:index")
    public String showSysDictIndex(HttpServletRequest req, HttpServletResponse resp) {
         return "/bsc/bscBank/index";
    }

    @SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
    @RequiresPermissions("bscBank:ajax")
    @ResponseBody
    public Object showSysDictPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<BscBank> pageRequest = new Page<BscBank>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		return bscBankService.findByPage(pageRequest,map);		 
    }

    @RequestMapping(value = "save")
    @RequiresPermissions("bscBank:save")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "银行管理",desc = "保存银行信息")
    public Object saveBscBank(@ModelAttribute BscBank bscBank, HttpServletRequest req, HttpServletResponse resp) {
    	return bscBankService.saveBscBank(bscBank);
    }


    @RequestMapping(value = "delete")
    @RequiresPermissions("bscBank:delete")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "银行管理",desc = "删除银行信息")
    public Object deleteBank(HttpServletRequest req, HttpServletResponse resp) {
    	long[] ids = ServletRequestUtils.getLongParameters(req, "ids[]"); 
    	BscBank bscBank = new BscBank();
    	bscBank.setIds(ids);
    	return bscBankService.batchDeleteBank(bscBank);
    }
    
    @RequestMapping(value = "getBankList")
    @RequiresPermissions("bscBank:getBankList")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "银行管理",desc = "获取银行列表")
    public Object getBankList(@ModelAttribute BscBank bscBank, HttpServletRequest req, HttpServletResponse resp) {
    	return bscBankService.getBankList();
    }
}