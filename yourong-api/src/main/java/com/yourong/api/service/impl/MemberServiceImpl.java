package com.yourong.api.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.api.dto.AutoInvestSetDto;
import com.yourong.api.dto.CouponTemplateDto;
import com.yourong.api.dto.FinEaringDto;
import com.yourong.api.dto.MemberAuthDto;
import com.yourong.api.dto.MemberCheckDto;
import com.yourong.api.dto.MemberCheckInfoDto;
import com.yourong.api.dto.MemberReferralDto;
import com.yourong.api.dto.MemberSecurityDto;
import com.yourong.api.dto.PopularityInOutLogDto;
import com.yourong.api.dto.RechargeDto;
import com.yourong.api.dto.RechargeLogDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.MemberService;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.MemberLogEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.SinaBankMobileEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.mail.SendMailService;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.pay.PayMentService;
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
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.fin.model.RechargeLog;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AvatarAttachmentHandle;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.dao.ActivityLotteryResultMapper;
import com.yourong.core.mc.manager.ActivityHistoryManager;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.push.PushEnum;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.tc.manager.ContractCoreManager;
import com.yourong.core.tc.manager.TransactionManager;
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
import com.yourong.core.uc.model.AutoInvestSet;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBankCard;
import com.yourong.core.uc.model.MemberCheck;
import com.yourong.core.uc.model.MemberVerify;
import com.yourong.core.uc.model.biz.MemberForLottery;
import com.yourong.core.upload.model.ImageConfig;

@Service
public class MemberServiceImpl implements MemberService {

	private static final String TICKET_NO_EXIT = "ticket不存在或已失效";
	private static final String  SMS_ERROR_CODE = "短信验证失败";
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String ROOT_URL = "root_url";

	
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
	private MemberCheckManager memberCheckManager;
	
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
	private MemberTokenManager memberTokenManager;
	
	@Autowired
	private ActivityHistoryManager activityHistoryManager;
	
	@Autowired
	private CouponTemplateManager couponTemplateManager;

	@Autowired
	private ActivityLotteryManager activityLotteryManager;
	
	@Autowired
	private ActivityLotteryResultMapper activityLotteryResultMapper;
	
	@Autowired
	private ContractCoreManager contractCoreManager;
	
	@Autowired
	private AutoInvestSetManager autoInvestSetManager;
	
	@Autowired
	private AutoInvestLogManager autoInvestLogManager;
	
	@Autowired
	private AutoInvestSetMapper autoInvestMapper;
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
	private VipDiscountManager vipDiscountManager;

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
	public ResultDTO<Object> updatePassword(Long id, String oldpassword,
			String newPassword) {
		ResultDTO<Object> result = new ResultDTO<Object>();
		result.setIsSuccess();
		ResultDO resultDO = memberManager.updatePassword(id, oldpassword, newPassword);
		if(!resultDO.isSuccess()){
			result.setResultCode(resultDO.getResultCode());
		}
		return result;
	}
	
	public ResultDTO<Object> updatePasswordByMobile(Long id,Long mobile,String newPassword){
		ResultDTO<Object> result = new ResultDTO<Object>();
		result.setIsSuccess();
		ResultDO resultDO =  memberManager.updatePasswordByMobile(id, mobile, newPassword);
		if(!resultDO.isSuccess()){
			result.setResultCode(resultDO.getResultCode());
		}
		return result;
	}
	
