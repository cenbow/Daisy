package com.yourong.backend.sys.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.util.CryptHelper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.sys.service.SysUserService;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysUser;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("sysUser")
public class SysUserController extends BaseController {
	@Autowired
	private SysUserService sysUserService;

	@RequestMapping(value = "index")
	@RequiresPermissions("sysUser:index")
	public String showSysUserIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/sys/sysUser/index";
	}

	@RequestMapping(value = "ajax")
	@RequiresPermissions("sysUser:ajax")
	@ResponseBody
	public Object showSysUserPages(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<SysUser> pageRequest = new Page<SysUser>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<SysUser> pager = sysUserService.findByPage(pageRequest, map);
		return pager;
	}

	@RequestMapping(value = "save")
	@RequiresPermissions("sysUser:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "用户模块",desc = "保存用户")
	public Object saveSysUser(@ModelAttribute SysUser sysUser,
			HttpServletRequest req, HttpServletResponse resp) {
		int insertFlag = 0;
		if (sysUser.getId() != null) {
			boolean exists = sysUserService.checkLoginNameExists(sysUser.getLoginName(), sysUser.getId());
			if(!exists){
				insertFlag = sysUserService.updateByPrimaryKeySelective(sysUser);
			}
		} else {
			boolean exists = sysUserService.checkLoginNameExists(sysUser.getLoginName());
			if(!exists){
				insertFlag = sysUserService.insert(sysUser);
			}
		}
		return insertFlag;
	}

	@RequestMapping(value = "show")
	@RequiresPermissions("sysUser:show")
	@ResponseBody
	public Object showSysUser(HttpServletRequest req, HttpServletResponse resp) {
		long id = ServletRequestUtils.getIntParameter(req, "id", 0);
		SysUser sysUser = sysUserService.selectByPrimaryKey(id);
		return sysUser;
	}

	@RequestMapping(value = "delete")
	@RequiresPermissions("sysUser:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "用户模块",desc = "删除用户")
	public Object deleteSysUser(@ModelAttribute("ids") String ids,HttpServletRequest req, HttpServletResponse resp) {
		long[] id = getParametersIds(req, "ids");
		return sysUserService.batchDelete(id);
	}

	@RequestMapping(value = "/showPerson")
	public ModelAndView showPerson(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/sys/sysUser/updatePerson");
		return modelAndView;
	}
	@RequestMapping(value = "saveUpdateSysuser")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "用户模块",desc = "个人信息修改")
	public  boolean  saveUpdateSysuser(@ModelAttribute SysUser sysUser,
											HttpServletRequest req, HttpServletResponse resp){
			sysUser.setId(getCurrentLoginUserInfo().getId());
			sysUser.setLoginName(getCurrentLoginUserInfo().getLoginName());
			sysUser.setRoleIds(getCurrentLoginUserInfo().getRoleIds());
			boolean exists = sysUserService.checkLoginNameExists(sysUser.getLoginName(), sysUser.getId());
			if(com.yourong.common.util.StringUtil.isNotBlank(sysUser.getNewPassword())){
				String password = sysUser.getNewPassword();
				String encryptToSHA = CryptHelper.encryptByase(password);
				sysUser.setPassword(encryptToSHA);
			}
			if(!exists){
				sysUserService.updateLoginIPandDate(sysUser);
				exists = true;
			}
		return exists ;
	}



















}