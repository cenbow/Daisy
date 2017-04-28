package com.yourong.api.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * Created by py on 2015/3/25.
 */
public class RechargeDto extends AbstractBaseObject {
    /**
     * 银卡卡ID
     */
    @NotNull(message="10008")
    private Long cardId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * rechargeType 1:直接充值,2:交易充值
     */
    @NotNull(message="10008")
    private  Integer  rechargeType;
    /**
     * 充值金额
     */
    @NotBlank(message="10008")
    private  String amount;
    /**
     * 新浪ticket
     */
    private String ticket;
    /**
     * 单号
     */
    private String outAdvanceNo;
    /**
     * 短信验证码
     */
    private String validCode;
    
    /**
     * 来源 0-pc 1-android 2-ios,默认null，是pc
     */
    private Integer rechargeSource;

	/** 活动标识 */
	private boolean isActivity = false;
	
	
	 private String resultUrl;
	 
	 private String resultValue;
	 
	 private String result;
	 
	 
	 private String errorDesc;
	 
	 private String tradeNo;
	 
	 private String withdrawNo;
	 
	 
	 
	 
	 

    public String getWithdrawNo() {
		return withdrawNo;
	}

	public void setWithdrawNo(String withdrawNo) {
		this.withdrawNo = withdrawNo;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	

	public String getResultUrl() {
		return resultUrl;
	}

	public void setResultUrl(String resultUrl) {
		this.resultUrl = resultUrl;
	}

	public String getResultValue() {
		return resultValue;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}

	public Integer getRechargeSource() {
        return rechargeSource;
    }

    public void setRechargeSource(Integer rechargeSource) {
        this.rechargeSource = rechargeSource;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getOutAdvanceNo() {
        return outAdvanceNo;
    }

    public void setOutAdvanceNo(String outAdvanceNo) {
        this.outAdvanceNo = outAdvanceNo;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(Integer rechargeType) {
        this.rechargeType = rechargeType;
    }

	public boolean isActivity() {
		return isActivity;
	}

	public void setActivity(boolean isActivity) {
		this.isActivity = isActivity;
	}
}
