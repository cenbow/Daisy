/**
 * 
 */
package com.yourong.backend.sys.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yourong.backend.BaseController;
import com.yourong.backend.sys.service.AppAdvertService;
import com.yourong.backend.sys.service.SysDictService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.sys.model.SysDict;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年1月4日上午11:14:43
 */
@Controller
@RequestMapping("appAdvert")
public class AppAdvertController extends BaseController {

	@Autowired
	private SysDictService sysDictService;
	
	@Autowired
	private AppAdvertService appAdvertService;
	
	
	@RequestMapping(value = "index")
	@RequiresPermissions("appAdvert:index")
	public String showMBannerIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/cms/mBanner/index";
	}
	
	
	/**
	 *获取app广告参数
	 * 
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "getParam")
	@RequiresPermissions("appAdvert:getParam")
	@ResponseBody
	public Object getParam(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		
		Page<SysDict> pageRequest = new Page<SysDict>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		map.put("groupName", "launchAdvImgUrl");
		map.put("asc", "id");
		Page<SysDict> SysDict = appAdvertService.findByPage(pageRequest, map);
        return SysDict;
	}
	
	/**
	 *获取app广告参数
	 * 
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "getAppFindParam")
	@RequiresPermissions("appAdvert:getAppFindParam")
	@ResponseBody
	public Object getAppFindParam(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		
		Page<SysDict> pageRequest = new Page<SysDict>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		map.put("groupName", "app_find");
		map.put("asc", "id");
		Page<SysDict> SysDict = appAdvertService.findByPage(pageRequest, map);
        return SysDict;
	}
	
	/**
	 *保存app广告参数
	 * 
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "save")
	@RequiresPermissions("appAdvert:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "app广告管理",desc = "保存广告")
	public Object saveAdvert(@ModelAttribute("type") String type,@ModelAttribute("id") long id, HttpServletRequest req, HttpServletResponse resp,@RequestParam("file") MultipartFile file) {
		String appPath = req.getSession().getServletContext()
				.getRealPath("/");
		
		int result = appAdvertService.insert(id, appPath, file);
		return result;
	}
	
	/**
	 *保存app广告参数
	 * 
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "saveTitle")
	@RequiresPermissions("appAdvert:saveTitle")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "app广告管理",desc = "保存广告")
	public Object saveAdvertTitle(@ModelAttribute("title_type") String type,@ModelAttribute("title_id") long id, @ModelAttribute("value") String value, 
			HttpServletRequest req, HttpServletResponse resp) {
		
		int result = appAdvertService.insertTitle(id, value);
		
		return  result;
	}
	
	/**
	 *删除app广告参数
	 * 
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "clear")
	@RequiresPermissions("appAdvert:clear")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "app广告管理",desc = "删除广告")
	public Object clearAdvert(@ModelAttribute("id") long id, HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<SysDict> result = new ResultDO<SysDict>();
		Integer delFlag = appAdvertService.clear(id);
		if(delFlag>0){
			result.setSuccess(true);
		}else{
			result.setSuccess(false);
		}
		return result;
	}
	
	
}
