package com.yourong.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.api.utils.ServletUtil;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.core.ic.model.LeaseBonusDetail;

public class TransactionForProjectDto implements Serializable {
	
	/**项目编号**/
	private Long projectId;
	
	/**用户ID**/
    private Long memberId;
    
    /**用户昵称**/
    private String username;
    
    /**投资金额**/
    private BigDecimal investAmount;

    /**年化收益**/
    private BigDecimal annualizedRate;
    
    /**额外年化收益**/
    private BigDecimal extraAnnualizedRate;

    /**交易时间**/
    private Date transactionTime;
    
    /**总利息收益**/
    private BigDecimal totalInterest;
    
    /**租赁分红收益**/
    private BigDecimal leaseBonusAmounts;
    
    /**租赁分红收益率**/
    private BigDecimal bonusAnnualizedRate;
    
    /**头像**/
    private String avatars;
    
    /**
     * 是否租赁分红
     */
    private boolean isLeaseBonus = false;
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
    
	/**转让本金 **/
	private BigDecimal transferPrincipal;
	
    /**
     * 租赁收益明细列表
     */
    @JSONField(serialize=false)
    public List<LeaseBonusDetail> leaseBonusDetails;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getUsername() {
		return ServletUtil.getMemberUserName(memberId);
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

	public String getAvatars() {
		return ServletUtil.getMemberAvatarById(memberId);
	}

	public boolean isLeaseBonus() {
		return isLeaseBonus;
	}

	public void setLeaseBonus(boolean isLeaseBonus) {
		this.isLeaseBonus = isLeaseBonus;
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

	public List<LeaseBonusDetailDto> getLeaseBonusDetailList(){
		if(Collections3.isNotEmpty(leaseBonusDetails)){
			return BeanCopyUtil.mapList(leaseBonusDetails, LeaseBonusDetailDto.class);
		}
		return null;
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
	
}
