package com.yourong.core.repayment.manager;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.tc.model.HostingCollectTrade;

/**
 * 代收平台的收益劵后业务处理
 * Created by py on 2015/8/5.
 */
public interface AfterHostingCollectTradeCouponHandleManager {
    /**
     * 代收平台的收益劵后业务处理
     * @param tradeNo
     * @param outTradeNo
     * @param tradeStatus
     * @return
     */
    public ResultDO<HostingCollectTrade> afterHostingCollectTradeCouponBForPayInterestAndPrincipal( String tradeNo, String outTradeNo, String tradeStatus);
}
