package com.yourong.api.service;

import com.yourong.core.mc.model.ActivitySubscribe;

/**
 * Created by alan.zheng on 2017/3/13.
 */
public interface ActivitySubscribeService {
    ActivitySubscribe querySubscribeByOpenIdAndUniqueStr(String openId,String uniqueStr);

    boolean insertSubscribe(ActivitySubscribe subscribe);
}
