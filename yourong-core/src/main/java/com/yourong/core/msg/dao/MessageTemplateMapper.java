package com.yourong.core.msg.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.msg.model.MessageTemplate;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface MessageTemplateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MessageTemplate record);

    int insertSelective(MessageTemplate record);

    MessageTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MessageTemplate record);

    int updateByPrimaryKeyWithBLOBs(MessageTemplate record);

    int updateByPrimaryKey(MessageTemplate record);

    Page findByPage(Page pageRequest, @Param("map") Map map);

    int batchDelete(@Param("ids") int[] ids);

    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);
    
    public List queryMessageTemplate(@Param("map") Map map);
    
    public List<MessageTemplate> getMessageTemplateByCode(String templateCode);
    
    public int queryMessageTemplateTotalCount(@Param("map") Map map);
    
    /**
     * 
     * @param id
     * @return
     */
    public int diabledMessageTemplate(Long id);
    
    /**
     * 
     * @param id
     * @return
     */
    public int enabledMessageTemplate(Long id);
    
    /**
     * 获取最后一条数据
     * @return
     */
    MessageTemplate selectLastRecord();
    
}