package com.yourong.backend.mc.service;

import java.util.Date;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.CouponTemplatePrint;

public interface CouponTemplateService {
	/**
	 * 优惠券模板分页列表
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<CouponTemplate> findByPage(Page<CouponTemplate> pageRequest,
			Map<String, Object> map);

	/**
	 * 插入优惠券模板
	 * @param couponTemplate
	 * @return
	 */
	public ResultDO<CouponTemplate> insertCouponTemplate(
			CouponTemplate couponTemplate);

	/**
	 * 删除优惠券模板
	 * @param id
	 * @return
	 */
	public ResultDO<CouponTemplate> deleteByCouponTemplateId(Long id);

	/**
	 * 根据id查询优惠券模板信息
	 * @param id
	 * @return
	 */
	public CouponTemplate selectByPrimaryKey(Long id);
	
	/**
	 * 更新
	 * @param record
	 * @return
	 */
	public ResultDO<CouponTemplate> update(CouponTemplate record);
	
	
	public ResultDO<CouponTemplate> printCouponTemplate(CouponTemplatePrint couponTemplatePrint) throws ManagerException;

	/**
	 * 更新排序时间
	 * @return
     */
	public int updateSort(Date sorttime, Long id);
}
