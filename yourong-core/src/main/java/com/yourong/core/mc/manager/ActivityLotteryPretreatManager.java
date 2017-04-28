package com.yourong.core.mc.manager;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.mc.model.ActivityLotteryPretreat;

/**
 * @desc 抽奖预处理
 * @author wangyanji 2016年1月8日下午1:37:04
 */
public interface ActivityLotteryPretreatManager {

	/**
	 * 
	 * @Description:领取奖励
	 * @param activityLotteryPretreat
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月11日 上午9:55:34
	 */
	public int receivePrize(ActivityLotteryPretreat activityLotteryPretreat) throws ManagerException;

	/**
	 * 
	 * @Description:通过sourceId查询reward_sort最大值
	 * @param activityLotteryPretreat
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月11日 下午1:02:02
	 */
	public Integer selectMaxRewardSortBySourceId(ActivityLotteryPretreat activityLotteryPretreat) throws ManagerException;

	/**
	 * 
	 * @Description:获取单组红包领取记录
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月12日 上午10:00:15
	 */
	public List<ActivityLotteryPretreat> selectClaimedBySourceId(ActivityLotteryPretreat query) throws ManagerException;

	/**
	 * 
	 * @Description:根据手机号获取未领取的奖励
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月18日 下午2:04:21
	 */
	public List<ActivityLotteryPretreat> selectUnclaimByMobile(ActivityLotteryPretreat query) throws ManagerException;

	/**
	 * 
	 * @Description:根据主键更新领取奖励状态
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月22日 上午11:00:41
	 */
	public int receiveById(Long id) throws ManagerException;

	/**
	 * 
	 * @Description:设置手气最佳
	 * @param activityId
	 * @param sourceId
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年3月24日 下午3:24:07
	 */
	public int setRedPackageTop(Long activityId, Long sourceId) throws ManagerException;

	/**
	 * 
	 * @Description:统计会员总共抢到多少奖励
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月20日 上午11:37:25
	 */
	public int receiveTotalPrize(ActivityLotteryPretreat query) throws ManagerException;

	/**
	 * 
	 * @Description:统计会员总共发出多少奖励
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月20日 上午11:37:25
	 */
	public int sendTotalPrize(ActivityLotteryPretreat query) throws ManagerException;

	/**
	 * 
	 * @Description:统计会员总共手气最佳多少次
	 * @param query
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月20日 上午11:37:25
	 */
	public int totalTop(ActivityLotteryPretreat query) throws ManagerException;

	/**
	 * 
	 * @Description:自定义查询
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月18日 下午2:04:21
	 */
	public List<ActivityLotteryPretreat> getRecordByQuery(ActivityLotteryPretreat query) throws ManagerException;
}
