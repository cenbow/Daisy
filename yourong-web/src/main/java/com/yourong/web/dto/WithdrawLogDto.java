package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class WithdrawLogDto {	

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
    
    private String bankCardNo;

    /**会员id**/
    private Long memberId;
    /**会员真实姓名*/
    private String trueName;

    /**手续费**/
    private BigDecimal fee;

    /**备注**/
    private String notice;

    /**-2-失败 -1-拒绝  0-冻结  1-处理中 2-提现待支付 3-支付中 5-操作成功  **/
    private Integer status;
    
    /**提现流水号**/
    private String withdrawNo;
    
    private String outerWithdrawNo;
    //银行编码
    private String bankCode;    

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

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

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getNotice() {
		if(StringUtil.isBlank(notice)){
			return "";
		}		
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
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
		this.withdrawNo = withdrawNo;
	}

	public String getOuterWithdrawNo() {
		return outerWithdrawNo;
	}

	public void setOuterWithdrawNo(String outerWithdrawNo) {
		this.outerWithdrawNo = outerWithdrawNo;
	}

	public String getWithdrawAmountToS() {
		return FormulaUtil.formatCurrency(withdrawAmount);
	}

	public String getWithdrawTimeToS() {
		
		if (withdrawTime == null) {
			return "";
		}
		String data = DateUtils.formatDatetoString(withdrawTime,
				DateUtils.DATE_FMT_3);
		String time = DateUtils.formatDatetoString(withdrawTime,
				DateUtils.DATE_FMT_8);
		return "<span>"+data + "</span><span>" + time+"</span>";	
	}

	public String getBankCardNo() {
		return StringUtil.maskBankCodeNumber(bankCardNo);
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}
	/**
	 * /**-2-失败 -1-拒绝  0-冻结  1-处理中 2-提现待支付 3-支付中 5-操作成功  **/	
	public String getStatusToS() {
		String value  = "";
		if(status == null){
			return value;
		}			
		if(status == -2){
			value = "失败";
		}
		if(status == -1){
			value = "拒绝";
		}
		if(status == 0){
			value = "冻结";
		}
		if(status == 1){
			value = "处理中";
		}
		if(status == 2){
			value = "提现待支付";
		}
		if(status == 3){
			value = "支付中";
		}
		if(status == 5){
			value = "操作成功";
		}			
		if(status == 6){
			value = "已取消";
		}	
		return value;
	}

}
