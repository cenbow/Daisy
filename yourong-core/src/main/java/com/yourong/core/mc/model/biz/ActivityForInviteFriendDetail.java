package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.StringUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by alan.zheng on 2017/3/21.
 */
public class ActivityForInviteFriendDetail extends AbstractBaseObject {
    /**
     * 现金券和
     */
    private BigDecimal coupon;
    /**
     * 人气值和
     */
    private BigDecimal popularity;
    /**
     * 用户名
     */
    private String username;
    /**
     * 被推荐人手机号
     */
    private Long mobile;
    /**
     * 注册时间
     */
    private Date registerTime;

    public BigDecimal getCoupon() {
        return coupon;
    }

    public void setCoupon(BigDecimal coupon) {
        this.coupon = coupon;
    }

    public BigDecimal getPopularity() {
        return popularity;
    }

    public void setPopularity(BigDecimal popularity) {
        this.popularity = popularity;
    }

    public String getUsername() {
        if(username!=null&&username.contains("*")){
            return username;
        }
        if(mobile!=null&&mobile.toString().contains("*")){
            return mobile.toString();
        }
        return StringUtil.maskUserNameOrMobile(username, mobile);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getMobile() {
        return null;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }
}
