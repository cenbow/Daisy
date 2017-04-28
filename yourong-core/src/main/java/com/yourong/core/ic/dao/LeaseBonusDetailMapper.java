package com.yourong.core.ic.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.ic.model.LeaseBonusDetail;
import com.yourong.core.ic.model.query.LeaseBonusDetailQuery;

public interface LeaseBonusDetailMapper {
	int deleteByPrimaryKey(Long id);

	int insert(LeaseBonusDetail record);

	int insertSelective(LeaseBonusDetail record);

	LeaseBonusDetail selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(LeaseBonusDetail record);

	int updateByPrimaryKey(LeaseBonusDetail record);

	int batchDelete(@Param("ids") long[] ids);

	List<LeaseBonusDetail> selectForPagin(@Param("map") Map<String, Object> map);

	int selectForPaginTotalCount(@Param("map") Map<String, Object> map);

	/**
	 * 项目分红明细数据
	 * 
	 * @param leaseBonusDetailQuery
	 * @return
	 */
	List<LeaseBonusDetail> selectBonusDetailByProjectIdForPagin(
			@Param("leaseBonusDetailQuery") LeaseBonusDetailQuery leaseBonusDetailQuery);

	/**
	 * 项目分红明细总数
	 * 
	 * @param leaseBonusDetailQuery
	 * @return
	 */
	int selectCountByProjectIdForPagin(@Param("leaseBonusDetailQuery") LeaseBonusDetailQuery leaseBonusDetailQuery);
	
	/**
	 * 根据交易id获取分红明细
	 * @param transactionId
	 * @return
	 */
	List<LeaseBonusDetail> findListByTransactionId(@Param("transactionId") Long transactionId);
	
	/**
	 * 根据交易ID获取总收益 
	 * @param transactionId
	 * @return
	 */
	LeaseBonusDetail getLeaseTotalIncomeByTransactionId(@Param("transactionId") Long transactionId);
	
}