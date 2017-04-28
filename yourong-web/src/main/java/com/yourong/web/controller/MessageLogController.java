package com.yourong.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.biz.MessageLogForMember;
import com.yourong.core.msg.model.query.MessageLogQuery;
import com.yourong.web.service.MessageLogService;

@Controller
@RequestMapping("message")
public class MessageLogController extends BaseController {

	@Autowired
	private MessageLogService messageLogService;

	/**
	 * 查询用户站内信
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "queryMessage")
	@ResponseBody
	public Object queryMessageLogForPagin(HttpServletRequest req, HttpServletResponse res) {
		MessageLogQuery messageLogQuery = getMessageLogQuery(req);
		Page<MessageLogForMember> log = messageLogService.queryMessageLogForPagin(messageLogQuery);
		return log;
	}

	/**
	 * 批量更新消息状态
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "batchUpdateMessageStatus")
	@ResponseBody
	public ResultDO batchUpdateMessageStatus(HttpServletRequest req, HttpServletResponse res) {
		MessageLogQuery messageLogQuery = getMessageLogQuery(req);
		return messageLogService.batchUpdateMessageStatus(messageLogQuery);
	}

	/**
	 * 更新单条消息状态
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "updateMessageStatus")
	@ResponseBody
	public ResultDO updateMessageStatus(HttpServletRequest req, HttpServletResponse res) {
		Long memberId = getMember().getId();
		Long msgId = ServletRequestUtils.getLongParameter(req, "msgId", 0L);
		return messageLogService.updateMessageStatus(memberId, msgId);
	}

	/**
	 * 获得未读消息数量
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "unreadMessage")
	@ResponseBody
	public ResultDO countUnreadMessage(HttpServletRequest req, HttpServletResponse res) {
		ResultDO result = new ResultDO();
		Long memberId = getMember().getId();
		int count = messageLogService.countUnreadMessage(memberId);
		result.setResult(count);
		return result;
	}

	private MessageLogQuery getMessageLogQuery(HttpServletRequest req) {
		MessageLogQuery query = new MessageLogQuery();
		int filterDate = ServletRequestUtils.getIntParameter(req, "filterDate", 0);
		int notifyTypeIds[] = ServletRequestUtils.getIntParameters(req, "notifyTypeIds");
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		Long memberId = getMember().getId();
		query.setMemberId(memberId);
		query.setNotifyTypeIds(notifyTypeIds);
		query.setFilterDate(filterDate);
		query.setCurrentPage(pageNo);
		int[] msgType = {1,3};
		query.setMsgTypeIds(msgType);
		return query;
	}
}
