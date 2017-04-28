package com.yourong.core.mc.manager.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.dao.CouponTemplateMapper;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.model.CouponTemplate;

@Component
public class CouponTemplateManagerImpl implements CouponTemplateManager {
	@Autowired
	private CouponTemplateMapper couponTemplateMapper;

	public Integer deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = couponTemplateMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer insert(CouponTemplate couponTemplate) throws ManagerException {
		try {
			int result = couponTemplateMapper.insert(couponTemplate);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public CouponTemplate selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return couponTemplateMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKey(CouponTemplate couponTemplate) throws ManagerException {
		try {

			return couponTemplateMapper.updateByPrimaryKey(couponTemplate);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 部分选择更新（原因：优惠券模板有些字段当值为null时也需要更新（startDate、endData、days））
	 */
	public Integer updateByPrimaryKeyPartSelective(CouponTemplate couponTemplate) throws ManagerException {
		try {

			return couponTemplateMapper.updateByPrimaryKeyPartSelective(couponTemplate);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Page<CouponTemplate> findByPage(Page<CouponTemplate> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try {
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = couponTemplateMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<CouponTemplate> selectForPagin = couponTemplateMapper.selectForPagin(map);
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
	public Integer deleteByCouponTemplateId(Long id) throws ManagerException {
		try {
			return couponTemplateMapper.deleteByCouponTemplateId(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 选择性的更新（当更新的值为空时不做更新操作）
	 */
	@Override
	public Integer updateByPrimaryKeySelective(CouponTemplate couponTemplate) throws ManagerException {
		try {

			return couponTemplateMapper.updateByPrimaryKeySelective(couponTemplate);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<CouponTemplate> findExchangeCouponsByIds(Long[] ids) throws ManagerException {
		try {
			return couponTemplateMapper.findExchangeCouponsByIds(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer updateSort(Date sorttime, Long id) {
		return couponTemplateMapper.updateSort(sorttime,id);
	}
}