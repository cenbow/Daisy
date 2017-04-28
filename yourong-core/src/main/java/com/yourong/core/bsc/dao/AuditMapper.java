package com.yourong.core.bsc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.Audit;

public interface AuditMapper {
	int deleteByPrimaryKey(Long id);

    int insert(Audit record);

    int insertSelective(Audit record);

    Audit selectByPrimaryKey(Long id);
    
    Audit selectByMap(@Param("map")Map<String, Object> map);

    int updateByPrimaryKeySelective(Audit record);

    int updateByPrimaryKey(Audit record);

    Page findByPage(Page pageRequest, @Param("map") Map map);

    int batchDelete(@Param("ids") int[] ids);
    
    List<Audit> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
}