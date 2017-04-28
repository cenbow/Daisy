package com.yourong.core.mc.dao;

import java.util.List;

import com.yourong.core.mc.model.PdGeneralMonth;


public interface PdGeneralMonthMapper {

	List<PdGeneralMonth> selectTotalInvestAmountMonth();

	PdGeneralMonth selectPdGeneralNewMonth();
	
	

}