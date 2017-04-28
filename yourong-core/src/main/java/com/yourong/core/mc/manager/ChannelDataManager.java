package com.yourong.core.mc.manager;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.mc.model.PdGeneralMonth;
import com.yourong.core.mc.model.PdRegionMonth;


public interface ChannelDataManager {
	

	public List<PdGeneralMonth> selectTotalInvestAmountMonth() throws ManagerException;

	public PdGeneralMonth selectPdGeneralNewMonth()throws ManagerException;

	public List<PdRegionMonth> selectPdRegionMonth()throws ManagerException;
	
}