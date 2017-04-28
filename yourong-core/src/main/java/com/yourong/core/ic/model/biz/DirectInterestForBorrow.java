package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;

public class DirectInterestForBorrow extends AbstractBaseObject{

	/** */
	private static final long serialVersionUID = 1L;
	
	/**还款时间**/
	private Date refundTime;
	/**还款金额**/
	private BigDecimal payableAmount;
	/**还款状态**/
	private Integer status;
	/**逾期状态**/
	private Integer overdueStatus;
	
	 /**
     * 还款类型  1 本金+利息  2 利息
     */
    private Integer payTypes;
    /**
     * 垫资标识
     */
    private Integer underwrite;
    /**
     * 逾期标识
     */
    private Integer overdue;
	
	//还款时间格式化
	public String getRefundTimeStr() {
		if(refundTime!=null){
			return DateUtils.getDateStrFromDate(refundTime);
		}
		return "";
	}
	/**
	 * 
	 * @Description:还款金额格式化
	 * @return
	 * @author: chaisen
	 * @time:2016年2月29日 下午1:41:20
	 */
	public String getPayableAmountStr() {
		if(payableAmount == null || BigDecimal.ZERO.compareTo(payableAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(payableAmount);
	}
	/**
	 * 
	 * @Description:还款状态
	 * @return
	 * @author: chaisen
	 * @time:2016年2月29日 下午1:43:51
	 */
	public String getStatusStr(){
		if(status==null){
			return "";
		}
		if(StatusEnum.INTEREST_RECORD_WAITPAY.getStatus()==status){
			return StatusEnum.INTEREST_RECORD_WAITPAY.getDesc();
		}
		if(StatusEnum.INTEREST_RECORD_HADPAY.getStatus()==status){
			return StatusEnum.INTEREST_RECORD_HADPAY.getDesc();
		}
		if(StatusEnum.INTEREST_RECORD_OVERDUE.getStatus()==status){
			return StatusEnum.INTEREST_RECORD_OVERDUE.getDesc();
		}
		return "";
	}
	/**
	 * 
	 * @Description:TODO
	 * @return
	 * @author: chaisen
	 * @time:2016年2月29日 下午2:28:08
	 */
	public String getOverdueStatusStr(){
		if(overdueStatus==null){
			return "";
		}
		if(StatusEnum.INTEREST_RECORD_WAITPAY.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_RECORD_WAITPAY.getDesc();
		}
		if(StatusEnum.INTEREST_RECORD_UNDERWRITE.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_RECORD_UNDERWRITE.getDesc();
		}
		if(StatusEnum.INTEREST_RECORD_HADPAY.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_RECORD_HADPAY.getDesc();
		}
		if(StatusEnum.INTEREST_RECORD_OVERDUE.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_RECORD_OVERDUE.getDesc();
		}
		return "";
	}
	/**
	 * 
	 * @Description:还款类型
	 * @return
	 * @author: chaisen
	 * @time:2016年3月10日 下午1:25:43
	 */
	public String getPayTypesStr(){
		if(payTypes==null){
			return "";
		}
		if(1==payTypes){
			return "本金+利息";
		}
		if(2==payTypes){
			return "利息";
		}
		return "";
	}
	public Integer getOverdueStatus() {
		return overdueStatus;
	}
	public void setOverdueStatus(Integer overdueStatus) {
		this.overdueStatus = overdueStatus;
	}
	public Date getRefundTime() {
		return refundTime;
	}
	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}
	public BigDecimal getPayableAmount() {
		return payableAmount;
	}
	public void setPayableAmount(BigDecimal payableAmount) {
		this.payableAmount = payableAmount;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getPayTypes() {
		return payTypes;
	}
	public void setPayTypes(Integer payTypes) {
		this.payTypes = payTypes;
	}
	public Integer getUnderwrite() {
		return underwrite;
	}
	public void setUnderwrite(Integer underwrite) {
		this.underwrite = underwrite;
	}
	public Integer getOverdue() {
		return overdue;
	}
	public void setOverdue(Integer overdue) {
		this.overdue = overdue;
	}
	
	public String getUnderwriteStr(){
		if(underwrite==null){
			return "无";
		}
		if(1==underwrite){
			return "有";
		}else{
			return "无";
		}
	}
	public String getOverdueStr(){
		if(overdue==null){
			return "无";
		}
		if(1==overdue){
			return "有";
		}else{
			return "无";
		}
	}
	
	

}
