package com.yourong.core.mc.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.core.mc.model.biz.ActivityForInviteFriendList;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.ActivityLottery;

/**
 * /** 活动抽奖mapper
 * 
 * @author wangyanji
 *
 */
@SuppressWarnings("rawtypes")
@Repository
public interface ActivityLotteryMapper {
	int deleteByPrimaryKey(Long id);

	int insert(ActivityLottery record);

	int insertSelective(ActivityLottery record);

	ActivityLottery selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ActivityLottery record);

	int updateByPrimaryKey(ActivityLottery record);

	Page findByPage(Page pageRequest, @Param("map") Map map);

	int batchDelete(@Param("ids") int[] ids);

	List selectForPagin(@Param("map") Map map);

	int selectForPaginTotalCount(@Param("map") Map map);

	/**
	 * 更新抽奖次数
	 * 
	 * @param record
	 * @return
	 */
	int updateByActivityAndMember(ActivityLottery record);

	/**
	 * 查询用户抽奖信息
	 * 
	 * @param record
	 * @return
	 */
	ActivityLottery selectByMemberActivity(@Param("map") Map map);

	/**
	 * 抽奖次数-1
	 * 
	 * @param record
	 * @return
	 */
	int updateRealCount(ActivityLottery record);

	/**
	 * 判断用户是否存在可抽奖记录
	 * 
	 * @param record
	 * @return
	 */
	ActivityLottery checkExistLottery(ActivityLottery record);

	/**
	 * 
	 * @Description:根据活动ID和会员ID查询抽奖记录
	 * @param activtyId
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月28日 下午3:15:19
	 */
	List<ActivityLottery> queryLotteryByMemberAndActivity(@Param("map") Map map);

	/**
	 * 锁行
	 * 
	 * @param record
	 * @return
	 */
	ActivityLottery getRecordForLock(Long id);
	
	/**
	 * 
	 * @Description:根据活动id统计
	 * @param activityId
	 * @param cycleConstraint
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 上午11:08:53
	 */
	int countActivityLotteryByActivityId(@Param("activityId") Long activityId,@Param("cycleConstraint") String cycleConstraint);
	/**
	 * 
	 * @Description:统计第四张拼图
	 * @param activityId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月19日 上午11:00:03
	 */
	int countActivityLotteryFourByActivityId(@Param("activityId") Long activityId);
	/**
	 * 
	 * @Description:统计徽章数量
	 * @param activityId
	 * @param cycleConstraint
	 * @return
	 * @author: chaisen
	 * @time:2016年7月13日 上午10:23:39
	 */
	int countActivityLotteryByActivityIdAndCycleConstraint(@Param("activityId") Long activityId,@Param("memberId") Long memberId,@Param("cycleConstraint") String cycleConstraint);
	/**
	 * 
	 * @Description:TODO
	 * @param activityId
	 * @param memberId
	 * @param cycleConstraint
	 * @return
	 * @author: chaisen
	 * @time:2016年7月8日 下午12:46:15
	 */
	List<ActivityLottery> selectActivityLotteryByActivityIdAndCycleConstraint(@Param("activityId") Long activityId,@Param("memberId") Long memberId);
	
	/**
	 * 
	 * @Description:第四张拼图是否已经发放
	 * @param activityId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author: chaisen
	 * @time:2016年7月19日 上午11:47:30
	 */
	int checkCurrentDayFourSet(@Param("activityId") Long activityId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
	
	 /** 
	 * @Description:统计押注记录
	 * @param activityId
	 * @param cycleConstraint
	 * @return
	 * @author: chaisen
	 * @time:2016年7月4日 下午4:54:48
	 */
	int countBetTotal(@Param("activityId") Long activityId,@Param("cycleConstraint") String cycleConstraint);
	
	/**
	 * 
	 * @Description:查询活动校验次数信息
	 * @param memberId
	 * @param remark
	 * @return
	 * @author: chaisen
	 * @time:2016年11月2日 下午4:48:39
	 */
	List<ActivityLottery> selectActivityLotteryByMemberId(@Param("memberId") Long memberId,@Param("remark") String remark);
	
	
	Integer sumLotteryNumByMemberId(ActivityLottery record);
	
	
	int updateByActivityAndMemberAll(ActivityLottery record);

	/**
	 * 查询抽奖表活动数据
	 * @param activityId
	 * @return
     */
	List<ActivityLottery> queryActivityLotteryByActivityId(Long activityId);

	/**
	 * 查询抽奖表活动数据条数
	 * @param activityId
	 * @return
     */
	int queryCountByActivityId(Long activityId);

	/**
	 * 查询用户领取条数
	 * @param activityId
	 * @param memberId
	 * @param cycleConstraint
     * @return
     */
	int queryCountMemberReceiveLikeCycle(@Param("activityId") Long activityId,@Param("memberId")Long memberId,@Param("cycleConstraint")String cycleConstraint);

	/**
	 * 查询用户领取信息
	 * @param activityId
	 * @param memberId
	 * @param cycleConstraint
	 * @return
	 */
	List<ActivityLottery> queryMemberReceiveLikeCycle(@Param("activityId") Long activityId,@Param("memberId")Long memberId,@Param("cycleConstraint")String cycleConstraint);
}