package com.yourong.backend.cms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.yourong.common.util.Collections3;
import com.yourong.core.bsc.model.BscAttachment;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.servlet.ModelAndView;

import com.yourong.backend.BaseController;
import com.yourong.backend.cms.service.CmsArticleService;
import com.yourong.backend.cms.service.CmsCategoryService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsArticle;

@Controller
@RequestMapping("cmsArticle")
public class CmsArticleController extends BaseController {
	@Autowired
	private CmsArticleService cmsArticleService;
	@Autowired
	private CmsCategoryService cmsCategoryService;

	@RequestMapping(value = "index")
	@RequiresPermissions("cmsArticle:index")
	public ModelAndView showCmsArticleIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		
		List<CmsArticle> cmsList = cmsArticleService.selectAppPushArticle();
		ModelAndView model = new ModelAndView();
		model.addObject("cmsList", cmsList);
		model.setViewName("/cms/cmsArticle/index");

		return model;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("cmsArticle:ajax")
	@ResponseBody
	public Object showCmsArticlePages(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<CmsArticle> pageRequest = new Page<CmsArticle>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<CmsArticle> pager = cmsArticleService.findByPage(pageRequest, map);
		return pager;
	}

	@RequestMapping(value = "save")
	@RequiresPermissions("cmsArticle:save")
	@LogInfoAnnotation(moduleName = "文章模块",desc = "保存文章")
	public String saveCmsArticle(HttpServletRequest req,
			HttpServletResponse resp, @ModelAttribute("cmsArticleForm") CmsArticle cmsArticle,@RequestParam(value = "articleContent",required = false)byte[] articleContent,
				 @RequestParam(value = "commonAttachmentsData")String commonAttachmentsData,
				 @RequestParam(value = "chosenAttachmentsData")String chosenAttachmentsData)
			throws Exception {
		String userName = SysServiceUtils.getCurrentLoginUser();
		String appPath = req.getSession().getServletContext()
				.getRealPath("/");

		List<BscAttachment> achments= new ArrayList<>();
		if (!StringUtils.isEmpty(commonAttachmentsData)){
			List<BscAttachment> common= JSON.parseArray(commonAttachmentsData,BscAttachment.class);
			if (Collections3.isNotEmpty(common)){
				for (BscAttachment bsc:common) {
					achments.add(bsc);
				}
			}
		}
		//新闻报道有精选图片
		if (cmsArticle.getCategoryId()==null||cmsArticle.getCategoryId()==2){
			if (!StringUtils.isEmpty(chosenAttachmentsData)){
				List<BscAttachment> chosen= JSON.parseArray(chosenAttachmentsData,BscAttachment.class);
				if (Collections3.isNotEmpty(chosen)){
					for (BscAttachment bsc:chosen) {
						achments.add(bsc);
					}
				}
			}
		}
		cmsArticle.setContent(articleContent);
		if (cmsArticle.getCategoryId()==null||cmsArticle.getCategoryId()!=1){
			cmsArticle.setGenre(null);
		}
		if (cmsArticle.getId() != null) {
			cmsArticle.setUpdateBy(userName);
			cmsArticleService.updateByPrimaryKeySelective(cmsArticle,appPath,achments);
		} else {
			cmsArticle.setCreateBy(userName);
			cmsArticleService.insert(cmsArticle,appPath,achments);
		}
		
		return "redirect:/cmsArticle/index";
	}

	@RequestMapping(value = "show")
	@RequiresPermissions("cmsArticle:show")
	@ResponseBody
	public Object showCmsArticle(HttpServletRequest req,
			HttpServletResponse resp) {
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		CmsArticle cmsArticle = cmsArticleService.selectByPrimaryKey(id);
		return cmsArticle;
	}

	@RequestMapping(value = "delete")
	@RequiresPermissions("cmsArticle:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "文章模块",desc = "删除文章")
	public Object deleteCmsArticle(HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<CmsArticle> result = new ResultDO<CmsArticle>();
		long[] id = getParametersIds(req, "ids");
		Integer delFlag = cmsArticleService.batchDelete(id);
		if(delFlag>0){
			result.setSuccess(true);
		}else{
			result.setSuccess(false);
		}
		return result;
	}

}
