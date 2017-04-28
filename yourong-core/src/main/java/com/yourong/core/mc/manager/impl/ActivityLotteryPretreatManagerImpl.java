package com.yourong.core.mc.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.mc.dao.ActivityLotteryPretreatMapper;
import com.yourong.core.mc.manager.ActivityLotteryPretreatManager;
import com.yourong.core.mc.model.ActivityLotteryPretreat;

@Component
public class ActivityLotteryPretreatManagerImpl implements ActivityLotteryPretreatManager {

	@Autowired
	private ActivityLotteryPretreatMapper activityLotteryPretreatMapper;

	@Override
	public int receivePrize(ActivityLotteryPretreat activityLotteryPretreat) throws ManagerException {
		try {
			return activityLotteryPretreatMapper.receivePrize(activityLotteryPretreat);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer selectMaxRewardSortBySourceId(ActivityLotteryPretreat activityLotteryPretreat) throws ManagerException {
		try {
			return activityLotteryPretreatMapper.selectMaxRewardSortBySourceId(activityLotteryPretreat);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityLotteryPretreat> selectClaimedBySourceId(ActivityLotteryPretreat query) throws ManagerException {
		try {
			return activityLotteryPretreatMapper.selectClaimedBySourceId(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityLotteryPretreat> selectUnclaimByMobile(ActivityLotteryPretreat query) throws ManagerException {
		try {
			return activityLotteryPretreatMapper.selectUnclaimByMobile(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int receiveById(Long id) throws ManagerException {
		try {
			return activityLotteryPretreatMapper.receiveById(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int setRedPackageTop(Long activityId, Long sourceId) throws ManagerException {
		try {
			return activityLotteryPretreatMapper.setRedPackageTop(activityId, sourceId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int receiveTotalPrize(ActivityLotteryPretreat query) throws ManagerException {
		try {
			return activityLotteryPretreatMapper.receiveTotalPrize(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int sendTotalPrize(ActivityLotteryPretreat query) throws ManagerException {
		try {
			return activityLotteryPretreatMapper.sendTotalPrize(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int totalTop(ActivityLotteryPretreat query) throws ManagerException {
		try {
			return activityLotteryPretreatMapper.totalTop(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityLotteryPretreat> getRecordByQuery(ActivityLotteryPretreat query) throws ManagerException {
		try {
			return activityLotteryPretreatMapper.getRecordByQuery(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
}
