package com.yourong.api.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.api.dto.AutoInvestSetDto;
import com.yourong.api.dto.FinEaringDto;
import com.yourong.api.dto.MemberAuthDto;
import com.yourong.api.dto.MemberCheckDto;
import com.yourong.api.dto.MemberCheckInfoDto;
import com.yourong.api.dto.MemberReferralDto;
import com.yourong.api.dto.MemberSecurityDto;
import com.yourong.api.dto.PopularityInOutLogDto;
import com.yourong.api.dto.RechargeDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.enums.RechargeBankCode;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.uc.model.AutoInvestSet;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.biz.MemberForLottery;

public interface MemberService {

	Member selectByPrimaryKey(Long id);

	Member selectByUsername(String username);

	Member selectByMobile(Long mobile);
	
	ResultDTO<Object>  updatePassword(Long id,String oldpassword,String newPassword);
	
	ResultDTO<Object> updatePasswordByMobile(Long id,Long mobile,String newPassword);
	/**
	 * 判断会员是否实名和手机验证
	 * @param id
	 * @return
	 */
	 boolean isAuthIdentityAndPhone(Long id);
	
	/**
	 * 认证客户身份证信息
	 * @param memberDto
	 * @return
	 */
	ResultDO<Object> authIdentity(MemberAuthDto memberDto)throws Exception;
	
	/**
	 * 根据用户的URL获得用户信息
	 * @param shortUrl
	 * @return
	 * @throws ManagerException
	 */
	public Member getMemberByShortUrl(String shortUrl);


    /**
     * 充值返回签名FORM， 并插入 充值流水
     * @param memberID
     * @param bigDecimal
     * @param code
     * @param type 是直接充值， 还是交易充值
     * @return
     * @throws Exception
     */
	public String buildSigleFormAndRechargeLog(Long memberID, String payerIp, BigDecimal bigDecimal, RechargeBankCode code, TypeEnum type,
			String orderNo) throws Exception;

	/**
	 *绑卡充值，并插入 充值流水
	 * @param memberID
	 * @param bigDecimal
	 * @param
	 * @param type 是直接充值， 还是交易充值
	 * @return
	 * @throws Exception
	 */
	public ResultDO<Object> rechargeOnCardAndRechargeLog(Long memberID, String payerIp, BigDecimal bigDecimal, Long cardID, TypeEnum type,
			String orderNo) throws Exception;


	/**
	 *绑卡充值，并插入 充值流水
	 * @param memberID
	 * @param bigDecimal
	 * @param
	 * @param type 是直接充值， 还是交易充值
	 * @return
	 * @throws Exception
	 */
	public ResultDO<Object> rechargeOnCardAndRechargeLog(Long memberID, String payerIp, BigDecimal bigDecimal, Long cardID, TypeEnum type,
			String orderNo, Integer rechargeSource) throws Exception;

	/**
	 * 绑卡支付  确认银行卡
	 * @param outAdvanceNo
	 * @param ticket
	 * @param validateCode
	 * @param userIp
	 * @return
	 */
	public ResultDO<Object> rechargeOnCardAndCheck(String outAdvanceNo, String ticket, String validateCode, String userIp);


    /**
     * 保存用户头像
     * @param memberId 用户编号
     * @param avatarURL 用户URL
     * @return
     */
    public String saveAvatar(Long memberId, String avatarURL);
    
    /**
     * 获得用户头像地址
     * @param memberId
     * @return
     */
    public String getMemberAvatar(Long memberId);
    
    /**
     * 保存会员昵称
     * @param memberId
     * @param userName
     * @return
     */
    public ResultDTO saveUserName(Long memberId, String userName);

    /**
     * 绑定邮箱
     * @param email
     * @return
     */
    public ResultDO<Member> bindEmail(String email) throws Exception;

	/**
	 * 绑定邮箱，发送链接到邮箱进行验证
	 * @param memberId
	 * @param email
	 * @return
	 */
    public ResultDTO toBindEmail(Long memberId, String email);

