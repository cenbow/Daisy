package com.yourong.backend.ic.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.common.cache.RedisProjectClient;
import com.yourong.common.constant.ActivityConstant;
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
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.model.AttachmentIndex;
import com.yourong.core.fin.dao.OverdueLogMapper;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.LoanDetailManager;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.manager.OverdueRepayLogManager;
import com.yourong.core.fin.manager.ProjectFeeManager;
import com.yourong.core.fin.manager.UnderwriteLogManager;
import com.yourong.core.fin.model.LoanDetail;
import com.yourong.core.fin.model.OverdueLog;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.fin.model.ProjectFee;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AttachmentThread;
import com.yourong.core.handle.DebtAttachmentHandle;
import com.yourong.core.handle.ProjectThumbnailsAttachmentHandle;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.LeaseBonusManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectInterestManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.DirectSettlementBiz;
import com.yourong.core.ic.model.LeaseBonus;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectBiz;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.ProjectInterest;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.sys.dao.SysDictMapper;
import com.yourong.core.sys.manager.SysOperateInfoManager;
import com.yourong.core.sys.manager.SysUserManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.sys.model.SysOperateInfo;
import com.yourong.core.sys.model.SysUser;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.ThirdCompanyManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.ThirdCompany;

@Service
public class ProjectServiceImpl implements ProjectService {
	private static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private DebtManager debtManager;

	@Autowired
	private ProjectInterestManager projectInterestManager;

	@Autowired
	private AttachmentIndexManager attachmentIndexManager;

	@Autowired
	private BalanceManager balanceManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	@Resource
	private ProjectThumbnailsAttachmentHandle projectThumbnailsAttachmentHandle;

	@Autowired
	private LeaseBonusManager leaseBonusManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private UnderwriteLogManager underWriteLogManager;
	
	@Autowired
	private OverdueLogManager overdueLogManager;
	
	@Autowired
	private OverdueLogMapper overdueLogMapper;
	
	@Autowired
	private DebtInterestManager debtInterestManager;
	
	@Autowired
	private LoanDetailManager loanDetailManager;
	
	@Autowired
	private ProjectFeeManager managementFeeManager;
	
	@Autowired
	private ThirdCompanyManager thirdCompanyManager;
	
	@Autowired
	private OverdueRepayLogManager overdueRepayLogManager;
	
	@Autowired
	private ProjectExtraManager projectExtraManager;
	
	@Autowired
	private SysUserManager sysUserManager;
	
	@Autowired
	private SysOperateInfoManager sysOperateInfoManager;
	
	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	
	@Autowired
	private DebtAttachmentHandle debtAttachmentHandle;
	
	@Autowired
	private SysDictMapper sysDictMapper;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;


	@Override
	public Page<Project> findByPage(Page<Project> pageRequest, Map<String, Object> map) {
		try {
			return projectManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询项目失败", e);
		}
		return null;
	}

