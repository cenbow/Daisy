package com.yourong.core.mc.manager.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.dao.HelpCenterLabelMapper;
import com.yourong.core.mc.manager.HelpCenterLabelManager;
import com.yourong.core.mc.model.HelpCenterLabel;

@Component
public class HelpCenterLabelManagerImpl implements HelpCenterLabelManager {
	@Autowired
	private HelpCenterLabelMapper helpCenterLabelMapper;

	public Integer deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = helpCenterLabelMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer insert(HelpCenterLabel helpCenterLabel) throws ManagerException {
		try {
			int result = helpCenterLabelMapper.insertSingle(helpCenterLabel);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public HelpCenterLabel selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return helpCenterLabelMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKey(HelpCenterLabel helpCenterLabel) throws ManagerException {
		try {

			return helpCenterLabelMapper.updateByPrimaryKey(helpCenterLabel);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	/**
	 * 分页查询
	 */
	public Page<HelpCenterLabel> findByPage(Page<HelpCenterLabel> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try {
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = helpCenterLabelMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<HelpCenterLabel> selectForPagin = helpCenterLabelMapper.selectForPagin(map);
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
	public Integer deleteByHelpCenterLabelId(Long id) throws ManagerException {
		try {
			return helpCenterLabelMapper.deleteByHelpCenterLabelId(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer updateByPrimaryKeySelective(
			HelpCenterLabel helpCenterLabel) throws ManagerException {
		try {

			return helpCenterLabelMapper.updateByPrimaryKeySelective(helpCenterLabel);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean updateSortById(Long id, Integer sort, Date date) {
		if (helpCenterLabelMapper.updateSortById(id,sort,date)>0){
            return true;
        }
        return false;
	}

	@Override
	public List<HelpCenterLabel> selectByCategory(String category) {
		return helpCenterLabelMapper.selectByCategory(category);
	}

}