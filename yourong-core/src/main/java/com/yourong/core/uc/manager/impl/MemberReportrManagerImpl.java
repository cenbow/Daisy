package com.yourong.core.uc.manager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.StringUtil;
import com.yourong.core.uc.dao.MemberReportMapper;
import com.yourong.core.uc.manager.MemberReportManager;
import com.yourong.core.uc.model.MemberReport;

@Service
public class MemberReportrManagerImpl implements MemberReportManager {
	private static Logger logger = LoggerFactory.getLogger(MemberReportrManagerImpl.class);
	@Autowired
	private MemberReportMapper reportMapper;
	public ResultDO<MemberReport> insert(MemberReport memberReport) throws ManagerException {
		ResultDO<MemberReport> resultDO = new ResultDO<MemberReport>();
		try {
			int totalCount = this.selectCountMemberReport(memberReport.getMemberId());
			if (totalCount > 0) {
				resultDO.setResultCode(ResultCode.ACTIVITY_XIAOMING_STORY_YET_JOIN_ACTIVITY_ERROR);
				resultDO.setSuccess(false);
				return resultDO;
			}
			//校验省份是否为空
			if(StringUtil.isBlank(memberReport.getProvince())){
				resultDO.setSuccess(false);
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return resultDO;
			}
			//校验出行方式是否为空
			if(null==memberReport.getTravelMode()){
				resultDO.setSuccess(false);
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return resultDO;
			}
			//校验省份和出行方式是否同时为空
			if(StringUtil.isBlank(memberReport.getProvince())&&null==memberReport.getTravelMode()){
				resultDO.setSuccess(false);
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return resultDO;
			}
			//校验省份的长度
			if(memberReport.getProvince().length() > 8){
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_TOO_LONG);
				resultDO.setSuccess(false);
				return resultDO;
			}
			if(memberReport.getTravelMode()!=StatusEnum.TRAVEL_MODE_ONE.getStatus()&&memberReport.getTravelMode()!=StatusEnum.TRAVEL_MODE_TWO.getStatus()&&memberReport.getTravelMode()!=StatusEnum.TRAVEL_MODE_THREE.getStatus()){
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM);
				resultDO.setSuccess(false);
				return resultDO;
			}
			int i=reportMapper.insert(memberReport);
			if(i<0){
				resultDO.setSuccess(false);
				return resultDO;
			}
			return resultDO;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public MemberReport selectByPrimaryKey(Long memberId) throws ManagerException {
		try {
			return reportMapper.selectByPrimaryKey(memberId);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKey(MemberReport memberReport) throws ManagerException {
		try {
			return reportMapper.updateByPrimaryKey(memberReport);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int selectCountMemberReport(Long memberId) {
		int i=reportMapper.selectCountMemberReport(memberId);
		return i;
	}

	@Override
	public int countHaveReported() throws ManagerException {
		int i=reportMapper.countHaveReported();
		return i;
	}

	
}