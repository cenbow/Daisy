package com.yourong.core.msg.manager.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.msg.dao.MessageBodyMapper;
import com.yourong.core.msg.dao.MessageLogMapper;
import com.yourong.core.msg.manager.CustomMessageManager;
import com.yourong.core.msg.manager.MessageBodyManager;
import com.yourong.core.msg.manager.MessageLogManager;
import com.yourong.core.msg.model.CustomMessage;
import com.yourong.core.msg.model.MessageBody;
import com.yourong.core.msg.model.MessageLog;
import com.yourong.core.msg.model.biz.MessageLogForMember;
import com.yourong.core.msg.model.query.MessageLogQuery;
import com.yourong.core.tc.manager.TransactionManager;

@Component
public class MessageLogManagerImpl implements MessageLogManager {

	private static final Logger logger = LoggerFactory.getLogger(MessageLogManagerImpl.class);

	@Autowired
	private MessageLogMapper messageLogMapper;

	@Autowired
	private MessageBodyMapper messageBodyMapper;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private CustomMessageManager customMessageManager;

	@Autowired
	private MessageBodyManager messageBodyManager;

	@Override
	public Page<MessageLogForMember> queryMessageLogForPagin(MessageLogQuery query) throws ManagerException {
		Page<MessageLogForMember> page = new Page<MessageLogForMember>();
		try {
			List<MessageLogForMember> messageLogList = Lists.newArrayList();
			int count = messageLogMapper.queryMessageLogForPaginTotalCount(query);
			if (count > 0) {
				messageLogList = messageLogMapper.queryMessageLogForPagin(query);
			}
			page.setData(messageLogList);
			// 每页总数
			page.setiDisplayLength(query.getPageSize());
			// 当前页
			page.setPageNo(query.getCurrentPage());
			// 总数
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return page;
	}

	@Override
	public int batchUpdateMessageStatus(MessageLogQuery messageLogQuery) throws ManagerException {
		try {
			return messageLogMapper.batchUpdateMessageStatus(messageLogQuery);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public int updateMessageStatus(Long memberId, Long messageId) throws ManagerException {
		try {
			return messageLogMapper.updateMessageStatus(memberId, messageId);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public int insert(MessageLog record) throws ManagerException {
		try {
			return messageLogMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertFromMessageTemplate(String content, Long memberId, Long templateId, Integer notifyType,Integer msgType,Integer serviceType) throws ManagerException {
		MessageBody mb = new MessageBody();
		mb.setContent(content);
		mb.setMsgId(templateId);
		mb.setMsgSource(2);
		messageBodyMapper.insert(mb);
		MessageLog log = new MessageLog();
		log.setServiceType(serviceType);
		log.setMemberId(memberId);
		log.setMsgBodyId(mb.getId());
		log.setMsgSource(2);
		log.setMsgType(msgType);
		log.setNotifyType(notifyType);
		log.setReceiveDate(DateUtils.getCurrentDate());
		log.setStatus(0);
		log.setDelFlag(1);
		log.setMsgSourceId(templateId);
		insert(log);
		return 1;
	}

	@Override
	public int countUnreadMessage(Long memberId) throws ManagerException {
		try {
			readInternalMessages(memberId);
			return messageLogMapper.countUnreadMessage(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public void readInternalMessages(Long memberId) {
		try {
			List<CustomMessage> messageList = customMessageManager.queryUnreadMessages(memberId);
			if (Collections3.isNotEmpty(messageList)) {
				for (CustomMessage msg : messageList) {
					if (checkMemberIsUse(msg, memberId)) {
						MessageBody mb = messageBodyManager.getMessageBody(msg.getId(), 1);
						if (mb == null) {
							mb = new MessageBody();
							mb.setContent(msg.getContent());
							mb.setMsgId(msg.getId());
							mb.setMsgSource(1);
							messageBodyManager.insert(mb);
						}
						MessageLog log = new MessageLog();
						log.setMemberId(memberId);
						log.setMsgBodyId(mb.getId());
						log.setMsgSource(1);
						log.setMsgType(msg.getMsgType());
						log.setNotifyType(msg.getNotifyType());
						log.setReceiveDate(DateUtils.getCurrentDate());
						log.setStatus(0);
						log.setDelFlag(1);
						log.setMsgSourceId(msg.getId());
						insert(log);
					}
				}
			}
		} catch (Exception e) {
			logger.error("读取站内信异常", e);
		}
	}

	@Override
	public boolean checkMemberIsUse(CustomMessage msg, Long memberId) throws ManagerException {
		try {
			if (msg.getUserType() == 3) {
				String projectId = msg.getCustomAttr();
				if (projectId != null && StringUtil.isNumeric(projectId)) {
					BigDecimal amount = transactionManager.investmentMaxAmountProject(Long.parseLong(projectId), memberId);
					if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
						return false;
					}
				} else {
					return false;
				}
			} else if (msg.getUserType() == 4) {
				String memberIds = msg.getCustomAttr();
				if (memberIds != null) {
					if (!memberIds.contains(memberId.toString())) {
						return false;
					}
				} else {
					return false;
				}
			} else if (msg.getUserType() == 2) {
				int count = transactionManager.getTransactionCountByMember(memberId);
				if (count <= 0) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int updateMessageStatusByMemberId(Long memberId) throws ManagerException {
		try {
			return messageLogMapper.updateMessageStatusByMemberId(memberId,Constant.MSG_TEMPLATE_TYPE_APP);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int countUnreadMessageByType(Long memberId,Integer msgType) throws ManagerException {
		try {
			readMessagesByType(memberId,msgType);
			return messageLogMapper.countUnreadMessageByType(memberId,msgType);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	private void readMessagesByType(Long memberId,Integer msgType) {
		try {
			List<CustomMessage> messageList = customMessageManager.queryUnreadMessagesByType(memberId,msgType);
			if (Collections3.isNotEmpty(messageList)) {
				for (CustomMessage msg : messageList) {
					if (checkMemberIsUse(msg, memberId)) {
						MessageBody mb = messageBodyManager.getMessageBody(msg.getId(), 1);
						if (mb == null) {
							mb = new MessageBody();
							String content = msg.getContent();
							if(5==msg.getMsgType()){//app消息
								content = "{\"part1\":\""+msg.getContent()+"\",\"part4\":\""+msg.getRemark()+"\""
										+ ",\"part5\":\""+msg.getMsgName()+"\"}";
							}
							mb.setContent(content);
							mb.setMsgId(msg.getId());
							mb.setMsgSource(1);
							messageBodyManager.insert(mb);
						}
						MessageLog log = new MessageLog();
						log.setMemberId(memberId);
						log.setMsgBodyId(mb.getId());
						log.setMsgSource(1);
						log.setMsgType(msg.getMsgType());
						log.setNotifyType(msg.getNotifyType());
						if(5==msg.getMsgType()){
							log.setServiceType(16);
						}
						log.setReceiveDate(DateUtils.getCurrentDate());
						log.setStatus(0);
						log.setDelFlag(1);
						log.setMsgSourceId(msg.getId());
						insert(log);
					}
				}
			}
		} catch (Exception e) {
			logger.error("读取站内信异常", e);
		}
	}
	
}
