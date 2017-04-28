package com.yourong.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.common.constant.Constant;
import com.yourong.core.sys.model.SysDict;
import com.yourong.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageLinkModel;
import com.yourong.core.ic.model.ProjectPackageModel;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.biz.ProjectForRewardDetail;
import com.yourong.core.ic.model.biz.QuickProjectBiz;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.web.dto.ProjectInfoDto;

@Controller
@RequestMapping("products")
public class ProjectController extends BaseController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private SysAreaService sysAreaService;
	
	@Autowired
	private BannerService bannerService;
	
	@Autowired
	private ActivityAfterTransactionManager activityAfterTransactionManager;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private ActivityLotteryService activityLotteryService;

	@Autowired
	private SysDictService sysDictService;

	/**
	 * 项目列表
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "list-{investTypeCode}-{projectType}-{statusCode}-{projectCategory}-{orderSource}-{currentPage}.html")
	public ModelAndView findProjectList(
			@ModelAttribute("projectQuery") ProjectQuery query,
			HttpServletRequest req, HttpServletResponse resp) {
		//快投专区推荐项目
		if(query.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_NORMAL.getType()){
			QuickProjectBiz recommendQuickProject = projectService.getRecommendQuickProject();
			req.setAttribute("recommendQuick", recommendQuickProject);
		}
		req.setAttribute("projectCategory", query.getProjectCategory());
		//信用融专区
	//	ProjectPackageBiz projectPackageBiz = projectService.findProjectPackageBiz(1L);
	//	req.setAttribute("projectPackageBiz", projectPackageBiz);
		//理财列表
		searchPre(query);
		Map<String,Object> resMap = projectService.findProjectListByPage(query);
		if(resMap.get("page")!=null){
			@SuppressWarnings("unchecked")
			Page<ProjectForFront> projectList =(Page<ProjectForFront>) resMap.get("page");
			req.setAttribute("projectList", projectList);
		}
		if(resMap.get("packageList")!=null){
			@SuppressWarnings("unchecked")
			List<ProjectPackage> packageList =(List<ProjectPackage>) resMap.get("packageList");
			req.setAttribute("packageList", packageList);
		}
		if (StringUtil.isBlank(query.getProjectType())) {
			query.setProjectType("all");
		}

		ModelAndView model = new ModelAndView();

		model.setViewName("/products/list");

		req.setAttribute("query", query);
		return model;
	}
	@RequestMapping(value ="showProjectPackage-{projectPackageId}.html")
	public ModelAndView showProjectPackageDetail(@PathVariable Long projectPackageId, HttpServletRequest req, HttpServletResponse resp){
		ModelAndView model = new ModelAndView();
		List<ProjectPackageLinkModel> pList= projectService.getProjectPackageLinkModelList(projectPackageId);
		req.setAttribute("pList", pList);
		ProjectPackage projectPackage = projectService.findProjectPackage(projectPackageId);
		String minRewardLimit= projectService.getMinRewardLimit();
		req.setAttribute("minRewardLimit", minRewardLimit);
		req.setAttribute("projectPackage", projectPackage);
		model.setViewName("/products/creditList");
		return model;
	};
	@RequestMapping(value ="showProjectPackageCompleted.html")
	public ModelAndView showProjectPackageCompleted(HttpServletRequest req, HttpServletResponse resp){
		ModelAndView model = new ModelAndView();
		List<ProjectPackage> list = projectService.getAllCompletedProjectPackageList();
		req.setAttribute("list", list);
		model.setViewName("/products/completedCase");
		return model;
	};
	/**
	 * 项目详情
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "detail-{projectId}.html")
	public ModelAndView getProjectInfo(@PathVariable String projectId,
			HttpServletRequest req, HttpServletResponse resp) {
		Long id = 0L;
		ModelAndView model = new ModelAndView();
		if (StringUtil.isNotBlank(projectId) && StringUtil.isNumeric(projectId)) {
			id = Long.parseLong(projectId);
		}
		if (id <= 0) {
			model.setViewName("/404");
			return model;
		}
		Long memberId =0L;
		if(	getMember()!=null)
		{
			memberId = getMember().getId();
		}
		ProjectInfoDto projectInfoDto = projectService.getAllProjectInfo(id,memberId);
		if (projectInfoDto == null ) {
			model.setViewName("/404");
			return model;
		}
		String minRewardLimit= projectService.getMinRewardLimit();
		req.setAttribute("minRewardLimit", minRewardLimit);
		req.setAttribute("projectInfo", projectInfoDto);
		
		model.setViewName("/products/detail");
		return model;
	}

	/**
	 * 获取新客项目
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/prop/getNewCustomerProject", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<ProjectForFront> getNewCustomerProject(
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<ProjectForFront> res = projectService.getNewCustomerProject();
		return res;
	}

	/**
	 * 获取着陆页推荐项目
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/prop/getLandingRecommendProjectProject", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<ProjectForFront> getNewProject(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ResultDO<ProjectForFront> res = projectService.getRecommendProject();
		return res;
	}

	/**
	 * 按债权类型查询推荐项目
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/prop/getHouseRecommendProject", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<ProjectForFront> getHouseRecommendProject(
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String[] guaranties = new String[] { "house", "houseRecord" };
		Integer queryNum = ServletRequestUtils.getIntParameter(req, "queryNum",
				1);
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("guaranties", guaranties);
		paraMap.put("queryNum", queryNum);
		paraMap.put("redisKey", "houseRecommend");
		ResultDO<ProjectForFront> res = projectService
				.getRecommendProjectByGuaranty(paraMap);
		return res;
	}

	/**
	 * 搜索的前置条件
	 * 
	 * @param query
	 */
	private void searchPre(ProjectQuery query) {
		String statusCode = query.getStatusCode();
		String guarantyType = query.getProjectType();
		// 加这些判断主要是防止“错误数据”，以及前台Tab项的切换
		if (statusCode != null) {
			if (!statusCode.equals("investing")
					&& !statusCode.equals("performance")
					&& !statusCode.equals("repayment")&& !statusCode.equals("lose")) {
				query.setStatus(null);
				query.setStatusCode("all");
			}
		}
		if(guarantyType != null){
			if(!guarantyType.equals("car") && !guarantyType.equals("house") && !guarantyType.equals("newCar") 
					&& !guarantyType.equals("equity") && !guarantyType.equals("carPayIn") 
					&& !guarantyType.equals("carBusiness") && !guarantyType.equals("buyCar")&& !guarantyType.equals("runCompany")
					&& !guarantyType.equals("credit") && !guarantyType.equals("guarantee")
					&& !guarantyType.equals("carLeas")){
				query.setProjectType(null);
			}
		}
		if(query.getProjectCategory()!=1&&query.getProjectCategory()!=2){
			query.setProjectCategory(1);
		}
		
		String orderSource = query.getOrderSource();
		//加这些判断主要是防止“错误数据”，以及前台Tab项的切换
		if(orderSource == null||
				(!orderSource.equals("createTimeAsc")&&!orderSource.equals("createTimeDesc")
						&&!orderSource.equals("daysAsc")&&!orderSource.equals("daysDesc")
						&&!orderSource.equals("rateAsc")&&!orderSource.equals("rateDesc"))){    
			//空处理
			query.setOrderSource("createTimeDesc");
		}
		
		query.setPageSize(6);
	}

	/************************ YourongMobile ***************************/
	/**
	 * 项目列表
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "m/list-{projectType}-{statusCode}-{currentPage}.html")
	public String findProjectListMobile(
			@ModelAttribute("projectQuery") ProjectQuery query,
			HttpServletRequest req, HttpServletResponse resp) {
		searchPre(query);
		Map<String,Object> resMap = projectService.findProjectListByPage(query);
		if(resMap.get("page")!=null){
			@SuppressWarnings("unchecked")
			Page<ProjectForFront> projectList =(Page<ProjectForFront>) resMap.get("page");
			req.setAttribute("projectList", projectList);
		}
		if(resMap.get("packageList")!=null){
			@SuppressWarnings("unchecked")
			List<ProjectPackage> packageList =(List<ProjectPackage>) resMap.get("packageList");
			req.setAttribute("packageList", packageList);
		}
		if (StringUtil.isBlank(query.getProjectType())) {
			query.setProjectType("all");
		}
		req.setAttribute("query", query);
		return "/mobile/products/list";
	}

	/**
     * 直投项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/p2pContract/preview")
    public ModelAndView ztPreview(HttpServletRequest req, HttpServletResponse resp) {
    	Long projectId = ServletRequestUtils.getLongParameter(req, "projectId",0L);
    	
    	boolean flag = projectService.isBorrowerTypeEnterprise(projectId);
    	ModelAndView model = new ModelAndView();
        model.addObject("preview", true);
        model.addObject("isEnterprise",flag);
        model.setViewName("/contract/ztContract-preview");
        return model;
    }
    
    
    /**
     * 直投转让项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/transferContract/preview")
    public ModelAndView transferContractPreview(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.addObject("preview", true);
        model.setViewName("/contract/transferContract-preview");
        return model;
    }
    
    /**
     * 直投转让项目合同预览
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/ztTransferContract/preview")
    public ModelAndView ztTransferContractPreview(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.addObject("preview", true);
        model.setViewName("/contract/ztTransferContract-preview");
        return model;
    }
    
    /**
	 * 获取等本等息推荐项目
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/averageCapitalMethod" , method = RequestMethod.POST)
	@ResponseBody
	public ResultDO averageCapitalMethod(
			HttpServletRequest req, HttpServletResponse resp){
		ResultDO result = projectService.averageCapitalMethod();
		return result;
	}
	
	
	 /**
	  * 
	  * @Description:奖励详情
	  * @param req
	  * @param resp
	  * @return
	  * @author: chaisen
	  * @time:2016年11月3日 下午9:19:42
	  */
	 @RequestMapping(value = "/lottery/rewardDetail",method=RequestMethod.POST)
	 @ResponseBody
	 public ResultDO<ProjectForRewardDetail> rewardDetail(HttpServletRequest req, HttpServletResponse resp) {
		 Long projectId = ServletRequestUtils.getLongParameter(req, "projectId",0L);
		 return projectService.getrewardDetail(projectId);
	 }
	
}
