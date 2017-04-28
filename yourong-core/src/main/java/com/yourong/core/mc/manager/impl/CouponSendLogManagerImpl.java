package com.yourong.core.mc.manager.impl;

import com.yourong.core.mc.dao.CouponSendLogMapper;
import com.yourong.core.mc.manager.CouponSendLogManager;
import com.yourong.core.mc.model.CouponSendLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/1/11.
 */
@Component
public class CouponSendLogManagerImpl implements CouponSendLogManager {
    @Autowired
    private CouponSendLogMapper couponSendLogMapper;

    @Override
    public List<CouponSendLog> queryPageNoSendList(Long templateId, String sign, Integer startRow, Integer pageSize) {
        return couponSendLogMapper.queryPageNoSendList(templateId,sign,startRow,pageSize);
    }

    @Override
    public int queryCountNoSendList(Long templateId, String sign) {
        return couponSendLogMapper.queryCountNoSendList(templateId,sign);
    }

    @Override
    public int querySendCount(Long templateId, String sign) {
        return couponSendLogMapper.querySendCount(templateId,sign);
    }

    @Override
    public boolean insertCouponSendLog(CouponSendLog couponSendLog) {
        if (couponSendLogMapper.insertCouponSendLog(couponSendLog)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSendStatusById(Long id, Integer sendStatus, Date updateTime) {
        if (couponSendLogMapper.updateSendStatusById(id,sendStatus,updateTime)>0){
            return true;
        }
        return false;
    }
}
