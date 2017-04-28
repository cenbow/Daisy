package com.yourong.core.sys.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysLog;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface SysLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

    Page findByPage(Page pageRequest, @Param("map") Map map);

    int batchDelete(@Param("ids") int[] ids);

    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);
}