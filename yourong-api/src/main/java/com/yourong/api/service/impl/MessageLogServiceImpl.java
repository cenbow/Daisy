package com.yourong.api.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.MessageLogService;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.StringUtil;
import com.yourong.core.msg.manager.MessageLogManager;
import com.yourong.core.msg.model.biz.MessageLogForMember;
import com.yourong.core.msg.model.query.MessageLogQuery;

@Service
public class MessageLogServiceImpl implements MessageLogService {

	private static final Logger logger = LoggerFactory.getLogger(MessageLogServiceImpl.class);

	@Autowired
	private MessageLogManager messageLogManager;

	@Override
	public ResultDTO<Object> queryMessageLogForPagin(MessageLogQuery query) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			Page<MessageLogForMember> paper = messageLogManager.queryMessageLogForPagin(query);
			if (Collections3.isNotEmpty(paper.getData())) {
				for (MessageLogForMember mfm : paper.getData()) {
					//mfm.setContent(StringUtil.splitAndFilterString(mfm.getContent(), 5000));
					if(16==mfm.getServiceType()){
						String content = mfm.getContent();
						Map map = JSON.parseObject(content);
						if(map.containsKey("part5")){
							mfm.setTitle(map.get("part5").toString());
						}
					}
				}
			}
			result.setResult(paper);
			//查询后即视为已读
			messageLogManager.updateMessageStatusByMemberId(query.getMemberId());
		} catch (ManagerException e) {
			logger.error("分页获取用户消息异常", e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

	@Override
	public ResultDTO<Object> updateMessageStatus(Long memberId, Long messageId) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			messageLogManager.updateMessageStatus(memberId, messageId);
			result.setIsSuccess();
		} catch (ManagerException e) {
			logger.error("更新消息状态异常", e);
			result.setIsError();
		}
		return result;
	}

	@Override
	public ResultDTO<Object> batchUpdateMessageStatus(MessageLogQuery messageLogQuery) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		try {
			messageLogManager.batchUpdateMessageStatus(messageLogQuery);
			result.setIsSuccess();
		} catch (ManagerException e) {
			logger.error("批量更新消息状态异常", e);
			result.setIsError();
		}
		return result;
	}

	@Override
	public int countUnreadMessage(Long memberId) {
		try {
			return messageLogManager.countUnreadMessage(memberId);
		} catch (ManagerException e) {
			logger.error("获取未读站内信失败", e);
		}
		return 0;
	}

	
	
}
