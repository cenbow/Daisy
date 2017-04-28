package com.yourong.core.tc.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.BorrowInterestDetailBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestCollect;
import com.yourong.core.tc.model.biz.CalenderTransactionInterestDetail;
import com.yourong.core.tc.model.biz.TransactionInterestDetailForCalendar;
import com.yourong.core.tc.model.biz.TransactionInterestForDate;
import com.yourong.core.tc.model.biz.TransactionInterestForLateFee;
import com.yourong.core.tc.model.biz.TransactionInterestForMember;
import com.yourong.core.tc.model.biz.TransactionInterestForMonth;
import com.yourong.core.tc.model.biz.TransactionInterestForPay;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.query.BorrowRepayQuery;
import com.yourong.core.tc.model.query.BorrowTransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionInterestQuery;

@Repository
public interface TransactionInterestMapper {

	List<TransactionInterest> findByPage(@Param("map") Map<String, Object> map);

	int findByPageCount(@Param("map") Map<String, Object> map);

	/**
	 * 插入交易本息
	 * 
	 * @param record
	 * @return
	 */
	int insertSelective(TransactionInterest record);

	/**
	 * 通过id查询交易本息
	 * 
	 * @param id
	 * @return
	 */
	TransactionInterest selectByPrimaryKey(Long id);

	/**
	 * 通过id查询交易本息
	 * 
	 * @param id
	 * @return
	 */
	TransactionInterest selectByPrimaryKeyForLock(Long id);

	/**
	 * 更新交易本息
	 * 
	 * @param record
	 * @return
	 */
	int updateByPrimaryKeySelective(TransactionInterest record);

	/**
	 * 通过条件查询交易本息记录
	 * 
	 * @param transactionQuery
	 * @return
	 */
	List<TransactionInterest> selectTransactionInterestsByQueryParams(TransactionInterestQuery transactionQuery);

	/**
	 * 通过条件查询交易本息记录总数
	 * 
	 * @param transactionQuery
	 * @return
	 */
	int selectTransactionInterestsByQueryParamsTotalCount(TransactionInterestQuery transactionQuery);

	/**
	 * 查询待支付交易本息记录
	 * 
	 * @return
	 */
	List<TransactionInterestForPay> selectToBePaidTransactionInterests();

	/**
	 * 更新状态
	 * 
	 * @param params
	 * @return
	 */
	int updateStatusAndRealPayForPaySuccess(Map<String, Object> params);

	/**
	 * 通过项目id更新交易本息记录
	 * 
	 * @param beforeStatus
	 * @param afterStatus
	 * @param projectId
	 * @param tradeNo
	 * @return
	 */
	int updateStatusToPayingForPayInterest(@Param("beforeStatus") int beforeStatus,
			@Param("afterStatus") int afterStatus, @Param("projectId") Long projectId, @Param("tradeNo") String tradeNo);

	/**
	 * 通过项目id查询出代付的本息记录
	 * 
	 * @param projectId
	 * @param status
	 * @return
	 */
	List<TransactionInterest> selectPayingTransactionInterestsByTradeNo(@Param("tradeNo") String tradeNo,
			@Param("status") int status);

	/**
	 * 查询额外收益总额
	 * 
	 * @param sourceId
	 * @param status
	 * @return
	 */
	BigDecimal selectExtraInterestByProject(@Param("projectId") Long projectId, @Param("status") int status);

	/**
	 * 还本付息更新本息记录状态（包含更新为待支付和已支付）
	 * 
	 * @param beforeStatus
	 * @param afterStatus
	 * @param tradeNo
	 * @return
	 */
	int updateStatusForPayInterest(@Param("beforeStatus") int beforeStatus, @Param("afterStatus") int afterStatus,
			@Param("tradeNo") String tradeNo);

	/**
	 * 查询最近一笔还本人付息数据
	 * 
	 * @param memberId
	 * @return
	 */
	public TransactionInterest queryLastPayTransactionInterest(@Param("memberId") Long memberId);

	/**
	 * 查询当前还款的期数
	 * 
	 * @param transactionId
	 * @param endDate
	 * @return
	 */
	public String queryRepayPeriods(@Param("transactionId") Long transactionId, @Param("endDate") Date endDate);

	/**
	 * 查询会员当前待收（本金+利息）
	 * 
	 * @param memberId
	 * @return
	 */
	BigDecimal queryCollectedAmount(@Param("memberId") Long memberId);

