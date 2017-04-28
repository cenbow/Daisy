package com.yourong.core.fin.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.RechargeLog;

public interface RechargeLogManager {

    int insertSelective(RechargeLog record) throws ManagerException;

    Page<RechargeLog> findByPage(Page<RechargeLog> pageRequest, Map<String, Object> map) throws ManagerException;
    /**
     * 统计总数 分页用
     * @param map
     * @return
     */
    int selectForPaginTotalCount(Map<String, Object> map)throws ManagerException;
    /**
     *  分页查询
     * @param map
     * @return
     */
    List<RechargeLog> selectForPagin(Map<String, Object> map)throws ManagerException;
    

    int updateStateByOutTradeNo(BigDecimal amount, int status,String rechargeNo, String outTradeNo) throws ManagerException;

    /**
     * 根据流水号查
     * @param tradeNo
     * @return
     * @throws ManagerException
     */
    public RechargeLog   getRechargeLogByTradeNo(String tradeNo) throws ManagerException;

    int updateStateByID(long id,  int status,int eqStatus, String remarks)throws ManagerException;
    /**
     * 成功充值次数
     * @param memberId
     * @return
     * @throws ManagerException
     */
	public int countRecharge(long memberId) throws ManagerException;
	/**
	 * 成功充值总金额
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public BigDecimal totalRecharge(long memberId) throws ManagerException;
	
	/**
	 * 充值成功
	 * 
	 * @param rechargeNo
	 * @param outTradeNo
	 * @param memo
	 * @param memberBankCardId
	 * @param bankCode
	 * @param payMethod
	 * @return
	 * @throws Exception
	 */
	public boolean rechargeSuccess(String rechargeNo, String outTradeNo, String memo, 
			Long memberBankCardId, String bankCode, Integer payMethod) throws Exception;
	
	/**
	 * 充值失败
	 * 
	 * @param rechargeNo
	 * @param outTradeNo
	 * @param memo
	 * @param memberBankCardId
	 * @param bankCode
	 * @param payMethod
	 * @return
	 * @throws Exception
	 */
	public boolean rechargeFailed(String rechargeNo, String outTradeNo, String memo, 
			Long memberBankCardId, String bankCode, Integer payMethod) throws Exception;

    /**
     *
     * @param map
     * @return
     * @throws ManagerException
     */
    public  List<RechargeLog> selectRechargeByMap(Map<String, Object> map)throws ManagerException;
    
    /**
     * 充值记录分页查询
     * @param pageRequest
     * @param map
     * @return
     * @throws ManagerException
     */
    Page<RechargeLog> queryLogByPage(Page<RechargeLog> pageRequest, Map<String, Object> map) throws ManagerException;
    
    /**
     * 查询用户首次在APP直接充值记录
     * @param memberId
     * @param startDate
     * @param endDate
     * @return
     * @throws ManagerException
     */
    RechargeLog queryAppFirstRechargeAmount(Long memberId, Date startDate, Date endDate) throws ManagerException;
    
    RechargeLog selectByPrimaryKey(Long id) throws ManagerException;
    
    /**
     * 查询选择状态的充值记录
     * 
     * @param status
     * @param rechargeNo
     * @param startTime
     * @param endTime
     * @return
     * @throws ManagerException
     */
    List<RechargeLog> selectSynchronizedRecharge(Integer status, String rechargeNo, String startTime, String endTime)throws ManagerException;
    
    /**
     * 锁定查询充值记录
     * 
     * @param id
     * @return
     * @throws ManagerException
     * @author luwenshan
     * @time 2016年8月11日 下午4:51:31
     */
    RechargeLog selectByPrimaryKeyForLock(Long id) throws ManagerException;
    
}
