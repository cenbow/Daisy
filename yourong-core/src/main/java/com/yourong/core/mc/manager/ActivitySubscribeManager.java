package com.yourong.core.mc.manager;

import com.yourong.core.mc.model.ActivitySubscribe;

/**
 * Created by alan.zheng on 2017/3/13.
 */
public interface ActivitySubscribeManager {
    ActivitySubscribe querySubscribeByOpenIdAndUniqueStr(String openId,String uniqueStr);

    boolean insertSubscribe(ActivitySubscribe subscribe);
}
