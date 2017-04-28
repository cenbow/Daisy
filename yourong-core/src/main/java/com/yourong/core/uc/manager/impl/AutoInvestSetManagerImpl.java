package com.yourong.core.uc.manager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.dao.AutoInvestSetMapper;
import com.yourong.core.uc.manager.AutoInvestSetManager;
import com.yourong.core.uc.model.AutoInvestSet;

@Component
public class AutoInvestSetManagerImpl  implements AutoInvestSetManager {
	private Logger logger = LoggerFactory.getLogger(AutoInvestSetManagerImpl.class);
	@Autowired
	private AutoInvestSetMapper autoInvestMapper;
	
	@Override
	public AutoInvestSet selectByMemberId(Long memberId) throws ManagerException {
		try {
			return autoInvestMapper.selectByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}

	}

	@Override
	public int updateByPrimaryMemberIdSelective(AutoInvestSet biz) throws ManagerException {
		try {
			return autoInvestMapper.updateByPrimaryMemberIdSelective(biz);
		} catch (Exception e) {
			throw new ManagerException(e);
		}

	}
}
