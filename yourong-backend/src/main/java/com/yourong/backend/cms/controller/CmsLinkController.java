package com.yourong.backend.cms.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.cms.service.CmsLinkService;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsLink;

@Controller
@RequestMapping("cmsLink")
public class CmsLinkController extends BaseController {
	@Autowired
	private CmsLinkService cmsLinkService;

	@RequestMapping(value = "index")
	@RequiresPermissions("cmsLink:index")
	private String showCmsLinkIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/cms/cmsLink/index";
	}

	@RequestMapping(value = "ajax")
	@RequiresPermissions("cmsLink:ajax")
	@ResponseBody
	private Object showCmsLinkPages(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<CmsLink> pageRequest = new Page<CmsLink>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<CmsLink> pager = cmsLinkService.findByPage(pageRequest, map);
		return pager;
	}

}
