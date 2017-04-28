package com.yourong.core.sh.manager;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sh.model.OrderDelivery;
import com.yourong.core.sh.model.OrderForCreat;
import com.yourong.core.sh.model.OrderMain;
import com.yourong.core.sh.model.OrderSub;
import com.yourong.core.sh.model.biz.OrderBiz;
import com.yourong.core.sh.model.biz.OrderRechargeBiz;
import com.yourong.core.sh.model.query.ShopOrderForAppQuery;
import com.yourong.core.sh.model.query.ShopOrderQuery;

/**
 * Created by XR on 2016/10/21.
 */
public interface ShopOrderManager {
    Page<OrderBiz> queryPageOrderInfo(ShopOrderQuery query);

    void saveSendRemark(Long orderid,String sendremark);

    void rechargeOrder(Long orderid);

    void rechargeResult(Long orderid,Integer resultstatus);

    public Object creatGoodsOrder(OrderForCreat orderForCreat)throws Exception;

    public Page<OrderMain> getOrderMainPage(ShopOrderForAppQuery query);

    public List<OrderMain> getOrderMainList(Long memberId) throws ManagerException;


    public List<OrderSub> queryOrderSubListByOrderMainId(Long orderMainId) throws ManagerException;

    public OrderMain selectByPrimaryKey(Long orderMainId) throws ManagerException;


    OrderRechargeBiz queryOrderRechargeInfo(Long orderid);

    boolean updateSendRemarkById(Long orderid, String sendremark);

    boolean updateRemarkById(Long orderid, String remark);

    String queryRemarkById(Long orderid);
    
    int creatOrderDelivery(OrderDelivery orderDelivery) throws ManagerException;
    
    OrderDelivery queryOrderDelivery(Long memberId);
    
    int updateOrderDelivery(OrderDelivery orderDelivery) throws ManagerException;
}
