package com.yourong.core.uc.manager;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberInfo;

public interface MemberInfoManager {
	
	/**
	 * 获得客户详细信息
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public MemberInfo getMemberInfoByMemberId(Long memberId) throws ManagerException;
	
	/**
	 * 保存用户详细信息
	 * @param info
	 * @return
	 * @throws ManagerException
	 */
	public int saveMemberInfoByMemberId(MemberInfo info) throws ManagerException;
	
	/**
	 * 修改用户详细信息
	 * @param info
	 * @return
	 * @throws ManagerException
	 */
	public int updateMemberInfoByMemberId(MemberInfo info) throws ManagerException;
	/**
	 * 
	 * @Description:风险测评
	 * @param memberId
	 * @param evaluationScore
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年6月15日 下午1:42:25
	 */
	public MemberInfo updateMemberInfoByMemberId(Long memberId, int evaluationScore) throws ManagerException;
	
	
	public ResultDO<MemberInfo> updateMemberInfoByMemberIdS(Long memberId, int evaluationScore)throws ManagerException;

}
