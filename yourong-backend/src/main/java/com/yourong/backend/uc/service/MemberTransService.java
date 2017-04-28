package com.yourong.backend.uc.service;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberTrans;

public interface MemberTransService {

	/**
	 * 分页查询客户信息
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws Exception 
	 * @throws ManagerException
	 */
	public MemberTrans getMemberTransInfo(Map<String, Object> map) throws Exception;

}
