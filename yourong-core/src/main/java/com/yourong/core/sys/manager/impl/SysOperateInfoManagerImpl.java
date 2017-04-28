package com.yourong.core.sys.manager.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.sys.dao.SysOperateInfoMapper;
import com.yourong.core.sys.manager.SysOperateInfoManager;
import com.yourong.core.sys.model.SysOperateInfo;

@Component
public class SysOperateInfoManagerImpl implements SysOperateInfoManager {
    @Autowired
    private SysOperateInfoMapper sysOperateInfoMapper;

	@Override
	public int insertSelective(SysOperateInfo sysOperateInfo) throws ManagerException {
		try {
			return sysOperateInfoMapper.insertSelective(sysOperateInfo);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	@Override
	public int updateByPrimaryKeySelective(SysOperateInfo record) throws ManagerException {
		try {
			return sysOperateInfoMapper.updateByPrimaryKeySelective(record);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	@Override
	public int insert(SysOperateInfo record) throws ManagerException {
		try {
			record.setOperateTime(new Date());
			record.setDelFlag(1);
			return sysOperateInfoMapper.insert(record);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public SysOperateInfo selectOperateBySourceId(Map<String, Object> map) throws ManagerException {
		try {
			return sysOperateInfoMapper.selectOperateBySourceId(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int saveOperateInfo(Long sourceId, int operateTableType,Long operateId,String operateMsg,String operateCode ,String remarks){
		SysOperateInfo info=new SysOperateInfo();
		info.setSourceId(sourceId);
		info.setOperateId(operateId);
		info.setOperateTableType(operateTableType);
		info.setOperateMsg(operateMsg);
		info.setOperateCode(operateCode);
		info.setOperateTime(new Date());
		info.setCreateTime(new Date());
		info.setUpdateTime(new Date());
		info.setRemarks(remarks);
		info.setDelFlag(1);
		return sysOperateInfoMapper.insertSelective(info);
	}
}
