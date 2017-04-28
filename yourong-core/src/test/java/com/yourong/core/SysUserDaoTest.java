package com.yourong.core;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.google.common.collect.Maps;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.sys.dao.SysUserMapper;
import com.yourong.core.sys.model.SysRole;
import com.yourong.core.sys.model.SysUser;

public class SysUserDaoTest extends BaseTest{
	@Autowired
	private SysUserMapper sysUserMapper;
	
	@Autowired
	private BalanceManager  balanceManager;
	
	
//	@Rollback(false)
//	//@Test
//	public void saveSysUserTest(){
//		SysUser info = new SysUser();
//		info.setLoginName("xxxxx");
//		info.setPassword("xxxxxxxx");
//		info.setName("ssss");
//		int insertSelective = sysUserMapper.insertSelective(info);
//		System.out.println(insertSelective);
//	}
//	//@Test
//	public void findByPageTest(){
//
//		Page pageRequest =new Page<SysUser>();
//		pageRequest.setiDisplayLength(10);
//		pageRequest.setiDisplayStart(1);
//		Map map = Maps.newHashMap();
////		Page findByPage = sysUserMapper.findByPage(pageRequest , map, sort );
////		for (int i = 0; i < findByPage.getData().size(); i++) {
////			System.out.println(findByPage.getData().get(i));
////		}
//	}
	
	@Test
	public void selectUserRole(){
		SysUser user = sysUserMapper.selectSysUserRole(4l);
		List<SysRole> roles = user.getRoles();
		SysRole sysRole = roles.get(0);
		System.out.println(sysRole.getName());
		
		
		
		
		
		
	}
	
	@Test
	@Rollback(false)
	public void testManget(){
		try {
			balanceManager.resetProjectBalance(new BigDecimal("19.50"), 1009L);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
}