	/**
	 * 获取一个项目的付息时间金额表
	 * 
	 * @param projectId
	 * @return
	 */
	public List<TransactionInterest> queryInterestByProjectId(@Param("projectId") Long projectId);

	/**
	 * 
	 * @return
	 */
	public List<TransactionInterestForMember> queryTransactionInterestForMember(
			@Param("query") TransactionInterestQuery query);

	/**
	 * 
	 * @param query
	 * @return
	 */
	public int queryTransactionInterestForMemberTotalCount(@Param("query") TransactionInterestQuery query);

	/**
	 * 剔除P2P项目
	 * 
	 * @return
	 */
	public List<TransactionInterestForMember> p2pQueryTransactionInterestForMember(
			@Param("query") TransactionInterestQuery query);

	/**
	 * 剔除P2P项目
	 * 
	 * @param query
	 * @return
	 */
	public int p2pQueryTransactionInterestForMemberTotalCount(@Param("query") TransactionInterestQuery query);

	/**
	 * 统计用户投资使用收益券获得的利息
	 * 
	 * @param memberId
	 * @return
	 */
	public BigDecimal totalMemberExtraInterest(@Param("memberId") Long memberId);

	/**
	 * 根据日期获得用户还本付息详情
	 * 
	 * @param memberId
	 * @param endDate
	 * @return
	 */
	public List<TransactionInterestDetailForCalendar> getMemberInterestDetailByDate(
			@Param("map") Map<String, Object> map);

	/**
	 * 当前还款的笔数
	 * 
	 * @param memberId
	 * @param transactionId
	 * @param endDate
	 * @return
	 */
	public Integer getCurrentInterestNum(@Param("memberId") Long memberId, @Param("transactionId") Long transactionId,
			@Param("endDate") String endDate);

	/**
	 * 获得项目还款的总笔数
	 * 
	 * @param memberId
	 * @param transactionId
	 * @return
	 */
	public Integer getTotalInterestNum(@Param("memberId") Long memberId, @Param("transactionId") Long transactionId);

	/**
	 * 查看用户还本付息明细
	 * 
	 * @param query
	 * @return
	 */
	public List<TransactionInterestForMember> getTransactionInterestDetailForMember(
			@Param("query") TransactionInterestQuery query);

	/**
	 * 查询待支付 项目id 集合
	 * 
	 * @return
	 */
	public List<Long> queryToBePaidTransactionInterests();

	/**
	 * 查询待支付 交易id 集合
	 * 
	 * @return
	 */
	public List<Long> queryIDToBePaidTransactionInterests(@Param("projectId") Long projectId);

	/**
	 * 查询待支付的，包括直投项目
	 * 
	 * @return
	 */
	List<TransactionInterestForPay> selectAllProjectToBePaidTransactionInterests();

	/**
	 * 
	 * @Description:过滤p2p
	 * @param transactionQuery
	 * @return
	 * @author: chaisen
	 * @time:2016年3月8日 上午11:15:47
	 */
	List<TransactionInterest> selectTransactionInterestsByQueryParamsFilterP2p(TransactionInterestQuery transactionQuery);

	/**
	 * 
	 * @Description:查看用户还本付息明细
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年3月11日 下午5:18:14
	 */
	List<TransactionInterestForMember> getTransactionInterestDetailForMemberFilterP2P(
			@Param("query") TransactionInterestQuery query);

	/**
	 * 获取一个项目的付息时间金额表，和本息标志
	 * 
	 * @param projectId
	 * @return
	 */
	public List<TransactionInterest> queryInterestAndPrincipalFlagByProjectId(@Param("projectId") Long projectId);

	/**
	 * 
	 * @Description:统计代收金额
	 * @param memberId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月6日 下午1:26:20
	 */
	List<TransactionInterestForMonth> selectTransactionInterestsByMonth(TransactionInterestQuery query);

	/**
	 * 
	 * @Description:根据结束日期分组查询交易本息，status: 1 当天全部已还款，0 存在未还
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月6日 下午2:37:36
	 */
	List<TransactionInterestForDate> selectTransactionInterestsByEndDate(TransactionInterestQuery query);

	Integer ifPayByEndDateAndMemberId(@Param("map") Map<String, Object> map);

	/**
	 * 通过项目id查询该项目平台垫付收益券总额
	 * 
	 * @param tradeNo
	 * @return
	 */
	BigDecimal sumExtraInterest(@Param("projectId") Long projectId);

