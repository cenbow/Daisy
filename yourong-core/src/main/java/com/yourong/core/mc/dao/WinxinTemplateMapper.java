package com.yourong.core.mc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.WinxinTemplate;
@Repository
public interface WinxinTemplateMapper {

	int insert(WinxinTemplate record);

	int deleteByPrimaryKey(Long id);

	Page<WinxinTemplate> findByPage(Page<WinxinTemplate> pageRequest, @Param("map") Map<String, Object> map);
	
	int deleteBymenuId(@Param("id") Long id);

	int updateByPrimaryKeySelective(WinxinTemplate record);

	WinxinTemplate selectByPrimaryKey(Long id);

	int deleteByPrimaryKey(WinxinTemplate record);

	/**
	 * @return
	 */
	List<WinxinTemplate> getAllWeixinMenu();

	/**
	 * @return
	 */
	List<WinxinTemplate> getWeixininfo();

	/**
	 * @param menuId 
	 * @return
	 */
	List<WinxinTemplate> getChildMenu(@Param("menuId") Long menuId);

	/**
	 * @param ids
	 * @return
	 */
	int batchDelete(@Param("ids") long[] ids);

	/**
	 * @param record
	 * @return
	 */
	int insertSelective(WinxinTemplate record);

	/**
	 * @return
	 */
	List<WinxinTemplate> queryWeixinAtten();
	
	int updateWeixin();
	
}