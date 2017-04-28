package com.yourong.backend.mc.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.mc.service.HelpCenterService;
import com.yourong.backend.sh.dto.GoodsDto;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.mc.manager.HelpCenterManager;
import com.yourong.core.mc.model.HelpCenterQuestion;

@Service
public class HelpCenterServiceImpl implements HelpCenterService {

	private static Logger logger = LoggerFactory.getLogger(HelpCenterServiceImpl.class);
	
	@Autowired
	private HelpCenterManager helpCenterManager;	
	
	/**
	 * 问题分页列表
	 */
	@Override
	public Page<HelpCenterQuestion> findByPage(Page<HelpCenterQuestion> pageRequest, Map<String, Object> map) {
		try {
			return helpCenterManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("问题分页列表获取失败", e);
		}
		return null;
	}

	/**
	 * 插入问题
	 */
	@Override
	public ResultDO<HelpCenterQuestion> insertHelpCenterQuestion(HelpCenterQuestion helpCenterQuestion) {
		ResultDO<HelpCenterQuestion> resultDO = new ResultDO<HelpCenterQuestion>();
		try {
			int insertResult = helpCenterManager.insert(helpCenterQuestion);
			if (insertResult <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("新增问题失败,couponTemplate=" + helpCenterQuestion, e);
			resultDO.setResultCode(ResultCode.COUPONTEMPLATE_ADD_SAVE_FAIL_ERROR);
		}
		return resultDO;
	}

	/**
	 * 删除问题
	 */
	@Override
	public ResultDO<HelpCenterQuestion> deleteByHelpCenterQuestionId(Long id) {
		ResultDO<HelpCenterQuestion> resultDO = new ResultDO<HelpCenterQuestion>();
		try {
			int result = helpCenterManager.deleteByPrimaryKey(id);
			if (result <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("删除问题失败", e);
		}
		return resultDO;
	}

	/**
	 * 根据id获取问题
	 */
	@Override
	public HelpCenterQuestion selectByPrimaryKey(Long id) {
		try {
			return helpCenterManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("获取问题失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public ResultDO<HelpCenterQuestion> update(HelpCenterQuestion record) {
		ResultDO<HelpCenterQuestion> resultDO = new ResultDO<HelpCenterQuestion>();
		try {
			int result = helpCenterManager.updateByPrimaryKeySelective(record);
			if (result <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("更新问题列表失败,couponTemplate=" + record, e);
		}
		return resultDO;
	}

    @Override
    public void updateSortById(List<GoodsDto> list) {
        if (Collections3.isNotEmpty(list)){
            for (GoodsDto dto:list) {
            	helpCenterManager.updateSortById(dto.getId(),dto.getSort(),new Date());
            }
        }
    }

	@Override
	public void flushQuestion(Integer terminal) {
		try {
		helpCenterManager.flushQuestion(terminal);
		} catch (Exception e) {
			logger.error("刷新问题失败", e);
		}
	}
}
