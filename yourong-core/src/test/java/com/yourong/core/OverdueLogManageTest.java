package com.yourong.core;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.manager.UnderwriteLogManager;
import com.yourong.core.uc.manager.ThirdCompanyManager;
import com.yourong.core.uc.model.ThirdCompany;
/**
 * 
 * @desc 逾期记录、垫资 测试用例
 * @author chaisen
 * 2016年3月14日上午11:02:33
 */


public class OverdueLogManageTest extends BaseTest {
    @Autowired
    private OverdueLogManager overdueLogManager;
    
    @Autowired
    private ThirdCompanyManager thirdCompanyManager;
    
    @Autowired
    private UnderwriteLogManager underwriteLogManager;
	/**
	 * 
	 * @Description:统计逾期记录数
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月14日 上午11:07:57
	 */
    @Test
    @Rollback(false)
    public int  countOverdueRecordByProjectIdTest() throws ManagerException{
    	int i=overdueLogManager.countOverdueRecordByProjectId(989800483L);
    	return i;
    }
    /**
     * 
     * @Description:获取地三方垫资公司id
     * @return
     * @throws ManagerException
     * @author: chaisen
     * @time:2016年3月14日 上午11:10:49
     */
    //@Test
    @Rollback(false)
    public ThirdCompany  getThirdCompanyIdTest() throws ManagerException{
    	ThirdCompany thirdCompany =thirdCompanyManager.getThirdCompanyId(110800000201L,989800483L);
    	return thirdCompany;
    }
    /**
     * 
     * @Description:根据项目本息id  更新垫资表
     * @return
     * @throws ManagerException
     * @author: chaisen
     * @time:2016年3月14日 上午11:12:40
     */
    //@Test
    @Rollback(false)
    public int  updateUnderWriteLogByInterestIdTest() throws ManagerException{
    	int i =underwriteLogManager.updateUnderWriteLogByInterestId(11L);
    	return i;
    }


}
