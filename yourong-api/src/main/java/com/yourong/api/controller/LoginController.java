package com.yourong.api.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.yourong.api.cache.RedisAPIMemberClient;
import com.yourong.api.dto.LoginDto;
import com.yourong.api.dto.MemberDto;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.ResetPasswordDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.LoginService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.TokenService;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.api.utils.SysServiceUtils;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.AES;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Identities;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.RegexUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberToken;

/**
 * 登陆，注册，验证用户名， 忘记密码
 *
 * @author pengyong
 *
 */
@Controller
public class LoginController extends BaseController {

	private static final Integer MEMBER_LOGIN_COUNT = Integer.valueOf(APIPropertiesUtil.getProperties("member.login.count"));

	private static final Integer APP_SENDSMS_COUNT = Integer.valueOf(APIPropertiesUtil.getProperties("app.user.sendsms.count"));

	private static  final  String  APP_EXIT_MESSAGE ="您的账号于%s在%s客户端上登录了。";
	
	private static final String REGISTER_MOBILE = "Register_mobile";


	@Autowired
	private MemberService memberService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private SmsMobileSend smsMobileSend;

	@Autowired
	private TokenService tokenService;

	/**
	 *  登陆
	 * @param form
	 * @param result
	 * @param request
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "logining",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO<Object> logining(@Valid  @ModelAttribute("form") LoginDto form,BindingResult result,
									HttpServletRequest request, HttpServletResponse resp) {
		ResultDTO<Object> resultRe = new ResultDTO<Object>();
		validateResult(resultRe, result);
		if(resultRe.proessError()){
			return resultRe;
		}
	//	Integer count = loginCount(form.getUsername());
		String ip = getIp(request);
		form.setLoginIp(ip);
//		// 如果登陆次数大于 5次， 不允许登陆，或者，以后做锁定登陆用户名
	//	if (count > MEMBER_LOGIN_COUNT) {
//			resultRe.setResultCode(ResultCode.MEMBER_LOGIN_COUNT_ERROR);
//			return resultRe;
	//	}
		ResultDO<Member> resultDO = loginService.login(form);
		if (resultDO.isSuccess()) {
			Long id = resultDO.getResult().getId();
			String encodeBase64String = generateEncryTokenAndSaveToken(id, form.getDevice(), form.getEquipment(), form.getLoginSource(),form.getChannelId());
			//登陆成功后，清空登陆次数
			RedisMemberClient.removeMemberLoginCount(form.getUsername());
			// 登录成功后，清空短信发送次数
            RedisMemberClient.removeMemberMobileSMSCount(resultDO.getResult().getMobile());
            RedisMemberClient.removeMemberMobileVoiceCount(resultDO.getResult().getMobile());
			resultRe.setIsSuccess();
			Map map = Maps.newHashMap();
			Member member = resultDO.getResult();
			if(StringUtil.isNotBlank(member.getAvatars())){
				member.setAvatars(PropertiesUtil.getAliyunUrl()+"/"+member.getAvatars());
			}
			MemberDto memberDto= BeanCopyUtil.map(member, MemberDto.class);
		    map.put("token",encodeBase64String);
			map.put("member",memberDto);
			map.put("title","有融网");
			map.put("shareContent","我正在用有融网APP投资理财，快和我一起来吧！");
			map.put("shareUrl",PropertiesUtil.getMstationRootUrl()+"/mstation/landing/inviteRegister?inviteCode_shortURL="+memberDto.getShortUrl());
			SPParameter  parameter =new SPParameter();
			parameter.setMemberId(member.getId());
			SPEngine.getSPEngineInstance().run(parameter);
			resultRe.setResult(map);
		} else {
			resultRe.setResultCodeList(resultDO.getResultCodeList());
		}
		// 前台 根据登陆次数显示验证码
		//resultRe.setResult(count);
		return resultRe;
	}

	/**
	 * 生成Token,并且保存到数据库
	 * @param id
	 * @param device
	 * @param equipment
	 * @param type
	 * @return
	 */
	private String generateEncryTokenAndSaveToken(Long id, String device, String equipment, Integer type,String channelId){
		String token = Identities.uuid2();
		MemberToken memberToken = new MemberToken();
		memberToken.setMemberId(id);
		memberToken.setDevice(device);
		memberToken.setEquipment(equipment);
		memberToken.setTokenType(type);
		memberToken.setToken(token);
		memberToken.setChannelId(channelId);
		MemberToken oldMemberToken = tokenService.queryLastLoginDeviceByMemberId(id);
		boolean isPushMessage = false;
		String pushMessage = "";
		if(oldMemberToken != null && StringUtil.isNotBlank(oldMemberToken.getChannelId())  &&(!StringUtil.equals(oldMemberToken.getChannelId(), channelId,false))){
			String time = DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_10);
			pushMessage  = String.format(APP_EXIT_MESSAGE, time, equipment);
			isPushMessage = true;
		}
		tokenService.cleanAppClientTokenAndPushMessage(id,isPushMessage,pushMessage);
		tokenService.insertSelective(memberToken);
		String encodeBase64String =AES.getInstance().encryptToken(id,token);
		return encodeBase64String;
	}
	
	@RequestMapping(value = "updatephone/checkMobileCode",method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	@ResponseBody
	public ResultDTO<Object> checkMobileCode1(HttpServletRequest request,
			HttpServletResponse response) {
        int type = ServletRequestUtils.getIntParameter(request, "type", 0);
        ResultDTO<Object> result = new ResultDTO<Object>();
        long mobile = ServletRequestUtils.getLongParameter(request, "mobile", 0);
        Long id = getMemberID(request);
     // 修改手机号验证旧手机验证码
     		int checkType = ServletRequestUtils.getIntParameter(request, "checkType", 0);
     		if (checkType == 1) {
     			mobile = 0;
     			MemberSessionDto memberDto = getMember();
     			if (memberDto != null) {
     				mobile = memberDto.getMobile();
     			}else{
     				Member biz = memberService.selectByPrimaryKey(id);
     				if(biz!=null){
     					mobile=biz.getMobile();
     				}
     			}
     		}
     		// 获取手机号码有误
     		if (mobile == 0) {
     			result.setResultCode(ResultCode.ERROR_SYSTEM_REQUEST_PARAM_DECODE);
     			return result;
     		}
        boolean checkSmsCodeCounts = checkSmsCodeCounts(mobile);
        if (checkSmsCodeCounts){
            result.setResultCode(ResultCode.ERROR);
            return result;
        }
        if (type == 1)
           return checkSessionValue(request, mobile);
        if (type == 2)
        	return checkSessionValue(request,mobile);
        	
        return result;
    }
	private ResultDTO<Object> checkSessionValue(HttpServletRequest request, Long mobile) {
    	ResultDTO<Object> result = new ResultDTO<Object>();
        HttpSession session = getHttpSession(request);
       // String token = (String) session.getAttribute(key);
        String code = ServletRequestUtils.getStringParameter(request, "code", null);
        /*long mobile = ServletRequestUtils
                .getLongParameter(request, "mobile", 0);*/
        if(StringUtil.isNotBlank(code) && mobile > 0){
			Member member = memberService.selectByMobile(mobile);
			if(member != null){
				String code2 = RedisAPIMemberClient.getUserMobileSMScode(member.getMobile());
				if(StringUtil.isNotBlank(code2) && code.equals(code2)){
					//RedisAPIMemberClient.setUserUpdatePasswordFlag(mobile, "2");
					RedisAPIMemberClient.removeUserMobileSMSCode(mobile);
					 Integer smscount = RedisMemberClient.getMemberMobileSMSCount(mobile);
				     Integer voiceCount = RedisMemberClient.getMemberMobileVoiceCount(mobile);
				     Integer counts = smscount + voiceCount;
				    result.setResult(counts);
					result.setIsSuccess();
					return result;
				}else{
					result.setIsError();
					result.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
					return result;
				}
			}else{
				result.setIsError();
				result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return result;
			}
		}else{
			result.setIsError();
			result.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
			return result;
		}
        
    }
	 @RequestMapping(value = "updatephone/checkMobileAndSendCode",method = RequestMethod.POST,headers = {"Accept-Version=1.7.0"})
	 @ResponseBody
	public ResultDTO<Integer> checkMobileAndSendCode1(@RequestParam("mobile") String smobile,HttpServletRequest req,   HttpServletResponse resp) throws ServletRequestBindingException {
	        // 发送手机验证码 ，放进session里
	        int type = ServletRequestUtils.getIntParameter(req, "type", 1);
	        long mobile = ServletRequestUtils.getLongParameter(req, "mobile", 0);

	        int isCheckMobile = ServletRequestUtils.getIntParameter(req, "isCheckMobile", 0);
	        ResultDTO<Integer> result = new ResultDTO<Integer>();
	        Long id = getMemberID(req);
	        Member member = memberService.selectByMobile(mobile);
	        // 注册页面 手机号码不存在
	        if (isCheckMobile == 1 && member != null) {
	            result.setResultCode(ResultCode.MEMBER_IS_EXIST);
	            return result;
	        }
	        // 忘记密码  手机号码存在
	        if (isCheckMobile == 2 && member == null) {
	            result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
	            return result;
	        }
	        // 修改手机号，发送旧手机号验证码
	     		if (isCheckMobile == 4) {
	     			MemberSessionDto memberDto = getMember();
	     			if (memberDto != null) {
	     				mobile = memberDto.getMobile();
	     			}else{
	     				Member biz = memberService.selectByPrimaryKey(id);
	     				if(biz!=null){
	     					mobile=biz.getMobile();
	     				}
	     			}
	     		}
	     		if(isCheckMobile==3){
	     			Member checkMobile = memberService.selectByMobile(mobile);
	     			if(checkMobile!=null){
	     				result.setResultCode(ResultCode.MEMBER_PHONE_NUMBER_REGISTED_ERROR);
	    	            return result;
	     			}
	     		}
	     	// 获取手机号码有误
	     	if (!RegexUtils.checkMobile(mobile+"")) {
	     		result.setResultCode(ResultCode.ERROR_SYSTEM_REQUEST_PARAM_DECODE);
	    		return result;
	    	}
	        Integer countsms = RedisMemberClient.getMemberMobileSMSCount(mobile);
	        Integer countvoice = RedisMemberClient.getMemberMobileVoiceCount(mobile);
	        Integer count = countsms + countvoice;
	        if (count >= Constant.SMSVOICECOUNT) {
	            result.setResult(count);
	            result.setResultCode(ResultCode.REGIST_COUNT);
	            return result;
	        }
	        HttpSession session = getHttpSession(req); // 发送短信
	        session.setAttribute(REGISTER_MOBILE, mobile);
	        if (type == 1) {
	            String smsContent = Identities.randomNumberLength(Constant.SEND_SMS_LENGTH);
	            RedisAPIMemberClient.setUserMobileSMScode(mobile,smsContent);
	            session.setAttribute(Constant.SMS_CONTENT_CODE, smsContent);
	            ResultDO<Integer> sendSMSDefault = new ResultDO<Integer>();
	            logger.info(String.format("短信随机验证码,手机号： %d,验证码：%s", mobile, smsContent));
	            if (PropertiesUtil.isDev()) {
	                sendSMSDefault = sendSMSDefault.setResult(0);
	            } else {
	                sendSMSDefault = smsMobileSend.sendSMSVerificationCode(mobile, smsContent);
	            }
	            //发送成功 才记录次数
	            if (sendSMSDefault.getResult() == 0) {
	                remberSendSmsCount(mobile);
	            }
	        } else {
	            // 语音 验证码
	            String smsContent = Identities.randomNumberLength(Constant.SEND_SMS_LENGTH);
	            session.setAttribute(Constant.VOICE_CONTENT_CODE, smsContent);
	            if (PropertiesUtil.isDev()) {
	                logger.debug("语音随机验证码 smsContent--" + smsContent);
	                ResultDO<Integer> sendVoice = new ResultDO<Integer>();
	                sendVoice.setSuccess(true);
	                if (sendVoice.isSuccess()) {
	                    remberSendVoiceCount(mobile);
	                }
	            } else {
	                logger.debug("短信随机验证码 smsContent--" + smsContent);
	                ResultDO<Integer> sendVoice = smsMobileSend.sendVoice(mobile, smsContent);
	                if (sendVoice.isSuccess()) {
	                    remberSendVoiceCount(mobile);
	                }
	            }
	        }
	        result.setResult(count);
	        return result;
	    }
	 
	 /**
	     * 记录语音次数
	     *
	     * @param
	     * @return
	     */
	    private Integer remberSendVoiceCount(Long mobile) {
	        RedisMemberClient.setMemberMobileVoiceCount(mobile);
	        // 语音次数
	        Integer count = RedisMemberClient.getMemberMobileVoiceCount(mobile);
	        return count;
	    }
	/**
	 * 验证会员并发送短信
	 *
	 * @param req
	 * @param resp
	 * @return
	 * @throws org.springframework.web.bind.ServletRequestBindingException
	 */
	@RequestMapping(value = "register/checkMobileAndSendCode",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO<Object> checkMobileAndSendCode(@RequestParam("mobile") String smobile,HttpServletRequest req,   HttpServletResponse resp) throws ServletRequestBindingException {
		boolean checkMobile = RegexUtils.checkMobile(smobile);
		ResultDTO<Object> resultobject = new ResultDTO<Object>();
		Long	mobile = Long.valueOf(smobile);
		if (!checkMobile){
			resultobject.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return  resultobject;
		}
		Member member = memberService.selectByMobile(mobile);
		if (member == null) {
			Integer countsms = RedisMemberClient.getMemberMobileSMSCount(mobile);
			if(countsms >= APP_SENDSMS_COUNT){
				resultobject.setResult(countsms);
				resultobject.setResultCode(ResultCode.MEMBER_API_SMS_COUNT_EROOR);
				return  resultobject;
			}
			//发送验证码
			String smsContent = Identities.randomNumberLength(4);
			RedisAPIMemberClient.setUserMobileSMScode(mobile,smsContent);
			ResultDO<Integer> sendSMSDefault  = new ResultDO<Integer>();
			logger.info(String.format("短信随机验证码,手机号： %d,验证码：%s", mobile, smsContent));
			if (PropertiesUtil.isDev()) {
				sendSMSDefault =  sendSMSDefault.setResult(0);
			} else {
				sendSMSDefault = smsMobileSend.sendSMSVerificationCode(mobile,smsContent);
			}
			//发送成功 才记录次数
			if(sendSMSDefault.getResult() == 0){
				remberSendSmsCount(mobile);
				resultobject.setIsSuccess();
				return  resultobject;
			}else{
				resultobject.setResultCode(ResultCode.MOBILE_SYSTEM_SEND_SMS_ERROR);
				return  resultobject;
			}
		} else {
			if (member.getStatus()==0){
				resultobject.setResultCode(ResultCode.MEMBER_FOZONE_ERROR);
				return resultobject;
			}
			if(member.getStatus() == 1){
				resultobject.setResultCode(ResultCode.MEMBER_NOT_ACTIVATION_ERROR);
				return resultobject;
			}
			resultobject.setResultCode(ResultCode.MEMBER_IS_EXIST);
		}
		return resultobject;
	}
	 @RequestMapping(value = "updatephone/checkMember", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	    @ResponseBody
	    public ResultDO<Object> checkMember(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
	        ResultDO<Object> resultobject = new ResultDO<Object>();
	        Member member = getMemberByType(req);
	        if (member == null) {
	            resultobject.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
	        } else {

	            if (member.getStatus() == 0) {
	                resultobject.setResultCode(ResultCode.MEMBER_FOZONE_ERROR);
	                return resultobject;
	            }
	            if (member.getStatus() == 1) {
	                resultobject.setResultCode(ResultCode.MEMBER_NOT_ACTIVATION_ERROR);
	                return resultobject;
	            }
	            resultobject.setResultCode(ResultCode.MEMBER_IS_EXIST);
	            int getTrueName = ServletRequestUtils.getIntParameter(req, "getTrueName", 0);
	            // 带 掩码的 真实姓名
	            if (getTrueName == 1) {
	                String[] result = {StringUtil.maskString(member.getUsername(), StringUtil.ASTERISK, 1, 1, 3), member.getShortUrl(), member.getStatus().toString()};
	                resultobject.setResult(result);
	            }
	        }
	        return resultobject;

	    }
	 
	  private Member getMemberByType(HttpServletRequest req)
	            throws ServletRequestBindingException {

	        int type = ServletRequestUtils.getIntParameter(req, "type", 0);
	        Member member = null;
	        // 验证手机号码是否存在
	        if (type == 1) {
	            Long mobile = ServletRequestUtils.getLongParameter(req, "code");
	            member = memberService.selectByMobile(mobile);
	        }
	        // 验证用户昵称
	        if (type == 2) {
	            String name = ServletRequestUtils.getStringParameter(req, "code");
	            member = memberService.selectByUsername(name);
	        }
	        //
	        if (type == 3) {
	            String shortUrl = ServletRequestUtils.getStringParameter(req, "data");
	            member = memberService.getMemberByShortUrl(shortUrl);
	        }
	        return member;
	    }
	/**
	 * 记录发短信的次数
	 * @param mobile
	 * @return
	 */
	private Integer remberSendSmsCount(Long mobile) {
		RedisMemberClient.setMemberMobileSMSCount(mobile);
		// 短信次数
		Integer count = RedisMemberClient.getMemberMobileSMSCount(mobile);
		return count;
	}

	/**
	 * 记录登入次数
	 * @param userName
	 * @return
	 */
	private Integer loginCount(String userName) {
		// 登陆次数 放进redis
		RedisMemberClient.setMemberLoginCount(userName);
		Integer count = RedisMemberClient.getMemberLoginCount(userName);
		return count;
	}
		/**
		 * 保存会员
		 *
		 * @param member
		 * @param result
		 * @param req
		 * @param resp
		 * @return
		 */
		@RequestMapping(value = "register/saveMember",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
		@ResponseBody
		public ResultDTO<Object> saveMember(	@Valid MemberDto member,		BindingResult result, HttpServletRequest req,	HttpServletResponse resp) {
			ResultDTO<Object> resultObject = new ResultDTO<Object>();
			validateResult(resultObject, result);
			if(resultObject.proessError()){
				return resultObject;
			}
			boolean checkMobile = RegexUtils.checkMobile(member.getUsername());
			if (!checkMobile){
				resultObject.setResultCode(ResultCode.ERROR_SYSTEM_PARAM);
				return  resultObject;
			}
			Long mobile = Long.valueOf(member.getUsername());
			//验证次数，防止暴力破解
			if (checkSmsCodeCounts(mobile)){
				resultObject.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return  resultObject;
			}
			member.setMobile(mobile);
			member.setUsername(null);
			String ip = getIp(req);
			member.setIp(ip);
			String smscode  = getSmsContents(req, mobile);
			
			logger.info(mobile+"用户输入短信验证码："+member.getPhonecode()+",系统验证码："+smscode);
			
			if(!StringUtil.equals(smscode, member.getPhonecode()) ){
				resultObject.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
				return resultObject;
			}
			if(member.getReferralMobile() !=null && member.getReferralMobile() != 0L){
				Member m = memberService.selectByMobile(member.getReferralMobile());
				if(m == null){
					resultObject.setResultCode(ResultCode.MEMBER_ERRO_TUIJIAN_EROOR);
					return resultObject;
				}
			}
			if (resultObject.isSuccess()) {
				try {
					ResultDO<Object> register = loginService.register(member);
				} catch (Exception e) {
					logger.error("注册异常", e);
					resultObject.setResultCode(ResultCode.ERROR_SYSTEM);
				}
				if (resultObject.isSuccess()) {
					loginService.initOtherMemberData(member);
					Member username = memberService.selectByMobile(mobile);
					if (SysServiceUtils.isMstation(req)){
						mstationHandle(req,member, resultObject, username);
					}else {
						apihandle(member, resultObject, username);
					}

					RedisMemberClient.setMemberInviteCode(username.getShortUrl(), username.getId());
					String generateEncryToken = generateEncryTokenAndSaveToken(username.getId(), member.getDevice(), member.getEquipment(), member.getLoginSource(),member.getChannelId());
					resultObject.setIsSuccess();
					
					
					Map map = Maps.newHashMap();
					MemberDto memberDto= BeanCopyUtil.map(username, MemberDto.class);
					map.put("token",generateEncryToken);
					map.put("member",memberDto);
					map.put("title","有融网");
					map.put("shareContent","我正在用有融网APP投资理财，快和我一起来吧！");
					map.put("shareUrl",PropertiesUtil.getWebRootUrl()+"/security/register/?inviteCode_shortURL="+memberDto.getShortUrl());
					resultObject.setResult(map);
				}
			}
			return resultObject;
		}

	/**
	 * 获取短信内容
	 *
	 * @param req
	 * @param mobile
	 * @return
	 */
	private String getSmsContents(HttpServletRequest req, Long mobile) {
		String smscode = "";
		if (SysServiceUtils.isMstation(req)) {
			smscode = RedisAPIMemberClient.getUserMobileSMScode(mobile);
		} else {
			smscode = RedisAPIMemberClient.getUserMobileSMScode(mobile);
		}
		return smscode;
	}

	/**
	 *  m站保存session
	 * @param req
	 * @param member
	 * @param resultObject
	 * @param username
	 */
	private  void mstationHandle(HttpServletRequest req,MemberDto member, ResultDTO<Object> resultObject, Member username){
			RedisMemberClient.setMemberInviteCode(username.getShortUrl(), username.getId());
			String userAgent = req.getHeader("User-Agent");
			if (StringUtil.isNotBlank(userAgent) && userAgent.length() >255){
				userAgent = userAgent.substring(0, 255);//如果用户代理数据长度超过255，则截取255字符保存
			}
			addMemberToSession(req, username,member.getLoginSource(),userAgent);
			resultObject.setIsSuccess();
		}
		private void addMemberToSession(HttpServletRequest request, Member mber,Integer loginSource,String userAgent) {
			MemberSessionDto dto = BeanCopyUtil.map(mber, MemberSessionDto.class);
			dto.setLoginSource(loginSource);
			dto.setUserAgent(userAgent);
			request.getSession().setAttribute(Constant.CURRENT_USER, dto);
		}

	/** api 接口处理用户登陆
	 * @param member
	 * @param resultObject
	 * @param username
	 */
	private void apihandle(MemberDto member, ResultDTO<Object> resultObject, Member username) {
		String generateEncryToken = generateEncryTokenAndSaveToken(username.getId(), member.getDevice(), member.getEquipment(), member.getLoginSource(), member.getChannelId());
		resultObject.setIsSuccess();
		Map map = Maps.newHashMap();
		MemberDto memberDto = BeanCopyUtil.map(username, MemberDto.class);
		map.put("token", generateEncryToken);
		map.put("member", memberDto);
		resultObject.setResult(map);
	}

	/**
	 * 会员退出
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "security/logout", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO<Object> logout(HttpServletRequest req, HttpServletResponse resp) {
		Long id = getMemberID(req);
		tokenService.deleteMemberTokenByMemberID(id);
		ResultDTO<Object> resultDTO = new ResultDTO<>();
		resultDTO.setIsSuccess();
		return resultDTO;
	}

//	private String getIp(HttpServletRequest req) {
//		String ip = req.getHeader("X-Forwarded-For");
//		if(StringUtil.isBlank(ip)){
//            ip = req.getRemoteAddr();
//        }else {
//            ip ="127.0.0.1";
//        }
//		return ip;
//	}

	/**
	 * 获得修改密码的Key（短信验证码）
	 * @param smobile
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "getUpdatePasswordKey",method = RequestMethod.POST,headers = {"Accept-Version=1.0.1"})
	@ResponseBody
	public ResultDTO<Object> getUpdatePasswordKey(@RequestParam("mobile") String smobile,HttpServletRequest req,   HttpServletResponse resp) throws ServletRequestBindingException {
		boolean checkMobile = RegexUtils.checkMobile(smobile);
		ResultDTO<Object> resultobject = new ResultDTO<Object>();
		if (!checkMobile){
			resultobject.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return  resultobject;
		}
		Long	mobile = Long.valueOf(smobile);
		Member member = memberService.selectByMobile(mobile);
		if (member != null) {
			if (member.getStatus()==0){
				resultobject.setResultCode(ResultCode.MEMBER_FOZONE_ERROR);
				return resultobject;
			}
			if(member.getStatus() == 1){
				resultobject.setResultCode(ResultCode.MEMBER_NOT_ACTIVATION_ERROR);
				return resultobject;
			}
			
			Integer countsms = RedisMemberClient.getMemberMobileSMSCount(mobile);
			if(countsms >= APP_SENDSMS_COUNT){
				resultobject.setResult(countsms);
				resultobject.setResultCode(ResultCode.MEMBER_API_SMS_COUNT_EROOR);
				return  resultobject;
			}
			
			//发送验证码
			String smsContent = Identities.randomNumberLength(4);
			RedisAPIMemberClient.setUserMobileSMScode(mobile, smsContent);
			ResultDO<Integer> sendSMSDefault  = new ResultDO<Integer>();
			logger.info(String.format("短信随机验证码,手机号： %d,验证码：%s", mobile, smsContent));
			if (PropertiesUtil.isDev()) {
				sendSMSDefault =  sendSMSDefault.setResult(0);
			} else {
				sendSMSDefault = smsMobileSend.sendSMSVerificationCode(mobile,smsContent);
			}
			//发送成功 才记录次数
			if(sendSMSDefault.getResult() == 0){
				remberSendSmsCount(mobile);
				RedisAPIMemberClient.setUserUpdatePasswordFlag(mobile, "1");
				resultobject.setIsSuccess();
				return  resultobject;
			}else{
				resultobject.setResultCode(ResultCode.MOBILE_SYSTEM_SEND_SMS_ERROR);
				return  resultobject;
			}
		} else {
			resultobject.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
		}
		return resultobject;
	}

	/**
	 *  检查手机短信验证码是否正确
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "checkMobileCode",method = RequestMethod.POST, headers = {"Accept-Version=1.0.1"})
	@ResponseBody
	public ResultDTO<Object> checkMobileCode(HttpServletRequest request,
			HttpServletResponse response) {
		ResultDTO result = new ResultDTO();
		String code = ServletRequestUtils.getStringParameter(request, "code", null);
		String username = ServletRequestUtils.getStringParameter(request, "mobile", null);
		boolean checkMobile = RegexUtils.checkMobile(username);
		if (!checkMobile){
			result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return  result;
		}
		Long mobile = Long.parseLong(username);
		if (checkSmsCodeCounts(mobile)){
			result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return  result;
		}
		if(StringUtil.isNotBlank(code) && mobile > 0){
			Member member = memberService.selectByMobile(mobile);
			if(member != null){
				String code2 = RedisAPIMemberClient.getUserMobileSMScode(member.getMobile());
				if(StringUtil.isNotBlank(code2) && code.equals(code2)){
					RedisAPIMemberClient.setUserUpdatePasswordFlag(mobile, "2");
					RedisAPIMemberClient.removeUserMobileSMSCode(mobile);
					result.setIsSuccess();
					return result;
				}
			}else{
				result.setResult(ResultCode.MEMBER_NOT_EXIST_ERROR);
			}
		}
		result.setResultCode(ResultCode.MOBILE_SYSTEM_VOICE_VALIDATOR_ERROR);
		return result;
	}

	private boolean checkSmsCodeCounts(long mobile) {
		boolean  result = false;
		//记录验证次数
		RedisMemberClient.setUserCheckCount(mobile);
		//如果验证次数大于5次
		Integer userCheckCount = RedisMemberClient.getUserCheckCount(mobile);
		if (userCheckCount >= Constant.USER_MEMBER_COUNTS){
			result = true;
		}
		return result;
	}
	
	/**
	 * 重置密码
	 * @param memberDto
	 * @param result
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "updatePassword",method = RequestMethod.POST, headers = {"Accept-Version=1.0.1"})
	@ResponseBody
	public ResultDTO<Object> updatePasswordBymobile(@Valid ResetPasswordDto memberDto,BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
		ResultDTO<Object> resultDO = new ResultDTO<Object>();
		validateResult(resultDO, result);		
		if(!resultDO.isSuccess()){
			return resultDO;
		}	
		boolean checkMobile = RegexUtils.checkMobile(memberDto.getUsername());
		if (!checkMobile){
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return  resultDO;
		}
		Long mobile = Long.valueOf(memberDto.getUsername());
		if (checkSmsCodeCounts(mobile)){
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return  resultDO;
		}
		Member member = memberService.selectByMobile(mobile);
		if(member != null){
			String flag = RedisAPIMemberClient.getUserUpdatePasswordFlag(member.getMobile());
			if(StringUtil.isNotBlank(flag) && flag.equals("2")){
				//验证两次密码是否一样
				if(!StringUtil.equals(memberDto.getPassword(), memberDto.getRepassword(),true)){
					resultDO.setResultCode(ResultCode.MEMBER_RE_PASSWORD_ERROR);
				    return resultDO;	
				}
				resultDO = memberService.updatePasswordByMobile(member.getId(),member.getMobile(), memberDto.getPassword());
				if(resultDO.isSuccess()){
					RedisAPIMemberClient.removeUserUpdatePasswordFlag(mobile);
					resultDO.setIsSuccess();
					return resultDO;
				}
			}
		}else{
			resultDO.setResult(ResultCode.MEMBER_NOT_EXIST_ERROR);
		}
		resultDO.setResultCode(ResultCode.ERROR);
		return resultDO;
	}

}
