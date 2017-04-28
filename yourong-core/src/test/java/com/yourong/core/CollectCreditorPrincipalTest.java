package com.yourong.core;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.repayment.manager.BeginToRepayment;
import com.yourong.core.repayment.manager.CollectCreditorPrincipalAndInterestManager;
import com.yourong.core.repayment.manager.SynchronizedHostingCollectTradeManager;
import com.yourong.core.repayment.manager.SynchronizedHostingPayTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * Created by py on 2015/7/21.
 */
public class CollectCreditorPrincipalTest extends BaseTest  {
    @Autowired
    private CollectCreditorPrincipalAndInterestManager collectCreditorPrincipalAndInterestManager;
    @Autowired
    private     BeginToRepayment beginToRepayment;

    @Autowired
    private HostingPayTradeManager hostingPayTradeManager;

    @Autowired
    private SynchronizedHostingCollectTradeManager synchronizedHostingCollectTradeManager;

    @Autowired
    private SynchronizedHostingPayTradeManager synchronizedHostingPayTradeManager;

    //@Test
    //@Rollback(false)
    public void hostingCollectTradeForPayInterestAndPrincipalTest() {
        collectCreditorPrincipalAndInterestManager.createHostingCollectTradeForPayInterestAndPrincipal();
    }
    @Test
    @Rollback(false)
    public void beginToRepaymentTest() {
        try {
            beginToRepayment.beginToRepayment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Rollback(false)
    public void synchronizedHostingCollectTradeTest() {
        try {
            synchronizedHostingCollectTradeManager.synchronizedHostingCollectTrade();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
public void selectProjectInverstAndHostingPayTradeTest(){
        Long projectId =989800011L;
        try {
            hostingPayTradeManager.selectProjectInverstAndHostingPayTrade(projectId);
        } catch (ManagerException e) {
           logger.error(e);
        }
    }

    @Test
    @Rollback(false)
    public void synchronizedHostingPayTradeeTest() {
        try {
            synchronizedHostingPayTradeManager.synchronizedHostingPayTrade();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
