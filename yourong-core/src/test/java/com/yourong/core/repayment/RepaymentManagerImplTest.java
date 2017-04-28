package com.yourong.core.repayment;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.BaseTest;
import com.yourong.core.repayment.manager.AfterHostingPayHandleManager;
import com.yourong.core.repayment.manager.BeginToRepayment;
import com.yourong.core.repayment.manager.RepaymentManager;
import com.yourong.core.tc.dao.HostingCollectTradeMapper;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.TransactionInterest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * 还本付息测试类
 * Created by peng.yong on 2015/12/29.
 */
public class RepaymentManagerImplTest extends BaseTest {
    @Autowired
    private RepaymentManager  repaymentManager;

    @Autowired
    private BeginToRepayment beginToRepayment;

    @Autowired
    private HostingCollectTradeMapper hostingCollectTradeMapper;

    @Autowired
    private AfterHostingPayHandleManager afterHostingPayHandleManager;


    //@Test
    @Rollback(false)
    public  void  beginToRepaymentTest()throws  Exception{
        beginToRepayment.beginToRepayment();
    }

   // @Test
    public void queryAlreadyHostingCollectTradeForPalform()throws  Exception{
        HostingCollectTrade hostingCollectTrade = hostingCollectTradeMapper.queryAlreadyHostingCollectTradeForPalform(110800001054L, 4, "1");
        System.out.println(hostingCollectTrade.getSourceId());
    }
    @Test
    @Rollback(false)
    public void  afterHostingPayHandleManagerTest()throws  Exception{
        String tradeStatus ="TRADE_FINISHED";
        String tradeNo="YRPT20160219101627051001034";
        String outTradeNo="";
        ResultDO<TransactionInterest> transactionInterestResultDO = afterHostingPayHandleManager.afterPayInterestAndPrincipal(tradeStatus,tradeNo,outTradeNo);
    }


}
