package com.yourong.api.service;

import java.util.List;
import java.util.Map;

import com.yourong.api.dto.MemberNotifySettingsDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.common.exception.ManagerException;

public interface MemberNotifySettingsService {
	
	/**
	 * 更新消息通知状态
	 * @param map 通知类型 通知方式键对值
	 * @param memberId 用户编号
	 * @return
	 */
	public ResultDTO updateNotifySettingsStatus(Map<Integer,Map<Integer,Integer>> map, Long memberId) throws ManagerException;
	
	
	/**
	 * 获得未配置的消息订阅项
	 * @param memberId
	 * @return
	 */
	public List<MemberNotifySettingsDto> getUncheckedNotifySettings(Long memberId);
	
	/**
	 * 获得配置的消息订阅项
	 * @param memberId
	 * @return
	 */
	public List<MemberNotifySettingsDto> getCheckedNotifySettings(Long memberId);
	
	/**
     * 取消邮件订阅
     * @param memberId
     */
    public int unsubscribe(Long memberId);

}
