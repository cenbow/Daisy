package com.yourong.core.mc.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.dao.HelpCenterLabelMapper;
import com.yourong.core.mc.dao.HelpCenterMapper;
import com.yourong.core.mc.manager.HelpCenterManager;
import com.yourong.core.mc.model.HelpCenterLabel;
import com.yourong.core.mc.model.HelpCenterQuestion;
import com.yourong.core.mc.model.HelpCenterQuestionShow;

@Component
public class HelpCenterManagerImpl implements HelpCenterManager {
	@Autowired
	private HelpCenterMapper helpCenterMapper;
	
	@Autowired
	private HelpCenterLabelMapper helpCenterLabelMapper;

	public Integer deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = helpCenterMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer insert(HelpCenterQuestion helpCenterQuestion) throws ManagerException {
		try {
			int result = helpCenterMapper.insertSingle(helpCenterQuestion);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public HelpCenterQuestion selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return helpCenterMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKey(HelpCenterQuestion helpCenterQuestion) throws ManagerException {
		try {

			return helpCenterMapper.updateByPrimaryKey(helpCenterQuestion);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	/**
	 * 分页查询
	 */
	public Page<HelpCenterQuestion> findByPage(Page<HelpCenterQuestion> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try {
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = helpCenterMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<HelpCenterQuestion> selectForPagin = helpCenterMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据主键删除（逻辑）
	 */
	@Override
	public Integer deleteByHelpCenterQuestionId(Long id) throws ManagerException {
		try {
			return helpCenterMapper.deleteByHelpCenterQuestionId(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer updateByPrimaryKeySelective(
			HelpCenterQuestion helpCenterQuestion) throws ManagerException {
		try {

			return helpCenterMapper.updateByPrimaryKeySelective(helpCenterQuestion);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean updateSortById(Long id, Integer sort, Date date) {
		if (helpCenterMapper.updateSortById(id,sort,date)>0){
            return true;
        }
        return false;
	}

	@Override
	public void flushQuestion(Integer terminal) throws ManagerException{
		HashMap<String,LinkedHashMap<String,List<HelpCenterQuestionShow>>> quesCategoryMap = new HashMap<String,LinkedHashMap<String,List<HelpCenterQuestionShow>>>();
		ArrayList<String> listCN = new ArrayList<String>();
		ArrayList<String> listEN = new ArrayList<String>();
		listCN.add(0,"账户类");
		listCN.add(1,"资金类");
		listCN.add(2,"投资类");
		listCN.add(3,"功能类");
		listCN.add(4,"活动类");
		listEN.add(0,"Account");
		listEN.add(1,"Fund");
		listEN.add(2,"Invest");
		listEN.add(3,"Function");
		listEN.add(4,"Activity");
		String terminalName = "";
		String labelName = "";
		if(terminal == 1){
			terminalName = "client";
		}else{
			terminalName = "PC";
		}
		
		
		try{
			//热门问题刷新到缓存
			List<HelpCenterQuestionShow> hotQuestion = helpCenterMapper.selectHotShow(terminal);
			RedisManager.putObject(terminalName + "HotQuestion", hotQuestion);
			
			//新手引导问题刷新到缓存
			List<HelpCenterQuestionShow> newComerQuestion = helpCenterMapper.selectNewComerShow(terminal);
			RedisManager.putObject(terminalName + "NewComerQuestion", newComerQuestion);
			
			//常见问题刷新到缓存
			for (int i = 0;i < listCN.size();i++) {
				LinkedHashMap<String,List<HelpCenterQuestionShow>> quesLabelMap = new LinkedHashMap<String,List<HelpCenterQuestionShow>>();
				List<HelpCenterLabel> accountLabelList = helpCenterLabelMapper.selectByCategory(listCN.get(i));
				for (HelpCenterLabel accountLabel : accountLabelList) {
					labelName = accountLabel.getLabelName();
					List<HelpCenterQuestionShow> question = helpCenterMapper.selectCommonShowByLabel(terminal,labelName);
					quesLabelMap.put(labelName, question);
				}
				quesCategoryMap.put(terminalName + listEN.get(i) + "Question", quesLabelMap);
			}
			
			RedisManager.putObject(terminalName + "CommonQuestion", quesCategoryMap);
			
			//问题答案刷新到缓存
			List<HelpCenterQuestionShow> QuesAnswer = helpCenterMapper.selectAnswerShow(terminal);
			RedisManager.putObject(terminalName + "QuesAnswer", QuesAnswer);
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
}