package com.yourong.api.service.impl;

import com.yourong.api.service.TokenService;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.push.PushEnum;
import com.yourong.core.uc.manager.MemberTokenManager;
import com.yourong.core.uc.model.MemberToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by py on 2015/3/26.
 */
@Service
public class TokenServiceImpl implements TokenService {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MemberTokenManager memberTokenManager;

    /**
     * 根据设备ID 查询Token
     * @param device
     * @return
     */
    public MemberToken selectByDeviceAndMemberID(String device,Long memberID) {
        try {
            return memberTokenManager.selectByDeviceAndMemberID(device, memberID);
        }catch(Exception e){
            logger.error("根据设备ID 查询Token device ="+device,e);
        }
        return  null;
    }

    @Override
    public int deleteMemberTokenByMemberID(Long memberID) {
        try {
            return memberTokenManager.deleteMemberTokenByMemberID(memberID);
        }catch(Exception e){
            logger.error("删除其他平台的令牌，memberID:"+memberID,e);
        }
        return  0;
    }

    public  void  insertSelective (MemberToken tokenType ) {
        try {
            memberTokenManager.insertSelective(tokenType);
        } catch (Exception e) {
            logger.error("tokenType:" + tokenType.toString() , e);
        }

    }

    @Override
    public MemberToken selectByWeixinID(String device) {
        try {
            return  memberTokenManager.selectByDevice(device);
        } catch (Exception e) {
            logger.error("device:" + device , e);
        }
        return  null;
    }

    @Override
    public MemberToken selectByPrimaryKey(Long id) {
        try {
            return  memberTokenManager.selectByPrimaryKey(id);
        } catch (Exception e) {
            logger.error("id:" + id , e);
        }
        return  null;
    }
    public MemberToken queryLastLoginDeviceByMemberId(Long memberId){
        try {
            return  memberTokenManager.queryLastLoginDeviceByMemberId(memberId);
        } catch (Exception e) {
            logger.error("memberId:" + memberId , e);
        }
        return  null;
    }

    @Override
    public void cleanAppClientTokenAndPushMessage(Long memberId, boolean isPush, String message) {
        try {
             memberTokenManager.cleanAppClientTokenAndPushMessage(memberId,isPush,message,PushEnum.MEMBER_EXIT);
        } catch (Exception e) {
            logger.error("memberId:" + memberId , e);
        }
    }
}
