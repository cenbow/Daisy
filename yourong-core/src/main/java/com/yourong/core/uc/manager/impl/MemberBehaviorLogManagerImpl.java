package com.yourong.core.uc.manager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.core.uc.dao.MemberBehaviorLogMapper;
import com.yourong.core.uc.manager.MemberBehaviorLogManager;
import com.yourong.core.uc.model.MemberBehaviorLog;

@Component
public class MemberBehaviorLogManagerImpl implements MemberBehaviorLogManager {

	@Autowired
	private MemberBehaviorLogMapper memberBehaviorLogMapper;

	private Logger logger = LoggerFactory
			.getLogger(MemberBehaviorLogManagerImpl.class);

	@Override
	public Integer logInsert(Long memberId, String sourceId, Integer type,
			Integer operatWay,  String remarks) {
		 return this.logInsertA(memberId, sourceId, type, operatWay, null, null, null, remarks);
	}
	
	@Override
	public Integer logInsertA(Long memberId, String sourceId, Integer type,
			Integer operatWay, String device, String deviceParam,
			String anchor, String remarks) {
		try {
			MemberBehaviorLog memberBehaviorLog = new MemberBehaviorLog();
			memberBehaviorLog.setMemberId(memberId);
			memberBehaviorLog.setSourceId(sourceId);
			memberBehaviorLog.setType(type);
			memberBehaviorLog.setOperatWay(operatWay);
			memberBehaviorLog.setDevice(device);
			memberBehaviorLog.setDeviceParam(deviceParam);
			memberBehaviorLog.setAnchor(anchor);
			memberBehaviorLog.setRemarks(remarks);
			memberBehaviorLog.setOperatTime(DateUtils.getCurrentDate());
			return memberBehaviorLogMapper.insert(memberBehaviorLog);
		} catch (Exception e) {
			logger.error(
					"记录用户行为日志异常，memberId={},sourceId={},type={},operatWay={}",
					memberId, sourceId, type, operatWay, e);
		}
		return null;
	}
	

}
