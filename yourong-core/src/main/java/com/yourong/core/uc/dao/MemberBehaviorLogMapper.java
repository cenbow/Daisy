package com.yourong.core.uc.dao;

import org.springframework.stereotype.Repository;

import com.yourong.core.uc.model.MemberBehaviorLog;

@Repository
public interface MemberBehaviorLogMapper {
	
	 int insert(MemberBehaviorLog memberBehaviorLog);

}
