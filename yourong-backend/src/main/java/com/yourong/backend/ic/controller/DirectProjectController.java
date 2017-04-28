package com.yourong.backend.ic.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.servlet.ModelAndView;

import rop.thirdparty.com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.yourong.backend.BaseController;
import com.yourong.backend.fin.service.BalanceService;
import com.yourong.backend.ic.service.BorrowerCreditService;
import com.yourong.backend.ic.service.DirectProjectService;
import com.yourong.backend.ic.service.ProjectOpenService;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.backend.sys.service.SysDictService;
import com.yourong.backend.uc.service.MemberService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.cache.RedisForProjectClient;
import com.yourong.core.ic.model.BorrowerCredit;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectOpen;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageBiz;
import com.yourong.core.ic.model.ProjectPackageModel;
import com.yourong.core.ic.model.biz.TransferProjectPageBiz;
import com.yourong.core.ic.model.biz.TransferRateList;
import com.yourong.core.ic.model.query.TransferProjectPageQuery;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.sys.model.SysUser;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.uc.model.Member;
import com.yourong.core.upload.util.UploadUtil;

/**
 * @desc 直投项目
 * @author fuyili 2015年12月30日下午6:17:49
 */
@Controller
@RequestMapping("directProject")
public class DirectProjectController extends BaseController {

	private static final String[] ATTACHMENT_TYPE_OF_DIRECT_PROJECT = { "signAttachmentsData", "borrowerAttachmentsData","borrowerMosaicAttachmentsData",
			"collateralAttachmentsData","collateralMosaicAttachmentsData", "contractAttachmentsData","contractMosaicAttachmentsData",
			"legalAttachmentsData","legalMosaicAttachmentsData","creditAttachmentsData","creditMosaicAttachmentsData",
			"baseAttachmentsData","baseMosaicAttachmentsData"};
	@Autowired
	private DirectProjectService directProjectService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectOpenService projectOpenService;

	@Autowired
	private BalanceService balanceService;
	
	@Autowired
	private MemberService memberService;

	@Autowired
	private SysDictService sysDictService;
	
	@Autowired
    private BorrowerCreditService borrowerCreditService;

