/**
 * 
 */
package com.yourong.backend.sys.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.sys.service.WebAdminService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.core.sys.model.WebInfo;

/**
 * @author zhanghao
 *
 */
@Controller
@RequestMapping("webAdmin")
public class WebAdminController extends BaseController {
    @Autowired
    private WebAdminService webAdminService;
    
    /**
	 * 网站公告管理
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("webAdmin:index")
	public String showWebAdminIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/sys/webAdmin/index";
	}

	/**
	 * 保存网站公告
	 * @param customMessage
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "saveWebAdmin")
	@RequiresPermissions("webAdmin:saveWebAdmin")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "网站公告管理", desc = "保存公告")
	public Object saveWebNotice(@ModelAttribute WebInfo webInfo ,HttpServletRequest req, HttpServletResponse resp){
		return webAdminService.saveWebInfo(webInfo);
	}
	
}

