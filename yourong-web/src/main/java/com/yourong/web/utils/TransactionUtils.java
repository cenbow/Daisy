package com.yourong.web.utils;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.dao.TransferProjectMapper;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.tc.dao.OrderMapper;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.biz.TransactionForProject;
import com.yourong.web.service.TransactionService;

/**
 * 投资util
 * @author Leon Ray
 * 2014年10月8日-下午3:06:50
 */
public class TransactionUtils {
	
	private static TransactionService transactionService = SpringContextHolder.getBean(TransactionService.class);
	
	
	
	private static OrderMapper orderMapper = SpringContextHolder.getBean(OrderMapper.class);
	
	private static ProjectMapper projectMapper = SpringContextHolder.getBean(ProjectMapper.class);
	
	private static TransferProjectMapper transferProjectMapper = SpringContextHolder.getBean(TransferProjectMapper.class);
	
	
	/**
	 * 项目列表页最新投资记录
	 * @param req
	 * @param resp
	 * @return
	 */
    public List<TransactionForProject> getNewTransactions( int pageSize )  {
    	return transactionService.selectNewTransactions(pageSize);
    }
    
    /**
     * 获取用户投资总额
     * @param memberId
     * @return
     */
    public BigDecimal getMemberTotalInvestAmount(Long memberId) {
    	return RedisMemberClient.getTotalInvestAmount(memberId);
    }
    
    /**
     * 查询用户交易记录总数
     * @param type
     * @return
     */
    public int getTransactionCount(int type) {
    	return transactionService.getTransactionCount(ServletUtil.getUserDO().getId(), type);
    }
    
    /**
     * 通过项目id查询投资笔数
     * @param projectId
     * @return
     */
    public int getTransactionCountByProject(Long projectId) {
    	return transactionService.getTransactionCountByProject(projectId);
    }
    
    /**
     * 通过转让项目id查询投资笔数
     * @param projectId
     * @return
     */
    public int getTransactionCountByTransferId(Long transferId) {
    	return transactionService.getTransactionCountByTransferId(transferId);
    }
    
    /**
     * 通过转让项目id投资会员数
     * @param projectId
     * @return
     */
    public int getTransactionMemberCountByTransferId(Long transferId) {
    	return transactionService.getTransactionMemberCountByTransferId(transferId);
    }
    
    /**
     * 通过项目id查询投资会员数
     * @param projectId
     * @return
     */
    public int getTransactionMemberCountByProject(Long projectId) {
    	return transactionService.getTransactionMemberCountByProject(projectId);
    }
    
    /**
     * 通过项目id查询该项目总收益
     * @param projectId
     * @return
     */
    public String getTotalTransactionInterestByProject(Long projectId) {
    	Project project=projectMapper.selectByPrimaryKey(projectId);
    	BigDecimal totalTransactionInterest =BigDecimal.ZERO;
    	if(project!=null&&project.getStatus()==StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()){
    		totalTransactionInterest= transactionService.getTotalTransactionReceivedInterestByProject(projectId);
    	}else{
    		totalTransactionInterest= transactionService.getTotalTransactionInterestByProject(projectId);
    	}
    	if(totalTransactionInterest == null){
    		return "0.00";
    	}
    	return FormulaUtil.formatCurrencyNoUnit(totalTransactionInterest);
    }
    
    /**
     * 通过项目id查询该项目总收益
     * @param projectId
     * @return
     */
    public String getTotalTransactionInterestByByTransferId(Long transferId) {
    	TransferProject transferProject=transferProjectMapper.selectByPrimaryKey(transferId);
    	BigDecimal totalTransactionInterest =BigDecimal.ZERO;
    	if(transferProject!=null&&transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_REPAYMENT.getStatus()){
    		totalTransactionInterest= transactionService.getTotalTransactionReceivedInterestByTransferId(transferId);
    	}else{
    		totalTransactionInterest= transactionService.getTotalTransactionInterestByTransferId(transferId);
    	}
    	if(totalTransactionInterest == null){
    		return "0.00";
    	}
    	return FormulaUtil.formatCurrencyNoUnit(totalTransactionInterest);
    }
    
    /**
	 * 用户是否有过投资
	 * @param memberId
	 * @return
	 */
	public boolean hasTransactionByMember(Long memberId){
		if(memberId == null){
			return false;
		}
		return transactionService.hasTransactionByMember(memberId);
	}
	
