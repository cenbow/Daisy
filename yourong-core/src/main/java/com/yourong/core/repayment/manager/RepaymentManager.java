package com.yourong.core.repayment.manager;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.biz.TransactionInterestForPay;

import java.util.List;

/**
 * Created by py on 2015/7/20.
 */
public interface RepaymentManager {

    /**
     *还本付息
     */
    public void  payInterestAndPrincipal(List<TransactionInterestForPay> hostingCollectTradeForPayInterestAndPrincipal )throws  Exception;

    /**
     * 根据代收好，发起代付给投资者
     * @param tradeNo
     * @throws Exception
     */
    public ResultDO<HostingCollectTrade> processPayInterestAndPrincipal(String tradeNo) throws Exception;

    /**
     * 批量更新交易本息状态
     * @param hostingCollectTradeForPayInterestAndPrincipal
     * @return
     * @throws Exception
     */
	public Integer updateInterestStatus(
			List<TransactionInterestForPay> hostingCollectTradeForPayInterestAndPrincipal) throws Exception;

}
