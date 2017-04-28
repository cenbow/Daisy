package com.yourong.backend.msc.service;

import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.CustomMessage;

public interface CustomMessageService {
	
	/**
	 * 保存消息
	 * @param customMessage
	 * @return
	 */
	public ResultDO save(CustomMessage customMessage);
	
	/**
	 * 根据编号获得消息
	 * @param id
	 * @return
	 */
	public CustomMessage getCustomMessageById(Long id);
	
	/**
	 * 删除消息
	 * @param id
	 * @return
	 */
	public ResultDO delete(Long id);
	
	/**
	 * 更新消息
	 * @param customMessage
	 * @return
	 */
	public ResultDO update(CustomMessage customMessage);
	
	/**
	 * 提交审核
	 * @param id
	 * @return
	 */
	public ResultDO submitReview(Long id);
	
	/**
	 * 审核
	 * @param id
	 * @param auditId
	 * @param isApprove
	 * @param auditMessage
	 * @return
	 */
	public ResultDO review(Long id, Long auditId, boolean isApprove, String auditMessage);
	
	/**
	 * 分页查询消息
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<CustomMessage> findByPage(Page<CustomMessage> pageRequest, Map<String, Object> map);
	
	/**
	 * 取消发布
	 * @param id
	 * @return
	 */
	public ResultDO cancel(Long id);

	/**
	 * 保存消息
	 * @param customMessage
	 * @return
	 */
	public Object saveShortMessage(CustomMessage customMessage);
	
	/**
	 * 保存app消息
	 * @param customMessage
	 * @return
	 */
	public Object saveAppMessage(CustomMessage customMessage);
	
}
