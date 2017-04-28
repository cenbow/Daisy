package com.yourong.backend.mc.controller;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yourong.backend.BaseController;
import com.yourong.backend.mc.service.WeixinTemplateService;
import com.yourong.backend.sys.service.SysDictService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.weixin.Weixin;

/**
 * 
 * @desc 微信菜单操作
 * @author chaisen
 * 2016年4月11日下午4:56:16
 */
@Controller
@RequestMapping("weixinMenu")
public class WeixinMenuController extends BaseController{
	
	@Autowired
	private WeixinTemplateService weixinMenuService;
	
	@Autowired
	private SysDictService sysDictService;
	
	
	@RequestMapping(value = "index")
	@RequiresPermissions("weixinMenu:index")
	public String weixinMenuIndex(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/mc/weixinMenu/index";
	}
	@RequestMapping(value = "ajaxTree")
	@RequiresPermissions("weixin:ajaxTree")
	@ResponseBody
	public Object getWeixinMenu(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		 List<Weixin> list = Lists.newArrayList();
		 list=weixinMenuService.getWeixinMenuList();
		 return list;
	}
	@RequestMapping(value = "show")
	@RequiresPermissions("weixin:show")
	@ResponseBody
	public Object showWeixin(HttpServletRequest req, HttpServletResponse resp) {
		long id = ServletRequestUtils.getIntParameter(req, "id", 0);
		Weixin weixin = weixinMenuService.selectByweixinId(id);
		return weixin;
	}
	@RequestMapping(value = "save")
	@RequiresPermissions("weixin:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "营销模块",desc = "保存微信菜单")
	public ResultDO<Weixin> saveWeixinMenu(
			@ModelAttribute("weixin") Weixin weixin,
			HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<Weixin> result = new ResultDO<Weixin>();
		if(weixin.getName().isEmpty()){
			result.setResultCode(ResultCode.WEIXIN_KEY_NAME_NULL);
			return result;
		}
		if(weixin.getType().equals("click")){
			if(weixin.getKey().isEmpty()){
				result.setResultCode(ResultCode.WEIXIN_KEY_NOT_NULL);
				return result;
			}
		}
		if(weixin.getType().equals("view")){
			if(weixin.getUrl().isEmpty()){
				if(!weixin.getKey().equals("parentNode")){
					result.setResultCode(ResultCode.WEIXIN_KEY_URL_NULL);
					return result;
				}
				
			}
		}
		if (weixin.getId().isEmpty()) {
			result = weixinMenuService.insertWeixin(weixin);
		} else {
			result = weixinMenuService.updateWeixin(weixin);
		}
		return result;
	}
	
	@RequestMapping(value = "ajaxPublish")
    @ResponseBody
    @RequiresPermissions("weixin:ajaxPublish")
    @LogInfoAnnotation(moduleName = "微信管理模块",desc = "发布微信菜单")
    public Object publishWeixin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
				return  weixinMenuService.pushWeixin();
    }
	@RequestMapping(value = "delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "微信模块",desc = "删除")
	public Object delMenu(HttpServletRequest req, HttpServletResponse resp) {
	    	String id = req.getParameter("id");
	    	return sysDictService.deleteByPrimaryKey(Long.parseLong(id));
	}
}
