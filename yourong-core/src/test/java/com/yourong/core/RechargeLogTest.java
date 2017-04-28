package com.yourong.core;

import com.yourong.core.fin.dao.RechargeLogMapper;
import com.yourong.core.fin.model.RechargeLog;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;

import java.awt.*;

public class RechargeLogTest extends BaseTest{
	
	@Autowired
	private RechargeLogMapper rechargeLogMapper;
	
//	@Autowired
//	private BalanceManager  balanceManager;
	
	
	//@Test
//	public void testRechargeLogMapper(){
//		BigDecimal totalRecharge = rechargeLogMapper.totalRecharge(1);
//		System.out.println(totalRecharge);
//	}
	
//	@Test
//	@Rollback(false)
//	public void testManget(){
//		try {
//			Balance queryBalance = balanceManager.queryBalance(9898000123L, TypeEnum.BALANCE_TYPE_PROJECT);
//		} catch (ManagerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
    @Test
    public  void testRechalog(){
        RechargeLog rechargeLog = rechargeLogMapper.selectRechargeLogByTradeNo("YRRC1411471956727000081");
        System.out.printf("s-----------------------------sss"+ rechargeLog.getBankCode());
    }
	
	


}
