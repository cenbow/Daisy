package com.yourong.backend.mc.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.CouponTemplatePrint;

public interface CouponTemplatePrintService {
	public Integer insert(CouponTemplatePrint record);

	public Page<CouponTemplatePrint> findByPage(Page<CouponTemplatePrint> pageRequest,
			Map<String, Object> map);

}
