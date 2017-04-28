package com.yourong.backend.mc.controller;

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
import com.yourong.backend.cms.service.BannerService;
import com.yourong.backend.mc.service.ActivityService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.common.weixin.Weixin;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.sys.model.SysMenu;

@Controller
@RequestMapping("activityData")
public class ActivityDataController extends BaseController {

	@Autowired
	ActivityService activityService;
	@Autowired
	private BannerService bannerService;

	@RequestMapping(value = "index")
	@RequiresPermissions("activityData:index")
	public String showActivityDataIndex(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("activityList", bannerService.showNotFinishActivityList());
		return "/mc/activityData/index";
	}

	/**
	 * 
	 * @Description:获取列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年7月8日 下午6:44:52
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("activityData:ajax")
	@ResponseBody
	public Object showActivityDataPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<ActivityData> pageRequest = new Page<ActivityData>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<ActivityData> pager = activityService.showActivityDataPages(pageRequest, map);
		return pager;
	}
	/**
	 * 
	 * @Description:保存活动数据
	 * @param activityData
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年7月9日 上午10:39:44
	 */
	 @RequestMapping(value = "save")
	 @RequiresPermissions("activityData:save")
	 @ResponseBody
	 @LogInfoAnnotation(moduleName = "营销模块",desc = "保存活动数据")
	 public Object saveActivityData(@ModelAttribute ActivityData activityData, HttpServletRequest req, HttpServletResponse resp) {
		 ResultDO<ActivityData> result = new ResultDO<ActivityData>();
	     if(activityData.getId()!=null){ 
	    	 result = activityService.updateByPrimaryKeySelective(activityData); 
	     }else{	 
	    	 result = activityService.insertSelective(activityData);
	      }	 
	    return result;		 
	    }
	 /**
	  * 
	  * @Description:修改
	  * @param req
	  * @param resp
	  * @return
	  * @author: chaisen
	  * @time:2016年7月9日 上午11:38:52
	  */
	  @RequestMapping(value = "show")
	    @RequiresPermissions("activityData:show")
	    @ResponseBody
	    public Object showActivityData(HttpServletRequest req, HttpServletResponse resp) {
	        long id =  ServletRequestUtils.getLongParameter(req, "id", 0); 
	        ActivityData activityData = activityService.selectByPrimaryKey(id);	 
	        return activityData;		 
	    }
}
