package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.core.fin.dao.OverdueLogMapper;
import com.yourong.core.fin.dao.UnderwriteLogMapper;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.model.OverdueLog;
import com.yourong.core.fin.model.UnderwriteLog;
import com.yourong.core.ic.dao.DebtInterestMapper;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.ProjectInterestBiz;

@Component
public class OverdueLogManagerImpl implements OverdueLogManager {
	@Autowired
	private OverdueLogMapper overdueLogMapper;
	
	@Autowired
	private UnderwriteLogMapper underWriteLogMapper;
	
	@Autowired
	private DebtInterestMapper debtInterestMapper;
	@Override
	public OverdueLog getOverdueLogByInterestId(Long interestId) throws ManagerException {
		try {
			return overdueLogMapper.getOverdueLogByInterestId(interestId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int insertSelective(OverdueLog overdueLog) throws ManagerException {
		try {
			return overdueLogMapper.insertSelective(overdueLog);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public List<OverdueLog> getOverdueLogByProjectId(Long projectId) throws ManagerException {
		try {
			return overdueLogMapper.getOverdueLogByProjectId(projectId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public OverdueLog selectOverdueByProjectId(Map<String, Object> map) throws ManagerException {
		try {
			return overdueLogMapper.selectOverdueByProjectId(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	/**
	 * 
	 * @desc   保存逾期记录
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月29日 下午2:39:48
	 *
	 */
	@Override
	public int saveOverdueLog(ProjectInterestBiz project) throws ManagerException {
		OverdueLog overdueLog=new OverdueLog();
		overdueLog.setProjectId(project.getProjectId());
		overdueLog.setInterestId(project.getInterestId());
		overdueLog.setUnderwriteMemberId(project.getThirdMemberId());
		overdueLog.setOverduePrincipal(project.getPayablePrincipal());
		overdueLog.setOverdueInterest(project.getPayableInterest());
		overdueLog.setStatus(StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
		overdueLog.setStartDate(project.getEndDate());
		UnderwriteLog underwriteLog=underWriteLogMapper.getUnderwriteLogByInterestId(project.getInterestId());
		if(underwriteLog!=null){
			overdueLog.setUnderwriteId(underwriteLog.getId());
		}
		try {
			return overdueLogMapper.insertSelective(overdueLog);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int findOverdueByProjectIdTotalCount(Map<String, Object> map) throws ManagerException {
		try {
			return overdueLogMapper.findOverdueByProjectIdTotalCount(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	@Override
	public int updateByPrimaryKeySelective(OverdueLog record) throws ManagerException {
		try {
			return overdueLogMapper.updateByPrimaryKeySelective(record);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	/**
	 * 
	 * @desc 统计逾期记录
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月1日 下午3:24:02
	 *
	 */
	@Override
	public int countOverdueRecordByProjectId(Long projectId) throws ManagerException {
		try {
			return overdueLogMapper.countOverdueRecordByProjectId(projectId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	@Override
	public int insertOverdue(Long interestId,int type) throws ManagerException {
		try {
			//是否添加过逾期记录
			OverdueLog overdueLogByInterestId = this.getOverdueLogByInterestId(interestId);
			if (overdueLogByInterestId != null){
			 return  0;
			}
			OverdueLog record = new OverdueLog();
			DebtInterest debtInterest=debtInterestMapper.selectByPrimaryKey(interestId);
			if(debtInterest!=null){
				record.setProjectId(debtInterest.getProjectId());
				record.setStartDate(DateUtils.addDate(debtInterest.getEndDate(), 1));
				record.setOverdueInterest(debtInterest.getPayableInterest());
				record.setOverduePrincipal(debtInterest.getPayablePrincipal());
			}
			if(TypeEnum.OVERDUE_LOG_TYPE_UNDERWRITE.getType()==type){
				//是否存在垫资记录
				UnderwriteLog underwriteLog=underWriteLogMapper.getUnderwriteLogByInterestId(interestId);
				if (underwriteLog == null ){
					return  0;
				}
				record.setUnderwriteId(underwriteLog.getId());
				record.setUnderwriteMemberId(underwriteLog.getUnderwriteMemberId());
				record.setOverduePrincipal(underwriteLog.getPayablePrincipal());
				record.setOverdueInterest(underwriteLog.getPayableInterest());
			} 
			record.setType(type);
			record.setInterestId(interestId);
			
			record.setStatus(StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus());
			return overdueLogMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 逾期未还记录数
	 * @param status
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月23日 下午1:44:22
	 *
	 */
	@Override
	public int countOverdueNotpay(Long projectId) throws ManagerException {
		try {
			int status = StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus();
			return overdueLogMapper.countOverdueRecordByProjectIdandStatus(status,projectId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	/**
	 * 
	 * @desc 逾期还款中
	 * @param status
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月23日 下午1:44:27
	 *
	 */
	@Override
	public int countOverduePaying(Long projectId) throws ManagerException {
		try {
			int status = StatusEnum.OVERDUE_LOG_PAYING.getStatus();
			return overdueLogMapper.countOverdueRecordByProjectIdandStatus(status,projectId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int updateStatus(int beforeStatus, int afterStatus, Date repayDate,Long projectId,String remark)throws ManagerException{
		try {
			return overdueLogMapper.updateStatusByProjectId(beforeStatus,afterStatus,repayDate,projectId,remark);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int updateOverdueFine(BigDecimal overdueFine, Integer status,Long projectId) throws ManagerException{
		try {
			return overdueLogMapper.updateOverdueFine(overdueFine,status,projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public OverdueLog selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return overdueLogMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int updateForOverdueRepaySuccess(int beforeStatus, int afterStatus, Long id) throws ManagerException {
		try {
			return overdueLogMapper.updateForOverdueRepaySuccess(beforeStatus,afterStatus,id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 统计逾期记录数  
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月29日 下午2:04:05
	 *
	 */
	@Override
	public int countOverdueRecordByInterestId(Map<String, Object> map) throws ManagerException {
		try {
			return overdueLogMapper.countOverdueRecordByInterestId(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	/**
	 * 
	 * @desc 查询逾期本金和利息 根据memberId
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年3月9日 上午9:43:57
	 *
	 */
	@Override
	public OverdueLog selectOverduePayAmountByMemberId(Long memberId) throws ManagerException {
		try {
			return overdueLogMapper.selectOverduePayAmountByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int getOverdueLogByProjectIdAndType(Long id, int type) throws ManagerException {
		try {
			return overdueLogMapper.getOverdueLogByProjectIdAndType(type,id);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int getOverdueLogByInterestIdAndtype(Long interestId, int type) throws ManagerException {
		try {
			return overdueLogMapper.getOverdueLogByInterestIdAndtype(interestId,type);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public List<OverdueLog> selectInterestsByProjectId(Long projectId) throws ManagerException {
		try {
			return overdueLogMapper.selectInterestsByProjectId(projectId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int getCountOverdueRecord(Long interestId) throws ManagerException {
		try {
			return overdueLogMapper.getCountOverdueRecord(interestId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int getCountOverdueRecordByInterestId(Long interestId, int type) throws ManagerException {
		try {
			return overdueLogMapper.getCountOverdueRecordByInterestId(interestId,type);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int updateStatusForOverdueRepay(int beforeStatus, int afterStatus, Long id)throws ManagerException {
		try {
			return overdueLogMapper.updateStatusForOverdueRepay(beforeStatus,afterStatus,id);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public OverdueLog selectByInterestId(Long interestId) throws ManagerException {
		try {
			return overdueLogMapper.selectByInterestId(interestId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	/**
	 * @Description:根据项目id获取当前未还款的垫资逾期记录的startDate
	 * @param interestId
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年7月1日 下午3:13:17
	 */
	@Override
	public OverdueLog getUnderwriteStartDateByProjectIdandStatus(Long projectId) throws ManagerException {
		try {
			return overdueLogMapper.getStartDateByProjectIdandStatus(StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus(), projectId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	/**
	 * @desc 根据项目id获取未还款的垫资逾期记录
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author fuyili
	 * @time 2016年7月19日 下午2:02:43
	 *
	 */
	@Override
	public List<OverdueLog> getIdsByProjectIdandStatus(Long projectId) throws ManagerException {
		try {
			return overdueLogMapper.getIdsByProjectIdandStatus(StatusEnum.OVERDUE_LOG_WAIT_PAY.getStatus(), projectId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public OverdueLog getLockForUpdate(Long id) throws ManagerException {
		try {
			return overdueLogMapper.getLockForUpdate(id);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	/**
	 * 
	 * @Description:是否项目存在逾期未还记录
	 * @param projectId
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月8日 下午3:26:00
	 */
	@Override
	public boolean isOverDueUnrepayment(Long projectId) throws ManagerException {
		try {
			int num = overdueLogMapper.isOverDueUnrepayment(projectId);
			if(num>0){
				return true ;
			}
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
		return false;
	}
}
