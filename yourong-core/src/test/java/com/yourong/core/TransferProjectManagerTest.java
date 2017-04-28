/**
 * 
 */
package com.yourong.core;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.tc.model.Transaction;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年8月31日上午10:23:46
 */
public class TransferProjectManagerTest extends BaseTest {

	@Autowired
    private ActivityAfterTransactionManager underwriteLogManager;
	/**
	 * 
	 * @return 生成垫资记录
	 * @throws ManagerException
	 */
 
    /**
     * 
     * @return 更新垫资记录
     * @throws ManagerException
     */
    @Test
    @Rollback(false)
    public int  updateUnderWriteByInterestIdTest() throws ManagerException{
    	Transaction transaction=new Transaction();
    	underwriteLogManager.thirtyGift(transaction);
    	return 0;
    }
	
}
