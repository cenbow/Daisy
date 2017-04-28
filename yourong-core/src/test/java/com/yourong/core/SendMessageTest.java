package com.yourong.core;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.common.constant.Constant;
import com.yourong.common.enums.MessageEnum;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.dao.RechargeLogMapper;
import com.yourong.core.fin.model.RechargeLog;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.model.Transaction;

public class SendMessageTest extends BaseTest{

	@Autowired
	private TransactionMapper transactionMapper;
	
	@Test
    public  void testRechalog(){
		
		
		Transaction transaction = transactionMapper.selectByPrimaryKey(888800007934L);
		
		MessageClient.sendMsgForCommon(810800001331L, Constant.MSG_TEMPLATE_TYPE_BAIDU, MessageEnum.FIVE_PRIZE.getCode(), 
				transaction.getMarkProjectName(),"一锤定音");
    }
	
	

	
}
