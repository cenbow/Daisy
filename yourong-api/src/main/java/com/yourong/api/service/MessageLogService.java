package com.yourong.api.service;

import com.yourong.api.dto.ResultDTO;
import com.yourong.core.msg.model.query.MessageLogQuery;

public interface MessageLogService {

	/**
	 * 
	 * @Description:站内信查询
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月3日 上午10:02:12
	 */
	public ResultDTO<Object> queryMessageLogForPagin(MessageLogQuery query);

	/**
	 * 
	 * @Description:更新单条消息状态
	 * @param memberId
	 * @param messageId
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月4日 下午4:24:53
	 */
	public ResultDTO<Object> updateMessageStatus(Long memberId, Long messageId);

	/**
	 * 
	 * @Description:批量更新消息状态
	 * @param messageLogQuery
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月4日 下午4:45:20
	 */
	public ResultDTO<Object> batchUpdateMessageStatus(MessageLogQuery messageLogQuery);

	/**
	 * 
	 * @Description:未读站内信消息数
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月9日 下午1:19:48
	 */
	public int countUnreadMessage(Long memberId);
}
