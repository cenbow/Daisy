package com.yourong.core.mc.dao;

import com.yourong.core.mc.model.CouponSendLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/1/11.
 */
public interface CouponSendLogMapper {
    List<CouponSendLog> queryPageNoSendList(@Param("templateId")Long templateId,@Param("sign")String sign,@Param("startRow")Integer startRow,@Param("pageSize")Integer pageSize);

    int queryCountNoSendList(@Param("templateId")Long templateId,@Param("sign")String sign);

    int querySendCount(@Param("templateId")Long templateId,@Param("sign")String sign);

    int insertCouponSendLog(CouponSendLog couponSendLog);

    int updateSendStatusById(@Param("id")Long id,@Param("sendStatus")Integer sendStatus, @Param("updateTime")Date updateTime);
}
