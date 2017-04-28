package com.yourong.backend.mc.service.impl;

import com.yourong.backend.mc.service.CouponTemplateSMSService;
import com.yourong.core.mc.manager.CouponTemplateSMSManager;
import com.yourong.core.mc.model.CouponTemplateSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by XR on 2016/9/21.
 */
@Service
public class CouponTemplateSMSServiceImpl implements CouponTemplateSMSService {
    @Autowired
    private CouponTemplateSMSManager couponTemplateSMSManager;
    @Override
    public CouponTemplateSMS queryByTemplateId(Long id) {
        return couponTemplateSMSManager.queryByTemplateId(id);
    }

    @Override
    public boolean saveCouponTemplateSMS(CouponTemplateSMS couponTemplateSMS) {
        return couponTemplateSMSManager.saveCouponTemplateSMS(couponTemplateSMS);
    }
}
