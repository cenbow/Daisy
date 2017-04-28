package com.yourong.core.mc.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.mc.dao.PdGeneralMonthMapper;
import com.yourong.core.mc.dao.PdRegionMonthMapper;
import com.yourong.core.mc.manager.ChannelDataManager;
import com.yourong.core.mc.model.PdGeneralMonth;
import com.yourong.core.mc.model.PdRegionMonth;

@Component
public class ChannelDataManagerImpl implements ChannelDataManager {

	private static final Logger logger = LoggerFactory.getLogger(ChannelDataManagerImpl.class);

	
	@Autowired
	private PdGeneralMonthMapper pdGeneralMonthMapper;
	
	@Autowired
	private PdRegionMonthMapper pdRegionMonthMapper;
	
	@Override
	public List<PdGeneralMonth> selectTotalInvestAmountMonth() throws ManagerException {
		try {
			return pdGeneralMonthMapper.selectTotalInvestAmountMonth();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public PdGeneralMonth selectPdGeneralNewMonth() throws ManagerException {
		try {
			return pdGeneralMonthMapper.selectPdGeneralNewMonth();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public List<PdRegionMonth> selectPdRegionMonth() throws ManagerException {
		List<PdRegionMonth> listPd=Lists.newArrayList();
		try {
			listPd= pdRegionMonthMapper.selectPdRegionMonth(DateUtils.getStrFromDate(DateUtils.addMonth(DateUtils.getCurrentDate(), -1),DateUtils.DATE_FMT_14));
			if(Collections3.isEmpty(listPd)){
				listPd= pdRegionMonthMapper.selectPdRegionMonth(DateUtils.getStrFromDate(DateUtils.addMonth(DateUtils.getCurrentDate(), -2),DateUtils.DATE_FMT_14));
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return listPd;
	}

	

}