package com.yourong.backend;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.core.uc.manager.MemberNotifySettingsManager;

public class SendMailTest extends BaseWebControllerTest{
	@Autowired
	private MemberNotifySettingsManager memberNotifySettingsManager;
	
	@Test
	public void testSendMessage(){
		try {
//			memberNotifySettingsManager.sendMessageByNotifyType(110800000088L, 2, "30");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
