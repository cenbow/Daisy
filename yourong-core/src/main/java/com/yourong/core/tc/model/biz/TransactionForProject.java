package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.model.LeaseBonusDetail;

public class TransactionForProject extends AbstractBaseObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2784523806362490740L;

	/** 交易号 **/
	private Long id;

	/** 订单号 **/
	private Long orderId;

	/** 订单来源 **/
	private int orderSource;

	/** 用户ID **/
	private Long memberId;

	private String username;

	private Long mobile;

	/** 项目ID **/
	private Long projectId;

	/** 投资金额 **/
	private BigDecimal investAmount;

	/** 年化收益 **/
	private BigDecimal annualizedRate;

	/** 额外年化收益 **/
	private BigDecimal extraAnnualizedRate;
	
	/** 项目加息年化收益 **/
	private BigDecimal extraProjectAnnualizedRate;
	
	private BigDecimal totalExtraProjectInterest;

	/** 交易时间 **/
	private Date transactionTime;

	/** 总利息收益 **/
	private BigDecimal totalInterest;

	/** 租赁分红收益 **/
	private BigDecimal leaseBonusAmounts;

	/** 租赁分红收益率 **/
	private BigDecimal bonusAnnualizedRate;

	/**
	 * 收益天数
	 */
	private int totalDays;
	/**
	 * 是否首笔投资
	 */
	private boolean isFirstInvest = false;
	/**
	 * 是否最后一笔投资
	 */
	private boolean isLastInvest = false;
	/**
	 * 是否最高投资
	 */
	private boolean isMostInvest = false;
	/**
	 * 是否幸运女神
	 */
	private boolean isLuckInvest = false;
	/**
	 * 是否一掷千金
	 */
	private boolean isMostAndLastInvest = false;
	/**
	 * 租赁收益明细列表
	 */
	private List<LeaseBonusDetail> leaseBonusDetails;

	/**
	 * 是否租赁分红
	 */
	private boolean isLeaseBonus = false;
	
	/**
	 * 
	 */
	private String maskUserName;

	/**
	 * 头像
	 */
	private String  avatars;

	/**
	 * 项目名称
	 */
	private String projectName;
	
	/**
	 * 使用现金券金额
	 */
	private BigDecimal usedCouponAmount;

	/** 插入时间 **/
	private Date insertDate;
	
	/**转让本金 **/
	private BigDecimal transferPrincipal;
	
	
	private BigDecimal totalExtraInterest;
	
	
	private BigDecimal allInterest;
	
	
	private Integer extraInterestDay;
	
	
	

	public BigDecimal getTotalExtraProjectInterest() {
		return totalExtraProjectInterest;
	}

	public void setTotalExtraProjectInterest(BigDecimal totalExtraProjectInterest) {
		this.totalExtraProjectInterest = totalExtraProjectInterest;
	}

	public BigDecimal getTotalExtraInterest() {
		return totalExtraInterest;
	}

	public void setTotalExtraInterest(BigDecimal totalExtraInterest) {
		this.totalExtraInterest = totalExtraInterest;
	}

	


	public BigDecimal getAllInterest() {
		return allInterest;
	}

	public void setAllInterest(BigDecimal allInterest) {
		this.allInterest = allInterest;
	}

	public Integer getExtraInterestDay() {
		return extraInterestDay;
	}

	public void setExtraInterestDay(Integer extraInterestDay) {
		this.extraInterestDay = extraInterestDay;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getUsername() {
		if (StringUtil.isNotBlank(username) && username.length() > 1) {
			// 字符替换，这里没有用正则，这种性能更具有优势!
			if (username.length() == 2)
				return username.substring(0, 1) + "***";
			else
				return username.substring(0, 1) + "***" + username.substring(username.length() - 1, username.length());
		}
		return username;
	}
	public String getAvatars() {
		return avatars;
	}
	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public BigDecimal getExtraAnnualizedRate() {
		return extraAnnualizedRate;
	}

	public void setExtraAnnualizedRate(BigDecimal extraAnnualizedRate) {
		this.extraAnnualizedRate = extraAnnualizedRate;
	}

	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public BigDecimal getLeaseBonusAmounts() {
		return leaseBonusAmounts;
	}

	public void setLeaseBonusAmounts(BigDecimal leaseBonusAmounts) {
		this.leaseBonusAmounts = leaseBonusAmounts;
	}

	public BigDecimal getBonusAnnualizedRate() {
		return bonusAnnualizedRate;
	}

	public void setBonusAnnualizedRate(BigDecimal bonusAnnualizedRate) {
		this.bonusAnnualizedRate = bonusAnnualizedRate;
	}

	public BigDecimal getTotalRate() {
		if (extraAnnualizedRate == null || extraAnnualizedRate.compareTo(BigDecimal.ZERO) <= 0) {
			return annualizedRate;
		}
		return annualizedRate.add(extraAnnualizedRate);
	}

	public void setMaskUserName(String maskUserName){
		this.maskUserName = maskUserName;
	}
	public String getMaskUserName() {
		if(StringUtil.isNotBlank(maskUserName)){
			return maskUserName;
		}else{
			if (StringUtil.isNotBlank(username)) {
				return StringUtil.maskString(username, StringUtil.ASTERISK, 1, 1, 3);
			}
			if(mobile!=null)
				return StringUtil.maskString(mobile.toString(), StringUtil.ASTERISK, 3, 2, 4);
			}
			return "";
	}

	public boolean isFirstInvest() {
		return isFirstInvest;
	}

	public void setFirstInvest(boolean isFirstInvest) {
		this.isFirstInvest = isFirstInvest;
	}

	public boolean isLastInvest() {
		return isLastInvest;
	}

	public void setLastInvest(boolean isLastInvest) {
		this.isLastInvest = isLastInvest;
	}

	public boolean isMostInvest() {
		return isMostInvest;
	}

	public void setMostInvest(boolean isMostInvest) {
		this.isMostInvest = isMostInvest;
	}

	public boolean isLuckInvest() {
		return isLuckInvest;
	}

	public void setLuckInvest(boolean isLuckInvest) {
		this.isLuckInvest = isLuckInvest;
	}

	public List<LeaseBonusDetail> getLeaseBonusDetails() {
		return leaseBonusDetails;
	}

	public void setLeaseBonusDetails(List<LeaseBonusDetail> leaseBonusDetails) {
		this.leaseBonusDetails = leaseBonusDetails;
	}

	public boolean isLeaseBonus() {
		if (this.leaseBonusAmounts != null && this.leaseBonusAmounts.doubleValue() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public String getLeaseBonusDetailJson() {
		if (Collections3.isNotEmpty(leaseBonusDetails)) {
			return JSON.toJSONString(leaseBonusDetails);
		}
		return "";
	}

	public String getTotalBonusRateStr() {
		if (leaseBonusAmounts != null && leaseBonusAmounts.doubleValue() > 0) {
			return FormulaUtil.getFormatPrice(leaseBonusAmounts);
		}
		return "";
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public int getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(int orderSource) {
		this.orderSource = orderSource;
	}

	public BigDecimal getUsedCouponAmount() {
		return usedCouponAmount;
	}

	public void setUsedCouponAmount(BigDecimal usedCouponAmount) {
		this.usedCouponAmount = usedCouponAmount;
	}
	
	/**
	 * 现金券投资资金格式化
	 * @return
	 */
	public String getFormatUsedCouponAmount(){
		if(usedCouponAmount !=null && usedCouponAmount.compareTo(BigDecimal.ZERO)>0){
			return FormulaUtil.getFormatPrice(usedCouponAmount);
		}
		return "0";
	}
	/**
	 * 用户投资资金
	 * @return
	 */
	public String getFormatUserInvestAmount(){
		if(usedCouponAmount!=null && investAmount!=null){
			BigDecimal userInvest = investAmount.subtract(usedCouponAmount);
			if(investAmount.subtract(usedCouponAmount).compareTo(BigDecimal.ZERO)>0){
				return FormulaUtil.getFormatPrice(userInvest);
			}
		}
		return "0";
	}

	public boolean isMostAndLastInvest() {
		return isMostAndLastInvest;
	}

	public void setMostAndLastInvest(boolean isMostAndLastInvest) {
		this.isMostAndLastInvest = isMostAndLastInvest;
	}

	/**
	 * @return the transferPrincipal
	 */
	public BigDecimal getTransferPrincipal() {
		return transferPrincipal;
	}

	/**
	 * @param transferPrincipal the transferPrincipal to set
	 */
	public void setTransferPrincipal(BigDecimal transferPrincipal) {
		this.transferPrincipal = transferPrincipal;
	}

	public BigDecimal getExtraProjectAnnualizedRate() {
		return extraProjectAnnualizedRate;
	}

	public void setExtraProjectAnnualizedRate(BigDecimal extraProjectAnnualizedRate) {
		this.extraProjectAnnualizedRate = extraProjectAnnualizedRate;
	}
	
}