	@Override
	public int updateProjectStatus(int newStatus, int currentStatus, Long projectId) {
		try {
			return projectManager.updateProjectStatus(newStatus, currentStatus, projectId);
		} catch (ManagerException e) {
			logger.error("更新项目状态失败", e);
		}
		return 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO addProject(Project project) throws ManagerException {
		ResultDO result = new ResultDO();
		try {
			// 检查债权有没有使用
			Debt debt = debtManager.findDebtBySerialNumber(project.getSerialNumber());
			if (debt != null && debt.getStatus() == StatusEnum.DEBT_STATUS_SAVE.getStatus()) {
				String thumbnail = project.getThumbnail();
				project.setThumbnail(null);// 保存的是阿里云地址，所以此处需要设置为空
				// 基础数据 赋值
				project.setInterestFrom(debt.getInterestFrom());// 起息日
				project.setDebtId(debt.getId());
				project.setRecommend(Constant.DISABLE);// 是否推荐
				project.setStatus(StatusEnum.PROJECT_STATUS_SAVE.getStatus());// 状态
				project.setDelFlag(Constant.ENABLE);
				project.setStartDate(debt.getStartDate());
				project.setEndDate(debt.getEndDate());
				project.setTotalAmount(debt.getAmount());
				project.setProfitType(debt.getReturnType());
				project.setProjectType(debt.getGuarantyType());
				project.setInvestType(ProjectEnum.PROJECT_TYPE_DEBT.getType());
				project.setCreateTime(DateUtils.getCurrentDateTime());
				project.setUpdateTime(DateUtils.getCurrentDateTime());
				project.setPublishTime(DateUtils.getCurrentDateTime());
				project.setInstalment(debt.getInstalment());

				Integer annualizedRateType = project.getAnnualizedRateType();

				// 递增收益
				if (annualizedRateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_INCREMENT.getStatus()) {
					annualizedRateTypeByIncrement(project, result);
				}

				// 阶梯收益
				if (annualizedRateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getStatus()) {
					// 阶梯收益 最大值和最小值写入主表。 为了简单，没有写更新方法去处理。
					List<ProjectInterest> projectInterestList = project.getProjectInterestList();
					if (Collections3.isNotEmpty(projectInterestList)) {
						// 最小收益
						BigDecimal minRate = projectInterestList.get(0).getAnnualizedRate();
						// 最大收益
						BigDecimal maxRate = projectInterestList.get(projectInterestList.size() - 1).getAnnualizedRate();
						project.setMinAnnualizedRate(minRate);
						project.setMaxAnnualizedRate(maxRate);
					}
				}
				
				// 保存项目
				projectManager.insertSelective(project);

				/*AttachmentInfo attinfo = new AttachmentInfo();
				attinfo.setKeyId(debt.getId().toString());
				attinfo.setBscAttachments(project.getBscAttachments());
				attinfo.setAppPath(appPath);
				attinfo.setOperation(AttachmentInfo.SAVE);
				taskExecutor.execute(new AttachmentThread(debtAttachmentHandle, attinfo));*/
				// 阶梯收益
				if (annualizedRateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getStatus()) {
					annualizedRateTypeByLadder(project, result);
					if (!result.isSuccess()) {
						throw new ManagerException(ResultCode.PROJECT_INTEREST_IS_NULL_ERROR.getMsg());
					}
				}

				// 保存附件索引
				List<AttachmentIndex> attachmentIndexList = project.getAttachmentIndexList();
				if (Collections3.isNotEmpty(attachmentIndexList)) {
					attachmentIndexManager.batchInsertAttachmentIndex(attachmentIndexList, project.getId().toString());
				}

				// 更新债权为 已使用 状态
				debtManager.updateDebtStatusById(StatusEnum.DEBT_STATUS_USED.getStatus(), debt.getId());

				// 写入余额
				balanceManager.insertBalance(TypeEnum.BALANCE_TYPE_PROJECT, project.getTotalAmount(), project.getId());

				// 写入缓存
				RedisProjectClient.setProjectBalance(project.getId(), project.getTotalAmount());

				if (StringUtil.isNotBlank(thumbnail)) {
					AttachmentInfo info = new AttachmentInfo();
					info.setKeyId(project.getId().toString());
					info.setAppPath(thumbnail);
					taskExecutor.execute(new AttachmentThread(projectThumbnailsAttachmentHandle, info));
				}

				// 是否参与租赁分红
				insertLeaseBonus(project, result);

				// 是否为特殊活动项目
				if (project.getActivitySign() != null && project.getActivitySign().intValue() > 0) {
					ProjectExtra projectExtra = new ProjectExtra();
					projectExtra.setProjectId(project.getId());
					projectExtra.setActivitySign(project.getActivitySign());
					projectExtraManager.insert(projectExtra);
				}
				// 更新项目本息的id
				List<DebtInterest> interests = debtInterestManager.findInterestsByDebtId(debt.getId());
				for (DebtInterest debtInterest : interests) {
					debtInterest.setProjectId(project.getId());
					debtInterestManager.updateByPrimaryKey(debtInterest);
				}
				
				
				result.setSuccess(true);
			} else {
				result.setResultCode(ResultCode.PROJECT_SERIAL_NUMBER_IS_NOT_ADD_ERROR);
			}
		} catch (ManagerException e) {
			logger.error("添加项目失败,债权编号：=" + project.getSerialNumber(), e);
			result.setResultCode(ResultCode.PROJECT_SAVE_FAIL_ERROR);
			throw new ManagerException(e);
		}
		return result;
	}

	/**
	 * 是否参与租赁分红
	 * 
	 * @param project
	 * @param result
	 * @throws ManagerException
	 */
	private void insertLeaseBonus(Project project, ResultDO<?> result) throws ManagerException {
		// 判断是否已插入租赁记录
		LeaseBonus leaseBonus = leaseBonusManager.findLeaseBonusByProjectId(project.getId());
		// 不参与租赁并且租赁记录为空
		if (project.getJoinLease().intValue() == StatusEnum.LEASE_BONUS_JOIN_LEASE.getStatus() && leaseBonus == null) {
			leaseBonus = new LeaseBonus();
			leaseBonus.setProjectId(project.getId());
			// 添加
			int leaseInsertRes = leaseBonusManager.insertSelective(leaseBonus);
			if (leaseInsertRes <= 0) {
				result.setResultCode(ResultCode.LEASE_BONUS_SAVE_ERROR);//
			}
			// 参与记录并且租赁记录不为空
		} else if (project.getJoinLease().intValue() == StatusEnum.LEASE_BONUS_NOT_JOIN_LEASE.getStatus() && leaseBonus != null) {
			// 删除
			int delRes = leaseBonusManager.deleteByPrimaryKey(leaseBonus.getId());
			if (delRes <= 0) {
				result.setResultCode(ResultCode.LEASE_BONUS_SAVE_ERROR);//
			}
		}

	}

	/**
	 * 递增收益计算
	 * 
	 * @param project
	 * @param result
	 */
	private void annualizedRateTypeByIncrement(Project project, ResultDO result) throws ManagerException {
		BigDecimal maxAnnualizedRate = project.getMaxAnnualizedRate();// 最大收益
		BigDecimal minAnnualizedRate = project.getMinAnnualizedRate();// 最小收益
		BigDecimal minInvestAmount = project.getMinInvestAmount();// 起投金额
		BigDecimal maxInvestAmount = project.getMaxInvestAmount();// 收益封顶
		BigDecimal incrementAmount = project.getIncrementAmount();// 递增单位金额
		// 数据值校验

		// 格式化收益存储
		// project.setMaxAnnualizedRate(maxAnnualizedRate.divide(new
		// BigDecimal(100),2,BigDecimal.ROUND_HALF_UP));
		// project.setMinAnnualizedRate(minAnnualizedRate.divide(new
		// BigDecimal(100),2,BigDecimal.ROUND_HALF_UP));

		// 计算递增率
		BigDecimal incrementAnnualizedRate = (maxAnnualizedRate.subtract(minAnnualizedRate)).divide(
				(maxInvestAmount.subtract(minInvestAmount)).divide(incrementAmount), 2, BigDecimal.ROUND_HALF_UP);
		project.setIncrementAnnualizedRate(incrementAnnualizedRate);
	}

	/**
	 * 阶梯收益计算
	 * 
	 * @param project
	 * @param result
	 * @throws ManagerException
	 */
	private void annualizedRateTypeByLadder(Project project, ResultDO result) throws ManagerException {
		List<ProjectInterest> projectInterestList = project.getProjectInterestList();
		if (Collections3.isNotEmpty(projectInterestList)) {
			// 校验阶梯收益值是否符合要求
			checkProjectInterestData(projectInterestList, project.getMinInvestAmount(), project.getIncrementAmount(),
					project.getTotalAmount());
			// 阶递收益
			projectInterestManager.batchInsertProjectInterest(projectInterestList, project.getId());
		} else {
			// 没有配置阶递收益，不可添加
			result.setResultCode(ResultCode.PROJECT_INTEREST_IS_NULL_ERROR);
		}
	}

	/**
	 * 校验阶梯收益值是否符合要求
	 * 
	 * @param projectInterestList
	 *            阶梯收益数据
	 * @param minInvestAmount
	 *            起投金额
	 * @param incrementAmount
	 *            递增单位金额
	 * @param totalAmount
	 *            总金额
	 * @return
	 * @throws ManagerException
	 */
	private boolean checkProjectInterestData(List<ProjectInterest> projectInterestList, BigDecimal minInvestAmount,
			BigDecimal incrementAmount, BigDecimal totalAmount) throws ManagerException {
		boolean checkFlag = false;
		BigDecimal investAmount = minInvestAmount;// 默认 起投金额
		int size = projectInterestList.size();
		int index = 1;// 索引
		for (ProjectInterest pi : projectInterestList) {
			if (pi.getMinInvest().remainder(incrementAmount).compareTo(BigDecimal.ZERO) != 0) {
				throw new ManagerException("第" + index + "行投资下限有误");
			}
			if (pi.getMaxInvest().remainder(incrementAmount).compareTo(BigDecimal.ZERO) != 0) {
				throw new ManagerException("第" + index + "行投资上限有误");
			}
			// 投资下限必须大于或等于起投金额、上一组金额
			// 投资上限必须大于投资下限且小于等于总金额
			if (pi.getMinInvest().compareTo(investAmount) < 0 || pi.getMaxInvest().compareTo(investAmount) < 0
					|| pi.getMaxInvest().compareTo(totalAmount) > 0) {
				throw new ManagerException("第" + index + "行投资下限或上限数据有误");
			}

			// 如果配置项大于1并且不是最后一项，上限必须大于下线
			if (size != 1 && size != index) {
				if (pi.getMaxInvest().compareTo(investAmount) <= 0) {
					throw new ManagerException("第" + index + "行投资上限数据有误");
				}
			}
			index++;
			investAmount = pi.getMaxInvest();
		}
		checkFlag = true;
		return checkFlag;
	}

	@Override
	public ProjectBiz findProjectById(Long projectId) {
		try {
			return projectManager.findProjectById(projectId);
		} catch (ManagerException e) {
			logger.error("查看项目失败,项目编号：=" + projectId, e);
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO updateProject(Project project) throws ManagerException {
		ResultDO result = new ResultDO();
		try {
			Project p = projectManager.selectByPrimaryKey(project.getId());
			if (p.getStatus() == StatusEnum.PROJECT_STATUS_SAVE.getStatus()) {// 只有存盘的状态下才可以编辑
				String thumbnail = project.getThumbnail();
				project.setThumbnail(p.getThumbnail());// 保存的是阿里云地址，所以此不需要保存新地址
				Debt debt = debtManager.findDebtBySerialNumber(project.getSerialNumber());
				if (debt != null) {
					if (!(debt.getId().toString().equals(p.getDebtId().toString()))) {// 如果不是同一个债权，需要判断责权是否已经被使用
						if (debt.getStatus() == 0) {
							// 更新债权为 已使用 状态
							debtManager.updateDebtStatusById(StatusEnum.DEBT_STATUS_USED.getStatus(), debt.getId());

							// 把现在的债权改为未使用
							debtManager.updateDebtStatusById(StatusEnum.DEBT_STATUS_SAVE.getStatus(), p.getDebtId());
						} else {
							result.setResultCode(ResultCode.PROJECT_SERIAL_NUMBER_IS_NOT_ADD_ERROR);
							return result;
						}
					}
					// 重设债权信息
					project.setInterestFrom(debt.getInterestFrom());
					project.setDebtId(debt.getId());
					project.setStartDate(debt.getStartDate());
					project.setEndDate(debt.getEndDate());
					project.setTotalAmount(debt.getAmount());
					project.setProfitType(debt.getReturnType());
					project.setProjectType(debt.getGuarantyType());
					project.setInstalment(debt.getInstalment());

					// 更新时间
					project.setUpdateTime(DateUtils.getCurrentDateTime());

					// 编辑时把数据先清空
					projectInterestManager.deleteProjectInterestByProjectId(p.getId());
					attachmentIndexManager.deleteAttachmentIndexByKeyId(p.getId().toString());

					Integer annualizedRateType = project.getAnnualizedRateType();
					// 递增收益
					if (annualizedRateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_INCREMENT.getStatus()) {
						annualizedRateTypeByIncrement(project, result);
					}

					// 阶梯收益
					if (annualizedRateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getStatus()) {
						// 阶梯收益 最大值和最小值写入主表。 为了简单，没有写更新方法去处理。
						List<ProjectInterest> projectInterestList = project.getProjectInterestList();
						if (Collections3.isNotEmpty(projectInterestList)) {
							// 最小收益
							BigDecimal minRate = projectInterestList.get(0).getAnnualizedRate();
							// 最大收益
							BigDecimal maxRate = projectInterestList.get(projectInterestList.size() - 1).getAnnualizedRate();
							project.setMinAnnualizedRate(minRate);
							project.setMaxAnnualizedRate(maxRate);
						}
					}

					// 保存项目
					project.setInvestType(ProjectEnum.PROJECT_TYPE_DEBT.getType());
					
					projectManager.updateByPrimaryKeySelective(project);
					// 阶梯收益
					if (annualizedRateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getStatus()) {
						annualizedRateTypeByLadder(project, result);
						if (!result.isSuccess()) {
							throw new ManagerException(ResultCode.PROJECT_INTEREST_IS_NULL_ERROR.getMsg());
						}
					}
					// 保存附件索引
					List<AttachmentIndex> attachmentIndexList = project.getAttachmentIndexList();
					if (Collections3.isNotEmpty(attachmentIndexList)) {
						attachmentIndexManager.batchInsertAttachmentIndex(attachmentIndexList, p.getId().toString());
					}

					// 更新余额
					balanceManager.resetProjectBalance(project.getTotalAmount(), project.getId());

					// 写入缓存
					RedisProjectClient.setProjectBalance(project.getId(), project.getTotalAmount());

					if (StringUtil.isNotBlank(thumbnail)) {
						if (StringUtil.isBlank(p.getThumbnail()) || !p.getThumbnail().equals(thumbnail)) {
							AttachmentInfo info = new AttachmentInfo();
							info.setKeyId(project.getId().toString());
							info.setAppPath(thumbnail);
							taskExecutor.execute(new AttachmentThread(projectThumbnailsAttachmentHandle, info));
						}
					}
					// 更新项目本息的id
					List<DebtInterest> interests = debtInterestManager.findInterestsByDebtId(debt.getId());
					for (DebtInterest debtInterest : interests) {
						debtInterest.setProjectId(project.getId());
						debtInterestManager.updateByPrimaryKey(debtInterest);
					}
					// 是否参与租赁分红
					insertLeaseBonus(project, result);
					// 记录项目信息被谁修改过
					logger.info("项目：" + project.getId() + "被" + p.getPublishId() + "用户修改");
				}
			} else {
				// 项目在存盘状态下才可以编辑
				result.setResultCode(ResultCode.PROJECT_STATUS_IS_NOT_SAVE_ERROR);
			}

		} catch (ManagerException e) {
			logger.error("更新项目失败：" + project.getId(), e);
			result.setResultCode(ResultCode.PROJECT_UPDATE_FAIL_ERROR);
			throw new ManagerException(e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO emergencyUpdateProject(Project project) throws ManagerException {
		ResultDO result = new ResultDO();
		try {
			Project p = projectManager.selectByPrimaryKey(project.getId());
			String thumbnail = project.getThumbnail();
			project.setThumbnail(p.getThumbnail());// 保存的是阿里云地址，所以此不需要保存新地址
			Debt debt = debtManager.findDebtBySerialNumber(project.getSerialNumber());
			if (debt != null) {
				if (!(debt.getId().toString().equals(p.getDebtId().toString()))) {// 如果不是同一个债权，需要判断责权是否已经被使用
					if (debt.getStatus() == 0) {
						// 更新债权为 已使用 状态
						debtManager.updateDebtStatusById(StatusEnum.DEBT_STATUS_USED.getStatus(), debt.getId());

						// 把现在的债权改为未使用
						debtManager.updateDebtStatusById(StatusEnum.DEBT_STATUS_SAVE.getStatus(), p.getDebtId());
					} else {
						result.setResultCode(ResultCode.PROJECT_SERIAL_NUMBER_IS_NOT_ADD_ERROR);
						return result;
					}
				}
				// 重设债权信息
				project.setInterestFrom(debt.getInterestFrom());
				project.setDebtId(debt.getId());
				project.setStartDate(debt.getStartDate());
				project.setEndDate(debt.getEndDate());
				project.setTotalAmount(debt.getAmount());
				project.setProfitType(debt.getReturnType());
				project.setProjectType(debt.getGuarantyType());

				// 更新时间
				project.setUpdateTime(DateUtils.getCurrentDateTime());

				// 编辑时把数据先清空
				projectInterestManager.deleteProjectInterestByProjectId(p.getId());
				attachmentIndexManager.deleteAttachmentIndexByKeyId(p.getId().toString());

				Integer annualizedRateType = project.getAnnualizedRateType();
				// 递增收益
				if (annualizedRateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_INCREMENT.getStatus()) {
					annualizedRateTypeByIncrement(project, result);
				}

				// 阶梯收益
				if (annualizedRateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getStatus()) {
					// 阶梯收益 最大值和最小值写入主表。 为了简单，没有写更新方法去处理。
					List<ProjectInterest> projectInterestList = project.getProjectInterestList();
					if (Collections3.isNotEmpty(projectInterestList)) {
						// 最小收益
						BigDecimal minRate = projectInterestList.get(0).getAnnualizedRate();
						// 最大收益
						BigDecimal maxRate = projectInterestList.get(projectInterestList.size() - 1).getAnnualizedRate();
						project.setMinAnnualizedRate(minRate);
						project.setMaxAnnualizedRate(maxRate);
					}
				}

				// 保存项目
				project.setInvestType(ProjectEnum.PROJECT_TYPE_DEBT.getType());
				
				projectManager.updateByPrimaryKeySelective(project);
				// 阶梯收益
				if (annualizedRateType == StatusEnum.PROJECT_ANNUALIZED_RATE_TYPE_LADDER.getStatus()) {
					annualizedRateTypeByLadder(project, result);
					if (!result.isSuccess()) {
						throw new ManagerException(ResultCode.PROJECT_INTEREST_IS_NULL_ERROR.getMsg());
					}
				}
				// 保存附件索引
				List<AttachmentIndex> attachmentIndexList = project.getAttachmentIndexList();
				if (Collections3.isNotEmpty(attachmentIndexList)) {
					attachmentIndexManager.batchInsertAttachmentIndex(attachmentIndexList, p.getId().toString());
				}

				// 更新余额
				// balanceManager.resetProjectBalance(project.getTotalAmount(),
				// project.getId());

				// 写入缓存
				RedisProjectClient.setProjectBalance(project.getId(), project.getTotalAmount());

				if (StringUtil.isNotBlank(thumbnail)) {
					if (StringUtil.isBlank(p.getThumbnail()) || !p.getThumbnail().equals(thumbnail)) {
						AttachmentInfo info = new AttachmentInfo();
						info.setKeyId(project.getId().toString());
						info.setAppPath(thumbnail);
						taskExecutor.execute(new AttachmentThread(projectThumbnailsAttachmentHandle, info));
					}
				}
				// 更新项目本息的id
				List<DebtInterest> interests = debtInterestManager.findInterestsByDebtId(debt.getId());
				for (DebtInterest debtInterest : interests) {
					debtInterest.setProjectId(project.getId());
					debtInterestManager.updateByPrimaryKey(debtInterest);
				}
				// 是否参与租赁分红
				insertLeaseBonus(project, result);// 紧急修改债权是不允许修改
				// 记录项目信息被谁修改过
				logger.info("项目：" + project.getId() + "被" + p.getPublishId() + "用户修改");
			}
		} catch (ManagerException e) {
			logger.error("更新项目失败：" + project.getId(), e);
			result.setResultCode(ResultCode.PROJECT_UPDATE_FAIL_ERROR);
			throw new ManagerException(e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteProjectById(Long id) throws ManagerException {
		int delFlag = 0;
		try {
			Project p = projectManager.selectByPrimaryKey(id);
			if (p != null && p.getStatus() == StatusEnum.PROJECT_STATUS_SAVE.getStatus()) {// 只有存盘的状态下才可以删除
				delFlag = projectManager.deleteProjectById(p.getId());
				// 如果是租赁分红项目删除租赁分红
				LeaseBonus leaseBonus = leaseBonusManager.findLeaseBonusByProjectId(p.getId());
				if (p.getJoinLease().intValue() == StatusEnum.LEASE_BONUS_JOIN_LEASE.getStatus() && leaseBonus != null) {
					leaseBonusManager.deleteByPrimaryKey(leaseBonus.getId());
				}
				// 删除后，把债权设置为可用
				debtManager.updateDebtStatusById(StatusEnum.DEBT_STATUS_SAVE.getStatus(), p.getDebtId());
			}
		} catch (ManagerException e) {
			logger.error("删除项目：" + id + "失败", e);
			throw new ManagerException(e);
		}
		return delFlag;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int waitReviewProject(Long id, Long auditId) throws ManagerException {
		int actionFlag = 0;
		try {

			int newStatus = StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_SAVE.getStatus();
			;
			actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, null);
		} catch (ManagerException e) {
			logger.error("项目：" + id + "提交待审失败");
			throw new ManagerException(e);
		}
		return actionFlag;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int reviewProject(Long id, Long auditId, String msg) throws ManagerException {
		int actionFlag = 0;
		try {
			int newStatus = StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus();
			actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msg);
			// 项目审核成功，扣除用户的可用余额
			if (actionFlag > 0) {
				ProjectBiz project = projectManager.findProjectById(id);
				if (project.getProjectType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode())
						&& project.getDebtBiz().getEnterpriseId() != null) {
					balanceManager.reduceBalance(TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT, project.getTotalAmount(), project
							.getDebtBiz().getEnterpriseId(), BalanceAction.balance_Available_subtract);
				}
			}
		} catch (ManagerException e) {
			logger.error("项目：" + id + "审核失败");
			throw new ManagerException(e);
		}
		return actionFlag;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int releaseProject(Long id, Long auditId, String msg) throws ManagerException {
		int actionFlag = 0;
		try {
			int newStatus = StatusEnum.PROJECT_STATUS_INVESTING.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus();
			actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msg);
			// 插入余额数据
		} catch (ManagerException e) {
			logger.error("项目：" + id + "发布失败");
			throw new ManagerException(e);
		}
		return actionFlag;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int stopProject(Long id, Long auditId, String msg) throws ManagerException {
		int actionFlag = 0;
		try {
			int newStatus = StatusEnum.PROJECT_STATUS_STOP.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_INVESTING.getStatus();
			actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msg);
		} catch (ManagerException e) {
			logger.error("项目：" + id + "暂停失败");
			throw new ManagerException(e);
		}
		return actionFlag;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int startProject(Long id, Long auditId, String msg) throws ManagerException {
		int actionFlag = 0;
		try {
			int newStatus = StatusEnum.PROJECT_STATUS_INVESTING.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_STOP.getStatus();
			actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msg);
		} catch (ManagerException e) {
			logger.error("项目：" + id + "恢复状态到投资中失败");
			throw new ManagerException(e);
		}
		return actionFlag;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int fallbackSaveStatus(Long id, Long auditId, String msg) throws ManagerException {
		int actionFlag = 0;
		try {
			int newStatus = StatusEnum.PROJECT_STATUS_SAVE.getStatus();
			int currentStatus = StatusEnum.PROJECT_STATUS_WAIT_REVIEW.getStatus();
			actionFlag = modifyProjectStatus(newStatus, currentStatus, id, auditId, msg);
		} catch (ManagerException e) {
			logger.error("项目：" + id + "回退到存盘状态失败");
			throw new ManagerException(e);
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
			p.setAuditId(auditId);
			p.setAuditTime(DateUtils.getCurrentDateTime());
			p.setUpdateTime(DateUtils.getCurrentDateTime());
			p.setId(project.getId());
			p.setAuditMessage(msg);
			projectManager.updateByPrimaryKeySelective(p);
			logger.info("项目：" + project.getId() + ",由操作员" + auditId + "将状态【" + currentStatus + "】改为【" + newStatus + "】");
		}
		return actionFlag;
	}

	@Override
	public boolean checkNameExists(String name, String namePeriod) {
		try {
			return projectManager.checkNameExists(name, namePeriod);
		} catch (ManagerException e) {
			logger.error("项目当前期数" + namePeriod + "已存在或项目名称：" + name + "已经存在");
		}
		return true;
	}

	@Override
	public boolean checkNameExists(String name, String namePeriod, Long id) {
		try {
			return projectManager.checkNameExists(name, namePeriod, id);
		} catch (ManagerException e) {
			logger.error("项目当前期数" + namePeriod + "已存在或项目名称：" + name + "已经存在");
		}
		return true;
	}

	@Override
	public ResultDO updateOnlineTimeAndEndDate(Date onlineTime, Date saleEndTime, Long id) {
		ResultDO result = new ResultDO();
		try {
			int row = projectManager.updateOnlineTimeAndEndDate(new Timestamp(onlineTime.getTime()), new Timestamp(
					saleEndTime.getTime()), id);
			if (row <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public ResultDO updateEndDate(Date saleEndTime, Long id) {
		ResultDO result = new ResultDO();
		try {
			int row = projectManager.updateEndDate(new Timestamp(saleEndTime.getTime()), id);
			if (row <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public int findProjectCountByStatus(Integer status) {
		try {
			return projectManager.findProjectCountByStatus(status);
		} catch (Exception e) {
			logger.error("获取项目个数根据项目状态失败，status=" + status, e);
		}
		return 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Object> addControlRemarks(Long id, String newControlRemarks) throws ManagerException {
		ResultDO<Object> resultDO = new ResultDO<>();
		try {
			Project Project = new Project();
			Project.setId(id);
			Project.setRemarks(newControlRemarks);
			int returnRowNum = projectManager.updateByPrimaryKeySelective(Project);
			if (returnRowNum > 0) {
				resultDO.setSuccess(true);
			} else {
				resultDO.setSuccess(false);
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_DATABASE_UPDATE);
			}
		} catch (Exception e) {
			logger.debug("项目备注更新失败！ id=" + id, e);
			throw new ManagerException(e);
		}
		return resultDO;
	}
	/**
	 * 分页查询放款管理列表
	 * @desc TODO
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月4日 上午10:38:38
	 *
	 */
	@Override
	public Page<DirectProjectBiz> selectLoanForPagin(Page<DirectProjectBiz> pageRequest, Map<String, Object> map) {
		try {
			return projectManager.selectLoanForPagin(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询放款管理列表失败", e);
		}
		return null;
	}
	/**
	 * 
	 * 根据id查询项目
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月4日 下午5:57:49
	 *
	 */
	@Override
	public ProjectInterestBiz selectProjectInfoById(Long id)  {
		ProjectInterestBiz projectBiz=new ProjectInterestBiz();
		try {
			Project project= projectManager.selectByPrimaryKey(id);
			if(project!=null){
				projectBiz.setTotalAmount(project.getTotalAmount());
				projectBiz.setId(project.getId());
				projectBiz.setManageFeeRate(project.getManageFeeRate());
				projectBiz.setGuaranteeFeeRate(project.getGuaranteeFeeRate());
				projectBiz.setRiskFeeRate(project.getRiskFeeRate());
				projectBiz.setIntroducerFeeRate(project.getIntroducerFeeRate());
				projectBiz.setOriginalProjectNumber(project.getOriginalProjectNumber());
				projectBiz.setProjectName(project.getName());
				projectBiz.setSinaAccount(SerialNumberUtil.PREFIX_UID+project.getBorrowerId());
				Member member=memberManager.selectByPrimaryKey(project.getBorrowerId());
				if(member!=null){
					projectBiz.setMobile(member.getMobile());
					projectBiz.setBorrowerName(member.getTrueName());
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
				projectBiz.setQuickReward(ratioAmount);
			}
		} catch (ManagerException e) {
			logger.error("查看项目失败,项目id：=" + id, e);
		}
		return projectBiz;
	}
	/**
	 * 
	 * @desc 放款
	 * @param projectBiz
	 * @return
	 * @author chaisen
	 * @time 2016年1月5日 上午9:56:27
	 *
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Project> saveLoanInfo(Project project) {
		ResultDO<Project> resultDO = new ResultDO<Project>();
		try {
			Project p = projectManager.selectByPrimaryKey(project.getId());
			if (p != null && p.getStatus() == StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()) {//待放款状态才可以放款
				Project Nproject=new Project();
				Nproject.setId(project.getId());
				Nproject.setRemarks(project.getRemarks());
				int i=projectManager.updateByPrimaryKeySelective(Nproject);
				if(i>0){
					//放款表插入放款记录
					LoanDetail loan=new LoanDetail();
					loanDetailManager.insertSelective(loan);
					resultDO.setSuccess(true);
				}
			}else{
				resultDO.setResultCode(ResultCode.PROJECT_NOT_FULL_STATUS_ERROR);
				resultDO.setSuccess(false);
			}
		} catch (ManagerException e) {
			logger.error("放款失败,项目：=" + project, e);
		}
		return resultDO;
	}
	/**
	 * 
	 * @desc 分页查询还本付息列表
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author chaisen
	 * @time 2016年1月6日 上午9:56:19
	 *
	 */
	@Override
	public Page<ProjectInterestBiz> findRepayInterestForPagin(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map) {
		try {
			return projectManager.findRepayInterestForPagin(pageRequest, map); 
		} catch (Exception e) {
			logger.error("获取还本付息列表失败",e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 获取垫资代付信息
	 * @param id
	 * @return
	 * @author chaisen
	 * @time 2016年1月6日 下午5:21:19
	 *
	 */
	@Override
	public ProjectInterestBiz selectInterestInfoByPrimaryKey(Long id) {
		try {
			return projectManager.selectInterestInfoByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("获取垫资代付信息失败", e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 垫资代还
	 * @param project
	 * @return
	 * @author chaisen
	 * @throws ManagerException 
	 * @time 2016年1月12日 下午2:09:10
	 *
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<ProjectInterestBiz> saveRepayment(ProjectInterestBiz project) throws Exception {
		ResultDO<ProjectInterestBiz> resultDO = new ResultDO<ProjectInterestBiz>();
			if(projectManager.checkIsExistUnderwriteLog(project.getInterestId())){
				resultDO.setResultCode(ResultCode.UNDER_WRITE_LOG_EXIST_ERROR);
				return resultDO;
			}
			int j=overdueLogMapper.getCountOverdueRecordByProjectId(project.getProjectId());
			if(j>0){
				resultDO.setResultCode(ResultCode.PROJECT_OVERDUE_NOPAY);
				return resultDO;
			}
			//第三方账户余额
			BigDecimal amount=projectManager.getThirdAccountMoney(project.getThirdMemberId());
			if(amount.compareTo(project.getTotalPayAmount())<0){
				resultDO.setResultCode(ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING_SELECTOTHER);
				return resultDO;
			}
			//生成垫资记录
			int i=underWriteLogManager.saveUnderWriteLog(project);
				//生成逾期记录
				//int j=overdueLogManager.saveOverdueLog(project);
				if(i<1){
					logger.error("设置垫资人失败",project );
					resultDO.setSuccess(false);
					throw new ManagerException("设置垫资人失败");
				}
			return resultDO;
	}
	/**
	 * 
	 * @desc 查询余额
	 * @param memberId
	 * @return
	 * @author chaisen
	 * @time 2016年1月7日 下午8:15:18
	 *
	 */
	@Override
	public BigDecimal getThirdAccountMoney(Long memberId) {
		try {
			return projectManager.getThirdAccountMoney(memberId);
		} catch (Exception e) {
			logger.debug("根据memberId查询余额失败,memberId={}", memberId, e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 获取垫资标记
	 * @param id
	 * @return
	 * @author chaisen
	 * @time 2016年1月7日 下午8:15:07
	 *
	 */
	@Override
	public ProjectInterestBiz getFlagInfo(Long id) {
		try {
			return projectManager.getFlagInfo(id);
		} catch (ManagerException e) {
			logger.error("获取垫资标记失败", e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 获取逾期还款记录
	 * @param id
	 * @return
	 * @author chaisen
	 * @time 2016年1月7日 下午8:14:54
	 *
	 */
	@Override
	public ProjectInterestBiz getOverdueInfo(Long id) {
		try {
			return projectManager.getOverdueInfo(id);
		} catch (ManagerException e) {
			logger.error("获取逾期还款记录失败", e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 逾期还款标记
	 * @param project
	 * @return
	 * @author chaisen
	 * @throws ManagerException 
	 * @time 2016年1月7日 下午8:53:54
	 *
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<ProjectInterestBiz> saveRepayFlagInterest(ProjectInterestBiz project) throws Exception {
		ResultDO<ProjectInterestBiz> resultDO = new ResultDO<ProjectInterestBiz>();
		List<ProjectInterestBiz> list = Lists.newArrayList();
			if(project.getOverdueInterest() == null || BigDecimal.ZERO.compareTo(project.getOverdueInterest())==0){
				resultDO.setResultCode(ResultCode.PROJECT_OVERDUE_NO_AMOUNT);
				return resultDO;
			}
			Map<String, Object> mapRecord = Maps.newHashMap();
			mapRecord.put("projectId", project.getProjectId());
			mapRecord.put("startDate", DateUtils.addDate(project.getRefundTime(), 1));
			mapRecord.put("status", StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
			//逾期记录
			int overduecount = overdueLogManager.findOverdueByProjectIdTotalCount(mapRecord);
			if(overduecount<1){
				resultDO.setResultCode(ResultCode.PROJECT_OVERDUE_NO_AMOUNT);
				return resultDO;
			}
				String overdueId="";
				Map<String, Object> map = Maps.newHashMap();
				map.put("projectId", project.getProjectId());
				map.put("endDate", project.getRefundTime());
				map.put("overdueStatus", StatusEnum.OVERDUE_LOG_PAYING.getStatus());
				list=debtInterestManager.getOverdueInfoListByMap(map);
				//判断是否有还款中的逾期记录
				if(Collections3.isNotEmpty(list)){
					resultDO.setResultCode(ResultCode.PROJECT_OVERDUE_PAYING);
					return resultDO;
				}
				map.put("overdueStatus", StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
				list=debtInterestManager.getOverdueInfoListByMap(map);
				if(Collections3.isNotEmpty(list)){
					for(ProjectInterestBiz biz:list){
						//更新逾期记录表
						OverdueLog overdueLog=new OverdueLog(); 
						overdueLog.setId(biz.getOverdueId());
						overdueLog.setStatus(StatusEnum.OVERDUE_LOG_HAD_PAY.getStatus());
						overdueLog.setUpdateTime(new Date());
						overdueLog.setEndDate(new Date());
						overdueLogManager.updateByPrimaryKeySelective(overdueLog);
						//更新项目本息表
						DebtInterest interest=new DebtInterest();
						interest.setId(biz.getInterestId());
						interest.setStatus(StatusEnum.DEBT_INTEREST_ALL_PAYED.getStatus());
						debtInterestManager.updateByPrimaryKeySelective(interest);
						//更新垫资记录表
						underWriteLogManager.updateUnderWriteByInterestId(biz.getInterestId());
						overdueId=overdueId+biz.getOverdueId()+",";
						
					}
			}
		    project.setOverdueIds(overdueId);
		    //保存逾期还款记录
			int i=overdueRepayLogManager.saveOverdueRepayLog(project);
			if(i<1){
				logger.error("逾期还款失败", project.toString());
				resultDO.setSuccess(false);
				throw new ManagerException("逾期还款失败");
			}
			return resultDO;
			
	}

	@Override
	public Page<ProjectFee> selectManageFeeForPagin(Page<ProjectFee> pageRequest, Map<String, Object> map) {
		try {
			return managementFeeManager.selectManageFeeForPagin(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询管理费列表失败", e);
		}
		return null;
	}

	@Override
	public ProjectInterestBiz showProject(Long id) {
		try {
			return projectManager.showProject(id);
		} catch (ManagerException e) {
			logger.error("查询项目基本信息失败", e);
		}
		return null;
	}

	@Override
	public Page<ProjectInterestBiz> showProjectInterest(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map) {
		try {
			List<DebtInterest> interestList = debtInterestManager.findInterestlistByProjectId((Long)map.get("projectId"));
			List<ProjectInterestBiz> bizList = getInterestInfo(interestList, map.get("isMark"));
			pageRequest.setData(bizList);
			return pageRequest;
		} catch (ManagerException e) {
			logger.error("查询本息记录列表失败", e);
		}
		return null;
	}
	/**
	 * 
	 * @Description:查询本息记录中 本金和利息之和
	 * @param interestList
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年2月24日 上午9:34:56
	 */
	private List<ProjectInterestBiz> getInterestInfo(List<DebtInterest> interestList, Object isMark) throws ManagerException {
		List<ProjectInterestBiz> bizs = Lists.newArrayList();
		if(Collections3.isNotEmpty(interestList)){
			for(DebtInterest interest:interestList){
				ProjectInterestBiz projectBiz = new ProjectInterestBiz();
				BeanCopyUtil.copy(interest, projectBiz);
				Map<String, Object> map = Maps.newHashMap();
				map.put("interestId", interest.getId());
				BigDecimal principal = BigDecimal.ZERO;
				BigDecimal interests = BigDecimal.ZERO;
				
				int countOverdue = overdueLogManager.countOverdueRecordByInterestId(map);
				// 查询垫资还款标记本息记录
				if (map != null && isMark != null && 1 == Integer.parseInt(isMark.toString())) {
					// 如果项目本息未支付，则不展示
					if (StatusEnum.DEBT_INTEREST_ALL_PAYED.getStatus() != interest.getStatus()) {
						continue;
					}
					// 判断如果当前项目没有存在垫资逾期记录，则不展示
					map.put("type", TypeEnum.OVERDUE_SETTLEMENT_TYPE_UNDERWRITE.getType());
					if (countOverdue < 1) {
						continue;
					}
					// 如果垫资还款并已支付，则不展示
					map.put("status", 2);
					int isExistsPayed = overdueLogManager.countOverdueRecordByInterestId(map);
					if (isExistsPayed > 0) {
						continue;
					}
				}
				
				//待支付
				if(interest.getStatus()==0){
					projectBiz.setOverdueStatus(StatusEnum.INTEREST_OVERDUE_WAIT_PAY.getStatus());
				//已支付或支付中
				}else{
					//正常还款
					if(countOverdue<1){
						projectBiz.setOverdueStatus(0);
					}else{
						map.put("interestId", interest.getId());
						map.put("status", 1);
						int count=overdueLogManager.countOverdueRecordByInterestId(map);
						map.put("status", 2);
						int counts=overdueLogManager.countOverdueRecordByInterestId(map);
						if(count>0){
							projectBiz.setOverdueStatus(1);
						}else if(counts>0){
							projectBiz.setOverdueStatus(2);
						}else{
							projectBiz.setOverdueStatus(3);
						}
					}
				}
				if(interest.getPayablePrincipal()!=null){
					principal=interest.getPayablePrincipal();
				}
				if(interest.getPayableInterest()!=null){
					interests=interest.getPayableInterest();
				}
				if(principal.compareTo(BigDecimal.ZERO)>0&&interests.compareTo(BigDecimal.ZERO)>0){
					projectBiz.setPayType(1);
				}
				if(principal.compareTo(BigDecimal.ZERO)==0&&interests.compareTo(BigDecimal.ZERO)>0){
					projectBiz.setPayType(2);
				}
				projectBiz.setTotalPayAmount(principal.add(interests));
				bizs.add(projectBiz);
			}
		}
		return bizs;
	}

	@Override
	public Page<ProjectInterestBiz> findOverdueList(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map) {
		try {
			return projectManager.findOverdueList(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询垫资管理列表失败", e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 同步第三方垫资人余额
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月2日 下午5:22:44
	 *
	 */
	@Override
	public Object synchronizedBalance() throws ManagerException {
		List<ThirdCompany>  listCompany=thirdCompanyManager.getAllThirdCompany();
		if(Collections3.isNotEmpty(listCompany)){
			for(ThirdCompany company:listCompany){
					balanceManager.synchronizedBalance(company.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
			}
		}
		return null;
	}
	/**
	 * 
	 * @desc  查询逾期还款记录
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author chaisen
	 * @time 2016年1月28日 下午1:19:04
	 *
	 */
	@Override
	public Page<OverdueRepayLog> getOverdueRecord(Page<OverdueRepayLog> pageRequest, Map<String, Object> map) {
		try {
			List<OverdueRepayLog> recordList=projectManager.getOverdueRepayLogRecordByProjectId((Long)map.get("projectId"));
			if(Collections3.isNotEmpty(recordList)){
				recordList=overdueRepayLogInfo(recordList);
			}
			pageRequest.setData(recordList);
			return pageRequest;
		} catch (ManagerException e) {
			logger.error("查询逾期还款记录列表失败", e);
		}
		return null;
	}
	private List<OverdueRepayLog> overdueRepayLogInfo(List<OverdueRepayLog> recordList) throws ManagerException {
		List<OverdueRepayLog> list=Lists.newArrayList();
		for(OverdueRepayLog bean:recordList){
			Map<String, Object> map = Maps.newHashMap();
			map.put("sourceId", bean.getId());
			map.put("operateTableType", TypeEnum.OPERATE_OVERDUE_REPAYMENT.getType());
			map.put("operateCode", TypeEnum.OPERATE_OVERDUE_REPAYMENT.getCode());
			SysOperateInfo sysOperateInfo=sysOperateInfoManager.selectOperateBySourceId(map);
			if(sysOperateInfo!=null){
				SysUser user=sysUserManager.selectByPrimaryKey(sysOperateInfo.getOperateId());
				if(user!=null){
					bean.setOparateName(user.getLoginName());
				}
			}
			list.add(bean);
		}
		return list;
	}

	/**
	 * 
	 * @desc TODO 获取滞纳金
	 * @param projectId
	 * @param repayDate
	 * @return
	 * @author chaisen
	 * @time 2016年1月28日 下午4:34:25
	 *
	 */
	@Override
	public ProjectInterestBiz getOverdueAmount(Long projectId, String repayDate) {
		try {
			return projectManager.getOverdueAmount(projectId, repayDate);
		} catch (ManagerException e) {
			logger.error("获取滞纳金失败,projectId={}", projectId, e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 根据违约金计算总额
	 * @param payableAmount
	 * @param overdueFine
	 * @return
	 * @author chaisen
	 * @time 2016年2月1日 下午1:18:33
	 *
	 */
	@Override
	public BigDecimal getPayableAmount(BigDecimal payableAmount, BigDecimal overdueFine) {
		return payableAmount.add(overdueFine);
	}

	@Override
	public Page<ProjectInterestBiz> findProjectInterest(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map) {
		try {
			List<DebtInterest> interestList=debtInterestManager.findInterestlistByProjectId((Long)map.get("projectId"));
			List<ProjectInterestBiz> bizList=getProjectInterestInfo(interestList);
			pageRequest.setData(bizList);
			return pageRequest;
		} catch (ManagerException e) {
			logger.error("查询本息记录列表失败", e);
		}
		return null;
	}

	private List<ProjectInterestBiz> getProjectInterestInfo(List<DebtInterest> interestList) {
		List<ProjectInterestBiz> bizs = Lists.newArrayList();
		if(Collections3.isNotEmpty(interestList)){
			for(DebtInterest interest:interestList){
				ProjectInterestBiz projectBiz = new ProjectInterestBiz();
				projectBiz.setStartDate(interest.getStartDate());
				projectBiz.setEndDate(interest.getEndDate());
				projectBiz.setStatus(interest.getStatus());
				projectBiz.setPayablePrincipal(interest.getPayablePrincipal());
				projectBiz.setPayableInterest(interest.getPayableInterest());
				projectBiz.setRealPayPrincipal(interest.getRealPayPrincipal());
				projectBiz.setRealPayInterest(interest.getRealPayInterest());
				bizs.add(projectBiz);
			}
		}
		return bizs;
	}

	@Override
	public void loseProject(Long projectId) {
		try {
			projectManager.loseProject(projectId);
		} catch (ManagerException e) {
			logger.error("项目流标创建退款失败，projectId={}",projectId);
		}
	}
	

	@Override
	public ResultDO<?> createHostPayForOverdueRepayByCollectTradeNo(String collectTradeNo) {
		try {
			return projectManager.createHostPayForOverdueRepayByCollectTradeNo(collectTradeNo);
		} catch (Exception e) {
			logger.error("垫资代付异常处理接口调用失败，collectTradeNo={}",collectTradeNo);
		}
		return null;
	}

	@Override
	public ResultDO setSaveProject(Long id, Long operateId, String msg) {
		ResultDO result = new ResultDO();
		try {
			int currentStatus = StatusEnum.PROJECT_STATUS_WAIT_RELEASE.getStatus();
			Project project = projectManager.selectByPrimaryKey(id);
			if(project!=null&&currentStatus==project.getStatus()){
				Project pt=new Project();
				pt.setId(id);
				pt.setStatus(StatusEnum.PROJECT_STATUS_SAVE.getStatus());
				pt.setUpdateTime(DateUtils.getCurrentDate());
				int i=projectManager.updateByPrimaryKeySelective(pt);
				if(i>0){
					sysOperateInfoManager.saveOperateInfo(id, TypeEnum.OPERATE_TYPE_PROJECT.getType(), operateId, TypeEnum.OPERATE_PROJECT_SET.getDesc(),TypeEnum.OPERATE_PROJECT_SET.getCode(),msg);
					result.setSuccess(true);
				}
			}else{
				result.setResultCode(ResultCode.PROJECT_WAIT_RELEASE_NOT_EXITS);
			}
		} catch (ManagerException e) {
			logger.error("项目：" + id + "置为存盘失败");
		}
		return result;
	}

	@Override
	public ResultDO<Debt> getSerialNumber(Long projectId) {
		ResultDO<Debt> resultDO = new ResultDO<Debt>();
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			if(project!=null){
				Debt debt = debtManager.selectByPrimaryKey(project.getDebtId());
				if(debt!=null){
					resultDO.setSuccess(true);
					resultDO.setResult(debt);
					return resultDO;
				}
				
			}
		} catch (ManagerException e) {
			logger.error("查询失败，projectId={}",projectId);
		}
		
		return resultDO;
	}
	/**
	 * 
	 * @desc 直投项目营收结算
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author chaisen
	 * @time 2016年5月6日 下午4:32:06
	 *
	 */
	@Override
	public Page<DirectSettlementBiz> findDirectSettlementByPage(Page<DirectSettlementBiz> pageRequest, Map<String, Object> map) {
		try {
			return projectManager.findDirectSettlementByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询直投项目营收结算列表失败", e);
		}
		return null;
	}

	@Override
	public Page<ProjectInterestBiz> findNormalOverdueList(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map) {
		try {
			return projectManager.findNormalOverdueList(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询逾期放款管理列表失败", e);
		}
		return null;
	}

	@Override
	public ResultDO<HostingCollectTrade> createCollectTradeForGuaranteeFee(Long projectId) {
		ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
		try {
			if(projectId==null){
				result.setSuccess(false);
				return result;
			}
			Project project = projectManager.selectByPrimaryKey(projectId);
			result = projectManager.createCollectTradeForGuaranteeFee(project);
		} catch (Exception e) {
			logger.error("分页查询逾期放款管理列表失败", e);
		}
		return result;
	}

	@Override
	public ResultDO<HostingPayTrade> createPayTradeByCollectTradeNoForGuaranteeFee(String collectTradeNo) {
		ResultDO<HostingPayTrade> result = new ResultDO<HostingPayTrade>();
		try {
			if(StringUtil.isEmpty(collectTradeNo)){
				result.setSuccess(false);
				return result;
			}
			HostingCollectTrade collectTrade = hostingCollectTradeManager.getByTradeNoAndType(collectTradeNo,TypeEnum.HOSTING_COLLECT_TRADE_PROJECT_GUARNANTEEFEE.getType());
			if(collectTrade==null){
				result.setSuccess(false);
				return result;
			}
			Project project = projectManager.selectByPrimaryKey(collectTrade.getProjectId());
			HostingPayTrade hostingPayTrade = projectManager.createLocalHostPayForGuaranteeFee(collectTrade,project);
			if(hostingPayTrade==null){
				result.setSuccess(false);
				return result;
			}
			projectManager.createSinpayHostingPayTradeForGuaranteeFee(hostingPayTrade);
			result.setResult(hostingPayTrade);
		} catch (Exception e) {
			logger.error("分页查询逾期放款管理列表失败", e);
		}
		return result;
	}

	@Override
	public SysDict selectByGroupNameAndValue(String channelBusiness) {
		return sysDictMapper.selectByGroupNameAndValue(channelBusiness);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<ProjectInterestBiz> toUnderWriteRepay(ProjectInterestBiz project) throws Exception {
		ResultDO<ProjectInterestBiz> resultDO = new ResultDO<ProjectInterestBiz>();
		if(project.getOverdueInterest() == null || BigDecimal.ZERO.compareTo(project.getOverdueInterest())==0){
			resultDO.setResultCode(ResultCode.PROJECT_OVERDUE_NO_AMOUNT);
			return resultDO;
		}
		// 判断是否为债权项目
		Project p = projectManager.selectByPrimaryKey(project.getProjectId());
		if(p != null) {
			if (ProjectEnum.PROJECT_TYPE_DEBT.getType() != p.getInvestType()) {
				resultDO.setResultCode(ResultCode.PROJECT_INVEST_TYPE_NO_DEBT_ERROR);
				return resultDO;
			}
		} else {
			resultDO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
			return resultDO;
		}
		
		Map<String, Object> mapRecord = Maps.newHashMap();
		mapRecord.put("projectId", project.getProjectId());
		mapRecord.put("startDate", project.getRefundTime());
		mapRecord.put("status", StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
		// 逾期记录
		int overduecount = overdueLogManager.findOverdueByProjectIdTotalCount(mapRecord);
		if(overduecount < 1){
			resultDO.setResultCode(ResultCode.PROJECT_OVERDUE_NO_AMOUNT);
			return resultDO;
		}
		String overdueId = "";
		// 本息期数
		String interestPeriods = "";
		Map<String, Object> map = Maps.newHashMap();
		map.put("projectId", project.getProjectId());
		map.put("endDate", project.getRefundTime());
		map.put("overdueStatus", StatusEnum.OVERDUE_LOG_PAYING.getStatus());
		List<ProjectInterestBiz> list = Lists.newArrayList();
		list = debtInterestManager.getOverdueInfoListByMap(map);
		//判断是否有还款中的逾期记录
		if(Collections3.isNotEmpty(list)){
			resultDO.setResultCode(ResultCode.PROJECT_OVERDUE_PAYING);
			return resultDO;
		}
		map.put("overdueStatus", StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
		list = debtInterestManager.getOverdueInfoListByMap(map);
		if(Collections3.isNotEmpty(list)){
			for(ProjectInterestBiz biz : list){
				//更新逾期记录表
				OverdueLog overdueLog = new OverdueLog(); 
				overdueLog.setId(biz.getOverdueId());
				overdueLog.setStatus(StatusEnum.OVERDUE_LOG_HAD_PAY.getStatus());
				overdueLog.setUpdateTime(new Date());
				overdueLog.setEndDate(new Date());
				overdueLogManager.updateByPrimaryKeySelective(overdueLog);
				//更新项目本息表
				DebtInterest interest = new DebtInterest();
				interest.setId(biz.getInterestId());
				interest.setStatus(StatusEnum.DEBT_INTEREST_ALL_PAYED.getStatus());
				debtInterestManager.updateByPrimaryKeySelective(interest);
				//更新垫资记录表
				underWriteLogManager.updateUnderWriteByInterestId(biz.getInterestId());
				overdueId = overdueId + biz.getOverdueId() + ",";
				interestPeriods = interestPeriods + biz.getPeriods() + ",";
			}
		}
		project.setOverdueIds(overdueId);
	    project.setInterestPeriods(interestPeriods);
		//保存逾期还款记录
		int i = overdueRepayLogManager.saveOverdueRepayLog(project);
		if(i < 1){
			logger.error("逾期还款失败", project.toString());
			resultDO.setSuccess(false);
			throw new ManagerException("逾期还款失败");
		}
		return resultDO;
	}
	
}
