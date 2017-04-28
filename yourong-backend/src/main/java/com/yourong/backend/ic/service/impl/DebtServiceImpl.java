package com.yourong.backend.ic.service.impl;

import java.math.BigDecimal;
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

import com.yourong.backend.ic.service.DebtService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AttachmentThread;
import com.yourong.core.handle.DebtAttachmentHandle;
import com.yourong.core.ic.manager.DebtCollateralManager;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.DebtPledgeManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;
import com.yourong.core.upload.model.ImageConfig;

@Service
public class DebtServiceImpl implements DebtService {

	private static Logger logger = LoggerFactory
			.getLogger(DebtServiceImpl.class);

	@Autowired
	private DebtCollateralManager debtCollateralManager;

	@Autowired
	private DebtPledgeManager debtPledgeManager;

	@Autowired
	private DebtManager debtManager;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private DebtInterestManager debtInterestManager;

	@Autowired
	private BscAttachmentManager bscAttachmentManager;

	@Autowired
	private AttachmentIndexManager attachmentIndexManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private Map<String, List<ImageConfig>> imagesConfig;
	
	@Autowired
	private DebtAttachmentHandle debtAttachmentHandle;
	
	@Autowired
	private EnterpriseManager enterpriseManager;
	@Autowired
	private BalanceManager balanceManager;
	

