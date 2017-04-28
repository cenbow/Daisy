package com.yourong.core.tc.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.model.query.TransferRecordQuery;
import com.yourong.core.mc.model.biz.ActivityFirstInvest;
import com.yourong.core.mc.model.biz.ActivityForInvest;
import com.yourong.core.mc.model.biz.ActivityForKing;
import com.yourong.core.mc.model.biz.ActivityMonthlyMyRank;
import com.yourong.core.os.biz.TransactionsForOpen;
import com.yourong.core.tc.model.ContractSignDto;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.DebtForLenderMember;
import com.yourong.core.tc.model.biz.TraceSourceCollectBiz;
import com.yourong.core.tc.model.biz.TransactionForActivity;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.biz.TransactionForOrder;
import com.yourong.core.tc.model.biz.TransactionForProject;
import com.yourong.core.tc.model.biz.TransactionInterestForMember;
import com.yourong.core.tc.model.biz.TransactionProjectBiz;
import com.yourong.core.tc.model.query.CollectingProjectQuery;
import com.yourong.core.tc.model.query.DebtForLenderQuery;
import com.yourong.core.tc.model.query.TraceSourceCollectQuery;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionProjectBizQuery;
import com.yourong.core.tc.model.query.TransactionQuery;

@Resource
public interface TransactionMapper {

	/**
	 * 插入交易
	 * 
	 * @param record
	 * @return
	 */
	int insertSelective(Transaction record);

	/**
	 * 通过id查询交易
	 * 
	 * @param id
	 * @return
	 */
	Transaction selectByPrimaryKey(Long id);

	/**
	 * 更新交易
	 * 
	 * @param record
	 * @return
	 */
	int updateByPrimaryKeySelective(Transaction record);

	/**
	 * 通过条件查询项目详情页交易记录
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	List<TransactionForProject> selectTransactionForProjectsByQueryParams(
			TransactionQuery transactionQuery);

	/**
	 * 通过条件查询项目详情页交易记录总数
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	int selectTransactionForProjectsByQueryParamsTotalCount(
			TransactionQuery transactionQuery);

	/**
	 * 通过条件查询交易记录
	 * 
	 * @param transactionQuery
	 * @return
	 */
	List<Transaction> selectTransactionsByQueryParams(
			TransactionQuery transactionQuery);

	/**
	 * 通过条件查询交易记录总数
	 * 
	 * @param transactionQuery
	 * @return
	 */
	int selectTransactionsByQueryParamsTotalCount(
			TransactionQuery transactionQuery);

	/**
	 * 查询最新投资记录
	 * 
	 * @return
	 */
	List<TransactionForProject> selectNewTransactions(
			@Param("size") int pageSize);

	/**
	 * 更新交易表中已收利息和本金
	 * 
	 * @param transactionQuery
	 * @return
	 */
	int updateReceivedInterestAndPrincipal(TransactionQuery transactionQuery);

	/**
	 * 查询加锁的交易信息
	 * 
	 * @param id
	 * @return
	 */
	Transaction selectByPrimaryKeyLock(Long id);

	/**
	 * 查询条件查询会员中心交易记录
	 * 
	 * @param transactionQuery
	 * @return
	 */
	List<TransactionForMemberCenter> selectAllTransactionForMember(
			TransactionQuery transactionQuery);

	/**
	 * 查询条件查询会员中心交易笔数
	 * 
	 * @param transactionQuery
	 * @return
	 */
	long selectAllTransactionForMemberCount(TransactionQuery transactionQuery);

	/**
	 * 获取交易详情
	 * 
	 * @param transactionId
	 * @return
	 */
	TransactionForMemberCenter getTransactionForMember(@Param("transactionId")Long transactionId,@Param("memberId")Long memberId);

	/**
	 * 查询条件查询会员中心交易记录,剔除P2P项目数据
	 * @param transactionQuery
	 * @return
	 */
	List<TransactionForMemberCenter> p2pSelectAllTransactionForMember(
			TransactionQuery transactionQuery);
	/**
	 *  查询条件查询会员中心交易笔数,剔除p2p项目数据
	 * @param transactionQuery
	 * @return
	 */
	long p2pSelectAllTransactionForMemberCount(TransactionQuery transactionQuery);
	/**
	 * 通过订单id获取交易信息
	 * 
	 * @param orderId
	 * @return
	 */
	Transaction getTransactionByOrderId(Long orderId);

