package com.yourong.backend.mc.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.mc.service.HelpCenterLabelService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.mc.manager.HelpCenterLabelManager;
import com.yourong.core.mc.model.HelpCenterLabel;

@Service
public class HelpCenterLabelServiceImpl implements HelpCenterLabelService {

	private static Logger logger = LoggerFactory.getLogger(HelpCenterLabelServiceImpl.class);
	
	@Autowired
	private HelpCenterLabelManager helpCenterLabelManager;	
	
	/**
	 * 标签分页列表
	 */
	@Override
	public Page<HelpCenterLabel> findByPage(Page<HelpCenterLabel> pageRequest, Map<String, Object> map) {
		try {
			return helpCenterLabelManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("标签分页列表获取失败", e);
		}
		return null;
	}

	/**
	 * 插入问题
	 */
	@Override
	public ResultDO<HelpCenterLabel> insertHelpCenterLabel(HelpCenterLabel helpCenterLabel) {
		ResultDO<HelpCenterLabel> resultDO = new ResultDO<HelpCenterLabel>();
		try {
			int insertResult = helpCenterLabelManager.insert(helpCenterLabel);
			if (insertResult <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("新增标签失败,helpCenterLabel=" + helpCenterLabel, e);
			resultDO.setResultCode(ResultCode.COUPONTEMPLATE_ADD_SAVE_FAIL_ERROR);
		}
		return resultDO;
	}

	/**
	 * 删除
	 */
	@Override
	public ResultDO<HelpCenterLabel> deleteByHelpCenterLabelId(Long id) {
		ResultDO<HelpCenterLabel> resultDO = new ResultDO<HelpCenterLabel>();
		try {
			int result = helpCenterLabelManager.deleteByHelpCenterLabelId(id);
			if (result <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("删除标签失败", e);
		}
		return resultDO;
	}

	/**
	 * 根据id获取标签
	 */
	@Override
	public HelpCenterLabel selectByPrimaryKey(Long id) {
		try {
			return helpCenterLabelManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("获取标签失败,id=" + id, e);
		}
		return null;
	}



/*	@Override
	public int updateSort(Date sorttime,Long id) {
		return helpCenterManager.updateSort(sorttime,id);
	}
*/

	@Override
	public ResultDO<HelpCenterLabel> update(HelpCenterLabel record) {
		ResultDO<HelpCenterLabel> resultDO = new ResultDO<HelpCenterLabel>();
		try {
			int result = helpCenterLabelManager.updateByPrimaryKeySelective(record);
			if (result <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("更新标签失败,helpCenterLabel=" + record, e);
		}
		return resultDO;
	}

    @Override
    public void updateSortById(List<HelpCenterLabel> list) {
        if (Collections3.isNotEmpty(list)){
            for (HelpCenterLabel dto:list) {
            	helpCenterLabelManager.updateSortById(dto.getId(),dto.getSort(),new Date());
            }
        }
    }
	
	/**
	 * 删除
	 */
	@Override
	public ResultDO<HelpCenterLabel> deleteByPrimaryKey(Long id) {
		ResultDO<HelpCenterLabel> resultDO = new ResultDO<HelpCenterLabel>();
		try {
			int result = helpCenterLabelManager.deleteByPrimaryKey(id);
			if (result <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("删除标签失败", e);
		}
		return resultDO;
	}

	@Override
	public List<HelpCenterLabel> selectByCategory(String category) {
		return helpCenterLabelManager.selectByCategory(category);
	}
}
