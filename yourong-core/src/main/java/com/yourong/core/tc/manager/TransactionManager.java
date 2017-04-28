package com.yourong.core.tc.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.SummaryEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.biz.TransactionTransferRecordBiz;
import com.yourong.core.ic.model.biz.TransferRecordBiz;
import com.yourong.core.ic.model.query.TransferRecordQuery;
import com.yourong.core.mc.model.QuickRewardForPay;
import com.yourong.core.mc.model.biz.ActivityForInvest;
import com.yourong.core.mc.model.biz.ActivityForKing;
import com.yourong.core.mc.model.biz.ActivityMonthlyMyRank;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.ContractBiz;
import com.yourong.core.tc.model.biz.DebtForLenderMember;
import com.yourong.core.tc.model.biz.TraceSourceCollectBiz;
import com.yourong.core.tc.model.biz.TransactionForActivity;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.biz.TransactionForOrder;
import com.yourong.core.tc.model.biz.TransactionForProject;
import com.yourong.core.tc.model.biz.TransactionInterestForMember;
import com.yourong.core.tc.model.biz.TransactionProjectBiz;
import com.yourong.core.tc.model.biz.TransferContractBiz;
import com.yourong.core.tc.model.query.CollectingProjectQuery;
import com.yourong.core.tc.model.query.DebtForLenderQuery;
import com.yourong.core.tc.model.query.TraceSourceCollectQuery;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionProjectBizQuery;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.model.Member;

public interface TransactionManager {

	/**
	 * 插入交易记录
	 * 
	 * @param transaction
	 * @return
	 * @throws ManagerException
	 */
	Integer insert(Transaction transaction) throws ManagerException;

	/**
	 * 通过id查询交易记录
	 * 
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	Transaction selectTransactionById(Long id) throws ManagerException;

	/**
	 * 更新交易记录
	 * 
	 * @param transaction
	 * @return
	 * @throws ManagerException
	 */
	Integer updateByPrimaryKeySelective(Transaction transaction) throws ManagerException;

	/**
	 * 前台项目详情页分页查询交易记录
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<TransactionForProject> selectTransactionForProjectsForPage(TransactionQuery transactionQuery)
			throws ManagerException;

	/**
	 * 通过参数查询交易列表
	 * 
	 * @param transactionQuery
	 * @return
	 * @throws ManagerException
	 */
	public List<Transaction> selectTransactionsByQueryParams(TransactionQuery transactionQuery) throws ManagerException;

	/**
	 * 查询最新投资记录
	 * 
	 * @return
	 * @throws ManagerException
	 */
	List<TransactionForProject> selectNewTransactions(int pageSize) throws ManagerException;

	/**
	 * 更新交易表中已收利息和本金
	 * 
	 * @param transactionQuery
	 * @return
	 * @throws ManagerException
	 */
	Integer updateReceivedInterestAndPrincipal(TransactionQuery transactionQuery) throws ManagerException;

	/**
	 * 交易代收回调后处理方法
	 * 
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 * @throws Exception
	 */
	public ResultDO<Transaction> afterTransactionCollectNotifyProcess(String tradeNo, String outTradeNo,	String tradeStatus) throws Exception;

	/**
	 * 创建债权放款代付
	 * 
	 * @param projectId
	 * @param totalUsedCapitalAmount
	 * @param transactions
	 */
	public void createHostingPayForDirectLoan(Long projectId, BigDecimal totalUsedCapitalAmount,
			List<Transaction> transactions, String collectTradeNo);

	/**
	 * 加锁查询交易信息
	 * 
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 */
	Transaction selectTransactionByIdLock(Long transactionId) throws ManagerException;

	/**
	 * 查询用户中心交易
	 * 
	 * @param query
	 * @return
	 */
	Page<TransactionForMemberCenter> selectAllTransactionForMember(TransactionQuery query) throws ManagerException;
	
