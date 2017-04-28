package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 邀请好友
 * Created by alan.zheng on 2017/3/17.
 */
public class ActivityForInviteFriend extends AbstractBaseObject {
    /**
     * 奖金
     */
    private BigDecimal reward;
    /**
     * 风云榜
     */
    private List<ActivityForInviteFriendList> inviteFriendLists;
    /**
     * 开始日期
     */
    private Date startDate;
    /**
     * 结束日期
     */
    private Date endDate;
    /**
     * 累计投资额
     */
    private BigDecimal invest;
    /**
     * 邀请人数
     */
    private Integer inviteCount;
    /**
     * 比率
     */
    private Integer ratio;
    /**
     * 现金券上限
     */
    private Integer totalReceive;
    /**
     * 邀请现金券id
     */
    private Long referralTemplateId;
    /**
     * 新用户被邀请优惠券
     */
    private Long newTemplateId;
    /**
     * 投资日期
     */
    private Date transactionDate;

    public BigDecimal getReward() {
        return reward;
    }

    public void setReward(BigDecimal reward) {
        this.reward = reward;
    }

    public List<ActivityForInviteFriendList> getInviteFriendLists() {
        return inviteFriendLists;
    }

    public void setInviteFriendLists(List<ActivityForInviteFriendList> inviteFriendLists) {
        this.inviteFriendLists = inviteFriendLists;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getInvest() {
        return invest;
    }

    public void setInvest(BigDecimal invest) {
        this.invest = invest;
    }

    public Integer getInviteCount() {
        return inviteCount;
    }

    public void setInviteCount(Integer inviteCount) {
        this.inviteCount = inviteCount;
    }

    public Integer getRatio() {
        return ratio;
    }

    public void setRatio(Integer ratio) {
        this.ratio = ratio;
    }

    public Integer getTotalReceive() {
        return totalReceive;
    }

    public void setTotalReceive(Integer totalReceive) {
        this.totalReceive = totalReceive;
    }

    public Long getReferralTemplateId() {
        return referralTemplateId;
    }

    public void setReferralTemplateId(Long referralTemplateId) {
        this.referralTemplateId = referralTemplateId;
    }

    public Long getNewTemplateId() {
        return newTemplateId;
    }

    public void setNewTemplateId(Long newTemplateId) {
        this.newTemplateId = newTemplateId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
