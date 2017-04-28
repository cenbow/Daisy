package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.biz.MessageLogForMember;
import com.yourong.core.msg.model.query.MessageLogQuery;

public interface MessageLogService {

	/**
	 * 
	 * @param query
	 * @return
	 */
	public Page<MessageLogForMember> queryMessageLogForPagin(MessageLogQuery query);

	/**
	 * 批量更新消息状态
	 * 
	 * @param query
	 * @return
	 */
	public ResultDO batchUpdateMessageStatus(MessageLogQuery messageLogQuery);

	/**
	 * 更新单条消息状态
	 * 
	 * @param memberId
	 * @param messageId
	 * @return
	 */
	public ResultDO updateMessageStatus(Long memberId, Long messageId);

	/**
	 * 统计未读消息总数
	 * 
	 * @param memberId
	 * @return
	 */
	public int countUnreadMessage(Long memberId);
}
