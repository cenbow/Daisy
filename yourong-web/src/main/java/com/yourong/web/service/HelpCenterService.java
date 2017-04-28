package com.yourong.web.service;

import java.util.HashMap;
import java.util.List;

import com.yourong.core.mc.model.HelpCenterQuestionShow;


public interface HelpCenterService {

	public List<HelpCenterQuestionShow> getHotFromRedis();

	public HashMap<String,List<HelpCenterQuestionShow>> getCommonFromRedis();

	public List<HelpCenterQuestionShow> getAnswerFromRedis();

	public List<HelpCenterQuestionShow> getNewComerFromRedis();
	
	public void memberBehaviorLogInsert(Long memberId, String sourceId,
			Integer type, Integer operatWay,String device, String deviceParam,
			String anchor, String remarks);
	
}
