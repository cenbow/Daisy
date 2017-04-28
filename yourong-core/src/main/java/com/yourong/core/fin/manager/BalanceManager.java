package com.yourong.core.fin.manager;

import java.math.BigDecimal;


import java.util.Date;

import org.springframework.stereotype.Repository;

import com.yourong.common.enums.BalanceAction;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.model.Balance;

@Repository
public interface BalanceManager {
	
	/**
	 * 查询余额表 
	 * @param memberId
	 * @param type
	 * @return
	 * @throws ManagerException
	 */
    public Balance queryBalance(Long sourceId, TypeEnum type) throws ManagerException;
	
	/**
	 * 查询余额表 ，锁住余额
	 * @param memberId
	 * @param type
	 * @return
	 * @throws ManagerException
	 */
    public Balance queryBalanceLocked(Long sourceId, TypeEnum type) throws ManagerException;

    public int insert(Balance record) throws ManagerException;    
    /**
     * 余额
     * @param type 余额类型
     * @param amount 金额
     * @param sourceId 来源id(用户id、项目id)
     * @return
     * @throws ManagerException
     */
    public Balance reduceBalance(TypeEnum type, BigDecimal amount, Long sourceId, BalanceAction action) throws ManagerException;
    
    /**
     * 增加余额
     * @param type 余额类型
     * @param amount 金额
     * @param sourceId 来源id(用户id、项目id)
     * @return
     * @throws ManagerException
     */
    public Balance increaseBalance(TypeEnum type, BigDecimal amount, Long sourceId) throws ManagerException;
    
    /**
     * 新增余额
     * @param type 余额类型
     * @param balance 余额
     * @param sourceId 来源id(用户id、项目id)
     * @return
     * @throws ManagerException
     */
    public int insertBalance(TypeEnum type, BigDecimal balance, Long sourceId) throws ManagerException;
    
    
    /**
     * 新增余额
     * @param type 余额类型
     * @param balance 余额
     * @param sourceId 来源id(用户id、项目id)
     * @return
     * @throws ManagerException
     */
    public int insertBalance(TypeEnum type, BigDecimal balance,BigDecimal availableBalance, Long sourceId) throws ManagerException;
    
    
    /**
     * 更新余额
     * @param balance
     * @param sourceId
     * @return
     * @throws ManagerException
     */
    public int resetProjectBalance(BigDecimal balance, Long sourceId) throws ManagerException;

    /**
     * 冻结余额
     * @param balanceTypeProject
     * @param investAmount
     * @param projectId
     * @return
     */
	public Balance frozenBalance(TypeEnum balanceTypeProject,
			BigDecimal investAmount, Long projectId) throws ManagerException;

	/**
	 * 解冻余额
	 * @param balanceTypeProject
	 * @param investAmount
	 * @param projectId
	 * @return
	 */
	public Balance unfrozenBalance(TypeEnum balanceTypeProject,
			BigDecimal investAmount, Long projectId) throws ManagerException;
	
	/**
     * 更新余额
     * @param balance
     * @return
     * @throws ManagerException
     */
	public int   updateBalanceByID(BigDecimal balance,BigDecimal availableBalance,Long id) throws  ManagerException;

	/**
	 * 同步余额
	 * 1、查询新浪余额（企业账号是基本户）
	 * 2、查询平台余额宝
	 * 3、判断新浪返回的余额是否和平台余额表一致
	 * 4、不一致则更新平台余额，返回余额
	 * @param memberId
	 * @param balanceTypePiggy
	 * @return
	 */
	public Balance synchronizedBalance(Long sourceId, TypeEnum balanceTypePiggy)throws  ManagerException;


    /**
     *  充值回调接口 同步第三方支付 更新余额表， 记录流水号
     * @param memberID
     * @param income 充值金额
     * @param sourceId 第三方流水号
     *  @param  balanceType  余额类型
     * @return
     * @throws ManagerException
     */
    public Balance rechargeFromThirdPay(long memberID,BigDecimal income,String sourceId,TypeEnum balanceType) throws ManagerException;

