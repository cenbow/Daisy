package com.yourong.backend.sys.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yourong.common.util.Collections3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.cache.MyCacheManager;
import com.yourong.backend.sys.service.SysRoleService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.web.ResultObject;
import com.yourong.core.sys.manager.SysRoleManager;
import com.yourong.core.sys.model.SysMenu;
import com.yourong.core.sys.model.SysRole;

@Service
public class SysRoleServiceImpl implements SysRoleService {
	private static Logger logger = LoggerFactory.getLogger(SysRoleServiceImpl.class);	
	
    @Autowired
    private SysRoleManager sysRoleManager;
    
    @Autowired
    private MyCacheManager myCacheManager;
    

    public int deleteByPrimaryKey(Long id) {
        int result = 0;
		try {
			result = sysRoleManager.deleteByPrimaryKey(id);
		} catch (ManagerException e) {			
			logger.error("deleteByPrimaryKey", e);
		}
        return result;
    }

    public int insert(SysRole sysRole) {
    	 int result = 0;
 		try {
 			sysRole.setDataScope("1");
 			sysRole.setDelFlag("0");
 			result = sysRoleManager.insert(sysRole);
 		} catch (ManagerException e) {			
 			logger.error("insert", e);
 		}
         return result;
    }

	public SysRole selectByPrimaryKey(Long id) {
		SysRole result = null;
		try {
			result = sysRoleManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("selectByPrimaryKey", e);
		}
		return result;
	}

    public SysRole  selectRoleMenus(Long id){
    	SysRole result = null;
		try {
			result = sysRoleManager.selectRoleMenus(id);
		} catch (ManagerException e) {
			logger.error("selectRoleMenus", e);
		}
		return result;
    }
    

	public int updateByPrimaryKey(SysRole sysRole) {
		int result = 0;
		try {
			result = sysRoleManager.updateByPrimaryKey(sysRole);
		} catch (ManagerException e) {
			logger.error("updateByPrimaryKey", e);
		}
		return result;
	}

    public int updateByPrimaryKeySelective(SysRole sysRole) {
		int result = 0;
		try {
			result = sysRoleManager.updateByPrimaryKeySelective(sysRole);
		} catch (ManagerException e) {
			logger.error("updateByPrimaryKeySelective", e);
		}
		return result;
    }

    public int batchDelete(long[] ids) {
    	int result = 0;
		try {
			result = sysRoleManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("batchDelete", e);
		}
		return result;
    }

    public Page findByPage(Page pageRequest, Map map) {
    	
		try {
			 return sysRoleManager.findByPage(pageRequest,map);
		} catch (ManagerException e) {
			logger.error("findByPage", e);
		}
		return null;
    }

	@Override
	public List<SysMenu> selectRoleTree(Long id) {
		SysRole role = selectRoleMenus(id);
		Set set = Collections3.extractToSet(role.getMenus(), "id");
		List<SysMenu> allMenu = myCacheManager.getAllMenu();		
		if (role != null && role.getMenus() != null) {
			for (SysMenu temp : allMenu) {
				temp.setChecked(false);
//				for (SysMenu menu : role.getMenus()) {
					if (set.contains(temp.getId())) {
						temp.setChecked(true);
					}
//				}
			}
		}
		return allMenu;
	}

	@Override
	public ResultObject batchInsertRoleAndMenus(Long id, long[] menus) {
		ResultObject result = new ResultObject();		
		try {
			sysRoleManager.batchDeleteRoleAndMenus(id);
			int i = sysRoleManager.batchInsertRoleAndMenus(id, menus);
			if(i > 0){
				result.isSuccess();
			}			
		} catch (ManagerException e) {
			logger.error("修改权限树", e);
			result.isError();
		}		
		return result;
	}

	@Override
	public List<SysRole> selectAllSysRole() {
		try {
			return sysRoleManager.selectAllSysRole();
		} catch (ManagerException e) {
			logger.error("selectAllSysRole", e);
		}
		return null;
	}

	@Override
	public boolean checkRoleNameExists(String name, long selfId) {
		try {
			SysRole sysRole = sysRoleManager.checkRoleNameExists(name, selfId);
			if(sysRole != null){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("checkRoleNameExists", e);
		}
		return false;
	}

	@Override
	public boolean checkRoleNameExists(String name) {
		return checkRoleNameExists(name, -1);
	}
}