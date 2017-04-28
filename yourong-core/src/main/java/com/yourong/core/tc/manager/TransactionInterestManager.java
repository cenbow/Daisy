package com.yourong.core.tc.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.BorrowInterestDetailBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestCollect;
import com.yourong.core.tc.model.biz.CalenderTransactionInterestDetail;
import com.yourong.core.tc.model.biz.TransactionInterestDetailForCalendar;
import com.yourong.core.tc.model.biz.TransactionInterestForDate;
import com.yourong.core.tc.model.biz.TransactionInterestForMember;
import com.yourong.core.tc.model.biz.TransactionInterestForMonth;
import com.yourong.core.tc.model.biz.TransactionInterestForPay;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.query.BorrowRepayQuery;
import com.yourong.core.tc.model.query.BorrowTransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionInterestQuery;

public interface TransactionInterestManager {
	
	public Page<TransactionInterest> findByPage(Page<TransactionInterest> pageRequest, Map<String, Object> map)
			throws ManagerException;
	
	/**
	 * 插入交易本息记录
	 * @param transactionInterest
	 * @return
	 * @throws ManagerException
	 */
	Integer insert(TransactionInterest transactionInterest) throws ManagerException;

	/**
	 * 通过id获取交易本息记录
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
    TransactionInterest getTransactionInterestById(Long id) throws ManagerException;

    /**
     * 更新交易本息记录
     * @param TransactionInterest
     * @return
     * @throws ManagerException
     */
    Integer updateTransactionInterest(TransactionInterest TransactionInterest) throws ManagerException;

    /**
     * 通过TransactionInterestQuery查询条件查询交易本息列表
     * @param transactionQuery
     * @return
     * @throws ManagerException
     */
	List<TransactionInterest> selectTransactionInterestsByQueryParams(
			TransactionInterestQuery transactionInterestQuery) throws ManagerException;
	
	/**
     * 通过TransactionInterestQuery查询条件查询交易本息列表附加滞纳金处理
     * @param transactionQuery
     * @return
     * @throws ManagerException
     */
	List<TransactionInterest> selectTransactionInterestsByQueryParamsAndOverdue(
			TransactionInterestQuery transactionInterestQuery) throws ManagerException;
	
	/**
	 * 通过项目和债权本息计算项目本息，用于创建订单前后使用
	 * @param debtInterestList
	 * @param project
	 * @param investAmount
	 * @param annualizedRate
	 * @return
	 * @throws ManagerException
	 */
	public List<TransactionInterest> getTransactionInterestByProjectAndDebtInterest(
			List<DebtInterest> debtInterestList,
			Project project, 
			BigDecimal investAmount,
			BigDecimal annualizedRate
			) throws ManagerException;

	/**
	 * 查询待支付交易本息记录
	 * @return
	 * @throws ManagerException
	 */
	public List<TransactionInterestForPay> selectToBePaidTransactionInterests() throws ManagerException;

	/**
	 * 更新状态
	 * @param beforeStatus
	 * @param afterStatus
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int updateStatusAndRealPayForPaySuccess(int beforeStatus, int afterStatus, Long id) throws ManagerException;

	/**
	 * 还本付息时更新待支付的记录为支付中
	 * @param status
	 * @param status2
	 * @param projectId
	 * @param tradeNo 
	 * @return
	 */
	int updateStatusToPayingForPayInterest(int beforeStatus, int afterStatus, Long projectId, String tradeNo) throws ManagerException;
	
	/**
	 * 还本付息更新本息记录状态（包含更新为待支付和已支付）
	 * @param beforeStatus
	 * @param afterStatus
	 * @param tradeNo
	 * @return
	 * @throws ManagerException
	 */
	int updateStatusForPayInterest(int beforeStatus, int afterStatus,  String tradeNo) throws ManagerException;
	
	/**
	 * 代收完成后执行还本付息
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 * @throws Exception
	 */
	public ResultDO<HostingCollectTrade> afterHostingCollectTradeForPayInterestAndPrincipal(String tradeNo,
			String outTradeNo, String tradeStatus) throws Exception;
	
	/**
	 * 同步代付交易后处理支付本息业务
	 * @param tradeStatus
	 * @param outTradeNo
	 * @throws Exception
	 */
	public  ResultDO<TransactionInterest> afterPayInterestAndPrincipal(String tradeStatus,
			String outTradeNo) throws Exception;
	
	/**
	 * 获取预期收益
	 * @param project
	 * @param investAmount
	 * @param annualizedRate
	 * @param extraAnnualizedRate
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getExpectAmount(Project project, BigDecimal  investAmount, BigDecimal annualizedRate , BigDecimal extraAnnualizedRate) throws Exception;
	
	/**
	 * 平台收益回调后处理还本付息
	 * @param projectId
	 * @throws Exception
	 */
	public void processPayInterestAndPrincipal(String tradeNo) throws Exception;
	
