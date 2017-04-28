package com.yourong.web.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.query.CapitalInOutLogQuery;
import com.yourong.core.ic.model.biz.TransactionProjectDetailBiz;
import com.yourong.core.ic.model.query.TransactionProjectDetailBizQuery;
import com.yourong.core.mc.model.biz.ActivityForInvest;
import com.yourong.core.mc.model.biz.ActivityForKing;
import com.yourong.core.mc.model.biz.ActivityForRankingList2nd;
import com.yourong.core.mc.model.biz.ActivityMonthlyMyRank;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.biz.ContractBiz;
import com.yourong.core.tc.model.biz.DebtForLenderMember;
import com.yourong.core.tc.model.biz.MemberTransactionCapital;
import com.yourong.core.tc.model.biz.TraceSourceCollectBiz;
import com.yourong.core.tc.model.biz.TradeBiz;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.biz.TransactionForProject;
import com.yourong.core.tc.model.biz.TransactionProjectBiz;
import com.yourong.core.tc.model.biz.TransferContractBiz;
import com.yourong.core.tc.model.query.DebtForLenderQuery;
import com.yourong.core.tc.model.query.TraceSourceCollectQuery;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionProjectBizQuery;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.web.dto.OrderPayDto;


public interface TransactionService {

	/**
	 * 创建代收交易处理类
	 * @param transaction
	 * @param order
	 * @return
	 */
	public ResultDO<TradeBiz> createTransactionHostingTrade(Order order, int sourceType, String payerIp);

	/**
	 * 执行交易处理类
	 * 
	 * @param tradeStatus
	 * @param outTradeNo
	 * @param tradeNo
	 */
	public ResultDO<Transaction> afterTransactionCollectNotifyProcess(String tradeNo, String outTradeNo, String tradeStatus) ;
	
	/**
	 * 分页查询项目详情页交易列表
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public List<TransactionForProject> selectTransactionForProjectsForPage(TransactionQuery transactionQuery);
	
	 /**
     *  查询会员投资资金信息
     * @param memberID
     * @return
     */
    public MemberTransactionCapital getMemberTransactionCapital(Long memberID);
    
    /**
     * 生成交易合同
     * @param transactionId
     * @return
     */
    public boolean generateContract(Long transactionId);
    

    /**
     * 查询最新投资记录
     * @return
     */
	public List<TransactionForProject> selectNewTransactions(int pageSize);

	/**
	 * 处理回调后放款给原始债权人业务
	 * @param tradeStatus
	 * @param outTradeNo
	 */
	public void afterloanToOriginalCreditor(String tradeStatus,
			String outTradeNo);

	/**
	 * 查询用户中心交易记录
	 * @param query
	 * @return
	 */
	public Page<TransactionForMemberCenter> selectAllTransactionForMember(
			TransactionQuery query);
	
	/**
	 * 获取交易详情
	 * @param transactionId
	 * @param id
	 * @return
	 */
	public ResultDO<TransactionForMemberCenter> getTransactionForMemberCenter(
			Long transactionId,Long memberId);

	/**
	 * 通过订单id获取交易信息
	 * @param orderId
	 * @return
	 */
	public ResultDO<OrderPayDto> getTransactionByOrderId(Long orderId);

	/**
	 * 合同预览
	 * @param memberId
	 * @param projectId
	 * @param investAmount
	 * @param annualizedRate 
	 * @return
	 */
	public ResultDO<ContractBiz> previewContract(Long memberId, Long projectId,
			BigDecimal investAmount, BigDecimal annualizedRate);
	
	/**
	 * 查看合同
	 * @param memberId 
	 * @param memberId
	 * @param projectId
	 * @param investAmount
	 * @param annualizedRate 
	 * @return
	 */
	public ResultDO<ContractBiz> viewContract(Long orderId, Long memberId);
	
	/**
	 * P2P查看合同
	 * @param memberId 
	 * @param memberId
	 * @param projectId
	 * @param investAmount
	 * @param annualizedRate 
	 * @return
	 */
	public ResultDO<ContractBiz> p2pViewContract(Long orderId, Long memberId);

	/**
	 * 下载合同，生成一个临时链接供下载
	 * @param memberId
	 * @param transactionId 
	 * @return
	 */
	public ResultDO<String> downloadContract(Long memberId, Long transactionId);

	/**
	 * 查询交易记录总数
	 * @param id
	 * @param type
	 * @return
	 */
	public int getTransactionCount(Long memberId, int type);

	/**
	 * 代收平台垫付优惠券回调处理方法
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 */
	public ResultDO<?> afterPaltformCouponCollectNotify(String tradeNo,
			String outTradeNo, String tradeStatus);

