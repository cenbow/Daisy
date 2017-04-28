package com.yourong.core;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.OverdueRepayLogManager;
import com.yourong.core.fin.manager.UnderwriteLogManager;
import com.yourong.core.ic.model.ProjectInterestBiz;
/**
 * 
 * @desc 逾期还款记录测试用例
 * @author chaisen
 * 2016年3月14日上午11:02:33
 */


public class OverdueRepayLogManageTest extends BaseTest {
    @Autowired
    private OverdueRepayLogManager overdueRepayLogManager;
    
    @Autowired
    private UnderwriteLogManager underwriteLogManager;
	/**
	 * 
	 * @Description:保存逾期还款记录
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月14日 上午11:07:57
	 */
    //@Test
    @Rollback(false)
    public int  saveOverdueRepayLogTest() throws ManagerException{
    	ProjectInterestBiz biz=new ProjectInterestBiz();
    	int i=overdueRepayLogManager.saveOverdueRepayLog(biz);
    	return i;
    }
    /**
     * 
     * @return 统计逾期记录数
     * @throws ManagerException
     */
	@Test
	@Rollback(false)
	public int  countOverdueRepayLogByProjectIdTest() throws ManagerException{
	   int i=overdueRepayLogManager.countOverdueRepayLogByProjectId(984509394L);
	   return i;
    }


}
