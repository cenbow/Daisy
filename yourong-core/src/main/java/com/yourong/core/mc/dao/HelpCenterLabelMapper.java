package com.yourong.core.mc.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.HelpCenterLabel;

public interface HelpCenterLabelMapper {
	
    @Delete({
        "delete from mc_help_label",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into mc_help_label (label_name,",
        "sort,category,question_type,create_time",
        "values (#{labelName,jdbcType=VARCHAR},",
        "#{sort,jdbcType=INTEGER}, #{category,jdbcType=VARCHAR},",
        "#{questionType,jdbcType=VARCHAR},now())"
    })
    int insert(HelpCenterLabel record);

    @Select({
        "select",
        "id, label_name, sort, category, question_type, create_time",
        "from mc_help_label",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    HelpCenterLabel selectByPrimaryKey(Long id);
    
    

    @Update({
        "update mc_help_label",
        "set label_name = #{labelName,jdbcType=VARCHAR},",
          "sort = #{terminal,jdbcType=INTEGER},",
          "category = #{labelId,jdbcType=VARCHAR},",
          "question_type = #{content,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(HelpCenterLabel record);

    Page<HelpCenterLabel> findByPage(Page<HelpCenterLabel> pageRequest, @Param("map") Map<String, Object> map);

    List<HelpCenterLabel> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    int updateByPrimaryKeySelective(HelpCenterLabel record);
    
    int deleteByHelpCenterLabelId(Long id);
    
    int updateSortById(@Param("id")Long id, @Param("sort")Integer sort, @Param("date")Date date);
    
    int insertSingle(HelpCenterLabel record);
    
    List<HelpCenterLabel> selectByCategory(@Param("category")String category);

}