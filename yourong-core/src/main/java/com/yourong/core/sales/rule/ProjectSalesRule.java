package com.yourong.core.sales.rule;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.SPProject;
import com.yourong.core.sales.SPRuleMethod;
import com.yourong.core.sales.annotation.SPMethod;
import com.yourong.core.sales.manager.ProjectRuleManager;


public class ProjectSalesRule extends SPRuleBase {
	private static ProjectRuleManager projectRuleManager = SpringContextHolder.getBean(ProjectRuleManager.class);
	
	@Override
	public boolean execute(SPParameter parameter) {
		return super.build(parameter);
	}

	
	/**
	 * 项目第一个投资用户
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="xiangMuDYTZ")
	public boolean firstInvestmentProjectMember(SPParameter parameter){
		List<SPProject> projectList = parameter.getProjects();
		for(SPProject spp : projectList){
			Long memberId = projectRuleManager.firstInvestmentProjectMember(spp.getId(), parameter.getStartTime(), parameter.getEndTime());
			if(memberId != null && parameter.getMemberId().equals(memberId)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 项目最后一个投资用户
	 * @param parameter
	 * @return
	 */
	@SPMethod(name="xiangMuZHTZ")
	public boolean lastInvestmentProjectMember(SPParameter parameter){
		List<SPProject> projectList = parameter.getProjects();
		for(SPProject spp : projectList){
			Long memberId = projectRuleManager.lastInvestmentProjectMember(spp.getId(), parameter.getStartTime(), parameter.getEndTime());
			if(memberId != null && parameter.getMemberId().equals(memberId)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 单笔投资最大金额
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="xiangMuDBTZ")
	public boolean investmentMaxAmountProject(SPParameter parameter, SPRuleMethod method){
		List<SPProject> projectList = parameter.getProjects();
		for(SPProject spp : projectList){
			BigDecimal investmentMaxAmount = projectRuleManager.investmentMaxAmountProject(spp.getId(), parameter.getMemberId());
			BigDecimal amount = new BigDecimal(method.getValue());
			if(investmentMaxAmount!= null && investmentMaxAmount.compareTo(amount) >= 0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 好友单笔投资最大金额
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="xiangMuHYDBTZ")
	public boolean friendsInvestmentMaxAmountProject(SPParameter parameter, SPRuleMethod method){
		List<SPProject> projectList = parameter.getProjects();
		for(SPProject spp : projectList){
			BigDecimal friendsInvestmentMaxAmount = projectRuleManager.friendsInvestmentMaxAmountProject(spp.getId(), parameter.getMemberId());
			BigDecimal amount = new BigDecimal(method.getValue());
			if(friendsInvestmentMaxAmount !=null && friendsInvestmentMaxAmount.compareTo(amount) >=0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 整点投资
	 * @param parameter
	 * @param method
	 * @return
	 */
	@SPMethod(name="xiangMuZDTZ")
	public boolean integralPointInvestmentProject(SPParameter parameter, SPRuleMethod method){
		List<SPProject> projectList = parameter.getProjects();
		for(SPProject spp : projectList){
			BigDecimal integralPointInvestment = projectRuleManager.integralPointInvestmentProject(spp.getId(), parameter.getMemberId(), method.getValue(), parameter.getStartTime(), parameter.getEndTime());
			if(integralPointInvestment !=null && integralPointInvestment.compareTo(BigDecimal.ZERO) > 0){
				return true;
			}
		}
		return false;
	}
}
