package com.yourong.core.fin.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.dao.BalanceForzenMapper;
import com.yourong.core.fin.dao.BalanceUnforzenMapper;
import com.yourong.core.fin.manager.BalanceUnForzenManager;
import com.yourong.core.fin.model.BalanceForzen;
import com.yourong.core.fin.model.BalanceUnforzen;

@Component
public class BalanceUnForzenManagerImpl implements BalanceUnForzenManager {

	@Autowired
	private BalanceForzenMapper balanceForzenMapper;
	
	@Autowired
	private BalanceUnforzenMapper balanceUnforzenMapper;
	
	
	@Override
	public int insertSelective(BalanceUnforzen unforzen) throws ManagerException {
		try {
			return balanceUnforzenMapper.insertSelective(unforzen);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	@Override
	public int updateByPrimaryKeySelective(BalanceUnforzen unforzen) throws ManagerException {
		try {
			return balanceUnforzenMapper.updateByPrimaryKeySelective(unforzen);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public BalanceForzen selectBalanceForzenByForzenNo(String forzenNo) throws ManagerException {
		try {
			return balanceForzenMapper.selectBalanceForzenByForzenNo(forzenNo);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public List<BalanceUnforzen> selectUnforzenListByForzenNo(String forzenNo) throws ManagerException {
		try {
			return balanceUnforzenMapper.selectUnforzenListByForzenNo(forzenNo);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	
}	