package com.yourong.backend.sys.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.sys.service.SysAreaService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.manager.SysAreaManager;
import com.yourong.core.sys.model.SysArea;

@Service
public class SysAreaServiceImpl implements SysAreaService {

	private static Logger logger = LoggerFactory
			.getLogger(SysAreaServiceImpl.class);

	@Autowired
	private SysAreaManager sysAreaManager;

	public Integer deleteByPrimaryKey(Long id) {
		try {
			return sysAreaManager.deleteByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("删除区域信息失败,id=" + id, e);
		}
		return null;
	}

	public Integer insert(SysArea sysArea) {
		try {
			return sysAreaManager.insert(sysArea);
		} catch (ManagerException e) {
			logger.error("插入区域信息失败,sysArea=" + sysArea, e);
		}
		return null;
	}

	public SysArea selectByPrimaryKey(Long id) {
		try {
			return sysAreaManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("查询区域信息失败,id=" + id, e);
		}
		return null;
	}

	public Integer updateByPrimaryKey(SysArea sysArea) {
		try {
			return sysAreaManager.updateByPrimaryKey(sysArea);
		} catch (ManagerException e) {
			logger.error("更新区域信息失败,sysArea=" + sysArea, e);
		}
		return null;
	}

	public Integer updateByPrimaryKeySelective(SysArea sysArea) {
		try {
			return sysAreaManager.updateByPrimaryKeySelective(sysArea);
		} catch (ManagerException e) {
			logger.error("更新区域信息失败,sysArea=" + sysArea, e);
		}
		return null;
	}

	public Integer batchDelete(long[] ids) {
		try {
			return sysAreaManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("批量删除数据字典失败,ids=" + ids, e);
		}
		return null;
	}

	public Page<SysArea> findByPage(Page<SysArea> pageRequest,
			Map<String, Object> map) {
		try {
			return sysAreaManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询区域信息失败", e);
		}
		return null;
	}

	@Override
	public List<SysArea> getAllSysArea() {
		try {
			return sysAreaManager.getAllSysArea();
		} catch (ManagerException e) {
			logger.error("获取区域信息失败", e);
		}
		return null;
	}

	@Override
	public List<SysArea> getSysAreasByType(Integer type) {
		try {
			return sysAreaManager.getSysAreasByType(type);
		} catch (ManagerException e) {
			logger.error("根据区域类型获取区域信息失败,type=" + type, e);
		}
		return null;
	}

	@Override
	public List<SysArea> getSysAreasByParentId(Long parentId) {
		try {
			return sysAreaManager.getSysAreasByParentId(parentId);
		} catch (ManagerException e) {
			logger.error("根据区域编码获取区域信息失败,parentId=" + parentId, e);
		}
		return null;
	}

	@Override
	public List getParentIdsByCode(Long code) {
		try {
			return sysAreaManager.getParentIdsByCode(code);
		} catch (ManagerException e) {
			logger.error("根据区域编码获取区域信息失败,code=" + code, e);
		}
		return null;
	}

}