package com.yourong.api.utils;

import com.yourong.api.service.TransactionService;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.tc.dao.OrderMapper;
import com.yourong.core.tc.model.biz.TransactionForProject;

import java.math.BigDecimal;
import java.util.List;

/**
 * 投资util
 * @author Leon Ray
 * 2014年10月8日-下午3:06:50
 */
public class TransactionUtils {
	
	private static TransactionService transactionService = SpringContextHolder.getBean(TransactionService.class);
	
	private static OrderMapper orderMapper = SpringContextHolder.getBean(OrderMapper.class);
	
	
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
     * 获取用户投资总额,过滤直投项目
     * @param memberId
     * @return
     */
    public BigDecimal getMemberTotalInvestAmountDirectProject(Long memberId) {
    	return transactionService.getMemberTotalInvestAmountDirectProject(memberId);
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
    	BigDecimal totalTransactionInterest = transactionService.getTotalTransactionInterestByProject(projectId);
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
     * 通过项目id查询该项目支付中的订单数
     * @param projectId
     * @return
     */
    public int getPayingCountByProject(Long projectId) {
    	return orderMapper.getOrderCountByProject(projectId, StatusEnum.ORDER_WAIT_PROCESS.getStatus());
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
    
    
}
