package com.yourong.web.service.impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.manager.MemberReportManager;
import com.yourong.core.uc.model.MemberReport;
import com.yourong.web.service.MemberReportService;

@Service
public class MemberReportServiceImpl implements MemberReportService {
	private static Logger logger = LoggerFactory.getLogger(MemberReportServiceImpl.class);
	@Autowired
	private MemberReportManager memberReportManager;
   
	@Override
	public ResultDO<MemberReport> saveReport(MemberReport memberReport){
		ResultDO<MemberReport> result = new ResultDO<MemberReport>();
		try {
			result=memberReportManager.insert(memberReport);
		} catch (ManagerException e) {
			logger.error("保存报名信息" , e);
		}
		return result;
		}

	@Override
	public MemberReport selectByPrimaryId(Long memberId) {
		try {
			MemberReport memberReport=memberReportManager.selectByPrimaryKey(memberId);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int selectCountMemberReport(Long memberId) {
		try {
			return memberReportManager.selectCountMemberReport(memberId);
		} catch (ManagerException e) {
			logger.error("memberId=" + memberId , e);
		}
		return 0;
	}

	@Override
	public ResultDO<MemberReport> updateMemberReport(MemberReport memberReport) {
		try {
			Integer i= memberReportManager.updateByPrimaryKey(memberReport);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean memberReportIsApply(Long id) {
		try {
			int count =memberReportManager.selectCountMemberReport(id);
			if(count>0){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("id=" + id , e);
		}
		return false;
	}

	@Override
	public int countHaveReported() {
		try {
			return memberReportManager.countHaveReported();
		} catch (ManagerException e) {
			logger.error("", e);
		}
		return 0;
	}
}
