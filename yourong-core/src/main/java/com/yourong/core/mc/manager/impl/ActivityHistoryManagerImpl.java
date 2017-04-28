package com.yourong.core.mc.manager.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.mc.dao.ActivityHistoryMapper;
import com.yourong.core.mc.manager.ActivityHistoryManager;
import com.yourong.core.mc.model.ActivityHistory;
import com.yourong.core.mc.model.biz.ActivityHistoryBiz;
import com.yourong.core.mc.model.biz.ActivityHistoryDetail;

@Component
public class ActivityHistoryManagerImpl implements ActivityHistoryManager {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivityHistoryManagerImpl.class);
	
	@Autowired
	private ActivityHistoryMapper activityHistoryMapper;
	
	@Override
	public void insertActivityHistory(ActivityHistory activityHistory) throws ManagerException {
		try{
			activityHistoryMapper.insert(activityHistory);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public boolean isParticipateInActivity(Long memberId, Long activityId)
			throws ManagerException {
		try{
			ActivityHistory activityHistory = activityHistoryMapper.getActivityHistory(memberId, activityId);
			if(activityHistory != null){
				return true;
			}
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
		return false;
	}

	public Page<ActivityHistoryBiz> findByPage(Page<ActivityHistoryBiz> pageRequest, Map<String, Object> map) throws ManagerException {
		try{
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = activityHistoryMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<ActivityHistoryBiz> selectForPagin = activityHistoryMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);			
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Long totalMemberByActivityId(Long activityId)
			throws ManagerException {
		try{
		 return activityHistoryMapper.totalMemberByActivityId(activityId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<ActivityHistoryDetail> findActivityHistoryDetailPage(
			Page<ActivityHistoryDetail> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try{
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = activityHistoryMapper.selectActivityHistoryDetailForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<ActivityHistoryDetail> selectForPagin = activityHistoryMapper.selectActivityHistoryDetailForPagin(map);
			pageRequest.setData(selectForPagin);			
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public ActivityHistory getActivityHistory(Long memberId, Long activityId)
			throws ManagerException {
		try{
			ActivityHistory activityHistory = activityHistoryMapper.getActivityHistory(memberId, activityId);
			return activityHistory;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean isCompletedMemberInfo(Long memberId) {
		try {
			Long activityId = PropertiesUtil.getActivityCompletedMemberInfoId();
			return redisActivity(memberId, activityId);
		} catch (ManagerException e) {
			logger.error("查询用户是否参与完善信息活动异常", e);
		}
		return false;
	}

	@Override
	public boolean isBindEmail(Long memberId) {
		try {
			Long activityId = PropertiesUtil.getActivityBindEmailId();
			return redisActivity(memberId, activityId);
		} catch (ManagerException e) {
			logger.error("查询用户是否参与绑定邮箱活动异常", e);
		}
		return false;
	}
	
	private boolean redisActivity(Long memberId, Long activityId) throws ManagerException{
		boolean flag = RedisActivityClient.isParticipateInActivity(activityId, memberId);
		if(!flag){
			flag = isParticipateInActivity(memberId, activityId);
			if(flag){
				RedisActivityClient.setActivitiesMember(activityId, memberId);
			}
		}
		return flag;
	}

	@Override
	public List<ActivityHistoryBiz> getNewerPrizeList(int num) throws ManagerException {
		try{
			List<Long> activityIds = Lists.newArrayList(Long.valueOf(PropertiesUtil.getProperties("activity.bindEmail")),
					Long.valueOf(PropertiesUtil.getProperties("activity.completedMemberInfo")),
					Long.valueOf(PropertiesUtil.getProperties("activity.verifyTrueName")),
					Long.valueOf(PropertiesUtil.getProperties("activity.fristBindingApp")),
					Long.valueOf(PropertiesUtil.getProperties("activity.fristBindingWeixin")),
					Long.valueOf(PropertiesUtil.getProperties("activity.fristInvest")));
			return activityHistoryMapper.getNewerPrizeList(activityIds.toArray(), num);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
}
