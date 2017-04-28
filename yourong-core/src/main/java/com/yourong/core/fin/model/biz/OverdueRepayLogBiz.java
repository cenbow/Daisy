package com.yourong.core.fin.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;

public class OverdueRepayLogBiz extends AbstractBaseObject{
	
	 private static final long serialVersionUID = -6788851483909460435L;
    /****/
    private Long id;

    /**项目id**/
    private Long projectId;
    
    /**还款日期**/
    private Date repayDate;
    
    /**未还本金**/
    private BigDecimal unreturnPrincipal;
    
    /**未还利息**/
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
    
    /**还款金额**/
    private BigDecimal totalPayAmount;
    
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
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date repayTime;
   
    /**备注**/
    private String remarks;

    /****/
    private Date updateTime;

    /****/
    private Date createTime;

    /**-1:删除，1：正常**/
    private Integer delFlag;
    
    /**1-未还款；2-已还款；3-还款中**/
    private Integer status;
    
    /**未还本金**/
    private BigDecimal returnPrincipal;
    
    /**逾期罚息**/
    private BigDecimal lateFeeRate;
    
    
    /**垫资罚息**/
    private BigDecimal overdueFeeRate;
    
    private List<OverdueRepayLogBiz> overdueList;

    /**
     * 还款类型  1 本金+利息  2 利息
     */
    private Integer interestTypes;
    
    /**逾期开始时间**/
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date overdueStartDate;
    
    /**
     * 垫资标识
     */
    private Integer underwrite;
    /**
     * 逾期标识
     */
    private Integer overdue;
    
    /**所属期数**/
    private String periods;
    
    private Integer overdueStatus;
    
    /**管理费用**/
    private BigDecimal manageFeeRate;
    
    /**保证金费率**/
    private BigDecimal guaranteeFeeRate;
    
    /**风险金费率**/
    private BigDecimal riskFeeRate;
    
    /**介绍费率**/
    private BigDecimal introducerFeeRate;
    
    /**投资总金额**/
    private BigDecimal totalAmount;
    
    private BigDecimal commonPayableAmount;
    
    
	
	public BigDecimal getCommonPayableAmount() {
		return commonPayableAmount;
	}

	public void setCommonPayableAmount(BigDecimal commonPayableAmount) {
		this.commonPayableAmount = commonPayableAmount;
	}

	public String getPeriods() {
		return periods;
	}

	public void setPeriods(String periods) {
		this.periods = periods;
	}

	public BigDecimal getLateFeeRate() {
		return lateFeeRate;
	}

	public void setLateFeeRate(BigDecimal lateFeeRate) {
		this.lateFeeRate = lateFeeRate;
	}

	public List<OverdueRepayLogBiz> getOverdueList() {
		return overdueList;
	}

	public void setOverdueList(List<OverdueRepayLogBiz> overdueList) {
		this.overdueList = overdueList;
	}
	
	public BigDecimal getReturnPrincipal() {
		return returnPrincipal;
	}

	public void setReturnPrincipal(BigDecimal returnPrincipal) {
		this.returnPrincipal = returnPrincipal;
	}

	public Integer getOverdueStatus() {
		return overdueStatus;
	}

	public void setOverdueStatus(Integer overdueStatus) {
		this.overdueStatus = overdueStatus;
	}

	public Integer getStatus() {
		return status;
	}

	public BigDecimal getTotalPayAmount() {
		return totalPayAmount;
	}

	public Integer getUnderwrite() {
		return underwrite;
	}

	public BigDecimal getManageFeeRate() {
		return manageFeeRate;
	}

	public void setManageFeeRate(BigDecimal manageFeeRate) {
		this.manageFeeRate = manageFeeRate;
	}

	public BigDecimal getGuaranteeFeeRate() {
		return guaranteeFeeRate;
	}

	public void setGuaranteeFeeRate(BigDecimal guaranteeFeeRate) {
		this.guaranteeFeeRate = guaranteeFeeRate;
	}

	public BigDecimal getRiskFeeRate() {
		return riskFeeRate;
	}

	public void setRiskFeeRate(BigDecimal riskFeeRate) {
		this.riskFeeRate = riskFeeRate;
	}

