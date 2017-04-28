package com.yourong.core.mc.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.dao.CouponTemplatePrintMapper;
import com.yourong.core.mc.manager.CouponTemplatePrintManager;
import com.yourong.core.mc.model.CouponTemplatePrint;

@Component
public class CouponTemplatePrintManagerImpl implements
		CouponTemplatePrintManager {
	@Autowired
	private CouponTemplatePrintMapper couponTemplatePrintMapper;

	public Integer deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = couponTemplatePrintMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer insert(CouponTemplatePrint activity) throws ManagerException {
		try {
			int result = couponTemplatePrintMapper.insert(activity);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public CouponTemplatePrint selectByPrimaryKey(Long id)
			throws ManagerException {
		try {
			return couponTemplatePrintMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKey(CouponTemplatePrint activity)
			throws ManagerException {
		try {

			return couponTemplatePrintMapper.updateByPrimaryKey(activity);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKeySelective(CouponTemplatePrint activity)
			throws ManagerException {
		try {

			return couponTemplatePrintMapper
					.updateByPrimaryKeySelective(activity);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Page<CouponTemplatePrint> findByPage(
			Page<CouponTemplatePrint> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try {
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = couponTemplatePrintMapper
					.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<CouponTemplatePrint> selectForPagin = couponTemplatePrintMapper
					.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer insertSelective(CouponTemplatePrint record)
			throws ManagerException {
		try {
			int result = couponTemplatePrintMapper.insertSelective(record);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public CouponTemplatePrint selectByTemplateId(Long couponTemplateId)
			throws ManagerException {
		try {
			return couponTemplatePrintMapper.selectByTemplateId(couponTemplateId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public CouponTemplatePrint selectInforByTemplateId(Long couponTemplateId)
			throws ManagerException {
		try {
			return couponTemplatePrintMapper.selectInforByTemplateId(couponTemplateId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

}