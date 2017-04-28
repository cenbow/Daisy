package com.yourong.core.uc.model;

import java.io.Serializable;
import java.util.Date;

public class MemberNotifySettings implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3824549909227941574L;

	/****/
    private Long id;

    /**用户id**/
    private Long memberId;

    /**通知类型 0-充值操作 ；1-提现操作 2-奖励兑付 4-投资成功 5-还本付息**/
    private Integer notifyType;

    /**通知方式 1-系统通知 2-短信通知  3-邮件通知**/
    private Integer notifyWay;

    /**状态 1-开启 2-停用**/
    private Integer status;

    /****/
    private Date createTime;

    /****/
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(Integer notifyType) {
        this.notifyType = notifyType;
    }

    public Integer getNotifyWay() {
        return notifyWay;
    }

    public void setNotifyWay(Integer notifyWay) {
        this.notifyWay = notifyWay;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((memberId == null) ? 0 : memberId.hashCode());
		result = prime * result
				+ ((notifyType == null) ? 0 : notifyType.hashCode());
		result = prime * result
				+ ((notifyWay == null) ? 0 : notifyWay.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((updateTime == null) ? 0 : updateTime.hashCode());
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
		MemberNotifySettings other = (MemberNotifySettings) obj;
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
		if (memberId == null) {
			if (other.memberId != null)
				return false;
		} else if (!memberId.equals(other.memberId))
			return false;
		if (notifyType == null) {
			if (other.notifyType != null)
				return false;
		} else if (!notifyType.equals(other.notifyType))
			return false;
		if (notifyWay == null) {
			if (other.notifyWay != null)
				return false;
		} else if (!notifyWay.equals(other.notifyWay))
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
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MemberNotifySettings [id=");
		builder.append(id);
		builder.append(", memberId=");
		builder.append(memberId);
		builder.append(", notifyType=");
		builder.append(notifyType);
		builder.append(", notifyWay=");
		builder.append(notifyWay);
		builder.append(", status=");
		builder.append(status);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", updateTime=");
		builder.append(updateTime);
		builder.append("]");
		return builder.toString();
	}
}