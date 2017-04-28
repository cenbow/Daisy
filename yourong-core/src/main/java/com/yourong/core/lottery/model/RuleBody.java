package com.yourong.core.lottery.model;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 抽奖规则类
 * 
 * @author wangyanji
 *
 */
public class RuleBody extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7529912064968094120L;

	private Long activityId;

	private Long memberId;

	private Long mobile;

	private String cycleStr;

	private String activityName;

	/**
	 * 有效奖品数
	 */
	private Integer rewardsAvailableNum;

	/**
	 * 奖池总数包含谢谢惠顾
	 */
	private Integer rewardsPoolMaxNum;

	/**
	 * 是否允许奖品重复
	 */
	private boolean repeatDrawFlag;

	/**
	 * 奖池是否过滤已抽奖品
	 */
	private boolean exceptDrawedRewards;

	/**
	 * 奖品概率等分
	 */
	private boolean probabilityByAverage;

	/**
	 * 扣除的扣除方式，参考状态枚举类
	 */
	private Integer deductMode;

	/**
	 * 扣除值
	 */
	private Integer deductValue;

	/**
	 * 参加数量
	 */
	private Integer participateNum;

	/**
	 * 扣除值备注
	 */
	private String deductRemark;

	/**
	 * 编组
	 */
	private String rewardGroup;

	/**
	 * 奖励对应业务标识
	 */
	private String rewardId;

	/**
	 * 校验专属类
	 */
	private Object verificationObj;

	public Integer getRewardsAvailableNum() {
		return rewardsAvailableNum;
	}

	public void setRewardsAvailableNum(Integer rewardsAvailableNum) {
		this.rewardsAvailableNum = rewardsAvailableNum;
	}

	public Integer getRewardsPoolMaxNum() {
		return rewardsPoolMaxNum;
	}

	public void setRewardsPoolMaxNum(Integer rewardsPoolMaxNum) {
		this.rewardsPoolMaxNum = rewardsPoolMaxNum;
	}

	public boolean isRepeatDrawFlag() {
		return repeatDrawFlag;
	}

	public void setRepeatDrawFlag(boolean repeatDrawFlag) {
		this.repeatDrawFlag = repeatDrawFlag;
	}

	public Integer getDeductMode() {
		return deductMode;
	}

	public void setDeductMode(Integer deductMode) {
		this.deductMode = deductMode;
	}

	public Integer getDeductValue() {
		return deductValue;
	}

	public void setDeductValue(Integer deductValue) {
		this.deductValue = deductValue;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getDeductRemark() {
		return deductRemark;
	}

	public void setDeductRemark(String deductRemark) {
		this.deductRemark = deductRemark;
	}

	public String getCycleStr() {
		return cycleStr;
	}

	public void setCycleStr(String cycleStr) {
		this.cycleStr = cycleStr;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Integer getParticipateNum() {
		return participateNum;
	}

	public void setParticipateNum(Integer participateNum) {
		this.participateNum = participateNum;
	}

	public boolean isExceptDrawedRewards() {
		return exceptDrawedRewards;
	}

	public void setExceptDrawedRewards(boolean exceptDrawedRewards) {
		this.exceptDrawedRewards = exceptDrawedRewards;
	}

	public boolean isProbabilityByAverage() {
		return probabilityByAverage;
	}

	public void setProbabilityByAverage(boolean probabilityByAverage) {
		this.probabilityByAverage = probabilityByAverage;
	}

	public String getRewardGroup() {
		return rewardGroup;
	}

	public void setRewardGroup(String rewardGroup) {
		this.rewardGroup = rewardGroup;
	}

	public String getRewardId() {
		return rewardId;
	}

	public void setRewardId(String rewardId) {
		this.rewardId = rewardId;
	}

	public Object getVerificationObj() {
		return verificationObj;
	}

	public void setVerificationObj(Object verificationObj) {
		this.verificationObj = verificationObj;
	}

}

