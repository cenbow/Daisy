package com.yourong.core.ic.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.FormulaUtil;

/**
 * 
 * @Description 借款人授信额度信息
 * @author luwenshan
 * @time 2016年11月23日 下午2:32:13
 */
public class BorrowerCredit implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8619371871926548569L;

	/**id**/
    private Long id;
    
    /**投资类型（1-债权；2-直投）**/
    private Integer investType;

    /**借款人id**/
    private Long borrowerId;
    
    /**借款人类型**/
    private Integer borrowerType;
    
    /**借款人姓名**/
    private String borrowerTrueName;
    
    /**借款人手机号**/
    private Long borrowerMobile;
    
    /**渠道商类型:taizilongstore-太子龙，jimistore-机蜜，毛介浜-maojiebang，星睿行-xingruihang**/
    private String openPlatformKey;
    
    /**存续量**/
    private BigDecimal payablePrincipal;

    /**授信额度**/
    private BigDecimal creditAmount;

    /**状态， 1-正常，0-超出授信额**/
    private Integer status;

    /**上线标识， 1-正常上线，0-暂停上线**/
    private Integer onlineFlag;

    /**备注**/
    private String remark;

    /**创建时间**/
    private Date createTime;
    
    /**更新时间**/
    private Date updateTime;
    
    /**最小存续量**/
    private BigDecimal minPayablePrincipal;
    
    /**最大存续量**/
    private BigDecimal maxPayablePrincipal;
    
    /**最小授信额度**/
    private BigDecimal minCreditAmount;
    
    /**最大授信额度**/
    private BigDecimal maxCreditAmount;
    
    /**默认值**/
    private BigDecimal defaultCreditAmount;
	/**
	 * 信用等级
	 */
	private String creditLevel;
	/**
	 * 信用等级描述
	 */
	private String creditLevelDes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public Long getBorrowerId() {
		return borrowerId;
	}

	public void setBorrowerId(Long borrowerId) {
		this.borrowerId = borrowerId;
	}

	public Integer getBorrowerType() {
		return borrowerType;
	}

	public void setBorrowerType(Integer borrowerType) {
		this.borrowerType = borrowerType;
	}

	public String getBorrowerTrueName() {
		return borrowerTrueName;
	}

	public void setBorrowerTrueName(String borrowerTrueName) {
		this.borrowerTrueName = borrowerTrueName;
	}

	public Long getBorrowerMobile() {
		return borrowerMobile;
	}

	public void setBorrowerMobile(Long borrowerMobile) {
		this.borrowerMobile = borrowerMobile;
	}

	public String getOpenPlatformKey() {
		return openPlatformKey;
	}

	public void setOpenPlatformKey(String openPlatformKey) {
		this.openPlatformKey = openPlatformKey;
	}

	public BigDecimal getPayablePrincipal() {
		return payablePrincipal;
	}

	public void setPayablePrincipal(BigDecimal payablePrincipal) {
		this.payablePrincipal = payablePrincipal;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOnlineFlag() {
		return onlineFlag;
	}

	public void setOnlineFlag(Integer onlineFlag) {
		this.onlineFlag = onlineFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public BigDecimal getMinPayablePrincipal() {
		return minPayablePrincipal;
	}

	public void setMinPayablePrincipal(BigDecimal minPayablePrincipal) {
		this.minPayablePrincipal = minPayablePrincipal;
	}

	public BigDecimal getMaxPayablePrincipal() {
		return maxPayablePrincipal;
	}

	public void setMaxPayablePrincipal(BigDecimal maxPayablePrincipal) {
		this.maxPayablePrincipal = maxPayablePrincipal;
	}

	public BigDecimal getMinCreditAmount() {
		return minCreditAmount;
	}

	public void setMinCreditAmount(BigDecimal minCreditAmount) {
		this.minCreditAmount = minCreditAmount;
	}

	public BigDecimal getMaxCreditAmount() {
		return maxCreditAmount;
	}

	public void setMaxCreditAmount(BigDecimal maxCreditAmount) {
		this.maxCreditAmount = maxCreditAmount;
	}

	public BigDecimal getDefaultCreditAmount() {
		return defaultCreditAmount;
	}

	public void setDefaultCreditAmount(BigDecimal defaultCreditAmount) {
		this.defaultCreditAmount = defaultCreditAmount;
	}

	/**
	 * 
	 * @Description 存续量格式化
	 * @return
	 * @author luwenshan
	 * @time 2016年11月24日 下午6:15:50
	 */
	public String getFormatPayablePrincipal() {
		if(payablePrincipal == null || BigDecimal.ZERO.compareTo(payablePrincipal)==0){
			return "￥0";
		}
		return "￥" + FormulaUtil.getFormatPrice(payablePrincipal);
	}
	
	/**
	 * 
	 * @Description 授信额格式化
	 * @return
	 * @author luwenshan
	 * @time 2016年11月24日 下午6:15:50
	 */
	public String getFormatCreditAmount() {
		if(creditAmount == null || BigDecimal.ZERO.compareTo(creditAmount)==0){
			return "￥0";
		}
		return "￥" + FormulaUtil.getFormatPrice(creditAmount);
	}

	public String getCreditLevel() {
		return creditLevel;
	}

	public void setCreditLevel(String creditLevel) {
		this.creditLevel = creditLevel;
	}

	public String getCreditLevelDes() {
		return creditLevelDes;
	}

	public void setCreditLevelDes(String creditLevelDes) {
		this.creditLevelDes = creditLevelDes;
	}
}