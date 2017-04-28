package com.yourong.core;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.core.sys.manager.SysDictManager;

/**
 * 
 * @desc TODO
 * @author chaisen
 * 2016年4月7日下午2:27:54	
 */
public class SysDictManagerTest extends BaseTest  {
    @Autowired
    private SysDictManager sysDictManager;
    @Autowired
	private SmsMobileSend smsMobileSend;
    @Test
    @Rollback(false)
    public void sendSmsRemindTest(){
        try {
        	sysDictManager.sendSmsRemind();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    //@Test
    @Rollback(false)
    public void queryBalanceSMSTest(){
        try {
        	smsMobileSend.queryBalanceSMS("6SDK-EMY-6688-KFXSO");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }


}
