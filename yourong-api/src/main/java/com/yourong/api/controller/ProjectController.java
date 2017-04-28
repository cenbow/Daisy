package com.yourong.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yourong.api.dto.*;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.ProjectNoticeService;
import com.yourong.api.service.ProjectService;
import com.yourong.api.service.TransactionService;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageLinkModel;
import com.yourong.core.ic.model.biz.TransferProjectBiz;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.core.ic.model.query.TransferProjectQuery;
import com.yourong.core.sys.manager.SysAreaManager;
import com.yourong.core.tc.model.query.TransactionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("project")
public class ProjectController extends BaseController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectNoticeService projectNoticeService;

	@Autowired
	private SysAreaManager sysAreaManager;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private MemberService memberService;

	/**
	 * 项目列表
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryProjectList", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO queryProjectList(
			@ModelAttribute("projectQuery") ProjectQuery query,
			HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		query.setCurrentPage(pageNo);
		ResultDTO resultDTO = new ResultDTO();
		searchPre(query);
		query.setAppQuery(true);
		return getProjectList(resultDTO, query);
	}

	/**
	 * 项目列表1.0.2 放开新客项目&租凭分红项目
	 *
	 * @param query
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryProjectList", method = RequestMethod.POST, headers = { "Accept-Version=1.0.2" })
	@ResponseBody
	public ResultDTO queryProjectList2(
			@ModelAttribute("projectQuery") ProjectQuery query,
			HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		query.setCurrentPage(pageNo);
		ResultDTO resultDTO = new ResultDTO();
		searchPre(query);
		query.setAppQuery(false);
		return getProjectListTemp(resultDTO, query);
	}

	// 提供给旧版本APP,过滤P2P项目
	private ResultDTO getProjectListTemp(ResultDTO resultDTO, ProjectQuery query) {
		query.setIsbuyCarNotShow(false);//
		if (query.getStatus() != null && query.getStatus().equals("-5")) {// 请求没有指明状态，直接返回
			resultDTO.setResultCode(ResultCode.ERROR);
			return resultDTO;
		}
		if (query.getStatusCode().equals("notice")) {// 预告项目
			List<ProjectNoticeDto> projectNoticeDto = projectNoticeService
					.p2pGetProjectNoticeList();
			JSONObject b = new JSONObject();
			b.put("data", projectNoticeDto);
			b.put("statusCode", query.getStatusCode());
			resultDTO.setResult(b);
		} else {
			ProjectPageDto<ProjectListDto> projectListDto = projectService
					.p2pQueryPagingProject(query);
			resultDTO.setResult(projectListDto);
		}
		return resultDTO;
	}

	/**
	 * 项目列表1.3.0 放开新客项目&租凭分红项目&直投项目
	 *
	 * @param query
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryProjectList", method = RequestMethod.POST, headers = { "Accept-Version=1.3.0" })
	@ResponseBody
	public ResultDTO queryProjectList3(
			@ModelAttribute("projectQuery") ProjectQuery query,
			HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		query.setCurrentPage(pageNo);
		ResultDTO resultDTO = new ResultDTO();
		searchPre(query);
		query.setAppQuery(false);
		return getProjectList(resultDTO, query);
	}

	/**
	 * 项目列表1.3.0 放开新客项目&租凭分红项目&直投项目
	 *
	 * @param query
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryProjectList", method = RequestMethod.POST, headers = { "Accept-Version=1.9.6" })
	@ResponseBody
	public ResultDTO queryProjectList4(
			@ModelAttribute("projectQuery") ProjectQuery query,
			HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		query.setCurrentPage(pageNo);
		ResultDTO resultDTO = new ResultDTO();
		searchPre(query);
		query.setAppQuery(false);
		query.setIsbuyCarNotShow(false);//
		if (query.getStatus() != null && query.getStatus().equals("-5")) {// 请求没有指明状态，直接返回
			resultDTO.setResultCode(ResultCode.ERROR);
			return resultDTO;
		}
		if (query.getStatusCode().equals("notice")) {// 预告项目
			List<ProjectNoticeDto> projectNoticeDto = projectNoticeService
					.getProjectNoticeList();
			JSONObject b = new JSONObject();
			b.put("data", projectNoticeDto);
			b.put("statusCode", query.getStatusCode());
			resultDTO.setResult(b);
		} else {
			ProjectPageDto<ProjectListDto> projectListDto = projectService
					.queryPagingProject(query);
			ProjectListAndQuickProjectDto dto = new ProjectListAndQuickProjectDto();
			dto.setProjectPageDto(projectListDto);
			//加载资产包集合

			if ("30".equals(query.getStatus())) {
				// 加载快投推荐项目
				ProjectListDto quickDto = projectService.getRecommendQuickProject();
				dto.setQuickProject(quickDto);
			}
			resultDTO.setResult(dto);
		}
		return resultDTO;
	}
	/**
	 * 添加项目包
	 * @param query
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryProjectList", method = RequestMethod.POST, headers = { "Accept-Version=2.1.0" })
	@ResponseBody
	public ResultDTO queryProjectList5(@ModelAttribute("projectQuery") ProjectQuery query,	HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		query.setCurrentPage(pageNo);
		ResultDTO resultDTO = new ResultDTO();
		searchPre(query);
		query.setAppQuery(false);

		query.setIsbuyCarNotShow(false);//
		if (query.getStatus() != null && query.getStatus().equals("-5")) {// 请求没有指明状态，直接返回
			resultDTO.setResultCode(ResultCode.ERROR);
			return resultDTO;
		}
		if (query.getStatusCode().equals("notice")) {// 预告项目
			List<ProjectNoticeDto> projectNoticeDto = projectNoticeService
					.getProjectNoticeList();
			JSONObject b = new JSONObject();
			b.put("data", projectNoticeDto);
			b.put("statusCode", query.getStatusCode());
			resultDTO.setResult(b);
		} else {
			ProjectPageDto<ProjectListDto> projectListDto = projectService
					.queryPagingProject(query);
			ProjectListAndQuickProjectDto dto = new ProjectListAndQuickProjectDto();
			dto.setProjectPageDto(projectListDto);
			if(pageNo ==1){//第一页才显示
				//查询项目包列表
				ProjectPackageListDto<ProjectPackage> packDto = projectService.queryAllProjectPackageList();
				if(packDto.getData() ==null ||packDto.getData().isEmpty()){
					packDto =null;
				}
				int count = projectService.countCompletedProjectPackage(StatusEnum.PROJECT_PACKAGE_STATUS_SALESCOMPLETED.getStatus());
				if(count > 0){
					dto.setExistCompleted(true);
				}
				dto.setPackDto(packDto);
			}
			if ("30".equals(query.getStatus())) {
				// 加载快投推荐项目
				ProjectListDto quickDto = projectService.getRecommendQuickProject();
				dto.setQuickProject(quickDto);
			}
			resultDTO.setResult(dto);
		}
		return resultDTO;
	}
	private ResultDTO getProjectList(ResultDTO resultDTO, ProjectQuery query) {
		query.setIsbuyCarNotShow(false);//
		if (query.getStatus() != null && query.getStatus().equals("-5")) {// 请求没有指明状态，直接返回
			resultDTO.setResultCode(ResultCode.ERROR);
			return resultDTO;
		}
		if (query.getStatusCode().equals("notice")) {// 预告项目
			List<ProjectNoticeDto> projectNoticeDto = projectNoticeService
					.getProjectNoticeList();
			JSONObject b = new JSONObject();
			b.put("data", projectNoticeDto);
			b.put("statusCode", query.getStatusCode());
			resultDTO.setResult(b);
		} else {
			ProjectPageDto<ProjectListDto> projectListDto = projectService
					.queryPagingProject(query);
			resultDTO.setResult(projectListDto);
		}
		return resultDTO;
	}

	/**
	 * 根据项目编号获得项目数据
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryProjectById", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	@Deprecated
	public ResultDTO<ProjectInfoDto> getProjectById(HttpServletRequest req,
													HttpServletResponse resp) {
		ResultDTO<ProjectInfoDto> resultDTO = new ResultDTO<ProjectInfoDto>();
		Long pid = ServletRequestUtils.getLongParameter(req, "pid", 0L);
		if (pid > 0) {
			resultDTO = projectService.getProjectInfoById(pid);
		} else {
			resultDTO.setResultCode(ResultCode.ERROR);
		}
		return resultDTO;
	}

	/**
	 * 根据项目编号获得项目数据 v1.0.2版本
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryProjectById", method = RequestMethod.POST, headers = { "Accept-Version=1.0.2" })
	@ResponseBody
	public ResultDTO<ProjectInfoDto> getProjectById2(HttpServletRequest req,
													 HttpServletResponse resp) {
		ResultDTO resultDTO = new ResultDTO();
		Long pid = ServletRequestUtils.getLongParameter(req, "pid", 0L);
		Long memberId = getMemberID(req);
		if (pid > 0) {
			JSONObject json = new JSONObject();
			resultDTO = projectService.getProjectInfoById(pid);
			if (resultDTO.isSuccess()) {
				ProjectInfoDto p = (ProjectInfoDto) resultDTO.getResult();
				if (memberId != null) {
					if (p != null && p.getIsNoviceProject()) {
						if (transactionService.hasTransactionByMember(memberId) && memberService.needCheckNoviceProject(memberId)) {
							json.put("isNewCustomer", false);
							json.put("project", p);
							resultDTO.setResult(json);
							return resultDTO;
						}
					}
				}
				json.put("isNewCustomer", true);
				json.put("project", p);
				resultDTO.setResult(json);
			}
		} else {
			resultDTO.setResultCode(ResultCode.ERROR);
		}
		return resultDTO;
	}

	/**
	 * 获得预告中的项目
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryNoticeProjectList", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO getNoticeProjectList(HttpServletRequest req,
										  HttpServletResponse resp) {
		ResultDTO resultDTO = new ResultDTO();
		List<ProjectNoticeDto> projectNoticeList = projectNoticeService
				.getProjectNoticeList();
		resultDTO.setResult(projectNoticeList);
		return resultDTO;
	}

	@RequestMapping(value = "queryProjectInterestById", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO getProjectInterestList(HttpServletRequest req,
											HttpServletResponse resp) {
		Long pid = ServletRequestUtils.getLongParameter(req, "pid", 0L);
		Long memberId = ServletRequestUtils.getLongParameter(req, "memberId",
				0L);
		int projectCategory = ServletRequestUtils.getIntParameter(req,
				"projectCategory", 1);
		Long transferId = ServletRequestUtils.getLongParameter(req,
				"transferId", 0l);
		if (pid < 1) {
			ResultDTO resultDTO = new ResultDTO();
			resultDTO.setResultCode(ResultCode.ERROR);
			return resultDTO;
		}
		return projectService.getProjectInterestList(pid, memberId,
				projectCategory, transferId);
	}

	/**
	 * 搜索的前置条件
	 *
	 * @param query
	 */
	private void searchPre(ProjectQuery query) {
		String statusCode = query.getStatusCode();
		// 加这些判断主要是防止“错误数据”，以及前台Tab项的切换
		if (statusCode != null) {
			if (!statusCode.equals("investing")
					&& !statusCode.equals("performance")
					&& !statusCode.equals("repayment")
					&& !statusCode.equals("notice")
					&& !statusCode.equals("all")) {
				query.setStatus("-5");
				query.setStatusCode(null);
			}
			if (statusCode.equals("investing")) {
				query.setStatus("30");
			}
		} else {
			// 请求格式不标准
			query.setStatus("-5");
		}
		query.setProjectType(null);
		query.setPageSize(5);
	}

	/**
	 * 查询项目详情
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "detail-{projectId}.html")
	public String queryProjectDetail(@PathVariable String projectId,
									 HttpServletRequest req, HttpServletResponse resp) {
		if (StringUtil.isNumeric(projectId)) {
			ProjectDetailDto project = projectService.getProjectDetail(Long
					.parseLong(projectId));
			if(project.getId()==null){//拦截m站用户url访问
				return "redirect:/404";
			}
			req.setAttribute("project", project);
			return "/project/info";
		}
		return "redirect:/404";
	}

	/**
	 * p2p-查询项目详情
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "p2p-detail-{projectId}.html")
	public String queryP2pProjectDetail(@PathVariable String projectId,
										HttpServletRequest req, HttpServletResponse resp) {
		if (StringUtil.isNumeric(projectId)) {
			Long memberId = getMemberIdAppToHtml(req);
			ProjectDetailDto project = projectService.p2pProjectDetail(
					Long.parseLong(projectId), memberId);
			if(project.getInvestType()==null){//拦截m站用户url访问
				return "redirect:/404";
			}
			req.setAttribute("project", project);
			return "/project/ztInfo";
		}
		return "redirect:/404";
	}

	/**
	 * p2p-查询项目详情
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "p2p-detail150-{projectId}.html")
	public String queryP2pProjectDetailNew(@PathVariable String projectId,
										   HttpServletRequest req, HttpServletResponse resp) {
		if (StringUtil.isNumeric(projectId)) {
			Long memberId = getMemberIdAppToHtml(req);
			ProjectDetailDto project = projectService.p2pProjectDetail(
					Long.parseLong(projectId), memberId);
			if(project.getInvestType()==null){//拦截m站用户url访问
				return "redirect:/404";
			}
			req.setAttribute("project", project);
			return "/project/ztInfo150";
		}
		return "redirect:/404";
	}

	/**
	 * p2p-查询项目还款计划
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "p2p-repaymentPlan150-{projectId}.html")
	public String queryP2pProjectrepaymentPlan(@PathVariable String projectId,
											   HttpServletRequest req, HttpServletResponse resp) {
		if (StringUtil.isNumeric(projectId)) {
			ProjectDetailDto project = projectService
					.p2pProjectrepaymentPlan(Long.parseLong(projectId));
			req.setAttribute("project", project);
			return "/project/repaymentPlan";
		}
		return "redirect:/404";
	}

	/**
	 * p2p-查询项目还款计划
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "p2p-repaymentPlan-{projectId}.html")
	public String queryP2pProjectrepaymentPlanNew(
			@PathVariable String projectId, HttpServletRequest req,
			HttpServletResponse resp) {
		if (StringUtil.isNumeric(projectId)) {
			ProjectDetailDto project = projectService
					.p2pProjectrepaymentPlan(Long.parseLong(projectId));
			req.setAttribute("project", project);
			req.setAttribute("pid", projectId);
			return "/project/repaymentPlan150";
		}
		return "redirect:/404";
	}

	/**
	 * p2p-查询项目还款计划
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getRepaymentPlan", method = RequestMethod.POST, headers = { "Accept-Version=1.5.0" })
	@ResponseBody
	public ResultDTO getP2pProjectrepaymentPlan(HttpServletRequest req,
												HttpServletResponse resp) {
		ResultDTO resultDTO = new ResultDTO();
		Long pid = ServletRequestUtils.getLongParameter(req, "pid", 0L);
		if (pid != null) {
			RepaymentPlanDto project = projectService
					.p2pProjectrepaymentPlanJosn(pid);
			resultDTO.setResult(project);
		}
		return resultDTO;
	}

	/**
	 * 项目合同资料图片
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "contract-{projectId}.html")
	public String queryProjectContractImage(@PathVariable String projectId,
											HttpServletRequest req, HttpServletResponse resp) {
		if (StringUtil.isNumeric(projectId)) {
			List<BscAttachment> contractAttachmentList = projectService
					.getProjectContractImage(Long.parseLong(projectId));
			req.setAttribute("contractAttachmentList", contractAttachmentList);
			return "/project/contract";
		}
		return "redirect:/404";
	}

	/**
	 * p2p-项目合同资料图片
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "p2p-contract-{projectId}.html")
	public String queryP2pProjectContractImage(@PathVariable String projectId,
											   HttpServletRequest req, HttpServletResponse resp) {
		if (StringUtil.isNumeric(projectId)) {
			Long memberId = getMemberIdAppToHtml(req);
			ProjectDetailDto project = projectService
					.getP2pProjectContractImage(Long.parseLong(projectId),
							memberId);
			req.setAttribute("project", project);
			return "/project/ztcontract";
		}
		return "redirect:/404";
	}

	/**
	 * 转让项目详情
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "transfer-detail-{projectId}.html")
	public String queryTransferProjectDetail(@PathVariable String projectId,
											 HttpServletRequest req, HttpServletResponse resp) {
		if (StringUtil.isNumeric(projectId)) {
			Long memberId = getMemberIdAppToHtml(req);
			TransferProjectBiz project = projectService
					.transferProjectDetail(Long.parseLong(projectId));
			if(project.getProjectId()==null){
				return "redirect:/404";
			}
			req.setAttribute("project", project);
			return "/project/transferInfo";
		}
		return "redirect:/404";
	}

	/**
	 * 转让项目还款计划
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "transfer-repaymentPlan-{projectId}.html")
	public String transferProjectrepaymentPlan(@PathVariable String projectId,
											   HttpServletRequest req, HttpServletResponse resp) {
		if (StringUtil.isNumeric(projectId)) {
			req.setAttribute("pid", projectId);
			return "/project/transferRepaymentPlan";
		}
		return "redirect:/404";
	}

	/**
	 * 查询转让项目还款计划
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getTransferRepaymentPlan", method = RequestMethod.POST, headers = { "Accept-Version=1.8.0" })
	@ResponseBody
	public ResultDTO getTransferRepaymentPlan(HttpServletRequest req,
											  HttpServletResponse resp) {
		Long pid = ServletRequestUtils.getLongParameter(req, "pid", 0L);
		ResultDTO<Object> result = new ResultDTO<Object>();
		if (pid != null) {
			result = projectService.transferProjectrepaymentPlan(pid);
		}
		return result;
	}

	/**
	 * 转让项目投资记录
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/transfer-record-{projectId}.html")
	public String queryTransferTransactionByProjectId(
			@ModelAttribute("transactionQuery") TransactionQuery transactionQuery,
			HttpServletRequest req, HttpServletResponse resp) {
		transactionQuery.setPageSize(20);
		transactionQuery.setProjectId(transactionQuery.getProjectId());
		Page<TransactionForProjectDto> page = transactionService
				.selectTransferTransactionForProjectsForPage(transactionQuery);
		req.setAttribute("orderList", page);
		req.setAttribute("pid", transactionQuery.getTransferId());
		return "/project/transferRecord";
	}

	/**
	 * 项目投资记录
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = { "/record2-{projectId}-{currentPage}.html",
			"/record-{projectId}.html" })
	public String queryTransactionByProjectId(
			@ModelAttribute("transactionQuery") TransactionQuery transactionQuery,
			HttpServletRequest req, HttpServletResponse resp) {
		transactionQuery.setPageSize(20);
		transactionQuery.setProjectId(transactionQuery.getProjectId());
		Page<TransactionForProjectDto> page = transactionService
				.selectTransactionForProjectsForPage(transactionQuery);
		req.setAttribute("orderList", page);
		req.setAttribute("pid", transactionQuery.getProjectId());
		return "/project/record";
	}


	@RequestMapping("/queryTransactionDetail")
	@ResponseBody
	public Object getTransactionDetailByProjectId(HttpServletRequest req,
												  HttpServletResponse resp) {
		Long projectId = ServletRequestUtils.getLongParameter(req, "projectId",
				0L);
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		TransactionQuery transactionQuery = new TransactionQuery();
		transactionQuery.setPageSize(20);
		transactionQuery.setProjectId(projectId);
		transactionQuery.setCurrentPage(pageNo);
		Page<TransactionForProjectDto> page = transactionService
				.selectTransactionForProjectsForPage(transactionQuery);
		// List<TransactionForProjectDto> list = Lists.newArrayList();
		// if(Collections3.isNotEmpty(page.getData())){
		// list = BeanCopyUtil.mapList(page.getData(),
		// TransactionForProjectDto.class);
		// }
		return page;
	}

	/**
	 * 查询项目投资记录
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getTransferProjectRecord", method = RequestMethod.POST, headers = { "Accept-Version=1.9.3" })
	@ResponseBody
	public ResultDTO getTransferProjectRecord(HttpServletRequest req,
											  HttpServletResponse resp) {
		Long projectId = ServletRequestUtils.getLongParameter(req, "projectId",
				0L);
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		TransactionQuery transactionQuery = new TransactionQuery();
		transactionQuery.setPageSize(20);
		transactionQuery.setProjectId(projectId);
		transactionQuery.setCurrentPage(pageNo);
		Page<TransactionForProjectDto> page = transactionService
				.selectTransferTransactionForProjectsForPage(transactionQuery);
		ResultDTO<Object> result = new ResultDTO<Object>();
		result.setResult(page);
		return result;
	}

	/**
	 * 查询项目投资记录
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getProjectRecord", method = RequestMethod.POST, headers = { "Accept-Version=1.9.3" })
	@ResponseBody
	public ResultDTO getProjectRecord(HttpServletRequest req,
									  HttpServletResponse resp) {
		Long projectId = ServletRequestUtils.getLongParameter(req, "projectId",
				0L);
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		TransactionQuery transactionQuery = new TransactionQuery();
		transactionQuery.setPageSize(20);
		transactionQuery.setProjectId(projectId);
		transactionQuery.setCurrentPage(pageNo);
		Page<TransactionForProjectDto> page = transactionService
				.selectTransactionForProjectsForPage(transactionQuery);
		ResultDTO<Object> result = new ResultDTO<Object>();
		result.setResult(page);
		return result;
	}


	/**
	 * 转让项目列表
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryTransferProjectList", method = RequestMethod.POST, headers = { "Accept-Version=1.8.0" })
	@ResponseBody
	public ResultDTO queryTransferProjectList(
			@ModelAttribute("transferProjectQuery") TransferProjectQuery transferProjectQuery,
			HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		transferProjectQuery.setCurrentPage(pageNo);

		ResultDTO resultDTO = new ResultDTO();
		transferTearchPre(transferProjectQuery);
		transferProjectQuery.setAppQuery(true);
		ProjectPageDto<ProjectListDto> projectListDto = projectService
				.queryTransferProject(transferProjectQuery);
		resultDTO.setResult(projectListDto);
		return resultDTO;
	}

	/**
	 * 搜索的前置条件
	 *
	 * @param query
	 */
	private void transferTearchPre(TransferProjectQuery query){
		String orderSource = query.getOrderSource();
		//加这些判断主要是防止“错误数据”，以及前台Tab项的切换
		/*if(orderSource != null&&StringUtil.isNotBlank(orderSource)){
			
		}else{*/
		//空处理
		query.setOrderSource("createTimeDesc"); //转让发起时间倒序
		//}
		query.setProjectType(null);
		query.setPageSize(5);
	}

	/**
	 * 直投，快投有奖
	 *
	 * @param projectId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "p2p-direct-reward-{projectId}.html")
	public String queryP2pDirectReward(@PathVariable String projectId,
									   HttpServletRequest req, HttpServletResponse resp) {
		if (StringUtil.isNumeric(projectId)) {
			Object data = projectService.directRewardDetail(
					Long.parseLong(projectId));
			req.setAttribute("data", JSON.toJSON(data));
			return "/app/activity/directReward/index";
		}
		return "redirect:/404";
	}
	/**
	 * 查询资产包项目内容
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryProjectPackageDetailList", method = RequestMethod.POST, headers = { "Accept-Version=2.1.0" })
	@ResponseBody
	public ResultDTO queryProjectPackageDetailList(
			@ModelAttribute("projectPackageId") Long projectPackageId,@ModelAttribute("status") Integer status,
			HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO dto =new ResultDTO();
		ProjectPackageDto resultDTO = new ProjectPackageDto();
		ProjectPackageListDto<ProjectPackageLinkModel> packageLinkDto = projectService.findProjectPackageList(projectPackageId);
		String minRewardLimit = projectService.getMinRewardLimit();
		resultDTO.setMinRewardLimit(minRewardLimit);
		resultDTO.setData(packageLinkDto);
		dto.setResult(resultDTO);
		return dto;
	}
	/**
	 * 查询资产包列表
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryProjectPackageList", method = RequestMethod.POST, headers = { "Accept-Version=2.1.0" })
	@ResponseBody
	public ResultDTO queryProjectPackageList(@ModelAttribute("projectQuery") ProjectQuery projectQuery,
											 HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO resultDTO = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		if(projectQuery.getPageSize() == 0){
			projectQuery.setPageSize(20);
		}
		projectQuery.setCurrentPage(pageNo);
		Page<ProjectPackage> page = projectService.queryProjectPackageList(projectQuery);
		resultDTO.setResult(page);
		return resultDTO;
	}
}