	/**
	 * 查询用户中心交易,剔除P2P项目数据
	 * 
	 * @param query
	 * @return
	 */
	Page<TransactionForMemberCenter> p2pSelectAllTransactionForMember(TransactionQuery query) throws ManagerException;
	

	/**
	 * 获取交易详情
	 * 
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 */
	TransactionForMemberCenter getTransactionForMember(Long transactionId,Long memberId) throws ManagerException;

	/**
	 * 通过订单id获取交易信息
	 * 
	 * @param orderId
	 * @return
	 * @throws ManagerException
	 */
	Transaction getTransactionByOrderId(Long orderId) throws ManagerException;

	/**
	 * 查询交易记录总数
	 * 
	 * @param memberId
	 * @param type
	 * @return
	 * @throws ManagerException
	 */
	int getTransactionCount(Long memberId, int type) throws ManagerException;

	/**
	 * 不需要创建代收交易直接执行交易方法
	 * 
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public ResultDO<Transaction> doTransactionWithoutCreateHostingCollect(Order order) throws Exception;

	/**
	 * 通过项目id查询使用优惠券总额
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	BigDecimal getCouponAmountForPaltform(Long projectId) throws ManagerException;

	/**
	 * 代收平台垫付优惠券回调处理方法
	 * 
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 * @throws Exception
	 */
	ResultDO<?> afterPaltformCouponCollectNotify(String tradeNo, String outTradeNo, String tradeStatus)
			throws Exception;

	/**
	 * 放款代付成功后处理
	 * 
	 * @param processStatus
	 * @param tradeNo
	 * @return
	 */
	ResultDO<?> afterHostingPayTradeLoan(String processStatus, String tradeNo) throws Exception;

	/**
	 * 直接代付回调后处理业务
	 * 
	 * @param tradeStatus
	 * @param tradeNo
	 * @return
	 * @throws Exception
	 */
	ResultDO<?> afterDirectHostingPay(String tradeStatus, String tradeNo) throws Exception;

	/**
	 * 通过项目id查询投资笔数
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	int getTransactionCountByProject(Long projectId) throws Exception;
	
	/**
	 * 通过转让项目id查询投资笔数
	 * 
	 * @param transferId
	 * @return
	 * @throws Exception
	 */
	int getTransactionCountByTransferId(Long transferId) throws Exception;
	
	
	/**
	 * 通过项目id查询投资笔数
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	int getTransactionCountByTransferProject(Long projectId) throws Exception;

	/**
	 * 通过项目id查询投资会员数
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	int getTransactionMemberCountByProject(Long projectId) throws Exception;
	
	/**
	 * 通过转让项目id查询投资会员数
	 * 
	 * @param transferId
	 * @return
	 * @throws Exception
	 */
	int getTransactionMemberCountByTransferId(Long transferId) throws Exception;
	

	/**
	 * 通过项目id查询该项目总收益
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	BigDecimal getTotalTransactionInterestByProject(Long projectId) throws Exception;
	
	/**
	 * 通过转让项目id查询该项目总收益
	 * 
	 * @param transferId
	 * @return
	 * @throws Exception
	 */
	BigDecimal getTotalTransactionInterestByTransferId(Long transferId) throws Exception;

	/**
	 * 根据项目id获取最后一笔交易
	 * 
	 * @param projectId
	 * @return
	 */
	Transaction selectLastTransactionByProject(Long projectId) throws Exception;

	/**
	 * 通过项目查询投资额最高的一笔（额度相同取投资时间最早的）
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public Transaction selectMostTransactionByProject(Long projectId) throws Exception;

	/**
	 * 根据项目获取结算额度
	 * 
	 * @param projectId
	 * @param loanStatus
	 * @return
	 * @throws Exception
	 */
	Transaction selectAmountByProject(Long projectId, Integer loanStatus) throws Exception;

	/**
	 * 我是王推荐好友投资额排行榜
	 * 
	 * @return
	 * @throws Exception
	 */
	List<ActivityForKing> getRefferalInvestAmountList() throws Exception;

