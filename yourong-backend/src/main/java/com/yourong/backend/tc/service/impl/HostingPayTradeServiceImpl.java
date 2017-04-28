package com.yourong.backend.tc.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.tc.service.HostingPayTradeService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.model.HostingPayTrade;

@Service
public class HostingPayTradeServiceImpl implements HostingPayTradeService {

	private static Logger logger = LoggerFactory.getLogger(HostingPayTradeServiceImpl.class);
	
	@Autowired 
	private HostingPayTradeManager hostingPayTradeManager;

	@Override
	public Page<HostingPayTrade> findByPage(Page<HostingPayTrade> pageReques, Map<String, Object> map) {
		try{
			return hostingPayTradeManager.findByPage(pageReques, map);
		}catch(ManagerException e) {
			logger.error("代付查询失败", e);
		}
		return null;
	}
}
