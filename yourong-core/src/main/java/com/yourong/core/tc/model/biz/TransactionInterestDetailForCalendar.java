package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;

import org.apache.ibatis.type.Alias;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

@Alias(value="TransactionInterestDetailForCalendar")
public class TransactionInterestDetailForCalendar {

	/** 项目编号 **/
	private Long projectId;
	/** 项目名称 **/
	@JSONField(serialize=false)
	private String projectName;
	/** 总计支付利息 **/
	private BigDecimal totalPayableInterest;
	/** 总计支付本金 **/
	private BigDecimal totalPayablePrincipal;
	/** 当前笔数 **/
	private Integer currentNum;
	/** 总笔数 **/
	private Integer totalNum;
	/**交易编号**/
	private Long transactionId;
	/** 还款状态 **/
	private Integer repaymentStatus;
	
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public BigDecimal getTotalPayableInterest() {
		return totalPayableInterest;
	}
	public void setTotalPayableInterest(BigDecimal totalPayableInterest) {
		this.totalPayableInterest = totalPayableInterest;
	}
	public BigDecimal getTotalPayablePrincipal() {
		return totalPayablePrincipal;
	}
	public void setTotalPayablePrincipal(BigDecimal totalPayablePrincipal) {
		this.totalPayablePrincipal = totalPayablePrincipal;
	}
	public Integer getCurrentNum() {
		return currentNum;
	}
	public void setCurrentNum(Integer currentNum) {
		this.currentNum = currentNum;
	}
	public Integer getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	
	public String getFormatTotalPayablePrincipal(){
		return FormulaUtil.getFormatPrice(getTotalPayablePrincipal());
	}
	
	public String getFormatTotalPayableInterest(){
		return FormulaUtil.getFormatPrice(getTotalPayableInterest());
	}
	
	public Integer getRepaymentStatus() {
		return repaymentStatus;
	}

	public void setRepaymentStatus(Integer repaymentStatus) {
		this.repaymentStatus = repaymentStatus;
	}

	public String getPrefixProjectName() {
		String name = getProjectName();
		if(StringUtil.isNotBlank(name)){
			if (name.contains("期")) {
				return name.substring(0, name.indexOf("期") + 1);
			} else {
				return name;
			}
		}
		return "";
	}

}