	public BigDecimal getIntroducerFeeRate() {
		return introducerFeeRate;
	}

	public void setIntroducerFeeRate(BigDecimal introducerFeeRate) {
		this.introducerFeeRate = introducerFeeRate;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setUnderwrite(Integer underwrite) {
		this.underwrite = underwrite;
	}

	public Integer getOverdue() {
		return overdue;
	}

	public void setOverdue(Integer overdue) {
		this.overdue = overdue;
	}

	public void setTotalPayAmount(BigDecimal totalPayAmount) {
		this.totalPayAmount = totalPayAmount;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BigDecimal getOverdueFeeRate() {
		return overdueFeeRate;
	}

	public void setOverdueFeeRate(BigDecimal overdueFeeRate) {
		this.overdueFeeRate = overdueFeeRate;
	}


	public Integer getInterestTypes() {
		return interestTypes;
	}

	public void setInterestTypes(Integer interestTypes) {
		this.interestTypes = interestTypes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
	
	public Date getOverdueStartDate() {
		return overdueStartDate;
	}

	public void setOverdueStartDate(Date overdueStartDate) {
		this.overdueStartDate = overdueStartDate;
	}

	//还款时间格式化
	public String getRepayDateStr() {
		if(repayDate!=null){
			return DateUtils.getDateStrFromDate(repayDate);
		}
	return "";
		}
	public String getOverdueStartDateStr() {
		if(overdueStartDate!=null){
			return DateUtils.getDateStrFromDate(overdueStartDate);
		}
	return "";
		}
	public String getRepayTimeStr() {
		if(repayTime!=null){
			return DateUtils.getDateStrFromDate(repayTime);
		}
	return "";
		}
	//本金
	public String getOverduePrincipalStr() {
		if(overduePrincipal == null || BigDecimal.ZERO.compareTo(overduePrincipal)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(overduePrincipal);
	}
	//利息
	public String getOverdueInterestStr() {
		if(overdueInterest == null || BigDecimal.ZERO.compareTo(overdueInterest)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(overdueInterest);
	}
	//滞纳金
	public String getOverdueFineStr() {
		if(overdueFine == null || BigDecimal.ZERO.compareTo(overdueFine)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(overdueFine.setScale(2, BigDecimal.ROUND_HALF_UP));
	}
	
	public String getFormatOverdueFineStr() {
		if(overdueFine == null || BigDecimal.ZERO.compareTo(overdueFine)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(overdueFine);
	}
	//违约金
	public String getBreachAmountStr() {
		if(breachAmount == null || BigDecimal.ZERO.compareTo(breachAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(breachAmount);
	}
	//未还本金
	public String getReturnPrincipalStr() {
		if(returnPrincipal == null || BigDecimal.ZERO.compareTo(returnPrincipal)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(returnPrincipal);
	}
	//共需支付
	public String getPayableAmountStr() {
		if(payableAmount == null || BigDecimal.ZERO.compareTo(payableAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(payableAmount);
	}
	//实际支付
	public String getRealpayAmountStr() {
		if(realpayAmount == null || BigDecimal.ZERO.compareTo(realpayAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(realpayAmount);
	}
	//还款金额
	public String getTotalPayAmountStr() {
		if(totalPayAmount == null || BigDecimal.ZERO.compareTo(totalPayAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(totalPayAmount);
	}
	//还款方式
	public String getRepayTypeStr(){
		if(repayType==null){
			return "";
		}
		if(StatusEnum.OVERDUE_REPAYTYPE_ONLINE.getStatus()==repayType){
			return StatusEnum.OVERDUE_REPAYTYPE_ONLINE.getDesc();
		}
		if(StatusEnum.OVERDUE_REPAYTYPE_UNDERLINE.getStatus()==repayType){
			return StatusEnum.OVERDUE_REPAYTYPE_UNDERLINE.getDesc();
		}
		return "";
	}
	//还款状态
	public String getRepayStatusStr(){
		if(repayStatus==null){
			return "";
		}
		if(StatusEnum.OVERDUE_REPAYSTATUS_NOPAY.getStatus()==repayStatus){
			return StatusEnum.OVERDUE_REPAYSTATUS_NOPAY.getDesc();
		}
		if(StatusEnum.OVERDUE_REPAYSTATUS_PAYING.getStatus()==repayStatus){
			return StatusEnum.OVERDUE_REPAYSTATUS_PAYING.getDesc();
		}
		if(StatusEnum.OVERDUE_REPAYSTATUS_HADPAY.getStatus()==repayStatus){
			return StatusEnum.OVERDUE_REPAYSTATUS_HADPAY.getDesc();
		}
		if(StatusEnum.OVERDUE_REPAYSTATUS_PAYFAIL.getStatus()==repayStatus){
			return StatusEnum.OVERDUE_REPAYSTATUS_PAYFAIL.getDesc();
		}
		return "";
	}
	//垫资还款情况
	public String getUnderStatusStr(){
		if(status==null){
			return "";
		}
		if(StatusEnum.INTEREST_OVERDUE_REMARK_UNDERPPAY.getStatus()==status){
			return StatusEnum.INTEREST_OVERDUE_REMARK_UNDERPPAY.getDesc();
		}
		if(StatusEnum.INTEREST_OVERDUE_REMARK_UNDERHADPAY.getStatus()==status){
			return StatusEnum.INTEREST_OVERDUE_REMARK_UNDERHADPAY.getDesc();
		}
		return "";
	}
	public String getInterestTypesStr(){
		if(interestTypes==null){
			return "";
		}
		if(1==interestTypes){
			return "本金+利息";
		}
		if(2==interestTypes){
			return "利息";
		}
		return "";
	}
	public String getUnderwriteStr(){
		if(underwrite==null){
			return "否";
		}
		if(1==underwrite){
			return "是";
		}else{
			return "否";
		}
	}
	public String getOverdueStr(){
		if(overdue==null){
			return "否";
		}
		if(1==overdue){
			return "是";
		}else{
			return "否";
		}
	}
	public String getOverdueStatusStr(){
		if(overdueStatus==null){
			return "";
		}
		if(StatusEnum.INTEREST_RECORD_WAITPAY.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_RECORD_WAITPAY.getDesc();
		}
		if(StatusEnum.INTEREST_RECORD_UNDERWRITE.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_RECORD_UNDERWRITE.getDesc();
		}
		if(StatusEnum.INTEREST_RECORD_HADPAY.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_RECORD_HADPAY.getDesc();
		}
		if(StatusEnum.INTEREST_RECORD_OVERDUE.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_RECORD_OVERDUE.getDesc();
		}
		return "";
	}
	public String getProjectFeeStr(){
		BigDecimal manage = BigDecimal.ZERO;
		BigDecimal guarantee = BigDecimal.ZERO;
		BigDecimal risk = BigDecimal.ZERO;
		BigDecimal introducer = BigDecimal.ZERO;
		if(manageFeeRate==null){
			manage=BigDecimal.ZERO;
		}else{
			manage=FormulaUtil.getManagerAmount(totalAmount,manageFeeRate);
		}
		if(guaranteeFeeRate==null){
			guarantee=BigDecimal.ZERO;
		}else{
			guarantee=FormulaUtil.getManagerAmount(totalAmount,guaranteeFeeRate);
		}
		if(riskFeeRate==null){
			risk=BigDecimal.ZERO;
		}else{
			risk=FormulaUtil.getManagerAmount(totalAmount,riskFeeRate);
		}
		if(introducerFeeRate==null){
			introducer=BigDecimal.ZERO;
		}else{
			introducer=FormulaUtil.getManagerAmount(totalAmount,introducerFeeRate);
		}
		return "￥"+FormulaUtil.getFormatPrice(manage.add(guarantee).add(risk).add(introducer));
	}
	public BigDecimal getUnreturnInterest() {
		return unreturnInterest;
	}


	public void setUnreturnInterest(BigDecimal unreturnInterest) {
		this.unreturnInterest = unreturnInterest;
	}
	
	//共需支付
	public String getCommonPayableAmountStr() {
		if(commonPayableAmount == null){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(commonPayableAmount);
	}
}