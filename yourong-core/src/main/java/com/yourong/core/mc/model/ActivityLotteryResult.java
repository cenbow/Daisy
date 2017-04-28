package com.yourong.core.mc.model;

import java.math.BigDecimal;
import java.util.Date;

public class ActivityLotteryResult {
    /**主键**/
    private Long id;

    /**活动ID**/
    private Long activityId;

    /**活动监控ID**/
    private Long lotteryId;

    /**会员ID**/
    private Long memberId;

    /**会员ID**/
    private String memberName;
    
    /**活动奖励内容**/
    private String rewardInfo;

    /**奖品类型:1现金券2收益券3礼品4其他**/
    private Integer rewardType;

    /**对应奖品的标识**/
    private String rewardId;

    /**状态（0未领取、1已领取）**/
    private Integer status;

    /**备注**/
    private String remark;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    /**抽奖时间间隔**/
    private String drawInterval;
    
    /**活动周期约束**/
    private String cycleStr;
    
    /**活动下注**/
    private Integer chip;
    
    /**最终奖品**/
    private String rewardResult;
    
    private Date guessStartTime;
    
    private Date guessEndTime;
    
    private Integer sort;
    
    private Integer lotteryStatus;
    
    private Integer number;
    
    private BigDecimal totalRewardAmount;
    
    
    
    public BigDecimal getTotalRewardAmount() {
		return totalRewardAmount;
	}

	public void setTotalRewardAmount(BigDecimal totalRewardAmount) {
		this.totalRewardAmount = totalRewardAmount;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getLotteryStatus() {
		return lotteryStatus;
	}

	public void setLotteryStatus(Integer lotteryStatus) {
		this.lotteryStatus = lotteryStatus;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

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

	public String getCycleStr() {
		return cycleStr;
	}

	public void setCycleStr(String cycleStr) {
		this.cycleStr = cycleStr;
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

	public Date getGuessStartTime() {
		return guessStartTime;
	}

	public void setGuessStartTime(Date guessStartTime) {
		this.guessStartTime = guessStartTime;
	}

	public Date getGuessEndTime() {
		return guessEndTime;
	}

	public void setGuessEndTime(Date guessEndTime) {
		this.guessEndTime = guessEndTime;
	}
	
}