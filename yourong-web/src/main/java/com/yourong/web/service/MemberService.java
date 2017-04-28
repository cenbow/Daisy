package com.yourong.web.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.enums.RechargeBankCode;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.model.AutoInvestSet;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberCheck;
import com.yourong.core.uc.model.MemberToken;
import com.yourong.core.uc.model.biz.MemberForLottery;
import com.yourong.core.uc.model.biz.ReferralBiz;
import com.yourong.web.dto.AutoInvestSetDto;
import com.yourong.web.dto.FinEaringDto;
import com.yourong.web.dto.MemberDto;
import com.yourong.web.dto.NoviceTaskDto;

public interface MemberService {

	Member selectByPrimaryKey(Long id);

	Member selectByUsername(String username);

	Member selectByMobile(Long mobile);
	
	ResultDO<Object>  updatePassword(Long id,String oldpassword,String newPassword);
	
	ResultDO<Object> updatePasswordByMobile(Long id,Long mobile,String newPassword);
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
	ResultDO<Object> authIdentity(MemberDto memberDto)throws Exception;
	
	/**
	 * 根据用户的URL获得用户信息
	 * @param shortUrl
	 * @return
	 * @throws ManagerException
	 */
	public Member getMemberByShortUrl(String shortUrl);

    /**
	 * 充值返回签名FORM， 并插入 充值流水
	 * 
	 * @param memberID
	 * @param payerIp
	 * @param bigDecimal
	 * @param code
	 * @param type
	 *            是直接充值， 还是交易充值
	 * @return
	 * @throws Exception
	 */
	public String buildSigleFormAndRechargeLog(Long memberID, String payerIp, BigDecimal bigDecimal, RechargeBankCode code, TypeEnum type,
			String orderNo) throws Exception;
	
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
	public ResultDO<Object> createHostingDepositAndRechargeLog(Long memberId, String payerIp, BigDecimal rechargeAmount, TypeEnum type,
			String orderNo, boolean isMobileSource);

	/**
	 * 绑卡充值，并插入 充值流水
	 * 
	 * @param memberID
	 * @param payerIp
	 * @param bigDecimal
	 * @param
	 * @param type
	 *            是直接充值， 还是交易充值
	 * @return
	 * @throws Exception
	 */
	public ResultDO<Object> rechargeOnCardAndRechargeLog(Long memberID, String payerIp, BigDecimal bigDecimal, Long cardID, TypeEnum type,
			String orderNo) throws Exception;

	/**
	 * 绑卡支付  确认银行卡
	 * @param outAdvanceNo
	 * @param ticket
	 * @param validateCode
	 * @param userIp
	 * 
	 * @return
	 */
	public ResultDO<Object> rechargeOnCardAndCheck(String outAdvanceNo, String ticket, String validateCode, String userIp);


    /**
     * 保存用户头像
     * @param memberId 用户编号
     * @param avatarURL 用户URL
     * @return
     */
    public void saveAvatar(Long memberId, String avatarURL);
    
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
    public ResultDO<Member> saveUserName(Long memberId, String userName);

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
	ResultDO<Member> toBindEmail(Long memberId, String email);

	/**
	 * 通过邮箱查询用户
	 * @param email
	 * @return
	 */
	Member selectByEmail(String email);
	
	/**
	 * 获取用户推荐列表
	 * @param query
	 * @return
	 * @throws ManagerException
	 */
	public Page<ReferralBiz> getReferralBiz(BaseQueryParam query) ;

	/**
	 * 获取用户人气值列表
	 * @param
	 * @param
	 * @return
	 */
	public Page<PopularityInOutLog> getMemberPopularityInOutLogList(BaseQueryParam query);
	
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
	 * 用户签到
	 * @param memberId
	 * @param loginSource 
	 * @return
	 * @throws Exception
	 */
	ResultDO<MemberCheck> memberCheck(Long memberId, int loginSource) throws Exception;
	
	/**
	 * 新浪存钱罐查看验证 返回签名的FORM
	 */
	public String getAuditMemberInfosForm(Long memberId) throws Exception;
	
	/**
	 * 
	 * @param openId
	 * @return
	 */
	public MemberToken getWeiXinMemberToken(String openId);
	
	/**
	 * 新手任务状态
	 * @param memberId
	 * @return
	 */
	public ResultDO getNoviceTaskStatus(Long memberId);
	
	
	/**
	 * 新手任务
	 * @param memberId
	 * @return
	 */
	public List<NoviceTaskDto> getNoviceTaskList(Long memberId);
	
	/**
	 * 检查生日的状态
	 * @param birthday
	 * @return
	 */
	public int getBirthdayStatus(Date birthday);
	
	/**
	 * 获得生日专题活动
	 * @return
	 */
	public Activity getBirthdayActivity();
	
