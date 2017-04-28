package com.yourong.backend.uc.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yourong.backend.fin.service.BalanceService;
import com.yourong.backend.uc.service.MemberService;
import com.yourong.common.annotation.BussAnnotation;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.MemberType;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyType;
import com.yourong.common.thirdparty.sinapay.common.util.DateUtil;
import com.yourong.common.thirdparty.sinapay.member.common.Constants;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Identities;
import com.yourong.common.util.OSSUtil;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.push.PushEnum;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.ContractCoreManager;
import com.yourong.core.uc.dao.MemberOpenMapper;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberNotifySettingsManager;
import com.yourong.core.uc.manager.MemberOpenManager;
import com.yourong.core.uc.manager.MemberTokenManager;
import com.yourong.core.uc.manager.MemberVerifyManager;
import com.yourong.core.uc.manager.MemberVipManager;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;
import com.yourong.core.uc.model.MemberOpen;
import com.yourong.core.uc.model.MemberVerify;
import com.yourong.core.uc.model.biz.MemberBiz;
import com.yourong.core.uc.model.biz.MemberInfoBiz;

@Service
public class MemberServiceImpl implements MemberService {

    private static Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);
    @Autowired
    private MemberNotifySettingsManager memberNotifySettingsManager;
    @Autowired
    private MemberManager memberManager;

    @Autowired
    private MemberVerifyManager memberVerifyManager;

    @Autowired
    private SinaPayClient sinaPayClient;


    @Autowired
    private BalanceService balanceService;

    @Autowired
    private TaskExecutor threadPool;
    @Autowired
    private CouponManager couponManager;

    @Autowired
    private SmsMobileSend smsMobileSend;
    @Autowired
    private MemberTokenManager memberTokenManager;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private BalanceManager balanceManager;
    
    @Autowired
	private MemberInfoManager memberInfoManager;
    
    @Autowired
    private ContractCoreManager contractCoreManager;
	
    @Autowired
    private BscAttachmentManager bscAttachmentManager;
    
    @Autowired
    private AttachmentIndexManager attachmentIndexManager;
    
    @Autowired
	private SysDictManager sysDictManager;
    
    @Autowired
   	private MemberOpenMapper memberOpenMapper;
    
    @Autowired
    private MemberVipManager memberVipManager;

	@Autowired
	private MemberOpenManager memberOpenManager;


    @BussAnnotation(moduleName = "客户管理分页查询", option = "查询")
    public Page<MemberBiz> findByPage(Page<MemberBiz> pageRequest,
                                   Map<String, Object> map) {
        try {
            memberManager.findMemberBizByPage(pageRequest, map);
        } catch (ManagerException e) {
            logger.error("findByPage", e);
        }
        return pageRequest;
    }

    @Override
    public int batchDelete(long[] ids) {
        try {
            if (ids.length < 1) {
                return 0;
            }
            return memberManager.batchDelete(ids);
        } catch (ManagerException e) {
            logger.error("batchDelete", e);
        }
        return 0;
    }

    @Override
    public Member getMemberById(Long id) {
        try {
            return memberManager.selectByPrimaryKey(id);
        } catch (ManagerException ex) {
            logger.error("getMemberById", ex);
        }
        return null;
    }

    @Override
    public List<Member> getMemberInfoByTrueName(String trueName, String memberType) {
        try {
            Map<String, Object> paramsMap = Maps.newHashMap();
            paramsMap.put("trueName", trueName);
            paramsMap.put("memberType", memberType);
            return memberManager.getMemberInfoByTrueName(paramsMap);
        } catch (ManagerException ex) {
            logger.error("根据姓名获取会员信息失败,trueName=" + trueName + "memberType=" + memberType, ex);
        }
        return null;
    }
    
    @Override
    public List<Member> getMemberInfoByNameIdentity(String trueName, String identityNumber, String memberType) {
        try {
            Map<String, Object> paramsMap = Maps.newHashMap();
            if (StringUtil.isNotBlank(trueName)) {
            	paramsMap.put("trueName", trueName);
            }
            if (StringUtil.isNotBlank(identityNumber)) {
            	paramsMap.put("identityNumber", identityNumber);
            }
            if (StringUtil.isNotBlank(memberType)) {
            	paramsMap.put("memberType", memberType);
            }
            return memberManager.getMemberInfoByTrueName(paramsMap);
        } catch (ManagerException ex) {
            logger.error("根据姓名获取会员信息失败,trueName=" + trueName + ",identityNumber=" + identityNumber + ",memberType=" + memberType, ex);
        }
        return null;
    }
    
    private void authThirdPay(long memberID, String realName, String certificateNumber, ResultDO<Object> resultDO, Long mobie, String clientIp) throws Exception {
        //绑定手机
        String verifyEntity = mobie.toString();
        ResultDto<?> verify = sinaPayClient.queryVerify(memberID, VerifyType.MOBILE);
        if (verify != null && Constants.VERIFY_NOT_EXIST.equalsIgnoreCase(verify.getErrorCode())) {
            ResultDto<?> resultDto = sinaPayClient.bindingVerify(memberID, verifyEntity, VerifyType.MOBILE, clientIp);
            if (resultDto.isError()) {
                resultDO.setSuccess(false);
                resultDO.setResult(resultDto.getErrorMsg());
                return;
            }
        }else{
            resultDO.setSuccess(false);
            return;
        }
        //实名认证
        ResultDto<?> dto = sinaPayClient.setRealName(memberID, VerifyStatus.Y, certificateNumber, realName, clientIp);
        if (dto == null || dto.isError()) {
            resultDO.setSuccess(false);
            resultDO.setResult(dto.getErrorMsg());
            return;
        }
//        try{
//            couponManager.sendCoupon(memberID, TypeEnum.COUPON_SEND_SOURCE_TYPE_IDENTITY_BIND, null);
//        }catch (Exception e){
//            logger.error("");
//        }
    }

    public ResultDO<Object> insertAutch(Member memberDto) throws ManagerException {
        ResultDO<Object> resultDO = new ResultDO<Object>();
        try {
            //更新身份证已经生日信息
            Date birthday = DateUtils.getDateFromString(memberDto.getIdentityNumber().substring(6, 14), DateUtils.DATE_FMT_0);
            int sex = StringUtil.getSexByIdentityNumber(memberDto.getIdentityNumber());
            int rs = memberManager.updateIdentityNumberById(memberDto.getTrueName(), memberDto.getIdentityNumber(), memberDto.getId(), birthday, sex);
            if (rs > 0) {
                //添加认证信息
                MemberVerify record = new MemberVerify();
                record.setVerifyOperate(Constant.ENABLE);
                record.setVerifyType(TypeEnum.MEMBER_VERIFY_TYPE_IDENTITY.getCode());
                record.setMemberId(memberDto.getId());
                record.setVerifyContent(memberDto.getTrueName() + StringUtil.CARET + memberDto.getIdentityNumber());
                record.setCreateTime(DateUtils.getCurrentDateTime());
                record.setClientIp(memberDto.getRegisterIp());
                memberVerifyManager.insertSelective(record);
                resultDO.setSuccess(true);
                
                SPParameter  parameter =new SPParameter();
        		parameter.setMemberId(memberDto.getId());
        		SPEngine.getSPEngineInstance().run(parameter);
            } else {
                //认证失败
                resultDO.setResultCode(ResultCode.MEMBER_AUTH_IDENTITY_ERROR);
            }
        } catch (ManagerException e) {
            logger.error(ResultCode.MEMBER_AUTH_IDENTITY_ERROR.getMsg(), e);
            throw new ManagerException(e);

        }
        return resultDO;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ResultDO<Object> register(Member memberDto) throws Exception {
        ResultDO<Object> result = new ResultDO<Object>();
        try {
            Member member = this.memberManager.selectByMobile(memberDto.getMobile());
            if (member != null) {
                result.setResultCode(ResultCode.MEMBER_PHONE_NUMBER_ISEXIST_ERROR);
                return result;
            }
            if (StringUtil.isBlank(memberDto.getTrueName()) || StringUtil.isBlank(memberDto.getIdentityNumber())) {
                result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
                result.setResult(ResultCode.MEMBER_AUTH_IDENTITY_EXIT.getMsg());
                result.setSuccess(false);
                return result;
            }
            if (memberManager.isExitMemberByIdentityNumber(memberDto.getIdentityNumber())) {
                result.setResultCode(ResultCode.MEMBER_AUTH_IDENTITY_EXIT);
                result.setResult(ResultCode.MEMBER_AUTH_IDENTITY_EXIT.getMsg());
                result.setSuccess(false);
                return result;
            }
            SysDict sysDict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
            String clientIp = "";
            if(sysDict != null) {
            	clientIp = sysDict.getValue();
            }
            memberDto.setRegisterIp(clientIp);
            int i = insertMemberAndPayAccount(memberDto);
            if(i>0){
            	if(StringUtil.isNotBlank(memberDto.getChannelBusiness())){
            		// 获取渠道编号前缀
					SysDict openDict = sysDictManager.findByGroupNameAndKey("channel_key", memberDto.getChannelBusiness());
					if (openDict != null) {
						// 插入会员渠道绑定关系
						MemberOpen memberOpen = new MemberOpen();
						memberOpen.setMemberId(memberDto.getId());
						memberOpen.setOpenPlatformKey(memberDto.getChannelBusiness());
						memberOpen.setOpenId(SerialNumberUtil.generateOpenNo(memberDto.getId(), openDict.getDescription()));
						memberOpen.setStatus(1);
						memberOpenManager.insertMemberOpen(memberOpen);
					}
				}
				authThirdPay(memberDto.getId(), memberDto.getTrueName(), memberDto.getIdentityNumber(), result, memberDto.getMobile(),
						clientIp);
            }
            if (result.isSuccess()) {
                result = insertAutch(memberDto);
            } else {
                ResultDto<?> verify = sinaPayClient.unbindingVerify(memberDto.getId(), VerifyType.MOBILE, clientIp);
                throw new Exception("实名认证失败");
            }
        } catch (Exception e) {
            logger.error("插入会员失败  MemberDto=" + memberDto, e);
            result.setSuccess(false);
            result.setResultCode(ResultCode.MEMBER_REGISTER_ERROR);
            throw e;
        }
        return result;
    }

    /**
     * 创建会员和 激活第三方支付账户
     *
     * @param
     * @return
     * @throws ManagerException
     */
    private int insertMemberAndPayAccount(Member member) throws ManagerException {
        member.setRegisterTime(DateUtils.getCurrentDateTime());
        member.setDelFlag(Constant.ENABLE);
        member.setSex(StatusEnum.MEMBER_SEX_UN.getStatus());
        member.setStatus(StatusEnum.MEMBER_STATUS_ACTIVE.getStatus());
        if(!StringUtil.isBlank(member.getChannelBusiness())){
        	member.setStatus(StatusEnum.MEMBER_STATUS_UN_ACTIVE.getStatus());
        }
        // member.setMemberType(1);
        member.setRegisterMethod(StatusEnum.MEMBER_SOURCE_BACKEND.getStatus());
        String shortUrl = Identities.randomBase62(6);
        Member shotMember = memberManager.getMemberByShortUrl(shortUrl);
        if (shotMember == null) {
            member.setShortUrl(shortUrl);
        } else {
            member.setShortUrl(Identities.randomBase62(6));
        }
        Long mobile = member.getMobile();
        String password = member.getPassword();
        String decryptByYourong = CryptHelper.decryptByYourong(mobile.toString(), password);
        member.setPassword(decryptByYourong);
        int i = memberManager.insertSelective(member);
        if (i > 0) {
            try {
                String name = SerialNumberUtil.PREFIX_UID + member.getId();
                ResultDto<?> activateMemberResult = sinaPayClient.createActivateMember(member.getId(), MemberType.PERSONAL, member.getRegisterIp());
                if (activateMemberResult != null && activateMemberResult.isSuccess()) {
                    return i;
                } else {
                    String message = activateMemberResult == null ? "返回结果异常" : activateMemberResult.getErrorMsg();
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

    private class MemberRegisterServiceThread implements Runnable {
        private Member memberDto;
        private Member member;

        public MemberRegisterServiceThread(final Member memberDto) {
            this.memberDto = memberDto;
        }

        @Override
        public void run() {
            registerService();
        }

        public void registerService() {
            try {
                member = memberManager.selectByMobile(memberDto.getMobile());
                if (member != null) {
                    //推荐
                    //referService();
                    //初始化用户消息配置项
                    memberNotifySettingsManager.initMemberNotifySettings(member.getId());

                    //初始化资金表
                    balanceService.initBalance(member.getId(), TypeEnum.BALANCE_TYPE_PIGGY);
                    //初始化存钱收益
                    balanceService.initBalance(member.getId(), TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY);
                    //初始化人气值
                    balanceService.initBalance(member.getId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
//					// 登陆日志
//					MemberLogin memberLogin = new MemberLogin();
//					memberLogin.setMemberId(member.getId());
//					memberLogin.setLoginType(TypeEnum.MEMBER_LOGIN_TYPE_MOBILE.getType());
//					memberLogin.setLoginSource(TypeEnum.MEMBER_LOGIN_SOURCE_PC.getType());
//					memberLogin.setLoginIp(memberDto.getIp());
//					memberLogin.setId(member.getId());
//					logger.info("会员id"+member.getId()+"登陆成功");
//					memberLoginManager.insert(memberLogin);
                    //插入手机认证信息
                    SysDict sysDict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
                    String clientIp = "";
                    if(sysDict != null) {
                    	clientIp = sysDict.getValue();
                    }
                    insertMemberVerify(member.getMobile(), member, clientIp);
                    //增加平台注册人数redis
                    RedisPlatformClient.addMemberCount(1);

                }
            } catch (ManagerException ex) {
                logger.error("注册服务失败", ex);
            }
        }

        /**
         * 注册推荐
         * @throws ManagerException
         */
//		private void referService() throws ManagerException{
//			String referCode = memberDto.getReferCode();
//			if(StringUtil.isNotBlank(referCode)  && referCode.length() == 6){
//				Long mobile = memberDto.getMobile();
//				if(StringUtil.isNumeric(referCode) && referCode.length() == 11){//手机
//					//手动输入手机号码,referCode 为不加掩码的 真实手机号码
//					Member m = memberManager.selectByMobile(Long.valueOf(referCode));
//					insertMemberRefer(m);
//				} else if(referCode.length() == 6){//推荐码固定是六位，如果超出就没必要去数据库找了
//				  //手动输入 邀请码  ，referCode 为 随机6位数字+字母
        //		Member m = memberManager.getMemberByShortUrl(referCode);
        //	insertMemberRefer(m);
//				}else if(memberDto.getReferral() != 0){
//				      //打开链接 邀请注册 ，referCode 为 139******1111;
//				    Member m = memberManager.selectByPrimaryKey(memberDto.getReferral());
//					insertMemberRefer(m);
//				}
//			}
//		}
//		private void insertMemberRefer(Member m) throws ManagerException {
//			if(m != null){
//				MemberRefer refer = new MemberRefer();
//				refer.setReferral(m.getId());//推荐人
//				refer.setReferred(member.getId());//被推荐人
//				refer.setReferSource(memberDto.getReferSource());//推荐来源
//				refer.setReferType(StatusEnum.MEMBER_REFER_TYPE_REGIST.getStatus());//推荐方式
//				refer.setCreateTime(DateUtils.getCurrentDateTime());
//				memberReferManager.insertSelective(refer);
//				logger.info("用户"+m.getMobile()+"推荐"+member.getMobile()+"注册成功");
//			}
//		}

    }

    @Override
    public void initOtherMemberData(Member record) {
        threadPool.execute(new MemberRegisterServiceThread(record));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ResultDO<Object> changeMobile(Long id, Long newMobile) throws Exception {
        ResultDO<Object> resultDO = new ResultDO<>();
        Member nullObject = this.memberManager.selectByMobile(newMobile);
        if (nullObject != null) {
            resultDO.setSuccess(false);
            resultDO.setResultCode(ResultCode.MEMBER_PHONE_NUMBER_ISEXIST_ERROR);
            return resultDO;
        }
        Member member = this.memberManager.selectByPrimaryKey(id);
        String verifyEntity = newMobile.toString();
        ResultDto<?> verify = sinaPayClient.queryVerify(id, VerifyType.MOBILE);
        if (verify.isError()) {
            resultDO.setSuccess(false);
            resultDO.setResult(verify.getErrorMsg());
            return resultDO;
        }
        
        SysDict sysDict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
        String clientIp = "";
        if(sysDict != null) {
        	clientIp = sysDict.getValue();
        }
        //解绑手机号码
        ResultDto<?> resultDto = sinaPayClient.unbindingVerify(id, VerifyType.MOBILE, clientIp);
        if (resultDto.isError()) {
            resultDO.setSuccess(false);
            resultDO.setResult(resultDto.getErrorMsg());
            return resultDO;
        }
        //解绑成功后
        resultDto = sinaPayClient.bindingVerify(id, verifyEntity, VerifyType.MOBILE, clientIp);
        if (resultDto.isError()) {
            resultDO.setSuccess(false);
            resultDO.setResult(resultDto.getErrorMsg());
            return resultDO;
        }
        insertMemberVerify(newMobile, member, clientIp);
        //更改手机号码
        member.setMobile(newMobile);
        int i = this.memberManager.updateMobileByid(member);
        if (i > 0) {
            resultDO.setSuccess(true);
            cleanAppClientTokenAndPushMessage(member.getId());
            return resultDO;
        }
        return resultDO;
    }
    //清空APP端token
    private void cleanAppClientTokenAndPushMessage(Long memberId)  {
        try {
            memberTokenManager.cleanAppClientTokenAndPushMessage(memberId,true,"您的手机已修改。", PushEnum.MEMBER_UPDATE_PASSWORD);
        } catch (ManagerException e) {
            logger.error("清空app端token失败,memberID:" + memberId, e);
        }
    }
    //插入认证信息
    private void insertMemberVerify(Long newMobile, Member member, String clientIp) throws ManagerException {
        MemberVerify memberVerify = new MemberVerify();
        memberVerify.setVerifyOperate(Constant.ENABLE);
        memberVerify.setVerifyType(TypeEnum.MEMBER_VERIFY_TYPE_MOBILE.getCode());
        memberVerify.setMemberId(member.getId());
        memberVerify.setVerifyContent(newMobile.toString());
        memberVerify.setClientIp(clientIp);
        memberVerifyManager.insertSelective(memberVerify);
    }

	@Override
	public List<Member> selectMember(Map<String, Object> map){
		try {
			return memberManager.selectMember(map);
		} catch (ManagerException e) {
			logger.error("用户查询出现异常", e);
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int cancelMember(Long id) throws Exception {
		Member cancelMember = new Member();
		cancelMember.setId(id);
		cancelMember.setStatus(0);
		int i = memberManager.updateByPrimaryKeySelective(cancelMember);
		if(i > 0){
			memberNotifySettingsManager.unsubscribe(id);
		}
		return i;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Enterprise> saveMemberEnterprise(Enterprise enterprise) throws Exception {
		ResultDO<Enterprise> resultDO = new ResultDO<Enterprise>();
		BigDecimal creditAmount = enterprise.getCreditAmount()==null?BigDecimal.ZERO:enterprise.getCreditAmount();
		if (enterprise.getId() != null) {
			//填写信用额度的情况
			Balance companyCreditAmount = balanceManager.queryBalance(enterprise.getId(), TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
			if(companyCreditAmount!=null){//已保存过信用额度//更新
				//不能小于已使用的信用额度
				BigDecimal usedCredit = companyCreditAmount.getBalance().subtract(companyCreditAmount.getAvailableBalance());
				if(creditAmount.compareTo(usedCredit)>=0){
					balanceManager.updateBalanceByID(creditAmount,creditAmount.subtract(usedCredit),companyCreditAmount.getId());
				}else{
					resultDO.setResultCode(ResultCode.MEMBER_ENTERPRISE_MODIFY_CREDIT_LESS_THEN_USED_ERROR);
					return resultDO;
				}
			}else{
				//插入信用额度
				balanceManager.insertBalance(TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT,creditAmount,enterprise.getId());
			}
			enterpriseManager.updateByPrimaryKeySelective(enterprise);
		} else {
			enterprise.setDelFlage(Constant.ENABLE);
			enterprise.setCreateTime(DateUtil.getCurrentTS());
			enterpriseManager.insertSelective(enterprise);
			//插入信用额度
			balanceManager.insertBalance(TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT,creditAmount,enterprise.getId());
		}
		return resultDO;
	}
	
	@Override
	public List<Enterprise> getEnterpriseByName(String name) {
		try {
			return enterpriseManager.getEnterpriseByName(name);
		} catch (Exception e) {
			logger.error("根据企业名称查询企业信息失败，name=",name,e);
		}
		return null;
	}
    @Override
    public ResultDO<Object> addReferral(Long referredId, Long referralMobile) {
        try {
            return memberManager.addReferral(referredId, referralMobile);
        } catch (ManagerException e) {
            logger.error("添加推荐人异常, referredId={}, referralMobile={}", referredId, referralMobile, e);
        }
        return null;
    }



	
	@Override
	public List<Enterprise> getEnterpriseByLegalName(String legalname) {
		try {
			return enterpriseManager.getEnterpriseByLegalName(legalname);
		} catch (Exception e) {
			logger.error("根据企业法人名称查询企业信息失败，name=",legalname,e);
		}
		return null;
	}

	@Override
	public Object bindingVerifyOnly(Long memberId, Long mobile) {
		ResultDto<?> dto = new ResultDto<Object>();
		try {
			SysDict sysDict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
            String clientIp = "";
            if(sysDict != null) {
            	clientIp = sysDict.getValue();
            }
			dto = sinaPayClient.bindingVerify(memberId, mobile.toString(), VerifyType.MOBILE, clientIp);
		} catch (Exception e) {
			dto.setSuccess(false);
			logger.error("只绑定新浪认证信息失败，memberId={}, mobile={}", memberId, mobile, e);
		}
		return dto;
	}


	 @Override
	 public ResultDto<Object> cancellationMember(Long mobile) throws Exception  {
	    	ResultDto<Object> resultDto = new ResultDto<>();
	        Member nullObject = this.memberManager.selectByMobile(mobile);
	        
	        if(nullObject==null){
	        	 resultDto.setSuccess(false);
		         resultDto.setErrorCode(ResultCode.MEMBER_NOT_EXIST_ERROR.getMsg());
		         return resultDto;
	        }
	        //根据trueName和Identitynumber判断用户是否实名认证过，若已实名认证则直接返回
	        if (StringUtil.isNotBlank(nullObject.getTrueName())||StringUtil.isNotBlank(nullObject.getIdentityNumber()) ) {
	            resultDto.setSuccess(false);
	            resultDto.setErrorCode(ResultCode.MEMBER_TRUE_NAME_IDENTITY_ISEXIST.getMsg());
	            return resultDto;
	        }
	        //判断用户是否绑定过认证信息，没有则直接返回
	        ResultDto<?> verify = sinaPayClient.queryVerify(nullObject.getId(), VerifyType.MOBILE);
	        if(verify!= null && Constants.VERIFY_NOT_EXIST.equalsIgnoreCase(verify.getErrorCode())){
	        	this.frozenMemberByMobile(mobile);
	        	resultDto.setSuccess(true);
				return resultDto;
	        }
	        	 
	      //若用户绑定过认证信息，则申请解绑
	        SysDict sysDict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
            String clientIp = "";
            if(sysDict != null) {
            	clientIp = sysDict.getValue();
            }
	        ResultDto<?> resultDo = sinaPayClient.unbindingVerify(nullObject.getId(), VerifyType.MOBILE, clientIp);
	        if (resultDo.isError()) {
	        	resultDto.setSuccess(false);
	        	resultDto.setErrorCode(resultDo.getErrorMsg());
	            return resultDto;
	        }
	        //解绑成功后，把当前账户置为无效，同时修改手机号为无效字段
	        this.frozenMemberByMobile(mobile);
        	resultDto.setSuccess(true);
			return resultDto;
	      
	    }
	 
	 private int frozenMemberByMobile(Long mobile){
	        try {
	        Long newMobile =  memberManager.getMaxMobile();
	        	 return memberManager.frozenMemberByMobile(newMobile+1, mobile);
			} catch (ManagerException e) {
				logger.error("冻结用户失败, mobile={}", mobile, e);
			}
	        return 0;
	 }
	 
	 @Override
	 public boolean synThirdPayEaring(Long memberId)  {
	        try {
	        	balanceManager.synThirdPayEaring(memberId);
	        } catch (ManagerException e) {
	            logger.error("同步存钱余额失败,memberID:" + memberId, e);
	            return false ;
	        }
	        return true ;
	}
	/**
	 * 
	 * @desc 保存会员信息
	 * @param memberInfoBiz
	 * @return
	 * @author chaisen
	 * @throws ManagerException 
	 * @time 2016年4月5日 上午10:28:15
	 *
	 */
	@Override
	public Object saveUcMemberInfo(MemberInfoBiz memberInfoBiz) throws ManagerException {
		ResultDto<Object> result = new ResultDto<>();
		Member member = memberManager.selectByPrimaryKey(memberInfoBiz.getMemberId());
		if(member == null){
			result.setSuccess(false);
			return result;
		}
		MemberInfo info = memberInfoManager.getMemberInfoByMemberId(memberInfoBiz.getMemberId());
		if(info != null){//更新
			MemberInfo memberInfo = new MemberInfo();
			BeanCopyUtil.copy(memberInfoBiz, memberInfo);
			memberInfo.setId(info.getId());
			memberInfo.setMemberId(info.getMemberId());
			memberInfoManager.updateMemberInfoByMemberId(memberInfo);
		}else{//添加
			MemberInfo memberInfo = new MemberInfo();
			BeanCopyUtil.copy(memberInfoBiz, memberInfo);
			memberInfoManager.saveMemberInfoByMemberId(memberInfo);
		}
		result.setSuccess(true);
		return result;
	}
	
	@Override
	public List<Member> selectListByMobile(Long mobile)  {
		try {
			Map<String, Object> paramsMap = Maps.newHashMap();
	        paramsMap.put("mobile", mobile);
			return memberManager.selectListByMobile(paramsMap);
		} catch (Exception e) {
			logger.error("根据手机号查询会员信息失败,mobile:" + mobile, e);
		}
		return null;
	}
	

	@Override
	public ResultDO<Object> enterpriseCa(Long enterpriseId) {
		
		ResultDO<Object> result  = new ResultDO<Object>();
		ResultDO<Object> rDO = contractCoreManager.enterpriseCa(enterpriseId);
		if(rDO.isSuccess()){
			Enterprise enterprise  =  enterpriseManager.selectByKey(enterpriseId);
			result.setSuccess(true);
			result.setResult(enterprise);
			return result;
		}
		result.setSuccess(false);
		return  result;
	}
	
	@Override
	public ResultDO<Object> saveImage(Long enterpriseId,List<BscAttachment> stampAttachment,String appPath) {
		
		ResultDO<Object> result = new ResultDO<Object>();
		Enterprise enterprise = this.enterpriseManager.selectByKey(enterpriseId);
		BscAttachment stamp = stampAttachment.get(0);
		
		String imgType = stamp.getFileExt().replace(".","");
		String imgName = stamp.getFileName();
		String filePath = appPath+stamp.getFileUrl();

		//TODO   附件上传阿里云，本地化信息
		ResultDO<Object> res = contractCoreManager.uploadImage(enterprise.getId(), imgType, filePath, imgName); 
		String url = (String) res.getResult();
		if (StringUtil.isBlank(url)) {
			result.setSuccess(false);
			return result;
        }
		this.updateEnterprise(enterprise.getId(), url, stamp);
		return result;
	}
	
	private void updateEnterprise(Long id,String url,BscAttachment stamp){
		//更新上传图片记录
		Enterprise record =new Enterprise();
		record.setId(id);
		record.setIsStamp(1);
		enterpriseManager.updateByPrimaryKeySelective(record);
		//
		stamp.setModule("stamp");
		stamp.setFileUrl(OSSUtil.getSimpleImageUrl(url));
		stamp.setStorageWay(TypeEnum.ATTACHMENT_STORAGE_WAY_OSS.getCode());
		try {
			bscAttachmentManager.insertSelective(stamp);
			attachmentIndexManager.deleteAttachmentIndexByKeyId(id.toString());
			attachmentIndexManager.insertAttachmentIndex(stamp.getId(), id.toString());
		} catch (ManagerException e) {
			logger.error("保存企业图章信息失败,企业id={},url={}" ,id,url, e);
		}
		
         
	}
	
	@Override
	public Enterprise caEnterprise(Long id) {
		
		Enterprise enterprise = this.enterpriseManager.selectByKey(id);
		try {
			BscAttachment bscAttachment =bscAttachmentManager.getBscAttachmentByKeyIdAndModule(enterprise.getId().toString(), "stamp");
			enterprise.setBscAttachment(bscAttachment);
		} catch (ManagerException e) {
			logger.error("获取企业ca信息和图章信息失败,id={}," ,id,e);
		}
		
		return enterprise;
		
	}

	
	@Override
	public void memberLevelUpHandle(){
		memberVipManager.memberLevelUpHandle();
	}

	@Override
	public List<Enterprise> getEnterpriseByRegisNo(String regisNo) {
		try {
			return enterpriseManager.getEnterpriseByRegisNo(regisNo);
		} catch (Exception e) {
			logger.error("根据注册号查询企业信息失败，regisNo=",regisNo,e);
		}
		return null;
	}
	
	@Override
	public Member getMemberByMobile(Long mobile) {
		try {
            return memberManager.selectByMobile(mobile);
        } catch (ManagerException ex) {
            logger.error("getMemberByMobile", ex);
        }
        return null;
	}
}
