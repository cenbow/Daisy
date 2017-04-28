package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.model.DebtInterest;

public class DebtInterestForAppDto extends DebtInterest{

	/**利息本金标志（0-利息 1-本金 2-利息+本金 ）**/
	private Integer interPrinMark;
	
	/**显示金额**/
	private BigDecimal  amount;
	
    /**还款类型标记（0-正常 1-提前还款 2-逾期还款 ）**/
    private Integer payType;
	
	/**逾期天数**/
    private Integer overDays;

	public Integer getInterPrinMark() {
		/*if(1==super.getStatus()){
			if(super.getRealPayInterest()!=null&&super.getRealPayPrincipal()!=null
					&&BigDecimal.ZERO.compareTo(super.getRealPayInterest())!=0
					&&BigDecimal.ZERO.compareTo(super.getRealPayPrincipal())!=0){
				return 2;
			}
			if(super.getRealPayPrincipal()!=null//本金不为0
					&&BigDecimal.ZERO.compareTo(super.getRealPayPrincipal())!=0
					){
				return 1;
			}
			if(super.getRealPayInterest()!=null //利息不为0
					&&BigDecimal.ZERO.compareTo(super.getRealPayInterest())!=0
					){
				return 0;
			}
		}else{*/
			if(super.getPayableInterest()!=null&&super.getPayablePrincipal()!=null
					&&BigDecimal.ZERO.compareTo(super.getPayableInterest())!=0
					&&BigDecimal.ZERO.compareTo(super.getPayablePrincipal())!=0){
				return 2;
			}
			if(super.getPayablePrincipal()!=null//本金不为0
					&&BigDecimal.ZERO.compareTo(super.getPayablePrincipal())!=0
					){
				return 1;
			}
			if(super.getPayableInterest()!=null //利息不为0
					&&BigDecimal.ZERO.compareTo(super.getPayableInterest())!=0
					){
				return 0;
			}
		//}
		return 2;
	}

	public void setInterPrinMark(Integer interPrinMark) {
		this.interPrinMark = interPrinMark;
	}

	public BigDecimal getAmount() {
		return amount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getOverDays() {
		return overDays;
	}

	public void setOverDays(Integer overDays) {
		this.overDays = overDays;
	}
	
	/**
	 * 支付时间格式化
	 * @return
	 */
	public String getPayTimeStr() {
		if(super.getPayTime()!=null){
			return DateUtils.formatDatetoString(super.getPayTime(), DateUtils.DATE_FMT_3);
		}
		return "";
	}
	
	/**
	 * 结束时间格式化
	 * @return
	 */
	public String getEndDateStr() {
		if(super.getEndDate()!=null){
			return DateUtils.formatDatetoString(super.getEndDate(), DateUtils.DATE_FMT_3);
		}
		return "";
	}
	
}
