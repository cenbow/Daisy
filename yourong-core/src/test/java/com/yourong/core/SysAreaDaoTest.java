package com.yourong.core;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.core.sys.dao.SysAreaMapper;
import com.yourong.core.sys.model.SysArea;

public class SysAreaDaoTest extends BaseTest{
	@Autowired
	private SysAreaMapper sysAreaMapper;
	
	
	@Test
	@Rollback(false)
	public void saveSysAreaTest(){
		SysArea record = new SysArea();
		record.setCode("ss");
		record.setName("ssssss");
		record.setParentId((long) 1);;
		sysAreaMapper.insertSelective(record );
	}
	
}
