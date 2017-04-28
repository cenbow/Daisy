package com.yourong.core.mc.manager;

import com.yourong.core.mc.model.CouponTemplateSMS;

import java.util.List;

/**
 * Created by XR on 2016/9/21.
 */
public interface CouponTemplateSMSManager {
    CouponTemplateSMS queryByTemplateId(Long id);

    boolean saveCouponTemplateSMS(CouponTemplateSMS couponTemplateSMS);

    List<CouponTemplateSMS> queryAllValid();
}
