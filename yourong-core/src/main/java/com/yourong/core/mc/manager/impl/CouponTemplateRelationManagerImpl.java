package com.yourong.core.mc.manager.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;
import rop.thirdparty.com.google.common.collect.Maps;

import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.CouponTemplateRelationEnum;
import com.yourong.common.util.StringUtil;
import com.yourong.core.mc.dao.CouponTemplateRelationMapper;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.manager.CouponTemplateRelationManager;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.biz.ActivityLotteryResultBiz;

/**
 * Created by XR on 2016/8/19.
 */
@Component
public class CouponTemplateRelationManagerImpl implements CouponTemplateRelationManager {

    @Autowired
    private CouponTemplateRelationMapper couponTemplateRelationMapper;
    
    @Autowired
    private CouponTemplateManager couponTemplateManager;
    
	private Logger logger = LoggerFactory.getLogger(CouponTemplateRelationManagerImpl.class);
    
    @Override
    public String selectByCode(String code) {
        return couponTemplateRelationMapper.selectByCode(code);
    }
    
    
    
    @Override
    public List<CouponTemplate> getDirectReward(){
    	List<CouponTemplate> templateAmount = Lists.newArrayList();
    	try{
    		String key = RedisConstant.DIRECT_LOTTERY_KEY_ACTIVITY_COUPON+1;
    		boolean isExit = RedisManager.isExitByObjectKey(key);
    		
    		if (isExit) {
    			templateAmount =(List<CouponTemplate>) RedisManager.getObject(key);
    		} else {
    			String idstr= this.selectByCode(CouponTemplateRelationEnum.COUPON_TEMPLATE_RELATION_ENUM_DIRECT_REWARD.getCode());
        		if (StringUtil.isBlank(idstr)){
        			return null;
        		}
        		List<Long> templateIds=Lists.newArrayList();
        		String[] ids= idstr.split(",");
        		for (String id:ids) {
        			if (StringUtil.isNotBlank(id)){
        				templateIds.add(Long.parseLong(id));
        			}
        		}
        		for(Long id:templateIds){
        			CouponTemplate couponT = couponTemplateManager.selectByPrimaryKey(id);
        			templateAmount.add(couponT);
        		}
        		RedisManager.putObject(key, templateAmount);
    		}
    	} catch(Exception e){
    		logger.error("获取直投抽奖优惠券模板信息异常",e);
    	}
		return templateAmount;
    }
    
    
}