    /**
     *   提现回调接口 同步第三方支付 更新余额表， 记录流水号
     * @param memberID
     * @param income 提现 金额
     * @param sourceId 第三方流水号
     *   @param  balanceType 余额类型
     * @return
     * @throws ManagerException
     */
    public Balance withdrawFromThirdPay(long memberID,BigDecimal income,String sourceId,TypeEnum balanceType) throws ManagerException;


    /**
     * 查询 第三方余额 ，不更新余额表
     * @param memberId
     * @param balanceTypePiggy
     * @return
     * @throws ManagerException
     */
    public Balance queryFromThirdPay(Long memberId, TypeEnum balanceTypePiggy)throws ManagerException;

    /**
     * 减少人气值余额
     * @param amount
     * @param sourceId
     * @return
     * @throws ManagerException
     */
    public Balance reducePopularityBalance(BigDecimal amount, Long sourceId) throws ManagerException;
    
    
    /**
     * 会员发起提现冻结可用余额 
     * @param amount
     * @param sourceId
     * @return
     * @throws ManagerException
     * author: pengyong
     * 下午4:53:36
     */   
    public Balance frozenMemberBalance(BigDecimal amount, Long sourceId)throws ManagerException;
    
    /**
     * 提现失败   解冻余额
     * @param amount
     * @param sourceId
     * @return
     * @throws ManagerException
     * author: pengyong
     * 下午4:53:04
     */
    public Balance unFrozenMemberBalance(BigDecimal amount, Long sourceId)throws ManagerException;
    
    /**
     * 同步新浪存钱罐 收益， 和存钱罐余额
     * @return
     * @throws ManagerException
     * author: pengyong
     * 下午4:15:51
     */
    public void  synThirdPayEaring(long memberId)throws ManagerException;


    /**
     *   查询会员所有资产，资产总计＝存钱罐余额＋待收本金＋待收收益
     * @param memberId
     * @return
     * @throws ManagerException
     */
    BigDecimal getMemberTotalAsset(Long memberId) throws  ManagerException;

    /**
     *   会员所有资产 ==0  返回true，否则false
     * @param memberId
     * @return
     * @throws ManagerException
     */
     Boolean  isZeroMemberTotalAsset(Long memberId)throws ManagerException;
     
     /**
      * 平台总投资额
      * @return
      * @throws ManagerException
      */
     public BigDecimal getPaltformTotalInvest() throws ManagerException;
     
     /**
      * 退回提现手续费
      * @param memberId
      * @param sourceId
      * @throws ManagerException
      */
     public void refundWithdrawFee(Long memberId, Long sourceId,String remark) throws ManagerException;
     
     /**
      * 冻结人气值
      * @param memberId
      * @return
      * @throws ManagerException
      */
     public Balance frozenPopularityBalance(Long memberId,Integer WithdrawFee)throws ManagerException;
     
     /**
      * 解冻人气值
      * @param memberId
      * @param withdrawLogId
      * @return
      * @throws ManagerException
      */
     public Balance unFrozenPopularityBalance(Long withdrawLogId, Long memberId)throws ManagerException;
     
     /**
      * 扣除提现手续费
      * @param sourceId
      * @return
      * @throws ManagerException
      */
     public Balance reduceWithdrawalsFee(Long memberId, Long sourceId,Integer withdrawFee) throws ManagerException;

    /**
     * 	增加平台兑换人气值总额
     * 	@param outLay
      */
    public int  incrExchangePlatformTotalPoint( BigDecimal outLay);


    /**
     * 	增加平台送出人气值总额
     * 	@param outLay
     */
    public int  incrGivePlatformTotalPoint( BigDecimal outLay);


    /**
     *
     */
    int updateByIdAndTypeForLotty(BigDecimal point, Long sourceId)  throws ManagerException;
    /**
     * 
     * @Description:同步余额
     * @param memberId
     * @param date
     * @throws ManagerException
     * @throws Exception
     * @author: chaisen
     * @time:2016年4月11日 上午11:00:56
     */
    public void synThirdPayEaringByAdmin(long memberId, Date startDate, Date endDate) throws ManagerException;
    
    
    public Balance reduceForShopConsume(Long memberId, Long sourceId,Integer amount) throws ManagerException;

}
