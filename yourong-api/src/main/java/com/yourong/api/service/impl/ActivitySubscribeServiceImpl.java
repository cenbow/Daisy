package com.yourong.api.service.impl;

import com.yourong.api.service.ActivitySubscribeService;
import com.yourong.common.domain.ResultDO;
import com.yourong.core.mc.manager.ActivitySubscribeManager;
import com.yourong.core.mc.model.ActivitySubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by alan.zheng on 2017/3/13.
 */
@Service
public class ActivitySubscribeServiceImpl implements ActivitySubscribeService {
    private static final Logger logger = LoggerFactory.getLogger(ActivitySubscribeServiceImpl.class);
    @Autowired
    private ActivitySubscribeManager activitySubscribeManager;

    @Override
    public ActivitySubscribe querySubscribeByOpenIdAndUniqueStr(String openId,String uniqueStr) {
        try {
            return activitySubscribeManager.querySubscribeByOpenIdAndUniqueStr(openId,uniqueStr);
        } catch (Exception e) {
            logger.error("查询订阅号关注统计表异常openid:"+openId+"",e);
            return null;
        }
    }

    @Override
    public boolean insertSubscribe(ActivitySubscribe subscribe) {
        try {
            return activitySubscribeManager.insertSubscribe(subscribe);
        } catch (Exception e) {
            return false;
        }
    }
}
