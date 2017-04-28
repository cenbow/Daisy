package com.yourong.core.ic.manager;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.model.ProjectInterest;

public interface ProjectInterestManager {

	int insert(ProjectInterest record) throws ManagerException;

	int insertSelective(ProjectInterest record) throws ManagerException;
	
	/**
	 * 批量插入阶梯收益表
	 * @param projectInterestList
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	int batchInsertProjectInterest(List<ProjectInterest> projectInterestList, Long projectId) throws ManagerException;
	
	/**
	 * 根据项目编号删除阶梯收益
	 * @param projectId
	 * @return
	 */
	int deleteProjectInterestByProjectId(Long projectId) throws ManagerException;
	
	/**
	 * 根据项目编号获得阶梯收益
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectInterest> getProjectInterestByProjectId(Long projectId) throws ManagerException;
	
	/**
	 * 根据投资金额获得年化收益
	 * @param investAmount 投资金额
	 * @param projectId 项目编号
	 * @return
	 * @throws ManagerException
	 */
	public BigDecimal getAnnualizedRateByInvestAmount(BigDecimal investAmount, Long projectId) throws ManagerException;
	
	/**
	 * @Description:更新项目的阶梯收益记录
	 * @param record
	 * @return
	 * @author: fuyili
	 * @time:2016年2月1日 下午3:13:53
	 */
	int updateByPrimaryKeySelective(ProjectInterest record)throws ManagerException;
}
