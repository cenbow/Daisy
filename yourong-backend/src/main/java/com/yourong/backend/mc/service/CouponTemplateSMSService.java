package com.yourong.backend.mc.service;

import com.yourong.core.mc.model.CouponTemplateSMS;

/**
 * Created by XR on 2016/9/21.
 */
public interface CouponTemplateSMSService {
    public CouponTemplateSMS queryByTemplateId(Long id);

    public boolean saveCouponTemplateSMS(CouponTemplateSMS couponTemplateSMS);
}
