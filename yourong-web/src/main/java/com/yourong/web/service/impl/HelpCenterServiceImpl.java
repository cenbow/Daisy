package com.yourong.web.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.cache.RedisManager;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.mc.manager.HelpCenterManager;
import com.yourong.core.mc.model.HelpCenterQuestionShow;
import com.yourong.core.uc.manager.MemberBehaviorLogManager;
import com.yourong.web.service.HelpCenterService;

@Service
public class HelpCenterServiceImpl implements HelpCenterService {
	@Autowired
	private HelpCenterManager helpCenterManager;
	
	@Autowired
	private MemberBehaviorLogManager memberBehaviorLogManager;

	@Override
	public List<HelpCenterQuestionShow> getHotFromRedis() {
			boolean isExit = RedisManager.isExitByObjectKey("PCHotQuestion");
			List<HelpCenterQuestionShow> listHot = null;
			if (isExit) {
				listHot = (List<HelpCenterQuestionShow>) RedisManager.getObject("PCHotQuestion");
			}else{
				try {
					helpCenterManager.flushQuestion(0);
				} catch (ManagerException e) {
					e.printStackTrace();
				}
				listHot = (List<HelpCenterQuestionShow>) RedisManager.getObject("PCHotQuestion");
			}
			return listHot;
	}
	
	
	@Override
	public HashMap<String,List<HelpCenterQuestionShow>> getCommonFromRedis() {
		boolean isExit = RedisManager.isExitByObjectKey("PCCommonQuestion");
		HashMap<String,List<HelpCenterQuestionShow>> quesMap = null;
		if (isExit) {
			quesMap = (HashMap<String, List<HelpCenterQuestionShow>>) RedisManager.getObject("PCCommonQuestion");	
		}else{		
			try {
				helpCenterManager.flushQuestion(0);
			} catch (ManagerException e) {
				e.printStackTrace();
			}
			quesMap = (HashMap<String, List<HelpCenterQuestionShow>>)  RedisManager.getObject("PCCommonQuestion");
		}
		return quesMap;
	}
	
	@Override
	public List<HelpCenterQuestionShow> getAnswerFromRedis() {
			boolean isExit = RedisManager.isExitByObjectKey("PCQuesAnswer");
			List<HelpCenterQuestionShow> listAnswer = null;
			if (isExit) {
				listAnswer = (List<HelpCenterQuestionShow>) RedisManager.getObject("PCQuesAnswer");
			}else{
				try {
					helpCenterManager.flushQuestion(0);
				} catch (ManagerException e) {
					e.printStackTrace();
				}
				listAnswer = (List<HelpCenterQuestionShow>) RedisManager.getObject("PCQuesAnswer");
			}
			return listAnswer;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<HelpCenterQuestionShow> getNewComerFromRedis() {
		boolean isExit = RedisManager.isExitByObjectKey("PCNewComerQuestion");
		List<HelpCenterQuestionShow> listNewComer = null;
		if (isExit) {
			listNewComer = (List<HelpCenterQuestionShow>) RedisManager.getObject("PCNewComerQuestion");
		}else{
			try {
				helpCenterManager.flushQuestion(0);
			} catch (ManagerException e) {
				e.printStackTrace();
			}
			listNewComer = (List<HelpCenterQuestionShow>) RedisManager.getObject("PCNewComerQuestion");
		}
		return listNewComer;
	}


	@Override
	public void memberBehaviorLogInsert(Long memberId, String sourceId,
			Integer type, Integer operatWay, String device, String deviceParam,
			String anchor, String remarks) {
		memberBehaviorLogManager.logInsertA(memberId, sourceId, type,
				operatWay, device, deviceParam, anchor, remarks);
		
	}


}
