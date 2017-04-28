package com.yourong.core.fin.manager.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.core.fin.dao.UnderwriteLogMapper;
import com.yourong.core.fin.manager.UnderwriteLogManager;
import com.yourong.core.fin.model.UnderwriteLog;
import com.yourong.core.ic.model.ProjectInterestBiz;

@Component
public class UnderwriteLogManagerImpl implements UnderwriteLogManager {

	@Autowired
	private UnderwriteLogMapper underWriteLogMapper;
	@Override
	public int insertSelective(UnderwriteLog record) throws ManagerException {
		try {
			return underWriteLogMapper.insertSelective(record);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public UnderwriteLog getUnderwriteLogByInterestId(Long interestId) throws ManagerException {
		try {
			return underWriteLogMapper.getUnderwriteLogByInterestId(interestId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	/**
	 * 
	 * @desc 保存垫资记录
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月29日 下午2:22:02
	 *
	 */
	@Override
	public int saveUnderWriteLog(ProjectInterestBiz project) throws ManagerException {
		UnderwriteLog underwriteLog=new UnderwriteLog();
		underwriteLog.setProjectInterestId(project.getInterestId());
		underwriteLog.setUnderwriteMemberId(project.getThirdMemberId());
		underwriteLog.setPayablePrincipal(project.getPayablePrincipal());
		underwriteLog.setPayableInterest(project.getPayableInterest());
		underwriteLog.setUnderwriteStatus(1);
		underwriteLog.setCreateTime(new Date());
		underwriteLog.setDelFlag(1);
		try {
			return this.insertSelective(underwriteLog);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	/**
	 * 
	 * @desc 根据项目本息id  更新垫资表
	 * @param interestId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月22日 下午5:10:10
	 *
	 */
	@Override
	public int updateUnderWriteLogByInterestId(Long interestId) throws ManagerException {
			try {
				return underWriteLogMapper.updateUnderWriteLogByInterestId(interestId);
			} catch (Exception ex) {
				throw new ManagerException(ex);
			}
	}
	@Override
	public int updateForOverdueRepaySuccess(int beforeStatus, int afterStatus,String remarks, Long underwriteId) throws ManagerException {
		try {
			return underWriteLogMapper.updateForOverdueRepaySuccess(beforeStatus,afterStatus,remarks,underwriteId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	@Override
	public int updateUnderWriteByInterestId(Long interestId) throws ManagerException {
			try {
				return underWriteLogMapper.updateUnderWriteByInterestId(interestId);
			} catch (Exception ex) {
				throw new ManagerException(ex);
			}
	}
	
	@Override
	public int deleteByInterestId(Long interestId) throws ManagerException {
		try {
			return underWriteLogMapper.deleteByInterestId(interestId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
		
}
