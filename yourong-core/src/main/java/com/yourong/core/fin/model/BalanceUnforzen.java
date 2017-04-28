package com.yourong.core.fin.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BalanceUnforzen implements Serializable{
	
	private static final long serialVersionUID = 2827208227226428074L;
    /****/
    private Long id;

    /**解冻订单号**/
    private String unforzenNo;

    /**冻结订单号**/
    private String forzenNo;

    /**用户id**/
    private Long memberId;


    /**解冻金额**/
    private BigDecimal amount;


    /**摘要**/
    private String summary;
    
    /**解冻状态**/
    private String unforzenStatus;
    
    /**解冻原因**/
    private String unforzenReason;

    /**备注**/
    private String remarks;

    /**解冻时间**/
    private Date unforzenTime;

    private Date createTime;
    
    private Date updateTime;
    
    /**
     * 请求者ip
     */
    private String clientIp;

    public Long getId() {
        return id;
    }

    public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public void setId(Long id) {
        this.id = id;
    }

	public String getUnforzenNo() {
		return unforzenNo;
	}

	public void setUnforzenNo(String unforzenNo) {
		this.unforzenNo = unforzenNo;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getUnforzenStatus() {
		return unforzenStatus;
	}

	public void setUnforzenStatus(String unforzenStatus) {
		this.unforzenStatus = unforzenStatus;
	}

	public String getUnforzenReason() {
		return unforzenReason;
	}

	public void setUnforzenReason(String unforzenReason) {
		this.unforzenReason = unforzenReason;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getUnforzenTime() {
		return unforzenTime;
	}

	public void setUnforzenTime(Date unforzenTime) {
		this.unforzenTime = unforzenTime;
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

   
}