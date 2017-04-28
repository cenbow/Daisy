package com.yourong.api.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.api.service.HelpCenterService;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.mc.manager.HelpCenterManager;
import com.yourong.core.mc.model.HelpCenterQuestionShow;

@Service
public class HelpCenterServiceImpl implements HelpCenterService {
	@Autowired
	private HelpCenterManager helpCenterManager;

	@Override
	public List<HelpCenterQuestionShow> getHotFromRedis() {
			boolean isExit = RedisManager.isExitByObjectKey("clientHotQuestion");
			List<HelpCenterQuestionShow> listHot = null;
			if (isExit) {
				listHot = (List<HelpCenterQuestionShow>) RedisManager.getObject("clientHotQuestion");
			}else{
				try {
					helpCenterManager.flushQuestion(1);
				} catch (ManagerException e) {
					e.printStackTrace();
				}
				listHot = (List<HelpCenterQuestionShow>) RedisManager.getObject("clientHotQuestion");
			}
			return listHot;
	}
	
	
	@Override
	public HashMap<String,List<HelpCenterQuestionShow>> getCommonFromRedis() {
		boolean isExit = RedisManager.isExitByObjectKey("clientCommonQuestion");
		HashMap<String,List<HelpCenterQuestionShow>> quesMap = null;
		if (isExit) {
			quesMap = (HashMap<String, List<HelpCenterQuestionShow>>) RedisManager.getObject("clientCommonQuestion");	
		}else{		
			try {
				helpCenterManager.flushQuestion(1);
			} catch (ManagerException e) {
				e.printStackTrace();
			}
			quesMap = (HashMap<String, List<HelpCenterQuestionShow>>)  RedisManager.getObject("clientCommonQuestion");
		}
		return quesMap;
	}
	
	@Override
	public List<HelpCenterQuestionShow> getAnswerFromRedis() {
			boolean isExit = RedisManager.isExitByObjectKey("clientQuesAnswer");
			List<HelpCenterQuestionShow> listAnswer = null;
			if (isExit) {
				listAnswer = (List<HelpCenterQuestionShow>) RedisManager.getObject("clientQuesAnswer");
			}else{
				try {
					helpCenterManager.flushQuestion(1);
				} catch (ManagerException e) {
					e.printStackTrace();
				}
				listAnswer = (List<HelpCenterQuestionShow>) RedisManager.getObject("clientQuesAnswer");
			}
			return listAnswer;
	}

}