	/**
	 * 通过邮箱查询用户
	 * @param email
	 * @return
	 */
	Member selectByEmail(String email);
	
	
	/**
	 * 是否领取完善信息优惠卷
	 * @param memberId
	 * @return
	 */
	public boolean isReceiveMemberInfoCompleteCoupon(Long memberId);

	/**
	 * 获取昨日注册送彩票用户
	 * @return
	 */
	public List<MemberForLottery> getMembersForLottery();
	
	/**
	 * 获取新浪基金年华
	 * @return
	 */
	public  List<FinEaringDto> getfin();
	
	/**
	 * 获得会员昵称
	 * @param id
	 * @return
	 */
	public String getMemberUserName(Long id);
	
	/**
	 * 获得格式化的会员昵称
	 * @param id
	 * @return
	 */
	public String getFormatMemberUserName(Long id);
	
	/**
	 * 获得用户安全认证信息
	 * @param id
	 * @return
	 */
	public ResultDTO<MemberSecurityDto> queryMemberAuthorize(Long id);
	
	/**
	 * 获取用户推荐列表
	 * @param query
	 * @return
	 */
	public Page<MemberReferralDto> queryMemberReferralList(BaseQueryParam query) ;
	
	/**
	 * 获取用户人气值列表
	 * @param
	 * @param
	 * @return
	 */
	public Page<PopularityInOutLogDto> queryMemberPopularityLogList(BaseQueryParam query);
	
	/**
	 * 签到
	 * @param memberId
	 * @param loginSource
	 * @return
	 * @throws Exception
	 */
	public ResultDTO<MemberCheckDto> memberCheck(Long memberId, int loginSource) throws Exception;
	
	/**
	 * 查询签到信息
	 * @param memberId
	 * @return
	 */
	public ResultDTO<MemberCheckInfoDto> queryMemberSignInInfo(Long memberId);
	
	/**
	 * 
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public String getAuditMemberInfosForm(Long memberId) throws Exception;
	
	/**
	 * 获得生日专题活动
	 * @return
	 */
	public Activity getBirthdayActivity();
	
	/**
	 * 获取用户人气值流水
	 * @param query
	 * @return
	 * @throws ManagerException
	 */
	public Page<PopularityInOutLogDto> getPopularityInOutLog(BaseQueryParam query) ;
	
	/**
	 * 检查生日的状态
	 * @param birthday
	 * @return
	 */
	public int getBirthdayStatus(Date birthday);
	
	/**
	 * 设置用户签署方式
	 * @param memberId
	 * @return
	 */
	public ResultDTO<Member> saveSignWay(Long memberId, int sighWay);
	
	/**
	 * 
	 * @Description:设置支付密码
	 * @param memberId
	 * @param handleType
	 * @return
	 * @author: zhanghao
	 * @time:2016年8月5日 下午5:56:33
	 */
	public ResultDTO<String> handlePayPassword(Long memberId, int handleType,Integer source);
	/**
	 * 
	 * @Description:充值
	 * @param memberID
	 * @param ip
	 * @param bigDecimal
	 * @param type
	 * @param outAdvanceNo
	 * @return
	 * @author: chaisen
	 * @time:2016年8月8日 上午11:17:21
	 */
	public ResultDTO<Object> createHostingDepositAndRechargeLog(Long memberID,
			String ip, RechargeDto dto,Integer source );
	
	
	  /**
	 * 充值返回签名FORM， 并插入 充值流水(收银台)
	 * 
	 * @param memberId
	 * @param payerIp
	 * @param rechargeAmount
	 * @param type 是直接充值， 还是交易充值
	 * @return
	* @throws Exception
	*/
	public ResultDTO<Object> createHostingDepositAndRechargeLogInvestment(Long memberId, String payerIp, BigDecimal rechargeAmount, TypeEnum type,
			String orderNo,Integer source,Long orderId); 
	
