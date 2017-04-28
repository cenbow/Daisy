package com.yourong.backend.msc.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.MessageTemplate;

public interface MessageTemplateService {
	
	/**
	 * 查询所有消息模板
	 * @return
	 */
	public Page<MessageTemplate> queryMessageTemplate(Page<MessageTemplate> pageRequest, Map<String, Object> map);
	
	/**
	 * 根据编号获得消息模板
	 * @param id
	 * @return
	 */
	public MessageTemplate getMessageTemplateById(Long id);
	
	/**
	 * 更新消息模板
	 * @param messageTemplate
	 * @return
	 */
	public ResultDO update(MessageTemplate messageTemplate);
	
	/**
	 * 
	 * @param templateCode
	 * @return
	 */
	public List<MessageTemplate> getMessageTemplateByCode(String templateCode);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public ResultDO diabledMessageTemplate(Long id);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public ResultDO enabledMessageTemplate(Long id);
	
	/**
	 * 保存邮件模板
	 * @param messageTemplate
	 * @return
	 */
	public ResultDO saveMailTemplate(MessageTemplate messageTemplate);
	
	/**
	 * 删除模板
	 * @param id
	 * @return
	 */
	public ResultDO delMsgTemplate(Long id);
}
