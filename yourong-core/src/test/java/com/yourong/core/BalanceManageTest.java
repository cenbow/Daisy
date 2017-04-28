package com.yourong.core;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.BalanceManager;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2015/9/17.
 */
public class BalanceManageTest extends BaseTest {
    @Autowired
    private BalanceManager balanceManager;

   // @Test
    //@Rollback(false)
    public void  incrExchangePlatformTotalPointTest(){
        balanceManager.incrExchangePlatformTotalPoint(new BigDecimal("30"));
    }
    
    @Test
    @Rollback(false)
    public void  synThirdPayEaringByAdminTest() throws ManagerException {
        balanceManager.synThirdPayEaringByAdmin(110800001050L,new Date(),new Date());
    }





}
