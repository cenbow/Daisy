package com.yourong.core.ic.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectInterest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ProjectInterestMapper {
    @Delete({
        "delete from ic_project_interest",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into ic_project_interest (id, project_id, ",
        "min_invest, max_invest, ",
        "annualized_rate, gmt_created, ",
        "gmt_modified)",
        "values (#{id,jdbcType=BIGINT}, #{projectId,jdbcType=BIGINT}, ",
        "#{minInvest,jdbcType=VARCHAR}, #{maxInvest,jdbcType=VARCHAR}, ",
        "#{annualizedRate,jdbcType=DECIMAL}, #{gmtCreated,jdbcType=TIMESTAMP}, ",
        "#{gmtModified,jdbcType=TIMESTAMP})"
    })
    int insert(ProjectInterest record);

    int insertSelective(ProjectInterest record);

    @Select({
        "select",
        "id, project_id, min_invest, max_invest, annualized_rate, gmt_created, gmt_modified",
        "from ic_project_interest",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    ProjectInterest selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProjectInterest record);

    @Update({
        "update ic_project_interest",
        "set project_id = #{projectId,jdbcType=BIGINT},",
          "min_invest = #{minInvest,jdbcType=VARCHAR},",
          "max_invest = #{maxInvest,jdbcType=VARCHAR},",
          "annualized_rate = #{annualizedRate,jdbcType=DECIMAL},",
          "gmt_created = #{gmtCreated,jdbcType=TIMESTAMP},",
          "gmt_modified = #{gmtModified,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(ProjectInterest record);

    Page<ProjectInterest> findByPage(Page<ProjectInterest> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<ProjectInterest> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    int batchInsertProjectInterest(@Param("list") List<ProjectInterest> projectInterestList);
    
    @Delete({
        "delete from ic_project_interest",
        "where project_id = #{projectId,jdbcType=BIGINT}"
    })
    int deleteProjectInterestByProjectId(Long projectId);
    
    
    @Select({
    	" select id, project_id, min_invest, max_invest, annualized_rate from ic_project_interest",
    	" where project_id = #{projectId,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    public List<ProjectInterest> getProjectInterestByProjectId(@Param("projectId") Long projectId);
    
    @Select({
    	" select annualized_rate from ic_project_interest where min_invest <=#{investAmount,jdbcType=DECIMAL}  and max_invest >#{investAmount,jdbcType=DECIMAL} ",
    	 " and project_id=#{projectId,jdbcType=BIGINT} "
    })
    BigDecimal getAnnualizedRateByInvestAmount(@Param("investAmount")BigDecimal investAmount, @Param("projectId")Long projectId);
}