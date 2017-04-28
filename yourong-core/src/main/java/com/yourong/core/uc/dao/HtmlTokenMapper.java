package com.yourong.core.uc.dao;

import org.springframework.stereotype.Repository;

import com.yourong.core.uc.model.MemberHtmlToken;

@Repository
public interface HtmlTokenMapper {
	
	int insertSelective(MemberHtmlToken record);
	
	/**逻辑删除令牌**/
    int deleteHtmlMemberTokenByMemberID(Long memberID);
	
	MemberHtmlToken selectByPrimaryKey(Long id);
	
	MemberHtmlToken selectByMemberId(Long memberId);
	

}
