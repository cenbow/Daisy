package com.yourong.core.tc.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.Order;

public interface HostingCollectTradeManager {

	/**
	 * 插入代收交易记录
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	public int insertSelective(HostingCollectTrade record) throws ManagerException;

	/**
	 * 通过代收交易号查询代收交易
	 * @param tradeNo
	 * @return
	 * @throws ManagerException
	 */
	public HostingCollectTrade getByTradeNo(String tradeNo) throws ManagerException;

	/**
	 * 更新代收交易记录
	 * @param hostingCollectTrade
	 * @return
	 * @throws ManagerException
	 */
	public int updateHostingCollectTrade(
			HostingCollectTrade hostingCollectTrade) throws ManagerException;

	/**
	 * 通过交易号和类型查询代收交易
	 * @param sourceId
	 * @param type
	 * @return
	 * @throws ManagerException
	 */
	public HostingCollectTrade getBySourceIdAndType(Long sourceId, Integer type) throws ManagerException;

	/**
	 * 查询需要同步的代收交易
	 * @return
	 * @throws ManagerException
	 */
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTrades() throws ManagerException;
	
	/**
	 * 更新代收交易记录状态
	 * @param hostingCollectTrade
	 * @return
	 * @throws ManagerException
	 */
	public int updateTradeStatus(
			HostingCollectTrade hostingCollectTrade, String status, String remarks) throws ManagerException;

	public HostingCollectTrade getById(Long id) throws ManagerException;

	/**
	 * 判断是否有正在还款的原始债权人代收
	 * @param projectId
	 * @param transactionInterestIds
	 * @return
	 * @throws ManagerException
	 */
	public boolean haveHostingCollectTradeForLender(Long projectId,
			String transactionInterestIds) throws ManagerException;
	
	/**
	 * 判断是否有正在还款的平台收益券代收
	 * @param sourceId
	 * @param transactionInterestIds
	 * @return
	 * @throws ManagerException
	 */
	public boolean haveHostingCollectTradeForPaltform(Long sourceId,
			String transactionInterestIds) throws ManagerException;

	
	/**
	 * 通过交易号获取代付交易
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public HostingCollectTrade getByIdForLock(Long id) throws ManagerException;
	
	/**
	 * 判断是否有正在垫付租赁分红的平台代收交易
	 * @param sourceId
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	public boolean haveHostingCollectTradeForLeaseBonus(Long sourceId,
			Long projectId) throws ManagerException;


	/**
	 * 查询类型为用户投资、交易状态是待支付的代收交易
	 * @param orderId 订单号
	 * @return
	 * @throws ManagerException
	 */
	public HostingCollectTrade getWaitPayHostingCollectTrade(Long orderId) throws ManagerException;
	
	/**
	 *  查询类型为用户投资、交易状态是待支付或完成的代收交易
	 * @param source
	 * @param type
	 * @return
	 * @throws ManagerException
	 */
	public HostingCollectTrade selectWaitPayOrFinishedHostingCollectTrade(Long source, int type) throws ManagerException;
	
	/**
	 * 后台代收查询
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	Page<HostingCollectTrade> findByPage(Page<HostingCollectTrade> pageRequest, Map<String, Object> map) throws ManagerException;
	
	
	/**
	 * @Description:根据项目id获取等待付款的交易号
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2015年11月25日 下午1:43:01
	 */
	List<HostingCollectTrade>  getWaitPayTradeNoByProjectId(Long projectId)throws ManagerException;

	/**
	 * 查询状态是等待付款， 类型是  （ 2-借款人还款 3-基本户现金券投资垫付代收 4-基本户收益权付息垫付代收 6-租赁分红代收） 代收记录
	 * @return
	 * @throws ManagerException
	 */
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTradesForPayerAndPlaform()throws ManagerException;
	/**
	 * 查询状态是等待付款， 类型是  （ 2-借款人还款  4-基本户收益权付息垫付代收 ) 代收记录
	 * @return
	 * @throws ManagerException
	 */
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTradesForPayerAndReplayment()throws ManagerException;

	/**
	 * 查询该项目下所有的代收记录 （ 2-借款人还款 4-基本户收益权付息垫付代收 ）
	 * @param sourceId
	 * @return
	 * @throws ManagerException
	 */
	public List<HostingCollectTrade> selectHostingCollectTradesBySourceId(Long sourceId) throws ManagerException;
	
	/**
	 * @Description:根据项目id查询投资的未退款或者退款失败的代收记录
	 * @param projectId
	 * @param type
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月4日 下午2:54:24
	 */
	public List<HostingCollectTrade> selectHostCollectByProjectIdAndType(Long projectId)throws ManagerException;
	
	/**
	 * 根据项目ID查询需要同步的代收交易
	 * @param projectId
	 * @param type
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年5月3日 下午19:00:24
	 */
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTradesByProjectId(Long projectId)throws ManagerException;
	
	/**
	 * 
	 * @Description:根据订单封装代收交易
	 * @param order
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年5月20日 下午5:25:17
	 */
	public void loadCollectTradeByOrder(Order order, HostingCollectTrade collectTrade) throws ManagerException;
	
	/**
	 * 
	 * @Description:代收完成/撤销成功的业务处理
	 * @param tradeStatus
	 * @param collectTrade
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年5月25日 下午10:15:49
	 */
	public ResultDO<Object> handlePreAuthTrade(String notifyTradeStatus, HostingCollectTrade collectTrade) throws Exception;

	/**
	 * 
	 * @Description:查询项目下代收冻结的代收记录
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年5月26日 下午6:04:14
	 */
	public List<HostingCollectTrade> selectPreAuthApplySuccessByProjectId(Long projectId) throws ManagerException;

	/**
	 * 根据主键和交易状态更新代收交易记录
	 * 
	 * @param hostingCollectTrade
	 * @return
	 * @throws ManagerException
	 */
	public int updateHostingCollectTradeByIdAndTradeStatus(HostingCollectTrade hostingCollectTrade) throws ManagerException;

	/**
	 * 判断还本付息中借款人代收和平台代收是否都已经完成，如果都完成则返回true，否则返回false
	 * @param tradeNo
	 * @return
	 */
	public boolean isAllCollectFinishedForRepayment(String tradeNo) throws ManagerException;
	
	/**
	 * 通过平台代收号查询关联的借款人代收（还本付息专用）
	 * @param paltformTradeNo
	 * @return
	 * @throws ManagerException
	 */
	public HostingCollectTrade getBorrowTradeByPlatformTradeNo(String paltformTradeNo)  throws ManagerException;

	/**
	 * @Description:根据代收交易号和类型查询代收交易记录
	 * @param tradeNo
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年7月29日 下午1:57:24
	 */
	HostingCollectTrade getByTradeNoAndType(String tradeNo,int type) throws ManagerException;

	/**
	 * 
	 * @Description:实际发起新浪代收时是否开通委托
	 * @param hostingCollectTrade
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年8月1日 上午11:45:42
	 */
	public int updateHostingCollectTradeWithholdAuthority(HostingCollectTrade hostingCollectTrade) throws ManagerException;

	/**
	 * 
	 * @Description:根据转让ID获取代收列表
	 * @param transferId
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年9月18日 下午1:15:14
	 */
	public List<HostingCollectTrade> selectPreAuthApplySuccessByTransferId(Long transferId) throws ManagerException;

}