	/**
	 * 通过项目ID查询剩余支付本金、利息、平台贴息
	 * 
	 * @param transactionQuery
	 * @return
	 */
	TransactionInterest selectTransactionInterestsByProjectId(@Param("map") Map<String, Object> map);

	/**
	 * 查询提前还款待支付的，包括直投项目
	 * 
	 * @return
	 */
	List<TransactionInterestForPay> selectAllProjectToBePrePaidTransactionInterests();

	/**
	 * @Description:交易对应不属于某一状态的本息个数
	 * @return
	 * @author: fuyili
	 * @time:2016年5月23日 上午10:25:45
	 */
	int getCountTransationInterestByTransationIdAndStatus(@Param("transactionId") Long transactionId,
			@Param("status") int status);

	/**
	 * @Description: 项目本息对应不属于某一状态的交易本息个数
	 * @param projectId
	 * @param transactionId
	 * @param status
	 * @return
	 * @author: fuyili
	 * @time:2016年5月24日 下午1:35:12
	 */
	int getCountByProjectInterestNotStatus(@Param("interestId") Long interestId);

	/**
	 * @Description:根据项目本息id更新交易还款类型
	 * @param afterPayType
	 * @param beforePayType
	 * @param interestId
	 * @return
	 * @author: fuyili
	 * @time:2016年6月1日 下午6:39:37
	 */
	int updatePayTypeByInterestId(@Param("afterPayType") int afterPayType, @Param("beforePayType") int beforePayType,
			@Param("interestId") Long interestId);

	/**
	 * 
	 * @Description:统计滞纳金
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月31日 下午2:11:46
	 */
	BigDecimal totalOverdueFine(@Param("projectId") Long projectId);

	/**
	 * @Description:获取还本付息最后一期
	 * @return
	 * @author: zhanghao
	 * @time:2016年6月1日 下午14:00:45
	 */
	TransactionInterest getPreTransactionInterest(@Param("map") Map<String, Object> map);

	/**
	 * @Description:根据项目本息id查询交易本息
	 * @param interest
	 * @return
	 * @author: fuyili
	 * @time:2016年6月5日 上午9:36:31
	 */
	List<TransactionInterestForLateFee> getByProjectInterestId(@Param("interestId") Long interestId);

	/**
	 * @Description:逾期还款的交易本息
	 * @return
	 * @author: fuyili
	 * @time:2016年6月5日 下午7:13:48
	 */
	List<TransactionInterestForPay> selectAllProjectToBeOverPaidTransactionInterests();

	/**
	 * @Description:交易本息还款之后的更新状态
	 * @param params
	 * @return
	 * @author: fuyili
	 * @time:2016年6月6日 下午3:15:25
	 */
	int updateStatusForPaySuccess(@Param("beforeStatus") int beforeStatus, @Param("afterStatus") int afterStatus,
			@Param("id") Long id);

	/**
	 * 根据日期获得用户还本付息详情
	 * 
	 * @param memberId
	 * @param endDate
	 * @author: zhanghao
	 * @time:2016年6月1日 下午14:00:45
	 * @return
	 */
	public List<CalenderTransactionInterestDetail> getMemberInterestDetailByDateNew(
			@Param("map") Map<String, Object> map);

	/**
	 * @Description:根据项目本息id统计交易本息本期所有实付金额
	 * @param interestId
	 * @return
	 * @author: fuyili
	 * @time:2016年6月18日 下午5:21:32
	 */
	TransactionInterest getRealPayByInterestId(@Param("interestId") Long interestId);

	/**
	 * @Description:查询当前未还本息的
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年6月21日 下午5:53:28
	 */
	TransactionInterest selectUnreturnPrincipalAndInterestByProjectId(@Param("projectId") Long projectId);
	
	/**
	 * @Description:查询当前未还本息的
	 * @param transactionId
	 * @return
	 * @author: fuyili
	 * @time:2016年6月21日 下午5:53:28
	 */
	TransactionInterest selectUnreturnPrincipalAndInterestByTransactionId(@Param("transactionId") Long transactionId);

	/**
	 * 查询当日交易本息所关联的借款人代收号
	 * 
	 * @param tradeNo
	 * @return
	 */
	List<String> selectCurrentDateCollectTradeNos(@Param("status") Integer status);