	/**
     * 通过项目id查询该项目已经支付成功的订单数
     * @param projectId
     * @return
     */
    public int getPayedCountByProject(Long projectId) {
    	return orderMapper.getOrderCountByProject(projectId, StatusEnum.ORDER_PAYED_INVESTED.getStatus());
    }
    
    /**
     * 通过转让项目id查询该项目已经支付成功的订单数
     * @param transferId
     * @return
     */
    public int getPayedCountByTransferProject(Long transferId) {
    	return orderMapper.getTransferOrderCountByProject(transferId, StatusEnum.ORDER_PAYED_INVESTED.getStatus());
    }
    
    
    /**
     * 通过项目id查询该项目支付中的订单数
     * @param projectId
     * @return
     */
    public int getPayingCountByProject(Long projectId) {
    	return orderMapper.getOrderCountByProject(projectId, StatusEnum.ORDER_WAIT_PROCESS.getStatus());
    }
    
    /**
     * 通过转让项目id查询该项目支付中的订单数
     * @param transferId
     * @return
     */
    public int getPayingCountByTransferProject(Long transferId) {
    	return orderMapper.getTransferOrderCountByProject(transferId, StatusEnum.ORDER_WAIT_PROCESS.getStatus());
    }
    
    /**
     * 通过项目id查询该项目支付中的订单金额
     * @param projectId
     * @return
     */
    public BigDecimal getPayingAmountByProject(Long projectId) {
    	BigDecimal payingAmount = orderMapper.getOrderSumAmountByProject(projectId, StatusEnum.ORDER_WAIT_PROCESS.getStatus());
    	if(payingAmount==null) {
    		return BigDecimal.ZERO.setScale(2);
    	} else {
    		return payingAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    	}
    }
    
    /**
     * 通过转让项目id查询该项目支付中的订单金额
     * @param transferId
     * @return
     */
    public BigDecimal getPayingAmountByTransferProject(Long transferId) {
    	BigDecimal payingAmount = orderMapper.getTransferPayingAmountByProject(transferId, StatusEnum.ORDER_WAIT_PROCESS.getStatus());
    	if(payingAmount==null) {
    		return BigDecimal.ZERO.setScale(2);
    	} else {
    		return payingAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    	}
    }
    
    /**
     * 通过项目id查询该项目已经支付成功的订单金额
     * @param projectId
     * @return
     */
    public BigDecimal getPayedAmountByProject(Long projectId) {
    	BigDecimal payedAmount = orderMapper.getOrderSumAmountByProject(projectId, StatusEnum.ORDER_PAYED_INVESTED.getStatus());
    	if(payedAmount==null) {
    		return BigDecimal.ZERO.setScale(2);
    	} else {
    		return payedAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    	}
    }
    
    /**
     * 通过转让项目id查询该项目已经支付成功的订单金额
     * @param transferId
     * @return
     */
    public BigDecimal getPayedAmountByTransferProject(Long transferId) {
    	BigDecimal payedAmount = orderMapper.getTransferPayingAmountByProject(transferId, StatusEnum.ORDER_PAYED_INVESTED.getStatus());
    	if(payedAmount==null) {
    		return BigDecimal.ZERO.setScale(2);
    	} else {
    		return payedAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    	}
    }
    
    /**
     * 
     * @Description:平台交易笔数
     * @return
     * @author: chaisen
     * @time:2016年8月28日 上午10:47:52
     */
    public int getTotalTransactionCount() {
    	return transactionService.getTotalTransactionCount();
    }
    
    /**
     * 
     * @Description:根据交易id获取抽奖次数
     * @param transactionId
     * @return
     * @author: chaisen
     * @time:2016年11月8日 上午9:49:22
     */
    public int getLotteryNum(Long orderId) {
    	return transactionService.getLotteryNum(orderId);
    }
    /**
     * 
     * @Description:是否是快投有奖项目
     * @param orderId
     * @return
     * @author: chaisen
     * @time:2016年11月9日 上午9:33:41
     */
    public boolean isQuickLotteryProject(Long projectId) {
    	return transactionService.isQuickLotteryProject(projectId);
    }
    
    
}
