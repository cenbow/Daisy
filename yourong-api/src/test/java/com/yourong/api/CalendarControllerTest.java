package com.yourong.api;

import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.yourong.api.dto.RechargeDto;
import com.yourong.api.service.CalendarService;
import com.yourong.api.service.MemberService;

public class CalendarControllerTest extends BaseWebControllerTest {

	@Autowired
	private MemberService memberService;

	private static MockHttpServletRequest request;

    private static MockHttpServletResponse response;

	@BeforeClass
	public static void before() {
		request = new MockHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		response = new MockHttpServletResponse();
	}
	
	private static final Logger logger =  LoggerFactory.getLogger(ActivityControllerTest.class);
	
	@Test
    public void testCalendar() throws Exception {
		RechargeDto dto=new RechargeDto();
		Long memberId=110800000211L;
		String ip="";
		dto.setAmount("0.01");
		dto.setRechargeType(1);
		memberService.createHostingDepositAndRechargeLog(memberId,ip,dto,1);
		//memberService.createWithdrawAndWithdrawLog(memberId, ip, new BigDecimal(2), new BigDecimal(1));
	}
}
