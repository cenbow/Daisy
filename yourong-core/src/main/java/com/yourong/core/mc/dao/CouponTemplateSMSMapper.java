package com.yourong.core.mc.dao;

import com.yourong.core.mc.model.CouponTemplateSMS;

import java.util.List;

/**
 * Created by XR on 2016/9/21.
 */
public interface CouponTemplateSMSMapper {
    CouponTemplateSMS queryByTemplateId(Long templateid);

    int insert(CouponTemplateSMS couponTemplateSMS);

    int updateById(CouponTemplateSMS couponTemplateSMS);

    List<CouponTemplateSMS> queryAllValid();
}
