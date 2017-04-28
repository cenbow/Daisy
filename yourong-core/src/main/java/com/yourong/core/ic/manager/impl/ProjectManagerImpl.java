package com.yourong.core.ic.manager.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.bestsign.sdk.integration.Constants;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.BalanceAction;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.pay.sina.SinaPayConfig;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.BatchTradeNotifyMode;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.RefundStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.common.util.DateUtil;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.CancelAuthTradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.FinishAuthTradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.TradeArgs;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.dao.BscAttachmentMapper;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.dao.BalanceMapper;
import com.yourong.core.fin.dao.LoanDetailMapper;
import com.yourong.core.fin.dao.OverdueLogMapper;
import com.yourong.core.fin.dao.OverdueRepayLogMapper;
import com.yourong.core.fin.dao.PayPrincipalInterestMapper;
import com.yourong.core.fin.dao.PopularityInOutLogMapper;
import com.yourong.core.fin.dao.ProjectFeeMapper;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.manager.OverdueRepayLogManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.manager.ProjectFeeManager;
import com.yourong.core.fin.manager.UnderwriteLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.LoanDetail;
import com.yourong.core.fin.model.OverdueLog;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.fin.model.ProjectFee;
import com.yourong.core.fin.model.UnderwriteLog;
import com.yourong.core.fin.model.biz.OverdueRepayLogBiz;
import com.yourong.core.fin.model.query.BalanceQuery;
import com.yourong.core.ic.dao.CollectionProcessMapper;
import com.yourong.core.ic.dao.DebtCollateralMapper;
import com.yourong.core.ic.dao.DebtInterestMapper;
import com.yourong.core.ic.dao.ProjectExtraMapper;
import com.yourong.core.ic.dao.ProjectInterestMapper;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.dao.ProjectNoticeMapper;
import com.yourong.core.ic.dao.ProjectPackageLinkMapper;
import com.yourong.core.ic.dao.ProjectPackageMapper;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.DirectLotteryResultList;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.DirectSettlementBiz;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectBiz;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.ProjectInterest;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageBiz;
import com.yourong.core.ic.model.ProjectPackageLink;
import com.yourong.core.ic.model.ProjectPackageLinkModel;
import com.yourong.core.ic.model.ProjectPackageModel;
import com.yourong.core.ic.model.QuickRewardConfig;
import com.yourong.core.ic.model.biz.AfterHandlePreAuthTradeBiz;
import com.yourong.core.ic.model.biz.ProjectForDirectLottery;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.biz.ProjectForLevel;
import com.yourong.core.ic.model.biz.ProjectForLotteryReturn;
import com.yourong.core.ic.model.biz.ProjectForRewardDetail;
import com.yourong.core.ic.model.biz.ProjectForRewardMember;
import com.yourong.core.ic.model.biz.ProjectInvestingDto;
import com.yourong.core.ic.model.biz.ProjectNoticeForFront;
import com.yourong.core.ic.model.query.ProjectBorrowQuery;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.dao.ActivityLotteryMapper;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.dao.ActivityMapper;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.ActivityMemberLottery;
import com.yourong.core.mc.model.biz.ActivityLeadingSheep;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepProject;
import com.yourong.core.os.biz.ProjectForOpen;
import com.yourong.core.os.biz.ProjectStatus;
import com.yourong.core.os.biz.ProjectsPageOutPut;
import com.yourong.core.os.biz.ProjectsStatusOutPut;
import com.yourong.core.os.biz.TransactionsForOpen;
import com.yourong.core.sys.dao.SysDictMapper;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.manager.SysOperateInfoManager;
import com.yourong.core.sys.manager.SysUserManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.sys.model.SysOperateInfo;
import com.yourong.core.sys.model.SysUser;
import com.yourong.core.tc.dao.HostingRefundMapper;
import com.yourong.core.tc.dao.MemberHistoryRepaymentMapper;
import com.yourong.core.tc.dao.OrderMapper;
import com.yourong.core.tc.dao.TransactionInterestMapper;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.ContractCoreManager;
import com.yourong.core.tc.manager.HostingCollectTradeAuthManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingCollectTradeAuth;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.ProjectForMember;
import com.yourong.core.tc.model.biz.TransactionInterestForLateFee;
import com.yourong.core.tc.model.query.CollectingProjectQuery;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.dao.EnterpriseMapper;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.ThirdCompanyManager;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;
import com.yourong.core.uc.model.ThirdCompany;

@Component
public class ProjectManagerImpl implements ProjectManager {

	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private DebtManager debtManager;

	@Autowired
	private ProjectInterestMapper projectInterestMapper;

	@Autowired
	private BscAttachmentMapper bscAttachmentMapper;

	@Autowired
	private ProjectNoticeMapper projectNoticeMapper;

	@Autowired
	private PopularityInOutLogMapper popularityInOutLogMapper;

	@Autowired
	private SysUserManager sysUserManager;

	@Autowired
	private TransactionMapper transactionMapper;
	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private DebtInterestMapper debtInterestMapper;

	@Autowired
	private DebtInterestManager debtInterestManager;

	@Autowired
	private DebtCollateralMapper debtCollateralMapper;

	@Autowired
	private UnderwriteLogManager underWriteLogManager;
	@Autowired
	private OverdueLogManager overdueLogManager;
	@Autowired
	private BalanceMapper balanceMapper;
	@Autowired
	private ThirdCompanyManager thirdCompanyManager;
	@Autowired
	private EnterpriseMapper enterpriseMapper;

	@Autowired
	private PayPrincipalInterestMapper payPrincipalInterestMapper;
	@Autowired
	private ProjectFeeManager managementFeeManager;
	@Autowired
	private LoanDetailMapper loanDetailMapper;
	
	@Autowired
	private OverdueLogMapper overdueLogMapper;
	
	@Autowired
	private OverdueRepayLogMapper overdueRepayLogMapper;
	
	private Logger logger = LoggerFactory.getLogger(ProjectManagerImpl.class);

	@Autowired
	private HostingRefundMapper hostingRefundMapper;

	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;

	@Autowired
	private SinaPayClient sinaPayClient;
	
	@Autowired
	private  ProjectExtraManager projectExtraManager;
	
	@Autowired
	private OverdueRepayLogManager overdueRepayLogManager;

	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;

	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;
	
	@Autowired
	private UnderwriteLogManager underwriteLogManager;
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private CouponManager couponManager;
	
	@Autowired
	private SysOperateInfoManager sysOperateInfoManager;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
	private HostingCollectTradeAuthManager hostingCollectTradeAuthManager;

	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private TransactionInterestMapper transactionInterestMapper;
	
	@Autowired
	private ActivityManager activityManager;
	
	@Autowired
	private ProjectFeeMapper projectFeeMapper;
	
	@Autowired
	private CollectionProcessMapper collectionProcessMapper;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired
	private ContractCoreManager contractCoreManager;
	
	@Autowired
	private SysDictMapper sysDictMapper;
	
	
	@Autowired
	private ActivityLotteryMapper activityLotteryMapper;
	
	@Autowired
	private ActivityMapper activityMapper;
	
	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;
	
	@Autowired
	private ProjectExtraMapper projectExtraMapper;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;
	
	@Autowired 
	private PopularityInOutLogManager popularityInOutLogManager;

	@Autowired
	private ProjectPackageLinkMapper projectPackageLinkMapper;
	
	@Autowired
	private ProjectPackageMapper projectPackageMapper;
	@Autowired
	private MemberHistoryRepaymentMapper memberHistoryRepaymentMapper;
	@Override
	public Page<Project> findByPage(Page<Project> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());

			//针对前台传入时间，添加时分秒字段
			if(map.containsKey("onlineTimeStart")){
				map.put("onlineTimeStart", (map.get("onlineTimeStart")+" 00:00:00"));
			}
			if(map.containsKey("onlineTimeEnd")){
				map.put("onlineTimeEnd", (map.get("onlineTimeEnd")+" 23:59:59"));
			}
			if(map.containsKey("endDateStartTime")){
				map.put("endDateStartTime", (map.get("endDateStartTime")+" 00:00:00"));
			}
			if(map.containsKey("endDateEndTime")){
				map.put("endDateEndTime", (map.get("endDateEndTime")+" 23:59:59"));
			}
			
			
			int totalCount = projectMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<Project> selectForPagin = projectMapper.selectForPagin(map);
			
