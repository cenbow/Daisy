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
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.biz.ActivityBiz;

public interface ActivityMapper {
	@Delete({ "delete from mc_activity", "where id = #{id,jdbcType=BIGINT}" })
	int deleteByPrimaryKey(Long id);

	@Insert({
			"insert into mc_activity (id, activity_name, ",
			"start_time, end_time, ",
			"release_reason, activity_desc, ",
			"ad_desc, obtain_conditions_json, type,total_budget, ",
			"activity_status, audit_status, ",
			"grant_type, grant_number, ",
			"user_number, is_release, ",
			"create_id, audit_id, ",
			"audit_time, audit_message, ",
			"create_time, update_time)",
			"values (#{id,jdbcType=BIGINT}, #{activityName,jdbcType=VARCHAR}, ",
			"#{startTime,jdbcType=TIMESTAMP}, #{endTime,jdbcType=TIMESTAMP}, ",
			"#{releaseReason,jdbcType=VARCHAR}, #{activityDesc,jdbcType=VARCHAR}, ",
			"#{adDesc,jdbcType=VARCHAR},#{obtainConditionsJson,jdbcType=VARCHAR}, #{type,jdbcType=INTEGER},#{totalBudget,jdbcType=DECIMAL}, ",
			"#{activityStatus,jdbcType=INTEGER}, #{auditStatus,jdbcType=INTEGER}, ",
			"#{grantType,jdbcType=INTEGER}, #{grantNumber,jdbcType=INTEGER}, ",
			"#{userNumber,jdbcType=INTEGER}, #{isRelease,jdbcType=INTEGER}, ", "#{createId,jdbcType=BIGINT}, #{auditId,jdbcType=BIGINT}, ",
			"#{auditTime,jdbcType=TIMESTAMP}, #{auditMessage,jdbcType=VARCHAR}, ",
			"#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})" })
	int insert(Activity record);

	int insertSelective(Activity record);

	@Select({ "select", "id, activity_name, start_time, end_time, release_reason, activity_desc, ad_desc, obtain_conditions_json,type",
			"total_budget, activity_status, audit_status, grant_type, grant_number, user_number, ",
			"is_release, create_id, audit_id, audit_time, audit_message, create_time, update_time", "from mc_activity",
			"where id = #{id,jdbcType=BIGINT}" })
	@ResultMap("BaseResultMap")
	Activity selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Activity record);

	@Update({ "update mc_activity", "set activity_name = #{activityName,jdbcType=VARCHAR},",
			"start_time = #{startTime,jdbcType=TIMESTAMP},", "end_time = #{endTime,jdbcType=TIMESTAMP},",
			"release_reason = #{releaseReason,jdbcType=VARCHAR},", "activity_desc = #{activityDesc,jdbcType=VARCHAR},",
			"ad_desc = #{adDesc,jdbcType=VARCHAR},", "obtain_conditions_json = #{obtainConditionsJson,jdbcType=VARCHAR},",
			"type = #{type,jdbcType=INTEGER},", "total_budget = #{totalBudget,jdbcType=DECIMAL},",
			"activity_status = #{activityStatus,jdbcType=INTEGER},", "audit_status = #{auditStatus,jdbcType=INTEGER},",
			"grant_type = #{grantType,jdbcType=INTEGER},", "grant_number = #{grantNumber,jdbcType=INTEGER},",
			"user_number = #{userNumber,jdbcType=INTEGER},", "is_release = #{isRelease,jdbcType=INTEGER},",
			"create_id = #{createId,jdbcType=BIGINT},", "audit_id = #{auditId,jdbcType=BIGINT},",
			"audit_time = #{auditTime,jdbcType=TIMESTAMP},", "audit_message = #{auditMessage,jdbcType=VARCHAR},",
			"create_time = #{createTime,jdbcType=TIMESTAMP},", "update_time = #{updateTime,jdbcType=TIMESTAMP}",
			"where id = #{id,jdbcType=BIGINT}" })
	int updateByPrimaryKey(Activity record);

	Page<Activity> findByPage(Page<Activity> pageRequest, @Param("map") Map<String, Object> map);

	int batchDelete(@Param("ids") int[] ids);

	List<Activity> selectForPagin(@Param("map") Map<String, Object> map);

	int selectForPaginTotalCount(@Param("map") Map<String, Object> map);

	List<ActivityBiz> findInProgressActivity();

	ActivityBiz findActivityById(@Param("id") Long activityId);

	int autoStartActivityJob();

	int autoEndActivityJob();

	int reviewActivityById(Activity activity);

	int submittedForReview(Long id);

	List<ActivityBiz> showNotFinishActivityList();

	List<Activity> selectByParentId(@Param("parentId") Long parentId, @Param("childGroup") int childGroup);

	/**
	 * 
	 * @Description:后台定制活动查询
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月9日 上午10:40:00
	 */
	Page<ActivityBiz> showCustomActivityPages(Page<ActivityBiz> pageRequest, @Param("map") Map<String, Object> map);

    List<Activity> getActivityBySelective(Activity record);

    /**
     * 
     * @Description:根据活动业务类型
     * @param bizType
     * @return
     * @author: wangyanji
     * @time:2016年3月22日 下午6:58:16
     */
	List<ActivityBiz> findInProgressActivityByBizType(ActivityBiz activityBiz);
	
	
	Activity getNewActivityByActivityName(@Param("activityName") String activityName);
}

