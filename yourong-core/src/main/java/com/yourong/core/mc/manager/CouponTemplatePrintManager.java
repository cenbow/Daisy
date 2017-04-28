package com.yourong.core.mc.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.CouponTemplatePrint;

public interface CouponTemplatePrintManager {
	Integer deleteByPrimaryKey(Long id) throws ManagerException;

	Integer insert(CouponTemplatePrint couponTemplatePrint) throws ManagerException;

    Integer insertSelective(CouponTemplatePrint record) throws ManagerException;

    CouponTemplatePrint selectByPrimaryKey(Long id) throws ManagerException;

    Integer updateByPrimaryKey(CouponTemplatePrint couponTemplatePrint) throws ManagerException;

    Integer updateByPrimaryKeySelective(CouponTemplatePrint couponTemplatePrint) throws ManagerException;

    Page<CouponTemplatePrint> findByPage(Page<CouponTemplatePrint> pageRequest, Map<String, Object> map) throws ManagerException;

    CouponTemplatePrint selectByTemplateId(Long couponTemplateId) throws ManagerException;
    
    CouponTemplatePrint selectInforByTemplateId(Long couponTemplateId) throws ManagerException;
}