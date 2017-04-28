package com.yourong.core.mc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.ActivityGroup;
import com.yourong.core.mc.model.ActivityMessage;

public interface ActivityGroupMapper {

	int deleteByPrimaryKey(Long id);

	int insert(ActivityMessage record);

	int insertSelective(ActivityGroup record);

	ActivityMessage selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ActivityGroup record);

	int updateByPrimaryKey(ActivityMessage record);

	Page findByPage(Page pageRequest, @Param("map") Map map);

	int batchDelete(@Param("ids") int[] ids);

	List selectForPagin(@Param("map") Map map);

	int selectForPaginTotalCount(@Param("map") Map map);


	int countGroupByMemberIdAndActivityId(@Param("activityId") Long activityId, @Param("memberId") Long memberId);
	/**
	 * 
	 * @Description:获取用户分组信息
	 * @param activityId
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月30日 上午11:29:30
	 */
	ActivityGroup getCurrentMemberGroupBy(@Param("activityId") Long activityId, @Param("memberId") Long memberId);
	/**
	 * 
	 * @Description:根据条件查询组团
	 * @param record
	 * @return
	 * @author: chaisen
	 * @time:2016年7月4日 上午10:48:50
	 */
	List<ActivityGroup> getActivityGroupBySelective(ActivityGroup record);
	/**
	 * 
	 * @Description:更新组团表
	 * @param activityId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月5日 下午7:33:38
	 */
	int updateGroupTypeByActivityId(@Param("activityId") Long activityId);
}