	/**
	 * 
	 * @Description:提现
	 * @param memberID
	 * @param ip
	 * @param withdrawAmount
	 * @param freeWithdrawAmount
	 * @return
	 * @author: chaisen
	 * @time:2016年8月8日 上午11:42:06
	 */
	ResultDTO<Object> createWithdrawAndWithdrawLog(Long memberID, String ip,
			BigDecimal withdrawAmount, BigDecimal freeWithdrawAmount,Integer source,BigDecimal fee);
	/**
	 * 
	 * @Description:验证码方式修改手机号
	 * @param id
	 * @param newMobile
	 * @param mobile
	 * @return
	 * @author: chaisen
	 * @time:2016年8月8日 下午2:09:08
	 */
	ResultDTO<Object> modifyMobileByOldMobile(Long memberId, Long newMobile,
			Long mobile);
	/**
	 * 
	 * @Description:验证码方式修改手机号重置密码
	 * @param newMobile
	 * @param newPassword
	 * @return
	 * @author: chaisen
	 * @time:2016年8月8日 下午2:29:10
	 */
	ResultDTO<Object> modifyPasswordByNewMobile(Long newMobile,
			String newPassword);
	/**
	 * 
	 * @Description:身份信息修改手机号，验证信息
	 * @param mobile
	 * @param trueName
	 * @param identityNumber
	 * @param password
	 * @return
	 * @author: chaisen
	 * @time:2016年8月8日 下午2:37:02
	 */
	public ResultDTO<Object> validateMemberInfo(Long mobile, String trueName,
			String identityNumber, String password);
    /**
     * 
     * @Description:身份信息修改手机号
     * @param mobile
     * @param newMobile
     * @param trueName
     * @param identityNumber
     * @return
     * @author: chaisen
     * @time:2016年8月8日 下午2:43:28
     */
	ResultDTO<Object> modifyMobileByIdentity(Long mobile, Long newMobile,
			String trueName, String identityNumber);
	/**
	 * 
	 * @Description:身份信息修改手机号重置密码
	 * @param newMobile
	 * @param trueName
	 * @param identityNumber
	 * @param newPassword
	 * @return
	 * @author: chaisen
	 * @time:2016年8月8日 下午2:49:30
	 */
	ResultDTO<Object> modifyPasswordByIdentity(Long newMobile, String trueName,
			String identityNumber, String newPassword);
	
	
	/**
	 * 
	 * @Description:同步支付密码状态
	 * @param memberId
	 * @param handleType
	 * @return
	 * @author: zhanghao
	 * @time:2016年8月5日 下午5:56:33
	 */
	public ResultDTO<Boolean> synPayPasswordFlag(Long memberId);
	
	/**
	 * 委托扣款授权处理
	 * 
	 * @param memberID
	 * @param type 操作类型：1：开通委托扣款, 2：关闭委托扣款
	 * @return
	 */
	public ResultDTO<String> handWithholdAuthority(Long memberID, int type,Integer source,int mType,Long orderId);
	
	/**
	 * 查询是否开通委托扣款并同步
	 * 
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public ResultDTO<Boolean> synWithholdAuthority(Long memberId);
	
	
	public ResultDTO<Object> showMemberInfosSina(Long memberId,Integer source);
	/**
	 * 
	 * @param 查询自动投标
	 * @return
	 */
	public ResultDTO<AutoInvestSet> queryAutoInvest(Long memberId);
	/**
	 * 自动投标设置
	 * @param memberId
	 * @param investFlag
	 * @return
	 */
	public Object autoInvestSet(Long memberId, int investFlag);
	/**
	 * 保存自动投标信息
	 * @param autoInvestDto
	 * @return
	 */
	ResultDTO<AutoInvestSet> saveAutoInvestSetByMemberId(
			AutoInvestSetDto autoInvestDto);

	int countRegisterNumberByDate(Date date);
	
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
