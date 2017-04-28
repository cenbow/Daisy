package com.yourong.core.uc.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.dao.MemberInfoMapper;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.model.MemberInfo;

@Component
public class MemberInfoManagerImpl implements MemberInfoManager {
	
	@Autowired
	MemberInfoMapper memberInfoMapper;
	
	@Override
	public MemberInfo getMemberInfoByMemberId(Long memberId) throws ManagerException {
		try{
			return memberInfoMapper.getMemberInfoByMemberId(memberId);
		} catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	public int saveMemberInfoByMemberId(MemberInfo info)
			throws ManagerException {
		try{
			return memberInfoMapper.insertSelective(info);
		} catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateMemberInfoByMemberId(MemberInfo info)
			throws ManagerException {
		try{
			return memberInfoMapper.updateByPrimaryKeySelective(info);
		} catch(Exception e){
			throw new ManagerException(e);
		}
	}

	

	@Override
	public ResultDO<MemberInfo> updateMemberInfoByMemberIdS(Long memberId,int evaluationScore) throws ManagerException {
		ResultDO<MemberInfo> result = new ResultDO<MemberInfo>();
		MemberInfo info = getMemberInfoByMemberId(memberId);
		int i=0;
		if(info != null){//更新
			MemberInfo memberInfo = new MemberInfo();
			memberInfo.setId(info.getId());
			memberInfo.setMemberId(info.getMemberId());
			memberInfo.setEvaluationScore(evaluationScore);
			i=updateMemberInfoByMemberId(memberInfo);
		}else{//添加
			MemberInfo memberInfo = new MemberInfo();
			memberInfo.setMemberId(memberId);
			memberInfo.setEvaluationScore(evaluationScore);
			i=saveMemberInfoByMemberId(memberInfo);
		}
		if(i>0){
			MemberInfo memberInfo = getMemberInfoByMemberId(memberId);
			if(memberInfo==null){
				result.setResultCode(ResultCode.EVALUATION_SCORE_FAIL);
				return result;
			}
			if(memberInfo.getEvaluationScore()==null){
				result.setResultCode(ResultCode.EVALUATION_SCORE_FAIL);
				return result;
			}else{
				result.setResult(memberInfo);
				return result;
			}
			
		}
		return result;
	}

	@Override
	public MemberInfo updateMemberInfoByMemberId(Long memberId,
			int evaluationScore) throws ManagerException {
		MemberInfo info = getMemberInfoByMemberId(memberId);
		MemberInfo memberInf=new MemberInfo ();
		int i=0;
		if(info != null){//更新
			MemberInfo memberInfo = new MemberInfo();
			memberInfo.setId(info.getId());
			memberInfo.setMemberId(info.getMemberId());
			memberInfo.setEvaluationScore(evaluationScore);
			i=updateMemberInfoByMemberId(memberInfo);
		}else{//添加
			MemberInfo memberInfo = new MemberInfo();
			memberInfo.setMemberId(memberId);
			memberInfo.setEvaluationScore(evaluationScore);
			i=saveMemberInfoByMemberId(memberInfo);
		}
		if(i>0){
			memberInf = getMemberInfoByMemberId(memberId);
		}
		return memberInf;
		
	}

}
