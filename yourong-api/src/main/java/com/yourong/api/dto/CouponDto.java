package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class CouponDto {
	/** 主键id **/
	private Long id;

	/** 优惠券编码 **/
	private String couponCode;
	
	/** 优惠券模板名称 **/
	private String name;
	
	/** 开始使用日期 **/
	private Date startDate;

	/** 结束使用日期 **/
	private Date endDate;
	
	private BigDecimal amount;
	
	/** 优惠券状态 0-未领取 1-已领取，未使用 2-已使用 3-未领取，已过期 4-已领取，已过期 5-使用中 **/
	private Integer status;
	
	/** 起投金额 **/
	private BigDecimal amountScope;

	/** 起投期限 **/
	private Integer daysScope;

	/** 用户领取后有效期计算的天数 **/
	private Integer days;
	
	/** 有效类型 0-永久 1-按时间计算 2-按领取后天数计算 **/
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
	
	/**使用条件**/
	@JSONField(serialize=false)
	private String useCondition;
	
	
	private Integer extraInterestType;
	
	private Integer extraInterestDay;
	
	private String extraName;
	
	private Integer remainDay;
	


	public String getExtraName() {
		return extraName;
	}

	public void setExtraName(String extraName) {
		this.extraName = extraName;
	}

	public Integer getExtraInterestType() {
		return extraInterestType;
	}

	public void setExtraInterestType(Integer extraInterestType) {
		this.extraInterestType = extraInterestType;
	}

	public Integer getExtraInterestDay() {
		return extraInterestDay;
	}

	public void setExtraInterestDay(Integer extraInterestDay) {
		this.extraInterestDay = extraInterestDay;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BigDecimal getAmountScope() {
		return amountScope;
	}

	public void setAmountScope(BigDecimal amountScope) {
		this.amountScope = amountScope;
	}

	public Integer getDaysScope() {
		return daysScope;
	}

	public void setDaysScope(Integer daysScope) {
		this.daysScope = daysScope;
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

	public Integer getVaildCalcType() {
		return vaildCalcType;
	}

	public void setVaildCalcType(Integer vaildCalcType) {
		this.vaildCalcType = vaildCalcType;
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
	
	
	public String getCouponPrivileges(){
		if(isAvailable()){
			return getUseCondition();
		}
		if(getWebScope()==1&&getWapScope()==1&&getAppScope()==1){
//			return "各客户端通用";
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
		return null;
	}
	
	/**
	 * 兑换有效期
	 * @return
	 */
	public String getCouponValidity(){
		if(getVaildCalcType() == 0){
			return "长期有效";
		}else if(getVaildCalcType()==1 || getVaildCalcType()==2){
			String _startDate = DateUtils.formatDatetoString(getStartDate(), "yyyy.M.d");
			String _endDate =  DateUtils.formatDatetoString(getEndDate(), "yyyy.M.d");
//			if(getStartDate().getTime() > DateUtils.getCurrentDate().getTime()){
//				
//			}			
			return _startDate+"-"+_endDate;
		}
		return null;
	}
	
	/**
	 * 投资限额
	 * @return
	 */
	public String getCouponAmountScope(){
		if(!isAvailable()){
			if(getAmountScope() != null && getAmountScope().compareTo(BigDecimal.ZERO) > 0){
				return "投资额≥"+FormulaUtil.getFormatPrice(getAmountScope())+"元可用";
			}
		}
		return null;
	}
	
	/**
	 * 投资收益天数限制
	 * @return
	 */
	public String getCouponDaysScope(){
		if(!isAvailable()){
			if(getDaysScope() != null && getDaysScope()> 0){
				return "投资期限≥"+getDaysScope()+"天可用";
			}
		}
		return null;
	}

	public String getUseCondition() {
		return useCondition;
	}

	public void setUseCondition(String useCondition) {
		this.useCondition = useCondition;
	}
	
	@JSONField(serialize=false)
	public boolean isAvailable(){
		if(StringUtil.isNotBlank(getUseCondition()) && getUseCondition().equals("全网通用")){
			return true;
		}
		return false;
	}

	public Integer getRemainDay() {
		return remainDay;
	}

	public void setRemainDay(Integer remainDay) {
		this.remainDay = remainDay;
	}
	
}
