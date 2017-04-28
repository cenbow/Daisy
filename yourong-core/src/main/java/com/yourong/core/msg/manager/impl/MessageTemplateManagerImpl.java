package com.yourong.core.msg.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rop.thirdparty.com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.mail.SendMailService;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.msg.dao.MessageTemplateMapper;
import com.yourong.core.msg.manager.MessageTemplateManager;
import com.yourong.core.msg.model.MessageTemplate;

@Component
public class MessageTemplateManagerImpl implements MessageTemplateManager {

	@Autowired
	private MessageTemplateMapper messageTemplateMapper;
	
	@Autowired
	private SendMailService sendMailService;
	
	@Override
	public Page<MessageTemplate> queryMessageTemplate(Page<MessageTemplate> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = messageTemplateMapper.queryMessageTemplateTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<MessageTemplate> selectForPagin = Lists.newArrayList();
			if(totalCount > 0){
				selectForPagin = messageTemplateMapper.queryMessageTemplate(map);
			}
			pageRequest.setData(selectForPagin);
			return pageRequest;
		}catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public MessageTemplate getMessageTemplateById(Long id)
			throws ManagerException {
		return messageTemplateMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(MessageTemplate messageTemplate) throws ManagerException {
		messageTemplateMapper.updateByPrimaryKeySelective(messageTemplate);
	}

	@Override
	public List<MessageTemplate> getMessageTemplateByCode(String templateCode)
			throws ManagerException {
		return messageTemplateMapper.getMessageTemplateByCode(templateCode);
	}

	@Override
	public int diabledMessageTemplate(Long id) throws ManagerException {
		// TODO Auto-generated method stub
		return messageTemplateMapper.diabledMessageTemplate(id);
	}

	@Override
	public int enabledMessageTemplate(Long id) throws ManagerException {
		// TODO Auto-generated method stub
		return messageTemplateMapper.enabledMessageTemplate(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void saveMailTemplate(MessageTemplate messageTemplate)
			throws ManagerException {
		try{
			int num = 0;
			if(messageTemplate.getId() != null) {
				messageTemplate.setUpdateTime(DateUtils.getCurrentDate());
				num = messageTemplateMapper.updateByPrimaryKeySelective(messageTemplate);
			} else {
				messageTemplate.setTemplateCode(newCode());
				messageTemplate.setMsgType(2);
				messageTemplate.setStatus(1);
				messageTemplate.setDelFlag(1);
				messageTemplate.setCreateTime(DateUtils.getCurrentDate());
				messageTemplate.setUpdateTime(DateUtils.getCurrentDate());
				num = messageTemplateMapper.insertSelective(messageTemplate);
			}
			if(num != 1) {
				throw new ManagerException("邮件模板数据保存异常");
			} else {
				String retStr = null;
				if (messageTemplate.getId() != null) {
					retStr = sendMailService.sohuTemplateUpdate(
							messageTemplate.getTemplateCode(),
							messageTemplate.getTemplateName(),
							messageTemplate.getTemplateSubject(),
							messageTemplate.getTemplateType(),
							messageTemplate.getContent());
				} else {
					retStr = sendMailService.sohuTemplateAdd(
							messageTemplate.getTemplateCode(),
							messageTemplate.getTemplateName(),
							messageTemplate.getTemplateSubject(),
							messageTemplate.getTemplateType(),
							messageTemplate.getContent(), null);
				}
				Map<String, Object> callBackMap = JSON.parseObject(retStr, java.util.HashMap.class);
				if(!"success".equals(callBackMap.get("message"))) {
					throw new ManagerException("邮件模板保存失败，" + retStr);
				}
			}
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 获取最新的模板code
	 * @return
	 */
	private String newCode() {
		MessageTemplate lastRecord = messageTemplateMapper.selectLastRecord();
		if(lastRecord != null) {
			String code = lastRecord.getTemplateCode();
			while(code.startsWith("0")) {
				code = code.replaceFirst("0", "");
			}
			String newCode = Integer.parseInt(code) + 1 + "";
			if(newCode.length() < 3) {
				int position = 3-newCode.length();
				for(int i=0; i<position; i++) {
					newCode = "0" + newCode;
				}
			}
			return newCode;
		} 
		return "001";
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void delMsgTemplate(Long id) throws ManagerException {
		try{
			MessageTemplate model = new MessageTemplate();
			model.setId(id);
			model.setDelFlag(-1);
			int retNum = messageTemplateMapper.updateByPrimaryKeySelective(model);
			if(retNum == 1) {
				MessageTemplate record = messageTemplateMapper.selectByPrimaryKey(id);
				if(record != null) {
					String retStr = sendMailService.sohuTemplateDelete(record.getTemplateCode());
					Map<String, Object> callBackMap = JSON.parseObject(retStr, java.util.HashMap.class);
					if(!"success".equals(callBackMap.get("message"))) {
						throw new ManagerException("邮件模板删除失败，" + retStr);
					}
				}
			}
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}
}
