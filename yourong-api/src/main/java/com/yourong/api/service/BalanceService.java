package com.yourong.api.service;

import java.math.BigDecimal;

import com.yourong.api.dto.ResultDTO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.biz.OverduePopularityBiz;

public interface BalanceService {
  /**
   * 充值成功，从第三方同步资金	 写入到我们的资金表 记录流水号
   */
  public void  topUpFromThirdPay(long memberID,BigDecimal income,String sourceId) throws Exception;

  /**
   * 查询余额（主要用于查询平台投资总额和平台收益利息总额）
   * @param type
   * @return
   */
  public BigDecimal getBalanceByType(TypeEnum type); 
  
  /**
   * 查询余额
   * @param sourceID
   * @param type
   * @return
   */
  public Balance queryBalance(Long sourceID, TypeEnum type);
  
  /**
   *  初始化资金表，资金额为0
   * @param sourceID
   * @param type
   */
  public void  initBalance(Long sourceID, TypeEnum type);

  /**
   *  判断余额是否等于0.
   *  先同步第三方支付余额，然后判断
   * @param memberID
   * @return
   */
  public boolean balanceIsZero(Long memberID);
  
  /**
   * 查询用户余额统计信息
   * @param memberId
   * @return
   */
  public ResultDTO queryMemberBalance(Long memberId);
  /**
   *  判断资产是否等于0.
   *  先同步第三方支付余额，然后判断
   * @param memberId
   * @return
   */
  public boolean isZeroMemberTotalAsset(Long memberId);


  public ResultDTO queryMemberBalance3(Long memberId);
  
  
  public ResultDTO queryMemberWorth(Long memberId);
  
  public ResultDTO queryMemberEarn(Long memberId);
  
  
  /**
   * 提现手续费
   * @param withdrawAmount
   * @param memberID
   * @return
   */
  public BigDecimal getWithdrawFee(BigDecimal withdrawAmount, Long memberID);

  /**
   * 查询过期人气值
   * @param memberId
   * @return
     */
  public OverduePopularityBiz queryOverduePopularity(Long memberId);
}
