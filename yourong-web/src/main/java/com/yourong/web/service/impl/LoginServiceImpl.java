package com.yourong.web.service.impl;

import com.yourong.core.mc.manager.ActivityLotteryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.pay.PayMentService;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.MemberType;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Identities;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.lottery.LotteryEngine;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.uc.manager.MemberLoginManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberNotifySettingsManager;
import com.yourong.core.uc.manager.MemberReferManager;
import com.yourong.core.uc.manager.MemberTokenManager;
import com.yourong.core.uc.manager.MemberVerifyManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberLogin;
import com.yourong.core.uc.model.MemberRefer;
import com.yourong.core.uc.model.MemberToken;
import com.yourong.core.uc.model.MemberVerify;
import com.yourong.web.dto.LoginDto;
import com.yourong.web.dto.MemberDto;
import com.yourong.web.service.BalanceService;
import com.yourong.web.service.LoginService;

/**
 * 登录，用户
 * @author pengyong
 *
 */
@Service
public class LoginServiceImpl implements LoginService{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MemberManager memberManager;
	@Autowired
	private TaskExecutor threadPool;
	@Autowired
	private MemberReferManager memberReferManager;
	@Autowired
	private MemberNotifySettingsManager memberNotifySettingsManager;
	@Autowired
	private PayMentService payMentService;
    @Autowired
    private SinaPayClient sinaPayClient;
	@Autowired
	private MemberLoginManager  memberLoginManager;
	@Autowired
	private MemberVerifyManager memberVerifyManager;

	@Autowired
	private MemberTokenManager memberTokenManager;

	
	@Autowired
	private BalanceService balanceService;

	@Autowired
	private ActivityLotteryManager activityLotteryManager;


	public ResultDO<Member> login(LoginDto dto){
		//账号，也有可能手机号码
		String username  = dto.getUsername();
		Member member  = null;
		ResultDO<Member>  result = new ResultDO<Member>();
		try {
			if(StringUtil.isNumeric(username)){
			        dto.setLoginType(1);
				member= memberManager.selectByMobile(Long.valueOf(username));			
			}else{
			        dto.setLoginType(2);
				member = memberManager.selectByUsername(username);
			}
			if(member == null ){
				result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return result;
			}
			
			Integer status = member.getStatus();
			if(status == 0){
				result.setResultCode(ResultCode.MEMBER_FOZONE_ERROR);
				return result;
			}
			if(status == 1){
				result.setResultCode(ResultCode.MEMBER_NOT_ACTIVATION_ERROR);
				return result;
			}
			if(status == 2){
				Long mobile = member.getMobile();			
				String decryptByYourong = CryptHelper.decryptByYourong(mobile.toString(), dto.getPassword());
				if(decryptByYourong.equals(member.getPassword())){
					result.setResult(member);
					result.setSuccess(true);
					dto.setMemberId(member.getId());					
					MemberLogin record = BeanCopyUtil.map(dto, MemberLogin.class);
					logger.info("会员id"+member.getId()+"登陆成功");
					memberLoginManager.insert(record);
					try{
						//60亿活动，登录就送1%收益券一张
						activityLotteryManager.sixBillionActAfterLogin(member);
					}catch(Exception e){
						logger.error("【庆60亿，抢标奖励翻6倍！】首次登录可获得1%收益券一张异常  memberId={}", member.getId(), e);
					}
					if(StringUtil.isNotBlank(dto.getWeixinId())){
						MemberToken token = memberTokenManager.selectByDevice(dto.getWeixinId());
						if(token == null){
							//记录微信ID
							boolean exitsertAndWeixinIdTo = isExitsertAndWeixinIdTo(member.getId(), dto.getWeixinId());
							if(exitsertAndWeixinIdTo){
								result.setResultCode(ResultCode.MEMBER_IS_EXIT_WEIXIN_EROOR);
							}
						}else{
							result.setResultCode(ResultCode.WEIXIN_IS_UNBUNDLING_EROOR);
						}
					}

					SPParameter  parameter =new SPParameter();
					parameter.setMemberId(member.getId());
					SPEngine.getSPEngineInstance().run(parameter);
					return result;
				}else{
					logger.info("用户密码错误 手机号码：" + mobile);
					result.setResultCode(ResultCode.MEMBER_PASSWORD_ERROR);
					return result;
				}
			}			
		} catch (Exception e) {
			logger.error("验证用户：username="+dto.getUsername(), e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
			return result;
		} 	
			
		return result;
	}
	
	@Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Object> register(MemberDto memberDto) throws Exception {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			if(!StringUtil.equals(memberDto.getPassword(), memberDto.getRepassword(),true)){
				result.setResultCode(ResultCode.MEMBER_RE_PASSWORD_ERROR);
			    return result;	
			}
			Member member = this.memberManager.selectByMobile(memberDto.getMobile());
			if(member != null){
				result.setResultCode(ResultCode.MEMBER_PHONE_NUMBER_ISEXIST_ERROR);
				return result;
			}			
			int i = insertMemberAndPayAccount(memberDto);			
		} catch (Exception e) {	
			logger.error("插入会员失败  MemberDto="+memberDto, e);
			result.setSuccess(false);
			result.setResultCode(ResultCode.MEMBER_REGISTER_ERROR);
			throw e;
		}
		return result;
	}	
	/**
	 * 创建会员和 激活第三方支付账户
	 * 
	 * @param record
	 * @return
	 * @throws ManagerException
	 */

