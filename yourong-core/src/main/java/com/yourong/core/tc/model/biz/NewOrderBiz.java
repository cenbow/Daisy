package com.yourong.core.tc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.enums.DebtEnum;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.mc.model.Coupon;

/**
 * 创建订单biz
 * @author Leon Ray
 * 2014年10月8日-下午6:28:14
 */
public class NewOrderBiz implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 690063366925237829L;

	/**项目id**/
    private Long projectId;

    /**项目名称**/
    private String projectName;
    
    /**年化收益率**/
    private BigDecimal annualizedRate;
    /**投资额**/
    private BigDecimal investAmount;

    /**预期收益**/
    private BigDecimal expectAmount;
    /**购买时间**/
    private String orderTime;
    
    /**
     * 投资人姓名
     */
    private String trueName;
    
    /**
     * 投资人身份证号码
     */
    private String identityNumber;
    
    /**
     * 投资人手机号码
     */
    private String mobile;
    
    /**
     * 存钱罐账户
     */
    private String sinaPayAccount;
    
    /**用户id**/
    private Long memberId;
    

    /**使用收益券**/
    private String profitCouponNo;
    
    
    /**使用收益券增加的年化收益**/
    private BigDecimal extraAnnualizedRate;


    /**收益天数**/
    private int profitDays;
    


    /**项目到期日**/
    private Date endDate;

    /**
     * 回款计划
     */
    private List<TransactionInterestForOrderAndMember> transactionInterestForOrders;
    
    /**
     * 优惠券列表
     */
    private List<Coupon> coupons;
    
    /**
     * 收益类型
     */
    private String profitType;

	private boolean isDirectProject;

	public boolean isDirectProject() {
		return isDirectProject;
	}

	public void setDirectProject(boolean isDirectProject) {
		this.isDirectProject = isDirectProject;
	}

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



	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}



	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}



	public BigDecimal getInvestAmount() {
		return investAmount;
	}



	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}



	public BigDecimal getExpectAmount() {
		return expectAmount;
	}



	public void setExpectAmount(BigDecimal expectAmount) {
		this.expectAmount = expectAmount;
	}



	public String getOrderTime() {
		return orderTime;
	}



	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}



	public String getTrueName() {
		return trueName;
	}



	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}



	public String getIdentityNumber() {
		return identityNumber;
	}



	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}



	public String getMobile() {
		return mobile;
	}



	public void setMobile(String mobile) {
		this.mobile = mobile;
	}



	public String getSinaPayAccount() {
		return sinaPayAccount;
	}



	public void setSinaPayAccount(String sinaPayAccount) {
		this.sinaPayAccount = sinaPayAccount;
	}



	public Long getMemberId() {
		return memberId;
	}



	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}



	public String getProfitCouponNo() {
		return profitCouponNo;
	}



	public void setProfitCouponNo(String profitCouponNo) {
		this.profitCouponNo = profitCouponNo;
	}



	public BigDecimal getExtraAnnualizedRate() {
		return extraAnnualizedRate;
	}



	public void setExtraAnnualizedRate(BigDecimal extraAnnualizedRate) {
		this.extraAnnualizedRate = extraAnnualizedRate;
	}



	public int getProfitDays() {
		return profitDays;
	}



	public void setProfitDays(int profitDays) {
		this.profitDays = profitDays;
	}



	public Date getEndDate() {
		return endDate;
	}



	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}



	public List<TransactionInterestForOrderAndMember> getTransactionInterestForOrders() {
		return transactionInterestForOrders;
	}



	public void setTransactionInterestForOrders(
			List<TransactionInterestForOrderAndMember> transactionInterestForOrders) {
		this.transactionInterestForOrders = transactionInterestForOrders;
	}

	public BigDecimal getTotalAnnualizedRate() {
		if(this.extraAnnualizedRate==null) {
			return this.annualizedRate;
		} else {
			return this.annualizedRate.add(this.extraAnnualizedRate).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
	}



	public List<Coupon> getCoupons() {
		return coupons;
	}



	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}



	public String getProfitType() {
		return profitType;
	}



	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	
	/**
	 * 收益类型的名称
	 *
	 * @return
	 */
	public String getProfitTypeName() {
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_DAY.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_ONCE.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_ONCE.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_AVG_PRINCIPAL.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_INTEREST.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_AVG_INTEREST.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_DAY_SEASON.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getDesc();
		}
		return null;
	}
	//格式化投资本金
	public String getInvestAmountStr() {
		return FormulaUtil.formatCurrency(investAmount);
	}
	//格式化预期收益
	public String getExpectAmountStr() {
		return FormulaUtil.formatCurrency(expectAmount);
	}
}