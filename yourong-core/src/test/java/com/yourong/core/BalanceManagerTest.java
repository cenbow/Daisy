package com.yourong.core;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.BalanceManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

/**
 * Created by py on 2015/10/26.
 */
public class BalanceManagerTest extends BaseTest  {
    @Autowired
    private BalanceManager balanceManager;

    @Test
    @Rollback(false)
    public void updateByIdAndTypeForLottyTest(){
        try {
            int i = balanceManager.updateByIdAndTypeForLotty(new BigDecimal("30"), 110800000199L);
            System.out.println(i);
        } catch (Exception e) {
        e.printStackTrace();
        }
    }



}
