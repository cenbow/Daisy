package com.yourong.backend.msc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.msc.service.CustomMessageService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.CustomMessage;
import com.yourong.core.sys.model.SysUser;

@Controller
@RequestMapping("message")
public class CustomMessageController extends BaseController {

	@Autowired
	private CustomMessageService customMessageService;

	/**
	 * 消息列表
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("message:querySiteMessageList")
	public String showMessageIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/msg/message/index";
	}

	/**
	 * 添加消息
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "add")
	@RequiresPermissions("message:addSiteMessage")
	public String addMessage(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("action", "add");
		return "/msg/message/add";
	}

	/**
	 * 添加消息
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "find")
	@RequiresPermissions("message:find")
	public String findMessage(HttpServletRequest req, HttpServletResponse resp) {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		int type = ServletRequestUtils.getIntParameter(req, "type", 0);
		req.setAttribute("customMessage",
				customMessageService.getCustomMessageById(id));
		req.setAttribute("action", "show");
		if (type == 1) {
			return "/msg/message/addShortMsg";
		}  else if (type==5){
			return "/msg/message/addAppMsg";
		} else if (type==4){
			return "/msg/message/addAppPush";
		} else {
			return "/msg/message/add";
		}

	}

	/**
	 * 添加消息
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "edit")
	@RequiresPermissions("message:edit")
	public String editMessage(HttpServletRequest req, HttpServletResponse resp) {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		int type = ServletRequestUtils.getIntParameter(req, "type", 0);
		req.setAttribute("customMessage",
				customMessageService.getCustomMessageById(id));
		req.setAttribute("action", "edit");
		if (type == 1) {
			return "/msg/message/addShortMsg";
		} else if (type==5){
			return "/msg/message/addAppMsg";
		} else if (type==4){
			return "/msg/message/addAppPush";
		} else {
			return "/msg/message/add";
		}

	}

	/**
	 * 提交审核
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "submitReview")
	@RequiresPermissions("message:waitReview")
	@ResponseBody
	public ResultDO submitReview(HttpServletRequest req,
			HttpServletResponse resp) {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		return customMessageService.submitReview(id);
	}

	/**
	 * 审核
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "review")
	@RequiresPermissions("message:review")
	@ResponseBody
	public ResultDO review(HttpServletRequest req, HttpServletResponse resp) {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		String msg = ServletRequestUtils.getStringParameter(req, "message", "");
		Integer radioStatus = ServletRequestUtils.getIntParameter(req,
				"radioStatus", 0);
		SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
		boolean isApprove = false;
		if (radioStatus > 0) {
			isApprove = true;
		}
		return customMessageService.review(id, sysUser.getId(), isApprove, msg);
	}

	/**
	 * 删除消息
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "delete")
	@RequiresPermissions("message:del")
	@ResponseBody
	public ResultDO deleteMessage(HttpServletRequest req,
			HttpServletResponse resp) {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		return customMessageService.delete(id);
	}

	/**
	 * 取消发布
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "cancel")
	@RequiresPermissions("message:cancel")
	@ResponseBody
	public ResultDO cancelMessage(HttpServletRequest req,
			HttpServletResponse resp) {
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		return customMessageService.cancel(id);
	}

	/**
	 * 保存消息
	 * 
	 * @param customMessage
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "save")
	@RequiresPermissions("message:addSiteMessage")
	@ResponseBody
	public ResultDO saveCustomMessage(
			@ModelAttribute CustomMessage customMessage,
			HttpServletRequest req, HttpServletResponse resp) {
		ResultDO result = new ResultDO();
		SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
		customMessage.setCreatorId(sysUser.getId());
		result = customMessageService.save(customMessage);
		return result;
	}

	/**
	 * 更新消息
	 * 
	 * @param customMessage
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "update")
	@RequiresPermissions("message:addSiteMessage")
	@ResponseBody
	public ResultDO updateCustomMessage(
			@ModelAttribute CustomMessage customMessage,
			HttpServletRequest req, HttpServletResponse resp) {
		SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
		customMessage.setCreatorId(sysUser.getId());
		ResultDO result = customMessageService.update(customMessage);
		return result;
	}

	/**
	 * 分页查询消息
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "ajax")
	@RequiresPermissions("message:querySiteMessageList")
	@ResponseBody
	public Object queryCustomMessage(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<CustomMessage> pageRequest = new Page<CustomMessage>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		return customMessageService.findByPage(pageRequest, map);
	}

	/**
	 * 添加短信
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "addShortMsg")
	@RequiresPermissions("message:addShortMsg")
	public String addShortMessage(HttpServletRequest req,
			HttpServletResponse resp) {
		req.setAttribute("action", "add");
		return "/msg/message/addShortMsg";
	}

	/**
	 * 保存短信
	 * 
	 * @param customMessage
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "saveShortMsg")
	@RequiresPermissions("message:saveShortMsg")
	@ResponseBody
	public Object saveShortMessage(@ModelAttribute CustomMessage customMessage,
			HttpServletRequest req, HttpServletResponse resp) {
		SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
		customMessage.setCreatorId(sysUser.getId());
		return customMessageService.saveShortMessage(customMessage);
	}
	
	

	/**
	 * 添加app消息
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "addAppMsg")
	@RequiresPermissions("message:addAppMsg")
	public String addAppMsg(HttpServletRequest req,
			HttpServletResponse resp) {
		req.setAttribute("action", "add");
		return "/msg/message/addAppMsg";
	}
	
	/**
	 * 保存app消息
	 * 
	 * @param customMessage
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "saveAppMsg")
	@RequiresPermissions("message:AppMsg")
	@ResponseBody
	public Object saveAppMsg(@ModelAttribute CustomMessage customMessage,
			HttpServletRequest req, HttpServletResponse resp) {
		SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
		customMessage.setCreatorId(sysUser.getId());
		return customMessageService.saveAppMessage(customMessage);
	}
	
	/**
	 * 添加app推送
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "addAppPush")
	@RequiresPermissions("message:addAppPush")
	public String addAppPush(HttpServletRequest req,
			HttpServletResponse resp) {
		req.setAttribute("action", "add");
		return "/msg/message/addAppPush";
	}
	
	/**
	 * 保存app推送
	 * 
	 * @param customMessage
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "saveAppPush")
	@RequiresPermissions("message:addAppPush")
	@ResponseBody
	public Object saveAppPush(@ModelAttribute CustomMessage customMessage,
			HttpServletRequest req, HttpServletResponse resp) {
		SysUser sysUser = SysServiceUtils.getCurrentLoginUserInfo();
		customMessage.setCreatorId(sysUser.getId());
		return customMessageService.saveAppMessage(customMessage);
	}
	
}
