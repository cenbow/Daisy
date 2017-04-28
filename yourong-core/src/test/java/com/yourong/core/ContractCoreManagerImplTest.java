/**
 * 
 */
package com.yourong.core;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.model.QuickRewardConfig;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.tc.manager.ContractCoreManager;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年7月27日上午11:25:30
 */
public class ContractCoreManagerImplTest extends BaseTest{

	 @Autowired
	    private ContractCoreManager contractCoreManager;
	 
	 @Autowired
	 private ProjectExtraManager projectExtraManager;
	 
	 
	 @Test
		public void selectSysDictTest(){
		
		 Date date = DateUtils.getDateFromString("2017-02-06");
		 Date begin = DateUtils.getDateFromString("2017-02-06");
		 Date end = DateUtils.getDateFromString("2017-02-06");

		 
		 DateUtils.isDateBetween(DateUtils.getCurrentDate(), begin, end);
		 String ab="abc";
		}
	
}
