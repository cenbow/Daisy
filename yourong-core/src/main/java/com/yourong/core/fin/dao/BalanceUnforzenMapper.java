package com.yourong.core.fin.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.fin.model.BalanceForzen;
import com.yourong.core.fin.model.BalanceUnforzen;

public interface BalanceUnforzenMapper {
	
    int insert(BalanceForzen record);

    int insertSelective(BalanceUnforzen record);
    
    BalanceForzen selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BalanceUnforzen record);

    int updateByPrimaryKey(BalanceForzen record);


	
	
	/**
	 * 
	 * @Description:根据冻结id查询解冻明细
	 * @param forzenNo
	 * @return
	 * @author: chaisen
	 * @time:2016年8月1日 上午11:41:19  
	 */
	List<BalanceUnforzen> selectUnforzenListByForzenNo(@Param("forzenNo")  String forzenNo);
	
	
	List<BalanceUnforzen> selectUnforzenListByForzenNoAndProcessing(@Param("forzenNo")  String forzenNo);

	int updateByUnforzenNoSelective(BalanceUnforzen biz);
}