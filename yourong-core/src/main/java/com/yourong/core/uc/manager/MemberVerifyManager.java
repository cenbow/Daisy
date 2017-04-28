package com.yourong.core.uc.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberVerify;

public interface MemberVerifyManager {

	/**
	 * 插入用户认证信息
	 * @param record
	 * @return
	 */
	public int insertSelective(MemberVerify record) throws ManagerException;
}
