package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class LeaseBonusDetail extends AbstractBaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 主键 **/
	private Long id;

	/** 租赁分红id **/
	private Long leaseBonusId;

	/** 租赁分红结算id **/
	private Long leaseDetailId;

	/** 项目id **/
	private Long projectId;

	/** 用户id **/
	private Long memberId;

	/** 交易id **/
	private Long transactionId;

	/** 新浪代收交易号 **/
	private String sinaCollectNo;

	/** 新浪代付交易号 **/
	private String sinaPayNo;

	/** 分红值 **/
	private BigDecimal bonusAmount;

	/** 分红额外利率 **/
	private BigDecimal bonusRate;

	/** 创建时间 **/
	private Date createTime;

	/** 更新时间 **/
	private Date updateTime;

	/** 用户昵称 */
	private String username;

	/** 用户手机号码 */
	private Long mobile;

	/** 个人头像url */
	private String avatars;

	/** 删除标记（0-正常；1-删除） **/
	private Integer delFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLeaseBonusId() {
		return leaseBonusId;
	}

	public void setLeaseBonusId(Long leaseBonusId) {
		this.leaseBonusId = leaseBonusId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getSinaCollectNo() {
		return sinaCollectNo;
	}

	public void setSinaCollectNo(String sinaCollectNo) {
		this.sinaCollectNo = sinaCollectNo == null ? null : sinaCollectNo.trim();
	}

	public String getSinaPayNo() {
		return sinaPayNo;
	}

	public void setSinaPayNo(String sinaPayNo) {
		this.sinaPayNo = sinaPayNo == null ? null : sinaPayNo.trim();
	}

	public BigDecimal getBonusAmount() {
		return bonusAmount;
	}

	public void setBonusAmount(BigDecimal bonusAmount) {
		this.bonusAmount = bonusAmount;
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

	public String getBonusAmountStr() {
		return FormulaUtil.getFormatPrice(bonusAmount);
	}

	public String getUsername() {
		if (StringUtil.isNotBlank(username)) {
			return StringUtil.maskString(username, StringUtil.ASTERISK, 1, 1, 3);
		} 
		if (mobile!=null) {
			return StringUtil.maskString(mobile.toString(), StringUtil.ASTERISK, 3, 2, 4);
		}
		return "";
	}

	public Long getLeaseDetailId() {
		return leaseDetailId;
	}

	public void setLeaseDetailId(Long leaseDetailId) {
		this.leaseDetailId = leaseDetailId;
	}

	public BigDecimal getBonusRate() {
		return bonusRate;
	}

	public void setBonusRate(BigDecimal bonusRate) {
		this.bonusRate = bonusRate;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
	
	public String getCreateTimeStr(){
		return DateUtils.formatDatetoString(createTime, DateUtils.DATE_FMT_3);
	}
}