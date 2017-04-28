package com.yourong.core;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.PopularityInOutLog;

public class PopularityManagerTest extends BaseTest {

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;
	@Test
	@Rollback(false)
	public void getPopularity() {
		try {
			List<PopularityInOutLog> list = Lists.newArrayList();
			list = popularityInOutLogManager.findLastExchangeCoupon();
			System.out.println(list);
		} catch (ManagerException e) {
		}
	}
}
