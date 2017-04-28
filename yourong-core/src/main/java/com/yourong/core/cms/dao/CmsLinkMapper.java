package com.yourong.core.cms.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsLink;

public interface CmsLinkMapper {
    @Delete({
        "delete from cms_link",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cms_link (id, category_id, ",
        "title, color, image, ",
        "href, weight, weight_time, ",
        "create_by, create_time, ",
        "update_by, update_time, ",
        "remarks, del_flag)",
        "values (#{id,jdbcType=BIGINT}, #{categoryId,jdbcType=BIGINT}, ",
        "#{title,jdbcType=VARCHAR}, #{color,jdbcType=VARCHAR}, #{image,jdbcType=VARCHAR}, ",
        "#{href,jdbcType=VARCHAR}, #{weight,jdbcType=INTEGER}, #{weightTime,jdbcType=TIMESTAMP}, ",
        "#{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, ",
        "#{remarks,jdbcType=VARCHAR}, #{delFlag,jdbcType=INTEGER})"
    })
    int insert(CmsLink record);

    int insertSelective(CmsLink record);

    @Select({
        "select",
        "id, category_id, title, color, image, href, weight, weight_time, create_by, ",
        "create_time, update_by, update_time, remarks, del_flag",
        "from cms_link",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    CmsLink selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsLink record);

    @Update({
        "update cms_link",
        "set category_id = #{categoryId,jdbcType=BIGINT},",
          "title = #{title,jdbcType=VARCHAR},",
          "color = #{color,jdbcType=VARCHAR},",
          "image = #{image,jdbcType=VARCHAR},",
          "href = #{href,jdbcType=VARCHAR},",
          "weight = #{weight,jdbcType=INTEGER},",
          "weight_time = #{weightTime,jdbcType=TIMESTAMP},",
          "create_by = #{createBy,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_by = #{updateBy,jdbcType=VARCHAR},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "del_flag = #{delFlag,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CmsLink record);

    Page<CmsLink> findByPage(Page<CmsLink> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") long[] ids);
}