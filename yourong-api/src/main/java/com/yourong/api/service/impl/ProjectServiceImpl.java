package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.api.dto.BannerDto;
import com.yourong.api.dto.DebtDto;
import com.yourong.api.dto.DebtInterestForAppDto;
import com.yourong.api.dto.DynamicMenuDto;
import com.yourong.api.dto.EnterpriseProjectInfoDto;
import com.yourong.api.dto.IconDto;
import com.yourong.api.dto.IndexDataDto;
import com.yourong.api.dto.IndexDto;
import com.yourong.api.dto.IndexProjectDto;
import com.yourong.api.dto.OrderCouponDto;
import com.yourong.api.dto.ProjectDetailDto;
import com.yourong.api.dto.ProjectForDirectReward;
import com.yourong.api.dto.ProjectInfoDto;
import com.yourong.api.dto.ProjectInterestDto;
import com.yourong.api.dto.ProjectInterestInfoDto;
import com.yourong.api.dto.ProjectListDto;
import com.yourong.api.dto.ProjectPackageListDto;
import com.yourong.api.dto.ProjectPageDto;
import com.yourong.api.dto.RecommendProjectDto;
import com.yourong.api.dto.RepaymentPlanDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.ZhongNiuProject;
import com.yourong.api.dto.ZhongNiuProjectDetail;
import com.yourong.api.service.ArticleService;
import com.yourong.api.service.BannerService;
import com.yourong.api.service.ProjectService;
import com.yourong.api.utils.ServletUtil;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.AttachmentEnum;
import com.yourong.common.enums.CouponEnum;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.member.common.Constants;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.MoneyUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.cms.manager.CmsIconManager;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.CmsIcon;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.OverdueRepayLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.ic.dao.ProjectPackageLinkMapper;
import com.yourong.core.ic.dao.ProjectPackageMapper;
import com.yourong.core.ic.manager.DebtCollateralManager;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectInterestManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.ProjectNoticeManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.manager.BorrowerCreditGradeManager;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.ProjectInterest;
import com.yourong.core.ic.model.ProjectNotice;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageBiz;
import com.yourong.core.ic.model.ProjectPackageLinkModel;
import com.yourong.core.ic.model.ProjectPackageModel;
import com.yourong.core.ic.model.QuickRewardConfig;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.BorrowerCreditGrade;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.biz.ProjectForLevel;
import com.yourong.core.ic.model.biz.ProjectForRewardMember;
import com.yourong.core.ic.model.biz.ProjectInvestingDto;
import com.yourong.core.ic.model.biz.ProjectNoticeForFront;
import com.yourong.core.ic.model.biz.TransferProjectBiz;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.core.ic.model.query.TransferProjectQuery;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.biz.ActivityForSixBillion;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberCheckManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;

@Service
public class ProjectServiceImpl implements ProjectService {
	private static Logger logger = LoggerFactory
			.getLogger(ProjectServiceImpl.class);

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private DebtManager debtManager;

	@Autowired
	private BscAttachmentManager bscAttachmentManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	ProjectInterestManager projectInterestManager;

	@Autowired
	private ProjectNoticeManager projectNoticeManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private BannerService bannerService;
	
	@Autowired
	private CmsIconManager cmsIconManager;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private ProjectExtraManager projectExtraManager;

	@Autowired
	private DebtInterestManager debtInterestManager;

	@Autowired
	private DebtCollateralManager debtCollateralManager;

	@Autowired
	private EnterpriseManager enterpriseManager;
	@Autowired
	private OverdueRepayLogManager overdueRepayLogManager;

	@Autowired
	private MemberCheckManager memberCheckManager;

	@Autowired
	private OrderManager orderManager;

	@Autowired
	private ArticleService articleService;
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private TransferProjectManager transferProjectManager;

	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

	@Autowired
	private TransactionInterestManager transactionInterestManager;

	@Autowired
	private ActivityLotteryManager activityLotteryManager;
	
	@Autowired
	private ProjectPackageLinkMapper projectPackageLinkMapper;
	
	@Autowired
	private BorrowerCreditGradeManager borrowerCreditGradeManager;
	
	@Autowired
	private ProjectPackageMapper projectPackageMapper;
	
	@Autowired
	private SysDictManager sysDictManager;
	public ProjectPageDto<ProjectListDto> queryPagingProject(
			ProjectQuery projectQuery) {
		ProjectPageDto<ProjectListDto> pageList = null;
		try {
						
			Page<ProjectForFront> data = projectManager
					.findFrontProjectListExtraByPage(projectQuery);
			if (data != null) {
				pageList = new ProjectPageDto<ProjectListDto>();
				List<ProjectListDto> dto = BeanCopyUtil.mapList(data.getData(),
						ProjectListDto.class);

				for (ProjectListDto projectListDto : dto) {
					if (StatusEnum.PROJECT_STATUS_INVESTING.getStatus() == projectListDto.getStatus()
							&& BigDecimal.ZERO.compareTo(projectListDto
									.getAvailableBalance()) == 0) {
						int orders = orderManager
								.getOrderCountByProject(projectListDto.getId());
						projectListDto.setOrders(orders);
						BigDecimal orderAmount = BigDecimal.ZERO;
						orderAmount = orderManager
								.getPayingAmountByProject(projectListDto
										.getId());
						projectListDto.setOrderAmount(orderAmount);
					}
					this.getExtraProject(projectListDto);
				}
				pageList.setData(dto);
				pageList.setiDisplayLength(data.getiDisplayLength());
				pageList.setiDisplayStart(data.getiDisplayStart());
				pageList.setiTotalRecords(data.getiTotalRecords());
				pageList.setPageNo(data.getPageNo());
				pageList.setStatusCode(projectQuery.getStatusCode());
			}
		} catch (ManagerException e) {
			logger.error("获取项目列表异常", e);
		}
		return pageList;
	}

	private void getExtraProject(ProjectListDto projectListDto) throws ManagerException{
		
		projectListDto.setQuickRewardFlag(0);
		ProjectExtra pro = projectExtraManager.getProjectQucikReward(projectListDto.getId());
		if(pro.getActivitySign()!=null&&pro.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
			projectListDto.setQuickRewardFlag(1);
		}
		ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(projectListDto.getId());
		int extraType = 0;//特殊业务类型: 1-活动 2-项目加息
		if(projectExtraAddRate!=null){
			extraType=projectExtraAddRate.getExtraType();
			projectListDto.setAddRate(projectExtraAddRate.getExtraAmount().toString());
		}
		projectListDto.setExtraType(extraType);
	}
	
	public ProjectPageDto<ProjectListDto> p2pQueryPagingProject(
			ProjectQuery projectQuery) {
		ProjectPageDto<ProjectListDto> pageList = null;
		try {
			Page<ProjectForFront> data = projectManager
					.p2pFindFrontProjectListByPage(projectQuery);
			if (data != null) {
				pageList = new ProjectPageDto<ProjectListDto>();
				List<ProjectListDto> dto = BeanCopyUtil.mapList(data.getData(),
						ProjectListDto.class);
				pageList.setData(dto);
				pageList.setiDisplayLength(data.getiDisplayLength());
				pageList.setiDisplayStart(data.getiDisplayStart());
				pageList.setiTotalRecords(data.getiTotalRecords());
				pageList.setPageNo(data.getPageNo());
				pageList.setStatusCode(projectQuery.getStatusCode());
			}
		} catch (ManagerException e) {
			logger.error("过滤p2p获取项目列表异常", e);
		}
		return pageList;
	}
	
