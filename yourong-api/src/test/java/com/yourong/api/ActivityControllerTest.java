package com.yourong.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.yourong.api.controller.MemberController;
import com.yourong.api.dto.MemberCheckInfoDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.ActivityLotteryService;
import com.yourong.api.service.MemberService;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.mc.manager.ActivityRuleManager;
import com.yourong.core.uc.manager.MemberManager;


public class ActivityControllerTest  extends BaseWebControllerTest{
	@Autowired
    public RequestMappingHandlerAdapter handlerAdapter;
    
	@Autowired
	private ActivityLotteryService activityLotteryService;
	
	@Autowired
	private DrawByPrizeDirectly drawByPrizeDirectly;

	@Autowired
	private ActivityRuleManager activityRuleManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberController memberController;
	
	private static MockHttpServletRequest request;

    private static MockHttpServletResponse response;

	@BeforeClass
	public static void before() {
		request = new MockHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		response = new MockHttpServletResponse();
	}
	
	private static final Logger logger =  LoggerFactory.getLogger(ActivityControllerTest.class);
	
	//@Test
    public void testRed() throws Exception {
		activityLotteryService.anniversaryGetRed("bTywmPEIC+7DYf4OKwwXGHwtaCIxxKskkgdc8P9IZk9YMvxqmJZdIOLcZSg2mz4ng1Fzzz+JN0Ych7FXqteYiw==", "13564622214");
//		Page<Member> pageRequest = new Page<Member>();
//		pageRequest.setiDisplayStart(1);
//		pageRequest.setiDisplayLength(100);
//		Map<String, Object> m = new HashMap<String, Object>();
//		Page<Member> p = memberManager.findByPage(pageRequest, m);
//		List<Member> l = p.getData();
//		Long startTime = System.currentTimeMillis();
//		for(Member member : l) {
//			activityLotteryService.anniversaryGetRed("bTywmPEIC+7DYf4OKwwXGHwtaCIxxKskkgdc8P9IZk9YMvxqmJZdIOLcZSg2mz4ng1Fzzz+JN0Ych7FXqteYiw==", member.getMobile());
//		}
//		Long endTime = System.currentTimeMillis();
//		System.out.println("总耗时："+(endTime-startTime)+"ms");
        System.out.println("##########");
    }
	    
	@Test
	public void queryMemberSignInInfo(){
		ResultDTO<MemberCheckInfoDto> infoDto= memberService.queryMemberSignInInfo(110800001052L);
		System.out.println(infoDto.getResult());
	}
	
	@Test
	public void login(){
		request.setRequestURI("/security/member/queryMemberSignInInfo");
		request.setMethod(HttpMethod.POST.name());
		request.addParameter("device", "12312313");
//		request.addParameter("token", value);
	}
	
	@Test
	public void query(){
		request.setRequestURI("/security/member/queryMemberSignInInfo");
		request.setMethod(HttpMethod.POST.name());
		request.addParameter("device", "12312313");
		request.addParameter("token", "ndSkQdIUUcW8y5FwJi6pBjAVDW/xEvRgpEQlWDnpEocrz1SncZoMSkHrOeGRaXOfIaxSPC+00Q4kGRwXf8+wMA==");
		request.addHeader("Accept-Version", "1.0.0");
		ModelAndView mv = null;
		try {
			mv = handlerAdapter.handle(request, response,new HandlerMethod(memberController, "queryMemberSignInInfo",HttpServletRequest.class,HttpServletResponse.class));
		} catch (Exception e) {
			logger.error("test error!",e);
		}
	}

	@Test
	public void dynamicInvokeTest(){
		request.setRequestURI("/activity/dynamicInvoke");
		request.setMethod(HttpMethod.POST.name());
		request.addParameter("method", "anniversaryGetRed");
		request.addParameter("1_string_param", "bTywmPEIC+7DYf4OKwwXGHwtaCIxxKskkgdc8P9IZk9YMvxqmJZdIOLcZSg2mz4ng1Fzzz+JN0Ych7FXqteYiw==");
		request.addParameter("2_string_mobile", "13564622214");
		request.addParameter("device", "12312313");
		request.addParameter("token", "ndSkQdIUUcW8y5FwJi6pBjAVDW/xEvRgpEQlWDnpEocrz1SncZoMSkHrOeGRaXOfIaxSPC+00Q4kGRwXf8+wMA==");
		request.addHeader("Accept-Version", "1.0.0");
		ModelAndView mv = null;
		try {
			mv = handlerAdapter.handle(request, response,new HandlerMethod(memberController, "dynamicInvoke",HttpServletRequest.class,HttpServletResponse.class));
		} catch (Exception e) {
			logger.error("test error!",e);
		}
	}
}
