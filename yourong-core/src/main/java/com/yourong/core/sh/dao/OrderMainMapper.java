package com.yourong.core.sh.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.sh.model.OrderMain;
import com.yourong.core.sh.model.biz.OrderBiz;
import com.yourong.core.sh.model.biz.OrderRechargeBiz;
import com.yourong.core.sh.model.query.ShopOrderForAppQuery;
import com.yourong.core.sh.model.query.ShopOrderQuery;

/**
 * Created by XR on 2016/10/19.
 */
public interface OrderMainMapper {
    List<OrderBiz> queryPageOrderInfo(@Param("query")ShopOrderQuery query);
    
    

    int queryPageCountOrderInfo(@Param("query")ShopOrderQuery query);

    int saveSend(@Param("orderid")Long orderid, @Param("sendremark")String sendremark, @Param("sendtime")Date sendtime);

    int updateSendRemarkById(@Param("orderid")Long orderid, @Param("sendremark")String sendremark);

    int updateRemarkById(@Param("orderid")Long orderid, @Param("remark")String remark);

    int rechargeOrder(@Param("orderid")Long orderid,@Param("updatetime")Date updatetime);

    int rechargeResult(@Param("orderid")Long orderid,@Param("resultstatus")Integer resultstatus,@Param("updatetime")Date updatetime);

    int insertOrderMain(OrderMain orderMain);
    
    List<OrderMain> getOrderMainList(@Param("memberId")Long memberId);
    
    List<OrderMain> getOrderMainListByQuery(@Param("query")ShopOrderForAppQuery query);
    
    int getOrderMainListCountByQuery(@Param("query")ShopOrderForAppQuery query);    
    
    OrderMain selectByPrimaryKey(@Param("id")Long id);

    OrderRechargeBiz queryOrderRechargeInfo(Long orderid);

    String queryRemarkById(@Param("id")Long id);
}
