package com.yourong.web.dto;

public class MemberNotifySettingsDto {
	
	/****/
    private Long id;

    /**用户id**/
    private Long memberId;

    /**通知类型 0-充值操作 ；1-提现操作 2-奖励兑付 4-投资成功 5-还本付息**/
    private Integer notifyType;

    /**通知方式 1-系统通知 2-短信通知  3-邮件通知**/
    private Integer notifyWay;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append("notifyType=");
		builder.append(notifyType);
		builder.append(", notifyWay=");
		builder.append(notifyWay);
		builder.append("]");
		return builder.toString();
	}
    
    
}
