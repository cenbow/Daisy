/**
 * 
 */
package com.yourong.backend;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.backend.jobs.WebMaintenAnnounceTask;
import com.yourong.common.exception.ManagerException;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年1月13日下午3:45:12
 */
public class WebMaintenAnnounceTaskTest extends BaseWebControllerTest {

	@Autowired
	private WebMaintenAnnounceTask webMaintenAnnounceTask;
	
	@Test
	public void testSendMessage(){
			try {
				webMaintenAnnounceTask.webMaintenAnnounce();
			} catch (ManagerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
}
