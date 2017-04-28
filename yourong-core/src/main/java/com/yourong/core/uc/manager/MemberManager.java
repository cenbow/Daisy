package com.yourong.core.uc.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;
import com.yourong.core.uc.model.biz.MemberBiz;
import com.yourong.core.uc.model.biz.MemberForLottery;

public interface MemberManager {

	/**
	 * 分页查询用户信息
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<Member> findByPage(Page<Member> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * 分页查询用户Biz信息
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
     */
	public Page<MemberBiz> findMemberBizByPage(Page<MemberBiz> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * 批量删除用户
	 * @param ids
	 * @return
	 */
	int batchDelete(long[] ids) throws ManagerException;
	
	int insertSelective(Member record) throws ManagerException;

	Member selectByPrimaryKey(Long id) throws ManagerException;

	Member selectByUsername(String username) throws ManagerException;

	Member selectByMobile(Long mobile) throws ManagerException;
	
	int updatePasswordByMemberID(Member record) throws ManagerException;
	
	/**
	 * 更新用户身份证
	 * @param identityNumber 身份证
	 * @param id 客户编号
	 * @param birthday 
	 * @param sex 
	 * @return
	 * @throws ManagerException
	 */
	int updateIdentityNumberById(String trueName,String identityNumber, Long id, Date birthday, int sex) throws ManagerException;
	
	/**
	 * 根据用户的URL获得用户信息
	 * @param shortUrl
	 * @return
	 * @throws ManagerException
	 */
	public Member getMemberByShortUrl(String shortUrl) throws ManagerException;
	
	/**
	 * 根据会员类型和会员的姓名模糊查询会员和详情列表
	 * @param paramsMap
	 * @return
	 * @throws ManagerException
	 */
	public List<Member> getMemberInfoByTrueName(Map<String, Object> paramsMap) throws ManagerException;
		
	/**
	 * 根据会员ID 查询会员详细信息，企业信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	MemberBaseBiz selectMemberBaseBiz(Long id) throws ManagerException;
	
	
	 int selectForPaginTotalCount(Map<String, Object> map) throws ManagerException;
	 
	 List<Member> selectForPagin(Map<String, Object> map) throws ManagerException;
	 
	 /**
	  * 判断身份证是否重复
	  * @param identityNumber
	  * @return
	  * @throws ManagerException
	  */
	 boolean isExitMemberByIdentityNumber(String  identityNumber) throws ManagerException;
	
	
	 /**
	  * 保存用户头像
	  * @param id
	  * @param avatars
	  * @return
	  * @throws ManagerException
	  */
	public int saveMemberAvatarById(Long id, String avatars) throws ManagerException;
	
	
	/**
	 * 获得用户头像地址
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public String getMemberAvatar(Long id) throws ManagerException;
	
	/**
	 * 更新用户信息
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	public int updateByPrimaryKeySelective(Member record) throws ManagerException;

	/**
	 * 通过邮箱查询用户
	 * @param email
	 * @return
	 * @throws ManagerException
	 */
	public Member selectByEmail(String email) throws ManagerException;

	/**
	 * 通过会员id查询被推荐会员
	 * @param query
	 * @return
	 */
	public Page<Member> getReferralMemberById(BaseQueryParam query) throws ManagerException;
	
	/**
	 * 完善个人信息发送红包
	 * @param memberId 会员编号
	 * @return (0:已经发送,1:正常发送,-1：不符合发送条件)
	 * @throws ManagerException
	 */
	public int sendCouponOnCompleteMemberInfo(Long memberId) throws ManagerException;

	/**
	 * 获取昨日注册送彩票用户
	 * @return
	 * @throws ManagerException
	 */
	public List<MemberForLottery> getMembersForLottery() throws ManagerException;

	/**
	 * 更新手机号码
	 * @param record
	 * @return
	 */
	int updateMobileByid(Member record)throws ManagerException;

	/**
	 * 获取最大的会员id
	 * @return
	 * @throws ManagerException
	 */
	public Long getMaxMemberId()throws ManagerException;
	
	/**
	 * 获取最大的手机号
	 * @return
	 * @throws ManagerException
	 */
	public Long getMaxMobile()throws ManagerException;
	
	/**
	 * 冻结用户
	 * @param newMobile mobile
	 * @return
	 */
	public int frozenMemberByMobile(Long newMobile,Long mobile)throws ManagerException;
	
	
	/**
	 * 用户查询
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public List<Member> selectMember(Map<String, Object> map) throws ManagerException;
	
	/**
	 * 当天第一个注册的用户
	 * @return
	 * @throws ManagerException
	 */
	public Member todayFirstRegistered() throws ManagerException;
	
	/**
	 * 统计好友数量
	 * @param memberId
	 * @return
	 */
	public int countFriends(Long memberId, Date registerStartTime, Date registerEndTime) throws ManagerException;
	
	/**
	 * 统计开通新浪存钱罐的好友数量
	 * @param memberId
	 * @return
	 */
	public int countFriendsActivateWallet(Long memberId) throws ManagerException;
	
	/**
	 * 统计绑定邮箱的好友数量
	 * @param memberId
	 * @return
	 */
	public int countFriendsBindEmail(Long memberId) throws ManagerException;
	
	/**
	 * 统计完善信息的好友数量
	 * @param memberId
	 * @return
	 */
	public int countFrinedsPerfectInformation(Long memberId) throws ManagerException;
	
	/**
	 * 修改密码
	 * @param id
	 * @param oldpassword
	 * @param newPassword
	 * @return
	 */
	public ResultDO updatePassword(Long id,String oldpassword,String newPassword);
	
	/**
	 * 重置密码
	 * @param id
	 * @param mobile
	 * @param newPassword
	 * @return
	 */
	public ResultDO updatePasswordByMobile(Long id,Long mobile,String newPassword);
	
	/**
     * 通过会员id查询被推荐会员总数
     * @param query
     * @return
     */
	long getReferralMemberByIdCount(Long memberId) throws ManagerException;
	
	/**
	 * 查询今天生日的用户
	 * @return
	 * @throws ManagerException
	 */
	public List<Member> selectTodayBirthdayMember() throws ManagerException;
	
	/**
	 * 获得生日专题活动
	 * @return
	 */
	public Activity getBirthdayActivity() throws ManagerException;

	/**
	 * 查询需要同步存钱罐余额的用户
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	int selectActiveForPaginTotalCount(Map<String, Object> map) throws ManagerException;
	/**
	 * 查询需要同步存钱罐余额的用户
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	List<Member> selectActiveForPagin(Map<String, Object> map) throws ManagerException;

	/**
	 * 根据企业id查询用户信息
	 * @param enterpriseId
	 * @return
	 */
	MemberBaseBiz getMemberBizByEnterpriseId(Long enterpriseId);

	/**
	 * 查询符合条件的用户
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Member getMemberId(Map<String, Object> map) throws ManagerException;

	/**
	 * 添加推荐人
	 * @param referredId 被推荐人ID
	 * @param referralMobile 推荐人手机号
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO<Object> addReferral(Long referredId, Long referralMobile) throws ManagerException;
	
	/**
	 * 
	 * @Description:红包用户注册判断
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年3月22日 上午9:36:42
	 */
	public Member registerByRedPackageSuccessful(Long memberId);
	/**
	 * 
	 * @Description:根据借款人类型查询借款人和企业信息
	 * @param borrowerId
	 * @param borrowerType
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年4月20日 下午2:11:59
	 */
	public MemberBaseBiz selectMemberBaseBizByBorrowerType(Long borrowerId, Integer borrowerType ,Long enterpriseId) throws ManagerException;
	
	/**
	 * 
	 * @Description:根据用户手机模糊查询用户
	 * @param paramsMap
	 * @return
	 * @author: zhanghao
	 * @throws ManagerException 
	 * @time:2016年6月8日 上午9:28:59
	 */
	public List<Member> selectListByMobile(Map<String, Object> paramsMap) throws ManagerException;
	
	/**
	 * 查询并同步会员是否委托扣款
	 * 
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO<Boolean> synWithholdAuthority(Long memberId) throws ManagerException;
	
	/**
	 * 委托扣款授权处理
	 * 
	 * @param memberID
	 * @param type 操作类型：1：委托扣款, 2：解除委托扣款
	 * @return
	 * @author: luwenshan
	 * @time: 2016年8月9日 下午15:38:11
	 * @throws Exception
	 */
	public ResultDO<String> handWithholdAuthority(Long memberID, int type) throws ManagerException;
	
	/**
	 * 委托扣款授权处理
	 * 
	 * @param memberID
	 * @param type 操作类型：1：委托扣款, 2：解除委托扣款
	 * @param returnUrl 新浪返回url
	 * @return
	 * @author: luwenshan
	 * @time: 2016年8月9日 下午15:38:11
	 * @throws Exception
	 */
	public ResultDO<String> handWithholdAuthority(Long memberID, int type, String returnUrl) throws ManagerException;

	/**
	 * 
	 * @Description:同步是否设置支付密码
	 * @param memberId
	 * @return true 已设置，false 未设置（或者第三方查询失败）
	 * @author: wangyanji
	 * @time:2016年7月15日 下午2:15:11
	 */
	public Object synPayPasswordFlag(Long memberId) throws ManagerException;

	/**
	 * 
	 * @Description:设置支付密码
	 * @param memberId
	 * @param handleType
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年7月15日 下午6:00:46
	 */
	public Object handlePayPassword(Long memberId, int handleType, String handlePayPassword) throws ManagerException;
	
	/**
	 * 托管充值
	 * 
	 * @param memberId 会员ID
	 * @param rechargeAmount 充值金额
	 * @param orderNo 充值订单号
	 * @param otherParams (payerIp-会员充值IP/returnUrl-新浪返回url/tradeNo-交易单号/type-充值类型)
	 * @param rechargeSource
	 * @return
	 * @author: luwenshan
	 * @time: 2016年8月9日 下午15:38:11
	 * @throws Exception 
	 */
	public ResultDO<Object> createHostingDeposit(Long memberId, String payerIp, BigDecimal rechargeAmount, TypeEnum type, String orderNo, 
			String tradeNo, Integer rechargeSource) throws Exception;
	
	/**
	 * 托管充值
	 * 
	 * @param memberId 会员ID
	 * @param rechargeAmount 充值金额
	 * @param orderNo 充值订单号
	 * @param otherParams (payerIp-会员充值IP/returnUrl-新浪返回url/tradeNo-交易单号/type-充值类型)
	 * @return
	 * @author: luwenshan
	 * @time: 2016年8月9日 下午15:38:11
	 * @throws Exception 
	 */
	public ResultDO<Object> createHostingDeposit(Long memberId, BigDecimal rechargeAmount, String orderNo, Map<String, Object> otherParams)
			throws Exception;
	
	/**
	 * 托管提现
	 * 
	 * @param memberId
	 * @param userIp
	 * @param withdrawAmount
	 * @param freeWithdrawAmount 
	 * @param returnUrl 新浪返回url
	 * @return
	 * @author: luwenshan
	 * @time: 2016年8月9日 下午15:38:11
	 * @throws Exception
	 */
	public ResultDO<Object> createHostingWithdraw(Long memberId, String userIp, BigDecimal withdrawAmount, BigDecimal freeWithdrawAmount, BigDecimal fee) 
			throws Exception;
	
	/**
	 * 托管提现
	 * 
	 * @param memberId
	 * @param userIp
	 * @param withdrawAmount
	 * @param freeWithdrawAmount
	 * @param returnUrl 新浪返回url
	 * @return
	 * @author: luwenshan
	 * @time: 2016年8月9日 下午15:38:11
	 * @throws Exception
	 */
	public ResultDO<Object> createHostingWithdraw(Long memberId, String userIp, BigDecimal withdrawAmount, BigDecimal freeWithdrawAmount, 
			String returnUrl,String withdrawNo, BigDecimal fee, Integer withdrawSource) throws Exception;
	
	/**
	 * 根据原手机号更新会员新手机号码
	 * 
	 * @param memberId
	 * @param newMobile
	 * @param oldMobile
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO<Object> updateMobileByOldMobile(Long memberId, Long newMobile, Long oldMobile) throws Exception;
	
	/**
	 * 修改手机号检验会员信息
	 * 
	 * @param oldMobile
	 * @param trueName
	 * @param identityNumber
	 * @param password
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO<Object> validateMemberInfo(Long oldMobile, String trueName, String identityNumber, String password) throws ManagerException;
	
	/**
	 * 根据身份信息修改手机号码
	 * 
	 * @param oldMobile
	 * @param newMobile
	 * @param trueName
	 * @param identityNumber
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO<Object> updateMobileByIdentity(Long oldMobile, Long newMobile, String trueName, String identityNumber) throws ManagerException;
	
	/**
	 * 根据新手机号相关信息重置登录密码
	 * 
	 * @param newMobile
	 * @param trueName
	 * @param identityNumber
	 * @param newPassword
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO<Object> updateNewMobilePassword(Long newMobile, String trueName, String identityNumber, String newPassword) throws ManagerException;
	
	
	public ResultDO<Object> requestRecharge(Long memberId, String payerIp, BigDecimal rechargeAmount, int type, 
			String orderNo, String tradeNo, String returnUrl, boolean isMobileSource, Integer rechargeSource) throws Exception; 
	
	public ResultDO<Object> showMemberInfosSina(Long memberId,String returnUrl) throws ManagerException;

	public ResultDO<Object> autoInvestSet(Long memberId, int investFlag)throws ManagerException;

	public int countRegisterNumberByDate(Date date) throws ManagerException;

	public ResultDO<Member> registerMemberAndAuth(Long mobile, String password, String trueName, String identityNumber, int registerMethod,
			String channelBusiness, String ip) throws Exception;

	/**
	 * 
	 * @Description:判断是否要校验新客限制（特殊用户可以重复投资新客项目）
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2016年11月17日 下午2:18:32
	 */
	public boolean needCheckNoviceProject(Long memberId);

	/**
	 * 查询时间区间内用户推荐用户数
	 * @param memberid
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	int queryMemberReferralCount(Long memberid,Date starttime,Date endtime);

	/**
	 * 查询时间区间内用户推荐并完成投资用户数
	 * @param memberid
	 * @param starttime
	 * @param endtime
     * @return
     */
	public int queryMemberReferralAndTransactionCount(Long memberid,Date starttime,Date endtime);
}
