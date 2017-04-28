package com.yourong.core.repayment.manager;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.tc.model.HostingCollectTrade;

/**
 * 代收之后处理的业务逻辑
 * Created by py on 2015/7/29.
 */
public interface AfterHostingCollectTradeHandleManager {
    ResultDO<HostingCollectTrade> afterHostingCollectTradeForPayInterestAndPrincipal(String tradeNo, String outTradeNo, String tradeStatus) throws Exception;
}
