package com.yourong.backend.ic.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.yourong.backend.ic.service.DirectProjectService;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.BalanceAction;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.OSSUtil;
import com.yourong.core.bsc.manager.AuditManager;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.ProjectFeeManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AttachmentThread;
import com.yourong.core.handle.DirectProjectAttachmentHandle;
import com.yourong.core.handle.ProjectThumbnailsAttachmentHandle;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.dao.ProjectPackageLinkMapper;
import com.yourong.core.ic.dao.ProjectPackageMapper;
import com.yourong.core.ic.manager.BorrowerCreditManager;
import com.yourong.core.ic.manager.DebtCollateralManager;
import com.yourong.core.ic.manager.LeaseBonusManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectInterestManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.ProjectNoticeManager;
import com.yourong.core.ic.manager.ProjectOpenManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.BorrowerCredit;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.LeaseBonus;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.ProjectInterest;
import com.yourong.core.ic.model.ProjectNotice;
import com.yourong.core.ic.model.ProjectOpen;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageLink;
import com.yourong.core.ic.model.ProjectPackageLinkModel;
import com.yourong.core.ic.model.ProjectPackageModel;
import com.yourong.core.ic.model.biz.TransferProjectPageBiz;
import com.yourong.core.ic.model.query.TransferProjectPageQuery;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.sys.dao.SysDictMapper;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

/**
 * @desc 直投项目服务类
 * @author fuyili 2015年12月30日下午7:56:42
 */
@Service
public class DirectProjectServiceImpl implements DirectProjectService {

	private static final Logger logger = LoggerFactory.getLogger(DirectProjectService.class);
	@Autowired
	private ProjectManager projectManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	@Resource
	private ProjectThumbnailsAttachmentHandle projectThumbnailsAttachmentHandle;

	@Resource
	private DebtCollateralManager debtCollateralManager;

	@Autowired
	private DirectProjectAttachmentHandle directProjectAttachmentHandle;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private AuditManager auditManager;

	@Autowired
	private ProjectFeeManager managementFeeManager;

	@Autowired
	private LeaseBonusManager leaseBonusManager;

	@Autowired
	private ProjectInterestManager projectInterestManager;

	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	
	@Autowired
	private ProjectExtraManager projectExtraManager;
	
	@Autowired
	private ActivityManager activityManager;
	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Autowired
	private SysDictMapper sysDictMapper;

	@Autowired
	private ProjectOpenManager projectOpenManager;
	
	@Autowired
	private BorrowerCreditManager borrowerCreditManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

	@Autowired
	private ProjectPackageLinkMapper  projectPackageLinkMapper;
	
	@Autowired
	private ProjectPackageMapper projectPackageMapper;
	
	@Autowired
	private ProjectMapper projectMapper;
	
	@Autowired
	private ProjectNoticeManager projectNoticeManager;
	
	@Override
	public Page<DirectProjectBiz> directFindByPage(Page<DirectProjectBiz> pageRequest, Map<String, Object> map) {
		try {
			return projectManager.directFindByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("直投项目分页获取数据失败，pageRequest：{}，map:{}", pageRequest, map, e);
		}
		return null;
	}

	/**
	 * @desc 保存直投项目
	 * @param directProjectBiz
	 * @param appPath
	 * @return
	 * @author fuyili
	 * @time 2016年1月7日 下午8:02:34
	 *
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<DirectProjectBiz> insertDirectProjectInfo(DirectProjectBiz directProjectBiz, String appPath) throws Exception {
		ResultDO<DirectProjectBiz> resultDO = new ResultDO<DirectProjectBiz>();
		/* 前置校验 */
		resultDO = preCheckForAdd(resultDO, directProjectBiz);
		if (!resultDO.isSuccess()) {
			return resultDO;
		}
		SysDict sysdict=sysDictMapper.selectByGroupNameAndValue(directProjectBiz.getChannelBusiness());
		if(sysdict!=null){
			directProjectBiz.setOpenPlatformKey(sysdict.getKey());
			directProjectBiz.setOpenPlatformKeyName(sysdict.getLabel());
		}else {
			directProjectBiz.setOpenPlatformKey(null);
		}
		directProjectBiz.setAnnualizedRateType(ProjectEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getType());// 阶梯收益
		directProjectBiz.setMinAnnualizedRate(directProjectBiz.getAnnualizedRate());
		directProjectBiz.setMaxAnnualizedRate(directProjectBiz.getAnnualizedRate());
		directProjectBiz.setRecommend(Constant.DISABLE);
		String thumbnail = directProjectBiz.getThumbnail();
		directProjectBiz.setThumbnail(null);// 保存的是阿里云地址，所以此处需要设置为空

		//不可转让
		if (directProjectBiz.getTransferFlag()!=null&&directProjectBiz.getTransferFlag()==0){
			directProjectBiz.setTransferRecentRepayment(null);
		}else {
			directProjectBiz.setTransferRecentRepayment(1);
		}
		/* 保存项目 */
		Project project = projectManager.insertDirectProject(directProjectBiz);
		if (project == null) {
			resultDO.setSuccess(false);
			return resultDO;
		}
		Long projectId = project.getId();
		/* 保存抵质押物详细信息 */
		if (directProjectBiz.getDebtCollateral() != null) {
			directProjectBiz.getDebtCollateral().setProjectId(projectId);
			directProjectBiz.getDebtCollateral().setDebtId(0L);// 直投项目，不关联债权id
			directProjectBiz.getDebtCollateral().setCollateralType(directProjectBiz.getProjectType());
			directProjectBiz.getDebtCollateral().setDebtType(directProjectBiz.getSecurityType());
			debtCollateralManager.insert(directProjectBiz.getDebtCollateral());
		}
		/*更新商家订单状态*/
		if (directProjectBiz.getOpenId()!=null&&directProjectBiz.getOpenId()>0){
			ProjectOpen projectOpen=new ProjectOpen();
			projectOpen.setId(directProjectBiz.getOpenId());
			projectOpen.setStatus(4);
			projectOpen.setUpdateTime(new Date());
			projectOpen.setProjectId(projectId);
			projectOpenManager.buildProject(projectOpen);
		}
		/* 保存项目缩略图 */
		if (StringUtil.isNotBlank(thumbnail)) {
			AttachmentInfo info = new AttachmentInfo();
			info.setKeyId(projectId.toString());
			info.setAppPath(thumbnail);
			taskExecutor.execute(new AttachmentThread(projectThumbnailsAttachmentHandle, info));
		}
		/* 保存阶梯收益 */
		ProjectInterest interest = new ProjectInterest();
		interest.setAnnualizedRate(project.getAnnualizedRate());
		interest.setMinInvest(directProjectBiz.getMinInvestAmount());
		interest.setMaxInvest(directProjectBiz.getTotalAmount());
		interest.setProjectId(projectId);
		projectInterestManager.insertSelective(interest);
		
