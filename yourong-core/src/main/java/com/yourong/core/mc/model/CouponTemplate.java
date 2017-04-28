package com.yourong.core.mc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class CouponTemplate implements Serializable {
	/**
	 * 优惠券模板
	 */
	private static final long serialVersionUID = -1556656446043628674L;

	/****/
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
	private Integer webScope;

	/** wap(0-不支持；1-支持) **/
	private Integer wapScope;

	/** app(0-不支持；1-支持) **/
	private Integer appScope;

	/** 起投金额 **/
	private BigDecimal amountScope;

	/** 起投期限 **/
	private Integer daysScope;

	/** 以日期计算的开始时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;

	/** 以日期计算的结束时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;

	/** 用户领取后有效期计算的天数 **/
	private Integer days;

	/** 0：未印刷 1：已印刷 **/
	private Integer status;

	/** 使用条件(预留，暂不使用) **/
	private String useCondition;

	/****/
	private Long createBy;

	/****/
	private Date createTime;

	/****/
	private Date updateTime;

	/** 打印数量 **/
	private Integer printNum;

	/** 合计金额 **/
	private BigDecimal totalAmount;

	/** 打印次数 */
	private Integer printTimes;

	/** 已领用数量 */
	private Integer receivedNum;

	/** 已使用数量 */
	private Integer usedNum;

	/** 删除标记 */
	private Integer delFlag;

	/**
	 * 排序时间
	 */
	private Date sortTime;
	
	/**兑换值*/
	private BigDecimal exchangeAmount;
	
	private Integer extraInterestType;
	
	private Integer extraInterestDay;
	
	


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

	public Integer getDays() {
		if(days == null){
			days = 0;
		}
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUseCondition() {
		return useCondition;
	}

	public void setUseCondition(String useCondition) {
		this.useCondition = useCondition == null ? null : useCondition.trim();
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getPrintNum() {
		return printNum;
	}

	public void setPrintNum(Integer printNum) {
		this.printNum = printNum;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getPrintTimes() {
		return printTimes;
	}

	public void setPrintTimes(Integer printTimes) {
		this.printTimes = printTimes;
	}

	public Integer getReceivedNum() {
		return receivedNum;
	}

	public void setReceivedNum(Integer receivedNum) {
		this.receivedNum = receivedNum;
	}

	public Integer getUsedNum() {
		return usedNum;
	}

	public void setUsedNum(Integer usedNum) {
		this.usedNum = usedNum;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Date getSortTime() {
		return sortTime;
	}

	public void setSortTime(Date sortTime) {
		this.sortTime = sortTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public String getFormatAmount(){
		return FormulaUtil.getFormatPriceNoSep(getAmount());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CouponTemplate [id=");
		builder.append(id);
		builder.append(", couponType=");
		builder.append(couponType);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", vaildCalcType=");
		builder.append(vaildCalcType);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", days=");
		builder.append(days);
		builder.append(", status=");
		builder.append(status);
		builder.append(", useCondition=");
		builder.append(useCondition);
		builder.append(", createBy=");
		builder.append(createBy);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", updateTime=");
		builder.append(updateTime);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((couponType == null) ? 0 : couponType.hashCode());
		result = prime * result + ((createBy == null) ? 0 : createBy.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((days == null) ? 0 : days.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result + ((useCondition == null) ? 0 : useCondition.hashCode());
		result = prime * result + ((vaildCalcType == null) ? 0 : vaildCalcType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CouponTemplate other = (CouponTemplate) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (couponType == null) {
			if (other.couponType != null)
				return false;
		} else if (!couponType.equals(other.couponType))
			return false;
		if (createBy == null) {
			if (other.createBy != null)
				return false;
		} else if (!createBy.equals(other.createBy))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (days == null) {
			if (other.days != null)
				return false;
		} else if (!days.equals(other.days))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		if (useCondition == null) {
			if (other.useCondition != null)
				return false;
		} else if (!useCondition.equals(other.useCondition))
			return false;
		if (vaildCalcType == null) {
			if (other.vaildCalcType != null)
				return false;
		} else if (!vaildCalcType.equals(other.vaildCalcType))
			return false;
		return true;
	}
	
	public String getAmountScopeStr(){
		if(amountScope!=null){
			return FormulaUtil.getFormatPriceNoSep(amountScope);
		}
		return "";
	}

	public BigDecimal getExchangeAmount() {
		return exchangeAmount;
	}

	public void setExchangeAmount(BigDecimal exchangeAmount) {
		this.exchangeAmount = exchangeAmount;
	}

}