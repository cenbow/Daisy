package com.yourong.core.tc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.tc.model.HostingRefund;
public interface HostingRefundMapper {
    int deleteByPrimaryKey(Long id);

    int insert(HostingRefund record);

    int insertSelective(HostingRefund record);

    HostingRefund selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HostingRefund record);

    int updateByPrimaryKey(HostingRefund record);
    
    HostingRefund selectByTradeNo(@Param("tradeNo")String tradeNo);
    
    public HostingRefund getByIdForLock(@Param("id") Long id);
    
    public int findHostingRefundCountByStatusAndProjectId(@Param("projectId")Long projectId,@Param("refundStatus")String refundStatus);
    
    public List<HostingRefund> findRefundByCollectNoForProjectLose(@Param("collectTradeNo")String collectTradeNo);

    String queryStatusByOrderId(String orderid);
}