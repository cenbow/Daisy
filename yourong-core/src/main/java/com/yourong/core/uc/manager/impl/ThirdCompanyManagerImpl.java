package com.yourong.core.uc.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.dao.MemberBankCardMapper;
import com.yourong.core.uc.dao.ThirdCompanyMapper;
import com.yourong.core.uc.manager.ThirdCompanyManager;
import com.yourong.core.uc.model.ThirdCompany;
@Component
public class ThirdCompanyManagerImpl implements ThirdCompanyManager{
	@Autowired
    private ThirdCompanyMapper thirdCompanyMapper;
	@Override
	public ThirdCompany getCompanyByMemberId(Long underwriteMemberId)throws ManagerException {
        try {
            return thirdCompanyMapper.getCompanyByMemberId(underwriteMemberId);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }
	@Override
	public List<ThirdCompany> getAllThirdCompany() throws ManagerException {
        try {
            return thirdCompanyMapper.getThirdCompanyList();
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }
	/**
	 * 
	 * @desc 获取第三方垫资公司id
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月1日 下午3:59:59
	 *
	 */
	@Override
	public ThirdCompany getThirdCompanyId(Long interestId,Long projectId) throws ManagerException {
        try {
            return thirdCompanyMapper.getThirdCompanyId(interestId,projectId);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }
	
}
