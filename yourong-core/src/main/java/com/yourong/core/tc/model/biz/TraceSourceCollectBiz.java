package com.yourong.core.tc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by XR on 2016/9/12.
 */
public class TraceSourceCollectBiz extends AbstractBaseObject {
    private Long memberid;
    private String mobile;
    private Date registertime;
    private BigDecimal investcollect;
    private Date lastinvest;

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

    public Date getRegistertime() {
        return registertime;
    }

    public void setRegistertime(Date registertime) {
        this.registertime = registertime;
    }

    public BigDecimal getInvestcollect() {
        return investcollect;
    }

    public void setInvestcollect(BigDecimal investcollect) {
        this.investcollect = investcollect;
    }

    public Date getLastinvest() {
        return lastinvest;
    }

    public void setLastinvest(Date lastinvest) {
        this.lastinvest = lastinvest;
    }
}
