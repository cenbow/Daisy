package com.yourong.backend.sys.service.impl;

import com.yourong.backend.sys.service.SysLogService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.dao.SysLogMapper;
import com.yourong.core.sys.model.SysLog;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysLogServiceImpl implements SysLogService {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return 0;
    }

    public int insert(SysLog sysLog) {
         int result = sysLogMapper.insert( sysLog );
        return result;
    }

    public SysLog selectByPrimaryKey(Long id) {
         return sysLogMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKey(SysLog sysLog) {
         return sysLogMapper.updateByPrimaryKey(sysLog );
    }

    public int updateByPrimaryKeySelective(SysLog sysLog) {
         return sysLogMapper.updateByPrimaryKeySelective(sysLog );
    }

    public int batchDelete(int[] ids) {
         return sysLogMapper.batchDelete(ids);
    }

    public Page findByPage(Page pageRequest, Map map) {
        return sysLogMapper.findByPage(pageRequest,map); 
    }
}