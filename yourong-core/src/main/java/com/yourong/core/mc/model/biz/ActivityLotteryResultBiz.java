package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.util.Date;

import com.yourong.common.util.DateUtils;

public class ActivityLotteryResultBiz implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3915010812234179982L;

	/** 主键 **/
	private Long id;

	/** 活动ID **/
	private Long activityId;

	/** 活动监控ID **/
	private Long lotteryId;

	/** 会员ID **/
	private Long memberId;

	/** 会员ID **/
	private String memberName;

	/** 活动奖励内容 **/
	private String rewardInfo;

	/** 奖品类型:1现金券2收益券3礼品4其他 **/
	private Integer rewardType;

	/** 对应奖品的标识 **/
	private String rewardId;

	/** 状态（0未领取、1已领取） **/
	private Integer status;

	/** 备注 **/
	private String remark;

	/** 创建时间 **/
	private Date createTime;

	/** 更新时间 **/
	private Date updateTime;

	/** 活动下注 **/
	private Integer chip;

	/** 最终奖品 **/
	private String rewardResult;

	/** 抽奖时间间隔 **/
	private String drawInterval;

	/** 头像 **/
	private String Avatar;

	/** 活动留言 */
	private String content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Long getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(Long lotteryId) {
		this.lotteryId = lotteryId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getRewardInfo() {
		return rewardInfo;
	}

	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo == null ? null : rewardInfo.trim();
	}

	public Integer getRewardType() {
		return rewardType;
	}

	public void setRewardType(Integer rewardType) {
		this.rewardType = rewardType;
	}

	public String getRewardId() {
		return rewardId;
	}

	public void setRewardId(String rewardId) {
		this.rewardId = rewardId == null ? null : rewardId.trim();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
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

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getDrawInterval() {
		return drawInterval;
	}

	public void setDrawInterval(String drawInterval) {
		this.drawInterval = drawInterval;
	}

	public String getAvatar() {
		return Avatar;
	}

	public void setAvatar(String avatar) {
		Avatar = avatar;
	}

	public Integer getChip() {
		return chip;
	}

	public void setChip(Integer chip) {
		this.chip = chip;
	}

	public String getRewardResult() {
		return rewardResult;
	}

	public void setRewardResult(String rewardResult) {
		this.rewardResult = rewardResult;
	}

	public String getCreateTimeStr() {
		return DateUtils.prettyTime(getCreateTime());
	}
	public String getCreateTimeStr2() {
		return DateUtils.formatDatetoString(getCreateTime(), DateUtils.DATE_FMT_15);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}