package com.yourong.core.mc.manager;

import com.yourong.core.mc.model.CouponSendLog;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/1/11.
 */
public interface CouponSendLogManager {
    List<CouponSendLog> queryPageNoSendList(Long templateId, String sign,Integer startRow,Integer pageSize);

    int queryCountNoSendList(Long templateId,String sign);

    int querySendCount(Long templateId, String sign);

    boolean insertCouponSendLog(CouponSendLog couponSendLog);

    boolean updateSendStatusById(Long id,Integer sendStatus, Date updateTime);
}
