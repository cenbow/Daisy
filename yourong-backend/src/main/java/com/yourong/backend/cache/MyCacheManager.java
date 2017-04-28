package com.yourong.backend.cache;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yourong.backend.sys.service.SysAreaService;
import com.yourong.backend.sys.service.SysDictService;
import com.yourong.backend.sys.service.SysMenuService;
import com.yourong.backend.sys.service.SysRoleService;
import com.yourong.core.sys.model.SysArea;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.sys.model.SysMenu;
import com.yourong.core.sys.model.SysRole;
import com.yourong.core.uc.dao.ThirdCompanyMapper;
import com.yourong.core.uc.model.ThirdCompany;

@Service
public class MyCacheManager {
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(MyCacheManager.class);

	@Autowired
	private SysDictService sysDictService;

	@Autowired
	private SysMenuService sysMenuService;
	
	@Autowired
	private SysAreaService sysAreaService;
	
	@Autowired
	private SysRoleService sysRoleService;
	
	@Autowired
	private ThirdCompanyMapper thirdCompanyMapper;
	/**
	 * 获取指定数据字典
	 * @param groupName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  List<SysDict> getListSysDictByGroupName(final String groupName) {
					return sysDictService.findByGroupName(groupName);
	}
	/**
	 * 获取所有菜单
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysMenu> getAllMenu(){
					return sysMenuService.getAllSysmenu();
	}	
	
	
	/**
	 * 获取所有菜单
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysMenu> getTreeMenu(){
		return sysMenuService.getTreeSysmenus();
	}
	/**
	 * 获取所有区域
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysArea> getAllArea(){
					return sysAreaService.getAllSysArea();
	}	
	
	/**
	 * 获取指定类型的区域（type=2查询所有省）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysArea> getSysAreasByType(){
		return sysAreaService.getSysAreasByType(2);
	}
	/**
	 * 获取所有有效的角色
	 * @return
	 */
	public List<SysRole> getAllSysRole(){
					return sysRoleService.selectAllSysRole();
	}
	/**
	 * 
	 * @Description:获取第三方垫付公司
	 * @return
	 * @author: chaisen
	 * @time:2016年1月7日 下午4:06:11
	 */
	public List<ThirdCompany> getThirdCompanyList() {
		return thirdCompanyMapper.getThirdCompanyList();
	}


}
