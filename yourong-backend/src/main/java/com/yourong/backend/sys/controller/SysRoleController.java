package com.yourong.backend.sys.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.sys.service.SysRoleService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.common.web.ResultObject;
import com.yourong.core.sys.model.SysMenu;
import com.yourong.core.sys.model.SysRole;

@Controller
@RequestMapping("sysRole")
public class SysRoleController extends BaseController {

	@Autowired
	private SysRoleService sysRoleService;

	/**
	 * 设置角色权限
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "roleTree")
	@RequiresPermissions("sysRole:roleTree")
	public String showRoleMenus(HttpServletRequest req, HttpServletResponse resp) {
		return "/sys/sysRole/roleTree";
	}

	@RequestMapping(value = "index")
	@RequiresPermissions("sysRole:index")
	public String showSysRoleIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/sys/sysRole/index";
	}

	@RequestMapping(value = "ajax")
	@RequiresPermissions("sysRole:ajax")
	@ResponseBody
	public Object showSysRolePages(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<SysRole> pageRequest = new Page<SysRole>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<SysRole> pager = sysRoleService.findByPage(pageRequest, map);
		return pager;
	}
	
	
	@RequestMapping(value = "ajaxTree")
	@RequiresPermissions("sysRole:ajaxTree")
	@ResponseBody
	public Object showRoleTree(HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		List<SysMenu> roleTree = sysRoleService.selectRoleTree(id);
		return roleTree;
	}
	
	@RequestMapping(value = "updateRoleTree")
	@RequiresPermissions("sysRole:updateRoleTree")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "角色模块",desc = "更新角色树")
	public Object updateRoleTree(@ModelAttribute("id") long id,@ModelAttribute("menus") String menus ,HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		String[] split = menus.split(",");
		long[] menu = new long[split.length];		
		for(int i=0;i<split.length;i++){
			menu[i] = NumberUtils.toInt(split[i]);
		}		
		ResultObject object = sysRoleService.batchInsertRoleAndMenus(id, menu);		
		return object;
	}
	
	

	@RequestMapping(value = "save")
	@RequiresPermissions("sysRole:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "角色模块",desc = "保存角色")
	public Object saveSysRole(@ModelAttribute SysRole sysRole,
			HttpServletRequest req, HttpServletResponse resp) {
		int insertSelective = 0;
		if (sysRole.getId() != null) {
			boolean exists = sysRoleService.checkRoleNameExists(sysRole.getName(), sysRole.getId());
			if(!exists){
				insertSelective = sysRoleService.updateByPrimaryKeySelective(sysRole);
			}
		} else {
			boolean exists = sysRoleService.checkRoleNameExists(sysRole.getName());
			if(!exists){
				insertSelective = sysRoleService.insert(sysRole);
			}
		}
		return insertSelective;
	}

	@RequestMapping(value = "show")
	@RequiresPermissions("sysRole:show")
	@ResponseBody
	public Object showSysRole(HttpServletRequest req, HttpServletResponse resp) {
		long id = ServletRequestUtils.getIntParameter(req, "id", 0);
		SysRole sysRole = sysRoleService.selectByPrimaryKey(id);
		return sysRole;
	}

	@RequestMapping(value = "delete")
	@RequiresPermissions("sysRole:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "角色模块",desc = "删除角色")
	public Object deleteSysRole(HttpServletRequest req, HttpServletResponse resp) {
		long[] ids = getParametersIds(req, "ids");
		sysRoleService.batchDelete(ids);
		return "1";
	}

}