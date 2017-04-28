package com.yourong.core.sh.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.sh.model.OrderSub;

/**
 * Created by XR on 2016/10/19.
 */
public interface OrderSubMapper {
    int insertOrderSub(OrderSub orderSub);
    
    List<OrderSub> queryOrderSubListByOrderMainId(@Param("orderId")Long orderId);
}
