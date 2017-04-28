package com.yourong.core.msg.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.msg.model.MessageBody;

public interface MessageBodyManager {

	/**
	 * 
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	public int insert(MessageBody record) throws ManagerException;
	
	/**
	 * 获取消息主体内容
	 * @param msgId 消息ID
	 * @param msgSource 消息来源
	 * @return
	 */
	public MessageBody getMessageBody(Long msgId, Integer msgSource);
}
