package com.yourong.api.dto;

import java.util.Date;
import java.util.List;

import com.yourong.core.fin.model.OverdueRepayLog;

public class RepaymentPlanDto {

	/** 还款计划 **/
	private List<DebtInterestForAppDto>  interestList;
	
	/** 提前还款标识 */
	private Integer preFlag;
	
	/** 逾期标识 */
	private Integer overFlag;
	
	 /**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer status;
			
	/** 提前还款原因备注 */
	private String prepaymentRemark;
	
	/**逾期结算记录(催收成果)**/
	private List<OverdueRepayLog> overdueRepayBiz;
	
	/**逾期结算记录(催收中)**/
	private OverdueRepayLog overdue;
	
	/**提前还款时间**/
	private String preDate;

	public Integer getPreFlag() {
		return preFlag;
	}

	public void setPreFlag(Integer preFlag) {
		this.preFlag = preFlag;
	}

	public Integer getOverFlag() {
		return overFlag;
	}

	public void setOverFlag(Integer overFlag) {
		this.overFlag = overFlag;
	}

	public String getPrepaymentRemark() {
		return prepaymentRemark;
	}

	public void setPrepaymentRemark(String prepaymentRemark) {
		this.prepaymentRemark = prepaymentRemark;
	}

	public List<DebtInterestForAppDto> getInterestList() {
		return interestList;
	}

	public void setInterestList(List<DebtInterestForAppDto> interestList) {
		this.interestList = interestList;
	}

	public List<OverdueRepayLog> getOverdueRepayBiz() {
		return overdueRepayBiz;
	}

	public void setOverdueRepayBiz(List<OverdueRepayLog> overdueRepayBiz) {
		this.overdueRepayBiz = overdueRepayBiz;
	}

	public OverdueRepayLog getOverdue() {
		return overdue;
	}

	public void setOverdue(OverdueRepayLog overdue) {
		this.overdue = overdue;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPreDate() {
		return preDate;
	}

	public void setPreDate(String preDate) {
		this.preDate = preDate;
	}
	
}
