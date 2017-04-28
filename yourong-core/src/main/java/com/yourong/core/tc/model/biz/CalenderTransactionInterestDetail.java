package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;

import com.yourong.common.util.FormulaUtil;
import com.yourong.core.tc.model.TransactionInterest;

public class CalenderTransactionInterestDetail extends TransactionInterest {

	/*项目名称*/
	private String projectName;
	
	/** 总计支付利息 **/
	private BigDecimal totalInterest;
	/** 总计支付本金 **/
	private BigDecimal totalPrincipal;
	
    /**逾期 天数**/
    private Integer overDays;
    

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public BigDecimal getTotalPrincipal() {
		return totalPrincipal;
	}

	public void setTotalPrincipal(BigDecimal totalPrincipal) {
		this.totalPrincipal = totalPrincipal;
	}
	
	/**
	 * 获取格式化利息
	 * @return
	 */
	public String getFormatTotalInterest() {
		if(totalInterest!=null){
			return FormulaUtil.getFormatPrice(totalInterest);
		}
		return null;
	}
	
	/**
	 * 获取格式化本金
	 * @return
	 */
	public String getFormatTotalPrincipal() {
		if(totalPrincipal!=null){
			return FormulaUtil.getFormatPrice(totalPrincipal);
		}
		return null;
	}

	public Integer getOverDays() {
		return overDays;
	}

	public void setOverDays(Integer overDays) {
		this.overDays = overDays;
	}
	
	public String getPrefixProjectName() {
		if (projectName.contains("期")) {
			return projectName.substring(0, projectName.indexOf("期") + 1);
		} else {
			return projectName;
		}
	}
	
	
}
