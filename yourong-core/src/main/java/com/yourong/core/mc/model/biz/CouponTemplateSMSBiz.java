package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * Created by XR on 2016/9/22.
 */
public class CouponTemplateSMSBiz extends AbstractBaseObject {
    private Long memberid;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 张数
     */
    private Integer num;
    /**
     * 优惠券模板名称
     */
    private String topic;
    
    private BigDecimal amount;
    
    private Integer coupontType;
    

    public Long getMemberid() {
        return memberid;
    }

    public void setMemberid(Long memberid) {
        this.memberid = memberid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the coupontType
	 */
	public Integer getCoupontType() {
		return coupontType;
	}

	/**
	 * @param coupontType the coupontType to set
	 */
	public void setCoupontType(Integer coupontType) {
		this.coupontType = coupontType;
	}
    
}
