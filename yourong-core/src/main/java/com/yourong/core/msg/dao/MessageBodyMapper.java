package com.yourong.core.msg.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.MessageBody;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface MessageBodyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MessageBody record);

    int insertSelective(MessageBody record);

    MessageBody selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MessageBody record);

    int updateByPrimaryKeyWithBLOBs(MessageBody record);

    int updateByPrimaryKey(MessageBody record);

    Page findByPage(Page pageRequest, @Param("map") Map map);

    int batchDelete(@Param("ids") int[] ids);

    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);
    
    
    public MessageBody getMessageBody(@Param("msgId")Long msgId, @Param("msgSource")Integer msgSource);
}