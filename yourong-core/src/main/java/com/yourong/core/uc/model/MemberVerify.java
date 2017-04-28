package com.yourong.core.uc.model;

import java.io.Serializable;
import java.util.Date;

public class MemberVerify implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1267980765000474082L;

	/**id**/
    private Long id;

    /**会员号**/
    private Long memberId;

    /**认证类型：mobile identity email**/
    private String verifyType;

    /**认证提交的内容**/
    private String verifyContent;

    /**-1:取消 1：认证**/
    private Integer verifyOperate;

    /**备注**/
    private String remarks;
    
    /**认证IP**/
    private String clientIp;

    /**创建时间**/
    private Date createTime;

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

    public String getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(String verifyType) {
        this.verifyType = verifyType == null ? null : verifyType.trim();
    }

    public String getVerifyContent() {
        return verifyContent;
    }

    public void setVerifyContent(String verifyContent) {
        this.verifyContent = verifyContent == null ? null : verifyContent.trim();
    }

    public Integer getVerifyOperate() {
        return verifyOperate;
    }

    public void setVerifyOperate(Integer verifyOperate) {
        this.verifyOperate = verifyOperate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
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
				+ ((memberId == null) ? 0 : memberId.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result
				+ ((verifyContent == null) ? 0 : verifyContent.hashCode());
		result = prime * result
				+ ((verifyOperate == null) ? 0 : verifyOperate.hashCode());
		result = prime * result
				+ ((verifyType == null) ? 0 : verifyType.hashCode());
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
		MemberVerify other = (MemberVerify) obj;
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
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (verifyContent == null) {
			if (other.verifyContent != null)
				return false;
		} else if (!verifyContent.equals(other.verifyContent))
			return false;
		if (verifyOperate == null) {
			if (other.verifyOperate != null)
				return false;
		} else if (!verifyOperate.equals(other.verifyOperate))
			return false;
		if (verifyType == null) {
			if (other.verifyType != null)
				return false;
		} else if (!verifyType.equals(other.verifyType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MemberVerify [id=");
		builder.append(id);
		builder.append(", memberId=");
		builder.append(memberId);
		builder.append(", verifyType=");
		builder.append(verifyType);
		builder.append(", verifyContent=");
		builder.append(verifyContent);
		builder.append(", verifyOperate=");
		builder.append(verifyOperate);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append("]");
		return builder.toString();
	}
}