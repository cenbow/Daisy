package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.uc.model.Member;
import com.yourong.web.dto.LoginDto;
import com.yourong.web.dto.MemberDto;

public interface LoginService {
	
	
	public ResultDO<Member> login(LoginDto dto);
	
	/**
	 * 用户注册
	 * @param record
	 * @return
	 */
	ResultDO<Object> register(MemberDto record)throws Exception ;
	
	/**
	 * 异步初始化其他会员注册信息
	 * @param record
	 * author: pengyong
	 * 下午5:36:42
	 */
	public void  initOtherMemberData(MemberDto record);
	

}
