package com.yourong.api.service;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberToken;

/**
 * Created by py on 2015/3/26.
 */
public interface TokenService {

    public MemberToken selectByDeviceAndMemberID(String device,Long memberID);

    public void insertSelective(MemberToken tokenType);

    /**
     * 删除其他平台的令牌，主要是安卓和苹果平台 ，微信暂不考虑删除
     * @param memberID
     * @return
     */
    public int deleteMemberTokenByMemberID(Long memberID);

    /**
     * 根据微信的openID  查询用户是否登陆
     * @param device
     * @return
     */
    public MemberToken selectByWeixinID(String device);


    MemberToken selectByPrimaryKey(Long id) ;

    MemberToken queryLastLoginDeviceByMemberId(Long memberId);


    /**
     * 清空 app端登陆的token，并发送下线通知
     * @throws ManagerException
     */
    public void cleanAppClientTokenAndPushMessage(Long memberId, boolean isPush, String message);
}
