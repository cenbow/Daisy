package com.yourong.backend.uc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.uc.service.MemberInfoService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.StringUtil;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.model.MemberInfo;

@Service
public class MemberInfoServiceImpl implements MemberInfoService {
	
	private static Logger logger = LoggerFactory.getLogger(MemberInfoServiceImpl.class);

	@Autowired
	private MemberInfoManager memberInfoManager;
	
	@Override
	public MemberInfo getMemberInfoByMemberId(Long memberId) {
		try{
			return memberInfoManager.getMemberInfoByMemberId(memberId);
		} catch(ManagerException e){
			logger.error("getMemberInfoByMemberId",e);
		}
		return null;
	}

	@Override
	public int saveMemberInfoByMemberId(MemberInfo memberInfo) {
		try{
			if(StringUtil.isBlank(memberInfo.getCensusRegisterId())) {
				memberInfo.setCensusRegisterId(null);
			}
			if(StringUtil.isBlank(memberInfo.getCensusRegisterName())) {
				memberInfo.setCensusRegisterName(null);
			}
			return memberInfoManager.saveMemberInfoByMemberId(memberInfo);
		} catch(ManagerException e){
			logger.error("保存用户信息表失败",e);
		}
		return 0;
	}

}
