package com.yourong.core.mc.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.uc.model.Member;

public class Coupon extends AbstractBaseObject  {
	/**
	 * 优惠券
	 */
	private static final long serialVersionUID = -6263092266781392061L;

	/** 主键id **/
	private Long id;

	/** 优惠券编码 **/
	private String couponCode;

	/** 优惠券模板id **/
	private Long couponTemplateId;

	/** 优惠券模板名称 **/
	private String couponTemplateName;
	

	/** 优惠券模板印刷id **/
	private Long couponTemplatePrintId;

	/** 优惠券类型 1-现金券 2-收益券 **/
	private Integer couponType;

	/** 优惠券面额(如果是现金券则表示金额，收益券则表示收益) **/
	private BigDecimal amount;

	/** 优惠券状态 0-未领取 1-已领取，未使用 2-已使用 3-未领取，已过期 4-已领取，已过期 **/
	private Integer status;

	/** 有效类型 0-永久 1-按时间计算 2-按领取后天数计算 **/
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

	/** 用户领取后有效期计算的天数 **/
	private Integer days;

	/** 开始使用日期 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date startDate;

	/** 结束使用日期 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date endDate;

	/** 持有人id **/
	private Long holderId;

	/** 领取时间 **/
	private Date receiveTime;

	/** 使用时间 **/
	private Date usedTime;

	/****/
	private String remarks;

	/****/
	private Long activityId;

	/** 发放人用户id **/
	private Long senderId;

	/** 使用条件 **/
	private String useCondition;

	/****/
	private Integer projectId;
	
	/** 优惠券发送渠道（0-web，1-m，2-app，3-backend） **/
	private Integer way;
	
	/** 优惠券获取方式（0-人气值兑换，1-注册，2-活动，3-后台补偿） **/
	private Integer accessSource;

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
	
	/****/
	private Long transactionId;

	/****/
	private Date createTime;

	/****/
	private Date updateTime;

	/** 优惠券模板名称 **/
	private String name;

	/** 优惠券模板名称 **/
	private String activityName;
	
	/** 优惠券持有人信息**/
	private Member member ;
	
	/**投资使用是否受限**/
	private Integer limited;
	
	//是否显示客户端可用
	private boolean isShowClient = true;
	
	//是否显示有效期
	private boolean isShowCouponValidity = true;
	
	//是否显示金额
	private boolean isShowCouponAmountScope = true;
	
	//是否显示天数
	private boolean isShowCouponDaysScope  = true;
	
	// 现金券投资本金是否大于现金券金额
	private boolean isCashCouponAmountLessThanInvest = true;
	
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

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode == null ? null : couponCode.trim();
	}

	public Long getCouponTemplateId() {
		return couponTemplateId;
	}

	public void setCouponTemplateId(Long couponTemplateId) {
		this.couponTemplateId = couponTemplateId;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getVaildCalcType() {
		return vaildCalcType;
	}

	public void setVaildCalcType(Integer vaildCalcType) {
		this.vaildCalcType = vaildCalcType;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
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
		if(daysScope == null){
			daysScope = 0;
		}
		return daysScope;
	}

	public void setDaysScope(Integer daysScope) {
		this.daysScope = daysScope;
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

	public Long getHolderId() {
		return holderId;
	}

	public void setHolderId(Long holderId) {
		this.holderId = holderId;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public Date getUsedTime() {
		return usedTime;
	}

	public void setUsedTime(Date usedTime) {
		this.usedTime = usedTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks == null ? null : remarks.trim();
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getUseCondition() {
		return useCondition;
	}

	public void setUseCondition(String useCondition) {
		this.useCondition = useCondition == null ? null : useCondition.trim();
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
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

	public Long getCouponTemplatePrintId() {
		return couponTemplatePrintId;
	}

	public void setCouponTemplatePrintId(Long couponTemplatePrintId) {
		this.couponTemplatePrintId = couponTemplatePrintId;
	}

	public String getEndDateStr() {
		if (endDate != null) {
			return DateUtils.getStrFromDate(endDate, DateUtils.DATE_FMT_3);
		}
		return null;
	}

	public String getStartDateStr() {
		if (startDate != null) {
			return DateUtils.getStrFromDate(startDate, DateUtils.DATE_FMT_3);
		}
		return null;
	}

	public String getReceiveTimeStr() {
		if (receiveTime != null) {
			return DateUtils.getStrFromDate(receiveTime, DateUtils.DATE_FMT_3);
		}
		return null;
	}

	public String getUsedTimeStr() {
		if (usedTime != null) {
			return DateUtils.getStrFromDate(usedTime, DateUtils.DATE_FMT_3);
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	public String getAmountScopeStr() {
		if(amountScope != null){
			return FormulaUtil.getFormatPrice(amountScope); 
		}
		return "";
	}

	public Integer getLimited() {
		return limited;
	}

	public void setLimited(Integer limited) {
		this.limited = limited;
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
	

	public boolean isCashCouponAmountLessThanInvest() {
		return isCashCouponAmountLessThanInvest;
	}

	public void setCashCouponAmountLessThanInvest(boolean isCashCouponAmountLessThanInvest) {
		this.isCashCouponAmountLessThanInvest = isCashCouponAmountLessThanInvest;
	}

	public String getCouponTemplateName() {
		return couponTemplateName;
	}

	public void setCouponTemplateName(String couponTemplateName) {
		this.couponTemplateName = couponTemplateName;
	}

	public String getCouponPrivileges(){
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
   public boolean isAvailable(){
	   if(getDaysScope()<=0 && getAmountScope().compareTo(BigDecimal.ZERO) <=0 && (getWebScope()==1&&getWapScope()==1&&getAppScope()==1) && isShowCouponValidity()){
		   return true;
	   }
	   return false;
   }

	/**
	 * @return the way
	 */
	public Integer getWay() {
		return way;
	}

	/**
	 * @param way the way to set
	 */
	public void setWay(Integer way) {
		this.way = way;
	}

	/**
	 * @return the accessSource
	 */
	public Integer getAccessSource() {
		return accessSource;
	}

	/**
	 * @param accessSource the accessSource to set
	 */
	public void setAccessSource(Integer accessSource) {
		this.accessSource = accessSource;
	}
   
   
}