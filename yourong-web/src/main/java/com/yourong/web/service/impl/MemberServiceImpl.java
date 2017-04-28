package com.yourong.web.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.MemberLogEnum;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.mail.SendMailService;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.pay.PayMentService;
import com.yourong.common.thirdparty.sinapay.SinaCashDeskClient;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.RechargeBankCode;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyType;
import com.yourong.common.thirdparty.sinapay.member.common.Constants;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.PayResult;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Identities;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.RandomUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.manager.RechargeLogManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.fin.model.RechargeLog;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AttachmentThread;
import com.yourong.core.handle.AvatarAttachmentHandle;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.manager.ActivityHistoryManager;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityHistory;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.push.PushEnum;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.ContractCoreManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.dao.AutoInvestSetMapper;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.manager.AutoInvestLogManager;
import com.yourong.core.uc.manager.AutoInvestSetManager;
import com.yourong.core.uc.manager.MemberBankCardManager;
import com.yourong.core.uc.manager.MemberCheckManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberTokenManager;
import com.yourong.core.uc.manager.MemberVerifyManager;
import com.yourong.core.uc.manager.VipDiscountManager;
import com.yourong.core.uc.model.AutoInvestLog;
import com.yourong.core.uc.model.AutoInvestSet;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBankCard;
import com.yourong.core.uc.model.MemberCheck;
import com.yourong.core.uc.model.MemberToken;
import com.yourong.core.uc.model.MemberVerify;
import com.yourong.core.uc.model.biz.MemberForLottery;
import com.yourong.core.uc.model.biz.ReferralBiz;
import com.yourong.core.upload.model.ImageConfig;
import com.yourong.web.dto.AutoInvestSetDto;
import com.yourong.web.dto.FinEaringDto;
import com.yourong.web.dto.MemberDto;
import com.yourong.web.dto.NoviceTaskDto;
import com.yourong.web.dto.RechargeLogDto;
import com.yourong.web.service.DouwanService;
import com.yourong.web.service.MemberService;
import com.yourong.web.utils.SysServiceUtils;
import com.yourong.web.utils.WebPropertiesUtil;

