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
import com.yourong.backend.cms.service.BannerService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.CmsArticle;

@Controller
@RequestMapping("banner")
public class BannerController extends BaseController {
	@Autowired
	private BannerService bannerService;

	@RequestMapping(value = "index")
	@RequiresPermissions("banner:index")
	public String showBannerIndex(HttpServletRequest req, HttpServletResponse resp) {
		//获取活动列表
		req.setAttribute("activityList", bannerService.showNotFinishActivityList());
		return "/cms/banner/index";
	}
	
	@RequestMapping(value = "indexM")
	@RequiresPermissions("banner:indexM")
	public String showMBannerIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/cms/mBanner/index";
	}

	@RequestMapping(value = "ajax")
	@RequiresPermissions("banner:ajax")
	@ResponseBody
	public Object showBannerPages(HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		Map<String, Object> map = Maps.newHashMap();
		getFilterMapFromRequest(req, map);
		Page<Banner> pageRequest = new Page<Banner>();
		getPageInfoFromRequest(req, pageRequest);
		Page<Banner> pager = bannerService.findByPage(pageRequest, map);
		return pager;
	}

	@RequestMapping(value = "save")
	@RequiresPermissions("banner:save")
	@LogInfoAnnotation(moduleName = "banner模块",desc = "保存banner")
	public Object saveBanner(@ModelAttribute("banner") Banner banner, HttpServletRequest req, HttpServletResponse resp,@RequestParam("file") MultipartFile[] file) {
		String userName = SysServiceUtils.getCurrentLoginUser();
		String appPath = req.getSession().getServletContext()
				.getRealPath("/");
		MultipartFile imageFile = null;
		MultipartFile imageBgFile = null;
		if(file!=null && file.length>0){
			imageFile = file[0];  
			if(file.length > 1){
				imageBgFile = file[1];
			}
		}
		if(banner.getType()!=TypeEnum.BANNER_CHANNEL_TYPE_PC.getType()&&banner.getAreaSign()==null){
			banner.setAreaSign(banner.getType());//排序方法需要，非pcbanenr写死areasign
		}
		
		if (banner.getId() != null) {
			banner.setUpdateBy(userName);
			bannerService.updateByPrimaryKeySelective(banner,appPath,imageFile,imageBgFile);
		} else {
			banner.setCreateBy(userName);
			bannerService.insert(banner,appPath,imageFile,imageBgFile);
		}
		
		return "redirect:/banner/index";
	}

	@RequestMapping(value = "show")
	@RequiresPermissions("banner:show")
	@ResponseBody
	public Object showBanner(HttpServletRequest req, HttpServletResponse resp) {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		Banner banner = bannerService.selectByPrimaryKey(id);
		return banner;
	}

	@RequestMapping(value = "delete")
	@RequiresPermissions("banner:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "banner模块",desc = "删除banner")
	public Object deleteBanner(HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<CmsArticle> result = new ResultDO<CmsArticle>();
		long[] id = getParametersIds(req, "id");
		Integer delFlag = bannerService.batchDelete(id);
		if(delFlag>0){
			result.setSuccess(true);
		}else{
			result.setSuccess(false);
		}
		return result;
	}
	
	@RequestMapping(value="update")
	@RequiresPermissions("banner:update")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "banner模块",desc = "调整banner排序")
	public Object updateBanner(HttpServletRequest req, HttpServletResponse resp){
		String userName = SysServiceUtils.getCurrentLoginUser();
		Long bannerId = ServletRequestUtils.getLongParameter(req, "bannerId", 0);
		Integer position = ServletRequestUtils.getIntParameter(req, "position", 0);
		return bannerService.updateWeight(bannerId, position, userName);
	}
	
	/**
	 * 定时过期banner
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "expire")
	@ResponseBody
	public Object expireBannerTask(HttpServletRequest req,
			HttpServletResponse resp) {
		Integer result = bannerService.expireBannerTask();
		return result;
	}
}