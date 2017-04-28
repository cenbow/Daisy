package com.yourong.core.sh.dao;

import com.yourong.core.sh.model.Area;
import com.yourong.core.sh.model.Goods;
import com.yourong.core.sh.model.query.GoodsQuery;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 
 */
public interface GoodsAddrMapper {
	
	public List<Area> queryAreasByParentCode(@Param("id")Long id);
}
