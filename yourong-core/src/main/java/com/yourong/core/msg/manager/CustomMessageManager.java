package com.yourong.core.msg.manager;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.CustomMessage;

public interface CustomMessageManager {
	/**
	 * 添加消息
	 * @param customMessage
	 * @return
	 */
	public int insert(CustomMessage customMessage) throws ManagerException;
	
	/**
	 * 更新消息
	 * @param customMessage
	 */
	public void update(CustomMessage customMessage) throws ManagerException;
	
	/**
	 * 删除消息
	 * @param id
	 */
	public int delete(Long id) throws ManagerException;
	
	/**
	 * 根据编号获得消息
	 * @param id
	 */
	public CustomMessage select(Long id) throws ManagerException;
	
	/**
	 * 把消息置为发布状态
	 * @return
	 */
	public int updateMessageStatusTo4() throws ManagerException;
	
	/**
	 * 提交审核
	 * @param id
	 * @return
	 */
	public int submitReview(Long id) throws ManagerException;
	
	/**
	 * 审核通过
	 * @param id
	 * @param auditId
	 * @param auditMessage
	 * @return
	 */
	public int approval(Long id, Long auditId, String auditMessage) throws ManagerException;
	
	/**
	 * 审核驳回
	 * @param id
	 * @param auditId
	 * @param auditMessage
	 * @return
	 */
	public int disallowance(Long id, Long auditId, String auditMessage) throws ManagerException;
	
	/**
	 * 分页查询消息
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<CustomMessage> findByPage(Page<CustomMessage> pageRequest, Map<String, Object> map) throws ManagerException;
	
	/**
     * 查询用户未读消息
     * @param memberId
     * @return
     */
    public List<CustomMessage> queryUnreadMessages(Long memberId) throws ManagerException;
    
    
    public List<CustomMessage> queryUnreadMessagesByType(Long memberId,Integer msgType) throws ManagerException;
    
    /**
	 * 取消发布
	 * @param id
	 * @return
	 */
	public int cancel(Long id) throws ManagerException;

	/**
	 *	发送短信
	 */
	public boolean sendMessage(String mobilestr,String content);
}
