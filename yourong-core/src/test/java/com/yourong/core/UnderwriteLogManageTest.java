package com.yourong.core;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.UnderwriteLogManager;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.tc.model.Transaction;
/**
 * 垫资 测试用例
 * @author chaisen 
 *
 */
public class UnderwriteLogManageTest extends BaseTest {
    
    
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
