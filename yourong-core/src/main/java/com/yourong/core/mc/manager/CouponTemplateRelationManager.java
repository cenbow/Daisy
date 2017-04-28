package com.yourong.core.mc.manager;

import java.util.List;
import java.util.Map;

import com.yourong.core.mc.model.CouponTemplate;

/**
 * Created by XR on 2016/8/19.
 */
public interface CouponTemplateRelationManager {
    String selectByCode(String code);
    
    public List<CouponTemplate> getDirectReward();
    
}
