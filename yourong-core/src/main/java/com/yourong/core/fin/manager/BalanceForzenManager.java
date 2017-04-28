package com.yourong.core.fin.manager;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.BalanceForzen;

@Repository
public interface BalanceForzenManager {
	
	
	public int insertSelective(BalanceForzen forzen) throws ManagerException;

	int updateByPrimaryKeySelective(BalanceForzen forzen)
			throws ManagerException;

	public BalanceForzen selectBalanceForzenByForzenNo(String forzenNo) throws ManagerException;

	public Page<BalanceForzen> showForzenPage(Page<BalanceForzen> pageRequest,
			Map<String, Object> map)throws ManagerException;

	public BalanceForzen selectByPrimayKey(Long id)throws ManagerException;
		
}
