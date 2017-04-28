package com.yourong.core.tc.model.biz;

import com.yourong.common.util.FormulaUtil;
import com.yourong.core.tc.model.TransactionInterest;

public class OverTransactionInterestForCalendar extends TransactionInterest{
	
	
	/*项目名称*/
	private String projectName;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * 获取格式化应付利息
	 * @return
	 */
	public String getFormatInterest() {
		if(super.getPayableInterest()!=null){
			return FormulaUtil.getFormatPrice(super.getPayableInterest());
		}
		return null;
	}
	
	/**
	 * 获取格式化应付本金
	 * @return
	 */
	public String getFormatPrincipal() {
		if(super.getPayablePrincipal()!=null){
			return FormulaUtil.getFormatPrice(super.getPayablePrincipal());
		}
		return null;
	}
	
}
