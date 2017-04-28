package com.yourong.backend.tc.service;

import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.OrderForMember;

public interface OrderService {

	public void schedueCloseOrder();

	/**
	 * 订单查询
	 * 
	 * @author wangyanji
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<OrderForMember> findByPage(Page<OrderForMember> pageRequest, Map<String, Object> map);
	
	/**
	 * 取消订单
	 * 
	 * @author wangyanji
	 * @param orderId
	 * @return
	 */
	public ResultDO<Order> cancelOrder(Long orderId);
	
	/**
	 * 刷新单条订单
	 * 
	 * @author wangyanji
	 * @param orderId
	 * @return
	 */
	public ResultDO<Order> reflashOrder(Long orderId);
	
	/**
     * 查询投资人信息
     * 
     * @author wangyanji
     * @param orderNo
     * @return
     */
	ResultDO<Map> selectTransPersonByOrderNo(String orderNo);
	
	/**
	 * 自动投标
	 * @author luwenshan
	 * @time 2016/08/16
	 * 
	 */
	public void schedueAutoIntest();
	
	/**
	 * 关闭直投待确认订单
	 * 
	 * @author wangyanji
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public ResultDO<Order> closeDirectProjectOrder(Long orderId) throws Exception;

	/**
	 * 根据订单id查询退款状态
	 * @param orderid
	 * @return
     */
	public String queryStatusByOrderId(String orderid);
	
}
