package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class WithdrawLog extends AbstractBaseObject  {
    /**
	 * 
	 */
   private static final long serialVersionUID = -6788851483909460435L;

	/****/
    private Long id;

    /**提现金额**/    
    private BigDecimal withdrawAmount;

    /**到账金额**/
    private BigDecimal arrivedAmount;

    /**更新时间**/
    private Date withdrawTime;

    /**用户绑定银行卡id**/
    private Long bankCardId;

    /**会员id**/
    private Long memberId;
    /**会员真实姓名*/
    private String trueName;
    /**手机号**/    
    private Long mobile;
    /**手续费**/
    private BigDecimal fee;

    /**备注**/
    private String notice;

    /**-2-失败 -1-拒绝  0-冻结  1-处理中 2-提现待支付 3-支付中 5-操作成功  **/
    private Integer status;

    /**提现流水号**/
    private String withdrawNo;
    
    private String outerWithdrawNo;
    //来源 0-pc 1-android 2-ios,默认null，是pc
    private  Integer withdrawSource;
    
    /**提现手续费**/
    private Integer withdrawFee;

    /**
     * 提现处理时间
     */
    private Date processTime;

    /**
     * 提现会员IP
     */
    private String userIp;
    
    private String remarks;
    
    

    public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getWithdrawSource() {
        return withdrawSource;
    }

    public void setWithdrawSource(Integer withdrawSource) {
        this.withdrawSource = withdrawSource;
    }

    public String getOuterWithdrawNo() {
		return outerWithdrawNo;
	}

	public void setOuterWithdrawNo(String outerWithdrawNo) {
		this.outerWithdrawNo = outerWithdrawNo;
	}

	/****/
    private Date updateTime;
    
    /**
	 * 操作人姓名
	 */
    private String operateName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public BigDecimal getArrivedAmount() {
        return arrivedAmount;
    }

    public void setArrivedAmount(BigDecimal arrivedAmount) {
        this.arrivedAmount = arrivedAmount;
    }

    public Date getWithdrawTime() {
        return withdrawTime;
    }

    public void setWithdrawTime(Date withdrawTime) {
        this.withdrawTime = withdrawTime;
    }

    public Long getBankCardId() {
        return bankCardId;
    }

    public void setBankCardId(Long bankCardId) {
        this.bankCardId = bankCardId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice == null ? null : notice.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWithdrawNo() {
        return withdrawNo;
    }

    public void setWithdrawNo(String withdrawNo) {
        this.withdrawNo = withdrawNo == null ? null : withdrawNo.trim();
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

	public Date getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}

	public Integer getWithdrawFee() {
		return withdrawFee;
	}

	public void setWithdrawFee(Integer withdrawFee) {
		this.withdrawFee = withdrawFee;
	}

	public String getOperateName() {
		return operateName;
	}

	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	
}