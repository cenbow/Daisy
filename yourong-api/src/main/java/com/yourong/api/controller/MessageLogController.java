package com.yourong.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.MessageLogService;
import com.yourong.common.util.StringUtil;
import com.yourong.core.msg.model.query.MessageLogQuery;

/**
 * 
 * @desc 站内信controller
 * @author wangyanji 2015年12月28日下午4:32:14
 */
@Controller
@RequestMapping("security/message")
public class MessageLogController extends BaseController {

	@Autowired
	private MessageLogService messageLogService;

	private static final Logger logger = LoggerFactory.getLogger(MessageLogController.class);

	/**
	 * 
	 * @Description:获取会员站内信
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月28日 下午5:07:21
	 */
	@RequestMapping(value = "queryMessage", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO<Object> queryMessageLogForPagin(HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		int pageSize = ServletRequestUtils.getIntParameter(req, "pageSize", 10);
		String notifyTypeIds = ServletRequestUtils.getStringParameter(req, "notifyTypeIds", "5");
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		if (StringUtil.isBlank(notifyTypeIds)) {
			rDTO.setIsError();
			return rDTO;
		}
		try {
			String[] arr = notifyTypeIds.split(",");
			int[] typeIds = new int[arr.length];
			for (int i = 0; i < arr.length; i++) {
				typeIds[i] = Integer.parseInt(arr[i]);
			}
			Long memberID = getMemberID(req);
			MessageLogQuery query = new MessageLogQuery();
			query.setCurrentPage(pageNo);
			query.setPageSize(pageSize);
			query.setMemberId(memberID);
			query.setMsgTypeIds(typeIds);
			int[] notifyType = {1,2,3,4};
			query.setNotifyTypeIds(notifyType);
			return messageLogService.queryMessageLogForPagin(query);
		} catch (Exception e) {
			logger.error("获取会员站内信失败,pageNo={},pageSize={},notifyTypeIds={}", pageNo, pageSize, notifyTypeIds, e);
			rDTO.setIsError();
			return rDTO;
		}
	}

	/**
	 * 
	 * @Description:更新单条消息状态
	 * @param req
	 * @param res
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月4日 下午4:22:13
	 */
	@RequestMapping(value = "updateMessageStatus", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO<Object> updateMessageStatus(HttpServletRequest req, HttpServletResponse res) {
		Long msgId = ServletRequestUtils.getLongParameter(req, "msgId", 0L);
		Long memberID = getMemberID(req);
		return messageLogService.updateMessageStatus(memberID, msgId);
	}

	/**
	 * 
	 * @Description:批量更新消息状态
	 * @param req
	 * @param res
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月4日 下午4:29:50
	 */
	@RequestMapping(value = "batchUpdateMessageStatus", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO<Object> batchUpdateMessageStatus(HttpServletRequest req, HttpServletResponse res) {
		String notifyTypeIds = ServletRequestUtils.getStringParameter(req, "notifyTypeIds", "1,2,3,4");
		int filterDate = ServletRequestUtils.getIntParameter(req, "filterDate", 0);
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		if (StringUtil.isBlank(notifyTypeIds)) {
			rDTO.setIsError();
			return rDTO;
		}
		try {
			String[] arr = notifyTypeIds.split(",");
			int[] typeIds = new int[arr.length];
			for (int i = 0; i < arr.length; i++) {
				typeIds[i] = Integer.parseInt(arr[i]);
			}
			Long memberID = getMemberID(req);
			MessageLogQuery query = new MessageLogQuery();
			query.setFilterDate(filterDate);
			query.setMemberId(memberID);
			query.setNotifyTypeIds(typeIds);
			return messageLogService.batchUpdateMessageStatus(query);
		} catch (Exception e) {
			logger.error("批量更新消息状态失败,notifyTypeIds={},filterDate={}", notifyTypeIds, filterDate, e);
			rDTO.setIsError();
			return rDTO;
		}
	}
}
