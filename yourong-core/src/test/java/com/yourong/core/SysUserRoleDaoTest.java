package com.yourong.core;

import java.util.List;

import com.yourong.core.sys.manager.SysUserManager;
import com.yourong.core.sys.model.SysUser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.core.sys.dao.SysUserRoleMapper;
import com.yourong.core.sys.model.SysUserRole;

import static org.apache.commons.lang.ArrayUtils.*;
import static org.junit.Assert.*;

public class SysUserRoleDaoTest extends BaseTest {
	
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
//
    @Autowired
	private SysUserManager sysUserManager;
	
	@Test
	public void getSysUserRoleByUserId() throws  Exception{
		SysUser sysUser = sysUserManager.selectByPrimaryKey(54L);
		Long id = sysUser.getId();
		assertEquals(id.longValue(), 55L);
	}

}
