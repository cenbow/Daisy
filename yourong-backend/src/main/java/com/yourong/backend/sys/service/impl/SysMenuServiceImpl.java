package com.yourong.backend.sys.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.sys.service.SysMenuService;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.manager.SysMenuManager;
import com.yourong.core.sys.model.SysMenu;

@Service
public class SysMenuServiceImpl  implements SysMenuService {
	
	private static Logger logger = LoggerFactory.getLogger(SysMenuServiceImpl.class);
    @Autowired
    private SysMenuManager sysMenuManager;

    public Integer deleteByPrimaryKey(Long id) {
    	try {
    		return sysMenuManager.deleteByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("删除菜单失败，id=" + id, e);
		}
        return null;
    }

    public Integer insert(SysMenu sysMenu) {
    	try {
    		sysMenu.setDelFlag(0);
    		return sysMenuManager.insert( sysMenu );
		} catch (Exception e) {
			logger.error("新增菜单失败，sysMenu=" + sysMenu, e);
		}
        return null;
    }

    public SysMenu selectByPrimaryKey(Long id) {
    	try {
    		return sysMenuManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("查询菜单失败，id=" + id, e);
		}
        return null;
    }

    public Integer updateByPrimaryKey(SysMenu sysMenu) {
    	try {
    		return sysMenuManager.updateByPrimaryKey(sysMenu );
		} catch (Exception e) {
			logger.error("更新菜单失败，sysMenu=" + sysMenu, e);
		}
        return null;
    }

    public Integer updateByPrimaryKeySelective(SysMenu sysMenu) {
    	try {
    		return sysMenuManager.updateByPrimaryKeySelective(sysMenu );
		} catch (Exception e) {
			logger.error("更新菜单失败，sysMenu=" + sysMenu, e);
		}
        return null;
    }

    public Integer batchDelete(long[] ids) {
    	try {
    		return sysMenuManager.batchDelete(ids);
		} catch (Exception e) {
			logger.error("批量删除菜单失败，ids=" + ids, e);
		}
        return null;
    }

    public Page<SysMenu> findByPage(Page<SysMenu> pageRequest, Map<String, Object> map) {
    	try {
    		 return sysMenuManager.findByPage(pageRequest,map); 
		} catch (Exception e) {
			logger.error("分页查询菜单失败" , e);
		}
        return null;
    }

	@Override
	public List<SysMenu> getAllSysmenu() {
		try {
   		 return sysMenuManager.getAllSysmenu();
		} catch (Exception e) {
			logger.error("获取所有菜单失败" , e);
		}
       return null;
	}

	@Override
	public List<SysMenu> getTreeSysmenus() {
		try {			
			List<SysMenu> parent = sysMenuManager.selectChildByParent(1L, 1);
			for (SysMenu sys : parent) {
				List<SysMenu> selectChildByParent = sysMenuManager.selectChildByParent(sys.getId(), 2);
				if (selectChildByParent != null && selectChildByParent.size() > 0) {
//					for (SysMenu info1 : selectChildByParent) {
//						List<SysMenu> child1 = this.sysMenuManager.selectChildByParent(info1.getId(),3);
//						if (child1 != null && child1.size() > 0) {
//							info1.setChildList(child1);
//						}
//					}
					sys.setChildList(selectChildByParent);
					
				}
				
			}			
			return parent;
		} catch (Exception e) {
			logger.error("获取TREE所有菜单失败", e);
		}
		return null;
	}
}