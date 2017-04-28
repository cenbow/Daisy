package com.yourong.core.repayment.manager;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.tc.model.TransactionInterest;

/**
 * 代付后的业务处理逻辑
 * Created by Administrator on 2015/7/29.
 */
public interface AfterHostingPayHandleManager {
    public ResultDO<TransactionInterest> afterPayInterestAndPrincipal(String tradeStatus, String tradeNo,String outTradeNo)throws Exception;
    
    public void afterHostingPayTradeSucess(TransactionInterest transactionInterest)throws Exception;

}
