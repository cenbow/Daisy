package com.yourong.core.uc.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.AutoInvestSet;


public interface AutoInvestSetManager {

	AutoInvestSet selectByMemberId(Long memberId) throws ManagerException;

	int updateByPrimaryMemberIdSelective(AutoInvestSet biz) throws ManagerException;
	
}
