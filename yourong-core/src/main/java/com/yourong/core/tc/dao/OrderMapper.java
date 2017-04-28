package com.yourong.core.tc.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.OrderForMember;
import com.yourong.core.tc.model.query.OrderQuery;

public interface OrderMapper {

	/**
	 * 插入订单
	 * @param record
	 * @return
	 */
    int insert(Order record);

    /**
     * 通过id查询订单
     * @param id
     * @return
     */
    Order selectByPrimaryKey(Long id);

    /**
     * 更新订单
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     * 获取当日订单总数
     * @param param
     * @return
     */
	int getOrderCountCurrentDay(Map<String, Object> param);

	/**
	 * 通过代收交易号查询订单
	 * @param tradeNo
	 * @return
	 */
	Order getOrderByTradeNo(String tradeNo);

	/**
	 * 通过订单号查询订单
	 * @param orderNo
	 * @return
	 */
	Order getOrderByOrderNo(String orderNo);

	/**
	 * 定时关闭订单
	 * @param orderId 
	 * @param beforeStatus
	 * @param afterStatus
	 * @return
	 */
	int schedueCloseOrder(@Param("orderId")Long orderId, @Param("beforeStatus") int beforeStatus, @Param("afterStatus")int afterStatus);

	/**
	 * 通过条件查询订单记录总数
	 * @param orderQuery
	 * @return
	 */
	int selectOrdersByQueryTotalCount(OrderQuery orderQuery);

	/**
	 * 通过条件查询订单记录
	 * @param orderQuery
	 * @return
	 */
	List<Order> selectOrdersByQuery(OrderQuery orderQuery);

	/**
	 * 通过条件查询前台会员中心订单记录
	 * @param orderQuery
	 * @return
	 */
	List<OrderForMember> selectAllOrderForMember(OrderQuery orderQuery);
	
	/**
	 * 通过条件查询前台会员中心订单记录，剔除P2P数据
	 * @param orderQuery
	 * @return
	 */
	List<OrderForMember> p2pSelectAllOrderForMember(OrderQuery orderQuery);

	/**
	 * 通过条件查询前台会员中心订单记录总数,剔除P2P数据
	 * @param orderQuery
	 * @return
	 */
	long p2pSelectAllOrderForMemberCount(OrderQuery orderQuery);
	
	/**
	 * 通过条件查询前台会员中心订单记录总数
	 * @param orderQuery
	 * @return
	 */
	long selectAllOrderForMemberCount(OrderQuery orderQuery);

	/**
	 * 获取当前用户 还未支付的订单
	 * @param memberId
	 * @param status
	 * @return
	 */
	int getNoPayOrdeCount(@Param("memberId") Long memberId);

	/**
	 * 取消订单
	 * @param orderId
	 * @param beforeStatus
	 * @param afterStatus
	 * @param remarks
	 * @return
	 */
	int cancelOrder(
			@Param("orderId") Long orderId, 
			@Param("beforeStatus") int beforeStatus, 
			@Param("afterStatus") int afterStatus, 
			@Param("remarks") String remarks);

	/**
	 * 通过订单id获取会员中心订单信息
	 * @param orderId
	 * @param memberId 
	 * @return
	 */
	OrderForMember getOrderForMemberByOrderId(@Param("orderId")Long orderId, @Param("memberId")Long memberId);

	/**
	 * 查询需要定时关闭的订单
	 * @return
	 */
	List<Order> selectSchedueCloseOrder(@Param("status")Integer status);

	/**
	 * 通过id查询订单（加锁）
	 * @param id
	 * @return
	 */
	Order getOrderByIdForLock(Long id);

	/**
	 * 通过项目id和状态查询订单记录
	 * @param projectId
	 * @param status
	 * @return
	 */
	int getOrderCountByProject(@Param("projectId")Long projectId, @Param("status")Integer status);
	
	/**
	 * 通过项目id和状态查询订单记录
	 * @param projectId
	 * @param status
	 * @return
	 */
	int getTransferOrderCountByProject(@Param("transferId")Long transferId, @Param("status")Integer status);

	/**
	 * 通过项目id和订单状态查询总金额
	 * @param projectId
	 * @param status
	 * @return
	 */
	BigDecimal getOrderSumAmountByProject(@Param("projectId")Long projectId, @Param("status")Integer status);
	
	/**
	 * 通过项目id和订单状态查询总金额
	 * @param projectId
	 * @param status
	 * @return
	 */
	BigDecimal getTransferPayingAmountByProject(@Param("transferId")Long transferId, @Param("status")Integer status);
	
	/**
     * 订单分页查询
     * @param map
     * @return
     * @throws ManagerException
     */
	List<OrderForMember> findByPage(@Param("map") Map<String, Object> map);
	
	/**
     * 订单分页查询数量
     * @param map
     * @return
     * @throws ManagerException
     */
	int findByPageCount(@Param("map") Map<String, Object> map);
	
	/**
     * 查询投资人信息
     * 
     * @param orderNo
     * @return
     */
	Map selectTransPersonByOrderNo(String orderNo);
	
	
	/**
     * 根据优惠券查询订单
     * 
     * @param orderNo
     * @return
     */
	List<Order> selectOrderByCoupon(String couponCode);
	/**
	 * 
	 * @Description:未支付订单 过滤p2p
	 * @param memberId
	 * @param status
	 * @return
	 * @author: chaisen
	 * @time:2016年3月8日 下午1:15:31
	 */
	int getNoPayOrdeCountFilterP2p(@Param("memberId") Long memberId);
	
	
	/**
	 * @Description:全额现金券投资的订单 根据项目id
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年3月18日 下午4:34:21
	 */
	List<Order> selectAllAmountUseCouponForInvest(@Param("projectId")Long projectId);
	
	/**
	 * @Description:全额现金券投资的订单 根据转让项目id
	 * @param transfer_id
	 * @return
	 * @author: zhanghao
	 * @time:2016年3月18日 下午4:34:21
	 */
	List<Order> selectAllAmountUseCouponForInvestTransfer(@Param("transferId")Long transferId);
	
	/**
	 * @Description:不是全额现金券投资的订单 根据转让项目id
	 * @param transfer_id
	 * @return
	 * @author: zhanghao
	 * @time:2016年11月30日 上午10:34:21
	 */
	List<Order> selectUseAmountForInvestTransfer(@Param("transferId")Long transferId);
	
	
	
	/**
	 * 
	 * @Description 获取用户处理中或待处理的订单数量
	 * @param memberId
	 * @param investFlag 投标标识（0-手动投资；1-自动投资）
	 * @return
	 * @author luwenshan
	 * @time 2016年8月18日 下午2:19:35
	 */
	int getHandleingOrderCount(@Param("memberId") Long memberId, @Param("investFlag") Integer investFlag);
	
	List<Order> selectAllOrderList(@Param("projectId")Long projectId);
	
}