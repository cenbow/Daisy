/**
 * 
 */
package com.yourong.core.tc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.cms.model.Banner;
import com.yourong.core.tc.model.ContractSign;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年7月6日下午2:38:46
 */
public interface ContractSignMapper {

	ContractSign selectByPrimaryKey(Long id);
	
	 List<ContractSign> selectListByPrimaryKey(@Param("map") Map<String,Object> map);

    int updateByPrimaryKey(ContractSign record);

	int batchInsert(List<ContractSign> record);
	
	ContractSign selectByMap(@Param("map") Map<String,Object> map);
	
	int deletByTransactionId(Long  transactionId);
	
	ContractSign getByMap(@Param("map") Map<String,Object> map);
}