	int getTransactionCount(@Param("memberId") Long memberId,
			@Param("type") int type);

	/**
	 * 通过项目id查询使用优惠券总额
	 * 
	 * @param projectId
	 * @return
	 */
	BigDecimal getCouponAmountForPaltform(@Param("projectId") Long projectId);

	/**
	 * 通过项目id查询投资总额
	 * 
	 * @param projectId
	 * @return
	 */
	BigDecimal getTotalAmountByProject(@Param("projectId") Long projectId);

	/**
	 * 通过项目id查询投资笔数
	 * 
	 * @param projectId
	 * @return
	 */
	int getTransactionCountByProject(@Param("projectId") Long projectId);
	
	/**
	 * 通过转让项目id查询投资笔数
	 * 
	 * @param transferId
	 * @return
	 */
	int getTransactionCountByTransferId(@Param("transferId") Long transferId);

	/**
	 * 通过项目id查询投资笔数
	 * 
	 * @param projectId
	 * @return
	 */
	int getTransactionCountByTransferProject(@Param("projectId") Long projectId);
	
	/**
	 * 通过项目id查询该项目总收益
	 * 
	 * @param projectId
	 * @return
	 */
	BigDecimal getTotalTransactionInterestByProject(
			@Param("projectId") Long projectId);
	
	/**
	 * 通过转让项目id查询该项目总收益
	 * 
	 * @param projectId
	 * @return
	 */
	BigDecimal getTotalTransactionInterestByTransferId(
			@Param("transferId") Long transferId);
	
	
	/**
	 * 通过项目id查询该项目已收取收益
	 * 
	 * @param projectId
	 * @return
	 */
	BigDecimal getTotalTransactionReceivedInterestByProject(
			@Param("projectId") Long projectId);
	
	/**
	 * 通过项目id查询该项目已收取收益
	 * 
	 * @param projectId
	 * @return
	 */
	BigDecimal getTotalTransactionReceivedInterestByTransferId(
			@Param("transferId") Long transferId);
	

	/**
	 * 通过项目id查询投资会员数
	 * 
	 * @param projectId
	 * @return
	 */
	int getTransactionMemberCountByProject(@Param("projectId") Long projectId);
	
	/**
	 * 通过转让项目id查询投资会员数
	 * 
	 * @param projectId
	 * @return
	 */
	int getTransactionMemberCountByTransferId(@Param("transferId") Long transferId);

	List<ActivityForKing> getRefferalInvestAmountList();

	/**
	 * 根据项目id获取最后一笔交易
	 * 
	 * @param projectId
	 * @return
	 */
	Transaction selectLastTransactionByProject(
			@Param("projectId") Long projectId);

	/**
	 * 根据项目获取结算额度
	 * 
	 * @param projectId
	 * @param loanStatus
	 * @return
	 */
	Transaction selectAmountByProject(@Param("projectId") Long projectId,
			@Param("loanStatus") Integer loanStatus);

	/**
	 * 通过交易号查询现金券总额
	 * 
	 * @param tradeNo
	 * @return
	 */
	BigDecimal getTotalCouponAmountByQuery(TransactionQuery transactionQuery);

	int updatePayTradeNoByCollectTradeNo(
			@Param("collectTradeNo") String collectTradeNo,
			@Param("payTradeNo") String payTradeNo);

	int updateLoanStatusByPayTradeNo(@Param("payTradeNo") String payTradeNo,
			@Param("loanStatus") Integer loanStatus);

	BigDecimal getTotalAmountByQuery(TransactionQuery transactionQuery);

	/**
	 * 通过项目查询投资额最高的一笔（额度相同取投资时间最早的）
	 * 
	 * @param projectId
	 * @return
	 */
	Transaction selectMostTransactionByProject(
			@Param("projectId") Long projectId);
	
	/**
	 * 通过项目查询投资额最高的一笔（额度相同取投资时间最早的）
	 * 
	 * @param projectId
	 * @return
	 */
	Transaction selectMostTransactionBytransferId(
			@Param("transferId") Long transferId);

	List<TransactionForFirstInvestAct> selectTopTenInvest();

	/**
	 * 获取项目投资日期，及该日期投资总额
	 * 
	 * @param projectId
	 * @return
	 */
	List<Transaction> findDayTransactionByProject(
			@Param("projectId") Long projectId);

