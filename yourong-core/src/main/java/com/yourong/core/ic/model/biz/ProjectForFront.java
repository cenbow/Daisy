package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;

public class ProjectForFront extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9193339820515134532L;

	/** 项目id **/
	private Long id;

	/** 目前只有债权ID **/
	private Long debtId;

	/** 项目名称 **/
	private String name;

	/** 投资总金额 **/
	private BigDecimal totalAmount;

	/** 起投金额 **/
	private BigDecimal minInvestAmount;

	/** 递增单位金额 **/
	private BigDecimal incrementAmount;

	/** 收益计算方式(1-阶梯收益， 2-利率随递增额递增) **/
	private Integer annualizedRateType;

	/** 最小收益 **/
	private BigDecimal minAnnualizedRate;

	/** 最大收益 **/
	private BigDecimal maxAnnualizedRate;

	/** 递增收益 **/
	private BigDecimal incrementAnnualizedRate;
    
    /**项目余额**/
    private BigDecimal availableBalance;
    
    /**项目已投金额**/
    private BigDecimal investedBalance;

	/** 最大收益 **/
	private BigDecimal maxInvestAmount;

	/** 起息日，T+1则存1 **/
	private Integer interestFrom;

	/** 状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款) **/
	private Integer status;

	/** 项目开始日期 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date startDate;

	/** 还款时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date endDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	/**上线时间(年月日时分秒)**/
	private Date onlineTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	/**销售截止日期(年月日时分秒)**/
	private Date saleEndTime;

	/** 债权号 **/
	private String serialNumber;

	/** 但保方式 **/
	private String debtType;

	/****/
	private String projectType;

	/** 项目描述，保障措施 **/
	private String description;

	/** 项目缩略图 **/
	private String thumbnail;

	/** 就否新手项目 **/
	private Integer isNovice;

	/** 是否参与租赁分红 **/
	private Integer joinLease;
	
	/**收益类型**/
    private String profitType;
    
    /**借款人ID**/
    private Long borrowerId;
    
    /**项目进度**/
    private String process;
    
    /**进度条**/
    private Integer round;
    
    /**特殊活动标识*/
    private Integer activitySign;
    /**投资类型（1-债权；2-直投）*/
    private Integer investType;

    /**销售完成时间*/
	private Date saleComplatedTime;
	
	private BigDecimal annualizedRate;
	
	private boolean isDirectProject;

	private Integer borrowPeriod;

	private Integer borrowPeriodType;
	
	 /**投资人数*/
	private Integer investNum;
	
	 /**最高投资额*/
	private BigDecimal mostInvestAmount;
	
	 /**累计总收益*/
	private BigDecimal totalIncome;
	
	/** 折价 **/
	private BigDecimal discount;
    
	/** 项目类型 **/
	private Integer projectCategory = TypeEnum.PROJECT_CATEGORY_NORMAL.getType();
	
	/** 转让标记 **/
	private Integer transferFlag;
	
	private Integer quickRewardFlag;
	
	/**特殊业务标识*/
    private Integer extraType;
    
    /**加息数值*/
    private String addRate;
    
    private BigDecimal addRateBigDecimal;
    
	private Integer transferAfterInterest;

	/** 快投总金额 **/
	private BigDecimal extraAmount;
    /**是否属于资产包***/
	private Integer packageFlag;
	
	public Integer getPackageFlag() {
		return packageFlag;
	}

	public void setPackageFlag(Integer packageFlag) {
		this.packageFlag = packageFlag;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDebtId() {
		return debtId;
	}

	public void setDebtId(Long debtId) {
		this.debtId = debtId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAnnualizedRateType() {
		return annualizedRateType;
	}

	public void setAnnualizedRateType(Integer annualizedRateType) {
		this.annualizedRateType = annualizedRateType;
	}

	public BigDecimal getMinAnnualizedRate() {
		return minAnnualizedRate;
	}

	public void setMinAnnualizedRate(BigDecimal minAnnualizedRate) {
		this.minAnnualizedRate = minAnnualizedRate;
	}

	public BigDecimal getMaxAnnualizedRate() {
		return maxAnnualizedRate;
	}

	public void setMaxAnnualizedRate(BigDecimal maxAnnualizedRate) {
		this.maxAnnualizedRate = maxAnnualizedRate;
	}

	public BigDecimal getIncrementAnnualizedRate() {
		return incrementAnnualizedRate;
	}

	public void setIncrementAnnualizedRate(BigDecimal incrementAnnualizedRate) {
		this.incrementAnnualizedRate = incrementAnnualizedRate;
	}

	public Integer getInterestFrom() {
		return interestFrom;
	}

	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getDebtType() {
		return debtType;
	}

	public void setDebtType(String debtType) {
		this.debtType = debtType;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Date getSaleEndTime() {
		return saleEndTime;
	}

	public void setSaleEndTime(Date saleEndTime) {
		this.saleEndTime = saleEndTime;
	}

	public BigDecimal getMinInvestAmount() {
		return minInvestAmount;
	}

	public void setMinInvestAmount(BigDecimal minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
	}

	public BigDecimal getIncrementAmount() {
		return incrementAmount;
	}

	public void setIncrementAmount(BigDecimal incrementAmount) {
		this.incrementAmount = incrementAmount;
	}

	public BigDecimal getMaxInvestAmount() {
		return maxInvestAmount;
	}

	public void setMaxInvestAmount(BigDecimal maxInvestAmount) {
		this.maxInvestAmount = maxInvestAmount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Integer getIsNovice() {
		return isNovice;
	}

	public void setIsNovice(Integer isNovice) {
		this.isNovice = isNovice;
	}



	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}


	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public Long getBorrowerId() {
		return borrowerId;
	}

	public void setBorrowerId(Long borrowerId) {
		this.borrowerId = borrowerId;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
		this.round = round;
	}

	/**
	 * 收益总天数
	 * 
	 * @return
	 */
	public Integer getEarningsTotalDays() {
		int day = DateUtils.getIntervalDays(getStartDate(), getEndDate()) + 1 - getInterestFrom();
		return day > 0 ? day : 0;
	}

	/**
	 * 剩余收益天数
	 * 
	 * @return
	 */
	public Integer getEarningsDays() {
		int day = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3),
				getEndDate()) + 1 - getInterestFrom();
		return day > 0 ? day : 0;
	}

	public String getFormatTotalAmount() {
		if (totalAmount == null) {
			return "";
		}
		return new DecimalFormat("###,###.##").format(totalAmount);
	}

	public String getFormatMinAnnualizedRate() {
		if (minAnnualizedRate == null) {
			return "";
		}
		return new DecimalFormat("###.00").format(minAnnualizedRate);
	}

	public String getFormatMaxAnnualizedRate() {
		if (maxAnnualizedRate == null) {
			return "";
		}
		return new DecimalFormat("###.00").format(maxAnnualizedRate);
	}

	public String getPrefixProjectName() {
		if (name.contains("期")) {
			return name.substring(0, name.indexOf("期") + 1);
		} else {
			return "";
		}
	}

	public String getSuffixProjectName() {
		if (name.contains("期")) {
			return name.substring(name.indexOf("期") + 1);
		} else {
			return name;
		}
	}

	public String getFormatTotalAmount2() {
		if (totalAmount == null) {
			return "";
		}
		return new DecimalFormat("###,###").format(totalAmount);
	}

	/**
	 * 是否是预告
	 * 
	 * @return
	 */
	public boolean isNotice() {
		if (status >= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
			return false;
		}
		return true;
	}

	/**
	 * 是否激活,用于判断首页不在项目状态的样式
	 * 
	 * @return
	 */
	public boolean isActive() {
		// 投资中或非暂停状态
		if (status < StatusEnum.PROJECT_STATUS_INVESTING.getStatus()
				|| status > StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
			return false;
		}
		return true;
	}

	/**
	 * 是否暂停
	 * 
	 * @return
	 */
	public boolean isStop() {
		if (status == StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
			return true;
		}
		return false;
	}

	/**
	 * 根据状态获得按钮文本
	 * 
	 * @return
	 */
	public String getButtonText() {
		
		if (getProjectCategory() != null && getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
			if (getStatus() == StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()) {
				return "立即认购";
			} else if(getStatus() == StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus()){
				return "履约中";
			}else if (getStatus() == StatusEnum.TRANSFER_PROJECT_STATUS_REPAYMENT.getStatus()) {
				return "已还款";
			} 
		}
		if (isDirectProject()) {
			if (getStatus() == StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
				return "立即投资";
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_FULL.getStatus()
					|| (getStatus() == StatusEnum.PROJECT_STATUS_LOSING.getStatus() && saleComplatedTime != null )
					||getStatus() == StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus()) {
				return "已投满";
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
				return StatusEnum.PROJECT_STATUS_STOP.getDesc();
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()
					||getStatus() == StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()
					) {
				return "履约中";
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_END.getStatus() 
					||(getStatus() == StatusEnum.PROJECT_STATUS_LOSING.getStatus() && saleComplatedTime == null )) {
				return StatusEnum.PROJECT_STATUS_END.getDesc();
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()) {
				return StatusEnum.PROJECT_STATUS_REPAYMENT.getDesc();
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_LOSE.getStatus()) {
				return StatusEnum.PROJECT_STATUS_LOSE.getDesc();
			}
		}else {
			if (status == StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
				return "立即投资";
			} else if (status == StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
				return StatusEnum.PROJECT_STATUS_STOP.getDesc();
			} else if (status == StatusEnum.PROJECT_STATUS_FULL.getStatus()
					|| status == StatusEnum.PROJECT_STATUS_END.getStatus()) {
				return "履约中";
			} else if (status == StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()) {
				return "已还款";
			}
		}
		return "";
	}

	/**
	 * 根据状态显示剩余收益天数或收益总天数
	 * 
	 * @return
	 */
	public Integer getEarningsDaysByStatus() {
		if (status == StatusEnum.PROJECT_STATUS_INVESTING.getStatus()
				|| status == StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
			return getEarningsDays();
		}
		return getEarningsTotalDays();
	}
	
	/**
	 * 预告是否就绪
	 * 
	 * @return
	 */
	public boolean noticeIsReady() {
		int sencond = getNoticeEndTime();
		float _sencond = (float) sencond / 3600;
		if (_sencond > 48) {
			return false;
		}
		return true;
	}

	/**
	 * 预告的结束时间（秒）
	 * 
	 * @return
	 * 
	 */
	public int getNoticeEndTime() {
		return DateUtils.getTimeIntervalSencond(DateUtils.getCurrentDate(), getOnlineTime());
	}

	/**
	 * 获得预告项目的收益总天数
	 * 
	 * @return
	 */
	public Integer getNoticeProjectEarningsTotalDays() {
		int day = 0;
		if(getOnlineTime() != null && getEndDate() != null) {
			day = DateUtils.getIntervalDays(DateUtils.formatDate(getOnlineTime(), DateUtils.DATE_FMT_3), getEndDate())
					+ 1 - getInterestFrom();			
		}
		return day > 0 ? day : 0;
	}

	/**
	 * 是否新手项目
	 * 
	 * @return
	 */
	public boolean isNoviceProject() {
		if(projectCategory==2){
			return false;
		}
		
		if (isNovice == 0) {
			return true;
		}
		return false;
	}
	
	public String getFormatAvailableBalance() {
		if (availableBalance == null) {
			return "";
		}
		return FormulaUtil.getFormatPrice(availableBalance);
	}

	public boolean isJoinLease() {
		if (joinLease == null) {
			return false;
		}
		if (joinLease == 1) {
			return true;
		}
		return false;
	}

	public void setJoinLease(Integer joinLease) {
		this.joinLease = joinLease;
	}

	public Integer getActivitySign() {
		return activitySign;
	}

	public void setActivitySign(Integer activitySign) {
		this.activitySign = activitySign;
	}
	
	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public Date getSaleComplatedTime() {
		return saleComplatedTime;
	}

	public void setSaleComplatedTime(Date saleComplatedTime) {
		this.saleComplatedTime = saleComplatedTime;
	}

	public boolean isDirectProject() {
		if(ProjectEnum.PROJECT_TYPE_DIRECT.getType()==getInvestType()){
			return true;
		}
		return false;
	}

	public void setDirectProject(boolean isDirectProject) {
		this.isDirectProject = isDirectProject;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}
	
	/**
	 * 获取直投项目的收益周期
	 */
	public String getFormatProfitPeriodType(){
		String profitPeriod = "";
		if(borrowPeriodType!=null){
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()==borrowPeriodType){
				profitPeriod = "天";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()==borrowPeriodType){
				profitPeriod = "个月";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()==borrowPeriodType){
				profitPeriod = "年";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType()==borrowPeriodType){
				profitPeriod = "周";
			}
		}
		return profitPeriod;
	}

	public Integer getBorrowPeriod() {
		return borrowPeriod;
	}

	public void setBorrowPeriod(Integer borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}

	public Integer getBorrowPeriodType() {
		return borrowPeriodType;
	}

	public void setBorrowPeriodType(Integer borrowPeriodType) {
		this.borrowPeriodType = borrowPeriodType;
	}

	/**
	 * @return the investNum
	 */
	public Integer getInvestNum() {
		return investNum;
	}

	/**
	 * @param investNum the investNum to set
	 */
	public void setInvestNum(Integer investNum) {
		this.investNum = investNum;
	}

	/**
	 * @return the mostInvestAmount
	 */
	public BigDecimal getMostInvestAmount() {
		return mostInvestAmount;
	}

	/**
	 * @param mostInvestAmount the mostInvestAmount to set
	 */
	public void setMostInvestAmount(BigDecimal mostInvestAmount) {
		this.mostInvestAmount = mostInvestAmount;
	}

	/**
	 * @return the totalIncome
	 */
	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	/**
	 * @param totalIncome the totalIncome to set
	 */
	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}

	/**
	 * @return the discount
	 */
	public BigDecimal getDiscount() {
		return discount;
	}

	/**
	 * @param discount the discount to set
	 */
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	/**
	 * @return the projectCategory
	 */
	public Integer getProjectCategory() {
		return projectCategory;
	}

	/**
	 * @param projectCategory the projectCategory to set
	 */
	public void setProjectCategory(Integer projectCategory) {
		this.projectCategory = projectCategory;
	}

	public Integer getTransferFlag() {
		return transferFlag;
	}

	public void setTransferFlag(Integer transferFlag) {
		this.transferFlag = transferFlag;
	}
	
	public boolean isQuickLotteryProject() {
		if (activitySign == null) {
			return false;
		}
		if (activitySign == 2) {
			return true;
		}
		return false;
	}

	public Integer getQuickRewardFlag() {
		return quickRewardFlag;
	}

	public void setQuickRewardFlag(Integer quickRewardFlag) {
		this.quickRewardFlag = quickRewardFlag;
	}

	public Integer getExtraType() {
		return extraType;
	}

	public void setExtraType(Integer extraType) {
		this.extraType = extraType;
	}

	public String getAddRate() {
		return addRate;
	}

	public void setAddRate(String addRate) {
		this.addRate = addRate;
	}
	
	public BigDecimal getAddRateBigDecimal(){
		return addRateBigDecimal;
	}

	public void setAddRateBigDecimal(BigDecimal addRateBigDecimal) {
		this.addRateBigDecimal = addRateBigDecimal;
	}

	public Integer getTransferAfterInterest() {
		return transferAfterInterest;
	}

	public void setTransferAfterInterest(Integer transferAfterInterest) {
		this.transferAfterInterest = transferAfterInterest;
	}

	public BigDecimal getExtraAmount() {
		return extraAmount;
	}

	public void setExtraAmount(BigDecimal extraAmount) {
		this.extraAmount = extraAmount;
	}

	public BigDecimal getInvestedBalance() {
		return investedBalance;
	}

	public void setInvestedBalance(BigDecimal investedBalance) {
		this.investedBalance = investedBalance;
	}
	
	
}
