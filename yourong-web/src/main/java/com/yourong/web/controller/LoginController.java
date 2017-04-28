package com.yourong.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Splitter;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.member.common.Constants;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Identities;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.RegexUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.common.util.ValidateCode;
import com.yourong.common.web.SavedRequest;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberToken;
import com.yourong.web.dto.LoginDto;
import com.yourong.web.dto.MemberDto;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.dto.MemberUpdatePasswordDto;
import com.yourong.web.service.BannerService;
import com.yourong.web.service.LoginService;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.SysDictService;
import com.yourong.web.service.TransactionService;
import com.yourong.web.utils.ServletUtil;

/**
 * 登陆，注册，验证用户名， 忘记密码
 * 
 * @author pengyong
 *
 */
@Controller
@RequestMapping("security")
public class LoginController extends BaseController {

	private static final String MEMBER_UPDATE_PASSWORD_ID = "member-update-password-id";
	@Autowired
	private MemberService memberService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private BannerService bannerService;

	@Autowired
	private SmsMobileSend smsMobileSend;

	private static final String REGISTER_MOBILE = "Register_mobile";

	private static final String WEIXIN_ID = "weixin_id";

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private SysDictService sysDictService;

	/**
	 * 微信注册页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "mRegister", method = RequestMethod.GET)
	public ModelAndView registerByMobile(HttpServletRequest req,
			HttpServletResponse resp) {
		String shortUrl = (String) getHttpSession(req).getAttribute(INVITECODE);

		getWeixinIDAddSession(req);
		Member member = null;
		if (StringUtil.isNotBlank(shortUrl) && shortUrl.length() == 6) {
			member = memberService.getMemberByShortUrl(shortUrl);
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("member/login/mRegister");
		if (member != null) {
			model.addObject("mobile", StringUtil.maskMobile(member.getMobile()));
			model.addObject("shortUrl", member.getShortUrl());
			model.addObject("referSource", "1");
			model.addObject("tureName",
					StringUtil.maskTrueName(member.getTrueName()));
			model.addObject("disabled", "disabled");
		} else {
			model.addObject("referSource", "2");
		}

		return model;
	}

	/**
	 * 注册页面wap
	 * 
	 * @return
	 */
	@RequestMapping(value = "/landing/mRegister", method = RequestMethod.GET)
	public ModelAndView registerByMobileLanding(HttpServletRequest req,
			HttpServletResponse resp) {
		String shortUrl = (String) getHttpSession(req).getAttribute(INVITECODE);
		Member member = null;
		if (StringUtil.isNotBlank(shortUrl) && shortUrl.length() == 6) {
			member = memberService.getMemberByShortUrl(shortUrl);
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("/landing/mRegister");
		if (member != null) {
			model.addObject("mobile", StringUtil.maskMobile(member.getMobile()));
			model.addObject("shortUrl", member.getShortUrl());
			model.addObject("referSource", "1");
			model.addObject("tureName",
					StringUtil.maskTrueName(member.getTrueName()));
			model.addObject("disabled", "disabled");
		} else {
			model.addObject("referSource", "2");
		}

		return model;
	}

	/**
	 * 注册页面wap第二个
	 *
	 * @return
	 */
	@RequestMapping(value = "/landing/mRegisterA", method = RequestMethod.GET)
	public ModelAndView register_AByMobileLanding(HttpServletRequest req,
			HttpServletResponse resp) {
		String shortUrl = (String) getHttpSession(req).getAttribute(INVITECODE);
		Member member = null;
		if (StringUtil.isNotBlank(shortUrl) && shortUrl.length() == 6) {
			member = memberService.getMemberByShortUrl(shortUrl);
		}
		ModelAndView model = new ModelAndView();
		model.setViewName("/landing/mRegister_A");
		if (member != null) {
			model.addObject("mobile", StringUtil.maskMobile(member.getMobile()));
			model.addObject("shortUrl", member.getShortUrl());
			model.addObject("referSource", "1");
			model.addObject("tureName",
					StringUtil.maskTrueName(member.getTrueName()));
			model.addObject("disabled", "disabled");
		} else {
			model.addObject("referSource", "2");
		}

		return model;
	}

	/**
	 * 注册页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "register", method = RequestMethod.GET)
	public ModelAndView register(HttpServletRequest req,
			HttpServletResponse resp) {
		String shortUrl = (String) getHttpSession(req).getAttribute(INVITECODE);
		if (ServletUtil.isMobile(req)) {
			// 跳转M站
			ModelAndView model = new ModelAndView();
			String api_url = PropertiesUtil.getMstationRootUrl();
			model.setViewName("redirect:" + api_url + "/mstation/register?"
					+ INVITECODE + "=" + shortUrl);
			return model;
		}
		Member member = null;
		if (StringUtil.isNotBlank(shortUrl) && shortUrl.length() == 6) {
			member = memberService.getMemberByShortUrl(shortUrl);
		}

		ModelAndView model = new ModelAndView();

		model.setViewName("member/login/register");
		if (member != null) {
			model.addObject("mobile", StringUtil.maskMobile(member.getMobile()));
			model.addObject("shortUrl", member.getShortUrl());
			model.addObject("referSource", "1");
			model.addObject("tureName",
					StringUtil.maskTrueName(member.getTrueName()));
			model.addObject("disabled", "disabled");
		} else {
			model.addObject("referSource", "2");
		}

		return model;
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
	@RequestMapping(value = "register/saveMember", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<Object> saveMember(@Valid MemberDto member,
			BindingResult result, HttpServletRequest req,
			HttpServletResponse resp) {
		String registerTraceSource = (String) getHttpSession(req).getAttribute(
				Constant.REGISTERTRACESOURCE);
		String registerTraceNo = (String) getHttpSession(req).getAttribute(
				Constant.REGISTERTRACENO);
		
		// 都玩ID
		String tid = member.getTid();
		if (StringUtil.isNotBlank(tid)) {
			member.setTid(tid);
			registerTraceNo = registerTraceNo + "_" + tid;
		}
		member.setRegisterTraceSource(registerTraceSource);
		member.setRegisterTraceNo(registerTraceNo);
		member.setIp(getIp(req));
		ResultDO<Object> resultObject = new ResultDO<Object>();
		boolean checkMobile = RegexUtils.checkMobile(member.getMobile() + "");
		
		//判断用户Ip限制
		SysDict sysDict=null;
		try {
			sysDict = sysDictService.findByGroupNameAndKey("register_ip_expire", "register_ip_expire");
			if(sysDict!=null && sysDict.getStatus()==1){
				boolean isExit = RedisMemberClient.isExitRegistedIp(member.getIp());
				if(isExit){
					//限制未失效
					resultObject.setResultCode(ResultCode.MEMBER_REGISTER_IP_RESTRICT_ERROR);
					return resultObject;
				}
			}
			
		} catch (ManagerException e) {
			logger.error("限制注册IP异常", e);
		}
		
		
	
		if (!checkMobile) {
			resultObject.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return resultObject;
		}
		validateResult(resultObject, result);
		if (!resultObject.isSuccess()) {
			return resultObject;
		}
		// 效验验证码
		if (validateMemberInfo(member, req, resultObject))
			return resultObject;
		// 设置微信ID
		setWeixinId(member, req);
		try {
			resultObject = loginService.register(member);
		} catch (Exception e) {
			logger.error("注册异常", e);
			resultObject.setSuccess(false);
			resultObject.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		if (resultObject.isError()) {
			return resultObject;
		}
		// 初始化其他信息
		if (StringUtil.isNotBlank(tid)) {
			member.setTid(tid);
		}
		// 判断登陆的是mobile还是pc端
		isjudePcOrMoblie(member, req);
		// 记录用户的userAgent
		setUserAgent(member, req);
		if (StringUtil.isNotBlank(member.getWeixinId())) {
			if (memberService.getWeiXinMemberToken(member.getWeixinId()) != null) {
				resultObject
						.setResultCode(ResultCode.WEIXIN_IS_UNBUNDLING_EROOR);
			}
		}
		loginService.initOtherMemberData(member);
		// 将新增用户的 资料，保存到session
		Member username = memberService.selectByMobile(member.getMobile());
		if (username == null) {
			logger.error("用户注册成功后查询会员数据为空 mobile={}", member.getMobile());
		}
		RedisMemberClient.setMemberInviteCode(username.getShortUrl(),
				username.getId());
		
		//存储用户注册用Ip到redis
		if(sysDict!=null && sysDict.getStatus()==1){
			RedisMemberClient.setUserRegisterIp(member.getIp(),Integer.valueOf(sysDict.getValue()));
		}
		
		
		
		
		addMemberToSession(req, username, member.getLoginSource(),
				member.getUserAgent());
		return resultObject;
	}

	/**
	 * 设置微信ID
	 * 
	 * @param member
	 * @param req
	 */
	private void setWeixinId(MemberDto member, HttpServletRequest req) {
		String openId = (String) getHttpSession(req).getAttribute(WEIXIN_ID);
		if (StringUtil.isNotBlank(openId)) {
			member.setWeixinId(openId);
			member.setLoginSource(4);
		}
	}

	/***
	 * 效验用户的 验证码
	 * 
	 * @param member
	 * @param req
	 * @param resultObject
	 * @return
	 */
	private boolean validateMemberInfo(MemberDto member,
			HttpServletRequest req, ResultDO<Object> resultObject) {
		String pngeCode = (String) getHttpSession(req).getAttribute(
				Constant.CAPTCHA_TOKEN);
		String smscode = (String) getHttpSession(req).getAttribute(
				Constant.SMS_CONTENT_CODE);
		String voicecode = (String) getHttpSession(req).getAttribute(
				Constant.VOICE_CONTENT_CODE);
		logger.info(member.getMobile()+"用户验证码信息: checkType="+member.getCheckType()+"; phonecode="+
				member.getPhonecode()+"; pngCode="+member.getPngCode());
		if(member.getCheckType()!=1 && member.getCheckType()!=2){
			resultObject.setResultCode(ResultCode.MEMBER_REGISTER_CHECK_TYPE_ERROR);
			return true;
		}
		if (member.getCheckType() == 1
				&& (!StringUtil.equals(smscode, member.getPhonecode()))) {
			resultObject.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
			return true;
		}
		if (member.getCheckType() == 2
				&& (!StringUtil.equals(voicecode, member.getPhonecode()))) {
			resultObject.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
			return true;
		}
		if (!StringUtil.equalsIgnoreCases(pngeCode, member.getPngCode(), true)) {
			resultObject.setResultCode(ResultCode.MEMBER_PNGCODE_ERROR);
			return true;
		}
		Long mobile = (Long) getHttpSession(req).getAttribute(REGISTER_MOBILE);
		if (member.getMobile().longValue() != mobile.longValue()) {
			resultObject
					.setResultCode(ResultCode.MEMBER_CHECK_MOBILE_CODE_ERROR);
			return true;
		}
		return false;
	}

	/**
	 * 判断用户是否移动端
	 * 
	 * @param member
	 * @param req
	 */
	private void isjudePcOrMoblie(MemberDto member, HttpServletRequest req) {
		boolean isMobile = ServletUtil.isMobile(req);
		if (isMobile) {
			member.setLoginSource(TypeEnum.MEMBER_LOGIN_SOURCE_MOBILE.getType());
		}
	}

	/**
	 * 设置浏览器代理信息
	 * 
	 * @param member
	 * @param req
	 */
	private void setUserAgent(MemberDto member, HttpServletRequest req) {
		// 添加用户代理信息
		String userAgent = req.getHeader("User-Agent");
		if (StringUtil.isNotBlank(userAgent) && userAgent.length() > 255) {
			userAgent = userAgent.substring(0, 255);// 如果用户代理数据长度超过255，则截取255字符保存
		}
		member.setUserAgent(userAgent);
	}

	/**
	 * 验证会员 短信验证码， 图形验证码，声音验证码
	 *
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "checkMember", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<Object> checkMember(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		ResultDO<Object> resultobject = new ResultDO<Object>();
		Member member = getMemberByType(req);
		if (member == null) {
			resultobject.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			resultobject.setSuccess(true);
		} else {
			if (member.getStatus() == 0) {
				resultobject.setResultCode(ResultCode.MEMBER_FOZONE_ERROR);
				return resultobject;
			}
			if (member.getStatus() == 1) {
				resultobject
						.setResultCode(ResultCode.MEMBER_NOT_ACTIVATION_ERROR);
				return resultobject;
			}
			resultobject.setResultCode(ResultCode.MEMBER_IS_EXIST);
			int getTrueName = ServletRequestUtils.getIntParameter(req,
					"getTrueName", 0);
			// 带 掩码的 真实姓名
			if (getTrueName == 1) {
				String[] result = {
						StringUtil.maskString(member.getUsername(),
								StringUtil.ASTERISK, 1, 1, 3),
						member.getShortUrl(), member.getStatus().toString() };
				resultobject.setResult(result);
			}
		}
		return resultobject;

	}

	/**
	 * @param req
	 * @return
	 * @throws ServletRequestBindingException
	 */
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
			String shortUrl = ServletRequestUtils.getStringParameter(req,
					"data");
			member = memberService.getMemberByShortUrl(shortUrl);
		}
		return member;
	}

	/**
	 * 手机发送验证码 或者语音短信
	 * 
	 * @param req(type:1-发送短信，2-发送语音/mobile:手机号/isCheckMobile:1-注册，2-忘记密码，3-修改手机号)
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 *             *
	 */
	@RequestMapping(value = "sendMobileCode", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<Integer> sendMobileCode(HttpServletRequest req, HttpServletResponse resp) {
		// 发送手机验证码 ，放进session里
		int type = ServletRequestUtils.getIntParameter(req, "type", 0);
		long mobile = ServletRequestUtils.getLongParameter(req, "mobile", 0);

		int isCheckMobile = ServletRequestUtils.getIntParameter(req, "isCheckMobile", 0);
		ResultDO<Integer> result = new ResultDO<Integer>();

		Member member = memberService.selectByMobile(mobile);
		// 注册页面 手机号码不存在
		if (isCheckMobile == 1 && member != null) {
			result.setResultCode(ResultCode.MEMBER_IS_EXIST);
			return result;
		}
		// 忘记密码 手机号码存在
		if (isCheckMobile == 2 && member == null) {
			result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return result;
		}
		// 修改手机号，发送新手机号验证码
		if (isCheckMobile == 3) {// 无操作
		}
		// 修改手机号，发送旧手机号验证码
		if (isCheckMobile == 4) {
			MemberSessionDto memberDto = getMember();
			if (memberDto != null) {
				mobile = memberDto.getMobile();
			}
		}
		
		// 获取手机号码有误
		if (!RegexUtils.checkMobile(mobile+"")) {
			result.setResultCode(ResultCode.MEMBER_MOBILE_ERROR);
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
		//随机验证码
		String smsContent = Identities.randomNumberLength(Constant.SEND_SMS_LENGTH);
		if (type == 1) {
			// 如果修改手机号，新手机号及验证码存入session
			if (isCheckMobile == 3) {
				session.setAttribute(Constant.SMS_CONTENT_CODE, mobile + Constants.ANGLE_BRACKETS + smsContent);
			} else {
				session.setAttribute(Constant.SMS_CONTENT_CODE, smsContent);
			}
			
			ResultDO<Integer> sendSMSDefault = new ResultDO<Integer>();
			logger.info(String.format("短信随机验证码,手机号： %d,验证码：%s", mobile, smsContent));
			if (PropertiesUtil.isDev()) {
				sendSMSDefault = sendSMSDefault.setResult(0);
			} else {
				sendSMSDefault = smsMobileSend.sendSMSVerificationCode(mobile,	smsContent);
			}
			// 发送成功 才记录次数
			if (sendSMSDefault.getResult() == 0) {
				remberSendSmsCount(mobile);
			}
		} else {
			// 如果修改手机号，新手机号及验证码存入session
			if (isCheckMobile == 3) {
				session.setAttribute(Constant.VOICE_CONTENT_CODE, mobile + Constants.ANGLE_BRACKETS + smsContent);
			} else {
				session.setAttribute(Constant.VOICE_CONTENT_CODE, smsContent);
			}
			
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
	 * 记录短信 次数
	 * 
	 * @param
	 * @return
	 */
	private Integer remberSendSmsCount(Long mobile) {

		RedisMemberClient.setMemberMobileSMSCount(mobile);
		// 语音次数
		Integer count = RedisMemberClient.getMemberMobileSMSCount(mobile);
		return count;
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
	 * 检查手机短信验证码是否正确
	 */
	@RequestMapping(value = "checkMobileCode", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<Object> checkMobileCode(HttpServletRequest request,
			HttpServletResponse response) {
		int type = ServletRequestUtils.getIntParameter(request, "type", 0);
		ResultDO<Object> result = new ResultDO<Object>();
		long mobile = ServletRequestUtils.getLongParameter(request, "mobile", 0);
		
		// 修改手机号验证旧手机验证码
		int checkType = ServletRequestUtils.getIntParameter(request, "checkType", 0);
		if (checkType == 1) {
			mobile = 0;
			MemberSessionDto memberDto = getMember();
			if (memberDto != null) {
				mobile = memberDto.getMobile();
			}
		}
		// 获取手机号码有误
		if (mobile == 0 || new Long(mobile).toString().length() != 11) {
			result.setResultCode(ResultCode.MEMBER_MOBILE_ERROR);
			return result;
		}
		
		boolean checkSmsCodeCounts = checkSmsCodeCounts(mobile);
		if (checkSmsCodeCounts) {
			result.setSuccess(false);
			result.setResultCode(ResultCode.ERROR);
			return result;
		}
		if (type == 1)
			result = checkSessionValue(request, Constant.SMS_CONTENT_CODE, mobile);
		if (type == 2)
			result = checkSessionValue(request, Constant.VOICE_CONTENT_CODE, mobile);
		return result;
	}

	/**
	 * 检查session里的值跟页面传过来的值是否匹配
	 * 
	 * @param request
	 * @param key
	 * @param mobile
	 * @return
	 */
	private ResultDO<Object> checkSessionValue(HttpServletRequest request, String key, long mobile) {
		ResultDO<Object> resultobject = new ResultDO<Object>();
		HttpSession session = getHttpSession(request);
		String token = (String) session.getAttribute(key);
		String parameter = ServletRequestUtils.getStringParameter(request,"code", null);
		if (StringUtil.equalsIgnoreCases(token, parameter, true)) {
			resultobject.setResultCode(ResultCode.SUCCESS);
			resultobject.setSuccess(true);
		} else {
			resultobject.setResultCode(ResultCode.ERROR);
		}
		Integer smscount = RedisMemberClient.getMemberMobileSMSCount(mobile);
		Integer voiceCount = RedisMemberClient
				.getMemberMobileVoiceCount(mobile);
		Integer counts = smscount + voiceCount;
		resultobject.setResult(counts);
		return resultobject;
	}

	private boolean checkSmsCodeCounts(long mobile) {
		boolean result = false;
		// 记录验证次数
		RedisMemberClient.setUserCheckCount(mobile);
		// 如果验证次数大于5次
		Integer userCheckCount = RedisMemberClient.getUserCheckCount(mobile);
		if (userCheckCount >= Constant.USER_MEMBER_COUNTS) {
			result = true;
		}
		return result;
	}

	/**
	 * 根据推荐码或者手机号码，获取会员信息
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 *             *
	 */
	@RequestMapping(value = "getMemberDtoByReqData", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<MemberDto> getMemberDtoByReqData(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		ResultDO<MemberDto> object = new ResultDO<MemberDto>();
		Member memberByType = getMemberByType(req);
		if (memberByType != null) {
			MemberDto dto = BeanCopyUtil.map(memberByType, MemberDto.class);
			object.setResult(dto);
			object.setSuccess(true);
		}
		return object;
	}

	/**
	 * 登陆页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "login")
	public String login(HttpServletRequest request, HttpServletResponse resp) {
		String referer = request.getHeader("Referer");
		/*if (StringUtil.isNotBlank(referer) && referer.contains("/activity/") && !referer.contains("/activity/decGift/")) {
			HttpSession session = request.getSession();
			// 把请求放入会话，登录后将用于跳转
			SavedRequest saved = new SavedRequest(request);
			session.setAttribute(Constant.SAVED_REQUEST_KEY, saved);
			session.setAttribute(Constant.SAVED_REQUEST_KEY_IS_IGNORE, false);
		}*/
		return "member/login/login";
	}

	/**
	 * 微信登陆页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "mLogin")
	public String loginByMobile(HttpServletRequest request,
			HttpServletResponse resp) {
		String openId = ServletRequestUtils.getStringParameter(request,
				"openId", null);
		if (StringUtil.isNotBlank(openId)) {// 检查微信号是否有绑定其它账号
			MemberToken token = memberService.getWeiXinMemberToken(openId);
			if (token != null) {
				request.setAttribute("openIdIsBind",
						ResultCode.WEIXIN_IS_UNBUNDLING_EROOR.getMsg());
			}
		}
		getWeixinIDAddSession(request);
		return "member/login/mLogin";
	}

	// 记录微信id 到session
	private void getWeixinIDAddSession(HttpServletRequest request) {
		String openId = ServletRequestUtils.getStringParameter(request,
				"openId", null);
		if (StringUtil.isNotBlank(openId)) {
			getHttpSession(request).setAttribute(WEIXIN_ID, openId);
		}
	}

	/**
	 * 登陆
	 * 
	 * @return
	 */
	@RequestMapping(value = "logined", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<Object> logined(
			@Valid @ModelAttribute("form") LoginDto form, BindingResult result,
			HttpServletRequest request, HttpServletResponse resp) {
		ResultDO<Object> resultRe = new ResultDO<Object>();
		validateResult(resultRe, result);
		if (!resultRe.isSuccess()) {
			return resultRe;
		}

		Integer count = loginCount(form.getUsername());
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtil.isBlank(ip)) {
			ip = request.getRemoteAddr();
		}
		form.setLoginIp(ip);
		// // 如果登陆次数大于 5次， 不允许登陆，或者，以后做锁定登陆用户名
		// if (count > 5) {
		// result.setResultCode(ResultCode.MEMBER_LOGIN_COUNT_ERROR);
		// return result;
		// }
		String pngeCode = (String) getHttpSession(request).getAttribute(
				Constant.CAPTCHA_TOKEN);
		// 验证图形验证码
		if (StringUtil.isNotBlank(pngeCode) && count > 3) {
			if (!StringUtil.equalsIgnoreCase(pngeCode, form.getPngCode())) {
				resultRe.setResultCode(ResultCode.MEMBER_PNGCODE_ERROR);
				resultRe.setResult(count);
				return resultRe;
			}
		}
		// 判断登陆的是mobile还是pc端
		boolean isMobile = ServletUtil.isMobile(request);
		if (isMobile) {
			form.setLoginSource(TypeEnum.MEMBER_LOGIN_SOURCE_MOBILE.getType());
		}
		String openId = (String) getHttpSession(request)
				.getAttribute(WEIXIN_ID);
		if (StringUtil.isNotBlank(openId)) {
			form.setWeixinId(openId);
			form.setLoginSource(TypeEnum.MEMBER_LOGIN_SOURCE_WEIXIN.getType());
		}
		// 添加用户代理信息
		String userAgent = request.getHeader("User-Agent");
		if (StringUtil.isNotBlank(userAgent) && userAgent.length() > 255) {
			userAgent = userAgent.substring(0, 255);// 如果用户代理数据长度超过255，则截取255字符保存
		}
		form.setUserAgent(userAgent);
		ResultDO<Member> resultDO = loginService.login(form);
		if (resultDO.isSuccess()) {
			// 登陆成功后， 将用户放进session中
			addMemberToSession(request, resultDO.getResult(),
					form.getLoginSource(), form.getUserAgent());
			// 登录成功后，清空短信发送次数
			RedisMemberClient.removeMemberMobileSMSCount(resultDO.getResult().getMobile());
			RedisMemberClient.removeMemberMobileVoiceCount(resultDO.getResult().getMobile());
			// 注册成功后，清空登陆次数
			RedisMemberClient.removeMemberLoginCount(form.getUsername());
			resultRe.setSuccess(true);
		} else {
			resultRe.setSuccess(false);
			resultRe.setResultCodeList(resultDO.getResultCodeList());
		}
		// 前台 根据登陆次数显示验证码
		if (resultRe.isSuccess()) {// 该判断可能不严谨
			String url = getTargetURL(request);
			resultRe.setResult(url);
		} else {
			resultRe.setResult(count);
		}
		return resultRe;
	}

	private Integer loginCount(String userName) {
		// 登陆次数 放进redis
		RedisMemberClient.setMemberLoginCount(userName);
		Integer count = RedisMemberClient.getMemberLoginCount(userName);
		return count;
	}

	private void addMemberToSession(HttpServletRequest request, Member mber,
			Integer loginSource, String userAgent) {
		MemberSessionDto dto = BeanCopyUtil.map(mber, MemberSessionDto.class);
		dto.setLoginSource(loginSource);
		dto.setUserAgent(userAgent);
		dto.setIsMemberInvested(transactionService.hasTransactionByMember(mber
				.getId()));
		request.getSession().setAttribute(Constant.CURRENT_USER, dto);
	}

	/**
	 * 生成验证码
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "validateCode")
	public void validateCode(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = getHttpSession(request);
		setResponseHeaders(response);
		String token;
		ServletOutputStream outputStream = null;
		try {
			outputStream = response.getOutputStream();
			token = ValidateCode.getChallangeAndWriteImage("png", outputStream);
			session.setAttribute(Constant.CAPTCHA_TOKEN, token);
			if (logger.isDebugEnabled()) {
				logger.debug(session.getId() + "-----------" + token);
			}
		} catch (Exception e) {
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 检查图形验证码是否正确
	 */
	@RequestMapping(value = "checkCode", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<Object> checkCode(HttpServletRequest request, HttpServletResponse response) {
		long mobile = ServletRequestUtils.getLongParameter(request, "mobile", 0);
		return checkSessionValue(request, Constant.CAPTCHA_TOKEN, mobile);
	}

	/**
	 * 忘记密码 1 打开页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "lostpwd")
	public String lostpwd() {
		return "member/login/lost-password";
	}

	/**
	 * 忘记密码 2提交form
	 * 
	 * @return
	 */
	@RequestMapping(value = "lostpwdsubmit", method = RequestMethod.POST)
	public ModelAndView lostpwd(MemberUpdatePasswordDto dto,
			HttpServletRequest req) {
		ModelAndView model = new ModelAndView();
		HttpSession session = getHttpSession(req);
		String pngeCode = (String) session.getAttribute(Constant.CAPTCHA_TOKEN);
		Long mobile = dto.getMobile();
		if (checkSmsCodeCounts(mobile)) {
			model.setViewName("redirect:/reminder");
			return model;
		}
		Long sessionMoblie = (Long)session.getAttribute(REGISTER_MOBILE);
		if (mobile == null ||sessionMoblie==null ||  mobile.compareTo(sessionMoblie) !=0 ){
			model.setViewName("redirect:/security/lostpwd");
			return model;
		}
		Member member = this.memberService.selectByMobile(mobile);
		boolean isSmsVoiceType = false;
		int type = dto.getCheckType();
		// 短信方式
		if (type == 1) {
			String smscontext = (String) session
					.getAttribute(Constant.SMS_CONTENT_CODE);
			isSmsVoiceType = StringUtil.equalsIgnoreCase(smscontext,
					dto.getSmsCode());
		} else if (type == 2) {
			String smscontext = (String) session
					.getAttribute(Constant.VOICE_CONTENT_CODE);
			isSmsVoiceType = StringUtil.equalsIgnoreCase(smscontext,
					dto.getSmsCode());
		}
		if (isSmsVoiceType
				&& StringUtil.equalsIgnoreCase(pngeCode, dto.getPngCode())
				&& member != null) {
			model.setViewName("member/login/update-password");
			dto.setId(member.getId());
			session.setAttribute(MEMBER_UPDATE_PASSWORD_ID, member.getId());
			model.addObject("dto", dto);
		} else {
			model.setViewName("redirect:/security/lostpwd");
		}
		return model;
	}

	/**
	 * 忘记密码 3 提交修改密码form
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "updatePassword", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<Object> updatePasswordBymobile(
			@Valid MemberUpdatePasswordDto dto, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		validateResult(resultDO, result);
		if (!resultDO.isSuccess()) {
			return resultDO;
		}
		String newPassword = dto.getPassword();
		Long mobile = dto.getMobile();
		Long id = (Long) request.getSession().getAttribute(
				MEMBER_UPDATE_PASSWORD_ID);
		HttpSession session = getHttpSession(request);
		Long sessionMoblie = (Long)session.getAttribute(REGISTER_MOBILE);
		if (mobile == null ||sessionMoblie==null ||  mobile.compareTo(sessionMoblie) !=0 ){
			resultDO = new ResultDO<Object>();
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return resultDO;
		}
		if (mobile == null || mobile == 0) {
			resultDO = new ResultDO<Object>();
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return resultDO;
		}

		// 验证两次密码是否一样
		if (!StringUtil.equals(dto.getPassword(), dto.getRepassword(), true)) {
			resultDO = new ResultDO<Object>();
			resultDO.setResultCode(ResultCode.MEMBER_RE_PASSWORD_ERROR);
			return resultDO;
		}
		resultDO = memberService
				.updatePasswordByMobile(id, mobile, newPassword);
		return resultDO;
	}

	/**
	 * 登录返回目标地址
	 * 
	 * @param request
	 * @return
	 */
	private String getTargetURL(HttpServletRequest request) {
		String targetUrl = "/member/home";
		HttpSession session = request.getSession();
		SavedRequest savedRequest = (SavedRequest) session
				.getAttribute(Constant.SAVED_REQUEST_KEY);
		if (savedRequest != null) {
			boolean isIgnore = (boolean) session.getAttribute(Constant.SAVED_REQUEST_KEY_IS_IGNORE);
			targetUrl = savedRequest.getWebFrontRefererURL(isIgnore);
			session.removeAttribute(Constant.SAVED_REQUEST_KEY);
		}
		return targetUrl;
	}
	
	/**
	 * 验证码方式修改手机号重置密码
	 * 
	 * @param dto
	 * @param req
	 * @param resp
	 * @return
	 */
    @RequestMapping(value = "resetPasswordByCaptcha",method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Object> resetPasswordByCaptcha(@Valid MemberUpdatePasswordDto dto, HttpServletRequest req, HttpServletResponse resp) {
    	ResultDO<Object> result = new ResultDO<Object>();
    	// 重复新密码的检验
    	if (!StringUtil.equals(dto.getPassword(), dto.getRepassword(), true)) {
    		result.setResultCode(ResultCode.MEMBER_RE_PASSWORD_ERROR);
    		logger.info(result.getResultCode().getMsg());
    		return result;
    	}
    	
    	// 获取session中加密的手机号和验证码
    	HttpSession httpSession = getHttpSession(req);
    	Object newMobileCaptcharAes = httpSession.getAttribute(Constant.MEMBER_MODIFY_MOBILE_CAPTCHA_AES);
    	if (newMobileCaptcharAes == null) {
    		result.setResultCode(ResultCode.ERROR_SYSTEM);
    		logger.info("未获取到加密的新手机号或验证码！");
    		return result;
    	}
    	// 解密
    	String sessionCaptcha = null;
    	Long newMobile = null;
    	String newMobileInfo = CryptHelper.decryptByase(newMobileCaptcharAes.toString());
    	List<String> infos = Splitter.on(Constants.ANGLE_BRACKETS).splitToList(newMobileInfo);
    	if (!infos.isEmpty()) {
    		newMobile = Long.valueOf(infos.get(0));
    		sessionCaptcha = infos.get(1);
    	}
    	if (newMobile==null || StringUtil.isBlank(sessionCaptcha)) {
    		result.setResultCode(ResultCode.ERROR_SYSTEM);
    		logger.info("未获取到加密的新手机号或验证码！");
    		return result;
    	}
    	// 获取redis中的验证码
    	if(!RedisManager.isExit(RedisConstant.REDIS_KEY_MODIFY_MOBILE + newMobile)) {
//    		result.setResultCode(ResultCode.MOBILE_SYSTEM_VOICE_VALIDATOR_ERROR);
    		result.setResultCode(ResultCode.ERROR_SYSTEM);
    		logger.info("验证码错误或已过期！");
    		return result;
    	}
    	String redisCaptcha = RedisManager.get(RedisConstant.REDIS_KEY_MODIFY_MOBILE + newMobile).toString();
    	if (StringUtil.isBlank(redisCaptcha)) {
//    		result.setResultCode(ResultCode.MOBILE_SYSTEM_VOICE_VALIDATOR_ERROR);
    		result.setResultCode(ResultCode.ERROR_SYSTEM);
    		logger.info("未获取到redis中的验证码！");
    		return result;
    	}
    	// 验证码是否正确
    	if(!StringUtil.equals(sessionCaptcha, redisCaptcha)) {
    		result.setResultCode(ResultCode.MEMBER_CHECK_MOBILE_CODE_ERROR);
    		logger.info(result.getResultCode().getMsg());
    		return result;
    	}
    	String newPassword = dto.getPassword();
    	result = memberService.modifyPasswordByNewMobile(newMobile, newPassword);
    	// 修改密码成功， 清空session
    	if (result.isSuccess()) {
    		if (httpSession.getAttribute(Constant.MEMBER_MODIFY_MOBILE_CAPTCHA_AES) != null) {
    			httpSession.removeAttribute(Constant.MEMBER_MODIFY_MOBILE_CAPTCHA_AES);
    		}
    		// 清除redis中的修改手机号的验证码
    		if(RedisManager.isExit(RedisConstant.REDIS_KEY_MODIFY_MOBILE + newMobile)) {
    			RedisManager.removeString(RedisConstant.REDIS_KEY_MODIFY_MOBILE + newMobile);
    		}
    	}
    	return result;
    }
    
	/**
	 * 身份信息修改手机号重置密码
	 * 
	 * @param dto
	 * @param req
	 * @param resp
	 * @return
	 */
    @RequestMapping(value = "resetPasswordByIdentity",method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Object> resetPasswordByIdentity(@Valid MemberUpdatePasswordDto dto, HttpServletRequest req, HttpServletResponse resp) {
    	ResultDO<Object> result = new ResultDO<Object>();
    	if (!StringUtil.equals(dto.getPassword(), dto.getRepassword(), true)) {
    		result.setResultCode(ResultCode.MEMBER_RE_PASSWORD_ERROR);
    		return result;
    	}
    	// 获取session中身份信息
    	Long newMobile = null;
    	// 姓名
    	String trueName = null;
    	// 身份证号码
    	String identityNumber = null;
    	HttpSession httpSession = getHttpSession(req);
    	Object memberInfos = httpSession.getAttribute(Constant.MEMBER_MODIFY_MOBILE_IDENTITY_NAME);
    	if (memberInfos == null) {
    		result.setResultCode(ResultCode.ERROR_SYSTEM);
    		logger.info("用户的身份信息未获取到！");
    		return result;
    	}
    	String newMemberMobileInfo = CryptHelper.decryptByase(memberInfos.toString());
    	List<String> newMemberMobileInfos = Splitter.on(Constants.ANGLE_BRACKETS).splitToList(newMemberMobileInfo);
    	if (!newMemberMobileInfos.isEmpty()) {
    		newMobile = Long.valueOf(newMemberMobileInfos.get(0));
    		identityNumber = newMemberMobileInfos.get(1);
    		trueName = newMemberMobileInfos.get(2);
    	}
    	String newPassword = dto.getPassword();
    	result = memberService.modifyPasswordByIdentity(newMobile, trueName, identityNumber, newPassword);
    	// 修改密码成功， 清空session
    	if (result.isSuccess()) {
            // 清除更改手机的session
    		if (httpSession.getAttribute(Constant.MEMBER_MODIFY_MOBILE_IDENTITY_NAME) != null) {
    			httpSession.removeAttribute(Constant.MEMBER_MODIFY_MOBILE_IDENTITY_NAME);
    			httpSession.invalidate();
    		}
    	}
    	return result;
    }

    /**
     *  修改手机号-重置密码页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "resetPassword")
    public ModelAndView resetPassword(HttpServletRequest req,
                                   HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/login/resetPassword");
        return model;
    }
}
