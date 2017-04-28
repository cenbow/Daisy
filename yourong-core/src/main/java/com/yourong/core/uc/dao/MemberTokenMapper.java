package com.yourong.core.uc.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.MemberToken;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberTokenMapper {

    int deleteByPrimaryKey(Long id);

    /**
     * 逻辑删除令牌，主要是安卓和苹果平台
     * @param memberID
     * @return
     */
    int deleteMemberTokenByMemberID(Long memberID);

    int insert(MemberToken record);

    int insertSelective(MemberToken record);

    MemberToken selectByPrimaryKey(Long id);

    MemberToken selectByMemberId(Long memberId);

    MemberToken selectByMap(@Param("map") Map map);

    MemberToken selectByDevice(String  device);

    int updateByPrimaryKeySelective(MemberToken record);

    int updateByPrimaryKey(MemberToken record);

    Page findByPage(Page pageRequest, @Param("map") Map map);

    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);
    
    /**
     * 通过会员ID查询最后一次在IOS或者Android登录设备
     * @param memberId
     * @return
     */
    public MemberToken queryLastLoginDeviceByMemberId(Long memberId);

    MemberToken  selectWeixinTokenByMemberId (Long memberId);
    MemberToken selectByWeixinID(@Param("map") Map map);

    /**
     * 统计会员绑定微信次数
     * @param memberId
     * @return
     */
    public Integer selectBindWeiXinCount(@Param("memberId")Long memberId, @Param("startTime")Date startTime);
    
    /**
     * 解绑微信号
     * @param memberId
     * @return
     */
    public int unbundlingWeiXin(Long memberId);
    
    /**
     *  查询首次在微信登录
     * @param memberId
     * @return
     */
    public MemberToken selectFirstLoginWeiXin(Long memberId);
    
    /**
     * 查询首次在APP登录
     * @param memberId
     * @return
     */
    public MemberToken selectFirstLoginApp(Long memberId);

}