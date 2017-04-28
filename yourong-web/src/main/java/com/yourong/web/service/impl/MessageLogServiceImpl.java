package com.yourong.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.manager.CustomMessageManager;
import com.yourong.core.msg.manager.MessageBodyManager;
import com.yourong.core.msg.manager.MessageLogManager;
import com.yourong.core.msg.model.biz.MessageLogForMember;
import com.yourong.core.msg.model.query.MessageLogQuery;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.web.service.MessageLogService;

@Service
public class MessageLogServiceImpl implements MessageLogService {
	private static Logger logger = LoggerFactory.getLogger(MessageLogServiceImpl.class);
	@Autowired
	private MessageLogManager messageLogManager;

	@Autowired
	private CustomMessageManager customMessageManager;

	@Autowired
	private MessageBodyManager messageBodyManager;

	@Autowired
	private TransactionManager transactionManager;

	@Override
	public Page<MessageLogForMember> queryMessageLogForPagin(MessageLogQuery query) {
		try {
			return messageLogManager.queryMessageLogForPagin(query);
		} catch (ManagerException e) {
			logger.error("分页获取用户消息异常", e);
		}
		return null;
	}

	@Override
	public ResultDO batchUpdateMessageStatus(MessageLogQuery messageLogQuery) {
		ResultDO result = new ResultDO();
		try {
			int updateNum = messageLogManager.batchUpdateMessageStatus(messageLogQuery);
			if (updateNum <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			logger.error("批量更新消息状态异常", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public ResultDO updateMessageStatus(Long memberId, Long messageId) {
		ResultDO result = new ResultDO();
		try {
			int updateNum = messageLogManager.updateMessageStatus(memberId, messageId);
			if (updateNum <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			logger.error("更新消息状态异常", e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public int countUnreadMessage(Long memberId) {
		try {
			return messageLogManager.countUnreadMessage(memberId);
		} catch (ManagerException e) {
			logger.error("统计未读消息总数异常", e);
		}
		return 0;
	}

}
