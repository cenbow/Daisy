package com.yourong.core;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.yourong.core.sys.dao.SysMenuMapper;
import com.yourong.core.sys.dao.SysRoleMapper;
import com.yourong.core.sys.model.SysMenu;
import com.yourong.core.sys.model.SysRole;
import com.yourong.core.sys.model.SysRoleMenu;

public class SysRoleDaoTest extends BaseTest{
	@Autowired
	private SysRoleMapper sysRoleMapper;
	
	@Autowired
	private  SysMenuMapper sysMenuMapper;
	
	
	//@Test
	public void selectRoleMapper(){
		SysRole role = sysRoleMapper.selectRoleMenus((long) 1);
		System.out.println(role.getMenus().size());
	}
	
	//@Test
	public void batchInsertSysRole(){
		Long id  = 1L;
		Long[] menus ={1L,2L,3L,4L};
		
		List<SysRoleMenu> list = Lists.newArrayList();
		SysRoleMenu  temp = null;
		for(Long menuID: menus){
			temp = new  SysRoleMenu();
			temp.setRoleId(id);
			temp.setMenuId(menuID);
			list.add(temp);
		}			
		sysRoleMapper.batchInsertRoleAndMenus(list);
	}
	
	@Test
	public void selectTreeSysMenus(){
		List<SysMenu> selectChildByParent = sysMenuMapper.selectChildByParent(1L, 1);
		System.out.println(selectChildByParent.size());
	}
	
	
	
}
