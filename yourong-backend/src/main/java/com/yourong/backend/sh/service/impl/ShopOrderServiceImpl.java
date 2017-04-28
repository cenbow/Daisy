package com.yourong.backend.sh.service.impl;

import com.yourong.backend.sh.service.ShopOrderService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sh.manager.ShopOrderLogManager;
import com.yourong.core.sh.manager.ShopOrderManager;
import com.yourong.core.sh.model.OrderLog;
import com.yourong.core.sh.model.OrderMain;
import com.yourong.core.sh.model.biz.OrderBiz;
import com.yourong.core.sh.model.biz.OrderRechargeBiz;
import com.yourong.core.sh.model.query.ShopOrderQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by XR on 2016/10/21.
 */
@Service
public class ShopOrderServiceImpl implements ShopOrderService {
    @Autowired
    private ShopOrderManager shopOrderManager;
    @Autowired
    private ShopOrderLogManager shopOrderLogManager;
    @Override
    public Page<OrderBiz> queryPageOrderInfo(ShopOrderQuery query) {
        return shopOrderManager.queryPageOrderInfo(query);
    }

    @Override
    public OrderRechargeBiz queryOrderRechargeInfoByOrderId(Long orderid) {
        return shopOrderManager.queryOrderRechargeInfo(orderid);
    }

    @Override
    public void send(Long orderid, String remark) throws ManagerException {
        shopOrderManager.saveSendRemark(orderid,remark);
    }

    @Override
    public void rechargeOrder(Long orderid) throws ManagerException {
        shopOrderManager.rechargeOrder(orderid);
    }

    @Override
    public void rechargeResult(Long orderid, Integer resultstatus) {
        shopOrderManager.rechargeResult(orderid,resultstatus);
    }

    @Override
    public boolean updateSendRemarkById(Long orderid, String sendremark) {
        return shopOrderManager.updateSendRemarkById(orderid,sendremark);
    }

    @Override
    public boolean updateRemarkById(Long orderid, String remark) {
        return shopOrderManager.updateRemarkById(orderid,remark);
    }

    @Override
    public String queryRemarkById(Long orderid) {
        return shopOrderManager.queryRemarkById(orderid);
    }
}
