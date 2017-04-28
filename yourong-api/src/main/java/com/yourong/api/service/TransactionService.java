package com.yourong.api.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.api.dto.ContractSignDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.TransactionForMemberDto;
import com.yourong.api.dto.TransactionForProjectDto;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.query.TransferRecordQuery;
import com.yourong.core.mc.model.biz.ActivityForKing;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.biz.ContractBiz;
import com.yourong.core.tc.model.biz.MemberTransactionCapital;
import com.yourong.core.tc.model.biz.TradeBiz;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.biz.TransactionForProject;
import com.yourong.core.tc.model.biz.TransferContractBiz;
import com.yourong.core.tc.model.query.TransactionQuery;


public interface TransactionService {

	/**
	 * 创建代收交易处理类
	 * @param transaction
	 * @param order
	 * @return
	 */
	public ResultDTO<TradeBiz> createTransactionHostingTrade(Order order, int sourceType, String payerIp,Integer source);
	
	public ResultDO<Order> createTransactionHostingTradeOld(Order order, int sourceType, String payerIp) throws Exception;
	
	
	/**
	 * 执行交易处理类
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
	public Page<TransactionForProjectDto> selectTransactionForProjectsForPage(TransactionQuery transactionQuery);
	
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
	 *//*
	public ResultDO<TransactionForMemberCenter> getTransactionForMemberCenter(
			Long transactionId);*/

	/**
	 * 通过订单id获取交易信息
	 * @param orderId
	 * @return
	 */
	public ResultDO<Transaction> getTransactionByOrderId(Long orderId);

	/**
	 * 合同预览
	 * @param memberId
	 * @param projectId
	 * @param investAmount
	 * @param annualizedRate 
	 * @return
	 */
	public ResultDTO<ContractBiz> previewContract(Long memberId, Long projectId,
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
	public ResultDTO<ContractBiz> viewContract(Long orderId, Long memberId);

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
	 * 查询用户中心交易记录
	 * @param query
	 * @return
	 */
	public Page<TransactionForMemberDto> queryTransactionList(TransactionQuery query);
	
	/**
	 * 查询用户中心交易记录和活动信息
	 * @param query
	 * @return
	 */
	public Page<TransactionForMemberDto> queryTransactionListWithActivity(TransactionQuery query);
	
	/**
	 * 获得用户交易明细
	 * @param transactionId
	 * @param memberId
	 * @return
	 */
	public ResultDTO<TransactionForMemberDto> getTransactionDetailForMember(Long transactionId, Long memberId);
	
	/**
	 * 获得用户的交易总笔数
	 * @param memberId
	 * @param status
	 * @return
	 * @throws ManagerException
	 */
	public int getTransactionsTotalCount(Long memberId, int status);
	
	 /**
     * 是否投资直投项目
     * @param memberId
     * @return
     */
    public boolean isMemberDirectInvest(Long memberId);
    

	 /**
    * M站用户中心累计投资本金，过滤P2P数据
    * @param memberId
    * @return
    */
    public BigDecimal getMemberTotalInvestAmountDirectProject(Long memberId);

	public MemberTransactionCapital getMemberTransactionCapitalTemp(
			Long memberId);

	/**
	 * 
	 * @Description:查询我的交易和我的募集中列表
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月12日 上午9:32:52
	 */
	public Page<TransactionForMemberDto> queryTransactionListWithP2P(TransactionQuery query);
	/**
	 * 
	 * @Description:P2P合同展示
	 * @param query
	 * @return
	 * @author: zhanghao
	 * @time:2016年4月18日 下午4:32:52
	 */
	public ResultDTO<ContractBiz> p2pViewContract(Long orderId, Long memberId); 
	
	/**
	 * 
	 * @Description:查询用户未签署合同信息
	 * @param query
	 * @return
	 * @author: zhanghao
	 * @time:2016年4月18日 下午4:32:52
	 */
	public ResultDTO<ContractSignDto> queryUnsignList(TransactionQuery query);
	

	/**
	 * 一键签署
	 * @param id
	 * @return
	 */    
	public ResultDTO<Object> signAllContract(
			Long memberId);
	

	/**
	 * 手动签署
	 * @param id
	 * @return
	 */    
	public ResultDTO<Object> signContract(
			Long transactionId,Integer source);
	
	/**
	 * 自动签署单个合同
	 * @param id
	 * @return
	 */    
	public ResultDTO<Object> signContractAuto(Long memberId,
			Long transactionId);
	/**
	 * 使用现金券全额投资时，合同初始化方法
	 * @param order
	 * @return
	 */    
	public void afterTransactionContractIsFullCoupon(Order order);
	
	
	public ResultDTO<TradeBiz> isRepeatToCashDest(Order order, String payerIp);
	
	/**
	 * 分页查询项目详情页交易列表
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<TransactionForProjectDto> selectTransferTransactionForProjectsForPage(TransactionQuery transactionQuery);
	
	/**
	 * 转让记录
	 * @param memberId
	 * @return
	 */    
	public ResultDTO<Object> transferList(TransferRecordQuery query);
	
	/**
	 * 转让记录
	 * @param memberId
	 * @return
	 */    
	public ResultDTO<Object> transactionTransferList(TransferRecordQuery query);
	
	
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
	public ResultDTO<Object> transferToProject(Long memberId,Long transactionId,BigDecimal transferAmount) ;
	
	/**
	 * 
	 * @Description:转让信息
	 * @param memberId
	 * @param transactionId
	 * @param discount
	 * @param transferAmount
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月8日 下午4:26:40
	 */
	public ResultDTO<Object> transferInformation(Long memberId,Long transactionId ) ;
	
	/**
	 * 
	 * @Description:撤销转让
	 * @param memberId
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月14日 下午4:26:40
	 */
	public ResultDTO<Object> cancelTransferProject(Long memberId,Long transactionId);
	
	
	/**
	 * 查看转让直投合同
	 * @param memberId 
	 * @param orderId 
	 * @return
	 */
	public ResultDTO<TransferContractBiz> viewZtTransferContract(Long orderId, Long memberId);
	
	/**
	 * 查看转让债权合同
	 * @param memberId 
	 * @param orderId 
	 * @return
	 */
	public ResultDTO<TransferContractBiz> transferContract(Long orderId, Long memberId);
	
	/**
	 * 
	 * @Description:是否可以发起转让
	 * @param memberId
	 * @param transactionId
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月24日 下午4:26:40
	 */
	public ResultDTO<Object> ableToTransfer(Long memberId,Long transactionId) ;

	/**
	 * 查询用户时间区间内投资数
	 * @param memberid
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public int queryMemberTransactionCount(Long memberid, Date starttime, Date endtime);
}
