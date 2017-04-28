package com.yourong.core.tc.manager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.OrderForMember;
import com.yourong.core.tc.model.biz.PayOrderBiz;
import com.yourong.core.tc.model.query.OrderQuery;

public interface OrderManager {

	/**
	 * 插入订单
	 * @param order
	 * @return
	 * @throws ManagerException
	 */
	Integer insert(Order order) throws ManagerException;

	/**
	 * 通过id查询订单
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
    Order selectByPrimaryKey(Long id) throws ManagerException;

    /**
     * 更新订单
     * @param order
     * @return
     * @throws ManagerException
     */
    Integer updateByPrimaryKeySelective(Order order) throws ManagerException;

    /**
     * 查询当天订单数量
     * @param param
     * @return
     * @throws ManagerException
     */
	int getOrderCountCurrentDay(Map<String, Object> param)throws ManagerException;

	/**
	 * 通过交易号查询订单信息
	 * @param tradeNo
	 * @return
	 * @throws ManagerException
	 */
	Order getOrderByTradeNo(String tradeNo)throws ManagerException;

	/**
	 * 通过订单号号查询订单信息
	 * @param orderNo
	 * @return
	 */
	Order getOrderByOrderNo(String orderNo) throws ManagerException;

	/**
	 * 定时关闭订单
	 * @param orderId 
	 * @return
	 * @throws ManagerException
	 */
	int schedueCloseOrder(Long orderId, int beforeStatus, int afterStatus) throws ManagerException;

	/**
	 * 用户中心查询订单分页列表
	 * @param pageRequest
	 * @param orderQuery
	 * @return
	 */
	Page<OrderForMember> selectAllOrderForMember(OrderQuery orderQuery) throws ManagerException ;
	
	/**
	 * 用户中心查询订单分页列表,剔除p2p项目
	 * @param pageRequest
	 * @param orderQuery
	 * @return
	 */
	Page<OrderForMember> p2pSelectAllOrderForMember(OrderQuery orderQuery) throws ManagerException ;

	/**
	 * 获取当前用户 还未支付的订单
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	int getNoPayOrdeCount(Long memberId) throws ManagerException ;

	/**
	 * 取消订单
	 * @param orderId
	 * @return
	 * @throws ManagerException
	 */
	int cancelOrder(Long orderId, String remarks) throws ManagerException ;

	/**
	 * 获取订单详情
	 * @param orderId
	 * @param memberId 
	 * @return
	 */
	OrderForMember getOrderForMemberByOrderId(Long orderId, Long memberId) throws ManagerException ;
	
	  /**
     * 更新订单状态
     * @param order
     * @return
     * @throws ManagerException
     */
    int updateStatus(Order order, int status, String remarks) throws ManagerException;

    /**
     * 查询需要定时关闭的订单
     * @return
     * @throws ManagerException
     */
	List<Order> selectSchedueCloseOrder() throws ManagerException;

	/**
	 * 通过id查询订单（加锁）
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	Order getOrderByIdForLock(Long id) throws ManagerException;
	
    /**
     * 订单分页查询
     * @param pageRequest
     * @param map
     * @return
     * @throws ManagerException
     */
    Page<OrderForMember> findByPage(Page<OrderForMember> pageRequest, Map<String, Object> map) throws ManagerException;
    
    /**
     * 查询投资人信息
     * 
     * @param orderNo
     * @return
     */
	Map selectTransPersonByOrderNo(String orderNo) throws ManagerException;
	
	/**
     * 根据优惠券查询订单
     * 
     * @param orderNo
     * @return
     */
	List<Order> selectOrderByCoupon(String couponCode) throws ManagerException;
	
	
	/**
	 * 购车垫资和车商融的订单是否使用了优惠券
	 */
	boolean orderOfCarPayInOrCarBusinessIsUseCoupon(Long projectId,String couponNo)throws ManagerException;

	/**
	 * 购车分期订单是否使用了优惠券
	 */
	boolean orderOfBuyCarIsUseCoupon(Long projectId, String couponNo) throws ManagerException;
	/**
	 * 
	 * @Description:获取当前用户 还未支付的订单
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月8日 下午1:13:48
	 */
	int getNoPayOrdeCountFilterP2p(Long memberId)throws ManagerException ;
	/**
	 * 
	 * @Description:获取当前项目支付确认中的订单
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年6月12日 下午18:05:48
	 */
	int getOrderCountByProject(Long id) throws ManagerException;
	
	/**
	 * 
	 * @Description:获取当前项目支付确认中的订单
	 * @param transferId 转让项目ID
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年9月9日 下午18:05:48
	 */
	int getTransferOrderCountByProject(Long transferId) throws ManagerException;
	
	/**
     * 通过项目id查询该项目支付中的订单金额
     * @param projectId
     * @return
     * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年6月12日 下午18:05:48
     */
	BigDecimal getPayingAmountByProject(Long projectId) throws ManagerException;
	
	/**
     * 通过项目id查询该项目支付中的订单金额
     * @param transferId
     * @return
     * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年6月12日 下午18:05:48
     */
	BigDecimal getTransferPayingAmountByProject(Long transferId) throws ManagerException;

	/**
	 * 
	 * @Description:支付订单详情页
	 * @param orderNo
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2016年7月25日 下午5:42:05
	 */
	public ResultDO<Object> getPayOrderDetail(String orderNo, Long memberId) throws ManagerException;
	/**
	 * 直投收益劵额外收益
	 */
	public void p2pProjectCheck(Project project, Order order, PayOrderBiz payOrderBiz) throws ManagerException ;
	/**
	 * 债权收益劵额外收益
	 */
	public void debtExpectAmount(Project project, Order order, PayOrderBiz payOrderBiz) throws ManagerException ;
	
	/**
	 * 
	 * @Description 获取用户处理中或待处理的订单数量
	 * @param memberId
	 * @param investFlag 投标标识（0-手动投资；1-自动投资）
	 * @return
	 * @author luwenshan
	 * @time 2016年8月18日 下午2:19:35
	 */
	int getHandleingOrderCount(Long memberId, Integer investFlag) throws ManagerException;
	
	

	/**
	 * 关闭待确认订单
	 * @param orderId
	 * @return
	 * @throws ManagerException
	 */
	int closeDirectProjectOrder(Long orderId, String remarks) throws ManagerException ;
	
	/**
	 * 
	 * @Description:创建订单前置校验
	 * @param order
	 * @return
	 * @author: wangyanji
	 * @time:2016年8月28日 下午4:02:59
	 */
	public ResultDO<Order> preCreateOrderValidate(Order order);

}