package com.yourong.api.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import rop.thirdparty.com.alibaba.fastjson.JSON;

import com.google.common.collect.Maps;
import com.yourong.api.dto.LoginDto;
import com.yourong.api.dto.MemberDto;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.MstationLoginDto;
import com.yourong.api.dto.MstationMemberDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.LoginService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.TransactionService;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.CryptCode;
import com.yourong.common.util.GenerateQuestionMd5;
import com.yourong.common.util.Identities;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.common.util.ValidateCode;
import com.yourong.common.web.SavedRequest;
import com.yourong.core.uc.model.Member;

/**
 * m站     登陆，注册，验证用户名
 *
 * @author pengyong
 */
@Controller
@RequestMapping("mstation")
public class MstationLoginController extends BaseController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private SmsMobileSend smsMobileSend;

    private static final String REGISTER_MOBILE = "Register_mobile";

    @Autowired
    private TransactionService transactionService;


    private static final String  INVITECODE = "inviteCode_shortURL";

    /**
     * 注册页面
     *
     * @return
     */
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public ModelAndView register(HttpServletRequest req,
                                 HttpServletResponse resp) {
        String shortUrl = (String) getHttpSession(req).getAttribute(INVITECODE);
        if(StringUtil.isBlank(shortUrl)){
        	shortUrl = (String) req.getParameter(INVITECODE);
        }
        Member member = null;
        if (StringUtil.isNotBlank(shortUrl) && shortUrl.length() == 6) {
            member = memberService.getMemberByShortUrl(shortUrl);
        }
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/login/register");
        if (member != null) {
            model.addObject("mobile", StringUtil.maskMobile(member.getMobile()));
            model.addObject("shortUrl", member.getShortUrl());
            model.addObject("referSource", "1");
            model.addObject("tureName", StringUtil.maskTrueName(member.getTrueName()));
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
    public ResultDTO<Object> saveMember(
            @Valid MstationMemberDto member,
            BindingResult result, HttpServletRequest req,
            HttpServletResponse resp) {

        String registerTraceSource = (String) getHttpSession(req).getAttribute(Constant.REGISTERTRACESOURCE);
        String registerTraceNo = (String) getHttpSession(req).getAttribute(Constant.REGISTERTRACENO);
        //都玩ID
        String tid = member.getTid();
        if (StringUtil.isNotBlank(tid)) {
            member.setTid(tid);
            registerTraceNo = registerTraceNo + "_" + tid;
        }
        member.setRegisterTraceSource(registerTraceSource);
        member.setRegisterTraceNo(registerTraceNo);
        member.setIp(req.getRemoteAddr());
        ResultDTO<Object> resultObject = new ResultDTO<Object>();
        validateResult(resultObject, result);
        if (!resultObject.isSuccess()) {
            return resultObject;
        }
        String pngeCode = (String) getHttpSession(req).getAttribute(Constant.CAPTCHA_TOKEN);
        String smscode = (String) getHttpSession(req).getAttribute(Constant.SMS_CONTENT_CODE);
        String voicecode = (String) getHttpSession(req).getAttribute(Constant.VOICE_CONTENT_CODE);
        if (member.getCheckType() == 1 && (!StringUtil.equals(smscode, member.getPhonecode()))) {
            resultObject.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
            return resultObject;
        }

        if (member.getCheckType() == 2 && (!StringUtil.equals(voicecode, member.getPhonecode()))) {
            resultObject.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
            return resultObject;
        }
        if (!StringUtil.equalsIgnoreCases(pngeCode, member.getPngCode(), true)) {
            resultObject.setResultCode(ResultCode.MEMBER_PNGCODE_ERROR);
            return resultObject;
        }
        Long mobile = (Long) getHttpSession(req).getAttribute(REGISTER_MOBILE);
        if (member.getMobile().longValue() != mobile.longValue()) {
            resultObject.setResultCode(ResultCode.MEMBER_CHECK_MOBILE_CODE_ERROR);
            return resultObject;
        }
//        String openId = (String) getHttpSession(req).getAttribute(WEIXIN_ID);
//        if (StringUtil.isNotBlank(openId)) {
//            //member.setWeixinId(openId);
//            member.setLoginSource(4);
//        }
        if (resultObject.isSuccess()) {
            ResultDO<Object> resultDO =new ResultDO<>();
            MemberDto memberDto = BeanCopyUtil.map(member,MemberDto.class);
            try {
                resultDO = loginService.register(memberDto);
            } catch (Exception e) {
                logger.error("注册异常", e);
                resultObject.setIsError();
                resultObject.setResultCode(ResultCode.ERROR_SYSTEM);
            }
            if (resultObject.isSuccess()) {
                //初始化其他信息
                if (StringUtil.isNotBlank(tid)) {
                    member.setTid(tid);
                }
                //判断登陆的是mobile还是pc端
//		        boolean isMobile = ServletUtil.isMobile(req);
//		        if(isMobile){
                memberDto.setLoginSource(TypeEnum.MEMBER_LOGIN_SOURCE_MOBILE.getType());
                //}
                //添加用户代理信息
                String userAgent = req.getHeader("User-Agent");
                if (StringUtil.isNotBlank(userAgent) && userAgent.length() > 255) {
                    userAgent = userAgent.substring(0, 255);//如果用户代理数据长度超过255，则截取255字符保存
                }
                memberDto.setUserAgent(userAgent);
                loginService.initOtherMemberData(memberDto);
                // 将新增用户的 资料，保存到session
                Member username = memberService.selectByMobile(member.getMobile());
                RedisMemberClient.setMemberInviteCode(username.getShortUrl(), username.getId());
                addMemberToSession(req, username, member.getLoginSource(), userAgent);
            }
        }
        return resultObject;
    }

    /**
     * 验证会员
     *
     * @param req
     * @param resp
     * @return
     * @throws org.springframework.web.bind.ServletRequestBindingException
     */
    @RequestMapping(value = "checkMember", method = RequestMethod.POST)
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

    /**
     * @param req
     * @return
     * @throws org.springframework.web.bind.ServletRequestBindingException
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
            String shortUrl = ServletRequestUtils.getStringParameter(req, "data");
            member = memberService.getMemberByShortUrl(shortUrl);
        }
        return member;
    }

    /**
     * 手机发送验证码 或者语音短信
     *
     * @param req
     * @param resp
     * @return
     * @throws org.springframework.web.bind.ServletRequestBindingException *
     */
    @RequestMapping(value = "sendMobileCode", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO<Integer> sendMobileCode(HttpServletRequest req,
                                            HttpServletResponse resp) {
    	String token = req.getParameter("token");
        // 发送手机验证码 ，放进session里
        int type = ServletRequestUtils.getIntParameter(req, "type", 0);
        long mobile = ServletRequestUtils.getLongParameter(req, "mobile", 0);

        int isCheckMobile = ServletRequestUtils.getIntParameter(req, "isCheckMobile", 0);
        ResultDTO<Integer> result = new ResultDTO<Integer>();

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
     			mobile = 0;
     			if (memberDto != null) {
     				mobile = memberDto.getMobile();
     			}
     		}
     	// 获取手机号码有误
    	if (mobile == 0) {
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
        long mobile = ServletRequestUtils	.getLongParameter(request, "mobile", 0);
        
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
            result = checkSessionValue(request, Constant.SMS_CONTENT_CODE);
        if (type == 2)
            result = checkSessionValue(request, Constant.VOICE_CONTENT_CODE);
        return result;
    }

    /**
     * 检查session里的值跟页面传过来的值是否匹配
     *
     * @param request
     * @param key
     * @return
     */
    private ResultDO<Object> checkSessionValue(HttpServletRequest request, String key) {
    	ResultDO<Object> resultobject = new ResultDO<Object>();
        HttpSession session = getHttpSession(request);
        String token = (String) session.getAttribute(key);
        String parameter = ServletRequestUtils.getStringParameter(request, "code", null);
        long mobile = ServletRequestUtils
                .getLongParameter(request, "mobile", 0);
        if (StringUtil.equalsIgnoreCases(token, parameter, true)) {
            resultobject.setResultCode(ResultCode.SUCCESS);
            resultobject.setSuccess(true);
        } else {
            resultobject.setResultCode(ResultCode.ERROR);
        }
        Integer smscount = RedisMemberClient.getMemberMobileSMSCount(mobile);
        Integer voiceCount = RedisMemberClient.getMemberMobileVoiceCount(mobile);
        Integer counts = smscount + voiceCount;
        resultobject.setResult(counts);
        return resultobject;
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
     * 根据推荐码或者手机号码，获取会员信息
     *
     * @param req
     * @param resp
     * @return
     * @throws org.springframework.web.bind.ServletRequestBindingException *
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
     * 登陆
     *
     * @return
     */
    @RequestMapping(value = "logined", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO<Object> logined(@Valid @ModelAttribute("form") MstationLoginDto form, BindingResult result,
                                    HttpServletRequest request, HttpServletResponse resp) {
        ResultDTO<Object> resultRe = new ResultDTO<Object>();
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
//		// 如果登陆次数大于 5次， 不允许登陆，或者，以后做锁定登陆用户名
//		if (count > 5) {
//			result.setResultCode(ResultCode.MEMBER_LOGIN_COUNT_ERROR);
//			return result;
//		}
        String pngeCode = (String) getHttpSession(request).getAttribute(Constant.CAPTCHA_TOKEN);
        //验证图形验证码
        if (StringUtil.isNotBlank(pngeCode) && count > 3) {
            if (!StringUtil.equalsIgnoreCase(pngeCode, form.getPngCode())) {
            	resultRe.setResult(count);
                resultRe.setResultCode(ResultCode.MEMBER_PNGCODE_ERROR);
                return resultRe;
            }
        }
//        //判断登陆的是mobile还是pc端
//        boolean isMobile = ServletUtil.isMobile(request);
//        if(isMobile){
        form.setLoginSource(TypeEnum.MEMBER_LOGIN_SOURCE_MOBILE.getType());
        //  }
//		String  openId = (String)getHttpSession(request).getAttribute(WEIXIN_ID);
//		if(StringUtil.isNotBlank(openId)){
//			form.setWeixinId(openId);
//			form.setLoginSource(TypeEnum.MEMBER_LOGIN_SOURCE_WEIXIN.getType());
//		}
        //添加用户代理信息
        String userAgent = request.getHeader("User-Agent");
        if (StringUtil.isNotBlank(userAgent) && userAgent.length() > 255) {
            userAgent = userAgent.substring(0, 255);//如果用户代理数据长度超过255，则截取255字符保存
        }
        form.setUserAgent(userAgent);
        LoginDto dto = BeanCopyUtil.map(form, LoginDto.class);
        ResultDO<Member> resultDO = loginService.login(dto);
        if (resultDO.isSuccess()) {
            // 登陆成功后， 将用户放进session中
            addMemberToSession(request, resultDO.getResult(), form.getLoginSource(), form.getUserAgent());
            // 登录成功后，清空短信发送次数
            RedisMemberClient.removeMemberMobileSMSCount(resultDO.getResult().getMobile());
            RedisMemberClient.removeMemberMobileVoiceCount(resultDO.getResult().getMobile());
            //注册成功后，清空登陆次数
            RedisMemberClient.removeMemberLoginCount(form.getUsername());


            String url = getTargetURL(request);
            resultRe.setResult(url);
            resultRe.setIsSuccess();
        } else {
        	// 前台 根据登陆次数显示验证码
        	resultRe.setResult(count);
            resultRe.setIsError();
            resultRe.setResultCodeList(resultDO.getResultCodeList());
        }
        return resultRe;
    }
    private Integer loginCount(String userName) {
        // 登陆次数 放进redis
        RedisMemberClient.setMemberLoginCount(userName);
        Integer count = RedisMemberClient.getMemberLoginCount(userName);
        return count;
    }

    private void addMemberToSession(HttpServletRequest request, Member mber, Integer loginSource, String userAgent) {
        MemberSessionDto dto = BeanCopyUtil.map(mber, MemberSessionDto.class);
        dto.setLoginSource(loginSource);
        dto.setUserAgent(userAgent);
        dto.setIsMemberInvested(transactionService.hasTransactionByMember(mber.getId()));
        request.getSession().setAttribute(Constant.CURRENT_USER, dto);
    }

    /**
     * 生成验证码
     *
     * @param request
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "validateCode")
    public void validateCode(HttpServletRequest request, HttpServletResponse response) {
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
    public ResultDO<Object> checkCode(HttpServletRequest request,
                                      HttpServletResponse response) {
        return checkSessionValue(request, Constant.CAPTCHA_TOKEN);
    }



    /**
     * 登陆页面
     *
     * @return
     */
    @RequestMapping(value = "login")
    public String loginMobile() {
        return "/mobile/login/login";
    }

    /**
     * 着陆页
     *
     * @return
     */
    @RequestMapping(value = "/landing/registerA")
    public String landingRegisterA() {
        return "/mobile/landing/registerA";
    }
    /**
     * 着陆页 guide
     *
     * @return
     */
    @RequestMapping(value = "/landing/registerA-guide")
    public String landingRegisterAGuide() {
        return "/mobile/landing/registerA-guide";
    }
    
    /**
     * 周年庆
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("memberReport")
    public ModelAndView memberReport(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        //model.setViewName("/mobile/member/reportIndex");
        model.setViewName("/mobile/activity/memberReport");
        return model;
    }
    
    /**
     * 着陆页
     *
     * @return
     */
    @RequestMapping(value = "/landing/{landingName:^[0-9a-zA-Z_]+$}")
    public ModelAndView landing(@PathVariable("landingName")String landingName,HttpServletRequest req, HttpServletResponse resp) {

        String shortUrl = (String) getHttpSession(req).getAttribute(INVITECODE);
        if(StringUtil.isBlank(shortUrl)){
            shortUrl = (String) req.getParameter(INVITECODE);
        }
        Member member = null;
        if (StringUtil.isNotBlank(shortUrl) && shortUrl.length() == 6) {
            member = memberService.getMemberByShortUrl(shortUrl);
        }
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/"+landingName);
        if (member != null) {
            model.addObject("mobile", StringUtil.maskMobile(member.getMobile()));
            model.addObject("shortUrl", member.getShortUrl());
            model.addObject("referSource", "1");
            model.addObject("tureName", StringUtil.maskTrueName(member.getTrueName()));
        }
        return model;
    }

    /**
     * 登录返回目标地址
     *
     * @param request
     * @return
     */
    private String getTargetURL(HttpServletRequest request) {
        String targetUrl = "/mCenter/home";
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
     * 专题页
     *
     * @return
     */
    @RequestMapping(value = "/post/{landingName:^[0-9a-zA-Z_]+$}")
    public ModelAndView posting(@PathVariable("landingName")String landingName,HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		isExistsVM(request, "mobile/post", landingName);
        String loginSource = request.getHeader("loginSource");
        modelAndView.setViewName("/mobile/post/"+landingName);
        modelAndView.addObject("loginSource", loginSource);
        return modelAndView;
    }

    /**
     * 
     * @Description:问卷跳转
     * @param req
     * @param resp
     * @return
     * @author: zhanghao
     * @time:2016年11月16日 上午11:37:13
     */

	@RequestMapping("answer")
	public String showProject(RedirectAttributes attr) {
		// /s/proj_id/?site=***&user=***&callback=***&md5=***&proj_id=***
		String url = Config.getQuestionAddr() + "/s/"+Config.getQuestionProjId()+"/";
		Map<String, String> params = Maps.newHashMap();
		params.put("site", Config.getQuestionSite());
		MemberSessionDto memberDto = getMember();
		if (memberDto != null) {
			params.put("user", String.valueOf(memberDto.getId()));
			params.put("callback", Config.getQuestionNotify());
			params.put("proj_id", Config.getQuestionProjId());
			List<String> keyList = new ArrayList<String>();
			keyList.add("site");
			keyList.add("user");
			keyList.add("callback");
			keyList.add("proj_id");
			String md5Param = GenerateQuestionMd5.sortParam(keyList, params);
			String md5 = CryptCode.encryptToMD5(md5Param + Config.getQuestionSecretKey());
			params.put("md5", md5);
			attr.addAllAttributes(params);
			return "redirect:" + url;
		} else{
			return "member/login/login";
		}
	}


}
