package com.yourong.backend.ic.controller;

import java.util.HashMap;
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

import com.google.common.collect.Lists;
import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.CollectService;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.model.CollectionProcess;
import com.yourong.core.ic.model.ProjectInterestBiz;
	
/**
 * 
 * @desc 普通逾期管理
 * @author chaisen
 * 2016年5月25日上午10:57:11
 */
@Controller
@RequestMapping("normalOverdue")
public class NormalOverdueController extends BaseController{
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private CollectService collectService;
	/**
	 * 
	 * @Description:逾期还款管理页面
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年1月20日 上午10:41:24
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("normalOverdue:index")
	public String overdueIndex(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/p2p/normalOverdue/index";
	}
	
	/**
	 * 
	 * @Description:分页查询逾期还款列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年1月20日 下午1:53:23
	 */
	@RequestMapping(value = "ajaxOverdue")
	@RequiresPermissions("normalOverdue:ajaxOverdue")
	@ResponseBody
	public Object findOverdueList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Page<ProjectInterestBiz> pageRequest = new Page<ProjectInterestBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<ProjectInterestBiz> pager = projectService.findNormalOverdueList(pageRequest, map);
		return pager;
	}
	/**
	 * 
	 * @Description:添加催收历程
	 * @param collect
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年5月23日 下午4:09:43
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "save")
	@RequiresPermissions("normalOverdue:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "逾期管理模块", desc = "添加催收历程")
	public ResultDO<CollectionProcess> saveCollect(@ModelAttribute("collect") CollectionProcess collect, HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<CollectionProcess> result = new ResultDO<CollectionProcess>();
		try {
			// 催收历程图片
			List<BscAttachment> collectAttachments = parseJsonToObject(req, "collectAttachmentsData", BscAttachment.class);
			List<BscAttachment> debtAttachments = Lists.newArrayList();
			if (Collections3.isNotEmpty(collectAttachments)) {
				debtAttachments.addAll(collectAttachments);
			}
			collect.setBscAttachments(debtAttachments);
			String appPath = req.getSession().getServletContext().getRealPath("/");
			if(collect!=null&&collect.getId()!=null){
				result = collectService.updateCollect(collect,appPath);
			}else{
				result = collectService.insertCollect(collect, appPath);
			}
		} catch (Exception e) {
			result.setSuccess(false);
		}
		return result;
	}
	/**
	 * 
	 * @Description:更新催收历程
	 * @param collect
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年5月24日 上午11:54:36
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "update")
	@RequiresPermissions("normalOverdue:update")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "逾期管理模块", desc = "更新催收历程")
	public ResultDO<CollectionProcess> updateCollect(@ModelAttribute("collect") CollectionProcess collect, HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<CollectionProcess> result = new ResultDO<CollectionProcess>();
		try {
			result = collectService.updateCollect(collect,"");
		} catch (Exception e) {
			result.setSuccess(false);
		}
		return result;
	}
	/**
	 * 
	 * @Description:查询上一次的催收时间和形式
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年5月23日 下午5:09:18
	 */
	@RequestMapping(value = "showCollectInfo")
	@ResponseBody
	public Object showCollectInfo(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long overdueRepayId = ServletRequestUtils.getLongParameter(req, "overdueRepayId");
		CollectionProcess biz=new CollectionProcess();
		CollectionProcess collect = collectService.getCollectByRepayId(overdueRepayId);
		if(collect!=null){
			biz.setCollectTime(collect.getNextCollectTime());
			biz.setCollectForm(collect.getNextCollectForm());
		}
		return biz;
	}
	/**
	 * 
	 * @Description:查询催收历程
	 * @param req
	 * @param resp
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月24日 上午9:35:40
	 */
	@RequestMapping(value = "find")
	@RequiresPermissions("normalOverdue:find")
	@ResponseBody
	public Object findCollect(HttpServletRequest req, HttpServletResponse resp) throws ManagerException {
		ModelMap map = new ModelMap();
		long overdueRepayId = ServletRequestUtils.getLongParameter(req, "overdueRepayId", 0);
		List<CollectionProcess> collectList=collectService.getCollectListByRepayId(overdueRepayId);
		if(Collections3.isNotEmpty(collectList)){
			map.put("collectList", collectList);
		}
		return map;
	}
	/**
	 * 
	 * @Description 催收记录
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年5月27日 下午1:53:52
	 */
	@RequestMapping(value = "showCollectRecord")
	@RequiresPermissions("normalOverdue:showCollectRecord")
	@ResponseBody
	public Object showCollectRecord(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<CollectionProcess> pageRequest = new Page<CollectionProcess>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("overdueRepayId", ServletRequestUtils.getLongParameter(req, "overdueRepayId"));
		Page<CollectionProcess> pager = collectService.showCollectRecord(pageRequest, map);
		return pager;
	}
	/**
	 * 
	 * @Description:TODO
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年5月27日 下午1:54:12
	 */
	@RequestMapping(value = "findCollectProcess")
	@ResponseBody
	public Object findCollectProcess(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		CollectionProcess collect = collectService.findCollectProcess(id);
		collect.setCollectStatus(0);
		return collect;
	}
}
