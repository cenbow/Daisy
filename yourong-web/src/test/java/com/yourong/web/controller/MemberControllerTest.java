package com.yourong.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.yourong.web.BaseWebControllerTest;
import com.yourong.web.service.MemberService;

public class MemberControllerTest extends BaseWebControllerTest {
	
	 @Autowired
	 private MemberService memberService;
	
 	//@Test  
    public void test() throws Exception {  
        mockMvc.perform((get("/erere"))).andExpect(status().isNotFound())  
               .andDo(print())  
                ;  
    }
 	private String json ="{\"memberID\":1234,\"oldPassword\":\"1235\",\"newPassword\":\"213123123\"}"; 
// 	@Test
 	public void testUpdatePassword() throws Exception{ 		
 		
 		  mockMvc.perform(post("/member/updatePassword").param(json)
// 				  .param("oldPassword", "123124")
// 				  .param("newPassword", "sdfsdfsd") 				  
 				 .accept(MediaType.APPLICATION_JSON)
 				  )
 		 .andDo(print())  ;  
 	}
 	
// 	@Test
 	public void testRecharge() throws Exception {
 		setLoginedSession(15837012873L);
 		mockMvc.perform(post("/memberBalance/recharge").session(session).param("withdrawAmount", "100")).andDo(MockMvcResultHandlers.print()).andReturn();
    }
 	
// 	@Test
    public void testWithdraw() throws Exception {
 		setLoginedSession(15837012873L);
 		mockMvc.perform(post("/memberBalance/withdraw").session(session).param("withdrawAmount", "100")).andDo(MockMvcResultHandlers.print()).andReturn();
    }
    
    @Test
 	public void testHandWithholdAuthority() throws Exception {
 		setLoginedSession(15837012873L);
 		mockMvc.perform(post("/member/queryWithholdAuthority").session(session).param("type", "1")).andDo(MockMvcResultHandlers.print()).andReturn();
    }
    
}