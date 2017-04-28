package com.yourong.core.mc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.mc.model.ActivityHistory;
import com.yourong.core.mc.model.biz.ActivityHistoryBiz;
import com.yourong.core.mc.model.biz.ActivityHistoryDetail;


public interface ActivityHistoryMapper {

	int insert(ActivityHistory record);
	
	ActivityHistory getActivityHistory(@Param("memberId")Long memberId, @Param("activityId")Long activityId);
	
	
	List<ActivityHistoryBiz> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    Long totalMemberByActivityId(Long activityId);
    
    List<ActivityHistoryDetail> selectActivityHistoryDetailForPagin(@Param("map") Map<String, Object> map);

    int selectActivityHistoryDetailForPaginTotalCount(@Param("map") Map<String, Object> map);
	
	/**
	 * 
	 * @Description:新手任务最新动态
	 * @param ids
	 * @param num
	 * @return
	 * @author: wangyanji
	 * @time:2016年2月26日 下午3:06:22
	 */
	List<ActivityHistoryBiz> getNewerPrizeList(@Param("ids") Object[] ids, @Param("num") Integer num);
}
