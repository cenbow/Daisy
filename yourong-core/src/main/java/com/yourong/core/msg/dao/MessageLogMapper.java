package com.yourong.core.msg.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.MessageLog;
import com.yourong.core.msg.model.biz.MessageLogForMember;
import com.yourong.core.msg.model.query.MessageLogQuery;

public interface MessageLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MessageLog record);

    int insertSelective(MessageLog record);

    MessageLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MessageLog record);

    int updateByPrimaryKey(MessageLog record);

    Page findByPage(Page pageRequest, @Param("map") Map map);

    int batchDelete(@Param("ids") int[] ids);

    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);
    
    /**
     * 分页查询消息
     * @param query
     * @return
     */
    public List<MessageLogForMember> queryMessageLogForPagin(MessageLogQuery messageLogQuery);
    
    /**
     * 分页查询消息
     * @param query
     * @return
     */
    public int queryMessageLogForPaginTotalCount(MessageLogQuery messageLogQuery);
    
    /**
     * 批量更新消息状态
     * @param query
     * @return
     */
    public int batchUpdateMessageStatus(MessageLogQuery messageLogQuery);
    
    /**
     * 更新单条消息状态
     * @param memberId
     * @param messageId
     * @return
     */
    public int updateMessageStatus(@Param("memberId")Long memberId, @Param("messageId")Long messageId);
    
    /**
     * 统计未读消息总数
     * @param memberId
     * @return
     */
    public int countUnreadMessage(Long memberId);
    
    
    public int updateMessageStatusByMemberId(@Param("memberId")Long memberId, @Param("msgType")Integer msgType);
    
    
    /**
     * 统计未读消息总数
     * @param memberId
     * @return
     */
    public int countUnreadMessageByType(@Param("memberId")Long memberId,@Param("msgType")Integer msgType);
    
}