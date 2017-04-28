package com.yourong.core.ic.manager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseDetail;

public interface LeaseDetailManager {
	int deleteByPrimaryKey(Long id) throws ManagerException;

	int insert(LeaseDetail record) throws ManagerException;

	int insertSelective(LeaseDetail record) throws ManagerException;

	LeaseDetail selectByPrimaryKey(Long id) throws ManagerException;

	int updateByPrimaryKeySelective(LeaseDetail record) throws ManagerException;

	int updateByPrimaryKey(LeaseDetail record) throws ManagerException;

	Page<LeaseDetail> findByPage(Page<LeaseDetail> pageRequest, Map<String, Object> map) throws ManagerException;

	int batchDelete(long[] ids) throws ManagerException;

	/**
	 * 根据租赁分红id查询租赁记录的条数
	 * 
	 * @param leaseBonusId
	 * @return
	 * @throws ManagerException
	 */
	int selectCountByLeaseBonusId(Long leaseBonusId) throws ManagerException;

	/**
	 * 根据租赁分红id查询已分红的租赁记录 
	 * 
	 * @param leaseBonusId
	 * @return
	 * @throws ManagerException
	 */
	List<LeaseDetail> selectListByLeaseBonusId(Long leaseBonusId) throws ManagerException;

	/**
	 * 根据项目id获取租赁记录
	 * 
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	List<LeaseDetail> findListByProjectId(Long projectId) throws ManagerException;
	
	/**
	 * 获取一个分红项目中用户已分红的总额
	 * @param leaseBonusId
	 * @return
	 */
	BigDecimal getTotalUserBonusByLeaseBonusId(Long leaseBonusId) throws ManagerException;
	
}