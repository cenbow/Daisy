package com.yourong.backend.mc.service;

import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.biz.ActivityBiz;

public interface ActivityService {

	/**
	 * 新增促销活动
	 * 
	 * @param activityBiz
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO addActivity(ActivityBiz activityBiz) throws ManagerException;

	/**
	 * 修改促销活动
	 * 
	 * @param activityBiz
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO updateActivity(ActivityBiz activityBiz) throws ManagerException;

	/**
	 * 根据ID查询活动
	 * 
	 * @param activityId
	 * @return
	 */
	public ActivityBiz findActivityById(Long activityId);

	/**
	 * 分页查询活动
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<Activity> findByPage(Page<Activity> pageRequest, Map<String, Object> map);

	/**
	 * 删除活动
	 * 
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO deleteActivityById(Long activityId) throws ManagerException;

	/**
	 * 活动审核
	 * 
	 * @param activityId
	 * @param userId
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO reviewActivityById(Long activityId, Long userId) throws ManagerException;

	/**
	 * 提交审核
	 * 
	 * @param id
	 * @return
	 */
	public ResultDO submittedForReview(Long id);

	/**
	 * 
	 * @Description:定制活动查询
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月8日 下午1:51:45
	 */
	public Page<ActivityBiz> showCustomActivityPages(Page<ActivityBiz> pageRequest, Map<String, Object> map);

	/**
	 * 
	 * @Description:修改活动规则
	 * @param activityBiz
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月8日 下午5:39:28
	 */
	public Object saveRule(ActivityBiz activityBiz);

	/**
	 * 
	 * @Description:后台人工生成红包
	 * @param transactionId
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月25日 上午9:53:54
	 */
	public Object createRedPackage(Long transactionId);

	public Page<ActivityData> showActivityDataPages(Page<ActivityData> pageRequest, Map<String, Object> map);
	/**
	 * 
	 * @Description:更新
	 * @param activityData
	 * @return
	 * @author: chaisen
	 * @time:2016年7月9日 上午9:56:44
	 */
	public  ResultDO<ActivityData> updateByPrimaryKeySelective(ActivityData activityData);
	/**
	 * 
	 * @Description:保存
	 * @param activityData
	 * @return
	 * @author: chaisen
	 * @time:2016年7月9日 上午9:57:53
	 */
	public  ResultDO<ActivityData> insertSelective(ActivityData activityData);

	public ActivityData selectByPrimaryKey(long id);
	/**
	 * 
	 * @Description:根据红包加密串查询交易号
	 * @param redBagCode
	 * @return
	 * @author: chaisen
	 * @time:2016年7月21日 下午2:28:58
	 */
	public Object findTransactionId(String redBagCode);
	/**
	 * 
	 * @Description:快投有奖补发
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年12月14日 下午4:11:49
	 */
	public Object sendQuickDirectLottery(Long projectId);
}