			/*for (Project project:selectForPagin){
				
					SysUser user = sysUserManager.selectByPrimaryKey(project.getAuditId());
					if(user!=null){
						project.setAuditName(user.getName());
					}
				
			}*/
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(Project record) throws ManagerException {
		try {
			return projectMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(Project record) throws ManagerException {
		try {
			return projectMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateProjectStatus(int statusCode, int currentStatus, Long projectId) throws ManagerException {
		try {
			return projectMapper.updateProjectStatus(statusCode, currentStatus, projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Project selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return projectMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectBiz findProjectById(Long projectId) throws ManagerException {
		ProjectBiz projectBiz = null;
		try {
			Project project = selectByPrimaryKey(projectId);
			if (project != null) {
				projectBiz = BeanCopyUtil.map(project, ProjectBiz.class);

				Debt debt = debtManager.selectByPrimaryKey(projectBiz.getDebtId());
				if (debt != null) {
					DebtBiz debtBiz = debtManager.getFullDebtInfoBySerialNumber(debt.getSerialNumber());
					projectBiz.setDebtBiz(debtBiz);

					// 债权附件信息
					List<BscAttachment> bscAttachments = bscAttachmentMapper.findAttachmentsByKeyId(String.valueOf(projectBiz.getId()));
					if (!Collections3.isEmpty(bscAttachments)) {
						debtBiz.setBscAttachments(bscAttachments);
					} else {
						debtBiz.setBscAttachments(null);
					}
				}

				if (projectBiz.getAnnualizedRateType() == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getStatus()) {// 阶递收益
					List<ProjectInterest> projectInterestList = projectInterestMapper.getProjectInterestByProjectId(projectBiz.getId());
					projectBiz.setProjectInterestList(projectInterestList);
				}
				// 特殊活动标识
				int activitySign = projectExtraManager.getProjectActivitySign(projectId);
				projectBiz.setActivitySign(activitySign);
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return projectBiz;
	}

	@Override
	public int updateByPrimaryKeySelective(Project record) throws ManagerException {
		try {
			return projectMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getAnnualizedRateByProjectIdAndInvestAmount(BigDecimal investAmount, Long projectId) throws ManagerException {
		if (investAmount == null || investAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}
		Project project = selectByPrimaryKey(projectId);
		if (project != null) {
			// 如果投资金额少于起投金额或者大小总金额，不参与计算。
			if ((investAmount.compareTo(project.getMinInvestAmount()) < 0) || (investAmount.compareTo(project.getTotalAmount()) > 0)) {
				return null;
			}
			// 如果投资金额不是递增金额的倍数，不参与计算
			if (investAmount.remainder(project.getIncrementAmount()).compareTo(BigDecimal.ZERO) != 0) {
				return null;
			}
			// 如果金额等于起投金额，年化收益为最小收益
			if (investAmount.compareTo(project.getMinInvestAmount()) == 0) {
				return project.getMinAnnualizedRate();// 最小收益
			}
			// 如果金额等于项目金额，年化收益为最大收益
			if (investAmount.compareTo(project.getTotalAmount()) == 0) {
				return project.getMaxAnnualizedRate();// 最大收益
			}

			Integer rateType = project.getAnnualizedRateType();
			if (rateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getStatus()) {// 阶梯
				BigDecimal rate = projectInterestMapper.getAnnualizedRateByInvestAmount(investAmount, projectId);
				return rate;
			}
			if (rateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_INCREMENT.getStatus()) {// 递增
				// 如果金额大于或等于收益封顶，年化收益为最大收益
				if (investAmount.compareTo(project.getMaxInvestAmount()) >= 0) {
					return project.getMaxAnnualizedRate();// 最大收益
				}
				BigDecimal result = investAmount.divide(project.getIncrementAmount(), 2, BigDecimal.ROUND_HALF_UP);
				if (result.doubleValue() < 2) {
					return project.getMinAnnualizedRate();// 最小收益
				} else {
					BigDecimal rate = project.getMinAnnualizedRate().add(
							(result.subtract(new BigDecimal("1"))).multiply(project.getIncrementAnnualizedRate()));
					if (rate.compareTo(project.getMaxAnnualizedRate()) >= 0) {
						return project.getMaxAnnualizedRate();// 最大收益
					} else {
						return rate;
					}
				}
			}
		}
		return null;
	}

	@Override
	public int deleteProjectById(Long id) throws ManagerException {
		try {
			return projectMapper.deleteProjectById(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public  Map<String,Object> findFrontProjectListByPage(ProjectQuery projectQuery) throws ManagerException {
		try {
			Map<String,Object> resMap =new HashMap<String,Object>();
			List<ProjectForFront> projectForFrontList = Lists.newArrayList();
			List<ProjectPackage> packageList = Lists.newArrayList();
			long normal_count = projectMapper.selectForPaginTotalCountFront(projectQuery);
			//查询销售中的项目
			int packageCount =0;
			//只有【全部 - 全部类型 - 投资中】和【全部 - 全部类型 - 全部状态】筛选条件下会显示集合列表
			if("all".equals(projectQuery.getInvestTypeCode()) &&
			("all".equals(projectQuery.getStatusCode()) || "investing".equals(projectQuery.getStatusCode()))
			 && projectQuery.getProjectType() == null){
				//先查资产包包数量
				packageCount = projectPackageMapper.getProjectPackageCount(projectQuery);
				if(packageCount > 0){
					packageList = projectPackageMapper.getProjectList(projectQuery);
				}else if(projectQuery.getCurrentPage() ==1){
					//重置分页偏移量和要显示查询的数量		
					ProjectQuery query = new ProjectQuery();
					BeanUtils.copyProperties(projectQuery, query);
					query.setStartRow(1);
					query.setPageSize(2);
					packageList = projectPackageMapper.getProjectListCompleted(query);
				}
			}
			resMap.put("packageList", packageList);
			//资产包的数量已经小于一页要显示的总数量
			if(packageList.size() < Constant.SMSVOICECOUNT && normal_count > 0){
                //重置分页偏移量和要显示查询的数量		
				ProjectQuery query = new ProjectQuery();
				BeanUtils.copyProperties(projectQuery, query);
				//新的起始位置 = 原始起始位置-当前资产包的数量
				int startRow =0;
				if(packageList.size() ==0){
					startRow = query.getStartRow() - packageCount;
				}else{
					startRow  = 0;
				}
				
				int pageSize = query.getPageSize() - packageList.size();
				query.setStartRow(startRow);
				query.setPageSize(pageSize);
				projectForFrontList = projectMapper.selectForPaginFront(query);
			}
			Page<ProjectForFront> page = new Page<ProjectForFront>();
			page.setiTotalDisplayRecords(packageCount+normal_count);
			page.setPageNo(projectQuery.getCurrentPage());
			page.setiDisplayLength(projectQuery.getPageSize());
			page.setiTotalRecords(packageCount+normal_count);
			page.setData(projectForFrontList);
			resMap.put("page", page);
			return resMap;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public Page<ProjectForFront> findFrontProjectListExtraByPage(ProjectQuery projectQuery) throws ManagerException {
		try {			
			List<ProjectForFront> projectForFrontList = Lists.newArrayList();
			long count = projectMapper.selectForPaginTotalCountFront(projectQuery);
			if (count > 0) {
				projectForFrontList = projectMapper.selectForPaginFront(projectQuery);
				for(ProjectForFront projectForFront : projectForFrontList){
					this.extraInfoForAPP(projectForFront);
				}
			}
			Page<ProjectForFront> page = new Page<ProjectForFront>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(projectQuery.getCurrentPage());
			page.setiDisplayLength(projectQuery.getPageSize());
			page.setiTotalRecords(count);
			page.setData(projectForFrontList);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	private void extraInfoForAPP(ProjectForFront projectForFront) {
		Long projectId = projectForFront.getId();
		String key = projectId + RedisConstant.REDIS_SEPERATOR
    			+ RedisConstant.REDIS_KEY_PROJECT;
		Map<String,Object> map = Maps.newHashMap();
		//投资人数
		Integer investNum = 0 ;
		//投资最高额
		BigDecimal mostInvestAmount = BigDecimal.ZERO;
		//项目总收益
		BigDecimal totalIncome = BigDecimal.ZERO;
		
		if(RedisManager.isExit(key)){
			//Object m = RedisManager.getObject(key);
			map = RedisManager.hgetAll(key);
		}
		
		if(((projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_FULL.getStatus()||projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_END.getStatus())
					&& projectForFront.getInvestType()==ProjectEnum.PROJECT_TYPE_DEBT.getType())
				||(projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()&&projectForFront.getInvestType()==ProjectEnum.PROJECT_TYPE_DIRECT.getType())
				||((projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_FULL.getStatus()||projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus())
						&&projectForFront.getInvestType()==ProjectEnum.PROJECT_TYPE_DIRECT.getType())
				||(projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_LOSE.getStatus())||(projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_LOSING.getStatus())
				||(projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_END.getStatus()&&projectForFront.getInvestType()==ProjectEnum.PROJECT_TYPE_DIRECT.getType())
				||(projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus()&&projectForFront.getInvestType()==ProjectEnum.PROJECT_TYPE_DIRECT.getType())
				){
			if(map.containsKey(RedisConstant.REDIS_FIELD_PROJECT_INVEST_MEMBER)){
				investNum = Integer.valueOf(map.get(RedisConstant.REDIS_FIELD_PROJECT_INVEST_MEMBER).toString());
			}else{
				investNum = transactionMapper.getTransactionMemberCountByProject(projectId);
				RedisManager.hset(key,
		    			RedisConstant.REDIS_FIELD_PROJECT_INVEST_MEMBER,investNum.toString());
			}
		}
		
		if(((projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_FULL.getStatus()||projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_END.getStatus())
				&& projectForFront.getInvestType()==ProjectEnum.PROJECT_TYPE_DEBT.getType())
			||(projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()&&projectForFront.getInvestType()==ProjectEnum.PROJECT_TYPE_DIRECT.getType())
			||((projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()||projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_FULL.getStatus())
			&&projectForFront.getInvestType()==ProjectEnum.PROJECT_TYPE_DIRECT.getType())
			||(projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_LOSING.getStatus()&&projectForFront.getSaleComplatedTime()!=null)
			||(projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus()&&projectForFront.getInvestType()==ProjectEnum.PROJECT_TYPE_DIRECT.getType())
			){
			if(map.containsKey(RedisConstant.REDIS_FIELD_PROJECT_MOST_INVEST_AMOUNT)){
				mostInvestAmount = new BigDecimal(map.get(RedisConstant.REDIS_FIELD_PROJECT_MOST_INVEST_AMOUNT).toString());
			}else{
				Transaction transaction = transactionMapper.selectMostTransactionByProject(projectId);
				mostInvestAmount = (transaction!=null?transaction.getInvestAmount():BigDecimal.ZERO);
				RedisManager.hset(key,
		    			RedisConstant.REDIS_FIELD_PROJECT_MOST_INVEST_AMOUNT,mostInvestAmount.toString());
			}
		}
		
		if(projectForFront.getStatus()==StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()){
			if(map.containsKey(RedisConstant.REDIS_FIELD_PROJECT_TOTAL_AMOUNT)){
				totalIncome = new BigDecimal(map.get(RedisConstant.REDIS_FIELD_PROJECT_TOTAL_AMOUNT).toString());
			}else{
				totalIncome = transactionMapper.getTotalTransactionReceivedInterestByProject(projectId);
				totalIncome = (totalIncome!=null?totalIncome:BigDecimal.ZERO);
				RedisManager.hset(key,
		    			RedisConstant.REDIS_FIELD_PROJECT_TOTAL_AMOUNT,totalIncome.toString());
			}	
		}
		projectForFront.setInvestNum(investNum);
		projectForFront.setMostInvestAmount(mostInvestAmount);
		projectForFront.setTotalIncome(totalIncome);
	}
	
	
	@Override
	public Page<ProjectForFront> p2pFindFrontProjectListByPage(ProjectQuery projectQuery) throws ManagerException {
		try {
			List<ProjectForFront> projectForFrontList = Lists.newArrayList();
			long count = projectMapper.p2pSelectForPaginTotalCountFront(projectQuery);
			if (count > 0) {
				projectForFrontList = projectMapper.p2pSelectForPaginFront(projectQuery);
			}
			Page<ProjectForFront> page = new Page<ProjectForFront>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(projectQuery.getCurrentPage());
			page.setiDisplayLength(projectQuery.getPageSize());
			page.setiTotalRecords(count);
			page.setData(projectForFrontList);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int investingProjectTask() throws ManagerException {
		try {
			return projectMapper.investingProjectTask();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int endSaleProjectTask() throws ManagerException {
		try {
			return projectMapper.endSaleProjectTask();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int repaymentProjectTask() throws ManagerException {
		try {
			return projectMapper.repaymentProjectTask();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int repaymentDirectProjectTask() throws ManagerException {
		try {
			return projectMapper.repaymentDirectProjectTask();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public boolean checkNameExists(String name, String namePeriod) throws ManagerException {
		try {
			Project p = projectMapper.checkNameExists(name, namePeriod, -1L);
			if (p == null) {
				return false;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return true;
	}

	@Override
	public boolean checkNameExists(String name, String namePeriod, Long id) throws ManagerException {
		try {
			Project p = projectMapper.checkNameExists(name, namePeriod, id);
			if (p == null) {
				return false;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return true;
	}

	@Override
	public int updateOnlineTimeAndEndDate(Timestamp onlineTime, Timestamp saleEndTime, Long id) throws ManagerException {
		try {
			return projectMapper.updateOnlineTimeAndEndDate(onlineTime, saleEndTime, id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateEndDate(Timestamp saleEndTime, Long id) throws ManagerException {
		try {
			return projectMapper.updateEndDate(saleEndTime, id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int findMaxRecommendWeight() throws ManagerException {
		try {
			Integer weight = projectMapper.findMaxRecommendWeight();
			if (weight == null) {
				return 0;
			}
			return weight;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int recommendProject(Long id, int recommendWeight) throws ManagerException {
		try {
			return projectMapper.recommendProject(id, recommendWeight);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int resetRecommendWeight(int recommendWeight) throws ManagerException {
		try {
			return projectMapper.resetRecommendWeight(recommendWeight);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<Project> findProjectRecommendByPage(Page<Project> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = projectMapper.selectProjectRecommendForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<Project> selectForPagin = projectMapper.selectProjectRecommendForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> findIndexInvestingProjectList(Integer num,Integer investType) throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("num", num);
			map.put("investType", investType);
			return projectMapper.findIndexInvestingProjectList(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectForFront getProjectForFrontByProjectId(Long projectId) throws ManagerException {
		try {
			return projectMapper.getProjectForFrontByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> findRecommendProjectList(Integer num,Integer investType) throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("num", num);
			map.put("investType", investType);
			return projectMapper.findRecommendProjectList(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int cancelRecommendByNotInvestingState() throws ManagerException {
		try {
			return projectMapper.cancelRecommendByNotInvestingState();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> findIndexNotInvestingProjectList(Integer num,Integer investType) throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("num", num);
			map.put("investType", investType);
			return projectMapper.findIndexNotInvestingProjectList(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int cancelRecommendByProjectId(Long id) throws ManagerException {
		try {
			return projectMapper.cancelRecommendByProjectId(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Long> findProjectByLoanStatus(Map<String, Object> map) throws ManagerException {
		try {
			return projectMapper.findProjectByLoanStatus(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int findProjectCountByStatus(Integer status) throws ManagerException {
		try {
			return projectMapper.findProjectCountByStatus(status);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> getNoviceProjectByInvesting(Integer num) throws ManagerException {
		try {
			return projectMapper.getNoviceProjectByInvesting(num);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> getNoviceProjectByNotInvesting(Integer num) throws ManagerException {
		try {
			return projectMapper.getNoviceProjectByNotInvesting(num);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Long findProjectIdByDebtId(Long debtId) throws ManagerException {
		try {
			return projectMapper.findProjectIdByDebtId(debtId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Project> getProjectListToZhongNiu() throws ManagerException {
		try {
			return projectMapper.getProjectListToZhongNiu();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Project> findUpcomingProject() throws ManagerException {
		try {
			return projectMapper.findUpcomingProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int findMaxAppRecommendWeight() throws ManagerException {
		try {
			Integer weight = projectMapper.findMaxAppRecommendWeight();
			if (weight == null) {
				return 0;
			}
			return weight;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int recommendAppProject(Long id, int recommendWeight) throws ManagerException {
		try {
			return projectMapper.recommendAppProject(id, recommendWeight);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int resetAppRecommendWeight(int recommendWeight) throws ManagerException {
		try {
			return projectMapper.resetAppRecommendWeight(recommendWeight);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int cancelAppRecommendByProjectId(Long id) throws ManagerException {
		try {
			return projectMapper.cancelAppRecommendByProjectId(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> findAppRecommendProjectList(Integer num) throws ManagerException {
		try {
			return projectMapper.findAppRecommendProjectList(num);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> findAppIndexProjectList(Integer num, Integer status) throws ManagerException {
		try {
			return projectMapper.findAppIndexProjectList(num, status);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> queryInvestingProject() throws ManagerException {
		try {
			return projectMapper.queryInvestingProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public List<Project> isInvestingProjectByProject() throws ManagerException {
		try {
			return projectMapper.isInvestingProjectByProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	

	@Override
	public Project getProjectBySortIndex(int sort, int recommendType) throws ManagerException {
		try {
			return projectMapper.getProjectBySortIndex(sort - 1, recommendType);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Project> queryProjectFromLvGou(Map<String, Object> paramMap) throws ManagerException {
		try {
			return projectMapper.queryProjectFromLvGou(paramMap);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectForFront findLandingRecommendProject() throws ManagerException {
		try {
			return projectMapper.findLandingRecommendProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean isExistProjectByType(String projectType, Integer instalment, String debtType) throws ManagerException {
		boolean result = false;
		try {
			Integer invest = projectMapper.isExistProjectByType(projectType, instalment, debtType);
			if (invest == null) {
				result = false;
			} else {
				result = true;
			}
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取支持一羊领头等的项目
	 */
	@Override
	public List<ActivityLeadingSheep> getProjectForLeadingSheeps() throws ManagerException {
		List<ActivityLeadingSheep> activityLeadingSheepList = Lists.newArrayList();
		Optional<Activity> optAct = LotteryContainer.getInstance().getActivityByName(ActivityConstant.FIVE_RITES);
		if(optAct.isPresent()&&DateUtil.isBeforeNow(optAct.get().getStartTime())){
			activityLeadingSheepList=getP2pProjectForLeadingSheeps();
			return activityLeadingSheepList;
		}
		// 一羊领头
		ActivityLeadingSheep firstSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> firstSheepProjects = Lists.newArrayList();
		// 预告的项目 单个
		ProjectNoticeForFront indexShow = projectNoticeMapper.getProjectNoticeByIndexShow();
		Integer limitSize = 4;
		if (indexShow != null) {
			ProjectForFront project = projectMapper.getProjectForFrontByProjectId(indexShow.getProjectId());
			if (project != null && project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus()) {
				limitSize = limitSize - 1;
				ActivityLeadingSheepProject leadingSheepActivity = new ActivityLeadingSheepProject();
				leadingSheepActivity.setId(project.getId());
				leadingSheepActivity.setOnlineTime(project.getOnlineTime());
				leadingSheepActivity.setName(project.getName());
				leadingSheepActivity.setStatus(project.getStatus());
				leadingSheepActivity.setTotalAmount(project.getTotalAmount());
				leadingSheepActivity.setThumbnail(project.getThumbnail());
				leadingSheepActivity.setNotice(true);
				firstSheepProjects.add(leadingSheepActivity);
			}
		}
		// 投资中但是无一羊领头的项目
		List<ActivityLeadingSheepProject> investNoFirstInvestProject = projectMapper.findInvestingAndFirstInvestProjects(limitSize);
		if (Collections3.isNotEmpty(investNoFirstInvestProject)) {
			firstSheepProjects.addAll(investNoFirstInvestProject);
		}
		if (Collections3.isNotEmpty(firstSheepProjects)) {
			/*
			 * 存在一羊领头项目
			 */
			firstSheeps.setExist(true);
		} else {
			// 不存在一羊领头项目：获取最近一个获取一羊领头的项目
			ActivityLeadingSheepProject lastLeadingSheepProject = popularityInOutLogMapper
					.findLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
			if (lastLeadingSheepProject != null) {
				// 根据项目id获取第一笔交易
				Transaction transaction = transactionMapper.selectFirstTransactionByProject(lastLeadingSheepProject.getId());
				if (transaction != null) {
					lastLeadingSheepProject.setTransactionTime(transaction.getTransactionTime());
					firstSheepProjects.add(lastLeadingSheepProject);
				}
			}
		}
		firstSheeps.setActivityLeadingSheepProjects(firstSheepProjects);
		activityLeadingSheepList.add(firstSheeps);

		// 一鸣惊人
		ActivityLeadingSheep mostSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> mostSheepProjects = Lists.newArrayList();
		mostSheepProjects = projectMapper.findSupportMostInvestProject();
		if (Collections3.isNotEmpty(mostSheepProjects)) {
			/*
			 * 存在一鸣惊人项目
			 */
			mostSheeps.setExist(true);// 存在一鸣惊人项目
			for (ActivityLeadingSheepProject project : mostSheepProjects) {
				Transaction transaction = transactionMapper.selectMostTransactionByProject(project.getId());
				if (transaction != null) {
					project.setInvestAmount(transaction.getInvestAmount());
					project.setMemberId(transaction.getMemberId());
				}
			}
		} else {
			// 不存在一鸣惊人：获取最近一个一鸣惊人的项目
			ActivityLeadingSheepProject lastLeadingSheepProject = popularityInOutLogMapper
					.findLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
			if (lastLeadingSheepProject != null) {
				Transaction transaction = transactionMapper.selectMostTransactionByProject(lastLeadingSheepProject.getId());
				if (transaction != null) {
					lastLeadingSheepProject.setInvestAmount(transaction.getInvestAmount());
				}
				mostSheepProjects.add(lastLeadingSheepProject);
			}
		}
		mostSheeps.setActivityLeadingSheepProjects(mostSheepProjects);
		activityLeadingSheepList.add(mostSheeps);

		// 一锤定音
		ActivityLeadingSheep lastSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> lastSheepProjects = Lists.newArrayList();
		lastSheepProjects = projectMapper.findInvestingProjectForLeadingSheep();
		if (Collections3.isNotEmpty(lastSheepProjects)) {
			/*
			 * 存在一锤定音项目
			 */
			lastSheeps.setExist(true);// 存在一锤定音项目
			for (ActivityLeadingSheepProject project : lastSheepProjects) {
				Balance _balance = balanceManager.queryBalance(project.getId(), TypeEnum.BALANCE_TYPE_PROJECT);
				if (_balance != null) {
					project.setInvestAmount(_balance.getBalance());
				}

			}
		} else {
			// 不存一锤定音项目：获取最近一个一锤定音项目
			ActivityLeadingSheepProject lastLeadingSheepProject = popularityInOutLogMapper
					.findLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
			if (lastLeadingSheepProject != null) {
				Transaction transaction = transactionMapper.selectLastTransactionByProject(lastLeadingSheepProject.getId());
				if (transaction != null) {
					lastLeadingSheepProject.setInvestAmount(transaction.getInvestAmount());// 一锤定音的投资额
				}
				lastSheepProjects.add(lastLeadingSheepProject);
			}
		}
		lastSheeps.setActivityLeadingSheepProjects(lastSheepProjects);
		activityLeadingSheepList.add(lastSheeps);

		// 一掷千金
		ActivityLeadingSheep lastAndMostSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> lastAndMostSheepsProjects = Lists.newArrayList();
		lastAndMostSheepsProjects = projectMapper.findLastAndMostProjectForLeadingSheep();
		if (Collections3.isNotEmpty(lastAndMostSheepsProjects)) {
			/*
			 * 存在一掷千金项目
			 */
			lastAndMostSheeps.setExist(true);// 存在一掷千金项目
			for (ActivityLeadingSheepProject project : lastAndMostSheepsProjects) {
				Balance _balance = balanceManager.queryBalance(project.getId(), TypeEnum.BALANCE_TYPE_PROJECT);
				if (_balance != null) {
					project.setInvestAmount(_balance.getBalance());
				}

			}
		} else {
			// 不存一掷千金项目：获取最近一个一掷千金项目
			ActivityLeadingSheepProject lastAndMostLeadingSheepProject = popularityInOutLogMapper
					.findLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks());
			if (lastAndMostLeadingSheepProject != null) {
				Transaction transaction = transactionMapper.selectLastTransactionByProject(lastAndMostLeadingSheepProject.getId());
				if (transaction != null) {
					lastAndMostLeadingSheepProject.setInvestAmount(transaction.getInvestAmount());// 一掷千金的投资额
				}
				lastAndMostSheepsProjects.add(lastAndMostLeadingSheepProject);
			}
		}
		lastAndMostSheeps.setActivityLeadingSheepProjects(lastAndMostSheepsProjects);
		activityLeadingSheepList.add(lastAndMostSheeps);

		// 幸运女神
		ActivityLeadingSheep luckSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> luckSheepProjects = Lists.newArrayList();
		luckSheepProjects = projectMapper.findInvestingProjectForLeadingSheep();
		if (Collections3.isNotEmpty(luckSheepProjects)) {
			luckSheeps.setExist(true);// 存在幸运女神项目
		} else {
			// 不存幸运女神项目：获取最近一个幸运女神项目
			ActivityLeadingSheepProject lastLeadingSheepProject = popularityInOutLogMapper
					.findLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
			if (lastLeadingSheepProject != null) {
				Integer investCount = transactionMapper.getTransactionMemberCountByProject(lastLeadingSheepProject.getId());
				if (investCount != null) {
					lastLeadingSheepProject.setInvestCount(investCount);// 项目id查询投资会员数
				}
				luckSheepProjects.add(lastLeadingSheepProject);
			}
		}
		luckSheeps.setActivityLeadingSheepProjects(luckSheepProjects);
		activityLeadingSheepList.add(luckSheeps);

		return activityLeadingSheepList;
	}
	/**
	 * 
	 * @Description:五重礼过滤直投
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月8日 下午1:38:48
	 */
	public List<ActivityLeadingSheep> getP2pProjectForLeadingSheeps() throws ManagerException {
		List<ActivityLeadingSheep> activityLeadingSheepList = Lists.newArrayList();

		// 一羊领头
		ActivityLeadingSheep firstSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> firstSheepProjects = Lists.newArrayList();
		// 预告的项目 单个
		ProjectNoticeForFront indexShow = projectNoticeMapper.p2pGetProjectNoticeByIndexShow();
		Integer limitSize = 4;
		if (indexShow != null) {
			ProjectForFront project = projectMapper.getProjectForFrontByProjectId(indexShow.getProjectId());
			if (project != null && project.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus()) {
				limitSize = limitSize - 1;
				ActivityLeadingSheepProject leadingSheepActivity = new ActivityLeadingSheepProject();
				leadingSheepActivity.setId(project.getId());
				leadingSheepActivity.setOnlineTime(project.getOnlineTime());
				leadingSheepActivity.setName(project.getName());
				leadingSheepActivity.setStatus(project.getStatus());
				leadingSheepActivity.setTotalAmount(project.getTotalAmount());
				leadingSheepActivity.setThumbnail(project.getThumbnail());
				leadingSheepActivity.setNotice(true);
				firstSheepProjects.add(leadingSheepActivity);
			}
		}
		// 投资中但是无一羊领头的项目
		List<ActivityLeadingSheepProject> investNoFirstInvestProject = projectMapper.p2pFindInvestingAndFirstInvestProjects(limitSize);
		if (Collections3.isNotEmpty(investNoFirstInvestProject)) {
			firstSheepProjects.addAll(investNoFirstInvestProject);
		}
		if (Collections3.isNotEmpty(firstSheepProjects)) {
			/*
			 * 存在一羊领头项目
			 */
			firstSheeps.setExist(true);
		} else {
			// 不存在一羊领头项目：获取最近一个获取一羊领头的项目
			ActivityLeadingSheepProject lastLeadingSheepProject = popularityInOutLogMapper
					.p2pFindLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
			if (lastLeadingSheepProject != null) {
				// 根据项目id获取第一笔交易
				Transaction transaction = transactionMapper.selectFirstTransactionByProject(lastLeadingSheepProject.getId());
				if (transaction != null) {
					lastLeadingSheepProject.setTransactionTime(transaction.getTransactionTime());
					firstSheepProjects.add(lastLeadingSheepProject);
				}
			}
		}
		firstSheeps.setActivityLeadingSheepProjects(firstSheepProjects);
		activityLeadingSheepList.add(firstSheeps);

		// 一鸣惊人
		ActivityLeadingSheep mostSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> mostSheepProjects = Lists.newArrayList();
		mostSheepProjects = projectMapper.p2pFindSupportMostInvestProject();
		if (Collections3.isNotEmpty(mostSheepProjects)) {
			/*
			 * 存在一鸣惊人项目
			 */
			mostSheeps.setExist(true);// 存在一鸣惊人项目
			for (ActivityLeadingSheepProject project : mostSheepProjects) {
				Transaction transaction = transactionMapper.selectMostTransactionByProject(project.getId());
				if (transaction != null) {
					project.setInvestAmount(transaction.getInvestAmount());
					project.setMemberId(transaction.getMemberId());
				}
			}
		} else {
			// 不存在一鸣惊人：获取最近一个一鸣惊人的项目
			ActivityLeadingSheepProject lastLeadingSheepProject = popularityInOutLogMapper
					.p2pFindLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
			if (lastLeadingSheepProject != null) {
				Transaction transaction = transactionMapper.selectMostTransactionByProject(lastLeadingSheepProject.getId());
				if (transaction != null) {
					lastLeadingSheepProject.setInvestAmount(transaction.getInvestAmount());
				}
				mostSheepProjects.add(lastLeadingSheepProject);
			}
		}
		mostSheeps.setActivityLeadingSheepProjects(mostSheepProjects);
		activityLeadingSheepList.add(mostSheeps);

		// 一锤定音
		ActivityLeadingSheep lastSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> lastSheepProjects = Lists.newArrayList();
		lastSheepProjects = projectMapper.p2pFindInvestingProjectForLeadingSheep();
		if (Collections3.isNotEmpty(lastSheepProjects)) {
			/*
			 * 存在一锤定音项目
			 */
			lastSheeps.setExist(true);// 存在一锤定音项目
			for (ActivityLeadingSheepProject project : lastSheepProjects) {
				Balance _balance = balanceManager.queryBalance(project.getId(), TypeEnum.BALANCE_TYPE_PROJECT);
				if (_balance != null) {
					project.setInvestAmount(_balance.getBalance());
				}

			}
		} else {
			// 不存一锤定音项目：获取最近一个一锤定音项目
			ActivityLeadingSheepProject lastLeadingSheepProject = popularityInOutLogMapper
					.p2pFindLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
			if (lastLeadingSheepProject != null) {
				Transaction transaction = transactionMapper.selectLastTransactionByProject(lastLeadingSheepProject.getId());
				if (transaction != null) {
					lastLeadingSheepProject.setInvestAmount(transaction.getInvestAmount());// 一锤定音的投资额
				}
				lastSheepProjects.add(lastLeadingSheepProject);
			}
		}
		lastSheeps.setActivityLeadingSheepProjects(lastSheepProjects);
		activityLeadingSheepList.add(lastSheeps);

		// 一掷千金
		ActivityLeadingSheep lastAndMostSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> lastAndMostSheepsProjects = Lists.newArrayList();
		lastAndMostSheepsProjects = projectMapper.p2pFindLastAndMostProjectForLeadingSheep();
		if (Collections3.isNotEmpty(lastAndMostSheepsProjects)) {
			/*
			 * 存在一掷千金项目
			 */
			lastAndMostSheeps.setExist(true);// 存在一掷千金项目
			for (ActivityLeadingSheepProject project : lastAndMostSheepsProjects) {
				Balance _balance = balanceManager.queryBalance(project.getId(), TypeEnum.BALANCE_TYPE_PROJECT);
				if (_balance != null) {
					project.setInvestAmount(_balance.getBalance());
				}

			}
		} else {
			// 不存一掷千金项目：获取最近一个一掷千金项目
			ActivityLeadingSheepProject lastAndMostLeadingSheepProject = popularityInOutLogMapper
					.p2pFindLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks());
			if (lastAndMostLeadingSheepProject != null) {
				Transaction transaction = transactionMapper.selectLastTransactionByProject(lastAndMostLeadingSheepProject.getId());
				if (transaction != null) {
					lastAndMostLeadingSheepProject.setInvestAmount(transaction.getInvestAmount());// 一掷千金的投资额
				}
				lastAndMostSheepsProjects.add(lastAndMostLeadingSheepProject);
			}
		}
		lastAndMostSheeps.setActivityLeadingSheepProjects(lastAndMostSheepsProjects);
		activityLeadingSheepList.add(lastAndMostSheeps);

		// 幸运女神
		ActivityLeadingSheep luckSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> luckSheepProjects = Lists.newArrayList();
		luckSheepProjects = projectMapper.p2pFindInvestingProjectForLeadingSheep();
		if (Collections3.isNotEmpty(luckSheepProjects)) {
			luckSheeps.setExist(true);// 存在幸运女神项目
		} else {
			// 不存幸运女神项目：获取最近一个幸运女神项目
			ActivityLeadingSheepProject lastLeadingSheepProject = popularityInOutLogMapper
					.p2pFindLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
			if (lastLeadingSheepProject != null) {
				Integer investCount = transactionMapper.getTransactionMemberCountByProject(lastLeadingSheepProject.getId());
				if (investCount != null) {
					lastLeadingSheepProject.setInvestCount(investCount);// 项目id查询投资会员数
				}
				luckSheepProjects.add(lastLeadingSheepProject);
			}
		}
		luckSheeps.setActivityLeadingSheepProjects(luckSheepProjects);
		activityLeadingSheepList.add(luckSheeps);

		return activityLeadingSheepList;
	}
	@Override
	public Project getSortFirstAppRecommendProject() throws ManagerException {
		try {
			return projectMapper.getSortFirstAppRecommendProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Project p2pGetSortFirstAppRecommendProject() throws ManagerException {
		try {
			return projectMapper.p2pGetSortFirstAppRecommendProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Project getLatestOnLineProject() throws ManagerException {
		try {
			return projectMapper.getLatestOnLineProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Project p2pGetLatestOnLineProject() throws ManagerException {
		try {
			return projectMapper.p2pGetLatestOnLineProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Project getFinishProject() throws ManagerException {
		try {
			return projectMapper.getFinishProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Project p2pGetFinishProject() throws ManagerException {
		try {
			return projectMapper.p2pGetFinishProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Project getNoviceProject() throws ManagerException {
		try {
			return projectMapper.getNoviceProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Project p2pGetNoviceProject() throws ManagerException {
		try {
			return projectMapper.p2pGetNoviceProject();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<ProjectForFront> findIndexNoviceProject(Integer num) throws ManagerException {
		try {
			List<ProjectForFront> indexNoviceProject = projectMapper.findIndexNoviceProject(num);
			if (Collections3.isEmpty(indexNoviceProject)) {
				indexNoviceProject = projectMapper.findIndexNoviceProjectNoRecommend(num);
			}
			return indexNoviceProject;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> getRecommendProjectByGuaranty(Map<String, Object> paraMap) throws ManagerException {
		try {
			return projectMapper.getRecommendProjectByGuaranty(paraMap);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean isProjectOfCannotUseCoupon(String projectGuarantyType) throws ManagerException {
		return false;
		// 优惠券限制解除
		// boolean flag = false;
		// try {
		// Set<String> set = new HashSet<String>();
		// set.add(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CARPAYIN.getCode());
		// set.add(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CARBUSINESS.getCode());
		// if(set.contains(projectGuarantyType)){
		// flag = true;
		// }
		// return flag;
		// } catch (Exception e) {
		// throw new ManagerException(e);
		// }
	}

	@Override
	public List<Project> queryProjectFromNotice(String projectName) throws ManagerException {
		try {
			return projectMapper.queryProjectFromNotice(projectName);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	// 判断项目类型为不能使用收益券的项目类型
	public boolean projectOfCanotUseProfitCoupon(String compareGuarantyType, int instalment) throws ManagerException {
		// 优惠券限制解除
		// try {
		// Set<String> set = new HashSet<String>();
		// set.add(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CAR.getCode());
		// if(set.contains(compareGuarantyType) && instalment == 1){
		// return true;
		// }
		// } catch (Exception e) {
		// throw new ManagerException(e);
		// }
		return false;
	}

	@Override
	public boolean isProjectOfCannotUseCoupon(Long projectId) throws ManagerException {
		try {
			Project project = this.selectByPrimaryKey(projectId);
			if (project != null && StringUtil.isNotBlank(project.getProjectType())) {
				return this.isProjectOfCannotUseCoupon(project.getProjectType());
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return false;
	}

	@Override
	public boolean isProjectOfCannotUseProfitCoupon(Long projectId) throws ManagerException {
		boolean flag = false;
		try {
			if (projectId == null) {
				return flag;
			}
			Project project = this.selectByPrimaryKey(projectId);
			if (project == null) {
				return flag;
			}
			// 等本等息不使用收益券
			/*if (project.getProfitType().equals(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode())) {
				return true;
			}*/
			Debt debt = debtManager.selectByPrimaryKey(project.getDebtId());
			if (debt != null && StringUtil.isNotBlank(debt.getGuarantyType())) {
				flag = this.projectOfCanotUseProfitCoupon(debt.getGuarantyType(), debt.getInstalment());
				return flag;
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return false;
	}

	@Override
	public boolean isBuyCar(Long projectId) throws ManagerException {
		boolean flag = false;
		try {
			if (projectId == null) {
				return flag;
			}
			Project project = this.selectByPrimaryKey(projectId);
			if (project == null) {
				return flag;
			}
			Debt debt = debtManager.selectByPrimaryKey(project.getDebtId());
			if (debt != null && StringUtil.isNotBlank(debt.getGuarantyType())) {
				if (debt.getGuarantyType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CAR.getCode()) && debt.getInstalment() == 1) {
					return true;
				}
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return false;
	}

	@Override
	public List<ProjectInvestingDto> getFullProjectsByEnterpriseId(Long enterpriseId) throws ManagerException {
		try {
			return projectMapper.getFullProjectsByEnterpriseId(enterpriseId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getRepaymentTotalAmountByEnterpriseId(Long enterpriseId) throws ManagerException {
		try {
			int[] status = { StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus() };
			BigDecimal repaymentTotalAmount = projectMapper.getTotalAmountByEnterpriseIdAndStatus(enterpriseId, status) == null ? BigDecimal.ZERO
					: projectMapper.getTotalAmountByEnterpriseIdAndStatus(enterpriseId, status);
			return repaymentTotalAmount;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getCurrentTotalAmountByEnterpriseId(Long enterpriseId) throws ManagerException {
		try {
			int[] status = { StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus(), StatusEnum.PROJECT_STATUS_INVESTING.getStatus(),
					StatusEnum.PROJECT_STATUS_STOP.getStatus(), StatusEnum.PROJECT_STATUS_END.getStatus(),
					StatusEnum.PROJECT_STATUS_FULL.getStatus() };
			BigDecimal currentTotalAmount = projectMapper.getTotalAmountByEnterpriseIdAndStatus(enterpriseId, status) == null ? BigDecimal.ZERO
					: projectMapper.getTotalAmountByEnterpriseIdAndStatus(enterpriseId, status);
			return currentTotalAmount;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getHistoryTotalAmountByEnterpriseId(Long enterpriseId) throws ManagerException {
		try {
			int[] status = { StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus(), StatusEnum.PROJECT_STATUS_INVESTING.getStatus(),
					StatusEnum.PROJECT_STATUS_STOP.getStatus(), StatusEnum.PROJECT_STATUS_END.getStatus(),
					StatusEnum.PROJECT_STATUS_FULL.getStatus(), StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus() };
			BigDecimal historyTotalAmount = projectMapper.getTotalAmountByEnterpriseIdAndStatus(enterpriseId, status) == null ? BigDecimal.ZERO
					: projectMapper.getTotalAmountByEnterpriseIdAndStatus(enterpriseId, status);
			return historyTotalAmount;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Project> repaymentProjectAddAvailableCredit() throws ManagerException {
		try {
			List<Project> projects = projectMapper.selectToRepaymentProject();
			if (Collections3.isNotEmpty(projects)) {
				for (Project project : projects) {
					Debt debt = new Debt();
					if (project.getProjectType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode())) {
						debt = debtManager.selectEnterpriseIdByProjectId(project.getId());
						if (debt != null && debt.getEnterpriseId() != null) {
							balanceManager.reduceBalance(TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT, debt.getAmount(),
									debt.getEnterpriseId(), BalanceAction.balance_Available_add);
						}
					}
				}
			}
			return projects;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 分页查询放款管理
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月11日 下午5:32:10
	 *
	 */
	@Override
	public Page<DirectProjectBiz> selectLoanForPagin(Page<DirectProjectBiz> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			List<DirectProjectBiz> projectBizs = Lists.newArrayList();
			if(map.get("borrowerId")!=null&&map.get("borrowerId").toString().contains("YRUC")){
				String[] ary = map.get("borrowerId").toString().split("YRUC");
				if(ary.length>0){
					map.put("borrowerId",ary[1]);
				}
			}
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			/*int[] status = { StatusEnum.PROJECT_STATUS_FULL.getStatus(), StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus(),
					StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus(),StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus() };
			map.put("status", status);*/
			int totalCount = projectMapper.selectLoanForPaginTotalCount(map);
			if(totalCount>0){
				List<Project> selectForPagin = projectMapper.selectLoanForPagin(map);
				projectBizs = getLoanBiz(selectForPagin);
			}
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			pageRequest.setData(projectBizs);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @Description:放款列表信息
	 * @param loanProjectList
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月13日 上午11:59:52
	 */
	private List<DirectProjectBiz> getLoanBiz(List<Project> loanProjectList) throws ManagerException {
		List<DirectProjectBiz> bizs = Lists.newArrayList();
		DirectProjectBiz directProjectBiz = null;
		if (Collections3.isNotEmpty(loanProjectList)) {
			for (Project project : loanProjectList) {
				directProjectBiz = new DirectProjectBiz();
				BeanCopyUtil.copy(project, directProjectBiz);
				//现金券总金额
				directProjectBiz.setTotalCouponAmount(getTotalCouponAmount(project.getId()));
				//管理费
				BigDecimal manageFee = BigDecimal.ZERO;
				manageFee=FormulaUtil.getManagerAmount(directProjectBiz.getTotalAmount(),directProjectBiz.getManageFeeRate());
				directProjectBiz.setManageFee(getFormatManageFee(directProjectBiz.getTotalAmount(),directProjectBiz.getManageFeeRate()));
				//可放款金额
				directProjectBiz.setLoanAmount(directProjectBiz.getTotalAmount().subtract(manageFee));
				if(directProjectBiz.getLoanAmount() != null && BigDecimal.ZERO.compareTo(directProjectBiz.getLoanAmount())<0){
					directProjectBiz.setAbleLoanAmount(FormulaUtil.getFormatPrice(directProjectBiz.getLoanAmount()));
				}
				//用户投资总额
				BigDecimal investAmount=transactionMapper.getTotalInvestAmountByProjectId(project.getId());
				directProjectBiz.setInvestAmount(investAmount);
				//放款时间
				LoanDetail loanDetail=loanDetailMapper.getLoanTimeByProjectId(project.getId());
				if(loanDetail!=null){
					directProjectBiz.setLoanDate(loanDetail.getLoanTime());
				}
				//快投奖金
				BigDecimal ratioAmount = BigDecimal.ZERO;
				ProjectExtra pro = projectExtraManager.getProjectQucikReward(project.getId());
				if(pro.getActivitySign()!=null&&pro.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
					//奖池金额
					ActivityLotteryResult model=new ActivityLotteryResult();
					model.setActivityId(pro.getActivityId());
					model.setRemark(project.getId().toString());
					model.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);//中奖标识
					List<ActivityLotteryResult> listActivityResult=activityLotteryResultManager.getLotteryResultBySelectiveAndLotteryStatus(model);
					if(Collections3.isNotEmpty(listActivityResult)){
						for(ActivityLotteryResult lottery:listActivityResult){
							ratioAmount = ratioAmount.add(lottery.getRewardInfo()!=null?new BigDecimal(lottery.getRewardInfo()):BigDecimal.ZERO);
						}
					}
				}
				directProjectBiz.setQuickReward(ratioAmount);
				if(StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()==project.getStatus()||StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()==project.getStatus()){
					Map<String, Object> map = Maps.newHashMap();
					map.put("sourceId", project.getId());
					map.put("operateTableType", TypeEnum.OPERATE_TYPE_PROJECT.getType());
					map.put("operateCode", TypeEnum.OPERATE_DIRECT_LOAN.getCode());
					SysOperateInfo sysOperateInfo=sysOperateInfoManager.selectOperateBySourceId(map);
					if(sysOperateInfo!=null){
						SysUser sysUser=sysUserManager.selectByPrimaryKey(sysOperateInfo.getOperateId());
						if(sysUser!=null){
							directProjectBiz.setOperateName(sysUser.getLoginName());
						}
					}
				}
				bizs.add(directProjectBiz);
			}
		}
		return bizs;
	}
	/**
	 * 
	 * @Description:获取管理费
	 * @param totalAmount
	 * @param manageFeeRate
	 * @return
	 * @author: chaisen
	 * @time:2016年2月17日 下午5:05:34
	 */
	private String getFormatManageFee(BigDecimal totalAmount,BigDecimal manageFeeRate) {
		BigDecimal manageFee = BigDecimal.ZERO;
		if(totalAmount == null || BigDecimal.ZERO.compareTo(totalAmount)==0){
			return "￥0";
		}
		if(manageFeeRate == null || BigDecimal.ZERO.compareTo(manageFeeRate)==0){
			return "￥0";
		}
		manageFee=FormulaUtil.getManagerAmount(totalAmount,manageFeeRate);
		if(manageFee == null || BigDecimal.ZERO.compareTo(manageFee)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(manageFee);
	}
	/**
	 * 
	 * @Description:现金券金额
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月7日 下午3:39:15
	 */
	private BigDecimal getTotalCouponAmount(Long projectId) throws ManagerException {
		try {
			BigDecimal historyTotalAmount = transactionMapper.getCouponAmountForPaltform(projectId);
			if (historyTotalAmount == null) {
				historyTotalAmount = BigDecimal.ZERO;
			}
			return historyTotalAmount;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<DirectProjectBiz> directFindByPage(Page<DirectProjectBiz> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = projectMapper.directSelectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<Project> directProjectList = projectMapper.directSelectForPagin(map);
			List<DirectProjectBiz> projectBizs = getDirectBiz(directProjectList);
			pageRequest.setData(projectBizs);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<Project> selectPaymentingProject()throws ManagerException {
		try{
			return projectMapper.selectPaymentingProject();
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取支持一锤定音的项目
	 */
	@Override
	public ActivityLeadingSheep getLastProjects() throws ManagerException {

		// 一锤定音
		ActivityLeadingSheep lastSheeps = new ActivityLeadingSheep();
		List<ActivityLeadingSheepProject> lastSheepProjects = projectMapper.findInvestingProjectForLeadingSheep();
		if (Collections3.isNotEmpty(lastSheepProjects)) {
			/*
			 * 存在一锤定音项目
			 */
			lastSheeps.setExist(true);// 存在一锤定音项目
			for (ActivityLeadingSheepProject project : lastSheepProjects) {
				Balance _balance = balanceManager.queryBalance(project.getId(), TypeEnum.BALANCE_TYPE_PROJECT);
				if (_balance != null) {
					project.setInvestAmount(_balance.getBalance());
				}

			}
		} else {
			// 不存一锤定音项目：获取最近一个一锤定音项目
			ActivityLeadingSheepProject lastLeadingSheepProject = popularityInOutLogMapper
					.findLastLeadingSheepProject(RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
			if (lastLeadingSheepProject != null) {
				Transaction transaction = transactionMapper.selectLastTransactionByProject(lastLeadingSheepProject.getId());
				if (transaction != null) {
					lastLeadingSheepProject.setInvestAmount(transaction.getInvestAmount());// 一锤定音的投资额
				}
				lastSheepProjects.add(lastLeadingSheepProject);
			}
		}
		lastSheeps.setActivityLeadingSheepProjects(lastSheepProjects);

		return lastSheeps;
	}

	@Override
	public List<ProjectForFront> selectExtraProject(Map<String, Object> map, int[] projectStatus) throws ManagerException {
		try {
			return projectMapper.selectExtraProject(map, projectStatus);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> getFirstInvestProject(Integer num) throws ManagerException {
		try {
			return projectMapper.getFirstInvestProject(num);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * @Description:获取直投项目列表数据的详细信息
	 * @param directProjectList
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2015年12月30日 下午7:50:45
	 */
	private List<DirectProjectBiz> getDirectBiz(List<Project> directProjectList) throws ManagerException {
		List<DirectProjectBiz> bizs = Lists.newArrayList();
		DirectProjectBiz directProjectBiz = null;
		if (Collections3.isNotEmpty(directProjectList)) {
			for (Project project : directProjectList) {
				directProjectBiz = new DirectProjectBiz();
				BeanCopyUtil.copy(project, directProjectBiz);
				// 查询借款用户信息
				if (project.getBorrowerId() != null) {
					MemberBaseBiz memberBiz = memberManager.selectMemberBaseBiz(project.getBorrowerId());
					if (memberBiz != null) {
						directProjectBiz.setBorrowerMemberBaseBiz(memberBiz);
					}
				}
				// 项目投资进度
				directProjectBiz.setProgress(getProjectNumberProgress(directProjectBiz.getTotalAmount(), directProjectBiz.getId()));
				directProjectBiz.setOperateName(project.getAuditName());
				directProjectBiz.setPublishName(project.getPublishName());
				bizs.add(directProjectBiz);
			}
		}
		return bizs;
	}

	/**
	 * @Description:计算项目进度
	 * @param totalAmount
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2015年12月31日 下午2:42:38
	 */
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

	/**
	 * @Description:获取项目余额
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2015年12月31日 下午2:47:18
	 */
	private BigDecimal getProjectBalanceById(Long id) throws ManagerException {
		// 可用余额
		BigDecimal availableBalance = null;
		// 如果为Null，到余额表找
		Balance balance = balanceManager.queryBalance(id, TypeEnum.BALANCE_TYPE_PROJECT);
		if (balance != null) {
			availableBalance = balance.getAvailableBalance();
		}
		if (availableBalance == null) {
			// 再没有，那只能从项目中去找了
			Project project = selectByPrimaryKey(id);
			availableBalance = project.getTotalAmount();
		}
		return availableBalance;
	}

	@Override
	public Project insertDirectProject(DirectProjectBiz directProjectBiz) throws ManagerException {
		Project project = new Project();
		BeanCopyUtil.copy(directProjectBiz, project);
		projectMapper.insertDirectProject(project);
		return project;
	}

	/**
	 * 
	 * @desc 分页查询还本付息列表
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月6日 上午10:03:31
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> findRepayInterestForPagin(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map)
			throws ManagerException {
		map.put("startRow", pageRequest.getiDisplayStart());
		map.put("pageSize", pageRequest.getiDisplayLength());
		List<ProjectInterestBiz> projectBizs = Lists.newArrayList();
		if(map.get("createdStartTime")!=null){
			String start=map.get("createdStartTime").toString();
			map.put("createdStartTime", start+" 00:00:00");
		}
		if(map.get("createdEndTime")!=null){
			String start=map.get("createdEndTime").toString();
			map.put("createdEndTime", start+" 23:59:59");
		}
		int totalCount = payPrincipalInterestMapper.selectDirectForPaginTotalCount(map);
		if(totalCount>0){
			List<ProjectInterestBiz> interestBizs = payPrincipalInterestMapper.selectDirectForPagin(map);
			for (ProjectInterestBiz payBiz : interestBizs) {
				payBiz=findPayPrincipalAndInterestByProject(payBiz);
				projectBizs.add(payBiz);
			}
		}
		pageRequest.setiTotalDisplayRecords(totalCount);
		pageRequest.setiTotalRecords(totalCount);
		pageRequest.setData(projectBizs);
		return pageRequest;
	}
	/**
	 * 
	 * @desc 还本付息详细信息
	 * @param interestBiz
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月19日 下午4:33:25
	 *
	 */
	@Override
	public ProjectInterestBiz findPayPrincipalAndInterestByProject(ProjectInterestBiz interestBiz) throws ManagerException {
		Long id = interestBiz.getProjectId();
		if (id != null) {
			// 总期数
			Map<String, Object> map = Maps.newHashMap();
			map.put("projectId", id);
			int totalPeriods = debtInterestManager.findDPeriodsByProjectId(map);
			interestBiz.setTotalPeriods(totalPeriods);
			// 当前期数
			map.put("endDate", interestBiz.getEndDate());
			int paidPeriods = debtInterestManager.findDPeriodsByProjectId(map);
			interestBiz.setCurrentPeriods(paidPeriods);
			
			// 距离到期日
			Date nowDate = DateUtils.getCurrentDate();
			Date endDate = DateUtils.addHour(interestBiz.getEndDate(), Constant.PAY_PRINCIPAL_INTEREST_TIME);
			//距离到期日
			if (DateUtils.daysOfTwo(nowDate, endDate) > 0) {
				interestBiz
						.setExpireDays(DateUtils.daysOfTwo(DateUtils.getCurrentDate(), interestBiz.getEndDate()) + 1);
			} else if (DateUtils.getTimeIntervalHours(nowDate, endDate) > 0) {
				interestBiz.setExpireHours(DateUtils.getTimeIntervalHours(nowDate, endDate) + 1);
			}
			// 查询垫付记录表状态
			UnderwriteLog underwriteLog=underWriteLogManager.getUnderwriteLogByInterestId(interestBiz.getInterestId());
			if(underwriteLog!=null){
				interestBiz.setUnderwriteState(underwriteLog.getUnderwriteStatus());
				ThirdCompany thirdCompany=thirdCompanyManager.getCompanyByMemberId(underwriteLog.getUnderwriteMemberId());
				if(thirdCompany!=null){
					interestBiz.setThirdPayName(thirdCompany.getCompanyName());
				}
			}
			//是否满足撤销垫资还款
			if(underwriteLog!=null&&underwriteLog.getUnderwriteStatus()==StatusEnum.UNDERWRITE_STATUS_WAIT_UNDERWRITE.getStatus()){
				if(DateUtils.getSpecialTime(interestBiz.getEndDate(),13).getTime()> DateUtils.getCurrentDate().getTime()){
					interestBiz.setCanelUnderWrite(true);
				}
			}
			//本金
			BigDecimal principal = BigDecimal.ZERO;
			//总利息
			BigDecimal interest = BigDecimal.ZERO;
			//应支付利息
			BigDecimal realInterest = BigDecimal.ZERO;
			//应支付总额
			BigDecimal realTotalAmount = BigDecimal.ZERO;
			//平台贴息
			BigDecimal extraInterest = BigDecimal.ZERO;
			if(interestBiz.getPayablePrincipal()!=null){
				principal=interestBiz.getPayablePrincipal();
			}
			if(interestBiz.getPayableInterest()!=null){
				interest=interestBiz.getPayableInterest();
			}
			if(interestBiz.getExtraInterest()!=null){
				extraInterest=interestBiz.getExtraInterest();
			}
			//需支付利息=总支付利息-平台贴息
			realInterest=interest.subtract(extraInterest);
			realTotalAmount=principal.add(realInterest);
			interestBiz.setRealInterest(realInterest);
			interestBiz.setTotalPayAmount(principal.add(interest));
			interestBiz.setRealPayAmount(realTotalAmount);
			if (interestBiz.getStatus()!= null) {
				//已付
				if (interestBiz.getStatus().intValue() == 0) {
					interestBiz.setAllStatus(StatusEnum.FIN_PAY_PRINCIPAL_INTEREST_CURRENT_PAID.getStatus());
				} else {
					// 未付
					interestBiz.setAllStatus(StatusEnum.FIN_PAY_PRINCIPAL_INTEREST_WAIT_PAY.getStatus());
				}
				
			}
		}
		return interestBiz;
	}

	/**
	 * 
	 * @desc 获取垫资代付信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月6日 下午5:23:13
	 *
	 */
	@Override
	public ProjectInterestBiz selectInterestInfoByPrimaryKey(Long id) throws ManagerException {
		ProjectInterestBiz projectBiz=new ProjectInterestBiz();
	try {
		projectBiz=debtInterestMapper.selectInterestInfoByPrimaryKey(id);
		if(projectBiz!=null){
			// 总期数
			Map<String, Object> map = Maps.newHashMap();
			map.put("projectId", projectBiz.getProjectId());
			int totalPeriods = debtInterestManager.findDPeriodsByProjectId(map);
			// 当前期数
			map.put("endDate", projectBiz.getEndDate());
			int paidPeriods = debtInterestManager.findDPeriodsByProjectId(map);
			projectBiz.setPeriods(paidPeriods+"/"+totalPeriods);
			projectBiz.setTotalPayAmount(projectBiz.getPayablePrincipal().add(projectBiz.getPayableInterest()));
			Member member=memberManager.selectByPrimaryKey(projectBiz.getBorrowerId());
			if(member!=null){
				projectBiz.setMobile(member.getMobile());
				projectBiz.setBorrowerName(member.getTrueName());
			}
		}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return projectBiz;
	}

	/**
	 * @desc 根据项目编号或者名称查询项目
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author fuyili
	 * @time 2016年1月7日 下午8:14:04
	 *
	 */
	@Override
	public int findDirectProjectByProjectNameOrCode(Map<String, Object> map) throws ManagerException {
		try {
			return projectMapper.findDirectProjectByProjectNameOrCode(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * @desc 查询直投项目详情
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author fuyili
	 * @time 2016年1月7日 下午8:49:47
	 *
	 */
	@Override
	public DirectProjectBiz findDirectProjectBizById(Long id) throws ManagerException {
		try {
			DirectProjectBiz directBiz = new DirectProjectBiz();
			// 项目信息
			Project project = selectByPrimaryKey(id);
			if (project != null) {
				directBiz = BeanCopyUtil.map(project, DirectProjectBiz.class);
			}
			// 附件信息
			List<BscAttachment> bscAttachments = bscAttachmentMapper.findAttachmentsByKeyId(String.valueOf(project.getId()));
			if (!Collections3.isEmpty(bscAttachments)) {
				directBiz.setBscAttachments(bscAttachments);
			}
			// 查询借款人信息
			MemberBaseBiz borrower = memberManager.selectMemberBaseBiz(project.getBorrowerId());
			directBiz.setBorrowerMemberBaseBiz(borrower);
			
			if(project.getIntroducerId()!=null){
				// 查询介绍人信息
				MemberBaseBiz introducerMemberBaseBiz = memberManager.selectMemberBaseBiz(project.getIntroducerId());
				directBiz.setIntroducerMemberBaseBiz(introducerMemberBaseBiz);;
			}
			
			//企业信息
			if(TypeEnum.MEMBER_TYPE_COMPANY.getType()==directBiz.getBorrowerType()||TypeEnum.MEMBER_TYPE_ORG.getType()==directBiz.getBorrowerType()){
				Enterprise enterprise = enterpriseMapper.selectByPrimaryKey(project.getEnterpriseId());
				directBiz.setEnterprise(enterprise);
			}
			// 抵质押物信息
			DebtCollateral debtCollateral = debtCollateralMapper.findCollateralByProjectId(project.getId());
			directBiz.setDebtCollateral(debtCollateral);
			
			//项目额外活动信息
			ProjectExtra projectExtraQuickReward =projectExtraManager.getProjectQucikReward(id);
			directBiz.setCatalyzerFlag(0);//1：是  0：否
			if(projectExtraQuickReward.getProjectId()!=null&&projectExtraQuickReward.getActivitySign() == TypeEnum.PROJECT_ACTIVITY_CATALYZER
					.getType()){
				directBiz.setCatalyzerFlag(1);//1：是  0：否
				directBiz.setCatalyzerExtraAmount(projectExtraQuickReward.getExtraAmount());
				directBiz.setCatalyzerHour(projectExtraQuickReward.getExtraInformationTwo());
			}
			
			//项目加息信息
			ProjectExtra projectExtraAddRate =projectExtraManager.getAddRateProject(id);
			directBiz.setAddRateFlag(0);//1：是  0：否
			if(projectExtraAddRate!=null&&projectExtraAddRate.getProjectId()!=null&&
					projectExtraAddRate.getExtraType() == TypeEnum.PROJECT_EXTRA_PROJECT_ADD_RATE.getType()){
				directBiz.setAddRateFlag(1);//1：是  0：否
				directBiz.setAddRate(projectExtraAddRate.getExtraAmount());
			}
			
			if(project.getOpenPlatformKey()!=null){
				SysDict dict = sysDictManager.findByGroupNameAndKey("channel_key", project.getOpenPlatformKey());
				if(dict!=null){
					directBiz.setChannelBusiness(dict.getValue());
				}
			}
			return directBiz;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 
	 * @desc 获取第三方账户余额
	 * @param memberId
	 * @return
	 * @author chaisen
	 * @throws ManagerException 
	 * @time 2016年1月7日 下午5:51:24
	 *
	 */
	@Override
	public BigDecimal getThirdAccountMoney(Long memberId) throws ManagerException {
		BalanceQuery balanceQuery = new BalanceQuery();
		balanceQuery.setBalanceType(2);
		balanceQuery.setSourceId(memberId);
		BigDecimal availableBalance =BigDecimal.ZERO;
		//Balance result = balanceMapper.selectByQuery(balanceQuery);
		Balance balance = balanceManager.synchronizedBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
		if (balance != null) {
			availableBalance = balance.getAvailableBalance();
		}
		return availableBalance;
	}

	@Override
	public ProjectInterestBiz getFlagInfo(Long id) throws ManagerException {
		ProjectInterestBiz project=new ProjectInterestBiz();
	try {
		ProjectInterestBiz projectBiz=debtInterestMapper.selectInterestInfoByPrimaryKey(id);
		if(projectBiz!=null){
			project.setInterestId(projectBiz.getInterestId());
			project.setProjectName(projectBiz.getProjectName());
			Member member=memberManager.selectByPrimaryKey(projectBiz.getBorrowerId());
			if(member!=null){
				project.setMobile(member.getMobile());
				project.setBorrowerName(member.getTrueName());
			}
			UnderwriteLog underwriteLog=underWriteLogManager.getUnderwriteLogByInterestId(id);
			if(underwriteLog!=null){
				project.setRepayTime(underwriteLog.getCreateTime());
			}
			ThirdCompany thirdCompany=thirdCompanyManager.getCompanyByMemberId(underwriteLog.getUnderwriteMemberId());
			if(thirdCompany!=null){
				project.setThirdPayName(thirdCompany.getCompanyName());
			}
		}
	} catch (ManagerException e) {
		throw new ManagerException(e);
	}
	return project;
}

	/**
	 * 
	 * @desc 获取逾期还款记录
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月7日 下午8:17:09
	 *
	 */
	@Override
	public ProjectInterestBiz getOverdueInfo(Long id) throws ManagerException {
		ProjectInterestBiz projectBiz = new ProjectInterestBiz();
		try {
			OverdueLog overdueLog = overdueLogManager.getOverdueLogByInterestId(id);
			if (overdueLog != null) {
				BeanCopyUtil.copy(overdueLog, projectBiz);
			}
			UnderwriteLog underwriteLog=underWriteLogManager.getUnderwriteLogByInterestId(id);
			if(underwriteLog!=null){
				projectBiz.setUnderwriteId(underwriteLog.getId());
				projectBiz.setRepayTime(underwriteLog.getCreateTime());
				ThirdCompany thirdCompany=thirdCompanyManager.getCompanyByMemberId(underwriteLog.getUnderwriteMemberId());
				if(thirdCompany!=null){
					projectBiz.setThirdPayName(thirdCompany.getCompanyName());
				}
			}
			ProjectInterestBiz biz=debtInterestMapper.selectInterestInfoByPrimaryKey(underwriteLog.getProjectInterestId());
			if(biz!=null){
				projectBiz.setMobile(biz.getMobile());
				projectBiz.setBorrowerName(biz.getBorrowerName());
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return projectBiz;
	}
	/**
	 * 
	 * @desc 分页查询管理费
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月11日 下午5:32:40
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> selectManageFeeForPagin(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int[] status = { StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus(), StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus(),
					StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus() };
			map.put("status", status);
			int totalCount = projectMapper.selectManageFeeForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<Project> selectForPagin = projectMapper
					.selectManageFeeForPagin(map);
			List<ProjectInterestBiz> projectBizs =  getManageFeeInfo(selectForPagin);
			pageRequest.setData(projectBizs);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 管理费详细信息
	 * @Description:TODO
	 * @param projectList
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月25日 下午2:59:03
	 */
	private List<ProjectInterestBiz> getManageFeeInfo(List<Project> projectList) throws ManagerException {
		List<ProjectInterestBiz> bizs = Lists.newArrayList();
		if(Collections3.isNotEmpty(projectList)){
			for(Project project:projectList){
				ProjectInterestBiz projectBiz = new ProjectInterestBiz();
				BeanCopyUtil.copy(project, projectBiz);
				projectBiz.setProjectName(project.getName());
				projectBiz.setSecurityType(project.getSecurityType());
				projectBiz.setCurrentDeadline(project.getSaleEndTime());
				projectBiz.setProjectStatus(project.getStatus());
				projectBiz.setPayableInterest(project.getAnnualizedRate());
				projectBiz.setBreachAmount(project.getManageFeeRate());
				ProjectFee managementFee=managementFeeManager.getManageMentFeeByProjectId(project.getId());
			/*	if(managementFee!=null){
					projectBiz.setManagementFee(managementFee.getManagementFee());
					projectBiz.setManagermentState(managementFee.getManagermentState());
					projectBiz.setGatherTime(managementFee.getGatherTime());
				}*/
				bizs.add(projectBiz);
			}
		}
		return bizs;
	}
    /**
     * 
     * @desc 查询项目基本信息
     * @param id
     * @return
     * @throws ManagerException
     * @author chaisen
     * @time 2016年1月12日 上午10:15:40
     *
     */
	@Override
	public ProjectInterestBiz showProject(Long id) throws ManagerException {
		Project project = selectByPrimaryKey(id);
		ProjectInterestBiz projectBiz = new ProjectInterestBiz();
		if(project!=null){
			projectBiz.setOriginalProjectNumber(project.getOriginalProjectNumber());
			projectBiz.setTotalAmount(project.getTotalAmount());
			projectBiz.setAnnualizedRate(project.getAnnualizedRate());
			projectBiz.setOnlineTime(project.getOnlineTime());
			projectBiz.setSaleEndTime(project.getSaleEndTime());
			projectBiz.setProfitType(project.getProfitType());
			projectBiz.setStartDate(project.getStartDate());
			projectBiz.setEndDate(project.getEndDate());
			Member member=memberManager.selectByPrimaryKey(project.getBorrowerId());
			if(member!=null){
				projectBiz.setBorrowerName(member.getTrueName());
			}
		}
		return projectBiz;
	}

	@Override
	public int updateDirectProjectSelective(DirectProjectBiz directProjectBiz) throws ManagerException{
		Project project = new Project();
		try{
			BeanCopyUtil.copy(directProjectBiz, project);
			if(project.getBorrowerType()==TypeEnum.MEMBER_TYPE_PERSONAL.getType()){
				project.setEnterpriseId(0L);
			}
			return projectMapper.updateDirectProjectSelective(project);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public int updateDirectProjectSelectiveWithNull(DirectProjectBiz directProjectBiz) throws ManagerException{
		Project project = new Project();
		try{
			BeanCopyUtil.copy(directProjectBiz, project);
			if(project.getBorrowerType()==TypeEnum.MEMBER_TYPE_PERSONAL.getType()){
				project.setEnterpriseId(0L);
			}
			return projectMapper.updateDirectProjectSelectiveWithNull(project);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/***
	 *   计算项目本息数据
	 * @param project
	 * @param date  起息时间
	 * @return
	 */
	@Override
	public List<DebtInterest> calculateInterest(Project project,Date date) {
		List<DebtInterest>  debtInterests = Lists.newArrayList();
		//起息日
		Integer interestFrom = project.getInterestFrom();
		//借款周期
		Integer borrowPeriod = project.getBorrowPeriod();
		//借款周期类型（1-日；2-月；3-年）
		Integer borrowPeriodType = project.getBorrowPeriodType();
		//年化利率
		BigDecimal annualizedRate = project.getAnnualizedRate();
		//项目总投资额
		BigDecimal totalAmount = project.getTotalAmount();
		//开始计息日
		Date beginInterestDate = DateUtils.addDate(date , interestFrom);
		//单位利息
		BigDecimal unitInterest = FormulaUtil.getUnitInterest(DebtEnum.RETURN_TYPE_DAY.getCode(), totalAmount,annualizedRate);
		//按日计息，按月付息，到期还本
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())){
			//借款周期类型天
			if (borrowPeriodType == 1){
				Date endDate = DateUtils.addDate(beginInterestDate, borrowPeriod);
				endDate = DateUtils.addDate(endDate, -1);
				int months = DateUtils.getMonthsBetweenDates(beginInterestDate,endDate);
				return  computeInterestByDays(borrowPeriod, annualizedRate, totalAmount, beginInterestDate, unitInterest, months);
			}
			//借款周期类型月
			if (borrowPeriodType == 2){
				Date endDate = DateUtils.addMonth(beginInterestDate,borrowPeriod);
				int months = borrowPeriod;
				int days = DateUtils.getIntervalDays(beginInterestDate,endDate) ;
				return  computeInterestByDays(days, annualizedRate, totalAmount, beginInterestDate, unitInterest, months);
			}
			//借款周期类型年
			if (borrowPeriodType ==3){
				Date endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
				int months = borrowPeriod  *  12;
				int days  = DateUtils.getIntervalDays(beginInterestDate,endDate) ;
				return  computeInterestByDays(days, annualizedRate, totalAmount, beginInterestDate, unitInterest, months);
			}
		}
		//一次性还本付息
		if (DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())){
			return onceRePaymentPrincipalAndIntest(borrowPeriod, borrowPeriodType, annualizedRate, totalAmount, beginInterestDate, unitInterest);
		}
		//等本等息 (按月)   不支持 借款周期为天的
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())){
			return  repaymentByAvgPrincipalInterest(borrowPeriod, borrowPeriodType, annualizedRate, totalAmount, beginInterestDate, unitInterest);
		}
		
		// 等本等息按周还款
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())){
			return repaymentByAvgPrincipalInterestByWeek(borrowPeriod, borrowPeriodType, annualizedRate, totalAmount, beginInterestDate);
		}
		
		// 按日计息，按季付息，到期还本
		if (DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())){
			return computeInterestBySeasonDays(borrowPeriod, borrowPeriodType, annualizedRate, totalAmount, beginInterestDate);
		}
		
		return debtInterests;
	}
	
	/**
	 * 计算两个时间间隔的季度数
	 * 
	 * @param firstDate
	 *            小者
	 * @param lastDate
	 *            大者
	 * @return int 默认-1
	 */
	public static int getIntervalSeasons(Date firstDate, Date lastDate) {
		if (null == firstDate || null == lastDate) {
			return -1;
		}
		
		int months = DateUtils.getMonthsBetweenDates(firstDate, lastDate);
		double seasonTemp = new BigDecimal(months).divide(new BigDecimal(3),2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return (int)Math.ceil(seasonTemp);
	}
	
	/**
	 * 等本等息按月付款
	 * @param borrowPeriod
	 * @param borrowPeriodType
	 * @param annualizedRate
	 * @param totalAmount
	 * @param beginInterestDate
	 * @param unitInterest
	 * @return
	 */
	private List<DebtInterest>  repaymentByAvgPrincipalInterest(Integer borrowPeriod, Integer borrowPeriodType, BigDecimal annualizedRate,
																BigDecimal totalAmount, Date beginInterestDate, BigDecimal unitInterest) {
		List<DebtInterest> debtInterests = Lists.newArrayList() ;
		//借款周期类型月
		int months = 0;
		if (borrowPeriodType == 2){
			months = borrowPeriod;
		}
		//借款周期类型年
		if (borrowPeriodType ==3){
			months = borrowPeriod  *  12;
		}
		Date endDateAddMonth = null;
		Date temp = beginInterestDate;
		//计算利息
		BigDecimal payableInterest = FormulaUtil.calculateInterestByAvg(totalAmount, annualizedRate);
		//计算本金
		BigDecimal principal = FormulaUtil.calculatePrincipalByAvg(totalAmount, months);
		BigDecimal tempTotal = BigDecimal.ZERO;
		for (int i =1;i <= months;i++){
			DebtInterest interest = new DebtInterest();
			endDateAddMonth = DateUtils.addMonth(temp, 1);
			interest.setStartDate(temp);
			endDateAddMonth = DateUtils.addDate(endDateAddMonth, -1 );
			temp = DateUtils.addDate(endDateAddMonth,1);
			interest.setEndDate(endDateAddMonth);
			//interest.setPayTime(endDateAddMonth);
			interest.setDelFlag(Constant.ENABLE);
			interest.setStatus(0);
			interest.setUnitInterest(unitInterest);
			interest.setPayableInterest(payableInterest);
			if (i==months){
				principal = totalAmount.subtract(tempTotal);
				interest.setPayablePrincipal(principal);
			}else {
				tempTotal = tempTotal.add(principal);
				interest.setPayablePrincipal(principal);
			}
			interest.setPeriods(i+"/"+months);
			interest.setUnitPrincipal(interest.getPayablePrincipal());
			debtInterests.add(interest);
		}
		return debtInterests;
	}

	/**
	 * 一次性还本还息
	 * @param borrowPeriod
	 * @param borrowPeriodType
	 * @param annualizedRate
	 * @param totalAmount
	 * @param beginInterestDate
	 * @param unitInterest
	 * @return
	 */
	private List<DebtInterest> onceRePaymentPrincipalAndIntest(Integer borrowPeriod, Integer borrowPeriodType, BigDecimal annualizedRate,
															   BigDecimal totalAmount, Date beginInterestDate, BigDecimal unitInterest) {
		Date endDate = null;
		int months = 1;
		int days = 0;
		//借款周期类型天
		if (borrowPeriodType == 1){
			days = borrowPeriod;
		}
		//借款周期类型月
		if (borrowPeriodType == 2){
			endDate = DateUtils.addMonth(beginInterestDate, borrowPeriod);
			days = DateUtils.getIntervalDays(beginInterestDate,endDate) ;
		}
		//借款周期类型年
		if (borrowPeriodType ==3){
			endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
			days  = DateUtils.getIntervalDays(beginInterestDate,endDate);
		}
		return  computeInterestByDays(days, annualizedRate, totalAmount, beginInterestDate, unitInterest, months);
	}
	/**
	 * 按日计息，按月付息算法
	 * @param borrowPeriod
	 * @param annualizedRate
	 * @param totalAmount
	 * @param beginInterestDate
	 * @param unitInterest
	 * @param months
	 * @return
	 */
	private List<DebtInterest> computeInterestByDays(Integer borrowPeriod, BigDecimal annualizedRate, BigDecimal totalAmount, Date beginInterestDate, BigDecimal unitInterest, int months) {
		List<DebtInterest> debtInterests = Lists.newArrayList();
		Date temp = beginInterestDate;
		int totalDay = 0;
		for (int i =1;i <= months;i++){
			DebtInterest interest = new DebtInterest();
			Date endDateAddMonth = null;
			int intervalDays = 0;
			interest.setStartDate(temp);
			//最后一个周期
			if (i == months) {
				endDateAddMonth = DateUtils.addDate(temp, borrowPeriod - totalDay);
				endDateAddMonth = DateUtils.addDate(endDateAddMonth, -1 );
				intervalDays = DateUtils.getIntervalDays(temp, endDateAddMonth)+1;
				//最后一期还本金
				interest.setPayablePrincipal(totalAmount);
			}else {
				endDateAddMonth = DateUtils.addMonth(temp,1);
				endDateAddMonth = DateUtils.addDate(endDateAddMonth, -1 );
				intervalDays = DateUtils.getIntervalDays(temp, endDateAddMonth)+1;
				totalDay  = totalDay + intervalDays;
				temp = DateUtils.addDate(endDateAddMonth,1);
				interest.setPayablePrincipal(BigDecimal.ZERO);
			}
			interest.setEndDate(endDateAddMonth);
//			interest.setPayTime(endDateAddMonth);
			interest.setDelFlag(Constant.ENABLE);
			interest.setStatus(0);
			interest.setUnitPrincipal(interest.getPayablePrincipal());
			interest.setUnitInterest(unitInterest);
			interest.setPeriods(i+"/"+months);
			//计算利息
			BigDecimal payableInterest = FormulaUtil.calculateInterest(totalAmount, annualizedRate, intervalDays);
			interest.setPayableInterest(payableInterest);
			debtInterests.add(interest);
		}
		return debtInterests;
	}
	
	/**
	 * 
	 * @Description 等本等息按周还款
	 * @param borrowPeriod
	 * @param borrowPeriodType
	 * @param annualizedRate
	 * @param totalAmount
	 * @param beginInterestDate
	 * @return
	 * @author luwenshan
	 * @time 2016年10月25日 下午3:06:59
	 */
	private List<DebtInterest> repaymentByAvgPrincipalInterestByWeek(Integer borrowPeriod, Integer borrowPeriodType, BigDecimal annualizedRate,
																      BigDecimal totalAmount, Date beginInterestDate) {
		List<DebtInterest> debtInterests = Lists.newArrayList() ;
		if (TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType() != borrowPeriodType) {
			return debtInterests;
		}
		//借款周期类型只能为周
		int weeks = borrowPeriod;
		// 应付利息
		BigDecimal payableInterest = FormulaUtil.calculateInterestByAvgWeek(totalAmount, annualizedRate, 7);
		// 应付本金
		BigDecimal payablePrincipal = FormulaUtil.calculatePrincipalByAvgWeek(totalAmount, weeks);
		// 生成项目本息
		Date endDateAddWeeks = null;
		BigDecimal tempTotalAmount = BigDecimal.ZERO;
		for (int i=1; i<= weeks; i++) {
			DebtInterest debtInterest = new DebtInterest();
			endDateAddWeeks = DateUtils.addDaysByDate(beginInterestDate, 6);
			debtInterest.setStartDate(beginInterestDate);
			beginInterestDate = DateUtils.addDaysByDate(beginInterestDate, 7);
			
			debtInterest.setEndDate(endDateAddWeeks);
			debtInterest.setDelFlag(Constant.ENABLE);
			debtInterest.setStatus(0);
			debtInterest.setUnitInterest(payableInterest);
			// 如果最后一期
			if (i == weeks) {
				payablePrincipal = totalAmount.subtract(tempTotalAmount);
			} else {
				tempTotalAmount = tempTotalAmount.add(payablePrincipal);
			}
			debtInterest.setPayableInterest(payableInterest);
			debtInterest.setPayablePrincipal(payablePrincipal);
			debtInterest.setPeriods(i + "/" + weeks);
			debtInterest.setUnitPrincipal(debtInterest.getPayablePrincipal());
			
			debtInterests.add(debtInterest);
		}
		
		return debtInterests;
	}
	
	/**
	 * 
	 * @Description 按日计息，按季付息，到期还本
	 * @param borrowPeriodType
	 * @param borrowPeriod
	 * @param annualizedRate
	 * @param totalAmount
	 * @param beginInterestDate
	 * @return
	 * @author luwenshan
	 * @time 2016年10月26日 下午2:42:23
	 */
	private List<DebtInterest> computeInterestBySeasonDays(Integer borrowPeriod, Integer borrowPeriodType, BigDecimal annualizedRate, BigDecimal totalAmount, Date beginInterestDate) {
		int seasonNums = 0;
		int periodDays = 0;
		// 借款周期类型天
		if (borrowPeriodType == 1){
			Date endDate = DateUtils.addDate(beginInterestDate, borrowPeriod - 1);
			periodDays = borrowPeriod;
			seasonNums = getIntervalSeasons(beginInterestDate, endDate);
		}
		// 借款周期类型月
		if (borrowPeriodType == 2){
			Date endDate = DateUtils.addMonth(beginInterestDate, borrowPeriod);
			seasonNums = getIntervalSeasons(beginInterestDate, endDate);
			periodDays = DateUtils.getIntervalDays(beginInterestDate, endDate) ;
		}
		// 借款周期类型年
		if (borrowPeriodType == 3){
			seasonNums = borrowPeriod * 4;
			Date endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
			periodDays = DateUtils.getIntervalDays(beginInterestDate,endDate);
		}
		
		List<DebtInterest> debtInterests = Lists.newArrayList();
		Date temp = beginInterestDate;
		int totalCurrentPreDays = 0;
		for (int i =1;i <= seasonNums;i++){
			DebtInterest interest = new DebtInterest();
			Date endDateAddSeason = null; // 每周期的计息结束日期
			int intervalDays = 0; // 每周期的天数
			interest.setStartDate(temp);
			//最后一个周期
			if (i == seasonNums) {
				endDateAddSeason = DateUtils.addDate(temp, periodDays - totalCurrentPreDays - 1);
				intervalDays = periodDays - totalCurrentPreDays;
				//最后一期还本金
				interest.setPayablePrincipal(totalAmount);
			} else {
				endDateAddSeason = DateUtils.addMonth(temp, 3);
				endDateAddSeason = DateUtils.addDate(endDateAddSeason, -1 );
				intervalDays = DateUtils.getIntervalDays(temp, endDateAddSeason) + 1;
				totalCurrentPreDays = totalCurrentPreDays + intervalDays;
				temp = DateUtils.addDate(endDateAddSeason, 1);
 				interest.setPayablePrincipal(BigDecimal.ZERO);
			}
			//计算利息
			BigDecimal payableInterest = FormulaUtil.calculateInterestByAvgSeason(totalAmount, annualizedRate, intervalDays);
			interest.setPayableInterest(payableInterest);
			interest.setEndDate(endDateAddSeason);
			interest.setDelFlag(Constant.ENABLE);
			interest.setStatus(0);
			interest.setUnitPrincipal(interest.getPayablePrincipal());
			interest.setUnitInterest(payableInterest);
			interest.setPeriods(i + "/" + seasonNums);
			debtInterests.add(interest);
		}
		
		return debtInterests;
	}
	
	/**
	 * 
	 * @desc 分页查询垫资管理
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月20日 下午2:18:33
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> findOverdueList(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<ProjectInterestBiz> projectBizs = Lists.newArrayList();
			int totalCount =projectMapper.selectOverdueForPaginTotalCount(map);
			if(totalCount>0){
				List<ProjectInterestBiz> selectForPagin = projectMapper.selectOverdueForPagin(map);
				projectBizs = findOverdueRecordList(selectForPagin);
			}
			//projectMapper.selectOverdueForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			pageRequest.setData(projectBizs);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 逾期管理
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年5月25日 上午11:12:00
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> findNormalOverdueList(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<ProjectInterestBiz> projectBizs = Lists.newArrayList();
			int totalCount =projectMapper.selectNormalOverdueForPaginTotalCount(map);
			if(totalCount>0){
				List<ProjectInterestBiz> selectForPagin = projectMapper
						.selectNormalOverdueForPagin(map);
				projectBizs =  findNormalOverdueList(selectForPagin);
			}
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			pageRequest.setData(projectBizs);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @Description:逾期管理列表信息组装
	 * @param projectList
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月27日 上午9:47:20
	 */
	private List<ProjectInterestBiz> findOverdueList(List<ProjectInterestBiz> projectList)  throws ManagerException {
		List<ProjectInterestBiz> bizs = Lists.newArrayList();
		if(Collections3.isNotEmpty(projectList)){
			for(ProjectInterestBiz project:projectList){
				ProjectInterestBiz biz=getOverdueAmountInfo(project.getProjectId());
				if(biz!=null){
					project.setOverdueDays(biz.getOverdueDays());
					project.setOverdueAmount(biz.getOverdueAmount());
					project.setOverduePrincipal(biz.getOverduePrincipal());
					project.setOverdueInterest(biz.getOverdueInterest());
					project.setLateFees(biz.getLateFees());
					project.setTotalPayAmount(biz.getTotalPayAmount());
					project.setOverdueStartDate(biz.getOverdueStartDate());
					project.setManagementFee(FormulaUtil.getManagerAmount(project.getTotalAmount(),project.getManageFeeRate()));
				}
				//应还款日期
				OverdueLog overdueLog=overdueLogMapper.selectOverdueDateByProjectId(project.getProjectId());
				if(overdueLog!=null){
					if (overdueLog.getStartDate() != null) {
						project.setRefundTime(DateUtils.addDate(overdueLog.getStartDate(), -1));
					}
				}
				//统计逾期未还记录
				int i=overdueLogManager.countOverdueRecordByProjectId(project.getProjectId());
				if(i>0){
					project.setAllStatus(StatusEnum.OVERDUE_STATUS_YES.getStatus());
				}else{
					project.setAllStatus(StatusEnum.OVERDUE_STATUS_NO.getStatus());
				}
				//逾期还款记录数
				int j=overdueRepayLogMapper.countOverdueRepayLogByProjectId(project.getProjectId());
				if(j>0){
					//project.setOverdueRecord(1);
					project.setOverdueStatus(StatusEnum.OVERDUE_STATUS_YES.getStatus());
				}
				int k=overdueLogMapper.countUnderOverdueByProjectId(project.getProjectId());
				int l=overdueLogMapper.getOverdueLogByProjectIdAndStatus(project.getProjectId());
				int undercount=overdueRepayLogMapper.countOverdueRepayLogByProjectIdAndtype(project.getProjectId(),TypeEnum.OVERDUE_SETTLEMENT_TYPE_UNDERWRITE.getType());
				List<OverdueRepayLog> listcommon=overdueRepayLogMapper.getCommonOverdueRepayLogRecord(project.getProjectId());
				//垫资和逾期
				if(k>0&&l>0){
					project.setOverdueUnder(1);
				}
				//垫资
				if(k>0&&l==0){
					project.setOverdueUnder(1);
				}
				//逾期
				if(k==0&&l>0){
					project.setOverdueUnder(2);
				}
				if(undercount>0){
					project.setOverdueRecord(1);
				}
				if(Collections3.isNotEmpty(listcommon)){
					project.setCommonRecord(1);
				}
				bizs.add(project);
			}
		}
		return bizs;
	}
	/**
	 * 
	 * @Description:逾期管理列表信息组装
	 * @param projectList
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月27日 上午9:47:20
	 */
	private List<ProjectInterestBiz> findOverdueRecordList(List<ProjectInterestBiz> projectList)  throws ManagerException {
		List<ProjectInterestBiz> bizs = Lists.newArrayList();
		if(Collections3.isNotEmpty(projectList)){
			for(ProjectInterestBiz project:projectList){
				ProjectInterestBiz biz=getOverdueAmountInfo(project.getProjectId());
				if(biz!=null){
					project.setOverdueDays(biz.getOverdueDays());
					project.setOverdueAmount(biz.getOverdueAmount());
					project.setOverduePrincipal(biz.getOverduePrincipal());
					project.setOverdueInterest(biz.getOverdueInterest());
					project.setLateFees(biz.getLateFees());
					project.setTotalPayAmount(biz.getTotalPayAmount());
					project.setOverdueStartDate(biz.getOverdueStartDate());
					project.setManagementFee(FormulaUtil.getManagerAmount(project.getTotalAmount(),project.getManageFeeRate()));
				}
				//应还款日期
				OverdueLog overdueLog=overdueLogMapper.selectOverdueInfoByProjectId(project.getProjectId());
				if(overdueLog!=null){
					if (overdueLog.getStartDate() != null) {
						project.setRefundTime(DateUtils.addDate(overdueLog.getStartDate(), -1));
						project.setAllStatus(StatusEnum.OVERDUE_STATUS_YES.getStatus());
					} else {
						project.setAllStatus(StatusEnum.OVERDUE_STATUS_NO.getStatus());
					}
				} else {
					project.setAllStatus(StatusEnum.OVERDUE_STATUS_NO.getStatus());
				}
				
				//逾期还款记录数
				int j=overdueRepayLogMapper.countOverdueRepayLogByProjectId(project.getProjectId());
				if(j>0){
					//project.setOverdueRecord(1);
					project.setOverdueStatus(StatusEnum.OVERDUE_STATUS_YES.getStatus());
				}
				int k=overdueLogMapper.countUnderOverdueByProjectId(project.getProjectId());
				int l=overdueLogMapper.getOverdueLogByProjectIdAndStatus(project.getProjectId());
				int undercount=overdueRepayLogMapper.countOverdueRepayLogByProjectIdAndtype(project.getProjectId(),TypeEnum.OVERDUE_SETTLEMENT_TYPE_UNDERWRITE.getType());
				List<OverdueRepayLog> listcommon=overdueRepayLogMapper.getCommonOverdueRepayLogRecord(project.getProjectId());
				//垫资和逾期
				if(k>0&&l>0){
					project.setOverdueUnder(1);
				}
				//垫资
				if(k>0&&l==0){
					project.setOverdueUnder(1);
				}
				//逾期
				if(k==0&&l>0){
					project.setOverdueUnder(2);
				}
				if(undercount>0){
					project.setOverdueRecord(1);
				}
				if(Collections3.isNotEmpty(listcommon)){
					project.setCommonRecord(1);
				}
				bizs.add(project);
			}
		}
		return bizs;
	}
	/**
	 * 
	 * @Description:普通逾期详细信息
	 * @param projectList
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月20日 下午2:36:10
	 */
	private List<ProjectInterestBiz> findNormalOverdueList(List<ProjectInterestBiz> projectList)  throws ManagerException {
		List<ProjectInterestBiz> bizs = Lists.newArrayList();
		if(Collections3.isEmpty(projectList)){
			return bizs;
		}
		for(ProjectInterestBiz project:projectList){
			Project pro=projectMapper.selectByPrimaryKey(project.getProjectId());
			if(pro!=null){
				if(project.getRepayStatus()==3){
					project.setOverdueDays(project.getOverdueDays());
					project.setOverdueFine(project.getOverdueFine());
				}else{ 
					//滞纳金
					project.setOverdueDays(DateUtils.daysOfTwo(project.getOverdueStartDate(), DateUtils.getCurrentDate())+1);
					project.setOverdueFine(FormulaUtil.getLateFeeAmount(getNormalOverdueAmountInfo(project.getProjectId(),project.getOverdueStartDate()), pro.getLateFeeRate(),project.getOverdueDays()));
				}
				int i=collectionProcessMapper.countCollectionByRepayId(project.getOverdueRepayId());
				if(i>0){
					project.setCollect(1);
				}else{
					project.setCollect(0);
				}
			}
			bizs.add(project);
			}
		return bizs;
	}
	
	public BigDecimal getNormalOverdueAmountInfo(Long projectId,Date date) throws ManagerException{
		BigDecimal nopay = BigDecimal.ZERO;
		//逾期本金
		BigDecimal overduePrincipal = BigDecimal.ZERO;
		//待支付本金
		BigDecimal waitpayPrincipal = BigDecimal.ZERO;
		//待支付利息
		BigDecimal waitpayInterest = BigDecimal.ZERO;
		//逾期利息
		BigDecimal overdueInterest = BigDecimal.ZERO;
		//逾期未还本金
		BigDecimal overdueNopayAmount = BigDecimal.ZERO;
		//逾期未还利息
		BigDecimal overdueNopayInterest = BigDecimal.ZERO;
		//逾期金额  逾期本金+逾期利息
		
		OverdueLog overAmount=overdueLogMapper.selectNormalOverduePayAmountByProjectId(projectId,date);
		if(overAmount!=null){
			overduePrincipal=overAmount.getOverduePrincipal();
			overdueInterest=overAmount.getOverdueInterest();
		}
		List<DebtInterest> feeInterests=debtInterestManager.findWaitPayInterestsByProjectId(projectId);
		if(Collections3.isNotEmpty(feeInterests)){
			for(DebtInterest biz : feeInterests){
				BigDecimal tempInterest = BigDecimal.ZERO;
				BigDecimal tempPrincipal = BigDecimal.ZERO;
				if(biz.getPayablePrincipal()==null){
					tempPrincipal=BigDecimal.ZERO;
				}else{
					tempPrincipal=biz.getPayablePrincipal();
				}
				if(biz.getPayableInterest()==null){
					tempInterest=BigDecimal.ZERO;
				}else{
					tempInterest=biz.getPayableInterest();
				}
				int i=overdueLogMapper.getCountOverdueRecord(biz.getId());
				if(i>0){
					continue;
				}
				waitpayPrincipal=waitpayPrincipal.add(tempPrincipal);
				waitpayInterest=waitpayInterest.add(tempInterest);
			}
		}
		//逾期未还本金
		overdueNopayAmount=overduePrincipal.add(waitpayPrincipal);
		overdueNopayInterest=overdueInterest.add(waitpayInterest);
		nopay=overdueNopayAmount.add(overdueNopayInterest);
		return nopay;
	}
	/**
	 * 
	 * @Description:获取 逾期金额、滞纳金、合计应还款
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月19日 下午5:03:26
	 */
	public ProjectInterestBiz getOverdueAmountInfo(Long projectId) throws ManagerException{
		ProjectInterestBiz projectInterestBiz=new ProjectInterestBiz();
		Map<String, Object> map = Maps.newHashMap();
		map.put("projectId", projectId);
		map.put("status", StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
		//逾期本金
		BigDecimal overduePrincipal = BigDecimal.ZERO;
		//待支付本金
		BigDecimal waitpayPrincipal = BigDecimal.ZERO;
		//待支付利息
		BigDecimal waitpayInterest = BigDecimal.ZERO;
		//逾期利息
		BigDecimal overdueInterest = BigDecimal.ZERO;
		//逾期未还本金
		BigDecimal overdueNopayAmount = BigDecimal.ZERO;
		//逾期未还利息
		BigDecimal overdueNopayInterest = BigDecimal.ZERO;
		//逾期金额  逾期本金+逾期利息
		BigDecimal overdueAmount = BigDecimal.ZERO;
		//滞纳金   逾期未还本金*罚息率*逾期天数
		BigDecimal lateFee = BigDecimal.ZERO;
		// 合计应还款 逾期金额+滞纳金
		BigDecimal totalPayAmount = BigDecimal.ZERO;
		
		OverdueLog overAmount=overdueLogMapper.selectOverdueAmountByProjectId(projectId);
		if(overAmount!=null){
			overduePrincipal=overAmount.getOverduePrincipal();
			overdueInterest=overAmount.getOverdueInterest();
		}
		List<DebtInterest> feeInterests=debtInterestManager.findWaitPayInterestsByProjectId(projectId);
		if(Collections3.isNotEmpty(feeInterests)){
			for(DebtInterest biz : feeInterests){
				BigDecimal tempInterest = BigDecimal.ZERO;
				BigDecimal tempPrincipal = BigDecimal.ZERO;
				if(biz.getPayablePrincipal()==null){
					tempPrincipal=BigDecimal.ZERO;
				}else{
					tempPrincipal=biz.getPayablePrincipal();
				}
				if(biz.getPayableInterest()==null){
					tempInterest=BigDecimal.ZERO;
				}else{
					tempInterest=biz.getPayableInterest();
				}
				int i=overdueLogMapper.getCountOverdueRecord(biz.getId());
				if(i>0){
					continue;
				}
				waitpayPrincipal=waitpayPrincipal.add(tempPrincipal);
				waitpayInterest=waitpayInterest.add(tempInterest);
			}
		}
		//逾期未还本金
		overdueNopayAmount=overduePrincipal.add(waitpayPrincipal);
		overdueNopayInterest=overdueInterest.add(waitpayInterest);
		overdueAmount=overduePrincipal.add(overdueInterest);
		
		//逾期天数
		int overdueDays=0;
		OverdueLog overdue = overdueLogMapper.selectOverdueDateByProjectId(projectId);
		if(overdue!=null){
			overdueDays=DateUtils.daysBetween(overdue.getStartDate(), DateUtils.getCurrentDate()) + 1;
			projectInterestBiz.setOverdueStartDate(overdue.getStartDate());
		} 
		Project project=selectByPrimaryKey(projectId);
		if(project!=null){
			lateFee=FormulaUtil.getLateFeeAmount(overdueNopayAmount, project.getOverdueFeeRate(),overdueDays);
			projectInterestBiz.setOverdueFeeRate(project.getOverdueFeeRate());
		}
		totalPayAmount=overdueAmount.add(lateFee);
		projectInterestBiz.setOverdueAmount(overdueAmount);
		projectInterestBiz.setOverdueInterest(waitpayInterest.add(overdueInterest));
		projectInterestBiz.setLateFees(lateFee);
		projectInterestBiz.setTotalPayAmount(totalPayAmount);
		projectInterestBiz.setOverdueDays(overdueDays);
		projectInterestBiz.setOverduePrincipal(overduePrincipal);
		projectInterestBiz.setOverdueInterest(overdueInterest);
		projectInterestBiz.setUnreturnPrincipal(overdueNopayAmount);
		projectInterestBiz.setNoPayInterest(overdueNopayInterest);
		projectInterestBiz.setNoPayPrincipalInterest(overdueNopayAmount.add(overdueNopayInterest));
		return projectInterestBiz;
	}
	/**
	 * 
	 * @Description:获取逾期未还本金
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月19日 下午4:38:17
	 */
	public BigDecimal getOverdueNoreturnPrincipal(Long projectId) throws ManagerException{
		Map<String, Object> map = Maps.newHashMap();
		map.put("projectId", projectId);
		map.put("status", StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
		//逾期本金
		BigDecimal overduePrincipal = BigDecimal.ZERO;
		//待支付本金
		BigDecimal waitpayPrincipal = BigDecimal.ZERO;
		//逾期未还本金
		BigDecimal overdueNopayAmount = BigDecimal.ZERO;
		DebtInterest debtInterest = debtInterestManager.selectOverdueAmountByProjectId(map);
		if(debtInterest!=null){
			overduePrincipal=debtInterest.getPayablePrincipal();
		}
		Map<String, Object> feemap = Maps.newHashMap();
		feemap.put("projectId", projectId);
		feemap.put("status", StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus());
		DebtInterest feeInterest=debtInterestManager.selectWaitPayAmountByProjectId(feemap);
		if(feeInterest!=null){
			waitpayPrincipal=feeInterest.getPayablePrincipal();
		}
		overdueNopayAmount=overduePrincipal.add(waitpayPrincipal);
		return overdueNopayAmount;
	}
	/**
	 * 
	 * @Description:获取滞纳金
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月23日 上午10:09:13
	 */
	public BigDecimal getLateFee(Long projectId) throws ManagerException{
		//滞纳金
		BigDecimal lateFee = BigDecimal.ZERO;
		//罚息率
		BigDecimal overdueFeeRate = BigDecimal.ZERO;
		//逾期天数
		int overdueDays=0;
		Map<String, Object> overdueMap = Maps.newHashMap();
		overdueMap.put("projectId", projectId);
		overdueMap.put("status", StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
		OverdueLog overdue = overdueLogManager.selectOverdueByProjectId(overdueMap);
		if(overdue!=null){
			overdueDays=DateUtils.daysBetween(overdue.getStartDate(), DateUtils.getCurrentDate()) + 1;
		}
		Project project=selectByPrimaryKey(projectId);
		if(project!=null){
			overdueFeeRate=project.getOverdueFeeRate();
		}
		lateFee=FormulaUtil.getLateFeeAmount(getOverdueNoreturnPrincipal(projectId), overdueFeeRate,overdueDays);
		return lateFee;
	}
	
	
	public ProjectInterestBiz getUnderOverdueAmountInfo(Long projectId) throws ManagerException{
		ProjectInterestBiz projectInterestBiz=new ProjectInterestBiz();
		Map<String, Object> map = Maps.newHashMap();
		map.put("projectId", projectId);
		map.put("status", StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
		//逾期本金
		BigDecimal overduePrincipal = BigDecimal.ZERO;
		//待支付本金
		BigDecimal waitpayPrincipal = BigDecimal.ZERO;
		//待支付利息
		BigDecimal waitpayInterest = BigDecimal.ZERO;
		//逾期利息
		BigDecimal overdueInterest = BigDecimal.ZERO;
		//逾期未还本金
		BigDecimal overdueNopayAmount = BigDecimal.ZERO;
		//逾期金额  逾期本金+逾期利息
		BigDecimal overdueAmount = BigDecimal.ZERO;
		//滞纳金   逾期未还本金*罚息率*逾期天数
		BigDecimal lateFee = BigDecimal.ZERO;
		// 合计应还款 逾期金额+滞纳金
		BigDecimal totalPayAmount = BigDecimal.ZERO;
		
		//DebtInterest debtInterest = debtInterestManager.selectOverdueAmountByProjectId(map);
		OverdueLog overAmount=overdueLogMapper.selectUnderOverdueAmountByProjectId(projectId);
		if(overAmount!=null){
			overduePrincipal=overAmount.getOverduePrincipal();
			overdueInterest=overAmount.getOverdueInterest();
		}
		Map<String, Object> feemap = Maps.newHashMap();
		feemap.put("projectId", projectId);
		feemap.put("status",StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus());
		
		DebtInterest feeInterest=debtInterestMapper.selectCommonOverdueByProjectId(projectId);
		if(feeInterest!=null){
			waitpayPrincipal=feeInterest.getPayablePrincipal();
			waitpayInterest=feeInterest.getPayableInterest();
		}
		overdueNopayAmount=overduePrincipal.add(waitpayPrincipal);
		overdueAmount=overduePrincipal.add(overdueInterest);
		
		//逾期天数
		int overdueDays=0;
		OverdueLog overdue = overdueLogMapper.selectOverdueDateByProjectId(projectId);
		if(overdue!=null){
			overdueDays=DateUtils.daysBetween(overdue.getStartDate(), DateUtils.getCurrentDate()) + 1;
		} 
		Project project=selectByPrimaryKey(projectId);
		if(project!=null){
			lateFee=FormulaUtil.getLateFeeAmount(overdueNopayAmount, project.getOverdueFeeRate(),overdueDays);
			projectInterestBiz.setOverdueFeeRate(project.getOverdueFeeRate());
		}
		totalPayAmount=overdueAmount.add(lateFee);
		projectInterestBiz.setOverdueAmount(overdueAmount);
		projectInterestBiz.setOverdueInterest(waitpayInterest.add(overdueInterest));
		projectInterestBiz.setLateFees(lateFee);
		projectInterestBiz.setTotalPayAmount(totalPayAmount);
		projectInterestBiz.setOverdueDays(overdueDays);
		projectInterestBiz.setOverduePrincipal(overduePrincipal);
		projectInterestBiz.setOverdueInterest(overdueInterest);
		projectInterestBiz.setUnreturnPrincipal(overdueNopayAmount);
		return projectInterestBiz;
	}
	/**
	 * 
	 * @desc 垫资记录是否存在
	 * @param interestId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月26日 上午10:57:53
	 *
	 */
	@Override
	public boolean checkIsExistUnderwriteLog(Long interestId) throws ManagerException {
		UnderwriteLog underwriteLog=underWriteLogManager.getUnderwriteLogByInterestId(interestId);
		if(underwriteLog!=null){
			return true;
		}else{
			return false;
		}
		
		
	}
	/**
	 * 
	 * @desc 还款中的项目个数
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月26日 下午3:07:04
	 *
	 */
	@Override
	public int selectPayingTotalCount(Map<String, Object> map) throws ManagerException {
		map.put("status", StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus());
		int total=projectMapper.selectPayingTotalCount(map);
		return total;
	}
	/**
	 * 
	 * @desc 借款列表
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月26日 下午7:20:07
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> getBorrowList(ProjectBorrowQuery query) throws ManagerException {
		try {
			List<ProjectInterestBiz> projectForFrontList = Lists.newArrayList();
			List<ProjectInterestBiz> projectBizs = Lists.newArrayList();
			long count = projectMapper.selectForPaginTotalCountBorrow(query);
			if (count > 0) {
				projectForFrontList = projectMapper.selectForPaginBorrow(query);
				projectBizs =  findBorrowList(projectForFrontList);
			}
			Page<ProjectInterestBiz> page = new Page<ProjectInterestBiz>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setData(projectBizs);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 还款中的借款
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年3月2日 上午9:33:56
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> getPayingBorrowList(ProjectBorrowQuery query) throws ManagerException {
		try {
			List<ProjectInterestBiz> projectForFrontList = Lists.newArrayList();
			List<ProjectInterestBiz> projectBizs = Lists.newArrayList();
			int count = projectMapper.selectForPaginCountPayingBorrow(query);
			if(count>0){
				projectForFrontList = projectMapper.selectForPaginPayingBorrow(query);
				projectBizs =  findPayingBorrowList(projectForFrontList);
			}
			Page<ProjectInterestBiz> page = new Page<ProjectInterestBiz>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setData(projectBizs);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @Description:还款中的借款详细信息
	 * @param projectForFrontList
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月2日 下午5:08:41
	 */
	private List<ProjectInterestBiz> findPayingBorrowList(List<ProjectInterestBiz> projectForFrontList) throws ManagerException {
		List<ProjectInterestBiz> bizs = Lists.newArrayList();
		if(Collections3.isNotEmpty(projectForFrontList)){
			for(ProjectInterestBiz project:projectForFrontList){
				Map<String, Object> map = Maps.newHashMap();
				map.put("projectId", project.getProjectId());
				//逾期记录
				int overduecount=overdueLogMapper.findOverdueByProjectIdTotalCount(map);
				//还款中的借款
				//代还本息
				//project.setWaitTotalPayAmount(waitPayablePrincipalAndPayableInterest(project.getProjectId()));
				TransactionInterest transactionInterest=transactionInterestMapper.selectUnreturnPrincipalAndInterestByProjectId(project.getProjectId());
				BigDecimal principal=BigDecimal.ZERO;
				BigDecimal interest=BigDecimal.ZERO;
				if(transactionInterest!=null){
					if(transactionInterest.getPayablePrincipal()!=null){
						principal=transactionInterest.getPayablePrincipal();
					}
					if(transactionInterest.getPayableInterest()!=null){
						interest=transactionInterest.getPayableInterest();
					}
				}
				project.setWaitTotalPayAmount(principal.add(interest));
				
				DebtInterest maxInterset=debtInterestMapper.selectMaxTimeByProjectId(project.getProjectId());
				if(overduecount>0){
					OverdueLog overdue = overdueLogMapper.selectOverdueDateByProjectId(project.getProjectId());
					if(overdue!=null){
						//还款日期
						project.setRefundTime(overdue.getStartDate());
					}else{
						if(maxInterset!=null){
							project.setRefundTime(maxInterset.getEndDate());
						}
					}
				}else{
					if(maxInterset!=null){
						project.setRefundTime(maxInterset.getEndDate());
					}
					
				}
				int undercount=overdueRepayLogMapper.countOverdueRepayLogByProjectIdAndtype(project.getProjectId(),TypeEnum.OVERDUE_SETTLEMENT_TYPE_UNDERWRITE.getType());
				List<OverdueRepayLog> listcommon=overdueRepayLogMapper.getCommonOverdueRepayLogRecord(project.getProjectId());
				//有垫资还款记录
				if(undercount>0){
					project.setOverdueUnder(1);
				}
				//逾期结算
				if(Collections3.isNotEmpty(listcommon)){
					project.setOverdueRecord(1);
				}
				bizs.add(project);
				}
			}
		return bizs;
	}
	/**
	 * 
	 * @Description:借款列表详细信息
	 * @param projectForFrontList
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月19日 下午1:38:36
	 */
	private List<ProjectInterestBiz> findBorrowList(List<ProjectInterestBiz> projectForFrontList) throws ManagerException {
		List<ProjectInterestBiz> bizs = Lists.newArrayList();
		if(Collections3.isNotEmpty(projectForFrontList)){
			for(ProjectInterestBiz project:projectForFrontList){
				Map<String, Object> map = Maps.newHashMap();
				map.put("projectId", project.getProjectId());
				map.put("status", StatusEnum.OVERDUE_LOG_HAD_PAY.getStatus());
				//逾期记录
				int overduecount=overdueLogMapper.findOverdueByProjectIdTotalCount(map);
				//还款中的借款
				/*if(StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()==project.getProjectStatus()){
					//代还本息
					project.setWaitTotalPayAmount(waitPayablePrincipalAndPayableInterest(project.getProjectId()));
					project.setRefundTime(project.getEndDate());
					if(overduecount>0){
						Map<String, Object> overdueMap = Maps.newHashMap();
						overdueMap.put("projectId",  project.getProjectId());
						overdueMap.put("status",StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
						OverdueLog overdue = overdueLogManager.selectOverdueByProjectId(overdueMap);
						if(overdue!=null){
							//还款日期
							project.setRefundTime(overdue.getEndDate());
						}
					}
				}*/
				//已还款的借款
				//if(StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()==project.getProjectStatus()){
					//本息总额
					project.setTotalPayAmount(hadPayTotalAmount(project.getProjectId()));
					//还清日期
					DebtInterest debtInterest=debtInterestManager.selectOverdueDyasByProjectId(project.getProjectId());
					if(debtInterest!=null){
						if(overduecount>0){
							OverdueLog overdueLog=overdueLogMapper.selectOverdueRecordByProjectId(project.getProjectId());
							if(overdueLog!=null){
								if(DateUtils.compareDate(overdueLog.getEndDate(), debtInterest.getEndDate())>1){
									project.setRefundTime(debtInterest.getEndDate());
								}else{
									project.setRefundTime(overdueLog.getEndDate());
								}
							}
						}else{
							project.setRefundTime(debtInterest.getEndDate());
						}
						
					}
				//}
				int overcount=overdueRepayLogMapper.countOverdueRepayLogByProjectId(project.getProjectId());
				//有逾期还款记录
				if(overcount>0){
					project.setOverdueRecord(1);
				}
				
				int undercount=overdueRepayLogMapper.countOverdueRepayLogByProjectIdAndtype(project.getProjectId(),TypeEnum.OVERDUE_SETTLEMENT_TYPE_UNDERWRITE.getType());
				List<OverdueRepayLog> listcommon=overdueRepayLogMapper.getCommonOverdueRepayLogRecord(project.getProjectId());
				//有垫资还款记录
				if(undercount>0){
					project.setOverdueUnder(1);
				}
				//逾期结算
				if(Collections3.isNotEmpty(listcommon)){
					project.setCommonRecord(1);
				}
				//project.setRefundTime(getRefundTime(overcount,project.getProjectId(),project.getProjectStatus()));
				// 项目投资进度
				//project.setProgress(getProjectNumberProgress(project.getTotalAmount(), project.getProjectId()));
				bizs.add(project);
				}
			}
		return bizs;
	}
	/**
	 * 
	 * @Description:代还本息总计
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年2月25日 下午4:05:30
	 */
	private BigDecimal waitPayablePrincipalAndPayableInterest(Long projectId) throws ManagerException{
				//待还本息总计
				BigDecimal totalAmount = BigDecimal.ZERO;
				//逾期本金
				BigDecimal overduePrincipal = BigDecimal.ZERO;
				//逾期利息
				BigDecimal overdueInterest = BigDecimal.ZERO;
				//逾期本金加利息
				BigDecimal overdueAmount = BigDecimal.ZERO;
				
				//待支付本金
				BigDecimal waitPrincipal = BigDecimal.ZERO;
				//待支付利息
				BigDecimal waitInterest = BigDecimal.ZERO;
				//待支付本金加利息
				BigDecimal waitAmount = BigDecimal.ZERO;
				
				OverdueLog OverdueLog= overdueLogMapper.selectOverdueLogPrincipalByProjectId(projectId);
				if(OverdueLog!=null){
					if(OverdueLog.getOverduePrincipal()!=null){
						overduePrincipal=OverdueLog.getOverduePrincipal();
					}
					if(OverdueLog.getOverdueInterest()!=null){
						overdueInterest=OverdueLog.getOverdueInterest();
					}
					overdueAmount=overduePrincipal.add(overdueInterest);
				}
				
				Map<String, Object> waitMap = Maps.newHashMap();
				waitMap.put("projectId", projectId);
				waitMap.put("status", StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus());
				
				DebtInterest waitDebtInterest=debtInterestManager.selectWaitPayAmountByProjectId(waitMap);
				if(waitDebtInterest!=null){
					waitPrincipal=waitDebtInterest.getPayablePrincipal();
					waitInterest=waitDebtInterest.getPayableInterest();
					waitAmount=waitPrincipal.add(waitInterest);
				}
				totalAmount=overdueAmount.add(waitAmount);
				return totalAmount;
	}
	/**
	 * 
	 * @Description:本息总额
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月25日 下午4:28:28
	 */
	private BigDecimal hadPayTotalAmount(Long projectId) throws ManagerException{
		//本息总额
		BigDecimal totalAmount = BigDecimal.ZERO;
		//本金
		BigDecimal principal = BigDecimal.ZERO;
		//利息
		BigDecimal interest = BigDecimal.ZERO;
		DebtInterest debtInterest=debtInterestMapper.selectRealPayAmountByProjectId(projectId);
		if(debtInterest!=null){
			principal=debtInterest.getRealPayPrincipal();
			interest=debtInterest.getRealPayInterest();
			BigDecimal overduefine=overdueRepayLogMapper.totalOverdueFine(projectId);
			if(overduefine==null){
				overduefine=BigDecimal.ZERO;
			}
			totalAmount=principal.add(interest).add(overduefine);
		}
		return totalAmount;
		
	}
	private Date getRefundTime(int overcount,Long projectId,int status) throws ManagerException{
		Date refundTime = null;
			if(overcount>0){
				Map<String, Object> overdueMap = Maps.newHashMap();
				overdueMap.put("projectId", projectId);
				overdueMap.put("status",StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
				OverdueLog overdue = overdueLogManager.selectOverdueByProjectId(overdueMap);
				if(overdue!=null){
					//还款日期
					refundTime=overdue.getEndDate();
				}
			}else{
				Date dtMax = null;
				Date dtMin = null;
				DebtInterest minInterset=debtInterestMapper.selectMinTimeByProjectId(projectId);
				int min=0;
				if(minInterset!=null){
					min=DateUtils.getTimeIntervalHours(minInterset.getEndDate(),DateUtils.getCurrentDate());
					dtMin=minInterset.getEndDate();
				}
				DebtInterest maxInterset=debtInterestMapper.selectMaxTimeByProjectId(projectId);
				int max=0;
				if(maxInterset!=null){
					max=DateUtils.getTimeIntervalHours(DateUtils.getCurrentDate(),maxInterset.getEndDate());
					dtMax=maxInterset.getEndDate();
				}
				if(min>max){
					refundTime=dtMax;
				}else{
					refundTime=dtMin;
				}
		}
		return refundTime;
	}
	@Override
	public int updateOnlineAEndTimeById(Timestamp onlineTime, Timestamp saleEndTime, Long id) throws ManagerException {
		try{
			int[] statuses = {StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus(),StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus()};
			return projectMapper.updateOnlineAEndTimeById(onlineTime, saleEndTime, id, statuses );
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public int updateSaleEndTimeById(Timestamp saleEndTime, Long id) throws ManagerException {
		try{
			int[] statuses = {StatusEnum.PROJECT_STATUS_INVESTING.getStatus(),StatusEnum.PROJECT_STATUS_STOP.getStatus()};
			return projectMapper.updateSaleEndTimeById(saleEndTime, id, statuses);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<OverdueRepayLog> getOverdueRepayLogRecordByProjectId(Long projectId) throws ManagerException {
		try{
			return overdueRepayLogMapper.getOverdueRepayLogRecordByProjectId(projectId);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 根据 projectId和repayDate 获取滞纳金和 总的还款金额
	 * @param projectId
	 * @param repayDate
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月28日 下午5:44:31
	 *
	 */
	@Override
	public ProjectInterestBiz getOverdueAmount(Long projectId, String repayDate) throws ManagerException {
		ProjectInterestBiz projectInterestBiz=new ProjectInterestBiz();
		//逾期本金
		BigDecimal overduePrincipal = BigDecimal.ZERO;
		//逾期利息
		BigDecimal overdueInterest = BigDecimal.ZERO;
		//未还本金
		BigDecimal noPayPrincipal = BigDecimal.ZERO;
		OverdueLog overdueLogAmount=overdueLogMapper.selectOverduePayAmountByProjectId(projectId,DateUtils.getDateFromString(DateUtils.addDays(repayDate, 1, DateUtils.DATE_FMT_3)));
		if(overdueLogAmount!=null){
			overduePrincipal=overdueLogAmount.getOverduePrincipal();
			overdueInterest=overdueLogAmount.getOverdueInterest();
		}
		
		Map<String, Object> waitMap = Maps.newHashMap();
		waitMap.put("projectId", projectId);
		waitMap.put("status", StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus());
		//待支付本金
		BigDecimal waitPrincipal = BigDecimal.ZERO;
		//待支付利息
		BigDecimal waitInterest = BigDecimal.ZERO;
		//逾期罚息
		BigDecimal rate = BigDecimal.ZERO;
		//DebtInterest feeInterest=debtInterestManager.selectWaitPayAmountByProjectId(waitMap);
		DebtInterest feeInterest=debtInterestMapper.selectCommonOverdueByProjectId(projectId);
		if(feeInterest!=null){
			waitPrincipal=feeInterest.getPayablePrincipal();
			waitInterest=feeInterest.getPayableInterest();
		}
		//逾期天数
		OverdueLog overdueLog=overdueLogMapper.selectOverdueDyasByProjectId(projectId);
		//项目信息
		Project Project=projectMapper.selectByPrimaryKey(projectId);
		if(Project!=null){
			rate=Project.getOverdueFeeRate();
		}
		//逾期天数
		int days=0;
		if(overdueLog!=null){
			days=DateUtils.daysBetween(overdueLog.getStartDate(), DateUtils.getDateFromString(repayDate)) + 1;
		}
		noPayPrincipal=overduePrincipal.add(waitPrincipal);
		projectInterestBiz.setPayablePrincipal(overduePrincipal);
		projectInterestBiz.setPayableInterest(overdueInterest);
		projectInterestBiz.setOverdueDays(days);
		projectInterestBiz.setUnreturnPrincipal(noPayPrincipal);
		//滞纳金
		BigDecimal latefee = BigDecimal.ZERO;
		latefee=FormulaUtil.getLateFeeAmount(noPayPrincipal, rate,days);
		projectInterestBiz.setLateFees(latefee);
		//共需还款  逾期本金+逾期利息+滞纳金+违约金
		projectInterestBiz.setPayableAmount(overduePrincipal.add(overdueInterest).add(latefee));
			
		return projectInterestBiz;
	}
	/**
	 * 
	 * @Description:根据 projectId 和repayDate 获取滞纳金
	 * @param projectId
	 * @param repayDate
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年2月23日 下午5:40:26
	 */
	@Override
	public BigDecimal getLateFeesByProjectIdandRepaydate(Long projectId, Date repayDate) throws ManagerException{
		//滞纳金
		BigDecimal lateFees = BigDecimal.ZERO;
		//逾期的本金
		BigDecimal overduePayablePrincipal = BigDecimal.ZERO;
		//待支付的本金
		BigDecimal waitPayablePrincipal = BigDecimal.ZERO;
		//逾期未还本金
		BigDecimal overdueNopayAmount = BigDecimal.ZERO;
	
		Map<String, Object> map = Maps.newHashMap();
		map.put("projectId", projectId);
		map.put("endDate", repayDate);
		map.put("overdueStatus", StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
		map.put("interestStatus", StatusEnum.DEBT_INTEREST_ALL_PAYED.getStatus());
		//逾期的
		ProjectInterestBiz projectInterestBiz=debtInterestManager.selectOverduePayAmountByProjectId(map);
		if(projectInterestBiz!=null){
			overduePayablePrincipal=projectInterestBiz.getPayablePrincipal();
		}
		
		Map<String, Object> waitMap = Maps.newHashMap();
		waitMap.put("projectId", projectId);
		waitMap.put("status", StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus());
		//待支付的
		DebtInterest waitInterest=debtInterestManager.selectWaitPayAmountByProjectId(waitMap);
		if(waitInterest!=null){
			waitPayablePrincipal=projectInterestBiz.getPayablePrincipal();
		}
		overdueNopayAmount=overduePayablePrincipal.add(waitPayablePrincipal);
		//逾期天数
		int days=0;
		OverdueLog overdueLog=overdueLogMapper.selectOverdueDyasByProjectId(projectId);
		if(overdueLog!=null){
			days=DateUtils.daysBetween(overdueLog.getStartDate(), repayDate) + 1;
		}
		Project Project=projectMapper.selectByPrimaryKey(projectId);
		if(Project!=null){
			lateFees=FormulaUtil.getLateFeeAmount(overdueNopayAmount, Project.getOverdueFeeRate(),days);
		}
		return lateFees; 
	}
	/**
	 * 
	 * @Description:获取逾期本金，逾期利息，滞纳金，逾期天数
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月2日 下午1:38:40
	 */
	public Map<String, Object> getOverdueInfoByProjectId(Long projectId)throws ManagerException{
		Map<String, Object> overdueMap = Maps.newHashMap();
		overdueMap.put("projectId",projectId);
		overdueMap.put("status", StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
		OverdueLog overdue = overdueLogManager.selectOverdueByProjectId(overdueMap);
		//逾期天数
		int overdueDays=0;
		if(overdue!=null){
			overdueDays=DateUtils.daysBetween(overdue.getStartDate(), DateUtils.getCurrentDate()) + 1;
		}
		//逾期本金
		BigDecimal overduePrincipal = BigDecimal.ZERO;
		//逾期利息
		BigDecimal overdueInterest = BigDecimal.ZERO;
		//逾期本金加利息
		BigDecimal overdueAmount = BigDecimal.ZERO;
		//未还本金总额
		BigDecimal principal = BigDecimal.ZERO; 
		//滞纳金
		BigDecimal lateFees = BigDecimal.ZERO;
		//逾期罚息率
		BigDecimal overdueFeeRate = BigDecimal.ZERO;
		//合计应还款
		BigDecimal totalAmount = BigDecimal.ZERO;
		DebtInterest interest = debtInterestManager.selectOverdueAmountByProjectId(overdueMap);
		if(interest!=null){
			overduePrincipal=interest.getPayablePrincipal();
			overdueInterest=interest.getPayableInterest();
			overdueAmount=overduePrincipal.add(overdueInterest);
		}
		Map<String, Object> Feemap = Maps.newHashMap();
		Feemap.put("projectId", projectId);
		Feemap.put("status", StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus());
		//待支付本金
		BigDecimal waitPrincipal = BigDecimal.ZERO;
		DebtInterest feeInterest=debtInterestManager.selectWaitPayAmountByProjectId(Feemap);
		if(feeInterest!=null){
			waitPrincipal=feeInterest.getPayablePrincipal();
		}
		principal=overduePrincipal.add(waitPrincipal);
		Project project=projectMapper.selectByPrimaryKey(projectId);
		if(project!=null){
			overdueFeeRate=project.getOverdueFeeRate();
		}
		lateFees=FormulaUtil.getLateFeeAmount(overduePrincipal.add(waitPrincipal),overdueFeeRate,overdueDays);
		totalAmount=overdueAmount.add(lateFees);
		 Map<String, Object> borrowMap = Maps.newHashMap();
		 borrowMap.put("overduePrincipal", overduePrincipal);
		 borrowMap.put("overdueInterest", overdueInterest);
		 borrowMap.put("lateFees", lateFees);
		 borrowMap.put("principal", principal);
		 borrowMap.put("totalAmount", totalAmount);
		return borrowMap;
	}
	/**
	 * 查询条件查询会员中心募集中的项目
	 * 
	 * @param
	 * @return
	 */
	@Override
	public Page<ProjectForMember> selectCollectProjectForMember(
			CollectingProjectQuery query) throws ManagerException {
		try {
			
			List<ProjectForMember> projectForMember = projectMapper
					.selectCollectProjectForMember(query);
			int count = projectMapper.selectCollectProjectForMemberCount(query);
			Page<ProjectForMember> page = new Page<ProjectForMember>();
			page.setData(projectForMember);
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setiTotalDisplayRecords(count);
		    page.setPageNo(query.getCurrentPage());
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 查询用户是否有投资募集中的项目
	 * 
	 * @param
	 * @return
	 */
	@Override
	@Deprecated 
	public int collectingProject(CollectingProjectQuery query)
			throws ManagerException {
		try {
			List<ProjectForMember> projectForMember =projectMapper.selectCollectProjectForMemberCounting(query);
			return projectForMember.size();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 查询用户是否有投资募集中的项目，对应的投资总额
	 * 
	 * @param
	 * @return
	 */
	@Override
	public BigDecimal selectCollectProjectForMemberInvestAmount(CollectingProjectQuery query)
			throws ManagerException {
		try {
			return projectMapper.selectCollectProjectForMemberInvestAmount(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 查询募集中的项目详情
	 * 
	 * @param
	 * @return
	 */
	@Override 
	public ProjectForMember selectCollectProjectDetail(Long transactionId,Long memberId) throws ManagerException {
		try {
			return projectMapper.selectCollectProjectDetail(transactionId,memberId);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 查询用户是否有投资募集中的项目，最后一笔投资
	 * 
	 * @param
	 * @return
	 */
	@Override
	public ProjectForMember selectCollectProjectDescTransactionTime(CollectingProjectQuery query)
			throws ManagerException {
		try {
			return projectMapper.selectCollectProjectDescTransactionTime(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public BigDecimal invertExpectEarnings(Project project, BigDecimal investAmount, Date date, BigDecimal extraAnnualizedRate) {
		BigDecimal result = BigDecimal.ZERO;
		extraAnnualizedRate =  extraAnnualizedRate ==null ? BigDecimal.ZERO  :extraAnnualizedRate;
		Integer interestFrom = project.getInterestFrom();
		//借款周期
		Integer borrowPeriod = project.getBorrowPeriod();
		//借款周期类型（1-日；2-月；3-年）
		Integer borrowPeriodType = project.getBorrowPeriodType();
		//年化利率
		BigDecimal annualizedRate = this.getProjectAnnulizedRate(project);
		// 加上收益劵
		annualizedRate = annualizedRate.add(extraAnnualizedRate);
		//开始计息日
		Date beginInterestDate = DateUtils.addDate(date, interestFrom);
		//按日计息，按月付息，到期还本
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType()) ||
				DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
			//借款周期类型天
			int borrowDays = 0;
			if (borrowPeriodType == 1) {
				borrowDays = borrowPeriod;
			}
			//借款周期类型月
			if (borrowPeriodType == 2) {
				Date endDate = DateUtils.addMonth(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				Date endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			result = FormulaUtil.calculateInterest(investAmount, annualizedRate, borrowDays);
			return result;
		}
		//等本等息 (按月)   不支持 借款周期为天的
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())) {
			int months = 0;
			//借款周期类型月
			if (borrowPeriodType == 2) {
				months = borrowPeriod;
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				months = borrowPeriod * 12;
			}
			result = FormulaUtil.calculateInterestAndMonthByAvg(investAmount, annualizedRate, months);
			return result;
		}
		
		//等本等息按周还款
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())) {
			if (borrowPeriodType != 4) {
				return result;
			}
			result = FormulaUtil.calculateTotalInterestByWeek(investAmount, annualizedRate, borrowPeriod);
			return result;
		}
		
		//按日计息，按季付息，到期还本
		if (DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())) {
			//借款周期类型天
			int borrowDays = 0;
			if (borrowPeriodType == 1) {
				borrowDays = borrowPeriod;
			}
			//借款周期类型月
			if (borrowPeriodType == 2) {
				Date endDate = DateUtils.addMonth(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				Date endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			
			result = FormulaUtil.calculateInterestByAvgSeason(investAmount, annualizedRate, borrowDays);
			return result;
		}
				
		return result;
	}
	
	private BigDecimal getProjectAnnulizedRate(Project project){
		BigDecimal annualizedRate = BigDecimal.ZERO;
		//年化利率
		annualizedRate = project.getAnnualizedRate();
		try {
			ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(project.getId());
			if(projectExtraAddRate!=null){
				annualizedRate = annualizedRate.add(projectExtraAddRate.getExtraAmount());
			}
		} catch (ManagerException e) {
			logger.error("【计算项目收益率】异常,projectId={}", project.getId());
		}
		return annualizedRate;
	}
	
	
	@Override
	public BigDecimal invertExpectEarningsByRate(Project project, BigDecimal investAmount, Date date, BigDecimal annualizedRate) {
		BigDecimal result = BigDecimal.ZERO;
		Integer interestFrom = project.getInterestFrom();
		//借款周期
		Integer borrowPeriod = project.getBorrowPeriod();
		//借款周期类型（1-日；2-月；3-年）
		Integer borrowPeriodType = project.getBorrowPeriodType();
		
		//开始计息日
		Date beginInterestDate = DateUtils.addDate(date, interestFrom);
		//按日计息，按月付息，到期还本
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType()) ||
				DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
			//借款周期类型天
			int borrowDays = 0;
			if (borrowPeriodType == 1) {
				borrowDays = borrowPeriod;
			}
			//借款周期类型月
			if (borrowPeriodType == 2) {
				Date endDate = DateUtils.addMonth(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				Date endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			result = FormulaUtil.calculateInterest(investAmount, annualizedRate, borrowDays);
			return result;
		}
		
		//等本等息按周还款
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())) {
			if (borrowPeriodType != 4) {
				return result;
			}
			result = FormulaUtil.calculateTotalInterestByWeek(investAmount, annualizedRate, borrowPeriod);
			return result;
		}
		
		
		//等本等息 (按月)   不支持 借款周期为天的
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())) {
			int months = 0;
			//借款周期类型月
			if (borrowPeriodType == 2) {
				months = borrowPeriod;
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				months = borrowPeriod * 12;
			}
			result = FormulaUtil.calculateInterestAndMonthByAvg(investAmount, annualizedRate, months);
			return result;
		}
		//按日计息，按季付息，到期还本
		if (DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())) {
			//借款周期类型天
			int borrowDays = 0;
			if (borrowPeriodType == 1) {
				borrowDays = borrowPeriod;
			}
			//借款周期类型月
			if (borrowPeriodType == 2) {
				Date endDate = DateUtils.addMonth(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				Date endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
	
			result = FormulaUtil.calculateInterestByAvgSeason(investAmount, annualizedRate, borrowDays);
			return result;
		}
		
		return result;
	}

	@Override
	public BigDecimal expectAmount(Project project, BigDecimal investAmount, BigDecimal extraAnnualizedRate) throws ManagerException {
		BigDecimal expectAmount = BigDecimal.ZERO;
		//年化利率
		BigDecimal annualizedRate = getAnnualizedRateByProjectIdAndInvestAmount(investAmount, project.getId());
		// 加上收益劵
		annualizedRate = annualizedRate.add(extraAnnualizedRate);
		
		int days = 0;
		days = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), project.getEndDate())+1 - project.getInterestFrom();
		if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())||DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
			// 单位利息
			BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(), investAmount, annualizedRate.setScale(2, BigDecimal.ROUND_HALF_UP));
			expectAmount = (unitInterest.multiply(new BigDecimal(days))).setScale(2, BigDecimal.ROUND_HALF_UP);
			
		}else if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode()// 还款方式：等本等息 
			.equals(project.getProfitType())) {
			List<DebtInterest> debtInterests = debtManager.getFullDebtInfoById(project.getDebtId()).getDebtInterests();
			int period = 0;
			Date startInterestDate = DateUtils.addDate(DateUtils.formatDate(DateUtils.getCurrentDate(),  DateUtils.DATE_FMT_3), project.getInterestFrom());
			if(Collections3.isNotEmpty(debtInterests)) {
				// 如果某一期的结束时间早于开始计息时间，则不记录收益和收益天数
				for (DebtInterest debtInterest : debtInterests){
					if(!startInterestDate.after(debtInterest.getEndDate())){
						BigDecimal interest = BigDecimal.ZERO;
						interest = FormulaUtil.getTransactionInterest(project.getProfitType(),
								investAmount,
								annualizedRate.setScale(2, BigDecimal.ROUND_HALF_UP),
								period,
								debtInterest.getStartDate(),
								startInterestDate,
								debtInterest.getEndDate());//应付利息
						expectAmount = expectAmount.add(interest);
						period = period + 1;
					}
				}
				
			}
		}
		return expectAmount;
	}
	
	@Override
	public int getInvestProjectCountByProjectType(String projectType,String investTypeCode) throws ManagerException {
		try {
			ProjectQuery query = new ProjectQuery();
			query.setProjectType(projectType);
			query.setInvestTypeCode(investTypeCode);
			query.setStatusCode("investing");
			return projectMapper.selectForPaginTotalCountFront(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Project> findLoseProjects() throws ManagerException {
		try {
			int status = StatusEnum.PROJECT_STATUS_LOSING.getStatus();
			return projectMapper.findProjectsByStatus(status);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc web  逾期列表
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月17日 下午1:58:48
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> getBorrowOverdueList(ProjectBorrowQuery query) throws ManagerException {
		try {
			List<ProjectInterestBiz> projectBizs = Lists.newArrayList();
			int count =projectMapper.selectCountOverdueList(query);
			if (count > 0) {
				List<ProjectInterestBiz> selectForPagin = projectMapper
						.selectOverdueList(query);
				projectBizs= findOverdueList(selectForPagin);
			}
			Page<ProjectInterestBiz> page = new Page<ProjectInterestBiz>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setData(projectBizs);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc web 流标
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月17日 下午3:31:25
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> getBorrowLabelList(ProjectBorrowQuery query) throws ManagerException {
		try {
			List<ProjectInterestBiz> projectForFrontList = Lists.newArrayList();
			List<ProjectInterestBiz> projectBizs = Lists.newArrayList();
			int count = projectMapper.selectForPaginTotalCountBorrow(query);
			if (count > 0) {
				projectForFrontList = projectMapper.selectForPaginBorrow(query);
				projectBizs =  findBorrowLabelList(projectForFrontList);
			}
			Page<ProjectInterestBiz> page = new Page<ProjectInterestBiz>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setData(projectBizs);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @Description:流标详细信息
	 * @param projectForFrontList
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月1日 上午11:20:32
	 */
	private List<ProjectInterestBiz> findBorrowLabelList(List<ProjectInterestBiz> projectForFrontList) throws ManagerException {
		List<ProjectInterestBiz> bizs = Lists.newArrayList();
		if(Collections3.isNotEmpty(projectForFrontList)){
			for(ProjectInterestBiz project:projectForFrontList){
				// 项目投资进度
				project.setProgress(getProjectNumberProgress(project.getTotalAmount(), project.getProjectId()));
				bizs.add(project);
				}
			}
		return bizs;
	}
	
	@Override
	public int autoLoseProject() throws ManagerException {
		int num = 0;
		//流标中的项目
		List<Project> projects  = findLoseProjects();
		if(Collections3.isEmpty(projects)){
			return num;
		}
		for (Project p : projects) {
			// 是否存在未处理的退款
			int hostingRefundCount = hostingRefundMapper.findHostingRefundCountByStatusAndProjectId(p.getId(),
					RefundStatus.WAIT_REFUND.name());
			if(hostingRefundCount>0){//
				continue;
			}
			// 判断是否所有的代收都退过款
			boolean existUnRefundCollect = false;
			List<HostingCollectTrade> collectTrades = hostingCollectTradeManager.selectHostCollectByProjectIdAndType(p.getId());
			for (HostingCollectTrade  hostingCollectTrade: collectTrades) {
				//存在未退过款的代收
				if(Collections3.isEmpty(hostingRefundMapper.findRefundByCollectNoForProjectLose(hostingCollectTrade.getTradeNo()))){
					existUnRefundCollect = true;
					break;
				}
			}
			if(existUnRefundCollect){
				continue;
			}
			// 项目的所有代收都创建退款，并且全都已处理
			int result = updateProjectStatus(StatusEnum.PROJECT_STATUS_LOSE.getStatus(), StatusEnum.PROJECT_STATUS_LOSING.getStatus(),
					p.getId());
			if(result>0){
				p.setStatus(StatusEnum.PROJECT_STATUS_LOSE.getStatus());
				if(p.getSaleComplatedTime()!=null){
					p.setRemarks("项目审核未通过");
				}else{
					p.setRemarks("募集未完成");
				}
				updateByPrimaryKeySelective(p);
				//合同置为已过期
				transactionManager.expireContract(p.getId());
			}
			num = num + result;
		}
		return num;
	}
	
	@Override
	public void loseProject(Long projectId) throws ManagerException {
		List<Project> projects = Lists.newArrayList();
		if(projectId!=null){
			Project project = projectMapper.selectByPrimaryKey(projectId);
			if(project!=null){
				projects.add(project);
			}
		}else{
			projects = findLoseProjects();
		}
		//查询状态为流标中的项目
		if(Collections3.isEmpty(projects)){
			logger.info("不存在流标中的项目");
			return;
		}
		for(Project project :projects){
			logger.info("[项目流标创建代收撤销开始],projectId={}", project.getId());
			//查询所有订单
			List<Order> ordersList = orderMapper.selectAllOrderList(project.getId());
			if(Collections3.isNotEmpty(ordersList)){
				for(Order order:ordersList){
				  Transaction tra = transactionManager.getTransactionByOrderId(order.getId());
				  //处理用户交易记录表状态
				  memberHistoryRepaymentMapper.updateByTransactionId(2,tra.getId());
				}
				
			}
			//查询全都使用优惠券的订单，直接退回
			List<Order> orders = orderMapper.selectAllAmountUseCouponForInvest(project.getId());
			if(Collections3.isNotEmpty(orders)){
				for(Order order:orders){
					couponManager.extendCouponEndDateForProjectLose(order, order.getOrderTime(), DateUtils.getCurrentDate());
					//更新交易表的状态为流标
					transactionMapper.updateStatusByOrderId(StatusEnum.TRANSACTION_LOSE.getStatus(),
							StatusEnum.TRANSACTION_INVESTMENTING.getStatus(),
							RemarksEnum.TRANSACTION_LOSE_AND_RETURN_CAPITAL.getRemarks(), order.getId());
					
					MessageClient.sendMsgForCommon(order.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM,
							MessageEnum.P2P_RAISE_FAIL.getCode(),
							DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_7),
							(order.getProjectName().contains("期")?order.getProjectName().substring(0, order.getProjectName().indexOf("期") + 1):order.getProjectName()),
							order.getInvestAmount().toString());
					if(!project.isDirectProject()){//非直投直接返回
						continue;
					}
					Transaction tra = transactionManager.getTransactionByOrderId(order.getId());
					//处理用户交易记录表状态
					memberHistoryRepaymentMapper.updateByTransactionId(2,tra.getId());
					//普通直投项目 流标
					MessageClient.sendMsgForCommon(order.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_DIRECT_PROJECT_FAIL.getCode(), 
							project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName(),tra.getId().toString());
					if(projectExtraManager.isQuickProject(project.getId())){//直投项目中奖用户额外通知奖励失效
						//校验流标通知是否发放过
						if(RedisActivityClient.directLotteryFailIsSend(order.getProjectId())){
							ProjectExtra pe = projectExtraManager.getProjectQucikReward(order.getProjectId());
							ActivityLotteryResult modelResult=new ActivityLotteryResult(); 
							modelResult.setActivityId(pe.getActivityId());
							modelResult.setRemark(order.getProjectId().toString());
							modelResult.setRewardType(5);
							modelResult.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);
							List<ActivityLotteryResult> rewardInfoList=activityLotteryResultManager.sumRewardInfoByMemberId(modelResult);
							if(Collections3.isNotEmpty(rewardInfoList)){
								for(ActivityLotteryResult sendResult:rewardInfoList){
									MessageClient.sendMsgForCommon(sendResult.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_QUICK_FAIL.getCode(), 
											order.getProjectName().contains("期")?order.getProjectName().substring(0, order.getProjectName().indexOf("期") + 1):order.getProjectName());
								}
							}
						}
					
					}
				}
			}
			// 查询项目下代收冻结的代收记录
			List<HostingCollectTrade> freezeList = hostingCollectTradeManager.selectPreAuthApplySuccessByProjectId(project.getId());
			if (Collections3.isNotEmpty(freezeList)) {
				// 发起代收撤销
				ResultDO<Project> rDO = new ResultDO<Project>();
				cancelPreAuthTrade(rDO, freezeList);
			} else {
				// 极端情况，所有都是用现金券投完的，在这里判断，直接项目状态改为流标
				int n = updateProjectStatus(StatusEnum.PROJECT_STATUS_LOSE.getStatus(), StatusEnum.PROJECT_STATUS_LOSING.getStatus(),
						project.getId());
				if (n < 1) {
					return;
				}
				// 更新项目流标备注
				Project p = new Project();
				p.setId(project.getId());
				if (project.getSaleComplatedTime() != null) {
					p.setRemarks("项目审核未通过");
				} else {
					p.setRemarks("募集未完成");
				}
				updateByPrimaryKeySelective(p);
				//合同置为已过期
				transactionManager.expireContract(p.getId());
			}
			logger.info("[项目流标创建代收撤销结束],projectId={}", project.getId());
		}
	}

	/**
	 * 
	 * @desc 募集中列表
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月25日 下午4:51:04
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> getBorrowRaisingList(ProjectBorrowQuery query) throws ManagerException {
		try {
			List<ProjectInterestBiz> projectBizs = Lists.newArrayList();
			int count=projectMapper.selectForPaginBorrowCount(query);
			if(count>0){
				List<ProjectInterestBiz> projectForFrontList = projectMapper.selectForPaginBorrow(query);
				projectBizs =  findBorrowRaisingList(projectForFrontList);
			}
			Page<ProjectInterestBiz> page = new Page<ProjectInterestBiz>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setData(projectBizs);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @Description:募集中详细信息
	 * @param projectForFrontList
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年2月25日 下午4:53:44
	 */
	private List<ProjectInterestBiz> findBorrowRaisingList(List<ProjectInterestBiz> projectForFrontList) throws ManagerException {
		List<ProjectInterestBiz> bizs = Lists.newArrayList();
		if(Collections3.isNotEmpty(projectForFrontList)){
			for(ProjectInterestBiz project:projectForFrontList){
				// 项目投资进度
				project.setProgress(getProjectNumberProgress(project.getTotalAmount(), project.getProjectId()));
				bizs.add(project);
				}
			}
		return bizs;
	}
	/**
	 * 
	 * @desc 获取 逾期本金、逾期利息、滞纳金、合计应还款、未还本金总额、逾期天数、逾期罚息率
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月29日 下午1:07:53
	 *
	 */
	@Override
	public OverdueRepayLogBiz getUnderWriteAmountInfoByProjectId(Long projectId) throws ManagerException {
		ProjectInterestBiz projectInterestBiz=getUnderOverdueAmountInfo(projectId);
		OverdueRepayLogBiz overdueRepayLogBiz=new OverdueRepayLogBiz();
		if(projectInterestBiz!=null){
			//逾期本金
			overdueRepayLogBiz.setOverduePrincipal(projectInterestBiz.getOverduePrincipal());
			//逾期利息
			overdueRepayLogBiz.setOverdueInterest(projectInterestBiz.getOverdueInterest());
			//滞纳金
			overdueRepayLogBiz.setOverdueFine(projectInterestBiz.getLateFees());
			//未还本金总额
			overdueRepayLogBiz.setReturnPrincipal(projectInterestBiz.getUnreturnPrincipal());
			//逾期天数
			overdueRepayLogBiz.setOverdueDay(projectInterestBiz.getOverdueDays());
			//逾期罚息率
			overdueRepayLogBiz.setOverdueFeeRate(projectInterestBiz.getOverdueFeeRate());
			//合计应还款
			overdueRepayLogBiz.setPayableAmount(projectInterestBiz.getTotalPayAmount());
			overdueRepayLogBiz.setProjectId(projectId);
			
		}
		return overdueRepayLogBiz;
	}
	/**
	 * 
	 * @desc 已还清的借款
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年3月1日 下午6:27:19
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> getHasPayoffBorrow(ProjectBorrowQuery query) throws ManagerException {
		try {
			List<ProjectInterestBiz> projectForFrontList = Lists.newArrayList();
			List<ProjectInterestBiz> projectBizs = Lists.newArrayList();
			int count=projectMapper.selectForPaginHasPayoffBorrowCount(query);
			if(count>0){
				projectForFrontList = projectMapper.selectForPaginHasPayoffBorrow(query);
				projectBizs =  findBorrowList(projectForFrontList);
			}
			Page<ProjectInterestBiz> page = new Page<ProjectInterestBiz>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(query.getCurrentPage());
			page.setiDisplayLength(query.getPageSize());
			page.setiTotalRecords(count);
			page.setData(projectBizs);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 还款中 的借款统计
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年3月2日 上午10:36:09
	 *
	 */
	@Override
	public int selectForPaginCountPayingBorrow(ProjectBorrowQuery query) throws ManagerException {
		try {
			return projectMapper.selectForPaginCountPayingBorrow(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * @desc 获取交易列表的收益状态
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author fuyili
	 * @time 2016年3月8日 上午11:49:21
	 *
	 */
	@Override
	public boolean getTransactionListProfitStatus(Long projectId) throws ManagerException {
		boolean flag = false;
		try {
			if (projectId == null) {
				return flag;
			}
			Project project = this.selectByPrimaryKey(projectId);
			if (project == null) {
				return flag;
			}
			Set<Integer> set = new HashSet<Integer>();
			set.add(StatusEnum.PROJECT_STATUS_INVESTING.getStatus());// 30
			set.add(StatusEnum.PROJECT_STATUS_STOP.getStatus());// 40
			set.add(StatusEnum.PROJECT_STATUS_FULL.getStatus());// 50
			set.add(StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus());// 51
			set.add(StatusEnum.PROJECT_STATUS_END.getStatus());// 60
			set.add(StatusEnum.PROJECT_STATUS_LOSE.getStatus());// 90
			set.add(StatusEnum.PROJECT_STATUS_LOSING.getStatus());// 80
			if (set.contains(project.getStatus()) && ProjectEnum.PROJECT_TYPE_DIRECT.getType() == project.getInvestType()) {
				flag = true;
				return flag;
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return flag;
	}

	@Override
	public List<ProjectInterestBiz> selectForPaginPayingBorrow(ProjectBorrowQuery query) throws ManagerException {
		try {
			return projectMapper.selectForPaginPayingBorrow(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int countCurrentBorrowerByMemberId(Long memberId) throws ManagerException {
		try {
			return projectMapper.countCurrentBorrowerByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<List<HostingPayTrade>> afterOverdueRepayCollectNotify(String tradeNo, String outTradeNo, String tradeStatus)
			throws Exception {
		ResultDO<List<HostingPayTrade>> result = new ResultDO<List<HostingPayTrade>>();
		try {
			HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
			HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getByIdForLock(collectTrade.getId());
			if (hostingCollectTrade != null) {
				// 如果是最终状态，则直接返回
				if (hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
						|| hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
						|| hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())) {
					logger.info("垫资还款代收已经是最终状态，tradeNo=" + tradeNo);
					return result;
				}
				// 将交易状态置为最终状态
				hostingCollectTrade.setTradeStatus(tradeStatus);
				hostingCollectTrade.setOutTradeNo(outTradeNo);
				hostingCollectTradeManager.updateHostingCollectTrade(hostingCollectTrade);
				// 如果交易为交易成功状态
				if (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
					// 写流水
					Balance balance = new Balance();
					try {
						balance = balanceManager.synchronizedBalance(hostingCollectTrade.getPayerId(), TypeEnum.BALANCE_TYPE_PIGGY);
					} catch (Exception e) {
						logger.error("【逾期垫资还款】查询第三方余额失败,payerId={}", hostingCollectTrade.getPayerId());
						balance = balanceManager.queryBalance(hostingCollectTrade.getPayerId(), TypeEnum.BALANCE_TYPE_PIGGY);
					}
					Project p = projectMapper.selectByPrimaryKey(hostingCollectTrade.getProjectId());
					String remark = "";
					if(p!=null){
						remark = StringUtil.getShortProjectName(p.getName()) ;
					}
					// 记录借款人垫资还款资金流水
					capitalInOutLogManager.insert(hostingCollectTrade.getPayerId(), TypeEnum.FINCAPITALINOUT_TYPE_UNDERWRITE_TYPE, null,
							hostingCollectTrade.getAmount(), balance.getAvailableBalance(), hostingCollectTrade.getId().toString(), remark,
							TypeEnum.BALANCE_TYPE_PIGGY);
					List<HostingPayTrade> hostingPayTrades = createLocalHostPayForOverdueRepay(collectTrade);
					result.setResult(hostingPayTrades);
				}
				if (TradeStatus.TRADE_FAILED.name().equals(tradeStatus) || TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)) {
					// 更新逾期还款记录为失败
					overdueRepayLogManager.updateStatusById(StatusEnum.OVERDUE_REPAYSTATUS_PAYING.getStatus(),
							StatusEnum.OVERDUE_REPAYSTATUS_PAYFAIL.getStatus(), hostingCollectTrade.getSourceId());
					// 设置逾期记录为未还款
					overdueLogManager.updateStatus(StatusEnum.OVERDUE_LOG_PAYING.getStatus(), StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus(),
							null, hostingCollectTrade.getSourceId(), null);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	private List<HostingPayTrade> createLocalHostPayForOverdueRepay(HostingCollectTrade collectTrade) throws Exception {
		List<HostingPayTrade> hostingPayTradeList = Lists.newArrayList();
		try {
			// 根据代收查询逾期还款记录
			OverdueRepayLog overdueRepayLog = overdueRepayLogManager.selectByPrimaryKey(collectTrade.getSourceId());
			if (overdueRepayLog == null) {
				logger.error("【垫资代付】collectTradeId={},逾期还款记录不存在", collectTrade.getId());
				return null;
			}
			// 查找对应的逾期还款记录
			String overdueIds = overdueRepayLog.getOverdueId();
			if (overdueIds == null) {
				logger.error("【垫资代付】逾期还款记录对应的逾期记录不存在，逾期还款记录id={}，逾期记录id={}", overdueRepayLog.getId(), overdueIds);
				return null;
			}
			String[] overdueIdArray = overdueIds.split(",");
			String batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo();
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			for (String overdueId : overdueIdArray) {
				// 根据逾期记录id获取逾期记录
				OverdueLog overdueLog = overdueLogManager.selectByPrimaryKey(Long.valueOf(overdueId));
				if(overdueLog.getStatus()==StatusEnum.OVERDUE_LOG_PAYING.getStatus()){
					BigDecimal overduePrincipal = overdueLog.getOverduePrincipal()==null?BigDecimal.ZERO:overdueLog.getOverduePrincipal();
					BigDecimal overdueInterest = overdueLog.getOverdueInterest()==null?BigDecimal.ZERO:overdueLog.getOverdueInterest();
					BigDecimal overdueFine = overdueLog.getOverdueFine()==null?BigDecimal.ZERO:overdueLog.getOverdueFine();
					BigDecimal returnAmount = overduePrincipal.add(overdueInterest).add(overdueFine);
					if (overdueLog != null) {
						HostingPayTrade hostingPay = new HostingPayTrade();
						hostingPay.setTradeNo(SerialNumberUtil.generatePayTradeaNo(overdueLog.getUnderwriteMemberId()));
						hostingPay.setBatchPayNo(batchPayNo);
						hostingPay.setAmount(returnAmount);
						hostingPay.setPayeeId(overdueLog.getUnderwriteMemberId());
						hostingPay.setSourceId(Long.valueOf(overdueId));
						hostingPay.setTradeStatus(TradeStatus.WAIT_PAY.name());
						hostingPay.setType(TypeEnum.HOSTING_PAY_TRADE_OVERDUE_REPAY.getType());
						hostingPay.setProjectId(collectTrade.getProjectId());
						hostingPay.setUserIp(ip);
						if (hostingPayTradeManager.insertSelective(hostingPay) > 0) {
							hostingPayTradeList.add(hostingPay);
						}
					}
				}
			}
			return hostingPayTradeList;
		} catch (ManagerException e) {
			throw e;
		}
	}

	@Override
	public void createSinpayHostingPayTradeForRepay(List<HostingPayTrade> hostingPayTrades) throws Exception {
		// 创建批付参数
		if (Collections3.isEmpty(hostingPayTrades)) {
			logger.error("【垫资代付】本地代付记录为空，hostingPayTrades={}", hostingPayTrades);
			return;
		}
		String batchPayNo = hostingPayTrades.get(0).getBatchPayNo();
		String ip=hostingPayTrades.get(0).getUserIp();
		String summary = "垫资逾期还款";
		List<TradeArgs> tradeArgList = Lists.newArrayList();
		for (HostingPayTrade hostingPayTrade : hostingPayTrades) {
			TradeArgs tradeArg = new TradeArgs();
			tradeArg.setAccountType(AccountType.SAVING_POT);
			tradeArg.setIdType(IdType.UID);
			tradeArg.setMoney(new Money(hostingPayTrade.getAmount()));
			tradeArg.setPayeeId(SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()));
			tradeArg.setRemark(summary);
			tradeArg.setTradeNo(hostingPayTrade.getTradeNo());
			tradeArgList.add(tradeArg);
		}
		sinaPayClient.createBatchPayTrade(batchPayNo, summary, ip, tradeArgList, TradeCode.PAY_TO_UNDERWRITER, BatchTradeNotifyMode.single_notify);
	}

	@Override
	public ResultDO<?> afterOverdueRepayHostingPay(String tradeStatus, String tradeNo, String outTradeNo) throws Exception {
		ResultDO<?> result = new ResultDO<>();
		try {
			HostingPayTrade payTrade = hostingPayTradeManager.getByTradeNo(tradeNo);
			HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByIdForLock(payTrade.getId());
			// 如果是最终状态，则直接返回
			if (hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
					|| hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())) {
				logger.info("垫资还款代付已经是最终状态，tradeNo=" + tradeNo);
				return result;
			}
			// 将交易状态置为最终状态
			hostingPayTrade.setTradeStatus(tradeStatus);
			hostingPayTrade.setOutTradeNo(outTradeNo);
			int updateNum = hostingPayTradeManager.updateHostingPayTradeStatus(tradeStatus, TradeStatus.WAIT_PAY.name(), outTradeNo,
					hostingPayTrade.getId());
			if (updateNum > 0) {
				if (hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())) {
					// 代付成功处理业务
					// 插入垫资人资金流水，type19:垫资还款
					// 写流水
					Balance balance = new Balance();
					try{
						balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
					}catch(Exception e){
						logger.error("【垫资还款】新浪查询余额失败，memberId={}",hostingPayTrade.getPayeeId());
						balance = balanceManager.queryBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
					}
					OverdueLog overdueLog = overdueLogManager.selectByPrimaryKey(hostingPayTrade.getSourceId());
					Project p = projectMapper.selectByPrimaryKey(overdueLog.getProjectId());
					String remark = "";
					if(p!=null){
						remark = StringUtil.getShortProjectName(p.getName()) ;
						if(overdueLog.getOverdueFine()!=null && overdueLog.getOverdueFine().compareTo(BigDecimal.ZERO)>0){
							remark = remark +" 滞纳金"+ overdueLog.getOverdueFine();
						}
					}
					
					// 记录垫资人资金流水
					capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(), TypeEnum.FINCAPITALINOUT_TYPE_UNDERWRITE_TYPE,
							hostingPayTrade.getAmount(), null, balance.getAvailableBalance(),
							String.valueOf(hostingPayTrade.getSourceId()), remark, TypeEnum.BALANCE_TYPE_PIGGY);

					// 设置垫资表的垫资状态为已结束，设置垫资结束时间为逾期还款时间
					underwriteLogManager.updateForOverdueRepaySuccess(StatusEnum.UNDERWRITE_STATUS_HAD_UNDERWRITE.getStatus(),
							StatusEnum.UNDERWRITE_STATUS_OVER_UNDERWRITE.getStatus(), "线上逾期垫资还款", overdueLog.getUnderwriteId());
					// 逾期记录状态已还款
					overdueLogManager.updateForOverdueRepaySuccess(StatusEnum.OVERDUE_LOG_PAYING.getStatus(),
							StatusEnum.OVERDUE_LOG_HAD_PAY.getStatus(), overdueLog.getId());
					// 判断批付号下的代付都成功，跳转到步骤3
					if (hostingPayTradeManager.countHostPayByBatchNoAndStatus(hostingPayTrade.getBatchPayNo(), TradeStatus.WAIT_PAY.name()) <= 0) {
						// 更新逾期还款记录状态为已还款，根据逾期记录id
						overdueRepayLogManager.updateStatusByOverdueId(StatusEnum.OVERDUE_REPAYSTATUS_PAYING.getStatus(),
								StatusEnum.OVERDUE_REPAYSTATUS_HADPAY.getStatus(), overdueLog.getId());
					}
				}
			}
			return result;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public ResultDO<?> createHostPayForOverdueRepayByCollectTradeNo(String collectTradeNo) throws Exception{
		ResultDO<?> result = new ResultDO();
		HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNo(collectTradeNo);
		List<HostingPayTrade> hostingPayTrades = createLocalHostPayForOverdueRepay(collectTrade);
		try {
			if(Collections3.isEmpty(hostingPayTrades)){
				result.setSuccess(false);
				return result;
			}
			createSinpayHostingPayTradeForRepay(hostingPayTrades);
		} catch (Exception e) {
			logger.error("【垫资贷还异常处理接口】，调用新浪代收失败，collectTradeNo={}",collectTradeNo,e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public int getDirectProjectCount()throws ManagerException {
		try {
			return projectMapper.getDirectProjectCount();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Project averageCapitalMethod() throws ManagerException{
		try {
			return projectMapper.averageCapitalMethod();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Project averageCapitalMethoding() throws ManagerException{
		try {
			return projectMapper.averageCapitalMethoding();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	@Override
	public Project averageCapitalMethodNoticing() throws ManagerException{
		try {
			return projectMapper.averageCapitalMethodNoticing();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<DirectSettlementBiz> findDirectSettlementByPage(Page<DirectSettlementBiz> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			
			int totalCount = projectMapper.selectForPaginTotalInterestSettlementCount(map);
			List<DirectSettlementBiz> selectForPagin = projectMapper.selectForPaginInterestSettlement(map);
			if(map.containsKey("grossProfitStart")||map.containsKey("grossProfitEnd")){
				selectForPagin=queryGrossProfit(selectForPagin,map);
				if(selectForPagin!=null){
					totalCount=selectForPagin.size();
				}
			}
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	private List<DirectSettlementBiz> queryGrossProfit(List<DirectSettlementBiz> listSettlment,Map<String, Object> map) {
		List<DirectSettlementBiz> list=Lists.newArrayList();
		String startGross="";
		String endGross="";
		BigDecimal start=BigDecimal.ZERO;
		BigDecimal end=BigDecimal.ZERO;
		if(map.get("grossProfitStart")!=null){
			startGross=map.get("grossProfitStart").toString();
			start=new BigDecimal(startGross);
		}
		if(map.get("grossProfitEnd")!=null){
			endGross=map.get("grossProfitEnd").toString();
			end=new BigDecimal(endGross);
		}
		for(DirectSettlementBiz biz:listSettlment){
			//管理费
			BigDecimal manage = BigDecimal.ZERO;
			//收益券支出
			BigDecimal extraInterest = BigDecimal.ZERO;
			if(biz.getExtraInterest()==null){
				extraInterest = BigDecimal.ZERO;
			}
			//现金券支出
			BigDecimal usedCouponAmount = BigDecimal.ZERO;
			if(biz.getUsedCouponAmount()==null){
				usedCouponAmount = BigDecimal.ZERO;
			}
			//毛利润
			BigDecimal grossProfit = BigDecimal.ZERO;
			
			manage=FormulaUtil.getManagerAmount(biz.getTotalAmount(),biz.getManageFeeRate());
			
			grossProfit=manage.subtract(extraInterest).subtract(usedCouponAmount);
			if(map.get("grossProfitStart")!=null&&map.get("grossProfitEnd")!=null){
				if(grossProfit.compareTo(start)>=0&&grossProfit.compareTo(end)<=0){
					list.add(biz);
				}
			}else if(map.get("grossProfitStart")!=null&&map.get("grossProfitEnd")==null){
				if(grossProfit.compareTo(start)>=0){
					list.add(biz);
				}
			}else{
				if(grossProfit.compareTo(end)<=0){
					list.add(biz);
				}
			}
		}
		return list;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<HostingPayTrade> afterGuaranteeFeeCollectNotify(String tradeNo, String outTradeNo,
			String tradeStatus) throws Exception{
			ResultDO<HostingPayTrade> result = new ResultDO<HostingPayTrade>();
		try {
			HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
			HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getByIdForLock(collectTrade.getId());
			if (hostingCollectTrade == null) {
				return result;
			}
			// 如果是最终状态，则直接返回
			if (hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
					|| hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
					|| hostingCollectTrade.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())) {
				logger.info("【项目保证金代收】已经是最终状态，tradeNo=" + tradeNo);
				return result;
			}
			// 将交易状态置为最终状态
			hostingCollectTrade.setTradeStatus(tradeStatus);
			hostingCollectTrade.setOutTradeNo(outTradeNo);
			hostingCollectTradeManager.updateHostingCollectTrade(hostingCollectTrade);
			// 如果交易为交易成功状态
			if (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
				// 写流水
				Balance balance = new Balance();
				try {
					balance = balanceManager.synchronizedBalance(hostingCollectTrade.getPayerId(),
							TypeEnum.BALANCE_TYPE_PIGGY);
				} catch (Exception e) {
					logger.error("【项目保证金归还】查询第三方余额失败,payerId={}", hostingCollectTrade.getPayerId());
					balance = balanceManager
							.queryBalance(hostingCollectTrade.getPayerId(), TypeEnum.BALANCE_TYPE_PIGGY);
				}
				Project p = projectMapper.selectByPrimaryKey(hostingCollectTrade.getProjectId());
				String remark = "";
				if (p != null) {
					remark = StringUtil.getShortProjectName(p.getName());
				}
				// 记录保证金归还平台资金流水
				capitalInOutLogManager.insert(hostingCollectTrade.getPayerId(),
						TypeEnum.FINCAPITALINOUT_TYPE_PAY_GUARANTEE_FEE, null, hostingCollectTrade.getAmount(),
						balance.getAvailableBalance(), hostingCollectTrade.getId().toString(), remark,
						TypeEnum.BALANCE_TYPE_BASIC);
				//代付给借款人
				HostingPayTrade hostingPayTrade = createLocalHostPayForGuaranteeFee(collectTrade,p);
				result.setResult(hostingPayTrade);
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	public HostingPayTrade createLocalHostPayForGuaranteeFee(HostingCollectTrade collectTrade,Project project) throws Exception {
		if (project == null) {
			return null;
		}
		if(hostingPayTradeManager.haveHostPayTradeForPayInterestAndPrincipal( collectTrade.getSourceId(), TypeEnum.HOSTING_PAY_TRADE_GUARANTEEFEE_RETURN.getType())){
			logger.error("【项目保险金】,projectId={}已经代收过了，不能重复代收",project.getId());
			return null;
		}
		String ip = null;
		SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
		if (dict != null) {
			ip = dict.getValue();
		}
		HostingPayTrade hostingPay = new HostingPayTrade();
		hostingPay.setTradeNo(SerialNumberUtil.generatePayTradeaNo(project.getBorrowerId()));
		hostingPay.setAmount(collectTrade.getAmount());
		hostingPay.setPayeeId(project.getBorrowerId());
		hostingPay.setSourceId(collectTrade.getSourceId());
		hostingPay.setTradeStatus(TradeStatus.WAIT_PAY.name());
		hostingPay.setType(TypeEnum.HOSTING_PAY_TRADE_GUARANTEEFEE_RETURN.getType());
		hostingPay.setProjectId(project.getId());
		hostingPay.setRemarks("项目还款后保证金归还");
		hostingPay.setUserIp(ip);
		if (hostingPayTradeManager.insertSelective(hostingPay) > 0) {
			return hostingPay;
		}
		return null;
	}

	@Override
	public void createSinpayHostingPayTradeForGuaranteeFee(HostingPayTrade hostingPayTrade) {
		if (hostingPayTrade == null) {
			logger.error("【保证金归还代付】本地代付记录为空，hostingPayTrade={}", hostingPayTrade);
			return;
		}
		try {
			sinaPayClient.createSinglePayTrade(AccountType.SAVING_POT, hostingPayTrade.getAmount(),
					hostingPayTrade.getTradeNo(), IdType.UID,
					SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()), hostingPayTrade.getRemarks(),hostingPayTrade.getUserIp(),
					TradeCode.PAY_TO_BORROWER_FOR_GUARANTEE_FEE);
		} catch (Exception e) {
			logger.error("【保证金还款给借款人代付失败】，hostingPayTrade={}", hostingPayTrade, e);
		}
	}

	@Override
	public ResultDO<?> afterGuaranteeFeeHostingPay(String tradeStatus, String tradeNo, String outTradeNo) throws Exception{
		ResultDO<?> result = new ResultDO<>();
		try {
			HostingPayTrade payTrade = hostingPayTradeManager.getByTradeNo(tradeNo);
			HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByIdForLock(payTrade.getId());
			// 如果是最终状态，则直接返回
			if (hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
					|| hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())) {
				logger.info("保证金代付已经是最终状态，tradeNo=" + tradeNo);
				return result;
			}
			// 将交易状态置为最终状态
			hostingPayTrade.setTradeStatus(tradeStatus);
			hostingPayTrade.setOutTradeNo(outTradeNo);
			int updateNum = hostingPayTradeManager.updateHostingPayTradeStatus(tradeStatus, TradeStatus.WAIT_PAY.name(), outTradeNo,
					hostingPayTrade.getId());
			if (updateNum > 0) {
				if (hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())) {
					// 写流水
					Project p = projectMapper.selectByPrimaryKey(payTrade.getProjectId());
					String remark = "";
					if (p != null) {
						remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_GUARANTEEFEE.getRemarks(),
								StringUtil.getShortProjectName(p.getName()));
					}
					Balance balance = null;
					try{
						balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
					}catch(Exception e){
						logger.error("【保证金代付成功】新浪查询余额失败，memberId={}",hostingPayTrade.getPayeeId());
						balance = balanceManager.queryBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
					}
					// 记录垫资人资金流水
					capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(), TypeEnum.FINCAPITALINOUT_TYPE_PAY_GUARANTEE_FEE,
							hostingPayTrade.getAmount(), null, balance.getAvailableBalance(),
							String.valueOf(hostingPayTrade.getSourceId()), remark, TypeEnum.BALANCE_TYPE_PIGGY);
					// 更新费用表的状态
					projectFeeMapper.updateAfterReturnFee(StatusEnum.MANAGEMENT_FEE_GATHER_STATE_RETURNED.getStatus(),
							StatusEnum.MANAGEMENT_FEE_GATHER_STATE_GATHERED.getStatus(), hostingPayTrade.getSourceId());
				}
			}
		}catch (Exception e) {
			throw e;
		}
		return result;
	}

	@Override
	public synchronized ResultDO<HostingCollectTrade> createCollectTradeForGuaranteeFee(Project project) throws Exception{
		ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
		if (project == null) {
			result.setSuccess(false);
			return result;
		}
		HostingCollectTrade guaranteeCollect = insertLocalHostingCollectTradeForGuaranteeFee(project);
		if(guaranteeCollect == null){
			logger.info("【项目归还保证金】-【本地代收】-创建代收失败");
			result.setSuccess(false);
			return result;
		}
		try {
			// 调用第三方接口完成托管代收交易
			sinaPayClient.createHostingCollectTrade(
					guaranteeCollect.getTradeNo(),
					TypeEnum.HOSTING_COLLECT_TRADE_PROJECT_GUARNANTEEFEE.getDesc(), 
					SinaPayConfig.indentityEmail, guaranteeCollect.getPayerIp(), AccountType.BASIC,
					guaranteeCollect.getAmount(), IdType.EMAIL, TradeCode.COLLECT_FROM_PLATFORM_FOR_GUARANTEEFEE,
					TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode());
		} catch (Exception e) {
			logger.error("【项目保险金归还】-【新浪代收】-创建代收失败", e);
			result.setSuccess(false);
		}
		result.setResult(guaranteeCollect);
		return result;
	}
	
	@Override
	public List<Project> getRecommendProject(Integer num) throws ManagerException{
		try {
			return projectMapper.getRecommendProject(num);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Project> findInvestingDebtProject(Map<String, Object> map) throws ManagerException{
		try {
			return projectMapper.findInvestingDebtProject(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	/**
	 * @Description:为保证金归还保存本地代收
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年5月27日 上午11:28:30
	 */
	private HostingCollectTrade insertLocalHostingCollectTradeForGuaranteeFee(Project project)  {
		Long projectId = project.getId();
		//根据项目id查询保险金id
		try {
			ProjectFee guaranteeFee = managementFeeManager.getProjectFeeByProjectIdType(projectId, TypeEnum.FEE_TYPE_GUARANTEE.getType());
			if(guaranteeFee==null){
				logger.info("项目保证金费记录不存在，不需要创建本地代收");
				return null;
			}
			if(guaranteeFee.getAmount()==null || guaranteeFee.getAmount().compareTo(BigDecimal.ZERO)<=0){
				logger.info("项目保证金金额小于等于0，不需要创建本地代收");
				return null;
			}
			HostingCollectTrade trade = hostingCollectTradeManager.selectWaitPayOrFinishedHostingCollectTrade(
					guaranteeFee.getId(), TypeEnum.HOSTING_COLLECT_TRADE_PROJECT_GUARNANTEEFEE.getType());
			if (trade != null) {
				logger.info("项目保证金" + projectId + ",已经存在代收，创建代收失败");
				return null;
			}
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			BigDecimal guaranteeFeeAmount = FormulaUtil.getManagerAmount(project.getTotalAmount(), project.getGuaranteeFeeRate());
			// 本地保存HostingCollectTrade信息
			HostingCollectTrade collectTrade = new HostingCollectTrade();
			collectTrade.setAmount(guaranteeFeeAmount);
			collectTrade.setProjectId(projectId);
			collectTrade.setPayerId(SerialNumberUtil.getInternalMemberId());
			collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
			collectTrade.setRemarks("项目保证金归还" + ":projectId=" + projectId);
			collectTrade.setSourceId(guaranteeFee.getId());
			collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
			collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
			collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
			collectTrade.setPayStatus((SinaPayEnum.PAY_PROCESSING.getCode()));
			collectTrade.setTradeTime(DateUtils.getCurrentDate());
			collectTrade.setType(TypeEnum.HOSTING_COLLECT_TRADE_PROJECT_GUARNANTEEFEE.getType());
			collectTrade.setPayerIp(ip);
			if (hostingCollectTradeManager.insertSelective(collectTrade) > 0) {
				return collectTrade;
			}
		} catch (Exception e) {
			logger.error("交易出错[保存HostingCollectTrade信息出错]：project={}", project, e);
		}
		return null;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Project> afterHandlePreAuthTrade(Long projectId, boolean reAuthFlag) throws Exception {
		ResultDO<Project> rDO = new ResultDO<Project>();
		try {
			Project project = selectByPrimaryKey(projectId);
			if (project == null) {
				rDO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
				return rDO;
			}
			rDO.setResult(project);
			if (StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus() != project.getStatus()
					&& StatusEnum.PROJECT_STATUS_LOSING.getStatus() != project.getStatus()) {
				rDO.setResultCode(ResultCode.PROJECT_LOSE_NOT_AUTH_STATUS_ERROR);
				return rDO;
			}
			if (!project.isDirectProject()) {
				rDO.setResultCode(ResultCode.PROJECT_INVEST_TYPE_ERROR);
				return rDO;
			}
			// 查询项目下代收冻结的代收记录
			List<HostingCollectTrade> freezeList = hostingCollectTradeManager.selectPreAuthApplySuccessByProjectId(project.getId());
			if (Collections3.isEmpty(freezeList)) {
				// 发起代收完成/撤销后续业务
				afterCollectTradeAuth(rDO);
				return rDO;
			}
			if (reAuthFlag) {
				// 再次发起代收完成或者撤销
				authCollectTradeByProject(rDO, freezeList);
			}
		} catch (Exception e) {
			logger.error("单笔代收完成/撤销后续业务处理失败 projectId={}", projectId, e);
			throw new Exception(e);
		}
		return rDO;
	}

	/**
	 * 
	 * @Description:发起代收完成/撤销后续业务
	 * @param rDO
	 * @author: wangyanji
	 * @throws ManagerException
	 * @time:2016年5月26日 下午6:20:00
	 */
	private void afterCollectTradeAuth(ResultDO<Project> rDO) throws ManagerException {
		Project project = rDO.getResult();
		try {
			if (StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus() == project.getStatus()) {
				allCollectTradeFinish(rDO);
			} else if (StatusEnum.PROJECT_STATUS_LOSING.getStatus() == project.getStatus()) {
				allCollectTradeCancel(rDO);
			}
		} catch (Exception e) {
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("发起代收完成/撤销后续业务失败！projectId={}, projectStatus={}", project.getId(), project.getStatus(), e);
		}
	}

	/**
	 * 
	 * @Description:全部代收完成
	 * @param rDO
	 * @author: wangyanji
	 * @throws ManagerException
	 * @time:2016年5月26日 下午5:23:57
	 */
	private void allCollectTradeFinish(ResultDO<Project> rDO) throws Exception {
		Project project = rDO.getResult();
		// 更新项目状态为待放款，防止并发
		int num = updateProjectStatus(StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus(), StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus(),
				project.getId());
		if (num < 1) {
			logger.info("代收全部完成后续业务处理失败, 项目非审核通过状态, projectId={}", project.getId());
			return;
		}
		RedisManager.removeString(RedisConstant.REDIS_KEY_ACTIVITY+RedisConstant.REDIS_SEPERATOR+ RedisConstant.REDIS_KEY_LAST_INVEST+RedisConstant.REDIS_SEPERATOR+project.getId());
		RedisManager.removeString(RedisConstant.REDIS_KEY_ACTIVITY+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_MOST_INVEST+RedisConstant.REDIS_SEPERATOR+project.getId());
		RedisManager.removeString(RedisConstant.REDIS_KEY_ACTIVITY+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_MOST_LAST_INVEST+RedisConstant.REDIS_SEPERATOR+project.getId());
		RedisManager.removeString(RedisConstant.REDIS_KEY_ACTIVITY+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_LUCK_INVEST+RedisConstant.REDIS_SEPERATOR+project.getId());
		RedisManager.removeString(RedisConstant.REDIS_KEY_ACTIVITY+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_ACTIVITY_LEADING_SHEEP+RedisConstant.REDIS_SEPERATOR+project.getId());
		collectTradeFinishBiz(project);

		// 通知财务放款
		MessageClient.sendMsgForCommonBymobile(getMobileList("loan_mobile"), Constant.MSG_NOTIFY_TYPE_SYSTEM,
				MessageEnum.NOTICE_LOAN.getCode(), project.getName(),
				DateUtils.getStrFromDate(DateUtils.addDaysByDate(DateUtils.getCurrentDate(), 1), DateUtils.TIME_PATTERN_SHORT_2));
		// 直投项目合同保全
		taskExecutor.execute(new ProcessContractPreservationThread(project.getId()));

	}

	private void collectTradeFinishBiz(Project project) throws Exception {
		try {
			// 生成项目本息记录
			List<DebtInterest> interests = calculateInterest(project, project.getSaleComplatedTime());
			logger.info("【直投项目放款】销售完成时间={}", project.getSaleComplatedTime());
			int totalDays = 0;
			if (Collections3.isNotEmpty(interests)) {
				for (DebtInterest debtInterest : interests) {
					debtInterest.setDebtId(0L);
					debtInterest.setProjectId(project.getId());
					debtInterestMapper.insert(debtInterest);// 插入本息记录
				}
				totalDays = DateUtils.getIntervalDays(interests.get(0).getStartDate(), interests.get(interests.size() - 1).getEndDate()) + 1;
				// 更新项目开始时间和结束时间
				Project p = new Project();
				p.setStartDate(interests.get(0).getStartDate());
				p.setEndDate(interests.get(interests.size() - 1).getEndDate());
				p.setId(project.getId());
				updateByPrimaryKeySelective(p);// TODO 重写一个更新开始时间和结束时间的方法
			}
			// 查询项目本息（得到项目本息id）
			interests = debtInterestManager.findInterestlistByProjectId(project.getId());
			// 交易后续操作
			transactionManager.afterDirectProjectFinishCollectTradeProcess(project, interests, totalDays);
			// 平台累计投资额
			balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_INVEST, project.getTotalAmount(), -1L);
			// 统计项目的交易利息总额
			BigDecimal totalInterest = transactionManager.getTotalTransactionInterestByProject(project.getId());
			balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_INTEREST,
					totalInterest.setScale(2, BigDecimal.ROUND_HALF_UP), -1L);
		} catch (Exception e) {
			logger.error("代收完成全部成功后处理失败，", e);
			throw e;
		}
	}

	/**
	 * 
	 * @Description:全部代收撤销
	 * @param rDO
	 * @author: wangyanji
	 * @throws ManagerException
	 * @time:2016年5月26日 下午5:24:07
	 */
	private void allCollectTradeCancel(ResultDO<Project> rDO) throws ManagerException {
		Project project = rDO.getResult();
		// 更新项目状态为流标
		updateProjectStatus(StatusEnum.PROJECT_STATUS_LOSE.getStatus(), StatusEnum.PROJECT_STATUS_LOSING.getStatus(), project.getId());
		// 更新项目流标备注
		Project p = new Project();
		p.setId(project.getId());
		if (project.getSaleComplatedTime() != null) {
			p.setRemarks("项目审核未通过");
		} else {
			p.setRemarks("募集未完成");
		}
		updateByPrimaryKeySelective(p);
		// 合同置为已过期
		transactionManager.expireContract(project.getId());
	}

	/**
	 * 
	 * @Description:根据项目发起代收完成/撤销
	 * @param rDO
	 * @param freezeList
	 *            : true 非首次发起代收完成/撤销(先同步交易状态), false 首次发起代收完成/撤销
	 * @author: wangyanji
	 * @throws Exception
	 * @time:2016年5月26日 下午6:38:59
	 */
	private void authCollectTradeByProject(ResultDO<Project> rDO, List<HostingCollectTrade> freezeList) throws Exception {
		Project project = rDO.getResult();
		List<HostingCollectTrade> resultList = Lists.newArrayList();
		// 先同步交易状态
		resultList = hostingCollectTradeAuthManager.synHostingCollectTrade(freezeList);
		// 同步交易状态之后全部代收记录已经完成,再次回调之前业务。
		if (Collections3.isEmpty(resultList)) {
			afterHandlePreAuthTrade(project.getId(), true);
			return;
		}
		if (StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus() == project.getStatus()) {
			// 发起代收完成
			finishPreAuthTrade(rDO, resultList);
		} else if (StatusEnum.PROJECT_STATUS_LOSING.getStatus() == project.getStatus()) {
			// 发起代收撤销
			cancelPreAuthTrade(rDO, resultList);
		}
	}

	/**
	 * 
	 * @Description:代收完成接口
	 * @param rDO
	 * @param freezeList
	 * @param freezeList
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年5月27日 上午9:45:56
	 */
	@Override
	public void finishPreAuthTrade(ResultDO<Project> rDO, List<HostingCollectTrade> freezeList) throws ManagerException {
		Project project = rDO.getResult();
		try {
			// 单次请求100条代收
			List<List<HostingCollectTrade>> subList = Lists.partition(freezeList, 10);
			boolean isError = false;
			StringBuffer errorMsg = new StringBuffer("代收完成失败：");
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			for (int i = 0, size = subList.size(); i < size; i++) {
				String outRequestNo = SerialNumberUtil.generateFinishCollecTradeBatchNo(i);
				List<FinishAuthTradeArgs> tradeList = Lists.newArrayList();
				List<HostingCollectTradeAuth> records = Lists.newArrayList();
				for (HostingCollectTrade collectTrade : subList.get(i)) {
					// 封装交易参数
					FinishAuthTradeArgs args = new FinishAuthTradeArgs();
					args.setRequestNo(SerialNumberUtil.generateFinishCollecTradeRequestNo(collectTrade.getSourceId()));
					args.setTradeNo(collectTrade.getTradeNo());
					args.setAmount(new Money(collectTrade.getAmount()));
					args.setSummary(RemarksEnum.PROJECT_AUTH_PASS.getRemarks());
					tradeList.add(args);
					// 封装本地记录参数
					HostingCollectTradeAuth collectAuth = new HostingCollectTradeAuth();
					collectAuth.setTradeNo(args.getTradeNo());
					collectAuth.setTradeRequestNo(args.getRequestNo());
					collectAuth.setBatchRequestNo(outRequestNo);
					collectAuth.setAuthType(TypeEnum.COLLECT_TRADE_FINISH_TYPE.getType());
					collectAuth.setProjectId(collectTrade.getProjectId());
					collectAuth.setUserIp(collectTrade.getPayerIp());
					records.add(collectAuth);
				}
				hostingCollectTradeAuthManager.batchInsert(records);
				ResultDto<?> sinaRdo = sinaPayClient.finishPreAuthTrade(outRequestNo, tradeList,ip);
				if (!sinaRdo.isSuccess()) {
					isError = true;
					errorMsg.append("【批次号:" + outRequestNo + "】" + sinaRdo.getErrorMsg());
				}
			}
			if (isError) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM);
				rDO.getResultCode().setMsg(errorMsg.toString());
			}
		} catch (Exception e) {
			logger.error("代收完成失败 projectId={}", project.getId(), e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
	}

	/**
	 * 
	 * @Description:代收撤销接口
	 * @param rDO
	 * @param freezeList
	 * @param freezeList
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年5月27日 上午9:45:56
	 */
	@Override
	public void cancelPreAuthTrade(ResultDO<Project> rDO, List<HostingCollectTrade> freezeList) throws ManagerException {
		Project project = rDO.getResult();
		try {
			// 单次请求100条代收
			List<List<HostingCollectTrade>> subList = Lists.partition(freezeList, 10);
			boolean isError = false;
			StringBuffer errorMsg = new StringBuffer("代收撤销失败：");
			for (int i = 0, size = subList.size(); i < size; i++) {
				String outRequestNo = SerialNumberUtil.generateCancelCollecTradeBatchNo(i);
				List<CancelAuthTradeArgs> tradeList = Lists.newArrayList();
				List<HostingCollectTradeAuth> records = Lists.newArrayList();
				for (HostingCollectTrade collectTrade : subList.get(i)) {
					// 封装交易参数
					CancelAuthTradeArgs args = new CancelAuthTradeArgs();
					args.setRequestNo(SerialNumberUtil.generateCancelCollecTradeRequestNo(collectTrade.getSourceId()));
					args.setTradeNo(collectTrade.getTradeNo());
					args.setSummary(RemarksEnum.PROJECT_LOSE.getRemarks());
					tradeList.add(args);
					// 封装本地记录参数
					HostingCollectTradeAuth collectAuth = new HostingCollectTradeAuth();
					collectAuth.setTradeNo(args.getTradeNo());
					collectAuth.setTradeRequestNo(args.getRequestNo());
					collectAuth.setBatchRequestNo(outRequestNo);
					collectAuth.setAuthType(TypeEnum.COLLECT_TRADE_CANCEL_TYPE.getType());
					collectAuth.setProjectId(collectTrade.getProjectId());
					records.add(collectAuth);
				}
				hostingCollectTradeAuthManager.batchInsert(records);
				ResultDto<?> sinaRdo = sinaPayClient.cancelPreAuthTrade(outRequestNo, tradeList);
				if (!sinaRdo.isSuccess()) {
					isError = true;
					errorMsg.append("【批次号:" + outRequestNo + "】" + sinaRdo.getErrorMsg());
				}
			}
			if (isError) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM);
				rDO.getResultCode().setMsg(errorMsg.toString());
			}
		} catch (Exception e) {
			logger.error("代收撤销失败 projectId={}", project.getId(), e);
		}
	}
	
	private List<Long> getMobileList(String groupName) throws ManagerException {
		List<Long> mobileList = Lists.newArrayList();
		List<SysDict> sysDictList = sysDictManager.findByGroupName(groupName);
		if (Collections3.isEmpty(sysDictList)) {
			return Lists.newArrayList();
		}
		for (SysDict sysDict : sysDictList) {
			mobileList.add(Long.parseLong(sysDict.getValue()));
		}
		return mobileList;
	}
	/**
	 * 
	 * @desc 逾期结算记录
	 * @param projectId
	 * @param type
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年5月30日 上午10:20:36
	 *
	 */
	@Override
	public List<OverdueRepayLog> getOverdueRepayLogRecordByProjectIdAndType(Long projectId, int type) throws ManagerException {
		try{
			return overdueRepayLogMapper.getOverdueRepayLogRecordByProjectIdAndType(projectId,type);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	//获取滞纳金
	@Override
	public OverdueRepayLog  getLateFeeByProjectId(OverdueRepayLog overdueRepayLog) throws ManagerException {
		OverdueRepayLog overdue =new OverdueRepayLog();
		String key = RedisConstant.REDIS_KEY_TRANSACTION_LATEFEE + RedisConstant.REDIS_SEPERATOR
				+ overdueRepayLog.getId();
		BigDecimal unretrunPrincipal = BigDecimal.ZERO;
		BigDecimal unretrunInterest = BigDecimal.ZERO;
		boolean isExit = RedisManager.isExitByObjectKey(key);
		if (isExit) {
			overdue = (OverdueRepayLog) RedisManager.getObject(key);
		} else {
		Map<String, Object> map=overdueRepayLogManager.getLateFeeByOverdueRepayLog(overdueRepayLog, overdueRepayLog.getLateFeeRate());
		if(!map.isEmpty()){
			if(map.containsKey("overdueRepayLog")){
				overdue=(OverdueRepayLog) map.get("overdueRepayLog");
				if(overdue!=null){
					if(overdue.getUnreturnPrincipal()!=null){
						unretrunPrincipal=overdue.getUnreturnPrincipal();
					}
					if(overdue.getUnreturnInterest()!=null){
						unretrunInterest=overdue.getUnreturnInterest();
					}
					overdue.setUnreturnPrincipalInterest(unretrunPrincipal.add(unretrunInterest));
				}
			}
		}
		RedisManager.putObject(key, overdue);
		RedisManager.expireObject(key, 120);
		}
		 return overdue;
	}
	
	
	public BigDecimal getNoPayAmountByProjectId(OverdueRepayLog overdueRepayLog,BigDecimal overdueFeeRate) throws ManagerException {
		BigDecimal totalLateFee = BigDecimal.ZERO;//总滞纳金
			//逾期天数
			int overdueDays = DateUtils.daysBetween(overdueRepayLog.getOverdueStartDate(), DateUtils.getCurrentDate())+1;
			String overdueId = overdueRepayLog.getOverdueId();
			String[] overdueIds = overdueId.split(",");
			BigDecimal totalUnreturnPrincipal = BigDecimal.ZERO;//总未还本金
			BigDecimal totalBorUnreturnInterest = BigDecimal.ZERO;//总借款人未还利息
			List<TransactionInterestForLateFee> transactionInterests = Lists.newArrayList();
			int overdueIdSize = overdueIds.length;
			for (String period : overdueIds) {
				//根据逾期记录id查询逾期记录
				OverdueLog log = overdueLogMapper.selectByPrimaryKey(Long.valueOf(period));
				if(log==null){
					continue;
				}
				//根据本息id查询交易本息记录
				List<TransactionInterestForLateFee> interestsByPeriod = transactionInterestMapper.getByProjectInterestId(log.getInterestId());
				//未还本金，未还利息总和
				for (TransactionInterestForLateFee interest : interestsByPeriod) {
					//根据交易ID查询对应的未还本金和利息
					//计算对应交易本息的未还本金和利息
					TransactionInterest unreturnPrincipalAndInterest = transactionInterestMapper.selectUnreturnPrincipalAndInterestByTransactionId(interest.getTransactionId());
					/*
					//未还本金
					BigDecimal unReturnPricipal = FormulaUtil.subtractDecimal(interest.getTotalPrincipal(), interest.getReceivedPrincipal());
					//未还利息
					BigDecimal unReturnInterest = FormulaUtil.subtractDecimal(interest.getTotalInterest(), interest.getReceivedInterest());
					//未还额外利息
					BigDecimal unReturnExtraInterest = FormulaUtil.subtractDecimal(interest.getTotalExtraInterest(), interest.getReceivedExtraInterest());
					//未还借款人需支付利息
					BigDecimal borUnreturnInterest = FormulaUtil.subtractDecimal(unReturnInterest,unReturnExtraInterest);*/
					//未还本息总和
					BigDecimal unReturnPrincipalAndInterest = unreturnPrincipalAndInterest.getPayablePrincipal().add(unreturnPrincipalAndInterest.getPayableInterest());
					//每一笔交易本息需要归还的滞纳金
					BigDecimal tInterestLateFee = FormulaUtil.getTransactionInterestLateFeeAmount(unReturnPrincipalAndInterest, overdueFeeRate, overdueDays,overdueIdSize);
					interest.setOverdueFine(tInterestLateFee);
					totalLateFee = totalLateFee.add(tInterestLateFee);
					
					//交易本息  未还本金
					totalUnreturnPrincipal = FormulaUtil.addDecimal(totalUnreturnPrincipal,unreturnPrincipalAndInterest.getPayablePrincipal());
					//交易本息  借款人未还利息
					totalBorUnreturnInterest = FormulaUtil.addDecimal(totalBorUnreturnInterest,unreturnPrincipalAndInterest.getPayableInterest());
				}
				transactionInterests.addAll(interestsByPeriod);
			}
			overdueRepayLog.setUnreturnPrincipal(totalUnreturnPrincipal);//逾期未还本金
			overdueRepayLog.setUnreturnInterest(totalBorUnreturnInterest);//逾期未还利息
			overdueRepayLog.setOverdueDay(overdueDays);//逾期天数
			overdueRepayLog.setOverdueFine(totalLateFee);//滞纳金
			return totalLateFee;
	}
	@Override
	public OverdueRepayLogBiz getOverdueAmountInfoByProjectId(Long projectId) throws ManagerException {
		DebtInterest feeInterest=debtInterestMapper.selectOverdueInterestAmountByProjectId(projectId);
		OverdueRepayLogBiz overdueRepayLogBiz=new OverdueRepayLogBiz();
		int overdueDays=0;
		Project project=selectByPrimaryKey(projectId);
		OverdueRepayLog overdueRepayLog = overdueRepayLogManager.getOverdueRepayByStatus(projectId);
		if(project!=null&&overdueRepayLog!=null){
			overdueRepayLogBiz.setOverdueFeeRate(project.getOverdueFeeRate());
			overdueRepayLogBiz.setLateFeeRate(project.getLateFeeRate());
			overdueRepayLog.setLateFeeRate(project.getLateFeeRate());
		}
		if(feeInterest!=null){
			BigDecimal overduePrincipal = BigDecimal.ZERO;
			BigDecimal overdueInterest = BigDecimal.ZERO;
			BigDecimal principalInterest = BigDecimal.ZERO;
			
			BigDecimal waitPrincipal = BigDecimal.ZERO;
			BigDecimal waitInterest = BigDecimal.ZERO;
			BigDecimal waitPay = BigDecimal.ZERO;
			
			BigDecimal lateFee = BigDecimal.ZERO;
			if(feeInterest.getPayablePrincipal()!=null){
				overduePrincipal=feeInterest.getPayablePrincipal();
			}
			if(feeInterest.getPayableInterest()!=null){
				overdueInterest=feeInterest.getPayableInterest();
			}
			DebtInterest startDate=debtInterestMapper.findOverdueDayByProjectId(projectId);
			if(startDate!=null){
				overdueDays=DateUtils.daysBetween(startDate.getEndDate(), DateUtils.getCurrentDate());
			}
			principalInterest=overduePrincipal.add(overdueInterest);
			//待支付
			DebtInterest waitdebtInterest=debtInterestMapper.selectWaitPayPrincipalByProjectId(projectId);
			if(waitdebtInterest!=null){
				waitPrincipal=waitdebtInterest.getPayablePrincipal();
				waitInterest=waitdebtInterest.getPayableInterest();
				waitPay=waitPrincipal.add(waitInterest);
			}
			//计算滞纳金
			if(overdueRepayLog!=null){
				overdueRepayLog=getLateFeeByProjectId(overdueRepayLog);
				lateFee=overdueRepayLog.getOverdueFine();
			}
			//逾期本金
			overdueRepayLogBiz.setOverduePrincipal(overduePrincipal);
			//逾期利息
			overdueRepayLogBiz.setOverdueInterest(overdueInterest);
			//滞纳金
			overdueRepayLogBiz.setOverdueFine(lateFee);
			//未还本金总额
			overdueRepayLogBiz.setReturnPrincipal(overdueRepayLog.getUnreturnPrincipalInterest());
			//垫资罚息率
			overdueRepayLogBiz.setOverdueFeeRate(project.getOverdueFeeRate());
			//逾期罚息率
			overdueRepayLogBiz.setLateFeeRate(project.getLateFeeRate());
			//合计应还款
			overdueRepayLogBiz.setPayableAmount(principalInterest.add(lateFee).add(waitPay));
			
			overdueRepayLogBiz.setCommonPayableAmount(principalInterest.add(lateFee));
			
			overdueRepayLogBiz.setProjectId(projectId);
			
		}
		//逾期天数
		overdueRepayLogBiz.setOverdueDay(overdueDays);
		return overdueRepayLogBiz;
	}
	/**
	 * 
	 * @Description:从交易表计算滞纳金
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月2日 下午7:40:13
	 */
	public BigDecimal getLateFeeByProjectId1(Long projectId,int days,BigDecimal rate){
		Transaction transaction =new Transaction();
		String key = RedisConstant.REDIS_KEY_TRANSACTION_LATEFEE + RedisConstant.REDIS_SEPERATOR
					+ rate;
		boolean isExit = RedisManager.isExitByObjectKey(key);
		if (isExit) {
			transaction = (Transaction) RedisManager.getObject(key);
		} else {
			transaction  = transactionMapper.totalTransationLateFeeByProjecdtId(projectId);
			RedisManager.putObject(key, transaction);
			RedisManager.expireObject(key, 604800);
		}
		BigDecimal waitTotalAmount = BigDecimal.ZERO;
		if(transaction!=null){
			//总本金
			BigDecimal totalPrincipal = BigDecimal.ZERO;
			//已收取本金
			BigDecimal receivedPrincipal = BigDecimal.ZERO;
			//总利息
			BigDecimal totalInterest = BigDecimal.ZERO;
			//已收取利息
			BigDecimal receivedInterest = BigDecimal.ZERO;
			//收益券增加的年化收益
			BigDecimal totalExtraInterest = BigDecimal.ZERO;
			//已收取的收益券收益
			BigDecimal receivedExtraInterest = BigDecimal.ZERO;
			
			if(transaction.getTotalPrincipal()!=null){
				totalPrincipal=transaction.getTotalPrincipal();
			}
			if(transaction.getReceivedPrincipal()!=null){
				receivedPrincipal=transaction.getReceivedPrincipal();
			}
			if(transaction.getTotalInterest()!=null){
				totalInterest=transaction.getTotalInterest();
			}
			if(transaction.getReceivedInterest()!=null){
				receivedInterest=transaction.getReceivedInterest();
			}
			if(transaction.getTotalExtraInterest()!=null){
				totalExtraInterest=transaction.getTotalExtraInterest();
			}
			if(transaction.getReceivedExtraInterest()!=null){
				receivedExtraInterest=transaction.getReceivedExtraInterest();
			}
			BigDecimal principal = BigDecimal.ZERO;
			BigDecimal interest = BigDecimal.ZERO;
			BigDecimal extraInterest = BigDecimal.ZERO;
			principal=totalPrincipal.subtract(receivedPrincipal);
			interest=totalInterest.subtract(receivedInterest);
			extraInterest=totalExtraInterest.subtract(receivedExtraInterest);
			waitTotalAmount=principal.add(interest).subtract(extraInterest);
		}
		BigDecimal lateFee = BigDecimal.ZERO;
		lateFee=FormulaUtil.getLateFeeAmount(waitTotalAmount,rate,days);
		return lateFee;
	}
	
	/**
	 * @desc 直投项目合同保全线程
	 * @author zhanghao
	 * 2016年7月11日下午2:11:07
	 */
	private class ProcessContractPreservationThread implements Runnable {
		
		private Long projectId;
		

		public ProcessContractPreservationThread(final Long projectId) {
			this.projectId = projectId;
		}

		@Override
		@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
		public void run() {
			logger.info("直投项目改为待放款后，保全线程开始执行，projectId：" + projectId );
			//债权项目签完即保
			TransactionQuery transactionQuery = new TransactionQuery();
			transactionQuery.setProjectId(projectId);
			List<Transaction> transactions = Lists.newArrayList();
			try {
				transactions = transactionManager.selectTransactionsByQueryParams(transactionQuery);
			} catch (ManagerException e) {
				logger.error("直投项目保全线程，获取指定项目的所有交易异常，projectId={}", projectId);
			}
			for(Transaction tran :transactions ){
				if(tran.getSignStatus()==StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus()){
					contractCoreManager.getContractPreservation(tran.getId(), "backend");
				}
			}
			
			logger.info("直投项目改为待放款后，保全线程开始执行结束，projectId：" + projectId);
		}
	}	
 	

	@Override
	public List<Project> getNoviceInvestingProject(Integer isNovice, Integer status) throws Exception {
		return projectMapper.findNoviceInvestingProject(isNovice, status);
	}

	@Override
	public List<ProjectForFront> getRecommendProjectByInvestDay(int number, String investArea ,int type) throws ManagerException {
		try {
			ProjectQuery projectQuery=new ProjectQuery();
			String status="0";
			if(type==1){
				status="1";
			}
			projectQuery.setStatus(status);
			projectQuery.setInvestArea(investArea);
			projectQuery.setNumber(number);
			return projectMapper.getRecommendProjectByInvestDay(projectQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public Page<ProjectForFront> findFrontInvestProjectListByPage(ProjectQuery projectQuery) throws ManagerException {
		try {
			List<ProjectForFront> projectForFrontList = Lists.newArrayList();
			long count = projectMapper.selectForPaginTotalCountFrontInvest(projectQuery);
			if (count > 0) {
				projectForFrontList = projectMapper.selectForPaginFrontInvest(projectQuery);
			}
			Page<ProjectForFront> page = new Page<ProjectForFront>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(projectQuery.getCurrentPage());
			page.setiDisplayLength(projectQuery.getPageSize());
			page.setiTotalRecords(count);
			page.setData(projectForFrontList);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 直投项目抽奖
	 * @param projectId
	 * @param memberId
	 * @return
	 * @author chaisen
	 * @throws Exception 
	 * @time 2016年10月31日 下午1:53:37
	 *
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Object> directProjectLottery(Long projectId, Long memberId, int type) throws Exception{
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			if(projectId==0||type==0){
				result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return result;
			}
			//项目是否存在
			Project project=projectMapper.selectByPrimaryKey(projectId);
			if(project==null){
				result.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
				return result;
			}
			if(project.getStatus()==StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()||project.getStatus()==StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()||
					project.getStatus()==StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()){
				result.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_HADSEND_ERROR);
				return result;
			}
			if(project.getStatus()==StatusEnum.PROJECT_STATUS_LOSE.getStatus()){
				result.setResultCode(ResultCode.DIRECT_LOTTERY_LOSE_NOCHANCE_ERROR);
				return result;
			}
			//校验抽奖次数
			ActivityLottery lottery =new ActivityLottery();
			lottery.setMemberId(memberId);
			lottery.setCycleConstraint(projectId.toString());
			ProjectExtra pe = projectExtraMapper.getProjectActivitySign(projectId);
			if(pe==null){
				result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return result;
			}
			Optional<Activity> directActivity = LotteryContainer.getInstance().getActivity(pe.getActivityId());
			if(!directActivity.isPresent()){
				result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return result;
			}
			lottery.setActivityId(pe.getActivityId());
			ActivityLottery activityLottery=activityLotteryMapper.checkExistLottery(lottery);
			if(activityLottery==null||activityLottery.getRealCount()<1){
				result.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NUM_ERROR);
				return result;
			}
			//查询是否有未抽奖的记录
			ActivityLotteryResult model=new ActivityLotteryResult();
			model.setActivityId(pe.getActivityId());
			model.setMemberId(memberId);
			//model.setRewardType(5);
			model.setRemark(projectId.toString());
			model.setLotteryStatus(StatusEnum.LOTTERY_STATUS_NO.getStatus());
			List<ActivityLotteryResult> listActivityResult=activityLotteryResultMapper.getLotteryResultBySelectiveAndLotteryStatus(model);
			if(Collections3.isEmpty(listActivityResult)){
				result.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NUM_ERROR);
				return result;
			}
			//ActivityLotteryResult lotteryResult=listActivityResult.get(0);
			//查询项目所有的记录
			ActivityLotteryResult allmodel=new ActivityLotteryResult();
			allmodel.setActivityId(pe.getActivityId());
			allmodel.setRemark(projectId.toString());
			allmodel.setRewardType(5);
			List<ActivityLotteryResult> allListActivityResult=activityLotteryResultMapper.getActivityLotteryResultByProject(allmodel);
			if(Collections3.isEmpty(allListActivityResult)){
				result.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_RECORD_ERROR);
				return result;
			}
			
			ProjectForLotteryReturn rewardLottery=new ProjectForLotteryReturn();
			String rewardInfo="";
			ActivityMemberLottery memberLottery=getUserLotteryResult(allListActivityResult,listActivityResult,type);
			
			//更新抽奖次数
			ActivityLottery lotter=new ActivityLottery();
			lotter.setActivityId(pe.getActivityId());
			lotter.setMemberId(memberId);
			lotter.setCycleConstraint(projectId.toString());
			if(type==2){
				//校验抽奖次数
				lotter.setRealCount(-listActivityResult.size());
			}else{
				lotter.setRealCount(-1);
			}
			activityLotteryMapper.updateByActivityAndMember(lotter);
			
			//获取奖项
			List<LotteryRuleAmountNumber> instance = JSON.parseArray(pe.getExtraInformation(), LotteryRuleAmountNumber.class);
			String resultId="";
			List<DirectLotteryResultList> directLotteryResultList = Lists.newArrayList();
			for(int i=0;i<memberLottery.getLotteryId().length;i++){
				resultId=memberLottery.getResultId()[i];
				if(StringUtils.isBlank(resultId)){
					continue;
				}
					//更新抽奖状态
					ActivityLotteryResult aclotteryResult=new ActivityLotteryResult();
					aclotteryResult.setId(Long.parseLong(resultId));
					//aclotteryResult.setRewardInfo("未抽中");
					Boolean flag = false;
					for(LotteryRuleAmountNumber reward:instance){
						if(reward.getNumber()==Integer.parseInt(memberLottery.getLotteryId()[i])){
							flag = true;//单次是否中奖
							
							int prize=reward.getPrize();
							aclotteryResult.setChip(prize);//几等奖
							aclotteryResult.setRewardInfo(this.getRewardAmount(project, pe, prize).toString());//奖励金额
							aclotteryResult.setStatus(1);//抽奖次数已领取，且抽奖
							aclotteryResult.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);//中奖标识
							activityLotteryResultMapper.updateByPrimaryKeySelective(aclotteryResult);
							
							DirectLotteryResultList directLotteryResult = new DirectLotteryResultList();
							directLotteryResult.setPrize(aclotteryResult.getChip().toString());
							directLotteryResult.setRewardAmount(aclotteryResult.getRewardInfo());
							directLotteryResultList.add(directLotteryResult);
							
							ActivityLotteryResult record = new ActivityLotteryResult();
							// 记录奖品结果
							record.setActivityId(pe.getActivityId());
							record.setMemberId(memberId);
							record.setCycleStr(project.getId().toString());
							record.setRewardId(project.getId().toString());
							record.setChip(Integer.valueOf(aclotteryResult.getRewardInfo()));
							record.setRewardResult(aclotteryResult.getRewardInfo());
							record.setRewardInfo(aclotteryResult.getRewardInfo()+"元现金");
							record.setRewardType(7);//7类型为现金
							record.setStatus(0);//奖金未发送，放款时发放现金
							record.setRemark("快投奖励发放");
							logger.info("快投奖励中奖结果入库, activityId={}, memberId={}, cycleStr={}", record.getActivityId(), record.getMemberId(),
									record.getCycleStr());
							activityLotteryResultMapper.insertSelective(record);
							
							if(StringUtils.isNotBlank(rewardInfo)){
								rewardInfo = rewardInfo + ","+prize;
							}else{
								rewardInfo = prize+"";
							}
							break;
						}
					}
					if(!flag){
						//普通次数未中奖
						this.quickRewardPopularity(aclotteryResult, projectId,memberId,pe.getActivityId(),memberLottery);
					}
			}
			if(Collections3.isNotEmpty(directLotteryResultList)){
				rewardLottery.setLottery(true);
			}else{
				rewardLottery.setLottery(false);
			}
			rewardLottery.setDirectLotteryResultList(directLotteryResultList);
			rewardLottery.setRewardInfo(rewardInfo);
			rewardLottery.setPopularity(memberLottery.getUnLottery().toString());
			result.setResult(rewardLottery);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("直投项目抽奖, memberId={},projectId={}", memberId,projectId, e);
			throw e;
		}
		return result;
	}
	
	//获取项目几等奖金额
	private BigDecimal getRewardAmount(Project project,ProjectExtra pe,Integer prize){
		
		BigDecimal rewardAmount = BigDecimal.ZERO;
		BigDecimal totalRewardAmount = pe.getExtraAmount();//奖池金额
		
		//获取奖励系数
		List<PrizeInPool> prizeInPool = projectExtraManager.getPrizeInPoolByProjectId(project.getId()); 
		
		//各等级占比例
		for(PrizeInPool priInPool : prizeInPool){
			if(priInPool.getLevel()==prize){
				rewardAmount = totalRewardAmount.multiply(new BigDecimal(priInPool.getProportion()))
				.setScale(0,BigDecimal.ROUND_HALF_UP);
			}
		}
		return rewardAmount;
	}
	
	
	public ResultDO<Object> directProjectLotteryByTransactionId(Long transactionId, Long memberId) throws Exception{
		return this.directProjectLotteryByTransactionIdType(transactionId, memberId, 1);
	}
	
	/**
	 * 
	 * @desc 直投项目抽奖
	 * @param transactionId
	 * @param memberId
	 * @return
	 * @author chaisen
	 * @throws Exception 
	 * @time 2016年10月31日 下午1:53:37
	 *
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Object> directProjectLotteryByTransactionIdType(Long transactionId, Long memberId,int type) throws Exception{
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			if(transactionId==0||memberId==0){
				result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return result;
			}
			//交易是否存在
			Transaction tra = transactionManager.selectTransactionById(transactionId);
			if(tra==null){
				result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return result;
			}
			if (memberId.compareTo(tra.getMemberId()) != 0) {
				result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
				return result;
			}
			Project project=projectMapper.selectByPrimaryKey(tra.getProjectId());
			if(project==null){
				result.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
				return result;
			}
			if(project.getStatus()==StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()||project.getStatus()==StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()||
					project.getStatus()==StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()){
				result.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_HADSEND_ERROR);
				return result;
			}
			if(project.getStatus()==StatusEnum.PROJECT_STATUS_LOSE.getStatus()){
				result.setResultCode(ResultCode.DIRECT_LOTTERY_LOSE_NOCHANCE_ERROR);
				return result;
			}
			if(tra.getStatus()!=StatusEnum.TRANSACTION_INVESTMENTING.getStatus()){
				result.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_HADSEND_ERROR);
				return result;
			}
			
			//校验抽奖次数
			ActivityLottery lottery =new ActivityLottery();
			lottery.setMemberId(memberId);
			lottery.setCycleConstraint(tra.getProjectId().toString());
			ProjectExtra pe = projectExtraMapper.getProjectActivitySign(tra.getProjectId());
			if(pe==null){
				result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return result;
			}
			Optional<Activity> directActivity = LotteryContainer.getInstance().getActivity(pe.getActivityId());
			if(!directActivity.isPresent()){
				result.setResultCode(ResultCode.ACTIVITY_NOT_EXSIST_ERROR);
				return result;
			}
			lottery.setActivityId(pe.getActivityId());
			ActivityLottery activityLottery=activityLotteryMapper.checkExistLottery(lottery);
			if(activityLottery==null||activityLottery.getRealCount()<1){
				result.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NUM_ERROR);
				return result;
			}
			//查询是否有未抽奖的记录
			ActivityLotteryResult model=new ActivityLotteryResult();
			model.setActivityId(pe.getActivityId());
			model.setMemberId(memberId);
			//model.setRewardType(5);
			model.setRemark(tra.getProjectId().toString());
			model.setRewardId(tra.getId().toString());
			model.setLotteryStatus(StatusEnum.LOTTERY_STATUS_NO.getStatus());
			List<ActivityLotteryResult> listActivityResult=activityLotteryResultMapper.getLotteryResultBySelectiveAndLotteryStatus(model);
			if(Collections3.isEmpty(listActivityResult)){
				result.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NUM_ERROR);
				return result;
			}
			//ActivityLotteryResult lotteryResult=listActivityResult.get(0);
			//查询项目所有的记录
			ActivityLotteryResult allmodel=new ActivityLotteryResult();
			allmodel.setActivityId(pe.getActivityId());
			allmodel.setRemark(tra.getProjectId().toString());
			allmodel.setRewardType(5);
			List<ActivityLotteryResult> allListActivityResult=activityLotteryResultMapper.getActivityLotteryResultByProject(allmodel);
			if(Collections3.isEmpty(allListActivityResult)){
				result.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_RECORD_ERROR);
				return result;
			}
			ProjectForLotteryReturn rewardLottery=new ProjectForLotteryReturn();
			String rewardInfo="";
			ActivityMemberLottery memberLottery=getUserLotteryResult(allListActivityResult,listActivityResult,type);//1:抽一次  2：抽两次
			
			//更新抽奖次数
			ActivityLottery lotter=new ActivityLottery();
			lotter.setActivityId(pe.getActivityId());
			lotter.setMemberId(memberId);
			lotter.setCycleConstraint(tra.getProjectId().toString());
			if(type==2){
				//校验抽奖次数
				lotter.setRealCount(-listActivityResult.size());
			}else{
				lotter.setRealCount(-1);
			}
			activityLotteryMapper.updateByActivityAndMember(lotter);
			
			//获取奖项
			List<LotteryRuleAmountNumber> instance = JSON.parseArray(pe.getExtraInformation(), LotteryRuleAmountNumber.class);
			String resultId="";
			List<DirectLotteryResultList> directLotteryResultList = Lists.newArrayList();
			for(int i=0;i<memberLottery.getLotteryId().length;i++){
				resultId=memberLottery.getResultId()[i];
				if(StringUtils.isBlank(resultId)){
					continue;
				}
					//更新抽奖状态
					ActivityLotteryResult aclotteryResult=new ActivityLotteryResult();
					aclotteryResult.setId(Long.parseLong(resultId));
					//aclotteryResult.setRewardInfo("未抽中");
					Boolean flag = false;
					for(LotteryRuleAmountNumber reward:instance){
						if(reward.getNumber()==Integer.parseInt(memberLottery.getLotteryId()[i])){
							flag = true;//单次是否中奖
							
							int prize=reward.getPrize();
							aclotteryResult.setChip(prize);//几等奖
							aclotteryResult.setRewardInfo(this.getRewardAmount(project, pe, prize).toString());//奖励金额
							aclotteryResult.setStatus(1);//抽奖次数已领取，且抽奖
							aclotteryResult.setRewardResult(ActivityConstant.DIRECT_WINNER_LOTTERY_KEY);//中奖标识
							activityLotteryResultMapper.updateByPrimaryKeySelective(aclotteryResult);
							
							DirectLotteryResultList directLotteryResult = new DirectLotteryResultList();
							directLotteryResult.setPrize(aclotteryResult.getChip().toString());
							directLotteryResult.setRewardAmount(aclotteryResult.getRewardInfo());
							directLotteryResultList.add(directLotteryResult);
							
							ActivityLotteryResult record = new ActivityLotteryResult();
							// 记录奖品结果
							record.setActivityId(pe.getActivityId());
							record.setMemberId(memberId);
							record.setCycleStr(project.getId().toString());
							record.setRewardId(project.getId().toString());
							record.setChip(Integer.valueOf(aclotteryResult.getRewardInfo()));
							record.setRewardResult(aclotteryResult.getRewardInfo());
							record.setRewardInfo(aclotteryResult.getRewardInfo()+"元现金");
							record.setRewardType(7);//7类型为现金
							record.setStatus(0);//奖金未发送，放款时发放现金
							record.setRemark("快投奖励发放");
							logger.info("快投奖励中奖结果入库, activityId={}, memberId={}, cycleStr={}", record.getActivityId(), record.getMemberId(),
									record.getCycleStr());
							activityLotteryResultMapper.insertSelective(record);
							
							break;
						}
					}
					if(!flag){
						//普通次数未中奖
						this.quickRewardPopularity(aclotteryResult, tra.getProjectId(),memberId,pe.getActivityId(),memberLottery);
					}
			}
			
			if(Collections3.isNotEmpty(directLotteryResultList)){
				rewardLottery.setLottery(true);
			}else{
				rewardLottery.setLottery(false);
			}
			rewardLottery.setDirectLotteryResultList(directLotteryResultList);
			rewardLottery.setRewardInfo(rewardInfo);
			rewardLottery.setPopularity(memberLottery.getUnLottery().toString());
			result.setResult(rewardLottery);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("直投项目根据交易Id抽奖, memberId={},transactionId={}", memberId,transactionId, e);
			throw e;
		}
		return result;
	}

	
	private void quickRewardPopularity(ActivityLotteryResult aclotteryResult,Long projectId,Long memberId,Long activityId,ActivityMemberLottery lottery) throws ManagerException{
		
		
		QuickRewardConfig quickRewardConfig = projectExtraManager.getQuickRewardConfig(projectId);
		//是否开启
		if(!quickRewardConfig.isFlag()){
			return;
		}
		//不在补偿活动时间内
		if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), quickRewardConfig.getStartDate(), quickRewardConfig.getEndDate())){
			return;
		}
		// 人气值
		aclotteryResult.setRewardInfo(quickRewardConfig.getPopularity());//补偿人气值
		aclotteryResult.setStatus(1);//已领取
		aclotteryResult.setRewardResult(ActivityConstant.DIRECT_LOSER_LOTTERY_KEY);//补偿人气值标识
		activityLotteryResultMapper.updateByPrimaryKeySelective(aclotteryResult);
		lottery.setUnLottery(lottery.getUnLottery()+Integer.valueOf(quickRewardConfig.getPopularity()));
		//发人气值
		Balance balance = balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY,new BigDecimal(quickRewardConfig.getPopularity()), memberId);
		// 记录人气值资金流水
		popularityInOutLogManager.insert(memberId, TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, new BigDecimal(quickRewardConfig.getPopularity()), null,
				balance.getAvailableBalance(), projectId, "快投有奖福利");
		balanceManager.incrGivePlatformTotalPoint(new BigDecimal(quickRewardConfig.getPopularity()));
		
		ActivityLotteryResult record = new ActivityLotteryResult();
		// 记录奖品结果
		record.setActivityId(activityId);
		record.setMemberId(memberId);
		record.setCycleStr(projectId.toString());
		record.setRewardId(projectId.toString());
		record.setChip(Integer.valueOf(quickRewardConfig.getPopularity()));
		record.setRewardResult(quickRewardConfig.getPopularity());
		record.setRewardInfo(quickRewardConfig.getPopularity()+"点人气值");
		record.setRewardType(1);//1类型为人气值
		record.setStatus(1);//人气值直接发放
		record.setRemark("快投奖励发放");
		logger.info("快投奖励 人气值补偿记录入库, activityId={}, memberId={}, cycleStr={}", record.getActivityId(), record.getMemberId(),
				record.getCycleStr());
		activityLotteryResultMapper.insertSelective(record);
		
	}

	
	private ActivityMemberLottery getUserLotteryResult(
			List<ActivityLotteryResult> allListActivityResult,
			List<ActivityLotteryResult> listActivityResult, int type) throws NumberFormatException, ManagerException {
		ActivityMemberLottery lottery = new ActivityMemberLottery();
		lottery.setUnLottery(0);//未中奖 补偿人气值
		String lotteryId = "";
		String resultId = "";
		
			if (type == 1) {

				ActivityLotteryResult singleLottery = listActivityResult.get(0);
				singleLottery.setLotteryStatus(StatusEnum.LOTTERY_STATUS_YES
						.getStatus());
				activityLotteryResultMapper
						.updateByPrimaryKeySelective(singleLottery);
				
				if(singleLottery.getRewardType()==6){//额外次数直接失败
					this.quickRewardPopularity(singleLottery, Long.valueOf(singleLottery.getRemark())
							,singleLottery.getMemberId(),singleLottery.getActivityId(), lottery);
				}
				for (ActivityLotteryResult allbiz : allListActivityResult) {
					if (allbiz.getId().equals(singleLottery.getId())) {
						lotteryId = allbiz.getSort().toString();
						resultId = singleLottery.getId().toString();
						break;
					}
				}
			} else {
					for (ActivityLotteryResult memberbiz : listActivityResult) {
	
						memberbiz.setLotteryStatus(StatusEnum.LOTTERY_STATUS_YES
								.getStatus());
						activityLotteryResultMapper
								.updateByPrimaryKeySelective(memberbiz);
	
						if(memberbiz.getRewardType()==6){//额外次数直接失败
							this.quickRewardPopularity(memberbiz, Long.valueOf(memberbiz.getRemark())
									,memberbiz.getMemberId(),memberbiz.getActivityId(),lottery);
						}
						for (ActivityLotteryResult allbiz : allListActivityResult) {
							if (allbiz.getId().equals(memberbiz.getId())) {
								String templotteryId = allbiz.getSort().toString();
								lotteryId += templotteryId + ",";
								String tempresultId = memberbiz.getId().toString();
								resultId += tempresultId + ",";
							}
						}
					}
	
			}
		String lotterArry[] = lotteryId.split(",");
		String resultArry[] = resultId.split(",");
		lottery.setLotteryId(lotterArry);
		lottery.setResultId(resultArry);
		return lottery;
	}

	@Override
	public List<ProjectForFront> findQuickInvestRecommendProject() throws ManagerException {
		try{
			return projectMapper.findQuickInvestRecommendProject();
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectForFront> findQuickInvestLotteryProject(ActivityLotteryResult modelResult) throws ManagerException {
		try{
			return projectMapper.findQuickInvestLotteryProject(modelResult);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int countLotteryResultByProjectId(ActivityLotteryResult modelResult) throws ManagerException {
		try{
			return projectMapper.countLotteryResultByProjectId(modelResult);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectForDirectLottery getDirectLotteryDetailByProjectId(Long projectId) {
		
		ProjectForDirectLottery model=new ProjectForDirectLottery();
		Project project=projectMapper.selectByPrimaryKey(projectId);
		if(project==null){
			return model;
		}
		model.setStatus(project.getStatus());
		ProjectExtra pe = projectExtraMapper.getProjectActivitySign(projectId);
		if(pe==null){
			return model;
		}
		//奖池金额
		BigDecimal ratioAmount=pe.getExtraAmount();
		
		//奖池天数系数变化规则
		List<PrizePool> prizePoolList =projectExtraManager.getPrizePoolByProjectId(projectId);
		if(Collections3.isEmpty(prizePoolList)){
			//快2项目属性
			model.setIsQuick2Project(true);
			List<PrizeInPool> prizeInPoolList =projectExtraManager.getPrizeInPoolByProjectId(projectId);
			List<ProjectForLevel> projectForLevel=Lists.newArrayList();
			for(PrizeInPool inPool:prizeInPoolList){
				ProjectForLevel level=new ProjectForLevel();
				level.setLevel(inPool.getLevel());
				level.setNumber(inPool.getNum());
//				level.setRadio(radio);
				level.setReward(ratioAmount.multiply(new BigDecimal(inPool.getProportion())).intValue()+"");
				projectForLevel.add(level);
			}
			model.setProjectForLevel(projectForLevel);
			
			//项目下会员中奖信息  
			ActivityLotteryResult record=new ActivityLotteryResult();
			record.setActivityId(pe.getActivityId());
			record.setLotteryStatus(1);
			record.setRemark(projectId.toString());
			record.setRewardResult("winnerLottery");
			List<ProjectForRewardMember> projectRewardList=activityLotteryResultMapper.findProjectRewardByProjectId(record);
			for(ProjectForRewardMember biz:projectRewardList){
				record.setMemberId(biz.getMemberId());
				List<ProjectForLevel> projectForLevelDetail=activityLotteryResultMapper.getRewardLevelByProjectId(record);
				biz.setProjectForLevelDetail(projectForLevelDetail);
			}
			model.setProjectRwardMember(projectRewardList);
			
			return model;
		}
		//奖池变化map<第几天天数，奖池系数>
		Map<String, Object> poolMap = new HashMap<String, Object>();
		for(PrizePool pool:prizePoolList){
			poolMap.put(pool.getDay()+"day", pool.getRatio());
		}
		model.setPrizePoolList(prizePoolList);
		
		//当前活动最大天数
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
		}
		//int maxDay=prizePoolList.get(prizePoolList.size()-1).getDay();
		
		String ratio="";//履约中 最后一天奖池比例
		
		//有奖励的天数
		int day=0;
		for(PrizePool pool:prizePoolList){
			if(pool.getRatio().equals("0")){
				day=pool.getDay()-1;
				break;
			}
		}
		
		
		//流标
		if(project.getStatus()==StatusEnum.PROJECT_STATUS_LOSE.getStatus()){
			model.setProjectLotteryJson("项目流标");
		
		//履约中
		}else if(StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()==project.getStatus()
				||StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()==project.getStatus()||StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()==project.getStatus()){
			//销售天数
			int totalDays=DateUtils.daysByCalendar(project.getOnlineTime(),project.getSaleComplatedTime());
			//奖励截至时间
			model.setEndDate(DateUtils.addDate(project.getOnlineTime(), day-1));
			//有奖励的天数
			model.setDay(DateUtils.getIntervalDays(project.getOnlineTime(),model.getEndDate())+1);
			
			//奖项信息
			List<PrizeInPool> prizeInPoolList =projectExtraManager.getPrizeInPoolByProjectId(projectId);
			if(maxDay<totalDays){//活动天数小于销售天数 正常
				model.setHasReward(false);
				//一等奖，二等奖各多少名
				List<ProjectForLevel> projectForLevel=Lists.newArrayList();
				for(PrizeInPool inPool:prizeInPoolList){
					ProjectForLevel level=new ProjectForLevel();
					level.setLevel(inPool.getLevel());
					projectForLevel.add(level);
				}
				//model.setPrizeInPoolList(prizeInPoolList);
				model.setProjectForLevel(projectForLevel);
			}else{
				model.setHasReward(true);
				model.setSaleComplatedTime(project.getSaleComplatedTime());
				//募集天数
				model.setTotalDays(totalDays);
				//最后一天奖金池百分比
				model.setProportion(new BigDecimal(poolMap.get(totalDays+"day").toString()).multiply( new BigDecimal(100)).intValue()+"");
				for(PrizePool pool:prizePoolList){
					if(totalDays==pool.getDay()){
						ratio=pool.getRatio();
						break;
					}
				}
				if(ratio==null){
					return model;
				}
				if(pe.getExtraAmount()!=null&&StringUtils.isNotBlank(ratio)){
					 ratioAmount=pe.getExtraAmount().multiply(new BigDecimal(ratio));
				}
				model.setRatioAmount(ratioAmount);
				model.setPrizeAmount(ratioAmount.intValue());
				//中奖记录   各等奖对应的数量和金额
				ActivityLotteryResult record=new ActivityLotteryResult();
				record.setActivityId(pe.getActivityId());
				record.setLotteryStatus(1);
				record.setRemark(projectId.toString());
				record.setRewardResult("winnerLottery");
				
				//List<ProjectForLevel> projectForLevel=activityLotteryResultMapper.getRewardInfoLevelByProjectId(record);
				//model.setProjectForLevel(projectForLevel);
				
				//一等奖，二等奖各多少名
				List<ProjectForLevel> projectForLevel=Lists.newArrayList();
				for(PrizeInPool inPool:prizeInPoolList){
					ProjectForLevel level=new ProjectForLevel();
					level.setLevel(inPool.getLevel());
					level.setNumber(inPool.getNum());
					level.setReward(ratioAmount.multiply(new BigDecimal(inPool.getProportion())).intValue()+"");
					level.setRadio(new BigDecimal(100).multiply(new BigDecimal(ratio)).intValue()+"");
					projectForLevel.add(level);
				}
				//model.setPrizeInPoolList(prizeInPoolList);
				model.setProjectForLevel(projectForLevel);
				//项目下会员中奖信息  
				List<ProjectForRewardMember> projectRewardList=activityLotteryResultMapper.findProjectRewardByProjectId(record);
				for(ProjectForRewardMember biz:projectRewardList){
					record.setMemberId(biz.getMemberId());
					List<ProjectForLevel> projectForLevelDetail=activityLotteryResultMapper.getRewardLevelByProjectId(record);
					biz.setProjectForLevelDetail(projectForLevelDetail);
				}
				model.setProjectRwardMember(projectRewardList);
			}
			
		//募集中
		}else{
			//当前第几天
			int totalDays=DateUtils.getIntervalDays(project.getOnlineTime(),DateUtils.getCurrentDate())+1;
			if(StatusEnum.PROJECT_STATUS_FULL.getStatus()==project.getStatus()){
				totalDays=DateUtils.getIntervalDays(project.getOnlineTime(),project.getSaleComplatedTime())+1;
			}
			model.setTotalDays(totalDays);
			model.setEndDate(DateUtils.addDate(project.getOnlineTime(), day-1));
			model.setDay(DateUtils.getIntervalDays(project.getOnlineTime(),model.getEndDate())+1);
			List<PrizeInPool> prizeInPoolList =projectExtraManager.getPrizeInPoolByProjectId(projectId);
			//如果超出奖励期限
			if(maxDay<totalDays){
				model.setHasReward(false);
				//一等奖，二等奖各多少名
				List<ProjectForLevel> projectForLevel=Lists.newArrayList();
				for(PrizeInPool inPool:prizeInPoolList){
					ProjectForLevel level=new ProjectForLevel();
					level.setLevel(inPool.getLevel());
					projectForLevel.add(level);
				}
				model.setProjectForLevel(projectForLevel);
			}else{
				model.setProportion(new BigDecimal(poolMap.get(totalDays+"day").toString()).multiply( new BigDecimal(100)).intValue()+"");
				model.setHasReward(true);
				
				//计算当前奖金池金额
				for(PrizePool pool:prizePoolList){
					if(totalDays==pool.getDay()){
						ratio=pool.getRatio();
						break;
					}
				}
				if(pe.getExtraAmount()!=null&&StringUtils.isNotBlank(ratio)){
					 ratioAmount=pe.getExtraAmount().multiply(new BigDecimal(ratio));
				}
				
				//当前各等级奖项的数据
				List<ProjectForLevel> projectForLevel=Lists.newArrayList();
				for(PrizeInPool inPool:prizeInPoolList){
					ProjectForLevel level=new ProjectForLevel();
					level.setLevel(inPool.getLevel());
					level.setNumber(inPool.getNum());
					level.setReward(ratioAmount.multiply(new BigDecimal(inPool.getProportion())).intValue()+"");
					level.setRadio(new BigDecimal(100).multiply(new BigDecimal(ratio)).intValue()+"");
					projectForLevel.add(level);
				}
				//model.setPrizeInPoolList(prizeInPoolList);
				model.setProjectForLevel(projectForLevel);
			}
	
		}	
		//奖励详情弹框
		List<ProjectForRewardDetail> listRewardDetail=Lists.newArrayList();
			List<PrizeInPool> prizeInPoolList=projectExtraManager.getPrizeInPoolByProjectId(projectId);
			//各等级占比例<几等奖，奖项占奖金池比例>
			Map<String, Object> paraMap = new HashMap<String, Object>();
			for(PrizeInPool pool:prizeInPoolList){
				paraMap.put(pool.getLevel()+"level", pool.getProportion().toString());
			}
			
			//各等级个数<几等奖，数量>
			Map<String, Object> paraMapNumber = new HashMap<String, Object>();
			for(PrizeInPool pool:prizeInPoolList){
				paraMapNumber.put(pool.getLevel()+"level", pool.getNum());
			}
			//每天的奖项金额详情
			for(int i=0;i<=prizePoolList.size()-1;i++){
				ProjectForRewardDetail detail=new ProjectForRewardDetail();
				detail.setTime(DateUtils.addDate(project.getOnlineTime(),i));
				detail.setDay(i+1);
				//第i天的比例系数
				String radio=prizePoolList.get(i).getRatio();
				if(radio.equals("0")){
					break;
				}
				detail.setProportion(new BigDecimal(radio).multiply(new BigDecimal(100)).intValue()+"");
				//第i天的奖金总数
				BigDecimal ratioTotalAmount=pe.getExtraAmount().multiply(new BigDecimal(radio));
				if(paraMap.containsKey("1level")){
					BigDecimal level1Amount=ratioTotalAmount.multiply(new BigDecimal(paraMap.get("1level").toString()));
					detail.setLevel1Amount(level1Amount);
				}
				
				if(paraMap.containsKey("2level")){
					BigDecimal level2Amount=ratioTotalAmount.multiply(new BigDecimal(paraMap.get("2level").toString()));
					detail.setLevel2Amount(level2Amount);
				}
				
				if(paraMap.containsKey("3level")){
					BigDecimal level3Amount=ratioTotalAmount.multiply(new BigDecimal(paraMap.get("3level").toString()));
					detail.setLevel3Amount(level3Amount);
				}
				
				if(paraMap.containsKey("4level")){
					BigDecimal level4Amount=ratioTotalAmount.multiply(new BigDecimal(paraMap.get("4level").toString()));
					detail.setLevel4Amount(level4Amount);
				}
				
				if(paraMap.containsKey("5level")){
					BigDecimal level5Amount=ratioTotalAmount.multiply(new BigDecimal(paraMap.get("5level").toString()));
					detail.setLevel5Amount(level5Amount);
				}

				if(paraMap.containsKey("6level")){
					BigDecimal level6Amount=ratioTotalAmount.multiply(new BigDecimal(paraMap.get("6level").toString()));
					detail.setLevel6Amount(level6Amount);
				}
				
				listRewardDetail.add(detail);
				
			}
			model.setListRewardDetail(listRewardDetail);
			List<ProjectForLevel> levelList = model.getProjectForLevel();
			if(levelList!=null){
				for(ProjectForLevel level:levelList){
					if(level.getLevel()==1){
						model.setMaxReward(level.getReward());
					}
				}
			}
		return model;
	}



	@Override
	public List<AfterHandlePreAuthTradeBiz> queryAfterHandlePreAuthTrade() {
		return projectMapper.queryAfterHandlePreAuthTrade();
	}
	
	@Override
	public BigDecimal invertExtraInterest(Project project, BigDecimal investAmount, Date date, BigDecimal extraAnnualizedRate) {
		BigDecimal result = BigDecimal.ZERO;
		extraAnnualizedRate =  extraAnnualizedRate ==null ? BigDecimal.ZERO  :extraAnnualizedRate;
		Integer interestFrom = project.getInterestFrom();
		//借款周期
		Integer borrowPeriod = project.getBorrowPeriod();
		//借款周期类型（1-日；2-月；3-年）
		Integer borrowPeriodType = project.getBorrowPeriodType();
		//年化利率
		//BigDecimal annualizedRate = this.getProjectAnnulizedRate(project);
		// 加上收益劵
		BigDecimal annualizedRate = extraAnnualizedRate;
		//开始计息日
		Date beginInterestDate = DateUtils.addDate(date, interestFrom);
		//按日计息，按月付息，到期还本
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType()) ||
				DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
			//借款周期类型天
			int borrowDays = 0;
			if (borrowPeriodType == 1) {
				borrowDays = borrowPeriod;
			}
			//借款周期类型月
			if (borrowPeriodType == 2) {
				Date endDate = DateUtils.addMonth(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				Date endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			result = FormulaUtil.calculateInterest(investAmount, annualizedRate, borrowDays);
			return result;
		}
		//等本等息 (按月)   不支持 借款周期为天的
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())) {
			int months = 0;
			//借款周期类型月
			if (borrowPeriodType == 2) {
				months = borrowPeriod;
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				months = borrowPeriod * 12;
			}
			result = FormulaUtil.calculateInterestAndMonthByAvg(investAmount, annualizedRate, months);
			return result;
		}
		
		//等本等息按周还款
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())) {
			if (borrowPeriodType != 4) {
				return result;
			}
			result = FormulaUtil.calculateTotalInterestByWeek(investAmount, annualizedRate, borrowPeriod);
			return result;
		}
		
		//按日计息，按季付息，到期还本
		if (DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())) {
			//借款周期类型天
			int borrowDays = 0;
			if (borrowPeriodType == 1) {
				borrowDays = borrowPeriod;
			}
			//借款周期类型月
			if (borrowPeriodType == 2) {
				Date endDate = DateUtils.addMonth(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			//借款周期类型年
			if (borrowPeriodType == 3) {
				Date endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
				borrowDays = DateUtils.getIntervalDays(beginInterestDate, endDate);
			}
			
			result = FormulaUtil.calculateInterestByAvgSeason(investAmount, annualizedRate, borrowDays);
			return result;
		}
				
		return result;
	}

	@Override
	public Long getRecommendQuickProjectId() throws ManagerException {
		try {
			return projectMapper.getRecommendQuickProjectId();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectForFront getRecommendQuickProject(Long projectId) throws ManagerException {
		try {
			return projectMapper.getRecommendQuickProject(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	/***
	 *   计算项目回款信息-项目信息展示数据，根据项目和优惠券年华计算
	 * @param project
	 * @param date  起息时间
	 * @param 优惠券年化
	 * @return
	 */
	@Override
	public List<DebtInterest> calculateInterestForProjectDetail(Project project,Date date,BigDecimal couponAnnualized,Long investAmount) {
		List<DebtInterest>  debtInterests = Lists.newArrayList();
		//起息日
		Integer interestFrom = project.getInterestFrom();
		//借款周期
		Integer borrowPeriod = project.getBorrowPeriod();
		//借款周期类型（1-日；2-月；3-年）
		Integer borrowPeriodType = project.getBorrowPeriodType();
		//年化利率
		BigDecimal annualizedRate = project.getAnnualizedRate();
		annualizedRate = annualizedRate.add(couponAnnualized);
		//项目总投资额
		BigDecimal totalAmount = BigDecimal.valueOf(investAmount);
		//开始计息日
		Date beginInterestDate = DateUtils.addDate(date , interestFrom);
		//单位利息
		BigDecimal unitInterest = FormulaUtil.getUnitInterest(DebtEnum.RETURN_TYPE_DAY.getCode(), totalAmount,annualizedRate);
		//按日计息，按月付息，到期还本
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())){
			//借款周期类型天
			if (borrowPeriodType == 1){
				Date endDate = DateUtils.addDate(beginInterestDate, borrowPeriod);
				endDate = DateUtils.addDate(endDate, -1);
				int months = DateUtils.getMonthsBetweenDates(beginInterestDate,endDate);
				return  computeInterestByDays(borrowPeriod, annualizedRate, totalAmount, beginInterestDate, unitInterest, months);
			}
			//借款周期类型月
			if (borrowPeriodType == 2){
				Date endDate = DateUtils.addMonth(beginInterestDate,borrowPeriod);
				int months = borrowPeriod;
				int days = DateUtils.getIntervalDays(beginInterestDate,endDate) ;
				return  computeInterestByDays(days, annualizedRate, totalAmount, beginInterestDate, unitInterest, months);
			}
			//借款周期类型年
			if (borrowPeriodType ==3){
				Date endDate = DateUtils.addYearsApart(beginInterestDate, borrowPeriod);
				int months = borrowPeriod  *  12;
				int days  = DateUtils.getIntervalDays(beginInterestDate,endDate) ;
				return  computeInterestByDays(days, annualizedRate, totalAmount, beginInterestDate, unitInterest, months);
			}
		}
		//一次性还本付息
		if (DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())){
			return onceRePaymentPrincipalAndIntest(borrowPeriod, borrowPeriodType, annualizedRate, totalAmount, beginInterestDate, unitInterest);
		}
		//等本等息 (按月)   不支持 借款周期为天的
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())){
			return  repaymentByAvgPrincipalInterest(borrowPeriod, borrowPeriodType, annualizedRate, totalAmount, beginInterestDate, unitInterest);
		}
		
		// 等本等息按周还款
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())){
			return repaymentByAvgPrincipalInterestByWeek(borrowPeriod, borrowPeriodType, annualizedRate, totalAmount, beginInterestDate);
		}
		
		// 按日计息，按季付息，到期还本
		if (DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())){
			return computeInterestBySeasonDays(borrowPeriod, borrowPeriodType, annualizedRate, totalAmount, beginInterestDate);
		}
		
		return debtInterests;
	}
	@Override
	public Integer queryPayOffCountByBorrowerId(Long borrowerId) {
		return projectMapper.queryPayOffCountByBorrowerId(borrowerId);
	}
	public Page<ProjectPackage> directPackagePage(Page<ProjectPackage> pageRequest, Map<String, Object> map,ProjectPackage projectPackage) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			map.put("name", projectPackage.getName());
			map.put("status", projectPackage.getStatus());
			int totalCount = projectPackageMapper.countProjectPackageList(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<ProjectPackage> directProjectList = projectPackageMapper.getProjectPackageList(map);	
			pageRequest.setData(directProjectList);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public void computerProjectPackageProgress(Long projectId){
		 List<ProjectPackageLink> list =projectPackageLinkMapper.getProjectPackageByProjectId(projectId);
		 for(ProjectPackageLink link:list){
			 ProjectPackage projectPackage =projectPackageMapper.selectByPrimaryKey(link.getProjectPackageId());
			  this.computerProgressByProjectPackage(projectPackage);
		 }
	}
	/**
	 * 计算资产包进度
	 * @param projectPackage
	 * @return
	 * @throws ManagerException
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	private int computerProgressByProjectPackage(ProjectPackage projectPackage){
		try{
			BigDecimal surplusBalance = BigDecimal.ZERO ;  
			BigDecimal totalAmount = BigDecimal.ZERO ;  
			Date saleComplatedTime = null;
			boolean isFull =true;
			List<ProjectPackageLinkModel> packageModelList= projectPackageMapper.getProjectPackageModelList(projectPackage.getId());
			for(ProjectPackageLinkModel model:packageModelList){
				Project project = projectMapper.selectByPrimaryKey(model.getId());
				Balance _balance = balanceManager.queryBalance(project.getId(), TypeEnum.BALANCE_TYPE_PROJECT);
				BigDecimal projectAmount = project.getTotalAmount()==null?BigDecimal.ZERO:project.getTotalAmount();
				totalAmount= totalAmount.add(projectAmount);//总金额
				if(_balance != null){
					surplusBalance = surplusBalance.add(_balance.getAvailableBalance()==null?BigDecimal.ZERO:_balance.getAvailableBalance());
				}
				//项目状态为 1:存盘,10:待审核,20:待发布
				if((StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus() ==project.getStatus() ||StatusEnum.PROJECT_STATUS_INVESTING.getStatus() == project.getStatus())
						&& StatusEnum.PROJECT_STATUS_SAVE.getStatus() !=project.getStatus() && StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus() !=project.getStatus()
						&& isFull){
					isFull =false;
				}else{
					if(DateUtils.compareTwoDate(saleComplatedTime, project.getSaleComplatedTime()) ==1){
						saleComplatedTime = project.getSaleComplatedTime();
					}
				}
			}
			projectPackage.setTotalAmount(totalAmount);
			if(isFull){//满标
				projectPackage.setSaleComplatedTime(saleComplatedTime);
				projectPackage.setStatus(StatusEnum.PROJECT_PACKAGE_STATUS_SALESCOMPLETED.getStatus());
			}
			projectPackage.setProgress(this.getProjectNumberProgress(projectPackage.getTotalAmount(),surplusBalance));
			projectPackage.setVoteAmount(surplusBalance);
			return projectPackageMapper.updateByPrimaryKeySelective(projectPackage);
		}catch(ManagerException e){
			logger.error("计算资产包进度出现异常{}", projectPackage, e);
		}
		return -1;
	}
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
	public ProjectsPageOutPut getOverPros(Map<String, Object> map) throws ManagerException {
		try {
			ProjectsPageOutPut outPut = projectMapper.getOverProsCount(map);
			outPut.setCurrentPage(map.get("currentPage").toString());
			int mod = outPut.getTotalCount().intValue() % Integer.valueOf(map.get("pageSize").toString());
			int totalPage = outPut.getTotalCount().intValue() / Integer.valueOf(map.get("pageSize").toString());
			if (mod == 0) {
				outPut.setTotalPage(String.valueOf(totalPage));
			} else {
				outPut.setTotalPage(String.valueOf(totalPage + 1));
			}
			if (outPut.getTotalCount() == 0
					|| Integer.valueOf(map.get("startRow").toString()) >= outPut.getTotalCount()) {
				return outPut;
			}
			if (outPut.getTotalCount() == 0
					|| Integer.valueOf(map.get("startRow").toString()) >= outPut.getTotalCount()) {
				return outPut;
			}
			List<ProjectForOpen> dataList = projectMapper.getOverPros(map);
			for (ProjectForOpen data : dataList) {
				List<TransactionsForOpen> transactionList = transactionMapper.queryTransactionForOpen(Long.valueOf(data
						.getProjectId()));
				data.setSubscribes(transactionList);
			}
			outPut.setBorrowList(dataList);
			return outPut;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectsPageOutPut getInvestingPros(Map<String, Object> map) throws ManagerException {
		try {
			ProjectsPageOutPut outPut = projectMapper.getInvestingProsCount(map);
			outPut.setCurrentPage(map.get("currentPage").toString());
			int mod = outPut.getTotalCount().intValue() % Integer.valueOf(map.get("pageSize").toString());
			int totalPage = outPut.getTotalCount().intValue() / Integer.valueOf(map.get("pageSize").toString());
			if (mod == 0) {
				outPut.setTotalPage(String.valueOf(totalPage));
			} else {
				outPut.setTotalPage(String.valueOf(totalPage + 1));
			}
			if (outPut.getTotalCount() == 0
					|| Integer.valueOf(map.get("startRow").toString()) >= outPut.getTotalCount()) {
				return outPut;
			}
			List<ProjectForOpen> dataList = projectMapper.getInvestingPros(map);
			outPut.setOnSaleBorrowList(dataList);
			return outPut;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectsStatusOutPut getProsStatus(List<Long> ids) throws ManagerException {
		try {
			ProjectsStatusOutPut out = new ProjectsStatusOutPut();
			List<ProjectStatus> dataList = projectMapper.getProsStatus(ids);
			out.setTotalLoan(dataList.size());
			if (dataList.size() > 0)
				out.setBorrowStatusList(dataList);
			return out;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
}
