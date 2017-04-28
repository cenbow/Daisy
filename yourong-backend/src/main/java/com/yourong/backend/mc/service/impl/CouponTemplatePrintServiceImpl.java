package com.yourong.backend.mc.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.mc.service.CouponTemplatePrintService;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.manager.CouponTemplatePrintManager;
import com.yourong.core.mc.model.CouponTemplatePrint;

@Service
public class CouponTemplatePrintServiceImpl implements
		CouponTemplatePrintService {
	private static Logger logger = LoggerFactory
			.getLogger(CouponTemplatePrintServiceImpl.class);

	@Autowired
	private CouponTemplatePrintManager couponTemplatePrintManager;

	@Override
	public Integer insert(CouponTemplatePrint record) {
		try {
			return couponTemplatePrintManager.insert(record);
		} catch (Exception e) {
			logger.error("插入打印记录失败，couponTemplatePrint=" + record, e);
		}
		return null;
	}

	@Override
	public Page<CouponTemplatePrint> findByPage(
			Page<CouponTemplatePrint> pageRequest, Map<String, Object> map) {
		try {
			return couponTemplatePrintManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("获取优惠券印刷列表失败", e);
		}
		return null;
	}
}
