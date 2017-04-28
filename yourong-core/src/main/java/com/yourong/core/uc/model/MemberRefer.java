package com.yourong.core.uc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class MemberRefer implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3110935353301247961L;

	/**id**/
    private Long id;

    /**推荐人**/
    private Long referral;

    /**被推荐人**/
    private Long referred;

    /**推荐方式：1：推荐注册	2：推荐投资 3：其他**/
    private Integer referType;

    /**推荐来源：1：普通url	2：站内注册 3：微博 4：微信**/
    private Integer referSource;

    /**推荐链接**/
    private String referLink;

    /**推荐获得的奖励**/
    private BigDecimal reward;

    /**备注**/
    private String remarks;

    /**创建时间**/
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReferral() {
        return referral;
    }

    public void setReferral(Long referral) {
        this.referral = referral;
    }

    public Long getReferred() {
        return referred;
    }

    public void setReferred(Long referred) {
        this.referred = referred;
    }

    public Integer getReferType() {
        return referType;
    }

    public void setReferType(Integer referType) {
        this.referType = referType;
    }

    public Integer getReferSource() {
        return referSource;
    }

    public void setReferSource(Integer referSource) {
        this.referSource = referSource;
    }

    public String getReferLink() {
        return referLink;
    }

    public void setReferLink(String referLink) {
        this.referLink = referLink == null ? null : referLink.trim();
    }

    public BigDecimal getReward() {
        return reward;
    }

    public void setReward(BigDecimal reward) {
        this.reward = reward;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((referLink == null) ? 0 : referLink.hashCode());
		result = prime * result
				+ ((referSource == null) ? 0 : referSource.hashCode());
		result = prime * result
				+ ((referType == null) ? 0 : referType.hashCode());
		result = prime * result
				+ ((referral == null) ? 0 : referral.hashCode());
		result = prime * result
				+ ((referred == null) ? 0 : referred.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((reward == null) ? 0 : reward.hashCode());
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
		MemberRefer other = (MemberRefer) obj;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (referLink == null) {
			if (other.referLink != null)
				return false;
		} else if (!referLink.equals(other.referLink))
			return false;
		if (referSource == null) {
			if (other.referSource != null)
				return false;
		} else if (!referSource.equals(other.referSource))
			return false;
		if (referType == null) {
			if (other.referType != null)
				return false;
		} else if (!referType.equals(other.referType))
			return false;
		if (referral == null) {
			if (other.referral != null)
				return false;
		} else if (!referral.equals(other.referral))
			return false;
		if (referred == null) {
			if (other.referred != null)
				return false;
		} else if (!referred.equals(other.referred))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (reward == null) {
			if (other.reward != null)
				return false;
		} else if (!reward.equals(other.reward))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MemberRefer [id=");
		builder.append(id);
		builder.append(", referral=");
		builder.append(referral);
		builder.append(", referred=");
		builder.append(referred);
		builder.append(", referType=");
		builder.append(referType);
		builder.append(", referSource=");
		builder.append(referSource);
		builder.append(", referLink=");
		builder.append(referLink);
		builder.append(", reward=");
		builder.append(reward);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append("]");
		return builder.toString();
	}
}