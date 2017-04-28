package com.yourong.core.fin.manager;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.model.BalanceForzen;
import com.yourong.core.fin.model.BalanceUnforzen;

@Repository
public interface BalanceUnForzenManager {
	
	
	public int insertSelective(BalanceUnforzen unForzen) throws ManagerException;

	int updateByPrimaryKeySelective(BalanceUnforzen unForzen)
			throws ManagerException;

	public BalanceForzen selectBalanceForzenByForzenNo(String forzenNo) throws ManagerException;
	
	/**
	 * 
	 * @Description:查询冻结明细
	 * @param forzenNo
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年8月1日 上午11:15:33
	 */
	public List<BalanceUnforzen> selectUnforzenListByForzenNo(String forzenNo) throws ManagerException;


		
}
