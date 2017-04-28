/**
 * 
 */
package com.yourong.core.uc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.core.uc.model.MemberVip;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月19日下午3:22:37
 */
@Repository
public interface MemberVipMapper {

	MemberVip selectRecentMemberVipByMemberId(@Param("memberId")Long memberId);
	
	List<MemberVip> selectMemberVipList(BaseQueryParam query);
	
	long selectMemberVipListCount(BaseQueryParam query);
	
	List<MemberVip> memberLevelUpHandle(@Param("map") Map<String, Object> map);
	
	
	Integer selectMemberVipNewByMemberId(@Param("memberId")Long memberId);
	
}
