package com.yourong.core.cms.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsComment;

public interface CmsCommentMapper {
    @Delete({
        "delete from cms_comment",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cms_comment (id, category_id, ",
        "article_id, title, ",
        "content, name, ip, ",
        "audit_user_id, audit_time, ",
        "create_time, del_flag)",
        "values (#{id,jdbcType=BIGINT}, #{categoryId,jdbcType=BIGINT}, ",
        "#{articleId,jdbcType=BIGINT}, #{title,jdbcType=VARCHAR}, ",
        "#{content,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{ip,jdbcType=VARCHAR}, ",
        "#{auditUserId,jdbcType=BIGINT}, #{auditTime,jdbcType=TIMESTAMP}, ",
        "#{createTime,jdbcType=TIMESTAMP}, #{delFlag,jdbcType=INTEGER})"
    })
    int insert(CmsComment record);

    int insertSelective(CmsComment record);

    @Select({
        "select",
        "id, category_id, article_id, title, content, name, ip, audit_user_id, audit_time, ",
        "create_time, del_flag",
        "from cms_comment",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    CmsComment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CmsComment record);

    @Update({
        "update cms_comment",
        "set category_id = #{categoryId,jdbcType=BIGINT},",
          "article_id = #{articleId,jdbcType=BIGINT},",
          "title = #{title,jdbcType=VARCHAR},",
          "content = #{content,jdbcType=VARCHAR},",
          "name = #{name,jdbcType=VARCHAR},",
          "ip = #{ip,jdbcType=VARCHAR},",
          "audit_user_id = #{auditUserId,jdbcType=BIGINT},",
          "audit_time = #{auditTime,jdbcType=TIMESTAMP},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "del_flag = #{delFlag,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(CmsComment record);

    Page<CmsComment> findByPage(Page<CmsComment> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") long[] ids);
}