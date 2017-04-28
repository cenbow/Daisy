package com.yourong.core.fin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.BalanceForzen;

public interface BalanceForzenMapper {
    int insert(BalanceForzen record);

    int insertSelective(BalanceForzen record);
    
    BalanceForzen selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BalanceForzen record);

    int updateByPrimaryKey(BalanceForzen record);

    Page findByPage(Page pageRequest, @Param("map") Map map);

    int batchDelete(@Param("ids") int[] ids);

    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);

	BalanceForzen selectBalanceForzenByForzenNo(String forzenNo);
	
	
	List<BalanceForzen> getOverdueLogByProjectId(Long projectId);

	int selectForzenForPaginTotalCount(@Param("map") Map<String, Object> map);
	
	
	List<BalanceForzen> selectForzenForPagin(@Param("map") Map<String, Object> map);
	
	/**
	 * 
	 * @Description:根据冻结订单号更新冻结结果
	 * @param record
	 * @return
	 * @author: chaisen
	 * @time:2016年8月1日 上午11:34:19
	 */
	int updateByForzenNoSelective(BalanceForzen record);
}