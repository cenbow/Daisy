package com.yourong.core.fin.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;

public class CapitalInOutForMemberCenter extends AbstractBaseObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

    /**资金备注**/
    private String remark;

    /**发生时间**/
    private Date happenTime;

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
	
	public String getFormatBalance() {
		if(balance == null || BigDecimal.ZERO.compareTo(balance)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(balance);
	}

	public Integer getType() {
		return type;
	}

	public String getTypeDesc(){
		if(type==null){
			return "";
		}
		int typeInt = type.intValue();
		if(typeInt==TypeEnum.FINCAPITALINOUT_TYPE_RECHARGE.getType()){
			return "充值";
		}
		if(typeInt==TypeEnum.FINCAPITALINOUT_TYPE_WITHDRAW.getType()){
			return "提现";
		}
		if(typeInt==TypeEnum.FINCAPITALINOUT_TYPE_INVEST.getType()){
			return "投资";
		}
		if(typeInt==TypeEnum.FINCAPITALINOUT_TYPE_INTEREST.getType()){
			return "利息回款";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PRINCIPAL.getType()){
			return "本金回款";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_THIRDPAY.getType()){
			return "存钱罐收益";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_LOAN.getType()){
			return "项目放款";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_RETURN.getType()){
			return "项目还款";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_CAPITAL_FALLBACK.getType()){
			return "资金回退";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_UNDERWRITE_TYPE.getType()){
			return "垫资还款";
		}
		if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PAY_TRANSFER_PAYMENT.getType()){
			return "转让收款";
		}
		/*if(typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PAY_MANAGER_FEE.getType()){
			return "项目管理费";
		}*/
		if(typeInt== TypeEnum.FINCAPITALINOUT_TYPE_DIRECT_PAY.getType()||typeInt== TypeEnum.FINCAPITALINOUT_TYPE_DIRECT_COLLECT.getType()
				||typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PAY_LEASE_BONUS.getType()||typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PAY_MANAGER_FEE.getType()
				||typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PAY_GUARANTEE_FEE.getType()||typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PAY_OVERDUE.getType()
				||typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PAY_INTRODUCE_FEE.getType()||typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PAY_QUICK_REWARD.getType()
				||typeInt == TypeEnum.FINCAPITALINOUT_TYPE_PAY_QUICK_REWARD.getType()
				){
			return "其他";
		}
		return "";
	}
	
	public BigDecimal getIncome() {
		return income;
	}

	public BigDecimal getOutlay() {
		return outlay;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getFormatIncome() {
		if(income == null || BigDecimal.ZERO.compareTo(income)==0){
			return "-";
		}
		return "￥"+FormulaUtil.getFormatPrice(income);
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public String getFormatOutlay() {
		if(outlay == null || BigDecimal.ZERO.compareTo(outlay)==0){
			return "-";
		}
		return "￥"+FormulaUtil.getFormatPrice(outlay);
	}

	public void setOutlay(BigDecimal outlay) {
		this.outlay = outlay;
	}

	public String getRemark() {
		if(remark!=null){
			return remark;
		}
		return "";
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getHappenTime() {
		return happenTime;
	}

	public String getFormatHappenTime() {
		if(happenTime!=null){
			return DateUtils.formatDatetoString(happenTime,DateUtils.DATE_FMT_11);
		}
		return "";
	}

	public void setHappenTime(Date happenTime) {
		this.happenTime = happenTime;
	}

}
