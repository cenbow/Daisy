package com.yourong.core.msg.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.MessageTemplate;

public interface MessageTemplateManager {

	/**
	 * 查询所有消息模板
	 * @return
	 * @throws ManagerException
	 */
	public Page<MessageTemplate> queryMessageTemplate(Page<MessageTemplate> pageRequest, Map<String, Object> map) throws ManagerException;
	
	/**
	 * 根据编号获得消息模板
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public MessageTemplate getMessageTemplateById(Long id) throws ManagerException;
	
	/**
	 * 更新消息模板
	 * @param messageTemplate
	 */
	public void update(MessageTemplate messageTemplate) throws ManagerException;
	
	/**
	 * 
	 * @param templateCode
	 * @return
	 * @throws ManagerException
	 */
	public List<MessageTemplate> getMessageTemplateByCode(String templateCode) throws ManagerException;
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public int diabledMessageTemplate(Long id) throws ManagerException;
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public int enabledMessageTemplate(Long id) throws ManagerException;
	
	/**
	 * 保存邮件模板
	 * @param messageTemplate
	 */
	public void saveMailTemplate(MessageTemplate messageTemplate) throws ManagerException;
	
	/**
	 * 删除模板
	 * @param id
	 * @throws ManagerException
	 */
	public void delMsgTemplate(Long id) throws ManagerException;
	
}
