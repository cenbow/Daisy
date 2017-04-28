package com.yourong.core.ic.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseBonus;
import com.yourong.core.ic.model.biz.LeaseBonusForFront;

public interface LeaseBonusMapper {
	int deleteByPrimaryKey(Long id);

	int insert(LeaseBonus record);

	int insertSelective(LeaseBonus record);

	LeaseBonus selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(LeaseBonus record);

	int updateByPrimaryKey(LeaseBonus record);

	Page<LeaseBonus> findByPage(Page<LeaseBonus> pageRequest, @Param("map") Map<String, Object> map);

	int batchDelete(@Param("ids") long[] ids);

	List<LeaseBonus> selectForPagin(@Param("map") Map<String, Object> map);

	int selectForPaginTotalCount(@Param("map") Map<String, Object> map);

	/** 根据项目查询租赁分红记录 */
	LeaseBonus findLeaseBonusByProjectId(@Param("projectId") long projectId);

	/**
	 * 添加租赁结算,更新租赁分红数据
	 * 
	 * @param id
	 * @return
	 */
	int updateForLeaseSettlement(@Param("leaseBonus") LeaseBonus leaseBonus);

	/**
	 * 前台分页查询数据
	 * 
	 * @param map
	 * @return
	 */
	List<LeaseBonusForFront> selectForFrontPagin(@Param("baseQueryParam") BaseQueryParam baseQueryParam);

	/**
	 * 前台分页查询总条数
	 * 
	 * @param map
	 * @return
	 */
	int selectForFrontPaginTotalCount(@Param("baseQueryParam") BaseQueryParam baseQueryParam);

	/**
	 * 更新租赁分红状态
	 * 
	 * @param id
	 * @return
	 */
	int updateStatusByLeaseBonusId(@Param("id") long id);

	/**
	 * 删除租赁记录时更新租赁分红总额
	 * 
	 * @param totalIncome
	 * @param id
	 * @return
	 */
	int updateTotalIncomeByLeaseBonusId(@Param("totalIncome") BigDecimal totalIncome, @Param("id") long id);

	/**
	 * 编辑租赁记录时更新租赁分红总额
	 * @param map
	 * @return
	 */
	int updateTotalIncomeForEditLeaseDetail(@Param("map") Map<String, Object> map);

	LeaseBonus getByProject(@Param("projectId") Long projectId);
	
}