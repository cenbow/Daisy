package com.yourong.core.mc.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.dao.ActivityMessageMapper;
import com.yourong.core.mc.manager.ActivityMessageManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityMessage;

/**
 * 
 * @desc 活动留言
 * @author wangyanji 2015年12月30日下午1:42:50
 */
@Component
public class ActivityMessageManagerImpl implements ActivityMessageManager {

	@Autowired
	private ActivityMessageMapper activityMessageMapper;

	@Override
	public Integer insert(String actvityName, Long memberId, Long messageTemplateId) throws ManagerException {
		try {
			// 获取活动
			Optional<Activity> optOfSpring = LotteryContainer.getInstance().getActivityByName(actvityName);
			if (!optOfSpring.isPresent()) {
				return 0;
			}
			Activity activity = optOfSpring.get();
			if (activity.getActivityStatus() != StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
				return 0;
			}
			ActivityMessage activityMessage = new ActivityMessage();
			activityMessage.setActivityId(activity.getId());
			activityMessage.setMemberId(memberId);
			activityMessage.setTemplateId(messageTemplateId);
			return activityMessageMapper.insert(activityMessage);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityMessage> selectRankByActivityId(Long activityId, int rowNum) throws ManagerException {
		try {
			return activityMessageMapper.selectRankByActivityId(activityId, rowNum);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean checkMessageByActivityIdAndMemberId(Long activityId, Long memberId) throws ManagerException {
		try {
			ActivityMessage am = activityMessageMapper.checkMessageByActivityIdAndMemberId(activityId, memberId);
			return am != null ? true : false;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}