	private  int insertMemberAndPayAccount(MemberDto record)	throws ManagerException {
		Member member = BeanCopyUtil.map(record, Member.class);
		member.setRegisterTime(DateUtils.getCurrentDateTime());
		member.setDelFlag(Constant.ENABLE);
		member.setSex(StatusEnum.MEMBER_SEX_UN.getStatus());
		member.setStatus(StatusEnum.MEMBER_STATUS_ACTIVE.getStatus());
		member.setMemberType(1);
		if (StringUtil.isNotBlank(record.getWeixinId())){
			member.setRegisterMethod(StatusEnum.MEMBER_SOURCE_WEIXIN.getStatus());
		}else {
			member.setRegisterMethod(StatusEnum.MEMBER_SOURCE_WEB.getStatus());
		}
		if(StringUtil.isNotBlank(record.getReferCode())){
			Member m = memberManager.getMemberByShortUrl(record.getReferCode());
			if(m != null){
				member.setReferral(m.getId());
			}
		}
		String shortUrl = Identities.randomBase62(6);
		Member shotMember = memberManager.getMemberByShortUrl(shortUrl);
		if (shotMember == null) {
			member.setShortUrl(shortUrl);
		} else {
			member.setShortUrl(Identities.randomBase62(6));
		}
		Long mobile = member.getMobile();
		String password = member.getPassword();		
		String decryptByYourong = CryptHelper.decryptByYourong(	mobile.toString(), password);
		member.setPassword(decryptByYourong);
		member.setRegisterIp(record.getIp());
		int i = memberManager.insertSelective(member);
		if (i > 0) {
			try {
				String name = SerialNumberUtil.PREFIX_UID + member.getId();
                ResultDto<?> activateMemberResult = sinaPayClient.createActivateMember(member.getId(), MemberType.PERSONAL, record.getIp());
                if(activateMemberResult != null  && activateMemberResult.isSuccess()){
                	return i;
                }else{
                	String message = activateMemberResult == null? "返回结果异常":activateMemberResult.getErrorMsg();
                	logger.error("创建会员异常,异常原因：message=" + message);
                	throw new ManagerException("创建会员异常,异常原因：message=" + message);
                }          

			} catch (Exception e) {
				i = 0;
				logger.error("调用第三方支付出现异常", e);
				throw new ManagerException("调用第三方支付出现异常", e);
			}
		}

		return i;
	}
	
	private class MemberRegisterServiceThread implements Runnable{
		private MemberDto memberDto;
		private Member member;
		public MemberRegisterServiceThread(final MemberDto memberDto){
			this.memberDto = memberDto;
		}
		
		@Override
		public void run() {
			registerService();
		}
		
		public void registerService(){
			try{
				member = memberManager.selectByMobile(memberDto.getMobile());
				logger.info("注册后续初始化线程开始，mobile={}",memberDto.getMobile());
				if(member != null){
					//推荐
					referService();
					//初始化用户消息配置项

					initMemberNotifySettings();		
					//初始化资金表
					balanceService.initBalance(member.getId(), TypeEnum.BALANCE_TYPE_PIGGY);
					//初始化存钱收益
					balanceService.initBalance(member.getId(), TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY);
					//初始化人气值
					balanceService.initBalance(member.getId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);	
					// 登陆日志
					MemberLogin memberLogin = new MemberLogin();
					memberLogin.setMemberId(member.getId());
					memberLogin.setLoginType(TypeEnum.MEMBER_LOGIN_TYPE_MOBILE.getType());
					memberLogin.setLoginSource(memberDto.getLoginSource());
					memberLogin.setUserAgent(memberDto.getUserAgent());
					memberLogin.setLoginIp(memberDto.getIp());
					memberLogin.setId(member.getId());
					logger.info("会员id"+member.getId()+"登陆成功");
					memberLoginManager.insert(memberLogin);				
					//插入手机认证信息
					MemberVerify memberVerify = new MemberVerify();
					memberVerify.setVerifyOperate(Constant.ENABLE);
					memberVerify.setVerifyType(TypeEnum.MEMBER_VERIFY_TYPE_MOBILE.getCode());
					memberVerify.setMemberId(member.getId());
					memberVerify.setVerifyContent(member.getMobile().toString());
					memberVerify.setClientIp(member.getRegisterIp());
					memberVerifyManager.insertSelective(memberVerify);
					//增加平台注册人数redis
					RedisPlatformClient.addMemberCount(1);
					
					//记录微信openId
					isExitsertAndWeixinIdTo(member.getId(), memberDto.getWeixinId());
					//邀请好友活动送收益券
					activityLotteryManager.inviteFriendNewUser(member.getId());
					SPParameter  parameter =new SPParameter();
	    			parameter.setMemberId(member.getId());
	    			SPEngine.getSPEngineInstance().run(parameter);
	    			//注册以前的活动奖励发放
	    			LotteryEngine.getEngineInstance().runForOutsider(member.getId(), member.getMobile());
				}
				logger.info("注册后续初始化线程结束，mobile={}",memberDto.getMobile());
			}catch(ManagerException ex){
				logger.error("注册服务失败",ex);
			}
		}

