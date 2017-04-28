package com.yourong.core.uc.model.biz;

import com.yourong.core.uc.model.Member;

/**
 * Created by XR on 2016/10/24.
 */
public class MemberBiz extends Member {
    /**
     * 等级
     */
    private Integer vipLevel;
    /**
     * 分数
     */
    private Integer score;

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
