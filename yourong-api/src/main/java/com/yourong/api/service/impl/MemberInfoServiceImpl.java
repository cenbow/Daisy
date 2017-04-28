package com.yourong.api.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yourong.api.dto.MemberInfoDto;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.MemberInfoService;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.uc.manager.AutoInvestSetManager;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.AutoInvestSet;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;

@Service
public class MemberInfoServiceImpl implements MemberInfoService {
	private Logger logger = LoggerFactory.getLogger(MemberInfoServiceImpl.class);
	
	@Autowired
	private MemberInfoManager memberInfoManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private AutoInvestSetManager autoInvestSetManager;

	@Override
	public MemberInfoDto getMemberInfoByMemberId(Long memberId) {
		MemberInfoDto memberInfoDto = null;
		try {
			MemberInfo info = memberInfoManager.getMemberInfoByMemberId(memberId);
			if(info != null){
				memberInfoDto = new MemberInfoDto();
				BeanCopyUtil.copy(info, memberInfoDto);
				Member member = memberManager.selectByPrimaryKey(memberId);
				if(member.getBirthday() != null){
					memberInfoDto.setBirthday(DateUtils.formatDatetoString(member.getBirthday(), "yyyy年MM月dd日"));
				}
				memberInfoDto.setSex(member.getSex());
			}
			return memberInfoDto;
		} catch (ManagerException e) {
			logger.error("查询用户详细信息异常:memberId"+memberId, e);
		}
		return memberInfoDto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDTO saveOrUpdateMemberInfoByMemberId(MemberInfoDto infoDto) throws ManagerException {
		ResultDTO result = new ResultDTO();
		Member member = memberManager.selectByPrimaryKey(infoDto.getMemberId());
		if(member == null){
			result.setResultCode(ResultCode.MEMBER_IS_EXIST);
			return result;
		}
		MemberInfoDto info = getMemberInfoByMemberId(infoDto.getMemberId());
		if(info != null){//更新
			MemberInfo memberInfo = new MemberInfo();
			BeanCopyUtil.copy(infoDto, memberInfo);
			memberInfo.setId(info.getId());
			memberInfo.setMemberId(info.getMemberId());
			memberInfoManager.updateMemberInfoByMemberId(memberInfo);
		}else{//添加
			MemberInfo memberInfo = new MemberInfo();
			BeanCopyUtil.copy(infoDto, memberInfo);
			memberInfoManager.saveMemberInfoByMemberId(memberInfo);
		}
		memberManager.sendCouponOnCompleteMemberInfo(infoDto.getMemberId());
		Member record = new Member();
		record.setId(member.getId());
		if(StringUtil.isNotEmpty(infoDto.getBirthday())){
			record.setBirthday(DateUtils.toDate(infoDto.getBirthday(), "yyyy年MM月dd日"));
		}
		result.setIsSuccess();
		memberManager.updateByPrimaryKeySelective(record);
		SPParameter parameter = new SPParameter();
		parameter.setMemberId(member.getId());
		SPEngine.getSPEngineInstance().run(parameter);
		return result;
	}

	/*@Override
	public ResultDO<MemberInfo> UpdateMemberInfoByMemberId(Long memberId, int evaluationScore) throws ManagerException {
		try {
			return memberInfoManager.updateMemberInfoByMemberId(memberId,evaluationScore);
		} catch (ManagerException e) {
			logger.error("保存信息异常:memberId"+memberId, e);
		}
		return null;*/
	
	//}


	@Override
	public ResultDTO getEvaluaByMemberId(DynamicParamBuilder builder) throws ManagerException {
		ResultDTO<Map<String, Object>> result = new ResultDTO();
		try {
			Map<String, Object> map = Maps.newHashMap();
			if (!builder.isMemberFlag()) {
				result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return result;
			}
			MemberInfo info = memberInfoManager.getMemberInfoByMemberId(builder.getMemberId());
			int evaluationScore=0;
			if(info != null){
				evaluationScore=info.getEvaluationScore();
			}
			String avatar = memberManager.getMemberAvatar(builder.getMemberId());
			map.put("avatar", avatar);
			Member member=memberManager.selectByPrimaryKey(builder.getMemberId());
			if(member!=null){
				if (StringUtil.isNotBlank(member.getUsername())) {
					map.put("userName", StringUtil.maskString(member.getUsername(),StringUtil.ASTERISK, 1, 1, 3));
				}
				map.put("mobile", StringUtil.maskMobileCanNull(member.getMobile()));
			}
			map.put("evaluationScore", evaluationScore);
			result.setResult(map);
		} catch (ManagerException e) {
			logger.error("获取测评初始化失败", e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}
	
	@Override
	public ResultDTO getEvaluationByMemberId(Long memberId) throws ManagerException {
		ResultDTO<Map<String, Object>> result = new ResultDTO();
		try {
			Map<String, Object> map = Maps.newHashMap();
			MemberInfo info = memberInfoManager.getMemberInfoByMemberId(memberId);
			int evaluationScore=0;
			String evaluatName="";
			if(info != null){
				evaluationScore=info.getEvaluationScore();
				if(info.getEvaluationScore()!=null){
					if(info.getEvaluationScore()>=8&&info.getEvaluationScore()<=12){
						info.setEvaluationName("保守型");
					}else if(info.getEvaluationScore()>=13&&info.getEvaluationScore()<=17){
						info.setEvaluationName("稳健型");
					}else if(info.getEvaluationScore()>=18&&info.getEvaluationScore()<=24){
						info.setEvaluationName("平衡型");
					}else if(info.getEvaluationScore()>=25&&info.getEvaluationScore()<=28){
						info.setEvaluationName("进取型");
					}else if(info.getEvaluationScore()>=29&&info.getEvaluationScore()<=33){
						info.setEvaluationName("积极型");
					}else{
						info.setEvaluationName("");
					}
					evaluatName=info.getEvaluationName();
				}
				}
			Member member=memberManager.selectByPrimaryKey(memberId);
			map.put("email", member.getEmail()!=null?member.getEmail():"");
			map.put("signWay", member.getSignWay());
			map.put("evaluationName", evaluatName);
			map.put("evaluationScore", evaluationScore);
			map.put("memberId", memberId.toString());
			//是否开通委托扣款
			ResultDO<Boolean> rDO  = (ResultDO<Boolean>) memberManager.synWithholdAuthority(memberId);
			map.put("isWithholdAuthorityFlag", rDO.getResult());
			//是否设置支付密码
			ResultDO<Boolean> rDOPayPass  = (ResultDO<Boolean>) memberManager.synPayPasswordFlag(memberId);
			map.put("isPayPasswordFlag", rDOPayPass.getResult());
			Boolean isRealName = false;
			if(member !=null && StringUtil.isNotBlank(member.getTrueName()) && StringUtil.isNotBlank(member.getIdentityNumber())){
				isRealName = true;
			}	
			map.put("isRealName",isRealName);
			Boolean isAutoInvest = false;
			AutoInvestSet autoInvest=autoInvestSetManager.selectByMemberId(memberId);
			if(autoInvest!=null&&autoInvest.getInvestFlag()==1){
				isAutoInvest = true;
			}
			map.put("isAutoInvest",isAutoInvest);
			
			result.setResult(map);
		} catch (ManagerException e) {
			logger.error("获取测评初始化失败", e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

	@Override
	public ResultDO<MemberInfo> saveMemberInfoByMemberId(MemberSessionDto memberSessionDto,int evaluationScore) { 
		ResultDO<MemberInfo> result = new ResultDO<MemberInfo>();
		//登录认证
		if (memberSessionDto==null) {
			result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return result;
		}
		if(evaluationScore==0){
			result.setResultCode(ResultCode.EVALUATION_SCORE_RESULT);
			return result;
		}
	    try {
	    	MemberInfo info= memberInfoManager.updateMemberInfoByMemberId(memberSessionDto.getId(),evaluationScore);
	    	result.setResult(info);
	    } catch (ManagerException e) {
	        logger.error("测评失败,evaluationScore:" + evaluationScore);
	    }
	    return result;
    }

	@Override
	public ResultDTO getSignInfoByMemberId(Long memberId) throws ManagerException {
		ResultDTO<Map<String, Object>> result = new ResultDTO();
		try {
			Map<String, Object> map = Maps.newHashMap();
			Member member=memberManager.selectByPrimaryKey(memberId);
			map.put("name", member.getTrueName());
			map.put("signWay", member.getSignWay());

			result.setResult(map);
		} catch (ManagerException e) {
			logger.error("获取用户签署信息异常,memberId={}",memberId, e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}
	
	
}
