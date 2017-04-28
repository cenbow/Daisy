package com.yourong.core.uc.manager;

import java.util.List;

import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberNotifySettings;
import com.yourong.core.uc.model.biz.MemberNotifySettingsForSendMsg;

public interface MemberNotifySettingsManager {

	/**
	 * 更新消息通知状态
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	int updateNotifySettingsStatus(MemberNotifySettings record) throws ManagerException;
	
	/**
	 * 初始化消息通知配置项
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	int initMemberNotifySettings(Long memberId) throws ManagerException ;
	
	/**
	 * 批量更新消息通知状态
	 * @param list
	 * @return
	 */
	int batchUpdateNotifySettingsStatus(List<MemberNotifySettings> list) throws ManagerException ;
	
	/**
	 * 根据用户编号删除消息配置项
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	int deleteMemberNotifySettingsByMemberId(Long memberId) throws ManagerException ;
	
	/**
	 * 发送信息
	 * @param memberId
	 * @param notiyType
	 * @param args
	 * @throws Exception
	 */	
    public void sendMessageByNotifyType(MemberNotifySettingsForSendMsg sendType)throws Exception;
    
    /**
     * 直接发送信息	
     * @param sendType
     * @throws Exception
     */
    public void sendMessageDirectly(MemberNotifySettingsForSendMsg sendType)throws Exception;
    
    /**
     * 获取发送通知雷系
     * @param notifyType
     * @param memberId
     * @return
     * @throws Exception
     */
    public MemberNotifySettingsForSendMsg getNotifySendType(int notifyType, Long memberId)throws Exception;
    
    /**
     *  获得未配置的消息订阅项
     * @param memberId
     * @return
     * @throws Exception
     */
    public List<MemberNotifySettings> getUncheckedNotifySettings(Long memberId) throws Exception;
    
    /**
     *  获得配置的消息订阅项
     * @param memberId
     * @return
     * @throws Exception
     */
    public List<MemberNotifySettings> getCheckedNotifySettings(Long memberId) throws Exception;
    
    /**
     * 取消邮件订阅
     * @param memberId
     * @throws ManagerException
     */
    public int unsubscribe(Long memberId) throws ManagerException;
}
