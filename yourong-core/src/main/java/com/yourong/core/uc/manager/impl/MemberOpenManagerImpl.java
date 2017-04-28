package com.yourong.core.uc.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.yourong.core.uc.dao.MemberOpenMapper;
import com.yourong.core.uc.manager.MemberOpenManager;
import com.yourong.core.uc.model.MemberOpen;

/**
 * Created by XR on 2016/11/10.
 */
@Component
public class MemberOpenManagerImpl implements MemberOpenManager {

	private static final Logger logger = LoggerFactory.getLogger(MemberOpenManagerImpl.class);

    @Autowired
    private MemberOpenMapper memberOpenMapper;
    @Override
    public List<MemberOpen> queryMemberOpenByMemberIdAndKey(Long memberId, String key) {
        return memberOpenMapper.queryMemberOpenByMemberIdAndKey(memberId,key);
    }

    @Override
    public boolean insertMemberOpen(MemberOpen memberOpen) {
		int num = 0;
		try {
			num = memberOpenMapper.insertSelective(memberOpen);
		} catch (DuplicateKeyException de) {
			logger.error("重复插入会员渠道绑定关系 memberId={} OpenPlatformKey={}", memberOpen.getMemberId(), memberOpen.getOpenPlatformKey());
		} catch (Exception e) {
			logger.error("插入会员渠道绑定关系报错 memberId={} OpenPlatformKey={}", memberOpen.getMemberId(), memberOpen.getOpenPlatformKey(), e);
		}
		if (num > 0) {
            return true;
        }
        return false;
    }
    
    @Override
    public MemberOpen queryMemberOpenByMember(Long memberId) {
        return memberOpenMapper.queryMemberOpenByMember(memberId);
    }
}