	/**
	 * 直投项目列表页面
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("directProject:index")
	public String showProjectIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/ic/directProject/index";
	}
	/**
	 * 直投项目列表数据
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("directProject:ajax")
	@ResponseBody
	public Object showProjectList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<DirectProjectBiz> pageRequest = new Page<DirectProjectBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<DirectProjectBiz> directProjectBizPage = directProjectService.directFindByPage(pageRequest, map);
		// 查询借款人授信额度的上线提示
		if (directProjectBizPage != null) {
			setBorrowerCreditOnline(directProjectBizPage.getData());
		}
		return directProjectBizPage;
	}
	/**
	 * 查看资产包
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "packageIndex")
	@RequiresPermissions("directProject:packageIndex")
	public String packageIndex(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		return "/ic/directProject/package";
	}
	
	/**
	 * 直投资产包数据列表
	 */
	@RequestMapping(value = "packageAjax")
	@RequiresPermissions("directProject:packageAjax")
	@ResponseBody
	public Object packageAjax(HttpServletRequest req, HttpServletResponse resp,ProjectPackage projectPackage) throws ServletRequestBindingException {
		Page<ProjectPackage> pageRequest = new Page<ProjectPackage>();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<ProjectPackage> directProjectBizPage = directProjectService.directPackagePage(pageRequest, map,projectPackage);
		return directProjectBizPage;
	}
	
	
	/**
	 * 添加项目编号到资产项目中
	 */
	@RequestMapping(value = "addProjectNumber")
	@RequiresPermissions("directProject:addProjectNumber")
	@ResponseBody
	public int addProjectNumber(HttpServletRequest req, ProjectPackageModel packageModel){
		try {
			String appPath = req.getSession().getServletContext().getRealPath("/");
			packageModel.setAppPath(appPath);
			return directProjectService.addProjectNumber(packageModel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	

	/**
	 * 获取资产包内容
	 */
	@RequestMapping(value = "getPackage")
	@RequiresPermissions("directProject:getPackage")
	@ResponseBody
	public Object getPackage(HttpServletRequest req,Long projectPackageId) {
		return directProjectService.getPackage(projectPackageId);
	}
	
	/**
	 * 直投资产包项目列表数据
	 */
	@RequestMapping(value = "packageProjectAjax")
	@RequiresPermissions("directProject:packageProjectAjax")
	@ResponseBody
	public Object packageProjectAjax(HttpServletRequest req, HttpServletResponse resp,Long projectPackageId) throws ServletRequestBindingException {
		return directProjectService.getProjectPackageList(projectPackageId);
	}
	
	/**
	 * 删除项目编号到资产项目中
	 */
	@RequestMapping(value = "deleteProjectFromPackage")
	@RequiresPermissions("deleteProjectFromPackage")
	@ResponseBody
	public int deleteProjectFromPackage(String projectIds){
		return directProjectService.batchDelete(projectIds);
	}
	/**
	 *审核资产包包上线
	 */
	@RequestMapping(value = "auditProjectFromPackage")
	@RequiresPermissions("auditProjectFromPackage")
	@ResponseBody
	public int auditProjectFromPackage(Long projectPackageId){
		return directProjectService.auditProjectFromPackage(projectPackageId);
	}
	// 查询借款人授信额度的上线提示
	private void setBorrowerCreditOnline (List<DirectProjectBiz> directProjectBizs) {
		Map<String, Integer> onlineStatusMap = Maps.newHashMap();
		for (DirectProjectBiz directProjectBiz : directProjectBizs) {
			// 1-正常上线，2-暂停上线，3-正常上线(超出授信额)，4-暂停上线(超出授信额)
			// 渠道商
			Integer onLineFlagValue = -1;
			Integer onlineFlag = 1;
			Integer creditStatus = StatusEnum.DIRECT_BORROWER_CREDIT_STATUS_NORMAL.getStatus();
			String key = "";
			if(StringUtil.isNotBlank(directProjectBiz.getOpenPlatformKey())) {
				key = directProjectBiz.getBorrowerId() + directProjectBiz.getOpenPlatformKey();
				if(onlineStatusMap.containsKey(directProjectBiz.getBorrowerId() + directProjectBiz.getOpenPlatformKey())) {
					directProjectBiz.setOnlineFlag(onlineStatusMap.get(key));
				} else {
					BorrowerCredit borrowerCreditInfo = borrowerCreditService.selectByBorrower
							(null, 3, directProjectBiz.getOpenPlatformKey(),ProjectEnum.PROJECT_TYPE_DIRECT.getType());
					if (borrowerCreditInfo != null) {
						onlineFlag = borrowerCreditInfo.getOnlineFlag();
						creditStatus = borrowerCreditInfo.getStatus();
					}
				}
			} else { // 个人用户/企业用户
				if (directProjectBiz.getBorrowerId() != null) {
					key = directProjectBiz.getBorrowerId().toString();
				}
				if(onlineStatusMap.containsKey(directProjectBiz.getBorrowerId())) {
					directProjectBiz.setOnlineFlag(onlineStatusMap.get(directProjectBiz.getBorrowerId()));
				} else {
					BorrowerCredit borrowerCreditInfo = borrowerCreditService.selectByBorrower
							(directProjectBiz.getBorrowerId(), directProjectBiz.getBorrowerType(), null, ProjectEnum.PROJECT_TYPE_DIRECT.getType());
					if (borrowerCreditInfo != null) {
						onlineFlag = borrowerCreditInfo.getOnlineFlag();
						creditStatus = borrowerCreditInfo.getStatus();
					}
				}
				
			}
			
			if (onlineFlag == null || TypeEnum.BORROWER_CREDIT_NORMAL.getType() == onlineFlag) {
				if (creditStatus == StatusEnum.DIRECT_BORROWER_CREDIT_STATUS_OVER.getStatus()) {
					onLineFlagValue = 3;
				} else {
					onLineFlagValue = 1;
				}
			} else {
				if (creditStatus == StatusEnum.DIRECT_BORROWER_CREDIT_STATUS_OVER.getStatus()) {
					onLineFlagValue = 4;
				} else {
					onLineFlagValue = 2;
				}
			}
			directProjectBiz.setOnlineFlag(onLineFlagValue);
			onlineStatusMap.put(key, onLineFlagValue);
		}
		
	}

	/**
	 * 添加直投项目页面
	 */
	@RequestMapping(value = "add")
	@RequiresPermissions("directProject:add")
	public String addProject(HttpServletRequest req, HttpServletResponse resp) {
		return "/ic/directProject/directProject";
	}

	/**
	 * 保存直投项目
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "save")
	@RequiresPermissions("directProject:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "保存直投项目")
	public ResultDO<DirectProjectBiz> addProject(@ModelAttribute("directProjectBiz") DirectProjectBiz directProjectBiz,
			HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<DirectProjectBiz> result = new ResultDO<DirectProjectBiz>();
		try {
			// 设置状态
			directProjectBiz.setStatus(StatusEnum.PROJECT_STATUS_SAVE.getStatus());// 存盘
			// 设置添加人
			directProjectBiz.setPublishId(getCurrentLoginUserInfo().getId());
			String appPath = req.getSession().getServletContext().getRealPath("/");
			// 所有附件类型
			List<BscAttachment> totalAttachments = Lists.newArrayList();
			for (String attachment : ATTACHMENT_TYPE_OF_DIRECT_PROJECT) {
				List<BscAttachment> attachments = parseJsonToObject(req, attachment, BscAttachment.class);
				if (Collections3.isNotEmpty(attachments)) {
					totalAttachments.addAll(attachments);
				}
			}
			//不可转让
			if (directProjectBiz.getTransferFlag()!=null&&directProjectBiz.getTransferFlag()==0){
				directProjectBiz.setTransferAfterInterest(null);
			}
			directProjectBiz.setBscAttachments(totalAttachments);
			directProjectBiz.setThumbnail(UploadUtil.getThumbnailUrl(req));
			result = directProjectService.insertDirectProjectInfo(directProjectBiz, appPath);
		} catch (Exception e) {
			logger.error("保存直投项目失败，directProjectBiz={}",directProjectBiz,e);
		}
		return result;
	}

	/**
	 * 更新直投项目
	 */
	@RequestMapping(value = "update")
	@RequiresPermissions("directProject:update")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "更新直投项目")
	public ResultDO<DirectProjectBiz> updateProject(@ModelAttribute("directProjectBiz") DirectProjectBiz directProjectBiz,
			@RequestParam("action") String action, HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<DirectProjectBiz> result = new ResultDO<DirectProjectBiz>();
		try {
			// 设置添加人
			//directProjectBiz.setPublishId(getCurrentLoginUserInfo().getId());
			String appPath = req.getSession().getServletContext().getRealPath("/");
			// 所有附件类型
			List<BscAttachment> totalAttachments = Lists.newArrayList();
			for (String attachment : ATTACHMENT_TYPE_OF_DIRECT_PROJECT) {
				List<BscAttachment> attachments = parseJsonToObject(req, attachment, BscAttachment.class);
				if (Collections3.isNotEmpty(attachments)) {
					totalAttachments.addAll(attachments);
				}
			}
			String thumbnail = UploadUtil.getThumbnailUrl(req);
			if (StringUtil.isNotBlank(thumbnail)) {
				directProjectBiz.setThumbnail(thumbnail);
			}
			SysDict sysdict=projectService.selectByGroupNameAndValue(directProjectBiz.getChannelBusiness());
			if(sysdict!=null){
				directProjectBiz.setOpenPlatformKey(sysdict.getKey());
			}else {
				directProjectBiz.setOpenPlatformKey(null);
			}
			directProjectBiz.setBscAttachments(totalAttachments);
			
			
			if ("edit".equals(action)) {// 编辑
					result = directProjectService.editDirectProjectInfo(directProjectBiz, appPath,action);
			}else if("emergencyAll".equals(action)){//紧急修改（全部）
				logger.info("紧急修改（全部）",directProjectBiz);
				SysServiceUtils.writeLogger("直投项目模块","紧急修改（全部）");
				result = directProjectService.editDirectProjectInfo(directProjectBiz, appPath,action);
			} else if ("emergencyPart".equals(action)) {// 紧急修改（部分）
				logger.info("紧急修改（部分）",directProjectBiz);
				SysServiceUtils.writeLogger("直投项目模块","紧急修改（部分）");
				result = directProjectService.emergencyPartDirectProjectInfo(directProjectBiz, appPath);
			}	
		} catch (Exception e) {
			logger.error("更新直投项目失败，directProjectBiz={}",directProjectBiz,e);
		}
		return result;

	}

	/**
	 * 对外项目创建
	 */
	@RequestMapping(value = "opencreate")
	@RequiresPermissions(value = { "directProject:openCreate" })
	public ModelAndView openCreate(@RequestParam("openid") Long openid, HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mView = new ModelAndView();
		mView.setViewName("/ic/directProject/directProject");
		mView.addObject("openid", openid);
		mView.addObject("action", "open");
		return mView;
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "edit")
	@RequiresPermissions(value = { "directProject:edit" })
	public ModelAndView showEdit(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mView = new ModelAndView();
		mView.setViewName("/ic/directProject/directProject");
		mView.addObject("id", id);
		mView.addObject("action", "edit");
		return mView;
	}

	/**
	 * 详情
	 */
	@RequestMapping(value = "detail")
	@RequiresPermissions(value = { "directProject:detail" })
	public ModelAndView showDetail(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mView = new ModelAndView();
		mView.setViewName("/ic/directProject/directProject");
		mView.addObject("id", id);
		mView.addObject("action", "detail");
		return mView;
	}

	/**
	 * 紧急修改-全部
	 */
	@RequestMapping(value = "emergencyAll")
	@RequiresPermissions(value = { "directProject:emergencyAll" })
	public ModelAndView showEmergencyAll(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mView = new ModelAndView();
		mView.setViewName("/ic/directProject/directProject");
		mView.addObject("id", id);
		mView.addObject("action", "emergencyAll");
		return mView;
	}

	/**
	 * 紧急修改-部分
	 */
	@RequestMapping(value = "emergencyPart")
	@RequiresPermissions(value = { "directProject:emergencyPart" })
	public ModelAndView showEmergencyPart(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mView = new ModelAndView();
		mView.setViewName("/ic/directProject/directProject");
		mView.addObject("id", id);
		mView.addObject("action", "emergencyPart");
		return mView;
	}

	/**
	 * @Description:查询项目
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 * @author: fuyili
	 * @time:2016年1月7日 下午8:38:37
	 */
	@RequestMapping(value = "find")
	@RequiresPermissions("directProject:find")
	@ResponseBody
	public Object findProject(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp) {
		return directProjectService.findDirectProjectBizById(id);
	}

	/**
	 * 查询对外项目
	 * @param openid
	 * @param req
	 * @param resp
     * @return
     */
	@RequestMapping(value = "findopen")
	@RequiresPermissions("directProject:findOpenProject")
	@ResponseBody
	public Object findOpenProject(@RequestParam("openid") Long openid, HttpServletRequest req, HttpServletResponse resp) {
		DirectProjectBiz directProjectBiz=new DirectProjectBiz();
		ProjectOpen projectOpen=projectOpenService.queryById(openid);
		if (projectOpen==null){
			return directProjectBiz;
		}
		try {
			directProjectBiz= JSON.parseObject(projectOpen.getProjectbizJson(),DirectProjectBiz.class);
			List<BscAttachment> bscAttachments=JSON.parseObject(projectOpen.getBscAttachmentsJson(),new TypeReference<ArrayList<BscAttachment>>(){});
			directProjectBiz.setShortDesc(projectOpen.getShortDesc());
			directProjectBiz.setBorrowDetail(projectOpen.getBorrowDetail());
			directProjectBiz.setBscAttachments(bscAttachments);
			directProjectBiz.setInterestFrom(1);
			directProjectBiz.setProfitType("avg_principal");
			directProjectBiz.setBorrowerType(1);
			directProjectBiz.setProjectType("consumer_inst");
			directProjectBiz.setOverdueFeeRate(new BigDecimal(1));
			directProjectBiz.setGuaranteeFeeRate(new BigDecimal(0));
			directProjectBiz.setRiskFeeRate(new BigDecimal(0));
			directProjectBiz.setManageFeeRate(new BigDecimal(2));
			directProjectBiz.setLateFeeRate(new BigDecimal(1));
			SysDict sysDict=sysDictService.findByGroupNameAndKey("channel_key","jimistore");
			directProjectBiz.setChannelBusiness(sysDict.getValue());
			directProjectBiz.setOpenPlatformKey("jimistore");
			directProjectBiz.setSecurityType("credit");
		} catch (Exception e) {
			logger.info("对外创建项目projectopen数据解析出错",directProjectBiz);
			return directProjectBiz;
		}
		return directProjectBiz;
	}

	/**
	 * @Description:恢复项目状态到募集中
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 * @author: fuyili
	 * @time:2016年1月7日 下午8:38:37
	 */
	@RequestMapping(value = "start")
	@RequiresPermissions("directProject:start")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "恢复项目状态到募集中")
	public Object startProject(@RequestParam("id") Long id, @RequestParam("msg") String msg, HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<DirectProjectBiz> result = new ResultDO<DirectProjectBiz>();
		SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
		int actionFlag = directProjectService.startProject(id, sysUser.getId(), msg);
		if (actionFlag <= 0) {
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * @Description:暂停项目
	 * @param id
	 * @param msg
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: fuyili
	 * @time:2016年1月20日 下午3:46:09
	 */
	@RequestMapping(value = "stop")
	@RequiresPermissions("directProject:stop")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "暂停项目")
	public Object stopProject(@RequestParam("id") Long id, @RequestParam("msg") String msg, HttpServletResponse resp)
			throws ServletRequestBindingException {
		ResultDO<DirectProjectBiz> result = new ResultDO<DirectProjectBiz>();
		SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
		int actionFlag = directProjectService.stopProject(id, sysUser.getId(), msg);
		if (actionFlag <= 0) {
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * @Description:上线审核
	 * @param id
	 * @param msg
	 * @param radioStatus
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: fuyili
	 * @time:2016年1月20日 下午8:03:28
	 */
	@RequestMapping(value = "review")
	@RequiresPermissions("directProject:review")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "上线审核")
	public Object reviewProject(@RequestParam("id") Long id, @RequestParam("message") String msg,
			@RequestParam("radioStatus") Integer radioStatus, HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		ResultDO<Project> result = new ResultDO<Project>();
		try {
			SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
			if (radioStatus > 0) {
				result = directProjectService.reviewProject(id, sysUser.getId(), msg);
			} else {
				result = directProjectService.fallbackSaveStatus(id, sysUser.getId(), msg);
			}
		} catch (Exception e) {
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 提交待审
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "waitReview")
	@RequiresPermissions("directProject:waitReview")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "提交待审")
	public ResultDO<Project> waitReview(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		ResultDO<Project> result = new ResultDO<Project>();
		SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
		int actionFlag = directProjectService.waitReviewProject(id, sysUser.getId());
		if (actionFlag <= 0) {
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * @Description:风控审核
	 * @param id
	 * @param msg
	 * @param radioStatus
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: fuyili
	 * @time:2016年1月26日 上午9:42:06
	 */
	@RequestMapping(value = "riskReview")
	@RequiresPermissions("directProject:riskReview")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "风控审核")
	public ResultDO<Project> riskReviewProject(@RequestParam("id") Long id, @RequestParam("msg") String msg,
			@RequestParam("radioStatus") Integer radioStatus, HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		ResultDO<Project> result = new ResultDO<Project>();
		try {
			SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
			if (radioStatus > 0) {
				result = directProjectService.riskReviewProjectSuccess(id, sysUser.getId(), msg);
			} else {
				result = directProjectService.riskReviewProjectFail(id, sysUser.getId(), msg);
			}
		} catch (Exception e) {
			logger.error("风控审核出错，id={}，msg={}", id, msg, e);
		}
		return result;

	}

	/**
	 * @Description:删除项目（逻辑）
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: fuyili
	 * @time:2016年1月25日 下午1:29:35
	 */
	@RequestMapping(value = "delete")
	@RequiresPermissions("directProject:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "删除项目")
	public ResultDO<Object> deleteProject(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			int delFlag = directProjectService.deleteProjectById(id);
			if (delFlag <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			logger.error("删除直投项目失败，id={}，", id, e);
		}
		return result;
	}

	/**
	 * 修改上线时间&销售截止时间
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "updateOnlineTimeAndEndDate")
	@RequiresPermissions("directProject:updateOnlineTimeAndEndDate")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "修改上线时间&销售截止时间")
	public ResultDO<Object> updateOnlineTimeAndSaleEndTime(@RequestParam("id") Long id, @RequestParam("onlineTime") String strOnlineTime,
			@RequestParam("saleEndTime") String strSaleEndTime, HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		ResultDO<Object> result = new ResultDO<Object>();
		Date onlineTime = DateUtils.getDateFromString(strOnlineTime, "yyyy-MM-dd HH:mm");
		Date saleEndTime = DateUtils.getDateFromString(strSaleEndTime, "yyyy-MM-dd HH:mm");
		result = directProjectService.updateOnlineTimeAndEndDate(onlineTime, saleEndTime, id);
		return result;
	}

	/**
	 * 修改项目到期时间
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "updateEndDate")
	@RequiresPermissions("directProject:updateEndDate")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "修改销售截止时间")
	public ResultDO<Object> updateEndDate(@RequestParam("id") Long id, @RequestParam("saleEndTime") String strSaleEndTime,
			HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		ResultDO<Object> result = new ResultDO<Object>();
		Date saleEndTime = DateUtils.getDateFromString(strSaleEndTime, "yyyy-MM-dd HH:mm");
		result = directProjectService.updateEndDate(saleEndTime, id);
		return result;
	}
	
	
	/**
	 * 流标
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "lose")
	@RequiresPermissions("directProject:lose")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "流标")
	public ResultDO<Project> lose(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		ResultDO<Project> result = new ResultDO<Project>();
		result = directProjectService.loseProject(id);
		return result;
	}
	
	/**
	 * 流标项目创建退款
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "lose/project/create/refund")
	@RequiresPermissions("directProject:loseProjectCreateRefund")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "流标项目创建退款")
	public boolean loseProjectCreateRefund(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		projectService.loseProject(id);
		return true;
	}
	
	@RequestMapping(value = "delProjectRedis")
	@RequiresPermissions("directProject:delProjectRedis")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "删除项目缓存")
	public Object delProjectRedis(HttpServletRequest req, HttpServletRequest resp, @ModelAttribute("projectId") Long projectId) {
		ResultDO<String> resultDO = new ResultDO<String>();
		RedisForProjectClient.clearTransactionDetail(projectId);
		resultDO.setSuccess(true);
		return resultDO;
	}

	@RequestMapping(value = "addRemark")
	@RequiresPermissions("directProject:addRemark")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "直投项目模块", desc = "添加备注")
	public Object addRemark(HttpServletRequest req, HttpServletRequest resp, @RequestParam("id") Long id,String remarks) throws Exception {
		Long pid = ServletRequestUtils.getLongParameter(req, "id");
		return projectService.addControlRemarks(pid,remarks);
	}
	
	/**
	 * 根据项目id发起保证金归还【归还保证金】
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/create/collectTrade/guaranteeFee")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块", desc = "根据项目id发起保证金归还")
	@RequiresPermissions("directProject:createCollectTradeGuaranteeFee")
	public ResultDO<?> createCollectTradeForGuaranteeFee(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("projectId") Long projectId) {
		ResultDO<?> resultDO = new ResultDO();
		resultDO = projectService.createCollectTradeForGuaranteeFee(projectId);
		return resultDO;
	}
	
	/**
	 * 模糊查询会员信息列表
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "searchMemberByMobile")
	@RequiresPermissions("directProject:searchMemberByMobile")
	@ResponseBody
	public Object searchMemberList(HttpServletRequest req, HttpServletResponse resp) {
		String mobile = req.getParameter("mobile");
		List<Member> members = memberService.selectListByMobile(Long.valueOf(mobile));// 1：表示个人2：表示企业
		List<Member> list=Lists.newArrayList();
		String temp="";
		for(Member bean:members){
			temp=bean.getTrueName()+bean.getMobile();
			if(temp.contains(mobile)){
				list.add(bean);
			}
		}
		return list;
	}

	/**
	 * 根据本地代收号发起代付【归还保证金】
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/create/payTrade/guaranteeFee")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "项目模块", desc = "根据本地代收号发起归还保证金代付")
	@RequiresPermissions("directProject:createPayTradeByCollectTradeNoForGuaranteeFee")
	public ResultDO<?> createPayTradeByCollectTradeNoForGuaranteeFee(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("tradeNo") String tradeNo) {
		ResultDO<HostingPayTrade> resultDO = new ResultDO<HostingPayTrade>();
		resultDO = projectService.createPayTradeByCollectTradeNoForGuaranteeFee(tradeNo);
		return resultDO;
	}

	@RequestMapping(value = "/findTransferRate")
	@ResponseBody
	public Object findTransferRate(){
		ResultDO<List<TransferRateList>> resultDO = new ResultDO<List<TransferRateList>>();
		try {
			SysDict sysDict= sysDictService.findByGroupNameAndKey("transferRate_group","Rate");
			List<TransferRateList> rateBizs=JSON.parseObject(sysDict.getValue(),new TypeReference<ArrayList<TransferRateList>>(){}) ;
			resultDO.setResult(rateBizs);
			resultDO.setSuccess(true);
		} catch (Exception e) {
			resultDO.setSuccess(false);
		}
		return resultDO;
	}

	@RequestMapping(value = "/transferindex")
	@RequiresPermissions("directProject:transferIndex")
	public String transferIndex(){
		return "/ic/transferProject/index";
	}

	@RequestMapping(value = "transferajax")
	@RequiresPermissions("directProject:transferProjectAjax")
	@ResponseBody
	public Object transferAjax(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		Page<TransferProjectPageBiz> pageRequest = new Page<TransferProjectPageBiz>();
		String id=req.getParameter("id");
		String transferName=req.getParameter("transferName");
		String transferMember=req.getParameter("transferMember");
		String status=req.getParameter("status");
		String projectName=req.getParameter("projectName");
		String borrowName=req.getParameter("borrowName");
		String pagesize= req.getParameter("iDisplayLength");
		String start= req.getParameter("iDisplayStart");
		TransferProjectPageQuery query=new TransferProjectPageQuery();
		if (!StringUtils.isEmpty(id)){
			try {
				query.setId(Long.parseLong(id));
			} catch (NumberFormatException e) {
				return pageRequest;
			}
		}
		String transferEndDateStart=req.getParameter("transferEndDateStart");
        if (!StringUtils.isEmpty(transferEndDateStart)){
            try {
                Date startDate=dateFormat.parse(transferEndDateStart);
                query.setTransferEndDateStart(startDate);
            } catch (ParseException e) {
            	query.setTransferEndDateStart(null);
            }
        }
    	String transferEndDateEnd=req.getParameter("transferEndDateEnd");
        if (!StringUtils.isEmpty(transferEndDateEnd)){
            try {
                Date endDate=dateFormat.parse(transferEndDateEnd);
                query.setTransferEndDateEnd(endDate);
            } catch (ParseException e) {
            	query.setTransferEndDateEnd(null);
            }
        }
		query.setProjectName(projectName);
		query.setTransferName(transferName);
		query.setTransferMember(transferMember);
		if (!StringUtils.isEmpty(status)){
			if (status.equals("-1")){
				query.setStatus(Arrays.asList(52,70,90));
			}
			if (status.equals("1")){
				query.setStatus(Arrays.asList(30));
			}
		}
		query.setBorrowName(borrowName);
		query.setPageSize(Integer.parseInt(pagesize));
		query.setStartRow(Integer.parseInt(start));
		return directProjectService.queryPageTransferProjectPageBiz(query);
	}
}
