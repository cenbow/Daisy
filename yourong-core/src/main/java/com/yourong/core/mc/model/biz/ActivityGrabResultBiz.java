package com.yourong.core.mc.model.biz;

import com.yourong.common.util.StringUtil;

import java.io.Serializable;

/**
 * 新年活动中奖纪录
 * Created by alan.zheng on 2017/1/22.
 */
public class ActivityGrabResultBiz implements Serializable {
    /**
     * 手机
     */
    private Long mobile;
    /**
     * 格式化手机
     */
    private String mobileStr;
    /**
     * 个人头像
     */
    private String avatars;
    /**
     * 中奖信息
     */
    private String rewardInfo;

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    public String getMobileStr() {
        return StringUtil.maskUserNameOrMobile("",mobile);
    }

    public void setMobileStr(String mobileStr) {
        this.mobileStr = mobileStr;
    }

    public String getAvatars() {
        return avatars;
    }

    public void setAvatars(String avatars) {
        this.avatars = avatars;
    }

    public String getRewardInfo() {
        return rewardInfo;
    }

    public void setRewardInfo(String rewardInfo) {
        this.rewardInfo = rewardInfo;
    }
}
