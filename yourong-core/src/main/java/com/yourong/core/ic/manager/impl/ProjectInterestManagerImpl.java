package com.yourong.core.ic.manager.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.dao.ProjectInterestMapper;
import com.yourong.core.ic.manager.ProjectInterestManager;
import com.yourong.core.ic.model.ProjectInterest;

@Component
public class ProjectInterestManagerImpl implements ProjectInterestManager {
	@Autowired
	private ProjectInterestMapper projectInterestMapper;

	@Override
	public int insert(ProjectInterest record) throws ManagerException {
		try {
			return projectInterestMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(ProjectInterest record) throws ManagerException {
		try {
			return projectInterestMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchInsertProjectInterest(List<ProjectInterest> projectInterestList, Long projectId) throws ManagerException {
		try {
			for(ProjectInterest projectInterest : projectInterestList){
				projectInterest.setProjectId(projectId);
				projectInterest.setGmtCreated(DateUtils.getCurrentDateTime());
				projectInterest.setGmtModified(DateUtils.getCurrentDateTime());
			}
			return projectInterestMapper.batchInsertProjectInterest(projectInterestList);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int deleteProjectInterestByProjectId(Long projectId) throws ManagerException {
		try {
			return projectInterestMapper.deleteProjectInterestByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ProjectInterest> getProjectInterestByProjectId(Long projectId)
			throws ManagerException {
		try {
			return projectInterestMapper.getProjectInterestByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getAnnualizedRateByInvestAmount(BigDecimal investAmount,
			Long projectId) throws ManagerException {
		try {
			return projectInterestMapper.getAnnualizedRateByInvestAmount(investAmount, projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(ProjectInterest record) throws ManagerException {
		try {
			return projectInterestMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
