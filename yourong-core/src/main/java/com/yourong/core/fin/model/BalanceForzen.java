package com.yourong.core.fin.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.FormulaUtil;

public class BalanceForzen implements Serializable{
	
	private static final long serialVersionUID = 2827208227226428074L;
    /****/
    private Long id;

    /**冻结订单号**/
    private String forzenNo;


    /**用户id**/
    private Long memberId;

    /**1-冻结 2-解冻**/
    private Integer type;

    /**冻结金额**/
    private BigDecimal amount;

    /**余额**/
    private BigDecimal balance;

    /**摘要**/
    private String summary;
    
    private String forzenStatus;
    
    private String forzenReason;

    /**备注**/
    private String remarks;

    /**冻结时间**/
    private Date forzenTime;

    private Date createTime;
    
    private Date updateTime;
    
    private String trueName;
    
    private Long mobile;
    
    private Integer unforzenRecord;

    private Integer processRecord;
    /**
     * 请求者ip
     */
    private String clientIp;
    
    
    
	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public Integer getProcessRecord() {
		return processRecord;
	}

	public void setProcessRecord(Integer processRecord) {
		this.processRecord = processRecord;
	}

	public Integer getUnforzenRecord() {
		return unforzenRecord;
	}

	public void setUnforzenRecord(Integer unforzenRecord) {
		this.unforzenRecord = unforzenRecord;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getForzenNo() {
		return forzenNo;
	}

	public void setForzenNo(String forzenNo) {
		this.forzenNo = forzenNo;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getForzenStatus() {
		return forzenStatus;
	}

	public void setForzenStatus(String forzenStatus) {
		this.forzenStatus = forzenStatus;
	}

	public String getForzenReason() {
		return forzenReason;
	}

	public void setForzenReason(String forzenReason) {
		this.forzenReason = forzenReason;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getForzenTime() {
		return forzenTime;
	}

	public void setForzenTime(Date forzenTime) {
		this.forzenTime = forzenTime;
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

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}
	
	public String getForzenAmountStr() {
		if(amount == null ){
			return "0";
		}
		return FormulaUtil.getFormatPrice(amount);
	}
	
	public String getForzenBalanceStr() {
		if(balance == null ){
			return "0";
		}
		return FormulaUtil.getFormatPrice(balance);
	}
   
}