package com.yourong.api.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.api.service.SysDictService;

@Service
public class SysDictServiceImpl  implements SysDictService {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SysDictManager sysDictManager;

    public Integer deleteByPrimaryKey(Long id) {
		try {
			return sysDictManager.deleteByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("删除数据字典失败,id=" + id, e);
		}
        return null;
    }

    public Integer insert(SysDict sysDict) {
    	try {
    	        return sysDictManager.insert( sysDict );
		} catch (ManagerException e) {
			logger.error("插入数据字典失败,sysDict=" + sysDict, e);
		}
        return null;
        
    }

    public SysDict selectByPrimaryKey(Long id) {
    	try {
    		 return sysDictManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("通过id查询数据字典失败,id=" + id, e);
		}
        return null;
        
    }

    public Integer updateByPrimaryKey(SysDict sysDict) {
    	try {
    		 return sysDictManager.updateByPrimaryKey(sysDict );
		} catch (ManagerException e) {
			logger.error("更新数据字典失败,sysDict=" + sysDict, e);
		}
        return null;
        
    }

    public Integer updateByPrimaryKeySelective(SysDict sysDict) {
    	try {
    		 return sysDictManager.updateByPrimaryKeySelective(sysDict );
		} catch (ManagerException e) {
			logger.error("更新数据字典失败,sysDict=" + sysDict, e);
		}
        return null;
        
    }

    public Integer batchDelete(long[] ids) {
    	try {
    		return sysDictManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("批量删除数据字典失败,ids=" + ids, e);
		}
        return null;
         
    }

    public Page<SysDict> findByPage(Page<SysDict> pageRequest, Map<String, Object> map) {
    	try {
    		 return sysDictManager.findByPage(pageRequest,map); 
		} catch (ManagerException e) {
			logger.error("分页查询数据字典失败", e);
		}
        return null;
       
    }

	@Override
	public List<SysDict> findByGroupName(String groupName) {
		try {
   		 return sysDictManager.findByGroupName(groupName);
		} catch (ManagerException e) {
			logger.error("分组查询数据字典失败", e);
		}
       return null;
	}
}