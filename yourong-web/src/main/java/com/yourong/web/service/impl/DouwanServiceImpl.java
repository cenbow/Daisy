package com.yourong.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.core.mc.manager.DouwanManager;
import com.yourong.web.service.DouwanService;

/**
 * 都玩主要回调接口 Created by Administrator on 2015/2/12.
 */
@Component
public class DouwanServiceImpl implements DouwanService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private DouwanManager douwanManager;

	// 回调接口
	public void douwanRegisteredCallBack(final Long id) {
		douwanManager.douwanRegisteredCallBack(id);
	}

	public void douwanEmailBingCallBack(final Long id) {
		douwanManager.douwanEmailBingCallBack(id);
	}

	public void douwanFirstTransaction(final Long id,final int totalDays) {
		douwanManager.douwanFirstTransaction(id,totalDays);
	}

}
