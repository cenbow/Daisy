package com.yourong.api.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

public class ProjectListDto {

	/**项目id**/
	@JSONField(name="pid")
    private Long id;
    @JSONField(serialize = false)
    /**目前只有债权ID**/
    private Long debtId;

    /**项目名称**/
    private String name;
    @JSONField(serialize = false)
	/**收益计算方式(1-阶梯收益， 2-利率随递增额递增)**/
    private Integer annualizedRateType;

    /**最小收益**/
    private BigDecimal minAnnualizedRate;

    /**最大收益**/
    private BigDecimal maxAnnualizedRate;
    
    @JSONField(serialize = false)
    /**起息日，T+1则存1**/
    private Integer interestFrom;
    /**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer status;
    @JSONField(serialize = false)
    /**项目开始日期(年月日)**/
    private Date startDate;
    @JSONField(serialize = false)
    /**还款时间(年月日)**/
    private Date endDate;
    /**上线时间**/
    private Date onlineTime;
    @JSONField(serialize = false)
    /**销售截止日期**/
    private Date saleEndTime;
    
    /**收益天数**/
    private Integer earningsDays;
    
    /**余额**/
    private BigDecimal availableBalance;
    
    /**投资总金额**/
    private BigDecimal totalAmount;
    
    /**项目缩略图**/
    private String thumbnail;
    
    /**投资进度**/
    private String investmentProgress;
    
    /** 就否新手项目 **/
    @JSONField(serialize=false)
	private Integer isNovice;

	/** 是否参与租赁分红 **/
    @JSONField(serialize=false)
	private Integer joinLease;
    
    /**特殊活动标识**/
    private Integer activitySign;
    
    /**投资类型（1-债权；2-直投）*/
    private Integer investType;
    
    private boolean isDirectProject;
    
    /**状态码**/
    private Integer statusCode;
    
    private Integer borrowPeriod;

	private Integer borrowPeriodType;
	
	/**销售完成时间*/
	private Date saleComplatedTime;
	
	/**投资笔数*/
	private Integer investNum;
		
	/**最高投资额*/
	private BigDecimal mostInvestAmount;
		
	/**累计总收益*/
	private BigDecimal totalIncome;
	
	/**支付中的订单数**/
    private Integer orders;
    
    /**项目支付中的订单金额**/
    private BigDecimal orderAmount;
    
    /**折价**/
    private BigDecimal discount;
    
    /** 转让标记 **/
	private Integer transferFlag;
	
	
	private Integer quickRewardFlag;
    
	/**特殊业务标识*/
    private Integer extraType;
    
    /**加息数值*/
    private String addRate;

	/** 快投抽奖结束时间 */
	private Date quickLotteryEndTime;
	
	/** 快投抽奖最高奖金 */
	private BigDecimal quickMaxAmount;

	/** 快投抽奖最高奖金得主 */
	private String quickMaxAmountOwner;

	private Integer transferAfterInterest;

	/** 快投总金额 **/
	private BigDecimal extraAmount;
	
	/** 快投补偿人气值 **/
	private Integer popularity;

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

