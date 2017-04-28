package com.yourong.backend.uc.service;

import com.yourong.core.uc.model.MemberInfo;

public interface MemberInfoService {

	/**
	 * 获得客户详细信息
	 * @param memberId
	 * @return
	 */
	public MemberInfo getMemberInfoByMemberId(Long memberId);
	
	/**
	 * 保存会员信息表
	 * @param memberInfo
	 * @return
	 */
	public int saveMemberInfoByMemberId(MemberInfo memberInfo);
}
