package com.yourong.api.service;

import com.yourong.core.uc.model.MemberHtmlToken;

public interface HtmlTokenService {
	
	 public  void  insertSelective (MemberHtmlToken tokenType );
	 
	 public MemberHtmlToken selectByPrimaryKey(Long id);
	 
	 public int deleteHtmlMemberTokenByMemberID(Long memberID);
	 
	 public MemberHtmlToken selectByMemberId(Long memberId);

}
