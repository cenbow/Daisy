package com.yourong.core.ic.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.fin.dao.OverdueRepayLogMapper;
import com.yourong.core.fin.dao.UnderwriteLogMapper;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.model.OverdueLog;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.fin.model.UnderwriteLog;
import com.yourong.core.ic.dao.DebtInterestMapper;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.tc.dao.TransactionInterestMapper;

@Component
public class DebtInterestManagerImpl implements DebtInterestManager {
	@Autowired
	private DebtInterestMapper debtInterestMapper;
	@Autowired
	private OverdueLogManager overdueLogManager;
	@Autowired
	private TransactionInterestMapper transactionInterestMapper;
	@Autowired
	private UnderwriteLogMapper underwriteLogMapper;
	@Autowired
	private OverdueRepayLogMapper overdueRepayLogMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return debtInterestMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(DebtInterest record) throws ManagerException {
		try {
			return debtInterestMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(DebtInterest record) throws ManagerException {
		try {
			return debtInterestMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtInterest selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return debtInterestMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(DebtInterest record) throws ManagerException {
		try {
			return debtInterestMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(DebtInterest record) throws ManagerException {
		try {
			return debtInterestMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<DebtInterest> findByPage(Page<DebtInterest> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = debtInterestMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<DebtInterest> selectForPagin = debtInterestMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(int[] ids) throws ManagerException {
		try {
			return debtInterestMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据债权id获取债权本息信息
	 */
	@Override
	public List<DebtInterest> findInterestsByDebtId(Long debtId) throws ManagerException {
		try {
			return debtInterestMapper.findInterestsByDebtId(debtId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateStatusForPayInterestAndPrincipal() throws ManagerException {
		try {
			return debtInterestMapper.updateStatusForPayInterestAndPrincipal(
					StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus(), StatusEnum.DEBT_INTEREST_ALL_PAYED.getStatus());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/* 根据项目获取债权本息期数 */
	@Override
	public int findPeriodsByProjectId(Map<String, Object> map) throws ManagerException {
		int result = 0;
		try {
			result = debtInterestMapper.findPeriodsByProjectId(map);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/*根据项目id获取债权本息数据*/
	@Override
	public List<DebtInterest> findDebtInterestByProjectId(Long projectId) throws ManagerException {
		try {
			return debtInterestMapper.findDebtInterestByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int findDPeriodsByProjectId(Map<String, Object> map) throws ManagerException {
		int result = 0;
		try {
			result = debtInterestMapper.findDPeriodsByProjectId(map);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/*根据项目id获取债权本息数据*/
	@Override
	public List<DebtInterest> findInterestsByProjectId(Long projectId) throws ManagerException {
		try {
			return debtInterestMapper.findInterestsByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public List<DebtInterest> findInterestlistByProjectId(Long projectId) throws ManagerException {
		try {
			return debtInterestMapper.findInterestlistByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public Page<DebtInterest> findInterestsByProjectId(Page<DebtInterest> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			List<DebtInterest> interestList = debtInterestMapper.findInterestsByProjectId((Long)map.get("projectId"));
			pageRequest.setData(interestList);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return pageRequest;
	}
	/**
	 * 
	 * @desc 还款日期
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月27日 上午9:52:03
	 *
	 */
	@Override
	public DebtInterest selectOverdueDyasByProjectId(Long projectId) throws ManagerException {
		try {
			return debtInterestMapper.selectOverdueDyasByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc TODO 逾期本金和利息
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月27日 上午9:55:39
	 *
	 */
	@Override
	public DebtInterest selectOverdueAmountByProjectId(Map<String, Object> map) throws ManagerException {
		try {
			return debtInterestMapper.selectOverdueAmountByProjectId(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtInterest selectWaitPayAmountByBorrowerId(Map<String, Object> map)throws ManagerException {
		try {
			return debtInterestMapper.selectWaitPayAmountByBorrowerId(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtInterest selectWaitPayAmountByProjectId(Map<String, Object> map) throws ManagerException {
		try {
			return debtInterestMapper.selectHadPayAmountByProjectId(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectInterestBiz selectOverduePayAmountByProjectId(Map<String, Object> map) throws ManagerException {
		try {
			return debtInterestMapper.selectOverduePayAmountByProjectId(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int findInterestsByProjectIdTotalCount(Map<String, Object> map) throws ManagerException {
		try {
			return debtInterestMapper.findInterestsByProjectIdTotalCount(map);
		} catch (Exception e) {
			//throw new ManagerException(e);
		}
		return 0;
	}
	/**
	 * 
	 * @desc TODO 查询逾期记录和本息记录
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月1日 下午2:30:28
	 *
	 */
	@Override
	public List<ProjectInterestBiz> getOverdueInfoListByMap(Map<String, Object> map) throws ManagerException {
		try {
			return debtInterestMapper.getOverdueInfoListByMap(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtInterest findDebtInterestByEndDateAndProjectId(Long projectId, Date endDate) throws ManagerException {
		try {
			return debtInterestMapper.findDebtInterestByEndDateAndProjectId(projectId,endDate);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtInterest selectNOPayAmountByMemberId(Long memberId) throws ManagerException {
		try {
			return debtInterestMapper.selectNOPayAmountByBorrowerId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<DebtInterest> findProjectInterestsByProjectId(Long projectId)throws ManagerException {
		try {
			return debtInterestMapper.findProjectInterestsByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 待支付本金
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年3月9日 上午10:05:19
	 *
	 */
	@Override
	public DebtInterest selectWaitPayAmountByMemberId(Long memberId) throws ManagerException {
		try {
			return debtInterestMapper.selectWaitPayAmountByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	 /**
     * @Description:根据项目获取债权本息状态
     * @return
     * @author: zhanghao
     * @time:2016年4月7日 上午9:40:05
     */
	@Override
	public DebtInterest findStatusByProjectId(Map<String, Object> map)throws ManagerException {
		try {
			return debtInterestMapper.findStatusByProjectId(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateStatusById(Long interestId, int beforeStatus, int afterStatus)
			throws ManagerException {
		try{
			return debtInterestMapper.updateStatusById(interestId, beforeStatus, afterStatus);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getCountUnReturnByProjectId(Long projectId) throws ManagerException {
		try{
			return debtInterestMapper.getCountUnReturnByProjectId(projectId);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<DebtInterest> findOverdueInterestsByProjectId(Long projectId) throws ManagerException {
		try {
			return debtInterestMapper.findOverdueInterestsByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public List<DebtInterest> findWaitPayInterestsByProjectId(Long projectId) throws ManagerException {
		try {
			return debtInterestMapper.findWaitPayInterestsByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	/**
	 * TODO 考虑是否采用事务的方式
	 * @Description:
	 * 1.查询需要生成逾期的项目本息(条件1-未垫资 条件2-直投项目)
	 * 2.更新逾期状态
	 * 3.插入逾期记录
	 * 4.查询交易本息，更新还款类型
	 * 5.插入或更新逾期结算记录（存在未还款的逾期结算记录，则更新逾期结算记录；不存在则新增逾期还款记录，逾期开始时间为end_date+1）
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年6月1日 下午5:33:00
	 */
	@Override
	public synchronized void generateGeneralOverdue() throws ManagerException {
		List<DebtInterest> overduedProjectInterest = debtInterestMapper.selectOverduedProjectInterest();
		for (DebtInterest debtInterest : overduedProjectInterest) {
			// 2-更新项目本息状态为逾期
			int overdueInterest = debtInterestMapper.updateStatusForOverdueProjectInterest(debtInterest.getId());
			if (overdueInterest > 0) {
				// 3-插入逾期记录
				overdueLogManager.insertOverdue(debtInterest.getId(),
						TypeEnum.OVERDUE_LOG_TYPE_GENERAL.getType());
				// 4-更新交易本息的还款类型
				transactionInterestMapper.updatePayTypeByInterestId(
						TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_OVERDUE.getType(),
						TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_NORMAL.getType(), debtInterest.getId());
				// 5-插入或更新逾期结算记录
				OverdueRepayLog repayLog = overdueRepayLogMapper.getOverdueRepayByStatus(debtInterest
						.getProjectId());
				if (repayLog != null) {
					// TODO 封装一个方法
					// 更新逾期结算记录 获取锁
					OverdueRepayLog repayLogLock = overdueRepayLogMapper.getForLock(repayLog.getId());
					repayLogLock.setOverduePrincipal(FormulaUtil.addDecimal(repayLogLock.getOverduePrincipal(),
							debtInterest.getPayablePrincipal()));
					repayLogLock.setOverdueInterest(FormulaUtil.addDecimal(repayLogLock.getOverdueInterest(),
							debtInterest.getPayableInterest()));
					OverdueLog overdueLog = overdueLogManager.getOverdueLogByInterestId(debtInterest.getId());
					if (overdueLog != null) {
						repayLogLock.setInterestPeriods(repayLogLock.getInterestPeriods() + ","
								+ debtInterest.getPeriods());// 本息期数
						repayLogLock.setOverdueId(repayLogLock.getOverdueId() + "," + overdueLog.getId());// 逾期记录id
					}
					overdueRepayLogMapper.updateByPrimaryKeySelective(repayLogLock);
				} else {
					// TODO 封装一个方法
					OverdueRepayLog repayLogNew = new OverdueRepayLog();
					repayLogNew.setProjectId(debtInterest.getProjectId());
					repayLogNew.setOverdueStartDate(DateUtils.addDate(debtInterest.getEndDate(), 1));
					repayLogNew.setOverduePrincipal(debtInterest.getPayablePrincipal());
					repayLogNew.setOverdueInterest(debtInterest.getPayableInterest());
					repayLogNew.setInterestPeriods(debtInterest.getPeriods());
					repayLogNew.setRepayType(1);// TODO 采用枚举方式
					repayLogNew.setRepayStatus(1);// TODO 采用枚举方式
					repayLogNew.setType(TypeEnum.OVERDUE_SETTLEMENT_TYPE_COMMON.getType());
					OverdueLog overdueLog = overdueLogManager.getOverdueLogByInterestId(debtInterest.getId());
					if (overdueLog != null) {
						repayLogNew.setOverdueId(overdueLog.getId().toString());
					}
					overdueRepayLogMapper.insertSelective(repayLogNew);
				}

			}
		}
	}

	@Override
	public void generateUnderWriteOverdue() throws ManagerException {
		//查询未生成逾期记录的垫资记录，生成逾期记录
		List<UnderwriteLog> underwriteLogs = underwriteLogMapper.findUnderwriteNoOverdue();
		for (UnderwriteLog underwriteLog : underwriteLogs) {
			overdueLogManager.insertOverdue(underwriteLog.getProjectInterestId(),
					TypeEnum.OVERDUE_LOG_TYPE_UNDERWRITE.getType());
		}
	}
	
	
	@Override
	public int updateRealPayForPrincipalAndInterestSuccess(Long id, BigDecimal realPayInterest,BigDecimal realPayPrincipal)
			throws ManagerException {
		try{
			return debtInterestMapper.updateRealPayForPrincipalAndInterestSuccess(id, realPayInterest,realPayPrincipal);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
