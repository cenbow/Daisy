package com.yourong.core.data.model;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * Created by XR on 2016/11/28.
 */
public class NoInvest extends AbstractBaseObject {
    private String registerTime;
    private String cName;
    private String mobile;
    private String registerTraceSource;
    private String noInvest;
    private String belong;

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRegisterTraceSource() {
        return registerTraceSource;
    }

    public void setRegisterTraceSource(String registerTraceSource) {
        this.registerTraceSource = registerTraceSource;
    }

    public String getNoInvest() {
        return noInvest;
    }

    public void setNoInvest(String noInvest) {
        this.noInvest = noInvest;
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }
}
