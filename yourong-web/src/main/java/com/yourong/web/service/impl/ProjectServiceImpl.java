package com.yourong.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.*;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.member.common.Constants;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.util.*;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.dao.OverdueRepayLogMapper;
import com.yourong.core.fin.manager.*;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.OverdueLog;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.fin.model.biz.OverdueRepayLogBiz;
import com.yourong.core.ic.dao.DebtCollateralMapper;
import com.yourong.core.ic.dao.DebtInterestMapper;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.LeaseDetailManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectInterestManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.ProjectNoticeManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.manager.BorrowerCreditGradeManager;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.LeaseDetail;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.ProjectInterest;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.ic.model.ProjectNotice;
import com.yourong.core.ic.model.QuickRewardConfig;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.BorrowerCreditGrade;
import com.yourong.core.ic.model.biz.DirectInterestForBorrow;
import com.yourong.core.ic.model.biz.LotteryRewardBiz;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.biz.ProjectForLevel;
import com.yourong.core.ic.model.biz.ProjectForLottery;
import com.yourong.core.ic.model.biz.ProjectForReward;
import com.yourong.core.ic.model.biz.ProjectForRewardDetail;
import com.yourong.core.ic.model.biz.ProjectInvestingDto;
import com.yourong.core.ic.model.biz.ProjectNoticeForFront;
import com.yourong.core.ic.model.biz.QuickProjectBiz;
import com.yourong.core.ic.dao.ProjectPackageLinkMapper;
import com.yourong.core.ic.dao.ProjectPackageMapper;
import com.yourong.core.ic.manager.*;
import com.yourong.core.ic.model.*;
import com.yourong.core.ic.model.biz.*;
import com.yourong.core.ic.model.query.ProjectBorrowQuery;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.core.ic.model.query.TransferProjectQuery;
import com.yourong.core.mc.manager.*;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.biz.ActivityProject;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.ProjectForMember;
import com.yourong.core.tc.model.biz.TransactionInterestForLateFee;
import com.yourong.core.tc.model.query.CollectingProjectQuery;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.uc.dao.EnterpriseMapper;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;
import com.yourong.web.dto.*;
import com.yourong.web.service.BalanceService;
import com.yourong.web.service.ProjectService;
import com.yourong.web.utils.SysServiceUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public  class ProjectServiceImpl implements ProjectService {
	private static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

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
	private LeaseDetailManager leaseDetailManager;
	
	@Autowired
	private ProjectExtraManager projectExtraManager;
	
	
	@Autowired
	private DebtInterestManager debtInterestManager;
	
	@Autowired
	private OverdueLogManager overdueLogManager;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private EnterpriseMapper enterpriseMapper;


	@Autowired
	private DebtCollateralMapper debtCollateralMapper;
	
	@Autowired
	private BalanceService balanceService;
	
	@Autowired
	private OverdueRepayLogManager overdueRepayLogManager;

	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	
	@Autowired
	private SinaPayClient sinaPayClient;
	
	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	
	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;

	@Autowired
	private UnderwriteLogManager underwriteLogManager;
	
	@Autowired
    private EnterpriseManager enterpriseManager;
	
	@Autowired
	private DebtInterestMapper debtInterestMapper;
	
	@Autowired
	private TransactionInterestManager transactionInterestManager;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private OverdueRepayLogMapper overdueRepayLogMapper;
	
	@Autowired
	private CouponManager couponManager;
	
	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Autowired
	private ActivityLotteryManager activityLotteryManager;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;
	
	@Autowired
	private CouponTemplateRelationManager couponTemplateRelationManager;
	
	@Autowired
	private ActivityManager activityManager;
     
	@Autowired
	private ProjectPackageMapper projectPackageMapper;
	
	@Autowired
	private ProjectPackageLinkMapper projectPackageLinkMapper;
	
	@Autowired
	private SysDictManager sysDictManager;

	@Autowired
	private BorrowerCreditGradeManager borrowerCreditGradeManager;
	
	private static String flag="1";//已阅标示
	
	private static String unFlag="0";//未读标示
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object>  findProjectListByPage(ProjectQuery projectQuery) {
		Map<String,Object> resMap =new HashMap<String,Object>();
		Page<ProjectForFront> pageList = null;
		try {
			if(projectQuery.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_NORMAL.getType()){
/*				if (projectQuery.getCurrentPage() < 1 || projectQuery.getStartRow() < 0) {
					 return resMap.put("page", new Page<ProjectForFront>());
				}*/
				resMap = projectManager.findFrontProjectListByPage(projectQuery);
				if(resMap.get("page")!=null){//直投项目
					pageList = (Page<ProjectForFront>) resMap.get("page");
					for(ProjectForFront p : pageList.getData()){
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
							p.setAddRateBigDecimal(projectExtraAddRate.getExtraAmount());
						}
						p.setExtraType(extraType);
					}
				}
			}
			if(projectQuery.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			/*	if (projectQuery.getCurrentPage() < 1 || projectQuery.getStartRow() < 0) {
					return new Page<ProjectForFront>();
				}*/
				pageList = this.queryTransferProject(projectQuery);
				resMap.put("page", pageList);
			}
		} catch (ManagerException e) {
			logger.error("获取项目列表异常", e);
		}
		return resMap;
	}

	@Override
	public ProjectInfoDto getProjectInfoById(Long id) {
		ProjectInfoDto projectInfoDto = null;
		ProjectNotice projectNotice = null;
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			// 只可查看已发布后的项目
			if (project != null && project.getStatus() >= StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus()) {
				if (project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus()) {
					projectNotice = projectNoticeManager.getProjectNoticingByProjectId(project.getId());
					if (projectNotice == null) {
						return projectInfoDto;
					}
				}
				projectInfoDto = BeanCopyUtil.map(project, ProjectInfoDto.class);
				DebtBiz biz = debtManager.getFullDebtInfoById(project.getDebtId());
				DebtDto debtDto = BeanCopyUtil.map(biz, DebtDto.class);
				projectInfoDto.setDebtDto(debtDto);
				List<BscAttachment> bscAttachments = bscAttachmentManager.findAttachmentsByKeyId(String.valueOf(project.getId()));
				if (!Collections3.isEmpty(bscAttachments)) {
					// projectInfoDto.setBscAttachments(bscAttachments);
					assignationAttachment(bscAttachments, projectInfoDto);
				}
				if (project.getAnnualizedRateType() == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getStatus()) {
					// 阶段收益
					List<ProjectInterest> projectInterestList = projectInterestManager.getProjectInterestByProjectId(project.getId());
					projectInfoDto.setProjectInterestList(projectInterestList);
				}
				if (projectNotice != null) {

				}
				// 获取租赁分红结算记录
				List<LeaseDetail> leaseDetails = leaseDetailManager.findListByProjectId(id);
				if (Collections3.isNotEmpty(leaseDetails)) {
					projectInfoDto.setLeaseDetails(leaseDetails);
				}
				
				if(project.getProjectType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode()) && biz.getEnterpriseId()!=null){
					projectInfoDto.setEnterpriseProjectInfoDto(getEnterpriseProjectInfo(biz.getEnterpriseId()));
				}

			}
		} catch (ManagerException e) {
			logger.error("查看项目" + id + "异常", e);
		}
		return projectInfoDto;
	}

	@Override
	public String getProjectImagePath(Long id) {
		try {
			BscAttachment attachment = bscAttachmentManager.getBscAttachmentByKeyIdAndModule(id.toString(), "debt_collateral");
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
				Balance _balance = balanceManager.queryBalance(id, TypeEnum.BALANCE_TYPE_PROJECT);
				if (_balance != null) {
					availableBalance = _balance.getAvailableBalance();
				} else {
					logger.debug("项目" + id + "，可用余额在余额表未找到。");
				}
			}
			if (availableBalance == null) {
				// 再没有，那只能从项目中去找了
				Project project = projectManager.selectByPrimaryKey(id);
				if(project!=null){
					availableBalance = project.getTotalAmount();
				}else{
					availableBalance = transferProjectManager.getTransferProjectBalanceById(id);
				}

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
	private void assignationAttachment(List<BscAttachment> bscAttachments, ProjectInfoDto projectInfoDto) {
		List<BscAttachment> collateralAttachments = projectInfoDto.getCollateralAttachments();
		List<BscAttachment> lenderMemberAttachments = projectInfoDto.getLenderMemberAttachments();
		List<BscAttachment> borrowMemberAttachments = projectInfoDto.getBorrowMemberAttachments();
		List<BscAttachment> contractAttachments = projectInfoDto.getContractAttachments();
		List<String> contractCategoryName = projectInfoDto.getContractCategoryName();
		for (BscAttachment attachment : bscAttachments) {
			String module = attachment.getModule();
			if (module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_COLLATERAL.getCode())) {// 质押或抵押物附件
				collateralAttachments.add(attachment);
			} else if (module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_LENDER.getCode())) {// 原始债权人附件
				lenderMemberAttachments.add(attachment);
			} else if (module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_BORROWER.getCode())) {// 借款人附件
				borrowMemberAttachments.add(attachment);
			} else if (module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_CONTRACT.getCode()) // 合同图片
					|| module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_LEGAL.getCode())// 法律意见书
					|| module.equals(DebtEnum.ATTACHMENT_MODULE_DEBT_BASE.getCode())) {// 其他资料图片
				String categoryName = DebtEnum.getEnumByCode(module).getDesc();
				attachment.setModule(categoryName);
				contractAttachments.add(attachment);
				if (!contractCategoryName.contains(categoryName)) {
					contractCategoryName.add(categoryName);
				}
			}
		}
	}

	@Override
	public ResultDO<ProjectForFront> findIndexProjectList(int default_show_number) {
		return findProjectList(default_show_number,null);
	}

	@Override
	public ResultDO<ProjectForFront> findLandProjectList() {
		return findProjectList(4,null);
	}

	private ResultDO<ProjectForFront> findProjectList(Integer number,Integer investType) {
		ResultDO<ProjectForFront> result = new ResultDO<ProjectForFront>();
		List<ProjectForFront> projectForFrontList = Lists.newArrayList();
		try {
			if(investType==null){
				// 首位，预告
				ProjectNoticeForFront projectNoticeForFront = projectNoticeManager.getProjectNoticeByIndexShow();
				if (projectNoticeForFront != null) {
					ProjectForFront project = projectManager.getProjectForFrontByProjectId(projectNoticeForFront.getProjectId());
					if (project != null && project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus()) {
						projectForFrontList.add(project);
					}
				}
				// 新客项目优先
				List<ProjectForFront> noviceProject = projectManager.findIndexNoviceProject(1);
				if (Collections3.isNotEmpty(noviceProject)) {
					projectForFrontList.addAll(noviceProject);
				}
			}

			// 推荐位优先
			List<ProjectForFront> recommendProjectForList = projectManager.findRecommendProjectList(number - projectForFrontList.size(),investType);
			if (Collections3.isNotEmpty(recommendProjectForList)) {
				projectForFrontList.addAll(recommendProjectForList);
			}

			// 常规投资中的项目
			if (projectForFrontList.size() < number) {
				List<ProjectForFront> investingProjectForList = projectManager.findIndexInvestingProjectList(number
						- projectForFrontList.size(),investType);
				if (Collections3.isNotEmpty(investingProjectForList)) {
					projectForFrontList.addAll(investingProjectForList);
				}
			}

			// 不足8个项目，再上履约或还款中的项目
			if (projectForFrontList.size() < number) {
				List<ProjectForFront> notInvestingProjectForList = projectManager.findIndexNotInvestingProjectList(number
						- projectForFrontList.size(),investType);
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
					p.setAddRateBigDecimal(projectExtraAddRate.getExtraAmount());
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
			List<ProjectForFront> projectForFrongList = projectManager.getNoviceProjectByInvesting(1);
			if (Collections3.isNotEmpty(projectForFrongList)) {
				return projectForFrongList.get(0);
			} else {
				projectForFrongList = projectManager.getNoviceProjectByNotInvesting(1);
				if (Collections3.isNotEmpty(projectForFrongList)) {
					return projectForFrongList.get(0);
				}
			}
		} catch (ManagerException e) {
			logger.error("获得在首页显示的新手项目异常", e);
		}
		return null;
	}

	public JSONObject getProjectListToZhongNiu() {
		JSONObject jo = new JSONObject();
		try {
			List<Project> projectList = projectManager.getProjectListToZhongNiu();
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
					znp.setProgress(getProjectNumberProgress(p.getTotalAmount(), balance));
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
	public String getProjectNumberProgress(BigDecimal totalAmount, BigDecimal availableBalance) {
		String progress = "0";
		if (availableBalance != null) {
			if (availableBalance.compareTo(BigDecimal.ZERO) <= 0) {
				progress = "100";
			} else if (availableBalance.compareTo(totalAmount) == 0) {
				progress = "0";
			} else {
				progress = new DecimalFormat("###.##").format((totalAmount.subtract(availableBalance)).divide(totalAmount, 4,
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
				BigDecimal amounted = project.getTotalAmount().subtract(balance);
				ZhongNiuProjectDetail detail = new ZhongNiuProjectDetail();
				detail.setPid(project.getId());
				detail.setName(project.getName());
				detail.setType(7);// 债权标
				detail.setYield(project.getMaxAnnualizedRate().toString());
				detail.setDuration(DateUtils.getIntervalMonth(project.getStartDate(), project.getEndDate()));// 投资期限（单位：月），精确到小数点2位
				detail.setRepaytype(1);// 按月付息 到期还本
				detail.setGuaranttype(1);// 本息但保
				detail.setThreshold(project.getMinInvestAmount().longValue());
				detail.setStatus(project.getStatus());
				detail.setAmount(project.getTotalAmount().longValue());
				detail.setAmounted(amounted.longValue());
				detail.setUrl(PropertiesUtil.getWebRootUrl() + "/products/detail-" + project.getId() + ".html");
				detail.setProgress(getProjectNumberProgress(project.getTotalAmount(), balance));
				detail.setStartdate(DateUtils.formatDatetoString(project.getOnlineTime(), "yyyy-MM-dd"));
				detail.setEnddate(DateUtils.formatDatetoString(project.getSaleEndTime(), "yyyy-MM-dd"));
				detail.setPublishtime(DateUtils.formatDatetoString(project.getOnlineTime(), "yyyy-MM-dd HH:mm:ss"));

				DebtBiz debt = debtManager.getDebtInfoToZhongNiu(project.getDebtId());

				JSONObject xmjs = new JSONObject();
				xmjs.put("title", "项目介绍");
				JSONObject xmjsContent = new JSONObject();
				xmjsContent.put("content", project.getShortDesc());

				JSONObject jbxx = new JSONObject();
				jbxx.put("title", detail.getBaseInfoTitel(debt.getGuarantyType()));
				JSONObject jbxxContent = new JSONObject();
				if (debt.getDebtCollateral() != null) {
					jbxxContent.put("content",
							detail.formatContent(debt.getDebtCollateral().getCollateralDetails(), debt.getGuarantyType()));
				} else {
					jbxxContent.put("content", detail.formatContent(debt.getDebtPledge().getPledgeDetails(), debt.getGuarantyType()));
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

				List<BscAttachment> bscAttachments = bscAttachmentManager.findAttachmentsByKeyIdAndModule(project.getId().toString(),
						DebtEnum.ATTACHMENT_MODULE_DEBT_COLLATERAL.getCode(), -1);
				for (BscAttachment att : bscAttachments) {
					JSONObject jimage = new JSONObject();
					jimage.put("image", Config.ossPicUrl + StringUtil.getFilePath(att.getFileUrl(), "425"));
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
	public List<PinYouProject> queryInvestingProjectToPinYou() {
		List<PinYouProject> pypList = Lists.newArrayList();
		try {
			List<ProjectForFront> list = projectManager.queryInvestingProject();
			if (Collections3.isNotEmpty(list)) {
				for (ProjectForFront p : list) {
					BigDecimal balance = getProjectBalanceById(p.getId());
					PinYouProject pyp = new PinYouProject();
					pyp.setName(p.getName());
					pyp.setRepaymentTime(DateUtils.formatDatetoString(p.getEndDate(), "yyyy-MM-dd"));
					pyp.setProgress(getProjectNumberProgress(p.getTotalAmount(), balance) + "%");
					pyp.setTotalProject(p.getTotalAmount());
					pyp.setType("债权");
					pyp.setAnnualRevenue(p.getMaxAnnualizedRate());
					String kind = SysServiceUtils.getDictLabelByKey(p.getProjectType(), "guaranty_type", "车有融");
					if (kind.endsWith("项目")) {
						kind = kind.substring(0, kind.length() - 2);
					}
					pyp.setKind(kind);
					pyp.setUrl(PropertiesUtil.getWebRootUrl() + "/products/detail-" + p.getId() + ".html");
					pyp.setPicture(Config.ossPicUrl + p.getThumbnail());
					pyp.setPaymentMethod(SysServiceUtils.getDictLabelByKey(p.getProfitType(), "return_type", ""));
					pyp.setIncomeDay(p.getEarningsDaysByStatus());
					pyp.setProductNo(p.getId());
					String status = p.getButtonText();
					if (status != "") {
						if (status.equals("立即投资")) {
							status = "投资中";
						}
					}
					pyp.setStatus(status);
					pypList.add(pyp);
				}
			}
		} catch (ManagerException e) {
			logger.error("获得正在投资中的项目异常", e);
		}
		return pypList;
	}

	@Override
	public int queryInvestingProjectCount() {
		try {
			List<ProjectForFront> list = projectManager.queryInvestingProject();
			return list.size();
		} catch (ManagerException e) {
			logger.error("获得投资中的项目个数异常", e);
		}
		return 0;
	}
	

	@Override
	public int isInvestingProjectByProject() {
		try {
			List<Project> list = projectManager.isInvestingProjectByProject();
			return list.size();
		} catch (ManagerException e) {
			logger.error("获得投资中的项目,纯项目校验,个数异常", e);
		}
		return 0;
	}
	
	
	

	@Override
	public List<CnGoldProjectDto> queryCnGoldProjectList() {
		List<CnGoldProjectDto> cngoldList = Lists.newArrayList();
		try {
			List<ProjectForFront> projectList = projectManager.queryInvestingProject();
			if (Collections3.isNotEmpty(projectList)) {
				for (ProjectForFront project : projectList) {
					BigDecimal balance = getProjectBalanceById(project.getId());
					CnGoldProjectDto dto = new CnGoldProjectDto();
					dto.setProductId(project.getId());
					dto.setTitle(project.getName());
					dto.setAmount(project.getTotalAmount());
					dto.setComplete(getProjectNumberProgress(project.getTotalAmount(), balance));
					dto.setProfit(project.getMaxAnnualizedRate());
					dto.setDeadline(project.getEarningsDaysByStatus());
					dto.setDeadlineUnit("天");
					dto.setReward(0);
					dto.setPlatformId(301);
					dto.setShortName("有融网");
					if (project.getDebtType().equals("collateral")) {
						dto.setType("1");// 抵押
					} else {
						dto.setType("2");// 质押
					}
					dto.setPayOption(3);
					dto.setUserName(project.getBorrowerId().toString());
					dto.setLoanUrl(PropertiesUtil.getWebRootUrl() + "/products/detail-" + project.getId() + ".html");
					if (project.getStatus() > 40) {
						dto.setSuccessTime(DateUtils.formatDatetoString(project.getEndDate(), "yyyy-MM-dd HH:mm:ss"));
					}
					dto.setPublishTime(DateUtils.formatDatetoString(project.getOnlineTime(), "yyyy-MM-dd HH:mm:ss"));
					cngoldList.add(dto);
				}
			}
		} catch (ManagerException e) {
			logger.error("获得金投网的数据接口异常", e);
		}

		return cngoldList;
	}

	@Override
	public ProjectForFront findLandingRecommendProjectProject() {
		try {
			return projectManager.findLandingRecommendProject();
		} catch (ManagerException e) {
			logger.error("获取着陆页推荐项目异常", e);
		}
		return null;
	}

	@Override
	public boolean isExistProjectByType(String projectType, Integer instalment, String debtType) {
		try {
			return projectManager.isExistProjectByType(projectType, instalment, debtType);
		} catch (ManagerException e) {
			logger.error("判断类目下是否存在项目", e);
		}
		return false;
	}

	@Override
	public ResultDO<ProjectForFront> getNewCustomerProject() {
		ResultDO<ProjectForFront> res = new ResultDO<ProjectForFront>();
		try {
			ProjectForFront newCustomerPorject = null;
			String redisKey = RedisConstant.REDIS_KEY_LANDING + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_LANDING_NEWCUSTOMER;
			boolean isExit = RedisManager.isExitByObjectKey(redisKey);
			if (isExit) {
				newCustomerPorject = (ProjectForFront) RedisManager.getObject(redisKey);
			} else {
				newCustomerPorject = getIndexNoviceProject();
				if (newCustomerPorject != null) {
					// 获取进度
					newCustomerPorject.setProcess(SysServiceUtils.getProjectProgress(newCustomerPorject.getTotalAmount(),
							newCustomerPorject.getId()));
					// 获取剩余可投金额
					newCustomerPorject.setAvailableBalance(SysServiceUtils.getProjectBalance(newCustomerPorject.getId()));
					// 获取进度条
					newCustomerPorject.setRound(SysServiceUtils.getProgressCeil(newCustomerPorject.getTotalAmount(),
							newCustomerPorject.getId()));
					RedisManager.putObject(redisKey, newCustomerPorject);
					RedisManager.expireObject(redisKey, 1200);
				}
			}
			res.setResult(newCustomerPorject);
			res.setSuccess(true);
			return res;
		} catch (Exception e) {
			logger.error("获取新客项目失败", e);
			res.setSuccess(false);
			return res;
		}
	}

	@Override
	public ResultDO<ProjectForFront> getRecommendProject() {
		ResultDO<ProjectForFront> res = new ResultDO<ProjectForFront>();
		try {
			ProjectForFront recommendProject = null;
			String redisKey = RedisConstant.REDIS_KEY_LANDING + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_LANDING_RECOMMEND;
			boolean isExit = RedisManager.isExitByObjectKey(redisKey);
//			if (isExit) {
//				recommendProject = (ProjectForFront) RedisManager.getObject(redisKey);
//logger.info(isExit+"--"+redisKey+"--"+ recommendProject.getBorrowPeriod()+"--"+recommendProject.getBorrowPeriodType());
//			} else {
				recommendProject = findLandingRecommendProjectProject();
				if (recommendProject != null) {
					// 获取进度
					recommendProject.setProcess(SysServiceUtils.getProjectProgress(recommendProject.getTotalAmount(),
							recommendProject.getId()));
					// 获取剩余可投金额
					recommendProject.setAvailableBalance(SysServiceUtils.getProjectBalance(recommendProject.getId()));
					// 获取进度条
					recommendProject.setRound(SysServiceUtils.getProgressCeil(recommendProject.getTotalAmount(), recommendProject.getId()));
					RedisManager.putObject(redisKey, recommendProject);
					RedisManager.expireObject(redisKey, 1200);
				}
//			}
			res.setResult(recommendProject);
			res.setSuccess(true);
			return res;
		} catch (Exception e) {
			logger.error("获取着陆页推荐项目失败", e);
			res.setSuccess(false);
			return res;
		}
	}

	@Override
	public ResultDO<ProjectForFront> getRecommendProjectByGuaranty(Map<String, Object> paraMap) {
		ResultDO<ProjectForFront> res = new ResultDO<ProjectForFront>();
		try {
			List<ProjectForFront> recommendProjectList = null;
			String redisKey = RedisConstant.REDIS_KEY_LANDING + RedisConstant.REDIS_SEPERATOR + paraMap.get("redisKey").toString();
			boolean isExit = RedisManager.isExitByObjectKey(redisKey);
			if (isExit) {
				recommendProjectList = (List<ProjectForFront>) RedisManager.getObject(redisKey);
			} else {
				recommendProjectList = projectManager.getRecommendProjectByGuaranty(paraMap);
				if (Collections3.isNotEmpty(recommendProjectList)) {
					for (ProjectForFront recommendProject : recommendProjectList) {
						// 获取进度
						recommendProject.setProcess(SysServiceUtils.getProjectProgress(recommendProject.getTotalAmount(),
								recommendProject.getId()));
						// 获取剩余可投金额
						recommendProject.setAvailableBalance(SysServiceUtils.getProjectBalance(recommendProject.getId()));
						// 获取进度条
						recommendProject.setRound(SysServiceUtils.getProgressCeil(recommendProject.getTotalAmount(),
								recommendProject.getId()));
					}
					RedisManager.putObject(redisKey, recommendProjectList);
					RedisManager.expireObject(redisKey, 1200);
				}
			}
			res.setResultList(recommendProjectList);
			res.setSuccess(true);
			return res;
		} catch (Exception e) {
			logger.error("获取着陆页推荐项目失败", e);
			res.setSuccess(false);
			return res;
		}
	}

	@Override
	public boolean isProjectOfCannotUseCoupon(Long projectId) {
		try {
			return projectManager.isProjectOfCannotUseCoupon(projectId);
		} catch (ManagerException e) {
			logger.error("判断是否为不可使用优惠券的项目类型失败,projectId={}", projectId, e);
		}
		return false;
	}

	@Override
	public boolean isProjectOfCannotUseProfitCoupon(Long projectId) {
		try {
			return projectManager.isProjectOfCannotUseProfitCoupon(projectId);
		} catch (ManagerException e) {
			logger.error("判断是否为不可使用收益券的项目类型失败,projectId={}", projectId, e);
		}
		return false;
	}

	@Override
	public boolean isBuyCar(Long projectId) {
		try {
			return projectManager.isBuyCar(projectId);
		} catch (ManagerException e) {
			logger.error("判断是否为购车分期项目失败,projectId={}", projectId, e);
		}
		return false;
	}

	@Override
	public List<ProjectInvestingDto> getInvestingProjectsByEnterpriseId(Long enterpriseId) {
		try {
			return projectManager.getFullProjectsByEnterpriseId(enterpriseId);
		} catch (Exception e) {
			logger.error("根据企业id获取履约中的项目,enterpriseId={}",enterpriseId,e);
		}
		return null;
	}
	
	private EnterpriseProjectInfoDto getEnterpriseProjectInfo(Long enterpriseId) throws ManagerException{
		EnterpriseProjectInfoDto enterprise = new EnterpriseProjectInfoDto();
		List<ProjectInvestingDto> investingDtos = getInvestingProjectsByEnterpriseId(enterpriseId);
		//企业投资中的项目记录
		if(Collections3.isNotEmpty(investingDtos)){
			enterprise.setProjectInvestingDtos(investingDtos);
		}
		//企业在不同状态的项目总额
		enterprise.setRepaymentTotalAmount(MoneyUtil.formatToWan(projectManager.getRepaymentTotalAmountByEnterpriseId(enterpriseId)));
		enterprise.setCurrentTotalAmount(MoneyUtil.formatToWan(projectManager.getCurrentTotalAmountByEnterpriseId(enterpriseId)));
		enterprise.setHistoryTotalAmount(MoneyUtil.formatToWan(projectManager.getHistoryTotalAmountByEnterpriseId(enterpriseId)));
		//获取企业的可用余额
		Balance balance = balanceManager.queryBalance(enterpriseId, TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
		if(balance!=null){
			enterprise.setCreditAmount(MoneyUtil.formatToWan((balance.getBalance())));
			enterprise.setUsedCreditAmount(MoneyUtil.formatToWan(balance.getBalance().subtract(balance.getAvailableBalance())));
		}
		return enterprise;
		
	}
	/**
	 * 
	 * @desc 借款中心数据统计
	 * @param memberId
	 * @return
	 * @author chaisen
	 * @time 2016年1月26日 下午4:43:05
	 *
	 */
	@Override
	public ResultDO getBorrowCenterData(Long memberId) {
		ResultDO result = new ResultDO();
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("borrowerId", memberId);
			ProjectBorrowQuery query=new ProjectBorrowQuery();
			query.setBorrowerId(memberId);
			query.setStatus(StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus());
			/*List<ProjectInterestBiz> projectForFrontList = projectManager.selectForPaginPayingBorrow(query);
			int totalPerfom=0;
			if(Collections3.isNotEmpty(projectForFrontList)){
				totalPerfom=projectForFrontList.size();
			}*/
			int totalPerfom = projectManager.selectForPaginCountPayingBorrow(query);
			//int totalPerfom = projectManager.selectForPaginCountPayingBorrow(query);
			//逾期本金
			BigDecimal overduePayablePrincipal = BigDecimal.ZERO;
			//逾期利息
	  	    BigDecimal overduePayableInterest = BigDecimal.ZERO;	
	  	    //待支付本金
			BigDecimal waitPayablePrincipal = BigDecimal.ZERO;
			//待支付利息
	  	    BigDecimal waitPayableInterest = BigDecimal.ZERO;
			//DebtInterest debtInterest=debtInterestManager.selectWaitPayAmountByBorrowerId(map);
			OverdueLog overAmount=overdueLogManager.selectOverduePayAmountByMemberId(memberId);
			DebtInterest waitInterest=debtInterestManager.selectWaitPayAmountByMemberId(memberId);
			 //待还本金总计
			 BigDecimal totalPayablePrincipal = BigDecimal.ZERO;
			 //待还利息总计
	  	     BigDecimal totalPayableInterest = BigDecimal.ZERO;
	  	     //代还本息总计
	  	     BigDecimal payableTotal = BigDecimal.ZERO;
	  	     
	  	     //逾期未还的
			 if(overAmount!=null){
				 overduePayablePrincipal=overAmount.getOverduePrincipal();
				 overduePayableInterest=overAmount.getOverdueInterest();
			 }
			 //待还款的本金和利息
			 if(waitInterest!=null){
				 waitPayablePrincipal=waitInterest.getPayablePrincipal();
				 waitPayableInterest=waitInterest.getPayableInterest();
			 }
			 totalPayablePrincipal=overduePayablePrincipal.add(waitPayablePrincipal);
			 totalPayableInterest=overduePayableInterest.add(waitPayableInterest);
			 payableTotal=totalPayablePrincipal.add(totalPayableInterest);
			 Map<String, Object> borrowMap = Maps.newHashMap();
			 borrowMap.put("totalPerfom", totalPerfom);
			 borrowMap.put("totalPayablePrincipal", totalPayablePrincipal);
			 borrowMap.put("totalPayableInterest", totalPayableInterest);
			 borrowMap.put("payableTotal", payableTotal);
	  	     result.setResult(borrowMap);
	  	     
		} catch (ManagerException e) {
			logger.error("借款中心数据异常={}", memberId);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}
	/**
	 * 
	 * @desc 获取借款列表
	 * @param query
	 * @return
	 * @author chaisen
	 * @time 2016年1月26日 下午7:51:00
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> getBorrowList(ProjectBorrowQuery query) {
		try {
			return projectManager.getBorrowList(query);
		} catch (ManagerException e) {
			logger.error("获取借款列表失败,query={}", query, e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 还款中的借款
	 * @param query
	 * @return
	 * @author chaisen
	 * @time 2016年3月2日 上午9:36:11
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> getPayingBorrowList(ProjectBorrowQuery query) {
		try {
			return projectManager.getPayingBorrowList(query);
		} catch (ManagerException e) {
			logger.error("获取还款中的借款列表失败,query={}", query, e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 获取本息记录
	 * @param projectId
	 * @return
	 * @author chaisen
	 * @time 2016年1月26日 下午7:51:16
	 *
	 */
	@Override
	public List<DirectInterestForBorrow> selectInterestByProjectId(Long projectId) {
		try {
			List<DirectInterestForBorrow> interestList = Lists.newArrayList();
			Map<String, Object> map = Maps.newHashMap();
			map.put("projectId", projectId);
			List<DebtInterest> list=debtInterestManager.findInterestlistByProjectId(projectId);
			if (Collections3.isNotEmpty(list)) {
				for(DebtInterest interest:list){
					DirectInterestForBorrow biz=getInterestInfo(interest);
					interestList.add(biz);
				}
			}
			return interestList;
		} catch (ManagerException e) {
			logger.error("获取本息记录失败,projectId={}", projectId, e);
		}
		return null;
	}
	/**
	 * 
	 * @Description:获取逾期状态
	 * @param interest
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月2日 上午11:52:30
	 */
	private DirectInterestForBorrow getInterestInfo(DebtInterest interest) throws ManagerException {
		DirectInterestForBorrow biz =new DirectInterestForBorrow();
		
		BigDecimal principal = BigDecimal.ZERO;
		BigDecimal interests = BigDecimal.ZERO;
		//还款状态
		biz.setStatus(interest.getStatus());
		//还款时间
		biz.setRefundTime(interest.getEndDate());
		//逾期记录数
		Map<String, Object> map = Maps.newHashMap();
		map.put("interestId", interest.getId());
		
		if(interest.getStatus()==1){
			biz.setOverdueStatus(StatusEnum.INTEREST_RECORD_HADPAY.getStatus());
		}else if(interest.getStatus()==0){
			int i=overdueLogManager.getCountOverdueRecordByInterestId(interest.getId(),1);
			if(i>0){
				biz.setOverdueStatus(StatusEnum.INTEREST_RECORD_UNDERWRITE.getStatus());
			}else{
				biz.setOverdueStatus(StatusEnum.INTEREST_RECORD_WAITPAY.getStatus());
			}
		}else if(interest.getStatus()==3){
			int i=overdueLogManager.getCountOverdueRecordByInterestId(interest.getId(),2);
			if(i>0){
				biz.setOverdueStatus(StatusEnum.INTEREST_RECORD_OVERDUE.getStatus());
			}
		}
		/*else{
			//正常还款
			if(countOverdue<1){
				biz.setOverdueStatus(StatusEnum.INTEREST_OVERDUE_REMARK_NORMALPAY.getStatus());
			}else{
				map.put("interestId", interest.getId());
				map.put("status", 1);
				int count=overdueLogManager.countOverdueRecordByInterestId(map);
				map.put("status", 2);
				int counts=overdueLogManager.countOverdueRecordByInterestId(map);
				if(count>0){
					biz.setOverdueStatus(StatusEnum.INTEREST_OVERDUE_REMARK_UNDERPPAY.getStatus());
				}else if(counts>0){
					biz.setOverdueStatus(StatusEnum.INTEREST_OVERDUE_REMARK_UNDERHADPAY.getStatus());
				}else{
					biz.setOverdueStatus(StatusEnum.INTEREST_OVERDUE_REMARK_NO.getStatus());
				}
			}
		}*/
		if(interest.getPayablePrincipal()!=null){
			principal=interest.getPayablePrincipal();
		}
		if(interest.getPayableInterest()!=null){
			interests=interest.getPayableInterest();
		}
		if(principal.compareTo(BigDecimal.ZERO)>0&&interests.compareTo(BigDecimal.ZERO)>0){
			biz.setPayTypes(1);
		}
		if(principal.compareTo(BigDecimal.ZERO)==0&&interests.compareTo(BigDecimal.ZERO)>0){
			biz.setPayTypes(2);
		}
		//还款金额
		biz.setPayableAmount(interest.getPayablePrincipal().add(interest.getPayableInterest()));
		//是否垫资
		int i=overdueLogManager.getOverdueLogByInterestIdAndtype(interest.getId(),1);
		if(i>0){
			biz.setUnderwrite(1);
		}
		//是否逾期
		int j=overdueLogManager.getOverdueLogByInterestIdAndtype(interest.getId(),2);
		if(j>0){
			biz.setOverdue(1);
		}
		return biz;
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
	public ResultDO<Object> firstInvestProject() {
		ResultDO<Object> rDO = new ResultDO<Object>();
		ProjectForFront recommendProject = new ProjectForFront();
		try {
			// 获取推荐项目
			String redisKey = RedisConstant.REDIS_KEY_FIRST_INVEST;
			boolean isExit = RedisManager.isExitByObjectKey(redisKey);
			if (isExit) {
				recommendProject = (ProjectForFront) RedisManager.getObject(redisKey);
			} else {
				List<ProjectForFront> projectForFrongList = projectManager.getFirstInvestProject(1);
				if (Collections3.isNotEmpty(projectForFrongList)) {
					recommendProject = projectForFrongList.get(0);
					// 获取进度
					recommendProject.setProcess(SysServiceUtils.getProjectProgress(recommendProject.getTotalAmount(),
							recommendProject.getId()));
					// 获取剩余可投金额
					recommendProject.setAvailableBalance(SysServiceUtils.getProjectBalance(recommendProject.getId()));
					// 获取进度条
					recommendProject.setRound(SysServiceUtils.getProgressCeil(recommendProject.getTotalAmount(), recommendProject.getId()));
					RedisManager.putObject(redisKey, recommendProject);
					RedisManager.expireObject(redisKey, 120);
				} else {
					rDO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
					return rDO;
				}
			}
			rDO.setResult(recommendProject);
		} catch (Exception e) {
			logger.error("获取首次投资推荐项目失败", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	/**
	 * 
	 * @desc 逾期还款记录
	 * @param projectId
	 * @return
	 * @author chaisen
	 * @time 2016年2月2日 上午10:36:34
	 *
	 */
	@Override
	public List<OverdueRepayLogBiz> getOverdueByProjectId(Long projectId,int type) {
		try {
			List<OverdueRepayLogBiz> interestList = Lists.newArrayList();
			List<OverdueRepayLog> list=projectManager.getOverdueRepayLogRecordByProjectIdAndType(projectId,type);
			if (Collections3.isNotEmpty(list)) {
				for(OverdueRepayLog overdue:list){
					OverdueRepayLogBiz biz =new OverdueRepayLogBiz();
					BeanCopyUtil.copy(overdue,biz);
					interestList.add(biz);
				}
			}
			return interestList;
		} catch (ManagerException e) {
			logger.error("获取逾期还款记录失败,projectId={}", projectId, e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 获取滞纳金，逾期利息，逾期本金
	 * @param projectId
	 * @return
	 * @author chaisen
	 * @time 2016年2月2日 下午2:21:05
	 *
	 */
	@Override
	public ResultDO getOverdueInfo(Long projectId) {
		ResultDO result = new ResultDO();
		try {
			Map<String, Object> map = Maps.newHashMap();
			map=projectManager.getOverdueInfoByProjectId(projectId);
	  	    result.setResult(map);
		} catch (Exception e) {
			logger.error("还款信息数据异常={}", projectId);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}

	/**
	 * @desc 获取项目详情-总
	 * @param projectId
	 * @return
	 * @author fuyili
	 * @time 2016年1月26日 下午1:51:46
	 *
	 */
	@Override
	public ProjectInfoDto getAllProjectInfo(Long projectId,Long memberId) {
		ProjectInfoDto projectInfoDto = new ProjectInfoDto();
		projectInfoDto.setId(projectId);
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			if(project==null || project.getDelFlag()<1){
				TransferProject transferProject = transferProjectManager.selectByPrimaryKey(projectId);
				if (transferProject ==null|| transferProject.getDelFlag()<1){
					return null;
				}
				projectInfoDto = this.getTransferProjectInfoById(transferProject, memberId);
				return projectInfoDto;
			}
			Integer daysScope = 0;
			if (project.getInvestType().equals(ProjectEnum.PROJECT_TYPE_DIRECT.getType())) {
				
				//获取直投项目详情
				projectInfoDto = getProjectP2pInfoById(project,memberId);
				if (memberId > 0l) {
					// 获取收益天数
					daysScope = project.countProjectDays();

				}
			} else {
				projectInfoDto = getProjectInfoById(projectId);
				if (memberId > 0l) {
				//获取收益天数
					daysScope = projectInfoDto.getEarningsDaysByStatus();
				}
			}
			projectInfoDto.setProjectCategory(TypeEnum.PROJECT_CATEGORY_NORMAL.getType());
			projectInfoDto.setTransferFlag(project.getTransferFlag());
			if (memberId > 0L && daysScope > 0) {
				// 非限制使用优惠券用户
				if (couponManager.useCouponSpecialLimit(memberId)) {
					List<Coupon> coupons = couponManager.getUsableAndLimitedCoupons(memberId, TypeEnum.COUPON_TYPE_INCOME.getType(),
							CouponEnum.COUPON_CLIENT_WEB.getCode(), project.getTotalAmount(), daysScope);
					if (CollectionUtils.isNotEmpty(coupons)) {
						projectInfoDto.setCoupons(coupons);
					}
				}
			}
			if (projectInfoDto.isNoviceProject() && memberId != null && memberId > 0l) {
				// 判断新客校验
				boolean checkNoviceFlag = memberManager.needCheckNoviceProject(memberId);
				if (!checkNoviceFlag) {
					projectInfoDto.setNoCheckNovice(true);
				}
			}
		} catch (ManagerException e) {
			logger.error("获取项目详情失败,projectId={}", projectId, e);
		}
		return projectInfoDto;
	}
	
	
	private void getExtraProject(ProjectInfoDto p2pProject) throws ManagerException{
		
		p2pProject.setIsQuickLottery(false);
		//快投有奖项目显示详情 
		ProjectExtra pro = projectExtraManager.getProjectQucikReward(p2pProject.getId());
		if(pro.getActivitySign()!=null&&pro.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
			p2pProject.setIsQuickLottery(true);
			List<LotteryRuleAmountNumber> lotteryRuleList =projectExtraManager.getLotteryByProjectId(p2pProject.getId());
			p2pProject.setLotteryRuleList(lotteryRuleList);
			
			p2pProject.setDirectLotteryDetail(projectManager.getDirectLotteryDetailByProjectId(p2pProject.getId()));
		}
		
		ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(p2pProject.getId());
		int extraType = 0;//特殊业务类型: 1-活动 2-项目加息
		if(projectExtraAddRate!=null){
			extraType=projectExtraAddRate.getExtraType();
			p2pProject.setAddRate(projectExtraAddRate.getExtraAmount().toString());
			p2pProject.setAddRateBigDecimal(projectExtraAddRate.getExtraAmount());
			p2pProject.setMinAnnualizedRate(p2pProject.getMinAnnualizedRate().add(new BigDecimal(p2pProject.getAddRate())));//选择金额收益率显示
			p2pProject.setMaxAnnualizedRate(p2pProject.getMaxAnnualizedRate().add(new BigDecimal(p2pProject.getAddRate())));
		}
		p2pProject.setExtraType(extraType);
		
		QuickProjectBiz quickProjectBiz = getQuickInfoByProductId(p2pProject.getId());
		if(quickProjectBiz!=null){
			p2pProject.setLotteryEndCountDown(quickProjectBiz.getLotteryEndCountDown());
			p2pProject.setPopularityFlag(quickProjectBiz.getPopularityFlag());
			p2pProject.setPopularity(quickProjectBiz.getPopularity());
			p2pProject.setPopularityStratDate(quickProjectBiz.getPopularityStratDate());
			p2pProject.setPopularityEndDate(quickProjectBiz.getPopularityEndDate());
			p2pProject.setExtraAmount(quickProjectBiz.getExtraAmount());
			p2pProject.setOnlineCountDown(quickProjectBiz.getOnlineCountDown());
		}
		
		
	}
	
	
	
	/**
	 * @Description:获取项目详情-P2P
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年1月26日 下午1:52:27
	 */
	private ProjectInfoDto getProjectP2pInfoById(Project project,Long memberId) throws ManagerException {
		ProjectInfoDto p2pProject = null;
		ProjectNotice projectNotice = null;
		// 只可查看已发布后的项目和预告项目
		if (project != null && project.getStatus() >= StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus()) {
			if (project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus()) {
				projectNotice = projectNoticeManager.getProjectNoticingByProjectId(project.getId());
				if (projectNotice == null) {
					return p2pProject;
				}
			}
			p2pProject = BeanCopyUtil.map(project, ProjectInfoDto.class);
			// 借款人信息
			MemberBaseBiz borrower = memberManager.selectMemberBaseBiz(project.getBorrowerId());
			p2pProject.setBorrowMemberBiz(borrower);

			// 企业信息
			if (TypeEnum.MEMBER_TYPE_COMPANY.getType() == p2pProject.getBorrowerType()||TypeEnum.MEMBER_TYPE_ORG.getType() == p2pProject.getBorrowerType()) {
				Enterprise enterprise = enterpriseMapper.selectByPrimaryKey(project.getEnterpriseId());
				p2pProject.setEnterprise(enterprise);
			}
			
			Boolean flag  = transactionManager.isMemberInvestedProject(project.getId(), memberId);
		/*	if(project.getId()!= null && project.getId()<=989807383L){
				flag = true;
			}*/
			// 附件信息
			List<BscAttachment> bscAttachments = bscAttachmentManager.findAttachmentsByKeyId(String.valueOf(project.getId()));
			if (!Collections3.isEmpty(bscAttachments)) {
				assignationP2pAttachment(bscAttachments, p2pProject,flag);
			}
			//收益本息表
			List<DebtInterest> interests =  debtInterestManager.findInterestsByProjectId(project.getId());
			if(Collections3.isNotEmpty(interests)){
				int i=debtInterestMapper.findOverduesByProjectId(project.getId());
				if(i>0){
					p2pProject.setIsOverdue(true);
				}
				p2pProject.setInterests(interests);
			}
			if (project.getAnnualizedRateType() == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getStatus()) {
				// 阶段收益
				List<ProjectInterest> projectInterestList = projectInterestManager.getProjectInterestByProjectId(project.getId());
				p2pProject.setProjectInterestList(projectInterestList);
			}
			
			List<DebtInterest> debtInterests = debtInterestManager.findProjectInterestsByProjectId(project.getId());
			if(Collections3.isNotEmpty(debtInterests)){
				p2pProject.setStartDate(debtInterests.get(0).getStartDate());
				p2pProject.setEndDate((debtInterests.get(debtInterests.size()-1).getEndDate()));
				p2pProject.setInterests(debtInterests);
			}
			//担保物类型的详细信息
			DebtCollateral collateral = debtCollateralMapper.findCollateralByProjectId(project.getId());
			processCollateralDetail(collateral);
			p2pProject.setDebtCollateral(collateral);
			//逾期结算记录(已完成)
			List<OverdueRepayLog> overdueRepaylogs = overdueRepayLogManager.getOverdueRepayListByProjectId(project.getId());
			p2pProject.setOverdueRepayBiz(overdueRepaylogs);
			//催收中
			OverdueRepayLog overdue=overdueRepayLogManager.getRepayLogByProjectIdCollect(project.getId());
			p2pProject.setOverdue(overdue);
			//项目是否有逾期
			int i=debtInterestMapper.findOverduesByProjectId(project.getId());
			if(i>0){
				p2pProject.setIsOverdue(true);
			}
			//借款人信用信息
			borrowerCredit(p2pProject,project.getBorrowerId());
			//获取特殊活动信息（快投）
			this.getExtraProject(p2pProject);
			
		}
		return p2pProject;
	}

	/**
	 * 借款人信用信息
	 * @param projectInfoDto
	 * @param borrowerId
     */
	private void borrowerCredit(ProjectInfoDto projectInfoDto,Long borrowerId){
		Integer payOffCount = projectManager.queryPayOffCountByBorrowerId(borrowerId);
		projectInfoDto.setPayOffCount(payOffCount);
		Integer overdueCount= overdueRepayLogManager.queryOverdueCountByBorrowerId(borrowerId);
		projectInfoDto.setOverdueCount(overdueCount);
		BigDecimal overdueAmount= overdueRepayLogManager.queryOverdueAmountByBorrowerId(borrowerId);
		projectInfoDto.setOverdueAmount(overdueAmount);
		BorrowerCreditGrade borrowerCreditGrade= borrowerCreditGradeManager.queryByBorrowerId(borrowerId);
		if (borrowerCreditGrade!=null){
			projectInfoDto.setBorrowerCreditLevel(borrowerCreditGrade.getCreditLevel());
			projectInfoDto.setBorrowerCreditLevelDes(borrowerCreditGrade.getCreditLevelDes());
		}
	}

	/**
	 * P2P附件分类
	 */
	private void assignationP2pAttachment(List<BscAttachment> bscAttachments,ProjectInfoDto p2pProject,Boolean flag){
		
		List<BscAttachment> signMemberAttachments = p2pProject.getSignAttachments();
		List<BscAttachment> collateralAttachments = p2pProject.getCollateralAttachments();
		List<BscAttachment> borrowMemberAttachments = p2pProject.getBorrowMemberAttachments();
		List<BscAttachment> contractAttachments = p2pProject.getContractAttachments();
		List<String> contractCategoryName = p2pProject.getContractCategoryName();
		
		if(flag){
			for (BscAttachment attachment : bscAttachments) {
				String module = attachment.getModule();
				if (module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_SIGN.getCode())) {// 项目形象图
					signMemberAttachments.add(attachment);
				} else if (module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_COLLATERAL.getCode())) {// 担保物附件
					collateralAttachments.add(attachment);
				} else if (module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_BORROWER.getCode())) {// 借款人附件
					borrowMemberAttachments.add(attachment);
				} else if (module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_CONTRACT.getCode()) // 合同图片
						|| module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_LEGAL.getCode())// 法律意见书
						|| module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_CREDIT.getCode())// 征信报告
						|| module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_BASE.getCode())) {// 其他资料图片
					String categoryName = AttachmentEnum.getEnumByCode(module).getDesc();
					attachment.setModule(categoryName);
					contractAttachments.add(attachment);
					if (!contractCategoryName.contains(categoryName)) {
						contractCategoryName.add(categoryName);
					}
				}
			}
		}else{
			for (BscAttachment attachment : bscAttachments) {
				String module = attachment.getModule();
				if (module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_SIGN.getCode())) {// 项目形象图
					signMemberAttachments.add(attachment);
				} else if (module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_COLLATERAL_MOSAIC.getCode())) {// 担保物附件
					collateralAttachments.add(attachment);
				} else if (module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_BORROWER_MOSAIC.getCode())) {// 借款人附件
					borrowMemberAttachments.add(attachment);
				} else if (module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_CONTRACT_MOSAIC.getCode()) // 合同图片
						|| module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_LEGAL_MOSAIC.getCode())// 法律意见书
						|| module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_CREDIT_MOSAIC.getCode())// 征信报告
						|| module.equals(AttachmentEnum.ATTACHMENT_MODULE_DIRECT_PROJECT_BASE_MOSAIC.getCode())) {// 其他资料图片
					String categoryName = AttachmentEnum.getEnumByCode(module).getDesc();
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
		if (collateral != null && StringUtil.isNotBlank(collateral.getCollateralDetails())) {
			Map<String, String> colMap = JSON.parseObject(collateral.getCollateralDetails(), new TypeReference<LinkedHashMap<String, String>>() {});
			for (Object o : colMap.entrySet()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) o;
				if (StringUtil.isBlank(entry.getValue())) {
					continue;
				}
				if (entry.getKey().equals("发动机号")) {
					entry.setValue(StringUtil.maskString(entry.getValue(), StringUtil.ASTERISK, 1, 1));
				}
				if (entry.getKey().equals("车架号")) {
					entry.setValue(StringUtil.maskString(entry.getValue(), StringUtil.ASTERISK, 3, 1));
				}
				if (entry.getKey().equals("车牌号")) {
					entry.setValue(StringUtil.maskString(entry.getValue(), StringUtil.ASTERISK, 2, 1));
				}
				if (entry.getKey().equals("合格证编号")) {
					entry.setValue(StringUtil.maskString(entry.getValue(), StringUtil.ASTERISK, 2, 1));
				}
				if (entry.getKey().equals("房屋产权证")) {
					entry.setValue(numberMaskString(entry.getKey(), entry.getValue()));
				}
				if (entry.getKey().equals("土地使用权证")) {
					entry.setValue(numberMaskString(entry.getKey(), entry.getValue()));
				}
			}
			collateral.setCollateralDetails(JSON.toJSONString(colMap,true));
		}
	}

	private String numberMaskString(String colName,String colValue) {
		String regex="\\d+";
		Pattern p=Pattern.compile(regex);
		Matcher m = p.matcher(colValue);
		 if(m.find()==true){
			return StringUtil.maskString(m.group(0), StringUtil.ASTERISK, 1, 1);
		 }
		return colValue;
	}

	@Override
	public Page<ProjectForMember> selectCollectProjectForMember(CollectingProjectQuery query) {
		try {
			Page<ProjectForMember>  page = projectManager.selectCollectProjectForMember(query);
			List<ProjectForMember> proForList = page.getData();
			for(ProjectForMember pro :proForList ){
				if(pro.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
					TransferProject transferProject = transferProjectManager.selectByPrimaryKey(pro.getTransferId());
					pro.setBorrowPeriod(transferProjectManager.getReturnDay(transferProject.getTransactionId()));
					pro.setBorrowPeriodType(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType());
				}
				pro.setAnnualizedRate(pro.getAnnualizedRate().add(pro.getExtraProjectAnnualizedRate()));
				//pro.setAnnualizedRate(pro.getAnnualizedRate());
				BigDecimal extraInterest=BigDecimal.ZERO;
				if(pro.getExtraInterestDay()!=null&&pro.getExtraInterestDay()>0){
					extraInterest=FormulaUtil.calculateInterest(pro.getInvestAmount(),pro.getExtraAnnualizedRate(),pro.getExtraInterestDay());
					pro.setTotalExtraInterest(extraInterest);
					if(extraInterest==null){
						extraInterest=BigDecimal.ZERO;
					}
				}else if(pro.getExtraAnnualizedRate()!=null){
					Project project=projectManager.selectByPrimaryKey(pro.getProjectId());
					if(project!=null){
						if(project.isDirectProject()){
							extraInterest=projectManager.invertExtraInterest(project, pro.getInvestAmount(), pro.getTransactionTimeDate(), pro.getExtraAnnualizedRate());
						}
						if(extraInterest==null){
							extraInterest=BigDecimal.ZERO;
						}
					}
				}
				pro.setTotalExtraInterest(extraInterest);
				pro.setTotalAllInterest(pro.getTotalInterest());
				pro.setTotalInterest(pro.getTotalInterest().subtract(extraInterest));
			}
			
			return page;
		} catch (ManagerException e) {
			logger.error("用户中心查询募集中的项目失败", e);
		}
		return null;
	}
	
	/**
	 * 查询用户是否有投资募集中的项目
	 * @param 
	 * @return
	 */
	@Override
	public int collectingProject(Long memberId) {
		try {
			CollectingProjectQuery query =new CollectingProjectQuery();
			query.setMemberId(memberId);
			int  count =  transactionManager.selectCollectForMemberCounting(query);
			return count;
		} catch (ManagerException e) {
			logger.error("查询用户投资过的募集中的项目失败", e);
		}
		return 0;
	}
	/**
	 * 查询用户是否有投资募集中的项目
	 * @param 
	 * @return
	 */
	@Override
	public BigDecimal selectCollectProjectForMemberInvestAmount(Long memberId) {
		try {
			CollectingProjectQuery query =new CollectingProjectQuery();
			query.setMemberId(memberId);
			return projectManager.selectCollectProjectForMemberInvestAmount(query);
		} catch (ManagerException e) {
			logger.error("查询用户投资过的募集中的项目失败", e);
		}
		return new BigDecimal(0);
	}
	/**
	 * 查询用户是否有投资募集中的项目
	 * @param 
	 * @return
	 */
	@Override
	public Long selectCollectProjectDescTransactionTime(Long memberId) {
		Long timeStamp = null;
		try {
			CollectingProjectQuery query =new CollectingProjectQuery();
			query.setMemberId(memberId);
			ProjectForMember projectForMember = projectManager.selectCollectProjectDescTransactionTime(query);
			if(projectForMember!=null){
				timeStamp = projectForMember.getTransactionTimeDate().getTime();
			}
			return timeStamp;
		} catch (ManagerException e) {
			logger.error("查询用户最近一笔募集中的项目的投资", e);
		}
		return timeStamp;
	}
	
	
	/**
	 * 查询募集中的项目详情
	 * @param 
	 * @return
	 */
	@Override
	public ResultDO<ProjectForMember> selectCollectProjectDetail(Long transactionId,Long memberId){
		ResultDO<ProjectForMember> result = new ResultDO<ProjectForMember>();
		try {
			ProjectForMember projectForMember = projectManager.selectCollectProjectDetail(transactionId,memberId);
			Member member = memberManager.selectByPrimaryKey(projectForMember.getMemberId());
			projectForMember.setMembertName(member.getTrueName());
			projectForMember.setIdentityNumber(member.getIdentityNumber());
			projectForMember.setMobile(member.getMobile());
			projectForMember.setAnnualizedRate(projectForMember.getAnnualizedRate().add(projectForMember.getExtraProjectAnnualizedRate()));
			if(projectForMember.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
				TransferProject transferProject = transferProjectManager.selectByPrimaryKey(projectForMember.getTransferId());
				
				projectForMember.setBorrowPeriod(transferProjectManager.getReturnDay(transferProject.getTransactionId()));
				projectForMember.setBorrowPeriodType(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType());
				
				BigDecimal totalInterest = projectForMember.getTotalInterest().add(projectForMember.getTotalPrincipal()).subtract(projectForMember.getInvestAmount());
				projectForMember.setTotalInterest(totalInterest);
			}
			if(projectForMember.getExtraInterestDay()!=null&&projectForMember.getExtraInterestDay()>0){
				BigDecimal totalExtraInterest=FormulaUtil.calculateInterest(projectForMember.getInvestAmount(),projectForMember.getExtraAnnualizedRate(),projectForMember.getExtraInterestDay());
				if(totalExtraInterest==null){
					totalExtraInterest=BigDecimal.ZERO;
				}
				projectForMember.setTotalInterest(projectForMember.getTotalInterest());
			}
			result.setResult(projectForMember);
		} catch (ManagerException e) {
			logger.error("查询募集中的项目详情,transactionId={}", transactionId, e);
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 用户点击、已阅，募集中的项目
	 * @param 
	 * @return
	 */
	@Override
	public void clickCollectingProject(Map<String, Object> map) {
		try {
			String memberId = map.get("memberId").toString();
			String key =  RedisConstant.REDIS_KEY_COLLECTING_PROJECT+RedisConstant.REDIS_SEPERATOR
	    			+ memberId;
			Map<String, String> projects=RedisManager.hgetAll(key);
			for (Map.Entry<String, String> entry : projects.entrySet()) {
				RedisManager.hset(key, entry.getKey(),flag);
			}
		} catch (Exception e) {
			logger.error("用户点击、已阅，募集中的项目失败", e);
		}
	}


	@Override
	public Project selectByPrimaryKey(Long id) {
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			return project;
		} catch (Exception e) {
			logger.error("查询项目失败  id={}",id, e);
		}
		return null;
	}
	
	
	@Override
	public int getInvestProjectCountByProjectType(String projectType,String investTypeCode) {
		try {
			return projectManager.getInvestProjectCountByProjectType(projectType,investTypeCode);
		} catch (Exception e) {
			logger.error("根据项目类型获取投资中项目个数失败,项目类型={}",projectType,e);
		}
		return 0;
	}

	@Override
	public Page<ProjectInterestBiz> getBorrowOverdueList(ProjectBorrowQuery query) {
		try {
			return projectManager.getBorrowOverdueList(query);
		} catch (ManagerException e) {
			logger.error("获取借款逾期列表失败,query={}", query, e);
		}
		return null;
	}

	@Override
	public Page<ProjectInterestBiz> getBorrowLabelList(ProjectBorrowQuery query) {
		try {
			return projectManager.getBorrowLabelList(query);
		} catch (ManagerException e) {
			logger.error("获取流标列表失败,query={}", query, e);
		}
		return null;
	}

	/**
	 * @desc 垫资逾期还款
	 * @param projectId
	 * @return
	 * @author fuyili
	 * @throws ManagerException 
	 * @time 2016年2月23日 上午11:46:23
	 *
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<HostingCollectTrade> toUnderWriteRepay(Long projectId, String payerIp) throws Exception {
		logger.info("垫资还款开始执行");
		ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
		
		//未还款的垫资逾期记录加锁
		List<OverdueLog> overdueNotPayLogs= overdueLogManager.getIdsByProjectIdandStatus(projectId);
		for (OverdueLog overdueLog : overdueNotPayLogs) {
			overdueLog = overdueLogManager.getLockForUpdate(overdueLog.getId());
		}
		
		// 查询还款中的逾期记录count
		int overduePaying = overdueLogManager.countOverduePaying(projectId);
		logger.info("还款中的逾期记录：overduePaying={}",overduePaying);
		if (overduePaying > 0) {
			result.setResultCode(ResultCode.OVERDUE_REPAY_EXIST_PAYING);
			return result;
		}
		// 查询未还款的逾期记录count
		int overdueNotpay = overdueLogManager.countOverdueNotpay(projectId);
		logger.info("未还款的逾期记录：overdueNotpay={}",overdueNotpay);
		if (overdueNotpay <= 0) {
			result.setResultCode(ResultCode.OVERDUE_REPAY_NOT_EXIST_NO_PAY);
			return result;
		}
		Project project = projectManager.selectByPrimaryKey(projectId);
		if (project == null) {
			result.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
			return result;
		}
		Long borrowid= project.getBorrowerId();

		if (!StringUtils.isEmpty(project.getOpenPlatformKey())){
			SysDict dict = sysDictManager.findByGroupNameAndKey("channel_business", project.getOpenPlatformKey());
			if (dict != null && StringUtil.isNotBlank(dict.getValue()) && StringUtil.isNumeric(dict.getValue())) {
				borrowid = Long.valueOf(dict.getValue());
			}
		}
		// 借款人存钱罐可用余额
		Balance queryBalance = new Balance();
		try {
			queryBalance = this.balanceManager.synchronizedBalance(borrowid, TypeEnum.BALANCE_TYPE_PIGGY);
		} catch (Exception e) {
			logger.error("【逾期垫资还款】查询第三方资金余额出错,projectId=",project.getBorrowerId());
			result.setResultCode(ResultCode.REQUEST_ERROR);
			return result;
		}
		// 应还金额
		Date repayDate = DateUtils.getCurrentDate();
		ProjectInterestBiz piz = projectManager.getOverdueAmount(projectId, DateUtils.getStrFromDate(repayDate, DateUtils.DATE_FMT_3));
		// 如果用户存钱罐余额不够，直接返回
		if (piz.getPayableAmount().compareTo(queryBalance.getAvailableBalance()) > 0) {
			result.setResultCode(ResultCode.OVERDUE_REPAY_PIGGY_BALANCE_LACKING);
			return result;
		}
		// 生成逾期还款记录，设置状态为2-还款中
		OverdueRepayLog overdueRepayLog = overdueRepayLogManager.saveRecordOnline(projectId, repayDate);
		if (overdueRepayLog == null) {
			return result;
		}
		// 设置逾期记录为还款中
		overdueLogManager.updateStatus(StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus(), StatusEnum.OVERDUE_LOG_PAYING.getStatus(),
				repayDate, projectId, null);
		
		// 更新最近一笔逾期记录的滞纳金
		int updateOverdueFee = overdueLogManager.updateOverdueFine(piz.getLateFees(),StatusEnum.OVERDUE_LOG_PAYING.getStatus(),projectId);
		logger.info("【逾期垫资还款】更新滞纳金={},滞纳金={}",updateOverdueFee,piz.getLateFees());
		// 创建本地垫付代收
		HostingCollectTrade collectTrade = insertLocalHostingCollectTradeForRepay(overdueRepayLog, borrowid, payerIp);
		result.setResult(collectTrade);
		logger.info("垫资还款执行结束");
		return result;
	}
	
	/**
	 * 为垫付创建本地代收交易
	 * @param memberId 
	 *
	 * @param order
	 * @param collectType
	 * @throws ManagerException
	 */
	private HostingCollectTrade insertLocalHostingCollectTradeForRepay(OverdueRepayLog overdueRepayLog, Long memberId, String payerIp)
			throws ManagerException {
		try {
			HostingCollectTrade trade = hostingCollectTradeManager.selectWaitPayOrFinishedHostingCollectTrade(overdueRepayLog.getId(), TypeEnum.HOSTING_COLLECT_TRADE_OVERDUE_REPAY.getType());
			if(trade != null) {
				throw new ManagerException("垫付还款"+overdueRepayLog.getId()+",已经存在待收，创建待收失败");
			}
			// 本地保存HostingCollectTrade信息
			HostingCollectTrade collectTrade = new HostingCollectTrade();
			collectTrade.setAmount(overdueRepayLog.getPayableAmount());
			collectTrade.setPayerId(memberId);
			collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
			collectTrade.setRemarks(overdueRepayLog.getRemarks());
			collectTrade.setSourceId(overdueRepayLog.getId());
			collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
			collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
			collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
			collectTrade.setPayStatus((SinaPayEnum.PAY_PROCESSING.getCode()));
			collectTrade.setTradeTime(overdueRepayLog.getCreateTime());
			collectTrade.setType(TypeEnum.HOSTING_COLLECT_TRADE_OVERDUE_REPAY.getType());
			collectTrade.setProjectId(overdueRepayLog.getProjectId());
			collectTrade.setPayerIp(payerIp);
			if (hostingCollectTradeManager.insertSelective(collectTrade) > 0) {
				return collectTrade;
			}

		} catch (ManagerException e) {
			logger.error("交易出错[保存HostingCollectTrade信息出错]：overdueRepayLog=" + overdueRepayLog, e);
			throw e;
		}
		return null;
	}
	/**
	 * 
	 * @desc 募集中列表
	 * @param query
	 * @return
	 * @author chaisen
	 * @time 2016年2月25日 下午4:49:13
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> getBorrowRaisingList(ProjectBorrowQuery query) {
		try {
			return projectManager.getBorrowRaisingList(query);
		} catch (ManagerException e) {
			logger.error("获取募集中列表失败,query={}", query, e);
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDto<CreateCollectTradeResult> createSinpayHostingCollectTradeForRepay(
			HostingCollectTrade collectTrade, String payerIp) throws Exception {
		ResultDto<CreateCollectTradeResult> result = new ResultDto<CreateCollectTradeResult>();
		try {
			HostingCollectTrade CollectTradeForLock = hostingCollectTradeManager.getByIdForLock(collectTrade.getId());
			// 如果已经是最终状态，不处理，直接返回
			if (CollectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
					|| CollectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())
					|| CollectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())) {
				logger.info("垫资还款代收tradeNo=" + CollectTradeForLock.getTradeNo() + "状态已经是最终状态：" + CollectTradeForLock.getTradeStatus());
				return result;
			}
			result = sinaPayClient.createHostingCollectTrade(
					CollectTradeForLock.getTradeNo(),
					TypeEnum.HOSTING_COLLECT_TRADE_OVERDUE_REPAY.getDesc(), 
					SerialNumberUtil.generateIdentityId(CollectTradeForLock.getPayerId()), payerIp, AccountType.SAVING_POT,
					CollectTradeForLock.getAmount(), IdType.UID, TradeCode.COLLECT_FROM_BORROWER_FOR_OVERDUE_REPAY,
					TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode());
			return result;
		} catch (Exception e) {
			logger.error("创建垫资还款代收发生异常："+collectTrade.getTradeNo(), e);
		}
		return result;
	}

	@Override
	public ResultDO<List<HostingPayTrade>> afterOverdueRepayCollectNotify(String tradeNo, String outTradeNo, String tradeStatus)
			throws Exception {
		return projectManager.afterOverdueRepayCollectNotify(tradeNo, outTradeNo, tradeStatus);
	}

	

	@Override
	public void createSinpayHostingPayTradeForRepay(List<HostingPayTrade> hostingPayTrades) throws Exception {
		projectManager.createSinpayHostingPayTradeForRepay(hostingPayTrades);
	}

	@Override
	public ResultDO<?> afterOverdueRepayHostingPay(String tradeStatus,
			String tradeNo, String outTradeNo) throws Exception {
		return projectManager.afterOverdueRepayHostingPay(tradeStatus, tradeNo, outTradeNo);
	}
	
	@Override
	public ResultDO<ProjectForFront> findIndexDebtProjectList() {
		return findProjectList(Constant.INDEX_DEFAULT_NUMBER,ProjectEnum.PROJECT_TYPE_DEBT.getType());
	}
	
	@Override
	public ResultDO<ProjectForFront> findIndexDirectProjectList() {
		return findProjectList(Constant.INDEX_DEFAULT_NUMBER,ProjectEnum.PROJECT_TYPE_DIRECT.getType());
	}
	/**
	 * 
	 * @desc 获取逾期记录
	 * @param projectId
	 * @return
	 * @author chaisen
	 * @time 2016年2月29日 下午3:50:13
	 *
	 */
	@Override
	public ResultDO<OverdueRepayLogBiz> getOverdueRecordListByProjectId(Long projectId) {
		ResultDO<OverdueRepayLogBiz> result = new ResultDO<OverdueRepayLogBiz>();
		try {
			List<OverdueLog> listRecord=overdueLogManager.selectInterestsByProjectId(projectId);
			List<OverdueRepayLogBiz> list = Lists.newArrayList();
			if (!Collections3.isEmpty(listRecord)) {
				for(OverdueLog biz:listRecord){
					OverdueRepayLogBiz overdueRepayLogBiz=new OverdueRepayLogBiz();
					BigDecimal overduePrincipal = BigDecimal.ZERO;
					BigDecimal overdueInterest = BigDecimal.ZERO;
					if(biz.getOverduePrincipal()!=null){
						overduePrincipal=biz.getOverduePrincipal();
					}
					if(biz.getOverdueInterest()!=null){
						overdueInterest=biz.getOverdueInterest();
					}
					DebtInterest debtInterest=debtInterestMapper.selectByPrimaryKey(biz.getInterestId());
					if(debtInterest!=null){
						overdueRepayLogBiz.setRepayDate(debtInterest.getEndDate());
					}else{
						overdueRepayLogBiz.setRepayDate(biz.getStartDate());
					}
					overdueRepayLogBiz.setTotalPayAmount(overduePrincipal.add(overdueInterest));
					if(overduePrincipal.compareTo(BigDecimal.ZERO)>0&&overdueInterest.compareTo(BigDecimal.ZERO)>0){
						overdueRepayLogBiz.setInterestTypes(TypeEnum.INTEREST_TYPE_PRININTER.getType());
					}
					if(overduePrincipal.compareTo(BigDecimal.ZERO)==0&&overdueInterest.compareTo(BigDecimal.ZERO)>0){
						overdueRepayLogBiz.setInterestTypes(TypeEnum.INTEREST_TYPE_INTEREST.getType());
					}
					//overdueRepayLogBiz.setStatus(biz.getStatus());
					//是否垫资
					int i=overdueLogManager.getOverdueLogByInterestIdAndtype(biz.getInterestId(),TypeEnum.OVERDUE_LOG_TYPE_UNDERWRITE.getType());
					if(i>0){
						overdueRepayLogBiz.setUnderwrite(1);
					}
					//是否逾期
					int j=overdueLogManager.getOverdueLogByInterestIdAndtype(biz.getInterestId(),TypeEnum.OVERDUE_LOG_TYPE_GENERAL.getType());
					if(j>0){
						overdueRepayLogBiz.setOverdue(1);
					}
					overdueRepayLogBiz=getInterestStatus(overdueRepayLogBiz,biz.getInterestId());
					overdueRepayLogBiz.setPeriods(biz.getPeriods());
					list.add(overdueRepayLogBiz);
				}
				result.setResultList(list);
			}
		} catch (ManagerException e) {
			logger.error("获取逾期记录失败,projectId={}", projectId, e);
		}
		return result;
	}
	/**
	 * 
	 * @desc 获取逾期金额，滞纳金
	 * @param projectId
	 * @return
	 * @author chaisen
	 * @time 2016年2月29日 上午11:56:45
	 *
	 */
	@Override
	public ResultDO<OverdueRepayLogBiz> getUnderWriteAmountInfo(Long projectId) {
		ResultDO<OverdueRepayLogBiz> result = new ResultDO<OverdueRepayLogBiz>();
		try {
			OverdueRepayLogBiz overdue=projectManager.getUnderWriteAmountInfoByProjectId(projectId);
			result.setResult(overdue);
		} catch (ManagerException e) {
			logger.error("获取逾期金额失败,projectId={}", projectId, e);
		}
		return result;
	}
	/**
	 * 
	 * @desc 已还清借款
	 * @param query
	 * @return
	 * @author chaisen
	 * @time 2016年3月1日 下午6:26:20
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> getHasPayoffBorrow(ProjectBorrowQuery query) {
		try {
			return projectManager.getHasPayoffBorrow(query);
		} catch (ManagerException e) {
			logger.error("获取已还清借款列表失败,query={}", query, e);
		}
		return null;
	}

	@Override
	public boolean getTransactionListProfitStatus(Long projectId) {
		try {
			return projectManager.getTransactionListProfitStatus(projectId);
		} catch (ManagerException e) {
			logger.error("获取交易列表的收益状态失败,projectId={}", projectId, e);
		}
		return false;
	}

	@Override
	public int countCurrentBorrowerByMemberId(Long memberId) {
		try {
			return projectManager.countCurrentBorrowerByMemberId(memberId);
		} catch (ManagerException e) {
			logger.error("统计登录用户的 借款项目数,memberId={}", memberId, e);
		}
		return 0;
	}

	@Override
	public int getDirectProjectCount() {
		try {
			return projectManager.getDirectProjectCount();
		} catch (ManagerException e) {
			logger.error("获取直投项目个数失败", e);
		}
		return 0;
	}
	
	@Override
	public boolean isBorrowerTypeEnterprise(Long projectId) {
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			if(project == null){
				return false;
			}
			Enterprise enterprise =enterpriseManager.selectByMemberID(project.getBorrowerId());
			if(project.getBorrowerType()==2 && enterprise!=null){//企业
				return true;
			}
		} catch (ManagerException e) {
			logger.error("获取当前项目借款人是否为企业,projectId={}", projectId, e);
		}
		return false;
	}
	
	
	@Override
	public ResultDO averageCapitalMethod(){
			ResultDO result = new ResultDO();
			result.setSuccess(false);
		try {
			JSONObject json = new JSONObject();
			Project project = projectManager.averageCapitalMethod();
			if(project !=null){
				json.put("id", project.getId());
				result.setResult(json);
				result.setSuccess(true);
				return result;
			}
			Project projectNoticing = projectManager.averageCapitalMethodNoticing();
			if(projectNoticing !=null){
				json.put("id", projectNoticing.getId());
				result.setResult(json);
				result.setSuccess(true);
				return result;
			}
			Project	projectProcessing = projectManager.averageCapitalMethoding();
			if(projectProcessing !=null){
				json.put("id", projectProcessing.getId());
				result.setResult(json);
				result.setSuccess(true);
				return result;
			}
			json.put("id","");
		} catch (ManagerException e) {
			logger.error("查询等本等息项目",e);
		}
		return result;
	}
	
	
	@Override
	public ResultDO<HostingPayTrade> afterGuaranteeFeeCollectNotify(String tradeNo, String outTradeNo, String tradeStatus)
			throws Exception {
		return projectManager.afterGuaranteeFeeCollectNotify(tradeNo, outTradeNo, tradeStatus);
	}

	@Override
	public void createSinpayHostingPayTradeForGuaranteeFee(HostingPayTrade hostingPayTrades) throws Exception {
		projectManager.createSinpayHostingPayTradeForGuaranteeFee(hostingPayTrades);
	}

	@Override
	public ResultDO<?> afterGuaranteeFeeHostingPay(String tradeStatus, String tradeNo, String outTradeNo)
			throws Exception {
		return projectManager.afterGuaranteeFeeHostingPay(tradeStatus,tradeNo,outTradeNo);
	}
	/**
	 * 
	 * @desc 逾期本息记录
	 * @param projectId
	 * @return
	 * @author chaisen
	 * @time 2016年5月30日 下午4:24:41
	 *
	 */
	@Override
	public ResultDO<OverdueRepayLogBiz> geOverdueDebtInterestListByProjectId(Long projectId) {
		ResultDO<OverdueRepayLogBiz> result = new ResultDO<OverdueRepayLogBiz>();
		try {
			List<DebtInterest> interests=debtInterestManager.findOverdueInterestsByProjectId(projectId);
			
			List<OverdueRepayLogBiz> list = Lists.newArrayList();
			
			if (Collections3.isNotEmpty(interests)) {
				for(DebtInterest biz:interests){
					OverdueRepayLogBiz overdueRepayLogBiz=new OverdueRepayLogBiz();
					BigDecimal overduePrincipal = BigDecimal.ZERO;
					BigDecimal overdueInterest = BigDecimal.ZERO;
					
					if(biz.getPayablePrincipal()!=null){
						overduePrincipal=biz.getPayablePrincipal();
					}
					if(biz.getPayableInterest()!=null){
						overdueInterest=biz.getPayableInterest();
					}
					overdueRepayLogBiz.setRepayDate(biz.getEndDate());
					overdueRepayLogBiz.setTotalPayAmount(overduePrincipal.add(overdueInterest));
					if(overduePrincipal.compareTo(BigDecimal.ZERO)>0&&overdueInterest.compareTo(BigDecimal.ZERO)>0){
						overdueRepayLogBiz.setInterestTypes(TypeEnum.INTEREST_TYPE_PRININTER.getType());
					}
					if(overduePrincipal.compareTo(BigDecimal.ZERO)==0&&overdueInterest.compareTo(BigDecimal.ZERO)>0){
						overdueRepayLogBiz.setInterestTypes(TypeEnum.INTEREST_TYPE_INTEREST.getType());
					}
					overdueRepayLogBiz.setStatus(biz.getStatus());
					overdueRepayLogBiz.setPeriods(biz.getPeriods());
					//是否垫资
					int i=overdueLogManager.getOverdueLogByInterestIdAndtype(biz.getId(),1);
					if(i>0){
						overdueRepayLogBiz.setUnderwrite(1);
					}
					//是否逾期
					int j=overdueLogManager.getOverdueLogByInterestIdAndtype(biz.getId(),2);
					if(j>0){
						overdueRepayLogBiz.setOverdue(1);
					}
					overdueRepayLogBiz=getInterestStatus(overdueRepayLogBiz,biz.getId());
					list.add(overdueRepayLogBiz);
				}
				result.setResultList(list);
			}
		} catch (ManagerException e) {
			logger.error("获取逾期记录失败,projectId={}", projectId, e);
		}
		return result;
	}
	/**
	 * 
	 * @Description:获取还款状态
	 * @param overdueRepayLogBiz
	 * @param interestId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月1日 下午2:45:26
	 */
	public OverdueRepayLogBiz getInterestStatus(OverdueRepayLogBiz overdueRepayLogBiz,Long interestId) throws ManagerException{
		DebtInterest interest=debtInterestManager.selectByPrimaryKey(interestId);
		if(interest!=null){
			if(interest.getStatus()==StatusEnum.DEBT_INTEREST_ALL_PAYED.getStatus()){
				overdueRepayLogBiz.setOverdueStatus(StatusEnum.INTEREST_RECORD_HADPAY.getStatus());
			}else if(interest.getStatus()==StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus()){
				int k=overdueLogManager.getCountOverdueRecordByInterestId(interest.getId(),1);
				if(k>0){
					overdueRepayLogBiz.setOverdueStatus(StatusEnum.INTEREST_RECORD_UNDERWRITE.getStatus());
				}else{
					overdueRepayLogBiz.setOverdueStatus(StatusEnum.INTEREST_RECORD_WAITPAY.getStatus());
				}
			}else if(interest.getStatus()==StatusEnum.DEBT_INTEREST_OVERDUE_PAY.getStatus()){
				int l=overdueLogManager.getCountOverdueRecordByInterestId(interest.getId(),2);
				if(l>0){
					overdueRepayLogBiz.setOverdueStatus(StatusEnum.INTEREST_RECORD_OVERDUE.getStatus());
				}
			}
		}
		return overdueRepayLogBiz;
	}
	
	@Override
	public ResultDO<OverdueRepayLogBiz> getOverdueAmountInfo(Long projectId) {
		ResultDO<OverdueRepayLogBiz> result = new ResultDO<OverdueRepayLogBiz>();
		try {
			OverdueRepayLogBiz overdue=projectManager.getOverdueAmountInfoByProjectId(projectId);
			result.setResult(overdue);
		} catch (ManagerException e) {
			logger.error("获取逾期金额失败,projectId={}", projectId, e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public synchronized ResultDO<OverdueRepayLog> toOverdueRepay(Long projectId, String payerIp) throws Exception {
		ResultDO<OverdueRepayLog>  resultDO = new ResultDO<OverdueRepayLog>();
		//判断项目是否存在
		Project project = projectManager.selectByPrimaryKey(projectId);
		if (project == null) {
			resultDO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
			return resultDO;
		}
		//判断是否存在未还款的逾期记录
		//TODO 	加锁
		OverdueRepayLog overdueRepayLog = overdueRepayLogManager.getOverdueRepayByStatus(projectId);
		if(overdueRepayLog == null){
			resultDO.setResultCode(ResultCode.OVERDUE_REPAY_NOT_EXIST_NO_PAY);
			return resultDO;
		}
		
		//计算滞纳金
		Map<String, Object> overdueMap = overdueRepayLogManager.getLateFeeByOverdueRepayLog(overdueRepayLog,project.getLateFeeRate());
		if(overdueMap.containsKey("overdueRepayLog")){
			overdueRepayLog = (OverdueRepayLog) overdueMap.get("overdueRepayLog");
		}
		Balance queryBalance = new Balance();

		//判断是否是渠道商项目
		Long borrowId=project.getBorrowerId();
		if (StringUtil.isNotBlank(project.getOpenPlatformKey())) {
			// 从字典表找对应渠道合作商资金账户
			SysDict dict = sysDictManager.findByGroupNameAndKey("channel_business", project.getOpenPlatformKey());
			if (dict != null && StringUtil.isNotBlank(dict.getValue()) && StringUtil.isNumeric(dict.getValue())) {
				borrowId = Long.valueOf(dict.getValue());
			}
		}
		try {
			queryBalance = this.balanceManager.synchronizedBalance(borrowId, TypeEnum.BALANCE_TYPE_PIGGY);
		} catch (Exception e) {
			logger.error("【普通逾期还款】查询第三方资金余额出错,projectId=",borrowId);
			resultDO.setResultCode(ResultCode.REQUEST_ERROR);
			return resultDO;
		}
		// 如果用户存钱罐余额不够，直接返回
		if (overdueRepayLog.getPayableAmount().compareTo(queryBalance.getAvailableBalance()) > 0) {
			resultDO.setResultCode(ResultCode.OVERDUE_REPAY_PIGGY_BALANCE_LACKING);
			return resultDO;
		}
		//更新逾期结算记录，滞纳金、未还本金、未还利息、逾期天数、还款状态
		overdueRepayLog.setRepayStatus(StatusEnum.OVERDUE_REPAYSTATUS_PAYING.getStatus());
		overdueRepayLog.setRepayTime(DateUtils.getCurrentDate());
		overdueRepayLog.setRealpayAmount(overdueRepayLog.getPayableAmount());
		overdueRepayLogManager.updateByPrimaryKeySelective(overdueRepayLog);
		//查询逾期记录并更新逾期记录状态为“还款中”,滞纳金
		String overdueId = overdueRepayLog.getOverdueId();
		String[] overdueIds = overdueId.split(",");
		int overdueSize = overdueIds.length;
		for (String id : overdueIds) {
			OverdueLog log = new OverdueLog();
			log.setId(Long.valueOf(id));
			log.setOverdueFine(overdueRepayLog.getOverdueFine().divide(new BigDecimal(overdueSize)).setScale(2,BigDecimal.ROUND_HALF_UP));
			log.setOverdueRepayId(overdueRepayLog.getId());
			log.setEndDate(DateUtils.getCurrentDate());
			overdueLogManager.updateByPrimaryKeySelective(log);
			overdueLogManager.updateStatusForOverdueRepay(StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus(), StatusEnum.OVERDUE_LOG_PAYING.getStatus(),Long.valueOf(id));
		}
		if(overdueMap.containsKey("transactionInterests")){
			@SuppressWarnings("unchecked")
			List<TransactionInterestForLateFee>  transactionInterests= (List<TransactionInterestForLateFee>)overdueMap.get("transactionInterests");
			TransactionInterest interest = new TransactionInterest();
			interest.setRealPayPrincipal(null);
			interest.setRealPayInterest(null);
			interest.setRealPayExtraInterest(null);
			interest.setRealPayExtraProjectInterest(null);
			for (TransactionInterestForLateFee transactionInterest : transactionInterests) {
				interest.setId(transactionInterest.getId());
				interest.setOverdueFine(transactionInterest.getOverdueFine());
				interest.setTopayDate(DateUtils.getCurrentDate());
				transactionInterestManager.updateTransactionInterest(interest);
			}
		}
		return resultDO;
	}
	/**
	 * 
	 * @desc 获取项目费用
	 * @param projectId
	 * @return
	 * @author chaisen
	 * @time 2016年6月12日 下午5:18:57
	 *
	 */
	@Override
	public ResultDO<ProjectInterestBiz> getProjectFeeDetail(Long projectId) {
		ResultDO<ProjectInterestBiz>  resultDO = new ResultDO<ProjectInterestBiz>();
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			if(project==null){
				resultDO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
			}
			ProjectInterestBiz overdue=new ProjectInterestBiz();
			overdue.setTotalAmount(project.getTotalAmount());
			overdue.setManageFeeRate(project.getManageFeeRate());
			overdue.setRiskFeeRate(project.getRiskFeeRate());
			overdue.setGuaranteeFeeRate(project.getGuaranteeFeeRate());
			overdue.setIntroducerFeeRate(project.getIntroducerFeeRate());
			resultDO.setResult(overdue);
		} catch (ManagerException e) {
			logger.error("获取项目费用失败,projectId={}", projectId, e);
		}
		
		return resultDO;
	}

	@Override
	public List<OverdueRepayLogBiz> getCommonOverdueRepayLogRecord(Long projectId) {
		try {
			List<OverdueRepayLogBiz> interestList = Lists.newArrayList();
			List<OverdueRepayLog> list=overdueRepayLogMapper.getCommonOverdueRepayLogRecord(projectId);
			if (Collections3.isNotEmpty(list)) {
				for(OverdueRepayLog overdue:list){
					OverdueRepayLogBiz biz =new OverdueRepayLogBiz();
					BeanCopyUtil.copy(overdue,biz);
					interestList.add(biz);
				}
			}
			return interestList;
		} catch (ManagerException e) {
			logger.error("获取逾期还款记录失败,projectId={}", projectId, e);
		}
		return null;
	}
	
	
	/**
	 * @Description:获取项目详情-转让
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年9月10日 下午1:52:27
	 */
	private ProjectInfoDto getTransferProjectInfoById(TransferProject transferProject,Long memberId) throws ManagerException {
		ProjectInfoDto projectInfoDto = new ProjectInfoDto();
		Project project = projectManager.selectByPrimaryKey(transferProject.getProjectId());
		
		projectInfoDto.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
		projectInfoDto.setName(transferProject.getTransferName());
		projectInfoDto.setProfitType(transferProject.getProfitType());
		projectInfoDto.setInvestType(project.getInvestType());
		
		projectInfoDto.setMaxAnnualizedRate(transferProject.getTransferAnnualizedRate());
		projectInfoDto.setMinAnnualizedRate(transferProject.getTransferAnnualizedRate());
		projectInfoDto.setAnnualizedRate(transferProject.getTransferAnnualizedRate());
		projectInfoDto.setTransferAnnualizedRate(transferProjectManager.getTransferProjectAnnualized(transferProject.getId()));
		projectInfoDto.setTransferAfterInterest(project.getTransferAfterInterest());
		//pLD.setEarningsDays(transferProject.getDays());//earnings计算得到
		projectInfoDto.setBorrowPeriod(transferProjectManager.getReturnDay(transferProject.getTransactionId()));
		projectInfoDto.setBorrowPeriodType(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType());
		projectInfoDto.setStatus(transferProject.getStatus());
		projectInfoDto.setStartDate(transferProject.getTransferStartDate());
		projectInfoDto.setEndDate(project.getEndDate());
		projectInfoDto.setSaleEndTime(transferProject.getTransferEndDate());	
		projectInfoDto.setInterestFrom(transferProject.getInterestFrom());
		projectInfoDto.setId(transferProject.getProjectId());
		projectInfoDto.setTransferId(transferProject.getId());
		projectInfoDto.setThumbnail(transferProject.getThumbnail());
		
		projectInfoDto.setTotalAmount(transferProject.getSubscriptionPrincipal());
		projectInfoDto.setDiscount(transferProject.getDiscount());
		
		/*BigDecimal projectValue = transferProjectManager.getProjectValue(transferProject.getTransactionId());
		projectInfoDto.setProjectValue(projectValue);*/
		
		projectInfoDto.setTransferAmount(transferProject.getTransferAmount());
		projectInfoDto.setTransferEndDate(transferProject.getTransferEndDate());
		projectInfoDto.setCurrentDate(DateUtils.getCurrentDate());
		
		
		Transaction transaction = transactionManager.selectTransactionById(transferProject.getTransactionId());
		/*BigDecimal extraAnnualizedRate = 
				(transaction.getExtraAnnualizedRate()!=null?transaction.getExtraAnnualizedRate():new BigDecimal(0));
		*/
		projectInfoDto.setOriginalAnnualizedRate(transaction.getAnnualizedRate());
		
		projectInfoDto.setMinInvestAmount(transferProject.getUnitSubscriptionAmount());
		
		projectInfoDto.setUnitSubscriptionAmount(transferProject.getUnitSubscriptionAmount());
		projectInfoDto.setUnitTransferAmount(transferProject.getUnitTransferAmount());
		
		
		projectInfoDto.setIsNovice(1);
		
		
		TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
		
		transactionInterestQuery.setEndDate(transferProject.getTransferStartDate());
		transactionInterestQuery.setTransactionId(transferProject.getTransactionId());
		
		List<TransactionInterest> tranList = transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
		List<DebtInterest> interests =   Lists.newArrayList();
		if (Collections3.isNotEmpty(tranList)) {
			for(TransactionInterest tran:tranList){
				DebtInterest interest =new DebtInterest();
				BeanCopyUtil.copy(tran,interest);
				interests.add(interest);
			}
		}
		projectInfoDto.setInterests(interests);
		projectInfoDto.setOpenPlatformKey(project.getOpenPlatformKey());
		//projectInfoDto.setTransferAmount(transferAmount);
		/*
		String progress = getProjectNumberProgress(transferProject.getTransferAmount(), balance);
		if(transferProject.getStatus() >= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
			int totalInvestment = transactionManager.getTransactionCountByTransferProject(projectInfoDto.getId());
			projectInfoDto.setTotalInvestment(totalInvestment);
			projectInfoDto.setAvailableBalance(balance);
			projectInfoDto.setInvestmentProgress(progress);
		}else{//如果还处未投资状态，直接就是总金额
			projectInfoDto.setAvailableBalance(balance);
			projectInfoDto.setInvestmentProgress(progress);
		}
		
		if(StatusEnum.PROJECT_STATUS_INVESTING.getStatus()==transferProject.getStatus()
				&&BigDecimal.ZERO.compareTo(balance)==0){
			int orders =  orderManager.getTransferOrderCountByProject(projectInfoDto.getId());
			projectInfoDto.setOrders(orders);
			BigDecimal orderAmount =BigDecimal.ZERO;  
			orderAmount = orderManager.getTransferPayingAmountByProject(projectInfoDto.getId());
			projectInfoDto.setOrderAmount(orderAmount);
		}*/
		

		
		return projectInfoDto;
	}
	
	@Override
	public ResultDO<ProjectForFront> findTransferProjectList() {
		return findTransferProjectList(Constant.INDEX_DEFAULT_NUMBER);
	}
	
	private ResultDO<ProjectForFront> findTransferProjectList(Integer number) {
		ResultDO<ProjectForFront> result = new ResultDO<ProjectForFront>();
		List<ProjectForFront> projectForFrontList = Lists.newArrayList();
		try {

			// 常规投资中的项目
			if (projectForFrontList.size() < number) {
				
				 List<TransferProject>  transferProjectList= transferProjectManager.findIndexInvestingProjectList(number);
				if (Collections3.isNotEmpty(transferProjectList)) {
					for(TransferProject tran:transferProjectList){
						ProjectForFront pro = new ProjectForFront();
						
						pro.setId(tran.getId());
					
						pro.setAnnualizedRate(tran.getTransferAnnualizedRate());
						
						pro.setMaxAnnualizedRate(tran.getTransferAnnualizedRate());
						pro.setMinAnnualizedRate(tran.getTransferAnnualizedRate());
						
						pro.setName(tran.getTransferName());
						pro.setTotalAmount(tran.getSubscriptionPrincipal());
						pro.setAvailableBalance(transferProjectManager.getTransferProjectBalanceById(tran.getId()));
						pro.setBorrowPeriod(transferProjectManager.getReturnDay(tran.getTransactionId()));
						pro.setBorrowPeriodType(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType());
						pro.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
						pro.setStatus(tran.getStatus());
						
						Project oriPro = projectManager.selectByPrimaryKey(tran.getProjectId());
						pro.setThumbnail(oriPro.getThumbnail());
						
						projectForFrontList.add(pro);
					}
				}
			}

			/*// 不足8个项目，再上履约或还款中的项目
			if (projectForFrontList.size() < number) {
				List<TransferProject> notInvestingProjectForList = transferProjectManager.findIndexNotInvestingProjectList(number
						- projectForFrontList.size());
				if (Collections3.isNotEmpty(notInvestingProjectForList)) {
					
					for(TransferProject tran:notInvestingProjectForList){
						ProjectForFront pro = new ProjectForFront();
						
						pro.setId(tran.getId());
						pro.setAnnualizedRate(tran.getTransferAnnualizedRate());
						
						pro.setMaxAnnualizedRate(tran.getTransferAnnualizedRate());
						pro.setMinAnnualizedRate(tran.getTransferAnnualizedRate());
						
						pro.setName(tran.getTransferName());
						pro.setTotalAmount(tran.getSubscriptionPrincipal());
						pro.setAvailableBalance(transferProjectManager.getTransferProjectBalanceById(tran.getId()));
						pro.setBorrowPeriod(transferProjectManager.getReturnDay(tran.getTransactionId()));
						pro.setStatus(tran.getStatus());
						pro.setBorrowPeriodType(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType());
						pro.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
						
						Project oriPro = projectManager.selectByPrimaryKey(tran.getProjectId());
						pro.setThumbnail(oriPro.getThumbnail());
						
						projectForFrontList.add(pro);
					}
				}
			}*/
			result.setResultList(projectForFrontList);
		} catch (ManagerException e) {
			logger.error("查询转让项目列表异常", e);
			result.setSuccess(false);
			return result;
		}
		return result;

	}
	
	
	/**
	 * 分页获取转让项目列表数据
	 * @param projectQuery
	 * @return
	 */
	private Page<ProjectForFront> queryTransferProject(ProjectQuery projectQuery){
		Page<ProjectForFront> pageList = new Page<ProjectForFront>();
		try {
			TransferProjectQuery transferProjectQuery = new TransferProjectQuery();
			transferProjectQuery = BeanCopyUtil.map(projectQuery,TransferProjectQuery.class);
			Page<TransferProject> data = transferProjectManager.findTransferProjectList(transferProjectQuery);
			List<ProjectForFront> projectList = Lists.newArrayList(); 
			if(data != null){
				for(TransferProject transferProject : data.getData()){
					ProjectForFront pForFront = new ProjectForFront();
					pForFront.setName(transferProject.getTransferName());
					pForFront.setDiscount(transferProject.getDiscount());
					pForFront.setAnnualizedRateType(1);
					pForFront.setMaxAnnualizedRate(transferProject.getTransferAnnualizedRate());
					pForFront.setMinAnnualizedRate(transferProject.getTransferAnnualizedRate());
					//pLD.setEarningsDays(transferProject.getDays());//earnings计算得到
					pForFront.setBorrowPeriod(transferProjectManager.getReturnDay(transferProject.getTransactionId()));
					pForFront.setBorrowPeriodType(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType());
					pForFront.setStatus(transferProject.getStatus());
					
					BigDecimal balance = transferProjectManager.getTransferProjectBalanceById(transferProject.getId());
					pForFront.setAvailableBalance(balance);
					pForFront.setTotalAmount(transferProject.getSubscriptionPrincipal());
					pForFront.setStartDate(transferProject.getTransferStartDate());
					pForFront.setEndDate(transferProject.getTransferEndDate());
					pForFront.setInterestFrom(transferProject.getInterestFrom());
					
					
					Project oriPro = projectManager.selectByPrimaryKey(transferProject.getProjectId());
					pForFront.setThumbnail(oriPro.getThumbnail());
					pForFront.setInvestType(oriPro.getInvestType());
					
					pForFront.setId(transferProject.getId());
					pForFront.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
					
					pForFront.setInvestNum(transferProject.getInvestNum());
					pForFront.setMostInvestAmount(transferProject.getMostInvestAmount());
					pForFront.setTotalIncome(transferProject.getTotalIncome());
					projectList.add(pForFront);
				}
				pageList.setData(projectList);
				pageList.setiDisplayLength(data.getiDisplayLength());
				pageList.setiDisplayStart(data.getiDisplayStart());
				pageList.setiTotalRecords(data.getiTotalRecords());
				pageList.setPageNo(data.getPageNo());
			}		
		} catch (ManagerException e) {
			logger.error("获取转让项目列表", e);
		}
		return pageList;
	}
	
	
	@Override
	public ResultDO<?> afterHostPayForTransferSuccess(String tradeStatus, String tradeNo, String outTradeNo)
			throws Exception {
		return transferProjectManager.afterHostPayForTransferSuccess(tradeStatus,tradeNo,outTradeNo);
	}

	@Override
	public Page<TransferProject> findTransferProjectForMemberCenter(TransferProjectQuery transferProjectQuery){
		try {
			return transferProjectManager.findTransferProjectListForMember(transferProjectQuery);
		} catch (Exception e) {
			logger.error("获取转让项目列表失败", e);
		}
		return null;
	}

	@Override
	public ResultDO<Object> directProjectLottery(Long projectId, Long memberId ,int type) {
		try {
			//projectManager.getDirectLotteryDetailByProjectId(projectId);
			return projectManager.directProjectLottery(projectId,memberId,type);
		} catch (Exception e) {
			logger.error("直投项目抽奖失败", e);
		}
		return null;
	}
	private String getProjectNumberProgress(BigDecimal totalAmount, Long id) throws ManagerException {
		String progress = "0";
		BigDecimal availableBalance = getProjectBalanceById(id);
		if (availableBalance != null) {
			if (availableBalance.compareTo(BigDecimal.ZERO) <= 0) {
				progress = "100";
			} else if (availableBalance.compareTo(totalAmount) == 0) {
				progress = "0";
			} else {
				progress = new DecimalFormat("###.##").format((totalAmount.subtract(availableBalance)).divide(totalAmount, 4,
						RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
			}
		}
		return progress;
	}
	
	@Override
	public Object quickLotteryProject(MemberSessionDto user) {
		ResultDO<ProjectForLottery> result = new ResultDO<ProjectForLottery>();
		ProjectForLottery model=new ProjectForLottery();
		try {
		 if(user!=null){
				//抽奖次数
				List<ActivityLottery> listLottery = activityLotteryManager.selectActivityLotteryByMemberId(user.getId(),ActivityConstant.DIRECT_COUNT_LOTTERY_KEY);
				List<ActivityProject> listProjectLottery=Lists.newArrayList();
				if(Collections3.isNotEmpty(listLottery)){
					for(ActivityLottery lottery:listLottery){
						ActivityProject activityp=new ActivityProject();
						activityp.setNummber(lottery.getRealCount());
						activityp.setProjectId(Long.parseLong(lottery.getCycleConstraint()));
						Project project=projectManager.selectByPrimaryKey(Long.parseLong(lottery.getCycleConstraint()));
						if(project!=null){
							activityp.setProjectName(project.getName());
						}
						listProjectLottery.add(activityp);
					}
					model.setListProjectLottery(listProjectLottery);
				}
				
				//中奖信息
				List<ProjectForReward> projectForReward=Lists.newArrayList();
				ActivityLotteryResult modelResult=new ActivityLotteryResult(); 
				
				ActivityLotteryResult modelResultAmount=new ActivityLotteryResult(); 
				modelResultAmount.setMemberId(user.getId());
				modelResult.setRewardType(5);
				modelResult.setMemberId(user.getId());
				modelResult.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);
				List<ProjectForFront> listRewardProject=projectManager.findQuickInvestLotteryProject(modelResult);
				if(Collections3.isNotEmpty(listRewardProject)){
					for(ProjectForFront bean:listRewardProject){
						ProjectForReward reward=new ProjectForReward();
						
						reward.setStatus(bean.getStatus());
						reward.setId(bean.getId());
						reward.setName(bean.getName());
						if(StatusEnum.PROJECT_STATUS_LOSE.getStatus()==bean.getStatus()){
							//reward.setProgress("募集失败");
							reward.setRewardInfo("奖励已失效");
						}else if(StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()==bean.getStatus()
								||StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()==bean.getStatus()||StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()==bean.getStatus()){
							//reward.setProgress("募集成功");
							//募集时间超过奖励期限
							int totalDays=DateUtils.getIntervalDays(bean.getOnlineTime(),bean.getSaleComplatedTime())+1;
							List<PrizePool> prizePoolList =projectExtraManager.getPrizePoolByProjectId(bean.getId());
							Integer maxDay = 0;
							if(Collections3.isNotEmpty(prizePoolList)){
								for (PrizePool pri : prizePoolList) {
									if (Float.parseFloat(pri.getRatio())<= 0) {
										continue;
									}
									if (maxDay < pri.getDay()) {
										maxDay = pri.getDay();
									}
								}
								if(totalDays>maxDay){
									reward.setOverDate(false);
								}
							}
							
						}else{
							reward.setProgress(getProjectNumberProgress(bean.getTotalAmount(), bean.getId()));
							reward.setRewardInfo("奖励将于募集成功后发放");
							//募集时间超过奖励期限
							int totalDays=DateUtils.getIntervalDays(bean.getOnlineTime(),DateUtils.getCurrentDate())+1;
							List<PrizePool> prizePoolList =projectExtraManager.getPrizePoolByProjectId(bean.getId());
							Integer maxDay = 0;
							if(Collections3.isNotEmpty(prizePoolList)){
								for (PrizePool pri : prizePoolList) {
									if (Float.parseFloat(pri.getRatio())<= 0) {
										continue;
									}
									if (maxDay < pri.getDay()) {
										maxDay = pri.getDay();
									}
								}
								if(totalDays>maxDay){
									reward.setOverDate(false);
								}
							}
						}
						modelResult.setRemark(bean.getId().toString());
						modelResultAmount.setRewardId(bean.getId().toString());
						//List<ActivityLotteryResult> listResult=activityLotteryResultManager.getLotteryResultBySelective(modelResult);
						reward.setTotalRewardAmount(activityLotteryResultManager.sumRewardInfoByProjectId(modelResultAmount,3));
						reward.setPopularity(activityLotteryResultManager.sumRewardInfoByProjectId(modelResultAmount,1));
						reward.setTotalCash(activityLotteryResultManager
								.sumRewardInfoByProjectId(modelResultAmount, 7));
						modelResult.setRewardType(5);
						List<ProjectForLevel> projectForLevel=activityLotteryResultManager.getRewardLevelByProjectId(modelResult);
						reward.setProjectForLevel(projectForLevel);
						projectForReward.add(reward);
					}
				}
				model.setProjectForReward(projectForReward);
		 }else{
			 ActivityLotteryResult recrod=new ActivityLotteryResult();
			 recrod.setRewardType(5);
			 recrod.setRewardResult("winnerLottery");
			 List<ActivityLotteryResult> listRecord=activityLotteryResultManager.getActivityLotteryResultByProject(recrod);
			 List<ProjectForReward> listReward=Lists.newArrayList();
			 if(Collections3.isNotEmpty(listRecord)){
				 for(ActivityLotteryResult lotteryBiz:listRecord){
					 ProjectForReward reward=new ProjectForReward();
					 Project project=projectManager.selectByPrimaryKey(Long.parseLong(lotteryBiz.getRemark()));
						if(project!=null){
							 reward.setName(project.getName());
							 reward.setId(project.getId());
						}
						reward.setLevel1(lotteryBiz.getChip());
						reward.setReceivedTime(lotteryBiz.getUpdateTime());
						listReward.add(reward);
				 }
			 }
			 model.setListReward(listReward);
		 }
		 //获取最新的规则信息
		List<PrizePool> prizePoolList=projectExtraManager.getPrizePoolByProjectId(null);
		model.setPrizePoolList(prizePoolList);
		List<LotteryRuleAmountNumber> ruleAmountList=projectExtraManager.getLotteryByProjectId(null);
		model.setRuleAmountList(ruleAmountList);
		 List<PrizeInPool> prizeInPoolList =projectExtraManager.getPrizeInPoolByProjectId(null);
		 if(Collections3.isNotEmpty(prizeInPoolList)){
			 for(PrizeInPool inPool:prizeInPoolList){
				 inPool.setProportion(new BigDecimal(100).multiply(new BigDecimal(inPool.getProportion())).intValue()+"");
			 }
		 }
		 model.setPrizeInPoolList(prizeInPoolList);
		 model.setRewardHour(projectExtraManager.getRewardHourByProjectId(null));
		 
		 
		 //快投有奖项目列表
		 List<ProjectForFront> projectFrontList=projectManager.findQuickInvestRecommendProject();
		 if(Collections3.isNotEmpty(projectFrontList)){
			 for(ProjectForFront font:projectFrontList){
					// 获取进度
				 font.setProcess(SysServiceUtils.getProjectNumberProgress(font.getTotalAmount(),
						 font.getId()));
					// 获取剩余可投金额
				 font.setAvailableBalance(SysServiceUtils.getProjectBalance(font.getId()));
				 
				// 获取进度条
				 font.setRound(SysServiceUtils.getProgressCeil(font.getTotalAmount(), font.getId()));
			 }
		 }
		 model.setProjectFrontList(projectFrontList);
		
		 
		 List<CouponTemplate> listTemplate=couponTemplateRelationManager.getDirectReward();
		 Collections.reverse(listTemplate);
		 model.setListTemplate(listTemplate);
		 result.setResult(model);
		 } catch (ManagerException e) {
			 logger.error("获取快投抽奖专题页失败", e);
		 }
		return result;
	}
	/**
	 * 
	 * @desc 奖励详情
	 * @param projectId
	 * @return
	 * @author chaisen
	 * @time 2016年11月4日 上午9:32:12
	 *
	 */
	@Override
	public ResultDO<ProjectForRewardDetail> getrewardDetail(Long projectId) {
		
		ResultDO<ProjectForRewardDetail> result = new ResultDO<ProjectForRewardDetail>();
		List<ProjectForRewardDetail> listRewardDetail=Lists.newArrayList();
		List<PrizePool> prizePoolList =projectExtraManager.getPrizePoolByProjectId(projectId);
		try {
			Project project=projectManager.selectByPrimaryKey(projectId);
			if(Collections3.isEmpty(prizePoolList)){
				return null;
			}
			if(project==null){
				return null;
			}
			List<PrizeInPool> prizeInPoolList=projectExtraManager.getPrizeInPoolByProjectId(projectId);
			//各等级占比例
			Map<String, Object> paraMap = new HashMap<String, Object>();
			for(PrizeInPool pool:prizeInPoolList){
				paraMap.put(pool.getLevel()+"level", pool.getProportion().toString());
			}
			ProjectExtra pe = projectExtraManager.getProjectQucikReward(projectId);
			for(int i=1;i<=prizePoolList.size();i++){
				ProjectForRewardDetail detail=new ProjectForRewardDetail();
				detail.setTime(DateUtils.addDate(project.getOnlineTime(),i));
				//比例
				String radio=prizePoolList.get(i).getRatio();
				detail.setProportion(radio);
				BigDecimal ratioAmount=pe.getExtraAmount().multiply(new BigDecimal(radio));
				BigDecimal level1Amount=ratioAmount.multiply(new BigDecimal(paraMap.get("1level").toString()));
				BigDecimal level2Amount=ratioAmount.multiply(new BigDecimal(paraMap.get("2level").toString()));
				BigDecimal level3Amount=ratioAmount.multiply(new BigDecimal(paraMap.get("3level").toString()));
				BigDecimal level4Amount=ratioAmount.multiply(new BigDecimal(paraMap.get("4level").toString()));
				BigDecimal level5Amount=ratioAmount.multiply(new BigDecimal(paraMap.get("5level").toString()));
				BigDecimal level6Amount=ratioAmount.multiply(new BigDecimal(paraMap.get("6level").toString()));
				detail.setLevel1Amount(level1Amount);
				detail.setLevel2Amount(level2Amount);
				detail.setLevel3Amount(level3Amount);
				detail.setLevel4Amount(level4Amount);
				detail.setLevel5Amount(level5Amount);
				detail.setLevel6Amount(level6Amount);
				listRewardDetail.add(detail);
			}
			result.setResultList(listRewardDetail);
		} catch (ManagerException e) {
			 logger.error("获取奖励详情失败,projectId={}", projectId, e);
		}
		return result;
	}

	@Override
	public Integer getQuickLotteryNumber(Long memberId) {
		try {
			return activityLotteryManager.getQuickLotteryNumber(memberId);
		} catch (Exception e) {
			logger.error("统计抽奖次数失败", e);
		}
		return null;
	}

	@Override
	public ProjectExtra getProjectActivitySignByProjectId(Long projectId) {
		try {
			return projectExtraManager.getProjectQucikReward(projectId);
		} catch (ManagerException e) {
			logger.error("获取特殊额外活动项目标识失败, projectId={}", projectId, e);
		}
		return null;
	}

	@Override
	public QuickProjectBiz getRecommendQuickProject() {

		try{
			Long projectId = projectManager.getRecommendQuickProjectId();
			if(projectId==null){
				logger.warn("没有推荐的快投项目。" );
				return null;
			}

			return getQuickInfoByProductId(projectId);
			
		}catch(Exception e){
			logger.error("获取推荐快投项目失败：",  e);
			return null;
		}
	}
	
	/**
	 * 根据指定ID获取快投属性
	 * @param projectId
	 * @return
	 */
	public QuickProjectBiz getQuickInfoByProductId(Long projectId){
		
		QuickProjectBiz quickProject = new QuickProjectBiz();
		
		try{
			//项目属性
			Project project = projectManager.selectByPrimaryKey(projectId);
			BeanUtils.copyProperties(project, quickProject);
			//上线倒计时
			if(quickProject.getOnlineTime()!=null){
				quickProject.setOnlineCountDown(DateUtils.getTimeIntervalSencond(new Date(), project.getOnlineTime()));
			}

			
			//奖金总额
			ProjectExtra projectExtra = projectExtraManager.getProjectQucikReward(projectId);
			quickProject.setExtraAmount(projectExtra.getExtraAmount());
			
			//抽奖倒计时
			String rewardHour = projectExtraManager.getRewardHourByProjectId(projectId) ;
			Date lotteryEndTime = DateUtils.addHour(quickProject.getOnlineTime(),Integer.valueOf(rewardHour));
			quickProject.setLotteryEndCountDown(DateUtils.getTimeIntervalSencond(new Date(),lotteryEndTime));
			
			//人气值
			String popularity = projectExtraManager.getRewardPopularityByProjectId(projectId);
			quickProject.setPopularity(popularity);
			
			//超值福利 开始结束时间
			QuickRewardConfig quickRewardConfig = projectExtraManager.getQuickRewardConfig(projectId);
			quickProject.setPopularityStratDate(DateUtils.getStrFromDate(quickRewardConfig.getStartDate(), DateUtils.DATE_FMT_9));
			quickProject.setPopularityEndDate( DateUtils.getStrFromDate(quickRewardConfig.getEndDate(), DateUtils.DATE_FMT_9));
			quickProject.setPopularityFlag(quickRewardConfig.isFlag());
			
			//快投专区中奖名单
			if(quickProject.getStatus()==StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus() || quickProject.getStatus()==StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()){
				//快投项目在履约中 ，获取本期中奖名单
				List<LotteryRewardBiz> lotteryRewardList =  activityLotteryResultManager.getProjectLotteryReward(quickProject.getId());
				quickProject.setLotteryRewardList(lotteryRewardList);
			}else{
				//获取最近所有快投中奖名单
				List<LotteryRewardBiz> lotteryRewardList = null;
				String redisKey = "lotteryRewardList";
				boolean isExit = RedisManager.isExitByObjectKey(redisKey);
				if (isExit) {
					lotteryRewardList = (List<LotteryRewardBiz>) RedisManager.getObject(redisKey);
				} else {
					lotteryRewardList = activityLotteryResultManager.getLotteryReward(50);
					RedisManager.putObject(redisKey, lotteryRewardList);
					RedisManager.expireObject(redisKey, 3000);
				}
				//List<LotteryRewardBiz> lotteryRewardList = activityLotteryResultManager.getLotteryReward(50);
				quickProject.setLotteryRewardList(lotteryRewardList);
			}
			//是否项目加息
			ProjectExtra addRateProject = projectExtraManager.getAddRateProject(projectId);
			if(addRateProject!=null){
				quickProject.setAddRate(addRateProject.getExtraAmount());
			}
			
			return quickProject;
		}catch(Exception e){
			logger.error("获取推荐快投属性失败：",  e);
			return null;
		}
	}
	public ProjectPackageBiz findProjectPackageBiz(){
		ProjectPackageBiz projectPackageBiz =new ProjectPackageBiz();
		ProjectPackage projectPackage = projectPackageMapper.selectByPrimaryKey(1L);
		projectPackageBiz.setProjectPackageName(projectPackage.getName());
		projectPackageBiz.setDescription(projectPackage.getDescription());
		
		
		return projectPackageBiz;
	}

	@Override
	public  ProjectPackage findProjectPackage(Long projectPackageId){
		return projectPackageMapper.selectByPrimaryKey(projectPackageId);		
	}
	@Override
	public List<ProjectPackageLinkModel> getProjectPackageLinkModelList(Long projectPackageId) {
		try{
			
			List<ProjectPackageLinkModel> modelList = projectPackageMapper.getProjectPackageModelList(projectPackageId);
			for(ProjectPackageLinkModel model:modelList){
				//排序 1，优先排序：投资中>预告>已投满>履约中>已还款>已截止>已暂停>流标 2，二级排序：项目上线时间倒叙
				model.setEndTime(DateUtils.getTimeIntervalSencond(new Date(),model.getOnlineTime()));
				ProjectExtra addRateProject = projectExtraManager.getAddRateProject(model.getId());
				//额外收益
				if(addRateProject != null && addRateProject.getExtraType().intValue()==2){
					model.setAnnualizedRate(model.getAnnualizedRate().add(addRateProject.getExtraAmount()));
				}
			}
			return modelList;
		}catch(Exception e){
			logger.error("获取项目b信息失败：",  e);
			return null;
		}
	}

	@Override
	public ResultDO<ProjectPackage> getProjectPackageIndex() {
		ResultDO<ProjectPackage> result = new ResultDO<ProjectPackage>();
		ProjectPackage projectPackage = projectPackageMapper.getMaxProgressModel();
		result.setResult(projectPackage);
		return result ;
	}

	@Override
	public List<ProjectPackage> ProjectPackageList(Integer  status) {
		return projectPackageMapper.getAllProjectPackageList(2);
	}
	public List<ProjectPackage> getAllCompletedProjectPackageList(){
		return projectPackageMapper.selectCompletedProjectPackageList();
	}

	public String getMinRewardLimit(){
		try {
			SysDict sysDict = sysDictManager.findByGroupNameAndKey(Constants.PACKAGE_FIVE_AMOUNT_LIMIT, Constants.PACKAGE_FIVE_AMOUNT_LIMIT);
			if(sysDict != null){
				return MoneyUtil.convertNumberToWAN(sysDict.getValue());
			}

		}catch (Exception e){

		}
		return null;
	}

}
