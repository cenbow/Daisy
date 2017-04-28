package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.OrderForMember;
import com.yourong.core.tc.model.query.OrderQuery;


public interface OrderService {

	/**
	 * 保存订单
	 * @param order
	 * @return
	 */
	public ResultDO<Order> saveOrder(Order order) throws Exception;

	/**
	 * 通过订单编号查询订单信息
	 * @param orderNo
	 * @return
	 */
	public Order getOrderByOrderNo(String orderNo);

	/**
	 * 用户中心查询订单分页列表
	 * @param pageRequest
	 * @param orderQuery
	 * @return
	 */
	public Page<OrderForMember> selectAllOrderForMember(OrderQuery orderQuery);
	
	/**
	 * 获取当前用户 还未支付的订单
	 * @param memberId
	 * @return
	 */
	public int  getNoPayOrdeCount(Long memberId);

	/**
	 * 取消订单
	 * @param orderId
	 * @param memberId 
	 * @return
	 */
	public ResultDO<Order> cancelOrder(Long orderId, String remarks, Long memberId);

	/**
	 * 获取订单详情
	 * @param orderId
	 * @param memberId 
	 * @return
	 */
	public ResultDO<OrderForMember> getOrderForMember(Long orderId, Long memberId);

	/**
	 * 支付交易时如果需要充值，则先调用次接口更新订单信息
	 * @param order
	 * @return
	 */
	public ResultDO<Order> updateOrderForRecharge(Order order) throws Exception;
	
	/**
	 * 检查订单现金券是否用
	 * @param order
	 * @return
	 */
	public ResultDO<Order> checkOrderCashCouponNo(Order order, boolean checkStatus);
	
	/**
	 * 检查订单收益券是否可用
	 * @param order
	 * @return
	 */
	public ResultDO<Order> checkOrderProfitCouponNo(Order order, boolean checkStatus);

	/**
	 * 
	 * @Description:支付订单详情页
	 * @param orderNo
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2016年7月25日 下午5:42:05
	 */
	public ResultDO<Object> getPayOrderDetail(String orderNo, Long memberId);
}
