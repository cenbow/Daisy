package com.yourong.api.service;

import java.util.HashMap;
import java.util.List;

import com.yourong.core.mc.model.HelpCenterQuestionShow;


public interface HelpCenterService {

	public List<HelpCenterQuestionShow> getHotFromRedis();

	public HashMap<String,List<HelpCenterQuestionShow>> getCommonFromRedis();

	public List<HelpCenterQuestionShow> getAnswerFromRedis();
	
}
