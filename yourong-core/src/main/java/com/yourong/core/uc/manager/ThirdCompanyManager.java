package com.yourong.core.uc.manager;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.ThirdCompany;


public interface ThirdCompanyManager {

	ThirdCompany getCompanyByMemberId(Long underwriteMemberId)throws ManagerException;

	List<ThirdCompany> getAllThirdCompany()throws ManagerException;
	/**
	 * 
	 * @Description:获取第三方垫资公司id
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月1日 下午3:59:12
	 */
	ThirdCompany getThirdCompanyId(Long borrowerId, Long projectId) throws ManagerException;
	
}
