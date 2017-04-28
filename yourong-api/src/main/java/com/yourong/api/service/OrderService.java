package com.yourong.api.service;

import java.math.BigDecimal;

import com.yourong.api.dto.OrderDetailForMember;
import com.yourong.api.dto.OrderForAppDto;
import com.yourong.api.dto.OrderForMemberDto;
import com.yourong.api.dto.PayOrderDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.query.OrderQuery;


public interface OrderService {
	
	/**
	 * 把项目加入购物车
	 * @param projectId
	 * @param investAmount
	 * @param memberId
	 * @return
	 */
	public ResultDTO investment(Long projectId, BigDecimal investAmount, Long memberId);

	/**
	 * 保存订单
	 * @param order
	 * @return
	 */
	public ResultDTO<Object> saveOrder(Order order) throws Exception;
	
	/**
	 * 保存订单
	 * @param order
	 * @return
	 */
	public ResultDTO<Object> saveOrderSina(Order order) throws Exception;
	
	/**
	 * 取消订单
	 * @param orderId
	 * @param memberId 
	 * @return
	 */
	public ResultDTO<Order> cancelOrder(Long orderId, String remarks, Long memberId);
	
	/**
	 * 获取订单详情
	 * @param orderId
	 * @param memberId 
	 * @return
	 */
	public ResultDTO<OrderDetailForMember> getOrderDetailForMember(Long orderId, Long memberId);
	
	/**
	 * 查询用户不含取消的订单
	 * @param orderQuery
	 * @return
	 */
	public Page<OrderForMemberDto> queryOrderyForMember(OrderQuery orderQuery);
	
	/**
	 * 查询用户不含取消的订单 过滤p2p
	 * @param orderQuery
	 * @return
	 */
	public Page<OrderForMemberDto> queryOrderyForMemberP2p(OrderQuery orderQuery);
	
	
	
	/**
	 * 通过订单编号查询订单信息
	 * @param orderNo
	 * @return
	 */
	public Order getOrderByOrderNo(String orderNo);
	
	/**
	 * 支付交易时如果需要充值，则先调用次接口更新订单信息
	 * @param order
	 * @return
	 */
	public ResultDO<Order> updateOrderForRecharge(Order order) throws Exception;
	
	/**
	 * 获取当前用户 还未支付的订单
	 * @param memberId
	 * @return
	 */
	public int  getNoPayOrdeCount(Long memberId);
	
	/**
	 * 订单支付信息
	 * @param orderId
	 * @param cashCouponNo
	 * @param memberId
	 * @return
	 */
	public ResultDTO<PayOrderDto> queryPayOrderInfo(Long orderId, String cashCouponNo, Long memberId);
	
	/**
	 * 订单支付信息-收银台改版
	 * @param orderId
	 * @param cashCouponNo
	 * @param memberId
	 * @return
	 */
	public ResultDTO<OrderForAppDto> queryPayOrderInfoSinaBank(Long orderId,Long memberId);
	
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
	 * @Description:未支付订单 过滤p2p
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月8日 下午1:09:25
	 */
	public int getNoPayOrdeCountFilterP2p(Long memberId);
	
	/**
	 * 
	 * @Description:投资完成后的临时页
	 * @param orderId
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月7日 下午4:55:23
	 */
	public Object tempPageAfterInvest(Long orderId, Long memberId);
	
	
	/**
	 * 
	 * @Description:投资完成后，抽奖
	 * @param transactionId
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月7日 下午4:55:23
	 */
	public Object directLottery(Long memberId,Long transactionId,int type);
}