	/**
	 * 通过项目id查询投资笔数
	 * @param projectId
	 * @return
	 */
	public int getTransactionCountByProject(Long projectId);
	
	/**
	 * 通过项目id查询投资笔数
	 * @param projectId
	 * @return
	 */
	public int getTransactionCountByTransferId(Long transferId);
	/**
	 * 通过转让项目id查询投资会员数
	 * @param transferId
	 * @return
	 */
	public int getTransactionMemberCountByTransferId(Long transferId);
	/**
	 * 通过项目id查询投资会员数
	 * @param projectId
	 * @return
	 */
	public int getTransactionMemberCountByProject(Long projectId);

	/**
	 * 通过项目id查询该项目总收益
	 * @param projectId
	 * @return
	 */
	public BigDecimal getTotalTransactionInterestByProject(Long projectId);
	
	/**
	 * 通过转让项目id查询该项目总收益
	 * @param transferId
	 * @return
	 */
	public BigDecimal getTotalTransactionInterestByTransferId(Long transferId);

	/**
	 * 我是王推荐好友投资额排行榜
	 * @return
	 */
	public List<ActivityForKing> getRefferalInvestAmountList();

	/**
	 * 投资排行前10
	 * @return
	 */
	public List<TransactionForFirstInvestAct> selectTopTenInvest();

	/**
	 * 用户是否有过投资
	 * @param memberId
	 * @return
	 */
	public boolean hasTransactionByMember(Long memberId);
	
	/**
	 * 获取满足投资总额在某一范围内的用户
	 * @return
	 */
	public ActivityForRankingList2nd getMemberMeetTotalInvestRange(BigDecimal minAmount,BigDecimal maxAmount,Integer limitNum);

	public int getCountMemberMeetTotalInvestRange(Map<String, Object> map);
	
	/**
	 *  查看我排名
	 * @param map
	 * @return
	 */
	public ActivityMonthlyMyRank findMyRank(Map<String, Object> map);
	
	public ActivityForRankingList2nd getMonthlyMemberMeetTotalInvestRange(BigDecimal minAmount,BigDecimal maxAmount,Integer limitNum);
	
	/**
	 * 投房有喜投资列表
	 * @return
	 */
	public ResultDO<ActivityForInvest> getInvestHouseList();
	
	/**
	 * 投房有喜优惠券
	 * @return
	 */
	public ResultDO<ActivityForInvest> getInvestHouseCoupon();
	
	/**
	 * 投房有喜我的排名
	 * @param memberId
	 * @return
	 */
	public ResultDO<ActivityForInvest> getMyInvestHouseRank(Long memberId);
	
	/**
	 * 查询用户历史投资详情
	 * @param query
	 * @return
	 */
	public ResultDO  getTransactionsDetailForMember(TransactionInterestQuery query);
	/**
	 * 
	 * @Description:获取会员出借人身份名下的债权项目 
	 * @param debtForLenderQuery
	 * @return
	 * @author: zhanghao
	 * @time:2016年3月23日 上午16:17:56
	 */
	public Page<DebtForLenderMember> getDebtInfoByLenderId(DebtForLenderQuery debtForLenderQuery);
	/**
	 * 
	 * @Description:获取会员出借人身份名下的债权项目数量，本息金额
	 * @param debtForLenderQuery
	 * @return
	 * @author: zhanghao
	 * @time:2016年3月24日 上午15:57:56
	 */
	public DebtForLenderMember getStatisticalDataByLenderId(DebtForLenderQuery debtForLenderQuery);
	
	/**
	 * 获取债权本息详情
	 * @param transactionId
	 * @param id
	 * @return
	 */    
	public ResultDO<DebtForLenderMember> getTransactionInterestDetail(
			Long projectId);
	/**
	 * 
	 * @Description:通过项目id查询该项目实际总收益
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月13日 下午4:05:27
	 */
	public BigDecimal getTotalTransactionReceivedInterestByProject(Long projectId);
	
	/**
	 * 
	 * @Description:通过转让项目id查询该项目实际总收益
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年9月28日 下午4:05:27
	 */
	public BigDecimal getTotalTransactionReceivedInterestByTransferId(Long projectId);
	


	/**
	 * 代付批次回调业务处理
	 * @param batchNo
	 * @param outBatchNo
	 * @param tradeList
	 * @return
	 */
	public boolean afterHostingPayBatchNotifyProcess(String batchNo,
			String outBatchNo, String tradeList);
	
	/**
	 * 一键签署
	 * @param id
	 * @return
	 */    
	public ResultDO<Object> signAllContractByType(Long memberId,Integer type);
	
	/**
	 * 手动签署
	 * @param id
	 * @return
	 */    
	public ResultDO<Object> signContract(
			Long transactionId);
	/**
	 * 开启交易完成合同初始化业务
	 * @param id
	 * @return
	 */ 
	public void afterTransactionContract(Transaction t);
	