	/**
	 * 
	 * @Description:根据交易ID更新
	 * @param transactionInterest
	 * @return
	 * @author: wangyanji
	 * @time:2016年9月21日 上午4:30:12
	 */
	Integer updateByTransactionId(TransactionInterest transactionInterest);

	/**
	 * @Description:查询交易对应的所有可转让（status=待支付）的交易本息
	 * @param status
	 * @param transactionId
	 * @return
	 * @author: fuyili
	 * @time:2016年9月23日 下午3:59:04
	 */
	List<TransactionInterest> selectToTransferInterestByTransactionId(@Param("status") Integer status,
			@Param("transactionId") Long transactionId);
	/**
	 * 查询借款人还本付息
	 * @return
     */
	List<BorrowRepayInterestBiz> findByPageBorrowRepayInterest(@Param("query")BorrowRepayQuery query);

	/**
	 * 查询借款人还本付息当天汇总
	 * @param date
	 * @return
     */
	BorrowRepayInterestCollect findByDayCollectBorrowRepayInterest(@Param("enddate") Date date);
	/**
	 * 查询借款人还本付息条数
	 * @param query
	 * @return
     */
	Integer findByPageCountBorrowRepayInterest(@Param("query")BorrowRepayQuery query);

	/**
	 * 查询借款人的待还款数量
	 * @param enddate
	 * @param projectids
     * @return
     */
	Integer selectWaitRepayNum(@Param("enddate")Date enddate,@Param("projectids")List<Long> projectids);

	/**
	 * 查询借款人还本付息当天待还款项目汇总
	 * @param date
	 * @return
     */
	Integer selectDayCollectWaitRepayNum(@Param("enddate") Date date);
	/**
	 * 查询借款人的已还款数量
	 * @param enddate
	 * @param projectids
     * @return
     */
	Integer selectEndRepayNum(@Param("enddate")Date enddate,@Param("projectids")List<Long> projectids);

	/**
	 * 查询借款人的还本付息列表
	 * @param query
	 * @return
     */
	List<BorrowInterestDetailBiz> selectDayInterestByBorrerId(@Param("query")BorrowTransactionInterestQuery query,@Param("productids")List<Long> productids);

	/**
	 *查询借款人的还本付息列表总条数
	 * @return
     */
	Integer selectCountDayInterestByBorrerId(@Param("query")BorrowTransactionInterestQuery query,@Param("productids")List<Long> productids);
	/**
	 * 获取用户待回款笔数
	 * @param memberId
	 * @param status
	 * @return
	 */
	public Integer getCountInterestByMemberId(@Param("memberId")Long memberId, @Param("status")Integer status ,@Param("startTime")Date startTime);
	
	/**
	 * 
	 * @Description:已转让金额
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年9月27日 下午1:57:43
	 */
	BigDecimal totalTransferAmount(@Param("memberId") Long memberId);
	
	Integer totalTransferAmountNum(@Param("memberId") Long memberId);

	BigDecimal totalMemberExtraInterestT(@Param("memberId") Long memberId);
	
	TransactionInterest selectRealPayByEndDate(@Param("startTime")Date startTime);
	
	
	TransactionInterest selectWaitPayByEndDate(@Param("status")Integer status ,@Param("startTime")Date startTime);

	List<TransactionInterestForMember> getTransactionInterestDetailForMemberTransfer(
			@Param("query")TransactionInterestQuery query);
	
	
	List<TransactionInterest> selectTransactionInterestByInterestId(@Param("interestId") Long interestId);

	/**
	 * @Description:查询提前还款记录
	 * @param transactionQuery
	 * @return
	 * @author: wangyanji
	 * @time:2016年12月12日
	 */
	List<TransactionInterest> queryEarlyInterest(TransactionInterestQuery transactionQuery);

	/**
	 * 查询交易的第一笔交易本息
	 * @param transactionId
	 * @return
     */
	TransactionInterest queryFirstInterest(Long transactionId);

	/**
	 * 查询实际支付收益券产生的利息和实付利息
	 * @param transactionId
	 * @return
     */
	TransactionForMemberCenter queryTotalInterestByTransactionId(Long transactionId);
	/**
	 * 获取用户的待余额总数
	 * @param memberId
	 * @return
	 */
	BigDecimal totalMemberTransferAmount(@Param("memberId") Long memberId,@Param("status") Integer status
			,@Param("limitTime") String limitTime,@Param("transactionId")Long transactionId);
	
}