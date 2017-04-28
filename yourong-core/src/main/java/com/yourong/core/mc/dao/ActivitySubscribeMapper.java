package com.yourong.core.mc.dao;

import com.yourong.core.mc.model.ActivitySubscribe;
import org.apache.ibatis.annotations.Param;

/**
 * Created by alan.zheng on 2017/3/13.
 */
public interface ActivitySubscribeMapper {
    ActivitySubscribe querySubscribeByOpenIdAndUniqueStr(@Param("openId") String openId,@Param("uniqueStr") String uniqueStr);

    int insertSubscribe(@Param("subscribe") ActivitySubscribe subscribe);
}
