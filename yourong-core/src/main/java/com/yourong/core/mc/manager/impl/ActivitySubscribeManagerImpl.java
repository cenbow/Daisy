package com.yourong.core.mc.manager.impl;

import com.yourong.core.mc.dao.ActivitySubscribeMapper;
import com.yourong.core.mc.manager.ActivitySubscribeManager;
import com.yourong.core.mc.model.ActivitySubscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alan.zheng on 2017/3/13.
 */
@Component
public class ActivitySubscribeManagerImpl implements ActivitySubscribeManager {
    @Autowired
    private ActivitySubscribeMapper subscribeMapper;

    @Override
    public ActivitySubscribe querySubscribeByOpenIdAndUniqueStr(String openId,String uniqueStr) {
        return subscribeMapper.querySubscribeByOpenIdAndUniqueStr(openId,uniqueStr);
    }

    @Override
    public boolean insertSubscribe(ActivitySubscribe subscribe) {
        if (subscribeMapper.insertSubscribe(subscribe)>0){
            return true;
        }
        return false;
    }
}
