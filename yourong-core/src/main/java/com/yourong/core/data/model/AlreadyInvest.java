package com.yourong.core.data.model;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * Created by XR on 2016/11/28.
 */
public class AlreadyInvest extends AbstractBaseObject {
    private String cName;
    private String mobile;
    private String registerTime;
    private String msg;
    private String invest;
    private String belong;

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

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getInvest() {
        return invest;
    }

    public void setInvest(String invest) {
        this.invest = invest;
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }
}
