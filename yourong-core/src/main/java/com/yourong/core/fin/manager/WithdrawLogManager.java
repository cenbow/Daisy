package com.yourong.core.fin.manager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.WithdrawLog;

public interface WithdrawLogManager {

    int insertSelective(WithdrawLog record) throws ManagerException;
   
    int updateByPrimaryKey(WithdrawLog record) throws ManagerException;
    
    int updateBywithdrawNo(String withdrawNo,int status) throws ManagerException;
    
    WithdrawLog selectByPrimaryKey(Long id) throws ManagerException;
    
    List<WithdrawLog> selectForPagin(Map<String, Object> map)throws ManagerException;

    int selectForPaginTotalCount(Map<String, Object> map)throws ManagerException;
    
    
    int  updateStateByParimarkey(Long id, int status,int eqStatus, String notice)throws ManagerException;

    int updateStateByID(Long id, int status,int eqStatus, String withdrawNo, String notice,BigDecimal withAmout) throws ManagerException; 
    
    
    public Page<WithdrawLog> findByPage(Page<WithdrawLog> pageRequest,Map<String, Object> map) throws ManagerException;

    public WithdrawLog selectByOuterWithdrawNo(String tradeNo) throws ManagerException; 

    public  WithdrawLog  getWithdrawLogByTradeNo(String tradeNo) throws ManagerException;
    /**
     * 统计成功提现次数
     * @param memberId
     * @return
     * @throws ManagerException
     */
	public int countWithDraw(long memberId) throws ManagerException;
	
	/**
	 * 统计成功提现金额
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public BigDecimal totalWithDraw(long memberId) throws ManagerException;

	/**
	 * 查询需要同步的提现订单
	 * @return
	 * @throws ManagerException
	 */
	List<WithdrawLog> selectSynchronizedWithdraws() throws ManagerException;

	/**
	 * 提现处理中
	 * @param outTradeNo
	 * @param memo
	 * @param bankCardId
	 * @return
	 * @throws Exception
	 */
	public boolean withdrawProcess(String outTradeNo, String memo, Long bankCardId) throws ManagerException;
	
	/**
	 * 提现成功
	 * @param outTradeNo
	 * @param memo
	 * @param bankCardId
	 * @return
	 * @throws Exception
	 */
	public boolean withdrawSuccess(String outTradeNo, String memo, Long bankCardId) throws ManagerException;
    
	/**
	 * 提现失败
	 * @param outTradeNo
	 * @param memo
	 * @param bankCardId
	 * @return
	 * @throws Exception
	 */
	public boolean withdrawFailed(String outTradeNo, String memo, Long bankCardId) throws ManagerException;
	
	public ResultDO<WithdrawLog> cancelWithdraw(Long id,int status);
	 
    /**
     * 网站前台分页
     * @param map
     * @return
     * 
     */
    List<WithdrawLog> selectForPaginWeb(@Param("map") Map<String, Object> map)throws ManagerException;
    /**
     * 网站前台分页
     * @param map
     * @return
     * 
     */
    int selectForPaginTotalCountWeb(@Param("map") Map<String, Object> map)throws ManagerException;
    
    
    /**
     * 会员发送提现，冻结可用余额
     */    
    int  reqSubmitWithDraw(WithdrawLog record)throws ManagerException;

    /**
     *  卡是否正在提现
     */
     boolean  cardIsWithDrawIng(Long memberID,Long cardID)throws ManagerException;

    /**
     * 修改提现时间
     * @param id
     * @return
     * @throws ManagerException
     */
    public void  updateProssTimeById(Long id) throws ManagerException;

    public WithdrawLog selectByPrimaryKeyForLock(Long id) throws ManagerException;
    
    /**
     * 
     * @Description:统计当月提现次数
     * @param withdrawTime
     * @return
     * @throws ManagerException
     * @author: chaisen
     * @time:2016年8月22日 下午4:41:19
     */
	int countWithDrawFree(Long memberId,String withdrawTime) throws ManagerException;

}
