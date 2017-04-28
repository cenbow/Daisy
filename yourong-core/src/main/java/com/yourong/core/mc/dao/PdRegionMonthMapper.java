package com.yourong.core.mc.dao;

import java.util.List;

import com.yourong.core.mc.model.PdRegionMonth;


public interface PdRegionMonthMapper {

	List<PdRegionMonth> selectPdRegionMonth(String month);
	
	

}