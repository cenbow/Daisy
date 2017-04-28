package com.yourong.core.msg.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.CustomMessage;

public interface CustomMessageMapper {
	int deleteByPrimaryKey(Long id);

	int insert(CustomMessage record);

	int insertSelective(CustomMessage record);

	CustomMessage selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(CustomMessage record);

	int updateByPrimaryKeyWithBLOBs(CustomMessage record);

	int updateByPrimaryKey(CustomMessage record);

	Page findByPage(Page pageRequest, @Param("map") Map map);

	int batchDelete(@Param("ids") int[] ids);

	List selectForPagin(@Param("map") Map map);

	int selectForPaginTotalCount(@Param("map") Map map);

	/**
	 * 把消息置为发布状态
	 * 
	 * @return
	 */
	public int updateMessageStatusTo4();

	/**
	 * 提交审核
	 * 
	 * @param id
	 * @return
	 */
	public int submitReview(@Param("id") Long id);

	/**
	 * 审核通过
	 * 
	 * @param id
	 * @param remarks
	 * @return
	 */
	public int approval(@Param("id") Long id, @Param("auditId") Long auditId,
			@Param("auditMessage") String auditMessage);

	/**
	 * 审核驳回
	 * 
	 * @param id
	 * @param remarks
	 * @return
	 */
	public int disallowance(@Param("id") Long id,
			@Param("auditId") Long auditId,
			@Param("auditMessage") String auditMessage);

	/**
	 * 查询用户未读消息
	 * 
	 * @param memberId
	 * @return
	 */
	public List<CustomMessage> queryUnreadMessages(
			@Param("memberId") Long memberId);
	
	/**
	 * 查询用户未读消息
	 * 
	 * @param memberId
	 * @return
	 */
	public List<CustomMessage> queryUnreadMessagesByType(
			@Param("memberId") Long memberId,@Param("msgType") Integer msgType);
	

	/**
	 * 查询未发送短信
	 * 
	 * @param memberId
	 * @return
	 */
	public List<CustomMessage> findUnsendMessage();

	/**
	 * 把短信置为发布状态
	 * 
	 * @return
	 */
	public int updateShortMessageStatusTo4(@Param("id") Long id);

	/**
	 * 取消发布
	 * 
	 * @param id
	 * @return
	 */
	public int cancel(@Param("id") Long id);
	
	/**
	 * 新注册用户发送短信后强制更新状态为待发布
	 * 
	 * @param id
	 * @return
	 */
	public int updateWaitStatus(@Param("id") Long id);
	
}