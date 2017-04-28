package com.yourong.core.mc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.ActivityLotteryPretreat;

public interface ActivityLotteryPretreatMapper {
	int deleteByPrimaryKey(Long id);

	int insert(ActivityLotteryPretreat record);

	int insertSelective(ActivityLotteryPretreat record);

	ActivityLotteryPretreat selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ActivityLotteryPretreat record);

	int updateByPrimaryKey(ActivityLotteryPretreat record);

	Page findByPage(Page pageRequest, @Param("map") Map map);

	int batchDelete(@Param("ids") int[] ids);

	List selectForPagin(@Param("map") Map map);

	int selectForPaginTotalCount(@Param("map") Map map);

	/**
	 * 
	 * @Description:认领奖励
	 * @param record
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月11日 上午9:56:59
	 */
	int receivePrize(ActivityLotteryPretreat record);

	/**
	 * 
	 * @Description:通过sourceId查询reward_sort最大值
	 * @param record
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月11日 下午12:01:55
	 */
	Integer selectMaxRewardSortBySourceId(ActivityLotteryPretreat record);

	/**
	 * 
	 * @Description:获取单组红包领取记录
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月12日 上午10:00:15
	 */
	List<ActivityLotteryPretreat> selectClaimedBySourceId(ActivityLotteryPretreat query);

	/**
	 * 
	 * @Description:根据手机号获取未领取的奖励
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月12日 上午10:00:15
	 */
	List<ActivityLotteryPretreat> selectUnclaimByMobile(ActivityLotteryPretreat query);

	/**
	 * 
	 * @Description:根据主键更新领取奖励状态
	 * @param id
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月22日 上午11:03:37
	 */
	int receiveById(Long id);

	/**
	 * 
	 * @Description:设置手气最佳
	 * @param activityId
	 * @param sourceId
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月24日 下午3:25:04
	 */
	int setRedPackageTop(@Param("activityId") Long activityId, @Param("sourceId") Long sourceId);

	/**
	 * 
	 * @Description:统计会员总共抢到多少奖励
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月20日 上午11:38:32
	 */
	int receiveTotalPrize(ActivityLotteryPretreat query);

	/**
	 * 
	 * @Description:统计会员总共发出多少奖励
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月20日 上午11:38:32
	 */
	int sendTotalPrize(ActivityLotteryPretreat query);

	/**
	 * 
	 * @Description:统计会员总共手气最佳多少次
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月20日 上午11:38:32
	 */
	int totalTop(ActivityLotteryPretreat query);

	/**
	 * 
	 * @Description:自定义查询记录
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月23日 上午11:47:25
	 */
	List<ActivityLotteryPretreat> getRecordByQuery(ActivityLotteryPretreat query);
}