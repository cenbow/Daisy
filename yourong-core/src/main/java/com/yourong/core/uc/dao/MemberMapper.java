package com.yourong.core.uc.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.core.uc.model.biz.MemberBiz;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.biz.MemberForLottery;

@Repository
public interface MemberMapper {
	@Delete({
		"delete from uc_member",
		"where id = #{id,jdbcType=BIGINT}"
	})
	int deleteByPrimaryKey(Long id);
	
	@Insert({
		"insert into uc_member (id, username, ",
		"mobile, password, ",
		"sex, birthday, status, ",
		"true_name, identity_number, ",
		"member_type, email, ",
		"short_url, avatars, recommend,",
		"referral, register_method, ",
		"register_time, update_time, ",
		"remarks, del_flag,register_trace_source,",
		"register_trace_no,withhold_authority_flag)",
		"values (#{id,jdbcType=BIGINT}, #{username,jdbcType=VARCHAR}, ",
		"#{mobile,jdbcType=BIGINT}, #{password,jdbcType=VARCHAR}, ",
		"#{sex,jdbcType=INTEGER}, #{birthday,jdbcType=DATE}, #{status,jdbcType=INTEGER}, ",
		"#{trueName,jdbcType=VARCHAR}, #{identityNumber,jdbcType=VARCHAR}, ",
		"#{memberType,jdbcType=INTEGER}, #{email,jdbcType=VARCHAR}, ",
		"#{shortUrl,jdbcType=VARCHAR}, #{avatars,jdbcType=VARCHAR}, #{recommend,jdbcType=VARCHAR},",
		"#{referral,jdbcType=BIGINT}, #{registerMethod,jdbcType=INTEGER}, ",
		"#{registerTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP}, ",
		"#{remarks,jdbcType=VARCHAR}, #{delFlag,jdbcType=INTEGER},#{registerTraceSource,jdbcType=VARCHAR},",
		"#{registerTraceNo,jdbcType=VARCHAR}, #{withholdAuthorityFlag,jdbcType=INTEGER})"
	})
	int insert(Member record);
	
	int insertSelective(Member record);
	
	@Select({
		"select",
		"id, username, mobile, password, sex, birthday, status, true_name, identity_number, ",
		"member_type, email, short_url, avatars, recommend, referral, register_method, register_time, ",
		"update_time, remarks, del_flag, register_Trace_Source, register_Trace_No, pay_password_flag, withhold_authority_flag,sign_way,is_auth,ca_no ",
		"from uc_member",
		"where id = #{id,jdbcType=BIGINT}"
	})
	@ResultMap("BaseResultMap")
	Member selectByPrimaryKey(Long id);
	
	
	@Select({
		"select",
		"id, username, mobile, password, sex, birthday, status, true_name, identity_number, ",
		"member_type, email, short_url, avatars, recommend, referral, register_method, register_time, ",
		"update_time, remarks, del_flag,register_Trace_Source,register_Trace_No, pay_password_flag, withhold_authority_flag ",
		"from uc_member",
		"where username = #{username,jdbcType=VARCHAR}"
	})
	@ResultMap("BaseResultMap")
	Member selectByUsername(String  username);
	
	@Select({
		"select 1 ",        
		"from uc_member",
		"where identity_number = #{identityNumber,jdbcType=VARCHAR}"
	})   
	Integer selectByIdentityNumber(String  identityNumber);
	
	
	//根据会员类型和会员的姓名模糊查询会员列表
	/*@Select({
        "select",
        "id, username, mobile, password, sex, birthday, status, true_name, identity_number, ",
        "member_type, email, short_url, avatars, recommend, referral, register_method, register_time, ",
        "update_time, remarks, del_flag",
        "from uc_member",
        "where true_name like #{trueName,jdbcType=VARCHAR} and member_type = #{memberType,jdbcType=VARCHAR}" 
    })
    @ResultMap("BaseResultMap")
    List<Member> selectMembersByTrueName(@Param("trueName")String  trueName,@Param("memberType")String memberType);
	 */
	
	@Select({
		"select",
		"id, username, mobile, password, sex, birthday, status, true_name, identity_number, ",
		"member_type, email, short_url, avatars, recommend, referral, register_method, register_time, ",
		"update_time, remarks, del_flag,register_Trace_Source,register_Trace_No, pay_password_flag, withhold_authority_flag,register_ip ",
		"from uc_member",
		"where mobile = #{mobile,jdbcType=BIGINT}"
	})
	@ResultMap("BaseResultMap")
	Member selectByMobile(Long  mobile);
	
	int updateByPrimaryKeySelective(Member record);
	
