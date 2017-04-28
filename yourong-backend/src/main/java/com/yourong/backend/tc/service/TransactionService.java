package com.yourong.backend.tc.service;

import java.math.BigDecimal;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.TransactionForOrder;

public interface TransactionService {

	/**
	 * 定时支付利息和本金时候创建代收交易
	 */
	public void createHostingCollectTradeForPayInterestAndPrincipal();

	/**
	 * 同步代收交易状态
	 */
	public void SynchronizedHostingCollectTrade();
	/**
	 * 生成合同
	 * @param transactionId
	 * @return
	 */
	public boolean generateContract(Long transactionId);

	/**
	 * 同步代付交易
	 */
	public void SynchronizedHostingPayTrade();
	/**
	 * 同步批次代付交易
	 */
	public void SynchronizedBatchHostingPayTrade();
	
	/**
	 * 放款给原始债权人前的业务（代收平台优惠券垫付金额）
	 * @param projectId
	 */
	public ResultDO<?> beforeLoanToOriginalCreditor(Long projectId,Long currentId) throws Exception;

	/**
	 * 直接代付
	 * @param memberId
	 * @param amount
	 * @param type 
	 * @return
	 * @throws Exception
	 */
	public ResultDO<?> directHostingPayTrade(Long memberId, BigDecimal amount, int type,String remark) throws Exception;

	/**
	 * 直接代收
	 * @param memberId
	 * @param amount
	 * @return
	 */
	public ResultDO<?> directHostingCollectTrade(Long memberId,
			BigDecimal amount, int type, String remark) throws Exception;

	/**
	 * 同步单笔代收交易
	 * @param tradeNo
	 * @return
	 * @throws Exception
	 */
	public ResultDO<?> synHostingCollectTrade(String tradeNo) throws Exception;

	public ResultDO<HostingCollectTrade> processPayInterestAndPrincipal(String tradeNo) throws Exception;

	/**
	 * 投资信息分页查询
	 * @param pageReques
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Page<Transaction> findByPage(Page<Transaction> pageReques, Map<String, Object> map) throws Exception;
	
	/**
     * 交易管理分页查询
     * 
     * @param pageRequest
     * @param map
     * @return
     * @throws ManagerException
     */
	public Page<TransactionForOrder> queryTansactionForList(Page<TransactionForOrder> pageRequest, Map<String, Object> map);
	
	/**
	 * 根据交易ID查询付息记录
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<TransactionInterest> showInterestRecord(Page<TransactionInterest> pageRequest, Map<String, Object> map);

	/**
	 * 保存代付到新浪（处理代付本地保存了，新浪没有保存的业务）
	 * @param tradeNo
	 * @return
	 */
	public ResultDO<?> addHostingPayTradeToSina(String tradeNo);

	/**
	 * @Description:同步项目待支付的交易
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2015年11月25日 下午1:51:55
	 */
	public ResultDO<?> synProjectHostCollectToSina(Long projectId);
	
	/**
	 * @Description:同步单笔代付交易
	 * @param tradeNo
	 * @return
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年3月15日 下午7:34:04
	 */
	public ResultDO<?> synHostingPayTrade(String tradeNo) throws Exception;
	
	/**
	 * @Description:同步退款交易
	 * @param tradeNo
	 * @return
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年3月15日 下午8:38:55
	 */
	public ResultDO<?> synHostingRefundTrade(String tradeNo) throws Exception;
	
	/**
	 * @Description:根据代收号创建放款代付
	 * @param projectId
	 * @param totalUsedCapitalAmount
	 * @param hostingCollectTrade
	 * @param transactions
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月1日 下午4:51:31
	 */
	ResultDO<?> createHostPayForLoan(String collectTradeNo);

	/**
	 *  根据代收号，重新发起代收交易
	 * @param collectTradeNo
	 * @return
	 * @throws Exception
	 */
	public Object rebuildHostingCollectTrade(String collectTradeNo);

	/**
	 * @Description:根据批付号同步代付交易
	 * @param batchPayNo
	 * @return
	 * @author: fuyili
	 * @time:2016年6月17日 下午2:45:35
	 */
	public ResultDO<?> synBatchHostingPayTrade(String batchPayNo)throws Exception;
	
	/**
	 * 根据批付号创建远程批付
	 * @return
	 * @throws Exception
	 */
	public ResultDO<?> createRemoteBatchPay(Long projectId)throws Exception;

	/**
	 * 根据批付号创建本地批付
	 * @return
	 * @throws Exception
	 */
	public ResultDO<?> createHostBatchPay(String tradeNo)throws Exception;
	
	/**
	 * 获取合同下载链接
	 * @param transactionId
	 * @return
	 */ 
	public ResultDO<Object> getContractDownUrl(Long transactionId) ;
}
