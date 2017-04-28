/**
 * 
 */
package com.yourong.core.ic.manager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.biz.TransactionProjectDetailBiz;
import com.yourong.core.ic.model.biz.TransferProjectPageBiz;
import com.yourong.core.ic.model.query.TransactionProjectDetailBizQuery;
import com.yourong.core.ic.model.query.TransferProjectPageQuery;
import com.yourong.core.ic.model.query.TransferProjectQuery;
import com.yourong.core.ic.model.query.TransferRecordQuery;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;

/**
 * @desc 转让项目
 * @author zhanghao
 * 2016年8月28日上午11:02:07
 */
public interface TransferProjectManager {

	/**
	 * 根据编号获得转让项目信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public TransferProject selectByPrimaryKey(Long id) throws ManagerException;
	
	/**
	 * 根据编号获得转让项目信息 锁表
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public TransferProject selectByPrimaryKeyForLock(Long id) throws ManagerException;
	
	/**
	 * 根据交易编号获得转让项目信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public TransferProject selectByTransactionId(Long transactionId) throws ManagerException ;
	
	public TransferProject selectByTransactionIdForLock(Long transactionId) throws ManagerException;
	
	
	/**
	 * 根据交易编号获得今日转让项目信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public TransferProject selectByTransactionIdToday(Long transactionId) throws ManagerException ;
	

	/**
	 * 更新转让项目
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int updateByPrimaryKeySelective(TransferProject transferProject) throws ManagerException ;
	
	/**
	 * 获得用户所有转让项目信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public List<TransferProject> selectTransferInfoByMemberId(TransferRecordQuery query) throws ManagerException ;
	
	public int selectCountTransferInfoByMemberId(TransferRecordQuery query) throws ManagerException;
	
	/**
	 * 根据交易编号获得投资中转让项目信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public TransferProject selectByTransactionIdStatus(Long transactionId) throws ManagerException;
	
	
	
	public int insert(TransferProject transferProject) throws ManagerException;
	
	/**
	 * 发起转让
	 */
	public ResultDO<Object> transferToProject(Long memberId ,Long transactionId,BigDecimal transferAmount)throws ManagerException ;
	
	/**
	 * 撤销转让
	 */
	public ResultDO<Object> cancelTransferProject(Long transactionId) throws ManagerException;
	/**
	 * 项目价值
	 */
	public BigDecimal getProjectValue(Long transactionId);
	/**
	 * 剩余本金
	 */
	public BigDecimal getResidualPrincipal(Long transactionId);
	
	/**
	 * 剩余认购本金
	 */
	public BigDecimal getTransactionAmount(Long transactionId);
	
	/**
	 * 剩余收益天数
	 */
	public Integer getReturnDay(Long transactionId);
	
	
	public Page<TransferProject> findTransferProjectList(TransferProjectQuery transferProjectQuery) throws ManagerException;
	
	/**
	 * 更新项目状态
	 * 
	 * @param newStatus
	 *            新状态
	 * @param currentStatus
	 *            当前状态
	 * @param id
	 *            转让项目编号
	 * @return
	 * @throws ManagerException
	 */
	public int updateProjectStatus(int newStatus, int currentStatus, Long id) throws ManagerException;

	/**
	 * 
	 * @Description:转让项目募集成功
	 * @param transferId
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年9月18日 上午10:21:04
	 */
	public void finishPreAuthTrade(Long transferId) throws Exception;

	/**
	 * 
	 * @Description:转让项目代收完成后续业务
	 * @param transferId
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年9月19日 下午10:29:39
	 */
	public List<HostingPayTrade> transferSuccess(Long transferId) throws Exception;
	
	/**
	 * 
	 * @Description:转让项目首页项目
	 * @param num
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年9月21日 下午10:29:39
	 */
	public List<TransferProject> findIndexInvestingProjectList(Integer num) throws ManagerException;
	
	/**
	 * 
	 * @Description:转让项目首页项目
	 * @param num
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年9月21日 下午10:29:39
	 */
	public List<TransferProject> findIndexNotInvestingProjectList(Integer num) throws ManagerException;
	