	/**
	 * 统计用户出借人身份下的债权数量
	 * @param memberId
	 * @return
	 */
	public boolean countDebtByLenderId(Long memberId);
	/**
	 * 获取用户签署方式
	 * @param memberId
	 * @return
	 */
	public Integer getMemberSignWay(Long memberId);
	/**
	 * 设置用户签署方式
	 * @param memberId
	 * @return
	 */
	public ResultDO<Member> saveSignWay(Long memberId, int sighWay);
	
	/**
	 * 
	 * @Description:同步是否设置支付密码
	 * @param memberId
	 * @return true 已设置，false 未设置（或者第三方查询失败）
	 * @author: wangyanji
	 * @time:2016年7月15日 下午2:15:11
	 */
	public Object synPayPasswordFlag(Long memberId);
	
	/**
	 * 委托扣款授权处理
	 * 
	 * @param memberID
	 * @param type 操作类型：1：开通委托扣款, 2：关闭委托扣款
	 * @return
	 */
	public ResultDO<String> handWithholdAuthority(Long memberID, int type);
	
	/**
	 * 查询是否开通委托扣款并同步
	 * 
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public ResultDO<Boolean> synWithholdAuthority(Long memberId);
	
	/**
	 * 
	 * @Description:设置支付密码
	 * @param memberId
	 * @param handleType
	 * @return
	 * @author: wangyanji
	 * @time:2016年7月15日 下午5:56:33
	 */
	public Object handlePayPassword(Long memberId, int handleType);
	
    /**
     * 提现发起返回签名FORM， 并插入提现记录
     * 
     * @param memberId 会员ID
     * @param userIp 会员提现IP
     * @param withdrawAmount 提现金额
     * @param freeWithdrawAmount 提现手续费
     * @return
     * @throws Exception
     */
	public ResultDO<Object> createWithdrawAndWithdrawLog(Long memberId, String userIp, BigDecimal withdrawAmount, 
			BigDecimal freeWithdrawAmount, BigDecimal fee) throws Exception;
	
	/**
	 * 根据原手机号修改手机号码
	 * 
	 * @param memberId
	 * @param newMobile
	 * @param oldMobile
	 * @return
	 * @throws Exception
	 */
	public ResultDO<Object> modifyMobileByOldMobile(Long memberId, Long newMobile, Long oldMobile);
	
	/**
	 * 修改手机号检验会员信息
	 * 
	 * @param oldMobile
	 * @param trueName
	 * @param identityNumber
	 * @param password
	 * 
	 * @author luwenshan 2016-08-04
	 * @return
	 * @throws ManagerException
	 * 
	 */
	public ResultDO<Object> validateMemberInfo(Long oldMobile, String trueName, String identityNumber, String password) throws ManagerException;
	
	/**
	 * 根据身份信息修改手机号码
	 * 
	 * @param oldMobile
	 * @param newMobile
	 * @param trueName
	 * @param identityNumber
	 * 
	 * @author luwenshan 2016-08-04
	 * @return
	 */
	public ResultDO<Object> modifyMobileByIdentity(Long oldMobile, Long newMobile, String trueName, String identityNumber);
	
	/**
	 * 根据新手机号重置登录密码
	 * 
	 * @param newMobile
	 * @param newPassword
	 * 
	 * @author luwenshan 2016-08-04
	 * @return
	 */
	public ResultDO<Object> modifyPasswordByNewMobile(Long newMobile, String newPassword);
	
	/**
	 * 根据新手机号相关信息重置登录密码
	 * 
	 * @param newMobile
	 * @param trueName
	 * @param identityNumber
	 * @param newPassword
	 * 
	 * @author luwenshan 2016-08-04
	 * @return
	 */
	public ResultDO<Object> modifyPasswordByIdentity(Long newMobile, String trueName, String identityNumber, String newPassword);
	/**
	 * 
	 * @Description:自动投标设置
	 * @param memberId
	 * @param investFlag
	 * @return
	 * @author: chaisen
	 * @time:2016年8月16日 上午11:40:29
	 */
	public ResultDO<Object> autoInvestSet(Long memberId, int investFlag);
	/**
	 * 
	 * @Description:保存投标设置信息
	 * @param autoInvestDto
	 * @return
	 * @author: chaisen
	 * @time:2016年8月17日 上午9:37:22
	 */
	public ResultDO<AutoInvestSet> saveAutoInvestSetByMemberId(AutoInvestSetDto autoInvestDto)throws ManagerException;
	/**
	 * 
	 * @Description:查询投标设置信息
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年8月17日 上午10:16:11
	 */
	public ResultDO<AutoInvestSet> queryAutoInvest(Long memberId);
	
	
	/**
	 * 
	 * @Description:获取用户信息新浪展示页面
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年8月12日 下午15:38:33
	 */
	public ResultDO<Object> showMemberInfosSina(Long memberId) ;

	/**
	 * 
	 * @Description:自动投标回调处理
	 * @param transaction
	 * @author: chaisen
	 * @time:2016年8月18日 上午9:28:57
	 */
	public void autoInvestSetAfterTransaction(Transaction transaction, Long orderId);

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
