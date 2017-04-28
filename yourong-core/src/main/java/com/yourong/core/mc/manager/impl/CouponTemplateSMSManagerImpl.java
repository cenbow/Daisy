package com.yourong.core.mc.manager.impl;

import com.yourong.core.mc.dao.CouponTemplateSMSMapper;
import com.yourong.core.mc.manager.CouponTemplateSMSManager;
import com.yourong.core.mc.model.CouponTemplateSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by XR on 2016/9/21.
 */
@Component
public class CouponTemplateSMSManagerImpl implements CouponTemplateSMSManager {
    @Autowired
    private CouponTemplateSMSMapper couponTemplateSMSMapper;
    @Override
    public CouponTemplateSMS queryByTemplateId(Long id) {
        return couponTemplateSMSMapper.queryByTemplateId(id);
    }

    @Override
    public boolean saveCouponTemplateSMS(CouponTemplateSMS couponTemplateSMS) {
        if (couponTemplateSMS.getTemplateId()!=null&&couponTemplateSMS.getTemplateId()>0){  //更新
            if (couponTemplateSMSMapper.updateById(couponTemplateSMS)>0){
                return true;
            }
        }
        if (couponTemplateSMSMapper.insert(couponTemplateSMS)>0){
            return true;
        }
        return false;
    }

    @Override
    public List<CouponTemplateSMS> queryAllValid() {
        return couponTemplateSMSMapper.queryAllValid();
    }
}
