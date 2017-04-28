package com.yourong.core.uc.manager;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberReport;
	
public interface MemberReportManager {

	ResultDO<MemberReport> insert(MemberReport memberReport) throws ManagerException;

	MemberReport selectByPrimaryKey(Long id) throws ManagerException;
	
	Integer updateByPrimaryKey(MemberReport memberReport) throws ManagerException;

	int selectCountMemberReport(@Param("memberId") Long memberId)throws ManagerException;
	
	int countHaveReported()throws ManagerException;

}