	public ResultDO<Object> signContractAuto(Long memberId,
			Long transactionId);
	
	/**
	 * 获取合同下载链接
	 * @param memberId
	 * @param transactionId
	 * @return
	 */ 
	public ResultDO<Object> getContractDownUrl(Long memberId,
			Long transactionId) ;
	/**
	 * 获取用户未签署合同数量
	 * @param memberId
	 * @return
	 */ 
	public int getUnsignContractNum(Long memberId);
	/**
	 * 获取用户募集中未签署合同数量
	 * @param memberId
	 * @return
	 */ 
	public int getCollectSignableContractNum(Long memberId);
	/**
	 * 获取用户我的交易未签署合同数量
	 * @param memberId
	 * @return
	 */ 
	public int getTransactionSignableContractNum(Long memberId);
	/**
	 * 使用现金券全额投资时，合同初始化方法
	 * @param order
	 * @return
	 */   
	public void afterTransactionContractIsFullCoupon(Order order);

	/**
	 * 
	 * @Description:非委托支付获取支付链接
	 * @param order
	 * @param type
	 * @param payerIp
	 * @return
	 * @author: wangyanji
	 * @time:2016年8月8日 下午4:26:40
	 */
	public ResultDO<TradeBiz> isRepeatToCashDest(Order order, String payerIp);
	/**
	 * 
	 * @Description:平台累计交易笔数
	 * @return
	 * @author: chaisen
	 * @time:2016年8月28日 上午11:02:28
	 */
	public int getTotalTransactionCount();

	/**
	 * 查询注册渠道会员投资汇总
	 * @param query
	 * @return
     */
	Page<TraceSourceCollectBiz> queryByPageCollectByTraceSource(TraceSourceCollectQuery query);
	
	/**
	 * 
	 * @Description:发起转让
	 * @param memberId
	 * @param transactionId
	 * @param discount
	 * @param transferAmount
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月8日 下午4:26:40
	 */
	public ResultDO<Object> transferToProject(Long memberId,Long transactionId,BigDecimal transferAmount) ;
	
	/**
	 * 
	 * @Description:是否可以转让
	 * @param memberId
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月8日 下午4:26:40
	 */
	public ResultDO<Object> ableToTransfer(Long memberId,Long transactionId) ;
	
	
	
	/**
	 * 
	 * @Description:转让详情
	 * @param memberId
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月8日 下午4:26:40
	 */
	public ResultDO<Object> transferInformation(Long memberId,Long transactionId) ;
	
	/**
	 * 
	 * @Description:撤销转让
	 * @param memberId
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月21日 上午10:26:40
	 */
	public ResultDO<Object> cancelTransferProject(Long memberId,Long transactionId) ;
	
	/**
	 * 分页查询转让项目详情页交易列表
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public List<TransactionForProject> getTransferProjectDetailTransactions(TransactionQuery transactionQuery);
	
	/**
	 * 查看合同
	 * @param memberId 
	 * @param orderId 
	 * @return
	 */
	public ResultDO<TransferContractBiz> viewZtTransferContract(Long orderId, Long memberId);
	
	/**
	 * 查看转让债权合同
	 * @param memberId 
	 * @param orderId 
	 * @return
	 */
	public ResultDO<TransferContractBiz> transferContract(Long orderId, Long memberId);

	public ResultDO getTransactionInterestDetailForMemberTransfer(
			TransactionInterestQuery query);

	/**
	 * 
	 * @Description:会员已转让收款/手续费
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月4日
	 */
	public Object getTransferDetailForMember(Integer type, Long memberId);

	/**
	 *  总计转让收款详情
	 * @param query
	 * @return
	 */
	public ResultDO getTotalTransferAmountForMemberTransfer(
			CapitalInOutLogQuery query);

	public int getLotteryNum(Long transactionId);

	public boolean isQuickLotteryProject(Long projectId);

	/**
	 * 查询用户时间区间内投资数
	 * @param memberid
	 * @param starttime
	 * @param endtime
     * @return
     */
	public int queryMemberTransactionCount(Long memberid,Date starttime,Date endtime);

	
	public Page<TransactionProjectBiz> queryPageTransactionProjectBiz(TransactionProjectBizQuery query);

	/**
	 * 查询投资中转让项目
	 * @param transactionId
	 * @param memberId
	 * @return
	 */
	public TransactionProjectDetailBiz queryInvestmentedTransactionProjectDetailBiz(Long transactionId, Long memberId);

	public Page<TransactionProjectDetailBiz> queryPageTransactionProjectDetailBiz(TransactionProjectDetailBizQuery query);
}
