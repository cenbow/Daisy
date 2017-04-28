package com.yourong.backend.ic.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.fin.service.BalanceService;
import com.yourong.backend.ic.service.DebtService;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FileInfoUtil;
import com.yourong.core.bsc.model.AttachmentIndex;
import com.yourong.core.cache.RedisForProjectClient;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectBiz;
import com.yourong.core.ic.model.ProjectInterest;
import com.yourong.core.sys.model.SysUser;

@Controller
@RequestMapping("project")
public class ProjectController extends BaseController {

	@Autowired
	private DebtService debtService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private BalanceService balanceService;
	

	/**
	 * 添加项目页面
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "add")
	@RequiresPermissions("project:add")
	public String addPage(HttpServletRequest req, HttpServletResponse resp) {
		return "/ic/project/project";
	}

	/**
	 * 查看项目页面
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "show")
	@RequiresPermissions("project:find")
	public String showProjectPage(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		req.setAttribute("action", "show");
		req.setAttribute("id", id);
		return "/ic/project/project";
	}
	
	/**
	 * 编辑项目页面
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "edit")
	@RequiresPermissions("project:edit")
	public String editProjectPage(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		req.setAttribute("action", "edit");
		req.setAttribute("id", id);
		return "/ic/project/project";
	}
	
	/**
	 * 紧急编辑项目页面
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "emergency")
	@RequiresPermissions("project:emergency")
	public String emergencyProjectPage(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		req.setAttribute("action", "emergency");
		req.setAttribute("id", id);
		return "/ic/project/project";
	}

	
	/**
	 * 根据编号查看项目
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "find")
	@RequiresPermissions("project:find")
	@ResponseBody
	public ProjectBiz findProjectById(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		ProjectBiz projectBiz = projectService.findProjectById(id);
		return projectBiz;
	}
	
	
	
	/**
	 * 项目列表
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("project:index")
	public String showProjectIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/ic/project/index";
	}
	
	/**
	 * 项目列表
	 * @param project
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "ajax")
	@RequiresPermissions("project:index")
	@ResponseBody
	public Object showProjectList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Page<Project> pageRequest = new Page<Project>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		return projectService.findByPage(pageRequest, map);
	}
	
	/**
	 * 添加项目
	 * @param project
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "save")
	@RequiresPermissions("project:add")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块",desc = "保存项目")
	public ResultDO saveProject(@ModelAttribute Project project ,HttpServletRequest req, HttpServletResponse resp){
		ResultDO result = new ResultDO();
		try {
			if(project != null){
				// 判断项目名称或当前期数是否重复
				String projectName = project.getName();
				String namePeriod = projectName.substring(0, projectName.indexOf("期") + 1);
				boolean exists = projectService.checkNameExists(projectName, namePeriod);
				if(exists){
//					result.setResultCode(ResultCode.PROJECT_NAME_EXISTS_ERROR);
					result.setResultCode(ResultCode.PROJECT_NAME_TERM_EXISTS_ERROR);
					return result;
				}
				
				SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
				//当前登录用户
				project.setPublishId(sysUser.getId());
				//阶递收益
				List<ProjectInterest> projectInterestList = parseJsonToObject(req,"interestData",ProjectInterest.class);
				project.setProjectInterestList(projectInterestList);
				//附件索引
				List<AttachmentIndex> attachmentIndexList = parseJsonToObject(req,"attachmentIndexData",AttachmentIndex.class);
				project.setAttachmentIndexList(attachmentIndexList);
				project.setThumbnail(getThumbnailUrl(req));
				result = projectService.addProject(project);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 获得缩略图路径
	 * @param req
	 * @return
	 */
	private String getThumbnailUrl(HttpServletRequest req){
        try {
        	int x = Math.round(ServletRequestUtils.getFloatParameter(req, "x", 0));
 	        int y = Math.round(ServletRequestUtils.getFloatParameter(req, "y", 0));
 	        int w = Math.round(ServletRequestUtils.getFloatParameter(req, "w", 0));
 	        int h = Math.round(ServletRequestUtils.getFloatParameter(req, "h", 0));
        	if(w>1 && h>1){
	        	String appPath = req.getSession().getServletContext().getRealPath("/");
	        	String filePath = appPath+ServletRequestUtils.getStringParameter(req, "thumbnail");
	        	String newPath = FileInfoUtil.rename(filePath, "project_");
				Thumbnails.of(filePath).sourceRegion(x, y, w, h).size(120, 120).keepAspectRatio(false).toFile(newPath);
				return newPath;
        	}
		} catch (Exception e) {
			logger.error("生成项目缩略图异常", e);
		}
        return null;
	}
	
