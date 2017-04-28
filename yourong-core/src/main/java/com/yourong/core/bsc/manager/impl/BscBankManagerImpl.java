package com.yourong.core.bsc.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.dao.BscBankMapper;
import com.yourong.core.bsc.manager.BscBankManager;
import com.yourong.core.bsc.model.BscBank;

@Component
public class BscBankManagerImpl implements BscBankManager {

	@Autowired
	private BscBankMapper bscBankMapper;
	
	@Override
	public BscBank getBscBankByCode(String code) throws ManagerException {
		try{
			return bscBankMapper.getBscBankByCode(code);
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<BscBank> findByPage(Page<BscBank> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try{
			return bscBankMapper.findByPage(pageRequest, map);
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertBank(BscBank bscBank) throws ManagerException {
		try{
			return bscBankMapper.insertSelective(bscBank);
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	public BscBank selectBscBank(BscBank bscBank) throws ManagerException {
		try{
			return bscBankMapper.selectBscBank(bscBank);
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateBank(BscBank bscBank) throws ManagerException {
		try{
			if(bscBankMapper.updateByPrimaryKeySelective(bscBank) == 1) {
				return 1;
			} else {
				throw new ManagerException("银行信息更新条目大于1");
			}
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDeleteBanks(long[] ids) throws ManagerException {
		try{
			return bscBankMapper.batchDelete(ids);
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	public List<BscBank> getBankList() throws ManagerException {
		try{
			return bscBankMapper.getBankList();
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}
	
}
