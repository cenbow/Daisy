package com.yourong.backend.msc.service.impl;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.yourong.backend.msc.service.CustomMessageService;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.msg.manager.CustomMessageManager;
import com.yourong.core.msg.model.CustomMessage;

@Service
public class CustomMessageServiceImpl implements CustomMessageService {
	private static Logger logger = LoggerFactory
			.getLogger(CustomMessageServiceImpl.class);

	@Autowired
	private TaskExecutor threadPool;

	@Autowired
	private CustomMessageManager customMessageManager;

	@Override
	public ResultDO save(CustomMessage customMessage) {
		ResultDO result = new ResultDO();
		try {
			Date currentDate = DateUtils.getCurrentDate();
			customMessage.setUpdateTime(currentDate);
			customMessage.setCreateTime(currentDate);
			customMessage.setDelFlag(1);
			customMessage.setStatus(1);
			customMessageManager.insert(customMessage);
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("保存消息异常", e);
		}
		return result;
	}

	@Override
	public CustomMessage getCustomMessageById(Long id) {
		try {
			return customMessageManager.select(id);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResultDO delete(Long id) {
		ResultDO result = new ResultDO();
		try {
			int num = customMessageManager.delete(id);
			if (num <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ResultDO update(CustomMessage customMessage) {
		ResultDO result = new ResultDO();
		try {
			if (customMessage.getId() != null && customMessage.getId() > 0) {
				Date currentDate = DateUtils.getCurrentDate();
				customMessage.setUpdateTime(currentDate);
				if (customMessage.getRegisterEndTime() != null) {
					customMessage.setInvalidDate(customMessage.getRegisterEndTime());
				}
				customMessageManager.update(customMessage);
				result.setSuccess(true);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ResultDO submitReview(Long id) {
		ResultDO result = new ResultDO();
		try {
			int num = customMessageManager.submitReview(id);
			if (num <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ResultDO review(Long id, Long auditId, boolean isApprove,
			String auditMessage) {
		ResultDO result = new ResultDO();
		try {
			int num = 0;
			if (isApprove) {
				num = customMessageManager.approval(id, auditId, auditMessage);
			} else {
				num = customMessageManager.disallowance(id, auditId,
						auditMessage);
			}
			if (num <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Page<CustomMessage> findByPage(Page<CustomMessage> pageRequest,
			Map<String, Object> map) {
		try {
			return customMessageManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询消息异常", e);
		}
		return null;
	}

	@Override
	public ResultDO cancel(Long id) {
		ResultDO result = new ResultDO();
		try {
			int num = customMessageManager.cancel(id);
			if (num <= 0) {
				result.setSuccess(false);
			}
		} catch (ManagerException e) {
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Object saveShortMessage(CustomMessage customMessage) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			Date currentDate = DateUtils.getCurrentDate();
			// customMessage.setSendDate(currentDate);
			customMessage.setUpdateTime(currentDate);
			customMessage.setCreateTime(currentDate);
			customMessage.setDelFlag(1);
			customMessage.setStatus(1);
			customMessage.setNotifyType(Constant.MSG_NOTIFY_TYPE_SYSTEM);
			if (customMessage.getRegisterEndTime() != null) {
				customMessage.setInvalidDate(customMessage.getRegisterEndTime());
			}
			customMessageManager.insert(customMessage);
			// threadPool.execute(new
			// ShortMessageNoticeThread(customMessage.getContent()));
		} catch (ManagerException e) {
			rDO.setSuccess(false);
			logger.error("保存短信异常", e);
		}
		return rDO;
	}

	private class ShortMessageNoticeThread implements Runnable {

		private String content;

		public ShortMessageNoticeThread(String content) {
			this.content = content;
		}

		@Override
		public void run() {
			sendShortMessageNotice();
		}

		public void sendShortMessageNotice() {
			MessageClient.sendShortMessageNotice(content);
		}
	}
	
	@Override
	public Object saveAppMessage(CustomMessage customMessage) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			Date currentDate = DateUtils.getCurrentDate();
			// customMessage.setSendDate(currentDate);
			customMessage.setUpdateTime(currentDate);
			customMessage.setCreateTime(currentDate);
			customMessage.setDelFlag(1);
			customMessage.setStatus(1);
			customMessage.setNotifyType(Constant.MSG_NOTIFY_TYPE_SYSTEM);
			customMessageManager.insert(customMessage);
			// threadPool.execute(new
			// ShortMessageNoticeThread(customMessage.getContent()));
		} catch (ManagerException e) {
			rDO.setSuccess(false);
			logger.error("保存短信异常", e);
		}
		return rDO;
	}
}