	/**
	 * 查询投资排行前10
	 * 
	 * @return
	 */
	List<TransactionForFirstInvestAct> selectTopTenInvest() throws Exception;

	/**
	 * 赠送人气值
	 * 
	 * @param senderId
	 * @param memberId
	 * @param income
	 * @param remarks
	 * @return
	 * @throws ManagerException
	 */
	boolean givePopularity(Long senderId, Long memberId, TypeEnum type,BigDecimal income, String remarks) throws ManagerException;

	/**
	 * 用户的交易笔数
	 * 
	 * @param memberId
	 * @return
	 */
	int getTransactionCountByMember(Long memberId) throws Exception;

	/**
	 * 会员的投资总额
	 * 
	 * @param memberId
	 * @return
	 */
	BigDecimal getTotalInvestAmountByMemberId(Long memberId) throws ManagerException;

	/**
	 * 用户首次投资金额
	 * 
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	BigDecimal firstInvestmentAmount(Long memberId, Date startTime, Date endTime) throws Exception;

	/**
	 * 累计投资
	 * 
	 * @param memberId
	 * @return
	 */
	BigDecimal countInvestmentAmount(Long memberId) throws ManagerException;

	/**
	 * 单笔投资最高金额
	 * 
	 * @param memberId
	 * @return
	 */
	BigDecimal largestInvestmentAmount(Long memberId, Date startTime, Date endTime) throws ManagerException;

	/**
	 * 投资项目数量
	 * 
	 * @param memberId
	 * @return
	 */
	int countInvestmentProject(Long memberId) throws ManagerException;

	/**
	 * 获得今天第一位投资用户
	 * 
	 * @return
	 * @throws ManagerException
	 */
	Long getTodayFirstInvestmentMemberId() throws ManagerException;

	/**
	 * 统计好友投资笔数
	 * 
	 * @param memberId
	 * @return
	 */
	public int countFriendsInvestmentNum(Long memberId, Date startTime, Date endTime) throws ManagerException;

	/**
	 * 好友首次投资金额
	 * 
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsFirstInvestmentAmount(Long memberId, Date startTime, Date endTime) throws ManagerException;

	/**
	 * 好友累计投资金额
	 * 
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsCountInvestmentAmount(Long memberId) throws ManagerException;

	/**
	 * 好友单笔投资最大金额
	 * 
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsInvestmentMaxAmount(Long memberId, Date startTime, Date endTime) throws ManagerException;

	/**
	 * 好友累计投资项目
	 * 
	 * @param memberId
	 * @return
	 */
	public int friendsCountInvestmentProjectNum(Long memberId) throws ManagerException;

	/**
	 * 项目第一个投资用户
	 * 
	 * @param projectId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ManagerException
	 */
	public Long firstInvestmentProjectMember(Long projectId, Date startTime, Date endTime) throws ManagerException;

	/**
	 * 项目最后一个投资用户
	 * 
	 * @param projectId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ManagerException
	 */
	public Long lastInvestmentProjectMember(Long projectId, Date startTime, Date endTime) throws ManagerException;

	/**
	 * 单笔投资最大金额
	 * 
	 * @param projectId
	 * @param memberId
	 * @return
	 */
	public BigDecimal investmentMaxAmountProject(Long projectId, Long memberId) throws ManagerException;

	/**
	 * 好友单笔投资最大金额
	 * 
	 * @param projectId
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsInvestmentMaxAmountProject(Long projectId, Long memberId) throws ManagerException;

	/**
	 * 项目整点投资
	 * 
	 * @param projectId
	 * @param memberId
	 * @param punctuality
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public BigDecimal integralPointInvestmentProject(Long projectId, Long memberId, String punctuality, Date startTime,
			Date endTime) throws ManagerException;

	/**
	 * 整点投资
	 * 
	 * @param memberId
	 * @param punctuality
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public BigDecimal integralPointInvestment(Long memberId, String punctuality, Date startTime, Date endTime)
			throws ManagerException;

	/**
	 * 根据map 查询投资数据
	 * 
	 * @param map
	 * @return
	 */
	List<Transaction> getTransactionByMap(@Param("map") Map map) throws ManagerException;
	
