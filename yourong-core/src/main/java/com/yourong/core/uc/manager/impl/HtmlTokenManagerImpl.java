package com.yourong.core.uc.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.dao.HtmlTokenMapper;
import com.yourong.core.uc.manager.HtmlTokenManager;
import com.yourong.core.uc.model.MemberHtmlToken;

@Component
public class HtmlTokenManagerImpl implements HtmlTokenManager {

	@Autowired
	private HtmlTokenMapper htmlTokenMapper;

	@Override
	public int insertSelective(MemberHtmlToken record) throws ManagerException {
		try {
			return htmlTokenMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


    @Override
    public int deleteHtmlMemberTokenByMemberID(Long id) throws ManagerException {
        try {
            return htmlTokenMapper.deleteHtmlMemberTokenByMemberID(id);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }
	
	
	@Override
	public MemberHtmlToken selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return htmlTokenMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public MemberHtmlToken selectByMemberId(Long memberId) throws ManagerException {
		try {
			return htmlTokenMapper.selectByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
}
