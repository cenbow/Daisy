package com.yourong.backend.cms.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yourong.backend.BaseController;
import com.yourong.backend.cms.service.CmsArticleService;
import com.yourong.backend.cms.service.CmsCategoryService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.StringUtil;
import com.yourong.common.web.ResultObject;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.CmsCategory;

/**
 * 栏目
 * @author fuyili
 * 2015年1月23日上午11:45:53
 */
@Controller
@RequestMapping("cmsCategory")
public class CmsCategoryController extends BaseController {
	private static Logger logger = LoggerFactory
			.getLogger(CmsArticleController.class);
	@Autowired
	private CmsCategoryService cmsCategoryService;

	@Autowired
	private CmsArticleService cmsArticleService;

	/**
	 * 列表页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("cmsCategory:index")
	public String showCmsCategoryIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/cms/cmsCategory/index";
	}

	/**
	 * 列表数据
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "ajax")
	@RequiresPermissions("cmsCategory:ajax")
	@ResponseBody
	public Object showCmsCategoryPages(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<CmsCategory> pageRequest = new Page<CmsCategory>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<CmsCategory> pager = cmsCategoryService.findByPage(pageRequest,
				map);
		return pager;
	}

	/**
	 * 栏目树
	 * 
	 * @return
	 */
	@RequestMapping(value = "tree")
	@RequiresPermissions("cmsCategory:tree")
	@ResponseBody
	public Object showCmsCategoryTree() {
		List<CmsCategory> cmsCategories = cmsCategoryService
				.selectAllCmsCategory();
		for (CmsCategory cmsCategory : cmsCategories) {
			if (cmsCategory.getParentId().equals("0")) {
				cmsCategory.setOpen(true);
			}
		}
		return cmsCategories;
	}

	/**
	 * 新增保存
	 */
	@RequestMapping(value = "save")
	@RequiresPermissions("cmsCategory:save")
	@LogInfoAnnotation(moduleName = "栏目模块",desc = "保存栏目")
	public Object saveCmsCategory(@RequestParam("file") MultipartFile file,
			HttpServletRequest req, HttpServletResponse resp,@ModelAttribute("cmsCategoryForm") CmsCategory cmsCategory) {
		// 文件上传
		if (!file.isEmpty()) { // 获取文件名称
			String fileName = file.getOriginalFilename(); // 获取文件需要保存的路径
			String path = req.getSession().getServletContext().getRealPath("/")
					+ "static/upload/" + fileName;
			logger.debug("文件路径：" + path);
			File localFile = new File(path);
			try { // 保存文件
				file.transferTo(localFile);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			cmsCategory.setImage("$root_url/static/upload/" + fileName);
		}

		int insertSelective;
		if (cmsCategory.getId() != null) {
			insertSelective = cmsCategoryService
					.updateByPrimaryKeySelective(cmsCategory);
		} else {
			if (StringUtil.isBlank(cmsCategory.getParentId())) {
				cmsCategory.setParentId("0");
			}
			insertSelective = cmsCategoryService.insert(cmsCategory);
		}
		return "/cms/cmsCategory/index";
	}

	@RequestMapping(value = "show")
	@RequiresPermissions("cmsCategory:show")
	@ResponseBody
	public Object showCmsCategory(HttpServletRequest req,
			HttpServletResponse resp) {
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		CmsCategory cmsCategory = cmsCategoryService.selectByPrimaryKey(id);
		return cmsCategory;

	}

	@RequestMapping(value = "delete")
	@RequiresPermissions("cmsCategory:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "栏目模块",desc = "删除栏目")
	public Object deleteCmsCategory(HttpServletRequest req,
			HttpServletResponse resp,@RequestParam("ids")long ids) {
		ResultObject<CmsCategory> resultObject = new ResultObject<CmsCategory>();
		List<String> messages = new ArrayList<String>();
		//判断是否有文章，如果有文章则不允许删除
		List<CmsArticle> cmsArticles = cmsArticleService.selectByCategoryId(ids);
		if(cmsArticles!=null&&cmsArticles.size()>0){
			messages.add("该栏目下有文章，不能删除！");
			resultObject.addListMessage(messages);
			return resultObject;
		}
		//判断是否有下级目录，如果有则不允许删除
		
		// 判断是否为根目录，如果为根目录不允许删除
		CmsCategory category = cmsCategoryService.selectByPrimaryKey(ids);
		if (category != null) {
			if (category.getParentId().equals("0")) {
				messages.add("根目录，不能删除！");
				resultObject.addListMessage(messages);
				return resultObject;
			}
		}
		cmsCategoryService.deleteByPrimaryKey(ids);
		resultObject.setStatus(1);// 删除成功
		resultObject.addListMessage(messages);
		return resultObject;
	}
	
}
