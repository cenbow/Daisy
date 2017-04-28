package com.yourong.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;
import com.yourong.web.dto.MemberInfoDto;
import com.yourong.web.service.MemberInfoService;

@Service
public class MemberInfoServiceImpl implements MemberInfoService {
	private Logger logger = LoggerFactory.getLogger(MemberInfoServiceImpl.class);
	
	@Autowired
	private MemberInfoManager memberInfoManager;
	
	@Autowired
	private MemberManager memberManager;

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
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<MemberInfo> saveOrUpdateMemberInfoByMemberId(MemberInfoDto infoDto) throws ManagerException {
		ResultDO<MemberInfo> result = new ResultDO<MemberInfo>();
		Member member = memberManager.selectByPrimaryKey(infoDto.getMemberId());
		if(member == null){
			result.setSuccess(false);
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
//		record.setSex(infoDto.getSex());
		if(StringUtil.isNotEmpty(infoDto.getBirthday())){
			record.setBirthday(DateUtils.toDate(infoDto.getBirthday(), "yyyy年MM月dd日"));
		}
		memberManager.updateByPrimaryKeySelective(record);
		SPParameter parameter = new SPParameter();
		parameter.setMemberId(member.getId());
		SPEngine.getSPEngineInstance().run(parameter);
		return result;
	}
	/**
	 * 
	 * @desc 保存测评信息
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年6月3日 下午6:09:13
	 *
	 */
	@Override
	public ResultDO<MemberInfo> UpdateMemberInfoByMemberId(Long memberId,int evaluationScore) throws ManagerException {
		try {
			return memberInfoManager.updateMemberInfoByMemberIdS(memberId,evaluationScore);
		} catch (ManagerException e) {
			logger.error("保存信息异常:memberId"+memberId, e);
		}
		return null;
	
	}

}
