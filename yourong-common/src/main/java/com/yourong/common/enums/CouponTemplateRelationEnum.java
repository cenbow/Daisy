package com.yourong.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by XR on 2016/8/19.
 * 优惠券模板关系
 */
public enum CouponTemplateRelationEnum {

    COUPON_TEMPLATE_RELATION_ENUM_CASH("001","人气值现金券"),

    COUPON_TEMPLATE_RELATION_ENUM_BENEFIT("002","人气值优惠券"),
    
    
    COUPON_TEMPLATE_RELATION_ENUM_DIRECT_REWARD("003","直投抽奖兑换优惠券");    
    


    private static Map<String, CouponEnum> enumMap;

    private String code;

    private String desc;

    CouponTemplateRelationEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public static CouponEnum getEnumByCode(String code) {
        return getMap().get(code);
    }

    public static Map<String, CouponEnum> getMap() {
        if (enumMap == null) {
            enumMap = new HashMap<String, CouponEnum>();
            for (CouponEnum value : CouponEnum.values()) {
                enumMap.put(value.getCode(), value);
            }
        }
        return enumMap;
    }
}
