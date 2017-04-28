package com.yourong.core.uc.manager;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberCheck;
import com.yourong.core.uc.model.query.MemberCheckQuery;

public interface MemberCheckManager {
	/**
	 * 插入签到记录
	 * @param check
	 * @return
	 * @throws ManagerException
	 */
    int insert(MemberCheck check) throws ManagerException;
    
    /**
     * 通过条件查询会员签到记录
     * @param query
     * @return
     */
    List<MemberCheck> queryMemberCheckListByQuery(MemberCheckQuery query) throws ManagerException;
    
    /**
     * 在指定时间内是否有过签到
     * @param memberId
     * @param startTime
     * @param endTime
     * @return
     */
    public boolean isChecked(Long memberId) throws ManagerException;

    /**
     * 获取今日签到获取的人气值
     * @param memberId
     * @return
     * @throws ManagerException
     */
	int getPopularityValueForChecked(Long memberId) throws ManagerException;
	
	/**
	 * 统计会员签到累计人气值
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	int countPopularityValueByMemberId(Long memberId) throws ManagerException;
}
