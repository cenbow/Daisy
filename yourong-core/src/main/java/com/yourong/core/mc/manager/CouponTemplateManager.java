package com.yourong.core.mc.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.CouponTemplate;

public interface CouponTemplateManager {
	Integer deleteByPrimaryKey(Long id) throws ManagerException;

	Integer insert(CouponTemplate couponTemplate) throws ManagerException;

    CouponTemplate selectByPrimaryKey(Long id) throws ManagerException;

    Integer updateByPrimaryKey(CouponTemplate couponTemplate) throws ManagerException;

    Integer updateByPrimaryKeyPartSelective(CouponTemplate couponTemplate) throws ManagerException;
    
    Integer updateByPrimaryKeySelective(CouponTemplate couponTemplate) throws ManagerException;

    Page<CouponTemplate> findByPage(Page<CouponTemplate> pageRequest, Map<String, Object> map) throws ManagerException;
    
    Integer deleteByCouponTemplateId(Long id) throws ManagerException;
    
    List<CouponTemplate> findExchangeCouponsByIds(Long[] ids) throws ManagerException;

    Integer updateSort(Date sorttime,Long id);
}