	@Update({
		"update uc_member",
		"set username = #{username,jdbcType=VARCHAR},",
		"mobile = #{mobile,jdbcType=BIGINT},",
		"password = #{password,jdbcType=VARCHAR},",
		"sex = #{sex,jdbcType=INTEGER},",
		"birthday = #{birthday,jdbcType=DATE},",
		"status = #{status,jdbcType=INTEGER},",
		"true_name = #{trueName,jdbcType=VARCHAR},",
		"identity_number = #{identityNumber,jdbcType=VARCHAR},",
		"member_type = #{memberType,jdbcType=INTEGER},",
		"email = #{email,jdbcType=VARCHAR},",
		"short_url = #{shortUrl,jdbcType=VARCHAR},",
		"avatars = #{avatars,jdbcType=VARCHAR},",
		"recommend = #{recommend,jdbcType=VARCHAR},",
		"referral = #{referral,jdbcType=BIGINT},",
		"register_method = #{registerMethod,jdbcType=INTEGER},",
		"register_time = #{registerTime,jdbcType=TIMESTAMP},",
		"update_time = #{updateTime,jdbcType=TIMESTAMP},",
		"remarks = #{remarks,jdbcType=VARCHAR},",
		"del_flag = #{delFlag,jdbcType=INTEGER}",
		"where id = #{id,jdbcType=BIGINT}"
	})
	int updateByPrimaryKey(Member record);
	
	@Update({
		"update uc_member",
		"set ",
		"member_type = 2",
		"where id = #{id,jdbcType=BIGINT}"
	})
	int updateMemberTypeSatusByID( @Param("id")Long id);
	
	
	@Update({
		"update uc_member",
		"set ",
		"member_type = 4",
		"where id = #{id,jdbcType=BIGINT}"
	})
	int updateMemberTypeByMemberId( @Param("id")Long id);
	
	
	@Update({
		"update uc_member",
		"set ",
		"password = #{password,jdbcType=VARCHAR},",        
		"update_time = now()",      
		"where id = #{id,jdbcType=BIGINT}"
	})
	int updatePasswordByMemberID(Member record);
	
	@Update({
		"update uc_member",
		"set ",
		"mobile = #{mobile,jdbcType=BIGINT},",
		"update_time = now()",
		"where id = #{id,jdbcType=BIGINT}"
	})
	int updateMobileByid(Member record);
	
	
	List<Member> selectForPagin(@Param("map") Map<String, Object> map);
	
	int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
	
	List<MemberBiz> selectMemberBizForPagin(@Param("map") Map<String, Object> map);
	
	int selectMemberBizForPaginTotalCount(@Param("map") Map<String, Object> map);
	
	Page<Member> findByPage(Page<Member> pageRequest, @Param("map") Map<String, Object> map);
	
	int batchDelete(@Param("ids") long[] ids);
	
	@Update({
		"update uc_member",
		"set ",
		"true_name = #{trueName,jdbcType=VARCHAR},",
		" identity_number = #{identityNumber,jdbcType=VARCHAR},",
		" birthday = #{birthday,jdbcType=DATE},",
		" sex = #{sex,jdbcType=INTEGER},",
		" update_time = now()",      
		"where id = #{id,jdbcType=BIGINT}"
	})
	int updateIdentityNumberById(@Param("trueName")String trueName, @Param("identityNumber")String identityNumber, @Param("id")Long id, @Param("birthday")Date birthday, @Param("sex")int sex);
	
	
	@Select({
		"select",
		"id, username, mobile, sex, birthday, status, true_name, identity_number, ",
		"member_type, email, short_url, avatars, recommend, referral,register_Trace_Source,register_Trace_No",
		"from uc_member",
		"where short_url = #{shortUrl,jdbcType=VARCHAR}"
	})
	@ResultMap("BaseResultMap")
	Member getMemberByShortUrl(@Param("shortUrl")String shortUrl);
	
	//根据会员类型和会员的姓名模糊查询会员和详情列表
	List<Member> getMemberInfoByTrueName(@Param("map")Map<String, Object> paramsMap);
	
	@Update({
		"update uc_member set avatars = #{avatars,jdbcType=VARCHAR},update_time = now() where id = #{id,jdbcType=BIGINT}"
		
	})
	int saveMemberAvatarById(@Param("id")Long id, @Param("avatars")String avatars);
	
	@Select({
		"select avatars from uc_member where id = #{id,jdbcType=BIGINT}"
	})
	String getMemberAvatar(@Param("id")Long id);
	
	@Select({
		"select",
		"id, username, mobile, password, sex, birthday, status, true_name, identity_number, ",
		"member_type, email, short_url, avatars, recommend, referral, register_method, register_time, ",
		"update_time, remarks, del_flag,register_Trace_Source,register_Trace_No, pay_password_flag, withhold_authority_flag ",
		"from uc_member",
		"where email = #{email,jdbcType=VARCHAR}"
	})
	@ResultMap("BaseResultMap")
	Member selectByEmail(String email);
	
