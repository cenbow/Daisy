package com.yourong.core.uc.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.push.PushEnum;
import com.yourong.core.uc.model.MemberToken;

/**
 * Created by py on 2015/3/25.
 */
public interface MemberTokenManager {

    int deleteByPrimaryKey(Long id) throws ManagerException;

    int deleteMemberTokenByMemberID(Long memberID) throws ManagerException;

    int insert(MemberToken record) throws ManagerException;

    int insertSelective(MemberToken record) throws ManagerException;

    MemberToken selectByPrimaryKey(Long id) throws ManagerException;

    MemberToken selectByDeviceAndMemberID(String device,Long memberID) throws ManagerException;

    int updateByPrimaryKeySelective(MemberToken record) throws ManagerException;

    int updateByPrimaryKey(MemberToken record) throws ManagerException;

    List selectForPagin(@Param("map") Map map) throws ManagerException;

    MemberToken selectByWeixinIDAndMemberId(String device, Long memberId) throws ManagerException;
    /**
	 * 通过会员ID查询最后一次在IOS或者Android登录设备
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public MemberToken queryLastLoginDeviceByMemberId(Long memberId) throws ManagerException;

    /**
     * 通过会员ID查询微信的的open
     * @param memberId
     * @return
     * @throws ManagerException
     */
	public MemberToken selectWeixinTokenByMemberId(Long memberId) throws ManagerException;

    public MemberToken selectByDevice(String  device) throws ManagerException;

    /**
     * 清空 app端登陆的token，并发送下线通知
     * @throws ManagerException
     */
    public void cleanAppClientTokenAndPushMessage(Long memberId,boolean isPush,String message,PushEnum pushEnum)throws ManagerException;

    /**
     * 是否首次绑定微信
     * @param memberId
     * @return
     */
    public boolean isFirstBindingWeiXin(Long memberId, Date startTime) throws ManagerException;
    
    /**
     * 解绑微信号
     * @param memberId
     * @return
     * @throws ManagerException
     */
    public int unbundlingWeiXin(Long memberId) throws ManagerException;
    
    /**
     * 查询首次在微信登录
     * @param memberId
     * @return
     */
    public MemberToken selectFirstLoginWeiXin(Long memberId) throws ManagerException;
    
    /**
     * 查询首次在APP登录
     * @param memberId
     * @return
     */
    public MemberToken selectFirstLoginApp(Long memberId) throws ManagerException;

}