		/* 保存额外活动信息 */
		if (directProjectBiz.getCatalyzerFlag() != 0 && directProjectBiz.getCatalyzerExtraAmount().intValue() > 0) {
			ProjectExtra projectExtra = new ProjectExtra();
			projectExtra.setProjectId(project.getId());
			projectExtra.setExtraType(TypeEnum.PROJECT_EXTRA_PROJECT_ACTIVITY.getType());
			projectExtra.setActivitySign(TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType());
			projectExtra.setExtraAmount(directProjectBiz.getCatalyzerExtraAmount());
			projectExtra.setExtraInformationTwo(directProjectBiz.getCatalyzerHour());
			projectExtraManager.insert(projectExtra);
		}
		
		/* 保存项目加息信息 */
		if (directProjectBiz.getAddRateFlag()!= 0 && directProjectBiz.getAddRate().intValue() > 0) {
			ProjectExtra projectExtra = new ProjectExtra();
			projectExtra.setProjectId(project.getId());
			projectExtra.setExtraType(TypeEnum.PROJECT_EXTRA_PROJECT_ADD_RATE.getType());
			projectExtra.setExtraAmount(directProjectBiz.getAddRate());
			projectExtraManager.insert(projectExtra);
		}

		// 写入余额
		balanceManager.insertBalance(TypeEnum.BALANCE_TYPE_PROJECT, project.getTotalAmount(), project.getId());

		/* 添加直投项目附件 */
		AttachmentInfo info = new AttachmentInfo();
		info.setKeyId(projectId.toString());
		info.setBscAttachments(directProjectBiz.getBscAttachments());
		info.setAppPath(appPath);
		info.setOperation(AttachmentInfo.SAVE);
		info.setAttachMentType(AttachmentInfo.AttachMentType.PROJECT);
		taskExecutor.execute(new AttachmentThread(directProjectAttachmentHandle, info));
		
		// 添加直投项目后增加直投项目借款人授信额度信息
		taskExecutor.execute(new ProcessAddBorrowerCreditAfterAddProjectThread
				(directProjectBiz.getBorrowerId(), directProjectBiz.getBorrowerType(), directProjectBiz.getOpenPlatformKey(), directProjectBiz.getOpenPlatformKeyName()));
		