	/**
	 * 通过会员id查询被推荐会员
	 * @param query
	 * @return
	 */
	List<Member> getReferralMemberById(BaseQueryParam query);
	/**
	 * 通过会员id查询被推荐会员总数
	 * @param query
	 * @return
	 */
	long getReferralMemberByIdCount(BaseQueryParam query);
	
	/**
	 * 获取昨日注册送彩票用户
	 * @return
	 */
	List<MemberForLottery> getMembersForLottery();
	
	/**
	 * 获取最大的会员id
	 * @return
	 */
	Long getMaxMemberId();
	
	/**
	 * 获取最大的手机号
	 * @return
	 */
	Long getMaxMobile();
	/**
	 * 冻结用户
	 * @param newMobile mobile
	 * @return
	 */
	int frozenMemberByMobile(@Param("newMobile")Long newMobile, @Param("mobile")Long mobile);
	/**
	 * 用户查询
	 * @param map
	 * @return
	 */
	public List<Member> selectMember(@Param("map")Map<String, Object> map);
	
	/**
	 * 当天第一个注册的用户
	 * @return
	 */
	Member todayFirstRegistered();
	
	/**
	 * 统计好友数量
	 * @param memberId
	 * @return
	 */
	int countFriends(@Param("memberId")Long memberId, @Param("registerStartTime")Date registerStartTime, @Param("registerEndTime")Date registerEndTime);
	
	/**
	 * 统计开通新浪存钱罐的好友数量
	 * @param memberId
	 * @return
	 */
	int countFriendsActivateWallet(@Param("memberId")Long memberId);
	
	/**
	 * 统计绑定邮箱的好友数量
	 * @param memberId
	 * @return
	 */
	int countFriendsBindEmail(@Param("memberId")Long memberId);
	
	/**
	 * 统计完善信息的好友数量
	 * @param memberId
	 * @return
	 */
	int countFrinedsPerfectInformation(@Param("memberId")Long memberId);
	
	/**
	 * 查询今天生日的用户
	 * @return
	 */
	List<Member> selectBirthdayMember(@Param("birthday") String birthday);
	
	/**
	 * 查询需要同步存钱罐余额的用户 (只要充值成功的 用户都是活跃用户)
	 * @param map
	 * @return
	 */
	int selectActiveForPaginTotalCount(@Param("map")Map<String, Object> map);
	/**
	 * 查询需要同步存钱罐余额的用户 (只要充值成功的 用户都是活跃用户)
	 * @param map
	 * @return
	 */
	List<Member> selectActiveForPagin(@Param("map")Map<String, Object> map);
	
	/**
	 * 根据注册日期查询注册用户
	 * 
	 * @param registerStartTime
	 * @param registerEndTime
	 * @return
	 */
	List<Member> selectRegisterNumberByDate(@Param("registerStartTime")Date registerStartTime,@Param("registerEndTime")Date registerEndTime);
	
	
	Member getMemberId(@Param("map")Map<String, Object> map);
	
	
	Member registerByRedPackageSuccessful(@Param("memberId") Long memberId);
	
	//根据会员手机号模糊查询会员和详情列表
	List<Member> selectListByMobile(@Param("map")Map<String, Object> paramsMap);
	
	/**
	 * 查询实名认证用户
	 * @author zhanghao
	 * @return
	 */
	List<Long> countRealNameMember(@Param("map") Map<String, Object> map);
	
	/**
	 * 根据原手机号更新会员新手机号码
	 * 
	 * @param memberId
	 * @param newMobile
	 * @param oldMobile
	 * @return
	 */
	int updateMobileByOldMobile(@Param("memberId")Long memberId, @Param("newMobile")Long newMobile, @Param("oldMobile")Long oldMobile);
	
	/**
	 * 根据身份信息修改手机号码
	 * 
	 * @param newMobile
	 * @param oldMobile
	 * @param trueName
	 * @param identityNumber
	 * @return
	 */
	int updateMobileByIdentity(@Param("newMobile")Long newMobile, @Param("oldMobile")Long oldMobile, @Param("trueName")String trueName, @Param("identityNumber")String identityNumber);
	
	
	int countRegisterNumberByDate(@Param("registerStartTime")Date registerStartTime,@Param("registerEndTime")Date registerEndTime);
	
	/**
	 * 查询时间区间内用户推荐用户数
	 * @param memberid
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	int queryMemberReferralCount(@Param("memberid")Long memberid,@Param("starttime")Date starttime,@Param("endtime")Date endtime);
	
	/**
	 * 查询时间区间内用户推荐并完成投资用户数
	 * @param memberid
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	int queryMemberReferralAndTransactionCount(@Param("memberid")Long memberid,@Param("starttime")Date starttime,@Param("endtime")Date endtime);
	
	String getMemberMobile(@Param("start")Long start);
}