	//清空APP端token
	private void cleanAppClientTokenAndPushMessage(Long memberId)  {
		try {
			memberTokenManager.cleanAppClientTokenAndPushMessage(memberId,true,"您的密码已修改。", PushEnum.MEMBER_UPDATE_PASSWORD);
		} catch (ManagerException e) {
			logger.error("清空app端token失败,memberID:" + memberId, e);
		}
	}
		
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)	
	public ResultDO<Object> authIdentity(MemberAuthDto memberDto) throws Exception{
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
			if (memberDto.getMemberID()!=null){
				Member member = this.memberManager.selectByPrimaryKey(memberDto.getMemberID());
			   if (member!=null){
				   memberDto.setMobile(member.getMobile());
			   }else {
				   resultDO.setResultCode(ResultCode.MEMBER_ID_IS_NOT_NULL);
				   resultDO.setSuccess(false);
				   return resultDO;
			   }
			}
            authThirdPay(memberDto.getMemberID(), memberDto.getTrueName(),memberDto.getIdentityNumber(), resultDO,memberDto.getMobile(), memberDto.getClientIp());
            if(resultDO.isSuccess()) {
                resultDO = insertAutch(memberDto);
                //绑定后送红包
                //couponManager.sendCoupon(memberDto.getId(), TypeEnum.COUPON_SEND_SOURCE_TYPE_IDENTITY_BIND, null);
                
//                SPParameter  parameter =new SPParameter();
//    			parameter.setMemberId(memberDto.getMemberID());
//    			SPEngine.getSPEngineInstance().run(parameter);
    			
    			MessageClient.sendMsgForRegSina(DateUtils.getCurrentDate(), new BigDecimal(518), memberDto.getMemberID());
            }
		} catch (Exception e) {
			// 认证失败
			resultDO.setResultCode(ResultCode.MEMBER_AUTH_IDENTITY_ERROR);
			resultDO.setResult(ResultCode.MEMBER_AUTH_IDENTITY_EXIT.getMsg());
			throw e;
		}
		return resultDO;
	}

	
	public ResultDO<Object> insertAutch(MemberAuthDto memberDto) throws ManagerException{
	    ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			//更新身份证已经生日信息
			Date birthday = DateUtils.getBirthdayByIdentity(memberDto.getIdentityNumber());
			int sex = StringUtil.getSexByIdentityNumber(memberDto.getIdentityNumber());
			int rs = memberManager.updateIdentityNumberById(memberDto.getTrueName(),memberDto.getIdentityNumber(), memberDto.getMemberID(), birthday,sex);
			if(rs > 0){
				//添加认证信息
				MemberVerify record = new MemberVerify();
				//BeanCopyUtil.copy(record, memberDto.getMemberVerifyDto());
				record.setVerifyOperate(Constant.ENABLE);
				record.setVerifyType(TypeEnum.MEMBER_VERIFY_TYPE_IDENTITY.getCode());
				record.setMemberId(memberDto.getMemberID());
				record.setVerifyContent(memberDto.getTrueName()+StringUtil.CARET+memberDto.getIdentityNumber());
				record.setCreateTime(DateUtils.getCurrentDateTime());
				record.setClientIp(memberDto.getClientIp());
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
	
	

	private void authThirdPay(long memberID, String realName,String certificateNumber, ResultDO<Object> resultDO,Long mobie, String clientIp) throws Exception {
		//绑定手机
        String verifyEntity = mobie.toString();
	        
        ResultDto<?> verify = sinaPayClient.queryVerify(memberID, VerifyType.MOBILE);
        if(verify!= null && Constants.VERIFY_NOT_EXIST.equalsIgnoreCase(verify.getErrorCode())){
        	 ResultDto<?> resultDto = sinaPayClient.bindingVerify(memberID, verifyEntity, VerifyType.MOBILE, clientIp);
             if (!resultDto.isSuccess()) {
                 resultDO.setSuccess(false);
				 ResultCode.SINA_BIZ_ERROR.setMsg(resultDto.getErrorMsg());
                 resultDO.setResult(resultDto.getErrorMsg());
				 resultDO.setResultCode( ResultCode.SINA_BIZ_ERROR);
                 return;
             }
        }        
		 //实名认证
        ResultDto<?> dto = sinaPayClient.setRealName(memberID, VerifyStatus.Y, certificateNumber, realName, clientIp);
        if (dto== null || dto.isError()) {
            resultDO.setSuccess(false);
			ResultCode.SINA_BIZ_ERROR.setMsg(dto.getErrorMsg());
			resultDO.setResult(dto.getErrorMsg());
			resultDO.setResultCode(ResultCode.SINA_BIZ_ERROR);
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
            rechargeLogManager.insertSelective(record);
        }else{
            logger.error("充值生成签名异常 memberID = "+memberID);
            throw new ManagerException("充值生成签名异常memberID" +memberID);
        }
        return result;

    }
	@Override
	public ResultDO<Object> rechargeOnCardAndRechargeLog(Long memberID, String payerIp, BigDecimal bigDecimal, Long cardID, TypeEnum type,
			String orderNo, Integer rechargeSource) throws Exception {
		return bangCardPay(memberID, payerIp, bigDecimal, cardID, type, orderNo, rechargeSource);
	}

	@Override
	public ResultDO<Object> rechargeOnCardAndRechargeLog(Long memberID, String peyerIp, BigDecimal bigDecimal, Long cardID, TypeEnum type,
			String orderNo) throws Exception {
		return bangCardPay(memberID, peyerIp, bigDecimal, cardID, type, orderNo, 0);
	}

	private ResultDO<Object> bangCardPay(Long memberID, String payerIp, BigDecimal bigDecimal, Long cardID, TypeEnum type, String orderNo,
			Integer rechargeSource) throws Exception {
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
			record.setRechargeSource(rechargeSource);
			record.setOrderNo(orderNo);
			record.setType(type.getType());
			record.setStatus(StatusEnum.RECHARGE_STATUS_PROESS.getStatus());
			record.setBankCardId(cardID);
			record.setPayerIp(payerIp);
			int i = rechargeLogManager.insertSelective(record);
			result.setSuccess(false);
			if(TICKET_NO_EXIT.equals(payResult.getErrorMsg()) ||SMS_ERROR_CODE.equals(payResult.getErrorMsg())){
				result.setResultCode(ResultCode.MEMBER_ADVACE_CODE_EROOR);
			}else {
				ResultCode.SINA_BIZ_ERROR.setMsg(payResult.getErrorMsg());
				result.setResultCode(ResultCode.SINA_BIZ_ERROR);
				result.setResult(payResult.getErrorMsg());
			}
			logger.info("绑卡支付失败：memberID"+memberID+": error: "+payResult.getErrorMsg());
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
			record.setRechargeSource(rechargeSource);
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
				if (TICKET_NO_EXIT.equals(resultDto.getErrorMsg()) || SMS_ERROR_CODE.equals(resultDto.getErrorMsg()) || "验证码错误".equals(resultDto.getErrorMsg())) {
						resultDO.setResultCode(ResultCode.MEMBER_ADVACE_CODE_EROOR);
					}else{
						resultDO.setSuccess(false);
						resultDO.setResult(resultDto.getErrorMsg());
					    ResultCode.SINA_BIZ_ERROR.setMsg(resultDto.getErrorMsg());
						resultDO.setResultCode(ResultCode.SINA_BIZ_ERROR);
					}
				logger.info("支付推进失败：outAdvanceNo"+outAdvanceNo+": error"+resultDto.getErrorMsg());
			}
		} catch (Exception e) {
			logger.error("绑卡支付异常,outAdvanceNo=" + outAdvanceNo, e);
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.SINA_BIZ_ERROR);
		}
		return resultDO;
	}

	private void restSinaErrorCode(ResultDO<Object> resultDO, ResultDto<?> resultDto) {
		resultDO.setSuccess(false);
		resultDO.setResult(resultDto.getErrorMsg());
		resultDO.setResultCode(ResultCode.MEMBER_ADVACE_CODE_EROOR);
	}

	@Override
	public String saveAvatar(Long memberId, String avatarURL) {
		AttachmentInfo info = new AttachmentInfo();
		info.setKeyId(memberId.toString());
		info.setAppPath(avatarURL);
		avatarAttachmentHandle.attachmentsHandle(info);
		return Config.ossPicUrl+getMemberAvatar(memberId);
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
	public ResultDTO saveUserName(Long memberId, String userName) {
		ResultDTO result = new ResultDTO();
		try {
			//只允许中文和字母,后台长度限制2~10。
			String regx= "^[\u4E00-\u9FA5\uf900-\ufa2dA-Za-z]{2,14}$";
			int u = StringUtil.getStrLeng(userName);
			if(!Pattern.matches(regx, userName) || (u < 3 || u > 14)){
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
				result.setIsSuccess();
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
	public ResultDTO toBindEmail(Long memberId, String email) {
		ResultDTO result = new ResultDTO();
		try {
			Member memberByEmail = memberManager.selectByEmail(email);
			if(memberByEmail != null){
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
				params.put("url", PropertiesUtil.getWebRootUrl());
				params.put("emailUnsubscribeCode", key);
				sendMailService.sendMailNow(email, "有融网-邮箱地址验证", "bindEmail", params);
				RedisMemberClient.setEmailToken(memberId, email, emailToken);
				result.setIsSuccess();
			}
		} catch (Exception e) {
			logger.error("会员绑定邮箱异常, memberId="+memberId + ", email=" + email);
			result.setResult(ResultCode.ERROR);
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
	public Page<MemberReferralDto> queryMemberReferralList(BaseQueryParam query) {
		Page<MemberReferralDto> page = new Page<MemberReferralDto>();
		try {
			Page<Member> memberForPage = memberManager.getReferralMemberById(query);
			List<Member> members = memberForPage.getData();
			if(Collections3.isNotEmpty(members)) {
				List<MemberReferralDto> referralBizs = Lists.newArrayList();
				for (Member member : members) {
					MemberReferralDto referralBiz = new MemberReferralDto();
					referralBiz.setUsername(member.getUsername());
					referralBiz.setMobile(member.getMobile());
					referralBiz.setEmailBind(member.isEmailBind());
					referralBiz.setReferralRegisterTime(member.getRegisterTime());
					referralBiz.setSavingPotOpen(member.isSavingPotOpen());
					BigDecimal totalInvestAmount = RedisMemberClient.getTotalInvestAmount(member.getId());
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
						int countNum = activityLotteryResultMapper.countRewrdsByMemberActivityRewardId(result);
						BigDecimal totalAmount=BigDecimal.ZERO;
						totalAmount=transactionManager.getTotalAmountByMemberId(member.getId(),optAct.get().getStartTime());
						if(totalAmount==null){
							totalAmount=BigDecimal.ZERO;
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
						int couponNum = activityLotteryResultMapper.sumRewrdsByMemberActivityRewardId(activityResult);
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
				page.setData(referralBizs);
				page.setiDisplayLength(query.getPageSize());
				page.setPageNo(query.getCurrentPage());
				page.setiTotalRecords(memberForPage.getiTotalRecords());
			}
		} catch (ManagerException e) {
			logger.error("获取用户推荐列表失败  memberId="+query.getMemberId(), e);				
		}
		return page;
	}

	@Override
	public Page<PopularityInOutLogDto> queryMemberPopularityLogList(BaseQueryParam query) {
		Page<PopularityInOutLogDto> pageList = new Page<PopularityInOutLogDto>();
		try {
			Page<PopularityInOutLog> log = null;
			//获取列表
			if(query.getType()==1) {
				 log = popularityInOutLogManager.selectPopularityInOutLogForGain(query);
			}
			//兑换列表
			if(query.getType()==2) {
				log = popularityInOutLogManager.selectPopularityInOutLogForExchange(query);
			}
			if(log != null){
				List<PopularityInOutLogDto> list = BeanCopyUtil.mapList(log.getData(), PopularityInOutLogDto.class);
				pageList.setData(list);
				pageList.setiDisplayLength(log.getiDisplayLength());
				pageList.setiDisplayStart(log.getiDisplayStart());
				pageList.setiTotalRecords(log.getiTotalRecords());
				pageList.setPageNo(log.getPageNo());
			}
		} catch (Exception e) {
			logger.error("获取用户人气值列表发生异常, memberId=", e);
		}
		return pageList;
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

	
	public boolean isReceiveMemberInfoCompleteCoupon(Long memberId){
		String status = RedisMemberClient.getMemberInfoCompleteStatus(memberId);
		if(StringUtil.isBlank(status) || status.equals("false")){
			try {
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
				if(StringUtil.isNotBlank(member.getUsername())){
					userName = member.getUsername();
					RedisMemberClient.setMemberUserName(id, member.getUsername());
					logger.info("用户昵称加入缓存 memberID+"+id);
				}else{
					userName = member.getMobile().toString();
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
	public ResultDTO<MemberSecurityDto> queryMemberAuthorize(Long id){
		ResultDTO<MemberSecurityDto> result = new ResultDTO<MemberSecurityDto>();
		try {
			Member member = memberManager.selectByPrimaryKey(id);
			if(member != null){
				MemberSecurityDto dto = new MemberSecurityDto();
				if(StringUtil.isNotBlank(member.getTrueName()) && StringUtil.isNotBlank(member.getIdentityNumber())){
					dto.setTrueName(member.getTrueName());
					dto.setIdentityNumber(member.getIdentityNumber());
					dto.setMemberId(member.getId());
				}
				if(StringUtil.isNotBlank(member.getEmail())){
					dto.setEmail(member.getEmail());
				}
				if(member.getMobile() != null){
					dto.setMobile(member.getMobile().toString());
				}
				MemberBankCard bank = memberBankCardManager.querySecurityBankCard(member.getId());
				if(bank != null){
					dto.setSecurity(true);
				}
				ResultDO<Boolean> isPassword = (ResultDO<Boolean>) memberManager.synPayPasswordFlag(id);
				dto.setPayPassword(isPassword.getResult());
				ResultDO<Boolean> isAuthority = (ResultDO<Boolean>)memberManager.synWithholdAuthority(id);
				dto.setWithholdAuthority(isAuthority.getResult());
				dto.setCompletedInfo(activityHistoryManager.isCompletedMemberInfo(member.getId()));
				dto.setBindEmail(activityHistoryManager.isBindEmail(member.getId()));
				result.setResult(dto);
			}else{
				result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			}
		} catch (Exception e) {
			logger.error("获得用户安全认证信息异常 memberID = "+id);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}

	@Override
	public ResultDTO<MemberCheckDto> memberCheck(Long memberId, int loginSource)
			throws Exception {
		ResultDTO<MemberCheckDto> result = new ResultDTO<MemberCheckDto>();
		try {
			//判断今天是否 已经签到
			if(memberCheckManager.isChecked(memberId)) {
				result.setResultCode(ResultCode.MEMBER_IS_CHECKED_YET_ERROR);
				logger.info(ResultCode.MEMBER_IS_CHECKED_YET_ERROR.getMsg()+",memberId:"+memberId);
				return result;
			}
			//处理活动
			taskExecutor.execute(new ActivityThread(memberId));
			//随机获得签到点数
			int dickNum = RandomUtils.dickRandom();
			int popularityDouble = 1;
			BigDecimal _gainPopularity = BigDecimal.valueOf(dickNum);
			boolean hasBirthday = false;
			//2015-05-20 00:00:00 到2015-05-30 23:59:59期间签到翻2倍
			String remark = RemarksEnum.MEMBER_CHECK_ADD_POPULARITY_BALANCE.getRemarks();
			Member member = memberManager.selectByPrimaryKey(memberId);
			Activity activity = memberManager.getBirthdayActivity();
			if(activity != null){
				if(DateUtils.getCurrentDate().after(activity.getStartTime())) {
					if(member.getBirthday() != null){
						int status = getBirthdayStatus(member.getBirthday());
						if(status == 1){
							popularityDouble = 10;
							dickNum = dickNum * popularityDouble;
							remark = remark+"(【祝你生日快乐】签到特权翻10倍)";
							hasBirthday = true;
							if(loginSource!=3){//针对ios（2）和安卓（1），统一为2app兑换
								vipDiscountManager.birthGoSign(memberId, MemberLogEnum.MEMBER_BEHAVIOR_APP.getType());
							}else{
								vipDiscountManager.birthGoSign(memberId, MemberLogEnum.MEMBER_BEHAVIOR_M.getType());
							}
							
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
//				dickNum = dickNum * popularityDouble;
//				remark = remark+"(国庆签到翻"+popularityDouble+"倍)";
//			}
			// 2016年三八妇女节签到翻10倍
//			Optional<Activity> optForWomensDay = LotteryContainer.getInstance().getActivityByName(ActivityConstant.WOMEN_DAY_NAME);
//			if (optForWomensDay.isPresent()
//					&& optForWomensDay.get().getActivityStatus().equals(StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) && !hasBirthday
//					&& member.getSex().equals(TypeEnum.SEX_WOMAN.getType())) {
//				popularityDouble = Integer.valueOf(optForWomensDay.get().getObtainConditionsJson());
//				dickNum = dickNum * popularityDouble;
//				remark = remark + "(女人节签到翻" + popularityDouble + "倍)";
//			}
			logger.info("用户memberId：" + memberId + remark + ",点数为："+dickNum);
			BigDecimal gainPopularity = BigDecimal.valueOf(dickNum);
			//插入签到记录、人气值流水记录，新增人气值余额
			MemberCheck memberCheck = new MemberCheck();
			memberCheck.setCheckDate(DateUtils.getCurrentDate());
			memberCheck.setCheckSource(loginSource);
			memberCheck.setGainPopularity(gainPopularity);
			memberCheck.setMemberId(memberId);
			memberCheckManager.insert(memberCheck);
			
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
			
			transactionManager.givePopularity(memberCheck.getId(),memberId,TypeEnum.FIN_POPULARITY_TYPE_CHECK, gainPopularity, remark);
			
			MessageClient.sendMsgForSignIn(DateUtils.getCurrentDate(), dickNum, memberId);
			// 签到送现金券活动
			activityLotteryManager.prizeByCheck(member);
			activityLotteryManager.signSendCoupon(memberId);
			//情人节赠送高额收益券
//			activityLotteryManager.valentineDay(memberId);
			
			// 人气值总额
			Balance balance = balanceManager.queryBalance(memberId,
					TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		
			MemberCheckDto dto = new MemberCheckDto();
			dto.setCheckDate(memberCheck.getCheckDate());
			dto.setGainPopularity(_gainPopularity);
			dto.setPopularityDouble(popularityDouble);
			if (balance != null) {
				dto.setPopularity(balance.getBalance().intValue());
			} else {
			dto.setPopularity(0);
			}
			result.setResult(dto);
			
			return result;
		} catch (DuplicateKeyException mysqlE) { //重复签到不打印日志
			logger.error("会员重复签到！memberId={}", memberId);
			throw mysqlE;
		} catch (Exception e) {
			logger.error("会员："+memberId+"签到发生异常", e);
			throw e;
		}
	}

	@Override
	public ResultDTO<MemberCheckInfoDto> queryMemberSignInInfo(Long memberId) {
		ResultDTO<MemberCheckInfoDto> result = new ResultDTO<MemberCheckInfoDto>();
		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			MemberCheckInfoDto dto = new MemberCheckInfoDto();
			dto.setSignIn(memberCheckManager.isChecked(memberId));
			dto.setTodayGainPopularity(memberCheckManager.getPopularityValueForChecked(memberId));
			dto.setTotalGainPopularity(memberCheckManager.countPopularityValueByMemberId(memberId));
			// 人气值总额
			Balance balance = balanceManager.queryBalance(memberId,
								TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
			if (balance != null) {
				dto.setPopularity(balance.getBalance().intValue());
			} else {
				dto.setPopularity(0);
			}
			dto.setBirthdayTips("祝你生日快乐！今天签到人气值翻10倍啦！");
			dto.setUrl(APIPropertiesUtil.getProperties("birthday.url"));
			if(StringUtils.isNotBlank(member.getTrueName()) && member.getBirthday() != null){
				int status = getBirthdayStatus(member.getBirthday());
				if(status == 1){
					Activity activity = memberManager.getBirthdayActivity();
					if(activity != null){
						if(DateUtils.getCurrentDate().before(activity.getStartTime())) {
							dto.setBirthday(false);
						}else{
							dto.setBirthday(true);
							setBirthdayCouponTemplate(member,dto);
						}
					}
				}else{
					dto.setBirthday(false);
				}
			}else{
				dto.setBirthday(false);
			}
			result.setResult(dto);
		} catch (ManagerException e) {
			result.setResultCode(ResultCode.ERROR);
			logger.error("获得签到信息发生异常", e);
		}
		return result;
	}
	
	@Override
	public String getAuditMemberInfosForm(Long memberId) throws Exception {
		return sinaPayClient.auditMemberInfos(memberId);
	}
	
	public int getBirthdayStatus(Date birthday){
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
	public Page<PopularityInOutLogDto> getPopularityInOutLog(BaseQueryParam query) {
		Page<PopularityInOutLogDto> pageList = new Page<PopularityInOutLogDto>();
		try {
			Page<PopularityInOutLog> log = popularityInOutLogManager.selectPopularityInOutLog(query);
			if(log != null){
				List<PopularityInOutLogDto> list = BeanCopyUtil.mapList(log.getData(), PopularityInOutLogDto.class);
				pageList.setData(list);
				pageList.setiDisplayLength(log.getiDisplayLength());
				pageList.setiDisplayStart(log.getiDisplayStart());
				pageList.setiTotalRecords(log.getiTotalRecords());
				pageList.setPageNo(log.getPageNo());
			}
		}catch (ManagerException e) {
			logger.error("获取用户人气值流水,query={}", query,e);
		}
		return pageList;
	}

	/**
	 * @Description:生日当天兑换的券的模板
	 * @param dto
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2015年12月8日 下午3:06:29
	 */
	private void setBirthdayCouponTemplate(Member member,MemberCheckInfoDto dto)throws ManagerException{
		CouponTemplate cashCoupon = couponTemplateManager.selectByPrimaryKey(PropertiesUtil.getCouponTemplateId50());
		CouponTemplate profitCoupon = couponTemplateManager.selectByPrimaryKey(PropertiesUtil.getCouponTemplateId001());
		CouponTemplateDto cashDto = new CouponTemplateDto(); 
		BeanCopyUtil.copy(cashCoupon, cashDto);
		CouponTemplateDto profitDto = new CouponTemplateDto(); 
		BeanCopyUtil.copy(profitCoupon, profitDto);
		if(cashCoupon!=null){
			if(StringUtil.isNotBlank(cashDto.getCouponAmountScope())){
				dto.setCashCouponAmountScope(cashDto.getCouponAmountScope());
			}
			if(StringUtil.isNotBlank(cashDto.getCouponDaysScope())){
				dto.setCashCouponDaysScope(cashDto.getCouponDaysScope());
			}
			if(StringUtil.isNotBlank(cashDto.getCouponPrivileges())){
				dto.setCashCouponPrivileges(cashDto.getCouponPrivileges());
			}
			if(StringUtil.isNotBlank(cashDto.getCouponValidity())){
				dto.setCashCouponValidity(cashDto.getCouponValidity());
			}
			dto.setCashAmount(cashCoupon.getAmount());
			dto.setReceiveCashCoupon(couponManager.isReceiveBirthdayCoupon(member.getId(), PropertiesUtil.getCoupon50ActvityId(), member.getBirthday()));
		}
		if(profitCoupon!=null){
			if(StringUtil.isNotBlank( profitDto.getCouponAmountScope())){
				dto.setProfitCouponAmountScope( profitDto.getCouponAmountScope());
			}
			if(StringUtil.isNotBlank( profitDto.getCouponDaysScope())){
				dto.setProfitCouponDaysScope( profitDto.getCouponDaysScope());
			}
			if(StringUtil.isNotBlank( profitDto.getCouponPrivileges())){
				dto.setProfitCouponPrivileges( profitDto.getCouponPrivileges());
			}
			if(StringUtil.isNotBlank( profitDto.getCouponValidity())){
				dto.setProfitCouponValidity( profitDto.getCouponValidity());
			}
			dto.setProfitAmount( profitDto.getAmount());
			dto.setReceiveProfitCoupon(couponManager.isReceiveBirthdayCoupon(member.getId(), PropertiesUtil.getCoupon001ActvityId(), member.getBirthday()));
		}
	}
	
	@Override
	public ResultDTO<Member> saveSignWay(Long memberId, int sighWay) {
		ResultDTO<Member> result = new ResultDTO<Member>();
		try {
			Member record = new Member();
			record.setId(memberId);
			record.setSignWay(sighWay);
			Member member = memberManager.selectByPrimaryKey(memberId);
			if(sighWay==1&&member.getIsAuth()==0){
				contractCoreManager.memberCa(memberId);
			}
			int num = memberManager.updateByPrimaryKeySelective(record);
			result.setIsSuccess();
		} catch (ManagerException e) {
			logger.error("保存用户签署方式异常memberId={},sighWay={}",memberId,sighWay);
			result.setResultCode(ResultCode.ERROR_SYSTEM_DATABASE_UPDATE);
		}
		return result;
	}
	
	@Override
	public ResultDTO<String> handlePayPassword(Long memberId, int handleType,Integer source ) {
		ResultDTO<String> rDTO = new ResultDTO<String>();
		try {
			if (memberId == null || handleType < TypeEnum.SET_PAY_PASSWORD.getType() || handleType > TypeEnum.FIND_PAY_PASSWORD.getType() ) {
				rDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				rDTO.setIsError();
				return rDTO;
			}
			String returnurl = APIPropertiesUtil.getProperties("sinaBankReturnUrl")+"?source="+source+"&type="+SinaBankMobileEnum.PAY_PASSWORD_M.getStatus();
			ResultDO<String> rDO = (ResultDO<String>) memberManager.handlePayPassword(memberId, handleType, returnurl);
			rDTO.setResultCode(rDO.getResultCode());
			rDTO.setResult(rDO.getResult());
			if(rDO.isSuccess()){
				rDTO.setIsSuccess();
			}else{
				rDTO.setIsError();
			}
			return  rDTO;
		} catch (ManagerException e) {
			logger.error("设置支付密码失败  memberId={}, handleType={}", memberId, handleType, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}
	
	@Override
	public ResultDTO<Boolean> synPayPasswordFlag(Long memberId) {
		ResultDTO<Boolean> rDTO = new ResultDTO<Boolean>();
		try {
			ResultDO<Boolean> rDO  = (ResultDO<Boolean>) memberManager.synPayPasswordFlag(memberId);
			rDTO.setResultCode(rDO.getResultCode());
			rDTO.setResult(rDO.getResult());
			if(rDO.isSuccess()){
				rDTO.setIsSuccess();
			}else{
				rDTO.setIsError();
			}
			return  rDTO;
		} catch (ManagerException e) {
			logger.error("同步是否设置支付密码失败  memberId={}", memberId, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}
	
	@Override
	public ResultDTO<String> handWithholdAuthority(Long memberId, int type,Integer source,int mType,Long orderId) {
		ResultDTO<String> rDTO = new ResultDTO<String>();
		try {
			if (memberId == null || type < TypeEnum.HANDLE_WITHHOLD_AUTHORITY.getType() || type > TypeEnum.RELIEVE_WITHHOLD_AUTHORITY.getType()) {
				rDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return rDTO;
			}
			String returnurl = "";
			if(mType==2){
				returnurl = APIPropertiesUtil.getProperties("sinaBankReturnUrl")+"?source="+source+"&type="+SinaBankMobileEnum.INVEST_WITHHOLD_AUTHORITY_M.getStatus()+"&orderId="+orderId;
			}else{
				returnurl = APIPropertiesUtil.getProperties("sinaBankReturnUrl")+"?source="+source+"&type="+SinaBankMobileEnum.WITHHOLD_AUTHORITY_M.getStatus();
			}			
			ResultDO<String> rDO = (ResultDO<String>) memberManager.handWithholdAuthority(memberId, type,returnurl);
			rDTO.setResultCode(rDO.getResultCode());
			rDTO.setResult(rDO.getResult());
			if(rDO.isSuccess()){
				rDTO.setIsSuccess();
			}else{
				rDTO.setIsError();
			}
			return  rDTO;
		} catch (ManagerException e) {
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			if (type == TypeEnum.HANDLE_WITHHOLD_AUTHORITY.getType()) {
				logger.error("[委托扣款授权]操作出现异常!", e); 
			} else if (type == TypeEnum.RELIEVE_WITHHOLD_AUTHORITY.getType()) {
				logger.error("[解除委托扣款]操作出现异常!", e); 
			}
		}
		return rDTO;
	}

	@Override
	public ResultDTO<Boolean> synWithholdAuthority(Long memberId)  {
		ResultDTO<Boolean> rDTO = new ResultDTO<Boolean>();
		try {
			ResultDO<Boolean> rDO  = (ResultDO<Boolean>) memberManager.synWithholdAuthority(memberId);
			rDTO.setResultCode(rDO.getResultCode());;
			rDTO.setResult(rDO.getResult());
			if(rDO.isSuccess()){
				rDTO.setIsSuccess();
			}else{
				rDTO.setIsError();
			}
			return rDTO;
		} catch (ManagerException e) {
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("同步是否开通委托扣款失败  memberId={}", memberId, e);
		}
		return rDTO;
	}
	
	

	@Override
	public ResultDTO<Object> createHostingDepositAndRechargeLog(Long memberId,
			String ip, RechargeDto dto,Integer source ) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		try {
			BigDecimal bigDecimal = new BigDecimal(dto.getAmount());
	        //充值金额必须大于0.01
	        if(bigDecimal.compareTo(new BigDecimal(Config.minrechargeAmount)) == -1){
	        	resultDTO.setResultCode(ResultCode.MEMBER_RECHARGE_MUST_MORE_ERROR);
	        	return resultDTO;
	        }
	        String tradeNo = SerialNumberUtil.generateRechargeNo(memberId);
	        TypeEnum type = TypeEnum.RECHARGELOG_TYPE_DIRECTLY;
	    	String returnurl = APIPropertiesUtil.getProperties("sinaBankReturnUrl")+"?source="+source+"&type="+SinaBankMobileEnum.RECHARGE_M.getStatus()+"&tradeNo="+tradeNo;
			if(dto.getRechargeType() == TypeEnum.RECHARGELOG_TYPE_TRADING.getType()){
				type = TypeEnum.RECHARGELOG_TYPE_TRADING;
				returnurl = APIPropertiesUtil.getProperties("sinaBankReturnUrl")+"?source="+source+"&type="+SinaBankMobileEnum.RECHARGE_INVESTMENT_M.getStatus()+"&tradeNo="+tradeNo;
			}
			ResultDO<Object> resultDO = memberManager.requestRecharge(memberId, ip, bigDecimal, type.getType(), dto.getOutAdvanceNo(),tradeNo,returnurl,true,source);
			resultDTO.setResultCode(resultDO.getResultCode());;
			resultDTO.setResult(resultDO.getResult());
			RechargeDto model = new RechargeDto();
			model.setTradeNo(tradeNo);
			Document doc = Jsoup.parse(resultDO.getResult().toString());
			Element content = doc.getElementById("form1");
			if(content!=null){
				Elements q=doc.getElementsByTag("input");
				for(Element link:q){
					if(link.attr("name").equals("ft")){
						model.setResultValue(link.attr("value"));
					}else if(link.attr("name").equals("errorDesc")){
						model.setErrorDesc(link.attr("value"));
					}else if(link.attr("name").equals("errorCode")){
						
					}
				}
				model.setResultUrl(content.attr("action"));
			}else{
				resultDTO.setResultCode(ResultCode.ERROR);
				resultDTO.setIsError();
				return resultDTO;
			}
			model.setResult(resultDO.getResult().toString());
			resultDTO.setResult(model);
			if(model.getResultValue()!=null){
				model.setResultUrl(content.attr("action")+"?ft="+model.getResultValue());
			}else if(model.getErrorDesc()!=null){
				model.setResultUrl(content.attr("action")+"?errorDesc="+model.getErrorDesc());
			}
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("托管充值(收银台模式), memberId={}, payerIp={}, rechargeAmount={},  orderNo={}", memberId, ip, dto.getAmount(),
					 dto.getOutAdvanceNo(), e);
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
    	return resultDTO;
    }

	@Override
	public ResultDTO<Object> createWithdrawAndWithdrawLog(Long memberId,
			String ip, BigDecimal withdrawAmount, BigDecimal freeWithdrawAmount,Integer source, BigDecimal fee) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		try {
			String withdrawNo = SerialNumberUtil.generateWithdrawNo(memberId);
			String returnurl = APIPropertiesUtil.getProperties("sinaBankReturnUrl")+"?source="+source+"&type="+SinaBankMobileEnum.WITHDRAWALS_M.getStatus()+"&withdrawNo="+withdrawNo;
			ResultDO<Object> resultDO = memberManager.createHostingWithdraw(memberId, ip, withdrawAmount, freeWithdrawAmount,returnurl,withdrawNo,fee,source);
			resultDTO.setResultCode(resultDO.getResultCode());;
			resultDTO.setResult(resultDO.getResult());
			RechargeDto model = new RechargeDto();
			model.setWithdrawNo(withdrawNo);
			Document doc = Jsoup.parse(resultDO.getResult().toString());
			Element content = doc.getElementById("form1");
			if(content!=null){
				model.setResultUrl(content.attr("action"));
				Elements q=doc.getElementsByTag("input");
				for(Element link:q){
					if(link.attr("name").equals("ft")){
						model.setResultValue(link.attr("value"));
					}else if(link.attr("name").equals("errorDesc")){
						model.setErrorDesc(link.attr("value"));
					}else if(link.attr("name").equals("errorCode")){
						
					}
				}
			}else{
				resultDTO.setResultCode(ResultCode.ERROR);
				resultDTO.setIsError();
				return resultDTO;
			}
			
			if(model.getResultValue()!=null){
				model.setResultUrl(content.attr("action")+"?ft="+model.getResultValue());
			}else if(model.getErrorDesc()!=null){
				model.setResultUrl(content.attr("action")+"?errorDesc="+model.getErrorDesc());
			}
			model.setResult(resultDO.getResult().toString());
			resultDTO.setResult(model);
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (Exception e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("[托管提现]出现异常  memberId={}", memberId, e);
			return resultDTO;
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<Object> modifyMobileByOldMobile(Long memberId, Long newMobile,
			Long mobile) {
		ResultDTO<Object> resulDTO = new ResultDTO<Object>();
        try {
        	ResultDO<Object> resultDO =this.memberManager.updateMobileByOldMobile(memberId, newMobile, mobile);
        	resulDTO.setResultCode(resultDO.getResultCode());
        	resulDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resulDTO.setIsSuccess();
			}else{
				resulDTO.setIsError();
			}
		} catch (Exception e) {
			resulDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("根据原手机号更新会员新手机号码，修改失败, mobile=" + mobile + ",newMobile=" + newMobile);
			return resulDTO;
		}
        return resulDTO;
	}

	@Override
	public ResultDTO<Object> modifyPasswordByNewMobile(Long newMobile,
			String newPassword) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		try {
			ResultDO<Object> resultDO= memberManager.updateNewMobilePassword(newMobile, null, null, newPassword);
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (ManagerException e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("新手机号重置密码，用手机短信验证方式，重置失败, mobile=" + newMobile + ",errormsg=" + e);
			return resultDTO;
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<Object> validateMemberInfo(Long mobile, String trueName,
			String identityNumber, String password) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		try {
			ResultDO<Object> resultDO= memberManager.validateMemberInfo(mobile, trueName, identityNumber, password);
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (ManagerException e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("根据会员身份信息更新手机号码，验证信息失败, mobile=" + mobile + ","
					+ "trueName=" + trueName + ",identityNumber=" + identityNumber);
			return resultDTO;
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<Object> modifyMobileByIdentity(Long mobile,
			Long newMobile, String trueName, String identityNumber) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		try {
			ResultDO<Object> resultDO= memberManager.updateMobileByIdentity(mobile, newMobile, trueName, identityNumber);
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (ManagerException e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("根据会员身份信息更新手机号码，修改失败, mobile=" + mobile + ",newMobile=" + newMobile + ","
					+ "trueName=" + trueName + ",identityNumber=" + identityNumber);
		}
		return resultDTO;
	}

	@Override
	public ResultDTO<Object> modifyPasswordByIdentity(Long newMobile,
			String trueName, String identityNumber, String newPassword) {
		ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		try {
			ResultDO<Object> resultDO=  memberManager.updateNewMobilePassword(newMobile, trueName, identityNumber, newPassword);
			resultDTO.setResultCode(resultDO.getResultCode());
			resultDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (ManagerException e) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("新手机号重置密码，验证身份信息方式，重置失败, mobile=" + newMobile + ",trueName=" + trueName + ",identityNumber=" + identityNumber + ",errormsg=" + e);
			return resultDTO;
		}
		return resultDTO;
	}
	
	
	/**
     * 托管充值(收银台模式)
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDTO<Object> createHostingDepositAndRechargeLogInvestment(Long memberId, String payerIp, BigDecimal rechargeAmount, TypeEnum type,
			String orderNo,Integer source,Long orderId) {
    	ResultDTO<Object> resultDTO = new ResultDTO<Object>();
		try {
			String tradeNo = SerialNumberUtil.generateRechargeNo(memberId);
			String returnurl = APIPropertiesUtil.getProperties("sinaBankReturnUrl")+"?source="+source+"&type="+SinaBankMobileEnum.INVESTMENT_RECHARGE_M.getStatus()+"&orderId="+orderId;
			ResultDO<Object> resultDO = memberManager.requestRecharge(memberId, payerIp, rechargeAmount, type.getType(), orderNo,tradeNo,returnurl,true,source);
			resultDTO.setResultCode(resultDO.getResultCode());
			String form = (String) resultDO.getResult();
			String  redirectUrl =this.getReturnUrlByForm(form);
			resultDTO.setResult(redirectUrl);
			
			if(resultDO.isSuccess()){
				resultDTO.setIsSuccess();
			}else{
				resultDTO.setIsError();
			}
		} catch (Exception e) {
			logger.error("托管充值(收银台模式), memberId={}, payerIp={}, rechargeAmount={}, type={}, orderNo={}", memberId, payerIp, rechargeAmount,
					type.getType(), orderNo, e);
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
    	return resultDTO;
    }
    
    private String getReturnUrlByForm(String form){
		
		if(StringUtil.isBlank(form)){
			return "";
		}
		String value="";
		String returnUrl="";
		String errorDesc="";
		Document doc = Jsoup.parse(form);
		Element content = doc.getElementById("form1");
		Elements q=doc.getElementsByTag("input");
		for(Element link:q){
			if(link.attr("name").equals("ft")){
				value = link.attr("value");
			}else if(link.attr("name").equals("errorDesc")){
				errorDesc = link.attr("value");
			}else if(link.attr("name").equals("errorCode")){
				
			}
		}
		if(content!=null){
			returnUrl = content.attr("action");
		}
		if(StringUtil.isNotBlank(value)){
			returnUrl = returnUrl+"?ft="+value;
		}else if(StringUtil.isNotBlank(errorDesc)){
			returnUrl = returnUrl+"?errorDesc="+errorDesc;
		}
		return returnUrl;
	}
    
    @Override
	public ResultDTO<Object> showMemberInfosSina(Long memberId,Integer source) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			String returnUrl="";
			if(source==3){
				returnUrl= APIPropertiesUtil.getProperties("sinaBankReturnUrl")+"?source="+source+"&type="+SinaBankMobileEnum.SAFETY_CERTIFICATION.getStatus();
			}
			resultDO= memberManager.showMemberInfosSina(memberId, returnUrl);
			rDTO.setResult(resultDO.getResult());
			if(resultDO.isSuccess()){
				rDTO.setIsSuccess();
			}else{
				rDTO.setIsError();
			}
		} catch (ManagerException e) {
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("获取用户信息新浪展示页面失败, memberId={}", memberId ,e);
			return rDTO;
		}
		return rDTO;
	}

	@Override
	public ResultDTO<AutoInvestSet> queryAutoInvest(Long memberId) {
		ResultDTO<AutoInvestSet> resultDO = new ResultDTO<AutoInvestSet>();
		try {
			AutoInvestSet autoInvest=autoInvestSetManager.selectByMemberId(memberId);
			if(autoInvest!=null){
				autoInvest.setOpen(true);
				if(autoInvest.getInvestFlag()==StatusEnum.INVEST_FLAG_OPEN.getStatus()){
					AutoInvestSet biz=autoInvestMapper.countMemberIdSort(memberId);
					if(biz!=null){
						autoInvest.setSort(biz.getSort());
					}
				}
				if(autoInvest.getStartTime()!=null){
					autoInvest.setStartTimeStr(DateUtils.getDateStrFromDate(autoInvest.getStartTime()));
				}
				if(autoInvest.getEndTime()!=null){
					autoInvest.setEndTimeStr(DateUtils.getDateStrFromDate(autoInvest.getEndTime()));
				}
			}else{
				autoInvest=new AutoInvestSet();
				autoInvest.setOpen(false);
			}
			resultDO.setIsSuccess();
			resultDO.setResult(autoInvest);
		} catch (Exception e) {
			resultDO.setIsError();
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("查询自动投标设置失败 memberId=" + memberId   +  ",errormsg=" + e);
			return resultDO;
		}
		return resultDO;
	}

	@Override
	public Object autoInvestSet(Long memberId, int investFlag) {
		ResultDTO<Object> resultDO = new ResultDTO<Object>();
		AutoInvestSet biz=new AutoInvestSet();
		biz.setInvestFlag(investFlag);
		biz.setUpdateTime(DateUtils.getCurrentDate());
		biz.setSortTime(DateUtils.getCurrentDate());
		biz.setMemberId(memberId);
		int i=0;
		//开启
		if(investFlag==StatusEnum.INVEST_FLAG_OPEN.getStatus()){
			//是否开通委托支付
			Member member =memberMapper.selectByPrimaryKey(memberId);
			if (member == null
					|| StatusEnum.WITHHOLD_AUTHORITY_FLAG_Y.getStatus() != member.getWithholdAuthorityFlag()) {
				resultDO.setResultCode(ResultCode.AUTO_INVEST_AUTHORITY_ERROR);
				return resultDO;
			}
			AutoInvestSet autoInvest=	autoInvestMapper.selectByMemberId(memberId);
			if(autoInvest==null){
				//resultDO.setResultCode(ResultCode.AUTO_INVEST_NO_SAVE_ERROR);
				//return resultDO;
				//biz.setStartTime(DateUtils.getCurrentDate());
				//biz.setEndTime(DateUtils.addMonth(DateUtils.getCurrentDate(), 1));
				biz.setCreateTime(DateUtils.getCurrentDate());
				//biz.setInvestFrequency(1);
				//biz.setProjectType("1,2");
				//biz.setPeriodMin(1);
				//biz.setPeriodMax(3);
				//biz.setAmount(new BigDecimal(1000));
				i=autoInvestMapper.insertSelective(biz);
			}else{
				if(autoInvest.getInvestFlag()==StatusEnum.INVEST_FLAG_OPEN.getStatus()){
					resultDO.setResultCode(ResultCode.AUTO_INVEST_HAD_OPEN_ERROR);
					return resultDO;
				}
				i=autoInvestMapper.updateByPrimaryMemberIdSelective(biz);
			}
			if(i>0){
				AutoInvestSet setSort=autoInvestMapper.countMemberIdSort(memberId);
				if(setSort!=null){
					resultDO.setResult(setSort.getSort());
				}
				//resultDO.setResultCode(ResultCode.AUTO_INVEST_OPEN_SUCCESS);
				resultDO.setIsSuccess();
				return resultDO;
			}else{
				resultDO.setIsError();
				return resultDO;
			}
			
		//关闭	
		}else{
			AutoInvestSet autoInvest=	autoInvestMapper.selectByMemberId(memberId);
			if(autoInvest==null){
				resultDO.setResultCode(ResultCode.AUTO_INVEST_NO_SAVE_ERROR);
				return resultDO;
			}
			if(autoInvest.getInvestFlag()!=StatusEnum.INVEST_FLAG_OPEN.getStatus()){
				resultDO.setResultCode(ResultCode.AUTO_INVEST_HAD_CLOSE_ERROR);
				return resultDO;
			}
			i=autoInvestMapper.updateByPrimaryMemberIdSelective(biz);
			if(i>0){
				//resultDO.setResultCode(ResultCode.AUTO_INVEST_CLOSE_SUCCESS);
				resultDO.setIsSuccess();
				return resultDO;
			}else{
				resultDO.setIsError();
				return resultDO;
			}
			
		}
	}

	@Override
	public ResultDTO<AutoInvestSet> saveAutoInvestSetByMemberId(
			AutoInvestSetDto autoInvestDto) {
		ResultDTO<AutoInvestSet> resultDO = new ResultDTO<AutoInvestSet>();
		if(autoInvestDto.getStartTime()==null||autoInvestDto.getEndTime()==null||autoInvestDto.getInvestFrequency()==null||
				autoInvestDto.getProjectType()==null||autoInvestDto.getPeriodMin()==null||autoInvestDto.getPeriodMax()==null||autoInvestDto.getAmount()==null){
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			return resultDO;
		}
		if(autoInvestDto.getPeriodMin()>autoInvestDto.getPeriodMax()){
			resultDO.setResultCode(ResultCode.AUTO_SET_PERIOD_ERROR);
			return resultDO;
		}
		if(autoInvestDto.getStartTime().after(autoInvestDto.getEndTime())){
			resultDO.setResultCode(ResultCode.AUTO_SET_TIME_ERROR);
			return resultDO;
		}
		try {
		AutoInvestSet autoInvest=	autoInvestSetManager.selectByMemberId(autoInvestDto.getMemberId());
		AutoInvestSet biz=new AutoInvestSet();
		BeanCopyUtil.copy(autoInvestDto, biz);
		if(autoInvest==null){
			biz.setCreateTime(DateUtils.getCurrentDate());
			biz.setUpdateTime(DateUtils.getCurrentDate());
			biz.setSortTime(DateUtils.getCurrentDate());
			int i=autoInvestMapper.insertSelective(biz);
			if(i>0){
				resultDO.setIsSuccess();
				return resultDO;
			}else{
				resultDO.setIsError();
				return resultDO;
			}
		}else{
			biz.setUpdateTime(DateUtils.getCurrentDate());
			int i=autoInvestSetManager.updateByPrimaryMemberIdSelective(biz);
			if(i>0){
				resultDO.setIsSuccess();
				return resultDO;
			}else{
				resultDO.setIsError();
				return resultDO;
			}
		}
		} catch (Exception e) {
			logger.error("保存自动投标设置失败 memberId=" + autoInvestDto.getMemberId()   +  ",errormsg=" + e);
		}
		return resultDO;
	}

	@Override
	public int countRegisterNumberByDate(Date date) {
		try {
			return memberManager.countRegisterNumberByDate(date);	
		} catch (ManagerException e) {
			logger.error("统计注册人数失败  ", e);				
		}
		return 0;
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
		private Long memberId;
		public ActivityThread(final Long _memberId){
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
