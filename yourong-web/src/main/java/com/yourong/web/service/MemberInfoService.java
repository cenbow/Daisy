package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberInfo;
import com.yourong.web.dto.MemberInfoDto;

public interface MemberInfoService {

	/**
	 * 
	 * @param memberId
	 * @return
	 */
	public MemberInfoDto getMemberInfoByMemberId(Long memberId);
	
	/**
	 * 
	 * @param infoDto
	 * @return
	 */
	public ResultDO<MemberInfo> saveOrUpdateMemberInfoByMemberId(MemberInfoDto infoDto)  throws ManagerException;
	/**
	 * 
	 * @Description:保存测评结果
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月3日 下午5:57:56
	 */
	public ResultDO<MemberInfo> UpdateMemberInfoByMemberId(Long memberId,int evaluationScore)throws ManagerException;
	
}
