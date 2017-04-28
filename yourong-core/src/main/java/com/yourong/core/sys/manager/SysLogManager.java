package com.yourong.core.sys.manager;

import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/1/14.
 */
public interface SysLogManager {

//    int deleteByPrimaryKey(Long id);
//
//    int insert(SysLog record);

    int insertSelective(SysLog record);

//    SysLog selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(SysLog record);
//
//    int updateByPrimaryKey(SysLog record);
//
//    Page findByPage(Page pageRequest, @Param("map") Map map);
//
//    int batchDelete(@Param("ids") int[] ids);
//
//    List selectForPagin(@Param("map") Map map);
//
//    int selectForPaginTotalCount(@Param("map") Map map);
}