	/**
	 * @Description:创建本地代付
	 * @param transferProject
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年9月23日 上午9:42:54
	 */
	public List<HostingPayTrade> insertHostPayForTransferSuccess(TransferProject transferProject, boolean firstFlag)
			throws ManagerException;
	
	
	/**
	 * @Description:创建新浪代付
	 * @param transferProject
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @throws Exception 
	 * @time:2016年9月23日 上午9:42:54
	 */
	public void createSinaHostPayForTransferSuccess(List<HostingPayTrade> hostingPayTrades) throws Exception ;
	
	
	/**
	 * @Description:转让代付回调
	 * @param hostingPayTrade
	 * @return
	 * @author: fuyili
	 * @time:2016年9月23日 上午10:13:09
	 */
	public ResultDO<?> afterHostPayForTransferSuccess(String tradeStatus, String tradeNo, String outTradeNo)throws Exception;
	
	
	public void insertTransferTransactionInterest(Long transferId, TransferProject transferProject,List<TransactionInterest> toTransferInterests)
			throws ManagerException ;
	
	/**
	 * @Description:账户中心转让记录列表
	 * @param transferProjectQuery
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年9月24日 下午12:35:12
	 */
	public Page<TransferProject> findTransferProjectListForMember(TransferProjectQuery transferProjectQuery) throws ManagerException;
	
	
	public List<TransferProject> queryTransferProjectListByMap(Map<String, Object> map) throws ManagerException ;
	
	
	/**
	 * @Description:转让失败处理
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年9月24日 下午5:07:06
	 */
	public void loseTransferProject(Long transferId,Integer flag) throws ManagerException;
	
	/**
	 * @Description:转让撤销完成后续操作
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年9月24日 下午5:07:06
	 */
	public void afterLoseTransferProject(Long transferId) throws ManagerException;
	
	

	public TransferProject totalTransferProjectByMemberId(Long memberId) throws ManagerException;
	/**
	 * 转让笔数
	 * @param memberId
	 * @return
	 * @throws ManagerException 
	 */
	public List<TransferProject> totalTransferProjectByMemberIdNum(Long memberId) throws ManagerException;

	/**
	 * 
	 * @Description:批量更新转让项目状态
	 * @param projectId
	 * @param newStatus
	 * @param oldStatus
	 * @return
	 * @author: wangyanji
	 * @time:2016年10月9日 下午4:55:20
	 */
	public int updateStatusByProjectId(Long projectId, int newStatus, int oldStatus) throws ManagerException;

	/**
	 * 
	 * @Description:转让交易业务处理
	 * @param transaction
	 * @param isFull
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年12月13日
	 */
	public void handleTransaction(Transaction transaction, boolean isFull, Balance transferBalance)
			throws ManagerException;

	public TransferProject selectByTransactionIdAndMemberId(Long transactionId,
			Long memberId) throws ManagerException;

	public List<TransferProject> selectTransferByMemberId(Long memberId) throws ManagerException;

	public BigDecimal getTransferProjectBalanceById(Long id);
	
	public BigDecimal getTransferProjectAnnualized(Long id);

	public Page<TransferProjectPageBiz> queryPageTransferProjectPageBiz(TransferProjectPageQuery query);

	public List<TransferProject> queryTransferProjectListByTransactionId(Long transactionId)throws ManagerException;

	/**
	 * 查询投资中转让项目
	 * @param transactionId
	 * @param memberId
     * @return
     */
	public TransactionProjectDetailBiz queryInvestmentedTransactionProjectDetailBiz(Long transactionId, Long memberId);

	public Page<TransactionProjectDetailBiz> queryPageTransactionProjectDetailBiz(TransactionProjectDetailBizQuery query);
	
	public void transferEndSendMessage(Long id);

	/**
	 * 
	 * @Description:查询会员总转让本金
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月3日
	 */
	public BigDecimal getTotalTransferPrincipal(Long memberId) throws ManagerException;
}
