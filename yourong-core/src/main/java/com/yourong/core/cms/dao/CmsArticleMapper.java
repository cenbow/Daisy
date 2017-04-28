package com.yourong.core.cms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.query.CmsArticleQuery;

@CacheNamespace(implementation=com.yourong.common.cache.MybatisRedisCache.class)
public interface CmsArticleMapper {
    @Delete({
        "delete from cms_article",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into cms_article (id, category_id, ",
        "title, link, color, ",
        "image, keywords, ",
        "description, weight, ",
        "weight_time, hits, ",
        "posid, copyfrom, ",
        "relation, allow_comment, ",
        "remarks, create_by, ",
        "create_time, update_by, ",
        "update_time, del_flag,publish_state,online_time,end_time, ",
        "content)",
        "values (#{id,jdbcType=BIGINT}, #{categoryId,jdbcType=INTEGER}, ",
        "#{title,jdbcType=VARCHAR}, #{link,jdbcType=VARCHAR}, #{color,jdbcType=VARCHAR}, ",
        "#{image,jdbcType=VARCHAR}, #{keywords,jdbcType=VARCHAR}, ",
        "#{description,jdbcType=VARCHAR}, #{weight,jdbcType=INTEGER}, ",
        "#{weightTime,jdbcType=TIMESTAMP}, #{hits,jdbcType=INTEGER}, ",
        "#{posid,jdbcType=VARCHAR}, #{copyfrom,jdbcType=VARCHAR}, ",
        "#{relation,jdbcType=VARCHAR}, #{allowComment,jdbcType=INTEGER}, ",
        "#{remarks,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, ",
        "now(), #{updateBy,jdbcType=VARCHAR}, ",
        "now(), 1, #{publishState,jdbcType=INTEGER}, #{onlineTime,jdbcType=TIMESTAMP},#{endTime,jdbcType=TIMESTAMP}, ",
        "#{content,jdbcType=LONGVARBINARY})"
    })
    @Options(flushCache=true)
    int insert(CmsArticle record);

    int insertCmsArticle(CmsArticle record);

    @Options(flushCache=true)
    int insertSelective(CmsArticle record);

    @Select({
        "select",
        "id, category_id, genre, title, link, color, image, chosen_image, keywords, description, weight, weight_time, ",
        "hits, posid, copyfrom, relation, allow_comment, remarks, create_by, create_time, ",
        "update_by, update_time, del_flag, content,online_time,publish_state",
        "from cms_article",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("ResultMapWithBLOBs")
    CmsArticle selectByPrimaryKey(Long id);
    
    // 根据栏目ID查找文章
    @Select({
    	"select",
    	"id, category_id, title, link, color, image, keywords, description, weight, weight_time, ",
    	"hits, posid, copyfrom, relation, allow_comment, remarks, create_by, create_time, ",
    	"update_by, update_time, del_flag, content,online_time,publish_state",
    	"from cms_article",
    	"where category_id = #{categoryId,jdbcType=INTEGER} and  del_flag = 1"
    })
    @ResultMap("ResultMapWithBLOBs")
    List<CmsArticle> selectByCategoryId(Long categoryId);

    @Options(flushCache=true)
    int updateByPrimaryKeySelective(CmsArticle record);

    @Update({
        "update cms_article",
        "set category_id = #{categoryId,jdbcType=INTEGER},",
          "title = #{title,jdbcType=VARCHAR},",
          "link = #{link,jdbcType=VARCHAR},",
          "color = #{color,jdbcType=VARCHAR},",
          "image = #{image,jdbcType=VARCHAR},",
          "keywords = #{keywords,jdbcType=VARCHAR},",
          "description = #{description,jdbcType=VARCHAR},",
          "weight = #{weight,jdbcType=INTEGER},",
          "weight_time = #{weightTime,jdbcType=TIMESTAMP},",
          "hits = #{hits,jdbcType=INTEGER},",
          "posid = #{posid,jdbcType=VARCHAR},",
          "copyfrom = #{copyfrom,jdbcType=VARCHAR},",
          "relation = #{relation,jdbcType=VARCHAR},",
          "allow_comment = #{allowComment,jdbcType=INTEGER},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "create_by = #{createBy,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_by = #{updateBy,jdbcType=VARCHAR},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "del_flag = #{delFlag,jdbcType=INTEGER},",
          "online_time = #{onlineTime,jdbcType=TIMESTAMP},",
          "publish_state = #{publishState,jdbcType=INTEGER},",
          "content = #{content,jdbcType=LONGVARBINARY}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int updateByPrimaryKeyWithBLOBs(CmsArticle record);

    @Update({
        "update cms_article",
        "set category_id = #{categoryId,jdbcType=INTEGER},",
          "title = #{title,jdbcType=VARCHAR},",
          "link = #{link,jdbcType=VARCHAR},",
          "color = #{color,jdbcType=VARCHAR},",
          "image = #{image,jdbcType=VARCHAR},",
          "keywords = #{keywords,jdbcType=VARCHAR},",
          "description = #{description,jdbcType=VARCHAR},",
          "weight = #{weight,jdbcType=INTEGER},",
          "weight_time = #{weightTime,jdbcType=TIMESTAMP},",
          "hits = #{hits,jdbcType=INTEGER},",
          "posid = #{posid,jdbcType=VARCHAR},",
          "copyfrom = #{copyfrom,jdbcType=VARCHAR},",
          "relation = #{relation,jdbcType=VARCHAR},",
          "allow_comment = #{allowComment,jdbcType=INTEGER},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "create_by = #{createBy,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_by = #{updateBy,jdbcType=VARCHAR},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "online_time = #{onlineTime,jdbcType=TIMESTAMP},",
          "publish_state = #{publishState,jdbcType=INTEGER},",
          "del_flag = #{delFlag,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int updateByPrimaryKey(CmsArticle record);

    Page<CmsArticle> findByPage(Page<CmsArticle> pageRequest, @Param("map") Map<String,Object> map);

    @Options(flushCache=true)
    int batchDelete(@Param("ids") long[] ids);
    
    /****=============================前台分页==start========================================*/
    List<CmsArticle> selectForPaginFront(@Param("cmsArticleQuery") CmsArticleQuery cmsArticleQuery);
    
    int selectForPaginTotalCountFront(@Param("cmsArticleQuery") CmsArticleQuery cmsArticleQuery);
    /****=============================前台分页==end==========================================*/
    
    @Options(flushCache=true)
    List<CmsArticle> selectHomeNotice(@Param("map") Map<String,Object> map);
    
    CmsArticle selectArticleByIdAndCategoryId(@Param("map") Map<String,Object> map);
    
    @Options(flushCache=true)
    CmsArticle selectNoticeArticle();
    
    /*文章 前一条*/
    Long selectPreArticle(@Param("map") Map<String,Object> map);
    
    /*文章 后一条*/
    Long selectNextArticle(@Param("map") Map<String,Object> map);
    /*根据上线时间，更改文章发布状态*/
    @Options(flushCache=true)
    int updateArticlePubState();
    
    List<CmsArticle> selectListByMap(@Param("map") Map<String,Object> map);

	List<CmsArticle> selectHomeArticle(@Param("map") Map<String,Object> map);

	List<CmsArticle> selectHomeArticleByCategoryId(@Param("map") Map<String,Object> map);

	int selectArticles(Integer newsId);

    int updateCmsArticleAttachments(@Param("id") Long id,@Param("image") String image,@Param("chosenImage") String chosenImage);
    
}