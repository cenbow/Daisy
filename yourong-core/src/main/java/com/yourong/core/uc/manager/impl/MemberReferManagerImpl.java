package com.yourong.core.uc.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.StringUtil;
import com.yourong.core.mc.model.biz.ActivityForKing;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.dao.MemberReferMapper;
import com.yourong.core.uc.manager.MemberReferManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberRefer;

@Component
public class MemberReferManagerImpl implements MemberReferManager {
	
	@Autowired
	private MemberReferMapper memberReferMapper;
	@Autowired 
	private MemberMapper memberMapper;

	@Override
	public int insertSelective(MemberRefer record) throws ManagerException {
		try{
			return memberReferMapper.insertSelective(record);
		} catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public List<ActivityForKing> getRefferalCountList() throws ManagerException {
		List<ActivityForKing> activityForKings = memberReferMapper.getRefferalCountList();
		if(Collections3.isNotEmpty(activityForKings)) {
			for (ActivityForKing activityForKing : activityForKings) {
				Member member = memberMapper.selectByPrimaryKey(activityForKing.getMemberId());
				activityForKing.setUsername(StringUtil.maskUserNameOrMobile(member.getUsername(), member.getMobile()));
			}
		}
		return activityForKings;
	}

}
