package com.yourong.backend.msc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.msc.service.MessageTemplateService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.CustomMessage;
import com.yourong.core.msg.model.MessageTemplate;

@Controller
@RequestMapping("msgTemplate")
public class MessageTemplateController extends BaseController{
	
	@Autowired
	private MessageTemplateService messageTemplateService;
	
	@RequestMapping(value = "index")
	public String msgTemplate(HttpServletRequest req, HttpServletResponse resp){
		return "/msg/template/index";
	}
	
	@RequestMapping(value = "ajax")
	@ResponseBody
	public Object queryMessageTemplate(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Page<MessageTemplate> pageRequest = new Page<MessageTemplate>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		return messageTemplateService.queryMessageTemplate(pageRequest, map);
	}
	
	@RequestMapping(value = "find")
	public String findTemplate(HttpServletRequest req, HttpServletResponse resp){
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		req.setAttribute("messageTemplate", messageTemplateService.getMessageTemplateById(id));
		return "/msg/template/update";
	}
	
	@RequestMapping(value = "update")
	@ResponseBody
	public ResultDO updateTemplate(@ModelAttribute MessageTemplate messageTemplate, HttpServletRequest req, HttpServletResponse resp){
		return messageTemplateService.update(messageTemplate);
	}
	
	@RequestMapping(value = "del")
	@ResponseBody
	public ResultDO delTemplate(HttpServletRequest req, HttpServletResponse resp){
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		return messageTemplateService.delMsgTemplate(id);
	}
	
	@RequestMapping(value = "updateStatus")
	@ResponseBody
	public ResultDO updateTemplateStatus(HttpServletRequest req, HttpServletResponse resp){
		int status = ServletRequestUtils.getIntParameter(req, "status", 0);
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		if(status==0){
			return messageTemplateService.diabledMessageTemplate(id);
		}
		return messageTemplateService.enabledMessageTemplate(id);
	}
	
	@RequestMapping(value = "eidtEmailTemplate")
	public String addEmailTemplate(HttpServletRequest req, HttpServletResponse resp){
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		if(id > 0) {
			req.setAttribute("messageTemplate", messageTemplateService.getMessageTemplateById(id));
		}
		return "/msg/template/emailTemplate";
	}
	
	@RequestMapping(value = "saveMailTemplate")
	@ResponseBody
	public ResultDO saveMailTemplate(@ModelAttribute MessageTemplate messageTemplate, HttpServletRequest req, HttpServletResponse resp){
		return messageTemplateService.saveMailTemplate(messageTemplate);
	}
}
