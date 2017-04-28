package com.yourong.core.msg.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.CustomMessage;
import com.yourong.core.msg.model.MessageLog;
import com.yourong.core.msg.model.biz.MessageLogForMember;
import com.yourong.core.msg.model.query.MessageLogQuery;

public interface MessageLogManager {

	/**
	 * 
	 * @param query
	 * @return
	 * @throws ManagerException
	 */
	public Page<MessageLogForMember> queryMessageLogForPagin(MessageLogQuery query) throws ManagerException;

	/**
	 * 批量更新消息状态
	 * 
	 * @param query
	 * @return
	 */
	public int batchUpdateMessageStatus(MessageLogQuery messageLogQuery) throws ManagerException;

	/**
	 * 更新单条消息状态
	 * 
	 * @param memberId
	 * @param messageId
	 * @return
	 */
	public int updateMessageStatus(Long memberId, Long messageId) throws ManagerException;

	/**
	 * 添加消息
	 * 
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	public int insert(MessageLog record) throws ManagerException;

	/**
	 * 
	 * @param content
	 * @param memberId
	 * @param templateId
	 * @return
	 */
	public int insertFromMessageTemplate(String content, Long memberId, Long templateId, Integer notifyType,Integer msgType,Integer serverType) throws ManagerException;

	/**
	 * 统计未读消息总数
	 * 
	 * @param memberId
	 * @return
	 */
	public int countUnreadMessage(Long memberId) throws ManagerException;

	/**
	 * 
	 * @Description:自定义站内信数据初始化
	 * @param memberId
	 * @author: wangyanji
	 * @time:2015年12月9日 下午1:38:05
	 */
	public void readInternalMessages(Long memberId);

	/**
	 * 
	 * @Description:判断是否有效的用户
	 * @param msg
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月9日 下午1:59:12
	 */
	public boolean checkMemberIsUse(CustomMessage msg, Long memberId) throws ManagerException;
	

	/**
	 * 根据用户ID更新app消息已读
	 * 
	 * @param memberId
	 * @param messageId
	 * @return
	 */
	public int updateMessageStatusByMemberId(Long memberId) throws ManagerException;
	

	/**
	 * 根据用户ID、消息类型，统计用户未读数据
	 * @param memberId
	 * @param msgType
	 * @return
	 */
	public int countUnreadMessageByType(Long memberId,Integer msgType) throws ManagerException;
}
