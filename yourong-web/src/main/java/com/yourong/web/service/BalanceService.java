package com.yourong.web.service;

import java.math.BigDecimal;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.core.fin.model.Balance;

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
   * @param memberId
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
   *   会员所有资产 ==0  返回true，否则false
   * @param memberId
   * @return
   * @throws com.yourong.common.exception.ManagerException
   */
  public boolean isZeroMemberTotalAsset(Long memberId);
  
  /**
   * 用户中心数据
   * @param memberId
   * @return
   */
  public ResultDO getMemberCenterData(Long memberId);
  /**
   * 提现手续费
   * @return
   */
  public BigDecimal getWithdrawFee(BigDecimal withdrawAmount,Long memberId);

}
