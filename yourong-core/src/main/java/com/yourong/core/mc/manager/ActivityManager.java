package com.yourong.core.mc.manager;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.sales.SPGift;
import com.yourong.core.tc.model.Transaction;

public interface ActivityManager {
	Integer deleteByPrimaryKey(Long id) throws ManagerException;

	Integer insert(Activity activity) throws ManagerException;

	Activity selectByPrimaryKey(Long id) throws ManagerException;

	Integer updateByPrimaryKey(Activity activity) throws ManagerException;

	Integer updateByPrimaryKeySelective(Activity activity) throws ManagerException;

	Page<Activity> findByPage(Page<Activity> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * 根据ID查询活动
	 * 
	 * @param activityId
	 * @return
	 */
	public ActivityBiz findActivityById(Long activityId) throws ManagerException;

	/**
	 * 添加活动
	 * 
	 * @param activity
	 * @throws ManagerException
	 */
	public void addActivity(Activity activity) throws ManagerException;

	/**
	 * 查询正在进行中的活动
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public List<ActivityBiz> findInProgressActivity() throws ManagerException;

	/**
	 * 发放活动礼品
	 * 
	 * @param activityId
	 * @param activityName
	 * @param memberId
	 * @param giftList
	 * @throws ManagerException
	 */
	public void sendGifts(Long activityId, String activityName, Long memberId, List<SPGift> giftList, String methodName) throws Exception;

	/**
	 * 把活动改为进行中状态
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public int autoStartActivityJob() throws ManagerException;

	/**
	 * 把活动改为结束状态
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public int autoEndActivityJob() throws ManagerException;

	/**
	 * 活动审核
	 * 
	 * @param activity
	 * @return
	 * @throws ManagerException
	 */
	public int reviewActivityById(Activity activity) throws ManagerException;

	/**
	 * 提交审核
	 * 
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int submittedForReview(Long id) throws ManagerException;

	/**
	 * 查询活动列表
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public List<ActivityBiz> showNotFinishActivityList() throws ManagerException;

	/**
	 * 投房有喜领取优惠券
	 */
	public void investHouseReceiveCoupon(Transaction model);

	/**
	 * 领券活动
	 * 
	 * @param memberId
	 * @param activityId
	 * @return code: -2 项目不存在, -1 活动未开始, 0 已领取, 1 领取成功, 2 券已领完, 3 活动已结束
	 * @throws ManagerException
	 */
	public String reciveCoupon(Long memberId, Long activityId) throws ManagerException;

	/**
	 * 根据父级查询活动集合
	 * 
	 * @param parentId
	 * @return
	 * @throws ManagerException
	 */
	public ImmutableList<Activity> selectByParentId(Long parentId, int childGroup) throws ManagerException;

	/**
	 * 
	 * @Description:TODO
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月8日 下午1:56:15
	 */
	public Page<ActivityBiz> showCustomActivityPages(Page<ActivityBiz> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * 
	 * @Description:修改活动规则
	 * @param activityBiz
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月8日 下午5:47:02
	 */
	public void saveRule(ActivityBiz activityBiz) throws ManagerException;

    
    /**
     * 
     * @Description:根据条件查询活动信息
     * @param activity
     * @return
     * @throws ManagerException
     * @author: wangyanji
     * @time:2015年12月28日 下午1:49:38
     */
    public List<Activity> getActivityBySelective(Activity activity) throws ManagerException;

	/**
	 * 
	 * @Description:根据活动业务类型查询进行中的定制活动
	 * @param bizType
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年3月22日 下午7:00:37
	 */
	public List<ActivityBiz> findInProgressActivityByBizType(ActivityBiz activityBiz) throws ManagerException;

	public Page<ActivityData> showActivityDataPages(Page<ActivityData> pageRequest, Map<String, Object> map)throws ManagerException;

	 ResultDO<ActivityData> updateByPrimaryKeySelective(ActivityData activityData);

	 ResultDO<ActivityData> insertSelective(ActivityData activityData);

	 ActivityData selectDataByPrimaryKey(long id) throws ManagerException;
	 /**
	  * 
	  * @Description:根据活动名获取最新活动
	  * @param activityName
	  * @return
	  * @throws ManagerException
	  * @author: chaisen
	  * @time:2016年11月10日 下午1:53:39
	  */
	 Activity getNewActivityByActivityName(String activityName) throws ManagerException;

}