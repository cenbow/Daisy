package com.yourong.backend;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.backend.tc.service.TransactionService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.thirdparty.pay.sina.SinaPayConfig;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;

public class TransactionServiceTest extends BaseWebControllerTest {
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private TransactionService transactionService;

	

	@Test
    public void  toDirectLoan() throws  Exception{
    	Project p = projectManager.selectByPrimaryKey(989800430L);//测试项目‘989800430L’的放款
    	SinaPayConfig.setIndentityEmail("sinaweibopay_zg@weibopay.com");
    	//ResultDO<?> resultDO = transactionService.beforeLoanToOriginalCreditor(p.getId());
    	//System.out.println("放款结果{}"+resultDO.toString());
    	
    }

}
