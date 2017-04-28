package com.yourong.core.bsc.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.BscAttachment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface BscAttachmentMapper {
    @Delete({
        "delete from bsc_attachment",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into bsc_attachment (id, module, ",
        "cat_id, file_name, ",
        "file_url, file_size, ",
        "file_ext, upload_time, ",
        "status, list_order, ",
        "file_desc, storage_way, ",
        "del_flag)",
        "values (#{id,jdbcType=BIGINT}, #{module,jdbcType=VARCHAR}, ",
        "#{catId,jdbcType=INTEGER}, #{fileName,jdbcType=VARCHAR}, ",
        "#{fileUrl,jdbcType=VARCHAR}, #{fileSize,jdbcType=BIGINT}, ",
        "#{fileExt,jdbcType=VARCHAR}, now(), ",
        "#{status,jdbcType=INTEGER}, #{listOrder,jdbcType=INTEGER}, ",
        "#{fileDesc,jdbcType=VARCHAR}, #{storageWay,jdbcType=VARCHAR}, ",
        "1)"
    })
    int insert(BscAttachment record);

    int insertSelective(BscAttachment record);

    @Select({
        "select",
        "id, module, cat_id, file_name, file_url, file_size, file_ext, upload_time, status, ",
        "list_order, file_desc, storage_way, del_flag",
        "from bsc_attachment",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    BscAttachment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BscAttachment record);

    @Update({
        "update bsc_attachment",
        "set module = #{module,jdbcType=VARCHAR},",
          "cat_id = #{catId,jdbcType=INTEGER},",
          "file_name = #{fileName,jdbcType=VARCHAR},",
          "file_url = #{fileUrl,jdbcType=VARCHAR},",
          "file_size = #{fileSize,jdbcType=BIGINT},",
          "file_ext = #{fileExt,jdbcType=VARCHAR},",
          "upload_time = #{uploadTime,jdbcType=TIMESTAMP},",
          "status = #{status,jdbcType=INTEGER},",
          "list_order = #{listOrder,jdbcType=INTEGER},",
          "file_desc = #{fileDesc,jdbcType=VARCHAR},",
          "storage_way = #{storageWay,jdbcType=VARCHAR},",
          "del_flag = #{delFlag,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(BscAttachment record);

    Page<BscAttachment> findByPage(Page<BscAttachment> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<BscAttachment> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    List<BscAttachment> findAttachmentsByKeyId(String keyId);
    
    List<BscAttachment> findAttachmentsByKeyIdAndModule(@Param("keyId")String keyId, @Param("module")String module, @Param("num")Integer num);
    
}