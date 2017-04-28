package com.yourong.web.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.uc.manager.MemberNotifySettingsManager;
import com.yourong.core.uc.model.MemberNotifySettings;
import com.yourong.web.dto.MemberNotifySettingsDto;
import com.yourong.web.service.MemberNotifySettingsService;

@Service
public class MemberNotifySettingsServiceImpl implements MemberNotifySettingsService {
	
	private Logger logger = LoggerFactory.getLogger(MemberNotifySettingsServiceImpl.class);
	
	@Autowired
	private MemberNotifySettingsManager memberNotifySettingsManager;
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	@Override
	public ResultDO<MemberNotifySettings> updateNotifySettingsStatus(Map<Integer, Map<Integer, Integer>> map, Long memberId) throws ManagerException {
		ResultDO<MemberNotifySettings> result = new ResultDO<MemberNotifySettings>();
		try{
			List<MemberNotifySettings> memberNotifySettingsList = Lists.newArrayList();
			Timestamp timestamp = DateUtils.getCurrentDateTime();
			for(Integer notifyId : map.keySet()){
				Map<Integer,Integer> m = map.get(notifyId);
				for(Integer notifyWayId : m.keySet()){
					MemberNotifySettings memberNotifySettings = new MemberNotifySettings();
					memberNotifySettings.setNotifyType(notifyId);
					memberNotifySettings.setNotifyWay(notifyWayId);
					memberNotifySettings.setCreateTime(timestamp);
					memberNotifySettings.setUpdateTime(timestamp);
					memberNotifySettings.setStatus(m.get(notifyWayId));
					memberNotifySettings.setMemberId(memberId);
					memberNotifySettingsList.add(memberNotifySettings);
				}
			}
			if(memberNotifySettingsList.size() > 0){
				memberNotifySettingsManager.deleteMemberNotifySettingsByMemberId(memberId);
				memberNotifySettingsManager.batchUpdateNotifySettingsStatus(memberNotifySettingsList);
			}
			result.setSuccess(true);
		} catch(ManagerException ex){
			logger.error("客户消息通知状态更新失败,memberID="+memberId,ex);
			result.setResultCode(ResultCode.MEMBER_UPDATE_NOTIFY_SETTINGS_ERROR);
			throw ex;
		}
		return result;
	}

	@Override
	public List<MemberNotifySettingsDto> getUncheckedNotifySettings(Long memberId) {
		try {
			List<MemberNotifySettingsDto> memberNotifySettingsDtoList = Lists.newArrayList();
			List<MemberNotifySettings> list = memberNotifySettingsManager.getUncheckedNotifySettings(memberId);
			if(Collections3.isNotEmpty(list)){
				memberNotifySettingsDtoList = BeanCopyUtil.mapList(list, MemberNotifySettingsDto.class);
			}
			return memberNotifySettingsDtoList;
		} catch (Exception e) {
			logger.error("获得未配置的消息订阅项失败,memberID="+memberId, e);
		}
		return null;
	}

	@Override
	public List<MemberNotifySettingsDto> getCheckedNotifySettings(Long memberId) {
		try {
			List<MemberNotifySettingsDto> memberNotifySettingsDtoList = Lists.newArrayList();
			List<MemberNotifySettings> list = memberNotifySettingsManager.getCheckedNotifySettings(memberId);
			if(Collections3.isNotEmpty(list)){
				memberNotifySettingsDtoList = BeanCopyUtil.mapList(list, MemberNotifySettingsDto.class);
			}
			return memberNotifySettingsDtoList;
		} catch (Exception e) {
			logger.error("获得未配置的消息订阅项失败,memberID="+memberId, e);
		}
		return null;
	}

	@Override
	public int unsubscribe(Long memberId) {
		try {
			int num = memberNotifySettingsManager.unsubscribe(memberId);
			logger.info("用户："+memberId+",取消订阅");
			return num;
		} catch (Exception e) {
			logger.error("取消订阅异常,memberID="+memberId, e);
		}
		return 0;
	}

}
