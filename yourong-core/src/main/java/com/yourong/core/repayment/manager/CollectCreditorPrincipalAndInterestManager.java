package com.yourong.core.repayment.manager;

import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.util.Collections3;
import com.yourong.core.tc.model.biz.TransactionInterestForPay;

import java.util.List;

/**
 * 代收债权人的本金和利息， 还有平台贴补的利息
 * Created by py on 2015/7/21.
 */
public interface CollectCreditorPrincipalAndInterestManager {

    public  List<TransactionInterestForPay> createHostingCollectTradeForPayInterestAndPrincipal();



}