	@Override
	public Integer deleteByPrimaryKey(Long id) {
		try {
			return debtManager.deleteByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("删除债权失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public Integer insert(Debt record) {
		try {
			return debtManager.insert(record);
		} catch (Exception e) {
			logger.error("插入债权失败,debt=" + record, e);
		}
		return null;
	}

	@Override
	public Integer updateByPrimaryKeySelective(Debt record) {
		try {
			return debtManager.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			logger.error("更新债权失败,debt=" + record, e);
		}
		return null;
	}

	@Override
	public Debt selectByPrimaryKey(Long id) {
		try {
			return debtManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("查询债权失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public Integer updateByPrimaryKey(Debt record) {
		try {
			return debtManager.updateByPrimaryKey(record);
		} catch (Exception e) {
			logger.error("更新债权失败,debt=" + record, e);
		}
		return null;
	}

	@Override
	public Page<Debt> findByPage(Page<Debt> pageRequest, Map<String, Object> map) {
		try {
			return debtManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("分页查询债权失败", e);
		}
		return null;
	}

	@Override
	public Integer batchDelete(long[] ids) {
		try {
			return debtManager.batchDelete(ids);
		} catch (Exception e) {
			logger.error("批量删除债权失败,ids=" + ids, e);
		}
		return null;
	}

	/**
	 * 获取完整债权信息失败
	 */
	@Override
	public DebtBiz getFullDebtInfoBySerialNumber(String serialNumber) {
		try {
			return debtManager.getFullDebtInfoBySerialNumber(serialNumber);
		} catch (Exception e) {
			logger.error("获取所有债权信息失败,serialNumber=" + serialNumber, e);
		}
		return null;
	}

	@Override
	public List<Map<String, String>> findSerialNumberAndMemberName(
			String serialNumber) {
		try {
			return debtManager.findSerialNumberAndMemberName(serialNumber);
		} catch (Exception e) {
			logger.error("查询公司债权编号&借款人失败,serialNumber=" + serialNumber, e);
		}
		return null;
	}

	@Override
	public MemberBaseBiz selectMemberBaseBiz(Long memberId) {
		try {
			return memberManager.selectMemberBaseBiz(memberId);
		} catch (Exception e) {
			logger.error("获取会员信息失败,memberId=" + memberId, e);
		}
		return null;
	}

	@Override
	public Integer insertSelective(Debt record) {
		try {
			return debtManager.insertSelective(record);
		} catch (Exception e) {
			logger.error("插入,record=" + record, e);
		}
		return null;
	}

	/**
	 * 新增
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Debt> insertDebtInfo(DebtBiz debtBiz, String appPath)
			throws ManagerException {
		ResultDO<Debt> resultDO = new ResultDO<Debt>();
		try {
			if(StringUtil.isNotBlank(debtBiz.getOriginalDebtNumber())){
				int isExistDebt = debtManager.findDebtByOriginalDebtNumber(debtBiz
						.getOriginalDebtNumber());
				if (isExistDebt > 0) {
					resultDO.setResultCode(ResultCode.DEBT_ORIGINAL_REPEAT_ERROR);
					return resultDO;
				}
			}
			//判断添加经营融的企业是否添加授信额度
			resultDO = checkRunCompanyIsCreditAmount(debtBiz);
			if(!resultDO.isSuccess()){
				return resultDO;
			}
			Debt debt = BeanCopyUtil.map(debtBiz, Debt.class);
			//getDebtInfoByGuaranty(debt);
			if (debt != null) {
				// 保存债权信息
				int result = debtManager.insertSelective(debt);
				if (result > 0) {
					// 设置公司债权编号
					Debt savedDebt = debtManager.selectByPrimaryKey(debt
							.getId());
					String debtNo = SerialNumberUtil.generateDebtNo(savedDebt
							.getId());// 生成债权编号
					savedDebt.setSerialNumber(debtNo);
					debtManager.updateByPrimaryKey(savedDebt);
					if (DebtEnum.DEBT_TYPE_COLLATERAL.getCode().equals(
							debt.getDebtType())||DebtEnum.DEBT_TYPE_CREDIT.getCode().equals(
									debt.getDebtType())) {//债权类型为抵押、信用
						if (debtBiz.getDebtCollateral() != null) {
							debtBiz.getDebtCollateral().setDebtId(debt.getId());
							debtCollateralManager.insert(debtBiz
									.getDebtCollateral());
						}
					}
					if (DebtEnum.DEBT_TYPE_PLEDGE.getCode().equals(
							debt.getDebtType())) {
						if (debtBiz.getDebtPledge() != null) {
							debtBiz.getDebtPledge().setDebtId(debt.getId());
							debtPledgeManager.insert(debtBiz.getDebtPledge());
						}
					}
					
					// 计算每期的本息
					if (Collections3.isNotEmpty(debtBiz.getDebtInterests())) {
						int period = 0;
						int totalPeriod = debtBiz.getDebtInterests().size();
						for (DebtInterest interest : debtBiz.getDebtInterests()) {
							interest = getInterestData(interest, savedDebt,period,totalPeriod);
							debtInterestManager.insert(interest);
							period = period +1;
						}
					}
					// 保存到aliyun
//					List<BscAttachment> bscAttachments = saveToAliyun(
//							debt.getId(), debtBiz.getBscAttachments(), appPath);
					// 保存附件
//					saveAttachments(bscAttachments, debt.getId(), resultDO);
					
					AttachmentInfo info = new AttachmentInfo();
					info.setKeyId(debt.getId().toString());
					info.setBscAttachments(debtBiz.getBscAttachments());
					info.setAppPath(appPath);
					info.setOperation(AttachmentInfo.SAVE);
					taskExecutor.execute(new AttachmentThread(debtAttachmentHandle, info));
				}
			}
		} catch (ManagerException e) {
			logger.error("插入所有债券信息失败,debtBiz=" + debtBiz, e);
			resultDO.setResultCode(ResultCode.DEBT_ADD_SAVE_FAIL_ERROR);
			throw new ManagerException(e);
		}
		return resultDO;
	}


	/**
	 * 更新
	 */

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Debt> updateDebtInfo(DebtBiz debtBiz, String appPath)
			throws ManagerException {
		ResultDO<Debt> resultDO = new ResultDO<Debt>();
		resultDO = checkRunCompanyIsCreditAmount(debtBiz);
		if(!resultDO.isSuccess()){
			return resultDO;
		}
		try {
			Debt debt = BeanCopyUtil.map(debtBiz, Debt.class);
			debt.setUpdatedTime(DateUtils.getCurrentDate());
			//根据质押或抵押物类型的不同，设置不同debt对象值
			//getDebtInfoByGuaranty(debt);
			getDebtInfoByEnterprise(debt);
			if (debt != null) {
				// 更新债权信息
				int result = debtManager.updateByPrimaryKeySelective(debt);
				if (result > 0) {
					// 更新担保物
					updateCollateralAndPledge(debt.getId(), debtBiz, resultDO);

					// 更新债权本息数据
					updateInterests(debtBiz.getDebtInterests(), debt, resultDO);

					// 保存到aliyun
//					List<BscAttachment> bscAttachments = saveToAliyun(
//							debt.getId(), debtBiz.getBscAttachments(), appPath);
					// 更新附件
//					updateAttachments(bscAttachments, debt.getId(), resultDO);
					
					AttachmentInfo info = new AttachmentInfo();
					info.setKeyId(debt.getId().toString());
					info.setBscAttachments(debtBiz.getBscAttachments());
					info.setAppPath(appPath);
					info.setOperation(AttachmentInfo.UPDATE);
					taskExecutor.execute(new AttachmentThread(debtAttachmentHandle, info));
					
				} else {
					resultDO.setResultCode(ResultCode.DEBT_UPDATE_FAIL_ERROR);
				}
			}
		} catch (ManagerException e) {
			logger.error("更新所有债券信息失败,debtBiz=" + debtBiz, e);
			resultDO.setResultCode(ResultCode.DEBT_UPDATE_FAIL_ERROR);
			throw new ManagerException(e);
		}
		return resultDO;
	}
	
	/**
	 * 紧急更新
	 */

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Debt> emergencyUpdateDebtInfo(DebtBiz debtBiz, String appPath)
			throws ManagerException {
		ResultDO<Debt> resultDO = new ResultDO<Debt>();
		resultDO = checkRunCompanyIsCreditAmount(debtBiz);
		if(!resultDO.isSuccess()){
			return resultDO;
		}
		try {
			Debt debt = BeanCopyUtil.map(debtBiz, Debt.class);
			debt.setUpdatedTime(DateUtils.getCurrentDate());
			//getDebtInfoByGuaranty(debt);//
			getDebtInfoByEnterprise(debt);
			if (debt != null) {
				// 更新债权信息
				int result = debtManager.emergencyUpdateByPrimaryKeySelective(debt);
				if (result > 0) {
					// 更新担保物
					updateCollateralAndPledge(debt.getId(), debtBiz, resultDO);
					
					AttachmentInfo info = new AttachmentInfo();
					info.setKeyId(debt.getId().toString());
					info.setBscAttachments(debtBiz.getBscAttachments());
					info.setAppPath(appPath);
					info.setOperation(AttachmentInfo.EMERGENCY_UPDATE);
					taskExecutor.execute(new AttachmentThread(debtAttachmentHandle, info));
					
				} else {
					resultDO.setResultCode(ResultCode.DEBT_UPDATE_FAIL_ERROR);
				}
			}
		} catch (ManagerException e) {
			logger.error("紧急更新所有债券信息失败,debtBiz=" + debtBiz, e);
			resultDO.setResultCode(ResultCode.DEBT_UPDATE_FAIL_ERROR);
			throw new ManagerException(e);
		}
		return resultDO;
	}

	/**
	 * 计算应收利息
	 * 
	 * @param unitInterest
	 * @param days
	 * @return
	 */
	private BigDecimal getPayableInterest(BigDecimal unitInterest, Integer days) {
		return unitInterest.multiply(new BigDecimal(days));
	}

	private DebtInterest getInterestData(DebtInterest debtInterest, Debt debt,int period,int totalPeriod) {
		//按日计息、一次性还本付息
		if(debt.getReturnType().equals(DebtEnum.RETURN_TYPE_DAY.getCode()) ||debt.getReturnType().equals(DebtEnum.RETURN_TYPE_ONCE.getCode()) ){
			debtInterest.setDebtId(debt.getId());
			// 每期天数
			int days = DateUtils.getIntervalDays(debtInterest.getStartDate(),debtInterest.getEndDate())+1;
			// 单位利息
			BigDecimal unitInterest = FormulaUtil.getUnitInterest(
					debt.getReturnType(), debt.getAmount(),
					debt.getAnnualizedRate());
			// 应付利息
			BigDecimal payableInterest = getPayableInterest(unitInterest, days);
			debtInterest.setUnitInterest(unitInterest);
			debtInterest.setPayableInterest(payableInterest);
			// 债权结束日期==本期结束日期 --->添加应付本金
			if (debt.getEndDate().equals(debtInterest.getEndDate())) {
				debtInterest.setPayablePrincipal(debt.getAmount());// 应付本金
				debtInterest.setUnitPrincipal(debt.getAmount());// 单位本金
			} else {
				debtInterest.setPayablePrincipal(BigDecimal.ZERO);
				debtInterest.setUnitPrincipal(BigDecimal.ZERO);//
			}
			debtInterest.setPeriods((period+1)+"/"+totalPeriod);
			return debtInterest;
		}
		//等本等息
		if(debt.getReturnType().equals(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode()) ){
			debtInterest.setDebtId(debt.getId());
			// 每期天数
			int days = DateUtils.getIntervalDays(debtInterest.getStartDate(),
					debtInterest.getEndDate())+1;
			// 应付利息
			BigDecimal payableInterest = BigDecimal.ZERO;
			payableInterest = FormulaUtil.getInterest(debt.getReturnType(), debt.getAmount(),
					debt.getAnnualizedRate());
			//应付本金
			BigDecimal payablePrincipal = BigDecimal.ZERO;
			payablePrincipal = FormulaUtil.getPrincipal(debt.getReturnType(), debt.getAmount(), totalPeriod,period);
			//单位利息 
			BigDecimal unitInterest = BigDecimal.ZERO;
			unitInterest = payableInterest.divide(new BigDecimal(days),2,BigDecimal.ROUND_HALF_UP);
			debtInterest.setUnitInterest(unitInterest);
			debtInterest.setPayableInterest(payableInterest);
			debtInterest.setPayablePrincipal(payablePrincipal);
			debtInterest.setUnitPrincipal(payablePrincipal);//
			debtInterest.setPeriods((period+1)+"/"+totalPeriod);
		}
		return debtInterest;	
	}


	/**
	 * 更新本息数据
	 * 
	 * @param interests
	 * @param result
	 */
	private void updateInterests(List<DebtInterest> interests, Debt debt,
			ResultDO<Debt> result) throws ManagerException {
		try {
			// 原有债权本息数据
			List<DebtInterest> oldInterests = debtInterestManager
					.findInterestsByDebtId(debt.getId());
			if (Collections3.isNotEmpty(interests)) {
				int period = 0;
				int totalPeriod = interests.size();
				for (DebtInterest newInterest : interests) {
					boolean existFlag = false;
					for (DebtInterest oldInterest : oldInterests) {
						if (oldInterest.getId().equals(newInterest.getId())) {
							existFlag = true;
							oldInterests.remove(oldInterest);
							break;
						}
					}
					newInterest = getInterestData(newInterest, debt,period,totalPeriod);
					if (existFlag) {
						debtInterestManager
								.updateByPrimaryKeySelective(newInterest);
					} else {
						debtInterestManager.insert(newInterest);
					}
					period = period +1;
				}
			}
			// 批量删除债权本息数据
			if (Collections3.isNotEmpty(oldInterests)) {
				for (DebtInterest debtInterest : oldInterests) {
					debtInterestManager
							.deleteByPrimaryKey(debtInterest.getId());
				}
			}
		} catch (Exception e) {
			logger.debug("更新本息数据失败,interests=" + interests);
			result.setResultCode(ResultCode.DEBT_ADD_SAVE_FAIL_ERROR);
			throw new ManagerException(e);
		}
	}

	/**
	 * 更新担保物数据
	 * 
	 * @param debtId
	 * @param debtBiz
	 * @param result
	 */
	private void updateCollateralAndPledge(Long debtId, DebtBiz debtBiz,
			ResultDO<Debt> result) throws ManagerException {
		try {
			if (DebtEnum.DEBT_TYPE_COLLATERAL.getCode().equals(
					debtBiz.getDebtType())||DebtEnum.DEBT_TYPE_CREDIT.getCode().equals(
							debtBiz.getDebtType())) {
				// 删除质押物
				debtPledgeManager.deletePledgeByDebtId(debtId);
				if (debtBiz.getDebtCollateral() != null) {
					if (StringUtil.isNotBlank(debtBiz.getDebtCollateral()
							.getCollateralType())) {
						if (debtBiz.getDebtCollateral().getId() != null) {
							debtCollateralManager
									.updateByPrimaryKeySelective(debtBiz
											.getDebtCollateral());
						} else {
							debtBiz.getDebtCollateral().setDebtId(debtId);
							debtCollateralManager.insert(debtBiz
									.getDebtCollateral());
						}
					} else {
						// 质押物为空，删除数据库质押物
						debtCollateralManager.deleteCollateralByDebtId(debtId);
					}
				} else {
					// 抵押物为空，删除数据库抵押物
					debtCollateralManager.deleteCollateralByDebtId(debtId);
				}
			}
			if (DebtEnum.DEBT_TYPE_PLEDGE.getCode().equals(
					debtBiz.getDebtType())) {
				// 删除抵押物
				debtCollateralManager.deleteCollateralByDebtId(debtId);
				if (debtBiz.getDebtPledge() != null) {
					if (StringUtil.isNotBlank(debtBiz.getDebtPledge()
							.getPledgeType())) {
						if (debtBiz.getDebtPledge().getId() != null) {
							debtPledgeManager
									.updateByPrimaryKeySelective(debtBiz
											.getDebtPledge());
						} else {
							debtBiz.getDebtPledge().setDebtId(debtId);
							debtPledgeManager.insert(debtBiz.getDebtPledge());
						}
					} else {
						// 质押物为空，删除数据库质押物
						debtPledgeManager.deletePledgeByDebtId(debtId);
					}
				} else {
					// 质押物为空，删除数据库质押物
					debtPledgeManager.deletePledgeByDebtId(debtId);
				}
			}
		} catch (Exception e) {
			logger.debug("债权担保物更新失败,debtBiz" + debtBiz, e);
			result.setResultCode(ResultCode.DEBT_ADD_SAVE_FAIL_ERROR);
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer deleteByDebtId(Long id) {
		try {
			return debtManager.deleteByDebtId(id);
		} catch (Exception e) {
			logger.error("逻辑删除债权失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public Page<Debt> findDebtInfoByPage(Page<Debt> pageRequest,
			Map<String, Object> map) {
		try {
			//查询债权信息的查询条件包含用户查询条件，在此打上标记
			if (map.containsKey("borrowerTrueName") || map.containsKey("lenderTrueName") ||map.containsKey("mobile")
					|| map.containsKey("identityNumber")
					|| map.containsKey("originalDebtNumber")
					|| map.containsKey("debtType")
					|| map.containsKey("guarantyType")) {
				map.put("innerMember", true);
			}
			Page<Debt> debtPage = debtManager.findDebtInfoByPage(pageRequest, map);
			if(debtPage != null && debtPage.getiTotalRecords() > 0) {
				Member member = null;
				//循环结果集查询出借人和借款人
				for (Debt debt : debtPage.getData()) {
					if(debt.getBorrowerId() != null && debt.getBorrowerId() > 0) {
						member = memberManager.selectByPrimaryKey(debt.getBorrowerId());
						if(member != null) {
							debt.setBorrowerName(member.getTrueName());
						}
					} 
					if(debt.getLenderId() != null && debt.getLenderId() > 0) {
						member = memberManager.selectByPrimaryKey(debt.getLenderId());
						if(member != null) {
							debt.setLenderName(member.getTrueName());
						}
					} 
				}
			}
			return debtPage;
		} catch (Exception e) {
			logger.error("分页查询债权信息失败", e);
		}
		return null;
	}

	@Override
	public Page<DebtInterest> showInterestRecord(
			Page<DebtInterest> pageRequest, Map<String, Object> map) {
		try {
			List<DebtInterest> interestList = debtInterestManager.findInterestsByDebtId((Long)map.get("debtId"));
			pageRequest.setData(interestList);
			return pageRequest;
		} catch (Exception e) {
			logger.error("查询付息记录失败", e);
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Object> addControlRemarks(Long id, String newControlRemarks) throws ManagerException {
		ResultDO<Object> resultDO = new ResultDO<>();
		try {
			Debt debt = new Debt();
			debt.setId(id);
			debt.setControlRemarks(newControlRemarks);
			int returnRowNum = debtManager.updateControlRemarksById(debt);
			if(returnRowNum > 0) {
				resultDO.setSuccess(true);
			} else {
				resultDO.setSuccess(false);
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_DATABASE_UPDATE);
			}
		} catch(Exception e) {
			logger.debug("风控备注更新失败！ id=" + id, e);
			throw new ManagerException(e);
		}
		return resultDO;
	}

	@Override
	public Debt findDebtAndProjectStatusByDebtId(Long id){
		try {
			return debtManager.findDebtAndProjectStatusByDebtId(id);
		} catch (Exception e) {
			logger.debug("根据债权id查询债权及项目状态失败,id={}", id, e);
		}
		return null;
	}
	
	@Override
	public MemberBaseBiz getMemberBaseBizByEnterpriseId(Long enterpriseId){
		try {
			return memberManager.getMemberBizByEnterpriseId(enterpriseId);
		} catch (Exception e) {
			logger.debug("根据企业id查询用户信息失败,id={}", enterpriseId, e);
		}
		return null;
	}
	
	private Debt getDebtInfoByGuaranty(Debt debt){
		//如果是经营融，保存企业id,否则只保存借款memberid
		if(!debt.getGuarantyType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode())){
			debt.setEnterpriseId(null);
		}
		return debt;
	}
	
	private Debt getDebtInfoByEnterprise(Debt debt){
		if(debt.getBorrowerType()==TypeEnum.MEMBER_TYPE_PERSONAL.getType()){
			debt.setEnterpriseId(null);
		}
		if(debt.getLenderType()==TypeEnum.MEMBER_TYPE_PERSONAL.getType()){
			debt.setLenderEnterpriseId(null);
		}
		return debt;
	}
	/**
	 * 
	 * @param debtBiz
	 * @return
	 */
	private ResultDO<Debt> checkRunCompanyIsCreditAmount(DebtBiz debtBiz) throws ManagerException{
		ResultDO<Debt> resultDO = new ResultDO<Debt>();
		if(debtBiz.getGuarantyType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode()) && debtBiz.getEnterpriseId()!=null){
			Balance balance = balanceManager.queryBalance(debtBiz.getEnterpriseId(), TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
			if(balance==null){
				resultDO.setResultCode(ResultCode.DEBT_SAVE_ENTERPRISE_CREDIT_AMOUNT_NOT_NULL);
				return resultDO;
			}
		}
		return resultDO;
	}

	@Override
	public List<DebtInterest> autoInterest(String returnType, int interestFrom, String sDate, String eDate) {
		try {
			return debtManager.autoInterest(returnType,interestFrom,sDate,eDate);
		} catch (Exception e) {
			logger.debug("自动分期失败,returnType={}", returnType, e);
		}
		return null;
	}

}