		/**
		 * 注册推荐
		 * @throws ManagerException
		 */
		private void referService() throws ManagerException{		    
			String referCode = memberDto.getReferCode();
			if(StringUtil.isNotBlank(referCode)  && referCode.length() == 6){
//				Long mobile = memberDto.getMobile();
//				if(StringUtil.isNumeric(referCode) && referCode.length() == 11){//手机
//					//手动输入手机号码,referCode 为不加掩码的 真实手机号码
//					Member m = memberManager.selectByMobile(Long.valueOf(referCode));
//					insertMemberRefer(m);
//				} else if(referCode.length() == 6){//推荐码固定是六位，如果超出就没必要去数据库找了
//				  //手动输入 邀请码  ，referCode 为 随机6位数字+字母
					Member m = memberManager.getMemberByShortUrl(referCode);
					insertMemberRefer(m);
//				}else if(memberDto.getReferral() != 0){
//				      //打开链接 邀请注册 ，referCode 为 139******1111;
//				    Member m = memberManager.selectByPrimaryKey(memberDto.getReferral());
//					insertMemberRefer(m);
//				}
			}
		}
		private void insertMemberRefer(Member m) throws ManagerException {
		    if(m != null){
		    	MemberRefer refer = new MemberRefer();
		    	refer.setReferral(m.getId());//推荐人
		    	refer.setReferred(member.getId());//被推荐人
		    	refer.setReferSource(memberDto.getReferSource());//推荐来源
		    	refer.setReferType(StatusEnum.MEMBER_REFER_TYPE_REGIST.getStatus());//推荐方式
		    	refer.setCreateTime(DateUtils.getCurrentDateTime());
		    	memberReferManager.insertSelective(refer);
		    	logger.info("用户"+m.getMobile()+"推荐"+member.getMobile()+"注册成功");
		    }
		}		
		/**
		 * 初始化消息通知配置项
		 * @throws ManagerException
		 */
		private void initMemberNotifySettings() throws ManagerException{
			memberNotifySettingsManager.initMemberNotifySettings(member.getId());
		}
	}

	@Override
	public void initOtherMemberData(MemberDto record) {		
		threadPool.execute(new MemberRegisterServiceThread(record));
	}


	private boolean  isExitsertAndWeixinIdTo(Long memberId,String openId)  {
		if (StringUtil.isBlank(openId)){
			return false;
		}
		if(StringUtil.isNotBlank(openId)){
			try {
				MemberToken token = memberTokenManager.selectWeixinTokenByMemberId(memberId);
				if(token != null ){
					return true ;
				}
				MemberToken token2 = memberTokenManager.selectByDevice(openId);
				if(token2 != null ){
					return true ;
				}
			} catch (ManagerException e) {
				logger.info("查询memberToken失败，memberId"+memberId+",openId:"+openId);
			}
		}
		MemberToken memberToken = new MemberToken();
		memberToken.setMemberId(memberId);
		memberToken.setDevice(openId);
		memberToken.setEquipment("weinxin");
		memberToken.setTokenType(3);
		memberToken.setToken(openId);
		try {
			memberTokenManager.insertSelective(memberToken);
			Long startDate = DateUtils.getDateTimeFromString("2015-06-06 00:00:00").getTime();
			Long currentDate = DateUtils.getCurrentDate().getTime();
			if(currentDate >= startDate){
				MessageClient.sendMsgForBindWeixin(memberId);
			}
		} catch (ManagerException e) {
			logger.info("插入memberToken失败，memberId"+memberId+",openId:"+openId);
		}
		return false;
	}


}
