package com.yourong.core.sys.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.sys.model.SysOperateInfo;

@Repository
public interface SysOperateInfoMapper {
	
    int insert(SysOperateInfo record);
    
    int insertSelective(SysOperateInfo record);
    
    int updateByPrimaryKeySelective(SysOperateInfo record);
    
    SysOperateInfo selectOperateBySourceId(@Param("map") Map<String, Object> map);
    
}