	/**
	 * 平台收益权垫付代收回调
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 */
	public ResultDO<HostingCollectTrade> afterHostingCollectTradeCouponBForPayInterestAndPrincipal(
			String tradeNo, String outTradeNo, String tradeStatus);
	
	/**
	 * 查询最近一笔还本人付息数据
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public TransactionInterest queryLastPayTransactionInterest(Long memberId) throws Exception;
	
	/**
	 * 获取一个项目的付息时间金额表
	 * @param projectId
	 * @return
	 */
	public List<TransactionInterest> queryInterestByProjectId(Long projectId) throws ManagerException;
	
	public Page<TransactionInterestForMember> queryTransactionInterestForMember(TransactionInterestQuery query) throws ManagerException;
	/**
	 * 用户待收本金和待收收益，剔除P2P项目收益
	 * @param TransactionInterestQuery
	 * @return
	 * @throws ManagerException
	 */
	public Page<TransactionInterestForMember> p2pQueryTransactionInterestForMember(TransactionInterestQuery query) throws ManagerException;
	/**
	 * 统计用户投资使用收益券获得的利息
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public BigDecimal totalMemberExtraInterest(Long memberId) throws ManagerException;
	
	/**
	 * 根据日期获得用户还本付息详情
	 * @param memberId
	 * @param endDate
	 * @return
	 */
	public List<TransactionInterestDetailForCalendar> getMemberInterestDetailByDate(Map<String,Object> map) throws ManagerException;
	
	/**
	 * 当前还款的笔数 
	 * @param memberId
	 * @param transactionId
	 * @param endDate
	 * @return
	 */
	public Integer getCurrentInterestNum(Long memberId, Long transactionId, String endDate) throws ManagerException;
	
	/**
	 * 获得项目还款的总笔数 
	 * @param memberId
	 * @param transactionId
	 * @return
	 */
	public Integer getTotalInterestNum(Long memberId, Long transactionId) throws ManagerException;
	
	/**
	 * 查看用户还本付息明细
	 * @param query
	 * @return
	 */
	public List<TransactionInterestForMember>  getTransactionInterestDetailForMember(TransactionInterestQuery query) throws ManagerException;

	/**
	 * 查询待支付 项目id 集合
	 * @return
	 */
	public List<Long> queryToBePaidTransactionInterests()  throws ManagerException;

	/**
	 * 查询待支付的，包括直投项目
	 * @return
	 */
	List<TransactionInterestForPay> selectAllProjectToBePaidTransactionInterests() throws ManagerException;

	/**
	 * @Description:根据交易id获取交易本息记录数
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月26日 下午8:21:11
	 */
	int selectTransactionInterestsCountByTransactionId(Long transactionId) throws ManagerException;
	/**
	 * 
	 * @Description:过滤p2p 
	 * @param transactionInterestQuery
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月8日 上午11:14:28
	 */
	List<TransactionInterest> selectTransactionInterestsByQueryParamsFilterP2p(TransactionInterestQuery transactionInterestQuery)throws ManagerException;
	/**
	 * 
	 * @Description:查看用户还本付息明细
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月11日 下午5:16:34
	 */
	public List<TransactionInterestForMember> getTransactionInterestDetailForMemberFilterP2P(TransactionInterestQuery query)
			throws ManagerException;

	/**
	 * 
	 * @Description:统计月收金额
	 * @param memberId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年4月6日 下午1:23:41
	 */
	public List<TransactionInterestForMonth> selectTransactionInterestsByMonth(Long memberId, String startYM, String endYM)
			throws ManagerException;

	/**
	 * 
	 * @Description:根据结束日期分组查询交易本息，status: 1 当天全部已还款，0 存在未还
	 * @param memberId
	 * @param startYM
	 * @param endYM
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年4月6日 下午2:41:05
	 */
	public List<TransactionInterestForDate> selectTransactionInterestsByEndDate(Long memberId, String startYM, String endYM)
			throws ManagerException;
	
	/**
	 * 
	 * @Description:获取一个项目的付息时间金额表，和本息标志
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年4月6日 下午2:00:34
	 */
	public List<TransactionInterest> queryInterestAndPrincipalFlagByProjectId(Long projectId)throws ManagerException; 
	