		return resultDO;
	}

	private ResultDO<DirectProjectBiz> preCheckForAdd(ResultDO<DirectProjectBiz> resultDO, DirectProjectBiz directProjectBiz)
			throws ManagerException {
		// 还款方式选择等本等息按周还款，只能选择借款周期以周为单位，反之一样
		if(directProjectBiz.getBorrowPeriodType() != null && !StringUtil.isNotBlank(directProjectBiz.getProfitType())) {
			String profitType = directProjectBiz.getProfitType();
			int periodType = directProjectBiz.getBorrowPeriodType();
			if((TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType() == periodType 
					&& !DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(profitType)) || 
					(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(profitType) 
							&& TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType() != periodType)	
					) {
				resultDO.setResultCode(ResultCode.PROJECT_BORROW_PERIOD_WEEK_REPAY);
				return resultDO;
			}
		}
		
		// 判断如果选择可转让，不能选择是新手项目
		if (directProjectBiz.getTransferFlag() != null && directProjectBiz.getIsNovice() != null) {
			if (1 == directProjectBiz.getTransferFlag() && 0 == directProjectBiz.getIsNovice()) {
				resultDO.setResultCode(ResultCode.PROJECT_NOVICE_NO_TRANSFER);
				return resultDO;
			}
		}
		
		Map<String, Object> map = Maps.newHashMap();
		String projectName = directProjectBiz.getName();
		map.put("name", projectName);
		map.put("namePeriod", projectName.substring(0, projectName.indexOf("期") + 1));
		map.put("originalProjectNumber", directProjectBiz.getOriginalProjectNumber());
		int count = projectManager.findDirectProjectByProjectNameOrCode(map);
		if (count > 0) {
//			resultDO.setResultCode(ResultCode.DIRECT_PROJECT_NAME_OR_CODE_REPEAT_ERROR);
			resultDO.setResultCode(ResultCode.DIRECT_PROJECT_NAME_OR_PERIOD_OR_CODE_REPEAT_ERROR);
		}
		if(directProjectBiz.getCatalyzerFlag() != 0 && directProjectBiz.getCatalyzerExtraAmount().intValue()<= 0){
			resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_AMOUNT_ERROR);
		}
		if( directProjectBiz.getCatalyzerFlag()!=null&&directProjectBiz.getCatalyzerFlag() != 0 
				&& directProjectBiz.getIsNovice()!=null&&0 == directProjectBiz.getIsNovice()){
			resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NOVICE_ERROR);
		}
		
		if (directProjectBiz.getCatalyzerFlag() != 0 && directProjectBiz.getCatalyzerExtraAmount().intValue() > 0) {
			Activity activity = new Activity();
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
					.getActivityBySelective(activity);
			if(activityList.size()<1){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NOT_SET_ERROR);
			}
		}
		
		
		return resultDO;
	}
	
	/**
	 * 添加直投项目时增加借款人授信额度信息
	 * 
	 * @author luwenshan
	 *
	 */
	private class ProcessAddBorrowerCreditAfterAddProjectThread implements Runnable {
		private Long borrowerId;
		private Integer borrowerType;
		private String openPlatformKey;
		private String openPlatformKeyName;
		
		public ProcessAddBorrowerCreditAfterAddProjectThread(final Long borrowerId, final Integer borrowerType, 
				final String openPlatformKey, final String openPlatformKeyName) {
			this.borrowerId = borrowerId;
			this.borrowerType = borrowerType;
			this.openPlatformKey = openPlatformKey;
			this.openPlatformKeyName = openPlatformKeyName;
		}
		
		@Override
		public void run() {
			logger.info("添加直投项目时增加借款人授信额度信息,borrowerId={},borrowerType={},openPlatformKey={},openPlatformKeyName={}",
					borrowerId,borrowerType,openPlatformKey,openPlatformKeyName);
			try {
				if (StringUtil.isNotBlank(openPlatformKey)) { // 增加渠道商授信额度信息
					BorrowerCredit borrowerCredit = borrowerCreditManager.selectByBorrower(null, 3, openPlatformKey);
					if (borrowerCredit == null) {
						borrowerCredit = new BorrowerCredit();
						borrowerCredit.setBorrowerTrueName(openPlatformKeyName);
						borrowerCredit.setOpenPlatformKey(openPlatformKey);
						borrowerCredit.setBorrowerMobile(null);
						borrowerCredit.setBorrowerType(borrowerType);
						borrowerCredit.setCreditAmount(Constant.DEFALUT_ZT_BORROWER_CREDIT);
						borrowerCredit.setOnlineFlag(TypeEnum.BORROWER_CREDIT_NORMAL.getType());
						borrowerCredit.setInvestType(ProjectEnum.PROJECT_TYPE_DIRECT.getType());
						
						borrowerCreditManager.saveBorrower(borrowerCredit);
					}
				} else { // 增加个人用户/企业用户授信额度信息
					BorrowerCredit borrowerCredit = borrowerCreditManager.selectByBorrower(borrowerId, borrowerType, null);
					if (borrowerCredit == null) {
						borrowerCredit = new BorrowerCredit();
						borrowerCredit.setBorrowerId(borrowerId);
						if (borrowerId != null) {
							Member member = memberManager.selectByPrimaryKey(borrowerId);
							if (member != null) {
								borrowerCredit.setBorrowerTrueName(member.getTrueName());
								borrowerCredit.setBorrowerMobile(member.getMobile());
							}
						}
						borrowerCredit.setBorrowerType(borrowerType);
						borrowerCredit.setCreditAmount(Constant.DEFALUT_ZT_BORROWER_CREDIT);
						borrowerCredit.setOnlineFlag(TypeEnum.BORROWER_CREDIT_NORMAL.getType());
						borrowerCredit.setInvestType(ProjectEnum.PROJECT_TYPE_DIRECT.getType());
						borrowerCreditManager.saveBorrower(borrowerCredit);
					}
				}
			} catch (ManagerException e) {
				logger.error("添加直投项目时增加借款人授信额度信息出现错误,borrowerId={},borrowerType={},openPlatformKey={},openPlatformKeyName={}",
						borrowerId,borrowerType,openPlatformKey,openPlatformKeyName,e);
			}
		}
	}

	@Override
	public DirectProjectBiz findDirectProjectBizById(Long id) {
		DirectProjectBiz biz = null;
		try {
			biz = projectManager.findDirectProjectBizById(id);
			if (biz == null) {
				biz = new DirectProjectBiz();
			}
		} catch (ManagerException e) {
			logger.error("查看直投项目项目失败,项目编号：={}", id, e);
		}
		return biz;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<DirectProjectBiz> editDirectProjectInfo(DirectProjectBiz directProjectBiz, String appPath,String action) throws Exception {
		ResultDO<DirectProjectBiz> resultDO = new ResultDO<DirectProjectBiz>();
		Project p = projectManager.selectByPrimaryKey(directProjectBiz.getId());
		/* 前置校验 */
		resultDO = preCheckForUpdate(resultDO,p,directProjectBiz,action);
		if (!resultDO.isSuccess()) {
			return resultDO;
		}
		// 设置最小收益、最大收益
		if (p.getAnnualizedRate().compareTo(directProjectBiz.getAnnualizedRate()) != 0) {
			directProjectBiz.setMinAnnualizedRate(directProjectBiz.getAnnualizedRate());
			directProjectBiz.setMaxAnnualizedRate(directProjectBiz.getAnnualizedRate());
		}
		String thumbnail = directProjectBiz.getThumbnail();
		directProjectBiz.setThumbnail(p.getThumbnail());// 保存的是阿里云地址，所以此处需要设置为空
		/* 更新项目 */                    
		directProjectBiz.setIsUpdateIntroFlag(1);//更新介绍人信息
		directProjectBiz.setTransferRecentRepayment(0);
		int resultInt = projectManager.updateDirectProjectSelectiveWithNull(directProjectBiz);
		if (resultInt <= 0) {
			resultDO.setSuccess(false);
			return resultDO;
		}
		Long projectId = directProjectBiz.getId();
		/* 更新抵质押物详细信息 */
		if (directProjectBiz.getDebtCollateral() != null) {
			directProjectBiz.getDebtCollateral().setCollateralType(directProjectBiz.getProjectType());
			directProjectBiz.getDebtCollateral().setDebtType(directProjectBiz.getSecurityType());
			debtCollateralManager.updateByPrimaryKeySelective(directProjectBiz.getDebtCollateral());
		}
		/* 更新项目缩略图 */
		if (StringUtil.isNotBlank(thumbnail) && StringUtil.isBlank(p.getThumbnail()) || !p.getThumbnail().equals(thumbnail)) {
			AttachmentInfo info = new AttachmentInfo();
			info.setKeyId(projectId.toString());
			info.setAppPath(thumbnail);
			taskExecutor.execute(new AttachmentThread(projectThumbnailsAttachmentHandle, info));
		}

		/* 更新阶梯收益 */
		List<ProjectInterest> interests = projectInterestManager.getProjectInterestByProjectId(projectId);
		if (Collections3.isNotEmpty(interests)) {
			ProjectInterest interest = interests.get(0);
			interest.setAnnualizedRate(directProjectBiz.getAnnualizedRate());
			interest.setMinInvest(directProjectBiz.getMinInvestAmount());
			interest.setMaxInvest(directProjectBiz.getTotalAmount());
			projectInterestManager.updateByPrimaryKeySelective(interest);
		}

		ProjectExtra projectExtra = projectExtraManager.getProjectQucikReward(directProjectBiz.getId());
		/* 保存额外活动信息 */
		if(directProjectBiz.getCatalyzerFlag()==1&&projectExtra.getProjectId()==null){//新增
			ProjectExtra proExtra = new ProjectExtra();
			proExtra.setProjectId(directProjectBiz.getId());
			proExtra.setExtraType(TypeEnum.PROJECT_EXTRA_PROJECT_ACTIVITY.getType());
			proExtra.setActivitySign(TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType());
			proExtra.setExtraAmount(directProjectBiz.getCatalyzerExtraAmount());
			proExtra.setExtraInformationTwo(directProjectBiz.getCatalyzerHour());
			projectExtraManager.insert(proExtra);
		}else if(directProjectBiz.getCatalyzerFlag()==0&&projectExtra.getProjectId()!=null){//删除
			projectExtraManager.deleteByPrimaryKey(projectExtra.getId());
		}else if(projectExtra.getId()!=null){
			projectExtra.setExtraAmount(directProjectBiz.getCatalyzerExtraAmount());
			projectExtra.setExtraInformationTwo(directProjectBiz.getCatalyzerHour());
			projectExtraManager.updateByPrimaryKeySelective(projectExtra);
		}
		
		ProjectExtra projectExtraAddRate = projectExtraManager.getAddRateProject(directProjectBiz.getId());
		/* 保存项目加息信息 */
		if(directProjectBiz.getAddRateFlag()==1&&projectExtraAddRate==null){//新增
			ProjectExtra proExtra = new ProjectExtra();
			proExtra.setProjectId(directProjectBiz.getId());
			proExtra.setExtraType(TypeEnum.PROJECT_EXTRA_PROJECT_ADD_RATE.getType());
			proExtra.setExtraAmount(directProjectBiz.getAddRate());
			projectExtraManager.insert(proExtra);
		}else if(directProjectBiz.getAddRateFlag()==0&&projectExtraAddRate!=null&&projectExtraAddRate.getProjectId()!=null){//删除
			projectExtraManager.deleteByPrimaryKey(projectExtraAddRate.getId());
		}else if(projectExtraAddRate!=null&&projectExtraAddRate.getId()!=null){
			projectExtraAddRate.setExtraAmount(directProjectBiz.getAddRate());
			projectExtraManager.updateByPrimaryKeySelective(projectExtraAddRate);
		}
		
		// 更新余额
		balanceManager.resetProjectBalance(directProjectBiz.getTotalAmount(), directProjectBiz.getId());

		/* 更新直投项目附件 */
		AttachmentInfo info = new AttachmentInfo();
		info.setKeyId(projectId.toString());
		info.setBscAttachments(directProjectBiz.getBscAttachments());
		info.setAppPath(appPath);
		info.setOperation(AttachmentInfo.UPDATE);
		info.setAttachMentType(AttachmentInfo.AttachMentType.PROJECT);
		taskExecutor.execute(new AttachmentThread(directProjectAttachmentHandle, info));
		
		// 提交审核前编辑直投项目后增加直投项目借款人授信额度信息
		if(p.getStatus() == StatusEnum.PROJECT_STATUS_SAVE.getStatus() || p.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus()){
			taskExecutor.execute(new ProcessAddBorrowerCreditAfterAddProjectThread
					(directProjectBiz.getBorrowerId(), directProjectBiz.getBorrowerType(), directProjectBiz.getOpenPlatformKey(), directProjectBiz.getOpenPlatformKeyName()));
		}
		return resultDO;
	}

	private ResultDO<DirectProjectBiz> preCheckForUpdate(ResultDO<DirectProjectBiz> resultDO, Project project,DirectProjectBiz directProjectBiz,String action)
			throws ManagerException {
		// 还款方式选择等本等息按周还款，只能选择借款周期以周为单位，反之一样
		if(directProjectBiz.getBorrowPeriodType() != null && !StringUtil.isNotBlank(directProjectBiz.getProfitType())) {
			String profitType = directProjectBiz.getProfitType();
			int periodType = directProjectBiz.getBorrowPeriodType();
			if((TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType() == periodType 
					&& !DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(profitType)) || 
					(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(profitType) 
							&& TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType() != periodType)	
					) {
				resultDO.setResultCode(ResultCode.PROJECT_BORROW_PERIOD_WEEK_REPAY);
				return resultDO;
			}
		}
		
		// 判断如果选择可转让，不能选择是新手项目
		if (directProjectBiz.getTransferFlag() != null && directProjectBiz.getIsNovice() != null) {
			if (1 == directProjectBiz.getTransferFlag() && 0 == directProjectBiz.getIsNovice()) {
				resultDO.setResultCode(ResultCode.PROJECT_NOVICE_NO_TRANSFER);
				return resultDO;
			}
		}
		
		Map<String, Object> map = Maps.newHashMap();
		String projectName = directProjectBiz.getName();
		map.put("name", directProjectBiz.getName());
		map.put("namePeriod", projectName.substring(0, projectName.indexOf("期") + 1));
		map.put("originalProjectNumber", directProjectBiz.getOriginalProjectNumber());
		map.put("id", directProjectBiz.getId());
		int count = projectManager.findDirectProjectByProjectNameOrCode(map);
		if (count > 0) {
//			resultDO.setResultCode(ResultCode.DIRECT_PROJECT_NAME_OR_CODE_REPEAT_ERROR);
			resultDO.setResultCode(ResultCode.DIRECT_PROJECT_NAME_OR_PERIOD_OR_CODE_REPEAT_ERROR);
		}
		if("emergencyAll".equals(action)){
			if(project.getStatus()!=StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus()){
				resultDO.setResultCode(ResultCode.PROJECT_STATUS_CANNOT_EMERGENCYALL);
				return resultDO;
			}
		}
		if("edit".equals(action)){
			if(project.getStatus()!=StatusEnum.PROJECT_STATUS_SAVE.getStatus()&&project.getStatus()!=StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus()){
				resultDO.setResultCode(ResultCode.PROJECT_STATUS_CANNOT_EDIT);
				return resultDO;
			}
		}
		if(directProjectBiz.getCatalyzerFlag() != 0 && directProjectBiz.getCatalyzerExtraAmount().intValue()<= 0){
			resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_AMOUNT_ERROR);
		}
		if( directProjectBiz.getCatalyzerFlag()!=null&&directProjectBiz.getCatalyzerFlag() != 0 
				&& directProjectBiz.getIsNovice()!=null&&0 == directProjectBiz.getIsNovice()){
			resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NOVICE_ERROR);
		}
		
		return resultDO;
	}

	@Override
	public ResultDO<DirectProjectBiz> emergencyPartDirectProjectInfo(DirectProjectBiz directProjectBiz, String appPath) {
		ResultDO<DirectProjectBiz> resultDO = new ResultDO<DirectProjectBiz>();
		try {
			Project p = projectManager.selectByPrimaryKey(directProjectBiz.getId());
			Long projectId = directProjectBiz.getId();
			/* 更新项目 ，只支持（项目介绍、风控信息） */
			DirectProjectBiz projectBiz = new DirectProjectBiz();
			projectBiz.setShortDesc(directProjectBiz.getShortDesc());// 项目介绍
			projectBiz.setDescription(directProjectBiz.getDescription());// 风控信息
			projectBiz.setBorrowerType(p.getBorrowerType());
			projectBiz.setId(projectId);
			projectBiz.setTransferRecentRepayment(0);
			int resultInt = projectManager.updateDirectProjectSelective(projectBiz);
			if (resultInt <= 0) {
				resultDO.setSuccess(false);
				return resultDO;
			}
			/* 更新抵质押物详细信息 */
			if (directProjectBiz.getDebtCollateral() != null) {
				directProjectBiz.getDebtCollateral().setCollateralType(directProjectBiz.getProjectType());
				directProjectBiz.getDebtCollateral().setDebtType(directProjectBiz.getSecurityType());
				debtCollateralManager.updateByPrimaryKeySelective(directProjectBiz.getDebtCollateral());
			}
			/* 更新直投项目附件 */
			AttachmentInfo info = new AttachmentInfo();
			info.setKeyId(projectId.toString());
			info.setBscAttachments(directProjectBiz.getBscAttachments());
			info.setAppPath(appPath);
			info.setOperation(AttachmentInfo.UPDATE);
			info.setAttachMentType(AttachmentInfo.AttachMentType.PROJECT);
			taskExecutor.execute(new AttachmentThread(directProjectAttachmentHandle, info));
		} catch (ManagerException e) {
			logger.error("紧急修改（部分）直投项目项目失败,directProjectBiz={},appPath={}", directProjectBiz, appPath, e);
		}
		return resultDO;
	}

	@Override
	public int waitReviewProject(Long id, Long auditId) {
		int actionFlag = 0;
		try {
			int newStatus = StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_SAVE.getStatus();
			actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, null);
		} catch (ManagerException e) {
			logger.error("直投项目提交待审失败：id={}，auditId={}", id, auditId, e);
		}
		return actionFlag;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Project> reviewProject(Long id, Long auditId, String msg) throws Exception {
		int actionFlag = 0;
		ResultDO<Project> resultDO = new ResultDO<Project>();
		// 判断项目是否为经营融，信用额度是否满足要求
		resultDO = checkCompanyCreditAmount(id);
		if (!resultDO.isSuccess()) {
			return resultDO;
		}
		int newStatus = StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus();
		int currentStatus = StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus();
		actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msg);
		if (actionFlag <= 0) {
			resultDO.setSuccess(false);
			return resultDO;
		}
		// 审核记录
		auditManager.insertAudit(auditId, msg, TypeEnum.AUDIT_RESULT_SUCCESS.getType(), TypeEnum.AUDIT_TYPE_PROJECT.getType(), id,
				StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus());
		// 项目审核成功，扣除用户的可用余额
		DirectProjectBiz project = projectManager.findDirectProjectBizById(id);
		if (DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode().equals(project.getProjectType()) && project.getEnterpriseId() != null) {
			balanceManager.reduceBalance(TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT, project.getTotalAmount(), project.getEnterpriseId(),
					BalanceAction.balance_Available_subtract);
		}
		
		// 增加直投项目借款人的授信额存续量
		taskExecutor.execute(new ProcessIncreaseBorrowerCreditThread(project.getBorrowerId(), project.getBorrowerType(), project.getOpenPlatformKey(), project.getTotalAmount()));
		return resultDO;
	}
	
	/**
	 * 直投项目上线审核后借款人存续量增加线程
	 * 
	 * @author luwenshan
	 *
	 */
	private class ProcessIncreaseBorrowerCreditThread implements Runnable {
		private Long borrowerId;
		private Integer borrowerType;
		private String openPlatformKey;
		private BigDecimal projectTotalAmount;
		
		public ProcessIncreaseBorrowerCreditThread(final Long borrowerId, final Integer borrowerType, final String openPlatformKey, final BigDecimal projectTotalAmount) {
			this.borrowerId = borrowerId;
			this.borrowerType = borrowerType;
			this.openPlatformKey = openPlatformKey;
			this.projectTotalAmount = projectTotalAmount;
		}
		
		@Override
		@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
		public void run() {
			logger.info("增加直投项目后增加该项目借款人的存续量,borrowerId={},borrowerType={},openPlatformKey={},projectTotalAmount={}",
					borrowerId,borrowerType,openPlatformKey,projectTotalAmount);
			try {
				borrowerCreditManager.increaseBorrowerCredit(borrowerId, borrowerType, openPlatformKey, projectTotalAmount);
			} catch (ManagerException e) {
				logger.error("增加直投项目后增加该项目借款人的存续量出现错误,borrowerId={},borrowerType={},openPlatformKey={},projectTotalAmount={}",
						borrowerId,borrowerType,openPlatformKey,projectTotalAmount,e);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Project> fallbackSaveStatus(Long id, Long auditId, String msg) throws ManagerException {
		int actionFlag = 0;
		ResultDO<Project> resultDO = new ResultDO<Project>();
		try {
			int newStatus = StatusEnum.PROJECT_STATUS_SAVE.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus();
			actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msg);
			if (actionFlag <= 0) {
				resultDO.setSuccess(false);
				return resultDO;
			}
			auditManager.insertAudit(auditId, msg, TypeEnum.AUDIT_RESULT_FAILD.getType(), TypeEnum.AUDIT_TYPE_PROJECT.getType(), id,
					StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus());
		} catch (ManagerException e) {
			logger.error("直投项目回退到存盘状态失败： id ={},auditId={},msg={}", id, auditId, msg, e);
			throw new ManagerException(e);
		}
		return resultDO;
	}

	@Override
	public int stopProject(Long id, Long auditId, String msg) {
		int actionFlag = 0;
		try {
			int newStatus = StatusEnum.PROJECT_STATUS_STOP.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_INVESTING.getStatus();
			actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msg);
		} catch (ManagerException e) {
			logger.error("直投项目暂停失败：id={},auditId={},msg={}", id, auditId, msg, e);
		}
		return actionFlag;
	}

	@Override
	public int startProject(Long id, Long auditId, String msg) {
		int actionFlag = 0;
		try {
			int newStatus = StatusEnum.PROJECT_STATUS_INVESTING.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_STOP.getStatus();
			actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msg);
		} catch (ManagerException e) {
			logger.error("直投项目恢复状态到投资中失败：id={},auditId={},msg={}", id, auditId, msg, e);
		}
		return actionFlag;
	}

	/**
	 * 更新状态
	 * 
	 * @param newStatus
	 *            新状态
	 * @param currentStatus
	 *            当前状态
	 * @param id
	 *            项目编号
	 * @param auditId
	 *            操作员编号
	 * @param msg
	 *            操作原因
	 * @return
	 * @throws ManagerException
	 */
	private int modifyProjectStatus(int newStatus, int currentStatus, Long id, Long auditId, String msg) throws ManagerException {
		int actionFlag = 0;
		Project project = projectManager.selectByPrimaryKey(id);
		if (project != null && project.getStatus() == currentStatus) {
			actionFlag = projectManager.updateProjectStatus(newStatus, currentStatus, project.getId());
			// 只更新部分值
			Project p = new Project();
			p.setUpdateTime(DateUtils.getCurrentDateTime());
			p.setId(project.getId());
			projectManager.updateByPrimaryKeySelective(p);
			logger.info("直投项目：{}，由操作员{}将状态【{}】改为【{}】", project.getId(), auditId, currentStatus, +newStatus);
		}
		return actionFlag;
	}

	/**
	 * @Description:验证公司信用总额
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月22日 下午2:40:31
	 */
	private ResultDO<Project> checkCompanyCreditAmount(Long id) throws ManagerException {
		ResultDO<Project> result = new ResultDO<Project>();
		DirectProjectBiz project = this.findDirectProjectBizById(id);
		if (DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode().equals(project.getProjectType()) && project.getEnterpriseId() != null) {
			Balance balance = balanceManager.queryBalance(project.getEnterpriseId(), TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
			if (balance == null) {
				result.setResultCode(ResultCode.DEBT_SAVE_ENTERPRISE_CREDIT_AMOUNT_NOT_NULL);
				return result;
			}
			if (balance.getAvailableBalance().compareTo(project.getTotalAmount()) < 0) {
				result.setResultCode(ResultCode.PROJECT_AVAILABLE_CREDIT_LESS_THEN_PROJECT_AMOUNT_ERROR);
				return result;
			}
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Project> riskReviewProjectSuccess(Long id, Long auditId, String msg) throws Exception {
		ResultDO<Project> result = new ResultDO<Project>();
		int newStatus = StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus();
		int currentStatus = StatusEnum.PROJECT_STATUS_FULL.getStatus();
		int actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msg);
		if (actionFlag <= 0) {
			result.setSuccess(false);
			return result;
		}
		// 添加记录到审核表
		auditManager.insertAudit(auditId, msg, TypeEnum.AUDIT_RESULT_SUCCESS.getType(), TypeEnum.AUDIT_TYPE_PROJECT.getType(), id,
				StatusEnum.PROJECT_STATUS_FULL.getStatus());
		//未抽奖 用户自动抽奖
		this.directQuickReward(id);
		
		// 插入管理费记录,保证金和风险金，介绍费
		Project project = projectManager.selectByPrimaryKey(id);
		managementFeeManager.insertProjectFee(project.getId(), TypeEnum.FEE_TYPE_MANAGE.getType(), Long.parseLong(Config.internalMemberId));
		managementFeeManager.insertProjectFee(project.getId(), TypeEnum.FEE_TYPE_RISK.getType(), Long.parseLong(Config.internalMemberId));
		managementFeeManager.insertProjectFee(project.getId(), TypeEnum.FEE_TYPE_GUARANTEE.getType(), Long.parseLong(Config.internalMemberId));
		// 存在介绍人，需插入介绍费
		if (project.getIntroducerId() != null && project.getIntroducerFeeRate() != null) {
			managementFeeManager.insertProjectFee(project.getId(), TypeEnum.FEE_TYPE_INTRODUCER.getType(), project.getIntroducerId());
		}
		// 平台投资总额累计破亿
		Balance balance = balanceManager.queryBalance(-1L, TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_INVEST);
		if (balance != null) {
			RedisPlatformClient.breakHundredMillion(balance.getAvailableBalance());
		}
		// 查询项目下代收冻结的代收记录
		List<HostingCollectTrade> freezeList = hostingCollectTradeManager.selectPreAuthApplySuccessByProjectId(project.getId());
		if (Collections3.isNotEmpty(freezeList)) {
			// 发起代收完成
			result.setResult(project);
			projectManager.finishPreAuthTrade(result, freezeList);
		} else {
			// 极端情况，所有都是用现金券投完的，执行审核通过后续业务
			projectManager.afterHandlePreAuthTrade(project.getId(), false);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteProjectById(Long id) throws ManagerException {
		int delFlag = 0;
		Project p = projectManager.selectByPrimaryKey(id);
		if (p != null && p.getStatus() == StatusEnum.PROJECT_STATUS_SAVE.getStatus()) {// 只有存盘的状态下才可以删除
			delFlag = projectManager.deleteProjectById(p.getId());
			// 如果是租赁分红项目删除租赁分红
			LeaseBonus leaseBonus = leaseBonusManager.findLeaseBonusByProjectId(p.getId());
			if (p.getJoinLease().intValue() == StatusEnum.LEASE_BONUS_JOIN_LEASE.getStatus() && leaseBonus != null) {
				leaseBonusManager.deleteByPrimaryKey(leaseBonus.getId());
			}
		}
		return delFlag;
	}

	@Override
	public ResultDO<Object> updateOnlineTimeAndEndDate(Date onlineTime, Date saleEndTime, Long id) {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			int row = projectManager
					.updateOnlineAEndTimeById(new Timestamp(onlineTime.getTime()), new Timestamp(saleEndTime.getTime()), id);
			if (row <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			logger.error("更新上线和销售截止时间失败，onlineTime={},saleEndTime={}，id={}", onlineTime, saleEndTime, id, e);
		}
		return result;
	}

	@Override
	public ResultDO<Object> updateEndDate(Date saleEndTime, Long id) {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			int row = projectManager.updateSaleEndTimeById(new Timestamp(saleEndTime.getTime()), id);
			if (row <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			logger.error("更新销售截止时间失败，saleEndTime={},id={}", saleEndTime, id, e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Project> riskReviewProjectFail(Long id, Long auditId, String msgs) throws ManagerException {
		ResultDO<Project> result = new ResultDO<Project>();
		int newStatus = StatusEnum.PROJECT_STATUS_LOSING.getStatus();
		int currentStatus = StatusEnum.PROJECT_STATUS_FULL.getStatus();
		int actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msgs);
		if (actionFlag <= 0) {
			result.setResultCode(ResultCode.PROJECT_LOSE_NOT_PROJECT_STATUS_FULL);
			return result;
		}
		// 添加记录到审核表
		auditManager.insertAudit(auditId, msgs, TypeEnum.AUDIT_RESULT_FAILD.getType(), TypeEnum.AUDIT_TYPE_PROJECT.getType(), id,
				StatusEnum.PROJECT_STATUS_FULL.getStatus());
		//调用流标处理
		taskExecutor.execute(new DirectProjectLoseThread(id));
		return result;
	}

	@Override
	public ResultDO<Project> loseProject(Long id) {
		ResultDO<Project> result = new ResultDO<Project>();
		try {
			int newStatus = StatusEnum.PROJECT_STATUS_LOSING.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_END.getStatus();
			int actionFlag = projectManager.updateProjectStatus(newStatus, currentStatus, id);
			if (actionFlag <= 0) {
				result.setResultCode(ResultCode.PROJECT_LOSE_NOT_PROJECT_STATUS_END);
				return result;
			}
			//调用流标处理
			taskExecutor.execute(new DirectProjectLoseThread(id));
		} catch (ManagerException e) {
			logger.error("执行流标失败，id={}", id, e);
		}
		return result;
	}
	
	
	private class DirectProjectLoseThread implements Runnable {
		private Long projectId;
		public DirectProjectLoseThread(final Long projectId) {
			this.projectId = projectId;
		}
		public void run() {
			try {
				projectManager.loseProject(projectId);
			} catch (ManagerException e) {
				logger.error("【流标】发生异常{}",projectId,e);
			}
		}
	}

	@Override
	public List<SysDict> findTransferRate() {
		List<SysDict> sysDicts= sysDictMapper.findByGroupName("transferRate_group");
		return sysDicts;
	}

	@Override
	public Page<TransferProjectPageBiz> queryPageTransferProjectPageBiz(TransferProjectPageQuery query) {
		return transferProjectManager.queryPageTransferProjectPageBiz(query);
	}
	
	private void directQuickReward(Long projectId){
		try {
			ProjectExtra pe = projectExtraManager.getProjectQucikReward(projectId);
			if(pe.getActivitySign()==null||pe.getActivitySign()!=TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType()){
				return;
			}
			
			ActivityLotteryResult model=new ActivityLotteryResult();
			model.setActivityId(pe.getActivityId());
			model.setRemark(projectId.toString());
			model.setLotteryStatus(StatusEnum.LOTTERY_STATUS_NO.getStatus());
			List<ActivityLotteryResult> listActivityResult=activityLotteryResultManager.sumRewardInfoByMemberId(model);
			if(Collections3.isEmpty(listActivityResult)){
				return;
			}
			for(ActivityLotteryResult act:listActivityResult){
				ResultDO<Object> rDO = projectManager.directProjectLottery(
						projectId, act.getMemberId(), 2);
			}
			
		} catch (Exception  e) {
			logger.error("【风控审核，自动抽奖】发生异常{}",projectId,e);
		}
	}
	public Page<ProjectPackage> directPackagePage(Page<ProjectPackage> pageRequest, Map<String, Object> map,ProjectPackage projectPackage){
		try {
			return projectManager.directPackagePage(pageRequest, map,projectPackage);
		} catch (Exception e) {
			logger.error("直投打包查询失败，pageRequest：{}，map:{}", pageRequest, map, e);
		}
		return null;
	}
	private int checkProjectJson(List<String> projectIdList,Long projectPackageId){
		int resInt =0;
		try{
			//检查输入的projectId是否正确
			for(String projectId:projectIdList){
				if(StringUtil.isBlank(projectId)){
					resInt =-1;
					break;
				}
				//判断项目是否存在
				Project project = projectManager.selectByPrimaryKey(Long.parseLong(projectId));
				if(project == null){
					resInt =-2;
					break;
				}
				//判断项目状态 项目在存盘、待审核和待发布(没有预告)不能添加集合中
				if(StatusEnum.PROJECT_STATUS_SAVE.getStatus() == project.getStatus().intValue()||
						StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus() == project.getStatus().intValue()
						){
					resInt =-4;
					break;
				}
				//判断项目是否预告
				if( StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus() == project.getStatus().intValue()){
					ProjectNotice projectNotice =projectNoticeManager.getProjectNoticingByProjectId(Long.parseLong(projectId));
					if(projectNotice == null){
						resInt =-5;
						break;
					}
				}
				//更新操作坚持项目是否已经添加过资产包
				int count = projectPackageLinkMapper.countPackageLinkProject(Long.parseLong(projectId),projectPackageId);
				if(count !=0){
					resInt = -3;
				    break;
				}
		   }
		}catch(Exception e){
			logger.error("解析项目编号发生异常{}",projectIdList.toString(),e);
		}
		return resInt;
	}
	/**
	 * 同一个项目可以被多个资产包应用，暂时未做任何控制
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int addProjectNumber(ProjectPackageModel packageModel)throws Exception{
		List<String> projectIdList = JSONArray.parseArray(packageModel.getProjectIdJson(),String.class);
		int resInt = this.checkProjectJson(projectIdList,packageModel.getId());
		if(resInt < 0){
			return resInt;
		}
		ProjectPackage projectPackage =new  ProjectPackage();
		
		org.springframework.beans.BeanUtils.copyProperties(packageModel, projectPackage);
		//上传图片到阿里云
		if(StringUtil.isNotBlank(packageModel.getImgurl())){
			if(projectPackage.getId()!=null){
				projectPackage = projectPackageMapper.selectByPrimaryKey(projectPackage.getId());
				projectPackage.setName(packageModel.getName());
			}
			if(projectPackage.getId()==null || (projectPackage.getImgurl()!=null && !projectPackage.getImgurl().equals(packageModel.getImgurl()))){
			  String imgurl = packageModel.getAppPath()+packageModel.getImgurl();
		      String imagePath  = uploadFile(imgurl);
		      projectPackage.setImgurl(imagePath);
			}
		}
		//保全资产包
		if(projectPackage.getId() == null){
			projectPackage.setUpdateTime(new Date());
			projectPackage.setCreateTime(new Date());
			projectPackage.setStatus(StatusEnum.PROJECT_PACKAGE_STATUS_NO_AUDIT.getStatus());
			projectPackage.setSaleStartTime(new Date());
			projectPackage.setSaleEndTime(new Date());
			projectPackageMapper.insertSelective(projectPackage);
		}else{
			
			List<ProjectPackageLink> pplList = projectPackageLinkMapper.selectByPackageList(projectPackage.getId());
			for(ProjectPackageLink p:pplList){
				 //将项目标记非资产包项目状态
			    projectMapper.updateProjectPakcageFlag(0, p.getProjectId());
			}
			projectPackage.setUpdateTime(new Date());
			//清除所有资产包内容
			projectPackageLinkMapper.deleteProjectFromPackage(projectPackage.getId());
		}
		projectPackage.setTotalCount(projectIdList.size());
		BigDecimal maxAnnualizedRate = BigDecimal.ZERO ;
		BigDecimal minAnnualizedRate = BigDecimal.ZERO ;
		BigDecimal surplusBalance = BigDecimal.ZERO ;  
		BigDecimal totalAmount = BigDecimal.ZERO ;  
		int maxBorrowPeriod = 0,minBorrowPeriod = 0,maxBorrowPeriodType = 0,minBorrowPeriodType = 0;
		Date saleComplatedTime =null, saleStartTime = null ,saleEndTime = null;
		boolean isFull =true;
		for(int i=0;i<projectIdList.size();i++){
			String projectId =projectIdList.get(i);
			ProjectPackageLink  projectPackageLink = new ProjectPackageLink();
			projectPackageLink.setProjectId(Long.parseLong(projectId));
			projectPackageLink.setProjectPackageId(projectPackage.getId());
			Project project = projectManager.selectByPrimaryKey(Long.parseLong(projectId));
			
			Balance _balance = balanceManager.queryBalance(project.getId(), TypeEnum.BALANCE_TYPE_PROJECT);
			BigDecimal projectAmount = project.getTotalAmount()==null?BigDecimal.ZERO:project.getTotalAmount();
			BigDecimal currentAnnualizedRate = project.getAnnualizedRate()==null?BigDecimal.ZERO:project.getAnnualizedRate();
			Integer currentBorrowPeriod = project.getBorrowPeriod()==null?0:project.getBorrowPeriod();
			Integer currentBorrowerType =  project.getBorrowPeriodType()==null?0:project.getBorrowPeriodType();
			//额外收益
			ProjectExtra  projectExtra = projectExtraManager.getAddRateProject(Long.parseLong(projectId));
			if(projectExtra!=null && projectExtra.getExtraType().intValue() ==2){
				BigDecimal addRate = projectExtra.getExtraAmount() ==null?BigDecimal.ZERO:projectExtra.getExtraAmount();
				currentAnnualizedRate = currentAnnualizedRate.add(addRate);
			}
			if(i==0){//初始化
				maxAnnualizedRate = minAnnualizedRate = currentAnnualizedRate;
				maxBorrowPeriod = minBorrowPeriod = currentBorrowPeriod;
				maxBorrowPeriodType = minBorrowPeriodType = currentBorrowerType;
				saleStartTime = project.getOnlineTime();
				saleEndTime =  project.getSaleEndTime();
			}
			if(DateUtils.compareTwoDate(saleStartTime, project.getOnlineTime())==2){
				saleStartTime = project.getOnlineTime();
			}
			if(DateUtils.compareTwoDate(saleEndTime, project.getSaleEndTime())==1){
				saleEndTime = project.getSaleEndTime();
			}
			if(maxAnnualizedRate.compareTo(currentAnnualizedRate) > 0){//最小利率
				minAnnualizedRate =currentAnnualizedRate;
			}
			if(minAnnualizedRate.compareTo(currentAnnualizedRate) < 0){//最大利率
				maxAnnualizedRate =currentAnnualizedRate;
			}
			if(coverBorrowPeriod(maxBorrowPeriod,maxBorrowPeriodType) < coverBorrowPeriod(currentBorrowPeriod,currentBorrowerType)){ //最大收益周期
				maxBorrowPeriod = currentBorrowPeriod;
				maxBorrowPeriodType= currentBorrowerType;
			}
			if(coverBorrowPeriod(minBorrowPeriod,minBorrowPeriodType) > coverBorrowPeriod(currentBorrowPeriod,currentBorrowerType)){ //最小收益周期
				minBorrowPeriod = currentBorrowPeriod;
				minBorrowPeriodType = currentBorrowerType;
			}
			totalAmount= totalAmount.add(projectAmount);//总金额
			if(_balance != null){
				surplusBalance = surplusBalance.add(_balance.getAvailableBalance()==null?BigDecimal.ZERO:_balance.getAvailableBalance());
			}
			//项目状态为 1:存盘,10:待审核,20:待发布
			if((StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus() ==project.getStatus() ||StatusEnum.PROJECT_STATUS_INVESTING.getStatus() == project.getStatus())
					&& StatusEnum.PROJECT_STATUS_SAVE.getStatus() !=project.getStatus() && StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus() !=project.getStatus()
					&& isFull){
				saleComplatedTime = null;
				isFull =false;
			}else{
				if(DateUtils.compareTwoDate(saleComplatedTime, project.getSaleComplatedTime()) ==1){
					saleComplatedTime = project.getSaleComplatedTime();
				}
			}
		    projectPackageLinkMapper.insert(projectPackageLink);
		    //将项目标记成资产包引用状态
		    projectMapper.updateProjectPakcageFlag(1, project.getId());
		}
		projectPackage.setTotalAmount(totalAmount);
		projectPackage.setMinAnnualizedRate(minAnnualizedRate);
		projectPackage.setMaxAnnualizedRate(maxAnnualizedRate);
		projectPackage.setMaxBorrowPeriod(maxBorrowPeriod);
		projectPackage.setMinBorrowPeriod(minBorrowPeriod);
		projectPackage.setMinBorrowPeriodType(minBorrowPeriodType);
		projectPackage.setMaxBorrowPeriodType(maxBorrowPeriodType);
		projectPackage.setSaleStartTime(saleStartTime);
		projectPackage.setSaleEndTime(saleEndTime);
		if(isFull && projectPackage.getStatus()!=StatusEnum.PROJECT_PACKAGE_STATUS_NO_AUDIT.getStatus()){//满标
			projectPackage.setSaleComplatedTime(saleComplatedTime);
			projectPackage.setStatus(StatusEnum.PROJECT_PACKAGE_STATUS_SALESCOMPLETED.getStatus());
		}
		projectPackage.setProgress(this.getProjectNumberProgress(projectPackage.getTotalAmount(),surplusBalance));
		projectPackage.setVoteAmount(surplusBalance);
		resInt = projectPackageMapper.updateByPrimaryKeySelective(projectPackage);
		return resInt;
	}
	private int coverBorrowPeriod(int borrowPerod,int borrowPeriodType){
		int day =borrowPerod;
		switch(borrowPeriodType){
			case 2: day = borrowPerod*30;break;
			case 3: day = borrowPerod*365;break;
			case 4: day = borrowPerod*7;break;
			default: day = borrowPerod; break;
		}
		return day;
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
	public  ProjectPackage getDefualtProjectPackage(){
		return projectPackageMapper.selectByPrimaryKey(1L);
	}
	public int batchDelete(String ids){
		List<String> projectPackageIdList = JSONArray.parseArray(ids,String.class);
		int resInt =-1;
		if(StringUtil.isBlank(ids)){
			return resInt;
		}
		long[] id_array =new long[projectPackageIdList.size()];
		for(int i=0;i<projectPackageIdList.size();i++){
			if(StringUtil.isBlank(projectPackageIdList.get(i))){
				resInt =-1;
				break;
			}
			id_array[i]=Long.parseLong(projectPackageIdList.get(i));
			List<ProjectPackageLink> list =projectPackageLinkMapper.selectByPackageList(Long.parseLong(projectPackageIdList.get(i)));
			for(ProjectPackageLink p:list){
				 //将项目标记非资产包项目状态
			    projectMapper.updateProjectPakcageFlag(0, p.getProjectId());
			}			
		}
		resInt = projectPackageMapper.batchDelete(id_array);
		resInt = projectPackageLinkMapper.batchDelete(id_array);
		return resInt;
	}	
	// 文件上传阿里云
	public String uploadFile(String rootPath) {
		// 文件上传
		if (StringUtil.isNotBlank(rootPath)) { // 获取文件名称
			logger.debug("文件路径：" + rootPath);
			File localFile = new File(rootPath);
			String key = OSSUtil.getBannerKey(localFile.getName(), DateUtils.getCurrentDateTime());
			String url = OSSUtil.uploadImageToOSS(key, rootPath);
			String simpleUrl = OSSUtil.getSimpleImageUrl(url);
			return simpleUrl;
		}
		return null;
	}

	@Override
	public ProjectPackageModel getPackage(Long projectPackageId) {
		ProjectPackageModel model  = new ProjectPackageModel();
		if(projectPackageId == null){
			return model;
		}
		//获取资产包信息
		ProjectPackage packageEntity = projectPackageMapper.selectByPrimaryKey(projectPackageId);
		org.springframework.beans.BeanUtils.copyProperties(packageEntity, model);
		model.setPathImg(Config.getOssPicUrl()+model.getImgurl());
		//获取资产包项目信息
		List<ProjectPackageLink> pbList = projectPackageLinkMapper.selectByPackageList(packageEntity.getId());
		model.setProjectList(pbList);		 
		return model;
	}
	public int auditProjectFromPackage(Long projectPackageId){
		//获取资产包信息
		ProjectPackage packageEntity = projectPackageMapper.selectByPrimaryKey(projectPackageId);
		List<ProjectPackageLink> projectPackageLinkList = projectPackageLinkMapper.selectByPackageList(projectPackageId);
		Date saleComplatedTime = null;
		boolean isFull =true;
		for(ProjectPackageLink packLink:projectPackageLinkList){
			Long projectId =packLink.getProjectId();
			Project project = projectMapper.selectByPrimaryKey(projectId);
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
		if(isFull){
			packageEntity.setStatus(StatusEnum.PROJECT_PACKAGE_STATUS_SALESCOMPLETED.getStatus());
			packageEntity.setSaleComplatedTime(saleComplatedTime);
		}else{
			packageEntity.setStatus(StatusEnum.PROJECT_PACKAGE_STATUS_INSALES.getStatus());
		}
		packageEntity.setAuditTime(new Date());
		return projectPackageMapper.updateByPrimaryKeySelective(packageEntity);
	}

	@Override
	public List<ProjectPackageLinkModel> getProjectPackageList(Long projectPackageId) {
		List<ProjectPackageLinkModel> modelList = projectPackageMapper.getProjectPackageModelList(projectPackageId);
		try{
			for(ProjectPackageLinkModel model:modelList){
				ProjectExtra  projectExtra = projectExtraManager.getAddRateProject(model.getId());
				BigDecimal annualizedRate = model.getAnnualizedRate() ==null?BigDecimal.ZERO:model.getAnnualizedRate();
				if(projectExtra!=null && projectExtra.getExtraType().intValue() ==2){
					BigDecimal addRate = projectExtra.getExtraAmount() ==null?BigDecimal.ZERO:projectExtra.getExtraAmount();
					annualizedRate.add(addRate);
				}
				model.setAnnualizedRate(annualizedRate);
				Balance _balance = balanceManager.queryBalance(model.getId(), TypeEnum.BALANCE_TYPE_PROJECT);
				model.setProgress(this.getProjectNumberProgress(model.getTotalAmount(),_balance.getAvailableBalance()));
			}
		}catch(Exception e){
			logger.error("获取资产包项目列表异常{}",modelList.toString(),e);
		}
		
		return modelList;
	}
}
