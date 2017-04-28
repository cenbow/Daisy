package com.yourong.core.mc.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class CouponBiz extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 主键id **/
	private Long id;

	/** 优惠券编码 **/
	private String couponCode;

	/** 优惠券模板id **/
	private Long couponTemplateId;

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

	/** 用户领取后有效期计算的天数 **/
	private Integer days;

	/** 开始使用日期 **/
	private Date startDate;

	/** 结束使用日期 **/
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

	/****/
	private Long transactionId;

	/****/
	private Date createTime;

	/****/
	private Date updateTime;

	/** 优惠券模板名称 **/
	private String name;

	/** 用户昵称 */
	private String username;

	/** 用户手机号码 */
	private Long mobile;

	/** 个人头像url */
	private String avatars;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getAvatars() {
		return avatars;
	}

	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}
	
	/**领取时间格式化*/
	public String getReceiveTimeStr(){
		if(receiveTime!=null){
			return DateUtils.formatDatetoString(receiveTime,"yyyy.MM.dd HH:mm:ss");
		}
		return null;
	}
	
	public String getMaskUserName(){
		if(StringUtil.isNotBlank(username)) {
			return StringUtil.maskString(username, StringUtil.ASTERISK, 1, 1, 3);
		} else {
			return StringUtil.maskString(mobile.toString(), StringUtil.ASTERISK, 3, 2, 4);
		}
	}

}
