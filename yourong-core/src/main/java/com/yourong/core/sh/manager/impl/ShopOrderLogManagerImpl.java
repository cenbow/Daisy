package com.yourong.core.sh.manager.impl;

import com.yourong.core.sh.dao.OrderLogMapper;
import com.yourong.core.sh.manager.ShopOrderLogManager;
import com.yourong.core.sh.model.OrderLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by XR on 2016/10/25.
 */
@Component
public class ShopOrderLogManagerImpl implements ShopOrderLogManager {
    @Autowired
    private OrderLogMapper orderLogMapper;
    @Override
    public boolean inserOrderLog(OrderLog orderLog) {
        if (orderLogMapper.insertOrderLog(orderLog)>0){
            return true;
        }
        return false;
    }
}
