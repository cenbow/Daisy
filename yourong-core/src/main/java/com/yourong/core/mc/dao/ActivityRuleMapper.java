package com.yourong.core.mc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.ActivityRule;

public interface ActivityRuleMapper {
	@Delete({ "delete from mc_activity_rule", "where id = #{id,jdbcType=BIGINT}" })
	int deleteByPrimaryKey(Long id);

	@Insert({ "insert into mc_activity_rule (id, activity_id, ", "description, rule_type, ", "rule_parameter, version, ",
			"remarks, create_time)", "values (#{id,jdbcType=BIGINT}, #{activityId,jdbcType=BIGINT}, ",
			"#{description,jdbcType=VARCHAR}, #{ruleType,jdbcType=VARCHAR}, ",
			"#{ruleParameter,jdbcType=VARCHAR}, #{version,jdbcType=VARCHAR}, ",
			"#{remarks,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP})" })
	int insert(ActivityRule record);

	int insertSelective(ActivityRule record);

	@Select({ "select", "id, activity_id, description, rule_type, rule_parameter, version, remarks, create_time", "from mc_activity_rule",
			"where id = #{id,jdbcType=BIGINT}" })
	@ResultMap("BaseResultMap")
	ActivityRule selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ActivityRule record);

	@Update({ "update mc_activity_rule", "set activity_id = #{activityId,jdbcType=BIGINT},",
			"description = #{description,jdbcType=VARCHAR},", "rule_type = #{ruleType,jdbcType=VARCHAR},",
			"rule_parameter = #{ruleParameter,jdbcType=VARCHAR},", "version = #{version,jdbcType=VARCHAR},",
			"remarks = #{remarks,jdbcType=VARCHAR},", "create_time = #{createTime,jdbcType=TIMESTAMP}", "where id = #{id,jdbcType=BIGINT}" })
	int updateByPrimaryKey(ActivityRule record);

	Page<ActivityRule> findByPage(Page<ActivityRule> pageRequest, @Param("map") Map<String, Object> map);

	int batchDelete(@Param("ids") int[] ids);

	List<ActivityRule> selectForPagin(@Param("map") Map<String, Object> map);

	int selectForPaginTotalCount(@Param("map") Map<String, Object> map);

	/**
	 * 根据活动ID获取活动规则（一对一）
	 * 
	 * @param activityId
	 * @return
	 */
	ActivityRule findRuleByActivityId(Long activityId);

	/**
	 * 根据活动ID删除活动规则
	 * 
	 * @param activityId
	 * @return
	 */
	int deleteActivityRuleByActivityId(Long activityId);

	/**
	 * 
	 * @Description:根据活动ID更新规则
	 * @param record
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月8日 下午6:01:11
	 */
	int updateRuleByActivityId(ActivityRule record);
}