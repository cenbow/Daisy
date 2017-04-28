package com.yourong.core.uc.manager;

import com.yourong.core.uc.model.MemberOpen;

import java.util.List;

/**
 * Created by XR on 2016/11/10.
 */
public interface MemberOpenManager {
    List<MemberOpen> queryMemberOpenByMemberIdAndKey(Long memberId, String key);

    boolean insertMemberOpen(MemberOpen memberOpen);
    
    public MemberOpen queryMemberOpenByMember(Long memberId);
}
