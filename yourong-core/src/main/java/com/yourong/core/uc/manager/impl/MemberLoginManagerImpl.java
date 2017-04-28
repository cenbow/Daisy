package com.yourong.core.uc.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.dao.MemberLoginMapper;
import com.yourong.core.uc.manager.MemberLoginManager;
import com.yourong.core.uc.model.MemberLogin;
@Component
public class MemberLoginManagerImpl implements MemberLoginManager{
    @Autowired
    private MemberLoginMapper memberLoginMapper;

    @Override
    public int insert(MemberLogin record) throws ManagerException {
	try {
	    return memberLoginMapper.insert(record);
	} catch (Exception e) {
	    throw new ManagerException(e);
	}
    }

}
