package com.yourong.backend.sys.controller;

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
import com.yourong.backend.sys.service.SysDictService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysDict;

@Controller
@RequestMapping("sysDict")
public class SysDictController extends BaseController {
    @Autowired
    private SysDictService sysDictService;

    @RequestMapping(value = "index")
    @RequiresPermissions("sysDict:index")
    public String showSysDictIndex(HttpServletRequest req, HttpServletResponse resp) {
         return "/sys/sysDict/index";
    }

    @RequestMapping(value = "ajax")
    @RequiresPermissions("sysDict:ajax")
    @ResponseBody
    public Object showSysDictPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<SysDict> pageRequest = new Page<SysDict>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
        	Page<SysDict> pager = sysDictService.findByPage(pageRequest,map);		 
         return pager;
    }

    @RequestMapping(value = "save")
    @RequiresPermissions("sysDict:save")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "字典模块",desc = "保存字典")
    public Object saveSysDict(@ModelAttribute SysDict sysDict, HttpServletRequest req, HttpServletResponse resp) {
        if(sysDict.getId()!=null){ 
        	int insertSelective = sysDictService.updateByPrimaryKeySelective(sysDict); 
         }else{	 
        	int insertSelective = sysDictService.insert(sysDict);
         }	 
        	return "1";		 
    }

    @RequestMapping(value = "show")
    @RequiresPermissions("sysDict:show")
    @ResponseBody
    public Object showSysDict(HttpServletRequest req, HttpServletResponse resp) {
        long id =  ServletRequestUtils.getIntParameter(req, "id", 0); 
        SysDict sysDict = sysDictService.selectByPrimaryKey(id);	 
        	return sysDict;		 
    }

    @RequestMapping(value = "delete")
    @RequiresPermissions("sysDict:delete")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "字典模块",desc = "删除字典")
    public Object deleteSysDict(HttpServletRequest req, HttpServletResponse resp) {
        long[] id = ServletRequestUtils.getLongParameters(req, "id[]"); 
        sysDictService.batchDelete(id); 
        	return "1";		 
    }
}