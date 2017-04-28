package com.yourong.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.mc.manager.ActivityRuleManager;
import com.yourong.core.mc.model.biz.ActivityForAnniversary;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.web.BaseWebControllerTest;

public class ActivityLotteryServiceTest extends BaseWebControllerTest{
	
	@Autowired
	private ActivityLotteryService activityLotteryService;
	
	@Autowired
	private DrawByPrizeDirectly drawByPrizeDirectly;

	@Autowired
	private ActivityRuleManager activityRuleManager;
	
	@Autowired
	private MemberManager memberManager;
	
	private static MockHttpServletRequest request;

    private static MockHttpServletResponse response;

	@BeforeClass
	public static void before() {
		request = new MockHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		response = new MockHttpServletResponse();
	}
    
	//controller test
	// @Test
	public void testActivity() {
		try {
			Page<Member> pageRequest = new Page<Member>();
			pageRequest.setiDisplayStart(1);
			pageRequest.setiDisplayLength(100);
			Map<String, Object> m = new HashMap<String, Object>();
			Page<Member> p = memberManager.findByPage(pageRequest, m);
			List<Member> l = p.getData();
			System.out.println(l.size());
			Long startTime = System.currentTimeMillis();
			for(Member member : l) {
				activityLotteryService.anniversaryReceivePrize(member.getId(), 131l);
			}
			Long endTime = System.currentTimeMillis();
			System.out.println("总耗时："+(endTime-startTime)+"ms");
			//ResultDO<ActivityForAnniversary> rDO = activityLotteryService.anniversaryReceivePrize(110800001021L, 131l);
//			RuleBody ruleBody = new RuleBody();
//			ruleBody.setActivityId(131L);
//			ruleBody.setMemberId(110800001021L);
//			ruleBody.setDeductMode(StatusEnum.LOTTERY_DEDUCT_MODE_FREE.getStatus());
//			ruleBody.setCycleStr("131");
//			ruleBody.setActivityName("【干杯！我们的纪念日】奖励大放送");
//			
//				ActivityRule rule = activityRuleManager.findRuleByActivityId(131L);
//				String jsonStr = rule.getRuleParameter();
//				List<RewardsBase> rBaseList = JSON.parseArray(jsonStr, RewardsBase.class);
//				drawByPrizeDirectly.drawLottery(ruleBody, rBaseList.get(0), TypeEnum.ACTIVITY_LOTTERY_VALIDATE_PARTICIPATE.getCode());
//				
	        System.out.println("#######################################");
		}catch(Exception e) {
			e.getMessage();
			e.getLocalizedMessage();
			e.printStackTrace();
		}
	}
	
	// @Test
	public void test25Grid() {
		Long memberId = 110800001021L;
		Long startTime = System.currentTimeMillis();
		for(int i = 1; i <= 2; i++) {
			activityLotteryService.anniversaryTwentyFiveGrid(memberId, 1);
		}
		Long endTime = System.currentTimeMillis();
		System.out.println("总耗时："+(endTime-startTime)+"ms");
	}
	
	@Test
	public void getRedURL() {
		Long memberId = 110800000604L;
		Long tansactionId = 888800001114L;
		ResultDO<ActivityForAnniversary> rDO = activityLotteryService.anniversaryShareUrl(tansactionId, memberId);
		System.out.println(rDO.getResult().getEncryptUrl());
	}
	
}
