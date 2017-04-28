package com.yourong.core.cms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsCategory;

public interface CmsCategoryMapper {
	@Delete({ "delete from cms_category", "where id = #{id,jdbcType=BIGINT}" })
	int deleteByPrimaryKey(Long id);

	@Insert({
			"insert into cms_category (parent_id, ",
			"module, name, image, ",
			"href, target, description, ",
			"keywords, sort, ",
			"in_menu, in_list, ",
			"allow_comment, is_audit, ",
			"create_time, update_time, ",
			"remarks, del_flag)",
			"values (#{parentId,jdbcType=VARCHAR}, ",
			"#{module,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{image,jdbcType=VARCHAR}, ",
			"#{href,jdbcType=VARCHAR}, #{target,jdbcType=VARCHAR}, #{description,jdbcType=VARCHAR}, ",
			"#{keywords,jdbcType=VARCHAR}, #{sort,jdbcType=INTEGER}, ",
			"#{inMenu,jdbcType=INTEGER}, #{inList,jdbcType=INTEGER}, ",
			"#{allowComment,jdbcType=INTEGER}, #{isAudit,jdbcType=INTEGER}, ",
			"now(), now(), ",
			"#{remarks,jdbcType=VARCHAR}, 1)" })
	int insert(CmsCategory record);

	int insertSelective(CmsCategory record);

	@Select({
			"select",
			"id, parent_id, module, name, image, href, target, description, keywords, sort, ",
			"in_menu, in_list, allow_comment, is_audit, create_time, update_time, remarks, ",
			"del_flag", "from cms_category",
			"where id = #{id,jdbcType=BIGINT}" })
	@ResultMap("BaseResultMap")
	CmsCategory selectByPrimaryKey(Long id);

	/** 根据id查询name */
	String selectNameById(Long id);

	int updateByPrimaryKeySelective(CmsCategory record);

	@Update({ "update cms_category",
			"set parent_id = #{parentId,jdbcType=VARCHAR},",
			"module = #{module,jdbcType=VARCHAR},",
			"name = #{name,jdbcType=VARCHAR},",
			"image = #{image,jdbcType=VARCHAR},",
			"href = #{href,jdbcType=VARCHAR},",
			"target = #{target,jdbcType=VARCHAR},",
			"description = #{description,jdbcType=VARCHAR},",
			"keywords = #{keywords,jdbcType=VARCHAR},",
			"sort = #{sort,jdbcType=INTEGER},",
			"in_menu = #{inMenu,jdbcType=INTEGER},",
			"in_list = #{inList,jdbcType=INTEGER},",
			"allow_comment = #{allowComment,jdbcType=INTEGER},",
			"is_audit = #{isAudit,jdbcType=INTEGER},",
			"create_time = #{createTime,jdbcType=TIMESTAMP},",
			"update_time = #{updateTime,jdbcType=TIMESTAMP},",
			"remarks = #{remarks,jdbcType=VARCHAR},",
			"del_flag = #{delFlag,jdbcType=INTEGER}",
			"where id = #{id,jdbcType=BIGINT}" })
	int updateByPrimaryKey(CmsCategory record);

	Page<CmsCategory> findByPage(Page<CmsCategory> pageRequest,
			@Param("map") Map<String, Object> map);

	int batchDelete(@Param("ids") long[] ids);

	List<CmsCategory> selectAllCmsCategory();
	
	Long  selectIdByName(String name);
}