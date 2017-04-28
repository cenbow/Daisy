package com.yourong.api.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.yourong.api.cache.RedisAPIMemberClient;
import com.yourong.api.dto.AutoInvestSetDto;
import com.yourong.api.dto.MemberAuthDto;
import com.yourong.api.dto.MemberCheckDto;
import com.yourong.api.dto.MemberCheckInfoDto;
import com.yourong.api.dto.MemberInfoDto;
import com.yourong.api.dto.MemberNotifySettingsDto;
import com.yourong.api.dto.MemberSecurityDto;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.MstationMemberAuthDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.UpdatePasswordDto;
import com.yourong.api.service.CouponService;
import com.yourong.api.service.FeedbackService;
import com.yourong.api.service.MemberInfoService;
import com.yourong.api.service.MemberNotifySettingsService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.SysDictService;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.member.common.Constants;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.ConfigUtil;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Encodes;
import com.yourong.common.util.FileInfoUtil;
import com.yourong.common.util.RegexUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.msg.model.Feedback;
import com.yourong.core.msg.model.query.FeedBackQuery;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.model.AutoInvestSet;
import com.yourong.core.uc.model.Member;
import com.yourong.core.upload.model.ImageConfig;
import com.yourong.core.upload.util.UploadUtil;

/**
 * Created by py on 2015/3/22.
 */
@Controller
@RequestMapping("security/member")
public class MemberController extends  BaseController {
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private MemberInfoService memberInfoService;
    
    @Autowired
    private MemberNotifySettingsService memberNotifySettingsService;
    
    @Autowired
	private SysDictService sysDictService;
    
    @Autowired
    private Map<String, List<ImageConfig>> imagesConfig;
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
	private CouponService couponService;
    