	/**
	 * 用户的交易笔数
	 * 
	 * @param memberId
	 * @return
	 */
	int getTransactionCountByMember(@Param("memberId") Long memberId);

	/**
	 * 会员的投资总额
	 * 
	 * @param memberId
	 * @return
	 */
	BigDecimal getTotalInvestAmountByMemberId(@Param("memberId") Long memberId);

	/**
	 * 根据MAP 查询投资记录
	 * 
	 * @param map
	 * @return
	 */
	List<Transaction> getTransactionByMap(@Param("map") Map<String, Object> map);

	/**
	 * 首次投资金额
	 * 
	 * @param memberId
	 * @return
	 */
	BigDecimal firstInvestmentAmount(@Param("memberId") Long memberId,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 累计投资
	 * 
	 * @param memberId
	 * @return
	 */
	BigDecimal countInvestmentAmount(@Param("memberId") Long memberId);

	/**
	 * 单笔投资最高金额
	 * 
	 * @param memberId
	 * @return
	 */
	BigDecimal largestInvestmentAmount(@Param("memberId") Long memberId,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 投资项目数量
	 * 
	 * @param memberId
	 * @return
	 */
	int countInvestmentProject(@Param("memberId") Long memberId);

	/**
	 * 获得今天第一位投资用户
	 * 
	 * @return
	 */
	Long getTodayFirstInvestmentMemberId();

	/**
	 * 统计好友投资笔数
	 * 
	 * @param memberId
	 * @return
	 */
	public int countFriendsInvestmentNum(@Param("memberId") Long memberId,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 好友首次投资金额
	 * 
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsFirstInvestmentAmount(
			@Param("memberId") Long memberId,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 好友累计投资金额
	 * 
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsCountInvestmentAmount(
			@Param("memberId") Long memberId);

	/**
	 * 好友单笔投资最大金额
	 * 
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsInvestmentMaxAmount(
			@Param("memberId") Long memberId,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 好友累计投资项目
	 * 
	 * @param memberId
	 * @return
	 */
	public int friendsCountInvestmentProjectNum(@Param("memberId") Long memberId);

	/**
	 * 项目第一个投资用户
	 * 
	 * @param projectId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Long firstInvestmentProjectMember(
			@Param("projectId") Long projectId,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 项目最后一个投资用户
	 * 
	 * @param projectId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Long lastInvestmentProjectMember(@Param("projectId") Long projectId,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 单笔投资最大金额
	 * 
	 * @param projectId
	 * @param memberId
	 * @return
	 */
	public BigDecimal investmentMaxAmountProject(
			@Param("projectId") Long projectId, @Param("memberId") Long memberId);

	/**
	 * 好友单笔投资最大金额
	 * 
	 * @param projectId
	 * @param memberId
	 * @return
	 */
	public BigDecimal friendsInvestmentMaxAmountProject(
			@Param("projectId") Long projectId, @Param("memberId") Long memberId);

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
	public BigDecimal integralPointInvestmentProject(
			@Param("projectId") Long projectId,
			@Param("memberId") Long memberId,
			@Param("punctuality") String punctuality,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 整点投资
	 * 
	 * @param memberId
	 * @param punctuality
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public BigDecimal integralPointInvestment(@Param("memberId") Long memberId,
			@Param("punctuality") String punctuality,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 获得用户交易明细
	 * 
	 * @param transactionId
	 * @param memberId
	 * @return
	 */
	public TransactionForMemberCenter getTransactionDetailForMember(
			@Param("transactionId") Long transactionId,
			@Param("memberId") Long memberId);

	/**
	 * 获取满足投资总额在某一范围内的用户
	 * 
	 * @param map
	 * @return
	 */
	public List<TransactionForFirstInvestAct> getMemberMeetTotalInvestRange(
			@Param("map") Map<String, Object> map);

	/**
	 * 获取满足投资总额在某一范围内的用户总数
	 * 
	 * @param map
	 * @return
	 */
	public int getCountMemberMeetTotalInvestRange(
			@Param("map") Map<String, Object> map);

	/**
	 * 投资分页查询
	 * @param map
	 * @return
	 */
	List<Transaction> findByPage(@Param("map") Map<String, Object> map);
	
	
	/**
	 * 投资分页查询数量
	 * @param map
	 * @return
	 */
	int findByPageCount(@Param("map") Map<String, Object> map);

	/**
	 * 用户首次投资
	 * 
	 * @param memberId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Transaction appFirstInvestmentForMember(
			@Param("memberId") Long memberId,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 查询没有创建保全的交易订单
	 * 
	 * @author wangyanji
	 * @param maxNum
	 * @return
	 */
	public List<Transaction> queryNotPreservTransForList(
			@Param("map") Map<String, Object> map);

	/**
	 * 交易分页查询
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	List<TransactionForOrder> queryTansactionForList(
			@Param("map") Map<String, Object> map);
	
	/**
	 * 交易分页查询数量
	 * 
	 * @param map
	 * @return
	 */
	int queryTansactionForListCount(
			@Param("map") Map<String, Object> map);

	/**
	 * 根据项目ID获取投资人信息
	 * 
	 * @param projectId
	 * @return
	 */
	public List<TransactionForMemberCenter> selectTransactionMemberByPorjectId(
			@Param("projectId") Long projectId);

	/**
	 * 判断会员是否投资过
	 * 
	 * @param memberId
	 * @return
	 */
	Integer isMemberInvest(@Param("memberId") Long memberId);

	/**
	 * 判断会员是否投资过直投项目
	 * @param memberId
	 * @return
	 */
	Integer  isMemberDirectInvest(@Param("memberId")Long memberId);
	
	
	/**
	 * 根据项目id获取第一笔交易
	 * 
	 * @param projectId
	 * @return
	 */
	Transaction selectFirstTransactionByProject(
			@Param("projectId") Long projectId);

	/**
	 * 根据项目id和用户id获取获取幸运女神的交易
	 * 
	 * @param projectId
	 * @param memberId
	 * @return
	 */
	public Transaction selectLuckTransactionByProject(
			@Param("projectId") Long projectId, @Param("memberId") Long memberId);

	/**
	 * 统计用户租赁分红总额
	 * 
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public BigDecimal totalMemberLeaseBonusAmounts(
			@Param("memberId") Long memberId);

	/**
	 * 统计用户投资使用优惠券的总金额
	 * 
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public BigDecimal totalMemberUsedCouponAmount(
			@Param("memberId") Long memberId);

	/**
	 * 统计用户使用优惠券数量
	 * 
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public Integer totalMemberUsedCouponNum(@Param("memberId") Long memberId);

	/**
	 * 用户首次投资详情
	 * 
	 * @param memberId
	 * @return
	 */
	public Transaction getFirstTransaction(Long memberId);

	/**
	 * 查看我排名
	 * 
	 * @param map
	 * @return
	 */
	ActivityMonthlyMyRank findMyRank(@Param("map") Map<String, Object> map);

	/**
	 * 通过条件查询项目详情页交易记录 没有分页
	 * 
	 * @param transactionQuery
	 * @return
	 */
	List<TransactionForProject> selectTransactionDetailByQueryParams(
			TransactionQuery transactionQuery);

	/**
	 * 通过交易id查询交易记录
	 * 
	 * @param id
	 * @return
	 */
	TransactionForProject selectTransactionDetailById(@Param("id") Long id);

	/**
	 * 获取指定债权类型的投资列表
	 * 
	 * @param param
	 * @return
	 */
	List<Transaction> getInvestHouseList(ActivityForInvest param);

	/**
	 * 投房有喜我的排名
	 * 
	 * @param param
	 * @return
	 */
	ActivityForInvest getMyInvestHouseRank(ActivityForInvest param);

	/**
	 * 查看用户投资本金详情
	 * 
	 * @param query
	 * @return
	 */
	public List<TransactionInterestForMember> getTransactionsDetailForMember(
			@Param("query") TransactionInterestQuery query);

	/**
	 * 根据项目ID查询投资成功的用户
	 * 
	 * @author wangyanji
	 * @param maxNum
	 * @return
	 */
	List<Long> selectMemberByProject(@Param("map") Map<String, Object> map);

	/**
	 * 查询投资成功的用户
	 * 
	 * @author wangyanji
	 * @param maxNum
	 * @return
	 */
	List<Long> selectMemberSucceed(@Param("map") Map<String, Object> map);

	
	
	
	/**
	 * 
	 * @Description:活动项目交易查询
	 * @param query
	 * @author: wangyanji
	 * @time:2016年1月25日 上午11:27:23
	 */
	TransactionForActivity selectForProjectExtra(TransactionForActivity query);
	
	/**
	 * 
	 * @Description:根据projectId查询投资总额
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年1月11日 上午10:20:39
	 */
	BigDecimal getTotalInvestAmountByProjectId(@Param("projectId")Long projectId);
	
	/**
	 * @Description:更新交易的放款状态以及投资状态
	 * @param payTradeNo
	 * @param loanStatus
	 * @return
	 * @author: fuyili
	 * @time:2016年1月29日 下午4:33:19
	 */
	int updateTransactionByProjectId(Transaction t);
	
	
	/**
	 * @Description:更新交易状态根据订单id
	 * @param projectId
	 * @param status
	 * @return
	 * @author: fuyili
	 * @time:2016年2月17日 下午4:05:23
	 */
	int updateStatusByOrderId(@Param("afterStatus")Integer afterStatus,@Param("beforeStatus")Integer beforeStatus,@Param("remarks")String remarks,@Param("orderId")Long orderId);
	/**
	 * 
	 * @Description:过滤p2p
	 * @param transactionQuery
	 * @return
	 * @author: chaisen
	 * @time:2016年3月8日 上午10:59:56
	 */
	List<Transaction> selectTransactionsByQueryParamsFilterP2p(TransactionQuery transactionQuery);
	
	/**
	 * M站 用户累计投资本金，过滤P2P
	 * 
	 * @param memberId
	 * @return
	 */
	BigDecimal getMemberTotalInvestAmountDirectProject(@Param("memberId") Long memberId);
	/**
	 * 
	 * @Description:获取会员出借人身份名下的债权项目 
	 * @param debtForLenderQuery
	 * @return
	 * @author: zhanghao
	 * @time:2016年3月23日 上午16:17:56
	 */
	List<DebtForLenderMember> getDebtInfoByLenderId(DebtForLenderQuery debtForLenderQuery);
	
	/**
	 * 获取会员出借人身份名下的债权项目 总数
	 * 
	 * @param debtForLenderQuery
	 * @return
	 */
	int getDebtInfoByLenderIdCount(DebtForLenderQuery debtForLenderQuery);
	/**
	 * 
	 * @Description:获取会员出借人身份名下的债权项目数量，本息金额
	 * @param debtForLenderQuery
	 * @return
	 * @author: zhanghao
	 * @time:2016年3月23日 上午16:17:56
	 */
	DebtForLenderMember getStatisticalDataByLenderId(DebtForLenderQuery debtForLenderQuery);
	
	
	/**
	 * 
	 * @Description:获取会员时间范围内的总投资额
	 * @param transactionQuery
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月18日 下午2:44:29
	 */
	BigDecimal getMemberTotalInvest(TransactionQuery transactionQuery);
	
	
	

	/**
	 * 
	 * @Description:查询我的交易和我的募集中列表
	 * @param transactionQuery
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月12日 上午10:53:34
	 */
	List<TransactionForMemberCenter> selectAllTransactionWithP2P(TransactionQuery transactionQuery);

	/**
	 * 
	 * @Description:查询我的交易和我的募集中列表总数
	 * @param transactionQuery
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月12日 上午10:53:34
	 */
	int selectAllTransactionWithP2PCount(TransactionQuery transactionQuery);
	
	/**
	 * 
	 * @Description:统计滞纳金
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月2日 下午7:36:53
	 */
	public Transaction totalTransationLateFeeByProjecdtId(
			@Param("projectId") Long projectId);

	/**
	 * 判断会员是否投资过
	 * 
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年6月7日 下午17:20:53
	 */
	Integer isMemberInvestedProject(@Param("projectId") Long projectId,@Param("memberId") Long memberId);
	/**
	 * 
	 * @Description:用户投资额
	 * @param memberId
	 * @param transactionTime
	 * @return
	 * @author: chaisen
	 * @time:2016年6月27日 下午5:58:11
	 */
	BigDecimal getTotalAmountByMemberId(@Param("memberId") Long memberId,@Param("transactionTime")  Date transactionTime);
	
	/**
	 * 
	 * @Description:更新交易时间
	 * @param record
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月30日 下午3:28:32
	 */
	int updateTransactionTimeByPrimaryKey(Transaction record);
	
	/**
	 * 最后一笔交易
	 * @param projectId
	 * @return
	 */
	Transaction selectLastTransactionByProjectOrderById(
			@Param("projectId") Long projectId);
	
	/**
	 * 
	 * @Description:投资排行前8用户
	 * @param transactionStartTime
	 * @param transactionEndTime
	 * @return
	 * @author: chaisen
	 * @time:2016年7月11日 下午2:42:14
	 */
	List<TransactionForFirstInvestAct> selectTopEightInvestAmount(@Param("transactionStartTime")  Date transactionStartTime,@Param("transactionEndTime")  Date transactionEndTime);
	/**
	 * 
	 * @Description:赛奥运列表
	 * @param transactionStartTime
	 * @param transactionEndTime
	 * @return
	 * @author: chaisen
	 * @time:2016年7月11日 下午3:11:25
	 */
	List<TransactionForFirstInvestAct> selectTopTenInvestAmount(@Param("transactionStartTime")  Date transactionStartTime,@Param("transactionEndTime")  Date transactionEndTime);
	/**
	 * 
	 * @Description:查询指定时间内指定用户的投资总额
	 * @param memberId
	 * @param transactionStartTime
	 * @param transactionEndTime
	 * @return
	 * @author: chaisen
	 * @time:2016年7月11日 下午3:24:03
	 */
	BigDecimal getMyTotalInvestByMemberIdAndTime(@Param("memberId") Long memberId,@Param("transactionStartTime") 
	Date transactionStartTime,@Param("transactionEndTime")  Date transactionEndTime);
	
	/**
	 * 
	 * @Description:根据时间统计投资笔数
	 * @param transactionStartTime
	 * @param transactionEndTime
	 * @return
	 * @author: chaisen
	 * @time:2016年7月8日 下午4:42:28
	 */
	int countInvestNumbers(@Param("transactionStartTime") 
	Date transactionStartTime,@Param("transactionEndTime")  Date transactionEndTime);
	/**
	 * 
	 * @Description:获取用户未签署合同
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午9:59:11
	 */
	List<Transaction> getUnsignContractNum(@Param("memberId") Long memberId);
	
	/**
	 * 
	 * @Description:获取用户可签署合同
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午9:59:11
	 */
	List<Transaction> getSignableContractNum(@Param("memberId") Long memberId);
	
	/**
	 * 
	 * @Description:查询募集中可签署交易
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午9:59:11
	 */
	List<Transaction> getCollectSignableContractNum(@Param("memberId") Long memberId);
	
	/**
	 * 
	 * @Description:查询我的交易可签署交易
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午9:59:11
	 */
	List<Transaction> getTransactionSignableContractNum(@Param("memberId") Long memberId);
	
	/**
	 * @Description:更新签署状态
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 下午17:42:23
	 */
	int updateSignStatus(@Param("memberId") Long memberId);
	
	/**
	 * 
	 * @Description:获取用户未签署合同分页
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午9:59:11
	 */
	List<Transaction> getUnsignContractNumPage(TransactionQuery transactionQuery);
	
	/**
	 * @Description:合同置为已过期
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 下午17:42:23
	 */
	int expireContract(@Param("projectId") Long projectId);
	
	/**
	 * @Description:合同置为已过期
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月24日 下午17:42:23
	 */
	int expireContractForTransfer(@Param("transferId") Long transferId);
	
	/**
	 * @Description:历史合同置为已过期
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月25日 下午17:42:23
	 */
	int expireHistoryContract();
	
	
	/**
	 * 根据MAP 查询合同签署数据
	 * 
	 * @param map
	 * @return
	 */
	List<ContractSignDto> getContractSignByMap(@Param("map") Map<String, Object> map);
	
	/**
	 * 根据MAP 查询合同签署数据
	 * 
	 * @param map
	 * @return
	 */
	int getContractSignByMapCount(@Param("map") Map<String, Object> map);
	
	/**
	 * 
	 * @Description:获取未还本付息的交易
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 上午9:59:11
	 */
	List<Transaction> getUnendTransaction();
	
	
	/**
	 * @Description:更新我的交易签署状态
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 下午17:42:23
	 */
	int updateTransactionSignableContractStatus(@Param("memberId") Long memberId);
	
	/**
	 * @Description:更新募集中签署状态
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月11日 下午17:42:23
	 */
	int updateCollectSignableContractStatus(@Param("memberId") Long memberId);
	
	
	/**
	 * 根据条件查询需要短信提醒合同未签署的用户
	 * 
	 * @author wangyanji
	 * @param maxNum
	 * @return
	 */
	List<Long> selectUnSignMember();
	
	
	/**
	 * 
	 * @Description:平台交易笔数
	 * @return
	 * @author: chaisen
	 * @time:2016年8月28日 上午10:48:22
	 */
	int getTotalTransactionCount();

	
	/**
	 * 
	 * @Description:查询战队投资排行前十列表 
	 * @return
	 * @author: chaisen
	 * @time:2016年7月2日 上午10:26:37  
	 */
	List<TransactionForFirstInvestAct> selectTopTenInvestByGroupType(TransactionQuery transactionQuery);
	/**
	 * 
	 * @Description:获取战队投资总额
	 * @param transactionQuery
	 * @return
	 * @author: chaisen
	 * @time:2016年7月2日 上午11:04:02  
	 */
	BigDecimal getTotalInvestByGroupType(TransactionQuery transactionQuery);
	/**
	 * 
	 * @Description:查询投资额最先达到的交易
	 * @param transactionQuery
	 * @return
	 * @author: chaisen
	 * @time:2016年7月5日 下午5:19:02
	 */
	Transaction selectMaxTransactionTime(TransactionQuery transactionQuery);

	/**
	 * 查询注册渠道会员投资汇总
	 * @param date
	 * @return
     */
	List<TraceSourceCollectBiz> queryByPageCollectByTraceSource(@Param("query") TraceSourceCollectQuery query);

	/**
	 * 查询总条数
	 * @param query
	 * @return
     */
	Integer queryCountCollectByTraceSource(@Param("query") TraceSourceCollectQuery query);

	/**
	 * 查询会员最后投资时间
	 * @param memberid
	 * @return
     */
	Date queryLastTransactionByMemberId(@Param("memberid") Long memberid);
	
	
	
	BigDecimal getMemberTotalInvestByStatus(TransactionQuery transactionQuery);
	/**
	 * 根据转让项目ID更新交易
	 * 
	 * @param record
	 * @return
	 */
	int updateByTransferId(Transaction record);
	
	/**
	 * 根据转让id查询交易记录
	 */
	List<Transaction> selectByTransferId(@Param("transferId")Long transferId);
	
	
	BigDecimal getInvestAmountByTransferId(@Param("transferId")Long transferId);
	
	
	/**
	 * 查询条件查询会员中心募集中的交易
	 * @param 
	 * @author: zhanghao
	 * @return
	 */
	int selectCollectForMemberCounting(CollectingProjectQuery query);
	/**
	 * 
	 * @Description:募集中金额
	 * @param date
	 * @return
	 * @author: chaisen
	 * @time:2016年10月24日 下午2:26:26
	 */
	BigDecimal getTotalInvestAmount(@Param("transactionStartTime")  Date transactionStartTime,@Param("transactionEndTime")  Date transactionEndTime);

	
	List<Transaction> getTotalInvestAmountList(@Param("pageSize")Integer pageSize);
	
	BigDecimal getMemberTotalInvestByMemberId(@Param("memberId") Long memberId,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("totalDays") Integer totalDays);
	/**
	 * 通过条件查询可以转让的交易记录
	 * 
	 * @param transactionQuery
	 * @return
	 */
	int selectCountToTransferList(
			TransferRecordQuery query);
	
	/**
	 * 通过条件查询可以转让的交易记录
	 * 
	 * @param transactionQuery
	 * @return
	 */
	List<Transaction> selectTransctionToTransferList(
			TransferRecordQuery query);
	
	/**
	 * 
	 * @Description:用户首次投资信息
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年10月28日 下午12:59:40
	 */
	Transaction firstInvestmentByMemberId(
			@Param("memberId") Long memberId);
	/**
	 * 
	 * @Description:总共赚取收益
	 * @param memberId
	 * @returns
	 * @author: chaisen
	 * @time:2016年11月4日 下午5:37:52
	 */
	Transaction getTotalTransactionReceivedInterestByMemberId(@Param("memberId") Long memberId);
	
	BigDecimal getMemberTotalInvestByMemberIdAndTotalDays(@Param("map") Map<String, Object> map);
	List<TransactionForFirstInvestAct> selectTopTenInvestAmountByActivityId(@Param("transactionStartTime")  Date transactionStartTime,
			@Param("transactionEndTime")  Date transactionEndTime,@Param("activityId")Long activityId);

	
	BigDecimal getInvestAmountByMemberId(@Param("memberId")Long memberId,@Param("activityId")Long activityId,@Param("activityEndTime")  Date activityEndTime,@Param("startTime")  Date startTime);

	BigDecimal getInvestAmountByMemberIdAndTime(@Param("memberId")Long memberId, @Param("startTime")  Date startTime, @Param("endTime")  Date endTime, @Param("activityStartTime")  Date activityStartTime);

	List<ActivityFirstInvest> selectTopInvestAmountByActivityIdAndTime(@Param("startTime")  Date startTime, @Param("endTime")  Date endTime, @Param("activityStartTime")  Date activityStartTime);
	
	List<TransactionProjectBiz> queryPageTransactionProjectBiz(@Param("query") TransactionProjectBizQuery query);

	int queryPageCountTransactionProjectBiz(@Param("query") TransactionProjectBizQuery query);
	/**
	 * 查询交易转让累计收益
	 * @param transactionid
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
	 * 查询用户时间区间内投资数
	 * @param memberid
	 * @param starttime
	 * @param endtime
     * @return
     */
	int queryMemberTransactionCount(@Param("memberid")Long memberid,@Param("starttime")Date starttime,@Param("endtime")Date endtime);
	/**
	 * @Description:更新交易转让状态根据id
	 * @return
	 * @author: zhanghao
	 * @time:2016年12月28日 下午4:05:23
	 */
	int updateTransferStatusById(@Param("afterStatus")Integer afterStatus,@Param("beforeStatus")Integer beforeStatus,@Param("remarks")String remarks,@Param("id")Long id);
	
	/**
	 * 查询最近三个月投资成功的用户
	 * 
	 * @author zhanghao
	 * @param map
	 * @return
	 */
	List<Long> selectMemberThreeMonth(@Param("map") Map<String, Object> map);
	
	/**
	 * 累计投资额
	 * @param memberId
	 * @param transactionStartTime
	 * @param transactionEndTime
	 * @return
	 */
	BigDecimal findTotalInvestAmount(@Param("memberId") Long memberId,@Param("transactionStartTime") 
	Date transactionStartTime,@Param("transactionEndTime")  Date transactionEndTime);

	/**
	 * 获取会员时间范围内的非转让项目总投资额
	 * @param memberId
	 * @param transactionStartTime
	 * @param transactionEndTime
     * @return
     */
	BigDecimal getMemberTotalInvestNoTransfer(@Param("memberId")Long memberId,@Param("transactionStartTime")Date transactionStartTime,@Param("transactionEndTime") Date transactionEndTime);


	/**
	 * 查询用户某个时间之前的投资条数
	 * @param memberid
	 * @param endtime
     * @return
     */
    int	queryMemberTransactionCountEndTime(@Param("memberid")Long memberid,@Param("endtime")Date endtime);	
	
	/**
	 * 查询成功的用户的手机号
	 * @param memberId
	 * @return
	 */
	Long selectMemberMobileSucceed(@Param("memberId") Long memberId);
	/**
	 * 通过项目ID获取交易本息表列表
	 * @param projectId
	 * @return
	 */
	List<Transaction> getTransactionByProjectId(@Param("projectId") Long projectId);
	/**
	 * 获取交易成功但未生成交易本息表的总金额
	 * @param memberId
	 * @return
	 */
	BigDecimal getTransactionNoPayAmount(@Param("memberId")Long memberId,@Param("limitTime") String limitTime,@Param("transactionId") Long transactionId);
	
	
	/**
	 * 查询用户时间区间内非转让投资数
	 * @param memberid
	 * @param starttime
	 * @param endtime
     * @return
     */
	int queryMemberNormalTransactionCount(@Param("memberid")Long memberid,@Param("starttime")Date starttime,@Param("endtime")Date endtime);
	
	/**
	 * 
	 * @Description:根据项目ID返回网贷之家交易数据
	 * @param projectId
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月17日
	 */
	List<TransactionsForOpen> queryTransactionForOpen(@Param("projectId") Long projectId);

	/**
	 * 查询用户投资6个月及以上项目投资额
	 * @param memberId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	BigDecimal queryMemberInvestSixProjectPeriod(@Param("memberId")Long memberId,@Param("startTime") Date startTime,@Param("endTime") Date endTime);
}