package com.yourong.core.uc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.uc.model.MemberCheck;
import com.yourong.core.uc.model.query.MemberCheckQuery;

public interface MemberCheckMapper {

	/**
	 *  插入签到记录
	 * @param memberCheck
	 * @return
	 */
    int insertSelective(MemberCheck memberCheck);

    /**
     * 通过条件查询会员签到记录
     * @param query
     * @return
     */
	List<MemberCheck> queryMemberCheckListByQuery(MemberCheckQuery query);
	
	/**
	 * 统计会员签到累计人气值
	 * @param memberId
	 * @return
	 */
	Integer countPopularityValueByMemberId(Long memberId);

	/**
	 * 判断用户当天有没有签到
	 * @param memberId
	 * @param todayYMD
	 * @return
	 */
	MemberCheck queryMemberCheckToday(@Param("memberId")Long memberId, @Param("todayYMD")String todayYMD);
}