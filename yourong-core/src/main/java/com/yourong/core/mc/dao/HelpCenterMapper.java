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
import com.yourong.core.mc.model.HelpCenterQuestion;
import com.yourong.core.mc.model.HelpCenterQuestionShow;

public interface HelpCenterMapper {
	
    @Delete({
        "delete from mc_help_question",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into mc_help_question (sort, ",
        "label_id,question_order,content,answer, ",
        "values (#{sort,jdbcType=INTEGER},",
        "#{labelId,jdbcType=BIGINT}, #{questionOrder,jdbcType=VARCHAR},#{content,jdbcType=VARCHAR},",
        "#{answer,jdbcType=VARCHAR})"
    })
    int insert(HelpCenterQuestion record);

    @Select({
        "select",
        "id, sort, terminal, label_id, question_order,content, answer, create_time, update_time,is_hot",
        "from mc_help_question",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    HelpCenterQuestion selectByPrimaryKey(Long id);
    
    

    @Update({
        "update mc_help_question",
        "set sort = #{sort,jdbcType=INTEGER},",
          "terminal = #{terminal,jdbcType=INTEGER},",
          "label_id = #{labelId,jdbcType=BIGINT},",
          "question_order = #{questionOrder,jdbcType=VARCHAR},",
          "content = #{content,jdbcType=VARCHAR},",
          "answer = #{answer,jdbcType=VARCHAR},",
          "create_time = #{createDate,jdbcType=TIMESTAMP},",
          "update_time = #{updateDate,jdbcType=TIMESTAMP},",
          "is_hot = #{isHot,jdbcType=INTEGER},",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(HelpCenterQuestion record);

    Page<HelpCenterQuestion> findByPage(Page<HelpCenterQuestion> pageRequest, @Param("map") Map<String, Object> map);

    List<HelpCenterQuestion> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    int updateByPrimaryKeySelective(HelpCenterQuestion record);
    
    int deleteByHelpCenterQuestionId(Long id);
    
    int updateSortById(@Param("id")Long id, @Param("sort")Integer sort, @Param("date")Date date);
    
    int insertSingle(HelpCenterQuestion record);
    
    List<HelpCenterQuestionShow> selectHotShow(@Param("terminal")Integer terminal);
    
    List<HelpCenterQuestionShow> selectAnswerShow(@Param("terminal")Integer terminal);
    
    //List<HelpCenterQuestionShow> selectCommonShow(@Param("terminal")Integer terminal,@Param("category")String category);
    
    List<HelpCenterQuestionShow> selectCommonShowByLabel(@Param("terminal")Integer terminal,@Param("labelName")String labelName);

	List<HelpCenterQuestionShow> selectNewComerShow(@Param("terminal")Integer terminal);
    
}