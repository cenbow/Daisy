package com.yourong.core.uc.dao;
import com.yourong.core.uc.model.MemberReport;

public interface MemberReportMapper {
	int insert(MemberReport memberReport);

	MemberReport selectByPrimaryKey(Long memberId);

	Integer updateByPrimaryKey(MemberReport memberReport);

	int selectCountMemberReport(Long memberId);
	
	int countHaveReported();
}