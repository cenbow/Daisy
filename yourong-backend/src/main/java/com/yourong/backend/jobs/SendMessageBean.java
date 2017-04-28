package com.yourong.backend.jobs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.yourong.backend.utils.BackendPropertiesUtil;
import com.yourong.common.constant.Constant;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.SendMsgClient;
import com.yourong.core.uc.manager.MemberNotifySettingsManager;
import com.yourong.core.uc.model.biz.MemberNotifySettingsForSendMsg;

/**
 * 发送消息bean
 * 
 * @author Administrator
 *
 */
public class SendMessageBean {

	private static Logger logger = LoggerFactory
			.getLogger(SendMessageBean.class);

	private ExecutorService exec;
	@Autowired
	private MemberNotifySettingsManager memberNotifySettingsManager;

	public void init() {
		int theadcount = BackendPropertiesUtil.getSendMessageThreadCount();
		exec = Executors.newFixedThreadPool(theadcount);
		for (int i = 0; i < theadcount; i++) {
			exec.execute(new Runnable() {
				@Override
				public void run() {
					while (true) {
						String message = SendMsgClient.popSendMsgFromRedis();
						if (StringUtil.isNotBlank(message)) {
							try {
								MemberNotifySettingsForSendMsg notifySettingsForSendMsg = JSONObject
										.parseObject(
												message,
												MemberNotifySettingsForSendMsg.class);
								if (notifySettingsForSendMsg.getSendType() == Constant.MSG_SEND_BY_FORWARDOBJECT) {
									memberNotifySettingsManager
											.sendMessageDirectly(notifySettingsForSendMsg);
								} else {
									memberNotifySettingsManager
											.sendMessageByNotifyType(notifySettingsForSendMsg);
								}
							} catch (Exception e) {
								logger.error("发送通知发送异常,", e);
							}
						} else {
							try {
								Thread.sleep(100000);// 100000
							} catch (InterruptedException e) {
								logger.error("线程中断", e);
							}
						}
					}
				}
			});
		}

	}

	public void close() {
		exec.isShutdown();
	}

}