    /**
     * 实名认证接口
     * @param memberDto
     * @param result
     * @param req
     * @param resp
     * @ret
     */
    @RequestMapping(value = "authIdentity",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<Object> authIdentity( @Valid @ModelAttribute MemberAuthDto memberDto, BindingResult result, HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object> resultObject = new ResultDTO<Object>();
        if (sinaPayAuth(memberDto, result, req, resultObject)) return resultObject;
        return resultObject;
    }

    /**
     * m 站实名认证接口
     * @param
     * @param result
     * @param req
     * @param resp
     * @ret
     */
    @RequestMapping(value = "mstation/authIdentity",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<Object> mastionAuthIdentity( @Valid @ModelAttribute MstationMemberAuthDto mstationMemberAuthDto, BindingResult result, HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object> resultObject = new ResultDTO<Object>();
        MemberAuthDto memberDto = BeanCopyUtil.map(mstationMemberAuthDto,MemberAuthDto.class);
        if (sinaPayAuth(memberDto, result, req, resultObject)){
        	MemberSessionDto memberSessionDto = getMember();
            memberSessionDto.setIdentityNumber(memberDto.getIdentityNumber());
            memberSessionDto.setTrueName(memberDto.getTrueName());
            updateMemberSessionDto(req, memberSessionDto);
        	return resultObject;
        }
        return resultObject;
    }

    private boolean sinaPayAuth(MemberAuthDto memberDto, BindingResult result, HttpServletRequest req, ResultDTO<Object> resultObject) {
    	
        validateResult(resultObject, result);
        if (resultObject.proessError()) {
            return true;
        }
        if (!(RegexUtils.checkChinese(memberDto.getTrueName()) && RegexUtils.checkIdCard(memberDto.getIdentityNumber()))) {
            resultObject.setResultCode(ResultCode.ERROR_SYSTEM_PARAM);
            return true;
        }
        memberDto.setMemberID(getMemberID(req));
        memberDto.setClientIp(getIp(req));
        ResultDO<Object> resultDO = new ResultDO<Object>();
        try {
              resultDO = memberService.authIdentity(memberDto);
        } catch (Exception e) {
            logger.error("第三方支付开通异常", e);
            resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
        }
        if (resultDO.isSuccess()) {
        	SPParameter  parameter =new SPParameter();
			parameter.setMemberId(memberDto.getMemberID());
			SPEngine.getSPEngineInstance().run(parameter);
			CouponTemplate couponTemplate =couponService.getkaiTongCunQianGuanCouponTemplate();
			Integer source = getRequestSource(req);
			if(source==2){//ios紧急修复显示问题
				Integer dayScope =couponTemplate.getDaysScope();
				BigDecimal amountScope = couponTemplate.getAmountScope();
				couponTemplate.setDaysScope(amountScope.intValue());
				couponTemplate.setAmountScope(new BigDecimal(dayScope));
			}
			resultObject.setResult(couponTemplate);
			resultObject.setIsSuccess();
            return true;
        }else {
           converterResultDTO(resultObject,resultDO);
        }
        return false;
    }

    /**
     * 保存用户昵称
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "saveUserName", method = RequestMethod.POST, headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO saveUserName(HttpServletRequest req, HttpServletResponse resp) {
    	ResultDTO result = new ResultDTO();
        String userName = ServletRequestUtils.getStringParameter(req, "userName", null);
        if (StringUtils.isNotEmpty(userName)) {
            Long memberId = getMemberID(req);
            result = memberService.saveUserName(memberId, userName);
        } else {
            result.setResultCode(ResultCode.ERROR);
        }
        return result;
    }
    
    
    /**
     * 保存用户详细信息
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "saveMemberInfo", method = RequestMethod.POST, headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO saveMemberInfo(@Valid @ModelAttribute("memberInfoDto") MemberInfoDto memberInfoDto, BindingResult bindingResult, HttpServletRequest req, HttpServletResponse resp){
    	Long memberId = getMemberID(req);
        memberInfoDto.setMemberId(memberId);
        memberInfoDto.setId(memberInfoDto.getInfoId());
        ResultDTO result = new ResultDTO();
        try {
        	logger.info("保存用户详细信息,memberId:" + memberInfoDto.getMemberId());
            result = memberInfoService.saveOrUpdateMemberInfoByMemberId(memberInfoDto);
        } catch (ManagerException e) {
            logger.error("保存用户详细信息失败,memberId:" + memberInfoDto.getMemberId());
            result.setResultCode(ResultCode.ERROR);
        }
        return result;
    }
    
    @RequestMapping(value = "queryMemberInfo", method = RequestMethod.POST, headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO queryMemberInfo(HttpServletRequest req, HttpServletResponse resp){
    	 Long memberId = getMemberID(req);
         MemberInfoDto infoDto = memberInfoService.getMemberInfoByMemberId(memberId);
         ResultDTO result = new ResultDTO();
         if(infoDto == null){
        	 infoDto = new MemberInfoDto();
         }
         result.setResult(infoDto);
         return result;
    }
    
    
    /**
     * 更新客户消息通知状态
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletRequestBindingException
     */
    @RequestMapping(value = "updateMemberNotifySettings", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO updateMemberNotifySettings(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	ResultDTO result = new ResultDTO();
        Long memberId = getMemberID(req);
        Map<Integer, Map<Integer, Integer>> map = getNotifySettings(req);
        try {
            result = memberNotifySettingsService.updateNotifySettingsStatus(map, memberId);
        } catch (ManagerException e) {
            result.setResultCode(ResultCode.MEMBER_UPDATE_NOTIFY_SETTINGS_ERROR);
        }
        return result;
    }
    
    /**
     * 获得配置的消息订阅项
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletRequestBindingException
     */
    @RequestMapping(value = "queryMemberNotifySettings", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO queryMemberNotifySettings(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	ResultDTO result = new ResultDTO();
        Long memberId = getMemberID(req);
        List<MemberNotifySettingsDto> memberNotifySettingsList = memberNotifySettingsService.getCheckedNotifySettings(memberId);
        result.setResult(memberNotifySettingsList);
        return result;
    }
    
    

    /**
     * 获得订阅配置项
     *
     * @param req
     * @return
     */
    private Map<Integer, Map<Integer, Integer>> getNotifySettings(HttpServletRequest req) {
        Map<Integer, Map<Integer, Integer>> map = Maps.newHashMap();
        // 通知类型 通知方式
        List<SysDict> notifyTypeSysDictList = sysDictService.findByGroupName("notify_type");
        List<SysDict> notifyWaySysDictList = sysDictService.findByGroupName("notify_way");
        for (SysDict notifyType : notifyTypeSysDictList) {
            Map<Integer, Integer> xmap = Maps.newHashMap();
            for (SysDict notifyWay : notifyWaySysDictList) {
                Integer id = ServletRequestUtils.getIntParameter(req, notifyType.getKey() + "_" + notifyWay.getKey(), Constant.DISABLE);
                xmap.put(Integer.parseInt(notifyWay.getValue()), id);
            }
            map.put(Integer.parseInt(notifyType.getValue()), xmap);
        }
        return map;
    }
    
    /**
     * 获得用户安全认证信息
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "queryMemberAuthorize", method = RequestMethod.POST, headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO<MemberSecurityDto> queryMemberAuthorize(HttpServletRequest req, HttpServletResponse resp){
    	return memberService.queryMemberAuthorize(getMemberID(req));
    }
    
    /**
     * 用户签到
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "goSignIn", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<MemberCheckDto> memberSignIn(HttpServletRequest req, HttpServletResponse resp){
    	ResultDTO result = new ResultDTO();
    	Long memberId = getMemberID(req);
    	int loginSource = getRequestSource(req);
    	try {
    		result = memberService.memberCheck(memberId, loginSource);
		} catch (DuplicateKeyException mysqlE) { //重复签到不打印日志
			logger.error("会员重复签到！memberId={}", memberId);
		} catch (Exception e) {
			logger.error("签到异常"+memberId);
			result.setResultCode(ResultCode.ERROR);
		}
    	return result;
    }
    
    /**
     * 获得用户签到信息
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "queryMemberSignInInfo", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<MemberCheckInfoDto> queryMemberSignInInfo(HttpServletRequest req, HttpServletResponse resp){
    	Long memberId = getMemberID(req);
    	return memberService.queryMemberSignInInfo(memberId);
    }
    
    /**
     * 用户反馈
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "feedback", method = RequestMethod.POST, headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO feedBack(HttpServletRequest req, HttpServletResponse resp){
    	ResultDTO result = new ResultDTO();
    	String content = ServletRequestUtils.getStringParameter(req, "content", null);
    	String type = ServletRequestUtils.getStringParameter(req, "type", "6");
    	
		Feedback feedback = new Feedback();
		feedback.setMemberId(getMemberID(req));
		feedback.setSource(getRequestSource(req));
		feedback.setType(Integer.valueOf(type));
		feedback.setContent(content);
		result  = feedbackService.addFeedback(feedback);
    	return result;
    }
    
    /**
     * 保存用户头像
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "uploadAvatar", method = RequestMethod.POST, headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO saveAvatar(HttpServletRequest req, HttpServletResponse resp) {
    	ResultDTO result = new ResultDTO();
    	String content = ServletRequestUtils.getStringParameter(req, "avatar", null);
    	try {
    		if(StringUtil.isNotBlank(content)){
	    		byte[] data = Encodes.decodeBase64(content);
				if(data.length <= 1024*1024*2){//2MB
					Long id = getMemberID(req);
		    		String appPath = req.getSession().getServletContext().getRealPath("/")+ConfigUtil.getInstance().getUploadDirectory()+File.separator;
					File file = FileInfoUtil.writeFile(new ByteArrayInputStream(data), "avatar_uploadAvata.jpg", appPath,"avatar");
					UploadUtil.doScaleAndAddWatemarkImage(file, "avatar", imagesConfig);
					String avatarUrl = memberService.saveAvatar(id, file.getPath().replace("\\", "/"));
					JSONObject json = new JSONObject();
					json.put("avatar", avatarUrl);
					result.setResult(json);
				}else{
					result.setResultCode(ResultCode.MEMBER_UPLOAD_AVATAR_FILE_SIZE_ERROR);
				}
    		}else{
    			result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
    		}
		} catch (Exception e) {
			result.setResultCode(ResultCode.ERROR);
		}
    	return result;
    }
    
    /**
     * 修改密码
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "updatePassword", method = RequestMethod.POST, headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO updatePassword(@Valid @ModelAttribute("form")UpdatePasswordDto form, BindingResult bingResult, HttpServletRequest req, HttpServletResponse resp){
    	ResultDTO<Object> result = new ResultDTO<Object>();
    	 Long id = getMemberID(req);
         validateResult(result, bingResult);
         if (!result.isSuccess()) {
             return result;
         }
         if (!StringUtil.equals(form.getPassword(), form.getCheckNewPassword(), true)) {
             result.setResultCode(ResultCode.MEMBER_RE_PASSWORD_ERROR);
             return result;
         }
         result = memberService.updatePassword(id, form.getOldPassword(), form.getPassword());
         return result;
    }
    

    /**
     * 新浪存钱罐查询验证
     * @param req
     * @param resp
     * @return
     */
	 @RequestMapping(value = "auditMemberInfos", method = RequestMethod.POST)
	 public ModelAndView auditSavingPot(HttpServletRequest req, HttpServletResponse resp){
		 Long memberId= getMemberID(req);
		 ModelAndView mv = new ModelAndView();
		 mv.setViewName("/member/authSavingPotPage");
		 try {
			String auditHtml = memberService.getAuditMemberInfosForm(memberId);
			mv.addObject("auditHtml", auditHtml);
		} catch (Exception e) {
			logger.error("新浪存钱罐查询验证生成签名异常",e);
			mv.setViewName("/error");  
		}
		 return mv;
	 }
	 
	 /**
	  * 绑定邮箱
	  * @param req
	  * @param resp
	  * @return
	  */
	@RequestMapping(value = "bindEmail", method = RequestMethod.POST, headers = {"Accept-Version=1.0.4"})
    @ResponseBody
    public ResultDTO toBindEmail(HttpServletRequest req, HttpServletResponse resp) {
        Long memberId = getMemberID(req);
        String email = req.getParameter("email");
        return memberService.toBindEmail(memberId, email);
    }
	
	/**
	 * 检查邮箱是否绑定成功
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "checkBindEmailStatus", method = RequestMethod.POST, headers = {"Accept-Version=1.0.4"})
    @ResponseBody
	public ResultDTO checkBindEmailStatus(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO result = new ResultDTO();
		Long memberId = getMemberID(req);
		Member member = memberService.selectByPrimaryKey(memberId);
		String email = req.getParameter("email");
		if(member != null && StringUtil.isNotBlank(member.getEmail()) && StringUtil.isNotBlank(email) && email.equals(member.getEmail())){
			result.setResult(true);
		}else{
			result.setResult(false);
		}
		return result;
	}
	
	/**
	 * @Description:领取生日优惠券（1:50现金券 2:1%收益券）
	 * @param type
	 * @param req
	 * @param resp
	 * @return
	 * @author: fuyili
	 * @time:2015年12月8日 上午11:01:37
	 */
	@RequestMapping(value = "/birthday/receiveBirthdayCoupon", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO receiveBirthday50Coupon(@RequestParam("type") int type, HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO result = new ResultDTO();
		Long memberID = getMemberID(req);
		Member member = memberService.selectByPrimaryKey(memberID);
		MemberSessionDto userName = new MemberSessionDto();
		BeanCopyUtil.copy(member, userName);
		result = preVerification(result, userName.getBirthday(), 2);
		if (result.isSuccess()) {
			if (type != TypeEnum.BIRTHDAY_COUPON_TYPE_50CASH.getType() && type != TypeEnum.BIRTHDAY_COUPON_TYPE_001INCOME.getType()) {
				result.setIsError();
				return result;
			}
			if (type == TypeEnum.BIRTHDAY_COUPON_TYPE_50CASH.getType()) {
				return couponService.receiveBirthday50Coupon(memberID, userName.getBirthday());
			}
			if (type == TypeEnum.BIRTHDAY_COUPON_TYPE_001INCOME.getType()) {
				return couponService.receiveBirthday001Coupon(memberID, userName.getBirthday());
			}
		}
		return result;
	}

	/**
	 * 前期验证
	 * 
	 * @param result
	 * @param birthday
	 * @return
	 */
	private ResultDTO preVerification(ResultDTO result,Date birthday, int source) {
		result.setIsSuccess();
		Activity activity = memberService.getBirthdayActivity();
		if (activity != null) {
			if (DateUtils.getCurrentDate().before(activity.getStartTime())) {
				result.setResultCode(ResultCode.ACTIVITY_BIRTHDAY_IS_NOT_START);
				return result;
			}
		}
		int status = memberService.getBirthdayStatus(birthday);
		if (status != 1) {
			if (source == 1) {
				result.setResultCode(ResultCode.MEMBER_BIRTHDAY_SIGN_TIPS_ERROR);
			} else if (source == 2) {
				result.setResultCode(ResultCode.MEMBER_BIRTHDAY_COUPON_TIPS_ERROR);
			} else {
				result.setResultCode(ResultCode.MEMBER_BIRTHDAY_INCOME_TIPS_ERROR);
			}
			return result;
		}
		return result;
	}
	/**
	 * 
	 * @Description:问卷测评
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年6月6日 上午11:46:50
	 *//*
	 @RequestMapping(value = "saveEvaluation", method = RequestMethod.POST)
	    @ResponseBody
	    public ResultDO<MemberInfo>saveEvaluationMemberInfo(HttpServletRequest req, HttpServletResponse resp) {
	        ResultDO<MemberInfo> result = new ResultDO<MemberInfo>();
	        MemberSessionDto member = getMember();
	        int evaluationScore=ServletRequestUtils.getIntParameter(req, "evaluationScore", 0);
			//登录认证
			if (member == null) {
				result.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
				return result;
			}
			if(evaluationScore==0){
				result.setResultCode(ResultCode.EVALUATION_SCORE_RESULT);
				return result;
			}
			Long memberId = getMember().getId();
	        try {
	            result = memberInfoService.UpdateMemberInfoByMemberId(memberId,evaluationScore);
	        } catch (ManagerException e) {
	            logger.error("测评失败,memberId:" + memberId);
	        }
	        return result;
	    }*/

	/* *
	  * 设置签署方式
	  * @param req
	  * @param resp
	  * @return
	  */
	@RequestMapping(value = "saveSignWay", method = RequestMethod.POST, headers = {"Accept-Version=1.6.0"})
	@ResponseBody
	public ResultDTO saveSignWay(HttpServletRequest req, HttpServletResponse resp) {
       
       ResultDTO<Member> result = new ResultDTO<Member>();
       int sighWay = ServletRequestUtils.getIntParameter(req, "signWay", 0);
       if(sighWay==1||sighWay==0){
    	   Long memberId = getMemberID(req);
           result = memberService.saveSignWay(memberId, sighWay);
           return result;
       }   
       result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
       return result;
	}
	
	
	/**
	 * 
	 * @Description:设置支付密码
	 * @param req
	 * @param resp
	 * @return
	 * @author: zhanghao
	 * @time:2016年8月5日 下午5:50:36
	 */
	@RequestMapping(value = "handlePayPassword", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	@ResponseBody
	public ResultDTO<String> handlePayPassword(HttpServletRequest req, HttpServletResponse resp) {
		int handleType = ServletRequestUtils.getIntParameter(req, "handleType", 0);
		Integer source = getRequestSource(req);
		ResultDTO<String> rDTO =  memberService.handlePayPassword(getMemberID(req), handleType,source);
		return rDTO;
	}
	  // 检查手机的验证次数
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
		 private String cleanSession(HttpServletRequest req, HttpServletResponse resp) {
		        HttpSession httpSession = getHttpSession(req);
		        // 清除session
		        Enumeration<String> em = httpSession.getAttributeNames();
		        while (em.hasMoreElements()) {
		            req.getSession().removeAttribute(em.nextElement().toString());
		        }
		        httpSession.removeAttribute(Constant.CURRENT_USER);
		        httpSession.invalidate();
		        String path = req.getContextPath();
		        setResponseHeaders(resp);
		        return path;
		    }
	/**
	 * 
	 * @Description:验证码方式修改手机号
	 * @param newMobile
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年8月8日 下午2:02:39
	 */
	@RequestMapping(value = "modifyMobileByCaptcha", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	@ResponseBody
	public Object modifyMobileByCaptcha(@ModelAttribute("nMobile") Long newMobile, HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO<Object> resultDO = new ResultDTO<>();
		 Long id = getMemberID(req);
		 Member biz = memberService.selectByPrimaryKey(id);
		 if(biz==null){
			 resultDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			 return resultDO;
		 }
		try {
			String newMobileCaptcha = ServletRequestUtils.getStringParameter(req, "captcha");
			String sessionMobileCaptcha = (String) req.getSession().getAttribute(Constant.SMS_CONTENT_CODE);
			
			// 检验手机验证码的验证次数
			boolean checkMobile = RegexUtils.checkMobile(newMobile.toString());
			if (!checkMobile){
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return  resultDO;
			}
			boolean checkSmsCodeCounts = checkSmsCodeCounts(newMobile);
			if (checkSmsCodeCounts) {
				resultDO.setResultCode(ResultCode.ERROR);
				return resultDO;
			}
			// 核对验证码
			 if(StringUtil.isNotBlank(newMobileCaptcha) && newMobile > 0){
						String code2 = RedisAPIMemberClient.getUserMobileSMScode(newMobile);
						if(StringUtil.isNotBlank(code2) && newMobileCaptcha.equals(code2)){
							String newMobileCaptcharAes = CryptHelper.encryptByase(newMobile + Constants.ANGLE_BRACKETS + newMobileCaptcha);
							resultDO = this.memberService.modifyMobileByOldMobile(biz.getId(), newMobile, biz.getMobile());
							if(resultDO.isSuccess()){
								RedisAPIMemberClient.removeUserMobileSMSCode(newMobile);
								RedisManager.putString(RedisConstant.REDIS_KEY_MODIFY_MOBILE+ newMobile, newMobileCaptcha);
								// 清空session
								cleanSession(req, resp);
								// 重新把加密后的手机号和验证码放如session中
								req.getSession().setAttribute(Constant.MEMBER_MODIFY_MOBILE_CAPTCHA_AES, newMobileCaptcharAes);
								// 新手机号验证码存入redis中
								RedisManager.putString(RedisConstant.REDIS_KEY_MODIFY_MOBILE+ newMobile, newMobileCaptcha);
//								RedisManager.expireObject(Constant.MEMBER_MODIFY_MOBILE_CAPTCHA, 1200);
							}
							
							// Integer smscount = RedisMemberClient.getMemberMobileSMSCount(newMobile);
						    // Integer voiceCount = RedisMemberClient.getMemberMobileVoiceCount(newMobile);
						     
							return resultDO;
						}else{
							resultDO.setIsError();
							resultDO.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
							return resultDO;
						}
				}else{
					resultDO.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
					return resultDO;
				}
			/*if (StringUtil.equals(newMobileCaptcha, sessionMobileCaptcha, true)) {
				// 手机号和验证码进行加密
				String newMobileCaptcharAes = CryptHelper.encryptByase(newMobile + Constants.ANGLE_BRACKETS + newMobileCaptcha);
				resultDO = this.memberService.modifyMobileByOldMobile(biz.getId(), newMobile, biz.getMobile());
				// 清空session
				if (resultDO.isSuccess()) {
					// 清空session
					cleanSession(req, resp);
					// 重新把加密后的手机号和验证码放如session中
					req.getSession().setAttribute(Constant.MEMBER_MODIFY_MOBILE_CAPTCHA_AES, newMobileCaptcharAes);
					// 新手机号验证码存入redis中
					RedisManager.putString(RedisConstant.REDIS_KEY_MODIFY_MOBILE+ newMobile, newMobileCaptcha);
//					RedisManager.expireObject(Constant.MEMBER_MODIFY_MOBILE_CAPTCHA, 1200);
				}
			} else {
				resultDO.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
			}*/
		} catch (ServletRequestBindingException ex) {
			logger.error("验证码方式修改手机号异常，mobile="+ biz.getMobile() +",newMobile=" + newMobile, ex);
		}
		return resultDO;
	}
	/**
	 * 
	 * @Description:验证码方式修改手机号重置密码
	 * @param dto
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年8月8日 下午2:27:37
	 */
	    @RequestMapping(value = "resetPasswordByCaptcha",method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	    @ResponseBody
	    public ResultDTO<Object> resetPasswordByCaptcha(@ModelAttribute("password") String passWord,@ModelAttribute("repassword") String rePassWord, HttpServletRequest req, HttpServletResponse resp) {
		 ResultDTO<Object> result = new ResultDTO<Object>();
	    	if (!StringUtil.equals(passWord, rePassWord, true)) {
			result.setResultCode(ResultCode.MEMBER_RE_PASSWORD_ERROR);
			return result;
		}
		
		// 获取session中加密的手机号和验证码
		HttpSession httpSession = getHttpSession(req);
		Object newMobileCaptcharAes = httpSession.getAttribute(Constant.MEMBER_MODIFY_MOBILE_CAPTCHA_AES);
		if (newMobileCaptcharAes == null) {
			result.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("新手机号或验证码未获取到！");
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
			logger.error("新手机号或验证码未获取到！");
			return result;
		}
		// 获取redis中的验证码
		if(!RedisManager.isExit(RedisConstant.REDIS_KEY_MODIFY_MOBILE + newMobile)) {
//	    		result.setResultCode(ResultCode.MOBILE_SYSTEM_VOICE_VALIDATOR_ERROR);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
			return result;
		}
		String redisCaptcha = RedisManager.get(RedisConstant.REDIS_KEY_MODIFY_MOBILE + newMobile).toString();
		if (StringUtil.isBlank(redisCaptcha)) {
//	    		result.setResultCode(ResultCode.MOBILE_SYSTEM_VOICE_VALIDATOR_ERROR);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
			return result;
		}
		// 验证码是否正确
		if(!StringUtil.equals(sessionCaptcha, redisCaptcha)) {
			result.setResultCode(ResultCode.MEMBER_CHECK_MOBILE_CODE_ERROR);
			return result;
		}
		String newPassword = passWord;
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
	     * 
	     * @Description:身份信息修改手机号，验证信息
	     * @param req
	     * @param resp
	     * @return
	     * @throws Exception
	     * @author: chaisen
	     * @time:2016年8月8日 下午2:36:21
	     */
	 	@RequestMapping(value = "validateMemberInfo", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
		@ResponseBody
		public Object validateMemberInfo(HttpServletRequest req, HttpServletResponse resp) throws Exception {
			// 核对验证码
			//MemberSessionDto member = getMember();
			 Long id = getMemberID(req);
			 Member member = memberService.selectByPrimaryKey(id);	
			// 姓名
			String trueName = ServletRequestUtils.getStringParameter(req, "trueName");
			// 身份证号码
			String identityNumber = ServletRequestUtils.getStringParameter(req, "identityNumber");
			// 登录密码
			String password = ServletRequestUtils.getStringParameter(req, "password");
			return this.memberService.validateMemberInfo(member.getMobile(), trueName, identityNumber, password);
		}  
	 	
	 	/**
	 	 * 
	 	 * @Description:身份信息修改手机号
	 	 * @param newMobile
	 	 * @param req
	 	 * @param resp
	 	 * @return
	 	 * @throws Exception
	 	 * @author: chaisen
	 	 * @time:2016年8月8日 下午2:43:36
	 	 */
	 	@RequestMapping(value = "modifyMobileByIdentity", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
		@ResponseBody
		public Object modifyMobileByIdentity(@ModelAttribute("nMobile") Long newMobile, HttpServletRequest req, HttpServletResponse resp) throws Exception {
			String newMobileCaptcha = ServletRequestUtils.getStringParameter(req, "captcha");
			String sessionMobileCaptcha = (String) req.getSession().getAttribute(Constant.SMS_CONTENT_CODE);
			ResultDTO<Object> resultDO = new ResultDTO<>();
			
			// 检验手机验证码的验证次数
			boolean checkSmsCodeCounts = checkSmsCodeCounts(newMobile);
			if (checkSmsCodeCounts) {
				resultDO.setResultCode(ResultCode.ERROR);
				return resultDO;
			}
			// 验证手机号
			boolean checkMobile = RegexUtils.checkMobile(newMobile.toString());
			if (!checkMobile){
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return  resultDO;
			}
			
			Long mobile=0L;
			// 核对验证码
			if (StringUtil.equals(newMobileCaptcha, sessionMobileCaptcha, true)) {
				MemberSessionDto member = getMember();
				if(member==null){
					 Long id = getMemberID(req);
					 Member biz = memberService.selectByPrimaryKey(id);
	     				if(biz!=null){
	     					mobile=biz.getMobile();
	     				}
				}else{
					mobile=member.getMobile();
				}
				// 姓名
				String trueName = ServletRequestUtils.getStringParameter(req, "trueName");
				// 身份证号码
				String identityNumber = ServletRequestUtils.getStringParameter(req, "identityNumber");
				resultDO = this.memberService.modifyMobileByIdentity(mobile, newMobile, trueName, identityNumber);
				if (resultDO.isSuccess()) {
					// 修改手机号后清空session
					cleanSession(req, resp);
					// 手机号、身份证号码、姓名
					req.getSession().setAttribute(Constant.MEMBER_MODIFY_MOBILE_IDENTITY_NAME, newMobile + Constants.ANGLE_BRACKETS + identityNumber+ Constants.ANGLE_BRACKETS + trueName);
				}
			} else {
				resultDO.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
			}
			return resultDO;
		}
	 	/**
	 	 * 
	 	 * @Description:身份信息修改手机号重置密码
	 	 * @param dto
	 	 * @param req
	 	 * @param resp
	 	 * @return
	 	 * @author: chaisen
	 	 * @time:2016年8月8日 下午2:46:55
	 	 */
	 	 @RequestMapping(value = "resetPasswordByIdentity",method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	     @ResponseBody
	     public ResultDTO<Object> resetPasswordByIdentity(@ModelAttribute("password") String password,@ModelAttribute("repassword") String repassword, HttpServletRequest req, HttpServletResponse resp) {
	 		ResultDTO<Object> result = new ResultDTO<Object>();
	     	if (!StringUtil.equals(password, repassword, true)) {
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
	     	String newMobileInfo = httpSession.getAttribute(Constant.MEMBER_MODIFY_MOBILE_IDENTITY_NAME).toString();
	     	if (StringUtil.isNotBlank(newMobileInfo)) {
	     		List<String> memberInfo = Splitter.on(Constants.ANGLE_BRACKETS).splitToList(newMobileInfo);
	     		newMobile = Long.valueOf(memberInfo.get(0));
	     		identityNumber = memberInfo.get(1);
	     		trueName = memberInfo.get(2);
	     	}
	     	String newPassword = password;
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
	 * 
	 * @Description:同步设置支付密码状态
	 * @param req
	 * @param resp
	 * @return
	 * @author: zhanghao
	 * @time:2016年8月8日 上午9:50:36
	 */
	@RequestMapping(value = "synPayPasswordFlag", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	@ResponseBody
	public Object synPayPasswordFlag(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return memberService.synPayPasswordFlag(memberId);
	}
	
	
	/**
	 * 委托授权扣款操作(1开通/2关闭)
	 * 
	 * @return
	 */
    @RequestMapping(value = "handWithholdAuthority", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
    @ResponseBody
	public ResultDTO<String> handWithholdAuthority(HttpServletRequest req, HttpServletResponse resp) {
    	int handleType = ServletRequestUtils.getIntParameter(req, "type", 0);
    	int mType = ServletRequestUtils.getIntParameter(req, "mType", 0);
    	Long orderId = ServletRequestUtils.getLongParameter(req, "orderId",0L);
    	
    	Long memberId = getMemberID(req);
    	Integer source = getRequestSource(req);
    	return this.memberService.handWithholdAuthority(memberId, handleType,source,mType,orderId);
	}
    
    /**
	 * 查询并同步是否开通委托扣款
	 * 
	 */
    @RequestMapping(value = "queryWithholdAuthority", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
    @ResponseBody
	public ResultDTO<Boolean> queryWithholdAuthority(HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMemberID(req);
    	return memberService.synWithholdAuthority(memberId);
	}
	
    /**
	 * 获取用户信息新浪展示页面
	 * 
	 * @return
	 */
    @RequestMapping(value = "showMemberInfosSina", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
    @ResponseBody
	public ResultDTO<Object> showMemberInfosSina(HttpServletRequest req, HttpServletResponse resp) {
    	Integer source = getRequestSource(req);
    	Long memberId = getMemberID(req);
    	return memberService.showMemberInfosSina(memberId,source);
	}
    /**
     * 
     * @Description:查询意见反馈
     * @param req
     * @param resp
     * @returns
     * @author: chaisen
     * @time:2016年9月23日 上午10:49:20
     */
    @RequestMapping(value = "queryFeedbackList", method = RequestMethod.POST, headers = {"Accept-Version=1.8.0"})
	@ResponseBody
	public ResultDTO queryMyTransactionList(HttpServletRequest req, HttpServletResponse resp) {
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo", 1);
		Long memberId = ServletRequestUtils.getLongParameter(req, "memberId", 0L);
		ResultDTO resultDto = new ResultDTO();
		FeedBackQuery query = new FeedBackQuery();
		query.setMemberId(memberId);
		query.setPageSize(20); 
		query.setCurrentPage(pageNo);
		Page<Feedback> pager = feedbackService.queryFeedbackList(query);
		resultDto.setResult(pager);
		return resultDto;
	}
    
    /**
     * 
     * @Description:自动投标设置初始化页面
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2016年8月16日 上午11:28:15
     */
    @RequestMapping(value = "queryAutoInvest", method = RequestMethod.POST, headers = {"Accept-Version=1.8.0"})
    @ResponseBody
	public Object queryAutoInvest(HttpServletRequest req, HttpServletResponse resp) {
    	Long memberId = getMemberID(req);
		ResultDTO<AutoInvestSet> result = new ResultDTO<AutoInvestSet>();
		 result= memberService.queryAutoInvest(memberId);
		 return result;
	}
    /**
     * 
     * @Description:自动投标设置
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2016年8月16日 上午11:37:13
     */
    @RequestMapping(value = "autoInvestSet", method = RequestMethod.POST, headers = {"Accept-Version=1.8.0"})
    @ResponseBody
    public Object autoInvestOpen(HttpServletRequest req, HttpServletResponse resp) {
    	ResultDTO<AutoInvestSet> result = new ResultDTO<AutoInvestSet>();
    	Long memberId = getMemberID(req);
        if(memberId==null){
        	result.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
        	return result;
        }
        int investFlag = ServletRequestUtils.getIntParameter(req, "investFlag", 0);
        return memberService.autoInvestSet(memberId,investFlag);
    }
    /**
     * 
     * @Description:保存投标设置信息
     * @param autoInvestDto
     * @param bindingResult
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2016年8月17日 上午9:35:12
     */
    @RequestMapping(value = "saveAutoInvestSet", method = RequestMethod.POST, headers = {"Accept-Version=1.8.0"})
    @ResponseBody
    public ResultDTO<AutoInvestSet> saveAutoInvestSet(@Valid @ModelAttribute("autoInvestDto") AutoInvestSetDto autoInvestDto, BindingResult bindingResult,
                                                       HttpServletRequest req, HttpServletResponse resp) throws ManagerException {
    	Long memberId = getMemberID(req);
        autoInvestDto.setMemberId(memberId);
        return memberService.saveAutoInvestSetByMemberId(autoInvestDto);
    }
    
}
