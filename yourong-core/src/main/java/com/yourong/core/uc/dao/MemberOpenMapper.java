package com.yourong.core.uc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.uc.model.MemberOpen;


public interface MemberOpenMapper {
	
	
	int insertSelective(MemberOpen record);
	
	int updateByPrimaryKeySelective(MemberOpen	 record);

	List<MemberOpen> queryMemberOpenByMemberIdAndKey(@Param("memberId")Long memberId,@Param("key")String key);
	
	MemberOpen queryMemberOpenByMember(@Param("memberId")Long memberId);
	
}