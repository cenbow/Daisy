package com.yourong.backend.mc.controller;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.yourong.backend.BaseController;
import com.yourong.backend.mc.service.WeixinTemplateService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.weixin.Weixin;
import com.yourong.common.weixin.WeixinMenu;
import com.yourong.common.weixin.WeixinUtil;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.mc.model.WinxinTemplate;

/**
 * 微信管理
 * 
 * @Author:chaisen
 * 
 * @date:2015年9月17日下午5:15:11
 */
@Controller
@RequestMapping("weixin")
public class WeixinTemplateController extends BaseController{
    @Autowired
    private WeixinTemplateService weixinMenuService;
	/**
	 * 列表展示
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("weixin:index")
	public String showWeixinManageIndex(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/mc/weixin/index";
	}
	@RequestMapping(value = "weixin")
	@RequiresPermissions("weixin:index")
	public String showWeixinIndex(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/sys/testWeixin";
	}
	@RequestMapping(value = "/menu/index")
	@RequiresPermissions("weixin:index")
	public String showWeixinMenuIndex(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/mc/weixin/menu/index";
	}
	/**
	 * 
	 * @Description:启用
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2015年12月10日 下午1:12:06
	 */
	@RequestMapping(value="ajaxOpen")
	@ResponseBody
	@RequiresPermissions("weixin:ajaxOpen")
	@LogInfoAnnotation(moduleName = "微信模块", desc = "开启模板")
	public Object ajaxOpen(@ModelAttribute("id")Long id){
		WinxinTemplate weixin =weixinMenuService.selectByPrimaryKey(id);
		weixin.setStatus("1");
		ResultDO<WinxinTemplate> result = new ResultDO<WinxinTemplate>();
		weixinMenuService.updateWeixin();
		int i=weixinMenuService.updateByPrimaryKeySelective(weixin); 
		if(i>0){
			result.setSuccess(true);
		}else{
			result.setSuccess(false);
			return result;
		}
		return result;
	}
	/**
	 * 
	 * @Description:关闭
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2015年12月10日 下午1:12:19
	 */
	@RequestMapping(value="ajaxClose")
	@ResponseBody
	@RequiresPermissions("weixin:ajaxClose")
	@LogInfoAnnotation(moduleName = "微信模块", desc = "关闭模板")
	public Object ajaxClose(@ModelAttribute("id")Long id){
		WinxinTemplate weixin =weixinMenuService.selectByPrimaryKey(id);
		weixin.setStatus("0");
		ResultDO<WinxinTemplate> result = new ResultDO<WinxinTemplate>();
		int i=weixinMenuService.updateByPrimaryKeySelective(weixin); 
		if(i>0){
			result.setSuccess(true);
		}else{
			result.setSuccess(false);
			return result;
		}
		return result;
	}
	/**
	 * 
	 * @Description:TODO
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2015年12月10日 下午1:12:40
	 */
    @RequestMapping(value = "ajax")
    @ResponseBody
    public Object showWeixinPage(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<WinxinTemplate> pageRequest = new Page<WinxinTemplate>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		map.put("type", "1");
        	Page<WinxinTemplate> pager = 	weixinMenuService.findByPage(pageRequest,map);		 
         return pager;
    }
    /**
     * 
     * @Description:TODO
     * @param req
     * @param resp
     * @return
     * @throws ServletRequestBindingException
     * @author: chaisen
     * @time:2015年12月10日 下午1:12:47
     */
    @RequestMapping(value = "ajaxAtten")
    @ResponseBody
    public Object showWeixinAttenPage(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<WinxinTemplate> pageRequest = new Page<WinxinTemplate>();
    	Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
    	map.put("type", "2");
        	Page<WinxinTemplate> pager = 	weixinMenuService.findByPage(pageRequest,map);		 
         return pager;
    }
    /**
     * 
     * @Description:保存微信模板
     * @param winxin
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2015年12月10日 下午1:11:40
     */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "微信模块", desc = "保存模板信息")
	public ResultDO<WinxinTemplate> saveWinxinModule(@ModelAttribute WinxinTemplate winxin, HttpServletRequest req,
			HttpServletResponse resp) {
			ResultDO<WinxinTemplate> result = new ResultDO<WinxinTemplate>();
		try {
			String flag = ServletRequestUtils.getStringParameter(req, "flag");
			if(flag.equals("1")){
				winxin.setType("1");
				if(winxin.getKeyword1()==null){
					result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
					result.setSuccess(false);
					return result;
				}
			}else{
				winxin.setType("2");
			}
			List<BscAttachment> lenderAttachments = parseJsonToObject(req, "lenderAttachmentsData", BscAttachment.class);
			List<BscAttachment> debtAttachments = Lists.newArrayList();
			if (Collections3.isNotEmpty(lenderAttachments)) {
				debtAttachments.addAll(lenderAttachments);
			}
			winxin.setBscAttachments(debtAttachments);
			String appPath = req.getSession().getServletContext().getRealPath("/");
			if(winxin.getId()!=null){
				result = weixinMenuService.updateWeixinInfo(winxin, appPath);
			}else{
				if(flag.equals("2")&&winxin.getStatus().equals("1")){
					weixinMenuService.updateWeixin();
				}
				result = weixinMenuService.insertWeixinInfo(winxin, appPath);
				
			}
			
		} catch (Exception e) {
			result.setSuccess(false);
		}
		return result;
	}
	/**
	 * 获取微信菜单
	 * @param req
	 * @param resp
	 * @return
	 * @throws 
	 */
	@RequestMapping(value = "ajaxTree")
	@RequiresPermissions("weixin:ajaxTree")
	@ResponseBody
	public Object showWeixinMenuTree(HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
			WinxinTemplate winxinMenu=new WinxinTemplate();
			winxinMenu=weixinMenuService.getMenu();
			return winxinMenu;
	}
	/**
	 * 增加模板页面跳转
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "add")
	@RequiresPermissions("weixin:show")
	public ModelAndView addWeixin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String type = ServletRequestUtils.getStringParameter(req, "type");
		String url="";
		if(type.equals("1")){
			url="/mc/weixin/addTemplateAtten";
		}else{
			url="/mc/weixin/addTemplateKey";
		}
		ModelAndView mv = new ModelAndView(url);
		mv.addObject("action", "show");
		return mv;
	}
	/**
	 * 
	 * @Description:TODO
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2015年12月10日 下午1:13:01
	 */
	@RequestMapping(value = "show")
	@RequiresPermissions("weixin:show")
	public ModelAndView detailWeixin(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mv = new ModelAndView("/mc/weixin/detail");
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		mv.addObject("id", id);
		mv.addObject("action", "show");
		return mv;
	}
	/**
	 * 
	 * @Description:TODO
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2015年12月10日 下午1:13:06
	 */
	@RequestMapping(value = "showAtten")
	@RequiresPermissions("weixin:show")
	public ModelAndView detailWeixinAtten(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mv = new ModelAndView("/mc/weixin/detailAtten");
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		mv.addObject("id", id);
		mv.addObject("action", "show");
		return mv;
	}
	/**
	 * 发布微信菜单
	 * @param winxinMenu
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "ajaxPublish")
    @ResponseBody
    @RequiresPermissions("weixin:ajaxPublish")
    @LogInfoAnnotation(moduleName = "微信管理模块",desc = "发布微信菜单")
    public Object saveWeixinMenu1(@ModelAttribute WinxinTemplate winxinMenu, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<WinxinTemplate> result = new ResultDO<WinxinTemplate>();
		String json=winxinMenu.getRemarks();
		String accessToken = WeixinUtil.getAccessToken(PropertiesUtil.getWeixinAppID(), PropertiesUtil.getWeixinAppsecret());
		 String res = WeixinUtil.createMenu1(json.toString(), accessToken);
		 if(res.equals("ok")){
			 result.setSuccess(true);
			 return result;
		 }else{
			 result.setSuccess(false);
			 return result;
		 }
				 
    }
	 /**
	  * 
	  * @Description:删除微信模板
	  * @param req
	  * @param resp
	  * @return
	  * @author: chaisen
	  * @time:2016年4月8日 下午4:15:37
	  */
	 @RequestMapping(value = "delete")
	 @ResponseBody
	 @LogInfoAnnotation(moduleName = "微信模块",desc = "删除微信模板")
	 public Object deleteWeixinTemplate(HttpServletRequest req, HttpServletResponse resp) {
		 ResultDO<WinxinTemplate> result = new ResultDO<WinxinTemplate>();
		 long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		int i= weixinMenuService.deleteByPrimaryKey(id);
		if(i<0){
			result.setSuccess(false);
		}
		return result;
	  }
		@RequestMapping(value = "find")
		@RequiresPermissions("weixin:find")
		@ResponseBody
		public Object findWeixin(HttpServletRequest req, HttpServletResponse resp) {
			ModelMap map = new ModelMap();
			long id = ServletRequestUtils.getLongParameter(req, "id", 0);
			WinxinTemplate winxinMenu = weixinMenuService.queryInfobyId(id);
			if (winxinMenu != null && Collections3.isNotEmpty(winxinMenu.getBscAttachments())) {
				map.put("lenderAttachments", winxinMenu.getBscAttachments());
			}
			map.put("winxinMenu", winxinMenu);
			return map;
		}
		  /**
		   * 
		   * @Description:获取微信菜单
		   * @param req
		   * @param resp
		   * @return
		   * @throws ServletRequestBindingException
		   * @author: chaisen
		   * @time:2015年12月1日 下午4:45:24
		   */
		    @RequestMapping(value = "getWeixin")
			@ResponseBody
			public Object getWeixinMenu(HttpServletRequest req,
					HttpServletResponse resp) throws ServletRequestBindingException {
			  List<Weixin> list = Lists.newArrayList();
			  list=weixinMenuService.getWeixinMenu();
			  return list;
			}
		   /**
		    * 
		    * @Description:save 
		    * @param weixin
		    * @param req
		    * @param resp
		    * @return
		    * @author: chaisen
		    * @time:2015年12月2日 下午3:11:38
		    */
		    @RequestMapping(value = "saveWeixin")
			@ResponseBody
			@LogInfoAnnotation(moduleName = "微信模块",desc = "保存微信菜单")
			public Object saveWeixin(@ModelAttribute Weixin weixin,
					HttpServletRequest req, HttpServletResponse resp) {
		    	 List<Weixin> list = Lists.newArrayList();
		    	 list=weixinMenuService.saveWeixinMenu(weixin);
				return list;
			}
		    /**
		     * 
		     * @Description:发布微信
		     * @param req
		     * @param resp
		     * @return
		     * @author: chaisen
		     * @time:2015年12月2日 下午3:28:40
		     */
		    @RequestMapping(value = "push")
			@ResponseBody
			@RequiresPermissions("weixin:ajaxPublish")
			@LogInfoAnnotation(moduleName = "微信模块",desc = "发布微信")
			public Object push(HttpServletRequest req, HttpServletResponse resp) {
		    	String jsonStr = req.getParameter("jsonStr");
		    	ResultDO<WeixinMenu> result = new ResultDO<WeixinMenu>();
		    	result=weixinMenuService.pushWeixinMenu(jsonStr);
		    	return result;
			}
		    /**
		     * 
		     * @Description:删除
		     * @param req
		     * @param resp
		     * @return
		     * @author: chaisen
		     * @time:2015年12月2日 下午3:35:42
		     */
		    @RequestMapping(value = "del")
			@ResponseBody
			@LogInfoAnnotation(moduleName = "微信模块",desc = "删除")
			public Object delMenu(HttpServletRequest req, HttpServletResponse resp) {
		    	 String id = req.getParameter("id");
		    	 List<Weixin> list = Lists.newArrayList();
		    	 list=weixinMenuService.delWeixinMenu(id);
				 return list;
			}
}
