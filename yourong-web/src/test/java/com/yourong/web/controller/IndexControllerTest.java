package com.yourong.web.controller;

import java.util.Locale;

import com.yourong.web.BaseWebControllerTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.web.dto.LoginDto;
import com.yourong.web.service.LoginService;
import org.springframework.mock.web.portlet.MockActionRequest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

public class IndexControllerTest  extends BaseWebControllerTest {
    @Test
    public void test() throws Exception {
			this.mockMvc.perform(MockMvcRequestBuilders.get("/")).andDo(MockMvcResultHandlers.print()).andReturn();
    }

	//@Test
	public void testGetHistrory()throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/getTransactionHistory")).andDo(MockMvcResultHandlers.print()).andReturn();
	}



//    @Autowired
//		private LoginService loginService;
//	//@Test
//	 public void testLong(){
//		 loginService.login(new LoginDto());
//	 }
//		@Autowired
//		private BalanceManager  balanceManager;
//		@Test
//		//@Rollback(false)
//		public void testManget(){
//			try {
//				Balance queryBalance = balanceManager.queryBalance(9898000123L, TypeEnum.BALANCE_TYPE_PROJECT);
//			} catch (ManagerException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//
//
	    
}
