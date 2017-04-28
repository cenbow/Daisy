package com.yourong.core.fin.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.fin.model.biz.InterestSettlementBiz;

public interface InterestSettlementMapper {
	
	List<InterestSettlementBiz> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
}
