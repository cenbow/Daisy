package com.yourong.core.uc.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberHtmlToken;

public interface HtmlTokenManager {
	
	public int insertSelective(MemberHtmlToken record) throws ManagerException;
	
	 public int deleteHtmlMemberTokenByMemberID(Long id) throws ManagerException;
	
	public MemberHtmlToken selectByPrimaryKey(Long id) throws ManagerException;
	
	public MemberHtmlToken selectByMemberId(Long memberId) throws ManagerException;

}
