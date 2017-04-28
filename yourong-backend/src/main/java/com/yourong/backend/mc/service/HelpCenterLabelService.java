package com.yourong.backend.mc.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.HelpCenterLabel;

public interface HelpCenterLabelService {
	/**
	 * 优惠券模板分页列表
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<HelpCenterLabel> findByPage(Page<HelpCenterLabel> pageRequest,
			Map<String, Object> map);

	/**
	 * 插入优惠券模板
	 * @param couponTemplate
	 * @return
	 */
	public ResultDO<HelpCenterLabel> insertHelpCenterLabel(
			HelpCenterLabel helpCenterLabel);

	/**
	 * 删除优惠券模板
	 * @param id
	 * @return
	 */
	public ResultDO<HelpCenterLabel> deleteByHelpCenterLabelId(Long id);

	/**
	 * 根据id查询优惠券模板信息
	 * @param id
	 * @return
	 */
	public HelpCenterLabel selectByPrimaryKey(Long id);
	
	/**
	 * 更新记录
	 * @param record
	 * @return
	 */
	public ResultDO<HelpCenterLabel> update(HelpCenterLabel record);

	/**
	 * 更新排序
	 * @param sorts
	 */
	public void updateSortById(List<HelpCenterLabel> sorts);

	public ResultDO<HelpCenterLabel> deleteByPrimaryKey(Long id);
	
	/**
	 * 根据标签类别查询具体标签
	 * @param category
	 * @return
	 */
	public List<HelpCenterLabel> selectByCategory(String category);
	

	/**
	 * 更新排序时间
	 * @return
     */
	/*public int updateSort(Date sorttime, Long id);*/
}
