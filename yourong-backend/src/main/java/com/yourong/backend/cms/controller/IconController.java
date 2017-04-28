/**
 * 
 */
package com.yourong.backend.cms.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.cms.service.CmsIconService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.CmsIcon;

/**
 * @author wanglei
 * app首页图标icon 后台维护处理
 */
@Controller
@RequestMapping("icon")
public class IconController extends BaseController  {

	@Autowired
	private CmsIconService cmsIconService;
	
	@RequestMapping(value = "index")
	@RequiresPermissions("icon:index")
	public String showIconIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/cms/icon/index";
	}
	
	
	@RequestMapping(value = "ajax")
	@RequiresPermissions("icon:ajax")
	@ResponseBody
	public Object showIconPages(HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		Map<String, Object> map = Maps.newHashMap();
		getFilterMapFromRequest(req, map);
		Page<CmsIcon> pageRequest = new Page<CmsIcon>();
		getPageInfoFromRequest(req, pageRequest);
		Page<CmsIcon> pager = cmsIconService.findByPage(pageRequest, map);
		return pager;
	}
	
	
	@RequestMapping(value = "save")
	@RequiresPermissions("icon:save")
	@LogInfoAnnotation(moduleName = "CmsIcon模块",desc = "保存CmsIcon")
	public Object saveIcon(@ModelAttribute("cmsIcon") CmsIcon cmsIcon, HttpServletRequest req, HttpServletResponse resp,@RequestParam("file") MultipartFile[] file) {
		String userName = SysServiceUtils.getCurrentLoginUser();
		String appPath = req.getSession().getServletContext()
				.getRealPath("/");
		MultipartFile imageFile = null;
		if(file!=null && file.length>0){
			imageFile = file[0];  
		}

		cmsIcon.setUpdateBy(userName);
		if (cmsIcon.getId() != null) {
			cmsIconService.updateByPrimaryKeySelective(cmsIcon,appPath,imageFile);
		} else {
			cmsIconService.insert(cmsIcon,appPath,imageFile);
		}
		return "redirect:/icon/index";
	}
	
	
	
	@RequestMapping(value = "show")
	@RequiresPermissions("icon:show")
	@ResponseBody
	public Object showBanner(HttpServletRequest req, HttpServletResponse resp) {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		CmsIcon cmsIcon = cmsIconService.selectByPrimaryKey(id);
		return cmsIcon;
	}
	
	@RequestMapping(value = "delete")
	@RequiresPermissions("icon:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "CmsIcon模块",desc = "删除CmsIcon")
	public Object deleteBanner(HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<CmsArticle> result = new ResultDO<CmsArticle>();
		long[] id = getParametersIds(req, "ids");
		Integer delFlag = cmsIconService.batchDelete(id);
		if(delFlag>0){
			result.setSuccess(true);
		}else{
			result.setSuccess(false);
		}
		return result;
	}
	
	@RequestMapping(value="update")
	@RequiresPermissions("icon:update")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "CmsIcon模块",desc = "调整CmsIcon排序")
	public Object updateBanner(HttpServletRequest req, HttpServletResponse resp){
		String userName = SysServiceUtils.getCurrentLoginUser();
		Long iconId = ServletRequestUtils.getLongParameter(req, "iconId", 0);
		Integer position = ServletRequestUtils.getIntParameter(req, "position", 0);
		return cmsIconService.updateWeight(iconId, position, userName);
	}
	
}
