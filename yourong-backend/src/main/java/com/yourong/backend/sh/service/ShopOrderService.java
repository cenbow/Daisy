package com.yourong.backend.sh.service;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sh.model.OrderMain;
import com.yourong.core.sh.model.biz.OrderBiz;
import com.yourong.core.sh.model.biz.OrderRechargeBiz;
import com.yourong.core.sh.model.query.ShopOrderQuery;

/**
 * Created by XR on 2016/10/21.
 */
public interface ShopOrderService {
    Page<OrderBiz> queryPageOrderInfo(ShopOrderQuery query);

    OrderRechargeBiz queryOrderRechargeInfoByOrderId(Long orderid);

    void send(Long orderid,String remark) throws ManagerException;

    void rechargeOrder(Long orderid) throws ManagerException;

    void rechargeResult(Long orderid,Integer resultstatus);

    boolean updateSendRemarkById(Long orderid, String sendremark);

    boolean updateRemarkById(Long orderid, String remark);

    String queryRemarkById(Long orderid);
}
