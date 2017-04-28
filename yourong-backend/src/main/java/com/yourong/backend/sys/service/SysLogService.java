package com.yourong.backend.sys.service;

import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysLog;

import java.util.Map;

public interface SysLogService {
    int deleteByPrimaryKey(Long id);

    int insert(SysLog record);

    SysLog selectByPrimaryKey(Long id);

    int updateByPrimaryKey(SysLog record);

    int updateByPrimaryKeySelective(SysLog record);

    Page findByPage(Page pageRequest, Map map);
}