	/**
	 * 获得用户交易明细
	 * @param transactionId
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public TransactionForMemberCenter getTransactionDetailForMember(Long transactionId, Long memberId) throws ManagerException;

	/**
	 * 获取满足投资总额在某一范围内的用户
	 * 
	 * @param map
	 * @return
	 */
	public List<TransactionForFirstInvestAct> getMemberMeetTotalInvestRange(Map<String, Object> map)
			throws ManagerException;

	public int getCountMemberMeetTotalInvestRange(Map<String, Object> map) throws ManagerException;
	
	/**
	 * 获得用户的交易总笔数
	 * @param memberId
	 * @param status
	 * @return
	 * @throws ManagerException
	 */
	public int getTransactionsTotalCount(Long memberId, int status) throws ManagerException;

	public void generateContractfinal(Long transactionId, String fromSys);
	/**
	 * 投资分页查询
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	Page<Transaction> findByPage(Page<Transaction> pageRequest, Map<String, Object> map) throws ManagerException;
	
	/**
	 * 查询没有创建保全的交易订单
	 * 
	 * @author wangyanji
	 * @param maxNum
	 * @return
	 * @throws ManagerException
	 */
	public List<Transaction> queryNotPreservTransForList(Map<String, Object> map) throws ManagerException;

	/**
     * 交易分页查询
     * 
     * @param pageRequest
     * @param map
     * @return
     * @throws ManagerException
     */
	Page<TransactionForOrder> queryTansactionForList(Page<TransactionForOrder> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * 根据项目ID获取投资人信息
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	public List<TransactionForMemberCenter> selectTransactionMemberByPorjectId(Long projectId) throws ManagerException;

	/**
	 * 判断用户是否投资
	 *
	 * @param memberId
	 * @return
	 */
	public boolean isMemberInvested(Long memberId) throws ManagerException;
	
	/**
	 * 判断用户是否投资过直投项目
	 *
	 * @param memberId
	 * @return
	 */
	public boolean isMemberDirectInvest(Long memberId) throws ManagerException;
	
	
	/**
	 * 统计用户租赁分红总额
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public BigDecimal totalMemberLeaseBonusAmounts(Long memberId) throws ManagerException;
	
	/**
	 *  查看我排名
	 * @param map
	 * @return
	 */
	public ActivityMonthlyMyRank findMyRank(Map<String, Object> map)throws ManagerException;

	/**
	 * 统计用户投资使用优惠券的总金额
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public BigDecimal totalMemberUsedCouponAmount(Long memberId) throws ManagerException;
	
	/**
	 * 统计用户使用优惠券数量
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public int totalMemberUsedCouponNum(Long memberId) throws ManagerException;
	
	/**
	 * 用户首次投资详情
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public Transaction getFirstTransaction(Long memberId) throws ManagerException;
	/**
     * 通过条件查询项目详情页交易记录
     * @param pageRequest
     * @param map
     * @return
     */
	public List<TransactionForProject> selectTransactionDetailByQueryParams(TransactionQuery transactionQuery) throws ManagerException;
	
	/**
	 * 指定债券类型的投资列表
	 * @param param
	 * @return
	 */
	public List<Transaction> getInvestHouseList(ActivityForInvest param) throws ManagerException;
	
	/**
	 * 投房有喜我的排名
	 * @param param
	 * @return
	 * @throws ManagerException
	 */
	public ActivityForInvest getMyInvestHouseRank(ActivityForInvest param) throws ManagerException;
	
	
	/**
	 * 查看用户投资本金详情
	 * @param query
	 * @return
	 */
	public List<TransactionInterestForMember>  getTransactionsDetailForMember(TransactionInterestQuery query)throws ManagerException;
	
	/**
	 * 
	 * @Description:活动项目交易查询
	 * @param query
	 * @author: wangyanji
	 * @time:2016年1月25日 上午11:27:23
	 */
	public TransactionForActivity selectForProjectExtra(TransactionForActivity query) throws ManagerException;
	
	/**
	 * 
	 * @Description:根据projectId查询投资总额
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月11日 上午10:18:32
	 */
	BigDecimal getTotalInvestAmountByProjectId(Long projectId) throws ManagerException;

	/**
	 * 根据项目的还款方式和周期,计算投资者的本息记录
	 * @param debtInterests
	 * @param project
	 * @return
	 */
	public List<TransactionInterest> computeInvesterPrincipalAndInterest(Transaction transaction,List<DebtInterest> debtInterests, Project project) throws ManagerException;

	/**
	 * @Description:直投项目放款
	 * @param projectId
	 * @param totalBorrowAmount
	 * @param manangerFee
	 * @author: fuyili
	 * @time:2016年2月1日 下午5:17:00
	 */
	public void createHostingPayForDirectProjectLoan(Long projectId, BigDecimal totalBorrowAmount, BigDecimal managerFee,
			BigDecimal guaranteeFee, BigDecimal riskFee, BigDecimal introducerFee,List<QuickRewardForPay> quickReForPayList);
	
	/**
	 * @Description:创建放款代付
	 * @param projectId
	 * @param totalUsedCapitalAmount
	 * @param hostingCollectTrade
	 * @param transactions
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月1日 下午4:51:31
	 */
	void createHostPayForLoan(Long projectId,BigDecimal totalUsedCapitalAmount,List<Transaction> transactions,HostingCollectTrade hostingCollectTrade)throws ManagerException;
	
	
	
	/**
	 * @Description:退款处理
	 * @param processStatus
	 * @param tradeNo
	 * @return
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年2月17日 下午3:15:31
	 */
	ResultDO<?> afterHostingRefund(String processStatus, String tradeNo, String outTradeNo) throws Exception;
	/**
	 * 
	 * @Description:过滤p2p
	 * @param transactionQuery
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月8日 上午10:58:10
	 */
	List<Transaction> selectTransactionsByQueryParamsFilterP2p(TransactionQuery transactionQuery)throws ManagerException;
	
	/**
	 * 
	 * @Description:p2p合同展示
	 * @param orderId ，memberId
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年3月11日 上午9:25:10
	 */
	public ResultDO<ContractBiz> p2pViewContract(Long orderId, Long memberId)throws ManagerException;
	
	/**
	 * 
	 * @Description:M站累计投资本金，过滤P2P
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年3月21日 上午9:16:10
	 */
	public BigDecimal getMemberTotalInvestAmountDirectProject(Long memberId) throws ManagerException;
	/**
	 * 
	 * @Description:获取会员出借人身份名下的债权项目 
	 * @param debtForLenderQuery
	 * @return
	 * @author: zhanghao
	 * @time:2016年3月23日 上午16:17:56
	 */
	public Page<DebtForLenderMember> getDebtInfoByLenderId(DebtForLenderQuery debtForLenderQuery) throws ManagerException;
	/**
	 * 
	 * @Description:获取会员出借人身份名下的债权项目数量，本息金额
	 * @param debtForLenderQuery
	 * @return
	 * @author: zhanghao
	 * @time:2016年3月24日 上午15:53:56
	 */
	public DebtForLenderMember getStatisticalDataByLenderId(DebtForLenderQuery debtForLenderQuery) throws ManagerException;
	
	/**
	 * 
	 * @Description:查询用户时间范围内的总投资额
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年4月18日 下午2:45:48
	 */
	public BigDecimal getMemberTotalInvest(TransactionQuery transactionQuery) throws ManagerException;

	/**
	 * 查询交易列表（包含募集中）
	 * 
	 * @param query
	 * @return
	 */
	Page<TransactionForMemberCenter> selectAllTransactionWithP2P(TransactionQuery query) throws ManagerException;

	/**
	 * 
	 * @Description: 项目代收完成后续交易处理
	 * @param project
	 * @param interests
	 * @param totalDays
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年5月29日 下午1:43:33
	 */
	public void afterDirectProjectFinishCollectTradeProcess(Project project, List<DebtInterest> interests, int totalDays) throws Exception;
	/**
	 * 
	 * @Description:五重礼人气值
	 * @param activityId
	 * @param days
	 * @param popularityValue
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年6月7日 下午5:43:27
	 */
	public String getPopularityValue(String activityId, int days, String popularityValue) throws Exception;
	
	/**
	 * 
	 * @Description: 用户是否投资过某项目
	 * @param ProjectId
	 * @param memberId
	 * @throws ManagerException
	 * @author: ZHANGHAO
	 * @time:2016年6月7日 下午15:19:33
	 */
	public boolean isMemberInvestedProject(Long projectId,Long memberId) throws ManagerException;
	/**
	 * 
	 * @Description:通过项目id查询该项目实际总收益
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年6月13日 下午4:06:58
	 */
	BigDecimal getTotalTransactionReceivedInterestByProject(Long projectId) throws ManagerException;
	
	/**
	 * 
	 * @Description:通过转让项目id查询该项目实际总收益
	 * @param transferId
	 * @return
	 * @author: zhanghao
	 * @throws ManagerException 
	 * @time:2016年9月28日 下午4:06:58
	 */
	public BigDecimal getTotalTransactionReceivedInterestByTransferId(Long transferId) throws ManagerException;
	
	/**
	 * 
	 * @Description:根据会员id和时间查询用户投资总额
	 * @param memberId
	 * @param transactionTime
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月27日 下午5:56:20
	 */
	BigDecimal getTotalAmountByMemberId(Long memberId,Date transactionTime) throws ManagerException;
	/**
	 * 
	 * @Description:获取用户未签署合同数量
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午9:59:11
	 */
	public int getUnsignContractNum(Long memberId) throws ManagerException;

	/**
	 * 
	 * @Description:跟新交易时间
	 * @param record
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年6月30日 下午3:30:56
	 */
	int updateTransactionTimeByPrimaryKey(Transaction record) throws ManagerException;

	void addTransactionDetailForIndexQuadruplegifts(Transaction luckTransaction,
			boolean isFirstInvest, boolean isLast, boolean b, boolean c,
			Project project, Member memberLuck);
	/**
	 * 最后一笔交易
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	Transaction selectLastTransactionByProjectOrderById(Long projectId)throws ManagerException;

	/**
	 * 
	 * @Description:一键签署
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午17:31:11
	 */
	public int signAllContract(Long memberId) throws ManagerException;
	
	public int signAllContractByType(Long memberId,Integer type) throws ManagerException;
	
	/**
	 * 
	 * @Description:手动签署
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午17:31:11
	 */
	public String signContract(Long transactionId,int typeDevice,Integer source) throws ManagerException;
	
	/**
	 * 
	 * @Description:查询未签署交易信息
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月13日 上午10:31:11
	 */
	public Page<Transaction> queryUnsignList(TransactionQuery query) throws ManagerException ;
	
	/**
	 * 
	 * @Description:交易后开启合同初始化业务
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月13日 上午10:31:11
	 */
	public void afterTransactionContract(Transaction transaction);
	/**
	 * 
	 * @Description:交易后开启合同初始化业务-全额使用现金券
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月13日 上午10:31:11
	 */
	public void afterTransactionContractIsFullCoupon(Order order);
	/**
	 * 
	 * @Description:自动签署
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午17:31:11
	 */
	public int signContractAuto(Long transactionId) throws ManagerException;
	
	/**
	 * 
	 * @Description:合同下载链接
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午17:31:11
	 */
	public String getContractDownUrl(Long transactionId) throws ManagerException ;
	/**
	 * 
	 * @Description:查询用户募集中未签署数量
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午17:31:11
	 */
	public int getCollectSignableContractNum(Long memberId) throws ManagerException;
	/**
	 * 
	 * @Description:查询用户我的交易未签署数量
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午17:31:11
	 */
	public int getTransactionSignableContractNum(Long memberId) throws ManagerException;
	/**
	 * 
	 * @Description:合同置为已过期
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午17:31:11
	 */
	public int expireContract(Long projectId) throws ManagerException;
	
	/**
	 * 
	 * @Description:合同置为已过期
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月24日 下午17:31:11
	 */
	public int expireContractForTransfer(Long transferId) throws ManagerException;
	
	/**
	 * 
	 * @Description:历史合同置为已过期
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月25日 上午17:31:11
	 */
	public int expireHistoryContract() throws ManagerException;

	/**
	 * 
	 * @Description:获取未完结交易
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月25日 上午17:31:11
	 */
	public List<Transaction> getUnendTransaction() throws ManagerException;
	
	/**
	 * 
	 * @Description:同步签署信息
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午17:31:11
	 */
	ResultDO<Object> queryContractInfo(Long transactionId);
	
	/**
	 * 
	 * @Description:查询昨日未签署的用户
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年8月2日 上午17:31:11
	 */
	public List<Long> selectUnSignMember() throws ManagerException;
	
	/**
	 * 
	 * @Description:累计投资前8用户
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月11日 下午2:46:11
	 */
	List<TransactionForFirstInvestAct> selectTopEightInvestAmount(Date startTime, Date endTime) throws ManagerException;
	/**
	 * 
	 * @Description:赛奥运列表
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年7月11日 下午3:10:22
	 */
	List<TransactionForFirstInvestAct> selectTopTenInvestAmount(Date startTime, Date endTime)throws ManagerException;
	/**
	 * 
	 * @Description:根据id和时间查询投资总额
	 * @param memberId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月11日 下午3:26:42
	 */
	BigDecimal getMyTotalInvestByMemberIdAndTime(Long memberId, Date startTime, Date endTime) throws ManagerException;
	
	/**
	 * 非委托支付退款/解冻
	 * 
	 * @param tradeStatus
	 * @param orderForLock
	 * @param hostingCollectTrade
	 * @throws ManagerException
	 */
	public ResultDO<Object> isWithholdAuthorityTradeFailedAndClosed(String tradeStatus, Order orderForLock, HostingCollectTrade hostingCollectTrade, SummaryEnum summary)
			throws ManagerException;
	/**
	 * 获取用户回款笔数
	 * @param memberId
	 * @param status
	 * @return
	 * @throws ManagerException 
	 */
	int getCountInterestByMemberId(Long memberId, int status,Date date) throws ManagerException;

	/**
	 * 
	 * @Description:累计交易笔数
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年8月28日 上午11:04:10
	 */
	public int getTotalTransactionCount() throws ManagerException;
	

	Page<TraceSourceCollectBiz> queryByPageCollectByTraceSource(TraceSourceCollectQuery query);
	
	/**
	 * 
	 * @param 待回款金额
	 * @param date
	 * @return
	 * @throws ManagerException
	 */
	TransactionInterest selectWaitPayByEndDate(int status, Date date) throws ManagerException;

	TransactionInterest selectRealPayByEndDate(Date date)throws ManagerException;

	BigDecimal getMemberTotalInvestByStatus(TransactionQuery transactionQuery)
			throws ManagerException;
	
	
	public Page<TransferRecordBiz> transferList(TransferRecordQuery query);
	
	public TransactionTransferRecordBiz transactionTransferList(TransferRecordQuery query) throws ManagerException;
	
	
	
	/**
	 * 根据转让项目ID更新交易
	 * 
	 * @param transaction
	 * @return
	 * @throws ManagerException
	 */
	Integer updateByTransferId(Transaction transaction) throws ManagerException;
	
	
	/**
	 * 
	 * @Description:转让直投合同展示
	 * @param orderId ，memberId
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年9月23日 上午9:25:10
	 */
	public ResultDO<TransferContractBiz> viewZtTransferContract(Long orderId, Long memberId)throws ManagerException;
	
	/**
	 * 
	 * @Description:转让债权合同展示
	 * @param orderId ，memberId
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年9月23日 上午9:25:10
	 */
	public ResultDO<TransferContractBiz> transferContract(Long orderId, Long memberId)throws ManagerException;
	
	
	public List<Transaction> selectByTransferId(Long transferId) throws ManagerException ;
	
	
	
	public int selectCollectForMemberCounting(CollectingProjectQuery query)throws ManagerException;
	
	/**
	 * 
	 * @Description:投资前十
	 * @param startTime
	 * @param endTime
	 * @param id
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年10月9日 下午3:19:48
	 */
	List<TransactionForFirstInvestAct> selectTopTenInvestAmountByActivityId(Date startTime, Date endTime, Long id) throws ManagerException;

	List<TransactionInterestForMember> getTransactionInterestDetailForMemberTransfer(
			TransactionInterestQuery query)throws ManagerException ;
	

	List<TransferProject> getTransferDetailForMember(
			TransactionInterestQuery query)throws ManagerException;
	
	
	/**
	 * 
	 * @Description:募集中的金额
	 * @param date
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年10月24日 下午2:25:18
	 */
	BigDecimal getTotalInvestAmount(Date date) throws ManagerException;
	/**
	 * 
	 * @Description:历史投资总额
	 * @param pageSize
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年10月24日 下午3:55:59
	 */
	List<Transaction> getTotalInvestAmountList(int pageSize) throws ManagerException;
	/**
	 * 
	 * @Description:用户首次投资信息
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年10月28日 下午1:01:30
	 */
	Transaction firstInvestmentByMemberId(Long memberId)throws ManagerException;
	/**
	 * 
	 * @Description:总共赚取收益
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年11月4日 下午5:36:58
	 */
	Transaction totalInvestInterest(Long memberId)throws ManagerException;

	BigDecimal getMemberTotalInvestByMemberId(Long memberId, Date startTime, Date endTime, Integer totalDays) throws ManagerException;

	BigDecimal getMemberTotalInvestByMemberIdAndTotalDays(Map<String, Object> map) throws ManagerException;

	
	BigDecimal getInvestAmountByMemberId(Long memberId, Long activityId ,Date endTime,Date startTime)throws ManagerException;

	BigDecimal getInvestAmountByMemberIdAndTime(Long memberId, Date startTime, Date endTime ,Date activityStartTime)throws ManagerException;

	List<TransactionForFirstInvestAct>  selectTopInvestAmountByActivityIdAndTime(Date startTime, Date endTime, Date activityStartTime) throws ManagerException;

	List<TransactionForFirstInvestAct> getEverydayFirstAmount(Long activityId, Date startTime, Date endTime);

	List<TransactionForFirstInvestAct> getCountEverydayFirstAmount(Long activityId, Date startTime, Date endTime);

	/**
	 * 查询用户时间区间内投资数
	 * @param memberid
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public int queryMemberTransactionCount(Long memberid,Date starttime,Date endtime);
	List<TransactionForFirstInvestAct> getFirstEightInvestAmount(Long id, Date endTime);

	/**
	 * 获得交易手续费率
	 * @param transactionId
	 * @return
	 */
	BigDecimal getTransferRate(Long transactionId);

	Page<TransactionProjectBiz> queryPageTransactionProjectBiz(TransactionProjectBizQuery query);

	/**
	 * 查询交易转让累计收益
	 * @return
     */
	BigDecimal queryTransactionTotalIncome(Long transactionid);

	/**
	 * 查询交易转让投资条数
	 * @param transactionid
	 * @return
	 */
	int queryTransactionTransferCount(Long transactionid);

	/**
	 * @param memberId
	 * @param transactionStartTime
	 * @param transactionEndTime
	 * @return
	 * @throws ManagerException
	 */
	BigDecimal findTotalInvestAmount(Long memberId, Date transactionStartTime,
			Date transactionEndTime) throws ManagerException;
	/**
	 * 记录用户代收记录
	 * @param projectId
	 */
	public  void incrMemberRepayment(Long projectId);

}