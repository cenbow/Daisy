package com.yourong.core.sales.manager;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.exception.ManagerException;

public interface ProjectRuleManager {
	/**
	 * 项目第一个投资用户
	 * @param projectId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Long firstInvestmentProjectMember(Long projectId, Date startTime, Date endTime);
	
	/**
	 * 项目最后一个投资用户
	 * @param projectId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Long lastInvestmentProjectMember(Long projectId, Date startTime, Date endTime);
	
	/**
	 * 单笔投资最大金额
	 * @param projectId
	 * @param memberId
	 * @return
	 */
	public BigDecimal investmentMaxAmountProject(Long projectId, Long memberId);
	
	/**
	 * 好友单笔投资最大金额
	 * @param projectId
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsInvestmentMaxAmountProject(Long projectId, Long memberId);
	
	/**
	 * 项目整点投资
	 * @param projectId
	 * @param memberId
	 * @param punctuality
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public BigDecimal integralPointInvestmentProject(Long projectId, Long memberId, String punctuality , Date startTime, Date endTime);
}
