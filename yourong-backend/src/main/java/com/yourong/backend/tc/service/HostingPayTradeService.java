package com.yourong.backend.tc.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.HostingPayTrade;

public interface HostingPayTradeService {

	/**
	 * 代付分页查询
	 * @param pageReques
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Page<HostingPayTrade> findByPage(Page<HostingPayTrade> pageReques, Map<String, Object> map);
	
}
