package com.yourong.core.fin.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.ic.model.Project;
import com.yourong.core.uc.model.Member;

/**
 * 托管放款管理
 * 
 * @author fuyili 2014年12月8日下午3:08:47
 */
public class LoanBiz {
//	/* 项目编号 */
//	private Long projectId;
//	/* 项目名称 */
//	private String projectName;
//	/* 上线时间 */
//	private Date onlineTime;
//	/* 销售截止时间 */
//	private Date saleEndTime;
	/*项目信息(项目编号、项目名称、上线时间、销售截止时间 、 项目总额)*/
	private Project project;
	/* 项目进度 */
	private BigDecimal progress;
	/* 最后一笔投资时间 */
	private Date lastTransDate;
	/* 出借人信息 （出借人手机、出借人姓名 ） */
	private Member lenderMember;
	/* 用户-可结算额度 */
	private BigDecimal loanableUserCapital;
	/* 平台-可结算额度 */
	private BigDecimal loanablePlatCapital;
	/* 用户-已结算额度 */
	private BigDecimal loanedUserCapital;
	/* 平台-已结算额度 */
	private BigDecimal loanedPlatCapital;
	/* 结算状态-0：待放款 1：全部放款 2：部分放款 */
	private Integer status;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public BigDecimal getProgress() {
		return progress;
	}

	public void setProgress(BigDecimal progress) {
		this.progress = progress;
	}

	public Date getLastTransDate() {
		return lastTransDate;
	}

	public void setLastTransDate(Date lastTransDate) {
		this.lastTransDate = lastTransDate;
	}

	public Member getLenderMember() {
		return lenderMember;
	}

	public void setLenderMember(Member lenderMember) {
		this.lenderMember = lenderMember;
	}

	public BigDecimal getLoanableUserCapital() {
		return loanableUserCapital==null?BigDecimal.ZERO:loanableUserCapital;
	}

	public void setLoanableUserCapital(BigDecimal loanableUserCapital) {
		this.loanableUserCapital = loanableUserCapital;
	}

	public BigDecimal getLoanablePlatCapital() {
		return loanablePlatCapital==null?BigDecimal.ZERO:loanablePlatCapital;
	}

	public void setLoanablePlatCapital(BigDecimal loanablePlatCapital) {
		this.loanablePlatCapital = loanablePlatCapital;
	}

	public BigDecimal getLoanedUserCapital() {
		return loanedUserCapital==null?BigDecimal.ZERO:loanedUserCapital;
	}

	public void setLoanedUserCapital(BigDecimal loanedUserCapital) {
		this.loanedUserCapital = loanedUserCapital;
	}

	public BigDecimal getLoanedPlatCapital() {
		return loanedPlatCapital==null?BigDecimal.ZERO:loanedPlatCapital;
	}

	public void setLoanedPlatCapital(BigDecimal loanedPlatCapital) {
		this.loanedPlatCapital = loanedPlatCapital;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	/*获取可结算额度字符串-用户*/
	public String getLoanableUserCapitalStr() {
		return FormulaUtil.formatCurrencyNoUnit(loanableUserCapital);
	}
	/*获取可结算额度字符串-平台*/
	public String getLoanablePlatCapitalStr() {
		return FormulaUtil.formatCurrencyNoUnit(loanablePlatCapital);
	}
	/*获取已结算额度-用户*/
	public String getLoanedUserCapitalStr() {
		return FormulaUtil.formatCurrencyNoUnit(loanedUserCapital);
	}
	/*获取已结算额度-平台*/
	public String getLoanedPlatCapitalStr() {
		return FormulaUtil.formatCurrencyNoUnit(loanedPlatCapital);
	}
	/*获取可结算额度*/
	public String getLoanableCapitalStr(){
		BigDecimal loanable =  loanableUserCapital.add(loanablePlatCapital);
		return FormulaUtil.formatCurrencyNoUnit(loanable);
	}
	/*已结算额度*/
	public String getLoanedCapitalStr() {
		BigDecimal loaned = loanedUserCapital.add(loanedPlatCapital);
		return FormulaUtil.formatCurrencyNoUnit(loaned);
	}
	public BigDecimal getProgressStr() {
		return progress.multiply(BigDecimal.valueOf(100));
	}

}
