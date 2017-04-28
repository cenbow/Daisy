package com.yourong.core.ic.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseDetail;

public interface LeaseDetailMapper {
	int deleteByPrimaryKey(Long id);

	int insert(LeaseDetail record);

	int insertSelective(LeaseDetail record);

	LeaseDetail selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(LeaseDetail record);

	int updateByPrimaryKey(LeaseDetail record);

	Page<LeaseDetail> findByPage(Page<LeaseDetail> pageRequest, @Param("map") Map<String, Object> map);

	int batchDelete(@Param("ids") long[] ids);

	List<LeaseDetail> selectForPagin(@Param("map") Map<String, Object> map);

	int selectForPaginTotalCount(@Param("map") Map<String, Object> map);

	/**
	 * 根据租赁分红id查询租赁记录的条数
	 * 
	 * @param leaseBonusId
	 * @return
	 */
	int selectCountByLeaseBonusId(@Param("leaseBonusId") Long leaseBonusId);

	/**
	 *根据租赁分红id查询已分红的租赁记录 
	 * 
	 * @param leaseBonusId
	 * @return
	 */
	List<LeaseDetail> selectListByLeaseBonusId(@Param("leaseBonusId") Long leaseBonusId);

	/**
	 * 根据项目id获取租赁记录
	 * 
	 * @param projectId
	 * @return
	 */
	List<LeaseDetail> findListByProjectId(@Param("projectId") Long projectId);

	/**
	 * 获取一个分红项目中用户已分红的总额
	 * @param leaseBonusId
	 * @return
	 */
	BigDecimal getTotalUserBonusByLeaseBonusId(@Param("leaseBonusId")Long leaseBonusId);
	
	/**
	 * 根据状态获取项目分红期数
	 * @param leaseBonusId
	 * @return
	 */
	int getLeaseDetailCountByStatus(@Param("map")Map<String, Object> map);
}