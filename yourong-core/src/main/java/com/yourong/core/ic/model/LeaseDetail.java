package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;

public class LeaseDetail {
	/** 主键 **/
	private Long id;

	/** 租赁分红id` **/
	private Long leaseBonusId;

	/** 租赁开始时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;

	/** 租赁结束时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;

	/** 租赁天数 **/
	private Integer leaseDays;

	/** 租金 **/
	private BigDecimal rental;

	/** 租金总额 **/
	private BigDecimal totalRental;

	/** 用户共分红比例 **/
	private BigDecimal userBonus;

	/** 借款人分红比例 **/
	private BigDecimal borrowerBonus;

	/** 出借人分红比例 **/
	private BigDecimal lenderBonus;

	/** 租赁公司分红比例 **/
	private BigDecimal lessorBonus;

	/** 分红状态(1-分红中；2-已分红) **/
	private Integer bonusStatus;

	/** 分红发放日期 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date bonusDate;

	/** 创建时间 **/
	private Date createTime;

	/** 更新时间 **/
	private Date updateTime;

	/** 备注 **/
	private String remark;

	/** 删除标记 **/
	private Integer delFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLeaseBonusId() {
		return leaseBonusId;
	}

	public void setLeaseBonusId(Long leaseBonusId) {
		this.leaseBonusId = leaseBonusId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getRental() {
		return rental;
	}

	public void setRental(BigDecimal rental) {
		this.rental = rental;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Integer getDayNum() {
		return DateUtils.daysOfTwo(startDate, endDate) + 1;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Integer getLeaseDays() {
		return leaseDays;
	}

	public void setLeaseDays(Integer leaseDays) {
		this.leaseDays = leaseDays;
	}

	public BigDecimal getUserBonus() {
		return userBonus;
	}

	public void setUserBonus(BigDecimal userBonus) {
		this.userBonus = userBonus;
	}

	public BigDecimal getBorrowerBonus() {
		return borrowerBonus;
	}

	public void setBorrowerBonus(BigDecimal borrowerBonus) {
		this.borrowerBonus = borrowerBonus;
	}

	public BigDecimal getLenderBonus() {
		return lenderBonus;
	}

	public void setLenderBonus(BigDecimal lenderBonus) {
		this.lenderBonus = lenderBonus;
	}

	public BigDecimal getLessorBonus() {
		return lessorBonus;
	}

	public void setLessorBonus(BigDecimal lessorBonus) {
		this.lessorBonus = lessorBonus;
	}

	public Integer getBonusStatus() {
		return bonusStatus;
	}

	public void setBonusStatus(Integer bonusStatus) {
		this.bonusStatus = bonusStatus;
	}

	public Date getBonusDate() {
		return bonusDate;
	}

	public void setBonusDate(Date bonusDate) {
		this.bonusDate = bonusDate;
	}

	public void setTotalRental(BigDecimal totalRental) {
		this.totalRental = totalRental;
	}

	public BigDecimal getTotalRental() {
		return totalRental;
	}

	public String getStartDateStr() {
		return DateUtils.formatDatetoString(startDate,  DateUtils.DATE_FMT_3);
	}

	public String getEndDateStr() {
		return DateUtils.formatDatetoString(endDate,  DateUtils.DATE_FMT_3);
	}
	
	public String getBonusDateStr() {
		if(bonusDate!=null){
			return DateUtils.formatDatetoString(bonusDate, DateUtils.DATE_FMT_3);
		}
		return "";
	}
	
	public String getRentalStr() {
		return FormulaUtil.formatCurrencyNoUnit(rental);
	}

	public String getTotalRentalStr() {
		return FormulaUtil.formatCurrencyNoUnit(totalRental);
	}
}