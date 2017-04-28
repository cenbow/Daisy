package com.yourong.backend.mc.service;

import java.util.List;
import java.util.Map;

import com.yourong.backend.sh.dto.GoodsDto;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.HelpCenterQuestion;

public interface HelpCenterService {
	/**
	 * 问题分页列表
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<HelpCenterQuestion> findByPage(Page<HelpCenterQuestion> pageRequest,
			Map<String, Object> map);

	/**
	 * 插入问题
	 * @param couponTemplate
	 * @return
	 */
	public ResultDO<HelpCenterQuestion> insertHelpCenterQuestion(
			HelpCenterQuestion helpCenterQuestion);

	/**
	 * 删除问题
	 * @param id
	 * @return
	 */
	public ResultDO<HelpCenterQuestion> deleteByHelpCenterQuestionId(Long id);

	/**
	 * 根据id查询问题
	 * @param id
	 * @return
	 */
	public HelpCenterQuestion selectByPrimaryKey(Long id);
	
	/**
	 * 更新记录
	 * @param record
	 * @return
	 */
	public ResultDO<HelpCenterQuestion> update(HelpCenterQuestion record);

	/**
	 * 更新排序
	 * @param sorts
	 */
	public void updateSortById(List<GoodsDto> sorts);

	public void flushQuestion(Integer terminal);
	

}
