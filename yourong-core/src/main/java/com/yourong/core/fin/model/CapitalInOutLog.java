package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;

public class CapitalInOutLog {
    /****/
    private Integer id;

    /**用户id**/
    private Long memberId;

    /**第三方支付账户类型 1存钱罐账户 2-保证金账户 3-基本户账户 **/
    private Integer payAccountType;

    /**余额**/
    private BigDecimal balance;

    /**1:充值  2:提现 3:充值手续费 4：提现手续费  5:用户投资 6：现金券支出 7：收益券支出 8:利息收支  9：本金收支 **/
    private Integer type;

    /**收入**/
    private BigDecimal income;

    /**支出**/
    private BigDecimal outlay;

    /**来源id(充值流水id，提现流水id，支付id, 交易号，支付号)**/
    private String sourceId;

    /****/
    private String remark;

    /**发生时间**/
    private Date happenTime;
    
    /**会员姓名**/
    private String trueName;
    
    /**手机号**/
    private Long mobile;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getPayAccountType() {
        return payAccountType;
    }

    public void setPayAccountType(Integer payAccountType) {
        this.payAccountType = payAccountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getOutlay() {
        return outlay;
    }

    public void setOutlay(BigDecimal outlay) {
        this.outlay = outlay;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId == null ? null : sourceId.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Date happenTime) {
        this.happenTime = happenTime;
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
	
	public String getHappenTimeStr(){
		if(happenTime!=null){
			return DateUtils.formatDatetoString(happenTime, DateUtils.DATE_FMT_11);
		}
		return "";
	}
}