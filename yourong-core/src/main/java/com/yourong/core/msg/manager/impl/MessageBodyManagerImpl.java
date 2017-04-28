package com.yourong.core.msg.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.msg.dao.MessageBodyMapper;
import com.yourong.core.msg.manager.MessageBodyManager;
import com.yourong.core.msg.model.MessageBody;

@Component
public class MessageBodyManagerImpl implements MessageBodyManager {
	
	@Autowired
	private MessageBodyMapper messageBodyMapper;

	@Override
	public int insert(MessageBody record) throws ManagerException {
		try{
			return messageBodyMapper.insert(record);
		}catch(Exception ex){
			throw ex;
		}
	}

	@Override
	public MessageBody getMessageBody(Long msgId, Integer msgSource) {
		try{
			return messageBodyMapper.getMessageBody(msgId, msgSource);
		}catch(Exception ex){
			throw ex;
		}
	}

}
