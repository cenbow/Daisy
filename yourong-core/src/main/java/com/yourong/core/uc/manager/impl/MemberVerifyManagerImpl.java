package com.yourong.core.uc.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.dao.MemberVerifyMapper;
import com.yourong.core.uc.manager.MemberVerifyManager;
import com.yourong.core.uc.model.MemberVerify;

@Component
public class MemberVerifyManagerImpl implements MemberVerifyManager{

	@Autowired
	private MemberVerifyMapper memberVerifyMapper;
	
	@Override
	public int insertSelective(MemberVerify record) throws ManagerException {
		try{
			return memberVerifyMapper.insertSelective(record);
		} catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

}
