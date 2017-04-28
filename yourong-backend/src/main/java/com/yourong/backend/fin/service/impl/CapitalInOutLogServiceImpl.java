package com.yourong.backend.fin.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.fin.service.CapitalInOutLogService;
import com.yourong.backend.sys.service.impl.SysDictServiceImpl;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.CapitalInOutLog;

@Service
public class CapitalInOutLogServiceImpl implements CapitalInOutLogService {

	private static Logger logger = LoggerFactory.getLogger(SysDictServiceImpl.class);
	
	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	
	@Override
	public Page<CapitalInOutLog> findByPage(Page<CapitalInOutLog> pageRequest,
			Map<String, Object> map) {
		try {
			return capitalInOutLogManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询资金流水记录失败", e);
		}
		return null;
	}
	
	/**
	 * 出借人资金管理列表
	 * @param pageRequest
	 * @param map
	 * @return
	 */

	@Override
	public Page<CapitalInOutLog> findLenderCapitalInOutLogPage(Page<CapitalInOutLog> pageRequest,
			Map<String, Object> map) {
		try {
			return capitalInOutLogManager.findLenderCapitalInOutLogPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询出借人资金流水记录失败", e);
		}
		return null;
	}
}
