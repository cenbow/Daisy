package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.uc.model.MemberReport;

public interface MemberReportService {
	
	
	public ResultDO<MemberReport> saveReport(MemberReport memberReport) throws Exception;
	
	MemberReport selectByPrimaryId(Long memberId);

	public int selectCountMemberReport(Long memberId)throws Exception;

	public ResultDO<MemberReport> updateMemberReport(MemberReport memberReport)throws Exception;

	boolean memberReportIsApply(Long id);
	
	public int countHaveReported();
}
