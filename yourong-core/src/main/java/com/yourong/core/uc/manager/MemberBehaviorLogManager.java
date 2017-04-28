package com.yourong.core.uc.manager;


public interface MemberBehaviorLogManager {

	
	Integer logInsert(Long memberId,String sourceId,Integer type,Integer operatWay,String remarks);
	
	Integer logInsertA(Long memberId, String sourceId, Integer type,
			Integer operatWay, String device, String deviceParam,
			String anchor, String remarks);
}