	@Override
	public ResultDTO<ProjectInfoDto> getProjectInfoById(Long id) {
		ResultDTO<ProjectInfoDto> resultDTO = new ResultDTO<ProjectInfoDto>();
		ProjectInfoDto projectInfoDto = new ProjectInfoDto();
		List<DynamicMenuDto> inforMations = Lists.newArrayList();
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			TransferProject transferProject = transferProjectManager
					.selectByPrimaryKey(id);
			if (project != null
					&& project.getDelFlag() > 0
					&& project.getStatus() >= StatusEnum.PROJECT_STATUS_WAIT_RELEASE
							.getStatus()) {
				
				ProjectNotice projectNotice = null;
				if (project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE
						.getStatus()) {
					projectNotice = projectNoticeManager
							.getProjectNoticingByProjectId(project.getId());
					if (projectNotice == null) {
						resultDTO.setResultCode(ResultCode.ERROR);
						return resultDTO;
					}
				}
				projectInfoDto = this.getNormalProjectInfoById(project, projectInfoDto);
				this.projectHandle(project, projectInfoDto);
				this.getExtraProjectInfor(project, projectInfoDto);
				resultDTO.setResult(projectInfoDto);
			} else if (transferProject != null) {
				projectInfoDto = new ProjectInfoDto();
				projectInfoDto = this.getTransferProjectInfoById(transferProject, projectInfoDto);
				this.transferProjectHandle(transferProject, projectInfoDto);
				
				resultDTO.setResult(projectInfoDto);

			} else {
				resultDTO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
			}
		} catch (Exception e) {
			logger.error("查看项目" + id + "异常", e);
			resultDTO.setResultCode(ResultCode.ERROR);
		}
		return resultDTO;
	}
	
	private void getExtraProjectInfor(Project project,ProjectInfoDto projectInfoDto){
		
		Integer hour = Integer.valueOf(projectExtraManager.getRewardHourByProjectId(project.getId()));
		
		projectInfoDto.setQuickRewardDate(DateUtils.addHour(projectInfoDto.getOnlineTime(),
				hour));
		
	}
	
	private void projectHandle(Project project,ProjectInfoDto projectInfoDto) throws Exception{
		List<DynamicMenuDto> inforMations = Lists.newArrayList();
		if(project.isDirectProject()){//TODO zhang-hao
			String[] titles = {"项目信息","信用认证","投资记录","还款计划"};
			String[] urlEles = {"/project/p2p-detail-"+project.getId()+".html?isNeedYRWtoken=Y",
					"/project/p2p-contract-"+project.getId()+".html?isNeedYRWtoken=Y","/project/record-"+project.getId()+".html",
					"/project/p2p-repaymentPlan-"+project.getId()+".html"};//接口路由地址
			int[] corners = {0,0,projectInfoDto.getTotalInvestment(),0};
			for(int i=0;i<4;i++){
				String t = titles[i];
				String url =urlEles[i];
				int corner = corners[i];
				DynamicMenuDto d = new DynamicMenuDto();
				d.setTitle(t);
				d.setUrl(url);
				d.setCorner(corner);
				inforMations.add(d);
			}
			
			projectInfoDto.setMinAnnualizedRate(project
					.getAnnualizedRate());
			projectInfoDto.setMaxAnnualizedRate(project
					.getAnnualizedRate());
			
		}else{
			String[] titles = {"项目信息","项目图片","投资记录"};
			String[] urlEles = {"/project/detail-"+project.getId()+".html",
					"/project/contract-"+project.getId()+".html","/project/record-"+project.getId()+".html"};//接口路由地址
			int[] corners = {0,0,projectInfoDto.getTotalInvestment()};
			for(int i=0;i<3;i++){
				String t = titles[i];
				String url =urlEles[i];
				int corner = corners[i];
				DynamicMenuDto d = new DynamicMenuDto();
				d.setTitle(t);
				d.setUrl(url);
				d.setCorner(corner);
				inforMations.add(d);
			}
		}
		
		projectInfoDto.setInforMations(inforMations);
		// 履约及以后的状态显示统计数据
		if (project.getStatus() > StatusEnum.PROJECT_STATUS_STOP
				.getStatus()) {
			projectInfoDto
					.setTransactionMemberCount(transactionManager
							.getTransactionMemberCountByProject(project
									.getId()));
			projectInfoDto
					.setTotalTransactionInterest(transactionManager
							.getTotalTransactionInterestByProject(project
									.getId()));
		}
		// 已还款后，显示实际收益
		if (project.getStatus() == StatusEnum.PROJECT_STATUS_REPAYMENT
				.getStatus()) {
			projectInfoDto
					.setTotalTransactionInterest(transactionManager
							.getTotalTransactionReceivedInterestByProject(project
									.getId()));
		}


		if (StatusEnum.PROJECT_STATUS_INVESTING.getStatus() == projectInfoDto
				.getStatus()
				&& BigDecimal.ZERO.compareTo(projectInfoDto
						.getAvailableBalance()) == 0) {
			int orders = orderManager.getOrderCountByProject(project.getId());
			projectInfoDto.setOrders(orders);
			BigDecimal orderAmount = BigDecimal.ZERO;
			orderAmount = orderManager.getPayingAmountByProject(project.getId());
			projectInfoDto.setOrderAmount(orderAmount);
		}
		projectInfoDto
				.setProjectCategory(TypeEnum.PROJECT_CATEGORY_NORMAL
						.getType());
		projectInfoDto.setQuickRewardFlag(0);
		ProjectExtra projectExtra = projectExtraManager.getProjectQucikReward(project.getId());
		if(projectExtra.getActivitySign()!=null&&projectExtra.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
			projectInfoDto.setQuickRewardFlag(1);
		}
		
		ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(project.getId());
		int extraType = 0;//特殊业务类型: 1-活动 2-项目加息
		if(projectExtraAddRate!=null){
			extraType=projectExtraAddRate.getExtraType();
			projectInfoDto.setAddRate(projectExtraAddRate.getExtraAmount().toString());
		}
		projectInfoDto.setExtraType(extraType);
		
		projectInfoDto.setCurrentDate(DateUtils.getCurrentDate());
	}
	
	
	private void transferProjectHandle(TransferProject transferProject,ProjectInfoDto projectInfoDto) throws Exception{
		List<DynamicMenuDto> inforMations = Lists.newArrayList();
		String[] titles = {"项目信息","认购记录","还款计划"};
		String[] urlEles = {"/project/transfer-detail-"+transferProject.getId()+".html",
				"/project/transfer-record-"+transferProject.getId()+".html","/project/transfer-repaymentPlan-"+transferProject.getId()+".html"};//接口路由地址
		int[] corners = {0,projectInfoDto.getTotalInvestment(),0};
		for(int i=0;i<3;i++){
			String t = titles[i];
			String url =urlEles[i];
			int corner = corners[i];
			DynamicMenuDto d = new DynamicMenuDto();
			d.setTitle(t);
			d.setUrl(url);
			d.setCorner(corner);
			inforMations.add(d);
		}
		projectInfoDto.setInforMations(inforMations);

	}
	
	//普通项目
	private ProjectInfoDto getNormalProjectInfoById (Project project,ProjectInfoDto projectInfoDto) throws Exception{
		
		projectInfoDto = BeanCopyUtil.map(project, ProjectInfoDto.class);
		BigDecimal balance = getProjectBalanceById(project.getId());
		String progress = getProjectNumberProgress(project.getTotalAmount(), balance);
		if(project.getStatus() >= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
			int totalInvestment = transactionManager.getTransactionCountByProject(project.getId());
			projectInfoDto.setTotalInvestment(totalInvestment);
			projectInfoDto.setAvailableBalance(balance);
			projectInfoDto.setInvestmentProgress(progress);
		}else{//如果还处未投资状态，直接就是总金额
			projectInfoDto.setAvailableBalance(balance);
			projectInfoDto.setInvestmentProgress(progress);
		}
		//履约及以后的状态显示统计数据
		if(project.getStatus() > StatusEnum.PROJECT_STATUS_STOP.getStatus()){
			projectInfoDto.setTransactionMemberCount(transactionManager.getTransactionMemberCountByProject(project.getId()));
			projectInfoDto.setTotalTransactionInterest(transactionManager.getTotalTransactionInterestByProject(project.getId()));
		}
		//已还款后，显示实际收益
		if(project.getStatus() == StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()){
			projectInfoDto.setTotalTransactionInterest(transactionManager.getTotalTransactionReceivedInterestByProject(project.getId()));
		}
		
		if(project.isDirectProject()){ 
			projectInfoDto.setMinAnnualizedRate(project.getAnnualizedRate());
			projectInfoDto.setMaxAnnualizedRate(project.getAnnualizedRate());
		}
							
		if(StatusEnum.PROJECT_STATUS_INVESTING.getStatus()==projectInfoDto.getStatus()
				&&BigDecimal.ZERO.compareTo(projectInfoDto.getAvailableBalance())==0){
			int orders =  orderManager.getOrderCountByProject(project.getId());
			projectInfoDto.setOrders(orders);
			BigDecimal orderAmount =BigDecimal.ZERO;
			orderAmount = orderManager.getPayingAmountByProject(project.getId());
			projectInfoDto.setOrderAmount(orderAmount);
		}
		projectInfoDto.setProjectCategory(TypeEnum.PROJECT_CATEGORY_NORMAL.getType());
		projectInfoDto.setCurrentDate(DateUtils.getCurrentDate());
		return projectInfoDto;
		
	}
	
	//转让项目
	private ProjectInfoDto getTransferProjectInfoById (TransferProject transferProject,ProjectInfoDto projectInfoDto) throws Exception{
		
		projectInfoDto.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
		projectInfoDto.setName(transferProject.getTransferName());
		projectInfoDto.setProfitType(transferProject.getProfitType());
		projectInfoDto.setDiscount(transferProject.getDiscount());
		projectInfoDto.setMaxAnnualizedRate(transferProject.getTransferAnnualizedRate());
		projectInfoDto.setMinAnnualizedRate(transferProject.getTransferAnnualizedRate());
		//pLD.setEarningsDays(transferProject.getDays());//earnings计算得到
		projectInfoDto.setBorrowPeriod(transferProjectManager.getReturnDay(transferProject.getTransactionId()));
		projectInfoDto.setBorrowPeriodType(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType());
		projectInfoDto.setStatus(transferProject.getStatus());
		
		BigDecimal balance = transferProjectManager.getTransferProjectBalanceById(transferProject.getId());
		projectInfoDto.setAvailableBalance(balance);
		projectInfoDto.setTotalAmount(transferProject.getTransactionAmount());
		projectInfoDto.setStartDate(transferProject.getTransferStartDate());
		projectInfoDto.setEndDate(transferProject.getTransferEndDate());
		projectInfoDto.setSaleEndTime(transferProject.getTransferEndDate());	
		projectInfoDto.setInterestFrom(transferProject.getInterestFrom());
		projectInfoDto.setInvestType(2);
		projectInfoDto.setId(transferProject.getProjectId());
		projectInfoDto.setTransferId(transferProject.getId());
		
		if(transferProject.getStatus() >= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
			int totalInvestment = transactionManager.getTransactionCountByTransferProject(transferProject.getId());
			projectInfoDto.setTotalInvestment(totalInvestment);
			String progress = getProjectNumberProgress(transferProject.getTransactionAmount(), balance);
			projectInfoDto.setInvestmentProgress(progress);
		}
		//履约及以后的状态显示统计数据
		if(transferProject.getStatus() > StatusEnum.PROJECT_STATUS_STOP.getStatus()){
			BigDecimal totalIncome = BigDecimal.ZERO;
			List<Transaction> tranList= transactionManager.selectByTransferId(transferProject.getId());
			for(Transaction tran :tranList){
				totalIncome = totalIncome.add(tran.getTotalPrincipal().add(tran.getTotalInterest()).subtract(tran.getInvestAmount()));
			}
			totalIncome = (totalIncome!=null?totalIncome:BigDecimal.ZERO);
			projectInfoDto.setTransactionMemberCount(transactionManager.getTransactionMemberCountByTransferId(transferProject.getId()));
			projectInfoDto.setTotalTransactionInterest(totalIncome);
		}
		//已还款后，显示实际收益
		if(transferProject.getStatus() == StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()){
			BigDecimal totalIncome = BigDecimal.ZERO;
			List<Transaction> tranList= transactionManager.selectByTransferId(transferProject.getId());
			for(Transaction tran :tranList){
				totalIncome = totalIncome.add(tran.getReceivedPrincipal().add(tran.getReceivedInterest()).subtract(tran.getInvestAmount()));
			}
			totalIncome = (totalIncome!=null?totalIncome:BigDecimal.ZERO);
			projectInfoDto.setTotalTransactionInterest(totalIncome);
		}
		
		if(StatusEnum.PROJECT_STATUS_INVESTING.getStatus()==transferProject.getStatus()
				&&BigDecimal.ZERO.compareTo(balance)==0){
			int orders =  orderManager.getTransferOrderCountByProject(transferProject.getId());
			projectInfoDto.setOrders(orders);
			BigDecimal orderAmount =BigDecimal.ZERO;
			orderAmount = orderManager.getTransferPayingAmountByProject(transferProject.getId());
			projectInfoDto.setOrderAmount(orderAmount);
		}
		
		projectInfoDto.setCurrentDate(DateUtils.getCurrentDate());
		return projectInfoDto;
	}

	@Override
	public String getProjectImagePath(Long id) {
		try {
			BscAttachment attachment = bscAttachmentManager
					.getBscAttachmentByKeyIdAndModule(id.toString(),
							"debt_collateral");
			if (attachment != null) {
				return attachment.getFileUrl();
			}
		} catch (ManagerException e) {
			logger.error("项目" + id + "未配置图片", e);
		}
		return "";
	}

	@Override
	public BigDecimal getProjectBalanceById(Long id) {
		// 可用余额
		BigDecimal availableBalance = null;
		try {
			// 从缓存中找可用余额
			// availableBalance = RedisProjectClient.getProjectBalance(id);
			if (availableBalance == null) {
				// logger.info("项目"+id+"，可用余额在redis未找到。");
				// 如果为Null，到余额表找
				Balance _balance = balanceManager.queryBalance(id,
						TypeEnum.BALANCE_TYPE_PROJECT);
				if (_balance != null) {
					availableBalance = _balance.getAvailableBalance();
				} else {
					logger.debug("项目" + id + "，可用余额在余额表未找到。");
				}
			}
			if (availableBalance == null) {
				// 再没有，那只能从项目中去找了
				Project project = projectManager.selectByPrimaryKey(id);
				availableBalance = project.getTotalAmount();
			}
			logger.debug("项目" + id + "，可用余额" + availableBalance);
		} catch (ManagerException e) {
			logger.error("项目" + id + "查找", e);
		}
		return availableBalance;
	}

	/**
	 * 附件分类
	 * 
	 * @param bscAttachments
	 * @param projectInfoDto
	 */
	private void assignationAttachment(List<BscAttachment> bscAttachments,
			ProjectInfoDto projectInfoDto) {
		// List<BscAttachment> collateralAttachments =
		// projectInfoDto.getCollateralAttachments();
		// List<BscAttachment> lenderMemberAttachments =
		// projectInfoDto.getLenderMemberAttachments();
		// List<BscAttachment> borrowMemberAttachments =
		// projectInfoDto.getBorrowMemberAttachments();
		// List<BscAttachment> contractAttachments =
		// projectInfoDto.getContractAttachments();
		// List<String> contractCategoryName =
		// projectInfoDto.getContractCategoryName();
		// for(BscAttachment attachment : bscAttachments){
		// String module = attachment.getModule();
		// if(module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_COLLATERAL.getCode())){//质押或抵押物附件
		// collateralAttachments.add(attachment);
		// }else
		// if(module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_LENDER.getCode())){//原始债权人附件
		// lenderMemberAttachments.add(attachment);
		// }else
		// if(module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_BORROWER.getCode())){//借款人附件
		// borrowMemberAttachments.add(attachment);
		// }else
		// if(module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_CONTRACT.getCode())
		// //合同图片
		// ||
		// module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_LEGAL.getCode())//法律意见书
		// ||
		// module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_BASE.getCode())){//其他资料图片
		// String categoryName = DebtEnum.getEnumByCode(module).getDesc();
		// attachment.setModule(categoryName);
		// contractAttachments.add(attachment);
		// if(!contractCategoryName.contains(categoryName)){
		// contractCategoryName.add(categoryName);
		// }
		// }
		// }
	}

	@Override
	public ResultDO<ProjectForFront> findIndexProjectList() {
		return findProjectList(Constant.INDEX_DEFAULT_NUMBER, null);
	}

	@Override
	public ResultDO<ProjectForFront> findLandProjectList() {
		return findProjectList(4, null);
	}

	private ResultDO<ProjectForFront> findProjectList(Integer number,
			Integer investType) {
		ResultDO<ProjectForFront> result = new ResultDO<ProjectForFront>();
		List<ProjectForFront> projectForFrontList = Lists.newArrayList();
		try {
			// 首位，预告
			ProjectNoticeForFront projectNoticeForFront = projectNoticeManager
					.getProjectNoticeByIndexShow();
			if (projectNoticeForFront != null) {
				ProjectForFront project = projectManager
						.getProjectForFrontByProjectId(projectNoticeForFront
								.getProjectId());
				if (project != null
						&& project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE
								.getStatus()) {
					projectForFrontList.add(project);
				}
			}

			// 推荐位优先
			List<ProjectForFront> recommendProjectForList = projectManager
					.findRecommendProjectList(
							number - projectForFrontList.size(), investType);
			if (Collections3.isNotEmpty(recommendProjectForList)) {
				projectForFrontList.addAll(recommendProjectForList);
			}

			// 常规投资中的项目
			if (projectForFrontList.size() < number) {
				List<ProjectForFront> investingProjectForList = projectManager
						.findIndexInvestingProjectList(number
								- projectForFrontList.size(), investType);
				if (Collections3.isNotEmpty(investingProjectForList)) {
					projectForFrontList.addAll(investingProjectForList);
				}
			}

			// 不足8个项目，再上履约或还款中的项目
			if (projectForFrontList.size() < number) {
				List<ProjectForFront> notInvestingProjectForList = projectManager
						.findIndexNotInvestingProjectList(number
								- projectForFrontList.size(), investType);
				if (Collections3.isNotEmpty(notInvestingProjectForList)) {
					projectForFrontList.addAll(notInvestingProjectForList);
				}
			}
			
			for(ProjectForFront p : projectForFrontList){
				p.setQuickRewardFlag(0);
				ProjectExtra pro = projectExtraManager.getProjectQucikReward(p.getId());
				if(pro.getActivitySign()!=null&&pro.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
					p.setQuickRewardFlag(1);
				}
				ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(p.getId());
				int extraType = 0;//特殊业务类型: 1-活动 2-项目加息
				if(projectExtraAddRate!=null){
					extraType=projectExtraAddRate.getExtraType();
					p.setAddRate(projectExtraAddRate.getExtraAmount().toString());
				}
				p.setExtraType(extraType);
			}
			
			result.setResultList(projectForFrontList);
		} catch (ManagerException e) {
			logger.error("查询项目列表异常", e);
			result.setSuccess(false);
			return result;
		}
		return result;

	}

	@Override
	public ProjectForFront getIndexNoviceProject() {
		try {
			List<ProjectForFront> projectForFrongList = projectManager
					.getNoviceProjectByInvesting(1);
			if (Collections3.isNotEmpty(projectForFrongList)) {
				
				return this.getExtraProject(projectForFrongList.get(0));
			} else {
				projectForFrongList = projectManager
						.getNoviceProjectByNotInvesting(1);
				if (Collections3.isNotEmpty(projectForFrongList)) {
					return this.getExtraProject(projectForFrongList.get(0));
				}
			}
		} catch (ManagerException e) {
			logger.error("获得在首页显示的新手项目异常", e);
		}
		return null;
	}
	
	private ProjectForFront getExtraProject(ProjectForFront p) throws ManagerException{
		
		p.setQuickRewardFlag(0);
		ProjectExtra pro = projectExtraManager.getProjectQucikReward(p.getId());
		if(pro.getActivitySign()!=null&&pro.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
			p.setQuickRewardFlag(1);
		}
		ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(p.getId());
		int extraType = 0;//特殊业务类型: 1-活动 2-项目加息
		if(projectExtraAddRate!=null){
			extraType=projectExtraAddRate.getExtraType();
			p.setAddRate(projectExtraAddRate.getExtraAmount().toString());
		}
		p.setExtraType(extraType);
		return p;
	}
	
	

	public JSONObject getProjectListToZhongNiu() {
		JSONObject jo = new JSONObject();
		try {
			List<Project> projectList = projectManager
					.getProjectListToZhongNiu();
			if (Collections3.isEmpty(projectList)) {
				jo.put("status", 1);// 没有数据
				jo.put("msg", "");
			} else {
				List<ZhongNiuProject> znpList = new ArrayList<ZhongNiuProject>();
				for (Project p : projectList) {
					ZhongNiuProject znp = new ZhongNiuProject();
					BigDecimal balance = getProjectBalanceById(p.getId());
					BigDecimal amounted = p.getTotalAmount().subtract(balance);
					znp.setPid(p.getId());
					znp.setStatus(p.getStatus());
					znp.setAmounted(amounted.longValue());
					znp.setProgress(getProjectNumberProgress(
							p.getTotalAmount(), balance));
					znpList.add(znp);
				}
				jo.put("status", 0);// 正常
				jo.put("msg", "");
				jo.put("list", znpList);
			}
		} catch (ManagerException e) {
			jo.put("status", 2);// 错误
			jo.put("msg", "查询项目列表数据出现错误");
			logger.error("众牛查询项目列表数据异常", e);
		}
		return jo;
	}

	/**
	 * 项目进度
	 * 
	 * @param totalAmount
	 * @param availableBalance
	 * @return
	 */
	public String getProjectNumberProgress(BigDecimal totalAmount,
			BigDecimal availableBalance) {
		String progress = "0";
		if (availableBalance != null) {
			if (availableBalance.compareTo(BigDecimal.ZERO) <= 0) {
				progress = "100";
			} else if (availableBalance.compareTo(totalAmount) == 0) {
				progress = "0";
			} else {
				progress = new DecimalFormat("###.##").format((totalAmount
						.subtract(availableBalance)).divide(totalAmount, 4,
						RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
			}
		}
		return progress;
	}

	@Override
	public JSONObject getProjectDetailToZhongNiu(Long projectId) {
		JSONObject jo = new JSONObject();
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			if (project != null && project.getDelFlag() >= 0) {
				BigDecimal balance = getProjectBalanceById(project.getId());
				BigDecimal amounted = project.getTotalAmount()
						.subtract(balance);
				ZhongNiuProjectDetail detail = new ZhongNiuProjectDetail();
				detail.setPid(project.getId());
				detail.setName(project.getName());
				detail.setType(7);// 债权标
				detail.setYield(project.getMaxAnnualizedRate().toString());
				detail.setDuration(DateUtils.getIntervalMonth(
						project.getStartDate(), project.getEndDate()));// 投资期限（单位：月），精确到小数点2位
				detail.setRepaytype(1);// 按月付息 到期还本
				detail.setGuaranttype(1);// 本息但保
				detail.setThreshold(project.getMinInvestAmount().longValue());
				detail.setStatus(project.getStatus());
				detail.setAmount(project.getTotalAmount().longValue());
				detail.setAmounted(amounted.longValue());
				detail.setUrl(PropertiesUtil.getWebRootUrl()
						+ "/products/detail-" + project.getId() + ".html");
				detail.setProgress(getProjectNumberProgress(
						project.getTotalAmount(), balance));
				detail.setStartdate(DateUtils.formatDatetoString(
						project.getOnlineTime(), "yyyy-MM-dd"));
				detail.setEnddate(DateUtils.formatDatetoString(
						project.getSaleEndTime(), "yyyy-MM-dd"));
				detail.setPublishtime(DateUtils.formatDatetoString(
						project.getOnlineTime(), "yyyy-MM-dd HH:mm:ss"));

				DebtBiz debt = debtManager.getDebtInfoToZhongNiu(project
						.getDebtId());

				JSONObject xmjs = new JSONObject();
				xmjs.put("title", "项目介绍");
				JSONObject xmjsContent = new JSONObject();
				xmjsContent.put("content", project.getShortDesc());

				JSONObject jbxx = new JSONObject();
				jbxx.put("title",
						detail.getBaseInfoTitel(debt.getGuarantyType()));
				JSONObject jbxxContent = new JSONObject();
				if (debt.getDebtCollateral() != null) {
					jbxxContent.put("content", detail.formatContent(debt
							.getDebtCollateral().getCollateralDetails(), debt
							.getGuarantyType()));
				} else {
					jbxxContent.put("content", detail.formatContent(debt
							.getDebtPledge().getPledgeDetails(), debt
							.getGuarantyType()));
				}

				JSONObject hgly = new JSONObject();
				hgly.put("title", "还款来源");
				JSONObject hglyContent = new JSONObject();
				hglyContent.put("content", debt.getLoanUse());

				JSONArray ja = new JSONArray();
				ja.add(xmjs);
				ja.add(xmjsContent);

				ja.add(jbxx);
				ja.add(jbxxContent);

				ja.add(hgly);
				ja.add(hglyContent);

				List<BscAttachment> bscAttachments = bscAttachmentManager
						.findAttachmentsByKeyIdAndModule(project.getId()
								.toString(),
								DebtEnum.ATTACHMENT_MODULE_DEBT_COLLATERAL
										.getCode(), -1);
				for (BscAttachment att : bscAttachments) {
					JSONObject jimage = new JSONObject();
					jimage.put(
							"image",
							Config.ossPicUrl
									+ StringUtil.getFilePath(att.getFileUrl(),
											"425"));
					ja.add(jimage);
				}
				detail.setDetail(ja);
				jo.put("status", 0);// 正常
				jo.put("msg", "");
				jo.put("data", detail);
			} else {
				jo.put("status", 2);// 没有数据
				jo.put("msg", "未找到匹配的数据");
			}
		} catch (ManagerException e) {
			jo.put("status", 2);// 错误
			jo.put("msg", "查询项目:" + projectId + "数据出现错误");
			logger.error("众牛查询项目:" + projectId + "异常", e);
		}
		return jo;
	}

	@Override
	public ResultDTO getProjectInterestList(Long pid, Long memberId,
			int projectCategory, Long transferId) {
		if (projectCategory == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
			return getProjectInterestListForTransferProject(pid, memberId,
					projectCategory, transferId);
		} else {
			return getProjectInterestListForCommonProject(pid, memberId);
		}
	}

	private ResultDTO getProjectInterestListForCommonProject(Long pid,
			Long memberId) {
		ResultDTO resultDTO = new ResultDTO();
		try {
			Project project = projectManager.selectByPrimaryKey(pid);
			SysDict sysDict = sysDictManager.findByGroupNameAndKey(Constants.PACKAGE_FIVE_AMOUNT_LIMIT, Constants.PACKAGE_FIVE_AMOUNT_LIMIT);
			// 新客校验
			if (project != null && project.isNoviceProject()) {
				boolean checkNoviceFlag = memberManager.needCheckNoviceProject(memberId);
				if (checkNoviceFlag && transactionManager.isMemberInvested(memberId)) {
					resultDTO.setResultCode(ResultCode.PROJECT_ONLY_ALLOWING_NEW_USERS_ERROR);
					return resultDTO;
				}
			}
			if (project != null
					&& project.getDelFlag() > 0
					&& (project.getStatus() == StatusEnum.PROJECT_STATUS_INVESTING
							.getStatus()
							|| project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE
									.getStatus() || project.getStatus() == StatusEnum.PROJECT_STATUS_FULL
							.getStatus())) {
				if (project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE
						.getStatus()) {
					ProjectNotice projectNotice = projectNoticeManager
							.getProjectNoticingByProjectId(project.getId());
					if (projectNotice == null) {
						resultDTO
								.setResultCode(ResultCode.PROJECT_NOT_EXIST_OR_NOT_INVESTING_STATUS__ERROR);
						return resultDTO;
					}
				}
				BigDecimal projectRemainAmount = getProjectBalanceById(pid);
				BigDecimal availableBalance = getProjectBalanceById(project
						.getId());
				if (availableBalance.compareTo(BigDecimal.ZERO) == 0
						|| availableBalance == null
						|| project.getStatus() == StatusEnum.PROJECT_STATUS_FULL
								.getStatus()) {
					resultDTO
							.setResultCode(ResultCode.PROJECT_BALANCE_ZERO_APP_ERROR);
					return resultDTO;
				}
				ProjectInterestDto projectInterestDto = new ProjectInterestDto();
				projectInterestDto
						.setProjectCategory(TypeEnum.PROJECT_CATEGORY_NORMAL
								.getType());
				projectInterestDto.setProjectName(project.getName());
				int days = project.getEarningsDaysByStatus();
				if (project.isDirectProject()) {
					days = this.getDirectProjectEarningDays(
							project.getBorrowPeriod(),
							project.getBorrowPeriodType());
				}
				
				/** 60亿活动，人气值奖励翻六倍 **/
				ActivityForSixBillion sixBillionActivity = this.sixBillionActivityCheck();
				
				String mostInvestPopularity = null;
				String lastInvestPopularity = null;
				String mostAndLastInvestPopularity = null;
				BigDecimal projectAmount = project.getTotalAmount()==null?BigDecimal.ZERO:project.getTotalAmount();
				BigDecimal limtAmount = new BigDecimal(sysDict.getValue());
				if((StatusEnum.IS_PROJECT_PACKAGE_FLAG.getStatus() == project.getPackageFlag().intValue()) && sysDict!=null && (projectAmount.compareTo(limtAmount) <= 0)){
						mostInvestPopularity = BigDecimal.ZERO.toString();
						lastInvestPopularity = BigDecimal.ZERO.toString();
						mostAndLastInvestPopularity = BigDecimal.ZERO.toString();
				}else {
					// 一鸣惊人人气值
					  mostInvestPopularity = transactionManager
								.getPopularityValue(
										RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST
												.getCode(), days,
										Config.mostInvestPopularity);
					// 一锤定音
					lastInvestPopularity = transactionManager
							.getPopularityValue(
									RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST
											.getCode(), days,
									Config.lastInvestPopularity);
					// 一掷千金
					mostAndLastInvestPopularity = transactionManager
							.getPopularityValue(
									RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST
											.getCode(), days,
									Config.mostAndLastInvestPopularity);
					if(sixBillionActivity != null){
						mostInvestPopularity = sixActivityTurn(mostInvestPopularity, sixBillionActivity);
						lastInvestPopularity = sixActivityTurn(lastInvestPopularity, sixBillionActivity);
						mostAndLastInvestPopularity = sixActivityTurn(mostAndLastInvestPopularity, sixBillionActivity);
					}
				}
				if (mostInvestPopularity != null) {
					  projectInterestDto.setMostInvestPopularity(Integer
							.parseInt(mostInvestPopularity));
				} else {
					projectInterestDto.setMostInvestPopularity(0);
				}

				if (lastInvestPopularity != null) {
					projectInterestDto.setLastInvestPopularity(Integer
							.parseInt(lastInvestPopularity));
				} else {
					projectInterestDto.setLastInvestPopularity(0);
				}

				if (mostAndLastInvestPopularity != null) {
					projectInterestDto.setMostAndLastInvestPopularity(Integer
							.parseInt(mostAndLastInvestPopularity));
				} else {
					projectInterestDto.setMostAndLastInvestPopularity(0);
				}
				if (project.isDirectProject()) {
					projectInterestDto.setInvestType(project.getInvestType());
					projectInterestDto.setProjectId(project.getId());
					projectInterestDto.setAvailableBalance(availableBalance);
					projectInterestDto.setAnnualizedRateType(project
							.getAnnualizedRateType());
					projectInterestDto.setIncrementAnnualizedRate(project
							.getIncrementAnnualizedRate());
					projectInterestDto.setEarningsDays(project
							.getEarningsDaysByStatus());
					projectInterestDto.setCurrentDate(DateUtils
							.getCurrentDate());
					projectInterestDto.setOnlineTime(project.getOnlineTime());
					projectInterestDto.setStatus(project.getStatus());
					projectInterestDto.setInterestFrom(project
							.getInterestFrom());
					projectInterestDto.setProfitType(project.getProfitType());
					projectInterestDto.setAnnualizedRateType(1);// 直投配合移动端，处理为一个阶梯收益
					projectInterestDto
							.setEarningPeriod(this.getPeriod(project));
					List<ProjectInterestInfoDto> projectInterestList = new ArrayList<ProjectInterestInfoDto>();
					ProjectInterestInfoDto pi = new ProjectInterestInfoDto();
					pi.setMaxInvest(project.getTotalAmount());
					pi.setMinInvest(project.getMinInvestAmount());
					
					BigDecimal rate = project.getAnnualizedRate();
					
					ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(project.getId());
					if(projectExtraAddRate!=null){
						rate = rate.add(projectExtraAddRate.getExtraAmount());
					}
					pi.setAnnualizedRate(rate);//
					
					projectInterestList.add(pi);
					projectInterestDto
							.setProjectInterestInfoList(projectInterestList);
					projectInterestDto.setMostInvestAmount(getMostInvestAmount(
							project.getTotalAmount(), availableBalance,
							project.getMinInvestAmount(), project.getId()));
					//mostInvestAmount取整
					if(projectInterestDto.getMostInvestAmount() !=null) {
						BigDecimal mostInvestAmoun = projectInterestDto.getMostInvestAmount().setScale(0, BigDecimal.ROUND_HALF_UP);
						projectInterestDto.setMostInvestAmount(mostInvestAmoun);
					}
					// 非受限使用优惠券用户
					if (couponManager.useCouponSpecialLimit(memberId)) {
						List<Coupon> couponList = couponManager
								.getUsableAndLimitedCouponsExceptAmount(
										memberId,
										TypeEnum.COUPON_TYPE_INCOME.getType(),
										CouponEnum.COUPON_CLIENT_APP.getCode(),
										project.countProjectDays(),
										projectRemainAmount);
						List<OrderCouponDto> couponDtoList=BeanCopyUtil.mapList(
								couponList, OrderCouponDto.class);
						if(Collections3.isNotEmpty(couponDtoList)){
							for(OrderCouponDto dto:couponDtoList){
								if(dto.getExtraInterestType()==1&&dto.getExtraInterestDay()>0){
									dto.setExtraName("加息"+dto.getExtraInterestDay()+"天");
								}
							}
						}
						projectInterestDto.setCoupons(couponDtoList);
					}
					ProjectExtra projectExtra = projectExtraManager.getProjectQucikReward(project.getId());
					if(projectExtra.getActivitySign()!=null&&projectExtra.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
						//募集时间超过奖励期限
						int totalHour=DateUtils.getTimeIntervalHours(project.getOnlineTime(),DateUtils.getCurrentDate());
						String rewardHour =projectExtraManager.getRewardHourByProjectId(project.getId());
						if(totalHour<Float.valueOf(rewardHour)){
							//直投快投规则
							List<LotteryRuleAmountNumber> lottery = projectExtraManager.getLotteryByProjectId(project.getId());		
							projectInterestDto.setLottery(lottery);
						}
					}
					
					
					resultDTO.setResult(projectInterestDto);
					return resultDTO;
				}

				// ProjectInterestDto projectInterestDto = new
				// ProjectInterestDto();
				projectInterestDto.setInvestType(project.getInvestType());
				projectInterestDto.setProjectId(project.getId());
				projectInterestDto.setAvailableBalance(availableBalance);
				projectInterestDto.setAnnualizedRateType(project
						.getAnnualizedRateType());
				projectInterestDto.setIncrementAnnualizedRate(project
						.getIncrementAnnualizedRate());
				projectInterestDto.setEarningsDays(project
						.getEarningsDaysByStatus());
				projectInterestDto.setCurrentDate(DateUtils.getCurrentDate());
				projectInterestDto.setOnlineTime(project.getOnlineTime());
				projectInterestDto.setStatus(project.getStatus());
				projectInterestDto.setInterestFrom(project.getInterestFrom());
				projectInterestDto.setProfitType(project.getProfitType());
				DebtBiz biz = debtManager.getFullDebtInfoById(project
						.getDebtId());
				DebtDto debtDto = BeanCopyUtil.map(biz, DebtDto.class);
				projectInterestDto.setDebtDto(debtDto);
				if (project.getAnnualizedRateType() == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER
						.getStatus()) {
					List<ProjectInterest> projectInterestList = projectInterestManager
							.getProjectInterestByProjectId(project.getId());
					projectInterestDto.setProjectInterestInfoList(BeanCopyUtil
							.mapList(projectInterestList,
									ProjectInterestInfoDto.class));
				} else {
					List<ProjectInterestInfoDto> projectInterestList = new ArrayList<ProjectInterestInfoDto>();
					ProjectInterestInfoDto pi = new ProjectInterestInfoDto();
					pi.setMinInvest(project.getMinInvestAmount());
					pi.setMaxInvest(project.getMaxInvestAmount());
					pi.setAnnualizedRate(project.getMinAnnualizedRate());
					projectInterestList.add(pi);

					ProjectInterestInfoDto pi2 = new ProjectInterestInfoDto();
					pi2.setMinInvest(project.getMaxInvestAmount());
					pi2.setMaxInvest(project.getTotalAmount());
					pi2.setAnnualizedRate(project.getMaxAnnualizedRate());
					projectInterestList.add(pi2);
					projectInterestDto
							.setProjectInterestInfoList(projectInterestList);
				}
				projectInterestDto.setMostInvestAmount(getMostInvestAmount(
						project.getTotalAmount(), availableBalance,
						project.getMinInvestAmount(), project.getId()));

				int earningsDays = 0;
				earningsDays = DateUtils.getIntervalDays(DateUtils.formatDate(
						DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3),
						project.getEndDate())
						+ 1 - project.getInterestFrom();
				// 非受限使用优惠券用户
				if (couponManager.useCouponSpecialLimit(memberId)) {
					List<Coupon> couponList = couponManager
							.getUsableAndLimitedCouponsExceptAmount(memberId,
									TypeEnum.COUPON_TYPE_INCOME.getType(),
									CouponEnum.COUPON_CLIENT_APP.getCode(),
									earningsDays, projectRemainAmount);
					projectInterestDto.setCoupons(BeanCopyUtil.mapList(
							couponList, OrderCouponDto.class));
				}
				resultDTO.setResult(projectInterestDto);
			} else {
				resultDTO
						.setResultCode(ResultCode.PROJECT_NOT_INVESTING_STATUS_APP_ERROR);
			}
		} catch (Exception e) {
			logger.error("获得项目收益异常：" + pid + "memberId:" + memberId, e);
			resultDTO.setResultCode(ResultCode.ERROR);
		}
		return resultDTO;
	}

	private ResultDTO getProjectInterestListForTransferProject(Long pid,
			Long memberId, int projectCategory, Long transferId) {
		ResultDTO resultDTO = new ResultDTO();
		try {
			TransferProject project = transferProjectManager
					.selectByPrimaryKey(transferId);

			if (project != null
					&& project.getDelFlag() > 0
					&& project.getStatus() == StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING
							.getStatus()) {
				BigDecimal projectRemainAmount = null;
				Balance proBalance = balanceManager.queryBalance(project.getTransactionId(),
						TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT);
				if (proBalance != null) {
					projectRemainAmount = proBalance.getAvailableBalance();
				} else {
					logger.error("项目id={}可用余额在余额表未找到", transferId);
					projectRemainAmount = project.getTransferAmount();
				}
				logger.debug("项目id={}可用余额={}", transferId, projectRemainAmount);
				if (projectRemainAmount.compareTo(BigDecimal.ZERO) == 0
						|| projectRemainAmount == null) {
					resultDTO
							.setResultCode(ResultCode.PROJECT_BALANCE_ZERO_APP_ERROR);
					return resultDTO;
				}
				ProjectInterestDto projectInterestDto = new ProjectInterestDto();
				projectInterestDto
						.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER
								.getType());
				projectInterestDto.setProjectName(project.getTransferName());
				projectInterestDto.setTransferId(transferId);
				projectInterestDto.setProjectId(project.getProjectId());
				projectInterestDto.setAvailableBalance(projectRemainAmount);
				projectInterestDto.setTransferAnnualizedRate(transferProjectManager.getTransferProjectAnnualized(transferId));
				projectInterestDto.setEarningPeriod(transferProjectManager.getReturnDay(project.getTransactionId()));
				projectInterestDto.setCurrentDate(DateUtils.getCurrentDate());
				projectInterestDto.setOnlineTime(project.getCreateTime());
				projectInterestDto.setStatus(project.getStatus());
				projectInterestDto.setAnnualizedRateType(1);// 配合移动端，转让项目即直投项目处理成一个阶梯收益
				List<ProjectInterestInfoDto> projectInterestList = new ArrayList<ProjectInterestInfoDto>();
				ProjectInterestInfoDto pi = new ProjectInterestInfoDto();
				pi.setMaxInvest(project.getSubscriptionPrincipal());
				pi.setMinInvest(project.getUnitSubscriptionAmount());
				pi.setAnnualizedRate(transferProjectManager.getTransferProjectAnnualized(transferId));
				projectInterestList.add(pi);
				projectInterestDto
						.setProjectInterestInfoList(projectInterestList);
				projectInterestDto
						.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER
								.getType());

				projectInterestDto.setUnitTransferAmount(project
						.getUnitTransferAmount());

				projectInterestDto.setInterestFrom(project.getInterestFrom());
				projectInterestDto.setProfitType(project.getProfitType());
				projectInterestDto.setTransactionAmount(project
						.getTransactionAmount());
				projectInterestDto.setTransferAmount(project
						.getTransferAmount());
				projectInterestDto.setDiscount(project.getDiscount());
				// projectInterestDto.setProjectValue(project.getProjectValue());
				resultDTO.setResult(projectInterestDto);
				return resultDTO;
			} else {
				resultDTO
						.setResultCode(ResultCode.PROJECT_NOT_INVESTING_STATUS_APP_ERROR);
			}
		} catch (Exception e) {
			logger.error("获得项目收益异常：" + pid + "memberId:" + memberId, e);
			resultDTO.setResultCode(ResultCode.ERROR);
		}
		return resultDTO;
	}

	/**
	 * 获取直投项目的收益天数
	 */
	public int getDirectProjectEarningDays(int borrowPeriod,
			int borrowPeriodType) {
		Date currendDate = DateUtils.getCurrentDate();
        int days = 0;
        if (TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()==borrowPeriodType) {
            days = borrowPeriod;
        }
        //借款周期类型月
        if (TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()==borrowPeriodType) {
            Date peroidEndDate = DateUtils.addMonth(currendDate, borrowPeriod);
            days = DateUtils.getIntervalDays(currendDate, peroidEndDate);
        }
        //借款周期类型年
        if (TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()==borrowPeriodType) {
        	Date peroidEndDate = DateUtils.addYearsApart(currendDate, borrowPeriod);
            days = DateUtils.getIntervalDays(currendDate, peroidEndDate);
        }
		if (TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType() == borrowPeriodType) {
			days = borrowPeriod * 7;
		}
        return days;
    }
	private Integer getPeriod(Project project)throws ManagerException{
		Integer interestFrom = project.getInterestFrom();
		// 借款周期
		Integer borrowPeriod = project.getBorrowPeriod();
		// 借款周期类型（1-日；2-月；3-年）
		Integer borrowPeriodType = project.getBorrowPeriodType();
		// 开始计息日
		Date beginInterestDate = DateUtils.addDate(DateUtils.getCurrentDate(),
				interestFrom);
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())
				|| DebtEnum.RETURN_TYPE_ONCE.getCode().equals(
						project.getProfitType())
						||DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())) {
			// 借款周期类型天
			int borrowDays = 0;
			if (borrowPeriodType == 1) {
				borrowDays = borrowPeriod;
			}
			// 借款周期类型月
			if (borrowPeriodType == 2) {
				Date endDate = DateUtils.addMonth(beginInterestDate,
						borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate,
						endDate);
			}
			// 借款周期类型年
			if (borrowPeriodType == 3) {
				Date endDate = DateUtils.addYearsApart(beginInterestDate,
						borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate,
						endDate);
			}
			return borrowDays;
		}
		//等本等息按周还款
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())) {
			int week = 0 ;
			if (borrowPeriodType != 4) {
				return week;
			}
			week = borrowPeriod;
			return week;
		}
		
		// 等本等息 (按月) 不支持 借款周期为天的
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(
				project.getProfitType())) {
			int months = 0;
			// 借款周期类型月
			if (borrowPeriodType == 2) {
				months = borrowPeriod;
			}
			// 借款周期类型年
			if (borrowPeriodType == 3) {
				months = borrowPeriod * 12;
			}
			return months;
		}
		return 0;
	}

	/**
	 * 
	 * @param totalAmount
	 *            投资总额
	 * @param availableBalance
	 *            剩余金额
	 * @param minInvestAmount
	 *            最少金额
	 * @param projectId
	 *            项目编号
	 * @return
	 */
	private BigDecimal getMostInvestAmount(BigDecimal totalAmount,
			BigDecimal availableBalance, BigDecimal minInvestAmount,
			Long projectId) {
		try {
			if (availableBalance.compareTo(totalAmount) != 0) {
				Transaction transaction = transactionManager
						.selectMostTransactionByProject(projectId);
				if (transaction == null) {
					BigDecimal mostInvestAmount = availableBalance
							.divide(new BigDecimal(2));
					return getInvestAmount(mostInvestAmount, minInvestAmount);
				}
				BigDecimal investAmount = transaction.getInvestAmount();
				if (availableBalance.compareTo(investAmount) > 0) {
					BigDecimal _subAmount = availableBalance
							.divide(new BigDecimal(2));
					BigDecimal m = _subAmount.remainder(minInvestAmount)
							.setScale(0, BigDecimal.ROUND_HALF_UP);
					if (m.compareTo(BigDecimal.ZERO) != 0) {
						_subAmount = _subAmount.subtract(m)
								.add(minInvestAmount);
					}
					if (_subAmount.compareTo(investAmount) > 0) {
						return _subAmount;
					} else {
						return investAmount.add(minInvestAmount);
					}
				} else {
					return null;
				}
			} else {
				BigDecimal mostInvestAmount = availableBalance
						.divide(new BigDecimal(2));
				return getInvestAmount(mostInvestAmount, minInvestAmount);
			}
		} catch (Exception ex) {
			logger.error("获得可一鸣惊人金额异常：" + projectId, ex);
		}
		return null;
	}

	private BigDecimal getInvestAmount(BigDecimal mostInvestAmount,
			BigDecimal minInvestAmount) {
		BigDecimal m = mostInvestAmount.remainder(minInvestAmount).setScale(0,
				BigDecimal.ROUND_HALF_UP);
		if (m.compareTo(BigDecimal.ZERO) != 0) {
			mostInvestAmount = mostInvestAmount.subtract(m)
					.add(minInvestAmount);
		}
		return mostInvestAmount;
	}

	@Override
	public ProjectDetailDto getProjectDetail(Long id) {
		ProjectDetailDto detail = new ProjectDetailDto();
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			if (project == null) {
				return detail;
			}
			// 项目介绍
			detail.setShortDesc(project.getShortDesc());
			detail.setId(project.getId());
			detail.setDescription(project.getDescription());
			detail.setOpenPlatformKey(project.getOpenPlatformKey());
			DebtBiz biz = debtManager.getFullDebtInfoById(project.getDebtId());
			DebtDto debtDto = BeanCopyUtil.map(biz, DebtDto.class);
			detail.setDebtDto(debtDto);
			detail.setCollateralAttachments(bscAttachmentManager
					.findAttachmentsByKeyIdAndModule(
							project.getId().toString(),
							DebtEnum.ATTACHMENT_MODULE_DEBT_COLLATERAL
									.getCode(), -1));
			detail.setLenderMemberAttachments(bscAttachmentManager
					.findAttachmentsByKeyIdAndModule(
							project.getId().toString(),
							DebtEnum.ATTACHMENT_MODULE_DEBT_LENDER.getCode(),
							-1));
			detail.setBorrowMemberAttachments(bscAttachmentManager
					.findAttachmentsByKeyIdAndModule(
							project.getId().toString(),
							DebtEnum.ATTACHMENT_MODULE_DEBT_BORROWER.getCode(),
							-1));
			if (project.getProjectType().equals(
					DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode())
					&& biz.getEnterpriseId() != null) {
				detail.setEnterpriseProjectInfoDto(getEnterpriseProjectInfo(biz
						.getEnterpriseId()));
			}
			if (project.getInvestType().equals(ProjectEnum.PROJECT_TYPE_DIRECT.getType())){
				borrowerCredit(detail,project.getBorrowerId());
			}
		} catch (ManagerException e) {
			logger.error("获得项目详情异常：", e);
		}
		return detail;
	}

	/**
	 * 借款人信用信息
	 * @param projectDetailDto
	 * @param borrowerId
	 */
	private void borrowerCredit(ProjectDetailDto projectDetailDto,Long borrowerId){
		Integer payOffCount = projectManager.queryPayOffCountByBorrowerId(borrowerId);
		projectDetailDto.setPayOffCount(payOffCount);
		Integer overdueCount= overdueRepayLogManager.queryOverdueCountByBorrowerId(borrowerId);
		projectDetailDto.setOverdueCount(overdueCount);
		BigDecimal overdueAmount= overdueRepayLogManager.queryOverdueAmountByBorrowerId(borrowerId);
		projectDetailDto.setOverdueAmount(overdueAmount);
		BorrowerCreditGrade borrowerCreditGrade= borrowerCreditGradeManager.queryByBorrowerId(borrowerId);
		if (borrowerCreditGrade!=null){
			projectDetailDto.setBorrowerCreditLevel(borrowerCreditGrade.getCreditLevel());
			projectDetailDto.setBorrowerCreditLevelDes(borrowerCreditGrade.getCreditLevelDes());
		}
	}

	/**
	 * P2P项目详情
	 */
	@Override
	public ProjectDetailDto p2pProjectDetail(Long id, Long memberId) {
		ProjectDetailDto detail = new ProjectDetailDto();
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			if (project == null) {
				return detail;
			}
			// 项目介绍
			detail.setShortDesc(project.getShortDesc());
			// 项目类型
			detail.setProjectType(project.getProjectType());
			// 直投债权
			detail.setInvestType(project.getInvestType());
			// 担保方式
			detail.setSecurityType(project.getSecurityType());
			// 项目描述，保障措施
			detail.setP2pDescription(project.getDescription());
			// 借款人信息
			MemberBaseBiz borrower = memberManager.selectMemberBaseBiz(project
					.getBorrowerId());
			detail.setBorrowMemberBiz(borrower);
			// 企业信息
			detail.setBorrowerType(project.getBorrowerType());
			if (TypeEnum.MEMBER_TYPE_COMPANY.getType() == project
					.getBorrowerType()||TypeEnum.MEMBER_TYPE_ORG.getType() == project
							.getBorrowerType()) {
				Enterprise enterprise = enterpriseManager.selectByKey(project
						.getEnterpriseId());
				detail.setEnterprise(enterprise);
			}
			Boolean flag = transactionManager.isMemberInvestedProject(
					project.getId(), memberId);
			/*
			 * if(project.getId()!= null && project.getId()<=989807383L){ flag =
			 * true; }
			 */
			// 附件信息
			List<BscAttachment> bscAttachments = bscAttachmentManager
					.findAttachmentsByKeyId(String.valueOf(project.getId()));
			if (!Collections3.isEmpty(bscAttachments)) {
				assignationP2pAttachment(bscAttachments, detail, flag);
			}

			// 逾期结算记录
			List<OverdueRepayLog> overdueRepaylogs = overdueRepayLogManager
					.getOverdueRepayListByProjectId(project.getId());
			detail.setOverdueRepayBiz(overdueRepaylogs);
			// 担保物类型的详细信息
			DebtCollateral collateral = debtCollateralManager
					.findCollateralByProjectId(project.getId());
			processCollateralDetail(collateral);
			detail.setDebtCollateral(collateral);
			detail.setOpenPlatformKey(project.getOpenPlatformKey());
			if (project.getInvestType().equals(ProjectEnum.PROJECT_TYPE_DIRECT.getType())){
				borrowerCredit(detail,project.getBorrowerId());
			}
		} catch (ManagerException e) {
			logger.error("获得直投项目详情异常：", e);
		}
		return detail;
	}

	@Override
	public ProjectDetailDto getP2pProjectContractImage(Long id, Long memberId) {
		ProjectDetailDto detail = new ProjectDetailDto();
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			if (project == null) {
				return detail;
			}
			Boolean flag = transactionManager.isMemberInvestedProject(
					project.getId(), memberId);
			/*
			 * if(project.getId()!= null && project.getId()<=989807383L){ flag =
			 * true; }
			 */
			// 附件信息
			List<BscAttachment> bscAttachments = bscAttachmentManager
					.findAttachmentsByKeyId(String.valueOf(project.getId()));
			if (!Collections3.isEmpty(bscAttachments)) {
				assignationP2pAttachment(bscAttachments, detail, flag);
			}
		} catch (ManagerException e) {
			logger.error("获得项目图片异常：", e);
		}
		return detail;
	}

	/**
	 * P2P项目还款计划
	 */
	@Override
	public ProjectDetailDto p2pProjectrepaymentPlan(Long id) {
		ProjectDetailDto detail = new ProjectDetailDto();
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			if (project == null) {
				return detail;
			}
			detail.setStatus(project.getStatus());
			// 收益本息表
			List<DebtInterest> interests = debtInterestManager
					.findInterestsByProjectId(project.getId());
			if (Collections3.isNotEmpty(interests)) {
				detail.setInterests(interests);
			}
			if (project.getAnnualizedRateType() == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER
					.getStatus()) {
				// 阶段收益
				List<ProjectInterest> projectInterestList = projectInterestManager
						.getProjectInterestByProjectId(project.getId());
				detail.setProjectInterestList(projectInterestList);
			}
			List<DebtInterest> debtInterests = debtInterestManager
					.findProjectInterestsByProjectId(project.getId());
			if (Collections3.isNotEmpty(debtInterests)) {
				detail.setStartDate(debtInterests.get(0).getStartDate());
				detail.setEndDate((debtInterests.get(debtInterests.size() - 1)
						.getEndDate()));
				detail.setInterests(debtInterests);
			}
		} catch (ManagerException e) {
			logger.error("获得项目还款计划：", e);
		}
		return detail;
	}

	/**
	 * P2P项目还款计划
	 */
	@Override
	public RepaymentPlanDto p2pProjectrepaymentPlanJosn(Long id) {
		RepaymentPlanDto repay = new RepaymentPlanDto();
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			if (project == null) {
				return repay;
			}
			repay.setStatus(project.getStatus());
			// 项目本息表
			List<DebtInterest> interests = debtInterestManager
					.findInterestlistByProjectId(project.getId());
			if (Collections3.isNotEmpty(interests)) {
				List<DebtInterestForAppDto> interestList = BeanCopyUtil
						.mapList(interests, DebtInterestForAppDto.class);
				repay.setPreFlag(0);
				repay.setOverFlag(0);
				for (DebtInterestForAppDto dto : interestList) {

					Date end = DateUtils.formatDate(dto.getEndDate(),
							DateUtils.DATE_FMT_3);
					BigDecimal amount = BigDecimal.ZERO;

					if (1 == dto.getStatus()) {
						amount = dto.getRealPayInterest().add(
								dto.getRealPayPrincipal());
						Date pay = DateUtils.formatDate(dto.getPayTime(),
								DateUtils.DATE_FMT_3);

						int f = DateUtils.compareTwoDate(end, pay);// before = 1
																	// after =2
																	// equal =3
						if (f == 1) {// 预期时间 在实际支付时间之前 表示 发生逾期
							dto.setPayType(2);
							repay.setOverFlag(1);
						}
						if (f == 2) {// 预期时间在实际支付时间之后 表示提前还款 记录提前还款标记 和提前还款原因
							dto.setPayType(1);
							repay.setPreFlag(1);
							repay.setPrepaymentRemark(project
									.getPrepaymentRemark());
							repay.setPreDate(DateUtils.formatDatetoString(
									dto.getPayTime(), DateUtils.DATE_FMT_9));
						}
						if (f == 3) {// 相等 表示正常
							dto.setPayType(0);

						}
					} else if (3 == dto.getStatus()) {
						dto.setPayType(2);
						repay.setOverFlag(1);
						Integer days = DateUtils.daysBetween(dto.getEndDate(),
								DateUtils.getCurrentDate());
						dto.setOverDays(days);
						amount = dto.getPayableInterest().add(
								dto.getPayablePrincipal());
					} else {
						// Date pay =
						// DateUtils.formatDate(DateUtils.getCurrentDate(),
						// DateUtils.DATE_FMT_3);
						// int f = DateUtils.compareTwoDate(end, pay);//before =
						// 1 after =2 equal =3
						/*
						 * if(f==1){// 预期时间 在当前时间之前 表示 发生逾期 dto.setPayType(2);
						 * repay.setOverFlag(1); Integer days =
						 * DateUtils.daysBetween(dto.getEndDate(),
						 * DateUtils.getCurrentDate()); dto.setOverDays(days); }
						 */
						// if(f==2||f==3){//预期时间在当前时间之后 或相当 表示待还款 正常
						dto.setPayType(0);

						// }
						amount = dto.getPayableInterest().add(
								dto.getPayablePrincipal());
					}
					dto.setAmount(amount);
				}
				repay.setInterestList(interestList);
			}

			// 逾期结算记录(已完成)
			List<OverdueRepayLog> overdueRepaylogs = overdueRepayLogManager
					.getOverdueRepayListByProjectId(project.getId());
			repay.setOverdueRepayBiz(overdueRepaylogs);
			// 催收中
			OverdueRepayLog overdue = overdueRepayLogManager
					.getRepayLogByProjectIdCollect(project.getId());
			repay.setOverdue(overdue);

		} catch (ManagerException e) {
			logger.error("获得直投项目还款计划：", e);
		}
		return repay;
	}

	/**
	 * P2P附件分类
	 */
	private void assignationP2pAttachment(List<BscAttachment> bscAttachments,
			ProjectDetailDto p2pProject, Boolean flag) {

		List<BscAttachment> signMemberAttachments = p2pProject
				.getSignAttachments();
		List<BscAttachment> collateralAttachments = p2pProject
				.getCollateralAttachments();
		List<BscAttachment> borrowMemberAttachments = p2pProject
				.getBorrowMemberAttachments();
		List<BscAttachment> contractAttachments = p2pProject
				.getContractAttachments();
		List<String> contractCategoryName = p2pProject
				.getContractCategoryName();

		if (flag) {
			for (BscAttachment attachment : bscAttachments) {
				String module = attachment.getModule();
				if (module
						.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_SIGN
								.getCode())) {// 项目形象图
					signMemberAttachments.add(attachment);
				} else if (module
						.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_COLLATERAL
								.getCode())) {// 担保物附件
					collateralAttachments.add(attachment);
				} else if (module
						.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_BORROWER
								.getCode())) {// 借款人附件
					borrowMemberAttachments.add(attachment);
				} else if (module
						.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_CONTRACT
								.getCode()) // 合同图片
						|| module
								.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_LEGAL
										.getCode())// 法律意见书
						|| module
								.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_CREDIT
										.getCode())// 征信报告
						|| module
								.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_BASE
										.getCode())) {// 其他资料图片
					String categoryName = AttachmentEnum.getEnumByCode(module)
							.getDesc();
					attachment.setModule(categoryName);
					contractAttachments.add(attachment);
					if (!contractCategoryName.contains(categoryName)) {
						contractCategoryName.add(categoryName);
					}
				}
			}
		} else {
			for (BscAttachment attachment : bscAttachments) {
				String module = attachment.getModule();
				if (module
						.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_SIGN
								.getCode())) {// 项目形象图
					signMemberAttachments.add(attachment);
				} else if (module
						.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_COLLATERAL_MOSAIC
								.getCode())) {// 担保物附件
					collateralAttachments.add(attachment);
				} else if (module
						.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_BORROWER_MOSAIC
								.getCode())) {// 借款人附件
					borrowMemberAttachments.add(attachment);
				} else if (module
						.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_CONTRACT_MOSAIC
								.getCode()) // 合同图片
						|| module
								.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_LEGAL_MOSAIC
										.getCode())// 法律意见书
						|| module
								.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_CREDIT_MOSAIC
										.getCode())// 征信报告
						|| module
								.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_BASE_MOSAIC
										.getCode())) {// 其他资料图片
					String categoryName = AttachmentEnum.getEnumByCode(module)
							.getDesc();
					attachment.setModule(categoryName);
					contractAttachments.add(attachment);
					if (!contractCategoryName.contains(categoryName)) {
						contractCategoryName.add(categoryName);
					}
				}
			}
		}
	}

	private void processCollateralDetail(DebtCollateral collateral) {
		if (collateral != null
				&& StringUtil.isNotBlank(collateral.getCollateralDetails())) {
			Map<String, String> colMap = JSON.parseObject(
					collateral.getCollateralDetails(),
					new TypeReference<LinkedHashMap<String, String>>() {
					});
			for (Object o : colMap.entrySet()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) o;
				if (StringUtil.isBlank(entry.getValue())) {
					continue;
				}
				if (entry.getKey().equals("发动机号")) {
					entry.setValue(StringUtil.maskString(entry.getValue(),
							StringUtil.ASTERISK, 1, 1));
				}
				if (entry.getKey().equals("车架号")) {
					entry.setValue(StringUtil.maskString(entry.getValue(),
							StringUtil.ASTERISK, 3, 1));
				}
				if (entry.getKey().equals("车牌号")) {
					entry.setValue(StringUtil.maskString(entry.getValue(),
							StringUtil.ASTERISK, 2, 1));
				}
				if (entry.getKey().equals("合格证编号")) {
					entry.setValue(StringUtil.maskString(entry.getValue(),
							StringUtil.ASTERISK, 2, 1));
				}
				if (entry.getKey().equals("房屋产权证")) {
					entry.setValue(numberMaskString(entry.getKey(),
							entry.getValue()));
				}
				if (entry.getKey().equals("土地使用权证")) {
					entry.setValue(numberMaskString(entry.getKey(),
							entry.getValue()));
				}
			}
			collateral.setCollateralDetails(JSON.toJSONString(colMap, true));
		}
	}

	private String numberMaskString(String colName, String colValue) {
		String regex = "\\d+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(colValue);
		if (m.find() == true) {
			return StringUtil.maskString(m.group(0), StringUtil.ASTERISK, 1, 1);
		}
		return colValue;
	}

	@Override
	public List<BscAttachment> getProjectContractImage(Long id) {
		List<BscAttachment> attachmentList = Lists.newArrayList();
		try {
			List<BscAttachment> contrActattachmentList = bscAttachmentManager
					.findAttachmentsByKeyIdAndModule(id.toString(),
							DebtEnum.ATTACHMENT_MODULE_DEBT_CONTRACT.getCode(),
							-1);
			if (Collections3.isNotEmpty(contrActattachmentList)) {
				attachmentList.addAll(contrActattachmentList);
			}
			List<BscAttachment> legalActattachmentList = bscAttachmentManager
					.findAttachmentsByKeyIdAndModule(id.toString(),
							DebtEnum.ATTACHMENT_MODULE_DEBT_LEGAL.getCode(), -1);
			if (Collections3.isNotEmpty(legalActattachmentList)) {
				attachmentList.addAll(legalActattachmentList);
			}
			List<BscAttachment> otherActattachmentList = bscAttachmentManager
					.findAttachmentsByKeyIdAndModule(id.toString(),
							DebtEnum.ATTACHMENT_MODULE_DEBT_BASE.getCode(), -1);
			if (Collections3.isNotEmpty(otherActattachmentList)) {
				attachmentList.addAll(otherActattachmentList);
			}
		} catch (ManagerException e) {
			logger.error("获得项目图片异常：", e);
		}
		return attachmentList;
	}

	@Override
	public ResultDTO<IndexDataDto> queryIndexData() {
		ResultDTO<IndexDataDto> result = new ResultDTO<IndexDataDto>();
		try {
			IndexDataDto indexData = new IndexDataDto();
			
			//banner
			List<Banner> bannerList = bannerService.findAppOnlineBanner();
			if (bannerList != null) {
				indexData.setBannerList(BeanCopyUtil.mapList(bannerList,
						BannerDto.class));
			}
			
			//icon
			List<CmsIcon> iconList = cmsIconManager.findOnlineIcon();
			if( iconList != null){
				indexData.setIconList(BeanCopyUtil.mapList(iconList, IconDto.class));
			}
			
			
			int number = 5;
			List<ProjectListDto> projectList = Lists.newArrayList();
			// 推荐位优先
			List<ProjectForFront> recommendProjectForList = projectManager
					.findAppRecommendProjectList(number);
			if (Collections3.isNotEmpty(recommendProjectForList)) {
				projectList.addAll(BeanCopyUtil.mapList(
						recommendProjectForList, ProjectListDto.class));
			}
			
			
			// 常规投资中的项目
			if (projectList.size() < number) {
				List<ProjectForFront> investingProjectForList = projectManager
						.findAppIndexProjectList(number - projectList.size(), 1);
				if (Collections3.isNotEmpty(investingProjectForList)) {
					projectList.addAll(BeanCopyUtil.mapList(
							investingProjectForList, ProjectListDto.class));
				}
			}
			// 不足5个项目，再上履约或还款中的项目
			if (projectList.size() < number) {
				List<ProjectForFront> notInvestingProjectForList = projectManager
						.findAppIndexProjectList(number - projectList.size(), 0);
				if (Collections3.isNotEmpty(notInvestingProjectForList)) {
					projectList.addAll(BeanCopyUtil.mapList(
							notInvestingProjectForList, ProjectListDto.class));
				}
			}
			for (ProjectListDto p : projectList) {
				p.setAvailableBalance(getProjectBalanceById(p.getId()));
			}
			indexData.setProjectList(projectList);
			result.setResult(indexData);
		} catch (Exception ex) {
			logger.error("查询首页数据异常", ex);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}

//	@Override
//	public Page<ProjectForFront> findProjectListByPage(ProjectQuery projectQuery) {
//		Page<ProjectForFront> pageList = null;
//		try {
//			projectQuery.setInvestType(ProjectEnum.PROJECT_TYPE_DEBT.getType());
//			projectQuery.setInvestTypeCode("debt");
//			pageList = projectManager.findFrontProjectListByPage(projectQuery);
//		} catch (ManagerException e) {
//			logger.error("获取项目列表异常", e);
//		}
//		return pageList;
//	}

	@Override
	public ResultDTO<IndexDto> queryIndexData2(Long memberId, Integer version) {
		ResultDTO<IndexDto> result = new ResultDTO<IndexDto>();
		boolean isInvested = false;
		IndexDto indexDto = new IndexDto();
		IndexProjectDto indexProject = new IndexProjectDto();
		try {
			if (memberId != null) {
				Member member = memberManager.selectByPrimaryKey(memberId);
				int friends = 0;
				String shortUrl = "";
				if (member != null) {
					isInvested = transactionManager.isMemberInvested(memberId);
					friends = memberManager.countFriends(memberId, null, null);
					shortUrl = member.getShortUrl();
					try{
						//60亿活动，登录就送1%收益券一张
						activityLotteryManager.sixBillionActAfterLogin(member);
					}catch(Exception e){
						logger.error("【庆60亿，抢标奖励翻6倍！】首次登录可获得1%收益券一张异常  memberId={}", member.getId(), e);
					}
					//是否生日
					indexDto.setBirth(false);
					if(member.getBirthday() != null){
						int status = getBirthdayStatus(member.getBirthday());
						if(status == 1){
							indexDto.setBirth(true);
						}
					}
					
				}
				indexDto.setFriends(friends);
				indexDto.setTitle("有融网");
				indexDto.setShareContent("我正在用有融网APP投资理财，快和我一起来吧");
				indexDto.setShareUrl(PropertiesUtil.getWebRootUrl()
						+ "/security/register/?inviteCode_shortURL=" + shortUrl);
				indexDto.setShortUrl(shortUrl);
			}
			//首页项目部分
			if (isInvested) {// 是否有投资 有
				getIndexProject(indexProject);
			} else {
				// 是否有新客项目
				Project noviceProject = projectManager.getNoviceProject();
				if (noviceProject != null) {
					// 显示新客项目
					BeanCopyUtil.copy(noviceProject, indexProject);
				} else {
					getIndexProject(indexProject);
				}

			}
			if (indexProject.getId() != null) {
				indexProject
						.setAvailableBalance(getProjectBalanceById(indexProject
								.getId()));
				if (indexProject.getStatus() > 40) {
					int countMember = transactionManager
							.getTransactionMemberCountByProject(indexProject
									.getId());
					BigDecimal transactionInterest = transactionManager
							.getTotalTransactionInterestByProject(indexProject
									.getId());
					if (transactionInterest == null) {
						indexProject.setTotalInterest("0.00");
					} else {
						indexProject.setTotalInterest(FormulaUtil
								.getFormatPriceRound(transactionInterest));
					}
					indexProject.setCountMember(countMember);
				}
			}
			
			this.getExtraProject(indexProject);

			//首页banner
			List<Banner> bannerList = bannerService
					.findAppOnlineBannerForVersion(version);
			List<IndexProjectDto> projectList = Lists.newArrayList();
			projectList.add(indexProject); // 考虑后面升级，APP客户端无需变更
			indexDto.setProjectList(projectList);
			if (bannerList != null) {
				indexDto.setBannerList(BeanCopyUtil.mapList(bannerList,
						BannerDto.class));
			}
			
			//icon
			List<CmsIcon> iconList = cmsIconManager.findOnlineIcon();
			if( iconList != null){
				indexDto.setIconList(BeanCopyUtil.mapList(iconList, IconDto.class));
			}
			
			//app首页广告
			List<Banner> appIndexAdList =bannerService.findAppIndexAd();
			if (appIndexAdList != null && !appIndexAdList.isEmpty()) {
				Banner appIndexAd = appIndexAdList.get(0);
				BannerDto dto = new BannerDto();
				BeanCopyUtil.copy(appIndexAd, dto);
				List<BannerDto> dtoList=new ArrayList<BannerDto>();
				dtoList.add(dto);
				indexDto.setAppIndexAdList(dtoList);
			}
			
			//首页弹层
			List<Banner> appIndexPopupList =bannerService.findAppIndexPopup();
			if (appIndexPopupList != null && !appIndexPopupList.isEmpty()) {
				Banner appIndexPopup = appIndexPopupList.get(0);
				BannerDto dto = new BannerDto();
				BeanCopyUtil.copy(appIndexPopup, dto);
				List<BannerDto> dtoList=new ArrayList<BannerDto>();
				dtoList.add(dto);
				indexDto.setAppIndexPopupList(dtoList);
			}
			
			BigDecimal totalInvest = balanceManager.getPaltformTotalInvest();

			indexDto.setChecked(memberCheckManager.isChecked(memberId));

			// 金额取亿，直接舍弃
			String platTotalInvest = FormulaUtil
					.getFormatPriceHundredMillion(totalInvest.toString());
			indexDto.setPlatTotalInvest(platTotalInvest + "亿");
			// 人数取万，直接舍弃
			Long memberNum = RedisPlatformClient.getMemberCount();
			String platMembers = FormulaUtil
					.getFormatPriceTenThousand(memberNum.toString());
			indexDto.setPlatMembers(platMembers + "万");

			indexDto.setPaltformTotalInvest(totalInvest);
			indexDto.setPaltformMembers(RedisPlatformClient.getMemberCount());

			Map<String, Object> map = Maps.newHashMap();
			map.put("categoryId", 1);// 公告
			List<CmsArticle> articles = articleService.selectHomeNotice(map);
			indexDto.setArticles(articles);

			CmsArticle cmsArticle = articleService.selectNoticeArticle();
			indexDto.setNoticeArticles(cmsArticle);

			result.setResult(indexDto);
		} catch (Exception e) {
			logger.error("查询首页数据异常", e);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}
	
	public int getBirthdayStatus(Date birthday){
		Date date = DateUtils.getCurrentDate();
		int c_month = DateUtils.getMonth(date);
		int c_day = DateUtils.getDate(date);
		int b_month = DateUtils.getMonth(birthday);
		int b_day = DateUtils.getDate(birthday);
		if(c_month < b_month){
			return 0;//生日还未到
		}else if(c_month == b_month && c_day == b_day){
			return 1;//今天生日
		}else{
			return -1;//生日已经过了
		}
	}

	private void getExtraProject(IndexProjectDto indexProject) throws Exception{
		
		int activitySign = 0;//活动标识 新春项目1，快投2
		int extraType = 0;//特殊业务类型: 1-活动 2-项目加息
		if (indexProject.getId() != null) {
			activitySign = projectExtraManager
					.getProjectActivitySign(indexProject.getId());
			ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(indexProject.getId());
			if(projectExtraAddRate!=null){
				extraType=projectExtraAddRate.getExtraType();
		        indexProject.setAddRate(projectExtraAddRate.getExtraAmount().toString());
			}
		}
		indexProject.setActivitySign(activitySign);
		indexProject.setExtraType(extraType);
	}
	

	private void getProject(IndexProjectDto indexProject)
			throws ManagerException {
		Project recommendProject = projectManager
				.getSortFirstAppRecommendProject();
		if (recommendProject == null) {// 是否有推荐 无
			Project onlineProject = projectManager.getLatestOnLineProject();
			if (onlineProject == null) {// 是否有可投项目
				Project finishProject = projectManager.getFinishProject();
				// 显示履约中项目
				BeanCopyUtil.copy(finishProject, indexProject);
			} else {
				// 显示可投项目
				BeanCopyUtil.copy(onlineProject, indexProject);
			}
		} else {
			// 显示推荐项目
			BeanCopyUtil.copy(recommendProject, indexProject);
		}
	}

	private void p2pGetProject(IndexProjectDto indexProject)
			throws ManagerException {
		Project recommendProject = projectManager
				.p2pGetSortFirstAppRecommendProject();
		if (recommendProject == null) {// 是否有推荐 无
			Project onlineProject = projectManager.p2pGetLatestOnLineProject();
			if (onlineProject == null) {// 是否有可投项目
				Project finishProject = projectManager.p2pGetFinishProject();
				// 显示履约中项目
				BeanCopyUtil.copy(finishProject, indexProject);
			} else {
				// 显示可投项目
				BeanCopyUtil.copy(onlineProject, indexProject);
			}
		} else {
			// 显示推荐项目
			BeanCopyUtil.copy(recommendProject, indexProject);
		}
	}

	private void getIndexProject(IndexProjectDto indexProject)
			throws ManagerException {
		ProjectNotice notice = projectNoticeManager.getSortFirstProjectNotice();
		if (notice == null) {// 是否有预告 无
			getProject(indexProject);
		} else {
			// 显示预告项目
			ProjectForFront project = projectManager
					.getProjectForFrontByProjectId(notice.getProjectId());
			if (project != null
					&& project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE
							.getStatus()) {
				BeanCopyUtil.copy(project, indexProject);
			} else {
				getProject(indexProject);
			}
		}
	}

	private void p2pGetIndexProject(IndexProjectDto indexProject)
			throws ManagerException {
		ProjectNotice notice = projectNoticeManager
				.p2pGetSortFirstProjectNotice();
		if (notice == null) {// 是否有预告 无
			p2pGetProject(indexProject);
		} else {
			// 显示预告项目
			ProjectForFront project = projectManager
					.getProjectForFrontByProjectId(notice.getProjectId());
			if (project != null
					&& project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE
							.getStatus()) {
				BeanCopyUtil.copy(project, indexProject);
			} else {
				p2pGetProject(indexProject);
			}
		}
	}

	@Override
	public List<ProjectInvestingDto> getInvestingProjectsByEnterpriseId(
			Long enterpriseId) {
		try {
			return projectManager.getFullProjectsByEnterpriseId(enterpriseId);
		} catch (Exception e) {
			logger.error("根据企业id获取履约中的项目,enterpriseId={}", enterpriseId, e);
		}
		return null;
	}

	private EnterpriseProjectInfoDto getEnterpriseProjectInfo(Long enterpriseId)
			throws ManagerException {
		EnterpriseProjectInfoDto enterprise = new EnterpriseProjectInfoDto();
		List<ProjectInvestingDto> investingDtos = getInvestingProjectsByEnterpriseId(enterpriseId);
		// 企业投资中的项目记录
		if (Collections3.isNotEmpty(investingDtos)) {
			enterprise.setProjectInvestingDtos(investingDtos);
		}
		// 企业在不同状态的项目总额
		enterprise.setRepaymentTotalAmount(MoneyUtil.formatToWan(projectManager
				.getRepaymentTotalAmountByEnterpriseId(enterpriseId)));
		enterprise.setCurrentTotalAmount(MoneyUtil.formatToWan(projectManager
				.getCurrentTotalAmountByEnterpriseId(enterpriseId)));
		enterprise.setHistoryTotalAmount(MoneyUtil.formatToWan(projectManager
				.getHistoryTotalAmountByEnterpriseId(enterpriseId)));
		// 获取企业的可用余额
		Balance balance = balanceManager.queryBalance(enterpriseId,
				TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
		if (balance != null) {
			enterprise.setCreditAmount(MoneyUtil.formatToWan((balance
					.getBalance())));
			enterprise.setUsedCreditAmount(MoneyUtil.formatToWan(balance
					.getBalance().subtract(balance.getAvailableBalance())));
		}
		return enterprise;

	}

	@Override
	public int getProjectActivitySign(Long projectId) {
		try {
			return projectExtraManager.getProjectActivitySign(projectId);
		} catch (ManagerException e) {
			logger.error("获取特殊活动项目标识失败, projectId={}", projectId, e);
		}
		return 0;
	}

	@Override
	public boolean isBorrowerTypeEnterprise(Long projectId) {
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			if (project == null) {
				return false;
			}
			Enterprise enterprise = enterpriseManager.selectByMemberID(project
					.getBorrowerId());
			if (project.getBorrowerType() == 2 && enterprise != null) {// 企业
				return true;
			}
		} catch (ManagerException e) {
			logger.error("获取当前项目借款人是否为企业,projectId={}", projectId, e);
		}
		return false;
	}

	@Override
	public List<RecommendProjectDto> getRecommendProject(Integer num) {
		List<RecommendProjectDto> recommendProjectList = Lists.newArrayList();
		try {
			List<Project> projectList = projectManager.getRecommendProject(num);
			for (Project project : projectList) {
				RecommendProjectDto recommendProjectDto = BeanCopyUtil.map(
						project, RecommendProjectDto.class);
				recommendProjectDto
						.setAvailableBalance(getProjectBalanceById(recommendProjectDto
								.getId()));
				if (!recommendProjectDto.getIsDirectProject()) {
					recommendProjectDto.setIncomeDays(project
							.getEarningsDaysByStatus());
				}
				recommendProjectList.add(recommendProjectDto);
			}
		} catch (Exception e) {
			logger.error("获取推荐项目", e);
		}
		return recommendProjectList;
	}

	/**
	 * 分页获取项目列表数据
	 * 
	 * @return
	 */
	public ProjectPageDto<ProjectListDto> queryTransferProject(
			TransferProjectQuery transferProjectQuery) {
		ProjectPageDto<ProjectListDto> pageList = new ProjectPageDto<ProjectListDto>();
		Page<TransferProject> data;
		try {
			data = transferProjectManager
					.findTransferProjectList(transferProjectQuery);
			List<ProjectListDto> projectList = Lists.newArrayList();
			if (data != null) {
				for (TransferProject transferProject : data.getData()) {
					ProjectListDto pLD = new ProjectListDto();
					pLD.setName(transferProject.getTransferName());
					pLD.setDiscount(transferProject.getDiscount());
					pLD.setAnnualizedRateType(1);
					pLD.setMaxAnnualizedRate(transferProject
							.getTransferAnnualizedRate());
					pLD.setMinAnnualizedRate(transferProject
							.getTransferAnnualizedRate());
					// pLD.setEarningsDays(transferProject.getDays());//earnings计算得到
					pLD.setBorrowPeriod(transferProjectManager.getReturnDay(transferProject.getTransactionId()));
					pLD.setBorrowPeriodType(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY
							.getType());
					
					pLD.setStatus(transferProject.getStatus());

					BigDecimal balance = transferProjectManager.getTransferProjectBalanceById(transferProject.getId());
					pLD.setAvailableBalance(balance);
					pLD.setTotalAmount(transferProject.getSubscriptionPrincipal());
					pLD.setStartDate(transferProject.getTransferStartDate());
					pLD.setEndDate(transferProject.getTransferEndDate());
					pLD.setInterestFrom(transferProject.getInterestFrom());

					Project oriPro = projectManager
							.selectByPrimaryKey(transferProject.getProjectId());
					pLD.setThumbnail(oriPro.getThumbnail());
					pLD.setInvestType(oriPro.getInvestType());
					pLD.setId(transferProject.getId());

					pLD.setInvestNum(transferProject.getInvestNum());
					pLD.setMostInvestAmount(transferProject
							.getMostInvestAmount());
					pLD.setTotalIncome(transferProject.getTotalIncome());

					if (StatusEnum.PROJECT_STATUS_INVESTING.getStatus() == transferProject
							.getStatus()
							&& BigDecimal.ZERO.compareTo(balance) == 0) {
						int orders = orderManager
								.getTransferOrderCountByProject(transferProject
										.getId());
						pLD.setOrders(orders);
						BigDecimal orderAmount = BigDecimal.ZERO;
						orderAmount = orderManager
								.getTransferPayingAmountByProject(transferProject
										.getId());
						pLD.setOrderAmount(orderAmount);
					}

					projectList.add(pLD);
				}
				pageList.setData(projectList);
				pageList.setiDisplayLength(data.getiDisplayLength());
				pageList.setiDisplayStart(data.getiDisplayStart());
				pageList.setiTotalRecords(data.getiTotalRecords());
				pageList.setPageNo(data.getPageNo());
				pageList.setStatusCode(transferProjectQuery.getStatusCode());
			}
		} catch (ManagerException e) {
			logger.error("获取转让项目列表", e);
		}
		return pageList;

	}

	/**
	 * 转让项目详情
	 */
	@Override
	public TransferProjectBiz transferProjectDetail(Long id) {
		TransferProjectBiz detail = new TransferProjectBiz();
		try {
			TransferProject transferProject = transferProjectManager
					.selectByPrimaryKey(id);
			Project project = projectManager.selectByPrimaryKey(transferProject
					.getProjectId());
			if (project == null) {
				return detail;
			}
			detail.setProjectId(project.getId());
			detail.setEndDate(project.getEndDate());
			Transaction transaction = transactionManager
					.selectTransactionById(transferProject.getTransactionId());
			/*BigDecimal extraAnnualizedRate = (transaction
					.getExtraAnnualizedRate() != null ? transaction
					.getExtraAnnualizedRate() : new BigDecimal(0));*/
			detail.setOriginalAnnualizedRate(project.getAnnualizedRate());//展示原项目的年化
			detail.setName(project.getName());
			detail.setTransferStartDate(transferProject.getTransferStartDate());
			/*
			 * BigDecimal projectValue =
			 * transferProjectManager.getProjectValue(transferProject
			 * .getTransactionId()); detail.setProjectValue(projectValue);
			 */
			detail.setDiscount(transferProject.getDiscount());
			detail.setTransferAmount(transferProject.getTransferAmount());

		} catch (ManagerException e) {
			logger.error("获得转让项目详情异常：pid={}", id, e);
		}
		return detail;
	}

	/**
	 * 转让项目还款计划
	 */
	@Override
	public ResultDTO<Object> transferProjectrepaymentPlan(Long id) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			TransferProject transferProject = transferProjectManager
					.selectByPrimaryKey(id);
			if (transferProject == null) {
				return result;
			}

			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();

			transactionInterestQuery.setEndDate(transferProject
					.getTransferStartDate());
			transactionInterestQuery.setTransactionId(transferProject
					.getTransactionId());

			List<TransactionInterest> tranList = transactionInterestManager
					.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			/*
			 * List<DebtInterest> debtInterests =
			 * debtInterestManager.findProjectInterestsByProjectId
			 * (project.getId()); if(Collections3.isNotEmpty(debtInterests)){
			 * detail.setStartDate(debtInterests.get(0).getStartDate());
			 * detail.setEndDate
			 * ((debtInterests.get(debtInterests.size()-1).getEndDate()));
			 * detail.setInterests(debtInterests); }
			 */
			result.setResult(tranList);
		} catch (ManagerException e) {
			logger.error("获得转让项目还款计划：pid={}", id, e);
		}
		return result;
	}

	@Override
	public Object directRewardDetail(Long id) {
		ProjectForDirectReward p = new ProjectForDirectReward();
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			ProjectExtra projectExtra = projectExtraManager
					.getProjectQucikReward(project.getId());

			// 补偿人气值设置
			QuickRewardConfig quickRewardConfig = projectExtraManager
					.getQuickRewardConfig(project.getId());
			p.setQuickRewardConfig(quickRewardConfig);
			// 上线时间
			p.setOnlineTime(project.getOnlineTime());
			// 总奖金
			p.setRatioAmount(projectExtra.getExtraAmount());
			// 快投截止时间
			Integer hour = Integer.valueOf(projectExtraManager
					.getRewardHourByProjectId(project.getId()));
			p.setQuickRewardDate(DateUtils.addHour(p.getOnlineTime(), hour));

			p.setStatus(project.getStatus());
			// 各奖项奖金
			List<PrizeInPool> prizeInPoolList = projectExtraManager
					.getPrizeInPoolByProjectId(project.getId());
			List<PrizeInPool> prizeInDay = Lists.newArrayList();
			for (PrizeInPool priInDay : prizeInPoolList) {// 根据总奖金，计算各奖项金额
				PrizeInPool priNew = new PrizeInPool();
				priNew.setLevel(priInDay.getLevel());
				priNew.setNum(priInDay.getNum());
				priNew.setProportion(priInDay.getProportion());
				priNew.setRewardAmount(new BigDecimal(priInDay.getProportion())
						.multiply(p.getRatioAmount()).setScale(0,
								BigDecimal.ROUND_DOWN));
				prizeInDay.add(priNew);
			}
			p.setPrizeInPoolList(prizeInDay);

			// 抽奖次数规则
			List<LotteryRuleAmountNumber> lottery = projectExtraManager
					.getLotteryByProjectId(project.getId());
			p.setLottery(lottery);

			if (project.getStatus() == StatusEnum.PROJECT_STATUS_INVESTING
					.getStatus()
					|| project.getStatus() == StatusEnum.PROJECT_STATUS_STOP
							.getStatus()
					|| project.getStatus() == StatusEnum.PROJECT_STATUS_FULL
							.getStatus()
					|| project.getStatus() == StatusEnum.PROJECT_STATUS_AUTH_PASS
							.getStatus()
					|| project.getStatus() == StatusEnum.PROJECT_STATUS_END
							.getStatus()) {// 投资中

				p.setFlag(1);
			} else if(project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE
					.getStatus()){
				p.setFlag(2);
			} else if (project.getStatus() == StatusEnum.PROJECT_STATUS_LOSE
					.getStatus()) {// 流标
				p.setFlag(4);
			} else {// 履约中
				p.setFlag(3);
				p.setSaleComplatedTime(project.getSaleComplatedTime());
				// 各等奖对应的数量和金额
				ActivityLotteryResult record = new ActivityLotteryResult();
				record.setActivityId(projectExtra.getActivityId());
				record.setLotteryStatus(1);
				record.setRemark(project.getId().toString());
				record.setRewardResult("winnerLottery");

				// 项目下会员中奖信息
				List<ProjectForRewardMember> projectRewardList = activityLotteryResultManager
						.findProjectRewardByProjectId(record);
				for (ProjectForRewardMember biz : projectRewardList) {
					record.setMemberId(biz.getMemberId());
					List<ProjectForLevel> projectForLevelDetail = activityLotteryResultManager
							.getRewardLevelByProjectId(record);
					biz.setProjectForLevelDetail(projectForLevelDetail);
				}
				p.setProjectRewardList(projectRewardList);

			}
			return p;
		} catch (ManagerException e) {
			logger.error("获得直投项目，直投抽奖详情异常：pid={}", id, e);
		}
		return p;
	}

	@Override
	public ProjectListDto getRecommendQuickProject() {
		try {
			Long projectId = projectManager.getRecommendQuickProjectId();
			if (projectId == null || projectId <= 0l) {
				return null;
			}
			ProjectForFront data = projectManager.getRecommendQuickProject(projectId);
			ProjectListDto dto = new ProjectListDto();
			BeanCopyUtil.copy(data, dto);
			if (StatusEnum.PROJECT_STATUS_INVESTING.getStatus() == dto.getStatus()
					&& BigDecimal.ZERO.compareTo(dto.getAvailableBalance()) == 0) {
				int orders = orderManager
						.getOrderCountByProject(dto.getId());
				dto.setOrders(orders);
				BigDecimal orderAmount = BigDecimal.ZERO;
				orderAmount = orderManager.getPayingAmountByProject(dto.getId());
				dto.setOrderAmount(orderAmount);
			}
			// 获取抽奖结束时间
			String hours = projectExtraManager.getRewardHourByProjectId(dto.getId());
			Date endTime = DateUtils.addHour(dto.getOnlineTime(), Integer.valueOf(hours));
			dto.setQuickLotteryEndTime(endTime);
			if (StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus() == dto.getStatus()
					|| StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus() == dto.getStatus()) {
				// 获取中奖名单
				ActivityLotteryResult maxData = activityLotteryResultManager.getMaxRewardForQuickProject(dto.getId()
						.toString());
				if (maxData.getMemberId() > 0L) {
					dto.setQuickMaxAmount(new BigDecimal(maxData.getRewardInfo()));
					dto.setQuickMaxAmountOwner(ServletUtil.getMemberUserName(maxData.getMemberId()));
				}
			}
			this.getExtraProject(dto);
			/*Integer popularity = 0;
			// 抽奖补偿人气值
			QuickRewardConfig quickRewardConfig = projectExtraManager.getQuickRewardConfig(projectId);
			//补偿开启
			if(quickRewardConfig.isFlag()){
				//在补偿活动时间内
				if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), quickRewardConfig.getStartDate(), quickRewardConfig.getEndDate())){
					popularity = Integer.valueOf(projectExtraManager.getRewardPopularityByProjectId(dto.getId()));
				}
			}
			dto.setPopularity(popularity);*/
			return dto;
		} catch (Exception e) {
			logger.error("加载快投推荐项目异常", e);
		}
		return null;
	}

	@Override
	public  Page<ProjectPackage>  queryProjectPackageList(ProjectQuery projectQuery) {
		Page<ProjectPackage> page = new Page<ProjectPackage>();
		List<ProjectPackage> packList =projectPackageMapper.getCompletedProjectPackageList(projectQuery.getStatus(),projectQuery.getStartRow(),projectQuery.getPageSize());
		for(ProjectPackage p:packList){
			if(StringUtil.isNotBlank(p.getImgurl())){
				p.setImgurl(Config.ossPicUrl+p.getImgurl());
			}
		}
		int count =projectPackageMapper.countAllProjectPackage(projectQuery.getStatus());
		page.setData(packList);
		page.setiDisplayLength(projectQuery.getPageSize());
		page.setiDisplayStart(projectQuery.getStartRow());
		page.setiTotalRecords(count);
		page.setPageNo(projectQuery.getCurrentPage());
		return page;
	}

	@Override
	public ProjectPackageListDto<ProjectPackageLinkModel> findProjectPackageList(Long projectPackageId) {
		try{
			ProjectPackageListDto<ProjectPackageLinkModel> dto =new ProjectPackageListDto<ProjectPackageLinkModel>();
			List<ProjectPackageLinkModel> modelList =projectPackageMapper.getAppProjectPackageModelList(projectPackageId);
			for(ProjectPackageLinkModel model:modelList){
				//排序 1，优先排序：投资中>预告>已投满>履约中>已还款>已截止>已暂停>流标 2，二级排序：项目上线时间倒叙
				model.setEndTime(DateUtils.getTimeIntervalSencond(new Date(),model.getOnlineTime()));
				ProjectExtra addRateProject = projectExtraManager.getAddRateProject(model.getId());
				//额外收益
				if(addRateProject != null && addRateProject.getExtraType().intValue()==2){
					model.setAnnualizedRate(model.getAnnualizedRate().add(addRateProject.getExtraAmount()));
				}
				BigDecimal balance = getProjectBalanceById(model.getId());
				String progress = getProjectNumberProgress(model.getTotalAmount(), balance);
				model.setProgress(progress);
				if(StringUtil.isNotBlank(model.getThumbnail())){
					model.setThumbnail(Config.ossPicUrl+model.getThumbnail());
				}
				model.setCurrentDate(DateUtils.getCurrentDate());
			}
			dto.setData(modelList);
			return dto;
		}catch(Exception e){
			logger.error("获取项目包信息失败：",  e);
			return null;
		}
	}

	private String converBorrowPeriodType(int borrowPeriodType){
		String borrowPeriodTypeCN ="天";
		switch(borrowPeriodType){
		case 2:
			borrowPeriodTypeCN ="月";
			break;
		case 3:
			borrowPeriodTypeCN ="年";
			break;
		case 4:
			borrowPeriodTypeCN ="周";
			break;
		default :
		   break;
		}
		return borrowPeriodTypeCN;
	}

	@Override
	public ProjectPackage getProjectPackage(Long projectPackageId) {
		return projectPackageMapper.selectByPrimaryKey(projectPackageId);
	}

	@Override
	public int countCompletedProjectPackage(Integer status) {
		return projectPackageMapper.countAllProjectPackage(String.valueOf(status));
	}
	@Override
	public ProjectPackageListDto<ProjectPackage> queryAllProjectPackageList() {
		ProjectPackageListDto<ProjectPackage> dto = new ProjectPackageListDto<ProjectPackage>();
		List<ProjectPackage> packList =projectPackageMapper.getAllProjectPackageList(1);
		for(ProjectPackage p:packList){
			if(StringUtil.isNotBlank(p.getImgurl())){
				p.setImgurl(Config.ossPicUrl+p.getImgurl());
			}
		}
		dto.setData(packList);
		return dto;
	}

	@Override
	public String getMinRewardLimit() {
		try {
			SysDict sysDict = sysDictManager.findByGroupNameAndKey(Constants.PACKAGE_FIVE_AMOUNT_LIMIT, Constants.PACKAGE_FIVE_AMOUNT_LIMIT);
		    if(sysDict!=null){
		    	String limitAmount = sysDict.getValue();
		    	return MoneyUtil.convertNumberToWAN(limitAmount);
		    }
		} catch (ManagerException e) {
			logger.error("获取项目包信息失败：",  e);
		}
		return null;
	}
	
	
	/**
	 * 判断60亿活动是否开启,如果开启 就返回活动规则
	 * @return
	 */
	private ActivityForSixBillion  sixBillionActivityCheck(){
		Optional<Activity> activity = LotteryContainer.getInstance().getActivityByName(ActivityConstant.ACTIVITY_SIXBILLION_NAME);
		if (!activity.isPresent()) {
			return null;
		}
		Activity sixActivity = activity.get();
		if (StatusEnum.ACTIVITY_STATUS_IS_START.getStatus() != sixActivity.getActivityStatus()) {
			return null;
		}
		//获取活动规则
		ActivityForSixBillion sixBillionActivity = LotteryContainer.getInstance().getObtainConditions(sixActivity, ActivityForSixBillion.class,ActivityConstant.ACTIVITY_SIXBILLION_KEY);
		if(sixBillionActivity==null){
			return null;
		}
		return sixBillionActivity;
	}
	
	/**
	 * 应用60亿活动规则
	 * @param popularity
	 * @param sixBillionActivity
	 * @return
	 */
	private String sixActivityTurn(String popularity,ActivityForSixBillion sixBillionActivity){
		BigDecimal balanceValue = new BigDecimal(popularity);
		balanceValue = balanceValue.multiply(sixBillionActivity.getTurnTimes());
		return balanceValue.toString();
	}
}
