package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class CouponTemplateDto {
	
	private Long id;
	/** 模板名称 **/
	private String name;

	/** 优惠券类型 1-现金券 2-收益券 **/
	private Integer couponType;

	/** 面额(如果是现金券则表示金额，收益券则表示收益) **/
	private BigDecimal amount;

	/** 有效类型 0:永久 1:按时间计算 2：按领取后天数计算 **/
	private Integer vaildCalcType;

	/** web(0-不支持；1-支持) **/
	@JSONField(serialize=false)
	private Integer webScope;

	/** wap(0-不支持；1-支持) **/
	@JSONField(serialize=false)
	private Integer wapScope;

	/** app(0-不支持；1-支持) **/
	@JSONField(serialize=false)
	private Integer appScope;

	/** 起投金额 **/
	private BigDecimal amountScope;

	/** 起投期限 **/
	private Integer daysScope;
	
	/** 用户领取后有效期计算的天数 **/
	private Integer days;
	
	/** 以日期计算的开始时间 **/
	private Date startDate;
	
	/** 以日期计算的结束时间 **/
	private Date endDate;
	
	/** 使用条件(预留，暂不使用) **/
	@JSONField(serialize=false)
	private String useCondition;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getVaildCalcType() {
		return vaildCalcType;
	}

	public void setVaildCalcType(Integer vaildCalcType) {
		this.vaildCalcType = vaildCalcType;
	}

	public BigDecimal getAmountScope() {
		if(amountScope == null){
			amountScope = BigDecimal.ZERO;
		}
		return amountScope;
	}

	public void setAmountScope(BigDecimal amountScope) {
		this.amountScope = amountScope;
	}

	public Integer getDaysScope() {
		if(daysScope == null){
			daysScope = 0;
		}
		return daysScope;
	}

	public void setDaysScope(Integer daysScope) {
		this.daysScope = daysScope;
	}
	
	public Integer getWebScope() {
		if(webScope == null){
			webScope = 1;
		}
		return webScope;
	}

	public void setWebScope(Integer webScope) {
		this.webScope = webScope;
	}

	public Integer getWapScope() {
		if(wapScope == null){
			wapScope = 1;
		}
		return wapScope;
	}

	public void setWapScope(Integer wapScope) {
		this.wapScope = wapScope;
	}

	public Integer getAppScope() {
		if(appScope == null){
			appScope = 1;
		}
		return appScope;
	}

	public void setAppScope(Integer appScope) {
		this.appScope = appScope;
	}
	
	public Integer getDays() {
		if(days == null){
			days = 0;
		}
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCouponPrivileges(){
		if(isAvailable()){
			return "";
		}
		if(getWebScope()==1&&getWapScope()==1&&getAppScope()==1){
			return "各客户端通用";
		}else if(getWebScope()==1&&getWapScope()==1){
			return "限PC、M站使用";
		}else if(getWebScope()==1&&getAppScope()==1){
			return "限PC、APP使用";
		}else if(getWapScope()==1&&getAppScope()==1){
			return "限移动端使用";
		}else if(getWapScope()==1&&getAppScope()==0&&getWebScope()==0){
			return "限M站使用";
		}else if(getAppScope()==1&&getWapScope()==0&&getWebScope()==0){
			return "限APP使用";
		}else if(getWebScope()==1&&getWapScope()==0&&getAppScope()==0){
			return "限PC端使用";
		}
		return "各客户端通用";
	}
	
	/**
	 * 兑换有效期
	 * @return
	 */
	public String getCouponValidity(){
		if(getVaildCalcType() == 0){
			return "长期有效";
		}else if(getVaildCalcType()==1){
			String _startDate = DateUtils.formatDatetoString(getStartDate(), "yyyy.M.d");
			String _endDate =  DateUtils.formatDatetoString(getEndDate(), "yyyy.M.d");
			if(StringUtil.isNotBlank(_startDate) && StringUtil.isNotBlank(_endDate)){
				return _startDate+"-"+_endDate;
			}
		}else if(getVaildCalcType()==2){
			return "兑换后"+days+"天内有效";
		}
		return null;
	}
	
	/**
	 * 投资限额
	 * @return
	 */
	public String getCouponAmountScope(){
		if(isAvailable()){
			return null;
		}
		if(getAmountScope() != null && getAmountScope().compareTo(BigDecimal.ZERO) > 0){
			return "投资额≥"+FormulaUtil.getFormatPrice(getAmountScope())+"元可用";
		}
		return null;
	}
	
	/**
	 * 投资收益天数限制
	 * @return
	 */
	public String getCouponDaysScope(){
		if(isAvailable()){
			return null;
		}
		if(getDaysScope() != null && getDaysScope()> 0){
			return "投资期限≥"+getDaysScope()+"天可用";
		}
		return null;
	}
	

	/**
	 * 是否显示全网通用
	 * @return
	 */
   private boolean isAvailable(){
	   if(getDaysScope()<=0 && getAmountScope().compareTo(BigDecimal.ZERO) <=0 && (getWebScope()==1&&getWapScope()==1&&getAppScope()==1)){
		   return true;
	   }
	   return false;
   }
   
   public BigDecimal getExchangeAmount(){
	   BigDecimal amount = getAmount();
	   if(amount != null){
		   if(amount.compareTo(new BigDecimal(0.5)) == 0) {
				return new BigDecimal(APIPropertiesUtil.getProperties("business.exchangeProfitCouponAmount0.5"));
		   }else if (amount.compareTo(new BigDecimal(1)) == 0) {
			   return new BigDecimal(APIPropertiesUtil.getProperties("business.exchangeProfitCouponAmount1"));
		   }else if (amount.compareTo(new BigDecimal(1.5)) == 0) {
				return new BigDecimal(APIPropertiesUtil.getProperties("business.exchangeProfitCouponAmount1.5"));
		   }else if (amount.compareTo(new BigDecimal(2)) == 0) {
				return new BigDecimal(APIPropertiesUtil.getProperties("business.exchangeProfitCouponAmount2"));
		   }
	   }
	   return null;
   }
	
}
