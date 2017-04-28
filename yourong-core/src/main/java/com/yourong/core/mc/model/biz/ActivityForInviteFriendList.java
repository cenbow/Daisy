package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.StringUtil;

import java.math.BigDecimal;

/**
 * Created by alan.zheng on 2017/3/17.
 */
public class ActivityForInviteFriendList extends AbstractBaseObject {
    /**
     * 用户名
     */
    private String username;
    /**
     * 手机号
     */
    private Long mobile;
    /**
     * 用户头像
     */
    private String avatars;
    /**
     * 邀请人数
     */
    private Integer referralCount;
    /**
     * 好友累计投资额
     */
    private BigDecimal invest;

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

    public String getAvatars() {
        return avatars;
    }

    public void setAvatars(String avatars) {
        this.avatars = avatars;
    }

    public Integer getReferralCount() {
        return referralCount;
    }

    public void setReferralCount(Integer referralCount) {
        this.referralCount = referralCount;
    }

    public BigDecimal getInvest() {
        return invest;
    }

    public void setInvest(BigDecimal invest) {
        this.invest = invest;
    }
}
