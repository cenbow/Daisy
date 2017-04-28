package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.model.CollectionProcess;

public class OverdueRepayLog extends AbstractBaseObject{
	
	 private static final long serialVersionUID = -6788851483909460435L;
    /****/
    private Long id;

    /**项目id**/
    private Long projectId;
    
    /**还款日期**/
    private Date repayDate;
    
    /**逾期开始时间**/
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date overdueStartDate;
    
    /**未还本金**/
    private BigDecimal unreturnPrincipal;
    
    /**未还利息（借款人）**/
    private BigDecimal unreturnInterest;
    
    /**逾期天数 = 当天日期-项目应还日期 +1**/
    private Integer overdueDay;
    
    /**逾期本金**/
    private BigDecimal overduePrincipal;

    /**逾期利息**/
    private BigDecimal overdueInterest;
    
    /**滞纳金**/
    private BigDecimal overdueFine;

    /**违约金**/
    private BigDecimal breachAmount;


    /**应付金额= 逾期金额+逾期利息+滞纳金+（违约金，只有线下有违约金）**/
    private BigDecimal payableAmount;
    
    /**实际付金额**/
    private BigDecimal realpayAmount;
    
    /**逾期记录ID**/
    private String overdueId;
    
    /***还款方式，1线上，2线下**/
    private Integer repayType;
   
    /**逾期还款状态[还款状态（1-未还款；2-还款中；3-已还款；4-还款失败）]**/
    private Integer repayStatus;
   
    /*(1-垫资还款；)***/
    private Integer type;
 
    /**还款时间**/
    private Date repayTime;
   
    /**备注**/
    private String remarks;

    /****/
    private Date updateTime;

    /****/
    private Date createTime;

    /**-1:删除，1：正常**/
    private Integer delFlag;
    
    /**操作人名称**/
    private String oparateName;

    /**催收历程**/
    private List<CollectionProcess> collectList;
    
    /**本息期数**/
    private String interestPeriods;
    
    private BigDecimal unreturnPrincipalInterest;
    
    /**逾期罚息**/
    private BigDecimal lateFeeRate;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getUnreturnPrincipalInterest() {
		return unreturnPrincipalInterest;
	}

	public void setUnreturnPrincipalInterest(BigDecimal unreturnPrincipalInterest) {
		this.unreturnPrincipalInterest = unreturnPrincipalInterest;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Date getRepayDate() {
		return repayDate;
	}

	public void setRepayDate(Date repayDate) {
		this.repayDate = repayDate;
	}

	public BigDecimal getUnreturnPrincipal() {
		return unreturnPrincipal;
	}

	public void setUnreturnPrincipal(BigDecimal unreturnPrincipal) {
		this.unreturnPrincipal = unreturnPrincipal;
	}

	public Integer getOverdueDay() {
		return overdueDay;
	}

	public String getInterestPeriods() {
		return interestPeriods;
	}

	public void setInterestPeriods(String interestPeriods) {
		this.interestPeriods = interestPeriods;
	}

	public void setOverdueDay(Integer overdueDay) {
		this.overdueDay = overdueDay;
	}

	public BigDecimal getOverduePrincipal() {
		return overduePrincipal;
	}

	public void setOverduePrincipal(BigDecimal overduePrincipal) {
		this.overduePrincipal = overduePrincipal;
	}

	public BigDecimal getOverdueInterest() {
		return overdueInterest;
	}

	public void setOverdueInterest(BigDecimal overdueInterest) {
		this.overdueInterest = overdueInterest;
	}

	public BigDecimal getOverdueFine() {
		return overdueFine;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

	public BigDecimal getBreachAmount() {
		return breachAmount;
	}

	public void setBreachAmount(BigDecimal breachAmount) {
		this.breachAmount = breachAmount;
	}

	public BigDecimal getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(BigDecimal payableAmount) {
		this.payableAmount = payableAmount;
	}


	public BigDecimal getRealpayAmount() {
		return realpayAmount;
	}

	public void setRealpayAmount(BigDecimal realpayAmount) {
		this.realpayAmount = realpayAmount;
	}

	public String getOverdueId() {
		return overdueId;
	}

	public void setOverdueId(String overdueId) {
		this.overdueId = overdueId;
	}

	public Integer getRepayType() {
		return repayType;
	}

	public void setRepayType(Integer repayType) {
		this.repayType = repayType;
	}

	public Integer getRepayStatus() {
		return repayStatus;
	}

	public void setRepayStatus(Integer repayStatus) {
		this.repayStatus = repayStatus;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getRepayTime() {
		return repayTime;
	}

	public void setRepayTime(Date repayTime) {
		this.repayTime = repayTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public BigDecimal getLateFeeRate() {
		return lateFeeRate;
	}

	public void setLateFeeRate(BigDecimal lateFeeRate) {
		this.lateFeeRate = lateFeeRate;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getOparateName() {
		return oparateName;
	}

	public void setOparateName(String oparateName) {
		this.oparateName = oparateName;
	}

	public List<CollectionProcess> getCollectList() {
		return collectList;
	}

	public void setCollectList(List<CollectionProcess> collectList) {
		this.collectList = collectList;
	}
	
	public Date getOverdueStartDate() {
		return overdueStartDate;
	}

	public void setOverdueStartDate(Date overdueStartDate) {
		this.overdueStartDate = overdueStartDate;
	}

	public String getCollectStatusName() {
		String collectName="";
		if(repayStatus!=null){
			if(repayStatus==1||repayStatus==2||repayStatus==4){
				collectName= "催收中";
			}else if(repayStatus==3){
				collectName= "已还款";
			}
		}
		return collectName;
	}

	public BigDecimal getUnreturnInterest() {
		return unreturnInterest;
	}

	public void setUnreturnInterest(BigDecimal unreturnInterest) {
		this.unreturnInterest = unreturnInterest;
	}	
	
	/**
	 * 逾期开始时间格式化
	 * @return
	 */
	public String getOverdueStartDateStr() {
		if(overdueStartDate!=null){
			return DateUtils.formatDatetoString(overdueStartDate, DateUtils.DATE_FMT_3);
		}
		return "";
	}
	
	/**
	 * 逾期开始时间格式化
	 * @return
	 */
	public String getRepayDateStr() {
		if(repayTime!=null){
			return DateUtils.formatDatetoString(repayTime, DateUtils.DATE_FMT_3);
		}
		return "";
	}

}