package com.yourong.api.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.api.service.MemberBehaviorLogService;
import com.yourong.core.uc.manager.MemberBehaviorLogManager;

@Service
public class MemberBehaviorLogServiceImpl implements MemberBehaviorLogService {

	private Logger logger = LoggerFactory
			.getLogger(MemberBehaviorLogServiceImpl.class);

	@Autowired
	private MemberBehaviorLogManager memberBehaviorLogManager;

	@Override
	public void memberBehaviorLogInsert(Long memberId, String sourceId,
			Integer type, Integer operatWay, String device, String deviceParam,
			String anchor, String remarks) {
		memberBehaviorLogManager.logInsertA(memberId, sourceId, type,
				operatWay, device, deviceParam, anchor, remarks);
	}

}