	/**
	 * 更新项目
	 * @param project
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "update")
	@RequiresPermissions("project:edit")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块",desc = "更新项目")
	public ResultDO updateProject(@ModelAttribute Project project ,@ModelAttribute("action") String action,
			@ModelAttribute("interestData") String interestData,
			@ModelAttribute("attachmentIndexData") String attachmentIndexData, 
			HttpServletRequest req, HttpServletResponse resp){
		ResultDO result = new ResultDO();
		try {
			if(project != null){
				// 判断项目名称或当前期数是否重复
				String projectName = project.getName();
				String namePeriod = projectName.substring(0, projectName.indexOf("期") + 1);
				boolean exists = projectService.checkNameExists(project.getName(), namePeriod, project.getId());
				if(exists){
//					result.setResultCode(ResultCode.PROJECT_NAME_EXISTS_ERROR);
					result.setResultCode(ResultCode.PROJECT_NAME_TERM_EXISTS_ERROR);
					return result;
				}
				SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
				//当前登录用户
				project.setPublishId(sysUser.getId());
				//阶递收益
				List<ProjectInterest> projectInterestList = parseJsonToObject(req,"interestData",ProjectInterest.class);
				project.setProjectInterestList(projectInterestList);
				//附件索引
				List<AttachmentIndex> attachmentIndexList = parseJsonToObject(req,"attachmentIndexData",AttachmentIndex.class);
				project.setAttachmentIndexList(attachmentIndexList);
				String thumbnail = getThumbnailUrl(req);
				if(StringUtil.isNotBlank(thumbnail)){
					project.setThumbnail(thumbnail);
				}
				if(action!=null && "emergency".equals(action)){
					logger.info("紧急修改项目",project);
					SysServiceUtils.writeLogger("项目模块","紧急修改项目");
					result = projectService.emergencyUpdateProject(project);
				}else{
					result = projectService.updateProject(project);
				}
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("项目保存出错",e);
		}
		return result;
	}
	
	
	/**
	 * 删除项目（逻辑）
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "delete")
	@RequiresPermissions("project:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块",desc = "删除详情")
	public ResultDO deleteProject(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO result = new ResultDO();
		try {
			Long id = ServletRequestUtils.getLongParameter(req, "id");
			int delFlag = projectService.deleteProjectById(id);
			if(delFlag <= 0){
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 提交待审
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "waitReview")
	@RequiresPermissions("project:waitReview")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块",desc = "提交待审")
	public ResultDO waitReview(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO result = new ResultDO();
		try {
			Long id = ServletRequestUtils.getLongParameter(req, "id");
			SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
			int actionFlag = projectService.waitReviewProject(id, sysUser.getId());
			if(actionFlag <= 0){
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 审核项目
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "review")
	@RequiresPermissions("project:review")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块",desc = "审核项目")
	public ResultDO reviewProject(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO result = new ResultDO();
		try {
			Long id = ServletRequestUtils.getLongParameter(req, "id");
			String msg = ServletRequestUtils.getStringParameter(req, "message");
			Integer radioStatus = ServletRequestUtils.getIntParameter(req, "radioStatus",0);
			SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
			int actionFlag = 0;
			if(radioStatus > 0){
				//判断项目是否为经营融，信用额度是否满足要求
				result=checkCompanyCreditAmount(id);
				if(!result.isSuccess()){
					return result;
				}
				actionFlag = projectService.reviewProject(id, sysUser.getId(), msg);
			}else{
				actionFlag = projectService.fallbackSaveStatus(id, sysUser.getId(), msg);
			}
			if(actionFlag <= 0){
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 修改上线时间
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "updateOnlineTimeAndEndDate")
	@RequiresPermissions("project:updateOnlineTimeAndEndDate")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块",desc = "修改上线时间&销售截止时间")
	public ResultDO updateOnlineTimeAndSaleEndTime(@RequestParam("id")Long id,HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO result = new ResultDO();
		try {
			String StrOnlineTime = ServletRequestUtils.getStringParameter(req, "onlineTime");
			String StrSaleEndTime = ServletRequestUtils.getStringParameter(req, "saleEndTime");
			Date onlineTime = DateUtils.getDateFromString(StrOnlineTime, "yyyy-MM-dd HH:mm");
			Date saleEndTime = DateUtils.getDateFromString(StrSaleEndTime, "yyyy-MM-dd HH:mm");
			result = projectService.updateOnlineTimeAndEndDate(onlineTime, saleEndTime, id);
			logger.info("projectId:{},项目上线时间:{},销售截止时间={}",id,StrOnlineTime,StrSaleEndTime);
		} catch (Exception e) {
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 修改项目到期时间
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "updateEndDate")
	@RequiresPermissions("project:updateEndDate")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块",desc = "修改销售截止时间")
	public ResultDO updateEndDate(@RequestParam("id")Long id,HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO result = new ResultDO();
		try {
			String StrSaleEndTime = ServletRequestUtils.getStringParameter(req, "saleEndTime");
			Date saleEndTime = DateUtils.getDateFromString(StrSaleEndTime, "yyyy-MM-dd HH:mm");
			result = projectService.updateEndDate(saleEndTime, id);
			logger.info("projectId:{},修改销售截止时间={}",id,StrSaleEndTime);
		} catch (Exception e) {
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 添加备注
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "addRemarks")
	@RequiresPermissions("project:addRemarks")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="项目模块",desc = "添加备注")
	public Object addRemarks(@ModelAttribute("newControlRemarks")  String newControlRemarks, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		return projectService.addControlRemarks(id, newControlRemarks);
	}
	/**
	 * 暂停项目
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "stop")
	@RequiresPermissions("project:stop")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块",desc = "暂停项目")
	public ResultDO stopProject(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO result = new ResultDO();
		try {
			Long id = ServletRequestUtils.getLongParameter(req, "id");
			String msg = ServletRequestUtils.getStringParameter(req, "message");
			SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
			int actionFlag = projectService.stopProject(id, sysUser.getId(), msg);
			if(actionFlag <= 0){
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
		}
		return result;
	}
	/**
	 * 
	 * @Description:置为存盘
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年4月29日 下午2:28:04
	 */
	@RequestMapping(value = "setSave")
	@RequiresPermissions("project:setSave")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块",desc = "置为存盘")
	public ResultDO setSaveProject(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO result = new ResultDO();
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		String msg = ServletRequestUtils.getStringParameter(req, "message");
		SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
		result  = projectService.setSaveProject(id, sysUser.getId(), msg);
		return result;
	}
	/**
	 * 恢复项目状态到投资中
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "start")
	@RequiresPermissions("project:start")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块",desc = "恢复项目状态到投资中")
	public ResultDO startProject(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ResultDO result = new ResultDO();
		try {
			Long id = ServletRequestUtils.getLongParameter(req, "id");
			String msg = ServletRequestUtils.getStringParameter(req, "message");
			SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
			int actionFlag = projectService.startProject(id, sysUser.getId(), msg);
			if(actionFlag <= 0){
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * 自动完成加载债权序列号
	 * 
	 * @param req
	 * @param resp
	 */
	@RequestMapping(value = "autoCompleteSerialNumber")
	@ResponseBody
	public List<Map<String, String>> autoCompleteSerialNumber(HttpServletRequest req, HttpServletResponse resp) {
		String serialNumber = req.getParameter("serialNumber");
		List<Map<String, String>> debtList = debtService.findSerialNumberAndMemberName(serialNumber);
		return debtList;
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getDebtBySerialNumber")
	@ResponseBody
	public DebtBiz getDebtBySerialNumber(HttpServletRequest req, HttpServletResponse resp) {
		String serialNumber = req.getParameter("serialNumber");
		DebtBiz biz = debtService.getFullDebtInfoBySerialNumber(serialNumber);
		return biz;
	}
	
	private ResultDO checkCompanyCreditAmount(Long id){
		ResultDO result = new ResultDO();
		ProjectBiz project = projectService.findProjectById(id);
		if(project.getProjectType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode()) && project.getDebtBiz().getEnterpriseId()!=null){
			Balance balance = balanceService.queryBalance(project.getDebtBiz().getEnterpriseId(), TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
			if(balance==null){
				result.setResultCode(ResultCode.DEBT_SAVE_ENTERPRISE_CREDIT_AMOUNT_NOT_NULL);
				return result;
			}
			if(balance.getAvailableBalance().compareTo(project.getTotalAmount())<0){
				result.setResultCode(ResultCode.PROJECT_AVAILABLE_CREDIT_LESS_THEN_PROJECT_AMOUNT_ERROR);
				return result;
			}
		}
		return result;
	}
	
	@RequestMapping(value = "delProjectRedis")
	@RequiresPermissions("project:delProjectRedis")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块", desc = "删除项目缓存")
	public Object delProjectRedis(HttpServletRequest req, HttpServletRequest resp, @ModelAttribute("projectId") Long projectId) {
		ResultDO<String> resultDO = new ResultDO<String>();
		RedisForProjectClient.clearTransactionDetail(projectId);
		resultDO.setSuccess(true);
		return resultDO;
	}
	
	@RequestMapping(value="getSerialNumber")
	@ResponseBody
	public Object getSerialNumber(@ModelAttribute("projectId")Long projectId){
		ResultDO<Debt> result = new ResultDO();
		result = projectService.getSerialNumber(projectId);
		return result;
	}
	
}
