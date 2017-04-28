package com.yourong.backend.sys.controller;

import java.util.List;
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
import com.yourong.backend.cache.MyCacheManager;
import com.yourong.backend.sys.service.SysMenuService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysMenu;

@Controller
@RequestMapping("sysMenu")
public class SysMenuController extends BaseController {
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private MyCacheManager myCacheManager;
    
    @RequestMapping(value = "index")
    @RequiresPermissions("sysMenu:index")
    public String showSysMenuIndex(HttpServletRequest req, HttpServletResponse resp) {
         return "/sys/sysMenu/index";
    }

    @RequestMapping(value = "ajax")
    @RequiresPermissions("sysMenu:ajax")
    @ResponseBody
    public Object showSysMenuPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<SysMenu> pageRequest = new Page<SysMenu>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
        	Page<SysMenu> pager = sysMenuService.findByPage(pageRequest,map);		 
         return pager;
    }
    
	@RequestMapping(value = "allMenu")
	@RequiresPermissions("sysMenu:ajax")
	@ResponseBody
	public Object showAllSysMenu(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		List<SysMenu> allSysmenu = myCacheManager.getAllMenu();
		return allSysmenu;
	}
    
    
    

    @RequestMapping(value = "save")
    @RequiresPermissions("sysMenu:save")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "菜单模块",desc = "保存菜单")
    public Object saveSysMenu(@ModelAttribute SysMenu sysMenu, HttpServletRequest req, HttpServletResponse resp) {
        if(sysMenu.getId()!=null){ 
        	int insertSelective = sysMenuService.updateByPrimaryKeySelective(sysMenu); 
         }else{	 
        	int insertSelective = sysMenuService.insert(sysMenu);
         }	 
    	return "1";		 
    }

    @RequestMapping(value = "show")
    @RequiresPermissions("sysMenu:show")
    @ResponseBody
    public Object showSysMenu(HttpServletRequest req, HttpServletResponse resp) {
        long id =  ServletRequestUtils.getLongParameter(req, "id", 0); 
        SysMenu sysMenu = sysMenuService.selectByPrimaryKey(id);	 
        	return sysMenu;		 
    }

    @RequestMapping(value = "delete")
    @RequiresPermissions("sysMenu:delete")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "菜单模块",desc = "删除菜单")
    public Object deleteSysMenu(HttpServletRequest req, HttpServletResponse resp) {
        long[] id = ServletRequestUtils.getLongParameters(req, "id[]"); 
        sysMenuService.batchDelete(id); 
        	return "1";		 
    }
}