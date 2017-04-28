package com.yourong.core.tc.manager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.HostingPayProjectMember;
import com.yourong.core.tc.model.HostingPayTrade;

/**
 * 代付交易manager
 * @author Leon Ray
 * 2014年10月18日-下午4:22:54
 */
public interface HostingPayTradeManager {
	
	/**
	 * 插入代付交易
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	int insertSelective(HostingPayTrade record) throws ManagerException;

	/**
	 * 通过交易号获取代付交易
	 * @param tradeNo
	 * @return
	 * @throws ManagerException
	 */
	public HostingPayTrade getByTradeNo(String tradeNo) throws ManagerException;
	
	/**
	 * 通过交易号获取代付交易
	 * @param tradeNo
	 * @return
	 * @throws ManagerException
	 */
	public HostingPayTrade getByIdForLock(Long id) throws ManagerException;

	/**
	 * 更新代付交易
	 * @param hostingPayTrade
	 * @return
	 * @throws ManagerException
	 */
	int updateHostingPayTrade(HostingPayTrade hostingPayTrade) throws ManagerException;

	/**
	 * 通参数查询代付交易记录
	 * @param params
	 * @return
	 * @throws ManagerException
	 */
	List<HostingPayTrade> getHostingPayTradeByParam(Map<String, Object> params) throws ManagerException;

	/**
	 * 查询需要同步的代付交易记录
	 * @return
	 * @throws ManagerException
	 */
	List<HostingPayTrade> selectSynchronizedHostingPayTrades() throws ManagerException;

	/**
	 * 查询是否有本息代付记录
	 * @param sourceId
	 * @param transactionId 
	 * @return
	 * @throws ManagerException
	 */
	boolean haveHostPayTradeForPayInterestAndPrincipal(Long sourceId, int type) throws Exception;
	/**
	 * 查询是否有本息代付记录
	 * @param sourceId
	 * @param transactionId 
	 * @return
	 * @throws ManagerException
	 */
	boolean haveHostPayTradeForPayLeaseBonus(Long sourceId, int type, Long transactionId) throws ManagerException;

	int countHostPayBySourceIdAndType(Long sourceId, int type, String tradeStatus) throws ManagerException;

	/**
	 * 后台代付查询
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	Page<HostingPayTrade> findByPage(Page<HostingPayTrade> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * 查询交易本息表 和代付表关联的记录
	 * @param projectId
	 * @return
	 */
	List<HostingPayProjectMember> selectProjectInverstAndHostingPayTrade(Long projectId) throws  ManagerException;

	/**
	 * @Description:根据批付号和状态查询批付
	 * @param batchPayNo
	 * @param name
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年1月29日 下午4:13:36
	 */
	int countHostPayByBatchNoAndStatus(String batchPayNo, String tradeStatus)throws  ManagerException;

	public int updateHostingPayTradeStatus(String afterTradeStatus,String beforeTradeStatus,String outTradeNo,Long id) throws ManagerException;
	
	
	/**
	 * @Description:根据批付号和状态查询批付
	 * @param batchPayNo
	 * @param tradeStatus
	 * @return
	 * @author: fuyili
	 * @time:2016年6月17日 下午2:49:50
	 */
	List<HostingPayTrade> selectHostPayByBatchNoAndStatus(String batchPayNo,String tradeStatus)throws ManagerException;

	/**
	 * 通过借款人代收号批量创建还本付息代付
	 * @param tradeNo
	 * @return 返回批付号列表
	 * @throws ManagerException
	 */
	public List<String> createHostBatchHostingPayForRepayment(String tradeNo) throws Exception;

	/**
	 * 批量创建新浪远程代收
	 * @param batchPayNos
	 * @param projectId 
	 * @throws ManagerException
	 */
	void createRemoteBatchHostingPayForRepayment(List<String> batchPayNos, Long projectId) throws ManagerException;

	/**
	 * 通过项目id和状态查询批次号
	 * @param projectId
	 * @param status 
	 * @return
	 * @throws ManagerException
	 */
	List<String> selectBatchPayNosByProject(Long projectId, String status) throws ManagerException;

	/**
	 * 查询需要同步的状态为wait_pay批次
	 * @return
	 * @throws ManagerException
	 */
	List<String> selectSynchronizedBatchHostingPayTrades() throws ManagerException;
	
	/**
	 * 查询代付记录
	 * @param sourceId
	 * @param transactionId 
	 * @return
	 * @throws ManagerException
	 */
	public HostingPayTrade getHostingPayTradeBySourceIdType(Long sourceId, int type) throws ManagerException ;

	HostingPayTrade selectByPrimaryKey(Long sourceId) throws ManagerException;
	
	public List<HostingPayTrade> getTransferHostingPayTradeByTransferId(Long transferId) throws ManagerException;

	/**
	 * 
	 * @Description:查询会员转让总收款
	 * @param payeeId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月3日
	 */
	public Map getTotalTransferGetAmount(Long payeeId) throws ManagerException;

	/**
	 * 
	 * @Description:查询会员转让总支付手续费
	 * @param payeeId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月3日
	 */
	public Map getTotalTransferFeeAmount(Long payeeId) throws ManagerException;

	/**
	 * 
	 * @Description:查询会员转让收款记录
	 * @param payeeId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月3日
	 */
	public List<HostingPayTrade> getTotalTransferGetList(Long payeeId) throws ManagerException;

	/**
	 * 
	 * @Description:查询会员转让支付手续费记录
	 * @param payeeId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月3日
	 */
	public List<HostingPayTrade> getTotalTransferFeeList(Long payeeId) throws ManagerException;

	/**
	 * 
	 * @Description:根据批付号重新发起转让代付
	 * @param batchNo
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2017年1月9日
	 */
	public ResultDO<Object> synTransferPay(String batchNo);
	
	/**
	 * 
	 * @Description:根据收款人ID和类型金额求和
	 * @param payeeId
	 * @return
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2017年1月16日
	 */
	public BigDecimal totalMemberPayAmountByType(Long payeeId, int type) throws ManagerException;
}