package com.yourong.core.mc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.ActivityLottery;

public interface ActivityDataMapper {
	
	Page<ActivityData> showActivityDataPages(Page<ActivityData> pageRequest, @Param("map") Map<String, Object> map);
	
	int insertSelective(ActivityData record);


	int updateByPrimaryKeySelective(ActivityData record);
	
	
	int countActivityDateByActivityId(@Param("activityId") Long activityId,@Param("remark") String remark);

	ActivityData selectByPrimaryKey(long id);
	
	ActivityData selectActivityDateByActivityId(@Param("activityId") Long activityId,@Param("remark") String remark);
	
	int sumTotalGold(@Param("activityId") Long activityId);


}