@Service
public class MemberServiceImpl implements MemberService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private MemberVerifyManager memberVerifyManager;
	
	@Autowired
	private TaskExecutor threadPool;
	
	@Autowired
	private PayMentService payMentService;

    @Autowired
    private SinaPayClient sinaPayClient;

    @Autowired
    private RechargeLogManager rechargeLogManager;
    
    @Autowired
   	private Map<String, List<ImageConfig>> imagesConfig;
    
    @Autowired
    private AvatarAttachmentHandle avatarAttachmentHandle;
	
    @Resource
	private ThreadPoolTaskExecutor taskExecutor;
    
    @Autowired
    private CouponManager couponManager;
    
    @Autowired 
    private SendMailService sendMailService;
    
    @Autowired
    private TransactionManager transactionManager;
    
    @Autowired
    private PopularityInOutLogManager popularityInOutLogManager;

	@Autowired
	private MemberBankCardManager memberBankCardManager;
	@Autowired
	private DouwanService douwanService;
	@Autowired
	private MemberCheckManager memberCheckManager;
	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private MemberTokenManager memberTokenManager;
	
	@Autowired
	private ActivityHistoryManager activityHistoryManager;
	
	@Autowired
	private ActivityManager activityManager;
	
	@Autowired
	private ActivityLotteryManager activityLotteryManager;
	
	@Autowired
	private DebtManager debtManager;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;
	
	@Autowired
	private MemberMapper memberMapper;

	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private TransactionMapper transactionMapper;
	
	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;
	
	@Autowired
    private SinaCashDeskClient sinaCashDeskClient;
	
	@Autowired
	private WithdrawLogManager withdrawLogManager;
	
	@Autowired
	private ContractCoreManager contractCoreManager;
	
	@Autowired
	private AutoInvestSetMapper autoInvestMapper;
	
	@Autowired
	private AutoInvestSetManager autoInvestSetManager;
	
	@Autowired
	private AutoInvestLogManager autoInvestLogManager;
	
	@Autowired
	private OrderManager orderManager;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private VipDiscountManager vipDiscountManager;
	
	private static final String YESAUTOINVESTFLAG = "Y";

		

	@Override
	public Member selectByPrimaryKey(Long id) {	
		Member member = null;
		try {
			member = memberManager.selectByPrimaryKey(id);			
		} catch (ManagerException e) {
			logger.error("查询会员失败  id="+id, e);
			
		}
		return member;		
	}

	@Override
	public Member selectByUsername(String username) {		
		Member member = null;
		try {
			member = memberManager.selectByUsername(username);			
		} catch (ManagerException e) {
			logger.error("查询会员失败  username="+username, e);
		
		}
		return member;	
	}



	@Override
	public Member selectByMobile(Long mobile) {		
		Member member = null;
		try {
			member = memberManager.selectByMobile(mobile);			
		} catch (ManagerException e) {
			logger.error("查询会员失败  mobile="+mobile, e);				
		}
		return member;	
	}

	@Override
	public ResultDO<Object> updatePassword(Long id, String oldpassword,
			String newPassword) {
		ResultDO result =  memberManager.updatePassword(id, oldpassword, newPassword);
		if(result.isSuccess()){
			cleanAppClientTokenAndPushMessage(id);
		}
		return result;
	}
	
	public ResultDO<Object> updatePasswordByMobile(Long id,Long mobile,String newPassword){
		ResultDO result = memberManager.updatePasswordByMobile(id, mobile, newPassword);
		if(result.isSuccess()){
			cleanAppClientTokenAndPushMessage(id);
		}
		return result;
	}
	
	/**
	 * 清空APP端token
	 * @param memberId
	 */
	private void cleanAppClientTokenAndPushMessage(Long memberId)  {
		try {
			memberTokenManager.cleanAppClientTokenAndPushMessage(memberId,true,"您的密码已修改。", PushEnum.MEMBER_UPDATE_PASSWORD);
		} catch (ManagerException e) {
			logger.error("清空app端token失败,memberID:" + memberId, e);
		}
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Object> authIdentity(MemberDto memberDto) throws Exception{
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			if(StringUtil.isBlank(memberDto.getTrueName())|| StringUtil.isBlank(memberDto.getIdentityNumber())){
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				resultDO.setResult(ResultCode.MEMBER_AUTH_IDENTITY_EXIT.getMsg());
				resultDO.setSuccess(false);
				return resultDO;
			}		
			if(memberManager.isExitMemberByIdentityNumber(memberDto.getIdentityNumber())){
				resultDO.setResultCode(ResultCode.MEMBER_AUTH_IDENTITY_EXIT);
				resultDO.setResult(ResultCode.MEMBER_AUTH_IDENTITY_EXIT.getMsg());
				resultDO.setSuccess(false);
				return resultDO;
			}
			Date addYearsApart = DateUtils.addYearsApart(DateUtils.getBirthdayByIdentity(memberDto.getIdentityNumber()),18);    	
	    	if(addYearsApart.after(DateUtils.getCurrentDate())){	    	
				resultDO.setResultCode(ResultCode.MEMBER_IDENTITYNUMBER_ERROR);
				resultDO.setResult(ResultCode.MEMBER_IDENTITYNUMBER_ERROR.getMsg());
				resultDO.setSuccess(false);
				return resultDO;
			}
					
            authThirdPay(memberDto.getId(), memberDto.getTrueName(),memberDto.getIdentityNumber(), resultDO,memberDto.getMobile(), memberDto.getIp());
            if(resultDO.isSuccess()) {
                resultDO = insertAutch(memberDto);
                //绑定后送红包
                //couponManager.sendCoupon(memberDto.getId(), TypeEnum.COUPON_SEND_SOURCE_TYPE_IDENTITY_BIND, null);
                
//                SPParameter  parameter =new SPParameter();
//    			parameter.setMemberId(memberDto.getId());
//    			SPEngine.getSPEngineInstance().run(parameter);
				//都玩回调接口
				douwanService.douwanRegisteredCallBack(memberDto.getId());
				MessageClient.sendMsgForRegSina(DateUtils.getCurrentDate(), new BigDecimal(518), memberDto.getId());
            }
		} catch (Exception e) {
			// 认证失败
			resultDO.setResultCode(ResultCode.MEMBER_AUTH_IDENTITY_ERROR);
			resultDO.setResult(ResultCode.MEMBER_AUTH_IDENTITY_EXIT.getMsg());
			throw e;
		}
		return resultDO;
	}

	
	public ResultDO<Object> insertAutch(MemberDto memberDto) throws ManagerException{
	    ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			//更新身份证已经生日信息
			Date birthday = DateUtils.getBirthdayByIdentity(memberDto.getIdentityNumber());
			int sex = StringUtil.getSexByIdentityNumber(memberDto.getIdentityNumber());
			int rs = memberManager.updateIdentityNumberById(memberDto.getTrueName(),memberDto.getIdentityNumber(), memberDto.getId(), birthday, sex);
			if(rs > 0){
				//添加认证信息
				MemberVerify record = new MemberVerify();
				//BeanCopyUtil.copy(record, memberDto.getMemberVerifyDto());
				record.setVerifyOperate(Constant.ENABLE);
				record.setVerifyType(TypeEnum.MEMBER_VERIFY_TYPE_IDENTITY.getCode());
				record.setMemberId(memberDto.getId());
				record.setVerifyContent(memberDto.getTrueName()+StringUtil.CARET+memberDto.getIdentityNumber());
				record.setCreateTime(DateUtils.getCurrentDateTime());
				memberVerifyManager.insertSelective(record);
				resultDO.setSuccess(true);
			}else{
				//认证失败
				resultDO.setResultCode(ResultCode.MEMBER_AUTH_IDENTITY_ERROR);
			}
		} catch (ManagerException e) {
			logger.error(ResultCode.MEMBER_AUTH_IDENTITY_ERROR.getMsg(), e);
			throw new ManagerException(e);
			
		}
		return resultDO;
	}
	
	private void authThirdPay(long memberID, String realName,String certificateNumber, ResultDO<Object> resultDO, Long mobie, String registerIp) throws Exception {
		//绑定手机
        String verifyEntity = mobie.toString();
	        
        ResultDto<?> verify = sinaPayClient.queryVerify(memberID, VerifyType.MOBILE);
        if(verify!= null && Constants.VERIFY_NOT_EXIST.equalsIgnoreCase(verify.getErrorCode())){
        	 ResultDto<?> resultDto = sinaPayClient.bindingVerify(memberID, verifyEntity, VerifyType.MOBILE, registerIp);
             if (!resultDto.isSuccess()) {
                 resultDO.setSuccess(false);
                 resultDO.setResult(resultDto.getErrorMsg());
                 return;
             }
        }        
		 //实名认证
        ResultDto<?> dto = sinaPayClient.setRealName(memberID, VerifyStatus.Y, certificateNumber, realName, registerIp);
        if (dto== null || dto.isError()) {
            resultDO.setSuccess(false);
            resultDO.setResult(dto.getErrorMsg());
            return;
        }
		       
	}
		
	@Override
	public boolean isAuthIdentityAndPhone(Long id) {
		try {
			Member member = memberManager.selectByPrimaryKey(id);
			if (member != null
					&& StringUtil.isNotBlank(member.getIdentityNumber())
					&& member.getMobile() != null) {
				return true;
			}
		} catch (ManagerException e) {
			logger.error("查询会员失败  id=" + id, e);
		}
		return false;
	}

	@Override
	public Member getMemberByShortUrl(String shortUrl) {
	    	Member member = null;
		try {
			member = memberManager.getMemberByShortUrl(shortUrl);			
		} catch (ManagerException e) {
			logger.error("查询会员失败  shortUrl="+shortUrl, e);				
		}
		return member;
	}

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public String buildSigleFormAndRechargeLog(Long memberID, String payerIp, BigDecimal bigDecimal, RechargeBankCode code, TypeEnum type,
			String orderNo) throws Exception {
		if (bigDecimal != null) {
			bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
		}   	
        String tradeNo = SerialNumberUtil.generateRechargeNo(memberID);
		String result = sinaPayClient.createDepositByBank(tradeNo, memberID, payerIp, bigDecimal, code);
        if (StringUtil.isNotBlank(result)) {
        	BigDecimal fee = bigDecimal.multiply(new BigDecimal(Config.poundage));
        	if(fee == null || fee.compareTo(new BigDecimal(Config.minrecharge) ) < 1){
        		fee = new BigDecimal(Config.minrecharge);
        	}
            RechargeLog record = new RechargeLog();
            record.setAmount(bigDecimal);
            record.setBankCode(code.name());
            record.setMemberId(memberID);
            record.setPayMethod(Constant.SINA_PAY);
            record.setRechargeNo(tradeNo);
            record.setFee(fee);
            record.setOrderNo(orderNo);
            record.setType(type.getType());
            record.setStatus(StatusEnum.RECHARGE_STATUS_PROESS.getStatus());
			record.setPayerIp(payerIp);
            rechargeLogManager.insertSelective(record);
        }else{
            logger.error("充值生成签名异常 memberID = "+memberID);
            throw new ManagerException("充值生成签名异常memberID" +memberID);
        }
        return result;

    }
    
    /**
     * 托管充值(收银台模式)
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Object> createHostingDepositAndRechargeLog(Long memberId, String payerIp, BigDecimal rechargeAmount, TypeEnum type,
			String orderNo, boolean isMobileSource) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			String tradeNo = SerialNumberUtil.generateRechargeNo(memberId);
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("payerIp", payerIp);
			paramsMap.put("type", type.getType());
			paramsMap.put("tradeNo", tradeNo);
			paramsMap.put("returnUrl", WebPropertiesUtil.getSinaCashDeskReturnUrl());
			paramsMap.put("isMobileSource", isMobileSource);
			paramsMap.put("rechargeSource", TypeEnum.MEMBER_LOGIN_SOURCE_PC.getType());
			resultDO = memberManager.createHostingDeposit(memberId, rechargeAmount, orderNo, paramsMap);
		} catch (Exception e) {
			logger.error("托管充值(收银台模式), memberId={}, payerIp={}, rechargeAmount={}, type={}, orderNo={}", memberId, payerIp, rechargeAmount,
					type.getType(), orderNo, e);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
    	return resultDO;
    }
    
	@Override
	public ResultDO<Object> rechargeOnCardAndRechargeLog(Long memberID, String payerIp, BigDecimal bigDecimal, Long cardID, TypeEnum type,
			String orderNo) throws Exception {
		ResultDO<Object> result = new ResultDO<Object>();
		if (!memberBankCardManager.isExist(cardID, memberID)) {
			result.setResultCode(ResultCode.MEMBER_USERNAME_WRONG_BANKID);
			return result;
		}
		if (bigDecimal != null) {
			bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		MemberBankCard memberBankCard = memberBankCardManager.selectByPrimaryKey(cardID);
		String tradeNo = SerialNumberUtil.generateRechargeNo(memberID);
		ResultDto<PayResult> payResult = sinaPayClient.createDeposit(tradeNo, memberID, payerIp, bigDecimal,
				memberBankCard.getOuterSourceId());
		if (payResult.isError()){
			
			BigDecimal fee = bigDecimal.multiply(new BigDecimal(Config.poundage));
			if(fee == null || fee.compareTo(new BigDecimal(Config.minrecharge) ) < 1){
				fee = new BigDecimal(Config.minrecharge);
			}
			RechargeLog record = new RechargeLog();
			record.setAmount(bigDecimal);
			record.setBankCode(memberBankCard.getBankCode());
			record.setMemberId(memberID);
			record.setPayMethod(Constant.CARD_PAY);
			record.setRechargeNo(tradeNo);
			record.setFee(fee);
			record.setOrderNo(orderNo);
			record.setType(type.getType());
			record.setStatus(StatusEnum.RECHARGE_STATUS_PROESS.getStatus());
			record.setBankCardId(cardID);
			record.setPayerIp(payerIp);
			int i = rechargeLogManager.insertSelective(record);
			
			result.setSuccess(false);
			result.setResultCode(ResultCode.SINA_PAY_ERROR);
			result.setResult(payResult.getErrorMsg());
			logger.info("绑卡支付失败：memberID"+memberID+": error"+payResult.getErrorMsg());
			return  result;
		}
		if (payResult.isSuccess()) {
			BigDecimal fee = bigDecimal.multiply(new BigDecimal(Config.poundage));
			if(fee == null || fee.compareTo(new BigDecimal(Config.minrecharge) ) < 1){
				fee = new BigDecimal(Config.minrecharge);
			}
			RechargeLog record = new RechargeLog();
			record.setAmount(bigDecimal);
			record.setBankCode(memberBankCard.getBankCode());
			record.setMemberId(memberID);
			record.setPayMethod(Constant.CARD_PAY);
			record.setRechargeNo(tradeNo);
			record.setFee(fee);
			record.setOrderNo(orderNo);
			record.setType(type.getType());
			record.setStatus(StatusEnum.RECHARGE_STATUS_PROESS.getStatus());
			record.setBankCardId(cardID);
			record.setPayerIp(payerIp);
			int i = rechargeLogManager.insertSelective(record);
			if(i>0){
				RechargeLog rechargeLog = rechargeLogManager.getRechargeLogByTradeNo(tradeNo);
				RechargeLogDto rechargeLogDto = BeanCopyUtil.map(rechargeLog, RechargeLogDto.class);
				rechargeLogDto.setTicket(payResult.getModule().getTicket());
				result.setSuccess(true);
				result.setResult(rechargeLogDto);
			}else {
				result.setSuccess(false);
				result.setResultCode(ResultCode.ERROR_SYSTEM_DATABASE_INSERT);
			}
		}
		return result;
	}

	public ResultDO<Object> rechargeOnCardAndCheck(String outAdvanceNo, String ticket, String validateCode, String userIp) {
		ResultDO<Object> resultDO = new ResultDO<>();
		try {
			ResultDto<?> resultDto = this.sinaPayClient.advanceHostingPay(outAdvanceNo, ticket, validateCode, userIp);
			if (resultDto.isSuccess()) {
				resultDO.setSuccess(true);
			} else {
				resultDO.setSuccess(false);
				resultDO.setResult(resultDto.getErrorMsg());
				resultDO.setResultCode(ResultCode.MEMBER_BANK_ADVACE_CODE);
				logger.info("支付推进失败：outAdvanceNo"+outAdvanceNo+": error"+resultDto.getErrorMsg());
			}
		} catch (Exception e) {
			logger.error("绑卡支付异常,outAdvanceNo=" + outAdvanceNo, e);
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.SINA_PAY_ERROR);
		}
		return resultDO;
	}
	@Override
	public void saveAvatar(Long memberId, String avatarURL) {
		AttachmentInfo info = new AttachmentInfo();
		info.setKeyId(memberId.toString());
		info.setAppPath(avatarURL);
		taskExecutor.execute(new AttachmentThread(avatarAttachmentHandle, info));
	}

	@Override
	public String getMemberAvatar(Long id) {
		String avatar = RedisMemberClient.getMemberAvatar(id);
		try {
			if(StringUtil.isBlank(avatar)){
				avatar = memberManager.getMemberAvatar(id);
				if(StringUtil.isNotBlank(avatar)){
					RedisMemberClient.setMemberAvatar(id, avatar);
					logger.info("用户头像加入缓存 memberID+"+id);
				}
			}
		} catch (ManagerException e) {
			logger.error("获取用户头像异常 memberID = "+id);
		}
		return avatar;
	}

	@Override
	public ResultDO<Member> saveUserName(Long memberId, String userName) {
		ResultDO<Member> result = new ResultDO<Member>();
		try {
			//只允许中文和字母,后台长度限制2~10。
			String regx= "^[\u4E00-\u9FA5\uf900-\ufa2dA-Za-z]{2,14}$";
			if(!Pattern.matches(regx, userName)){
				result.setResultCode(ResultCode.MEMBER_USERNAME_CHECK_ERROR);
				return result;
			}
			//是否包含敏感词
			if(isFullMatchFilterWords(userName) || isFilterWords(userName)){
				result.setResultCode(ResultCode.MEMBER_USERNAME_FILTER_WORDS_ERROR);
				return result;
			}
			Member member = selectByPrimaryKey(memberId);
			if(member != null){
				//昵称只允许设置一次
				if(StringUtils.isNotEmpty(member.getUsername())){
					result.setResultCode(ResultCode.ERROR);
					logger.error("用户昵称不允许重复设置 memberId:"+memberId);
					return result;
				}
				//昵称唯一约束
				member = selectByUsername(userName);
				if(member != null){
					result.setResultCode(ResultCode.MEMBER_USERNAME_IS_EXIST);
					return result;
				}
				Member m = new Member();
				m.setId(memberId);
				m.setUsername(userName);
				memberManager.updateByPrimaryKeySelective(m);
				memberManager.sendCouponOnCompleteMemberInfo(memberId);
				RedisMemberClient.setMemberUserName(memberId, userName);
			}
		} catch (ManagerException e) {
			logger.error("设置用户昵称异常userName="+userName);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Member> bindEmail(String emailToken) throws Exception{
		ResultDO<Member> result = new ResultDO<Member>();
		String emailAndMemberIdFromReis = RedisMemberClient.getEmailToken(emailToken);
		//判断token是否失效
		if(StringUtil.isBlank(emailAndMemberIdFromReis)) {
			result.setResultCode(ResultCode.MEMBER_EMAIL_TOKEN_VAILD);
			return result;
		}
		String[] emailAndMemberIdFromReisArr = emailAndMemberIdFromReis.split(RedisConstant.REDIS_SEPERATOR);
		Long memberId = Long.parseLong(emailAndMemberIdFromReisArr[0]);
		String email = emailAndMemberIdFromReisArr[1];
		try {
			Member memberByEmail = memberManager.selectByEmail(email);
			if(memberByEmail!=null){
				result.setResultCode(ResultCode.MEMBER_EMAIL_IS_BINDED);
				logger.info("该邮箱已经绑定, memberId="+memberId);
				return result;
			}
			
			Member member = selectByPrimaryKey(memberId);
			if(member != null){
				// 如果第一次绑定邮箱，送优惠券
				if(StringUtil.isBlank(member.getEmail())) {
					//couponManager.sendCoupon(memberId, TypeEnum.COUPON_SEND_SOURCE_TYPE_EMAIL_BIND, null);
					//活动引擎
					SPParameter parameter = new SPParameter();
					parameter.setMemberId(member.getId());
					SPEngine.getSPEngineInstance().run(parameter);
					//都玩回调接口
					douwanService.douwanEmailBingCallBack(member.getId());
				}
				member.setEmail(email);
				memberManager.updateByPrimaryKeySelective(member);
				//添加认证信息
				MemberVerify memberVerify = new MemberVerify();
				memberVerify.setVerifyOperate(Constant.ENABLE);
				memberVerify.setVerifyType(TypeEnum.MEMBER_VERIFY_TYPE_EMAIL.getCode());
				memberVerify.setMemberId(member.getId());
				memberVerify.setVerifyContent(member.getTrueName()+StringUtil.CARET+email);
				memberVerifyManager.insertSelective(memberVerify);
				//TODO 暂时不调用新浪绑定邮箱接口
//				if(sinaPayClient.bindingVerify(memberId, email, VerifyType.EMAIL).isSuccess()) {
					//清除redis中token
				RedisMemberClient.removeEmailToken(emailToken);
				result.setResult(member);
//				}
			}
		} catch (Exception e) {
			logger.error("会员绑定邮箱异常, memberId="+memberId + ", email=" + email);
			throw e;
		}
		return result;
	}

	@Override
	public ResultDO<Member> toBindEmail(Long memberId, String email) {
		ResultDO<Member> result = new ResultDO<Member>();
		try {
			Member memberByEmail = memberManager.selectByEmail(email);
			if(memberByEmail!=null){
				result.setResultCode(ResultCode.MEMBER_EMAIL_IS_BINDED);
				logger.info("该邮箱已经绑定, memberId="+memberId);
				return result;
			}
			Member member = selectByPrimaryKey(memberId);
				if(member != null){
				//生成email token，并且保持到redis
				String emailToken = Identities.randomBase62(64);
				Map<String, Object> params = Maps.newHashMap();
				String verifyTime = DateUtils.formatDatetoString(new Date(), DateUtils.TIME_PATTERN);
				String key = CryptHelper.encryptByase(member.getShortUrl());
				String emailUnsubscribeCode = RedisMemberClient.getEmailUnsubscribe(key);
				if(StringUtil.isBlank(emailUnsubscribeCode)){
					RedisMemberClient.setEmailUnsubscribe(member.getId(), key);
				}
				String username = member.getUsername();
				if(StringUtil.isBlank(member.getUsername())) {
					username = member.getMobile().toString();
				}
				params.put("emailToken", emailToken);
				params.put("verifyTime", verifyTime);
				params.put("email", email);
				params.put("username", username);
				params.put("url", SysServiceUtils.getRootURL());
				params.put("emailUnsubscribeCode", key);
				
				sendMailService.sendMailNow(email, "有融网-邮箱地址验证", "bindEmail", params);
				RedisMemberClient.setEmailToken(memberId, email, emailToken);
				String logs = String.format("邮箱验证memberId=%s,email=%s,emailToken=%s", memberId,email,emailToken);
				logger.info(logs);
			}
		} catch (Exception e) {
			logger.error("会员绑定邮箱异常, memberId="+memberId + ", email=" + email);
		}
		return result;
		
	}

	@Override
	public Member selectByEmail(String email) {
		try {
			return memberManager.selectByEmail(email);	
		} catch (ManagerException e) {
			logger.error("通过邮箱查询用户失败  email="+email, e);				
		}
		return null;
	}

	@Override
	public Page<ReferralBiz> getReferralBiz(BaseQueryParam query) {
		try {
			Page<Member> memberForPage = memberManager.getReferralMemberById(query);
			List<Member> members = memberForPage.getData();
			if(Collections3.isNotEmpty(members)) {
				List<ReferralBiz> referralBizs = Lists.newArrayList();
				for (Member member : members) {
					ReferralBiz referralBiz = new ReferralBiz();
					referralBiz.setUsername(member.getUsername());
					referralBiz.setMobile(member.getMobile());
					referralBiz.setEmailBind(member.isEmailBind());
					referralBiz.setMemberId(query.getMemberId());
					referralBiz.setReferralId(member.getId());
					referralBiz.setReferralRegisterTime(DateUtils.getStrFromDate(member.getRegisterTime(), DateUtils.TIME_PATTERN));
					referralBiz.setSavingPotOpen(member.isSavingPotOpen());
					BigDecimal totalInvestAmount = RedisMemberClient.getTotalInvestAmount(member.getId());
					if(totalInvestAmount!=null && totalInvestAmount.doubleValue()>0) {
						referralBiz.setInvestment(true);
					}
					Optional<Activity> optAct = LotteryContainer.getInstance().getActivityByName(ActivityConstant.INVITATION_FRIENDS);
					if(optAct.isPresent()&&DateUtils.isDateBetween(DateUtils.getCurrentDate(),optAct.get().getStartTime(), optAct.get().getEndTime())){
						ActivityLotteryResult activityResult =new ActivityLotteryResult();
						activityResult.setActivityId(optAct.get().getId());
						activityResult.setMemberId(member.getId());
						activityResult.setRewardId(query.getMemberId().toString());
						
						ActivityLotteryResult result =new ActivityLotteryResult();
						result.setActivityId(optAct.get().getId());
						result.setMemberId(member.getId());
						result.setRewardId(query.getMemberId().toString());
						result.setRemark("counActivityBeginAomount");
						int countNum = activityLotteryResultManager.countRewrdsByMemberActivityRewardId(result);
						BigDecimal totalAmount=BigDecimal.ZERO;
						totalAmount=transactionManager.getTotalAmountByMemberId(member.getId(),optAct.get().getStartTime());
						if(totalAmount==null){
							totalAmount=BigDecimal.ZERO;
						}
						if(totalAmount.compareTo(BigDecimal.ZERO)>0){
							referralBiz.setInvestment(true);
						}
						if(countNum<1){
							ActivityLotteryResult result1 = new ActivityLotteryResult();
							result1.setActivityId(optAct.get().getId());
							result1.setMemberId(member.getId());
							result1.setRewardId(query.getMemberId().toString());
							result1.setRewardResult(String.valueOf(totalAmount));
							result1.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
							result1.setRemark("counActivityBeginAomount");
							if(totalAmount.compareTo(BigDecimal.ZERO)>0){
								activityLotteryResultMapper.insertSelective(result1);
							}
						}
						BigDecimal againTotalAmount=totalAmount.multiply(new BigDecimal(Config.investRecommendScale));
						BigDecimal amount=BigDecimal.ZERO;
						int couponNum = activityLotteryResultManager.sumRewrdsByMemberActivityRewardId(activityResult);
						amount=againTotalAmount.add(new BigDecimal(couponNum));
						referralBiz.setContributePopularityValue(againTotalAmount.add(new BigDecimal(couponNum)).intValue());
						if(amount.compareTo(BigDecimal.ZERO)>0){
							referralBiz.setInvestment(true);
						}
					}else{
						if(totalInvestAmount!=null && totalInvestAmount.doubleValue()>0) {
							referralBiz.setInvestment(true);
							referralBiz.setContributePopularityValue(totalInvestAmount.multiply(new BigDecimal(Config.investRecommendScale)).intValue());
						}
				}
					referralBizs.add(referralBiz);
				}
				Page<ReferralBiz> page = new Page<ReferralBiz>();
				page.setData(referralBizs);
				page.setiDisplayLength(query.getPageSize());
				page.setPageNo(query.getCurrentPage());
				page.setiTotalDisplayRecords(memberForPage.getiTotalDisplayRecords());
				page.setiTotalRecords(memberForPage.getiTotalRecords());
				return page;
			}
		} catch (ManagerException e) {
			logger.error("获取用户推荐列表失败  memberId="+query.getMemberId(), e);				
		}
		return null;
	}

	@Override
	public Page<PopularityInOutLog> getMemberPopularityInOutLogList(BaseQueryParam query) {
		try {
			//获取列表
			if(query.getType()!=null){
				if(query.getType().intValue()==1) {
					return popularityInOutLogManager.selectPopularityInOutLogForGain(query);
				}
				//兑换列表
				if(query.getType().intValue()==2) {
					return popularityInOutLogManager.selectPopularityInOutLogForExchange(query);
				}
			}
		} catch (Exception e) {
			logger.error("获取用户人气值列表发生异常, memberId=", e);
		}
		return null;
	}

	
	/**
	 * 是否过滤词
	 * @param
	 * @return
	 */
	private boolean isFilterWords(String word) {
		//直接从文件读取比在redis更快
		String path = this.getClass().getClassLoader().getResource("").getFile()+File.separator+"minGanCi.txt";
		try {
			List<String> minGanCiList = FileUtils.readLines(new File(path));
			if(Collections3.isNotEmpty(minGanCiList)){
				for(String s : minGanCiList){
					if(s.equals(word)){
						return true;
					}
				}
			}
		} catch (IOException e) {
			logger.error("未找到文件"+path,e);
		}
		return false;
	}
	
	/**
	 * 模糊匹配过滤
	 * @param
	 * @return
	 */
	private boolean isFullMatchFilterWords(String word) {
		String filterWords = PropertiesUtil.getProperties("filter.words");
		if(StringUtil.isNotBlank(filterWords)){
			String filterWordArray[] = filterWords.split(",");
			for(String _filterWord : filterWordArray){
				if(word.indexOf(_filterWord) !=-1){
					return true;
				}
			}
		}
		return false;
	}

	
	public boolean isReceiveMemberInfoCompleteCoupon(Long memberId){
		String status = RedisMemberClient.getMemberInfoCompleteStatus(memberId);
		if(StringUtil.isBlank(status) || status.equals("false")){
			try {
				//活动是否参与
				boolean inActivity = activityHistoryManager.isParticipateInActivity(memberId,PropertiesUtil.getActivityCompletedMemberInfoId());
				if(inActivity){
					RedisMemberClient.setMemberInfoCompleteStatus(memberId, true);
					return true;
				}
				//优惠券是否赠送
				List<Coupon> coupon = couponManager.getCouponByMemberIdAndActivity(memberId, Long.parseLong(Config.activityIdForCompleteMemberInfo));
				if(Collections3.isNotEmpty(coupon)){
					RedisMemberClient.setMemberInfoCompleteStatus(memberId, true);
					return true;
				}
				
			} catch (ManagerException e) {
				logger.error("查询是否领取完善信息优惠卷异常",e);
			}
		}else if(status.equals("true")){
			return true;
		}
		return false;
	}

	@Override
	public List<MemberForLottery> getMembersForLottery() {
		try {
			List<MemberForLottery> memberForLotterys = memberManager.getMembersForLottery();
			return memberForLotterys;
		} catch (Exception e) {
			logger.error("获取昨日注册送彩票用户发生异常", e);
		}
		return null;
	}
	@Override
	public List<FinEaringDto> getfin() {
		// String fincode = Constant.FIN_CODE_SINA;
		// ResultDto<?> fundYield = sinaPayClient.queryFundYield(fincode);
		// QueryFinResponse module = (QueryFinResponse) fundYield.getModule();
		// 20140630^4.1234^1.2121|20140629^4.1234^1.2121
		String yieldList = RedisPlatformClient.getSinapaySevenDaysBonus();
		if (StringUtil.isNotBlank(yieldList)) {
			String[] array = StringUtils.split(yieldList,
					Constants.VERTICAL_LINE);
			List<FinEaringDto> list = Lists.newArrayList();
			int k = 0;
			for (String a : array) {
				FinEaringDto dto = new FinEaringDto();
				if (StringUtil.isNotBlank(a)) {
					String[] split = StringUtils.split(a,
							Constants.ANGLE_BRACKETS);
					dto.setDate(split[0]);
					if (NumberUtils.isNumber(split[1]))
						dto.setServenEaring(new BigDecimal(split[1]));
					if (NumberUtils.isNumber(split[2]))
						dto.setYearEaring(new BigDecimal(split[2]));
					list.add(dto);
				}
				k = k + 1;
				if (k >= 7) {
					break;
				}
			}
			return list;
		}
		return null;
	}

	@Override
	public String getMemberUserName(Long id) {
		String userName = RedisMemberClient.getMemberUserName(id);
		try {
			if(StringUtil.isBlank(userName)){
				Member member = memberManager.selectByPrimaryKey(id);
				if(member != null){
					if(StringUtil.isNotBlank(member.getUsername())){
						RedisMemberClient.setMemberUserName(id, member.getUsername());
						logger.info("用户昵称加入缓存 memberID+"+id);
					}else{
						userName = member.getMobile().toString();
					}
				}
			}
		} catch (ManagerException e) {
			logger.error("获取用户昵称异常 memberID = "+id);
		}
		return userName;
	}

	@Override
	public String getFormatMemberUserName(Long id) {
		String userName = getMemberUserName(id);
		if(StringUtil.isNumeric(userName) && userName.length() >= 11){//手机号码
			return StringUtil.maskString(userName, StringUtil.ASTERISK, 3, 2, 4);
		}else{
			return StringUtil.maskString(userName, StringUtil.ASTERISK, 1, 1, 3);
		}
	}

	@Override
	public ResultDO<MemberCheck> memberCheck(Long memberId, int loginSource) throws Exception {
		try {
			//判断今天是否 已经签到
			if(memberCheckManager.isChecked(memberId)) {
				ResultDO<MemberCheck> result = new ResultDO<MemberCheck>();
				result.setResultCode(ResultCode.MEMBER_IS_CHECKED_YET_ERROR);
				logger.info(ResultCode.MEMBER_IS_CHECKED_YET_ERROR.getMsg()+",memberId={}", memberId);
				return result;
			}
			//处理活动
			taskExecutor.execute(new ActivityThread(memberId));
			return memberCheckProc(memberId, loginSource);
		}catch (DuplicateKeyException mysqlE) { //重复签到不打印日志
			logger.error("会员重复签到！memberId={}", memberId);
			throw mysqlE;
		}catch (Exception e) {
			logger.error("会员："+memberId+"签到发生异常", e);
			throw e;
		}
	}

	@Override
	public String getAuditMemberInfosForm(Long memberId) throws Exception {
		return sinaPayClient.auditMemberInfos(memberId);
	}
	
	
	public MemberToken getWeiXinMemberToken(String openId){
		try {
			return memberTokenManager.selectByDevice(openId);
		} catch (ManagerException e) {
			logger.error("获取微信Token异常："+openId);
		}
		return null;
	}
	
	/**
	 * 签到程序
	 * @param memberId
	 * @param loginSource
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	private ResultDO<MemberCheck> memberCheckProc(Long memberId, int loginSource) throws Exception {
		ResultDO<MemberCheck> result = new ResultDO<MemberCheck>();
		try {
			//随机获得签到点数
			int dickNum = RandomUtils.dickRandom();
			int _dickNum = dickNum;
			int popularityDouble = 1;
			//2015-05-01 00:00:00 到2015-05-03 23:59:59期间签到翻10倍
			boolean hasBirthday = false;
			String remark = RemarksEnum.MEMBER_CHECK_ADD_POPULARITY_BALANCE.getRemarks();
			Member member = memberManager.selectByPrimaryKey(memberId);
			Activity activity = getBirthdayActivity();
			if(activity != null){
				if(DateUtils.getCurrentDate().after(activity.getStartTime())) {
					if(member.getBirthday() != null){
						int status = getBirthdayStatus(member.getBirthday());
						if(status == 1){
							popularityDouble = 10;
							_dickNum = dickNum * popularityDouble;
							remark = remark+"(【祝你生日快乐】签到特权翻10倍)";
							hasBirthday = true;
							vipDiscountManager.birthGoSign(memberId, MemberLogEnum.MEMBER_BEHAVIOR_WEB.getType());
						}
					}
				}
			}
			//2015-10-01 00:00:00 到2015-10-07 23:59:59期间国庆签到翻N倍
//			Date nationalDayStart = DateUtils.getDateTimeFromString("2015-10-01 00:00:00");
//			Date nationalDayEnd = DateUtils.getDateTimeFromString("2015-10-07 23:59:59");
//			if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), nationalDayStart, nationalDayEnd) && !hasBirthday) {
//				int dayIndex = DateUtils.getDate(DateUtils.getCurrentDate());
//				popularityDouble = Integer.parseInt(PropertiesUtil.getProperties("activity.nationalDay.memberCheck.day" + dayIndex));
//				_dickNum = dickNum * popularityDouble;
//				remark = remark+"(国庆签到翻"+popularityDouble+"倍)";
//			}
			// 2016年三八妇女节签到翻10倍
//			Optional<Activity> optForWomensDay = LotteryContainer.getInstance().getActivityByName(ActivityConstant.WOMEN_DAY_NAME);
//			if (optForWomensDay.isPresent()
//					&& optForWomensDay.get().getActivityStatus().equals(StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) && !hasBirthday
//					&& member.getSex().equals(TypeEnum.SEX_WOMAN.getType())) {
//				popularityDouble = Integer.valueOf(optForWomensDay.get().getObtainConditionsJson());
//				_dickNum = dickNum * popularityDouble;
//				remark = remark + "(女人节签到翻" + popularityDouble + "倍)";
//			}
			logger.info("用户memberId：" + memberId + remark + ",点数为："+_dickNum);
			BigDecimal gainPopularity = BigDecimal.valueOf(_dickNum);
			//插入签到记录、人气值流水记录，新增人气值余额
			MemberCheck memberCheck = new MemberCheck();
			memberCheck.setCheckDate(DateUtils.getCurrentDate());
			memberCheck.setCheckSource(loginSource);//TODO 后期需要从session里读取
			memberCheck.setGainPopularity(gainPopularity);
			memberCheck.setPopularityDouble(popularityDouble);
			memberCheck.setMemberId(memberId);
			memberCheckManager.insert(memberCheck);
			
			//返回给前端的是点数，而不是翻倍的结果
			MemberCheck memberCheckDto = new MemberCheck();
			memberCheckDto.setCheckDate(DateUtils.getCurrentDate());
			memberCheckDto.setCheckSource(loginSource);//TODO 后期需要从session里读取
			memberCheckDto.setGainPopularity(new BigDecimal(dickNum));//签到的点数
			memberCheckDto.setPopularityDouble(popularityDouble);
			memberCheckDto.setMemberId(memberId);
			result.setResult(memberCheckDto);
			//调用赠送人气值接口
//			Balance balance = balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, gainPopularity, memberId);
//			//记录人气值资金流水
//			popularityInOutLogManager.insert(
//					memberId, 
//					TypeEnum.FIN_POPULARITY_TYPE_CHECK, 
//					gainPopularity, 
//					null, 
//					balance.getAvailableBalance(), 
//					memberCheck.getId(), 
//					remark
//					);
			transactionManager.givePopularity(memberCheck.getId(), memberId, TypeEnum.FIN_POPULARITY_TYPE_CHECK, gainPopularity, remark);
			
			MessageClient.sendMsgForSignIn(DateUtils.getCurrentDate(), _dickNum, memberId);
			//写入session
			HttpSession session = ((ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes()).getRequest().getSession();
			Object isChecked = session.getAttribute("checked"+DateUtils.formatDatetoString(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_3));
			if(isChecked!=null){
				session.removeAttribute("checked"+DateUtils.formatDatetoString(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_3));
			}
			session.setAttribute("checked"+DateUtils.formatDatetoString(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_3), true);
			logger.info("用户签到送现金券开始：" + memberId);
			activityLotteryManager.signSendCoupon(memberId);
			logger.info("用户签到送现金券结束：" + memberId);
			//签到送现金券活动
			activityLotteryManager.prizeByCheck(member);
			//情人节赠送高额收益券
//			activityLotteryManager.valentineDay(memberId);
			return result;
		} catch (DuplicateKeyException mysqlE) { //重复签到不打印日志
			logger.error("会员重复签到！memberId={}", memberId);
			throw mysqlE;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public int getBirthdayStatus(Date birthday){
		if(birthday == null){
			return 0;
		}
		Date date = DateUtils.getCurrentDate();
		int c_month = DateUtils.getMonth(date);
		int c_day = DateUtils.getDate(date);
		int b_month = DateUtils.getMonth(birthday);
		int b_day = DateUtils.getDate(birthday);
		if(c_month < b_month){
			return 0;//生日还未到
		}else if(c_month == b_month && c_day == b_day){
			return 1;//今天生日
		}else{
			return -1;//生日已经过了
		}
	}

	@Override
	public Activity getBirthdayActivity() {
		try {
			return memberManager.getBirthdayActivity();
		} catch (ManagerException e) {
			logger.error("获取生日专题活动异常", e);
		}
		return null;
	}

	@Override
	public ResultDO getNoviceTaskStatus(Long memberId) {
		ResultDO  result = new ResultDO();
		try {
			Map<String, Object> map = Maps.newHashMap();
			Date activityStartDate = DateUtils.getDateFromString(WebPropertiesUtil.getProperties("noviceTaskStartTime"));
			ActivityHistory bindEmailActivityHistory = activityHistoryManager.getActivityHistory(memberId, PropertiesUtil.getActivityBindEmailId());
			map.put("isBindEmail", isJoinActivity(bindEmailActivityHistory, activityStartDate, 1));
			map.put("showBindEmailReward", isJoinActivity(bindEmailActivityHistory, activityStartDate, 2));
			
			ActivityHistory completedInfoActivity = activityHistoryManager.getActivityHistory(memberId, PropertiesUtil.getActivityCompletedMemberInfoId());
			map.put("isCompletedInfo", isJoinActivity(completedInfoActivity, activityStartDate, 1));
			map.put("showCompletedInfoReward", isJoinActivity(completedInfoActivity, activityStartDate, 2));
			
			Transaction transaction = transactionManager.getFirstTransaction(memberId);
			map.put("isInvestment", transaction != null ? true : false);
			map.put("showInvestmentReward", (transaction != null && transaction.getTransactionTime().after(activityStartDate)) ? true : false);
			
			MemberToken weixinToken = memberTokenManager.selectFirstLoginWeiXin(memberId);
			map.put("isFollowWeiXin", weixinToken != null ? true : false);
			map.put("showFollowWeiXinReward", (weixinToken != null && weixinToken.getCreateTime().after(activityStartDate)) ? true : false);
			
			MemberToken appToken = memberTokenManager.selectFirstLoginApp(memberId);
			map.put("isUseApp", appToken != null ? true : false);
			map.put("showUseAppReward", (appToken != null && appToken.getCreateTime().after(activityStartDate)) ? true : false);
			result.setResult(map);
		} catch (ManagerException e) {
			logger.error("新手任务状态异常："+memberId);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}

	private boolean isJoinActivity(ActivityHistory activityHistory, Date activityStartDate, int type){
		if(type == 1){
			if(activityHistory != null){
				return true;
			}
			return false;
		}
		if(activityHistory != null && activityHistory.getCreateTime().after(activityStartDate)){
			return true;
		}
		return false;
	}

	@Override
	public List<NoviceTaskDto> getNoviceTaskList(Long memberId) {
		ResultDO result = getNoviceTaskStatus(memberId);
		Map<String, Object> map = (Map<String, Object>) result.getResult();
		NoviceTaskDto bindEmail = new NoviceTaskDto();
		bindEmail.setName("绑定邮箱");
		bindEmail.setJoin((boolean) map.get("isBindEmail"));
		bindEmail.setShow((boolean) map.get("showBindEmailReward"));
		bindEmail.setUrl("/member/security");
		bindEmail.setCode("email");
		
		NoviceTaskDto completedInfo = new NoviceTaskDto();
		completedInfo.setName("完善信息");
		completedInfo.setJoin((boolean) map.get("isCompletedInfo"));
		completedInfo.setShow((boolean) map.get("showCompletedInfoReward"));
		completedInfo.setUrl("/member/profile");
		completedInfo.setCode("info");
		
		NoviceTaskDto transaction = new NoviceTaskDto();
		transaction.setName("首次投资");
		transaction.setJoin((boolean) map.get("isInvestment"));
		transaction.setShow((boolean) map.get("showInvestmentReward"));
		transaction.setUrl("/products/list-all-all-investing-1-createTimeAsc-1.html");
		transaction.setCode("project");
		
		NoviceTaskDto weixin = new NoviceTaskDto();
		weixin.setName("绑定微信");
		weixin.setJoin((boolean) map.get("isFollowWeiXin"));
		weixin.setShow((boolean) map.get("showFollowWeiXinReward"));
		weixin.setCode("weixin");
		
		NoviceTaskDto app = new NoviceTaskDto();
		app.setName("体验APP");
		app.setJoin((boolean) map.get("isUseApp"));
		app.setShow((boolean) map.get("showUseAppReward"));
		app.setCode("app");
		
		List<NoviceTaskDto> noviceTaskList = Lists.newArrayList();
		noviceTaskList.add(bindEmail);
		noviceTaskList.add(completedInfo);
		noviceTaskList.add(transaction);
		noviceTaskList.add(weixin);
		noviceTaskList.add(app);
		
		Collections.sort(noviceTaskList, new Comparator<NoviceTaskDto>(){
			@Override
			public int compare(NoviceTaskDto o1, NoviceTaskDto o2) {
				if(!o2.isJoin()){
					return 1;
				}
				if(o1.isJoin() && o2.isJoin()){
					return 0;
				}
				return -1;
			}
			
		});
				
		return noviceTaskList;
	}
	
	@Override
	public boolean countDebtByLenderId(Long memberId){
		try {
			return debtManager.countDebtByLenderId(memberId);
		} catch (ManagerException e) {
			logger.error("统计用户出借人身份下的债权数量", e);
		}
		return false;
	}

	@Override
	public Object synPayPasswordFlag(Long memberId) {
		ResultDO<Boolean> rDO = new ResultDO<Boolean>();
		try {
			return memberManager.synPayPasswordFlag(memberId);
		} catch (ManagerException e) {
			logger.error("同步是否设置支付密码失败  memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public Object handlePayPassword(Long memberId, int handleType) {
		ResultDO<String> rDO = new ResultDO<String>();
		try {
			if (memberId == null || handleType < TypeEnum.SET_PAY_PASSWORD.getType() || handleType > TypeEnum.FIND_PAY_PASSWORD.getType()) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDO;
			}
			String returnUrl = WebPropertiesUtil.getSinaCashDeskReturnUrl();
			return memberManager.handlePayPassword(memberId, handleType, returnUrl);
		} catch (ManagerException e) {
			logger.error("设置支付密码失败  memberId={}, handleType={}", memberId, handleType, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		}
	}
	
	@Override
	public Integer getMemberSignWay(Long memberId){
		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			int signWay = member.getSignWay();
			return signWay;
		} catch (ManagerException e) {
			logger.error("获取用户签署方式异常", e);
		}
		return null;
	}

	@Override
	public ResultDO<String> handWithholdAuthority(Long memberId, int type) {
		ResultDO<String> resultDO = new ResultDO<String>();
		try {
			if (memberId == null || type < TypeEnum.HANDLE_WITHHOLD_AUTHORITY.getType() || type > TypeEnum.RELIEVE_WITHHOLD_AUTHORITY.getType()) {
				resultDO.setSuccess(false);
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return resultDO;
			}
			return memberManager.handWithholdAuthority(memberId, type, WebPropertiesUtil.getSinaCashDeskReturnUrl());
		} catch (ManagerException e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			if (type == TypeEnum.HANDLE_WITHHOLD_AUTHORITY.getType()) {
				logger.error("[委托扣款授权]操作出现异常!", e); 
			} else if (type == TypeEnum.RELIEVE_WITHHOLD_AUTHORITY.getType()) {
				logger.error("[解除委托扣款]操作出现异常!", e); 
			}
			return resultDO;
		}
	}

	@Override
	public ResultDO<Boolean> synWithholdAuthority(Long memberId)  {
		ResultDO<Boolean> resultDO = new ResultDO<Boolean>();
		try {
			return memberManager.synWithholdAuthority(memberId);
		} catch (ManagerException e) {
			resultDO.setResult(false);
			logger.error("同步是否开通委托扣款失败  memberId={}", memberId, e);
			return resultDO;
		}
	}

	// 调用新浪托管提现接口，返回提现form
	@Override
	public ResultDO<Object> createWithdrawAndWithdrawLog(Long memberId, String userIp, BigDecimal withdrawAmount, BigDecimal freeWithdrawAmount, BigDecimal fee) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			String withdrawNo = SerialNumberUtil.generateWithdrawNo(memberId);
			return memberManager.createHostingWithdraw(memberId, userIp, withdrawAmount, freeWithdrawAmount, WebPropertiesUtil.getSinaCashDeskReturnUrl(),
					withdrawNo, fee, TypeEnum.MEMBER_LOGIN_SOURCE_PC.getType());
		} catch (Exception e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("[托管提现]出现异常  memberId={}", memberId, e);
			return resultDO;
		}
	}

	@Override
	public ResultDO<Object> modifyMobileByOldMobile(Long memberId, Long newMobile, Long oldMobile) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
        try {
			return this.memberManager.updateMobileByOldMobile(memberId, newMobile, oldMobile);
		} catch (Exception e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("根据原手机号更新会员新手机号码，修改失败, oldMobile=" + oldMobile + ",newMobile=" + newMobile);
			return resultDO;
		}
	}
	
	@Override
	public ResultDO<Member> saveSignWay(Long memberId, int sighWay) {
		ResultDO<Member> result = new ResultDO<Member>();
		try {
			Member record = new Member();
			record.setId(memberId);
			record.setSignWay(sighWay);
			Member member = memberManager.selectByPrimaryKey(memberId);
			if(sighWay==1&&member.getIsAuth()==0){
				contractCoreManager.memberCa(memberId);
			}
			memberManager.updateByPrimaryKeySelective(record);
		} catch (ManagerException e) {
			logger.error("保存用户签署方式异常memberId={},sighWay={}",memberId,sighWay);
		}
		return result;
	}

	@Override
	public ResultDO<Object> validateMemberInfo(Long oldMobile, String trueName, String identityNumber, String password) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			return memberManager.validateMemberInfo(oldMobile, trueName, identityNumber, password);
		} catch (ManagerException e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("根据会员身份信息更新手机号码，验证信息失败, oldMobile=" + oldMobile + ","
					+ "trueName=" + trueName + ",identityNumber=" + identityNumber);
			return resultDO;
		}
	}

	@Override
	public ResultDO<Object> modifyMobileByIdentity(Long oldMobile, Long newMobile, String trueName, String identityNumber) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			return memberManager.updateMobileByIdentity(oldMobile, newMobile, trueName, identityNumber);
		} catch (ManagerException e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("根据会员身份信息更新手机号码，修改失败, oldMobile=" + oldMobile + ",newMobile=" + newMobile + ","
					+ "trueName=" + trueName + ",identityNumber=" + identityNumber);
			return resultDO;
		}
	}

	
	@Override
	public ResultDO<Object> modifyPasswordByNewMobile(Long newMobile, String newPassword) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			return memberManager.updateNewMobilePassword(newMobile, null, null, newPassword);
		} catch (ManagerException e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("新手机号重置密码，用手机短信验证方式，重置失败, mobile=" + newMobile + ",errormsg=" + e);
			return resultDO;
		}
	}

	
	@Override
	public ResultDO<Object> modifyPasswordByIdentity(Long newMobile, String trueName, String identityNumber, String newPassword) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			return memberManager.updateNewMobilePassword(newMobile, trueName, identityNumber, newPassword);
		} catch (ManagerException e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("新手机号重置密码，验证身份信息方式，重置失败, mobile=" + newMobile + ",trueName=" + trueName + ",identityNumber=" + identityNumber + ",errormsg=" + e);
			return resultDO;
		}
	}

	@Override
	public ResultDO<Object> autoInvestSet(Long memberId, int investFlag) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			return memberManager.autoInvestSet(memberId, investFlag);
		} catch (ManagerException e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("自动投标设置失败,investFlag 等于1 是开启，2是关闭, memberId=" + memberId + ",investFlag=" + investFlag  +  ",errormsg=" + e);
			return resultDO;
		}
	}
	/**
	 * 
	 * @desc 保存自动投标设置信息
	 * @param autoInvestDto
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年8月17日 上午10:10:59
	 *
	 */
	@Override
	public ResultDO<AutoInvestSet> saveAutoInvestSetByMemberId(
			AutoInvestSetDto autoInvestDto) throws ManagerException {
		ResultDO<AutoInvestSet> resultDO = new ResultDO<AutoInvestSet>();
		if(autoInvestDto.getStartTime()==null||autoInvestDto.getEndTime()==null||autoInvestDto.getInvestFrequency()==null||
				autoInvestDto.getProjectType()==null||autoInvestDto.getPeriodMin()==null||autoInvestDto.getPeriodMax()==null||autoInvestDto.getAmount()==null){
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return resultDO;
		}
		AutoInvestSet autoInvest=	autoInvestSetManager.selectByMemberId(autoInvestDto.getMemberId());
		AutoInvestSet biz=new AutoInvestSet();
		BeanCopyUtil.copy(autoInvestDto, biz);
		if(autoInvest==null){
			biz.setCreateTime(DateUtils.getCurrentDate());
			biz.setUpdateTime(DateUtils.getCurrentDate());
			biz.setSortTime(DateUtils.getCurrentDate());
			int i=autoInvestMapper.insertSelective(biz);
			if(i>0){
				resultDO.setSuccess(true);
				return resultDO;
			}else{
				resultDO.setSuccess(false);
				return resultDO;
			}
		}else{
			biz.setUpdateTime(DateUtils.getCurrentDate());
			int i=autoInvestSetManager.updateByPrimaryMemberIdSelective(biz);
			if(i>0){
				resultDO.setSuccess(true);
				return resultDO;
			}else{
				resultDO.setSuccess(false);
				return resultDO;
			}
		}
	}
	/**
	 * 
	 * @desc 查询投标设置信息
	 * @param memberId
	 * @return
	 * @author chaisen
	 * @time 2016年8月17日 上午10:17:34
	 *
	 */
	@Override
	public ResultDO<AutoInvestSet> queryAutoInvest(Long memberId) {
		ResultDO<AutoInvestSet> resultDO = new ResultDO<AutoInvestSet>();
		try {
			AutoInvestSet autoInvest=autoInvestSetManager.selectByMemberId(memberId);
			if(autoInvest!=null){
				if(autoInvest.getProjectType()==null||autoInvest.getAmount()==null){
					autoInvest.setOpen(false);
				}else{
					autoInvest.setOpen(true);
				}
				if(autoInvest.getInvestFlag()==StatusEnum.INVEST_FLAG_OPEN.getStatus()){
					AutoInvestSet biz=autoInvestMapper.countMemberIdSort(memberId);
					if(biz!=null){
						autoInvest.setSort(biz.getSort());
					}
				}
			}else{
				autoInvest=new AutoInvestSet();
				autoInvest.setOpen(false);
			}
			resultDO.setResult(autoInvest);
		} catch (Exception e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("查询自动投标设置失败 memberId=" + memberId   +  ",errormsg=" + e);
			return resultDO;
		}
		return resultDO;
	}
	
	
	
	@Override
	public ResultDO<Object> showMemberInfosSina(Long memberId) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			String returnUrl = WebPropertiesUtil.getSinaCashDeskReturnUrl();
			return memberManager.showMemberInfosSina(memberId,returnUrl);
		} catch (ManagerException e) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("获取用户信息新浪展示页面失败, memberId={}", memberId ,e);
			return resultDO;
		}
	}
	
	/**
	 * 
	 * @desc 自动投标异步线程
	 * @author chaisen
	 * 2016年8月19日下午12:00:50
	 */
	private class AutoInvestAfterTransactionThread implements Runnable {
		private Transaction transaction;
		private Long orderId;
		
		public AutoInvestAfterTransactionThread(final Transaction transaction, final Long orderId) {
			this.transaction = transaction;
			this.orderId = orderId;
		}
		
		@Override
		public void run() {
			logger.info("交易完成后自动投标线程开始执行，transactionId：" + transaction.getId());
			try {
				Order order = orderManager.selectByPrimaryKey(orderId);
				if(order == null){
					logger.info("【自动投标】notify回来,订单为空!");
					return;
				}
				if(order.getInvestFlag()==null||order.getInvestFlag()==0){
					logger.info("【自动投标】notify回来,订单为手动投资!");
					return;
				}
				// 如果代收失败，更新自动投标记录的状态
				if (transaction == null) {
					//更新自动投标记录
					AutoInvestLog autoLog = new AutoInvestLog();
					autoLog.setOrderId(transaction.getOrderId());
					autoLog.setStatus(order.getStatus());
					autoLog.setRemarks("代收失败：" + order.getRemarks());
					autoInvestLogManager.updateAutoInvestLogByOrderId(autoLog);
					return;
				}
				AutoInvestSet autoInvest=autoInvestSetManager.selectByMemberId(transaction.getMemberId());
				Long memberId=transaction.getMemberId();
				if(autoInvest==null){
					return;
				}
				AutoInvestSet biz=new AutoInvestSet();
				biz.setMemberId(memberId);
				biz.setSortTime(DateUtils.getCurrentDate());
				biz.setUpdateTime(DateUtils.getCurrentDate());
				//更新自动投标设置的排序时间
				autoInvestSetManager.updateByPrimaryMemberIdSelective(biz);
				AutoInvestLog autoLog=new AutoInvestLog();
				autoLog.setAutoInvestId(autoInvest.getId());
				autoLog.setOrderId(transaction.getOrderId());
				autoLog.setTransactionId(transaction.getId());
				autoLog.setStatus(order.getStatus());
				//更新自动投标记录的状态、备注(原因)、交易记录ID
				autoInvestLogManager.updateAutoInvestLogByOrderId(autoLog);
				
				Transaction tran=new Transaction(); 
				tran.setId(transaction.getId());
				tran.setInvestFlag(order.getInvestFlag());
				//插入交易记录的时候增加投标标识
				transactionManager.updateByPrimaryKeySelective(tran);
				// 如果成功代收，加入缓存
				if(StatusEnum.ORDER_PAYED_INVESTED.getStatus() == order.getStatus()){
					// 用户对当前项目是否自动投标过
					String key = memberId + RedisConstant.MEMBER_CURRENT_PROJECT_AUTO_INVEST_FLAG + transaction.getProjectId();
					RedisManager.set(key, YESAUTOINVESTFLAG);
					//判断项目余额
					Balance projectBalance = balanceManager.queryBalanceLocked(transaction.getProjectId(),TypeEnum.BALANCE_TYPE_PROJECT);
					if(projectBalance!=null&&projectBalance.getAvailableBalance()!=null){
						if(autoInvest.getAmount()!=null&&autoInvest.getAmount().compareTo(projectBalance.getAvailableBalance())>0){
							RedisManager.expire(key, DateUtils.calculateCurrentToEndTime());
						}
					}
					// 用户当天是否自动投标过
					String key1 = memberId + RedisConstant.MEMBER_CURRENT_DAY_AUTO_INVEST_FLAG + DateUtils.getStrFromDate(new Date(), DateUtils.DATE_FMT_3);
					RedisManager.set(key1, YESAUTOINVESTFLAG);
					RedisManager.expire(key1, DateUtils.calculateCurrentToEndTime());
					
					Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
					//app信息提醒用户投资成功
					MessageClient.sendMsgForCommon(memberId, Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_AUTO_INVEST_SUCCESS.getCode(), 
						project.getName().contains("期")?project.getName().substring(0, project.getName().indexOf("期") + 1):project.getName()
								,autoInvest.getAmount().toString(),transaction.getId().toString());
				}
				
			} catch (Exception e) {
				logger.error("交易完成后自动投标线程执行结束发生异常，transactionId：" + transaction.getId(), e);
			}
		}
	}	
	
	@Override
	public void autoInvestSetAfterTransaction(Transaction transaction, Long orderId) {
		taskExecutor.execute(new AutoInvestAfterTransactionThread(transaction, orderId));
	}

	@Override
	public boolean needCheckNoviceProject(Long memberId) {
		return memberManager.needCheckNoviceProject(memberId);
	}

	@Override
	public int queryMemberReferralCount(Long memberid, Date starttime, Date endtime) {
		return memberManager.queryMemberReferralCount(memberid,starttime,endtime);
	}

	@Override
	public int queryMemberReferralAndTransactionCount(Long memberid, Date starttime, Date endtime) {
		return memberManager.queryMemberReferralAndTransactionCount(memberid,starttime,endtime);
	}

	/**
	 * 签到活动异步线程
	 */
	private class ActivityThread implements Runnable{
		private final Long memberId;
		public ActivityThread(Long _memberId){
			memberId=_memberId;
		}
		@Override
		public void run() {
			try {
				//51活动签到
				activityLotteryManager.laborSign(memberId);
			} catch (Exception e) {
				logger.error("异步发放活动奖励异常, memberId={}", memberId ,e);
			}
		}
	}
}