	/**
	 * 
	 * @Description:用户指定日期是否有还款
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年5月25日 上午11:49:34
	 */
	public Integer ifPayByEndDateAndMemberId(Map<String, Object> map)throws ManagerException;
	/**
	 * 查询当日交易本息所关联的借款人代收号
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public  List<String> selectCurrentDateCollectTradeNos(Integer status)throws ManagerException;
	
	/**
	 * @Description:查询需要提前还本付息的本息记录- 包括直投项目  
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年5月20日 上午10:14:59
	 */
	public List<TransactionInterestForPay> selectAllProjectToBePrePaidTransactionInterests() throws ManagerException;
	
	
	/**
	 * @Description:交易对应未还款的本息个数
	 * @return
	 * @author: fuyili
	 * @time:2016年5月23日 上午10:25:45
	 */
	int getCountUnReturnTransationInterestByTransationId(Long transactionId) throws ManagerException;
	
	/**
	 * @Description:项目本息对应未还款的本息个数
	 * @return
	 * @author: fuyili
	 * @time:2016年5月23日 上午10:25:45
	 */
	int getCountUnReturnTransationInterestByProjectInterestId(Long interestId) throws ManagerException;
	
	/**
	 * @Description:获取提前还款期数信息
	 * @return
	 * @author: zhanghao
	 * @time:2016年6月1日 下午14:00:45
	 */
	public TransactionInterest getPreTransactionInterest(Map<String,Object> map)throws ManagerException;
	
	/**
	 * @Description:逾期还款的交易本息
	 * @return
	 * @author: fuyili
	 * @time:2016年6月5日 下午7:13:48
	 */
	List<TransactionInterestForPay> selectAllProjectToBeOverPaidTransactionInterests()throws ManagerException;
	
	/**
	 * 更新状态
	 * @param beforeStatus
	 * @param afterStatus
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int updateStatusForPaySuccess(int beforeStatus, int afterStatus, Long id) throws ManagerException;
	/**
	 * @Description:根据日期获取用户收益日历单日信息
	 * @return
	 * @author: zhanghao
	 * @time:2016年6月6日 上午9:21:45
	 */
	public List<CalenderTransactionInterestDetail> getMemberInterestDetailByDateNew(
			Map<String,Object> map) throws ManagerException;
	
	
	/**
	 * @Description:根据项目本息id统计交易本息本期所有实付金额
	 * @param interestId
	 * @return
	 * @author: fuyili
	 * @time:2016年6月18日 下午5:21:32
	 */
	TransactionInterest getRealPayByInterestId(Long interestId) throws ManagerException;

	/**
	 * 分页查询借款人还本付息
	 * @param query
	 * @return
     */
	Page<BorrowRepayInterestBiz> findPageBorrowRepayInterest(BorrowRepayQuery query);

	/**
	 *查询借款人还本付息汇总
	 * @return
     */
	BorrowRepayInterestCollect findBorrowRepayInterestCollect(Date date);

	/**
	 * 查询借款人当天的还款本息
	 * @param borrwowid
	 * @param date
     * @return
     */
	Page<BorrowInterestDetailBiz> findDayBorrowRepayInterestByBorrowId(BorrowTransactionInterestQuery query,String projectids);
	
	/**
	 * 
	 * @Description:根据项目id更新
	 * @param transactionInterest
	 * @return
	 * @author: wangyanji
	 * @time:2016年9月21日 上午4:27:46
	 */
	public Integer updateByTransactionId(TransactionInterest transactionInterest) throws ManagerException;
	
	
	/**
	 * 
	 * @Description:查询交易对应的所有可转让（status=待支付）的交易本息
	 * @param transactionInterest
	 * @return
	 * @author: wangyanji
	 * @time:2016年9月21日 上午4:27:46
	 */
	public List<TransactionInterest> selectToTransferInterestByTransactionId(Long transactionId) throws ManagerException;
	/**
	 * 
	 * @Description:已转让本金
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年9月27日 下午2:03:33
	 */
	public BigDecimal totalTransferAmount(Long memberId) throws ManagerException;

	BigDecimal totalMemberExtraInterestT(Long memberId) throws ManagerException;
	/**
	 * 
	 * @param 已转让本金笔数
	 * @return
	 * @throws ManagerException 
	 */
	public Integer totalTransferAmountNum(Long memberId) throws ManagerException;

	/**
	 * @Description:查询提前还款记录
	 * @param transactionQuery
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年12月12日
	 */
	public List<TransactionInterest> queryEarlyInterest(TransactionInterestQuery transactionQuery)
			throws ManagerException;

	/**
	 * 
	 * @Description:加锁查询
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年12月14日
	 */
	public TransactionInterest selectByPrimaryKeyForLock(Long id) throws ManagerException;

	/**
	 * 查询实际支付收益券产生的利息和实付利息
	 * @param transactionId
	 * @return
	 */
	TransactionForMemberCenter queryTotalInterestByTransactionId(Long transactionId);
}