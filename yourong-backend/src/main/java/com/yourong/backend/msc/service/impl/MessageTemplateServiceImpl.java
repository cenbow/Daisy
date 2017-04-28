package com.yourong.backend.msc.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.msc.service.MessageTemplateService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.manager.MessageTemplateManager;
import com.yourong.core.msg.model.MessageTemplate;

@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {
	
	@Autowired
	private MessageTemplateManager messageTemplateManager;

	@Override
	public Page<MessageTemplate> queryMessageTemplate(Page<MessageTemplate> pageRequest, Map<String, Object> map) {
		try {
			return messageTemplateManager.queryMessageTemplate(pageRequest, map);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return pageRequest;
	}

	@Override
	public MessageTemplate getMessageTemplateById(Long id) {
		try {
			return messageTemplateManager.getMessageTemplateById(id);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResultDO update(MessageTemplate messageTemplate) {
		ResultDO result = new ResultDO();
		try {
			messageTemplateManager.update(messageTemplate);
			result.setSuccess(true);
		} catch (ManagerException e) {
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<MessageTemplate> getMessageTemplateByCode(String templateCode) {
		try {
			return messageTemplateManager.getMessageTemplateByCode(templateCode);
		} catch (ManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResultDO diabledMessageTemplate(Long id) {
		ResultDO result = new ResultDO();
		try {
			messageTemplateManager.diabledMessageTemplate(id);
			result.setSuccess(true);
		} catch (ManagerException e) {
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ResultDO enabledMessageTemplate(Long id) {
		ResultDO result = new ResultDO();
		try {
			messageTemplateManager.enabledMessageTemplate(id);
			result.setSuccess(true);
		} catch (ManagerException e) {
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ResultDO saveMailTemplate(MessageTemplate messageTemplate) {
		ResultDO result = new ResultDO();
		try {
			messageTemplateManager.saveMailTemplate(messageTemplate);
			result.setSuccess(true);
		} catch (ManagerException e) {
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ResultDO delMsgTemplate(Long id) {
		ResultDO result = new ResultDO();
		try {
			messageTemplateManager.delMsgTemplate(id);
			result.setSuccess(true);
		} catch (ManagerException e) {
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

}
