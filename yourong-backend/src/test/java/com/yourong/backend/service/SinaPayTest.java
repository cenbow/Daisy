package com.yourong.backend.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.backend.BaseWebControllerTest;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;

public class SinaPayTest extends BaseWebControllerTest {
	@Autowired
	private SinaPayClient sinaPayClient;

	@Test
	public void receiveCoupon() {		
		System.out.println("ss");
	}

}
