package com.yourong.api.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.manager.SysAreaManager;
import com.yourong.core.sys.model.SysArea;
import com.yourong.api.service.SysAreaService;

@Service
public class SysAreaServiceImpl implements SysAreaService {
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

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
			return sysAreaManager.insert( sysArea );
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
    		return sysAreaManager.updateByPrimaryKey(sysArea );
		} catch (ManagerException e) {
			logger.error("更新区域信息失败,sysArea=" + sysArea, e);
		}
        return null;
    }

    public Integer updateByPrimaryKeySelective(SysArea sysArea) {
    	try {
    		return sysAreaManager.updateByPrimaryKeySelective(sysArea );
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

    public Page<SysArea> findByPage(Page<SysArea> pageRequest, Map<String, Object> map) {
    	try {
    		return sysAreaManager.findByPage(pageRequest,map); 
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
	public List<SysArea> getSysAreasByParentId(Long parentId) {
		try {
			return sysAreaManager.getSysAreasByParentId(parentId);
		} catch (ManagerException e) {
			logger.error("根据父类ID，获得所有子区域异常parentID:"+parentId, e);
		}
        return null;
	}

	@Override
	public List<Long> getParentIdsByCode(Long code) {
		try {
			return sysAreaManager.getParentIdsByCode(code);
		} catch (ManagerException e) {
			logger.error("根据Code获取级联父类异常code:"+code, e);
		}
        return null;
	}

}