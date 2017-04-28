package com.yourong.core;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.core.sys.dao.SysDictMapper;
import com.yourong.core.sys.model.SysDict;

public class SysDictDaoTest extends BaseTest{
	@Autowired
	private SysDictMapper sysDictMapper;
	
	@Rollback(false)
//	@Test
	public void saveSysUserTest(){
		SysDict info = new SysDict();
		info.setKey("aaa");
		info.setValue("bbbb");
		info.setLabel("ccc");
		info.setDescription("dddddd");
		info.setSort(1);
		info.setStatus(1);
		info.setDelFlag(0);
		int insertSelective = sysDictMapper.insertSelective(info);
		System.out.println(insertSelective);
	}
	
	@Rollback(false)
	@Test
	public void selectSysDictTest(){
		List<SysDict> sysDictLists = sysDictMapper.findByGroupName("debt_type");
		System.out.println(sysDictLists);
	}

}
