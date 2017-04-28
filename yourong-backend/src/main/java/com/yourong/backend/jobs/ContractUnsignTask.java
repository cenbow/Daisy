package com.yourong.backend.jobs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.constant.Constant;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.tc.manager.TransactionManager;

public class ContractUnsignTask {

	private static final Logger logger = LoggerFactory
			.getLogger(ContractUnsignTask.class);

	@Autowired
	private SmsMobileSend smsMobileSend;
	@Autowired
	private TransactionManager transactionManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("未签署合同提醒开始");
					contractUnsign();
					logger.info("未签署合同提醒结束");
				} catch (Exception e) {
					logger.error("未签署合同提醒出现异常", e);
				}
			}
		});
	
	}

	private void contractUnsign() throws ManagerException {
		
		List<Long> memberList = transactionManager.selectUnSignMember();
		for(Long memberId:memberList){
			int num = transactionManager.getUnsignContractNum(memberId);
			MessageClient.sendMsgForCommon(memberId, Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.CONTRACT_UNSIGN.getCode(), 
					num+"");
		}
		
	}
	
	
}
