package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;

/**
 * Created by alan.zheng on 2017/2/6.
 */
public class ActivityForLanternFestivalAndValentine extends AbstractBaseObject {
    /**
     * 送高额优惠券时间
     */
    private String valentineDate;
    /**
     * 高额优惠券模板id
     */
    private String valentineTemplateIds;

    public String getValentineDate() {
        return valentineDate;
    }

    public void setValentineDate(String valentineDate) {
        this.valentineDate = valentineDate;
    }

    public String getValentineTemplateIds() {
        return valentineTemplateIds;
    }

    public void setValentineTemplateIds(String valentineTemplateIds) {
        this.valentineTemplateIds = valentineTemplateIds;
    }
}