	public Integer getInterestFrom() {
		return interestFrom;
	}

	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}

	public Integer getStatus() {
		if(status==81){
			return 50;
		}
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

	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	public void setEarningsDays(Integer earningsDays) {
		this.earningsDays = earningsDays;
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getThumbnail() {
		if(thumbnail != null){
			if(getId() != null && getId() >= 989800156L){
				return Config.ossPicUrl+StringUtil.getFilePath(thumbnail,"300");
			}else{
				return Config.ossPicUrl+thumbnail;
			}
		}
		return null;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public Integer getEarningsDays() {
		return getEarningsDaysByStatus();
	}
	
	public Integer getIsNovice() {
		return isNovice;
	}

	public void setIsNovice(Integer isNovice) {
		this.isNovice = isNovice;
	}

	public Integer getJoinLease() {
		return joinLease;
	}

	public void setJoinLease(Integer joinLease) {
		this.joinLease = joinLease;
	}

	/**
	 * 收益总天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getEarningsTotalDays() {
		int day = DateUtils.getIntervalDays(getStartDate(), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	/**
	 * 获得预告项目的收益总天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getNoticeProjectEarningsTotalDays() {
		int day = DateUtils.getIntervalDays(DateUtils.formatDate(getOnlineTime(), DateUtils.DATE_FMT_3), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	/**
	 * 剩余收益天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getResidualIncomeDays() {
		int day = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	/**
	 * 根据状态显示剩余收益天数或收益总天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getEarningsDaysByStatus(){
		if(isNotice()){//预告
			return getNoticeProjectEarningsTotalDays();
		}
		if(getStatus() == StatusEnum.PROJECT_STATUS_INVESTING.getStatus() || getStatus() == StatusEnum.PROJECT_STATUS_STOP.getStatus()){
			return getResidualIncomeDays();
		}
		return getEarningsTotalDays();
	}
	
	/**
	 * 是否是预告
	 * @return
	 */
	@JSONField(serialize = false)
	public boolean isNotice(){
		if(getStatus() >= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
			return false;
		}
		return true;
	}
	
	public String getInvestmentProgress(){
		if(availableBalance != null){
			if(availableBalance.compareTo(BigDecimal.ZERO) <= 0){
				investmentProgress = "100";
			}else if(availableBalance.compareTo(totalAmount) == 0){
				investmentProgress = "0";
			}else{
				investmentProgress = new DecimalFormat("###.##").format((totalAmount.subtract(availableBalance)).divide(totalAmount,4,RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
			}
		}
		return investmentProgress;
	}
	
	/**
     * 是否新手项目
     * @return
     */
    public boolean getIsNoviceProject(){
    	if(isNovice == null){
    		return false;
    	}
    	if(isNovice == 0){
    		return true;
    	}
    	return false;
    }
    
    /**
     * 是否参与租赁分红
     * @return
     */
    public boolean getIsJoinLease(){
    	if(joinLease == null){
    		return false;
    	}
    	if(joinLease == 1){
    		return true;
    	}
    	return false;
    }

	public Integer getActivitySign() {
		if(activitySign == null) {
			return 0;
		}
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
    
	public boolean getIsDirectProject() {
		if(ProjectEnum.PROJECT_TYPE_DIRECT.getType()==getInvestType()){
			return true;
		}
		return false;
	}

	public void setDirectProject(boolean isDirectProject) {
		this.isDirectProject = isDirectProject;
	}

	public Integer getStatusCode() {
		if(status != null ){
			return formatStatus(status);
		}
		return null;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	 * 状态转换 无：0 暂停中:1 已投满:2 履约中:3 已截止:4 已还款:5 已流标:6 投资中：7 待发布：8
	 * 
	 * @param statusCode
	 * @return
	 */
	private Integer formatStatus(Integer status){
		Integer s = 0;
		switch(status){
		case 20:
			s = 8;
			break;
			case 30:
				s = 7;
				break;
			case 40:
				s = 1;
				break;
			case 50:
				if( investType == ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
					s=2;
					break;
				}
				s = 3;
				break;
			case 51:
				s = 3;//直投项目，待放款，即履约中
				break;
			case 52:
				s = 3;//直投项目，已放款，即履约中
				break;
			case 60:
				if( investType == ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
					s=4;
					break;
				}
				s = 3;
				break;
			case 70:
				s = 5;
				break;
			case 80:
				if(saleComplatedTime!=null){//销售截止时间不为空，已投满
					s = 2;
				}else{//销售截止时间为空，已截止
					s = 4;
				}
				break;
			case 81:
					s = 2;//审核通过81 显示已投满
				break;
			case 90:
				s = 6;
				break;
			default:
				s = 0;
				break;
		}
		return s;
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
	
	public String getProfitPeriod() {
		if(this.borrowPeriod!=null && this.borrowPeriodType!=null){
//			return this.borrowPeriod.toString()+(this.borrowPeriodType==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()?"天":
//				(this.borrowPeriodType==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()?"个月":"年"));
			String borrowPeriodResult = this.borrowPeriod.toString();
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()==borrowPeriodType){
				return borrowPeriodResult + "天";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()==borrowPeriodType){
				return borrowPeriodResult + "个月";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()==borrowPeriodType){
				return borrowPeriodResult + "年";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType()==borrowPeriodType){
				return borrowPeriodResult + "周";
			}
		}
		return "";
	}

	public Date getSaleComplatedTime() {
		return saleComplatedTime;
	}

	public void setSaleComplatedTime(Date saleComplatedTime) {
		this.saleComplatedTime = saleComplatedTime;
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

	public Integer getOrders() {
		return orders;
	}

	public void setOrders(Integer orders) {
		this.orders = orders;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
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

	public Integer getTransferFlag() {
		return transferFlag;
	}

	public void setTransferFlag(Integer transferFlag) {
		this.transferFlag = transferFlag;
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

	public Date getQuickLotteryEndTime() {
		return quickLotteryEndTime;
	}

	public void setQuickLotteryEndTime(Date quickLotteryEndTime) {
		this.quickLotteryEndTime = quickLotteryEndTime;
	}

	public BigDecimal getQuickMaxAmount() {
		return quickMaxAmount;
	}

	public void setQuickMaxAmount(BigDecimal quickMaxAmount) {
		this.quickMaxAmount = quickMaxAmount;
	}

	public String getQuickMaxAmountOwner() {
		return quickMaxAmountOwner;
	}

	public void setQuickMaxAmountOwner(String quickMaxAmountOwner) {
		this.quickMaxAmountOwner = quickMaxAmountOwner;
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

	public Integer getPopularity() {
		return popularity;
	}

	public void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}
	
}
