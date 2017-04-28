package com.yourong.core.mc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.ActivityMessage;

public interface ActivityMessageMapper {

	int deleteByPrimaryKey(Long id);

	int insert(ActivityMessage record);

	int insertSelective(ActivityMessage record);

	ActivityMessage selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ActivityMessage record);

	int updateByPrimaryKey(ActivityMessage record);

	Page findByPage(Page pageRequest, @Param("map") Map map);

	int batchDelete(@Param("ids") int[] ids);

	List selectForPagin(@Param("map") Map map);

	int selectForPaginTotalCount(@Param("map") Map map);

	List<ActivityMessage> selectRankByActivityId(@Param("activityId") Long activityId, @Param("rowNum") int rowNum);

	ActivityMessage checkMessageByActivityIdAndMemberId(@Param("activityId") Long activityId, @Param("memberId") Long memberId);
	
	ActivityMessage selectFirstTenHistroyByActivityId(@Param("activityId") Long activityId,@Param("templateId") Long templateId);
	
	List<ActivityMessage> selectPkHistroyByActivityId(@Param("activityId") Long activityId);
	
	ActivityMessage selectTotalAmountByActivityId(@Param("activityId") Long activityId);
}