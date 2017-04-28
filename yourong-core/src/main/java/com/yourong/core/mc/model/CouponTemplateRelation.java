package com.yourong.core.mc.model;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;

/**
 * Created by XR on 2016/8/19.
 */
public class CouponTemplateRelation extends AbstractBaseObject {
    private Long id;
    private String code;
    private String coupon_template_ids;
    private String remark;
    private Date createtime;
    private Date updatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCoupon_template_ids() {
        return coupon_template_ids;
    }

    public void setCoupon_template_ids(String coupon_template_ids) {
        this.coupon_template_ids = coupon_template_ids;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}
