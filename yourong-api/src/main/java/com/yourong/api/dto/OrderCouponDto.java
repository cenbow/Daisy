package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

/**
 *下单过程中使用的收益券 
 */
public class OrderCouponDto {
	/** 优惠券编码 **/
	private String couponCode;
	
	/** 优惠券面额(如果是现金券则表示金额，收益券则表示收益) **/
	private BigDecimal amount;
	
	/** 有效日期 **/
	private Date endDate;
	
	/**投资使用是否受限**/
	@JSONField(serialize=false)
	private Integer limited;
	
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
	@JSONField(serialize=false)
	private Integer daysScope;
	
	/** 用户领取后有效期计算的天数 **/
	private Integer days;

	/** 开始使用日期 **/
	private Date startDate;
	
	//是否显示客户端可用
	@JSONField(serialize=false)
	private boolean isShowClient = true;
	
	//是否显示有效期
	@JSONField(serialize=false)
	private boolean isShowCouponValidity = true;
	
	//是否显示金额
	@JSONField(serialize=false)
	private boolean isShowCouponAmountScope = true;
	
	//是否显示天数
	@JSONField(serialize=false)
	private boolean isShowCouponDaysScope  = true;
	
	private Integer extraInterestType;
	
	private Integer extraInterestDay;
	
	private String extraName;
	
	


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

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getLimited() {
		return limited;
	}

	public void setLimited(Integer limited) {
		this.limited = limited;
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
	
	public boolean isShowClient() {
		return isShowClient;
	}

	public void setShowClient(boolean isShowClient) {
		this.isShowClient = isShowClient;
	}

	public boolean isShowCouponValidity() {
		return isShowCouponValidity;
	}

	public void setShowCouponValidity(boolean isShowCouponValidity) {
		this.isShowCouponValidity = isShowCouponValidity;
	}

	public boolean isShowCouponAmountScope() {
		return isShowCouponAmountScope;
	}

	public void setShowCouponAmountScope(boolean isShowCouponAmountScope) {
		this.isShowCouponAmountScope = isShowCouponAmountScope;
	}

	public boolean isShowCouponDaysScope() {
		return isShowCouponDaysScope;
	}

	public void setShowCouponDaysScope(boolean isShowCouponDaysScope) {
		this.isShowCouponDaysScope = isShowCouponDaysScope;
	}

	/**
	 * 是否可用
	 * @return
	 */
	public boolean getIsAvailable(){
		if(getLimited() == null || getLimited() <=0){
			return true;
		}
		return false;
	}
	
	@JSONField(serialize=false)
	public String getCouponMsg(){
		if(!getIsAvailable()){
			StringBuffer sb = new StringBuffer();
			sb.append("使用规则：\n");
			sb.append(getCouponPrivileges());
			if(StringUtil.isNotBlank(getCouponValidity())){
				sb.append("\n").append(getCouponValidity());
			}
			if(StringUtil.isNotBlank(getCouponAmountScope())){
				sb.append("\n").append(getCouponAmountScope());
			}
			if(StringUtil.isNotBlank(getCouponDaysScope())){
				sb.append("\n").append(getCouponDaysScope());
			}
			return sb.toString();
		}
		return null;
	}
	
	public String getCouponPrivileges(){
		if(isUnlimited()){
			return "";
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
		String validity = null;
		if(getVaildCalcType() == 0){
			validity =  "长期有效";
		}else if(getVaildCalcType()==1 || getVaildCalcType()==2){
			String _startDate = DateUtils.formatDatetoString(getStartDate(), "yyyy.M.d");
			String _endDate =  DateUtils.formatDatetoString(getEndDate(), "yyyy.M.d");
			if(getStartDate().getTime() > DateUtils.getCurrentDate().getTime()){
				validity =  "有效期"+_startDate+"-"+_endDate;
			}else{			
				validity = "有效期 "+_endDate;
			}
		}
		return validity;
	}
	
	/**
	 * 投资限额
	 * @return
	 */
	public String getCouponAmountScope(){
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
		if(getDaysScope() != null && getDaysScope()> 0){
			return "投资期限≥"+getDaysScope()+"天可用";
		}
		return null;
	}
	
	/**
	 * 是否显示全网通用
	 * @return
	 */
	@JSONField(serialize=false)
   private boolean isUnlimited(){
	   if(getDaysScope()<=0 && getAmountScope().compareTo(BigDecimal.ZERO) <=0 && (getWebScope()==1&&getWapScope()==1&&getAppScope()==1) && isShowCouponValidity()){
		   return true;
	   }
	   return false;
   }
}
