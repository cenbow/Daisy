package com.yourong.core.uc.manager.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.common.PushClient;
import com.yourong.core.push.PushEnum;
import com.yourong.core.uc.dao.MemberTokenMapper;
import com.yourong.core.uc.manager.MemberTokenManager;
import com.yourong.core.uc.model.MemberToken;

/**
 * Created by py on 2015/3/26.
 */
@Component
public class MemberTokenManagerImpl  implements MemberTokenManager{

    @Autowired
    private MemberTokenMapper memberTokenMapper;


    @Override
    public int deleteByPrimaryKey(Long id) throws ManagerException {
        try {
            return memberTokenMapper.deleteByPrimaryKey(id);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    @Override
    public int insert(MemberToken record) throws ManagerException {
        try {
            return memberTokenMapper.insert(record);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    @Override
    public int insertSelective(MemberToken record) throws ManagerException {
        try {
            return memberTokenMapper.insertSelective(record);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    @Override
    public MemberToken selectByPrimaryKey(Long id) throws ManagerException {
        try {
            return memberTokenMapper.selectByPrimaryKey(id);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    @Override
    public MemberToken selectByDeviceAndMemberID(String device,Long memberID) throws ManagerException {
        try {
            Map map = Maps.newHashMap();
            map.put("device",device);
            map.put("memberId",memberID);
            return memberTokenMapper.selectByMap(map);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    @Override
    public int updateByPrimaryKeySelective(MemberToken record) throws ManagerException {
        try {
            return memberTokenMapper.updateByPrimaryKeySelective(record);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    @Override
    public int updateByPrimaryKey(MemberToken record) throws ManagerException {
        try {
            return memberTokenMapper.updateByPrimaryKey(record);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    @Override
    public List selectForPagin(@Param("map") Map map) throws ManagerException {
        try {
            return memberTokenMapper.selectForPagin(map);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    @Override
    public int deleteMemberTokenByMemberID(Long memberID) throws ManagerException {
        try {
            return memberTokenMapper.deleteMemberTokenByMemberID(memberID);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    public MemberToken selectByWeixinIDAndMemberId(String device, Long memberId) throws ManagerException {
        try {
            Map map = Maps.newHashMap();
            map.put("device",device);
            map.put("memberId",memberId);
            return memberTokenMapper.selectByWeixinID(map);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }
    
    @Override
	public MemberToken queryLastLoginDeviceByMemberId(Long memberId)
			throws ManagerException {
		try {
            return memberTokenMapper.queryLastLoginDeviceByMemberId(memberId);
        }catch (Exception e){
            throw new ManagerException(e);
        }
	}

    @Override
    public MemberToken selectWeixinTokenByMemberId(Long memberId) throws ManagerException {
        try {
            return memberTokenMapper.selectWeixinTokenByMemberId(memberId);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    @Override
    public MemberToken selectByDevice(String  device) throws ManagerException {
        try {
            return memberTokenMapper.selectByDevice(device);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

    @Override
    public void cleanAppClientTokenAndPushMessage(Long memberId, boolean isPush, String message,PushEnum pushEnum) throws ManagerException {
        try {
            if (isPush){
                PushClient.pushMsgToMember(message, memberId, "",pushEnum);
            }
            int i = deleteMemberTokenByMemberID(memberId);
        }catch (Exception e){
            throw new ManagerException(e);
        }
    }

	@Override
	public boolean isFirstBindingWeiXin(Long memberId, Date startTime) throws ManagerException {
		 try {
			 Integer count = memberTokenMapper.selectBindWeiXinCount(memberId, startTime);
			if(count != null && count == 1){
				return true;
			}
        }catch (Exception e){
            throw new ManagerException(e);
        }
		return false;
	}

	@Override
	public int unbundlingWeiXin(Long memberId) throws ManagerException {
		try {
			return memberTokenMapper.unbundlingWeiXin(memberId);
        }catch (Exception e){
            throw new ManagerException(e);
        }
	}

	@Override
	public MemberToken selectFirstLoginWeiXin(Long memberId) throws ManagerException {
		try {
			return memberTokenMapper.selectFirstLoginWeiXin(memberId);
        }catch (Exception e){
            throw new ManagerException(e);
        }
	}

	@Override
	public MemberToken selectFirstLoginApp(Long memberId) throws ManagerException {
		try {
			return memberTokenMapper.selectFirstLoginApp(memberId);
        }catch (Exception e){
            throw new ManagerException(e);
        }
	}
}
