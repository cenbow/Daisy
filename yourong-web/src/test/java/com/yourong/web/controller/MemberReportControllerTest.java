package com.yourong.web.controller;

import java.util.Locale;

import com.yourong.web.BaseWebControllerTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberReport;
import com.yourong.web.service.MemberReportService;

public class MemberReportControllerTest  extends BaseWebControllerTest {
    //@Test
    public void test() throws Exception {
        String message = this.wac.getMessage("ssss", null, Locale.CHINA);
        System.out.println(message);
//	 	   String message1 = this.wac.getMessage("ssss", null, Locale.CHINA);
//	 	    System.out.println(message1);


//	        mockMvc.perform((get("/erere"))).andExpect(status().isNotFound())
//	               .andDo(print())
//	                ;
    }


    @Autowired
    private MemberReportService meberReportService;
		
		@Test
		public void testManget() throws Exception{
			try {
				MemberReport memberReport=new MemberReport();
				memberReport.setAddress("22");
				memberReport.setMobile(1358888L);
				memberReport.setMemberId(22L);
				memberReport.setTravelMode(1);
				ResultDO<MemberReport> i=meberReportService.saveReport(memberReport);
			} catch (ManagerException e) {
				e.printStackTrace();
			}
		}
		
	 	
	   
	    
}
