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
import com.yourong.backend.cms.service.CmsCommentService;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsComment;

@Controller
@RequestMapping("cmsComment")
public class CmsCommentController extends BaseController {
	@Autowired
	private CmsCommentService cmsCommentService;

	/**
	 * 列表数据
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("cmsComment:index")
	private String showCmsCommentIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/cms/cmsComment/index";
	}

	@RequestMapping(value = "ajax")
	@RequiresPermissions("cmsComment:ajax")
	@ResponseBody
	private Object showCmsCommentPages(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<CmsComment> pageRequest = new Page<CmsComment>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<CmsComment> pager = cmsCommentService.findByPage(pageRequest, map);
		return pager;

	}
}
