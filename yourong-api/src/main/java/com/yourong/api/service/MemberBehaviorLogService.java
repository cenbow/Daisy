package com.yourong.api.service;

public interface MemberBehaviorLogService {
	
	public void memberBehaviorLogInsert(Long memberId, String sourceId,
			Integer type, Integer operatWay,String device, String deviceParam,
			String anchor, String remarks);

}
