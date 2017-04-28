package com.yourong.api.service;

import com.yourong.api.dto.MemberInfoDto;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberInfo;

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
	public ResultDTO<MemberInfo> saveOrUpdateMemberInfoByMemberId(MemberInfoDto infoDto)  throws ManagerException;
	/**
	 * 
	 * @Description:
	 * @param builder
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:
	 */
	public ResultDTO getEvaluaByMemberId(DynamicParamBuilder builder)throws ManagerException;

	ResultDTO getEvaluationByMemberId(Long memberId) throws ManagerException;
	
	public ResultDO<MemberInfo> saveMemberInfoByMemberId(MemberSessionDto memberSessionDto,
			int evaluationScore);
	
	ResultDTO getSignInfoByMemberId(Long memberId) throws ManagerException;
	
	
}
