package com.yourong.core.bsc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.AttachmentIndex;

public interface AttachmentIndexMapper {
    @Delete({
        "delete from bsc_attachment_index",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into bsc_attachment_index (id, attachment_id, ",
        "key_id, create_time)",
        "values (#{id,jdbcType=BIGINT}, #{attachmentId,jdbcType=BIGINT}, ",
        "#{keyId,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP})"
    })
    int insert(AttachmentIndex record);

    int insertSelective(AttachmentIndex record);

    @Select({
        "select",
        "id, attachment_id, key_id, create_time",
        "from bsc_attachment_index",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    AttachmentIndex selectByPrimaryKey(Long id);
    
    @Select({
        "select",
        "id, attachment_id, key_id, create_time",
        "from bsc_attachment_index",
        "where key_id = #{keyId,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    AttachmentIndex selectByKey(Long keyId);

    int updateByPrimaryKeySelective(AttachmentIndex record);

    @Update({
        "update bsc_attachment_index",
        "set attachment_id = #{attachmentId,jdbcType=BIGINT},",
          "key_id = #{keyId,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(AttachmentIndex record);

    Page findByPage(Page pageRequest, @Param("map") Map map);

    int batchDelete(@Param("ids") int[] ids);

    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);

    int batchInsertAttachmentIndex(@Param("list") List<AttachmentIndex> list);
    
    @Delete({
        "delete from bsc_attachment_index",
        "where key_id = #{keyId,jdbcType=VARCHAR}"
    })
    int deleteAttachmentIndexByKeyId(@Param("keyId") String keyId);
    
    Map<String, Object> queryAttachmentInfoByIndex(@Param("keyId")Long keyId, @Param("module")String module);
}